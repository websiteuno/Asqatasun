/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2015  Asqatasun.org
 *
 * This program is free software: you can redistribute it and/or modify
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
package org.asqatasun.rules.rgaa30;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.asqatasun.entity.audit.*;
import org.asqatasun.rules.rgaa30.test.Rgaa30RuleImplementationTestCase;
import static org.asqatasun.rules.keystore.AttributeStore.*;
import org.asqatasun.rules.keystore.HtmlElementStore;
import static org.asqatasun.rules.keystore.MarkerStore.DECORATIVE_IMAGE_MARKER;
import static org.asqatasun.rules.keystore.MarkerStore.INFORMATIVE_IMAGE_MARKER;
import org.asqatasun.rules.keystore.RemarkMessageStore;

/**
 * Unit test class for the implementation of the rule 1-7-1 of the referential Rgaa 3.0.
 *
 * @author jkowalczyk
 */
public class Rgaa30Rule010701Test extends Rgaa30RuleImplementationTestCase {

    /**
     * Default constructor
     * @param testName
     */
    public Rgaa30Rule010701Test (String testName){
        super(testName);
    }

    @Override
    protected void setUpRuleImplementationClassName() {
        setRuleImplementationClassName("org.asqatasun.rules.rgaa30.Rgaa30Rule010701");
    }

    @Override
    protected void setUpWebResourceMap() {
        addWebResource("Rgaa30.Test.01.07.01-3NMI-01");
        addWebResource("Rgaa30.Test.01.07.01-3NMI-02",
                createParameter("Rules", INFORMATIVE_IMAGE_MARKER, "informative-image"));
        addWebResource("Rgaa30.Test.01.07.01-3NMI-03");
        addWebResource("Rgaa30.Test.01.07.01-4NA-01");
        addWebResource("Rgaa30.Test.01.07.01-4NA-02");
        addWebResource("Rgaa30.Test.01.07.01-4NA-03");
        addWebResource("Rgaa30.Test.01.07.01-4NA-04");
        addWebResource("Rgaa30.Test.01.07.01-4NA-05",
                createParameter("Rules", DECORATIVE_IMAGE_MARKER, "decorative-image"));

    }

    @Override
    protected void setProcess() {
        //----------------------------------------------------------------------
        //-------------------------------3NMI-01--------------------------------
        //----------------------------------------------------------------------
        ProcessResult processResult = processPageTest("Rgaa30.Test.01.07.01-3NMI-01");
        checkResultIsPreQualified(processResult, 1,  1);
        checkRemarkIsPresent(
                processResult,
                TestSolution.NEED_MORE_INFO,
                RemarkMessageStore.CHECK_NATURE_OF_IMAGE_AND_DESC_PERTINENCE_MSG,
                HtmlElementStore.IMG_ELEMENT,
                1,
                new ImmutablePair(ALT_ATTR, ""),
                new ImmutablePair(SRC_ATTR, "mock_image.jpg"));        
        
        //----------------------------------------------------------------------
        //-------------------------------3NMI-02--------------------------------
        //----------------------------------------------------------------------
        processResult = processPageTest("Rgaa30.Test.01.07.01-3NMI-02");
        checkResultIsPreQualified(processResult, 4,  4);
        checkRemarkIsPresent(
                processResult,
                TestSolution.NEED_MORE_INFO,
                RemarkMessageStore.CHECK_DESC_PERTINENCE_OF_INFORMATIVE_IMG_MSG,
                HtmlElementStore.INPUT_ELEMENT,
                1,
                new ImmutablePair(ALT_ATTR, ABSENT_ATTRIBUTE_VALUE),
                new ImmutablePair(SRC_ATTR, ABSENT_ATTRIBUTE_VALUE));
        checkRemarkIsPresent(
                processResult,
                TestSolution.NEED_MORE_INFO,
                RemarkMessageStore.CHECK_DESC_PERTINENCE_OF_INFORMATIVE_IMG_MSG,
                HtmlElementStore.IMG_ELEMENT,
                2,
                new ImmutablePair(ALT_ATTR, ""),
                new ImmutablePair(SRC_ATTR, "mock_image2.jpg")); 
        checkRemarkIsPresent(
                processResult,
                TestSolution.NEED_MORE_INFO,
                RemarkMessageStore.CHECK_NATURE_OF_IMAGE_AND_DESC_PERTINENCE_MSG,
                HtmlElementStore.INPUT_ELEMENT,
                3,
                new ImmutablePair(ALT_ATTR, ABSENT_ATTRIBUTE_VALUE),
                new ImmutablePair(SRC_ATTR, ABSENT_ATTRIBUTE_VALUE));
        checkRemarkIsPresent(
                processResult,
                TestSolution.NEED_MORE_INFO,
                RemarkMessageStore.CHECK_NATURE_OF_IMAGE_AND_DESC_PERTINENCE_MSG,
                HtmlElementStore.IMG_ELEMENT,
                4,
                new ImmutablePair(ALT_ATTR, ""),
                new ImmutablePair(SRC_ATTR, "mock_image1.jpg"));        
       
        
        //----------------------------------------------------------------------
        //-------------------------------3NMI-03--------------------------------
        //----------------------------------------------------------------------
        processResult = processPageTest("Rgaa30.Test.01.07.01-3NMI-03");
        checkResultIsPreQualified(processResult, 1,  1);
        checkRemarkIsPresent(
                processResult,
                TestSolution.NEED_MORE_INFO,
                RemarkMessageStore.CHECK_NATURE_OF_IMAGE_AND_DESC_PERTINENCE_MSG,
                HtmlElementStore.INPUT_ELEMENT,
                1,
                new ImmutablePair(ALT_ATTR, ABSENT_ATTRIBUTE_VALUE),
                new ImmutablePair(SRC_ATTR, "mock_image.jpg"));        
         
        //----------------------------------------------------------------------
        //------------------------------4NA-01------------------------------
        //----------------------------------------------------------------------
        checkResultIsNotApplicable(processPageTest("Rgaa30.Test.01.07.01-4NA-01"));        

        //----------------------------------------------------------------------
        //------------------------------4NA-02---------------------------------
        //----------------------------------------------------------------------
        checkResultIsNotApplicable(processPageTest("Rgaa30.Test.01.07.01-4NA-02"));        

        //----------------------------------------------------------------------
        //------------------------------4NA-03---------------------------------
        //----------------------------------------------------------------------
        checkResultIsNotApplicable(processPageTest("Rgaa30.Test.01.07.01-4NA-03"));        

        //----------------------------------------------------------------------
        //------------------------------4NA-04---------------------------------
        //----------------------------------------------------------------------
        checkResultIsNotApplicable(processPageTest("Rgaa30.Test.01.07.01-4NA-04"));

        //----------------------------------------------------------------------
        //------------------------------4NA-05---------------------------------
        //----------------------------------------------------------------------
        checkResultIsNotApplicable(processPageTest("Rgaa30.Test.01.07.01-4NA-05"));
    }

}
