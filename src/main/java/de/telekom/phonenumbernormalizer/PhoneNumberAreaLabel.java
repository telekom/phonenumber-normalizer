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


import java.util.Optional;

/**
 * An interface for dependency injection - for direct use within your code just use {@link PhoneNumberAreaLabel}
 */
public interface PhoneNumberAreaLabel {

    /**
     * Get a location name for a E164 formatted number
     *
     * @param e164number number following E164 schema e.g. +4961511234567
     * @return nullable optional with either a national label or if non is available a country label
     */
    Optional<String> getLocationByE164Number(String e164number);

    /**
     * Get a location name for a nationalnumber and region code
     *
     * @param nationalNumber number without the country prefix like 61511234567 (for number +4961511234567)
     * @param regionCode region code for the number plan like de, us
     * @return nullable optional with location name if present
     */
    Optional<String> getLocationByNationalNumberAndRegionCode(String nationalNumber, String regionCode);

    /**
     * Get country name string by country code
     *
     * @param countryCode - specific telephony code of country (telephone number prefix) like 49 (Germany), 1 (US),
     * @return nullable optional with country name if present
     */
    Optional<String> getCountryNameByCountryCode(String countryCode);
}
