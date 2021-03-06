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
package org.kuali.kfs.integration.cg;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Module service specific to those methods needed to support Contracts & Grants Billing.
 * If Contracts & Grants Billing is not used at your institution, there is no need to implement this service; simply continue to use
 * the provided NoOp implementation of the service.
 */
public interface ContractsAndGrantsModuleBillingService {

    /**
     * This method would return list of business object - in this case Awards for CG Invoice functionality in AR.
     *
     * @param fieldValues
     * @param unbounded
     * @return
     */
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded);

    /**
     * Compares the Proposal Number passed in with that in the Award object.  If they are the same, it returns the
     * original object.  Otherwise, it retrieves the Award based on the proposalNumber and returns it.
     *
     * @param proposalNumber
     * @param currentAward
     * @return
     */
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(Long proposalNumber, ContractsAndGrantsBillingAward currentAward);

    /**
     * This method sets last Billed Date to award Account.
     *
     * @param criteria
     * @param invoiceReversal
     * @param lastBilledDate
     */
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, java.sql.Date lastBilledDate);

    /**
     * This method sets last billed Date to Award
     *
     * @param proposalNumber
     * @param lastBilledDate
     */
    public void setLastBilledDateToAward(Long proposalNumber, java.sql.Date lastBilledDate);

    /**
     * This method sets value of LOC Creation Type to Award
     *
     * @param proposalNumber
     * @param locCreationType
     */
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType);

    /**
     * This method sets amount to draw to award Account.
     *
     * @param criteria
     * @param amountToDraw
     */
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw);

    /**
     * This method sets loc review indicator to award Account.
     *
     * @param criteria
     * @param locReviewIndicator
     */
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator);

    /**
     * This method sets final billed to award Account.
     *
     * @param criteria
     * @param finalBilled
     */
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled);

    /**
     * This method sets final billed and last billed date to Award Account.
     *
     * @param mapKey
     * @param finalBilled
     * @param invoiceReversal
     * @param lastBilledDate
     */
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, boolean invoiceReversal, java.sql.Date lastBilledDate);

}