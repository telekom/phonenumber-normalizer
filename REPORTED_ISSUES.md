# Reported Issues

## 2021-02-16 - [Local fix line number is not identified as IS_POSSIBLE_LOCAL_ONLY but as IS_POSSIBL6E](https://issuetracker.google.com/issues/180311606)

This issue was created to address the main problem, that a German Fixlinenumber is allways normalized, even if no NAC is introducing a NDC. So a just added CC creates a totally different number. This issue has not been resolved. Within the discussion, further examples where raised - which leaded to the next two issues, but meaybe that leads to confusion which parts of this issues have been taken care for.

### 2021-03-12 - [Validation issue with +4911833](https://issuetracker.google.com/issues/182490059#comment3)

This issue addresses special short codes for phone number directory assistant services. This issue has been fixed. 

### 2021-03-25 - [Germany (DE, +49): 116xxx Short Number valid vs. assigned](https://issuetracker.google.com/issues/183669955)

This issue addresses the EU wide special social number short code definition. While the regulation clearly defines a range, PhoneLib is not validating against that range, but against a list of currently assigned / operated numbers. At least for German number space - as mentioned in the initial issue discussion (see first one above), in other EU number spaces the lib is partly or even completely checking the whole range. 

On the one hand the last option is referred in the [faq](https://github.com/google/libphonenumber/blob/master/FAQ.md#what_is_valid) that valid does not mean "numbers are currently assigned to a specific user and reachable". On the other hand it stated that "A valid number range is one from which numbers can be freely assigned by carriers to users" - which is not the case for EU wide numbers, which need a special clearance.

While it seems the last point is the argument of rejecting the issue (while it was phrased around TOLL FREE calculation). So its intended, and other EU spaces are stated as needing correction, but are not corrected yet (oct 2023 - more than two years later).

## Internal Implementation

After longer conversation and finally closing those issues as stated above - we decided to implement a minimal fix for normalizing German Phonenumbers for us internally, to support the phoning capability of Deutsche Telekom Smart Speaker. We also established [test cases to check if PhoneLib behavior changes to correctly normalize the currently failing cases](https://github.com/telekom/phonenumber-normalizer/blob/main/src/test/groovy/de/telekom/phonenumbernormalizer/extern/libphonenumber/PhoneNumberUtilTest.groovy), so that our implementation would become unnecessary.

During its development, we also found, that the geolocation method uses BenetzA given labels, which include abbreviations. For a smart speaker we needed a "speakable" label - so we created the following issue and added an [own list to our interims solution](https://github.com/telekom/phonenumber-normalizer/blob/main/src/main/resources/arealabels/nationallabels/de.json).

### 2022-03-21 - [Germany (DE, +49): Update geocode details](https://issuetracker.google.com/issues/183383466)

We delivered a [change request of 34 corrected labels](https://github.com/google/libphonenumber/pull/2599/files), which was rejected because there is no official reference. 

We also provide a [test case, if the labels are corrected](https://github.com/telekom/phonenumber-normalizer/blob/main/src/test/groovy/de/telekom/phonenumbernormalizer/extern/libphonenumber/PhoneNumberOfflineGeocoderTest.groovy).

## Open Sourcing

While the Smart Speaker has been retired, we identified other projects could benefit from the wrapper and open sourced it on May 3rd 2023. While we are now maintaining the basic implementation the previous discussion is catching up to question if we extend the knowledge of the German Number plan to other functions as validating. When we have further insights we will report those as issues, reengage with Google and list the new issues in this file. 