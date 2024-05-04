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


import com.google.i18n.phonenumbers.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;
import java.util.Objects;


class CountryCodeExtractor {
    // https://www.itu.int/dms_pub/itu-t/opb/sp/T-SP-E.164D-11-2011-PDF-E.pdf
    public static String fromNumber(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "1":
                return "1";  // NANP
            case "2":
                return fromNumber2(number.substring(1));
            case "3":
                return fromNumber3(number.substring(1));
            case "4":
                return fromNumber4(number.substring(1));
            case "5":
                return fromNumber5(number.substring(1));
            case "6":
                return fromNumber6(number.substring(1));
            case "7":
                return "7";  // Russian Federation AND Kazakhstan (Republic of)
            case "8":
                return fromNumber8(number.substring(1));
            case "9":
                return fromNumber9(number.substring(1));
            default:
                return "";
        }
    }

    public static String fromNumber2(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "20";  // Egypt (Arab Republic of)
            case "1":
                return fromNumber21(number.substring(1));
            case "2":
                return fromNumber22(number.substring(1));
            case "3":
                return fromNumber23(number.substring(1));
            case "4":
                return fromNumber24(number.substring(1));
            case "5":
                return fromNumber25(number.substring(1));
            case "6":
                return fromNumber26(number.substring(1));
            case "7":
                return "27";  // South Africa (Republic of)
            case "9":
                return fromNumber29(number.substring(1));
            default:
                return "";
        }
    }

    public static String fromNumber21(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "1":
                return "211";  // South Sudan (Republic of)
            case "2":
                return "212";  // Morocco (Kingdom of)
            case "3":
                return "213";  // Algeria (People's Democratic Republic of)
            case "6":
                return "216";  // Tunisia
            case "8":
                return "218";  // Libya (Socialist People's Libyan Arab Jamahiriya)
            default:
                return "";
        }
    }

    public static String fromNumber22(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "220";  // Gambia (Republic of the)
            case "1":
                return "221";  // Senegal (Republic of)
            case "2":
                return "222";  // Mauritania (Islamic Republic of)
            case "3":
                return "223";  // Mali (Republic of)
            case "4":
                return "224";  // Guinea (Republic of)
            case "5":
                return "225";  // Côte d'Ivoire (Republic of)
            case "6":
                return "226";  // Burkina Faso
            case "7":
                return "227";  // Niger (Republic of the)
            case "8":
                return "228";  // Togolese Republic
            case "9":
                return "229";  // Benin (Republic of)
            default:
                return "";
        }
    }

    public static String fromNumber23(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "230";  // Mauritius (Republic of)
            case "1":
                return "231";  // Liberia (Republic of)
            case "2":
                return "232";  // Sierra Leone
            case "3":
                return "233";  // Ghana
            case "4":
                return "234";  // Nigeria (Federal Republic of)
            case "5":
                return "235";  // Chad (Republic of)
            case "6":
                return "236";  // Central African Republic
            case "7":
                return "237";  // Cameroon (Republic of)
            case "8":
                return "238";  // Cape Verde (Republic of)
            case "9":
                return "239";  // Sao Tome and Principe (Democratic Republic of)
            default:
                return "";
        }
    }

    public static String fromNumber24(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "240";  // Equatorial Guinea (Republic of)
            case "1":
                return "241";  // Gabonese Republic
            case "2":
                return "242";  // Congo (Republic of the)
            case "3":
                return "243";  // Democratic Republic of the Congo
            case "4":
                return "244";  // Angola (Republic of)
            case "5":
                return "245";  // Guinea-Bissau (Republic of)
            case "6":
                return "246";  // Diego Garcia
            case "7":
                return "247";  // {Saint Helena,} Ascension {and Tristan da Cunha}
            case "8":
                return "248";  // Seychelles (Republic of)
            case "9":
                return "249";  // Sudan (Republic of the)
            default:
                return "";
        }
    }

    public static String fromNumber25(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "250";  // Rwanda (Republic of)
            case "1":
                return "251";  // Ethiopia (Federal Democratic Republic of)
            case "2":
                return "252";  // Somali Democratic Republic
            case "3":
                return "253";  // Djibouti (Republic of)
            case "4":
                return "254";  // Kenya (Republic of)
            case "5":
                return "255";  // Tanzania (United Republic of)
            case "6":
                return "256";  // Uganda (Republic of)
            case "7":
                return "257";  // Burundi (Republic of)
            case "8":
                return "258";  // Mozambique (Republic of)
            default:
                return "";
        }
    }
    public static String fromNumber26(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "260";  // Zambia (Republic of)
            case "1":
                return "261";  // Madagascar (Republic of)
            case "2":
                return "262";  // French Departments and Territories in the Indian Ocean
            case "3":
                return "263";  // Zimbabwe (Republic of)
            case "4":
                return "264";  // Namibia (Republic of)
            case "5":
                return "265";  // Malawi
            case "6":
                return "266";  // Lesotho (Kingdom of)
            case "7":
                return "267";  // Botswana (Republic of)
            case "8":
                return "268";  // Swaziland (Kingdom of)
            case "9":
                return "269";  // Comoros (Union of the)
            default:
                return "";
        }
    }

    public static String fromNumber29(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "290";  // Saint Helena{, Ascension} and Tristan da Cunha
            case "1":
                return "291";  // Eritrea
            case "7":
                return "297";  // Aruba
            case "8":
                return "298";  // Faroe Islands
            case "9":
                return "299";  // Greenland (Denmark)
            default:
                return "";
        }
    }

    public static String fromNumber3(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "30";  // Greece
            case "1":
                return "31";  // Netherlands (Kingdom of the)
            case "2":
                return "32";  // Belgium
            case "3":
                return "33";  // France
            case "4":
                return "34";  // Spain
            case "5":
                return fromNumber35(number.substring(1));
            case "6":
                return "36";  // Hungary (Republic of)
            case "7":
                return fromNumber37(number.substring(1));
            case "8":
                return fromNumber38(number.substring(1));
            case "9":
                return "39";  //  Italy AND Vatican City State
            default:
                return "";
        }
    }

    public static String fromNumber35(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "350";  // Gibraltar
            case "1":
                return "351";  // Portugal
            case "2":
                return "352";  // Luxembourg
            case "3":
                return "353";  // Ireland
            case "4":
                return "354";  // Iceland
            case "5":
                return "355";  // Albania (Republic of)
            case "6":
                return "356";  // Malta
            case "7":
                return "357";  // Cyprus (Republic of)
            case "8":
                return "358";  // Finland
            case "9":
                return "359";  // Bulgaria (Republic of)
            default:
                return "";
        }
    }

    public static String fromNumber37(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "370";  // Lithuania (Republic of)
            case "1":
                return "371";  // Latvia (Republic of)
            case "2":
                return "372";  // Estonia (Republic of)
            case "3":
                return "373";  // Moldova (Republic of)
            case "4":
                return "374";  // Armenia (Republic of)
            case "5":
                return "375";  // Belarus (Republic of)
            case "6":
                return "376";  // Andorra (Principality of)
            case "7":
                return "377";  // Monaco (Principality of)
            case "8":
                return "378";  // San Marino (Republic of)
            case "9":
                return "379";  // Vatican City State
            default:
                return "";
        }
    }

    public static String fromNumber38(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "380";  // Ukraine
            case "1":
                return "381";  // Serbia (Republic of)
            case "2":
                return "382";  // Montenegro (Republic of)
            case "5":
                return "385";  // Croatia (Republic of)
            case "6":
                return "386";  // Slovenia (Republic of)
            case "7":
                return "387";  // Bosnia and Herzegovina
            case "8":
                return "388";  // Group of countries, shared code TODO: is it still valid? not on ITU number plan overview page
            case "9":
                return "389";  // The Former Yugoslav Republic of Macedonia - North Macedonia (ITU number plan overview page)
            default:
                return "";
        }
    }

    public static String fromNumber4(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "40";  // Romania
            case "1":
                return "41";  // Switzerland (Confederation of)
            case "2":
                return fromNumber42(number.substring(1));
            case "3":
                return "43";  // Austria
            case "4":
                return "44";  // United Kingdom of Great Britain and Northern Ireland
            case "5":
                return "45";  // Denmark
            case "6":
                return "46";  // Sweden
            case "7":
                return "47";  // Norway
            case "8":
                return "48";  // Poland (Republic of)
            case "9":
                return "49";  // Germany (Federal Republic of)
            default:
                return "";
        }
    }

    public static String fromNumber42(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "420";  // Czech Republic
            case "1":
                return "421";  // Slovak Republic
            case "3":
                return "423";  // Liechtenstein (Principality of)
            default:
                return "";
        }
    }

    public static String fromNumber5(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return fromNumber50(number.substring(1));
            case "1":
                return "51";  // Peru
            case "2":
                return "52";  // Mexico
            case "3":
                return "53";  // Cuba
            case "4":
                return "54";  // Argentine Republic
            case "5":
                return "55";  // Brazil (Federative Republic of)
            case "6":
                return "56";  // Chile
            case "7":
                return "57";  // Colombia (Republic of)
            case "8":
                return "58";  // Venezuela (Bolivarian Republic of)
            case "9":
                return fromNumber59(number.substring(1));
            default:
                return "";
        }
    }

    public static String fromNumber50(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "500";  // Falkland Islands (Malvinas)
            case "1":
                return "501";  // Belize
            case "2":
                return "502";  // Guatemala (Republic of)
            case "3":
                return "503";  // El Salvador (Republic of)
            case "4":
                return "504";  // Honduras (Republic of)
            case "5":
                return "505";  // Nicaragua
            case "6":
                return "506";  // Costa Rica
            case "7":
                return "507";  // Panama (Republic of)
            case "8":
                return "508";  // Saint Pierre and Miquelon (Collectivité territoriale de la République française)
            case "9":
                return "509";  // Haiti (Republic of)
            default:
                return "";
        }
    }

    public static String fromNumber59(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "590";  // Guadeloupe (French Department of)
            case "1":
                return "591";  // Bolivia (Plurinational State of)
            case "2":
                return "592";  // Guyana
            case "3":
                return "593";  // Ecuador
            case "4":
                return "594";  // French Guiana (French Department of)
            case "5":
                return "595";  // Paraguay (Republic of)
            case "6":
                return "596";  // Martinique (French Department of)
            case "7":
                return "597";  // Suriname (Republic of)
            case "8":
                return "598";  // Uruguay (Eastern Republic of)
            case "9":
                return "599";  // Bonaire, Saint Eustatius and Saba AND Curaçao
            default:
                return "";
        }
    }

    public static String fromNumber6(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "60";  // Malaysia
            case "1":
                return "61";  // Australia
            case "2":
                return "62";  // Indonesia (Republic of)
            case "3":
                return "63";  // Philippines (Republic of the)
            case "4":
                return "64";  // New Zealand
            case "5":
                return "65";  // Singapore (Republic of)
            case "6":
                return "66";  // Thailand
            case "7":
                return fromNumber67(number.substring(1));
            case "8":
                return fromNumber68(number.substring(1));
            case "9":
                return fromNumber69(number.substring(1));
            default:
                return "";
        }
    }

    public static String fromNumber67(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "670";  // Democratic Republic of Timor-Leste
            case "2":
                return "672";  // Australian External Territories
            case "3":
                return "673";  //Brunei Darussalam
            case "4":
                return "674";  // Nauru (Republic of)
            case "5":
                return "675";  // Papua New Guinea
            case "6":
                return "676";  // Tonga (Kingdom of)
            case "7":
                return "677";  // Solomon Islands
            case "8":
                return "678";  // Vanuatu (Republic of)
            case "9":
                return "679";  // Fiji (Republic of)
            default:
                return "";
        }
    }

    public static String fromNumber68(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "680";  // Palau (Republic of)
            case "1":
                return "681";  // Wallis and Futuna (Territoire français d'outre-mer)
            case "2":
                return "682";  // Cook Islands
            case "3":
                return "683";  // Niue
            case "5":
                return "685";  // Samoa (Independent State of)
            case "6":
                return "686";  // Kiribati (Republic of)
            case "7":
                return "687";  // New Caledonia (Territoire français d'outre-mer)
            case "8":
                return "688";  // Tuvalu
            case "9":
                return "689";  // French Polynesia (Territoire français d'outre-mer)
            default:
                return "";
        }
    }

    public static String fromNumber69(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "690";  // Tokelau
            case "1":
                return "691";  // Micronesia (Federated States of)
            case "2":
                return "692";  // Marshall Islands (Republic of the)
            default:
                return "";
        }
    }

    public static String fromNumber8(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return fromNumber80(number.substring(1));
            case "1":
                return "81";  // Japan
            case "2":
                return "82";  // Korea (Republic of)
            case "4":
                return "84";  // Viet Nam (Socialist Republic of)
            case "5":
                return fromNumber85(number.substring(1));
            case "6":
                return "86";  // China (People's Republic of)
            case "7":
                return fromNumber87(number.substring(1));
            case "8":
                return fromNumber88(number.substring(1));
            default:
                return "";
        }
    }

    public static String fromNumber80(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "800";  // International Freephone Service
            case "8":
                return "808";  // International Shared Cost Service (ISCS)
            default:
                return "";
        }
    }

    public static String fromNumber85(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "850";  // Democratic People's Republic of Korea
            case "2":
                return "852";  // Hong Kong, China
            case "3":
                return "853";  // Macao, China
            case "5":
                return "855";  // Cambodia (Kingdom of)
            case "6":
                return "856";  // Lao People's Democratic Republic
            default:
                return "";
        }
    }

    public static String fromNumber87(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "870";  // Inmarsat SNAC
            case "5":
                return "875";  // Reserved - Maritime Mobile Service Applications
            case "6":
                return "876";  // Reserved - Maritime Mobile Service Applications
            case "7":
                return "877";  // Reserved - Maritime Mobile Service Applications
            case "8":
                return "878";  // Universal Personal Telecommunication Service (UPT)
            case "9":
                return "879";  // Reserved for national non-commercial purposes
            default:
                return "";
        }
    }

    public static String fromNumber88(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "880";  // Bangladesh (People's Republic of)
            case "1":
                return "881";  // Global Mobile Satellite System (GMSS), shared code
            case "2":
                return "882";  // International Networks, shared code
            case "3":
                return "883";  // International Networks, shared code
            case "6":
                return "886";  // Taiwan, China
            case "8":
                return "888";  // Telecommunications for Disaster Relief (TDR)
            default:
                return "";
        }
    }

    public static String fromNumber9(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "90";  // Turkey
            case "1":
                return "91";  // India (Republic of)
            case "2":
                return "92";  // Pakistan (Islamic Republic of)
            case "3":
                return "93";  //Afghanistan
            case "4":
                return "94";  //Sri Lanka (Democratic Socialist Republic of)
            case "5":
                return "95";  // Myanmar (the Republic of the Union of)
            case "6":
                return fromNumber96(number.substring(1));
            case "7":
                return fromNumber97(number.substring(1));
            case "8":
                return "98";  // Iran (Islamic Republic of)
            case "9":
                return fromNumber99(number.substring(1));
            default:
                return "";
        }
    }

    public static String fromNumber96(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "960";  // Maldives (Republic of)
            case "1":
                return "961";  // Lebanon
            case "2":
                return "962";  // Jordan (Hashemite Kingdom of)
            case "3":
                return "963";  // Syrian Arab Republic
            case "4":
                return "964";  // Iraq (Republic of)
            case "5":
                return "965";  // Kuwait (State of)
            case "6":
                return "966";  // Saudi Arabia (Kingdom of)
            case "7":
                return "967";  // Yemen (Republic of)
            case "8":
                return "968";  // Oman (Sultanate of)
            case "9":
                return "969";  // Reserved - reservation currently under investigation
            default:
                return "";
        }
    }

    public static String fromNumber97(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "0":
                return "970";  // Reserved
            case "1":
                return "971";  // United Arab Emirates
            case "2":
                return "972";  // Israel (State of)
            case "3":
                return "973";  // Bahrain (Kingdom of)
            case "4":
                return "974";  // Qatar (State of)
            case "5":
                return "975";  // Bhutan (Kingdom of)
            case "6":
                return "976";  // Mongolia
            case "7":
                return "977";  // Nepal (Federal Democratic Republic of)
            case "9":
                return "979";  // International Premium Rate Service (IPRS)
            default:
                return "";
        }
    }

    public static String fromNumber99(String number) {
        if ((number == null) || (number.length()<1)) {
            return "";
        }

        switch (number.substring(0, 1)) {
            case "1":
                return "991";  // Trial of a proposed new international telecommunication public correspondence service, shared code
            case "2":
                return "992";  // Tajikistan (Republic of)
            case "3":
                return "993";  // Turkmenistan
            case "4":
                return "994";  // Azerbaijani Republic
            case "5":
                return "995";  // Georgia
            case "6":
                return "996";  // Kyrgyz Republic
            case "8":
                return "998";  // Uzbekistan (Republic of)
            case "9":
                return "999";  // Reserved for future global service
            default:
                return "";
        }
    }
}


