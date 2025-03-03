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

import de.telekom.phonenumbernormalizer.dto.DeviceContext
import de.telekom.phonenumbernormalizer.dto.DeviceContextDto
import de.telekom.phonenumbernormalizer.dto.DeviceContextLineType
import de.telekom.phonenumbernormalizer.numberplans.PhoneLibWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification


class PhoneNumberNormalizerImplTest extends Specification {

    PhoneNumberNormalizer target

    def "setup"() {
        target = new PhoneNumberNormalizerImpl()
    }

    def "normalizeNumber by RegionCode"(String number, String countryCode, expectedResult) {
        given:

        when:
        "normalize number: $number for country: $countryCode"
        String result = target.normalizePhoneNumber(number, countryCode)

        then:
        "it should normalize the number to: $expectedResult"
        result == expectedResult

        where:
        number                    | countryCode | expectedResult
        null                      | "DE"        | null
        "0723 413 641"            | "DE"        | "+49723413641"
        "0040 723 413 641"        | "DE"        | "+40723413641"
        "+40723413641"            | "DE"        | "+40723413641"
        "0040-723-413-641"        | "DE"        | "+40723413641"
        "0176 3 0 6 9 6544"       | "DE"        | "+4917630696544"
        "0203556677"              | "DE"        | "+49203556677"
        "203556677"               | "DE"        | "203556677"
        "556677"                  | "DE"        | "556677"
        "5566778"                 | "DE"        | "5566778"
        "55667789"                | "DE"        | "55667789"
        "556677889"               | "DE"        | "556677889"
        "5566778899"              | "DE"        | "5566778899"
        "55667788990"             | "DE"        | "55667788990"
        "000"                     | "AU"        | "000"
        "012345678"               | "IT"        | "+39012345678"
        "312345678"               | "IT"        | "+39312345678"
    }

    def "normalizeNumber by empty DeviceContext"(String number, countryCode, expectedResult) {
        given:

        def dc = new DeviceContextDto()

        target.setFallbackRegionCode(countryCode)

        when: "normalize number: $number"

        def result = target.normalizePhoneNumber(number, dc)

        then: "it should normalize the number to: $expectedResult"
        result == expectedResult

        where:

        number                    | countryCode | expectedResult
        null                      | "DE"        | null
        "0723 413 641"            | "DE"        | "+49723413641"
        "0040 723 413 641"        | "DE"        | "+40723413641"
        "+40723413641"            | "DE"        | "+40723413641"
        "0040-723-413-641"        | "DE"        | "+40723413641"
        "0176 3 0 6 9 6544"       | "DE"        | "+4917630696544"
        "0203556677"              | "DE"        | "+49203556677"
        "203556677"               | "DE"        | "203556677"
        "55"                      | "DE"        | "55"
        "556"                     | "DE"        | "556"
        "5566"                    | "DE"        | "5566"
        "55667"                   | "DE"        | "55667"
        "556677"                  | "DE"        | "556677"
        "5566778"                 | "DE"        | "5566778"
        "55667789"                | "DE"        | "55667789"
        "556677889"               | "DE"        | "556677889"
        "5566778899"              | "DE"        | "5566778899"
        "55667788990"             | "DE"        | "55667788990"
        "000"                     | "AU"        | "000"
        "012345678"               | "IT"        | "+39012345678"
        "312345678"               | "IT"        | "+39312345678"
        "0040(0176) 3 0 6 9 6541" | null        | "0040017630696541"
        "0040 176 3 0 6 9 6542"   | null        | "004017630696542"
        "004017630696543"         | null        | "004017630696543"
        "0040-0176 3 0 6 9 6544"  | null        | "0040017630696544"
        "0176 3 0 6 9 6544"       | null        | "017630696544"
        "0203556677"              | null        | "0203556677"
        "203556677"               | null        | "203556677"
        "*1"                      | "DE"        | "*1"
        "**1"                     | "DE"        | "**1"
        "*61"                     | "DE"        | "*61"
        "**61"                    | "DE"        | "**61"
        "*1"                      | null        | "*1"
        "**1"                     | null        | "**1"
        "*61"                     | null        | "*61"
        "**61"                    | null        | "**61"
    }

