/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.ENABLE_PER_DIEM_LOOKUP_LINKS_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.ENABLE_PRIMARY_DESTINATION_ATTRIBUTE;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TransportationMode;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Base class for travel documents
 */
public abstract class TravelFormBase extends KualiAccountingDocumentFormBase implements TravelMvcWrapperBean {
    protected static final String VENDOR_PAYMENT_BUTTON_IMAGE_NAME = "buttonsmall_vendorpayment.gif";

    private Integer travelerId;
    private Integer tempTravelerId;
    private String empPrincipalId;
    private String tempEmpPrincipalId;
    private boolean showLodging;
    private boolean showMileage;
    private boolean showPerDiem;
    private boolean showPerDiemBreakdown = false;
    private boolean calculated;
    private boolean canCalculate;
    private boolean canReturn;
    private boolean canShowImportExpenseDetails = true;
    private int perDiemPercentage;
    private boolean displayNonEmployeeForm = false;
    private boolean openPaymentInformationWindow = false;
    private boolean canOpenPaymentInformation = false;
    private String wireChargeMessage;
    private String foreignCurrencyUrl;
    private Observable observable;
    private AccountingDocumentRelationship newAccountingDocumentRelationship;
    private String defaultPerDiemMileageExpenseType;

    private Map<String, List<Document>> relatedDocuments;
    private Map<String, List<Note>> relatedDocumentNotes;

    private List<ActualExpense> newActualExpenseLines;
    private ActualExpense newActualExpenseLine;

    private List<ImportedExpense> newImportedExpenseLines;
    private ImportedExpense newImportedExpenseLine;

    private GroupTraveler newGroupTravelerLine;
    protected FormFile groupTravelerImportFile;

    protected List<AccountingDistribution> distribution;
    protected Integer accountDistributionnextSourceLineNumber;
    protected TemDistributionAccountingLine accountDistributionnewSourceLine;
    protected List<TemDistributionAccountingLine> accountDistributionsourceAccountingLines = new ArrayList<TemDistributionAccountingLine>();

    protected List<LinkField> agencyLinks;
    protected boolean shouldDisplayAgencyLinks;

    protected FormFile accountDistributionFile;

    // Indicates which result set we are using when refreshing/returning from a multi-value lookup.
    protected String lookupResultsSequenceNumber;

    // Type of result returned by the multi-value lookup. ?to be persisted in the lookup results service instead?
    protected String lookupResultsBOClassName;

    // The name of the collection looked up (by a multiple value lookup)
    protected String lookedUpCollectionName;

    protected boolean perDiemCreatable = true;

    protected TravelFormBase() {
        this.accountDistributionnextSourceLineNumber = new Integer(1);
        this.setAccountDistributionnewSourceLine(setupNewAccountDistributionAccountingLine());
        this.setNewActualExpenseLine(new ActualExpense());
        this.setNewActualExpenseLines(new ArrayList<ActualExpense>());
    }

    protected TemDistributionAccountingLine setupNewAccountDistributionAccountingLine() {
        TemDistributionAccountingLine accountingLine = new TemDistributionAccountingLine();
        accountingLine.setSequenceNumber(new Integer(0));
        return accountingLine;
    }

