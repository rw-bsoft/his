/**
 * Bank2HosServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package phis.source.ws.LsRmyyAPIService;

public class Bank2HosServiceLocator extends org.apache.axis.client.Service implements phis.source.ws.LsRmyyAPIService.Bank2HosService {

    public Bank2HosServiceLocator() {
    }


    public Bank2HosServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Bank2HosServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Bank2HosServiceHttpPort
    private java.lang.String Bank2HosServiceHttpPort_address = "http://10.2.200.202:9005/ws_bank2Clinic/services/Bank2HosService";

    public java.lang.String getBank2HosServiceHttpPortAddress() {
        return Bank2HosServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String Bank2HosServiceHttpPortWSDDServiceName = "Bank2HosServiceHttpPort";

    public java.lang.String getBank2HosServiceHttpPortWSDDServiceName() {
        return Bank2HosServiceHttpPortWSDDServiceName;
    }

    public void setBank2HosServiceHttpPortWSDDServiceName(java.lang.String name) {
        Bank2HosServiceHttpPortWSDDServiceName = name;
    }

    public phis.source.ws.LsRmyyAPIService.Bank2HosServicePortType getBank2HosServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Bank2HosServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBank2HosServiceHttpPort(endpoint);
    }

    public phis.source.ws.LsRmyyAPIService.Bank2HosServicePortType getBank2HosServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	phis.source.ws.LsRmyyAPIService.Bank2HosServiceHttpBindingStub _stub = new phis.source.ws.LsRmyyAPIService.Bank2HosServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getBank2HosServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBank2HosServiceHttpPortEndpointAddress(java.lang.String address) {
        Bank2HosServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (phis.source.ws.LsRmyyAPIService.Bank2HosServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
            	phis.source.ws.LsRmyyAPIService.Bank2HosServiceHttpBindingStub _stub = new phis.source.ws.LsRmyyAPIService.Bank2HosServiceHttpBindingStub(new java.net.URL(Bank2HosServiceHttpPort_address), this);
                _stub.setPortName(getBank2HosServiceHttpPortWSDDServiceName());
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
        if ("Bank2HosServiceHttpPort".equals(inputPortName)) {
            return getBank2HosServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.bank2clinic", "Bank2HosService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.bank2clinic", "Bank2HosServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Bank2HosServiceHttpPort".equals(portName)) {
            setBank2HosServiceHttpPortEndpointAddress(address);
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
