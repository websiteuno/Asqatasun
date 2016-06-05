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

import org.asqatasun.webapp.command.factory.*;
import org.asqatasun.webapp.command.helper.ContractSortCommandHelper;
import org.asqatasun.webapp.dto.factory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by meskoj on 16/05/16.
 */
@Configuration
public class SingletonConfig  {

    @Bean (name = "createContractCommandFactory")
    static CreateContractCommandFactory createContractCommandFactory() {
        return CreateContractCommandFactory.getInstance();
    }

    @Bean (name = "changeTestWeightCommandFactory")
    static ChangeTestWeightCommandFactory changeTestWeightCommandFactory() {
        return ChangeTestWeightCommandFactory.getInstance();
    }

    @Bean (name = "createUserCommandFactory")
    static CreateUserCommandFactory createUserCommandFactory() {
        return CreateUserCommandFactory.getInstance();
    }

    @Bean (name = "auditSetUpCommandFactory")
    static AuditSetUpCommandFactory auditSetUpCommandFactory() {
        return AuditSetUpCommandFactory.getInstance();
    }

    @Bean (name = "AuditResultSortCommandFactory")
    static AuditResultSortCommandFactory auditResultSortCommandFactory() {
        return AuditResultSortCommandFactory.getInstance();
    }

    @Bean (name = "contractSortCommandHelper")
    static ContractSortCommandHelper contractSortCommandHelper() {
        return ContractSortCommandHelper.getInstance();
    }

    @Bean (name = "actInfoFactory")
    static ActInfoFactory actInfoFactory() {
        return ActInfoFactory.getInstance();
    }

    @Bean (name = "auditStatisticsFactory")
    static AuditStatisticsFactory auditStatisticsFactory() {
        return AuditStatisticsFactory.getInstance();
    }

    @Bean (name = "contractInfoFactory")
    static ContractInfoFactory contractInfoFactory() {
        return ContractInfoFactory.getInstance();
    }

    @Bean (name = "criterionResultFactory")
    static CriterionResultFactory criterionResultFactory() {
        return CriterionResultFactory.getInstance();
    }

    @Bean (name = "detailedContractInfoFactory")
    static DetailedContractInfoFactory detailedContractInfoFactory() {
        return DetailedContractInfoFactory.getInstance();
    }

    @Bean (name = "testResultFactory")
    static TestResultFactory testResultFactoryFactory() {
        return TestResultFactory.getInstance();
    }
}
