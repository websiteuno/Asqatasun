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
package org.asqatasun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan({
				"org.asqatasun.entity.service",
				"org.asqatasun.entity.dao",
				"org.asqatasun.entity.audit.factory",
				"org.asqatasun.entity.subject.factory",
				"org.asqatasun.entity.parameterization.factory",
				"org.asqatasun.entity.reference.factory",
				"org.asqatasun.entity.statistics.factory",
				"org.asqatasun.rest"})
@PropertySource("file://${confDir}/conf/asqatasun.properties")
public class AsqatasunRestServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsqatasunRestServerApplication.class, args);
	}
}
