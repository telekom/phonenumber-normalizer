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
package de.telekom.phonenumbernormalizer.numberplans;

import com.google.i18n.phonenumbers.PhoneNumberUtil.ValidationResult;

/**
 * Wrapper around Google's LibPhoneNumber enum {@link ValidationResult} from Google
 * <p>
 * When Google's LibPhoneNumber is validating a phone number it returns a value of the enum {@link ValidationResult}.
 * </p><p>
 * It differentiates two possible positive and five possible negative results. The value
 * {@link ValidationResult#INVALID_LENGTH} is for any negative case, which is not explicitly covered by any of the other
 * four values.
 * While most of the values for negative cases are focused on the number length, the value
 * {@link ValidationResult#INVALID_COUNTRY_CODE} explicitly focuses on a specific number part - the Country Code.
 * </p><p>
 *  This wrapper introduces explicit INVALID_ reason codes for currently undifferentiated number parts IDP, NAC and NDC.
 *  Lastly there is also {@link PhoneNumberValidationResult#INVALID_DRAMA_NUMBER} reason code for the whole number if it
 *  is reserved as a fictional number in arts.
 *  </p><p>
 *  Furthermore it introduces additional IS_POSSIBLE_ values, to hint for more specific calling restrictions of the
 *  validated number besides {@link PhoneNumberValidationResult#IS_POSSIBLE_LOCAL_ONLY}.
 * </p>
 * @see ValidationResult
 */

public enum PhoneNumberValidationResult {

    /** The number length matches that of valid numbers for this region or used NDC without any identified calling
     * restrictions. */
    IS_POSSIBLE(ValidationResult.IS_POSSIBLE),

