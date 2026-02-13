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

import de.telekom.phonenumbernormalizer.dto.DeviceContextLineType;
import de.telekom.phonenumbernormalizer.numberplans.constants.DeFixedLineNumberPlan;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory to retrieve a NumberPlan for a given line-type and country calling code. Currently supporting:
 * <ul>
 *     <li>German Fixed-Line</li>
 * </ul>
 *
 * @see NumberPlanFactory#getNumberPlan(DeviceContextLineType, String)
 */
public class NumberPlanFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberPlanFactory.class);
    public static final NumberPlanFactory INSTANCE = new NumberPlanFactory();

    /**
     * Two-dimensional map - The first key is DeviceContextLineType and second key is the Country Calling Code while the value is a NumberPlan object.
     *
     * @see NumberPlan
     * @see DeviceContextLineType
     */
    private final Map<DeviceContextLineType, Map<String, NumberPlan>> numberPlans = new EnumMap<>(DeviceContextLineType.class);

    /**
     * Adding all coded NumberPlans to the factory
     */
    private NumberPlanFactory() {
       this.initFixedLineNumberPlans();
       this.initMobileNumberPlans();
       this.initFallBackNumberPlans();
    }

    /**
     * Adds coded NumberPlans for fixed-line context to the factory.
     *
     * @see NumberPlan
     * @see DeviceContextLineType#FIXEDLINE
     */
    private void initFixedLineNumberPlans() {
        Map<String, NumberPlan> fixedLineNumberPlans = new HashMap<>();
        fixedLineNumberPlans.put(DeFixedLineNumberPlan.getCountryCode(), new DeFixedLineNumberPlan());
        numberPlans.put(DeviceContextLineType.FIXEDLINE, fixedLineNumberPlans);
    }

    /**
     * Adds coded NumberPlans for mobile context to the factory.
     *
     * @see NumberPlan
     * @see DeviceContextLineType#MOBILE
     */
    private void initMobileNumberPlans() {
        // TODO: Mobile Number Plan
    }

    /**
     * Adds coded NumberPlans for unknown context to the factory. These are just the common rules for mobile and fixed-line, so that they could be applied regardless of which actual line context is used.
     *
     * @see NumberPlan
     * @see DeviceContextLineType#UNKNOWN
     */
    private void initFallBackNumberPlans() {
        Map<String, NumberPlan> fixedLineNumberPlans = new HashMap<>();
        // For Germany all short numbers of the fixed-line are also valid in mobile, so we can reuse it, if unknown.
        fixedLineNumberPlans.put(DeFixedLineNumberPlan.getCountryCode(), new DeFixedLineNumberPlan());
        numberPlans.put(DeviceContextLineType.UNKNOWN, fixedLineNumberPlans);
    }

    /**
     * Gets a NumberPlan for a line-type of a specific country.
     *
     * @param numberPlanType line-type where the NumberPlan is valid
     * @param countryCode country calling code for which the NumberPlan
     * @return {@link NumberPlan}  for further checks
     *
     * @see DeviceContextLineType
     * @see NumberPlan#isMatchingShortNumber(String)
     */
    public NumberPlan getNumberPlan(DeviceContextLineType numberPlanType, String countryCode) {
        if (numberPlans.containsKey(numberPlanType)) {
            LOGGER.debug("use number plan for type: {}", numberPlanType);
            Map<String, NumberPlan> numberPlan = numberPlans.get(numberPlanType);
            if (numberPlan.containsKey(countryCode)) {
                LOGGER.debug("use number plan for country code: {}", countryCode);
                return numberPlan.get(countryCode);
            }
        }
        LOGGER.debug("no number plan for country available");
        return null;
    }
}