    def "normalizeNumber by DeviceContext"(String number, String countryCode, String areaCode, expectedResult) {
        given:

        def dc = new DeviceContextDto(DeviceContextLineType.FIXEDLINE, countryCode, areaCode)

        target = new PhoneNumberNormalizerImpl()
        target.setFallbackRegionCode("DE")

        when: "normalize number: $number"

        def result = target.normalizePhoneNumber(number, dc)

        then: "it should normalize the number to: $expectedResult"
        result == expectedResult

        where:

        number                    | countryCode | areaCode |expectedResult
        //Special Case where Number already includes country code with leading +
        "+49723 413 641"        | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE   | "+49723413641"
        "+40723413641"          | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE   | "+40723413641"
        "(+40)723413641"        | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE   | "+40723413641"
        "(+40)(723)413641"      | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE   | "+40723413641"
        "(+40)723/413641"       | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE   | "+40723413641"
        "(+40)723-413641"       | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE   | "+40723413641"
        //Special Case areaCode is unknown
        "0203556677"              | "49"        | DeviceContext.UNKNOWN_VALUE | "+49203556677"
        "203556677"               | "49"        | DeviceContext.UNKNOWN_VALUE | "203556677"
        "0203556677"              | "33"        | DeviceContext.UNKNOWN_VALUE | "+33203556677"
        "203556677"               | "33"        | DeviceContext.UNKNOWN_VALUE | "203556677"
        //Special Case Number plan without Area Codes!
        "0203556677"              | "39"        | DeviceContext.UNKNOWN_VALUE | "+390203556677"
        "203556677"               | "39"        | DeviceContext.UNKNOWN_VALUE | "+39203556677"
        "0203556677"              | "39"        | "222"                             | "+390203556677"
        "203556677"               | "39"        | "222"                             | "+39203556677"
        "0203"                    | "39"        | "222"                             | "0203"
        "2035"                    | "39"        | "222"                             | "2035"
        //Special Case Number plan without Area Codes!
        "0203556677"              | "49"        | "222"                             | "+49203556677"
        "203556677"               | "49"        | "222"                             | "+49222203556677"
        //Special Case where country code is not valid - fallback to UserComponent.local default DE
        "0203556677"              | "83"        | DeviceContext.UNKNOWN_VALUE | "+49203556677"
        "203556677"               | "83"        | DeviceContext.UNKNOWN_VALUE | "203556677"
        //Special Case where country unknown - fallback to UserComponent.local default DE
        "0203556677"              | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE | "+49203556677"
        "203556677"               | DeviceContext.UNKNOWN_VALUE | DeviceContext.UNKNOWN_VALUE | "203556677"
        //Special Case where country code is not parsable - fallback to UserComponent.local default DE
        "0203556677"              | "xxx"       | DeviceContext.UNKNOWN_VALUE | "+49203556677"
        "203556677"               | "xxx"       | DeviceContext.UNKNOWN_VALUE | "203556677"
        //New Logic, if Country and Area Code is present for normalization:
        "0723 413 641"         | "49"        | "203"    | "+49723413641"
        "+49723 413 641"       | "49"        | "203"    | "+49723413641"
        "+40723413641"         | "49"        | "203"    | "+40723413641"
        "(+40)723413641"       | "49"        | "203"    | "+40723413641"
        "(+40)(723)413641"     | "49"        | "203"    | "+40723413641"
        "(+40)723/413641"      | "49"        | "203"    | "+40723413641"
        "(+40)723-413641"      | "49"        | "203"    | "+40723413641"
        "0176 3 0 6 9 6544"       | "49"        | "203"    | "+4917630696544"
        "0203556677"              | "49"        | "203"    | "+49203556677"
        "203556677"               | "49"        | "203"    | "+49203203556677"
        //New Logic, inside german fixed-line special short numbers
        "110"                     | "49"        | "203"    | "110"
        "112"                     | "49"        | "203"    | "112"
        "115"                     | "49"        | "203"    | "115"
        "0201115"                 | "49"        | "203"    | "+49201115"
        // too short not a short number
        "1181"                    | "49"        | "203"    | "+492031181"
        "11810"                   | "49"        | "203"    | "11810"
        "118101"                  | "49"        | "203"    | "+49203118101"
        "11820"                   | "49"        | "203"    | "11820"
        "118202"                  | "49"        | "203"    | "+49203118202"
        "11830"                   | "49"        | "203"    | "11830"
        "118303"                  | "49"        | "203"    | "+49203118303"
        "11840"                   | "49"        | "203"    | "11840"
        "118404"                  | "49"        | "203"    | "+49203118404"
        "11850"                   | "49"        | "203"    | "11850"
        "118505"                  | "49"        | "203"    | "+49203118505"
        "11860"                   | "49"        | "203"    | "11860"
        "118606"                  | "49"        | "203"    | "+49203118606"
        "11870"                   | "49"        | "203"    | "11870"
        "118707"                  | "49"        | "203"    | "+49203118707"
        "11880"                   | "49"        | "203"    | "11880"
        "118808"                  | "49"        | "203"    | "+49203118808"
        "11890"                   | "49"        | "203"    | "11890"
        "118909"                  | "49"        | "203"    | "+49203118909"
        // too short not a short number
        "11800"                   | "49"        | "203"    | "+4920311800"
        "118000"                  | "49"        | "203"    | "118000"
        "1180000"                 | "49"        | "203"    | "+492031180000"
        "116000"                  | "49"        | "203"    | "116000"
        "1160001"                 | "49"        | "203"    | "+492031160001"
        //New Logic, inside german fixed-line non short numbers
        "55"                      | "49"        | "203"    | "+4920355"
        "556"                     | "49"        | "203"    | "+49203556"
        "5566"                    | "49"        | "203"    | "+492035566"
        "55667"                   | "49"        | "203"    | "+4920355667"
        "556677"                  | "49"        | "203"    | "+49203556677"
        "5566778"                 | "49"        | "203"    | "+492035566778"
        "55667789"                | "49"        | "203"    | "+4920355667789"
        "556677889"               | "49"        | "203"    | "+49203556677889"
        "5566778899"              | "49"        | "203"    | "+492035566778899"
        "55667788990"             | "49"        | "203"    | "+4920355667788990"
        "000"                     | "61"        | "222"    | "000"
        "012345678"               | "39"        | "222"    | "+39012345678"
        "312345678"               | "39"        | "222"    | "+39312345678"
        "+49723 413 641"          | null        | null     | "+49723413641"
        "+40723413641"            | null        | null     | "+40723413641"
        "(+40)723413641"          | null        | null     | "+40723413641"
        "(+40)(723)413641"        | null        | null     | "+40723413641"
        "(+40)723/413641"         | null        | null     | "+40723413641"
        "(+40)723-413641"         | null        | null     | "+40723413641"
        "0176 3 0 6 9 6544"       | null        | null     | "+4917630696544"
        "0203556677"              | null        | null     | "+49203556677"
        "203556677"               | null        | null     | "203556677"
        //Internal DECT Numbers
        "*1"                      | null        | null     | "*1"
        "**1"                     | null        | null     | "**1"
        "*61"                     | null        | null     | "*61"
        "**61"                    | null        | null     | "**61"
        "*1"                      | "49"        | "203"    | "*1"
        "**1"                     | "49"        | "203"    | "**1"
        "*61"                     | "49"        | "203"    | "*61"
        "**61"                    | "49"        | "203"    | "**61"
    }

    def "private normalize(String regionCode, String dialableNumber, DeviceContextDto deviceContext)"() {
        given:

        target = new PhoneNumberNormalizerImpl()
        target.setFallbackRegionCode("DE")

        def dc = new DeviceContextDto(DeviceContextLineType.FIXEDLINE, "49", "203")


        when:
        def result = target.normalize(null, dc)

        then:

        assert result == null

    }

    def "private fallbackNormalizationFromDeviceContextToRegionCode no defaultRegion set"( ){
        given:

        target = new PhoneNumberNormalizerImpl()

        when:

        def result = target.fallbackNormalizationFromDeviceContextToDefaultRegionCode("111","333")

        then:
        assert result == "333"
    }


    def "private fallbackNormalizationFromDeviceContextToRegionCode empty defaultRegion set"( ){
        given:

        target = new PhoneNumberNormalizerImpl()
        target.setFallbackRegionCode("")

        when:

        def result = target.fallbackNormalizationFromDeviceContextToDefaultRegionCode("111","333")

        then:
        assert result == "333"
    }

}
