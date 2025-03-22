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

    def "validate ambulance transport 19222 short codes in combination as NDC"(String number, regionCode, expectedResult) {
        given:

        when: "get number isValid: $number"

        def result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "is number expected: $expectedResult"
        result == expectedResult

        where:

        number                      | regionCode | expectedResult
        // prior to mobile, there where 19xxx short codes in fixed line - only 19222 for no emergency ambulance call is still valid
        // its a national reserved number, which in contrast to 112 might also be called with NDC to reach a specific ambulance center - not all NDC have a connected 19222.
        // for more information see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vfg_25_2006_konsFassung100823.pdf?__blob=publicationFile&v=3 chapter 7
        "19222"                     | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY   // not valid on mobil but on fixedline
        // using 19222 als NDC after NAC is checked by "online services 019xx"
        "0203 19222"                | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0203 19222555"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER            // must not be longer
        // using 19222 from DE als NDC after CC is checked by "online services 019xx"
        "+49203 19222"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49203 19222555"           | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER              // must not be longer
        // using 19222 from FR als NDC after CC is checked by "online services 019xx"
        "+49203 19222"              | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49203 19222555"           | "FR"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER              // must not be longer
        // end of 19222
    }

    def "validate German mass traffic NDC"(String number, regionCode, expectedResult) {
        given:

        when: "get number isValid: $number"

        def result = target.isPhoneNumberPossibleWithReason(number, regionCode)

        then: "is number expected: $expectedResult"
        result == expectedResult

        where:

        number                      | regionCode | expectedResult
        // 137 is masstraffic 10 digits
        "0137 000 0000"             | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER              // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "0137 000 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 000 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT

        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0137/0137_Nummernplan.pdf?__blob=publicationFile&v=4
        // within each zone, there are only a few ranges assigned: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/belegteRNB/0137MABEZBelegteRNB_Basepage.html?nn=326370
        // Zone 1 is valid, but only with exactly 10 digits
        "0137 100 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 100 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 100 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 2 is valid, but only with exactly 10 digits
        "0137 200 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 200 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 200 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 3 is valid, but only with exactly 10 digits
        "0137 300 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 300 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 300 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 4 is valid, but only with exactly 10 digits
        "0137 400 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 400 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 400 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 5 is valid, but only with exactly 10 digits
        "0137 500 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 500 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 500 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 6 is valid, but only with exactly 10 digits
        "0137 600 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 600 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 600 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 7 is valid, but only with exactly 10 digits
        "0137 700 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 700 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 700 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 8 is valid, but only with exactly 10 digits
        "0137 800 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 800 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 800 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 9 is valid, but only with exactly 10 digits
        "0137 900 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY
        "0137 900 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "0137 900 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT

        // with CC from DE

        // 137 is masstraffic 10 digits
        "+49137 000 0000"             | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER              // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "+49137 000 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 000 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT

        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0137/0137_Nummernplan.pdf?__blob=publicationFile&v=4
        // within each zone, there are only a few ranges assigned: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/belegteRNB/0137MABEZBelegteRNB_Basepage.html?nn=326370
        // Zone 1 is valid, but only with exactly 10 digits
        "+49137 100 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 100 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 100 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 2 is valid, but only with exactly 10 digits
        "+49137 200 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 200 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 200 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 3 is valid, but only with exactly 10 digits
        "+49137 300 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 300 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 300 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 4 is valid, but only with exactly 10 digits
        "+49137 400 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 400 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 400 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 5 is valid, but only with exactly 10 digits
        "+49137 500 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 500 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 500 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 6 is valid, but only with exactly 10 digits
        "+49137 600 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 600 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 600 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 7 is valid, but only with exactly 10 digits
        "+49137 700 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 700 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 700 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 8 is valid, but only with exactly 10 digits
        "+49137 800 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 800 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 800 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 9 is valid, but only with exactly 10 digits
        "+49137 900 0000"             | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 900 00000"            | "DE"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 900 000"              | "DE"       | PhoneNumberValidationResult.TOO_SHORT


        // with CC from outside DE

        // 137 is masstraffic 10 digits
        "+49137 000 0000"             | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER              // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "+49137 000 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 000 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT

        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0137/0137_Nummernplan.pdf?__blob=publicationFile&v=4
        // within each zone, there are only a few ranges assigned: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/belegteRNB/0137MABEZBelegteRNB_Basepage.html?nn=326370
        // Zone 1 is valid, but only with exactly 10 digits
        "+49137 100 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 100 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 100 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 2 is valid, but only with exactly 10 digits
        "+49137 200 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 200 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 200 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 3 is valid, but only with exactly 10 digits
        "+49137 300 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 300 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 300 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 4 is valid, but only with exactly 10 digits
        "+49137 400 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 400 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 400 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 5 is valid, but only with exactly 10 digits
        "+49137 500 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 500 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 500 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 6 is valid, but only with exactly 10 digits
        "+49137 600 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 600 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 600 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 7 is valid, but only with exactly 10 digits
        "+49137 700 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 700 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 700 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 8 is valid, but only with exactly 10 digits
        "+49137 800 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 800 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 800 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        // Zone 9 is valid, but only with exactly 10 digits
        "+49137 900 0000"             | "FR"       | PhoneNumberValidationResult.IS_POSSIBLE
        "+49137 900 00000"            | "FR"       | PhoneNumberValidationResult.TOO_LONG
        "+49137 900 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT
        "+49137 900 000"              | "FR"       | PhoneNumberValidationResult.TOO_SHORT

    }

    def "validate German Mobile 15 range"(String numberUntilInfix, regionCode) {
        given:
        String[] numbersToTest = []

        if (numberUntilInfix.length() == 5) {
            numbersToTest = [numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "00000000",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999",
                             numberUntilInfix + "99999999"]
        }
        if (numberUntilInfix.length() == 6) {
            numbersToTest = [numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999"]
        }
        if (numberUntilInfix.length() == 7) {
            numbersToTest = [numberUntilInfix + "0000",
                             numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "9999",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999"]
        }
        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG]

        when: "get numbers isValid: $numbersToTest"
        PhoneNumberValidationResult[] results = []

        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            results[i] == expectedResults[i]
        }

        where:

        numberUntilInfix | regionCode
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 015xxyyyyyyy xx = block code, yyyyyyy fixed length number in 2 digit block, so together 9 digit is the overall length
        // 015zzzaaaaaa zzz = newer block zzz, aaaaaa fixes length number in 3 digit block, so together 9 digit is the overall length

        //
        // 0150
        //
        // 015000 is reserved for voicemail - see tests below
        "015001"         | "DE"
        "015002"         | "DE"
        "015003"         | "DE"
        "015004"         | "DE"
        "015005"         | "DE"
        "015006"         | "DE"
        "015007"         | "DE"
        "015008"         | "DE"
        "015009"         | "DE"
        "01501"          | "DE"
        "01502"          | "DE"
        "01503"          | "DE"
        "01504"          | "DE"
        "01505"          | "DE"
        "01506"          | "DE"
        "01507"          | "DE"
        "01508"          | "DE"
        "01509"          | "DE"
        //
        // 0151
        //
        "01510"          | "DE"
        "015110"         | "DE"
        "015111"         | "DE"
        "015112"         | "DE"
        // 015113 is reserved for voicemail - see tests below
        "015114"         | "DE"
        "015115"         | "DE"
        "015116"         | "DE"
        "015117"         | "DE"
        "015118"         | "DE"
        "015119"         | "DE"
        "01512"          | "DE"
        "01513"          | "DE"
        "01514"          | "DE"
        "01515"          | "DE"
        "01516"          | "DE"
        "01517"          | "DE"
        "01518"          | "DE"
        "01519"          | "DE"

        //
        // 0152
        //
        "015200"         | "DE"
        "015201"         | "DE"
        "015202"         | "DE"
        "015203"         | "DE"
        "015204"         | "DE"
        // 0152050 is reserved for voicemail - see tests below
        "0152051"        | "DE"
        "0152052"        | "DE"
        "0152053"        | "DE"
        "0152054"        | "DE"
        // 0152055 is reserved for voicemail - see tests below
        "0152056"        | "DE"
        "0152057"        | "DE"
        "0152058"        | "DE"
        "0152059"        | "DE"
        "015206"         | "DE"
        "015207"         | "DE"
        "015208"         | "DE"
        "015209"         | "DE"

        "015210"         | "DE"
        "015211"         | "DE"
        "015212"         | "DE"
        "015213"         | "DE"
        "015214"         | "DE"
        // 0152150 is reserved for voicemail - see tests below
        "0152151"        | "DE"
        "0152152"        | "DE"
        "0152153"        | "DE"
        "0152154"        | "DE"
        // 0152155 is reserved for voicemail - see tests below
        "0152156"        | "DE"
        "0152157"        | "DE"
        "0152158"        | "DE"
        "0152159"        | "DE"
        "015216"         | "DE"
        "015217"         | "DE"
        "015218"         | "DE"
        "015219"         | "DE"

        "015220"         | "DE"
        "015221"         | "DE"
        "015222"         | "DE"
        "015223"         | "DE"
        "015224"         | "DE"
        // 0152250 is reserved for voicemail - see tests below
        "0152251"        | "DE"
        "0152252"        | "DE"
        "0152253"        | "DE"
        "0152254"        | "DE"
        // 0152255 is reserved for voicemail - see tests below
        "0152256"        | "DE"
        "0152257"        | "DE"
        "0152258"        | "DE"
        "0152259"        | "DE"
        "015226"         | "DE"
        "015227"         | "DE"
        "015228"         | "DE"
        "015229"         | "DE"

        "015230"         | "DE"
        "015231"         | "DE"
        "015232"         | "DE"
        "015233"         | "DE"
        "015234"         | "DE"
        // 0152350 is reserved for voicemail - see tests below
        "0152351"        | "DE"
        "0152352"        | "DE"
        "0152353"        | "DE"
        "0152354"        | "DE"
        // 0152355 is reserved for voicemail - see tests below
        "0152356"        | "DE"
        "0152357"        | "DE"
        "0152358"        | "DE"
        "0152359"        | "DE"
        "015236"         | "DE"
        "015237"         | "DE"
        "015238"         | "DE"
        "015239"         | "DE"

        "015240"         | "DE"
        "015241"         | "DE"
        "015242"         | "DE"
        "015243"         | "DE"
        "015244"         | "DE"
        // 0152450 is reserved for voicemail - see tests below
        "0152451"        | "DE"
        "0152452"        | "DE"
        "0152453"        | "DE"
        "0152454"        | "DE"
        // 0152455 is reserved for voicemail - see tests below
        "0152456"        | "DE"
        "0152457"        | "DE"
        "0152458"        | "DE"
        "0152459"        | "DE"
        "015246"         | "DE"
        "015247"         | "DE"
        "015248"         | "DE"
        "015249"         | "DE"

        "015250"         | "DE"
        "015251"         | "DE"
        "015252"         | "DE"
        "015253"         | "DE"
        "015254"         | "DE"
        // 0152550 is reserved for voicemail - see tests below
        "0152551"        | "DE"
        "0152552"        | "DE"
        "0152553"        | "DE"
        "0152554"        | "DE"
        // 0152555 is reserved for voicemail - see tests below
        "0152556"        | "DE"
        "0152557"        | "DE"
        "0152558"        | "DE"
        "0152559"        | "DE"
        "015256"         | "DE"
        "015257"         | "DE"
        "015258"         | "DE"
        "015259"         | "DE"

        "015260"         | "DE"
        "015261"         | "DE"
        "015262"         | "DE"
        "015263"         | "DE"
        "015264"         | "DE"
        // 0152650 is reserved for voicemail - see tests below
        "0152651"        | "DE"
        "0152652"        | "DE"
        "0152653"        | "DE"
        "0152654"        | "DE"
        // 0152655 is reserved for voicemail - see tests below
        "0152656"        | "DE"
        "0152657"        | "DE"
        "0152658"        | "DE"
        "0152659"        | "DE"
        "015266"         | "DE"
        "015267"         | "DE"
        "015268"         | "DE"
        "015269"         | "DE"

        "015270"         | "DE"
        "015271"         | "DE"
        "015272"         | "DE"
        "015273"         | "DE"
        "015274"         | "DE"
        // 0152750 is reserved for voicemail - see tests below
        "0152751"        | "DE"
        "0152752"        | "DE"
        "0152753"        | "DE"
        "0152754"        | "DE"
        // 0152755 is reserved for voicemail - see tests below
        "0152756"        | "DE"
        "0152757"        | "DE"
        "0152758"        | "DE"
        "0152759"        | "DE"
        "015276"         | "DE"
        "015277"         | "DE"
        "015278"         | "DE"
        "015279"         | "DE"

        "015280"         | "DE"
        "015281"         | "DE"
        "015282"         | "DE"
        "015283"         | "DE"
        "015284"         | "DE"
        // 0152850 is reserved for voicemail - see tests below
        "0152851"        | "DE"
        "0152852"        | "DE"
        "0152853"        | "DE"
        "0152854"        | "DE"
        // 0152855 is reserved for voicemail - see tests below
        "0152856"        | "DE"
        "0152857"        | "DE"
        "0152858"        | "DE"
        "0152859"        | "DE"
        "015286"         | "DE"
        "015287"         | "DE"
        "015288"         | "DE"
        "015289"         | "DE"

        "015290"         | "DE"
        "015291"         | "DE"
        "015292"         | "DE"
        "015293"         | "DE"
        "015294"         | "DE"
        // 0152950 is reserved for voicemail - see tests below
        "0152951"        | "DE"
        "0152952"        | "DE"
        "0152953"        | "DE"
        "0152954"        | "DE"
        // 0152955 is reserved for voicemail - see tests below
        "0152956"        | "DE"
        "0152957"        | "DE"
        "0152958"        | "DE"
        "0152959"        | "DE"
        "015296"         | "DE"
        "015297"         | "DE"
        "015298"         | "DE"
        "015299"         | "DE"

        //
        // 0153
        //
        // 015300 is reserved for voicemail - see tests below
        "015301"         | "DE"
        "015302"         | "DE"
        "015303"         | "DE"
        "015304"         | "DE"
        "015305"         | "DE"
        "015306"         | "DE"
        "015307"         | "DE"
        "015308"         | "DE"
        "015309"         | "DE"
        "01531"          | "DE"
        "01532"          | "DE"
        "01533"          | "DE"
        "01534"          | "DE"
        "01535"          | "DE"
        "01536"          | "DE"
        "01537"          | "DE"
        "01538"          | "DE"
        "01539"          | "DE"

        //
        // 0154
        //
        // 015400 is reserved for voicemail - see tests below
        "015401"         | "DE"
        "015402"         | "DE"
        "015403"         | "DE"
        "015404"         | "DE"
        "015405"         | "DE"
        "015406"         | "DE"
        "015407"         | "DE"
        "015408"         | "DE"
        "015409"         | "DE"
        "01541"          | "DE"
        "01542"          | "DE"
        "01543"          | "DE"
        "0154"          | "DE"
        "01545"          | "DE"
        "01546"          | "DE"
        "01547"          | "DE"
        "01548"          | "DE"
        "01549"          | "DE"

        //
        // 0155
        //
        // 015500 is reserved for voicemail - see tests below
        "015501"         | "DE"
        "015502"         | "DE"
        "015503"         | "DE"
        "015504"         | "DE"
        "015505"         | "DE"
        "015506"         | "DE"
        "015507"         | "DE"
        "015508"         | "DE"
        "015509"         | "DE"
        "01551"          | "DE"
        "01552"          | "DE"
        "01553"          | "DE"
        "01554"          | "DE"
        "01555"          | "DE"
        "01556"          | "DE"
        "01557"          | "DE"
        "01558"          | "DE"
        "01559"          | "DE"

        //
        // 0156
        //
        // 015600 is reserved for voicemail - see tests below
        "015601"         | "DE"
        "015602"         | "DE"
        "015603"         | "DE"
        "015604"         | "DE"
        "015605"         | "DE"
        "015606"         | "DE"
        "015607"         | "DE"
        "015608"         | "DE"
        "015609"         | "DE"
        "01561"          | "DE"
        "01562"          | "DE"
        "01563"          | "DE"
        "01564"          | "DE"
        "01565"          | "DE"
        "01566"          | "DE"
        "01567"          | "DE"
        "01568"          | "DE"
        "01569"          | "DE"

        //
        // 0157
        //
        "015700"         | "DE"
        "015701"         | "DE"
        "015702"         | "DE"
        "015703"         | "DE"
        "015704"         | "DE"
        "015705"         | "DE"
        "015706"         | "DE"
        "015707"         | "DE"
        "015708"         | "DE"
        "0157090"        | "DE"
        "0157091"        | "DE"
        "0157092"        | "DE"
        "0157093"        | "DE"
        "0157094"        | "DE"
        "0157095"        | "DE"
        "0157096"        | "DE"
        "0157097"        | "DE"
        "0157098"        | "DE"
        // 0157099 is reserved for voicemail - see tests below

        "015710"         | "DE"
        "015711"         | "DE"
        "015712"         | "DE"
        "015713"         | "DE"
        "015714"         | "DE"
        "015715"         | "DE"
        "015716"         | "DE"
        "015717"         | "DE"
        "015718"         | "DE"
        "0157190"        | "DE"
        "0157191"        | "DE"
        "0157192"        | "DE"
        "0157193"        | "DE"
        "0157194"        | "DE"
        "0157195"        | "DE"
        "0157196"        | "DE"
        "0157197"        | "DE"
        "0157198"        | "DE"
        // 0157199 is reserved for voicemail - see tests below

        "015720"         | "DE"
        "015721"         | "DE"
        "015722"         | "DE"
        "015723"         | "DE"
        "015724"         | "DE"
        "015725"         | "DE"
        "015726"         | "DE"
        "015727"         | "DE"
        "015728"         | "DE"
        "0157290"        | "DE"
        "0157291"        | "DE"
        "0157292"        | "DE"
        "0157293"        | "DE"
        "0157294"        | "DE"
        "0157295"        | "DE"
        "0157296"        | "DE"
        "0157297"        | "DE"
        "0157298"        | "DE"
        // 0157299 is reserved for voicemail - see tests below

        "015730"         | "DE"
        "015731"         | "DE"
        "015732"         | "DE"
        "015733"         | "DE"
        "015734"         | "DE"
        "015735"         | "DE"
        "015736"         | "DE"
        "015737"         | "DE"
        "015738"         | "DE"
        "0157390"        | "DE"
        "0157391"        | "DE"
        "0157392"        | "DE"
        "0157393"        | "DE"
        "0157394"        | "DE"
        "0157395"        | "DE"
        "0157396"        | "DE"
        "0157397"        | "DE"
        "0157398"        | "DE"
        // 0157399 is reserved for voicemail - see tests below

        "015740"         | "DE"
        "015741"         | "DE"
        "015742"         | "DE"
        "015743"         | "DE"
        "015744"         | "DE"
        "015745"         | "DE"
        "015746"         | "DE"
        "015747"         | "DE"
        "015748"         | "DE"
        "0157490"        | "DE"
        "0157491"        | "DE"
        "0157492"        | "DE"
        "0157493"        | "DE"
        "0157494"        | "DE"
        "0157495"        | "DE"
        "0157496"        | "DE"
        "0157497"        | "DE"
        "0157498"        | "DE"
        // 0157499 is reserved for voicemail - see tests below

        "015750"         | "DE"
        "015751"         | "DE"
        "015752"         | "DE"
        "015753"         | "DE"
        "015754"         | "DE"
        "015755"         | "DE"
        "015756"         | "DE"
        "015757"         | "DE"
        "015758"         | "DE"
        "0157590"        | "DE"
        "0157591"        | "DE"
        "0157592"        | "DE"
        "0157593"        | "DE"
        "0157594"        | "DE"
        "0157595"        | "DE"
        "0157596"        | "DE"
        "0157597"        | "DE"
        "0157598"        | "DE"
        // 0157599 is reserved for voicemail - see tests below

        "015760"         | "DE"
        "015761"         | "DE"
        "015762"         | "DE"
        "015763"         | "DE"
        "015764"         | "DE"
        "015765"         | "DE"
        "015766"         | "DE"
        "015767"         | "DE"
        "015768"         | "DE"
        "0157690"        | "DE"
        "0157691"        | "DE"
        "0157692"        | "DE"
        "0157693"        | "DE"
        "0157694"        | "DE"
        "0157695"        | "DE"
        "0157696"        | "DE"
        "0157697"        | "DE"
        "0157698"        | "DE"
        // 0157699 is reserved for voicemail - see tests below

        "015770"         | "DE"
        "015771"         | "DE"
        "015772"         | "DE"
        "015773"         | "DE"
        "015774"         | "DE"
        "015775"         | "DE"
        "015776"         | "DE"
        "015777"         | "DE"
        "015778"         | "DE"
        "0157790"        | "DE"
        "0157791"        | "DE"
        "0157792"        | "DE"
        "0157793"        | "DE"
        "0157794"        | "DE"
        "0157795"        | "DE"
        "0157796"        | "DE"
        "0157797"        | "DE"
        "0157798"        | "DE"
        // 0157799 is reserved for voicemail - see tests below

        "015780"         | "DE"
        "015781"         | "DE"
        "015782"         | "DE"
        "015783"         | "DE"
        "015784"         | "DE"
        "015785"         | "DE"
        "015786"         | "DE"
        "015787"         | "DE"
        "015788"         | "DE"
        "0157890"        | "DE"
        "0157891"        | "DE"
        "0157892"        | "DE"
        "0157893"        | "DE"
        "0157894"        | "DE"
        "0157895"        | "DE"
        "0157896"        | "DE"
        "0157897"        | "DE"
        "0157898"        | "DE"
        // 0157899 is reserved for voicemail - see tests below

        "015790"         | "DE"
        "015791"         | "DE"
        "015792"         | "DE"
        "015793"         | "DE"
        "015794"         | "DE"
        "015795"         | "DE"
        "015796"         | "DE"
        "015797"         | "DE"
        "015798"         | "DE"
        "0157990"        | "DE"
        "0157991"        | "DE"
        "0157992"        | "DE"
        "0157993"        | "DE"
        "0157994"        | "DE"
        "0157995"        | "DE"
        "0157996"        | "DE"
        "0157997"        | "DE"
        "0157998"        | "DE"
        // 0157999 is reserved for voicemail - see tests below

        //
        // 0158
        //
        // 015800 is reserved for voicemail - see tests below
        "015801"         | "DE"
        "015802"         | "DE"
        "015803"         | "DE"
        "015804"         | "DE"
        "015805"         | "DE"
        "015806"         | "DE"
        "015807"         | "DE"
        "015808"         | "DE"
        "015809"         | "DE"
        "01581"          | "DE"
        "01582"          | "DE"
        "01583"          | "DE"
        "01584"          | "DE"
        "01585"          | "DE"
        "01586"          | "DE"
        "01587"          | "DE"
        "01588"          | "DE"
        "01589"          | "DE"

        //
        // 0159
        //
        "015900"         | "DE"
        "015901"         | "DE"
        "015902"         | "DE"
        "0159030"        | "DE"
        "0159031"        | "DE"
        "0159032"        | "DE"
        // 0159033 is reserved for voicemail - see tests below
        "0159034"        | "DE"
        "0159035"        | "DE"
        "0159036"        | "DE"
        "0159037"        | "DE"
        "0159038"        | "DE"
        "0159039"        | "DE"
        "015904"         | "DE"
        "015905"         | "DE"
        "015906"         | "DE"
        "015907"         | "DE"
        "015908"         | "DE"
        "015909"         | "DE"

        "015910"         | "DE"
        "015911"         | "DE"
        "015912"         | "DE"
        "0159130"        | "DE"
        "0159131"        | "DE"
        "0159132"        | "DE"
        // 0159133 is reserved for voicemail - see tests below
        "0159134"        | "DE"
        "0159135"        | "DE"
        "0159136"        | "DE"
        "0159137"        | "DE"
        "0159138"        | "DE"
        "0159139"        | "DE"
        "015914"         | "DE"
        "015915"         | "DE"
        "015916"         | "DE"
        "015917"         | "DE"
        "015918"         | "DE"
        "015919"         | "DE"

        "015920"         | "DE"
        "015921"         | "DE"
        "015922"         | "DE"
        "0159230"        | "DE"
        "0159231"        | "DE"
        "0159232"        | "DE"
        // 0159233 is reserved for voicemail - see tests below
        "0159234"        | "DE"
        "0159235"        | "DE"
        "0159236"        | "DE"
        "0159237"        | "DE"
        "0159238"        | "DE"
        "0159239"        | "DE"
        "015924"         | "DE"
        "015925"         | "DE"
        "015926"         | "DE"
        "015927"         | "DE"
        "015928"         | "DE"
        "015929"         | "DE"

        "015930"         | "DE"
        "015931"         | "DE"
        "015932"         | "DE"
        "0159330"        | "DE"
        "0159331"        | "DE"
        "0159332"        | "DE"
        // 0159333 is reserved for voicemail - see tests below
        "0159334"        | "DE"
        "0159335"        | "DE"
        "0159336"        | "DE"
        "0159337"        | "DE"
        "0159338"        | "DE"
        "0159339"        | "DE"
        "015934"         | "DE"
        "015935"         | "DE"
        "015936"         | "DE"
        "015937"         | "DE"
        "015938"         | "DE"
        "015939"         | "DE"

        "015940"         | "DE"
        "015941"         | "DE"
        "015942"         | "DE"
        "0159430"        | "DE"
        "0159431"        | "DE"
        "0159432"        | "DE"
        // 0159433 is reserved for voicemail - see tests below
        "0159434"        | "DE"
        "0159435"        | "DE"
        "0159436"        | "DE"
        "0159437"        | "DE"
        "0159438"        | "DE"
        "0159439"        | "DE"
        "015944"         | "DE"
        "015945"         | "DE"
        "015946"         | "DE"
        "015947"         | "DE"
        "015948"         | "DE"
        "015949"         | "DE"

        "015950"         | "DE"
        "015951"         | "DE"
        "015952"         | "DE"
        "0159530"        | "DE"
        "0159531"        | "DE"
        "0159532"        | "DE"
        // 0159533 is reserved for voicemail - see tests below
        "0159534"        | "DE"
        "0159535"        | "DE"
        "0159536"        | "DE"
        "0159537"        | "DE"
        "0159538"        | "DE"
        "0159539"        | "DE"
        "015954"         | "DE"
        "015955"         | "DE"
        "015956"         | "DE"
        "015957"         | "DE"
        "015958"         | "DE"
        "015959"         | "DE"

        "015960"         | "DE"
        "015961"         | "DE"
        "015962"         | "DE"
        "0159630"        | "DE"
        "0159631"        | "DE"
        "0159632"        | "DE"
        // 0159633 is reserved for voicemail - see tests below
        "0159634"        | "DE"
        "0159635"        | "DE"
        "0159636"        | "DE"
        "0159637"        | "DE"
        "0159638"        | "DE"
        "0159639"        | "DE"
        "015964"         | "DE"
        "015965"         | "DE"
        "015966"         | "DE"
        "015967"         | "DE"
        "015968"         | "DE"
        "015969"         | "DE"

        "015970"         | "DE"
        "015971"         | "DE"
        "015972"         | "DE"
        "0159730"        | "DE"
        "0159731"        | "DE"
        "0159732"        | "DE"
        // 0159733 is reserved for voicemail - see tests below
        "0159734"        | "DE"
        "0159735"        | "DE"
        "0159736"        | "DE"
        "0159737"        | "DE"
        "0159738"        | "DE"
        "0159739"        | "DE"
        "015974"         | "DE"
        "015975"         | "DE"
        "015976"         | "DE"
        "015977"         | "DE"
        "015978"         | "DE"
        "015979"         | "DE"

        "015980"         | "DE"
        "015981"         | "DE"
        "015982"         | "DE"
        "0159830"        | "DE"
        "0159831"        | "DE"
        "0159832"        | "DE"
        // 0159833 is reserved for voicemail - see tests below
        "0159834"        | "DE"
        "0159835"        | "DE"
        "0159836"        | "DE"
        "0159837"        | "DE"
        "0159838"        | "DE"
        "0159839"        | "DE"
        "015984"         | "DE"
        "015985"         | "DE"
        "015986"         | "DE"
        "015987"         | "DE"
        "015988"         | "DE"
        "015989"         | "DE"

        "015990"         | "DE"
        "015991"         | "DE"
        "015992"         | "DE"
        "0159930"        | "DE"
        "0159931"        | "DE"
        "0159932"        | "DE"
        // 0159933 is reserved for voicemail - see tests below
        "0159934"        | "DE"
        "0159935"        | "DE"
        "0159936"        | "DE"
        "0159937"        | "DE"
        "0159938"        | "DE"
        "0159939"        | "DE"
        "015994"         | "DE"
        "015995"         | "DE"
        "015996"         | "DE"
        "015997"         | "DE"
        "015998"         | "DE"
        "015999"         | "DE"

        // end of 015xx 
    }

    def "validate German Mobile 15 range with voicemail infix"(String numberUntilInfix, regionCode) {
        given:
        String[] numbersToTest
        if (numberUntilInfix.length() == 6) {
            numbersToTest = [numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "00000000",
                             numberUntilInfix + "000000000",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999",
                             numberUntilInfix + "99999999",
                             numberUntilInfix + "9999999999"]
        }
        if (numberUntilInfix.length() == 7) {
            numbersToTest = [numberUntilInfix + "0000",
                             numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "00000000",
                             numberUntilInfix + "9999",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999",
                             numberUntilInfix + "999999999"]
        }
        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG]


        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            results[i] == expectedResults[i]
        }

        where:

        numberUntilInfix | regionCode
        // There infixes of two digits used to address the voicemail of a line
        // see 2.5 in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // This makes the number two digits longer, but on the other hand a short version with the infix does not exists, that is the reason, why above range started at 15001, since 15000 would be an infix

        // 15-0-INFIX:OO-xx 3-Block: 0xx
        "015000"         | "DE"

        // 15-1-INFIX:13-x(x) 2-Block: 1x and 3-Block: 1xx
        "015113"         | "DE"

        // 15-2x-INFIX:50-(x) 2-Block: 2x and 3-Block: 2xx  First Infix: 50
        "0152050"         | "DE"
        "0152150"         | "DE"
        "0152250"         | "DE"
        "0152350"         | "DE"
        "0152450"         | "DE"
        "0152550"         | "DE"
        "0152650"         | "DE"
        "0152750"         | "DE"
        "0152850"         | "DE"
        "0152950"         | "DE"
        // 15-2x-INFIX:55-(x) 2-Block: 2x and 3-Block: 2xx  Second Infix: 55
        "0152055"         | "DE"
        "0152155"         | "DE"
        "0152255"         | "DE"
        "0152355"         | "DE"
        "0152455"         | "DE"
        "0152555"         | "DE"
        "0152655"         | "DE"
        "0152755"         | "DE"
        "0152855"         | "DE"
        "0152955"         | "DE"

        // 15-3-INFIX:OO-xx 3-Block: 3xx
        "015300"         | "DE"

        // 15-4-INFIX:OO-xx 3-Block: 4xx
        "015400"         | "DE"

        // 15-5-INFIX:OO-xx 3-Block: 5xx
        "015500"         | "DE"

        // 15-6-INFIX:OO-xx 3-Block: 6xx
        "015600"         | "DE"

        // 15-7x-INFIX:99-(x) 2-Block: 7x and 3-Block: 7xx
        "0157099"         | "DE"
        "0157199"         | "DE"
        "0157299"         | "DE"
        "0157399"         | "DE"
        "0157499"         | "DE"
        "0157599"         | "DE"
        "0157699"         | "DE"
        "0157799"         | "DE"
        "0157899"         | "DE"
        "0157999"         | "DE"

        // 15-8-INFIX:OO-xx 3-Block: 8xx
        "015800"         | "DE"

        // 15-9x-INFIX:33-(x) 2-Block: 9x and 3-Block: 9xx
        "0159033"         | "DE"
        "0159133"         | "DE"
        "0159233"         | "DE"
        "0159333"         | "DE"
        "0159433"         | "DE"
        "0159533"         | "DE"
        "0159633"         | "DE"
        "0159733"         | "DE"
        "0159833"         | "DE"
        "0159933"         | "DE"

        // end of 015xx for voicemail
    }

    /*
 TODO NDC Ranges see equivalent Testcases in IsValidNumberTest
 */

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

    // TODO: Reserve NDC like (0)11 where (0)115 and (0)116 is used, or (0)13 where (0)137x is used

}
