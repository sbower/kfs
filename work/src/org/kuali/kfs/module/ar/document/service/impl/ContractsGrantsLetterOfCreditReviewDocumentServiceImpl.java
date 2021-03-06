/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ContractsGrantsLetterOfCreditReviewDocumentServiceImpl implements ContractsGrantsLetterOfCreditReviewDocumentService {
    protected AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao;
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected KualiModuleService kualiModuleService;
    protected UniversityDateService universityDateService;
    protected OptionsService optionsService;

    /**
     * This method retrieves the amount to draw for the award account based on the criteria passed
     *
     * @param awardaccounts
     * @return
     */
    @Override
    public void setAwardAccountToDraw(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award) {

        boolean isValid = true;
        // 1. To get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = getAwardAccountObjectCodeTotalBilledDao().getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);


        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {

            final SystemOptions systemOptions = optionsService.getCurrentYearOptions();

            // 2. Get the Cumulative amount from GL Balances.

            KualiDecimal cumAmt = getContractsGrantsInvoiceDocumentService().getBudgetAndActualsForAwardAccount(awardAccount, systemOptions.getActualFinancialBalanceTypeCd(), award.getAwardBeginningDate());
            KualiDecimal billedAmount = KualiDecimal.ZERO;
            KualiDecimal amountToDraw = KualiDecimal.ZERO;


            // 3. Amount to Draw = Cumulative amount - Billed to Date.(This would be ultimately the current expenditures in the
            // invoice document.
            for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
                if (awardAccountTotalBilledAmount.getAccountNumber().equals(awardAccount.getAccountNumber()) && awardAccountTotalBilledAmount.getChartOfAccountsCode().equals(awardAccount.getChartOfAccountsCode()) && awardAccountTotalBilledAmount.getProposalNumber().equals(awardAccount.getProposalNumber())) {
                    billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
                }
            }
            amountToDraw = cumAmt.subtract(billedAmount);
            // set the amount to Draw in the award Account
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
            getContractsAndGrantsModuleBillingService().setAmountToDrawToAwardAccount(criteria, amountToDraw);
        }

    }

    /**
     * This method calculates the claim on cash balance for every award account.
     *
     * @param awardaccount
     * @return
     */
    @Override
    public KualiDecimal getClaimOnCashforAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, java.sql.Date awardBeginningDate) {
        KualiDecimal balAmt = KualiDecimal.ZERO;
        KualiDecimal expAmt = KualiDecimal.ZERO;
        KualiDecimal incAmt = KualiDecimal.ZERO;
        KualiDecimal claimOnCash = KualiDecimal.ZERO;
        Integer currentYear = getUniversityDateService().getCurrentFiscalYear();

        Integer fiscalYear = getUniversityDateService().getFiscalYear(awardBeginningDate);

        for (Integer currFiscalYear = fiscalYear; currFiscalYear <= currentYear; currFiscalYear++) {
            final List<String> objectTypeCodeList = getIncomeAndExpenseObjectTypesForFiscalYear(currFiscalYear);
            final Collection<Balance> glBalances = retrieveBalancesForAwardAccount(awardAccount, currFiscalYear, objectTypeCodeList);
            final SystemOptions systemOptions = optionsService.getCurrentYearOptions();

            for (Balance bal : glBalances) {
                if (ObjectUtils.isNull(bal.getSubAccount()) || ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                    if (bal.getObjectTypeCode().equalsIgnoreCase(systemOptions.getFinancialObjectTypeTransferExpenseCd())) {
                        balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());

                        expAmt = expAmt.add(balAmt);
                    }
                    else if (bal.getObjectTypeCode().equalsIgnoreCase(systemOptions.getFinancialObjectTypeTransferIncomeCd())) {
                        balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());

                        incAmt = incAmt.add(balAmt);
                    }
                }
            }
        }
        return claimOnCash = incAmt.subtract(expAmt);
    }

    /**
     * Looks up a Collection of Balances for a given award Account
     * @param awardAccount award account to find balances for
     * @param fiscalYear the fiscal year to find balances for
     * @param objectTypeCodeList the selection of object type codes the balance may have
     */
    protected Collection<Balance> retrieveBalancesForAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, Integer fiscalYear, List<String> objectTypeCodeList) {

        final SystemOptions systemOptions = optionsService.getCurrentYearOptions();

        Map<String, Object> balanceKeys = new HashMap<String, Object>();
        balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
        balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
        balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE, systemOptions.getActualFinancialBalanceTypeCd());
        balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, objectTypeCodeList);
        return getBusinessObjectService().findMatching(Balance.class, balanceKeys);
    }

    /**
     * Retrieves the SystemOptions for the given fiscal year and pulls the income/cash and expense/expenditure object types
     * from that business object
     * @param fiscalYear the fiscal year to find income and expense object types for
     * @return a List of the object type codes for the income and expense object types for the given fiscal year
     */
    protected List<String> getIncomeAndExpenseObjectTypesForFiscalYear(Integer fiscalYear) {
        final SystemOptions systemOptions = optionsService.getCurrentYearOptions();
        List<String> expenseTypes = new ArrayList<>();
        expenseTypes.add(systemOptions.getFinObjectTypeIncomecashCode());
        expenseTypes.add(systemOptions.getFinObjTypeExpenditureexpCd());
        return expenseTypes;
    }

    /**
     * This method retrieves the amount available to draw for the award accounts
     *
     * @param awardTotalAmount
     * @param awardAccount
     */
    @Override
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {

        // Get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = getAwardAccountObjectCodeTotalBilledDao().getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
        KualiDecimal billedAmount = KualiDecimal.ZERO;
        KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
            billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
        }
        amountAvailableToDraw = awardTotalAmount.subtract(billedAmount);

        return amountAvailableToDraw;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService#getActiveAwardsByCriteria(java.util.Map)
     */
    @Override
    public List<ContractsAndGrantsBillingAward> getActiveAwardsByCriteria(Map<String, Object> criteria) {
        return getKualiModuleService().getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, criteria);
    }

    public AwardAccountObjectCodeTotalBilledDao getAwardAccountObjectCodeTotalBilledDao() {
        return awardAccountObjectCodeTotalBilledDao;
    }

    public void setAwardAccountObjectCodeTotalBilledDao(AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao) {
        this.awardAccountObjectCodeTotalBilledDao = awardAccountObjectCodeTotalBilledDao;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

}