/*
 * Copyright © 2024 Deutsche Telekom AG (opensource@telekom.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.telekom.phonenumbernormalizer

import com.google.i18n.phonenumbers.PhoneNumberUtil
import de.telekom.phonenumbernormalizer.numberplans.PhoneNumberValidationResult
import spock.lang.Specification

class PhoneNumberValidatorImplTest extends Specification {

    PhoneNumberValidator target

    def "setup"() {
        target = new PhoneNumberValidatorImpl()
    }

    def "validate police short code 110 in combination as NDC"(String number, regionCode, expectedResult) {
        given:

        when: "validate number: $number for country: $regionCode"

        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "it should validate to: $expectedResult"
        result == expectedResult

        where:

        number                      | regionCode  | expectedResult
        // short code for Police (110) is not dial-able internationally nor does it has additional numbers
        "110"                       | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is short code, valid only locally
        "110556677"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // subscriber number starts with short code
        "0110"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0110 556677"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "0175 110"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 110555"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 1105555"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 11055555"             | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 110555555"            | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0203 110"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203 110555"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC within the region
        "+49110"                    | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49110 556677"             | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 110"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 110555"             | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1105555"            | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11055555"           | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 110555555"          | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 110"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203 110555"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC from outside the region
        "+49110"                    | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49110 556677"             | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 110"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 110555"             | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1105555"            | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11055555"           | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 110555555"          | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 110"                | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203 110555"             | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // end of 110
    }

    def "validate police short code 112 in combination as NDC"(String number, regionCode, expectedResult) {
        given:

        when: "validate number: $number for country: $regionCode"

        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "it should validate to: $expectedResult"
        result == expectedResult

        where:

        number                      | regionCode  | expectedResult
        // short code for Police (112) is not dial-able internationally nor does it has additional numbers
        "112"                       | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is short code, valid only locally
        "112556677"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // subscriber number starts with short code
        "0112"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0112 556677"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "0175 112"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 112555"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 1125555"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 11255555"             | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 112555555"            | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0203 112"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203 112555"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC within the region
        "+49112"                    | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49112 556677"             | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 112"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 112555"             | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1125555"            | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11255555"           | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 112555555"          | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 112"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203 112555"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC from outside the region
        "+49112"                    | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49112 556677"             | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 112"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 112555"             | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1125555"            | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11255555"           | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 112555555"          | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 112"                | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203 112555"             | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // end of 112
    }

    def "validate German Government short code 115 in combination as NDC"(String number, regionCode, expectedResult) {
        given:

        when: "validate number: $number for country: $regionCode"

        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "it should validate to: $expectedResult"
        result == expectedResult

        where:
        // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/115/115_Nummernplan_konsolidiert.pdf?__blob=publicationFile&v=1
        number                      | regionCode  | expectedResult
        // short code for German Government (115) is different to 110 & 112, dealable with NDC to reach a specific local one, or IDP+CC from outside of Germany, but not within!
        "115"                       | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is short code, valid only locally
        "115556677"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // subscriber number starts with short code
        "0115"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0115 556677"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "0175 115"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 115555"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 1155555"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 11555555"             | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 115555555"            | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0203 115"                  | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, optional fixed line NDC follows, SN equals short code and the local service is targeted regardless of caller location.
        "0203 115555"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC within the region
        "+49115"                    | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => inside germany explicitly not allowed (see BnetzA)
        "+49115 556677"             | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 115"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 115555"             | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1155555"            | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11555555"           | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 115555555"          | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 115"                | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code and the local service is targeted regardless of caller location.
        "+49203 115555"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC from outside the region
        "+49115"                    | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE_INTERNATIONAL_ONLY           // number starts with IDP+CC, normally NDC would follow, but that equals short code => outside germany explicitly allowed (see BnetzA)
        "+49115 556677"             | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 115"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 115555"             | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1155555"            | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11555555"           | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 115555555"          | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 115"                | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code and the local service is targeted regardless of caller location.
        "+49203 115555"             | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // end of 1105
    }

    def "validate EU social short codes 116xxx in combination as NDC"(String number, regionCode, expectedResult) {
        given:

        when: "validate number: $number for country: $regionCode"

        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "it should validate to: $expectedResult"
        result == expectedResult

        where:

        number                      | regionCode  | expectedResult
        // 116 is mentioned in number plan as 1160 and 1161 but in special ruling a full 6 digit number block: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/116xyz/StrukturAusgestNrBereich_Id11155pdf.pdf?__blob=publicationFile&v=4
        // 116xyz is nationally and internationally reachable - special check 116000 as initial number, 116116 as assigned number and 116999 as max legal number
        "116"                       | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number is to short, needs to be exactly 6 digits
        "116000"                    | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is valid short code (not assigned yet but in BnetzA defined range)
        "116116"                    | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is valid short code (already assigned)
        "116999"                    | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is valid short code (not assigned yet but in BnetzA defined range)
        "1165566"                   | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // subscriber number starts with short code
        "11655"                     | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number is to short, needs to be exactly 6 digits
        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/116xyz/116116.html
        // NAC + 116xxx
        // see no. 7: national 0116116 is not a valid number, but may be replaced by 116116 by the operator - caller could reach target. ( T-Mobile is doing so currently 03.11.2023 - no guarantee for the future nor for any other operator. Best practice, assuming call will not reach target=.
        "0116"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, normally NDC would follow and since short code length is not correct, not assuming Short Code is intended => which means NDC is wrong
        "0116000"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0116116"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0116999"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0116 5566"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, normally NDC would follow and since short code length is not correct, not assuming Short Code is intended => which means NDC is wrong
        "0116 55"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, normally NDC would follow and since short code length is not correct, not assuming Short Code is intended => which means NDC is wrong
        "0175 116"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 116555"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 1165555"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 11655555"             | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 116555555"            | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7

        "0203116"                   | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        "0203116000"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203116116"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203116999"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203116 5566"              | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        "0203116 55"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong

        // using IDP+CC within the region
        "+49116"                    | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number is to short, needs to be exactly 6 digits and is Valid with IDP & CC
        "+49116000"                 | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number is valid short code (not assigned yet but in BnetzA defined range) and is Valid with IDP & CC
        "+49116116"                 | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number is valid short code & assigned and is Valid with IDP & CC
        "+49116999"                 | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number is valid short code (not assigned yet but in BnetzA defined range) and is Valid with IDP & CC
        "+49116 5566"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number is to short, needs to be exactly 6 digits and is Valid with IDP & CC (TOO_LONG would be too general, the SN can't start with the short code)
        "+49116 55"                 | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number is to short, needs to be exactly 6 digits and is Valid with IDP & CC

        "+49175 116"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 116555"             | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1165555"            | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11655555"           | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 116555555"          | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7

        "+49203116"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        "+49203116000"              | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203116116"              | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203116999"              | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203116 5566"            | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        "+49203116 55"              | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong

        // using IDP+CC from outside the region
        "+49116"                    | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number is to short, needs to be exactly 6 digits and is Valid with IDP & CC
        "+49116000"                 | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number is valid short code (not assigned yet but in BnetzA defined range) and is Valid with IDP & CC
        "+49116116"                 | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number is valid short code & assigned and is Valid with IDP & CC
        "+49116999"                 | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number is valid short code (not assigned yet but in BnetzA defined range) and is Valid with IDP & CC
        "+49116 5566"               | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number is to short, needs to be exactly 6 digits and is Valid with IDP & CC (TOO_LONG would be too general, the SN can't start with the short code)
        "+49116 55"                 | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number is to short, needs to be exactly 6 digits and is Valid with IDP & CC

        "+49175 116"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 116555"             | "FR"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1165555"            | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11655555"           | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 116555555"          | "FR"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7

        "+49203116"                 | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        "+49203116000"              | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203116116"              | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203116999"              | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203116 5566"            | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        "+49203116 55"              | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // end of 116xxx
    }

    def "validate German Call Assistant short codes in combination as NDC"(String number, regionCode, expectedResult) {
        given:

        when: "validate number: $number for country: $regionCode"

        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "it should validate to: $expectedResult"
        result == expectedResult

        where:

        number                      | regionCode  | expectedResult
        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/118xy/118xyNummernplan.pdf?__blob=publicationFile&v=1
        // it is mentioned, that those numbers are nationally reachable - which excludes them from locally, so no local number should work this way because without NDC it could not be seperated from the national number
        // implicitly it could also mean that those numbers are not routed from outside germany

        // 118 is starting part and in general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "118"                       | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "1180"                      | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "11800"                     | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "118000"                    | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "118099"                    | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "1180000"                   | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "1181"                      | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "11810"                     | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        // Call Assistant of Deutsche Telekom - will be retired on 01.12.2024 see https://www.telekom.com/de/blog/konzern/artikel/telekom-stellt-auskunftsdienste-ein-1065536
        "11833"                     | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "118100"                    | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "1189"                      | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "11899"                     | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "118999"                    | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER

        // Tested on 26.12.2023 - 11833 works on TMD, but neither 011833 nor +4911833 is working on T-Mobile Germany
        // NAC + 118(y)xx belongs to the number reserve of NAC + 11

        "0118"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "01180"                     | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "011800"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0118000"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0118099"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "01180000"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "01181"                     | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "011810"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "011833"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "0118100"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "01189"                     | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "011899"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "0118999"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE

        // NAC + NDC (e.g. for Duisburg) + 118(y)xx
        "0203118"                   | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "02031180"                  | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "020311800"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "0203118000"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "0203118099"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "02031180000"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "02031181"                  | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "020311810"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "020311833"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0203118100"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "02031189"                  | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "020311899"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0203118999"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER

        // NAC + mobile NDC  + 118(y)xx
        "0175118"                   | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "01751180"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "017511800"                 | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "0175118000"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "0175118099"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "01751180000"               | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "017511800000"              | "DE"       | PhoneNumberValidationResult.TOO_LONG  // special for mobile
        "01751181"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "017511810"                 | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "017511833"                 | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "0175118100"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "01751181000"               | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY // special for mobile
        "017511810000"              | "DE"       | PhoneNumberValidationResult.TOO_LONG // special for mobile
        "01751189"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "017511899"                 | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "0175118999"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "01751189999"               | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY // special for mobile
        "017511899999"              | "DE"       | PhoneNumberValidationResult.TOO_LONG // special for mobile

        // CC + 118(y)xx
        "+49118"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491180"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4911800"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49118000"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49118099"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491180000"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491181"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4911810"                  | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+4911833"                  | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118100"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491189"                   | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4911899"                  | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118999"                 | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE

        // CC + NDC (e.g. for Duisburg) + 118(y)xx
        "+49203118"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031180"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+4920311800"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+49203118000"              | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031180000"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031181"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+4920311810"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4920311833"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203118100"              | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031189"                | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+4920311899"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203118999"              | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER

        // CC + mobile NDC  + 118(y)xx
        "+49175118"                 | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751180"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511800"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118099"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751180000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+4917511800000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG  // special for mobile
        "+491751181"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511810"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511833"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118100"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751181000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE // special for mobile
        "+4917511810000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG // special for mobile
        "+491751189"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511899"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118999"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751189999"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE // special for mobile
        "+4917511899999"            | "DE"       | PhoneNumberValidationResult.TOO_LONG // special for mobile

        // CC + 118(y)xx from outside Germany
        "+49118"                    | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491180"                   | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4911800"                  | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49118000"                 | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49118099"                 | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491180000"                | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491181"                   | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4911810"                  | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+4911833"                  | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118100"                 | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+491189"                   | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4911899"                  | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118999"                 | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE

        // CC + NDC (e.g. for Duisburg) + 118(y)xx from outside Germany
        "+49203118"                 | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031180"                | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+4920311800"               | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+49203118000"              | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031180000"             | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031181"                | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+4920311810"               | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+4920311833"               | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203118100"              | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+492031189"                | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER
        "+4920311899"               | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203118999"              | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER

        // CC + mobile NDC  + 118(y)xx from outside Germany
        "+49175118"                 | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751180"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511800"               | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118099"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751180000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+4917511800000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG  // special for mobile
        "+491751181"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511810"               | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511833"               | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118100"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751181000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE // special for mobile
        "+4917511810000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG // special for mobile
        "+491751189"                | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+4917511899"               | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+49175118999"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+491751189999"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE // special for mobile
        "+4917511899999"            | "FR"       | PhoneNumberValidationResult.TOO_LONG // special for mobile

        // end of 118
    }

    /*
     TODO NDC Ranges see equivalent Testcases in IsValidNumberTest
     */

    // TODO: 19222

    // TODO: 137

    // TODO: 15

    // TODO: 15 + voicemail infix

    // TODO: 16

    // TODO: 16 + voicemail infix

    // TODO: 17

    // TODO: 17 + voicemail infix

    // TODO: 180

    // TODO: 180 reserve

    // TODO: 181 VPN

    // TODO: 18(2-9) VPN

    // TODO: 18(2-9) VON nationl only

    // TODO: 18 59995 xxxx

    // TODO: 19(1-4)

    // TODO: 1981

    // TODO: 1981xx

    // TODO: 1981xx invalid

    // TODO: 1982

    // TODO: 1986

    // TODO: 1987

    // TODO: 1988

    // TODO: 1989

    def "validate German traffic routing 0199 for internal traffic routing"(String reserve,regionCode) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "0",
                                  reserve + "00",
                                  reserve + "000",
                                  reserve + "0000",
                                  reserve + "00000",
                                  reserve + "000000",
                                  reserve + "0000000",
                                  reserve + "00000000",
                                  reserve + "000000000",
                                  reserve + "0000000000",
                                  reserve + "00000000000",
                                  reserve + "000000000000",
                                  reserve + "9",
                                  reserve + "99",
                                  reserve + "999",
                                  reserve + "9999",
                                  reserve + "99999",
                                  reserve + "999999",
                                  reserve + "9999999",
                                  reserve + "99999999",
                                  reserve + "999999999",
                                  reserve + "9999999999",
                                  reserve + "99999999999",
                                  reserve + "999999999999"]

        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results +=  target.isPhoneNumberPossibleWithReason(number, regionCode)
        }


        then:

        PhoneNumberValidationResult[] expectedresults = []
        for (int i = 0; i < results.length; i++) {
            expectedresults += PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_OPERATOR_ONLY
        }

        expectedresults == results

        where:
        reserve    | regionCode
        //  0199 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  0199 is not further ruled, so assuming ITU rule of max length 15 with no lower limit, but operator only use
        "0199"     | "DE"
        "+49199"   | "DE"
        "+49199"   | "FR"
    }

    def "validate German personal 700 range"(String reserve, regionCode, possibleValue) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "2",
                                  reserve + "22",
                                  reserve + "223",
                                  reserve + "2233",
                                  reserve + "22334",
                                  reserve + "223344",
                                  reserve + "2233445",
                                  reserve + "22334455",
                                  reserve + "223344556",
                                  reserve + "2233445566"]

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         possibleValue,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_LONG]

        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        expectedResults == results

        where:
        reserve          | regionCode | possibleValue
        //  0700 is personal number range: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0700/0700_node.html
        //  it has 8-digit long numbers TODO: unclear if those numbers may only be called within Germany (no country code example)
        //  but general numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        //  indicates it is callable from outside Germany

        "0700"           | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "+49700"         | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49700"         | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
    }

    // TODO: 800

    // TODO: 900

    // TODO: 31x

    // TODO: 32

    // TODO: 32 - low level reserve

    // TODO: 32 - mid level reserve

    // TODO: 32 - high level reserve

    // TODO: DRAMA Numbers

    // TODO: DRAMA Numbers 2 digits range

    // TODO: DRAMA Numbers 3 digits range

    // TODO: NDC 010 - 02999

    // TODO: NDC 030 - 039999

    // TODO: NDC 040 - 069

    // TODO: NDC 0700 - 0999

    def "validate number starting with NAC digit after optional NDC"(String number, countryCode, expectedResult) {
        given:


        when:
        "get number isPossibleNumberWithReason: $number"

        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, countryCode)

        then:
        "it should validate to: $expectedResult"
        result == expectedResult

        where:

        number                    | countryCode | expectedResult
        "0203056677"              | "DE"        | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER  // after NAC+optional NDC number must not start with digit equal to NAC
        "+49203056677"            | "DE"        | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER  // after CC+optional NDC number must not start with digit equal to NAC
        "+49203056677"            | "FR"        | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER  // after CC+optional NDC number must not start with digit equal to NAC
        "01750556677"             | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY            // after NAC+mandatory NDC number may start with digit equal to NAC
        "+491750556677"           | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE                          // after CC+mandatory NDC number may start with digit equal to NAC
        "+491750556677"           | "FR"        | PhoneNumberValidationResult.IS_POSSIBLE                          // after CCC+mandatory NDC number may start with digit equal to NAC
    }

    // see "normalizeNumber by RegionCode" in PhoneNumberNormalizerImplTest
    def "validate Number by RegionCode"(String number, String countryCode, expectedResult) {
        given:

        when:
        "validate number: $number for country: $countryCode"
        PhoneNumberValidationResult result = target.isPhoneNumberPossibleWithReason(number, countryCode)

        then:
        "it should validate to: $expectedResult"
        result == expectedResult

        where:
        number                    | countryCode | expectedResult
        null                      | "DE"        | PhoneNumberValidationResult.INVALID_LENGTH
        // NDC+ national Romania numbers might be longer than 9 digits
        "0040(0176) 3 0 6 9 6541" | "DE"        | PhoneNumberValidationResult.TOO_LONG
        "0040 176 3 0 6 9 6542"   | "DE"        | PhoneNumberValidationResult.TOO_LONG
        "004017630696543"         | "DE"        | PhoneNumberValidationResult.TOO_LONG
        "0040-0176 3 0 6 9 6544"  | "DE"        | PhoneNumberValidationResult.TOO_LONG
        "+49176 3 0 6 9 6544"     | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE
        "0176 3 0 6 9 6544"       | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "+49203556677"            | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE
        "0203556677"              | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "203556677"               | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "556677"                  | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "5566778"                 | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "55667789"                | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "556677889"               | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "5566778899"              | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "55667788990"             | "DE"        | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "000"                     | "AU"        | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "+39012345678"            | "IT"        | PhoneNumberValidationResult.IS_POSSIBLE
        "012345678"               | "IT"        | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "+39312345678"            | "IT"        | PhoneNumberValidationResult.IS_POSSIBLE
        "312345678"               | "IT"        | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
    }


}
