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
package org.asqatasun.rest.yoga.audit;

import org.asqatasun.entity.audit.AuditImpl;
import org.skyscreamer.yoga.configuration.YogaEntityConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Component
public class AuditYogaMapping extends YogaEntityConfiguration<AuditImpl> {

	@Override
	public Collection<String> getCoreFields() {
		return Arrays.asList(new String[] { "id", "status"});
	}

	@Override
	public Collection<String> getSelectableFields() {
		Collection<String> fields = new ArrayList<>(getCoreFields());
		return fields;
	}
}
