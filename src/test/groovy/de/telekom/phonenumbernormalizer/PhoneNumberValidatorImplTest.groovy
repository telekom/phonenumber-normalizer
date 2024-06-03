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

    def "check if original lib fixes number starting with NAC digit after optional NDC"(String number, countryCode, expectedResult) {
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
        // end of 110
    }


}
