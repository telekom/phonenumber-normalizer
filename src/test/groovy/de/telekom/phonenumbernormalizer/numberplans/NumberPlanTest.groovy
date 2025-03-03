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


import spock.lang.Specification


class NumberPlanTest extends Specification {
    NumberPlan target
    NumberPlan nullTarget
    NumberPlan target2
    NumberPlan invalidTarget3
    NumberPlan target3

    def short_numbers
    def short_numbers2
    def invalid_short_numbers3

    def "setup"() {
        short_numbers = ["111": 3, "222": 5, "333": 9]
        target = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return short_numbers
            }
        }

        target3 = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return null
            }

            @Override
            public String getNationalDestinationCodeFromNationalSignificantNumber(String nsn) {
                PhoneLibWrapper wrapper = new PhoneLibWrapper(nsn, PhoneLibWrapper.getRegionCodeForCountryCode("49"));
                return wrapper.getNationalDestinationCode();
            }
        }




        nullTarget = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return null
            }
        }

        short_numbers2 = ["111": 3, "22": 5, "333": 9]
        target2 = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return short_numbers2
            }
        }

        invalid_short_numbers3 = ["111": 2, "222": 5, "333": 9]
        invalidTarget3 = new NumberPlan() {
            @Override
            protected Map<String, Integer> getShortNumberCodes() {
                return invalid_short_numbers3
            }
        }
    }

    def "getNationalDestinationCodeFromNationalSignificantNumber"(number, expectedResult) {
        given:

        when:
        String result = target3.getNationalDestinationCodeFromNationalSignificantNumber(number)

        then:
        assert result == expectedResult

        where:
        number              | expectedResult
        "0203556677"        | "203"

    }

    def "matchShortNumber "(number, expectedResult) {
        given:

        when:
        "match short number: $number"
        boolean result = target.isMatchingShortNumber(number)

        then:
        "it should return: $expectedResult"
        assert result == expectedResult

        where:
        number       | expectedResult
        "1"          | false
        "11"         | false
        "111"        | true
        "1111"       | false
        "112"        | false
        "1112"       | false
        "111222"     | false
        "22211"      | true
        "222111"     | false
        "222111333"  | false
        "333111333"  | true
        "3331113331" | false
        "3231113331" | false
    }

    def "matchShortNumber on null info"(number, expectedResult) {
        given:

        when:
        "match short number: $number"
        boolean result = nullTarget.isMatchingShortNumber(number)

        then:
        "it should return: $expectedResult"
        assert result == expectedResult

        where:
        number       | expectedResult
        "1"          | false
        "11"         | false
        "111"        | false
        "1111"       | false
        "112"        | false
        "1112"       | false
        "111222"     | false
        "22211"      | false
        "222111"     | false
        "222111333"  | false
        "333111333"  | false
        "3331113331" | false
        "3231113331" | false
    }

    def "matchShortNumber uneven info"(number, expectedResult) {
        given:

        when:
        "match short number: $number"
        boolean result = target2.isMatchingShortNumber(number)

        then:
        "it should return: $expectedResult"
        assert result == expectedResult

        where:
        number       | expectedResult
        "1"          | false
        "11"         | false
        "111"        | true
        "1111"       | false
        "112"        | false
        "1112"       | false
        "111222"     | false
        "22211"      | true
        "222111"     | false
        "222111333"  | false
        "333111333"  | true
        "3331113331" | false
        "3231113331" | false
    }

    def "matchShortNumber on invalid info"(number, expectedResult) {
        given:

        when:
        "match short number: $number"
        boolean result = invalidTarget3.isMatchingShortNumber(number)

        then:
        "it should return: $expectedResult"
        assert result == expectedResult

        where:
        number       | expectedResult
        "1"          | false
        "11"         | false
        "111"        | false
        "1111"       | false
        "112"        | false
        "1112"       | false
        "111222"     | false
        "22211"      | true
        "222111"     | false
        "222111333"  | false
        "333111333"  | true
        "3331113331" | false
        "3231113331" | false
    }

    def "validation of valid NumberPlan"() {
        given:

        when:
        boolean result = target.isNumberPlanValid()

        then:
        assert result
    }

    def "validation of valid null NumberPlan"() {
        given:

        when:
        boolean result = nullTarget.isNumberPlanValid()

        then:
        assert result
    }

    def "validation of uneven NumberPlan"() {
        given:

        when:
        boolean result = target2.isNumberPlanValid()

        then:
        assert result
    }

    def "validation of invalid NumberPlan: minLength to short"() {
        given:

        when:
        boolean result = invalidTarget3.isNumberPlanValid()

        then:
        assert !result
    }

}
