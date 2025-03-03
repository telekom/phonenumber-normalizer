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
package de.telekom.phonenumbernormalizer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;


import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Concrete implementation of {@link PhoneNumberAreaLabel}, which is using per default provided label configuration from resources folder:
 * <ul>
 *     <li>Country Calling Code to German Country Names</li>
 *     <li>AU-NDC: "Weihnachtsinsel" &amp; "Kokosinseln"</li>
 *     <li>DE-NDC: German City-Names replacing formal abbreviation with long name</li>
 *     <li>RU-NDC: Country seperation "Russland" &amp; "Kasachstan"</li>
 *     <li>US-NDC: For US and CA just the state names</li>
 * </ul>
 */
@RequiredArgsConstructor
@Component
public class PhoneNumberAreaLabelImpl implements PhoneNumberAreaLabel {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberAreaLabelImpl.class);

    /**
     * Array of full (path + filename + extension) files, where:<br/>
     * filename: matches ISO2 country code of country
     * content: JSON array with one object. Each key is a phone number prefix (similar to NDC, but without NAC) and the value its corresponding label. If keys have overlapping, the one with the longest key will be used.
     */
    @Value("classpath:${service.areaLabel.nationalLabels}")
    Resource[] numberPlanResources;

    /**
     * full (path + filename + extension) file, where:<br/>
     * content: JSON array with one object. Each key is the Country Calling Code (without "+" or IDP) and the value its corresponding label.
     */
    @Value("classpath:${service.areaLabel.countryLabels}")
    Resource countryCodeResource;


    /**
     * First key is the region code represented by an ISO2 country code of (the main) country.<br/>
     * Second key (key of the inner HashMap) is the phone number prefix (similar to NDC, but without NAC).<br/>
     * Value of the inner HashMap is corresponding label.
     *
     * @see PhoneNumberAreaLabelImpl#numberPlanResources
     *
     */
    private HashMap<String, HashMap<?, ?>> areaCodes;


    /**
     * Each key is the Country Calling Code (without "+" or IDP) and the value its corresponding label.
     *
     * @see PhoneNumberAreaLabelImpl#countryCodeResource
     */
    private HashMap<?, ?> internationalCountryCodes;

    /**
     * <ul>
     * <li>Loading {@link PhoneNumberAreaLabelImpl#internationalCountryCodes} from {@link PhoneNumberAreaLabelImpl#countryCodeResource}</li>
     * <li>Loading {@link PhoneNumberAreaLabelImpl#areaCodes} from {@link PhoneNumberAreaLabelImpl#numberPlanResources}</li>
     * </ul>
     */
    @PostConstruct
    public void initFile() {

        ClassLoader cl = this.getClass().getClassLoader();
        // if no resources are given, the default once are used:
        if (countryCodeResource == null) {
            countryCodeResource = new ClassPathResource("arealabels/international_country_codes.json", cl);
        }

        if (numberPlanResources == null || numberPlanResources.length==0) {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
            try {
                numberPlanResources = resolver.getResources("classpath:arealabels/nationallabels/*.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            LOGGER.debug("init code files");
            LOGGER.debug("read international country codes");
            this.internationalCountryCodes = this.initResource(countryCodeResource);
            LOGGER.debug("read number plans folder");
            this.areaCodes = new HashMap<>();
            for (Resource res : numberPlanResources) {
                String filename = res.getFilename();
                if (filename!=null) {
                    LOGGER.debug("read number plan file: {}", filename);
                    String prefix = this.getFilePrefix(filename).toUpperCase(Locale.ROOT);
                    LOGGER.debug("add prefix: {}", prefix);
                    this.areaCodes.put(prefix, this.initResource(res));
                }
            }
        } catch (Exception e) {
            LOGGER.error("init file failed");
            LOGGER.error("{}", e.getMessage());
        }
    }

    @Override
    public Optional<String> getLocationByNationalNumberAndRegionCode(String nationalNumber, String regionCode) {
        regionCode = regionCode.toUpperCase(Locale.ROOT);
        if (Objects.nonNull(this.areaCodes) && !this.areaCodes.containsKey(regionCode)) {
            LOGGER.debug("no number plan for regioncode: {} available", regionCode);
            return Optional.empty();
        }
        String locationName = this.getLabelForNDCasNumberPrefixinPlan(nationalNumber, this.areaCodes.get(regionCode));

        return Optional.ofNullable(locationName);
    }

    @Override
    public Optional<String> getCountryNameByCountryCode(String countryCode) {
        if (Objects.isNull(this.internationalCountryCodes)) {
            return Optional.empty();
        }
        return Optional.ofNullable((String) this.internationalCountryCodes.get(countryCode));
    }

    @Override
    public Optional<String> getLocationByE164Number(String e164number) {
        // be sure number is E164 normalized (leading +) ... and not fallback to dialable, where area information might be missing
        String resultLabel = null;
        if ((e164number.length()>0) && (e164number.charAt(0) == '+')) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber pn = phoneUtil.parse(e164number, "");

                Optional<String> locationName = Optional.empty();

                if (pn!=null) {
                    String regionCode=phoneUtil.getRegionCodeForCountryCode(pn.getCountryCode());
                    locationName = this.getLocationByNationalNumberAndRegionCode(String.valueOf(pn.getNationalNumber()), regionCode);
                    if (locationName.isEmpty()) {
                        return this.getCountryNameByCountryCode(String.valueOf(pn.getCountryCode()));
                    }
                }

                return locationName;
            } catch (NumberParseException e) {
                LOGGER.warn("could not parse normalize number: {}", e164number);
                LOGGER.warn(e.getMessage(), e);
                // removing leading "+"
                String tooShortNumber = e164number.substring(1);
                for (int i=tooShortNumber.length();i>0;i--)
                {
                    Optional<String> tempResult = this.getCountryNameByCountryCode(tooShortNumber.substring(0,i));
                    if (tempResult.isPresent()) {
                        resultLabel = tempResult.get();
                    }
                }
            }
        }
        return Optional.ofNullable(resultLabel);
    }

    /**
     * Returns the "main" file name - before the first ".", to exclude the extension.
     * @param filename where the extension should be removed
     * @return part before the first "."
     */
    private String getFilePrefix(String filename) {
        return filename.split(Pattern.quote("."))[0];
    }

    /**
     * Reads a resource (JSON Array of one Object into a HashMap of that object attributes).
     * @param res JSON to be loaded
     * @return Number prefix mapped to label
     * @throws IOException if there is a problem with the given resource
     */
    private HashMap<?, ?> initResource(Resource res) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return (HashMap<?, ?>) mapper.readValue(res.getInputStream(), List.class).get(0);
    }

    /**
     * Returns the label (value) for a NDC, if the number in national format starts with it.<br/>
     * If multiple NAC matches as number prefix, the longest one will be taken.
     * @param nationalNumber without NAC
     * @param plan maps NDC to Label
     * @return a label specifying the NDC location / area (might be city, state or country)
     */
    private String getLabelForNDCasNumberPrefixinPlan(String nationalNumber, Map<?, ?> plan) {
        for (int i = nationalNumber.length(); i > 0; i--) {
            String key = nationalNumber.substring(0, i);
            if (plan.containsKey(key)) {
                return plan.get(key).toString();
            }
        }
        return null;
    }
}
