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
package de.telekom.phonenumbernormalizer;


import de.telekom.phonenumbernormalizer.dto.DeviceContext;

/**
 * An interface for dependency injection - for direct use within your code just use {@link PhoneNumberNormalizerImpl}.
 */
public interface PhoneNumberNormalizer {

    /**
     * Sets the ISO2 country code, which is used if the {@link DeviceContext} does not indicate one.
     * The country might represent a region, like "US" for North America.
     *
     * @param fallBackRegionCode ISO2 code of the country
     *
     * @see PhoneNumberNormalizer#normalizePhoneNumber(String, DeviceContext)
     */
    void setFallbackRegionCode(String fallBackRegionCode);

    /**
     * Normalizes the number using PhoneLib with some additions to compensate.
     * <p>
     * Preferable to {@link PhoneNumberNormalizer#normalizePhoneNumber(String, String)}, because default NDC can be provided, so that more compensation for generating a valid E164 can be done.
     * </p>
     * @param number plain number to normalize
     * @param deviceContext  information like CC, NDC and {@link de.telekom.phonenumbernormalizer.dto.DeviceContextLineType} from which the number is dialled
     * @return E164 formatted phone number or at least a dialable version of the number
     *
     * @see PhoneNumberNormalizer#setFallbackRegionCode(String)
     */
    String normalizePhoneNumber(String number, DeviceContext deviceContext);

    /**
     * Normalizes the number using PhoneLib with some additions to compensate.
     * <p>
     * Not as powerful as {@link PhoneNumberNormalizer#normalizePhoneNumber(String, DeviceContext)}, because no default NDC can be set.
     * </p>
     * @param number plain number to normalize
     * @param regionCode ISO2 code of the country, which number-plan is used for normalization
     * @return E164 formatted phone number or at least a dialable version of the number
     */
    String normalizePhoneNumber(String number, String regionCode);
}
