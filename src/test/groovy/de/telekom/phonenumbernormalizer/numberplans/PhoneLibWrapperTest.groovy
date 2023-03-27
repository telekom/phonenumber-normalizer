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

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.Phonenumber
import spock.lang.Specification


class PhoneLibWrapperTest extends Specification {

    PhoneLibWrapper target

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance()

    def "national number and leading zeros"( number,  regionCode, expectedResult) {
        given:
            def result = ""
            def pn = null
            try {
                pn = phoneUtil.parse(number, regionCode)
            } catch (NumberParseException e) {
                result = e.errorType.toString()
            }
        when:
            if (pn != null) {
                result = PhoneLibWrapper.nationalPhoneNumberWithoutNationalPrefix(pn)
            }
        then:
            result == expectedResult

        where:
            number          | regionCode | expectedResult
            "+49203556677"  | "DE"       | "203556677"
            "0203556677"    | "DE"       | "203556677"
            "203556677"     | "DE"       | "203556677"
            "556677"        | "DE"       | "556677"

            "3784000"       | "US"       | "3784000"
            "14253784000"   | "US"       | "4253784000"
            "+14253784000"  | "US"       | "4253784000"
            "01114253784000"| "US"       | "4253784000"

            //special Short Code only valid in Australia, but retain in parsing for the others
            "000"           | "AU"       | "000"
            "000"           | "DE"       | "000"
            "000"           | "US"       | "000"
            "000"           | "IT"       | "000"
            // shorter zero check
            "00"            | "AU"       | "00"
            "00"            | "DE"       | "TOO_SHORT_AFTER_IDD" // because IDC in Germany is 00
            "00"            | "US"       | "00"
            "00"            | "IT"       | "TOO_SHORT_AFTER_IDD" // because IDC in Italy is 00
            //shorter zero check - just current PhoneLib behavior
            "0"            | "AU"       | "NOT_A_NUMBER" // because its to short
            "0"            | "DE"       | "NOT_A_NUMBER" // because its to short
            "0"            | "US"       | "NOT_A_NUMBER" // because its to short
            "0"            | "IT"       | "NOT_A_NUMBER" // because its to short
            //shorter 1 check - just current PhoneLib behavior
            "1"            | "AU"       | "NOT_A_NUMBER" // because its to short
            "1"            | "DE"       | "NOT_A_NUMBER" // because its to short
            "1"            | "US"       | "NOT_A_NUMBER" // because its to short
            "1"            | "IT"       | "NOT_A_NUMBER" // because its to short
            //shorter zero check - just current PhoneLib behavior
            "01"           | "AU"       | "01"
            "01"           | "DE"       | "01"
            "01"           | "US"       | "01"
            "01"           | "IT"       | "01"

            //Special Italian leading Zero within national number (and not)
            "012345678"     | "IT"       | "012345678"
            "+39012345678"  | "IT"       | "012345678"
            "0039012345678" | "IT"       | "012345678"
            "+39012345678"  | "DE"       | "012345678"  //Italy called from Germany
            "0039012345678" | "DE"       | "012345678"  //Italy called from Germany
            "+39012345678"  | "US"       | "012345678"  //Italy called from North America
            "01139012345678"| "US"       | "012345678"  //Italy called from North America
            "312345678"     | "IT"       | "312345678"
            "+39312345678"  | "IT"       | "312345678"
            "0039312345678" | "IT"       | "312345678"
            "+39312345678"  | "DE"       | "312345678"  //Italy called from Germany
            "0039312345678" | "DE"       | "312345678"  //Italy called from Germany
            "+39312345678"  | "US"       | "312345678"  //Italy called from North America
            "01139312345678"| "US"       | "312345678"  //Italy called from North America
    }

    def "isNormalizingTried"( number,  regionCode, expectedResult) {
        given:
            target = new PhoneLibWrapper(number, regionCode)

        when: "isNormalizingTried: $number and $regionCode"
            def result = target.isNormalizingTried()

        then: "it should be: $expectedResult"
            result == expectedResult

        where:
        number          | regionCode | expectedResult
        null            | null       | false
        ""              | null       | false
        ""              | ""         | false
        null            | "DE"       | false
        ""              | "DE"       | false
        "*61"           | "DE"       | false
        "**61"          | "DE"       | false
        "+49203555666"  | "DE"       | false
        "0203555666"    | "DE"       | true
        "1"             | "DE"       | true
        "1"             | ""         | true
        "1"             | null       | true
    }

