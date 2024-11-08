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
package de.telekom.phonenumbernormalizer.numberplans.constants;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.telekom.phonenumbernormalizer.numberplans.NumberPlan;
import lombok.RequiredArgsConstructor;


class NDCDetails {
    int minNumberLength = -1;

    int maxNumberLength = -1;

    boolean isOptional = true; // if both callers have same NDC, can the caller drop it?

    int lengthOfNumberPrefix = 0; // some NDC have different length definition for specific ranges defined by the prefix of a number.

    public NDCDetails(int min, int max, boolean optional, int prefixLength) {
        this.minNumberLength = min;
        this.maxNumberLength = max;
        this.isOptional = optional;
        this.lengthOfNumberPrefix = prefixLength;
    }

    public NDCDetails(int min, int max, boolean optional) {
        this.minNumberLength = min;
        this.maxNumberLength = max;
        this.isOptional = optional;
    }
}

class ShortNumberDetails {
    int length;

    boolean usableWithIDPandCCfromOutside;

    boolean usableWithIDPandCCandNDCfromOutside ;

    boolean usableWithIDPandCCfromInside;

    boolean usableWithIDPandCCandNDCfromInside;

    boolean usableWithNAC;
    boolean usableWithNACandNDC;

    boolean usableDirectly;

    public ShortNumberDetails(int length, boolean usableWithIDPandCCfromOutside,
                                          boolean usableWithIDPandCCandNDCfromOutside,
                                          boolean usableWithIDPandCCfromInside,
                                          boolean usableWithIDPandCCandNDCfromInside,
                                          boolean usableWithNAC,
                                          boolean usableWithNACandNDC,
                                          boolean usableDirectly) {
        this.length = length;
        this.usableWithIDPandCCfromOutside = usableWithIDPandCCfromOutside;
        this.usableWithIDPandCCandNDCfromOutside = usableWithIDPandCCandNDCfromOutside;
        this.usableWithIDPandCCfromInside = usableWithIDPandCCfromInside;
        this.usableWithIDPandCCandNDCfromInside = usableWithIDPandCCandNDCfromInside;
        this.usableWithNAC = usableWithNAC;
        this.usableWithNACandNDC = usableWithNACandNDC;
        this.usableDirectly = usableDirectly;
    }
}

/**
 *  Definition see Chapter 8.1 in <a href="https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Nummerierungskonzept/Nummerierungskonzept2011pdf.pdf?__blob=publicationFile">BNetzA German Number Plan</a>
 *
 */
public class DeFixedLineNumberPlan extends NumberPlan {

    /**
     * Constant for German Country Calling Code
     */
    private static final String COUNTRY_CODE = "49";

    /**
     * Constants for German short numbers in fixed-line
     */
    private static final Map<String, ShortNumberDetails> SHORT_NUMBER_CODES_DETAILS = Map.of(
            "110", new ShortNumberDetails(3, false, false, false, false, false, false, true),
            "112", new ShortNumberDetails(3, false, false, false, false, false, false, true),
            "115", new ShortNumberDetails(3, true, true, false, true, false, true, true),
            "116", new ShortNumberDetails(6, true, false, true, false, false, false, true),
            "1180", new ShortNumberDetails(6, false, false, false, false, false, false, false), // 1180xx is currently just reserved for future used
            "118", new ShortNumberDetails(5, false, false, false, false, false, false, true)  // This covers  1181 - 1189 since 1180 is longer prefix and has its own value.
    );

    private static final Map<String, NDCDetails> NDC_DETAILS;

