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