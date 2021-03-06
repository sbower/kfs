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
package org.kuali.kfs.module.tem.batch;

import java.util.Date;

import org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class CreditCardDataStagingToHistoricalStep extends AbstractStep{
    private CreditCardDataImportService creditCardDataImportService;
    
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return creditCardDataImportService.moveCreditCardDataToHistoricalExpenseTable();
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public CreditCardDataImportService getCreditCardDataImportService(){
        return this.creditCardDataImportService;
    }
    
    /**
     * 
     * This method...
     * @param argCreditCardDataImportService
     */
    public void setCreditCardDataImportService(CreditCardDataImportService argCreditCardDataImportService){
        this.creditCardDataImportService = argCreditCardDataImportService;
    }
}
