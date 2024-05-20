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
            "115", new ShortNumberDetails(3, false, false, false, false, false, false, true),
            "116", new ShortNumberDetails(6, false, false, false, false, false, false, true),
            "1180", new ShortNumberDetails(6, false, false, false, false, false, false, true),
            "118", new ShortNumberDetails(5, false, false, false, false, false, false, true)  // This covers  1181 - 1189 since 1180 is longer prefix and has its own value.
    );

    /**
     * Constant for German short numbers in fixed-line as extracted from the details above
     */
    private static final Map<String, Integer> SHORT_NUMBER_CODES = SHORT_NUMBER_CODES_DETAILS.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().length));

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

        if ("1".equals(nsn.substring(0,1))) {
          // Non-Geographic Area Codes
            if (nsn.length()<2) {
                return "";
            }



        }
        // Geographic Area Codes
        return GermanAreaCodeExtractor.fromNumber(nsn);
    }

}
