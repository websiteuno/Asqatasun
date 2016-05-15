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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * This class launches Asqatasun with urls passed as arguments by the user.
 *
 * @author jkowalczyk
 */
public class Asqatasun {

    private static final String APPLICATION_CONTEXT_FILE_PATH = "conf/context/application-context.xml";

    private static final String PAGE_AUDIT = "Page";
    private static final String SCENARIO_AUDIT = "Scenario";
    private static final String FILE_AUDIT = "File";
    private static final String SITE_AUDIT = "File";

    private static final String AW22_REF = "Aw22";
    private static final String RGAA22_REF = "Rgaa22";
    private static final String RGAA30_REF = "Rgaa30";
    private static String REF = AW22_REF;

    private static final String BRONZE_LEVEL = "Bz";
    private static final String SILVER_LEVEL = "Ar";
    private static final String GOLD_LEVEL = "Or";

    private static final int DEFAULT_XMS_VALUE = 64;
    
    private static String LEVEL = SILVER_LEVEL;

    private static final Options OPTIONS = createOptions();
    
    private static String AUDIT_TYPE = PAGE_AUDIT;
    
    private static String ASQATASUN_HOME;

    public static void main(String[] args) {
        if (args == null) {
            return;
        }

        ASQATASUN_HOME = System.getenv("ASQATASUN_PATH");
        CommandLineParser clp = new BasicParser();
        try {
            CommandLine cl = clp.parse(OPTIONS, args);
            if (cl.hasOption("h")) {
                printUsage();
                return;
            }
            if (cl.hasOption("f")) {
                String ffPath = cl.getOptionValue("f");
                if (isValidPath(ffPath, "f", false)) {
                    System.setProperty("webdriver.firefox.bin", ffPath);
                } else {
                    printUsage();
                    return;
                }
            }
            if (cl.hasOption("d")) {
                String display = cl.getOptionValue("d");
                if (isValidDisplay(display, "d")) {
                    System.setProperty("display", ":"+display);
                } else {
                    printUsage();
                    return;
                }
            }
            if (cl.hasOption("r")) {
                String ref = cl.getOptionValue("r");
                if (isValidReferential(ref)) {
                    REF = ref;
                } else {
                    printUsage();
                    return;
                }
            }
            if (cl.hasOption("l")) {
                String level = cl.getOptionValue("l");
                if (isValidLevel(level)) {
                    LEVEL = level;
                } else {
                    printUsage();
                    return;
                }
            }
            if (cl.hasOption("o")) {
                String outputDir = cl.getOptionValue("o");
                if (isValidPath(outputDir, "o", true)) {
                    try {
                        System.setOut(new PrintStream(outputDir));
                    } catch (FileNotFoundException ex) {
                        printUsage();
                        return;
                    }
                } else {
                    printUsage();
                    return;
                }
            }
            if (cl.hasOption("t")) {
                String auditType = cl.getOptionValue("t");
                if (!isValidAuditType(auditType)) {
                    printUsage();
                    return;
                } else {
                    AUDIT_TYPE = auditType;
                }
            }
            if (cl.hasOption("x")) {
                String xmxValue = cl.getOptionValue("x");
                if (!isValidXmxValue(xmxValue)) {
                    printUsage();
                    return;
                }
            }
            if (AUDIT_TYPE.equalsIgnoreCase(PAGE_AUDIT)) {
                if (!isValidPageUrl(cl)) {
                    printUsage();
                } else {
                    AsqatasunRunner runner = initSpringContextAndGetRunner();
                    runner.runAuditOnline(cl.getArgs(),ASQATASUN_HOME, REF, LEVEL);
                }
            } else if (AUDIT_TYPE.equalsIgnoreCase(SCENARIO_AUDIT)) {
                if (!isValidScenarioPath(cl)) {
                    printUsage();
                } else {
                    AsqatasunRunner runner = initSpringContextAndGetRunner();
                    runner.runAuditOnline(cl.getArgs(),ASQATASUN_HOME, REF, LEVEL);
                }
            } else if (AUDIT_TYPE.equalsIgnoreCase(FILE_AUDIT)) {
                if (!isValidFilePath(cl)) {
                    printUsage();
                } else {
                    AsqatasunRunner runner = initSpringContextAndGetRunner();
                    runner.runAuditOnline(cl.getArgs(),ASQATASUN_HOME, REF, LEVEL);
                }
            } else if (AUDIT_TYPE.equalsIgnoreCase(SITE_AUDIT)) {
                if (!isValidSiteUrl(cl)) {
                    printUsage();
                } else {
                    System.out.println("Functionnality is not working on cli interface");
                    printUsage();
                }
            }
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(Asqatasun.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     *
     * @return an instance of AsqatasunRunner
     */
    private static AsqatasunRunner initSpringContextAndGetRunner() {
        System.setProperty("exploitation.dir", ASQATASUN_HOME);
        ApplicationContext context = new FileSystemXmlApplicationContext(ASQATASUN_HOME + "/" + APPLICATION_CONTEXT_FILE_PATH);

        AsqatasunRunner asqatasunRunner = new AsqatasunRunner();

        //the magic: auto-wire the instance with all its dependencies:
        context.getAutowireCapableBeanFactory().autowireBeanProperties(asqatasunRunner,
                AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        return asqatasunRunner;
    }

    public Asqatasun() {
        super();
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

    /**
     * Create the asqatasun command line interface options
     * @return the options to launch asqatasun cli
     */
    private static Options createOptions() {
        Options options = new Options();
        
        options.addOption(OptionBuilder.withLongOpt("help")
                             .withDescription("Show this message.")
                             .hasArg(false)
                             .isRequired(false)
                             .create("h"));
        
        options.addOption(OptionBuilder.withLongOpt("output")
                             .withDescription("Path to the output result file.")
                             .hasArg()
                             .isRequired(false)
                             .create("o"));
        
        options.addOption(OptionBuilder.withLongOpt("firefox-bin")
                             .withDescription("Path to the firefox bin.")
                             .hasArg()
                             .isRequired(false)
                             .create("f"));
        
        options.addOption(OptionBuilder.withLongOpt("display")
                             .withDescription("Value of the display")
                             .hasArg()
                             .isRequired(false)
                             .create("d"));
        
        options.addOption(OptionBuilder.withLongOpt("referential")
                             .withDescription("Referential : \n"
                + "- \"Aw22\" for Accessiweb 2.2 (default)\n"
                + "- \"Rgaa22\" for Rgaa 2.2\n"
                + "- \"Rgaa30\" for Rgaa 3.0\n")
                             .hasArg()
                             .isRequired(false)
                             .create("r"));
        
        options.addOption(OptionBuilder.withLongOpt("level")
                             .withDescription("Level :\n"
                + "- \"Or\" for Gold level or AAA level, \n"
                + "- \"Ar\" for Silver level or AA level (default), \n"
                + "- \"Bz\" for Bronze level or A level")
                             .hasArg()
                             .isRequired(false)
                             .create("l"));
        
        options.addOption(OptionBuilder.withLongOpt("audit-type")
                             .withDescription("Audit type :\n"
                + "- \"Page\" for page audit (default)\n"
                + "- \"File\" for file audit \n"
                + "- \"Scenario\" for scenario audit \n"
                + "- \"Site\" for site audit")
                             .hasArg()
                             .isRequired(false)
                             .create("t"));
        options.addOption(OptionBuilder.withLongOpt("xmx-value")
                             .withDescription("Xmx value set to the process (without 'M' at the end):\n"
                + "- default is 256 \n"
                + "- must be superior to 64 (Xms value)")
                             .hasArg()
                             .isRequired(false)
                             .create("x"));

        return options;
    }
    
    /**
     * Print usage
     */
    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("./bin/asqatasun.sh [OPTIONS]... [URL OR FILE OR SCENARIO]...\n", OPTIONS);
    }

    /**
     * 
     * @param path
     * @param option
     * @param testWritable
     * @return whether the given path is valid for the given argument
     */
    private static boolean isValidPath(String path, String option, boolean testWritable) {
        File file = FileUtils.getFile(path);
        if (file.exists() && file.canExecute() && file.canRead()) {
            if (!testWritable) {
                return true;
            } else if (file.canWrite()) {
                return true;
            }
        }
        System.out.println("\n"+path + " is an invalid path for " + option + " option.\n");
        return false;
    }
    
    /**
     * 
     * @param display
     * @param option
     * @return whether the given display is valid 
     */
    private static boolean isValidDisplay(String display, String option) {
        try {
            Integer.valueOf(display);
            return true;
        } catch (NumberFormatException nme) {
            System.out.println("\n"+display + " is invalid for " + option + " option.\n");
            return false;
        }
    }
    
    /**
     * 
     * @param ref
     * @return whether the given referential is valid
     */
    private static boolean isValidReferential(String ref) {
        if (StringUtils.equals(ref, AW22_REF) ||
                StringUtils.equals(ref, RGAA22_REF) ||
                    StringUtils.equals(ref, RGAA30_REF)) {
            return true;
        }
        System.out.println("\nThe referential \"" + ref + "\" doesn't exist.\n");
        return false;
    }
    
    /**
     * 
     * @param level
     * @return whether the given level is valid
     */
    private static boolean isValidLevel(String level) {
        if (StringUtils.equals(level, BRONZE_LEVEL) || 
                StringUtils.equals(level, SILVER_LEVEL) ||
                StringUtils.equals(level, GOLD_LEVEL)) {
            return true;
        }
        System.out.println("\nThe level \"" + level + "\" doesn't exist.\n");
        return false;
    }
    
    /**
     * 
     * @param xmxStr
     * @return whether the given level is valid
     */
    private static boolean isValidXmxValue(String xmxStr) {
      System.out.println(xmxStr);
        try {
            int xmxValue = Integer.valueOf(xmxStr);
            if (xmxValue <= DEFAULT_XMS_VALUE) {
                System.out.println("\nThe value of the Xmx value \"" + xmxStr + "\" must be superior to "+DEFAULT_XMS_VALUE+".\n");
                return false;
            }
        } catch (NumberFormatException nfe) {
            System.out.println("\nThe format of the Xmx value \"" + xmxStr + "\" is incorrect.\n");
            return false;
        }
        return true;
    }
    
    /**
     * 
     * @param auditType
     * @return whether the given level is valid
     */
    private static boolean isValidAuditType(String auditType) {
        if (StringUtils.equalsIgnoreCase(auditType,PAGE_AUDIT) || 
                StringUtils.equalsIgnoreCase(auditType, FILE_AUDIT) ||
                StringUtils.equalsIgnoreCase(auditType, SITE_AUDIT) ||
                StringUtils.equalsIgnoreCase(auditType, SCENARIO_AUDIT)) {
            return true;
        }
        System.out.println("\nThe audit type \"" + auditType + "\" doesn't exist.\n");
        return false;
    }
    
    /**
     * 
     * @param cl
     * @return whether the given level is valid
     */
    private static boolean isValidPageUrl( CommandLine cl) {
        if (cl.getArgList().isEmpty()) {
            System.out.println("\nPlease specify at least one URL\n");
            return false;
        }
        for (String arg : cl.getArgs()) {
            try {
                URL url = new URL(arg);
            } catch (MalformedURLException ex) {
                System.out.println("\nThe URL " + arg + " is malformed\n");
                return false;
            }
        }
        return true;
    }

    private static boolean isValidSiteUrl( CommandLine cl) {
        if (cl.getArgList().isEmpty()) {
            System.out.println("\nPlease specify at least one URL\n");
            return false;
        }
        if (cl.getArgList().size() > 1) {
            System.out.println("\nOnly one URL is expected\n");
            return false;
        }
        try {
            URL url = new URL(cl.getArgs()[0]);
        } catch (MalformedURLException ex) {
            System.out.println("\nThe URL "+ cl.getArgs()[0]+ " is malformed\n");
            return false;
        }

        return true;
    }
    
    private static boolean isValidFilePath( CommandLine cl) {
        if (cl.getArgList().isEmpty()) {
            System.out.println("\nPlease specify at least one file\n");
            return false;
        }
        for (String arg : cl.getArgs()) {
            File file = FileUtils.getFile(arg);
            if (!file.canRead()) {
                System.out.println("\nThe file "+ file.getAbsolutePath() +" is unreadable.\n");
                return false;
            }
        }
        return true;
    }
    
    private static boolean isValidScenarioPath( CommandLine cl) {
        if (cl.getArgList().isEmpty()) {
            System.out.println("\nPlease specify at least one scenario\n");
            return false;
        }
        if (cl.getArgList().size() > 1) {
            System.out.println("\nOnly one scenario is expected\n");
            return false;
        }
        File scenario = FileUtils.getFile(cl.getArgs()[0]);
        if (!scenario.canRead()) {
            System.out.println("\nThe scenario file is unreadable.\n");
            return false;
        }
        return true;
    }

}
