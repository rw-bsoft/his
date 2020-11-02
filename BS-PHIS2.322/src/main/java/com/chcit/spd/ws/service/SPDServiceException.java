package com.chcit.spd.ws.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SPDServiceException" type="{http://service.ws.spd.chcit.com}Exception" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "spdServiceException" })
@XmlRootElement(name = "SPDServiceException")
public class SPDServiceException {

	@XmlElementRef(name = "SPDServiceException", namespace = "http://service.ws.spd.chcit.com", type = JAXBElement.class)
	protected JAXBElement<Exception> spdServiceException;

	/**
	 * Gets the value of the spdServiceException property.
	 * 
	 * @return possible object is {@link JAXBElement }{@code <}{@link Exception }
	 *         {@code >}
	 * 
	 */
	public JAXBElement<Exception> getSPDServiceException() {
		return spdServiceException;
	}

	/**
	 * Sets the value of the spdServiceException property.
	 * 
	 * @param value
	 *            allowed object is {@link JAXBElement }{@code <}
	 *            {@link Exception }{@code >}
	 * 
	 */
	public void setSPDServiceException(JAXBElement<Exception> value) {
		this.spdServiceException = ((JAXBElement<Exception>) value);
	}

}
