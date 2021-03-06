/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.service;


import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * This class defines all the service methods for Contracts and Grants invoice Document.
 */
public interface ContractsGrantsInvoiceDocumentService {

    /**
     * This method creates Source Accounting lines enabling the creation of GLPEs in the document.
     *
     * @param document the cash control document
     * @throws WorkflowException
     */
    public void createSourceAccountingLines(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException;

    /**
     * This method creates Source Accounting lines enabling the creation of GLPEs in the document.
     *
     * @param document the cash control document
     * @throws WorkflowException
     */
    public void recalculateNewTotalBilled(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * Recalculates the Total Expenditures for the Invoice due to reaching limit of the total award.
     *
     * @param contractsGrantsInvoiceDocument
     * @throws WorkflowException
     */
    public void prorateBill(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException;

    /**
     * Add the Total Billed amount from each invoiceDetailAccountObjectCodes to the corresponding Award Account Object Code.
     *
     * @param invoiceDetailAccountObjectCodes List account object codes to process
     */
    public void addToAccountObjectCodeBilledTotal(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes);

    /**
     * Returns the billed to date amount for the given Proposal Number for Milestones.
     *
     * @param proposalNumber
     * @return
     */
    public KualiDecimal getMilestonesBilledToDateAmount(Long proposalNumber);

    /**
     * Returns the billed to date amount for the given Proposal Number for Predetermined Billing.
     *
     * @param proposalNumber
     * @return
     */
    public KualiDecimal getPredeterminedBillingBilledToDateAmount(Long proposalNumber);

    /**
     * Returns the total amount billed to date for an Award.
     *
     * @param proposalNumber used to find the AwardAccountObjectCodeTotalBilled
     * @return billed to date amount
     */
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber);

    /**
     * This method retrieves CG invoice documents that match the given field values
     *
     * @param fieldValues
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteria(Map fieldValues);

    /**
     * This method retrieves CG invoice documents that match the given field values and excludes
     * the given outside collection agency code
     *
     * @param fieldValues
     * @param outsideColAgencyCodeToExclude
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesForReferallExcludingOutsideCollectionAgency(Map fieldValues, String outsideColAgencyCodeToExclude);

    /**
     * This method updates the Suspension Categories on the document
     *
     * @param contractsGrantsInvoiceDocument
     */
    public void updateSuspensionCategoriesOnDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * This method calculates and returns the total payments applied to date for an award.
     *
     * @param award used to calculate total payments
     * @return total payments to date for the award
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(ContractsAndGrantsBillingAward award);

    /**
     * This method calculates the Budget and cumulative amount for Award Account
     *
     * @param awardAccount
     * @param balanceTypeCode
     * @param awardBeginningDate
     * @return
     */
    public KualiDecimal getBudgetAndActualsForAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, String balanceTypeCode, Date awardBeginningDate);

    /**
     * Get award accounts's control accounts
     *
     * @param award
     * @return
     */
    public List<Account> getContractControlAccounts(ContractsAndGrantsBillingAward award);

    /**
     * Retrieve all the expired accounts of an award
     *
     * @param award
     * @return
     */
    public Collection<Account> getExpiredAccountsOfAward(ContractsAndGrantsBillingAward award);

    /**
     * To retrieve processing chart code and org code from the billing chart code and org code
     *
     * @param coaCode
     * @param orgCode
     * @return list of processing codes
     */
    public List<String> getProcessingFromBillingCodes(String coaCode, String orgCode);

    /**
     * To retrieve the list of ContractsGrantsInvoiceDocument from proposal number.
     *
     * @param proposalNumber
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByProposalNumber(Long proposalNumber);

    /**
     * Determine if the collectorPrincipalId can view the invoice, leverages role qualifiers
     * on the CGB Collector role to perform the check.
     *
     * @param invoice The invoice to check if the collector can view.
     * @param collectorPrincipalId The principal id of the collector to check permissions for.
     * @return Returns true if the collector can view the invoice, false otherwise.
     */
    public boolean canViewInvoice(ContractsGrantsInvoiceDocument invoice, String collectorPrincipalId);

    /**
     * This method sets the last billed date to Award and Award Account objects based on the status of the invoice.
     * If this is the final invoice, also sets Final Billed indicator on Award Account
     *
     * @param document ContractGrantsInvoiceDocument referencing the Award and Award Account objects to update
     */
    public void updateLastBilledDate(ContractsGrantsInvoiceDocument document);

    /**
     * This method updates the Bills and Milestone objects billed Field.
     *
     * @param billed
     * @param invoiceMilestones
     * @param invoiceBills
     */
    public void updateBillsAndMilestones(boolean billed, List<InvoiceMilestone> invoiceMilestones,List<InvoiceBill> invoiceBills);

    /**
     * This method generates the attached invoices for the invoice addresses in the Contracts and Grants Invoice Document.
     *
     * @param document
     */
    public void generateInvoicesForInvoiceAddresses(ContractsGrantsInvoiceDocument document);

    /**
     * This method updates AwardAccounts
     *
     * @param accountDetails
     * @param proposalNumber
     */
    public void updateUnfinalizationToAwardAccount(List<InvoiceAccountDetail> accountDetails,Long proposalNumber);

    /**
     * Corrects the Contracts and Grants Invoice Document.
     *
     * @param document
     * @throws WorkflowException
     */
    public void correctContractsGrantsInvoiceDocument(ContractsGrantsInvoiceDocument document) throws WorkflowException;

    /**
     * Determines if a Contracts and Grants cost category contains a given object code
     * @param category the cost category which may contain an object code
     * @param chartOfAccountsCode the chart of the object code to check
     * @param objectCode the object code to check
     * @return true if the cost category contains the given object code, false otherwise
     */
    public boolean doesCostCategoryContainObjectCode(CostCategory category, String chartOfAccountsCode, String objectCode);

    /**
     * Calculate the lastBilledDate for the Award based on it's AwardAccounts
     *
     * @param award ContractsAndGrantsBillingAward to calculate lastBilledDate for
     * @return the lastBilledDate
     */
    public java.sql.Date getLastBilledDate(ContractsAndGrantsBillingAward award);

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     *
     * @param award
     * @return errorString
     */
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award);

