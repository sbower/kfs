/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;


/**
 * Implementations of this interface provide access to persisted Awards.
 */
public interface AwardAccountObjectCodeTotalBilledDao {

    /**
     * This method returns a list of AwardAccountObjectCodeTotalBilled objects corresponding to a list of Award Accounts
     * (matching on the chart code, proposal number and account number of each Award Account).
     *
     * @param awardAccounts list of AwardAcounts used to find AwardAccountObjectCodeTotalBilled objects
     * @return List of AwardAccountObjectCodeTotalBilled objects
     */
    public List<AwardAccountObjectCodeTotalBilled> getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(List<ContractsAndGrantsBillingAwardAccount> awardAccounts);

}
