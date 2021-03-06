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

<script type="text/javascript" src="scripts/module/ar/awardObjectInfo.js"></script>

<%@ attribute name="collectionEventAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for cash control detail fields."%>
<%@ attribute name="readOnly" required="true" description="determines whether the cash control detail will be displayed readonly"%>
<%@ attribute name="addLine" required="true" description="determines whether the displayed line is the add line or not"%>
<%@ attribute name="rowHeading" required="true"%>
<%@ attribute name="propertyName" required="true" description="name of form property containing the cash control document"%>
<%@ attribute name="actionMethod" required="true" description="methodToCall value for actionImage"%>
<%@ attribute name="actionImage" required="true" description="path to image to be displayed in Action column"%>
<%@ attribute name="actionAlt" required="true" description="alt value for actionImage"%>
<%@ attribute name="cssClass" required="true"%>
<%@ attribute name="includeMultipleInvoiceLookup" description="Determines if the invoices selected multi-value lookup should be present"%>

<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>

<tr>
<c:choose>
	<c:when test="${addLine}">
		<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.collectionEventCode}"
			property="${propertyName}.collectionEventCode" readOnly="true" />
		</td>
		<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityCode}"
			property="${propertyName}.activityCode" readOnly="${readOnly}" />
			<c:if test="${not readOnly}">
			&nbsp;
			<kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CollectionActivityType" fieldConversions="activityCode:${propertyName}.activityCode" />
			</c:if>
		</td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityDate}"
			property="${propertyName}.activityDate" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityText}"
			property="${propertyName}.activityText" readOnly="${readOnly}" expandedTextArea="true" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.followup}"
			property="${propertyName}.followup" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.followupDate}"
			property="${propertyName}.followupDate" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.completed}"
			property="${propertyName}.completed" readOnly="${readOnly}" onclick="clearDate(this.name, '${propertyName}.completedDate');" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.completedDate}"
			property="${propertyName}.completedDate" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.postedDate}"
			property="${propertyName}.postedDate" readOnly="true" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.userPrincipalId}"
			property="${propertyName}.user.name" readOnly="true" /></td>
		<c:if test="${includeMultipleInvoiceLookup}">
			<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${DataDictionary.CollectionActivityDocument.attributes.selectedInvoiceDocumentNumberList}"
				property="document.selectedInvoiceDocumentNumberList" readOnly="true" />
				<kul:multipleValueLookup lookedUpCollectionName="selectedInvoiceDocumentNumberList" boClassName="org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceLookup"
					lookupParameters="document.proposalNumber:proposalNumber,document.agencyNumber:agencyNumber,document.agencyName:agencyName,document.customerNumber:customerNumber,document.customerName:customerName"/>
			</td>
		</c:if>
		<td class="${cssClass}">
		<c:if test="${not readOnly}">
			<div align="center">
				<html:image property="methodToCall.${actionMethod}" src="${actionImage}" alt="${actionAlt}" title="${actionAlt}" styleClass="tinybutton" />
			</div>
		</c:if>
		</td>
			
	</c:when>
	<c:otherwise>
		<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.collectionEventCode}"
			property="${propertyName}.collectionEventCode" readOnly="true" />
		</td>
		<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityCode}"
			property="${propertyName}.activityCode" readOnly="true" />
		</td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityDate}"
			property="${propertyName}.activityDate" readOnly="true" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityText}"
			property="${propertyName}.activityText" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.followup}"
			property="${propertyName}.followup" readOnly="true" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.followupDate}"
			property="${propertyName}.followupDate" readOnly="true" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.completed}" onclick="clearDate(this.name, '${propertyName}.completedDate');"
			property="${propertyName}.completed" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.completedDate}"
			property="${propertyName}.completedDate" readOnly="${readOnly}" /></td>
		<td align=left class="${cssClass}">
			<fmt:formatDate value="${KualiForm.document.selectedInvoiceEvents[rowHeading-1].postedDate}" pattern="${dateFormatPattern}"/>
		<td align=left class="${cssClass}"><kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.userPrincipalId}"
			property="${propertyName}.user.name" readOnly="true" /></td>
		<td class="${cssClass}">
		<c:if test="${not readOnly}">
			<div align="center">
				<html:image property="methodToCall.${actionMethod}" src="${actionImage}" alt="${actionAlt}" title="${actionAlt}" styleClass="tinybutton" />
			</div>
		</c:if>
		</td>
			
	</c:otherwise>
</c:choose>

	</tr>