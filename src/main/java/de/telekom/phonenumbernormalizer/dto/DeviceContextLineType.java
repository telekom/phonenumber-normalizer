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
package de.telekom.phonenumbernormalizer.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The enum values define the line-type in the context of a device involved in the call
 * <p/>
 * Currently supported values are for fixed-line, mobile and unknown (also for anything else).
 *
 * @see DeviceContextLineType#FIXEDLINE
 * @see DeviceContextLineType#MOBILE
 * @see DeviceContextLineType#UNKNOWN
 */
public enum DeviceContextLineType {

    /**
     * If the device from the user is a fixed-line device - like a smart speaker with a DECT connection over a DECT base on a fixed-line access
     *
     * @see DeviceContextLineType#FIXEDLINE_VALUE
     */
    @JsonProperty(DeviceContextLineType.FIXEDLINE_VALUE)
    FIXEDLINE(DeviceContextLineType.FIXEDLINE_VALUE),

    /**
     * If the device from the user is a mobile device - like a smart speaker with a HFP connection over a cell-phone
     *
     * @see DeviceContextLineType#MOBILE_VALUE
     */
    @JsonProperty(DeviceContextLineType.MOBILE_VALUE)
    MOBILE(DeviceContextLineType.MOBILE_VALUE),

    /**
     *  If the device from the user is none of any other defined value or not known.
     *
     * @see DeviceContextLineType#UNKNOWN_VALUE
     */
    @JsonProperty(DeviceContextLineType.UNKNOWN_VALUE)
    UNKNOWN(DeviceContextLineType.UNKNOWN_VALUE);

    /**
     * Value used to represent the FIXEDLINE enum value in a JSON
     *
     * @see DeviceContextLineType#FIXEDLINE
     */
    public static final String FIXEDLINE_VALUE = "fixline";

    /**
     * Value used to represent the MOBILE enum value in a JSON
     *
     * @see DeviceContextLineType#MOBILE
     */
    public static final String MOBILE_VALUE = "mobile";

    /**
     * Value used to represent the UNKNOWN enum value in a JSON
     *
     * @see DeviceContextLineType#UNKNOWN
     */
    public static final String UNKNOWN_VALUE = "unknown";

    /**
     * Besides the official representation FIXEDLINE_VALUE for the enum value FIXEDLINE
     * this additional strings are supported by decoding from String.
     *
     * @see DeviceContextLineType#FIXEDLINE
     * @see DeviceContextLineType#of(String)
     */
    private static final List<String> SYNONYM_LIST_FIXEDLINE_VALUES = Arrays.asList(
            FIXEDLINE_VALUE.toUpperCase(Locale.ROOT),
            "FIXEDLINE",
            "FIXED-LINE",
            "LANDLINE",
            "FESTNETZ"
    );

    /**
     * Besides the official representation MOBILE_VALUE for the enum value MOBILE
     * this additional strings are supported by decoding from String.
     *
     * @see DeviceContextLineType#MOBILE
     * @see DeviceContextLineType#of(String)
     */
    private static final List<String> SYNONYM_LIST_MOBILE_VALUES = Arrays.asList(
            MOBILE_VALUE.toUpperCase(Locale.ROOT),
            "MOBIL",
            "CELL",
            "CELLULAR",
            "MOBILFUNK"
    );

    /**
     * Storing the String representation for an enum value
     *
     * @see DeviceContextLineType#FIXEDLINE_VALUE
     * @see DeviceContextLineType#MOBILE_VALUE
     * @see DeviceContextLineType#UNKNOWN_VALUE
     * @see DeviceContextLineType#toValue()
     */
    private final String value;

    /**
     * Initializer to store the String representation for an enum value
     *
     * @param value string representing the enum value
     *
     * @see DeviceContextLineType#FIXEDLINE_VALUE
     * @see DeviceContextLineType#MOBILE_VALUE
     * @see DeviceContextLineType#UNKNOWN_VALUE
     */
    DeviceContextLineType(String value) {
        this.value = value;
    }

    /**
     * Parse a string representation to line-type
     *
     * @param value device context type as String to be parsed into line-type
     * @return {@link DeviceContextLineType}
     *
     * @see DeviceContextLineType#FIXEDLINE_VALUE
     * @see DeviceContextLineType#SYNONYM_LIST_FIXEDLINE_VALUES
     * @see DeviceContextLineType#MOBILE_VALUE
     * @see DeviceContextLineType#SYNONYM_LIST_MOBILE_VALUES
     * @see DeviceContextLineType#UNKNOWN_VALUE
     */
    @JsonCreator
    public static DeviceContextLineType of(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        String valueToSearch = value.toUpperCase(Locale.ROOT);

        if (SYNONYM_LIST_FIXEDLINE_VALUES.contains(valueToSearch)) {
            return FIXEDLINE;
        }
        if (SYNONYM_LIST_MOBILE_VALUES.contains(valueToSearch)) {
            return MOBILE;
        }
       return UNKNOWN;
    }

    /**
     * Return the string representation of a line-type enum value
     * @return (String) - line-type string representation
     *
     * @see DeviceContextLineType#FIXEDLINE_VALUE
     * @see DeviceContextLineType#MOBILE_VALUE
     * @see DeviceContextLineType#UNKNOWN_VALUE
     */
    @JsonValue
    public String toValue(){
        return value;
    }
}
