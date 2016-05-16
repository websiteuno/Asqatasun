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
package org.asqatasun.rules.test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.asqatasun.entity.audit.*;
import org.asqatasun.entity.audit.factory.ContentFactory;
import org.asqatasun.entity.parameterization.*;
import org.asqatasun.entity.reference.TestImpl;
import org.asqatasun.entity.subject.factory.WebResourceFactory;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.asqatasun.contentadapter.util.URLIdentifier;
import org.asqatasun.contentadapter.util.URLIdentifierFactory;
import org.asqatasun.entity.reference.Test;
import org.asqatasun.entity.subject.Page;
import org.asqatasun.entity.subject.Site;
import org.asqatasun.entity.subject.WebResource;
import org.asqatasun.service.ConsolidatorService;
import org.asqatasun.service.ContentAdapterService;
import org.asqatasun.service.ContentLoaderService;
import org.asqatasun.service.ProcessorService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author lralambomanana
 */
public abstract class AbstractRuleImplementationTestCase extends DataSourceBasedDBTestCase {

    private static final Logger LOGGER = Logger.getLogger(AbstractRuleImplementationTestCase.class);
    private final String applicationContextFilePath = "context/application-context.xml";

    private static ApplicationContext APPLICATION_CONTEXT;
    private static ContentLoaderService CONTENT_LOADER_SERVICE;
    private static ContentAdapterService CONTENT_ADAPTER_SERVICE;
    private static ProcessorService PROCESSOR_SERVICE;
    protected static ConsolidatorService CONSOLIDATOR_SERVICE;
    public ConsolidatorService getConsolidatorService() {
        return CONSOLIDATOR_SERVICE;
    }
    private static ContentFactory CONTENT_FACTORY;
    private static WebResourceFactory WEB_RESOURCE_FACTORY;
    public WebResourceFactory getWebResourceFactory() {
        return WEB_RESOURCE_FACTORY;
    }
    private static DataSource DATA_SOURCE;
    @Override
    protected DataSource getDataSource() {
        return DATA_SOURCE;
    }

    private URLIdentifier urlIdentifier;

    // Collections to handle content during test
    protected final Map<WebResource, List<Content>> contentMap = new HashMap<>();
    protected final Map<WebResource, List<String>> relatedContentMap = new HashMap<>();
    protected final List<Test> testList = new ArrayList<>();
    protected final Map<WebResource, Collection<ProcessResult>> grossResultMap = new HashMap<>();
    protected final Map<WebResource, ProcessResult> netResultMap = new HashMap<>();
    private final Map<String, WebResource> webResourceMap = new LinkedHashMap<>();
    public Map<String, WebResource> getWebResourceMap() {
        return webResourceMap;
    }
    private final Map<String, Collection<Parameter>> parameterMap = new HashMap<>();

    private String ruleImplementationClassName;
    public void setRuleImplementationClassName(String ruleImplementationClassName) {
        this.ruleImplementationClassName = ruleImplementationClassName;
    }

    private String testcasesFilePath = "";
    public String getTestcasesFilePath() {
        return testcasesFilePath;
    }
    private IDataSet dataset = null;
    private String inputDataFileName = "";
    public String getInputDataFileName() {
        return inputDataFileName;
    }
    
    /**
     * 
     * @param testName
     * @param inputDataFileName
     * @param testcasesFilePath
     */
    public AbstractRuleImplementationTestCase(
            String testName,
            String inputDataFileName,
            String testcasesFilePath) {
        super(testName);
        this.testcasesFilePath = testcasesFilePath;
        this.inputDataFileName = inputDataFileName;
        initialize();
        setUpRuleImplementationClassName();
        setUpWebResourceMap();
        setUpClass();
        setUpParameterMap();
    }

