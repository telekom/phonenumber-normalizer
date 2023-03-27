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
package de.telekom.phonenumbernormalizer.numberplans


import de.telekom.phonenumbernormalizer.dto.DeviceContextLineType
import de.telekom.phonenumbernormalizer.numberplans.constants.DeFixedLineNumberPlan
import spock.lang.Specification


class NumberPlanFactoryTest extends Specification {
    NumberPlanFactory target

    def "setup"() {
        target = NumberPlanFactory.INSTANCE
    }

    def "getNumberPlan - unavailable number plans"(type, expectedResult) {
        given:
        def countryCode = "de"

        when:
        "get number plan for type: $type"
        def result = target.getNumberPlan(type, countryCode)

        then:
        "it should return: $expectedResult"
        assert result == expectedResult

        where:
        type                          | expectedResult
        DeviceContextLineType.MOBILE  | null
        DeviceContextLineType.UNKNOWN | null
    }

    def "getNumberPlan - fixed-line-type "() {
        given:
        def countryCode = "49"
        def deviceType = DeviceContextLineType.FIXEDLINE

        when:
        "get number plan for type: $deviceType"
        def result = target.getNumberPlan(deviceType, countryCode)

        then:
        "it should return a DeFixedLineNumberPlan"
        assert result instanceof DeFixedLineNumberPlan
    }

    def "getNumberPlan - mobile vs. fixline"() {
        // TODO: This testcase needs to check if different Numberplans are used based on the DeviceContext
        given:
        def sn_fix = ["111": 3, "222": 5, "333": 9]
        def np_fix = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return sn_fix
            }
        }

        def sn_mobile = ["111": 5, "222": 3, "333": 9]
        def np_mobile  = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return sn_mobile
            }
        }

        when:
        def i = 1

        then:
        assert i == 1

    }
}
