package org.kuali.kfs.module.external.kc.webService;


/*
 * 
 */

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.kuali.kfs.module.external.kc.KcConstants;

/**
 * This class was generated by Apache CXF 2.2.9
 * Thu Feb 03 14:39:12 MST 2011
 * Generated source version: 2.2.9
 * 
 */


@WebServiceClient(name = KcConstants.EffortReporting.SOAP_SERVICE_NAME, 
                  wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/effortReportingServiceSoapService?wsdl",
                  targetNamespace = KcConstants.KC_NAMESPACE_URI) 
public class EffortReportingServiceSoapService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName EffortReportingServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.EffortReporting.SERVICE_PORT);
    static {
        URL url = null;
        try {
            url = new URL("http://test.kc.kuali.org/kc-trunk/remoting/effortReportingServiceSoapService?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://127.0.0.1:8080/kc-dev/remoting/effortReportingServiceSoapService?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public EffortReportingServiceSoapService(URL wsdlLocation) {
        super(wsdlLocation, KcConstants.EffortReporting.SERVICE);
    }

    public EffortReportingServiceSoapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EffortReportingServiceSoapService() {
        super(WSDL_LOCATION, KcConstants.EffortReporting.SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns EffortReportingService
     */
    @WebEndpoint(name = KcConstants.EffortReporting.SERVICE_PORT)
    public EffortReportingService getEffortReportingServicePort() {
        return super.getPort(EffortReportingServicePort, EffortReportingService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns EffortReportingService
     */
    @WebEndpoint(name = KcConstants.EffortReporting.SERVICE_PORT)
    public EffortReportingService getEffortReportingServicePort(WebServiceFeature... features) {
        return super.getPort(EffortReportingServicePort, EffortReportingService.class, features);
    }

}
