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


class IsPossibleNumberWithReasonTest extends Specification {

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
                    logger.info("isPossibleNumberWithReason is still not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
                }
            } else {
                logger.warning("isPossibleNumberWithReason is suddenly not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
            }
        } else {
            if (expectingFail) {
                logger.info("isPossibleNumberWithReason is now correctly validating $number to $expectedResult for region $regionCode !!!")
            }
        }
        return true
    }

    def "check if original lib fixed isPossibleNumberWithReason for police short code 110 in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
            this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // short code for Police (110) is not dial-able internationally nor does it has additional numbers
        "110"                       | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | false
        "0110"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // checked
        "0203 110"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203 110555"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49110"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Maybe IS_POSSIBLE_LOCAL_ONLY is also acceptable
        "+49110 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 110"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 110555"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49110"                    | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Maybe IS_POSSIBLE_LOCAL_ONLY is also acceptable
        "+49110 556677"             | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 110"                | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 110555"             | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // end of 110
    }

    def "check if original lib fixed isPossibleNumberWithReason for Emergency short code 112 in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // short code for emergency (112) is not dial-able internationally nor does it has additional numbers
        "112"                       | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | false
        "0112"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // checked
        "0112 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203 112"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203 112555"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49112"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Maybe IS_POSSIBLE_LOCAL_ONLY is also acceptable
        "+49112 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 112"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 112555"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49112"                    | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Maybe IS_POSSIBLE_LOCAL_ONLY is also acceptable
        "+49112 556677"             | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 112"                | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 112555"             | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // end of 112
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Government short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // 155 is Public Service Number for German administration, it is internationally reachable only from foreign countries
        "115"                       | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | false
        "0115"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not valid by BnetzA definition from within Germany
        "+49115"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Maybe IS_POSSIBLE_LOCAL_ONLY is also acceptable, if used on +49110 & +49112 + see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/115/115_Nummernplan_konsolidiert.pdf?__blob=publicationFile&v=1 at chapter 2.3
        "+49115"                    | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | true  // see https://www.115.de/SharedDocs/Nachrichten/DE/2018/115_aus_dem_ausland_erreichbar.html
        // 155 is supporting NDC to reach specific local government hotline: https://www.geoportal.de/Info/tk_05-erreichbarkeit-der-115
        "0203115"                   | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49203115"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49203115"                 | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        // 155 does not have additional digits
        "115555"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0115 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203 115555"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49115 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49115 556677"             | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 115555"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203 115555"             | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // end of 115
    }

    def "check if original lib fixed isPossibleNumberWithReason for EU social short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // 116 is mentioned in number plan as 1160 and 1161 but in special ruling a full 6 digit number block: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/116xyz/StrukturAusgestNrBereich_Id11155pdf.pdf?__blob=publicationFile&v=4
        // 116xyz is nationally and internationally reachable - special check 116000 as initial number, 116116 as assigned number and 116999 as max legal number
        "116"                       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "116000"                    | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "116116"                    | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "116999"                    | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "116 5566"                  | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "116 55"                    | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/116xyz/116116.html
        // NAC + 116xxx
        // see no. 7: national 0116116 is not a valid number, but may be replaced by 116116 by the operator - caller could reach target. ( T-Mobile is doing so currently 03.11.2023 - no guarantee for the future nor for any other operator. Best practice, assuming call will not reach target=.
        "0116"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0116000"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not valid by BnetzA definition just using NAC
        "0116116"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not valid by BnetzA definition just using NAC
        "0116999"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not valid by BnetzA definition just using NAC
        "0116 5566"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0116 55"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // NAC + NDC (e.g. for Duisburg) + 116xxx
        "0203116"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203116000"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203116116"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203116999"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203116 5566"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203116 55"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // CC + 116xxx
        "+49116"                    | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "+49116000"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116116"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116999"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116 5566"               | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "+49116 55"                 | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true

        // CC + NDC (e.g. for Duisburg) + 116xxx
        "+49203116"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203116000"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203116116"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203116999"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203116 5566"            | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203116 55"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // CC + 116xxx from outside Germany
        "+49116"                    | "FR"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "+49116000"                 | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116116"                 | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116999"                 | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116 5566"               | "FR"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "+49116 55"                 | "FR"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // end of 116
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Call Assistant short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"

        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/118xy/118xyNummernplan.pdf?__blob=publicationFile&v=1
        // it is mentioned, that those numbers are nationally reachable - which excludes them from locally, so no local number should work this way because without NDC it could not be seperated from the national number
        // implicitly it could also mean that those numbers are not routed from outside germany

        // 118 is starting part and in general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "118"                       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "1180"                      | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "11800"                     | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "118000"                    | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "1180000"                   | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "1181"                      | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "11810"                     | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        // Call Assistant of Deutsche Telekom
        "11833"                     | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "118100"                    | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "1189"                      | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "11899"                     | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "118999"                    | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true

        // Tested on 26.12.2023 - 11833 works on TMD, but neither 011833 nor +4911833 is working on T-Mobile Germany
        // NAC + 118(y)xx belongs to the number reserve of NAC + 11

        "0118"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "01180"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "011800"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0118000"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "01180000"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "01181"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "011810"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "011833"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0118100"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "01189"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "011899"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0118999"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // NAC + NDC (e.g. for Duisburg) + 118(y)xx
        "0203118"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02031180"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "020311800"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203118000"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02031180000"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02031181"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "020311810"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "020311833"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203118100"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02031189"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "020311899"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0203118999"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // CC + 118(y)xx
        "+49118"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491180"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911800"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49118000"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491180000"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491181"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911810"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911833"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49118100"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491189"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911899"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49118999"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // CC + NDC (e.g. for Duisburg) + 118(y)xx
        "+49203118"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+492031180"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4920311800"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203118000"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+492031180000"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+492031181"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4920311810"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4920311833"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203118100"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+492031189"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4920311899"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49203118999"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // CC + 118(y)xx from outside Germany
        "+49118"                    | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491180"                   | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911800"                  | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49118000"                 | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491180000"                | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491181"                   | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911810"                  | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911833"                  | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49118100"                 | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+491189"                   | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+4911899"                  | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49118999"                 | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        // end of 118
    }

    def "check if original lib fixed isPossibleNumberWithReason for ambulance transport 19222 short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // prior to mobile, there where 19xxx short codes in fixed line - only 19222 for no emergency ambulance call is still valid
        // its a national reserved number, which in contrast to 112 might also be called with NDC to reach a specific ambulance center - not all NDC have a connected 19222.
        // for more information see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vfg_25_2006_konsFassung100823.pdf?__blob=publicationFile&v=3 chapter 7
        "19222"                     | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true  // not valid on mobil
        // using 19222 als NDC after NAC is checked by "online services 019xx"
        "0203 19222"                | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0203 19222555"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // must not be longer
        "+4919222"                  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Maybe IS_POSSIBLE_LOCAL_ONLY is also acceptable
        // using 19222 from DE als NDC after CC is checked by "online services 019xx"
        "+49203 19222"              | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49203 19222555"           | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // must not be longer
        // using 19222 from FR als NDC after CC is checked by "online services 019xx"
        "+49203 19222"              | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49203 19222555"           | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // must not be longer
        // end of 19222
    }

    def "check if original lib fixed isPossibleNumberWithReason for German mass traffic  NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                           | expectingFail
        // 137 is masstraffic 10 digits
        "0137 000 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "0137 000 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370
        "0137 000 000"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Zone 0 are not assigend https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/freieRNB/0137_MABEZ_FreieRNB.html?nn=326370

        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0137/0137_Nummernplan.pdf?__blob=publicationFile&v=4
        // within each zone, there are only a few ranges assigned: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0137/belegteRNB/0137MABEZBelegteRNB_Basepage.html?nn=326370
        // Zone 1 is valid, but only with exactly 10 digits
        "0137 100 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 100 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 100 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 2 is valid, but only with exactly 10 digits
        "0137 200 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 200 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 200 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 3 is valid, but only with exactly 10 digits
        "0137 300 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 300 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 300 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 4 is valid, but only with exactly 10 digits
        "0137 400 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 400 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 400 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 5 is valid, but only with exactly 10 digits
        "0137 500 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 500 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 500 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 6 is valid, but only with exactly 10 digits
        "0137 600 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 600 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 600 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 7 is valid, but only with exactly 10 digits
        "0137 700 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 700 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 700 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 8 is valid, but only with exactly 10 digits
        "0137 800 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 800 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 800 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // Zone 9 is valid, but only with exactly 10 digits
        "0137 900 0000"             | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0137 900 00000"            | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "0137 900 000"              | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true

    }

    def "check if original lib fixed isPossibleNumberWithReason for German Mobile 15 range"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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
        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG]

        when:
        PhoneNumberUtil.ValidationResult[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
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
        "015001"         | "DE"      | [true, false, true, true, false, true]
        "015002"         | "DE"      | [true, false, true, true, false, true]
        "015003"         | "DE"      | [true, false, true, true, false, true]
        "015004"         | "DE"      | [true, false, true, true, false, true]
        "015005"         | "DE"      | [true, false, true, true, false, true]
        "015006"         | "DE"      | [true, false, true, true, false, true]
        "015007"         | "DE"      | [true, false, true, true, false, true]
        "015008"         | "DE"      | [true, false, true, true, false, true]
        "015009"         | "DE"      | [true, false, true, true, false, true]
        "01501"          | "DE"      | [true, false, true, true, false, true]
        "01502"          | "DE"      | [true, false, true, true, false, true]
        "01503"          | "DE"      | [true, false, true, true, false, true]
        "01504"          | "DE"      | [true, false, true, true, false, true]
        "01505"          | "DE"      | [true, false, true, true, false, true]
        "01506"          | "DE"      | [true, false, true, true, false, true]
        "01507"          | "DE"      | [true, false, true, true, false, true]
        "01508"          | "DE"      | [true, false, true, true, false, true]
        "01509"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0151
        //
        "01510"          | "DE"      | [true, false, true, true, false, true]
        "015110"         | "DE"      | [true, false, true, true, false, true]
        "015111"         | "DE"      | [true, false, true, true, false, true]
        "015112"         | "DE"      | [true, false, true, true, false, true]
        // 015113 is reserved for voicemail - see tests below
        "015114"         | "DE"      | [true, false, true, true, false, true]
        "015115"         | "DE"      | [true, false, true, true, false, true]
        "015116"         | "DE"      | [true, false, true, true, false, true]
        "015117"         | "DE"      | [true, false, true, true, false, true]
        "015118"         | "DE"      | [true, false, true, true, false, true]
        "015119"         | "DE"      | [true, false, true, true, false, true]
        "01512"          | "DE"      | [true, false, true, true, false, true]
        "01513"          | "DE"      | [true, false, true, true, false, true]
        "01514"          | "DE"      | [true, false, true, true, false, true]
        "01515"          | "DE"      | [true, false, true, true, false, true]
        "01516"          | "DE"      | [true, false, true, true, false, true]
        "01517"          | "DE"      | [true, false, true, true, false, true]
        "01518"          | "DE"      | [true, false, true, true, false, true]
        "01519"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0152
        //
        "015200"         | "DE"      | [true, false, true, true, false, true]
        "015201"         | "DE"      | [true, false, true, true, false, true]
        "015202"         | "DE"      | [true, false, true, true, false, true]
        "015203"         | "DE"      | [true, false, true, true, false, true]
        "015204"         | "DE"      | [true, false, true, true, false, true]
        // 0152050 is reserved for voicemail - see tests below
        "0152051"        | "DE"      | [true, false, true, true, false, true]
        "0152052"        | "DE"      | [true, false, true, true, false, true]
        "0152053"        | "DE"      | [true, false, true, true, false, true]
        "0152054"        | "DE"      | [true, false, true, true, false, true]
        // 0152055 is reserved for voicemail - see tests below
        "0152056"        | "DE"      | [true, false, true, true, false, true]
        "0152057"        | "DE"      | [true, false, true, true, false, true]
        "0152058"        | "DE"      | [true, false, true, true, false, true]
        "0152059"        | "DE"      | [true, false, true, true, false, true]
        "015206"         | "DE"      | [true, false, true, true, false, true]
        "015207"         | "DE"      | [true, false, true, true, false, true]
        "015208"         | "DE"      | [true, false, true, true, false, true]
        "015209"         | "DE"      | [true, false, true, true, false, true]

        "015210"         | "DE"      | [true, false, true, true, false, true]
        "015211"         | "DE"      | [true, false, true, true, false, true]
        "015212"         | "DE"      | [true, false, true, true, false, true]
        "015213"         | "DE"      | [true, false, true, true, false, true]
        "015214"         | "DE"      | [true, false, true, true, false, true]
        // 0152150 is reserved for voicemail - see tests below
        "0152151"        | "DE"      | [true, false, true, true, false, true]
        "0152152"        | "DE"      | [true, false, true, true, false, true]
        "0152153"        | "DE"      | [true, false, true, true, false, true]
        "0152154"        | "DE"      | [true, false, true, true, false, true]
        // 0152155 is reserved for voicemail - see tests below
        "0152156"        | "DE"      | [true, false, true, true, false, true]
        "0152157"        | "DE"      | [true, false, true, true, false, true]
        "0152158"        | "DE"      | [true, false, true, true, false, true]
        "0152159"        | "DE"      | [true, false, true, true, false, true]
        "015216"         | "DE"      | [true, false, true, true, false, true]
        "015217"         | "DE"      | [true, false, true, true, false, true]
        "015218"         | "DE"      | [true, false, true, true, false, true]
        "015219"         | "DE"      | [true, false, true, true, false, true]

        "015220"         | "DE"      | [true, false, true, true, false, true]
        "015221"         | "DE"      | [true, false, true, true, false, true]
        "015222"         | "DE"      | [true, false, true, true, false, true]
        "015223"         | "DE"      | [true, false, true, true, false, true]
        "015224"         | "DE"      | [true, false, true, true, false, true]
        // 0152250 is reserved for voicemail - see tests below
        "0152251"        | "DE"      | [true, false, true, true, false, true]
        "0152252"        | "DE"      | [true, false, true, true, false, true]
        "0152253"        | "DE"      | [true, false, true, true, false, true]
        "0152254"        | "DE"      | [true, false, true, true, false, true]
        // 0152255 is reserved for voicemail - see tests below
        "0152256"        | "DE"      | [true, false, true, true, false, true]
        "0152257"        | "DE"      | [true, false, true, true, false, true]
        "0152258"        | "DE"      | [true, false, true, true, false, true]
        "0152259"        | "DE"      | [true, false, true, true, false, true]
        "015226"         | "DE"      | [true, false, true, true, false, true]
        "015227"         | "DE"      | [true, false, true, true, false, true]
        "015228"         | "DE"      | [true, false, true, true, false, true]
        "015229"         | "DE"      | [true, false, true, true, false, true]

        "015230"         | "DE"      | [true, false, true, true, false, true]
        "015231"         | "DE"      | [true, false, true, true, false, true]
        "015232"         | "DE"      | [true, false, true, true, false, true]
        "015233"         | "DE"      | [true, false, true, true, false, true]
        "015234"         | "DE"      | [true, false, true, true, false, true]
        // 0152350 is reserved for voicemail - see tests below
        "0152351"        | "DE"      | [true, false, true, true, false, true]
        "0152352"        | "DE"      | [true, false, true, true, false, true]
        "0152353"        | "DE"      | [true, false, true, true, false, true]
        "0152354"        | "DE"      | [true, false, true, true, false, true]
        // 0152355 is reserved for voicemail - see tests below
        "0152356"        | "DE"      | [true, false, true, true, false, true]
        "0152357"        | "DE"      | [true, false, true, true, false, true]
        "0152358"        | "DE"      | [true, false, true, true, false, true]
        "0152359"        | "DE"      | [true, false, true, true, false, true]
        "015236"         | "DE"      | [true, false, true, true, false, true]
        "015237"         | "DE"      | [true, false, true, true, false, true]
        "015238"         | "DE"      | [true, false, true, true, false, true]
        "015239"         | "DE"      | [true, false, true, true, false, true]

        "015240"         | "DE"      | [true, false, true, true, false, true]
        "015241"         | "DE"      | [true, false, true, true, false, true]
        "015242"         | "DE"      | [true, false, true, true, false, true]
        "015243"         | "DE"      | [true, false, true, true, false, true]
        "015244"         | "DE"      | [true, false, true, true, false, true]
        // 0152450 is reserved for voicemail - see tests below
        "0152451"        | "DE"      | [true, false, true, true, false, true]
        "0152452"        | "DE"      | [true, false, true, true, false, true]
        "0152453"        | "DE"      | [true, false, true, true, false, true]
        "0152454"        | "DE"      | [true, false, true, true, false, true]
        // 0152455 is reserved for voicemail - see tests below
        "0152456"        | "DE"      | [true, false, true, true, false, true]
        "0152457"        | "DE"      | [true, false, true, true, false, true]
        "0152458"        | "DE"      | [true, false, true, true, false, true]
        "0152459"        | "DE"      | [true, false, true, true, false, true]
        "015246"         | "DE"      | [true, false, true, true, false, true]
        "015247"         | "DE"      | [true, false, true, true, false, true]
        "015248"         | "DE"      | [true, false, true, true, false, true]
        "015249"         | "DE"      | [true, false, true, true, false, true]

        "015250"         | "DE"      | [true, false, true, true, false, true]
        "015251"         | "DE"      | [true, false, true, true, false, true]
        "015252"         | "DE"      | [true, false, true, true, false, true]
        "015253"         | "DE"      | [true, false, true, true, false, true]
        "015254"         | "DE"      | [true, false, true, true, false, true]
        // 0152550 is reserved for voicemail - see tests below
        "0152551"        | "DE"      | [true, false, true, true, false, true]
        "0152552"        | "DE"      | [true, false, true, true, false, true]
        "0152553"        | "DE"      | [true, false, true, true, false, true]
        "0152554"        | "DE"      | [true, false, true, true, false, true]
        // 0152555 is reserved for voicemail - see tests below
        "0152556"        | "DE"      | [true, false, true, true, false, true]
        "0152557"        | "DE"      | [true, false, true, true, false, true]
        "0152558"        | "DE"      | [true, false, true, true, false, true]
        "0152559"        | "DE"      | [true, false, true, true, false, true]
        "015256"         | "DE"      | [true, false, true, true, false, true]
        "015257"         | "DE"      | [true, false, true, true, false, true]
        "015258"         | "DE"      | [true, false, true, true, false, true]
        "015259"         | "DE"      | [true, false, true, true, false, true]

        "015260"         | "DE"      | [true, false, true, true, false, true]
        "015261"         | "DE"      | [true, false, true, true, false, true]
        "015262"         | "DE"      | [true, false, true, true, false, true]
        "015263"         | "DE"      | [true, false, true, true, false, true]
        "015264"         | "DE"      | [true, false, true, true, false, true]
        // 0152650 is reserved for voicemail - see tests below
        "0152651"        | "DE"      | [true, false, true, true, false, true]
        "0152652"        | "DE"      | [true, false, true, true, false, true]
        "0152653"        | "DE"      | [true, false, true, true, false, true]
        "0152654"        | "DE"      | [true, false, true, true, false, true]
        // 0152655 is reserved for voicemail - see tests below
        "0152656"        | "DE"      | [true, false, true, true, false, true]
        "0152657"        | "DE"      | [true, false, true, true, false, true]
        "0152658"        | "DE"      | [true, false, true, true, false, true]
        "0152659"        | "DE"      | [true, false, true, true, false, true]
        "015266"         | "DE"      | [true, false, true, true, false, true]
        "015267"         | "DE"      | [true, false, true, true, false, true]
        "015268"         | "DE"      | [true, false, true, true, false, true]
        "015269"         | "DE"      | [true, false, true, true, false, true]

        "015270"         | "DE"      | [true, false, true, true, false, true]
        "015271"         | "DE"      | [true, false, true, true, false, true]
        "015272"         | "DE"      | [true, false, true, true, false, true]
        "015273"         | "DE"      | [true, false, true, true, false, true]
        "015274"         | "DE"      | [true, false, true, true, false, true]
        // 0152750 is reserved for voicemail - see tests below
        "0152751"        | "DE"      | [true, false, true, true, false, true]
        "0152752"        | "DE"      | [true, false, true, true, false, true]
        "0152753"        | "DE"      | [true, false, true, true, false, true]
        "0152754"        | "DE"      | [true, false, true, true, false, true]
        // 0152755 is reserved for voicemail - see tests below
        "0152756"        | "DE"      | [true, false, true, true, false, true]
        "0152757"        | "DE"      | [true, false, true, true, false, true]
        "0152758"        | "DE"      | [true, false, true, true, false, true]
        "0152759"        | "DE"      | [true, false, true, true, false, true]
        "015276"         | "DE"      | [true, false, true, true, false, true]
        "015277"         | "DE"      | [true, false, true, true, false, true]
        "015278"         | "DE"      | [true, false, true, true, false, true]
        "015279"         | "DE"      | [true, false, true, true, false, true]

        "015280"         | "DE"      | [true, false, true, true, false, true]
        "015281"         | "DE"      | [true, false, true, true, false, true]
        "015282"         | "DE"      | [true, false, true, true, false, true]
        "015283"         | "DE"      | [true, false, true, true, false, true]
        "015284"         | "DE"      | [true, false, true, true, false, true]
        // 0152850 is reserved for voicemail - see tests below
        "0152851"        | "DE"      | [true, false, true, true, false, true]
        "0152852"        | "DE"      | [true, false, true, true, false, true]
        "0152853"        | "DE"      | [true, false, true, true, false, true]
        "0152854"        | "DE"      | [true, false, true, true, false, true]
        // 0152855 is reserved for voicemail - see tests below
        "0152856"        | "DE"      | [true, false, true, true, false, true]
        "0152857"        | "DE"      | [true, false, true, true, false, true]
        "0152858"        | "DE"      | [true, false, true, true, false, true]
        "0152859"        | "DE"      | [true, false, true, true, false, true]
        "015286"         | "DE"      | [true, false, true, true, false, true]
        "015287"         | "DE"      | [true, false, true, true, false, true]
        "015288"         | "DE"      | [true, false, true, true, false, true]
        "015289"         | "DE"      | [true, false, true, true, false, true]

        "015290"         | "DE"      | [true, false, true, true, false, true]
        "015291"         | "DE"      | [true, false, true, true, false, true]
        "015292"         | "DE"      | [true, false, true, true, false, true]
        "015293"         | "DE"      | [true, false, true, true, false, true]
        "015294"         | "DE"      | [true, false, true, true, false, true]
        // 0152950 is reserved for voicemail - see tests below
        "0152951"        | "DE"      | [true, false, true, true, false, true]
        "0152952"        | "DE"      | [true, false, true, true, false, true]
        "0152953"        | "DE"      | [true, false, true, true, false, true]
        "0152954"        | "DE"      | [true, false, true, true, false, true]
        // 0152955 is reserved for voicemail - see tests below
        "0152956"        | "DE"      | [true, false, true, true, false, true]
        "0152957"        | "DE"      | [true, false, true, true, false, true]
        "0152958"        | "DE"      | [true, false, true, true, false, true]
        "0152959"        | "DE"      | [true, false, true, true, false, true]
        "015296"         | "DE"      | [true, false, true, true, false, true]
        "015297"         | "DE"      | [true, false, true, true, false, true]
        "015298"         | "DE"      | [true, false, true, true, false, true]
        "015299"         | "DE"      | [true, false, true, true, false, true]

        //
        // 0153
        //
        // 015300 is reserved for voicemail - see tests below
        "015301"         | "DE"      | [true, false, true, true, false, true]
        "015302"         | "DE"      | [true, false, true, true, false, true]
        "015303"         | "DE"      | [true, false, true, true, false, true]
        "015304"         | "DE"      | [true, false, true, true, false, true]
        "015305"         | "DE"      | [true, false, true, true, false, true]
        "015306"         | "DE"      | [true, false, true, true, false, true]
        "015307"         | "DE"      | [true, false, true, true, false, true]
        "015308"         | "DE"      | [true, false, true, true, false, true]
        "015309"         | "DE"      | [true, false, true, true, false, true]
        "01531"          | "DE"      | [true, false, true, true, false, true]
        "01532"          | "DE"      | [true, false, true, true, false, true]
        "01533"          | "DE"      | [true, false, true, true, false, true]
        "01534"          | "DE"      | [true, false, true, true, false, true]
        "01535"          | "DE"      | [true, false, true, true, false, true]
        "01536"          | "DE"      | [true, false, true, true, false, true]
        "01537"          | "DE"      | [true, false, true, true, false, true]
        "01538"          | "DE"      | [true, false, true, true, false, true]
        "01539"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0154
        //
        // 015400 is reserved for voicemail - see tests below
        "015401"         | "DE"      | [true, false, true, true, false, true]
        "015402"         | "DE"      | [true, false, true, true, false, true]
        "015403"         | "DE"      | [true, false, true, true, false, true]
        "015404"         | "DE"      | [true, false, true, true, false, true]
        "015405"         | "DE"      | [true, false, true, true, false, true]
        "015406"         | "DE"      | [true, false, true, true, false, true]
        "015407"         | "DE"      | [true, false, true, true, false, true]
        "015408"         | "DE"      | [true, false, true, true, false, true]
        "015409"         | "DE"      | [true, false, true, true, false, true]
        "01541"          | "DE"      | [true, false, true, true, false, true]
        "01542"          | "DE"      | [true, false, true, true, false, true]
        "01543"          | "DE"      | [true, false, true, true, false, true]
        "0154"          | "DE"      | [true, false, true, true, false, true]
        "01545"          | "DE"      | [true, false, true, true, false, true]
        "01546"          | "DE"      | [true, false, true, true, false, true]
        "01547"          | "DE"      | [true, false, true, true, false, true]
        "01548"          | "DE"      | [true, false, true, true, false, true]
        "01549"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0155
        //
        // 015500 is reserved for voicemail - see tests below
        "015501"         | "DE"      | [true, false, true, true, false, true]
        "015502"         | "DE"      | [true, false, true, true, false, true]
        "015503"         | "DE"      | [true, false, true, true, false, true]
        "015504"         | "DE"      | [true, false, true, true, false, true]
        "015505"         | "DE"      | [true, false, true, true, false, true]
        "015506"         | "DE"      | [true, false, true, true, false, true]
        "015507"         | "DE"      | [true, false, true, true, false, true]
        "015508"         | "DE"      | [true, false, true, true, false, true]
        "015509"         | "DE"      | [true, false, true, true, false, true]
        "01551"          | "DE"      | [true, false, true, true, false, true]
        "01552"          | "DE"      | [true, false, true, true, false, true]
        "01553"          | "DE"      | [true, false, true, true, false, true]
        "01554"          | "DE"      | [true, false, true, true, false, true]
        "01555"          | "DE"      | [true, false, true, true, false, true]
        "01556"          | "DE"      | [true, false, true, true, false, true]
        "01557"          | "DE"      | [true, false, true, true, false, true]
        "01558"          | "DE"      | [true, false, true, true, false, true]
        "01559"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0156
        //
        // 015600 is reserved for voicemail - see tests below
        "015601"         | "DE"      | [true, false, true, true, false, true]
        "015602"         | "DE"      | [true, false, true, true, false, true]
        "015603"         | "DE"      | [true, false, true, true, false, true]
        "015604"         | "DE"      | [true, false, true, true, false, true]
        "015605"         | "DE"      | [true, false, true, true, false, true]
        "015606"         | "DE"      | [true, false, true, true, false, true]
        "015607"         | "DE"      | [true, false, true, true, false, true]
        "015608"         | "DE"      | [true, false, true, true, false, true]
        "015609"         | "DE"      | [true, false, true, true, false, true]
        "01561"          | "DE"      | [true, false, true, true, false, true]
        "01562"          | "DE"      | [true, false, true, true, false, true]
        "01563"          | "DE"      | [true, false, true, true, false, true]
        "01564"          | "DE"      | [true, false, true, true, false, true]
        "01565"          | "DE"      | [true, false, true, true, false, true]
        "01566"          | "DE"      | [true, false, true, true, false, true]
        "01567"          | "DE"      | [true, false, true, true, false, true]
        "01568"          | "DE"      | [true, false, true, true, false, true]
        "01569"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0157
        //
        "015700"         | "DE"      | [true, false, true, true, false, true]
        "015701"         | "DE"      | [true, false, true, true, false, true]
        "015702"         | "DE"      | [true, false, true, true, false, true]
        "015703"         | "DE"      | [true, false, true, true, false, true]
        "015704"         | "DE"      | [true, false, true, true, false, true]
        "015705"         | "DE"      | [true, false, true, true, false, true]
        "015706"         | "DE"      | [true, false, true, true, false, true]
        "015707"         | "DE"      | [true, false, true, true, false, true]
        "015708"         | "DE"      | [true, false, true, true, false, true]
        "0157090"        | "DE"      | [true, false, true, true, false, true]
        "0157091"        | "DE"      | [true, false, true, true, false, true]
        "0157092"        | "DE"      | [true, false, true, true, false, true]
        "0157093"        | "DE"      | [true, false, true, true, false, true]
        "0157094"        | "DE"      | [true, false, true, true, false, true]
        "0157095"        | "DE"      | [true, false, true, true, false, true]
        "0157096"        | "DE"      | [true, false, true, true, false, true]
        "0157097"        | "DE"      | [true, false, true, true, false, true]
        "0157098"        | "DE"      | [true, false, true, true, false, true]
        // 0157099 is reserved for voicemail - see tests below

        "015710"         | "DE"      | [true, false, true, true, false, true]
        "015711"         | "DE"      | [true, false, true, true, false, true]
        "015712"         | "DE"      | [true, false, true, true, false, true]
        "015713"         | "DE"      | [true, false, true, true, false, true]
        "015714"         | "DE"      | [true, false, true, true, false, true]
        "015715"         | "DE"      | [true, false, true, true, false, true]
        "015716"         | "DE"      | [true, false, true, true, false, true]
        "015717"         | "DE"      | [true, false, true, true, false, true]
        "015718"         | "DE"      | [true, false, true, true, false, true]
        "0157190"        | "DE"      | [true, false, true, true, false, true]
        "0157191"        | "DE"      | [true, false, true, true, false, true]
        "0157192"        | "DE"      | [true, false, true, true, false, true]
        "0157193"        | "DE"      | [true, false, true, true, false, true]
        "0157194"        | "DE"      | [true, false, true, true, false, true]
        "0157195"        | "DE"      | [true, false, true, true, false, true]
        "0157196"        | "DE"      | [true, false, true, true, false, true]
        "0157197"        | "DE"      | [true, false, true, true, false, true]
        "0157198"        | "DE"      | [true, false, true, true, false, true]
        // 0157199 is reserved for voicemail - see tests below

        "015720"         | "DE"      | [true, false, true, true, false, true]
        "015721"         | "DE"      | [true, false, true, true, false, true]
        "015722"         | "DE"      | [true, false, true, true, false, true]
        "015723"         | "DE"      | [true, false, true, true, false, true]
        "015724"         | "DE"      | [true, false, true, true, false, true]
        "015725"         | "DE"      | [true, false, true, true, false, true]
        "015726"         | "DE"      | [true, false, true, true, false, true]
        "015727"         | "DE"      | [true, false, true, true, false, true]
        "015728"         | "DE"      | [true, false, true, true, false, true]
        "0157290"        | "DE"      | [true, false, true, true, false, true]
        "0157291"        | "DE"      | [true, false, true, true, false, true]
        "0157292"        | "DE"      | [true, false, true, true, false, true]
        "0157293"        | "DE"      | [true, false, true, true, false, true]
        "0157294"        | "DE"      | [true, false, true, true, false, true]
        "0157295"        | "DE"      | [true, false, true, true, false, true]
        "0157296"        | "DE"      | [true, false, true, true, false, true]
        "0157297"        | "DE"      | [true, false, true, true, false, true]
        "0157298"        | "DE"      | [true, false, true, true, false, true]
        // 0157299 is reserved for voicemail - see tests below

        "015730"         | "DE"      | [true, false, true, true, false, true]
        "015731"         | "DE"      | [true, false, true, true, false, true]
        "015732"         | "DE"      | [true, false, true, true, false, true]
        "015733"         | "DE"      | [true, false, true, true, false, true]
        "015734"         | "DE"      | [true, false, true, true, false, true]
        "015735"         | "DE"      | [true, false, true, true, false, true]
        "015736"         | "DE"      | [true, false, true, true, false, true]
        "015737"         | "DE"      | [true, false, true, true, false, true]
        "015738"         | "DE"      | [true, false, true, true, false, true]
        "0157390"        | "DE"      | [true, false, true, true, false, true]
        "0157391"        | "DE"      | [true, false, true, true, false, true]
        "0157392"        | "DE"      | [true, false, true, true, false, true]
        "0157393"        | "DE"      | [true, false, true, true, false, true]
        "0157394"        | "DE"      | [true, false, true, true, false, true]
        "0157395"        | "DE"      | [true, false, true, true, false, true]
        "0157396"        | "DE"      | [true, false, true, true, false, true]
        "0157397"        | "DE"      | [true, false, true, true, false, true]
        "0157398"        | "DE"      | [true, false, true, true, false, true]
        // 0157399 is reserved for voicemail - see tests below

        "015740"         | "DE"      | [true, false, true, true, false, true]
        "015741"         | "DE"      | [true, false, true, true, false, true]
        "015742"         | "DE"      | [true, false, true, true, false, true]
        "015743"         | "DE"      | [true, false, true, true, false, true]
        "015744"         | "DE"      | [true, false, true, true, false, true]
        "015745"         | "DE"      | [true, false, true, true, false, true]
        "015746"         | "DE"      | [true, false, true, true, false, true]
        "015747"         | "DE"      | [true, false, true, true, false, true]
        "015748"         | "DE"      | [true, false, true, true, false, true]
        "0157490"        | "DE"      | [true, false, true, true, false, true]
        "0157491"        | "DE"      | [true, false, true, true, false, true]
        "0157492"        | "DE"      | [true, false, true, true, false, true]
        "0157493"        | "DE"      | [true, false, true, true, false, true]
        "0157494"        | "DE"      | [true, false, true, true, false, true]
        "0157495"        | "DE"      | [true, false, true, true, false, true]
        "0157496"        | "DE"      | [true, false, true, true, false, true]
        "0157497"        | "DE"      | [true, false, true, true, false, true]
        "0157498"        | "DE"      | [true, false, true, true, false, true]
        // 0157499 is reserved for voicemail - see tests below

        "015750"         | "DE"      | [true, false, true, true, false, true]
        "015751"         | "DE"      | [true, false, true, true, false, true]
        "015752"         | "DE"      | [true, false, true, true, false, true]
        "015753"         | "DE"      | [true, false, true, true, false, true]
        "015754"         | "DE"      | [true, false, true, true, false, true]
        "015755"         | "DE"      | [true, false, true, true, false, true]
        "015756"         | "DE"      | [true, false, true, true, false, true]
        "015757"         | "DE"      | [true, false, true, true, false, true]
        "015758"         | "DE"      | [true, false, true, true, false, true]
        "0157590"        | "DE"      | [true, false, true, true, false, true]
        "0157591"        | "DE"      | [true, false, true, true, false, true]
        "0157592"        | "DE"      | [true, false, true, true, false, true]
        "0157593"        | "DE"      | [true, false, true, true, false, true]
        "0157594"        | "DE"      | [true, false, true, true, false, true]
        "0157595"        | "DE"      | [true, false, true, true, false, true]
        "0157596"        | "DE"      | [true, false, true, true, false, true]
        "0157597"        | "DE"      | [true, false, true, true, false, true]
        "0157598"        | "DE"      | [true, false, true, true, false, true]
        // 0157599 is reserved for voicemail - see tests below

        "015760"         | "DE"      | [true, false, true, true, false, true]
        "015761"         | "DE"      | [true, false, true, true, false, true]
        "015762"         | "DE"      | [true, false, true, true, false, true]
        "015763"         | "DE"      | [true, false, true, true, false, true]
        "015764"         | "DE"      | [true, false, true, true, false, true]
        "015765"         | "DE"      | [true, false, true, true, false, true]
        "015766"         | "DE"      | [true, false, true, true, false, true]
        "015767"         | "DE"      | [true, false, true, true, false, true]
        "015768"         | "DE"      | [true, false, true, true, false, true]
        "0157690"        | "DE"      | [true, false, true, true, false, true]
        "0157691"        | "DE"      | [true, false, true, true, false, true]
        "0157692"        | "DE"      | [true, false, true, true, false, true]
        "0157693"        | "DE"      | [true, false, true, true, false, true]
        "0157694"        | "DE"      | [true, false, true, true, false, true]
        "0157695"        | "DE"      | [true, false, true, true, false, true]
        "0157696"        | "DE"      | [true, false, true, true, false, true]
        "0157697"        | "DE"      | [true, false, true, true, false, true]
        "0157698"        | "DE"      | [true, false, true, true, false, true]
        // 0157699 is reserved for voicemail - see tests below

        "015770"         | "DE"      | [true, false, true, true, false, true]
        "015771"         | "DE"      | [true, false, true, true, false, true]
        "015772"         | "DE"      | [true, false, true, true, false, true]
        "015773"         | "DE"      | [true, false, true, true, false, true]
        "015774"         | "DE"      | [true, false, true, true, false, true]
        "015775"         | "DE"      | [true, false, true, true, false, true]
        "015776"         | "DE"      | [true, false, true, true, false, true]
        "015777"         | "DE"      | [true, false, true, true, false, true]
        "015778"         | "DE"      | [true, false, true, true, false, true]
        "0157790"        | "DE"      | [true, false, true, true, false, true]
        "0157791"        | "DE"      | [true, false, true, true, false, true]
        "0157792"        | "DE"      | [true, false, true, true, false, true]
        "0157793"        | "DE"      | [true, false, true, true, false, true]
        "0157794"        | "DE"      | [true, false, true, true, false, true]
        "0157795"        | "DE"      | [true, false, true, true, false, true]
        "0157796"        | "DE"      | [true, false, true, true, false, true]
        "0157797"        | "DE"      | [true, false, true, true, false, true]
        "0157798"        | "DE"      | [true, false, true, true, false, true]
        // 0157799 is reserved for voicemail - see tests below

        "015780"         | "DE"      | [true, false, true, true, false, true]
        "015781"         | "DE"      | [true, false, true, true, false, true]
        "015782"         | "DE"      | [true, false, true, true, false, true]
        "015783"         | "DE"      | [true, false, true, true, false, true]
        "015784"         | "DE"      | [true, false, true, true, false, true]
        "015785"         | "DE"      | [true, false, true, true, false, true]
        "015786"         | "DE"      | [true, false, true, true, false, true]
        "015787"         | "DE"      | [true, false, true, true, false, true]
        "015788"         | "DE"      | [true, false, true, true, false, true]
        "0157890"        | "DE"      | [true, false, true, true, false, true]
        "0157891"        | "DE"      | [true, false, true, true, false, true]
        "0157892"        | "DE"      | [true, false, true, true, false, true]
        "0157893"        | "DE"      | [true, false, true, true, false, true]
        "0157894"        | "DE"      | [true, false, true, true, false, true]
        "0157895"        | "DE"      | [true, false, true, true, false, true]
        "0157896"        | "DE"      | [true, false, true, true, false, true]
        "0157897"        | "DE"      | [true, false, true, true, false, true]
        "0157898"        | "DE"      | [true, false, true, true, false, true]
        // 0157899 is reserved for voicemail - see tests below

        "015790"         | "DE"      | [true, false, true, true, false, true]
        "015791"         | "DE"      | [true, false, true, true, false, true]
        "015792"         | "DE"      | [true, false, true, true, false, true]
        "015793"         | "DE"      | [true, false, true, true, false, true]
        "015794"         | "DE"      | [true, false, true, true, false, true]
        "015795"         | "DE"      | [true, false, true, true, false, true]
        "015796"         | "DE"      | [true, false, true, true, false, true]
        "015797"         | "DE"      | [true, false, true, true, false, true]
        "015798"         | "DE"      | [true, false, true, true, false, true]
        "0157990"        | "DE"      | [true, false, true, true, false, true]
        "0157991"        | "DE"      | [true, false, true, true, false, true]
        "0157992"        | "DE"      | [true, false, true, true, false, true]
        "0157993"        | "DE"      | [true, false, true, true, false, true]
        "0157994"        | "DE"      | [true, false, true, true, false, true]
        "0157995"        | "DE"      | [true, false, true, true, false, true]
        "0157996"        | "DE"      | [true, false, true, true, false, true]
        "0157997"        | "DE"      | [true, false, true, true, false, true]
        "0157998"        | "DE"      | [true, false, true, true, false, true]
        // 0157999 is reserved for voicemail - see tests below

        //
        // 0158
        //
        // 015800 is reserved for voicemail - see tests below
        "015801"         | "DE"      | [true, false, true, true, false, true]
        "015802"         | "DE"      | [true, false, true, true, false, true]
        "015803"         | "DE"      | [true, false, true, true, false, true]
        "015804"         | "DE"      | [true, false, true, true, false, true]
        "015805"         | "DE"      | [true, false, true, true, false, true]
        "015806"         | "DE"      | [true, false, true, true, false, true]
        "015807"         | "DE"      | [true, false, true, true, false, true]
        "015808"         | "DE"      | [true, false, true, true, false, true]
        "015809"         | "DE"      | [true, false, true, true, false, true]
        "01581"          | "DE"      | [true, false, true, true, false, true]
        "01582"          | "DE"      | [true, false, true, true, false, true]
        "01583"          | "DE"      | [true, false, true, true, false, true]
        "01584"          | "DE"      | [true, false, true, true, false, true]
        "01585"          | "DE"      | [true, false, true, true, false, true]
        "01586"          | "DE"      | [true, false, true, true, false, true]
        "01587"          | "DE"      | [true, false, true, true, false, true]
        "01588"          | "DE"      | [true, false, true, true, false, true]
        "01589"          | "DE"      | [true, false, true, true, false, true]

        //
        // 0159
        //
        "015900"         | "DE"      | [true, false, true, true, false, true]
        "015901"         | "DE"      | [true, false, true, true, false, true]
        "015902"         | "DE"      | [true, false, true, true, false, true]
        "0159030"        | "DE"      | [true, false, true, true, false, true]
        "0159031"        | "DE"      | [true, false, true, true, false, true]
        "0159032"        | "DE"      | [true, false, true, true, false, true]
        // 0159033 is reserved for voicemail - see tests below
        "0159034"        | "DE"      | [true, false, true, true, false, true]
        "0159035"        | "DE"      | [true, false, true, true, false, true]
        "0159036"        | "DE"      | [true, false, true, true, false, true]
        "0159037"        | "DE"      | [true, false, true, true, false, true]
        "0159038"        | "DE"      | [true, false, true, true, false, true]
        "0159039"        | "DE"      | [true, false, true, true, false, true]
        "015904"         | "DE"      | [true, false, true, true, false, true]
        "015905"         | "DE"      | [true, false, true, true, false, true]
        "015906"         | "DE"      | [true, false, true, true, false, true]
        "015907"         | "DE"      | [true, false, true, true, false, true]
        "015908"         | "DE"      | [true, false, true, true, false, true]
        "015909"         | "DE"      | [true, false, true, true, false, true]

        "015910"         | "DE"      | [true, false, true, true, false, true]
        "015911"         | "DE"      | [true, false, true, true, false, true]
        "015912"         | "DE"      | [true, false, true, true, false, true]
        "0159130"        | "DE"      | [true, false, true, true, false, true]
        "0159131"        | "DE"      | [true, false, true, true, false, true]
        "0159132"        | "DE"      | [true, false, true, true, false, true]
        // 0159133 is reserved for voicemail - see tests below
        "0159134"        | "DE"      | [true, false, true, true, false, true]
        "0159135"        | "DE"      | [true, false, true, true, false, true]
        "0159136"        | "DE"      | [true, false, true, true, false, true]
        "0159137"        | "DE"      | [true, false, true, true, false, true]
        "0159138"        | "DE"      | [true, false, true, true, false, true]
        "0159139"        | "DE"      | [true, false, true, true, false, true]
        "015914"         | "DE"      | [true, false, true, true, false, true]
        "015915"         | "DE"      | [true, false, true, true, false, true]
        "015916"         | "DE"      | [true, false, true, true, false, true]
        "015917"         | "DE"      | [true, false, true, true, false, true]
        "015918"         | "DE"      | [true, false, true, true, false, true]
        "015919"         | "DE"      | [true, false, true, true, false, true]

        "015920"         | "DE"      | [true, false, true, true, false, true]
        "015921"         | "DE"      | [true, false, true, true, false, true]
        "015922"         | "DE"      | [true, false, true, true, false, true]
        "0159230"        | "DE"      | [true, false, true, true, false, true]
        "0159231"        | "DE"      | [true, false, true, true, false, true]
        "0159232"        | "DE"      | [true, false, true, true, false, true]
        // 0159233 is reserved for voicemail - see tests below
        "0159234"        | "DE"      | [true, false, true, true, false, true]
        "0159235"        | "DE"      | [true, false, true, true, false, true]
        "0159236"        | "DE"      | [true, false, true, true, false, true]
        "0159237"        | "DE"      | [true, false, true, true, false, true]
        "0159238"        | "DE"      | [true, false, true, true, false, true]
        "0159239"        | "DE"      | [true, false, true, true, false, true]
        "015924"         | "DE"      | [true, false, true, true, false, true]
        "015925"         | "DE"      | [true, false, true, true, false, true]
        "015926"         | "DE"      | [true, false, true, true, false, true]
        "015927"         | "DE"      | [true, false, true, true, false, true]
        "015928"         | "DE"      | [true, false, true, true, false, true]
        "015929"         | "DE"      | [true, false, true, true, false, true]

        "015930"         | "DE"      | [true, false, true, true, false, true]
        "015931"         | "DE"      | [true, false, true, true, false, true]
        "015932"         | "DE"      | [true, false, true, true, false, true]
        "0159330"        | "DE"      | [true, false, true, true, false, true]
        "0159331"        | "DE"      | [true, false, true, true, false, true]
        "0159332"        | "DE"      | [true, false, true, true, false, true]
        // 0159333 is reserved for voicemail - see tests below
        "0159334"        | "DE"      | [true, false, true, true, false, true]
        "0159335"        | "DE"      | [true, false, true, true, false, true]
        "0159336"        | "DE"      | [true, false, true, true, false, true]
        "0159337"        | "DE"      | [true, false, true, true, false, true]
        "0159338"        | "DE"      | [true, false, true, true, false, true]
        "0159339"        | "DE"      | [true, false, true, true, false, true]
        "015934"         | "DE"      | [true, false, true, true, false, true]
        "015935"         | "DE"      | [true, false, true, true, false, true]
        "015936"         | "DE"      | [true, false, true, true, false, true]
        "015937"         | "DE"      | [true, false, true, true, false, true]
        "015938"         | "DE"      | [true, false, true, true, false, true]
        "015939"         | "DE"      | [true, false, true, true, false, true]

        "015940"         | "DE"      | [true, false, true, true, false, true]
        "015941"         | "DE"      | [true, false, true, true, false, true]
        "015942"         | "DE"      | [true, false, true, true, false, true]
        "0159430"        | "DE"      | [true, false, true, true, false, true]
        "0159431"        | "DE"      | [true, false, true, true, false, true]
        "0159432"        | "DE"      | [true, false, true, true, false, true]
        // 0159433 is reserved for voicemail - see tests below
        "0159434"        | "DE"      | [true, false, true, true, false, true]
        "0159435"        | "DE"      | [true, false, true, true, false, true]
        "0159436"        | "DE"      | [true, false, true, true, false, true]
        "0159437"        | "DE"      | [true, false, true, true, false, true]
        "0159438"        | "DE"      | [true, false, true, true, false, true]
        "0159439"        | "DE"      | [true, false, true, true, false, true]
        "015944"         | "DE"      | [true, false, true, true, false, true]
        "015945"         | "DE"      | [true, false, true, true, false, true]
        "015946"         | "DE"      | [true, false, true, true, false, true]
        "015947"         | "DE"      | [true, false, true, true, false, true]
        "015948"         | "DE"      | [true, false, true, true, false, true]
        "015949"         | "DE"      | [true, false, true, true, false, true]

        "015950"         | "DE"      | [true, false, true, true, false, true]
        "015951"         | "DE"      | [true, false, true, true, false, true]
        "015952"         | "DE"      | [true, false, true, true, false, true]
        "0159530"        | "DE"      | [true, false, true, true, false, true]
        "0159531"        | "DE"      | [true, false, true, true, false, true]
        "0159532"        | "DE"      | [true, false, true, true, false, true]
        // 0159533 is reserved for voicemail - see tests below
        "0159534"        | "DE"      | [true, false, true, true, false, true]
        "0159535"        | "DE"      | [true, false, true, true, false, true]
        "0159536"        | "DE"      | [true, false, true, true, false, true]
        "0159537"        | "DE"      | [true, false, true, true, false, true]
        "0159538"        | "DE"      | [true, false, true, true, false, true]
        "0159539"        | "DE"      | [true, false, true, true, false, true]
        "015954"         | "DE"      | [true, false, true, true, false, true]
        "015955"         | "DE"      | [true, false, true, true, false, true]
        "015956"         | "DE"      | [true, false, true, true, false, true]
        "015957"         | "DE"      | [true, false, true, true, false, true]
        "015958"         | "DE"      | [true, false, true, true, false, true]
        "015959"         | "DE"      | [true, false, true, true, false, true]

        "015960"         | "DE"      | [true, false, true, true, false, true]
        "015961"         | "DE"      | [true, false, true, true, false, true]
        "015962"         | "DE"      | [true, false, true, true, false, true]
        "0159630"        | "DE"      | [true, false, true, true, false, true]
        "0159631"        | "DE"      | [true, false, true, true, false, true]
        "0159632"        | "DE"      | [true, false, true, true, false, true]
        // 0159633 is reserved for voicemail - see tests below
        "0159634"        | "DE"      | [true, false, true, true, false, true]
        "0159635"        | "DE"      | [true, false, true, true, false, true]
        "0159636"        | "DE"      | [true, false, true, true, false, true]
        "0159637"        | "DE"      | [true, false, true, true, false, true]
        "0159638"        | "DE"      | [true, false, true, true, false, true]
        "0159639"        | "DE"      | [true, false, true, true, false, true]
        "015964"         | "DE"      | [true, false, true, true, false, true]
        "015965"         | "DE"      | [true, false, true, true, false, true]
        "015966"         | "DE"      | [true, false, true, true, false, true]
        "015967"         | "DE"      | [true, false, true, true, false, true]
        "015968"         | "DE"      | [true, false, true, true, false, true]
        "015969"         | "DE"      | [true, false, true, true, false, true]

        "015970"         | "DE"      | [true, false, true, true, false, true]
        "015971"         | "DE"      | [true, false, true, true, false, true]
        "015972"         | "DE"      | [true, false, true, true, false, true]
        "0159730"        | "DE"      | [true, false, true, true, false, true]
        "0159731"        | "DE"      | [true, false, true, true, false, true]
        "0159732"        | "DE"      | [true, false, true, true, false, true]
        // 0159733 is reserved for voicemail - see tests below
        "0159734"        | "DE"      | [true, false, true, true, false, true]
        "0159735"        | "DE"      | [true, false, true, true, false, true]
        "0159736"        | "DE"      | [true, false, true, true, false, true]
        "0159737"        | "DE"      | [true, false, true, true, false, true]
        "0159738"        | "DE"      | [true, false, true, true, false, true]
        "0159739"        | "DE"      | [true, false, true, true, false, true]
        "015974"         | "DE"      | [true, false, true, true, false, true]
        "015975"         | "DE"      | [true, false, true, true, false, true]
        "015976"         | "DE"      | [true, false, true, true, false, true]
        "015977"         | "DE"      | [true, false, true, true, false, true]
        "015978"         | "DE"      | [true, false, true, true, false, true]
        "015979"         | "DE"      | [true, false, true, true, false, true]

        "015980"         | "DE"      | [true, false, true, true, false, true]
        "015981"         | "DE"      | [true, false, true, true, false, true]
        "015982"         | "DE"      | [true, false, true, true, false, true]
        "0159830"        | "DE"      | [true, false, true, true, false, true]
        "0159831"        | "DE"      | [true, false, true, true, false, true]
        "0159832"        | "DE"      | [true, false, true, true, false, true]
        // 0159833 is reserved for voicemail - see tests below
        "0159834"        | "DE"      | [true, false, true, true, false, true]
        "0159835"        | "DE"      | [true, false, true, true, false, true]
        "0159836"        | "DE"      | [true, false, true, true, false, true]
        "0159837"        | "DE"      | [true, false, true, true, false, true]
        "0159838"        | "DE"      | [true, false, true, true, false, true]
        "0159839"        | "DE"      | [true, false, true, true, false, true]
        "015984"         | "DE"      | [true, false, true, true, false, true]
        "015985"         | "DE"      | [true, false, true, true, false, true]
        "015986"         | "DE"      | [true, false, true, true, false, true]
        "015987"         | "DE"      | [true, false, true, true, false, true]
        "015988"         | "DE"      | [true, false, true, true, false, true]
        "015989"         | "DE"      | [true, false, true, true, false, true]

        "015990"         | "DE"      | [true, false, true, true, false, true]
        "015991"         | "DE"      | [true, false, true, true, false, true]
        "015992"         | "DE"      | [true, false, true, true, false, true]
        "0159930"        | "DE"      | [true, false, true, true, false, true]
        "0159931"        | "DE"      | [true, false, true, true, false, true]
        "0159932"        | "DE"      | [true, false, true, true, false, true]
        // 0159933 is reserved for voicemail - see tests below
        "0159934"        | "DE"      | [true, false, true, true, false, true]
        "0159935"        | "DE"      | [true, false, true, true, false, true]
        "0159936"        | "DE"      | [true, false, true, true, false, true]
        "0159937"        | "DE"      | [true, false, true, true, false, true]
        "0159938"        | "DE"      | [true, false, true, true, false, true]
        "0159939"        | "DE"      | [true, false, true, true, false, true]
        "015994"         | "DE"      | [true, false, true, true, false, true]
        "015995"         | "DE"      | [true, false, true, true, false, true]
        "015996"         | "DE"      | [true, false, true, true, false, true]
        "015997"         | "DE"      | [true, false, true, true, false, true]
        "015998"         | "DE"      | [true, false, true, true, false, true]
        "015999"         | "DE"      | [true, false, true, true, false, true]

        // end of 015xx
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Mobile 15 range with voicemail infix"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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
        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG]


        when:
        PhoneNumberUtil.ValidationResult[] results = []
        for (number in numbersToTest) {
            def phoneNumber = phoneUtil.parse(number, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
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
        "015000"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-1-INFIX:13-x(x) 2-Block: 1x and 3-Block: 1xx
        "015113"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-2x-INFIX:50-(x) 2-Block: 2x and 3-Block: 2xx  First Infix: 50
        "0152050"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152150"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152250"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152350"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152450"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152550"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152650"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152750"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152850"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152950"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        // 15-2x-INFIX:55-(x) 2-Block: 2x and 3-Block: 2xx  Second Infix: 55
        "0152055"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152155"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152255"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152355"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152455"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152555"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152655"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152755"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152855"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152955"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-3-INFIX:OO-xx 3-Block: 3xx
        "015300"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-4-INFIX:OO-xx 3-Block: 4xx
        "015400"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-5-INFIX:OO-xx 3-Block: 5xx
        "015500"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-6-INFIX:OO-xx 3-Block: 6xx
        "015600"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-7x-INFIX:99-(x) 2-Block: 7x and 3-Block: 7xx
        "0157099"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157199"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157299"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157399"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157499"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0152599"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157699"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157799"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157899"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0157999"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-8-INFIX:OO-xx 3-Block: 8xx
        "015800"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // 15-9x-INFIX:33-(x) 2-Block: 9x and 3-Block: 9xx
        "0159033"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159133"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159233"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159333"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159433"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159533"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159633"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159733"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159833"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]
        "0159933"         | "DE"      | [true, true, true, false, true, true, true, true, false, true]

        // end of 015xx for voicemail
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Mobile 16 range"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG]

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
        "01600"          | "DE" | [true, false, false, true, true, false, false, true]
        "016010"         | "DE" | [true, false, false, true, true, false, false, true]
        "016011"         | "DE" | [true, false, false, true, true, false, false, true]
        "016012"         | "DE" | [true, false, false, true, true, false, false, true]
        // 016013 is reserved for voicemail - see tests below
        "016014"         | "DE" | [true, false, false, true, true, false, false, true]
        "016015"         | "DE" | [true, false, false, true, true, false, false, true]
        "016016"         | "DE" | [true, false, false, true, true, false, false, true]
        "016017"         | "DE" | [true, false, false, true, true, false, false, true]
        "016018"         | "DE" | [true, false, false, true, true, false, false, true]
        "016019"         | "DE" | [true, false, false, true, true, false, false, true]
        "01602"          | "DE" | [true, false, false, true, true, false, false, true]
        "01603"          | "DE" | [true, false, false, true, true, false, false, true]
        "01604"          | "DE" | [true, false, false, true, true, false, false, true]
        "01605"          | "DE" | [true, false, false, true, true, false, false, true]
        "01606"          | "DE" | [true, false, false, true, true, false, false, true]
        "01607"          | "DE" | [true, false, false, true, true, false, false, true]
        "01608"          | "DE" | [true, false, false, true, true, false, false, true]
        "01609"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0162
        //
        "01620"          | "DE" | [true, false, false, true, true, false, false, true]
        "01621"          | "DE" | [true, false, false, true, true, false, false, true]
        "01622"          | "DE" | [true, false, false, true, true, false, false, true]
        "01623"          | "DE" | [true, false, false, true, true, false, false, true]
        "01624"          | "DE" | [true, false, false, true, true, false, false, true]
        // 016250 is reserved for voicemail - see tests below
        "016251"         | "DE" | [true, false, false, true, true, false, false, true]
        "016252"         | "DE" | [true, false, false, true, true, false, false, true]
        "016253"         | "DE" | [true, false, false, true, true, false, false, true]
        "016254"         | "DE" | [true, false, false, true, true, false, false, true]
        // 016255 is reserved for voicemail - see tests below
        "016256"         | "DE" | [true, false, false, true, true, false, false, true]
        "016257"         | "DE" | [true, false, false, true, true, false, false, true]
        "016258"         | "DE" | [true, false, false, true, true, false, false, true]
        "016259"         | "DE" | [true, false, false, true, true, false, false, true]
        "01626"          | "DE" | [true, false, false, true, true, false, false, true]
        "01627"          | "DE" | [true, false, false, true, true, false, false, true]
        "01628"          | "DE" | [true, false, false, true, true, false, false, true]
        "01629"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0163
        //
        "01630"          | "DE" | [true, false, false, true, true, false, false, true]
        "01631"          | "DE" | [true, false, false, true, true, false, false, true]
        "01632"          | "DE" | [true, false, false, true, true, false, false, true]
        "01633"          | "DE" | [true, false, false, true, true, false, false, true]
        "01634"          | "DE" | [true, false, false, true, true, false, false, true]
        "01635"          | "DE" | [true, false, false, true, true, false, false, true]
        "01636"          | "DE" | [true, false, false, true, true, false, false, true]
        "01637"          | "DE" | [true, false, false, true, true, false, false, true]
        "01638"          | "DE" | [true, false, false, true, true, false, false, true]
        "016390"         | "DE" | [true, false, false, true, true, false, false, true]
        "016391"         | "DE" | [true, false, false, true, true, false, false, true]
        "016392"         | "DE" | [true, false, false, true, true, false, false, true]
        "016393"         | "DE" | [true, false, false, true, true, false, false, true]
        "016394"         | "DE" | [true, false, false, true, true, false, false, true]
        "016395"         | "DE" | [true, false, false, true, true, false, false, true]
        "016396"         | "DE" | [true, false, false, true, true, false, false, true]
        "016397"         | "DE" | [true, false, false, true, true, false, false, true]
        "016398"         | "DE" | [true, false, false, true, true, false, false, true]
        // 016399 is reserved for voicemail - see tests below
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Mobile 16 range with voicemail infix"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [numberUntilInfix + "000000",
                                  numberUntilInfix + "0000000",
                                  numberUntilInfix + "00000000",
                                  numberUntilInfix + "000000000",
                                  numberUntilInfix + "999999",
                                  numberUntilInfix + "9999999",
                                  numberUntilInfix + "99999999",
                                  numberUntilInfix + "999999999"]

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG]

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

    def "check if original lib fixed isPossibleNumberWithReason for German reserve 16 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH
        ]

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

    def "check if original lib fixed isPossibleNumberWithReason for German 'Funkruf' 16(8/9) range"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG
        ]

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
        // 0168, 169 are using a 14 digit national number (0164 is not further defined).
        // TODO: could 0164 needs to be covered
        "0168"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true, false, true]
        "0169"           | "DE" | [true, true, true, true, true, true, true, true, true, true, true, false, true]

    }

    def "check if original lib fixed isPossibleNumberWithReason for German Mobile 17 range"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG]

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
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0170
        //
        "01700"          | "DE" | [true, false, false, true, true, false, false, true]
        "017010"         | "DE" | [true, false, false, true, true, false, false, true]
        "017011"         | "DE" | [true, false, false, true, true, false, false, true]
        "017012"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017013 is reserved for voicemail - see tests below
        "017014"         | "DE" | [true, false, false, true, true, false, false, true]
        "017015"         | "DE" | [true, false, false, true, true, false, false, true]
        "017016"         | "DE" | [true, false, false, true, true, false, false, true]
        "017017"         | "DE" | [true, false, false, true, true, false, false, true]
        "017018"         | "DE" | [true, false, false, true, true, false, false, true]
        "017019"         | "DE" | [true, false, false, true, true, false, false, true]
        "01702"          | "DE" | [true, false, false, true, true, false, false, true]
        "01703"          | "DE" | [true, false, false, true, true, false, false, true]
        "01704"          | "DE" | [true, false, false, true, true, false, false, true]
        "01705"          | "DE" | [true, false, false, true, true, false, false, true]
        "01706"          | "DE" | [true, false, false, true, true, false, false, true]
        "01707"          | "DE" | [true, false, false, true, true, false, false, true]
        "01708"          | "DE" | [true, false, false, true, true, false, false, true]
        "01709"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0171
        //
        "01710"          | "DE" | [true, false, false, true, true, false, false, true]
        "017110"         | "DE" | [true, false, false, true, true, false, false, true]
        "017111"         | "DE" | [true, false, false, true, true, false, false, true]
        "017112"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017113 is reserved for voicemail - see tests below
        "017114"         | "DE" | [true, false, false, true, true, false, false, true]
        "017115"         | "DE" | [true, false, false, true, true, false, false, true]
        "017116"         | "DE" | [true, false, false, true, true, false, false, true]
        "017117"         | "DE" | [true, false, false, true, true, false, false, true]
        "017118"         | "DE" | [true, false, false, true, true, false, false, true]
        "017119"         | "DE" | [true, false, false, true, true, false, false, true]
        "01712"          | "DE" | [true, false, false, true, true, false, false, true]
        "01713"          | "DE" | [true, false, false, true, true, false, false, true]
        "01714"          | "DE" | [true, false, false, true, true, false, false, true]
        "01715"          | "DE" | [true, false, false, true, true, false, false, true]
        "01716"          | "DE" | [true, false, false, true, true, false, false, true]
        "01717"          | "DE" | [true, false, false, true, true, false, false, true]
        "01718"          | "DE" | [true, false, false, true, true, false, false, true]
        "01719"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0172
        //
        "01720"          | "DE" | [true, false, false, true, true, false, false, true]
        "01721"          | "DE" | [true, false, false, true, true, false, false, true]
        "01722"          | "DE" | [true, false, false, true, true, false, false, true]
        "01723"          | "DE" | [true, false, false, true, true, false, false, true]
        "01724"          | "DE" | [true, false, false, true, true, false, false, true]
        // 017250 is reserved for voicemail - see tests below
        "017251"         | "DE" | [true, false, false, true, true, false, false, true]
        "017252"         | "DE" | [true, false, false, true, true, false, false, true]
        "017253"         | "DE" | [true, false, false, true, true, false, false, true]
        "017254"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017255 is reserved for voicemail - see tests below
        "017256"         | "DE" | [true, false, false, true, true, false, false, true]
        "017257"         | "DE" | [true, false, false, true, true, false, false, true]
        "017258"         | "DE" | [true, false, false, true, true, false, false, true]
        "017259"         | "DE" | [true, false, false, true, true, false, false, true]
        "01726"          | "DE" | [true, false, false, true, true, false, false, true]
        "01727"          | "DE" | [true, false, false, true, true, false, false, true]
        "01728"          | "DE" | [true, false, false, true, true, false, false, true]
        "01729"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0173
        //
        "01730"          | "DE" | [true, false, false, true, true, false, false, true]
        "01731"          | "DE" | [true, false, false, true, true, false, false, true]
        "01732"          | "DE" | [true, false, false, true, true, false, false, true]
        "01733"          | "DE" | [true, false, false, true, true, false, false, true]
        "01734"          | "DE" | [true, false, false, true, true, false, false, true]
        // 017350 is reserved for voicemail - see tests below
        "017351"         | "DE" | [true, false, false, true, true, false, false, true]
        "017352"         | "DE" | [true, false, false, true, true, false, false, true]
        "017353"         | "DE" | [true, false, false, true, true, false, false, true]
        "017354"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017355 is reserved for voicemail - see tests below
        "017356"         | "DE" | [true, false, false, true, true, false, false, true]
        "017357"         | "DE" | [true, false, false, true, true, false, false, true]
        "017358"         | "DE" | [true, false, false, true, true, false, false, true]
        "017359"         | "DE" | [true, false, false, true, true, false, false, true]
        "01736"          | "DE" | [true, false, false, true, true, false, false, true]
        "01737"          | "DE" | [true, false, false, true, true, false, false, true]
        "01738"          | "DE" | [true, false, false, true, true, false, false, true]
        "01739"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0174
        //
        "01740"          | "DE" | [true, false, false, true, true, false, false, true]
        "01741"          | "DE" | [true, false, false, true, true, false, false, true]
        "01742"          | "DE" | [true, false, false, true, true, false, false, true]
        "01743"          | "DE" | [true, false, false, true, true, false, false, true]
        "01744"          | "DE" | [true, false, false, true, true, false, false, true]
        // 017450 is reserved for voicemail - see tests below
        "017451"         | "DE" | [true, false, false, true, true, false, false, true]
        "017452"         | "DE" | [true, false, false, true, true, false, false, true]
        "017453"         | "DE" | [true, false, false, true, true, false, false, true]
        "017454"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017455 is reserved for voicemail - see tests below
        "017456"         | "DE" | [true, false, false, true, true, false, false, true]
        "017457"         | "DE" | [true, false, false, true, true, false, false, true]
        "017458"         | "DE" | [true, false, false, true, true, false, false, true]
        "017459"         | "DE" | [true, false, false, true, true, false, false, true]
        "01746"          | "DE" | [true, false, false, true, true, false, false, true]
        "01747"          | "DE" | [true, false, false, true, true, false, false, true]
        "01748"          | "DE" | [true, false, false, true, true, false, false, true]
        "01749"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0175
        //
        "01750"          | "DE" | [true, false, false, true, true, false, false, true]
        "017510"         | "DE" | [true, false, false, true, true, false, false, true]
        "017511"         | "DE" | [true, false, false, true, true, false, false, true]
        "017512"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017513 is reserved for voicemail - see tests below
        "017514"         | "DE" | [true, false, false, true, true, false, false, true]
        "017515"         | "DE" | [true, false, false, true, true, false, false, true]
        "017516"         | "DE" | [true, false, false, true, true, false, false, true]
        "017517"         | "DE" | [true, false, false, true, true, false, false, true]
        "017518"         | "DE" | [true, false, false, true, true, false, false, true]
        "017519"         | "DE" | [true, false, false, true, true, false, false, true]
        "01752"          | "DE" | [true, false, false, true, true, false, false, true]
        "01753"          | "DE" | [true, false, false, true, true, false, false, true]
        "01754"          | "DE" | [true, false, false, true, true, false, false, true]
        "01755"          | "DE" | [true, false, false, true, true, false, false, true]
        "01756"          | "DE" | [true, false, false, true, true, false, false, true]
        "01757"          | "DE" | [true, false, false, true, true, false, false, true]
        "01758"          | "DE" | [true, false, false, true, true, false, false, true]
        "01759"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0176
        //
        "01760"          | "DE" | [true, false, false, true, true, false, false, true]
        "01761"          | "DE" | [true, false, false, true, true, false, false, true]
        "01762"          | "DE" | [true, false, false, true, true, false, false, true]
        "017630"         | "DE" | [true, false, false, true, true, false, false, true]
        "017631"         | "DE" | [true, false, false, true, true, false, false, true]
        "017632"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017633 is reserved for voicemail - see tests below
        "017634"         | "DE" | [true, false, false, true, true, false, false, true]
        "017635"         | "DE" | [true, false, false, true, true, false, false, true]
        "017636"         | "DE" | [true, false, false, true, true, false, false, true]
        "017637"         | "DE" | [true, false, false, true, true, false, false, true]
        "017638"         | "DE" | [true, false, false, true, true, false, false, true]
        "017639"         | "DE" | [true, false, false, true, true, false, false, true]
        "01764"          | "DE" | [true, false, false, true, true, false, false, true]
        "01765"          | "DE" | [true, false, false, true, true, false, false, true]
        "01766"          | "DE" | [true, false, false, true, true, false, false, true]
        "01767"          | "DE" | [true, false, false, true, true, false, false, true]
        "01768"          | "DE" | [true, false, false, true, true, false, false, true]
        "01769"          | "DE" | [true, false, false, true, true, false, false, true]

        //
        // 0177
        //
        "01770"          | "DE" | [true, false, false, true, true, false, false, true]
        "01771"          | "DE" | [true, false, false, true, true, false, false, true]
        "01772"          | "DE" | [true, false, false, true, true, false, false, true]
        "01773"          | "DE" | [true, false, false, true, true, false, false, true]
        "01774"          | "DE" | [true, false, false, true, true, false, false, true]
        "01775"          | "DE" | [true, false, false, true, true, false, false, true]
        "01776"          | "DE" | [true, false, false, true, true, false, false, true]
        "01777"          | "DE" | [true, false, false, true, true, false, false, true]
        "01778"          | "DE" | [true, false, false, true, true, false, false, true]
        "017790"         | "DE" | [true, false, false, true, true, false, false, true]
        "017791"         | "DE" | [true, false, false, true, true, false, false, true]
        "017792"         | "DE" | [true, false, false, true, true, false, false, true]
        "017793"         | "DE" | [true, false, false, true, true, false, false, true]
        "017794"         | "DE" | [true, false, false, true, true, false, false, true]
        "017795"         | "DE" | [true, false, false, true, true, false, false, true]
        "017796"         | "DE" | [true, false, false, true, true, false, false, true]
        "017797"         | "DE" | [true, false, false, true, true, false, false, true]
        "017798"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017799 is reserved for voicemail - see tests below

        //
        // 0178
        //
        "01780"          | "DE" | [true, false, false, true, true, false, false, true]
        "01781"          | "DE" | [true, false, false, true, true, false, false, true]
        "01782"          | "DE" | [true, false, false, true, true, false, false, true]
        "01783"          | "DE" | [true, false, false, true, true, false, false, true]
        "01784"          | "DE" | [true, false, false, true, true, false, false, true]
        "01785"          | "DE" | [true, false, false, true, true, false, false, true]
        "01786"          | "DE" | [true, false, false, true, true, false, false, true]
        "01787"          | "DE" | [true, false, false, true, true, false, false, true]
        "01788"          | "DE" | [true, false, false, true, true, false, false, true]
        "017890"         | "DE" | [true, false, false, true, true, false, false, true]
        "017891"         | "DE" | [true, false, false, true, true, false, false, true]
        "017892"         | "DE" | [true, false, false, true, true, false, false, true]
        "017893"         | "DE" | [true, false, false, true, true, false, false, true]
        "017894"         | "DE" | [true, false, false, true, true, false, false, true]
        "017895"         | "DE" | [true, false, false, true, true, false, false, true]
        "017896"         | "DE" | [true, false, false, true, true, false, false, true]
        "017897"         | "DE" | [true, false, false, true, true, false, false, true]
        "017898"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017899 is reserved for voicemail - see tests below

        //
        // 0179
        //
        "01790"          | "DE" | [true, false, false, true, true, false, false, true]
        "01791"          | "DE" | [true, false, false, true, true, false, false, true]
        "01792"          | "DE" | [true, false, false, true, true, false, false, true]
        "017930"         | "DE" | [true, false, false, true, true, false, false, true]
        "017931"         | "DE" | [true, false, false, true, true, false, false, true]
        "017932"         | "DE" | [true, false, false, true, true, false, false, true]
        // 017933 is reserved for voicemail - see tests below
        "017934"         | "DE" | [true, false, false, true, true, false, false, true]
        "017935"         | "DE" | [true, false, false, true, true, false, false, true]
        "017936"         | "DE" | [true, false, false, true, true, false, false, true]
        "017937"         | "DE" | [true, false, false, true, true, false, false, true]
        "017938"         | "DE" | [true, false, false, true, true, false, false, true]
        "017939"         | "DE" | [true, false, false, true, true, false, false, true]
        "01794"          | "DE" | [true, false, false, true, true, false, false, true]
        "01795"          | "DE" | [true, false, false, true, true, false, false, true]
        "01796"          | "DE" | [true, false, false, true, true, false, false, true]
        "01797"          | "DE" | [true, false, false, true, true, false, false, true]
        "01798"          | "DE" | [true, false, false, true, true, false, false, true]
        "01799"          | "DE" | [true, false, false, true, true, false, false, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Mobile 17 range with voicemail infix"(String numberUntilInfix, regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [numberUntilInfix + "000000",
                                  numberUntilInfix + "0000000",
                                  numberUntilInfix + "00000000",
                                  numberUntilInfix + "000000000",
                                  numberUntilInfix + "999999",
                                  numberUntilInfix + "9999999",
                                  numberUntilInfix + "99999999",
                                  numberUntilInfix + "999999999"]

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG]

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
        // 017xyyyyyyy(y) x = block code, yyyyyyy(y) variable line lenx of 7 - 8 digits

        //
        // 0170
        //
        "017013"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0171
        //
        "017113"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0172
        //
        "017250"         | "DE" | [true, false, false, true, true, false, false, true]
        "017255"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0173
        //
        "017350"         | "DE" | [true, false, false, true, true, false, false, true]
        "017355"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0174
        //
        "017450"         | "DE" | [true, false, false, true, true, false, false, true]
        "017455"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0175
        //
        "017513"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0176
        //
        "017633"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0177
        //
        "017799"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0178
        //
        "017899"         | "DE" | [true, false, false, true, true, false, false, true]
        //
        // 0179
        //
        "017933"         | "DE" | [true, false, false, true, true, false, false, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German ServiceNumbers 180 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG
        ]

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
        //  0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        //  Numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0180/Nummernplan0180_ServiceDiensteRufnummer.pdf?__blob=publicationFile&v=1
        //  points out, that national numbers have 10 (3+7) digits in this range, but that there are historically shorter numbers
        //  At https://data.bundesnetzagentur.de/Bundesnetzagentur/SharedDocs/ExterneLinks/DE/Sachgebiete/Telekommunikation/Nummerierung/NVMwD.0180.Rufnummer.Vergeben.zip it can be checked, that shorter numbers have 3+5 & 3+6 digits
        // 01800 is reserve
        "01801"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        "01802"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        "01803"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        "01804"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        "01805"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        "01806"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        "01807"           | "DE" | [true, true, true, true, false, false, false, true, true, true, true, true]
        // 01808 is reserve
        // 01809 is reserve
    }

    def "check if original lib fixed isPossibleNumberWithReason for German reserve 180 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH
        ]

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
        //  0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        //  Numberplan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/0180/Nummernplan0180_ServiceDiensteRufnummer.pdf?__blob=publicationFile&v=1
        //  points out, that national numbers have 10 (3+7) digits in this range, but that there are historically shorter numbers
        //  At https://data.bundesnetzagentur.de/Bundesnetzagentur/SharedDocs/ExterneLinks/DE/Sachgebiete/Telekommunikation/Nummerierung/NVMwD.0180.Rufnummer.Vergeben.zip it can be checked, that shorter numbers have 3+5 & 3+6 digits
        // reserve:

        "01800"          | "DE" | [true, true, true, true, true, true, true, true, true, true, true]
        "01808"          | "DE" | [true, true, true, true, true, true, true, true, true, true, true]
        "01809"          | "DE" | [true, true, true, true, true, true, true, true, true, true, true]

    }

    def "check if original lib fixed isPossibleNumberWithReason for German international VPN 181 range"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // TODO: Maybe IS Possible_Local_Only is better value, since VPN numbers are not public accessible, but only from numbers of same VPN
                                                              // that would mean at least first 6 to 7 digits after NAC have to be same, depending on the VPN size.

                                                              PhoneNumberUtil.ValidationResult.TOO_LONG
        ]

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
        "0181"           | "DE" | [true, true, true, false, false, false, false, false, false, false, false, true]
        "+49181"         | "FR" | [true, true, true, false, false, false, false, false, false, false, false, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German VPN 18(2-9) range"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // TODO: Maybe IS Possible_Local_Only is better value, since VPN numbers are not public accessible, but only from numbers of same VPN
                                                                                                             // that would mean at least first 4 to 9 digits after NAC have to be same, depending on the VPN size.
                                                                                                             // if such a check is added, 18 59995 would be an exception which a public accessible exception for historical reason.
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG
        ]

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
        //  018 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/Nummernplan.pdf?__blob=publicationFile&v=1
        //  Historical Reorder: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/TWiderruf.pdf?__blob=publicationFile&v=1
        //  nation number with 11 digits
        "0182"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0183"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0184"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0185"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0186"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0187"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0188"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
        "0189"           | "DE" | [true, true, true, true, true, true, true, true, false, true, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German VPN 18(2-9) range which is only reachable nationally"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // TODO: Maybe IS Possible_Local_Only is better value, since VPN numbers are not public accessible, but only from numbers of same VPN
                                                              // that would mean at least first 4 to 9 digits after NAC have to be same, depending on the VPN size.
                                                              // if such a check is added, 18 59995 would be an exception which a public accessible exception for historical reason.
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                                                              PhoneNumberUtil.ValidationResult.INVALID_LENGTH
        ]

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
        //  018 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/Nummernplan.pdf?__blob=publicationFile&v=1
        //  Historical Reorder: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/TWiderruf.pdf?__blob=publicationFile&v=1
        //  nation number with 11 digits
        "+49182"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49183"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49184"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49185"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49186"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49187"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49188"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]
        "+49189"           | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true]

    }

    def "check if original lib fixed isPossibleNumberWithReason for German VPN 018 59995 xxxx is reachable"(String reserve, regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "2",
                                  reserve + "22",
                                  reserve + "223",
                                  reserve + "2233",
                                  reserve + "22334",
                                  reserve + "223344"]

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // is reachable from normal telephony network
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
                                                              PhoneNumberUtil.ValidationResult.TOO_LONG,
        ]

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
        //  018 is VPN: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/Nummernplan.pdf?__blob=publicationFile&v=1
        //  Historical Reorder: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/018/TWiderruf.pdf?__blob=publicationFile&v=1
        //  nation number with 11 digits
        "018 59995"      | "DE" | [true, true, true, true, false, true, true]
        "+4918 59995"    | "FR" | [true, true, true, true, false, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German Online Services 019(1-4) inc. historic"(String reserve, historic,regionCode, boolean[] expectingFails) {
        given:
        String[] numbersToTest = [reserve + "",
                                  reserve + "2",
                                  reserve + "22",
                                  reserve + "223",
                                  reserve + "2233",
                                  reserve + "22334"]

        PhoneNumberUtil.ValidationResult[] expectedResults
        if (historic) {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // BnetzA mentioned historic numbers are 4 digits long
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // TODO: BnetzA only mentioned historic 4 digit numbers, but since we found 6 digit in use, we asume the gab with 5 digits should be possible
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // At TDG (Deutsche Telekom Germany) we are using historic 0191 range with a 6 digit number
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
        } else {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // BnetzA specified just 6 digits for current numbers
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
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
        reserve     | historic | regionCode | expectingFails
        //  019(1-4) is Online Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/019xyz/019xyz_node.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/019xyz/019_Nummernplan.pdf?__blob=publicationFile&v=1
        //  while currently only 019(2-4) is used, there are historically 019(1-3) allocations with other structure.
        //  are those services dead? https://www.teltarif.de/internet/by-call/
        //  Deutsche Telekom still offers 0191011 see https://www.telekom.de/hilfe/festnetz-internet-tv/anschluss-verfuegbarkeit/anschlussvarianten/festnetz-internet/einwahlnummern-internetzugang-aus-dem-ausland?samChecked=true
        //  that is historically a 0191 range, but not limit to 4 digits but using 6!
        //  Vodafone Germany is offering 0192070 see https://www.vodafone.de/media/downloads/pdf/090512_Preisliste_Vodafone_Festnetz.pdf
        //  Historical: 4 to 6
        "0191"      | true     | "DE" | [true, false, false, false, true, true]
        "0192"      | true     | "DE" | [true, false, false, false, true, true]
        "0193"      | true     | "DE" | [true, false, false, false, true, true]
        "+49191"    | true     | "FR" | [true, false, false, false, true, true]
        "+49192"    | true     | "FR" | [true, false, false, false, true, true]
        "+49193"    | true     | "FR" | [true, false, false, false, true, true]
        //  current: 6 digits
        "0194"      | false    | "DE" | [true, true, true, false, true, true]
        "+49194"    | false    | "FR" | [true, true, true, false, true, true]

    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01981 of mobile Emergency calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults
        if ((operator) && (regionCode == "DE")) {
             expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.TOO_LONG]
        } else {
             expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.TOO_SHORT,
                                                                  PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                                                                  PhoneNumberUtil.ValidationResult.TOO_LONG]
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
        //  01981 is used for emergency call routing from national mobile operators and are not callable by normal public telephony network users nor by international operators
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it could be checked if A is 2..5 and B is 1..3 (see own test below)
        //  additionally only valid NDCs see below could also be checked but that would be more a IsValid check
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01981"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true, false]
        "01981"     | true     | "DE" | [true, true, true, true, true, true, true, true, false, false, false, false, false]
        "+491981"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, false]
        "+491981"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, false]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01981xx of mobile Emergency calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults
        if ((operator) && (regionCode == "DE")) {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
        } else {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for national operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
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
        //  01981 is used for emergency call routing from national mobile operators and are not callable by normal public telephony network users nor by international operators
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it is checked if A is 2..5 and B is 1..3 - just for DE, for other countries it is INVALID Length which is tested by first 01981 test
        //  additionally only valid NDCs see below could also be checked but that would be more a IsValid check
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it

        // Telekom Deutschland GmbH
        "0198121"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198121"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198122"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198122"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198123"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198123"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        // Vodafone GmbH
        "0198131"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198131"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198132"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198132"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198133"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198133"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        // Telefónica Germany GmbH & Co. OHG
        "0198141"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198141"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198142"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198142"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198143"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198143"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        // Telefónica Germany GmbH & Co. OHG
        "0198151"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198151"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198152"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198152"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]
        "0198153"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, false]
        "0198153"     | true     | "DE" | [true, true, true, true, true, true, false, false, false, false, false]

        "+49198121"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198121"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198122"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198122"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198123"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198123"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]

        "+49198131"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198131"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198132"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198132"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198133"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198133"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]

        "+49198141"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198141"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198142"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198142"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198143"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198143"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]

        "+49198151"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198151"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198152"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198152"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198153"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
        "+49198153"   | true     | "FR" | [true, true, true, true, true, true, true, true, true, true, false]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German invalid traffic routing 01981xx of mobile Emergency calls"(String reserve, regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults = [PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH]

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
        reserve      | regionCode | expectingFails
        //  0198 is trafic control: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html
        //  Number Plan https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        //  01981 is used for emergency call routing from national mobile operators and are not callable by normal public telephony network users nor by international operators
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it is checked for non A is 2..5 and B is 1..3 - just for DE, for other countries it is INVALID Length which is tested by first 01981 test
        //  no distinguishing of user and operator needed because those ranges are INVALID for both.

        "0198100"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198101"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198102"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198103"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198104"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198105"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198106"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198107"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198108"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198109"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198110"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198111"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198112"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198113"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198114"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198115"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198116"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198117"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198118"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198119"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198120"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        // 1..3 are valid
        "0198124"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198125"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198126"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198127"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198128"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198129"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198130"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        // 1..3 are valid
        "0198134"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198135"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198136"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198137"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198138"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198139"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198140"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        // 1..3 are valid
        "0198144"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198145"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198146"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198147"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198148"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198149"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198150"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        // 1..3 are valid
        "0198154"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198155"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198156"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198157"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198158"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198159"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198160"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198161"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198162"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198163"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198164"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198165"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198166"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198167"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198168"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198169"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198170"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198171"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198172"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198173"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198174"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198175"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198176"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198177"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198178"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198179"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198180"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198181"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198182"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198183"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198184"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198185"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198186"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198187"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198188"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198189"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]

        "0198190"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198191"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198192"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198193"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198194"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198195"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198196"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198197"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198198"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
        "0198199"    | "DE"       | [true, true, true, true, true, true, true, true, true, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01982 of Emergency calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults
        if ((operator)) {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
        } else {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,  // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
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
        //  01982 is used for emergency call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01981-AB-(NDC 2-5 digits)-CC-XY
        //  additionally it could be checked if A is 2..5 and B is 1..3 (see own test below)
        //  additionally only valid NDCs see below could also be checked but that would be more a IsValid check
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01982"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, true]
        "01982"     | true     | "DE" | [true, true, true, false, false, false, false, false, true, true, true]
        "+491982"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true]
        "+491982"   | true     | "FR" | [true, true, true, false, false, false, false, false, true, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01986 of public service calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults
        if ((operator)) {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH]
        } else {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH]
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
        //  01986 is used for public service call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01986-115
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01986"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]
        "01986"     | true     | "DE" | [true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491986"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491986"   | true     | "FR" | [true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01987 of EU public service calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults
        if ((operator)) {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
        } else {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
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
        //  01987 is used for EU public service call routing from operators and are not callable by normal public telephony network users (TODO: verfiy it is callable by international operators, which is assumed, because +49 is usable (unlike at 01981)
        //  01987-xyz
        //  for traditional libphone it makes no difference if number is used by public user or operator, so one of it will always fail until it could distinguish it
        "01987"     | false    | "DE" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "01987"     | true     | "DE" | [true, true, true, false, true, true, true, true, true, false, true, true, true]
        "+491987"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491987"   | true     | "FR" | [true, true, true, false, true, true, true, true, true, false, true, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01988 for international free calls"(String reserve, operator,regionCode, boolean[] expectingFails) {
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

        PhoneNumberUtil.ValidationResult[] expectedResults
        if ((operator)) {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.IS_POSSIBLE, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
        } else {
            expectedResults = [PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_SHORT,
                               PhoneNumberUtil.ValidationResult.INVALID_LENGTH, // not callable public, but for operators
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG,
                               PhoneNumberUtil.ValidationResult.TOO_LONG]
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
        "01988"     | true     | "DE" | [true, true, false, true, true, true, true, true, false, true, true, true, true]
        "+491988"   | false    | "FR" | [true, true, true, true, true, true, true, true, true, true, true, true, true]
        "+491988"   | true     | "FR" | [true, true, false, true, true, true, true, true, false, true, true, true, true]
    }

    def "check if original lib fixed isPossibleNumberWithReason for German traffic routing 01989 for Call Assistant"(String number, boolean Operator, regionCode, expectedResult, expectingFail) {
        given:
        // Operator is currently not usable in original methods (just a preparation)
        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"

        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number     | Operator    | regionCode  | expectedResult                                           | expectingFail
        // traffic routing is described in https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Verkehrslenkungsnr/NummernplanVerkehrslenkungsnrn.pdf?__blob=publicationFile&v=1
        // https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/118xy/118xyNummernplan.pdf?__blob=publicationFile&v=1

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "019890"   | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198900"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "01989000" | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        "019890000"| true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019891"   | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198910"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        "01989100" | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019899"   | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198999"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        "01989999" | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "019890"   | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198900"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "01989000" | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        "019890000"| false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019891"   | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198910"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        "01989100" | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019899"   | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198999"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        "01989999" | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "019890"   | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198900"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "01989000" | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        "019890000"| true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019891"   | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198910"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        "01989100" | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019899"   | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198999"  | true        | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // not callable public, but for operators
        "01989999" | true        | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "01989"    | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "019890"   | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198900"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "01989000" | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        "019890000"| false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019891"   | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198910"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        // Call Assistant of Deutsche Telekom
        "0198933"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        "01989100" | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "019899"   | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        "0198999"  | false       | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not callable public, but for operators
        "01989999" | false       | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "+491989"    | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919890"   | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+49198900"  | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+491989000" | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919890000"| true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919891"   | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+49198910"  | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        // Call Assistant of Deutsche Telekom
        "+49198933"  | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+491989100" | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919899"   | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+49198999"  | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+491989999" | true        | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true

        // prefix 118 is replaced by 01989 and the rest of the general 5 digits long - except if the 4th digit is 0, than it is six digits long
        "+491989"    | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919890"   | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+49198900"  | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+491989000" | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919890000"| false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919891"   | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+49198910"  | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        // Call Assistant of Deutsche Telekom
        "+49198933"  | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+491989100" | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+4919899"   | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+49198999"  | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
        "+491989999" | false       | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH         | true
    }

    def "check if original lib fixed isPossibleNumberWithReason for invalid German NDC"(String number, regionCode, expectedResult, expectingFail) {
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


        when: "get number isPossibleNumberWithReason: $number"
        PhoneNumberUtil.ValidationResult[] results = []
        for (n in numbersToTest) {
            def phoneNumber = phoneUtil.parse(n, regionCode)
            results += phoneUtil.isPossibleNumberWithReason(phoneNumber)
        }

        then: "is number expected: $expectedResult"
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail, numbersToTest[i], regionCode)
        }

        where:

        number               | regionCode  | expectedResult                                            | expectingFail
        // short numbers which are reached internationally are also registered as NDC
        // TODO: 010 is operator selection see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/010/010xy_node.html ... will be canceled 31.12.2024
        "010"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0110 is checked in Emergency short codes see above
        // ---
        "0111"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0112 is checked in Emergency short codes see above
        // ---
        "0113"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0114"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0115 is checked in German Government short codes see above
        // ---
        // ---
        // 0116 is checked in EU social short codes see above
        // ---
        "0117"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0118 is checked in German call assistant services see above
        // ---
        "0119"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "012"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0120"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0121"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0122"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0123"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0124"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0125"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0126"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0127"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0128"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0129"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0130"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0131"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0132"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0133"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0134"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0135"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0136"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0137 is checked in Mass Traffic see above
        // ---
        "0138"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0139"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "014"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0140"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0141"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0142"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0143"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0144"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0145"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0146"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0147"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0148"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0149"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
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
        "0190"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve - previously premium rate numbers, which were relocated to 0900
        // ---
        // 019(1-4) is checked in German Online Services 019(1-4) inc. historic
        // ---
        "0195"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
        "0196"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
        "0197"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
        // ---
        // Traffic management numbers are only valid between operators - so not for end customers to call
        // ---
        "01980"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
        // ---
        // 01981 is checked in German traffic routing 01981 of mobile Emergency calls
        // ---
        // ---
        // 01982 is checked in German traffic routing 01982 for emergency calls
        // ---
        "01983"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
        "01984"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
        "01985"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // Reserve
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
        // TODO: 0199 - network internal Routing
        // ---

        // TODO: 0700 - personal: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0700/0700_node.html
        // TODO: 0800 - free call: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0800/0800_node.html
        // TODO: 0900 - premium: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0900/start.html
        // TODO: 09009 - Dialer: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/09009/9009_node.html
        // TODO: 031 - Testnumbers: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/031/031_node.html

        // TODO: DRAMA numbers: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mittlg148_2021.pdf?__blob=publicationFile&v=1

        // invalid area code for germany - using Invalid_Lenth, because its neither to long or short, but just NDC is not valid.
        "0200"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0201 is Essen
        // 0202 is Wuppertal
        // 0203 is Duisburg
        "02040"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02041 is Bottrop
        "02042"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02043 is Gladbeck
        "02044"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02045 is Bottrop-Kirchhellen
        "02046"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02047"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02048"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02049"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02050"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02051 till 02054 are in use
        "02055"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02056 is Heiligenhausen
        "02057"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02058 is Wülfrath
        "02059"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02060"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02061"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02062"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02063"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02064 till 02066 is in use
        "02067"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02068"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02069"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0207"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0208 & 0209 is in use
        "02100"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02101"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02102 till 02104 is in use
        "02105"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02106"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02107"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02108"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02109"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // special case 0212 for Solingen also covers 02129 for Haan Rheinl since Solingen may not use numbers starting with 9
        "02130"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02131 till 02133 is in use
        "02134"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02135"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02136"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02137 is Neuss-Norf
        "02138"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02139"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0214 is Leverkusen
        // 02150 till 02154 is in use
        "02155"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02156 till 02159 is in use
        "02160"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02161 till 02166 is in use
        "02167"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02168"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02169"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02170"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02171 is Leverkusen-Opladen
        "02172"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02173 till 02175 is in use
        "02176"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02177"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02178"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02179"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02180"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02181 till 02183 is in use
        "02184"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02185"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02186"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02187"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02188"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02189"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02190"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02191 till 02193 is in use
        "02194"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02195 till 02196 is in use
        "02197"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02198"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02199"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02200"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02201"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02202 till 02208 is in use
        "02209"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0221 is Köln
        "02220"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02221"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02222 till 02228 is in use
        "02229"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02230"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02231"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02232 till 02238 is in use
        "02239"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02240"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02241 till 02248 is in use
        "02249"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02250"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02251 till 02257 is in use
        "02258"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02259"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02260"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02261 till 02269 is in use
        "02270"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02271 till 02275 is in use
        "02276"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02277"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02278"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02279"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0228 is Bonn
        "02290"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02291 till 02297 is in use
        "02298"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02299"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02300"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02301 till 02309 is in use
        // 0231 is Dortmund
        "02320"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02321"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02322"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02323 till 02325 is in use
        "02326"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02327 is Bochum-Wattenscheid
        "02328"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02329"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02330 till 02339 is in use
        // 0234 is Bochum
        "02350"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02351 till 02355 is in use
        "02356"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02357 till 02358 is in use
        // 02360 till 02369 is in use
        "02370"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02371 till 02375 is in use
        "02376"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02377 till 02379 is in use
        "02380"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02381 till 02385 is in use
        "02386"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02387 till 02389 is in use
        "02390"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02391 till 02395 is in use
        "02396"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02397"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02398"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02399"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02400"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02401 till 02409 is in use
        // 0241 is Aachen
        "02420"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02421 till 02429 is in use
        "02430"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02431 till 02436 is in use
        "02437"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02438"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02439"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02440 till 02441 is in use
        "02442"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02443 till 02449 is in use
        "02450"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02451 till 02456 is in use
        "02457"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02458"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02459"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02460"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02461 till 02465 is in use
        "02466"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02467"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02468"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02469"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02470"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02471 till 02474 is in use
        "02475"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02476"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02477"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02478"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02479"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02480"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02481"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02482 is Hellenthal
        "02483"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02484 till 02486 is in use
        "02487"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02488"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02489"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0249"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02500"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02501 till 02502 is in use
        "02503"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02504 till 02509 is in use
        // 0251 is Münster
        // 02520 till 02529 is in use
        "02530"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02531"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02532 till 02536 is in use
        "02531"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02538 is Drensteinfurt-Rinkerode
        "02539"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02540"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02541 till 02543 is in use
        "02544"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02545 till 02548 is in use
        "02549"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02550"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02551 till 02558 is in use
        "02559"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02560"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02561 till 02568 is in use
        "02569"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02570"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02571 till 02575 is in use
        "02576"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02577"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02578"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02579"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02580"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02581 till 02588 is in use
        "02589"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02590 till 02599 is in use
        "02600"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02601 till 02608 is in use
        "02609"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0261 is Koblenz am Rhein
        // 02620 till 02628 is in use
        "02629"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02630 till 02639 is in use
        "02640"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02641 till 02647 is in use
        "02648"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02649"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02650"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02651 till 02657 is in use
        "02658"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02659"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02660"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02661 till 02664 is in use
        "02665"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02666 till 02667 is in use
        "02668"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02669"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02670"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02671 till 02678 is in use
        "02679"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02680 till 02689 is in use
        "02690"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02691 till 02697 is in use
        "02698"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02699"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0271 is Siegen
        "02720"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02721 till 02725 is in use
        "02726"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02727"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02728"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02729"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02730"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02731"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02731 till 02739 is in use
        "02740"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02741 till 02745 is in use
        "02746"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02747 is Molzhain
        "02748"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02749"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02750 till 02755 is in use
        "02756"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02757"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02758 till 02759 is in use
        "02760"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02761 till 02764 is in use
        "02765"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02766"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02767"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02768"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02769"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02770 till 02779 is in use
        "02780"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02781 till 02784 is in use
        "02785"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02786"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02787"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02788"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02789"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0279"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02790"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02791"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02792"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02793"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02794"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02795"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02796"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02797"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02798"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02799"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02800"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02801 till 02804 is in use
        "02805"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02806"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02807"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02808"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02809"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0281 is Wesel
        "02820"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02821 till 02828 is in use
        "02829"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02830"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02831 till 02839 is in use
        "02840"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02841 till 02845 is in use
        "02846"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02847"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02848"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02849"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02850 till 02853 is in use
        "02854"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02855 till 02859 is in use
        "02860"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02861 till 02867 is in use
        "02868"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02869"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02870"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02871 till 02874 is in use
        "02875"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02876"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02877"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02878"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02879"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0288"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0289"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02900"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02901"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02902 till 02905 is in use
        "02906"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02907"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02908"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02909"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0291 is Meschede
        "02920"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02921 till 02925 is in use
        "02926"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02927 till 02928 is in use
        "02929"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02930"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02931 till 02935 is in use
        "02936"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02937 till 02938 is in use
        "02939"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02940"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02941 till 02945 is in use
        "02946"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02947 till 02948 is in use
        "02949"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02950"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02951 till 02955 is in use
        "02956"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02957 till 02958 is in use
        "02959"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02960"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02961 till 02964 is in use
        "02965"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02966"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02967"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02968"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02969"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02970"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02971 till 02975 is in use
        "02976"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02977 is Schmallenberg-Bödefeld
        "02978"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02979"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02980"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02981 till 02985 is in use
        "02986"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02987"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02988"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02989"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02990"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02991 till 02994 is in use
        "02995"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02996"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02997"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02998"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02999"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 030 is Berlin
        // 0310 is National Test for length 3 -> TODO: OWN Test
        // 0311 is National Test for length 3 -> TODO: OWN Test
        "0312"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0313"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0314"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0315"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0316"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0317"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0318"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0319"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 032 is non geographical 11 till 13 length -> TODO: OWN Test
        "03300"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03301 till 03304 is in use
        "033050"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033051 till 033056 is in use
        "033057"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033058"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033059"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03306 till 03307 is in use
        // 033080 is Marienthal Kreis Oberhavel
        "033081"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033082 till 033089 is in use
        "033090"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033091"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033092"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033093 till 033094 is in use
        "033095"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033096"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033097"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033098"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033099"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0331 is Potsdam
        // 033200 till 033209 is in use
        // 03321 is Nauen Brandenburg
        // 03322 is Falkensee
        // 033230 till 033235 is in use
        "033236"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033237 till 033239 is in use
        "03324"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03325"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03326"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03327 till 03329 is in use
        "03330"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03331 till 03332 is in use
        "033330"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033331 till 033338 is in use
        "033339"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03334 till 03335 is in use
        "033360"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033361 till 033369 is in use
        // 03337 till 03338 is in use
        "033390"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033391"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033392"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033393 till 033398 is in use
        "033399"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03340"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03341 till 03342 is in use
        "033430"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033431"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033432 till 033439 is in use
        // 03344 is Bad Freienwalde
        "033450"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033451 till 033452 is in use
        "033453"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033454 is Wölsickendorf/Wollenberg
        "033455"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033456 till 033458 is in use
        "033459"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03346 is Seelow
        // 033470 is Lietzen
        "033471"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033472 till 033479 is in use
        // 0335 is Frankfurt (Oder)
        "033600"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033601 till 033609 is in use
        // 03361 till 03362 is in use
        "033630"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033631 till 033638 is in use
        "033639"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03364 is Eisenhüttenstadt
        "033650"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033651"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033652 till 033657 is in use
        "033658"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033659"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03366 is Beeskow
        "033670"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033671 till 033679 is in use
        "03368"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03369"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033700"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033701 till 033704 is in use
        "033705"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033706"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033707"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033708 is Rangsdorf
        "033709"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03371 till 03372 is in use
        "033730"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033731 till 033734 is in use
        "033735"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033736"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033737"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033738"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033739"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033740"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033741 till 033748 is in use
        "033749"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03375 is Königs Wusterhausen
        // 33760 is Münchehofe Kreis Dahme-Spreewald
        "033761"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033762 till 033769 is in use
        // 03377 till 03379 is in use
        "03380"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03381 till 03382 is in use
        // 033830 till 033839 is in use
        "033840"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033841 is Belzig
        "033842"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033843 till 033849 is in use
        // 03385 till 03386 is in use
        // 033870 is Zollchow bei Rathenow
        "033871"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033872 till 033878 is in use
        "033879"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03388"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03389"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03390"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03391 is Neuruppin
        // 033920 till 033929 is in use
        "033930"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033931 till 033933 is in use
        "033934"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033935"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033936"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033937"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033938"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033939"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03394 till 03395 is in use
        "033960"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033961"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033962 till 033969 is in use
        // 033970 till 033979 is in use
        "033980"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033981 till 033984 is in use
        "033985"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033986 is Falkenhagen Kreis Prignitz
        "033987"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033988"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033989 is Sadenbeck
        "03399"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0340 till 0341 is in use
        "034200"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034201"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034202 till 034208 is in use
        "034209"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03421 is Torgau
        "034220"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034221 till 034224 is in use
        "034225"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034226"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034227"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034228"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034229"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03423 is Eilenburg
        "034240"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034241 till 034244 is in use
        "034245"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034246"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034247"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034248"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034249"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03425 is Wurzen
        "034260"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034261 till 034263 is in use
        "03427"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03428"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034290"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034291 till 034293 is in use
        "03430"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03431 is Döbeln
        "034320"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034321 till 034322 is in use
        "034323"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034324 till 034325 is in use
        "034326"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034327 till 034328 is in use
        "034329"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03433 is Borna Stadt
        "034340"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034341 till 034348 is in use
        "034349"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03435 is Oschatz
        "034360"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034361 till 034364 is in use
        "034365"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034366"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034367"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034368"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034369"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03437 is Grimma
        "034380"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034381 till 034386 is in use
        "034387"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034388"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034389"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03439"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03440"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03441 is Zeitz
        "034420"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034421"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034422 till 034426 is in use
        "034427"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034428"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034429"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03443 is Weissenfels Sachsen-Anhalt
        "034440"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034441 is Hohenmölsen
        "034442"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034443 till 034446 is in use
        "034447"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034448"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034449"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03445 is Naumburg Saale
        "034460"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034461 till 034467 is in use
        "034468"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034469"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03447 till 03448 is in use
        "034490"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034491 till 034498 is in use
        "034499"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0345 is Halle Saale
        // 034600 toll 034607 is in use
        "034608"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034609 is Salzmünde
        // 03461 till 03462 is in use
        "034630"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034631"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034632 till 034633 is in use
        "034634"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034635 till 034639 is in use
        // 03464 is Sangerhausen
        "034650"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034651 till 034654 is in use
        "034655"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034656 is Wallhausen Sachsen-Anhalt
        "034657"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034658 till 034659 is in use
        // 03466 is Artern Unstrut
        "034670"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034671 till 034673 is in use
        "034674"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034675"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034676"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034677"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034678"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034679"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03468"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034690"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034691 till 034692 is in use
        "034693"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034694"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034695"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034696"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034697"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034698"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034699"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03470"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03471 is Bernburg Saale
        "034720"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034721 till 034722 is in use
        "034723"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034724"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034725"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034726"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034727"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034728"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034729"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 3473 is Aschersleben Sachsen-Anhalt
        "034740"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034741 till 034743 is in use
        "034744"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034745 till 034746 is in use
        "034747"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034748"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034749"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03475 till 03476 is in use
        "034770"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034771 till 034776 is in use
        "034777"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034778"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034779 is Abberode
        "034780"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034781 till 034783 is in use
        "034784"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034785 is Sandersleben
        "034786"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034787"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034788"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034789"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03479"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0348"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034900"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034901 is Roßlau Elbe
        "034902"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034903 till 034907
        "034908"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034909 is Aken Elbe
        // 03491 till 03494 (yes full 03492x is used, too) is in use
        "034950"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034951"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034952"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034953 till 034956
        "034957"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034958"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034959"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03496 is Köthen Anhalt
        "034970"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034971"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034972"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034973 is Osternienburg
        "034974"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034975 till 034979 is in use
        "03498"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03499"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03500"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03501 is Pirna
        "035029"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035030"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035031"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035032 till 035033 is in use
        "035034"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035035"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035036"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035038"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035038"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035039"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03504 is Dippoldiswalde
        "035050"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035051"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035052 till 035058
        "035059"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03506"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03507"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03508"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03509"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0351 is Dresden
        // 03520x till 03525 is in use (inclusive complete 03524x)
        "035260"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035261"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035262"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035263 till 035268
        "035269"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03527"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03529 till 03529 is in use
        "03530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03531 is Finsterwalde
        "035320"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035321"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035322 till 035327
        "035328"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035329 is Dollenchen
        // 03533 is Elsterwerda
        "035340"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035341 till 035343
        "035344"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035345"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035346"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035347"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035348"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035349"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03535 is Herzberg Elster
        "035360"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035361 till 035365 is in use
        "035366"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035367"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035369"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035369"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03537 is Jessen Elster
        "035380"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035381"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035382"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035383 till 035389 is in use
        "03539"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03541 till 03542 is in use
        "035430"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035431"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035432"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035433 till 035436 is in use
        "035437"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035438"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035439 is Zinnitz
        // 03544 is Luckau Brandenburg
        "035450"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035451 till 035456 is in use
        "035457"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035458"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035459"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03546 is Lübben Spreewald
        "035470"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035471 till 035478 is in use
        "035479"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03548"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03549"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0355 is Cottbus
        // 03560x till 03564 is in use
        "03565"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03566"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03567"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03568"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035690"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035691 till 035698 is in use
        "035699"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03570"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03571 is Hoyerswerda
        "035720"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035721"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035722 till 035728 is in use
        "035729"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03573 till 03574 is in use
        "035750"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035751 till 035756 is in use
        "035757"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035758"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035759"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03576 is Weisswasser
        "035770"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035771 till 035775 is in use
        "035776"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035777"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035778"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035779"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03578 is Kamenz
        "035790"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035791"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035792 till 035793 is in use
        "035794"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035795 till 035797 is in use
        "035798"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035799"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03580"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03581 is Görlitz
        // 035820 is Zodel
        "035821"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035822 till 035823 is in use
        "035824"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035825 till 035829 is in use
        // 03583 is Zittau
        "035840"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035841 till 035844 is in use
        "035845"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035846"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035847"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035848"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035849"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03585 till 03586 is in use
        "035870"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035871"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035872 till 035877 is in use
        "035878"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035879"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03588 is Niesky
        "035890"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035891 till 0358595 is in use
        "035896"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035897"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035898"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035899"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03590"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03591 till 03594 (including total 03593x) is in use
        "035950"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035951 till 035955 is in use
        "035956"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035957"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035958"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035959"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03596 is Neustadt in Sachsen
        "035970"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035971 till 035975 is in use
        "035976"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035977"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035978"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035979"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03598"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03599"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03600"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03601 till 03603 (including total 03602x) is in use
        "036040"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036041 till 036043 is in use
        "036044"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036045"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036046"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036047"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036048"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036049"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03605 till 03606 is in use
        "036070"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036071 till 036072 is in use
        "036073"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036074 till 036077 is in use
        "036078"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036079"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036080"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036081 till 036085 is in use
        "036086"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036087 is Wüstheuterode
        "036088"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036089"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03609"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0361 is Erfurt
        // 03620x till 03624 is in use
        "036250"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036251"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036252 till 036259 is in use
        "03626"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03627"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03628 till 03629 is in use
        "03630"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03631 till 03632 is in use
        // 036330 till 036338 is in use
        "036339"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03634 till 03637x is in use
        "03638"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03639"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03640"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03641 is Jena
        "036420"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036421 till 036428 is in use
        "036429"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03643 till 03644 is in use
        // 036450 till 036454 is in use
        "036455"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036456"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036457"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036458 till 036459 is in use
        "036460"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036461 till 036465 is in use
        "036466"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036467"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036468"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036469"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03647 is Pößneck
        "036480"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036481 till 036484 is in use
        "036485"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036486"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036487"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036488"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036489"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03649"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0365 is Gera
        "036600"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036601 till 036608 is in use
        "036609"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03661 is Greiz
        "036620"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036621 till 036626 is in use
        "036627"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036628 is Zeulenroda
        "036629"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03663 is Schleiz
        // 036640 is Remptendorf
        "036641"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036642 till 036649 is in use
        "036650"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036651 till 036653 is in use
        "036654"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036655"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036656"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036657"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036658"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036659"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03666"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03667"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03668"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036690"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036691 till 036695 is in use
        "036696"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036697"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036698"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036699"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036700"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036701 till 036705 is in use
        "036706"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036707"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036708"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036709"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03671 till 03673x is in use
        "036740"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036741 till 03644 is in use
        "036745"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036746"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036747"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036748"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036749"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03675 is Heubisch
        "036760"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036761 till 036762 is in use
        "036763"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036764 is Neuhaus-Schierschnitz
        "036765"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036766 is SChalkau
        "036767"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036768"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036769"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03677 is Ilmenau Thüringen
        "036780"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036781 till 036785 is in use
        "036786"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036787"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036788"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036789"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03679 is Suhl
        "03680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03681 till 03686 (inlcuding total 03684x) is in use
        // 036870 till 036871 is in use
        "036872"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036873 till 036875 is in use
        "036876"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036877"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036878 is Oberland
        "036879"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03690"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036891 till 03693 (including total 036892x) is in use
        // 0368940 till 0368941 is in use
        "036942"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0368943 till 0368949 is in use
        // 03695 is Bad Salzungen
        "036960"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036961 till 036969 is in use
        "03697"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03698"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03699"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0370"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0371 is Chemnitz Sachsen
        // 037200 is Wittgensdorf bei Chemnitz
        "037201"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037202 till 03724 is in use
        "037205"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037206 till 037209 is in use
        // 03721 till 03727 is in use
        "03728"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037290"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037291 till 037298 is in use
        "037299"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03731 till 03733 (including total 03732x) is in use
        "037340"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037341 till 037344 is in use
        "037345"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037346 till 037349 is in use
        // 03735 till 03737 (including total 03736x) is in use
        "037380"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037381 till 037384 is in use
        "037385"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037386"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037387"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037388"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037389"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03739"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03740"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03741 is Plauen
        "037420"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037421 till 037423 is in use
        "037424"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037425"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037426"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037427"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037428"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037429"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03473x till 03745 is in use
        "037460"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037461"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037462 till 037465 is in use
        "037466"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037467 till 037468 is in use
        "037469"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03747"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03748"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03749"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0375 is Zwickau
        // 03760x till 03765 is in use
        "03766"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03767"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03768"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03769"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03770"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03771 till 03774 is in use
        "037750"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037751"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037752 is Eibenstock
        "037753"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037754 till 037757
        "037758"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037759"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03776"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03777"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03778"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03779"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0378"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0379"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0380"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0381 is Rostock
        "038200"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038201 till 038209
        // 03821 till 03822x
        "038230"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038231 till 038234
        "038235"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038236"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038237"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038238"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038239"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03824"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03825"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03826"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03827"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03828"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038290"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038291"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038292 till 038297 is in use
        "038298"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038299"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03830x till 03831 is in use
        // 038320 till 038328 is in use
        "038329"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038330"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08331 till 038334 is in use
        "038335"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038336"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038337"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038338"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038339"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03834 is Greifswald
        "038350"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038351 till 038356 is in use
        "038357"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038358"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038359"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03836 till 03838 (including total 03837x) is in use
        "038390"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038391 till 038393 is in use
        "038394"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038395"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038396"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038397"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038398"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038399"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03840"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03841 id Neukloster
        "038420"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038421"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038422 till 038429
        // 03843 till 03845x is in use
        "038460"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038461 till 038462 is in use
        "038463"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038464 is Bernitt
        "038465"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038466 is Jürgenshagen
        "038467"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038468"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038469"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03847 is Sternberg
        "038480"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038481 till 038486 is in use
        "038487"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038488 is Demen
        "038489"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03849"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0385 is Schwerin
        // 03860 till 03861 is in use
        "03862"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03863 is Crivitz
        "03864"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03865 till 03869 is in use
        "03870"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03871 till  03872x is in use
        "038730"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038731 till 038733 is in use
        "038734"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038735 till 038738 is in use
        "038739"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03874 till 03877 (including total 03875x) is in use
        // 038780 till 038785 is in use
        "038786"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038787 till 038789 is in use
        "038790"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038791 till 038794
        "038795"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038796 till 038797
        "038798"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038799"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03880"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03881 is Grevesmühlen
        "038820"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038821 till 038828 is in use
        "038829"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03883 is Hagenow
        "038840"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038841 till 038845 is in use
        "038846"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038847 till 038848 is in use
        "038849"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038850 till 038856 is in use
        "038857"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038858 till 038859 is in use
        // 03886 is Gadebusch
        "038870"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038871 till 038876 is in use
        "038877"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038878"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038879"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03888"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03889"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0389"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03900x till 03905x (including total 03903x) is in use
        "039060"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039061 till 039062 is in use
        "039063"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039064"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039065"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039066"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039067"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039068"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039069"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03907 till 03909 (including total 03908x) is in use
        // 0391 is Magdeburg
        // 03920x till 03921 is in use
        "039220"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039221 till 039226 is in use
        "039227"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039228"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039229"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03923 is Zerbst
        "039240"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039241 till 039248 is in use
        "0392498"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03925 is Stassfurt
        "039260"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039261"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039262 till 039268 is in use
        "039269"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03927"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03928 is Schönebeck Elbe
        "039290"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039291 till 039298 is in use
        "039299"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03930"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03931 is Stendal
        // 039320 till 039325 is in use
        "039326"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039327 till 039329 is in use
        // 03933 is Genthin
        "039340"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039341 till 039349 is in use
        // 03935 is Tangerhütte
        "039360"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039361 till 039366 is in use
        "039367"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039368"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039369"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03937 is Osterburg Altmark
        "039380"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039381"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039382 till 039384 is in use
        "039385"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039386 till 039389 is in use
        // total 03939x is in use
        // 03940x till 03941 is in use
        "039420"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039421 till 039428 is in use
        "039429"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03943 till 03944 is in use
        "039450"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039451 till 039459 is in use
        // 03946 till 03947 is in use
        "039480"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039481 till 039485 is in use
        "039486"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039487 till 039489 is in use
        // 03949 is Oschersleben Bode
        // 0395 is Zwiedorf
        // 039600 till 039608 is in use
        "039609"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03961 till 03969 is in use
        "03970"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03971 is Anklam
        "039720"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039721 till 039724 is in use
        "039725"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039726 till 039728 is in use
        "039729"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03973 till 03974x is in use
        "039750"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039751 till 039754 is in use
        "039755"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039756"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039757"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039758"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039759"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03976 is Torgelow bei Uckermünde
        "039770"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039771 till 039779 is in use
        "03980"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03981 to 03982x is in use
        "039830"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039831 till 039833 is in use
        "039834"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039835"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039836"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039837"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039838"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039839"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03984 is Prenzlau
        "039850"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039851 till 039859 is in use
        "039860"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039861 till 039863 is in use
        "039863"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039864"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039865"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039866"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039867"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039868"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039869"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03987 is Templin
        "039880"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039881 till 039889 is in use
        "03989"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03990"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03991 is Waren Müritz
        "039920"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039921 till 039929 is in use
        "039930"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039931 till 039934 is in use
        "039935"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039936"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039937"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039938"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039939"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03994 is Malchin
        "039950"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039951 till 039957 is in use
        "039958"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039959 is Dargun
        // 03996 is Teterow
        "039970"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039971 till 039973 is in use
        "039974"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039975 till 039978
        "039979"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03998 is Demmin
        "039990"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039991 till 039999 is in use
        // 040 is Hamburg
        "04100"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04101 till 04109 is in use
        "0411"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0412x is in use
        "04130"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04131 till 04139 is in use
        // 04140 till 04144 is in use
        "04145"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04146 is Stade-Bützfleth
        "04147"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04148 till 04149 is in use
        "04150"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04151 till 04156 is in use
        "04157"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04158 till 04159 is in use
        "04160"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04161 till 04169 is in use
        "04170"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04171 till 04179 is in use
        // total 0418x is in sue
        "04190"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04191 till 04195 is in use
        "04196"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04197"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04198"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04199"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04200"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04201"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04202 till 04209 is in use
        // 0421 is Bremen
        "04220"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04221 till 04224 is in use
        "04225"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04226"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04227"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04228"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04229"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0423x till 0424x is in use
        "04250"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04251 till 04258 is in use
        "04259"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0426x is in use
        "04270"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04271 till 04277 is in use
        "04278"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04279"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04280"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04281 till 04289 is in use
        "04290"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04291"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04292 till 04298 is in use
        "04299"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04300"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04301"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04302 till 04303 is in use
        "04304"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04305 is Westensee
        "04306"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04307 till 04308 is in use
        "04309"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0431 till 0433x (including total 0432x) is in use
        // 04340 is Achterwehr
        "04341"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04342 till 04346 is in use
        "04350"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04351 till 04358 is in use
        "04359"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04360"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04361 till 04367 is in use
        "04368"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04369"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04370"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04371 till 04372 is in use
        "04373"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04374"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04375"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04376"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04377"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04378"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04379"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04380"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04381 till 04385 is in use
        "04386"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04387"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04388"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04389"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04390"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04391"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04392 till 04394 is in use
        "04395"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04396"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04397"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04398"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04399"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04400"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04401 till 04409 is in use
        // 0441 is Oldenburg (Oldb)
        "04420"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04421 till 04423 is in use
        "04424"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04425 till 04426 is in use
        "04427"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04428"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04429"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04430"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04431 till 04435 is in use
        "04436"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04437"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04438"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04439"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04440"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04441 till 04447 is in use
        "04448"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04449"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04450"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04451 till 04456 is in use
        "04457"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04458 is Wiefeldstede-Spohle
        "04459"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04460"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04461 till 04469 is in use
        "04470"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04471 till 04475 is in use
        "04476"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04477 till 04479 is in use
        // total 0448x is in use
        "04490"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04491 till 1199 is in use
        "04500"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04501 till 04506 is in use
        "04507"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04508 till 0459 is in use
        // 0451 is Lübeck
        "04520"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04521 till 04529 is in use
        "04530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04531 till 04537 is in use
        "04538"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04539 is Westerau
        "04540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04541 till 04547 is in use
        "04548"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04549"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0455x is in use
        "04560"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04561 till 04564 is in use
        "0457"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0458"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0459"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04600"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04601"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04602 till 04609 is in use
        // 0461 is Flensburg
        "04620"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04621 till 04627 is in use
        "04628"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04629"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0463x is in use
        "04640"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04641 till 04644 is in use
        "04645"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04646 is Morkirch
        "04647"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04648"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04649"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04650"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04651 is Sylt
        "04652"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04653"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04654"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04655"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04656"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04657"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04658"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04659"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04660"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04661 till 04668 is in use
        "04669"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04670"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04671 till 04674 is in use
        "04675"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04676"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04677"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04678"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04679"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04681 till 04684 is in use
        "04685"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04686"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04687"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04701"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04702 till 04708 is in use
        "04709"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0471 is Bremerhaven
        "04720"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04721 till 04725 is in use
        "04726"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04727"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04728"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04729"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04731 till 04737 is in use
        "04738"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04739"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0474x is in use
        "04750"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04751 till 04758 is in use
        "04759"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04760"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04761 till 04769 is in use
        // total 0477x is in use
        "0478"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04790"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04791 till 04796 is in use
        "04800"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04801"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04802 till 04806 is in use
        "04807"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04808"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04809"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0481 is Heide Holstein
        "04820"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04821 till 04829 is in use
        // 04830 is Süderhastedt
        "04831"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04832 till 04839 is in use
        "04840"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04841 till 04849 os in use
        "04850"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04851 till 04859 is in use
        "04860"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04861 till 04865 is in use
        "04866"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04867"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04868"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04869"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04870"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04871 till 04877 is in use
        "04878"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04879"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04880"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04881 till 04885 is in use
        "04886"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04887"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04888"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04889"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04890"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04891"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04892 till 04893 is in use
        "04894"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04895"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04896"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04897"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04898"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04899"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04900"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04901"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04902 till 04903 is in use
        "04904"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04905"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04906"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04907"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04908"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04909"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0491 is Leer Ostfriesland
        // total 0492x is in use
        "04930"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04931 till 04936 is in use
        "04937"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04938 till 04939 is in use
        "04940"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04941 till 04948 is in use
        "04949"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0495x is in use
        "04960"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04961 till 04968 is in use
        "04969"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04970"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04971 till 04977 is in use
        "04978"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04979"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0498"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0499"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0500"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0501"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05020"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05021 till 05028 is in use
        "05029"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05030"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05031 till 05037 is in use
        "05038"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05039"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05040"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05041 till 05045 is in use
        "05046"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05047"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05048"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05049"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05050"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05051 till 05056 is in use
        "05057"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05058"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05058"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05060 is Bodenburg
        "05061"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05062 till 05069 is in use
        "05070"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05071 till 05074 is in use
        "05075"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05076"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05077"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05078"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05079"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05080"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05081"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05082 till 05086 is in use
        "05087"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05088"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05089"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0509"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05100"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05101 till 05103 is in use
        "05104"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05105 is Barsinghausen
        "05106"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05107"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05108 till 05109 is in use
        // 0511 is Hannover
        "05120"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05121 is Hildesheim
        "05122"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05123 is Schellerten
        "05124"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05125"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05126 till 05129 is in use
        // 05130 till 05132 is in use
        "05133"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05134"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05135 till 05139 is in use
        "05140"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05141 till 05149 is in use
        "05150"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05151 till 05159 is in use
        "05160"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05161 till 05168 is in use
        "05169"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05170"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05171 till 05177 is in use
        "05178"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05179"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05180"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05181 till 05187 is in use
        "05188"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05189"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0519x is in use
        "05200"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05201 till 05209 is in use
        // 0521 is Bielefeld
        "05220"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05221 till 05226 is in use
        "05227"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05228 is Vlotho-Exter
        "05229"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05230"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05231 till 05238 is in use
        "05239"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05240"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05241 till 0522 is in use
        "05243"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05244 till 05248 is in use
        "05249"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05250 till 05255 is in use
        "05256"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05257 till 05259 is in use
        "05260"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05261 till 05266 is in use
        "05267"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05268"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05269"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05270"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05271 till 05278 is in use
        "05279"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05280"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05281 till 05286 is in use
        "05287"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05288"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05289"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05290"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05291"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05292 till 05295 is in use
        "05296"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05297"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05298"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05299"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0530x is in use
        // 0531 is Braunschweig
        // total 0532x is in use
        "05330"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05331 till 05337 is in use
        "05338"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05339 is Gielde
        "05340"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05341 is Salzgitter
        "05342"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05343"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05344 till 05347 is in use
        "05348"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05349"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05350"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05351 till 05358 is in use
        "05359"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05360"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05361 till 05368 is in use
        "05369"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05370"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05371 till 05379 is in use
        "05380"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05381 till 05384 is in use
        "05385"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05386"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05387"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05388"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05389"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0539"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05400"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05401 till 05407 is in use
        "05408"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05409 is Hilter am Teutoburger Wald
        // 0541 Osnabrück
        "05420"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05421 till 05429 is in use
        "05430"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05431 till 05439 is in use
        "05440"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05441 till 05448 is in use
        "05449"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05450"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05451 till 05459 is in use
        "05460"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05461 till 05462 is in use
        "05463"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05464 till 05468 is in use
        "05469"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05470"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05471 till 05476 is in use
        "05477"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05478"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05479"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05480"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05481 till 05485 is in use
        "05486"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05487"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05488"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05489"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05490"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05491 till 05495 is in use
        "05496"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05497"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05498"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05499"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05500"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05501"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05502 till 05509 is in use
        // 0551 is Göttingen
        // 05520 till 05525 is in use
        "05526"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05527 till 05529 is in use
        "05530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05531 till 05536 is in use
        "05537"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05538"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05539"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05541 till 05546 is in use
        "05547"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05548"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05549"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05550"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05551 till 05556 is in use
        "05557"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05558"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05559"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05560"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05561 till 05565 is in use
        "05566"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05567"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05568"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05569"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05570"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05571 till 05574 is in use
        "05575"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05576"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05577"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05578"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05579"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05580"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05581"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05582 till 05586 is in use
        "05587"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05588"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05589"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05590"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05591"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05592 till 05594 is in use
        "05595"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05596"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05597"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05598"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05599"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05600"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05601 till 05609 is in use
        // 0561 is Kassel
        "05620"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05621 till 05626 is in use
        "05627"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05628"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05629"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05630"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05631 till 05636 is in use
        "05637"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05638"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05639"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05640"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05641 till 05648 is in use
        "05649"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0565x is in use
        "05660"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05661 till 05665 is in use
        "05666"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05667"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05668"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05669"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05670"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05671 till 05677 is in use
        "05678"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05679"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05681 till 05686
        "05687"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05690"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05691 till 05696 is in use
        "05697"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05698"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05699"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05701"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05702 till 05707 is in use
        "05708"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05709"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0571 is Minden Westfalen
        "05720"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05721 till 05726 is in use
        "05727"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05728"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05729"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05731 till 05734 is in use
        "05735"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05736"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05737"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05738"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05739"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05740"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05741 till 05746 is in use
        "05747"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05748"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05749"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05750"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05751 till 05755 is in use
        "05756"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05757"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05758"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05759"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05760"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05761 is Stolzenau
        "05762"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05763 till 05769 is in use
        "05770"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05771 till 05777 is in use
        "05778"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05779"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0578"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0579"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05800"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05801"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05802 till 05808 is in use
        "05809"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0581 is Uelzen
        // total 0582x is in use
        "05830"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05831 till 05839 is in use
        // 05840 till 05846 is in use
        "05847"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05848 till 05849 is in use
        // 05850 till 05855 is in use
        "05856"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05857 till 05859 is in use
        "05860"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05861 till 05865 is in use
        "05866"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05867"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05868"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05869"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05870"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05871"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 5872 till 5875 is in use
        "05876"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05877"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05878"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05879"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05880"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05881"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05882 till 05883 is in use
        "05884"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05885"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05886"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05887"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05888"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05889"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0589"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05900"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05901 till 05909 is in use
        // 0591 is Lingen (ems)
        "05920"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05921 till 05926 is in use
        "05927"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05928"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05929"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05930"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05931 till 05937 is in use
        "05938"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05939 is Sustrum
        "05940"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05941 till 05948 is in use
        "05949"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05950"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05951 till 05957 is in use
        "05958"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05959"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05960"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05961 till 05966 is in use
        "05967"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05968"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05969"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05970"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05971 is Rheine
        "05972"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05973 is Neuenkirchen Kreis Steinfurt
        "05974"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05975 till 05978 is in use
        "05979"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0598"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0599"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06000"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06001"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06002 till 06004 is in use
        "06005"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06006"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06007 till 06008 is in use
        "06009"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0601"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06020 till 06024 is in use
        "06025"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06026 till 06029 is in use
        "06030"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06031 till 06036 is in use
        "06037"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06038"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06039 is Karben
        "06040"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06041 till 06049 is in use
        // total 0605x is in use
        "06060"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06061 till 06063 is in use
        "06064"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06065"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06066 is Michelstadt-Vielbrunn
        "06067"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06068 is Beerfelden
        "06070"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06071 is Dieburg
        "06072"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06073 till 06074 is in use
        "06075"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06076"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06077"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06078 is Gross-Umstadt
        "06079"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06080"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06081 till 06087 is in use
        "06088"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06089"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06090"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06091"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06092 till 06096 is in use
        "06097"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06098"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06099"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06100"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06101 till 06109 is in use
        // 0611 is Wiesbaden
        // 06120 is Aarbergen
        "06121"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06122 till 06124 is in use
        "06125"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06126 till 06129 is in use
        // 06130 till 06136 is in use
        "06137"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06138 till 06139 is in use
        "06140"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06141"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06142 is Rüsselsheim
        "06143"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06144 till 06147 is in use
        "06148"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06149"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06150 till 06152 is in use
        "06153"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06154 till 06155 is in use
        "06156"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06157 till 06159 is in use
        "06160"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06161 till 06167 is in use
        "06168"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06169"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06170"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06171 till 06175 is in use
        "06176"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06177"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06178"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06179"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06180"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06181 till 06188 is in use
        "06189"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06190 is Hattersheim am Main
        "06191"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06192 is Hofheim am Taunus
        "06193"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06194"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06195 till 06196 is in use
        "06197"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06198 is Eppstein
        "06199"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06200"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06201 till 06207 is in use
        "06208"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06209 is Mörlenbach
        // 0621 is Mannheim
        // 06220 till 06224 is in use
        "06225"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06226 till 06229 is in use
        "06230"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06231 till 06239 is in use
        "06240"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06241 till 06247 is in use
        "06248"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06249 is Guntersblum
        "06250"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06251 till 06258 is in use
        "06259"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06260"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06261 till 06269 is in use
        "06270"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06271 till 06272 is in use
        "06273"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06274 till 06276 is in use
        "06277"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06278"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06279"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06280"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06281 till 06287 is in use
        "06288"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06289"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06290"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06291 till 06298 is in use
        "06299"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06300"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06301 till 06308 is in use
        "06309"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0631 is Kauserslautern
        "06320"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06321 till 06329 is in use
        "06330"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06331 till 06339 is in use
        // total 0634x is in use
        "06350"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06351 till 06353 is in use
        "06354"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06355 till 06359 is in use
        "06360"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06361 till 06364 is in use
        "06365"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06366"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06367"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06368"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06369"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06370"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06371 till 06375 is in use
        "06376"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06377"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06378"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06379"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06380"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06381 till 06837 is in use
        "06388"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06389"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06390"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06391 till 06398 is in use
        "06399"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0640x till 0642x is in use
        // 06431 till 06436 is in use
        "06437"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06438 till 06439 is in use
        // total 0644x is in use
        "06450"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06451 till 06458 is in use
        "06459"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06460"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06461 till 06462 is in use
        "06463"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06464 till 06468 is in use
        "06469"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06470"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06471 till 06479 is in use
        "06480"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06481"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06482 till 06486 is in use
        "06487"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06488"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06489"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0649"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0650x till 0651 is in use
        "06520"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06521"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06522 till 06527 is in use
        "06528"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06529"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06531 till 06536 is in use
        "06537"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06538"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06539"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06541 till 06545 is in use
        "06546"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06547"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06548"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06549"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0655x is in use
        "06560"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06561 till 06569 is in use
        "06570"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06571 till 06575 is in use
        "06576"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06577"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06578 is Salmtal
        "06579"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0658x is in use
        "06590"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06591 till 06597 is in use
        "06598"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06599 is Wiedenbach bei Gerolstein
        "0660"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0661 till 0662x is in use
        // 06630 till 06631 is in use
        "06632"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06633 till 06639 is in use
        "06640"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06641 till 06648 is in use
        "06649"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0665x is in use
        // 06660 till 06661 is in use
        "06662"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06663 till 06669 is in use
        // 06670 is Ludwigsau Hessen
        "06671"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06672 till 06678 is in use
        "06679"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06681 till 06684 is in use
        "06685"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06686"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06687"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06690"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06691 till 06698 is in use
        "06699"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06701 is Sprendlingen Rheinhessen
        "06702"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06703 till 06704 is in use
        "06705"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06706 till 06709 is in use
        // 0671 is Bad Kreuznach
        "06720"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06721 till 06728 is in use
        "06729"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06731 till 06737 is in use
        "06738"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06739"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06740"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06741 till 06747 is in use
        "06748"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06749"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06750"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06751 till 06758 is in use
        "06759"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06760"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06761 till 06766 is in use
        "06767"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06768"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06769"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06770"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06771 till 06776 is in use
        "06777"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06778"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06779"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06780"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06781 to 06789 is in use
        "0679"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06800"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06801"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06802 till 06806 is in use
        "06807"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06808"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06809 is Grossrosseln
        // 0681 is Saarbrücken
        "06820"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06821 is Neunkirchen Saar
        "06822"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06823"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06824 till 06827 is in use
        "06828"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06829"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06830"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06831 till 06838 is in use
        "06839"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06840"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06841 till 06844 is in use
        "06845"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06846"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06847"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06848 till 06849 is in use
        "06850"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06851 till 06858 is in use
        "06859"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06860"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06861 is Merzig
        "06862"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06863"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06864 till 06869 is in use
        "06870"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06871 till 06876 is in use
        "06877"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06878"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06879"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06880"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06881 is Lebach
        "06882"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06883"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06884"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06885"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06886"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06887 rill 06888 is in use
        "06889"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06890"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06891"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06892"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06893 till 06894 is in use
        "06895"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06896"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06897 till 06898 is in use
        "06899"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 069 is Frankfurt am Mai
        // 0700 is special number code see: TODO will be coded - see above
        "0701"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07020"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 7021 till 7026 is in use
        "07027"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07028"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07029"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07030"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07031 till 07034 is in use
        "07035"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07036"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07037"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07038"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07039"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07040"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07041 till 07046 is in use
        "07047"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07048"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07049"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07050"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07051 till 07056 is in use
        "07057"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07058"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07059"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07060"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07061"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07062 till 07063 is in use
        "07064"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07065"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07066 is Bad Rappenau-Bonfeld
        "07067"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07068"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07069"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07070"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07071 till 07073 is in use
        "07074"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07075"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07076"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07077"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07078"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07079"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07080"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07081 till 07085 is in use
        "07086"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07087"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07088"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07089"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0709"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0710"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0711 is Stuttgart
        "07120"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07121 till 07129 is in use
        // 07130 till 07136 is in use
        "07137"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07138 till 07139 is in use
        "07140"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07141 till 07148 is in use
        "07149"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07150"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07150 till 07154 is in use
        "07155"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07156 till 07159 is in use
        "07160"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07161 till 07166 is in use
        "07167"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07168"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07169"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07170"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07171 till 07176 is in use
        "07177"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07178"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07179"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07180"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07181 till 07184 is in use
        "07185"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07186"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07187"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07188"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07189"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07190"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07191 till 07195
        "07196"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07197"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07198"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07199"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07200"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07201"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07202 till 07204 is in use
        "07205"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07206"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07207"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07208"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07209"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0721 is Karlsbad
        // total 0722x is in use
        "07230"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07231 till 07237 is in use
        "07238"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07239"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07240 is Pfinztal
        "07241"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07242 till 07249 is in use
        // 0725x till 0726x is in use
        "07270"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07271 till 07277 is in use
        "07278"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07279"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0728"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0729"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07300 is Roggenburg
        "07301"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0732 till 0739 is in use
        // 0731 is Ulm Donau
        "07320"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07321 till 07329 is in use
        "07330"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07331 till 07337 is in use
        "07338"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07339"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07340 is Neenstetten
        "07341"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07342"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07343 till 07348 is in use
        "07349"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07350"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07351 till 07358 is in use
        "07359"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07360"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07361 till 07367 is in use
        "07368"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07369"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07370"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07371 is Riedlingen Württemberg
        "07372"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07373 till 07376 is in use
        "07377"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07378"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07379"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07380"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07381 till 07389 is in use
        "07390"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07391 till 07395 is in use
        "07396"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07397"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07398"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07399"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07400"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07401"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07402 till 07404 is in use
        "07405"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07406"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07407"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07408"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07409"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0741 is Deisslingen
        // 07420 is Schramberg
        // 07421 till 07429 is in use
        "07430"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07431 till 07436 is in use
        "07437"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07438"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07439"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0744x is in use
        "07450"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07451 till 07459 is in use
        "07460"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07461 till 07467 is in use
        "07468"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07469"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07470"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07471 till 07478 is in use
        "07479"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07480"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07481"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07482 till 07486 is in use
        "07487"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07488"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07489"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0749"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07500"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07501"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07502 till 07506 is in use
        "07507"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07508"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07509"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0751 Ravensburg
        // 07520 is Bodnegg
        "07521"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07522 is Wangen im Allgäu
        "07523"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07524 till 07525 is in use
        "07526"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07527 till 07529 is in use
        "07530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07531 till 07534 is in use
        "07535"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07536"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07537"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07538"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07539"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07541 till 07546 is in use
        "07547"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07548"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07549"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07550"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07551 till 07558 is in use
        "07559"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07560"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07561 till 07569 is in use
        // total 0757x is in use
        "07580"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07581 till 07587 is in use
        "07588"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07589"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0759"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07600"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07601"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07602 is Oberried Breisgau
        "07603"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07604"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07605"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07606"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07607"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07608"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07609"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0761 Freiburg im Breisgau
        // total 0762x is in use
        "07630"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07631 till 07636 is in use
        "07637"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07638"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07639"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07640"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07641 till 07646
        "07647"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07648"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07649"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07650"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07651 till 07657 is in use
        "07658"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07659"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0766x is in use
        "07670"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07671 till 07676 is in use
        "07677"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07678"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07679"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 076781 till 07685 is in use
        "07686"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07687"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0769"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07701"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07702 till 07709 is in use
        // 0771 is Donaueschingen
        // total 0772x is in use
        "07730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07731 till 07736 is in use
        "07737"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07738 till 07339 is in use
        "07740"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07741 till 07748 is in use
        "07749"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07750"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07751 is Waldshut
        "07752"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07753 till 07755 is in use
        "07756"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07757"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07758"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07759"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07770"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07771 is Stockach
        "07772"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07773 till 07775 is in use
        "07776"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07777 is Sauldorf
        "07778"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07779"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0778"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0779"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07800"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07801"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07802 till 07808 is in use
        "07809"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0781 is Offenburg
        "07820"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07821 till 07826 is in use
        "07827"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07828"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07829"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07830"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07831 till 07839 is in use
        "07840"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07841 till 07844 is in use
        "07845"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07846"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07847"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07848"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07849"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07850"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07851 till 07854 is in use
        "07855"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07856"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07857"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07858"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07859"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0786"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0787"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0788"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0789"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07900"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07901"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07902"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07903 till 07907 is in use
        "07908"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07909"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0791 is Schwäbisch Hall
        "0792"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0793x till 0794x is in use
        // 07950 till 07955 is in use
        "07956"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07957 till 07959 is in use
        "07960"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07961 till 07967 is in use
        "07968"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07969"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07970"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 07971 till 07977 is in use
        "07978"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "07979"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0798"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0799"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0800 is special number code see: TODO will be coded - see above
        "0801"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0802x is in use
        "08030"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08031 till 08036 is in use
        "08037"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08038 till 08039 is in use
        "08040"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08041 till 08043 is in use
        "08044"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08045 till 08046 is in use
        "08047"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08048"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08049"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08050"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08051 till 08057 is in use
        "08058"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08059"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08060"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08061 till 08067 is in use
        "08068"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08069"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08070"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08071 till 08076 is in use
        "08077"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08078"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08079"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08080"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08081 till 08086 is in use
        "08087"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08088"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08089"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08090"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08091 till 08095 is in use
        "08096"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08097"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08098"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08099"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08100"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08101"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08102 is Höhenkirchen-Siegertsbrunn
        "08103"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08104 till 08106 is in use
        "08107"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08108"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08109"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0811 is Halbergmoos
        "08120"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08121 till 08124 is in use
        "08125"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08126"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08127"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08128"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08129"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08130"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08131 is Dachau
        "08132"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08133 till 08139 is in use
        "08140"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08141 till 08146 is in use
        "08147"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08148"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08149"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08150"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08151 till 08153 is in use
        "08154"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08155"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08156"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08157 till 08158 is in use
        "08159"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08160"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08161 is Freising
        "08162"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08163"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08164"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08165 till 08168 is in use
        "08169"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08170 till 08171 is in use
        "08172"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08173"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08174"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08175"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08176 till 08179 is in use
        "0818"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08190"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08191 till 08196 is in use
        "08197"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08198"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08199"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08200"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08201"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08202 till 08208 is in use
        "08209"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0821 is Augsburg
        "08220"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08221 till 08226 is in use
        "08227"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08228"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08229"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08230 till 08234 is in use
        "08235"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08236 till 08239 is in use
        "08240"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08241 is Buchloe
        "08242"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08243 is Fuchstal
        "08244"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08245 till 08249 is in use
        // 08250 till 08254 is in use
        "08255"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08256"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08257 till 08259 is in use
        "08260"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08261 till 08263 is in use
        "08264"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08265 till 08269 is in use
        "08270"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08271 till 08274 is in use
        "08275"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08276 is Baar Schwaben
        "08277"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08278"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08279"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08280"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08281 till 08285 is in use
        "08286"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08287"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08288"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08289"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08290"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08291 till 08296 is in use
        "08297"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08298"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08299"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08300"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08301"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08302 till 08304 is in use
        "08305"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08306 is Ronsberg
        "08307"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08308"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08309"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0831 is Kempten Allgäu
        // 08320 till 08328 is in use
        "08329"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08330 till 08338 is in use
        "08339"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0834x is in use
        "0835"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08360"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08361 till 08369 is in use
        // 08370 is Obergünzburg
        "08371"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08372 till 08379 is in use
        // total 0838x is in use
        "08390"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08391"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08392 till 08395 is in use
        "08396"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08397"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08398"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08399"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08400"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08401"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08402 till 08407 is in use
        "08408"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08409"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0841 is Ingolstadt Donau
        "08420"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08421 till 08424 is in use
        "08425"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08426 till 08427 is in use
        "08428"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08429"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08430"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08431 till 08435 is in use
        "08436"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08437"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08438"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08439"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08440"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08441 till 08446 is in use
        "08447"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08448"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08449"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08450 is Ingoldstadt-Zuchering
        "08451"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08452 till 08454 is in use
        "08455"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08456 till 08459 is in use
        // total 0846x is in use
        "0847"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0848"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0849"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08500"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08501 till 08507 is in use
        "08508"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08509 is Ruderting
        // 0851 is Passau
        "0852"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08531 till 08538 is in use
        "08539"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08541 till 08549 is in use
        // 08550 till 08558 is in use
        "08559"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08560"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08561 till 08565 is in use
        "08566"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08567"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08568"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08569"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08570"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08571 till 08574 is in use
        "08575"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08576"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08577"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08578"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08579"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08580"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08581 till 08586 is in use
        "08587"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08588"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08589"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08590"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08591 till 08593 is in use
        "08594"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08595"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08596"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08597"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08598"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08599"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0860"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0861 is Traunstein
        "08620"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08621 till 08624 is in use
        "08625"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08626"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08627"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08628 till 08629 is in use
        // 08630 till 08631 is in use
        "08632"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08633 till 08639 is in use
        // 08640 till 08642 is in use
        "08643"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08644"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08645"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08646"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08647"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08648"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08649 is Schleching
        // 08650 till 08652 is in use
        "08653"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08654 Freilassing
        "08655"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08656 till 08657 is in use
        "08658"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08659"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08660"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08661 till 08667 is in use
        "08668"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08669 is Traunreut
        // 08670 till 08671 is in use
        "08672"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08673"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08674"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08675"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08676"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08677 till 086779 is in use
        "08680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08681 till 08687 is in use
        "08688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0869"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08701"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08702 till 08709 is in use
        // 0871 is Landshut
        "08720"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08721 till 08728 is in use
        "08729"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08731 till 08735 is in use
        "08736"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08737"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08738"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08739"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08740"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08741 till 08745 is in use
        "08746"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08747"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08748"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08749"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08750"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08751 till 08754 is in use
        "08755"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08756 is Nandlstadt
        "08757"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08758"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08759"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08760"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08761 till 08762 is in use
        "08763"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08764 till 08766 is in use
        "08767"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08768"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08769"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08770"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08771 till 08774 is in use
        "08775"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08776"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08777"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08778"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08779"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08780"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08781 till 08785 is in use
        "08786"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08787"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08788"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08789"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0879"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08800"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08801 till 08803 is in use
        "08804"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08805 till 08809 is in use
        // 0881 is Weilheim in Oberbayern
        "08820"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08821 till 08826 is in use
        "08827"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08828"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08829"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0883"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08840"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08841 is Murnau am Staffelsee
        "08842"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08843"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08844"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08845 till 08847 is in use
        "08848"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08849"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08850"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08851 is Kochel am See
        "08852"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08853"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08854"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08855"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08856 till 08858 is in use
        "08859"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08860 till 08862 is in use
        "08863"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08864"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08865"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "08866"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08867 till 08869 is in use
        "0887"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0888"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0889"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 089 is München
        "09000"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09001 Information Service TODO:see above
        "09002"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09003 Entertainment Service TODO:see above
        "09004"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09005 other premium services TODO: see above
        "09006"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09007"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09008"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09009"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0901"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0902"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0903"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0904"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0905"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0906 is Donauwörth
        // 09070 till 09078 is in use
        "09079"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0908x is in use
        // 09090 till 0904 is in use
        "09095"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09096"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09097 is Marxheim
        "09098"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09099 is Kaisheim
        "09100"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09101 till 09107 is in use
        "09108"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09109"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0911 is Nürnberg
        // 09120 is Leinburg
        "09121"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09122 till 09123 is in use
        "09124"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09125"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09126 till 09129 is in use
        "09130"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09131 till 09135 is in use
        "09136"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09137"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09138"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09139"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09140"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09141 till 09149 is in use
        "09150"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09151 till 09158 is in use
        "09159"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09160"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09161 till 09167 is in use
        "09168"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09169"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0917x till 0919x is in use
        "09200"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09201 till 09209 is in use
        // 0921 is Bayreuth
        // 09220 till 09223 is in use
        "09224"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09225 is Stadtsteinach
        "09226"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09227 till 09229 is in use
        "09230"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09231 till 09236 is in use
        "09237"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09238 is Röslau
        "09239"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09240"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09241 till 09246 is in use
        "09247"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09248"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09249"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09250"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09251 till 09257 is in use
        "09258"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09259"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0926x till 0928x is in use
        "09290"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09291"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09292 till 09295 is in use
        "09296"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09297"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09298"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09300"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09301"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09302 till 09303 is in use
        "09304"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09305 till 09307 is in use
        "09308"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09309"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0931 is Würzburg
        "09320"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09321 is Kitzingen
        "09322"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09323 till 09326 is in use
        "09327"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09328"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09329"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09330"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09331 till 09339 is in use
        // 0934x till 0935x is in use
        // 09360 is Thüngen
        "09361"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09362"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09363 till 09367 is in use
        "09368"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09369 is Uettingen
        "09370"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09371 till 09378 is in use
        "09379"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09380"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09381 till 09386 is in use
        "09387"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09388"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09389"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09390"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09391 till 09398 is in use
        "09399"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09400"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09401 till 09409 is in use
        // 0941 is Regensburg
        // 09420 till 09424 is in use
        "09425"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09426 till 09429 is in use
        "09430"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09431 is Schwandorf
        "09432"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09433 till 09436 is in use
        "09437"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09438 till 09439 is in use
        "09440"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09441 till 09448 is in use
        "09449"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09450"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09451 till 09454 is in use
        "09455"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09456"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09457"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09458"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09459"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09460"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09461 till 09649 is in use
        "09470"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09471 till 09474 is in use
        "09475"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09476"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09477"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09478"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09479"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09480 till 09482 is in use
        "09483"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09484 is Brennberg
        "09485"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09486"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09487"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09488"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09489"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09490"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09491 till 09493 is in use
        "09494"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09495 is Breitenbrunn Oberfalz
        "09496"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09497 till 09499 is in use
        "09500"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09501"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09502 till 09505 is in use
        "09506"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09507"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09508"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09509"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0951 is Bamberg
        "09520"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09521 till 09529 is in use
        "09530"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09531 till 09536 is in use
        "09537"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09538"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09539"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09540"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09541"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09542 till 09549 is in use
        "09550"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09551 till 09556 is in use
        "09557"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09558"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09559"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0956x is in use
        "09570"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09571 till 09576 is in use
        "09577"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09578"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09579"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0958"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0959"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09600"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09601"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09602 till 09608 is in use
        "09609"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0961 is Weiden in der Oberfalz
        "09620"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09621 till 09622 is in use
        "09623"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09624 till 09628 is in use
        "09629"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09630"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09631 till 09639 is in use
        "09640"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09641 till 09648 is in use
        "09649"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09650"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09651 till 09659 is in use
        "09660"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09661 till 09666 is in use
        "09667"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09668"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09669"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09670"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09671 till 09677 is in use
        "09678"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09679"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09680"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09681 till 09683 is in use
        "09684"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09685"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09686"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09687"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09688"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09689"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0969"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09700"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09701 is Sandberg Unterfranken
        "09702"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09703"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09704 is Euerdorf
        "09705"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09706"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09707"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09708 is Bad Bocklet
        // total 0972x is in use
        "09730"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09731"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09732 till 09738 is in use
        "09739"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09740"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09741 till 09742 is in use
        "09743"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09744 till 09749 is in use
        "0975"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09760"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09761 till 09766 is in use
        "09767"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09768"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09769"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09770"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09771 till 09779 is in use
        "0978"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0979"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09800"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09801"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09802 till 09805
        "09806"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09807"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09808"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09809"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0981 is Ansbach
        // 09820 is Lehrberg
        "09821"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09822 till 09829 is in use
        "09830"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09831 till 09837 s in use
        "09838"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09839"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09840"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09841 till 09848 is in use
        "09849"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09850"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09851 till 09857 is in use
        "09858"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09859"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09860"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09861 is Rothenburg ob der Tauber
        "09862"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09863"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09864"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09865 is Adelshofen Mittelfranken
        "09866"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09867 till 09869 is in use
        "09870"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09871 till 09876 is in use
        "09877"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09878"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09879"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0988"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0989"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09900"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09901 is Hengersberg Bayern
        "09902"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09903 till 09908 is in use
        "09909"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0991 is Deggendorf
        // total 0992x is in use
        "09930"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09931 till 09933 is in use
        "09934"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09935 till 09938 is in use
        "09939"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09940"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09941 till 09948 is in use
        "09949"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09950"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09951 till 09956 is in use
        "09957"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09958"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09959"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09960"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09961 till 09966 is in use
        "09967"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09968"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09969"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "09970"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 09971 till 09978 is in use
        "09979"                     | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0998"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0999"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
    }

    def "check if original lib fixes Romania special service 7 marking too long"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when:
        "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then:
        "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                    | regionCode | expectedResult                                  | expectingFail
        // Romania numbers must not have 1 has first digit of NAC
        // those indicate a special service, but there is no special service starting with 7
        // so normally the whole number must be invalid, but it is marked as TOO_LONG - an error not intended to check here
        "0040(0176) 3 0 6 9 6541" | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH | true
        "0040 176 3 0 6 9 6542"   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH | true
        "004017630696543"         | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH | true
        "0040-0176 3 0 6 9 6544"  | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH | true
    }

    def "check if original lib fixed non check of NAC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                    | regionCode  | expectedResult                                            | expectingFail
        "0176 3 0 6 9 6544"       | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0203556677"              | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "203556677"               | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "55"                      | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | false
        "556"                     | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | false
        "5566"                    | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
        "55667"                   | "DE"        | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | true
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