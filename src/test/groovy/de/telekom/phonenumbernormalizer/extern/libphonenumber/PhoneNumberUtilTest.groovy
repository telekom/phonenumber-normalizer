/*
 * Copyright © 2023 Deutsche Telekom AG (opensource@telekom.de)
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
package de.telekom.phonenumbernormalizer.extern.libphonenumber

import com.google.i18n.phonenumbers.PhoneNumberUtil
import spock.lang.Specification
import java.util.logging.Logger


class PhoneNumberUtilTest extends Specification {

    PhoneNumberUtil phoneUtil

    Logger logger = Logger.getLogger("")


    def "setup"() {
        this.phoneUtil = PhoneNumberUtil.getInstance()
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4\$-7s: %5\$s %n")
    }

    def "check if original lib fixed non check of NAC - E164"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number as E164: $number"

        def result = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)

        then: "is number expected: $expectedResult"
        if (result != expectedResult) {
            if (expectingFail) {
                logger.info("PhoneLib is still not correctly normalizing $number to $expectedResult for region $regionCode, by giving $result")
            } else {
                logger.warning("PhoneLib is suddenly not correctly normalizing $number to $expectedResult for region $regionCode, by giving $result")
            }
        } else {
            if (expectingFail) {
                logger.info("!!! PhoneLib is now correctly normalizing $number to $expectedResult for region $regionCode !!!")
            }
        }

        where:

        number                    | regionCode  | expectedResult    | expectingFail
        "0040(0176) 3 0 6 9 6541" | "DE"        | "+4017630696541"  | false
        "0040 176 3 0 6 9 6542"   | "DE"        | "+4017630696542"  | false
        "004017630696543"         | "DE"        | "+4017630696543"  | false
        "0040-0176 3 0 6 9 6544"  | "DE"        | "+4017630696544"  | false
        "0176 3 0 6 9 6544"       | "DE"        | "+4917630696544"  | false
        "0203556677"              | "DE"        | "+49203556677"    | false
        "203556677"               | "DE"        | "203556677"       | true
        "556677"                  | "DE"        | "556677"          | true
        "5566778"                 | "DE"        | "5566778"         | true
        "55667789"                | "DE"        | "55667789"        | true
        "556677889"               | "DE"        | "556677889"       | true
        "5566778899"              | "DE"        | "5566778899"      | true
        "55667788990"             | "DE"        | "55667788990"     | true
        "000"                     | "AU"        | "000"             | true // this is Austrian Emergency code alternative for 112
        "012345678"               | "IT"        | "+39012345678"    | false
        "312345678"               | "IT"        | "+39312345678"    | false
    }

    def "check if original lib fixed non check of NAC - IS_POSSIBLE_LOCAL_ONLY"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        if (result != expectedResult) {
            if (expectingFail) {
                logger.info("PhoneLib is still not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
            } else {
                logger.warning("PhoneLib is suddenly not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
            }
        } else {
            if (expectingFail) {
                logger.info("!!! PhoneLib is now correctly validating $number to $expectedResult for region $regionCode !!!")
            }
        }

        where:

        number                    | regionCode  | expectedResult                                            | expectingFail
        // Romania numbers must not have 1 has first digit of NAC
        // those indicate a special service, but there is no special service starting with 7
        // so normally the whole number must be invalid, but it is marked as TOO_LONG - an error not intended to check here
        // "0040(0176) 3 0 6 9 6541" | "DE"        | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        // "0040 176 3 0 6 9 6542"   | "DE"        | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        // "004017630696543"         | "DE"        | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        // "0040-0176 3 0 6 9 6544"  | "DE"        | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0176 3 0 6 9 6544"       | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0203556677"              | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "203556677"               | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "556677"                  | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "5566778"                 | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "55667789"                | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "556677889"               | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "5566778899"              | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "55667788990"             | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "000"                     | "AU"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true // this is Austrian Emergency code alternative for 112
        "012345678"               | "IT"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "312345678"               | "IT"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
    }

}

