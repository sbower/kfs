<%--
 Copyright 2006-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
	<kul:tab tabTitle="Invoice Details" defaultOpen="true" tabErrorKey="document.invoiceDetails*,document.prorateWarning">
		<c:set var="contractsGrantsInvoiceDetailAttributes" value="${DataDictionary.ContractsGrantsInvoiceDetail.attributes}" />

		<div class="tab-container" align="center">
			<h3>Invoice Details</h3>
			<table cellpadding=0 class="datatable" summary="Invoice Details section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes['costCategory.categoryName']}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.budget}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.expenditures}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulative}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.adjustedCumExpenditures}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.adjustedBalance}" useShortLabel="false" />
				</tr>
				<c:if test="${!empty KualiForm.document.invoiceDetails}">
					<!-- If the categories are not retrieved, then its better not to display the total fields in this section. -->
					<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceDetails" id="invoiceDetail">
						<tr>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes['costCategory.categoryName']}"
									property="document.invoiceDetails[${ctr}].costCategory.categoryName" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budget}"
									property="document.invoiceDetails[${ctr}].budget" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.expenditures}"
									property="document.invoiceDetails[${ctr}].expenditures" readOnly="${readOnly}" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulative}"
									property="document.invoiceDetails[${ctr}].cumulative" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
									property="document.invoiceDetails[${ctr}].balance" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
									property="document.invoiceDetails[${ctr}].billed" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
									property="document.invoiceDetails[${ctr}].adjustedCumExpenditures" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
									property="document.invoiceDetails[${ctr}].adjustedBalance" readOnly="true" /></td>
						</tr>
					</logic:iterate>
					<tr>
						<td class="datacell"><b> <bean:message key="${ArKeyConstants.CONTRACTS_GRANTS_INVOICE_DETAILS_DIRECT_SUBTOTAL_LABEL}"/>
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budget}"
								property="document.totalDirectCostInvoiceDetail.budget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.expenditures}"
								property="document.totalDirectCostInvoiceDetail.expenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulative}"
								property="document.totalDirectCostInvoiceDetail.cumulative" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
								property="document.totalDirectCostInvoiceDetail.balance" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
								property="document.totalDirectCostInvoiceDetail.billed" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
								property="document.totalDirectCostInvoiceDetail.adjustedCumExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
								property="document.totalDirectCostInvoiceDetail.adjustedBalance" readOnly="true" /></td>
					</tr>

					<tr>
						<td class="datacell"><b> <bean:message key="${ArKeyConstants.CONTRACTS_GRANTS_INVOICE_DETAILS_INDIRECT_SUBTOTAL_LABEL}"/>
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budget}"
								property="document.totalIndirectCostInvoiceDetail.budget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.expenditures}"
								property="document.totalIndirectCostInvoiceDetail.expenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulative}"
								property="document.totalIndirectCostInvoiceDetail.cumulative" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
								property="document.totalIndirectCostInvoiceDetail.balance" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
								property="document.totalIndirectCostInvoiceDetail.billed" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
								property="document.totalIndirectCostInvoiceDetail.adjustedCumExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
								property="document.totalIndirectCostInvoiceDetail.adjustedBalance" readOnly="true" /></td>
					</tr>

					<tr>
						<td class="datacell"><b> <bean:message key="${ArKeyConstants.CONTRACTS_GRANTS_INVOICE_DETAILS_TOTAL_LABEL}"/>
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budget}"
								property="document.totalCostInvoiceDetail.budget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.expenditures}"
								property="document.totalCostInvoiceDetail.expenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulative}"
								property="document.totalCostInvoiceDetail.cumulative" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
								property="document.totalCostInvoiceDetail.balance" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
								property="document.totalCostInvoiceDetail.billed" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.balance}"
								property="document.totalCostInvoiceDetail.adjustedCumExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.billed}"
								property="document.totalCostInvoiceDetail.adjustedBalance" readOnly="true" /></td>
					</tr>


				</c:if>
			</table>
		</div>
	</kul:tab>
</c:if>