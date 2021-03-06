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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.validation.impl.FinalBilledIndicatorValidation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class tests the final billed indicator process
 */
@ConfigureContext(session = UserNameFixture.wklykins)
public class FinalBilledIndicatorTest extends CGInvoiceDocumentTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        document.getInvoiceGeneralDetail().setFinalBillIndicator(true);
        documentService.saveDocument(document);
        document.setInvoiceSuspensionCategories(ListUtils.EMPTY_LIST);
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).updateLastBilledDate(document);
    }

    public void testFinalInvoiceDocumentValidation() throws WorkflowException {
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        // need to switch to a user that is authorized to route doc, and route doc, so it goes to final and subsequent validation will pass
        // wcorbitt is the fund manager of proposal 11 - the proposal which the unit tests use here
        document.getDocumentHeader().getWorkflowDocument().switchPrincipal(UserNameFixture.wcorbitt.getPerson().getPrincipalId());
        documentService.routeDocument(document, "route test doc", new ArrayList<AdHocRouteRecipient>());

        FinalBilledIndicatorDocument firDocument = (FinalBilledIndicatorDocument) documentService.getNewDocument(FinalBilledIndicatorDocument.class);
        firDocument.getDocumentHeader().setDocumentDescription("Unit Test Document");
        FinalBilledIndicatorEntry entry = new FinalBilledIndicatorEntry();
        entry.setInvoiceDocumentNumber(document.getDocumentNumber());
        firDocument.addInvoiceEntry(entry);
        documentService.saveDocument(firDocument);
        assertTrue(FinalBilledIndicatorValidation.validateDocument(firDocument));
    }

    public void testInvoiceReversal() throws WorkflowException {
        FinalBilledIndicatorEntry entry = new FinalBilledIndicatorEntry();
        entry.setInvoiceDocumentNumber(document.getDocumentNumber());
        FinalBilledIndicatorDocument firDocument = (FinalBilledIndicatorDocument) documentService.getNewDocument(FinalBilledIndicatorDocument.class);
        firDocument.getDocumentHeader().setDocumentDescription("Unit Test Document");
        firDocument.addInvoiceEntry(entry);
        documentService.saveDocument(firDocument);
        Iterator<InvoiceAccountDetail> iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getInvoiceGeneralDetail().getProposalNumber());
            ContractsAndGrantsBillingAwardAccount awardAccount = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
            assertTrue(awardAccount.isFinalBilledIndicator());
        }

        firDocument.updateContractsGrantsInvoiceDocument();

        document = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(document.getDocumentNumber());
        assertFalse(document.getInvoiceGeneralDetail().isFinalBillIndicator());
        iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getInvoiceGeneralDetail().getProposalNumber());
            ContractsAndGrantsBillingAwardAccount awardAccount = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
            assertFalse(awardAccount.isFinalBilledIndicator());
        }
    }
}
