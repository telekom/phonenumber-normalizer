/*
 * Copyright © 2026 Deutsche Telekom AG (opensource@telekom.de)
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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Region dialing metadata used by {@link PhoneLibWrapper}.
 * <p>
 * This class is generated from the configured LibPhoneNumber dependency during Maven's generate-sources
 * phase by src/generators/metaDataExtractor/MetaDataExtractor.java.
 * </p>
 */
public final class GeneratedRegionDialingMetadata {

    public static final String LIBPHONENUMBER_VERSION = "9.0.33";
    public static final OffsetDateTime GENERATED_AT = OffsetDateTime.parse("2026-07-02T07:35:29Z");

    private static final Map<String, RegionDialingMetadata> METADATA_BY_REGION = createMetadata();

    private GeneratedRegionDialingMetadata() {
    }

    public static RegionDialingMetadata forRegion(String regionCode) {
        if (regionCode == null) {
            return null;
        }
        return METADATA_BY_REGION.get(regionCode.toUpperCase(Locale.ROOT));
    }

    private static Map<String, RegionDialingMetadata> createMetadata() {
        Map<String, RegionDialingMetadata> metadata = new HashMap<>();
        metadata.put("AC", new RegionDialingMetadata("00", "", false));
        metadata.put("AD", new RegionDialingMetadata("00", "", false));
        metadata.put("AE", new RegionDialingMetadata("00", "0", true));
        metadata.put("AF", new RegionDialingMetadata("00", "0", true));
        metadata.put("AG", new RegionDialingMetadata("011", "1", true));
        metadata.put("AI", new RegionDialingMetadata("011", "1", true));
        metadata.put("AL", new RegionDialingMetadata("00", "0", true));
        metadata.put("AM", new RegionDialingMetadata("00", "0", true));
        metadata.put("AO", new RegionDialingMetadata("00", "", false));
        metadata.put("AR", new RegionDialingMetadata("00", "0", true));
        metadata.put("AS", new RegionDialingMetadata("011", "1", true));
        metadata.put("AT", new RegionDialingMetadata("00", "0", true));
        metadata.put("AU", new RegionDialingMetadata("001[14-689]|14(?:1[14]|34|4[17]|[56]6|7[47]|88)0011", "0", true));
        metadata.put("AW", new RegionDialingMetadata("00", "", false));
        metadata.put("AX", new RegionDialingMetadata("00|99(?:[01469]|5(?:[14]1|3[23]|5[59]|77|88|9[09]))", "0", true));
        metadata.put("AZ", new RegionDialingMetadata("00", "0", true));
        metadata.put("BA", new RegionDialingMetadata("00", "0", true));
        metadata.put("BB", new RegionDialingMetadata("011", "1", true));
        metadata.put("BD", new RegionDialingMetadata("00", "0", true));
        metadata.put("BE", new RegionDialingMetadata("00", "0", true));
        metadata.put("BF", new RegionDialingMetadata("00", "", false));
        metadata.put("BG", new RegionDialingMetadata("00", "0", true));
        metadata.put("BH", new RegionDialingMetadata("00", "", false));
        metadata.put("BI", new RegionDialingMetadata("00", "", false));
        metadata.put("BJ", new RegionDialingMetadata("00", "", false));
        metadata.put("BL", new RegionDialingMetadata("00", "0", true));
        metadata.put("BM", new RegionDialingMetadata("011", "1", true));
        metadata.put("BN", new RegionDialingMetadata("00", "", false));
        metadata.put("BO", new RegionDialingMetadata("00(?:1\\d)?", "0", true));
        metadata.put("BQ", new RegionDialingMetadata("00", "", false));
        metadata.put("BR", new RegionDialingMetadata("00(?:1[245]|2[1-35]|31|4[13]|[56]5|99)", "0", true));
        metadata.put("BS", new RegionDialingMetadata("011", "1", true));
        metadata.put("BT", new RegionDialingMetadata("00", "", false));
        metadata.put("BW", new RegionDialingMetadata("00", "", false));
        metadata.put("BY", new RegionDialingMetadata("810", "8", true));
        metadata.put("BZ", new RegionDialingMetadata("00", "", false));
        metadata.put("CA", new RegionDialingMetadata("011", "1", true));
        metadata.put("CC", new RegionDialingMetadata("001[14-689]|14(?:1[14]|34|4[17]|[56]6|7[47]|88)0011", "0", true));
        metadata.put("CD", new RegionDialingMetadata("00", "0", true));
        metadata.put("CF", new RegionDialingMetadata("00", "", false));
        metadata.put("CG", new RegionDialingMetadata("00", "", false));
        metadata.put("CH", new RegionDialingMetadata("00", "0", true));
        metadata.put("CI", new RegionDialingMetadata("00", "", false));
        metadata.put("CK", new RegionDialingMetadata("00", "", false));
        metadata.put("CL", new RegionDialingMetadata("(?:0|1(?:1[0-69]|2[02-5]|5[13-58]|69|7[0167]|8[018]))0", "", false));
        metadata.put("CM", new RegionDialingMetadata("00", "", false));
        metadata.put("CN", new RegionDialingMetadata("00|1(?:[12]\\d|79)\\d\\d00", "0", true));
        metadata.put("CO", new RegionDialingMetadata("00(?:4(?:[14]4|56)|[579])", "0", true));
        metadata.put("CR", new RegionDialingMetadata("00", "", false));
        metadata.put("CU", new RegionDialingMetadata("119", "0", true));
        metadata.put("CV", new RegionDialingMetadata("0", "", false));
        metadata.put("CW", new RegionDialingMetadata("00", "", false));
        metadata.put("CX", new RegionDialingMetadata("001[14-689]|14(?:1[14]|34|4[17]|[56]6|7[47]|88)0011", "0", true));
        metadata.put("CY", new RegionDialingMetadata("00", "", false));
        metadata.put("CZ", new RegionDialingMetadata("00", "", false));
        metadata.put("DE", new RegionDialingMetadata("00", "0", true));
        metadata.put("DJ", new RegionDialingMetadata("00", "", false));
        metadata.put("DK", new RegionDialingMetadata("00", "", false));
        metadata.put("DM", new RegionDialingMetadata("011", "1", true));
        metadata.put("DO", new RegionDialingMetadata("011", "1", true));
        metadata.put("DZ", new RegionDialingMetadata("00", "0", true));
        metadata.put("EC", new RegionDialingMetadata("00", "0", true));
        metadata.put("EE", new RegionDialingMetadata("00", "", false));
        metadata.put("EG", new RegionDialingMetadata("00", "0", true));
        metadata.put("EH", new RegionDialingMetadata("00", "0", true));
        metadata.put("ER", new RegionDialingMetadata("00", "0", true));
        metadata.put("ES", new RegionDialingMetadata("00", "", false));
        metadata.put("ET", new RegionDialingMetadata("00", "0", true));
        metadata.put("FI", new RegionDialingMetadata("00|99(?:[01469]|5(?:[14]1|3[23]|5[59]|77|88|9[09]))", "0", true));
        metadata.put("FJ", new RegionDialingMetadata("0(?:0|52)", "", false));
        metadata.put("FK", new RegionDialingMetadata("00", "", false));
        metadata.put("FM", new RegionDialingMetadata("00", "", false));
        metadata.put("FO", new RegionDialingMetadata("00", "", false));
        metadata.put("FR", new RegionDialingMetadata("00", "0", true));
        metadata.put("GA", new RegionDialingMetadata("00", "", false));
        metadata.put("GB", new RegionDialingMetadata("00", "0", true));
        metadata.put("GD", new RegionDialingMetadata("011", "1", true));
        metadata.put("GE", new RegionDialingMetadata("00", "0", true));
        metadata.put("GF", new RegionDialingMetadata("00", "0", true));
        metadata.put("GG", new RegionDialingMetadata("00", "0", true));
        metadata.put("GH", new RegionDialingMetadata("00", "0", true));
        metadata.put("GI", new RegionDialingMetadata("00", "", false));
        metadata.put("GL", new RegionDialingMetadata("00", "", false));
        metadata.put("GM", new RegionDialingMetadata("00", "", false));
        metadata.put("GN", new RegionDialingMetadata("00", "", false));
        metadata.put("GP", new RegionDialingMetadata("00", "0", true));
        metadata.put("GQ", new RegionDialingMetadata("00", "", false));
        metadata.put("GR", new RegionDialingMetadata("00", "", false));
        metadata.put("GT", new RegionDialingMetadata("00", "", false));
        metadata.put("GU", new RegionDialingMetadata("011", "1", true));
        metadata.put("GW", new RegionDialingMetadata("00", "", false));
        metadata.put("GY", new RegionDialingMetadata("001", "", false));
        metadata.put("HK", new RegionDialingMetadata("00(?:30|5[09]|[126-9]?)", "", false));
        metadata.put("HN", new RegionDialingMetadata("00", "", false));
        metadata.put("HR", new RegionDialingMetadata("00", "0", true));
        metadata.put("HT", new RegionDialingMetadata("00", "", false));
        metadata.put("HU", new RegionDialingMetadata("00", "06", true));
        metadata.put("ID", new RegionDialingMetadata("00[89]", "0", true));
        metadata.put("IE", new RegionDialingMetadata("00", "0", true));
        metadata.put("IL", new RegionDialingMetadata("0(?:0|1[2-9])", "0", true));
        metadata.put("IM", new RegionDialingMetadata("00", "0", true));
        metadata.put("IN", new RegionDialingMetadata("00", "0", true));
        metadata.put("IO", new RegionDialingMetadata("00", "", false));
        metadata.put("IQ", new RegionDialingMetadata("00", "0", true));
        metadata.put("IR", new RegionDialingMetadata("00", "0", true));
        metadata.put("IS", new RegionDialingMetadata("00|1(?:0(?:01|[12]0)|100)", "", false));
        metadata.put("IT", new RegionDialingMetadata("00", "", false));
        metadata.put("JE", new RegionDialingMetadata("00", "0", true));
        metadata.put("JM", new RegionDialingMetadata("011", "1", true));
        metadata.put("JO", new RegionDialingMetadata("00", "0", true));
        metadata.put("JP", new RegionDialingMetadata("010", "0", true));
        metadata.put("KE", new RegionDialingMetadata("000", "0", true));
        metadata.put("KG", new RegionDialingMetadata("00", "0", true));
        metadata.put("KH", new RegionDialingMetadata("00[14-9]", "0", true));
        metadata.put("KI", new RegionDialingMetadata("00", "0", true));
        metadata.put("KM", new RegionDialingMetadata("00", "", false));
        metadata.put("KN", new RegionDialingMetadata("011", "1", true));
        metadata.put("KP", new RegionDialingMetadata("00|99", "0", true));
        metadata.put("KR", new RegionDialingMetadata("00(?:[125689]|3(?:[46]5|91)|7(?:00|27|3|55|6[126]))", "0", true));
        metadata.put("KW", new RegionDialingMetadata("00", "", false));
        metadata.put("KY", new RegionDialingMetadata("011", "1", true));
        metadata.put("KZ", new RegionDialingMetadata("810", "8", true));
        metadata.put("LA", new RegionDialingMetadata("00", "0", true));
        metadata.put("LB", new RegionDialingMetadata("00", "0", true));
        metadata.put("LC", new RegionDialingMetadata("011", "1", true));
        metadata.put("LI", new RegionDialingMetadata("00", "0", true));
        metadata.put("LK", new RegionDialingMetadata("00", "0", true));
        metadata.put("LR", new RegionDialingMetadata("00", "0", true));
        metadata.put("LS", new RegionDialingMetadata("00", "", false));
        metadata.put("LT", new RegionDialingMetadata("00", "0", true));
        metadata.put("LU", new RegionDialingMetadata("00", "", false));
        metadata.put("LV", new RegionDialingMetadata("00", "", false));
        metadata.put("LY", new RegionDialingMetadata("00", "0", true));
        metadata.put("MA", new RegionDialingMetadata("00", "0", true));
        metadata.put("MC", new RegionDialingMetadata("00", "0", true));
        metadata.put("MD", new RegionDialingMetadata("00", "0", true));
        metadata.put("ME", new RegionDialingMetadata("00", "0", true));
        metadata.put("MF", new RegionDialingMetadata("00", "0", true));
        metadata.put("MG", new RegionDialingMetadata("00", "0", true));
        metadata.put("MH", new RegionDialingMetadata("011", "1", true));
        metadata.put("MK", new RegionDialingMetadata("00", "0", true));
        metadata.put("ML", new RegionDialingMetadata("00", "", false));
        metadata.put("MM", new RegionDialingMetadata("00", "0", true));
        metadata.put("MN", new RegionDialingMetadata("001", "0", true));
        metadata.put("MO", new RegionDialingMetadata("00", "", false));
        metadata.put("MP", new RegionDialingMetadata("011", "1", true));
        metadata.put("MQ", new RegionDialingMetadata("00", "0", true));
        metadata.put("MR", new RegionDialingMetadata("00", "", false));
        metadata.put("MS", new RegionDialingMetadata("011", "1", true));
        metadata.put("MT", new RegionDialingMetadata("00", "", false));
        metadata.put("MU", new RegionDialingMetadata("0(?:0|[24-7]0|3[03])", "", false));
        metadata.put("MV", new RegionDialingMetadata("0(?:0|19)", "", false));
        metadata.put("MW", new RegionDialingMetadata("00", "0", true));
        metadata.put("MX", new RegionDialingMetadata("0[09]", "", false));
        metadata.put("MY", new RegionDialingMetadata("00", "0", true));
        metadata.put("MZ", new RegionDialingMetadata("00", "", false));
        metadata.put("NA", new RegionDialingMetadata("00", "0", true));
        metadata.put("NC", new RegionDialingMetadata("00", "", false));
        metadata.put("NE", new RegionDialingMetadata("00", "", false));
        metadata.put("NF", new RegionDialingMetadata("00", "", false));
        metadata.put("NG", new RegionDialingMetadata("009", "0", true));
        metadata.put("NI", new RegionDialingMetadata("00", "", false));
        metadata.put("NL", new RegionDialingMetadata("00", "0", true));
        metadata.put("NO", new RegionDialingMetadata("00", "", false));
        metadata.put("NP", new RegionDialingMetadata("00", "0", true));
        metadata.put("NR", new RegionDialingMetadata("00", "", false));
        metadata.put("NU", new RegionDialingMetadata("00", "", false));
        metadata.put("NZ", new RegionDialingMetadata("0(?:0|161)", "0", true));
        metadata.put("OM", new RegionDialingMetadata("00", "", false));
        metadata.put("PA", new RegionDialingMetadata("00", "", false));
        metadata.put("PE", new RegionDialingMetadata("00|19(?:1[124]|77|90)00", "0", true));
        metadata.put("PF", new RegionDialingMetadata("00", "", false));
        metadata.put("PG", new RegionDialingMetadata("00|140[1-3]", "", false));
        metadata.put("PH", new RegionDialingMetadata("00", "0", true));
        metadata.put("PK", new RegionDialingMetadata("00", "0", true));
        metadata.put("PL", new RegionDialingMetadata("00", "", false));
        metadata.put("PM", new RegionDialingMetadata("00", "0", true));
        metadata.put("PR", new RegionDialingMetadata("011", "1", true));
        metadata.put("PS", new RegionDialingMetadata("00", "0", true));
        metadata.put("PT", new RegionDialingMetadata("00", "", false));
        metadata.put("PW", new RegionDialingMetadata("01[12]", "", false));
        metadata.put("PY", new RegionDialingMetadata("00", "0", true));
        metadata.put("QA", new RegionDialingMetadata("00", "", false));
        metadata.put("RE", new RegionDialingMetadata("00", "0", true));
        metadata.put("RO", new RegionDialingMetadata("00", "0", true));
        metadata.put("RS", new RegionDialingMetadata("00", "0", true));
        metadata.put("RU", new RegionDialingMetadata("810", "8", true));
        metadata.put("RW", new RegionDialingMetadata("00", "0", true));
        metadata.put("SA", new RegionDialingMetadata("00", "0", true));
        metadata.put("SB", new RegionDialingMetadata("0[01]", "", false));
        metadata.put("SC", new RegionDialingMetadata("010|0[0-2]", "", false));
        metadata.put("SD", new RegionDialingMetadata("00", "0", true));
        metadata.put("SE", new RegionDialingMetadata("00", "0", true));
        metadata.put("SG", new RegionDialingMetadata("0[0-3]\\d", "", false));
        metadata.put("SH", new RegionDialingMetadata("00", "", false));
        metadata.put("SI", new RegionDialingMetadata("00|10(?:22|66|88|99)", "0", true));
        metadata.put("SJ", new RegionDialingMetadata("00", "", false));
        metadata.put("SK", new RegionDialingMetadata("00", "0", true));
        metadata.put("SL", new RegionDialingMetadata("00", "0", true));
        metadata.put("SM", new RegionDialingMetadata("00", "", false));
        metadata.put("SN", new RegionDialingMetadata("00", "", false));
        metadata.put("SO", new RegionDialingMetadata("00", "0", true));
        metadata.put("SR", new RegionDialingMetadata("00", "", false));
        metadata.put("SS", new RegionDialingMetadata("00", "0", true));
        metadata.put("ST", new RegionDialingMetadata("00", "", false));
        metadata.put("SV", new RegionDialingMetadata("00", "", false));
        metadata.put("SX", new RegionDialingMetadata("011", "1", true));
        metadata.put("SY", new RegionDialingMetadata("00", "0", true));
        metadata.put("SZ", new RegionDialingMetadata("00", "", false));
        metadata.put("TA", new RegionDialingMetadata("00", "", false));
        metadata.put("TC", new RegionDialingMetadata("011", "1", true));
        metadata.put("TD", new RegionDialingMetadata("00|16", "", false));
        metadata.put("TG", new RegionDialingMetadata("00", "", false));
        metadata.put("TH", new RegionDialingMetadata("00[1-9]", "0", true));
        metadata.put("TJ", new RegionDialingMetadata("810", "", false));
        metadata.put("TK", new RegionDialingMetadata("00", "", false));
        metadata.put("TL", new RegionDialingMetadata("00", "", false));
        metadata.put("TM", new RegionDialingMetadata("810", "8", true));
        metadata.put("TN", new RegionDialingMetadata("00", "", false));
        metadata.put("TO", new RegionDialingMetadata("00", "", false));
        metadata.put("TR", new RegionDialingMetadata("00", "0", true));
        metadata.put("TT", new RegionDialingMetadata("011", "1", true));
        metadata.put("TV", new RegionDialingMetadata("00", "", false));
        metadata.put("TW", new RegionDialingMetadata("0(?:0[25-79]|19)", "0", true));
        metadata.put("TZ", new RegionDialingMetadata("00[056]", "0", true));
        metadata.put("UA", new RegionDialingMetadata("00", "0", true));
        metadata.put("UG", new RegionDialingMetadata("00[057]", "0", true));
        metadata.put("US", new RegionDialingMetadata("011", "1", true));
        metadata.put("UY", new RegionDialingMetadata("0(?:0|1[3-9]\\d)", "0", true));
        metadata.put("UZ", new RegionDialingMetadata("00", "", false));
        metadata.put("VA", new RegionDialingMetadata("00", "", false));
        metadata.put("VC", new RegionDialingMetadata("011", "1", true));
        metadata.put("VE", new RegionDialingMetadata("00", "0", true));
        metadata.put("VG", new RegionDialingMetadata("011", "1", true));
        metadata.put("VI", new RegionDialingMetadata("011", "1", true));
        metadata.put("VN", new RegionDialingMetadata("00", "0", true));
        metadata.put("VU", new RegionDialingMetadata("00", "", false));
        metadata.put("WF", new RegionDialingMetadata("00", "", false));
        metadata.put("WS", new RegionDialingMetadata("0", "", false));
        metadata.put("XK", new RegionDialingMetadata("00", "0", true));
        metadata.put("YE", new RegionDialingMetadata("00", "0", true));
        metadata.put("YT", new RegionDialingMetadata("00", "0", true));
        metadata.put("ZA", new RegionDialingMetadata("00", "0", true));
        metadata.put("ZM", new RegionDialingMetadata("00", "0", true));
        metadata.put("ZW", new RegionDialingMetadata("00", "0", true));
        return Collections.unmodifiableMap(metadata);
    }
}