    def "getNationalPrefix"( number,  regionCode, expectedResult) {
        given:
        target = new PhoneLibWrapper(number, regionCode)

        when: "getNationalPrefix: $number and $regionCode"
        def result = target.getNationalAccessCode()

        then: "it should be: $expectedResult"
        result == expectedResult

        where:
        number          | regionCode | expectedResult
        null            | null       | null
        ""              | null       | null
        ""              | ""         | null
        null            | "DE"       | "0"
        ""              | "DE"       | "0"
        "*61"           | "DE"       | "0"
        "**61"          | "DE"       | "0"
        "+49203555666"  | "DE"       | "0"
        "0203555666"    | "DE"       | "0"
        "1"             | "DE"       | "0"
        "1"             | ""         | null
        "1"             | null       | null
        // here are some international special cases - which might be changed by those countries!
        "+49203555666"  | "FR"       | "0"
        "+49203555666"  | "US"       | "1"
        "+49203555666"  | "RU"       | "8"
        "+49203555666"  | "CZ"       | ""
        "+49203555666"  | "HU"       | "06"
        "+49203555666"  | "MX"       | "01"
    }

    def "hasRegionNationalPrefix"( number,  regionCode, expectedResult) {
        given:
        target = new PhoneLibWrapper(number, regionCode)

        when: "hasRegionNationalPrefix: $number and $regionCode"
        def result = target.hasRegionNationalAccessCode()

        then: "it should be: $expectedResult"
        result == expectedResult

        where:
        number          | regionCode | expectedResult
        null            | null       | false
        ""              | null       | false
        ""              | ""         | false
        null            | "DE"       | true
        ""              | "DE"       | true
        "*61"           | "DE"       | true
        "**61"          | "DE"       | true
        "+49203555666"  | "DE"       | true
        "0203555666"    | "DE"       | true
        "1"             | "DE"       | true
        "1"             | ""         | false
        "1"             | null       | false
        // here are some international special cases - which might be changed by those countries!
        "+49203555666"  | "FR"       | true
        "+49203555666"  | "US"       | true
        "+49203555666"  | "RU"       | true
        "+49203555666"  | "CZ"       | false
        "+49203555666"  | "HU"       | true
        "+49203555666"  | "MX"       | true
    }

    def "isSpecialFormat"( number,   expectedResult) {
        given:

        when: "isSpecialFormat: $number"
        def result = PhoneLibWrapper.isSpecialFormat(number)

        then: "it should be: $expectedResult"
        result == expectedResult

        where:
        number          | expectedResult
        null            | false
        ""              | false
        " "             | false
        "0"             | false
        "1"             | false
        "2"             | false
        "3"             | false
        "4"             | false
        "5"             | false
        "6"             | false
        "7"             | false
        "8"             | false
        "9"             | false
        "-"             | false
        "("             | false
        ")"             | false
        "+"             | true
        "*"             | true
    }

    /*
   *
   *  getMetadataForRegion is used via reflection an thus needs to catch possible exceptions.
   *
   *  did not found a way to make a test for this, so this means 3% of lines are not covered.
   *
   */

    def "getMetadataForRegion"(regionCode, expectedResult) {
        given:
        target = new PhoneLibWrapper(null, regionCode)

        when: "getMedadataForRegion: $regionCode"
        def result = target.getMetadataForRegion()

        then: "it should normalize the number to: $expectedResult"
        result == expectedResult

        where:
        regionCode | expectedResult
        null       | null
    }

    def "private extendNumberByDefaultAreaCodeAndCountryCode null"() {
        given:
            target = new PhoneLibWrapper(null, "DE")

        when:
            def result = target.extendNumberByDefaultAreaCodeAndCountryCode(null, null)

        then:
            assert result == null
    }

    def "parseNumber"( number,  regionCode, expectedResult) {
        given:
            target = new PhoneLibWrapper(number, regionCode)

        when: "parseNumber: $number and $regionCode"
            def result = target.parseNumber(number, regionCode)

        then: "it should normalize the number to: $expectedResult"
            result == expectedResult

        where:
            number | regionCode | expectedResult
            null   | null       | null
            ""     | ""         | null
    }

    def "exception check for getMetadataForRegion: phoneUtil == null"(){
        given:
            //overriding read only attribute by .metaClass. access
            target = new PhoneLibWrapper(null, "49")
            target.metaClass.phoneUtil = null

        when:
            def result = target.getMetadataForRegion()

        then:
            assert result == null
    }

    def "getRegionCodeForCountryCode"(countryCode, expectedResult) {
        given:

        when: "getRegionCodeForCountryCode: $countryCode"
        def result = PhoneLibWrapper.getRegionCodeForCountryCode(countryCode)

        then: "it should normalize the number to: $expectedResult"
        result == expectedResult

        where:
        countryCode | expectedResult
        null        | PhoneLibWrapper.UNKNOWN_REGIONCODE
        ""          | PhoneLibWrapper.UNKNOWN_REGIONCODE
        "invalid"   | PhoneLibWrapper.UNKNOWN_REGIONCODE
        "49"        | "DE"

    }

}
