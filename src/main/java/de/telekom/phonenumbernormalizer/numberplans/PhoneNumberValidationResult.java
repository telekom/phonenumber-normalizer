package de.telekom.phonenumbernormalizer.numberplans;

import com.google.i18n.phonenumbers.PhoneNumberUtil.ValidationResult;

/**
 * Wrapper around the PhoneLib enum {@link ValidationResult} from Google
 * <p>
 * When the PhoneLib is validating a phone number it returns a value of the enum {@link ValidationResult}.
 * </p><p>
 * It differentiate two possible positive and five possible negative results. The value {@link ValidationResult#INVALID_LENGTH} for any negative case, which is not explicitly covered by any of the other four values.
 * While most of the values for negative cases are focused on the number length, the value {@link ValidationResult#INVALID_COUNTRY_CODE} explicitly focus on a specific number part - the Country Code.
 * </p><p>
 *  This wrapper introduces explicit INVALID_ reason codes for currently undifferentiated number parts IDP, NAC and NDC. Lastly there is also {@link PhoneNumberValidationResult#INVALID_DRAMA_NUMBER} reason code for the whole number if it is reserved as a fictional number in arts.
 *  </p><p>
 *  Furthermore it introduces additional IS_POSSIBLE_ values, to hint for more specific calling restrictions of the validated number besides {@link PhoneNumberValidationResult#IS_POSSIBLE_LOCAL_ONLY}.
 * </p>
 * @see ValidationResult
 */

public enum PhoneNumberValidationResult {

    /** The number length matches that of valid numbers for this region or used NDC without any identified calling restrictions. */
    IS_POSSIBLE(ValidationResult.IS_POSSIBLE),
    /**
     * The number length/pattern matches that of local numbers for this region only (i.e. numbers that may
     * be able to be dialled within an area, but does not have all the information e.g. NDC or is not allowed to be dialled from
     * anywhere inside or outside the country).
     */
    IS_POSSIBLE_LOCAL_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),
    /**
     * The number length/pattern matches that of national numbers for this region only (i.e. numbers that may
     * be able to be dialled within an area or the region itself, but is not allowed to be dialled from
     * anywhere outside the country using CC).
     */
    IS_POSSIBLE_NATIONAL_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),
    /**
     * The number length/pattern matches that of international number for this region only (i.e. number is not allowed to be call from within the region,
     * even if CC of the regions is used).
     */
    IS_POSSIBLE_INTERNATIONAL_ONLY(ValidationResult.IS_POSSIBLE),
    /**
     * The number length/pattern matches that of national VPN number for this region only (i.e. number is only allowed to be call from other numbers of same VPN within this region and not by users of the public telephony network).
     */
    IS_POSSIBLE_NATIONAL_VPN_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),
    /**
     * The number length/pattern matches that of VPN number for this region only (i.e. number is only allowed to be call from other numbers of same VPN even internationally but not by users of the public telephony network).
     */
    IS_POSSIBLE_VPN_ONLY(ValidationResult.IS_POSSIBLE),
    /**
     * The number length/pattern matches that of national operator traffic control numbers for this region only (i.e. number is only allowed to be call between national operators for call routing and not directly by users of the public telephony network).
     */
    IS_POSSIBLE_NATIONAL_OPERATOR_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),
    /**
     * The number length/pattern matches that of operator traffic control numbers for this region only (i.e. number is only allowed to be call between operators for call routing and not directly by users of the public telephony network).
     */
    IS_POSSIBLE_OPERATOR_ONLY(ValidationResult.IS_POSSIBLE),
    /** The number has an invalid international dialing prefix (aka IDP) for this region. */
    INVALID_INTERNATIONAL_DIALING_PREFIX(ValidationResult.INVALID_LENGTH),
    /** The number has an invalid national destination code (aka NDC) for this region. */
    INVALID_COUNTRY_CODE(ValidationResult.INVALID_COUNTRY_CODE),
    /** The number has an invalid national access code (aka NAC). */
    INVALID_NATIONAL_ACCESS_CODE(ValidationResult.INVALID_LENGTH),
    /** The number has an invalid country calling code (aka CC). */
    INVALID_NATIONAL_DESTINATION_CODE(ValidationResult.INVALID_LENGTH),
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
    /** The number is matching a drama number range, which simulates valid number for this region only used in movies or other fictional story telling. */
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
     * Returns best matching corresponding {@link ValidationResult} enum value for an instance of a {@link PhoneNumberValidationResult} enum value
     * @return corresponding {@link ValidationResult} enum value
     */
    public ValidationResult getPhoneLibValidationResult() {
        return phoneLibResult;
    }

    /**
     * Returns if the validation result identifies a possible number regardless of calling limitations
     * @return boolean true for any IS_POSSIBLE(_xxx) enum value
     */
    public boolean isSomeHowValid() {
        return ((this == IS_POSSIBLE) || (this == IS_POSSIBLE_LOCAL_ONLY)
                || (this == IS_POSSIBLE_NATIONAL_ONLY) || (this == IS_POSSIBLE_NATIONAL_VPN_ONLY) || (this == IS_POSSIBLE_NATIONAL_OPERATOR_ONLY)
                || (this == IS_POSSIBLE_INTERNATIONAL_ONLY)
                || (this == IS_POSSIBLE_VPN_ONLY)
                || (this == IS_POSSIBLE_OPERATOR_ONLY));
    }

}
