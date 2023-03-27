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


import java.util.Map;

import de.telekom.phonenumbernormalizer.numberplans.NumberPlan;

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
     * Constant for German short numbers in fixed-line
     */
    private static final Map<String, Integer> SHORT_NUMBER_CODES = Map.of(
            "110", 3,
            "112", 3,
            "115", 3,
            "116", 6,
            "1180", 6,
            "118", 5  // This covers  1181 - 1189 since 1180 is longer prefix and has its own value.
    );


    @Override
    protected Map<String, Integer> getShortNumberCodes() {
        return SHORT_NUMBER_CODES;
    }

    public static String getCountryCode() {
        return COUNTRY_CODE;
    }

}
