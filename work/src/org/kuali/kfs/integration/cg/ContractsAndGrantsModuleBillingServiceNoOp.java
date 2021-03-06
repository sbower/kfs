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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Do nothing implementation of the ContractsAndGrantsModuleBillingService interface
 */
public class ContractsAndGrantsModuleBillingServiceNoOp implements ContractsAndGrantsModuleBillingService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleBillingServiceNoOp.class);

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<ContractsAndGrantsAward>();
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService#updateAwardIfNecessary(java.lang.Long, org.kuali.kfs.integration.cg.ContractsAndGrantsAward)
     */
    @Override
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(Long proposalNumber, ContractsAndGrantsBillingAward currentAward ) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLastBilledDateToAwardAccount(java.util.Map,
     *      java.lang.String, java.sql.Date)
     */
    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLastBilledDateToAward(java.lang.Long,
     *      java.sql.Date)
     */
    @Override
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLOCCreationTypeToAward(java.lang.Long,
     *      java.lang.String)
     */
    @Override
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setAmountToDrawToAwardAccount(java.util.Map,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLOCReviewIndicatorToAwardAccount(java.util.Map,
     *      boolean)
     */
    @Override
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledToAwardAccount(java.util.Map, boolean)
     */
    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledAndLastBilledDateToAwardAccount(java.util.Map, boolean, java.lang.String, java.sql.Date, java.lang.String)
     */
    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, boolean invoiceReversal, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }
}