    @Override
    public void populate(final HttpServletRequest request) {
        super.populate(request);
        final boolean enablePrimaryDestination = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.OVERRIDE_PRIMARY_DESTINATION_IND);
        request.setAttribute(ENABLE_PRIMARY_DESTINATION_ATTRIBUTE, enablePrimaryDestination);
        final boolean enablePerDiemLookupLinks = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.DISPLAY_PER_DIEM_URL_IND);
        request.setAttribute(ENABLE_PER_DIEM_LOOKUP_LINKS_ATTRIBUTE, enablePerDiemLookupLinks);
        if (getDocument() != null && !StringUtils.isBlank(getDocument().getDocumentNumber())) {
            getNewActualExpenseLine().setDocumentNumber(getDocument().getDocumentNumber());
        }
    }

    /**
     * Display accounting line only if - Trip is encumbrance OR for non-emcumbrance trip, if there are any imported expenses
     *
     * @return
     */

    public boolean isDisplayAccountingLines(){
        TravelDocument document = getTravelDocument();
        return !document.hasOnlyPrepaidExpenses() ? true : false ;
    }

    /**
     * Retrieve the Per Diem Expense Label for the form
     *
     * @return
     */
    public String getPerDiemLabel(){
        return TemConstants.PER_DIEM_EXPENSES_LABEL;
    }

    /**
     * Retrieve the Actual Expense Label for the form
     *
     * @return
     */
    public String getExpenseLabel(){
        return TemConstants.ACTUAL_EXPENSES_LABEL;
    }

    /**
     * Retrieve the Actual Expense Tab Label for the form
     *
     * @return
     */
    public String getExpenseTabLabel(){
        return TemConstants.ACTUAL_EXPENSES_LABEL;
    }


    @Override
    public Integer getTravelerId() {
        return travelerId;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean#getTravelDocument()
     */
    @Override
    public TravelDocument getTravelDocument() {
        return (TravelDocument) getDocument();
    }

    @Override
    public void setTravelerId(Integer travelerId) {
        this.travelerId = travelerId;
    }


    @Override
    public Integer getTempTravelerId() {
        return tempTravelerId;
    }


    @Override
    public void setTempTravelerId(Integer tempTravelerId) {
        this.tempTravelerId = tempTravelerId;
    }

    /**
     * Gets the observable attribute.
     *
     * @return Returns the observable.
     */
    public Observable getObservable() {
        return SpringContext.getBean(TravelStrutsObservable.class);
    }

    /**
     * Gets the empPrincipalId attribute.
     *
     * @return Returns the empPrincipalId.
     */
    @Override
    public String getEmpPrincipalId() {
        return empPrincipalId;
    }


    /**
     * Sets the empPrincipalId attribute value.
     *
     * @param empPrincipalId The empPrincipalId to set.
     */
    @Override
    public void setEmpPrincipalId(String empPrincipalId) {
        this.empPrincipalId = empPrincipalId;
    }


    /**
     * Gets the tempEmpPrincipalId attribute.
     *
     * @return Returns the tempEmpPrincipalId.
     */
    @Override
    public String getTempEmpPrincipalId() {
        return tempEmpPrincipalId;
    }


    /**
     * Sets the tempEmpPrincipalId attribute value.
     *
     * @param tempEmpPrincipalId The tempEmpPrincipalId to set.
     */
    @Override
    public void setTempEmpPrincipalId(String tempEmpPrincipalId) {
        this.tempEmpPrincipalId = tempEmpPrincipalId;
    }


    /**
     * Add necessary boilerplate prefix and suffix to lookup a DataDictionary Header field
     *
     * @param attrName the name of the attribute you're looking for
     */
    protected String getDataDictionaryAttributeName(final String attrName) {
        return new StringBuilder("DataDictionary.").append(getDocument().getClass().getSimpleName()).append(".attributes.").append(attrName).toString();
    }

    /**
     * Retrieve the name of the document identifier field for datadictionary queries
     *
     * @return String with the field name of the document identifier
     */
    protected abstract String getDocumentIdentifierFieldName();

    /**
     * Retrieve the name of the status code field for datadictionary queries
     *
     * @return String with the field name of the status code
     */
    protected String getStatusCodeFieldName() {
        return "dummyAppDocStatus";
    }

    /**
     * This puts in some new custom header fields for TA and TR documents Travel Number - ties related documents together Travel
     * Auth Status Code - current status of this Travel Authorization
     *
     * @see org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase#populateHeaderFields(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument)
     */
    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        final TravelDocument travelDoc = (TravelDocument) getDocument();

        String status = "Not Available";
        String identifier = "Not Available";

        if (ObjectUtils.isNotNull(travelDoc.getTravelDocumentIdentifier())) {
            identifier = "" + travelDoc.getTravelDocumentIdentifier();
        }
        status = workflowDocument.getApplicationDocumentStatus();

        getDocInfo().add(new HeaderField(getDataDictionaryAttributeName(getDocumentIdentifierFieldName()), identifier));
        getDocInfo().add(new HeaderField(getDataDictionaryAttributeName(getStatusCodeFieldName()), status));
    }

    @Override
    public Map<String, String> getModesOfTransportation() {
        Map<String, String> modesOfTrans = new HashMap<String, String>();

        Collection<TransportationMode> bos = SpringContext.getBean(BusinessObjectService.class).findAll(TransportationMode.class);
        for (TransportationMode mode : bos) {
            if (mode.isActive()) {
                modesOfTrans.put(mode.getCode(), mode.getName());
            }

        }

        return modesOfTrans;
    }

    /**
     * This method parses out the options from the parameters table and sets boolean values for each one LODGING MILEAGE PER_DIEM
     *
     * @param perDiemCats
     */
    public void parsePerDiemCategories(Collection<String> perDiemCats) {
        for (String category : perDiemCats) {
            String[] pair = category.split("=");
            if (pair[0].equalsIgnoreCase(TemConstants.LODGING)) {
                this.showLodging = pair[1].equalsIgnoreCase(TemConstants.YES);
            }
            if (pair[0].equalsIgnoreCase(TemConstants.MILEAGE)) {
                this.showMileage = pair[1].equalsIgnoreCase(TemConstants.YES);
            }
            if (pair[0].equalsIgnoreCase(TemConstants.PER_DIEM)) {
                this.showPerDiem = pair[1].equalsIgnoreCase(TemConstants.YES);
            }
        }
    }

    /**
     * Gets the showLodging attribute.
     *
     * @return Returns the showLodging.
     */
    @Override
    public boolean isShowLodging() {
        return showLodging;
    }

    /**
     * Sets the showLodging attribute value.
     *
     * @param showLodging The showLodging to set.
     */
    @Override
    public void setShowLodging(boolean showLodging) {
        this.showLodging = showLodging;
    }

    /**
     * Gets the showMileage attribute.
     *
     * @return Returns the showMileage.
     */
    @Override
    public boolean isShowMileage() {
        return showMileage;
    }

    /**
     * Sets the showMileage attribute value.
     *
     * @param showMileage The showMileage to set.
     */
    @Override
    public void setShowMileage(boolean showMileage) {
        this.showMileage = showMileage;
    }

    /**
     * Gets the showPerDiem attribute.
     *
     * @return Returns the showPerDiem.
     */
    @Override
    public boolean isShowPerDiem() {
        return showPerDiem;
    }

    /**
     * Sets the showPerDiemBreakdown attribute value.
     *
     * @param showPerDiemBreakdown The showPerDiemBreakdown to set.
     */
    public void setShowPerDiemBreakdown(boolean showPerDiemBreakdown) {
        this.showPerDiemBreakdown = showPerDiemBreakdown;
    }

    /**
     * Gets the showPerDiemBreakdown attribute.
     *
     * @return Returns the showPerDiemBreakdown.
     */
    public boolean isShowPerDiemBreakdown() {
        return showPerDiemBreakdown;
    }

    /**
     * Gets the canReturn attribute value.
     *
     * @return canReturn The canReturn to set.
     */
    @Override
    public boolean canReturn() {
        return canReturn;
    }

    /**
     * Sets the canReturn attribute value.
     *
     * @param canReturn The canReturn to set.
     */
    @Override
    public void setCanReturn(final boolean canReturn) {
        this.canReturn = canReturn;
    }

    public boolean canCalculate() {
        return canCalculate;
    }

    public void setCanCalculate(boolean canCalculate) {
        this.canCalculate = canCalculate;
    }

    /**
     * Gets the canImportExpenses attribute.
     * @return Returns the canImportExpenses.
     */
    public boolean isCanShowImportExpenseDetails() {
        return getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.IMPORTED_EXPENSE_DETAIL_IND);
    }

    /**
     * Sets the canShowImportExpenseDetails attribute value.
     *
     * @param canShowImportExpenseDetails The canShowImportExpenseDetails to set.
     */
    public void setCanShowImportExpenseDetails(boolean canShowImportExpenseDetails) {
        this.canShowImportExpenseDetails = canShowImportExpenseDetails;
    }

    /**
     * Sets the showPerDiem attribute value.
     *
     * @param showPerDiem The showPerDiem to set.
     */
    @Override
    public void setShowPerDiem(boolean showPerDiem) {
        this.showPerDiem = showPerDiem;
    }

    @Override
    public boolean isShowAllPerDiemCategories() {
        return (this.showLodging || this.showMileage || this.showPerDiem);
    }

    /**
     * This method takes a string parameter from the db and converts it to an int suitable for using in our calculations
     *
     * @param perDiemPercentage
     */
    @Override
    public void setPerDiemPercentage(String perDiemPercentage) {
        this.perDiemPercentage = Integer.parseInt(perDiemPercentage);
    }

    /**
     * Gets the perDiemPercentage attribute.
     *
     * @return Returns the perDiemPercentage.
     */
    @Override
    public int getPerDiemPercentage() {
        return perDiemPercentage;
    }

    /**
     * Sets the perDiemPercentage attribute value.
     *
     * @param perDiemPercentage The perDiemPercentage to set.
     */
    @Override
    public void setPerDiemPercentage(int perDiemPercentage) {
        this.perDiemPercentage = perDiemPercentage;
    }

    @Override
    public Map<String, List<Document>> getRelatedDocuments() {
        if (relatedDocuments == null) {
            relatedDocuments = new HashMap<String, List<Document>>();
        }
        return this.relatedDocuments;
    }

    @Override
    public void setRelatedDocuments(Map<String, List<Document>> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    /**
     * Gets the relatedDocumentNotes attribute.
     *
     * @return Returns the relatedDocumentNotes.
     */
    @Override
    public Map<String, List<Note>> getRelatedDocumentNotes() {
        if (relatedDocumentNotes == null) {
            relatedDocumentNotes = new HashMap<String, List<Note>>();
            for (List<Document> documents : relatedDocuments.values()) {
                for (Document document : documents) {
                    //retrieve by object Id base on document's usage with BoNotesSupport
                    List<Note> remoteNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(
                            document.getNoteType() == NoteType.BUSINESS_OBJECT? document.getObjectId() : document.getDocumentHeader().getObjectId());
                    Collections.reverse(remoteNotes);
                    relatedDocumentNotes.put(document.getDocumentNumber(), remoteNotes);
                }
            }
        }

        return relatedDocumentNotes;
    }


    /**
     * Sets the relatedDocumentNotes attribute value.
     *
     * @param relatedDocumentNotes The relatedDocumentNotes to set.
     */
    @Override
    public void setRelatedDocumentNotes(Map<String, List<Note>> relatedDocumentNotes) {
        this.relatedDocumentNotes = relatedDocumentNotes;
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    @Override
    public boolean isCalculated() {
        return calculated;
    }

    @Override
    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    /**
     * Add extra buttons to the TEM forms
     *
     * @see org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();
        String appExternalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        if (canReturn()) {
            addExtraButton("methodToCall.returnToFiscalOfficer", appExternalImageURL + "buttonsmall_return_to_fo.gif", "Return to Fiscal Officer");
        }
        if (canCalculate()){
            addExtraButton("methodToCall.recalculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
        }

        return extraButtons;
    }

    /**
     * Create and add button to the extraButton list
     *
     * @param property
     * @param source
     * @param altText
     */
    protected void addExtraButton(String property, String source, String altText) {
        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }

    @Override
    public SourceAccountingLine getNewSourceLine() {
        super.getNewSourceLine();

        if (newSourceLine != null && newSourceLine.getChartOfAccountsCode() == null && newSourceLine.getAccountNumber() == null && newSourceLine.getSubAccountNumber() == null && newSourceLine.getProjectCode() == null) {
            newSourceLine = createNewSourceAccountingLine(getFinancialDocument());
        }

        return newSourceLine;
    }

    @Override
    protected SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        TravelDocumentBase travelDoc = (TravelDocumentBase) financialDocument;

        try {
            SourceAccountingLine newSource = (SourceAccountingLine) financialDocument.getSourceAccountingLineClass().newInstance();
            if (travelDoc.getTemProfile() != null) {
                newSource.setChartOfAccountsCode(travelDoc.getTemProfile().getDefaultChartCode());
                newSource.setAccountNumber(travelDoc.getTemProfile().getDefaultAccount());
                newSource.setSubAccountNumber(travelDoc.getTemProfile().getDefaultSubAccount());
                newSource.setSubAccountNumber(travelDoc.getTemProfile().getDefaultSubAccount());
                newSource.setProjectCode(travelDoc.getTemProfile().getDefaultProjectCode());
            }
            newSource.setFinancialObjectCode("");
            return newSource;
        }
        catch (Exception e) {
            throw new InfrastructureException("Unable to create a new travel document accounting line", e);
        }
    }

    protected Map<String, ExtraButton> createPaymentExtraButtonMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();
        result.putAll(createDVExtraButtonMap());
        return result;
    }

    protected Map<String, ExtraButton> createDVExtraButtonMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();
        // Pay DV to Vendor Button
        ExtraButton payDVToVendorButton = new ExtraButton();
        payDVToVendorButton.setExtraButtonProperty("methodToCall.payDVToVendor");
        payDVToVendorButton.setExtraButtonSource("${" + EXTERNALIZABLE_IMAGES_URL_KEY + "}"+VENDOR_PAYMENT_BUTTON_IMAGE_NAME);
        payDVToVendorButton.setExtraButtonAltText("Pay DV to Vendor");

        result.put(payDVToVendorButton.getExtraButtonProperty(), payDVToVendorButton);
        return result;
    }

    /**
     *
     * @return newReimbursementButtonMap with appropriately named buttonsmall and javascript onclick to open the reimbursement document in a new window
     */
    protected Map<String, ExtraButton> createNewReimbursementButtonMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();

        // New Reimbursement button
        ExtraButton newReimbursementButton = new ExtraButton();
        newReimbursementButton.setExtraButtonProperty("methodToCall.newReimbursement");
        newReimbursementButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_newreimbursement.png");
        newReimbursementButton.setExtraButtonAltText("New Reimbursement");

        result.put(newReimbursementButton.getExtraButtonProperty(), newReimbursementButton);
        return result;
    }

    /**
     * Gets the newActualExpenseLine attribute.
     *
     * @return Returns the newActualExpenseLine.
     */
    @Override
    public List<ActualExpense> getNewActualExpenseLines() {
        if (newActualExpenseLines == null) {
            newActualExpenseLines = new ArrayList<ActualExpense>();
            for (int i = 0; i < this.getTravelDocument().getActualExpenses().size(); i++) {
                newActualExpenseLines.add(new ActualExpense());
            }
        }

        return newActualExpenseLines;
    }

    /**
     * Sets the newActualExpenseLines attribute value.
     *
     * @param newActualExpenseLines The newActualExpenseLines to set.
     */
    @Override
    public void setNewActualExpenseLines(List<ActualExpense> newActualExpenseLines) {
        this.newActualExpenseLines = newActualExpenseLines;
    }

    /**
     * Gets the newOtherExpenseLine attribute.
     *
     * @return Returns the newOtherExpenseLine.
     */
    @Override
    public ActualExpense getNewActualExpenseLine() {
        return newActualExpenseLine;
    }

    /**
     * Sets the newOtherExpenseLine attribute value.
     *
     * @param newOtherExpenseLine The newOtherExpenseLine to set.
     */
    @Override
    public void setNewActualExpenseLine(ActualExpense newActualExpenseLine) {
        this.newActualExpenseLine = newActualExpenseLine;
    }

    /**
     * Gets the newImportedExpenseLines attribute.
     *
     * @return Returns the newImportedExpenseLines.
     */
    @Override
    public List<ImportedExpense> getNewImportedExpenseLines() {
        if (newImportedExpenseLines == null) {
            newImportedExpenseLines = new ArrayList<ImportedExpense>();
            for (int i = 0; i < this.getTravelDocument().getImportedExpenses().size(); i++) {
                newImportedExpenseLines.add(new ImportedExpense());
            }
        }
        return newImportedExpenseLines;
    }

    /**
     * Sets the newImportedExpenseLines attribute value.
     *
     * @param newImportedExpenseLines The newImportedExpenseLines to set.
     */
    @Override
    public void setNewImportedExpenseLines(List<ImportedExpense> newImportedExpenseLines) {
        this.newImportedExpenseLines = newImportedExpenseLines;
    }

    /**
     * Gets the newImportedExpenseLine attribute.
     *
     * @return Returns the newImportedExpenseLine.
     */
    @Override
    public ImportedExpense getNewImportedExpenseLine() {
        return newImportedExpenseLine;
    }

    /**
     * Sets the newImportedExpenseLine attribute value.
     *
     * @param newImportedExpenseLine The newImportedExpenseLine to set.
     */
    @Override
    public void setNewImportedExpenseLine(ImportedExpense newImportedExpenseLine) {
        this.newImportedExpenseLine = newImportedExpenseLine;
    }

    @Override
    public AccountingDocumentRelationship getNewAccountingDocumentRelationship() {
        return newAccountingDocumentRelationship;
    }

    @Override
    public void setNewAccountingDocumentRelationship(AccountingDocumentRelationship newAccountingDocumentRelationship) {
        this.newAccountingDocumentRelationship = newAccountingDocumentRelationship;
    }

    /**
     * Gets the newGroupTravelerLine attribute.
     *
     * @return Returns the newGroupTravelerLine.
     */
    public GroupTraveler getNewGroupTravelerLine() {
        return newGroupTravelerLine;
    }

    /**
     * Sets the newGroupTravelerLine attribute value.
     *
     * @param newGroupTravelerLine The newGroupTravelerLine to set.
     */
    public void setNewGroupTravelerLine(GroupTraveler newGroupTravelerLine) {
        this.newGroupTravelerLine = newGroupTravelerLine;
    }

    public FormFile getGroupTravelerImportFile() {
        return groupTravelerImportFile;
    }

    public void setGroupTravelerImportFile(FormFile groupTravelerImportFile) {
        this.groupTravelerImportFile = groupTravelerImportFile;
    }

    /**
     * Gets the accountDistributionnextSourceLineNumber attribute.
     *
     * @return Returns the accountDistributionnextSourceLineNumber.
     */
    @Override
    public Integer getAccountDistributionnextSourceLineNumber() {
        return accountDistributionnextSourceLineNumber;
    }

    /**
     * Sets the accountDistributionnextSourceLineNumber attribute value.
     *
     * @param accountDistributionnextSourceLineNumber The accountDistributionnextSourceLineNumber to set.
     */
    @Override
    public void setAccountDistributionnextSourceLineNumber(Integer accountDistributionnextSourceLineNumber) {
        this.accountDistributionnextSourceLineNumber = accountDistributionnextSourceLineNumber;
    }

    /**
     * Gets the accountDistributionnewSourceLine attribute.
     *
     * @return Returns the accountDistributionnewSourceLine.
     */
    @Override
    public TemDistributionAccountingLine getAccountDistributionnewSourceLine() {
        return accountDistributionnewSourceLine;
    }

    /**
     * Sets the accountDistributionnewSourceLine attribute value.
     *
     * @param accountDistributionnewSourceLine The accountDistributionnewSourceLine to set.
     */
    @Override
    public void setAccountDistributionnewSourceLine(TemDistributionAccountingLine accountDistributionnewSourceLine) {
        this.accountDistributionnewSourceLine = accountDistributionnewSourceLine;
    }

    /**
     * Sets the sequence number appropriately for the passed in source accounting line using the value that has been stored in the
     * nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and then handles
     * incrementing the nextSourceLineNumber variable.
     *
     * @param line the accounting line to add to the list.
     * @see org.kuali.kfs.sys.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    @Override
    public void addAccountDistributionsourceAccountingLine(TemDistributionAccountingLine line) {
        line.setSequenceNumber(this.getAccountDistributionnextSourceLineNumber());
        this.getAccountDistributionsourceAccountingLines().add(line);
        this.accountDistributionnextSourceLineNumber = new Integer(this.getAccountDistributionnextSourceLineNumber().intValue() + 1);
        this.setAccountDistributionnewSourceLine(setupNewAccountDistributionAccountingLine());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean#setDistribution(java.util.List)
     */
    @Override
    public void setDistribution(final List<AccountingDistribution> distribution) {

        // set up the selected object code/card type
        if (!getDistribution().isEmpty() && this.distribution != null && !this.distribution.isEmpty()) {

            Map<AccountingLineDistributionKey, AccountingDistribution> distributionMap = new HashMap<AccountingLineDistributionKey, AccountingDistribution>();
            for (AccountingDistribution accountDistribution : this.distribution) {
                distributionMap.put(new AccountingLineDistributionKey(accountDistribution.getObjectCode(), accountDistribution.getCardType()), accountDistribution);
            }

            AccountingLineDistributionKey key;
            for (AccountingDistribution accountDistribution : distribution) {
                key = new AccountingLineDistributionKey(accountDistribution.getObjectCode(), accountDistribution.getCardType());
                if (distributionMap.containsKey(key)) {
                    accountDistribution.setSelected(distributionMap.get(key).getSelected());
                }
            }

        }
        this.distribution = distribution;
    }

    @Override
    public List<AccountingDistribution> getDistribution() {
        if (distribution == null) {
            distribution = new ArrayList<AccountingDistribution>();
        }
        return distribution;
    }

    public Boolean getHasSelectedDistributionRemainingAmount() {
        return KualiDecimal.ZERO.isLessThan(getSelectedDistributionRemainingAmount());
    }

    public KualiDecimal getSelectedDistributionRemainingAmount() {
        return getDistributionRemainingAmount(true);
    }

    public KualiDecimal getSelectedDistributionSubTotal() {
        return getDistributionSubTotal(true);
    }

    public KualiDecimal getFullDistributionRemainingAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (AccountingDistribution accountDistribution : distribution) {
            total = total.add(accountDistribution.getRemainingAmount());
        }
        return total;
    }

    public KualiDecimal getFullDistributionSubTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (AccountingDistribution accountDistribution : distribution) {
            total = total.add(accountDistribution.getRemainingAmount());
        }
        return total;
    }

    @Override
    public KualiDecimal getDistributionRemainingAmount(boolean selectedDistributions) {
        KualiDecimal total = KualiDecimal.ZERO;
        KualiDecimal distributedTotal = KualiDecimal.ZERO;
        for (AccountingDistribution accountDistribution : distribution) {
            if ((selectedDistributions && accountDistribution.getSelected()) || !selectedDistributions) {
                total = total.add(accountDistribution.getRemainingAmount());
                distributedTotal = distributedTotal.add(accountDistribution.getSubTotal().subtract(accountDistribution.getRemainingAmount()));
            }
        }
        if (getTravelDocument().getExpenseLimit() != null && getTravelDocument().getExpenseLimit().isPositive()) {
            if (distributedTotal.isGreaterEqual(getTravelDocument().getExpenseLimit())) {
                return KualiDecimal.ZERO; // we're over our expense limit
            } else if (total.isLessEqual(getTravelDocument().getExpenseLimit())) {
                return total;
            } else {
                return getTravelDocument().getExpenseLimit().subtract(distributedTotal); // return the remaining of our expense limit
            }
        }
        return total;
    }

    @Override
    public KualiDecimal getDistributionSubTotal(boolean selectedDistributions) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (AccountingDistribution accountDistribution : distribution) {
            if ((selectedDistributions && accountDistribution.getSelected()) || !selectedDistributions) {
                total = total.add(accountDistribution.getSubTotal());
            }
        }
        return total;
    }

    /**
     * Check if document is of Travel Authorization
     *
     * @return
     */
    public boolean getIsTravelAuthorizationDoc(){
        return getTravelDocument().isTravelAuthorizationDoc();
    }

    public FormFile getAccountDistributionFile() {
        return accountDistributionFile;
    }

    public void setAccountDistributionFile(FormFile accountDistributionFile) {
        this.accountDistributionFile = accountDistributionFile;
    }

    public String getUploadParserInstructionsUrl() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY) + SpringContext.getBean(ParameterService.class).getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.UPLOAD_PARSER_INSTRUCTIONS_URL);
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }


    public boolean isDefaultOpenPaymentInfoTab() {
        boolean  initiated =  getTravelDocument().getDocumentHeader().getWorkflowDocument().isInitiated();
        boolean  saved =  getTravelDocument().getDocumentHeader().getWorkflowDocument().isSaved();
        if (initiated || saved) {
            return true;
        }
        return false;

   }

    /**
     * Gets the accountDistributionsourceAccountingLines attribute.
     *
     * @return Returns the accountDistributionsourceAccountingLines.
     */
    @Override
    public List<TemDistributionAccountingLine> getAccountDistributionsourceAccountingLines() {
        return accountDistributionsourceAccountingLines;
    }

    /**
     * Sets the accountDistributionsourceAccountingLines attribute value.
     *
     * @param accountDistributionsourceAccountingLines The accountDistributionsourceAccountingLines to set.
     */
    @Override
    public void setAccountDistributionsourceAccountingLines(List<TemDistributionAccountingLine> accountDistributionsourceAccountingLines) {
        this.accountDistributionsourceAccountingLines = accountDistributionsourceAccountingLines;
    }

    /**
     * Gets the displayNonEmployeeForm attribute.
     *
     * @return Returns the displayNonEmployeeForm.
     */
    public boolean isDisplayNonEmployeeForm() {
        return displayNonEmployeeForm;
    }

    /**
     * Sets the displayNonEmployeeForm attribute value.
     *
     * @param displayNonEmployeeForm The displayNonEmployeeForm to set.
     */
    public void setDisplayNonEmployeeForm(boolean displayNonEmployeeForm) {
        this.displayNonEmployeeForm = displayNonEmployeeForm;
    }

    /**
     * @return true if the payment information window should be opened on the next refresh; false otherwise
     */
    public boolean isOpenPaymentInformationWindow() {
        return openPaymentInformationWindow;
    }

    /**
     * Sets whether the payment information window should be opened on the next refresh
     * @param openPaymentInformationWindow set to true if the payment information should be opened, false otherwise
     */
    public void setOpenPaymentInformationWindow(boolean openPaymentInformationWindow) {
        this.openPaymentInformationWindow = openPaymentInformationWindow;
    }

    /**
     * @return Returns the wireChargeMessage.
     */
    public String getWireChargeMessage() {
        return wireChargeMessage;
    }

    /**
     * @param wireChargeMessage The wireChargeMessage to set.
     */
    public void setWireChargeMessage(String wireChargeMessage) {
        this.wireChargeMessage = wireChargeMessage;
    }

    /**
     * @return the name of the action which the current form should return to
     */
    public abstract String getTravelPaymentFormAction();

    /**
     * @return the URL where payment details post-extraction for this payment can be looked up
     */
    public String getDisbursementInfoUrl() {
        final PaymentSourceHelperService paymentSourceHelperService = SpringContext.getBean(PaymentSourceHelperService.class);
        return paymentSourceHelperService.getDisbursementInfoUrl();
    }

    public String getForeignCurrencyUrl() {
        return foreignCurrencyUrl;
    }

    public void setForeignCurrencyUrl(String foreignCurrencyUrl) {
        this.foreignCurrencyUrl = foreignCurrencyUrl;
    }

    /**
     * @return the expense type code which should be set as the default in per diem mileage lookups
     */
    public String getDefaultPerDiemMileageExpenseType() {
        if (StringUtils.isBlank(defaultPerDiemMileageExpenseType)) {
            defaultPerDiemMileageExpenseType = this.getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE, KFSConstants.EMPTY_STRING);
        }
        return defaultPerDiemMileageExpenseType;
    }

    /**
     * @return if per diem can be created for this trip.  This is not dependent on trip type so much as it is that the conditions for per diem to be created exist (for instance, an active mileage rate for the default mileage rate expense type code)
     */
    public boolean isPerDiemCreatable() {
        return perDiemCreatable;
    }

    /**
     * Sets if per diem is creatable on this trip
     * @param perDiemCreatable set to true if per diem can be created on this trip, false otherwise
     */
    public void setPerDiemCreatable(boolean perDiemCreatable) {
        this.perDiemCreatable = perDiemCreatable;
    }

    public List<LinkField> getAgencyLinks() {
        return agencyLinks;
    }

    public void setAgencyLinks(List<LinkField> agencyLinks) {
        this.agencyLinks = agencyLinks;
    }

    public boolean isShouldDisplayAgencyLinks() {
        return shouldDisplayAgencyLinks;
    }

    public void setShouldDisplayAgencyLinks(boolean shouldDisplayAgencyLinks) {
        this.shouldDisplayAgencyLinks = shouldDisplayAgencyLinks;
    }
}
