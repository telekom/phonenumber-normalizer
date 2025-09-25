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
package de.telekom.phonenumbernormalizer

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import spock.lang.Specification


class PhoneAreaCodeComponentImplTest extends Specification {

    Resource[] numberPlanResources = Arrays.asList(new ClassPathResource("arealabels/nationallabels/de.json"),
            new ClassPathResource("arealabels/nationallabels/ru.json"),
            new ClassPathResource("arealabels/nationallabels/us.json"))

    Resource countryCodeResourceFile = new ClassPathResource("arealabels/international_country_codes.json")

    def phoneAreaCodeComponentImpl

    def setup() {
        this.phoneAreaCodeComponentImpl = new PhoneNumberAreaLabelImpl()
        this.phoneAreaCodeComponentImpl.numberPlanResources = numberPlanResources
        this.phoneAreaCodeComponentImpl.countryCodeResource = countryCodeResourceFile
        this.phoneAreaCodeComponentImpl.initFile()
    }

    def "default data loaded"() {
        given:
        def arealabel

        when:
        arealabel = new PhoneNumberAreaLabelImpl()
        arealabel.initFile()

        then:
        arealabel.numberPlanResources != null
    }

    def "get location by unknown area code"() {
        given:
        def nationalNumber = "123456789"
        def regionCode = ""

        when:
        "Get location by national number: ${nationalNumber} and unknown region code: ${regionCode}"
        def result = this.phoneAreaCodeComponentImpl.getLocationByNationalNumberAndRegionCode(nationalNumber, regionCode)

        then: "It should return a empty optional"
        result.isPresent() == false
    }

    def "get location by area code"(nationalNumber, regionCode, expectedResult) {
        given:
        when:
        "Get location name of national number: ${nationalNumber} and region code: ${regionCode}"
        def result = this.phoneAreaCodeComponentImpl.getLocationByNationalNumberAndRegionCode(nationalNumber, regionCode)

        then:
        "It should return the area name: ${expectedResult}"
        if (expectedResult == null) {
            result.isPresent() == false
        } else {
            result.isPresent() == true
            result.get() == expectedResult
        }
        where:
        nationalNumber | regionCode | expectedResult
        "201"          | "DE"       | "Essen"
        "202"          | "DE"       | "Wuppertal"
        "6041"         | "DE"       | "Bottrop"
        "60411"        | "DE"       | "Bottrop"
        "60412"        | "DE"       | "XXX"
        "60413"        | "DE"       | "Bottrop"
        "605"          | "DE"       | null
        "606"          | "DE"       | null
        "6065"         | "DE"       | null
        "6066"         | "DE"       | "AAA"
        "6067"         | "DE"       | null
        "205"          | "US"       | "Alabama"
        "659"          | "US"       | "Alabama"
        "203"          | "US"       | "Connecticut"
        "670"          | "US"       | "Nördliche Marianen"
        "1"            | "RU"       | "Location1"
        "2"            | "RU"       | "Location2"
    }

    def "get country by country code"(countryCode, expectedResult) {
        given:
        when:
        "Get country name of code: ${countryCode}"
        def result = this.phoneAreaCodeComponentImpl.getCountryNameByCountryCode(countryCode)

        then:
        "It should return the country name: ${expectedResult}"
        result.isPresent() == true
        result.get() == expectedResult

        where:
        countryCode | expectedResult
        "9712"      | "Abu Dhabi"
        "20"        | "Ägypten"
        "1"         | "Vereinigte Staaten"
        "49"        | "Deutschland"
    }

    def "get area label by E164 Number"(e164number, expectedResult) {
        given:
        when:
        "Get area label of E164 number: ${e164number}"
        def result = this.phoneAreaCodeComponentImpl.getLocationByE164Number(e164number)

        then:
        "It should return the label: ${expectedResult}"
        if (expectedResult == null) {
            assert result.isPresent() == false
        } else {
            assert result.isPresent() == true
            assert result.get() == expectedResult
        }

        where:
        e164number          | expectedResult
        "+492015551235"     | "Essen"
        // check fake for Duisburg which is normaly 203, to check it is using the config.
        "+496035551235"     | "Duisburg"
        "+496041551235"     | "Bottrop"
        // Bottrop has a four digit area code 6041 so 6042 is not in the test data and we have a fallback to the Country
        "+496042551235"     | "Deutschland"
        "+49112"            | "Deutschland"
        // too short number to parse
        "+491"              | "Deutschland"
        "+49"               | "Deutschland"
        "+4"                | null
        "+1"                | "Vereinigte Staaten"
        "+12"               | "Vereinigte Staaten"
        "+120"              | "Vereinigte Staaten"
        // normally this would also been known as "Birmingham" as city but in test data is grouped to the state
        "+1205"             | "Alabama"
        // +1 239 is Florida but not entered in test data
        "+1239"             | "Vereinigte Staaten"
        // +44 is UK, but not entered in test data
        "+445555"           | null
    }
}