    static {
        NDC_DETAILS = Map.ofEntries(
                /* https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/LaengeRufnummernbloecke/start.html */
                /*
                    The following Code is generated by the python script: src/generators/GermanAreaCodeExtractor/mobile.py
                    it is using a csv of all German fixed line Area Codes. If that gets updated, you can use the script to generate new
                    code and past it between the comments below.

                    TODO: special NDC need to be added to the script (mobile is done)
                */

                /*
                 * Generation started
                 */
                Map.entry("15019", new NDCDetails(6, 6, false)), // Tismi BV
                Map.entry("15020", new NDCDetails(6, 6, false)), // Legos - Local Exchange Global Operation Services
                Map.entry("1511", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1512", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1514", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1515", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1516", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1517", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("15180", new NDCDetails(6, 6, false)), // Telekom Deutschland GmbH
                Map.entry("15181", new NDCDetails(6, 6, false)), // Telekom Deutschland GmbH
                Map.entry("15182", new NDCDetails(6, 6, false)), // Telekom Deutschland GmbH
                Map.entry("15183", new NDCDetails(6, 6, false)), // Telekom Deutschland GmbH
                Map.entry("15310", new NDCDetails(6, 6, false)), // MTEL Deutschland GmbH
                Map.entry("1520", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("1521", new NDCDetails(7, 7, false)), // Lycamobile Europe Ltd.
                Map.entry("1522", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("1523", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("1525", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("1526", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("1529", new NDCDetails(7, 7, false)), // Vodafone GmbH  (Netznutzungsvereinbarung mit Fa. TP Germany Operations GmbH  ehemals Fa. Truphone GmbH )
                Map.entry("15510", new NDCDetails(6, 6, false)), // Lebara Limited
                Map.entry("15511", new NDCDetails(6, 6, false)), // Lebara Limited
                Map.entry("15560", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15561", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15562", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15563", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15564", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15565", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15566", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15567", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15568", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15569", new NDCDetails(6, 6, false)), // 1&1 Mobilfunk GmbH
                Map.entry("15630", new NDCDetails(6, 6, false)), // multiConnect GmbH
                Map.entry("15678", new NDCDetails(6, 6, false)), // Argon Networks UG
                Map.entry("15679", new NDCDetails(6, 6, false)), // Argon Networks UG
                Map.entry("15700", new NDCDetails(6, 6, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("15701", new NDCDetails(6, 6, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("15702", new NDCDetails(6, 6, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("15703", new NDCDetails(6, 6, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("15704", new NDCDetails(6, 6, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("15706", new NDCDetails(6, 6, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("1573", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("1575", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("1577", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("1578", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("15888", new NDCDetails(6, 6, false)), // TelcoVillage GmbH
                Map.entry("1590", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("160", new NDCDetails(7, 8, false, 1)), // Telekom Deutschland GmbH
                // NDC 160 uses first digit of number for deviating ranges with different length
                Map.entry("1600", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1601", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1602", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1603", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1604", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1605", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1606", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1607", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1608", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("1609", new NDCDetails(8, 8, false)), // Telekom Deutschland GmbH
                Map.entry("162", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("163", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("170", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("171", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("172", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("173", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("174", new NDCDetails(7, 7, false)), // Vodafone GmbH
                Map.entry("175", new NDCDetails(7, 7, false)), // Telekom Deutschland GmbH
                Map.entry("176", new NDCDetails(8, 8, false)), // Telefónica Germany GmbH & Co. OHG
                Map.entry("177", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("178", new NDCDetails(7, 7, false)), // Telefónica Germany GmbH & Co. OHG  (ehem. E-Plus Mobilfunk GmbH )
                Map.entry("179", new NDCDetails(7, 7, false)) // Telefónica Germany GmbH & Co. OHG
                /*
                 * Generation ended
                 */
        );
    }

    /**
     * Constant for German short numbers in fixed-line as extracted from the details above
     */
    private static final Map<String, Integer> SHORT_NUMBER_CODES = SHORT_NUMBER_CODES_DETAILS.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().length));

    public boolean isNDCNationalOperatorOnly(String ndc) {
        return "199".equals(ndc);
    }

    public int getNationDestinationCodeMinimalNumberLength(String ndc, String number) {

        if (NDC_DETAILS.containsKey(ndc)) {

            NDCDetails details = NDC_DETAILS.get(ndc);

            if ((details.lengthOfNumberPrefix > 0) && (number != null) && (number.length()>=details.lengthOfNumberPrefix)) {
                for (int i=details.lengthOfNumberPrefix; i>0; i--){
                    String ndcWithPrefix = ndc + number.substring(0, i);
                    if (NDC_DETAILS.containsKey(ndcWithPrefix)) {
                        return NDC_DETAILS.get(ndcWithPrefix).minNumberLength;
                    }
                }
            }

            return details.minNumberLength;
        }

        return -1;
    }

    public int getNationDestinationCodeMaximumNumberLength(String ndc, String number) {
        if (NDC_DETAILS.containsKey(ndc)) {

            NDCDetails details = NDC_DETAILS.get(ndc);

            if ((details.lengthOfNumberPrefix > 0) && (number != null) && (number.length()>=details.lengthOfNumberPrefix)) {
                for (int i=details.lengthOfNumberPrefix; i>0; i--){
                    String ndcWithPrefix = ndc + number.substring(0, i);
                    if (NDC_DETAILS.containsKey(ndcWithPrefix)) {
                        return NDC_DETAILS.get(ndcWithPrefix).maxNumberLength;
                    }
                }
            }

            return details.maxNumberLength;
        }

        return -1;
    }

    public int getDefaultMinimalNumberLength() {
        return 2; // VW in Wolfsburg (NDC: 5361) Number: 90
    }

    public int getDefaultMaximumNumberLength() {
        return 11; // National number is max 13 digits long, while shortest NDC is 2 digits, so 11 left for the number itself.
    }

    @Override
    public boolean isNDCOptional(String ndc) {
        if (NDC_DETAILS.containsKey(ndc)) {
            return NDC_DETAILS.get(ndc).isOptional;
        }

        return GermanAreaCodeExtractor.isNDCOptional(ndc);
    }

    @Override
    public boolean isReserved(String number) {
        // if the number is not usable at all, but it is defined so it is reserved (not valid yet - but maybe in the future)
        ShortNumberDetails numberDetails = SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number));
        if (numberDetails == null) {
            return false;
        }
        return !(numberDetails.usableWithIDPandCCfromOutside ||
                 numberDetails.usableWithIDPandCCandNDCfromOutside ||
                 numberDetails.usableWithIDPandCCfromInside ||
                 numberDetails.usableWithIDPandCCandNDCfromInside ||
                 numberDetails.usableWithNAC ||
                 numberDetails.usableWithNACandNDC ||
                 numberDetails.usableDirectly);
    }

    @Override
    public Integer isMatchingLength(String number) {
        ShortNumberDetails numberDetails = SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number));
        if (numberDetails == null) {
            return null;
        }
        return numberDetails.length - number.length();
    }


    @Override
    public boolean isUsableWithIDPandCCfromOutside(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableWithIDPandCCfromOutside;
    }

    @Override
    public boolean isUsableWithIDPandCCandNDCfromOutside(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableWithIDPandCCandNDCfromOutside;
    }

    @Override
    public boolean isUsableWithIDPandCCfromInside(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableWithIDPandCCfromInside;
    }

    @Override
    public boolean isUsableWithIDPandCCandNDCfromInside(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableWithIDPandCCandNDCfromInside;
    }

    @Override
    public boolean isUsableWithNAC(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableWithNAC;
    }
    @Override
    public boolean isUsableWithNACandNDC(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableWithNACandNDC;
    }

    @Override
    public boolean isUsableDirectly(String number) {
        return SHORT_NUMBER_CODES_DETAILS.get(startingWithShortNumberKey(number)).usableDirectly;
    }

    @Override
    protected Map<String, Integer> getShortNumberCodes() {
        return SHORT_NUMBER_CODES;
    }

    public static String getCountryCode() {
        return COUNTRY_CODE;
    }

    @Override
    public String getNationalDestinationCodeFromNationalSignificantNumber(String nsn) {
        if ((nsn == null) || (nsn.length()<1)) {
            return "";
        }
        // Geographic Area Codes
        return GermanAreaCodeExtractor.fromNumber(nsn);
    }

}
