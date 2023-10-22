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


    def "check if original lib fixed isPossibleNumberWithReason for Emergency short codes in combination as NDC"(String number, regionCode, expectedResult, expectingFail) {
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
        "0110"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Check if this is correct
        "0110 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
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
        // short code for emergency (112) is not dial-able internationally nor does it has additional numbers
        "112"                       | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY   | false
        "0112"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // TODO: Check if this is correct
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
        "+49203115"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false  // TODO: Need to be checked what is correct here
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
        // 116xyz is nationally and internationally reachable - special check 116116 as initial number and 116999 as max legal number
        "116116"                    | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "116999"                    | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0116"                      | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0116116"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not valid by BnetzA definition just using NAC
        "0116999"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true  // not valid by BnetzA definition just using NAC
        "+49116"                    | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49116116"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116999"                 | "DE"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116"                    | "FR"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "+49116116"                 | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "+49116999"                 | "FR"       | PhoneNumberUtil.ValidationResult.IS_POSSIBLE              | false
        "0116 5566"                 | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0116 55"                   | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "116 5566"                  | "DE"       | PhoneNumberUtil.ValidationResult.TOO_LONG                 | true
        "116 55"                    | "DE"       | PhoneNumberUtil.ValidationResult.TOO_SHORT                | true
        // end of 116
    }

    def "check if original lib fixed isPossibleNumberWithReason for invalid German NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isPossibleNumberWithReason: $number"

        def result = phoneUtil.isPossibleNumberWithReason(phoneNumber)

        then: "is number expected: $expectedResult"
        this.logResult(result, expectedResult, expectingFail, number, regionCode)

        where:

        number                      | regionCode  | expectedResult                                            | expectingFail
        // short numbers which are reached internationally are also registered as NDC
        // TODO: 010 is operator selection see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/010/010xy_node.html ... will be canceled 31.12.2024
        "010 556677"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0110 is checked in Emergency short codes see above
        // ---
        "0111 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0112 is checked in Emergency short codes see above
        // ---
        "0113 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0114 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0115 is checked in German Government short codes see above
        // ---
        // ---
        // 0116 is checked in EU social short codes see above
        // ---
        "0117 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // TODO: 118 is cal assistance service see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/118xy/start.html
        "0118 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true

        "0119 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "012 556677"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0120 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0121 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0122 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0123 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0124 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0125 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0126 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0127 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0128 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0129 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0130 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0131 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0132 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0133 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0134 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0135 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0136 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // ---
        // 0137 is checked in Mass Traffic see above
        // ---
        "0138 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0139 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "014 556677"                | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0140 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0141 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0142 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0143 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0144 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0145 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0146 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0147 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0148 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0149 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // TODO: 015, 016, 017 are mobile see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/start.html
        // TODO: 016x is "Funkruf": see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Funkruf/start.html
        // TODO: 018 is VPN see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/018/018_Node.html
        // TODO: 0180 is Services: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0180/start.html
        // TODO: 0181 is international VPN see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0181/181_node.html
        // TODO: 019xyz Online Services see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/019xyz/019xyz_node.html
        // TODO: 019x is traffic management see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/Verkehrslenkungsnummern/start.html

        // TODO: 0700 - personal: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0700/0700_node.html
        // TODO: 0800 - free call: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0800/0800_node.html
        // TODO: 0900 - premium: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/0900/start.html
        // TODO: 09009 - Dialer: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/09009/9009_node.html
        // TODO: 031 - Testnumbers: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/031/031_node.html


        // TODO: DRAMA numbers: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mittlg148_2021.pdf?__blob=publicationFile&v=1

        // invalid area code for germany - using Invalid_Lenth, because its neither to long or short, but just NDC is not valid.
        "0200 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0201 is Essen
        // 0202 is Wuppertal
        // 0203 is Duisburg
        "02040 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02041 is Bottrop
        "02042 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02043 is Gladbeck
        "02044 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02045 is Bottrop-Kirchhellen
        "02046 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02047 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02048 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02049 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02050 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02051 till 02054 are in use
        "02055 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02056 is Heiligenhausen
        "02057 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02058 is Wülfrath
        "02059 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02060 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02061 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02062 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02063 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02064 till 02066 is in use
        "02067 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02068 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02069 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0207 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0208 & 0209 is in use
        "02100 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02101 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02102 till 02104 is in use
        "02105 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02106 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02107 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02108 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02109 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // special case 0212 for Solingen also covers 02129 for Haan Rheinl since Solingen may not use numbers starting with 9
        "02130 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02131 till 02133 is in use
        "02134 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02135 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02136 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02137 is Neuss-Norf
        "02138 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02139 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0214 is Leverkusen
        // 02150 till 02154 is in use
        "02155 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02156 till 02159 is in use
        "02160 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02161 till 02166 is in use
        "02167 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02168 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02169 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02170 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02171 is Leverkusen-Opladen
        "02172 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02173 till 02175 is in use
        "02176 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02177 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02178 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02179 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02180 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02181 till 02183 is in use
        "02184 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02185 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02186 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02187 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02188 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02189 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02190 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02191 till 02193 is in use
        "02194 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02195 till 02196 is in use
        "02197 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02198 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02199 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02200 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02201 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02202 till 02208 is in use
        "02209 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0221 is Köln
        "02220 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02221 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02222 till 02228 is in use
        "02229 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02230 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02231 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02232 till 02238 is in use
        "02239 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02240 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02241 till 02248 is in use
        "02249 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02250 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02251 till 02257 is in use
        "02258 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02259 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02260 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02261 till 02269 is in use
        "02270 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02271 till 02275 is in use
        "02276 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02277 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02278 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02279 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0228 is Bonn
        "02290 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02291 till 02297 is in use
        "02298 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02299 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02300 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02301 till 02309 is in use
        // 0231 is Dortmund
        "02320 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02321 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02322 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02323 till 02325 is in use
        "02326 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02327 is Bochum-Wattenscheid
        "02328 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02329 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02330 till 02339 is in use
        // 0234 is Bochum
        "02350 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02351 till 02355 is in use
        "02356 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02357 till 02358 is in use
        // 02360 till 02369 is in use
        "02370 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02371 till 02375 is in use
        "02376 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02377 till 02379 is in use
        "02380 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02381 till 02385 is in use
        "02386 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02387 till 02389 is in use
        "02390 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02391 till 02395 is in use
        "02396 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02397 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02398 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02399 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02400 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02401 till 02409 is in use
        // 0241 is Aachen
        "02420 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02421 till 02429 is in use
        "02430 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02431 till 02436 is in use
        "02437 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02438 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02439 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02440 till 02441 is in use
        "02442 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02443 till 02449 is in use
        "02450 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02451 till 02456 is in use
        "02457 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02458 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02459 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02460 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02461 till 02465 is in use
        "02466 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02467 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02468 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02469 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02470 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02471 till 02474 is in use
        "02475 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02476 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02477 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02478 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02479 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02480 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02481 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02482 is Hellenthal
        "02483 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02484 till 02486 is in use
        "02487 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02488 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02489 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0249 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02500 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02501 till 02502 is in use
        "02503 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02504 till 02509 is in use
        // 0251 is Münster
        // 02520 till 02529 is in use
        "02530 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02531 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02532 till 02536 is in use
        "02531 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02538 is Drensteinfurt-Rinkerode
        "02539 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02540 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02541 till 02543 is in use
        "02544 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02545 till 02548 is in use
        "02549 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02550 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02551 till 02558 is in use
        "02559 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02560 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02561 till 02568 is in use
        "02569 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02570 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02571 till 02575 is in use
        "02576 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02577 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02578 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02579 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02580 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02581 till 02588 is in use
        "02589 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02590 till 02599 is in use
        "02600 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02601 till 02608 is in use
        "02609 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0261 is Koblenz am Rhein
        // 02620 till 02628 is in use
        "02629 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02630 till 02639 is in use
        "02640 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02641 till 02647 is in use
        "02648 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02649 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02650 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02651 till 02657 is in use
        "02658 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02659 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02660 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02661 till 02664 is in use
        "02665 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02666 till 02667 is in use
        "02668 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02669 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02670 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02671 till 02678 is in use
        "02679 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02680 till 02689 is in use
        "02690 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02691 till 02697 is in use
        "02698 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02699 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0271 is Siegen
        "02720 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02721 till 02725 is in use
        "02726 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02727 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02728 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02729 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02730 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02731 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02731 till 02739 is in use
        "02740 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02741 till 02745 is in use
        "02746 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02747 is Molzhain
        "02748 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02749 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02750 till 02755 is in use
        "02756 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02757 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02758 till 02759 is in use
        "02760 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02761 till 02764 is in use
        "02765 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02766 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02767 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02768 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02769 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02770 till 02779 is in use
        "02780 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02781 till 02784 is in use
        "02785 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02786 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02787 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02788 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02789 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0279 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02790 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02791 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02792 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02793 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02794 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02795 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02796 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02797 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02798 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02799 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02800 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02801 till 02804 is in use
        "02805 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02806 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02807 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02808 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02809 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0281 is Wesel
        "02820 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02821 till 02828 is in use
        "02829 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02830 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02831 till 02839 is in use
        "02840 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02841 till 02845 is in use
        "02846 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02847 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02848 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02849 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02850 till 02853 is in use
        "02854 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02855 till 02859 is in use
        "02860 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02861 till 02867 is in use
        "02868 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02869 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02870 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02871 till 02874 is in use
        "02875 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02876 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02877 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02878 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02879 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0288 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0289 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02900 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02901 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02902 till 02905 is in use
        "02906 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02907 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02908 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02909 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0291 is Meschede
        "02920 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02921 till 02925 is in use
        "02926 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02927 till 02928 is in use
        "02929 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02930 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02931 till 02935 is in use
        "02936 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02937 till 02938 is in use
        "02939 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02940 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02941 till 02945 is in use
        "02946 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02947 till 02948 is in use
        "02949 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02950 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02951 till 02955 is in use
        "02956 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02957 till 02958 is in use
        "02959 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02960 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02961 till 02964 is in use
        "02965 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02966 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02967 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02968 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02969 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02970 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02971 till 02975 is in use
        "02976 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02977 is Schmallenberg-Bödefeld
        "02978 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02979 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02980 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02981 till 02985 is in use
        "02986 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02987 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02988 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02989 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02990 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02991 till 02994 is in use
        "02995 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02996 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02997 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02998 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02999 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 030 is Berlin
        // 0310 is National Test for length 3 -> TODO: OWN Test
        // 0311 is National Test for length 3 -> TODO: OWN Test
        "0312 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0313 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0314 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0315 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0316 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0317 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0318 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0319 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 032 is non geographical 11 till 13 length -> TODO: OWN Test
        "03300 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03301 till 03304 is in use
        "033050 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033051 till 033056 is in use
        "033057 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033058 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033059 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03306 till 03307 is in use
        // 033080 is Marienthal Kreis Oberhavel
        "033081 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033082 till 033089 is in use
        "033090 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033091 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033092 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033093 till 033094 is in use
        "033095 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033096 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033097 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033098 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033099 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0331 is Potsdam
        // 033200 till 033209 is in use
        // 03321 is Nauen Brandenburg
        // 03322 is Falkensee
        // 033230 till 033235 is in use
        "033236 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033237 till 033239 is in use
        "03324 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03325 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03326 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03327 till 03329 is in use
        "03330 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03331 till 03332 is in use
        "033330 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033331 till 033338 is in use
        "033339 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03334 till 03335 is in use
        "033360 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033361 till 033369 is in use
        // 03337 till 03338 is in use
        "033390 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033391 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033392 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033393 till 033398 is in use
        "033399 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03340 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03341 till 03342 is in use
        "033430 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033431 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033432 till 033439 is in use
        // 03344 is Bad Freienwalde
        "033450 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033451 till 033452 is in use
        "033453 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033454 is Wölsickendorf/Wollenberg
        "033455 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033456 till 033458 is in use
        "033459 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03346 is Seelow
        // 033470 is Lietzen
        "033471 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033472 till 033479 is in use
        // 0335 is Frankfurt (Oder)
        "033600 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033601 till 033609 is in use
        // 03361 till 03362 is in use
        "033630 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033631 till 033638 is in use
        "033639 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03364 is Eisenhüttenstadt
        "033650 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033651 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033652 till 033657 is in use
        "033658 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033659 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03366 is Beeskow
        "033670 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033671 till 033679 is in use
        "03368 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03369 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033700 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033701 till 033704 is in use
        "033705 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033706 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033707 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033708 is Rangsdorf
        "033709 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03371 till 03372 is in use
        "033730 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033731 till 033734 is in use
        "033735 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033736 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033737 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033738 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033739 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033740 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033741 till 033748 is in use
        "033749 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03375 is Königs Wusterhausen
        // 33760 is Münchehofe Kreis Dahme-Spreewald
        "033761 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033762 till 033769 is in use
        // 03377 till 03379 is in use
        "03380 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03381 till 03382 is in use
        // 033830 till 033839 is in use
        "033840 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033841 is Belzig
        "033842 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033843 till 033849 is in use
        // 03385 till 03386 is in use
        // 033870 is Zollchow bei Rathenow
        "033871 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033872 till 033878 is in use
        "033879 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03388 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03389 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03390 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03391 is Neuruppin
        // 033920 till 033929 is in use
        "033930 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033931 till 033933 is in use
        "033934 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033935 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033936 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033937 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033938 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033939 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03394 till 03395 is in use
        "033960 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033961 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033962 till 033969 is in use
        // 033970 till 033979 is in use
        "033980 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033981 till 033984 is in use
        "033985 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033986 is Falkenhagen Kreis Prignitz
        "033987 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "033988 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 033989 is Sadenbeck
        "03399 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0340 till 0341 is in use
        "034200 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034201 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034202 till 034208 is in use
        "034209 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03421 is Torgau
        "034220 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034221 till 034224 is in use
        "034225 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034226 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034227 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034228 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034229 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03423 is Eilenburg
        "034240 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034241 till 034244 is in use
        "034245 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034246 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034247556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034248 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034249 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03425 is Wurzen
        "034260 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034261 till 034263 is in use
        "03427 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03428 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034290 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034291 till 034293 is in use
        "03430 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03431 is Döbeln
        "034320 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034321 till 034322 is in use
        "034323 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034324 till 034325 is in use
        "034326 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034327 till 034328 is in use
        "034329 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03433 is Borna Stadt
        "034340 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034341 till 034348 is in use
        "034349 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03435 is Oschatz
        "034360 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034361 till 034364 is in use
        "034365 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034366 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034367 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034368 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034369 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03437 is Grimma
        "034380 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034381 till 034386 is in use
        "034387 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034388 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034389 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03439 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03440 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03441 is Zeitz
        "034420 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034421 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034422 till 034426 is in use
        "034427 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034428 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034429 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03443 is Weissenfels Sachsen-Anhalt
        "034440 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034441 is Hohenmölsen
        "034442 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034443 till 034446 is in use
        "034447 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034448 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034449 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03445 is Naumburg Saale
        "034460 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034461 till 034467 is in use
        "034468 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034469 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03447 till 03448 is in use
        "034490 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034491 till 034498 is in use
        "034499 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0345 is Halle Saale
        // 034600 toll 034607 is in use
        "034608 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034609 is Salzmünde
        // 03461 till 03462 is in use
        "034630 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034631 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034632 till 034633 is in use
        "034634 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034635 till 034639 is in use
        // 03464 is Sangerhausen
        "034650 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034651 till 034654 is in use
        "034655 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034656 is Wallhausen Sachsen-Anhalt
        "034657 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034658 till 034659 is in use
        // 03466 is Artern Unstrut
        "034670 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034671 till 034673 is in use
        "034674 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034675 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034676 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034677 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034678 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034679 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03468 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034690 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034691 till 034692 is in use
        "034693 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034694 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034695 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034696 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034697 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034698 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034699 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03470 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03471 is Bernburg Saale
        "034720 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034721 till 034722 is in use
        "034723 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034724 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034725 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034726 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034727 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034728 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034729 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 3473 is Aschersleben Sachsen-Anhalt
        "034740 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034741 till 034743 is in use
        "034744 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034745 till 034746 is in use
        "034747 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034748 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034749 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03475 till 03476 is in use
        "034770 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034771 till 034776 is in use
        "034777 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034778 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034779 is Abberode
        "034780 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034781 till 034783 is in use
        "034784 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034785 is Sandersleben
        "034786 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034787 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034788 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034789 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03479 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0348 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034900 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034901 is Roßlau Elbe
        "034902 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034903 till 034907
        "034908 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034909 is Aken Elbe
        // 03491 till 03494 (yes full 03492x is used, too) is in use
        "034950 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034951 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034952 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034953 till 034956
        "034957 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034958 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034959 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03496 is Köthen Anhalt
        "034970 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034971 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "034972 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034973 is Osternienburg
        "034974 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 034975 till 034979 is in use
        "03498 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03499 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03500 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03501 is Pirna
        "035029 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035030 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035031 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035032 till 035033 is in use
        "035034 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035035 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035036 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035038 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035038 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035039 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03504 is Dippoldiswalde
        "035050 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035051 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035052 till 035058
        "035059 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03506 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03507 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03508 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03509 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0351 is Dresden
        // 03520x till 03525 is in use (inclusive complete 03524x)
        "035260 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035261 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035262 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035263 till 035268
        "035269 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03527 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03529 till 03529 is in use
        "03530 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03531 is Finsterwalde
        "035320 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035321 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035322 till 035327
        "035328 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035329 is Dollenchen
        // 03533 is Elsterwerda
        "035340 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035341 till 035343
        "035344 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035345 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035346 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035347 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035348 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035349 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03535 is Herzberg Elster
        "035360 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035361 till 035365 is in use
        "035366 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035367 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035369 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035369 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03537 is Jessen Elster
        "035380 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035381 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035382 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035383 till 035389 is in use
        "03539 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03540 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03541 till 03542 is in use
        "035430 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035431 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035432 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035433 till 035436 is in use
        "035437 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035438 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035439 is Zinnitz
        // 03544 is Luckau Brandenburg
        "035450 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035451 till 035456 is in use
        "035457 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035458 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035459 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03546 is Lübben Spreewald
        "035470 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035471 till 035478 is in use
        "035479 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03548 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03549 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0355 is Cottbus
        // 03560x till 03564 is in use
        "03565 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03566 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03567 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03568 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035690 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035691 till 035698 is in use
        "035699 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03570 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03571 is Hoyerswerda
        "035720 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035721 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035722 till 035728 is in use
        "035729 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03573 till 03574 is in use
        "035750 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035751 till 035756 is in use
        "035757 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035758 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035759 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03576 is Weisswasser
        "035770 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035771 till 035775 is in use
        "035776 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035777 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035778 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035779 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03578 is Kamenz
        "035790 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035791 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035792 till 035793 is in use
        "035794 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035795 till 035797 is in use
        "035798 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035799 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03580 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03581 is Görlitz
        // 035820 is Zodel
        "035821 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035822 till 035823 is in use
        "035824 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035825 till 035829 is in use
        // 03583 is Zittau
        "035840 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035841 till 035844 is in use
        "035845 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035846 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035847 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035848 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035849 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03585 till 03586 is in use
        "035870 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035871 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035872 till 035877 is in use
        "035878 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035879 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03588 is Niesky
        "035890 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035891 till 0358595 is in use
        "035896 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035897 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035898 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035899 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03590 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03591 till 03594 (including total 03593x) is in use
        "035950 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035951 till 035955 is in use
        "035956 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035957 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035958 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035959 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03596 is Neustadt in Sachsen
        "035970 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 035971 till 035975 is in use
        "035976 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035977 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035978 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "035979 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03598 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03599 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03600 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03601 till 03603 (including total 03602x) is in use
        "036040 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036041 till 036043 is in use
        "036044 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036045 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036046 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036047 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036048 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036049 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03605 till 03606 is in use
        "036070 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036071 till 036072 is in use
        "036073 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036074 till 036077 is in use
        "036078 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036079 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036080 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036081 till 036085 is in use
        "036086 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036087 is Wüstheuterode
        "036088 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036089 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03609 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0361 is Erfurt
        // 03620x till 03624 is in use
        "036250 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036251 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036252 till 036259 is in use
        "03626 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03627 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03628 till 03629 is in use
        "03630 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03631 till 03632 is in use
        // 036330 till 036338 is in use
        "036339 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03634 till 03637x is in use
        "03638 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03639 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03640 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03641 is Jena
        "036420 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036421 till 036428 is in use
        "036429 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03643 till 03644 is in use
        // 036450 till 036454 is in use
        "036455 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036456 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036457 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036458 till 036459 is in use
        "036460 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036461 till 036465 is in use
        "036466 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036467 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036468 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036469 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03647 is Pößneck
        "036480 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036481 till 036484 is in use
        "036485 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036486 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036487 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036488 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036489 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03649 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0365 is Gera
        "036600 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036601 till 036608 is in use
        "036609 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03661 is Greiz
        "036620 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036621 till 036626 is in use
        "036627 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036628 is Zeulenroda
        "036629 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03663 is Schleiz
        // 036640 is Remptendorf
        "036641 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036642 till 036649 is in use
        "036650 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036651 till 036653 is in use
        "036654 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036655 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036656 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036657 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036658 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036659 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03666 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03667 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03668 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036690 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036691 till 036695 is in use
        "036696 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036697 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036698 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036699 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036700 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036701 till 036705 is in use
        "036706 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036707 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036708 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036709 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03671 till 03673x is in use
        "036740 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036741 till 03644 is in use
        "036745 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036746 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036747 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036748 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036749 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03675 is Heubisch
        "036760 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036761 till 036762 is in use
        "036763 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036764 is Neuhaus-Schierschnitz
        "036765 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036766 is SChalkau
        "036767 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036768 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036769 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03677 is Ilmenau Thüringen
        "036780 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036781 till 036785 is in use
        "036786 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036787 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036788 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036789 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03679 is Suhl
        "03680 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03681 till 03686 (inlcuding total 03684x) is in use
        // 036870 till 036871 is in use
        "036872 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036873 till 036875 is in use
        "036876 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "036877 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036878 is Oberland
        "036879 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03688 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03689 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03690 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036891 till 03693 (including total 036892x) is in use
        // 0368940 till 0368941 is in use
        "036942 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0368943 till 0368949 is in use
        // 03695 is Bad Salzungen
        "036960 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 036961 till 036969 is in use
        "03697 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03698 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03699 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0370 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0371 is Chemnitz Sachsen
        // 037200 is Wittgensdorf bei Chemnitz
        "037201 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037202 till 03724 is in use
        "037205 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037206 till 037209 is in use
        // 03721 till 03727 is in use
        "03728 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037290 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037291 till 037298 is in use
        "037299 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03730 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03731 till 03733 (including total 03732x) is in use
        "037340 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037341 till 037344 is in use
        "037345 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037346 till 037349 is in use
        // 03735 till 03737 (including total 03736x) is in use
        "037380 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037381 till 037384 is in use
        "037385 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037386 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037387 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037388 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037389 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03739 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03740 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03741 is Plauen
        "037420 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037421 till 037423 is in use
        "037424 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037425 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037426 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037427 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037428 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037429 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03473x till 03745 is in use
        "037460 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037461 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037462 till 037465 is in use
        "037466 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037467 till 037468 is in use
        "037469 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03747 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03748 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03749 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0375 is Zwickau
        // 03760x till 03765 is in use
        "03766 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03767 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03768 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03769 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03770 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03771 till 03774 is in use
        "037750 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037751 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037752 is Eibenstock
        "037753 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 037754 till 037757
        "037758 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "037759 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03776 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03777 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03778 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03779 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0378 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0379 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0380 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0381 is Rostock
        "038200 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038201 till 038209
        // 03821 till 03822x
        "038230 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038231 till 038234
        "038235 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038236 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038237 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038238 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038239 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03824 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03825 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03826 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03827 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03828 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038290 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038291 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038292 till 038297 is in use
        "038298 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038299 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03830x till 03831 is in use
        // 038320 till 038328 is in use
        "038329 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038330 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 08331 till 038334 is in use
        "038335 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038336 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038337 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038338 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038339 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03834 is Greifswald
        "038350 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038351 till 038356 is in use
        "038357 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038358 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038359 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03836 till 03838 (including total 03837x) is in use
        "038390 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038391 till 038393 is in use
        "038394 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038395 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038396 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038397 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038398 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038399 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03840 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03841 id Neukloster
        "038420 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038421 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038422 till 038429
        // 03843 till 03845x is in use
        "038460 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038461 till 038462 is in use
        "038463 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038464 is Bernitt
        "038465 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038466 is Jürgenshagen
        "038467 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038468 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038469 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03847 is Sternberg
        "038480 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038481 till 038486 is in use
        "038487 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038488 is Demen
        "038489 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03849 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0385 is Schwerin
        // 03860 till 03861 is in use
        "03862 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03863 is Crivitz
        "03864 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03865 till 03869 is in use
        "03870 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03871 till  03872x is in use
        "038730 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038731 till 038733 is in use
        "038734 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038735 till 038738 is in use
        "038739 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03874 till 03877 (including total 03875x) is in use
        // 038780 till 038785 is in use
        "038786 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038787 till 038789 is in use
        "038790 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038791 till 038794
        "038795 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038796 till 038797
        "038798 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038799 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03880 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03881 is Grevesmühlen
        "038820 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038821 till 038828 is in use
        "038829 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03883 is Hagenow
        "038840 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038841 till 038845 is in use
        "038846 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038847 till 038848 is in use
        "038849 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038850 till 038856 is in use
        "038857 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038858 till 038859 is in use
        // 03886 is Gadebusch
        "038870 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 038871 till 038876 is in use
        "038877 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038878 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "038879 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03888 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03889 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0389 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03900x till 03905x (including total 03903x) is in use
        "039060 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039061 till 039062 is in use
        "039063 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039064 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039065 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039066 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039067 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039068 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039069 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03907 till 03909 (including total 03908x) is in use
        // 0391 is Magdeburg
        // 03920x till 03921 is in use
        "039220 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039221 till 039226 is in use
        "039227 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039228 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039229 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03923 is Zerbst
        "039240 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039241 till 039248 is in use
        "0392498 556677"            | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 032925 is Stassfurt
        "039260 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039261 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039262 till 039268 is in use
        "039269 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03927 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03928 is Schönebeck Elbe
        "039290 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039291 till 039298 is in use
        "039299 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03930 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03931 is Stendal
        // 039320 till 039325 is in use
        "039326 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039327 till 039329 is in use
        // 03933 is Genthin
        "039340 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039341 till 039349 is in use
        // 03935 is Tangerhütte
        "039360 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039361 till 039366 is in use
        "039367 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039368 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039369 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03937 is Osterburg Altmark
        "039380 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039381 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039382 till 039384 is in use
        "039385 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039386 till 039389 is in use
        // total 03939x is in use
        // 03940x till 03941 is in use
        "039420 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039421 till 039428 is in use
        "039429 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03943 till 03944 is in use
        "039450 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039451 till 039459 is in use
        // 03946 till 03947 is in use
        "039480 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039481 till 039485 is in use
        "039486 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039487 till 039489 is in use
        // 03949 is Oschersleben Bode
        // 0395 is Zwiedorf
        // 039600 till 039608 is in use
        "039609 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03961 till 03969 is in use
        "03970 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03971 is Anklam
        "039720 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039721 till 039724 is in use
        "039725 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039726 till 039728 is in use
        "039729 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03973 till 03974x is in use
        "039750 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039751 till 039754 is in use
        "039755 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039756 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039757 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039758 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039759 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03976 is Torgelow bei Uckermünde
        "039770 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039771 till 039779 is in use
        "03980 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03981 to 03982x is in use
        "039830 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039831 till 039833 is in use
        "039834 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039835 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039836 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039837 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039838 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039839 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03984 is Prenzlau
        "039850 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039851 till 039859 is in use
        "039860 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039861 till 039863 is in use
        "039863 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039864 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039865 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039866 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039867 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039868 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039869 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03987 is Templin
        "039880 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039881 till 039889 is in use
        "03989 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "03990 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03991 is Waren Müritz
        "039920 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039921 till 039929 is in use
        "039930 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039931 till 039934 is in use
        "039935 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039936 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039937 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039938 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "039939 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03994 is Malchin
        "039950 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039951 till 039957 is in use
        "039958 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039959 is Dargun
        // 03996 is Teterow
        "039970 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039971 till 039973 is in use
        "039974 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039975 till 039978
        "039979 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 03998 is Demmin
        "039990 556677"             | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 039991 till 039999 is in use
        // 040 is Hamburg
        "04100 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04101 till 04109 is in use
        "0411 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0412x is in use
        "04130 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04131 till 04139 is in use
        // 04140 till 04144 is in use
        "04145 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04146 is Stade-Bützfleth
        "04147 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04148 till 04149 is in use
        "04150 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04151 till 04156 is in use
        "04157 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04158 till 04159 is in use
        "04160 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04161 till 04169 is in use
        "04170 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04171 till 04179 is in use
        // total 0418x is in sue
        "04190 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04191 till 04195 is in use
        "04196 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04197 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04198 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04199 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04200 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04201 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04202 till 04209 is in use
        // 0421 is Bremen
        "04220 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04221 till 04224 is in use
        "04225 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04226 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04227 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04228 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04229 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0423x till 0424x is in use
        "04250 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04251 till 04258 is in use
        "04259 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0426x is in use
        "04270 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04271 till 04277 is in use
        "04278 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04279 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04280 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04281 till 04289 is in use
        "04290 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04291 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04292 till 04298 is in use
        "04299 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04300 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04301 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04302 till 04303 is in use
        "04304 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04305 is Westensee
        "04306 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04307 till 04308 is in use
        "04309 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0431 till 0433x (including total 0432x) is in use
        // 04340 is Achterwehr
        "04341 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04342 till 04346 is in use
        "04350 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04351 till 04358 is in use
        "04359 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04360 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04361 till 04367 is in use
        "04368 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04369 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04370 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04371 till 04372 is in use
        "04373 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04374 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04375 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04376 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04377 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04378 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04379 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04380 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04381 till 04385 is in use
        "04386 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04387 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04388 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04389 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04390 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04391 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04392 till 04394 is in use
        "04395 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04396 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04397 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04398 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04399 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04400 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04401 till 04409 is in use
        // 0441 is Oldenburg (Oldb)
        "04420 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04421 till 04423 is in use
        "04424 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04425 till 04426 is in use
        "04427 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04428 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04429 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04430 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04431 till 04435 is in use
        "04436 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04437 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04438 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04439 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04440 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04441 till 04447 is in use
        "04448 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04449 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04450 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04451 till 04456 is in use
        "04457 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04458 is Wiefeldstede-Spohle
        "04459 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04460 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04461 till 04469 is in use
        "04470 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04471 till 04475 is in use
        "04476 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04477 till 04479 is in use
        // total 0448x is in use
        "04490 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04491 till 1199 is in use
        "04500 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04501 till 04506 is in use
        "04507 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04508 till 0459 is in use
        // 0451 is Lübeck
        "04520 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04521 till 04529 is in use
        "04530 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04531 till 04537 is in use
        "04538 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04539 is Westerau
        "04540 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04541 till 04547 is in use
        "04548 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04549 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0455x is in use
        "04560 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04561 till 04564 is in use
        "0457 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0458 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0459 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04600 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04601 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04602 till 04609 is in use
        // 0461 is Flensburg
        "04620 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04621 till 04627 is in use
        "04628 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04629 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0463x is in use
        "04640 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04641 till 04644 is in use
        "04645 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04646 is Morkirch
        "04647 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04648 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04649 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04650 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04651 is Sylt
        "04652 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04653 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04654 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04655 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04656 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04657 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04658 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04659 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04660 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04661 till 04668 is in use
        "04669 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04670 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04671 till 04674 is in use
        "04675 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04676 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04677 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04678 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04679 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04680 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04681 till 04684 is in use
        "04685 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04686 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04687 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04688 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04689 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04700 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04701 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04702 till 04708 is in use
        "04709 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0471 is Bremerhaven
        "04720 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04721 till 04725 is in use
        "04726 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04727 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04728 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04729 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04730 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04731 till 04737 is in use
        "04738 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04739 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0474x is in use
        "04750 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04751 till 04758 is in use
        "04759 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04760 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04761 till 04769 is in use
        // total 0477x is in use
        "0478 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04790 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04791 till 04796 is in use
        "04800 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04801 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04802 till 04806 is in use
        "04807 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04808 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04809 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0481 is Heide Holstein
        "04820 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04821 till 04829 is in use
        // 04830 is Süderhastedt
        "04831 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04832 till 04839 is in use
        "04840 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04841 till 04849 os in use
        "04850 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04851 till 04859 is in use
        "04860 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04861 till 04865 is in use
        "04866 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04867 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04868 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04869 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04870 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04871 till 04877 is in use
        "04878 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04879 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04880 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04881 till 04885 is in use
        "04886 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04887 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04888 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04889 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04890 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04891 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04892 till 04893 is in use
        "04894 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04895 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04896 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04897 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04898 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04899 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04900 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04901 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04902 till 04903 is in use
        "04904 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04905 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04906 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04907 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04908 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04909 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0491 is Leer Ostfriesland
        // total 0492x is in use
        "04930 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04931 till 04936 is in use
        "04937 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04938 till 04939 is in use
        "04940 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04941 till 04948 is in use
        "04949 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0495x is in use
        "04960 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04961 till 04968 is in use
        "04969 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04970 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 04971 till 04977 is in use
        "04978 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "04979 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0498 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0499 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0500 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0501 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05020 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05021 till 05028 is in use
        "05029 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05030 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05031 till 05037 is in use
        "05038 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05039 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05040 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05041 till 05045 is in use
        "05046 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05047 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05048 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05049 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05050 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05051 till 05056 is in use
        "05057 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05058 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05058 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05060 is Bodenburg
        "05061 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05062 till 05069 is in use
        "05070 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05071 till 05074 is in use
        "05075 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05076 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05077 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05078 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05079 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05080 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05081 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05082 till 05086 is in use
        "05087 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05088 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05089 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0509 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05100 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05101 till 05103 is in use
        "05104 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05105 is Barsinghausen
        "05106 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05107 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05108 till 05109 is in use
        // 0511 is Hannover
        "05120 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05121 is Hildesheim
        "05122 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05123 is Schellerten
        "05124 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05125 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05126 till 05129 is in use
        // 05130 till 05132 is in use
        "05133 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05134 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05135 till 05139 is in use
        "05140 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05141 till 05149 is in use
        "05150 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05151 till 05159 is in use
        "05160 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05161 till 05168 is in use
        "05169 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05170 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05171 till 05177 is in use
        "05178 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05179 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05180 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05181 till 05187 is in use
        "05188 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05189 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0519x is in use
        "05200 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05201 till 05209 is in use
        // 0521 is Bielefeld
        "05220 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05221 till 05226 is in use
        "05227 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05228 is Vlotho-Exter
        "05229 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05230 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05231 till 05238 is in use
        "05239 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05240 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05241 till 0522 is in use
        "05243 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05244 till 05248 is in use
        "05249 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05250 till 05255 is in use
        "05256 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05257 till 05259 is in use
        "05260 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05261 till 05266 is in use
        "05267 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05268 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05269 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05270 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05271 till 05278 is in use
        "05279 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05280 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05281 till 05286 is in use
        "05287 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05288 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05289 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05290 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05291 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05292 till 05295 is in use
        "05296 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05297 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05298 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05299 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0530x is in use
        // 0531 is Braunschweig
        // total 0532x is in use
        "05330 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05331 till 05337 is in use
        "05338 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05339 is Gielde
        "05340 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05341 is Salzgitter
        "05342 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05343 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05344 till 05347 is in use
        "05348 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05349 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05350 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05351 till 05358 is in use
        "05359 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05360 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05361 till 05368 is in use
        "05369 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05370 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05371 till 05379 is in use
        "05380 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05381 till 05384 is in use
        "05385 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05386 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05387 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05388 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05389 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0539 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05400 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05401 till 05407 is in use
        "05408 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05409 is Hilter am Teutoburger Wald
        // 0541 Osnabrück
        "05420 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05421 till 05429 is in use
        "05430 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05431 till 05439 is in use
        "05440 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05441 till 05448 is in use
        "05449 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05450 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05451 till 05459 is in use
        "05460 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05461 till 05462 is in use
        "05463 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05464 till 05468 is in use
        "05469 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05470 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05471 till 05476 is in use
        "05477 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05478 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05479 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05480 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05481 till 05485 is in use
        "05486 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05487 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05488 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05489 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05490 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05491 till 05495 is in use
        "05496 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05497 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05498 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05499 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05500 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05501 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05502 till 05509 is in use
        // 0551 is Göttingen
        // 05520 till 05525 is in use
        "05526 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05527 till 05529 is in use
        "05530 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05531 till 05536 is in use
        "05537 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05538 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05539 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05540 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05541 till 05546 is in use
        "05547 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05548 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05549 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05550 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05551 till 05556 is in use
        "05557 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05558 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05559 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05560 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05561 till 05565 is in use
        "05566 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05567 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05568 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05569 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05570 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05571 till 05574 is in use
        "05575 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05576 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05577 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05578 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05579 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05580 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05581 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05582 till 05586 is in use
        "05587 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05588 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05589 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05590 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05591 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05592 till 05594 is in use
        "05595 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05596 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05597 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05598 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05599 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05600 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05601 till 05609 is in use
        // 0561 is Kassel
        "05620 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05621 till 05626 is in use
        "05627 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05628 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05629 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05630 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05631 till 05636 is in use
        "05637 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05638 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05639 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05640 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05641 till 05648 is in use
        "05649 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // total 0565x is in use
        "05660 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05661 till 05665 is in use
        "05666 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05667 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05668 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05669 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05670 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05671 till 05677 is in use
        "05678 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05679 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05680 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05681 till 05686
        "05687 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05688 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05689 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05690 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05691 till 05696 is in use
        "05697 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05698 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05699 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05700 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05701 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05702 till 05707 is in use
        "05708 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05709 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05700 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0571 is Minden Westfalen
        "05720 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05721 till 05726 is in use
        "05727 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05728 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05729 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05730 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05731 till 05734 is in use
        "05735 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05736 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05737 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05738 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05739 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05740 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05741 till 05746 is in use
        "05747 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05748 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05749 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05750 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05751 till 05755 is in use
        "05756 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05757 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05758 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05759 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05760 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05761 is Stolzenau
        "05762 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05763 till 05769 is in use
        "05770 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05771 till 05777 is in use
        "05778 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05779 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0578 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0579 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05800 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05801 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05802 till 05808 is in use
        "05809 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0581 is Uelzen
        // total 0582x is in use
        "05830 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05831 till 05839 is in use
        // 05840 till 05846 is in use
        "05847 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05848 till 05849 is in use
        // 05850 till 05855 is in use
        "05856 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05857 till 05859 is in use
        "05860 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05861 till 05865 is in use
        "05866 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05867 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05868 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05869 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05870 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05871 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 5872 till 5875 is in use
        "05876 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05877 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05878 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05879 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05880 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05881 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05882 till 05883 is in use
        "05884 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05885 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05886 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05887 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05888 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05889 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0589 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05900 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05901 till 05909 is in use
        // 0591 is Lingen (ems)
        "05920 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05921 till 05926 is in use
        "05927 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05928 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05929 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05930 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05931 till 05937 is in use
        "05938 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05939 is Sustrum
        "05940 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05941 till 05948 is in use
        "05949 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05950 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05951 till 05957 is in use
        "05958 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05959 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05960 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05961 till 05966 is in use
        "05967 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05968 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05969 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "05970 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05971 is Rheine
        "05972 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05973 is Neuenkirchen Kreis Steinfurt
        "05974 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 05975 till 05978 is in use
        "05979 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0598 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0599 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06000 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06001 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06002 till 06004 is in use
        "06005 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06006 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06007 till 06008 is in use
        "06009 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0601 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06020 till 06024 is in use
        "06025 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06026 till 06029 is in use
        "06030 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06031 till 06036 is in use
        "06037 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06038 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06039 is Karben
        "06040 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06041 till 06049 is in use
        // total 0605x is in use
        "06060 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06061 till 06063 is in use
        "06064 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06065 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06066 is Michelstadt-Vielbrunn
        "06067 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06068 is Beerfelden
        "06070 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06071 is Dieburg
        "06072 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06073 till 06074 is in use
        "06075 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06076 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06077 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06078 is Gross-Umstadt
        "06079 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06080 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06081 till 06087 is in use
        "06088 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06089 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06090 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06091 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06092 till 06096 is in use
        "06097 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06098 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06099 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "06100 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 06101 till 06109 is in use
    }


    def "check if original lib fixed isValidNumber for invalid German NDC"(String number, regionCode, expectedResult, expectingFail) {
        given:

        def phoneNumber = phoneUtil.parse(number, regionCode)

        when: "get number isValidNumber: $number"

        def result = phoneUtil.isValidNumber(phoneNumber)

        then: "is number expected: $expectedResult"
        if (result != expectedResult) {
            if (expectingFail) {
                logger.info("isValidNumber is still not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
            } else {
                logger.warning("isValidNumber is suddenly not correctly validating $number to $expectedResult for region $regionCode, by giving $result")
            }
        } else {
            if (expectingFail) {
                logger.info("!!! isValidNumber is now correctly validating $number to $expectedResult for region $regionCode !!!")
            }
        }

        where:

        number                      | regionCode  | expectedResult | expectingFail
        // invalid area code for germany - using Invalid_Lenth, because its neither to long or short, but just
        "02040 556677"              | "DE"        | false           | true
        // 02041 is Bottrop
        "02042 556677"              | "DE"        | false           | true
        // 02043 is Gladbeck
        "02044 556677"              | "DE"        | false           | true
        // 02045 is Bottrop-Kirchhellen
        "02046 556677"              | "DE"        | false           | true
        /*
        "02047 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02048 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02049 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02050 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02051 till 02054 are in use
        "02055 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02056 is Heiligenhausen
        "02057 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02058 is Wülfrath
        "02059 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02060 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02061 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02062 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02063 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02064 till 02066 is in use
        "02067 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02068 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02069 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "0207 556677"               | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0208 & 0209 is in use
        "02100 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02101 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02102 till 02104 is in use
        "02105 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02106 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02107 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02108 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02109 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // special case 0212 for Solingen also covers 02129 for Haan Rheinl since Solingen may not use numbers starting with 9
        "02130 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02131 till 02133 is in use
        "02134 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02135 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02136 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02137 is Neuss-Norf
        "02138 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02139 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0214 is Leverkusen
        // 02150 till 02154 is in use
        "02155 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02156 till 02159 is in use
        "02160 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02161 till 02166 is in use
        "02167 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02168 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02169 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02170 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02171 is Leverkusen-Opladen
        "02172 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02173 till 02175 is in use
        "02176 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02177 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02178 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02179 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02180 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02181 till 02183 is in use
        "02184 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02185 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02186 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02187 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02188 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02189 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02190 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02191 till 02193 is in use
        "02194 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02195 till 02196 is in use
        "02197 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02198 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02199 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02200 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02201 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02202 till 02208 is in use
        "02209 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0221 is Köln
        "02220 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02221 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02222 till 02228 is in use
        "02229 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02230 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02231 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02232 till 02238 is in use
        "02239 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02240 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02241 till 02248 is in use
        "02249 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02250 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02251 till 02257 is in use
        "02258 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02259 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02260 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02261 till 02269 is in use
        "02270 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02271 till 02275 is in use
        "02276 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02277 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02278 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02279 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 0228 is Bonn
        "02290 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02291 till 02297 is in use
        "02298 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02299 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02300 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02301 till 02309 is in use
        // 0231 is Dortmund
        "02320 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02321 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02322 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02323 till 02325 is in use
        "02326 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02327 is Bochum-Wattenscheid
        "02328 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        "02329 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02330 till 02339 is in use
        // 0234 is Bochum
        "02350 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02351 till 02355 is in use
        "02356 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02357 till 02358 is in use
        // 02360 till 02369 is in use
        "02370 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02371 till 02375 is in use
        "02376 556677"              | "DE"       | PhoneNumberUtil.ValidationResult.INVALID_LENGTH           | true
        // 02377 till 02379 is in use
        */
    }


}