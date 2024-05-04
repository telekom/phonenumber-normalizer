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
import de.telekom.phonenumbernormalizer.numberplans.PhoneLibWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 *  Concrete implementation of {@link PhoneNumberValidator} using {@link PhoneLibWrapper} to validate a number by mitigating some inaccuracies when it comes to number plans of optional NDC and NAC as zero.
 */
@RequiredArgsConstructor
@Component
public class PhoneNumberValidatorImpl implements PhoneNumberValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberValidatorImpl.class);


    @Override
    public PhoneNumberValidationResult isPhoneNumberPossibleWithReason(String number, String regionCode) {

        if (number == null || number.length()==0) {
            return PhoneNumberValidationResult.INVALID_LENGTH;
        }

        PhoneLibWrapper wrapper = new PhoneLibWrapper(number, regionCode);

        if (wrapper.startsWithIDP()) {     // Country Exit Code is part
            // IDP indicates CC is used
            return wrapper.validate();
            //return PhoneNumberValidationResult.IS_POSSIBLE;
        } else {
            // No Country Exit Code has been used, so no CC is following.
            if (wrapper.getNationalAccessCode()=="") {
                // no NAC is used in region
                return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
            } else {
                // NAC can be used in region
                if (wrapper.startsWithNAC()) {
                    return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
                } else {
                    return PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY;
                }
            }
        }

        // boolean hasNoCCAndNoNAC = wrapper.hasNoCountryCodeNorNationalAccessCode();

        // return PhoneNumberValidationResult.INVALID_DRAMA_NUMBER;

        // return wrapper.validate();
    }

}
