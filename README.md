# Phone Number Normalizer

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Build Status](https://github.com/telekom/phonenumber-normalizer/actions/workflows/maven.yml/badge.svg)

With the phonenumber-normalizer library, you can normalize phone numbers to the E164 format and national format, taking into account the specific complexities of the German number plan. The library can also differentiate between short numbers and those with NDCs and NACs, and can be useful for handling phone numbers in fixed-line contexts.

While Google's [PhoneLib](https://github.com/google/libphonenumber) is a general-purpose library for handling phone numbers worldwide, phonenumber-normalizer a wrapper around it, which is specifically tailored to handle the complexities of the German number plan. Especially, phonenumber-normalizer can differentiate between short numbers and those with NDCs and NACs. When PhoneLib is not able to make this distinction, our wrapping corrects the result.

![Phone Number Normalizer](https://user-images.githubusercontent.com/3244965/235174029-e58fab4c-37e9-49ba-834e-067b50082abb.png)

## Problem Statement(s)

If you have to deal with the complexity of telephone numbers your natural choice of handling it, should be the great [PhoneLib](https://github.com/google/libphonenumber) of [Google](https://opensource.google).

That's what we did for some projects.
And when we found bugs, we returned that as feedback in good open-source tradition.
But while some have been accepted and improved that library, other have been long discussed and finally rejected.

We are thankful for Googles work and are not here to blame them for any decision in those discussions, but for providing a technical solution for some inaccuracies which are not (yet) in their center of interest.

### German Number Plan and Its Implementation in Fixed-line

Germany is using a subscriber number (SN) of variable length.
The shortest one known is just 2 digit long, and it goes up to 8 digits.
Within the same area you can call those numbers directly.
If you want to call those numbers from a different area (or another country) you would need the national destination code (NDC) a.k.a. area code or ONKZ. 

From inside Germany you indicate the NDC with the National Access Code (NAC) "0".
From outside Germany you directly start with the NDC after the Country Calling Code (CC) for Germany (49).

Therefore, a local SN could be shorter than a minimal global short number with 3 digits (like Emergency 112) and longer than a maximum short number of 6 digits (like Central contact point for blocking electronic authorizations 116116).
A short number is a service number valid in all areas usable without NAC.
But additionally some short numbers can be called with NAC to reach the Service for a specific area, too - while other service numbers are must not be called with NAC.
From the number plan some short numbers might be called with the CC without NAC, but those routing is (if at all) only implemented when calling from outside Germany, not from within even if you should be able to use German CC starting numbers, too.

The problem we are facing is, identifying/separating:
a) a short number
b) a SN without NDC
c) a SN with NDC and NAC
d) a SN with NDC and CC (but without NAC in Germany but with in Italy)

We need that differentiation upfront normalization to E164 (which is d).
Currently, PhoneLib does not identify this situation and adds the CC infront a number b-style, which represents a complete different phone line.

### Example of failing normalzation

The number 20355555 itself is a valid number in many areas and can be dialed if you are within those areas.
If you are outside you need to add the area code with area exit code like 030 for Berlin so for Berlin the number would end up 03020355555. 
But the library does not identify this situation.
A not short number is stored internally with a leading zero and is normalized with the country code.

