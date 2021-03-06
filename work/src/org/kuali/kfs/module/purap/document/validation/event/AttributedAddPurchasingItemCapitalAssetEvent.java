/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.rice.krad.document.Document;

/**
 * Add Purchasing Item Capital Asset Event. 
 * This is triggered when a user presses the add button for a given document's item capital asset line.
 */
public final class AttributedAddPurchasingItemCapitalAssetEvent extends AttributedPurchasingItemCapitalAssetEventBase {
    /**
     * Constructs an AddItemEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param itemCapitalAsset the item being added 
     */
    public AttributedAddPurchasingItemCapitalAssetEvent(String errorPathPrefix, Document document, ItemCapitalAsset itemCapitalAsset) {
        super("adding item capital asset to document " + getDocumentId(document), errorPathPrefix, document, itemCapitalAsset);
    }
}
