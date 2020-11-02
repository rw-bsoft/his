
package Hcnservices;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "HCNWebservicesService", targetNamespace = "http://webservices/", wsdlLocation = "http://localhost:8092/Webservices/HCNWebservices?wsdl")
public class HCNWebservicesService
    extends Service
{

    private final static URL HCNWEBSERVICESSERVICE_WSDL_LOCATION;
    private final static WebServiceException HCNWEBSERVICESSERVICE_EXCEPTION;
    private final static QName HCNWEBSERVICESSERVICE_QNAME = new QName("http://webservices/", "HCNWebservicesService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8092/Webservices/HCNWebservices?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        HCNWEBSERVICESSERVICE_WSDL_LOCATION = url;
        HCNWEBSERVICESSERVICE_EXCEPTION = e;
    }

    public HCNWebservicesService() {
        super(__getWsdlLocation(), HCNWEBSERVICESSERVICE_QNAME);
    }

//    public HCNWebservicesService(WebServiceFeature... features) {
//        super(__getWsdlLocation(), HCNWEBSERVICESSERVICE_QNAME, features);
//    }

    public HCNWebservicesService(URL wsdlLocation) {
        super(wsdlLocation, HCNWEBSERVICESSERVICE_QNAME);
    }

//    public HCNWebservicesService(URL wsdlLocation, WebServiceFeature... features) {
//        super(wsdlLocation, HCNWEBSERVICESSERVICE_QNAME, features);
//    }

    public HCNWebservicesService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

//    public HCNWebservicesService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
//        super(wsdlLocation, serviceName, features);
//    }

    /**
     * 
     * @return
     *     returns HCNWebservices
     */
    @WebEndpoint(name = "HCNWebservicesPort")
    public HCNWebservices getHCNWebservicesPort() {
        return super.getPort(new QName("http://webservices/", "HCNWebservicesPort"), HCNWebservices.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns HCNWebservices
     */
    @WebEndpoint(name = "HCNWebservicesPort")
    public HCNWebservices getHCNWebservicesPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://webservices/", "HCNWebservicesPort"), HCNWebservices.class, features);
    }

    private static URL __getWsdlLocation() {
        if (HCNWEBSERVICESSERVICE_EXCEPTION!= null) {
            throw HCNWEBSERVICESSERVICE_EXCEPTION;
        }
        return HCNWEBSERVICESSERVICE_WSDL_LOCATION;
    }

}
