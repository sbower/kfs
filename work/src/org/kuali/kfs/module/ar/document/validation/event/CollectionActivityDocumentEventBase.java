/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Event class for Collection Activity Document.
 */
public abstract class CollectionActivityDocumentEventBase extends KualiDocumentEventBase implements CollectionActivityDocumentEvent {

    private static final Logger LOG = Logger.getLogger(CollectionActivityDocumentEventBase.class);

    private final CollectionEvent collectionEvent;

    /**
     * Constructor for the class.
     *
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param event
     */
    public CollectionActivityDocumentEventBase(String description, String errorPathPrefix, Document document, CollectionEvent collectionEvent) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.collectionEvent = (CollectionEvent) ObjectUtils.deepCopy(collectionEvent);

        logEvent();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.event.CollectionActivityDetailEvent#getEvent()
     */
    @Override
    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#validate()
     */
    @Override
    public void validate() {
        super.validate();
        if (ObjectUtils.isNull(getCollectionEvent())) {
            throw new IllegalArgumentException("invalid (null) collection event");
        }
    }

    /**
     * Logs the event type and some information about the associated event
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (ObjectUtils.isNull(collectionEvent)) {
            logMessage.append("null collection event");
        }
        else {
            logMessage.append(" collection event# ");
            logMessage.append(collectionEvent.getId());
        }

        LOG.debug(logMessage);
    }
}
