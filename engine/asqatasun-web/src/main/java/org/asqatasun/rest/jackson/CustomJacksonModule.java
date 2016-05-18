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
package org.asqatasun.rest.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Custom module to register custom serializers.
 * @author koj
 *
 */
@Component
public class CustomJacksonModule extends Module {

//    @Autowired
//    List<JsonSerializer<?>> jsonSerializers;
//
//    @Autowired
//    List<JsonDeserializer<?>> jsonDeserializers;

//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	@Override
//    public void setupModule(SetupContext context) {
//        super.setupModule(context);
    	// Custom serializers
//        SimpleSerializers serializers = new SimpleSerializers();
//        for (JsonSerializer jsonSerializer : jsonSerializers) {
//        	Class<?> clazz = (Class<?>) ((ParameterizedType) jsonSerializer.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        	serializers.addSerializer(clazz, jsonSerializer);
//        }
//        context.addSerializers(serializers);
//
//        // Custom deserializers
//        SimpleDeserializers deserializers = new SimpleDeserializers();
//        for (JsonDeserializer jsonDeserializer : jsonDeserializers) {
//        	Class<?> clazz = (Class<?>) ((ParameterizedType) jsonDeserializer.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        	 deserializers.addDeserializer(clazz, jsonDeserializer);
//        }
//        context.addDeserializers(deserializers);
//    }

    @Override
    public Version version() {
        return new Version(0, 0, 1, "", "", "");
    }

    @Override
    public void setupModule(SetupContext setupContext) {

    }

    @Override
    public String getModuleName() {
        return "AsqatasunJacksonModule";
    }
}
