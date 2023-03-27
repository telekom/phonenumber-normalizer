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
package de.telekom.phonenumbernormalizer.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This is an aggregation of attributes which define the context of a device in a telephony use case to
 * enable normalization of its telephone number even when optional NDC is not used.
 */
@ApiModel(description = "The context of a call about the used line and its relation in a number plan.")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DeviceContextDto implements DeviceContext {

    /**
     * The line-type the device is using. Default is DeviceContextLineType.UNKNOWN
     *
     * @see DeviceContextLineType#UNKNOWN
     */
    @ApiModelProperty(value = "Type of the devices line used. ", example = DeviceContextLineType.FIXEDLINE_VALUE, allowableValues = DeviceContextLineType.FIXEDLINE_VALUE + ", "
                                                                                                                                  + DeviceContextLineType.MOBILE_VALUE + ", "
                                                                                                                                  + DeviceContextLineType.UNKNOWN_VALUE)
    private DeviceContextLineType lineType = DeviceContextLineType.UNKNOWN;

    /**
     * The Country (Calling) Code of the countries number plan, where the device is originated. Also known as Landesvorwahl.
     * Without international dialing prefix nor trunc code. If not known or not set, it should return DeviceContext.UNKNOWN_VALUE.
     * <p/>
     * E.G. "49" for Germany
     *
     * @see DeviceContext#UNKNOWN_VALUE
     */
    @ApiModelProperty(value = "Country Calling Code without leading 00 nor +; if not present its unknown", example = "49")
    private String countryCode = DeviceContext.UNKNOWN_VALUE;

    /**
     * The National Destination Code (NDC) of the countries number plan, where the device is originated. Also known as AreaCode, ONKZ or (Orts-)Vorwahl.
     * Without National Access Code (NAC) nor trunc code. If not known or not set, it should return DeviceContext.UNKNOWN_VALUE.
     * <p/>
     * E.G. "228" for Bonn in Germany where the Deutsche Telekom Headquarter is located
     *
     * @see DeviceContext#UNKNOWN_VALUE
     */
    @ApiModelProperty(value = "National Destination Code without leading 0 nor other trunc codes; if not present its unknown", example = "228")
    private String nationalDestinationCode = DeviceContext.UNKNOWN_VALUE;

}
