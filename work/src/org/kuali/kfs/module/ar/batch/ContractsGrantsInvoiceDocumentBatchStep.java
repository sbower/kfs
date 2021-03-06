/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceBatchCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * A step to run the cgInvoice document creation process.
 */
public class ContractsGrantsInvoiceDocumentBatchStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocumentBatchStep.class);

    protected String batchFileDirectoryName;
    protected ContractsGrantsInvoiceBatchCreateDocumentService cgInvoiceBatchDocumentCreateService;
    protected DateTimeService dateTimeService;

    /**
     * See the class description.
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());
        String errOutputFile1 = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.CGINVOICE_BATCH_VALIDATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        String errOutputFile2 = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.CGINVOICE_BATCH_CREATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        cgInvoiceBatchDocumentCreateService.processBatchInvoiceDocumentCreation(errOutputFile1, errOutputFile2);
        return true;
    }


    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    @Override
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * Sets the cgInvoiceDocumentCreateService attribute value.
     *
     * @param cgInvoiceDocumentCreateService The cgInvoiceDocumentCreateService to set.
     */
    public void setCgInvoiceBatchDocumentCreateService(ContractsGrantsInvoiceBatchCreateDocumentService cgInvoiceBatchDocumentCreateService) {
        this.cgInvoiceBatchDocumentCreateService = cgInvoiceBatchDocumentCreateService;
    }


    /**
     * This method sets the batch file directory name.
     *
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
