# Reported Issues

## 2021-02-16 - [Local fix line number is not identified as IS_POSSIBLE_LOCAL_ONLY but as IS_POSSIBL6E](https://issuetracker.google.com/issues/180311606)

This issue was created to address the problem that German fixed-line numbers are always normalized, even if no NAC introduces an NDC.
As a result, a newly added CC creates a completely different number.
Unfortunately, this issue has not been resolved yet.
During the discussion, further examples were raised, which led to the creation of two more issues.
However, it’s possible that this has caused confusion about which parts of these issues have been addressed.

### 2021-03-12 - [Validation issue with +4911833](https://issuetracker.google.com/issues/182490059#comment3)

This issue addresses special short codes used for phone number directory assistant services.
This issue has been resolved. 

### 2021-03-25 - [Germany (DE, +49): 116xxx Short Number valid vs. assigned](https://issuetracker.google.com/issues/183669955)

This issue pertains to the EU-wide special social number short code definition. Although the regulation clearly defines a range, LibPhoneNumber is not validating against that range, but against a list of currently assigned/operated numbers. At least for the German number space, as mentioned in the initial issue discussion (see first one above), the library is only partly or even completely checking the whole range in other EU number spaces.

On the one hand, the [FAQ](https://github.com/google/libphonenumber/blob/master/FAQ.md#what_is_valid)(https://github.com/google/libphonenumber/blob/master/FAQ.md) states that “valid” does not mean “numbers are currently assigned to a specific user and reachable.”
On the other hand, it states that “a valid number range is one from which numbers can be freely assigned by carriers to users,” which is not the case for EU-wide numbers that require special clearance.

While it seems that the last point is the argument for rejecting the issue (which was phrased around TOLL FREE calculation), it is intended, and other EU spaces are stated as needing correction but have not been corrected yet (as of October 2023 - more than two years later).

## Internal Implementation

After a long discussion and closing the issues as stated above, we decided to implement a minimal fix for normalizing German Phonenumbers for our internal use, to support the phoning capability of Deutsche Telekom Smart Speaker.
We also set up [test cases to verify if LibPhoneNumber behavior changes to correctly normalize the currently failing cases](https://github.com/telekom/phonenumber-normalizer/blob/main/src/test/groovy/de/telekom/phonenumbernormalizer/extern/libphonenumber/PhoneNumberUtilTest.groovy), so that our implementation would become unnecessary.

We also discovered, during its development, that the geolocation method uses BenetzA given labels, which include abbreviations.
[For a smart speaker, we needed a “speakable” label - so we created the following issue and added our own list to our interim solution](https://github.com/telekom/phonenumber-normalizer/blob/main/src/main/resources/arealabels/nationallabels/de.json).

### 2022-03-21 - [Germany (DE, +49): Update geocode details](https://issuetracker.google.com/issues/183383466)

We submitted a [change request of 34 corrected labels]((https://github.com/google/libphonenumber/pull/2599/files)) to Google’s libphonenumber library, which was rejected due to the lack of an official reference.

However, we have provided a [test case to verify if the labels are corrected](https://github.com/telekom/phonenumber-normalizer/blob/main/src/test/groovy/de/telekom/phonenumbernormalizer/extern/libphonenumber/PhoneNumberOfflineGeocoderTest.groovy) in our phonenumber-normalizer repository.

## Open Sourcing

Although the Smart Speaker has been retired, we identified other projects that could benefit from the wrapper and open-sourced it on May 3rd, 2023.
While we are currently maintaining the basic implementation, the previous discussion is catching up to question if we should extend the knowledge of the German Number plan to other functions as validating.
When we have further insights, we will report them as issues, re-engage with Google, and list the new issues in this file.