/**
 * CommonService8Locator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package Tcmservices.Tcm08;

public class CommonService8Locator extends org.apache.axis.client.Service implements Tcmservices.Tcm08.CommonService8 {

    public CommonService8Locator() {
    }


    public CommonService8Locator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CommonService8Locator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TCM_HIS_08
    private java.lang.String TCM_HIS_08_address = "http://esb.sinosoft.com.cn:30088/services/TCM_HIS_08";

    public java.lang.String getTCM_HIS_08Address() {
        return TCM_HIS_08_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TCM_HIS_08WSDDServiceName = "TCM_HIS_08";

    public java.lang.String getTCM_HIS_08WSDDServiceName() {
        return TCM_HIS_08WSDDServiceName;
    }

    public void setTCM_HIS_08WSDDServiceName(java.lang.String name) {
        TCM_HIS_08WSDDServiceName = name;
    }

    public Tcmservices.Tcm08.CommonService7 getTCM_HIS_08() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TCM_HIS_08_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTCM_HIS_08(endpoint);
    }

    public Tcmservices.Tcm08.CommonService7 getTCM_HIS_08(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            Tcmservices.Tcm08.CommonService8SoapBindingStub _stub = new Tcmservices.Tcm08.CommonService8SoapBindingStub(portAddress, this);
            _stub.setPortName(getTCM_HIS_08WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTCM_HIS_08EndpointAddress(java.lang.String address) {
        TCM_HIS_08_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Tcmservices.Tcm08.CommonService7.class.isAssignableFrom(serviceEndpointInterface)) {
                Tcmservices.Tcm08.CommonService8SoapBindingStub _stub = new Tcmservices.Tcm08.CommonService8SoapBindingStub(new java.net.URL(TCM_HIS_08_address), this);
                _stub.setPortName(getTCM_HIS_08WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("TCM_HIS_08".equals(inputPortName)) {
            return getTCM_HIS_08();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://components.ecm.core.engine.transfer.com", "CommonService8");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://components.ecm.core.engine.transfer.com", "TCM_HIS_08"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TCM_HIS_08".equals(portName)) {
            setTCM_HIS_08EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
