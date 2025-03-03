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
import de.telekom.phonenumbernormalizer.dto.DeviceContextMapper;
import de.telekom.phonenumbernormalizer.numberplans.PhoneLibWrapper;
import de.telekom.phonenumbernormalizer.numberplans.NumberPlan;
import de.telekom.phonenumbernormalizer.numberplans.NumberPlanFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 *  Concrete implementation of {@link PhoneNumberNormalizer} using {@link PhoneLibWrapper} to normalize a number by mitigating some inaccuracies when it comes to number plans of optional NDC and NAC as zero.
 *  <p>
 *  Also supports {@link DeviceContext} to enrich a phone number during normalization if the optional NDC is missing.
 *  </p>
 */
@RequiredArgsConstructor
@Component
public class PhoneNumberNormalizerImpl implements PhoneNumberNormalizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberNormalizerImpl.class);

    /**
     * Storage for {@link PhoneNumberNormalizer#setFallbackRegionCode(String)}
     */
    private String fallbackRegionCode = null;

    @Override
    public void setFallbackRegionCode(String fallBackRegionCode) {
        if (fallBackRegionCode != null && !fallBackRegionCode.isEmpty() && PhoneLibWrapper.getCountryCodeForRegion(fallBackRegionCode) > 0) {
            this.fallbackRegionCode = fallBackRegionCode;
        } else {
            this.fallbackRegionCode = null; //invalid region code!
        }
    }

    /**
     * Fallback normalization within the number-plan of the fallback region.
     * @param number the original number to be normalized
     * @param dialableNumber the original number reduced to dialable digits
     * @return E164 formatted phone number or at least a dialable version of the number
     *
     * @see PhoneNumberNormalizer#setFallbackRegionCode(String)
     * @see PhoneNumberNormalizer#normalizePhoneNumber(String, String)
     */
    private String fallbackNormalizationFromDeviceContextToDefaultRegionCode(String number, String dialableNumber) {
        if (this.fallbackRegionCode == null) {
            LOGGER.debug("Fallback Region was set!");
            return dialableNumber;
        } else {
            return this.normalizePhoneNumber(number, this.fallbackRegionCode);
        }
    }

    /**
     * Uses wrapper of Google's LibPhoneNumber to identify if special rules apply for normalization.<br/>
     * Using device context for enriching the number make it normalizable to E164 format if NDC is optional in the used number plan, but not used in the phone number to be normalized.
     * @param wrapper instanced wrapper of Google's LibPhoneNumber
     * @param deviceContext information like CC, NDC and {@link de.telekom.phonenumbernormalizer.dto.DeviceContextLineType} from which the number is dialled
     * @return E164 formatted phone number or dialable version of it or null
     */
    private String normalize(PhoneLibWrapper wrapper, DeviceContext deviceContext) {
        // international prefix has been added by Google's LibPhoneNumber even if it's not valid in the number plan.
        if (wrapper == null) {
            LOGGER.debug("PhoneLipWrapper was not initialized");
            return null;
        }

        if (wrapper.getSemiNormalizedNumber() == null) {
            return wrapper.getDialableNumber();
        }

        NumberPlan numberplan = null;
        if (deviceContext != null) {
            numberplan = NumberPlanFactory.INSTANCE.getNumberPlan(deviceContext.getLineType(), deviceContext.getCountryCode());
        }

        if (wrapper.isShortNumber(numberplan)) {
            //if it is a short number, we can't add area code nor country code, so returning the dialable.
            return wrapper.getDialableNumber();
        }

        if (wrapper.hasRegionNationalAccessCode() && deviceContext != null) {
            //Number plan is using a NationalPrefix aka Trunk Code ... so we could add Area Code if not included in the number.
            return wrapper.extendNumberByDefaultAreaCodeAndCountryCode(wrapper.getNationalAccessCode(), deviceContext.getNationalDestinationCode());
        }
        // Number plan is not using NationalPrefix aka Trunk Code ... its also not a short number, so country code can be added:
        return wrapper.getE164Formatted();
    }

    @Override
    public String normalizePhoneNumber(String number, String regionCode) {

        PhoneLibWrapper wrapper = new PhoneLibWrapper(number, regionCode);

        if (wrapper.getSemiNormalizedNumber() == null) {
            return wrapper.getDialableNumber();
        }
        if (wrapper.isShortNumber()) {
            //if it is a short number, we can't add area code nor country code, so returning the dialable.
            return wrapper.getDialableNumber();
        }

        // international prefix is added by the lib even if it's not valid in the number plan.
        //checking if the input number is equal to the nationalNumber based on number plan and trunk code logic.
        boolean hasNoCCAndNoNAC = wrapper.hasNoCountryCodeNorNationalAccessCode();

        LOGGER.debug("Number has no CC and no NAC: {}.", hasNoCCAndNoNAC);

        //if the number is definitely a short number or needs an area code but does not have it, we do not add the country code.
        return (hasNoCCAndNoNAC) ?
                wrapper.getDialableNumber() : wrapper.getE164Formatted();

    }

    @Override
    public String normalizePhoneNumber(String number, DeviceContext deviceContext) {

        // checking if the number has a special format or is not valid at all.
        PhoneLibWrapper normalizerPhoneNumber = new PhoneLibWrapper(number, null);
        if (! normalizerPhoneNumber.isNormalizingTried()) {
            return normalizerPhoneNumber.getDialableNumber();
        }

        DeviceContext normalizedDeviceContext = DeviceContextMapper.normalized(deviceContext);

        if (!normalizedDeviceContext.getCountryCode().equals(DeviceContext.UNKNOWN_VALUE)) {
            String regionCode = PhoneLibWrapper.getRegionCodeForCountryCode(normalizedDeviceContext.getCountryCode());
            // now working again with the region code
            normalizerPhoneNumber = new PhoneLibWrapper(number, regionCode);
            if (!normalizedDeviceContext.getNationalDestinationCode().equals(DeviceContext.UNKNOWN_VALUE)) {
                // Number needs normalization:
                return normalize(normalizerPhoneNumber, deviceContext);
            }
            // Device Context with CountryCode but without AreaCode ...
            if (!(PhoneLibWrapper.UNKNOWN_REGIONCODE.equals(regionCode))) {
                return this.normalizePhoneNumber(number, regionCode);
            }
        }
        LOGGER.debug("Normalization based on DeviceContext did not work - falling back to normalization with fallback region.");
        return this.fallbackNormalizationFromDeviceContextToDefaultRegionCode(number, normalizerPhoneNumber.getDialableNumber());
    }

}
