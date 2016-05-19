/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2016  Asqatasun.org
 *
 * This file is part of Asqatasun.
 *
 * Asqatasun is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: asqatasun AT asqatasun DOT org
 */
package org.asqatasun.webapp.config;

import org.asqatasun.webapp.form.builder.SelectElementBuilder;
import org.asqatasun.webapp.form.builder.SelectFormFieldBuilder;
import org.asqatasun.webapp.form.builder.TextualFormFieldBuilder;
import org.asqatasun.webapp.ui.form.builder.SelectElementBuilderImpl;
import org.asqatasun.webapp.ui.form.builder.SelectFormFieldBuilderImpl;
import org.asqatasun.webapp.ui.form.builder.TextualFormFieldBuilderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by meskoj on 16/05/16.
 */
@Configuration
@PropertySource("classpath:asqatasun-form.properties")
public class FormConfig {

//    @Value("${alphabeticalContractSelectElementBuilder}")
//    Map<String, String> alphabeticalContractSelectElementBuilderMap;
//    @Value("${markContractSelectElementBuilder}")
//    Map<String, String> markContractSelectElementBuilderMap;
//    @Value("${dateContractSelectElementBuilder}")
//    Map<String, String> dateContractSelectElementBuilderMap;
//    @Value("${displayContractElementSelectFormFieldBuilder}")
//    Map<String, String> displayContractElementSelectFormFieldBuilderMap;
//    @Value("${ascendingContractSelectElementBuilder}")
//    Map<String, String> ascendingContractSelectElementBuilderMap;
//    @Value("${descendingContractSelectElementBuilder}")
//    Map<String, String> descendingContractSelectElementBuilderMap;
//    @Value("${displayContractOrderSelectFormFieldBuilder}")
//    Map<String, String> displayContractOrderSelectFormFieldBuilderMap;
//    @Value("${inclusionRegexpContractFormFieldBuilder}")
//    Map<String, String> inclusionRegexpContractFormFieldBuilderMap;
//    @Value("${exclusionRegexpContractFormFieldBuilder}")
//    Map<String, String> exclusionRegexpContractFormFieldBuilderMap;
//
//    @Bean(name="markContractSelectElementBuilder")
//    public SelectElementBuilder getMarkContractSelectElementBuilder() {
//        return buildSelectElementBuilder(markContractSelectElementBuilderMap);
//    }
//
//    @Bean(name="alphabeticalContractSelectElementBuilder")
//    public SelectElementBuilder getAlphabeticalContractSelectElementBuilder() {
//        return buildSelectElementBuilder(alphabeticalContractSelectElementBuilderMap);
//    }
//
//    @Bean(name="dateContractSelectElementBuilder")
//    public SelectElementBuilder getDateContractSelectElementBuilder() {
//        return buildSelectElementBuilder(dateContractSelectElementBuilderMap);
//    }
//
//    @Bean(name="ascendingContractSelectElementBuilder")
//    public SelectElementBuilder getAscendingContractSelectElementBuilder() {
//        return buildSelectElementBuilder(ascendingContractSelectElementBuilderMap);
//    }
//
//    @Bean(name="descendingContractSelectElementBuilder")
//    public SelectElementBuilder getDescendingContractSelectElementBuilder() {
//        return buildSelectElementBuilder(descendingContractSelectElementBuilderMap);
//    }
//
//    @Bean(name="displayContractElementSelectFormFieldBuilder")
//    public SelectFormFieldBuilder getDisplayContractElementSelectFormFieldBuilder() {
//        return buildSelectFormFieldBuilder(
//                displayContractElementSelectFormFieldBuilderMap,
//                getSortByChoice()
//        );
//    }
//
//    @Bean(name="displayContractOrderSelectFormFieldBuilder")
//    public SelectFormFieldBuilder getDisplayContractOrderSelectFormFieldBuilder() {
//        return buildSelectFormFieldBuilder(
//                displayContractOrderSelectFormFieldBuilderMap,
//                getSortByChoice()
//        );
//    }
//
//    @Bean(name="exclusionRegexpContractFormFieldBuilder")
//    public TextualFormFieldBuilder getExclusionRegexpContractFormFieldBuilder() {
//        return buildTextualFormFieldBuilder(exclusionRegexpContractFormFieldBuilderMap);
//    }
//
//    @Bean(name="inclusionRegexpContractFormFieldBuilder")
//    public TextualFormFieldBuilder getInclusionRegexpContractFormFieldBuilder() {
//        return buildTextualFormFieldBuilder(inclusionRegexpContractFormFieldBuilderMap);
//    }
//
//    @Bean
//    Map<String, List<SelectElementBuilder>> getOrderChoice() {
//        Map<String, List<SelectElementBuilder>> map = new HashMap<>();
//        List<SelectElementBuilder> list = new ArrayList<>();
//        list.add(getAscendingContractSelectElementBuilder());
//        list.add(getDescendingContractSelectElementBuilder());
//        map.put("order-choice", list);
//        return map;
//    }
//
//    @Bean
//    Map<String, List<SelectElementBuilder>> getSortByChoice() {
//        Map<String, List<SelectElementBuilder>> map = new HashMap<>();
//        List<SelectElementBuilder> list = new ArrayList<>();
//        list.add(getMarkContractSelectElementBuilder());
//        list.add(getAlphabeticalContractSelectElementBuilder());
//        list.add(getDateContractSelectElementBuilder());
//        map.put("sort-by-choice", list);
//        return map;
//    }
//
//    private static TextualFormFieldBuilder buildTextualFormFieldBuilder(
//            Map<String, String> builderMap) {
//        TextualFormFieldBuilder elementBuilder = new TextualFormFieldBuilderImpl();
//        if (builderMap.containsKey("value")) {
//            elementBuilder.setValue(builderMap.get("value"));
//        }
//        if (builderMap.containsKey("i18nKey")) {
//            elementBuilder.setI18nKey(builderMap.get("i18nKey"));
//        }
//        if (builderMap.containsKey("errorI18nKey")) {
//            elementBuilder.setErrorI18nKey(builderMap.get("errorI18nKey"));
//        }
//        if (builderMap.containsKey("errorI18nKey")) {
//            elementBuilder.setErrorI18nKey(builderMap.get("errorI18nKey"));
//        }
//        if (builderMap.containsKey("value")) {
//            elementBuilder.setValue(builderMap.get("value"));
//        }
//        return elementBuilder;
//    }
//
//    private static SelectFormFieldBuilder buildSelectFormFieldBuilder(
//            Map<String, String> builderMap,
//            Map<String, List<SelectElementBuilder>> selectElementBuilderMap) {
//        SelectFormFieldBuilder elementBuilder = new SelectFormFieldBuilderImpl();
//        if (builderMap.containsKey("value")) {
//            elementBuilder.setValue(builderMap.get("value"));
//        }
//        if (builderMap.containsKey("i18nKey")) {
//            elementBuilder.setI18nKey(builderMap.get("i18nKey"));
//        }
//        if (builderMap.containsKey("errorI18nKey")) {
//            elementBuilder.setErrorI18nKey(builderMap.get("errorI18nKey"));
//        }
//        if (builderMap.containsKey("errorI18nKey")) {
//            elementBuilder.setErrorI18nKey(builderMap.get("errorI18nKey"));
//        }
//        if (builderMap.containsKey("value")) {
//            elementBuilder.setValue(builderMap.get("value"));
//        }
//        elementBuilder.setSelectElementBuilderMap(selectElementBuilderMap);
//        return elementBuilder;
//    }
//
//    private static SelectElementBuilder buildSelectElementBuilder(
//            Map<String, String> builderMap) {
//        SelectElementBuilder elementBuilder = new SelectElementBuilderImpl();
//        if (builderMap.containsKey("value")) {
//            elementBuilder.setValue(builderMap.get("value"));
//        }
//        if (builderMap.containsKey("i18nKey")) {
//            elementBuilder.setI18nKey(builderMap.get("i18nKey"));
//        }
//        if (builderMap.containsKey("errorI18nKey")) {
//            elementBuilder.setErrorI18nKey(builderMap.get("errorI18nKey"));
//        }
//        if (builderMap.containsKey("errorI18nKey")) {
//            elementBuilder.setErrorI18nKey(builderMap.get("errorI18nKey"));
//        }
//        if (builderMap.containsKey("default")) {
//            elementBuilder.setDefault(Boolean.valueOf(builderMap.get("default")));
//        }
//        if (builderMap.containsKey("enabled")) {
//            elementBuilder.setEnabled(Boolean.valueOf(builderMap.get("enabled")));
//        }
//        if (builderMap.containsKey("value")) {
//            elementBuilder.setValue(builderMap.get("value"));
//        }
//        return elementBuilder;
//    }



}
