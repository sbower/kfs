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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Action class for Dunning Letter Distribution Summary.
 */
public class GenerateDunningLettersSummaryAction extends ContractsGrantsBillingSummaryActionBase {
    private static volatile DunningLetterService dunningLetterDistributionService;

    /**
     * 1. This method passes the control from Dunning Letter Distribution lookup to the Dunning Letter Distribution
     * Summary page. 2. Retrieves the list of selected awards by agency for sending Dunning Letters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        GenerateDunningLettersSummaryForm dunningLetterDistributionSummaryForm = (GenerateDunningLettersSummaryForm) form;
        String lookupResultsSequenceNumber = dunningLetterDistributionSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            Collection<GenerateDunningLettersLookupResult> generateDunningLettersLookupResults = getDunningLetterDistributionLookupResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

            dunningLetterDistributionSummaryForm.setGenerateDunningLettersLookupResults(generateDunningLettersLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method would create invoices for the list of awards. It calls the batch process to reuse the functionality to send
     * dunning letters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward generateDunningLetters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        GenerateDunningLettersSummaryForm dunningLetterDistributionSummaryForm = (GenerateDunningLettersSummaryForm) form;

        Person person = GlobalVariables.getUserSession().getPerson();
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        String lookupResultsSequenceNumber = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lookupResultsSequenceNumber = StringUtils.substringBetween(parameterName, ".number", ".");
        }

        Collection<GenerateDunningLettersLookupResult> lookupResults = getDunningLetterDistributionLookupResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        byte[] finalReport = getDunningLetterDistributionService().createDunningLettersForAllResults(lookupResults);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (finalReport.length > 0 && getDunningLetterDistributionService().createZipOfPDFs(finalReport, baos)) {
            WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.ZIP_MIME_TYPE, baos, "Dunning_Letters_" + getDateTimeService().toDateStringForFilename(getDateTimeService().getCurrentDate()) + KFSConstants.ReportGeneration.ZIP_FILE_EXTENSION);
            dunningLetterDistributionSummaryForm.setDunningLettersGenerated(true);
            return null;
        }
        else {
            KNSGlobalVariables.getMessageList().add(ArKeyConstants.DunningCampaignConstantsAndErrors.MESSAGE_DUNNING_CAMPAIGN_BATCH_NOT_SENT);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * To cancel the document, invoices are not created when the cancel method is called.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }

    /**
     * Convenience method to turn a set of multiple value lookup results of ContractsGrantsInvoiceDocuments into DunningLetterDistributionLookupResult data transfer objects
     * @param lookupResultsSequenceNumber the id of the multiple value lookup results
     * @param personId the user requesting results
     * @return a Collection of DunningLetterDistributionLookupResult data transfer objects
     */
    protected Collection<GenerateDunningLettersLookupResult> getDunningLetterDistributionLookupResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return getDunningLetterDistributionService().getPopulatedGenerateDunningLettersLookupResults(getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

    public DunningLetterService getDunningLetterDistributionService() {
        if (dunningLetterDistributionService == null) {
            dunningLetterDistributionService = SpringContext.getBean(DunningLetterService.class);
        }
        return dunningLetterDistributionService;
    }
}