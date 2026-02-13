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
package de.telekom.phonenumbernormalizer;

import de.telekom.phonenumbernormalizer.numberplans.PhoneNumberValidationResult;
import de.telekom.phonenumbernormalizer.dto.DeviceContext;

/**
 * An interface for dependency injection - for direct use within your code just use {@link PhoneNumberValidatorImpl}.
 */
public interface PhoneNumberValidator {

    /**
     * Validates the number using PhoneLib with some additions to compensate.
     * @param number plain number to validate
     * @param regionCode ISO2 code of the country, which number-plan is used for normalization
     * @return PhoneNumberValidationResult reason if the number is possible (and maybe its limited context) or why not.
     */
    PhoneNumberValidationResult isPhoneNumberPossibleWithReason(String number, String regionCode);
}
