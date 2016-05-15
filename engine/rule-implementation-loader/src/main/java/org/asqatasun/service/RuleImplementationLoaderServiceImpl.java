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
package org.asqatasun.service;

import java.util.HashSet;
import java.util.Set;
import org.asqatasun.entity.reference.Test;
import org.asqatasun.entity.service.audit.ProcessRemarkDataService;
import org.asqatasun.entity.service.audit.ProcessResultDataService;
import org.asqatasun.ruleimplementation.AbstractSiteRuleWithPageResultImplementation;
import org.asqatasun.ruleimplementation.RuleImplementation;
import org.asqatasun.ruleimplementationloader.RuleImplementationLoader;
import org.asqatasun.ruleimplementationloader.RuleImplementationLoaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author jkowalczyk
 */
@Service("ruleImplementationLoaderService")
public class RuleImplementationLoaderServiceImpl implements RuleImplementationLoaderService {

    @Autowired
    private NomenclatureLoaderService nomenclatureLoaderService;
    @Autowired
    private ProcessRemarkDataService processRemarkDataService;
    @Autowired
    private ProcessResultDataService processResultDataService;
    @Autowired
    private RuleImplementationLoaderFactory ruleImplementationLoaderFactory;
    private String archiveRoot;

    public RuleImplementationLoaderServiceImpl() {
        super();
    }

    @Override
    public Set<RuleImplementation> loadRuleImplementationSet(Set<Test> testSet) {
        Set<RuleImplementation> ruleImplementationSet = new HashSet<>();
        for (Test test : testSet) {
            ruleImplementationSet.add(loadRuleImplementation(test));
        }
        return ruleImplementationSet;
    }

    @Override
    public RuleImplementation loadRuleImplementation(Test test) {
        RuleImplementationLoader ruleImplementationLoader = ruleImplementationLoaderFactory.create(archiveRoot, test.getRuleArchiveName(), test.getRuleClassName());
        ruleImplementationLoader.run();
        RuleImplementation ruleImplementation = ruleImplementationLoader.getResult();
        ruleImplementation.setTest(test);
        ruleImplementation.setProcessResultDataService(processResultDataService);
        ruleImplementation.setNomenclatureLoaderService(nomenclatureLoaderService);
        
        // for specific purpose, we may need to access to ProcessRemark of
        // indefinite processResult while consolidating. 
        // yet, the ProcessResult are passed with lazy collection. 
        // The processRemarkDataService enables to retrieve the processRemark
        // of a given ProcessResult in this case
        if (ruleImplementation instanceof AbstractSiteRuleWithPageResultImplementation) {
            ((AbstractSiteRuleWithPageResultImplementation)ruleImplementation).setProcessRemarkDataService(processRemarkDataService);
        }
        
        return ruleImplementation;
    }

    @Override
    public void setArchiveRoot(String archiveRoot) {
        this.archiveRoot = archiveRoot;
    }

}