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
        assert result == expectedResult

        where:

        number                      | regionCode  | expectedResult
        // short code for Police (110) is not dial-able internationally nor does it has additional numbers
        "110"                       | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is short code, valid only locally
        "110556677"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // subscriber number starts with short code
        "0110"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0110 556677"               | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER                   // number starts with NAC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "0175 110"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 110555"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 1105555"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 11055555"             | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 110555555"            | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0203 110"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203 110555"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC within the region
        "+49110"                    | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49110 556677"             | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER                   // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 110"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 110555"             | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1105555"            | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11055555"           | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 110555555"          | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 110"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203 110555"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC from outside the region
        "+49110"                    | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49110 556677"             | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER                   // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
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
        assert result == expectedResult

        where:

        number                      | regionCode  | expectedResult
        // short code for Police (112) is not dial-able internationally nor does it has additional numbers
        "112"                       | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY                   // number is short code, valid only locally
        "112556677"                 | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // subscriber number starts with short code
        "0112"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE             // number starts with NAC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means NAC is the problem
        "0112 556677"               | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER                   // number starts with NAC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "0175 112"                  | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 112555"               | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 1125555"              | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY                // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 11255555"             | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0175 112555555"            | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with NAC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "0203 112"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with NAC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "0203 112555"               | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with NAC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC within the region
        "+49112"                    | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49112 556677"             | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER                   // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
        "+49175 112"                | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 112555"             | "DE"       | PhoneNumberValidationResult.TOO_SHORT                                // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 1125555"            | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE                              // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 11255555"           | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49175 112555555"          | "DE"       | PhoneNumberValidationResult.TOO_LONG                                 // number starts with IDP+CC, mandatory mobile NDC follows, so subscriber number is not overlapping with short codes - but SN length for this NDC is 7
        "+49203 112"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE        // number starts with IDP+CC, optional fixed line NDC follows, SN equals short code (but overlapping) => assuming Short Code is intended, which means NDC is wrongly used
        "+49203 112555"             | "DE"       | PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER      // number starts with IDP+CC, optional fixed line NDC follows, SN starts with short code (overlapping) => assuming NDC is intended, which means SN is wrong
        // using IDP+CC from outside the region
        "+49112"                    | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE                     // number starts with IDP+CC, normally NDC would follow, but that equals short code => assuming Short Code is intended, which means CC is the problem
        "+49112 556677"             | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER                   // number starts with IDP+CC, rest is longer than short code (see one above), so its 11x NDC which is just reserve
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
        assert result == expectedResult

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
        assert result == expectedResult

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
        assert result == expectedResult

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

        "0118"                      | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "01180"                     | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "011800"                    | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "0118000"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "0118099"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "01180000"                  | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "01181"                     | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "011810"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "011833"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "0118100"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "01189"                     | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "011899"                    | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "0118999"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER

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
        "+49118"                    | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491180"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+4911800"                  | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+49118000"                 | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+49118099"                 | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491180000"                | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491181"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+4911810"                  | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+4911833"                  | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118100"                 | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491189"                   | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+4911899"                  | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118999"                 | "DE"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER

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
        "+49118"                    | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491180"                   | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+4911800"                  | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+49118000"                 | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+49118099"                 | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491180000"                | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491181"                   | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+4911810"                  | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+4911833"                  | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118100"                 | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+491189"                   | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER
        "+4911899"                  | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49118999"                 | "FR"       | PhoneNumberValidationResult.INVALID_RESERVE_NUMBER

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
        assert result == expectedResult

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
        assert result == expectedResult

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

    def "validate German Mobile 15 range"(String numberUntilInfix, regionCode, String expectedResultskey) {
        /*
        "ASSIGNED" mentioned as assigned in https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/zugeteilteRNB/start.html

        "RESERVED" mentioned as reserved in above document (even as larger range)

        "FREE" mentioned as free in https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/freieRNB/start.html

        "NOT_FURTHER_SPECIFIED" not mentioned in any of the above documents

        TODO: Make a generate Script for this testcase data
         */
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

        // default (any not known expectedResultskey and ASSIGNED
        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG]

        if (expectedResultskey == "NOT_FURTHER_SPECIFIED") {
            expectedResults = [PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE]
        }

        if (expectedResultskey == "FREE") {
           expectedResults = [PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE]
        }

        if (expectedResultskey == "RESERVED") {
            expectedResults = [PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE,
                               PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE]
        }

        when: "get numbers isValid: $numbersToTest"
        PhoneNumberValidationResult[] results = []

        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:

        numberUntilInfix | regionCode | expectedResultskey
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 015xxyyyyyyy xx = block code, yyyyyyy fixed length number in 2 digit block, so together 9 digit is the overall length
        // 015zzzaaaaaa zzz = newer block zzz, aaaaaa fixes length number in 3 digit block, so together 9 digit is the overall length

        //
        // 0150
        //
        // 015000 is reserved for voicemail - see tests below
        "015001"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015002"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015003"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015004"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015005"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015006"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015007"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015008"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015009"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015010"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015011"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015012"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015013"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015014"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015015"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015016"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015017"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015018"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015019"         | "DE"      | "ASSIGNED"
        "015020"         | "DE"      | "ASSIGNED"
        "015021"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015022"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015023"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015024"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015025"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015026"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015027"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015028"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015029"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015030"         | "DE"      | "FREE"
        "015031"         | "DE"      | "FREE"
        "015032"         | "DE"      | "FREE"
        "015033"         | "DE"      | "FREE"
        "015034"         | "DE"      | "FREE"
        "015035"         | "DE"      | "FREE"
        "015036"         | "DE"      | "FREE"
        "015037"         | "DE"      | "FREE"
        "015038"         | "DE"      | "FREE"
        "015039"         | "DE"      | "FREE"
        "015040"         | "DE"      | "FREE"
        "015041"         | "DE"      | "FREE"
        "015042"         | "DE"      | "FREE"
        "015043"         | "DE"      | "FREE"
        "015044"         | "DE"      | "FREE"
        "015045"         | "DE"      | "FREE"
        "015046"         | "DE"      | "FREE"
        "015047"         | "DE"      | "FREE"
        "015048"         | "DE"      | "FREE"
        "015049"         | "DE"      | "FREE"
        "015050"         | "DE"      | "FREE"
        "015051"         | "DE"      | "FREE"
        "015052"         | "DE"      | "FREE"
        "015053"         | "DE"      | "FREE"
        "015054"         | "DE"      | "FREE"
        "015055"         | "DE"      | "FREE"
        "015056"         | "DE"      | "FREE"
        "015057"         | "DE"      | "FREE"
        "015058"         | "DE"      | "FREE"
        "015059"         | "DE"      | "FREE"
        "015060"         | "DE"      | "FREE"
        "015061"         | "DE"      | "FREE"
        "015062"         | "DE"      | "FREE"
        "015063"         | "DE"      | "FREE"
        "015064"         | "DE"      | "FREE"
        "015065"         | "DE"      | "FREE"
        "015066"         | "DE"      | "FREE"
        "015067"         | "DE"      | "FREE"
        "015068"         | "DE"      | "FREE"
        "015069"         | "DE"      | "FREE"
        "015070"         | "DE"      | "FREE"
        "015071"         | "DE"      | "FREE"
        "015072"         | "DE"      | "FREE"
        "015073"         | "DE"      | "FREE"
        "015074"         | "DE"      | "FREE"
        "015075"         | "DE"      | "FREE"
        "015076"         | "DE"      | "FREE"
        "015077"         | "DE"      | "FREE"
        "015078"         | "DE"      | "FREE"
        "015079"         | "DE"      | "FREE"
        "015080"         | "DE"      | "FREE"
        "015081"         | "DE"      | "FREE"
        "015082"         | "DE"      | "FREE"
        "015083"         | "DE"      | "FREE"
        "015084"         | "DE"      | "FREE"
        "015085"         | "DE"      | "FREE"
        "015086"         | "DE"      | "FREE"
        "015087"         | "DE"      | "FREE"
        "015088"         | "DE"      | "FREE"
        "015089"         | "DE"      | "FREE"
        "015090"         | "DE"      | "FREE"
        "015091"         | "DE"      | "FREE"
        "015092"         | "DE"      | "FREE"
        "015093"         | "DE"      | "FREE"
        "015094"         | "DE"      | "FREE"
        "015095"         | "DE"      | "FREE"
        "015096"         | "DE"      | "FREE"
        "015097"         | "DE"      | "FREE"
        "015098"         | "DE"      | "FREE"
        "015099"         | "DE"      | "FREE"
        //
        // 0151
        //
        "01510"          | "DE"      | "RESERVED"
        // 01511 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015110"         | "DE"      | "ASSIGNED"
        "015111"         | "DE"      | "ASSIGNED"
        "015112"         | "DE"      | "ASSIGNED"
        // 015113 is reserved for voicemail - see tests below
        "015114"         | "DE"      | "ASSIGNED"
        "015115"         | "DE"      | "ASSIGNED"
        "015116"         | "DE"      | "ASSIGNED"
        "015117"         | "DE"      | "ASSIGNED"
        "015118"         | "DE"      | "ASSIGNED"
        "015119"         | "DE"      | "ASSIGNED"
        // end of 01511 non VoiceMail
        "01512"          | "DE"      | "ASSIGNED"
        "01513"          | "DE"      | "RESERVED"
        "01514"          | "DE"      | "ASSIGNED"
        "01515"          | "DE"      | "ASSIGNED"
        "01516"          | "DE"      | "ASSIGNED"
        "01517"          | "DE"      | "ASSIGNED"
        "015180"         | "DE"      | "ASSIGNED"
        "015181"         | "DE"      | "ASSIGNED"
        "015182"         | "DE"      | "ASSIGNED"
        "015183"         | "DE"      | "ASSIGNED"
        "015184"         | "DE"      | "ASSIGNED"
        "015185"         | "DE"      | "ASSIGNED"
        "015186"         | "DE"      | "ASSIGNED"
        "015187"         | "DE"      | "RESERVED"
        "015188"         | "DE"      | "RESERVED"
        "015189"         | "DE"      | "RESERVED"
        "01519"          | "DE"      | "RESERVED"

        //
        // 0152
        //
        // 01520 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015200"         | "DE"      | "ASSIGNED"
        "015201"         | "DE"      | "ASSIGNED"
        "015202"         | "DE"      | "ASSIGNED"
        "015203"         | "DE"      | "ASSIGNED"
        "015204"         | "DE"      | "ASSIGNED"
        // 0152050 is reserved for voicemail - see tests below
        "0152051"        | "DE"      | "ASSIGNED"
        "0152052"        | "DE"      | "ASSIGNED"
        "0152053"        | "DE"      | "ASSIGNED"
        "0152054"        | "DE"      | "ASSIGNED"
        // 0152055 is reserved for voicemail - see tests below
        "0152056"        | "DE"      | "ASSIGNED"
        "0152057"        | "DE"      | "ASSIGNED"
        "0152058"        | "DE"      | "ASSIGNED"
        "0152059"        | "DE"      | "ASSIGNED"
        "015206"         | "DE"      | "ASSIGNED"
        "015207"         | "DE"      | "ASSIGNED"
        "015208"         | "DE"      | "ASSIGNED"
        "015209"         | "DE"      | "ASSIGNED"
        // end of 01520 non VoiceMail
        // 01521 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015210"         | "DE"      | "ASSIGNED"
        "015211"         | "DE"      | "ASSIGNED"
        "015212"         | "DE"      | "ASSIGNED"
        "015213"         | "DE"      | "ASSIGNED"
        "015214"         | "DE"      | "ASSIGNED"
        // 0152150 is reserved for voicemail - see tests below
        "0152151"        | "DE"      | "ASSIGNED"
        "0152152"        | "DE"      | "ASSIGNED"
        "0152153"        | "DE"      | "ASSIGNED"
        "0152154"        | "DE"      | "ASSIGNED"
        // 0152155 is reserved for voicemail - see tests below
        "0152156"        | "DE"      | "ASSIGNED"
        "0152157"        | "DE"      | "ASSIGNED"
        "0152158"        | "DE"      | "ASSIGNED"
        "0152159"        | "DE"      | "ASSIGNED"
        "015216"         | "DE"      | "ASSIGNED"
        "015217"         | "DE"      | "ASSIGNED"
        "015218"         | "DE"      | "ASSIGNED"
        "015219"         | "DE"      | "ASSIGNED"
        // end of 01521 non VoiceMail
        // 01522 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015220"         | "DE"      | "ASSIGNED"
        "015221"         | "DE"      | "ASSIGNED"
        "015222"         | "DE"      | "ASSIGNED"
        "015223"         | "DE"      | "ASSIGNED"
        "015224"         | "DE"      | "ASSIGNED"
        // 0152250 is reserved for voicemail - see tests below
        "0152251"        | "DE"      | "ASSIGNED"
        "0152252"        | "DE"      | "ASSIGNED"
        "0152253"        | "DE"      | "ASSIGNED"
        "0152254"        | "DE"      | "ASSIGNED"
        // 0152255 is reserved for voicemail - see tests below
        "0152256"        | "DE"      | "ASSIGNED"
        "0152257"        | "DE"      | "ASSIGNED"
        "0152258"        | "DE"      | "ASSIGNED"
        "0152259"        | "DE"      | "ASSIGNED"
        "015226"         | "DE"      | "ASSIGNED"
        "015227"         | "DE"      | "ASSIGNED"
        "015228"         | "DE"      | "ASSIGNED"
        "015229"         | "DE"      | "ASSIGNED"
        // end of 01522 non VoiceMail
        // 01523 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015230"         | "DE"      | "ASSIGNED"
        "015231"         | "DE"      | "ASSIGNED"
        "015232"         | "DE"      | "ASSIGNED"
        "015233"         | "DE"      | "ASSIGNED"
        "015234"         | "DE"      | "ASSIGNED"
        // 0152350 is reserved for voicemail - see tests below
        "0152351"        | "DE"      | "ASSIGNED"
        "0152352"        | "DE"      | "ASSIGNED"
        "0152353"        | "DE"      | "ASSIGNED"
        "0152354"        | "DE"      | "ASSIGNED"
        // 0152355 is reserved for voicemail - see tests below
        "0152356"        | "DE"      | "ASSIGNED"
        "0152357"        | "DE"      | "ASSIGNED"
        "0152358"        | "DE"      | "ASSIGNED"
        "0152359"        | "DE"      | "ASSIGNED"
        "015236"         | "DE"      | "ASSIGNED"
        "015237"         | "DE"      | "ASSIGNED"
        "015238"         | "DE"      | "ASSIGNED"
        "015239"         | "DE"      | "ASSIGNED"
        // end of 01523 non VoiceMail
        // 01524 is NOT assigned, but because of VoiceMail Infix, starting numbers are checked
        "015240"         | "DE"      | "RESERVED"
        "015241"         | "DE"      | "RESERVED"
        "015242"         | "DE"      | "RESERVED"
        "015243"         | "DE"      | "RESERVED"
        "015244"         | "DE"      | "RESERVED"
        // 0152450 is reserved for voicemail - see tests below
        "0152451"        | "DE"      | "RESERVED"
        "0152452"        | "DE"      | "RESERVED"
        "0152453"        | "DE"      | "RESERVED"
        "0152454"        | "DE"      | "RESERVED"
        // 0152455 is reserved for voicemail - see tests below
        "0152456"        | "DE"      | "RESERVED"
        "0152457"        | "DE"      | "RESERVED"
        "0152458"        | "DE"      | "RESERVED"
        "0152459"        | "DE"      | "RESERVED"
        "015246"         | "DE"      | "RESERVED"
        "015247"         | "DE"      | "RESERVED"
        "015248"         | "DE"      | "RESERVED"
        "015249"         | "DE"      | "RESERVED"
        // end of 01524 non VoiceMail
        // 01525 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015250"         | "DE"      | "ASSIGNED"
        "015251"         | "DE"      | "ASSIGNED"
        "015252"         | "DE"      | "ASSIGNED"
        "015253"         | "DE"      | "ASSIGNED"
        "015254"         | "DE"      | "ASSIGNED"
        // 0152550 is reserved for voicemail - see tests below
        "0152551"        | "DE"      | "ASSIGNED"
        "0152552"        | "DE"      | "ASSIGNED"
        "0152553"        | "DE"      | "ASSIGNED"
        "0152554"        | "DE"      | "ASSIGNED"
        // 0152555 is reserved for voicemail - see tests below
        "0152556"        | "DE"      | "ASSIGNED"
        "0152557"        | "DE"      | "ASSIGNED"
        "0152558"        | "DE"      | "ASSIGNED"
        "0152559"        | "DE"      | "ASSIGNED"
        "015256"         | "DE"      | "ASSIGNED"
        "015257"         | "DE"      | "ASSIGNED"
        "015258"         | "DE"      | "ASSIGNED"
        "015259"         | "DE"      | "ASSIGNED"
        // end of 01525 non VoiceMail
        // 01526 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015260"         | "DE"      | "ASSIGNED"
        "015261"         | "DE"      | "ASSIGNED"
        "015262"         | "DE"      | "ASSIGNED"
        "015263"         | "DE"      | "ASSIGNED"
        "015264"         | "DE"      | "ASSIGNED"
        // 0152650 is reserved for voicemail - see tests below
        "0152651"        | "DE"      | "ASSIGNED"
        "0152652"        | "DE"      | "ASSIGNED"
        "0152653"        | "DE"      | "ASSIGNED"
        "0152654"        | "DE"      | "ASSIGNED"
        // 0152655 is reserved for voicemail - see tests below
        "0152656"        | "DE"      | "ASSIGNED"
        "0152657"        | "DE"      | "ASSIGNED"
        "0152658"        | "DE"      | "ASSIGNED"
        "0152659"        | "DE"      | "ASSIGNED"
        "015266"         | "DE"      | "ASSIGNED"
        "015267"         | "DE"      | "ASSIGNED"
        "015268"         | "DE"      | "ASSIGNED"
        "015269"         | "DE"      | "ASSIGNED"
        // end of 01526 non VoiceMail
        // 01527 is NOT assigned, but because of VoiceMail Infix, starting numbers are checked
        "015270"         | "DE"      | "RESERVED"
        "015271"         | "DE"      | "RESERVED"
        "015272"         | "DE"      | "RESERVED"
        "015273"         | "DE"      | "RESERVED"
        "015274"         | "DE"      | "RESERVED"
        // 0152750 is reserved for voicemail - see tests below
        "0152751"        | "DE"      | "RESERVED"
        "0152752"        | "DE"      | "RESERVED"
        "0152753"        | "DE"      | "RESERVED"
        "0152754"        | "DE"      | "RESERVED"
        // 0152755 is reserved for voicemail - see tests below
        "0152756"        | "DE"      | "RESERVED"
        "0152757"        | "DE"      | "RESERVED"
        "0152758"        | "DE"      | "RESERVED"
        "0152759"        | "DE"      | "RESERVED"
        "015276"         | "DE"      | "RESERVED"
        "015277"         | "DE"      | "RESERVED"
        "015278"         | "DE"      | "RESERVED"
        "015279"         | "DE"      | "RESERVED"
        // end of 01527 non VoiceMail
        // 01528 is NOT assigned, but because of VoiceMail Infix, starting numbers are checked
        "015280"         | "DE"      | "RESERVED"
        "015281"         | "DE"      | "RESERVED"
        "015282"         | "DE"      | "RESERVED"
        "015283"         | "DE"      | "RESERVED"
        "015284"         | "DE"      | "RESERVED"
        // 0152850 is reserved for voicemail - see tests below
        "0152851"        | "DE"      | "RESERVED"
        "0152852"        | "DE"      | "RESERVED"
        "0152853"        | "DE"      | "RESERVED"
        "0152854"        | "DE"      | "RESERVED"
        // 0152855 is reserved for voicemail - see tests below
        "0152856"        | "DE"      | "RESERVED"
        "0152857"        | "DE"      | "RESERVED"
        "0152858"        | "DE"      | "RESERVED"
        "0152859"        | "DE"      | "RESERVED"
        "015286"         | "DE"      | "RESERVED"
        "015287"         | "DE"      | "RESERVED"
        "015288"         | "DE"      | "RESERVED"
        "015289"         | "DE"      | "RESERVED"
        // end of 01528 non VoiceMail
        // 01529 is assigned, but because of VoiceMail Infix, starting numbers are checked
        "015290"         | "DE"      | "ASSIGNED"
        "015291"         | "DE"      | "ASSIGNED"
        "015292"         | "DE"      | "ASSIGNED"
        "015293"         | "DE"      | "ASSIGNED"
        "015294"         | "DE"      | "ASSIGNED"
        // 0152950 is reserved for voicemail - see tests below
        "0152951"        | "DE"      | "ASSIGNED"
        "0152952"        | "DE"      | "ASSIGNED"
        "0152953"        | "DE"      | "ASSIGNED"
        "0152954"        | "DE"      | "ASSIGNED"
        // 0152955 is reserved for voicemail - see tests below
        "0152956"        | "DE"      | "ASSIGNED"
        "0152957"        | "DE"      | "ASSIGNED"
        "0152958"        | "DE"      | "ASSIGNED"
        "0152959"        | "DE"      | "ASSIGNED"
        "015296"         | "DE"      | "ASSIGNED"
        "015297"         | "DE"      | "ASSIGNED"
        "015298"         | "DE"      | "ASSIGNED"
        "015299"         | "DE"      | "ASSIGNED"
        // end of 01529 non VoiceMail
        //
        // 0153
        //
        // 015300 is reserved for voicemail - see tests below
        "015301"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015302"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015303"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015304"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015305"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015306"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015307"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015308"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015309"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015310"         | "DE"      | "ASSIGNED"
        "015311"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015312"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015313"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015314"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015315"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015316"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015317"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015318"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015319"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015320"         | "DE"      | "FREE"
        "015321"         | "DE"      | "FREE"
        "015322"         | "DE"      | "FREE"
        "015323"         | "DE"      | "FREE"
        "015324"         | "DE"      | "FREE"
        "015325"         | "DE"      | "FREE"
        "015326"         | "DE"      | "FREE"
        "015327"         | "DE"      | "FREE"
        "015328"         | "DE"      | "FREE"
        "015329"         | "DE"      | "FREE"
        "015330"         | "DE"      | "FREE"
        "015331"         | "DE"      | "FREE"
        "015332"         | "DE"      | "FREE"
        "015333"         | "DE"      | "ASSIGNED"
        "015334"         | "DE"      | "FREE"
        "015335"         | "DE"      | "FREE"
        "015336"         | "DE"      | "FREE"
        "015337"         | "DE"      | "FREE"
        "015338"         | "DE"      | "FREE"
        "015339"         | "DE"      | "FREE"
        "015340"         | "DE"      | "FREE"
        "015341"         | "DE"      | "FREE"
        "015342"         | "DE"      | "FREE"
        "015343"         | "DE"      | "FREE"
        "015344"         | "DE"      | "FREE"
        "015345"         | "DE"      | "FREE"
        "015346"         | "DE"      | "FREE"
        "015347"         | "DE"      | "FREE"
        "015348"         | "DE"      | "FREE"
        "015349"         | "DE"      | "FREE"
        "015350"         | "DE"      | "FREE"
        "015351"         | "DE"      | "FREE"
        "015352"         | "DE"      | "FREE"
        "015353"         | "DE"      | "FREE"
        "015354"         | "DE"      | "FREE"
        "015355"         | "DE"      | "FREE"
        "015356"         | "DE"      | "FREE"
        "015357"         | "DE"      | "FREE"
        "015358"         | "DE"      | "FREE"
        "015359"         | "DE"      | "FREE"
        "015360"         | "DE"      | "FREE"
        "015361"         | "DE"      | "FREE"
        "015362"         | "DE"      | "FREE"
        "015363"         | "DE"      | "FREE"
        "015364"         | "DE"      | "FREE"
        "015365"         | "DE"      | "FREE"
        "015366"         | "DE"      | "FREE"
        "015367"         | "DE"      | "FREE"
        "015368"         | "DE"      | "FREE"
        "015369"         | "DE"      | "FREE"
        "015370"         | "DE"      | "FREE"
        "015371"         | "DE"      | "FREE"
        "015372"         | "DE"      | "FREE"
        "015373"         | "DE"      | "FREE"
        "015374"         | "DE"      | "FREE"
        "015375"         | "DE"      | "FREE"
        "015376"         | "DE"      | "FREE"
        "015377"         | "DE"      | "FREE"
        "015378"         | "DE"      | "FREE"
        "015379"         | "DE"      | "FREE"
        "015380"         | "DE"      | "FREE"
        "015381"         | "DE"      | "FREE"
        "015382"         | "DE"      | "FREE"
        "015383"         | "DE"      | "FREE"
        "015384"         | "DE"      | "FREE"
        "015385"         | "DE"      | "FREE"
        "015386"         | "DE"      | "FREE"
        "015387"         | "DE"      | "FREE"
        "015388"         | "DE"      | "FREE"
        "015389"         | "DE"      | "FREE"
        "015390"         | "DE"      | "FREE"
        "015391"         | "DE"      | "FREE"
        "015392"         | "DE"      | "FREE"
        "015393"         | "DE"      | "FREE"
        "015394"         | "DE"      | "FREE"
        "015395"         | "DE"      | "FREE"
        "015396"         | "DE"      | "FREE"
        "015397"         | "DE"      | "FREE"
        "015398"         | "DE"      | "FREE"
        "015399"         | "DE"      | "FREE"
        //
        // 0154
        //
        // 015400 is reserved for voicemail - see tests below
        "015401"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015402"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015403"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015404"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015405"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015406"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015407"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015408"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015409"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015410"         | "DE"      | "FREE"
        "015411"         | "DE"      | "FREE"
        "015412"         | "DE"      | "FREE"
        "015413"         | "DE"      | "FREE"
        "015414"         | "DE"      | "FREE"
        "015415"         | "DE"      | "FREE"
        "015416"         | "DE"      | "FREE"
        "015417"         | "DE"      | "FREE"
        "015418"         | "DE"      | "FREE"
        "015419"         | "DE"      | "FREE"
        "015420"         | "DE"      | "FREE"
        "015421"         | "DE"      | "FREE"
        "015422"         | "DE"      | "FREE"
        "015423"         | "DE"      | "FREE"
        "015424"         | "DE"      | "FREE"
        "015425"         | "DE"      | "FREE"
        "015426"         | "DE"      | "FREE"
        "015427"         | "DE"      | "FREE"
        "015428"         | "DE"      | "FREE"
        "015429"         | "DE"      | "FREE"
        "015430"         | "DE"      | "FREE"
        "015431"         | "DE"      | "FREE"
        "015432"         | "DE"      | "FREE"
        "015433"         | "DE"      | "FREE"
        "015434"         | "DE"      | "FREE"
        "015435"         | "DE"      | "FREE"
        "015436"         | "DE"      | "FREE"
        "015437"         | "DE"      | "FREE"
        "015438"         | "DE"      | "FREE"
        "015439"         | "DE"      | "FREE"
        "015440"         | "DE"      | "FREE"
        "015441"         | "DE"      | "FREE"
        "015442"         | "DE"      | "FREE"
        "015443"         | "DE"      | "FREE"
        "015444"         | "DE"      | "FREE"
        "015445"         | "DE"      | "FREE"
        "015446"         | "DE"      | "FREE"
        "015447"         | "DE"      | "FREE"
        "015448"         | "DE"      | "FREE"
        "015449"         | "DE"      | "FREE"
        "015450"         | "DE"      | "FREE"
        "015451"         | "DE"      | "FREE"
        "015452"         | "DE"      | "FREE"
        "015453"         | "DE"      | "FREE"
        "015454"         | "DE"      | "FREE"
        "015455"         | "DE"      | "FREE"
        "015456"         | "DE"      | "FREE"
        "015457"         | "DE"      | "FREE"
        "015458"         | "DE"      | "FREE"
        "015459"         | "DE"      | "FREE"
        "015460"         | "DE"      | "FREE"
        "015461"         | "DE"      | "FREE"
        "015462"         | "DE"      | "FREE"
        "015463"         | "DE"      | "FREE"
        "015464"         | "DE"      | "FREE"
        "015465"         | "DE"      | "FREE"
        "015466"         | "DE"      | "FREE"
        "015467"         | "DE"      | "FREE"
        "015468"         | "DE"      | "FREE"
        "015469"         | "DE"      | "FREE"
        "015470"         | "DE"      | "FREE"
        "015471"         | "DE"      | "FREE"
        "015472"         | "DE"      | "FREE"
        "015473"         | "DE"      | "FREE"
        "015474"         | "DE"      | "FREE"
        "015475"         | "DE"      | "FREE"
        "015476"         | "DE"      | "FREE"
        "015477"         | "DE"      | "FREE"
        "015478"         | "DE"      | "FREE"
        "015479"         | "DE"      | "FREE"
        "015480"         | "DE"      | "FREE"
        "015481"         | "DE"      | "FREE"
        "015482"         | "DE"      | "FREE"
        "015483"         | "DE"      | "FREE"
        "015484"         | "DE"      | "FREE"
        "015485"         | "DE"      | "FREE"
        "015486"         | "DE"      | "FREE"
        "015487"         | "DE"      | "FREE"
        "015488"         | "DE"      | "FREE"
        "015489"         | "DE"      | "FREE"
        "015490"         | "DE"      | "FREE"
        "015491"         | "DE"      | "FREE"
        "015492"         | "DE"      | "FREE"
        "015493"         | "DE"      | "FREE"
        "015494"         | "DE"      | "FREE"
        "015495"         | "DE"      | "FREE"
        "015496"         | "DE"      | "FREE"
        "015497"         | "DE"      | "FREE"
        "015498"         | "DE"      | "FREE"
        "015499"         | "DE"      | "FREE"
        //
        // 0155
        //
        // 015500 is reserved for voicemail - see tests below
        "015501"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015502"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015503"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015504"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015505"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015506"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015507"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015508"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015509"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015510"         | "DE"      | "ASSIGNED"
        "015511"         | "DE"      | "ASSIGNED"
        "015512"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015513"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015514"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015515"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015516"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015517"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015518"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015519"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015520"         | "DE"      | "FREE"
        "015521"         | "DE"      | "FREE"
        "015522"         | "DE"      | "FREE"
        "015523"         | "DE"      | "FREE"
        "015524"         | "DE"      | "FREE"
        "015525"         | "DE"      | "FREE"
        "015526"         | "DE"      | "FREE"
        "015527"         | "DE"      | "FREE"
        "015528"         | "DE"      | "FREE"
        "015529"         | "DE"      | "FREE"
        "015530"         | "DE"      | "FREE"
        "015531"         | "DE"      | "FREE"
        "015532"         | "DE"      | "FREE"
        "015533"         | "DE"      | "FREE"
        "015534"         | "DE"      | "FREE"
        "015535"         | "DE"      | "FREE"
        "015536"         | "DE"      | "FREE"
        "015537"         | "DE"      | "FREE"
        "015538"         | "DE"      | "FREE"
        "015539"         | "DE"      | "FREE"
        "015540"         | "DE"      | "FREE"
        "015541"         | "DE"      | "FREE"
        "015542"         | "DE"      | "FREE"
        "015543"         | "DE"      | "FREE"
        "015544"         | "DE"      | "FREE"
        "015545"         | "DE"      | "FREE"
        "015546"         | "DE"      | "FREE"
        "015547"         | "DE"      | "FREE"
        "015548"         | "DE"      | "FREE"
        "015549"         | "DE"      | "FREE"
        "015550"         | "DE"      | "ASSIGNED"
        "015551"         | "DE"      | "ASSIGNED"
        "015552"         | "DE"      | "ASSIGNED"
        "015553"         | "DE"      | "ASSIGNED"
        "015554"         | "DE"      | "ASSIGNED"
        "015555"         | "DE"      | "ASSIGNED"
        "015556"         | "DE"      | "ASSIGNED"
        "015557"         | "DE"      | "ASSIGNED"
        "015558"         | "DE"      | "ASSIGNED"
        "015559"         | "DE"      | "ASSIGNED"
        "015560"         | "DE"      | "ASSIGNED"
        "015561"         | "DE"      | "ASSIGNED"
        "015562"         | "DE"      | "ASSIGNED"
        "015563"         | "DE"      | "ASSIGNED"
        "015564"         | "DE"      | "ASSIGNED"
        "015565"         | "DE"      | "ASSIGNED"
        "015566"         | "DE"      | "ASSIGNED"
        "015567"         | "DE"      | "ASSIGNED"
        "015568"         | "DE"      | "ASSIGNED"
        "015569"         | "DE"      | "ASSIGNED"
        "015570"         | "DE"      | "FREE"
        "015571"         | "DE"      | "FREE"
        "015572"         | "DE"      | "FREE"
        "015573"         | "DE"      | "FREE"
        "015574"         | "DE"      | "FREE"
        "015575"         | "DE"      | "FREE"
        "015576"         | "DE"      | "FREE"
        "015577"         | "DE"      | "FREE"
        "015578"         | "DE"      | "FREE"
        "015579"         | "DE"      | "FREE"
        "015580"         | "DE"      | "FREE"
        "015581"         | "DE"      | "FREE"
        "015582"         | "DE"      | "FREE"
        "015583"         | "DE"      | "FREE"
        "015584"         | "DE"      | "FREE"
        "015585"         | "DE"      | "FREE"
        "015586"         | "DE"      | "FREE"
        "015587"         | "DE"      | "FREE"
        "015588"         | "DE"      | "FREE"
        "015589"         | "DE"      | "FREE"
        "015590"         | "DE"      | "FREE"
        "015591"         | "DE"      | "FREE"
        "015592"         | "DE"      | "FREE"
        "015593"         | "DE"      | "FREE"
        "015594"         | "DE"      | "FREE"
        "015595"         | "DE"      | "FREE"
        "015596"         | "DE"      | "FREE"
        "015597"         | "DE"      | "FREE"
        "015598"         | "DE"      | "FREE"
        "015599"         | "DE"      | "FREE"
        //
        // 0156
        //
        // 015600 is reserved for voicemail - see tests below
        "015601"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015602"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015603"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015604"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015605"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015606"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015607"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015608"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015609"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015610"         | "DE"      | "FREE"
        "015611"         | "DE"      | "FREE"
        "015612"         | "DE"      | "FREE"
        "015613"         | "DE"      | "FREE"
        "015614"         | "DE"      | "FREE"
        "015615"         | "DE"      | "FREE"
        "015616"         | "DE"      | "FREE"
        "015617"         | "DE"      | "FREE"
        "015618"         | "DE"      | "FREE"
        "015619"         | "DE"      | "FREE"
        "015620"         | "DE"      | "FREE"
        "015621"         | "DE"      | "FREE"
        "015622"         | "DE"      | "FREE"
        "015623"         | "DE"      | "FREE"
        "015624"         | "DE"      | "FREE"
        "015625"         | "DE"      | "FREE"
        "015626"         | "DE"      | "FREE"
        "015627"         | "DE"      | "FREE"
        "015628"         | "DE"      | "FREE"
        "015629"         | "DE"      | "FREE"
        "015630"         | "DE"      | "ASSIGNED"
        "015631"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015632"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015633"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015634"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015635"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015636"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015637"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015638"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015639"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015640"         | "DE"      | "FREE"
        "015641"         | "DE"      | "FREE"
        "015642"         | "DE"      | "FREE"
        "015643"         | "DE"      | "FREE"
        "015644"         | "DE"      | "FREE"
        "015645"         | "DE"      | "FREE"
        "015646"         | "DE"      | "FREE"
        "015647"         | "DE"      | "FREE"
        "015648"         | "DE"      | "FREE"
        "015649"         | "DE"      | "FREE"
        "015650"         | "DE"      | "FREE"
        "015651"         | "DE"      | "FREE"
        "015652"         | "DE"      | "FREE"
        "015653"         | "DE"      | "FREE"
        "015654"         | "DE"      | "FREE"
        "015655"         | "DE"      | "FREE"
        "015656"         | "DE"      | "FREE"
        "015657"         | "DE"      | "FREE"
        "015658"         | "DE"      | "FREE"
        "015659"         | "DE"      | "FREE"
        "015660"         | "DE"      | "FREE"
        "015661"         | "DE"      | "FREE"
        "015662"         | "DE"      | "FREE"
        "015663"         | "DE"      | "FREE"
        "015664"         | "DE"      | "FREE"
        "015665"         | "DE"      | "FREE"
        "015666"         | "DE"      | "FREE"
        "015667"         | "DE"      | "FREE"
        "015668"         | "DE"      | "FREE"
        "015669"         | "DE"      | "FREE"
        "015670"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015671"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015672"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015673"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015674"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015675"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015676"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015677"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015678"         | "DE"      | "ASSIGNED"
        "015679"         | "DE"      | "ASSIGNED"
        "015680"         | "DE"      | "FREE"
        "015681"         | "DE"      | "FREE"
        "015682"         | "DE"      | "FREE"
        "015683"         | "DE"      | "FREE"
        "015684"         | "DE"      | "FREE"
        "015685"         | "DE"      | "FREE"
        "015686"         | "DE"      | "FREE"
        "015687"         | "DE"      | "FREE"
        "015688"         | "DE"      | "FREE"
        "015689"         | "DE"      | "FREE"
        "015690"         | "DE"      | "FREE"
        "015691"         | "DE"      | "FREE"
        "015692"         | "DE"      | "FREE"
        "015693"         | "DE"      | "FREE"
        "015694"         | "DE"      | "FREE"
        "015695"         | "DE"      | "FREE"
        "015696"         | "DE"      | "FREE"
        "015697"         | "DE"      | "FREE"
        "015698"         | "DE"      | "FREE"
        "015699"         | "DE"      | "FREE"
        //
        // 0157
        //
        "015700"         | "DE"      | "ASSIGNED"
        "015701"         | "DE"      | "ASSIGNED"
        "015702"         | "DE"      | "ASSIGNED"
        "015703"         | "DE"      | "ASSIGNED"
        "015704"         | "DE"      | "ASSIGNED"
        "015705"         | "DE"      | "RESERVED"
        "015706"         | "DE"      | "ASSIGNED"
        "015707"         | "DE"      | "RESERVED"
        "015708"         | "DE"      | "RESERVED"
        "0157090"        | "DE"      | "RESERVED"
        "0157091"        | "DE"      | "RESERVED"
        "0157092"        | "DE"      | "RESERVED"
        "0157093"        | "DE"      | "RESERVED"
        "0157094"        | "DE"      | "RESERVED"
        "0157095"        | "DE"      | "RESERVED"
        "0157096"        | "DE"      | "RESERVED"
        "0157097"        | "DE"      | "RESERVED"
        "0157098"        | "DE"      | "RESERVED"
        // 0157099 is reserved for voicemail - see tests below
        "015710"         | "DE"      | "RESERVED"
        "015711"         | "DE"      | "RESERVED"
        "015712"         | "DE"      | "RESERVED"
        "015713"         | "DE"      | "RESERVED"
        "015714"         | "DE"      | "RESERVED"
        "015715"         | "DE"      | "RESERVED"
        "015716"         | "DE"      | "RESERVED"
        "015717"         | "DE"      | "RESERVED"
        "015718"         | "DE"      | "RESERVED"
        "0157190"        | "DE"      | "RESERVED"
        "0157191"        | "DE"      | "RESERVED"
        "0157192"        | "DE"      | "RESERVED"
        "0157193"        | "DE"      | "RESERVED"
        "0157194"        | "DE"      | "RESERVED"
        "0157195"        | "DE"      | "RESERVED"
        "0157196"        | "DE"      | "RESERVED"
        "0157197"        | "DE"      | "RESERVED"
        "0157198"        | "DE"      | "RESERVED"
        // 0157199 is reserved for voicemail - see tests below
        "015720"         | "DE"      | "RESERVED"
        "015721"         | "DE"      | "RESERVED"
        "015722"         | "DE"      | "RESERVED"
        "015723"         | "DE"      | "RESERVED"
        "015724"         | "DE"      | "RESERVED"
        "015725"         | "DE"      | "RESERVED"
        "015726"         | "DE"      | "RESERVED"
        "015727"         | "DE"      | "RESERVED"
        "015728"         | "DE"      | "RESERVED"
        "0157290"        | "DE"      | "RESERVED"
        "0157291"        | "DE"      | "RESERVED"
        "0157292"        | "DE"      | "RESERVED"
        "0157293"        | "DE"      | "RESERVED"
        "0157294"        | "DE"      | "RESERVED"
        "0157295"        | "DE"      | "RESERVED"
        "0157296"        | "DE"      | "RESERVED"
        "0157297"        | "DE"      | "RESERVED"
        "0157298"        | "DE"      | "RESERVED"
        // 0157299 is reserved for voicemail - see tests below
        "015730"         | "DE"      | "ASSIGNED"
        "015731"         | "DE"      | "ASSIGNED"
        "015732"         | "DE"      | "ASSIGNED"
        "015733"         | "DE"      | "ASSIGNED"
        "015734"         | "DE"      | "ASSIGNED"
        "015735"         | "DE"      | "ASSIGNED"
        "015736"         | "DE"      | "ASSIGNED"
        "015737"         | "DE"      | "ASSIGNED"
        "015738"         | "DE"      | "ASSIGNED"
        "0157390"        | "DE"      | "ASSIGNED"
        "0157391"        | "DE"      | "ASSIGNED"
        "0157392"        | "DE"      | "ASSIGNED"
        "0157393"        | "DE"      | "ASSIGNED"
        "0157394"        | "DE"      | "ASSIGNED"
        "0157395"        | "DE"      | "ASSIGNED"
        "0157396"        | "DE"      | "ASSIGNED"
        "0157397"        | "DE"      | "ASSIGNED"
        "0157398"        | "DE"      | "ASSIGNED"
        // 0157399 is reserved for voicemail - see tests below
        "015740"         | "DE"      | "RESERVED"
        "015741"         | "DE"      | "RESERVED"
        "015742"         | "DE"      | "RESERVED"
        "015743"         | "DE"      | "RESERVED"
        "015744"         | "DE"      | "RESERVED"
        "015745"         | "DE"      | "RESERVED"
        "015746"         | "DE"      | "RESERVED"
        "015747"         | "DE"      | "RESERVED"
        "015748"         | "DE"      | "RESERVED"
        "0157490"        | "DE"      | "RESERVED"
        "0157491"        | "DE"      | "RESERVED"
        "0157492"        | "DE"      | "RESERVED"
        "0157493"        | "DE"      | "RESERVED"
        "0157494"        | "DE"      | "RESERVED"
        "0157495"        | "DE"      | "RESERVED"
        "0157496"        | "DE"      | "RESERVED"
        "0157497"        | "DE"      | "RESERVED"
        "0157498"        | "DE"      | "RESERVED"
        // 0157499 is reserved for voicemail - see tests below
        "015750"         | "DE"      | "ASSIGNED"
        "015751"         | "DE"      | "ASSIGNED"
        "015752"         | "DE"      | "ASSIGNED"
        "015753"         | "DE"      | "ASSIGNED"
        "015754"         | "DE"      | "ASSIGNED"
        "015755"         | "DE"      | "ASSIGNED"
        "015756"         | "DE"      | "ASSIGNED"
        "015757"         | "DE"      | "ASSIGNED"
        "015758"         | "DE"      | "ASSIGNED"
        "0157590"        | "DE"      | "ASSIGNED"
        "0157591"        | "DE"      | "ASSIGNED"
        "0157592"        | "DE"      | "ASSIGNED"
        "0157593"        | "DE"      | "ASSIGNED"
        "0157594"        | "DE"      | "ASSIGNED"
        "0157595"        | "DE"      | "ASSIGNED"
        "0157596"        | "DE"      | "ASSIGNED"
        "0157597"        | "DE"      | "ASSIGNED"
        "0157598"        | "DE"      | "ASSIGNED"
        // 0157599 is reserved for voicemail - see tests below
        "015760"         | "DE"      | "RESERVED"
        "015761"         | "DE"      | "RESERVED"
        "015762"         | "DE"      | "RESERVED"
        "015763"         | "DE"      | "RESERVED"
        "015764"         | "DE"      | "RESERVED"
        "015765"         | "DE"      | "RESERVED"
        "015766"         | "DE"      | "RESERVED"
        "015767"         | "DE"      | "RESERVED"
        "015768"         | "DE"      | "RESERVED"
        "0157690"        | "DE"      | "RESERVED"
        "0157691"        | "DE"      | "RESERVED"
        "0157692"        | "DE"      | "RESERVED"
        "0157693"        | "DE"      | "RESERVED"
        "0157694"        | "DE"      | "RESERVED"
        "0157695"        | "DE"      | "RESERVED"
        "0157696"        | "DE"      | "RESERVED"
        "0157697"        | "DE"      | "RESERVED"
        "0157698"        | "DE"      | "RESERVED"
        // 0157699 is reserved for voicemail - see tests below
        "015770"         | "DE"      | "ASSIGNED"
        "015771"         | "DE"      | "ASSIGNED"
        "015772"         | "DE"      | "ASSIGNED"
        "015773"         | "DE"      | "ASSIGNED"
        "015774"         | "DE"      | "ASSIGNED"
        "015775"         | "DE"      | "ASSIGNED"
        "015776"         | "DE"      | "ASSIGNED"
        "015777"         | "DE"      | "ASSIGNED"
        "015778"         | "DE"      | "ASSIGNED"
        "0157790"        | "DE"      | "ASSIGNED"
        "0157791"        | "DE"      | "ASSIGNED"
        "0157792"        | "DE"      | "ASSIGNED"
        "0157793"        | "DE"      | "ASSIGNED"
        "0157794"        | "DE"      | "ASSIGNED"
        "0157795"        | "DE"      | "ASSIGNED"
        "0157796"        | "DE"      | "ASSIGNED"
        "0157797"        | "DE"      | "ASSIGNED"
        "0157798"        | "DE"      | "ASSIGNED"
        // 0157799 is reserved for voicemail - see tests below
        "015780"         | "DE"      | "ASSIGNED"
        "015781"         | "DE"      | "ASSIGNED"
        "015782"         | "DE"      | "ASSIGNED"
        "015783"         | "DE"      | "ASSIGNED"
        "015784"         | "DE"      | "ASSIGNED"
        "015785"         | "DE"      | "ASSIGNED"
        "015786"         | "DE"      | "ASSIGNED"
        "015787"         | "DE"      | "ASSIGNED"
        "015788"         | "DE"      | "ASSIGNED"
        "0157890"        | "DE"      | "ASSIGNED"
        "0157891"        | "DE"      | "ASSIGNED"
        "0157892"        | "DE"      | "ASSIGNED"
        "0157893"        | "DE"      | "ASSIGNED"
        "0157894"        | "DE"      | "ASSIGNED"
        "0157895"        | "DE"      | "ASSIGNED"
        "0157896"        | "DE"      | "ASSIGNED"
        "0157897"        | "DE"      | "ASSIGNED"
        "0157898"        | "DE"      | "ASSIGNED"
        // 0157899 is reserved for voicemail - see tests below
        "015790"         | "DE"      | "ASSIGNED"
        "015791"         | "DE"      | "ASSIGNED"
        "015792"         | "DE"      | "ASSIGNED"
        "015793"         | "DE"      | "ASSIGNED"
        "015794"         | "DE"      | "ASSIGNED"
        "015795"         | "DE"      | "ASSIGNED"
        "015796"         | "DE"      | "ASSIGNED"
        "015797"         | "DE"      | "ASSIGNED"
        "015798"         | "DE"      | "ASSIGNED"
        "0157990"        | "DE"      | "ASSIGNED"
        "0157991"        | "DE"      | "ASSIGNED"
        "0157992"        | "DE"      | "ASSIGNED"
        "0157993"        | "DE"      | "ASSIGNED"
        "0157994"        | "DE"      | "ASSIGNED"
        "0157995"        | "DE"      | "ASSIGNED"
        "0157996"        | "DE"      | "ASSIGNED"
        "0157997"        | "DE"      | "ASSIGNED"
        "0157998"        | "DE"      | "ASSIGNED"
        // 0157999 is reserved for voicemail - see tests below
        //
        // 0158
        //
        // 015800 is reserved for voicemail - see tests below
        "015801"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015802"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015803"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015804"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015805"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015806"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015807"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015808"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015809"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015810"         | "DE"      | "FREE"
        "015811"         | "DE"      | "FREE"
        "015812"         | "DE"      | "FREE"
        "015813"         | "DE"      | "FREE"
        "015814"         | "DE"      | "FREE"
        "015815"         | "DE"      | "FREE"
        "015816"         | "DE"      | "FREE"
        "015817"         | "DE"      | "FREE"
        "015818"         | "DE"      | "FREE"
        "015819"         | "DE"      | "FREE"
        "015820"         | "DE"      | "FREE"
        "015821"         | "DE"      | "FREE"
        "015822"         | "DE"      | "FREE"
        "015823"         | "DE"      | "FREE"
        "015824"         | "DE"      | "FREE"
        "015825"         | "DE"      | "FREE"
        "015826"         | "DE"      | "FREE"
        "015827"         | "DE"      | "FREE"
        "015828"         | "DE"      | "FREE"
        "015829"         | "DE"      | "FREE"
        "015830"         | "DE"      | "FREE"
        "015831"         | "DE"      | "FREE"
        "015832"         | "DE"      | "FREE"
        "015833"         | "DE"      | "FREE"
        "015834"         | "DE"      | "FREE"
        "015835"         | "DE"      | "FREE"
        "015836"         | "DE"      | "FREE"
        "015837"         | "DE"      | "FREE"
        "015838"         | "DE"      | "FREE"
        "015839"         | "DE"      | "FREE"
        "015840"         | "DE"      | "FREE"
        "015841"         | "DE"      | "FREE"
        "015842"         | "DE"      | "FREE"
        "015843"         | "DE"      | "FREE"
        "015844"         | "DE"      | "FREE"
        "015845"         | "DE"      | "FREE"
        "015846"         | "DE"      | "FREE"
        "015847"         | "DE"      | "FREE"
        "015848"         | "DE"      | "FREE"
        "015849"         | "DE"      | "FREE"
        "015850"         | "DE"      | "FREE"
        "015851"         | "DE"      | "FREE"
        "015852"         | "DE"      | "FREE"
        "015853"         | "DE"      | "FREE"
        "015854"         | "DE"      | "FREE"
        "015855"         | "DE"      | "FREE"
        "015856"         | "DE"      | "FREE"
        "015857"         | "DE"      | "FREE"
        "015858"         | "DE"      | "FREE"
        "015859"         | "DE"      | "FREE"
        "015860"         | "DE"      | "FREE"
        "015861"         | "DE"      | "FREE"
        "015862"         | "DE"      | "FREE"
        "015863"         | "DE"      | "FREE"
        "015864"         | "DE"      | "FREE"
        "015865"         | "DE"      | "FREE"
        "015866"         | "DE"      | "FREE"
        "015867"         | "DE"      | "FREE"
        "015868"         | "DE"      | "FREE"
        "015879"         | "DE"      | "FREE"
        "015880"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015881"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015882"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015883"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015884"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015885"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015886"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015887"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015888"         | "DE"      | "ASSIGNED"
        "015889"         | "DE"      | "NOT_FURTHER_SPECIFIED"
        "015890"         | "DE"      | "FREE"
        "015891"         | "DE"      | "FREE"
        "015892"         | "DE"      | "FREE"
        "015893"         | "DE"      | "FREE"
        "015894"         | "DE"      | "FREE"
        "015895"         | "DE"      | "FREE"
        "015896"         | "DE"      | "FREE"
        "015897"         | "DE"      | "FREE"
        "015898"         | "DE"      | "FREE"
        "015899"         | "DE"      | "FREE"
        //
        // 0159
        //
        "015900"         | "DE"      | "ASSIGNED"
        "015901"         | "DE"      | "ASSIGNED"
        "015902"         | "DE"      | "ASSIGNED"
        "0159030"        | "DE"      | "ASSIGNED"
        "0159031"        | "DE"      | "ASSIGNED"
        "0159032"        | "DE"      | "ASSIGNED"
        // 0159033 is reserved for voicemail - see tests below
        "0159034"        | "DE"      | "ASSIGNED"
        "0159035"        | "DE"      | "ASSIGNED"
        "0159036"        | "DE"      | "ASSIGNED"
        "0159037"        | "DE"      | "ASSIGNED"
        "0159038"        | "DE"      | "ASSIGNED"
        "0159039"        | "DE"      | "ASSIGNED"
        "015904"         | "DE"      | "ASSIGNED"
        "015905"         | "DE"      | "ASSIGNED"
        "015906"         | "DE"      | "ASSIGNED"
        "015907"         | "DE"      | "ASSIGNED"
        "015908"         | "DE"      | "ASSIGNED"
        "015909"         | "DE"      | "ASSIGNED"

        "015910"         | "DE"      | "RESERVED"
        "015911"         | "DE"      | "RESERVED"
        "015912"         | "DE"      | "RESERVED"
        "0159130"        | "DE"      | "RESERVED"
        "0159131"        | "DE"      | "RESERVED"
        "0159132"        | "DE"      | "RESERVED"
        // 0159133 is reserved for voicemail - see tests below
        "0159134"        | "DE"      | "RESERVED"
        "0159135"        | "DE"      | "RESERVED"
        "0159136"        | "DE"      | "RESERVED"
        "0159137"        | "DE"      | "RESERVED"
        "0159138"        | "DE"      | "RESERVED"
        "0159139"        | "DE"      | "RESERVED"
        "015914"         | "DE"      | "RESERVED"
        "015915"         | "DE"      | "RESERVED"
        "015916"         | "DE"      | "RESERVED"
        "015917"         | "DE"      | "RESERVED"
        "015918"         | "DE"      | "RESERVED"
        "015919"         | "DE"      | "RESERVED"

        "015920"         | "DE"      | "RESERVED"
        "015921"         | "DE"      | "RESERVED"
        "015922"         | "DE"      | "RESERVED"
        "0159230"        | "DE"      | "RESERVED"
        "0159231"        | "DE"      | "RESERVED"
        "0159232"        | "DE"      | "RESERVED"
        // 0159233 is reserved for voicemail - see tests below
        "0159234"        | "DE"      | "RESERVED"
        "0159235"        | "DE"      | "RESERVED"
        "0159236"        | "DE"      | "RESERVED"
        "0159237"        | "DE"      | "RESERVED"
        "0159238"        | "DE"      | "RESERVED"
        "0159239"        | "DE"      | "RESERVED"
        "015924"         | "DE"      | "RESERVED"
        "015925"         | "DE"      | "RESERVED"
        "015926"         | "DE"      | "RESERVED"
        "015927"         | "DE"      | "RESERVED"
        "015928"         | "DE"      | "RESERVED"
        "015929"         | "DE"      | "RESERVED"

        "015930"         | "DE"      | "RESERVED"
        "015931"         | "DE"      | "RESERVED"
        "015932"         | "DE"      | "RESERVED"
        "0159330"        | "DE"      | "RESERVED"
        "0159331"        | "DE"      | "RESERVED"
        "0159332"        | "DE"      | "RESERVED"
        // 0159333 is reserved for voicemail - see tests below
        "0159334"        | "DE"      | "RESERVED"
        "0159335"        | "DE"      | "RESERVED"
        "0159336"        | "DE"      | "RESERVED"
        "0159337"        | "DE"      | "RESERVED"
        "0159338"        | "DE"      | "RESERVED"
        "0159339"        | "DE"      | "RESERVED"
        "015934"         | "DE"      | "RESERVED"
        "015935"         | "DE"      | "RESERVED"
        "015936"         | "DE"      | "RESERVED"
        "015937"         | "DE"      | "RESERVED"
        "015938"         | "DE"      | "RESERVED"
        "015939"         | "DE"      | "RESERVED"

        "015940"         | "DE"      | "RESERVED"
        "015941"         | "DE"      | "RESERVED"
        "015942"         | "DE"      | "RESERVED"
        "0159430"        | "DE"      | "RESERVED"
        "0159431"        | "DE"      | "RESERVED"
        "0159432"        | "DE"      | "RESERVED"
        // 0159433 is reserved for voicemail - see tests below
        "0159434"        | "DE"      | "RESERVED"
        "0159435"        | "DE"      | "RESERVED"
        "0159436"        | "DE"      | "RESERVED"
        "0159437"        | "DE"      | "RESERVED"
        "0159438"        | "DE"      | "RESERVED"
        "0159439"        | "DE"      | "RESERVED"
        "015944"         | "DE"      | "RESERVED"
        "015945"         | "DE"      | "RESERVED"
        "015946"         | "DE"      | "RESERVED"
        "015947"         | "DE"      | "RESERVED"
        "015948"         | "DE"      | "RESERVED"
        "015949"         | "DE"      | "RESERVED"

        "015950"         | "DE"      | "RESERVED"
        "015951"         | "DE"      | "RESERVED"
        "015952"         | "DE"      | "RESERVED"
        "0159530"        | "DE"      | "RESERVED"
        "0159531"        | "DE"      | "RESERVED"
        "0159532"        | "DE"      | "RESERVED"
        // 0159533 is reserved for voicemail - see tests below
        "0159534"        | "DE"      | "RESERVED"
        "0159535"        | "DE"      | "RESERVED"
        "0159536"        | "DE"      | "RESERVED"
        "0159537"        | "DE"      | "RESERVED"
        "0159538"        | "DE"      | "RESERVED"
        "0159539"        | "DE"      | "RESERVED"
        "015954"         | "DE"      | "RESERVED"
        "015955"         | "DE"      | "RESERVED"
        "015956"         | "DE"      | "RESERVED"
        "015957"         | "DE"      | "RESERVED"
        "015958"         | "DE"      | "RESERVED"
        "015959"         | "DE"      | "RESERVED"

        "015960"         | "DE"      | "RESERVED"
        "015961"         | "DE"      | "RESERVED"
        "015962"         | "DE"      | "RESERVED"
        "0159630"        | "DE"      | "RESERVED"
        "0159631"        | "DE"      | "RESERVED"
        "0159632"        | "DE"      | "RESERVED"
        // 0159633 is reserved for voicemail - see tests below
        "0159634"        | "DE"      | "RESERVED"
        "0159635"        | "DE"      | "RESERVED"
        "0159636"        | "DE"      | "RESERVED"
        "0159637"        | "DE"      | "RESERVED"
        "0159638"        | "DE"      | "RESERVED"
        "0159639"        | "DE"      | "RESERVED"
        "015964"         | "DE"      | "RESERVED"
        "015965"         | "DE"      | "RESERVED"
        "015966"         | "DE"      | "RESERVED"
        "015967"         | "DE"      | "RESERVED"
        "015968"         | "DE"      | "RESERVED"
        "015969"         | "DE"      | "RESERVED"

        "015970"         | "DE"      | "RESERVED"
        "015971"         | "DE"      | "RESERVED"
        "015972"         | "DE"      | "RESERVED"
        "0159730"        | "DE"      | "RESERVED"
        "0159731"        | "DE"      | "RESERVED"
        "0159732"        | "DE"      | "RESERVED"
        // 0159733 is reserved for voicemail - see tests below
        "0159734"        | "DE"      | "RESERVED"
        "0159735"        | "DE"      | "RESERVED"
        "0159736"        | "DE"      | "RESERVED"
        "0159737"        | "DE"      | "RESERVED"
        "0159738"        | "DE"      | "RESERVED"
        "0159739"        | "DE"      | "RESERVED"
        "015974"         | "DE"      | "RESERVED"
        "015975"         | "DE"      | "RESERVED"
        "015976"         | "DE"      | "RESERVED"
        "015977"         | "DE"      | "RESERVED"
        "015978"         | "DE"      | "RESERVED"
        "015979"         | "DE"      | "RESERVED"

        "015980"         | "DE"      | "RESERVED"
        "015981"         | "DE"      | "RESERVED"
        "015982"         | "DE"      | "RESERVED"
        "0159830"        | "DE"      | "RESERVED"
        "0159831"        | "DE"      | "RESERVED"
        "0159832"        | "DE"      | "RESERVED"
        // 0159833 is reserved for voicemail - see tests below
        "0159834"        | "DE"      | "RESERVED"
        "0159835"        | "DE"      | "RESERVED"
        "0159836"        | "DE"      | "RESERVED"
        "0159837"        | "DE"      | "RESERVED"
        "0159838"        | "DE"      | "RESERVED"
        "0159839"        | "DE"      | "RESERVED"
        "015984"         | "DE"      | "RESERVED"
        "015985"         | "DE"      | "RESERVED"
        "015986"         | "DE"      | "RESERVED"
        "015987"         | "DE"      | "RESERVED"
        "015988"         | "DE"      | "RESERVED"
        "015989"         | "DE"      | "RESERVED"

        "015990"         | "DE"      | "RESERVED"
        "015991"         | "DE"      | "RESERVED"
        "015992"         | "DE"      | "RESERVED"
        "0159930"        | "DE"      | "RESERVED"
        "0159931"        | "DE"      | "RESERVED"
        "0159932"        | "DE"      | "RESERVED"
        // 0159933 is reserved for voicemail - see tests below
        "0159934"        | "DE"      | "RESERVED"
        "0159935"        | "DE"      | "RESERVED"
        "0159936"        | "DE"      | "RESERVED"
        "0159937"        | "DE"      | "RESERVED"
        "0159938"        | "DE"      | "RESERVED"
        "0159939"        | "DE"      | "RESERVED"
        "015994"         | "DE"      | "RESERVED"
        "015995"         | "DE"      | "RESERVED"
        "015996"         | "DE"      | "RESERVED"
        "015997"         | "DE"      | "RESERVED"
        "015998"         | "DE"      | "RESERVED"
        "015999"         | "DE"      | "RESERVED"

        // end of 015xx 
    }

    def "validate German Mobile 15 range with voicemail infix"(String numberUntilInfix, regionCode, String expectedResultskey) {
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
        if (numberUntilInfix.length() == 8) {
            numbersToTest = [numberUntilInfix + "000",
                             numberUntilInfix + "0000",
                             numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "999",
                             numberUntilInfix + "9999",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "99999999"]
        }
        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG]


        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:

        numberUntilInfix | regionCode | expectedResultskey
        // There infixes of two digits used to address the voicemail of a line
        // see 2.5 in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // This makes the number two digits longer, but on the other hand a short version with the infix does not exists, that is the reason, why above range started at 15001, since 15000 would be an infix

        // 15-0-INFIX:OO-xx 3-Block: 0xx
        "015000"         | "DE"      | "TBD"

        // 15-1-INFIX:13-x(x) 2-Block: 1x and 3-Block: 1xx
        "015113"         | "DE"      | "TBD"

        // 15-2x-INFIX:50-(x) 2-Block: 2x and 3-Block: 2xx  First Infix: 50
        "0152050"        | "DE"      | "TBD"
        "0152150"        | "DE"      | "TBD"
        "0152250"        | "DE"      | "TBD"
        "0152350"        | "DE"      | "TBD"
        "0152450"        | "DE"      | "TBD"
        "0152550"        | "DE"      | "TBD"
        "0152650"        | "DE"      | "TBD"
        "0152750"        | "DE"      | "TBD"
        "0152850"        | "DE"      | "TBD"
        "0152950"        | "DE"      | "TBD"
        // 15-2x-INFIX:55-(x) 2-Block: 2x and 3-Block: 2xx  Second Infix: 55
        "0152055"        | "DE"      | "TBD"
        "0152155"        | "DE"      | "TBD"
        "0152255"        | "DE"      | "TBD"
        "0152355"        | "DE"      | "TBD"
        "0152455"        | "DE"      | "TBD"
        "0152555"        | "DE"      | "TBD"
        "0152655"        | "DE"      | "TBD"
        "0152755"        | "DE"      | "TBD"
        "0152855"        | "DE"      | "TBD"
        "0152955"        | "DE"      | "TBD"

        // 15-3-INFIX:OO-xx 3-Block: 3xx
        "015300"         | "DE"      | "TBD"

        // 15-4-INFIX:OO-xx 3-Block: 4xx
        "015400"         | "DE"      | "TBD"

        // 15-5-INFIX:OO-xx 3-Block: 5xx
        "015500"         | "DE"      | "TBD"

        // 15-6-INFIX:OO-xx 3-Block: 6xx
        "015600"         | "DE"      | "TBD"

        // 15-7x-INFIX:99-(x) 2-Block: 7x and 3-Block: 7xx
        "0157099"        | "DE"      | "TBD"
        "0157199"        | "DE"      | "TBD"
        "0157299"        | "DE"      | "TBD"
        "0157399"        | "DE"      | "TBD"
        "0157499"        | "DE"      | "TBD"
        "0157599"        | "DE"      | "TBD"
        "0157699"        | "DE"      | "TBD"
        "0157799"        | "DE"      | "TBD"
        "0157899"        | "DE"      | "TBD"
        "0157999"        | "DE"      | "TBD"

        // 15-8-INFIX:OO-xx 3-Block: 8xx
        "015800"         | "DE"      | "TBD"

        // 15-9x-INFIX:33-(x) 2-Block: 9x and 3-Block: 9xx
        "0159033"        | "DE"      | "TBD"
        "0159133"        | "DE"      | "TBD"
        "0159233"        | "DE"      | "TBD"
        "0159333"        | "DE"      | "TBD"
        "0159433"        | "DE"      | "TBD"
        "0159533"        | "DE"      | "TBD"
        "0159633"        | "DE"      | "TBD"
        "0159733"        | "DE"      | "TBD"
        "0159833"        | "DE"      | "TBD"
        "0159933"        | "DE"      | "TBD"

        // end of 015xx for voicemail
    }

    def "validate German Mobile 16 range"(String numberUntilInfix, regionCode) {
        given:
        String[] numbersToTest

        if (numberUntilInfix.length() == 5) {
            numbersToTest = [numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "00000000",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999",
                             numberUntilInfix + "99999999"]
        }
        if (numberUntilInfix.length() == 6) {
            numbersToTest = [numberUntilInfix + "0000",
                             numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "9999",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999"]
        }

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG]

        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            assert results[i] == expectedResults[i]
        }


        where:
        numberUntilInfix | regionCode
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 016xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0160
        //
        "01600"          | "DE"
        "016010"         | "DE"
        "016011"         | "DE"
        "016012"         | "DE"
        // 016013 is reserved for voicemail - see tests below
        "016014"         | "DE"
        "016015"         | "DE"
        "016016"         | "DE"
        "016017"         | "DE"
        "016018"         | "DE"
        "016019"         | "DE"
        "01602"          | "DE"
        "01603"          | "DE"
        "01604"          | "DE"
        "01605"          | "DE"
        "01606"          | "DE"
        "01607"          | "DE"
        "01608"          | "DE"
        "01609"          | "DE"

        //
        // 0162
        //
        "01620"          | "DE"
        "01621"          | "DE"
        "01622"          | "DE"
        "01623"          | "DE"
        "01624"          | "DE"
        // 016250 is reserved for voicemail - see tests below
        "016251"         | "DE"
        "016252"         | "DE"
        "016253"         | "DE"
        "016254"         | "DE"
        // 016255 is reserved for voicemail - see tests below
        "016256"         | "DE"
        "016257"         | "DE"
        "016258"         | "DE"
        "016259"         | "DE"
        "01626"          | "DE"
        "01627"          | "DE"
        "01628"          | "DE"
        "01629"          | "DE"

        //
        // 0163
        //
        "01630"          | "DE"
        "01631"          | "DE"
        "01632"          | "DE"
        "01633"          | "DE"
        "01634"          | "DE"
        "01635"          | "DE"
        "01636"          | "DE"
        "01637"          | "DE"
        "01638"          | "DE"
        "016390"         | "DE"
        "016391"         | "DE"
        "016392"         | "DE"
        "016393"         | "DE"
        "016394"         | "DE"
        "016395"         | "DE"
        "016396"         | "DE"
        "016397"         | "DE"
        "016398"         | "DE"
        // 016399 is reserved for voicemail - see tests below
    }

    def "validate German Mobile 16 range with voicemail infix"(String numberUntilInfix, regionCode) {
        given:
        String[] numbersToTest = [numberUntilInfix + "000000",
                                  numberUntilInfix + "0000000",
                                  numberUntilInfix + "00000000",
                                  numberUntilInfix + "000000000",
                                  numberUntilInfix + "999999",
                                  numberUntilInfix + "9999999",
                                  numberUntilInfix + "99999999",
                                  numberUntilInfix + "999999999"]

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY,
                                                         PhoneNumberValidationResult.TOO_LONG]

        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            assert results[i] == expectedResults[i]
        }


        where:
        numberUntilInfix | regionCode
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 016xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0160
        //
        "016013"         | "DE"
        //
        // 0162
        //
        "016250"         | "DE"
        "016255"         | "DE"

        //
        // 0163
        //
        "016399"         | "DE"
    }

    def "validate German reserve 16 range"(String reserve, regionCode) {
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

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER]

        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:
        reserve          | regionCode
        // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        // 0161, 165, 166, 167 are reserved for future use

        "0161"           | "DE"
        "0165"           | "DE"
        "0166"           | "DE"
        "0167"           | "DE"

    }

    def "validate German 'Funkruf' 16(8/9) range"(String reserve, regionCode) {
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
                                  reserve + "2233445566",
                                  reserve + "22334455667",
                                  reserve + "223344556677"]


        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
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
        for (int i = 0; i < results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:
        reserve          | regionCode
        // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        // 0168, 169 are using a 14 digit national number (0164 is not further defined).
        // TODO: could 0164 needs to be covered
        "0168"           | "DE"
        "0169"           | "DE"

    }

    def "validate German Mobile 17 range"(String numberUntilInfix, regionCode) {
        given:
        String[] numbersToTest

        if (numberUntilInfix.length() == 5) {
            numbersToTest = [numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "00000000",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999",
                             numberUntilInfix + "99999999"]
        }
        if (numberUntilInfix.length() == 6) {
            numbersToTest = [numberUntilInfix + "0000",
                             numberUntilInfix + "00000",
                             numberUntilInfix + "000000",
                             numberUntilInfix + "0000000",
                             numberUntilInfix + "9999",
                             numberUntilInfix + "99999",
                             numberUntilInfix + "999999",
                             numberUntilInfix + "9999999"]
        }


        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html
        // 176 is only 11 digit rest 10

        // but https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/Nummernplan_MobileDienste.pdf?__blob=publicationFile&v=1 rules
        // 11 or 10 is possible on each 17x and depends on the operator to decide

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG]


        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:
        numberUntilInfix | regionCode
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits
        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html
        // x: 6 length 8 otherwise 7

        //
        // 0170
        //
        "01700"          | "DE"
        "017010"         | "DE"
        "017011"         | "DE"
        "017012"         | "DE"
        // 017013 is reserved for voicemail - see tests below
        "017014"         | "DE"
        "017015"         | "DE"
        "017016"         | "DE"
        "017017"         | "DE"
        "017018"         | "DE"
        "017019"         | "DE"
        "01702"          | "DE"
        "01703"          | "DE"
        "01704"          | "DE"
        "01705"          | "DE"
        "01706"          | "DE"
        "01707"          | "DE"
        "01708"          | "DE"
        "01709"          | "DE"

        //
        // 0171
        //
        "01710"          | "DE"
        "017110"         | "DE"
        "017111"         | "DE"
        "017112"         | "DE"
        // 017113 is reserved for voicemail - see tests below
        "017114"         | "DE"
        "017115"         | "DE"
        "017116"         | "DE"
        "017117"         | "DE"
        "017118"         | "DE"
        "017119"         | "DE"
        "01712"          | "DE"
        "01713"          | "DE"
        "01714"          | "DE"
        "01715"          | "DE"
        "01716"          | "DE"
        "01717"          | "DE"
        "01718"          | "DE"
        "01719"          | "DE"

        //
        // 0172
        //
        "01720"          | "DE"
        "01721"          | "DE"
        "01722"          | "DE"
        "01723"          | "DE"
        "01724"          | "DE"
        // 017250 is reserved for voicemail - see tests below
        "017251"         | "DE"
        "017252"         | "DE"
        "017253"         | "DE"
        "017254"         | "DE"
        // 017255 is reserved for voicemail - see tests below
        "017256"         | "DE"
        "017257"         | "DE"
        "017258"         | "DE"
        "017259"         | "DE"
        "01726"          | "DE"
        "01727"          | "DE"
        "01728"          | "DE"
        "01729"          | "DE"

        //
        // 0173
        //
        "01730"          | "DE"
        "01731"          | "DE"
        "01732"          | "DE"
        "01733"          | "DE"
        "01734"          | "DE"
        // 017350 is reserved for voicemail - see tests below
        "017351"         | "DE"
        "017352"         | "DE"
        "017353"         | "DE"
        "017354"         | "DE"
        // 017355 is reserved for voicemail - see tests below
        "017356"         | "DE"
        "017357"         | "DE"
        "017358"         | "DE"
        "017359"         | "DE"
        "01736"          | "DE"
        "01737"          | "DE"
        "01738"          | "DE"
        "01739"          | "DE"

        //
        // 0174
        //
        "01740"          | "DE"
        "01741"          | "DE"
        "01742"          | "DE"
        "01743"          | "DE"
        "01744"          | "DE"
        // 017450 is reserved for voicemail - see tests below
        "017451"         | "DE"
        "017452"         | "DE"
        "017453"         | "DE"
        "017454"         | "DE"
        // 017455 is reserved for voicemail - see tests below
        "017456"         | "DE"
        "017457"         | "DE"
        "017458"         | "DE"
        "017459"         | "DE"
        "01746"          | "DE"
        "01747"          | "DE"
        "01748"          | "DE"
        "01749"          | "DE"

        //
        // 0175
        //
        "01750"          | "DE"
        "017510"         | "DE"
        "017511"         | "DE"
        "017512"         | "DE"
        // 017513 is reserved for voicemail - see tests below
        "017514"         | "DE"
        "017515"         | "DE"
        "017516"         | "DE"
        "017517"         | "DE"
        "017518"         | "DE"
        "017519"         | "DE"
        "01752"          | "DE"
        "01753"          | "DE"
        "01754"          | "DE"
        "01755"          | "DE"
        "01756"          | "DE"
        "01757"          | "DE"
        "01758"          | "DE"
        "01759"          | "DE"

        //
        // 0176
        //
        "01760"          | "DE"
        "01761"          | "DE"
        "01762"          | "DE"
        "017630"         | "DE"
        "017631"         | "DE"
        "017632"         | "DE"
        // 017633 is reserved for voicemail - see tests below
        "017634"         | "DE"
        "017635"         | "DE"
        "017636"         | "DE"
        "017637"         | "DE"
        "017638"         | "DE"
        "017639"         | "DE"
        "01764"          | "DE"
        "01765"          | "DE"
        "01766"          | "DE"
        "01767"          | "DE"
        "01768"          | "DE"
        "01769"          | "DE"

        //
        // 0177
        //
        "01770"          | "DE"
        "01771"          | "DE"
        "01772"          | "DE"
        "01773"          | "DE"
        "01774"          | "DE"
        "01775"          | "DE"
        "01776"          | "DE"
        "01777"          | "DE"
        "01778"          | "DE"
        "017790"         | "DE"
        "017791"         | "DE"
        "017792"         | "DE"
        "017793"         | "DE"
        "017794"         | "DE"
        "017795"         | "DE"
        "017796"         | "DE"
        "017797"         | "DE"
        "017798"         | "DE"
        // 017799 is reserved for voicemail - see tests below

        //
        // 0178
        //
        "01780"          | "DE"
        "01781"          | "DE"
        "01782"          | "DE"
        "01783"          | "DE"
        "01784"          | "DE"
        "01785"          | "DE"
        "01786"          | "DE"
        "01787"          | "DE"
        "01788"          | "DE"
        "017890"         | "DE"
        "017891"         | "DE"
        "017892"         | "DE"
        "017893"         | "DE"
        "017894"         | "DE"
        "017895"         | "DE"
        "017896"         | "DE"
        "017897"         | "DE"
        "017898"         | "DE"
        // 017899 is reserved for voicemail - see tests below

        //
        // 0179
        //
        "01790"          | "DE"
        "01791"          | "DE"
        "01792"          | "DE"
        "017930"         | "DE"
        "017931"         | "DE"
        "017932"         | "DE"
        // 017933 is reserved for voicemail - see tests below
        "017934"         | "DE"
        "017935"         | "DE"
        "017936"         | "DE"
        "017937"         | "DE"
        "017938"         | "DE"
        "017939"         | "DE"
        "01794"          | "DE"
        "01795"          | "DE"
        "01796"          | "DE"
        "01797"          | "DE"
        "01798"          | "DE"
        "01799"          | "DE"
    }

    def "validate German Mobile 17 range with voicemail infix"(String numberUntilInfix, regionCode) {
        given:
        String[] numbersToTest = [numberUntilInfix + "000000",
                                  numberUntilInfix + "0000000",
                                  numberUntilInfix + "00000000",
                                  numberUntilInfix + "000000000",
                                  numberUntilInfix + "999999",
                                  numberUntilInfix + "9999999",
                                  numberUntilInfix + "99999999",
                                  numberUntilInfix + "999999999"]

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG]

        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html
        // x: 6 length 8 otherwise 7

        // but https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/Nummernplan_MobileDienste.pdf?__blob=publicationFile&v=1 rules
        // 11 or 10 is possible on each 17x and depends on the operator to decide

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:
        numberUntilInfix | regionCode
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line len of 7 - 8 digits denping on x=6

        //
        // 0170
        //
        "017013"         | "DE"
        //
        // 0171
        //
        "017113"         | "DE"
        //
        // 0172
        //
        "017250"         | "DE"
        "017255"         | "DE"
        //
        // 0173
        //
        "017350"         | "DE"
        "017355"         | "DE"
        //
        // 0174
        //
        "017450"         | "DE"
        "017455"         | "DE"
        //
        // 0175
        //
        "017513"         | "DE"
        //
        // 0176
        //
        "017633"         | "DE"
        //
        // 0177
        //
        "017799"         | "DE"
        //
        // 0178
        //
        "017899"         | "DE"
        //
        // 0179
        //
        "017933"         | "DE"
    }

    def "validate German ServiceNumbers 180 range"(String reserve, regionCode) {
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
                                  reserve + "2233445566",
                                  reserve + "22334455667"]

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.TOO_SHORT,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.IS_POSSIBLE,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_LONG,
                                                         PhoneNumberValidationResult.TOO_LONG]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:
        reserve          | regionCode
        //  0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        //  Numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0180/Nummernplan0180_ServiceDiensteRufnummer.pdf?__blob=publicationFile&v=1
        //  points out, that national numbers have 10 (3+7) digits in this range, but that there are historically shorter numbers
        //  At https://data.bundesnetzagentur.de/Bundesnetzagentur/SharedDocs/ExterneLinks/DE/Sachgebiete/Telekommunikation/Nummerierung/NVMwD.0180.Rufnummer.Vergeben.zip it can be checked, that shorter numbers have 3+5 & 3+6 digits
        // 01800 is reserve
        "01801"           | "DE"
        "01802"           | "DE"
        "01803"           | "DE"
        "01804"           | "DE"
        "01805"           | "DE"
        "01806"           | "DE"
        "01807"           | "DE"
        // 01808 is reserve
        // 01809 is reserve
    }

    def "validate German reserve 180 range"(String reserve, regionCode) {
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

        PhoneNumberValidationResult[] expectedResults = [PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER,
                                                         PhoneNumberValidationResult.INVALID_RESERVE_NUMBER]

        when:
        PhoneNumberValidationResult[] results = []
        for (number in numbersToTest) {
            results += target.isPhoneNumberPossibleWithReason(number, regionCode)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            assert results[i] == expectedResults[i]
        }

        where:
        reserve          | regionCode
        //  0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        //  Numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0180/Nummernplan0180_ServiceDiensteRufnummer.pdf?__blob=publicationFile&v=1
        //  points out, that national numbers have 10 (3+7) digits in this range, but that there are historically shorter numbers
        //  At https://data.bundesnetzagentur.de/Bundesnetzagentur/SharedDocs/ExterneLinks/DE/Sachgebiete/Telekommunikation/Nummerierung/NVMwD.0180.Rufnummer.Vergeben.zip it can be checked, that shorter numbers have 3+5 & 3+6 digits
        // reserve:

        "01800"          | "DE"
        "01808"          | "DE"
        "01809"          | "DE"

    }

    /*
TODO NDC Ranges see equivalent Testcases in IsValidNumberTest
*/

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
            assert expectedresults += PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_OPERATOR_ONLY
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
            assert results += target.isPhoneNumberPossibleWithReason(number, regionCode)
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
        assert result == expectedResult

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
        assert result == expectedResult

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
