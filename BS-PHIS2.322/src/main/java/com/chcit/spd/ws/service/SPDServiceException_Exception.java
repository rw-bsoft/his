package com.chcit.spd.ws.service;

import javax.xml.ws.WebFault;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "SPDServiceException", targetNamespace = "http://service.ws.spd.chcit.com")
public class SPDServiceException_Exception extends java.lang.Exception {

	/**
	 * Java type that goes as soapenv:Fault detail element.
	 * 
	 */
	private SPDServiceException faultInfo;

	/**
	 * 
	 * @param message
	 * @param faultInfo
	 */
	public SPDServiceException_Exception(String message,
			SPDServiceException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	/**
	 * 
	 * @param message
	 * @param faultInfo
	 * @param cause
	 */
	public SPDServiceException_Exception(String message,
			SPDServiceException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	/**
	 * 
	 * @return returns fault bean: com.chcit.spd.ws.service.SPDServiceException
	 */
	public SPDServiceException getFaultInfo() {
		return faultInfo;
	}

}
