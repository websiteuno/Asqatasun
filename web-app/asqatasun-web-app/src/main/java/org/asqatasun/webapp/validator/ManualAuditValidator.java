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
package org.asqatasun.webapp.validator;

import java.util.List;

import org.asqatasun.entity.audit.DefiniteResultImpl;
import org.asqatasun.entity.audit.ProcessResult;
import org.asqatasun.webapp.command.ManualAuditCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
@Component("manualAuditValidator")
public class ManualAuditValidator implements org.springframework.validation.Validator {

    private static final String GENERAL_ERROR_MSG_KEY = "generalErrorMsg";
    private static final String RESULT_ABSENCE_VALUES = 
            "edit-contract.absenceManualVAlues";
    private static final String RESULT_ABSENCE_VALUES_OVER_TEN = 
            "edit-contract.absenceManualVAluesOverTen";

    @Override
    public boolean supports(Class<?> clazz) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ManualAuditCommand manualAuditCommand = (ManualAuditCommand) target;
        checkAuditManuelExist(manualAuditCommand.getProcessResultList(), errors);
    }

    private boolean checkAuditManuelExist(List<ProcessResult> allProcessResultList,
            Errors errors) {
        StringBuilder msg = new StringBuilder();
        int i = 0;
        boolean isExitStatut = true;
        for (ProcessResult testResultImpl : allProcessResultList) {
            DefiniteResultImpl definiteResult = (DefiniteResultImpl) testResultImpl;
            if (definiteResult.getManualDefiniteValue() == null) {
                isExitStatut = false;
                if (i < 11) {
                    msg.append(testResultImpl.getTest().getLabel());
                    msg.append("/");
                }
                i++;

            }
        }
        String[] arg = {msg.toString()};

        if (i > 0 && i < 11) {
            errors.rejectValue(GENERAL_ERROR_MSG_KEY, RESULT_ABSENCE_VALUES, arg, "{0}");
        } else if (i != 0) {
            errors.rejectValue(GENERAL_ERROR_MSG_KEY, RESULT_ABSENCE_VALUES_OVER_TEN, arg, "{0}");
        }

        return isExitStatut;
    }

}
