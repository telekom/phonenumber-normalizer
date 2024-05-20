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

import de.telekom.phonenumbernormalizer.numberplans.PhoneNumberValidationResult
import spock.lang.Specification

class PhoneNumberValidatorImplTest extends Specification {

    PhoneNumberValidator target

    def "setup"() {
        target = new PhoneNumberValidatorImpl()
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
        "110"                       | "DE"       | PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY
        "110556677"                 | "DE"       | PhoneNumberValidationResult.INVALID_LENGTH
        "0110"                      | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "0110 556677"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE
        "0175 110"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0175 110555"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0203 110"                  | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "0203 110555"               | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49110"                    | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49110 556677"             | "DE"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49175 110"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49175 110555"             | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203 110"                | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203 110555"             | "DE"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49110"                    | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49110 556677"             | "FR"       | PhoneNumberValidationResult.INVALID_COUNTRY_CODE
        "+49175 110"                | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49175 110555"             | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203 110"                | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        "+49203 110555"             | "FR"       | PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE
        // end of 110
    }



}
