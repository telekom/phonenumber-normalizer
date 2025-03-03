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


/**
 * This is an aggregation of attributes which define the context of a device in a telephony use case to
 * enable normalization of its telephone number even when optional NDC is not used.
 *
 * @see DeviceContextLineType
 */
public interface DeviceContext {

    /**
     * Indicates the value to be hold in an attribute is not known.
     *
     * @see DeviceContext#getCountryCode()
     * @see DeviceContext#setCountryCode(String)
     * @see DeviceContext#getNationalDestinationCode()
     * @see DeviceContext#setNationalDestinationCode(String)
     */
    String UNKNOWN_VALUE = "unknown";

    /**
     * Getter for the line-type the device is using
     *
     * @return the line-type the telephony device is using
     *
     * @see DeviceContext#setLineType(DeviceContextLineType)
     */
    DeviceContextLineType getLineType();

    /**
     * Setter for the line-type the device is using
     *
     * @param lineType the line-type the telephony device is using
     *
     * @see DeviceContext#getLineType()
     */
    void setLineType(DeviceContextLineType lineType);

    /**
     * Getter for the Country (Calling) Code of the countries number plan, where the device is originated.
     * Without international dialing prefix nor trunk code. If not known or not set, it should return DeviceContext.UNKNOWN_VALUE.
     * <p>
     * E.G. "49" for Germany
     * </p>
     * @return either a string containing one to three digits representing a country calling code or "unknown"
     *
     * @see DeviceContext#getCountryCode()
     * @see DeviceContext#UNKNOWN_VALUE
     */
    String getCountryCode();

    /**
     * Setter for the Country (Calling) Code of the countries number plan, where the device is originated.
     * Without international dealing prefix nor trunk code. If not known it should be set to DeviceContext.UNKNOWN_VALUE.
     * <p>
     * E.G. "49" for Germany
     * </p>
     * @param countryCode either a string containing one to three digits representing a country calling code or "unknown"
     *
     * @see DeviceContext#getCountryCode()
     * @see DeviceContext#UNKNOWN_VALUE
     */
    void setCountryCode(String countryCode);

    /**
     * Getter for the National Destination Code (NDC) of the countries number plan, where the device is originated.
     * Without National Access Code (NAC) nor trunk code. If not known or not set, it should return DeviceContext.UNKNOWN_VALUE.
     * <p>
     * E.G. "228" for Bonn in Germany where the Deutsche Telekom Headquarter is located
     * </p>
     * @return either a string containing a variable amount of digits representing a country calling code or "unknown"
     *
     * @see DeviceContext#setNationalDestinationCode(String)
     * @see DeviceContext#UNKNOWN_VALUE
     */
    String getNationalDestinationCode();

    /**
     * Setter for the National Destination Code (NDC) of the countries number plan, where the device is originated.
     * Without National Access Code (NAC) nor trunk code. If not known it should be set to DeviceContext.UNKNOWN_VALUE.
     * <p>
     * E.G. "228" for Bonn in Germany where the Deutsche Telekom Headquarter is located
     * </p>
     * @param nationalDestinationCode either a string containing a variable amount of digits representing a country calling code or "unknown"
     *
     * @see DeviceContext#getCountryCode()
     * @see DeviceContext#UNKNOWN_VALUE
     */
    void setNationalDestinationCode(String nationalDestinationCode);

}
