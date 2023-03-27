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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Helper class providing some static methods for setting up a DeviceContext instance with the correct value for unknown value if an attribute is not provided
 */
public class DeviceContextMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceContextMapper.class);

    /**
     * Since the class only provides some static methods, it mustn't be instantiated.
     * The initializer will always throw an IllegalStateException
     *
     * @see IllegalStateException
     */
    private DeviceContextMapper() {
        LOGGER.warn("DeviceContextMapper is a utility class and can't be initialised!");
        throw new IllegalStateException("DeviceContextMapper is a Utility class");
    }

    /**
     * This method takes a line-type and if its Null, returns DeviceContextLineType.UNKNOWN
     * @param lineType the line-type used in the DeviceContext
     * @return a valid {@link DeviceContextLineType} enum value - at least DeviceContextLineType.UNKNOWN
     *
     * @see DeviceContextLineType#UNKNOWN
     */
    private static DeviceContextLineType normalizeType(DeviceContextLineType lineType) {
        return Objects.requireNonNullElse(lineType, DeviceContextLineType.UNKNOWN);
    }

    /**
     * This method takes a country calling code. If that is empty, longer than three digits or contains non digit characters, it returns DeviceContext.UNKNOWN_VALUE
     * <p/>
     * There is no deep check if the CC is really assigned by the ITU!
     *
     * @param countryCode the country calling code used in the DeviceContext
     * @return a valid country calling code value - at least DeviceContext.UNKNOWN_VALUE
     *
     * @see DeviceContextLineType#UNKNOWN
     */
    private static String normalizeCountryCode(String countryCode) {
         if (StringUtils.isEmpty(countryCode)) {
            return DeviceContext.UNKNOWN_VALUE;
        } else if (StringUtils.isNumeric(countryCode)) {
             if (countryCode.length()>3) {
                 LOGGER.debug("Country Code has more than three digits: {}", countryCode);
                 return DeviceContext.UNKNOWN_VALUE;
             }
            // this is a valid country code which could be returned
            return countryCode;
        } else {
            // if a non digit character is included this might be ok, if the whole value matches UNKNOWN_VALUE
            if (! DeviceContext.UNKNOWN_VALUE.equalsIgnoreCase(countryCode) ) {
              LOGGER.debug("Country Code does not contain only digits: {}", countryCode);
            }
            return DeviceContext.UNKNOWN_VALUE;
        }
    }

    /**
     * This method takes a national destination code. If that is empty or contains non digit characters, it returns DeviceContext.UNKNOWN_VALUE
     * <p/>
     * There is no deep check if the NDC is assigned in the given CC number plan.
     *
     * @param nationalDestinationCode the national destination code used in the DeviceContext
     * @return a valid national destination code value - at least DeviceContext.UNKNOWN_VALUE
     *
     * @see DeviceContextLineType#UNKNOWN
     */
    private static String normalizeNationalDestinationCode(String nationalDestinationCode) {
        // DeviceContext.UNKNOWN_VALUE.equalsIgnoreCase(nationalDestinationCode) does not need to be checked, since it includes non digits which are covered by the regex.
        if (StringUtils.isEmpty(nationalDestinationCode)) {
            return DeviceContext.UNKNOWN_VALUE;
        } else if (StringUtils.isNumeric(nationalDestinationCode)) {
            return nationalDestinationCode;
        } else {
            LOGGER.debug("National Destination Code does not contain only digits: {}", nationalDestinationCode);
            return DeviceContext.UNKNOWN_VALUE;
        }
    }

    /**
     * This method creates a new DeviceContext object and copies the attributes of the given DeviceContext with a defined state.
     *
     * @param  context the object containing the parameters to be normalized.
     * @return {@link DeviceContext} new Object and copies of the parameters with a defined state.
     */
    public static DeviceContext normalized(DeviceContext context) {
        DeviceContext result = new DeviceContextDto();
        if (context==null) {
            result.setLineType(DeviceContextLineType.UNKNOWN);
            result.setCountryCode(DeviceContext.UNKNOWN_VALUE);
            result.setNationalDestinationCode(DeviceContext.UNKNOWN_VALUE);
        } else {
            result.setLineType(normalizeType(context.getLineType()));
            result.setCountryCode(normalizeCountryCode(context.getCountryCode()));
            result.setNationalDestinationCode(normalizeNationalDestinationCode(context.getNationalDestinationCode()));
        }
        return result;
    }

}
