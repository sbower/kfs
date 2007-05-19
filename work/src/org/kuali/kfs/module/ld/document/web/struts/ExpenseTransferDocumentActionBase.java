/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.Timer;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.exceptions.AccountingLineParserException;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.rule.event.DeleteAccountingLineEvent;
import org.kuali.kfs.rule.event.UpdateAccountingLineEvent;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.ui.AccountingLineDecorator;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.document.LaborExpenseTransferDocument;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles UI actions for all shared methods of financial documents.
 */
public class ExpenseTransferDocumentActionBase extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenseTransferDocumentActionBase.class);
    

    /**
     * Adds check for accountingLine updates, generates and dispatches any events caused by such updates
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Timer t0 = new Timer("KualiFinancialDocumentFormBase.execute");
        ExpenseTransferDocumentFormBase transForm = (ExpenseTransferDocumentFormBase) form;

        // handle changes to accountingLines
        if (transForm.hasDocumentId()) {
            LaborExpenseTransferDocument financialDocument = transForm.getFinancialDocument();

            processAccountingLines(financialDocument, transForm, KFSConstants.SOURCE);
            processAccountingLines(financialDocument, transForm, KFSConstants.TARGET);
        }

        // This is after a potential handleUpdate(), to display automatically cleared overrides following a route or save.
        processAccountingLineOverrides(transForm);

        // proceed as usual
        ActionForward result = super.execute(mapping, form, request, response);
        t0.log();
        return result;
    }

    /**
     * All document-creation gets routed through here
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        ExpenseTransferDocumentFormBase tform = (ExpenseTransferDocumentFormBase) kualiDocumentFormBase;

        // reset baseline accountingLine lists
        tform.getBaselineSourceAccountingLines().clear();
        tform.getBaselineTargetAccountingLines().clear();

        // reset decorator lists
        tform.getSourceLineDecorators().clear();
        tform.getTargetLineDecorators().clear();
    }

    /**
     * All document-load operations get routed through here
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);


        ExpenseTransferDocumentFormBase tform = (ExpenseTransferDocumentFormBase) kualiDocumentFormBase;

        // populate baseline accountingLine lists
        List baselineSourceLines = tform.getBaselineSourceAccountingLines();
        baselineSourceLines.clear();
        baselineSourceLines.addAll(tform.getFinancialDocument().getSourceAccountingLines());

        List baselineTargetLines = tform.getBaselineTargetAccountingLines();
        baselineTargetLines.clear();
        baselineTargetLines.addAll(tform.getFinancialDocument().getTargetAccountingLines());

        // populate decorator lists
        tform.resetSourceLineDecorators(baselineSourceLines.size());
        tform.resetTargetLineDecorators(baselineTargetLines.size());

        // clear out the new accounting line holders
        tform.setNewSourceLine(null);
        tform.setNewTargetLine(null);

        processAccountingLineOverrides(tform);
    }


    // Set of actions for which updateEvents should be generated
    protected static final Set UPDATE_EVENT_ACTIONS;
    static {
        String[] updateEventActions = { KFSConstants.SAVE_METHOD, KFSConstants.ROUTE_METHOD, KFSConstants.APPROVE_METHOD, KFSConstants.BLANKET_APPROVE_METHOD };
        UPDATE_EVENT_ACTIONS = new HashSet();
        for (int i = 0; i < updateEventActions.length; ++i) {
            UPDATE_EVENT_ACTIONS.add(updateEventActions[i]);
        }
    }

    /**
     * @param transForm
     */
    protected void processAccountingLineOverrides(ExpenseTransferDocumentFormBase transForm) {
        processAccountingLineOverrides(transForm.getNewSourceLine());
        processAccountingLineOverrides(transForm.getNewTargetLine());
        if (transForm.hasDocumentId()) {
            LaborExpenseTransferDocument financialDocument = transForm.getFinancialDocument();

            processAccountingLineOverrides(financialDocument.getSourceAccountingLines());
            processAccountingLineOverrides(financialDocument.getTargetAccountingLines());
        }
    }

    /**
     * @param line
     */
    protected static void processAccountingLineOverrides(ExpenseTransferAccountingLine line) {
        processAccountingLineOverrides(Arrays.asList(new AccountingLine[] { line }));
    }

    /**
     * @param accountingLines
     */
    protected static void processAccountingLineOverrides(List accountingLines) {
        if (!accountingLines.isEmpty()) {
            SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(accountingLines, AccountingLineOverride.REFRESH_FIELDS);

            for (Iterator i = accountingLines.iterator(); i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                AccountingLineOverride.processForOutput(line);
            }
        }
    }

    /**
     * @param transDoc
     * @param transForm
     * @param lineSet
     */
    private void processAccountingLines(LaborExpenseTransferDocument transDoc, ExpenseTransferDocumentFormBase transForm, String lineSet) {
        // figure out which set of lines we're looking at
        List baseLines;
        List formLines;
        List<AccountingLineDecorator> decorators;
        String pathPrefix;
        if (lineSet.equals(KFSConstants.SOURCE)) {
            baseLines = transForm.getBaselineSourceAccountingLines();
            formLines = transDoc.getSourceAccountingLines();
            decorators = transForm.getSourceLineDecorators(formLines.size());
            pathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME;
        }
        else {
            baseLines = transForm.getBaselineTargetAccountingLines();
            formLines = transDoc.getTargetAccountingLines();
            decorators = transForm.getTargetLineDecorators(formLines.size());
            pathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME;
        }

        Map baseLineMap = new HashMap();
        for (Iterator i = baseLines.iterator(); i.hasNext();) {
            AccountingLine baseLine = (AccountingLine) i.next();
            baseLineMap.put(baseLine.getSequenceNumber(), baseLine);
        }

        // find and process corresponding form and baselines
        int index = 0;
        for (Iterator i = formLines.iterator(); i.hasNext(); index++) {
            AccountingLine formLine = (AccountingLine) i.next();
            AccountingLine baseLine = (AccountingLine) baseLineMap.get(formLine.getSequenceNumber());
            AccountingLineDecorator decorator = decorators.get(index);

            // always update decorator
            handleDecorator(formLine, baseLine, decorator);

            // only generate update events for specific action methods
            String methodToCall = transForm.getMethodToCall();
            if (UPDATE_EVENT_ACTIONS.contains(methodToCall)) {
                handleUpdate(transDoc, pathPrefix + "[" + index + "]", formLine, baseLine);
            }
        }
    }

    /**
     * @param formLine
     * @param baseLine
     * @param decorator
     */
    private void handleDecorator(AccountingLine formLine, AccountingLine baseLine, AccountingLineDecorator decorator) {
        // if line is new, or line hasn't changed, make non-revertible;
        // otherwise, revertible
        if ((baseLine == null) || formLine.isLike(baseLine)) {
            decorator.setRevertible(false);
        }
        else {
            decorator.setRevertible(true);
        }
    }

    /**
     * @param transDoc
     * @param errorPathPrefix
     * @param formLine
     * @param baseLine
     */
    private void handleUpdate(LaborExpenseTransferDocument transDoc, String errorPathPrefix, AccountingLine formLine, AccountingLine baseLine) {
        if ((baseLine != null) && !formLine.isLike(baseLine)) {
            // reluctantly refresh BOs for clearOverridesThatBecameUnneeded()
            formLine.refresh();
            clearOverridesThatBecameUnneeded(formLine);
            // the rule itself is responsible for adding error messages to the global ErrorMap
            SpringServiceLocator.getKualiRuleService().applyRules(new UpdateAccountingLineEvent(errorPathPrefix, transDoc, baseLine, formLine));
        }
    }

    /**
     * Automatically clears any overrides that have become unneeded. This is for accounting lines that were changed right before
     * final actions like route. Normally the unneeded overrides are cleared in accountingLineOverrideField.tag instead, but that
     * requires another form submit. This method shouldn't be called on lines that haven't changed, to avoid automatically changing
     * read-only lines. This cannot be done in the Rule because Rules cannot change the AccountingLines; they only get a deepCopy.
     * 
     * @param formLine
     */
    private static void clearOverridesThatBecameUnneeded(AccountingLine formLine) {
        AccountingLineOverride currentlyNeeded = AccountingLineOverride.determineNeededOverrides(formLine);
        AccountingLineOverride currentOverride = AccountingLineOverride.valueOf(formLine.getOverrideCode());
        if (!currentOverride.isValidMask(currentlyNeeded)) {
            // todo: handle unsupported combinations of overrides (not a problem until we allow certain multiple overrides)
        }
        formLine.setOverrideCode(currentOverride.mask(currentlyNeeded).getCode());
    }

    /**
     * This method will revert a TargetAccountingLine by overwriting its current values with the values in the corresponding
     * baseline accountingLine. This assumes that the user presses the revert button for a specific accounting line on the document
     * and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward revertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        LaborExpenseTransferDocument financialDocument = financialDocumentForm.getFinancialDocument();

        int revertIndex = getSelectedLine(request);

        ExpenseTransferTargetAccountingLine originalLine = financialDocumentForm.getBaselineTargetAccountingLine(revertIndex);
        ExpenseTransferTargetAccountingLine brokenLine = financialDocument.getExpenseTransferTargetAccountingLine(revertIndex);

        if (revertAccountingLine(financialDocumentForm, revertIndex, originalLine, brokenLine)) {
            financialDocumentForm.getTargetLineDecorator(revertIndex).setRevertible(false);
        }

        // no business rules to check, no events to create

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method will revert a SourceAccountingLine by overwriting its current values with the values in the corresponding
     * baseline accountingLine. This assumes that the user presses the revert button for a specific accounting line on the document
     * and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward revertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;
        LaborExpenseTransferDocument financialDocument = financialDocumentForm.getFinancialDocument();

        int revertIndex = getSelectedLine(request);

        ExpenseTransferSourceAccountingLine originalLine = financialDocumentForm.getBaselineSourceAccountingLine(revertIndex);
        ExpenseTransferSourceAccountingLine brokenLine = financialDocument.getExpenseTransferSourceAccountingLine(revertIndex);

        if (revertAccountingLine(financialDocumentForm, revertIndex, originalLine, brokenLine)) {
            financialDocumentForm.getSourceLineDecorator(revertIndex).setRevertible(false);
        }

        // no business rules to check, no events to create

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Overwrites field values in the given brokenLine with those in the given originalLine, if the two accountingLines differ.
     * 
     * @param transForm
     * @param revertIndex
     * @param originalLine
     * @param newerLine
     * @return true if and only if the brokenLine was actually changed
     */
    protected boolean revertAccountingLine(ExpenseTransferDocumentFormBase transForm, int revertIndex, AccountingLine originalLine, AccountingLine newerLine) {
        boolean reverted = false;

        SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(originalLine);

        // *always* revert (so that if someone manually changes the line to its original values, then hits revert, they won't get an
        // error message saying "couldn't revert")
        newerLine.copyFrom(originalLine);
        reverted = true;
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_REVERT_SUCCESSFUL);

        return reverted;
    }


    /**
     * This method will remove a TargetAccountingLine from a FinancialDocument. This assumes that the user presses the delete
     * button for a specific accounting line on the document and that the document is represented by a
     * FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;

        boolean rulePassed = false;
        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";

        // check business rule, if there is a baseline copy
        // (accountingLines without baselines haven't been persisted yet, so they can safely be deleted)
        if (financialDocumentForm.hasBaselineTargetAccountingLine(deleteIndex)) {
            ExpenseTransferTargetAccountingLine baseline = financialDocumentForm.getBaselineTargetAccountingLine(deleteIndex);
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(baseline);

            rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), baseline, false));
        }
        else {
            rulePassed = true;
        }

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(false, financialDocumentForm, deleteIndex);
        }
        else {
            financialDocumentForm.getTargetLineDecorator(deleteIndex).setRevertible(true);

            String[] errorParams = new String[] { "target", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getErrorMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove a SourceAccountingLine from a FinancialDocument. This assumes that the user presses the delete
     * button for a specific accounting line on the document and that the document is represented by a
     * FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;

        boolean rulePassed = false;
        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";

        // check business rule, if there is a baseline copy
        // (accountingLines without baselines haven't been persisted yet, so they can safely be deleted)
        if (financialDocumentForm.hasBaselineSourceAccountingLine(deleteIndex)) {
            ExpenseTransferSourceAccountingLine baseline = financialDocumentForm.getBaselineSourceAccountingLine(deleteIndex);
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(baseline);

            rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), baseline, false));
        }
        else {
            rulePassed = true;
        }

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(true, financialDocumentForm, deleteIndex);
        }
        else {
            financialDocumentForm.getSourceLineDecorator(deleteIndex).setRevertible(true);

            String[] errorParams = new String[] { "source", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getErrorMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Deletes the source or target accountingLine with the given index from the given form. Assumes that the rule- and form-
     * validation have already occurred.
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param deleteIndex
     */
    protected void deleteAccountingLine(boolean isSource, ExpenseTransferDocumentFormBase financialDocumentForm, int deleteIndex) {
        if (isSource) {
            // remove from document
            financialDocumentForm.getFinancialDocument().getSourceAccountingLines().remove(deleteIndex);

            // remove baseline duplicate and decorator
            if (deleteIndex < financialDocumentForm.getBaselineSourceAccountingLines().size()) {
                financialDocumentForm.getBaselineSourceAccountingLines().remove(deleteIndex);
            }
            financialDocumentForm.getSourceLineDecorators().remove(deleteIndex);
        }
        else {
            // remove from document
            financialDocumentForm.getFinancialDocument().getTargetAccountingLines().remove(deleteIndex);

            // remove baseline duplicate and decorator
            if (deleteIndex < financialDocumentForm.getBaselineTargetAccountingLines().size()) {
                financialDocumentForm.getBaselineTargetAccountingLines().remove(deleteIndex);
            }
            financialDocumentForm.getTargetLineDecorators().remove(deleteIndex);
        }
    }

    /**
     * This method will add a TargetAccountingLine to a FinancialDocument. This assumes that the user presses the add button for
     * a specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase. It first
     * validates the line for data integrity and then checks appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;

        ExpenseTransferTargetAccountingLine line = financialDocumentForm.getNewTargetLine();

        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // add accountingLine
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
            insertAccountingLine(false, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewTargetLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This action executes an insert of a SourceAccountingLine into a document only after validating the accounting line and
     * checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase financialDocumentForm = (ExpenseTransferDocumentFormBase) form;

        ExpenseTransferSourceAccountingLine line = financialDocumentForm.getNewSourceLine();

        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        if (rulePassed) {
            // add accountingLine
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
            insertAccountingLine(true, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewSourceLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds the given accountingLine to the appropriate form-related datastructures.
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param line
     */
    protected void insertAccountingLine(boolean isSource, ExpenseTransferDocumentFormBase financialDocumentForm, ExpenseTransferAccountingLine line) {
        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        LaborExpenseTransferDocument tdoc = financialDocumentForm.getFinancialDocument();

        if (isSource) {
            // add it to the document
            tdoc.addExpenseTransferSourceAccountingLine((ExpenseTransferSourceAccountingLine) line);

            // add it to the baseline, to prevent generation of spurious update events
            financialDocumentForm.getBaselineSourceAccountingLines().add((ExpenseTransferSourceAccountingLine) line);

            // add the decorator
            financialDocumentForm.getSourceLineDecorators().add(decorator);
        }
        else {
            // add it to the document
            tdoc.addExpenseTransferTargetAccountingLine((ExpenseTransferTargetAccountingLine) line);

            // add it to the baseline, to prevent generation of spurious update events
            financialDocumentForm.getBaselineTargetAccountingLines().add((ExpenseTransferTargetAccountingLine) line);

            // add the decorator
            financialDocumentForm.getTargetLineDecorators().add(decorator);
        }
    }

    /**
     * Method that will take the current document, copy it, replace all references to doc header id with a new one, clear pending
     * entries, clear notes, and reset version numbers
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.copy(mapping, form, request, response);
       
        ExpenseTransferDocumentFormBase tmpForm = (ExpenseTransferDocumentFormBase) form;

        // KULEDOCS-1440: need to reset base accounting lines since when doc number changes on copy base lines would still reference
        // old doc number causing revert button to show up
        tmpForm.setBaselineSourceAccountingLines(tmpForm.getFinancialDocument().getSourceAccountingLines());
        tmpForm.setBaselineTargetAccountingLines(tmpForm.getFinancialDocument().getTargetAccountingLines());
        tmpForm.getSourceLineDecorators().clear();
        tmpForm.getTargetLineDecorators().clear();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show
     * all of the labels for each of the accounting line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase tmpForm = (ExpenseTransferDocumentFormBase) form;
        tmpForm.setHideDetails(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is triggered when the user toggles the show/hide button to "hide" thus making the UI render without any of the
     * accounting line labels/descriptions showing up underneath the values in the UI.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase tmpForm = (ExpenseTransferDocumentFormBase) form;
        tmpForm.setHideDetails(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for a
     * source accounting line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineIndex = getSelectedLine(request);

        ExpenseTransferSourceAccountingLine line = (ExpenseTransferSourceAccountingLine) ObjectUtils.deepCopy(((ExpenseTransferDocumentFormBase) form).getFinancialDocument().getSourceAccountingLine(lineIndex));

        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for a
     * target accounting line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineIndex = getSelectedLine(request);

        ExpenseTransferTargetAccountingLine line = (ExpenseTransferTargetAccountingLine) ObjectUtils.deepCopy(((ExpenseTransferDocumentFormBase) form).getFinancialDocument().getTargetAccountingLine(lineIndex));

        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * This method handles preparing all of the accounting line data so that it can be pushed up to the balance inquiries for
     * populating the search criteria of each.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param line
     * @return ActionForward
     */
    private ActionForward performBalanceInquiryForAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, AccountingLine line) {
        // build out base path for return location
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObject(form);

        // now add required parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        // need this next param b/c the lookup's return back will overwrite
        // the original doc form key
        parameters.put(KFSConstants.BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        if (StringUtils.isNotBlank(line.getBudgetYear())) {
            parameters.put("budgetYear", line.getBudgetYear());
        }
        if (StringUtils.isNotBlank(line.getReferenceOriginCode())) {
            parameters.put("referenceOriginCode", line.getReferenceOriginCode());
        }
        if (StringUtils.isNotBlank(line.getReferenceNumber())) {
            parameters.put("referenceNumber", line.getReferenceNumber());
        }
        if (StringUtils.isNotBlank(line.getReferenceTypeCode())) {
            parameters.put("referenceTypeCode", line.getReferenceTypeCode());
        }
        if (StringUtils.isNotBlank(line.getDebitCreditCode())) {
            parameters.put("debitCreditCode", line.getDebitCreditCode());
        }
        if (StringUtils.isNotBlank(line.getChartOfAccountsCode())) {
            parameters.put("chartOfAccountsCode", line.getChartOfAccountsCode());
        }
        if (StringUtils.isNotBlank(line.getAccountNumber())) {
            parameters.put("accountNumber", line.getAccountNumber());
        }
        if (StringUtils.isNotBlank(line.getFinancialObjectCode())) {
            parameters.put("financialObjectCode", line.getFinancialObjectCode());
        }
        if (StringUtils.isNotBlank(line.getSubAccountNumber())) {
            parameters.put("subAccountNumber", line.getSubAccountNumber());
        }
        if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
            parameters.put("financialSubObjectCode", line.getFinancialSubObjectCode());
        }
        if (StringUtils.isNotBlank(line.getProjectCode())) {
            parameters.put("projectCode", line.getProjectCode());
        }
        if (StringUtils.isNotBlank(line.getObjectTypeCode())) {
            parameters.put("objectTypeCode", line.getObjectTypeCode());
        }

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.BALANCE_INQUIRY_REPORT_MENU_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.save(mapping, form, request, response);
        
        // KULEDOCS-1443: For the revert button, set the new baseline accounting lines as the most recently saved lines
        ExpenseTransferDocumentFormBase tmpForm = (ExpenseTransferDocumentFormBase) form;
        tmpForm.setBaselineSourceAccountingLines(tmpForm.getFinancialDocument().getSourceAccountingLines());
        tmpForm.setBaselineTargetAccountingLines(tmpForm.getFinancialDocument().getTargetAccountingLines());
        
        return forward;
    }

    /**
     * Extended to make sure that the baseline accounting lines are being perpetuated.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase tmpForm = (ExpenseTransferDocumentFormBase) form;
        tmpForm.setBaselineSourceAccountingLines(tmpForm.getFinancialDocument().getSourceAccountingLines());
        tmpForm.setBaselineTargetAccountingLines(tmpForm.getFinancialDocument().getTargetAccountingLines());
        
        return super.route(mapping, form, request, response);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExpenseTransferDocumentFormBase tmpForm = (ExpenseTransferDocumentFormBase) form;
        tmpForm.setBaselineSourceAccountingLines(tmpForm.getFinancialDocument().getSourceAccountingLines());
        tmpForm.setBaselineTargetAccountingLines(tmpForm.getFinancialDocument().getTargetAccountingLines());
        
        return super.approve(mapping, form, request, response);
    }
    
}
