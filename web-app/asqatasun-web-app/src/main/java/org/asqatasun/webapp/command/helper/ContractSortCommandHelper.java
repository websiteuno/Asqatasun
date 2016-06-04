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
package org.asqatasun.webapp.command.helper;

import org.apache.commons.lang3.StringUtils;
import org.asqatasun.webapp.command.ContractSortCommand;
import org.asqatasun.webapp.command.factory.AddScenarioCommandFactory;
import org.asqatasun.webapp.command.factory.ContractSortCommandFactory;
import org.asqatasun.webapp.dto.data.ContractInfo;
import org.asqatasun.webapp.dto.factory.ContractInfoFactory;
import org.asqatasun.webapp.entity.contract.Contract;
import org.asqatasun.webapp.entity.user.User;
import org.asqatasun.webapp.form.FormField;
import org.asqatasun.webapp.form.builder.FormFieldBuilder;
import org.asqatasun.webapp.ui.form.parameterization.helper.FormFieldHelper;
import org.asqatasun.webapp.util.TgolKeyStore;
import org.displaytag.properties.SortOrderEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;

/**
 *
 * @author jkowalczyk
 */
@Component("contractSortCommandHelper")
public final class ContractSortCommandHelper  {

    @Value("${lastAuditDateSortValue:date}")
    private String lastAuditDateSortValue;
    @Value("${sortByKey:sort-by-choice}")
    private String sortByKey;
    @Value("${sortOrderKey:order-choice}")
    private String sortOrderKey;
    @Value("${exclusionContractSortKey:label-exclusion-choice}")
    private String exclusionContractSortKey;
    @Value("${inclusionContractSortKey:label-inclusion-choice}")
    private String inclusionContractSortKey;
    @Value("${lastAuditMarkSortValue:mark}")
    private String lastAuditMarkSortValue;

    /**
     * The holder that handles the unique instance of ContractSortCommandHelperHolder
     */
    private static class ContractSortCommandHelperHolder {
        private static final ContractSortCommandHelper INSTANCE = new ContractSortCommandHelper();
    }

    /**
     * Private constructor
     */
    private ContractSortCommandHelper() {}

    /**
     * Singleton pattern based on the "Initialization-on-demand
     * holder idiom". See @http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
     * @return the unique instance of AddScenarioCommandFactory
     */
    public static ContractSortCommandHelper getInstance() {
        return ContractSortCommandHelperHolder.INSTANCE;
    }

    /**
     * This methods retrieves and prepare contract info
     * 
     * @param user
     * @param csc the ContractSortCommand
     * @param displayOptionFieldsBuilderList
     * @param model
     * @return 
     */
    public Collection<ContractInfo> prepareContractInfo (
            User user, 
            ContractSortCommand csc, 
            List<FormFieldBuilder> displayOptionFieldsBuilderList,
            Model model) {
        
        csc = prepareDataForSortConsole(user.getId(), csc, displayOptionFieldsBuilderList, model);

        List<ContractInfo> contractInfoSet = new LinkedList();
        List<String> inclusionSortOccurrence;
        if (csc.getSortOptionMap().containsKey(inclusionContractSortKey))  {
            inclusionSortOccurrence = Arrays.asList(csc.getSortOptionMap().get(inclusionContractSortKey).toString().split(";"));
        } else {
            inclusionSortOccurrence = new ArrayList();
        }
        List<String> exclusionSortOccurrence;
        if (csc.getSortOptionMap().containsKey(exclusionContractSortKey))  {
            exclusionSortOccurrence = Arrays.asList(csc.getSortOptionMap().get(exclusionContractSortKey).toString().split(";"));
        } else {
            exclusionSortOccurrence = new ArrayList();
        }
        for (Contract contract : user.getContractSet()) {
            if (isContractLabelIncluded(inclusionSortOccurrence, contract.getLabel()) &&
                    !isContractLabelExcluded(exclusionSortOccurrence, contract.getLabel())) {
                contractInfoSet.add(ContractInfoFactory.getInstance().getContractInfo(contract));
            }
        }
        if (csc.getSortOptionMap().containsKey(sortOrderKey)) {
            sortContractInfoSetRegardingCommand(contractInfoSet, csc);
        }    
        return contractInfoSet;
    }
    
