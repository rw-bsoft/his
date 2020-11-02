/**
 * LSCrbbkMiddleServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package phis.source.ws.client.crbbk;

public class LSCrbbkMiddleServiceLocator extends org.apache.axis.client.Service implements phis.source.ws.client.crbbk.LSCrbbkMiddleService {

    public LSCrbbkMiddleServiceLocator() {
    }


    public LSCrbbkMiddleServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LSCrbbkMiddleServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LSCrbbkMiddleServiceHttpSoap12Endpoint
    private java.lang.String LSCrbbkMiddleServiceHttpSoap12Endpoint_address = "http://32.33.1.80:8100/bsoftservice/services/LSCrbbkMiddleService.LSCrbbkMiddleServiceHttpSoap12Endpoint/";

    public java.lang.String getLSCrbbkMiddleServiceHttpSoap12EndpointAddress() {
        return LSCrbbkMiddleServiceHttpSoap12Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName = "LSCrbbkMiddleServiceHttpSoap12Endpoint";

    public java.lang.String getLSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName() {
        return LSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName;
    }

    public void setLSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName(java.lang.String name) {
        LSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName = name;
    }

    public phis.source.ws.client.crbbk.LSCrbbkMiddleServicePortType getLSCrbbkMiddleServiceHttpSoap12Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LSCrbbkMiddleServiceHttpSoap12Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLSCrbbkMiddleServiceHttpSoap12Endpoint(endpoint);
    }

    public phis.source.ws.client.crbbk.LSCrbbkMiddleServicePortType getLSCrbbkMiddleServiceHttpSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap12BindingStub _stub = new phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap12BindingStub(portAddress, this);
            _stub.setPortName(getLSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLSCrbbkMiddleServiceHttpSoap12EndpointEndpointAddress(java.lang.String address) {
        LSCrbbkMiddleServiceHttpSoap12Endpoint_address = address;
    }


    // Use to get a proxy class for LSCrbbkMiddleServiceHttpSoap11Endpoint
    private java.lang.String LSCrbbkMiddleServiceHttpSoap11Endpoint_address = "http://32.33.1.80:8100/bsoftservice/services/LSCrbbkMiddleService.LSCrbbkMiddleServiceHttpSoap11Endpoint/";

    public java.lang.String getLSCrbbkMiddleServiceHttpSoap11EndpointAddress() {
        return LSCrbbkMiddleServiceHttpSoap11Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName = "LSCrbbkMiddleServiceHttpSoap11Endpoint";

    public java.lang.String getLSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName() {
        return LSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName;
    }

    public void setLSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName(java.lang.String name) {
        LSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName = name;
    }

    public phis.source.ws.client.crbbk.LSCrbbkMiddleServicePortType getLSCrbbkMiddleServiceHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LSCrbbkMiddleServiceHttpSoap11Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLSCrbbkMiddleServiceHttpSoap11Endpoint(endpoint);
    }

    public phis.source.ws.client.crbbk.LSCrbbkMiddleServicePortType getLSCrbbkMiddleServiceHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap11BindingStub _stub = new phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap11BindingStub(portAddress, this);
            _stub.setPortName(getLSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLSCrbbkMiddleServiceHttpSoap11EndpointEndpointAddress(java.lang.String address) {
        LSCrbbkMiddleServiceHttpSoap11Endpoint_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (phis.source.ws.client.crbbk.LSCrbbkMiddleServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap12BindingStub _stub = new phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap12BindingStub(new java.net.URL(LSCrbbkMiddleServiceHttpSoap12Endpoint_address), this);
                _stub.setPortName(getLSCrbbkMiddleServiceHttpSoap12EndpointWSDDServiceName());
                return _stub;
            }
            if (phis.source.ws.client.crbbk.LSCrbbkMiddleServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap11BindingStub _stub = new phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap11BindingStub(new java.net.URL(LSCrbbkMiddleServiceHttpSoap11Endpoint_address), this);
                _stub.setPortName(getLSCrbbkMiddleServiceHttpSoap11EndpointWSDDServiceName());
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
        if ("LSCrbbkMiddleServiceHttpSoap12Endpoint".equals(inputPortName)) {
            return getLSCrbbkMiddleServiceHttpSoap12Endpoint();
        }
        else if ("LSCrbbkMiddleServiceHttpSoap11Endpoint".equals(inputPortName)) {
            return getLSCrbbkMiddleServiceHttpSoap11Endpoint();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://lsservice.bsoft.com", "LSCrbbkMiddleService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://lsservice.bsoft.com", "LSCrbbkMiddleServiceHttpSoap12Endpoint"));
            ports.add(new javax.xml.namespace.QName("http://lsservice.bsoft.com", "LSCrbbkMiddleServiceHttpSoap11Endpoint"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LSCrbbkMiddleServiceHttpSoap12Endpoint".equals(portName)) {
            setLSCrbbkMiddleServiceHttpSoap12EndpointEndpointAddress(address);
        }
        else 
if ("LSCrbbkMiddleServiceHttpSoap11Endpoint".equals(portName)) {
            setLSCrbbkMiddleServiceHttpSoap11EndpointEndpointAddress(address);
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