So 20355555 [will be normalized](https://libphonenumber.appspot.com/phonenumberparser?number=20355555&country=DE) as +4920355555 (E164) or 020355555 (national format) and that is not the same.
This means an NDC of 203 for Duisburg and the SN 55555 - also valid from historical reasons!

It even comes down to very small numbers like 203333 which would not be valid if interpreted as 0203333 because 333 would be too short for that area, but it would be [treated like this](https://libphonenumber.appspot.com/phonenumberparser?number=203333&country=DE).

We need to differentiate the real number entered with or without area code and not just assuming, non short numbers will include those because in mobile context it is so.

Additionally, we want to normalize a number with a default NDC, because we might know where the device - using that number - is located and which NDC is implicitly used.

### Roote Cause

We think the problem arises from the following situation:
- PhoneLib is storing the National Significant Number (NSN) as the number, which is the combination of NDC + SN.
- PhoneLib is storing the number as uint64 which does not allow to store (a) leading Zero(s)
- PhoneLib is storing leading Zeros in hasNumberOfLeadingZeros and getNumberOfLeadingZeros
- PhoneLib checks "IS_POSSIBLE_LOCAL_ONLY" only by comparing the length of a number, which might work in a fix length number plan like the North American Number plan, but not with variable number length and optional NDC as in Germany.

What needs to be done:
- Checking if the Country is supporting optional NDC
- Checking if the number is starting with NAC
  - Is the NAC Zero, needs to check hasNumberOfLeadingZeros etc.
  - otherwise need to check prefix of number itself
- if that all applies, such an input needs to be treated as a short number at not be changed for E164 or National format (see short number [normalization of 116116](https://libphonenumber.appspot.com/phonenumberparser?number=116116&country=DE))

The problem has been addressed but rejected like with [issue tracker 180311606](https://issuetracker.google.com/issues/180311606).
Reasons are either PhoneLib just make best efforts for formatting and not format checking or that they focus on mobile context while most problems happens in fixed-line context.

## State of Our Implementation

As a wrapper we did not change any code of PhoneLib itself, so an upgrade to the newest version should be possible by just updating the version in the dependency POM definition.

For normalizing a phone number you should just use the our PhoneNumberNormalizer either bei Dependency injection or directly with its implementation like:

```
String number = "20355555";
String normalizedNumber = PhoneNumberNormalizerImpl().normalizePhoneNumber(number, "DE");
// normalizedNumber -> "20355555"
```
So here the number without NAC does not get E164 format, because it is only valid locally.

If we know the context of the devices use (where and how it is used), you can create a DeviceContext:

```
// Device is used on a fixed-line access in Berlin
DeviceContextDto deviceContext = new DeviceContextDto(DeviceContextLineType.FIXEDLINE, "DE", "30")

String number = "20355555";
String normalizedNumber = PhoneNumberNormalizerImpl().normalizePhoneNumber(number, deviceContext);
// normalizedNumber -> "+493020355555"
```

Now we get a E164 formatted number, because now we know, how which NDC has to be added after the CC.

### Use Of Reflection

To check if a number plan of a country is using an optional NDC and NAC, we need to get the countries region metadata from PhoneLib.

With getMetadataForRegion there is a method on the PhoneNumberUtil class, but it is not public.

So we are forced to [use reflection and override its accessibility](https://github.com/telekom/phonenumber-normalizer/blob/main/src/main/java/de/telekom/phonenumbernormalizer/numberplans/PhoneLibWrapper.java#L286).

If you are using AOT (ahead of time) compiler, you need to take care of this.
(While it is used indirectly with the normal PhoneLib use of the wrapper, it might not be safe for all AOT compilers).

### Use of Own ShortNumber Recognition

When we started with the wrapper, PhoneLib did not recognize some phone assistant services as short numbers.
This as been fixed by [issue tracker 182490059](https://issuetracker.google.com/u/1/issues/182490059). 

But for the EU Social Service Number Range 116xxx, PhoneLib is only checking the assigned number 116116 in Germany.
This is in contradiction to normal validation, where the range is checked and assignment checks are explicitly not in scope.
But for the [issue 183669955](https://issuetracker.google.com/u/1/issues/183669955), they insist on assignment, since they need it for free call checks.
But for other EU states PhoneLib is using the full raneg (e.g. [CZ](https://github.com/google/libphonenumber/blob/4c532d93587d2f9d16dc7a536df55bf179158210/resources/ShortNumberMetadata.xml#L3342))

### One More Thing: Area Gecode Label

There is a table of names for the area code from the BNetzA, which is using non-common abbreviations, which wouldn't be understood by end users.
But PhoneLib is using those and refusing the long transcription, because they only trust the BNetzA document (see [issue tracker 183383466](https://issuetracker.google.com/issues/183383466)).
In addition, the BNetzA has published a [document on the ITU](https://www.itu.int/dms_pub/itu-t/oth/02/02/T02020000510006PDFE.pdf) and [its own website](https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vorwahlverzeichnis_ONB.zip.zip?__blob=publicationFile&v=298), which differ for some values, too.

We provide resolution for each country but also for cities in Germany and states in the US.
Our engine is using easy to generate [JSON data](./src/main/resources/arealabels/nationallabels/), so you could also provide your own resolution.

We only rely on the number analysis / formation logic of PhoneLib.
Their GeoCoder is not used for your labeling.
We only use the PhoneLib's GeoCoder in some testcases, to check if ours and their labeling against each other. 

```
String normalizedNumber = "+493020355555";
String label = PhoneNumberAreaLabelImpl().getLocationByE164Number(normalizedNumber);
// label -> "Berlin"
```

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). 

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.1 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Licensing

Copyright (c) 2023 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
