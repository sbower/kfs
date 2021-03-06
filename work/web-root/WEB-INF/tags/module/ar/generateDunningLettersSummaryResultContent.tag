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

<c:set var="accountDetails" value="${DataDictionary.Account.attributes}" />
<c:set var="invoiceGeneralDetails" value="${DataDictionary.InvoiceGeneralDetail.attributes}" />
<c:set var="invoiceAttributes" value="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"/>
<div class="tab-container" align="center">
	<h3>Contracts & Grants Invoices</h3>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<thead>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAttributes.documentNumber}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${accountDetails.accountNumber}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAttributes.billingDate}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAttributes.age}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAttributes.sourceTotal}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAttributes.openAmount}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${invoiceGeneralDetails.dunningLetterTemplateSentDate}" useShortLabel="false" hideRequiredAsterisk="true" />
			</tr>
		</thead>
		<logic:iterate id="invoices" name="KualiForm" property="${requestScope.propertyName}.invoices" indexId="ctr">
			<ar:generateDunningLettersSummarySubResult invoiceAttributes="${invoiceAttributes}" propertyName="${requestScope.propertyName}.invoices[${ctr}]" />
		</logic:iterate>
	</table>
</div>
