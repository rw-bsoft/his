/**
 * LSTFWebserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package phis.source.ws.SelfHelpMachineAPIService;

public class LSTFWebserviceLocator extends org.apache.axis.client.Service implements phis.source.ws.SelfHelpMachineAPIService.LSTFWebservice {

    public LSTFWebserviceLocator() {
    }


    public LSTFWebserviceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LSTFWebserviceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LSTFWebserviceSoap
    private String LSTFWebserviceSoap_address = "http://192.168.10.123:8076/LSTFWebservice.asmx";

    public String getLSTFWebserviceSoapAddress() {
        return LSTFWebserviceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private String LSTFWebserviceSoapWSDDServiceName = "LSTFWebserviceSoap";

    public String getLSTFWebserviceSoapWSDDServiceName() {
        return LSTFWebserviceSoapWSDDServiceName;
    }

    public void setLSTFWebserviceSoapWSDDServiceName(String name) {
        LSTFWebserviceSoapWSDDServiceName = name;
    }

    public phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap getLSTFWebserviceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LSTFWebserviceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLSTFWebserviceSoap(endpoint);
    }

    public phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap getLSTFWebserviceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoapStub _stub = new phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoapStub(portAddress, this);
            _stub.setPortName(getLSTFWebserviceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLSTFWebserviceSoapEndpointAddress(String address) {
        LSTFWebserviceSoap_address = address;
    }


    // Use to get a proxy class for LSTFWebserviceSoap12
    private String LSTFWebserviceSoap12_address = "http://192.168.10.123:8076/LSTFWebservice.asmx";

    public String getLSTFWebserviceSoap12Address() {
        return LSTFWebserviceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private String LSTFWebserviceSoap12WSDDServiceName = "LSTFWebserviceSoap12";

    public String getLSTFWebserviceSoap12WSDDServiceName() {
        return LSTFWebserviceSoap12WSDDServiceName;
    }

    public void setLSTFWebserviceSoap12WSDDServiceName(String name) {
        LSTFWebserviceSoap12WSDDServiceName = name;
    }

    public phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap getLSTFWebserviceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LSTFWebserviceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLSTFWebserviceSoap12(endpoint);
    }

    public phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap getLSTFWebserviceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap12Stub _stub = new phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap12Stub(portAddress, this);
            _stub.setPortName(getLSTFWebserviceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLSTFWebserviceSoap12EndpointAddress(String address) {
        LSTFWebserviceSoap12_address = address;
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
            if (phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoapStub _stub = new phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoapStub(new java.net.URL(LSTFWebserviceSoap_address), this);
                _stub.setPortName(getLSTFWebserviceSoapWSDDServiceName());
                return _stub;
            }
            if (phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap12Stub _stub = new phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoap12Stub(new java.net.URL(LSTFWebserviceSoap12_address), this);
                _stub.setPortName(getLSTFWebserviceSoap12WSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
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
        String inputPortName = portName.getLocalPart();
        if ("LSTFWebserviceSoap".equals(inputPortName)) {
            return getLSTFWebserviceSoap();
        }
        else if ("LSTFWebserviceSoap12".equals(inputPortName)) {
            return getLSTFWebserviceSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "LSTFWebservice");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "LSTFWebserviceSoap"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "LSTFWebserviceSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("LSTFWebserviceSoap".equals(portName)) {
            setLSTFWebserviceSoapEndpointAddress(address);
        }
        else 
if ("LSTFWebserviceSoap12".equals(portName)) {
            setLSTFWebserviceSoap12EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
