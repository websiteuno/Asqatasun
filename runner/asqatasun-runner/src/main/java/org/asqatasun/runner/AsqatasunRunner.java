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
package org.asqatasun.runner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.asqatasun.entity.audit.Audit;
import org.asqatasun.entity.audit.EvidenceElement;
import org.asqatasun.entity.audit.ProcessRemark;
import org.asqatasun.entity.audit.ProcessResult;
import org.asqatasun.entity.parameterization.Parameter;
import org.asqatasun.entity.parameterization.ParameterElement;
import org.asqatasun.entity.service.audit.AuditDataService;
import org.asqatasun.entity.service.audit.ProcessRemarkDataService;
import org.asqatasun.entity.service.audit.ProcessResultDataService;
import org.asqatasun.entity.service.parameterization.ParameterDataService;
import org.asqatasun.entity.service.parameterization.ParameterElementDataService;
import org.asqatasun.entity.service.statistics.WebResourceStatisticsDataService;
import org.asqatasun.entity.service.subject.WebResourceDataService;
import org.asqatasun.entity.subject.Site;
import org.asqatasun.entity.subject.WebResource;
import org.asqatasun.service.AuditService;
import org.asqatasun.service.AuditServiceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class launches Asqatasun with urls passed as arguments by the user.
 *
 * @author jkowalczyk
 */

@Component
public class AsqatasunRunner implements AuditServiceListener {

    private static final String AW22_REF = "Aw22";
    private static final String RGAA22_REF = "Rgaa22";
    private static final String RGAA30_REF = "Rgaa30";
    private static String REF = AW22_REF;

    private static final String BRONZE_LEVEL = "Bz";
    private static final String A_LEVEL = "A";
    private static final String SILVER_LEVEL = "Ar";
    private static final String AA_LEVEL = "AA";
    private static final String GOLD_LEVEL = "Or";
    private static final String AAA_LEVEL = "AAA";

    private static final String LEVEL_1 = "LEVEL_1";
    private static final String LEVEL_2 = "LEVEL_2";
    private static final String LEVEL_3 = "LEVEL_3";

    private static final String LEVEL_PARAMETER_ELEMENT_CODE = "LEVEL";

    @Autowired
    private AuditService auditService;
    @Autowired
    private AuditDataService auditDataService;
    @Autowired
    private WebResourceDataService webResourceDataService;
    @Autowired
    private WebResourceStatisticsDataService webResourceStatisticsDataService;
    @Autowired
    private ProcessResultDataService processResultDataService;
    @Autowired
    private ProcessRemarkDataService processRemarkDataService;
    @Autowired
    private ParameterDataService parameterDataService;
    @Autowired
    private ParameterElementDataService parameterElementDataService;

    public AsqatasunRunner() {
        super();
    }

    public void runAuditOnline(String[] urlTab, String ref, String level) {
        Logger.getLogger(this.getClass()).info("runAuditOnline");
        initServices();

        Set<Parameter> paramSet = parameterDataService.getParameterSetFromAuditLevel(ref, level);

        List<String> pageUrlList = Arrays.asList(urlTab);

        if (pageUrlList.size() > 1) {
            auditService.auditSite("site:" + pageUrlList.get(0), pageUrlList, paramSet);
        } else {
            auditService.auditPage(pageUrlList.get(0), parameterDataService.getAuditPageParameterSet(paramSet));
        }
    }

