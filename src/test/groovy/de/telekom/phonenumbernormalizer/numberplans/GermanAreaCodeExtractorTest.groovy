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
package de.telekom.phonenumbernormalizer.numberplans


import de.telekom.phonenumbernormalizer.numberplans.constants.GermanAreaCodeExtractor
import spock.lang.Specification

class GermanAreaCodeExtractorTest extends Specification {

    def "Extract Area Codes"(String number, expectedResult) {
        given:

        when:

        String result = GermanAreaCodeExtractor.fromNumber(number)

        then:
        result == expectedResult

        where:
        number      | expectedResult
        "200556666" | ""
        "203555666" | "203"
        "204555666" | "2045"
        "204655666" | ""
        "212555666" | "212"
        "212955666" | "2129"
    }

}
