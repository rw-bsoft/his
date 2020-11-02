/**
 * @(#)GetEmpiId.java Created on 2009-10-23 上午11:03:37
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;

import chis.source.Constants;
import chis.source.empi.EmpiUtil;
import chis.source.empi.PIXProxy;
import chis.source.pub.PublicService;

import com.bsoft.pix.entry.MatchedDoc;
import com.bsoft.pix.entry.Person;
import com.bsoft.pix.protocol.PIXQueryPersonRequest;
import com.bsoft.pix.protocol.PIXQueryPersonResponse;
import com.bsoft.pix.protocol.PIXSubmitPersonRequest;
import com.bsoft.pix.protocol.PIXSubmitPersonResponse;
import com.bsoft.pix.protocol.builder.PIXQueryPersonRequestBuilder;
import com.bsoft.pix.protocol.builder.PIXSubmitPersonRequestBuilder;
import com.bsoft.pix.protocol.parser.PIXQueryPersonResponseParser;
import com.bsoft.pix.protocol.parser.PIXSubmitPersonResponseParser;
import com.bsoft.pix.server.embedded.EmbeddedResponse;

import ctd.service.core.ServiceException;

/**
 * 获取个人唯一号以及附属信息。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class GetEmpiIdService extends AbstractWsService {

	private static final Log logger = LogFactory.getLog(GetEmpiIdService.class);

	// @@ pix服务调用代理。
	private PIXProxy pixProxy = null;

	private String imagePath = null;

	@WebMethod(exclude = true)
	public String getImagePath() {
		return imagePath;
	}

	@WebMethod(exclude = true)
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * 执行获取EMPIID的请求。
	 * 
	 */
	@WebMethod
	public String execute(String request) {
		Object[] result = preExecute(request);
		Document resDoc = (Document) result[1];
		if ((Integer) result[0] == 1) {
			return resDoc.asXML();
		}
		Document reqDoc = (Document) result[2];
		Element reqRoot = reqDoc.getRootElement();
		Element personEle = reqRoot.element("person");
		String personXML = personEle.asXML();
		String reqClient = reqRoot.attributeValue("reqClient");
		Element resRoot = resDoc.getRootElement();
		Element codeEle = resRoot.element("code");
		Element msgEle = resRoot.element("msg");

		// @@ 先作查询。
		EmbeddedResponse embResponse = queryPerson(personXML, reqClient);
		codeEle.setText(String.valueOf(embResponse.getCode()));
		if (embResponse.getDescription() != null) {
			msgEle.setText(embResponse.getDescription());
		}

		Person person = null;
		if (embResponse.getCode() == Constants.CODE_OK) {
			person = parseQueryResponse(embResponse, codeEle, msgEle);
			if (person == null) {
				return resDoc.asXML();
			}
		}
		// @@ 如果查询结果不是200，也不是未找到，说明是查询出错
		if (embResponse.getCode() != Constants.CODE_OK
				&& embResponse.getCode() != com.bsoft.pix.Constants.CODE_PERSON_NOT_FOUND) {
			return resDoc.asXML();
		}
		// @@ 如果查询结果是未找到，将提交的个人信息作为新记录注册。
		if (embResponse.getCode() == com.bsoft.pix.Constants.CODE_PERSON_NOT_FOUND) {
			person = registerPerson(personEle, codeEle, msgEle, personXML,
					reqClient);
			if (person == null) {
				return resDoc.asXML();
			}
		}
		Element empiIdEle = DocumentHelper.createElement("empiId");
		empiIdEle.setText(person.getEmpiId());
		resRoot.add(empiIdEle);
		// 从数据库里取出照片数据并编成base64码。
		Session session = getSessionFactory().openSession();
		try {
			if (person.getPhoto() != null && person.getPhoto().length() != 0) {
				String photoBytes = null;
				// try {
				// photoBytes = getPhoto(person.getPhoto());
				// } catch (IOException e) {
				// logger.error("Get photo with id [" + person.getPhoto()
				// + "] failed.", e);
				// }
				photoBytes = PublicService.getPhoto(person.getPhoto(), session);
				if (photoBytes != null) {
					Element photoEle = DocumentHelper.createElement("photo");
					photoEle.setText(photoBytes);
					resRoot.add(photoEle);
				}
			}
			// @@ 如果有生日信息，计算生命周期。
			if (person.getBirthday() != null) {
				Element lifeCycleEle = makeLifeCycleElement(
						person.getBirthday(), codeEle, msgEle, session);
				if (lifeCycleEle == null) {
					return resDoc.asXML();
				}
				resRoot.add(lifeCycleEle);
			}
		} catch (NumberFormatException e) {
			logger.error("Invalid photo id : " + person.getPhoto(), e);
		} catch (Exception e) {
			logger.error("Get photo with id [" + person.getPhoto()
					+ "] failed.", e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		logger.info(new StringBuffer("Send response [").append(resDoc.asXML())
				.append("] to client [").append(reqClient).append("]."));
		return resDoc.asXML();
	}

	/**
	 * @see chis.source.ws.AbstractWsService#verifyRequest(org.dom4j.Element)
	 */
	@WebMethod(exclude = true)
	protected boolean verifyRequest(Element reqRoot) throws ServiceException {
		super.verifyRequest(reqRoot);
		Element personEle = reqRoot.element("person");
		if (personEle == null) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"没有个人信息。");
		}
		Element idCardEle = personEle.element("idCard");
		if (idCardEle == null || isEmpty(idCardEle.getText())) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"身份证信息缺失。");
		}

		String idCard18 = EmpiUtil.idCard15To18(idCardEle.getText());
		if (idCard18 == null) {
			throw new ServiceException(Constants.CODE_INVALID_REQUEST,
					"非法的身份证[" + idCardEle.getText() + "]。");
		}
		idCardEle.setText(idCard18);

		if (isEmpty(personEle.elementText("personName"))) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"未指定姓名。");
		}
		return true;
	}

	/**
	 * @param photoId
	 * @return
	 * @throws IOException
	 */
	// private String getPhoto(String photoId) throws IOException {
	// String path = this.getClass().getClassLoader().getResource("")
	// .getPath();
	// System.out.println("159------" + path);
	// File file = new File(imagePath + photoId + ".jpg");
	// FileInputStream fis = new FileInputStream(file);
	// // byte [] b = new byte[1024 * 10];
	// int size = (int) file.length();
	// byte[] content = new byte[size];
	// int readed = 0;
	// while (readed < size) {
	// int nextSize = size - readed;
	//
	// int nextReaded;
	// try {
	// nextReaded = fis.read(content, readed, nextSize);
	// } finally {
	// fis.close();
	// }
	// if (nextReaded != -1) {
	// readed += nextReaded;
	// } else {
	// break;
	// }
	// }
	// return Base64.encode(content);
	// }

	/**
	 * @param birthday
	 * @param codeEle
	 * @param msgEle
	 * @param session
	 * @return
	 */
	@WebMethod(exclude = true)
	private Element makeLifeCycleElement(Date birthday, Element codeEle,
			Element msgEle, Session session) {
		String[] lifeCycle;
		try {
			lifeCycle = PublicService.getLifeCycle(birthday, null, session);
		} catch (Exception e) {
			logger.error("Decide life cycle occured error.", e);
			codeEle.setText(String.valueOf(Constants.CODE_DATABASE_ERROR));
			msgEle.setText("获取生命周期失败。");
			return null;
		}
		Element lifeCycleEle = DocumentHelper.createElement("lifeCycle");
		if (lifeCycle != null) {
			lifeCycleEle.setText(lifeCycle[1]);
			return lifeCycleEle;
		}
		lifeCycleEle.setText("unknown");
		return lifeCycleEle;
	}

	/**
	 * @param personXML
	 * @param reqClient
	 * @return
	 */
	@WebMethod(exclude = true)
	private EmbeddedResponse queryPerson(String personXML, String reqClient) {
		// @@ 构建获取个人信息的请求。
		PIXQueryPersonRequest pixRequest = new PIXQueryPersonRequest();
		pixRequest.setReqClient(reqClient);
		PIXQueryPersonRequestBuilder builder = new PIXQueryPersonRequestBuilder(
				pixRequest);
		String requestXML = builder.buildMessage().append(personXML)
				.append(builder.buildMessageTail()).toString();

		EmbeddedResponse embResponse = pixProxy.getPerson(requestXML);
		return embResponse;
	}

	/**
	 * 解析EMPI查询的回应，返回个人信息。
	 * 
	 * @param embResponse
	 * @param codeEle
	 * @param msgEle
	 * @return
	 */
	@WebMethod(exclude = true)
	private Person parseQueryResponse(EmbeddedResponse embResponse,
			Element codeEle, Element msgEle) {
		PIXQueryPersonResponse response;
		try {
			response = (PIXQueryPersonResponse) new PIXQueryPersonResponseParser()
					.parse(embResponse.getBusiData());
		} catch (Exception e) {
			logger.error("Parse pix return message failed.", e);
			codeEle.setText(String.valueOf(Constants.CODE_RESPONSE_PARSE_ERROR));
			msgEle.setText("解析empi回应消息失败！");
			return null;
		}

		List<?> docList = response.getMatchedDocList();
		if (docList.size() > 1) {
			codeEle.setText(String
					.valueOf(com.bsoft.pix.Constants.CODE_TOO_MANY_RESULTS_MATCHED));
			msgEle.setText(Constants.PIX_MSG
					.get(com.bsoft.pix.Constants.CODE_TOO_MANY_RESULTS_MATCHED));
			return null;
		}
		MatchedDoc matchedDoc = (MatchedDoc) docList.get(0);
		Person prn = matchedDoc.getPerson();
		return prn;
	}

	/**
	 * @param personEle
	 * @param codeEle
	 * @param msgEle
	 * @param personXML
	 * @param reqClient
	 * @return
	 */
	@WebMethod(exclude = true)
	private Person registerPerson(Element personEle, Element codeEle,
			Element msgEle, String personXML, String reqClient) {
		String personName = personEle.elementText("personName");
		String idCard = personEle.elementText("idCard");
		String birth = personEle.elementText("birthday");
		String sex = personEle.elementText("sexCode");
		String photo = personEle.elementText("photo");
		if (isEmpty(personName) || isEmpty(idCard) || isEmpty(birth)
				|| isEmpty(sex)) {
			msgEle.setText(msgEle.getText()
					+ ", 要注册个人信息请提供更详细的数据（姓名，性别，出生日期，身份证必需）。");
			return null;
		}
		String createUnit = personEle.elementText("createUnit");
		String createUser = personEle.elementText("createUser");
		// @@ 构建注册个人信息的请求。
		PIXSubmitPersonRequest subRequest = new PIXSubmitPersonRequest();
		subRequest.setReqClient(reqClient);
		subRequest.setCreateUnit(createUnit);
		subRequest.setCreateUser(createUser);
		PIXSubmitPersonRequestBuilder subBuilder = new PIXSubmitPersonRequestBuilder(
				subRequest);
		String subXML = subBuilder.buildMessage().append(personXML)
				.append(subBuilder.buildMessageTail()).toString();
		EmbeddedResponse subResponse = pixProxy.submitPerson(subXML);

		codeEle.setText(String.valueOf(subResponse.getCode()));
		msgEle.setText(Constants.PIX_MSG.get(subResponse.getCode()));
		if (subResponse.getCode() != Constants.CODE_OK) {
			return null;
		}
		PIXSubmitPersonResponse response;
		try {
			response = (PIXSubmitPersonResponse) new PIXSubmitPersonResponseParser()
					.parse(subResponse.getBusiData());
		} catch (Exception e) {
			logger.error("Parse pix return message failed.", e);
			codeEle.setText(String.valueOf(Constants.CODE_RESPONSE_PARSE_ERROR));
			msgEle.setText("解析empi回应消息失败！");
			return null;
		}
		String empiId = response.getEmpiId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date birthday = null;
		try {
			birthday = sdf.parse(birth);
		} catch (ParseException e) {
			logger.error("Date parse failed : " + birth, e);
			codeEle.setText(String.valueOf(Constants.CODE_DATE_PASE_ERROR));
			msgEle.setText("解析出生日期[" + birth + "]失败！");
			return null;
		}
		Person person = new Person();
		person.setPersonName(personName);
		person.setSexCode(sex);
		person.setBirthday(birthday);
		person.setIdCard(idCard);
		person.setEmpiId(empiId);
		person.setPhoto(photo);
		return person;
	}

	/**
	 * @param str
	 * @return
	 */
	@WebMethod(exclude = true)
	public String isActive(String str) {
		return "echo :" + str;
	}

	@WebMethod(exclude = true)
	public PIXProxy getPixProxy() {
		return pixProxy;
	}

	@WebMethod(exclude = true)
	public void setPixProxy(PIXProxy pixProxy) {
		this.pixProxy = pixProxy;
	}

}
