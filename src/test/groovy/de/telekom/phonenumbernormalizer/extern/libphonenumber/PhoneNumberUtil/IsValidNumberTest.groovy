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

import org.slf4j.Logger
import org.slf4j.LoggerFactory


// Plain Number Format: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/NP_Nummernraum.pdf?__blob=publicationFile&v=6
// NDC with labels: https://www.itu.int/dms_pub/itu-t/oth/02/02/T02020000510006PDFE.pdf
// Overview of special number ranges: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/start.html

// Version 5.V.2020 of BenetzA number plan


class IsValidNumberTest extends Specification {

    PhoneNumberUtil phoneUtil

    Logger logger = LoggerFactory.getLogger(IsValidNumberTest.class)

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
                logger.warn("isValidNumber is suddenly not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
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

    def "check if original lib fixed isValid for Emergency short code 112 in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValid: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult  | expectingFail
        // short code for emergency (112) is not dial-able internationally nor does it has additional numbers
        "112"                       | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "0112"                      | "DE"       | false            | false
        "0112 556677"               | "DE"       | false            | false
        "0203 112"                  | "DE"       | false            | true
        "0203 112555"               | "DE"       | false            | true
        "+49112"                    | "DE"       | false            | false
        "+49112 556677"             | "DE"       | false            | false
        "+49203 112"                | "DE"       | false            | true
        "+49203 112555"             | "DE"       | false            | true
        "+49112"                    | "FR"       | false            | false
        "+49112 556677"             | "FR"       | false            | false
        "+49203 112"                | "FR"       | false            | true
        "+49203 112555"             | "FR"       | false            | true
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

        number                      | regionCode  | expectedResult  | expectingFail
        // 155 is Public Service Number for German administration, it is internationally reachable only from foreign countries
        "115"                       | "DE"       | true             | true  // known as intended to use ShortNumberInfo see https://github.com/google/libphonenumber/blob/master/FAQ.md#why-does-phonenumberutil-return-false-for-valid-short-numbers
        "0115"                      | "DE"       | false            | false // not valid by BnetzA definition from within Germany
        "+49115"                    | "DE"       | false            | false // see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/115/115_Nummernplan_konsolidiert.pdf?__blob=publicationFile&v=1 at chapter 2.3
        "+49115"                    | "FR"       | true             | true  // see https://www.115.de/SharedDocs/Nachrichten/DE/2018/115_aus_dem_ausland_erreichbar.html
        // 155 is supporting NDC to reach specific local government hotline: https://www.geoportal.de/Info/tk_05-erreichbarkeit-der-115
        "0203115"                   | "DE"       | true             | false
        "+49203115"                 | "DE"       | true             | false
        "+49203115"                 | "FR"       | true             | false
        // 155 does not have additional digits
        "115555"                    | "DE"       | false            | false
        "0115 556677"               | "DE"       | false            | false
        "0203 115555"               | "DE"       | false            | true
        "+49115 556677"             | "DE"       | false            | false
        "+49115 556677"             | "FR"       | false            | false
        "+49203 115555"             | "DE"       | false            | true
        "+49203 115555"             | "FR"       | false            | true
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
        "118000"                    | "DE"       | true             | true
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
        "02031180000"               | "DE"       | false            | true
        "02031181"                  | "DE"       | false            | true
        "020311810"                 | "DE"       | false            | true
        "020311833"                 | "DE"       | false            | true
        "0203118100"                | "DE"       | false            | true
        "02031189"                  | "DE"       | false            | true
        "020311899"                 | "DE"       | false            | true
        "0203118999"                | "DE"       | false            | true

        // CC + 118(y)xx
        "+49118"                    | "DE"       | false            | false
        "+491180"                   | "DE"       | false            | false
        "+4911800"                  | "DE"       | false            | false
        "+49118000"                 | "DE"       | false            | false
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
        "+492031180000"             | "DE"       | false            | true
        "+492031181"                | "DE"       | false            | true
        "+4920311810"               | "DE"       | false            | true
        "+4920311833"               | "DE"       | false            | true
        "+49203118100"              | "DE"       | false            | true
        "+492031189"                | "DE"       | false            | true
        "+4920311899"               | "DE"       | false            | true
        "+49203118999"              | "DE"       | false            | true

        // CC + 118(y)xx from outside Germany
        "+49118"                    | "FR"       | false            | false
        "+491180"                   | "FR"       | false            | false
        "+4911800"                  | "FR"       | false            | false
        "+49118000"                 | "FR"       | false            | false
        "+491180000"                | "FR"       | false            | false
        "+491181"                   | "FR"       | false            | false
        "+4911810"                  | "FR"       | false            | false
        "+4911833"                  | "FR"       | false            | false
        "+49118100"                 | "FR"       | false            | false
        "+491189"                   | "FR"       | false            | false
        "+4911899"                  | "FR"       | false            | false
        "+49118999"                 | "FR"       | false            | false

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
        Boolean[] results = []
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
        "016013"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0162
        //
        "016250"         | "DE" | [true, false, false, true, true, false, false, true]
        "016255"         | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0163
        //
        "016399"         | "DE" | [true, false, false, true, true, false, false, true]
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
        Boolean[] results = []
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
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

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
        "01760"          | "DE" | [false, false, false, false, false, false, false, false]
        "01761"          | "DE" | [false, false, false, false, false, false, false, false]
        "01762"          | "DE" | [false, false, false, false, false, false, false, false]
        "017630"         | "DE" | [false, false, false, false, false, false, false, false]
        "017631"         | "DE" | [false, false, false, false, false, false, false, false]
        "017632"         | "DE" | [false, false, false, false, false, false, false, false]
        // 017633 is reserved for voicemail - see tests below
        "017634"         | "DE" | [false, false, false, false, false, false, false, false]
        "017635"         | "DE" | [false, false, false, false, false, false, false, false]
        "017636"         | "DE" | [false, false, false, false, false, false, false, false]
        "017637"         | "DE" | [false, false, false, false, false, false, false, false]
        "017638"         | "DE" | [false, false, false, false, false, false, false, false]
        "017639"         | "DE" | [false, false, false, false, false, false, false, false]
        "01764"          | "DE" | [false, false, false, false, false, false, false, false]
        "01765"          | "DE" | [false, false, false, false, false, false, false, false]
        "01766"          | "DE" | [false, false, false, false, false, false, false, false]
        "01767"          | "DE" | [false, false, false, false, false, false, false, false]
        "01768"          | "DE" | [false, false, false, false, false, false, false, false]
        "01769"          | "DE" | [false, false, false, false, false, false, false, false]

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
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0170
        //
        "017013"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0171
        //
        "017113"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0172
        //
        "017250"         | "DE" | [true, true, true, false, true, true, true, false]
        "017255"         | "DE" | [true, false,false, false, true, false, false, false]
        //
        // 0173
        //
        "017350"         | "DE" | [true, true, true, false, true, true, true, false]
        "017355"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0174
        //
        "017450"         | "DE" | [true, true, true, false, true, true, true, false]
        "017455"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0175
        //
        "017513"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0176
        //
        "017633"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0177
        //
        "017799"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0178
        //
        "017899"         | "DE" | [true, false, false, false, true, false, false, false]
        //
        // 0179
        //
        "017933"         | "DE" | [true, false, false, false, true, false, false, false]
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
        Boolean[] results = []
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
        "0181"           | "DE" | [true, true, true, false, false, false, false, false, false, false, false, true]
        "+49181"         | "FR" | [true, true, true, false, false, false, false, false, false, false, false, true]
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
        Boolean[] results = []
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
        "01988"     | true     | "DE" | [true, true, false, true, true, true, true, true, false, true, true, true, true]
        "+491988"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491988"   | true     | "FR" | [true, true, false, true, true, true, true, true, false, true, true, true, true]
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

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 0199 for internal traffic routing"(String reserve, operator,regionCode, boolean[] expectingFails) {
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