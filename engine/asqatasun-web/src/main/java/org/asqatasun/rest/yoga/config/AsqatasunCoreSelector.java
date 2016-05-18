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
package org.asqatasun.rest.yoga.config;

import org.skyscreamer.yoga.selector.CoreSelector;
import org.skyscreamer.yoga.selector.Property;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Core selector for winfield to use our own PojoProprety to be able to use Joda {@link LocalDateTime} as a "primitive" type
 *
 * @author koj
 */
public class AsqatasunCoreSelector extends CoreSelector {

    /**
     * This method is overriden to create our own PojoProperty.<br>
     * This is a copy of the original method
     */
    @Override
    protected <T> Property<T> createProperty(Collection<Property<T>> properties, Class<T> instanceType,
            PropertyDescriptor desc) {
        if (properties != null) {
            for (Property<T> property : properties) {
                if (property.name().equals(desc.getName())) {
                    return property;
                }
            }
        }
        return new AsqatasunPojoProperty<T>(desc);
    }

}
