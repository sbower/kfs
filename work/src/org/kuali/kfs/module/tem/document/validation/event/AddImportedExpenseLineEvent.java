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
package org.kuali.kfs.module.tem.document.validation.event;

import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

/**
 * Event triggered when an {@link ImportedExpense} instance
 * is added to a {@link Document}
 */
public class AddImportedExpenseLineEvent<E extends ImportedExpense> extends AttributedDocumentEventBase implements TemExpenseLineEvent<E> {

    private final E expense;

    /**
     * Constructs an AddImportedExpenseLineEvent with the given errorPathPrefix, document, and otherExpense.
     *
     * @param errorPathPrefix
     * @param document
     * @param groupTraveler
     */
    public AddImportedExpenseLineEvent(String errorPathPrefix, Document document, E expense) {
        super("adding expenseLine to document " + getDocumentId(document), errorPathPrefix, document);
        this.expense = expense;
    }

    @Override
    public E getExpenseLine() {
        return expense;
    }

}
