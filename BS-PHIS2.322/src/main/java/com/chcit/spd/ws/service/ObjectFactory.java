package com.chcit.spd.ws.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.chcit.spd.ws.service package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _PUTUserResponseReturn_QNAME = new QName(
			"http://service.ws.spd.chcit.com", "return");
	private final static QName _PUTContractUserName_QNAME = new QName(
			"http://service.ws.spd.chcit.com", "UserName");
	private final static QName _PUTContractDataXML_QNAME = new QName(
			"http://service.ws.spd.chcit.com", "dataXML");
	private final static QName _PUTContractPassword_QNAME = new QName(
			"http://service.ws.spd.chcit.com", "Password");
	private final static QName _SPDServiceExceptionSPDServiceException_QNAME = new QName(
			"http://service.ws.spd.chcit.com", "SPDServiceException");
	private final static QName _ExceptionMessage_QNAME = new QName(
			"http://service.ws.spd.chcit.com", "Message");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.chcit.spd.ws.service
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link PUTUserResponse }
	 * 
	 */
	public PUTUserResponse createPUTUserResponse() {
		return new PUTUserResponse();
	}

	/**
	 * Create an instance of {@link PUTContract }
	 * 
	 */
	public PUTContract createPUTContract() {
		return new PUTContract();
	}

	/**
	 * Create an instance of {@link PUTPOPlan }
	 * 
	 */
	public PUTPOPlan createPUTPOPlan() {
		return new PUTPOPlan();
	}

	/**
	 * Create an instance of {@link PUTProductResponse }
	 * 
	 */
	public PUTProductResponse createPUTProductResponse() {
		return new PUTProductResponse();
	}

	/**
	 * Create an instance of {@link PUTWard }
	 * 
	 */
	public PUTWard createPUTWard() {
		return new PUTWard();
	}

	/**
	 * Create an instance of {@link SPDServiceException }
	 * 
	 */
	public SPDServiceException createSPDServiceException() {
		return new SPDServiceException();
	}

	/**
	 * Create an instance of {@link PUTPrescriptionResponse }
	 * 
	 */
	public PUTPrescriptionResponse createPUTPrescriptionResponse() {
		return new PUTPrescriptionResponse();
	}

	/**
	 * Create an instance of {@link PUTUser }
	 * 
	 */
	public PUTUser createPUTUser() {
		return new PUTUser();
	}

	/**
	 * Create an instance of {@link PUTWardResponse }
	 * 
	 */
	public PUTWardResponse createPUTWardResponse() {
		return new PUTWardResponse();
	}

	/**
	 * Create an instance of {@link PUTPreReturnUnOut }
	 * 
	 */
	public PUTPreReturnUnOut createPUTPreReturnUnOut() {
		return new PUTPreReturnUnOut();
	}

	/**
	 * Create an instance of {@link PUTProduct }
	 * 
	 */
	public PUTProduct createPUTProduct() {
		return new PUTProduct();
	}

	/**
	 * Create an instance of {@link PUTBPartnerResponse }
	 * 
	 */
	public PUTBPartnerResponse createPUTBPartnerResponse() {
		return new PUTBPartnerResponse();
	}

	/**
	 * Create an instance of {@link Exception }
	 * 
	 */
	public Exception createException() {
		return new Exception();
	}

	/**
	 * Create an instance of {@link PUTContractResponse }
	 * 
	 */
	public PUTContractResponse createPUTContractResponse() {
		return new PUTContractResponse();
	}

	/**
	 * Create an instance of {@link PUTPrescription }
	 * 
	 */
	public PUTPrescription createPUTPrescription() {
		return new PUTPrescription();
	}

	/**
	 * Create an instance of {@link PUTPOOrderResponse }
	 * 
	 */
	public PUTPOOrderResponse createPUTPOOrderResponse() {
		return new PUTPOOrderResponse();
	}

	/**
	 * Create an instance of {@link PUTPreReturnUnOutResponse }
	 * 
	 */
	public PUTPreReturnUnOutResponse createPUTPreReturnUnOutResponse() {
		return new PUTPreReturnUnOutResponse();
	}

	/**
	 * Create an instance of {@link PUTPOOrder }
	 * 
	 */
	public PUTPOOrder createPUTPOOrder() {
		return new PUTPOOrder();
	}

	/**
	 * Create an instance of {@link PUTBPartner }
	 * 
	 */
	public PUTBPartner createPUTBPartner() {
		return new PUTBPartner();
	}

	/**
	 * Create an instance of {@link PUTPOPlanResponse }
	 * 
	 */
	public PUTPOPlanResponse createPUTPOPlanResponse() {
		return new PUTPOPlanResponse();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTUserResponse.class)
	public JAXBElement<String> createPUTUserResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTUserResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTContract.class)
	public JAXBElement<String> createPUTContractUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTContract.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTContract.class)
	public JAXBElement<String> createPUTContractDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTContract.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTContract.class)
	public JAXBElement<String> createPUTContractPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTContract.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTPOPlan.class)
	public JAXBElement<String> createPUTPOPlanUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTPOPlan.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTPOPlan.class)
	public JAXBElement<String> createPUTPOPlanDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTPOPlan.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTPOPlan.class)
	public JAXBElement<String> createPUTPOPlanPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTPOPlan.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTProductResponse.class)
	public JAXBElement<String> createPUTProductResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTProductResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTWard.class)
	public JAXBElement<String> createPUTWardUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTWard.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTWard.class)
	public JAXBElement<String> createPUTWardDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTWard.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTWard.class)
	public JAXBElement<String> createPUTWardPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTWard.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Exception }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "SPDServiceException", scope = SPDServiceException.class)
	public JAXBElement<Exception> createSPDServiceExceptionSPDServiceException(
			Exception value) {
		return new JAXBElement<Exception>(
				_SPDServiceExceptionSPDServiceException_QNAME, Exception.class,
				SPDServiceException.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTPrescriptionResponse.class)
	public JAXBElement<String> createPUTPrescriptionResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTPrescriptionResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTUser.class)
	public JAXBElement<String> createPUTUserUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTUser.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTUser.class)
	public JAXBElement<String> createPUTUserDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTUser.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTUser.class)
	public JAXBElement<String> createPUTUserPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTUser.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTWardResponse.class)
	public JAXBElement<String> createPUTWardResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTWardResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTPreReturnUnOut.class)
	public JAXBElement<String> createPUTPreReturnUnOutUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTPreReturnUnOut.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTPreReturnUnOut.class)
	public JAXBElement<String> createPUTPreReturnUnOutDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTPreReturnUnOut.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTPreReturnUnOut.class)
	public JAXBElement<String> createPUTPreReturnUnOutPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTPreReturnUnOut.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTProduct.class)
	public JAXBElement<String> createPUTProductUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTProduct.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTProduct.class)
	public JAXBElement<String> createPUTProductDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTProduct.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTProduct.class)
	public JAXBElement<String> createPUTProductPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTProduct.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Message", scope = Exception.class)
	public JAXBElement<String> createExceptionMessage(String value) {
		return new JAXBElement<String>(_ExceptionMessage_QNAME, String.class,
				Exception.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTBPartnerResponse.class)
	public JAXBElement<String> createPUTBPartnerResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTBPartnerResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTContractResponse.class)
	public JAXBElement<String> createPUTContractResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTContractResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTPrescription.class)
	public JAXBElement<String> createPUTPrescriptionUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTPrescription.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTPrescription.class)
	public JAXBElement<String> createPUTPrescriptionDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTPrescription.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTPrescription.class)
	public JAXBElement<String> createPUTPrescriptionPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTPrescription.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTPOOrderResponse.class)
	public JAXBElement<String> createPUTPOOrderResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTPOOrderResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTPreReturnUnOutResponse.class)
	public JAXBElement<String> createPUTPreReturnUnOutResponseReturn(
			String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTPreReturnUnOutResponse.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTPOOrder.class)
	public JAXBElement<String> createPUTPOOrderUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTPOOrder.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTPOOrder.class)
	public JAXBElement<String> createPUTPOOrderDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTPOOrder.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTPOOrder.class)
	public JAXBElement<String> createPUTPOOrderPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTPOOrder.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "UserName", scope = PUTBPartner.class)
	public JAXBElement<String> createPUTBPartnerUserName(String value) {
		return new JAXBElement<String>(_PUTContractUserName_QNAME,
				String.class, PUTBPartner.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "dataXML", scope = PUTBPartner.class)
	public JAXBElement<String> createPUTBPartnerDataXML(String value) {
		return new JAXBElement<String>(_PUTContractDataXML_QNAME, String.class,
				PUTBPartner.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "Password", scope = PUTBPartner.class)
	public JAXBElement<String> createPUTBPartnerPassword(String value) {
		return new JAXBElement<String>(_PUTContractPassword_QNAME,
				String.class, PUTBPartner.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://service.ws.spd.chcit.com", name = "return", scope = PUTPOPlanResponse.class)
	public JAXBElement<String> createPUTPOPlanResponseReturn(String value) {
		return new JAXBElement<String>(_PUTUserResponseReturn_QNAME,
				String.class, PUTPOPlanResponse.class, value);
	}

}
