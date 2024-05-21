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
package de.telekom.phonenumbernormalizer.extern.libphonenumber

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder
import spock.lang.Specification

import java.util.logging.Logger

class PhoneNumberOfflineGeocoderTest extends Specification {

    PhoneNumberUtil phoneUtil
    PhoneNumberOfflineGeocoder geocoder

    Logger logger = Logger.getLogger(PhoneNumberOfflineGeocoderTest.class.toString())

    static final boolean LOGONLYUNEXPECTED = true

    def "setup"() {
        this.phoneUtil = PhoneNumberUtil.getInstance()
        this.geocoder = PhoneNumberOfflineGeocoder.getInstance()
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4\$-7s: %5\$s %n")
    }

    def "check German area name"(String areacode, String expectedResult, expectingFail) {
        given:
        def phoneNumber = phoneUtil.parse("0" + areacode + "555123", "DE")

        when: "get area name for area code: $areacode"

        def result = geocoder.getDescriptionForNumber( phoneNumber, Locale.GERMAN )
        def result2 = result.replace("-", " ")

        then: "is number expected: $expectedResult"
        if ((result != expectedResult) && (result2 != expectedResult)){
            if (expectingFail) {
                if (!LOGONLYUNEXPECTED) {
                    logger.info("PhoneLib is still not correctly labeling $areacode to $expectedResult by giving $result")
                }
            } else {
                logger.warning("PhoneLib is suddenly not correctly labeling $areacode to $expectedResult by giving $result")
            }
        } else {
            if (expectingFail) {
                logger.info("!!! PhoneLib is now correctly labeling $areacode to $expectedResult !!!")
            }
        }

        where:
        // BNetzA 27.07.2022: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vorwahlverzeichnis_ONB.zip.html
        // ITU 14.09.2022: https://www.itu.int/oth/T0202000051/en
        areacode | expectedResult                           | expectingFail
        "201"    | "Essen"                                  | false
        "202"    | "Wuppertal"                              | false
        "203"    | "Duisburg"                               | false
        "2041"   | "Bottrop"                                | false
        "2043"   | "Gladbeck"                               | false
        "2045"   | "Bottrop Kirchhellen"                    | false
        "2051"   | "Velbert"                                | false
        "2052"   | "Velbert Langenberg"                     | false
        "2053"   | "Velbert Neviges"                        | false
        "2054"   | "Essen Kettwig"                          | false
        "2056"   | "Heiligenhaus"                           | false
        "2058"   | "Wülfrath"                               | false
        "2064"   | "Dinslaken"                              | false
        "2065"   | "Duisburg Rheinhausen"                   | false
        "2066"   | "Duisburg Homberg"                       | false
        "208"    | "Oberhausen Rheinland"                   | false
        "209"    | "Gelsenkirchen"                          | false
        "2102"   | "Ratingen"                               | false
        "2103"   | "Hilden"                                 | false
        "2104"   | "Mettmann"                               | false
        "211"    | "Düsseldorf"                             | false
        "212"    | "Solingen"                               | false
        "2129"   | "Haan Rheinland"                         | false
        "2131"   | "Neuss"                                  | false
        "2132"   | "Meerbusch Büderich"                     | false
        "2133"   | "Dormagen"                               | false
        "2137"   | "Neuss Norf"                             | false
        "214"    | "Leverkusen"                             | false
        "2150"   | "Meerbusch Lank"                         | false
        "2151"   | "Krefeld"                                | false
        "2152"   | "Kempen"                                 | false
        "2153"   | "Nettetal Lobberich"                     | false
        "2154"   | "Willich"                                | false
        "2156"   | "Willich Anrath"                         | false
        "2157"   | "Nettetal Kaldenkirchen"                 | false
        "2158"   | "Grefrath bei Krefeld"                   | false
        "2159"   | "Meerbusch Osterath"                     | false
        "2161"   | "Mönchengladbach"                        | false
        "2162"   | "Viersen"                                | false
        "2163"   | "Schwalmtal Niederrhein"                 | false
        "2164"   | "Jüchen Otzenrath"                       | false
        "2165"   | "Jüchen"                                 | false
        "2166"   | "Mönchengladbach Rheydt"                 | false
        "2171"   | "Leverkusen Opladen"                     | false
        "2173"   | "Langenfeld Rheinland"                   | false
        "2174"   | "Burscheid Rheinland"                    | false
        "2175"   | "Leichlingen Rheinland"                  | false
        "2181"   | "Grevenbroich"                           | false
        "2182"   | "Grevenbroich Kapellen"                  | false
        "2183"   | "Rommerskirchen"                         | false
        "2191"   | "Remscheid"                              | false
        "2192"   | "Hückeswagen"                            | false
        "2193"   | "Dabringhausen"                          | false
        "2195"   | "Radevormwald"                           | false
        "2196"   | "Wermelskirchen"                         | false
        "2202"   | "Bergisch Gladbach"                      | false
        "2203"   | "Köln Porz"                              | false
        "2204"   | "Bensberg"                               | false
        "2205"   | "Rösrath"                                | false
        "2206"   | "Overath"                                | false
        "2207"   | "Kürten Dürscheid"                       | false
        "2208"   | "Niederkassel"                           | false
        "221"    | "Köln"                                   | false
        "2222"   | "Bornheim Rheinland"                     | false
        "2223"   | "Königswinter"                           | false
        "2224"   | "Bad Honnef"                             | false
        "2225"   | "Meckenheim Rheinland"                   | false
        "2226"   | "Rheinbach"                              | false
        "2227"   | "Bornheim Merten"                        | false
        "2228"   | "Remagen Rolandseck"                     | false
        "2232"   | "Brühl Rheinland"                        | false
        "2233"   | "Hürth Rheinland"                        | false
        "2234"   | "Frechen"                                | false
        "2235"   | "Erftstadt"                              | false
        "2236"   | "Wesseling Rheinland"                    | false
        "2237"   | "Kerpen Rheinland Türnich"               | false
        "2238"   | "Pulheim"                                | false
        "2241"   | "Siegburg"                               | false
        "2242"   | "Hennef Sieg"                            | false
        "2243"   | "Eitorf"                                 | false
        "2244"   | "Königswinter Oberpleis"                 | false
        "2245"   | "Much"                                   | false
        "2246"   | "Lohmar Rheinland"                       | false
        "2247"   | "Neunkirchen Seelscheid"                 | false
        "2248"   | "Hennef Uckerath"                        | false
        "2251"   | "Euskirchen"                             | false
        "2252"   | "Zülpich"                                | false
        "2253"   | "Bad Münstereifel"                       | false
        "2254"   | "Weilerswist"                            | false
        "2255"   | "Euskirchen Flamersheim"                 | false
        "2256"   | "Mechernich Satzvey"                     | false
        "2257"   | "Reckerscheid"                           | false
        "2261"   | "Gummersbach"                            | false
        "2262"   | "Wiehl"                                  | false
        "2263"   | "Engelskirchen"                          | false
        "2264"   | "Marienheide"                            | false
        "2265"   | "Reichshof Eckenhagen"                   | false
        "2266"   | "Lindlar"                                | false
        "2267"   | "Wipperfürth"                            | false
        "2268"   | "Kürten"                                 | false
        "2269"   | "Kierspe Rönsahl"                        | false
        "2271"   | "Bergheim Erft"                          | false
        "2272"   | "Bedburg Erft"                           | false
        "2273"   | "Kerpen Horrem"                          | false
        "2274"   | "Elsdorf Rheinland"                      | false
        "2275"   | "Kerpen Buir"                            | false
        "228"    | "Bonn"                                   | false
        "2291"   | "Waldbröl"                               | false
        "2292"   | "Windeck Sieg"                           | false
        "2293"   | "Nümbrecht"                              | false
        "2294"   | "Morsbach Sieg"                          | false
        "2295"   | "Ruppichteroth"                          | false
        "2296"   | "Reichshof Brüchermühle"                 | false
        "2297"   | "Wildbergerhütte"                        | false
        "2301"   | "Holzwickede"                            | false
        "2302"   | "Witten"                                 | false
        "2303"   | "Unna"                                   | false
        "2304"   | "Schwerte"                               | false
        "2305"   | "Castrop Rauxel"                         | false
        "2306"   | "Lünen"                                  | false
        "2307"   | "Kamen"                                  | false
        "2308"   | "Unna Hemmerde"                          | false
        "2309"   | "Waltrop"                                | false
        "231"    | "Dortmund"                               | false
        "2323"   | "Herne"                                  | false
        "2324"   | "Hattingen Ruhr"                         | false
        "2325"   | "Wanne Eickel"                           | false
        "2327"   | "Bochum Wattenscheid"                    | false
        "2330"   | "Herdecke"                               | false
        "2331"   | "Hagen Westfalen"                        | false
        "2332"   | "Gevelsberg"                             | false
        "2333"   | "Ennepetal"                              | false
        "2334"   | "Hagen Hohenlimburg"                     | false
        "2335"   | "Wetter Ruhr"                            | false
        "2336"   | "Schwelm"                                | false
        "2337"   | "Hagen Dahl"                             | false
        "2338"   | "Breckerfeld"                            | false
        "2339"   | "Sprockhövel Haßlinghausen"              | false
        "234"    | "Bochum"                                 | false
        "2351"   | "Lüdenscheid"                            | false
        "2352"   | "Altena Westfalen"                       | false
        "2353"   | "Halver"                                 | false
        "2354"   | "Meinerzhagen"                           | false
        "2355"   | "Schalksmühle"                           | false
        "2357"   | "Herscheid Westfalen"                    | false
        "2358"   | "Meinerzhagen Valbert"                   | false
        "2359"   | "Kierspe"                                | false
        "2360"   | "Haltern Lippramsdorf"                   | false
        "2361"   | "Recklinghausen"                         | false
        "2362"   | "Dorsten"                                | false
        "2363"   | "Datteln"                                | false
        "2364"   | "Haltern Westfalen"                      | false
        "2365"   | "Marl"                                   | false
        "2366"   | "Herten Westfalen"                       | false
        "2367"   | "Henrichenburg"                          | false
        "2368"   | "Oer Erkenschwick"                       | false
        "2369"   | "Dorsten Wulfen"                         | false
        "2371"   | "Iserlohn"                               | false
        "2372"   | "Hemer"                                  | false
        "2373"   | "Menden Sauerland"                       | false
        "2374"   | "Iserlohn Letmathe"                      | false
        "2375"   | "Balve"                                  | false
        "2377"   | "Wickede Ruhr"                           | false
        "2378"   | "Fröndenberg Langschede"                 | false
        "2379"   | "Menden Asbeck"                          | false
        "2381"   | "Hamm Westfalen"                         | false
        "2382"   | "Ahlen Westfalen"                        | false
        "2383"   | "Bönen"                                  | false
        "2384"   | "Welver"                                 | false
        "2385"   | "Hamm Rhynern"                           | false
        "2387"   | "Drensteinfurt Walstedde"                | false
        "2388"   | "Hamm Uentrop"                           | false
        "2389"   | "Werne"                                  | false
        "2391"   | "Plettenberg"                            | false
        "2392"   | "Werdohl"                                | false
        "2393"   | "Sundern Allendorf"                      | false
        "2394"   | "Neuenrade Affeln"                       | false
        "2395"   | "Finnentrop Rönkhausen"                  | false
        "2401"   | "Baesweiler"                             | false
        "2402"   | "Stolberg Rheinland"                     | false
        "2403"   | "Eschweiler Rheinland"                   | false
        "2404"   | "Alsdorf Rheinland"                      | false
        "2405"   | "Würselen"                               | false
        "2406"   | "Herzogenrath"                           | false
        "2407"   | "Herzogenrath Kohlscheid"                | false
        "2408"   | "Aachen Kornelimünster"                  | false
        "2409"   | "Stolberg Gressenich"                    | false
        "241"    | "Aachen"                                 | false
        "2421"   | "Düren"                                  | false
        "2422"   | "Kreuzau"                                | false
        "2423"   | "Langerwehe"                             | false
        "2424"   | "Vettweiss"                              | false
        "2425"   | "Nideggen Embken"                        | false
        "2426"   | "Nörvenich"                              | false
        "2427"   | "Nideggen"                               | false
        "2428"   | "Niederzier"                             | false
        "2429"   | "Hürtgenwald"                            | false
        "2431"   | "Erkelenz"                               | false
        "2432"   | "Wassenberg"                             | false
        "2433"   | "Hückelhoven"                            | false
        "2434"   | "Wegberg"                                | false
        "2435"   | "Erkelenz Lövenich"                      | false
        "2436"   | "Wegberg Rödgen"                         | false
        "2440"   | "Nettersheim Tondorf"                    | false
        "2441"   | "Kall"                                   | false
        "2443"   | "Mechernich"                             | false
        "2444"   | "Schleiden Gemünd"                       | false
        "2445"   | "Schleiden Eifel"                        | false
        "2446"   | "Heimbach Eifel"                         | false
        "2447"   | "Dahlem bei Kall"                        | false
        "2448"   | "Hellenthal Rescheid"                    | false
        "2449"   | "Blankenheim Ahr"                        | false
        "2451"   | "Geilenkirchen"                          | false
        "2452"   | "Heinsberg Rheinland"                    | false
        "2453"   | "Heinsberg Randerath"                    | false
        "2454"   | "Gangelt"                                | false
        "2455"   | "Waldfeucht"                             | false
        "2456"   | "Selfkant"                               | false
        "2461"   | "Jülich"                                 | false
        "2462"   | "Linnich"                                | false
        "2463"   | "Titz"                                   | false
        "2464"   | "Aldenhoven bei Jülich"                  | false
        "2465"   | "Inden"                                  | false
        "2471"   | "Roetgen Eifel"                          | false
        "2472"   | "Monschau"                               | false
        "2473"   | "Simmerath"                              | false
        "2474"   | "Nideggen Schmidt"                       | false
        "2482"   | "Hellenthal"                             | false
        "2484"   | "Mechernich Eiserfey"                    | false
        "2485"   | "Schleiden Dreiborn"                     | false
        "2486"   | "Nettersheim"                            | false
        "2501"   | "Münster Hiltrup"                        | false
        "2502"   | "Nottuln"                                | false
        "2504"   | "Telgte"                                 | false
        "2505"   | "Altenberge Westfalen"                   | false
        "2506"   | "Münster Wolbeck"                        | false
        "2507"   | "Havixbeck"                              | false
        "2508"   | "Drensteinfurt"                          | false
        "2509"   | "Nottuln Appelhülsen"                    | false
        "251"    | "Münster"                                | false
        "2520"   | "Wadersloh Diestedde"                    | false
        "2521"   | "Beckum"                                 | false
        "2522"   | "Oelde"                                  | false
        "2523"   | "Wadersloh"                              | false
        "2524"   | "Ennigerloh"                             | false
        "2525"   | "Beckum Neubeckum"                       | false
        "2526"   | "Sendenhorst"                            | false
        "2527"   | "Lippetal Lippborg"                      | false
        "2528"   | "Ennigerloh Enniger"                     | false
        "2529"   | "Oelde Stromberg"                        | false
        "2532"   | "Ostbevern"                              | false
        "2533"   | "Münster Nienberge"                      | false
        "2534"   | "Münster Roxel"                          | false
        "2535"   | "Sendenhorst Albersloh"                  | false
        "2536"   | "Münster Albachten"                      | false
        "2538"   | "Drensteinfurt Rinkerode"                | false
        "2541"   | "Coesfeld"                               | false
        "2542"   | "Gescher"                                | false
        "2543"   | "Billerbeck Westfalen"                   | false
        "2545"   | "Rosendahl Darfeld"                      | false
        "2546"   | "Coesfeld Lette"                         | false
        "2547"   | "Rosendahl Osterwick"                    | false
        "2548"   | "Dülmen Rorup"                           | false
        "2551"   | "Steinfurt Burgsteinfurt"                | false
        "2552"   | "Steinfurt Borghorst"                    | false
        "2553"   | "Ochtrup"                                | false
        "2554"   | "Laer Kreis Steinfurt"                   | false
        "2555"   | "Schöppingen"                            | false
        "2556"   | "Metelen"                                | false
        "2557"   | "Wettringen Kreis Steinfurt"             | false
        "2558"   | "Horstmar"                               | false
        "2561"   | "Ahaus"                                  | false
        "2562"   | "Gronau Westfalen"                       | false
        "2563"   | "Stadtlohn"                              | false
        "2564"   | "Vreden"                                 | false
        "2565"   | "Gronau Epe"                             | false
        "2566"   | "Legden"                                 | false
        "2567"   | "Ahaus Alstätte"                         | false
        "2568"   | "Heek"                                   | false
        "2571"   | "Greven Westfalen"                       | false
        "2572"   | "Emsdetten"                              | false
        "2573"   | "Nordwalde"                              | false
        "2574"   | "Saerbeck"                               | false
        "2575"   | "Greven Reckenfeld"                      | false
        "2581"   | "Warendorf"                              | false
        "2582"   | "Everswinkel"                            | false
        "2583"   | "Sassenberg"                             | false
        "2584"   | "Warendorf Milte"                        | false
        "2585"   | "Warendorf Hoetmar"                      | false
        "2586"   | "Beelen"                                 | false
        "2587"   | "Ennigerloh Westkirchen"                 | false
        "2588"   | "Harsewinkel Greffen"                    | false
        "2590"   | "Dülmen Buldern"                         | false
        "2591"   | "Lüdinghausen"                           | false
        "2592"   | "Selm"                                   | false
        "2593"   | "Ascheberg Westfalen"                    | false
        "2594"   | "Dülmen"                                 | false
        "2595"   | "Olfen"                                  | false
        "2596"   | "Nordkirchen"                            | false
        "2597"   | "Senden Westfalen"                       | false
        "2598"   | "Senden Ottmarsbocholt"                  | false
        "2599"   | "Ascheberg Herbern"                      | false
        "2601"   | "Nauort"                                 | false
        "2602"   | "Montabaur"                              | false
        "2603"   | "Bad Ems"                                | false
        "2604"   | "Nassau Lahn"                            | false
        "2605"   | "Löf"                                    | false
        "2606"   | "Winningen Mosel"                        | false
        "2607"   | "Kobern Gondorf"                         | false
        "2608"   | "Welschneudorf"                          | false
        "261"    | "Koblenz am Rhein"                       | false
        "2620"   | "Neuhäusel Westerwald"                   | false
        "2621"   | "Lahnstein"                              | false
        "2622"   | "Bendorf am Rhein"                       | false
        "2623"   | "Ransbach Baumbach"                      | false
        "2624"   | "Höhr Grenzhausen"                       | false
        "2625"   | "Ochtendung"                             | false
        "2626"   | "Selters Westerwald"                     | false
        "2627"   | "Braubach"                               | false
        "2628"   | "Rhens"                                  | false
        "2630"   | "Mülheim Kärlich"                        | false
        "2631"   | "Neuwied"                                | false
        "2632"   | "Andernach"                              | false
        "2633"   | "Brohl Lützing"                          | false
        "2634"   | "Rengsdorf"                              | false
        "2635"   | "Rheinbrohl"                             | false
        "2636"   | "Burgbrohl"                              | false
        "2637"   | "Weissenthurm"                           | false
        "2638"   | "Waldbreitbach"                          | false
        "2639"   | "Anhausen Kreis Neuwied"                 | false
        "2641"   | "Bad Neuenahr Ahrweiler"                 | false
        "2642"   | "Remagen"                                | false
        "2643"   | "Altenahr"                               | false
        "2644"   | "Linz am Rhein"                          | false
        "2645"   | "Vettelschoss"                           | false
        "2646"   | "Königsfeld Eifel"                       | false
        "2647"   | "Kesseling"                              | false
        "2651"   | "Mayen"                                  | false
        "2652"   | "Mendig"                                 | false
        "2653"   | "Kaisersesch"                            | false
        "2654"   | "Polch"                                  | false
        "2655"   | "Weibern"                                | false
        "2656"   | "Virneburg"                              | false
        "2657"   | "Uersfeld"                               | false
        "2661"   | "Bad Marienberg Westerwald"              | false
        "2662"   | "Hachenburg"                             | false
        "2663"   | "Westerburg Westerwald"                  | false
        "2664"   | "Rennerod"                               | false
        "2666"   | "Freilingen Westerwald"                  | false
        "2667"   | "Stein Neukirch"                         | false
        "2671"   | "Cochem"                                 | false
        "2672"   | "Treis Karden"                           | false
        "2673"   | "Ellenz Poltersdorf"                     | false
        "2674"   | "Bad Bertrich"                           | false
        "2675"   | "Ediger Eller"                           | false
        "2676"   | "Ulmen"                                  | false
        "2677"   | "Lutzerath"                              | false
        "2678"   | "Büchel bei Cochem"                      | false
        "2680"   | "Mündersbach"                            | false
        "2681"   | "Altenkirchen Westerwald"                | false
        "2682"   | "Hamm Sieg"                              | false
        "2683"   | "Asbach Westerwald"                      | false
        "2684"   | "Puderbach Westerwald"                   | false
        "2685"   | "Flammersfeld"                           | false
        "2686"   | "Weyerbusch"                             | false
        "2687"   | "Horhausen Westerwald"                   | false
        "2688"   | "Kroppach"                               | false
        "2689"   | "Dierdorf"                               | false
        "2691"   | "Adenau"                                 | false
        "2692"   | "Kelberg"                                | false
        "2693"   | "Antweiler"                              | false
        "2694"   | "Wershofen"                              | false
        "2695"   | "Insul"                                  | false
        "2696"   | "Nohn Eifel"                             | false
        "2697"   | "Blankenheim Ahrhütte"                   | false
        "271"    | "Siegen"                                 | false
        "2721"   | "Lennestadt"                             | false
        "2722"   | "Attendorn"                              | false
        "2723"   | "Kirchhundem"                            | false
        "2724"   | "Finnentrop Serkenrode"                  | false
        "2725"   | "Lennestadt Oedingen"                    | false
        "2732"   | "Kreuztal"                               | false
        "2733"   | "Hilchenbach"                            | false
        "2734"   | "Freudenberg Westfalen"                  | false
        "2735"   | "Neunkirchen Siegerl"                    | false
        "2736"   | "Burbach Siegerl"                        | false
        "2737"   | "Netphen Deuz"                           | false
        "2738"   | "Netphen"                                | false
        "2739"   | "Wilnsdorf"                              | false
        "2741"   | "Betzdorf"                               | false
        "2742"   | "Wissen"                                 | false
        "2743"   | "Daaden"                                 | false
        "2744"   | "Herdorf"                                | false
        "2745"   | "Brachbach Sieg"                         | false
        "2747"   | "Molzhain"                               | false
        "2750"   | "Diedenshausen"                          | false
        "2751"   | "Bad Berleburg"                          | false
        "2752"   | "Bad Laasphe"                            | false
        "2753"   | "Erndtebrück"                            | false
        "2754"   | "Bad Laasphe Feudingen"                  | false
        "2755"   | "Bad Berleburg Schwarzenau"              | false
        "2758"   | "Bad Berleburg Girkhausen"               | false
        "2759"   | "Bad Berleburg Aue"                      | false
        "2761"   | "Olpe Biggesee"                          | false
        "2762"   | "Wenden Südsauerland"                    | false
        "2763"   | "Drolshagen Bleche"                      | false
        "2764"   | "Welschen Ennest"                        | false
        "2770"   | "Eschenburg"                             | false
        "2771"   | "Dillenburg"                             | false
        "2772"   | "Herborn Hessen"                         | false
        "2773"   | "Haiger"                                 | false
        "2774"   | "Dietzhölztal"                           | false
        "2775"   | "Driedorf"                               | false
        "2776"   | "Bad Endbach Hartenrod"                  | false
        "2777"   | "Breitscheid Hessen"                     | false
        "2778"   | "Siegbach"                               | false
        "2779"   | "Greifenstein Beilstein"                 | false
        "2801"   | "Xanten"                                 | false
        "2802"   | "Alpen"                                  | false
        "2803"   | "Wesel Büderich"                         | false
        "2804"   | "Xanten Marienbaum"                      | false
        "281"    | "Wesel"                                  | false
        "2821"   | "Kleve Niederrhein"                      | false
        "2822"   | "Emmerich"                               | false
        "2823"   | "Goch"                                   | false
        "2824"   | "Kalkar"                                 | false
        "2825"   | "Uedem"                                  | false
        "2826"   | "Kranenburg Niederrhein"                 | false
        "2827"   | "Goch Hassum"                            | false
        "2828"   | "Emmerich Elten"                         | false
        "2831"   | "Geldern"                                | false
        "2832"   | "Kevelaer"                               | false
        "2833"   | "Kerken"                                 | false
        "2834"   | "Straelen"                               | false
        "2835"   | "Issum"                                  | false
        "2836"   | "Wachtendonk"                            | false
        "2837"   | "Weeze"                                  | false
        "2838"   | "Sonsbeck"                               | false
        "2839"   | "Straelen Herongen"                      | false
        "2841"   | "Moers"                                  | false
        "2842"   | "Kamp Lintfort"                          | false
        "2843"   | "Rheinberg"                              | false
        "2844"   | "Rheinberg Orsoy"                        | false
        "2845"   | "Neukirchen Vluyn"                       | false
        "2850"   | "Rees Haldern"                           | false
        "2851"   | "Rees"                                   | false
        "2852"   | "Hamminkeln"                             | false
        "2853"   | "Schermbeck"                             | false
        "2855"   | "Voerde Niederrhein"                     | false
        "2856"   | "Hamminkeln Brünen"                      | false
        "2857"   | "Rees Mehr"                              | false
        "2858"   | "Hünxe"                                  | false
        "2859"   | "Wesel Bislich"                          | false
        "2861"   | "Borken Westfalen"                       | false
        "2862"   | "Südlohn"                                | false
        "2863"   | "Velen"                                  | false
        "2864"   | "Reken"                                  | false
        "2865"   | "Raesfeld"                               | false
        "2866"   | "Dorsten Rhade"                          | false
        "2867"   | "Heiden Kreis Borken"                    | false
        "2871"   | "Bocholt"                                | false
        "2872"   | "Rhede Westfalen"                        | false
        "2873"   | "Isselburg Werth"                        | false
        "2874"   | "Isselburg"                              | false
        "2902"   | "Warstein"                               | false
        "2903"   | "Meschede Freienohl"                     | false
        "2904"   | "Bestwig"                                | false
        "2905"   | "Bestwig Ramsbeck"                       | false
        "291"    | "Meschede"                               | false
        "2921"   | "Soest"                                  | false
        "2922"   | "Werl"                                   | false
        "2923"   | "Lippetal Herzfeld"                      | false
        "2924"   | "Möhnesee"                               | false
        "2925"   | "Warstein Allagen"                       | false
        "2927"   | "Neuengeseke"                            | false
        "2928"   | "Soest Ostönnen"                         | false
        "2931"   | "Arnsberg"                               | false
        "2932"   | "Neheim Hüsten"                          | false
        "2933"   | "Sundern Sauerland"                      | false
        "2934"   | "Sundern Altenhellefeld"                 | false
        "2935"   | "Sundern Hachen"                         | false
        "2937"   | "Arnsberg Oeventrop"                     | false
        "2938"   | "Ense"                                   | false
        "2941"   | "Lippstadt"                              | false
        "2942"   | "Geseke"                                 | false
        "2943"   | "Erwitte"                                | false
        "2944"   | "Rietberg Mastholte"                     | false
        "2945"   | "Lippstadt Benninghausen"                | false
        "2947"   | "Anröchte"                               | false
        "2948"   | "Lippstadt Rebbeke"                      | false
        "2951"   | "Büren"                                  | false
        "2952"   | "Rüthen"                                 | false
        "2953"   | "Wünnenberg"                             | false
        "2954"   | "Rüthen Oestereiden"                     | false
        "2955"   | "Büren Wewelsburg"                       | false
        "2957"   | "Wünnenberg Haaren"                      | false
        "2958"   | "Büren Harth"                            | false
        "2961"   | "Brilon"                                 | false
        "2962"   | "Olsberg"                                | false
        "2963"   | "Brilon Messinghausen"                   | false
        "2964"   | "Brilon Alme"                            | false
        "2971"   | "Schmallenberg Dorlar"                   | false
        "2972"   | "Schmallenberg"                          | false
        "2973"   | "Eslohe Sauerland"                       | false
        "2974"   | "Schmallenberg Fredeburg"                | false
        "2975"   | "Schmallenberg Oberkirchen"              | false
        "2977"   | "Schmallenberg Bödefeld"                 | false
        "2981"   | "Winterberg Westfalen"                   | false
        "2982"   | "Medebach"                               | false
        "2983"   | "Winterberg Siedlinghausen"              | false
        "2984"   | "Hallenberg"                             | false
        "2985"   | "Winterberg Niedersfeld"                 | false
        "2991"   | "Marsberg Bredelar"                      | false
        "2992"   | "Marsberg"                               | false
        "2993"   | "Marsberg Canstein"                      | false
        "2994"   | "Marsberg Westheim"                      | false
        "30"     | "Berlin"                                 | false
        "3301"   | "Oranienburg"                            | false
        "3302"   | "Hennigsdorf"                            | false
        "3303"   | "Birkenwerder"                           | false
        "3304"   | "Velten"                                 | false
        "33051"  | "Nassenheide"                            | false
        "33052"  | "Leegebruch"                             | false
        "33053"  | "Zehlendorf Kreis Oberhavel"             | false
        "33054"  | "Liebenwalde"                            | false
        "33055"  | "Kremmen"                                | false
        "33056"  | "Mühlenbeck Kreis Oberhavel"             | false
        "3306"   | "Gransee"                                | false
        "3307"   | "Zehdenick"                              | false
        "33080"  | "Marienthal Kreis Oberhavel"             | false
        "33082"  | "Menz Kreis Oberhavel"                   | false
        "33083"  | "Schulzendorf Kreis Oberhavel"           | false
        "33084"  | "Gutengermendorf"                        | false
        "33085"  | "Seilershof"                             | false
        "33086"  | "Grieben Kreis Oberhavel"                | false
        "33087"  | "Bredereiche"                            | false
        "33088"  | "Falkenthal"                             | false
        "33089"  | "Himmelpfort"                            | false
        "33093"  | "Fürstenberg Havel"                      | false
        "33094"  | "Löwenberg"                              | false
        "331"    | "Potsdam"                                | false
        "33200"  | "Bergholz Rehbrücke"                     | false
        "33201"  | "Gross Glienicke"                        | false
        "33202"  | "Töplitz"                                | false
        "33203"  | "Kleinmachnow"                           | false
        "33204"  | "Beelitz Mark"                           | false
        "33205"  | "Michendorf"                             | false
        "33206"  | "Fichtenwalde"                           | false
        "33207"  | "Gross Kreutz"                           | false
        "33208"  | "Fahrland"                               | false
        "33209"  | "Caputh"                                 | false
        "3321"   | "Nauen Brandenburg"                      | false
        "3322"   | "Falkensee"                              | false
        "33230"  | "Börnicke Kreis Havelland"               | false
        "33231"  | "Pausin"                                 | false
        "33232"  | "Brieselang"                             | false
        "33233"  | "Ketzin"                                 | false
        "33234"  | "Wustermark"                             | false
        "33235"  | "Friesack"                               | false
        "33237"  | "Paulinenaue"                            | false
        "33238"  | "Senzke"                                 | false
        "33239"  | "Gross Behnitz"                          | false
        "3327"   | "Werder Havel"                           | false
        "3328"   | "Teltow"                                 | false
        "3329"   | "Stahnsdorf"                             | false
        "3331"   | "Angermünde"                             | false
        "3332"   | "Schwedt an der Oder"                    | true   // see https://issuetracker.google.com/issues/183383466
        "33331"  | "Casekow"                                | false
        "33332"  | "Gartz Oder"                             | false
        "33333"  | "Tantow"                                 | false
        "33334"  | "Greiffenberg"                           | false
        "33335"  | "Pinnow Kreis Uckermark"                 | false
        "33336"  | "Passow Kreis Uckermark"                 | false
        "33337"  | "Altkünkendorf"                          | false
        "33338"  | "Stolpe an der Oder"                     | true   // see https://issuetracker.google.com/issues/183383466
        "3334"   | "Eberswalde"                             | false
        "3335"   | "Finowfurt"                              | false
        "33361"  | "Joachimsthal"                           | false
        "33362"  | "Liepe Kreis Barnim"                     | false
        "33363"  | "Altenhof Kreis Barnim"                  | false
        "33364"  | "Gross Ziethen Kreis Barnim"             | false
        "33365"  | "Lüdersdorf Kreis Barnim"                | false
        "33366"  | "Chorin"                                 | false
        "33367"  | "Friedrichswalde Brandenburg"            | false
        "33368"  | "Hohensaaten"                            | false
        "33369"  | "Oderberg"                               | false
        "3337"   | "Biesenthal Brandenburg"                 | false
        "3338"   | "Bernau Brandenburg"                     | false
        "33393"  | "Gross Schönebeck Kreis Barnim"          | false
        "33394"  | "Blumberg Kreis Barnim"                  | false
        "33395"  | "Zerpenschleuse"                         | false
        "33396"  | "Klosterfelde"                           | false
        "33397"  | "Wandlitz"                               | false
        "33398"  | "Werneuchen"                             | false
        "3341"   | "Strausberg"                             | false
        "3342"   | "Neuenhagen bei Berlin"                  | false
        "33432"  | "Müncheberg"                             | false
        "33433"  | "Buckow Märkische Schweiz"               | false
        "33434"  | "Herzfelde bei Strausberg"               | false
        "33435"  | "Rehfelde"                               | false
        "33436"  | "Prötzel"                                | false
        "33437"  | "Reichenberg bei Strausberg"             | false
        "33438"  | "Altlandsberg"                           | false
        "33439"  | "Fredersdorf Vogelsdorf"                 | false
        "3344"   | "Bad Freienwalde"                        | false
        "33451"  | "Heckelberg"                             | false
        "33452"  | "Neulewin"                               | false
        "33454"  | "Wölsickendorf bei Wollenberg"           | true   // see https://issuetracker.google.com/issues/183383466
        "33456"  | "Wriezen"                                | false
        "33457"  | "Altreetz"                               | false
        "33458"  | "Falkenberg Mark"                        | false
        "3346"   | "Seelow"                                 | false
        "33470"  | "Lietzen"                                | false
        "33472"  | "Golzow bei Seelow"                      | false
        "33473"  | "Zechin"                                 | false
        "33474"  | "Neutrebbin"                             | false
        "33475"  | "Letschin"                               | false
        "33476"  | "Neuhardenberg"                          | false
        "33477"  | "Trebnitz bei Müncheberg"                | false
        "33478"  | "Gross Neuendorf"                        | false
        "33479"  | "Küstrin Kietz"                          | false
        "335"    | "Frankfurt (Oder)"                       | false
        "33601"  | "Podelzig"                               | false
        "33602"  | "Alt Zeschdorf"                          | false
        "33603"  | "Falkenhagen bei Seelow"                 | false
        "33604"  | "Lebus"                                  | false
        "33605"  | "Boossen"                                | false
        "33606"  | "Müllrose"                               | false
        "33607"  | "Briesen Mark"                           | false
        "33608"  | "Jacobsdorf Mark"                        | false
        "33609"  | "Brieskow Finkenheerd"                   | false
        "3361"   | "Fürstenwalde Spree"                     | false
        "3362"   | "Erkner"                                 | false
        "33631"  | "Bad Saarow Pieskow"                     | false
        "33632"  | "Hangelsberg"                            | false
        "33633"  | "Spreenhagen"                            | false
        "33634"  | "Berkenbrück Kreis Oder Spree"           | false
        "33635"  | "Arensdorf Kreis Oder Spree"             | false
        "33636"  | "Steinhöfel Kreis Oder Spree"            | false
        "33637"  | "Beerfelde"                              | false
        "33638"  | "Rüdersdorf bei Berlin"                  | false
        "3364"   | "Eisenhüttenstadt"                       | false
        "33652"  | "Neuzelle"                               | false
        "33653"  | "Ziltendorf"                             | false
        "33654"  | "Fünfeichen"                             | false
        "33655"  | "Grunow Kreis Oder Spree"                | false
        "33656"  | "Bahro"                                  | false
        "33657"  | "Steinsdorf Brandenburg"                 | false
        "3366"   | "Beeskow"                                | false
        "33671"  | "Lieberose"                              | false
        "33672"  | "Pfaffendorfb Beeskow"                   | false
        "33673"  | "Weichensdorf"                           | false
        "33674"  | "Trebatsch"                              | false
        "33675"  | "Tauche"                                 | false
        "33676"  | "Friedland bei Beeskow"                  | false
        "33677"  | "Glienicke bei Beeskow"                  | false
        "33678"  | "Storkow Mark"                           | false
        "33679"  | "Wendisch Rietz"                         | false
        "33701"  | "Grossbeeren"                            | false
        "33702"  | "Wünsdorf"                               | false
        "33703"  | "Sperenberg"                             | false
        "33704"  | "Baruth Mark"                            | false
        "33708"  | "Rangsdorf"                              | false
        "3371"   | "Luckenwalde"                            | false
        "3372"   | "Jüterbog"                               | false
        "33731"  | "Trebbin"                                | false
        "33732"  | "Hennickendorf bei Luckenwalde"          | false
        "33733"  | "Stülpe"                                 | false
        "33734"  | "Felgentreu"                             | false
        "33741"  | "Niedergörsdorf"                         | false
        "33742"  | "Oehna Brandenburg"                      | false
        "33743"  | "Blönsdorf"                              | false
        "33744"  | "Hohenseefeld"                           | false
        "33745"  | "Petkus"                                 | false
        "33746"  | "Werbig bei Jüterbog"                    | false
        "33747"  | "Marzahna"                               | false
        "33748"  | "Treuenbrietzen"                         | false
        "3375"   | "Königs Wusterhausen"                    | false
        "33760"  | "Münchehofe Kreis Dahme Spreewald"       | false
        "33762"  | "Zeuthen"                                | false
        "33763"  | "Bestensee"                              | false
        "33764"  | "Mittenwalde Mark"                       | false
        "33765"  | "Märkisch Buchholz"                      | false
        "33766"  | "Teupitz"                                | false
        "33767"  | "Friedersdorf bei Berlin"                | false
        "33768"  | "Prieros"                                | false
        "33769"  | "Töpchin"                                | false
        "3377"   | "Zossen Brandenburg"                     | false
        "3378"   | "Ludwigsfelde"                           | false
        "3379"   | "Mahlow"                                 | false
        "3381"   | "Brandenburg an der Havel"               | false
        "3382"   | "Lehnin"                                 | false
        "33830"  | "Ziesar"                                 | false
        "33831"  | "Weseram"                                | false
        "33832"  | "Rogäsen"                                | false
        "33833"  | "Wollin bei Brandenburg"                 | false
        "33834"  | "Pritzerbe"                              | false
        "33835"  | "Golzow bei Brandenburg"                 | false
        "33836"  | "Butzow bei Brandenburg"                 | false
        "33837"  | "Brielow"                                | false
        "33838"  | "Päwesin"                                | false
        "33839"  | "Wusterwitz"                             | false
        "33841"  | "Belzig"                                 | false
        "33843"  | "Niemegk"                                | false
        "33844"  | "Brück Brandenburg"                      | false
        "33845"  | "Borkheide"                              | false
        "33846"  | "Dippmannsdorf"                          | false
        "33847"  | "Görzke"                                 | false
        "33848"  | "Raben"                                  | false
        "33849"  | "Wiesenburg Mark"                        | false
        "3385"   | "Rathenow"                               | false
        "3386"   | "Premnitz"                               | false
        "33870"  | "Zollchow bei Rathenow"                  | false
        "33872"  | "Hohennauen"                             | false
        "33873"  | "Grosswudicke"                           | false
        "33874"  | "Stechow Brandenburg"                    | false
        "33875"  | "Rhinow"                                 | false
        "33876"  | "Buschow"                                | false
        "33877"  | "Nitzahn"                                | false
        "33878"  | "Nennhausen"                             | false
        "3391"   | "Neuruppin"                              | false
        "33920"  | "Walsleben bei Neuruppin"                | false
        "33921"  | "Zechlinerhütte"                         | false
        "33922"  | "Karwesee"                               | false
        "33923"  | "Flecken Zechlin"                        | false
        "33924"  | "Rägelin"                                | false
        "33925"  | "Wustrau Altfriesack"                    | false
        "33926"  | "Herzberg Mark"                          | false
        "33927"  | "Linum"                                  | false
        "33928"  | "Wildberg Brandenburg"                   | false
        "33929"  | "Gühlen Glienicke"                       | false
        "33931"  | "Rheinsberg Mark"                        | false
        "33932"  | "Fehrbellin"                             | false
        "33933"  | "Lindow Mark"                            | false
        "3394"   | "Wittstock Dosse"                        | false
        "3395"   | "Pritzwalk"                              | false
        "33962"  | "Heiligengrabe"                          | false
        "33963"  | "Wulfersdorf bei Wittstock"              | false
        "33964"  | "Fretzdorf"                              | false
        "33965"  | "Herzsprung bei Wittstock"               | false
        "33966"  | "Dranse"                                 | false
        "33967"  | "Freyenstein"                            | false
        "33968"  | "Meyenburg Kreis Prignitz"               | false
        "33969"  | "Stepenitz"                              | false
        "33970"  | "Neustadt Dosse"                         | false
        "33971"  | "Kyritz Brandenburg"                     | false
        "33972"  | "Breddin"                                | false
        "33973"  | "Zernitz bei Neustadt Dosse"             | false
        "33974"  | "Dessow"                                 | false
        "33975"  | "Dannenwalde Kreis Prignitz"             | false
        "33976"  | "Wutike"                                 | false
        "33977"  | "Gumtow"                                 | false
        "33978"  | "Segeletz"                               | false
        "33979"  | "Wusterhausen Dosse"                     | false
        "33981"  | "Putlitz"                                | false
        "33982"  | "Hoppenrade Kreis Prignitz"              | false
        "33983"  | "Gross Pankow Kreis Prignitz"            | false
        "33984"  | "Blumenthal bei Pritzwalk"               | false
        "33986"  | "Falkenhagen Kreis Prignitz"             | false
        "33989"  | "Sadenbeck"                              | false
        "340"    | "Dessau Anhalt"                          | true   // see https://issuetracker.google.com/issues/183383466
        "341"    | "Leipzig"                                | false
        "34202"  | "Delitzsch"                              | false
        "34203"  | "Zwenkau"                                | false
        "34204"  | "Schkeuditz"                             | false
        "34205"  | "Markranstädt"                           | false
        "34206"  | "Rötha"                                  | false
        "34207"  | "Zwochau"                                | false
        "34208"  | "Löbnitz bei Delitzsch"                  | false
        "3421"   | "Torgau"                                 | false
        "34221"  | "Schildau Gneisenaustadt"                | false
        "34222"  | "Arzberg bei Torgau"                     | false
        "34223"  | "Dommitzsch"                             | false
        "34224"  | "Belgern Sachsen"                        | false
        "3423"   | "Eilenburg"                              | false
        "34241"  | "Jesewitz"                               | false
        "34242"  | "Hohenpriessnitz"                        | false
        "34243"  | "Bad Düben"                              | false
        "34244"  | "Mockrehna"                              | false
        "3425"   | "Wurzen"                                 | false
        "34261"  | "Kühren bei Wurzen"                      | false
        "34262"  | "Falkenhain bei Wurzen"                  | false
        "34263"  | "Hohburg"                                | false
        "34291"  | "Borsdorf"                               | false
        "34292"  | "Brandis bei Wurzen"                     | false
        "34293"  | "Naunhof bei Grimma"                     | false
        "34294"  | "Rackwitz"                               | false
        "34295"  | "Krensitz"                               | false
        "34296"  | "Groitzsch bei Pegau"                    | false
        "34297"  | "Liebertwolkwitz"                        | false
        "34298"  | "Taucha bei Leipzig"                     | false
        "34299"  | "Gaschwitz"                              | false
        "3431"   | "Döbeln"                                 | false
        "34321"  | "Leisnig"                                | false
        "34322"  | "Rosswein"                               | false
        "34324"  | "Ostrau Sachsen"                         | false
        "34325"  | "Mochau Lüttewitz"                       | false
        "34327"  | "Waldheim Sachsen"                       | false
        "34328"  | "Hartha bei Döbeln"                      | false
        "3433"   | "Borna Stadt"                            | false
        "34341"  | "Geithain"                               | false
        "34342"  | "Neukieritzsch"                          | false
        "34343"  | "Regis Breitingen"                       | false
        "34344"  | "Kohren Sahlis"                          | false
        "34345"  | "Bad Lausick"                            | false
        "34346"  | "Narsdorf"                               | false
        "34347"  | "Oelzschau bei Borna"                    | false
        "34348"  | "Frohburg"                               | false
        "3435"   | "Oschatz"                                | false
        "34361"  | "Dahlen Sachsen"                         | false
        "34362"  | "Mügeln bei Oschatz"                     | false
        "34363"  | "Cavertitz"                              | false
        "34364"  | "Wermsdorf"                              | false
        "3437"   | "Grimma"                                 | false
        "34381"  | "Colditz"                                | false
        "34382"  | "Nerchau"                                | false
        "34383"  | "Trebsen Mulde"                          | false
        "34384"  | "Grossbothen"                            | false
        "34385"  | "Mutzschen"                              | false
        "34386"  | "Dürrweitzschen bei Grimma"              | false
        "3441"   | "Zeitz"                                  | false
        "34422"  | "Osterfeld"                              | false
        "34423"  | "Heuckewalde"                            | false
        "34424"  | "Reuden bei Zeitz"                       | false
        "34425"  | "Droyssig"                               | false
        "34426"  | "Kayna"                                  | false
        "3443"   | "Weissenfels Sachsen-Anhalt"             | false
        "34441"  | "Hohenmölsen"                            | false
        "34443"  | "Teuchern"                               | false
        "34444"  | "Lützen"                                 | false
        "34445"  | "Stößen"                                 | false
        "34446"  | "Grosskorbetha"                          | false
        "3445"   | "Naumburg Saale"                         | false
        "34461"  | "Nebra Unstrut"                          | false
        "34462"  | "Laucha Unstrut"                         | false
        "34463"  | "Bad Kösen"                              | false
        "34464"  | "Freyburg Unstrut"                       | false
        "34465"  | "Bad Bibra"                              | false
        "34466"  | "Janisroda"                              | false
        "34467"  | "Eckartsberga"                           | false
        "3447"   | "Altenburg Thüringen"                    | false
        "3448"   | "Meuselwitz Thüringen"                   | false
        "34491"  | "Schmölln Thüringen"                     | false
        "34492"  | "Lucka"                                  | false
        "34493"  | "Gößnitz Thüringen"                      | false
        "34494"  | "Ehrenhain"                              | false
        "34495"  | "Dobitschen"                             | false
        "34496"  | "Nöbdenitz"                              | false
        "34497"  | "Langenleuba Niederhain"                 | false
        "34498"  | "Rositz"                                 | false
        "345"    | "Halle Saale"                            | false
        "34600"  | "Ostrau Saalkreis"                       | false
        "34601"  | "Teutschenthal"                          | false
        "34602"  | "Landsberg Sachsen-Anhalt"               | false
        "34603"  | "Nauendorf Sachsen-Anhalt"               | false
        "34604"  | "Niemberg"                               | false
        "34605"  | "Gröbers"                                | false
        "34606"  | "Teicha Sachsen-Anhalt"                  | false
        "34607"  | "Wettin"                                 | false
        "34609"  | "Salzmünde"                              | false
        "3461"   | "Merseburg Saale"                        | false
        "3462"   | "Bad Dürrenberg"                         | false
        "34632"  | "Mücheln Geiseltal"                      | false
        "34633"  | "Braunsbedra"                            | false
        "34635"  | "Bad Lauchstädt"                         | false
        "34636"  | "Schafstädt"                             | false
        "34637"  | "Frankleben"                             | false
        "34638"  | "Zöschen"                                | false
        "34639"  | "Wallendorf Luppe"                       | false
        "3464"   | "Sangerhausen"                           | false
        "34651"  | "Rossla"                                 | false
        "34652"  | "Allstedt"                               | false
        "34653"  | "Rottleberode"                           | false
        "34654"  | "Stolberg Harz"                          | false
        "34656"  | "Wallhausen Sachsen-Anhalt"              | false
        "34658"  | "Hayn Harz"                              | false
        "34659"  | "Blankenheim bei Sangerhausen"           | false
        "3466"   | "Artern Unstrut"                         | false
        "34671"  | "Bad Frankenhausen Kyffhäuser"           | false
        "34672"  | "Rossleben"                              | false
        "34673"  | "Heldrungen"                             | false
        "34691"  | "Könnern"                                | false
        "34692"  | "Alsleben Saale"                         | false
        "3471"   | "Bernburg Saale"                         | false
        "34721"  | "Nienburg Saale"                         | false
        "34722"  | "Preusslitz"                             | false
        "3473"   | "Aschersleben Sachsen-Anhalt"            | false
        "34741"  | "Frose"                                  | false
        "34742"  | "Sylda"                                  | false
        "34743"  | "Ermsleben"                              | false
        "34745"  | "Winningen Sachsen-Anhalt"               | false
        "34746"  | "Giersleben"                             | false
        "3475"   | "Lutherstadt Eisleben"                   | false
        "3476"   | "Hettstedt Sachsen-Anhalt"               | false
        "34771"  | "Querfurt"                               | false
        "34772"  | "Helbra"                                 | false
        "34773"  | "Schwittersdorf"                         | false
        "34774"  | "Röblingen am See"                       | false
        "34775"  | "Wippra"                                 | false
        "34776"  | "Rothenschirmbach"                       | false
        "34779"  | "Abberode"                               | false
        "34781"  | "Greifenhagen"                           | false
        "34782"  | "Mansfeld Südharz"                       | false
        "34783"  | "Gerbstedt"                              | false
        "34785"  | "Sandersleben"                           | false
        "34901"  | "Roßlau Elbe"                            | false
        "34903"  | "Coswig Anhalt"                          | false
        "34904"  | "Oranienbaum"                            | false
        "34905"  | "Wörlitz"                                | false
        "34906"  | "Raguhn"                                 | false
        "34907"  | "Jeber Bergfrieden"                      | false
        "34909"  | "Aken Elbe"                              | false
        "3491"   | "Lutherstadt Wittenberg"                 | false
        "34920"  | "Kropstädt"                              | false
        "34921"  | "Kemberg"                                | false
        "34922"  | "Mühlanger"                              | false
        "34923"  | "Cobbelsdorf"                            | false
        "34924"  | "Zahna"                                  | false
        "34925"  | "Bad Schmiedeberg"                       | false
        "34926"  | "Pretzsch Elbe"                          | false
        "34927"  | "Globig Bleddin"                         | false
        "34928"  | "Seegrehna"                              | false
        "34929"  | "Straach"                                | false
        "3493"   | "Bitterfeld"                             | false
        "3494"   | "Wolfen"                                 | false
        "34953"  | "Gräfenhainichen"                        | false
        "34954"  | "Roitzsch bei Bitterfeld"                | false
        "34955"  | "Gossa"                                  | false
        "34956"  | "Zörbig"                                 | false
        "3496"   | "Köthen Anhalt"                          | false
        "34973"  | "Osternienburg"                          | false
        "34975"  | "Görzig Kreis Köthen"                    | false
        "34976"  | "Gröbzig"                                | false
        "34977"  | "Quellendorf"                            | false
        "34978"  | "Radegast Kreis Köthen"                  | false
        "34979"  | "Wulfen Sachsen-Anhalt"                  | false
        "3501"   | "Pirna"                                  | false
        "35020"  | "Struppen"                               | false
        "35021"  | "Königstein Sächsische Schweiz"          | false
        "35022"  | "Bad Schandau"                           | false
        "35023"  | "Bad Gottleuba"                          | false
        "35024"  | "Stadt Wehlen"                           | false
        "35025"  | "Liebstadt"                              | false
        "35026"  | "Dürrröhrsdorf Dittersbach"              | false
        "35027"  | "Weesenstein"                            | false
        "35028"  | "Krippen"                                | false
        "35032"  | "Langenhennersdorf"                      | false
        "35033"  | "Rosenthal Sächsische Schweiz"           | false
        "3504"   | "Dippoldiswalde"                         | false
        "35052"  | "Kipsdorf Kurort"                        | false
        "35053"  | "Glashütte Sachsen"                      | false
        "35054"  | "Lauenstein Sachsen"                     | false
        "35055"  | "Höckendorf bei Dippoldiswalde"          | false
        "35056"  | "Altenberg Sachsen"                      | false
        "35057"  | "Hermsdorf Erzgebirge"                   | false
        "35058"  | "Pretzschendorf"                         | false
        "351"    | "Dresden"                                | false
        "35200"  | "Arnsdorf bei Dresden"                   | false
        "35201"  | "Langebrück"                             | false
        "35202"  | "Klingenberg Sachsen"                    | false
        "35203"  | "Tharandt"                               | false
        "35204"  | "Wilsdruff"                              | false
        "35205"  | "Ottendorf Okrilla"                      | false
        "35206"  | "Kreischa bei Dresden"                   | false
        "35207"  | "Moritzburg"                             | false
        "35208"  | "Radeburg"                               | false
        "35209"  | "Mohorn"                                 | false
        "3521"   | "Meissen"                                | false
        "3522"   | "Grossenhain Sachsen"                    | false
        "3523"   | "Coswig bei Dresden"                     | false
        "35240"  | "Tauscha bei Großenhain"                 | false
        "35241"  | "Lommatzsch"                             | false
        "35242"  | "Nossen"                                 | false
        "35243"  | "Weinböhla"                              | false
        "35244"  | "Krögis"                                 | false
        "35245"  | "Burkhardswalde Munzig"                  | false
        "35246"  | "Ziegenhain Sachsen"                     | false
        "35247"  | "Zehren Sachsen"                         | false
        "35248"  | "Schönfeld bei Großenhain"               | false
        "35249"  | "Basslitz"                               | false
        "3525"   | "Riesa"                                  | false
        "35263"  | "Gröditz bei Riesa"                      | false
        "35264"  | "Strehla"                                | false
        "35265"  | "Glaubitz"                               | false
        "35266"  | "Heyda bei Riesa"                        | false
        "35267"  | "Diesbar Seusslitz"                      | false
        "35268"  | "Stauchitz"                              | false
        "3528"   | "Radeberg"                               | false
        "3529"   | "Heidenau Sachsen"                       | false
        "3531"   | "Finsterwalde"                           | false
        "35322"  | "Doberlug Kirchhain"                     | false
        "35323"  | "Sonnewalde"                             | false
        "35324"  | "Crinitz"                                | false
        "35325"  | "Rückersdorf bei Finsterwalde"           | false
        "35326"  | "Schönborn Kreis Elbe Elster"            | false
        "35327"  | "Priessen"                               | false
        "35329"  | "Dollenchen"                             | false
        "3533"   | "Elsterwerda"                            | false
        "35341"  | "Bad Liebenwerda"                        | false
        "35342"  | "Mühlberg Elbe"                          | false
        "35343"  | "Hirschfeld bei Elsterwerda"             | false
        "3535"   | "Herzberg Elster"                        | false
        "35361"  | "Schlieben"                              | false
        "35362"  | "Schönewalde bei Herzberg"               | false
        "35363"  | "Fermerswalde"                           | false
        "35364"  | "Lebusa"                                 | false
        "35365"  | "Falkenberg Elster"                      | false
        "3537"   | "Jessen Elster"                          | false
        "35383"  | "Elster Elbe"                            | false
        "35384"  | "Steinsdorf bei Jessen"                  | false
        "35385"  | "Annaburg"                               | false
        "35386"  | "Prettin"                                | false
        "35387"  | "Seyda"                                  | false
        "35388"  | "Klöden"                                 | false
        "35389"  | "Holzdorf Elster"                        | false
        "3541"   | "Calau"                                  | false
        "3542"   | "Lübbenau Spreewald"                     | false
        "35433"  | "Vetschau"                               | false
        "35434"  | "Altdöbern"                              | false
        "35435"  | "Gollmitz bei Calau"                     | false
        "35436"  | "Laasow bei Calau"                       | false
        "35439"  | "Zinnitz"                                | false
        "3544"   | "Luckau Brandenburg"                     | false
        "35451"  | "Dahme Brandenburg"                      | false
        "35452"  | "Golssen"                                | false
        "35453"  | "Drahnsdorf"                             | false
        "35454"  | "Uckro"                                  | false
        "35455"  | "Walddrehna"                             | false
        "35456"  | "Terpt"                                  | false
        "3546"   | "Lübben Spreewald"                       | false
        "35471"  | "Birkenhainchen"                         | false
        "35472"  | "Schlepzig"                              | false
        "35473"  | "Neu Lübbenau"                           | false
        "35474"  | "Schönwalde bei Lübben"                  | false
        "35475"  | "Straupitz"                              | false
        "35476"  | "Wittmannsdorf Bückchen"                 | false
        "35477"  | "Rietzneuendorf Friedrichshof"           | false
        "35478"  | "Goyatz"                                 | false
        "355"    | "Cottbus"                                | false
        "35600"  | "Döbern NL"                              | false
        "35601"  | "Peitz"                                  | false
        "35602"  | "Drebkau"                                | false
        "35603"  | "Burg Spreewald"                         | false
        "35604"  | "Krieschow"                              | false
        "35605"  | "Komptendorf"                            | false
        "35606"  | "Briesen bei Cottbus"                    | false
        "35607"  | "Jänschwalde"                            | false
        "35608"  | "Gross Ossnig"                           | false
        "35609"  | "Drachhausen"                            | false
        "3561"   | "Guben"                                  | false
        "3562"   | "Forst Lausitz"                          | false
        "3563"   | "Spremberg"                              | false
        "3564"   | "Schwarze Pumpe"                         | false
        "35691"  | "Bärenklau NL"                           | false
        "35692"  | "Kerkwitz"                               | false
        "35693"  | "Lauschütz"                              | false
        "35694"  | "Gosda bei Klinge"                       | false
        "35695"  | "Simmersdorf"                            | false
        "35696"  | "Briesnig"                               | false
        "35697"  | "Bagenz"                                 | false
        "35698"  | "Hornow"                                 | false
        "3571"   | "Hoyerswerda"                            | false
        "35722"  | "Lauta bei Hoyerswerda"                  | false
        "35723"  | "Bernsdorf OL"                           | false
        "35724"  | "Lohsa"                                  | false
        "35725"  | "Wittichenau"                            | false
        "35726"  | "Groß Särchen"                           | false
        "35727"  | "Burghammer"                             | false
        "35728"  | "Uhyst Spree"                            | false
        "3573"   | "Senftenberg"                            | false
        "3574"   | "Lauchhammer"                            | false
        "35751"  | "Welzow"                                 | false
        "35752"  | "Ruhland"                                | false
        "35753"  | "Großräschen"                            | false
        "35754"  | "Klettwitz"                              | false
        "35755"  | "Ortrand"                                | false
        "35756"  | "Hosena"                                 | false
        "3576"   | "Weisswasser"                            | false
        "35771"  | "Bad Muskau"                             | false
        "35772"  | "Rietschen"                              | false
        "35773"  | "Schleife"                               | false
        "35774"  | "Boxberg Sachsen"                        | false
        "35775"  | "Pechern"                                | false
        "3578"   | "Kamenz"                                 | false
        "35792"  | "Ossling"                                | false
        "35793"  | "Elstra"                                 | false
        "35795"  | "Königsbrück"                            | false
        "35796"  | "Panschwitz Kuckau"                      | false
        "35797"  | "Schwepnitz"                             | false
        "3581"   | "Görlitz"                                | false
        "35820"  | "Zodel"                                  | false
        "35822"  | "Hagenwerder"                            | false
        "35823"  | "Ostritz"                                | false
        "35825"  | "Kodersdorf"                             | false
        "35826"  | "Königshain bei Görlitz"                 | false
        "35827"  | "Nieder Seifersdorf"                     | false
        "35828"  | "Reichenbach OL"                         | false
        "35829"  | "Gersdorf bei Görlitz"                   | false
        "3583"   | "Zittau"                                 | false
        "35841"  | "Großschönau Sachsen"                    | false
        "35842"  | "Oderwitz"                               | false
        "35843"  | "Hirschfelde bei Zittau"                 | false
        "35844"  | "Oybin Kurort"                           | false
        "3585"   | "Löbau"                                  | false
        "3586"   | "Neugersdorf Sachsen"                    | false
        "35872"  | "Neusalza Spremberg"                     | false
        "35873"  | "Herrnhut"                               | false
        "35874"  | "Bernstadt an der Eigen"                 | false
        "35875"  | "Obercunnersdorf bei Löbau"              | false
        "35876"  | "Weissenberg Sachsen"                    | false
        "35877"  | "Cunewalde"                              | false
        "3588"   | "Niesky"                                 | false
        "35891"  | "Rothenburg OL"                          | false
        "35892"  | "Horka OL"                               | false
        "35893"  | "Mücka"                                  | false
        "35894"  | "Hähnichen"                              | false
        "35895"  | "Klitten"                                | false
        "3591"   | "Bautzen"                                | false
        "3592"   | "Kirschau"                               | false
        "35930"  | "Seitschen"                              | false
        "35931"  | "Königswartha"                           | false
        "35932"  | "Guttau"                                 | false
        "35933"  | "Neschwitz"                              | false
        "35934"  | "Grossdubrau"                            | false
        "35935"  | "Kleinwelka"                             | false
        "35936"  | "Sohland Spree"                          | false
        "35937"  | "Prischwitz"                             | false
        "35938"  | "Großpostwitz OL"                        | false
        "35939"  | "Hochkirch"                              | false
        "3594"   | "Bischofswerda"                          | false
        "35951"  | "Neukirch Lausitz"                       | false
        "35952"  | "Großröhrsdorf OL"                       | false
        "35953"  | "Burkau"                                 | false
        "35954"  | "Grossharthau"                           | false
        "35955"  | "Pulsnitz"                               | false
        "3596"   | "Neustadt in Sachsen"                    | false
        "35971"  | "Sebnitz"                                | false
        "35973"  | "Stolpen"                                | false
        "35974"  | "Hinterhermsdorf"                        | false
        "35975"  | "Hohnstein"                              | false
        "3601"   | "Mühlhausen Thüringen"                   | false
        "36020"  | "Ebeleben"                               | false
        "36021"  | "Schlotheim"                             | false
        "36022"  | "Grossengottern"                         | false
        "36023"  | "Horsmar"                                | false
        "36024"  | "Diedorf bei Mühlhausen Thüringen"       | true   // see https://issuetracker.google.com/issues/183383466
        "36025"  | "Körner"                                 | false
        "36026"  | "Struth bei Mühlhausen Thüringen"        | true   // see https://issuetracker.google.com/issues/183383466
        "36027"  | "Lengenfeld Unterm Stein"                | false
        "36028"  | "Kammerforst Thüringen"                  | false
        "36029"  | "Menteroda"                              | false
        "3603"   | "Bad Langensalza"                        | false
        "36041"  | "Bad Tennstedt"                          | false
        "36042"  | "Tonna"                                  | false
        "36043"  | "Kirchheilingen"                         | false
        "3605"   | "Leinefelde"                             | false
        "3606"   | "Heiligenstadt Heilbad"                  | false
        "36071"  | "Teistungen"                             | false
        "36072"  | "Weißenborn Lüderode"                    | false
        "36074"  | "Worbis"                                 | false
        "36075"  | "Dingelstädt Eichsfeld"                  | false
        "36076"  | "Niederorschel"                          | false
        "36077"  | "Grossbodungen"                          | false
        "36081"  | "Arenshausen"                            | false
        "36082"  | "Ershausen"                              | false
        "36083"  | "Uder"                                   | false
        "36084"  | "Heuthen"                                | false
        "36085"  | "Reinholterode"                          | false
        "36087"  | "Wüstheuterode"                          | false
        "361"    | "Erfurt"                                 | false
        "36200"  | "Elxleben bei Arnstadt"                  | false
        "36201"  | "Walschleben"                            | false
        "36202"  | "Neudietendorf"                          | false
        "36203"  | "Vieselbach"                             | false
        "36204"  | "Stotternheim"                           | false
        "36205"  | "Gräfenroda"                             | false
        "36206"  | "Grossfahner"                            | false
        "36207"  | "Plaue Thüringen"                        | false
        "36208"  | "Ermstedt"                               | false
        "36209"  | "Klettbach"                              | false
        "3621"   | "Gotha Thüringen"                        | false
        "3622"   | "Waltershausen Thüringen"                | false
        "3623"   | "Friedrichroda"                          | false
        "3624"   | "Ohrdruf"                                | false
        "36252"  | "Tambach Dietharz Thüringer Wald"        | true   // see https://issuetracker.google.com/issues/183383466
        "36253"  | "Georgenthal Thüringer Wald"             | false
        "36254"  | "Friedrichswerth"                        | false
        "36255"  | "Goldbach bei Gotha"                     | false
        "36256"  | "Wechmar"                                | false
        "36257"  | "Luisenthal Thüringen"                   | false
        "36258"  | "Friemar"                                | false
        "36259"  | "Tabarz Thüringer Wald"                  | false
        "3628"   | "Arnstadt"                               | false
        "3629"   | "Stadtilm"                               | false
        "3631"   | "Nordhausen Thüringen"                   | false
        "3632"   | "Sondershausen"                          | false
        "36330"  | "Grossberndten"                          | false
        "36331"  | "Ilfeld"                                 | false
        "36332"  | "Ellrich"                                | false
        "36333"  | "Heringen Helme"                         | false
        "36334"  | "Wolkramshausen"                         | false
        "36335"  | "Grosswechsungen"                        | false
        "36336"  | "Klettenberg"                            | false
        "36337"  | "Schiedungen"                            | false
        "36338"  | "Bleicherode"                            | false
        "3634"   | "Sömmerda"                               | false
        "3635"   | "Kölleda"                                | false
        "3636"   | "Greussen"                               | false
        "36370"  | "Grossenehrich"                          | false
        "36371"  | "Schlossvippach"                         | false
        "36372"  | "Kleinneuhausen"                         | false
        "36373"  | "Buttstädt"                              | false
        "36374"  | "Weissensee"                             | false
        "36375"  | "Kindelbrück"                            | false
        "36376"  | "Straussfurt"                            | false
        "36377"  | "Rastenberg"                             | false
        "36378"  | "Ostramondra"                            | false
        "36379"  | "Holzengel"                              | false
        "3641"   | "Jena"                                   | false
        "36421"  | "Camburg"                                | false
        "36422"  | "Reinstädt Thüringen"                    | false
        "36423"  | "Orlamünde"                              | false
        "36424"  | "Kahla Thüringen"                        | false
        "36425"  | "Isserstedt"                             | false
        "36426"  | "Ottendorf bei Stadtroda"                | false
        "36427"  | "Dornburg Saale"                         | false
        "36428"  | "Stadtroda"                              | false
        "3643"   | "Weimar Thüringen"                       | false
        "3644"   | "Apolda"                                 | false
        "36450"  | "Kranichfeld"                            | false
        "36451"  | "Buttelstedt"                            | false
        "36452"  | "Berlstedt"                              | false
        "36453"  | "Mellingen"                              | false
        "36454"  | "Magdala"                                | false
        "36458"  | "Bad Berka"                              | false
        "36459"  | "Blankenhain Thüringen"                  | false
        "36461"  | "Bad Sulza"                              | false
        "36462"  | "Ossmannstedt"                           | false
        "36463"  | "Gebstedt"                               | false
        "36464"  | "Wormstedt"                              | false
        "36465"  | "Oberndorf bei Apolda"                   | false
        "3647"   | "Pößneck"                                | false
        "36481"  | "Neustadt an der Orla"                   | false
        "36482"  | "Triptis"                                | false
        "36483"  | "Ziegenrück"                             | false
        "36484"  | "Knau bei Pößneck"                       | false
        "365"    | "Gera"                                   | false
        "36601"  | "Hermsdorf Thüringen"                    | false
        "36602"  | "Ronneburg Thüringen"                    | false
        "36603"  | "Weida"                                  | false
        "36604"  | "Münchenbernsdorf"                       | false
        "36605"  | "Bad Köstritz"                           | false
        "36606"  | "Kraftsdorf"                             | false
        "36607"  | "Niederpöllnitz"                         | false
        "36608"  | "Seelingstädt bei Gera"                  | false
        "3661"   | "Greiz"                                  | false
        "36621"  | "Elsterberg bei Plauen"                  | false
        "36622"  | "Triebes"                                | false
        "36623"  | "Berga Elster"                           | false
        "36624"  | "Teichwolframsdorf"                      | false
        "36625"  | "Langenwetzendorf"                       | false
        "36626"  | "Auma"                                   | false
        "36628"  | "Zeulenroda"                             | false
        "3663"   | "Schleiz"                                | false
        "36640"  | "Remptendorf"                            | false
        "36642"  | "Harra"                                  | false
        "36643"  | "Thimmendorf"                            | false
        "36644"  | "Hirschberg Saale"                       | false
        "36645"  | "Mühltroff"                              | false
        "36646"  | "Tanna bei Schleiz"                      | false
        "36647"  | "Saalburg Thüringen"                     | false
        "36648"  | "Dittersdorf bei Schleiz"                | false
        "36649"  | "Gefell bei Schleiz"                     | false
        "36651"  | "Lobenstein"                             | false
        "36652"  | "Wurzbach"                               | false
        "36653"  | "Lehesten Thüringer Wald"                | false
        "36691"  | "Eisenberg Thüringen"                    | false
        "36692"  | "Bürgel"                                 | false
        "36693"  | "Crossen an der Elster"                  | false
        "36694"  | "Schkölen Thüringen"                     | false
        "36695"  | "Söllmnitz"                              | false
        "36701"  | "Lichte"                                 | false
        "36702"  | "Lauscha"                                | false
        "36703"  | "Gräfenthal"                             | false
        "36704"  | "Steinheid"                              | false
        "36705"  | "Oberweißbach Thüringer Wald"            | false
        "3671"   | "Saalfeld Saale"                         | false
        "3672"   | "Rudolstadt"                             | false
        "36730"  | "Sitzendorf"                             | false
        "36731"  | "Unterloquitz"                           | false
        "36732"  | "Könitz"                                 | false
        "36733"  | "Kaulsdorf"                              | false
        "36734"  | "Leutenberg"                             | false
        "36735"  | "Probstzella"                            | false
        "36736"  | "Arnsgereuth"                            | false
        "36737"  | "Drognitz"                               | false
        "36738"  | "Königsee"                               | false
        "36739"  | "Rottenbach"                             | false
        "36741"  | "Bad Blankenburg"                        | false
        "36742"  | "Uhlstädt"                               | false
        "36743"  | "Teichel"                                | false
        "36744"  | "Remda"                                  | false
        "3675"   | "Sonneberg Thüringen"                    | false
        "36761"  | "Heubisch"                               | false
        "36762"  | "Steinach Thüringen"                     | false
        "36764"  | "Neuhaus Schierschnitz"                  | false
        "36766"  | "Schalkau"                               | false
        "3677"   | "Ilmenau Thüringen"                      | false
        "36781"  | "Grossbreitenbach"                       | false
        "36782"  | "Schmiedefeld am Rennsteig"              | false
        "36783"  | "Gehren Thüringen"                       | false
        "36784"  | "Stützerbach"                            | false
        "36785"  | "Gräfinau Angstedt"                      | false
        "3679"   | "Neuhaus am Rennweg"                     | false
        "3681"   | "Suhl"                                   | false
        "3682"   | "Zella Mehlis"                           | false
        "3683"   | "Schmalkalden"                           | false
        "36840"  | "Trusetal"                               | false
        "36841"  | "Schleusingen"                           | false
        "36842"  | "Oberhof Thüringen"                      | false
        "36843"  | "Benshausen"                             | false
        "36844"  | "Rohr Thüringen"                         | false
        "36845"  | "Gehlberg"                               | false
        "36846"  | "Suhl Dietzhausen"                       | false
        "36847"  | "Steinbach Hallenberg"                   | false
        "36848"  | "Wernshausen"                            | false
        "36849"  | "Kleinschmalkalden"                      | false
        "3685"   | "Hildburghausen"                         | false
        "3686"   | "Eisfeld"                                | false
        "36870"  | "Masserberg"                             | false
        "36871"  | "Bad Colberg Heldburg"                   | false
        "36873"  | "Themar"                                 | false
        "36874"  | "Schönbrunn bei Hildburghaus"            | false
        "36875"  | "Straufhain Streufdorf"                  | false
        "36878"  | "Oberland"                               | false
        "3691"   | "Eisenach Thüringen"                     | false
        "36920"  | "Grossenlupnitz"                         | false
        "36921"  | "Wutha Farnroda"                         | false
        "36922"  | "Gerstungen"                             | false
        "36923"  | "Treffurt"                               | false
        "36924"  | "Mihla"                                  | false
        "36925"  | "Marksuhl"                               | false
        "36926"  | "Creuzburg"                              | false
        "36927"  | "Unterellen"                             | false
        "36928"  | "Neuenhof Thüringen"                     | false
        "36929"  | "Ruhla"                                  | false
        "3693"   | "Meiningen"                              | false
        "36940"  | "Oepfershausen"                          | false
        "36941"  | "Wasungen"                               | false
        "36943"  | "Bettenhausen Thüringen"                 | false
        "36944"  | "Rentwertshausen"                        | false
        "36945"  | "Henneberg"                              | false
        "36946"  | "Erbenhausen Thüringen"                  | false
        "36947"  | "Jüchsen"                                | false
        "36948"  | "Römhild"                                | false
        "36949"  | "Obermaßfeld Grimmenthal"                | false
        "3695"   | "Bad Salzungen"                          | false
        "36961"  | "Bad Liebenstein"                        | false
        "36962"  | "Vacha"                                  | false
        "36963"  | "Dorndorf Rhön"                          | false
        "36964"  | "Dermbach Rhön"                          | false
        "36965"  | "Stadtlengsfeld"                         | false
        "36966"  | "Kaltennordheim"                         | false
        "36967"  | "Geisa"                                  | false
        "36968"  | "Rossdorf Rhön"                          | false
        "36969"  | "Merkers"                                | false
        "371"    | "Chemnitz Sachsen"                       | false
        "37200"  | "Wittgensdorf bei Chemnitz"              | false
        "37202"  | "Claussnitz bei Chemnitz"                | false
        "37203"  | "Gersdorf bei Chemnitz"                  | false
        "37204"  | "Lichtenstein Sachsen"                   | false
        "37206"  | "Frankenberg Sachsen"                    | false
        "37207"  | "Hainichen Sachsen"                      | false
        "37208"  | "Auerswalde"                             | false
        "37209"  | "Einsiedel bei Chemnitz"                 | false
        "3721"   | "Meinersdorf"                            | false
        "3722"   | "Limbach Oberfrohna"                     | false
        "3723"   | "Hohenstein Ernstthal"                   | false
        "3724"   | "Burgstädt"                              | false
        "3725"   | "Zschopau"                               | false
        "3726"   | "Flöha"                                  | false
        "3727"   | "Mittweida"                              | false
        "37291"  | "Augustusburg"                           | false
        "37292"  | "Oederan"                                | false
        "37293"  | "Eppendorf Sachsen"                      | false
        "37294"  | "Grünhainichen"                          | false
        "37295"  | "Lugau Erzgebirge"                       | false
        "37296"  | "Stollberg Erzgebirge"                   | false
        "37297"  | "Thum Sachsen"                           | false
        "37298"  | "Oelsnitz Erzgebirge"                    | false
        "3731"   | "Freiberg Sachsen"                       | false
        "37320"  | "Mulda Sachsen"                          | false
        "37321"  | "Frankenstein Sachsen"                   | false
        "37322"  | "Brand Erbisdorf"                        | false
        "37323"  | "Lichtenberg Erzgebirge"                 | false
        "37324"  | "Reinsberg Sachsen"                      | false
        "37325"  | "Niederbobritzsch"                       | false
        "37326"  | "Frauenstein Sachsen"                    | false
        "37327"  | "Rechenberg Bienenmühle"                 | false
        "37328"  | "Grossschirma"                           | false
        "37329"  | "Grosshartmannsdorf"                     | false
        "3733"   | "Annaberg Buchholz"                      | false
        "37341"  | "Ehrenfriedersdorf"                      | false
        "37342"  | "Cranzahl"                               | false
        "37343"  | "Jöhstadt"                               | false
        "37344"  | "Crottendorf Sachsen"                    | false
        "37346"  | "Geyer"                                  | false
        "37347"  | "Bärenstein Kreis Annaberg"              | false
        "37348"  | "Oberwiesenthal Kurort"                  | false
        "37349"  | "Scheibenberg"                           | false
        "3735"   | "Marienberg Sachsen"                     | false
        "37360"  | "Olbernhau"                              | false
        "37361"  | "Neuhausen Erzgebirge"                   | false
        "37362"  | "Seiffen Erzgebirge"                     | false
        "37363"  | "Zöblitz"                                | false
        "37364"  | "Reitzenhain Erzgebirge"                 | false
        "37365"  | "Sayda"                                  | false
        "37366"  | "Rübenau"                                | false
        "37367"  | "Lengefeld Erzgebirge"                   | false
        "37368"  | "Deutschneudorf"                         | false
        "37369"  | "Wolkenstein"                            | false
        "3737"   | "Rochlitz"                               | false
        "37381"  | "Penig"                                  | false
        "37382"  | "Geringswalde"                           | false
        "37383"  | "Lunzenau"                               | false
        "37384"  | "Wechselburg"                            | false
        "3741"   | "Plauen"                                 | false
        "37421"  | "Oelsnitz Vogtland"                      | false
        "37422"  | "Markneukirchen"                         | false
        "37423"  | "Adorf Vogtland"                         | false
        "37430"  | "Eichigt"                                | false
        "37431"  | "Mehltheuer Vogtland"                    | false
        "37432"  | "Pausa Vogtland"                         | false
        "37433"  | "Gutenfürst"                             | false
        "37434"  | "Bobenneukirchen"                        | false
        "37435"  | "Reuth bei Plauen"                       | false
        "37436"  | "Weischlitz"                             | false
        "37437"  | "Bad Elster"                             | false
        "37438"  | "Bad Brambach"                           | false
        "37439"  | "Jocketa"                                | false
        "3744"   | "Auerbach Vogtland"                      | false
        "3745"   | "Falkenstein Vogtland"                   | false
        "37462"  | "Rothenkirchen Vogtland"                 | false
        "37463"  | "Bergen Vogtland"                        | false
        "37464"  | "Schöneck Vogtland"                      | false
        "37465"  | "Tannenbergsthal Vogtland"               | false
        "37467"  | "Klingenthal Sachsen"                    | false
        "37468"  | "Treuen Vogtland"                        | false
        "375"    | "Zwickau"                                | false
        "37600"  | "Neumark Sachsen"                        | false
        "37601"  | "Mülsen Sankt Jacob"                     | true   // see https://issuetracker.google.com/issues/183383466
        "37602"  | "Kirchberg Sachsen"                      | false
        "37603"  | "Wildenfels"                             | false
        "37604"  | "Mosel"                                  | false
        "37605"  | "Hartenstein Sachsen"                    | false
        "37606"  | "Lengenfeld Vogtland"                    | false
        "37607"  | "Ebersbrunn Sachsen"                     | false
        "37608"  | "Waldenburg Sachsen"                     | false
        "37609"  | "Wolkenburg Mulde"                       | false
        "3761"   | "Werdau Sachsen"                         | false
        "3762"   | "Crimmitschau"                           | false
        "3763"   | "Glauchau"                               | false
        "3764"   | "Meerane"                                | false
        "3765"   | "Reichenbach Vogtland"                   | false
        "3771"   | "Aue Sachsen"                            | false
        "3772"   | "Schneeberg Erzgebirge"                  | false
        "3773"   | "Johanngeorgenstadt"                     | false
        "3774"   | "Schwarzenberg"                          | false
        "37752"  | "Eibenstock"                             | false
        "37754"  | "Zwönitz"                                | false
        "37755"  | "Schönheide Erzgebirge"                  | false
        "37756"  | "Breitenbrunn Erzgebirge"                | false
        "37757"  | "Rittersgrün"                            | false
        "381"    | "Rostock"                                | false
        "38201"  | "Gelbensande"                            | false
        "38202"  | "Volkenshagen"                           | false
        "38203"  | "Bad Doberan"                            | false
        "38204"  | "Broderstorf"                            | false
        "38205"  | "Tessin bei Rostock"                     | false
        "38206"  | "Graal Müritz Seeheilbad"                | false
        "38207"  | "Stäbelow"                               | false
        "38208"  | "Kavelstorf"                             | false
        "38209"  | "Sanitz bei Rostock"                     | false
        "3821"   | "Ribnitz Damgarten"                      | false
        "38220"  | "Wustrow Ostseebad"                      | false
        "38221"  | "Marlow"                                 | false
        "38222"  | "Semlow"                                 | false
        "38223"  | "Saal Vorpommern"                        | true   // see https://issuetracker.google.com/issues/183383466
        "38224"  | "Gresenhorst"                            | false
        "38225"  | "Trinwillershagen"                       | false
        "38226"  | "Dierhagen Ostseebad"                    | false
        "38227"  | "Lüdershagen bei Barth"                  | false
        "38228"  | "Dettmannsdorf Kölzow"                   | false
        "38229"  | "Bad Sülze"                              | false
        "38231"  | "Barth"                                  | false
        "38232"  | "Zingst Ostseebad"                       | false
        "38233"  | "Prerow Ostseebad"                       | false
        "38234"  | "Born am Darß"                           | true   // see https://issuetracker.google.com/issues/183383466
        "38292"  | "Kröpelin"                               | false
        "38293"  | "Kühlungsborn Ostseebad"                 | false
        "38294"  | "Neubukow"                               | false
        "38295"  | "Satow bei Bad Doberan"                  | false
        "38296"  | "Rerik Ostseebad"                        | false
        "38297"  | "Moitin"                                 | false
        "38300"  | "Insel Hiddensee"                        | false
        "38301"  | "Putbus"                                 | false
        "38302"  | "Sagard"                                 | false
        "38303"  | "Sellin Ostseebad"                       | false
        "38304"  | "Garz Rügen"                             | false
        "38305"  | "Gingst"                                 | false
        "38306"  | "Samtens"                                | false
        "38307"  | "Poseritz"                               | false
        "38308"  | "Göhren Rügen"                           | false
        "38309"  | "Trent"                                  | false
        "3831"   | "Stralsund"                              | false
        "38320"  | "Tribsees"                               | false
        "38321"  | "Martensdorf bei Stralsund"              | false
        "38322"  | "Richtenberg"                            | false
        "38323"  | "Prohn"                                  | false
        "38324"  | "Velgast"                                | false
        "38325"  | "Rolofshagen"                            | false
        "38326"  | "Grimmen"                                | false
        "38327"  | "Elmenhorst Vorpommern"                  | true   // see https://issuetracker.google.com/issues/183383466
        "38328"  | "Miltzow"                                | false
        "38331"  | "Rakow Vorpommern"                       | true   // Both ITU and BNetzA "Rakow Vorpom", which is short form of "Vorpommern", it is included in PhoneLibe data, but Geocoder does not delivers it.
        "38332"  | "Gross Bisdorf"                          | false
        "38333"  | "Horst bei Grimmen"                      | false
        "38334"  | "Grammendorf"                            | false
        "3834"   | "Greifswald"                             | false
        "38351"  | "Mesekenhagen"                           | false
        "38352"  | "Kemnitz bei Greifswald"                 | false
        "38353"  | "Gützkow bei Greifswald"                 | false
        "38354"  | "Wusterhusen"                            | false
        "38355"  | "Züssow"                                 | false
        "38356"  | "Behrenhoff"                             | false
        "3836"   | "Wolgast"                                | false
        "38370"  | "Kröslin"                                | false
        "38371"  | "Karlshagen"                             | false
        "38372"  | "Usedom"                                 | false
        "38373"  | "Katzow"                                 | false
        "38374"  | "Lassan bei Wolgast"                     | false
        "38375"  | "Koserow"                                | false
        "38376"  | "Zirchow"                                | false
        "38377"  | "Zinnowitz"                              | false
        "38378"  | "Heringsdorf Seebad"                     | false
        "38379"  | "Benz Usedom"                            | false
        "3838"   | "Bergen auf Rügen"                       | false
        "38391"  | "Altenkirchen Rügen"                     | false
        "38392"  | "Sassnitz"                               | false
        "38393"  | "Binz Ostseebad"                         | false
        "3841"   | "Wismar Mecklenburg"                     | true   // TODO: ITU "Wismar" only, but BNetzA "Wismar Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38422"  | "Neukloster"                             | false
        "38423"  | "Bad Kleinen"                            | false
        "38424"  | "Bobitz"                                 | false
        "38425"  | "Kirchdorf Poel"                         | false
        "38426"  | "Neuburg Steinhausen"                    | false
        "38427"  | "Blowatz"                                | false
        "38428"  | "Hohenkirchen bei Wismar"                | false
        "38429"  | "Glasin"                                 | false
        "3843"   | "Güstrow"                                | false
        "3844"   | "Schwaan"                                | false
        "38450"  | "Tarnow bei Bützow"                      | false
        "38451"  | "Hoppenrade bei Güstrow"                 | false
        "38452"  | "Lalendorf"                              | false
        "38453"  | "Mistorf"                                | false
        "38454"  | "Kritzkow"                               | false
        "38455"  | "Plaaz"                                  | false
        "38456"  | "Langhagen bei Güstrow"                  | false
        "38457"  | "Krakow am See"                          | false
        "38458"  | "Zehna"                                  | false
        "38459"  | "Laage"                                  | false
        "38461"  | "Bützow"                                 | false
        "38462"  | "Baumgarten Mecklenburg"                 | true   // TODO: ITU "Baumgarten" only, but BNetzA "Baumgarten Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38464"  | "Bernitt"                                | false
        "38466"  | "Jürgenshagen"                           | false
        "3847"   | "Sternberg"                              | false
        "38481"  | "Witzin"                                 | false
        "38482"  | "Warin"                                  | false
        "38483"  | "Brüel"                                  | false
        "38484"  | "Ventschow"                              | false
        "38485"  | "Dabel"                                  | false
        "38486"  | "Gustävel"                               | false
        "38488"  | "Demen"                                  | false
        "385"    | "Schwerin Mecklenburg"                   | true   // TODO: ITU "Schwerin" only, but BNetzA "Schwerin Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "3860"   | "Raben Steinfeld"                        | false
        "3861"   | "Plate"                                  | false
        "3863"   | "Crivitz"                                | false
        "3865"   | "Holthusen"                              | false
        "3866"   | "Cambs"                                  | false
        "3867"   | "Lübstorf"                               | false
        "3868"   | "Rastow"                                 | false
        "3869"   | "Dümmer"                                 | false
        "3871"   | "Parchim"                                | false
        "38720"  | "Grebbin"                                | false
        "38721"  | "Ziegendorf"                             | false
        "38722"  | "Raduhn"                                 | false
        "38723"  | "Kladrum"                                | false
        "38724"  | "Siggelkow"                              | false
        "38725"  | "Gross Godems"                           | false
        "38726"  | "Spornitz"                               | false
        "38727"  | "Mestlin"                                | false
        "38728"  | "Domsühl"                                | false
        "38729"  | "Marnitz"                                | false
        "38731"  | "Lübz"                                   | false
        "38732"  | "Gallin bei Lübz"                        | false
        "38733"  | "Karbow Vietlübbe"                       | false
        "38735"  | "Plau am See"                            | false
        "38736"  | "Goldberg Mecklenburg"                   | true   // TODO: ITU "Goldberg" only, but BNetzA "Goldberg Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38737"  | "Ganzlin"                                | false
        "38738"  | "Karow bei Lübz"                         | false
        "3874"   | "Ludwigslust Mecklenburg"                | true   // TODO: ITU "Ludwigslust" only, but BNetzA "Ludwigslust Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38750"  | "Malliss"                                | false
        "38751"  | "Picher"                                 | false
        "38752"  | "Zierzow bei Ludwigslust"                | false
        "38753"  | "Wöbbelin"                               | false
        "38754"  | "Leussow bei Ludwigslust"                | false
        "38755"  | "Eldena"                                 | false
        "38756"  | "Grabow Mecklenburg"                     | true   // TODO: ITU "Grabow" only, but BNetzA "Grabow Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38757"  | "Neustadt Glewe"                         | false
        "38758"  | "Dömitz"                                 | false
        "38759"  | "Tewswoos"                               | false
        "3876"   | "Perleberg"                              | false
        "3877"   | "Wittenberge"                            | false
        "38780"  | "Lanz Brandenburg"                       | false
        "38781"  | "Mellen"                                 | false
        "38782"  | "Reetz bei Perleberg"                    | false
        "38783"  | "Dallmin"                                | false
        "38784"  | "Kleinow Kreis Prignitz"                 | false
        "38785"  | "Berge bei Perleberg"                    | false
        "38787"  | "Glöwen"                                 | false
        "38788"  | "Gross Warnow"                           | false
        "38789"  | "Wolfshagen bei Perleberg"               | false
        "38791"  | "Bad Wilsnack"                           | false
        "38792"  | "Lenzen Elbe"                            | true   // see https://issuetracker.google.com/issues/183383466
        "38793"  | "Dergenthin"                             | false
        "38794"  | "Cumlosen"                               | false
        "38796"  | "Viesecke"                               | false
        "38797"  | "Karstädt Kreis Prignitz"                | false
        "3881"   | "Grevesmühlen"                           | false
        "38821"  | "Lüdersdorf Mecklenburg"                 | true   // TODO: ITU "Lüdersdorf" only, but BNetzA "Lüdersdorf Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38822"  | "Diedrichshagen bei Grevesmühlen"        | false
        "38823"  | "Selmsdorf"                              | false
        "38824"  | "Mallentin"                              | false
        "38825"  | "Klütz"                                  | false
        "38826"  | "Dassow"                                 | false
        "38827"  | "Kalkhorst"                              | false
        "38828"  | "Schönberg Mecklenburg"                  | true   // TODO: ITU "Schönberg" only, but BNetzA "Schönberg Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "3883"   | "Hagenow"                                | false
        "38841"  | "Neuhaus Elbe"                           | false
        "38842"  | "Lüttenmark"                             | false
        "38843"  | "Bennin"                                 | false
        "38844"  | "Gülze"                                  | false
        "38845"  | "Kaarssen"                               | false
        "38847"  | "Boizenburg Elbe"                        | false
        "38848"  | "Vellahn"                                | false
        "38850"  | "Gammelin"                               | false
        "38851"  | "Zarrentin Mecklenburg"                  | true   // TODO: ITU "Zarrentin" only, but BNetzA "Zarrentin Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "38852"  | "Wittenburg"                             | false
        "38853"  | "Drönnewitz bei Hagenow"                 | false
        "38854"  | "Redefin"                                | false
        "38855"  | "Lübtheen"                               | false
        "38856"  | "Pritzier bei Hagenow"                   | false
        "38858"  | "Lassahn"                                | false
        "38859"  | "Alt Zachun"                             | false
        "3886"   | "Gadebusch"                              | false
        "38871"  | "Mühlen Eichsen"                         | false
        "38872"  | "Rehna"                                  | false
        "38873"  | "Carlow"                                 | false
        "38874"  | "Lützow"                                 | false
        "38875"  | "Schlagsdorf bei Gadebusch"              | false
        "38876"  | "Roggendorf"                             | false
        "39000"  | "Beetzendorf"                            | false
        "39001"  | "Apenburg"                               | false
        "39002"  | "Oebisfelde"                             | false
        "39003"  | "Jübar"                                  | false
        "39004"  | "Köckte bei Gardelegen"                  | false
        "39005"  | "Kusey"                                  | false
        "39006"  | "Miesterhorst"                           | false
        "39007"  | "Tangeln"                                | false
        "39008"  | "Kunrau"                                 | false
        "39009"  | "Badel"                                  | false
        "3901"   | "Salzwedel"                              | false
        "3902"   | "Diesdorf Altmark"                       | true   // see https://issuetracker.google.com/issues/183383466
        "39030"  | "Brunau"                                 | false
        "39031"  | "Dähre"                                  | false
        "39032"  | "Mahlsdorf bei Salzwedel"                | false
        "39033"  | "Wallstawe"                              | false
        "39034"  | "Fleetmark"                              | false
        "39035"  | "Kuhfelde"                               | false
        "39036"  | "Binde"                                  | false
        "39037"  | "Pretzier"                               | false
        "39038"  | "Henningen"                              | false
        "39039"  | "Bonese"                                 | false
        "3904"   | "Haldensleben"                           | false
        "39050"  | "Bartensleben"                           | false
        "39051"  | "Calvörde"                               | false
        "39052"  | "Erxleben bei Haldensleben"              | false
        "39053"  | "Süplingen"                              | false
        "39054"  | "Flechtingen"                            | false
        "39055"  | "Hörsingen"                              | false
        "39056"  | "Klüden"                                 | false
        "39057"  | "Rätzlingen Sachsen-Anhalt"              | false
        "39058"  | "Uthmöden"                               | false
        "39059"  | "Wegenstedt"                             | false
        "39061"  | "Weferlingen"                            | false
        "39062"  | "Bebertal"                               | false
        "3907"   | "Gardelegen"                             | false
        "39080"  | "Kalbe Milde"                            | false
        "39081"  | "Kakerbeck Sachsen-Anhalt"               | false
        "39082"  | "Mieste"                                 | false
        "39083"  | "Messdorf"                               | false
        "39084"  | "Lindstedt"                              | false
        "39085"  | "Zichtau"                                | false
        "39086"  | "Jävenitz"                               | false
        "39087"  | "Jerchel Altmark"                        | false
        "39088"  | "Letzlingen"                             | false
        "39089"  | "Bismark Altmark"                        | false
        "3909"   | "Klötze Altmark"                         | false
        "391"    | "Magdeburg"                              | false
        "39200"  | "Gommern"                                | false
        "39201"  | "Wolmirstedt"                            | false
        "39202"  | "Gross Ammensleben"                      | false
        "39203"  | "Barleben"                               | false
        "39204"  | "Niederndodeleben"                       | false
        "39205"  | "Langenweddingen"                        | false
        "39206"  | "Eichenbarleben"                         | false
        "39207"  | "Colbitz"                                | false
        "39208"  | "Loitsche"                               | false
        "39209"  | "Wanzleben"                              | false
        "3921"   | "Burg bei Magdeburg"                     | false
        "39221"  | "Möckern bei Magdeburg"                  | false
        "39222"  | "Möser"                                  | false
        "39223"  | "Theessen"                               | false
        "39224"  | "Büden"                                  | false
        "39225"  | "Altengrabow"                            | false
        "39226"  | "Hohenziatz"                             | false
        "3923"   | "Zerbst"                                 | false
        "39241"  | "Leitzkau"                               | false
        "39242"  | "Prödel"                                 | false
        "39243"  | "Nedlitz bei Zerbst"                     | false
        "39244"  | "Steutz"                                 | false
        "39245"  | "Loburg"                                 | false
        "39246"  | "Lindau Anhalt"                          | true   // see https://issuetracker.google.com/issues/183383466
        "39247"  | "Güterglück"                             | false
        "39248"  | "Dobritz"                                | false
        "3925"   | "Stassfurt"                              | false
        "39262"  | "Güsten Anhalt"                          | true   // see https://issuetracker.google.com/issues/183383466
        "39263"  | "Unseburg"                               | false
        "39264"  | "Kroppenstedt"                           | false
        "39265"  | "Löderburg"                              | false
        "39266"  | "Förderstedt"                            | false
        "39267"  | "Schneidlingen"                          | false
        "39268"  | "Egeln"                                  | false
        "3928"   | "Schönebeck Elbe"                        | false
        "39291"  | "Calbe Saale"                            | false
        "39292"  | "Biederitz"                              | false
        "39293"  | "Dreileben"                              | false
        "39294"  | "Gross Rosenburg"                        | false
        "39295"  | "Zuchau"                                 | false
        "39296"  | "Welsleben"                              | false
        "39297"  | "Eickendorf Kreis Schönebeck"            | false
        "39298"  | "Barby Elbe"                             | false
        "3931"   | "Stendal"                                | false
        "39320"  | "Schinne"                                | false
        "39321"  | "Arneburg"                               | false
        "39322"  | "Tangermünde"                            | false
        "39323"  | "Schönhausen Elbe"                       | false
        "39324"  | "Kläden bei Stendal"                     | false
        "39325"  | "Vinzelberg"                             | false
        "39327"  | "Klietz"                                 | false
        "39328"  | "Rochau"                                 | false
        "39329"  | "Möringen"                               | false
        "3933"   | "Genthin"                                | false
        "39341"  | "Redekin"                                | false
        "39342"  | "Gladau"                                 | false
        "39343"  | "Jerichow"                               | false
        "39344"  | "Güsen"                                  | false
        "39345"  | "Parchen"                                | false
        "39346"  | "Tucheim"                                | false
        "39347"  | "Kade"                                   | false
        "39348"  | "Klitsche"                               | false
        "39349"  | "Parey Elbe"                             | false
        "3935"   | "Tangerhütte"                            | false
        "39361"  | "Lüderitz"                               | false
        "39362"  | "Grieben bei Tangerhütte"                | false
        "39363"  | "Angern"                                 | false
        "39364"  | "Dolle"                                  | false
        "39365"  | "Bellingen bei Stendal"                  | false
        "39366"  | "Kehnert"                                | false
        "3937"   | "Osterburg Altmark"                      | false
        "39382"  | "Kamern"                                 | false
        "39383"  | "Sandau Elbe"                            | false
        "39384"  | "Arendsee Altmark"                       | false
        "39386"  | "Seehausen Altmark"                      | false
        "39387"  | "Havelberg"                              | false
        "39388"  | "Goldbeck Altmark"                       | true   // see https://issuetracker.google.com/issues/183383466
        "39389"  | "Schollene"                              | false
        "39390"  | "Iden"                                   | false
        "39391"  | "Lückstedt"                              | false
        "39392"  | "Rönnebeck Sachsen Anhalt"               | false
        "39393"  | "Werben Elbe"                            | false
        "39394"  | "Hohenberg Krusemark"                    | false
        "39395"  | "Wanzer"                                 | false
        "39396"  | "Neukirchen Altmark"                     | false
        "39397"  | "Geestgottberg"                          | false
        "39398"  | "Gross Garz"                             | false
        "39399"  | "Kleinau"                                | false
        "39400"  | "Wefensleben"                            | false
        "39401"  | "Neuwegersleben"                         | false
        "39402"  | "Völpke"                                 | false
        "39403"  | "Gröningen Sachsen Anhalt"               | false
        "39404"  | "Ausleben"                               | false
        "39405"  | "Hötensleben"                            | false
        "39406"  | "Harbke"                                 | false
        "39407"  | "Seehausen Börde"                        | false
        "39408"  | "Hadmersleben"                           | false
        "39409"  | "Eilsleben"                              | false
        "3941"   | "Halberstadt"                            | false
        "39421"  | "Osterwieck"                             | false
        "39422"  | "Badersleben"                            | false
        "39423"  | "Wegeleben"                              | false
        "39424"  | "Schwanebeck Sachsen-Anhalt"             | false
        "39425"  | "Dingelstedt am Huy"                     | false
        "39426"  | "Hessen"                                 | false
        "39427"  | "Ströbeck"                               | false
        "39428"  | "Pabstorf"                               | false
        "3943"   | "Wernigerode"                            | false
        "3944"   | "Blankenburg Harz"                       | false
        "39451"  | "Wasserleben"                            | false
        "39452"  | "Ilsenburg"                              | false
        "39453"  | "Derenburg"                              | false
        "39454"  | "Elbingerode Harz"                       | false
        "39455"  | "Schierke"                               | false
        "39456"  | "Altenbrak"                              | false
        "39457"  | "Benneckenstein Harz"                    | false
        "39458"  | "Heudeber"                               | false
        "39459"  | "Hasselfelde"                            | false
        "3946"   | "Quedlinburg"                            | false
        "3947"   | "Thale"                                  | false
        "39481"  | "Hedersleben bei Aschersleben"           | false
        "39482"  | "Gatersleben"                            | false
        "39483"  | "Ballenstedt"                            | false
        "39484"  | "Harzgerode"                             | false
        "39485"  | "Gernrode Harz"                          | false
        "39487"  | "Friedrichsbrunn"                        | false
        "39488"  | "Güntersberge"                           | false
        "39489"  | "Strassberg Harz"                        | false
        "3949"   | "Oschersleben Bode"                      | false
        "395"    | "Neubrandenburg"                         | false
        "39600"  | "Zwiedorf"                               | false
        "39601"  | "Friedland Mecklenburg"                  | true   // TODO: ITU "Friedland" only, but BNetzA "Friedland Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "39602"  | "Kleeth"                                 | false
        "39603"  | "Burg Stargard"                          | false
        "39604"  | "Wildberg bei Altentreptow"              | false
        "39605"  | "Gross Nemerow"                          | false
        "39606"  | "Glienke"                                | false
        "39607"  | "Kotelow"                                | false
        "39608"  | "Staven"                                 | false
        "3961"   | "Altentreptow"                           | false
        "3962"   | "Penzlin bei Waren"                      | false
        "3963"   | "Woldegk"                                | false
        "3964"   | "Bredenfelde bei Strasburg"              | false
        "3965"   | "Burow bei Altentreptow"                 | false
        "3966"   | "Cölpin"                                 | false
        "3967"   | "Oertzenhof bei Strasburg"               | false
        "3968"   | "Schönbeck Mecklenburg"                  | true   // TODO: ITU "Schönbeck" only, but BNetzA "Schönbeck Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "3969"   | "Siedenbollentin"                        | false
        "3971"   | "Anklam"                                 | false
        "39721"  | "Liepen bei Anklam"                      | false
        "39722"  | "Sarnow bei Anklam"                      | false
        "39723"  | "Krien"                                  | false
        "39724"  | "Klein Bünzow"                           | false
        "39726"  | "Ducherow"                               | false
        "39727"  | "Spantekow"                              | false
        "39728"  | "Medow bei Anklam"                       | false
        "3973"   | "Pasewalk"                               | false
        "39740"  | "Nechlin"                                | false
        "39741"  | "Jatznick"                               | false
        "39742"  | "Brüssow bei Pasewalk"                   | false
        "39743"  | "Zerrenthin"                             | false
        "39744"  | "Rothenklempenow"                        | false
        "39745"  | "Hetzdorf bei Strasburg"                 | false
        "39746"  | "Krackow"                                | false
        "39747"  | "Züsedom"                                | false
        "39748"  | "Viereck"                                | false
        "39749"  | "Grambow bei Pasewalk"                   | false
        "39751"  | "Penkun"                                 | false
        "39752"  | "Blumenhagen bei Strasburg"              | false
        "39753"  | "Strasburg"                              | false
        "39754"  | "Löcknitz Vorpommern"                    | true   // see https://issuetracker.google.com/issues/183383466
        "3976"   | "Torgelow bei Ueckermünde"               | false
        "39771"  | "Ueckermünde"                            | false
        "39772"  | "Rothemühl"                              | false
        "39773"  | "Altwarp"                                | false
        "39774"  | "Mönkebude"                              | false
        "39775"  | "Ahlbeck bei Torgelow"                   | false
        "39776"  | "Hintersee"                              | false
        "39777"  | "Borkenfriede"                           | false
        "39778"  | "Ferdinandshof bei Torgelow"             | false
        "39779"  | "Eggesin"                                | false
        "3981"   | "Neustrelitz"                            | false
        "39820"  | "Triepkendorf"                           | false
        "39821"  | "Carpin"                                 | false
        "39822"  | "Kratzeburg"                             | false
        "39823"  | "Rechlin"                                | false
        "39824"  | "Hohenzieritz"                           | false
        "39825"  | "Wokuhl"                                 | false
        "39826"  | "Blankensee bei Neustrelitz"             | false
        "39827"  | "Schwarz bei Neustrelitz"                | false
        "39828"  | "Wustrow Kreis Mecklenburg Strelitz"     | false
        "39829"  | "Blankenförde"                           | false
        "39831"  | "Feldberg Mecklenburg"                   | true   // TODO: ITU "Feldberg" only, but BNetzA "Feldberg Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "39832"  | "Wesenberg Mecklenburg"                  | true   // TODO: ITU "Wesenberg" only, but BNetzA "Wesenberg Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "39833"  | "Mirow Kreis Neustrelitz"                | false
        "3984"   | "Prenzlau"                               | false
        "39851"  | "Göritz bei Prenzlau"                    | false
        "39852"  | "Schönermark bei Prenzlau"               | false
        "39853"  | "Holzendorf bei Prenzlau"                | false
        "39854"  | "Kleptow"                                | false
        "39855"  | "Parmen Weggun"                          | false
        "39856"  | "Beenz bei Prenzlau"                     | false
        "39857"  | "Drense"                                 | false
        "39858"  | "Bietikow"                               | false
        "39859"  | "Fürstenwerder"                          | false
        "39861"  | "Gramzow bei Prenzlau"                   | false
        "39862"  | "Schmölln bei Prenzlau"                  | false
        "39863"  | "Seehausen bei Prenzlau"                 | false
        "3987"   | "Templin"                                | false
        "39881"  | "Ringenwalde bei Templin"                | false
        "39882"  | "Gollin"                                 | false
        "39883"  | "Groß Dölln"                             | false
        "39884"  | "Hassleben bei Prenzlau"                 | false
        "39885"  | "Jakobshagen"                            | false
        "39886"  | "Milmersdorf"                            | false
        "39887"  | "Gerswalde"                              | false
        "39888"  | "Lychen"                                 | false
        "39889"  | "Boitzenburg"                            | false
        "3991"   | "Waren Müritz"                           | false
        "39921"  | "Ankershagen"                            | false
        "39922"  | "Dambeck bei Röbel"                      | false
        "39923"  | "Priborn"                                | false
        "39924"  | "Stuer"                                  | false
        "39925"  | "Wredenhagen"                            | false
        "39926"  | "Grabowhöfe"                             | false
        "39927"  | "Nossentiner Hütte"                      | false
        "39928"  | "Möllenhagen"                            | false
        "39929"  | "Jabel bei Waren"                        | false
        "39931"  | "Röbel Müritz"                           | false
        "39932"  | "Malchow bei Waren"                      | false
        "39933"  | "Vollrathsruhe"                          | false
        "39934"  | "Groß Plasten"                           | false
        "3994"   | "Malchin"                                | false
        "39951"  | "Faulenrost"                             | false
        "39952"  | "Grammentin"                             | false
        "39953"  | "Schwinkendorf"                          | false
        "39954"  | "Stavenhagen Reuterstadt"                | false
        "39955"  | "Jürgenstorf Mecklenburg"                | true   // TODO: ITU "Jürgenstorf" only, but BNetzA "Jürgenstorf Meckl", which is short form of "Mecklenburg", it is not included in PhoneLibe data.
        "39956"  | "Neukalen"                               | false
        "39957"  | "Gielow"                                 | false
        "39959"  | "Dargun"                                 | false
        "3996"   | "Teterow"                                | false
        "39971"  | "Gnoien"                                 | false
        "39972"  | "Walkendorf"                             | false
        "39973"  | "Altkalen"                               | false
        "39975"  | "Thürkow"                                | false
        "39976"  | "Groß Bützin"                            | false
        "39977"  | "Jördenstorf"                            | false
        "39978"  | "Gross Roge"                             | false
        "3998"   | "Demmin"                                 | false
        "39991"  | "Daberkow"                               | false
        "39992"  | "Görmin"                                 | false
        "39993"  | "Hohenmocker"                            | false
        "39994"  | "Metschow"                               | false
        "39995"  | "Nossendorf"                             | false
        "39996"  | "Törpin"                                 | false
        "39997"  | "Jarmen"                                 | false
        "39998"  | "Loitz bei Demmin"                       | false
        "39999"  | "Tutow"                                  | false
        "40"     | "Hamburg"                                | false
        "4101"   | "Pinneberg"                              | false
        "4102"   | "Ahrensburg"                             | false
        "4103"   | "Wedel"                                  | false
        "4104"   | "Aumühle bei Hamburg"                    | false
        "4105"   | "Seevetal"                               | false
        "4106"   | "Quickborn Kreis Pinneberg"              | false
        "4107"   | "Siek Kreis Stormarn"                    | false
        "4108"   | "Rosengarten Kreis Harburg"              | false
        "4109"   | "Tangstedt Bezirk Hamburg"               | true   // see https://issuetracker.google.com/issues/183383466
        "4120"   | "Ellerhoop"                              | false
        "4121"   | "Elmshorn"                               | false
        "4122"   | "Uetersen"                               | false
        "4123"   | "Barmstedt"                              | false
        "4124"   | "Glückstadt"                             | false
        "4125"   | "Seestermühe"                            | false
        "4126"   | "Horst Holstein"                         | false
        "4127"   | "Westerhorn"                             | false
        "4128"   | "Kollmar"                                | false
        "4129"   | "Haseldorf"                              | false
        "4131"   | "Lüneburg"                               | false
        "4132"   | "Amelinghausen"                          | false
        "4133"   | "Wittorf Kreis Lüneburg"                 | false
        "4134"   | "Embsen Kreis Lüneburg"                  | false
        "4135"   | "Kirchgellersen"                         | false
        "4136"   | "Scharnebeck"                            | false
        "4137"   | "Barendorf"                              | false
        "4138"   | "Betzendorf Kreis Lüneburg"              | false
        "4139"   | "Hohnstorf Elbe"                         | false
        "4140"   | "Estorf Kreis Stade"                     | false
        "4141"   | "Stade"                                  | false
        "4142"   | "Steinkirchen Kreis Stade"               | false
        "4143"   | "Drochtersen"                            | false
        "4144"   | "Himmelpforten"                          | false
        "4146"   | "Stade Bützfleth"                        | false
        "4148"   | "Drochtersen Assel"                      | false
        "4149"   | "Fredenbeck"                             | false
        "4151"   | "Schwarzenbek"                           | false
        "4152"   | "Geesthacht"                             | false
        "4153"   | "Lauenburg Elbe"                         | false
        "4154"   | "Trittau"                                | false
        "4155"   | "Büchen"                                 | false
        "4156"   | "Talkau"                                 | false
        "4158"   | "Roseburg"                               | false
        "4159"   | "Basthorst"                              | false
        "4161"   | "Buxtehude"                              | false
        "4162"   | "Jork"                                   | false
        "4163"   | "Horneburg Niederelbe"                   | false
        "4164"   | "Harsefeld"                              | false
        "4165"   | "Hollenstedt Nordheide"                  | false
        "4166"   | "Ahlerstedt"                             | false
        "4167"   | "Apensen"                                | false
        "4168"   | "Neu Wulmstorf Elstorf"                  | false
        "4169"   | "Sauensiek"                              | false
        "4171"   | "Winsen Luhe"                            | false
        "4172"   | "Salzhausen"                             | false
        "4173"   | "Wulfsen"                                | false
        "4174"   | "Stelle Kreis Harburg"                   | false
        "4175"   | "Egestorf Nordheide"                     | false
        "4176"   | "Marschacht"                             | false
        "4177"   | "Drage Elbe"                             | false
        "4178"   | "Radbruch"                               | false
        "4179"   | "Winsen Tönnhausen"                      | false
        "4180"   | "Königsmoor"                             | false
        "4181"   | "Buchholz in der Nordheide"              | false
        "4182"   | "Tostedt"                                | false
        "4183"   | "Jesteburg"                              | false
        "4184"   | "Hanstedt Nordheide"                     | false
        "4185"   | "Marxen Auetal"                          | false
        "4186"   | "Buchholz Trelde"                        | false
        "4187"   | "Holm Seppensen"                         | false
        "4188"   | "Welle Nordheide"                        | false
        "4189"   | "Undeloh"                                | false
        "4191"   | "Kaltenkirchen Holstein"                 | false
        "4192"   | "Bad Bramstedt"                          | false
        "4193"   | "Henstedt Ulzburg"                       | false
        "4194"   | "Sievershütten"                          | false
        "4195"   | "Hartenholm"                             | false
        "4202"   | "Achim bei Bremen"                       | false
        "4203"   | "Weyhe bei Bremen"                       | false
        "4204"   | "Thedinghausen"                          | false
        "4205"   | "Ottersberg"                             | false
        "4206"   | "Stuhr Heiligenrode"                     | false
        "4207"   | "Oyten"                                  | false
        "4208"   | "Grasberg"                               | false
        "4209"   | "Schwanewede"                            | false
        "421"    | "Bremen"                                 | false
        "4221"   | "Delmenhorst"                            | false
        "4222"   | "Ganderkesee"                            | false
        "4223"   | "Ganderkesee Bookholzberg"               | false
        "4224"   | "Gross Ippener"                          | false
        "4230"   | "Verden Walle"                           | false
        "4231"   | "Verden Aller"                           | false
        "4232"   | "Langwedel Kreis Verden"                 | false
        "4233"   | "Blender"                                | false
        "4234"   | "Dörverden"                              | false
        "4235"   | "Langwedel Etelsen"                      | false
        "4236"   | "Kirchlinteln"                           | false
        "4237"   | "Bendingbostel"                          | false
        "4238"   | "Neddenaverbergen"                       | false
        "4239"   | "Dörverden Westen"                       | false
        "4240"   | "Syke Heiligenfelde"                     | false
        "4241"   | "Bassum"                                 | false
        "4242"   | "Syke"                                   | false
        "4243"   | "Twistringen"                            | false
        "4244"   | "Harpstedt"                              | false
        "4245"   | "Neuenkirchen bei Bassum"                | false
        "4246"   | "Twistringen Heiligenloh"                | false
        "4247"   | "Affinghausen"                           | false
        "4248"   | "Bassum Neubruchhausen"                  | false
        "4249"   | "Bassum Nordwohlde"                      | false
        "4251"   | "Hoya"                                   | false
        "4252"   | "Bruchhausen Vilsen"                     | false
        "4253"   | "Asendorf Kreis Diepholz"                | false
        "4254"   | "Eystrup"                                | false
        "4255"   | "Martfeld"                               | false
        "4256"   | "Hilgermissen"                           | false
        "4257"   | "Schweringen"                            | false
        "4258"   | "Schwarme"                               | false
        "4260"   | "Visselhövede Wittorf"                   | false
        "4261"   | "Rotenburg Wümme"                        | false
        "4262"   | "Visselhövede"                           | false
        "4263"   | "Scheessel"                              | false
        "4264"   | "Sottrum Kreis Rotenburg"                | false
        "4265"   | "Fintel"                                 | false
        "4266"   | "Brockel"                                | false
        "4267"   | "Lauenbrück"                             | false
        "4268"   | "Bötersen"                               | false
        "4269"   | "Ahausen Kirchwalsede"                   | false
        "4271"   | "Sulingen"                               | false
        "4272"   | "Siedenburg"                             | false
        "4273"   | "Kirchdorf bei Sulingen"                 | false
        "4274"   | "Varrel bei Sulingen"                    | false
        "4275"   | "Ehrenburg"                              | false
        "4276"   | "Borstel bei Sulingen"                   | false
        "4277"   | "Schwaförden"                            | false
        "4281"   | "Zeven"                                  | false
        "4282"   | "Sittensen"                              | false
        "4283"   | "Tarmstedt"                              | false
        "4284"   | "Selsingen"                              | false
        "4285"   | "Rhade bei Zeven"                        | false
        "4286"   | "Gyhum"                                  | false
        "4287"   | "Heeslingen Boitzen"                     | false
        "4288"   | "Horstedt Kreis Rotenburg"               | false
        "4289"   | "Kirchtimke"                             | false
        "4292"   | "Ritterhude"                             | false
        "4293"   | "Ottersberg Fischerhude"                 | false
        "4294"   | "Riede Kreis Verden"                     | false
        "4295"   | "Emtinghausen"                           | false
        "4296"   | "Schwanewede Aschwarden"                 | false
        "4297"   | "Ottersberg Posthausen"                  | false
        "4298"   | "Lilienthal"                             | false
        "4302"   | "Kirchbarkau"                            | false
        "4303"   | "Schlesen"                               | false
        "4305"   | "Westensee"                              | false
        "4307"   | "Raisdorf"                               | false
        "4308"   | "Schwedeneck"                            | false
        "431"    | "Kiel"                                   | false
        "4320"   | "Heidmühlen"                             | false
        "4321"   | "Neumünster"                             | false
        "4322"   | "Bordesholm"                             | false
        "4323"   | "Bornhöved"                              | false
        "4324"   | "Brokstedt"                              | false
        "4326"   | "Wankendorf"                             | false
        "4327"   | "Grossenaspe"                            | false
        "4328"   | "Rickling"                               | false
        "4329"   | "Langwedel Holstein"                     | false
        "4330"   | "Emkendorf"                              | false
        "4331"   | "Rendsburg"                              | false
        "4332"   | "Hamdorf bei Rendsburg"                  | false
        "4333"   | "Erfde"                                  | false
        "4334"   | "Bredenbek bei Rendsburg"                | false
        "4335"   | "Hohn bei Rendsburg"                     | false
        "4336"   | "Owschlag"                               | false
        "4337"   | "Jevenstedt"                             | false
        "4338"   | "Alt Duvenstedt"                         | false
        "4339"   | "Christiansholm"                         | false
        "4340"   | "Achterwehr"                             | false
        "4342"   | "Preetz Kreis Plön"                      | false
        "4343"   | "Laboe"                                  | false
        "4344"   | "Schönberg Holstein"                     | false
        "4346"   | "Gettorf"                                | false
        "4347"   | "Flintbek"                               | false
        "4348"   | "Schönkirchen"                           | false
        "4349"   | "Dänischenhagen"                         | false
        "4351"   | "Eckernförde"                            | false
        "4352"   | "Damp"                                   | false
        "4353"   | "Ascheffel"                              | false
        "4354"   | "Fleckeby"                               | false
        "4355"   | "Rieseby"                                | false
        "4356"   | "Gross Wittensee"                        | false
        "4357"   | "Sehestedt Eider"                        | false
        "4358"   | "Loose bei Eckernförde"                  | false
        "4361"   | "Oldenburg in Holstein"                  | false
        "4362"   | "Heiligenhafen"                          | false
        "4363"   | "Lensahn"                                | false
        "4364"   | "Dahme Kreis Ostholstein"                | false
        "4365"   | "Heringsdorf Holstein"                   | false
        "4366"   | "Grömitz Cismar"                         | false
        "4367"   | "Grossenbrode"                           | false
        "4371"   | "Burg auf Fehmarn"                       | false
        "4372"   | "Westfehmarn"                            | false
        "4381"   | "Lütjenburg"                             | false
        "4382"   | "Wangels"                                | false
        "4383"   | "Grebin"                                 | false
        "4384"   | "Selent"                                 | false
        "4385"   | "Hohenfelde bei Kiel"                    | false
        "4392"   | "Nortorf bei Neumünster"                 | false
        "4393"   | "Boostedt"                               | false
        "4394"   | "Bokhorst"                               | false
        "4401"   | "Brake Unterweser"                       | false
        "4402"   | "Rastede"                                | false
        "4403"   | "Bad Zwischenahn"                        | false
        "4404"   | "Elsfleth"                               | false
        "4405"   | "Edewecht"                               | false
        "4406"   | "Berne"                                  | false
        "4407"   | "Wardenburg"                             | false
        "4408"   | "Hude Oldenburg"                         | false
        "4409"   | "Westerstede Ocholt"                     | false
        "441"    | "Oldenburg"                              | false
        "4421"   | "Wilhelmshaven"                          | false
        "4422"   | "Sande Kreis Friesl"                     | false
        "4423"   | "Fedderwarden"                           | false
        "4425"   | "Wangerland Hooksiel"                    | false
        "4426"   | "Wangerland Horumersiel"                 | false
        "4431"   | "Wildeshausen"                           | false
        "4432"   | "Dötlingen Brettorf"                     | false
        "4433"   | "Dötlingen"                              | false
        "4434"   | "Colnrade"                               | false
        "4435"   | "Grossenkneten"                          | false
        "4441"   | "Vechta"                                 | false
        "4442"   | "Lohne Oldenburg"                        | false
        "4443"   | "Dinklage"                               | false
        "4444"   | "Goldenstedt"                            | false
        "4445"   | "Visbek Kreis Vechta"                    | false
        "4446"   | "Bakum Kreis Vechta"                     | false
        "4447"   | "Vechta Langförden"                      | false
        "4451"   | "Varel Jadebusen"                        | false
        "4452"   | "Zetel Neuenburg"                        | false
        "4453"   | "Zetel"                                  | false
        "4454"   | "Jade"                                   | false
        "4455"   | "Jade Schweiburg"                        | false
        "4456"   | "Varel Altjührden"                       | false
        "4458"   | "Wiefelstede Spohle"                     | false
        "4461"   | "Jever"                                  | false
        "4462"   | "Wittmund"                               | false
        "4463"   | "Wangerland"                             | false
        "4464"   | "Wittmund Carolinensiel"                 | false
        "4465"   | "Friedeburg Ostfriesland"                | false
        "4466"   | "Wittmund Ardorf"                        | false
        "4467"   | "Wittmund Funnix"                        | false
        "4468"   | "Friedeburg Reepsholt"                   | false
        "4469"   | "Wangerooge"                             | false
        "4471"   | "Cloppenburg"                            | false
        "4472"   | "Lastrup"                                | false
        "4473"   | "Emstek"                                 | false
        "4474"   | "Garrel"                                 | false
        "4475"   | "Molbergen"                              | false
        "4477"   | "Lastrup Hemmelte"                       | false
        "4478"   | "Cappeln Oldenburg"                      | false
        "4479"   | "Molbergen Peheim"                       | false
        "4480"   | "Ovelgönne Strückhausen"                 | false
        "4481"   | "Hatten Sandkrug"                        | false
        "4482"   | "Hatten"                                 | false
        "4483"   | "Ovelgönne Großenmeer"                   | false
        "4484"   | "Hude Wüsting"                           | false
        "4485"   | "Elsfleth Huntorf"                       | false
        "4486"   | "Edewecht Friedrichsfehn"                | false
        "4487"   | "Grossenkneten Huntlosen"                | false
        "4488"   | "Westerstede"                            | false
        "4489"   | "Apen"                                   | false
        "4491"   | "Friesoythe"                             | false
        "4492"   | "Saterland"                              | false
        "4493"   | "Friesoythe Gehlenberg"                  | false
        "4494"   | "Bösel Oldenburg"                        | false
        "4495"   | "Friesoythe Thüle"                       | false
        "4496"   | "Friesoythe Markhausen"                  | false
        "4497"   | "Barßel Harkebrügge"                     | false
        "4498"   | "Saterland Ramsloh"                      | false
        "4499"   | "Barssel"                                | false
        "4501"   | "Kastorf Holstein"                       | false
        "4502"   | "Lübeck Travemünde"                      | false
        "4503"   | "Timmendorfer Strand"                    | false
        "4504"   | "Ratekau"                                | false
        "4505"   | "Stockelsdorf Curau"                     | false
        "4506"   | "Stockelsdorf Krumbeck"                  | false
        "4508"   | "Krummesse"                              | false
        "4509"   | "Groß Grönau"                            | false
        "451"    | "Lübeck"                                 | false
        "4521"   | "Eutin"                                  | false
        "4522"   | "Plön"                                   | false
        "4523"   | "Malente"                                | false
        "4524"   | "Scharbeutz Pönitz"                      | false
        "4525"   | "Ahrensbök"                              | false
        "4526"   | "Ascheberg Holstein"                     | false
        "4527"   | "Bosau"                                  | false
        "4528"   | "Schönwalde am Bungsberg"                | false
        "4529"   | "Süsel Bujendorf"                        | false
        "4531"   | "Bad Oldesloe"                           | false
        "4532"   | "Bargteheide"                            | false
        "4533"   | "Reinfeld Holstein"                      | false
        "4534"   | "Steinburg Kreis Storman"                | false
        "4535"   | "Nahe"                                   | false
        "4536"   | "Steinhorst Lauenburg"                   | false
        "4537"   | "Sülfeld Holstein"                       | false
        "4539"   | "Westerau"                               | false
        "4541"   | "Ratzeburg"                              | false
        "4542"   | "Mölln Lauenburg"                        | false
        "4543"   | "Nusse"                                  | false
        "4544"   | "Berkenthin"                             | false
        "4545"   | "Seedorf Lauenburg"                      | false
        "4546"   | "Mustin Lauenburg"                       | false
        "4547"   | "Gudow Lauenburg"                        | false
        "4550"   | "Bühnsdorf"                              | false
        "4551"   | "Bad Segeberg"                           | false
        "4552"   | "Leezen"                                 | false
        "4553"   | "Geschendorf"                            | false
        "4554"   | "Wahlstedt"                              | false
        "4555"   | "Seedorf bei Bad Segeberg"               | false
        "4556"   | "Ahrensbök Gnissau"                      | false
        "4557"   | "Blunk"                                  | false
        "4558"   | "Todesfelde"                             | false
        "4559"   | "Wensin"                                 | false
        "4561"   | "Neustadt in Holstein"                   | false
        "4562"   | "Grömitz"                                | false
        "4563"   | "Scharbeutz Haffkrug"                    | false
        "4564"   | "Schashagen"                             | false
        "4602"   | "Freienwill"                             | false
        "4603"   | "Havetoft"                               | false
        "4604"   | "Grossenwiehe"                           | false
        "4605"   | "Medelby"                                | false
        "4606"   | "Wanderup"                               | false
        "4607"   | "Janneby"                                | false
        "4608"   | "Handewitt"                              | false
        "4609"   | "Eggebek"                                | false
        "461"    | "Flensburg"                              | false
        "4621"   | "Schleswig"                              | false
        "4622"   | "Taarstedt"                              | false
        "4623"   | "Böklund"                                | false
        "4624"   | "Kropp"                                  | false
        "4625"   | "Jübek"                                  | false
        "4626"   | "Treia"                                  | false
        "4627"   | "Dörpstedt"                              | false
        "4630"   | "Barderup"                               | false
        "4631"   | "Glücksburg Ostsee"                      | false
        "4632"   | "Steinbergkirche"                        | false
        "4633"   | "Satrup"                                 | false
        "4634"   | "Husby"                                  | false
        "4635"   | "Sörup"                                  | false
        "4636"   | "Langballig"                             | false
        "4637"   | "Sterup"                                 | false
        "4638"   | "Tarp"                                   | false
        "4639"   | "Schafflund"                             | false
        "4641"   | "Süderbrarup"                            | false
        "4642"   | "Kappeln Schlei"                         | false
        "4643"   | "Gelting Angeln"                         | false
        "4644"   | "Karby"                                  | false
        "4646"   | "Mohrkirch"                              | false
        "4651"   | "Sylt"                                   | false  // ignoring the last digit (1) since 8.10.1
        "4661"   | "Niebüll"                                | false
        "4662"   | "Leck"                                   | false
        "4663"   | "Süderlügum"                             | false
        "4664"   | "Neukirchen bei Niebüll"                 | false
        "4665"   | "Emmelsbüll Horsbüll"                    | false
        "4666"   | "Ladelund"                               | false
        "4667"   | "Dagebüll"                               | false
        "4668"   | "Klanxbüll"                              | false
        "4671"   | "Bredstedt"                              | false
        "4672"   | "Langenhorn"                             | false
        "4673"   | "Joldelund"                              | false
        "4674"   | "Ockholm"                                | false
        "4681"   | "Wyk auf Föhr"                           | false
        "4682"   | "Amrum"                                  | false
        "4683"   | "Oldsum"                                 | false
        "4684"   | "Langeneß Hallig"                        | false
        "4702"   | "Sandstedt"                              | false
        "4703"   | "Loxstedt Donnern"                       | false
        "4704"   | "Drangstedt"                             | false
        "4705"   | "Wremen"                                 | false
        "4706"   | "Schiffdorf"                             | false
        "4707"   | "Langen Neuenwalde"                      | false
        "4708"   | "Ringstedt"                              | false
        "471"    | "Bremerhaven"                            | false
        "4721"   | "Cuxhaven"                               | false
        "4722"   | "Cuxhaven Altenbruch"                    | false
        "4723"   | "Cuxhaven Altenwalde"                    | false
        "4724"   | "Cuxhaven Lüdingworth"                   | false
        "4725"   | "Helgoland"                              | false
        "4731"   | "Nordenham"                              | false
        "4732"   | "Stadland Rodenkirchen"                  | false
        "4733"   | "Butjadingen Burhave"                    | false
        "4734"   | "Stadland Seefeld"                       | false
        "4735"   | "Butjadingen Stollhamm"                  | false
        "4736"   | "Butjadingen Tossens"                    | false
        "4737"   | "Stadland Schwei"                        | false
        "4740"   | "Loxstedt Dedesdorf"                     | false
        "4741"   | "Nordholz bei Bremerhaven"               | false
        "4742"   | "Dorum"                                  | false
        "4743"   | "Langen bei Bremerhaven"                 | false
        "4744"   | "Loxstedt"                               | false
        "4745"   | "Bad Bederkesa"                          | false
        "4746"   | "Hagen bei Bremerhaven"                  | false
        "4747"   | "Beverstedt"                             | false
        "4748"   | "Stubben bei Bremerhaven"                | false
        "4749"   | "Schiffdorf Geestenseth"                 | false
        "4751"   | "Otterndorf"                             | false
        "4752"   | "Neuhaus Oste"                           | false
        "4753"   | "Balje"                                  | false
        "4754"   | "Bülkau"                                 | false
        "4755"   | "Ihlienworth"                            | false
        "4756"   | "Odisheim"                               | false
        "4757"   | "Wanna"                                  | false
        "4758"   | "Nordleda"                               | false
        "4761"   | "Bremervörde"                            | false
        "4762"   | "Kutenholz"                              | false
        "4763"   | "Gnarrenburg"                            | false
        "4764"   | "Gnarrenburg Klenkendorf"                | false
        "4765"   | "Ebersdorf bei Bremervörde"              | false
        "4766"   | "Basdahl"                                | false
        "4767"   | "Bremervörde Bevern"                     | false
        "4768"   | "Hipstedt"                               | false
        "4769"   | "Bremervörde Iselersheim"                | false
        "4770"   | "Wischhafen"                             | false
        "4771"   | "Hemmoor"                                | false
        "4772"   | "Oberndorf Oste"                         | false
        "4773"   | "Lamstedt"                               | false
        "4774"   | "Hechthausen"                            | false
        "4775"   | "Grossenwörden"                          | false
        "4776"   | "Osten Altendorf"                        | false
        "4777"   | "Cadenberge"                             | false
        "4778"   | "Wingst"                                 | false
        "4779"   | "Freiburg Elbe"                          | false
        "4791"   | "Osterholz Scharmbeck"                   | false
        "4792"   | "Worpswede"                              | false
        "4793"   | "Hambergen"                              | false
        "4794"   | "Worpswede Ostersode"                    | false
        "4795"   | "Garlstedt"                              | false
        "4796"   | "Teufelsmoor"                            | false
        "4802"   | "Wrohm"                                  | false
        "4803"   | "Pahlen"                                 | false
        "4804"   | "Nordhastedt"                            | false
        "4805"   | "Schafstedt"                             | false
        "4806"   | "Sarzbüttel"                             | false
        "481"    | "Heide Holstein"                         | false
        "4821"   | "Itzehoe"                                | false
        "4822"   | "Kellinghusen"                           | false
        "4823"   | "Wilster"                                | false
        "4824"   | "Krempe"                                 | false
        "4825"   | "Burg Dithmarschen"                      | false
        "4826"   | "Hohenlockstedt"                         | false
        "4827"   | "Wacken"                                 | false
        "4828"   | "Lägerdorf"                              | false
        "4829"   | "Wewelsfleth"                            | false
        "4830"   | "Süderhastedt"                           | false
        "4832"   | "Meldorf"                                | false
        "4833"   | "Wesselburen"                            | false
        "4834"   | "Büsum"                                  | false
        "4835"   | "Albersdorf Holstein"                    | false
        "4836"   | "Hennstedt Dithmarschen"                 | false
        "4837"   | "Neuenkirchen Dithmarschen"              | false
        "4838"   | "Tellingstedt"                           | false
        "4839"   | "Wöhrden Dithmarschen"                   | false
        "4841"   | "Husum Nordsee"                          | false
        "4842"   | "Nordstrand"                             | false
        "4843"   | "Viöl"                                   | false
        "4844"   | "Pellworm"                               | false
        "4845"   | "Ostenfeld Husum"                        | false
        "4846"   | "Hattstedt"                              | false
        "4847"   | "Oster Ohrstedt"                         | false
        "4848"   | "Rantrum"                                | false
        "4849"   | "Hooge"                                  | false
        "4851"   | "Marne"                                  | false
        "4852"   | "Brunsbüttel"                            | false
        "4853"   | "Sankt Michaelisdonn"                    | false
        "4854"   | "Friedrichskoog"                         | false
        "4855"   | "Eddelak"                                | false
        "4856"   | "Kronprinzenkoog"                        | false
        "4857"   | "Barlt"                                  | false
        "4858"   | "Sankt Margarethen Holstein"             | false
        "4859"   | "Windbergen"                             | false
        "4861"   | "Tönning"                                | false
        "4862"   | "Garding"                                | false
        "4863"   | "Sankt Peter Ording"                     | false
        "4864"   | "Oldenswort"                             | false
        "4865"   | "Osterhever"                             | false
        "4871"   | "Hohenwestedt"                           | false
        "4872"   | "Hanerau Hademarschen"                   | false
        "4873"   | "Aukrug"                                 | false
        "4874"   | "Todenbüttel"                            | false
        "4875"   | "Stafstedt"                              | false
        "4876"   | "Reher Holstein"                         | false
        "4877"   | "Hennstedt bei Itzehoe"                  | false
        "4881"   | "Friedrichstadt"                         | false
        "4882"   | "Lunden"                                 | false
        "4883"   | "Süderstapel"                            | false
        "4884"   | "Schwabstedt"                            | false
        "4885"   | "Bergenhusen"                            | false
        "4892"   | "Schenefeld Mittelholstein"              | false
        "4893"   | "Hohenaspe"                              | false
        "4902"   | "Jemgum Ditzum"                          | false
        "4903"   | "Wymeer"                                 | false
        "491"    | "Leer Ostfriesland"                      | false
        "4920"   | "Wirdum"                                 | false
        "4921"   | "Emden Stadt"                            | false
        "4922"   | "Borkum"                                 | false
        "4923"   | "Krummhörn Pewsum"                       | false
        "4924"   | "Moormerland Oldersum"                   | false
        "4925"   | "Hinte"                                  | false
        "4926"   | "Krummhörn Greetsiel"                    | false
        "4927"   | "Krummhörn Loquard"                      | false
        "4928"   | "Ihlow Riepe"                            | false
        "4929"   | "Ihlow Kreis Aurich"                     | false
        "4931"   | "Norden"                                 | false
        "4932"   | "Norderney"                              | false
        "4933"   | "Dornum Ostfriesland"                    | false
        "4934"   | "Marienhafe"                             | false
        "4935"   | "Juist"                                  | false
        "4936"   | "Grossheide"                             | false
        "4938"   | "Hagermarsch"                            | false
        "4939"   | "Baltrum"                                | false
        "4941"   | "Aurich"                                 | false
        "4942"   | "Südbrookmerland"                        | false
        "4943"   | "Grossefehn"                             | false
        "4944"   | "Wiesmoor"                               | false
        "4945"   | "Grossefehn Timmel"                      | false
        "4946"   | "Grossefehn Bagband"                     | false
        "4947"   | "Aurich Ogenbargen"                      | false
        "4948"   | "Wiesmoor Marcardsmoor"                  | false
        "4950"   | "Holtland"                               | false
        "4951"   | "Weener"                                 | false
        "4952"   | "Rhauderfehn"                            | false
        "4953"   | "Bunde"                                  | false
        "4954"   | "Moormerland"                            | false
        "4955"   | "Westoverledingen"                       | false
        "4956"   | "Uplengen"                               | false
        "4957"   | "Detern"                                 | false
        "4958"   | "Jemgum"                                 | false
        "4959"   | "Dollart"                                | false
        "4961"   | "Papenburg"                              | false
        "4962"   | "Papenburg Aschendorf"                   | false
        "4963"   | "Dörpen"                                 | false
        "4964"   | "Rhede Ems"                              | false
        "4965"   | "Surwold"                                | false
        "4966"   | "Neubörger"                              | false
        "4967"   | "Rhauderfehn Burlage"                    | false
        "4968"   | "Neulehe"                                | false
        "4971"   | "Esens"                                  | false
        "4972"   | "Langeoog"                               | false
        "4973"   | "Wittmund Burhafe"                       | false
        "4974"   | "Neuharlingersiel"                       | false
        "4975"   | "Westerholt Ostfriesland"                | false
        "4976"   | "Spiekeroog"                             | false
        "4977"   | "Blomberg Ostfriesland"                  | false
        "5021"   | "Nienburg Weser"                         | false
        "5022"   | "Wietzen"                                | false
        "5023"   | "Liebenau Kreis Nieburg Weser"           | false
        "5024"   | "Rohrsen Kreis Nienburg Weser"           | false
        "5025"   | "Estorf Weser"                           | false
        "5026"   | "Steimbke"                               | false
        "5027"   | "Linsburg"                               | false
        "5028"   | "Pennigsehl"                             | false
        "5031"   | "Wunstorf"                               | false
        "5032"   | "Neustadt am Rübenberge"                 | false
        "5033"   | "Wunstorf Grossenheidorn"                | false
        "5034"   | "Neustadt Hagen"                         | false
        "5035"   | "Gross Munzel"                           | false
        "5036"   | "Neustadt Schneeren"                     | false
        "5037"   | "Bad Rehburg"                            | false
        "5041"   | "Springe Deister"                        | false
        "5042"   | "Bad Münder am Deister"                  | false
        "5043"   | "Lauenau"                                | false
        "5044"   | "Springe Eldagsen"                       | false
        "5045"   | "Springe Bennigsen"                      | false
        "5051"   | "Bergen Kreis Celle"                     | false
        "5052"   | "Hermannsburg"                           | false
        "5053"   | "Faßberg Müden"                          | false
        "5054"   | "Bergen Sülze"                           | false
        "5055"   | "Fassberg"                               | false
        "5056"   | "Winsen Meissendorf"                     | false
        "5060"   | "Bodenburg"                              | false
        "5062"   | "Holle bei Hildesheim"                   | false
        "5063"   | "Bad Salzdetfurth"                       | false
        "5064"   | "Groß Düngen"                            | false
        "5065"   | "Sibbesse"                               | false
        "5066"   | "Sarstedt"                               | false
        "5067"   | "Bockenem"                               | false
        "5068"   | "Elze Leine"                             | false
        "5069"   | "Nordstemmen"                            | false
        "5071"   | "Schwarmstedt"                           | false
        "5072"   | "Neustadt Mandelsloh"                    | false
        "5073"   | "Neustadt Esperke"                       | false
        "5074"   | "Rodewald"                               | false
        "5082"   | "Langlingen"                             | false
        "5083"   | "Hohne bei Celle"                        | false
        "5084"   | "Hambühren"                              | false
        "5085"   | "Burgdorf Ehlershausen"                  | false
        "5086"   | "Celle Scheuen"                          | false
        "5101"   | "Pattensen"                              | false
        "5102"   | "Laatzen"                                | false
        "5103"   | "Wennigsen Deister"                      | false
        "5105"   | "Barsinghausen"                          | false
        "5108"   | "Gehrden Han"                            | false
        "5109"   | "Ronnenberg"                             | false
        "511"    | "Hannover"                               | false
        "5121"   | "Hildesheim"                             | false
        "5123"   | "Schellerten"                            | false
        "5126"   | "Algermissen"                            | false
        "5127"   | "Harsum"                                 | false
        "5128"   | "Hohenhameln"                            | false
        "5129"   | "Söhlde"                                 | false
        "5130"   | "Wedemark"                               | false
        "5131"   | "Garbsen"                                | false
        "5132"   | "Lehrte"                                 | false
        "5135"   | "Burgwedel Fuhrberg"                     | false
        "5136"   | "Burgdorf Kreis Hannover"                | false
        "5137"   | "Seelze"                                 | false
        "5138"   | "Sehnde"                                 | false
        "5139"   | "Burgwedel"                              | false
        "5141"   | "Celle"                                  | false
        "5142"   | "Eschede"                                | false
        "5143"   | "Winsen Aller"                           | false
        "5144"   | "Wathlingen"                             | false
        "5145"   | "Beedenbostel"                           | false
        "5146"   | "Wietze"                                 | false
        "5147"   | "Uetze Hänigsen"                         | false
        "5148"   | "Steinhorst Niedersachsen"               | false
        "5149"   | "Wienhausen"                             | false
        "5151"   | "Hameln"                                 | false
        "5152"   | "Hessisch Oldendorf"                     | false
        "5153"   | "Salzhemmendorf"                         | false
        "5154"   | "Aerzen"                                 | false
        "5155"   | "Emmerthal"                              | false
        "5156"   | "Coppenbrügge"                           | false
        "5157"   | "Emmerthal Börry"                        | false
        "5158"   | "Hemeringen"                             | false
        "5159"   | "Coppenbrügge Bisperode"                 | false
        "5161"   | "Walsrode"                               | false
        "5162"   | "Fallingbostel"                          | false
        "5163"   | "Fallingbostel Dorfmark"                 | false
        "5164"   | "Hodenhagen"                             | false
        "5165"   | "Rethem Aller"                           | false
        "5166"   | "Walsrode Kirchboitzen"                  | false
        "5167"   | "Walsrode Westenholz"                    | false
        "5168"   | "Walsrode Stellichte"                    | false
        "5171"   | "Peine"                                  | false
        "5172"   | "Ilsede"                                 | false
        "5173"   | "Uetze"                                  | false
        "5174"   | "Lahstedt"                               | false
        "5175"   | "Lehrte Arpke"                           | false
        "5176"   | "Edemissen"                              | false
        "5177"   | "Edemissen Abbensen"                     | false
        "5181"   | "Alfeld Leine"                           | false
        "5182"   | "Gronau Leine"                           | false
        "5183"   | "Lamspringe"                             | false
        "5184"   | "Freden Leine"                           | false
        "5185"   | "Duingen"                                | false
        "5186"   | "Salzhemmendorf Wallensen"               | false
        "5187"   | "Delligsen"                              | false
        "5190"   | "Soltau Emmingen"                        | false
        "5191"   | "Soltau"                                 | false
        "5192"   | "Munster"                                | false
        "5193"   | "Schneverdingen"                         | false
        "5194"   | "Bispingen"                              | false
        "5195"   | "Neuenkirchen bei Soltau"                | false
        "5196"   | "Wietzendorf"                            | false
        "5197"   | "Soltau Frielingen"                      | false
        "5198"   | "Schneverdingen Wintermoor"              | false
        "5199"   | "Schneverdingen Heber"                   | false
        "5201"   | "Halle Westfalen"                        | false
        "5202"   | "Oerlinghausen"                          | false
        "5203"   | "Werther Westfalen"                      | false
        "5204"   | "Steinhagen Westfalen"                   | false
        "5205"   | "Bielefeld Sennestadt"                   | false
        "5206"   | "Bielefeld Jöllenbeck"                   | false
        "5207"   | "Schloss Holte Stukenbrock"              | false
        "5208"   | "Leopoldshöhe"                           | false
        "5209"   | "Gütersloh Friedrichsdorf"               | false
        "521"    | "Bielefeld"                              | false
        "5221"   | "Herford"                                | false
        "5222"   | "Bad Salzuflen"                          | false
        "5223"   | "Bünde"                                  | false
        "5224"   | "Enger Westfalen"                        | false
        "5225"   | "Spenge"                                 | false
        "5226"   | "Bruchmühlen Westfalen"                  | false
        "5228"   | "Vlotho Exter"                           | false
        "5231"   | "Detmold"                                | false
        "5232"   | "Lage Lippe"                             | false
        "5233"   | "Steinheim Westfalen"                    | false
        "5234"   | "Horn Bad Meinberg"                      | false
        "5235"   | "Blomberg Lippe"                         | false
        "5236"   | "Blomberg Grossenmarpe"                  | false
        "5237"   | "Augustdorf"                             | false
        "5238"   | "Nieheim Himmighausen"                   | false
        "5241"   | "Gütersloh"                              | false
        "5242"   | "Rheda Wiedenbrück"                      | false
        "5244"   | "Rietberg"                               | false
        "5245"   | "Herzebrock Clarholz"                    | false
        "5246"   | "Verl"                                   | false
        "5247"   | "Harsewinkel"                            | false
        "5248"   | "Langenberg Kreis Gütersloh"             | false
        "5250"   | "Delbrück Westfalen"                     | false
        "5251"   | "Paderborn"                              | false
        "5252"   | "Bad Lippspringe"                        | false
        "5253"   | "Bad Driburg"                            | false
        "5254"   | "Paderborn Schloss Neuhaus"              | false
        "5255"   | "Altenbeken"                             | false
        "5257"   | "Hövelhof"                               | false
        "5258"   | "Salzkotten"                             | false
        "5259"   | "Bad Driburg Neuenheerse"                | false
        "5261"   | "Lemgo"                                  | false
        "5262"   | "Extertal"                               | false
        "5263"   | "Barntrup"                               | false
        "5264"   | "Kalletal"                               | false
        "5265"   | "Dörentrup"                              | false
        "5266"   | "Lemgo Kirchheide"                       | false
        "5271"   | "Höxter"                                 | false
        "5272"   | "Brakel Westfalen"                       | false
        "5273"   | "Beverungen"                             | false
        "5274"   | "Nieheim"                                | false
        "5275"   | "Höxter Ottbergen"                       | false
        "5276"   | "Marienmünster"                          | false
        "5277"   | "Höxter Fürstenau"                       | false
        "5278"   | "Höxter Ovenhausen"                      | false
        "5281"   | "Bad Pyrmont"                            | false
        "5282"   | "Schieder Schwalenberg"                  | false
        "5283"   | "Lügde Rischenau"                        | false
        "5284"   | "Schwalenberg"                           | false
        "5285"   | "Bad Pyrmont Kleinenberg"                | false
        "5286"   | "Ottenstein Niedersachsen"               | false
        "5292"   | "Lichtenau Atteln"                       | false
        "5293"   | "Paderborn Dahl"                         | false
        "5294"   | "Hövelhof Espeln"                        | false
        "5295"   | "Lichtenau Westfalen"                    | false
        "5300"   | "Salzgitter Üfingen"                     | false
        "5301"   | "Lehre Essenrode"                        | false
        "5302"   | "Vechelde"                               | false
        "5303"   | "Wendeburg"                              | false
        "5304"   | "Meine"                                  | false
        "5305"   | "Sickte"                                 | false
        "5306"   | "Cremlingen"                             | false
        "5307"   | "Braunschweig Wenden"                    | false
        "5308"   | "Lehre"                                  | false
        "5309"   | "Lehre Wendhausen"                       | false
        "531"    | "Braunschweig"                           | false
        "5320"   | "Torfhaus"                               | false
        "5321"   | "Goslar"                                 | false
        "5322"   | "Bad Harzburg"                           | false
        "5323"   | "Clausthal Zellerfeld"                   | false
        "5324"   | "Vienenburg"                             | false
        "5325"   | "Goslar Hahnenklee"                      | false
        "5326"   | "Langelsheim"                            | false
        "5327"   | "Bad Grund Harz"                         | false
        "5328"   | "Altenau Harz"                           | false
        "5329"   | "Schulenberg im Oberharz"                | false
        "5331"   | "Wolfenbüttel"                           | false
        "5332"   | "Schöppenstedt"                          | false
        "5333"   | "Dettum"                                 | false
        "5334"   | "Hornburg Kreis Wolfenbüttel"            | false
        "5335"   | "Schladen"                               | false
        "5336"   | "Semmenstedt"                            | false
        "5337"   | "Kissenbrück"                            | false
        "5339"   | "Gielde"                                 | false
        "5341"   | "Salzgitter"                             | false
        "5344"   | "Lengede"                                | false
        "5345"   | "Baddeckenstedt"                         | false
        "5346"   | "Liebenburg"                             | false
        "5347"   | "Burgdorf bei Salzgitter"                | false
        "5351"   | "Helmstedt"                              | false
        "5352"   | "Schöningen"                             | false
        "5353"   | "Königslutter am Elm"                    | false
        "5354"   | "Jerxheim"                               | false
        "5355"   | "Frellstedt"                             | false
        "5356"   | "Helmstedt Barmke"                       | false
        "5357"   | "Grasleben"                              | false
        "5358"   | "Bahrdorf Mackendorf"                    | false
        "5361"   | "Wolfsburg"                              | false
        "5362"   | "Wolfsburg Fallersleben"                 | false
        "5363"   | "Wolfsburg Vorsfelde"                    | false
        "5364"   | "Velpke"                                 | false
        "5365"   | "Wolfsburg Neindorf"                     | false
        "5366"   | "Jembke"                                 | false
        "5367"   | "Rühen"                                  | false
        "5368"   | "Parsau"                                 | false
        "5371"   | "Gifhorn"                                | false
        "5372"   | "Meinersen"                              | false
        "5373"   | "Hillerse Kreis Gifhorn"                 | false
        "5374"   | "Isenbüttel"                             | false
        "5375"   | "Müden Aller"                            | false
        "5376"   | "Wesendorf Kreis Gifhorn"                | false
        "5377"   | "Ehra Lessien"                           | false
        "5378"   | "Sassenburg Platendorf"                  | false
        "5379"   | "Sassenburg Grussendorf"                 | false
        "5381"   | "Seesen"                                 | false
        "5382"   | "Bad Gandersheim"                        | false
        "5383"   | "Lutter am Barenberge"                   | false
        "5384"   | "Seesen Groß Rhüden"                     | false
        "5401"   | "Georgsmarienhütte"                      | false
        "5402"   | "Bissendorf Kreis Osnabrück"             | false
        "5403"   | "Bad Iburg"                              | false
        "5404"   | "Westerkappeln"                          | false
        "5405"   | "Hasbergen Kreis Osnabrück"              | false
        "5406"   | "Belm"                                   | false
        "5407"   | "Wallenhorst"                            | false
        "5409"   | "Hilter am Teutoburger Wald"             | false
        "541"    | "Osnabrück"                              | false
        "5421"   | "Dissen am Teutoburger Wald"             | false
        "5422"   | "Melle"                                  | false
        "5423"   | "Versmold"                               | false
        "5424"   | "Bad Rothenfelde"                        | false
        "5425"   | "Borgholzhausen"                         | false
        "5426"   | "Glandorf"                               | false
        "5427"   | "Melle Buer"                             | false
        "5428"   | "Melle Neuenkirchen"                     | false
        "5429"   | "Melle Wellingholzhausen"                | false
        "5431"   | "Quakenbrück"                            | false
        "5432"   | "Löningen"                               | false
        "5433"   | "Badbergen"                              | false
        "5434"   | "Essen Oldenburg"                        | false
        "5435"   | "Berge bei Quakenbrück"                  | false
        "5436"   | "Nortrup"                                | false
        "5437"   | "Menslage"                               | false
        "5438"   | "Bakum Lüsche"                           | false
        "5439"   | "Bersenbrück"                            | false
        "5441"   | "Diepholz"                               | false
        "5442"   | "Barnstorf Kreis Diepholz"               | false
        "5443"   | "Lemförde"                               | false
        "5444"   | "Wagenfeld"                              | false
        "5445"   | "Drebber"                                | false
        "5446"   | "Rehden"                                 | false
        "5447"   | "Lembruch"                               | false
        "5448"   | "Barver"                                 | false
        "5451"   | "Ibbenbüren"                             | false
        "5452"   | "Mettingen Westfalen"                    | false
        "5453"   | "Recke"                                  | false
        "5454"   | "Hörstel Riesenbeck"                     | false
        "5455"   | "Tecklenburg Brochterbeck"               | false
        "5456"   | "Westerkappeln Velpe"                    | false
        "5457"   | "Hopsten Schale"                         | false
        "5458"   | "Hopsten"                                | false
        "5459"   | "Hörstel"                                | false
        "5461"   | "Bramsche Hase"                          | false
        "5462"   | "Ankum"                                  | false
        "5464"   | "Alfhausen"                              | false
        "5465"   | "Neuenkirchen bei Bramsche"              | false
        "5466"   | "Merzen"                                 | false
        "5467"   | "Voltlage"                               | false
        "5468"   | "Bramsche Engter"                        | false
        "5471"   | "Bohmte"                                 | false
        "5472"   | "Bad Essen"                              | false
        "5473"   | "Ostercappeln"                           | false
        "5474"   | "Stemwede Dielingen"                     | false
        "5475"   | "Bohmte Hunteburg"                       | false
        "5476"   | "Ostercappeln Venne"                     | false
        "5481"   | "Lengerich Westfalen"                    | false
        "5482"   | "Tecklenburg"                            | false
        "5483"   | "Lienen"                                 | false
        "5484"   | "Lienen Kattenvenne"                     | false
        "5485"   | "Ladbergen"                              | false
        "5491"   | "Damme Dümmer"                           | false
        "5492"   | "Steinfeld Oldenburg"                    | false
        "5493"   | "Neuenkirchen Kreis Vechta"              | false
        "5494"   | "Holdorf Niedersachsen"                  | false
        "5495"   | "Vörden Kreis Vechta"                    | false
        "5502"   | "Dransfeld"                              | false
        "5503"   | "Nörten Hardenberg"                      | false
        "5504"   | "Friedland Kreis Göttingen"              | false
        "5505"   | "Hardegsen"                              | false
        "5506"   | "Adelebsen"                              | false
        "5507"   | "Ebergötzen"                             | false
        "5508"   | "Gleichen Rittmarshausen"                | false
        "5509"   | "Rosdorf Kreis Göttingen"                | false
        "551"    | "Göttingen"                              | false
        "5520"   | "Braunlage"                              | false
        "5521"   | "Herzberg am Harz"                       | false
        "5522"   | "Osterode am Harz"                       | false
        "5523"   | "Bad Sachsa"                             | false
        "5524"   | "Bad Lauterberg im Harz"                 | false
        "5525"   | "Walkenried"                             | false
        "5527"   | "Duderstadt"                             | false
        "5528"   | "Gieboldehausen"                         | false
        "5529"   | "Rhumspringe"                            | false
        "5531"   | "Holzminden"                             | false
        "5532"   | "Stadtoldendorf"                         | false
        "5533"   | "Bodenwerder"                            | false
        "5534"   | "Eschershausen an der Lenne"             | false
        "5535"   | "Polle"                                  | false
        "5536"   | "Holzminden Neuhaus"                     | false
        "5541"   | "Hannoversch Münden"                     | true   // see https://issuetracker.google.com/issues/183383466
        "5542"   | "Witzenhausen"                           | false
        "5543"   | "Staufenberg Niedersachsen"              | false
        "5544"   | "Reinhardshagen"                         | false
        "5545"   | "Hedemünden"                             | false
        "5546"   | "Scheden"                                | false
        "5551"   | "Northeim"                               | false
        "5552"   | "Katlenburg"                             | false
        "5553"   | "Kalefeld"                               | false
        "5554"   | "Moringen"                               | false
        "5555"   | "Moringen Fredelsloh"                    | false
        "5556"   | "Lindau Harz"                            | false
        "5561"   | "Einbeck"                                | false
        "5562"   | "Dassel Markoldendorf"                   | false
        "5563"   | "Kreiensen"                              | false
        "5564"   | "Dassel"                                 | false
        "5565"   | "Einbeck Wenzen"                         | false
        "5571"   | "Uslar"                                  | false
        "5572"   | "Bodenfelde"                             | false
        "5573"   | "Uslar Volpriehausen"                    | false
        "5574"   | "Oberweser"                              | false
        "5582"   | "Sankt Andreasberg"                      | false
        "5583"   | "Braunlage Hohegeiss"                    | false
        "5584"   | "Hattorf am Harz"                        | false
        "5585"   | "Herzberg Sieber"                        | false
        "5586"   | "Wieda"                                  | false
        "5592"   | "Gleichen Bremke"                        | false
        "5593"   | "Bovenden Lenglern"                      | false
        "5594"   | "Bovenden Reyershausen"                  | false
        "5601"   | "Schauenburg"                            | false
        "5602"   | "Hessisch Lichtenau"                     | false
        "5603"   | "Gudensberg"                             | false
        "5604"   | "Grossalmerode"                          | false
        "5605"   | "Kaufungen Hessen"                       | false
        "5606"   | "Zierenberg"                             | false
        "5607"   | "Fuldatal"                               | false
        "5608"   | "Söhrewald"                              | false
        "5609"   | "Ahnatal"                                | false
        "561"    | "Kassel"                                 | false
        "5621"   | "Bad Wildungen"                          | false
        "5622"   | "Fritzlar"                               | false
        "5623"   | "Edertal"                                | false
        "5624"   | "Bad Emstal"                             | false
        "5625"   | "Naumburg Hessen"                        | false
        "5626"   | "Bad Zwesten"                            | false
        "5631"   | "Korbach"                                | false
        "5632"   | "Willingen Upland"                       | false
        "5633"   | "Diemelsee"                              | false
        "5634"   | "Waldeck Sachsenhausen"                  | false
        "5635"   | "Vöhl"                                   | false
        "5636"   | "Lichtenfels Goddelsheim"                | false
        "5641"   | "Warburg"                                | false
        "5642"   | "Warburg Scherfede"                      | false
        "5643"   | "Borgentreich"                           | false
        "5644"   | "Willebadessen Peckelsheim"              | false
        "5645"   | "Borgentreich Borgholz"                  | false
        "5646"   | "Willebadessen"                          | false
        "5647"   | "Lichtenau Kleinenberg"                  | false
        "5648"   | "Brakel Gehrden"                         | false
        "5650"   | "Cornberg"                               | false
        "5651"   | "Eschwege"                               | false
        "5652"   | "Bad Sooden Allendorf"                   | false
        "5653"   | "Sontra"                                 | false
        "5654"   | "Herleshausen"                           | false
        "5655"   | "Wanfried"                               | false
        "5656"   | "Waldkappel"                             | false
        "5657"   | "Meissner"                               | false
        "5658"   | "Wehretal"                               | false
        "5659"   | "Ringgau"                                | false
        "5661"   | "Melsungen"                              | false
        "5662"   | "Felsberg Hessen"                        | false
        "5663"   | "Spangenberg"                            | false
        "5664"   | "Morschen"                               | false
        "5665"   | "Guxhagen"                               | false
        "5671"   | "Hofgeismar"                             | false
        "5672"   | "Bad Karlshafen"                         | false
        "5673"   | "Immenhausen Hessen"                     | false
        "5674"   | "Grebenstein"                            | false
        "5675"   | "Trendelburg"                            | false
        "5676"   | "Liebenau Hessen"                        | false
        "5677"   | "Calden Westuffeln"                      | false
        "5681"   | "Homberg Efze"                           | false
        "5682"   | "Borken Hessen"                          | false
        "5683"   | "Wabern Hessen"                          | false
        "5684"   | "Frielendorf"                            | false
        "5685"   | "Knüllwald"                              | false
        "5686"   | "Schwarzenborn Knüll"                    | false
        "5691"   | "Bad Arolsen"                            | false
        "5692"   | "Wolfhagen"                              | false
        "5693"   | "Volkmarsen"                             | false
        "5694"   | "Diemelstadt"                            | false
        "5695"   | "Twistetal"                              | false
        "5696"   | "Bad Arolsen Landau"                     | false
        "5702"   | "Petershagen Lahde"                      | false
        "5703"   | "Hille"                                  | false
        "5704"   | "Petershagen Friedewalde"                | false
        "5705"   | "Petershagen Windheim"                   | false
        "5706"   | "Porta Westfalica"                       | false
        "5707"   | "Petershagen Weser"                      | false
        "571"    | "Minden Westfalen"                       | false
        "5721"   | "Stadthagen"                             | false
        "5722"   | "Bückeburg"                              | false
        "5723"   | "Bad Nenndorf"                           | false
        "5724"   | "Obernkirchen"                           | false
        "5725"   | "Lindhorst bei Stadthagen"               | false
        "5726"   | "Wiedensahl"                             | false
        "5731"   | "Bad Oeynhausen"                         | false
        "5732"   | "Löhne"                                  | false
        "5733"   | "Vlotho"                                 | false
        "5734"   | "Bergkirchen Westfalen"                  | false
        "5741"   | "Lübbecke"                               | false
        "5742"   | "Preussisch Oldendorf"                   | false
        "5743"   | "Espelkamp Gestringen"                   | false
        "5744"   | "Hüllhorst"                              | false
        "5745"   | "Stemwede Levern"                        | false
        "5746"   | "Rödinghausen"                           | false
        "5751"   | "Rinteln"                                | false
        "5752"   | "Auetal Hattendorf"                      | false
        "5753"   | "Auetal Bernsen"                         | false
        "5754"   | "Extertal Bremke"                        | false
        "5755"   | "Kalletal Varenholz"                     | false
        "5761"   | "Stolzenau"                              | false
        "5763"   | "Uchte"                                  | false
        "5764"   | "Steyerberg"                             | false
        "5765"   | "Raddestorf"                             | false
        "5766"   | "Rehburg Loccum"                         | false
        "5767"   | "Warmsen"                                | false
        "5768"   | "Petershagen Heimsen"                    | false
        "5769"   | "Steyerberg Voigtei"                     | false
        "5771"   | "Rahden Westfalen"                       | false
        "5772"   | "Espelkamp"                              | false
        "5773"   | "Stemwede Wehdem"                        | false
        "5774"   | "Wagenfeld Ströhen"                      | false
        "5775"   | "Diepenau"                               | false
        "5776"   | "Preussisch Ströhen"                     | false
        "5777"   | "Diepenau Essern"                        | false
        "5802"   | "Wrestedt"                               | false
        "5803"   | "Rosche"                                 | false
        "5804"   | "Rätzlingen Kreis Uelzen"                | false
        "5805"   | "Oetzen"                                 | false
        "5806"   | "Barum bei Bad Bevensen"                 | false
        "5807"   | "Altenmedingen"                          | false
        "5808"   | "Gerdau"                                 | false
        "581"    | "Uelzen"                                 | false
        "5820"   | "Suhlendorf"                             | false
        "5821"   | "Bad Bevensen"                           | false
        "5822"   | "Ebstorf"                                | false
        "5823"   | "Bienenbüttel"                           | false
        "5824"   | "Bad Bodenteich"                         | false
        "5825"   | "Wieren"                                 | false
        "5826"   | "Suderburg"                              | false
        "5827"   | "Unterlüß"                               | false
        "5828"   | "Himbergen"                              | false
        "5829"   | "Wriedel"                                | false
        "5831"   | "Wittingen"                              | false
        "5832"   | "Hankensbüttel"                          | false
        "5833"   | "Brome"                                  | false
        "5834"   | "Wittingen Knesebeck"                    | false
        "5835"   | "Wahrenholz"                             | false
        "5836"   | "Wittingen Radenbeck"                    | false
        "5837"   | "Sprakensehl"                            | false
        "5838"   | "Gross Oesingen"                         | false
        "5839"   | "Wittingen Ohrdorf"                      | false
        "5840"   | "Schnackenburg"                          | false
        "5841"   | "Lüchow Wendland"                        | false
        "5842"   | "Schnega"                                | false
        "5843"   | "Wustrow Wendland"                       | false
        "5844"   | "Clenze"                                 | false
        "5845"   | "Bergen Dumme"                           | false
        "5846"   | "Gartow Niedersachsen"                   | false
        "5848"   | "Trebel"                                 | false
        "5849"   | "Waddeweitz"                             | false
        "5850"   | "Neetze"                                 | false
        "5851"   | "Dahlenburg"                             | false
        "5852"   | "Bleckede"                               | false
        "5853"   | "Neu Darchau"                            | false
        "5854"   | "Bleckede Barskamp"                      | false
        "5855"   | "Nahrendorf"                             | false
        "5857"   | "Bleckede Brackede"                      | false
        "5858"   | "Hitzacker Wietzetze"                    | false
        "5859"   | "Thomasburg"                             | false
        "5861"   | "Dannenberg Elbe"                        | false
        "5862"   | "Hitzacker Elbe"                         | false
        "5863"   | "Zernien"                                | false
        "5864"   | "Jameln"                                 | false
        "5865"   | "Gusborn"                                | false
        "5872"   | "Stoetze"                                | false
        "5873"   | "Eimke"                                  | false
        "5874"   | "Soltendieck"                            | false
        "5875"   | "Emmendorf"                              | false
        "5882"   | "Gorleben"                               | false
        "5883"   | "Lemgow"                                 | false
        "5901"   | "Fürstenau bei Bramsche"                 | false
        "5902"   | "Freren"                                 | false
        "5903"   | "Emsbüren"                               | false
        "5904"   | "Lengerich Emsl"                         | false
        "5905"   | "Beesten"                                | false
        "5906"   | "Lünne"                                  | false
        "5907"   | "Geeste"                                 | false
        "5908"   | "Wietmarschen Lohne"                     | false
        "5909"   | "Wettrup"                                | false
        "591"    | "Lingen Ems"                             | true   // see https://issuetracker.google.com/issues/183383466
        "5921"   | "Nordhorn"                               | false
        "5922"   | "Bad Bentheim"                           | false
        "5923"   | "Schüttorf"                              | false
        "5924"   | "Bad Bentheim Gildehaus"                 | false
        "5925"   | "Wietmarschen"                           | false
        "5926"   | "Engden"                                 | false
        "5931"   | "Meppen"                                 | false
        "5932"   | "Haren Ems"                              | false
        "5933"   | "Lathen"                                 | false
        "5934"   | "Haren Rütenbrock"                       | false
        "5935"   | "Twist Schöninghsdorf"                   | false
        "5936"   | "Twist"                                  | false
        "5937"   | "Geeste Gross Hesepe"                    | false
        "5939"   | "Sustrum"                                | false
        "5941"   | "Neuenhaus Dinkel"                       | false
        "5942"   | "Uelsen"                                 | false
        "5943"   | "Emlichheim"                             | false
        "5944"   | "Hoogstede"                              | false
        "5945"   | "Wilsum"                                 | false
        "5946"   | "Georgsdorf"                             | false
        "5947"   | "Laar Vechte"                            | false
        "5948"   | "Itterbeck"                              | false
        "5951"   | "Werlte"                                 | false
        "5952"   | "Sögel"                                  | false
        "5953"   | "Börger"                                 | false
        "5954"   | "Lorup"                                  | false
        "5955"   | "Esterwegen"                             | false
        "5956"   | "Rastdorf"                               | false
        "5957"   | "Lindern Oldenburg"                      | false
        "5961"   | "Haselünne"                              | false
        "5962"   | "Herzlake"                               | false
        "5963"   | "Bawinkel"                               | false
        "5964"   | "Lähden"                                 | false
        "5965"   | "Klein Berssen"                          | false
        "5966"   | "Meppen Apeldorn"                        | false
        "5971"   | "Rheine"                                 | false
        "5973"   | "Neuenkirchen Kreis Steinfurt"           | false
        "5975"   | "Rheine Mesum"                           | false
        "5976"   | "Salzbergen"                             | false
        "5977"   | "Spelle"                                 | false
        "5978"   | "Hörstel Dreierwalde"                    | false
        "6002"   | "Ober Mörlen"                            | false
        "6003"   | "Rosbach von der Höhe"                   | false
        "6004"   | "Lich Eberstadt"                         | false
        "6007"   | "Rosbach Rodheim"                        | false
        "6008"   | "Echzell"                                | false
        "6020"   | "Heigenbrücken"                          | false
        "6021"   | "Aschaffenburg"                          | false
        "6022"   | "Obernburg am Main"                      | false
        "6023"   | "Alzenau in Unterfranken"                | false
        "6024"   | "Schöllkrippen"                          | false
        "6026"   | "Grossostheim"                           | false
        "6027"   | "Stockstadt am Main"                     | false
        "6028"   | "Sulzbach am Main"                       | false
        "6029"   | "Mömbris"                                | false
        "6031"   | "Friedberg Hessen"                       | false
        "6032"   | "Bad Nauheim"                            | false
        "6033"   | "Butzbach"                               | false
        "6034"   | "Wöllstadt"                              | false
        "6035"   | "Reichelsheim Wetterau"                  | false
        "6036"   | "Wölfersheim"                            | false
        "6039"   | "Karben"                                 | false
        "6041"   | "Glauburg"                               | false
        "6042"   | "Büdingen Hessen"                        | false
        "6043"   | "Nidda"                                  | false
        "6044"   | "Schotten Hessen"                        | false
        "6045"   | "Gedern"                                 | false
        "6046"   | "Ortenberg Hessen"                       | false
        "6047"   | "Altenstadt Hessen"                      | false
        "6048"   | "Büdingen Eckartshausen"                 | false
        "6049"   | "Kefenrod"                               | false
        "6050"   | "Biebergemünd"                           | false
        "6051"   | "Gelnhausen"                             | false
        "6052"   | "Bad Orb"                                | false
        "6053"   | "Wächtersbach"                           | false
        "6054"   | "Birstein"                               | false
        "6055"   | "Freigericht"                            | false
        "6056"   | "Bad Soden Salmünster"                   | false
        "6057"   | "Flörsbachtal"                           | false
        "6058"   | "Gründau"                                | false
        "6059"   | "Jossgrund"                              | false
        "6061"   | "Michelstadt"                            | false
        "6062"   | "Erbach Odenwald"                        | false
        "6063"   | "Bad König"                              | false
        "6066"   | "Michelstadt Vielbrunn"                  | false
        "6068"   | "Beerfelden"                             | false
        "6071"   | "Dieburg"                                | false
        "6073"   | "Babenhausen Hessen"                     | false
        "6074"   | "Rödermark"                              | false
        "6078"   | "Gross Umstadt"                          | false
        "6081"   | "Usingen"                                | false
        "6082"   | "Niederreifenberg"                       | false
        "6083"   | "Weilrod"                                | false
        "6084"   | "Schmitten Taunus"                       | false
        "6085"   | "Waldsolms"                              | false
        "6086"   | "Grävenwiesbach"                         | false
        "6087"   | "Waldems"                                | false
        "6092"   | "Heimbuchenthal"                         | false
        "6093"   | "Laufach"                                | false
        "6094"   | "Weibersbrunn"                           | false
        "6095"   | "Bessenbach"                             | false
        "6096"   | "Wiesen Unterfranken"                    | false
        "6101"   | "Bad Vilbel"                             | false
        "6102"   | "Neu Isenburg"                           | false
        "6103"   | "Langen Hessen"                          | false
        "6104"   | "Heusenstamm"                            | false
        "6105"   | "Mörfelden Walldorf"                     | false
        "6106"   | "Rodgau"                                 | false
        "6107"   | "Kelsterbach"                            | false
        "6108"   | "Mühlheim am Main"                       | false
        "6109"   | "Frankfurt Bergen Enkheim"               | false
        "611"    | "Wiesbaden"                              | false
        "6120"   | "Aarbergen"                              | false
        "6122"   | "Hofheim Wallau"                         | false
        "6123"   | "Eltville am Rhein"                      | false
        "6124"   | "Bad Schwalbach"                         | false
        "6126"   | "Idstein"                                | false
        "6127"   | "Niedernhausen Taunus"                   | false
        "6128"   | "Taunusstein"                            | false
        "6129"   | "Schlangenbad"                           | false
        "6130"   | "Schwabenheim an der Selz"               | false
        "6131"   | "Mainz"                                  | false
        "6132"   | "Ingelheim am Rhein"                     | false
        "6133"   | "Oppenheim"                              | false
        "6134"   | "Mainz Kastel"                           | false
        "6135"   | "Bodenheim am Rhein"                     | true  // missed in https://issuetracker.google.com/issues/183383466
        "6136"   | "Nieder Olm"                             | false
        "6138"   | "Mommenheim"                             | false
        "6139"   | "Budenheim"                              | false
        "6142"   | "Rüsselsheim"                            | false
        "6144"   | "Bischofsheim bei Rüsselsheim"           | false
        "6145"   | "Flörsheim am Main"                      | false
        "6146"   | "Hochheim am Main"                       | false
        "6147"   | "Trebur"                                 | false
        "6150"   | "Weiterstadt"                            | false
        "6151"   | "Darmstadt"                              | false
        "6152"   | "Gross Gerau"                            | false
        "6154"   | "Ober Ramstadt"                          | false
        "6155"   | "Griesheim Hessen"                       | false
        "6157"   | "Pfungstadt"                             | false
        "6158"   | "Riedstadt"                              | false
        "6159"   | "Messel"                                 | false
        "6161"   | "Brensbach"                              | false
        "6162"   | "Reinheim Odenwald"                      | false
        "6163"   | "Höchst im Odenwald"                     | false
        "6164"   | "Reichelsheim Odenwald"                  | false
        "6165"   | "Breuberg"                               | false
        "6166"   | "Fischbachtal"                           | false
        "6167"   | "Modautal"                               | false
        "6171"   | "Oberursel Taunus"                       | false
        "6172"   | "Bad Homburg von der Höhe"               | false
        "6173"   | "Kronberg im Taunus"                     | false
        "6174"   | "Königstein im Taunus"                   | false
        "6175"   | "Friedrichsdorf Taunus"                  | false
        "6181"   | "Hanau"                                  | false
        "6182"   | "Seligenstadt"                           | false
        "6183"   | "Erlensee"                               | false
        "6184"   | "Langenselbold"                          | false
        "6185"   | "Hammersbach Hessen"                     | false
        "6186"   | "Grosskrotzenburg"                       | false
        "6187"   | "Schöneck"                               | false
        "6188"   | "Kahl am Main"                           | false
        "6190"   | "Hattersheim am Main"                    | false
        "6192"   | "Hofheim am Taunus"                      | false
        "6195"   | "Kelkheim Taunus"                        | false
        "6196"   | "Bad Soden am Taunus"                    | false
        "6198"   | "Eppstein"                               | false
        "6201"   | "Weinheim Bergstr"                       | false
        "6202"   | "Schwetzingen"                           | false
        "6203"   | "Ladenburg"                              | false
        "6204"   | "Viernheim"                              | false
        "6205"   | "Hockenheim"                             | false
        "6206"   | "Lampertheim"                            | false
        "6207"   | "Wald Michelbach"                        | false
        "6209"   | "Mörlenbach"                             | false
        // 621 extended 0..4, 7&8 because number with starting 5 is added
        "6210"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "6211"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "6212"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "6213"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "6214"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        // note of BnetzA, that starting number is separating both cities: see https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONVerzeichnisse/ONBVerzeichnis/Sonderregelungen0212_0621.pdf?__blob=publicationFile&v=1
        "6215"   | "Ludwigshafen"                           | false  // see https://issuetracker.google.com/issues/338710341
        "6216"   | "Ludwigshafen"                           | false  // see https://issuetracker.google.com/issues/338710341
        "6217"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "6218"   | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62190"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62191"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62192"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62193"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62194"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62195"  | "Ludwigshafen"                           | false  // see https://issuetracker.google.com/issues/338710341
        "62196"  | "Ludwigshafen"                           | false  // see https://issuetracker.google.com/issues/338710341
        "62197"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62198"  | "Mannheim"                               | false  // main BnetzA number plan - but shared with Ludwigshafen
        "62199"  | "Ludwigshafen"                           | false  // see https://issuetracker.google.com/issues/338710341
        "6220"   | "Wilhelmsfeld"                           | false
        "6221"   | "Heidelberg"                             | false
        "6222"   | "Wiesloch"                               | false
        "6223"   | "Neckargemünd"                           | false
        "6224"   | "Sandhausen Baden"                       | false
        "6226"   | "Meckesheim"                             | false
        "6227"   | "Walldorf Baden"                         | false
        "6228"   | "Schönau Odenwald"                       | false
        "6229"   | "Neckarsteinach"                         | false
        "6231"   | "Hochdorf Assenheim"                     | false
        "6232"   | "Speyer"                                 | false
        "6233"   | "Frankenthal Pfalz"                      | false
        "6234"   | "Mutterstadt"                            | false
        "6235"   | "Schifferstadt"                          | false
        "6236"   | "Neuhofen Pfalz"                         | false
        "6237"   | "Maxdorf"                                | false
        "6238"   | "Dirmstein"                              | false
        "6239"   | "Bobenheim Roxheim"                      | false
        "6241"   | "Worms"                                  | false
        "6242"   | "Osthofen"                               | false
        "6243"   | "Monsheim"                               | false
        "6244"   | "Westhofen Rheinhessen"                  | true   // see https://issuetracker.google.com/issues/183383466
        "6245"   | "Biblis"                                 | false
        "6246"   | "Eich Rheinhessen"                       | false
        "6247"   | "Worms Pfeddersheim"                     | false
        "6249"   | "Guntersblum"                            | false
        "6251"   | "Bensheim"                               | false
        "6252"   | "Heppenheim Bergstraße"                  | false
        "6253"   | "Fürth Odenwald"                         | false
        "6254"   | "Lautertal Odenwald"                     | false
        "6255"   | "Lindenfels"                             | false
        "6256"   | "Lampertheim Hüttenfeld"                 | false
        "6257"   | "Seeheim Jugenheim"                      | false
        "6258"   | "Gernsheim"                              | false
        "6261"   | "Mosbach Baden"                          | false
        "6262"   | "Aglasterhausen"                         | false
        "6263"   | "Neckargerach"                           | false
        "6264"   | "Neudenau"                               | false
        "6265"   | "Billigheim Baden"                       | false
        "6266"   | "Hassmersheim"                           | false
        "6267"   | "Fahrenbach Baden"                       | false
        "6268"   | "Hüffenhardt"                            | false
        "6269"   | "Gundelsheim Württemberg"                | false
        "6271"   | "Eberbach Baden"                         | false
        "6272"   | "Hirschhorn Neckar"                      | false
        "6274"   | "Waldbrunn Odenwald"                     | false
        "6275"   | "Rothenberg Odenwald"                    | false
        "6276"   | "Hesseneck"                              | false
        "6281"   | "Buchen Odenwald"                        | false
        "6282"   | "Walldürn"                               | false
        "6283"   | "Hardheim Odenwald"                      | false
        "6284"   | "Mudau"                                  | false
        "6285"   | "Walldürn Altheim"                       | false
        "6286"   | "Walldürn Rippberg"                      | false
        "6287"   | "Limbach Baden"                          | false
        "6291"   | "Adelsheim"                              | false
        "6292"   | "Seckach"                                | false
        "6293"   | "Schefflenz"                             | false
        "6294"   | "Krautheim Jagst"                        | false
        "6295"   | "Rosenberg Baden"                        | false
        "6296"   | "Ahorn Baden"                            | false
        "6297"   | "Ravenstein Baden"                       | false
        "6298"   | "Möckmühl"                               | false
        "6301"   | "Otterbach Pfalz"                        | false
        "6302"   | "Winnweiler"                             | false
        "6303"   | "Enkenbach Alsenborn"                    | false
        "6304"   | "Wolfstein Pfalz"                        | false
        "6305"   | "Hochspeyer"                             | false
        "6306"   | "Trippstadt"                             | false
        "6307"   | "Schopp"                                 | false
        "6308"   | "Olsbrücken"                             | false
        "631"    | "Kaiserslautern"                         | false
        "6321"   | "Neustadt an der Weinstraße"             | false
        "6322"   | "Bad Dürkheim"                           | false
        "6323"   | "Edenkoben"                              | false
        "6324"   | "Hassloch"                               | false
        "6325"   | "Lambrecht Pfalz"                        | false
        "6326"   | "Deidesheim"                             | false
        "6327"   | "Neustadt Lachen"                        | false
        "6328"   | "Elmstein"                               | false
        "6329"   | "Weidenthal Pfalz"                       | false
        "6331"   | "Pirmasens"                              | false
        "6332"   | "Zweibrücken"                            | false
        "6333"   | "Waldfischbach Burgalben"                | false
        "6334"   | "Thaleischweiler Fröschen"               | false
        "6335"   | "Trulben"                                | false
        "6336"   | "Dellfeld"                               | false
        "6337"   | "Grossbundenbach"                        | false
        "6338"   | "Hornbach Pfalz"                         | false
        "6339"   | "Grosssteinhausen"                       | false
        "6340"   | "Wörth Schaidt"                          | false
        "6341"   | "Landau in der Pfalz"                    | false
        "6342"   | "Schweigen Rechtenbach"                  | false
        "6343"   | "Bad Bergzabern"                         | false
        "6344"   | "Schwegenheim"                           | false
        "6345"   | "Albersweiler"                           | false
        "6346"   | "Annweiler am Trifels"                   | false
        "6347"   | "Hochstadt Pfalz"                        | false
        "6348"   | "Offenbach an der Queich"                | false
        "6349"   | "Billigheim Ingenheim"                   | false
        "6351"   | "Eisenberg Pfalz"                        | false
        "6352"   | "Kirchheimbolanden"                      | false
        "6353"   | "Freinsheim"                             | false
        "6355"   | "Albisheim Pfrimm"                       | false
        "6356"   | "Carlsberg Pfalz"                        | false
        "6357"   | "Standenbühl"                            | false
        "6358"   | "Kriegsfeld"                             | false
        "6359"   | "Grünstadt"                              | false
        "6361"   | "Rockenhausen"                           | false
        "6362"   | "Alsenz"                                 | false
        "6363"   | "Niederkirchen"                          | false
        "6364"   | "Nußbach Pfalz"                          | false
        "6371"   | "Landstuhl"                              | false
        "6372"   | "Bruchmühlbach Miesau"                   | false
        "6373"   | "Schönenberg Kübelberg"                  | false
        "6374"   | "Weilerbach"                             | false
        "6375"   | "Wallhalben"                             | false
        "6381"   | "Kusel"                                  | false
        "6382"   | "Lauterecken"                            | false
        "6383"   | "Glan Münchweiler"                       | false
        "6384"   | "Konken"                                 | false
        "6385"   | "Reichenbach Steegen"                    | false
        "6386"   | "Altenkirchen Pfalz"                     | false
        "6387"   | "Sankt Julian"                           | false
        "6391"   | "Dahn"                                   | false
        "6392"   | "Hauenstein Pfalz"                       | false
        "6393"   | "Fischbach bei Dahn"                     | false
        "6394"   | "Bundenthal"                             | false
        "6395"   | "Münchweiler an der Rodalb"              | false
        "6396"   | "Hinterweidenthal"                       | false
        "6397"   | "Leimen Pfalz"                           | false
        "6398"   | "Vorderweidenthal"                       | false
        "6400"   | "Mücke"                                  | false
        "6401"   | "Grünberg Hessen"                        | false
        "6402"   | "Hungen"                                 | false
        "6403"   | "Linden Hessen"                          | false
        "6404"   | "Lich Hessen"                            | false
        "6405"   | "Laubach Hessen"                         | false
        "6406"   | "Lollar"                                 | false
        "6407"   | "Rabenau Hessen"                         | false
        "6408"   | "Buseck"                                 | false
        "6409"   | "Biebertal"                              | false
        "641"    | "Giessen"                                | false
        "6420"   | "Lahntal"                                | false
        "6421"   | "Marburg"                                | false
        "6422"   | "Kirchhain"                              | false
        "6423"   | "Wetter Hessen"                          | false
        "6424"   | "Ebsdorfergrund"                         | false
        "6425"   | "Rauschenberg Hessen"                    | false
        "6426"   | "Fronhausen"                             | false
        "6427"   | "Cölbe Schönstadt"                       | false
        "6428"   | "Stadtallendorf"                         | false
        "6429"   | "Schweinsberg Hessen"                    | false
        "6430"   | "Hahnstätten"                            | false
        "6431"   | "Limburg an der Lahn"                    | false
        "6432"   | "Diez"                                   | false
        "6433"   | "Hadamar"                                | false
        "6434"   | "Bad Camberg"                            | false
        "6435"   | "Wallmerod"                              | false
        "6436"   | "Dornburg Hessen"                        | false
        "6438"   | "Hünfelden"                              | false
        "6439"   | "Holzappel"                              | false
        "6440"   | "Kölschhausen"                           | false
        "6441"   | "Wetzlar"                                | false
        "6442"   | "Braunfels"                              | false
        "6443"   | "Ehringshausen Dill"                     | false
        "6444"   | "Bischoffen"                             | false
        "6445"   | "Schöffengrund"                          | false
        "6446"   | "Hohenahr"                               | false
        "6447"   | "Langgöns Niederkleen"                   | false
        "6449"   | "Ehringshausen Katzenfurt"               | false
        "6451"   | "Frankenberg Eder"                       | false
        "6452"   | "Battenberg Eder"                        | false
        "6453"   | "Gemünden Wohra"                         | false
        "6454"   | "Lichtenfels Sachsenberg"                | false
        "6455"   | "Frankenau Hessen"                       | false
        "6456"   | "Haina Kloster"                          | false
        "6457"   | "Burgwald Eder"                          | false
        "6458"   | "Rosenthal Hessen"                       | false
        "6461"   | "Biedenkopf"                             | false
        "6462"   | "Gladenbach"                             | false
        "6464"   | "Angelburg"                              | false
        "6465"   | "Breidenbach bei Biedenkopf"             | false
        "6466"   | "Dautphetal Friedensdorf"                | false
        "6467"   | "Hatzfeld Eder"                          | false
        "6468"   | "Dautphetal Mornshausen"                 | false
        "6471"   | "Weilburg"                               | false
        "6472"   | "Weilmünster"                            | false
        "6473"   | "Leun"                                   | false
        "6474"   | "Villmar Aumenau"                        | false
        "6475"   | "Weilmünster Wolfenhausen"               | false
        "6476"   | "Mengerskirchen"                         | false
        "6477"   | "Greifenstein Nenderoth"                 | false
        "6478"   | "Greifenstein Ulm"                       | false
        "6479"   | "Waldbrunn Westerwald"                   | false
        "6482"   | "Runkel"                                 | false
        "6483"   | "Selters Taunus"                         | false
        "6484"   | "Beselich"                               | false
        "6485"   | "Nentershausen Westerwald"               | false
        "6486"   | "Katzenelnbogen"                         | false
        "6500"   | "Waldrach"                               | false
        "6501"   | "Konz"                                   | false
        "6502"   | "Schweich"                               | false
        "6503"   | "Hermeskeil"                             | false
        "6504"   | "Thalfang"                               | false
        "6505"   | "Kordel"                                 | false
        "6506"   | "Welschbillig"                           | false
        "6507"   | "Neumagen Dhron"                         | false
        "6508"   | "Hetzerath Mosel"                        | false
        "6509"   | "Büdlich"                                | false
        "651"    | "Trier"                                  | false
        "6522"   | "Mettendorf"                             | false
        "6523"   | "Holsthum"                               | false
        "6524"   | "Rodershausen"                           | false
        "6525"   | "Irrel"                                  | false
        "6526"   | "Bollendorf"                             | false
        "6527"   | "Oberweis"                               | false
        "6531"   | "Bernkastel Kues"                        | false
        "6532"   | "Zeltingen Rachtig"                      | false
        "6533"   | "Morbach Hunsrück"                       | false
        "6534"   | "Mülheim Mosel"                          | false
        "6535"   | "Osann Monzel"                           | false
        "6536"   | "Kleinich"                               | false
        "6541"   | "Traben Trarbach"                        | false
        "6542"   | "Bullay"                                 | false
        "6543"   | "Büchenbeuren"                           | false
        "6544"   | "Rhaunen"                                | false
        "6545"   | "Blankenrath"                            | false
        "6550"   | "Irrhausen"                              | false
        "6551"   | "Prüm"                                   | false
        "6552"   | "Olzheim"                                | false
        "6553"   | "Schönecken"                             | false
        "6554"   | "Waxweiler"                              | false
        "6555"   | "Bleialf"                                | false
        "6556"   | "Pronsfeld"                              | false
        "6557"   | "Hallschlag"                             | false
        "6558"   | "Büdesheim Eifel"                        | false
        "6559"   | "Leidenborn"                             | false
        "6561"   | "Bitburg"                                | false
        "6562"   | "Speicher"                               | false
        "6563"   | "Kyllburg"                               | false
        "6564"   | "Neuerburg Eifel"                        | false
        "6565"   | "Dudeldorf"                              | false
        "6566"   | "Körperich"                              | false
        "6567"   | "Oberkail"                               | false
        "6568"   | "Wolsfeld"                               | false
        "6569"   | "Bickendorf"                             | false
        "6571"   | "Wittlich"                               | false
        "6572"   | "Manderscheid Eifel"                     | false
        "6573"   | "Gillenfeld"                             | false
        "6574"   | "Hasborn"                                | false
        "6575"   | "Landscheid"                             | false
        "6578"   | "Salmtal"                                | false
        "6580"   | "Zemmer"                                 | false
        "6581"   | "Saarburg"                               | false
        "6582"   | "Freudenburg"                            | false
        "6583"   | "Palzem"                                 | false
        "6584"   | "Wellen Mosel"                           | false
        "6585"   | "Ralingen"                               | false
        "6586"   | "Beuren Hochwald"                        | false
        "6587"   | "Zerf"                                   | false
        "6588"   | "Pluwig"                                 | false
        "6589"   | "Kell am See"                            | false
        "6591"   | "Gerolstein"                             | false
        "6592"   | "Daun"                                   | false
        "6593"   | "Hillesheim Eifel"                       | false
        "6594"   | "Birresborn"                             | false
        "6595"   | "Dockweiler"                             | false
        "6596"   | "Üdersdorf"                              | false
        "6597"   | "Jünkerath"                              | false
        "6599"   | "Weidenbach bei Gerolstein"              | false
        "661"    | "Fulda"                                  | false
        "6620"   | "Philippsthal Werra"                     | false
        "6621"   | "Bad Hersfeld"                           | false
        "6622"   | "Bebra"                                  | false
        "6623"   | "Rotenburg an der Fulda"                 | false
        "6624"   | "Heringen Werra"                         | false
        "6625"   | "Niederaula"                             | false
        "6626"   | "Wildeck Obersuhl"                       | false
        "6627"   | "Nentershausen Hessen"                   | false
        "6628"   | "Oberaula"                               | false
        "6629"   | "Schenklengsfeld"                        | false
        "6630"   | "Schwalmtal Storndorf"                   | false
        "6631"   | "Alsfeld"                                | false
        "6633"   | "Homberg Ohm"                            | false
        "6634"   | "Gemünden Felda"                         | false
        "6635"   | "Kirtorf"                                | false
        "6636"   | "Romrod"                                 | false
        "6637"   | "Feldatal"                               | false
        "6638"   | "Schwalmtal Renzendorf"                  | false
        "6639"   | "Ottrau"                                 | false
        "6641"   | "Lauterbach Hessen"                      | false
        "6642"   | "Schlitz"                                | false
        "6643"   | "Herbstein"                              | false
        "6644"   | "Grebenhain"                             | false
        "6645"   | "Ulrichstein"                            | false
        "6646"   | "Grebenau"                               | false
        "6647"   | "Herbstein Stockhausen"                  | false
        "6648"   | "Bad Salzschlirf"                        | false
        "6650"   | "Hosenfeld"                              | false
        "6651"   | "Rasdorf"                                | false
        "6652"   | "Hünfeld"                                | false
        "6653"   | "Burghaun"                               | false
        "6654"   | "Gersfeld Rhön"                          | false
        "6655"   | "Neuhof Kreis Fulda"                     | false
        "6656"   | "Ebersburg"                              | false
        "6657"   | "Hofbieber"                              | false
        "6658"   | "Poppenhausen Wasserkuppe"               | false
        "6659"   | "Eichenzell"                             | false
        "6660"   | "Steinau Marjoss"                        | false
        "6661"   | "Schlüchtern"                            | false
        "6663"   | "Steinau an der Straße"                  | false
        "6664"   | "Sinntal Sterbfritz"                     | false
        "6665"   | "Sinntal Altengronau"                    | false
        "6666"   | "Freiensteinau"                          | false
        "6667"   | "Steinau Ulmbach"                        | false
        "6668"   | "Birstein Lichenroth"                    | false
        "6669"   | "Neuhof Hauswurz"                        | false
        "6670"   | "Ludwigsau Hessen"                       | false
        "6672"   | "Eiterfeld"                              | false
        "6673"   | "Haunetal"                               | false
        "6674"   | "Friedewald Hessen"                      | false
        "6675"   | "Breitenbach am Herzberg"                | false
        "6676"   | "Hohenroda Hessen"                       | false
        "6677"   | "Neuenstein Hessen"                      | false
        "6678"   | "Wildeck Hönebach"                       | false
        "6681"   | "Hilders"                                | false
        "6682"   | "Tann Rhön"                              | false
        "6683"   | "Ehrenberg Rhön"                         | false
        "6684"   | "Hofbieber Schwarzbach"                  | false
        "6691"   | "Schwalmstadt"                           | false
        "6692"   | "Neustadt Hessen"                        | false
        "6693"   | "Neuental"                               | false
        "6694"   | "Neukirchen Knüll"                       | false
        "6695"   | "Jesberg"                                | false
        "6696"   | "Gilserberg"                             | false
        "6697"   | "Willingshausen"                         | false
        "6698"   | "Schrecksbach"                           | false
        "6701"   | "Sprendlingen Rheinhessen"               | false
        "6703"   | "Wöllstein Rheinhessen"                  | false
        "6704"   | "Langenlonsheim"                         | false
        "6706"   | "Wallhausen Nahe"                        | false
        "6707"   | "Windesheim"                             | false
        "6708"   | "Bad Münster am Stein Ebernburg"         | false
        "6709"   | "Fürfeld Kreis Bad Kreuznach"            | false
        "671"    | "Bad Kreuznach"                          | false
        "6721"   | "Bingen am Rhein"                        | false
        "6722"   | "Rüdesheim am Rhein"                     | false
        "6723"   | "Oestrich Winkel"                        | false
        "6724"   | "Stromberg Hunsrück"                     | false
        "6725"   | "Gau Algesheim"                          | false
        "6726"   | "Lorch Rheingau"                         | false
        "6727"   | "Gensingen"                              | false
        "6728"   | "Ober Hilbersheim"                       | false
        "6731"   | "Alzey"                                  | false
        "6732"   | "Wörrstadt"                              | false
        "6733"   | "Gau Odernheim"                          | false
        "6734"   | "Flonheim"                               | false
        "6735"   | "Eppelsheim"                             | false
        "6736"   | "Bechenheim"                             | false
        "6737"   | "Köngernheim"                            | false
        "6741"   | "Sankt Goar"                             | true   // see https://issuetracker.google.com/issues/183383466
        "6742"   | "Boppard"                                | false
        "6743"   | "Bacharach"                              | false
        "6744"   | "Oberwesel"                              | false
        "6745"   | "Gondershausen"                          | false
        "6746"   | "Pfalzfeld"                              | false
        "6747"   | "Emmelshausen"                           | false
        "6751"   | "Bad Sobernheim"                         | false
        "6752"   | "Kirn Nahe"                              | false
        "6753"   | "Meisenheim"                             | false
        "6754"   | "Martinstein"                            | false
        "6755"   | "Odernheim am Glan"                      | false
        "6756"   | "Winterbach Soonwald"                    | false
        "6757"   | "Becherbach bei Kirn"                    | false
        "6758"   | "Waldböckelheim"                         | false
        "6761"   | "Simmern Hunsrück"                       | false
        "6762"   | "Kastellaun"                             | false
        "6763"   | "Kirchberg Hunsrück"                     | false
        "6764"   | "Rheinböllen"                            | false
        "6765"   | "Gemünden Hunsrück"                      | false
        "6766"   | "Kisselbach"                             | false
        "6771"   | "Sankt Goarshausen"                      | true   // see https://issuetracker.google.com/issues/183383466
        "6772"   | "Nastätten"                              | false
        "6773"   | "Kamp Bornhofen"                         | false
        "6774"   | "Kaub"                                   | false
        "6775"   | "Strüth Taunus"                          | false
        "6776"   | "Dachsenhausen"                          | false
        "6781"   | "Idar Oberstein"                         | false
        "6782"   | "Birkenfeld Nahe"                        | false
        "6783"   | "Baumholder"                             | false
        "6784"   | "Weierbach"                              | false
        "6785"   | "Herrstein"                              | false
        "6786"   | "Kempfeld"                               | false
        "6787"   | "Niederbrombach"                         | false
        "6788"   | "Sien"                                   | false
        "6789"   | "Heimbach Nahe"                          | false
        "6802"   | "Völklingen Lauterbach"                  | false
        "6803"   | "Mandelbachtal Ommersheim"               | false
        "6804"   | "Mandelbachtal"                          | false
        "6805"   | "Kleinblittersdorf"                      | false
        "6806"   | "Heusweiler"                             | false
        "6809"   | "Grossrosseln"                           | false
        "681"    | "Saarbrücken"                            | false
        "6821"   | "Neunkirchen Saar"                       | false
        "6824"   | "Ottweiler"                              | false
        "6825"   | "Illingen Saar"                          | false
        "6826"   | "Bexbach"                                | false
        "6827"   | "Eppelborn"                              | false
        "6831"   | "Saarlouis"                              | false
        "6832"   | "Beckingen Reimsbach"                    | false
        "6833"   | "Rehlingen Siersburg"                    | false
        "6834"   | "Bous"                                   | false
        "6835"   | "Beckingen"                              | false
        "6836"   | "Überherrn"                              | false
        "6837"   | "Wallerfangen"                           | false
        "6838"   | "Saarwellingen"                          | false
        "6841"   | "Homburg Saar"                           | false
        "6842"   | "Blieskastel"                            | false
        "6843"   | "Gersheim"                               | false
        "6844"   | "Blieskastel Altheim"                    | false
        "6848"   | "Homburg Einöd"                          | false
        "6849"   | "Kirkel"                                 | false
        "6851"   | "Sankt Wendel"                           | true   // see https://issuetracker.google.com/issues/183383466
        "6852"   | "Nohfelden"                              | false
        "6853"   | "Marpingen"                              | false
        "6854"   | "Oberthal Saar"                          | false
        "6855"   | "Freisen"                                | false
        "6856"   | "Sankt Wendel Niederkirchen"             | true   // see https://issuetracker.google.com/issues/183383466
        "6857"   | "Namborn"                                | false
        "6858"   | "Ottweiler Fürth"                        | false
        "6861"   | "Merzig"                                 | false
        "6864"   | "Mettlach"                               | false
        "6865"   | "Mettlach Orscholz"                      | false
        "6866"   | "Perl Nennig"                            | false
        "6867"   | "Perl"                                   | false
        "6868"   | "Mettlach Tünsdorf"                      | false
        "6869"   | "Merzig Silwingen"                       | false
        "6871"   | "Wadern"                                 | false
        "6872"   | "Losheim am See"                         | false
        "6873"   | "Nonnweiler"                             | false
        "6874"   | "Wadern Nunkirchen"                      | false
        "6875"   | "Nonnweiler Primstal"                    | false
        "6876"   | "Weiskirchen Saar"                       | false
        "6881"   | "Lebach"                                 | false
        "6887"   | "Schmelz Saar"                           | false
        "6888"   | "Lebach Steinbach"                       | false
        "6893"   | "Saarbrücken Ensheim"                    | false
        "6894"   | "Sankt Ingbert"                          | true   // see https://issuetracker.google.com/issues/183383466
        "6897"   | "Sulzbach Saar"                          | false
        "6898"   | "Völklingen"                             | false
        "69"     | "Frankfurt am Main"                      | false
        "7021"   | "Kirchheim unter Teck"                   | false
        "7022"   | "Nürtingen"                              | false
        "7023"   | "Weilheim an der Teck"                   | false
        "7024"   | "Wendlingen am Neckar"                   | false
        "7025"   | "Neuffen"                                | false
        "7026"   | "Lenningen"                              | false
        "7031"   | "Böblingen"                              | false
        "7032"   | "Herrenberg"                             | false
        "7033"   | "Weil Der Stadt"                         | false
        "7034"   | "Ehningen"                               | false
        "7041"   | "Mühlacker"                              | false
        "7042"   | "Vaihingen an der Enz"                   | false
        "7043"   | "Maulbronn"                              | false
        "7044"   | "Mönsheim"                               | false
        "7045"   | "Oberderdingen"                          | false
        "7046"   | "Zaberfeld"                              | false
        "7051"   | "Calw"                                   | false
        "7052"   | "Bad Liebenzell"                         | false
        "7053"   | "Bad Teinach Zavelstein"                 | false
        "7054"   | "Wildberg Württemberg"                   | false
        "7055"   | "Neuweiler Kreis Calw"                   | false
        "7056"   | "Gechingen"                              | false
        "7062"   | "Beilstein Württemberg"                  | false
        "7063"   | "Bad Wimpfen"                            | false
        "7066"   | "Bad Rappenau Bonfeld"                   | false
        "7071"   | "Tübingen"                               | false
        "7072"   | "Gomaringen"                             | false
        "7073"   | "Ammerbuch"                              | false
        "7081"   | "Bad Wildbad"                            | false
        "7082"   | "Neuenbürg Württemberg"                  | false
        "7083"   | "Bad Herrenalb"                          | false
        "7084"   | "Schömberg bei Neuenbürg"                | false
        "7085"   | "Enzklösterle"                           | false
        "711"    | "Stuttgart"                              | false
        "7121"   | "Reutlingen"                             | false
        "7122"   | "Sankt Johann Württemberg"               | true   // see https://issuetracker.google.com/issues/183383466
        "7123"   | "Metzingen Württemberg"                  | false
        "7124"   | "Trochtelfingen Hohenzollern"            | true   // see https://issuetracker.google.com/issues/183383466
        "7125"   | "Bad Urach"                              | false
        "7126"   | "Burladingen Melchingen"                 | false
        "7127"   | "Neckartenzlingen"                       | false
        "7128"   | "Sonnenbühl"                             | false
        "7129"   | "Lichtenstein Württemberg"               | false
        "7130"   | "Löwenstein Württemberg"                 | false
        "7131"   | "Heilbronn Neckar"                       | false
        "7132"   | "Neckarsulm"                             | false
        "7133"   | "Lauffen am Neckar"                      | false
        "7134"   | "Weinsberg"                              | false
        "7135"   | "Brackenheim"                            | false
        "7136"   | "Bad Friedrichshall"                     | false
        "7138"   | "Schwaigern"                             | false
        "7139"   | "Neuenstadt am Kocher"                   | false
        "7141"   | "Ludwigsburg Württemberg"                | false
        "7142"   | "Bietigheim Bissingen"                   | false
        "7143"   | "Besigheim"                              | false
        "7144"   | "Marbach am Neckar"                      | false
        "7145"   | "Markgröningen"                          | false
        "7146"   | "Remseck am Neckar"                      | false
        "7147"   | "Sachsenheim Württemberg"                | false
        "7148"   | "Grossbottwar"                           | false
        "7150"   | "Korntal Münchingen"                     | false
        "7151"   | "Waiblingen"                             | false
        "7152"   | "Leonberg Württemberg"                   | false
        "7153"   | "Plochingen"                             | false
        "7154"   | "Kornwestheim"                           | false
        "7156"   | "Ditzingen"                              | false
        "7157"   | "Waldenbuch"                             | false
        "7158"   | "Neuhausen auf den Fildern"              | false
        "7159"   | "Renningen"                              | false
        "7161"   | "Göppingen"                              | false
        "7162"   | "Süßen"                                  | false
        "7163"   | "Ebersbach an der Fils"                  | false
        "7164"   | "Boll Kreis Göppingen"                   | false
        "7165"   | "Göppingen Hohenstaufen"                 | false
        "7166"   | "Adelberg"                               | false
        "7171"   | "Schwäbisch Gmünd"                       | false
        "7172"   | "Lorch Württemberg"                      | false
        "7173"   | "Heubach"                                | false
        "7174"   | "Mögglingen"                             | false
        "7175"   | "Leinzell"                               | false
        "7176"   | "Spraitbach"                             | false
        "7181"   | "Schorndorf Württemberg"                 | false
        "7182"   | "Welzheim"                               | false
        "7183"   | "Rudersberg Württemberg"                 | false
        "7184"   | "Kaisersbach"                            | false
        "7191"   | "Backnang"                               | false
        "7192"   | "Murrhardt"                              | false
        "7193"   | "Sulzbach an der Murr"                   | false
        "7194"   | "Spiegelberg"                            | false
        "7195"   | "Winnenden"                              | false
        "7202"   | "Karlsbad"                               | false
        "7203"   | "Walzbachtal"                            | false
        "7204"   | "Malsch Völkersbach"                     | false
        "721"    | "Karlsruhe"                              | false
        "7220"   | "Forbach Hundsbach"                      | false
        "7221"   | "Baden Baden"                            | false
        "7222"   | "Rastatt"                                | false
        "7223"   | "Bühl Baden"                             | false
        "7224"   | "Gernsbach"                              | false
        "7225"   | "Gaggenau"                               | false
        "7226"   | "Bühl Sand"                              | false
        "7227"   | "Lichtenau Baden"                        | false
        "7228"   | "Forbach"                                | false
        "7229"   | "Iffezheim"                              | false
        "7231"   | "Pforzheim"                              | false
        "7232"   | "Königsbach Stein"                       | false
        "7233"   | "Niefern Öschelbronn"                    | false
        "7234"   | "Tiefenbronn"                            | false
        "7235"   | "Unterreichenbach Kreis Calw"            | false
        "7236"   | "Keltern"                                | false
        "7237"   | "Neulingen Enzkreis"                     | false
        "7240"   | "Pfinztal"                               | false
        "7242"   | "Rheinstetten"                           | false
        "7243"   | "Ettlingen"                              | false
        "7244"   | "Weingarten Baden"                       | false
        "7245"   | "Durmersheim"                            | false
        "7246"   | "Malsch Kreis Karlsruhe"                 | false
        "7247"   | "Linkenheim Hochstetten"                 | false
        "7248"   | "Marxzell"                               | false
        "7249"   | "Stutensee"                              | false
        "7250"   | "Kraichtal"                              | false
        "7251"   | "Bruchsal"                               | false
        "7252"   | "Bretten"                                | false
        "7253"   | "Bad Schönborn"                          | false
        "7254"   | "Waghäusel"                              | false
        "7255"   | "Graben Neudorf"                         | false
        "7256"   | "Philippsburg"                           | false
        "7257"   | "Bruchsal Untergrombach"                 | false
        "7258"   | "Oberderdingen Flehingen"                | false
        "7259"   | "Östringen Odenheim"                     | false
        "7260"   | "Sinsheim Hilsbach"                      | false
        "7261"   | "Sinsheim"                               | false
        "7262"   | "Eppingen"                               | false
        "7263"   | "Waibstadt"                              | false
        "7264"   | "Bad Rappenau"                           | false
        "7265"   | "Angelbachtal"                           | false
        "7266"   | "Kirchardt"                              | false
        "7267"   | "Gemmingen"                              | false
        "7268"   | "Bad Rappenau Obergimpern"               | false
        "7269"   | "Sulzfeld Baden"                         | false
        "7271"   | "Wörth am Rhein"                         | false
        "7272"   | "Rülzheim"                               | false
        "7273"   | "Hagenbach Pfalz"                        | false
        "7274"   | "Germersheim"                            | false
        "7275"   | "Kandel"                                 | false
        "7276"   | "Herxheim bei Landau Pfalz"              | false
        "7277"   | "Wörth Büchelberg"                       | false
        "7300"   | "Roggenburg"                             | false
        "7302"   | "Pfaffenhofen an der Roth"               | false
        "7303"   | "Illertissen"                            | false
        "7304"   | "Blaustein Württemberg"                  | false
        "7305"   | "Erbach Donau"                           | false
        "7306"   | "Vöhringen Iller"                        | false
        "7307"   | "Senden Iller"                           | false
        "7308"   | "Nersingen"                              | false
        "7309"   | "Weissenhorn"                            | false
        "731"    | "Ulm Donau"                              | false
        "7321"   | "Heidenheim an der Brenz"                | false
        "7322"   | "Giengen an der Brenz"                   | false
        "7323"   | "Gerstetten"                             | false
        "7324"   | "Herbrechtingen"                         | false
        "7325"   | "Sontheim an der Brenz"                  | false
        "7326"   | "Neresheim"                              | false
        "7327"   | "Dischingen"                             | false
        "7328"   | "Königsbronn"                            | false
        "7329"   | "Steinheim am Albuch"                    | false
        "7331"   | "Geislingen an der Steige"               | false
        "7332"   | "Lauterstein"                            | false
        "7333"   | "Laichingen"                             | false
        "7334"   | "Deggingen"                              | false
        "7335"   | "Wiesensteig"                            | false
        "7336"   | "Lonsee"                                 | false
        "7337"   | "Nellingen Alb"                          | false
        "7340"   | "Neenstetten"                            | false
        "7343"   | "Buch bei Illertissen"                   | false
        "7344"   | "Blaubeuren"                             | false
        "7345"   | "Langenau Württemberg"                   | false
        "7346"   | "Illerkirchberg"                         | false
        "7347"   | "Dietenheim"                             | false
        "7348"   | "Beimerstetten"                          | false
        "7351"   | "Biberach an der Riß"                    | false
        "7352"   | "Ochsenhausen"                           | false
        "7353"   | "Schwendi"                               | false
        "7354"   | "Erolzheim"                              | false
        "7355"   | "Hochdorf Riß"                           | false
        "7356"   | "Schemmerhofen"                          | false
        "7357"   | "Attenweiler"                            | false
        "7358"   | "Eberhardzell Füramoos"                  | false
        "7361"   | "Aalen"                                  | false
        "7362"   | "Bopfingen"                              | false
        "7363"   | "Lauchheim"                              | false
        "7364"   | "Oberkochen"                             | false
        "7365"   | "Essingen Württemberg"                   | false
        "7366"   | "Abtsgmünd"                              | false
        "7367"   | "Aalen Ebnat"                            | false
        "7371"   | "Riedlingen Württemberg"                 | false
        "7373"   | "Zwiefalten"                             | false
        "7374"   | "Uttenweiler"                            | false
        "7375"   | "Obermarchtal"                           | false
        "7376"   | "Langenenslingen"                        | false
        "7381"   | "Münsingen"                              | false
        "7382"   | "Römerstein"                             | false
        "7383"   | "Münsingen Buttenhausen"                 | false
        "7384"   | "Schelklingen Hütten"                    | false
        "7385"   | "Gomadingen"                             | false
        "7386"   | "Hayingen"                               | false
        "7387"   | "Hohenstein Württemberg"                 | false
        "7388"   | "Pfronstetten"                           | false
        "7389"   | "Heroldstatt"                            | false
        "7391"   | "Ehingen Donau"                          | false
        "7392"   | "Laupheim"                               | false
        "7393"   | "Munderkingen"                           | false
        "7394"   | "Schelklingen"                           | false
        "7395"   | "Ehingen Dächingen"                      | false
        "7402"   | "Fluorn Winzeln"                         | false
        "7403"   | "Dunningen"                              | false
        "7404"   | "Epfendorf"                              | false
        "741"    | "Rottweil"                               | false
        "7420"   | "Deisslingen"                            | false
        "7422"   | "Schramberg"                             | false
        "7423"   | "Oberndorf am Neckar"                    | false
        "7424"   | "Spaichingen"                            | false
        "7425"   | "Trossingen"                             | false
        "7426"   | "Gosheim"                                | false
        "7427"   | "Schömberg bei Balingen"                 | false
        "7428"   | "Rosenfeld"                              | false
        "7429"   | "Egesheim"                               | false
        "7431"   | "Albstadt Ebingen"                       | false
        "7432"   | "Albstadt Tailfingen"                    | false
        "7433"   | "Balingen"                               | false
        "7434"   | "Winterlingen"                           | false
        "7435"   | "Albstadt Laufen"                        | false
        "7436"   | "Messstetten Oberdigisheim"              | false
        "7440"   | "Bad Rippoldsau"                         | false
        "7441"   | "Freudenstadt"                           | false
        "7442"   | "Baiersbronn"                            | false
        "7443"   | "Dornstetten"                            | false
        "7444"   | "Alpirsbach"                             | false
        "7445"   | "Pfalzgrafenweiler"                      | false
        "7446"   | "Lossburg"                               | false
        "7447"   | "Baiersbronn Schwarzenberg"              | false
        "7448"   | "Seewald"                                | false
        "7449"   | "Baiersbronn Obertal"                    | false
        "7451"   | "Horb am Neckar"                         | false
        "7452"   | "Nagold"                                 | false
        "7453"   | "Altensteig Württemberg"                 | false
        "7454"   | "Sulz am Neckar"                         | false
        "7455"   | "Dornhan"                                | false
        "7456"   | "Haiterbach"                             | false
        "7457"   | "Rottenburg Ergenzingen"                 | false
        "7458"   | "Ebhausen"                               | false
        "7459"   | "Nagold Hochdorf"                        | false
        "7461"   | "Tuttlingen"                             | false
        "7462"   | "Immendingen"                            | false
        "7463"   | "Mühlheim an der Donau"                  | false
        "7464"   | "Talheim Kreis Tuttlingen"               | false
        "7465"   | "Emmingen Liptingen"                     | false
        "7466"   | "Beuron"                                 | false
        "7467"   | "Neuhausen ob Eck"                       | false
        "7471"   | "Hechingen"                              | false
        "7472"   | "Rottenburg am Neckar"                   | false
        "7473"   | "Mössingen"                              | false
        "7474"   | "Haigerloch"                             | false
        "7475"   | "Burladingen"                            | false
        "7476"   | "Bisingen"                               | false
        "7477"   | "Jungingen bei Hechingen"                | false
        "7478"   | "Hirrlingen"                             | false
        "7482"   | "Horb Dettingen"                         | false
        "7483"   | "Horb Mühringen"                         | false
        "7484"   | "Simmersfeld"                            | false
        "7485"   | "Empfingen"                              | false
        "7486"   | "Horb Altheim"                           | false
        "7502"   | "Wolpertswende"                          | false
        "7503"   | "Wilhelmsdorf Württemberg"               | false
        "7504"   | "Horgenzell"                             | false
        "7505"   | "Fronreute"                              | false
        "7506"   | "Wangen Leupolz"                         | false
        "751"    | "Ravensburg"                             | false
        "7520"   | "Bodnegg"                                | false
        "7522"   | "Wangen im Allgäu"                       | false
        "7524"   | "Bad Waldsee"                            | false
        "7525"   | "Aulendorf"                              | false
        "7527"   | "Wolfegg"                                | false
        "7528"   | "Neukirch bei Tettnang"                  | false
        "7529"   | "Waldburg Württemberg"                   | false
        "7531"   | "Konstanz"                               | false
        "7532"   | "Meersburg"                              | false
        "7533"   | "Allensbach"                             | false
        "7534"   | "Reichenau Baden"                        | false
        "7541"   | "Friedrichshafen"                        | false
        "7542"   | "Tettnang"                               | false
        "7543"   | "Kressbronn am Bodensee"                 | false
        "7544"   | "Markdorf"                               | false
        "7545"   | "Immenstaad am Bodensee"                 | false
        "7546"   | "Oberteuringen"                          | false
        "7551"   | "Überlingen Bodensee"                    | false
        "7552"   | "Pfullendorf"                            | false
        "7553"   | "Salem Baden"                            | false
        "7554"   | "Heiligenberg Baden"                     | false
        "7555"   | "Deggenhausertal"                        | false
        "7556"   | "Uhldingen Mühlhofen"                    | false
        "7557"   | "Herdwangen Schönach"                    | false
        "7558"   | "Illmensee"                              | false
        "7561"   | "Leutkirch im Allgäu"                    | false
        "7562"   | "Isny im Allgäu"                         | false
        "7563"   | "Kisslegg"                               | false
        "7564"   | "Bad Wurzach"                            | false
        "7565"   | "Aichstetten Kreis Ravensburg"           | false
        "7566"   | "Argenbühl"                              | false
        "7567"   | "Leutkirch Friesenhofen"                 | false
        "7568"   | "Bad Wurzach Hauerz"                     | false
        "7569"   | "Isny Eisenbach"                         | false
        "7570"   | "Sigmaringen Gutenstein"                 | false
        "7571"   | "Sigmaringen"                            | false
        "7572"   | "Mengen Württemberg"                     | false
        "7573"   | "Stetten am kalten Markt"                | false
        "7574"   | "Gammertingen"                           | false
        "7575"   | "Messkirch"                              | false
        "7576"   | "Krauchenwies"                           | false
        "7577"   | "Veringenstadt"                          | false
        "7578"   | "Wald Hohenzollern"                      | true   // missed in https://issuetracker.google.com/issues/183383466
        "7579"   | "Schwenningen Baden"                     | false
        "7581"   | "Saulgau"                                | false
        "7582"   | "Bad Buchau"                             | false
        "7583"   | "Bad Schussenried"                       | false
        "7584"   | "Altshausen"                             | false
        "7585"   | "Ostrach"                                | false
        "7586"   | "Herbertingen"                           | false
        "7587"   | "Hosskirch"                              | false
        "7602"   | "Oberried Breisgau"                      | false  // ignoring the last digit (2) since 8.10.1
        "761"    | "Freiburg im Breisgau"                   | false
        "7620"   | "Schopfheim Gersbach"                    | false
        "7621"   | "Lörrach"                                | false
        "7622"   | "Schopfheim"                             | false
        "7623"   | "Rheinfelden Baden"                      | false
        "7624"   | "Grenzach Wyhlen"                        | false
        "7625"   | "Zell im Wiesental"                      | false
        "7626"   | "Kandern"                                | false
        "7627"   | "Steinen Kreis Lörrach"                  | false
        "7628"   | "Efringen Kirchen"                       | false
        "7629"   | "Tegernau Baden"                         | false
        "7631"   | "Müllheim Baden"                         | false
        "7632"   | "Badenweiler"                            | false
        "7633"   | "Staufen im Breisgau"                    | false
        "7634"   | "Sulzburg"                               | false
        "7635"   | "Schliengen"                             | false
        "7636"   | "Münstertal Schwarzwald"                 | false
        "7641"   | "Emmendingen"                            | false
        "7642"   | "Endingen Kaiserstuhl"                   | false
        "7643"   | "Herbolzheim Breisgau"                   | false
        "7644"   | "Kenzingen"                              | false
        "7645"   | "Freiamt"                                | false
        "7646"   | "Weisweil Breisgau"                      | false
        "7651"   | "Titisee Neustadt"                       | false
        "7652"   | "Hinterzarten"                           | false
        "7653"   | "Lenzkirch"                              | false
        "7654"   | "Löffingen"                              | false
        "7655"   | "Feldberg Altglashütten"                 | false
        "7656"   | "Schluchsee"                             | false
        "7657"   | "Eisenbach Hochschwarzwald"              | false
        "7660"   | "Sankt Peter Schwarzwald"                | true   // see https://issuetracker.google.com/issues/183383466
        "7661"   | "Kirchzarten"                            | false
        "7662"   | "Vogtsburg im Kaiserstuhl"               | false
        "7663"   | "Eichstetten"                            | false
        "7664"   | "Freiburg Tiengen"                       | false
        "7665"   | "March Breisgau"                         | false
        "7666"   | "Denzlingen"                             | false
        "7667"   | "Breisach am Rhein"                      | false
        "7668"   | "Ihringen"                               | false
        "7669"   | "Sankt Märgen"                           | true   // see https://issuetracker.google.com/issues/183383466
        "7671"   | "Todtnau"                                | false
        "7672"   | "Sankt Blasien"                          | true   // see https://issuetracker.google.com/issues/183383466
        "7673"   | "Schönau im Schwarzwald"                 | false
        "7674"   | "Todtmoos"                               | false
        "7675"   | "Bernau Baden"                           | false
        "7676"   | "Feldberg Schwarzwald"                   | false
        "7681"   | "Waldkirch Breisgau"                     | false
        "7682"   | "Elzach"                                 | false
        "7683"   | "Simonswald"                             | false
        "7684"   | "Glottertal"                             | false
        "7685"   | "Gutach Bleibach"                        | false
        "7702"   | "Blumberg Baden"                         | false
        "7703"   | "Bonndorf im Schwarzwald"                | false
        "7704"   | "Geisingen Baden"                        | false
        "7705"   | "Wolterdingen Schwarzwald"               | true  // see https://issuetracker.google.com/issues/183383466
        "7706"   | "Oberbaldingen"                          | false
        "7707"   | "Bräunlingen"                            | false
        "7708"   | "Geisingen Leipferdingen"                | false
        "7709"   | "Wutach"                                 | false
        "771"    | "Donaueschingen"                         | false
        "7720"   | "Schwenningen am Neckar"                 | false
        "7721"   | "Villingen im Schwarzwald"               | false
        "7722"   | "Triberg im Schwarzwald"                 | false
        "7723"   | "Furtwangen im Schwarzwald"              | false
        "7724"   | "Sankt Georgen im Schwarzwald"           | true   // see https://issuetracker.google.com/issues/183383466
        "7725"   | "Königsfeld im Schwarzwald"              | false
        "7726"   | "Bad Dürrheim"                           | false
        "7727"   | "Vöhrenbach"                             | false
        "7728"   | "Niedereschach"                          | false
        "7729"   | "Tennenbronn"                            | false
        "7731"   | "Singen Hohentwiel"                      | false
        "7732"   | "Radolfzell am Bodensee"                 | false
        "7733"   | "Engen Hegau"                            | false
        "7734"   | "Gailingen"                              | false
        "7735"   | "Öhningen"                               | false
        "7736"   | "Tengen"                                 | false
        "7738"   | "Steisslingen"                           | false
        "7739"   | "Hilzingen"                              | false
        "7741"   | "Tiengen Hochrhein"                      | false
        "7742"   | "Klettgau"                               | false
        "7743"   | "Ühlingen Birkendorf"                    | false
        "7744"   | "Stühlingen"                             | false
        "7745"   | "Jestetten"                              | false
        "7746"   | "Wutöschingen"                           | false
        "7747"   | "Berau"                                  | false
        "7748"   | "Grafenhausen Hochschwarzwald"           | false
        "7751"   | "Waldshut"                               | false
        "7753"   | "Albbruck"                               | false
        "7754"   | "Görwihl"                                | false
        "7755"   | "Weilheim Kreis Waldshut"                | false
        "7761"   | "Bad Säckingen"                          | false
        "7762"   | "Wehr Baden"                             | false
        "7763"   | "Murg"                                   | false
        "7764"   | "Herrischried"                           | false
        "7765"   | "Rickenbach Hotzenwald"                  | false
        "7771"   | "Stockach"                               | false
        "7773"   | "Bodman Ludwigshafen"                    | false
        "7774"   | "Eigeltingen"                            | false
        "7775"   | "Mühlingen"                              | false
        "7777"   | "Sauldorf"                               | false
        "7802"   | "Oberkirch Baden"                        | false
        "7803"   | "Gengenbach"                             | false
        "7804"   | "Oppenau"                                | false
        "7805"   | "Appenweier"                             | false
        "7806"   | "Bad Peterstal Griesbach"                | false
        "7807"   | "Neuried Ortenaukreis"                   | false
        "7808"   | "Hohberg bei Offenburg"                  | false
        "781"    | "Offenburg"                              | false
        "7821"   | "Lahr Schwarzwald"                       | false
        "7822"   | "Ettenheim"                              | false
        "7823"   | "Seelbach Schutter"                      | false
        "7824"   | "Schwanau"                               | false
        "7825"   | "Kippenheim"                             | false
        "7826"   | "Schuttertal"                            | false
        "7831"   | "Hausach"                                | false
        "7832"   | "Haslach im Kinzigtal"                   | false
        "7833"   | "Hornberg Schwarzwaldbahn"               | false
        "7834"   | "Wolfach"                                | false
        "7835"   | "Zell am Harmersbach"                    | false
        "7836"   | "Schiltach"                              | false
        "7837"   | "Oberharmersbach"                        | false
        "7838"   | "Nordrach"                               | false
        "7839"   | "Schapbach"                              | false
        "7841"   | "Achern"                                 | false
        "7842"   | "Kappelrodeck"                           | false
        "7843"   | "Renchen"                                | false
        "7844"   | "Rheinau"                                | false
        "7851"   | "Kehl"                                   | false
        "7852"   | "Willstätt"                              | false
        "7853"   | "Kehl Bodersweier"                       | false
        "7854"   | "Kehl Goldscheuer"                       | false
        "7903"   | "Mainhardt"                              | false
        "7904"   | "Ilshofen"                               | false
        "7905"   | "Langenburg"                             | false
        "7906"   | "Braunsbach"                             | false
        "7907"   | "Schwäbisch Hall Sulzdorf"               | false
        "791"    | "Schwäbisch Hall"                        | false
        "7930"   | "Boxberg Baden"                          | false
        "7931"   | "Bad Mergentheim"                        | false
        "7932"   | "Niederstetten Württemberg"              | false
        "7933"   | "Creglingen"                             | false
        "7934"   | "Weikersheim"                            | false
        "7935"   | "Schrozberg"                             | false
        "7936"   | "Schrozberg Bartenstein"                 | false
        "7937"   | "Dörzbach"                               | false
        "7938"   | "Mulfingen Jagst"                        | false
        "7939"   | "Schrozberg Spielbach"                   | false
        "7940"   | "Künzelsau"                              | false
        "7941"   | "Öhringen"                               | false
        "7942"   | "Neuenstein Württemberg"                 | false
        "7943"   | "Schöntal Jagst"                         | false
        "7944"   | "Kupferzell"                             | false
        "7945"   | "Wüstenrot"                              | false
        "7946"   | "Bretzfeld"                              | false
        "7947"   | "Forchtenberg"                           | false
        "7948"   | "Öhringen Ohrnberg"                      | false
        "7949"   | "Pfedelbach Untersteinbach"              | false
        "7950"   | "Schnelldorf"                            | false
        "7951"   | "Crailsheim"                             | false
        "7952"   | "Gerabronn"                              | false
        "7953"   | "Blaufelden"                             | false
        "7954"   | "Kirchberg an der Jagst"                 | false
        "7955"   | "Wallhausen Württemberg"                 | false
        "7957"   | "Kressberg"                              | false
        "7958"   | "Rot Am See Brettheim"                   | false
        "7959"   | "Frankenhardt"                           | false
        "7961"   | "Ellwangen Jagst"                        | false
        "7962"   | "Fichtenau"                              | false
        "7963"   | "Adelmannsfelden"                        | false
        "7964"   | "Stödtlen"                               | false
        "7965"   | "Ellwangen Röhlingen"                    | false
        "7966"   | "Unterschneidheim"                       | false
        "7967"   | "Jagstzell"                              | false
        "7971"   | "Gaildorf"                               | false
        "7972"   | "Gschwend bei Gaildorf"                  | false
        "7973"   | "Obersontheim"                           | false
        "7974"   | "Bühlerzell"                             | false
        "7975"   | "Untergröningen"                         | false
        "7976"   | "Sulzbach Laufen"                        | false
        "7977"   | "Oberrot bei Gaildorf"                   | false
        "8020"   | "Weyarn"                                 | false
        "8021"   | "Waakirchen"                             | false
        "8022"   | "Tegernsee"                              | false
        "8023"   | "Bayrischzell"                           | false
        "8024"   | "Holzkirchen"                            | false
        "8025"   | "Miesbach"                               | false
        "8026"   | "Hausham"                                | false
        "8027"   | "Dietramszell"                           | false
        "8028"   | "Fischbachau"                            | false
        "8029"   | "Kreuth bei Tegernsee"                   | false
        "8031"   | "Rosenheim Oberbayern"                   | false
        "8032"   | "Rohrdorf Kreis Rosenheim"               | false
        "8033"   | "Oberaudorf"                             | false
        "8034"   | "Brannenburg"                            | false
        "8035"   | "Raubling"                               | false
        "8036"   | "Stephanskirchen Simssee"                | false
        "8038"   | "Vogtareuth"                             | false
        "8039"   | "Rott am Inn"                            | false
        "8041"   | "Bad Tölz"                               | false
        "8042"   | "Lenggries"                              | false
        "8043"   | "Jachenau"                               | false
        "8045"   | "Lenggries Fall"                         | false
        "8046"   | "Bad Heilbrunn"                          | false
        "8051"   | "Prien am Chiemsee"                      | false
        "8052"   | "Aschau im Chiemgau"                     | false
        "8053"   | "Bad Endorf"                             | false
        "8054"   | "Breitbrunn am Chiemsee"                 | false
        "8055"   | "Halfing"                                | false
        "8056"   | "Eggstätt"                               | false
        "8057"   | "Aschau Sachrang"                        | false
        "8061"   | "Bad Aibling"                            | false
        "8062"   | "Bruckmühl Mangfall"                     | false
        "8063"   | "Feldkirchen Westerham"                  | false
        "8064"   | "Au bei Bad Aibling"                     | false
        "8065"   | "Tuntenhausen Schönau"                   | false
        "8066"   | "Bad Feilnbach"                          | false
        "8067"   | "Tuntenhausen"                           | false
        "8071"   | "Wasserburg am Inn"                      | false
        "8072"   | "Haag in Oberbayern"                     | false
        "8073"   | "Gars am Inn"                            | false
        "8074"   | "Schnaitsee"                             | false
        "8075"   | "Amerang"                                | false
        "8076"   | "Pfaffing"                               | false
        "8081"   | "Dorfen Stadt"                           | false
        "8082"   | "Schwindegg"                             | false
        "8083"   | "Isen"                                   | false
        "8084"   | "Taufkirchen Vils"                       | false
        "8085"   | "Sankt Wolfgang"                         | false
        "8086"   | "Buchbach Oberbayern"                    | false
        "8091"   | "Kirchseeon"                             | false
        "8092"   | "Grafing bei München"                    | false
        "8093"   | "Glonn Kreis Ebersberg"                  | false
        "8094"   | "Steinhöring"                            | false
        "8095"   | "Aying"                                  | false
        "8102"   | "Höhenkirchen Siegertsbrunn"             | false
        "8104"   | "Sauerlach"                              | false
        "8105"   | "Gilching"                               | false
        "8106"   | "Vaterstetten"                           | false
        "811"    | "Hallbergmoos"                           | false
        "8121"   | "Markt Schwaben"                         | false
        "8122"   | "Erding"                                 | false
        "8123"   | "Moosinning"                             | false
        "8124"   | "Forstern Oberbayern"                    | false
        "8131"   | "Dachau"                                 | false
        "8133"   | "Haimhausen Oberbayern"                  | false
        "8134"   | "Odelzhausen"                            | false
        "8135"   | "Sulzemoos"                              | false
        "8136"   | "Markt Indersdorf"                       | false
        "8137"   | "Petershausen"                           | false
        "8138"   | "Schwabhausen bei Dachau"                | false
        "8139"   | "Röhrmoos"                               | false
        "8141"   | "Fürstenfeldbruck"                       | false
        "8142"   | "Olching"                                | false
        "8143"   | "Inning am Ammersee"                     | false
        "8144"   | "Grafrath"                               | false
        "8145"   | "Mammendorf"                             | false
        "8146"   | "Moorenweis"                             | false
        "8151"   | "Starnberg"                              | false
        "8152"   | "Herrsching am Ammersee"                 | false
        "8153"   | "Wessling"                               | false
        "8157"   | "Feldafing"                              | false
        "8158"   | "Tutzing"                                | false
        "8161"   | "Freising"                               | false
        "8165"   | "Neufahrn bei Freising"                  | false
        "8166"   | "Allershausen Oberbayern"                | false
        "8167"   | "Zolling"                                | false
        "8168"   | "Attenkirchen"                           | false
        "8170"   | "Straßlach Dingharting"                  | false
        "8171"   | "Wolfratshausen"                         | false
        "8176"   | "Egling bei Wolfratshausen"              | false
        "8177"   | "Münsing Starnberger See"                | false
        "8178"   | "Icking"                                 | false
        "8179"   | "Eurasburg an der Loisach"               | false
        "8191"   | "Landsberg am Lech"                      | false
        "8192"   | "Schondorf am Ammersee"                  | false
        "8193"   | "Geltendorf"                             | false
        "8194"   | "Vilgertshofen"                          | false
        "8195"   | "Weil Kreis Landsberg am Lech"           | false
        "8196"   | "Pürgen"                                 | false
        "8202"   | "Althegnenberg"                          | false
        "8203"   | "Grossaitingen"                          | false
        "8204"   | "Mickhausen"                             | false
        "8205"   | "Dasing"                                 | false
        "8206"   | "Egling an der Paar"                     | false
        "8207"   | "Affing"                                 | false
        "8208"   | "Eurasburg bei Augsburg"                 | false
        "821"    | "Augsburg"                               | false
        "8221"   | "Günzburg"                               | false
        "8222"   | "Burgau Schwaben"                        | false
        "8223"   | "Ichenhausen"                            | false
        "8224"   | "Offingen Donau"                         | false
        "8225"   | "Jettingen Scheppach"                    | false
        "8226"   | "Bibertal"                               | false
        "8230"   | "Gablingen"                              | false
        "8231"   | "Königsbrunn bei Augsburg"               | false
        "8232"   | "Schwabmünchen"                          | false
        "8233"   | "Kissing"                                | false
        "8234"   | "Bobingen"                               | false
        "8236"   | "Fischach"                               | false
        "8237"   | "Aindling"                               | false
        "8238"   | "Gessertshausen"                         | false
        "8239"   | "Langenneufnach"                         | false
        "8241"   | "Buchloe"                                | false
        "8243"   | "Fuchstal"                               | false
        "8245"   | "Türkheim Wertach"                       | false
        "8246"   | "Waal"                                   | false
        "8247"   | "Bad Wörishofen"                         | false
        "8248"   | "Lamerdingen"                            | false
        "8249"   | "Ettringen Wertach"                      | false
        "8250"   | "Hilgertshausen Tandern"                 | false
        "8251"   | "Aichach"                                | false
        "8252"   | "Schrobenhausen"                         | false
        "8253"   | "Pöttmes"                                | false
        "8254"   | "Altomünster"                            | false
        "8257"   | "Inchenhofen"                            | false
        "8258"   | "Sielenbach"                             | false
        "8259"   | "Schiltberg"                             | false
        "8261"   | "Mindelheim"                             | false
        "8262"   | "Mittelneufnach"                         | false
        "8263"   | "Breitenbrunn Schwaben"                  | false
        "8265"   | "Pfaffenhausen Schwaben"                 | false
        "8266"   | "Kirchheim in Schwaben"                  | false
        "8267"   | "Dirlewang"                              | false
        "8268"   | "Tussenhausen"                           | false
        "8269"   | "Unteregg bei Mindelheim"                | false
        "8271"   | "Meitingen"                              | false
        "8272"   | "Wertingen"                              | false
        "8273"   | "Nordendorf"                             | false
        "8274"   | "Buttenwiesen"                           | false
        "8276"   | "Baar Schwaben"                          | false
        "8281"   | "Thannhausen Schwaben"                   | false
        "8282"   | "Krumbach Schwaben"                      | false
        "8283"   | "Neuburg an der Kammel"                  | false
        "8284"   | "Ziemetshausen"                          | false
        "8285"   | "Burtenbach"                             | false
        "8291"   | "Zusmarshausen"                          | false
        "8292"   | "Dinkelscherben"                         | false
        "8293"   | "Welden bei Augsburg"                    | false
        "8294"   | "Horgau"                                 | false
        "8295"   | "Altenmünster Schwaben"                  | false
        "8296"   | "Villenbach"                             | false
        "8302"   | "Görisried"                              | false
        "8303"   | "Waltenhofen"                            | false
        "8304"   | "Wildpoldsried"                          | false
        "8306"   | "Ronsberg"                               | false
        "831"    | "Kempten Allgäu"                         | false
        "8320"   | "Missen Wilhams"                         | false
        "8321"   | "Sonthofen"                              | false
        "8322"   | "Oberstdorf"                             | false
        "8323"   | "Immenstadt im Allgäu"                   | false
        "8324"   | "Hindelang"                              | false
        "8325"   | "Oberstaufen Thalkirchdorf"              | false
        "8326"   | "Fischen im Allgäu"                      | false
        "8327"   | "Rettenberg"                             | false
        "8328"   | "Balderschwang"                          | false
        "8330"   | "Legau"                                  | false
        "8331"   | "Memmingen"                              | false
        "8332"   | "Ottobeuren"                             | false
        "8333"   | "Babenhausen Schwaben"                   | false
        "8334"   | "Bad Grönenbach"                         | false
        "8335"   | "Fellheim"                               | false
        "8336"   | "Erkheim"                                | false
        "8337"   | "Altenstadt Iller"                       | false
        "8338"   | "Böhen"                                  | false
        "8340"   | "Baisweil"                               | false
        "8341"   | "Kaufbeuren"                             | false
        "8342"   | "Marktoberdorf"                          | false
        "8343"   | "Aitrang"                                | false
        "8344"   | "Westendorf bei Kaufbeuren"              | false
        "8345"   | "Stöttwang"                              | false
        "8346"   | "Pforzen"                                | false
        "8347"   | "Friesenried"                            | false
        "8348"   | "Bidingen"                               | false
        "8349"   | "Stötten am Auerberg"                    | false
        "8361"   | "Nesselwang"                             | false
        "8362"   | "Füssen"                                 | false
        "8363"   | "Pfronten"                               | false
        "8364"   | "Seeg"                                   | false
        "8365"   | "Wertach"                                | false
        "8366"   | "Oy Mittelberg"                          | false
        "8367"   | "Roßhaupten Forggensee"                  | false
        "8368"   | "Halblech"                               | false
        "8369"   | "Rückholz"                               | false
        "8370"   | "Wiggensbach"                            | false
        "8372"   | "Obergünzburg"                           | false
        "8373"   | "Altusried"                              | false
        "8374"   | "Dietmannsried"                          | false
        "8375"   | "Weitnau"                                | false
        "8376"   | "Sulzberg Allgäu"                        | false
        "8377"   | "Unterthingau"                           | false
        "8378"   | "Buchenberg bei Kempten"                 | false
        "8379"   | "Waltenhofen Oberdorf"                   | false
        "8380"   | "Achberg"                                | false
        "8381"   | "Lindenberg im Allgäu"                   | false
        "8382"   | "Lindau Bodensee"                        | false
        "8383"   | "Grünenbach Allgäu"                      | false
        "8384"   | "Röthenbach Allgäu"                      | false
        "8385"   | "Hergatz"                                | false
        "8386"   | "Oberstaufen"                            | false
        "8387"   | "Weiler Simmerberg"                      | false
        "8388"   | "Hergensweiler"                          | false
        "8389"   | "Weissensberg"                           | false
        "8392"   | "Markt Rettenbach"                       | false
        "8393"   | "Holzgünz"                               | false
        "8394"   | "Lautrach"                               | false
        "8395"   | "Tannheim Württemberg"                   | false
        "8402"   | "Münchsmünster"                          | false
        "8403"   | "Pförring"                               | false
        "8404"   | "Oberdolling"                            | false
        "8405"   | "Stammham bei Ingolstadt"                | false
        "8406"   | "Böhmfeld"                               | false
        "8407"   | "Grossmehring"                           | false
        "841"    | "Ingolstadt Donau"                       | false
        "8421"   | "Eichstätt Bayern"                       | false
        "8422"   | "Dollnstein"                             | false
        "8423"   | "Titting"                                | false
        "8424"   | "Nassenfels"                             | false
        "8426"   | "Walting Kreis Eichstätt"                | false
        "8427"   | "Wellheim"                               | false
        "8431"   | "Neuburg an der Donau"                   | false
        "8432"   | "Burgheim"                               | false
        "8433"   | "Königsmoos"                             | false
        "8434"   | "Rennertshofen"                          | false
        "8435"   | "Ehekirchen"                             | false
        "8441"   | "Pfaffenhofen an der Ilm"                | false
        "8442"   | "Wolnzach"                               | false
        "8443"   | "Hohenwart Paar"                         | false
        "8444"   | "Schweitenkirchen"                       | false
        "8445"   | "Gerolsbach"                             | false
        "8446"   | "Pörnbach"                               | false
        "8450"   | "Ingolstadt Zuchering"                   | false
        "8452"   | "Geisenfeld"                             | false
        "8453"   | "Reichertshofen Oberbayern"              | false
        "8454"   | "Karlshuld"                              | false
        "8456"   | "Lenting"                                | false
        "8457"   | "Vohburg an der Donau"                   | false
        "8458"   | "Gaimersheim"                            | false
        "8459"   | "Manching"                               | false
        "8460"   | "Berching Holnstein"                     | false
        "8461"   | "Beilngries"                             | false
        "8462"   | "Berching"                               | false
        "8463"   | "Greding"                                | false
        "8464"   | "Dietfurt an der Altmühl"                | false
        "8465"   | "Kipfenberg"                             | false
        "8466"   | "Denkendorf Oberbayern"                  | false
        "8467"   | "Kinding"                                | false
        "8468"   | "Altmannstein Pondorf"                   | false
        "8469"   | "Freystadt Burggriesbach"                | false
        "8501"   | "Thyrnau"                                | false
        "8502"   | "Fürstenzell"                            | false
        "8503"   | "Neuhaus am Inn"                         | false
        "8504"   | "Tittling"                               | false
        "8505"   | "Hutthurm"                               | false
        "8506"   | "Bad Höhenstadt"                         | false
        "8507"   | "Neuburg am Inn"                         | false
        "8509"   | "Ruderting"                              | false
        "851"    | "Passau"                                 | false
        "8531"   | "Pocking"                                | false
        "8532"   | "Griesbach im Rottal"                    | false
        "8533"   | "Rotthalmünster"                         | false
        "8534"   | "Tettenweis"                             | false
        "8535"   | "Haarbach"                               | false
        "8536"   | "Kößlarn"                                | false
        "8537"   | "Bad Füssing Aigen"                      | false
        "8538"   | "Pocking Hartkirchen"                    | false
        "8541"   | "Vilshofen Niederbayern"                 | false
        "8542"   | "Ortenburg"                              | false
        "8543"   | "Aidenbach"                              | false
        "8544"   | "Eging am See"                           | false
        "8545"   | "Hofkirchen Bayern"                      | false
        "8546"   | "Windorf Otterskirchen"                  | false
        "8547"   | "Osterhofen Gergweis"                    | false
        "8548"   | "Vilshofen Sandbach"                     | false
        "8549"   | "Vilshofen Pleinting"                    | false
        "8550"   | "Philippsreut"                           | false
        "8551"   | "Freyung"                                | false
        "8552"   | "Grafenau Niederbayern"                  | false
        "8553"   | "Spiegelau"                              | false
        "8554"   | "Schönberg Niederbayern"                 | false
        "8555"   | "Perlesreut"                             | false
        "8556"   | "Haidmühle"                              | false
        "8557"   | "Mauth"                                  | false
        "8558"   | "Hohenau Niederbayern"                   | false
        "8561"   | "Pfarrkirchen Niederbayern"              | false
        "8562"   | "Triftern"                               | false
        "8563"   | "Bad Birnbach Rottal"                    | false
        "8564"   | "Johanniskirchen"                        | false
        "8565"   | "Dietersburg Baumgarten"                 | false
        "8571"   | "Simbach am Inn"                         | false
        "8572"   | "Tann Niederbayern"                      | false
        "8573"   | "Ering"                                  | false
        "8574"   | "Wittibreut"                             | false
        "8581"   | "Waldkirchen Niederbayern"               | false
        "8582"   | "Röhrnbach"                              | false
        "8583"   | "Neureichenau"                           | false
        "8584"   | "Breitenberg Niederbayern"               | false
        "8585"   | "Grainet"                                | false
        "8586"   | "Hauzenberg"                             | false
        "8591"   | "Obernzell"                              | false
        "8592"   | "Wegscheid Niederbayern"                 | false
        "8593"   | "Untergriesbach"                         | false
        "861"    | "Traunstein"                             | false
        "8621"   | "Trostberg"                              | false
        "8622"   | "Tacherting Peterskirchen"               | false
        "8623"   | "Kirchweidach"                           | false
        "8624"   | "Obing"                                  | false
        "8628"   | "Kienberg Oberbayern"                    | false
        "8629"   | "Palling"                                | false
        "8630"   | "Oberneukirchen"                         | false
        "8631"   | "Mühldorf am Inn"                        | false
        "8633"   | "Tüßling"                                | false
        "8634"   | "Garching an der Alz"                    | false
        "8635"   | "Pleiskirchen"                           | false
        "8636"   | "Ampfing"                                | false
        "8637"   | "Lohkirchen"                             | false
        "8638"   | "Waldkraiburg"                           | false
        "8639"   | "Neumarkt Sankt Veit"                    | false
        "8640"   | "Reit Im Winkl"                          | false
        "8641"   | "Grassau Kreis Traunstein"               | false
        "8642"   | "Übersee"                                | false
        "8649"   | "Schleching"                             | false
        "8650"   | "Marktschellenberg"                      | false
        "8651"   | "Bad Reichenhall"                        | false
        "8652"   | "Berchtesgaden"                          | false
        "8654"   | "Freilassing"                            | false
        "8656"   | "Anger"                                  | false
        "8657"   | "Ramsau bei Berchtesgaden"               | false
        "8661"   | "Grabenstätt Chiemsee"                   | false
        "8662"   | "Siegsdorf Kreis Traunstein"             | false
        "8663"   | "Ruhpolding"                             | false
        "8664"   | "Chieming"                               | false
        "8665"   | "Inzell"                                 | false
        "8666"   | "Teisendorf"                             | false
        "8667"   | "Seeon Seebruck"                         | false
        "8669"   | "Traunreut"                              | false
        "8670"   | "Reischach Kreis Altötting"              | false
        "8671"   | "Altötting"                              | false
        "8677"   | "Burghausen Salzach"                     | false
        "8678"   | "Marktl"                                 | false
        "8679"   | "Burgkirchen an der Alz"                 | false
        "8681"   | "Waging am See"                          | false
        "8682"   | "Laufen Salzach"                         | false
        "8683"   | "Tittmoning"                             | false
        "8684"   | "Fridolfing"                             | false
        "8685"   | "Kirchanschöring"                        | false
        "8686"   | "Petting"                                | false
        "8687"   | "Taching Tengling"                       | false
        "8702"   | "Wörth an der Isar"                      | false
        "8703"   | "Essenbach"                              | false
        "8704"   | "Altdorf Pfettrach"                      | false
        "8705"   | "Altfraunhofen"                          | false
        "8706"   | "Vilsheim"                               | false
        "8707"   | "Adlkofen"                               | false
        "8708"   | "Weihmichl Unterneuhausen"               | false
        "8709"   | "Eching Niederbayern"                    | false
        "871"    | "Landshut"                               | false
        "8721"   | "Eggenfelden"                            | false
        "8722"   | "Gangkofen"                              | false
        "8723"   | "Arnstorf"                               | false
        "8724"   | "Massing"                                | false
        "8725"   | "Wurmannsquick"                          | false
        "8726"   | "Schönau Niederbayern"                   | false
        "8727"   | "Falkenberg Niederbayern"                | false
        "8728"   | "Geratskirchen"                          | false
        "8731"   | "Dingolfing"                             | false
        "8732"   | "Frontenhausen"                          | false
        "8733"   | "Mengkofen"                              | false
        "8734"   | "Reisbach Niederbayern"                  | false
        "8735"   | "Gangkofen Kollbach"                     | false
        "8741"   | "Vilsbiburg"                             | false
        "8742"   | "Velden Vils"                            | false
        "8743"   | "Geisenhausen"                           | false
        "8744"   | "Gerzen"                                 | false
        "8745"   | "Bodenkirchen"                           | false
        "8751"   | "Mainburg"                               | false
        "8752"   | "Au in der Hallertau"                    | false
        "8753"   | "Elsendorf Niederbayern"                 | false
        "8754"   | "Volkenschwand"                          | false
        "8756"   | "Nandlstadt"                             | false
        "8761"   | "Moosburg an der Isar"                   | false
        "8762"   | "Wartenberg Oberbayern"                  | false
        "8764"   | "Mauern Kreis Freising"                  | false
        "8765"   | "Bruckberg Niederbayern"                 | false
        "8766"   | "Gammelsdorf"                            | false
        "8771"   | "Ergoldsbach"                            | false
        "8772"   | "Mallersdorf Pfaffenberg"                | false
        "8773"   | "Neufahrn in Niederbayern"               | false
        "8774"   | "Bayerbach bei Ergoldsbach"              | false
        "8781"   | "Rottenburg an der Laaber"               | false
        "8782"   | "Pfeffenhausen"                          | false
        "8783"   | "Rohr in Niederbayern"                   | false
        "8784"   | "Hohenthann"                             | false
        "8785"   | "Rottenburg Oberroning"                  | false
        "8801"   | "Seeshaupt"                              | false
        "8802"   | "Huglfing"                               | false
        "8803"   | "Peissenberg"                            | false
        "8805"   | "Hohenpeissenberg"                       | false
        "8806"   | "Utting am Ammersee"                     | false
        "8807"   | "Dießen am Ammersee"                     | false
        "8808"   | "Pähl"                                   | false
        "8809"   | "Wessobrunn"                             | false
        "881"    | "Weilheim in Oberbayern"                 | false
        "8821"   | "Garmisch Partenkirchen"                 | false
        "8822"   | "Oberammergau"                           | false
        "8823"   | "Mittenwald"                             | false
        "8824"   | "Oberau Loisach"                         | false
        "8825"   | "Krün"                                   | false
        "8841"   | "Murnau am Staffelsee"                   | false
        "8845"   | "Bad Kohlgrub"                           | false
        "8846"   | "Uffing am Staffelsee"                   | false
        "8847"   | "Obersöchering"                          | false
        "8851"   | "Kochel am See"                          | false
        "8856"   | "Penzberg"                               | false
        "8857"   | "Benediktbeuern"                         | false
        "8858"   | "Kochel Walchensee"                      | false
        "8860"   | "Bernbeuren"                             | false
        "8861"   | "Schongau"                               | false
        "8862"   | "Steingaden Oberbayern"                  | false
        "8867"   | "Rottenbuch Oberbayern"                  | false
        "8868"   | "Schwabsoien"                            | false
        "8869"   | "Kinsau"                                 | false
        "89"     | "München"                                | false
        "906"    | "Donauwörth"                             | false
        "9070"   | "Tapfheim"                               | false
        "9071"   | "Dillingen an der Donau"                 | false
        "9072"   | "Lauingen Donau"                         | false
        "9073"   | "Gundelfingen an der Donau"              | false
        "9074"   | "Höchstädt an der Donau"                 | false
        "9075"   | "Glött"                                  | false
        "9076"   | "Wittislingen"                           | false
        "9077"   | "Bachhagel"                              | false
        "9078"   | "Mertingen"                              | false
        "9080"   | "Harburg Schwaben"                       | false
        "9081"   | "Nördlingen"                             | false
        "9082"   | "Oettingen in Bayern"                    | false
        "9083"   | "Möttingen"                              | false
        "9084"   | "Bissingen Schwaben"                     | false
        "9085"   | "Alerheim"                               | false
        "9086"   | "Fremdingen"                             | false
        "9087"   | "Marktoffingen"                          | false
        "9088"   | "Mönchsdeggingen"                        | false
        "9089"   | "Bissingen Unterringingen"               | false
        "9090"   | "Rain Lech"                              | false
        "9091"   | "Monheim Schwaben"                       | false
        "9092"   | "Wemding"                                | false
        "9093"   | "Polsingen"                              | false
        "9094"   | "Tagmersheim"                            | false
        "9097"   | "Marxheim"                               | false
        "9099"   | "Kaisheim"                               | false
        "9101"   | "Langenzenn"                             | false
        "9102"   | "Wilhermsdorf"                           | false
        "9103"   | "Cadolzburg"                             | false
        "9104"   | "Emskirchen"                             | false
        "9105"   | "Grosshabersdorf"                        | false
        "9106"   | "Markt Erlbach"                          | false
        "9107"   | "Trautskirchen"                          | false
        "911"    | "Nürnberg"                               | false
        "9120"   | "Leinburg"                               | false
        "9122"   | "Schwabach"                              | false
        "9123"   | "Lauf an der Pegnitz"                    | false
        "9126"   | "Eckental"                               | false
        "9127"   | "Rosstal Mittelfranken"                  | false   // corrected with 8.12.22
        "9128"   | "Feucht"                                 | false
        "9129"   | "Wendelstein"                            | false
        "9131"   | "Erlangen"                               | false
        "9132"   | "Herzogenaurach"                         | false
        "9133"   | "Baiersdorf Mittelfranken"               | false   // corrected with 8.12.22
        "9134"   | "Neunkirchen am Brand"                   | false
        "9135"   | "Heßdorf Mittelfranken"                  | true    // Mittelfranken corrected with 8.12.22, but also replaced ß with ss, which is a new mistake
        "9141"   | "Weißenburg in Bayern"                   | false
        "9142"   | "Treuchtlingen"                          | false
        "9143"   | "Pappenheim Mittelfranken"               | false
        "9144"   | "Pleinfeld"                              | false
        "9145"   | "Solnhofen"                              | false
        "9146"   | "Markt Berolzheim"                       | false
        "9147"   | "Nennslingen"                            | false
        "9148"   | "Ettenstatt"                             | false
        "9149"   | "Weissenburg Suffersheim"                | false
        "9151"   | "Hersbruck"                              | false
        "9152"   | "Hartenstein Mittelfranken"              | false
        "9153"   | "Schnaittach"                            | false
        "9154"   | "Pommelsbrunn"                           | false
        "9155"   | "Simmelsdorf"                            | false
        "9156"   | "Neuhaus an der Pegnitz"                 | false
        "9157"   | "Alfeld Mittelfranken"                   | false
        "9158"   | "Offenhausen Mittelfranken"              | false
        "9161"   | "Neustadt an der Aisch"                  | false
        "9162"   | "Scheinfeld"                             | false
        "9163"   | "Dachsbach"                              | false
        "9164"   | "Langenfeld Mittelfranken"               | false
        "9165"   | "Sugenheim"                              | false
        "9166"   | "Münchsteinach"                          | false
        "9167"   | "Oberscheinfeld"                         | false
        "9170"   | "Schwanstetten"                          | false
        "9171"   | "Roth Mittelfranken"                     | false
        "9172"   | "Georgensgmünd"                          | false
        "9173"   | "Thalmässing"                            | false
        "9174"   | "Hilpoltstein"                           | false
        "9175"   | "Spalt"                                  | false
        "9176"   | "Allersberg"                             | false
        "9177"   | "Heideck"                                | false
        "9178"   | "Abenberg Mittelfranken"                 | false
        "9179"   | "Freystadt"                              | false
        "9180"   | "Pyrbaum"                                | false
        "9181"   | "Neumarkt in der Oberpfalz"              | false
        "9182"   | "Velburg"                                | false
        "9183"   | "Burgthann"                              | false
        "9184"   | "Deining Oberpfalz"                      | false
        "9185"   | "Mühlhausen Oberpfalz"                   | false
        "9186"   | "Lauterhofen Oberpfalz"                  | false
        "9187"   | "Altdorf bei Nürnberg"                   | false
        "9188"   | "Postbauer Heng"                         | false
        "9189"   | "Berg bei Neumarkt in der Oberpfalz"     | false
        "9190"   | "Heroldsbach"                            | false
        "9191"   | "Forchheim Oberfranken"                  | false
        "9192"   | "Gräfenberg"                             | false
        "9193"   | "Höchstadt an der Aisch"                 | false
        "9194"   | "Ebermannstadt"                          | false
        "9195"   | "Adelsdorf Mittelfranken"                | false
        "9196"   | "Wiesenttal"                             | false
        "9197"   | "Egloffstein"                            | false
        "9198"   | "Heiligenstadt in Oberfranken"           | false
        "9199"   | "Kunreuth"                               | false
        "9201"   | "Gesees"                                 | false
        "9202"   | "Waischenfeld"                           | false
        "9203"   | "Neudrossenfeld"                         | false
        "9204"   | "Plankenfels"                            | false
        "9205"   | "Vorbach"                                | false
        "9206"   | "Mistelgau Obernsees"                    | false
        "9207"   | "Königsfeld Oberfranken"                 | false
        "9208"   | "Bindlach"                               | false
        "9209"   | "Emtmannsberg"                           | false
        "921"    | "Bayreuth"                               | false
        "9220"   | "Kasendorf Azendorf"                     | false
        "9221"   | "Kulmbach"                               | false
        "9222"   | "Presseck"                               | false
        "9223"   | "Rugendorf"                              | false
        "9225"   | "Stadtsteinach"                          | false
        "9227"   | "Neuenmarkt"                             | false
        "9228"   | "Thurnau"                                | false
        "9229"   | "Mainleus"                               | false
        "9231"   | "Marktredwitz"                           | false
        "9232"   | "Wunsiedel"                              | false
        "9233"   | "Arzberg Oberfranken"                    | false
        "9234"   | "Neusorg"                                | false
        "9235"   | "Thierstein"                             | false
        "9236"   | "Nagel"                                  | false
        "9238"   | "Röslau"                                 | false
        "9241"   | "Pegnitz"                                | false
        "9242"   | "Gößweinstein"                           | false
        "9243"   | "Pottenstein"                            | false
        "9244"   | "Betzenstein"                            | false
        "9245"   | "Obertrubach"                            | false
        "9246"   | "Pegnitz Trockau"                        | false
        "9251"   | "Münchberg"                              | false
        "9252"   | "Helmbrechts"                            | false
        "9253"   | "Weissenstadt"                           | false
        "9254"   | "Gefrees"                                | false
        "9255"   | "Marktleugast"                           | false
        "9256"   | "Stammbach"                              | false
        "9257"   | "Zell Oberfranken"                       | false
        "9260"   | "Wilhelmsthal Oberfranken"               | false
        "9261"   | "Kronach"                                | false
        "9262"   | "Wallenfels"                             | false
        "9263"   | "Ludwigsstadt"                           | false
        "9264"   | "Küps"                                   | false
        "9265"   | "Pressig"                                | false
        "9266"   | "Mitwitz"                                | false
        "9267"   | "Nordhalben"                             | false
        "9268"   | "Teuschnitz"                             | false
        "9269"   | "Tettau Kreis Kronach"                   | false
        "9270"   | "Creussen"                               | false
        "9271"   | "Thurnau Alladorf"                       | false
        "9272"   | "Fichtelberg"                            | false
        "9273"   | "Bad Berneck im Fichtelgebirge"          | false
        "9274"   | "Hollfeld"                               | false
        "9275"   | "Speichersdorf"                          | false
        "9276"   | "Bischofsgrün"                           | false
        "9277"   | "Warmensteinach"                         | false
        "9278"   | "Weidenberg"                             | false
        "9279"   | "Mistelgau"                              | false
        "9280"   | "Selbitz Oberfranken"                    | false
        "9281"   | "Hof Saale"                              | false
        "9282"   | "Naila"                                  | false
        "9283"   | "Rehau"                                  | false
        "9284"   | "Schwarzenbach an der Saale"             | false
        "9285"   | "Kirchenlamitz"                          | false
        "9286"   | "Oberkotzau"                             | false
        "9287"   | "Selb"                                   | false
        "9288"   | "Bad Steben"                             | false
        "9289"   | "Schwarzenbach am Wald"                  | false
        "9292"   | "Konradsreuth"                           | false
        "9293"   | "Berg Oberfranken"                       | false
        "9294"   | "Regnitzlosau"                           | false
        "9295"   | "Töpen"                                  | false
        "9302"   | "Rottendorf Unterfranken"                | false
        "9303"   | "Eibelstadt"                             | false
        "9305"   | "Estenfeld"                              | false
        "9306"   | "Kist"                                   | false
        "9307"   | "Altertheim"                             | false
        "931"    | "Würzburg"                               | false
        "9321"   | "Kitzingen"                              | false
        "9323"   | "Iphofen"                                | false
        "9324"   | "Dettelbach"                             | false
        "9325"   | "Kleinlangheim"                          | false
        "9326"   | "Markt Einersheim"                       | false
        "9331"   | "Ochsenfurt"                             | false
        "9332"   | "Marktbreit"                             | false
        "9333"   | "Sommerhausen"                           | false
        "9334"   | "Giebelstadt"                            | false
        "9335"   | "Aub Kreis Würzburg"                     | false
        "9336"   | "Bütthard"                               | false
        "9337"   | "Gaukönigshofen"                         | false
        "9338"   | "Röttingen Unterfranken"                 | false
        "9339"   | "Ippesheim"                              | false
        "9340"   | "Königheim Brehmen"                      | false
        "9341"   | "Tauberbischofsheim"                     | false
        "9342"   | "Wertheim"                               | false
        "9343"   | "Lauda Königshofen"                      | false
        "9344"   | "Gerchsheim"                             | false
        "9345"   | "Külsheim Baden"                         | false
        "9346"   | "Grünsfeld"                              | false
        "9347"   | "Wittighausen"                           | false
        "9348"   | "Werbach Gamburg"                        | false
        "9349"   | "Werbach Wenkheim"                       | false
        "9350"   | "Eussenheim Hundsbach"                   | false
        "9351"   | "Gemünden am Main"                       | false
        "9352"   | "Lohr am Main"                           | false
        "9353"   | "Karlstadt"                              | false
        "9354"   | "Rieneck"                                | false
        "9355"   | "Frammersbach"                           | false
        "9356"   | "Burgsinn"                               | false
        "9357"   | "Gräfendorf Bayern"                      | false
        "9358"   | "Gössenheim"                             | false
        "9359"   | "Karlstadt Wiesenfeld"                   | false
        "9360"   | "Thüngen"                                | false
        "9363"   | "Arnstein Unterfranken"                  | false
        "9364"   | "Zellingen"                              | false
        "9365"   | "Rimpar"                                 | false
        "9366"   | "Geroldshausen Unterfranken"             | false
        "9367"   | "Unterpleichfeld"                        | false
        "9369"   | "Uettingen"                              | false
        "9371"   | "Miltenberg"                             | false
        "9372"   | "Klingenberg am Main"                    | false
        "9373"   | "Amorbach"                               | false
        "9374"   | "Eschau"                                 | false
        "9375"   | "Freudenberg Baden"                      | false
        "9376"   | "Collenberg"                             | false
        "9377"   | "Freudenberg Boxtal"                     | false
        "9378"   | "Eichenbühl Riedern"                     | false
        "9381"   | "Volkach"                                | false
        "9382"   | "Gerolzhofen"                            | false
        "9383"   | "Wiesentheid"                            | false
        "9384"   | "Schwanfeld"                             | false
        "9385"   | "Kolitzheim"                             | false
        "9386"   | "Prosselsheim"                           | false
        "9391"   | "Marktheidenfeld"                        | false
        "9392"   | "Faulbach Unterfranken"                  | false
        "9393"   | "Rothenfels Unterfranken"                | false
        "9394"   | "Esselbach"                              | false
        "9395"   | "Triefenstein"                           | false
        "9396"   | "Urspringen bei Lohr"                    | false
        "9397"   | "Wertheim Dertingen"                     | false
        "9398"   | "Birkenfeld bei Würzburg"                | false
        "9401"   | "Neutraubling"                           | false
        "9402"   | "Regenstauf"                             | false
        "9403"   | "Donaustauf"                             | false
        "9404"   | "Nittendorf"                             | false
        "9405"   | "Bad Abbach"                             | false
        "9406"   | "Mintraching"                            | false
        "9407"   | "Wenzenbach"                             | false
        "9408"   | "Altenthann"                             | false
        "9409"   | "Pielenhofen"                            | false
        "941"    | "Regensburg"                             | false
        "9420"   | "Feldkirchen Niederbayern"               | false
        "9421"   | "Straubing"                              | false
        "9422"   | "Bogen Niederbayern"                     | false
        "9423"   | "Geiselhöring"                           | false
        "9424"   | "Strasskirchen"                          | false
        "9426"   | "Oberschneiding"                         | false
        "9427"   | "Leiblfing"                              | false
        "9428"   | "Kirchroth"                              | false
        "9429"   | "Rain Niederbayern"                      | false
        "9431"   | "Schwandorf"                             | false
        "9433"   | "Nabburg"                                | false
        "9434"   | "Bodenwöhr"                              | false
        "9435"   | "Schwarzenfeld"                          | false
        "9436"   | "Nittenau"                               | false
        "9438"   | "Fensterbach"                            | false
        "9439"   | "Neunburg Kemnath"                       | false
        "9441"   | "Kelheim"                                | false
        "9442"   | "Riedenburg"                             | false
        "9443"   | "Abensberg"                              | false
        "9444"   | "Siegenburg"                             | false
        "9445"   | "Neustadt an der Donau"                  | false
        "9446"   | "Altmannstein"                           | false
        "9447"   | "Essing"                                 | false
        "9448"   | "Hausen Niederbayern"                    | false
        "9451"   | "Schierling"                             | false
        "9452"   | "Langquaid"                              | false
        "9453"   | "Thalmassing"                            | false
        "9454"   | "Aufhausen Oberpfalz"                    | false
        "9461"   | "Roding"                                 | false
        "9462"   | "Falkenstein Oberpfalz"                  | false
        "9463"   | "Wald Oberpfalz"                         | false
        "9464"   | "Walderbach"                             | false
        "9465"   | "Neukirchen Balbini"                     | false
        "9466"   | "Stamsried"                              | false
        "9467"   | "Michelsneukirchen"                      | false
        "9468"   | "Zell Oberpfalz"                         | false
        "9469"   | "Roding Neubäu"                          | false
        "9471"   | "Burglengenfeld"                         | false
        "9472"   | "Hohenfels Oberpfalz"                    | false
        "9473"   | "Kallmünz"                               | false
        "9474"   | "Schmidmühlen"                           | false
        "9480"   | "Sünching"                               | false
        "9481"   | "Pfatter"                                | false
        "9482"   | "Wörth an der Donau"                     | false
        "9484"   | "Brennberg"                              | false
        "9491"   | "Hemau"                                  | false
        "9492"   | "Parsberg"                               | false
        "9493"   | "Beratzhausen"                           | false
        "9495"   | "Breitenbrunn Oberpfalz"                 | false
        "9497"   | "Seubersdorf in der Oberpfalz"           | false
        "9498"   | "Laaber"                                 | false
        "9499"   | "Painten"                                | false
        "9502"   | "Frensdorf"                              | false
        "9503"   | "Oberhaid Oberfranken"                   | false
        "9504"   | "Stadelhofen"                            | false
        "9505"   | "Litzendorf"                             | false
        "951"    | "Bamberg"                                | false
        "9521"   | "Hassfurt"                               | false
        "9522"   | "Eltmann"                                | false
        "9523"   | "Hofheim in Unterfranken"                | false
        "9524"   | "Zeil am Main"                           | false
        "9525"   | "Königsberg in Bayern"                   | false
        "9526"   | "Riedbach"                               | false
        "9527"   | "Knetzgau"                               | false
        "9528"   | "Donnersdorf"                            | false
        "9529"   | "Oberaurach"                             | false
        "9531"   | "Ebern"                                  | false
        "9532"   | "Maroldsweisach"                         | false
        "9533"   | "Untermerzbach"                          | false
        "9534"   | "Burgpreppach"                           | false
        "9535"   | "Pfarrweisach"                           | false
        "9536"   | "Kirchlauter"                            | false
        "9542"   | "Schesslitz"                             | false
        "9543"   | "Hirschaid"                              | false
        "9544"   | "Baunach"                                | false
        "9545"   | "Buttenheim"                             | false
        "9546"   | "Burgebrach"                             | false
        "9547"   | "Zapfendorf"                             | false
        "9548"   | "Mühlhausen Mittelfranken"               | false
        "9549"   | "Lisberg"                                | false
        "9551"   | "Burgwindheim"                           | false
        "9552"   | "Burghaslach"                            | false
        "9553"   | "Ebrach Oberfranken"                     | false
        "9554"   | "Untersteinbach Unterfranken"            | false
        "9555"   | "Schlüsselfeld Aschbach"                 | false
        "9556"   | "Geiselwind"                             | false
        "9560"   | "Grub am Forst"                          | false
        "9561"   | "Coburg"                                 | false
        "9562"   | "Sonnefeld"                              | false
        "9563"   | "Rödental"                               | false
        "9564"   | "Bad Rodach"                             | false
        "9565"   | "Untersiemau"                            | false
        "9566"   | "Meeder"                                 | false
        "9567"   | "Seßlach Gemünda"                        | false
        "9568"   | "Neustadt bei Coburg"                    | false
        "9569"   | "Sesslach"                               | false
        "9571"   | "Lichtenfels Bayern"                     | false
        "9572"   | "Burgkunstadt"                           | false
        "9573"   | "Staffelstein Oberfranken"               | false
        "9574"   | "Marktzeuln"                             | false
        "9575"   | "Weismain"                               | false
        "9576"   | "Lichtenfels Isling"                     | false
        "9602"   | "Neustadt an der Waldnaab"               | false
        "9603"   | "Floss"                                  | false
        "9604"   | "Wernberg Köblitz"                       | false
        "9605"   | "Weiherhammer"                           | false
        "9606"   | "Pfreimd"                                | false
        "9607"   | "Luhe Wildenau"                          | false
        "9608"   | "Kohlberg Oberpfalz"                     | false
        "961"    | "Weiden in der Oberpfalz"                | false
        "9621"   | "Amberg Oberpfalz"                       | false
        "9622"   | "Hirschau Oberpfalz"                     | false
        "9624"   | "Ensdorf Oberpfalz"                      | false
        "9625"   | "Kastl bei Amberg"                       | false
        "9626"   | "Hohenburg"                              | false
        "9627"   | "Freudenberg Oberpfalz"                  | false
        "9628"   | "Ursensollen"                            | false
        "9631"   | "Tirschenreuth"                          | false
        "9632"   | "Waldsassen"                             | false
        "9633"   | "Mitterteich"                            | false
        "9634"   | "Wiesau"                                 | false
        "9635"   | "Bärnau"                                 | false
        "9636"   | "Plößberg"                               | false
        "9637"   | "Falkenberg Oberpfalz"                   | false
        "9638"   | "Neualbenreuth"                          | false
        "9639"   | "Mähring"                                | false
        "9641"   | "Grafenwöhr"                             | false
        "9642"   | "Kemnath Stadt"                          | false
        "9643"   | "Auerbach in der Oberpfalz"              | false
        "9644"   | "Pressath"                               | false
        "9645"   | "Eschenbach in der Oberpfalz"            | false
        "9646"   | "Freihung"                               | false
        "9647"   | "Kirchenthumbach"                        | false
        "9648"   | "Neustadt am Kulm"                       | false
        "9651"   | "Vohenstrauss"                           | false
        "9652"   | "Waidhaus"                               | false
        "9653"   | "Eslarn"                                 | false
        "9654"   | "Pleystein"                              | false
        "9655"   | "Tännesberg"                             | false
        "9656"   | "Moosbach bei Vohenstrauß"               | false
        "9657"   | "Waldthurn"                              | false
        "9658"   | "Georgenberg"                            | false
        "9659"   | "Leuchtenberg"                           | false
        "9661"   | "Sulzbach Rosenberg"                     | false
        "9662"   | "Vilseck"                                | false
        "9663"   | "Neukirchen bei Sulzbach Rosenberg"      | false
        "9664"   | "Hahnbach"                               | false
        "9665"   | "Königstein Oberpfalz"                   | false
        "9666"   | "Illschwang"                             | false
        "9671"   | "Oberviechtach"                          | false
        "9672"   | "Neunburg vorm Wald"                     | false
        "9673"   | "Tiefenbach Oberpfalz"                   | false
        "9674"   | "Schönsee"                               | false
        "9675"   | "Altendorf am Nabburg"                   | false
        "9676"   | "Winklarn"                               | false
        "9677"   | "Oberviechtach Pullenried"               | false
        "9681"   | "Windischeschenbach"                     | false
        "9682"   | "Erbendorf"                              | false
        "9683"   | "Friedenfels"                            | false
        "9701"   | "Sandberg Unterfranken"                  | false
        "9704"   | "Euerdorf"                               | false
        "9708"   | "Bad Bocklet"                            | false
        "971"    | "Bad Kissingen"                          | false
        "9720"   | "Üchtelhausen"                           | false
        "9721"   | "Schweinfurt"                            | false
        "9722"   | "Werneck"                                | false
        "9723"   | "Röthlein"                               | false
        "9724"   | "Stadtlauringen"                         | false
        "9725"   | "Poppenhausen Unterfranken"              | false
        "9726"   | "Euerbach"                               | false
        "9727"   | "Schonungen Marktsteinach"               | false
        "9728"   | "Wülfershausen Unterfranken"             | false
        "9729"   | "Grettstadt"                             | false
        "9732"   | "Hammelburg"                             | false
        "9733"   | "Münnerstadt"                            | false
        "9734"   | "Burkardroth"                            | false
        "9735"   | "Massbach"                               | false
        "9736"   | "Oberthulba"                             | false
        "9737"   | "Wartmannsroth"                          | false
        "9738"   | "Rottershausen"                          | false
        "9741"   | "Bad Brückenau"                          | false
        "9742"   | "Kalbach Rhön"                           | false
        "9744"   | "Zeitlofs Detter"                        | false
        "9745"   | "Wildflecken"                            | false
        "9746"   | "Zeitlofs"                               | false
        "9747"   | "Geroda Bayern"                          | false
        "9748"   | "Motten"                                 | false
        "9749"   | "Oberbach Unterfranken"                  | false
        "9761"   | "Bad Königshofen im Grabfeld"            | false
        "9762"   | "Saal an der Saale"                      | false
        "9763"   | "Sulzdorf an der Lederhecke"             | false
        "9764"   | "Höchheim"                               | false
        "9765"   | "Trappstadt"                             | false
        "9766"   | "Grosswenkheim"                          | false
        "9771"   | "Bad Neustadt an der Saale"              | false
        "9772"   | "Bischofsheim an der Rhön"               | false
        "9773"   | "Unsleben"                               | false
        "9774"   | "Oberelsbach"                            | false
        "9775"   | "Schönau an der Brend"                   | false
        "9776"   | "Mellrichstadt"                          | false
        "9777"   | "Ostheim von der Rhön"                   | false
        "9778"   | "Fladungen"                              | false
        "9779"   | "Nordheim von der Rhön"                  | false
        "9802"   | "Ansbach Katterbach"                     | false
        "9803"   | "Colmberg"                               | false
        "9804"   | "Aurach"                                 | false
        "9805"   | "Burgoberbach"                           | false
        "981"    | "Ansbach"                                | false
        "9820"   | "Lehrberg"                               | false
        "9822"   | "Bechhofen an der Heide"                 | false
        "9823"   | "Leutershausen"                          | false
        "9824"   | "Dietenhofen"                            | false
        "9825"   | "Herrieden"                              | false
        "9826"   | "Weidenbach Mittelfranken"               | false
        "9827"   | "Lichtenau Mittelfranken"                | false
        "9828"   | "Rügland"                                | false
        "9829"   | "Flachslanden"                           | false
        "9831"   | "Gunzenhausen"                           | false
        "9832"   | "Wassertrüdingen"                        | false
        "9833"   | "Heidenheim Mittelfranken"               | false
        "9834"   | "Theilenhofen"                           | false
        "9835"   | "Ehingen Mittelfranken"                  | false
        "9836"   | "Gunzenhausen Cronheim"                  | false
        "9837"   | "Haundorf"                               | false
        "9841"   | "Bad Windsheim"                          | false
        "9842"   | "Uffenheim"                              | false
        "9843"   | "Burgbernheim"                           | false
        "9844"   | "Obernzenn"                              | false
        "9845"   | "Oberdachstetten"                        | false
        "9846"   | "Ipsheim"                                | false
        "9847"   | "Ergersheim"                             | false
        "9848"   | "Simmershofen"                           | false
        "9851"   | "Dinkelsbühl"                            | false
        "9852"   | "Feuchtwangen"                           | false
        "9853"   | "Wilburgstetten"                         | false
        "9854"   | "Wittelshofen"                           | false
        "9855"   | "Dentlein am Forst"                      | false
        "9856"   | "Dürrwangen"                             | false
        "9857"   | "Schopfloch Mittelfranken"               | false
        "9861"   | "Rothenburg ob der Tauber"               | false
        "9865"   | "Adelshofen Mittelfranken"               | false
        "9867"   | "Geslau"                                 | false
        "9868"   | "Schillingsfürst"                        | false
        "9869"   | "Wettringen Mittelfranken"               | false
        "9871"   | "Windsbach"                              | false
        "9872"   | "Heilsbronn"                             | false
        "9873"   | "Abenberg Wassermungenau"                | false
        "9874"   | "Neuendettelsau"                         | false
        "9875"   | "Wolframs Eschenbach"                    | false
        "9876"   | "Rohr Mittelfranken"                     | false
        "9901"   | "Hengersberg Bayern"                     | false
        "9903"   | "Schöllnach"                             | false
        "9904"   | "Lalling"                                | false
        "9905"   | "Bernried Niederbayern"                  | false
        "9906"   | "Mariaposching"                          | false
        "9907"   | "Zenting"                                | false
        "9908"   | "Schöfweg"                               | false
        "991"    | "Deggendorf"                             | false
        "9920"   | "Bischofsmais"                           | false
        "9921"   | "Regen"                                  | false
        "9922"   | "Zwiesel"                                | false
        "9923"   | "Teisnach"                               | false
        "9924"   | "Bodenmais"                              | false
        "9925"   | "Bayerisch Eisenstein"                   | false
        "9926"   | "Frauenau"                               | false
        "9927"   | "Kirchberg Wald"                         | false
        "9928"   | "Kirchdorf im Wald"                      | false
        "9929"   | "Ruhmannsfelden"                         | false
        "9931"   | "Plattling"                              | false
        "9932"   | "Osterhofen"                             | false
        "9933"   | "Wallersdorf"                            | false
        "9935"   | "Stephansposching"                       | false
        "9936"   | "Wallerfing"                             | false
        "9937"   | "Oberpöring"                             | false
        "9938"   | "Moos Niederbayern"                      | false
        "9941"   | "Kötzting"                               | false
        "9942"   | "Viechtach"                              | false
        "9943"   | "Lam Oberpfalz"                          | false
        "9944"   | "Miltach"                                | false
        "9945"   | "Arnbruck"                               | false
        "9946"   | "Hohenwarth bei Kötzing"                 | false
        "9947"   | "Neukirchen beim Heiligen Blut"          | true  // see https://issuetracker.google.com/issues/183383466
        "9948"   | "Eschlkam"                               | false
        "9951"   | "Landau an der Isar"                     | false
        "9952"   | "Eichendorf"                             | false
        "9953"   | "Pilsting"                               | false
        "9954"   | "Simbach Niederbayern"                   | false
        "9955"   | "Mamming"                                | false
        "9956"   | "Eichendorf Aufhausen"                   | false
        "9961"   | "Mitterfels"                             | false
        "9962"   | "Schwarzach Niederbayern"                | false
        "9963"   | "Konzell"                                | false
        "9964"   | "Stallwang"                              | false
        "9965"   | "Sankt Englmar"                          | false
        "9966"   | "Wiesenfelden"                           | false
        "9971"   | "Cham"                                   | false
        "9972"   | "Waldmünchen"                            | false
        "9973"   | "Furth im Wald"                          | false
        "9974"   | "Traitsching"                            | false
        "9975"   | "Waldmünchen Geigant"                    | false
        "9976"   | "Rötz"                                   | false
        "9977"   | "Arnschwang"                             | false
        "9978"   | "Schönthal Oberpfalz"                    | false
    }

}
