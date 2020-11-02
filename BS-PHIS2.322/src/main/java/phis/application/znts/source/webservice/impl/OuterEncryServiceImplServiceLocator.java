/**
 * OuterEncryServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package phis.application.znts.source.webservice.impl;

public class OuterEncryServiceImplServiceLocator extends org.apache.axis.client.Service implements phis.application.znts.source.webservice.impl.OuterEncryServiceImplService {

    public OuterEncryServiceImplServiceLocator() {
    }


    public OuterEncryServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public OuterEncryServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for OuterEncryServiceImplPort
    private java.lang.String OuterEncryServiceImplPort_address = "http://11.42.30.12:7001/ehrEncry/ehrencry/service/outerEncry";

    public java.lang.String getOuterEncryServiceImplPortAddress() {
        return OuterEncryServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String OuterEncryServiceImplPortWSDDServiceName = "OuterEncryServiceImplPort";

    public java.lang.String getOuterEncryServiceImplPortWSDDServiceName() {
        return OuterEncryServiceImplPortWSDDServiceName;
    }

    public void setOuterEncryServiceImplPortWSDDServiceName(java.lang.String name) {
        OuterEncryServiceImplPortWSDDServiceName = name;
    }

    public phis.application.znts.source.webservice.EncryWebService getOuterEncryServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(OuterEncryServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getOuterEncryServiceImplPort(endpoint);
    }

    public phis.application.znts.source.webservice.EncryWebService getOuterEncryServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            phis.application.znts.source.webservice.impl.OuterEncryServiceImplServiceSoapBindingStub _stub = new phis.application.znts.source.webservice.impl.OuterEncryServiceImplServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getOuterEncryServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setOuterEncryServiceImplPortEndpointAddress(java.lang.String address) {
        OuterEncryServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (phis.application.znts.source.webservice.EncryWebService.class.isAssignableFrom(serviceEndpointInterface)) {
                phis.application.znts.source.webservice.impl.OuterEncryServiceImplServiceSoapBindingStub _stub = new phis.application.znts.source.webservice.impl.OuterEncryServiceImplServiceSoapBindingStub(new java.net.URL(OuterEncryServiceImplPort_address), this);
                _stub.setPortName(getOuterEncryServiceImplPortWSDDServiceName());
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
        if ("OuterEncryServiceImplPort".equals(inputPortName)) {
            return getOuterEncryServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://impl.webservice.encry.ehrview.wondersgroup.com/", "OuterEncryServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://impl.webservice.encry.ehrview.wondersgroup.com/", "OuterEncryServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("OuterEncryServiceImplPort".equals(portName)) {
            setOuterEncryServiceImplPortEndpointAddress(address);
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