    /**
     * The number length/pattern matches that of local numbers for this region only (i.e. numbers that may
     * be able to be dialled within an area, but does not have all the information e.g. NDC or is not allowed to be
     * dialled from anywhere inside or outside the country).
     */
    IS_POSSIBLE_LOCAL_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),

    /**
     * The number length/pattern matches that of national numbers for this region only (i.e. numbers that may
     * be able to be dialled within an area or the region itself, but is not allowed to be dialled from
     * anywhere outside the country using CC).
     */
    IS_POSSIBLE_NATIONAL_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),

    /**
     * The number length/pattern matches that of international number for this region only (i.e. number is not allowed
     * to be call from within the region, even if CC of the regions is used).
     */
    IS_POSSIBLE_INTERNATIONAL_ONLY(ValidationResult.IS_POSSIBLE),

    /**
     * The number length/pattern matches that of national VPN number for this region only (i.e. number is only allowed
     * to be call from other numbers of same VPN within this region and not by users of the public telephony network).
     */
    IS_POSSIBLE_NATIONAL_VPN_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),

    /**
     * The number length/pattern matches that of VPN number for this region only (i.e. number is only allowed to be call
     * from other numbers of same VPN even internationally but not by users of the public telephony network).
     */
    IS_POSSIBLE_VPN_ONLY(ValidationResult.IS_POSSIBLE),

    /**
     * The number length/pattern matches that of national operator traffic control numbers for this region only
     * (i.e. number is only allowed to be call between national operators for call routing and not directly by users of
     * the public telephony network).
     */
    IS_POSSIBLE_NATIONAL_OPERATOR_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),

    /**
     * The number length/pattern matches that of operator traffic control numbers for this region only (i.e. number is
     * only allowed to be call between operators for call routing and not directly by users of the public telephony
     * network).
     */
    IS_POSSIBLE_OPERATOR_ONLY(ValidationResult.IS_POSSIBLE),

    /** The number has an invalid international dialing prefix (aka IDP) for this region. */
    INVALID_INTERNATIONAL_DIALING_PREFIX(ValidationResult.INVALID_LENGTH),

    /** The number has an invalid country calling code (aka CC) or the specific number must not be used with used CC.*/
    INVALID_COUNTRY_CODE(ValidationResult.INVALID_COUNTRY_CODE),

    /** The number has an invalid national access code (aka NAC) or the specific number must not be used with used NAC.*/
    INVALID_NATIONAL_ACCESS_CODE(ValidationResult.INVALID_LENGTH),

    /**
     * The number has an invalid national destination code (aka NDC) for this region or the specific number must not be used with used NDC:
     * <ul>
     * <li>The regulating authority has left out a complete NDC-Block (so its unusable)</li>
     * <li>The regulating authority has defined a complete NDC-Block as not usable</li>
     * </ul>
     * Exception: If the regulating authority has defined a complete NDC-Block as reserved, then it is {@link PhoneNumberValidationResult#INVALID_RESERVE_NUMBER}
     * <p/>
     * If the regulating authority has defined a NDC-Block for a specific use (e.g. mobile operators) and a subpart of the block is:
     * <ul>
     * <li>reserved e.g. for a specific operator but not in use
     * <li>free e.g. neither assigned nor reserved for an operator</li>
     * <li>if a complete assigned, reserved and free list exists, but the subpart is on none of them</li>
     * </ul>
     * */
    // TODO: Check if subparts other than assigned (reserved, free, unspecified) are getting an own result values
    INVALID_NATIONAL_DESTINATION_CODE(ValidationResult.INVALID_LENGTH),

    /** The subscriber number starts with digits which makes the number invalid, e.g. overlapping special numbers when NDC is optional, so those numbers could not be distinct in digit by digit calling from those special numbers
     *  - If Region is using NAC and NDC is optional, the number must not start with NAC
     *  - If Region is using shortnumbers valid only without any prefix and NDC is optional, the number must not start with a prefix equal to those shortnumbers
     * */
    INVALID_PREFIX_OF_SUBSCRIBER_NUMBER(ValidationResult.INVALID_LENGTH),

    /** The region is using a definition for a number (range), which matches for the number, but the definition is marked as reserve for future use. So currently it is not a valid number */
    INVALID_RESERVE_NUMBER(ValidationResult.INVALID_LENGTH),

    /** The number is shorter than all valid numbers for this region or used NDC. */
    TOO_SHORT(ValidationResult.TOO_SHORT),

    /**
     * The number is longer than the shortest valid numbers for this region or used NDC, shorter than the
     * longest valid numbers for this region or used NDC, and does not itself have a number length that matches
     * valid numbers for this region. This can also be returned in the case where
     * isPossibleNumberForTypeWithReason was called, and there are no numbers of this type at all
     * for this region or used NDC while none of the other INVALID values would match.
     */
    INVALID_LENGTH(ValidationResult.INVALID_LENGTH),

    /** The number is longer than all valid numbers for this region, or for the used NDC. */
    TOO_LONG(ValidationResult.TOO_LONG),

    /** The number is matching a drama number range, which simulates valid number for this region only used in movies or
     *  other fictional story telling. */
    INVALID_DRAMA_NUMBER(ValidationResult.INVALID_LENGTH);

    /**
     * storing the corresponding enum value of {@link ValidationResult}
     */
    private final ValidationResult phoneLibResult;

    /**
     * Initializing a PhoneNumberValidationResult enum value with a corresponding {@link ValidationResult} enum value
     * @param phoneLibResult corresponding {@link ValidationResult} enum value
     */
    PhoneNumberValidationResult(ValidationResult phoneLibResult) {
        this.phoneLibResult = phoneLibResult;
    }

    /**
     * Returns best matching corresponding {@link ValidationResult} enum value for an instance of a
     * {@link PhoneNumberValidationResult} enum value
     * @return corresponding {@link ValidationResult} enum value
     */
    public ValidationResult getPhoneLibValidationResult() {
        return phoneLibResult;
    }

    public static PhoneNumberValidationResult byPhoneLibValidationResult(ValidationResult result) {
        switch(result){
            case IS_POSSIBLE:
                return PhoneNumberValidationResult.IS_POSSIBLE;
            case IS_POSSIBLE_LOCAL_ONLY:
                return PhoneNumberValidationResult.IS_POSSIBLE_LOCAL_ONLY;
            case INVALID_LENGTH:
                return PhoneNumberValidationResult.INVALID_LENGTH;
            case INVALID_COUNTRY_CODE:
                return PhoneNumberValidationResult.INVALID_COUNTRY_CODE;
            case TOO_SHORT:
                return PhoneNumberValidationResult.TOO_SHORT;
            case TOO_LONG:
                return PhoneNumberValidationResult.TOO_LONG;
        }
        return null;
    }

    /**
     * Returns if the validation result identifies a possible number regardless of calling limitations
     * @return boolean true for any IS_POSSIBLE(_xxx) enum value
     */
    public boolean isSomeHowValid() {
        return (   (this == IS_POSSIBLE)
                || (this == IS_POSSIBLE_LOCAL_ONLY)
                || (this == IS_POSSIBLE_NATIONAL_ONLY)
                || (this == IS_POSSIBLE_NATIONAL_VPN_ONLY)
                || (this == IS_POSSIBLE_NATIONAL_OPERATOR_ONLY)
                || (this == IS_POSSIBLE_INTERNATIONAL_ONLY)
                || (this == IS_POSSIBLE_VPN_ONLY)
                || (this == IS_POSSIBLE_OPERATOR_ONLY));
    }

    /**
     * Returns if the validation result identifies a possible number regardless of calling limitations
     * @return boolean true for any IS_POSSIBLE(_xxx) enum value except ending with _OPERATOR_ONLY or _VPN_ONLY
     */
    public boolean isPubliclyValid() {
        return (   (this == IS_POSSIBLE)
                || (this == IS_POSSIBLE_LOCAL_ONLY)
                || (this == IS_POSSIBLE_NATIONAL_ONLY)
                || (this == IS_POSSIBLE_INTERNATIONAL_ONLY));
    }

    /**
     * Returns if the validation result assuming a wrong usage, so taking the plain number would lead to a different result
     * e.g. emergency short number is used with country code while regulation authority blocked the NDC overlapping with
     * the short number as a reserved block. The reserved block is identified if its not exactly the shortnumber.
     * <p/>
     * So +49110 would lead to {@link PhoneNumberValidationResult#INVALID_COUNTRY_CODE} while +491105566 would lead to {@link PhoneNumberValidationResult#INVALID_RESERVE_NUMBER}
     * <p/>
     * This also applies to {@link PhoneNumberValidationResult#TOO_SHORT} and {@link PhoneNumberValidationResult#TOO_LONG}, which is more accurate than just being reserve
     * since even a reserve block might have length constrains.
     */
    public boolean isOverwritingReserve() {
        return (   (this == INVALID_COUNTRY_CODE)
                || (this == INVALID_NATIONAL_ACCESS_CODE)
                || (this == TOO_SHORT)
                || (this == TOO_LONG));
    }

}