    private void initialize() {
        initializePath();
        // the initialisation is process (set-up of spring context) is made 
        // only once for a given referential. That's why all the applicative
        // class attributes are defined as static. Otherwise, the context is 
        // fully loaded, and the test spend at least 200ms for nothing
        if (APPLICATION_CONTEXT == null) {
            APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(applicationContextFilePath);
            WEB_RESOURCE_FACTORY = (WebResourceFactory) APPLICATION_CONTEXT.getBean("webResourceFactory");
            CONTENT_FACTORY = (ContentFactory) APPLICATION_CONTEXT.getBean("contentFactory");

            CONTENT_LOADER_SERVICE = (ContentLoaderService) APPLICATION_CONTEXT.getBean("contentLoaderService");
            CONTENT_ADAPTER_SERVICE = (ContentAdapterService) APPLICATION_CONTEXT.getBean("contentAdapterService");
            PROCESSOR_SERVICE = (ProcessorService) APPLICATION_CONTEXT.getBean("processorService");
            CONSOLIDATOR_SERVICE = (ConsolidatorService) APPLICATION_CONTEXT.getBean("consolidatorService");

            DATA_SOURCE = (DataSource) APPLICATION_CONTEXT.getBean("dataSource");

            urlIdentifier = ((URLIdentifierFactory) APPLICATION_CONTEXT.getBean("urlIdentifierFactory")).create();

            //the magic: auto-wire the instance with all its dependencies:
            APPLICATION_CONTEXT.getAutowireCapableBeanFactory().autowireBeanProperties(
                    this,
                    AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,
                    true);
        }
    }

    /**
     * In this method, set the class name of the rule implementation to test.
     */
    protected abstract void setUpRuleImplementationClassName();

    /**
     * In this method, set the web resource list to test.
     */
    protected abstract void setUpWebResourceMap();

    /**
     * Add a webResource. Path is built from webResourceName.
     * 
     * @param webResourceName 
     * @param parameters
     */
    protected void addWebResource(String webResourceName, Parameter... parameters) {
        getWebResourceMap().put(webResourceName,
                WEB_RESOURCE_FACTORY.createPage(
                getTestcasesFilePath() + getRefKey()+"/"+getClassName()+"/"+webResourceName+".html"));
        for (Parameter param : parameters) {
            addParameterToParameterMap(webResourceName,param);
        }
    }
    
    /**
     * 
     * @return the name of the current referential
     */
    protected String getRefKey() {
        return "";
    }
    
    protected String getClassName() {
        return ruleImplementationClassName.substring(
                ruleImplementationClassName.lastIndexOf(".")+1, 
                ruleImplementationClassName.length());
    }
    
    /**
     * In this method, set the parameters associated with the each webresource.
     */
    protected void setUpParameterMap() {
        associateParameterToSSP();
    }

    /**
     * 
     */
    private void setUpClass() {
        Test test = new TestImpl();;
        test.setCode(this.getName());
        test.setRuleClassName(ruleImplementationClassName);
        test.setRuleArchiveName("MockArchiveName");
        testList.add(test);
        URL src = null;
        LOGGER.info("setUpClass()");
        for (WebResource webResource : webResourceMap.values()) {
            LOGGER.info("webResource.getURL() " + webResource.getURL());
            contentMap.put(webResource, CONTENT_LOADER_SERVICE.loadContent(webResource));
            
            if (relatedContentMap.get(webResource) != null) {
                for (String contentUrl : relatedContentMap.get(webResource)) {
                    if (contentMap.get(webResource).get(0) instanceof SSP) {
                        SSP ssp = (SSP) contentMap.get(webResource).get(0);
                        try {
                            src = new URL(ssp.getURI());
                            urlIdentifier.setUrl(src);
                        } catch (MalformedURLException ex) {
                            LOGGER.error(ex);
                        }
                        urlIdentifier.setUrl(src);
                        String relatedContentUrl =
                                urlIdentifier.resolve(contentUrl).toExternalForm();
                        if (isContentCss(relatedContentUrl)) {
                            ssp.addRelatedContent(
                                    CONTENT_FACTORY.createStylesheetContent(
                                    new Date(),
                                    relatedContentUrl,
                                    ssp,
                                    getTextContent(relatedContentUrl),
                                    200));
                        } else {
                            ssp.addRelatedContent(
                                    CONTENT_FACTORY.createImageContent(
                                    new Date(),
                                    relatedContentUrl,
                                    ssp,
                                    getBinaryImage(relatedContentUrl),
                                    200));
                        }
                    }
                }
            }
            contentMap.put(webResource, (List<Content>) CONTENT_ADAPTER_SERVICE.adaptContent((contentMap.get(webResource))));
        }
    }

    protected Collection<ProcessResult> process(String webResourceKey) {
        LOGGER.debug(this + "::process(\"" + webResourceKey + "\")");
        WebResource webResource = webResourceMap.get(webResourceKey);
        Collection<ProcessResult> grossResultList = PROCESSOR_SERVICE.process(contentMap.get(webResource), testList);
        for (Content content : contentMap.get(webResource)) {
            LOGGER.debug(content.getURI());
        }
        
        grossResultMap.put(webResource, grossResultList);
        return grossResultList;
    }