    /**
     * This methods retrieves and prepare contract info
     * 
     * @param user
     * @param csc the ContractSortCommand
     * @param displayOptionFieldsBuilderList
     * @param model
     * @return 
     */
    public  Collection<Contract> prepareContract (
            User user, 
            ContractSortCommand csc, 
            List<FormFieldBuilder> displayOptionFieldsBuilderList,
            Model model) {
        
        csc = prepareDataForSortConsole(
                user.getId(), 
                csc, 
                displayOptionFieldsBuilderList, 
                model);
        List<Contract> contractSet = new LinkedList();
        List<String> inclusionSortOccurrence =
                Arrays.asList(csc.getSortOptionMap().get(inclusionContractSortKey).toString().split(";"));
        List<String> exclusionSortOccurrence =
                Arrays.asList(csc.getSortOptionMap().get(exclusionContractSortKey).toString().split(";"));
        for (Contract contract : user.getContractSet()) {
            if (isContractLabelIncluded(inclusionSortOccurrence, contract.getLabel()) &&
                    !isContractLabelExcluded(exclusionSortOccurrence, contract.getLabel())) {
                contractSet.add(contract);
            }
        }
        sortContractSetRegardingCommand(contractSet, csc);
        return contractSet;
    }

    private boolean isContractLabelIncluded(List<String> inclusionList, String contractLabel) {
        if (inclusionList.isEmpty() || 
                (inclusionList.size() == 1 && 
                 StringUtils.isEmpty(inclusionList.get(0).trim()))) {
            return true;
        }
        for (String inclusion : inclusionList) {
            if (contractLabel.contains(inclusion)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isContractLabelExcluded(List<String> exclusionList, String contractLabel) {
        if (exclusionList.isEmpty() || 
                (exclusionList.size() == 1 && 
                 StringUtils.isEmpty(exclusionList.get(0).trim()))) {
            return false;
        }
        for (String exclusion : exclusionList) {
            if (contractLabel.contains(exclusion)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sort a collection of scenarios by date of creation
     */
    private class ContractInfoLabelSorter implements Comparator<ContractInfo> {
        SortOrderEnum sortOrder;
        public ContractInfoLabelSorter(int sortOrder) {
            this.sortOrder = SortOrderEnum.fromCode(sortOrder);
        }
        
        @Override
        public int compare(ContractInfo c, ContractInfo c1) {
            if (sortOrder.equals(SortOrderEnum.ASCENDING)) {
                return c1.getLabel().compareToIgnoreCase(c.getLabel());
            } else if (sortOrder.equals(SortOrderEnum.DESCENDING)){
                return c.getLabel().compareToIgnoreCase(c1.getLabel());
            }
            return c1.getLabel().compareToIgnoreCase(c.getLabel());
        }
    }
    
    /**
     * Sort a collection of scenarios by date of creation
     */
    private class ContractLabelSorter implements Comparator<Contract> {
        SortOrderEnum sortOrder;
        public ContractLabelSorter(int sortOrder) {
            this.sortOrder = SortOrderEnum.fromCode(sortOrder);
        }
        
        @Override
        public int compare(Contract c, Contract c1) {
            if (sortOrder.equals(SortOrderEnum.ASCENDING)) {
                return c1.getLabel().compareToIgnoreCase(c.getLabel());
            } else if (sortOrder.equals(SortOrderEnum.DESCENDING)){
                return c.getLabel().compareToIgnoreCase(c1.getLabel());
            }
            return c1.getLabel().compareToIgnoreCase(c.getLabel());
        }
    }
    
    private class ContractInfoMarkSorter implements Comparator<ContractInfo> {
        SortOrderEnum sortOrder;
        public ContractInfoMarkSorter(int sortOrder) {
            this.sortOrder = SortOrderEnum.fromCode(sortOrder);
        }
        
        @Override
        public int compare(ContractInfo c, ContractInfo c1) {
            if (sortOrder.equals(SortOrderEnum.ASCENDING)) {
                if (c1.getLastActInfo() != null 
                        && c.getLastActInfo() != null) {
                    return Integer.valueOf(c1.getLastActInfo().getRawMark())
                        .compareTo(c.getLastActInfo().getRawMark());
                } else if (c1.getLastActInfo() == null 
                        && c.getLastActInfo() == null) {
                    return c.getLabel().compareToIgnoreCase(c1.getLabel());
                } else if (c1.getLastActInfo() == null) {
                    return -1;
                } else if (c.getLastActInfo() == null) {
                    return 1;
                }
            } else if (sortOrder.equals(SortOrderEnum.DESCENDING)){
                if (c1.getLastActInfo() != null 
                        && c.getLastActInfo() != null) {
                    return Integer.valueOf(c.getLastActInfo().getRawMark())
                        .compareTo(c1.getLastActInfo().getRawMark());
                } else if (c1.getLastActInfo() == null 
                        && c.getLastActInfo() == null) {
                    return c.getLabel().compareToIgnoreCase(c1.getLabel());
                } else if (c1.getLastActInfo() == null) {
                    return -1;
                } else if (c.getLastActInfo() == null) {
                    return 1;
                }
            }
            return 0;
        }
    }
    
    private class ContractInfoDateSorter implements Comparator<ContractInfo> {
        SortOrderEnum sortOrder;
        public ContractInfoDateSorter(int sortOrder) {
            this.sortOrder = SortOrderEnum.fromCode(sortOrder);
        }
        
        @Override
        public int compare(ContractInfo c, ContractInfo c1) {
            if (sortOrder.equals(SortOrderEnum.ASCENDING)) {
                if (c1.getLastActInfo() != null && c.getLastActInfo() != null) {
                    return c1.getLastActInfo().getDate()
                        .compareTo(
                            c.getLastActInfo().getDate());
                } else if (c1.getLastActInfo() == null && c.getLastActInfo() == null) {
                    return c.getLabel().compareTo(c1.getLabel());
                } else if (c1.getLastActInfo() == null) {
                    return -1;
                } else if (c.getLastActInfo() == null) {
                    return 1;
                }
            } else if (sortOrder.equals(SortOrderEnum.DESCENDING)){
                if (c1.getLastActInfo() != null && c.getLastActInfo() != null) {
                    return c.getLastActInfo().getDate()
                        .compareTo(
                            c1.getLastActInfo().getDate());
                } else if (c1.getLastActInfo() == null && c.getLastActInfo() == null) {
                    return c.getLabel().compareTo(c1.getLabel());
                } else if (c1.getLastActInfo() == null) {
                    return -1;
                } else if (c.getLastActInfo() == null) {
                    return 1;
                }
            }
            return 0;
        }
    }
    
    /**
     * 
     * @param contractInfoSet
     * @param csc the ContractSortCommand 
     */
    public void sortContractInfoSetRegardingCommand(
            List<ContractInfo> contractInfoSet, 
            ContractSortCommand csc) {
        String sortByValue = csc.getSortOptionMap().get(sortByKey).toString();

        if (StringUtils.equalsIgnoreCase(sortByValue, lastAuditMarkSortValue)) {
            Collections.sort(
                contractInfoSet, 
                new ContractInfoMarkSorter(
                    Integer.valueOf(csc.getSortOptionMap().get(sortOrderKey).toString())));
        } else if (StringUtils.equalsIgnoreCase(sortByValue, lastAuditDateSortValue)) {
            Collections.sort(
                contractInfoSet, 
                new ContractInfoDateSorter(
                    Integer.valueOf(csc.getSortOptionMap().get(sortOrderKey).toString())));
        } else {
            Collections.sort(
                contractInfoSet, 
                new ContractInfoLabelSorter(Integer.valueOf(csc.getSortOptionMap().get(sortOrderKey).toString())));
        }
    }
    
    /**
     * 
     * @param contractSet
     * @param csc
     */
    private void sortContractSetRegardingCommand(
            List<Contract> contractSet, 
            ContractSortCommand csc) {
        // By default if the choice is not alphabetical, the contracts will be
        // sorted by alphabetical order in the second time. If the choice is 
        // alphabetical, this value will be overidden with the user value.
        int alphabeticalSortDirection = 
                    Integer.valueOf(csc.getSortOptionMap().get(sortOrderKey).toString());
        Collections.sort(
                contractSet, 
                new ContractLabelSorter(alphabeticalSortDirection));
    }
    
    /**
     * This method prepares the data to be displayed in the sort 
     * (score, alphabetical, date) console of the result page.
     */
    private ContractSortCommand prepareDataForSortConsole(
            Long userId, 
            ContractSortCommand contractSortCommand, 
            List<FormFieldBuilder> displayOptionFieldsBuilderList,
            Model model) {
        
        ContractSortCommand cdc;
        List<FormField> formFieldList;
        
        if (contractSortCommand == null) {
            formFieldList = FormFieldHelper.getFormFieldBuilderCopy(
                    displayOptionFieldsBuilderList);
            cdc = ContractSortCommandFactory.getInstance().getInitialisedContractDisplayCommand(
                        userId,
                        formFieldList);
        } else {
            formFieldList = FormFieldHelper.getFormFieldBuilderCopy(
                    displayOptionFieldsBuilderList, 
                    contractSortCommand.getSortOptionMap());
            cdc = contractSortCommand;
        }

        model.addAttribute(TgolKeyStore.CONTRACT_SORT_FIELD_LIST_KEY, formFieldList);
        model.addAttribute(TgolKeyStore.CONTRACT_SORT_COMMAND_KEY, cdc);

        return cdc;
    }
    
}