/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2017  Asqatasun.org
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
package org.asqatasun.rest.controller.audit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.asqatasun.entity.audit.Audit;
import org.asqatasun.entity.parameterization.Parameter;
import org.asqatasun.entity.service.parameterization.ParameterDataService;
import org.asqatasun.rest.controller.EntityController;
import org.asqatasun.rest.request.PageAuditRequest;
import org.asqatasun.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Created by meskoj on 17/05/16.
 */
@RestController
@RequestMapping(value="/audits")
public class AuditController extends EntityController<Audit> {

    @Autowired
    AuditService auditService;

    @Autowired
    ParameterDataService parameterDataService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value="/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void auditPage(@RequestBody final PageAuditRequest auditRequest) {
        runAuditOnline(new String[]{auditRequest.url}, auditRequest.referential, auditRequest.level);
//        return new AuditImpl();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value="/runS", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void auditScenario(@RequestBody final ScenarioAuditRequest auditRequest) {
        runAuditScenario(auditRequest.scenarioContent, auditRequest.referential, auditRequest.level);
    }

    private void runAuditOnline(String[] urlTab, String ref, String level) {
        Logger.getLogger(this.getClass()).info("runAuditOnline");

        Set<Parameter> paramSet = parameterDataService.getParameterSetFromAuditLevel(ref, level);

        List<String> pageUrlList = Arrays.asList(urlTab);

        if (pageUrlList.size() > 1) {
            auditService.auditSite("site:" + pageUrlList.get(0), pageUrlList, paramSet);
        } else {
            auditService.auditPage(pageUrlList.get(0), parameterDataService.getAuditPageParameterSet(paramSet));
        }
    }

    public void runAuditScenario(String scenarioFilePath, String ref, String level) {

        Set<Parameter> paramSet = parameterDataService.getParameterSetFromAuditLevel(ref, level);
        System.out.println(scenarioFilePath);
        File scenarioFile = new File(scenarioFilePath);
        try {
            auditService.auditScenario(scenarioFile.getName(), readFile(scenarioFile), paramSet);
        } catch (IOException ex) {
            System.out.println("Unreadable scenario file");
            System.exit(0);
        }
    }

    /**
     *
     * @return
     */
    private String readFile(File file) throws IOException {
        // #57 issue quick fix.......
        return FileUtils.readFileToString(file).replace("\"formatVersion\": 2", "\"formatVersion\":1")
                .replace("\"formatVersion\":2", "\"formatVersion\":1");
    }

    public class ScenarioAuditRequest {
        public String referential;
        public String level;
        public String scenarioContent;
    }
}
