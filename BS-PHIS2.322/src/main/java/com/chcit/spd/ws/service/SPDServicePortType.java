package com.chcit.spd.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "SPDServicePortType", targetNamespace = "http://service.ws.spd.chcit.com")
public interface SPDServicePortType {

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_POOrder", action = "urn:PUT_POOrder")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_POOrder", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPOOrder")
	@ResponseWrapper(localName = "PUT_POOrderResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPOOrderResponse")
	public String putPOOrder(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_User", action = "urn:PUT_User")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_User", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTUser")
	@ResponseWrapper(localName = "PUT_UserResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTUserResponse")
	public String putUser(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_BPartner", action = "urn:PUT_BPartner")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_BPartner", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTBPartner")
	@ResponseWrapper(localName = "PUT_BPartnerResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTBPartnerResponse")
	public String putBPartner(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_Ward", action = "urn:PUT_Ward")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_Ward", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTWard")
	@ResponseWrapper(localName = "PUT_WardResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTWardResponse")
	public String putWard(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_Product", action = "urn:PUT_Product")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_Product", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTProduct")
	@ResponseWrapper(localName = "PUT_ProductResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTProductResponse")
	public String putProduct(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_Contract", action = "urn:PUT_Contract")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_Contract", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTContract")
	@ResponseWrapper(localName = "PUT_ContractResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTContractResponse")
	public String putContract(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_POPlan", action = "urn:PUT_POPlan")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_POPlan", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPOPlan")
	@ResponseWrapper(localName = "PUT_POPlanResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPOPlanResponse")
	public String putPOPlan(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_PreReturnUnOut", action = "urn:PUT_PreReturnUnOut")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_PreReturnUnOut", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPreReturnUnOut")
	@ResponseWrapper(localName = "PUT_PreReturnUnOutResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPreReturnUnOutResponse")
	public String putPreReturnUnOut(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

	/**
	 * 
	 * @param dataXML
	 * @param userName
	 * @param password
	 * @return returns java.lang.String
	 * @throws SPDServiceException_Exception
	 */
	@WebMethod(operationName = "PUT_Prescription", action = "urn:PUT_Prescription")
	@WebResult(targetNamespace = "http://service.ws.spd.chcit.com")
	@RequestWrapper(localName = "PUT_Prescription", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPrescription")
	@ResponseWrapper(localName = "PUT_PrescriptionResponse", targetNamespace = "http://service.ws.spd.chcit.com", className = "com.chcit.spd.ws.service.PUTPrescriptionResponse")
	public String putPrescription(
			@WebParam(name = "dataXML", targetNamespace = "http://service.ws.spd.chcit.com") String dataXML,
			@WebParam(name = "UserName", targetNamespace = "http://service.ws.spd.chcit.com") String userName,
			@WebParam(name = "Password", targetNamespace = "http://service.ws.spd.chcit.com") String password)
			throws SPDServiceException_Exception;

}
