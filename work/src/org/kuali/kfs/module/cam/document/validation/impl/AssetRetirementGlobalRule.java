/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetRetirementGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalRule.class);

    private PersistableBusinessObject bo;


    /**
     * Constructs a AssetLocationGlobalRule
     */
    public AssetRetirementGlobalRule() {
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        AssetRetirementGlobal newRetirementGlobal = (AssetRetirementGlobal) super.getNewBo();
        newRetirementGlobal.refreshNonUpdateableReferences();
        for (AssetRetirementGlobalDetail detail : newRetirementGlobal.getAssetRetirementGlobalDetails()) {
            detail.refreshNonUpdateableReferences();
        }
    }

    /**
     * Processes rules when saving this global.
     * 
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        setupConvenienceObjects();
        valid &= assetRetirementValidation(assetRetirementGlobal);

        if ((valid & super.processCustomSaveDocumentBusinessRules(document)) && !getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal) && validateAssetObjectCodeExistence(assetRetirementGlobal)) {
            // create poster
            AssetRetirementGeneralLedgerPendingEntrySource assetRetirementGlPoster = new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) document.getDocumentHeader());
            // create postables
            if (!(valid = getAssetRetirementService().createGLPostables(assetRetirementGlobal, assetRetirementGlPoster))) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_OBJECT_CODE_FROM_ASSET_OBJECT_CODE);
                return valid;
            }
            if (SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetRetirementGlPoster)) {
                assetRetirementGlobal.setGeneralLedgerPendingEntries(assetRetirementGlPoster.getPendingEntries());
            }
            else {
                assetRetirementGlPoster.getPendingEntries().clear();
            }
        }

        return valid;
    }

    /**
     * Validates object code
     * 
     * @param assetRetirementGlobal
     * @return boolean
     */
    private boolean validateAssetObjectCodeExistence(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            Asset asset = assetRetirementGlobalDetail.getAsset();
            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                AssetObjectCode assetObjectCode = getAssetRetirementService().getAssetObjectCode(asset, assetPayment);
                if (ObjectUtils.isNull(assetObjectCode)) {
                    putFieldError(CamsPropertyConstants.AssetRetirementGlobal.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_ASSET_OBJECT_CODE_NOT_FOUND, new String[] { asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment.getFinancialObject().getFinancialObjectSubTypeCode() });
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }


    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = true;
        AssetRetirementGlobalDetail assetRetirementGlobalDetail = (AssetRetirementGlobalDetail) line;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();

        if (!checkEmptyValue(assetRetirementGlobalDetail.getCapitalAssetNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_BLANK_CAPITAL_ASSET_NUMBER);
            success = false;
        }
        else {
            success &= checkRetirementDetailOneLine(assetRetirementGlobalDetail, assetRetirementGlobal);
            success &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(0));
        }

        // Calculate summary fields in order to show the values even though add new line fails.
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            getAssetService().setAssetSummaryFields(detail.getAsset());
        }
        return success & super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * Check if only single asset is allowed to retire.
     * 
     * @param retirementReasonCode
     * @param assetRetirementDetails
     * @param maxNumber
     * @return
     */
    private boolean checkRetireMultipleAssets(String retirementReasonCode, List<AssetRetirementGlobalDetail> assetRetirementDetails, Integer maxNumber) {
        boolean success = true;

        if (assetRetirementDetails.size() > maxNumber && !getAssetRetirementService().isAllowedRetireMultipleAssets(retirementReasonCode)) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
            success = false;
        }

        return success;
    }

    /**
     * This method validates each asset to be retired.
     * 
     * @param assetRetirementGlobalDetails
     * @return
     */
    private boolean validateRetirementDetails(AssetRetirementGlobal assetRetirementGlobal) {
        boolean success = true;

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        if (assetRetirementGlobalDetails.size() == 0) {
            success = false;
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_ASSET_RETIREMENT_GLOBAL_NO_ASSET);
        }
        else {
            // validate each asset retirement detail
            int index = 0;
            for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "[" + index++ + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);

                success &= checkRetirementDetailOneLine(detail, assetRetirementGlobal);

                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            }
        }
        return success;
    }


    /**
     * This method validates one asset is a valid asset and no duplicate with target asset when merge.
     * 
     * @param detail
     * @return
     */
    private boolean checkRetirementDetailOneLine(AssetRetirementGlobalDetail assetRetirementGlobalDetail, AssetRetirementGlobal assetRetirementGlobal) {
        boolean success = true;

        assetRetirementGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);

        Asset asset = assetRetirementGlobalDetail.getAsset();

        if (ObjectUtils.isNull(asset)) {
            success = false;
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_CAPITAL_ASSET_NUMBER, assetRetirementGlobalDetail.getCapitalAssetNumber().toString());
        }
        else {
            success &= validateActiveCapitalAsset(asset);
            success &= validateNonMoveableAsset(asset);

            if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
                success &= validateDuplicateAssetNumber(assetRetirementGlobal.getMergedTargetCapitalAssetNumber(), assetRetirementGlobalDetail.getCapitalAssetNumber());
            }

            // Set asset non persistent fields
            getAssetService().setAssetSummaryFields(asset);
        }

        return success;
    }

    /**
     * Check for Merge Asset, no duplicate capitalAssetNumber between "from" and "to".
     * 
     * @param targetAssetNumber
     * @param sourceAssetNumber
     * @return
     */
    private boolean validateDuplicateAssetNumber(Long targetAssetNumber, Long sourceAssetNumber) {
        boolean success = true;

        if (getAssetService().isCapitalAssetNumberDuplicate(targetAssetNumber, sourceAssetNumber)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_DUPLICATE_CAPITAL_ASSET_NUMBER_WITH_TARGET, sourceAssetNumber.toString());
            success = false;
        }

        return success;
    }

    /**
     * User must be in work group CM_SUPER_USERS to retire a non-moveable asset.
     * 
     * @return
     */
    private boolean validateNonMoveableAsset(Asset asset) {
        boolean success = true;

        Person user = GlobalVariables.getUserSession().getPerson();
        if (!getAssetService().isAssetMovable(asset) && !KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_USER_GROUP_FOR_NON_MOVEABLE_ASSET, asset.getCapitalAssetNumber().toString());
            success = false;
        }

        return success;
    }


    /**
     * Validate Asset Retirement Global and Details.
     * 
     * @param assetRetirementGlobal
     * @return
     */
    protected boolean assetRetirementValidation(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        valid &= validateRequiredSharedInfoFields(assetRetirementGlobal);
        if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
            valid &= validateMergeTargetAsset(assetRetirementGlobal);
        }
        valid &= validateRetirementDetails(assetRetirementGlobal);
        valid &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(1));

        return valid;
    }

    /**
     * Validate mergedTargetCapitalAsset. Only valid and active capital asset is allowed.
     * 
     * @param assetRetirementGlobal
     * @return
     */
    private boolean validateMergeTargetAsset(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;
        Asset targetAsset = assetRetirementGlobal.getMergedTargetCapitalAsset();
        Long targetAssetNumber = assetRetirementGlobal.getMergedTargetCapitalAssetNumber();

        if (!checkEmptyValue(targetAssetNumber)) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_BLANK_CAPITAL_ASSET_NUMBER);
            valid = false;
        }
        else if (ObjectUtils.isNull(targetAsset)) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_MERGED_TARGET_ASSET_NUMBER, targetAssetNumber.toString());
            valid = false;
        }
        else {
            // Check asset of capital and active
            if (!getAssetService().isCapitalAsset(targetAsset)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_CAPITAL_ASSET_RETIREMENT, targetAssetNumber.toString());
                valid = false;
            }
            if (getAssetService().isAssetRetired(targetAsset)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, targetAssetNumber.toString());
                valid = false;
            }
        }

        // Validating the mergeAssetDescription is not blank
        if (!checkEmptyValue(assetRetirementGlobal.getMergedTargetCapitalAssetDescription())) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetRetirementGlobal.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_DESC).getLabel();
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_DESC, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        return valid;
    }


    /**
     * Only active capital equipment can be retired using the asset retirement document.
     * 
     * @param valid
     * @param detail
     * @return
     */
    private boolean validateActiveCapitalAsset(Asset asset) {
        boolean valid = true;

        if (!getAssetService().isCapitalAsset(asset)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_CAPITAL_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
            valid = false;
        }
        else if (getAssetService().isAssetRetired(asset)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
            valid = false;
        }

        return valid;
    }


    /**
     * Validate required fields for given retirement reason code
     * 
     * @param assetRetirementGlobal
     * @return
     */
    private boolean validateRequiredSharedInfoFields(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        AssetRetirementGlobalDetail sharedRetirementInfo = assetRetirementGlobal.getSharedRetirementInfo();

        if (getAssetRetirementService().isAssetRetiredBySold(assetRetirementGlobal)) {
            if (StringUtils.isBlank(sharedRetirementInfo.getBuyerDescription())) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.BUYER_DESCRIPTION, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.BUYER_DESCRIPTION, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
            if (sharedRetirementInfo.getSalePrice() == null) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.SALE_PRICE, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.SALE_PRICE, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
            valid &= validateCashReceiptFinancialDocumentNumber(sharedRetirementInfo);
        }
        else if (getAssetRetirementService().isAssetRetiredByAuction(assetRetirementGlobal)) {
            valid = validateCashReceiptFinancialDocumentNumber(sharedRetirementInfo);
        }
        else if (getAssetRetirementService().isAssetRetiredByExternalTransferOrGift(assetRetirementGlobal) && StringUtils.isBlank(sharedRetirementInfo.getRetirementInstitutionName())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.RETIREMENT_INSTITUTION_NAME, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.RETIREMENT_INSTITUTION_NAME, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        else if (getAssetRetirementService().isAssetRetiredByTheft(assetRetirementGlobal) && StringUtils.isBlank(sharedRetirementInfo.getPaidCaseNumber())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.PAID_CASE_NUMBER, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.PAID_CASE_NUMBER, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        return valid;
    }

    /**
     * validates Cash Receipt Financial Document Number
     * 
     * @param sharedRetirementInfo
     * @return boolean
     */
    private boolean validateCashReceiptFinancialDocumentNumber(AssetRetirementGlobalDetail sharedRetirementInfo) {
        boolean valid = true;
        if (StringUtils.isNotBlank(sharedRetirementInfo.getCashReceiptFinancialDocumentNumber())) {
            Map retirementInfoMap = new HashMap();
            retirementInfoMap.put(CamsPropertyConstants.AssetGlobal.DOCUMENT_NUMBER, sharedRetirementInfo.getCashReceiptFinancialDocumentNumber());
            bo = (FinancialSystemDocumentHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(FinancialSystemDocumentHeader.class, retirementInfoMap);
            if (bo == null) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_RETIREMENT_DETAIL_INFO, new String[] { CamsConstants.RetirementLabel.CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER, sharedRetirementInfo.getCashReceiptFinancialDocumentNumber() });
                valid = false;
            }
        }
        return valid;
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    private AssetRetirementService getAssetRetirementService() {
        return SpringContext.getBean(AssetRetirementService.class);
    }
}
