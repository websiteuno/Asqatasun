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
package org.asqatasun.webapp.entity.dao.contract;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.asqatasun.webapp.entity.contract.Contract;
import org.asqatasun.webapp.entity.contract.ContractImpl;
import org.asqatasun.webapp.entity.dao.test.AbstractDaoTestCase;
import org.asqatasun.webapp.entity.dao.user.UserDAO;
import org.asqatasun.webapp.entity.factory.contract.ContractFactory;
import org.asqatasun.webapp.entity.functionality.Functionality;
import org.asqatasun.webapp.entity.option.OptionElement;
import org.asqatasun.webapp.entity.referential.Referential;
import org.asqatasun.webapp.entity.scenario.Scenario;
import org.asqatasun.webapp.entity.service.contract.ContractDataService;
import org.asqatasun.webapp.entity.service.contract.ContractDataServiceImpl;
import org.asqatasun.webapp.entity.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jkowalczyk
 */
public class ContractDAOImplTest extends AbstractDaoTestCase {

    /**
     * Nom du fichier xml contenant le jeu de données à importer
     */
    private static final String INPUT_DATA_SET_FILENAME = "flatXmlDataSet.xml";

    @Autowired
    private ContractDAO contractDAO;
    @Autowired
    private UserDAO userDAO;

    @Override
    protected String getDataSetFilename() throws Exception {
        return getInputDataFilePath()+INPUT_DATA_SET_FILENAME;
    }

    public ContractDAOImplTest() {
        super();
    }
    
    /**
     * Test of findAllContractsByUser method, of class ContractDAOImpl.
     */
    @Test
    public void testFindAllContractsByUser() {
        System.out.println("findAllContractsByUser");
        User user = userDAO.read(Long.valueOf(1));
        assertEquals(2, contractDAO.findAllContractsByUser(user).size());
        user = userDAO.read(Long.valueOf(2));
        assertEquals(0, contractDAO.findAllContractsByUser(user).size());
    }

    /**
     * Test of read method, of class ContractDAOImpl.
     */
    @Test
    public void testRead() {
        System.out.println("read");
        
        Contract contract = contractDAO.read(Long.valueOf(1));
        assertNotNull(contract);
        assertEquals(Long.valueOf("1"), contract.getUser().getId());
        Set<String> functionalityCodeSet = new HashSet<>();
        for (Functionality functionality : contract.getFunctionalitySet()) {
            functionalityCodeSet.add(functionality.getCode());
        }
        assertTrue(functionalityCodeSet.contains("PAGES_AUDIT"));
        assertTrue(!functionalityCodeSet.contains("SITE_AUDIT"));
        assertEquals(1, contract.getReferentialSet().size());
        assertTrue(contract.getScenarioSet().isEmpty());
        
        contract = contractDAO.read(Long.valueOf(2));
        assertNotNull(contract);
        assertEquals(Long.valueOf("1"), contract.getUser().getId());
        functionalityCodeSet = new HashSet<>();
        for (Functionality functionality : contract.getFunctionalitySet()) {
            functionalityCodeSet.add(functionality.getCode());
        }
        assertTrue(functionalityCodeSet.contains("PAGES_AUDIT"));
        assertTrue(functionalityCodeSet.contains("SITE_AUDIT"));
        assertEquals(2, contract.getReferentialSet().size());
        assertEquals(2, contract.getScenarioSet().size());
        
        contract = contractDAO.read(Long.valueOf(3));
        assertNull(contract);
    }

    /**
     * Test of saveOrUpdate method, of class ContractDAOImpl.
     */
    @Test
    public void testSaveOrUpdate() {
        System.out.println("saveOrUpdate");
        Date beginDate = new Date();
        Date endDate = new Date();
        Date renewalDate = new Date();
        int nbOfContract = contractDAO.findAll().size();
        Contract contract = new ContractImpl();
        contract.setPrice(Float.valueOf(200));
        contract.setLabel("Contract-test");
        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);
        contract.setRenewalDate(renewalDate);
        contractDAO.saveOrUpdate(contract);
        assertEquals(nbOfContract+1, contractDAO.findAll().size());
    }

    /**
     * Test of update method, of class ContractDAOImpl.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Contract contract  = contractDAO.read((Long.valueOf(1)));
        Float contractPrice = contract.getPrice();
        contract.setPrice(contractPrice + 200);
        contractDAO.update(contract);
        assertEquals(contractPrice+200, contractDAO.read((Long.valueOf(1))).getPrice());
    }

}