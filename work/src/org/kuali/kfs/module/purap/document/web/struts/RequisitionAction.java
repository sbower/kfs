/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.DefaultPrincipalAddress;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAddPurchasingAccountsPayableItemEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts Action for Requisition document.
 */
public class RequisitionAction extends PurchasingActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionAction.class);
    private RequisitionService requisitionService;
    
    /**
     * save the document without any validations.....
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //call the super save to save the document without validations...
        
        ActionForward actionForward = super.save(mapping, form, request, response);
        
        //we need to make "calculated" to false so that the "below lines"
        //can be edited until calculated button is clicked.
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) kualiDocumentFormBase.getDocument();
        
        baseForm.setCalculated(false);
        purapDocument.setCalculated(false);
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Does initialization for a new requisition.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((RequisitionDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }

    public ActionForward setAsDefaultBuilding(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequisitionDocument req = (RequisitionDocument) ((RequisitionForm) form).getDocument();
        
        if (ObjectUtils.isNotNull(req.getDeliveryCampusCode()) && ObjectUtils.isNotNull(req.getDeliveryBuildingCode())) {
            DefaultPrincipalAddress defaultPrincipalAddress = new DefaultPrincipalAddress(GlobalVariables.getUserSession().getPerson().getPrincipalId());
            Map addressKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(defaultPrincipalAddress);
            defaultPrincipalAddress = (DefaultPrincipalAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DefaultPrincipalAddress.class, addressKeys);

            if (ObjectUtils.isNull(defaultPrincipalAddress)) {
                defaultPrincipalAddress = new DefaultPrincipalAddress(GlobalVariables.getUserSession().getPerson().getPrincipalId());
            }
            
            defaultPrincipalAddress.setDefaultBuilding(req.getDeliveryCampusCode(), req.getDeliveryBuildingCode(), req.getDeliveryBuildingRoomNumber());
            SpringContext.getBean(BusinessObjectService.class).save(defaultPrincipalAddress);
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.DEFAULT_BUILDING_SAVED);
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.refresh(mapping, form, request, response);
        RequisitionForm rqForm = (RequisitionForm) form;
        RequisitionDocument document = (RequisitionDocument) rqForm.getDocument();

        // super.refresh() must occur before this line to get the correct APO limit
        document.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(document.getVendorContractGeneratedIdentifier(), document.getChartOfAccountsCode(), document.getOrganizationCode()));
        return forward;
    }
    
    /**
     * Adds a PurchasingItemCapitalAsset (a container for the Capital Asset Number) to the selected 
     * item's list.
     * 
     * @param mapping       An ActionMapping
     * @param form          The Form
     * @param request       An HttpServletRequest
     * @param response      The HttpServletResponse
     * @return      An ActionForward
     * @throws Exception
     */
    public ActionForward addAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequisitionForm rqForm = (RequisitionForm) form;
        RequisitionDocument document = (RequisitionDocument) rqForm.getDocument();
        RequisitionItem item = (RequisitionItem)document.getItemByLineNumber(getSelectedLine(request) + 1);
        //TODO: Add a new way to add assets to the system.
        //item.addAsset();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward displayB2BRequisition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequisitionForm reqForm = (RequisitionForm) form;
        reqForm.setDocId((String) request.getSession().getAttribute("docId"));
        loadDocument(reqForm);
        String multipleB2BReqs = (String) request.getSession().getAttribute("multipleB2BRequisitions");
        if (StringUtils.isNotEmpty(multipleB2BReqs)) {
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.B2B_MULTIPLE_REQUISITIONS);
        }
        request.getSession().removeAttribute("docId");
        request.getSession().removeAttribute("multipleB2BRequisitions");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the vendor selection from the Requisition.  NOTE, this functionality is only available on Requisition and not PO.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request A HttpServletRequest
     * @param response A HttpServletResponse
     * @return An ActionForward
     * @throws Exception
     */
    public ActionForward clearVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        RequisitionDocument document = (RequisitionDocument) baseForm.getDocument();

        document.setVendorHeaderGeneratedIdentifier(null);
        document.setVendorDetailAssignedIdentifier(null);
        document.setVendorDetail(null);
        document.setVendorName("");
        document.setVendorLine1Address("");
        document.setVendorLine2Address("");
        document.setVendorAddressInternationalProvinceName("");
        document.setVendorCityName("");
        document.setVendorStateCode("");
        document.setVendorPostalCode("");
        document.setVendorCountryCode("");
        document.setVendorContractGeneratedIdentifier(null);
        document.setVendorContract(null);
        document.setVendorFaxNumber("");
        document.setVendorCustomerNumber("");
        document.setVendorAttentionName("");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Set up blanket approve indicator which will be used to decide if need to run accounting line validation at the time of
     * blanket approve.
     * 
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingActionBase#blanketApprove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequisitionDocument document = (RequisitionDocument) ((PurchasingFormBase) form).getDocument();
        document.setBlanketApproveRequest(true);
        return super.blanketApprove(mapping, form, request, response);
    }
    
    /**
     * Add a new item to the document.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    @Override
    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        PurApItem item = purchasingForm.getNewPurchasingItemLine();
        RequisitionItem requisitionItem = (RequisitionItem)item;
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        
        if (StringUtils.isBlank(requisitionItem.getPurchasingCommodityCode())) {
            boolean commCodeParam = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(RequisitionDocument.class, PurapParameterConstants.ENABLE_DEFAULT_VENDOR_COMMODITY_CODE_IND);

            if (commCodeParam) {
                if (purchasingForm instanceof RequisitionForm) {
                    RequisitionDocument reqs =(RequisitionDocument)purchasingForm.getDocument();
                    VendorDetail dtl = reqs.getVendorDetail();
                    if (ObjectUtils.isNotNull(dtl)) {
                        List<VendorCommodityCode> vcc = dtl.getVendorCommodities();
                        String defaultCommodityCode = "";
                        Iterator<VendorCommodityCode> it = vcc.iterator();
                        while (it.hasNext()) {
                            VendorCommodityCode commodity = it.next();
                            if (commodity.isCommodityDefaultIndicator()) {
                                defaultCommodityCode = commodity.getPurchasingCommodityCode();
                                requisitionItem.setPurchasingCommodityCode(defaultCommodityCode);
                            }
                        }
                    }
                }
            }
        }
        
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            item = purchasingForm.getAndResetNewPurchasingItemLine();
            purDocument.addItem(item);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (shouldWarnIfNoAccountingLines(form)) {
            String question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (StringUtils.equals(question, PurapConstants.REQUISITION_ACCOUNTING_LINES_QUESTION)) {
                // We're getting an answer from our question
                String answer = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
                // if the answer is "yes"- continue routing, but if it isn't...
                if (!StringUtils.equals(answer, ConfirmationQuestion.YES)) {
                    // answer is "no, don't continue." so we'll just add a warning and refresh the page 
                    LOG.info("add a warning and refresh the page ");
                    GlobalVariables.getMessageMap().putWarning(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapConstants.REQ_NO_ACCOUNTING_LINES);
                    return refresh(mapping, form, request, response);
                }
            }
            else {
                /*We have an empty item and we have a content reviewer. We will now ask the user
                 * if he wants to ignore the empty item (and let the content reviewer take care of it later).
                 */
                return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.REQUISITION_ACCOUNTING_LINES_QUESTION, PurapConstants.QUESTION_REQUISITON_ROUTE_WITHOUT_ACCOUNTING_LINES, KRADConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "1");
            }
        }
        return super.route(mapping, form, request, response);
    }
    
    protected boolean shouldWarnIfNoAccountingLines(ActionForm form){
        RequisitionDocument doc = (RequisitionDocument) ((PurchasingFormBase) form).getDocument();
        RequisitionService reqs = getRequisitionService();
        return (doc.isMissingAccountingLines() && reqs.hasContentReviewer(doc.getOrganizationCode(), doc.getChartOfAccountsCode()));
    }


    protected synchronized RequisitionService getRequisitionService(){
        if (this.requisitionService == null){
            this.requisitionService = SpringContext.getBean(RequisitionService.class);
        }
        return this.requisitionService;
    }

}
