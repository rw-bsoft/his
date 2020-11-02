/**
 * _Proxy7ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package Tcmservices.Tcm03;

public class _Proxy7ServiceLocator extends org.apache.axis.client.Service implements Tcmservices.Tcm03._Proxy7Service {

    public _Proxy7ServiceLocator() {
    }


    public _Proxy7ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public _Proxy7ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TCM_HIS_03
    private java.lang.String TCM_HIS_03_address = "";

    public java.lang.String getTCM_HIS_03Address() {
        return TCM_HIS_03_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TCM_HIS_03WSDDServiceName = "TCM_HIS_03";

    public java.lang.String getTCM_HIS_03WSDDServiceName() {
        return TCM_HIS_03WSDDServiceName;
    }

    public void setTCM_HIS_03WSDDServiceName(java.lang.String name) {
        TCM_HIS_03WSDDServiceName = name;
    }

    public Tcmservices.Tcm03._Proxy7 getTCM_HIS_03() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TCM_HIS_03_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTCM_HIS_03(endpoint);
    }

    public Tcmservices.Tcm03._Proxy7 getTCM_HIS_03(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            Tcmservices.Tcm03.TCM_HIS_03SoapBindingStub _stub = new Tcmservices.Tcm03.TCM_HIS_03SoapBindingStub(portAddress, this);
            _stub.setPortName(getTCM_HIS_03WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTCM_HIS_03EndpointAddress(java.lang.String address) {
        TCM_HIS_03_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Tcmservices.Tcm03._Proxy7.class.isAssignableFrom(serviceEndpointInterface)) {
                Tcmservices.Tcm03.TCM_HIS_03SoapBindingStub _stub = new Tcmservices.Tcm03.TCM_HIS_03SoapBindingStub(new java.net.URL(TCM_HIS_03_address), this);
                _stub.setPortName(getTCM_HIS_03WSDDServiceName());
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
        if ("TCM_HIS_03".equals(inputPortName)) {
            return getTCM_HIS_03();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://components.mule.server.mirth.webreach.com", "_Proxy7Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://components.mule.server.mirth.webreach.com", "TCM_HIS_03"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TCM_HIS_03".equals(portName)) {
            setTCM_HIS_03EndpointAddress(address);
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
