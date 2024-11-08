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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.Comparator;


/**
 * This class provides basic logic to check a given number against a simple set of rules to identify if it is short numbers, which does not need normalization.
 * It also needs to provide its country calling code, to specify where the rules apply.
 * <p>
 * PhoneLib already provide a ShortNumbers, but for EU wide 116xxx range only a few countries are configured to support the range. 
 * For Germany only currently assigned numbers are configured which is in contrast to Googles definition of checks, 
 * but nevertheless the <a href="https://issuetracker.google.com/u/1/issues/183669955">corresponding Issues</a> has been rejected.
 * </p><p>
 * Additionally, we designed the NumberPlanFactory to have a NumberPlan class for each DeviceContextLineType, so we can support short numbers which are valid only in fixed-line or mobile context.
 * </p>
 * @see NumberPlanFactory
 * @see de.telekom.phonenumbernormalizer.dto.DeviceContextLineType
 */
public abstract class NumberPlan {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberPlan.class);

    /**
     * A subclass needs to provide a Map&lt;String, Integer&gt; as tbe rules to identify short numbers
     * The key (String) is representing a prefix for the number and the value (Integer) is the total length of the short code (including the prefix)
     * <ul>
     * <li>e.g. "110"; 3 - the total length is already the length of the prefix, so its exactly the short number</li>
     * <li>e.g. "1100"; 5 - the total length is longer than the length of the prefix, so all number from 11000 to 11009 are coverd</li>
     * <li>e.g. both rules above can be combined, because longer prefixes are evaluated first, so that partical ranges of rules with shorter prefix can be overriden.</li>
     * </ul>
     * @return Map of rules for the short codes
     *
     * @see NumberPlan#isNumberPlanValid()
     */
    protected abstract Map<String, Integer> getShortNumberCodes();

    /**
     * A subclass can provide Country Calling Code of the rules - not used inside this class, but
     * re-usable when adding the subclass to the factory.
     *
     * @return Country Calling Code without leading international Dialing Prefix
     *
     * @see NumberPlanFactory
     */
    public static String getCountryCode() {
        return null;
    }

    /**
     * A subclass can provide National Destination Code of the rules - not used inside this class, but
     * re-usable when adding the subclass to the factory.
     *
     * @param nsn - National Significant Number (without IDP + CC or NAC as prefix)
     * @return National Destination Code without leading National Access Code
     *
     * @see NumberPlanFactory
     */
    public String getNationalDestinationCodeFromNationalSignificantNumber(String nsn) {
        return "";
    }

    public int getNationDestinationCodeMinimalNumberLength(String ndc, String number) {
        return -1;
    }

    public int getNationDestinationCodeMaximumNumberLength(String ndc, String number) {
        return -1;
    }

    public int getDefaultMinimalNumberLength() {
        return -1;
    }

    public int getDefaultMaximumNumberLength() {
        return -1;
    }

    public boolean isNumberTooShortForNationalDestinationCode(String ndc, String number) {
        int minLength = getNationDestinationCodeMinimalNumberLength(ndc, number);
        if (minLength == -1) {
            minLength = getDefaultMinimalNumberLength();
        }
        return ((minLength != -1) && (minLength>number.length()));
    }

    public boolean isNumberTooLongForNationalDestinationCode(String ndc, String number) {
        int maxLength = getNationDestinationCodeMaximumNumberLength(ndc, number);
        if (maxLength == -1) {
            maxLength = getDefaultMaximumNumberLength();
        }
        return ((maxLength != -1) && (maxLength<number.length()));
    }

    public boolean isSupportingNDC() { return true; }

    public boolean isNDCOptional(String ndc) {
        return true;
    }

    public boolean isNDCNationalOperatorOnly(String ndc) { return false; }

    public boolean isReserved(String number) {return false; }

    public Integer isMatchingLength(String number) {return null;}

    public boolean isUsableWithIDPandCCfromOutside(String number) {
        return false;
    }

    public boolean isUsableWithIDPandCCandNDCfromOutside(String number) {
        return false;
    }

    public boolean isUsableWithIDPandCCfromInside(String number) {
        return false;
    }

    public boolean isUsableWithIDPandCCandNDCfromInside(String number) {
        return false;
    }

    public boolean isUsableWithNAC(String number) {
        return false;
    }
    public boolean isUsableWithNACandNDC(String number) {
        return false;
    }

    public boolean isUsableDirectly(String number) {
        return isMatchingShortNumber(number);
    }


    public boolean isUsable(ShortCodeUseable how, String number) {

        if (how == null) {
            return false;
        }

        switch (how) {
            case WITH_IDP_AND_CC_FROM_OUTSIDE:
                return isUsableWithIDPandCCfromOutside(number);
            case WITH_IDP_AND_CC_AND_NDC_FROM_OUTSIDE:
                return isUsableWithIDPandCCandNDCfromOutside(number);
            case WITH_IDP_AND_CC_FROM_INSIDE:
                return isUsableWithIDPandCCfromInside(number);
            case WITH_IDP_AND_CC_AND_NDC_FROM_INSIDE:
                return isUsableWithIDPandCCandNDCfromInside(number);
            case WITH_NAC:
                return isUsableWithNAC(number);
            case WITH_NAC_AND_NDC:
                return isUsableWithNACandNDC(number);
            case DIRECTLY:
                return isUsableDirectly(number);
        }
        return false;
    }

    /**
     * Finds the longest prefix of a short number rule of the current number plan, at the beginning of a number.
     *
     * @param number - number that should be checked against the number plan
     * @return String - if number matches starts with a short number rule prefix, this is the longest one - otherwise it is an empty string.
     */
    public String startingWithShortNumberKey(String number) {
        // first check if we have rules at all
        if (this.getShortNumberCodes() == null) {
            LOGGER.debug("no short number code rules available");
            return "";
        }

        // check if the number is starting with a prefix defined in the rule
        int minShortNumberKeyLength = this.getMinShortNumberKeyLength();
        int maxShortNumberKeyLength = this.getMaxShortNumberKeyLength();

        // starting prefix check with the longest prefix, so overlapping prefixes could be realized
        // e.g. 1180 is in Germany a starting prefix for a 6 digit short number while 1181 - 1189 is in Germany a starting
        // prefix for a 5 digits number and could be summed up by 118 and only 1180 is overriding this prefix part.
        for (int i = maxShortNumberKeyLength; i >= minShortNumberKeyLength; i--) {
            if (number.length() >= i) {
                String shortNumber = number.substring(0, i);
                if (this.getShortNumberCodes().containsKey(shortNumber)) {
                    return shortNumber;
                }
            }
        }
        return "";
    }

    public int getShortCodeLength(String shortNumberKey) {
        return getShortNumberCodes().get(shortNumberKey);
    }

    /**
     * Checks if a number is matching any a short number rule of the current number plan.
     *
     * @param number - number that should be checked against the number plan
     * @return boolean - if short number was matched
     */
    public boolean isMatchingShortNumber(String number) {

        // first check if we have rules at all
        if (this.getShortNumberCodes() == null) {
            LOGGER.debug("no short number code rules available");
            return false;
        }

        // check if the number is in the length range of short numbers defined by the rules.
        int minShortNumberLength = this.getMinShortNumberLength();
        int maxShortNumberLength = this.getMaxShortNumberLength();

        if (number.length() < minShortNumberLength) {
            LOGGER.debug("no short number, to short number: {}", number);
            return false;
        }

        if (number.length() > maxShortNumberLength) {
            LOGGER.debug("no short number, too long number: {}", number);
            return false;
        }

        // check if the number length exactly matches the defined length of the prefix
        String shortNumberKey = startingWithShortNumberKey(number);
        if (shortNumberKey.length()>0) {
            return number.length() == getShortCodeLength(shortNumberKey);
        }

        LOGGER.debug("no short number, to code found for number: {}", number);
        return false;
    }

    /**
     * Returns the length of the shortest configured short Number within the rules.
     *
     * @return the length of the shortest short Number within the rules or 0 if no rule exists
     *
     * @see NumberPlan#getShortNumberCodes()
     */
    private int getMinShortNumberLength() {
        if (getShortNumberCodes() != null) {
            return Collections.min(getShortNumberCodes().values());
        } else return 0;
    }

    /**
     * Returns the length of the shortest prefix within the rules.
     *
     * @return the length of the shortest prefix within the rules or 0 if no rule exists
     *
     * @see NumberPlan#getShortNumberCodes()
     */
    private int getMinShortNumberKeyLength() {
        if (getShortNumberCodes() != null) {
            Optional<String> minKey = getShortNumberCodes().keySet().stream()
                    .min(Comparator.comparing(String::length));
            if (minKey.isPresent()) {
                return minKey.get().length();
            }
        }
        return 0;
    }


    /**
     * Returns the length of the longest configured short Number within the rules.
     *
     * @return the length of the longest short Number within the rules or 0 if no rule exists
     *
     * @see NumberPlan#getShortNumberCodes()
     */
    private int getMaxShortNumberLength() {
        if (getShortNumberCodes() != null) {
            return Collections.max(getShortNumberCodes().values());
        } else return 0;
    }

    /**
     * Returns the length of the longest prefix within the rules.
     *
     * @return the length of the longest prefix within the rules or 0 if no rule exists
     *
     * @see NumberPlan#getShortNumberCodes()
     */
    private int getMaxShortNumberKeyLength() {
        if (getShortNumberCodes() != null) {
            Optional<String> maxKey = getShortNumberCodes().keySet().stream()
                    .max(Comparator.comparing(String::length));
            if (maxKey.isPresent()) {
                return maxKey.get().length();
            }
        }
        return 0;
    }

    /**
     * Checks if the rules are logically without conflict.
     * Conflicts happen, if the length of a short number (value) is defined lower than the length of its prefix (key).
     *
     * @return are the rules free of conflict
     *
     * @see NumberPlan#getShortNumberCodes()
     */
    public Boolean isNumberPlanValid() {
        if (this.getShortNumberCodes() != null) {
            for ( Map.Entry<String, Integer> entry : this.getShortNumberCodes().entrySet()) {
                if (entry.getKey().length() > entry.getValue()) {
                    LOGGER.warn("The length of the ShortNumberCode '{}' is longer then its ShortnumberCodeLength '{}'", entry.getKey(), entry.getValue());
                    return false;
                }
            }
        }
        return true;
    }
}
