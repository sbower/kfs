package org.kuali.module.purap.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.bo.CampusParameter;
import org.kuali.module.purap.bo.ContractManager;
import org.kuali.module.purap.bo.PurchaseOrderContractLanguage;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.dao.ImageDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.exceptions.PurapConfigurationException;
import org.kuali.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.module.purap.pdf.PurchaseOrderPdfParameters;
import org.kuali.module.purap.pdf.PurchaseOrderQuotePdf;
import org.kuali.module.purap.service.PrintService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrintServiceImpl implements PrintService {
    private static Log LOG = LogFactory.getLog(PrintServiceImpl.class);
    private ImageDao imageDao;
    //private PurchaseOrderVendorStipulationsService purchaseOrderVendorStipulationsService;
    //private VendorService vendorService;
    //private ReferenceService referenceService;
    //private PurchaseOrderContractLanguageDao purchaseOrderContractLanguageDao;
    //private UserService userService;
    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
   
    /**
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @param imageDao The imageDao to set
     */
    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    /**
     * @param businessObjectService The businessObjectService to set
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Create the Purchase Order Quote Requests List Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrder that holds the Quote
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream) {
    /* TODO: Uncomment this method when we find out what to do for Kuali
      LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() started");
    LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() started");

    PurchaseOrderQuoteRequestsPdf poQuoteRequestsPdf = new PurchaseOrderQuoteRequestsPdf();
    Collection pdfErrorsIn = new ArrayList();
    Collection pdfErrorsOut = new ArrayList();

    pdfErrorsIn = poQuoteRequestsPdf.generatePOQuoteRequestsListPdf(po, byteArrayOutputStream);
    
    if (!pdfErrorsIn.isEmpty()) {
      for (Iterator iter = pdfErrorsIn.iterator(); iter.hasNext();) {
        String errorMessage = (String) iter.next();
        ServiceError se = new ServiceError("pdf.error", errorMessage);
        pdfErrorsOut.add(se);
      }
    }
    LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() ended");
    return pdfErrorsOut;
*/
    return null;
  }
  
  /**
   * Create the Purchase Order Quote Requests List Pdf document and save it
   * so that it can be faxed in a later process.
   * 
   * @param po        PurchaseOrderDocument that holds the Quote
   * @return Collection of ServiceError objects
   */
  public Collection savePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po) {
      /* TODO: Uncomment this method when we find out what to do for Kuali
    LOG.debug("savePurchaseOrderQuoteRequestsListPdf() started");
    LOG.debug("savePurchaseOrderQuoteRequestsListPdf() started");

    String pdfQuotesRequestsListFilename = "IU_PO_"+po.getId().toString()+"_Quotes_Requests_List_"+System.currentTimeMillis()+".pdf";
    PurchaseOrderQuoteRequestsPdf poQuoteRequestsPdf = new PurchaseOrderQuoteRequestsPdf();
    Collection pdfErrorsIn = new ArrayList();
    Collection pdfErrorsOut = new ArrayList();

    String pdfFileLocation = applicationSettingService.getString("PDF_DIRECTORY");
    if (pdfFileLocation == null) {
      LOG.debug("savePurchaseOrderQuoteRequestsListPdf() ended");
      throw new ConfigurationException("Application Setting PDF_DIRECTORY is missing.");
    }

    try {
      pdfErrorsIn = poQuoteRequestsPdf.savePOQuoteRequestsListPdf(po, pdfFileLocation, pdfQuotesRequestsListFilename);
    } catch (PurError e) {
      ServiceError se = new ServiceError("errors","error.blank");
      se.addParameter(e.getMessage());
      pdfErrorsIn.add(se);
    } finally {
      try {
        poQuoteRequestsPdf.deletePdf(pdfFileLocation, pdfQuotesRequestsListFilename);
      } catch (Throwable e) {
        LOG.error("savePurchaseOrderQuoteRequestsListPdf() Error deleting Quote Requests PDF" + pdfQuotesRequestsListFilename + " - Exception was " + e.getMessage(), e);
        ServiceError se = new ServiceError("errors","error.blank");
        se.addParameter("Your PO Quote Requests PDF was saved successfully but an error occurred. Please report this problem to Purchasing");
        pdfErrorsIn.add(se);
      }
    }
    
    if (!pdfErrorsIn.isEmpty()) {
      for (Iterator iter = pdfErrorsIn.iterator(); iter.hasNext();) {
        String errorMessage = (String) iter.next();
        ServiceError se = new ServiceError("pdf.error", errorMessage);
        pdfErrorsOut.add(se);
      }
    }
    LOG.debug("savePurchaseOrderQuoteRequestsListPdf() ended");
    return pdfErrorsOut;
    */
      return null;
  }

  
    private PurchaseOrderPdfParameters getPurchaseOrderQuotePdfParameters(PurchaseOrderDocument po) {
        String imageTempLocation = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.IMAGE_TEMP_PATH);
        if ( imageTempLocation == null ) {
            LOG.debug("generatePurchaseOrderQuotePdf() ended");
            throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");      
        }
        // Get logo image.
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        LOG.debug("Getting logo image. key is " + key + ". campusCode is " + campusCode);
        String logoImage;
        logoImage = imageDao.getLogo(key, campusCode, imageTempLocation); 

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(PropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter)((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        criteria.clear();
        ContractManager contractManager = po.getVendorContract().getContractManager();
        criteria.put("personUserIdentifier", contractManager.getContractManagerUserIdentifier());
        UniversalUser contractManagerUser = (UniversalUser) ((List) businessObjectService.findMatching(UniversalUser.class, criteria)).get(0);
        String contractManagerCampusCode = contractManagerUser.getCampusCode();

        String pdfFileLocation = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }
        
        PurchaseOrderPdfParameters pdfParameters = new PurchaseOrderPdfParameters();
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractManagerCampusCode(contractManagerCampusCode);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        return pdfParameters;
    }
    
    /*****************************************************************
  
    /**
     * Create the Purchase Order Quote Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrderDocument that holds the Quote
     * @param povq                       PurchaseOrderVendorQuote that is being transmitted to
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, 
        ByteArrayOutputStream byteArrayOutputStream, String environment) {
        LOG.debug("generatePurchaseOrderQuotePdf() started");
        po.refreshNonUpdateableReferences();
        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderQuotePdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
            poQuotePdf.generatePOQuotePDF(po, povq, deliveryCampusName, pdfParameters.getContractManagerCampusCode(), 
                pdfParameters.getLogoImage(), byteArrayOutputStream, environment);
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes temporary images; only need to call once.
        } catch (PurError pe) {
            errors.add(pe.getMessage());
        } catch (PurapConfigurationException pce) {
            errors.add(pce.getMessage());
        } catch (Exception e) {
            errors.add(e.getMessage());
        }

        LOG.debug("generatePurchaseOrderQuotePdf() ended");
        return errors;
    }
   
    /**
     * Create the Purchase Order Quote Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @param povq      PurchaseOrderVendorQuote that is being transmitted to
     * @return Collection of ServiceError objects
     */
    public Collection savePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq,
        String environment) {
        LOG.debug("savePurchaseOrderQuotePdf() started");

        String pdfQuoteFilename = "IU_PO_"+po.getPurapDocumentIdentifier().toString()+"_Quote_"+povq.getPurchaseOrderVendorQuoteIdentifier().toString()+"_"+System.currentTimeMillis()+".pdf";
        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        PurchaseOrderPdfParameters pdfParameters = null;
        try {
            pdfParameters = getPurchaseOrderQuotePdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
            poQuotePdf.savePOQuotePDF(po, povq, pdfParameters.getPdfFileLocation(), pdfQuoteFilename, deliveryCampusName, 
                pdfParameters.getContractManagerCampusCode(), pdfParameters.getLogoImage(), environment);
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes temporary images; only need to call once.
        } catch (PurError e) {
            errors.add(e.getMessage());
        } catch (PurapConfigurationException pce) {
            errors.add(pce.getMessage());
        } finally {
            try {
                poQuotePdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfQuoteFilename);
            } catch (Throwable e) {
                LOG.error("savePurchaseOrderQuotePdf() Error deleting Quote PDF" + pdfQuoteFilename + " - Exception was " + e.getMessage(), e);
                e.printStackTrace();
                errors.add(e.getMessage());
            }
        }
     
        LOG.debug("savePurchaseOrderQuotePdf() ended");
        return errors;
    }
   
    private PurchaseOrderPdfParameters getPurchaseOrderPdfParameters(PurchaseOrderDocument po) {
        String imageTempLocation = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.IMAGE_TEMP_PATH);
        if ( imageTempLocation == null ) {
            throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");      
        }

        // Get images
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String logoImage;
        if ( (logoImage = imageDao.getLogo(key, campusCode, imageTempLocation)) == null ) {
            throw new PurapConfigurationException("logoImage is null.");
        }
        String directorSignatureImage;
        if ((directorSignatureImage = imageDao.getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null ) {
            throw new PurapConfigurationException("directorSignatureImage is null.");
        }
     
        String contractManagerSignatureImage;
     
        if ( (contractManagerSignatureImage = imageDao.getContractManagerImage(key, po.getVendorContract().getContractManagerCode(), imageTempLocation)) == null ) {
            throw new PurapConfigurationException("contractManagerSignatureImage is null.");
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(PropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter)((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        String statusInquiryUrl = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.STATUS_INQUIRY_URL);
        if ( statusInquiryUrl == null ) {
            LOG.debug("generatePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting INVOICE_STATUS_INQUIRY_URL is missing.");
        }
         
        StringBuffer contractLanguage = new StringBuffer();
        criteria.put(PropertyConstants.ACTIVE, true);
        List<PurchaseOrderContractLanguage> contractLanguageList = (List<PurchaseOrderContractLanguage>) (businessObjectService.findMatching(PurchaseOrderContractLanguage.class, criteria));
        if (!contractLanguageList.isEmpty()) {
            int lineNumber = 1;
            for (PurchaseOrderContractLanguage row: contractLanguageList) {
                if (row.getCampusCode().equals(po.getDeliveryCampusCode())) {
                    contractLanguage.append(lineNumber+" "+ row.getPurchaseOrderContractLanguageDescription()+"\n");
                    ++lineNumber;
                }
            }
        }
        
        String pdfFileLocation = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }
        
        String pdfFileName = "IU_PO_"+po.getPurapDocumentIdentifier().toString()+"_" + System.currentTimeMillis() + ".pdf";

        PurchaseOrderPdfParameters pdfParameters = new PurchaseOrderPdfParameters();
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractLanguage(contractLanguage.toString());
        pdfParameters.setContractManagerSignatureImage(contractManagerSignatureImage);
        pdfParameters.setDirectorSignatureImage(directorSignatureImage);
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setStatusInquiryUrl(statusInquiryUrl);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        pdfParameters.setPdfFileName(pdfFileName);
        return pdfParameters;
    }

    /**
     * Create the Purchase Order Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrderDocument that holds the Quote
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, 
        boolean isRetransmit, String environment) {
        //LOG.debug("generatePurchaseOrderPdf() started");
        po.refreshNonUpdateableReferences();
        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();
        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderPdfParameters(po);
            poPdf.generatePdf(po, pdfParameters, byteArrayOutputStream, isRetransmit, environment);
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes the temporary images; only need to call once.
        } catch (PurError e) {
            errors.add(e.getMessage());
        } catch (PurapConfigurationException pce) {
            errors.add(pce.getMessage());
        }

        LOG.debug("generatePurchaseOrderPdf() ended");
        return errors;
    }
    
    /**
     * Create the Purchase Order Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @return Collection of ServiceError objects
     */
    public Collection savePurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit, String environment) {
        LOG.debug("savePurchaseOrderPdf() started");
        po.refreshNonUpdateableReferences();
        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();

        PurchaseOrderPdfParameters pdfParameters = null;
        
        //TODO: Uncomment the catch block after we know what exception/error to catch in Kuali
        try {
            pdfParameters = getPurchaseOrderPdfParameters(po);
            poPdf.savePdf(po, pdfParameters, isRetransmit, environment);
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes the temporary images; only need to call once.
        } catch (PurError e) {
            errors.add(e.getMessage());
        } catch (PurapConfigurationException pce) {
            errors.add(pce.getMessage());
        } finally {
            try {
                poPdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfParameters.getPdfFileName());
            } catch (Throwable e) {
                LOG.error("savePurchaseOrderPdf() Error deleting PDF" + pdfParameters.getPdfFileName() + " - Exception was " + e.getMessage(), e);
                e.printStackTrace();
                errors.add("Error while deleting the pdf after savePurchaseOrderPdf" + e.getMessage());
            }
        }


        LOG.debug("savePurchaseOrderPdf() ended");
        return errors;

    }
    

}
/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */