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

    Logger logger = Logger.getLogger(IsValidNumberTest.class.toString())

    static final boolean LOGONLYUNEXPECTED = true

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
        "110556677"                 | "DE"       | false            | false
        "0110"                      | "DE"       | false            | false
        "0175 110"                  | "DE"       | false            | false
        "0175 110555"               | "DE"       | false            | false
        "0175 1105555"              | "DE"       | true             | false
        "0175 11055555"             | "DE"       | false            | false
        "0175 110555555"            | "DE"       | false            | false
        "0203 110"                  | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number with NDC must not use 110
        "0203 110555"               | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number must not start with 110
        "+49110"                    | "DE"       | false            | false
        "+49110 556677"             | "DE"       | false            | false
        "+49175 110"                | "DE"       | false            | false
        "+49175 110555"             | "DE"       | false            | false
        "+49175 1105555"            | "DE"       | true             | false
        "+49175 11055555"           | "DE"       | false            | false
        "+49175 110555555"          | "DE"       | false            | false
        "+49203 110"                | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number with NDC must not use 110
        "+49203 110555"             | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number must not start with 110
        "+49110"                    | "FR"       | false            | false
        "+49110 556677"             | "FR"       | false            | false
        "+49175 110"                | "FR"       | false            | false
        "+49175 110555"             | "FR"       | false            | false
        "+49175 1105555"            | "FR"       | true             | false
        "+49175 11055555"           | "FR"       | false            | false
        "+49175 110555555"          | "FR"       | false            | false
        "+49203 110"                | "FR"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number with NDC must not use 110
        "+49203 110555"             | "FR"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number must not start with 110
        // end of 110
    }

    def "check if original lib fixed isValid for Emergency short code 112 in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode | expectedResult  | expectingFail
        // short code for emergency (112) is not dial-able internationally nor does it has additional numbers
        "112"                       | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "112556677"                 | "DE"       | false            | false
        "0112"                      | "DE"       | false            | false
        "0175 112"                  | "DE"       | false            | false
        "0175 112555"               | "DE"       | false            | false
        "0175 1125555"              | "DE"       | true             | false
        "0175 11255555"             | "DE"       | false            | false
        "0175 112555555"            | "DE"       | false            | false
        "0203 112"                  | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number with NDC must not use 112
        "0203 112555"               | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number must not start with 112
        "+49112"                    | "DE"       | false            | false
        "+49112 556677"             | "DE"       | false            | false
        "+49175 112"                | "DE"       | false            | false
        "+49175 112555"             | "DE"       | false            | false
        "+49175 1125555"            | "DE"       | true             | false
        "+49175 11255555"           | "DE"       | false            | false
        "+49175 112555555"          | "DE"       | false            | false
        "+49203 112"                | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number with NDC must not use 112
        "+49203 112555"             | "DE"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number must not start with 112
        "+49112"                    | "FR"       | false            | false
        "+49112 556677"             | "FR"       | false            | false
        "+49175 112"                | "FR"       | false            | false
        "+49175 112555"             | "FR"       | false            | false
        "+49175 1125555"            | "FR"       | true             | false
        "+49175 11255555"           | "FR"       | false            | false
        "+49175 112555555"          | "FR"       | false            | false
        "+49203 112"                | "FR"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number with NDC must not use 112
        "+49203 112555"             | "FR"       | false            | true  // see https://issuetracker.google.com/issues/341947688 fixline number must not start with 112
        // end of 112
    }

    def "check if original lib fixed isValid for German Government short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode | expectedResult   | expectingFail
        // 155 is Public Service Number for German administration, it is internationally reachable only from foreign countries
        "115"                       | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "115556677"                 | "DE"       | true             | true  // see https://issuetracker.google.com/issues/345753226 fixline number must not start with 155
        "0115"                      | "DE"       | false            | false // not valid by BnetzA definition from within Germany
        "0115 556677"               | "DE"       | false            | false
        "0175 115"                  | "DE"       | false            | false
        "0175 115555"               | "DE"       | false            | false
        "0175 1155555"              | "DE"       | true             | false
        "0175 11555555"             | "DE"       | false            | false
        "0175 115555555"            | "DE"       | false            | false
        "0203 115"                  | "DE"       | true             | false // 155 is supporting NDC to reach specific local government hotline: https://www.geoportal.de/Info/tk_05-erreichbarkeit-der-115
        "0203 115555"               | "DE"       | false            | true  // see https://issuetracker.google.com/issues/345753226 fixline number must not start with 155
        "+49115"                    | "DE"       | false            | false // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/115/115_Nummernplan_konsolidiert.pdf?__blob=publicationFile&v=1 at chapter 2.3
        "+49115 556677"             | "DE"       | false            | false
        "+49175 115"                | "DE"       | false            | false
        "+49175 115555"             | "DE"       | false            | false
        "+49175 1155555"            | "DE"       | true             | false
        "+49175 11555555"           | "DE"       | false            | false
        "+49175 115555555"          | "DE"       | false            | false
        "+49203 115"                | "DE"       | true             | false
        "+49203 115555"             | "DE"       | false            | true  // see https://issuetracker.google.com/issues/345753226 fixline number must not start with 155
        "+49115"                    | "FR"       | true             | true  // see https://issuetracker.google.com/issues/345753226 - https://www.115.de/SharedDocs/Nachrichten/DE/2018/115_aus_dem_ausland_erreichbar.html
        "+49115 556677"             | "FR"       | false            | false
        "+49175 115"                | "FR"       | false            | false
        "+49175 115555"             | "FR"       | false            | false
        "+49175 1155555"            | "FR"       | true             | false
        "+49175 11555555"           | "FR"       | false            | false
        "+49175 115555555"          | "FR"       | false            | false
        "+49203 115"                | "FR"       | true             | false
        "+49203 115555"             | "FR"       | false            | true  // see https://issuetracker.google.com/issues/345753226 fixline number must not start with 155
        // end of 115
    }

    def "check if original lib fixed isValid for EU social short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode | expectedResult   | expectingFail
        // 116 is mentioned in number plan as 1160 and 1161 but in special ruling a full 6 digit number block: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/116xyz/StrukturAusgestNrBereich_Id11155pdf.pdf?__blob=publicationFile&v=4
        // 116xyz is nationally and internationally reachable - special check 116000 as initial number, 116116 as assigned number and 116999 as max legal number
        "116"                       | "DE"       | false            | false
        "116000"                    | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "116116"                    | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "116999"                    | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "116 5566"                  | "DE"       | false            | false
        "116 55"                    | "DE"       | false            | false
        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/116xyz/116116.html
        // NAC + 116xxx
        // see no. 7: national 0116116 is not a valid number, but may be replaced by 116116 by the operator - caller could reach target. ( T-Mobile is doing so currently 03.11.2023 - no guarantee for the future nor for any other operator. Best practice, assuming call will not reach target=.
        "0116"                      | "DE"       | false            | false
        "0116000"                   | "DE"       | false            | false  // not valid by BnetzA definition just using NAC
        "0116116"                   | "DE"       | false            | false  // not valid by BnetzA definition just using NAC
        "0116999"                   | "DE"       | false            | false  // not valid by BnetzA definition just using NAC
        "0116 5566"                 | "DE"       | false            | false
        "0116 55"                   | "DE"       | false            | false

        // NAC + NDC (mobile) + 116xxx
        "0175 116"                  | "DE"       | false            | false
        "0175 116555"               | "DE"       | false            | false
        "0175 1165555"              | "DE"       | true             | false
        "0175 11655555"             | "DE"       | false            | false
        "0175 116555555"            | "DE"       | false            | false

        // NAC + NDC (e.g. for Duisburg) + 116xxx
        "0203116"                   | "DE"       | false            | true
        "0203116000"                | "DE"       | false            | true
        "0203116116"                | "DE"       | false            | true
        "0203116999"                | "DE"       | false            | true
        "0203116 5566"              | "DE"       | false            | true
        "0203116 55"                | "DE"       | false            | true

        // CC + 116xxx
        "+49116"                    | "DE"       | false            | false
        "+49116000"                 | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "+49116116"                 | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "+49116999"                 | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "+49116 5566"               | "DE"       | false            | false
        "+49116 55"                 | "DE"       | false            | false

        // CC + NDC (mobile) + 116xxx
        "+49175 116"                | "DE"       | false            | false
        "+49175 116555"             | "DE"       | false            | false
        "+49175 1165555"            | "DE"       | true             | false
        "+49175 11655555"           | "DE"       | false            | false
        "+49175 116555555"          | "DE"       | false            | false

        // CC + NDC (e.g. for Duisburg) + 116xxx
        "+49203116"                 | "DE"       | false            | true
        "+49203116000"              | "DE"       | false            | true
        "+49203116116"              | "DE"       | false            | true
        "+49203116999"              | "DE"       | false            | true
        "+49203116 5566"            | "DE"       | false            | true
        "+49203116 55"              | "DE"       | false            | true

        // CC + 116xxx from outside Germany
        "+49116"                    | "FR"       | false            | false
        "+49116000"                 | "FR"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "+49116116"                 | "FR"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "+49116999"                 | "FR"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "+49116 5566"               | "FR"       | false            | false
        "+49116 55"                 | "FR"       | false            | false

        // CC + NDC (mobile) + 116xxx from outside Germany
        "+49175 116"                | "FR"       | false            | false
        "+49175 116555"             | "FR"       | false            | false
        "+49175 1165555"            | "FR"       | true             | false
        "+49175 11655555"           | "FR"       | false            | false
        "+49175 116555555"          | "FR"       | false            | false

        // CC + NDC (e.g. for Duisburg) + 116xxx from outside Germany
        "+49203116"                 | "FR"       | false            | true
        "+49203116000"              | "FR"       | false            | true
        "+49203116116"              | "FR"       | false            | true
        "+49203116999"              | "FR"       | false            | true
        "+49203116 5566"            | "FR"       | false            | true
        "+49203116 55"              | "FR"       | false            | true

        // end of 116
    }

    def "check if original lib fixed isValid for German Call Assistant short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"

        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult  | expectingFail
        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/118xy/118xyNummernplan.pdf?__blob=publicationFile&v=1
        // it is mentioned, that those numbers are nationally reachable - which excludes them from locally, so no local number should work this way because without NDC it could not be seperated from the national number
        // implicitly it could also mean that those numbers are not routed from outside germany

        // 118 is starting part and in general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "118"                       | "DE"       | false            | false
        "1180"                      | "DE"       | false            | false
        "11800"                     | "DE"       | false            | false
        "118000"                    | "DE"       | false            | false // since its just reserve
        "118099"                    | "DE"       | false            | false // since its just reserve
        "1180000"                   | "DE"       | false            | false
        "1181"                      | "DE"       | false            | false
        "11810"                     | "DE"       | true             | true
        // Call Assistant of Deutsche Telekom
        "11833"                     | "DE"       | true             | true
        "118100"                    | "DE"       | false            | false
        "1189"                      | "DE"       | false            | false
        "11899"                     | "DE"       | true             | true
        "118999"                    | "DE"       | false            | false

        // Tested on 26.12.2023 - 11833 works on TMD, but neither 011833 nor +4911833 is working on T-Mobile Germany
        // NAC + 118(y)xx belongs to the number reserve of NAC + 11

        "0118"                      | "DE"       | false            | false
        "01180"                     | "DE"       | false            | false
        "011800"                    | "DE"       | false            | false
        "0118000"                   | "DE"       | false            | false
        "0118099"                   | "DE"       | false            | false
        "01180000"                  | "DE"       | false            | false
        "01181"                     | "DE"       | false            | false
        "011810"                    | "DE"       | false            | false
        "011833"                    | "DE"       | false            | false
        "0118100"                   | "DE"       | false            | false
        "01189"                     | "DE"       | false            | false
        "011899"                    | "DE"       | false            | false
        "0118999"                   | "DE"       | false            | false

        // NAC + NDC (e.g. for Duisburg) + 118(y)xx
        "0203118"                   | "DE"       | false            | true
        "02031180"                  | "DE"       | false            | true
        "020311800"                 | "DE"       | false            | true
        "0203118000"                | "DE"       | false            | true
        "0203118099"                | "DE"       | false            | true
        "02031180000"               | "DE"       | false            | true
        "02031181"                  | "DE"       | false            | true
        "020311810"                 | "DE"       | false            | true
        "020311833"                 | "DE"       | false            | true
        "0203118100"                | "DE"       | false            | true
        "02031189"                  | "DE"       | false            | true
        "020311899"                 | "DE"       | false            | true
        "0203118999"                | "DE"       | false            | true

        // NAC + mobile NDC  + 118(y)xx
        "0175118"                   | "DE"       | false            | false
        "01751180"                  | "DE"       | false            | false
        "017511800"                 | "DE"       | false            | false
        "0175118000"                | "DE"       | false            | false
        "0175118099"                | "DE"       | false            | false
        "01751180000"               | "DE"       | true             | false
        "017511800000"              | "DE"       | false            | false
        "01751181"                  | "DE"       | false            | false
        "017511810"                 | "DE"       | false            | false
        "017511833"                 | "DE"       | false            | false
        "0175118100"                | "DE"       | false            | false
        "01751181000"               | "DE"       | true             | false // special for mobile
        "017511810000"              | "DE"       | false            | false
        "01751189"                  | "DE"       | false            | false
        "017511899"                 | "DE"       | false            | false
        "0175118999"                | "DE"       | false            | false
        "01751189999"               | "DE"       | true             | false // special for mobile
        "017511899999"              | "DE"       | false            | false

        // CC + 118(y)xx
        "+49118"                    | "DE"       | false            | false
        "+491180"                   | "DE"       | false            | false
        "+4911800"                  | "DE"       | false            | false
        "+49118000"                 | "DE"       | false            | false
        "+49118099"                 | "DE"       | false            | false
        "+491180000"                | "DE"       | false            | false
        "+491181"                   | "DE"       | false            | false
        "+4911810"                  | "DE"       | false            | false
        "+4911833"                  | "DE"       | false            | false
        "+49118100"                 | "DE"       | false            | false
        "+491189"                   | "DE"       | false            | false
        "+4911899"                  | "DE"       | false            | false
        "+49118999"                 | "DE"       | false            | false

        // CC + NDC (e.g. for Duisburg) + 118(y)xx
        "+49203118"                 | "DE"       | false            | true
        "+492031180"                | "DE"       | false            | true
        "+4920311800"               | "DE"       | false            | true
        "+49203118000"              | "DE"       | false            | true
        "+49203118099"              | "DE"       | false            | true
        "+492031180000"             | "DE"       | false            | true
        "+492031181"                | "DE"       | false            | true
        "+4920311810"               | "DE"       | false            | true
        "+4920311833"               | "DE"       | false            | true
        "+49203118100"              | "DE"       | false            | true
        "+492031189"                | "DE"       | false            | true
        "+4920311899"               | "DE"       | false            | true
        "+49203118999"              | "DE"       | false            | true

        // CC + mobile NDC  + 118(y)xx
        "+49175118"                 | "DE"       | false            | false
        "+491751180"                | "DE"       | false            | false
        "+4917511800"               | "DE"       | false            | false
        "+49175118000"              | "DE"       | false            | false
        "+49175118099"              | "DE"       | false            | false
        "+491751180000"             | "DE"       | true             | false
        "+4917511800000"            | "DE"       | false            | false
        "+491751181"                | "DE"       | false            | false
        "+4917511810"               | "DE"       | false            | false
        "+4917511833"               | "DE"       | false            | false
        "+49175118100"              | "DE"       | false            | false
        "+491751181000"             | "DE"       | true             | false // special for mobile
        "+4917511810000"            | "DE"       | false            | false
        "+491751189"                | "DE"       | false            | false
        "+4917511899"               | "DE"       | false            | false
        "+49175118999"              | "DE"       | false            | false
        "+491751189999"             | "DE"       | true             | false // special for mobile
        "+4917511899999"            | "DE"       | false            | false

        // CC + 118(y)xx from outside Germany
        "+49118"                    | "FR"       | false            | false
        "+491180"                   | "FR"       | false            | false
        "+4911800"                  | "FR"       | false            | false
        "+49118000"                 | "FR"       | false            | false
        "+49118099"                 | "FR"       | false            | false
        "+491180000"                | "FR"       | false            | false
        "+491181"                   | "FR"       | false            | false
        "+4911810"                  | "FR"       | false            | false
        "+4911833"                  | "FR"       | false            | false
        "+49118100"                 | "FR"       | false            | false
        "+491189"                   | "FR"       | false            | false
        "+4911899"                  | "FR"       | false            | false
        "+49118999"                 | "FR"       | false            | false

        // CC + NDC (e.g. for Duisburg) + 118(y)xx from outside Germany
        "+49203118"                 | "FR"       | false            | true
        "+492031180"                | "FR"       | false            | true
        "+4920311800"               | "FR"       | false            | true
        "+49203118000"              | "FR"       | false            | true
        "+49203118099"              | "FR"       | false            | true
        "+492031180000"             | "FR"       | false            | true
        "+492031181"                | "FR"       | false            | true
        "+4920311810"               | "FR"       | false            | true
        "+4920311833"               | "FR"       | false            | true
        "+49203118100"              | "FR"       | false            | true
        "+492031189"                | "FR"       | false            | true
        "+4920311899"               | "FR"       | false            | true
        "+49203118999"              | "FR"       | false            | true

        // CC + mobile NDC  + 118(y)xx from outside Germany
        "+49175118"                 | "FR"       | false            | false
        "+491751180"                | "FR"       | false            | false
        "+4917511800"               | "FR"       | false            | false
        "+49175118000"              | "FR"       | false            | false
        "+49175118099"              | "FR"       | false            | false
        "+491751180000"             | "FR"       | true             | false
        "+4917511800000"            | "FR"       | false            | false
        "+491751181"                | "FR"       | false            | false
        "+4917511810"               | "FR"       | false            | false
        "+4917511833"               | "FR"       | false            | false
        "+49175118100"              | "FR"       | false            | false
        "+491751181000"             | "FR"       | true             | false // special for mobile
        "+4917511810000"            | "FR"       | false            | false
        "+491751189"                | "FR"       | false            | false
        "+4917511899"               | "FR"       | false            | false
        "+49175118999"              | "FR"       | false            | false
        "+491751189999"             | "FR"       | true             | false // special for mobile
        "+4917511899999"            | "FR"       | false            | false
        // end of 118
    }

    def "check if original lib fixed isValid for ambulance transport 19222 short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode | expectedResult   | expectingFail
        // prior to mobile, there where 19xxx short codes in fixed line - only 19222 for no emergency ambulance call is still valid
        // its a national reserved number, which in contrast to 112 might also be called with NDC to reach a specific ambulance center - not all NDC have a connected 19222.
        // for more information see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vfg_25_2006_konsFassung100823.pdf?__blob=publicationFile&v=3 chapter 7
        "19222"                     | "DE"       | true             | true  // not valid on mobil but on fixedline
        // using 19222 als NDC after NAC is checked by "online services 019xx"
        "0203 19222"                | "DE"       | true             | false
        "0203 19222555"             | "DE"       | false            | true  // must not be longer
        "+4919222"                  | "DE"       | false            | false
        // using 19222 from DE als NDC after CC is checked by "online services 019xx"
        "+49203 19222"              | "DE"       | true             | false
        "+49203 19222555"           | "DE"       | false            | true  // must not be longer
        // using 19222 from FR als NDC after CC is checked by "online services 019xx"
        "+49203 19222"              | "FR"       | true             | false
        "+49203 19222555"           | "FR"       | false            | true  // must not be longer
        // end of 19222
    }

    def "check if original lib fixed isValid for German mass traffic NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode | expectedResult   | expectingFail
        // 137 is masstraffic 10 digits
        "0137 000 0000"             | "DE"       | false            | false  // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "0137 000 00000"            | "DE"       | false            | false  // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "0137 000 000"              | "DE"       | false            | false  // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370

        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0137/0137_Nummernplan.pdf?__blob=publicationFile&v=4
        // within each zone, there are only a few ranges assigned: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/belegteRNB/0137MABEZBelegteRNB_Basepage.html?nn=326370
        // Zone 1 is valid, but only with exactly 10 digits
        "0137 100 0000"             | "DE"       | true             | false
        "0137 100 00000"            | "DE"       | false            | false
        "0137 100 000"              | "DE"       | false            | false
        // Zone 2 is valid, but only with exactly 10 digits
        "0137 200 0000"             | "DE"       | true             | false
        "0137 200 00000"            | "DE"       | false            | false
        "0137 200 000"              | "DE"       | false            | false
        // Zone 3 is valid, but only with exactly 10 digits
        "0137 300 0000"             | "DE"       | true             | false
        "0137 300 00000"            | "DE"       | false            | false
        "0137 300 000"              | "DE"       | false            | false
        // Zone 4 is valid, but only with exactly 10 digits
        "0137 400 0000"             | "DE"       | true             | false
        "0137 400 00000"            | "DE"       | false            | false
        "0137 400 000"              | "DE"       | false            | false
        // Zone 5 is valid, but only with exactly 10 digits
        "0137 500 0000"             | "DE"       | true             | false
        "0137 500 00000"            | "DE"       | false            | false
        "0137 500 000"              | "DE"       | false            | false
        // Zone 6 is valid, but only with exactly 10 digits
        "0137 600 0000"             | "DE"       | true             | false
        "0137 600 00000"            | "DE"       | false            | false
        "0137 600 000"              | "DE"       | false            | false
        // Zone 7 is valid, but only with exactly 10 digits
        "0137 700 0000"             | "DE"       | true             | false
        "0137 700 00000"            | "DE"       | false            | false
        "0137 700 000"              | "DE"       | false            | false
        // Zone 8 is valid, but only with exactly 10 digits
        "0137 800 0000"             | "DE"       | true             | false
        "0137 800 00000"            | "DE"       | false            | false
        "0137 800 000"              | "DE"       | false            | false
        // Zone 9 is valid, but only with exactly 10 digits
        "0137 900 0000"             | "DE"       | true             | false
        "0137 900 00000"            | "DE"       | false            | false
        "0137 900 000"              | "DE"       | false            | false

    }

    def "check if original lib fixed isValid for German Mobile 15 range"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
        given:
        String[]  numbersToTest

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
        Boolean[] expectedResults = [false, true, false, false, true, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i<results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        numberUntilInfix | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 015xxyyyyyyy xx = block code, yyyyyyy fixed length number in 2 digit block, so together 9 digit is the overall length
        // 015zzzaaaaaa zzz = newer block zzz, aaaaaa fixes length number in 3 digit block, so together 9 digit is the overall length

        // >>> https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/zugeteilte%20RNB/start.html is a list of used blocks
        // >>> https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/freie%20RNB/start.html
        // >>> markes testcases from isPosible, which are not valid right now.

        //
        // 0150
        //
        // 015000 is reserved for voicemail - see tests below
        "015001"         | "DE"      | [false, false, false, false, false, false]
        "015002"         | "DE"      | [false, false, false, false, false, false]
        "015003"         | "DE"      | [false, false, false, false, false, false]
        "015004"         | "DE"      | [false, false, false, false, false, false]
        "015005"         | "DE"      | [false, false, false, false, false, false]
        "015006"         | "DE"      | [false, false, false, false, false, false]
        "015007"         | "DE"      | [false, false, false, false, false, false]
        "015008"         | "DE"      | [false, false, false, false, false, false]
        "015009"         | "DE"      | [false, false, false, false, false, false]
        "01501"          | "DE"      | [false, false, false, false, false, false]
        "01502"          | "DE"      | [false, false, false, false, false, false]
        "01503"          | "DE"      | [false, false, false, false, false, false]
        "01504"          | "DE"      | [false, false, false, false, false, false]
        "01505"          | "DE"      | [false, false, false, false, false, false]
        "01506"          | "DE"      | [false, false, false, false, false, false]
        "01507"          | "DE"      | [false, false, false, false, false, false]
        "01508"          | "DE"      | [false, false, false, false, false, false]
        "01509"          | "DE"      | [false, false, false, false, false, false]

        //
        // 0151
        //
        "01510"          | "DE"      | [false, false, false, false, false, false]
        "015110"         | "DE"      | [false, false, false, false, false, false]
        "015111"         | "DE"      | [false, false, false, false, false, false]
        "015112"         | "DE"      | [false, false, false, false, false, false]
        // 015113 is reserved for voicemail - see tests below
        "015114"         | "DE"      | [false, false, false, false, false, false]
        "015115"         | "DE"      | [false, false, false, false, false, false]
        "015116"         | "DE"      | [false, false, false, false, false, false]
        "015117"         | "DE"      | [false, false, false, false, false, false]
        "015118"         | "DE"      | [false, false, false, false, false, false]
        "015119"         | "DE"      | [false, false, false, false, false, false]
        "01512"          | "DE"      | [false, false, false, false, false, false]
        "01513"          | "DE"      | [false, false, false, false, false, false]
        "01514"          | "DE"      | [false, false, false, false, false, false]
        "01515"          | "DE"      | [false, false, false, false, false, false]
        "01516"          | "DE"      | [false, false, false, false, false, false]
        "01517"          | "DE"      | [false, false, false, false, false, false]
        "01518"          | "DE"      | [false, false, false, false, false, false]
        "01519"          | "DE"      | [false, false, false, false, false, false]

        //
        // 0152
        //
        "015200"         | "DE"      | [false, false, false, false, false, false]
        "015201"         | "DE"      | [false, false, false, false, false, false]
        "015202"         | "DE"      | [false, false, false, false, false, false]
        "015203"         | "DE"      | [false, false, false, false, false, false]
        "015204"         | "DE"      | [false, false, false, false, false, false]
        // 0152050 is reserved for voicemail - see tests below
        "0152051"        | "DE"      | [false, false, false, false, false, false]
        "0152052"        | "DE"      | [false, false, false, false, false, false]
        "0152053"        | "DE"      | [false, false, false, false, false, false]
        "0152054"        | "DE"      | [false, false, false, false, false, false]
        // 0152055 is reserved for voicemail - see tests below
        "0152056"        | "DE"      | [false, false, false, false, false, false]
        "0152057"        | "DE"      | [false, false, false, false, false, false]
        "0152058"        | "DE"      | [false, false, false, false, false, false]
        "0152059"        | "DE"      | [false, false, false, false, false, false]
        "015206"         | "DE"      | [false, false, false, false, false, false]
        "015207"         | "DE"      | [false, false, false, false, false, false]
        "015208"         | "DE"      | [false, false, false, false, false, false]
        "015209"         | "DE"      | [false, false, false, false, false, false]

        "015210"         | "DE"      | [false, false, false, false, false, false]
        "015211"         | "DE"      | [false, false, false, false, false, false]
        "015212"         | "DE"      | [false, false, false, false, false, false]
        "015213"         | "DE"      | [false, false, false, false, false, false]
        "015214"         | "DE"      | [false, false, false, false, false, false]
        // 0152150 is reserved for voicemail - see tests below
        "0152151"        | "DE"      | [false, false, false, false, false, false]
        "0152152"        | "DE"      | [false, false, false, false, false, false]
        "0152153"        | "DE"      | [false, false, false, false, false, false]
        "0152154"        | "DE"      | [false, false, false, false, false, false]
        // 0152155 is reserved for voicemail - see tests below
        "0152156"        | "DE"      | [false, false, false, false, false, false]
        "0152157"        | "DE"      | [false, false, false, false, false, false]
        "0152158"        | "DE"      | [false, false, false, false, false, false]
        "0152159"        | "DE"      | [false, false, false, false, false, false]
        "015216"         | "DE"      | [false, false, false, false, false, false]
        "015217"         | "DE"      | [false, false, false, false, false, false]
        "015218"         | "DE"      | [false, false, false, false, false, false]
        "015219"         | "DE"      | [false, false, false, false, false, false]

        "015220"         | "DE"      | [false, false, false, false, false, false]
        "015221"         | "DE"      | [false, false, false, false, false, false]
        "015222"         | "DE"      | [false, false, false, false, false, false]
        "015223"         | "DE"      | [false, false, false, false, false, false]
        "015224"         | "DE"      | [false, false, false, false, false, false]
        // 0152250 is reserved for voicemail - see tests below
        "0152251"        | "DE"      | [false, false, false, false, false, false]
        "0152252"        | "DE"      | [false, false, false, false, false, false]
        "0152253"        | "DE"      | [false, false, false, false, false, false]
        "0152254"        | "DE"      | [false, false, false, false, false, false]
        // 0152255 is reserved for voicemail - see tests below
        "0152256"        | "DE"      | [false, false, false, false, false, false]
        "0152257"        | "DE"      | [false, false, false, false, false, false]
        "0152258"        | "DE"      | [false, false, false, false, false, false]
        "0152259"        | "DE"      | [false, false, false, false, false, false]
        "015226"         | "DE"      | [false, false, false, false, false, false]
        "015227"         | "DE"      | [false, false, false, false, false, false]
        "015228"         | "DE"      | [false, false, false, false, false, false]
        "015229"         | "DE"      | [false, false, false, false, false, false]

        "015230"         | "DE"      | [false, false, false, false, false, false]
        "015231"         | "DE"      | [false, false, false, false, false, false]
        "015232"         | "DE"      | [false, false, false, false, false, false]
        "015233"         | "DE"      | [false, false, false, false, false, false]
        "015234"         | "DE"      | [false, false, false, false, false, false]
        // 0152350 is reserved for voicemail - see tests below
        "0152351"        | "DE"      | [false, false, false, false, false, false]
        "0152352"        | "DE"      | [false, false, false, false, false, false]
        "0152353"        | "DE"      | [false, false, false, false, false, false]
        "0152354"        | "DE"      | [false, false, false, false, false, false]
        // 0152355 is reserved for voicemail - see tests below
        "0152356"        | "DE"      | [false, false, false, false, false, false]
        "0152357"        | "DE"      | [false, false, false, false, false, false]
        "0152358"        | "DE"      | [false, false, false, false, false, false]
        "0152359"        | "DE"      | [false, false, false, false, false, false]
        "015236"         | "DE"      | [false, false, false, false, false, false]
        "015237"         | "DE"      | [false, false, false, false, false, false]
        "015238"         | "DE"      | [false, false, false, false, false, false]
        "015239"         | "DE"      | [false, false, false, false, false, false]

        "015240"         | "DE"      | [false, false, false, false, false, false]
        "015241"         | "DE"      | [false, false, false, false, false, false]
        "015242"         | "DE"      | [false, false, false, false, false, false]
        "015243"         | "DE"      | [false, false, false, false, false, false]
        "015244"         | "DE"      | [false, false, false, false, false, false]
        // 0152450 is reserved for voicemail - see tests below
        "0152451"        | "DE"      | [false, false, false, false, false, false]
        "0152452"        | "DE"      | [false, false, false, false, false, false]
        "0152453"        | "DE"      | [false, false, false, false, false, false]
        "0152454"        | "DE"      | [false, false, false, false, false, false]
        // 0152455 is reserved for voicemail - see tests below
        "0152456"        | "DE"      | [false, false, false, false, false, false]
        "0152457"        | "DE"      | [false, false, false, false, false, false]
        "0152458"        | "DE"      | [false, false, false, false, false, false]
        "0152459"        | "DE"      | [false, false, false, false, false, false]
        "015246"         | "DE"      | [false, false, false, false, false, false]
        "015247"         | "DE"      | [false, false, false, false, false, false]
        "015248"         | "DE"      | [false, false, false, false, false, false]
        "015249"         | "DE"      | [false, false, false, false, false, false]

        "015250"         | "DE"      | [false, false, false, false, false, false]
        "015251"         | "DE"      | [false, false, false, false, false, false]
        "015252"         | "DE"      | [false, false, false, false, false, false]
        "015253"         | "DE"      | [false, false, false, false, false, false]
        "015254"         | "DE"      | [false, false, false, false, false, false]
        // 0152550 is reserved for voicemail - see tests below
        "0152551"        | "DE"      | [false, false, false, false, false, false]
        "0152552"        | "DE"      | [false, false, false, false, false, false]
        "0152553"        | "DE"      | [false, false, false, false, false, false]
        "0152554"        | "DE"      | [false, false, false, false, false, false]
        // 0152555 is reserved for voicemail - see tests below
        "0152556"        | "DE"      | [false, false, false, false, false, false]
        "0152557"        | "DE"      | [false, false, false, false, false, false]
        "0152558"        | "DE"      | [false, false, false, false, false, false]
        "0152559"        | "DE"      | [false, false, false, false, false, false]
        "015256"         | "DE"      | [false, false, false, false, false, false]
        "015257"         | "DE"      | [false, false, false, false, false, false]
        "015258"         | "DE"      | [false, false, false, false, false, false]
        "015259"         | "DE"      | [false, false, false, false, false, false]

        "015260"         | "DE"      | [false, false, false, false, false, false]
        "015261"         | "DE"      | [false, false, false, false, false, false]
        "015262"         | "DE"      | [false, false, false, false, false, false]
        "015263"         | "DE"      | [false, false, false, false, false, false]
        "015264"         | "DE"      | [false, false, false, false, false, false]
        // 0152650 is reserved for voicemail - see tests below
        "0152651"        | "DE"      | [false, false, false, false, false, false]
        "0152652"        | "DE"      | [false, false, false, false, false, false]
        "0152653"        | "DE"      | [false, false, false, false, false, false]
        "0152654"        | "DE"      | [false, false, false, false, false, false]
        // 0152655 is reserved for voicemail - see tests below
        "0152656"        | "DE"      | [false, false, false, false, false, false]
        "0152657"        | "DE"      | [false, false, false, false, false, false]
        "0152658"        | "DE"      | [false, false, false, false, false, false]
        "0152659"        | "DE"      | [false, false, false, false, false, false]
        "015266"         | "DE"      | [false, false, false, false, false, false]
        "015267"         | "DE"      | [false, false, false, false, false, false]
        "015268"         | "DE"      | [false, false, false, false, false, false]
        "015269"         | "DE"      | [false, false, false, false, false, false]

        "015270"         | "DE"      | [false, false, false, false, false, false]
        "015271"         | "DE"      | [false, false, false, false, false, false]
        "015272"         | "DE"      | [false, false, false, false, false, false]
        "015273"         | "DE"      | [false, false, false, false, false, false]
        "015274"         | "DE"      | [false, false, false, false, false, false]
        // 0152750 is reserved for voicemail - see tests below
        "0152751"        | "DE"      | [false, false, false, false, false, false]
        "0152752"        | "DE"      | [false, false, false, false, false, false]
        "0152753"        | "DE"      | [false, false, false, false, false, false]
        "0152754"        | "DE"      | [false, false, false, false, false, false]
        // 0152755 is reserved for voicemail - see tests below
        "0152756"        | "DE"      | [false, false, false, false, false, false]
        "0152757"        | "DE"      | [false, false, false, false, false, false]
        "0152758"        | "DE"      | [false, false, false, false, false, false]
        "0152759"        | "DE"      | [false, false, false, false, false, false]
        "015276"         | "DE"      | [false, false, false, false, false, false]
        "015277"         | "DE"      | [false, false, false, false, false, false]
        "015278"         | "DE"      | [false, false, false, false, false, false]
        "015279"         | "DE"      | [false, false, false, false, false, false]

        "015280"         | "DE"      | [false, false, false, false, false, false]
        "015281"         | "DE"      | [false, false, false, false, false, false]
        "015282"         | "DE"      | [false, false, false, false, false, false]
        "015283"         | "DE"      | [false, false, false, false, false, false]
        "015284"         | "DE"      | [false, false, false, false, false, false]
        // 0152850 is reserved for voicemail - see tests below
        "0152851"        | "DE"      | [false, false, false, false, false, false]
        "0152852"        | "DE"      | [false, false, false, false, false, false]
        "0152853"        | "DE"      | [false, false, false, false, false, false]
        "0152854"        | "DE"      | [false, false, false, false, false, false]
        // 0152855 is reserved for voicemail - see tests below
        "0152856"        | "DE"      | [false, false, false, false, false, false]
        "0152857"        | "DE"      | [false, false, false, false, false, false]
        "0152858"        | "DE"      | [false, false, false, false, false, false]
        "0152859"        | "DE"      | [false, false, false, false, false, false]
        "015286"         | "DE"      | [false, false, false, false, false, false]
        "015287"         | "DE"      | [false, false, false, false, false, false]
        "015288"         | "DE"      | [false, false, false, false, false, false]
        "015289"         | "DE"      | [false, false, false, false, false, false]

        "015290"         | "DE"      | [false, false, false, false, false, false]
        "015291"         | "DE"      | [false, false, false, false, false, false]
        "015292"         | "DE"      | [false, false, false, false, false, false]
        "015293"         | "DE"      | [false, false, false, false, false, false]
        "015294"         | "DE"      | [false, false, false, false, false, false]
        // 0152950 is reserved for voicemail - see tests below
        "0152951"        | "DE"      | [false, false, false, false, false, false]
        "0152952"        | "DE"      | [false, false, false, false, false, false]
        "0152953"        | "DE"      | [false, false, false, false, false, false]
        "0152954"        | "DE"      | [false, false, false, false, false, false]
        // 0152955 is reserved for voicemail - see tests below
        "0152956"        | "DE"      | [false, false, false, false, false, false]
        "0152957"        | "DE"      | [false, false, false, false, false, false]
        "0152958"        | "DE"      | [false, false, false, false, false, false]
        "0152959"        | "DE"      | [false, false, false, false, false, false]
        "015296"         | "DE"      | [false, false, false, false, false, false]
        "015297"         | "DE"      | [false, false, false, false, false, false]
        "015298"         | "DE"      | [false, false, false, false, false, false]
        "015299"         | "DE"      | [false, false, false, false, false, false]

        //
        // 0153
        //
        // 015300 is reserved for voicemail - see tests below
        "015301"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015302"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015303"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015304"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015305"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015306"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015307"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015308"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015309"         | "DE"      | [false, true, false, false, true, false]     // <--
        "01531"          | "DE"      | [false, false, false, false, true, false]    // <--
        "01532"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01533"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01534"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01535"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01536"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01537"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01538"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01539"          | "DE"      | [false, true, false, false, true, false]     // <--

        //
        // 0154
        //
        // 015400 is reserved for voicemail - see tests below
        "015401"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015402"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015403"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015404"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015405"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015406"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015407"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015408"         | "DE"      | [false, true, false, false, true, false]     // <--
        "015409"         | "DE"      | [false, true, false, false, true, false]     // <--
        "01541"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01542"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01543"          | "DE"      | [false, true, false, false, true, false]     // <--
        "0154"           | "DE"      | [false, true, false, false, true, false]     // <--
        "01545"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01546"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01547"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01548"          | "DE"      | [false, true, false, false, true, false]     // <--
        "01549"          | "DE"      | [false, true, false, false, true, false]     // <--

        //
        // 0155
        //
        // 015500 is reserved for voicemail - see tests below
        "015501"         | "DE"      | [false, false, false, false, false, false]
        "015502"         | "DE"      | [false, false, false, false, false, false]
        "015503"         | "DE"      | [false, false, false, false, false, false]
        "015504"         | "DE"      | [false, false, false, false, false, false]
        "015505"         | "DE"      | [false, false, false, false, false, false]
        "015506"         | "DE"      | [false, false, false, false, false, false]
        "015507"         | "DE"      | [false, false, false, false, false, false]
        "015508"         | "DE"      | [false, false, false, false, false, false]
        "015509"         | "DE"      | [false, false, false, false, false, false]
        "01551"          | "DE"      | [false, false, false, false, false, false]
        "01552"          | "DE"      | [false, false, false, false, false, false]
        "01553"          | "DE"      | [false, false, false, false, false, false]
        "01554"          | "DE"      | [false, false, false, false, false, false]
        "01555"          | "DE"      | [false, false, false, false, false, false]
        "01556"          | "DE"      | [false, false, false, false, false, false]
        "01557"          | "DE"      | [false, false, false, false, false, false]
        "01558"          | "DE"      | [false, false, false, false, false, false]
        "01559"          | "DE"      | [false, false, false, false, false, false]

        //
        // 0156
        //
        // 015600 is reserved for voicemail - see tests below
        "015601"         | "DE"      | [false, false, false, false, false, false]
        "015602"         | "DE"      | [false, false, false, false, false, false]
        "015603"         | "DE"      | [false, false, false, false, false, false]
        "015604"         | "DE"      | [false, false, false, false, false, false]
        "015605"         | "DE"      | [false, false, false, false, false, false]
        "015606"         | "DE"      | [false, false, false, false, false, false]
        "015607"         | "DE"      | [false, false, false, false, false, false]
        "015608"         | "DE"      | [false, false, false, false, false, false]
        "015609"         | "DE"      | [false, false, false, false, false, false]
        "01561"          | "DE"      | [false, false, false, false, false, false]
        "01562"          | "DE"      | [false, false, false, false, false, false]
        "01563"          | "DE"      | [false, false, false, false, false, false]
        "01564"          | "DE"      | [false, false, false, false, false, false]
        "01565"          | "DE"      | [false, false, false, false, false, false]
        "01566"          | "DE"      | [false, false, false, false, false, false]
        "01567"          | "DE"      | [false, false, false, false, false, false]
        "01568"          | "DE"      | [false, false, false, false, false, false]
        "01569"          | "DE"      | [false, false, false, false, false, false]

        //
        // 0157
        //
        "015700"         | "DE"      | [false, false, false, false, false, false]
        "015701"         | "DE"      | [false, false, false, false, false, false]
        "015702"         | "DE"      | [false, false, false, false, false, false]
        "015703"         | "DE"      | [false, false, false, false, false, false]
        "015704"         | "DE"      | [false, false, false, false, false, false]
        "015705"         | "DE"      | [false, false, false, false, false, false]
        "015706"         | "DE"      | [false, false, false, false, false, false]
        "015707"         | "DE"      | [false, false, false, false, false, false]
        "015708"         | "DE"      | [false, false, false, false, false, false]
        "0157090"        | "DE"      | [false, false, false, false, false, false]
        "0157091"        | "DE"      | [false, false, false, false, false, false]
        "0157092"        | "DE"      | [false, false, false, false, false, false]
        "0157093"        | "DE"      | [false, false, false, false, false, false]
        "0157094"        | "DE"      | [false, false, false, false, false, false]
        "0157095"        | "DE"      | [false, false, false, false, false, false]
        "0157096"        | "DE"      | [false, false, false, false, false, false]
        "0157097"        | "DE"      | [false, false, false, false, false, false]
        "0157098"        | "DE"      | [false, false, false, false, false, false]
        // 0157099 is reserved for voicemail - see tests below

        "015710"         | "DE"      | [false, false, false, false, false, false]
        "015711"         | "DE"      | [false, false, false, false, false, false]
        "015712"         | "DE"      | [false, false, false, false, false, false]
        "015713"         | "DE"      | [false, false, false, false, false, false]
        "015714"         | "DE"      | [false, false, false, false, false, false]
        "015715"         | "DE"      | [false, false, false, false, false, false]
        "015716"         | "DE"      | [false, false, false, false, false, false]
        "015717"         | "DE"      | [false, false, false, false, false, false]
        "015718"         | "DE"      | [false, false, false, false, false, false]
        "0157190"        | "DE"      | [false, false, false, false, false, false]
        "0157191"        | "DE"      | [false, false, false, false, false, false]
        "0157192"        | "DE"      | [false, false, false, false, false, false]
        "0157193"        | "DE"      | [false, false, false, false, false, false]
        "0157194"        | "DE"      | [false, false, false, false, false, false]
        "0157195"        | "DE"      | [false, false, false, false, false, false]
        "0157196"        | "DE"      | [false, false, false, false, false, false]
        "0157197"        | "DE"      | [false, false, false, false, false, false]
        "0157198"        | "DE"      | [false, false, false, false, false, false]
        // 0157199 is reserved for voicemail - see tests below

        "015720"         | "DE"      | [false, false, false, false, false, false]
        "015721"         | "DE"      | [false, false, false, false, false, false]
        "015722"         | "DE"      | [false, false, false, false, false, false]
        "015723"         | "DE"      | [false, false, false, false, false, false]
        "015724"         | "DE"      | [false, false, false, false, false, false]
        "015725"         | "DE"      | [false, false, false, false, false, false]
        "015726"         | "DE"      | [false, false, false, false, false, false]
        "015727"         | "DE"      | [false, false, false, false, false, false]
        "015728"         | "DE"      | [false, false, false, false, false, false]
        "0157290"        | "DE"      | [false, false, false, false, false, false]
        "0157291"        | "DE"      | [false, false, false, false, false, false]
        "0157292"        | "DE"      | [false, false, false, false, false, false]
        "0157293"        | "DE"      | [false, false, false, false, false, false]
        "0157294"        | "DE"      | [false, false, false, false, false, false]
        "0157295"        | "DE"      | [false, false, false, false, false, false]
        "0157296"        | "DE"      | [false, false, false, false, false, false]
        "0157297"        | "DE"      | [false, false, false, false, false, false]
        "0157298"        | "DE"      | [false, false, false, false, false, false]
        // 0157299 is reserved for voicemail - see tests below

        "015730"         | "DE"      | [false, false, false, false, false, false]
        "015731"         | "DE"      | [false, false, false, false, false, false]
        "015732"         | "DE"      | [false, false, false, false, false, false]
        "015733"         | "DE"      | [false, false, false, false, false, false]
        "015734"         | "DE"      | [false, false, false, false, false, false]
        "015735"         | "DE"      | [false, false, false, false, false, false]
        "015736"         | "DE"      | [false, false, false, false, false, false]
        "015737"         | "DE"      | [false, false, false, false, false, false]
        "015738"         | "DE"      | [false, false, false, false, false, false]
        "0157390"        | "DE"      | [false, false, false, false, false, false]
        "0157391"        | "DE"      | [false, false, false, false, false, false]
        "0157392"        | "DE"      | [false, false, false, false, false, false]
        "0157393"        | "DE"      | [false, false, false, false, false, false]
        "0157394"        | "DE"      | [false, false, false, false, false, false]
        "0157395"        | "DE"      | [false, false, false, false, false, false]
        "0157396"        | "DE"      | [false, false, false, false, false, false]
        "0157397"        | "DE"      | [false, false, false, false, false, false]
        "0157398"        | "DE"      | [false, false, false, false, false, false]
        // 0157399 is reserved for voicemail - see tests below

        "015740"         | "DE"      | [false, false, false, false, false, false]
        "015741"         | "DE"      | [false, false, false, false, false, false]
        "015742"         | "DE"      | [false, false, false, false, false, false]
        "015743"         | "DE"      | [false, false, false, false, false, false]
        "015744"         | "DE"      | [false, false, false, false, false, false]
        "015745"         | "DE"      | [false, false, false, false, false, false]
        "015746"         | "DE"      | [false, false, false, false, false, false]
        "015747"         | "DE"      | [false, false, false, false, false, false]
        "015748"         | "DE"      | [false, false, false, false, false, false]
        "0157490"        | "DE"      | [false, false, false, false, false, false]
        "0157491"        | "DE"      | [false, false, false, false, false, false]
        "0157492"        | "DE"      | [false, false, false, false, false, false]
        "0157493"        | "DE"      | [false, false, false, false, false, false]
        "0157494"        | "DE"      | [false, false, false, false, false, false]
        "0157495"        | "DE"      | [false, false, false, false, false, false]
        "0157496"        | "DE"      | [false, false, false, false, false, false]
        "0157497"        | "DE"      | [false, false, false, false, false, false]
        "0157498"        | "DE"      | [false, false, false, false, false, false]
        // 0157499 is reserved for voicemail - see tests below

        "015750"         | "DE"      | [false, false, false, false, false, false]
        "015751"         | "DE"      | [false, false, false, false, false, false]
        "015752"         | "DE"      | [false, false, false, false, false, false]
        "015753"         | "DE"      | [false, false, false, false, false, false]
        "015754"         | "DE"      | [false, false, false, false, false, false]
        "015755"         | "DE"      | [false, false, false, false, false, false]
        "015756"         | "DE"      | [false, false, false, false, false, false]
        "015757"         | "DE"      | [false, false, false, false, false, false]
        "015758"         | "DE"      | [false, false, false, false, false, false]
        "0157590"        | "DE"      | [false, false, false, false, false, false]
        "0157591"        | "DE"      | [false, false, false, false, false, false]
        "0157592"        | "DE"      | [false, false, false, false, false, false]
        "0157593"        | "DE"      | [false, false, false, false, false, false]
        "0157594"        | "DE"      | [false, false, false, false, false, false]
        "0157595"        | "DE"      | [false, false, false, false, false, false]
        "0157596"        | "DE"      | [false, false, false, false, false, false]
        "0157597"        | "DE"      | [false, false, false, false, false, false]
        "0157598"        | "DE"      | [false, false, false, false, false, false]
        // 0157599 is reserved for voicemail - see tests below

        "015760"         | "DE"      | [false, false, false, false, false, false]
        "015761"         | "DE"      | [false, false, false, false, false, false]
        "015762"         | "DE"      | [false, false, false, false, false, false]
        "015763"         | "DE"      | [false, false, false, false, false, false]
        "015764"         | "DE"      | [false, false, false, false, false, false]
        "015765"         | "DE"      | [false, false, false, false, false, false]
        "015766"         | "DE"      | [false, false, false, false, false, false]
        "015767"         | "DE"      | [false, false, false, false, false, false]
        "015768"         | "DE"      | [false, false, false, false, false, false]
        "0157690"        | "DE"      | [false, false, false, false, false, false]
        "0157691"        | "DE"      | [false, false, false, false, false, false]
        "0157692"        | "DE"      | [false, false, false, false, false, false]
        "0157693"        | "DE"      | [false, false, false, false, false, false]
        "0157694"        | "DE"      | [false, false, false, false, false, false]
        "0157695"        | "DE"      | [false, false, false, false, false, false]
        "0157696"        | "DE"      | [false, false, false, false, false, false]
        "0157697"        | "DE"      | [false, false, false, false, false, false]
        "0157698"        | "DE"      | [false, false, false, false, false, false]
        // 0157699 is reserved for voicemail - see tests below

        "015770"         | "DE"      | [false, false, false, false, false, false]
        "015771"         | "DE"      | [false, false, false, false, false, false]
        "015772"         | "DE"      | [false, false, false, false, false, false]
        "015773"         | "DE"      | [false, false, false, false, false, false]
        "015774"         | "DE"      | [false, false, false, false, false, false]
        "015775"         | "DE"      | [false, false, false, false, false, false]
        "015776"         | "DE"      | [false, false, false, false, false, false]
        "015777"         | "DE"      | [false, false, false, false, false, false]
        "015778"         | "DE"      | [false, false, false, false, false, false]
        "0157790"        | "DE"      | [false, false, false, false, false, false]
        "0157791"        | "DE"      | [false, false, false, false, false, false]
        "0157792"        | "DE"      | [false, false, false, false, false, false]
        "0157793"        | "DE"      | [false, false, false, false, false, false]
        "0157794"        | "DE"      | [false, false, false, false, false, false]
        "0157795"        | "DE"      | [false, false, false, false, false, false]
        "0157796"        | "DE"      | [false, false, false, false, false, false]
        "0157797"        | "DE"      | [false, false, false, false, false, false]
        "0157798"        | "DE"      | [false, false, false, false, false, false]
        // 0157799 is reserved for voicemail - see tests below

        "015780"         | "DE"      | [false, false, false, false, false, false]
        "015781"         | "DE"      | [false, false, false, false, false, false]
        "015782"         | "DE"      | [false, false, false, false, false, false]
        "015783"         | "DE"      | [false, false, false, false, false, false]
        "015784"         | "DE"      | [false, false, false, false, false, false]
        "015785"         | "DE"      | [false, false, false, false, false, false]
        "015786"         | "DE"      | [false, false, false, false, false, false]
        "015787"         | "DE"      | [false, false, false, false, false, false]
        "015788"         | "DE"      | [false, false, false, false, false, false]
        "0157890"        | "DE"      | [false, false, false, false, false, false]
        "0157891"        | "DE"      | [false, false, false, false, false, false]
        "0157892"        | "DE"      | [false, false, false, false, false, false]
        "0157893"        | "DE"      | [false, false, false, false, false, false]
        "0157894"        | "DE"      | [false, false, false, false, false, false]
        "0157895"        | "DE"      | [false, false, false, false, false, false]
        "0157896"        | "DE"      | [false, false, false, false, false, false]
        "0157897"        | "DE"      | [false, false, false, false, false, false]
        "0157898"        | "DE"      | [false, false, false, false, false, false]
        // 0157899 is reserved for voicemail - see tests below

        "015790"         | "DE"      | [false, false, false, false, false, false]
        "015791"         | "DE"      | [false, false, false, false, false, false]
        "015792"         | "DE"      | [false, false, false, false, false, false]
        "015793"         | "DE"      | [false, false, false, false, false, false]
        "015794"         | "DE"      | [false, false, false, false, false, false]
        "015795"         | "DE"      | [false, false, false, false, false, false]
        "015796"         | "DE"      | [false, false, false, false, false, false]
        "015797"         | "DE"      | [false, false, false, false, false, false]
        "015798"         | "DE"      | [false, false, false, false, false, false]
        "0157990"        | "DE"      | [false, false, false, false, false, false]
        "0157991"        | "DE"      | [false, false, false, false, false, false]
        "0157992"        | "DE"      | [false, false, false, false, false, false]
        "0157993"        | "DE"      | [false, false, false, false, false, false]
        "0157994"        | "DE"      | [false, false, false, false, false, false]
        "0157995"        | "DE"      | [false, false, false, false, false, false]
        "0157996"        | "DE"      | [false, false, false, false, false, false]
        "0157997"        | "DE"      | [false, false, false, false, false, false]
        "0157998"        | "DE"      | [false, false, false, false, false, false]
        // 0157999 is reserved for voicemail - see tests below

        //
        // 0158
        //
        // 015800 is reserved for voicemail - see tests below
        "015801"         | "DE"      | [false, false, false, false, false, false]
        "015802"         | "DE"      | [false, false, false, false, false, false]
        "015803"         | "DE"      | [false, false, false, false, false, false]
        "015804"         | "DE"      | [false, false, false, false, false, false]
        "015805"         | "DE"      | [false, false, false, false, false, false]
        "015806"         | "DE"      | [false, false, false, false, false, false]
        "015807"         | "DE"      | [false, false, false, false, false, false]
        "015808"         | "DE"      | [false, false, false, false, false, false]
        "015809"         | "DE"      | [false, false, false, false, false, false]
        "01581"          | "DE"      | [false, false, false, false, false, false]
        "01582"          | "DE"      | [false, false, false, false, false, false]
        "01583"          | "DE"      | [false, false, false, false, false, false]
        "01584"          | "DE"      | [false, false, false, false, false, false]
        "01585"          | "DE"      | [false, false, false, false, false, false]
        "01586"          | "DE"      | [false, false, false, false, false, false]
        "01587"          | "DE"      | [false, false, false, false, false, false]
        "01588"          | "DE"      | [false, false, false, false, false, false]
        "01589"          | "DE"      | [false, false, false, false, false, false]

        //
        // 0159
        //
        "015900"         | "DE"      | [false, false, false, false, false, false]
        "015901"         | "DE"      | [false, false, false, false, false, false]
        "015902"         | "DE"      | [false, false, false, false, false, false]
        "0159030"        | "DE"      | [false, false, false, false, false, false]
        "0159031"        | "DE"      | [false, false, false, false, false, false]
        "0159032"        | "DE"      | [false, false, false, false, false, false]
        // 0159033 is reserved for voicemail - see tests below
        "0159034"        | "DE"      | [false, false, false, false, false, false]
        "0159035"        | "DE"      | [false, false, false, false, false, false]
        "0159036"        | "DE"      | [false, false, false, false, false, false]
        "0159037"        | "DE"      | [false, false, false, false, false, false]
        "0159038"        | "DE"      | [false, false, false, false, false, false]
        "0159039"        | "DE"      | [false, false, false, false, false, false]
        "015904"         | "DE"      | [false, false, false, false, false, false]
        "015905"         | "DE"      | [false, false, false, false, false, false]
        "015906"         | "DE"      | [false, false, false, false, false, false]
        "015907"         | "DE"      | [false, false, false, false, false, false]
        "015908"         | "DE"      | [false, false, false, false, false, false]
        "015909"         | "DE"      | [false, false, false, false, false, false]

        "015910"         | "DE"      | [false, false, false, false, false, false]
        "015911"         | "DE"      | [false, false, false, false, false, false]
        "015912"         | "DE"      | [false, false, false, false, false, false]
        "0159130"        | "DE"      | [false, false, false, false, false, false]
        "0159131"        | "DE"      | [false, false, false, false, false, false]
        "0159132"        | "DE"      | [false, false, false, false, false, false]
        // 0159133 is reserved for voicemail - see tests below
        "0159134"        | "DE"      | [false, false, false, false, false, false]
        "0159135"        | "DE"      | [false, false, false, false, false, false]
        "0159136"        | "DE"      | [false, false, false, false, false, false]
        "0159137"        | "DE"      | [false, false, false, false, false, false]
        "0159138"        | "DE"      | [false, false, false, false, false, false]
        "0159139"        | "DE"      | [false, false, false, false, false, false]
        "015914"         | "DE"      | [false, false, false, false, false, false]
        "015915"         | "DE"      | [false, false, false, false, false, false]
        "015916"         | "DE"      | [false, false, false, false, false, false]
        "015917"         | "DE"      | [false, false, false, false, false, false]
        "015918"         | "DE"      | [false, false, false, false, false, false]
        "015919"         | "DE"      | [false, false, false, false, false, false]

        "015920"         | "DE"      | [false, false, false, false, false, false]
        "015921"         | "DE"      | [false, false, false, false, false, false]
        "015922"         | "DE"      | [false, false, false, false, false, false]
        "0159230"        | "DE"      | [false, false, false, false, false, false]
        "0159231"        | "DE"      | [false, false, false, false, false, false]
        "0159232"        | "DE"      | [false, false, false, false, false, false]
        // 0159233 is reserved for voicemail - see tests below
        "0159234"        | "DE"      | [false, false, false, false, false, false]
        "0159235"        | "DE"      | [false, false, false, false, false, false]
        "0159236"        | "DE"      | [false, false, false, false, false, false]
        "0159237"        | "DE"      | [false, false, false, false, false, false]
        "0159238"        | "DE"      | [false, false, false, false, false, false]
        "0159239"        | "DE"      | [false, false, false, false, false, false]
        "015924"         | "DE"      | [false, false, false, false, false, false]
        "015925"         | "DE"      | [false, false, false, false, false, false]
        "015926"         | "DE"      | [false, false, false, false, false, false]
        "015927"         | "DE"      | [false, false, false, false, false, false]
        "015928"         | "DE"      | [false, false, false, false, false, false]
        "015929"         | "DE"      | [false, false, false, false, false, false]

        "015930"         | "DE"      | [false, false, false, false, false, false]
        "015931"         | "DE"      | [false, false, false, false, false, false]
        "015932"         | "DE"      | [false, false, false, false, false, false]
        "0159330"        | "DE"      | [false, false, false, false, false, false]
        "0159331"        | "DE"      | [false, false, false, false, false, false]
        "0159332"        | "DE"      | [false, false, false, false, false, false]
        // 0159333 is reserved for voicemail - see tests below
        "0159334"        | "DE"      | [false, false, false, false, false, false]
        "0159335"        | "DE"      | [false, false, false, false, false, false]
        "0159336"        | "DE"      | [false, false, false, false, false, false]
        "0159337"        | "DE"      | [false, false, false, false, false, false]
        "0159338"        | "DE"      | [false, false, false, false, false, false]
        "0159339"        | "DE"      | [false, false, false, false, false, false]
        "015934"         | "DE"      | [false, false, false, false, false, false]
        "015935"         | "DE"      | [false, false, false, false, false, false]
        "015936"         | "DE"      | [false, false, false, false, false, false]
        "015937"         | "DE"      | [false, false, false, false, false, false]
        "015938"         | "DE"      | [false, false, false, false, false, false]
        "015939"         | "DE"      | [false, false, false, false, false, false]

        "015940"         | "DE"      | [false, false, false, false, false, false]
        "015941"         | "DE"      | [false, false, false, false, false, false]
        "015942"         | "DE"      | [false, false, false, false, false, false]
        "0159430"        | "DE"      | [false, false, false, false, false, false]
        "0159431"        | "DE"      | [false, false, false, false, false, false]
        "0159432"        | "DE"      | [false, false, false, false, false, false]
        // 0159433 is reserved for voicemail - see tests below
        "0159434"        | "DE"      | [false, false, false, false, false, false]
        "0159435"        | "DE"      | [false, false, false, false, false, false]
        "0159436"        | "DE"      | [false, false, false, false, false, false]
        "0159437"        | "DE"      | [false, false, false, false, false, false]
        "0159438"        | "DE"      | [false, false, false, false, false, false]
        "0159439"        | "DE"      | [false, false, false, false, false, false]
        "015944"         | "DE"      | [false, false, false, false, false, false]
        "015945"         | "DE"      | [false, false, false, false, false, false]
        "015946"         | "DE"      | [false, false, false, false, false, false]
        "015947"         | "DE"      | [false, false, false, false, false, false]
        "015948"         | "DE"      | [false, false, false, false, false, false]
        "015949"         | "DE"      | [false, false, false, false, false, false]

        "015950"         | "DE"      | [false, false, false, false, false, false]
        "015951"         | "DE"      | [false, false, false, false, false, false]
        "015952"         | "DE"      | [false, false, false, false, false, false]
        "0159530"        | "DE"      | [false, false, false, false, false, false]
        "0159531"        | "DE"      | [false, false, false, false, false, false]
        "0159532"        | "DE"      | [false, false, false, false, false, false]
        // 0159533 is reserved for voicemail - see tests below
        "0159534"        | "DE"      | [false, false, false, false, false, false]
        "0159535"        | "DE"      | [false, false, false, false, false, false]
        "0159536"        | "DE"      | [false, false, false, false, false, false]
        "0159537"        | "DE"      | [false, false, false, false, false, false]
        "0159538"        | "DE"      | [false, false, false, false, false, false]
        "0159539"        | "DE"      | [false, false, false, false, false, false]
        "015954"         | "DE"      | [false, false, false, false, false, false]
        "015955"         | "DE"      | [false, false, false, false, false, false]
        "015956"         | "DE"      | [false, false, false, false, false, false]
        "015957"         | "DE"      | [false, false, false, false, false, false]
        "015958"         | "DE"      | [false, false, false, false, false, false]
        "015959"         | "DE"      | [false, false, false, false, false, false]

        "015960"         | "DE"      | [false, false, false, false, false, false]
        "015961"         | "DE"      | [false, false, false, false, false, false]
        "015962"         | "DE"      | [false, false, false, false, false, false]
        "0159630"        | "DE"      | [false, false, false, false, false, false]
        "0159631"        | "DE"      | [false, false, false, false, false, false]
        "0159632"        | "DE"      | [false, false, false, false, false, false]
        // 0159633 is reserved for voicemail - see tests below
        "0159634"        | "DE"      | [false, false, false, false, false, false]
        "0159635"        | "DE"      | [false, false, false, false, false, false]
        "0159636"        | "DE"      | [false, false, false, false, false, false]
        "0159637"        | "DE"      | [false, false, false, false, false, false]
        "0159638"        | "DE"      | [false, false, false, false, false, false]
        "0159639"        | "DE"      | [false, false, false, false, false, false]
        "015964"         | "DE"      | [false, false, false, false, false, false]
        "015965"         | "DE"      | [false, false, false, false, false, false]
        "015966"         | "DE"      | [false, false, false, false, false, false]
        "015967"         | "DE"      | [false, false, false, false, false, false]
        "015968"         | "DE"      | [false, false, false, false, false, false]
        "015969"         | "DE"      | [false, false, false, false, false, false]

        "015970"         | "DE"      | [false, false, false, false, false, false]
        "015971"         | "DE"      | [false, false, false, false, false, false]
        "015972"         | "DE"      | [false, false, false, false, false, false]
        "0159730"        | "DE"      | [false, false, false, false, false, false]
        "0159731"        | "DE"      | [false, false, false, false, false, false]
        "0159732"        | "DE"      | [false, false, false, false, false, false]
        // 0159733 is reserved for voicemail - see tests below
        "0159734"        | "DE"      | [false, false, false, false, false, false]
        "0159735"        | "DE"      | [false, false, false, false, false, false]
        "0159736"        | "DE"      | [false, false, false, false, false, false]
        "0159737"        | "DE"      | [false, false, false, false, false, false]
        "0159738"        | "DE"      | [false, false, false, false, false, false]
        "0159739"        | "DE"      | [false, false, false, false, false, false]
        "015974"         | "DE"      | [false, false, false, false, false, false]
        "015975"         | "DE"      | [false, false, false, false, false, false]
        "015976"         | "DE"      | [false, false, false, false, false, false]
        "015977"         | "DE"      | [false, false, false, false, false, false]
        "015978"         | "DE"      | [false, false, false, false, false, false]
        "015979"         | "DE"      | [false, false, false, false, false, false]

        "015980"         | "DE"      | [false, false, false, false, false, false]
        "015981"         | "DE"      | [false, false, false, false, false, false]
        "015982"         | "DE"      | [false, false, false, false, false, false]
        "0159830"        | "DE"      | [false, false, false, false, false, false]
        "0159831"        | "DE"      | [false, false, false, false, false, false]
        "0159832"        | "DE"      | [false, false, false, false, false, false]
        // 0159833 is reserved for voicemail - see tests below
        "0159834"        | "DE"      | [false, false, false, false, false, false]
        "0159835"        | "DE"      | [false, false, false, false, false, false]
        "0159836"        | "DE"      | [false, false, false, false, false, false]
        "0159837"        | "DE"      | [false, false, false, false, false, false]
        "0159838"        | "DE"      | [false, false, false, false, false, false]
        "0159839"        | "DE"      | [false, false, false, false, false, false]
        "015984"         | "DE"      | [false, false, false, false, false, false]
        "015985"         | "DE"      | [false, false, false, false, false, false]
        "015986"         | "DE"      | [false, false, false, false, false, false]
        "015987"         | "DE"      | [false, false, false, false, false, false]
        "015988"         | "DE"      | [false, false, false, false, false, false]
        "015989"         | "DE"      | [false, false, false, false, false, false]

        "015990"         | "DE"      | [false, false, false, false, false, false]
        "015991"         | "DE"      | [false, false, false, false, false, false]
        "015992"         | "DE"      | [false, false, false, false, false, false]
        "0159930"        | "DE"      | [false, false, false, false, false, false]
        "0159931"        | "DE"      | [false, false, false, false, false, false]
        "0159932"        | "DE"      | [false, false, false, false, false, false]
        // 0159933 is reserved for voicemail - see tests below
        "0159934"        | "DE"      | [false, false, false, false, false, false]
        "0159935"        | "DE"      | [false, false, false, false, false, false]
        "0159936"        | "DE"      | [false, false, false, false, false, false]
        "0159937"        | "DE"      | [false, false, false, false, false, false]
        "0159938"        | "DE"      | [false, false, false, false, false, false]
        "0159939"        | "DE"      | [false, false, false, false, false, false]
        "015994"         | "DE"      | [false, false, false, false, false, false]
        "015995"         | "DE"      | [false, false, false, false, false, false]
        "015996"         | "DE"      | [false, false, false, false, false, false]
        "015997"         | "DE"      | [false, false, false, false, false, false]
        "015998"         | "DE"      | [false, false, false, false, false, false]
        "015999"         | "DE"      | [false, false, false, false, false, false]

        // end of 015xx
    }

    def "check if original lib fixed isValid for German Mobile 15 range with voicemail infix"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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
        Boolean[] expectedResults = [false, false, false, true, false,
                                     false, false, false, true, false]


        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:

        for (int i = 0; i<results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:

        numberUntilInfix | regionCode | expectingFails
        // There infixes of two digits used to address the voicemail of a line
        // see 2.5 in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // This makes the number two digits longer, but on the other hand a short version with the infix does not exists, that is the reason, why above range started at 15001, since 15000 would be an infix

        // 15-0-INFIX:OO-xx 3-Block: 0xx
        "015000"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-1-INFIX:13-x(x) 2-Block: 1x and 3-Block: 1xx
        "015113"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-2x-INFIX:50-(x) 2-Block: 2x and 3-Block: 2xx  First Infix: 50
        "0152050"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152150"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152250"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152350"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152450"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152550"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152650"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152750"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152850"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        "0152950"        | "DE"      | [false, true, false, true, false, false, true, false, true, false]
        // 15-2x-INFI:55-(x) 2-Block: 2x and 3-Block: 2xx  Second Infix: 55
        "0152055"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152155"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152255"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152355"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152455"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152555"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152655"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152755"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152855"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0152955"        | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-3-INFIX:OO-xx 3-Block: 3xx
        "015300"         | "DE"      | [false, false, false, false, false, false, false, false, false, false]

        // 15-4-INFIX:OO-xx 3-Block: 4xx
        "015400"         | "DE"      | [false, false, false, false, false, false, false, false, false, false]

        // 15-5-INFIX:OO-xx 3-Block: 5xx
        "015500"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-6-INFIX:OO-xx 3-Block: 6xx
        "015600"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-7x-INFIX:99-(x) 2-Block: 7x and 3-Block: 7xx
        "0157099"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157199"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157299"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157399"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157499"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157599"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157699"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157799"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157899"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0157999"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-8-INFIX:OO-xx 3-Block: 8xx
        "015800"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // 15-9x-INFIX:33-(x) 2-Block: 9x and 3-Block: 9xx
        "0159033"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159133"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159233"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159333"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159433"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159533"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159633"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159733"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159833"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]
        "0159933"         | "DE"      | [false, true, false, false, false, false, true, false, false, false]

        // end of 015xx for voicemail
    }

    def "check if original lib fixed isValid for German Mobile 16 range"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, true, true, false,
                                     false, true, true, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        numberUntilInfix | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 016xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0160
        //
        "01600"          | "DE" | [false, false, false, false, false, false, false, false]
        "016010"         | "DE" | [false, false, false, false, false, false, false, false]
        "016011"         | "DE" | [false, false, false, false, false, false, false, false]
        "016012"         | "DE" | [false, false, false, false, false, false, false, false]
        // 016013 is reserved for voicemail - see tests below
        "016014"         | "DE" | [false, false, false, false, false, false, false, false]
        "016015"         | "DE" | [false, false, false, false, false, false, false, false]
        "016016"         | "DE" | [false, false, false, false, false, false, false, false]
        "016017"         | "DE" | [false, false, false, false, false, false, false, false]
        "016018"         | "DE" | [false, false, false, false, false, false, false, false]
        "016019"         | "DE" | [false, false, false, false, false, false, false, false]
        "01602"          | "DE" | [false, false, false, false, false, false, false, false]
        "01603"          | "DE" | [false, false, false, false, false, false, false, false]
        "01604"          | "DE" | [false, false, false, false, false, false, false, false]
        "01605"          | "DE" | [false, false, false, false, false, false, false, false]
        "01606"          | "DE" | [false, false, false, false, false, false, false, false]
        "01607"          | "DE" | [false, false, false, false, false, false, false, false]
        "01608"          | "DE" | [false, false, false, false, false, false, false, false]
        "01609"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0162
        //
        "01620"          | "DE" | [false, false, false, false, false, false, false, false]
        "01621"          | "DE" | [false, false, false, false, false, false, false, false]
        "01622"          | "DE" | [false, false, false, false, false, false, false, false]
        "01623"          | "DE" | [false, false, false, false, false, false, false, false]
        "01624"          | "DE" | [false, false, false, false, false, false, false, false]
        // 016250 is reserved for voicemail - see tests below
        "016251"         | "DE" | [false, false, false, false, false, false, false, false]
        "016252"         | "DE" | [false, false, false, false, false, false, false, false]
        "016253"         | "DE" | [false, false, false, false, false, false, false, false]
        "016254"         | "DE" | [false, false, false, false, false, false, false, false]
        // 016255 is reserved for voicemail - see tests below
        "016256"         | "DE" | [false, false, false, false, false, false, false, false]
        "016257"         | "DE" | [false, false, false, false, false, false, false, false]
        "016258"         | "DE" | [false, false, false, false, false, false, false, false]
        "016259"         | "DE" | [false, false, false, false, false, false, false, false]
        "01626"          | "DE" | [false, false, false, false, false, false, false, false]
        "01627"          | "DE" | [false, false, false, false, false, false, false, false]
        "01628"          | "DE" | [false, false, false, false, false, false, false, false]
        "01629"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0163
        //
        "01630"          | "DE" | [false, false, false, false, false, false, false, false]
        "01631"          | "DE" | [false, false, false, false, false, false, false, false]
        "01632"          | "DE" | [false, false, false, false, false, false, false, false]
        "01633"          | "DE" | [false, false, false, false, false, false, false, false]
        "01634"          | "DE" | [false, false, false, false, false, false, false, false]
        "01635"          | "DE" | [false, false, false, false, false, false, false, false]
        "01636"          | "DE" | [false, false, false, false, false, false, false, false]
        "01637"          | "DE" | [false, false, false, false, false, false, false, false]
        "01638"          | "DE" | [false, false, false, false, false, false, false, false]
        "016390"         | "DE" | [false, false, false, false, false, false, false, false]
        "016391"         | "DE" | [false, false, false, false, false, false, false, false]
        "016392"         | "DE" | [false, false, false, false, false, false, false, false]
        "016393"         | "DE" | [false, false, false, false, false, false, false, false]
        "016394"         | "DE" | [false, false, false, false, false, false, false, false]
        "016395"         | "DE" | [false, false, false, false, false, false, false, false]
        "016396"         | "DE" | [false, false, false, false, false, false, false, false]
        "016397"         | "DE" | [false, false, false, false, false, false, false, false]
        "016398"         | "DE" | [false, false, false, false, false, false, false, false]
        // 016399 is reserved for voicemail - see tests below
    }

    def "check if original lib fixed isValid for German Mobile 16 range with voicemail infix"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [numberUntilInfix + "000000",
                                  numberUntilInfix + "0000000",
                                  numberUntilInfix + "00000000",
                                  numberUntilInfix + "000000000",
                                  numberUntilInfix + "999999",
                                  numberUntilInfix + "9999999",
                                  numberUntilInfix + "99999999",
                                  numberUntilInfix + "999999999"]

        Boolean[] expectedResults = [false, true, true, false,
                                     false, true, true, false]

        when:
        PhoneNumberUtil.ValidationResult[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        numberUntilInfix | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 016xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0160
        //
        "016013"         | "DE" | [true, true, true, true, true, true, true, true]
        //
        // 0162
        //
        "016250"         | "DE" | [true, true, true, true, true, true, true, true]
        "016255"         | "DE" | [true, true, true, true, true, true, true, true]

        //
        // 0163
        //
        "016399"         | "DE" | [true, true, true, true, true, true, true, true]
    }

    def "check if original lib fixed isValid for German reserve 16 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false]

        when:
        PhoneNumberUtil.ValidationResult[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        // 0161, 165, 166, 167 are reserved for future use

        "0161"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true]
        "0165"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true]
        "0166"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true]
        "0167"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true]

    }

    def "check if original lib fixed isValid for German 'Funkruf' 16(8/9) range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false, true, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        // 0168, 169 are using a 14 digit national number (0164 is not further defined).
        // TODO: could 0164 needs to be covered
        "0168"           | "DE" | [false, true, true, true, true, true, true, true, true, true, true, false, false]
        "0169"           | "DE" | [false, true, true, true, true, true, true, true, true, true, true, false, false]

    }

    def "check if original lib fixed isValid for German Mobile 17 range"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, true, false, false,
                                     false, true, false, false]

        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html
        // x: 6 length 8 otherwise 7
        if (numberUntilInfix.startsWith("0176")) {
            expectedResults = [false, false, true, false,
                               false, false, true, false]
        }


        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        numberUntilInfix | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits
        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html
        // x: 6 length 8 otherwise 7

        //
        // 0170
        //
        "01700"          | "DE" | [false, false, false, false, false, false, false, false]
        "017010"         | "DE" | [false, false, false, false, false, false, false, false]
        "017011"         | "DE" | [false, false, false, false, false, false, false, false]
        "017012"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017013 is reserved for voicemail - see tests below
        "017014"         | "DE" | [false, false, false, false, false, false, false, false]
        "017015"         | "DE" | [false, false, false, false, false, false, false, false]
        "017016"         | "DE" | [false, false, false, false, false, false, false, false]
        "017017"         | "DE" | [false, false, false, false, false, false, false, false]
        "017018"         | "DE" | [false, false, false, false, false, false, false, false]
        "017019"         | "DE" | [false, false, false, false, false, false, false, false]
        "01702"          | "DE" | [false, false, false, false, false, false, false, false]
        "01703"          | "DE" | [false, false, false, false, false, false, false, false]
        "01704"          | "DE" | [false, false, false, false, false, false, false, false]
        "01705"          | "DE" | [false, false, false, false, false, false, false, false]
        "01706"          | "DE" | [false, false, false, false, false, false, false, false]
        "01707"          | "DE" | [false, false, false, false, false, false, false, false]
        "01708"          | "DE" | [false, false, false, false, false, false, false, false]
        "01709"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0171
        //
        "01710"          | "DE" | [false, false, false, false, false, false, false, false]
        "017110"         | "DE" | [false, false, false, false, false, false, false, false]
        "017111"         | "DE" | [false, false, false, false, false, false, false, false]
        "017112"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017113 is reserved for voicemail - see tests below
        "017114"         | "DE" | [false, false, false, false, false, false, false, false]
        "017115"         | "DE" | [false, false, false, false, false, false, false, false]
        "017116"         | "DE" | [false, false, false, false, false, false, false, false]
        "017117"         | "DE" | [false, false, false, false, false, false, false, false]
        "017118"         | "DE" | [false, false, false, false, false, false, false, false]
        "017119"         | "DE" | [false, false, false, false, false, false, false, false]
        "01712"          | "DE" | [false, false, false, false, false, false, false, false]
        "01713"          | "DE" | [false, false, false, false, false, false, false, false]
        "01714"          | "DE" | [false, false, false, false, false, false, false, false]
        "01715"          | "DE" | [false, false, false, false, false, false, false, false]
        "01716"          | "DE" | [false, false, false, false, false, false, false, false]
        "01717"          | "DE" | [false, false, false, false, false, false, false, false]
        "01718"          | "DE" | [false, false, false, false, false, false, false, false]
        "01719"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0172
        //
        "01720"          | "DE" | [false, false, false, false, false, false, false, false]
        "01721"          | "DE" | [false, false, false, false, false, false, false, false]
        "01722"          | "DE" | [false, false, false, false, false, false, false, false]
        "01723"          | "DE" | [false, false, false, false, false, false, false, false]
        "01724"          | "DE" | [false, false, false, false, false, false, false, false]
        // 017250 is reserved for voicemail - see tests below
        "017251"         | "DE" | [false, false, false, false, false, false, false, false]
        "017252"         | "DE" | [false, false, false, false, false, false, false, false]
        "017253"         | "DE" | [false, false, false, false, false, false, false, false]
        "017254"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017255 is reserved for voicemail - see tests below
        "017256"         | "DE" | [false, false, false, false, false, false, false, false]
        "017257"         | "DE" | [false, false, false, false, false, false, false, false]
        "017258"         | "DE" | [false, false, false, false, false, false, false, false]
        "017259"         | "DE" | [false, false, false, false, false, false, false, false]
        "01726"          | "DE" | [false, false, false, false, false, false, false, false]
        "01727"          | "DE" | [false, false, false, false, false, false, false, false]
        "01728"          | "DE" | [false, false, false, false, false, false, false, false]
        "01729"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0173
        //
        "01730"          | "DE" | [false, false, false, false, false, false, false, false]
        "01731"          | "DE" | [false, false, false, false, false, false, false, false]
        "01732"          | "DE" | [false, false, false, false, false, false, false, false]
        "01733"          | "DE" | [false, false, false, false, false, false, false, false]
        "01734"          | "DE" | [false, false, false, false, false, false, false, false]
        // 017350 is reserved for voicemail - see tests below
        "017351"         | "DE" | [false, false, false, false, false, false, false, false]
        "017352"         | "DE" | [false, false, false, false, false, false, false, false]
        "017353"         | "DE" | [false, false, false, false, false, false, false, false]
        "017354"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017355 is reserved for voicemail - see tests below
        "017356"         | "DE" | [false, false, false, false, false, false, false, false]
        "017357"         | "DE" | [false, false, false, false, false, false, false, false]
        "017358"         | "DE" | [false, false, false, false, false, false, false, false]
        "017359"         | "DE" | [false, false, false, false, false, false, false, false]
        "01736"          | "DE" | [false, false, false, false, false, false, false, false]
        "01737"          | "DE" | [false, false, false, false, false, false, false, false]
        "01738"          | "DE" | [false, false, false, false, false, false, false, false]
        "01739"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0174
        //
        "01740"          | "DE" | [false, false, false, false, false, false, false, false]
        "01741"          | "DE" | [false, false, false, false, false, false, false, false]
        "01742"          | "DE" | [false, false, false, false, false, false, false, false]
        "01743"          | "DE" | [false, false, false, false, false, false, false, false]
        "01744"          | "DE" | [false, false, false, false, false, false, false, false]
        // 017450 is reserved for voicemail - see tests below
        "017451"         | "DE" | [false, false, false, false, false, false, false, false]
        "017452"         | "DE" | [false, false, false, false, false, false, false, false]
        "017453"         | "DE" | [false, false, false, false, false, false, false, false]
        "017454"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017455 is reserved for voicemail - see tests below
        "017456"         | "DE" | [false, false, false, false, false, false, false, false]
        "017457"         | "DE" | [false, false, false, false, false, false, false, false]
        "017458"         | "DE" | [false, false, false, false, false, false, false, false]
        "017459"         | "DE" | [false, false, false, false, false, false, false, false]
        "01746"          | "DE" | [false, false, false, false, false, false, false, false]
        "01747"          | "DE" | [false, false, false, false, false, false, false, false]
        "01748"          | "DE" | [false, false, false, false, false, false, false, false]
        "01749"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0175
        //
        "01750"          | "DE" | [false, false, false, false, false, false, false, false]
        "017510"         | "DE" | [false, false, false, false, false, false, false, false]
        "017511"         | "DE" | [false, false, false, false, false, false, false, false]
        "017512"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017513 is reserved for voicemail - see tests below
        "017514"         | "DE" | [false, false, false, false, false, false, false, false]
        "017515"         | "DE" | [false, false, false, false, false, false, false, false]
        "017516"         | "DE" | [false, false, false, false, false, false, false, false]
        "017517"         | "DE" | [false, false, false, false, false, false, false, false]
        "017518"         | "DE" | [false, false, false, false, false, false, false, false]
        "017519"         | "DE" | [false, false, false, false, false, false, false, false]
        "01752"          | "DE" | [false, false, false, false, false, false, false, false]
        "01753"          | "DE" | [false, false, false, false, false, false, false, false]
        "01754"          | "DE" | [false, false, false, false, false, false, false, false]
        "01755"          | "DE" | [false, false, false, false, false, false, false, false]
        "01756"          | "DE" | [false, false, false, false, false, false, false, false]
        "01757"          | "DE" | [false, false, false, false, false, false, false, false]
        "01758"          | "DE" | [false, false, false, false, false, false, false, false]
        "01759"          | "DE" | [false, false, false, false, false, false, false, false]

        //
        // 0176
        //
        "01760"          | "DE" | [false, true, false, false, false, true, false, false]
        "01761"          | "DE" | [false, true, false, false, false, true, false, false]
        "01762"          | "DE" | [false, true, false, false, false, true, false, false]
        "017630"         | "DE" | [false, true, false, false, false, true, false, false]
        "017631"         | "DE" | [false, true, false, false, false, true, false, false]
        "017632"         | "DE" | [false, true, false, false, false, true, false, false]
        // 017633 is reserved for voicemail - see tests below
        "017634"         | "DE" | [false, true, false, false, false, true, false, false]
        "017635"         | "DE" | [false, true, false, false, false, true, false, false]
        "017636"         | "DE" | [false, true, false, false, false, true, false, false]
        "017637"         | "DE" | [false, true, false, false, false, true, false, false]
        "017638"         | "DE" | [false, true, false, false, false, true, false, false]
        "017639"         | "DE" | [false, true, false, false, false, true, false, false]
        "01764"          | "DE" | [false, true, false, false, false, true, false, false]
        "01765"          | "DE" | [false, true, false, false, false, true, false, false]
        "01766"          | "DE" | [false, true, false, false, false, true, false, false]
        "01767"          | "DE" | [false, true, false, false, false, true, false, false]
        "01768"          | "DE" | [false, true, false, false, false, true, false, false]
        "01769"          | "DE" | [false, true, false, false, false, true, false, false]

        //
        // 0177
        //
        "01770"          | "DE" | [false, false, false, false, false, false, false, false]
        "01771"          | "DE" | [false, false, false, false, false, false, false, false]
        "01772"          | "DE" | [false, false, false, false, false, false, false, false]
        "01773"          | "DE" | [false, false, false, false, false, false, false, false]
        "01774"          | "DE" | [false, false, false, false, false, false, false, false]
        "01775"          | "DE" | [false, false, false, false, false, false, false, false]
        "01776"          | "DE" | [false, false, false, false, false, false, false, false]
        "01777"          | "DE" | [false, false, false, false, false, false, false, false]
        "01778"          | "DE" | [false, false, false, false, false, false, false, false]
        "017790"         | "DE" | [false, false, false, false, false, false, false, false]
        "017791"         | "DE" | [false, false, false, false, false, false, false, false]
        "017792"         | "DE" | [false, false, false, false, false, false, false, false]
        "017793"         | "DE" | [false, false, false, false, false, false, false, false]
        "017794"         | "DE" | [false, false, false, false, false, false, false, false]
        "017795"         | "DE" | [false, false, false, false, false, false, false, false]
        "017796"         | "DE" | [false, false, false, false, false, false, false, false]
        "017797"         | "DE" | [false, false, false, false, false, false, false, false]
        "017798"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017799 is reserved for voicemail - see tests below

        //
        // 0178
        //
        "01780"          | "DE" | [false, false, false, false, false, false, false, false]
        "01781"          | "DE" | [false, false, false, false, false, false, false, false]
        "01782"          | "DE" | [false, false, false, false, false, false, false, false]
        "01783"          | "DE" | [false, false, false, false, false, false, false, false]
        "01784"          | "DE" | [false, false, false, false, false, false, false, false]
        "01785"          | "DE" | [false, false, false, false, false, false, false, false]
        "01786"          | "DE" | [false, false, false, false, false, false, false, false]
        "01787"          | "DE" | [false, false, false, false, false, false, false, false]
        "01788"          | "DE" | [false, false, false, false, false, false, false, false]
        "017890"         | "DE" | [false, false, false, false, false, false, false, false]
        "017891"         | "DE" | [false, false, false, false, false, false, false, false]
        "017892"         | "DE" | [false, false, false, false, false, false, false, false]
        "017893"         | "DE" | [false, false, false, false, false, false, false, false]
        "017894"         | "DE" | [false, false, false, false, false, false, false, false]
        "017895"         | "DE" | [false, false, false, false, false, false, false, false]
        "017896"         | "DE" | [false, false, false, false, false, false, false, false]
        "017897"         | "DE" | [false, false, false, false, false, false, false, false]
        "017898"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017899 is reserved for voicemail - see tests below

        //
        // 0179
        //
        "01790"          | "DE" | [false, false, false, false, false, false, false, false]
        "01791"          | "DE" | [false, false, false, false, false, false, false, false]
        "01792"          | "DE" | [false, false, false, false, false, false, false, false]
        "017930"         | "DE" | [false, false, false, false, false, false, false, false]
        "017931"         | "DE" | [false, false, false, false, false, false, false, false]
        "017932"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017933 is reserved for voicemail - see tests below
        "017934"         | "DE" | [false, false, false, false, false, false, false, false]
        "017935"         | "DE" | [false, false, false, false, false, false, false, false]
        "017936"         | "DE" | [false, false, false, false, false, false, false, false]
        "017937"         | "DE" | [false, false, false, false, false, false, false, false]
        "017938"         | "DE" | [false, false, false, false, false, false, false, false]
        "017939"         | "DE" | [false, false, false, false, false, false, false, false]
        "01794"          | "DE" | [false, false, false, false, false, false, false, false]
        "01795"          | "DE" | [false, false, false, false, false, false, false, false]
        "01796"          | "DE" | [false, false, false, false, false, false, false, false]
        "01797"          | "DE" | [false, false, false, false, false, false, false, false]
        "01798"          | "DE" | [false, false, false, false, false, false, false, false]
        "01799"          | "DE" | [false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German Mobile 17 range with voicemail infix"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [numberUntilInfix + "000000",
                                  numberUntilInfix + "0000000",
                                  numberUntilInfix + "00000000",
                                  numberUntilInfix + "000000000",
                                  numberUntilInfix + "999999",
                                  numberUntilInfix + "9999999",
                                  numberUntilInfix + "99999999",
                                  numberUntilInfix + "999999999"]

        Boolean[] expectedResults;

        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html
        // x: 6 length 8 otherwise 7
        if (numberUntilInfix.startsWith("0176")) {
            expectedResults = [false, false, true, false,
                               false, false, true, false]
        }

       expectedResults = [false, true, false, false,
                          false, true, false, false]


        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        numberUntilInfix | regionCode | expectingFails
        // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // especially https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mobile%20Dienste/Nummernplan-2018-03-02.pdf?__blob=publicationFile&v=1
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line len of 7 - 8 digits denping on x=6

        //
        // 0170
        //
        "017013"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0171
        //
        "017113"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0172
        //
        "017250"         | "DE" | [false, true, false, false, false, true, false, false]
        "017255"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0173
        //
        "017350"         | "DE" | [false, true, false, false, false, true, false, false]
        "017355"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0174
        //
        "017450"         | "DE" | [false, true, false, false, false, true, false, false]
        "017455"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0175
        //
        "017513"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0176
        //
        "017633"         | "DE" | [true, false, true, false, true, false, true, false]
        //
        // 0177
        //
        "017799"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0178
        //
        "017899"         | "DE" | [false, false, true, false, false, false, true, false]
        //
        // 0179
        //
        "017933"         | "DE" | [false, false, true, false, false, false, true, false]
    }

    def "check if original lib fixed isValid for German ServiceNumbers 180 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false,
                                     true, true, true,
                                     false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        //  Numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0180/Nummernplan0180_ServiceDiensteRufnummer.pdf?__blob=publicationFile&v=1
        //  points out, that national numbers have 10 (3+7) digits in this range, but that there are historically shorter numbers
        //  At https://data.bundesnetzagentur.de/Bundesnetzagentur/SharedDocs/ExterneLinks/DE/Sachgebiete/Telekommunikation/Nummerierung/NVMwD.0180.Rufnummer.Vergeben.zip it can be checked, that shorter numbers have 3+5 & 3+6 digits
        // 01800 is reserve
        "01801"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        "01802"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        "01803"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        "01804"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        "01805"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        "01806"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        "01807"           | "DE" | [false, false, false, false, false, false, false, true, true, true, true, false]
        // 01808 is reserve
        // 01809 is reserve
    }

    def "check if original lib fixed isValid for German reserve 180 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        //  Numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0180/Nummernplan0180_ServiceDiensteRufnummer.pdf?__blob=publicationFile&v=1
        //  points out, that national numbers have 10 (3+7) digits in this range, but that there are historically shorter numbers
        //  At https://data.bundesnetzagentur.de/Bundesnetzagentur/SharedDocs/ExterneLinks/DE/Sachgebiete/Telekommunikation/Nummerierung/NVMwD.0180.Rufnummer.Vergeben.zip it can be checked, that shorter numbers have 3+5 & 3+6 digits
        // reserve:

        "01800"          | "DE" | [false, false, false, false, true, true, true, true, true, true, true]
        "01808"          | "DE" | [false, false, false, false, true, true, true, true, true, true, true]
        "01809"          | "DE" | [false, false, false, false, true, true, true, true, true, true, true]

    }

    def "check if original lib fixed isValid for German international VPN 181 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false,
                                     true, true, true, true, true, true, true, true, false]

        when:
        PhoneNumberUtil.ValidationResult[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  0181 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0181/181_node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0181/Nummernplan_IVPN.pdf?__blob=publicationFile&v=1
        //  nation number with 14 digits
        "0181"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49181"         | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
    }

    def "check if original lib fixed isValid for German VPN 18(2-9) range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, true, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  018 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/Nummernplan.pdf?__blob=publicationFile&v=1
        //  Historical Reorder: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/TWiderruf.pdf?__blob=publicationFile&v=1
        //  nation number with 11 digits
        "0182"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0183"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0184"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0185"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0186"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0187"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0188"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
        "0189"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German VPN 18(2-9) range which is only reachable nationally"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  018 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/Nummernplan.pdf?__blob=publicationFile&v=1
        //  Historical Reorder: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/TWiderruf.pdf?__blob=publicationFile&v=1
        //  nation number with 11 digits
        "+49182"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49183"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49184"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49185"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49186"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49187"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49188"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]
        "+49189"           | "FR" | [false, false, false, false, false, false, false, false, true, false, false, false]

    }

    def "check if original lib fixed isValid for German VPN 018 59995 xxxx is reachable"(String reserve, regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "2",
                                  reserve + "22",
                                  reserve + "223",
                                  reserve + "2233",
                                  reserve + "22334",
                                  reserve + "223344"]

        Boolean[] expectedResults = [false, false, false, false, true, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  018 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/Nummernplan.pdf?__blob=publicationFile&v=1
        //  Historical Reorder: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/TWiderruf.pdf?__blob=publicationFile&v=1
        //  nation number with 11 digits
        "018 59995"      | "DE" | [false, false, false, false, false, false, false]
        "+4918 59995"    | "FR" | [false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German Online Services 019(1-4) inc. historic"(String reserve, historic,regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "2",
                                  reserve + "22",
                                  reserve + "223",
                                  reserve + "2233",
                                  reserve + "22334"]

        Boolean[] expectedResults
        if (historic) {
            expectedResults = [false,
                               true,  // BnetzA mentioned historic numbers are 4 digits long
                               true,  // TODO: BnetzA only mentioned historic 4 digit numbers, but since we found 6 digit in use, we asume the gab with 5 digits should be possible
                               true,  // At TDG (Deutsche Telekom Germany) we are using historic 0191 range with a 6 digit number
                               false,
                               false]
        } else {
            expectedResults = [false,
                               false,
                               false,
                               true,  // BnetzA specified just 6 digits for current numbers
                               false,
                               false]
        }


        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | historic | regionCode | expectingFails
        //  019(1-4) is Online Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/019xyz/019xyz_node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/019xyz/019_Nummernplan.pdf?__blob=publicationFile&v=1
        //  while currently only 019(2-4) is used, there are historically 019(1-3) allocations with other structure.
        //  are those services dead? https://www.teltarif.de/internet/by-call/
        //  Deutsche Telekom still offers 0191011 see https://www.telekom.de/hilfe/festnetz-internet-tv/anschluss-verfuegbarkeit/anschlussvarianten/festnetz-internet/einwahlnummern-internetzugang-aus-dem-ausland?samChecked=true
        //  that is historically a 0191 range, but not limit to 4 digits but using 6!
        //  Vodafone Germany is offering 0192070 see https://www.vodafone.de/media/downloads/pdf/090512_Preisliste_Vodafone_Festnetz.pdf
        //  Historical: 4 to 6
        "0191"      | true     | "DE" | [false, true, true, true, false, false]
        "0192"      | true     | "DE" | [false, true, true, true, false, false]
        "0193"      | true     | "DE" | [false, true, true, true, false, false]
        "+49191"    | true     | "FR" | [false, true, true, true, false, false]
        "+49192"    | true     | "FR" | [false, true, true, true, false, false]
        "+49193"    | true     | "FR" | [false, true, true, true, false, false]
        //  current: 6 digits
        "0194"      | false    | "DE" | [false, false, false, true, false, false]
        "+49194"    | false    | "FR" | [false, false, false, true, false, false]

    }

    def "check if original lib fixed isValid for German traffic routing 01981 of mobile Emergency calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
        given:
        // starting with 22 is giving a real number  - 3344 is area code for Bad Freienwalde
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

        Boolean[] expectedResults
        if ((operator) && (regionCode == "DE")) {
            expectedResults = [false, false, false, false, false, false, false, false,
                               true,  // not callable public, but for national operators
                               true,  // not callable public, but for national operators
                               true,  // not callable public, but for national operators
                               true,  // not callable public, but for national operators
                               false]
        } else {
            expectedResults = [false, false, false, false, false, false, false, false,
                               false,  // not callable public, but for national operators
                               false,  // not callable public, but for national operators
                               false,  // not callable public, but for national operators
                               false,  // not callable public, but for national operators
                               false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | operator | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01981 is used for emergency call routing from national mobile operators and are not callable by normal public telephony network users nor by international operators
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it could be checked if A is 2..5 and B is 1..3 (see own test below)
        //  additionally only valid NDCs see below could also be checked but that would be more a IsValid check
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01981"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false, false]
        "01981"     | true     | "DE" | [false, false, false, false, false, false, false, false, true, true, true, true, false]
        "+491981"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false, false, false]
        "+491981"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German traffic routing 01981xx of mobile Emergency calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
        given:
        // 2233 is are code of Hürth
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

        Boolean[] expectedResults
        if ((operator) && (regionCode == "DE")) {
            expectedResults = [false, false, false, false, false, false,
                               true,  // not callable public, but for national operators
                               true,  // not callable public, but for national operators
                               true,  // not callable public, but for national operators
                               true,  // not callable public, but for national operators
                               false]
        } else {
            expectedResults = [false, false, false, false, false, false,
                               false,  // not callable public, but for national operators
                               false,  // not callable public, but for national operators
                               false,  // not callable public, but for national operators
                               false,  // not callable public, but for national operators
                               false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | operator | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01981 is used for emergency call routing from national mobile operators and are not callable by normal public telephony network users nor by international operators
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it is checked if A is 2..5 and B is 1..3 - just for DE, for other countries it is INVALID Length which is tested by first 01981 test
        //  additionally only valid NDCs see below could also be checked but that would be more a IsValid check
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it

        // Telekom Deutschland GmbH
        "0198121"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198121"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198122"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198122"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198123"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198123"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        // Vodafone GmbH
        "0198131"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198131"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198132"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198132"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198133"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198133"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        // Telefónica Germany GmbH & Co. OHG
        "0198141"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198141"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198142"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198142"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198143"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198143"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        // Telefónica Germany GmbH & Co. OHG
        "0198151"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198151"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198152"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198152"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]
        "0198153"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "0198153"     | true     | "DE" | [false, false, false, false, false, false, true, true, true, true, false]

        "+49198121"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198121"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198122"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198122"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198123"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198123"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "+49198131"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198131"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198132"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198132"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198133"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198133"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "+49198141"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198141"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198142"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198142"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198143"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198143"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "+49198151"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198151"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198152"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198152"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198153"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49198153"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German invalid traffic routing 01981xx of mobile Emergency calls"(String reserve, regionCode, boolean[] expectingFails) {
        given:
        // 2233 is are code of Hürth
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve      | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01981 is used for emergency call routing from national mobile operators and are not callable by normal public telephony network users nor by international operators
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it is checked for non A is 2..5 and B is 1..3 - just for DE, for other countries it is INVALID Length which is tested by first 01981 test
        //  no distinguishing of user and operator needed because those ranges are INVALID for both.

        "0198100"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198101"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198102"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198103"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198104"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198105"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198106"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198107"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198108"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198109"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198110"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198111"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198112"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198113"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198114"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198115"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198116"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198117"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198118"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198119"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198120"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        // 1..3 are valid
        "0198124"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198125"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198126"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198127"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198128"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198129"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198130"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        // 1..3 are valid
        "0198134"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198135"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198136"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198137"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198138"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198139"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198140"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        // 1..3 are valid
        "0198144"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198145"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198146"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198147"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198148"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198149"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198150"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        // 1..3 are valid
        "0198154"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198155"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198156"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198157"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198158"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198159"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198160"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198161"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198162"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198163"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198164"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198165"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198166"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198167"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198168"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198169"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198170"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198171"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198172"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198173"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198174"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198175"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198176"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198177"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198178"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198179"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198180"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198181"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198182"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198183"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198184"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198185"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198186"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198187"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198188"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198189"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]

        "0198190"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198191"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198192"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198193"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198194"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198195"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198196"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198197"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198198"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
        "0198199"    | "DE"       | [false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German traffic routing 01982 of Emergency calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults
        if ((operator)) {
            expectedResults = [false, false, false,
                               true, // not callable public, but for operators
                               true, // not callable public, but for operators
                               true, // not callable public, but for operators
                               true, // not callable public, but for operators
                               true, // not callable public, but for operators
                               false, false, false]
        } else {
            expectedResults = [false, false, false,
                               false,  // not callable public, but for operators
                               false,  // not callable public, but for operators
                               false,  // not callable public, but for operators
                               false,  // not callable public, but for operators
                               false,  // not callable public, but for operators
                               false, false, false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | operator | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01982 is used for emergency call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it could be checked if A is 2..5 and B is 1..3 (see own test below)
        //  additionally only valid NDCs see below could also be checked but that would be more a IsValid check
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01982"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "01982"     | true     | "DE" | [false, false, false, true, true, true, true, true, false, false, false]
        "+491982"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        "+491982"   | true     | "FR" | [false, false, false, true, true, true, true, true, false, false, false]
    }

    def "check if original lib fixed isValid for German traffic routing 01986 of public service calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "1",
                                  reserve + "11",
                                  reserve + "115",
                                  reserve + "1151",
                                  reserve + "11511",
                                  reserve + "115111",
                                  reserve + "222",
                                  reserve + "333",
                                  reserve + "444",
                                  reserve + "555",
                                  reserve + "666",
                                  reserve + "777",
                                  reserve + "888",
                                  reserve + "999",
                                  reserve + "000"]

        Boolean[] expectedResults
        if ((operator)) {
            expectedResults = [false, false, false,
                               true, // not callable public, but for operators
                               false, false, false,
                               false, false, false, false, false, false, false, false, false]
        } else {
            expectedResults = [false, false, false,
                               false, // not callable public, but for operators
                               false, false, false,
                               false, false, false, false, false, false, false, false, false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | operator | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01986 is used for public service call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01986-115
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01986"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
        "01986"     | true     | "DE" | [false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false]
        "+491986"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
        "+491986"   | true     | "FR" | [false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German traffic routing 01987 of EU public service calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "0",
                                  reserve + "00",
                                  reserve + "000",
                                  reserve + "0000",
                                  reserve + "00000",
                                  reserve + "000000",
                                  reserve + "9",
                                  reserve + "99",
                                  reserve + "999",
                                  reserve + "9999",
                                  reserve + "99999",
                                  reserve + "999999"]

        Boolean[] expectedResults
        if ((operator)) {
            expectedResults = [false, false, false,
                               true, // not callable public, but for operators
                               false, false, false,
                               false, false,
                               true, // not callable public, but for operators
                               false, false, false]
        } else {
            expectedResults = [false, false, false,
                               false, // not callable public, but for operators
                               false, false, false,
                               false, false,
                               false, // not callable public, but for operators
                               false, false, false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | operator | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01987 is used for EU public service call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01987-xyz
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01987"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false, false]
        "01987"     | true     | "DE" | [false, false, false, true, false, false, false, false, false, true, false, false, false]
        "+491987"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false, false, false]
        "+491987"   | true     | "FR" | [false, false, false, true, false, false, false, false, false, true, false, false, false]
    }

    def "check if original lib fixed isValid for German traffic routing 01988 for international free calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "0",
                                  reserve + "00",
                                  reserve + "000",
                                  reserve + "0000",
                                  reserve + "00000",
                                  reserve + "000000",
                                  reserve + "9",
                                  reserve + "99",
                                  reserve + "999",
                                  reserve + "9999",
                                  reserve + "99999",
                                  reserve + "999999"]

        Boolean[] expectedResults
        if ((operator)) {
            expectedResults = [false, false,
                               true, // not callable public, but for operators
                               false, false, false, false,
                               false,
                               true, // not callable public, but for operators
                               false, false, false, false]
        } else {
            expectedResults = [false, false,
                               false, // not callable public, but for operators
                               false, false, false, false,
                               false,
                               false, // not callable public, but for operators
                               false, false, false, false]
        }

        when:
        PhoneNumberUtil.ValidationResult[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve     | operator | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01988 is used for EU public service call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01988-xx TODO: verify called number information is transfered outside the number (no digits after xx)
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01988"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "01988"     | true     | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491988"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491988"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
    }

    def "check if original lib fixed isValid for German traffic routing 01989 for Call Assistant"(String number, boolean Operator, regionCode, expectedResult, expectingFail) {
        given:
        // Operator is currently not usable in original methods (just a preparation)
        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"

        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number     | Operator    | regionCode | expectedResult       | expectingFail
        // traffic routing is described in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/118xy/118xyNummernplan.pdf?__blob=publicationFile&v=1

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | true        | "DE"       | false                | false
        "019890"   | true        | "DE"       | false                | false
        "0198900"  | true        | "DE"       | false                | false
        "01989000" | true        | "DE"       | true                 | true  // not callable public, but for operators
        "019890000"| true        | "DE"       | false                | false
        "019891"   | true        | "DE"       | false                | false
        "0198910"  | true        | "DE"       | true                 | true  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | true        | "DE"       | true                 | true  // not callable public, but for operators
        "01989100" | true        | "DE"       | false                | false
        "019899"   | true        | "DE"       | false                | false
        "0198999"  | true        | "DE"       | true                 | true  // not callable public, but for operators
        "01989999" | true        | "DE"       | false                | false

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | false       | "DE"       | false                | false
        "019890"   | false       | "DE"       | false                | false
        "0198900"  | false       | "DE"       | false                | false
        "01989000" | false       | "DE"       | false                | false  // not callable public, but for operators
        "019890000"| false       | "DE"       | false                | false
        "019891"   | false       | "DE"       | false                | false
        "0198910"  | false       | "DE"       | false                | false  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | false       | "DE"       | false                | false  // not callable public, but for operators
        "01989100" | false       | "DE"       | false                | false
        "019899"   | false       | "DE"       | false                | false
        "0198999"  | false       | "DE"       | false                | false  // not callable public, but for operators
        "01989999" | false       | "DE"       | false                | false

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | true        | "DE"       | false                | false
        "019890"   | true        | "DE"       | false                | false
        "0198900"  | true        | "DE"       | false                | false
        "01989000" | true        | "DE"       | true                 | true  // not callable public, but for operators
        "019890000"| true        | "DE"       | false                | false
        "019891"   | true        | "DE"       | false                | false
        "0198910"  | true        | "DE"       | true                 | true  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | true        | "DE"       | true                 | true  // not callable public, but for operators
        "01989100" | true        | "DE"       | false                | false
        "019899"   | true        | "DE"       | false                | false
        "0198999"  | true        | "DE"       | true                 | true  // not callable public, but for operators
        "01989999" | true        | "DE"       | false                | false

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | false       | "DE"       | false                | false
        "019890"   | false       | "DE"       | false                | false
        "0198900"  | false       | "DE"       | false                | false
        "01989000" | false       | "DE"       | false                | false  // not callable public, but for operators
        "019890000"| false       | "DE"       | false                | false
        "019891"   | false       | "DE"       | false                | false
        "0198910"  | false       | "DE"       | false                | false  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | false       | "DE"       | false                | false  // not callable public, but for operators
        "01989100" | false       | "DE"       | false                | false
        "019899"   | false       | "DE"       | false                | false
        "0198999"  | false       | "DE"       | false                | false  // not callable public, but for operators
        "01989999" | false       | "DE"       | false                | false

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "+491989"    | true        | "FR"     | false                | false
        "+4919890"   | true        | "FR"     | false                | false
        "+49198900"  | true        | "FR"     | false                | false
        "+491989000" | true        | "FR"     | false                | false
        "+4919890000"| true        | "FR"     | false                | false
        "+4919891"   | true        | "FR"     | false                | false
        "+49198910"  | true        | "FR"     | false                | false
        // Call Assistant of Deutsche Telekom
        "+49198933"  | true        | "FR"     | false                | false
        "+491989100" | true        | "FR"     | false                | false
        "+4919899"   | true        | "FR"     | false                | false
        "+49198999"  | true        | "FR"     | false                | false
        "+491989999" | true        | "FR"     | false                | false

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "+491989"    | false       | "FR"     | false                | false
        "+4919890"   | false       | "FR"     | false                | false
        "+49198900"  | false       | "FR"     | false                | false
        "+491989000" | false       | "FR"     | false                | false
        "+4919890000"| false       | "FR"     | false                | false
        "+4919891"   | false       | "FR"     | false                | false
        "+49198910"  | false       | "FR"     | false                | false
        // Call Assistant of Deutsche Telekom
        "+49198933"  | false       | "FR"     | false                | false
        "+491989100" | false       | "FR"     | false                | false
        "+4919899"   | false       | "FR"     | false                | false
        "+49198999"  | false       | "FR"     | false                | false
        "+491989999" | false       | "FR"     | false                | false
    }

    def "check if original lib fixed isValid for German traffic routing 0199 for internal traffic routing"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults
        // TODO: Assumed 0199 is only valid within a German Operator network
        if ((operator) && regionCode == "DE") {
            expectedResults = [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                               true, true, true, true, true, true, true, true, true, true]
        } else {
            expectedResults = [false, false, false, false, false, false, false, false, false, false, false, false,
                               false, false, false, false, false, false, false, false, false, false, false, false,
                               false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve    | operator | regionCode | expectingFails
        //  0199 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  0199 is not further ruled, so assuming ITU rule of max length 15 with no lower limit, but operator only use
        "0199"     | false    | "DE" | [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
        "0199"     | true     | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+49199"   | false    | "FR" | [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
        "+49199"   | true     | "FR" | [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German personal 700 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, true, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  0700 is personal number range: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0700/0700_node.html
        //  it has 8-digit long numbers TODO: unclear if those numbers may only be called within Germany (no country code example)
        //  but general numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        //  indicates it is callable from outside Germany

        "0700"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49700"         | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49700"         | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German free call 800 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, true, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  0800 is personal number range: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0800/0800_node.html
        //  it has 7-digit long numbers TODO: unclear if those numbers may only be called within Germany (no country code example)
        //  but general numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        //  indicates it is callable from outside Germany
        //  numbers could be extended, but that it up to carrier support and might not be supported see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0800/0800_Nummernplan.pdf?__blob=publicationFile&v=1
        //  TODO: Need to check if extended numbers should be marked somehow-possible

        "0800"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49800"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49800"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]
    }

    def "check if original lib fixed isValid for German free call 900 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, true, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  0900x is premium number range: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0900/start.html
        //  it has 6-digit long numbers TODO: unclear if those numbers may only be called within Germany (no country code example)
        //  see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0900/0900_NummernplanMit.pdf?__blob=publicationFile&v=1
        //  but general numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        //  indicates it is callable from outside Germany

        // TODO start: by Dec 1st of 2024 the ranges 9000 till 09008 will be possible for premium service
        // Information
        "09001"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+499001"         | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+499001"         | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        // Entertaining
        "09003"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+499003"         | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+499003"         | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
        // everything else
        "09005"           | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+499005"         | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+499005"         | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German test numbers 031x range"(String reserve, regionCode, boolean[] expectingFails, boolean reserverange) {
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

        Boolean[] expectedResults
        if ((reserverange) || (regionCode != "DE")) {
            expectedResults = [false, false, false, false, false, false, false, false, false, false, false]
        } else {
            // if +49 is even not an option inside germany, then this would be IS_POSSIBLE_LOCAL_ONLY
            // bit currently it seems it is part of general numberplan with +49  https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
            expectedResults = [true, false, false, false, false, false, false, false, false, false, false]
        }

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | reserverange | regionCode | expectingFails
        //  031 is personal number range: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/031/031_node.html
        //  it has onl one digit (0 or 1)
        //  https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/031/Zutregel.pdf?__blob=publicationFile&v=1
        //  not callable from outside germany
        //  TODO: check if inside Germany it is reachable via +49
        //  TODO: Check if those test numbers are dropped when no preselection (010x) is possible anymore

        "0310"           | false | "DE" | [true, false, false, false, false, false, false, false, false, false, false]
        "+49310"         | false | "DE" | [true, false, false, false, false, false, false, false, false, false, false]
        "+49310"         | false | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0311"           | false | "DE" | [true, false, false, false, false, false, false, false, false, false, false]
        "+49311"         | false | "DE" | [true, false, false, false, false, false, false, false, false, false, false]
        "+49311"         | false | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0312"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49312"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49312"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0313"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49313"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49313"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0314"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49314"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49314"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0315"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49315"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49315"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0316"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49316"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49316"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0317"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49317"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49317"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0318"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49318"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49318"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]

        "0319"           | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49319"         | true | "DE" | [false, false, false, false, false, false, false, false, false, false, false]
        "+49319"         | true | "FR" | [false, false, false, false, false, false, false, false, false, false, false]
    }

    def "check if original lib fixed isValid for German personal numbers 032 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, true, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  032 is personal number range:https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/032/032_node.html
        //  only a view blocks are currently in use https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/032/Zuteilungsregeln032NationaleTeilnehmerrufnummern.pdf?__blob=publicationFile&v=1

        // (0)32210 is not usable for now

        "032211"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932211"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932211"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032212"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932212"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932212"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032213"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932213"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932213"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032214"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932214"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932214"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032215"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932215"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932215"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032216"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932216"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932216"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032217"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932217"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932217"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032218"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932218"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932218"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032219"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932219"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932219"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        // (0)32220 is not usable for now

        "032221"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932221"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932221"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032222"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932222"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932222"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032223"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932223"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932223"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032224"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932224"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932224"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032225"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932225"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932225"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032226"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932226"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932226"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032227"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932227"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932227"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032228"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932228"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932228"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]

        "032229"           | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932229"         | "DE" | [false, false, false, false, true, false, false, true, true, false, false]
        "+4932229"         | "FR" | [false, false, false, false, true, false, false, true, true, false, false]
    }

    def "check if original lib fixed isValid for German personal numbers 032 range - low level reserve"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  032 is personal number range:https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/032/032_node.html
        //  only a view blocks are currently in use https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/032/Zuteilungsregeln032NationaleTeilnehmerrufnummern.pdf?__blob=publicationFile&v=1

        "032210"           | "DE" | [false, false, false, false, true, false, true, true, true, false, false]
        "+4932210"         | "DE" | [false, false, false, false, true, false, true, true, true, false, false]
        "+4932210"         | "FR" | [false, false, false, false, true, false, true, true, true, false, false]

        "032220"           | "DE" | [false, false, false, false, true, false, true, true, true, false, false]
        "+4932220"         | "DE" | [false, false, false, false, true, false, true, true, true, false, false]
        "+4932220"         | "FR" | [false, false, false, false, true, false, true, true, true, false, false]
    }

    def "check if original lib fixed isValid for German personal numbers 032 range - mid level reserve"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  032 is personal number range:https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/032/032_node.html
        //  only a view blocks are currently in use https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/032/Zuteilungsregeln032NationaleTeilnehmerrufnummern.pdf?__blob=publicationFile&v=1

        "03220"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493220"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493220"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        // (0)3221xyyy is in use see above
        // (0)3222xyyy is in use see above

        "03223"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493223"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493223"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        "03224"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493224"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493224"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        "03225"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493225"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493225"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        "03226"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493226"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493226"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        "03227"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493227"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493227"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        "03228"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493228"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493228"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]

        "03229"           | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493229"         | "DE" | [false, false, false, false, false, true, false, true, true, true, false]
        "+493229"         | "FR" | [false, false, false, false, false, true, false, true, true, true, false]
    }

    def "check if original lib fixed isValid for German personal numbers 032 range - high level reserve"(String reserve, regionCode, boolean[] expectingFails) {
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

        Boolean[] expectedResults = [false, false, false, false, false, false, false, false, false, false, false]

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResults[i], expectingFails[i], numbersToTest[i], regionCode)
        }

        where:
        reserve          | regionCode | expectingFails
        //  032 is personal number range:https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/032/032_node.html
        //  only a view blocks are currently in use https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/032/Zuteilungsregeln032NationaleTeilnehmerrufnummern.pdf?__blob=publicationFile&v=1

        "0320"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49320"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49320"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0321"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49321"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49321"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        // (0)322 is checked in middle level test see above

        "0323"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49323"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49323"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0324"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49324"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49324"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0325"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49325"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49325"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0326"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49326"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49326"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0327"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49327"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49327"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0328"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49328"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49328"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]

        "0329"           | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49329"         | "DE" | [false, false, false, false, false, false, false, false, true, true, true]
        "+49329"         | "FR" | [false, false, false, false, false, false, false, false, true, true, true]
    }

    def "check if original lib fixed isValid for German explicit drama numbers"(String testnumber, regionCode, boolean expectingFail) {
        given:
        String[] numbersToTest = [testnumber]
        Boolean expectedResult = false

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail, numbersToTest[i], regionCode)
        }

        where:
        testnumber          | regionCode | expectingFail
        //  there are some drama numbers definded in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/mittlg148_2021.pdf?__blob=publicationFile&v=1

        "0152 28817386"     | "DE"       | true
        "+49152 28817386"   | "DE"       | true
        "+49152 28817386"   | "FR"       | true

        "0152 28895456"     | "DE"       | true
        "+49152 28895456"   | "DE"       | true
        "+49152 28895456"   | "FR"       | true

        "0152 54599371"     | "DE"       | true
        "+49152 54599371"   | "DE"       | true
        "+49152 54599371"   | "FR"       | true

        "0172 9925904"     | "DE"       | true
        "+49172 9925904"   | "DE"       | true
        "+49172 9925904"   | "FR"       | true

        "0172 9968532"     | "DE"       | true
        "+49172 9968532"   | "DE"       | true
        "+49172 9968532"   | "FR"       | true

        "0172 9973185"     | "DE"       | true
        "+49172 9973185"   | "DE"       | true
        "+49172 9973185"   | "FR"       | true

        "0172 9973186"     | "DE"       | true
        "+49172 9973186"   | "DE"       | true
        "+49172 9973186"   | "FR"       | true

        "0172 9980752"     | "DE"       | true
        "+49172 9980752"   | "DE"       | true
        "+49172 9980752"   | "FR"       | true

        "0174 9091317"     | "DE"       | true
        "+49174 9091317"   | "DE"       | true
        "+49174 9091317"   | "FR"       | true

        "0174 9464308"     | "DE"       | true
        "+49174 9464308"   | "DE"       | true
        "+49174 9464308"   | "FR"       | true
    }

    def "check if original lib fixed isValid for German 2 digit drama number range"(String testnumber, regionCode, boolean expectingFail) {
        given:
        ArrayList<String> numbersToTest = []

        for (int i1=0; i1<10; i1++) {
            for (int i2=0; i2<10; i2++) {
                String s = testnumber + String.valueOf(i1) + String.valueOf(i2)
                numbersToTest.add(s)
            }
        }

        Boolean expectedResult = false

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail, numbersToTest[i], regionCode)
        }

        where:
        testnumber          | regionCode | expectingFail
        //  there are some drama number ranges defined in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/mittlg148_2021.pdf?__blob=publicationFile&v=1

        "0171 39200"     | "DE"       | true
        "+49171 39200"   | "DE"       | true
        "+49171 39200"   | "FR"       | true

        "0176 040690"     | "DE"       | true
        "+49176 040690"   | "DE"       | true
        "+49176 040690"   | "FR"       | true
    }

    def "check if original lib fixed isValid for German 3 digit drama number range"(String testnumber, regionCode, boolean expectingFail) {
        given:
        ArrayList<String> numbersToTest = []

        for (int i1=0; i1<10; i1++) {
            for (int i2=0; i2<10; i2++) {
                for (int i3=0; i3<10; i3++) {
                    String s = testnumber + String.valueOf(i1) + String.valueOf(i2) + String.valueOf(i3)
                    numbersToTest.add(s)
                }
            }
        }

        Boolean expectedResult = false

        when:
        Boolean[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        then:
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail, numbersToTest[i], regionCode)
        }

        where:
        testnumber          | regionCode | expectingFail
        //  there are some drama number ranges defined in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/mittlg148_2021.pdf?__blob=publicationFile&v=1

        "030 23125"     | "DE"       | true
        "+4930 23125"   | "DE"       | true
        "+4930 23125"   | "FR"       | true

        "069 90009"     | "DE"       | true
        "+4969 90009"   | "DE"       | true
        "+4969 90009"   | "FR"       | true

        "040 66969"     | "DE"       | true
        "+4940 66969"   | "DE"       | true
        "+4940 66969"   | "FR"       | true

        "0221 4710"     | "DE"       | true
        "+49221 4710"   | "DE"       | true
        "+49221 4710"   | "FR"       | true

        "089 99998"     | "DE"       | true
        "+4989 99998"   | "DE"       | true
        "+4989 99998"   | "FR"       | true
    }

    /*
           invalid NDC test has been split into an own file,
           because we reach the 64kB Size limit when adapting them: groovyjarjarasm.asm.MethodTooLargeException
     */

    def "check if original lib fixed isValid for invalid German NDC 010 - 02999"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [number + "",
                                  number + "5",
                                  number + "55",
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]


        when:
        "get number isValid: $number"
        Boolean[] results = []
        for (n in numbersToTest) {
            def phoneNumber = phoneUtil.parse(n, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        then:
        "is number expected: $expectedResult"
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail[i], numbersToTest[i], regionCode)
        }


        where:

        number  | regionCode | expectedResult | expectingFail
        // short numbers which are reached internationally are also registered as NDC
        // TODO: 010 is operator selection see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/010/010xy_node.html ... will be canceled 31.12.2024
        "010"   | "DE"       | false          | false
        // ---
        // 0110 is checked in Emergency short codes see above
        // ---
        "0111"  | "DE"       | false          | false
        // ---
        // 0112 is checked in Emergency short codes see above
        // ---
        "0113"  | "DE"       | false          | false
        "0114"  | "DE"       | false          | false
        // ---
        // 0115 is checked in German Government short codes see above
        // ---
        // ---
        // 0116 is checked in EU social short codes see above
        // ---
        "0117"  | "DE"       | false          | false
        // ---
        // 0118 is checked in German call assistant services see above
        // ---
        "0119"  | "DE"       | false          | false
        "012"   | "DE"       | false          | false
        "0120"  | "DE"       | false          | false
        "0121"  | "DE"       | false          | false
        "0122"  | "DE"       | false          | false
        "0123"  | "DE"       | false          | false
        "0124"  | "DE"       | false          | false
        "0125"  | "DE"       | false          | false
        "0126"  | "DE"       | false          | false
        "0127"  | "DE"       | false          | false
        "0128"  | "DE"       | false          | false
        "0129"  | "DE"       | false          | false
        "0130"  | "DE"       | false          | false
        "0131"  | "DE"       | false          | false
        "0132"  | "DE"       | false          | false
        "0133"  | "DE"       | false          | false
        "0134"  | "DE"       | false          | false
        "0135"  | "DE"       | false          | false
        "0136"  | "DE"       | false          | false
        // ---
        // 0137 is checked in Mass Traffic see above
        // ---
        "0138"  | "DE"       | false          | [false, false, false, false, true, false, false, false, false]
        "0139"  | "DE"       | false          | false
        "014"   | "DE"       | false          | false
        "0140"  | "DE"       | false          | false
        "0141"  | "DE"       | false          | false
        "0142"  | "DE"       | false          | false
        "0143"  | "DE"       | false          | false
        "0144"  | "DE"       | false          | false
        "0145"  | "DE"       | false          | false
        "0146"  | "DE"       | false          | false
        "0147"  | "DE"       | false          | false
        "0148"  | "DE"       | false          | false
        "0149"  | "DE"       | false          | false
        // ---
        // 015x is checked in Mobile 15 and 15 voicemail see above
        // ---
        // ---
        // 016x:
        // 0160, 0162, 0163 are checked in Mobile 16 and 16 voicemail
        // 0161, 0165, 0166, 0167 are checked in Reserve 16
        // 0168, 0169 are checked in eMessage 16 - see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Funkruf/start.html and https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/np_nummernraum.pdf?__blob=publicationFile&v=1
        // TODO: 0164 eMessage length definition needed
        // ---
        // ---
        // 017x is checked in Mobile 17 and 17 voicemail see above
        // ---
        // ---
        // 0180 is checked in Service Numbers 0180 and its reserve
        // ---
        // ---
        // 0181 is checked in international VPN 0181
        // ---
        // ---
        // 018(2-9) is checked in German national VPN 018(2-9) range and that it is only reachable nationally
        // ---
        "0190"  | "DE"       | false          | false  // Reserve - previously premium rate numbers, which were relocated to 0900
        // ---
        // 019(1-4) is checked in German Online Services 019(1-4) inc. historic
        // ---
        "0195"  | "DE"       | false          | false  // Reserve
        "0196"  | "DE"       | false          | false  // Reserve
        "0197"  | "DE"       | false          | false  // Reserve
        // ---
        // Traffic management numbers are only valid between operators - so not for end customers to call
        // ---
        "01980" | "DE"       | false          | false  // Reserve
        // ---
        // 01981 is checked in German traffic routing 01981 of mobile Emergency calls
        // ---
        // ---
        // 01982 is checked in German traffic routing 01982 for emergency calls
        // ---
        "01983" | "DE"       | false          | false  // Reserve
        "01984" | "DE"       | false          | false  // Reserve
        "01985" | "DE"       | false          | false  // Reserve
        // ---
        // 01986 is checked in German traffic routing 01986 for public service calls 115
        // ---
        // ---
        // 01987 is checked in German traffic routing 01987 for public EU service calls 116xyz
        // ---
        // ---
        // 01988 is checked in German traffic routing 01988 for international freecalls
        // ---
        // ---
        // 01989 is checked in Assistant Service Routing
        // ---
        // ---
        // 0199 is checked in operator internal network traffic routing
        // ---
        // invalid area code for germany - using Invalid_Lenth, because its neither to long or short, but just NDC is not valid.
        "0200"  | "DE"       | false          | false
        // 0201 is Essen
        // 0202 is Wuppertal
        // 0203 is Duisburg
        "02040" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02041 is Bottrop
        "02042" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02043 is Gladbeck
        "02044" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02045 is Bottrop-Kirchhellen
        "02046" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02047" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02048" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02049" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02050" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02051 till 02054 are in use
        "02055" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02056 is Heiligenhausen
        "02057" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02058 is Wülfrath
        "02059" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02060" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02061" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02062" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02063" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02064 till 02066 is in use
        "02067" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02068" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02069" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "0207"  | "DE"       | false          | false
        // 0208 & 0209 is in use
        "02100" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02101" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02102 till 02104 is in use
        "02105" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02106" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02107" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02108" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02109" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // special case 0212 for Solingen also covers 02129 for Haan Rheinl since Solingen may not use numbers starting with 9
        "02130" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02131 till 02133 is in use
        "02134" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02135" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02136" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02137 is Neuss-Norf
        "02138" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02139" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0214 is Leverkusen
        // 02150 till 02154 is in use
        "02155" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02156 till 02159 is in use
        "02160" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02161 till 02166 is in use
        "02167" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02168" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02169" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02170" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02171 is Leverkusen-Opladen
        "02172" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02173 till 02175 is in use
        "02176" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02177" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02178" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02179" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02180" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02181 till 02183 is in use
        "02184" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02185" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02186" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02187" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02188" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02189" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02190" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02191 till 02193 is in use
        "02194" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02195 till 02196 is in use
        "02197" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02198" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02199" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02200" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02201" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02202 till 02208 is in use
        "02209" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0221 is Köln
        "02220" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02221" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02222 till 02228 is in use
        "02229" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02230" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02231" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02232 till 02238 is in use
        "02239" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02240" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02241 till 02248 is in use
        "02249" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02250" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02251 till 02257 is in use
        "02258" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02259" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02260" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02261 till 02269 is in use
        "02270" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02271 till 02275 is in use
        "02276" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02277" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02278" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02279" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0228 is Bonn
        "02290" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02291 till 02297 is in use
        "02298" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02299" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02300" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02301 till 02309 is in use
        // 0231 is Dortmund
        "02320" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02321" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02322" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02323 till 02325 is in use
        "02326" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02327 is Bochum-Wattenscheid
        "02328" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02329" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02330 till 02339 is in use
        // 0234 is Bochum
        "02350" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02351 till 02355 is in use
        "02356" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02357 till 02358 is in use
        // 02360 till 02369 is in use
        "02370" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02371 till 02375 is in use
        "02376" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02377 till 02379 is in use
        "02380" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02381 till 02385 is in use
        "02386" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02387 till 02389 is in use
        "02390" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02391 till 02395 is in use
        "02396" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02397" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02398" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02399" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02400" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02401 till 02409 is in use
        // 0241 is Aachen
        "02420" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02421 till 02429 is in use
        "02430" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02431 till 02436 is in use
        "02437" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02438" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02439" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02440 till 02441 is in use
        "02442" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02443 till 02449 is in use
        "02450" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02451 till 02456 is in use
        "02457" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02458" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02459" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02460" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02461 till 02465 is in use
        "02466" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02467" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02468" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02469" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02470" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02471 till 02474 is in use
        "02475" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02476" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02477" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02478" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02479" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02480" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02481" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02482 is Hellenthal
        "02483" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02484 till 02486 is in use
        "02487" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02488" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02489" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "0249"  | "DE"       | false          | false
        "02500" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02501 till 02502 is in use
        "02503" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02504 till 02509 is in use
        // 0251 is Münster
        // 02520 till 02529 is in use
        "02530" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02531" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02532 till 02536 is in use
        "02531" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02538 is Drensteinfurt-Rinkerode
        "02539" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02540" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02541 till 02543 is in use
        "02544" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02545 till 02548 is in use
        "02549" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02550" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02551 till 02558 is in use
        "02559" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02560" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02561 till 02568 is in use
        "02569" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02570" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02571 till 02575 is in use
        "02576" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02577" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02578" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02579" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02580" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02581 till 02588 is in use
        "02589" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02590 till 02599 is in use
        "02600" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02601 till 02608 is in use
        "02609" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0261 is Koblenz am Rhein
        // 02620 till 02628 is in use
        "02629" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02630 till 02639 is in use
        "02640" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02641 till 02647 is in use
        "02648" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02649" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02650" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02651 till 02657 is in use
        "02658" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02659" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02660" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02661 till 02664 is in use
        "02665" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02666 till 02667 is in use
        "02668" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02669" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02670" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02671 till 02678 is in use
        "02679" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02680 till 02689 is in use
        "02690" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02691 till 02697 is in use
        "02698" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02699" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0271 is Siegen
        "02720" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02721 till 02725 is in use
        "02726" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02727" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02728" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02729" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02730" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02731" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02731 till 02739 is in use
        "02740" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02741 till 02745 is in use
        "02746" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02747 is Molzhain
        "02748" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02749" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02750 till 02755 is in use
        "02756" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02757" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02758 till 02759 is in use
        "02760" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02761 till 02764 is in use
        "02765" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02766" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02767" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02768" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02769" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02770 till 02779 is in use
        "02780" | "DE"       | false          | false
        // 02781 till 02784 is in use
        "02785" | "DE"       | false          | false
        "02786" | "DE"       | false          | false
        "02787" | "DE"       | false          | false
        "02788" | "DE"       | false          | false
        "02789" | "DE"       | false          | false
        "0279"  | "DE"       | false          | false
        "02790" | "DE"       | false          | false
        "02791" | "DE"       | false          | false
        "02792" | "DE"       | false          | false
        "02793" | "DE"       | false          | false
        "02794" | "DE"       | false          | false
        "02795" | "DE"       | false          | false
        "02796" | "DE"       | false          | false
        "02797" | "DE"       | false          | false
        "02798" | "DE"       | false          | false
        "02799" | "DE"       | false          | false
        "02800" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02801 till 02804 is in use
        "02805" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02806" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02807" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02808" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02809" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0281 is Wesel
        "02820" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02821 till 02828 is in use
        "02829" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02830" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02831 till 02839 is in use
        "02840" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02841 till 02845 is in use
        "02846" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02847" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02848" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02849" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02850 till 02853 is in use
        "02854" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02855 till 02859 is in use
        "02860" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02861 till 02867 is in use
        "02868" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02869" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02870" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02871 till 02874 is in use
        "02875" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02876" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02877" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02878" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02879" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "0288"  | "DE"       | false          | false
        "0289"  | "DE"       | false          | false
        "02900" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02901" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02902 till 02905 is in use
        "02906" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02907" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02908" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02909" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 0291 is Meschede
        "02920" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02921 till 02925 is in use
        "02926" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02927 till 02928 is in use
        "02929" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02930" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02931 till 02935 is in use
        "02936" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02937 till 02938 is in use
        "02939" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02940" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02941 till 02945 is in use
        "02946" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02947 till 02948 is in use
        "02949" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02950" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02951 till 02955 is in use
        "02956" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02957 till 02958 is in use
        "02959" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02960" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02961 till 02964 is in use
        "02965" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02966" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02967" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02968" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02969" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02970" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02971 till 02975 is in use
        "02976" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02977 is Schmallenberg-Bödefeld
        "02978" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02979" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02980" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02981 till 02985 is in use
        "02986" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02987" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02988" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02989" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02990" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        // 02991 till 02994 is in use
        "02995" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02996" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02997" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02998" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
        "02999" | "DE"       | false          | [false, false, true, true, true, true, true, true, true]
    }

    def "check if original lib fixed isValid for invalid German NDC 030 - 039999"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [number + "",
                                  number + "5",
                                  number + "55",
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]


        when: "get number isValid: $number"
        Boolean[] results = []
        for (n in numbersToTest) {
            def phoneNumber = phoneUtil.parse(n, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        then: "is number expected: $expectedResult"
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail[i], numbersToTest[i], regionCode)
        }


        where:

        number               | regionCode | expectedResult  | expectingFail
        // 030 is Berlin
        // ---
        // 0310 is checked in German test numbers 031x
        // 0311 is checked in German test numbers 031x
        // 0312 till 0319 is also checked in German test numbers 031x - TODO: by end of 2024 Call By Call is disabled in Germany, to be checked if Testnumbers are dropped then.
        "0312"               | "DE"       | false           | false
        "0313"               | "DE"       | false           | false
        "0314"               | "DE"       | false           | false
        "0315"               | "DE"       | false           | false
        "0316"               | "DE"       | false           | false
        "0317"               | "DE"       | false           | false
        "0318"               | "DE"       | false           | false
        "0319"               | "DE"       | false           | false
        // ---
        // ---
        // 032 is checked in multiple 032 test (due to different blocks are only in use currently) see above
        // ---
        "03300"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03301 till 03304 is in use
        "033050"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033051 till 033056 is in use
        "033057"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033058"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033059"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03306 till 03307 is in use
        // 033080 is Marienthal Kreis Oberhavel
        "033081"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033082 till 033089 is in use
        "033090"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033091"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033092"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033093 till 033094 is in use
        "033095"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033096"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033097"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033098"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033099"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 0331 is Potsdam
        // 033200 till 033209 is in use
        // 03321 is Nauen Brandenburg
        // 03322 is Falkensee
        // 033230 till 033235 is in use
        "033236"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033237 till 033239 is in use
        "03324"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03325"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03326"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03327 till 03329 is in use
        "03330"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03331 till 03332 is in use
        "033330"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033331 till 033338 is in use
        "033339"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03334 till 03335 is in use
        "033360"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033361 till 033369 is in use
        // 03337 till 03338 is in use
        "033390"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033391"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033392"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033393 till 033398 is in use
        "033399"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03340"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03341 till 03342 is in use
        "033430"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033431"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033432 till 033439 is in use
        // 03344 is Bad Freienwalde
        "033450"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033451 till 033452 is in use
        "033453"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033454 is Wölsickendorf/Wollenberg
        "033455"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033456 till 033458 is in use
        "033459"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03346 is Seelow
        // 033470 is Lietzen
        "033471"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033472 till 033479 is in use
        // 0335 is Frankfurt (Oder)
        "033600"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033601 till 033609 is in use
        // 03361 till 03362 is in use
        "033630"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033631 till 033638 is in use
        "033639"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03364 is Eisenhüttenstadt
        "033650"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033651"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033652 till 033657 is in use
        "033658"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033659"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03366 is Beeskow
        "033670"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033671 till 033679 is in use
        "03368"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03369"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "033700"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033701 till 033704 is in use
        "033705"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033706"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033707"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033708 is Rangsdorf
        "033709"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03371 till 03372 is in use
        "033730"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033731 till 033734 is in use
        "033735"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033736"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033737"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033738"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033739"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033740"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033741 till 033748 is in use
        "033749"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03375 is Königs Wusterhausen
        // 33760 is Münchehofe Kreis Dahme-Spreewald
        "033761"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033762 till 033769 is in use
        // 03377 till 03379 is in use
        "03380"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03381 till 03382 is in use
        // 033830 till 033839 is in use
        "033840"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033841 is Belzig
        "033842"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033843 till 033849 is in use
        // 03385 till 03386 is in use
        // 033870 is Zollchow bei Rathenow
        "033871"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033872 till 033878 is in use
        "033879"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03388"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03389"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03390"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03391 is Neuruppin
        // 033920 till 033929 is in use
        "033930"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033931 till 033933 is in use
        "033934"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033935"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033936"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033937"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033938"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033939"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03394 till 03395 is in use
        "033960"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033961"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033962 till 033969 is in use
        // 033970 till 033979 is in use
        "033980"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033981 till 033984 is in use
        "033985"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033986 is Falkenhagen Kreis Prignitz
        "033987"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "033988"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 033989 is Sadenbeck
        "03399"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0340 till 0341 is in use
        "034200"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034201"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034202 till 034208 is in use
        "034209"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03421 is Torgau
        "034220"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034221 till 034224 is in use
        "034225"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034226"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034227"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034228"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034229"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03423 is Eilenburg
        "034240"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034241 till 034244 is in use
        "034245"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034246"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034247"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034248"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034249"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03425 is Wurzen
        "034260"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034261 till 034263 is in use
        "03427"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03428"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "034290"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034291 till 034293 is in use
        "03430"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03431 is Döbeln
        "034320"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034321 till 034322 is in use
        "034323"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034324 till 034325 is in use
        "034326"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034327 till 034328 is in use
        "034329"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03433 is Borna Stadt
        "034340"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034341 till 034348 is in use
        "034349"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03435 is Oschatz
        "034360"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034361 till 034364 is in use
        "034365"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034366"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034367"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034368"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034369"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03437 is Grimma
        "034380"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034381 till 034386 is in use
        "034387"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034388"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034389"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03439"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03440"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03441 is Zeitz
        "034420"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034421"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034422 till 034426 is in use
        "034427"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034428"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034429"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03443 is Weissenfels Sachsen-Anhalt
        "034440"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034441 is Hohenmölsen
        "034442"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034443 till 034446 is in use
        "034447"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034448"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034449"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03445 is Naumburg Saale
        "034460"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034461 till 034467 is in use
        "034468"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034469"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03447 till 03448 is in use
        "034490"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034491 till 034498 is in use
        "034499"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 0345 is Halle Saale
        // 034600 toll 034607 is in use
        "034608"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034609 is Salzmünde
        // 03461 till 03462 is in use
        "034630"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034631"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034632 till 034633 is in use
        "034634"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034635 till 034639 is in use
        // 03464 is Sangerhausen
        "034650"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034651 till 034654 is in use
        "034655"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034656 is Wallhausen Sachsen-Anhalt
        "034657"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034658 till 034659 is in use
        // 03466 is Artern Unstrut
        "034670"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034671 till 034673 is in use
        "034674"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034675"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034676"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034677"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034678"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034679"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03468"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "034690"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034691 till 034692 is in use
        "034693"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034694"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034695"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034696"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034697"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034698"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034699"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03470"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03471 is Bernburg Saale
        "034720"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034721 till 034722 is in use
        "034723"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034724"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034725"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034726"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034727"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034728"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034729"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 3473 is Aschersleben Sachsen-Anhalt
        "034740"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034741 till 034743 is in use
        "034744"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034745 till 034746 is in use
        "034747"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034748"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034749"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03475 till 03476 is in use
        "034770"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034771 till 034776 is in use
        "034777"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034778"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034779 is Abberode
        "034780"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034781 till 034783 is in use
        "034784"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034785 is Sandersleben
        "034786"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034787"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034788"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034789"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03479"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0348"               | "DE"       | false           | false
        "034900"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034901 is Roßlau Elbe
        "034902"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034903 till 034907
        "034908"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034909 is Aken Elbe
        // 03491 till 03494 (yes full 03492x is used, too) is in use
        "034950"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034951"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034952"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034953 till 034956
        "034957"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034958"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034959"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03496 is Köthen Anhalt
        "034970"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034971"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "034972"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034973 is Osternienburg
        "034974"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 034975 till 034979 is in use
        "03498"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03499"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03500"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03501 is Pirna
        "035029"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035030"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035031"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035032 till 035033 is in use
        "035034"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035035"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035036"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035038"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035038"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035039"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03504 is Dippoldiswalde
        "035050"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035051"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035052 till 035058
        "035059"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03506"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03507"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03508"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03509"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0351 is Dresden
        // 03520x till 03525 is in use (inclusive complete 03524x)
        "035260"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035261"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035262"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035263 till 035268
        "035269"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03527"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03529 till 03529 is in use
        "03530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03531 is Finsterwalde
        "035320"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035321"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035322 till 035327
        "035328"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035329 is Dollenchen
        // 03533 is Elsterwerda
        "035340"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035341 till 035343
        "035344"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035345"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035346"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035347"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035348"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035349"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03535 is Herzberg Elster
        "035360"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035361 till 035365 is in use
        "035366"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035367"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035369"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035369"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03537 is Jessen Elster
        "035380"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035381"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035382"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035383 till 035389 is in use
        "03539"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03541 till 03542 is in use
        "035430"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035431"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035432"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035433 till 035436 is in use
        "035437"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035438"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035439 is Zinnitz
        // 03544 is Luckau Brandenburg
        "035450"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035451 till 035456 is in use
        "035457"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035458"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035459"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03546 is Lübben Spreewald
        "035470"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035471 till 035478 is in use
        "035479"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03548"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03549"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0355 is Cottbus
        // 03560x till 03564 is in use
        "03565"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03566"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03567"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03568"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "035690"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035691 till 035698 is in use
        "035699"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03570"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03571 is Hoyerswerda
        "035720"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035721"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035722 till 035728 is in use
        "035729"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03573 till 03574 is in use
        "035750"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035751 till 035756 is in use
        "035757"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035758"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035759"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03576 is Weisswasser
        "035770"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035771 till 035775 is in use
        "035776"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035777"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035778"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035779"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03578 is Kamenz
        "035790"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035791"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035792 till 035793 is in use
        "035794"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035795 till 035797 is in use
        "035798"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035799"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03580"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03581 is Görlitz
        // 035820 is Zodel
        "035821"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035822 till 035823 is in use
        "035824"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035825 till 035829 is in use
        // 03583 is Zittau
        "035840"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035841 till 035844 is in use
        "035845"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035846"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035847"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035848"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035849"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03585 till 03586 is in use
        "035870"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035871"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035872 till 035877 is in use
        "035878"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035879"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03588 is Niesky
        "035890"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035891 till 0358595 is in use
        "035896"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035897"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035898"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035899"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03590"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03591 till 03594 (including total 03593x) is in use
        "035950"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035951 till 035955 is in use
        "035956"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035957"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035958"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035959"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03596 is Neustadt in Sachsen
        "035970"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 035971 till 035975 is in use
        "035976"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035977"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035978"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "035979"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03598"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03599"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03600"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03601 till 03603 (including total 03602x) is in use
        "036040"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036041 till 036043 is in use
        "036044"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036045"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036046"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036047"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036048"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036049"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03605 till 03606 is in use
        "036070"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036071 till 036072 is in use
        "036073"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036074 till 036077 is in use
        "036078"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036079"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036080"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036081 till 036085 is in use
        "036086"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036087 is Wüstheuterode
        "036088"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036089"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03609"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0361 is Erfurt
        // 03620x till 03624 is in use
        "036250"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036251"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036252 till 036259 is in use
        "03626"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03627"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03628 till 03629 is in use
        "03630"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03631 till 03632 is in use
        // 036330 till 036338 is in use
        "036339"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03634 till 03637x is in use
        "03638"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03639"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03640"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03641 is Jena
        "036420"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036421 till 036428 is in use
        "036429"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03643 till 03644 is in use
        // 036450 till 036454 is in use
        "036455"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036456"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036457"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036458 till 036459 is in use
        "036460"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036461 till 036465 is in use
        "036466"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036467"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036468"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036469"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03647 is Pößneck
        "036480"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036481 till 036484 is in use
        "036485"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036486"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036487"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036488"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036489"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03649"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0365 is Gera
        "036600"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036601 till 036608 is in use
        "036609"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03661 is Greiz
        "036620"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036621 till 036626 is in use
        "036627"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036628 is Zeulenroda
        "036629"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03663 is Schleiz
        // 036640 is Remptendorf
        "036641"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036642 till 036649 is in use
        "036650"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036651 till 036653 is in use
        "036654"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036655"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036656"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036657"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036658"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036659"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03666"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03667"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03668"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "036690"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036691 till 036695 is in use
        "036696"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036697"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036698"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036699"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036700"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036701 till 036705 is in use
        "036706"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036707"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036708"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036709"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03671 till 03673x is in use
        "036740"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036741 till 03644 is in use
        "036745"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036746"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036747"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036748"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036749"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03675 is Heubisch
        "036760"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036761 till 036762 is in use
        "036763"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036764 is Neuhaus-Schierschnitz
        "036765"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036766 is SChalkau
        "036767"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036768"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036769"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03677 is Ilmenau Thüringen
        "036780"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036781 till 036785 is in use
        "036786"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036787"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036788"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036789"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03679 is Suhl
        "03680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03681 till 03686 (inlcuding total 03684x) is in use
        // 036870 till 036871 is in use
        "036872"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036873 till 036875 is in use
        "036876"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "036877"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036878 is Oberland
        "036879"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03690"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 036891 till 03693 (including total 036892x) is in use
        // 0368940 till 0368941 is in use
        "036942"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 0368943 till 0368949 is in use
        // 03695 is Bad Salzungen
        "036960"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 036961 till 036969 is in use
        "03697"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03698"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03699"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0370"               | "DE"       | false           | false
        // 0371 is Chemnitz Sachsen
        // 037200 is Wittgensdorf bei Chemnitz
        "037201"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037202 till 03724 is in use
        "037205"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037206 till 037209 is in use
        // 03721 till 03727 is in use
        "03728"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "037290"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037291 till 037298 is in use
        "037299"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03731 till 03733 (including total 03732x) is in use
        "037340"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037341 till 037344 is in use
        "037345"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037346 till 037349 is in use
        // 03735 till 03737 (including total 03736x) is in use
        "037380"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037381 till 037384 is in use
        "037385"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037386"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037387"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037388"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037389"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03739"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03740"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03741 is Plauen
        "037420"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037421 till 037423 is in use
        "037424"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037425"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037426"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037427"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037428"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037429"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03473x till 03745 is in use
        "037460"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037461"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037462 till 037465 is in use
        "037466"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037467 till 037468 is in use
        "037469"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03747"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03748"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03749"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0375 is Zwickau
        // 03760x till 03765 is in use
        "03766"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03767"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03768"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03769"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03770"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03771 till 03774 is in use
        "037750"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037751"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037752 is Eibenstock
        "037753"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 037754 till 037757
        "037758"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "037759"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03776"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03777"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03778"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03779"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0378"               | "DE"       | false           | false
        "0379"               | "DE"       | false           | false
        "0380"               | "DE"       | false           | false
        // 0381 is Rostock
        "038200"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038201 till 038209
        // 03821 till 03822x
        "038230"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038231 till 038234
        "038235"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038236"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038237"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038238"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038239"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03824"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03825"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03826"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03827"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03828"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "038290"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038291"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038292 till 038297 is in use
        "038298"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038299"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03830x till 03831 is in use
        // 038320 till 038328 is in use
        "038329"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038330"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 08331 till 038334 is in use
        "038335"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038336"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038337"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038338"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038339"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03834 is Greifswald
        "038350"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038351 till 038356 is in use
        "038357"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038358"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038359"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03836 till 03838 (including total 03837x) is in use
        "038390"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038391 till 038393 is in use
        "038394"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038395"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038396"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038397"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038398"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038399"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03840"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03841 id Neukloster
        "038420"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038421"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038422 till 038429
        // 03843 till 03845x is in use
        "038460"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038461 till 038462 is in use
        "038463"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038464 is Bernitt
        "038465"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038466 is Jürgenshagen
        "038467"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038468"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038469"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03847 is Sternberg
        "038480"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038481 till 038486 is in use
        "038487"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038488 is Demen
        "038489"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03849"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0385 is Schwerin
        // 03860 till 03861 is in use
        "03862"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03863 is Crivitz
        "03864"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03865 till 03869 is in use
        "03870"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03871 till  03872x is in use
        "038730"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038731 till 038733 is in use
        "038734"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038735 till 038738 is in use
        "038739"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03874 till 03877 (including total 03875x) is in use
        // 038780 till 038785 is in use
        "038786"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038787 till 038789 is in use
        "038790"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038791 till 038794
        "038795"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038796 till 038797
        "038798"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038799"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03880"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03881 is Grevesmühlen
        "038820"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038821 till 038828 is in use
        "038829"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03883 is Hagenow
        "038840"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038841 till 038845 is in use
        "038846"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038847 till 038848 is in use
        "038849"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038850 till 038856 is in use
        "038857"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038858 till 038859 is in use
        // 03886 is Gadebusch
        "038870"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 038871 till 038876 is in use
        "038877"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038878"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "038879"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03888"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03889"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0389"               | "DE"       | false           | false
        // 03900x till 03905x (including total 03903x) is in use
        "039060"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039061 till 039062 is in use
        "039063"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039064"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039065"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039066"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039067"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039068"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039069"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03907 till 03909 (including total 03908x) is in use
        // 0391 is Magdeburg
        // 03920x till 03921 is in use
        "039220"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039221 till 039226 is in use
        "039227"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039228"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039229"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03923 is Zerbst
        "039240"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039241 till 039248 is in use
        "0392498"            | "DE"       | false           | true
        // 03925 is Stassfurt
        "039260"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039261"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039262 till 039268 is in use
        "039269"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03927"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03928 is Schönebeck Elbe
        "039290"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039291 till 039298 is in use
        "039299"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "03930"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03931 is Stendal
        // 039320 till 039325 is in use
        "039326"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039327 till 039329 is in use
        // 03933 is Genthin
        "039340"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039341 till 039349 is in use
        // 03935 is Tangerhütte
        "039360"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039361 till 039366 is in use
        "039367"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039368"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039369"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03937 is Osterburg Altmark
        "039380"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039381"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039382 till 039384 is in use
        "039385"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039386 till 039389 is in use
        // total 03939x is in use
        // 03940x till 03941 is in use
        "039420"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039421 till 039428 is in use
        "039429"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03943 till 03944 is in use
        "039450"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039451 till 039459 is in use
        // 03946 till 03947 is in use
        "039480"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039481 till 039485 is in use
        "039486"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039487 till 039489 is in use
        // 03949 is Oschersleben Bode
        // 0395 is Zwiedorf
        // 039600 till 039608 is in use
        "039609"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03961 till 03969 is in use
        "03970"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03971 is Anklam
        "039720"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039721 till 039724 is in use
        "039725"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039726 till 039728 is in use
        "039729"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03973 till 03974x is in use
        "039750"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039751 till 039754 is in use
        "039755"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039756"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039757"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039758"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039759"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03976 is Torgelow bei Uckermünde
        "039770"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039771 till 039779 is in use
        "03980"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03981 to 03982x is in use
        "039830"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039831 till 039833 is in use
        "039834"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039835"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039836"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039837"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039838"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039839"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03984 is Prenzlau
        "039850"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039851 till 039859 is in use
        "039860"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039861 till 039863 is in use
        "039863"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039864"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039865"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039866"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039867"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039868"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039869"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03987 is Templin
        "039880"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039881 till 039889 is in use
        "03989"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "03990"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 03991 is Waren Müritz
        "039920"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039921 till 039929 is in use
        "039930"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039931 till 039934 is in use
        "039935"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039936"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039937"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039938"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        "039939"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03994 is Malchin
        "039950"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039951 till 039957 is in use
        "039958"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039959 is Dargun
        // 03996 is Teterow
        "039970"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039971 till 039973 is in use
        "039974"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039975 till 039978
        "039979"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 03998 is Demmin
        "039990"             | "DE"       | false           | [false, true, true, true, true, true, true, true, true]
        // 039991 till 039999 is in use
    }

    def "check if original lib fixed isValid for invalid German NDC 040 - 069"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [number + "",
                                  number + "5",
                                  number + "55",
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]


        when: "get number isValid: $number"
        Boolean[] results = []
        for (n in numbersToTest) {
            def phoneNumber = phoneUtil.parse(n, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        then: "is number expected: $expectedResult"
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail[i], numbersToTest[i], regionCode)
        }


        where:

        number               | regionCode | expectedResult  | expectingFail
        // 040 is Hamburg
        "04100"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04101 till 04109 is in use
        "0411"               | "DE"       | false           | false
        // total 0412x is in use
        "04130"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04131 till 04139 is in use
        // 04140 till 04144 is in use
        "04145"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04146 is Stade-Bützfleth
        "04147"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04148 till 04149 is in use
        "04150"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04151 till 04156 is in use
        "04157"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04158 till 04159 is in use
        "04160"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04161 till 04169 is in use
        "04170"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04171 till 04179 is in use
        // total 0418x is in sue
        "04190"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04191 till 04195 is in use
        "04196"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04197"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04198"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04199"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04200"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04201"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04202 till 04209 is in use
        // 0421 is Bremen
        "04220"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04221 till 04224 is in use
        "04225"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04226"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04227"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04228"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04229"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0423x till 0424x is in use
        "04250"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04251 till 04258 is in use
        "04259"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0426x is in use
        "04270"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04271 till 04277 is in use
        "04278"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04279"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04280"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04281 till 04289 is in use
        "04290"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04291"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04292 till 04298 is in use
        "04299"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04300"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04301"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04302 till 04303 is in use
        "04304"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04305 is Westensee
        "04306"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04307 till 04308 is in use
        "04309"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0431 till 0433x (including total 0432x) is in use
        // 04340 is Achterwehr
        "04341"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04342 till 04346 is in use
        "04350"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04351 till 04358 is in use
        "04359"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04360"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04361 till 04367 is in use
        "04368"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04369"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04370"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04371 till 04372 is in use
        "04373"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04374"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04375"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04376"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04377"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04378"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04379"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04380"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04381 till 04385 is in use
        "04386"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04387"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04388"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04389"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04390"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04391"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04392 till 04394 is in use
        "04395"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04396"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04397"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04398"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04399"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04400"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04401 till 04409 is in use
        // 0441 is Oldenburg (Oldb)
        "04420"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04421 till 04423 is in use
        "04424"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04425 till 04426 is in use
        "04427"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04428"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04429"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04430"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04431 till 04435 is in use
        "04436"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04437"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04438"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04439"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04440"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04441 till 04447 is in use
        "04448"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04449"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04450"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04451 till 04456 is in use
        "04457"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04458 is Wiefeldstede-Spohle
        "04459"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04460"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04461 till 04469 is in use
        "04470"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04471 till 04475 is in use
        "04476"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04477 till 04479 is in use
        // total 0448x is in use
        "04490"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04491 till 1199 is in use
        "04500"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04501 till 04506 is in use
        "04507"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04508 till 0459 is in use
        // 0451 is Lübeck
        "04520"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04521 till 04529 is in use
        "04530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04531 till 04537 is in use
        "04538"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04539 is Westerau
        "04540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04541 till 04547 is in use
        "04548"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04549"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0455x is in use
        "04560"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04561 till 04564 is in use
        "0457"               | "DE"       | false           | false
        "0458"               | "DE"       | false           | false
        "0459"               | "DE"       | false           | false
        "04600"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04601"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04602 till 04609 is in use
        // 0461 is Flensburg
        "04620"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04621 till 04627 is in use
        "04628"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04629"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0463x is in use
        "04640"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04641 till 04644 is in use
        "04645"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04646 is Morkirch
        "04647"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04648"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04649"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04650"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04651 is Sylt
        "04652"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04653"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04654"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04655"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04656"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04657"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04658"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04659"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04660"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04661 till 04668 is in use
        "04669"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04670"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04671 till 04674 is in use
        "04675"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04676"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04677"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04678"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04679"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04681 till 04684 is in use
        "04685"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04686"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04687"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04701"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04702 till 04708 is in use
        "04709"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0471 is Bremerhaven
        "04720"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04721 till 04725 is in use
        "04726"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04727"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04728"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04729"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04731 till 04737 is in use
        "04738"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04739"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0474x is in use
        "04750"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04751 till 04758 is in use
        "04759"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04760"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04761 till 04769 is in use
        // total 0477x is in use
        "0478"               | "DE"       | false           | false
        "04790"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04791 till 04796 is in use
        "04800"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04801"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04802 till 04806 is in use
        "04807"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04808"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04809"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0481 is Heide Holstein
        "04820"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04821 till 04829 is in use
        // 04830 is Süderhastedt
        "04831"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04832 till 04839 is in use
        "04840"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04841 till 04849 os in use
        "04850"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04851 till 04859 is in use
        "04860"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04861 till 04865 is in use
        "04866"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04867"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04868"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04869"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04870"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04871 till 04877 is in use
        "04878"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04879"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04880"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04881 till 04885 is in use
        "04886"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04887"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04888"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04889"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04890"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04891"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04892 till 04893 is in use
        "04894"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04895"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04896"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04897"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04898"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04899"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04900"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04901"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 04902 till 04903 is in use
        "04904"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04905"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04906"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04907"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04908"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "04909"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0491 is Leer Ostfriesland
        // total 0492x is in use
        "04930"              | "DE"       | false           | [false, false, true, false, false, false, false, false, false]
        // 04931 till 04936 is in use
        "04937"              | "DE"       | false           | [false, false, true, true, false, false, false, false, false]
        // 04938 till 04939 is in use
        "04940"              | "DE"       | false           | [false, false, true, false, false, false, false, false, false]
        // 04941 till 04948 is in use
        "04949"              | "DE"       | false           | [false, false, true, true, false, false, false, false, false]
        // total 0495x is in use
        "04960"              | "DE"       | false           | [false, false, true, true, false, false, false, false, false]
        // 04961 till 04968 is in use
        "04969"              | "DE"       | false           | [false, false, true, false, false, false, false, false, false]
        "04970"              | "DE"       | false           | [false, false, true, true, false, false, false, false, false]
        // 04971 till 04977 is in use
        "04978"              | "DE"       | false           | [false, false, true, true, false, false, false, false, false]
        "04979"              | "DE"       | false           | [false, false, true, true, false, false, false, false, false]
        "0498"               | "DE"       | false           | false
        "0499"               | "DE"       | false           | false
        "0500"               | "DE"       | false           | false
        "0501"               | "DE"       | false           | false
        "05020"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05021 till 05028 is in use
        "05029"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05030"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05031 till 05037 is in use
        "05038"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05039"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05040"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05041 till 05045 is in use
        "05046"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05047"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05048"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05049"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05050"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05051 till 05056 is in use
        "05057"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05058"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05058"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05060 is Bodenburg
        "05061"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05062 till 05069 is in use
        "05070"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05071 till 05074 is in use
        "05075"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05076"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05077"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05078"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05079"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05080"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05081"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05082 till 05086 is in use
        "05087"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05088"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05089"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0509"               | "DE"       | false           | false
        "05100"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05101 till 05103 is in use
        "05104"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05105 is Barsinghausen
        "05106"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05107"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05108 till 05109 is in use
        // 0511 is Hannover
        "05120"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05121 is Hildesheim
        "05122"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05123 is Schellerten
        "05124"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05125"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05126 till 05129 is in use
        // 05130 till 05132 is in use
        "05133"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05134"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05135 till 05139 is in use
        "05140"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05141 till 05149 is in use
        "05150"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05151 till 05159 is in use
        "05160"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05161 till 05168 is in use
        "05169"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05170"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05171 till 05177 is in use
        "05178"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05179"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05180"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05181 till 05187 is in use
        "05188"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05189"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0519x is in use
        "05200"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05201 till 05209 is in use
        // 0521 is Bielefeld
        "05220"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05221 till 05226 is in use
        "05227"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05228 is Vlotho-Exter
        "05229"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05230"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05231 till 05238 is in use
        "05239"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05240"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05241 till 0522 is in use
        "05243"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05244 till 05248 is in use
        "05249"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05250 till 05255 is in use
        "05256"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05257 till 05259 is in use
        "05260"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05261 till 05266 is in use
        "05267"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05268"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05269"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05270"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05271 till 05278 is in use
        "05279"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05280"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05281 till 05286 is in use
        "05287"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05288"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05289"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05290"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05291"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05292 till 05295 is in use
        "05296"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05297"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05298"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05299"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0530x is in use
        // 0531 is Braunschweig
        // total 0532x is in use
        "05330"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05331 till 05337 is in use
        "05338"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05339 is Gielde
        "05340"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05341 is Salzgitter
        "05342"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05343"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05344 till 05347 is in use
        "05348"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05349"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05350"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05351 till 05358 is in use
        "05359"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05360"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05361 till 05368 is in use
        "05369"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05370"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05371 till 05379 is in use
        "05380"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05381 till 05384 is in use
        "05385"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05386"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05387"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05388"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05389"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0539"               | "DE"       | false           | false
        "05400"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05401 till 05407 is in use
        "05408"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05409 is Hilter am Teutoburger Wald
        // 0541 Osnabrück
        "05420"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05421 till 05429 is in use
        "05430"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05431 till 05439 is in use
        "05440"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05441 till 05448 is in use
        "05449"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05450"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05451 till 05459 is in use
        "05460"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05461 till 05462 is in use
        "05463"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05464 till 05468 is in use
        "05469"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05470"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05471 till 05476 is in use
        "05477"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05478"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05479"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05480"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05481 till 05485 is in use
        "05486"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05487"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05488"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05489"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05490"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05491 till 05495 is in use
        "05496"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05497"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05498"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05499"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05500"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05501"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05502 till 05509 is in use
        // 0551 is Göttingen
        // 05520 till 05525 is in use
        "05526"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05527 till 05529 is in use
        "05530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05531 till 05536 is in use
        "05537"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05538"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05539"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05541 till 05546 is in use
        "05547"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05548"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05549"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05550"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05551 till 05556 is in use
        "05557"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05558"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05559"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05560"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05561 till 05565 is in use
        "05566"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05567"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05568"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05569"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05570"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05571 till 05574 is in use
        "05575"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05576"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05577"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05578"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05579"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05580"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05581"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05582 till 05586 is in use
        "05587"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05588"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05589"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05590"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05591"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05592 till 05594 is in use
        "05595"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05596"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05597"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05598"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05599"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05600"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05601 till 05609 is in use
        // 0561 is Kassel
        "05620"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05621 till 05626 is in use
        "05627"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05628"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05629"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05630"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05631 till 05636 is in use
        "05637"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05638"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05639"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05640"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05641 till 05648 is in use
        "05649"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0565x is in use
        "05660"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05661 till 05665 is in use
        "05666"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05667"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05668"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05669"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05670"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05671 till 05677 is in use
        "05678"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05679"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05681 till 05686
        "05687"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05690"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05691 till 05696 is in use
        "05697"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05698"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05699"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05701"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05702 till 05707 is in use
        "05708"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05709"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0571 is Minden Westfalen
        "05720"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05721 till 05726 is in use
        "05727"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05728"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05729"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05731 till 05734 is in use
        "05735"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05736"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05737"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05738"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05739"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05740"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05741 till 05746 is in use
        "05747"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05748"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05749"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05750"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05751 till 05755 is in use
        "05756"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05757"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05758"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05759"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05760"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05761 is Stolzenau
        "05762"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05763 till 05769 is in use
        "05770"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05771 till 05777 is in use
        "05778"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05779"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0578"               | "DE"       | false           | false
        "0579"               | "DE"       | false           | false
        "05800"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05801"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05802 till 05808 is in use
        "05809"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0581 is Uelzen
        // total 0582x is in use
        "05830"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05831 till 05839 is in use
        // 05840 till 05846 is in use
        "05847"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05848 till 05849 is in use
        // 05850 till 05855 is in use
        "05856"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05857 till 05859 is in use
        "05860"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05861 till 05865 is in use
        "05866"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05867"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05868"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05869"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05870"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05871"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 5872 till 5875 is in use
        "05876"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05877"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05878"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05879"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05880"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05881"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05882 till 05883 is in use
        "05884"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05885"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05886"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05887"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05888"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05889"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0589"               | "DE"       | false           | false
        "05900"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05901 till 05909 is in use
        // 0591 is Lingen (ems)
        "05920"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05921 till 05926 is in use
        "05927"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05928"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05929"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05930"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05931 till 05937 is in use
        "05938"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05939 is Sustrum
        "05940"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05941 till 05948 is in use
        "05949"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05950"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05951 till 05957 is in use
        "05958"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05959"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05960"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05961 till 05966 is in use
        "05967"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05968"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05969"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "05970"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05971 is Rheine
        "05972"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05973 is Neuenkirchen Kreis Steinfurt
        "05974"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 05975 till 05978 is in use
        "05979"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0598"               | "DE"       | false           | false
        "0599"               | "DE"       | false           | false
        "06000"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06001"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06002 till 06004 is in use
        "06005"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06006"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06007 till 06008 is in use
        "06009"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0601"               | "DE"       | false           | false
        // 06020 till 06024 is in use
        "06025"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06026 till 06029 is in use
        "06030"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06031 till 06036 is in use
        "06037"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06038"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06039 is Karben
        "06040"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06041 till 06049 is in use
        // total 0605x is in use
        "06060"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06061 till 06063 is in use
        "06064"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06065"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06066 is Michelstadt-Vielbrunn
        "06067"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06068 is Beerfelden
        "06070"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06071 is Dieburg
        "06072"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06073 till 06074 is in use
        "06075"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06076"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06077"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06078 is Gross-Umstadt
        "06079"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06080"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06081 till 06087 is in use
        "06088"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06089"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06090"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06091"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06092 till 06096 is in use
        "06097"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06098"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06099"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06100"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06101 till 06109 is in use
        // 0611 is Wiesbaden
        // 06120 is Aarbergen
        "06121"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06122 till 06124 is in use
        "06125"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06126 till 06129 is in use
        // 06130 till 06136 is in use
        "06137"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06138 till 06139 is in use
        "06140"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06141"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06142 is Rüsselsheim
        "06143"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06144 till 06147 is in use
        "06148"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06149"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06150 till 06152 is in use
        "06153"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06154 till 06155 is in use
        "06156"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06157 till 06159 is in use
        "06160"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06161 till 06167 is in use
        "06168"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06169"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06170"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06171 till 06175 is in use
        "06176"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06177"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06178"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06179"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06180"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06181 till 06188 is in use
        "06189"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06190 is Hattersheim am Main
        "06191"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06192 is Hofheim am Taunus
        "06193"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06194"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06195 till 06196 is in use
        "06197"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06198 is Eppstein
        "06199"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06200"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06201 till 06207 is in use
        "06208"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06209 is Mörlenbach
        // 0621 is Mannheim
        // 06220 till 06224 is in use
        "06225"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06226 till 06229 is in use
        "06230"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06231 till 06239 is in use
        "06240"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06241 till 06247 is in use
        "06248"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06249 is Guntersblum
        "06250"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06251 till 06258 is in use
        "06259"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06260"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06261 till 06269 is in use
        "06270"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06271 till 06272 is in use
        "06273"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06274 till 06276 is in use
        "06277"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06278"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06279"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06280"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06281 till 06287 is in use
        "06288"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06289"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06290"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06291 till 06298 is in use
        "06299"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06300"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06301 till 06308 is in use
        "06309"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0631 is Kauserslautern
        "06320"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06321 till 06329 is in use
        "06330"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06331 till 06339 is in use
        // total 0634x is in use
        "06350"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06351 till 06353 is in use
        "06354"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06355 till 06359 is in use
        "06360"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06361 till 06364 is in use
        "06365"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06366"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06367"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06368"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06369"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06370"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06371 till 06375 is in use
        "06376"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06377"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06378"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06379"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06380"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06381 till 06837 is in use
        "06388"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06389"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06390"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06391 till 06398 is in use
        "06399"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0640x till 0642x is in use
        // 06431 till 06436 is in use
        "06437"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06438 till 06439 is in use
        // total 0644x is in use
        "06450"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06451 till 06458 is in use
        "06459"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06460"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06461 till 06462 is in use
        "06463"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06464 till 06468 is in use
        "06469"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06470"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06471 till 06479 is in use
        "06480"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06481"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06482 till 06486 is in use
        "06487"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06488"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06489"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0649"               | "DE"       | false           | false
        // 0650x till 0651 is in use
        "06520"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06521"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06522 till 06527 is in use
        "06528"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06529"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06531 till 06536 is in use
        "06537"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06538"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06539"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06541 till 06545 is in use
        "06546"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06547"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06548"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06549"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0655x is in use
        "06560"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06561 till 06569 is in use
        "06570"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06571 till 06575 is in use
        "06576"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06577"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06578 is Salmtal
        "06579"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0658x is in use
        "06590"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06591 till 06597 is in use
        "06598"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06599 is Wiedenbach bei Gerolstein
        "0660"               | "DE"       | false           | false
        // 0661 till 0662x is in use
        // 06630 till 06631 is in use
        "06632"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06633 till 06639 is in use
        "06640"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06641 till 06648 is in use
        "06649"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0665x is in use
        // 06660 till 06661 is in use
        "06662"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06663 till 06669 is in use
        // 06670 is Ludwigsau Hessen
        "06671"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06672 till 06678 is in use
        "06679"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06681 till 06684 is in use
        "06685"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06686"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06687"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06690"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06691 till 06698 is in use
        "06699"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06701 is Sprendlingen Rheinhessen
        "06702"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06703 till 06704 is in use
        "06705"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06706 till 06709 is in use
        // 0671 is Bad Kreuznach
        "06720"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06721 till 06728 is in use
        "06729"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06731 till 06737 is in use
        "06738"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06739"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06740"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06741 till 06747 is in use
        "06748"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06749"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06750"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06751 till 06758 is in use
        "06759"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06760"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06761 till 06766 is in use
        "06767"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06768"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06769"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06770"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06771 till 06776 is in use
        "06777"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06778"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06779"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06780"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06781 to 06789 is in use
        "0679"               | "DE"       | false           | false
        "06800"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06801"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06802 till 06806 is in use
        "06807"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06808"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06809 is Grossrosseln
        // 0681 is Saarbrücken
        "06820"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06821 is Neunkirchen Saar
        "06822"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06823"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06824 till 06827 is in use
        "06828"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06829"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06830"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06831 till 06838 is in use
        "06839"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06840"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06841 till 06844 is in use
        "06845"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06846"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06847"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06848 till 06849 is in use
        "06850"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06851 till 06858 is in use
        "06859"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06860"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06861 is Merzig
        "06862"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06863"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06864 till 06869 is in use
        "06870"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06871 till 06876 is in use
        "06877"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06878"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06879"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06880"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06881 is Lebach
        "06882"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06883"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06884"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06885"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06886"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06887 rill 06888 is in use
        "06889"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06890"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06891"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06892"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06893 till 06894 is in use
        "06895"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "06896"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 06897 till 06898 is in use
        "06899"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 069 is Frankfurt am Mai
    }

    def "check if original lib fixed isValid for invalid German NDC 0700 - 0999"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [number + "",
                                  number + "5",
                                  number + "55",
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]


        when: "get number isValid: $number"
        Boolean[] results = []
        for (n in numbersToTest) {
            def phoneNumber = phoneUtil.parse(n, regionCode)
            results += phoneUtil.isValidNumber(phoneNumber)
        }

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        then: "is number expected: $expectedResult"
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail[i], numbersToTest[i], regionCode)
        }


        where:

        number               | regionCode | expectedResult  | expectingFail
        // ---
        // 0700 is checked in personal number 0700 see above
        // ---
        "0701"               | "DE"       | false           | false
        "07020"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 7021 till 7026 is in use
        "07027"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07028"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07029"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07030"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07031 till 07034 is in use
        "07035"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07036"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07037"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07038"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07039"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07040"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07041 till 07046 is in use
        "07047"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07048"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07049"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07050"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07051 till 07056 is in use
        "07057"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07058"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07059"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07060"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07061"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07062 till 07063 is in use
        "07064"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07065"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07066 is Bad Rappenau-Bonfeld
        "07067"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07068"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07069"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07070"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07071 till 07073 is in use
        "07074"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07075"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07076"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07077"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07078"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07079"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07080"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07081 till 07085 is in use
        "07086"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07087"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07088"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07089"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0709"               | "DE"       | false           | false
        "0710"               | "DE"       | false           | false
        // 0711 is Stuttgart
        "07120"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07121 till 07129 is in use
        // 07130 till 07136 is in use
        "07137"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07138 till 07139 is in use
        "07140"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07141 till 07148 is in use
        "07149"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07150"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07150 till 07154 is in use
        "07155"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07156 till 07159 is in use
        "07160"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07161 till 07166 is in use
        "07167"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07168"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07169"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07170"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07171 till 07176 is in use
        "07177"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07178"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07179"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07180"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07181 till 07184 is in use
        "07185"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07186"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07187"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07188"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07189"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07190"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07191 till 07195
        "07196"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07197"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07198"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07199"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07200"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07201"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07202 till 07204 is in use
        "07205"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07206"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07207"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07208"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07209"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0721 is Karlsbad
        // total 0722x is in use
        "07230"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07231 till 07237 is in use
        "07238"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07239"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07240 is Pfinztal
        "07241"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07242 till 07249 is in use
        // 0725x till 0726x is in use
        "07270"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07271 till 07277 is in use
        "07278"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07279"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0728"               | "DE"       | false           | false
        "0729"               | "DE"       | false           | false
        // 07300 is Roggenburg
        "07301"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0732 till 0739 is in use
        // 0731 is Ulm Donau
        "07320"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07321 till 07329 is in use
        "07330"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07331 till 07337 is in use
        "07338"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07339"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07340 is Neenstetten
        "07341"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07342"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07343 till 07348 is in use
        "07349"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07350"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07351 till 07358 is in use
        "07359"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07360"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07361 till 07367 is in use
        "07368"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07369"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07370"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07371 is Riedlingen Württemberg
        "07372"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07373 till 07376 is in use
        "07377"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07378"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07379"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07380"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07381 till 07389 is in use
        "07390"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07391 till 07395 is in use
        "07396"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07397"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07398"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07399"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07400"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07401"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07402 till 07404 is in use
        "07405"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07406"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07407"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07408"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07409"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0741 is Deisslingen
        // 07420 is Schramberg
        // 07421 till 07429 is in use
        "07430"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07431 till 07436 is in use
        "07437"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07438"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07439"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0744x is in use
        "07450"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07451 till 07459 is in use
        "07460"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07461 till 07467 is in use
        "07468"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07469"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07470"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07471 till 07478 is in use
        "07479"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07480"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07481"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07482 till 07486 is in use
        "07487"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07488"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07489"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0749"               | "DE"       | false           | false
        "07500"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07501"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07502 till 07506 is in use
        "07507"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07508"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07509"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0751 Ravensburg
        // 07520 is Bodnegg
        "07521"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07522 is Wangen im Allgäu
        "07523"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07524 till 07525 is in use
        "07526"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07527 till 07529 is in use
        "07530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07531 till 07534 is in use
        "07535"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07536"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07537"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07538"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07539"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07541 till 07546 is in use
        "07547"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07548"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07549"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07550"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07551 till 07558 is in use
        "07559"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07560"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07561 till 07569 is in use
        // total 0757x is in use
        "07580"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07581 till 07587 is in use
        "07588"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07589"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0759"               | "DE"       | false           | false
        "07600"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07601"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07602 is Oberried Breisgau
        "07603"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07604"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07605"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07606"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07607"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07608"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07609"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0761 Freiburg im Breisgau
        // total 0762x is in use
        "07630"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07631 till 07636 is in use
        "07637"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07638"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07639"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07640"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07641 till 07646
        "07647"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07648"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07649"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07650"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07651 till 07657 is in use
        "07658"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07659"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0766x is in use
        "07670"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07671 till 07676 is in use
        "07677"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07678"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07679"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 076781 till 07685 is in use
        "07686"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07687"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0769"               | "DE"       | false           | false
        "07700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07701"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07702 till 07709 is in use
        // 0771 is Donaueschingen
        // total 0772x is in use
        "07730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07731 till 07736 is in use
        "07737"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07738 till 07339 is in use
        "07740"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07741 till 07748 is in use
        "07749"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07750"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07751 is Waldshut
        "07752"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07753 till 07755 is in use
        "07756"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07757"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07758"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07759"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07770"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07771 is Stockach
        "07772"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07773 till 07775 is in use
        "07776"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07777 is Sauldorf
        "07778"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07779"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0778"               | "DE"       | false           | false
        "0779"               | "DE"       | false           | false
        "07800"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07801"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07802 till 07808 is in use
        "07809"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0781 is Offenburg
        "07820"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07821 till 07826 is in use
        "07827"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07828"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07829"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07830"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07831 till 07839 is in use
        "07840"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07841 till 07844 is in use
        "07845"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07846"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07847"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07848"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07849"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07850"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07851 till 07854 is in use
        "07855"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07856"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07857"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07858"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07859"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0786"               | "DE"       | false           | false
        "0787"               | "DE"       | false           | false
        "0788"               | "DE"       | false           | false
        "0789"               | "DE"       | false           | false
        "07900"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07901"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07902"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07903 till 07907 is in use
        "07908"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07909"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0791 is Schwäbisch Hall
        "0792"               | "DE"       | false           | false
        // total 0793x till 0794x is in use
        // 07950 till 07955 is in use
        "07956"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07957 till 07959 is in use
        "07960"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07961 till 07967 is in use
        "07968"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07969"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07970"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 07971 till 07977 is in use
        "07978"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "07979"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0798"               | "DE"       | false           | false
        "0799"               | "DE"       | false           | false
        // ---
        // 0800 is checked with free call 800 range see above
        // ---
        "0801"               | "DE"       | false           | false
        // total 0802x is in use
        "08030"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08031 till 08036 is in use
        "08037"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08038 till 08039 is in use
        "08040"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08041 till 08043 is in use
        "08044"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08045 till 08046 is in use
        "08047"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08048"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08049"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08050"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08051 till 08057 is in use
        "08058"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08059"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08060"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08061 till 08067 is in use
        "08068"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08069"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08070"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08071 till 08076 is in use
        "08077"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08078"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08079"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08080"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08081 till 08086 is in use
        "08087"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08088"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08089"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08090"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08091 till 08095 is in use
        "08096"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08097"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08098"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08099"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08100"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08101"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08102 is Höhenkirchen-Siegertsbrunn
        "08103"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08104 till 08106 is in use
        "08107"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08108"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08109"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0811 is Halbergmoos
        "08120"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08121 till 08124 is in use
        "08125"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08126"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08127"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08128"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08129"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08130"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08131 is Dachau
        "08132"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08133 till 08139 is in use
        "08140"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08141 till 08146 is in use
        "08147"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08148"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08149"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08150"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08151 till 08153 is in use
        "08154"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08155"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08156"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08157 till 08158 is in use
        "08159"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08160"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08161 is Freising
        "08162"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08163"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08164"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08165 till 08168 is in use
        "08169"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08170 till 08171 is in use
        "08172"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08173"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08174"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08175"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08176 till 08179 is in use
        "0818"               | "DE"       | false           | false
        "08190"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08191 till 08196 is in use
        "08197"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08198"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08199"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08200"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08201"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08202 till 08208 is in use
        "08209"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0821 is Augsburg
        "08220"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08221 till 08226 is in use
        "08227"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08228"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08229"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08230 till 08234 is in use
        "08235"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08236 till 08239 is in use
        "08240"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08241 is Buchloe
        "08242"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08243 is Fuchstal
        "08244"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08245 till 08249 is in use
        // 08250 till 08254 is in use
        "08255"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08256"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08257 till 08259 is in use
        "08260"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08261 till 08263 is in use
        "08264"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08265 till 08269 is in use
        "08270"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08271 till 08274 is in use
        "08275"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08276 is Baar Schwaben
        "08277"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08278"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08279"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08280"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08281 till 08285 is in use
        "08286"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08287"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08288"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08289"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08290"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08291 till 08296 is in use
        "08297"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08298"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08299"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08300"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08301"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08302 till 08304 is in use
        "08305"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08306 is Ronsberg
        "08307"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08308"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08309"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0831 is Kempten Allgäu
        // 08320 till 08328 is in use
        "08329"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08330 till 08338 is in use
        "08339"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0834x is in use
        "0835"               | "DE"       | false           | false
        "08360"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08361 till 08369 is in use
        // 08370 is Obergünzburg
        "08371"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08372 till 08379 is in use
        // total 0838x is in use
        "08390"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08391"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08392 till 08395 is in use
        "08396"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08397"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08398"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08399"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08400"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08401"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08402 till 08407 is in use
        "08408"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08409"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0841 is Ingolstadt Donau
        "08420"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08421 till 08424 is in use
        "08425"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08426 till 08427 is in use
        "08428"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08429"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08430"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08431 till 08435 is in use
        "08436"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08437"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08438"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08439"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08440"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08441 till 08446 is in use
        "08447"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08448"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08449"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08450 is Ingoldstadt-Zuchering
        "08451"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08452 till 08454 is in use
        "08455"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08456 till 08459 is in use
        // total 0846x is in use
        "0847"               | "DE"       | false           | false
        "0848"               | "DE"       | false           | false
        "0849"               | "DE"       | false           | false
        "08500"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08501 till 08507 is in use
        "08508"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08509 is Ruderting
        // 0851 is Passau
        "0852"               | "DE"       | false           | false
        "08530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08531 till 08538 is in use
        "08539"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08541 till 08549 is in use
        // 08550 till 08558 is in use
        "08559"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08560"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08561 till 08565 is in use
        "08566"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08567"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08568"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08569"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08570"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08571 till 08574 is in use
        "08575"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08576"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08577"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08578"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08579"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08580"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08581 till 08586 is in use
        "08587"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08588"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08589"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08590"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08591 till 08593 is in use
        "08594"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08595"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08596"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08597"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08598"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08599"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0860"               | "DE"       | false           | false
        // 0861 is Traunstein
        "08620"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08621 till 08624 is in use
        "08625"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08626"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08627"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08628 till 08629 is in use
        // 08630 till 08631 is in use
        "08632"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08633 till 08639 is in use
        // 08640 till 08642 is in use
        "08643"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08644"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08645"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08646"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08647"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08648"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08649 is Schleching
        // 08650 till 08652 is in use
        "08653"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08654 Freilassing
        "08655"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08656 till 08657 is in use
        "08658"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08659"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08660"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08661 till 08667 is in use
        "08668"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08669 is Traunreut
        // 08670 till 08671 is in use
        "08672"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08673"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08674"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08675"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08676"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08677 till 086779 is in use
        "08680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08681 till 08687 is in use
        "08688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0869"               | "DE"       | false           | false
        "08700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08701"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08702 till 08709 is in use
        // 0871 is Landshut
        "08720"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08721 till 08728 is in use
        "08729"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08731 till 08735 is in use
        "08736"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08737"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08738"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08739"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08740"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08741 till 08745 is in use
        "08746"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08747"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08748"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08749"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08750"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08751 till 08754 is in use
        "08755"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08756 is Nandlstadt
        "08757"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08758"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08759"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08760"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08761 till 08762 is in use
        "08763"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08764 till 08766 is in use
        "08767"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08768"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08769"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08770"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08771 till 08774 is in use
        "08775"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08776"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08777"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08778"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08779"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08780"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08781 till 08785 is in use
        "08786"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08787"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08788"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08789"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0879"               | "DE"       | false           | false
        "08800"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08801 till 08803 is in use
        "08804"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08805 till 08809 is in use
        // 0881 is Weilheim in Oberbayern
        "08820"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08821 till 08826 is in use
        "08827"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08828"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08829"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0883"               | "DE"       | false           | false
        "08840"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08841 is Murnau am Staffelsee
        "08842"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08843"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08844"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08845 till 08847 is in use
        "08848"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08849"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08850"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08851 is Kochel am See
        "08852"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08853"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08854"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08855"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08856 till 08858 is in use
        "08859"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08860 till 08862 is in use
        "08863"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08864"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08865"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "08866"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 08867 till 08869 is in use
        "0887"               | "DE"       | false           | false
        "0888"               | "DE"       | false           | false
        "0889"               | "DE"       | false           | false
        // 089 is München
        // ---
        // TODO start: by Dec 1st of 2024 the ranges 9000 till 09008 will be possible for premium service
        "09000"              | "DE"       | false           | false
        // 09001 Information Service checked in 0900 range test
        "09002"              | "DE"       | false           | false
        // 09003 Entertainment Service checked in 0900 range test
        "09004"              | "DE"       | false           | false
        // 09005 other premium services checked in 0900 range test
        "09006"              | "DE"       | false           | false
        "09007"              | "DE"       | false           | false
        "09008"              | "DE"       | false           | false
        // TODO end: by Dec 1st of 2024 the ranges 9000 till 09008 will be possible for premium service
        // ---
        "09009"              | "DE"       | false           | [false, false, false, false, false, false, false, true, false]  // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/09009/9009_node.html removed block
        "0901"               | "DE"       | false           | false
        "0902"               | "DE"       | false           | false
        "0903"               | "DE"       | false           | false
        "0904"               | "DE"       | false           | false
        "0905"               | "DE"       | false           | false
        // 0906 is Donauwörth
        // 09070 till 09078 is in use
        "09079"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0908x is in use
        // 09090 till 0904 is in use
        "09095"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09096"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09097 is Marxheim
        "09098"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09099 is Kaisheim
        "09100"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09101 till 09107 is in use
        "09108"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09109"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0911 is Nürnberg
        // 09120 is Leinburg
        "09121"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09122 till 09123 is in use
        "09124"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09125"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09126 till 09129 is in use
        "09130"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09131 till 09135 is in use
        "09136"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09137"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09138"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09139"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09140"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09141 till 09149 is in use
        "09150"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09151 till 09158 is in use
        "09159"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09160"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09161 till 09167 is in use
        "09168"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09169"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0917x till 0919x is in use
        "09200"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09201 till 09209 is in use
        // 0921 is Bayreuth
        // 09220 till 09223 is in use
        "09224"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09225 is Stadtsteinach
        "09226"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09227 till 09229 is in use
        "09230"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09231 till 09236 is in use
        "09237"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09238 is Röslau
        "09239"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09240"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09241 till 09246 is in use
        "09247"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09248"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09249"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09250"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09251 till 09257 is in use
        "09258"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09259"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0926x till 0928x is in use
        "09290"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09291"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09292 till 09295 is in use
        "09296"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09297"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09298"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09300"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09301"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09302 till 09303 is in use
        "09304"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09305 till 09307 is in use
        "09308"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09309"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0931 is Würzburg
        "09320"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09321 is Kitzingen
        "09322"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09323 till 09326 is in use
        "09327"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09328"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09329"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09330"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09331 till 09339 is in use
        // 0934x till 0935x is in use
        // 09360 is Thüngen
        "09361"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09362"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09363 till 09367 is in use
        "09368"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09369 is Uettingen
        "09370"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09371 till 09378 is in use
        "09379"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09380"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09381 till 09386 is in use
        "09387"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09388"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09389"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09390"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09391 till 09398 is in use
        "09399"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09400"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09401 till 09409 is in use
        // 0941 is Regensburg
        // 09420 till 09424 is in use
        "09425"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09426 till 09429 is in use
        "09430"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09431 is Schwandorf
        "09432"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09433 till 09436 is in use
        "09437"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09438 till 09439 is in use
        "09440"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09441 till 09448 is in use
        "09449"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09450"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09451 till 09454 is in use
        "09455"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09456"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09457"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09458"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09459"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09460"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09461 till 09649 is in use
        "09470"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09471 till 09474 is in use
        "09475"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09476"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09477"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09478"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09479"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09480 till 09482 is in use
        "09483"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09484 is Brennberg
        "09485"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09486"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09487"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09488"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09489"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09490"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09491 till 09493 is in use
        "09494"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09495 is Breitenbrunn Oberfalz
        "09496"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09497 till 09499 is in use
        "09500"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09501"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09502 till 09505 is in use
        "09506"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09507"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09508"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09509"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0951 is Bamberg
        "09520"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09521 till 09529 is in use
        "09530"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09531 till 09536 is in use
        "09537"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09538"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09539"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09540"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09541"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09542 till 09549 is in use
        "09550"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09551 till 09556 is in use
        "09557"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09558"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09559"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // total 0956x is in use
        "09570"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09571 till 09576 is in use
        "09577"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09578"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09579"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0958"               | "DE"       | false           | false
        "0959"               | "DE"       | false           | false
        "09600"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09601"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09602 till 09608 is in use
        "09609"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0961 is Weiden in der Oberfalz
        "09620"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09621 till 09622 is in use
        "09623"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09624 till 09628 is in use
        "09629"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09630"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09631 till 09639 is in use
        "09640"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09641 till 09648 is in use
        "09649"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09650"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09651 till 09659 is in use
        "09660"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09661 till 09666 is in use
        "09667"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09668"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09669"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09670"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09671 till 09677 is in use
        "09678"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09679"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09680"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09681 till 09683 is in use
        "09684"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09685"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09686"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09687"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09688"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09689"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0969"               | "DE"       | false           | false
        "09700"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09701 is Sandberg Unterfranken
        "09702"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09703"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09704 is Euerdorf
        "09705"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09706"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09707"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09708 is Bad Bocklet
        // total 0972x is in use
        "09730"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09731"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09732 till 09738 is in use
        "09739"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09740"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09741 till 09742 is in use
        "09743"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09744 till 09749 is in use
        "0975"               | "DE"       | false           | false
        "09760"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09761 till 09766 is in use
        "09767"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09768"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09769"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09770"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09771 till 09779 is in use
        "0978"               | "DE"       | false           | false
        "0979"               | "DE"       | false           | false
        "09800"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09801"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09802 till 09805
        "09806"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09807"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09808"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09809"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0981 is Ansbach
        // 09820 is Lehrberg
        "09821"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09822 till 09829 is in use
        "09830"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09831 till 09837 s in use
        "09838"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09839"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09840"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09841 till 09848 is in use
        "09849"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09850"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09851 till 09857 is in use
        "09858"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09859"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09860"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09861 is Rothenburg ob der Tauber
        "09862"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09863"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09864"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09865 is Adelshofen Mittelfranken
        "09866"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09867 till 09869 is in use
        "09870"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09871 till 09876 is in use
        "09877"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09878"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09879"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0988"               | "DE"       | false           | false
        "0989"               | "DE"       | false           | false
        "09900"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09901 is Hengersberg Bayern
        "09902"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09903 till 09908 is in use
        "09909"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 0991 is Deggendorf
        // total 0992x is in use
        "09930"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09931 till 09933 is in use
        "09934"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09935 till 09938 is in use
        "09939"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09940"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09941 till 09948 is in use
        "09949"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09950"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09951 till 09956 is in use
        "09957"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09958"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09959"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09960"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09961 till 09966 is in use
        "09967"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09968"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09969"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "09970"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        // 09971 till 09978 is in use
        "09979"              | "DE"       | false           | [false, false, true, true, true, true, true, true, true]
        "0998"               | "DE"       | false           | false
        "0999"               | "DE"       | false           | false
    }


    def "check if original lib fixes number starting with NAC digit after optional NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when:
        "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then:
        "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                    | regionCode | expectedResult | expectingFail
        "0203056677"              | "DE"       | false          | true  // after NAC+optional NDC number must not start with digit equal to NAC
        "+49203056677"            | "DE"       | false          | true  // after CC+optional NDC number must not start with digit equal to NAC
        "+49203056677"            | "FR"       | false          | true  // after CC+optional NDC number must not start with digit equal to NAC
        "01750556677"             | "DE"       | true           | false // after NAC+mandatory NDC number may start with digit equal to NAC
        "+491750556677"           | "DE"       | true           | false // after CC+mandatory NDC number may start with digit equal to NAC
        "+491750556677"           | "FR"       | true           | false // after CCC+mandatory NDC number may start with digit equal to NAC
    }
}