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
 * Validation of other parts of the number are not covered by an own value. So this enum wrapper is introducing {@link PhoneNumberValidationResult#INVALID_NATIONAL_ACCESS_CODE} for number plans with a national access code.
 * </p>
 * @see ValidationResult
 */


// TODO: DRAMA numbers: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/Mittlg148_2021.pdf?__blob=publicationFile&v=1

public enum PhoneNumberValidationResult {

    /** The number length matches that of valid numbers for this region. */
    IS_POSSIBLE(ValidationResult.IS_POSSIBLE),
    /**
     * The number length matches that of local numbers for this region only (i.e. numbers that may
     * be able to be dialled within an area, but do not have all the information to be dialled from
     * anywhere inside or outside the country).
     */
    IS_POSSIBLE_LOCAL_ONLY(ValidationResult.IS_POSSIBLE_LOCAL_ONLY),
    /** The number has an invalid country calling code. */
    INVALID_COUNTRY_CODE(ValidationResult.INVALID_COUNTRY_CODE),
    /** The number has an invalid national access code. */
    INVALID_NATIONAL_ACCESS_CODE(ValidationResult.INVALID_LENGTH),
    /** The number is shorter than all valid numbers for this region. */
    TOO_SHORT(ValidationResult.TOO_SHORT),
    /**
     * The number is longer than the shortest valid numbers for this region, shorter than the
     * longest valid numbers for this region, and does not itself have a number length that matches
     * valid numbers for this region. This can also be returned in the case where
     * isPossibleNumberForTypeWithReason was called, and there are no numbers of this type at all
     * for this region.
     */
    INVALID_LENGTH(ValidationResult.INVALID_LENGTH),
    /** The number is longer than all valid numbers for this region. */
    TOO_LONG(ValidationResult.TOO_LONG);
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

}
