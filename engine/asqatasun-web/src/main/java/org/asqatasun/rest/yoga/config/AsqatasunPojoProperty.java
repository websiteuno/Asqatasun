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

import org.skyscreamer.yoga.selector.PojoProperty;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A PojoProperty which override original implementation to be able to use specific class as a "primitive" class
 * @author koj
 */
public class AsqatasunPojoProperty<T> extends PojoProperty<T> {

    private boolean primitive;

    public AsqatasunPojoProperty(PropertyDescriptor property) {
        super(property);
        Method readMethod = property.getReadMethod();
        primitive = LocalDateTime.class.isAssignableFrom(readMethod.getReturnType()) ||
        			LocalDate.class.isAssignableFrom(readMethod.getReturnType()) ||
        			Enum.class.isAssignableFrom(readMethod.getReturnType()) ;

    }

    @Override
    public boolean isPrimitive() {
        return super.isPrimitive() || primitive;
    }
}