    protected ProcessResult processPageTest(String webResourceKey) {
        return process(webResourceKey).iterator().next();
    }

    public ProcessResult consolidate(String webResourceKey) {
        LOGGER.debug(this + "::consolidate(\"" + webResourceKey + "\")");
        WebResource webResource = webResourceMap.get(webResourceKey);
        ProcessResult netResult = CONSOLIDATOR_SERVICE.consolidate(grossResultMap.get(webResource), testList).iterator().next();
        netResultMap.put(webResource, netResult);
        return netResult;
    }

    public void testRuleImplementation() {
        setProcess();
        setConsolidate();
    }

    protected abstract void setProcess();

    protected ProcessResult getGrossResult(String pageKey, String siteKey) {
        Site site = (Site) webResourceMap.get(siteKey);
        Collection<ProcessResult> grossResultList = grossResultMap.get(site);
        Page page = (Page) webResourceMap.get(pageKey);
        for (ProcessResult grossResult : grossResultList) {
            if (grossResult.getSubject().equals(page)) {
                return grossResult;
            }
        }
        return null;
    }
    
    private void initializePath() {
        testcasesFilePath =
                "file://" + System.getProperty("user.dir")  + "/" + testcasesFilePath;
        testcasesFilePath = FilenameUtils.normalize(testcasesFilePath);
    }

    private byte[] getBinaryImage(String imgUrl) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        URL url = null;
        try {
            url = new URL(imgUrl);
        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
        }
        byte[] resultImageAsRawBytes = null;
        try {
            BufferedImage image = ImageIO.read(url);
            // W R I T E
            ImageIO.write(image, getImageExtension(imgUrl), baos);
            // C L O S E
            baos.flush();
            resultImageAsRawBytes = baos.toByteArray();
            baos.close();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
        return resultImageAsRawBytes;
    }

    private String getImageExtension(String imageUrl) {
        String ext = imageUrl.substring(imageUrl.lastIndexOf('.') + 1);
        java.util.Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix(ext);
        if (it.next() != null) {
            return ext;
        } else {
            return "jpg";
        }
    }

    private boolean isContentCss(String url) {
        String ext = url.substring(url.lastIndexOf('.') + 1);
        return ext.equalsIgnoreCase("css");
    }

    private String getTextContent(String url) {
        BufferedReader in = null;
        try {
            StringBuilder urlContent = new StringBuilder();
            String thisLine;
            URL u = new URL(url);
            in = new BufferedReader(new InputStreamReader(u.openStream()));
            while ((thisLine = in.readLine()) != null) {
                //Correction of #34 bug
                urlContent.append(thisLine);
                urlContent.append("\r");
            }
            return urlContent.toString();
        } catch (IOException ex) {
            LOGGER.error(ex);
            return "";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LOGGER.error(ex);
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Override method to set custom properties/features {@inheritDoc}
     * @param config
     */
    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        super.setUpDatabaseConfig(config);
        config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, 97);
    }

    /**
     * Charge le jeu de données à partir d'un fichier XML d'import
     * @return 
     * @throws java.lang.Exception 
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        if (dataset != null) {
            return dataset;
        }
        if (!getInputDataFileName().isEmpty()) {
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);
            dataset = builder.build(new File(
                    getInputDataFileName()));
            return dataset;
        } else {
            return null;
        }
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    /**
     * 
     * @param familyValue
     * @param elementValue
     * @param value
     * @return 
     */
    protected Parameter createParameter(String familyValue, String elementValue, String value) {
        ParameterFamily parameterFamily = new ParameterFamilyImpl();
        parameterFamily.setParameterFamilyCode(familyValue);
        ParameterElement parameterElement = new ParameterElementImpl();
        parameterElement.setParameterFamily(parameterFamily);
        parameterElement.setParameterElementCode(elementValue);
        Parameter parameter = new ParameterImpl();
        parameter.setValue(value);
        parameter.setParameterElement(parameterElement);
        return parameter;
    }
    
    /**
     * 
     * @param sspIdentifier
     * @param parameter
     */
    protected void addParameterToParameterMap(String sspIdentifier, Parameter parameter) {
        if (parameterMap.containsKey(sspIdentifier)) {
            parameterMap.get(sspIdentifier).add(parameter);
        } else {
            Collection<Parameter> params = new ArrayList<>();
            params.add(parameter);
            parameterMap.put(sspIdentifier, params);
        }
    }