    public void runAuditScenario(String scenarioFilePath, String ref, String level) {
        initServices();

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
    
    public void runAuditUpload(String[] uploadFilePath, String ref, String level) {
        initServices();

        Set<Parameter> paramSet = parameterDataService.getParameterSetFromAuditLevel(ref, level);

        Map<String, String> fileMap = new HashMap<>();
        for (String file : Arrays.asList(uploadFilePath)) {
            File uploadFile = new File(file);
            try {
               fileMap.put(uploadFile.getName(), readFile(uploadFile));
            } catch (IOException ex) {
                System.out.println("Unreadable upload file");
                System.exit(0);
            }
        }
        auditService.auditPageUpload(fileMap, paramSet);
    }

    @Override
    public void auditCompleted(Audit audit) {
        audit = auditDataService.read(audit.getId());
        List<ProcessResult> processResultList = (List<ProcessResult>) processResultDataService.getNetResultFromAudit(audit);

        System.out.println("Audit terminated with success at " + audit.getDateOfCreation());
        System.out.println("Audit Id : " + audit.getId());
        System.out.println("");
        System.out.println("RawMark : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getRawMark() + "%");
        System.out.println("WeightedMark : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getMark() + "%");
        System.out.println("Nb Passed : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getNbOfPassed());
        System.out.println("Nb Failed test : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getNbOfInvalidTest());
        System.out.println("Nb Failed occurences : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getNbOfFailedOccurences());
        System.out.println("Nb Pre-qualified : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getNbOfNmi());
        System.out.println("Nb Not Applicable : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getNbOfNa());
        System.out.println("Nb Not Tested : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(audit.getSubject()).getNbOfNotTested());

        if (audit.getSubject() instanceof Site) {
            int numberOfChildWebResource = webResourceDataService.getNumberOfChildWebResource(audit.getSubject()).intValue();
            for (int i = 0; i < numberOfChildWebResource; i++) {
                displayWebResourceResult(webResourceDataService.getWebResourceFromItsParent(audit.getSubject(), i, 1).iterator().next(),
                        processResultList);
            }
        } else {
            displayWebResourceResult(audit.getSubject(), processResultList);
        }
        System.out.println("");
        System.exit(0);
    }

    private void displayWebResourceResult(WebResource wr, List<ProcessResult> processResultList) {
        System.out.println("");
        System.out.println("Subject : " + wr.getURL());
        List<ProcessResult> prList = new ArrayList<>();
        for (ProcessResult netResult : processResultList) {
            if (netResult.getSubject().getURL().equalsIgnoreCase(wr.getURL())) {
                prList.add(netResult);
            }
        }
        System.out.println("RawMark : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getRawMark() + "%");
        System.out.println("WeightedMark : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getMark() + "%");
        System.out.println("Nb Passed : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getNbOfPassed());
        System.out.println("Nb Failed test : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getNbOfInvalidTest());
        System.out.println("Nb Failed occurences : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getNbOfFailedOccurences());
        System.out.println("Nb Pre-qualified : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getNbOfNmi());
        System.out.println("Nb Not Applicable : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getNbOfNa());
        System.out.println("Nb Not Tested : " + webResourceStatisticsDataService.getWebResourceStatisticsByWebResource(wr).getNbOfNotTested());
        
        Collections.sort(prList, new Comparator<ProcessResult>() {
            @Override
            public int compare(ProcessResult t, ProcessResult t1) {
                return t.getTest().getId().compareTo(t1.getTest().getId());
            }
        }) ;
        for (ProcessResult result : prList) {
            System.out.println(result.getTest().getCode() + ": " + result.getValue());
            Set<ProcessRemark> processRemarkList = (Set<ProcessRemark>) processRemarkDataService.findProcessRemarksFromProcessResult(result, -1);
            for (ProcessRemark processRemark : processRemarkList) {
                System.out.println(" ->  " + processRemark.getIssue()
                        + " " + processRemark.getMessageCode());
                for (EvidenceElement el : processRemark.getElementList()) {
                    System.out.println("    -> " + el.getEvidence().getCode() + ":" + el.getValue());
                }
            }
        }
    }

    @Override
    public void auditCrashed(Audit audit, Exception exception) {
        exception.printStackTrace();
        System.out.println("crash (id+message): " + audit.getId() + " " + exception.fillInStackTrace());
    }

    /**
     * The default parameter set embeds a depth value that corresponds to the
     * site audit. We need here to replace this parameter by a parameter value
     * equals to 0.
     *
     * @return
     */
    private Set<Parameter> getAuditPageParameterSet(Set<Parameter> defaultParameterSet) {
        ParameterElement parameterElement = parameterElementDataService.getParameterElement("DEPTH");
        Parameter depthParameter = parameterDataService.getParameter(parameterElement, "0");
        Set<Parameter> auditPageParamSet = parameterDataService.updateParameter(
                defaultParameterSet, depthParameter);
        return auditPageParamSet;
    }

    /**
     *
     */
    private void initServices() {
        auditService.add(this);
    }

//    /**
//     *
//     * @param ref
//     * @param level
//     * @return
//     */
//    private Set<Parameter> getParameterSetFromAuditLevel(String ref, String level) {
//        if (ref.equalsIgnoreCase(RGAA22_REF) || ref.equalsIgnoreCase(RGAA30_REF)) {
//            if (level.equalsIgnoreCase(BRONZE_LEVEL)) {
//                level=A_LEVEL;
//            } else if (level.equalsIgnoreCase(SILVER_LEVEL)) {
//                level=AA_LEVEL;
//            } else if (level.equalsIgnoreCase(GOLD_LEVEL)) {
//                level=AAA_LEVEL;
//            }
//        }
//        if (level.equalsIgnoreCase(BRONZE_LEVEL) || level.equalsIgnoreCase(A_LEVEL)) {
//            level=LEVEL_1;
//        } else if (level.equalsIgnoreCase(SILVER_LEVEL) || level.equalsIgnoreCase(AA_LEVEL)) {
//            level=LEVEL_2;
//        } else if (level.equalsIgnoreCase(GOLD_LEVEL) || level.equalsIgnoreCase(AAA_LEVEL)) {
//            level=LEVEL_3;
//        }
//        ParameterElement levelParameterElement = parameterElementDataService.getParameterElement(LEVEL_PARAMETER_ELEMENT_CODE);
//        Parameter levelParameter = parameterDataService.getParameter(levelParameterElement, ref + ";" + level);
//        Set<Parameter> paramSet = parameterDataService.getDefaultParameterSet();
//        return parameterDataService.updateParameter(paramSet, levelParameter);
//    }
    
    /**
     * 
     * @return
     */
    private String readFile(File file) throws IOException {
      // #57 issue quick fix.......
        return FileUtils.readFileToString(file).replace("\"formatVersion\": 2", "\"formatVersion\":1")
                                               .replace("\"formatVersion\":2", "\"formatVersion\":1");
    }

}