    /**
     * Determines if the given invoice template can be utilized by the given CGB Invoice Document based on
     * a comparison of the billing chart/org of the invoiceTemplate to the billing chart/org of the invoice doc.
     *
     * @param invoiceTemplate the invoice template to check
     * @param contractsGrantsInvoiceDocument the invoice document to check against
     * @return true if the document can utilize the template, false otherwise
     */
    public boolean isTemplateValidForContractsGrantsInvoiceDocument(InvoiceTemplate invoiceTemplate, ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * This method retrieves all Invoice Documents that match the given field values, excluding the given
     * invoice numbers.
     *
     * @param fieldValues for search criteria.
     * @return Returns the invoices which matches the given field values.
     */
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollection(Map fieldValues);

    /**
     * Determines whether the given ContractsGrantsInvoiceDocument is "effective" or not: if it is disapproved, cancelled, or error corrected then it is NOT effective,
     * and in all other cases, it is effective
     * @param invoiceDocument the invoice document to check
     * @return true if the document is "effective" given the rules above, false otherwise
     */
    public boolean isInvoiceDocumentEffective(String documentNumber);

    /**
     * Update the billed indicator on a List of given Invoice Bills
     * @param billed the value for the billed indicator
     * @param invoiceBills the bills to update
     */
    public void updateBillsBilledIndicator(boolean billed, List<InvoiceBill> invoiceBills);

    /**
     * Update the billed indicator on a List of given Milestones
     * @param billed the value for the billed indicator
     * @param invoiceMilestones the invoice milestones to update
     */
    public void updateMilestonesBilledIndicator(boolean billed, List<InvoiceMilestone> invoiceMilestones);

    /**
     * This helper method returns a map of a list of invoices mapped by the proposal number of the invoice
     * @param invoices The list of invoices for which filtering to be done by proposal number
     * @return Returns the map of invoices based on key of proposal number.
     */
    public Map<Long, List<ContractsGrantsInvoiceDocument>> getInvoicesByAward(Collection<ContractsGrantsInvoiceDocument> invoices);
}