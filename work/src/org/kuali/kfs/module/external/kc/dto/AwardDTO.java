package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.util.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AwardDTO implements Serializable {

	private static final long serialVersionUID = -7830094624716529740L;

	private Long awardId;
	private String awardNumber;
    private ProposalDTO proposal;
	private Date awardStartDate;
	private Date awardEndDate;
	private KualiDecimal awardTotalAmount;
    private String awardDocumentNumber;
    private Date awardLastUpdateDate;
    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private Date awardCreateTimestamp;
    private String proposalAwardTypeCode;
    private String awardStatusCode;
    private String sponsorCode;
    private String title;
    private String awardCommentText;
    private SponsorDTO sponsor;
    private String principalInvestigatorId;
    private String unitNumber;
    private String fundManagerId;
    private boolean additionalFormsRequired;
    private boolean autoApproveInvoice;
    private boolean stopWork;
    private String additionalFormsDescription;
    private String invoicingOption;
    private String invoicingOptionDescription;
    private String dunningCampaignId;
    private String stopWorkReason;
    private boolean excludedFromInvoicing;
    private String excludedFromInvoicingReason;
    private KualiDecimal minInvoiceAmount;
    private AwardMethodOfPaymentDTO methodOfPayment;
    private FrequencyDto invoiceBillingFrequency;

	public Long getAwardId() {
		return awardId;
	}
	public void setAwardId(Long awardId) {
		this.awardId = awardId;
	}
	public ProposalDTO getProposal() {
		return proposal;
	}
	public void setProposal(ProposalDTO proposal) {
		this.proposal = proposal;
	}
	public Date getAwardStartDate() {
		return awardStartDate;
	}
	public void setAwardStartDate(Date awardStartDate) {
		this.awardStartDate = awardStartDate;
	}
	public Date getAwardEndDate() {
		return awardEndDate;
	}
	public void setAwardEndDate(Date awardEndDate) {
		this.awardEndDate = awardEndDate;
	}
	public KualiDecimal getAwardTotalAmount() {
		return awardTotalAmount;
	}
	public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
		this.awardTotalAmount = awardTotalAmount;
	}
	public String getAwardDocumentNumber() {
		return awardDocumentNumber;
	}
	public void setAwardDocumentNumber(String awardDocumentNumber) {
		this.awardDocumentNumber = awardDocumentNumber;
	}
	public Date getAwardLastUpdateDate() {
		return awardLastUpdateDate;
	}
	public void setAwardLastUpdateDate(Date awardLastUpdateDate) {
		this.awardLastUpdateDate = awardLastUpdateDate;
	}
	public KualiDecimal getAwardDirectCostAmount() {
		return awardDirectCostAmount;
	}
	public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
		this.awardDirectCostAmount = awardDirectCostAmount;
	}
	public KualiDecimal getAwardIndirectCostAmount() {
		return awardIndirectCostAmount;
	}
	public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
		this.awardIndirectCostAmount = awardIndirectCostAmount;
	}
	public Date getAwardCreateTimestamp() {
		return awardCreateTimestamp;
	}
	public void setAwardCreateTimestamp(Date awardCreateTimestamp) {
		this.awardCreateTimestamp = awardCreateTimestamp;
	}
	public String getProposalAwardTypeCode() {
		return proposalAwardTypeCode;
	}
	public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
		this.proposalAwardTypeCode = proposalAwardTypeCode;
	}
	public String getAwardStatusCode() {
		return awardStatusCode;
	}
	public void setAwardStatusCode(String awardStatusCode) {
		this.awardStatusCode = awardStatusCode;
	}
	public String getUnitNumber() {
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	public String getSponsorCode() {
		return sponsorCode;
	}
	public void setSponsorCode(String sponsorCode) {
		this.sponsorCode = sponsorCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAwardCommentText() {
		return awardCommentText;
	}
	public void setAwardCommentText(String awardCommentText) {
		this.awardCommentText = awardCommentText;
	}
	public SponsorDTO getSponsor() {
		return sponsor;
	}
	public void setSponsor(SponsorDTO sponsor) {
		this.sponsor = sponsor;
	}
	public String getPrincipalInvestigatorId() {
		return principalInvestigatorId;
	}
	public void setPrincipalInvestigatorId(String principalInvestigatorId) {
		this.principalInvestigatorId = principalInvestigatorId;
	}
	public String getFundManagerId() {
		return fundManagerId;
	}
	public void setFundManagerId(String fundManagerId) {
		this.fundManagerId = fundManagerId;
	}
	public boolean isAdditionalFormsRequired() {
		return additionalFormsRequired;
	}
	public void setAdditionalFormsRequired(boolean additionalFormsRequired) {
		this.additionalFormsRequired = additionalFormsRequired;
	}
	public boolean isStopWork() {
		return stopWork;
	}
	public void setStopWork(boolean stopWork) {
		this.stopWork = stopWork;
	}
	public String getAdditionalFormsDescription() {
		return additionalFormsDescription;
	}
	public void setAdditionalFormsDescription(String additionalFormsDescription) {
		this.additionalFormsDescription = additionalFormsDescription;
	}
	public String getInvoicingOption() {
		return invoicingOption;
	}
	public void setInvoicingOption(String invoicingOption) {
		this.invoicingOption = invoicingOption;
	}
	public String getDunningCampaignId() {
		return dunningCampaignId;
	}
	public void setDunningCampaignId(String dunningCampaignId) {
		this.dunningCampaignId = dunningCampaignId;
	}
	public String getStopWorkReason() {
		return stopWorkReason;
	}
	public void setStopWorkReason(String stopWorkReason) {
		this.stopWorkReason = stopWorkReason;
	}
	public boolean isAutoApproveInvoice() {
		return autoApproveInvoice;
	}
	public void setAutoApproveInvoice(boolean autoApproveInvoice) {
		this.autoApproveInvoice = autoApproveInvoice;
	}
	public KualiDecimal getMinInvoiceAmount() {
		return minInvoiceAmount;
	}
	public void setMinInvoiceAmount(KualiDecimal minInvoiceAmount) {
		this.minInvoiceAmount = minInvoiceAmount;
	}
	public String getAwardNumber() {
		return awardNumber;
	}
	public void setAwardNumber(String awardNumber) {
		this.awardNumber = awardNumber;
	}
	public AwardMethodOfPaymentDTO getMethodOfPayment() {
		return methodOfPayment;
	}
	public void setMethodOfPayment(AwardMethodOfPaymentDTO methodOfPayment) {
		this.methodOfPayment = methodOfPayment;
	}
	public FrequencyDto getInvoiceBillingFrequency() {
		return invoiceBillingFrequency;
	}
	public void setInvoiceBillingFrequency(FrequencyDto invoiceBillingFrequency) {
		this.invoiceBillingFrequency = invoiceBillingFrequency;
	}
	public String getInvoicingOptionDescription() {
		return invoicingOptionDescription;
	}
	public void setInvoicingOptionDescription(String invoicingOptionDescription) {
		this.invoicingOptionDescription = invoicingOptionDescription;
	}
	public boolean isExcludedFromInvoicing() {
		return excludedFromInvoicing;
	}
	public void setExcludedFromInvoicing(boolean excludedFromInvoicing) {
		this.excludedFromInvoicing = excludedFromInvoicing;
	}
	public String getExcludedFromInvoicingReason() {
		return excludedFromInvoicingReason;
	}
	public void setExcludedFromInvoicingReason(String excludedFromInvoicingReason) {
		this.excludedFromInvoicingReason = excludedFromInvoicingReason;
	}
}
