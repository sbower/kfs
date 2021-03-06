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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceGeneralDetail
 */
public enum InvoiceGeneralDetailFixture {

    INV_GNRL_DTL1("5678", "comment", "2011-05-02 - 2012-04-30", "MILE", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L),
    INV_GNRL_DTL2("5680", "comment", "2011-05-02 - 2012-04-30", "ANNU", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(13.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L),
    INV_GNRL_DTL3("5678", "comment", "2011-05-02 - 2012-04-30", "MNTH", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L),
    INV_GNRL_DTL4("5678", "comment", "2011-05-02 - 2012-04-30", "PDBS", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L),
    INV_GNRL_DTL5("5678", "comment", "2011-05-02 - 2012-04-30", "MILE", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L),
    INV_GNRL_DTL6("5678", "comment", "2011-05-02 - 2012-04-30", "MILE", true, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L),
    INV_GNRL_DTL7("5678", "comment", "2011-05-02 - 2012-04-30", "MILE", true, null, "GTMS - Grant - Milestone", new KualiDecimal(1000.00), new KualiDecimal(100000.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null, 11L);

    private String documentNumber;
    private String comment;
    private String awardDateRange;
    private String billingFrequencyCode;
    private boolean finalBillIndicator;
    private String billingPeriod;
    private String instrumentTypeCode;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private KualiDecimal newTotalBilled = KualiDecimal.ZERO;
    private KualiDecimal billedToDateAmount = KualiDecimal.ZERO;
    private KualiDecimal costShareAmount = KualiDecimal.ZERO;
    private Date lastBilledDate;
    private Long proposalNumber;

    private InvoiceGeneralDetailFixture(String documentNumber, String comment, String awardDateRange, String billingFrequencyCode, boolean finalBillIndicator, String billingPeriod, String instrumentTypeCode, KualiDecimal awardTotal, KualiDecimal newTotalBilled, KualiDecimal billedToDateAmount, KualiDecimal costShareAmount, Date lastBilledDate, Long proposalNumber) {
        this.documentNumber = documentNumber;
        this.comment = comment;
        this.awardDateRange = awardDateRange;
        this.billingFrequencyCode = billingFrequencyCode;
        this.finalBillIndicator = finalBillIndicator;
        this.billingPeriod = billingPeriod;
        this.instrumentTypeCode = instrumentTypeCode;
        this.awardTotal = awardTotal;
        this.newTotalBilled = newTotalBilled;
        this.billedToDateAmount = billedToDateAmount;
        this.costShareAmount = costShareAmount;
        this.lastBilledDate = lastBilledDate;
        this.proposalNumber = proposalNumber;
    }

    public InvoiceGeneralDetail createInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
        invoiceGeneralDetail.setDocumentNumber(this.documentNumber);
        invoiceGeneralDetail.setComment(comment);
        invoiceGeneralDetail.setAwardDateRange(awardDateRange);
        invoiceGeneralDetail.setBillingFrequencyCode(billingFrequencyCode);
        invoiceGeneralDetail.setFinalBillIndicator(finalBillIndicator);
        invoiceGeneralDetail.setBillingPeriod(billingPeriod);
        invoiceGeneralDetail.setInstrumentTypeCode(instrumentTypeCode);
        invoiceGeneralDetail.setAwardTotal(awardTotal);
        invoiceGeneralDetail.setNewTotalBilled(newTotalBilled);
        invoiceGeneralDetail.setBilledToDateAmount(billedToDateAmount);
        invoiceGeneralDetail.setCostShareAmount(costShareAmount);
        invoiceGeneralDetail.setLastBilledDate(lastBilledDate);
        invoiceGeneralDetail.setProposalNumber(proposalNumber);
        return invoiceGeneralDetail;
    }
}
