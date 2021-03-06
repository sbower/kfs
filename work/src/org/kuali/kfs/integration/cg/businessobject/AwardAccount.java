/*
 * Copyright 2006-2009 The Kuali Foundation
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

package org.kuali.kfs.integration.cg.businessobject;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents an association between an award and an account. It's like a reference to the account from the award. This
 * way an award can maintain a collection of these references instead of owning accounts directly.
 */
public class AwardAccount implements ContractsAndGrantsBillingAwardAccount, MutableInactivatable {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String principalId;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;
    private boolean finalBilledIndicator;
    private KualiDecimal amountToDraw = KualiDecimal.ZERO;
    private boolean letterOfCreditReviewIndicator;
    private boolean active = true;

    private Account account;
    private Chart chartOfAccounts;
    private Person projectDirector;

    /**
     * Gets the amountToDraw attribute.
     *
     * @return Returns the amountToDraw.
     */

    @Override
    public KualiDecimal getAmountToDraw() {
        return amountToDraw;
    }

    /**
     * Sets the amountToDraw attribute value.
     *
     * @param amountToDraw The amountToDraw to set.
     */
    public void setAmountToDraw(KualiDecimal amountToDraw) {
        this.amountToDraw = amountToDraw;
    }

    /**
     * Gets the letterOfCreditReviewIndicator attribute.
     *
     * @return Returns the letterOfCreditReviewIndicator.
     */

    @Override
    public boolean isLetterOfCreditReviewIndicator() {
        return letterOfCreditReviewIndicator;
    }

    /**
     * Sets the letterOfCreditReviewIndicator attribute value.
     *
     * @param letterOfCreditReviewIndicator The letterOfCreditReviewIndicator to set.
     */
    public void setLetterOfCreditReviewIndicator(boolean letterOfCreditReviewIndicator) {
        this.letterOfCreditReviewIndicator = letterOfCreditReviewIndicator;
    }


    /**
     * Gets the finalBilledIndicator attribute.
     *
     * @return Returns the finalBilledIndicator.
     */

    @Override
    public boolean isFinalBilledIndicator() {
        return finalBilledIndicator;
    }

    /**
     * Sets the finalBilledIndicator attribute value.
     *
     * @param finalBilledIndicator The finalBilledIndicator to set.
     */
    public void setFinalBilledIndicator(boolean finalBilledIndicator) {
        this.finalBilledIndicator = finalBilledIndicator;
    }

    /**
     * Gets the currentLastBilledDate attribute.
     *
     * @return Returns the currentLastBilledDate.
     */

    @Override
    public Date getCurrentLastBilledDate() {
        return currentLastBilledDate;
    }

    /**
     * Sets the currentLastBilledDate attribute value.
     *
     * @param currentLastBilledDate The currentLastBilledDate to set.
     */
    public void setCurrentLastBilledDate(Date currentLastBilledDate) {
        this.currentLastBilledDate = currentLastBilledDate;
    }

    /**
     * Gets the previousLastBilledDate attribute.
     *
     * @return Returns the previousLastBilledDate.
     */

    @Override
    public Date getPreviousLastBilledDate() {
        return previousLastBilledDate;
    }

    /**
     * Sets the previousLastBilledDate attribute value.
     *
     * @param previousLastBilledDate The previousLastBilledDate to set.
     */
    public void setPreviousLastBilledDate(Date previousLastBilledDate) {
        this.previousLastBilledDate = previousLastBilledDate;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProposalNumber()
     */
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getChartOfAccountsCode()
     */
    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAccountNumber()
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public Person getProjectDirector() {
        return projectDirector;
    }

    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirectorName()
     */
    @Override
    public String getProjectDirectorName() {
        if (!ObjectUtils.isNull(getProjectDirector())) {
            return getProjectDirector().getName();
        }
        return null;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    @Override
    public void refresh() {
    }

}