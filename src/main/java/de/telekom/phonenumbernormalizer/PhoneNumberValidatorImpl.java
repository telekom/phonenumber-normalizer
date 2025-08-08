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

import de.telekom.phonenumbernormalizer.dto.DeviceContextLineType;
import de.telekom.phonenumbernormalizer.numberplans.NumberPlan;
import de.telekom.phonenumbernormalizer.numberplans.NumberPlanFactory;
import de.telekom.phonenumbernormalizer.numberplans.PhoneNumberValidationResult;
import de.telekom.phonenumbernormalizer.numberplans.PhoneLibWrapper;
import de.telekom.phonenumbernormalizer.numberplans.ShortCodeUseable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 *  Concrete implementation of {@link PhoneNumberValidator} using {@link PhoneLibWrapper} to validate a number by mitigating some inaccuracies when it comes to number plans of optional NDC and NAC as zero.
 */
@RequiredArgsConstructor
@Component
public class PhoneNumberValidatorImpl implements PhoneNumberValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberValidatorImpl.class);


    private PhoneNumberValidationResult checkShortCodeOverlapping(NumberPlan numberplan, String numberToCheck, ShortCodeUseable mainSet, ShortCodeUseable oppositeSet,
                                                                  PhoneNumberValidationResult notUseableInMainSet, PhoneNumberValidationResult useableOnlyInMainSet,
                                                                  PhoneNumberValidationResult longerThanShortCode, PhoneNumberValidationResult shorterThanShortCode) {
        String shortNumberKey = numberplan.startingWithShortNumberKey(numberToCheck);
        if (shortNumberKey.length() > 0) {
            if (numberplan.isReserved(shortNumberKey)) {
                return null;
            }

            if (numberToCheck.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                if (!numberplan.isUsable(mainSet, shortNumberKey)) {
                    return notUseableInMainSet;
                } else {
                    if (numberplan.isUsable(oppositeSet, shortNumberKey)) {
                        return PhoneNumberValidationResult.IS_POSSIBLE;
                    } else {
                        return useableOnlyInMainSet;
                    }
                }
            } else {
                if (!numberplan.isUsable(mainSet, shortNumberKey) || !numberplan.isUsable(oppositeSet, shortNumberKey)) {
                    if (numberToCheck.length() < numberplan.getShortCodeLength(shortNumberKey)) {
                        return shorterThanShortCode;
                    } else {
                        return longerThanShortCode;
                    }
                } else {
                    if (numberToCheck.length() < numberplan.getShortCodeLength(shortNumberKey)) {
                        return PhoneNumberValidationResult.TOO_SHORT;
                    } else {
                        return PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER;  // similar to TOO_LONG, but more accurate
                    }
                }
            }
        }
        return null;
    }


    private PhoneNumberValidationResult checkExitCodeUsingNumber(PhoneLibWrapper wrapper, NumberPlan numberplan, String numberWithoutInitalExitCode,
                                                                 ShortCodeUseable mainSetIDPCC, ShortCodeUseable oppositeSetIDPCC,
                                                                 ShortCodeUseable mainSetIDPCCNDC, ShortCodeUseable oppositeSetIDPCCNDC,
                                                                 PhoneNumberValidationResult invalidInitialExitCode,
                                                                 PhoneNumberValidationResult mainSetResult){
        if (numberplan!=null) {

            PhoneNumberValidationResult isShortCodeDirectlyAfterInitalExitCode = checkShortCodeOverlapping(numberplan, numberWithoutInitalExitCode,
                    mainSetIDPCC, oppositeSetIDPCC,
                    invalidInitialExitCode, mainSetResult, null, null);

            if (isShortCodeDirectlyAfterInitalExitCode!=null) {
                return isShortCodeDirectlyAfterInitalExitCode;
            }

            if (! numberplan.isSupportingNDC()) {
                return null;
            }

            // Check for NDC after InitalExitCode:
            String ndc = numberplan.getNationalDestinationCodeFromNationalSignificantNumber(numberWithoutInitalExitCode);

            if (Objects.equals(ndc, "")) {
                return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;
            }

            if (numberplan.isNDCNationalOperatorOnly(ndc)) {
                return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_OPERATOR_ONLY;
            }

            String numberWithoutNationDestinationCode = numberWithoutInitalExitCode.substring(ndc.length());
            // Check for Shortnumber after NDC if NDC is Optional (<=> Fixline)
            if (numberplan.isNDCOptional(ndc)) {

                PhoneNumberValidationResult isShortCodeDirectlyAfterInitalExitCodeandNDC = checkShortCodeOverlapping(numberplan, numberWithoutNationDestinationCode,
                        mainSetIDPCCNDC, oppositeSetIDPCCNDC,
                        PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE, mainSetResult, PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER, PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER);

                if (isShortCodeDirectlyAfterInitalExitCodeandNDC!=null) {
                    return isShortCodeDirectlyAfterInitalExitCodeandNDC;
                } else {
                    if (numberplan.isReserved(numberWithoutNationDestinationCode))    {
                        return PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER;
                    }
                }

                // when NDC is optional, then number must not start with NAC again.
                String nac = wrapper.getNationalAccessCode();
                if (numberWithoutNationDestinationCode.startsWith(nac)) {
                    return PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER;
                }
            }

            if (numberplan.isNumberTooShortForNationalDestinationCode(ndc,numberWithoutNationDestinationCode)) {
                return PhoneNumberValidationResult.TOO_SHORT;
            }
            if (numberplan.isNumberTooLongForNationalDestinationCode(ndc,numberWithoutNationDestinationCode)) {
                return PhoneNumberValidationResult.TOO_LONG;
            }

        }
        return null;
    }

    @Override
    public PhoneNumberValidationResult isPhoneNumberPossibleWithReason(String number, String regionCode) {

        if (number == null || number.length()==0) {
            return PhoneNumberValidationResult.INVALID_LENGTH;
        }

        PhoneLibWrapper wrapper = new PhoneLibWrapper(number, regionCode);

        // TODO: change parameter regionCode to deviceContext
        NumberPlan numberplan = NumberPlanFactory.INSTANCE.getNumberPlan(DeviceContextLineType.UNKNOWN, String.valueOf(PhoneLibWrapper.getCountryCodeForRegion(regionCode)));

        if (wrapper.startsWithIDP()) {     // Country Exit Code is part
            // IDP indicates CC is used

            String numberCountryCode = wrapper.getCountryCode(false);

            String regionCountryCode = String.valueOf(PhoneLibWrapper.getCountryCodeForRegion(regionCode));
            if (regionCountryCode.equals("0")) {
                regionCountryCode = "";
            }

            String numberWithoutCountryCode = wrapper.removeIDP().substring(numberCountryCode.length());

            // using IDP as initial Exit Code
            PhoneNumberValidationResult isIDPNumberValid;

            if (regionCountryCode.equals(numberCountryCode)) {
                // Calling within the country
                isIDPNumberValid = checkExitCodeUsingNumber(wrapper, numberplan, numberWithoutCountryCode,
                        ShortCodeUseable.WITH_IDP_AND_CC_FROM_INSIDE, ShortCodeUseable.WITH_IDP_AND_CC_FROM_OUTSIDE,
                        ShortCodeUseable.WITH_IDP_AND_CC_AND_NDC_FROM_INSIDE, ShortCodeUseable.WITH_IDP_AND_CC_AND_NDC_FROM_OUTSIDE,
                        PhoneNumberValidationResult.INVALID_COUNTRY_CODE,
                        PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY);
            } else {
                // replacing the number plan by the one specified by the number's CC
                numberplan = NumberPlanFactory.INSTANCE.getNumberPlan(DeviceContextLineType.UNKNOWN, numberCountryCode);
                // calling from outside the country
                isIDPNumberValid = checkExitCodeUsingNumber(wrapper, numberplan, numberWithoutCountryCode,
                        ShortCodeUseable.WITH_IDP_AND_CC_FROM_OUTSIDE, ShortCodeUseable.WITH_IDP_AND_CC_FROM_INSIDE,
                        ShortCodeUseable.WITH_IDP_AND_CC_AND_NDC_FROM_OUTSIDE, ShortCodeUseable.WITH_IDP_AND_CC_AND_NDC_FROM_INSIDE,
                        PhoneNumberValidationResult.INVALID_COUNTRY_CODE,
                        PhoneNumberValidationResult.IS_POSSIBLE_INTERNATIONAL_ONLY);
            }

            if (isIDPNumberValid != null) {
               if (! isIDPNumberValid.isOverwritingReserve()) {
                    if (numberplan != null) {
                        PhoneNumberValidationResult specialRuling = numberplan.checkSpecialDefinitions(numberWithoutCountryCode);
                        if (specialRuling != null) {
                            return specialRuling;
                        }
                    }
               }
                return isIDPNumberValid;
            } else {
                    if (numberplan != null) {
                        PhoneNumberValidationResult specialRuling = numberplan.checkSpecialDefinitions(numberWithoutCountryCode);
                        if (specialRuling != null) {
                            return specialRuling;
                        }
                    }
            }



        } else {
            // No Country Exit Code has been used, so no CC is following.
            if (Objects.equals(wrapper.getNationalAccessCode(), "")) {
                // no NAC is used in region
                return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
            } else {
                // NAC can be used in region
                if (wrapper.startsWithNAC()) {
                    String numberWithOutNac = wrapper.removeNAC();

                    if (numberplan!=null) {
                        // check if a shortnumber is used directly after NAC and if that is allowed

                        // using NAC as initial Exit Code
                        PhoneNumberValidationResult isNACNumberValid = checkExitCodeUsingNumber(wrapper, numberplan, numberWithOutNac,
                                ShortCodeUseable.WITH_NAC, null,
                                ShortCodeUseable.WITH_NAC_AND_NDC, null,
                                PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE,
                                PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY);

                        if (isNACNumberValid != null) {

                            if (! isNACNumberValid.isOverwritingReserve()) {
                                PhoneNumberValidationResult specialRuling = numberplan.checkSpecialDefinitions(numberWithOutNac);
                                if (specialRuling != null) {
                                    return specialRuling;
                                }
                            }

                            return isNACNumberValid;
                        } else {
                                PhoneNumberValidationResult specialRuling = numberplan.checkSpecialDefinitions(numberWithOutNac);
                                if (specialRuling != null) {
                                    return specialRuling;
                                }
                        }
                    }
                    // As fallback check by libPhone
                    PhoneNumberValidationResult fallBackResult = wrapper.validate();

                    if ( (fallBackResult == PhoneNumberValidationResult.IS_POSSIBLE) ||
                            (fallBackResult == PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY) ||
                            // short number check e.g. AU 000 is short code which starts with NAC but is not treated as one:
                            ((fallBackResult == PhoneNumberValidationResult.TOO_SHORT) && (wrapper.isShortNumber()))
                    ) {
                        return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
                    }
                } else {
                    // NAC can be used in region, but is not.
                    if (numberplan==null) {
                        // ToDo: Is there a test with PhoneLib?
                        return PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY;
                    }

                    PhoneNumberValidationResult isShortCodeDirectly = checkShortCodeOverlapping(numberplan, wrapper.getDialableNumber(),
                            ShortCodeUseable.DIRECTLY, null,
                            PhoneNumberValidationResult.INVALID_LENGTH, PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY, PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER, PhoneNumberValidationResult.TOO_SHORT);

                    if (isShortCodeDirectly!=null) {
                        return isShortCodeDirectly;
                    } else {
                        if (numberplan.isReserved(wrapper.getDialableNumber()))    {
                            Integer lengthMatch = numberplan.isMatchingLength(wrapper.getDialableNumber());
                            if (lengthMatch!=null) {
                                if (lengthMatch>0) {
                                    return PhoneNumberValidationResult.TOO_SHORT;
                                }
                                if (lengthMatch<0) {
                                    return PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER;
                                }
                            }
                            return PhoneNumberValidationResult.INVALID_RESERVE_NUMBER;
                        }
                    }

                    return PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY;
                }
            }
        }

        // TODO: PhoneNumberValidationResult.INVALID_DRAMA_NUMBER;
        // TODO: PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_VPN_ONLY
        // TODO: PhoneNumberValidationResult.IS_POSSIBLE_VPN_ONLY
        // TODO: PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_OPERATOR_ONLY
        // TODO: PhoneNumberValidationResult.IS_POSSIBLE_OPERATOR_ONLY
        // TODO: PhoneNumberValidationResult.INVALID_INTERNATIONAL_DIALING_PREFIX
        // TODO: PhoneNumberValidationResult.INVALID_RESERVE_NUMBER

        return wrapper.validate();
    }

}