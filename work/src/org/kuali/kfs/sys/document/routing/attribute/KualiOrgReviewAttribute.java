/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers, Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees, Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on behalf of the University of Arizona, and the r*smart group. Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining, using and/or copying this Original Work, you agree that you have read, understand, and will comply with the terms and conditions of the Educational Community License. You may obtain a copy of the License at: http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RouteContext;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * KualiOrgReviewAttribute should be used when using Orgs and thier inner details to do routing.
 * 
 * 
 */
public class KualiOrgReviewAttribute implements WorkflowAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiOrgReviewAttribute.class);

    public static final String FIN_COA_CD_KEY = "fin_coa_cd";

    public static final String ORG_CD_KEY = "org_cd";

    public static final String FROM_AMOUNT_KEY = "fromAmount";

    public static final String TO_AMOUNT_KEY = "toAmount";

    private static final String TOTAL_AMOUNT_KEY = "totalAmount";

    public static final String OVERRIDE_CD_KEY = "overrideCd";

    private static final String ORG_REVIEW_ATTRIBUTE = "KUALI_ORG_REVIEW_ATTRIBUTE";

    private static Map ORGS = new HashMap();

    private static final String DOCUMENT_CHART_ORG_VALUES_KEY = "organizations";

    private String finCoaCd;

    private String orgCd;

    private String toAmount;

    private String fromAmount;

    private String overrideCd;

    private String totalDollarAmount;

    private boolean required;

    private List ruleRows;

    private List routingDataRows;

    /**
     * TODO clean the rest of the field defs up once chart is working this code up no arg constructor, which will initialize the
     * fields and rows of the attribute
     */
    public KualiOrgReviewAttribute() {
        List fields = new ArrayList();

        ruleRows = new ArrayList();
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Org.class, Constants.ORGANIZATION_CODE_PROPERTY_NAME, ORG_CD_KEY));
        ruleRows.add(KualiWorkflowUtils.buildTextRow(SourceAccountingLine.class, "overrideCode", OVERRIDE_CD_KEY));
        
        fields = new ArrayList();
        fields.add(new Field("From Amount", "", Field.TEXT, true, FROM_AMOUNT_KEY, "", null, null, FROM_AMOUNT_KEY));
        ruleRows.add(new Row(fields));
        fields = new ArrayList();
        fields.add(new Field("To Amount", "", Field.TEXT, true, TO_AMOUNT_KEY, "", null, null, TO_AMOUNT_KEY));
        ruleRows.add(new Row(fields));

        routingDataRows = new ArrayList();
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Org.class, Constants.ORGANIZATION_CODE_PROPERTY_NAME, ORG_CD_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRow(SourceAccountingLine.class, "overrideCode", OVERRIDE_CD_KEY));

        // TODO: hook TotalDollarAmount into the DD attribute for DocumentHeader.financialDocumentTotalAmount once
        // the DD has this attribute defined, like Chart and Org above
        fields = new ArrayList();
        fields.add(new Field("Total Amount", "", Field.TEXT, true, TOTAL_AMOUNT_KEY, "", null, null, TOTAL_AMOUNT_KEY));
        routingDataRows.add(new Row(fields));
    }

    /**
     * This method produces a chart row.
     * @return
     * @deprecated Use KualiWorkflowUtils.buildTextRow or buildTextRowWithLookup instead
     */
    public edu.iu.uis.eden.lookupable.Row getChartRow() {
        org.kuali.core.web.uidraw.Field kualiChartField = FieldUtils.getPropertyField(Chart.class, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, false);
        List chartFields = new ArrayList();
        chartFields.add(new Field(kualiChartField.getFieldLabel(), KualiWorkflowUtils.getHelpUrl(kualiChartField), Field.TEXT, true, FIN_COA_CD_KEY, kualiChartField.getPropertyValue(), kualiChartField.getFieldValidValues(), WorkflowLookupableImpl.getLookupableImplName(Chart.class), FIN_COA_CD_KEY));
        chartFields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, WorkflowLookupableImpl.getLookupableName(WorkflowLookupableImpl.getLookupableImplName(Chart.class), new StringBuffer(WorkflowLookupableImpl.LOOKUPABLE_IMPL_NAME_PREFIX).append(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).append(":").append(FIN_COA_CD_KEY).toString())));
        return new Row(chartFields);
    }
    
    /**
     * This method produces an org row.
     * @return
     * @deprecated Use KualiWorkflowUtils.buildTextRow or buildTextRowWithLookup instead
     */
    public edu.iu.uis.eden.lookupable.Row getOrgRow() {
        org.kuali.core.web.uidraw.Field kualiOrgField = FieldUtils.getPropertyField(Org.class, Constants.ORGANIZATION_CODE_PROPERTY_NAME, false);
        List orgFields = new ArrayList();
        orgFields.add(new Field(kualiOrgField.getFieldLabel(), KualiWorkflowUtils.getHelpUrl(kualiOrgField), Field.TEXT, true, ORG_CD_KEY, kualiOrgField.getPropertyValue(), kualiOrgField.getFieldValidValues(), WorkflowLookupableImpl.getLookupableImplName(Org.class), ORG_CD_KEY));
        orgFields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, WorkflowLookupableImpl.getLookupableName(WorkflowLookupableImpl.getLookupableImplName(Org.class), new StringBuffer(WorkflowLookupableImpl.LOOKUPABLE_IMPL_NAME_PREFIX).append(Constants.ORGANIZATION_CODE_PROPERTY_NAME).append(":").append(ORG_CD_KEY).toString())));
        return new Row(orgFields);
    }
    
    /**
     * This method produces an overrideCode row.
     * @return
     * @deprecated Use KualiWorkflowUtils.buildTextRow or buildTextRowWithLookup instead
     */
    public edu.iu.uis.eden.lookupable.Row getOverrideCodeRow() {
        org.kuali.core.web.uidraw.Field kualiOverrideCodeField;
        kualiOverrideCodeField = FieldUtils.getPropertyField(SourceAccountingLine.class, "overrideCode", false);
        List orgFields = new ArrayList();
        orgFields.add(new Field(kualiOverrideCodeField.getFieldLabel(), KualiWorkflowUtils.getHelpUrl(kualiOverrideCodeField), Field.TEXT, true, OVERRIDE_CD_KEY, kualiOverrideCodeField.getPropertyValue(), kualiOverrideCodeField.getFieldValidValues(), null, OVERRIDE_CD_KEY));
        return new Row(orgFields);
    }
    
    /**
     * constructor that takes the chart, org, which calls the no arg constructor
     * 
     * @param finCoaCd
     * @param orgCd
     */
    public KualiOrgReviewAttribute(String finCoaCd, String orgCd) {
        this();
        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", finCoaCd);
        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", orgCd);
    }

    public List getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, this.finCoaCd));
        extensions.add(new RuleExtensionValue(ORG_CD_KEY, this.orgCd));
        if (!StringUtils.isBlank(this.fromAmount)) {
            extensions.add(new RuleExtensionValue(FROM_AMOUNT_KEY, this.fromAmount));
        }
        if (!StringUtils.isBlank(this.toAmount)) {
            extensions.add(new RuleExtensionValue(TO_AMOUNT_KEY, this.toAmount));
        }
        if (!StringUtils.isBlank(this.overrideCd)) {
            extensions.add(new RuleExtensionValue(OVERRIDE_CD_KEY, this.overrideCd));
        }
        return extensions;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        if (isRequired()) {
            this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
            this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", (String) paramMap.get(ORG_CD_KEY));
            this.fromAmount = (String) paramMap.get(FROM_AMOUNT_KEY);
            this.toAmount = (String) paramMap.get(TO_AMOUNT_KEY);
            this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", (String) paramMap.get(OVERRIDE_CD_KEY));
            validateOrg(errors);
            if (StringUtils.isNotBlank(toAmount) && !StringUtils.isNumeric(toAmount)) {
                errors.add(new WorkflowServiceErrorImpl("To Amount is invalid.", "routetemplate.dollarrangeattribute.toamount.invalid"));
            }
            if (StringUtils.isNotBlank(fromAmount) && !StringUtils.isNumeric(fromAmount)) {
                errors.add(new WorkflowServiceErrorImpl("From Amount is invalid.", "routetemplate.dollarrangeattribute.fromamount.invalid"));
            }
        }
        return errors;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", (String) paramMap.get(ORG_CD_KEY));
        this.totalDollarAmount = (String) paramMap.get(TOTAL_AMOUNT_KEY);
        this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", (String) paramMap.get(OVERRIDE_CD_KEY));
        if (isRequired()) {
            validateOrg(errors);
            if (!StringUtils.isNumeric(this.totalDollarAmount)) {
                errors.add(new WorkflowServiceErrorImpl("Total Amount is invalid.", ""));
            }
        }
        return errors;
    }

    private void validateOrg(List errors) {
        if (StringUtils.isBlank(this.finCoaCd) || StringUtils.isBlank(this.orgCd)) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is required.", "routetemplate.chartorgattribute.chartorg.required"));
        }
        else {
            Org org = SpringServiceLocator.getOrganizationService().getByPrimaryIdWithCaching(finCoaCd, orgCd);
            if (org == null) {
                errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
            }
        }
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getOrgCd())) {
            return "";
        }
        return new StringBuffer("<report><chart>").append(getFinCoaCd()).append("</chart><org>").append(getOrgCd()).append("</org><totalDollarAmount>").append(getTotalDollarAmount()).append("</totalDollarAmount><overrideCode>").append(getOverrideCd()).append("</overrideCode></report>").toString();
    }

    public String getAttributeLabel() {
        return "";
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(java.lang.String, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions));
        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", getRuleExtentionValue(ORG_CD_KEY, ruleExtensions));
        this.fromAmount = getRuleExtentionValue(FROM_AMOUNT_KEY, ruleExtensions);
        this.toAmount = getRuleExtentionValue(TO_AMOUNT_KEY, ruleExtensions);
        this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", getRuleExtentionValue(OVERRIDE_CD_KEY, ruleExtensions));
        DocumentType documentType = docContent.getRouteContext().getDocument().getDocumentType();
        Set chartOrgValues = populateFromDocContent(documentType, docContent, docContent.getRouteContext());
        boolean matchesOrg = false;
        for (Iterator iter = chartOrgValues.iterator(); iter.hasNext();) {
            Org org = (Org) iter.next();
            if (org.getChartOfAccountsCode().equals(this.getFinCoaCd()) && org.getOrganizationCode().equals(this.getOrgCd())) {
                matchesOrg = true;
                break;
            }
        }

        if (!matchesOrg) {
            return false;
        }

        Float documentAmount = getAmount(documentType, docContent);
        if (documentAmount != null) {
            Float ruleFromAmount = null;
            Float ruleToAmount = null;
            if (!StringUtils.isBlank(fromAmount)) {
                ruleFromAmount = new Float(fromAmount);
                if (ruleFromAmount.floatValue() > documentAmount.floatValue()) {
                    return false;
                }
            }
            if (!StringUtils.isBlank(toAmount)) {
                ruleToAmount = new Float(toAmount);
                if (ruleToAmount.floatValue() < documentAmount.floatValue()) {
                    return false;
                }
            }
        }

        if (this.overrideCd != null) {
            String docOverrideCd = getOverrideCd(documentType, docContent);
            if (!docOverrideCd.equals(this.overrideCd)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is a recursive method that will retrive reports to orgs to build up the hierarchy of organizations
     * 
     * @param chartOrgSet
     * @param chartOrg
     */
    private void buildOrgReviewHierarchy(int counter, Set chartOrgSet, Org startOrg) {
        LOG.info("buildOrgReviewHierarchy iteration: " + counter);
        //  this will cause NPEs, so we dont let it through
        if (startOrg == null) { 
            throw new IllegalArgumentException("Parameter value for startOrg passed in was null.");
        }
        
        //  we're done if the reportsToOrg is the same as the Org, ie we're at the top of the Org hiearchy
        if (startOrg.getChartOfAccountsCode().equalsIgnoreCase(startOrg.getReportsToChartOfAccountsCode())) {
            if (startOrg.getOrganizationCode().equalsIgnoreCase(startOrg.getReportsToOrganizationCode())) {
                return;
            }
        }
        Org reportsToOrg = SpringServiceLocator.getOrganizationService().getByPrimaryIdWithCaching(startOrg.getReportsToChartOfAccountsCode(), startOrg.getReportsToOrganizationCode());
        if (reportsToOrg == null) {
            throw new RuntimeException("Org " + startOrg.getChartOfAccountsCode() + "-" + startOrg.getOrganizationCode() + 
                    " has a reportsToOrganization (" + startOrg.getReportsToChartOfAccountsCode() + "-" + startOrg.getReportsToOrganizationCode() + ") " + 
                    " that does not exist in the system.");
        }
        chartOrgSet.add(reportsToOrg);
        buildOrgReviewHierarchy(++counter, chartOrgSet, reportsToOrg);
    }

    private String getRuleExtentionValue(String key, List ruleExtensions) {
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(this.getClass().getName())) {
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    if (value.getKey().equals(key)) {
                        return value.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * this method will take the document content, and populate a list of OrgReviewAttribute objects that also contain the rollup in
     * terms of organizational hierarchy as well.
     * 
     * @param docContent
     * @return a list of OrgReviewAttribute objects that are contained in the doc, or roll up to able by one that is contained in
     *         the document
     */
    private Set populateFromDocContent(DocumentType docType, DocumentContent docContent, RouteContext routeContext) {
        Set chartOrgValues = null;
        if (routeContext.getParameters().containsKey(DOCUMENT_CHART_ORG_VALUES_KEY)) {
            chartOrgValues = (Set) routeContext.getParameters().get(DOCUMENT_CHART_ORG_VALUES_KEY);
        }
        else {
            chartOrgValues = new HashSet();
            NodeList nodes = null;
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            try {
                String chart = null;
                String org = null;
                boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("chart").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("org").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.ACCOUNT_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.FIS_USER_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.PROJECT_CODE_DOC_TYPE.equals(docType.getName())) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(Constants.ORGANIZATION_CODE_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.ORGANIZATION_DOC_TYPE.equals(docType.getName())) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append("finCoaCd").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append("orgCd").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.SUB_ACCOUNT_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.ACCOUNT_DEL_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.SUB_OBJECT_DOC_TYPE.equals(docType.getName())) {
                    // these documents don't have the organization code on them so it must be looked up
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    String accountNumber = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(Constants.ACCOUNT_NUMBER_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    Account account = SpringServiceLocator.getAccountService().getByPrimaryIdWithCaching(chart, accountNumber);
                    org = account.getOrganizationCode();
                }
                if (!StringUtils.isEmpty(chart) && !StringUtils.isEmpty(org)) {
                    Org docOrg = SpringServiceLocator.getOrganizationService().getByPrimaryIdWithCaching(chart, org);
                    if (docOrg == null) {
                        throw new RuntimeException("Org declared on the document cannot be found in the system, routing cannot continue.");
                    }
                    //  possibly duplicate add, but this is safe in a HashSet
                    chartOrgValues.add(docOrg);
                    buildOrgReviewHierarchy(0, chartOrgValues, docOrg);
                }
                else {
                    String xpathExp = null;
                    if (KualiWorkflowUtils.isMaintenanceDocument(docType)) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("kualiUser").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else {
                        if (KualiWorkflowUtils.isSourceLineOnly(docType.getName())) {
                            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/account").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        }
                        else if (KualiWorkflowUtils.isTargetLineOnly(docType.getName())) {
                            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/account").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        }
                        else {
                            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/account").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).append(" | ").append(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/account").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        }
                    }
                    nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node accountingLineNode = nodes.item(i);
                        // TODO: xstreamsafe should be handling this, but is not, therefore this code block
                        String referenceString = xpath.evaluate("@reference", accountingLineNode);
                        if (!StringUtils.isEmpty(referenceString)) {
                            accountingLineNode = (Node) xpath.evaluate(referenceString, accountingLineNode, XPathConstants.NODE);
                        }
                        String finCoaCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, accountingLineNode);
                        String orgCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + Constants.ORGANIZATION_CODE_PROPERTY_NAME, accountingLineNode);
                        if (!StringUtils.isEmpty(finCoaCd) && !StringUtils.isEmpty(orgCd)) {
                            Org organization = SpringServiceLocator.getOrganizationService().getByPrimaryIdWithCaching(finCoaCd, orgCd);
                            chartOrgValues.add(organization);
                            buildOrgReviewHierarchy(0, chartOrgValues, organization);
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            routeContext.getParameters().put(DOCUMENT_CHART_ORG_VALUES_KEY, chartOrgValues);
        }
        return chartOrgValues;
    }

    private String getOverrideCd(DocumentType docType, DocumentContent docContent) {
        try {
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                return xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report/overrideCode").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
            }
            String xpathExp = null;
            do {
                if (KualiWorkflowUtils.isMaintenanceDocument(docType)) {
                    return null;
                }
                else if (KualiWorkflowUtils.isSourceLineOnly(docType.getName())) {
                    xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/overrideCode").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    break;
                }
                else if (KualiWorkflowUtils.isTargetLineOnly(docType.getName())) {
                    xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/overrideCode").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    break;
                }
                else if (docType.getName().equals("KualiFinancialDocument")) {
                    xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/overrideCode").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).append(" | ").append(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/overrideCode").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    break;
                }
                else {
                    docType = docType.getParentDocType();
                }

            } while (docType != null);

            return xpath.evaluate(xpathExp, docContent.getDocument());

        }
        catch (Exception e) {
            LOG.error("Caught excpeption getting document override code", e);
            throw new RuntimeException(e);
        }

    }


    private Float getAmount(DocumentType docType, DocumentContent docContent) {
        try {
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                String floatVal = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report/totalDollarAmount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                if (StringUtils.isNumeric(floatVal) && StringUtils.isNotEmpty(floatVal)) {
                    return new Float(floatVal);
                }
                else {
                    return new Float(0);
                }
            }
            String xpathExp = null;
            if (KualiWorkflowUtils.isMaintenanceDocument(docType)) {
                return null;
            }
            else if (KualiWorkflowUtils.isSourceLineOnly(docType.getName())) {
                xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/amount/value").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            }
            else if (KualiWorkflowUtils.isTargetLineOnly(docType.getName())) {
                xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/amount/value").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            }
            else {
                xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/amount/value").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).append(" | ").append(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/amount/value").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            }
            String value = xpath.evaluate(new StringBuffer("sum(").append(xpathExp).append(")").toString(), docContent.getDocument());
            if (value == null) {
                throw new RuntimeException("Didn't find amount for document " + docContent.getRouteContext().getDocument().getRouteHeaderId());
            }
            return new Float(value);
        }
        catch (Exception e) {
            LOG.error("Caught excpeption getting document amount", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * simple getter for the rule rows
     */
    public List getRuleRows() {
        return ruleRows;
    }

    /**
     * simple getter for the routing data rows
     */
    public List getRoutingDataRows() {
        return routingDataRows;
    }

    /**
     * simple getter for fincoacd
     * 
     * @return String
     */
    public String getFinCoaCd() {
        return this.finCoaCd;
    }

    /**
     * simple setter for fincoacd
     * 
     * @param finCoaCd
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * simple getter for org code
     * 
     * @return String
     */
    public String getOrgCd() {
        return this.orgCd;
    }

    /**
     * simple setter for org code
     * 
     * @param orgCd
     */
    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

    public String getOverrideCd() {
        return overrideCd;
    }

    public void setOverrideCd(String overrideCd) {
        this.overrideCd = overrideCd;
    }

    /**
     * simple getter for required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * simple setter for required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

}