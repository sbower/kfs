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
package org.kuali.kfs.module.cg.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cg.businessobject.InvoiceIndicator;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MaintenanceDocumentTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This class tests InvoiceIndicator class
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class InvoiceIndicatorTest extends KualiTestBase {
    public MaintenanceDocument document;
    public DocumentService documentService;
    public static final String TYPE_CODE = "ABC";
    public static final String TYPE_DESCRIPTION = "print";
    public static final boolean ACTIVE = true;
    public static final Class<MaintenanceDocument> DOCUMENT_CLASS = MaintenanceDocument.class;
    private InvoiceIndicator invoiceIndicator;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        document = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getNewDocument("CGII");
        document.getDocumentHeader().setDocumentDescription("Test Document");
        documentService = SpringContext.getBean(DocumentService.class);
        invoiceIndicator = new InvoiceIndicator();
        invoiceIndicator.setInvoiceIndicator(TYPE_CODE);
        invoiceIndicator.setInvoiceIndicatorDescription(TYPE_DESCRIPTION);
        invoiceIndicator.setActive(ACTIVE);
        document.getNewMaintainableObject().setBusinessObject(invoiceIndicator);
        document.getNewMaintainableObject().setBoClass(invoiceIndicator.getClass());
    }


    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        MaintenanceDocumentTestUtils.testSaveDocument(document, documentService);
    }

    public void testGetNewDocument() throws Exception {
        Document document = (Document) documentService.getNewDocument("CGII");
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }
}