    /**
     * 
     */
    private void associateParameterToSSP() {
        for (Map.Entry<String, WebResource> entry : webResourceMap.entrySet()) {
            Audit audit = new AuditImpl(new Date());
            if (parameterMap.containsKey(entry.getKey())) {
                for (Parameter param : parameterMap.get(entry.getKey())) {
                    audit.addParameter(param);
                }
            }
            if (contentMap.containsKey(entry.getValue()) && !contentMap.get(entry.getValue()).isEmpty()) {
                Content content = contentMap.get(entry.getValue()).iterator().next();
                if (content instanceof SSP) {
                    ((SSP)content).setAudit(audit);
                }
            }
        }
    }
    
    @Override
    public String getName(){
        int lastPointIndex = StringUtils.lastIndexOf(ruleImplementationClassName, ".")+1;
        String className = StringUtils.substring(ruleImplementationClassName, lastPointIndex);
        return StringUtils.replace(className, "Rule", "-");
    }

    /**
     * Check whether the result of a ProcessResult is Passed
     * 
     * @param processResult
     * @param numberOfElements 
     */
    protected void checkResultIsPassed(ProcessResult processResult, int numberOfElements) {
        checkResult(processResult, TestSolution.PASSED, numberOfElements, 0);
    }
    
    /**
     * Check whether the result of a ProcessResult is Not Applicable
     * 
     * @param processResult
     */
    protected void checkResultIsNotApplicable(ProcessResult processResult) {
        checkResult(processResult, TestSolution.NOT_APPLICABLE, 0, 0);
    }
    
    /**
     * Check whether the result of a ProcessResult is Not Applicable
     * with elements in the original scope
     * @param processResult
     * @param numberOfElements
     */
    protected void checkResultIsNotApplicable(ProcessResult processResult, int numberOfElements) {
        checkResult(processResult, TestSolution.NOT_APPLICABLE, numberOfElements, 0);
    }
    
    /**
     * Check whether the result of a ProcessResult is Not Applicable
     * 
     * @param processResult
     */
    protected void checkResultIsNotTested(ProcessResult processResult) {
        checkResult(processResult, TestSolution.NOT_TESTED, 0, 0);
    }
    
    /**
     * Check whether the result of a ProcessResult is Failed
     * 
     * @param processResult
     * @param numberOfElements 
     * @param numberOfRemarks
     */
    protected void checkResultIsFailed(
            ProcessResult processResult,
            int numberOfElements,
            int numberOfRemarks) {
        checkResult(processResult, TestSolution.FAILED, numberOfElements, numberOfRemarks);
    }
    
    /**
     * Check whether the result of a ProcessResult is Pre-Qualified
     * 
     * @param processResult
     * @param numberOfElements 
     * @param numberOfRemarks
     */
    protected void checkResultIsPreQualified(
            ProcessResult processResult,
            int numberOfElements,
            int numberOfRemarks) {
        checkResult(processResult, TestSolution.NEED_MORE_INFO, numberOfElements, numberOfRemarks);
    }
    
    /**
     * Generic test result check, regarding the result of the ProcessResult, 
     * the number of remarks created, and the number of detected elements
     * 
     * @param processResult
     * @param testSolution
     * @param numberOfElements 
     * @param numberOfRemarks
     */
    private void checkResult(
            ProcessResult processResult, 
            TestSolution testSolution, 
            int numberOfElements,
            int numberOfRemarks) {
        // check test result
        assertEquals(testSolution, processResult.getValue());
        if (numberOfRemarks == 0) {
            // check test has no remark
            assertNull(processResult.getRemarkSet());
        } else {
            // check number of created remarks
            assertEquals(numberOfRemarks, processResult.getRemarkSet().size());
        }
        // check number of elements in the page
        assertEquals(numberOfElements, processResult.getElementCounter());
    }

    /**
     * 
     * @param processResult
     * @param testSolution
     * @param remarkMessageCode
     * @param position 
     */
    protected void checkRemarkIsPresent(
            ProcessResult processResult, 
            TestSolution testSolution, 
            String remarkMessageCode,
            int position) {
        ProcessRemark processRemark = 
                ((ProcessRemark)((LinkedHashSet)processResult.getRemarkSet()).toArray()[position-1]);
        Logger.getLogger(this.getClass()).debug(processRemark.getMessageCode());
        assertEquals(remarkMessageCode, processRemark.getMessageCode());
        Logger.getLogger(this.getClass()).debug(processRemark.getIssue());
        assertEquals(testSolution, processRemark.getIssue());
        assertNull(processRemark.getElementList());
    }
    
