/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for the Payment History Report.
 */
public class ContractsGrantsPaymentHistoryReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsPaymentHistoryReport> displayList = new ArrayList<ContractsGrantsPaymentHistoryReport>();

        lookupFormFields.put(ArPropertyConstants.INVOICE_DOCUMENT_TYPE, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        Collection<CashControlDocument> cgCashControlDocs = getLookupService().findCollectionBySearchHelper(CashControlDocument.class, lookupFormFields, true);


        // build search result fields
        // For each Cash Control doc, get a list of payment app doc numbers
        for (CashControlDocument cashControlDoc : cgCashControlDocs) {

            List<CashControlDetail> cashControlDetailList = cashControlDoc.getCashControlDetails();

            // For each payment app doc number get a list of payment application docs
            for (CashControlDetail cashControlDetail : cashControlDetailList) {

                PaymentApplicationDocument paymentApplicationDoc = cashControlDetail.getReferenceFinancialDocument();

                // If the retrieved APP went to final

                final boolean isFinal = paymentApplicationDoc.getDocumentHeader().getWorkflowDocument().isFinal();

                if (isFinal) {

                    List<InvoicePaidApplied> appliedPayments = paymentApplicationDoc.getInvoicePaidApplieds();

                    for (InvoicePaidApplied appliedPayment : appliedPayments) {
                        ContractsGrantsPaymentHistoryReport cgPaymentHistoryReport = new ContractsGrantsPaymentHistoryReport();

                        cgPaymentHistoryReport.setPaymentNumber(paymentApplicationDoc.getDocumentNumber());
                        cgPaymentHistoryReport.setPaymentDate(new java.sql.Date(paymentApplicationDoc.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis()));

                        cgPaymentHistoryReport.setCustomerNumber(cashControlDetail.getCustomerNumber());
                        if (!ObjectUtils.isNull(cashControlDetail.getCustomer())) {
                            cgPaymentHistoryReport.setCustomerName(cashControlDetail.getCustomer().getCustomerName());
                        }
                        cgPaymentHistoryReport.setPaymentAmount(appliedPayment.getInvoiceItemAppliedAmount());
                        cgPaymentHistoryReport.setInvoiceNumber(appliedPayment.getFinancialDocumentReferenceInvoiceNumber());
                        cgPaymentHistoryReport.setInvoiceAmount(appliedPayment.getCustomerInvoiceDocument().getTotalDollarAmount());

                        Map<String, String> criteria = new HashMap<String, String>();
                        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, cgPaymentHistoryReport.getInvoiceNumber());
                        ContractsGrantsInvoiceDocument cgInvoiceDocument = businessObjectService.findByPrimaryKey(ContractsGrantsInvoiceDocument.class, criteria);

                        cgPaymentHistoryReport.setAwardNumber(cgInvoiceDocument.getInvoiceGeneralDetail().getProposalNumber());
                        cgPaymentHistoryReport.setReversedIndicator(appliedPayment.getCustomerInvoiceDocument().isInvoiceReversal());

                        cgPaymentHistoryReport.setAppliedIndicator(true);

                        displayList.add(cgPaymentHistoryReport);
                    }

                }
            }
        }
        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }
}