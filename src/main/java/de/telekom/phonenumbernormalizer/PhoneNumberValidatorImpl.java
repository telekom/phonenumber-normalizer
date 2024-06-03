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

            if (regionCountryCode.equals(numberCountryCode)) {
                // Calling within the country


                if (numberplan!=null) {

                    // Check for ShortNumber directly after CC
                    String shortNumberKey = numberplan.startingWithShortNumberKey(numberWithoutCountryCode);
                    if (shortNumberKey.length() > 0) {
                        if (numberWithoutCountryCode.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                            if (!numberplan.isUsableWithIDPandCCfromInside(shortNumberKey)) {
                                return PhoneNumberValidationResult.INVALID_COUNTRY_CODE;
                            } else {
                                if (numberplan.isUsableWithIDPandCCfromOutside(shortNumberKey)) {
                                    return PhoneNumberValidationResult.IS_POSSIBLE;
                                } else {
                                    return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
                                }
                            }
                        }  // else path of invalid NDC is checked explicitly here after also for non short number cases.
                    }

                    // Check for NDC after CC:
                    String ndc = numberplan.getNationalDestinationCodeFromNationalSignificantNumber(numberWithoutCountryCode);

                    if (Objects.equals(ndc, "")) {
                        return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;  // TODO: What about a Numberplan without NDCs?
                    }

                    String numberWithoutNationDestinationCode = numberWithoutCountryCode.substring(ndc.length());
                    // Check for Shortnumber after NDC if NDC is Optional (<=> Fixline)
                    if (numberplan.isNDCOptional(ndc)) {
                        shortNumberKey = numberplan.startingWithShortNumberKey(numberWithoutNationDestinationCode);
                        if (shortNumberKey.length() > 0) {
                            if (numberWithoutNationDestinationCode.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                                if (!numberplan.isUsableWithIDPandCCandNDCfromInside(shortNumberKey)) {
                                    return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;
                                } else {
                                    if (numberplan.isUsableWithIDPandCCandNDCfromOutside(shortNumberKey)) {
                                        return PhoneNumberValidationResult.IS_POSSIBLE;
                                    } else {
                                        return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
                                    }
                                }
                            } else {
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

            } else {

                numberplan = NumberPlanFactory.INSTANCE.getNumberPlan(DeviceContextLineType.UNKNOWN, numberCountryCode);
                // calling from outside the country
                if (numberplan!=null) {

                    // Check for ShortNumber directly after CC
                    String shortNumberKey = numberplan.startingWithShortNumberKey(numberWithoutCountryCode);
                    if (shortNumberKey.length() > 0) {
                        if (numberWithoutCountryCode.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                            if (!numberplan.isUsableWithIDPandCCfromOutside(shortNumberKey)) {
                                return PhoneNumberValidationResult.INVALID_COUNTRY_CODE;
                            } else {
                                if (numberplan.isUsableWithIDPandCCfromInside(shortNumberKey)) {
                                    return PhoneNumberValidationResult.IS_POSSIBLE;
                                } else {
                                    return PhoneNumberValidationResult.IS_POSSIBLE_INTERNATIONAL_ONLY;
                                }
                            }
                        }  // else path of invalid NDC is checked explicitly here after also for non short number cases.
                    }

                    // Check for NDC after CC:
                    String ndc = numberplan.getNationalDestinationCodeFromNationalSignificantNumber(numberWithoutCountryCode);

                    if (Objects.equals(ndc, "")) {
                        return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;  // TODO: What about a Numberplan without NDCs?
                    }

                    String numberWithoutNationDestinationCode = numberWithoutCountryCode.substring(ndc.length());
                    // Check for Shortnumber after NDC if NDC is Optional (<=> Fixline)
                    if (numberplan.isNDCOptional(ndc)) {
                        shortNumberKey = numberplan.startingWithShortNumberKey(numberWithoutNationDestinationCode);
                        if (shortNumberKey.length() > 0) {
                            if (numberWithoutNationDestinationCode.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                                if (!numberplan.isUsableWithIDPandCCandNDCfromOutside(shortNumberKey)) {
                                    return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;
                                } else {
                                    if (numberplan.isUsableWithIDPandCCandNDCfromInside(shortNumberKey)) {
                                        return PhoneNumberValidationResult.IS_POSSIBLE;
                                    } else {
                                        return PhoneNumberValidationResult.IS_POSSIBLE_INTERNATIONAL_ONLY;
                                    }
                                }
                            } else {
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

            }

            // return wrapper.validate();
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
                        String shortNumberKey = numberplan.startingWithShortNumberKey(numberWithOutNac);
                        if (shortNumberKey.length() > 0) {
                            if (numberWithOutNac.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                                if (!numberplan.isUsableWithNAC(shortNumberKey)) {
                                    return PhoneNumberValidationResult.INVALID_NATIONAL_ACCESS_CODE;
                                } else {
                                    return PhoneNumberValidationResult.IS_POSSIBLE_NATIONAL_ONLY;
                                }
                            }  // else path of invalid NDC is checked explicitly here after also for non short number cases.
                        }

                        // Check for NDC after Nac:
                        String ndc = numberplan.getNationalDestinationCodeFromNationalSignificantNumber(numberWithOutNac);

                        if (Objects.equals(ndc, "")) {
                            return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;  // TODO: What about a Numberplan without NDCs?
                        }
                        String numberWithoutNationDestinationCode = numberWithOutNac.substring(ndc.length());
                        // Check for Shortnumber after NDC if NDC is Optional (<=> Fixline)
                        if (numberplan.isNDCOptional(ndc)) {
                            shortNumberKey = numberplan.startingWithShortNumberKey(numberWithoutNationDestinationCode);
                            if (shortNumberKey.length() > 0) {
                                if (numberWithoutNationDestinationCode.length() == numberplan.getShortCodeLength(shortNumberKey)) {
                                    if (!numberplan.isUsableWithNACandNDC(shortNumberKey)) {
                                        return PhoneNumberValidationResult.INVALID_NATIONAL_DESTINATION_CODE;
                                    } else {
                                        return PhoneNumberValidationResult.IS_POSSIBLE; // TODO: check if only international
                                    }
                                } else {
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

                    String shortNumberKey = numberplan.startingWithShortNumberKey(wrapper.getDialableNumber());
                    if (shortNumberKey.length()>0) {
                        if (!numberplan.isUsableDirectly(shortNumberKey)) {
                            return PhoneNumberValidationResult.INVALID_LENGTH;
                        } else {
                            if (wrapper.getDialableNumber().length() == numberplan.getShortCodeLength(shortNumberKey)) {
                                return PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY;
                            } else {
                                return PhoneNumberValidationResult.INVALID_PREFIX_OF_SUBSCRIBER_NUMBER;
                            }
                        }
                    }

                    return PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY;
                }
            }
        }

        // boolean hasNoCCAndNoNAC = wrapper.hasNoCountryCodeNorNationalAccessCode();

        // return PhoneNumberValidationResult.INVALID_DRAMA_NUMBER;

         return wrapper.validate();
    }

}
