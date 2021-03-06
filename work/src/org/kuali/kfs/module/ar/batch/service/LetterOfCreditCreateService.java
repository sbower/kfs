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
package org.kuali.kfs.module.ar.batch.service;

import java.io.IOException;
import java.io.PrintWriter;

import org.kuali.rice.core.api.util.type.KualiDecimal;



/**
 * Service interface for implementing methods to retrieve and validate awards to create contracts and grants invoice documents.
 */
public interface LetterOfCreditCreateService {

    /**
     * This method creates cashcontrol documents and payment application based on the loc creation type and loc value passed.
     *
     * @param customerNumber customer number used to create the cash control document
     * @param locCreationType LOC creation type (Award, Fund or Fund Group) used to determine how to process the LOC value
     * @param locValue LOC value, either Fund or Fund Group based on locCreationType
     * @param totalAmount amount to set on the new cash control doc
     * @param errorFile used to write out error messages
     * @return documentNumber for the newly created cash control document
     * @throws IOException if writing to the given error file has problems
     */
    public String createCashControlDocuments(String customerNumber, String locCreationType, String locValue, KualiDecimal totalAmount, PrintWriter errorFile) throws IOException;

    /**
     * The method validates if there are any existing cash control documents for the same locValue and customer number combination.
     *
     * @param customerNumber customer number used to search for existing documents
     * @param locCreationType LOC creation type (Award, Fund or Fund Group) used to determine how to process the LOC value
     * @param locValue LOC value, based on locCreationType, used to search for existing docs
     * @param errorFile used to write out error messages
     * @return true if there is an existing cash control document, false otherwise
     * @throws IOException if writing to the given error file has problems
     */
    public boolean validateCashControlDocument(String customerNumber, String locCreationType, String locValue, PrintWriter errorFile) throws IOException;

    /**
     * This method retrieves all the cash control and payment application docs with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     */
    public boolean routeLOCDocuments();

    /**
     * Processes Letters of Credit sorted by Fund
     *
     * @param batchFileDirectoryName the directory to write the report out to
     */
    public void processLettersOfCreditByFund(String batchFileDirectoryName);

    /**
     * Processes Letters of Credit sorted by Fund Group
     *
     * @param batchFileDirectoryName the directory to write the report out to
     */
    public void processLettersOfCreditByFundGroup(String batchFileDirectoryName);

}
