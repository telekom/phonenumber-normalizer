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
package de.telekom.phonenumbernormalizer.extern.libphonenumber.PhoneNumberUtil

import com.google.i18n.phonenumbers.PhoneNumberUtil
import spock.lang.Specification

import java.util.logging.Logger


// Plain Number Format: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/NP_Nummernraum.pdf?__blob=publicationFile&v=6
// NDC with labels: https://www.itu.int/dms_pub/itu-t/oth/02/02/T02020000510006PDFE.pdf
// Overview of special number ranges: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/start.html

// Version 5.V.2020 of BenetzA number plan


class IsValidNumberTest extends Specification {

    PhoneNumberUtil phoneUtil

    Logger logger = Logger.getLogger("")

    boolean LOGONLYUNEXPECTED = true

    def "setup"() {
        this.phoneUtil = PhoneNumberUtil.getInstance()
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4\$-7s: %5\$s %n")
    }

    def logResult(result, expectedResult, expectingFail, number, regionCode) {
        if (result != expectedResult) {
            if (expectingFail) {
                if (!LOGONLYUNEXPECTED) {
                    logger.info("isValidNumber is still not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
                }
            } else {
                logger.warning("isValidNumber is suddenly not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
            }
        } else {
            if (expectingFail) {
                logger.info("isValidNumber is now correctly validating $number to $expectedResult for region $regionCode !!!")
            }
        }
        return true
    }

    def "check if original lib fixed isValid for police short code 110 in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode | expectedResult   | expectingFail
        // short code for Police (110) is not dial-able internationally nor does it has additional numbers
        "110"                       | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "0110"                      | "DE"       | false            | false
        "0203 110"                  | "DE"       | false            | true
        "0203 110555"               | "DE"       | false            | true
        "+49110"                    | "DE"       | false            | false
        "+49110 556677"             | "DE"       | false            | false
        "+49203 110"                | "DE"       | false            | true
        "+49203 110555"             | "DE"       | false            | true
        "+49110"                    | "FR"       | false            | false
        "+49110 556677"             | "FR"       | false            | false
        "+49203 110"                | "FR"       | false            | true
        "+49203 110555"             | "FR"       | false            | true
        // end of 110
    }




    def "check if original lib fixed isValidNumber for invalid German NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValidNumber: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult | expectingFail
        // invalid area code for germany - need to be false
        "02040 556677"              | "DE"        | false           | true
        "02041 556677"              | "DE"        | true            | false
        // 02041 is Bottrop
        "02042 556677"              | "DE"        | false           | true
        // 02043 is Gladbeck
        "02044 556677"              | "DE"        | false           | true
        // 02045 is Bottrop-Kirchhellen
        "02046 556677"              | "DE"        | false           | true

    }


}