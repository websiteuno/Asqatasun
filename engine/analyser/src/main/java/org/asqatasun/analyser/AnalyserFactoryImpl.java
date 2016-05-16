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
package org.asqatasun.analyser;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.asqatasun.entity.audit.Audit;
import org.asqatasun.entity.parameterization.Parameter;
import org.asqatasun.entity.parameterization.ParameterFamily;
import org.asqatasun.entity.service.audit.AuditDataService;
import org.asqatasun.entity.service.audit.ProcessResultDataService;
import org.asqatasun.entity.service.parameterization.ParameterDataService;
import org.asqatasun.entity.service.parameterization.ParameterFamilyDataService;
import org.asqatasun.entity.service.statistics.CriterionStatisticsDataService;
import org.asqatasun.entity.service.statistics.TestStatisticsDataService;
import org.asqatasun.entity.service.statistics.ThemeStatisticsDataService;
import org.asqatasun.entity.service.statistics.WebResourceStatisticsDataService;
import org.asqatasun.entity.service.subject.WebResourceDataService;
import org.asqatasun.entity.subject.Site;
import org.asqatasun.entity.subject.WebResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author enzolalay
 */
@Component("analyserFactory")
public class AnalyserFactoryImpl implements AnalyserFactory {// TODO Write javadoc

    @Autowired
    @Qualifier("webResourceStatisticsDataService")
    private WebResourceStatisticsDataService webResourceStatisticsDataService;

    @Autowired
    private WebResourceDataService webResourceDataService;

    @Autowired
    private ThemeStatisticsDataService themeStatisticsDataService;

    @Autowired
    private TestStatisticsDataService testStatisticsDataService;

    @Autowired
    private CriterionStatisticsDataService criterionStatisticsDataService;

    @Autowired
    private AuditDataService auditDataService;

    @Autowired
    private ParameterDataService parameterDataService;

    @Autowired
    private ParameterFamilyDataService parameterFamilyDataService;

    @Autowired
    private ProcessResultDataService processResultDataService;

    @Value("${testWeightParameterFamilyCodeList:Seo_TEST_WEIGHT_MANAGEMENT}")
    private String testWeightParameterFamilyCodeList;
    public List<String> getTestWeightParameterFamilyCodeList() {
        return StringUtils.isNotBlank(testWeightParameterFamilyCodeList) ?
                Arrays.asList(testWeightParameterFamilyCodeList.split(";")) :
                Collections.<String>emptyList();
    }

    private Collection<ParameterFamily> testWeightParameterFamilySet;
    public Collection<ParameterFamily> getTestWeightParameterFamilySet() {
        if (testWeightParameterFamilySet == null) {
            testWeightParameterFamilySet = new HashSet<>();
            for (String paramFamilyCode : getTestWeightParameterFamilyCodeList()) {
                testWeightParameterFamilySet.add(parameterFamilyDataService.getParameterFamily(paramFamilyCode));
            }
        }
        return testWeightParameterFamilySet;
    }


    public AnalyserFactoryImpl() {}

    @Override
    public Analyser create(WebResource webResource, Audit audit) {
        int nbOfWebResource = 1;
        if (webResource instanceof Site) {
            nbOfWebResource = 
                    webResourceDataService.getNumberOfChildWebResource(webResource).intValue();
        }
        Analyser analyser = new AnalyserImpl(
                auditDataService,
                testStatisticsDataService,
                themeStatisticsDataService,
                webResourceStatisticsDataService,
                criterionStatisticsDataService,
                processResultDataService,
                webResource,
                getTestWeightParamSet(audit), 
                nbOfWebResource);
        return analyser;
    }
    
    /**
     * 
     * @param audit
     * @return 
     * 
     *  the collection of test weight parameters for the given audit
     */
    private Collection<Parameter> getTestWeightParamSet(Audit audit) {
        Collection<Parameter> testWeightParamSet = new HashSet<>();
        for (ParameterFamily pf : getTestWeightParameterFamilySet()) {
            testWeightParamSet.addAll(parameterDataService.getParameterSet(pf, audit));
        }
        return testWeightParamSet;
    }

    /**
     *
     * @param testWeightParameterFamilyCodeList
     */
    public void setTestWeightParameterFamilyCodeList(String testWeightParameterFamilyCodeList) {
        this.testWeightParameterFamilyCodeList = testWeightParameterFamilyCodeList;
    }
}