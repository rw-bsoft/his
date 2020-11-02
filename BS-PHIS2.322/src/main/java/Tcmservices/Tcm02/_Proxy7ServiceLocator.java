/**
 * _Proxy7ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package Tcmservices.Tcm02;

public class _Proxy7ServiceLocator extends org.apache.axis.client.Service implements Tcmservices.Tcm02._Proxy7Service {

    public _Proxy7ServiceLocator() {
    }


    public _Proxy7ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public _Proxy7ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TCM_HIS_02
    private java.lang.String TCM_HIS_02_address = "";
    //浦口
    //private java.lang.String TCM_HIS_02_address = "http://32.26.4.13:30007/services/TCM_HIS_02";
    //溧水
    //private java.lang.String TCM_HIS_02_address = "http://20.21.0.5:30007/services/TCM_HIS_02";

    public java.lang.String getTCM_HIS_02Address() {
        return TCM_HIS_02_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TCM_HIS_02WSDDServiceName = "TCM_HIS_02";

    public java.lang.String getTCM_HIS_02WSDDServiceName() {
        return TCM_HIS_02WSDDServiceName;
    }

    public void setTCM_HIS_02WSDDServiceName(java.lang.String name) {
        TCM_HIS_02WSDDServiceName = name;
    }

    public Tcmservices.Tcm02._Proxy7 getTCM_HIS_02() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TCM_HIS_02_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTCM_HIS_02(endpoint);
    }

    public Tcmservices.Tcm02._Proxy7 getTCM_HIS_02(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	Tcmservices.Tcm02.TCM_HIS_02SoapBindingStub _stub = new Tcmservices.Tcm02.TCM_HIS_02SoapBindingStub(portAddress, this);
            _stub.setPortName(getTCM_HIS_02WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTCM_HIS_02EndpointAddress(java.lang.String address) {
        TCM_HIS_02_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Tcmservices.Tcm02._Proxy7.class.isAssignableFrom(serviceEndpointInterface)) {
                Tcmservices.Tcm02.TCM_HIS_02SoapBindingStub _stub = new Tcmservices.Tcm02.TCM_HIS_02SoapBindingStub(new java.net.URL(TCM_HIS_02_address), this);
                _stub.setPortName(getTCM_HIS_02WSDDServiceName());
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
        if ("TCM_HIS_02".equals(inputPortName)) {
            return getTCM_HIS_02();
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
            ports.add(new javax.xml.namespace.QName("http://components.mule.server.mirth.webreach.com", "TCM_HIS_02"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TCM_HIS_02".equals(portName)) {
            setTCM_HIS_02EndpointAddress(address);
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
