/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2015  Asqatasun.org
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
package org.asqatasun.rest.config;

import org.asqatasun.rest.jackson.CustomJacksonModule;
import org.asqatasun.rest.yoga.config.AsqatasunCoreSelector;
import org.asqatasun.rest.yoga.config.AsqatasunJsonSelectorView;
import org.skyscreamer.yoga.configuration.DefaultEntityConfigurationRegistry;
import org.skyscreamer.yoga.configuration.YogaEntityConfiguration;
import org.skyscreamer.yoga.listener.RenderingListenerRegistry;
import org.skyscreamer.yoga.selector.CoreSelector;
import org.skyscreamer.yoga.selector.SelectorResolver;
import org.skyscreamer.yoga.selector.parser.AliasSelectorResolver;
import org.skyscreamer.yoga.selector.parser.DynamicPropertyResolver;
import org.skyscreamer.yoga.selector.parser.GDataSelectorParser;
import org.skyscreamer.yoga.selector.parser.SelectorParser;
import org.skyscreamer.yoga.springmvc.view.YogaSpringView;
import org.skyscreamer.yoga.view.JsonSelectorView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Yoga configuration, whose aims is to replace Dto by defining customs views of model objects.
 * @author koj
 *
 */
@Configuration
public class YogaConfig {

	@Autowired
	List<YogaEntityConfiguration<?>> entitiesConfig;

	@Autowired
	CustomJacksonModule customModule;

	@Bean
	public RenderingListenerRegistry renderingListenerRegistry() {
		return new RenderingListenerRegistry();
	}

	@Bean
	public DefaultEntityConfigurationRegistry entityConfigurationRegistry() {
		return new DefaultEntityConfigurationRegistry(entitiesConfig);
	}

	@Bean
	public AliasSelectorResolver aliasSelectorResolver() {
		DynamicPropertyResolver dynamicPropertyResolver = new DynamicPropertyResolver();
		try {
			dynamicPropertyResolver.setPropertyFile(new ClassPathResource("yoga-aliases.properties").getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dynamicPropertyResolver;
	}

	@Bean
	public SelectorParser selectorParser() {
		GDataSelectorParser parser = new GDataSelectorParser();
		parser.setAliasSelectorResolver(aliasSelectorResolver());
		parser.setDisableExplicitSelectors(true);
		return parser;
	}

	@Bean
	public CoreSelector coreSelector() {
		CoreSelector selector = new AsqatasunCoreSelector();
		selector.setEntityConfigurationRegistry(entityConfigurationRegistry());
		return selector;
	}

    @Bean
    public SelectorResolver selectorResolver() {
    	SelectorResolver resolver = new SelectorResolver();
    	resolver.setStarResolvesToAll(true);
    	resolver.setSelectorParameterName("view");
    	resolver.setBaseSelector(coreSelector());
    	resolver.setSelectorParser(selectorParser());
    	return resolver;
    }

    @Bean
    public JsonSelectorView jsonView() {
		AsqatasunJsonSelectorView view = new AsqatasunJsonSelectorView();
    	view.setRegistry(renderingListenerRegistry());
    	view.setSelectorResolver(selectorResolver());
    	view.registerModule(customModule);
    	return view;
    }

    @Bean
    public ContentNegotiatingViewResolver viewResolver() {
    	ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
    	resolver.setOrder(1);
    	List<View> defaultViews = new ArrayList<>();
    	YogaSpringView yogaView = new YogaSpringView();
    	yogaView.setYogaView(jsonView());
    	defaultViews.add(yogaView);
    	resolver.setDefaultViews(defaultViews);
    	return resolver;
    }
}

