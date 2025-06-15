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
import com.google.i18n.phonenumbers.Phonenumber
import spock.lang.Specification

import java.util.logging.Logger


// Plain Number Format: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/NP_Nummernraum.pdf?__blob=publicationFile&v=6
// NDC with labels: https://www.itu.int/dms_pub/itu-t/oth/02/02/T02020000510006PDFE.pdf
// Overview of special number ranges: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/start.html

// Version 5.V.2020 of BenetzA number plan


class RFC3966_DE_NDCTest extends Specification {

    PhoneNumberUtil phoneUtil

    Logger logger = Logger.getLogger(RFC3966_DE_NDCTest.class.toString())

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
                    logger.info("RFC3966 is still not correctly extracting NDC from $number for region $regionCode, by giving class $result instead of class $expectedResult ")
                }
            } else {
                logger.warning("RFC3966 is still not correctly extracting NDC from $number for region $regionCode, by giving class $result instead of class $expectedResult ")
            }
        } else {
            if (expectingFail) {
                logger.info("RFC3966 is now correctly extracting NDC from $number for region $regionCode, by giving class $expectedResult")
            }
        }
        return true
    }


    def extractONKZ(String number, String regionCode) {
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, regionCode)
        String r = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966)
        String[] rs = r.split("-")
        String onkz = null;
        if (rs.length>1) {
            onkz = rs[1]
        }
        return onkz;
    }

    def "check if original lib fixed RFC3966 for invalid German NDC 010 - 02999"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }


        when:
        "get number RFC3966: $number"
        String[] results = []
        for (int i = 0; i < numbersToTest.length; i++) {
            String onkz = extractONKZ(numbersToTest[i], regionCode)
            String eResult = number.substring(1)
            if (onkz == null) {
                results += "0"
            } else {
                if (eResult == onkz) {
                    results += "1"
                } else {
                    results += "2"
                }

            }
        }

        then:
        "is number expected: $expectedResult"
        boolean extracted = false;
        for (int i = 0; i < results.length; i++) {

            this.logResult(results[i], expectedResult, expectingFail[i], numbersToTest[i], regionCode)
        }


        where:

        number  | regionCode | expectedResult | expectingFail
        // short numbers which are reached internationally are also registered as NDC
        // TODO: 010 is operator selection see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/010/010xy_node.html ... will be canceled 31.12.2024
        "010"   | "DE"       | "0"            | true
        // ---
        // 0110 is checked in Emergency short codes see above
        // ---
        "0111"  | "DE"       | "0"            | true
        // ---
        // 0112 is checked in Emergency short codes see above
        // ---
        "0113"  | "DE"       | "0"            | true
        "0114"  | "DE"       | "0"            | true
        // ---
        // 0115 is checked in German Government short codes see above
        // ---
        // ---
        // 0116 is checked in EU social short codes see above
        // ---
        "0117"  | "DE"       | "0"            | true
        // ---
        // 0118 is checked in German call assistant services see above
        // ---
        "0119"  | "DE"       | "0"            | true
        "012"   | "DE"       | "0"            | true
        "0120"  | "DE"       | "0"            | true
        "0121"  | "DE"       | "0"            | true
        "0122"  | "DE"       | "0"            | true
        "0123"  | "DE"       | "0"            | true
        "0124"  | "DE"       | "0"            | true
        "0125"  | "DE"       | "0"            | true
        "0126"  | "DE"       | "0"            | true
        "0127"  | "DE"       | "0"            | true
        "0128"  | "DE"       | "0"            | true
        "0129"  | "DE"       | "0"            | true
        "0130"  | "DE"       | "0"            | true
        "0131"  | "DE"       | "0"            | true
        "0132"  | "DE"       | "0"            | true
        "0133"  | "DE"       | "0"            | true
        "0134"  | "DE"       | "0"            | true
        "0135"  | "DE"       | "0"            | true
        "0136"  | "DE"       | "0"            | true
        // ---
        // 0137 is checked in Mass Traffic see above
        // ---
        "0138"  | "DE"       | "0"            | true
        "0139"  | "DE"       | "0"            | true
        "014"   | "DE"       | "0"            | true
        "0140"  | "DE"       | "0"            | true
        "0141"  | "DE"       | "0"            | true
        "0142"  | "DE"       | "0"            | true
        "0143"  | "DE"       | "0"            | true
        "0144"  | "DE"       | "0"            | true
        "0145"  | "DE"       | "0"            | true
        "0146"  | "DE"       | "0"            | true
        "0147"  | "DE"       | "0"            | true
        "0148"  | "DE"       | "0"            | true
        "0149"  | "DE"       | "0"            | true
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
        "0190"  | "DE"       | "0"            | true  // Reserve - previously premium rate numbers, which were relocated to 0900
        // ---
        // 019(1-4) is checked in German Online Services 019(1-4) inc. historic
        // ---
        "0195"  | "DE"       | "0"            | true  // Reserve
        "0196"  | "DE"       | "0"            | true  // Reserve
        "0197"  | "DE"       | "0"            | true  // Reserve
        // ---
        // Traffic management numbers are only valid between operators - so not for end customers to call
        // ---
        "01980" | "DE"       | "0"            | true  // Reserve
        // ---
        // 01981 is checked in German traffic routing 01981 of mobile Emergency calls
        // ---
        // ---
        // 01982 is checked in German traffic routing 01982 for emergency calls
        // ---
        "01983" | "DE"       | "0"            | true  // Reserve
        "01984" | "DE"       | "0"            | true  // Reserve
        "01985" | "DE"       | "0"            | true  // Reserve
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
        "0200"  | "DE"       | "0"            | true
        // 0201 is Essen
        // 0202 is Wuppertal
        // 0203 is Duisburg
        "02040" | "DE"       | "0"            | true
        // 02041 is Bottrop
        "02042" | "DE"       | "0"            | true
        // 02043 is Gladbeck
        "02044" | "DE"       | "0"            | true
        // 02045 is Bottrop-Kirchhellen
        "02046" | "DE"       | "0"            | true
        "02047" | "DE"       | "0"            | true
        "02048" | "DE"       | "0"            | true
        "02049" | "DE"       | "0"            | true
        "02050" | "DE"       | "0"            | true
        // 02051 till 02054 are in use
        "02055" | "DE"       | "0"            | true
        // 02056 is Heiligenhausen
        "02057" | "DE"       | "0"            | true
        // 02058 is Wülfrath
        "02059" | "DE"       | "0"            | true
        "02060" | "DE"       | "0"            | true
        "02061" | "DE"       | "0"            | true
        "02062" | "DE"       | "0"            | true
        "02063" | "DE"       | "0"            | true
        // 02064 till 02066 is in use
        "02067" | "DE"       | "0"            | true
        "02068" | "DE"       | "0"            | true
        "02069" | "DE"       | "0"            | true
        "0207"  | "DE"       | "0"            | true
        // 0208 & 0209 is in use
        "02100" | "DE"       | "0"            | true
        "02101" | "DE"       | "0"            | true
        // 02102 till 02104 is in use
        "02105" | "DE"       | "0"            | true
        "02106" | "DE"       | "0"            | true
        "02107" | "DE"       | "0"            | true
        "02108" | "DE"       | "0"            | true
        "02109" | "DE"       | "0"            | true
        // special case 0212 for Solingen also covers 02129 for Haan Rheinl since Solingen may not use numbers starting with 9
        "02130" | "DE"       | "0"            | true
        // 02131 till 02133 is in use
        "02134" | "DE"       | "0"            | true
        "02135" | "DE"       | "0"            | true
        "02136" | "DE"       | "0"            | true
        // 02137 is Neuss-Norf
        "02138" | "DE"       | "0"            | true
        "02139" | "DE"       | "0"            | true
        // 0214 is Leverkusen
        // 02150 till 02154 is in use
        "02155" | "DE"       | "0"            | true
        // 02156 till 02159 is in use
        "02160" | "DE"       | "0"            | true
        // 02161 till 02166 is in use
        "02167" | "DE"       | "0"            | true
        "02168" | "DE"       | "0"            | true
        "02169" | "DE"       | "0"            | true
        "02170" | "DE"       | "0"            | true
        // 02171 is Leverkusen-Opladen
        "02172" | "DE"       | "0"            | true
        // 02173 till 02175 is in use
        "02176" | "DE"       | "0"            | true
        "02177" | "DE"       | "0"            | true
        "02178" | "DE"       | "0"            | true
        "02179" | "DE"       | "0"            | true
        "02180" | "DE"       | "0"            | true
        // 02181 till 02183 is in use
        "02184" | "DE"       | "0"            | true
        "02185" | "DE"       | "0"            | true
        "02186" | "DE"       | "0"            | true
        "02187" | "DE"       | "0"            | true
        "02188" | "DE"       | "0"            | true
        "02189" | "DE"       | "0"            | true
        "02190" | "DE"       | "0"            | true
        // 02191 till 02193 is in use
        "02194" | "DE"       | "0"            | true
        // 02195 till 02196 is in use
        "02197" | "DE"       | "0"            | true
        "02198" | "DE"       | "0"            | true
        "02199" | "DE"       | "0"            | true
        "02200" | "DE"       | "0"            | true
        "02201" | "DE"       | "0"            | true
        // 02202 till 02208 is in use
        "02209" | "DE"       | "0"            | true
        // 0221 is Köln
        "02220" | "DE"       | "0"            | true
        "02221" | "DE"       | "0"            | true
        // 02222 till 02228 is in use
        "02229" | "DE"       | "0"            | true
        "02230" | "DE"       | "0"            | true
        "02231" | "DE"       | "0"            | true
        // 02232 till 02238 is in use
        "02239" | "DE"       | "0"            | true
        "02240" | "DE"       | "0"            | true
        // 02241 till 02248 is in use
        "02249" | "DE"       | "0"            | true
        "02250" | "DE"       | "0"            | true
        // 02251 till 02257 is in use
        "02258" | "DE"       | "0"            | true
        "02259" | "DE"       | "0"            | true
        "02260" | "DE"       | "0"            | true
        // 02261 till 02269 is in use
        "02270" | "DE"       | "0"            | true
        // 02271 till 02275 is in use
        "02276" | "DE"       | "0"            | true
        "02277" | "DE"       | "0"            | true
        "02278" | "DE"       | "0"            | true
        "02279" | "DE"       | "0"            | true
        // 0228 is Bonn
        "02290" | "DE"       | "0"            | true
        // 02291 till 02297 is in use
        "02298" | "DE"       | "0"            | true
        "02299" | "DE"       | "0"            | true
        "02300" | "DE"       | "0"            | true
        // 02301 till 02309 is in use
        // 0231 is Dortmund
        "02320" | "DE"       | "0"            | true
        "02321" | "DE"       | "0"            | true
        "02322" | "DE"       | "0"            | true
        // 02323 till 02325 is in use
        "02326" | "DE"       | "0"            | true
        // 02327 is Bochum-Wattenscheid
        "02328" | "DE"       | "0"            | true
        "02329" | "DE"       | "0"            | true
        // 02330 till 02339 is in use
        // 0234 is Bochum
        "02350" | "DE"       | "0"            | true
        // 02351 till 02355 is in use
        "02356" | "DE"       | "0"            | true
        // 02357 till 02358 is in use
        // 02360 till 02369 is in use
        "02370" | "DE"       | "0"            | true
        // 02371 till 02375 is in use
        "02376" | "DE"       | "0"            | true
        // 02377 till 02379 is in use
        "02380" | "DE"       | "0"            | true
        // 02381 till 02385 is in use
        "02386" | "DE"       | "0"            | true
        // 02387 till 02389 is in use
        "02390" | "DE"       | "0"            | true
        // 02391 till 02395 is in use
        "02396" | "DE"       | "0"            | true
        "02397" | "DE"       | "0"            | true
        "02398" | "DE"       | "0"            | true
        "02399" | "DE"       | "0"            | true
        "02400" | "DE"       | "0"            | true
        // 02401 till 02409 is in use
        // 0241 is Aachen
        "02420" | "DE"       | "0"            | true
        // 02421 till 02429 is in use
        "02430" | "DE"       | "0"            | true
        // 02431 till 02436 is in use
        "02437" | "DE"       | "0"            | true
        "02438" | "DE"       | "0"            | true
        "02439" | "DE"       | "0"            | true
        // 02440 till 02441 is in use
        "02442" | "DE"       | "0"            | true
        // 02443 till 02449 is in use
        "02450" | "DE"       | "0"            | true
        // 02451 till 02456 is in use
        "02457" | "DE"       | "0"            | true
        "02458" | "DE"       | "0"            | true
        "02459" | "DE"       | "0"            | true
        "02460" | "DE"       | "0"            | true
        // 02461 till 02465 is in use
        "02466" | "DE"       | "0"            | true
        "02467" | "DE"       | "0"            | true
        "02468" | "DE"       | "0"            | true
        "02469" | "DE"       | "0"            | true
        "02470" | "DE"       | "0"            | true
        // 02471 till 02474 is in use
        "02475" | "DE"       | "0"            | true
        "02476" | "DE"       | "0"            | true
        "02477" | "DE"       | "0"            | true
        "02478" | "DE"       | "0"            | true
        "02479" | "DE"       | "0"            | true
        "02480" | "DE"       | "0"            | true
        "02481" | "DE"       | "0"            | true
        // 02482 is Hellenthal
        "02483" | "DE"       | "0"            | true
        // 02484 till 02486 is in use
        "02487" | "DE"       | "0"            | true
        "02488" | "DE"       | "0"            | true
        "02489" | "DE"       | "0"            | true
        "0249"  | "DE"       | "0"            | true
        "02500" | "DE"       | "0"            | true
        // 02501 till 02502 is in use
        "02503" | "DE"       | "0"            | true
        // 02504 till 02509 is in use
        // 0251 is Münster
        // 02520 till 02529 is in use
        "02530" | "DE"       | "0"            | true
        "02531" | "DE"       | "0"            | true
        // 02532 till 02536 is in use
        "02531" | "DE"       | "0"            | true
        // 02538 is Drensteinfurt-Rinkerode
        "02539" | "DE"       | "0"            | true
        "02540" | "DE"       | "0"            | true
        // 02541 till 02543 is in use
        "02544" | "DE"       | "0"            | true
        // 02545 till 02548 is in use
        "02549" | "DE"       | "0"            | true
        "02550" | "DE"       | "0"            | true
        // 02551 till 02558 is in use
        "02559" | "DE"       | "0"            | true
        "02560" | "DE"       | "0"            | true
        // 02561 till 02568 is in use
        "02569" | "DE"       | "0"            | true
        "02570" | "DE"       | "0"            | true
        // 02571 till 02575 is in use
        "02576" | "DE"       | "0"            | true
        "02577" | "DE"       | "0"            | true
        "02578" | "DE"       | "0"            | true
        "02579" | "DE"       | "0"            | true
        "02580" | "DE"       | "0"            | true
        // 02581 till 02588 is in use
        "02589" | "DE"       | "0"            | true
        // 02590 till 02599 is in use
        "02600" | "DE"       | "0"            | true
        // 02601 till 02608 is in use
        "02609" | "DE"       | "0"            | true
        // 0261 is Koblenz am Rhein
        // 02620 till 02628 is in use
        "02629" | "DE"       | "0"            | true
        // 02630 till 02639 is in use
        "02640" | "DE"       | "0"            | true
        // 02641 till 02647 is in use
        "02648" | "DE"       | "0"            | true
        "02649" | "DE"       | "0"            | true
        "02650" | "DE"       | "0"            | true
        // 02651 till 02657 is in use
        "02658" | "DE"       | "0"            | true
        "02659" | "DE"       | "0"            | true
        "02660" | "DE"       | "0"            | true
        // 02661 till 02664 is in use
        "02665" | "DE"       | "0"            | true
        // 02666 till 02667 is in use
        "02668" | "DE"       | "0"            | true
        "02669" | "DE"       | "0"            | true
        "02670" | "DE"       | "0"            | true
        // 02671 till 02678 is in use
        "02679" | "DE"       | "0"            | true
        // 02680 till 02689 is in use
        "02690" | "DE"       | "0"            | true
        // 02691 till 02697 is in use
        "02698" | "DE"       | "0"            | true
        "02699" | "DE"       | "0"            | true
        // 0271 is Siegen
        "02720" | "DE"       | "0"            | true
        // 02721 till 02725 is in use
        "02726" | "DE"       | "0"            | true
        "02727" | "DE"       | "0"            | true
        "02728" | "DE"       | "0"            | true
        "02729" | "DE"       | "0"            | true
        "02730" | "DE"       | "0"            | true
        "02731" | "DE"       | "0"            | true
        // 02731 till 02739 is in use
        "02740" | "DE"       | "0"            | true
        // 02741 till 02745 is in use
        "02746" | "DE"       | "0"            | true
        // 02747 is Molzhain
        "02748" | "DE"       | "0"            | true
        "02749" | "DE"       | "0"            | true
        // 02750 till 02755 is in use
        "02756" | "DE"       | "0"            | true
        "02757" | "DE"       | "0"            | true
        // 02758 till 02759 is in use
        "02760" | "DE"       | "0"            | true
        // 02761 till 02764 is in use
        "02765" | "DE"       | "0"            | true
        "02766" | "DE"       | "0"            | true
        "02767" | "DE"       | "0"            | true
        "02768" | "DE"       | "0"            | true
        "02769" | "DE"       | "0"            | true
        // 02770 till 02779 is in use
        "02780" | "DE"       | "0"            | true
        // 02781 till 02784 is in use
        "02785" | "DE"       | "0"            | true
        "02786" | "DE"       | "0"            | true
        "02787" | "DE"       | "0"            | true
        "02788" | "DE"       | "0"            | true
        "02789" | "DE"       | "0"            | true
        "0279"  | "DE"       | "0"            | true
        "02790" | "DE"       | "0"            | true
        "02791" | "DE"       | "0"            | true
        "02792" | "DE"       | "0"            | true
        "02793" | "DE"       | "0"            | true
        "02794" | "DE"       | "0"            | true
        "02795" | "DE"       | "0"            | true
        "02796" | "DE"       | "0"            | true
        "02797" | "DE"       | "0"            | true
        "02798" | "DE"       | "0"            | true
        "02799" | "DE"       | "0"            | true
        "02800" | "DE"       | "0"            | true
        // 02801 till 02804 is in use
        "02805" | "DE"       | "0"            | true
        "02806" | "DE"       | "0"            | true
        "02807" | "DE"       | "0"            | true
        "02808" | "DE"       | "0"            | true
        "02809" | "DE"       | "0"            | true
        // 0281 is Wesel
        "02820" | "DE"       | "0"            | true
        // 02821 till 02828 is in use
        "02829" | "DE"       | "0"            | true
        "02830" | "DE"       | "0"            | true
        // 02831 till 02839 is in use
        "02840" | "DE"       | "0"            | true
        // 02841 till 02845 is in use
        "02846" | "DE"       | "0"            | true
        "02847" | "DE"       | "0"            | true
        "02848" | "DE"       | "0"            | true
        "02849" | "DE"       | "0"            | true
        // 02850 till 02853 is in use
        "02854" | "DE"       | "0"            | true
        // 02855 till 02859 is in use
        "02860" | "DE"       | "0"            | true
        // 02861 till 02867 is in use
        "02868" | "DE"       | "0"            | true
        "02869" | "DE"       | "0"            | true
        "02870" | "DE"       | "0"            | true
        // 02871 till 02874 is in use
        "02875" | "DE"       | "0"            | true
        "02876" | "DE"       | "0"            | true
        "02877" | "DE"       | "0"            | true
        "02878" | "DE"       | "0"            | true
        "02879" | "DE"       | "0"            | true
        "0288"  | "DE"       | "0"            | true
        "0289"  | "DE"       | "0"            | true
        "02900" | "DE"       | "0"            | true
        "02901" | "DE"       | "0"            | true
        // 02902 till 02905 is in use
        "02906" | "DE"       | "0"            | true
        "02907" | "DE"       | "0"            | true
        "02908" | "DE"       | "0"            | true
        "02909" | "DE"       | "0"            | true
        // 0291 is Meschede
        "02920" | "DE"       | "0"            | true
        // 02921 till 02925 is in use
        "02926" | "DE"       | "0"            | true
        // 02927 till 02928 is in use
        "02929" | "DE"       | "0"            | true
        "02930" | "DE"       | "0"            | true
        // 02931 till 02935 is in use
        "02936" | "DE"       | "0"            | true
        // 02937 till 02938 is in use
        "02939" | "DE"       | "0"            | true
        "02940" | "DE"       | "0"            | true
        // 02941 till 02945 is in use
        "02946" | "DE"       | "0"            | true
        // 02947 till 02948 is in use
        "02949" | "DE"       | "0"            | true
        "02950" | "DE"       | "0"            | true
        // 02951 till 02955 is in use
        "02956" | "DE"       | "0"            | true
        // 02957 till 02958 is in use
        "02959" | "DE"       | "0"            | true
        "02960" | "DE"       | "0"            | true
        // 02961 till 02964 is in use
        "02965" | "DE"       | "0"            | true
        "02966" | "DE"       | "0"            | true
        "02967" | "DE"       | "0"            | true
        "02968" | "DE"       | "0"            | true
        "02969" | "DE"       | "0"            | true
        "02970" | "DE"       | "0"            | true
        // 02971 till 02975 is in use
        "02976" | "DE"       | "0"            | true
        // 02977 is Schmallenberg-Bödefeld
        "02978" | "DE"       | "0"            | true
        "02979" | "DE"       | "0"            | true
        "02980" | "DE"       | "0"            | true
        // 02981 till 02985 is in use
        "02986" | "DE"       | "0"            | true
        "02987" | "DE"       | "0"            | true
        "02988" | "DE"       | "0"            | true
        "02989" | "DE"       | "0"            | true
        "02990" | "DE"       | "0"            | true
        // 02991 till 02994 is in use
        "02995" | "DE"       | "0"            | true
        "02996" | "DE"       | "0"            | true
        "02997" | "DE"       | "0"            | true
        "02998" | "DE"       | "0"            | true
        "02999" | "DE"       | "0"            | true
    }

    def "check if original lib fixed RFC3966 for invalid German NDC 030 - 039999"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        when: "get number RFC3966: $number"
        String[] results = []
        for (int i = 0; i < numbersToTest.length; i++) {
            String onkz = extractONKZ(numbersToTest[i], regionCode)
            String eResult = number.substring(1)
            if (onkz == null) {
                results += "0"
            } else {
                if (eResult == onkz) {
                    results += "1"
                } else {
                    results += "2"
                }

            }
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
        "0312"               | "DE"       | "0"             | true
        "0313"               | "DE"       | "0"             | true
        "0314"               | "DE"       | "0"             | true
        "0315"               | "DE"       | "0"             | true
        "0316"               | "DE"       | "0"             | true
        "0317"               | "DE"       | "0"             | true
        "0318"               | "DE"       | "0"             | true
        "0319"               | "DE"       | "0"             | true
        // ---
        // ---
        // 032 is checked in multiple 032 test (due to different blocks are only in use currently) see above
        // ---
        "03300"              | "DE"       | "0"             | true
        // 03301 till 03304 is in use
        "033050"             | "DE"       | "0"             | true
        // 033051 till 033056 is in use
        "033057"             | "DE"       | "0"             | true
        "033058"             | "DE"       | "0"             | true
        "033059"             | "DE"       | "0"             | true
        // 03306 till 03307 is in use
        // 033080 is Marienthal Kreis Oberhavel
        "033081"             | "DE"       | "0"             | true
        // 033082 till 033089 is in use
        "033090"             | "DE"       | "0"             | true
        "033091"             | "DE"       | "0"             | true
        "033092"             | "DE"       | "0"             | true
        // 033093 till 033094 is in use
        "033095"             | "DE"       | "0"             | true
        "033096"             | "DE"       | "0"             | true
        "033097"             | "DE"       | "0"             | true
        "033098"             | "DE"       | "0"             | true
        "033099"             | "DE"       | "0"             | true
        // 0331 is Potsdam
        // 033200 till 033209 is in use
        // 03321 is Nauen Brandenburg
        // 03322 is Falkensee
        // 033230 till 033235 is in use
        "033236"             | "DE"       | "0"             | true
        // 033237 till 033239 is in use
        "03324"              | "DE"       | "0"             | true
        "03325"              | "DE"       | "0"             | true
        "03326"              | "DE"       | "0"             | true
        // 03327 till 03329 is in use
        "03330"              | "DE"       | "0"             | true
        // 03331 till 03332 is in use
        "033330"             | "DE"       | "0"             | true
        // 033331 till 033338 is in use
        "033339"             | "DE"       | "0"             | true
        // 03334 till 03335 is in use
        "033360"             | "DE"       | "0"             | true
        // 033361 till 033369 is in use
        // 03337 till 03338 is in use
        "033390"             | "DE"       | "0"             | true
        "033391"             | "DE"       | "0"             | true
        "033392"             | "DE"       | "0"             | true
        // 033393 till 033398 is in use
        "033399"             | "DE"       | "0"             | true
        "03340"              | "DE"       | "0"             | true
        // 03341 till 03342 is in use
        "033430"             | "DE"       | "0"             | true
        "033431"             | "DE"       | "0"             | true
        // 033432 till 033439 is in use
        // 03344 is Bad Freienwalde
        "033450"             | "DE"       | "0"             | true
        // 033451 till 033452 is in use
        "033453"             | "DE"       | "0"             | true
        // 033454 is Wölsickendorf/Wollenberg
        "033455"             | "DE"       | "0"             | true
        // 033456 till 033458 is in use
        "033459"             | "DE"       | "0"             | true
        // 03346 is Seelow
        // 033470 is Lietzen
        "033471"             | "DE"       | "0"             | true
        // 033472 till 033479 is in use
        // 0335 is Frankfurt (Oder)
        "033600"             | "DE"       | "0"             | true
        // 033601 till 033609 is in use
        // 03361 till 03362 is in use
        "033630"             | "DE"       | "0"             | true
        // 033631 till 033638 is in use
        "033639"             | "DE"       | "0"             | true
        // 03364 is Eisenhüttenstadt
        "033650"             | "DE"       | "0"             | true
        "033651"             | "DE"       | "0"             | true
        // 033652 till 033657 is in use
        "033658"             | "DE"       | "0"             | true
        "033659"             | "DE"       | "0"             | true
        // 03366 is Beeskow
        "033670"             | "DE"       | "0"             | true
        // 033671 till 033679 is in use
        "03368"              | "DE"       | "0"             | true
        "03369"              | "DE"       | "0"             | true
        "033700"             | "DE"       | "0"             | true
        // 033701 till 033704 is in use
        "033705"             | "DE"       | "0"             | true
        "033706"             | "DE"       | "0"             | true
        "033707"             | "DE"       | "0"             | true
        // 033708 is Rangsdorf
        "033709"             | "DE"       | "0"             | true
        // 03371 till 03372 is in use
        "033730"             | "DE"       | "0"             | true
        // 033731 till 033734 is in use
        "033735"             | "DE"       | "0"             | true
        "033736"             | "DE"       | "0"             | true
        "033737"             | "DE"       | "0"             | true
        "033738"             | "DE"       | "0"             | true
        "033739"             | "DE"       | "0"             | true
        "033740"             | "DE"       | "0"             | true
        // 033741 till 033748 is in use
        "033749"             | "DE"       | "0"             | true
        // 03375 is Königs Wusterhausen
        // 33760 is Münchehofe Kreis Dahme-Spreewald
        "033761"             | "DE"       | "0"             | true
        // 033762 till 033769 is in use
        // 03377 till 03379 is in use
        "03380"              | "DE"       | "0"             | true
        // 03381 till 03382 is in use
        // 033830 till 033839 is in use
        "033840"             | "DE"       | "0"             | true
        // 033841 is Belzig
        "033842"             | "DE"       | "0"             | true
        // 033843 till 033849 is in use
        // 03385 till 03386 is in use
        // 033870 is Zollchow bei Rathenow
        "033871"             | "DE"       | "0"             | true
        // 033872 till 033878 is in use
        "033879"             | "DE"       | "0"             | true
        "03388"              | "DE"       | "0"             | true
        "03389"              | "DE"       | "0"             | true
        "03390"              | "DE"       | "0"             | true
        // 03391 is Neuruppin
        // 033920 till 033929 is in use
        "033930"             | "DE"       | "0"             | true
        // 033931 till 033933 is in use
        "033934"             | "DE"       | "0"             | true
        "033935"             | "DE"       | "0"             | true
        "033936"             | "DE"       | "0"             | true
        "033937"             | "DE"       | "0"             | true
        "033938"             | "DE"       | "0"             | true
        "033939"             | "DE"       | "0"             | true
        // 03394 till 03395 is in use
        "033960"             | "DE"       | "0"             | true
        "033961"             | "DE"       | "0"             | true
        // 033962 till 033969 is in use
        // 033970 till 033979 is in use
        "033980"             | "DE"       | "0"             | true
        // 033981 till 033984 is in use
        "033985"             | "DE"       | "0"             | true
        // 033986 is Falkenhagen Kreis Prignitz
        "033987"             | "DE"       | "0"             | true
        "033988"             | "DE"       | "0"             | true
        // 033989 is Sadenbeck
        "03399"              | "DE"       | "0"             | true
        // 0340 till 0341 is in use
        "034200"             | "DE"       | "0"             | true
        "034201"             | "DE"       | "0"             | true
        // 034202 till 034208 is in use
        "034209"             | "DE"       | "0"             | true
        // 03421 is Torgau
        "034220"             | "DE"       | "0"             | true
        // 034221 till 034224 is in use
        "034225"             | "DE"       | "0"             | true
        "034226"             | "DE"       | "0"             | true
        "034227"             | "DE"       | "0"             | true
        "034228"             | "DE"       | "0"             | true
        "034229"             | "DE"       | "0"             | true
        // 03423 is Eilenburg
        "034240"             | "DE"       | "0"             | true
        // 034241 till 034244 is in use
        "034245"             | "DE"       | "0"             | true
        "034246"             | "DE"       | "0"             | true
        "034247"             | "DE"       | "0"             | true
        "034248"             | "DE"       | "0"             | true
        "034249"             | "DE"       | "0"             | true
        // 03425 is Wurzen
        "034260"             | "DE"       | "0"             | true
        // 034261 till 034263 is in use
        "03427"              | "DE"       | "0"             | true
        "03428"              | "DE"       | "0"             | true
        "034290"             | "DE"       | "0"             | true
        // 034291 till 034293 is in use
        "03430"              | "DE"       | "0"             | true
        // 03431 is Döbeln
        "034320"             | "DE"       | "0"             | true
        // 034321 till 034322 is in use
        "034323"             | "DE"       | "0"             | true
        // 034324 till 034325 is in use
        "034326"             | "DE"       | "0"             | true
        // 034327 till 034328 is in use
        "034329"             | "DE"       | "0"             | true
        // 03433 is Borna Stadt
        "034340"             | "DE"       | "0"             | true
        // 034341 till 034348 is in use
        "034349"             | "DE"       | "0"             | true
        // 03435 is Oschatz
        "034360"             | "DE"       | "0"             | true
        // 034361 till 034364 is in use
        "034365"             | "DE"       | "0"             | true
        "034366"             | "DE"       | "0"             | true
        "034367"             | "DE"       | "0"             | true
        "034368"             | "DE"       | "0"             | true
        "034369"             | "DE"       | "0"             | true
        // 03437 is Grimma
        "034380"             | "DE"       | "0"             | true
        // 034381 till 034386 is in use
        "034387"             | "DE"       | "0"             | true
        "034388"             | "DE"       | "0"             | true
        "034389"             | "DE"       | "0"             | true
        "03439"              | "DE"       | "0"             | true
        "03440"              | "DE"       | "0"             | true
        // 03441 is Zeitz
        "034420"             | "DE"       | "0"             | true
        "034421"             | "DE"       | "0"             | true
        // 034422 till 034426 is in use
        "034427"             | "DE"       | "0"             | true
        "034428"             | "DE"       | "0"             | true
        "034429"             | "DE"       | "0"             | true
        // 03443 is Weissenfels Sachsen-Anhalt
        "034440"             | "DE"       | "0"             | true
        // 034441 is Hohenmölsen
        "034442"             | "DE"       | "0"             | true
        // 034443 till 034446 is in use
        "034447"             | "DE"       | "0"             | true
        "034448"             | "DE"       | "0"             | true
        "034449"             | "DE"       | "0"             | true
        // 03445 is Naumburg Saale
        "034460"             | "DE"       | "0"             | true
        // 034461 till 034467 is in use
        "034468"             | "DE"       | "0"             | true
        "034469"             | "DE"       | "0"             | true
        // 03447 till 03448 is in use
        "034490"             | "DE"       | "0"             | true
        // 034491 till 034498 is in use
        "034499"             | "DE"       | "0"             | true
        // 0345 is Halle Saale
        // 034600 toll 034607 is in use
        "034608"             | "DE"       | "0"             | true
        // 034609 is Salzmünde
        // 03461 till 03462 is in use
        "034630"             | "DE"       | "0"             | true
        "034631"             | "DE"       | "0"             | true
        // 034632 till 034633 is in use
        "034634"             | "DE"       | "0"             | true
        // 034635 till 034639 is in use
        // 03464 is Sangerhausen
        "034650"             | "DE"       | "0"             | true
        // 034651 till 034654 is in use
        "034655"             | "DE"       | "0"             | true
        // 034656 is Wallhausen Sachsen-Anhalt
        "034657"             | "DE"       | "0"             | true
        // 034658 till 034659 is in use
        // 03466 is Artern Unstrut
        "034670"             | "DE"       | "0"             | true
        // 034671 till 034673 is in use
        "034674"             | "DE"       | "0"             | true
        "034675"             | "DE"       | "0"             | true
        "034676"             | "DE"       | "0"             | true
        "034677"             | "DE"       | "0"             | true
        "034678"             | "DE"       | "0"             | true
        "034679"             | "DE"       | "0"             | true
        "03468"              | "DE"       | "0"             | true
        "034690"             | "DE"       | "0"             | true
        // 034691 till 034692 is in use
        "034693"             | "DE"       | "0"             | true
        "034694"             | "DE"       | "0"             | true
        "034695"             | "DE"       | "0"             | true
        "034696"             | "DE"       | "0"             | true
        "034697"             | "DE"       | "0"             | true
        "034698"             | "DE"       | "0"             | true
        "034699"             | "DE"       | "0"             | true
        "03470"              | "DE"       | "0"             | true
        // 03471 is Bernburg Saale
        "034720"             | "DE"       | "0"             | true
        // 034721 till 034722 is in use
        "034723"             | "DE"       | "0"             | true
        "034724"             | "DE"       | "0"             | true
        "034725"             | "DE"       | "0"             | true
        "034726"             | "DE"       | "0"             | true
        "034727"             | "DE"       | "0"             | true
        "034728"             | "DE"       | "0"             | true
        "034729"             | "DE"       | "0"             | true
        // 3473 is Aschersleben Sachsen-Anhalt
        "034740"             | "DE"       | "0"             | true
        // 034741 till 034743 is in use
        "034744"             | "DE"       | "0"             | true
        // 034745 till 034746 is in use
        "034747"             | "DE"       | "0"             | true
        "034748"             | "DE"       | "0"             | true
        "034749"             | "DE"       | "0"             | true
        // 03475 till 03476 is in use
        "034770"             | "DE"       | "0"             | true
        // 034771 till 034776 is in use
        "034777"             | "DE"       | "0"             | true
        "034778"             | "DE"       | "0"             | true
        // 034779 is Abberode
        "034780"             | "DE"       | "0"             | true
        // 034781 till 034783 is in use
        "034784"             | "DE"       | "0"             | true
        // 034785 is Sandersleben
        "034786"             | "DE"       | "0"             | true
        "034787"             | "DE"       | "0"             | true
        "034788"             | "DE"       | "0"             | true
        "034789"             | "DE"       | "0"             | true
        "03479"              | "DE"       | "0"             | true
        "0348"               | "DE"       | "0"             | true
        "034900"             | "DE"       | "0"             | true
        // 034901 is Roßlau Elbe
        "034902"             | "DE"       | "0"             | true
        // 034903 till 034907
        "034908"             | "DE"       | "0"             | true
        // 034909 is Aken Elbe
        // 03491 till 03494 (yes full 03492x is used, too) is in use
        "034950"             | "DE"       | "0"             | true
        "034951"             | "DE"       | "0"             | true
        "034952"             | "DE"       | "0"             | true
        // 034953 till 034956
        "034957"             | "DE"       | "0"             | true
        "034958"             | "DE"       | "0"             | true
        "034959"             | "DE"       | "0"             | true
        // 03496 is Köthen Anhalt
        "034970"             | "DE"       | "0"             | true
        "034971"             | "DE"       | "0"             | true
        "034972"             | "DE"       | "0"             | true
        // 034973 is Osternienburg
        "034974"             | "DE"       | "0"             | true
        // 034975 till 034979 is in use
        "03498"              | "DE"       | "0"             | true
        "03499"              | "DE"       | "0"             | true
        "03500"              | "DE"       | "0"             | true
        // 03501 is Pirna
        "035029"             | "DE"       | "0"             | true
        "035030"             | "DE"       | "0"             | true
        "035031"             | "DE"       | "0"             | true
        // 035032 till 035033 is in use
        "035034"             | "DE"       | "0"             | true
        "035035"             | "DE"       | "0"             | true
        "035036"             | "DE"       | "0"             | true
        "035038"             | "DE"       | "0"             | true
        "035038"             | "DE"       | "0"             | true
        "035039"             | "DE"       | "0"             | true
        // 03504 is Dippoldiswalde
        "035050"             | "DE"       | "0"             | true
        "035051"             | "DE"       | "0"             | true
        // 035052 till 035058
        "035059"             | "DE"       | "0"             | true
        "03506"              | "DE"       | "0"             | true
        "03507"              | "DE"       | "0"             | true
        "03508"              | "DE"       | "0"             | true
        "03509"              | "DE"       | "0"             | true
        // 0351 is Dresden
        // 03520x till 03525 is in use (inclusive complete 03524x)
        "035260"             | "DE"       | "0"             | true
        "035261"             | "DE"       | "0"             | true
        "035262"             | "DE"       | "0"             | true
        // 035263 till 035268
        "035269"             | "DE"       | "0"             | true
        "03527"              | "DE"       | "0"             | true
        // 03529 till 03529 is in use
        "03530"              | "DE"       | "0"             | true
        // 03531 is Finsterwalde
        "035320"             | "DE"       | "0"             | true
        "035321"             | "DE"       | "0"             | true
        // 035322 till 035327
        "035328"             | "DE"       | "0"             | true
        // 035329 is Dollenchen
        // 03533 is Elsterwerda
        "035340"             | "DE"       | "0"             | true
        // 035341 till 035343
        "035344"             | "DE"       | "0"             | true
        "035345"             | "DE"       | "0"             | true
        "035346"             | "DE"       | "0"             | true
        "035347"             | "DE"       | "0"             | true
        "035348"             | "DE"       | "0"             | true
        "035349"             | "DE"       | "0"             | true
        // 03535 is Herzberg Elster
        "035360"             | "DE"       | "0"             | true
        // 035361 till 035365 is in use
        "035366"             | "DE"       | "0"             | true
        "035367"             | "DE"       | "0"             | true
        "035369"             | "DE"       | "0"             | true
        "035369"             | "DE"       | "0"             | true
        // 03537 is Jessen Elster
        "035380"             | "DE"       | "0"             | true
        "035381"             | "DE"       | "0"             | true
        "035382"             | "DE"       | "0"             | true
        // 035383 till 035389 is in use
        "03539"              | "DE"       | "0"             | true
        "03540"              | "DE"       | "0"             | true
        // 03541 till 03542 is in use
        "035430"             | "DE"       | "0"             | true
        "035431"             | "DE"       | "0"             | true
        "035432"             | "DE"       | "0"             | true
        // 035433 till 035436 is in use
        "035437"             | "DE"       | "0"             | true
        "035438"             | "DE"       | "0"             | true
        // 035439 is Zinnitz
        // 03544 is Luckau Brandenburg
        "035450"             | "DE"       | "0"             | true
        // 035451 till 035456 is in use
        "035457"             | "DE"       | "0"             | true
        "035458"             | "DE"       | "0"             | true
        "035459"             | "DE"       | "0"             | true
        // 03546 is Lübben Spreewald
        "035470"             | "DE"       | "0"             | true
        // 035471 till 035478 is in use
        "035479"             | "DE"       | "0"             | true
        "03548"              | "DE"       | "0"             | true
        "03549"              | "DE"       | "0"             | true
        // 0355 is Cottbus
        // 03560x till 03564 is in use
        "03565"              | "DE"       | "0"             | true
        "03566"              | "DE"       | "0"             | true
        "03567"              | "DE"       | "0"             | true
        "03568"              | "DE"       | "0"             | true
        "035690"             | "DE"       | "0"             | true
        // 035691 till 035698 is in use
        "035699"             | "DE"       | "0"             | true
        "03570"              | "DE"       | "0"             | true
        // 03571 is Hoyerswerda
        "035720"             | "DE"       | "0"             | true
        "035721"             | "DE"       | "0"             | true
        // 035722 till 035728 is in use
        "035729"             | "DE"       | "0"             | true
        // 03573 till 03574 is in use
        "035750"             | "DE"       | "0"             | true
        // 035751 till 035756 is in use
        "035757"             | "DE"       | "0"             | true
        "035758"             | "DE"       | "0"             | true
        "035759"             | "DE"       | "0"             | true
        // 03576 is Weisswasser
        "035770"             | "DE"       | "0"             | true
        // 035771 till 035775 is in use
        "035776"             | "DE"       | "0"             | true
        "035777"             | "DE"       | "0"             | true
        "035778"             | "DE"       | "0"             | true
        "035779"             | "DE"       | "0"             | true
        // 03578 is Kamenz
        "035790"             | "DE"       | "0"             | true
        "035791"             | "DE"       | "0"             | true
        // 035792 till 035793 is in use
        "035794"             | "DE"       | "0"             | true
        // 035795 till 035797 is in use
        "035798"             | "DE"       | "0"             | true
        "035799"             | "DE"       | "0"             | true
        "03580"              | "DE"       | "0"             | true
        // 03581 is Görlitz
        // 035820 is Zodel
        "035821"             | "DE"       | "0"             | true
        // 035822 till 035823 is in use
        "035824"             | "DE"       | "0"             | true
        // 035825 till 035829 is in use
        // 03583 is Zittau
        "035840"             | "DE"       | "0"             | true
        // 035841 till 035844 is in use
        "035845"             | "DE"       | "0"             | true
        "035846"             | "DE"       | "0"             | true
        "035847"             | "DE"       | "0"             | true
        "035848"             | "DE"       | "0"             | true
        "035849"             | "DE"       | "0"             | true
        // 03585 till 03586 is in use
        "035870"             | "DE"       | "0"             | true
        "035871"             | "DE"       | "0"             | true
        // 035872 till 035877 is in use
        "035878"             | "DE"       | "0"             | true
        "035879"             | "DE"       | "0"             | true
        // 03588 is Niesky
        "035890"             | "DE"       | "0"             | true
        // 035891 till 0358595 is in use
        "035896"             | "DE"       | "0"             | true
        "035897"             | "DE"       | "0"             | true
        "035898"             | "DE"       | "0"             | true
        "035899"             | "DE"       | "0"             | true
        "03590"              | "DE"       | "0"             | true
        // 03591 till 03594 (including total 03593x) is in use
        "035950"             | "DE"       | "0"             | true
        // 035951 till 035955 is in use
        "035956"             | "DE"       | "0"             | true
        "035957"             | "DE"       | "0"             | true
        "035958"             | "DE"       | "0"             | true
        "035959"             | "DE"       | "0"             | true
        // 03596 is Neustadt in Sachsen
        "035970"             | "DE"       | "0"             | true
        // 035971 till 035975 is in use
        "035976"             | "DE"       | "0"             | true
        "035977"             | "DE"       | "0"             | true
        "035978"             | "DE"       | "0"             | true
        "035979"             | "DE"       | "0"             | true
        "03598"              | "DE"       | "0"             | true
        "03599"              | "DE"       | "0"             | true
        "03600"              | "DE"       | "0"             | true
        // 03601 till 03603 (including total 03602x) is in use
        "036040"             | "DE"       | "0"             | true
        // 036041 till 036043 is in use
        "036044"             | "DE"       | "0"             | true
        "036045"             | "DE"       | "0"             | true
        "036046"             | "DE"       | "0"             | true
        "036047"             | "DE"       | "0"             | true
        "036048"             | "DE"       | "0"             | true
        "036049"             | "DE"       | "0"             | true
        // 03605 till 03606 is in use
        "036070"             | "DE"       | "0"             | true
        // 036071 till 036072 is in use
        "036073"             | "DE"       | "0"             | true
        // 036074 till 036077 is in use
        "036078"             | "DE"       | "0"             | true
        "036079"             | "DE"       | "0"             | true
        "036080"             | "DE"       | "0"             | true
        // 036081 till 036085 is in use
        "036086"             | "DE"       | "0"             | true
        // 036087 is Wüstheuterode
        "036088"             | "DE"       | "0"             | true
        "036089"             | "DE"       | "0"             | true
        "03609"              | "DE"       | "0"             | true
        // 0361 is Erfurt
        // 03620x till 03624 is in use
        "036250"             | "DE"       | "0"             | true
        "036251"             | "DE"       | "0"             | true
        // 036252 till 036259 is in use
        "03626"              | "DE"       | "0"             | true
        "03627"              | "DE"       | "0"             | true
        // 03628 till 03629 is in use
        "03630"              | "DE"       | "0"             | true
        // 03631 till 03632 is in use
        // 036330 till 036338 is in use
        "036339"             | "DE"       | "0"             | true
        // 03634 till 03637x is in use
        "03638"              | "DE"       | "0"             | true
        "03639"              | "DE"       | "0"             | true
        "03640"              | "DE"       | "0"             | true
        // 03641 is Jena
        "036420"             | "DE"       | "0"             | true
        // 036421 till 036428 is in use
        "036429"             | "DE"       | "0"             | true
        // 03643 till 03644 is in use
        // 036450 till 036454 is in use
        "036455"             | "DE"       | "0"             | true
        "036456"             | "DE"       | "0"             | true
        "036457"             | "DE"       | "0"             | true
        // 036458 till 036459 is in use
        "036460"             | "DE"       | "0"             | true
        // 036461 till 036465 is in use
        "036466"             | "DE"       | "0"             | true
        "036467"             | "DE"       | "0"             | true
        "036468"             | "DE"       | "0"             | true
        "036469"             | "DE"       | "0"             | true
        // 03647 is Pößneck
        "036480"             | "DE"       | "0"             | true
        // 036481 till 036484 is in use
        "036485"             | "DE"       | "0"             | true
        "036486"             | "DE"       | "0"             | true
        "036487"             | "DE"       | "0"             | true
        "036488"             | "DE"       | "0"             | true
        "036489"             | "DE"       | "0"             | true
        "03649"              | "DE"       | "0"             | true
        // 0365 is Gera
        "036600"             | "DE"       | "0"             | true
        // 036601 till 036608 is in use
        "036609"             | "DE"       | "0"             | true
        // 03661 is Greiz
        "036620"             | "DE"       | "0"             | true
        // 036621 till 036626 is in use
        "036627"             | "DE"       | "0"             | true
        // 036628 is Zeulenroda
        "036629"             | "DE"       | "0"             | true
        // 03663 is Schleiz
        // 036640 is Remptendorf
        "036641"             | "DE"       | "0"             | true
        // 036642 till 036649 is in use
        "036650"             | "DE"       | "0"             | true
        // 036651 till 036653 is in use
        "036654"             | "DE"       | "0"             | true
        "036655"             | "DE"       | "0"             | true
        "036656"             | "DE"       | "0"             | true
        "036657"             | "DE"       | "0"             | true
        "036658"             | "DE"       | "0"             | true
        "036659"             | "DE"       | "0"             | true
        "03666"              | "DE"       | "0"             | true
        "03667"              | "DE"       | "0"             | true
        "03668"              | "DE"       | "0"             | true
        "036690"             | "DE"       | "0"             | true
        // 036691 till 036695 is in use
        "036696"             | "DE"       | "0"             | true
        "036697"             | "DE"       | "0"             | true
        "036698"             | "DE"       | "0"             | true
        "036699"             | "DE"       | "0"             | true
        "036700"             | "DE"       | "0"             | true
        // 036701 till 036705 is in use
        "036706"             | "DE"       | "0"             | true
        "036707"             | "DE"       | "0"             | true
        "036708"             | "DE"       | "0"             | true
        "036709"             | "DE"       | "0"             | true
        // 03671 till 03673x is in use
        "036740"             | "DE"       | "0"             | true
        // 036741 till 03644 is in use
        "036745"             | "DE"       | "0"             | true
        "036746"             | "DE"       | "0"             | true
        "036747"             | "DE"       | "0"             | true
        "036748"             | "DE"       | "0"             | true
        "036749"             | "DE"       | "0"             | true
        // 03675 is Heubisch
        "036760"             | "DE"       | "0"             | true
        // 036761 till 036762 is in use
        "036763"             | "DE"       | "0"             | true
        // 036764 is Neuhaus-Schierschnitz
        "036765"             | "DE"       | "0"             | true
        // 036766 is SChalkau
        "036767"             | "DE"       | "0"             | true
        "036768"             | "DE"       | "0"             | true
        "036769"             | "DE"       | "0"             | true
        // 03677 is Ilmenau Thüringen
        "036780"             | "DE"       | "0"             | true
        // 036781 till 036785 is in use
        "036786"             | "DE"       | "0"             | true
        "036787"             | "DE"       | "0"             | true
        "036788"             | "DE"       | "0"             | true
        "036789"             | "DE"       | "0"             | true
        // 03679 is Suhl
        "03680"              | "DE"       | "0"             | true
        // 03681 till 03686 (inlcuding total 03684x) is in use
        // 036870 till 036871 is in use
        "036872"             | "DE"       | "0"             | true
        // 036873 till 036875 is in use
        "036876"             | "DE"       | "0"             | true
        "036877"             | "DE"       | "0"             | true
        // 036878 is Oberland
        "036879"             | "DE"       | "0"             | true
        "03688"              | "DE"       | "0"             | true
        "03689"              | "DE"       | "0"             | true
        "03690"              | "DE"       | "0"             | true
        // 036891 till 03693 (including total 036892x) is in use
        // 0368940 till 0368941 is in use
        "036942"             | "DE"       | "0"             | true
        // 0368943 till 0368949 is in use
        // 03695 is Bad Salzungen
        "036960"             | "DE"       | "0"             | true
        // 036961 till 036969 is in use
        "03697"              | "DE"       | "0"             | true
        "03698"              | "DE"       | "0"             | true
        "03699"              | "DE"       | "0"             | true
        "0370"               | "DE"       | "0"             | true
        // 0371 is Chemnitz Sachsen
        // 037200 is Wittgensdorf bei Chemnitz
        "037201"             | "DE"       | "0"             | true
        // 037202 till 03724 is in use
        "037205"             | "DE"       | "0"             | true
        // 037206 till 037209 is in use
        // 03721 till 03727 is in use
        "03728"              | "DE"       | "0"             | true
        "037290"             | "DE"       | "0"             | true
        // 037291 till 037298 is in use
        "037299"             | "DE"       | "0"             | true
        "03730"              | "DE"       | "0"             | true
        // 03731 till 03733 (including total 03732x) is in use
        "037340"             | "DE"       | "0"             | true
        // 037341 till 037344 is in use
        "037345"             | "DE"       | "0"             | true
        // 037346 till 037349 is in use
        // 03735 till 03737 (including total 03736x) is in use
        "037380"             | "DE"       | "0"             | true
        // 037381 till 037384 is in use
        "037385"             | "DE"       | "0"             | true
        "037386"             | "DE"       | "0"             | true
        "037387"             | "DE"       | "0"             | true
        "037388"             | "DE"       | "0"             | true
        "037389"             | "DE"       | "0"             | true
        "03739"              | "DE"       | "0"             | true
        "03740"              | "DE"       | "0"             | true
        // 03741 is Plauen
        "037420"             | "DE"       | "0"             | true
        // 037421 till 037423 is in use
        "037424"             | "DE"       | "0"             | true
        "037425"             | "DE"       | "0"             | true
        "037426"             | "DE"       | "0"             | true
        "037427"             | "DE"       | "0"             | true
        "037428"             | "DE"       | "0"             | true
        "037429"             | "DE"       | "0"             | true
        // 03473x till 03745 is in use
        "037460"             | "DE"       | "0"             | true
        "037461"             | "DE"       | "0"             | true
        // 037462 till 037465 is in use
        "037466"             | "DE"       | "0"             | true
        // 037467 till 037468 is in use
        "037469"             | "DE"       | "0"             | true
        "03747"              | "DE"       | "0"             | true
        "03748"              | "DE"       | "0"             | true
        "03749"              | "DE"       | "0"             | true
        // 0375 is Zwickau
        // 03760x till 03765 is in use
        "03766"              | "DE"       | "0"             | true
        "03767"              | "DE"       | "0"             | true
        "03768"              | "DE"       | "0"             | true
        "03769"              | "DE"       | "0"             | true
        "03770"              | "DE"       | "0"             | true
        // 03771 till 03774 is in use
        "037750"             | "DE"       | "0"             | true
        "037751"             | "DE"       | "0"             | true
        // 037752 is Eibenstock
        "037753"             | "DE"       | "0"             | true
        // 037754 till 037757
        "037758"             | "DE"       | "0"             | true
        "037759"             | "DE"       | "0"             | true
        "03776"              | "DE"       | "0"             | true
        "03777"              | "DE"       | "0"             | true
        "03778"              | "DE"       | "0"             | true
        "03779"              | "DE"       | "0"             | true
        "0378"               | "DE"       | "0"             | true
        "0379"               | "DE"       | "0"             | true
        "0380"               | "DE"       | "0"             | true
        // 0381 is Rostock
        "038200"             | "DE"       | "0"             | true
        // 038201 till 038209
        // 03821 till 03822x
        "038230"             | "DE"       | "0"             | true
        // 038231 till 038234
        "038235"             | "DE"       | "0"             | true
        "038236"             | "DE"       | "0"             | true
        "038237"             | "DE"       | "0"             | true
        "038238"             | "DE"       | "0"             | true
        "038239"             | "DE"       | "0"             | true
        "03824"              | "DE"       | "0"             | true
        "03825"              | "DE"       | "0"             | true
        "03826"              | "DE"       | "0"             | true
        "03827"              | "DE"       | "0"             | true
        "03828"              | "DE"       | "0"             | true
        "038290"             | "DE"       | "0"             | true
        "038291"             | "DE"       | "0"             | true
        // 038292 till 038297 is in use
        "038298"             | "DE"       | "0"             | true
        "038299"             | "DE"       | "0"             | true
        // 03830x till 03831 is in use
        // 038320 till 038328 is in use
        "038329"             | "DE"       | "0"             | true
        "038330"             | "DE"       | "0"             | true
        // 08331 till 038334 is in use
        "038335"             | "DE"       | "0"             | true
        "038336"             | "DE"       | "0"             | true
        "038337"             | "DE"       | "0"             | true
        "038338"             | "DE"       | "0"             | true
        "038339"             | "DE"       | "0"             | true
        // 03834 is Greifswald
        "038350"             | "DE"       | "0"             | true
        // 038351 till 038356 is in use
        "038357"             | "DE"       | "0"             | true
        "038358"             | "DE"       | "0"             | true
        "038359"             | "DE"       | "0"             | true
        // 03836 till 03838 (including total 03837x) is in use
        "038390"             | "DE"       | "0"             | true
        // 038391 till 038393 is in use
        "038394"             | "DE"       | "0"             | true
        "038395"             | "DE"       | "0"             | true
        "038396"             | "DE"       | "0"             | true
        "038397"             | "DE"       | "0"             | true
        "038398"             | "DE"       | "0"             | true
        "038399"             | "DE"       | "0"             | true
        "03840"              | "DE"       | "0"             | true
        // 03841 id Neukloster
        "038420"             | "DE"       | "0"             | true
        "038421"             | "DE"       | "0"             | true
        // 038422 till 038429
        // 03843 till 03845x is in use
        "038460"             | "DE"       | "0"             | true
        // 038461 till 038462 is in use
        "038463"             | "DE"       | "0"             | true
        // 038464 is Bernitt
        "038465"             | "DE"       | "0"             | true
        // 038466 is Jürgenshagen
        "038467"             | "DE"       | "0"             | true
        "038468"             | "DE"       | "0"             | true
        "038469"             | "DE"       | "0"             | true
        // 03847 is Sternberg
        "038480"             | "DE"       | "0"             | true
        // 038481 till 038486 is in use
        "038487"             | "DE"       | "0"             | true
        // 038488 is Demen
        "038489"             | "DE"       | "0"             | true
        "03849"              | "DE"       | "0"             | true
        // 0385 is Schwerin
        // 03860 till 03861 is in use
        "03862"              | "DE"       | "0"             | true
        // 03863 is Crivitz
        "03864"              | "DE"       | "0"             | true
        // 03865 till 03869 is in use
        "03870"              | "DE"       | "0"             | true
        // 03871 till  03872x is in use
        "038730"             | "DE"       | "0"             | true
        // 038731 till 038733 is in use
        "038734"             | "DE"       | "0"             | true
        // 038735 till 038738 is in use
        "038739"             | "DE"       | "0"             | true
        // 03874 till 03877 (including total 03875x) is in use
        // 038780 till 038785 is in use
        "038786"             | "DE"       | "0"             | true
        // 038787 till 038789 is in use
        "038790"             | "DE"       | "0"             | true
        // 038791 till 038794
        "038795"             | "DE"       | "0"             | true
        // 038796 till 038797
        "038798"             | "DE"       | "0"             | true
        "038799"             | "DE"       | "0"             | true
        "03880"              | "DE"       | "0"             | true
        // 03881 is Grevesmühlen
        "038820"             | "DE"       | "0"             | true
        // 038821 till 038828 is in use
        "038829"             | "DE"       | "0"             | true
        // 03883 is Hagenow
        "038840"             | "DE"       | "0"             | true
        // 038841 till 038845 is in use
        "038846"             | "DE"       | "0"             | true
        // 038847 till 038848 is in use
        "038849"             | "DE"       | "0"             | true
        // 038850 till 038856 is in use
        "038857"             | "DE"       | "0"             | true
        // 038858 till 038859 is in use
        // 03886 is Gadebusch
        "038870"             | "DE"       | "0"             | true
        // 038871 till 038876 is in use
        "038877"             | "DE"       | "0"             | true
        "038878"             | "DE"       | "0"             | true
        "038879"             | "DE"       | "0"             | true
        "03888"              | "DE"       | "0"             | true
        "03889"              | "DE"       | "0"             | true
        "0389"               | "DE"       | "0"             | true
        // 03900x till 03905x (including total 03903x) is in use
        "039060"             | "DE"       | "0"             | true
        // 039061 till 039062 is in use
        "039063"             | "DE"       | "0"             | true
        "039064"             | "DE"       | "0"             | true
        "039065"             | "DE"       | "0"             | true
        "039066"             | "DE"       | "0"             | true
        "039067"             | "DE"       | "0"             | true
        "039068"             | "DE"       | "0"             | true
        "039069"             | "DE"       | "0"             | true
        // 03907 till 03909 (including total 03908x) is in use
        // 0391 is Magdeburg
        // 03920x till 03921 is in use
        "039220"             | "DE"       | "0"             | true
        // 039221 till 039226 is in use
        "039227"             | "DE"       | "0"             | true
        "039228"             | "DE"       | "0"             | true
        "039229"             | "DE"       | "0"             | true
        // 03923 is Zerbst
        "039240"             | "DE"       | "0"             | true
        // 039241 till 039248 is in use
        "0392498"            | "DE"       | false           | true
        // 03925 is Stassfurt
        "039260"             | "DE"       | "0"             | true
        "039261"             | "DE"       | "0"             | true
        // 039262 till 039268 is in use
        "039269"             | "DE"       | "0"             | true
        "03927"              | "DE"       | "0"             | true
        // 03928 is Schönebeck Elbe
        "039290"             | "DE"       | "0"             | true
        // 039291 till 039298 is in use
        "039299"             | "DE"       | "0"             | true
        "03930"              | "DE"       | "0"             | true
        // 03931 is Stendal
        // 039320 till 039325 is in use
        "039326"             | "DE"       | "0"             | true
        // 039327 till 039329 is in use
        // 03933 is Genthin
        "039340"             | "DE"       | "0"             | true
        // 039341 till 039349 is in use
        // 03935 is Tangerhütte
        "039360"             | "DE"       | "0"             | true
        // 039361 till 039366 is in use
        "039367"             | "DE"       | "0"             | true
        "039368"             | "DE"       | "0"             | true
        "039369"             | "DE"       | "0"             | true
        // 03937 is Osterburg Altmark
        "039380"             | "DE"       | "0"             | true
        "039381"             | "DE"       | "0"             | true
        // 039382 till 039384 is in use
        "039385"             | "DE"       | "0"             | true
        // 039386 till 039389 is in use
        // total 03939x is in use
        // 03940x till 03941 is in use
        "039420"             | "DE"       | "0"             | true
        // 039421 till 039428 is in use
        "039429"             | "DE"       | "0"             | true
        // 03943 till 03944 is in use
        "039450"             | "DE"       | "0"             | true
        // 039451 till 039459 is in use
        // 03946 till 03947 is in use
        "039480"             | "DE"       | "0"             | true
        // 039481 till 039485 is in use
        "039486"             | "DE"       | "0"             | true
        // 039487 till 039489 is in use
        // 03949 is Oschersleben Bode
        // 0395 is Zwiedorf
        // 039600 till 039608 is in use
        "039609"             | "DE"       | "0"             | true
        // 03961 till 03969 is in use
        "03970"              | "DE"       | "0"             | true
        // 03971 is Anklam
        "039720"             | "DE"       | "0"             | true
        // 039721 till 039724 is in use
        "039725"             | "DE"       | "0"             | true
        // 039726 till 039728 is in use
        "039729"             | "DE"       | "0"             | true
        // 03973 till 03974x is in use
        "039750"             | "DE"       | "0"             | true
        // 039751 till 039754 is in use
        "039755"             | "DE"       | "0"             | true
        "039756"             | "DE"       | "0"             | true
        "039757"             | "DE"       | "0"             | true
        "039758"             | "DE"       | "0"             | true
        "039759"             | "DE"       | "0"             | true
        // 03976 is Torgelow bei Uckermünde
        "039770"             | "DE"       | "0"             | true
        // 039771 till 039779 is in use
        "03980"              | "DE"       | "0"             | true
        // 03981 to 03982x is in use
        "039830"             | "DE"       | "0"             | true
        // 039831 till 039833 is in use
        "039834"             | "DE"       | "0"             | true
        "039835"             | "DE"       | "0"             | true
        "039836"             | "DE"       | "0"             | true
        "039837"             | "DE"       | "0"             | true
        "039838"             | "DE"       | "0"             | true
        "039839"             | "DE"       | "0"             | true
        // 03984 is Prenzlau
        "039850"             | "DE"       | "0"             | true
        // 039851 till 039859 is in use
        "039860"             | "DE"       | "0"             | true
        // 039861 till 039863 is in use
        "039863"             | "DE"       | "0"             | true
        "039864"             | "DE"       | "0"             | true
        "039865"             | "DE"       | "0"             | true
        "039866"             | "DE"       | "0"             | true
        "039867"             | "DE"       | "0"             | true
        "039868"             | "DE"       | "0"             | true
        "039869"             | "DE"       | "0"             | true
        // 03987 is Templin
        "039880"             | "DE"       | "0"             | true
        // 039881 till 039889 is in use
        "03989"              | "DE"       | "0"             | true
        "03990"              | "DE"       | "0"             | true
        // 03991 is Waren Müritz
        "039920"             | "DE"       | "0"             | true
        // 039921 till 039929 is in use
        "039930"             | "DE"       | "0"             | true
        // 039931 till 039934 is in use
        "039935"             | "DE"       | "0"             | true
        "039936"             | "DE"       | "0"             | true
        "039937"             | "DE"       | "0"             | true
        "039938"             | "DE"       | "0"             | true
        "039939"             | "DE"       | "0"             | true
        // 03994 is Malchin
        "039950"             | "DE"       | "0"             | true
        // 039951 till 039957 is in use
        "039958"             | "DE"       | "0"             | true
        // 039959 is Dargun
        // 03996 is Teterow
        "039970"             | "DE"       | "0"             | true
        // 039971 till 039973 is in use
        "039974"             | "DE"       | "0"             | true
        // 039975 till 039978
        "039979"             | "DE"       | "0"             | true
        // 03998 is Demmin
        "039990"             | "DE"       | "0"             | true
        // 039991 till 039999 is in use
    }

    def "check if original lib fixed RFC3966 for invalid German NDC 040 - 069"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        when: "get number RFC3966: $number"
        String[] results = []
        for (int i = 0; i < numbersToTest.length; i++) {
            String onkz = extractONKZ(numbersToTest[i], regionCode)
            String eResult = number.substring(1)
            if (onkz == null) {
                results += "0"
            } else {
                if (eResult == onkz) {
                    results += "1"
                } else {
                    results += "2"
                }

            }
        }
        

        then: "is number expected: $expectedResult"
        for (int i = 0; i < results.length; i++) {
            this.logResult(results[i], expectedResult, expectingFail[i], numbersToTest[i], regionCode)
        }


        where:

        number               | regionCode | expectedResult  | expectingFail
        // 040 is Hamburg
        "04100"              | "DE"       | "0"             | true
        // 04101 till 04109 is in use
        "0411"               | "DE"       | "0"             | true
        // total 0412x is in use
        "04130"              | "DE"       | "0"             | true
        // 04131 till 04139 is in use
        // 04140 till 04144 is in use
        "04145"              | "DE"       | "0"             | true
        // 04146 is Stade-Bützfleth
        "04147"              | "DE"       | "0"             | true
        // 04148 till 04149 is in use
        "04150"              | "DE"       | "0"             | true
        // 04151 till 04156 is in use
        "04157"              | "DE"       | "0"             | true
        // 04158 till 04159 is in use
        "04160"              | "DE"       | "0"             | true
        // 04161 till 04169 is in use
        "04170"              | "DE"       | "0"             | true
        // 04171 till 04179 is in use
        // total 0418x is in sue
        "04190"              | "DE"       | "0"             | true
        // 04191 till 04195 is in use
        "04196"              | "DE"       | "0"             | true
        "04197"              | "DE"       | "0"             | true
        "04198"              | "DE"       | "0"             | true
        "04199"              | "DE"       | "0"             | true
        "04200"              | "DE"       | "0"             | true
        "04201"              | "DE"       | "0"             | true
        // 04202 till 04209 is in use
        // 0421 is Bremen
        "04220"              | "DE"       | "0"             | true
        // 04221 till 04224 is in use
        "04225"              | "DE"       | "0"             | true
        "04226"              | "DE"       | "0"             | true
        "04227"              | "DE"       | "0"             | true
        "04228"              | "DE"       | "0"             | true
        "04229"              | "DE"       | "0"             | true
        // 0423x till 0424x is in use
        "04250"              | "DE"       | "0"             | true
        // 04251 till 04258 is in use
        "04259"              | "DE"       | "0"             | true
        // total 0426x is in use
        "04270"              | "DE"       | "0"             | true
        // 04271 till 04277 is in use
        "04278"              | "DE"       | "0"             | true
        "04279"              | "DE"       | "0"             | true
        "04280"              | "DE"       | "0"             | true
        // 04281 till 04289 is in use
        "04290"              | "DE"       | "0"             | true
        "04291"              | "DE"       | "0"             | true
        // 04292 till 04298 is in use
        "04299"              | "DE"       | "0"             | true
        "04300"              | "DE"       | "0"             | true
        "04301"              | "DE"       | "0"             | true
        // 04302 till 04303 is in use
        "04304"              | "DE"       | "0"             | true
        // 04305 is Westensee
        "04306"              | "DE"       | "0"             | true
        // 04307 till 04308 is in use
        "04309"              | "DE"       | "0"             | true
        // 0431 till 0433x (including total 0432x) is in use
        // 04340 is Achterwehr
        "04341"              | "DE"       | "0"             | true
        // 04342 till 04346 is in use
        "04350"              | "DE"       | "0"             | true
        // 04351 till 04358 is in use
        "04359"              | "DE"       | "0"             | true
        "04360"              | "DE"       | "0"             | true
        // 04361 till 04367 is in use
        "04368"              | "DE"       | "0"             | true
        "04369"              | "DE"       | "0"             | true
        "04370"              | "DE"       | "0"             | true
        // 04371 till 04372 is in use
        "04373"              | "DE"       | "0"             | true
        "04374"              | "DE"       | "0"             | true
        "04375"              | "DE"       | "0"             | true
        "04376"              | "DE"       | "0"             | true
        "04377"              | "DE"       | "0"             | true
        "04378"              | "DE"       | "0"             | true
        "04379"              | "DE"       | "0"             | true
        "04380"              | "DE"       | "0"             | true
        // 04381 till 04385 is in use
        "04386"              | "DE"       | "0"             | true
        "04387"              | "DE"       | "0"             | true
        "04388"              | "DE"       | "0"             | true
        "04389"              | "DE"       | "0"             | true
        "04390"              | "DE"       | "0"             | true
        "04391"              | "DE"       | "0"             | true
        // 04392 till 04394 is in use
        "04395"              | "DE"       | "0"             | true
        "04396"              | "DE"       | "0"             | true
        "04397"              | "DE"       | "0"             | true
        "04398"              | "DE"       | "0"             | true
        "04399"              | "DE"       | "0"             | true
        "04400"              | "DE"       | "0"             | true
        // 04401 till 04409 is in use
        // 0441 is Oldenburg (Oldb)
        "04420"              | "DE"       | "0"             | true
        // 04421 till 04423 is in use
        "04424"              | "DE"       | "0"             | true
        // 04425 till 04426 is in use
        "04427"              | "DE"       | "0"             | true
        "04428"              | "DE"       | "0"             | true
        "04429"              | "DE"       | "0"             | true
        "04430"              | "DE"       | "0"             | true
        // 04431 till 04435 is in use
        "04436"              | "DE"       | "0"             | true
        "04437"              | "DE"       | "0"             | true
        "04438"              | "DE"       | "0"             | true
        "04439"              | "DE"       | "0"             | true
        "04440"              | "DE"       | "0"             | true
        // 04441 till 04447 is in use
        "04448"              | "DE"       | "0"             | true
        "04449"              | "DE"       | "0"             | true
        "04450"              | "DE"       | "0"             | true
        // 04451 till 04456 is in use
        "04457"              | "DE"       | "0"             | true
        // 04458 is Wiefeldstede-Spohle
        "04459"              | "DE"       | "0"             | true
        "04460"              | "DE"       | "0"             | true
        // 04461 till 04469 is in use
        "04470"              | "DE"       | "0"             | true
        // 04471 till 04475 is in use
        "04476"              | "DE"       | "0"             | true
        // 04477 till 04479 is in use
        // total 0448x is in use
        "04490"              | "DE"       | "0"             | true
        // 04491 till 1199 is in use
        "04500"              | "DE"       | "0"             | true
        // 04501 till 04506 is in use
        "04507"              | "DE"       | "0"             | true
        // 04508 till 0459 is in use
        // 0451 is Lübeck
        "04520"              | "DE"       | "0"             | true
        // 04521 till 04529 is in use
        "04530"              | "DE"       | "0"             | true
        // 04531 till 04537 is in use
        "04538"              | "DE"       | "0"             | true
        // 04539 is Westerau
        "04540"              | "DE"       | "0"             | true
        // 04541 till 04547 is in use
        "04548"              | "DE"       | "0"             | true
        "04549"              | "DE"       | "0"             | true
        // total 0455x is in use
        "04560"              | "DE"       | "0"             | true
        // 04561 till 04564 is in use
        "0457"               | "DE"       | "0"             | true
        "0458"               | "DE"       | "0"             | true
        "0459"               | "DE"       | "0"             | true
        "04600"              | "DE"       | "0"             | true
        "04601"              | "DE"       | "0"             | true
        // 04602 till 04609 is in use
        // 0461 is Flensburg
        "04620"              | "DE"       | "0"             | true
        // 04621 till 04627 is in use
        "04628"              | "DE"       | "0"             | true
        "04629"              | "DE"       | "0"             | true
        // total 0463x is in use
        "04640"              | "DE"       | "0"             | true
        // 04641 till 04644 is in use
        "04645"              | "DE"       | "0"             | true
        // 04646 is Morkirch
        "04647"              | "DE"       | "0"             | true
        "04648"              | "DE"       | "0"             | true
        "04649"              | "DE"       | "0"             | true
        "04650"              | "DE"       | "0"             | true
        // 04651 is Sylt
        "04652"              | "DE"       | "0"             | true
        "04653"              | "DE"       | "0"             | true
        "04654"              | "DE"       | "0"             | true
        "04655"              | "DE"       | "0"             | true
        "04656"              | "DE"       | "0"             | true
        "04657"              | "DE"       | "0"             | true
        "04658"              | "DE"       | "0"             | true
        "04659"              | "DE"       | "0"             | true
        "04660"              | "DE"       | "0"             | true
        // 04661 till 04668 is in use
        "04669"              | "DE"       | "0"             | true
        "04670"              | "DE"       | "0"             | true
        // 04671 till 04674 is in use
        "04675"              | "DE"       | "0"             | true
        "04676"              | "DE"       | "0"             | true
        "04677"              | "DE"       | "0"             | true
        "04678"              | "DE"       | "0"             | true
        "04679"              | "DE"       | "0"             | true
        "04680"              | "DE"       | "0"             | true
        // 04681 till 04684 is in use
        "04685"              | "DE"       | "0"             | true
        "04686"              | "DE"       | "0"             | true
        "04687"              | "DE"       | "0"             | true
        "04688"              | "DE"       | "0"             | true
        "04689"              | "DE"       | "0"             | true
        "04700"              | "DE"       | "0"             | true
        "04701"              | "DE"       | "0"             | true
        // 04702 till 04708 is in use
        "04709"              | "DE"       | "0"             | true
        // 0471 is Bremerhaven
        "04720"              | "DE"       | "0"             | true
        // 04721 till 04725 is in use
        "04726"              | "DE"       | "0"             | true
        "04727"              | "DE"       | "0"             | true
        "04728"              | "DE"       | "0"             | true
        "04729"              | "DE"       | "0"             | true
        "04730"              | "DE"       | "0"             | true
        // 04731 till 04737 is in use
        "04738"              | "DE"       | "0"             | true
        "04739"              | "DE"       | "0"             | true
        // total 0474x is in use
        "04750"              | "DE"       | "0"             | true
        // 04751 till 04758 is in use
        "04759"              | "DE"       | "0"             | true
        "04760"              | "DE"       | "0"             | true
        // 04761 till 04769 is in use
        // total 0477x is in use
        "0478"               | "DE"       | "0"             | true
        "04790"              | "DE"       | "0"             | true
        // 04791 till 04796 is in use
        "04800"              | "DE"       | "0"             | true
        "04801"              | "DE"       | "0"             | true
        // 04802 till 04806 is in use
        "04807"              | "DE"       | "0"             | true
        "04808"              | "DE"       | "0"             | true
        "04809"              | "DE"       | "0"             | true
        // 0481 is Heide Holstein
        "04820"              | "DE"       | "0"             | true
        // 04821 till 04829 is in use
        // 04830 is Süderhastedt
        "04831"              | "DE"       | "0"             | true
        // 04832 till 04839 is in use
        "04840"              | "DE"       | "0"             | true
        // 04841 till 04849 os in use
        "04850"              | "DE"       | "0"             | true
        // 04851 till 04859 is in use
        "04860"              | "DE"       | "0"             | true
        // 04861 till 04865 is in use
        "04866"              | "DE"       | "0"             | true
        "04867"              | "DE"       | "0"             | true
        "04868"              | "DE"       | "0"             | true
        "04869"              | "DE"       | "0"             | true
        "04870"              | "DE"       | "0"             | true
        // 04871 till 04877 is in use
        "04878"              | "DE"       | "0"             | true
        "04879"              | "DE"       | "0"             | true
        "04880"              | "DE"       | "0"             | true
        // 04881 till 04885 is in use
        "04886"              | "DE"       | "0"             | true
        "04887"              | "DE"       | "0"             | true
        "04888"              | "DE"       | "0"             | true
        "04889"              | "DE"       | "0"             | true
        "04890"              | "DE"       | "0"             | true
        "04891"              | "DE"       | "0"             | true
        // 04892 till 04893 is in use
        "04894"              | "DE"       | "0"             | true
        "04895"              | "DE"       | "0"             | true
        "04896"              | "DE"       | "0"             | true
        "04897"              | "DE"       | "0"             | true
        "04898"              | "DE"       | "0"             | true
        "04899"              | "DE"       | "0"             | true
        "04900"              | "DE"       | "0"             | true
        "04901"              | "DE"       | "0"             | true
        // 04902 till 04903 is in use
        "04904"              | "DE"       | "0"             | true
        "04905"              | "DE"       | "0"             | true
        "04906"              | "DE"       | "0"             | true
        "04907"              | "DE"       | "0"             | true
        "04908"              | "DE"       | "0"             | true
        "04909"              | "DE"       | "0"             | true
        // 0491 is Leer Ostfriesland
        // total 0492x is in use
        "04930"              | "DE"       | "0"             | true
        // 04931 till 04936 is in use
        "04937"              | "DE"       | "0"             | true
        // 04938 till 04939 is in use
        "04940"              | "DE"       | "0"             | true
        // 04941 till 04948 is in use
        "04949"              | "DE"       | "0"             | true
        // total 0495x is in use
        "04960"              | "DE"       | "0"             | true
        // 04961 till 04968 is in use
        "04969"              | "DE"       | "0"             | true
        "04970"              | "DE"       | "0"             | true
        // 04971 till 04977 is in use
        "04978"              | "DE"       | "0"             | true
        "04979"              | "DE"       | "0"             | true
        "0498"               | "DE"       | "0"             | true
        "0499"               | "DE"       | "0"             | true
        "0500"               | "DE"       | "0"             | true
        "0501"               | "DE"       | "0"             | true
        "05020"              | "DE"       | "0"             | true
        // 05021 till 05028 is in use
        "05029"              | "DE"       | "0"             | true
        "05030"              | "DE"       | "0"             | true
        // 05031 till 05037 is in use
        "05038"              | "DE"       | "0"             | true
        "05039"              | "DE"       | "0"             | true
        "05040"              | "DE"       | "0"             | true
        // 05041 till 05045 is in use
        "05046"              | "DE"       | "0"             | true
        "05047"              | "DE"       | "0"             | true
        "05048"              | "DE"       | "0"             | true
        "05049"              | "DE"       | "0"             | true
        "05050"              | "DE"       | "0"             | true
        // 05051 till 05056 is in use
        "05057"              | "DE"       | "0"             | true
        "05058"              | "DE"       | "0"             | true
        "05058"              | "DE"       | "0"             | true
        // 05060 is Bodenburg
        "05061"              | "DE"       | "0"             | true
        // 05062 till 05069 is in use
        "05070"              | "DE"       | "0"             | true
        // 05071 till 05074 is in use
        "05075"              | "DE"       | "0"             | true
        "05076"              | "DE"       | "0"             | true
        "05077"              | "DE"       | "0"             | true
        "05078"              | "DE"       | "0"             | true
        "05079"              | "DE"       | "0"             | true
        "05080"              | "DE"       | "0"             | true
        "05081"              | "DE"       | "0"             | true
        // 05082 till 05086 is in use
        "05087"              | "DE"       | "0"             | true
        "05088"              | "DE"       | "0"             | true
        "05089"              | "DE"       | "0"             | true
        "0509"               | "DE"       | "0"             | true
        "05100"              | "DE"       | "0"             | true
        // 05101 till 05103 is in use
        "05104"              | "DE"       | "0"             | true
        // 05105 is Barsinghausen
        "05106"              | "DE"       | "0"             | true
        "05107"              | "DE"       | "0"             | true
        // 05108 till 05109 is in use
        // 0511 is Hannover
        "05120"              | "DE"       | "0"             | true
        // 05121 is Hildesheim
        "05122"              | "DE"       | "0"             | true
        // 05123 is Schellerten
        "05124"              | "DE"       | "0"             | true
        "05125"              | "DE"       | "0"             | true
        // 05126 till 05129 is in use
        // 05130 till 05132 is in use
        "05133"              | "DE"       | "0"             | true
        "05134"              | "DE"       | "0"             | true
        // 05135 till 05139 is in use
        "05140"              | "DE"       | "0"             | true
        // 05141 till 05149 is in use
        "05150"              | "DE"       | "0"             | true
        // 05151 till 05159 is in use
        "05160"              | "DE"       | "0"             | true
        // 05161 till 05168 is in use
        "05169"              | "DE"       | "0"             | true
        "05170"              | "DE"       | "0"             | true
        // 05171 till 05177 is in use
        "05178"              | "DE"       | "0"             | true
        "05179"              | "DE"       | "0"             | true
        "05180"              | "DE"       | "0"             | true
        // 05181 till 05187 is in use
        "05188"              | "DE"       | "0"             | true
        "05189"              | "DE"       | "0"             | true
        // total 0519x is in use
        "05200"              | "DE"       | "0"             | true
        // 05201 till 05209 is in use
        // 0521 is Bielefeld
        "05220"              | "DE"       | "0"             | true
        // 05221 till 05226 is in use
        "05227"              | "DE"       | "0"             | true
        // 05228 is Vlotho-Exter
        "05229"              | "DE"       | "0"             | true
        "05230"              | "DE"       | "0"             | true
        // 05231 till 05238 is in use
        "05239"              | "DE"       | "0"             | true
        "05240"              | "DE"       | "0"             | true
        // 05241 till 0522 is in use
        "05243"              | "DE"       | "0"             | true
        // 05244 till 05248 is in use
        "05249"              | "DE"       | "0"             | true
        // 05250 till 05255 is in use
        "05256"              | "DE"       | "0"             | true
        // 05257 till 05259 is in use
        "05260"              | "DE"       | "0"             | true
        // 05261 till 05266 is in use
        "05267"              | "DE"       | "0"             | true
        "05268"              | "DE"       | "0"             | true
        "05269"              | "DE"       | "0"             | true
        "05270"              | "DE"       | "0"             | true
        // 05271 till 05278 is in use
        "05279"              | "DE"       | "0"             | true
        "05280"              | "DE"       | "0"             | true
        // 05281 till 05286 is in use
        "05287"              | "DE"       | "0"             | true
        "05288"              | "DE"       | "0"             | true
        "05289"              | "DE"       | "0"             | true
        "05290"              | "DE"       | "0"             | true
        "05291"              | "DE"       | "0"             | true
        // 05292 till 05295 is in use
        "05296"              | "DE"       | "0"             | true
        "05297"              | "DE"       | "0"             | true
        "05298"              | "DE"       | "0"             | true
        "05299"              | "DE"       | "0"             | true
        // total 0530x is in use
        // 0531 is Braunschweig
        // total 0532x is in use
        "05330"              | "DE"       | "0"             | true
        // 05331 till 05337 is in use
        "05338"              | "DE"       | "0"             | true
        // 05339 is Gielde
        "05340"              | "DE"       | "0"             | true
        // 05341 is Salzgitter
        "05342"              | "DE"       | "0"             | true
        "05343"              | "DE"       | "0"             | true
        // 05344 till 05347 is in use
        "05348"              | "DE"       | "0"             | true
        "05349"              | "DE"       | "0"             | true
        "05350"              | "DE"       | "0"             | true
        // 05351 till 05358 is in use
        "05359"              | "DE"       | "0"             | true
        "05360"              | "DE"       | "0"             | true
        // 05361 till 05368 is in use
        "05369"              | "DE"       | "0"             | true
        "05370"              | "DE"       | "0"             | true
        // 05371 till 05379 is in use
        "05380"              | "DE"       | "0"             | true
        // 05381 till 05384 is in use
        "05385"              | "DE"       | "0"             | true
        "05386"              | "DE"       | "0"             | true
        "05387"              | "DE"       | "0"             | true
        "05388"              | "DE"       | "0"             | true
        "05389"              | "DE"       | "0"             | true
        "0539"               | "DE"       | "0"             | true
        "05400"              | "DE"       | "0"             | true
        // 05401 till 05407 is in use
        "05408"              | "DE"       | "0"             | true
        // 05409 is Hilter am Teutoburger Wald
        // 0541 Osnabrück
        "05420"              | "DE"       | "0"             | true
        // 05421 till 05429 is in use
        "05430"              | "DE"       | "0"             | true
        // 05431 till 05439 is in use
        "05440"              | "DE"       | "0"             | true
        // 05441 till 05448 is in use
        "05449"              | "DE"       | "0"             | true
        "05450"              | "DE"       | "0"             | true
        // 05451 till 05459 is in use
        "05460"              | "DE"       | "0"             | true
        // 05461 till 05462 is in use
        "05463"              | "DE"       | "0"             | true
        // 05464 till 05468 is in use
        "05469"              | "DE"       | "0"             | true
        "05470"              | "DE"       | "0"             | true
        // 05471 till 05476 is in use
        "05477"              | "DE"       | "0"             | true
        "05478"              | "DE"       | "0"             | true
        "05479"              | "DE"       | "0"             | true
        "05480"              | "DE"       | "0"             | true
        // 05481 till 05485 is in use
        "05486"              | "DE"       | "0"             | true
        "05487"              | "DE"       | "0"             | true
        "05488"              | "DE"       | "0"             | true
        "05489"              | "DE"       | "0"             | true
        "05490"              | "DE"       | "0"             | true
        // 05491 till 05495 is in use
        "05496"              | "DE"       | "0"             | true
        "05497"              | "DE"       | "0"             | true
        "05498"              | "DE"       | "0"             | true
        "05499"              | "DE"       | "0"             | true
        "05500"              | "DE"       | "0"             | true
        "05501"              | "DE"       | "0"             | true
        // 05502 till 05509 is in use
        // 0551 is Göttingen
        // 05520 till 05525 is in use
        "05526"              | "DE"       | "0"             | true
        // 05527 till 05529 is in use
        "05530"              | "DE"       | "0"             | true
        // 05531 till 05536 is in use
        "05537"              | "DE"       | "0"             | true
        "05538"              | "DE"       | "0"             | true
        "05539"              | "DE"       | "0"             | true
        "05540"              | "DE"       | "0"             | true
        // 05541 till 05546 is in use
        "05547"              | "DE"       | "0"             | true
        "05548"              | "DE"       | "0"             | true
        "05549"              | "DE"       | "0"             | true
        "05550"              | "DE"       | "0"             | true
        // 05551 till 05556 is in use
        "05557"              | "DE"       | "0"             | true
        "05558"              | "DE"       | "0"             | true
        "05559"              | "DE"       | "0"             | true
        "05560"              | "DE"       | "0"             | true
        // 05561 till 05565 is in use
        "05566"              | "DE"       | "0"             | true
        "05567"              | "DE"       | "0"             | true
        "05568"              | "DE"       | "0"             | true
        "05569"              | "DE"       | "0"             | true
        "05570"              | "DE"       | "0"             | true
        // 05571 till 05574 is in use
        "05575"              | "DE"       | "0"             | true
        "05576"              | "DE"       | "0"             | true
        "05577"              | "DE"       | "0"             | true
        "05578"              | "DE"       | "0"             | true
        "05579"              | "DE"       | "0"             | true
        "05580"              | "DE"       | "0"             | true
        "05581"              | "DE"       | "0"             | true
        // 05582 till 05586 is in use
        "05587"              | "DE"       | "0"             | true
        "05588"              | "DE"       | "0"             | true
        "05589"              | "DE"       | "0"             | true
        "05590"              | "DE"       | "0"             | true
        "05591"              | "DE"       | "0"             | true
        // 05592 till 05594 is in use
        "05595"              | "DE"       | "0"             | true
        "05596"              | "DE"       | "0"             | true
        "05597"              | "DE"       | "0"             | true
        "05598"              | "DE"       | "0"             | true
        "05599"              | "DE"       | "0"             | true
        "05600"              | "DE"       | "0"             | true
        // 05601 till 05609 is in use
        // 0561 is Kassel
        "05620"              | "DE"       | "0"             | true
        // 05621 till 05626 is in use
        "05627"              | "DE"       | "0"             | true
        "05628"              | "DE"       | "0"             | true
        "05629"              | "DE"       | "0"             | true
        "05630"              | "DE"       | "0"             | true
        // 05631 till 05636 is in use
        "05637"              | "DE"       | "0"             | true
        "05638"              | "DE"       | "0"             | true
        "05639"              | "DE"       | "0"             | true
        "05640"              | "DE"       | "0"             | true
        // 05641 till 05648 is in use
        "05649"              | "DE"       | "0"             | true
        // total 0565x is in use
        "05660"              | "DE"       | "0"             | true
        // 05661 till 05665 is in use
        "05666"              | "DE"       | "0"             | true
        "05667"              | "DE"       | "0"             | true
        "05668"              | "DE"       | "0"             | true
        "05669"              | "DE"       | "0"             | true
        "05670"              | "DE"       | "0"             | true
        // 05671 till 05677 is in use
        "05678"              | "DE"       | "0"             | true
        "05679"              | "DE"       | "0"             | true
        "05680"              | "DE"       | "0"             | true
        // 05681 till 05686
        "05687"              | "DE"       | "0"             | true
        "05688"              | "DE"       | "0"             | true
        "05689"              | "DE"       | "0"             | true
        "05690"              | "DE"       | "0"             | true
        // 05691 till 05696 is in use
        "05697"              | "DE"       | "0"             | true
        "05698"              | "DE"       | "0"             | true
        "05699"              | "DE"       | "0"             | true
        "05700"              | "DE"       | "0"             | true
        "05701"              | "DE"       | "0"             | true
        // 05702 till 05707 is in use
        "05708"              | "DE"       | "0"             | true
        "05709"              | "DE"       | "0"             | true
        "05700"              | "DE"       | "0"             | true
        // 0571 is Minden Westfalen
        "05720"              | "DE"       | "0"             | true
        // 05721 till 05726 is in use
        "05727"              | "DE"       | "0"             | true
        "05728"              | "DE"       | "0"             | true
        "05729"              | "DE"       | "0"             | true
        "05730"              | "DE"       | "0"             | true
        // 05731 till 05734 is in use
        "05735"              | "DE"       | "0"             | true
        "05736"              | "DE"       | "0"             | true
        "05737"              | "DE"       | "0"             | true
        "05738"              | "DE"       | "0"             | true
        "05739"              | "DE"       | "0"             | true
        "05740"              | "DE"       | "0"             | true
        // 05741 till 05746 is in use
        "05747"              | "DE"       | "0"             | true
        "05748"              | "DE"       | "0"             | true
        "05749"              | "DE"       | "0"             | true
        "05750"              | "DE"       | "0"             | true
        // 05751 till 05755 is in use
        "05756"              | "DE"       | "0"             | true
        "05757"              | "DE"       | "0"             | true
        "05758"              | "DE"       | "0"             | true
        "05759"              | "DE"       | "0"             | true
        "05760"              | "DE"       | "0"             | true
        // 05761 is Stolzenau
        "05762"              | "DE"       | "0"             | true
        // 05763 till 05769 is in use
        "05770"              | "DE"       | "0"             | true
        // 05771 till 05777 is in use
        "05778"              | "DE"       | "0"             | true
        "05779"              | "DE"       | "0"             | true
        "0578"               | "DE"       | "0"             | true
        "0579"               | "DE"       | "0"             | true
        "05800"              | "DE"       | "0"             | true
        "05801"              | "DE"       | "0"             | true
        // 05802 till 05808 is in use
        "05809"              | "DE"       | "0"             | true
        // 0581 is Uelzen
        // total 0582x is in use
        "05830"              | "DE"       | "0"             | true
        // 05831 till 05839 is in use
        // 05840 till 05846 is in use
        "05847"              | "DE"       | "0"             | true
        // 05848 till 05849 is in use
        // 05850 till 05855 is in use
        "05856"              | "DE"       | "0"             | true
        // 05857 till 05859 is in use
        "05860"              | "DE"       | "0"             | true
        // 05861 till 05865 is in use
        "05866"              | "DE"       | "0"             | true
        "05867"              | "DE"       | "0"             | true
        "05868"              | "DE"       | "0"             | true
        "05869"              | "DE"       | "0"             | true
        "05870"              | "DE"       | "0"             | true
        "05871"              | "DE"       | "0"             | true
        // 5872 till 5875 is in use
        "05876"              | "DE"       | "0"             | true
        "05877"              | "DE"       | "0"             | true
        "05878"              | "DE"       | "0"             | true
        "05879"              | "DE"       | "0"             | true
        "05880"              | "DE"       | "0"             | true
        "05881"              | "DE"       | "0"             | true
        // 05882 till 05883 is in use
        "05884"              | "DE"       | "0"             | true
        "05885"              | "DE"       | "0"             | true
        "05886"              | "DE"       | "0"             | true
        "05887"              | "DE"       | "0"             | true
        "05888"              | "DE"       | "0"             | true
        "05889"              | "DE"       | "0"             | true
        "0589"               | "DE"       | "0"             | true
        "05900"              | "DE"       | "0"             | true
        // 05901 till 05909 is in use
        // 0591 is Lingen (ems)
        "05920"              | "DE"       | "0"             | true
        // 05921 till 05926 is in use
        "05927"              | "DE"       | "0"             | true
        "05928"              | "DE"       | "0"             | true
        "05929"              | "DE"       | "0"             | true
        "05930"              | "DE"       | "0"             | true
        // 05931 till 05937 is in use
        "05938"              | "DE"       | "0"             | true
        // 05939 is Sustrum
        "05940"              | "DE"       | "0"             | true
        // 05941 till 05948 is in use
        "05949"              | "DE"       | "0"             | true
        "05950"              | "DE"       | "0"             | true
        // 05951 till 05957 is in use
        "05958"              | "DE"       | "0"             | true
        "05959"              | "DE"       | "0"             | true
        "05960"              | "DE"       | "0"             | true
        // 05961 till 05966 is in use
        "05967"              | "DE"       | "0"             | true
        "05968"              | "DE"       | "0"             | true
        "05969"              | "DE"       | "0"             | true
        "05970"              | "DE"       | "0"             | true
        // 05971 is Rheine
        "05972"              | "DE"       | "0"             | true
        // 05973 is Neuenkirchen Kreis Steinfurt
        "05974"              | "DE"       | "0"             | true
        // 05975 till 05978 is in use
        "05979"              | "DE"       | "0"             | true
        "0598"               | "DE"       | "0"             | true
        "0599"               | "DE"       | "0"             | true
        "06000"              | "DE"       | "0"             | true
        "06001"              | "DE"       | "0"             | true
        // 06002 till 06004 is in use
        "06005"              | "DE"       | "0"             | true
        "06006"              | "DE"       | "0"             | true
        // 06007 till 06008 is in use
        "06009"              | "DE"       | "0"             | true
        "0601"               | "DE"       | "0"             | true
        // 06020 till 06024 is in use
        "06025"              | "DE"       | "0"             | true
        // 06026 till 06029 is in use
        "06030"              | "DE"       | "0"             | true
        // 06031 till 06036 is in use
        "06037"              | "DE"       | "0"             | true
        "06038"              | "DE"       | "0"             | true
        // 06039 is Karben
        "06040"              | "DE"       | "0"             | true
        // 06041 till 06049 is in use
        // total 0605x is in use
        "06060"              | "DE"       | "0"             | true
        // 06061 till 06063 is in use
        "06064"              | "DE"       | "0"             | true
        "06065"              | "DE"       | "0"             | true
        // 06066 is Michelstadt-Vielbrunn
        "06067"              | "DE"       | "0"             | true
        // 06068 is Beerfelden
        "06070"              | "DE"       | "0"             | true
        // 06071 is Dieburg
        "06072"              | "DE"       | "0"             | true
        // 06073 till 06074 is in use
        "06075"              | "DE"       | "0"             | true
        "06076"              | "DE"       | "0"             | true
        "06077"              | "DE"       | "0"             | true
        // 06078 is Gross-Umstadt
        "06079"              | "DE"       | "0"             | true
        "06080"              | "DE"       | "0"             | true
        // 06081 till 06087 is in use
        "06088"              | "DE"       | "0"             | true
        "06089"              | "DE"       | "0"             | true
        "06090"              | "DE"       | "0"             | true
        "06091"              | "DE"       | "0"             | true
        // 06092 till 06096 is in use
        "06097"              | "DE"       | "0"             | true
        "06098"              | "DE"       | "0"             | true
        "06099"              | "DE"       | "0"             | true
        "06100"              | "DE"       | "0"             | true
        // 06101 till 06109 is in use
        // 0611 is Wiesbaden
        // 06120 is Aarbergen
        "06121"              | "DE"       | "0"             | true
        // 06122 till 06124 is in use
        "06125"              | "DE"       | "0"             | true
        // 06126 till 06129 is in use
        // 06130 till 06136 is in use
        "06137"              | "DE"       | "0"             | true
        // 06138 till 06139 is in use
        "06140"              | "DE"       | "0"             | true
        "06141"              | "DE"       | "0"             | true
        // 06142 is Rüsselsheim
        "06143"              | "DE"       | "0"             | true
        // 06144 till 06147 is in use
        "06148"              | "DE"       | "0"             | true
        "06149"              | "DE"       | "0"             | true
        // 06150 till 06152 is in use
        "06153"              | "DE"       | "0"             | true
        // 06154 till 06155 is in use
        "06156"              | "DE"       | "0"             | true
        // 06157 till 06159 is in use
        "06160"              | "DE"       | "0"             | true
        // 06161 till 06167 is in use
        "06168"              | "DE"       | "0"             | true
        "06169"              | "DE"       | "0"             | true
        "06170"              | "DE"       | "0"             | true
        // 06171 till 06175 is in use
        "06176"              | "DE"       | "0"             | true
        "06177"              | "DE"       | "0"             | true
        "06178"              | "DE"       | "0"             | true
        "06179"              | "DE"       | "0"             | true
        "06180"              | "DE"       | "0"             | true
        // 06181 till 06188 is in use
        "06189"              | "DE"       | "0"             | true
        // 06190 is Hattersheim am Main
        "06191"              | "DE"       | "0"             | true
        // 06192 is Hofheim am Taunus
        "06193"              | "DE"       | "0"             | true
        "06194"              | "DE"       | "0"             | true
        // 06195 till 06196 is in use
        "06197"              | "DE"       | "0"             | true
        // 06198 is Eppstein
        "06199"              | "DE"       | "0"             | true
        "06200"              | "DE"       | "0"             | true
        // 06201 till 06207 is in use
        "06208"              | "DE"       | "0"             | true
        // 06209 is Mörlenbach
        // 0621 is Mannheim
        // 06220 till 06224 is in use
        "06225"              | "DE"       | "0"             | true
        // 06226 till 06229 is in use
        "06230"              | "DE"       | "0"             | true
        // 06231 till 06239 is in use
        "06240"              | "DE"       | "0"             | true
        // 06241 till 06247 is in use
        "06248"              | "DE"       | "0"             | true
        // 06249 is Guntersblum
        "06250"              | "DE"       | "0"             | true
        // 06251 till 06258 is in use
        "06259"              | "DE"       | "0"             | true
        "06260"              | "DE"       | "0"             | true
        // 06261 till 06269 is in use
        "06270"              | "DE"       | "0"             | true
        // 06271 till 06272 is in use
        "06273"              | "DE"       | "0"             | true
        // 06274 till 06276 is in use
        "06277"              | "DE"       | "0"             | true
        "06278"              | "DE"       | "0"             | true
        "06279"              | "DE"       | "0"             | true
        "06280"              | "DE"       | "0"             | true
        // 06281 till 06287 is in use
        "06288"              | "DE"       | "0"             | true
        "06289"              | "DE"       | "0"             | true
        "06290"              | "DE"       | "0"             | true
        // 06291 till 06298 is in use
        "06299"              | "DE"       | "0"             | true
        "06300"              | "DE"       | "0"             | true
        // 06301 till 06308 is in use
        "06309"              | "DE"       | "0"             | true
        // 0631 is Kauserslautern
        "06320"              | "DE"       | "0"             | true
        // 06321 till 06329 is in use
        "06330"              | "DE"       | "0"             | true
        // 06331 till 06339 is in use
        // total 0634x is in use
        "06350"              | "DE"       | "0"             | true
        // 06351 till 06353 is in use
        "06354"              | "DE"       | "0"             | true
        // 06355 till 06359 is in use
        "06360"              | "DE"       | "0"             | true
        // 06361 till 06364 is in use
        "06365"              | "DE"       | "0"             | true
        "06366"              | "DE"       | "0"             | true
        "06367"              | "DE"       | "0"             | true
        "06368"              | "DE"       | "0"             | true
        "06369"              | "DE"       | "0"             | true
        "06370"              | "DE"       | "0"             | true
        // 06371 till 06375 is in use
        "06376"              | "DE"       | "0"             | true
        "06377"              | "DE"       | "0"             | true
        "06378"              | "DE"       | "0"             | true
        "06379"              | "DE"       | "0"             | true
        "06380"              | "DE"       | "0"             | true
        // 06381 till 06837 is in use
        "06388"              | "DE"       | "0"             | true
        "06389"              | "DE"       | "0"             | true
        "06390"              | "DE"       | "0"             | true
        // 06391 till 06398 is in use
        "06399"              | "DE"       | "0"             | true
        // 0640x till 0642x is in use
        // 06431 till 06436 is in use
        "06437"              | "DE"       | "0"             | true
        // 06438 till 06439 is in use
        // total 0644x is in use
        "06450"              | "DE"       | "0"             | true
        // 06451 till 06458 is in use
        "06459"              | "DE"       | "0"             | true
        "06460"              | "DE"       | "0"             | true
        // 06461 till 06462 is in use
        "06463"              | "DE"       | "0"             | true
        // 06464 till 06468 is in use
        "06469"              | "DE"       | "0"             | true
        "06470"              | "DE"       | "0"             | true
        // 06471 till 06479 is in use
        "06480"              | "DE"       | "0"             | true
        "06481"              | "DE"       | "0"             | true
        // 06482 till 06486 is in use
        "06487"              | "DE"       | "0"             | true
        "06488"              | "DE"       | "0"             | true
        "06489"              | "DE"       | "0"             | true
        "0649"               | "DE"       | "0"             | true
        // 0650x till 0651 is in use
        "06520"              | "DE"       | "0"             | true
        "06521"              | "DE"       | "0"             | true
        // 06522 till 06527 is in use
        "06528"              | "DE"       | "0"             | true
        "06529"              | "DE"       | "0"             | true
        "06530"              | "DE"       | "0"             | true
        // 06531 till 06536 is in use
        "06537"              | "DE"       | "0"             | true
        "06538"              | "DE"       | "0"             | true
        "06539"              | "DE"       | "0"             | true
        "06540"              | "DE"       | "0"             | true
        // 06541 till 06545 is in use
        "06546"              | "DE"       | "0"             | true
        "06547"              | "DE"       | "0"             | true
        "06548"              | "DE"       | "0"             | true
        "06549"              | "DE"       | "0"             | true
        // total 0655x is in use
        "06560"              | "DE"       | "0"             | true
        // 06561 till 06569 is in use
        "06570"              | "DE"       | "0"             | true
        // 06571 till 06575 is in use
        "06576"              | "DE"       | "0"             | true
        "06577"              | "DE"       | "0"             | true
        // 06578 is Salmtal
        "06579"              | "DE"       | "0"             | true
        // total 0658x is in use
        "06590"              | "DE"       | "0"             | true
        // 06591 till 06597 is in use
        "06598"              | "DE"       | "0"             | true
        // 06599 is Wiedenbach bei Gerolstein
        "0660"               | "DE"       | "0"             | true
        // 0661 till 0662x is in use
        // 06630 till 06631 is in use
        "06632"              | "DE"       | "0"             | true
        // 06633 till 06639 is in use
        "06640"              | "DE"       | "0"             | true
        // 06641 till 06648 is in use
        "06649"              | "DE"       | "0"             | true
        // total 0665x is in use
        // 06660 till 06661 is in use
        "06662"              | "DE"       | "0"             | true
        // 06663 till 06669 is in use
        // 06670 is Ludwigsau Hessen
        "06671"              | "DE"       | "0"             | true
        // 06672 till 06678 is in use
        "06679"              | "DE"       | "0"             | true
        "06680"              | "DE"       | "0"             | true
        // 06681 till 06684 is in use
        "06685"              | "DE"       | "0"             | true
        "06686"              | "DE"       | "0"             | true
        "06687"              | "DE"       | "0"             | true
        "06688"              | "DE"       | "0"             | true
        "06689"              | "DE"       | "0"             | true
        "06690"              | "DE"       | "0"             | true
        // 06691 till 06698 is in use
        "06699"              | "DE"       | "0"             | true
        "06700"              | "DE"       | "0"             | true
        // 06701 is Sprendlingen Rheinhessen
        "06702"              | "DE"       | "0"             | true
        // 06703 till 06704 is in use
        "06705"              | "DE"       | "0"             | true
        // 06706 till 06709 is in use
        // 0671 is Bad Kreuznach
        "06720"              | "DE"       | "0"             | true
        // 06721 till 06728 is in use
        "06729"              | "DE"       | "0"             | true
        "06730"              | "DE"       | "0"             | true
        // 06731 till 06737 is in use
        "06738"              | "DE"       | "0"             | true
        "06739"              | "DE"       | "0"             | true
        "06740"              | "DE"       | "0"             | true
        // 06741 till 06747 is in use
        "06748"              | "DE"       | "0"             | true
        "06749"              | "DE"       | "0"             | true
        "06750"              | "DE"       | "0"             | true
        // 06751 till 06758 is in use
        "06759"              | "DE"       | "0"             | true
        "06760"              | "DE"       | "0"             | true
        // 06761 till 06766 is in use
        "06767"              | "DE"       | "0"             | true
        "06768"              | "DE"       | "0"             | true
        "06769"              | "DE"       | "0"             | true
        "06770"              | "DE"       | "0"             | true
        // 06771 till 06776 is in use
        "06777"              | "DE"       | "0"             | true
        "06778"              | "DE"       | "0"             | true
        "06779"              | "DE"       | "0"             | true
        "06780"              | "DE"       | "0"             | true
        // 06781 to 06789 is in use
        "0679"               | "DE"       | "0"             | true
        "06800"              | "DE"       | "0"             | true
        "06801"              | "DE"       | "0"             | true
        // 06802 till 06806 is in use
        "06807"              | "DE"       | "0"             | true
        "06808"              | "DE"       | "0"             | true
        // 06809 is Grossrosseln
        // 0681 is Saarbrücken
        "06820"              | "DE"       | "0"             | true
        // 06821 is Neunkirchen Saar
        "06822"              | "DE"       | "0"             | true
        "06823"              | "DE"       | "0"             | true
        // 06824 till 06827 is in use
        "06828"              | "DE"       | "0"             | true
        "06829"              | "DE"       | "0"             | true
        "06830"              | "DE"       | "0"             | true
        // 06831 till 06838 is in use
        "06839"              | "DE"       | "0"             | true
        "06840"              | "DE"       | "0"             | true
        // 06841 till 06844 is in use
        "06845"              | "DE"       | "0"             | true
        "06846"              | "DE"       | "0"             | true
        "06847"              | "DE"       | "0"             | true
        // 06848 till 06849 is in use
        "06850"              | "DE"       | "0"             | true
        // 06851 till 06858 is in use
        "06859"              | "DE"       | "0"             | true
        "06860"              | "DE"       | "0"             | true
        // 06861 is Merzig
        "06862"              | "DE"       | "0"             | true
        "06863"              | "DE"       | "0"             | true
        // 06864 till 06869 is in use
        "06870"              | "DE"       | "0"             | true
        // 06871 till 06876 is in use
        "06877"              | "DE"       | "0"             | true
        "06878"              | "DE"       | "0"             | true
        "06879"              | "DE"       | "0"             | true
        "06880"              | "DE"       | "0"             | true
        // 06881 is Lebach
        "06882"              | "DE"       | "0"             | true
        "06883"              | "DE"       | "0"             | true
        "06884"              | "DE"       | "0"             | true
        "06885"              | "DE"       | "0"             | true
        "06886"              | "DE"       | "0"             | true
        // 06887 rill 06888 is in use
        "06889"              | "DE"       | "0"             | true
        "06890"              | "DE"       | "0"             | true
        "06891"              | "DE"       | "0"             | true
        "06892"              | "DE"       | "0"             | true
        // 06893 till 06894 is in use
        "06895"              | "DE"       | "0"             | true
        "06896"              | "DE"       | "0"             | true
        // 06897 till 06898 is in use
        "06899"              | "DE"       | "0"             | true
        // 069 is Frankfurt am Mai
    }

    def "check if original lib fixed RFC3966 for invalid German NDC 0700 - 0999"(String number, regionCode, expectedResult, expectingFail) {
        given:

        String[] numbersToTest = [
                                  number + "556",
                                  number + "5566",
                                  number + "55667",
                                  number + "556677",
                                  number + "5566778",
                                  number + "55667788"]

        if (expectingFail == true) {
            expectingFail = [true, true, true, true, true, true, true, true, true]
        }

        if (expectingFail == false) {
            expectingFail = [false, false, false, false, false, false, false, false, false]
        }

        when: "get number RFC3966: $number"
        String[] results = []
        for (int i = 0; i < numbersToTest.length; i++) {
            String onkz = extractONKZ(numbersToTest[i], regionCode)
            String eResult = number.substring(1)
            if (onkz == null) {
                results += "0"
            } else {
                if (eResult == onkz) {
                    results += "1"
                } else {
                    results += "2"
                }

            }
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
        "0701"               | "DE"       | "0"             | true
        "07020"              | "DE"       | "0"             | true
        // 7021 till 7026 is in use
        "07027"              | "DE"       | "0"             | true
        "07028"              | "DE"       | "0"             | true
        "07029"              | "DE"       | "0"             | true
        "07030"              | "DE"       | "0"             | true
        // 07031 till 07034 is in use
        "07035"              | "DE"       | "0"             | true
        "07036"              | "DE"       | "0"             | true
        "07037"              | "DE"       | "0"             | true
        "07038"              | "DE"       | "0"             | true
        "07039"              | "DE"       | "0"             | true
        "07040"              | "DE"       | "0"             | true
        // 07041 till 07046 is in use
        "07047"              | "DE"       | "0"             | true
        "07048"              | "DE"       | "0"             | true
        "07049"              | "DE"       | "0"             | true
        "07050"              | "DE"       | "0"             | true
        // 07051 till 07056 is in use
        "07057"              | "DE"       | "0"             | true
        "07058"              | "DE"       | "0"             | true
        "07059"              | "DE"       | "0"             | true
        "07060"              | "DE"       | "0"             | true
        "07061"              | "DE"       | "0"             | true
        // 07062 till 07063 is in use
        "07064"              | "DE"       | "0"             | true
        "07065"              | "DE"       | "0"             | true
        // 07066 is Bad Rappenau-Bonfeld
        "07067"              | "DE"       | "0"             | true
        "07068"              | "DE"       | "0"             | true
        "07069"              | "DE"       | "0"             | true
        "07070"              | "DE"       | "0"             | true
        // 07071 till 07073 is in use
        "07074"              | "DE"       | "0"             | true
        "07075"              | "DE"       | "0"             | true
        "07076"              | "DE"       | "0"             | true
        "07077"              | "DE"       | "0"             | true
        "07078"              | "DE"       | "0"             | true
        "07079"              | "DE"       | "0"             | true
        "07080"              | "DE"       | "0"             | true
        // 07081 till 07085 is in use
        "07086"              | "DE"       | "0"             | true
        "07087"              | "DE"       | "0"             | true
        "07088"              | "DE"       | "0"             | true
        "07089"              | "DE"       | "0"             | true
        "0709"               | "DE"       | "0"             | true
        "0710"               | "DE"       | "0"             | true
        // 0711 is Stuttgart
        "07120"              | "DE"       | "0"             | true
        // 07121 till 07129 is in use
        // 07130 till 07136 is in use
        "07137"              | "DE"       | "0"             | true
        // 07138 till 07139 is in use
        "07140"              | "DE"       | "0"             | true
        // 07141 till 07148 is in use
        "07149"              | "DE"       | "0"             | true
        "07150"              | "DE"       | "0"             | true
        // 07150 till 07154 is in use
        "07155"              | "DE"       | "0"             | true
        // 07156 till 07159 is in use
        "07160"              | "DE"       | "0"             | true
        // 07161 till 07166 is in use
        "07167"              | "DE"       | "0"             | true
        "07168"              | "DE"       | "0"             | true
        "07169"              | "DE"       | "0"             | true
        "07170"              | "DE"       | "0"             | true
        // 07171 till 07176 is in use
        "07177"              | "DE"       | "0"             | true
        "07178"              | "DE"       | "0"             | true
        "07179"              | "DE"       | "0"             | true
        "07180"              | "DE"       | "0"             | true
        // 07181 till 07184 is in use
        "07185"              | "DE"       | "0"             | true
        "07186"              | "DE"       | "0"             | true
        "07187"              | "DE"       | "0"             | true
        "07188"              | "DE"       | "0"             | true
        "07189"              | "DE"       | "0"             | true
        "07190"              | "DE"       | "0"             | true
        // 07191 till 07195
        "07196"              | "DE"       | "0"             | true
        "07197"              | "DE"       | "0"             | true
        "07198"              | "DE"       | "0"             | true
        "07199"              | "DE"       | "0"             | true
        "07200"              | "DE"       | "0"             | true
        "07201"              | "DE"       | "0"             | true
        // 07202 till 07204 is in use
        "07205"              | "DE"       | "0"             | true
        "07206"              | "DE"       | "0"             | true
        "07207"              | "DE"       | "0"             | true
        "07208"              | "DE"       | "0"             | true
        "07209"              | "DE"       | "0"             | true
        // 0721 is Karlsbad
        // total 0722x is in use
        "07230"              | "DE"       | "0"             | true
        // 07231 till 07237 is in use
        "07238"              | "DE"       | "0"             | true
        "07239"              | "DE"       | "0"             | true
        // 07240 is Pfinztal
        "07241"              | "DE"       | "0"             | true
        // 07242 till 07249 is in use
        // 0725x till 0726x is in use
        "07270"              | "DE"       | "0"             | true
        // 07271 till 07277 is in use
        "07278"              | "DE"       | "0"             | true
        "07279"              | "DE"       | "0"             | true
        "0728"               | "DE"       | "0"             | true
        "0729"               | "DE"       | "0"             | true
        // 07300 is Roggenburg
        "07301"              | "DE"       | "0"             | true
        // 0732 till 0739 is in use
        // 0731 is Ulm Donau
        "07320"              | "DE"       | "0"             | true
        // 07321 till 07329 is in use
        "07330"              | "DE"       | "0"             | true
        // 07331 till 07337 is in use
        "07338"              | "DE"       | "0"             | true
        "07339"              | "DE"       | "0"             | true
        // 07340 is Neenstetten
        "07341"              | "DE"       | "0"             | true
        "07342"              | "DE"       | "0"             | true
        // 07343 till 07348 is in use
        "07349"              | "DE"       | "0"             | true
        "07350"              | "DE"       | "0"             | true
        // 07351 till 07358 is in use
        "07359"              | "DE"       | "0"             | true
        "07360"              | "DE"       | "0"             | true
        // 07361 till 07367 is in use
        "07368"              | "DE"       | "0"             | true
        "07369"              | "DE"       | "0"             | true
        "07370"              | "DE"       | "0"             | true
        // 07371 is Riedlingen Württemberg
        "07372"              | "DE"       | "0"             | true
        // 07373 till 07376 is in use
        "07377"              | "DE"       | "0"             | true
        "07378"              | "DE"       | "0"             | true
        "07379"              | "DE"       | "0"             | true
        "07380"              | "DE"       | "0"             | true
        // 07381 till 07389 is in use
        "07390"              | "DE"       | "0"             | true
        // 07391 till 07395 is in use
        "07396"              | "DE"       | "0"             | true
        "07397"              | "DE"       | "0"             | true
        "07398"              | "DE"       | "0"             | true
        "07399"              | "DE"       | "0"             | true
        "07400"              | "DE"       | "0"             | true
        "07401"              | "DE"       | "0"             | true
        // 07402 till 07404 is in use
        "07405"              | "DE"       | "0"             | true
        "07406"              | "DE"       | "0"             | true
        "07407"              | "DE"       | "0"             | true
        "07408"              | "DE"       | "0"             | true
        "07409"              | "DE"       | "0"             | true
        // 0741 is Deisslingen
        // 07420 is Schramberg
        // 07421 till 07429 is in use
        "07430"              | "DE"       | "0"             | true
        // 07431 till 07436 is in use
        "07437"              | "DE"       | "0"             | true
        "07438"              | "DE"       | "0"             | true
        "07439"              | "DE"       | "0"             | true
        // total 0744x is in use
        "07450"              | "DE"       | "0"             | true
        // 07451 till 07459 is in use
        "07460"              | "DE"       | "0"             | true
        // 07461 till 07467 is in use
        "07468"              | "DE"       | "0"             | true
        "07469"              | "DE"       | "0"             | true
        "07470"              | "DE"       | "0"             | true
        // 07471 till 07478 is in use
        "07479"              | "DE"       | "0"             | true
        "07480"              | "DE"       | "0"             | true
        "07481"              | "DE"       | "0"             | true
        // 07482 till 07486 is in use
        "07487"              | "DE"       | "0"             | true
        "07488"              | "DE"       | "0"             | true
        "07489"              | "DE"       | "0"             | true
        "0749"               | "DE"       | "0"             | true
        "07500"              | "DE"       | "0"             | true
        "07501"              | "DE"       | "0"             | true
        // 07502 till 07506 is in use
        "07507"              | "DE"       | "0"             | true
        "07508"              | "DE"       | "0"             | true
        "07509"              | "DE"       | "0"             | true
        // 0751 Ravensburg
        // 07520 is Bodnegg
        "07521"              | "DE"       | "0"             | true
        // 07522 is Wangen im Allgäu
        "07523"              | "DE"       | "0"             | true
        // 07524 till 07525 is in use
        "07526"              | "DE"       | "0"             | true
        // 07527 till 07529 is in use
        "07530"              | "DE"       | "0"             | true
        // 07531 till 07534 is in use
        "07535"              | "DE"       | "0"             | true
        "07536"              | "DE"       | "0"             | true
        "07537"              | "DE"       | "0"             | true
        "07538"              | "DE"       | "0"             | true
        "07539"              | "DE"       | "0"             | true
        "07540"              | "DE"       | "0"             | true
        // 07541 till 07546 is in use
        "07547"              | "DE"       | "0"             | true
        "07548"              | "DE"       | "0"             | true
        "07549"              | "DE"       | "0"             | true
        "07550"              | "DE"       | "0"             | true
        // 07551 till 07558 is in use
        "07559"              | "DE"       | "0"             | true
        "07560"              | "DE"       | "0"             | true
        // 07561 till 07569 is in use
        // total 0757x is in use
        "07580"              | "DE"       | "0"             | true
        // 07581 till 07587 is in use
        "07588"              | "DE"       | "0"             | true
        "07589"              | "DE"       | "0"             | true
        "0759"               | "DE"       | "0"             | true
        "07600"              | "DE"       | "0"             | true
        "07601"              | "DE"       | "0"             | true
        // 07602 is Oberried Breisgau
        "07603"              | "DE"       | "0"             | true
        "07604"              | "DE"       | "0"             | true
        "07605"              | "DE"       | "0"             | true
        "07606"              | "DE"       | "0"             | true
        "07607"              | "DE"       | "0"             | true
        "07608"              | "DE"       | "0"             | true
        "07609"              | "DE"       | "0"             | true
        // 0761 Freiburg im Breisgau
        // total 0762x is in use
        "07630"              | "DE"       | "0"             | true
        // 07631 till 07636 is in use
        "07637"              | "DE"       | "0"             | true
        "07638"              | "DE"       | "0"             | true
        "07639"              | "DE"       | "0"             | true
        "07640"              | "DE"       | "0"             | true
        // 07641 till 07646
        "07647"              | "DE"       | "0"             | true
        "07648"              | "DE"       | "0"             | true
        "07649"              | "DE"       | "0"             | true
        "07650"              | "DE"       | "0"             | true
        // 07651 till 07657 is in use
        "07658"              | "DE"       | "0"             | true
        "07659"              | "DE"       | "0"             | true
        // total 0766x is in use
        "07670"              | "DE"       | "0"             | true
        // 07671 till 07676 is in use
        "07677"              | "DE"       | "0"             | true
        "07678"              | "DE"       | "0"             | true
        "07679"              | "DE"       | "0"             | true
        "07680"              | "DE"       | "0"             | true
        // 076781 till 07685 is in use
        "07686"              | "DE"       | "0"             | true
        "07687"              | "DE"       | "0"             | true
        "07688"              | "DE"       | "0"             | true
        "07689"              | "DE"       | "0"             | true
        "0769"               | "DE"       | "0"             | true
        "07700"              | "DE"       | "0"             | true
        "07701"              | "DE"       | "0"             | true
        // 07702 till 07709 is in use
        // 0771 is Donaueschingen
        // total 0772x is in use
        "07730"              | "DE"       | "0"             | true
        // 07731 till 07736 is in use
        "07737"              | "DE"       | "0"             | true
        // 07738 till 07339 is in use
        "07740"              | "DE"       | "0"             | true
        // 07741 till 07748 is in use
        "07749"              | "DE"       | "0"             | true
        "07750"              | "DE"       | "0"             | true
        // 07751 is Waldshut
        "07752"              | "DE"       | "0"             | true
        // 07753 till 07755 is in use
        "07756"              | "DE"       | "0"             | true
        "07757"              | "DE"       | "0"             | true
        "07758"              | "DE"       | "0"             | true
        "07759"              | "DE"       | "0"             | true
        "07770"              | "DE"       | "0"             | true
        // 07771 is Stockach
        "07772"              | "DE"       | "0"             | true
        // 07773 till 07775 is in use
        "07776"              | "DE"       | "0"             | true
        // 07777 is Sauldorf
        "07778"              | "DE"       | "0"             | true
        "07779"              | "DE"       | "0"             | true
        "0778"               | "DE"       | "0"             | true
        "0779"               | "DE"       | "0"             | true
        "07800"              | "DE"       | "0"             | true
        "07801"              | "DE"       | "0"             | true
        // 07802 till 07808 is in use
        "07809"              | "DE"       | "0"             | true
        // 0781 is Offenburg
        "07820"              | "DE"       | "0"             | true
        // 07821 till 07826 is in use
        "07827"              | "DE"       | "0"             | true
        "07828"              | "DE"       | "0"             | true
        "07829"              | "DE"       | "0"             | true
        "07830"              | "DE"       | "0"             | true
        // 07831 till 07839 is in use
        "07840"              | "DE"       | "0"             | true
        // 07841 till 07844 is in use
        "07845"              | "DE"       | "0"             | true
        "07846"              | "DE"       | "0"             | true
        "07847"              | "DE"       | "0"             | true
        "07848"              | "DE"       | "0"             | true
        "07849"              | "DE"       | "0"             | true
        "07850"              | "DE"       | "0"             | true
        // 07851 till 07854 is in use
        "07855"              | "DE"       | "0"             | true
        "07856"              | "DE"       | "0"             | true
        "07857"              | "DE"       | "0"             | true
        "07858"              | "DE"       | "0"             | true
        "07859"              | "DE"       | "0"             | true
        "0786"               | "DE"       | "0"             | true
        "0787"               | "DE"       | "0"             | true
        "0788"               | "DE"       | "0"             | true
        "0789"               | "DE"       | "0"             | true
        "07900"              | "DE"       | "0"             | true
        "07901"              | "DE"       | "0"             | true
        "07902"              | "DE"       | "0"             | true
        // 07903 till 07907 is in use
        "07908"              | "DE"       | "0"             | true
        "07909"              | "DE"       | "0"             | true
        // 0791 is Schwäbisch Hall
        "0792"               | "DE"       | "0"             | true
        // total 0793x till 0794x is in use
        // 07950 till 07955 is in use
        "07956"              | "DE"       | "0"             | true
        // 07957 till 07959 is in use
        "07960"              | "DE"       | "0"             | true
        // 07961 till 07967 is in use
        "07968"              | "DE"       | "0"             | true
        "07969"              | "DE"       | "0"             | true
        "07970"              | "DE"       | "0"             | true
        // 07971 till 07977 is in use
        "07978"              | "DE"       | "0"             | true
        "07979"              | "DE"       | "0"             | true
        "0798"               | "DE"       | "0"             | true
        "0799"               | "DE"       | "0"             | true
        // ---
        // 0800 is checked with free call 800 range see above
        // ---
        "0801"               | "DE"       | "0"             | true
        // total 0802x is in use
        "08030"              | "DE"       | "0"             | true
        // 08031 till 08036 is in use
        "08037"              | "DE"       | "0"             | true
        // 08038 till 08039 is in use
        "08040"              | "DE"       | "0"             | true
        // 08041 till 08043 is in use
        "08044"              | "DE"       | "0"             | true
        // 08045 till 08046 is in use
        "08047"              | "DE"       | "0"             | true
        "08048"              | "DE"       | "0"             | true
        "08049"              | "DE"       | "0"             | true
        "08050"              | "DE"       | "0"             | true
        // 08051 till 08057 is in use
        "08058"              | "DE"       | "0"             | true
        "08059"              | "DE"       | "0"             | true
        "08060"              | "DE"       | "0"             | true
        // 08061 till 08067 is in use
        "08068"              | "DE"       | "0"             | true
        "08069"              | "DE"       | "0"             | true
        "08070"              | "DE"       | "0"             | true
        // 08071 till 08076 is in use
        "08077"              | "DE"       | "0"             | true
        "08078"              | "DE"       | "0"             | true
        "08079"              | "DE"       | "0"             | true
        "08080"              | "DE"       | "0"             | true
        // 08081 till 08086 is in use
        "08087"              | "DE"       | "0"             | true
        "08088"              | "DE"       | "0"             | true
        "08089"              | "DE"       | "0"             | true
        "08090"              | "DE"       | "0"             | true
        // 08091 till 08095 is in use
        "08096"              | "DE"       | "0"             | true
        "08097"              | "DE"       | "0"             | true
        "08098"              | "DE"       | "0"             | true
        "08099"              | "DE"       | "0"             | true
        "08100"              | "DE"       | "0"             | true
        "08101"              | "DE"       | "0"             | true
        // 08102 is Höhenkirchen-Siegertsbrunn
        "08103"              | "DE"       | "0"             | true
        // 08104 till 08106 is in use
        "08107"              | "DE"       | "0"             | true
        "08108"              | "DE"       | "0"             | true
        "08109"              | "DE"       | "0"             | true
        // 0811 is Halbergmoos
        "08120"              | "DE"       | "0"             | true
        // 08121 till 08124 is in use
        "08125"              | "DE"       | "0"             | true
        "08126"              | "DE"       | "0"             | true
        "08127"              | "DE"       | "0"             | true
        "08128"              | "DE"       | "0"             | true
        "08129"              | "DE"       | "0"             | true
        "08130"              | "DE"       | "0"             | true
        // 08131 is Dachau
        "08132"              | "DE"       | "0"             | true
        // 08133 till 08139 is in use
        "08140"              | "DE"       | "0"             | true
        // 08141 till 08146 is in use
        "08147"              | "DE"       | "0"             | true
        "08148"              | "DE"       | "0"             | true
        "08149"              | "DE"       | "0"             | true
        "08150"              | "DE"       | "0"             | true
        // 08151 till 08153 is in use
        "08154"              | "DE"       | "0"             | true
        "08155"              | "DE"       | "0"             | true
        "08156"              | "DE"       | "0"             | true
        // 08157 till 08158 is in use
        "08159"              | "DE"       | "0"             | true
        "08160"              | "DE"       | "0"             | true
        // 08161 is Freising
        "08162"              | "DE"       | "0"             | true
        "08163"              | "DE"       | "0"             | true
        "08164"              | "DE"       | "0"             | true
        // 08165 till 08168 is in use
        "08169"              | "DE"       | "0"             | true
        // 08170 till 08171 is in use
        "08172"              | "DE"       | "0"             | true
        "08173"              | "DE"       | "0"             | true
        "08174"              | "DE"       | "0"             | true
        "08175"              | "DE"       | "0"             | true
        // 08176 till 08179 is in use
        "0818"               | "DE"       | "0"             | true
        "08190"              | "DE"       | "0"             | true
        // 08191 till 08196 is in use
        "08197"              | "DE"       | "0"             | true
        "08198"              | "DE"       | "0"             | true
        "08199"              | "DE"       | "0"             | true
        "08200"              | "DE"       | "0"             | true
        "08201"              | "DE"       | "0"             | true
        // 08202 till 08208 is in use
        "08209"              | "DE"       | "0"             | true
        // 0821 is Augsburg
        "08220"              | "DE"       | "0"             | true
        // 08221 till 08226 is in use
        "08227"              | "DE"       | "0"             | true
        "08228"              | "DE"       | "0"             | true
        "08229"              | "DE"       | "0"             | true
        // 08230 till 08234 is in use
        "08235"              | "DE"       | "0"             | true
        // 08236 till 08239 is in use
        "08240"              | "DE"       | "0"             | true
        // 08241 is Buchloe
        "08242"              | "DE"       | "0"             | true
        // 08243 is Fuchstal
        "08244"              | "DE"       | "0"             | true
        // 08245 till 08249 is in use
        // 08250 till 08254 is in use
        "08255"              | "DE"       | "0"             | true
        "08256"              | "DE"       | "0"             | true
        // 08257 till 08259 is in use
        "08260"              | "DE"       | "0"             | true
        // 08261 till 08263 is in use
        "08264"              | "DE"       | "0"             | true
        // 08265 till 08269 is in use
        "08270"              | "DE"       | "0"             | true
        // 08271 till 08274 is in use
        "08275"              | "DE"       | "0"             | true
        // 08276 is Baar Schwaben
        "08277"              | "DE"       | "0"             | true
        "08278"              | "DE"       | "0"             | true
        "08279"              | "DE"       | "0"             | true
        "08280"              | "DE"       | "0"             | true
        // 08281 till 08285 is in use
        "08286"              | "DE"       | "0"             | true
        "08287"              | "DE"       | "0"             | true
        "08288"              | "DE"       | "0"             | true
        "08289"              | "DE"       | "0"             | true
        "08290"              | "DE"       | "0"             | true
        // 08291 till 08296 is in use
        "08297"              | "DE"       | "0"             | true
        "08298"              | "DE"       | "0"             | true
        "08299"              | "DE"       | "0"             | true
        "08300"              | "DE"       | "0"             | true
        "08301"              | "DE"       | "0"             | true
        // 08302 till 08304 is in use
        "08305"              | "DE"       | "0"             | true
        // 08306 is Ronsberg
        "08307"              | "DE"       | "0"             | true
        "08308"              | "DE"       | "0"             | true
        "08309"              | "DE"       | "0"             | true
        // 0831 is Kempten Allgäu
        // 08320 till 08328 is in use
        "08329"              | "DE"       | "0"             | true
        // 08330 till 08338 is in use
        "08339"              | "DE"       | "0"             | true
        // total 0834x is in use
        "0835"               | "DE"       | "0"             | true
        "08360"              | "DE"       | "0"             | true
        // 08361 till 08369 is in use
        // 08370 is Obergünzburg
        "08371"              | "DE"       | "0"             | true
        // 08372 till 08379 is in use
        // total 0838x is in use
        "08390"              | "DE"       | "0"             | true
        "08391"              | "DE"       | "0"             | true
        // 08392 till 08395 is in use
        "08396"              | "DE"       | "0"             | true
        "08397"              | "DE"       | "0"             | true
        "08398"              | "DE"       | "0"             | true
        "08399"              | "DE"       | "0"             | true
        "08400"              | "DE"       | "0"             | true
        "08401"              | "DE"       | "0"             | true
        // 08402 till 08407 is in use
        "08408"              | "DE"       | "0"             | true
        "08409"              | "DE"       | "0"             | true
        // 0841 is Ingolstadt Donau
        "08420"              | "DE"       | "0"             | true
        // 08421 till 08424 is in use
        "08425"              | "DE"       | "0"             | true
        // 08426 till 08427 is in use
        "08428"              | "DE"       | "0"             | true
        "08429"              | "DE"       | "0"             | true
        "08430"              | "DE"       | "0"             | true
        // 08431 till 08435 is in use
        "08436"              | "DE"       | "0"             | true
        "08437"              | "DE"       | "0"             | true
        "08438"              | "DE"       | "0"             | true
        "08439"              | "DE"       | "0"             | true
        "08440"              | "DE"       | "0"             | true
        // 08441 till 08446 is in use
        "08447"              | "DE"       | "0"             | true
        "08448"              | "DE"       | "0"             | true
        "08449"              | "DE"       | "0"             | true
        // 08450 is Ingoldstadt-Zuchering
        "08451"              | "DE"       | "0"             | true
        // 08452 till 08454 is in use
        "08455"              | "DE"       | "0"             | true
        // 08456 till 08459 is in use
        // total 0846x is in use
        "0847"               | "DE"       | "0"             | true
        "0848"               | "DE"       | "0"             | true
        "0849"               | "DE"       | "0"             | true
        "08500"              | "DE"       | "0"             | true
        // 08501 till 08507 is in use
        "08508"              | "DE"       | "0"             | true
        // 08509 is Ruderting
        // 0851 is Passau
        "0852"               | "DE"       | "0"             | true
        "08530"              | "DE"       | "0"             | true
        // 08531 till 08538 is in use
        "08539"              | "DE"       | "0"             | true
        "08540"              | "DE"       | "0"             | true
        // 08541 till 08549 is in use
        // 08550 till 08558 is in use
        "08559"              | "DE"       | "0"             | true
        "08560"              | "DE"       | "0"             | true
        // 08561 till 08565 is in use
        "08566"              | "DE"       | "0"             | true
        "08567"              | "DE"       | "0"             | true
        "08568"              | "DE"       | "0"             | true
        "08569"              | "DE"       | "0"             | true
        "08570"              | "DE"       | "0"             | true
        // 08571 till 08574 is in use
        "08575"              | "DE"       | "0"             | true
        "08576"              | "DE"       | "0"             | true
        "08577"              | "DE"       | "0"             | true
        "08578"              | "DE"       | "0"             | true
        "08579"              | "DE"       | "0"             | true
        "08580"              | "DE"       | "0"             | true
        // 08581 till 08586 is in use
        "08587"              | "DE"       | "0"             | true
        "08588"              | "DE"       | "0"             | true
        "08589"              | "DE"       | "0"             | true
        "08590"              | "DE"       | "0"             | true
        // 08591 till 08593 is in use
        "08594"              | "DE"       | "0"             | true
        "08595"              | "DE"       | "0"             | true
        "08596"              | "DE"       | "0"             | true
        "08597"              | "DE"       | "0"             | true
        "08598"              | "DE"       | "0"             | true
        "08599"              | "DE"       | "0"             | true
        "0860"               | "DE"       | "0"             | true
        // 0861 is Traunstein
        "08620"              | "DE"       | "0"             | true
        // 08621 till 08624 is in use
        "08625"              | "DE"       | "0"             | true
        "08626"              | "DE"       | "0"             | true
        "08627"              | "DE"       | "0"             | true
        // 08628 till 08629 is in use
        // 08630 till 08631 is in use
        "08632"              | "DE"       | "0"             | true
        // 08633 till 08639 is in use
        // 08640 till 08642 is in use
        "08643"              | "DE"       | "0"             | true
        "08644"              | "DE"       | "0"             | true
        "08645"              | "DE"       | "0"             | true
        "08646"              | "DE"       | "0"             | true
        "08647"              | "DE"       | "0"             | true
        "08648"              | "DE"       | "0"             | true
        // 08649 is Schleching
        // 08650 till 08652 is in use
        "08653"              | "DE"       | "0"             | true
        // 08654 Freilassing
        "08655"              | "DE"       | "0"             | true
        // 08656 till 08657 is in use
        "08658"              | "DE"       | "0"             | true
        "08659"              | "DE"       | "0"             | true
        "08660"              | "DE"       | "0"             | true
        // 08661 till 08667 is in use
        "08668"              | "DE"       | "0"             | true
        // 08669 is Traunreut
        // 08670 till 08671 is in use
        "08672"              | "DE"       | "0"             | true
        "08673"              | "DE"       | "0"             | true
        "08674"              | "DE"       | "0"             | true
        "08675"              | "DE"       | "0"             | true
        "08676"              | "DE"       | "0"             | true
        // 08677 till 086779 is in use
        "08680"              | "DE"       | "0"             | true
        // 08681 till 08687 is in use
        "08688"              | "DE"       | "0"             | true
        "08689"              | "DE"       | "0"             | true
        "0869"               | "DE"       | "0"             | true
        "08700"              | "DE"       | "0"             | true
        "08701"              | "DE"       | "0"             | true
        // 08702 till 08709 is in use
        // 0871 is Landshut
        "08720"              | "DE"       | "0"             | true
        // 08721 till 08728 is in use
        "08729"              | "DE"       | "0"             | true
        "08730"              | "DE"       | "0"             | true
        // 08731 till 08735 is in use
        "08736"              | "DE"       | "0"             | true
        "08737"              | "DE"       | "0"             | true
        "08738"              | "DE"       | "0"             | true
        "08739"              | "DE"       | "0"             | true
        "08740"              | "DE"       | "0"             | true
        // 08741 till 08745 is in use
        "08746"              | "DE"       | "0"             | true
        "08747"              | "DE"       | "0"             | true
        "08748"              | "DE"       | "0"             | true
        "08749"              | "DE"       | "0"             | true
        "08750"              | "DE"       | "0"             | true
        // 08751 till 08754 is in use
        "08755"              | "DE"       | "0"             | true
        // 08756 is Nandlstadt
        "08757"              | "DE"       | "0"             | true
        "08758"              | "DE"       | "0"             | true
        "08759"              | "DE"       | "0"             | true
        "08760"              | "DE"       | "0"             | true
        // 08761 till 08762 is in use
        "08763"              | "DE"       | "0"             | true
        // 08764 till 08766 is in use
        "08767"              | "DE"       | "0"             | true
        "08768"              | "DE"       | "0"             | true
        "08769"              | "DE"       | "0"             | true
        "08770"              | "DE"       | "0"             | true
        // 08771 till 08774 is in use
        "08775"              | "DE"       | "0"             | true
        "08776"              | "DE"       | "0"             | true
        "08777"              | "DE"       | "0"             | true
        "08778"              | "DE"       | "0"             | true
        "08779"              | "DE"       | "0"             | true
        "08780"              | "DE"       | "0"             | true
        // 08781 till 08785 is in use
        "08786"              | "DE"       | "0"             | true
        "08787"              | "DE"       | "0"             | true
        "08788"              | "DE"       | "0"             | true
        "08789"              | "DE"       | "0"             | true
        "0879"               | "DE"       | "0"             | true
        "08800"              | "DE"       | "0"             | true
        // 08801 till 08803 is in use
        "08804"              | "DE"       | "0"             | true
        // 08805 till 08809 is in use
        // 0881 is Weilheim in Oberbayern
        "08820"              | "DE"       | "0"             | true
        // 08821 till 08826 is in use
        "08827"              | "DE"       | "0"             | true
        "08828"              | "DE"       | "0"             | true
        "08829"              | "DE"       | "0"             | true
        "0883"               | "DE"       | "0"             | true
        "08840"              | "DE"       | "0"             | true
        // 08841 is Murnau am Staffelsee
        "08842"              | "DE"       | "0"             | true
        "08843"              | "DE"       | "0"             | true
        "08844"              | "DE"       | "0"             | true
        // 08845 till 08847 is in use
        "08848"              | "DE"       | "0"             | true
        "08849"              | "DE"       | "0"             | true
        "08850"              | "DE"       | "0"             | true
        // 08851 is Kochel am See
        "08852"              | "DE"       | "0"             | true
        "08853"              | "DE"       | "0"             | true
        "08854"              | "DE"       | "0"             | true
        "08855"              | "DE"       | "0"             | true
        // 08856 till 08858 is in use
        "08859"              | "DE"       | "0"             | true
        // 08860 till 08862 is in use
        "08863"              | "DE"       | "0"             | true
        "08864"              | "DE"       | "0"             | true
        "08865"              | "DE"       | "0"             | true
        "08866"              | "DE"       | "0"             | true
        // 08867 till 08869 is in use
        "0887"               | "DE"       | "0"             | true
        "0888"               | "DE"       | "0"             | true
        "0889"               | "DE"       | "0"             | true
        // 089 is München
        // ---
        // TODO start: by Dec 1st of 2024 the ranges 9000 till 09008 will be possible for premium service
        "09000"              | "DE"       | "0"             | true
        // 09001 Information Service checked in 0900 range test
        "09002"              | "DE"       | "0"             | true
        // 09003 Entertainment Service checked in 0900 range test
        "09004"              | "DE"       | "0"             | true
        // 09005 other premium services checked in 0900 range test
        "09006"              | "DE"       | "0"             | true
        "09007"              | "DE"       | "0"             | true
        "09008"              | "DE"       | "0"             | true
        // TODO end: by Dec 1st of 2024 the ranges 9000 till 09008 will be possible for premium service
        // ---
        "09009"              | "DE"       | "0"             | true  // see https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/09009/9009_node.html removed block
        "0901"               | "DE"       | "0"             | true
        "0902"               | "DE"       | "0"             | true
        "0903"               | "DE"       | "0"             | true
        "0904"               | "DE"       | "0"             | true
        "0905"               | "DE"       | "0"             | true
        // 0906 is Donauwörth
        // 09070 till 09078 is in use
        "09079"              | "DE"       | "0"             | true
        // total 0908x is in use
        // 09090 till 0904 is in use
        "09095"              | "DE"       | "0"             | true
        "09096"              | "DE"       | "0"             | true
        // 09097 is Marxheim
        "09098"              | "DE"       | "0"             | true
        // 09099 is Kaisheim
        "09100"              | "DE"       | "0"             | true
        // 09101 till 09107 is in use
        "09108"              | "DE"       | "0"             | true
        "09109"              | "DE"       | "0"             | true
        // 0911 is Nürnberg
        // 09120 is Leinburg
        "09121"              | "DE"       | "0"             | true
        // 09122 till 09123 is in use
        "09124"              | "DE"       | "0"             | true
        "09125"              | "DE"       | "0"             | true
        // 09126 till 09129 is in use
        "09130"              | "DE"       | "0"             | true
        // 09131 till 09135 is in use
        "09136"              | "DE"       | "0"             | true
        "09137"              | "DE"       | "0"             | true
        "09138"              | "DE"       | "0"             | true
        "09139"              | "DE"       | "0"             | true
        "09140"              | "DE"       | "0"             | true
        // 09141 till 09149 is in use
        "09150"              | "DE"       | "0"             | true
        // 09151 till 09158 is in use
        "09159"              | "DE"       | "0"             | true
        "09160"              | "DE"       | "0"             | true
        // 09161 till 09167 is in use
        "09168"              | "DE"       | "0"             | true
        "09169"              | "DE"       | "0"             | true
        // 0917x till 0919x is in use
        "09200"              | "DE"       | "0"             | true
        // 09201 till 09209 is in use
        // 0921 is Bayreuth
        // 09220 till 09223 is in use
        "09224"              | "DE"       | "0"             | true
        // 09225 is Stadtsteinach
        "09226"              | "DE"       | "0"             | true
        // 09227 till 09229 is in use
        "09230"              | "DE"       | "0"             | true
        // 09231 till 09236 is in use
        "09237"              | "DE"       | "0"             | true
        // 09238 is Röslau
        "09239"              | "DE"       | "0"             | true
        "09240"              | "DE"       | "0"             | true
        // 09241 till 09246 is in use
        "09247"              | "DE"       | "0"             | true
        "09248"              | "DE"       | "0"             | true
        "09249"              | "DE"       | "0"             | true
        "09250"              | "DE"       | "0"             | true
        // 09251 till 09257 is in use
        "09258"              | "DE"       | "0"             | true
        "09259"              | "DE"       | "0"             | true
        // 0926x till 0928x is in use
        "09290"              | "DE"       | "0"             | true
        "09291"              | "DE"       | "0"             | true
        // 09292 till 09295 is in use
        "09296"              | "DE"       | "0"             | true
        "09297"              | "DE"       | "0"             | true
        "09298"              | "DE"       | "0"             | true
        "09300"              | "DE"       | "0"             | true
        "09301"              | "DE"       | "0"             | true
        // 09302 till 09303 is in use
        "09304"              | "DE"       | "0"             | true
        // 09305 till 09307 is in use
        "09308"              | "DE"       | "0"             | true
        "09309"              | "DE"       | "0"             | true
        // 0931 is Würzburg
        "09320"              | "DE"       | "0"             | true
        // 09321 is Kitzingen
        "09322"              | "DE"       | "0"             | true
        // 09323 till 09326 is in use
        "09327"              | "DE"       | "0"             | true
        "09328"              | "DE"       | "0"             | true
        "09329"              | "DE"       | "0"             | true
        "09330"              | "DE"       | "0"             | true
        // 09331 till 09339 is in use
        // 0934x till 0935x is in use
        // 09360 is Thüngen
        "09361"              | "DE"       | "0"             | true
        "09362"              | "DE"       | "0"             | true
        // 09363 till 09367 is in use
        "09368"              | "DE"       | "0"             | true
        // 09369 is Uettingen
        "09370"              | "DE"       | "0"             | true
        // 09371 till 09378 is in use
        "09379"              | "DE"       | "0"             | true
        "09380"              | "DE"       | "0"             | true
        // 09381 till 09386 is in use
        "09387"              | "DE"       | "0"             | true
        "09388"              | "DE"       | "0"             | true
        "09389"              | "DE"       | "0"             | true
        "09390"              | "DE"       | "0"             | true
        // 09391 till 09398 is in use
        "09399"              | "DE"       | "0"             | true
        "09400"              | "DE"       | "0"             | true
        // 09401 till 09409 is in use
        // 0941 is Regensburg
        // 09420 till 09424 is in use
        "09425"              | "DE"       | "0"             | true
        // 09426 till 09429 is in use
        "09430"              | "DE"       | "0"             | true
        // 09431 is Schwandorf
        "09432"              | "DE"       | "0"             | true
        // 09433 till 09436 is in use
        "09437"              | "DE"       | "0"             | true
        // 09438 till 09439 is in use
        "09440"              | "DE"       | "0"             | true
        // 09441 till 09448 is in use
        "09449"              | "DE"       | "0"             | true
        "09450"              | "DE"       | "0"             | true
        // 09451 till 09454 is in use
        "09455"              | "DE"       | "0"             | true
        "09456"              | "DE"       | "0"             | true
        "09457"              | "DE"       | "0"             | true
        "09458"              | "DE"       | "0"             | true
        "09459"              | "DE"       | "0"             | true
        "09460"              | "DE"       | "0"             | true
        // 09461 till 09649 is in use
        "09470"              | "DE"       | "0"             | true
        // 09471 till 09474 is in use
        "09475"              | "DE"       | "0"             | true
        "09476"              | "DE"       | "0"             | true
        "09477"              | "DE"       | "0"             | true
        "09478"              | "DE"       | "0"             | true
        "09479"              | "DE"       | "0"             | true
        // 09480 till 09482 is in use
        "09483"              | "DE"       | "0"             | true
        // 09484 is Brennberg
        "09485"              | "DE"       | "0"             | true
        "09486"              | "DE"       | "0"             | true
        "09487"              | "DE"       | "0"             | true
        "09488"              | "DE"       | "0"             | true
        "09489"              | "DE"       | "0"             | true
        "09490"              | "DE"       | "0"             | true
        // 09491 till 09493 is in use
        "09494"              | "DE"       | "0"             | true
        // 09495 is Breitenbrunn Oberfalz
        "09496"              | "DE"       | "0"             | true
        // 09497 till 09499 is in use
        "09500"              | "DE"       | "0"             | true
        "09501"              | "DE"       | "0"             | true
        // 09502 till 09505 is in use
        "09506"              | "DE"       | "0"             | true
        "09507"              | "DE"       | "0"             | true
        "09508"              | "DE"       | "0"             | true
        "09509"              | "DE"       | "0"             | true
        // 0951 is Bamberg
        "09520"              | "DE"       | "0"             | true
        // 09521 till 09529 is in use
        "09530"              | "DE"       | "0"             | true
        // 09531 till 09536 is in use
        "09537"              | "DE"       | "0"             | true
        "09538"              | "DE"       | "0"             | true
        "09539"              | "DE"       | "0"             | true
        "09540"              | "DE"       | "0"             | true
        "09541"              | "DE"       | "0"             | true
        // 09542 till 09549 is in use
        "09550"              | "DE"       | "0"             | true
        // 09551 till 09556 is in use
        "09557"              | "DE"       | "0"             | true
        "09558"              | "DE"       | "0"             | true
        "09559"              | "DE"       | "0"             | true
        // total 0956x is in use
        "09570"              | "DE"       | "0"             | true
        // 09571 till 09576 is in use
        "09577"              | "DE"       | "0"             | true
        "09578"              | "DE"       | "0"             | true
        "09579"              | "DE"       | "0"             | true
        "0958"               | "DE"       | "0"             | true
        "0959"               | "DE"       | "0"             | true
        "09600"              | "DE"       | "0"             | true
        "09601"              | "DE"       | "0"             | true
        // 09602 till 09608 is in use
        "09609"              | "DE"       | "0"             | true
        // 0961 is Weiden in der Oberfalz
        "09620"              | "DE"       | "0"             | true
        // 09621 till 09622 is in use
        "09623"              | "DE"       | "0"             | true
        // 09624 till 09628 is in use
        "09629"              | "DE"       | "0"             | true
        "09630"              | "DE"       | "0"             | true
        // 09631 till 09639 is in use
        "09640"              | "DE"       | "0"             | true
        // 09641 till 09648 is in use
        "09649"              | "DE"       | "0"             | true
        "09650"              | "DE"       | "0"             | true
        // 09651 till 09659 is in use
        "09660"              | "DE"       | "0"             | true
        // 09661 till 09666 is in use
        "09667"              | "DE"       | "0"             | true
        "09668"              | "DE"       | "0"             | true
        "09669"              | "DE"       | "0"             | true
        "09670"              | "DE"       | "0"             | true
        // 09671 till 09677 is in use
        "09678"              | "DE"       | "0"             | true
        "09679"              | "DE"       | "0"             | true
        "09680"              | "DE"       | "0"             | true
        // 09681 till 09683 is in use
        "09684"              | "DE"       | "0"             | true
        "09685"              | "DE"       | "0"             | true
        "09686"              | "DE"       | "0"             | true
        "09687"              | "DE"       | "0"             | true
        "09688"              | "DE"       | "0"             | true
        "09689"              | "DE"       | "0"             | true
        "0969"               | "DE"       | "0"             | true
        "09700"              | "DE"       | "0"             | true
        // 09701 is Sandberg Unterfranken
        "09702"              | "DE"       | "0"             | true
        "09703"              | "DE"       | "0"             | true
        // 09704 is Euerdorf
        "09705"              | "DE"       | "0"             | true
        "09706"              | "DE"       | "0"             | true
        "09707"              | "DE"       | "0"             | true
        // 09708 is Bad Bocklet
        // total 0972x is in use
        "09730"              | "DE"       | "0"             | true
        "09731"              | "DE"       | "0"             | true
        // 09732 till 09738 is in use
        "09739"              | "DE"       | "0"             | true
        "09740"              | "DE"       | "0"             | true
        // 09741 till 09742 is in use
        "09743"              | "DE"       | "0"             | true
        // 09744 till 09749 is in use
        "0975"               | "DE"       | "0"             | true
        "09760"              | "DE"       | "0"             | true
        // 09761 till 09766 is in use
        "09767"              | "DE"       | "0"             | true
        "09768"              | "DE"       | "0"             | true
        "09769"              | "DE"       | "0"             | true
        "09770"              | "DE"       | "0"             | true
        // 09771 till 09779 is in use
        "0978"               | "DE"       | "0"             | true
        "0979"               | "DE"       | "0"             | true
        "09800"              | "DE"       | "0"             | true
        "09801"              | "DE"       | "0"             | true
        // 09802 till 09805
        "09806"              | "DE"       | "0"             | true
        "09807"              | "DE"       | "0"             | true
        "09808"              | "DE"       | "0"             | true
        "09809"              | "DE"       | "0"             | true
        // 0981 is Ansbach
        // 09820 is Lehrberg
        "09821"              | "DE"       | "0"             | true
        // 09822 till 09829 is in use
        "09830"              | "DE"       | "0"             | true
        // 09831 till 09837 s in use
        "09838"              | "DE"       | "0"             | true
        "09839"              | "DE"       | "0"             | true
        "09840"              | "DE"       | "0"             | true
        // 09841 till 09848 is in use
        "09849"              | "DE"       | "0"             | true
        "09850"              | "DE"       | "0"             | true
        // 09851 till 09857 is in use
        "09858"              | "DE"       | "0"             | true
        "09859"              | "DE"       | "0"             | true
        "09860"              | "DE"       | "0"             | true
        // 09861 is Rothenburg ob der Tauber
        "09862"              | "DE"       | "0"             | true
        "09863"              | "DE"       | "0"             | true
        "09864"              | "DE"       | "0"             | true
        // 09865 is Adelshofen Mittelfranken
        "09866"              | "DE"       | "0"             | true
        // 09867 till 09869 is in use
        "09870"              | "DE"       | "0"             | true
        // 09871 till 09876 is in use
        "09877"              | "DE"       | "0"             | true
        "09878"              | "DE"       | "0"             | true
        "09879"              | "DE"       | "0"             | true
        "0988"               | "DE"       | "0"             | true
        "0989"               | "DE"       | "0"             | true
        "09900"              | "DE"       | "0"             | true
        // 09901 is Hengersberg Bayern
        "09902"              | "DE"       | "0"             | true
        // 09903 till 09908 is in use
        "09909"              | "DE"       | "0"             | true
        // 0991 is Deggendorf
        // total 0992x is in use
        "09930"              | "DE"       | "0"             | true
        // 09931 till 09933 is in use
        "09934"              | "DE"       | "0"             | true
        // 09935 till 09938 is in use
        "09939"              | "DE"       | "0"             | true
        "09940"              | "DE"       | "0"             | true
        // 09941 till 09948 is in use
        "09949"              | "DE"       | "0"             | true
        "09950"              | "DE"       | "0"             | true
        // 09951 till 09956 is in use
        "09957"              | "DE"       | "0"             | true
        "09958"              | "DE"       | "0"             | true
        "09959"              | "DE"       | "0"             | true
        "09960"              | "DE"       | "0"             | true
        // 09961 till 09966 is in use
        "09967"              | "DE"       | "0"             | true
        "09968"              | "DE"       | "0"             | true
        "09969"              | "DE"       | "0"             | true
        "09970"              | "DE"       | "0"             | true
        // 09971 till 09978 is in use
        "09979"              | "DE"       | "0"             | true
        "0998"               | "DE"       | "0"             | true
        "0999"               | "DE"       | "0"             | true
    }

    def "check if original lib fixed RFC3966 for valid German NDC"(String number, expectingFail) {
        given:
        String numberToTest = "0" + number + "555123"
        expectingFail = false

        when:
        String result = "0"
        String onkz = extractONKZ(numberToTest, "DE")
        if (onkz != null) {
            if (number == onkz) {
                result = "1"
            } else {
                result = "2"
            }
        }

        then:
        if (result != "1") {
            this.logResult(result, number, expectingFail, numberToTest, "DE")
        }


        where:
        // BNetzA 27.07.2022: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vorwahlverzeichnis_ONB.zip.html
        // ITU 14.09.2022: https://www.itu.int/oth/T0202000051/ennumber  | expectingFail
        number | expectingFail
        "201"|  false
        "202"|  false
        "203"|  false
        "2041"| false
        "2043"| false
        "2045"| false
        "2051"| false
        "2052"| false
        "2053"| false
        "2054"| false
        "2056"| false
        "2058"| false
        "2064"| false
        "2065"| false
        "2066"| false
        "208"|  false
        "209"|  false
        "2102"| false
        "2103"| false
        "2104"| false
        "211"|  false
        "212"|  false
        "2129"| false
        "2131"| false
        "2132"| false
        "2133"| false
        "2137"| false
        "214"|  false
        "2150"| false
        "2151"| false
        "2152"| false
        "2153"| false
        "2154"| false
        "2156"| false
        "2157"| false
        "2158"| false
        "2159"| false
        "2161"| false
        "2162"| false
        "2163"| false
        "2164"| false
        "2165"| false
        "2166"| false
        "2171"| false
        "2173"| false
        "2174"| false
        "2175"| false
        "2181"| false
        "2182"| false
        "2183"| false
        "2191"| false
        "2192"| false
        "2193"| false
        "2195"| false
        "2196"| false
        "2202"| false
        "2203"| false
        "2204"| false
        "2205"| false
        "2206"| false
        "2207"| false
        "2208"| false
        "221"|  false
        "2222"| false
        "2223"| false
        "2224"| false
        "2225"| false
        "2226"| false
        "2227"| false
        "2228"| false
        "2232"| false
        "2233"| false
        "2234"| false
        "2235"| false
        "2236"| false
        "2237"| false
        "2238"| false
        "2241"| false
        "2242"| false
        "2243"| false
        "2244"| false
        "2245"| false
        "2246"| false
        "2247"| false
        "2248"| false
        "2251"| false
        "2252"| false
        "2253"| false
        "2254"| false
        "2255"| false
        "2256"| false
        "2257"| false
        "2261"| false
        "2262"| false
        "2263"| false
        "2264"| false
        "2265"| false
        "2266"| false
        "2267"| false
        "2268"| false
        "2269"| false
        "2271"| false
        "2272"| false
        "2273"| false
        "2274"| false
        "2275"| false
        "228"|  false
        "2291"| false
        "2292"| false
        "2293"| false
        "2294"| false
        "2295"| false
        "2296"| false
        "2297"| false
        "2301"| false
        "2302"| false
        "2303"| false
        "2304"| false
        "2305"| false
        "2306"| false
        "2307"| false
        "2308"| false
        "2309"| false
        "231"|  false
        "2323"| false
        "2324"| false
        "2325"| false
        "2327"| false
        "2330"| false
        "2331"| false
        "2332"| false
        "2333"| false
        "2334"| false
        "2335"| false
        "2336"| false
        "2337"| false
        "2338"| false
        "2339"| false
        "234"|  false
        "2351"| false
        "2352"| false
        "2353"| false
        "2354"| false
        "2355"| false
        "2357"| false
        "2358"| false
        "2359"| false
        "2360"| false
        "2361"| false
        "2362"| false
        "2363"| false
        "2364"| false
        "2365"| false
        "2366"| false
        "2367"| false
        "2368"| false
        "2369"| false
        "2371"| false
        "2372"| false
        "2373"| false
        "2374"| false
        "2375"| false
        "2377"| false
        "2378"| false
        "2379"| false
        "2381"| false
        "2382"| false
        "2383"| false
        "2384"| false
        "2385"| false
        "2387"| false
        "2388"| false
        "2389"| false
        "2391"| false
        "2392"| false
        "2393"| false
        "2394"| false
        "2395"| false
        "2401"| false
        "2402"| false
        "2403"| false
        "2404"| false
        "2405"| false
        "2406"| false
        "2407"| false
        "2408"| false
        "2409"| false
        "241"|  false
        "2421"| false
        "2422"| false
        "2423"| false
        "2424"| false
        "2425"| false
        "2426"| false
        "2427"| false
        "2428"| false
        "2429"| false
        "2431"| false
        "2432"| false
        "2433"| false
        "2434"| false
        "2435"| false
        "2436"| false
        "2440"| false
        "2441"| false
        "2443"| false
        "2444"| false
        "2445"| false
        "2446"| false
        "2447"| false
        "2448"| false
        "2449"| false
        "2451"| false
        "2452"| false
        "2453"| false
        "2454"| false
        "2455"| false
        "2456"| false
        "2461"| false
        "2462"| false
        "2463"| false
        "2464"| false
        "2465"| false
        "2471"| false
        "2472"| false
        "2473"| false
        "2474"| false
        "2482"| false
        "2484"| false
        "2485"| false
        "2486"| false
        "2501"| false
        "2502"| false
        "2504"| false
        "2505"| false
        "2506"| false
        "2507"| false
        "2508"| false
        "2509"| false
        "251"|  false
        "2520"| false
        "2521"| false
        "2522"| false
        "2523"| false
        "2524"| false
        "2525"| false
        "2526"| false
        "2527"| false
        "2528"| false
        "2529"| false
        "2532"| false
        "2533"| false
        "2534"| false
        "2535"| false
        "2536"| false
        "2538"| false
        "2541"| false
        "2542"| false
        "2543"| false
        "2545"| false
        "2546"| false
        "2547"| false
        "2548"| false
        "2551"| false
        "2552"| false
        "2553"| false
        "2554"| false
        "2555"| false
        "2556"| false
        "2557"| false
        "2558"| false
        "2561"| false
        "2562"| false
        "2563"| false
        "2564"| false
        "2565"| false
        "2566"| false
        "2567"| false
        "2568"| false
        "2571"| false
        "2572"| false
        "2573"| false
        "2574"| false
        "2575"| false
        "2581"| false
        "2582"| false
        "2583"| false
        "2584"| false
        "2585"| false
        "2586"| false
        "2587"| false
        "2588"| false
        "2590"| false
        "2591"| false
        "2592"| false
        "2593"| false
        "2594"| false
        "2595"| false
        "2596"| false
        "2597"| false
        "2598"| false
        "2599"| false
        "2601"| false
        "2602"| false
        "2603"| false
        "2604"| false
        "2605"| false
        "2606"| false
        "2607"| false
        "2608"| false
        "261"|  false
        "2620"| false
        "2621"| false
        "2622"| false
        "2623"| false
        "2624"| false
        "2625"| false
        "2626"| false
        "2627"| false
        "2628"| false
        "2630"| false
        "2631"| false
        "2632"| false
        "2633"| false
        "2634"| false
        "2635"| false
        "2636"| false
        "2637"| false
        "2638"| false
        "2639"| false
        "2641"| false
        "2642"| false
        "2643"| false
        "2644"| false
        "2645"| false
        "2646"| false
        "2647"| false
        "2651"| false
        "2652"| false
        "2653"| false
        "2654"| false
        "2655"| false
        "2656"| false
        "2657"| false
        "2661"| false
        "2662"| false
        "2663"| false
        "2664"| false
        "2666"| false
        "2667"| false
        "2671"| false
        "2672"| false
        "2673"| false
        "2674"| false
        "2675"| false
        "2676"| false
        "2677"| false
        "2678"| false
        "2680"| false
        "2681"| false
        "2682"| false
        "2683"| false
        "2684"| false
        "2685"| false
        "2686"| false
        "2687"| false
        "2688"| false
        "2689"| false
        "2691"| false
        "2692"| false
        "2693"| false
        "2694"| false
        "2695"| false
        "2696"| false
        "2697"| false
        "271"|  false
        "2721"| false
        "2722"| false
        "2723"| false
        "2724"| false
        "2725"| false
        "2732"| false
        "2733"| false
        "2734"| false
        "2735"| false
        "2736"| false
        "2737"| false
        "2738"| false
        "2739"| false
        "2741"| false
        "2742"| false
        "2743"| false
        "2744"| false
        "2745"| false
        "2747"| false
        "2750"| false
        "2751"| false
        "2752"| false
        "2753"| false
        "2754"| false
        "2755"| false
        "2758"| false
        "2759"| false
        "2761"| false
        "2762"| false
        "2763"| false
        "2764"| false
        "2770"| false
        "2771"| false
        "2772"| false
        "2773"| false
        "2774"| false
        "2775"| false
        "2776"| false
        "2777"| false
        "2778"| false
        "2779"| false
        "2801"| false
        "2802"| false
        "2803"| false
        "2804"| false
        "281"|  false
        "2821"| false
        "2822"| false
        "2823"| false
        "2824"| false
        "2825"| false
        "2826"| false
        "2827"| false
        "2828"| false
        "2831"| false
        "2832"| false
        "2833"| false
        "2834"| false
        "2835"| false
        "2836"| false
        "2837"| false
        "2838"| false
        "2839"| false
        "2841"| false
        "2842"| false
        "2843"| false
        "2844"| false
        "2845"| false
        "2850"| false
        "2851"| false
        "2852"| false
        "2853"| false
        "2855"| false
        "2856"| false
        "2857"| false
        "2858"| false
        "2859"| false
        "2861"| false
        "2862"| false
        "2863"| false
        "2864"| false
        "2865"| false
        "2866"| false
        "2867"| false
        "2871"| false
        "2872"| false
        "2873"| false
        "2874"| false
        "2902"| false
        "2903"| false
        "2904"| false
        "2905"| false
        "291"|  false
        "2921"| false
        "2922"| false
        "2923"| false
        "2924"| false
        "2925"| false
        "2927"| false
        "2928"| false
        "2931"| false
        "2932"| false
        "2933"| false
        "2934"| false
        "2935"| false
        "2937"| false
        "2938"| false
        "2941"| false
        "2942"| false
        "2943"| false
        "2944"| false
        "2945"| false
        "2947"| false
        "2948"| false
        "2951"| false
        "2952"| false
        "2953"| false
        "2954"| false
        "2955"| false
        "2957"| false
        "2958"| false
        "2961"| false
        "2962"| false
        "2963"| false
        "2964"| false
        "2971"| false
        "2972"| false
        "2973"| false
        "2974"| false
        "2975"| false
        "2977"| false
        "2981"| false
        "2982"| false
        "2983"| false
        "2984"| false
        "2985"| false
        "2991"| false
        "2992"| false
        "2993"| false
        "2994"| false
        "30"|  false
        "3301"| false
        "3302"| false
        "3303"| false
        "3304"| false
        "33051"|     false
        "33052"|     false
        "33053"|     false
        "33054"|     false
        "33055"|     false
        "33056"|     false
        "3306"| false
        "3307"| false
        "33080"|     false
        "33082"|     false
        "33083"|     false
        "33084"|     false
        "33085"|     false
        "33086"|     false
        "33087"|     false
        "33088"|     false
        "33089"|     false
        "33093"|     false
        "33094"|     false
        "331"|  false
        "33200"|     false
        "33201"|     false
        "33202"|     false
        "33203"|     false
        "33204"|     false
        "33205"|     false
        "33206"|     false
        "33207"|     false
        "33208"|     false
        "33209"|     false
        "3321"| false
        "3322"| false
        "33230"|     false
        "33231"|     false
        "33232"|     false
        "33233"|     false
        "33234"|     false
        "33235"|     false
        "33237"|     false
        "33238"|     false
        "33239"|     false
        "3327"| false
        "3328"| false
        "3329"| false
        "3331"| false
        "3332"| false
        "33331"|     false
        "33332"|     false
        "33333"|     false
        "33334"|     false
        "33335"|     false
        "33336"|     false
        "33337"|     false
        "33338"|     false
        "3334"| false
        "3335"| false
        "33361"|     false
        "33362"|     false
        "33363"|     false
        "33364"|     false
        "33365"|     false
        "33366"|     false
        "33367"|     false
        "33368"|     false
        "33369"|     false
        "3337"| false
        "3338"| false
        "33393"|     false
        "33394"|     false
        "33395"|     false
        "33396"|     false
        "33397"|     false
        "33398"|     false
        "3341"| false
        "3342"| false
        "33432"|     false
        "33433"|     false
        "33434"|     false
        "33435"|     false
        "33436"|     false
        "33437"|     false
        "33438"|     false
        "33439"|     false
        "3344"| false
        "33451"|     false
        "33452"|     false
        "33454"|     false
        "33456"|     false
        "33457"|     false
        "33458"|     false
        "3346"| false
        "33470"|     false
        "33472"|     false
        "33473"|     false
        "33474"|     false
        "33475"|     false
        "33476"|     false
        "33477"|     false
        "33478"|     false
        "33479"|     false
        "335"|  false
        "33601"|     false
        "33602"|     false
        "33603"|     false
        "33604"|     false
        "33605"|     false
        "33606"|     false
        "33607"|     false
        "33608"|     false
        "33609"|     false
        "3361"| false
        "3362"| false
        "33631"|     false
        "33632"|     false
        "33633"|     false
        "33634"|     false
        "33635"|     false
        "33636"|     false
        "33637"|     false
        "33638"|     false
        "3364"| false
        "33652"|     false
        "33653"|     false
        "33654"|     false
        "33655"|     false
        "33656"|     false
        "33657"|     false
        "3366"| false
        "33671"|     false
        "33672"|     false
        "33673"|     false
        "33674"|     false
        "33675"|     false
        "33676"|     false
        "33677"|     false
        "33678"|     false
        "33679"|     false
        "33701"|     false
        "33702"|     false
        "33703"|     false
        "33704"|     false
        "33708"|     false
        "3371"| false
        "3372"| false
        "33731"|     false
        "33732"|     false
        "33733"|     false
        "33734"|     false
        "33741"|     false
        "33742"|     false
        "33743"|     false
        "33744"|     false
        "33745"|     false
        "33746"|     false
        "33747"|     false
        "33748"|     false
        "3375"| false
        "33760"|     false
        "33762"|     false
        "33763"|     false
        "33764"|     false
        "33765"|     false
        "33766"|     false
        "33767"|     false
        "33768"|     false
        "33769"|     false
        "3377"| false
        "3378"| false
        "3379"| false
        "3381"| false
        "3382"| false
        "33830"|     false
        "33831"|     false
        "33832"|     false
        "33833"|     false
        "33834"|     false
        "33835"|     false
        "33836"|     false
        "33837"|     false
        "33838"|     false
        "33839"|     false
        "33841"|     false
        "33843"|     false
        "33844"|     false
        "33845"|     false
        "33846"|     false
        "33847"|     false
        "33848"|     false
        "33849"|     false
        "3385"| false
        "3386"| false
        "33870"|     false
        "33872"|     false
        "33873"|     false
        "33874"|     false
        "33875"|     false
        "33876"|     false
        "33877"|     false
        "33878"|     false
        "3391"| false
        "33920"|     false
        "33921"|     false
        "33922"|     false
        "33923"|     false
        "33924"|     false
        "33925"|     false
        "33926"|     false
        "33927"|     false
        "33928"|     false
        "33929"|     false
        "33931"|     false
        "33932"|     false
        "33933"|     false
        "3394"| false
        "3395"| false
        "33962"|     false
        "33963"|     false
        "33964"|     false
        "33965"|     false
        "33966"|     false
        "33967"|     false
        "33968"|     false
        "33969"|     false
        "33970"|     false
        "33971"|     false
        "33972"|     false
        "33973"|     false
        "33974"|     false
        "33975"|     false
        "33976"|     false
        "33977"|     false
        "33978"|     false
        "33979"|     false
        "33981"|     false
        "33982"|     false
        "33983"|     false
        "33984"|     false
        "33986"|     false
        "33989"|     false
        "340"|  false
        "341"|  false
        "34202"|     false
        "34203"|     false
        "34204"|     false
        "34205"|     false
        "34206"|     false
        "34207"|     false
        "34208"|     false
        "3421"| false
        "34221"|     false
        "34222"|     false
        "34223"|     false
        "34224"|     false
        "3423"| false
        "34241"|     false
        "34242"|     false
        "34243"|     false
        "34244"|     false
        "3425"| false
        "34261"|     false
        "34262"|     false
        "34263"|     false
        "34291"|     false
        "34292"|     false
        "34293"|     false
        "34294"|     false
        "34295"|     false
        "34296"|     false
        "34297"|     false
        "34298"|     false
        "34299"|     false
        "3431"| false
        "34321"|     false
        "34322"|     false
        "34324"|     false
        "34325"|     false
        "34327"|     false
        "34328"|     false
        "3433"| false
        "34341"|     false
        "34342"|     false
        "34343"|     false
        "34344"|     false
        "34345"|     false
        "34346"|     false
        "34347"|     false
        "34348"|     false
        "3435"| false
        "34361"|     false
        "34362"|     false
        "34363"|     false
        "34364"|     false
        "3437"| false
        "34381"|     false
        "34382"|     false
        "34383"|     false
        "34384"|     false
        "34385"|     false
        "34386"|     false
        "3441"| false
        "34422"|     false
        "34423"|     false
        "34424"|     false
        "34425"|     false
        "34426"|     false
        "3443"| false
        "34441"|     false
        "34443"|     false
        "34444"|     false
        "34445"|     false
        "34446"|     false
        "3445"| false
        "34461"|     false
        "34462"|     false
        "34463"|     false
        "34464"|     false
        "34465"|     false
        "34466"|     false
        "34467"|     false
        "3447"| false
        "3448"| false
        "34491"|     false
        "34492"|     false
        "34493"|     false
        "34494"|     false
        "34495"|     false
        "34496"|     false
        "34497"|     false
        "34498"|     false
        "345"|  false
        "34600"|     false
        "34601"|     false
        "34602"|     false
        "34603"|     false
        "34604"|     false
        "34605"|     false
        "34606"|     false
        "34607"|     false
        "34609"|     false
        "3461"| false
        "3462"| false
        "34632"|     false
        "34633"|     false
        "34635"|     false
        "34636"|     false
        "34637"|     false
        "34638"|     false
        "34639"|     false
        "3464"| false
        "34651"|     false
        "34652"|     false
        "34653"|     false
        "34654"|     false
        "34656"|     false
        "34658"|     false
        "34659"|     false
        "3466"| false
        "34671"|     false
        "34672"|     false
        "34673"|     false
        "34691"|     false
        "34692"|     false
        "3471"| false
        "34721"|     false
        "34722"|     false
        "3473"| false
        "34741"|     false
        "34742"|     false
        "34743"|     false
        "34745"|     false
        "34746"|     false
        "3475"| false
        "3476"| false
        "34771"|     false
        "34772"|     false
        "34773"|     false
        "34774"|     false
        "34775"|     false
        "34776"|     false
        "34779"|     false
        "34781"|     false
        "34782"|     false
        "34783"|     false
        "34785"|     false
        "34901"|     false
        "34903"|     false
        "34904"|     false
        "34905"|     false
        "34906"|     false
        "34907"|     false
        "34909"|     false
        "3491"| false
        "34920"|     false
        "34921"|     false
        "34922"|     false
        "34923"|     false
        "34924"|     false
        "34925"|     false
        "34926"|     false
        "34927"|     false
        "34928"|     false
        "34929"|     false
        "3493"| false
        "3494"| false
        "34953"|     false
        "34954"|     false
        "34955"|     false
        "34956"|     false
        "3496"| false
        "34973"|     false
        "34975"|     false
        "34976"|     false
        "34977"|     false
        "34978"|     false
        "34979"|     false
        "3501"| false
        "35020"|     false
        "35021"|     false
        "35022"|     false
        "35023"|     false
        "35024"|     false
        "35025"|     false
        "35026"|     false
        "35027"|     false
        "35028"|     false
        "35032"|     false
        "35033"|     false
        "3504"| false
        "35052"|     false
        "35053"|     false
        "35054"|     false
        "35055"|     false
        "35056"|     false
        "35057"|     false
        "35058"|     false
        "351"|  false
        "35200"|     false
        "35201"|     false
        "35202"|     false
        "35203"|     false
        "35204"|     false
        "35205"|     false
        "35206"|     false
        "35207"|     false
        "35208"|     false
        "35209"|     false
        "3521"| false
        "3522"| false
        "3523"| false
        "35240"|     false
        "35241"|     false
        "35242"|     false
        "35243"|     false
        "35244"|     false
        "35245"|     false
        "35246"|     false
        "35247"|     false
        "35248"|     false
        "35249"|     false
        "3525"| false
        "35263"|     false
        "35264"|     false
        "35265"|     false
        "35266"|     false
        "35267"|     false
        "35268"|     false
        "3528"| false
        "3529"| false
        "3531"| false
        "35322"|     false
        "35323"|     false
        "35324"|     false
        "35325"|     false
        "35326"|     false
        "35327"|     false
        "35329"|     false
        "3533"| false
        "35341"|     false
        "35342"|     false
        "35343"|     false
        "3535"| false
        "35361"|     false
        "35362"|     false
        "35363"|     false
        "35364"|     false
        "35365"|     false
        "3537"| false
        "35383"|     false
        "35384"|     false
        "35385"|     false
        "35386"|     false
        "35387"|     false
        "35388"|     false
        "35389"|     false
        "3541"| false
        "3542"| false
        "35433"|     false
        "35434"|     false
        "35435"|     false
        "35436"|     false
        "35439"|     false
        "3544"| false
        "35451"|     false
        "35452"|     false
        "35453"|     false
        "35454"|     false
        "35455"|     false
        "35456"|     false
        "3546"| false
        "35471"|     false
        "35472"|     false
        "35473"|     false
        "35474"|     false
        "35475"|     false
        "35476"|     false
        "35477"|     false
        "35478"|     false
        "355"|  false
        "35600"|     false
        "35601"|     false
        "35602"|     false
        "35603"|     false
        "35604"|     false
        "35605"|     false
        "35606"|     false
        "35607"|     false
        "35608"|     false
        "35609"|     false
        "3561"| false
        "3562"| false
        "3563"| false
        "3564"| false
        "35691"|     false
        "35692"|     false
        "35693"|     false
        "35694"|     false
        "35695"|     false
        "35696"|     false
        "35697"|     false
        "35698"|     false
        "3571"| false
        "35722"|     false
        "35723"|     false
        "35724"|     false
        "35725"|     false
        "35726"|     false
        "35727"|     false
        "35728"|     false
        "3573"| false
        "3574"| false
        "35751"|     false
        "35752"|     false
        "35753"|     false
        "35754"|     false
        "35755"|     false
        "35756"|     false
        "3576"| false
        "35771"|     false
        "35772"|     false
        "35773"|     false
        "35774"|     false
        "35775"|     false
        "3578"| false
        "35792"|     false
        "35793"|     false
        "35795"|     false
        "35796"|     false
        "35797"|     false
        "3581"| false
        "35820"|     false
        "35822"|     false
        "35823"|     false
        "35825"|     false
        "35826"|     false
        "35827"|     false
        "35828"|     false
        "35829"|     false
        "3583"| false
        "35841"|     false
        "35842"|     false
        "35843"|     false
        "35844"|     false
        "3585"| false
        "3586"| false
        "35872"|     false
        "35873"|     false
        "35874"|     false
        "35875"|     false
        "35876"|     false
        "35877"|     false
        "3588"| false
        "35891"|     false
        "35892"|     false
        "35893"|     false
        "35894"|     false
        "35895"|     false
        "3591"| false
        "3592"| false
        "35930"|     false
        "35931"|     false
        "35932"|     false
        "35933"|     false
        "35934"|     false
        "35935"|     false
        "35936"|     false
        "35937"|     false
        "35938"|     false
        "35939"|     false
        "3594"| false
        "35951"|     false
        "35952"|     false
        "35953"|     false
        "35954"|     false
        "35955"|     false
        "3596"| false
        "35971"|     false
        "35973"|     false
        "35974"|     false
        "35975"|     false
        "3601"| false
        "36020"|     false
        "36021"|     false
        "36022"|     false
        "36023"|     false
        "36024"|     false
        "36025"|     false
        "36026"|     false
        "36027"|     false
        "36028"|     false
        "36029"|     false
        "3603"| false
        "36041"|     false
        "36042"|     false
        "36043"|     false
        "3605"| false
        "3606"| false
        "36071"|     false
        "36072"|     false
        "36074"|     false
        "36075"|     false
        "36076"|     false
        "36077"|     false
        "36081"|     false
        "36082"|     false
        "36083"|     false
        "36084"|     false
        "36085"|     false
        "36087"|     false
        "361"|  false
        "36200"|     false
        "36201"|     false
        "36202"|     false
        "36203"|     false
        "36204"|     false
        "36205"|     false
        "36206"|     false
        "36207"|     false
        "36208"|     false
        "36209"|     false
        "3621"| false
        "3622"| false
        "3623"| false
        "3624"| false
        "36252"|     false
        "36253"|     false
        "36254"|     false
        "36255"|     false
        "36256"|     false
        "36257"|     false
        "36258"|     false
        "36259"|     false
        "3628"| false
        "3629"| false
        "3631"| false
        "3632"| false
        "36330"|     false
        "36331"|     false
        "36332"|     false
        "36333"|     false
        "36334"|     false
        "36335"|     false
        "36336"|     false
        "36337"|     false
        "36338"|     false
        "3634"| false
        "3635"| false
        "3636"| false
        "36370"|     false
        "36371"|     false
        "36372"|     false
        "36373"|     false
        "36374"|     false
        "36375"|     false
        "36376"|     false
        "36377"|     false
        "36378"|     false
        "36379"|     false
        "3641"| false
        "36421"|     false
        "36422"|     false
        "36423"|     false
        "36424"|     false
        "36425"|     false
        "36426"|     false
        "36427"|     false
        "36428"|     false
        "3643"| false
        "3644"| false
        "36450"|     false
        "36451"|     false
        "36452"|     false
        "36453"|     false
        "36454"|     false
        "36458"|     false
        "36459"|     false
        "36461"|     false
        "36462"|     false
        "36463"|     false
        "36464"|     false
        "36465"|     false
        "3647"| false
        "36481"|     false
        "36482"|     false
        "36483"|     false
        "36484"|     false
        "365"|  false
        "36601"|     false
        "36602"|     false
        "36603"|     false
        "36604"|     false
        "36605"|     false
        "36606"|     false
        "36607"|     false
        "36608"|     false
        "3661"| false
        "36621"|     false
        "36622"|     false
        "36623"|     false
        "36624"|     false
        "36625"|     false
        "36626"|     false
        "36628"|     false
        "3663"| false
        "36640"|     false
        "36642"|     false
        "36643"|     false
        "36644"|     false
        "36645"|     false
        "36646"|     false
        "36647"|     false
        "36648"|     false
        "36649"|     false
        "36651"|     false
        "36652"|     false
        "36653"|     false
        "36691"|     false
        "36692"|     false
        "36693"|     false
        "36694"|     false
        "36695"|     false
        "36701"|     false
        "36702"|     false
        "36703"|     false
        "36704"|     false
        "36705"|     false
        "3671"| false
        "3672"| false
        "36730"|     false
        "36731"|     false
        "36732"|     false
        "36733"|     false
        "36734"|     false
        "36735"|     false
        "36736"|     false
        "36737"|     false
        "36738"|     false
        "36739"|     false
        "36741"|     false
        "36742"|     false
        "36743"|     false
        "36744"|     false
        "3675"| false
        "36761"|     false
        "36762"|     false
        "36764"|     false
        "36766"|     false
        "3677"| false
        "36781"|     false
        "36782"|     false
        "36783"|     false
        "36784"|     false
        "36785"|     false
        "3679"| false
        "3681"| false
        "3682"| false
        "3683"| false
        "36840"|     false
        "36841"|     false
        "36842"|     false
        "36843"|     false
        "36844"|     false
        "36845"|     false
        "36846"|     false
        "36847"|     false
        "36848"|     false
        "36849"|     false
        "3685"| false
        "3686"| false
        "36870"|     false
        "36871"|     false
        "36873"|     false
        "36874"|     false
        "36875"|     false
        "36878"|     false
        "3691"| false
        "36920"|     false
        "36921"|     false
        "36922"|     false
        "36923"|     false
        "36924"|     false
        "36925"|     false
        "36926"|     false
        "36927"|     false
        "36928"|     false
        "36929"|     false
        "3693"| false
        "36940"|     false
        "36941"|     false
        "36943"|     false
        "36944"|     false
        "36945"|     false
        "36946"|     false
        "36947"|     false
        "36948"|     false
        "36949"|     false
        "3695"| false
        "36961"|     false
        "36962"|     false
        "36963"|     false
        "36964"|     false
        "36965"|     false
        "36966"|     false
        "36967"|     false
        "36968"|     false
        "36969"|     false
        "371"|  false
        "37200"|     false
        "37202"|     false
        "37203"|     false
        "37204"|     false
        "37206"|     false
        "37207"|     false
        "37208"|     false
        "37209"|     false
        "3721"| false
        "3722"| false
        "3723"| false
        "3724"| false
        "3725"| false
        "3726"| false
        "3727"| false
        "37291"|     false
        "37292"|     false
        "37293"|     false
        "37294"|     false
        "37295"|     false
        "37296"|     false
        "37297"|     false
        "37298"|     false
        "3731"| false
        "37320"|     false
        "37321"|     false
        "37322"|     false
        "37323"|     false
        "37324"|     false
        "37325"|     false
        "37326"|     false
        "37327"|     false
        "37328"|     false
        "37329"|     false
        "3733"| false
        "37341"|     false
        "37342"|     false
        "37343"|     false
        "37344"|     false
        "37346"|     false
        "37347"|     false
        "37348"|     false
        "37349"|     false
        "3735"| false
        "37360"|     false
        "37361"|     false
        "37362"|     false
        "37363"|     false
        "37364"|     false
        "37365"|     false
        "37366"|     false
        "37367"|     false
        "37368"|     false
        "37369"|     false
        "3737"| false
        "37381"|     false
        "37382"|     false
        "37383"|     false
        "37384"|     false
        "3741"| false
        "37421"|     false
        "37422"|     false
        "37423"|     false
        "37430"|     false
        "37431"|     false
        "37432"|     false
        "37433"|     false
        "37434"|     false
        "37435"|     false
        "37436"|     false
        "37437"|     false
        "37438"|     false
        "37439"|     false
        "3744"| false
        "3745"| false
        "37462"|     false
        "37463"|     false
        "37464"|     false
        "37465"|     false
        "37467"|     false
        "37468"|     false
        "375"|  false
        "37600"|     false
        "37601"|     false
        "37602"|     false
        "37603"|     false
        "37604"|     false
        "37605"|     false
        "37606"|     false
        "37607"|     false
        "37608"|     false
        "37609"|     false
        "3761"| false
        "3762"| false
        "3763"| false
        "3764"| false
        "3765"| false
        "3771"| false
        "3772"| false
        "3773"| false
        "3774"| false
        "37752"|     false
        "37754"|     false
        "37755"|     false
        "37756"|     false
        "37757"|     false
        "381"|  false
        "38201"|     false
        "38202"|     false
        "38203"|     false
        "38204"|     false
        "38205"|     false
        "38206"|     false
        "38207"|     false
        "38208"|     false
        "38209"|     false
        "3821"| false
        "38220"|     false
        "38221"|     false
        "38222"|     false
        "38223"|     false
        "38224"|     false
        "38225"|     false
        "38226"|     false
        "38227"|     false
        "38228"|     false
        "38229"|     false
        "38231"|     false
        "38232"|     false
        "38233"|     false
        "38234"|     false
        "38292"|     false
        "38293"|     false
        "38294"|     false
        "38295"|     false
        "38296"|     false
        "38297"|     false
        "38300"|     false
        "38301"|     false
        "38302"|     false
        "38303"|     false
        "38304"|     false
        "38305"|     false
        "38306"|     false
        "38307"|     false
        "38308"|     false
        "38309"|     false
        "3831"| false
        "38320"|     false
        "38321"|     false
        "38322"|     false
        "38323"|     false
        "38324"|     false
        "38325"|     false
        "38326"|     false
        "38327"|     false
        "38328"|     false
        "38331"|     false
        "38332"|     false
        "38333"|     false
        "38334"|     false
        "3834"| false
        "38351"|     false
        "38352"|     false
        "38353"|     false
        "38354"|     false
        "38355"|     false
        "38356"|     false
        "3836"| false
        "38370"|     false
        "38371"|     false
        "38372"|     false
        "38373"|     false
        "38374"|     false
        "38375"|     false
        "38376"|     false
        "38377"|     false
        "38378"|     false
        "38379"|     false
        "3838"| false
        "38391"|     false
        "38392"|     false
        "38393"|     false
        "3841"| false
        "38422"|     false
        "38423"|     false
        "38424"|     false
        "38425"|     false
        "38426"|     false
        "38427"|     false
        "38428"|     false
        "38429"|     false
        "3843"| false
        "3844"| false
        "38450"|     false
        "38451"|     false
        "38452"|     false
        "38453"|     false
        "38454"|     false
        "38455"|     false
        "38456"|     false
        "38457"|     false
        "38458"|     false
        "38459"|     false
        "38461"|     false
        "38462"|     false
        "38464"|     false
        "38466"|     false
        "3847"| false
        "38481"|     false
        "38482"|     false
        "38483"|     false
        "38484"|     false
        "38485"|     false
        "38486"|     false
        "38488"|     false
        "385"|  false
        "3860"| false
        "3861"| false
        "3863"| false
        "3865"| false
        "3866"| false
        "3867"| false
        "3868"| false
        "3869"| false
        "3871"| false
        "38720"|     false
        "38721"|     false
        "38722"|     false
        "38723"|     false
        "38724"|     false
        "38725"|     false
        "38726"|     false
        "38727"|     false
        "38728"|     false
        "38729"|     false
        "38731"|     false
        "38732"|     false
        "38733"|     false
        "38735"|     false
        "38736"|     false
        "38737"|     false
        "38738"|     false
        "3874"| false
        "38750"|     false
        "38751"|     false
        "38752"|     false
        "38753"|     false
        "38754"|     false
        "38755"|     false
        "38756"|     false
        "38757"|     false
        "38758"|     false
        "38759"|     false
        "3876"| false
        "3877"| false
        "38780"|     false
        "38781"|     false
        "38782"|     false
        "38783"|     false
        "38784"|     false
        "38785"|     false
        "38787"|     false
        "38788"|     false
        "38789"|     false
        "38791"|     false
        "38792"|     false
        "38793"|     false
        "38794"|     false
        "38796"|     false
        "38797"|     false
        "3881"| false
        "38821"|     false
        "38822"|     false
        "38823"|     false
        "38824"|     false
        "38825"|     false
        "38826"|     false
        "38827"|     false
        "38828"|     false
        "3883"| false
        "38841"|     false
        "38842"|     false
        "38843"|     false
        "38844"|     false
        "38845"|     false
        "38847"|     false
        "38848"|     false
        "38850"|     false
        "38851"|     false
        "38852"|     false
        "38853"|     false
        "38854"|     false
        "38855"|     false
        "38856"|     false
        "38858"|     false
        "38859"|     false
        "3886"| false
        "38871"|     false
        "38872"|     false
        "38873"|     false
        "38874"|     false
        "38875"|     false
        "38876"|     false
        "39000"|     false
        "39001"|     false
        "39002"|     false
        "39003"|     false
        "39004"|     false
        "39005"|     false
        "39006"|     false
        "39007"|     false
        "39008"|     false
        "39009"|     false
        "3901"| false
        "3902"| false
        "39030"|     false
        "39031"|     false
        "39032"|     false
        "39033"|     false
        "39034"|     false
        "39035"|     false
        "39036"|     false
        "39037"|     false
        "39038"|     false
        "39039"|     false
        "3904"| false
        "39050"|     false
        "39051"|     false
        "39052"|     false
        "39053"|     false
        "39054"|     false
        "39055"|     false
        "39056"|     false
        "39057"|     false
        "39058"|     false
        "39059"|     false
        "39061"|     false
        "39062"|     false
        "3907"| false
        "39080"|     false
        "39081"|     false
        "39082"|     false
        "39083"|     false
        "39084"|     false
        "39085"|     false
        "39086"|     false
        "39087"|     false
        "39088"|     false
        "39089"|     false
        "3909"| false
        "391"|  false
        "39200"|     false
        "39201"|     false
        "39202"|     false
        "39203"|     false
        "39204"|     false
        "39205"|     false
        "39206"|     false
        "39207"|     false
        "39208"|     false
        "39209"|     false
        "3921"| false
        "39221"|     false
        "39222"|     false
        "39223"|     false
        "39224"|     false
        "39225"|     false
        "39226"|     false
        "3923"| false
        "39241"|     false
        "39242"|     false
        "39243"|     false
        "39244"|     false
        "39245"|     false
        "39246"|     false
        "39247"|     false
        "39248"|     false
        "3925"| false
        "39262"|     false
        "39263"|     false
        "39264"|     false
        "39265"|     false
        "39266"|     false
        "39267"|     false
        "39268"|     false
        "3928"| false
        "39291"|     false
        "39292"|     false
        "39293"|     false
        "39294"|     false
        "39295"|     false
        "39296"|     false
        "39297"|     false
        "39298"|     false
        "3931"| false
        "39320"|     false
        "39321"|     false
        "39322"|     false
        "39323"|     false
        "39324"|     false
        "39325"|     false
        "39327"|     false
        "39328"|     false
        "39329"|     false
        "3933"| false
        "39341"|     false
        "39342"|     false
        "39343"|     false
        "39344"|     false
        "39345"|     false
        "39346"|     false
        "39347"|     false
        "39348"|     false
        "39349"|     false
        "3935"| false
        "39361"|     false
        "39362"|     false
        "39363"|     false
        "39364"|     false
        "39365"|     false
        "39366"|     false
        "3937"| false
        "39382"|     false
        "39383"|     false
        "39384"|     false
        "39386"|     false
        "39387"|     false
        "39388"|     false
        "39389"|     false
        "39390"|     false
        "39391"|     false
        "39392"|     false
        "39393"|     false
        "39394"|     false
        "39395"|     false
        "39396"|     false
        "39397"|     false
        "39398"|     false
        "39399"|     false
        "39400"|     false
        "39401"|     false
        "39402"|     false
        "39403"|     false
        "39404"|     false
        "39405"|     false
        "39406"|     false
        "39407"|     false
        "39408"|     false
        "39409"|     false
        "3941"| false
        "39421"|     false
        "39422"|     false
        "39423"|     false
        "39424"|     false
        "39425"|     false
        "39426"|     false
        "39427"|     false
        "39428"|     false
        "3943"| false
        "3944"| false
        "39451"|     false
        "39452"|     false
        "39453"|     false
        "39454"|     false
        "39455"|     false
        "39456"|     false
        "39457"|     false
        "39458"|     false
        "39459"|     false
        "3946"| false
        "3947"| false
        "39481"|     false
        "39482"|     false
        "39483"|     false
        "39484"|     false
        "39485"|     false
        "39487"|     false
        "39488"|     false
        "39489"|     false
        "3949"| false
        "395"|  false
        "39600"|     false
        "39601"|     false
        "39602"|     false
        "39603"|     false
        "39604"|     false
        "39605"|     false
        "39606"|     false
        "39607"|     false
        "39608"|     false
        "3961"| false
        "3962"| false
        "3963"| false
        "3964"| false
        "3965"| false
        "3966"| false
        "3967"| false
        "3968"| false
        "3969"| false
        "3971"| false
        "39721"|     false
        "39722"|     false
        "39723"|     false
        "39724"|     false
        "39726"|     false
        "39727"|     false
        "39728"|     false
        "3973"| false
        "39740"|     false
        "39741"|     false
        "39742"|     false
        "39743"|     false
        "39744"|     false
        "39745"|     false
        "39746"|     false
        "39747"|     false
        "39748"|     false
        "39749"|     false
        "39751"|     false
        "39752"|     false
        "39753"|     false
        "39754"|     false
        "3976"| false
        "39771"|     false
        "39772"|     false
        "39773"|     false
        "39774"|     false
        "39775"|     false
        "39776"|     false
        "39777"|     false
        "39778"|     false
        "39779"|     false
        "3981"| false
        "39820"|     false
        "39821"|     false
        "39822"|     false
        "39823"|     false
        "39824"|     false
        "39825"|     false
        "39826"|     false
        "39827"|     false
        "39828"|     false
        "39829"|     false
        "39831"|     false
        "39832"|     false
        "39833"|     false
        "3984"| false
        "39851"|     false
        "39852"|     false
        "39853"|     false
        "39854"|     false
        "39855"|     false
        "39856"|     false
        "39857"|     false
        "39858"|     false
        "39859"|     false
        "39861"|     false
        "39862"|     false
        "39863"|     false
        "3987"| false
        "39881"|     false
        "39882"|     false
        "39883"|     false
        "39884"|     false
        "39885"|     false
        "39886"|     false
        "39887"|     false
        "39888"|     false
        "39889"|     false
        "3991"| false
        "39921"|     false
        "39922"|     false
        "39923"|     false
        "39924"|     false
        "39925"|     false
        "39926"|     false
        "39927"|     false
        "39928"|     false
        "39929"|     false
        "39931"|     false
        "39932"|     false
        "39933"|     false
        "39934"|     false
        "3994"| false
        "39951"|     false
        "39952"|     false
        "39953"|     false
        "39954"|     false
        "39955"|     false
        "39956"|     false
        "39957"|     false
        "39959"|     false
        "3996"| false
        "39971"|     false
        "39972"|     false
        "39973"|     false
        "39975"|     false
        "39976"|     false
        "39977"|     false
        "39978"|     false
        "3998"| false
        "39991"|     false
        "39992"|     false
        "39993"|     false
        "39994"|     false
        "39995"|     false
        "39996"|     false
        "39997"|     false
        "39998"|     false
        "39999"|     false
        "40"|  false
        "4101"| false
        "4102"| false
        "4103"| false
        "4104"| false
        "4105"| false
        "4106"| false
        "4107"| false
        "4108"| false
        "4109"| false
        "4120"| false
        "4121"| false
        "4122"| false
        "4123"| false
        "4124"| false
        "4125"| false
        "4126"| false
        "4127"| false
        "4128"| false
        "4129"| false
        "4131"| false
        "4132"| false
        "4133"| false
        "4134"| false
        "4135"| false
        "4136"| false
        "4137"| false
        "4138"| false
        "4139"| false
        "4140"| false
        "4141"| false
        "4142"| false
        "4143"| false
        "4144"| false
        "4146"| false
        "4148"| false
        "4149"| false
        "4151"| false
        "4152"| false
        "4153"| false
        "4154"| false
        "4155"| false
        "4156"| false
        "4158"| false
        "4159"| false
        "4161"| false
        "4162"| false
        "4163"| false
        "4164"| false
        "4165"| false
        "4166"| false
        "4167"| false
        "4168"| false
        "4169"| false
        "4171"| false
        "4172"| false
        "4173"| false
        "4174"| false
        "4175"| false
        "4176"| false
        "4177"| false
        "4178"| false
        "4179"| false
        "4180"| false
        "4181"| false
        "4182"| false
        "4183"| false
        "4184"| false
        "4185"| false
        "4186"| false
        "4187"| false
        "4188"| false
        "4189"| false
        "4191"| false
        "4192"| false
        "4193"| false
        "4194"| false
        "4195"| false
        "4202"| false
        "4203"| false
        "4204"| false
        "4205"| false
        "4206"| false
        "4207"| false
        "4208"| false
        "4209"| false
        "421"|  false
        "4221"| false
        "4222"| false
        "4223"| false
        "4224"| false
        "4230"| false
        "4231"| false
        "4232"| false
        "4233"| false
        "4234"| false
        "4235"| false
        "4236"| false
        "4237"| false
        "4238"| false
        "4239"| false
        "4240"| false
        "4241"| false
        "4242"| false
        "4243"| false
        "4244"| false
        "4245"| false
        "4246"| false
        "4247"| false
        "4248"| false
        "4249"| false
        "4251"| false
        "4252"| false
        "4253"| false
        "4254"| false
        "4255"| false
        "4256"| false
        "4257"| false
        "4258"| false
        "4260"| false
        "4261"| false
        "4262"| false
        "4263"| false
        "4264"| false
        "4265"| false
        "4266"| false
        "4267"| false
        "4268"| false
        "4269"| false
        "4271"| false
        "4272"| false
        "4273"| false
        "4274"| false
        "4275"| false
        "4276"| false
        "4277"| false
        "4281"| false
        "4282"| false
        "4283"| false
        "4284"| false
        "4285"| false
        "4286"| false
        "4287"| false
        "4288"| false
        "4289"| false
        "4292"| false
        "4293"| false
        "4294"| false
        "4295"| false
        "4296"| false
        "4297"| false
        "4298"| false
        "4302"| false
        "4303"| false
        "4305"| false
        "4307"| false
        "4308"| false
        "431"|  false
        "4320"| false
        "4321"| false
        "4322"| false
        "4323"| false
        "4324"| false
        "4326"| false
        "4327"| false
        "4328"| false
        "4329"| false
        "4330"| false
        "4331"| false
        "4332"| false
        "4333"| false
        "4334"| false
        "4335"| false
        "4336"| false
        "4337"| false
        "4338"| false
        "4339"| false
        "4340"| false
        "4342"| false
        "4343"| false
        "4344"| false
        "4346"| false
        "4347"| false
        "4348"| false
        "4349"| false
        "4351"| false
        "4352"| false
        "4353"| false
        "4354"| false
        "4355"| false
        "4356"| false
        "4357"| false
        "4358"| false
        "4361"| false
        "4362"| false
        "4363"| false
        "4364"| false
        "4365"| false
        "4366"| false
        "4367"| false
        "4371"| false
        "4372"| false
        "4381"| false
        "4382"| false
        "4383"| false
        "4384"| false
        "4385"| false
        "4392"| false
        "4393"| false
        "4394"| false
        "4401"| false
        "4402"| false
        "4403"| false
        "4404"| false
        "4405"| false
        "4406"| false
        "4407"| false
        "4408"| false
        "4409"| false
        "441"|  false
        "4421"| false
        "4422"| false
        "4423"| false
        "4425"| false
        "4426"| false
        "4431"| false
        "4432"| false
        "4433"| false
        "4434"| false
        "4435"| false
        "4441"| false
        "4442"| false
        "4443"| false
        "4444"| false
        "4445"| false
        "4446"| false
        "4447"| false
        "4451"| false
        "4452"| false
        "4453"| false
        "4454"| false
        "4455"| false
        "4456"| false
        "4458"| false
        "4461"| false
        "4462"| false
        "4463"| false
        "4464"| false
        "4465"| false
        "4466"| false
        "4467"| false
        "4468"| false
        "4469"| false
        "4471"| false
        "4472"| false
        "4473"| false
        "4474"| false
        "4475"| false
        "4477"| false
        "4478"| false
        "4479"| false
        "4480"| false
        "4481"| false
        "4482"| false
        "4483"| false
        "4484"| false
        "4485"| false
        "4486"| false
        "4487"| false
        "4488"| false
        "4489"| false
        "4491"| false
        "4492"| false
        "4493"| false
        "4494"| false
        "4495"| false
        "4496"| false
        "4497"| false
        "4498"| false
        "4499"| false
        "4501"| false
        "4502"| false
        "4503"| false
        "4504"| false
        "4505"| false
        "4506"| false
        "4508"| false
        "4509"| false
        "451"|  false
        "4521"| false
        "4522"| false
        "4523"| false
        "4524"| false
        "4525"| false
        "4526"| false
        "4527"| false
        "4528"| false
        "4529"| false
        "4531"| false
        "4532"| false
        "4533"| false
        "4534"| false
        "4535"| false
        "4536"| false
        "4537"| false
        "4539"| false
        "4541"| false
        "4542"| false
        "4543"| false
        "4544"| false
        "4545"| false
        "4546"| false
        "4547"| false
        "4550"| false
        "4551"| false
        "4552"| false
        "4553"| false
        "4554"| false
        "4555"| false
        "4556"| false
        "4557"| false
        "4558"| false
        "4559"| false
        "4561"| false
        "4562"| false
        "4563"| false
        "4564"| false
        "4602"| false
        "4603"| false
        "4604"| false
        "4605"| false
        "4606"| false
        "4607"| false
        "4608"| false
        "4609"| false
        "461"|  false
        "4621"| false
        "4622"| false
        "4623"| false
        "4624"| false
        "4625"| false
        "4626"| false
        "4627"| false
        "4630"| false
        "4631"| false
        "4632"| false
        "4633"| false
        "4634"| false
        "4635"| false
        "4636"| false
        "4637"| false
        "4638"| false
        "4639"| false
        "4641"| false
        "4642"| false
        "4643"| false
        "4644"| false
        "4646"| false
        "4651"| false
        "4661"| false
        "4662"| false
        "4663"| false
        "4664"| false
        "4665"| false
        "4666"| false
        "4667"| false
        "4668"| false
        "4671"| false
        "4672"| false
        "4673"| false
        "4674"| false
        "4681"| false
        "4682"| false
        "4683"| false
        "4684"| false
        "4702"| false
        "4703"| false
        "4704"| false
        "4705"| false
        "4706"| false
        "4707"| false
        "4708"| false
        "471"|  false
        "4721"| false
        "4722"| false
        "4723"| false
        "4724"| false
        "4725"| false
        "4731"| false
        "4732"| false
        "4733"| false
        "4734"| false
        "4735"| false
        "4736"| false
        "4737"| false
        "4740"| false
        "4741"| false
        "4742"| false
        "4743"| false
        "4744"| false
        "4745"| false
        "4746"| false
        "4747"| false
        "4748"| false
        "4749"| false
        "4751"| false
        "4752"| false
        "4753"| false
        "4754"| false
        "4755"| false
        "4756"| false
        "4757"| false
        "4758"| false
        "4761"| false
        "4762"| false
        "4763"| false
        "4764"| false
        "4765"| false
        "4766"| false
        "4767"| false
        "4768"| false
        "4769"| false
        "4770"| false
        "4771"| false
        "4772"| false
        "4773"| false
        "4774"| false
        "4775"| false
        "4776"| false
        "4777"| false
        "4778"| false
        "4779"| false
        "4791"| false
        "4792"| false
        "4793"| false
        "4794"| false
        "4795"| false
        "4796"| false
        "4802"| false
        "4803"| false
        "4804"| false
        "4805"| false
        "4806"| false
        "481"|  false
        "4821"| false
        "4822"| false
        "4823"| false
        "4824"| false
        "4825"| false
        "4826"| false
        "4827"| false
        "4828"| false
        "4829"| false
        "4830"| false
        "4832"| false
        "4833"| false
        "4834"| false
        "4835"| false
        "4836"| false
        "4837"| false
        "4838"| false
        "4839"| false
        "4841"| false
        "4842"| false
        "4843"| false
        "4844"| false
        "4845"| false
        "4846"| false
        "4847"| false
        "4848"| false
        "4849"| false
        "4851"| false
        "4852"| false
        "4853"| false
        "4854"| false
        "4855"| false
        "4856"| false
        "4857"| false
        "4858"| false
        "4859"| false
        "4861"| false
        "4862"| false
        "4863"| false
        "4864"| false
        "4865"| false
        "4871"| false
        "4872"| false
        "4873"| false
        "4874"| false
        "4875"| false
        "4876"| false
        "4877"| false
        "4881"| false
        "4882"| false
        "4883"| false
        "4884"| false
        "4885"| false
        "4892"| false
        "4893"| false
        "4902"| false
        "4903"| false
        "491"|  false
        "4920"| false
        "4921"| false
        "4922"| false
        "4923"| false
        "4924"| false
        "4925"| false
        "4926"| false
        "4927"| false
        "4928"| false
        "4929"| false
        "4931"| false
        "4932"| false
        "4933"| false
        "4934"| false
        "4935"| false
        "4936"| false
        "4938"| false
        "4939"| false
        "4941"| false
        "4942"| false
        "4943"| false
        "4944"| false
        "4945"| false
        "4946"| false
        "4947"| false
        "4948"| false
        "4950"| false
        "4951"| false
        "4952"| false
        "4953"| false
        "4954"| false
        "4955"| false
        "4956"| false
        "4957"| false
        "4958"| false
        "4959"| false
        "4961"| false
        "4962"| false
        "4963"| false
        "4964"| false
        "4965"| false
        "4966"| false
        "4967"| false
        "4968"| false
        "4971"| false
        "4972"| false
        "4973"| false
        "4974"| false
        "4975"| false
        "4976"| false
        "4977"| false
        "5021"| false
        "5022"| false
        "5023"| false
        "5024"| false
        "5025"| false
        "5026"| false
        "5027"| false
        "5028"| false
        "5031"| false
        "5032"| false
        "5033"| false
        "5034"| false
        "5035"| false
        "5036"| false
        "5037"| false
        "5041"| false
        "5042"| false
        "5043"| false
        "5044"| false
        "5045"| false
        "5051"| false
        "5052"| false
        "5053"| false
        "5054"| false
        "5055"| false
        "5056"| false
        "5060"| false
        "5062"| false
        "5063"| false
        "5064"| false
        "5065"| false
        "5066"| false
        "5067"| false
        "5068"| false
        "5069"| false
        "5071"| false
        "5072"| false
        "5073"| false
        "5074"| false
        "5082"| false
        "5083"| false
        "5084"| false
        "5085"| false
        "5086"| false
        "5101"| false
        "5102"| false
        "5103"| false
        "5105"| false
        "5108"| false
        "5109"| false
        "511"|  false
        "5121"| false
        "5123"| false
        "5126"| false
        "5127"| false
        "5128"| false
        "5129"| false
        "5130"| false
        "5131"| false
        "5132"| false
        "5135"| false
        "5136"| false
        "5137"| false
        "5138"| false
        "5139"| false
        "5141"| false
        "5142"| false
        "5143"| false
        "5144"| false
        "5145"| false
        "5146"| false
        "5147"| false
        "5148"| false
        "5149"| false
        "5151"| false
        "5152"| false
        "5153"| false
        "5154"| false
        "5155"| false
        "5156"| false
        "5157"| false
        "5158"| false
        "5159"| false
        "5161"| false
        "5162"| false
        "5163"| false
        "5164"| false
        "5165"| false
        "5166"| false
        "5167"| false
        "5168"| false
        "5171"| false
        "5172"| false
        "5173"| false
        "5174"| false
        "5175"| false
        "5176"| false
        "5177"| false
        "5181"| false
        "5182"| false
        "5183"| false
        "5184"| false
        "5185"| false
        "5186"| false
        "5187"| false
        "5190"| false
        "5191"| false
        "5192"| false
        "5193"| false
        "5194"| false
        "5195"| false
        "5196"| false
        "5197"| false
        "5198"| false
        "5199"| false
        "5201"| false
        "5202"| false
        "5203"| false
        "5204"| false
        "5205"| false
        "5206"| false
        "5207"| false
        "5208"| false
        "5209"| false
        "521"|  false
        "5221"| false
        "5222"| false
        "5223"| false
        "5224"| false
        "5225"| false
        "5226"| false
        "5228"| false
        "5231"| false
        "5232"| false
        "5233"| false
        "5234"| false
        "5235"| false
        "5236"| false
        "5237"| false
        "5238"| false
        "5241"| false
        "5242"| false
        "5244"| false
        "5245"| false
        "5246"| false
        "5247"| false
        "5248"| false
        "5250"| false
        "5251"| false
        "5252"| false
        "5253"| false
        "5254"| false
        "5255"| false
        "5257"| false
        "5258"| false
        "5259"| false
        "5261"| false
        "5262"| false
        "5263"| false
        "5264"| false
        "5265"| false
        "5266"| false
        "5271"| false
        "5272"| false
        "5273"| false
        "5274"| false
        "5275"| false
        "5276"| false
        "5277"| false
        "5278"| false
        "5281"| false
        "5282"| false
        "5283"| false
        "5284"| false
        "5285"| false
        "5286"| false
        "5292"| false
        "5293"| false
        "5294"| false
        "5295"| false
        "5300"| false
        "5301"| false
        "5302"| false
        "5303"| false
        "5304"| false
        "5305"| false
        "5306"| false
        "5307"| false
        "5308"| false
        "5309"| false
        "531"|  false
        "5320"| false
        "5321"| false
        "5322"| false
        "5323"| false
        "5324"| false
        "5325"| false
        "5326"| false
        "5327"| false
        "5328"| false
        "5329"| false
        "5331"| false
        "5332"| false
        "5333"| false
        "5334"| false
        "5335"| false
        "5336"| false
        "5337"| false
        "5339"| false
        "5341"| false
        "5344"| false
        "5345"| false
        "5346"| false
        "5347"| false
        "5351"| false
        "5352"| false
        "5353"| false
        "5354"| false
        "5355"| false
        "5356"| false
        "5357"| false
        "5358"| false
        "5361"| false
        "5362"| false
        "5363"| false
        "5364"| false
        "5365"| false
        "5366"| false
        "5367"| false
        "5368"| false
        "5371"| false
        "5372"| false
        "5373"| false
        "5374"| false
        "5375"| false
        "5376"| false
        "5377"| false
        "5378"| false
        "5379"| false
        "5381"| false
        "5382"| false
        "5383"| false
        "5384"| false
        "5401"| false
        "5402"| false
        "5403"| false
        "5404"| false
        "5405"| false
        "5406"| false
        "5407"| false
        "5409"| false
        "541"|  false
        "5421"| false
        "5422"| false
        "5423"| false
        "5424"| false
        "5425"| false
        "5426"| false
        "5427"| false
        "5428"| false
        "5429"| false
        "5431"| false
        "5432"| false
        "5433"| false
        "5434"| false
        "5435"| false
        "5436"| false
        "5437"| false
        "5438"| false
        "5439"| false
        "5441"| false
        "5442"| false
        "5443"| false
        "5444"| false
        "5445"| false
        "5446"| false
        "5447"| false
        "5448"| false
        "5451"| false
        "5452"| false
        "5453"| false
        "5454"| false
        "5455"| false
        "5456"| false
        "5457"| false
        "5458"| false
        "5459"| false
        "5461"| false
        "5462"| false
        "5464"| false
        "5465"| false
        "5466"| false
        "5467"| false
        "5468"| false
        "5471"| false
        "5472"| false
        "5473"| false
        "5474"| false
        "5475"| false
        "5476"| false
        "5481"| false
        "5482"| false
        "5483"| false
        "5484"| false
        "5485"| false
        "5491"| false
        "5492"| false
        "5493"| false
        "5494"| false
        "5495"| false
        "5502"| false
        "5503"| false
        "5504"| false
        "5505"| false
        "5506"| false
        "5507"| false
        "5508"| false
        "5509"| false
        "551"|  false
        "5520"| false
        "5521"| false
        "5522"| false
        "5523"| false
        "5524"| false
        "5525"| false
        "5527"| false
        "5528"| false
        "5529"| false
        "5531"| false
        "5532"| false
        "5533"| false
        "5534"| false
        "5535"| false
        "5536"| false
        "5541"| false
        "5542"| false
        "5543"| false
        "5544"| false
        "5545"| false
        "5546"| false
        "5551"| false
        "5552"| false
        "5553"| false
        "5554"| false
        "5555"| false
        "5556"| false
        "5561"| false
        "5562"| false
        "5563"| false
        "5564"| false
        "5565"| false
        "5571"| false
        "5572"| false
        "5573"| false
        "5574"| false
        "5582"| false
        "5583"| false
        "5584"| false
        "5585"| false
        "5586"| false
        "5592"| false
        "5593"| false
        "5594"| false
        "5601"| false
        "5602"| false
        "5603"| false
        "5604"| false
        "5605"| false
        "5606"| false
        "5607"| false
        "5608"| false
        "5609"| false
        "561"|  false
        "5621"| false
        "5622"| false
        "5623"| false
        "5624"| false
        "5625"| false
        "5626"| false
        "5631"| false
        "5632"| false
        "5633"| false
        "5634"| false
        "5635"| false
        "5636"| false
        "5641"| false
        "5642"| false
        "5643"| false
        "5644"| false
        "5645"| false
        "5646"| false
        "5647"| false
        "5648"| false
        "5650"| false
        "5651"| false
        "5652"| false
        "5653"| false
        "5654"| false
        "5655"| false
        "5656"| false
        "5657"| false
        "5658"| false
        "5659"| false
        "5661"| false
        "5662"| false
        "5663"| false
        "5664"| false
        "5665"| false
        "5671"| false
        "5672"| false
        "5673"| false
        "5674"| false
        "5675"| false
        "5676"| false
        "5677"| false
        "5681"| false
        "5682"| false
        "5683"| false
        "5684"| false
        "5685"| false
        "5686"| false
        "5691"| false
        "5692"| false
        "5693"| false
        "5694"| false
        "5695"| false
        "5696"| false
        "5702"| false
        "5703"| false
        "5704"| false
        "5705"| false
        "5706"| false
        "5707"| false
        "571"|  false
        "5721"| false
        "5722"| false
        "5723"| false
        "5724"| false
        "5725"| false
        "5726"| false
        "5731"| false
        "5732"| false
        "5733"| false
        "5734"| false
        "5741"| false
        "5742"| false
        "5743"| false
        "5744"| false
        "5745"| false
        "5746"| false
        "5751"| false
        "5752"| false
        "5753"| false
        "5754"| false
        "5755"| false
        "5761"| false
        "5763"| false
        "5764"| false
        "5765"| false
        "5766"| false
        "5767"| false
        "5768"| false
        "5769"| false
        "5771"| false
        "5772"| false
        "5773"| false
        "5774"| false
        "5775"| false
        "5776"| false
        "5777"| false
        "5802"| false
        "5803"| false
        "5804"| false
        "5805"| false
        "5806"| false
        "5807"| false
        "5808"| false
        "581"|  false
        "5820"| false
        "5821"| false
        "5822"| false
        "5823"| false
        "5824"| false
        "5825"| false
        "5826"| false
        "5827"| false
        "5828"| false
        "5829"| false
        "5831"| false
        "5832"| false
        "5833"| false
        "5834"| false
        "5835"| false
        "5836"| false
        "5837"| false
        "5838"| false
        "5839"| false
        "5840"| false
        "5841"| false
        "5842"| false
        "5843"| false
        "5844"| false
        "5845"| false
        "5846"| false
        "5848"| false
        "5849"| false
        "5850"| false
        "5851"| false
        "5852"| false
        "5853"| false
        "5854"| false
        "5855"| false
        "5857"| false
        "5858"| false
        "5859"| false
        "5861"| false
        "5862"| false
        "5863"| false
        "5864"| false
        "5865"| false
        "5872"| false
        "5873"| false
        "5874"| false
        "5875"| false
        "5882"| false
        "5883"| false
        "5901"| false
        "5902"| false
        "5903"| false
        "5904"| false
        "5905"| false
        "5906"| false
        "5907"| false
        "5908"| false
        "5909"| false
        "591"|  false
        "5921"| false
        "5922"| false
        "5923"| false
        "5924"| false
        "5925"| false
        "5926"| false
        "5931"| false
        "5932"| false
        "5933"| false
        "5934"| false
        "5935"| false
        "5936"| false
        "5937"| false
        "5939"| false
        "5941"| false
        "5942"| false
        "5943"| false
        "5944"| false
        "5945"| false
        "5946"| false
        "5947"| false
        "5948"| false
        "5951"| false
        "5952"| false
        "5953"| false
        "5954"| false
        "5955"| false
        "5956"| false
        "5957"| false
        "5961"| false
        "5962"| false
        "5963"| false
        "5964"| false
        "5965"| false
        "5966"| false
        "5971"| false
        "5973"| false
        "5975"| false
        "5976"| false
        "5977"| false
        "5978"| false
        "6002"| false
        "6003"| false
        "6004"| false
        "6007"| false
        "6008"| false
        "6020"| false
        "6021"| false
        "6022"| false
        "6023"| false
        "6024"| false
        "6026"| false
        "6027"| false
        "6028"| false
        "6029"| false
        "6031"| false
        "6032"| false
        "6033"| false
        "6034"| false
        "6035"| false
        "6036"| false
        "6039"| false
        "6041"| false
        "6042"| false
        "6043"| false
        "6044"| false
        "6045"| false
        "6046"| false
        "6047"| false
        "6048"| false
        "6049"| false
        "6050"| false
        "6051"| false
        "6052"| false
        "6053"| false
        "6054"| false
        "6055"| false
        "6056"| false
        "6057"| false
        "6058"| false
        "6059"| false
        "6061"| false
        "6062"| false
        "6063"| false
        "6066"| false
        "6068"| false
        "6071"| false
        "6073"| false
        "6074"| false
        "6078"| false
        "6081"| false
        "6082"| false
        "6083"| false
        "6084"| false
        "6085"| false
        "6086"| false
        "6087"| false
        "6092"| false
        "6093"| false
        "6094"| false
        "6095"| false
        "6096"| false
        "6101"| false
        "6102"| false
        "6103"| false
        "6104"| false
        "6105"| false
        "6106"| false
        "6107"| false
        "6108"| false
        "6109"| false
        "611"|  false
        "6120"| false
        "6122"| false
        "6123"| false
        "6124"| false
        "6126"| false
        "6127"| false
        "6128"| false
        "6129"| false
        "6130"| false
        "6131"| false
        "6132"| false
        "6133"| false
        "6134"| false
        "6135"| false
        "6136"| false
        "6138"| false
        "6139"| false
        "6142"| false
        "6144"| false
        "6145"| false
        "6146"| false
        "6147"| false
        "6150"| false
        "6151"| false
        "6152"| false
        "6154"| false
        "6155"| false
        "6157"| false
        "6158"| false
        "6159"| false
        "6161"| false
        "6162"| false
        "6163"| false
        "6164"| false
        "6165"| false
        "6166"| false
        "6167"| false
        "6171"| false
        "6172"| false
        "6173"| false
        "6174"| false
        "6175"| false
        "6181"| false
        "6182"| false
        "6183"| false
        "6184"| false
        "6185"| false
        "6186"| false
        "6187"| false
        "6188"| false
        "6190"| false
        "6192"| false
        "6195"| false
        "6196"| false
        "6198"| false
        "6201"| false
        "6202"| false
        "6203"| false
        "6204"| false
        "6205"| false
        "6206"| false
        "6207"| false
        "6209"| false
        "621"| false
        "6220"| false
        "6221"| false
        "6222"| false
        "6223"| false
        "6224"| false
        "6226"| false
        "6227"| false
        "6228"| false
        "6229"| false
        "6231"| false
        "6232"| false
        "6233"| false
        "6234"| false
        "6235"| false
        "6236"| false
        "6237"| false
        "6238"| false
        "6239"| false
        "6241"| false
        "6242"| false
        "6243"| false
        "6244"| false
        "6245"| false
        "6246"| false
        "6247"| false
        "6249"| false
        "6251"| false
        "6252"| false
        "6253"| false
        "6254"| false
        "6255"| false
        "6256"| false
        "6257"| false
        "6258"| false
        "6261"| false
        "6262"| false
        "6263"| false
        "6264"| false
        "6265"| false
        "6266"| false
        "6267"| false
        "6268"| false
        "6269"| false
        "6271"| false
        "6272"| false
        "6274"| false
        "6275"| false
        "6276"| false
        "6281"| false
        "6282"| false
        "6283"| false
        "6284"| false
        "6285"| false
        "6286"| false
        "6287"| false
        "6291"| false
        "6292"| false
        "6293"| false
        "6294"| false
        "6295"| false
        "6296"| false
        "6297"| false
        "6298"| false
        "6301"| false
        "6302"| false
        "6303"| false
        "6304"| false
        "6305"| false
        "6306"| false
        "6307"| false
        "6308"| false
        "631"|  false
        "6321"| false
        "6322"| false
        "6323"| false
        "6324"| false
        "6325"| false
        "6326"| false
        "6327"| false
        "6328"| false
        "6329"| false
        "6331"| false
        "6332"| false
        "6333"| false
        "6334"| false
        "6335"| false
        "6336"| false
        "6337"| false
        "6338"| false
        "6339"| false
        "6340"| false
        "6341"| false
        "6342"| false
        "6343"| false
        "6344"| false
        "6345"| false
        "6346"| false
        "6347"| false
        "6348"| false
        "6349"| false
        "6351"| false
        "6352"| false
        "6353"| false
        "6355"| false
        "6356"| false
        "6357"| false
        "6358"| false
        "6359"| false
        "6361"| false
        "6362"| false
        "6363"| false
        "6364"| false
        "6371"| false
        "6372"| false
        "6373"| false
        "6374"| false
        "6375"| false
        "6381"| false
        "6382"| false
        "6383"| false
        "6384"| false
        "6385"| false
        "6386"| false
        "6387"| false
        "6391"| false
        "6392"| false
        "6393"| false
        "6394"| false
        "6395"| false
        "6396"| false
        "6397"| false
        "6398"| false
        "6400"| false
        "6401"| false
        "6402"| false
        "6403"| false
        "6404"| false
        "6405"| false
        "6406"| false
        "6407"| false
        "6408"| false
        "6409"| false
        "641"|  false
        "6420"| false
        "6421"| false
        "6422"| false
        "6423"| false
        "6424"| false
        "6425"| false
        "6426"| false
        "6427"| false
        "6428"| false
        "6429"| false
        "6430"| false
        "6431"| false
        "6432"| false
        "6433"| false
        "6434"| false
        "6435"| false
        "6436"| false
        "6438"| false
        "6439"| false
        "6440"| false
        "6441"| false
        "6442"| false
        "6443"| false
        "6444"| false
        "6445"| false
        "6446"| false
        "6447"| false
        "6449"| false
        "6451"| false
        "6452"| false
        "6453"| false
        "6454"| false
        "6455"| false
        "6456"| false
        "6457"| false
        "6458"| false
        "6461"| false
        "6462"| false
        "6464"| false
        "6465"| false
        "6466"| false
        "6467"| false
        "6468"| false
        "6471"| false
        "6472"| false
        "6473"| false
        "6474"| false
        "6475"| false
        "6476"| false
        "6477"| false
        "6478"| false
        "6479"| false
        "6482"| false
        "6483"| false
        "6484"| false
        "6485"| false
        "6486"| false
        "6500"| false
        "6501"| false
        "6502"| false
        "6503"| false
        "6504"| false
        "6505"| false
        "6506"| false
        "6507"| false
        "6508"| false
        "6509"| false
        "651"|  false
        "6522"| false
        "6523"| false
        "6524"| false
        "6525"| false
        "6526"| false
        "6527"| false
        "6531"| false
        "6532"| false
        "6533"| false
        "6534"| false
        "6535"| false
        "6536"| false
        "6541"| false
        "6542"| false
        "6543"| false
        "6544"| false
        "6545"| false
        "6550"| false
        "6551"| false
        "6552"| false
        "6553"| false
        "6554"| false
        "6555"| false
        "6556"| false
        "6557"| false
        "6558"| false
        "6559"| false
        "6561"| false
        "6562"| false
        "6563"| false
        "6564"| false
        "6565"| false
        "6566"| false
        "6567"| false
        "6568"| false
        "6569"| false
        "6571"| false
        "6572"| false
        "6573"| false
        "6574"| false
        "6575"| false
        "6578"| false
        "6580"| false
        "6581"| false
        "6582"| false
        "6583"| false
        "6584"| false
        "6585"| false
        "6586"| false
        "6587"| false
        "6588"| false
        "6589"| false
        "6591"| false
        "6592"| false
        "6593"| false
        "6594"| false
        "6595"| false
        "6596"| false
        "6597"| false
        "6599"| false
        "661"|  false
        "6620"| false
        "6621"| false
        "6622"| false
        "6623"| false
        "6624"| false
        "6625"| false
        "6626"| false
        "6627"| false
        "6628"| false
        "6629"| false
        "6630"| false
        "6631"| false
        "6633"| false
        "6634"| false
        "6635"| false
        "6636"| false
        "6637"| false
        "6638"| false
        "6639"| false
        "6641"| false
        "6642"| false
        "6643"| false
        "6644"| false
        "6645"| false
        "6646"| false
        "6647"| false
        "6648"| false
        "6650"| false
        "6651"| false
        "6652"| false
        "6653"| false
        "6654"| false
        "6655"| false
        "6656"| false
        "6657"| false
        "6658"| false
        "6659"| false
        "6660"| false
        "6661"| false
        "6663"| false
        "6664"| false
        "6665"| false
        "6666"| false
        "6667"| false
        "6668"| false
        "6669"| false
        "6670"| false
        "6672"| false
        "6673"| false
        "6674"| false
        "6675"| false
        "6676"| false
        "6677"| false
        "6678"| false
        "6681"| false
        "6682"| false
        "6683"| false
        "6684"| false
        "6691"| false
        "6692"| false
        "6693"| false
        "6694"| false
        "6695"| false
        "6696"| false
        "6697"| false
        "6698"| false
        "6701"| false
        "6703"| false
        "6704"| false
        "6706"| false
        "6707"| false
        "6708"| false
        "6709"| false
        "671"|  false
        "6721"| false
        "6722"| false
        "6723"| false
        "6724"| false
        "6725"| false
        "6726"| false
        "6727"| false
        "6728"| false
        "6731"| false
        "6732"| false
        "6733"| false
        "6734"| false
        "6735"| false
        "6736"| false
        "6737"| false
        "6741"| false
        "6742"| false
        "6743"| false
        "6744"| false
        "6745"| false
        "6746"| false
        "6747"| false
        "6751"| false
        "6752"| false
        "6753"| false
        "6754"| false
        "6755"| false
        "6756"| false
        "6757"| false
        "6758"| false
        "6761"| false
        "6762"| false
        "6763"| false
        "6764"| false
        "6765"| false
        "6766"| false
        "6771"| false
        "6772"| false
        "6773"| false
        "6774"| false
        "6775"| false
        "6776"| false
        "6781"| false
        "6782"| false
        "6783"| false
        "6784"| false
        "6785"| false
        "6786"| false
        "6787"| false
        "6788"| false
        "6789"| false
        "6802"| false
        "6803"| false
        "6804"| false
        "6805"| false
        "6806"| false
        "6809"| false
        "681"|  false
        "6821"| false
        "6824"| false
        "6825"| false
        "6826"| false
        "6827"| false
        "6831"| false
        "6832"| false
        "6833"| false
        "6834"| false
        "6835"| false
        "6836"| false
        "6837"| false
        "6838"| false
        "6841"| false
        "6842"| false
        "6843"| false
        "6844"| false
        "6848"| false
        "6849"| false
        "6851"| false
        "6852"| false
        "6853"| false
        "6854"| false
        "6855"| false
        "6856"| false
        "6857"| false
        "6858"| false
        "6861"| false
        "6864"| false
        "6865"| false
        "6866"| false
        "6867"| false
        "6868"| false
        "6869"| false
        "6871"| false
        "6872"| false
        "6873"| false
        "6874"| false
        "6875"| false
        "6876"| false
        "6881"| false
        "6887"| false
        "6888"| false
        "6893"| false
        "6894"| false
        "6897"| false
        "6898"| false
        "69"|  false
        "7021"| false
        "7022"| false
        "7023"| false
        "7024"| false
        "7025"| false
        "7026"| false
        "7031"| false
        "7032"| false
        "7033"| false
        "7034"| false
        "7041"| false
        "7042"| false
        "7043"| false
        "7044"| false
        "7045"| false
        "7046"| false
        "7051"| false
        "7052"| false
        "7053"| false
        "7054"| false
        "7055"| false
        "7056"| false
        "7062"| false
        "7063"| false
        "7066"| false
        "7071"| false
        "7072"| false
        "7073"| false
        "7081"| false
        "7082"| false
        "7083"| false
        "7084"| false
        "7085"| false
        "711"|  false
        "7121"| false
        "7122"| false
        "7123"| false
        "7124"| false
        "7125"| false
        "7126"| false
        "7127"| false
        "7128"| false
        "7129"| false
        "7130"| false
        "7131"| false
        "7132"| false
        "7133"| false
        "7134"| false
        "7135"| false
        "7136"| false
        "7138"| false
        "7139"| false
        "7141"| false
        "7142"| false
        "7143"| false
        "7144"| false
        "7145"| false
        "7146"| false
        "7147"| false
        "7148"| false
        "7150"| false
        "7151"| false
        "7152"| false
        "7153"| false
        "7154"| false
        "7156"| false
        "7157"| false
        "7158"| false
        "7159"| false
        "7161"| false
        "7162"| false
        "7163"| false
        "7164"| false
        "7165"| false
        "7166"| false
        "7171"| false
        "7172"| false
        "7173"| false
        "7174"| false
        "7175"| false
        "7176"| false
        "7181"| false
        "7182"| false
        "7183"| false
        "7184"| false
        "7191"| false
        "7192"| false
        "7193"| false
        "7194"| false
        "7195"| false
        "7202"| false
        "7203"| false
        "7204"| false
        "721"|  false
        "7220"| false
        "7221"| false
        "7222"| false
        "7223"| false
        "7224"| false
        "7225"| false
        "7226"| false
        "7227"| false
        "7228"| false
        "7229"| false
        "7231"| false
        "7232"| false
        "7233"| false
        "7234"| false
        "7235"| false
        "7236"| false
        "7237"| false
        "7240"| false
        "7242"| false
        "7243"| false
        "7244"| false
        "7245"| false
        "7246"| false
        "7247"| false
        "7248"| false
        "7249"| false
        "7250"| false
        "7251"| false
        "7252"| false
        "7253"| false
        "7254"| false
        "7255"| false
        "7256"| false
        "7257"| false
        "7258"| false
        "7259"| false
        "7260"| false
        "7261"| false
        "7262"| false
        "7263"| false
        "7264"| false
        "7265"| false
        "7266"| false
        "7267"| false
        "7268"| false
        "7269"| false
        "7271"| false
        "7272"| false
        "7273"| false
        "7274"| false
        "7275"| false
        "7276"| false
        "7277"| false
        "7300"| false
        "7302"| false
        "7303"| false
        "7304"| false
        "7305"| false
        "7306"| false
        "7307"| false
        "7308"| false
        "7309"| false
        "731"|  false
        "7321"| false
        "7322"| false
        "7323"| false
        "7324"| false
        "7325"| false
        "7326"| false
        "7327"| false
        "7328"| false
        "7329"| false
        "7331"| false
        "7332"| false
        "7333"| false
        "7334"| false
        "7335"| false
        "7336"| false
        "7337"| false
        "7340"| false
        "7343"| false
        "7344"| false
        "7345"| false
        "7346"| false
        "7347"| false
        "7348"| false
        "7351"| false
        "7352"| false
        "7353"| false
        "7354"| false
        "7355"| false
        "7356"| false
        "7357"| false
        "7358"| false
        "7361"| false
        "7362"| false
        "7363"| false
        "7364"| false
        "7365"| false
        "7366"| false
        "7367"| false
        "7371"| false
        "7373"| false
        "7374"| false
        "7375"| false
        "7376"| false
        "7381"| false
        "7382"| false
        "7383"| false
        "7384"| false
        "7385"| false
        "7386"| false
        "7387"| false
        "7388"| false
        "7389"| false
        "7391"| false
        "7392"| false
        "7393"| false
        "7394"| false
        "7395"| false
        "7402"| false
        "7403"| false
        "7404"| false
        "741"|  false
        "7420"| false
        "7422"| false
        "7423"| false
        "7424"| false
        "7425"| false
        "7426"| false
        "7427"| false
        "7428"| false
        "7429"| false
        "7431"| false
        "7432"| false
        "7433"| false
        "7434"| false
        "7435"| false
        "7436"| false
        "7440"| false
        "7441"| false
        "7442"| false
        "7443"| false
        "7444"| false
        "7445"| false
        "7446"| false
        "7447"| false
        "7448"| false
        "7449"| false
        "7451"| false
        "7452"| false
        "7453"| false
        "7454"| false
        "7455"| false
        "7456"| false
        "7457"| false
        "7458"| false
        "7459"| false
        "7461"| false
        "7462"| false
        "7463"| false
        "7464"| false
        "7465"| false
        "7466"| false
        "7467"| false
        "7471"| false
        "7472"| false
        "7473"| false
        "7474"| false
        "7475"| false
        "7476"| false
        "7477"| false
        "7478"| false
        "7482"| false
        "7483"| false
        "7484"| false
        "7485"| false
        "7486"| false
        "7502"| false
        "7503"| false
        "7504"| false
        "7505"| false
        "7506"| false
        "751"|  false
        "7520"| false
        "7522"| false
        "7524"| false
        "7525"| false
        "7527"| false
        "7528"| false
        "7529"| false
        "7531"| false
        "7532"| false
        "7533"| false
        "7534"| false
        "7541"| false
        "7542"| false
        "7543"| false
        "7544"| false
        "7545"| false
        "7546"| false
        "7551"| false
        "7552"| false
        "7553"| false
        "7554"| false
        "7555"| false
        "7556"| false
        "7557"| false
        "7558"| false
        "7561"| false
        "7562"| false
        "7563"| false
        "7564"| false
        "7565"| false
        "7566"| false
        "7567"| false
        "7568"| false
        "7569"| false
        "7570"| false
        "7571"| false
        "7572"| false
        "7573"| false
        "7574"| false
        "7575"| false
        "7576"| false
        "7577"| false
        "7578"| false
        "7579"| false
        "7581"| false
        "7582"| false
        "7583"| false
        "7584"| false
        "7585"| false
        "7586"| false
        "7587"| false
        "7602"| false
        "761"|  false
        "7620"| false
        "7621"| false
        "7622"| false
        "7623"| false
        "7624"| false
        "7625"| false
        "7626"| false
        "7627"| false
        "7628"| false
        "7629"| false
        "7631"| false
        "7632"| false
        "7633"| false
        "7634"| false
        "7635"| false
        "7636"| false
        "7641"| false
        "7642"| false
        "7643"| false
        "7644"| false
        "7645"| false
        "7646"| false
        "7651"| false
        "7652"| false
        "7653"| false
        "7654"| false
        "7655"| false
        "7656"| false
        "7657"| false
        "7660"| false
        "7661"| false
        "7662"| false
        "7663"| false
        "7664"| false
        "7665"| false
        "7666"| false
        "7667"| false
        "7668"| false
        "7669"| false
        "7671"| false
        "7672"| false
        "7673"| false
        "7674"| false
        "7675"| false
        "7676"| false
        "7681"| false
        "7682"| false
        "7683"| false
        "7684"| false
        "7685"| false
        "7702"| false
        "7703"| false
        "7704"| false
        "7705"| false
        "7706"| false
        "7707"| false
        "7708"| false
        "7709"| false
        "771"|  false
        "7720"| false
        "7721"| false
        "7722"| false
        "7723"| false
        "7724"| false
        "7725"| false
        "7726"| false
        "7727"| false
        "7728"| false
        "7729"| false
        "7731"| false
        "7732"| false
        "7733"| false
        "7734"| false
        "7735"| false
        "7736"| false
        "7738"| false
        "7739"| false
        "7741"| false
        "7742"| false
        "7743"| false
        "7744"| false
        "7745"| false
        "7746"| false
        "7747"| false
        "7748"| false
        "7751"| false
        "7753"| false
        "7754"| false
        "7755"| false
        "7761"| false
        "7762"| false
        "7763"| false
        "7764"| false
        "7765"| false
        "7771"| false
        "7773"| false
        "7774"| false
        "7775"| false
        "7777"| false
        "7802"| false
        "7803"| false
        "7804"| false
        "7805"| false
        "7806"| false
        "7807"| false
        "7808"| false
        "781"|  false
        "7821"| false
        "7822"| false
        "7823"| false
        "7824"| false
        "7825"| false
        "7826"| false
        "7831"| false
        "7832"| false
        "7833"| false
        "7834"| false
        "7835"| false
        "7836"| false
        "7837"| false
        "7838"| false
        "7839"| false
        "7841"| false
        "7842"| false
        "7843"| false
        "7844"| false
        "7851"| false
        "7852"| false
        "7853"| false
        "7854"| false
        "7903"| false
        "7904"| false
        "7905"| false
        "7906"| false
        "7907"| false
        "791"|  false
        "7930"| false
        "7931"| false
        "7932"| false
        "7933"| false
        "7934"| false
        "7935"| false
        "7936"| false
        "7937"| false
        "7938"| false
        "7939"| false
        "7940"| false
        "7941"| false
        "7942"| false
        "7943"| false
        "7944"| false
        "7945"| false
        "7946"| false
        "7947"| false
        "7948"| false
        "7949"| false
        "7950"| false
        "7951"| false
        "7952"| false
        "7953"| false
        "7954"| false
        "7955"| false
        "7957"| false
        "7958"| false
        "7959"| false
        "7961"| false
        "7962"| false
        "7963"| false
        "7964"| false
        "7965"| false
        "7966"| false
        "7967"| false
        "7971"| false
        "7972"| false
        "7973"| false
        "7974"| false
        "7975"| false
        "7976"| false
        "7977"| false
        "8020"| false
        "8021"| false
        "8022"| false
        "8023"| false
        "8024"| false
        "8025"| false
        "8026"| false
        "8027"| false
        "8028"| false
        "8029"| false
        "8031"| false
        "8032"| false
        "8033"| false
        "8034"| false
        "8035"| false
        "8036"| false
        "8038"| false
        "8039"| false
        "8041"| false
        "8042"| false
        "8043"| false
        "8045"| false
        "8046"| false
        "8051"| false
        "8052"| false
        "8053"| false
        "8054"| false
        "8055"| false
        "8056"| false
        "8057"| false
        "8061"| false
        "8062"| false
        "8063"| false
        "8064"| false
        "8065"| false
        "8066"| false
        "8067"| false
        "8071"| false
        "8072"| false
        "8073"| false
        "8074"| false
        "8075"| false
        "8076"| false
        "8081"| false
        "8082"| false
        "8083"| false
        "8084"| false
        "8085"| false
        "8086"| false
        "8091"| false
        "8092"| false
        "8093"| false
        "8094"| false
        "8095"| false
        "8102"| false
        "8104"| false
        "8105"| false
        "8106"| false
        "811"|  false
        "8121"| false
        "8122"| false
        "8123"| false
        "8124"| false
        "8131"| false
        "8133"| false
        "8134"| false
        "8135"| false
        "8136"| false
        "8137"| false
        "8138"| false
        "8139"| false
        "8141"| false
        "8142"| false
        "8143"| false
        "8144"| false
        "8145"| false
        "8146"| false
        "8151"| false
        "8152"| false
        "8153"| false
        "8157"| false
        "8158"| false
        "8161"| false
        "8165"| false
        "8166"| false
        "8167"| false
        "8168"| false
        "8170"| false
        "8171"| false
        "8176"| false
        "8177"| false
        "8178"| false
        "8179"| false
        "8191"| false
        "8192"| false
        "8193"| false
        "8194"| false
        "8195"| false
        "8196"| false
        "8202"| false
        "8203"| false
        "8204"| false
        "8205"| false
        "8206"| false
        "8207"| false
        "8208"| false
        "821"|  false
        "8221"| false
        "8222"| false
        "8223"| false
        "8224"| false
        "8225"| false
        "8226"| false
        "8230"| false
        "8231"| false
        "8232"| false
        "8233"| false
        "8234"| false
        "8236"| false
        "8237"| false
        "8238"| false
        "8239"| false
        "8241"| false
        "8243"| false
        "8245"| false
        "8246"| false
        "8247"| false
        "8248"| false
        "8249"| false
        "8250"| false
        "8251"| false
        "8252"| false
        "8253"| false
        "8254"| false
        "8257"| false
        "8258"| false
        "8259"| false
        "8261"| false
        "8262"| false
        "8263"| false
        "8265"| false
        "8266"| false
        "8267"| false
        "8268"| false
        "8269"| false
        "8271"| false
        "8272"| false
        "8273"| false
        "8274"| false
        "8276"| false
        "8281"| false
        "8282"| false
        "8283"| false
        "8284"| false
        "8285"| false
        "8291"| false
        "8292"| false
        "8293"| false
        "8294"| false
        "8295"| false
        "8296"| false
        "8302"| false
        "8303"| false
        "8304"| false
        "8306"| false
        "831"|  false
        "8320"| false
        "8321"| false
        "8322"| false
        "8323"| false
        "8324"| false
        "8325"| false
        "8326"| false
        "8327"| false
        "8328"| false
        "8330"| false
        "8331"| false
        "8332"| false
        "8333"| false
        "8334"| false
        "8335"| false
        "8336"| false
        "8337"| false
        "8338"| false
        "8340"| false
        "8341"| false
        "8342"| false
        "8343"| false
        "8344"| false
        "8345"| false
        "8346"| false
        "8347"| false
        "8348"| false
        "8349"| false
        "8361"| false
        "8362"| false
        "8363"| false
        "8364"| false
        "8365"| false
        "8366"| false
        "8367"| false
        "8368"| false
        "8369"| false
        "8370"| false
        "8372"| false
        "8373"| false
        "8374"| false
        "8375"| false
        "8376"| false
        "8377"| false
        "8378"| false
        "8379"| false
        "8380"| false
        "8381"| false
        "8382"| false
        "8383"| false
        "8384"| false
        "8385"| false
        "8386"| false
        "8387"| false
        "8388"| false
        "8389"| false
        "8392"| false
        "8393"| false
        "8394"| false
        "8395"| false
        "8402"| false
        "8403"| false
        "8404"| false
        "8405"| false
        "8406"| false
        "8407"| false
        "841"|  false
        "8421"| false
        "8422"| false
        "8423"| false
        "8424"| false
        "8426"| false
        "8427"| false
        "8431"| false
        "8432"| false
        "8433"| false
        "8434"| false
        "8435"| false
        "8441"| false
        "8442"| false
        "8443"| false
        "8444"| false
        "8445"| false
        "8446"| false
        "8450"| false
        "8452"| false
        "8453"| false
        "8454"| false
        "8456"| false
        "8457"| false
        "8458"| false
        "8459"| false
        "8460"| false
        "8461"| false
        "8462"| false
        "8463"| false
        "8464"| false
        "8465"| false
        "8466"| false
        "8467"| false
        "8468"| false
        "8469"| false
        "8501"| false
        "8502"| false
        "8503"| false
        "8504"| false
        "8505"| false
        "8506"| false
        "8507"| false
        "8509"| false
        "851"|  false
        "8531"| false
        "8532"| false
        "8533"| false
        "8534"| false
        "8535"| false
        "8536"| false
        "8537"| false
        "8538"| false
        "8541"| false
        "8542"| false
        "8543"| false
        "8544"| false
        "8545"| false
        "8546"| false
        "8547"| false
        "8548"| false
        "8549"| false
        "8550"| false
        "8551"| false
        "8552"| false
        "8553"| false
        "8554"| false
        "8555"| false
        "8556"| false
        "8557"| false
        "8558"| false
        "8561"| false
        "8562"| false
        "8563"| false
        "8564"| false
        "8565"| false
        "8571"| false
        "8572"| false
        "8573"| false
        "8574"| false
        "8581"| false
        "8582"| false
        "8583"| false
        "8584"| false
        "8585"| false
        "8586"| false
        "8591"| false
        "8592"| false
        "8593"| false
        "861"|  false
        "8621"| false
        "8622"| false
        "8623"| false
        "8624"| false
        "8628"| false
        "8629"| false
        "8630"| false
        "8631"| false
        "8633"| false
        "8634"| false
        "8635"| false
        "8636"| false
        "8637"| false
        "8638"| false
        "8639"| false
        "8640"| false
        "8641"| false
        "8642"| false
        "8649"| false
        "8650"| false
        "8651"| false
        "8652"| false
        "8654"| false
        "8656"| false
        "8657"| false
        "8661"| false
        "8662"| false
        "8663"| false
        "8664"| false
        "8665"| false
        "8666"| false
        "8667"| false
        "8669"| false
        "8670"| false
        "8671"| false
        "8677"| false
        "8678"| false
        "8679"| false
        "8681"| false
        "8682"| false
        "8683"| false
        "8684"| false
        "8685"| false
        "8686"| false
        "8687"| false
        "8702"| false
        "8703"| false
        "8704"| false
        "8705"| false
        "8706"| false
        "8707"| false
        "8708"| false
        "8709"| false
        "871"|  false
        "8721"| false
        "8722"| false
        "8723"| false
        "8724"| false
        "8725"| false
        "8726"| false
        "8727"| false
        "8728"| false
        "8731"| false
        "8732"| false
        "8733"| false
        "8734"| false
        "8735"| false
        "8741"| false
        "8742"| false
        "8743"| false
        "8744"| false
        "8745"| false
        "8751"| false
        "8752"| false
        "8753"| false
        "8754"| false
        "8756"| false
        "8761"| false
        "8762"| false
        "8764"| false
        "8765"| false
        "8766"| false
        "8771"| false
        "8772"| false
        "8773"| false
        "8774"| false
        "8781"| false
        "8782"| false
        "8783"| false
        "8784"| false
        "8785"| false
        "8801"| false
        "8802"| false
        "8803"| false
        "8805"| false
        "8806"| false
        "8807"| false
        "8808"| false
        "8809"| false
        "881"|  false
        "8821"| false
        "8822"| false
        "8823"| false
        "8824"| false
        "8825"| false
        "8841"| false
        "8845"| false
        "8846"| false
        "8847"| false
        "8851"| false
        "8856"| false
        "8857"| false
        "8858"| false
        "8860"| false
        "8861"| false
        "8862"| false
        "8867"| false
        "8868"| false
        "8869"| false
        "89"|  false
        "906"|  false
        "9070"| false
        "9071"| false
        "9072"| false
        "9073"| false
        "9074"| false
        "9075"| false
        "9076"| false
        "9077"| false
        "9078"| false
        "9080"| false
        "9081"| false
        "9082"| false
        "9083"| false
        "9084"| false
        "9085"| false
        "9086"| false
        "9087"| false
        "9088"| false
        "9089"| false
        "9090"| false
        "9091"| false
        "9092"| false
        "9093"| false
        "9094"| false
        "9097"| false
        "9099"| false
        "9101"| false
        "9102"| false
        "9103"| false
        "9104"| false
        "9105"| false
        "9106"| false
        "9107"| false
        "911"|  false
        "9120"| false
        "9122"| false
        "9123"| false
        "9126"| false
        "9127"| false
        "9128"| false
        "9129"| false
        "9131"| false
        "9132"| false
        "9133"| false
        "9134"| false
        "9135"| false
        "9141"| false
        "9142"| false
        "9143"| false
        "9144"| false
        "9145"| false
        "9146"| false
        "9147"| false
        "9148"| false
        "9149"| false
        "9151"| false
        "9152"| false
        "9153"| false
        "9154"| false
        "9155"| false
        "9156"| false
        "9157"| false
        "9158"| false
        "9161"| false
        "9162"| false
        "9163"| false
        "9164"| false
        "9165"| false
        "9166"| false
        "9167"| false
        "9170"| false
        "9171"| false
        "9172"| false
        "9173"| false
        "9174"| false
        "9175"| false
        "9176"| false
        "9177"| false
        "9178"| false
        "9179"| false
        "9180"| false
        "9181"| false
        "9182"| false
        "9183"| false
        "9184"| false
        "9185"| false
        "9186"| false
        "9187"| false
        "9188"| false
        "9189"| false
        "9190"| false
        "9191"| false
        "9192"| false
        "9193"| false
        "9194"| false
        "9195"| false
        "9196"| false
        "9197"| false
        "9198"| false
        "9199"| false
        "9201"| false
        "9202"| false
        "9203"| false
        "9204"| false
        "9205"| false
        "9206"| false
        "9207"| false
        "9208"| false
        "9209"| false
        "921"|  false
        "9220"| false
        "9221"| false
        "9222"| false
        "9223"| false
        "9225"| false
        "9227"| false
        "9228"| false
        "9229"| false
        "9231"| false
        "9232"| false
        "9233"| false
        "9234"| false
        "9235"| false
        "9236"| false
        "9238"| false
        "9241"| false
        "9242"| false
        "9243"| false
        "9244"| false
        "9245"| false
        "9246"| false
        "9251"| false
        "9252"| false
        "9253"| false
        "9254"| false
        "9255"| false
        "9256"| false
        "9257"| false
        "9260"| false
        "9261"| false
        "9262"| false
        "9263"| false
        "9264"| false
        "9265"| false
        "9266"| false
        "9267"| false
        "9268"| false
        "9269"| false
        "9270"| false
        "9271"| false
        "9272"| false
        "9273"| false
        "9274"| false
        "9275"| false
        "9276"| false
        "9277"| false
        "9278"| false
        "9279"| false
        "9280"| false
        "9281"| false
        "9282"| false
        "9283"| false
        "9284"| false
        "9285"| false
        "9286"| false
        "9287"| false
        "9288"| false
        "9289"| false
        "9292"| false
        "9293"| false
        "9294"| false
        "9295"| false
        "9302"| false
        "9303"| false
        "9305"| false
        "9306"| false
        "9307"| false
        "931"|  false
        "9321"| false
        "9323"| false
        "9324"| false
        "9325"| false
        "9326"| false
        "9331"| false
        "9332"| false
        "9333"| false
        "9334"| false
        "9335"| false
        "9336"| false
        "9337"| false
        "9338"| false
        "9339"| false
        "9340"| false
        "9341"| false
        "9342"| false
        "9343"| false
        "9344"| false
        "9345"| false
        "9346"| false
        "9347"| false
        "9348"| false
        "9349"| false
        "9350"| false
        "9351"| false
        "9352"| false
        "9353"| false
        "9354"| false
        "9355"| false
        "9356"| false
        "9357"| false
        "9358"| false
        "9359"| false
        "9360"| false
        "9363"| false
        "9364"| false
        "9365"| false
        "9366"| false
        "9367"| false
        "9369"| false
        "9371"| false
        "9372"| false
        "9373"| false
        "9374"| false
        "9375"| false
        "9376"| false
        "9377"| false
        "9378"| false
        "9381"| false
        "9382"| false
        "9383"| false
        "9384"| false
        "9385"| false
        "9386"| false
        "9391"| false
        "9392"| false
        "9393"| false
        "9394"| false
        "9395"| false
        "9396"| false
        "9397"| false
        "9398"| false
        "9401"| false
        "9402"| false
        "9403"| false
        "9404"| false
        "9405"| false
        "9406"| false
        "9407"| false
        "9408"| false
        "9409"| false
        "941"|  false
        "9420"| false
        "9421"| false
        "9422"| false
        "9423"| false
        "9424"| false
        "9426"| false
        "9427"| false
        "9428"| false
        "9429"| false
        "9431"| false
        "9433"| false
        "9434"| false
        "9435"| false
        "9436"| false
        "9438"| false
        "9439"| false
        "9441"| false
        "9442"| false
        "9443"| false
        "9444"| false
        "9445"| false
        "9446"| false
        "9447"| false
        "9448"| false
        "9451"| false
        "9452"| false
        "9453"| false
        "9454"| false
        "9461"| false
        "9462"| false
        "9463"| false
        "9464"| false
        "9465"| false
        "9466"| false
        "9467"| false
        "9468"| false
        "9469"| false
        "9471"| false
        "9472"| false
        "9473"| false
        "9474"| false
        "9480"| false
        "9481"| false
        "9482"| false
        "9484"| false
        "9491"| false
        "9492"| false
        "9493"| false
        "9495"| false
        "9497"| false
        "9498"| false
        "9499"| false
        "9502"| false
        "9503"| false
        "9504"| false
        "9505"| false
        "951"|  false
        "9521"| false
        "9522"| false
        "9523"| false
        "9524"| false
        "9525"| false
        "9526"| false
        "9527"| false
        "9528"| false
        "9529"| false
        "9531"| false
        "9532"| false
        "9533"| false
        "9534"| false
        "9535"| false
        "9536"| false
        "9542"| false
        "9543"| false
        "9544"| false
        "9545"| false
        "9546"| false
        "9547"| false
        "9548"| false
        "9549"| false
        "9551"| false
        "9552"| false
        "9553"| false
        "9554"| false
        "9555"| false
        "9556"| false
        "9560"| false
        "9561"| false
        "9562"| false
        "9563"| false
        "9564"| false
        "9565"| false
        "9566"| false
        "9567"| false
        "9568"| false
        "9569"| false
        "9571"| false
        "9572"| false
        "9573"| false
        "9574"| false
        "9575"| false
        "9576"| false
        "9602"| false
        "9603"| false
        "9604"| false
        "9605"| false
        "9606"| false
        "9607"| false
        "9608"| false
        "961"|  false
        "9621"| false
        "9622"| false
        "9624"| false
        "9625"| false
        "9626"| false
        "9627"| false
        "9628"| false
        "9631"| false
        "9632"| false
        "9633"| false
        "9634"| false
        "9635"| false
        "9636"| false
        "9637"| false
        "9638"| false
        "9639"| false
        "9641"| false
        "9642"| false
        "9643"| false
        "9644"| false
        "9645"| false
        "9646"| false
        "9647"| false
        "9648"| false
        "9651"| false
        "9652"| false
        "9653"| false
        "9654"| false
        "9655"| false
        "9656"| false
        "9657"| false
        "9658"| false
        "9659"| false
        "9661"| false
        "9662"| false
        "9663"| false
        "9664"| false
        "9665"| false
        "9666"| false
        "9671"| false
        "9672"| false
        "9673"| false
        "9674"| false
        "9675"| false
        "9676"| false
        "9677"| false
        "9681"| false
        "9682"| false
        "9683"| false
        "9701"| false
        "9704"| false
        "9708"| false
        "971"|  false
        "9720"| false
        "9721"| false
        "9722"| false
        "9723"| false
        "9724"| false
        "9725"| false
        "9726"| false
        "9727"| false
        "9728"| false
        "9729"| false
        "9732"| false
        "9733"| false
        "9734"| false
        "9735"| false
        "9736"| false
        "9737"| false
        "9738"| false
        "9741"| false
        "9742"| false
        "9744"| false
        "9745"| false
        "9746"| false
        "9747"| false
        "9748"| false
        "9749"| false
        "9761"| false
        "9762"| false
        "9763"| false
        "9764"| false
        "9765"| false
        "9766"| false
        "9771"| false
        "9772"| false
        "9773"| false
        "9774"| false
        "9775"| false
        "9776"| false
        "9777"| false
        "9778"| false
        "9779"| false
        "9802"| false
        "9803"| false
        "9804"| false
        "9805"| false
        "981"|  false
        "9820"| false
        "9822"| false
        "9823"| false
        "9824"| false
        "9825"| false
        "9826"| false
        "9827"| false
        "9828"| false
        "9829"| false
        "9831"| false
        "9832"| false
        "9833"| false
        "9834"| false
        "9835"| false
        "9836"| false
        "9837"| false
        "9841"| false
        "9842"| false
        "9843"| false
        "9844"| false
        "9845"| false
        "9846"| false
        "9847"| false
        "9848"| false
        "9851"| false
        "9852"| false
        "9853"| false
        "9854"| false
        "9855"| false
        "9856"| false
        "9857"| false
        "9861"| false
        "9865"| false
        "9867"| false
        "9868"| false
        "9869"| false
        "9871"| false
        "9872"| false
        "9873"| false
        "9874"| false
        "9875"| false
        "9876"| false
        "9901"| false
        "9903"| false
        "9904"| false
        "9905"| false
        "9906"| false
        "9907"| false
        "9908"| false
        "991"|  false
        "9920"| false
        "9921"| false
        "9922"| false
        "9923"| false
        "9924"| false
        "9925"| false
        "9926"| false
        "9927"| false
        "9928"| false
        "9929"| false
        "9931"| false
        "9932"| false
        "9933"| false
        "9935"| false
        "9936"| false
        "9937"| false
        "9938"| false
        "9941"| false
        "9942"| false
        "9943"| false
        "9944"| false
        "9945"| false
        "9946"| false
        "9947"| false
        "9948"| false
        "9951"| false
        "9952"| false
        "9953"| false
        "9954"| false
        "9955"| false
        "9956"| false
        "9961"| false
        "9962"| false
        "9963"| false
        "9964"| false
        "9965"| false
        "9966"| false
        "9971"| false
        "9972"| false
        "9973"| false
        "9974"| false
        "9975"| false
        "9976"| false
        "9977"| false
        "9978"| false

    }

}