    /**
     * 
     * @param processResult
     * @param testSolution
     * @param remarkMessageCode
     * @param remarkTarget 
     * @param position 
     * @param evidencePairs
     */
    protected void checkRemarkIsPresent(
            ProcessResult processResult, 
            TestSolution testSolution, 
            String remarkMessageCode,
            String remarkTarget,
            int position, 
            Pair<String,String>... evidencePairs) {
        checkRemarkIsPresent(
                processResult, 
                testSolution, 
                remarkMessageCode, 
                remarkTarget, 
                position, 
                false, 
                evidencePairs);
    }
    
    /**
     * 
     * @param processResult
     * @param testSolution
     * @param remarkMessageCode
     * @param remarkTarget
     * @param position
     * @param emptySnippet
     * @param evidencePairs 
     */
    protected void checkRemarkIsPresent(
            ProcessResult processResult, 
            TestSolution testSolution, 
            String remarkMessageCode,
            String remarkTarget,
            int position, 
            boolean emptySnippet,
            Pair<String,String>... evidencePairs) {
        if (((LinkedHashSet)processResult.getRemarkSet()).toArray()[position-1] instanceof SourceCodeRemark ) {
            SourceCodeRemark sourceCodeRemark = 
                    ((SourceCodeRemark)((LinkedHashSet)processResult.getRemarkSet()).toArray()[position-1]);
            Logger.getLogger(this.getClass()).debug(sourceCodeRemark.getMessageCode());
            assertEquals(remarkMessageCode, sourceCodeRemark.getMessageCode());
            assertEquals(testSolution, sourceCodeRemark.getIssue());
            Logger.getLogger(this.getClass()).debug(sourceCodeRemark.getIssue());
            if (remarkTarget.equals("EMPTY_TARGET") ) {
                assertEquals(-1, sourceCodeRemark.getLineNumber());
            } else {
                assertEquals(remarkTarget, sourceCodeRemark.getTarget());
                Logger.getLogger(this.getClass()).debug(sourceCodeRemark.getTarget());
                if (!emptySnippet) {
                    assertNotNull("Snippet is empty but shouldn't !!! ",sourceCodeRemark.getSnippet());
                }
            }
            if (evidencePairs.length == 0) {
                assertNull("Evidence Element list is not empty but should be !!! ",sourceCodeRemark.getElementList());
                return;
            }
            // check number of evidence elements and their value
            assertEquals(evidencePairs.length, sourceCodeRemark.getElementList().size());

            Object[] evEls = sourceCodeRemark.getElementList().toArray();
            for (int i=0 ; i<evEls.length ; i++) {
                EvidenceElement ee = (EvidenceElement)evEls[i];
                Logger.getLogger(this.getClass()).debug(ee.getEvidence().getCode());
                Logger.getLogger(this.getClass()).debug(ee.getValue());
                assertEquals(evidencePairs[i].getLeft(), ee.getEvidence().getCode());
                assertTrue("Wrong evidence element : expected["+evidencePairs[i].getRight()+"] actual["+ee.getValue()+"]",StringUtils.contains(ee.getValue(), evidencePairs[i].getRight()));
            }
        } else {
            ProcessRemark processRemark = 
                    (ProcessRemark)(((LinkedHashSet)processResult.getRemarkSet()).toArray()[position-1]);
            Logger.getLogger(this.getClass()).debug(processRemark.getMessageCode());
            assertEquals(remarkMessageCode, processRemark.getMessageCode());
            assertEquals(testSolution, processRemark.getIssue());
            assertNull(processRemark.getElementList());
        }
    }
    /**
     * Default implementation of setConsolidate. May be overridden only in 
     * special cases
     */
    protected void setConsolidate() {
        for (String webResourceKey : webResourceMap.keySet()) {
            if (StringUtils.containsIgnoreCase(webResourceKey, "Passed")) {
                assertEquals(TestSolution.PASSED,
                    consolidate(webResourceKey).getValue());
            } else if (StringUtils.containsIgnoreCase(webResourceKey, "Failed")) {
                assertEquals(TestSolution.FAILED,
                    consolidate(webResourceKey).getValue());
            } else if (StringUtils.containsIgnoreCase(webResourceKey, "NMI")) {
                assertEquals(TestSolution.NEED_MORE_INFO,
                    consolidate(webResourceKey).getValue());
            } else if (StringUtils.containsIgnoreCase(webResourceKey, "NA")) {
                assertEquals(TestSolution.NOT_APPLICABLE,
                    consolidate(webResourceKey).getValue());
            } 
        }
    }

}