/**
 * Wrapper around the PhoneLib library from Google
 * <p>
 * Using reflection to access internal information to know if a region has a nation prefix &amp; which one it is or
 * which IDP is used.
 * </p><p>
 * Providing own NumberPlans logic as an alternative to PhoneLib ShortNumber.
 * </p>
 * @see NumberPlan
 */
@Data
public class PhoneLibWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneLibWrapper.class);

    public static final String UNKNOWN_REGIONCODE = "ZZ"; // see https://github.com/google/libphonenumber/blob/5e9507a46051405120bc73fcc13d0b0be1b93c29/java/libphonenumber/test/com/google/i18n/phonenumbers/RegionCode.java#L62

    /**
     * The given number reduced to characters which could be dialed
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    String dialableNumber;

    /**
     * The given number normalized with PhoneLib, risking we get a incorrect normalization
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#isNormalizingTried()
     * @see PhoneLibWrapper#getSemiNormalizedNumber()
     */
    Phonenumber.PhoneNumber semiNormalizedNumber;

    /**
     * The given region code for which the given number should be normalized.<br/>
     * This is an ISO2 code for the country.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    String regionCode;

    /**
     * The number plan metadata which PhoneLib is using for the given region code.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    Phonemetadata.PhoneMetadata metadata;

    /**
     * An instance of the PhoneLib short number utility.
     */
    private static final ShortNumberInfo shortNumberUtil = ShortNumberInfo.getInstance();

    /**
     * An instance of the PhoneLib number utility.
     */
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    /**
     * Storing if PhoneLib has been used to parse the given number into semiNormalizedNumber.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#semiNormalizedNumber
     * @see PhoneLibWrapper#isNormalizingTried()
     */
    private boolean isNormalizingTried = false;

    /**
     * Initialize the wrapper by giving a phone number to be analyzed against a number plan of a given region
     * @param number the phone number to be analyzed
     * @param regionCode the ISO2 Code of the Region / Country, which telephone number plan is used
     */
    public PhoneLibWrapper(String number, String regionCode) {
        this.regionCode = regionCode;
        this.metadata = getMetadataForRegion(this.regionCode);

        if (number != null) {
            this.dialableNumber = PhoneNumberUtil.normalizeDiallableCharsOnly(number);

            if (this.dialableNumber.isEmpty()) {
                this.dialableNumber = "";
            } else {
                if (!isSpecialFormat(dialableNumber)) {
                    // Number needs normalization:
                    // international prefix is added by the lib even if it's not valid in the number plan.
                    this.isNormalizingTried = true;
                    this.semiNormalizedNumber = PhoneLibWrapper.parseNumber(dialableNumber, regionCode);
                }
            }
        }
    }

    /**
     * If PhoneLib has been used to parse the given number into semiNormalizedNumber.
     *
     * @return {@link PhoneLibWrapper#isNormalizingTried}
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public boolean isNormalizingTried() {
        return isNormalizingTried;
    }

    /**
     * Using PhoneLib short number utility if it identifies the given number as a short number, which would not need a NAC.
     * <p>
     * This is a fallback for {@link PhoneLibWrapper#isShortNumber(NumberPlan)}, when we do not have an own number plan information.
     * </p>
     * @return if PhoneLib identifies given number as a short number
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#isShortNumber(NumberPlan)
     */
    public boolean isShortNumber() {
        return shortNumberUtil.isPossibleShortNumber(this.getSemiNormalizedNumber());
    }

    /**
     * Using own {@link NumberPlan} to identify if the given number is a short number, which would not need a NAC.
     * <p>
     * If no number plan is given, {@link PhoneLibWrapper#isShortNumber} is used as fallback.
     * </p>
     * @param numberplan the number plan we identified to be used for a check
     * @return if number plan or as fallback PhoneLib identifies given number as a short number
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public boolean isShortNumber(NumberPlan numberplan) {
        if (numberplan == null) {
            return this.isShortNumber();
        }
        return numberplan.isMatchingShortNumber(this.getDialableNumber());
    }

    /**
     * If we have a plain national number based on regions number plan and potential NAC logic.
     * <p>
     * For a number plan without NAC logic, it will always return false!
     * </p>
     * @return if given number could have CC and NAC, but does not have any of them.
     */
    public boolean hasNoCountryCodeNorNationalAccessCode() {
        // if given number has no NAC and no CC, it equals national phone number (without NAC).
        if (! Objects.equals(dialableNumber, this.getNationalPhoneNumberWithoutNationalAccessCode())) {
            return false;
        }
        // checking the regions number plan, if a NAC logic can be applied - if not there would be no option of having a NAC or not.
        return hasRegionNationalAccessCode();
    }

    /**
     * Using PhoneLib to get a E164 formatted representation of the given number
     * <p>
     * This is a straight invocation, so no compensation of some inaccuracy is done here.
     * </p>
     * @return E164 format of the given phone number
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public String getE164Formatted() {
        return phoneUtil.format(this.semiNormalizedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    /**
     * If we know the given region for the given number {@link PhoneLibWrapper#hasRegionNationalAccessCode()}, this method checks if the given number does not start with a NAC nor a CC,
     * so we could permanently add a default NDC and NAC to the given number and for this new value the method directly return a E164 formatted representation.
     * @param nationalAccessCode the NAC to be added e.g. for Germany it would be "0"
     * @param defaultNationalDestinationCode the NDC to be added depending on the use telephone line origination.
     * @return if possible a E164 formatted representation or just the diallable representation of the given number.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    public String extendNumberByDefaultAreaCodeAndCountryCode(String nationalAccessCode, String defaultNationalDestinationCode) {
        String nationalPhoneNumberWithoutNationalAccessCode = this.getNationalPhoneNumberWithoutNationalAccessCode();
        //if the dialableNumber is same as the national Number, Without NationalPrefix, then there is no NDC, so it needs to be added.
        if (Objects.equals(dialableNumber, nationalPhoneNumberWithoutNationalAccessCode)) {

            String extendedNumber = nationalAccessCode + defaultNationalDestinationCode + nationalPhoneNumberWithoutNationalAccessCode;

            try {
                this.semiNormalizedNumber = phoneUtil.parse(extendedNumber, regionCode);
                // after area code has been added, we can add the country code by the lib:
                return getE164Formatted();
            } catch (NumberParseException e) {
                LOGGER.warn("could not parse extended number: {}", extendedNumber);
                LOGGER.debug("{}", e.getMessage());
                return dialableNumber;
            }
        } else {
            //it seems we have nationalnumber with national prefix, so we could add country code:
            return getE164Formatted();
        }
    }

    /**
     * Some Special dial-able characters make a number either not necessary to be normalized ("+" is already normalized) or can't be normalized ("*" control codes)
     * @param value phone number representation
     * @return if phone number starts with special characters which makes normalization unable / not necessary
     */
    static boolean isSpecialFormat(String value) {
        //+: Number is already in "+" ... International Format:
        //*: Number is internal and cannot be normalized
        if (value == null || value.length()==0) {
            return false;
        }
        String firstChar = value.substring(0, 1);
        return ("+".equals(firstChar)) || ("*".equals(firstChar));
    }

    /**
     * Checks if a given number starts with the given IDP (or the international IDP short form '+')
     * @param value the number to be checked
     * @param idp the IDP to be used searched for
     * @return if either given IDP or '+' is the beginning of the value
     */
    private static boolean isIDPUsed(String value, String idp) {
        if (idp == null || idp.length()==0) {
            return ("+".equals(value.substring(0, 1)));
        }

        return (("+".equals(value.substring(0, 1))) || (value.startsWith(idp)));
    }

    /**
     * Checks if a given number starts with the IDP (or the international IDP short form '+') of the given region
     * @param value the number to be checked
     * @param regionCode ISO2 code for the regions number plan used for checking IDP
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public static boolean startsWithIDP(String value, String regionCode) {
        if (value == null || value.length()==0) {
            return false;
        }

        String idp = getInternationalDialingPrefix(regionCode);

        return isIDPUsed(value, idp);
    }

    /**
     * Checks if the number starts with the IDP (or the international IDP short form '+') of the initializing region
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public boolean startsWithIDP() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        // TODO: AU => 001[14-689]|14(?:1[14]|34|4[17]|[56]6|7[47]|88)0011 ... must be a list and "+"
        String idp = this.getInternationalDialingPrefix();

        return isIDPUsed(this.dialableNumber, idp);
    }

    private int parseCountryCode(boolean alsoFromRegionCode) {
        Phonenumber.PhoneNumber tempNumber = parseNumber(this.dialableNumber, this.regionCode);

        // Using PhoneLib to extract Country Code from Number
        if (tempNumber!=null) {
            int result = tempNumber.getCountryCode();
            if (tempNumber.getCountryCodeSource() == Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY) {
                if (alsoFromRegionCode) {
                    return result;
                } else {
                    return 0;
                }
            }
            if ((tempNumber.getCountryCodeSource() == Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_IDD) ||
                    (tempNumber.getCountryCodeSource() == Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN) ||
                    (tempNumber.getCountryCodeSource() == Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN)) {
                return result;
            }
        }
        return 0;
    }

    public String getCountryCode(boolean alsoFromRegionCode) {
        int parsedCountryCode = parseCountryCode(alsoFromRegionCode);
        if (parsedCountryCode>0) {
            return String.valueOf(parsedCountryCode);
        }

        // FallBack Extraction:
        String numberWithoutIDP = removeIDP();
        String countryCode = CountryCodeExtractor.fromNumber(numberWithoutIDP);

        if (countryCode.length()>0) {
            return countryCode;
        }

        if (alsoFromRegionCode) {
            int regionCountryCode = getCountryCodeForRegion(this.regionCode);
            if (regionCountryCode>0) {
                return String.valueOf(regionCountryCode);
            }
        }

        return "";
    }

    public String removeIDP() {
        if (dialableNumber == null) {
            return "";
        }
        if (dialableNumber.startsWith("+")) {
            return dialableNumber.substring(1);
        }

        if (dialableNumber.startsWith(getInternationalDialingPrefix())) {
            return dialableNumber.substring(getInternationalDialingPrefix().length());
        }

        return "";
    }

    /**
     * Checks if the number starts with the NAC of the initializing region
     * Be aware, that some regions have IDP of 00 and NAC of 0 - so overlaping is also checked.
     */
    public boolean startsWithNAC() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();
        String nac = this.getNationalAccessCode();

        if (idp.startsWith(nac) && dialableNumber.startsWith(idp)) {
            return false;
        }

        return dialableNumber.startsWith(nac);
    }

    public String removeNAC() {
        if (dialableNumber == null) {
            return "";
        }
        if (startsWithNAC()) {
            return dialableNumber.substring(getNationalAccessCode().length());
        } else {
            return "";
        }
    }

    /**
     * Checks if a given number starts with the given IDP (or the international IDP short form '+')
     * @param value the number to be checked
     * @param idp the IDP to be used searched for
     * @return if either given IDP or '+' is the beginning of the value
     */
    private static boolean isIDPUsed(String value, String idp) {
        if (idp == null || idp.length()==0) {
            return ("+".equals(value.substring(0, 1)));
        }

        return (("+".equals(value.substring(0, 1))) || (value.startsWith(idp)));
    }

    /**
     * Checks if a given number starts with the IDP (or the international IDP short form '+') of the given region
     * @param value the number to be checked
     * @param regionCode ISO2 code for the regions number plan used for checking IDP
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public static boolean startsWithIDP(String value, String regionCode) {
        if (value == null || value.length()==0) {
            return false;
        }

        String idp = getInternationalDialingPrefix(regionCode);

        return isIDPUsed(value, idp);
    }

    /**
     * Checks if the number starts with the IDP (or the international IDP short form '+') of the initializing region
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public boolean startsWithIDP() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();

        return isIDPUsed(this.dialableNumber, idp);
    }

    /**
     * Checks if the number starts with the NAC of the initializing region
     * Be aware, that some regions have IDP of 00 and NAC of 0 - so overlaping is also checked.
     */
    public boolean startsWithNAC() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();
        String nac = this.getNationalAccessCode();

        if (idp.startsWith(nac) && dialableNumber.startsWith(idp)) {
            return false;

        }

        return dialableNumber.startsWith(nac);

    }

    /**
     * Checks if a given number starts with the given IDP (or the international IDP short form '+')
     * @param value the number to be checked
     * @param idp the IDP to be used searched for
     * @return if either given IDP or '+' is the beginning of the value
     */
    private static boolean isIDPUsed(String value, String idp) {
        if (idp == null || idp.length()==0) {
            return ("+".equals(value.substring(0, 1)));
        }

        return (("+".equals(value.substring(0, 1))) || (value.startsWith(idp)));
    }

    /**
     * Checks if a given number starts with the IDP (or the international IDP short form '+') of the given region
     * @param value the number to be checked
     * @param regionCode ISO2 code for the regions number plan used for checking IDP
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public static boolean startsWithIDP(String value, String regionCode) {
        if (value == null || value.length()==0) {
            return false;
        }

        String idp = getInternationalDialingPrefix(regionCode);

        return isIDPUsed(value, idp);
    }

    /**
     * Checks if the number starts with the IDP (or the international IDP short form '+') of the initializing region
     * @return if either regions IDP or '+' is the beginning of the value
     */
    public boolean startsWithIDP() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();

        return isIDPUsed(this.dialableNumber, idp);
    }

    /**
     * Checks if the number starts with the NAC of the initializing region
     * Be aware, that some regions have IDP of 00 and NAC of 0 - so overlaping is also checked.
     */
    public boolean startsWithNAC() {
        if (this.dialableNumber == null || this.dialableNumber.length()==0) {
            return false;
        }

        String idp = this.getInternationalDialingPrefix();
        String nac = this.getNationalAccessCode();

        if (idp.startsWith(nac) && dialableNumber.startsWith(idp)) {
            return false;

        }

        return dialableNumber.startsWith(nac);

    }

    /**
     * Use PhoneLib to parse a number for a regions code. If any exception occurs, they are logged and null is returned.
     * @param number the phone number to be parsed
     * @param regionCode ISO2 code for the regions number plan used for parsing the number
     * @return either the parsed {@link Phonenumber.PhoneNumber} or null
     */
    private static Phonenumber.PhoneNumber parseNumber(String number, String regionCode) {
        try {
            return phoneUtil.parse(number, regionCode);
            // international prefix is added by the lib even if it's not valid in the number plan.
        } catch (NumberParseException e) {
            LOGGER.warn("could not parse normalize number: {}", number);
            LOGGER.debug("{}", e.getMessage());
            return null;
        }
    }


    private static String internationalDialingPrefix(Phonemetadata.PhoneMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        return metadata.getInternationalPrefix();
    }

    /**
     * The International Dialing Prefix used in the given region from PhoneLib
     * @return IDP of given {@link PhoneLibWrapper#regionCode}
     */
    public String getInternationalDialingPrefix() {
        return internationalDialingPrefix(this.metadata);
    }

    /**
     * The International Dialing Prefix used in the given region from PhoneLib
     *
     * @param regionCode the Region which NAC is requested.
     * @return IDP of given regionCode
     */
    static public String getInternationalDialingPrefix(String regionCode) {
        return internationalDialingPrefix(getMetadataForRegion(regionCode));
    }


    private static String nationalAccessCode(Phonemetadata.PhoneMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        return metadata.getNationalPrefix();
    }

    /**
     * The National Access Code used before the National Destination Code in the given region from PhoneLib
     * @return NAC of given {@link PhoneLibWrapper#regionCode}
     */
    public String getNationalAccessCode() {
        return nationalAccessCode(this.metadata);
    }

    /**
     * The National Access Code used before the National Destination Code in the given region from PhoneLib
     *
     * @param regionCode the Region which NAC is requested.
     * @return NAC of given regionCode
     */
    static public String getNationalAccessCode(String regionCode) {
        return nationalAccessCode(getMetadataForRegion(regionCode));
    }

    /**
     * From PhoneLib, if a National Access Code is used before the National Destination Code in the given region
     * @return if given {@link PhoneLibWrapper#regionCode} is using NAC
     */
    public boolean hasRegionNationalAccessCode() {
        return metadata != null && metadata.hasNationalPrefix();
    }

    /**
     * Since we need the PhoneMetadta for fixing calculation of some number normalization,
     * we need to break encapsulation via reflection, because that data is private to phoneUtil
     * and Google rejected suggestion to make it public, because they did not see our need in correcting normalization.
     * @return {@link Phonemetadata.PhoneMetadata} of {@link PhoneLibWrapper#regionCode}
     */
    static private Phonemetadata.PhoneMetadata getMetadataForRegion(String regionCode) {
        try {
            Method m = phoneUtil.getClass().getDeclaredMethod("getMetadataForRegion", String.class);
            // violating encupsulation is intended by this method, so no need for SONAR code smell warning here
            m.setAccessible(true); //NOSONAR
            return (Phonemetadata.PhoneMetadata) m.invoke(phoneUtil, regionCode);
        } catch (Exception e) {
            LOGGER.warn("Error while accessing getMetadataForRegion on PhoneNumberUtil via Reflection.");
            LOGGER.debug("{}", e.getMessage());
            return null;
        }
    }

    /**
     * Using PhoneLib to get the national number from the given number
     *
     * @return national number without NAC, but any other leading zero.
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     * @see PhoneLibWrapper#getSemiNormalizedNumber()
     * @see PhoneLibWrapper#nationalPhoneNumberWithoutNationalPrefix(Phonenumber.PhoneNumber)
     */
    private String getNationalPhoneNumberWithoutNationalAccessCode() {
        return PhoneLibWrapper.nationalPhoneNumberWithoutNationalPrefix(this.semiNormalizedNumber);
    }

    /**
     * Using PhoneLib to get the national number from a parsed phone number with leading zeros, if those are not representing a National Access Code.
     * <p/>
     * This is necessary, because PhoneLib is storing the national number as a long, so leading "0" Digits as part of it are stored in other attributes.
     * @param phoneNumber A PhoneLib parsed phone number
     * @return national number part without NationalPrefix (aka NAC) but any other leading zero.
     */
    private static String nationalPhoneNumberWithoutNationalPrefix(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber==null) {
            return null;
        }
        StringBuilder nationalNumber = new StringBuilder(Long.toString(phoneNumber.getNationalNumber()));
        // if-clause necessary, because getNumberOfLeadingZeros is always 1 for a possible trunc code and special 0 in Italy
        if (phoneNumber.hasNumberOfLeadingZeros() || phoneNumber.hasItalianLeadingZero())
            for (int i = 0; i < phoneNumber.getNumberOfLeadingZeros(); i++) {
                nationalNumber.insert(0, "0");
            }
        return nationalNumber.toString();
    }

    /**
     * Using PhoneLib to get the Country Calling Code for a region code
     * <p>
     * e.g. "DE" is "49"
     * </p>
     * @param regionCode ISO2 code of a region
     * @return country calling code of the region or 0 if regionCode is invalid.
     */
    public static int getCountryCodeForRegion(String regionCode) {
        return phoneUtil.getCountryCodeForRegion(regionCode);
    }

    /**
     * Using PhoneLib to get the region code for a Country Calling Code
     * <p>
     * e.g. "49" is "DE"
     * </p>
     * @param countryCode only digits without IDP
     * @return regionCode or {@link PhoneLibWrapper#UNKNOWN_REGIONCODE} if countryCode is invalid.
     */
    public static String getRegionCodeForCountryCode(String countryCode) {
        try {
            return phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        } catch (Exception e) {
            LOGGER.info("Error while parsing Country Code: {}", countryCode);
            LOGGER.debug("{}", e.getMessage());
            return PhoneLibWrapper.UNKNOWN_REGIONCODE;
        }
    }


    /**
     * Using PhoneLib to check the number by isPossibleWithReason code. If number has been parsed during initialization
     * this is a straight invocation, so no compensation of some inaccuracy is done here. Otherwise, parsing is done
     * locally and exceptions are directly mapped to a result.
     * </p>
     * @return PhoneNumberUtil.ValidationResult which is PhoneLib isPossible Reason code
     *
     * @see PhoneLibWrapper#PhoneLibWrapper(String, String)
     */
    private PhoneNumberUtil.ValidationResult isPossibleWithReason() {
        if (semiNormalizedNumber == null) {
            try {
                Phonenumber.PhoneNumber tempNumber = phoneUtil.parse(dialableNumber, regionCode);
                return phoneUtil.isPossibleNumberWithReason(tempNumber);
                // international prefix is added by the lib even if it's not valid in the number plan.
            } catch (NumberParseException e) {
                LOGGER.info("could not parse normalize number: {}", dialableNumber);
                LOGGER.debug("{}", e.getMessage());

                switch (e.getErrorType()) {
                    case INVALID_COUNTRY_CODE:
                        return PhoneNumberUtil.ValidationResult.INVALID_COUNTRY_CODE;
                    case TOO_SHORT_NSN:
                        return PhoneNumberUtil.ValidationResult.TOO_SHORT;
                    case TOO_SHORT_AFTER_IDD:
                        return PhoneNumberUtil.ValidationResult.TOO_SHORT;
                    case TOO_LONG:
                        return PhoneNumberUtil.ValidationResult.TOO_LONG;
                    default:
                        // NOT_A_NUMBER
                        return PhoneNumberUtil.ValidationResult.INVALID_LENGTH;
                }
            }
        }
        return phoneUtil.isPossibleNumberWithReason(semiNormalizedNumber);
    }


    /**
     * Using PhoneLib to check the number by isPossibleWithReason code by internal wrapper method isPossibleWithReason
     * and map the result to PhoneNumberValidationResult type
     *
     * @return PhoneNumberValidationResult
     *
     * @see PhoneLibWrapper#isPossibleWithReason()
     * @see PhoneNumberValidationResult
     */
    public PhoneNumberValidationResult validate() {
        return PhoneNumberValidationResult.byPhoneLibValidationResult(isPossibleWithReason());
    }


}
