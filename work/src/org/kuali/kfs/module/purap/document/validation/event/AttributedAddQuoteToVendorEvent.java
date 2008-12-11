/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;

public class AttributedAddQuoteToVendorEvent extends AttributedDocumentEventBase {

    private final PurchaseOrderVendorQuote purchaseOrderVendorQuote;
    
    public AttributedAddQuoteToVendorEvent(Document document, PurchaseOrderVendorQuote purchaseOrderVendorQuote) {
        super("Adding Quote To Vendor for Purchase Order " + getDocumentId(document), null, document);
        this.document = document;
        this.purchaseOrderVendorQuote = purchaseOrderVendorQuote;
    }

    public PurchaseOrderVendorQuote getPurchaseOrderVendorQuote() {
        return purchaseOrderVendorQuote;
    }

}
