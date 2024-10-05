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
package de.telekom.phonenumbernormalizer.numberplans;


import com.google.i18n.phonenumbers.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Wrapper around the PhoneLib library from Google
 * <p>
 * Using reflection to access internal information to know if a region has a nation prefix &amp; which one it is or
 * which IDP is used.
 * </p><p>
 * Providing own NumberPlans logic as an alternative to PhoneLib ShortNumber.
 * </p>
 * @see NumberPlan
 */
@Data
public class PhoneLibWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneLibWrapper.class);

    public static final String UNKNOWN_REGIONCODE = "ZZ"; // see https://github.com/google/libphonenumber/blob/5e9507a46051405120bc73fcc13d0b0be1b93c29/java/libphonenumber/test/com/google/i18n/phonenumbers/RegionCode.java#L62

    /**
     * The given number reduced to characters which could be dialed
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    String dialableNumber;

    /**
     * The given number normalized with PhoneLib, risking we get a incorrect normalization
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#isNormalizingTried()
     * @see PhoneLibWrapper#getSemiNormalizedNumber()
     */
    Phonenumber.PhoneNumber semiNormalizedNumber;

    /**
     * The given region code for which the given number should be normalized.<br/>
     * This is an ISO2 code for the country.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    String regionCode;

    /**
     * The number plan metadata which PhoneLib is using for the given region code.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    Phonemetadata.PhoneMetadata metadata;

    /**
     * An instance of the PhoneLib short number utility.
     */
    private static final ShortNumberInfo shortNumberUtil = ShortNumberInfo.getInstance();

    /**
     * An instance of the PhoneLib number utility.
     */
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    /**
     * Storing if PhoneLib has been used to parse the given number into semiNormalizedNumber.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#semiNormalizedNumber
     * @see PhoneLibWrapper#isNormalizingTried()
     */
    private boolean isNormalizingTried = false;

    /**
     * Initialize the wrapper by giving a phone number to be analyzed against a number plan of a given region
     * @param number the phone number to be analyzed
     * @param regionCode the ISO2 Code of the Region / Country, which telephone number plan is used
     */
    public PhoneLibWrapper(String number, String regionCode) {
        this.regionCode = regionCode;
        this.metadata = getMetadataForRegion(this.regionCode);

        if (number != null) {
            this.dialableNumber = PhoneNumberUtil.normalizeDiallableCharsOnly(number);

            if (this.dialableNumber.isEmpty()) {
                this.dialableNumber = "";
            } else {
                if (!isSpecialFormat(dialableNumber)) {
                    // Number needs normalization:
                    // international prefix is added by the lib even if it's not valid in the number plan.
                    this.isNormalizingTried = true;
                    this.semiNormalizedNumber = PhoneLibWrapper.parseNumber(dialableNumber, regionCode);
                }
            }
        }
    }

    /**
     * If PhoneLib has been used to parse the given number into semiNormalizedNumber.
     *
     * @return {@link PhoneLibWrapper#isNormalizingTried}
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public boolean isNormalizingTried() {
        return isNormalizingTried;
    }

    /**
     * Using PhoneLib short number utility if it identifies the given number as a short number, which would not need a NAC.
     * <p>
     * This is a fallback for {@link PhoneLibWrapper#isShortNumber(NumberPlan)}, when we do not have an own number plan information.
     * </p>
     * @return if PhoneLib identifies given number as a short number
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#isShortNumber(NumberPlan)
     */
    public boolean isShortNumber() {
        return shortNumberUtil.isPossibleShortNumber(this.getSemiNormalizedNumber());
    }

    /**
     * Using own {@link NumberPlan} to identify if the given number is a short number, which would not need a NAC.
     * <p>
     * If no number plan is given, {@link PhoneLibWrapper#isShortNumber} is used as fallback.
     * </p>
     * @param numberplan the number plan we identified to be used for a check
     * @return if number plan or as fallback PhoneLib identifies given number as a short number
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public boolean isShortNumber(NumberPlan numberplan) {
        if (numberplan == null) {
            return this.isShortNumber();
        }
        return numberplan.isMatchingShortNumber(this.getDialableNumber());
    }

    /**
     * If we have a plain national number based on regions number plan and potential NAC logic.
     * <p>
     * For a number plan without NAC logic, it will always return false!
     * </p>
     * @return if given number could have CC and NAC, but does not have any of them.
     */
    public boolean hasNoCountryCodeNorNationalAccessCode() {
        // if given number has no NAC and no CC, it equals national phone number (without NAC).
        if (! Objects.equals(dialableNumber, this.getNationalPhoneNumberWithoutNationalAccessCode())) {
            return false;
        }
        // checking the regions number plan, if a NAC logic can be applied - if not there would be no option of having a NAC or not.
        return hasRegionNationalAccessCode();
    }

    /**
     * Using PhoneLib to get a E164 formatted representation of the given number
     * <p>
     * This is a straight invocation, so no compensation of some inaccuracy is done here.
     * </p>
     * @return E164 format of the given phone number
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public String getE164Formatted() {
        return phoneUtil.format(this.semiNormalizedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    /**
     * If we know the given region for the given number {@link PhoneLibWrapper#hasRegionNationalAccessCode()}, this method checks if the given number does not start with a NAC nor a CC,
     * so we could permanently add a default NDC and NAC to the given number and for this new value the method directly return a E164 formatted representation.
     * @param nationalAccessCode the NAC to be added e.g. for Germany it would be "0"
     * @param defaultNationalDestinationCode the NDC to be added depending on the use telephone line origination.
     * @return if possible a E164 formatted representation or just the diallable representation of the given number.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public String extendNumberByDefaultAreaCodeAndCountryCode(String nationalAccessCode, String defaultNationalDestinationCode) {
        String nationalPhoneNumberWithoutNationalAccessCode = this.getNationalPhoneNumberWithoutNationalAccessCode();
        //if the dialableNumber is same as the national Number, Without NationalPrefix, then there is no NDC, so it needs to be added.
        if (Objects.equals(dialableNumber, nationalPhoneNumberWithoutNationalAccessCode)) {

            String extendedNumber = nationalAccessCode + defaultNationalDestinationCode + nationalPhoneNumberWithoutNationalAccessCode;

            try {
                this.semiNormalizedNumber = phoneUtil.parse(extendedNumber, regionCode);
                // after area code has been added, we can add the country code by the lib:
                return getE164Formatted();
            } catch (NumberParseException e) {
                LOGGER.warn("could not parse extended number: {}", extendedNumber);
                LOGGER.debug("{}", e.getMessage());
                return dialableNumber;
            }
        } else {
            //it seems we have nationalnumber with national prefix, so we could add country code:
            return getE164Formatted();
        }
    }

    /**
     * Some Special dial-able characters make a number either not necessary to be normalized ("+" is already normalized) or can't be normalized ("*" control codes)
     * @param value phone number representation
     * @return if phone number starts with special characters which makes normalization unable / not necessary
     */
    static boolean isSpecialFormat(String value) {
        //+: Number is already in "+" ... International Format:
        //*: Number is internal and cannot be normalized
        if (value == null || value.length()==0) {
            return false;
        }
        return ("+".equals(value.substring(0, 1))) || ("*".equals(value.substring(0, 1)));
    }

    /**
     * Checks if a given number starts with the given IDP (or the international IDP short form '+')
     * @param value the number to be checked
     * @param idp the IDP to be used searched for
     * @return if either given IDP or '+' is the beginning of the value
     */
    private static boolean isIDPUsed(String value, String idp) {
        if (idp == null || idp.length()==0) {
            return ("+".equals(value.substring(0, 1)));
        }

        return (("+".equals(value.substring(0, 1))) || (value.startsWith(idp)));
    }

    /**
     * Checks if a given number starts with the IDP (or the international IDP short form '+') of the given region
     * @param value the number to be checked
     * @param regionCode ISO2 code for the regions number plan used for checking IDP
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public static boolean startsWithIDP(String value, String regionCode) {
        if (value == null || value.length()==0) {
            return false;
        }

        String idp = getInternationalDialingPrefix(regionCode);

        return isIDPUsed(value, idp);
    }

    /**
     * Checks if the number starts with the IDP (or the international IDP short form '+') of the initializing region
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public boolean startsWithIDP() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();

        return isIDPUsed(this.dialableNumber, idp);
    }

    /**
     * Checks if the number starts with the NAC of the initializing region
     * Be aware, that some regions have IDP of 00 and NAC of 0 - so overlaping is also checked.
     */
    public boolean startsWithNAC() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();
        String nac = this.getNationalAccessCode();

        if (idp.startsWith(nac) && dialableNumber.startsWith(idp)) {
            return false;

        }

        return dialableNumber.startsWith(nac);

    }

    /**
     * Use PhoneLib to parse a number for a regions code. If any exception occurs, they are logged and null is returned.
     * @param number the phone number to be parsed
     * @param regionCode ISO2 code for the regions number plan used for parsing the number
     * @return either the parsed {@link Phonenumber.PhoneNumber} or null
     */
    private static Phonenumber.PhoneNumber parseNumber(String number, String regionCode) {
        try {
            return phoneUtil.parse(number, regionCode);
            // international prefix is added by the lib even if it's not valid in the number plan.
        } catch (NumberParseException e) {
            LOGGER.warn("could not parse normalize number: {}", number);
            LOGGER.debug("{}", e.getMessage());
            return null;
        }
    }


    private static String internationalDialingPrefix(Phonemetadata.PhoneMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        return metadata.getInternationalPrefix();
    }

    /**
     * The International Dialing Prefix used in the given region from PhoneLib
     * @return IDP of given {@link PhoneLibWrapper#regionCode}
     */
    public String getInternationalDialingPrefix() {
        return internationalDialingPrefix(this.metadata);
    }

    /**
     * The International Dialing Prefix used in the given region from PhoneLib
     *
     * @param regionCode the Region which NAC is requested.
     * @return IDP of given regionCode
     */
    static public String getInternationalDialingPrefix(String regionCode) {
        return internationalDialingPrefix(getMetadataForRegion(regionCode));
    }


    private static String nationalAccessCode(Phonemetadata.PhoneMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        return metadata.getNationalPrefix();
    }

    /**
     * The National Access Code used before the National Destination Code in the given region from PhoneLib
     * @return NAC of given {@link PhoneLibWrapper#regionCode}
     */
    public String getNationalAccessCode() {
        return nationalAccessCode(this.metadata);
    }

    /**
     * The National Access Code used before the National Destination Code in the given region from PhoneLib
     *
     * @param regionCode the Region which NAC is requested.
     * @return NAC of given regionCode
     */
    static public String getNationalAccessCode(String regionCode) {
        return nationalAccessCode(getMetadataForRegion(regionCode));
    }

    /**
     * From PhoneLib, if a National Access Code is used before the National Destination Code in the given region
     * @return if given {@link PhoneLibWrapper#regionCode} is using NAC
     */
    public boolean hasRegionNationalAccessCode() {
        return metadata != null && metadata.hasNationalPrefix();
    }

    /**
     * Since we need the PhoneMetadta for fixing calculation of some number normalization,
     * we need to break encapsulation via reflection, because that data is private to phoneUtil
     * and Google rejected suggestion to make it public, because they did not see our need in correcting normalization.
     * @return {@link Phonemetadata.PhoneMetadata} of {@link PhoneLibWrapper#regionCode}
     */
    static private Phonemetadata.PhoneMetadata getMetadataForRegion(String regionCode) {
        try {
            Method m = phoneUtil.getClass().getDeclaredMethod("getMetadataForRegion", String.class);
            // violating encupsulation is intended by this method, so no need for SONAR code smell warning here
            m.setAccessible(true); //NOSONAR
            return (Phonemetadata.PhoneMetadata) m.invoke(phoneUtil, regionCode);
        } catch (Exception e) {
            LOGGER.warn("Error while accessing getMetadataForRegion on PhoneNumberUtil via Reflection.");
            LOGGER.debug("{}", e.getMessage());
            return null;
        }
    }

    /**
     * Using PhoneLib to get the national number from the given number
     *
     * @return national number without NAC, but any other leading zero.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#getSemiNormalizedNumber()
     * @see PhoneLibWrapper#nationalPhoneNumberWithoutNationalPrefix(Phonenumber.PhoneNumber)
     */
    private String getNationalPhoneNumberWithoutNationalAccessCode() {
        return PhoneLibWrapper.nationalPhoneNumberWithoutNationalPrefix(this.semiNormalizedNumber);
    }

    /**
     * Using PhoneLib to get the national number from a parsed phone number with leading zeros, if those are not representing a National Access Code.
     * <p/>
     * This is necessary, because PhoneLib is storing the national number as a long, so leading "0" Digits as part of it are stored in other attributes.
     * @param phoneNumber A PhoneLib parsed phone number
     * @return national number part without NationalPrefix (aka NAC) but any other leading zero.
     */
    private static String nationalPhoneNumberWithoutNationalPrefix(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber==null) {
            return null;
        }
        StringBuilder nationalNumber = new StringBuilder(Long.toString(phoneNumber.getNationalNumber()));
        // if-clause necessary, because getNumberOfLeadingZeros is always 1 for a possible trunc code and special 0 in Italy
        if (phoneNumber.hasNumberOfLeadingZeros() || phoneNumber.hasItalianLeadingZero())
            for (int i = 0; i < phoneNumber.getNumberOfLeadingZeros(); i++) {
                nationalNumber.insert(0, "0");
            }
        return nationalNumber.toString();
    }

    /**
     * Using PhoneLib to get the Country Calling Code for a region code
     * <p>
     * e.g. "DE" is "49"
     * </p>
     * @param regionCode ISO2 code of a region
     * @return country calling code of the region or 0 if regionCode is invalid.
     */
    public static int getCountryCodeForRegion(String regionCode) {
        return phoneUtil.getCountryCodeForRegion(regionCode);
    }

    /**
     * Using PhoneLib to get the region code for a Country Calling Code
     * <p>
     * e.g. "49" is "DE"
     * </p>
     * @param countryCode only digits without IDP
     * @return regionCode or {@link PhoneLibWrapper#UNKNOWN_REGIONCODE} if countryCode is invalid.
     */
    public static String getRegionCodeForCountryCode(String countryCode) {
        try {
            return phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        } catch (Exception e) {
            LOGGER.info("Error while parsing Country Code: {}", countryCode);
            LOGGER.debug("{}", e.getMessage());
            return PhoneLibWrapper.UNKNOWN_REGIONCODE;
        }
    }


    /**
     * Using PhoneLib to check the number by isPossibleWithReason code. If number has been parsed during initialization
     * this is a straight invocation, so no compensation of some inaccuracy is done here. Otherwise, parsing is done
     * locally and exceptions are directly mapped to a result.
     * </p>
     * @return PhoneNumberUtil.ValidationResult which is PhoneLib isPossible Reason code
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    private PhoneNumberUtil.ValidationResult isPossibleWithReason() {
        if (semiNormalizedNumber == null) {
            try {
                Phonenumber.PhoneNumber tempNumber = phoneUtil.parse(dialableNumber, regionCode);
                return phoneUtil.isPossibleNumberWithReason(tempNumber);
                // international prefix is added by the lib even if it's not valid in the number plan.
            } catch (NumberParseException e) {
                LOGGER.info("could not parse normalize number: {}", dialableNumber);
                LOGGER.debug("{}", e.getMessage());

                switch (e.getErrorType()) {
                    case INVALID_COUNTRY_CODE:
                        return PhoneNumberUtil.ValidationResult.INVALID_COUNTRY_CODE;
                    case TOO_SHORT_NSN:
                        return PhoneNumberUtil.ValidationResult.TOO_SHORT;
                    case TOO_SHORT_AFTER_IDD:
                        return PhoneNumberUtil.ValidationResult.TOO_SHORT;
                    case TOO_LONG:
                        return PhoneNumberUtil.ValidationResult.TOO_LONG;
                    default:
                        // NOT_A_NUMBER
                        return PhoneNumberUtil.ValidationResult.INVALID_LENGTH;
                }
            }
        }
        return phoneUtil.isPossibleNumberWithReason(semiNormalizedNumber);
    }


    /**
     * Using PhoneLib to check the number by isPossibleWithReason code by internal wrapper method isPossibleWithReason
     * and map the result to PhoneNumberValidationResult type
     *
     * @return PhoneNumberValidationResult
     *
     * @see PhoneLibWrapper#isPossibleWithReason()
     * @see PhoneNumberValidationResult
     */
    public PhoneNumberValidationResult validate() {
        return PhoneNumberValidationResult.byPhoneLibValidationResult(isPossibleWithReason());
    }


}
