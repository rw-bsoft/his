/*
 * @(#)Bschis.sourceDAO.java Created on 2011-12-15 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.empi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.conf.SystemCofigManageModel;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;

import com.bsoft.pix.server.EmpiIdGenerator;

import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 基本信息相关的工具类。
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class EmpiUtil implements BSCHISEntryNames {

	/**
	 * 根据身份证号获取生日
	 * 
	 * @param pid
	 *            身份证号
	 */
	public static Date getBirthdayFromIdCard(String pid) {
		Calendar birthdayCaoendar = Calendar.getInstance();
		if (pid != null
				&& (pid.trim().length() == 15 || pid.trim().length() == 18)) {
			if (pid.trim().length() == 15) {
				String pid18 = idCard15To18(pid);
				birthdayCaoendar.set(Calendar.YEAR,
						Integer.parseInt(pid18.substring(6, 10)));
				// Month 值是基于 0 的。例如，0 表示 January
				birthdayCaoendar.set(Calendar.MONTH,
						Integer.parseInt(pid18.substring(10, 12)) - 1);
				birthdayCaoendar.set(Calendar.DAY_OF_MONTH,
						Integer.parseInt(pid18.substring(12, 14)));
			} else {
				birthdayCaoendar.set(Calendar.YEAR,
						Integer.parseInt(pid.substring(6, 10)));
				// Month 值是基于 0 的。例如，0 表示 January
				birthdayCaoendar.set(Calendar.MONTH,
						Integer.parseInt(pid.substring(10, 12)) - 1);
				birthdayCaoendar.set(Calendar.DAY_OF_MONTH,
						Integer.parseInt(pid.substring(12, 14)));
			}
			return birthdayCaoendar.getTime();
		}
		return null;
	}

	/**
	 * 根据身份证号获取性别
	 * 
	 * @param pid
	 *            身份证号
	 * @return 性别 02为女01为男
	 */
	public static String getSexByPid(String pid) {
		if (pid != null
				&& (pid.trim().length() == 15 || pid.trim().length() == 18)) {
			if (pid.trim().length() == 15) {
				String pid18 = idCard15To18(pid);
				if (Integer.parseInt(pid18.substring(16, 17)) % 2 == 0) {
					return "02";
				} else {
					return "01";
				}
			} else {
				if (Integer.parseInt(pid.substring(16, 17)) % 2 == 0) {
					return "02";
				} else {
					return "01";
				}
			}
		}
		return null;
	}

	/**
	 * 将一个合法的15位身份证转换成18位身份证号码，如果长度为18将直接返回，否则如果长度不为15将返回null，
	 * 本方法并不检查传入的身份证的有效性。
	 * 
	 * @param idCard
	 * @return
	 */
	public static String idCard15To18(String idCard) {
		if (idCard.length() == 18) {
			return idCard;
		}
		if (idCard.length() != 15) {
			return null;
		}
		StringBuffer sb = new StringBuffer(idCard.substring(0, 6)).append("19")
				.append(idCard.substring(6, 8)).append(idCard.substring(8));
		int rights[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		int sum = 0;
		for (int i = 0; i < 17; i++) {
			int c = sb.charAt(i) - 48;
			sum += c * rights[i];
		}
		int m = sum % 11;
		String checkNum[] = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3",
				"2" };
		sb.append(checkNum[m]);
		return sb.toString();
	}

	/**
	 * 身份证号码验证
	 * 
	 * @param IDStr
	 * @return
	 * @throws ParseException
	 */
	public static String IDCardValidate(String IDStr) throws ParseException {
		String errorInfo = "";// 记录错误信息
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位。";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效。";
			return errorInfo;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
				|| (gc.getTime().getTime() - s.parse(
						strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
			errorInfo = "身份证生日不在有效范围。";
			return errorInfo;
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无效";
			return errorInfo;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无效";
			return errorInfo;
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		Hashtable<String, String> h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误。";
			return errorInfo;
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				return errorInfo;
			}
		} else {
			return "";
		}
		// =====================(end)=====================
		return "";
	}

	/**
	 * 功能：设置地区编码
	 * 
	 * @return Hashtable 对象
	 */
	private static Hashtable<String, String> GetAreaCode() {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> changeToPIXFormat(Map<String, Object> body) {
		Map<String, Object> records = new HashMap<String, Object>();
		records.putAll(body);
		if (records.get("empiId") != null) {
			records.put("mpiId", records.get("empiId"));
		}
		if ("".equals(records.get("idCard"))) {
			records.remove("idCard");
		}
		List<Map<String, Object>> phones = (List<Map<String, Object>>) records
				.get("phone");
		if (phones == null) {
			phones = new ArrayList<Map<String, Object>>();
		}
		if (records.get("mobileNumber") != null
				&& ((String) records.get("mobileNumber")).trim().length() > 0) {
			Map<String, Object> phone = new HashMap<String, Object>();
			phone.put("contactTypeCode", "01");
			phone.put("contactNo", records.get("mobileNumber"));
			phones.add(phone);
		}
		if (records.get("phoneNumber") != null
				&& ((String) records.get("phoneNumber")).trim().length() > 0) {
			Map<String, Object> phone = new HashMap<String, Object>();
			phone.put("contactTypeCode", "04");
			phone.put("contactNo", records.get("mobileNumber"));
			phones.add(phone);
		}
		records.put("contactWays", phones);
		if ((records.get("address") != null && ((String) records.get("address"))
				.trim().length() > 0)
				|| (records.get("zipCode") != null && ((String) records
						.get("zipCode")).trim().length() > 0)) {
			List<Map<String, Object>> addresses = (List<Map<String, Object>>) records
					.get("addresses");
			if (addresses == null) {
				addresses = new ArrayList<Map<String, Object>>();
			}
			Map<String, Object> address = new HashMap<String, Object>();
			if (records.get("address") != null
					&& ((String) records.get("address")).trim().length() > 0) {
				address.put("address", records.get("address"));
			}
			if (records.get("zipCode") != null
					&& ((String) records.get("zipCode")).trim().length() > 0) {
				address.put("postalCode", records.get("zipCode"));
			}
			address.put("addressTypeCode", "04");
			addresses.add(address);
			records.put("addresses", addresses);
		}
		if (records.get("zipCode") != null
				&& ((String) records.get("zipCode")).trim().length() > 0) {
			records.put("postalCode", records.get("zipCode"));
		}
		String contact = (String) records.get("contact");
		if (StringUtils.isNotEmpty(contact)) {
			records.put("contactName", contact);
		}
		String contactRelation = (String) records.get("contactRelation");
		if (StringUtils.isNotEmpty(contactRelation)) {
			records.put("contactRelation", contactRelation);
		}
//		String insuranceType = (String) records.get("insuranceType");
//		if (StringUtils.isNotEmpty(insuranceType)) {
//			insuranceType = "";
//		}
		// if (records.get("contact") != null
		// && ((String) records.get("contact")).trim().length() > 0) {
		// List<Map<String, Object>> contacts = new ArrayList<Map<String,
		// Object>>();
		// Map<String, Object> contact = new HashMap<String, Object>();
		// contact.put("contactName", records.get("contact"));
		// contacts.add(contact);
		// records.put("contacts", contacts);
		// }
		removePkey(records);
		// if (records.get("empiId") != null) {
		// records = putMpiIdToRecords(records);
		// }
		if (records.containsKey("photo")) {
			records.remove("photo");
		}
		return records;
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// private static Map<String, Object> putMpiIdToRecords(
	// Map<String, Object> records) {
	// String mpiId = (String) records.get("empiId");
	// Collection<Object> c = records.values();
	// Iterator it = c.iterator();
	// while (it.hasNext()) {
	// Object o = it.next();
	// if (o instanceof List) {
	// for (int j = 0; j < ((List) o).size(); j++) {
	// Map<String, Object> record = (Map<String, Object>) ((List) o)
	// .get(j);
	// record.put("mpiId", mpiId);
	// }
	// }
	// }
	// return records;
	// }

	/**
	 * 查询证件是否被他人使用
	 * 
	 * @param idCard
	 * @param idCardType
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	public static boolean checkIdCardUsed(String idCard, String empiId,
			BaseDAO dao) throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			List<Map<String, Object>> list = empiModel
					.getEmpiInfoByIdcard(idCard);
			if (list.size() == 0) {
				return false;
			}
			Map<String, Object> empiInfo = list.get(0);
			String oEmpiId = (String) empiInfo.get("empiId");
			if (oEmpiId.equals(empiId)) {
				return false;
			}

			return true;
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void removePkey(Map<String, Object> records) {
		Collection<Object> c = records.values();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof List) {
				for (int j = 0; j < ((List) o).size(); j++) {
					Map<String, Object> record = (Map<String, Object>) ((List) o)
							.get(j);
					record.remove("addressId");
					record.remove("cardId");
					record.remove("certificateId");
					record.remove("phoneId");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void saveEmpiInfo(Map<String, Object> body,
			EmpiModel empiModel, String empiId) throws ValidateException,
			ModelDataOperationException {
		Map<String, Object> diMap = empiModel.getDemographicInfo(empiId);
		String op = "create";
		if (diMap != null && diMap.size() > 0) {
			op = "update";
		}
		empiModel.saveEmpiRecord(body, op, false);
		if (body.get("addresses") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("addresses");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> address = list.get(i);
				address.put("empiId", empiId);
				empiModel.saveAddress(address);
			}
		}
		if (body.get("phones") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("phones");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> phone = list.get(i);
				phone.put("empiId", empiId);
				empiModel.savePhone(phone);
			}
		}
		if ((String) body.get("idCard") != null
				&& ((String) body.get("idCard")).trim().length() > 0
				|| body.get("certificates") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (body.get("certificates") != null) {
				list = (List<Map<String, Object>>) body.get("certificates");
			}
			if (((String) body.get("idCard")).trim().length() > 0) {
				Map<String, Object> idCard = new HashMap<String, Object>();
				idCard.put("certificateTypeCode", "01");
				idCard.put("certificateNo", body.get("idCard"));
				list.add(idCard);
			}
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> certificate = list.get(i);
				certificate.put("empiId", empiId);
				empiModel.saveCertificate(certificate, "create");
			}
		}
		if (body.get("extensions") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("extensions");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> extension = list.get(i);
				if (extension.get("propValue") != null
						&& !"".equals(extension.get("propValue"))) {
					extension.put("empiId", empiId);
					empiModel.saveExtension(extension);
				}
			}
		}
		if (body.get("cards") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("cards");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> card = list.get(i);
				String cardNo = (String) card.get("cardNo");
				if (cardNo != null) {
					card.put("cardNo", cardNo.toUpperCase());
					card.put("empiId", empiId);
					empiModel.saveCard(card);
				}
			}
		}
	}

	/**
	 * PIX个人基本信息转换成社区格式
	 * 
	 * @param records
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> changeToBSInfo(
			List<Map<String, Object>> records) {
		if (records != null && records.size() > 0) {
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				record.put("empiId", record.get("mpiId"));
				if (record.get("certificates") != null) {
					List<Map<String, Object>> certificates = (List<Map<String, Object>>) record
							.get("certificates");
					for (Map<String, Object> certificate : certificates) {
						if ("01".equals((String) certificate
								.get("certificateTypeCode"))) {
							record.put("idCard",
									certificate.get("certificateNo"));
						}
					}
				}
				if (record.get("contactWays") != null) {
					List<Map<String, Object>> phones = (List<Map<String, Object>>) record
							.get("contactWays");
					for (Map<String, Object> phone : phones) {
						if ("01".equals((String) phone.get("contactTypeCode"))) {
							record.put("mobileNumber", phone.get("contactNo"));
						}
						if ("04".equals((String) phone.get("contactTypeCode"))) {
							record.put("phoneNumber", phone.get("contactNo"));
						}
						if ("07".equals((String) phone.get("contactTypeCode"))) {
							record.put("email", phone.get("contactNo"));
						}
					}
				}
				record.put("mobileNumber", record.get("contactNo"));
				record.put("contact", record.get("contactName"));
				record.put("contactPhone", record.get("contactPhone"));
				record.put("zipCode", record.get("postalCode"));
				Date birthday = (Date) record.get("birthday");
				record.put("birthday", BSCHISUtil.toString(birthday));
				if (record.get("contacts") != null) {
					List<Map<String, Object>> contacts = (List<Map<String, Object>>) record
							.get("contacts");
					Map<String, Object> contact = contacts.get(0);
					record.put("contact", contact.get("contactName"));
				}
				if (record.get("addresses") != null) {
					List<Map<String, Object>> addresses = (List<Map<String, Object>>) record
							.get("addresses");
					Map<String, Object> address = addresses.get(0);
					record.put("zipCode", address.get("postalCode"));
					record.put("address", address.get("address"));
				}
				if (record.get("cards") != null) {
					List<Map<String, Object>> cards = (List<Map<String, Object>>) record
							.get("cards");
					for (Map<String, Object> card : cards) {
						if ("01".equals((String) card.get("cardTypeCode"))) {
							record.put("cardNo", card.get("cardNo"));
						}
					}
				}
				String insuranceType = (String) record.get("insuranceType");
				if (StringUtils.isNotEmpty(insuranceType)) {
					record.put("insuranceType", "");
					record.remove("insuranceType_text");
				}
				if ("0".equals(record.get("photo"))) {
					record.put("photo", "");
				}
				Date startWorkDate = (Date) record.get("startWorkDate");
				if (startWorkDate != null) {
					record.put("startWorkDate",
							BSCHISUtil.toString(startWorkDate));
				}
			}
			return records;
		}
		return null;
	}

	/**
	 * 注册
	 * 
	 * @param dao
	 * @param ctx
	 * @param body
	 *            社区系统存储数据
	 * @param PIXData
	 *            转换好格式的存入PIX的数据
	 * @return
	 * @throws ServiceException
	 */
	public static Map<String, Object> submitPerson(BaseDAO dao, Context ctx,
			Map<String, Object> body, Map<String, Object> PIXData)
			throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		String empiId = "";
		try {
			if (body.get("empiId") != null && !"".equals(body.get("empiId"))) {
				empiId = (String) body.get("empiId");
				if (getIfNeedPix(dao, ctx)) {
					Map<String, Object> map = empiInterfaceImpi
							.updatePerson(PIXData);
					empiId = (String) map.get("mpiId");
					String versionNumber = (String) map.get("versionNumber");
					body.put("versionNumber", versionNumber);
				}
			} else {
				if (getIfNeedPix(dao, ctx)) {
					Map<String, Object> map = empiInterfaceImpi
							.submitPerson(PIXData);
					empiId = (String) map.get("mpiId");
					String versionNumber = (String) map.get("versionNumber");
					body.put("versionNumber", versionNumber);
				} else {
					empiId = EmpiIdGenerator.generate();
				}
			}
			body.put("empiId", empiId);
			body.put("status", Constants.CODE_STATUS_NORMAL);
			saveEmpiInfo(body, empiModel, empiId);
			return body;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存个人基本信息失败。", e);
		}
	}

	public static EmpiInterfaceImpi empiInterfaceImpi = null;

	public static boolean getIfNeedPix(BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		String ifNeedPix = scmm.getSystemConfigData("ifNeedPix");
		if ("true".equals(ifNeedPix)) {
			if (EmpiUtil.empiInterfaceImpi == null) {
				EmpiInterfaceImpi empiInterfaceImpi = new EmpiInterfaceImpi(ctx);
				EmpiUtil.empiInterfaceImpi = empiInterfaceImpi;
			}
			return true;
		}
		return false;
	}

	/**
	 * 更新
	 * 
	 * @param dao
	 * @param ctx
	 * @param body
	 *            更新到社区系统的数据
	 * @param PIXData
	 *            转换好格式的更新到PIX的数据
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updatePerson(BaseDAO dao, Context ctx,
			Map<String, Object> reqBody, Map<String, Object> PIXData,
			Map<String, Object> jsonRes) throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		Map<String, Object> res = null;
		try {
			if (getIfNeedPix(dao, ctx)) {
				List<Map<String, Object>> mpiList = empiInterfaceImpi
						.advancedSearch(PIXData);
				if (mpiList != null && mpiList.size() > 0) {
					if (mpiList.size() == 1) {
						PIXData.put("mpiId", mpiList.get(0).get("mpiId"));
					} else {// 很少有多条记录情况
						String cMpiId = (String) PIXData.get("mpiId");
						String cIdCard = (String) PIXData.get("idCard");
						String cPersonName = (String) PIXData.get("personName");
						String cSexCode = (String) PIXData.get("sexCode");
						String cBirthday = (String) PIXData.get("birthday");
						List<Map<String, Object>> cCards = (List<Map<String, Object>>) PIXData
								.get("cards");
						String rMpiId = "";
						boolean hasMpiId = false;
						boolean hasIdCard = false;
						boolean hasNSD = false;// 姓名，性别，出生日期 相同
						boolean hasCardNo = false; // 卡号相同
						for (Map<String, Object> map : mpiList) {
							String mMpiId = (String) map.get("mpiId");
							String mIdCard = (String) map.get("idCard");
							String mPersonName = (String) map.get("personName");
							String mSexCode = (String) map.get("sexCode");
							String mBirthday = BSCHISUtil.toString((Date) map
									.get("birthday"));
							// mpiId 主键
							if (cMpiId.equals(mMpiId)) {
								rMpiId = mMpiId;
								hasMpiId = true;
								break;
							}
							// 身份证号
							if (StringUtils.isNotEmpty(cIdCard)
									&& StringUtils.isNotEmpty(mIdCard)
									&& cIdCard.equals(mIdCard)) {
								rMpiId = mMpiId;
								hasIdCard = true;
								break;
							}
							// 卡号
							List<Map<String, Object>> mCards = (List<Map<String, Object>>) map
									.get("cards");
							if ((cCards != null && cCards.size() > 0)
									&& (mCards != null && mCards.size() > 0)) {
								for (Map<String, Object> cMap : cCards) {
									String cCard = (String) cMap.get("cardNo");
									for (Map<String, Object> mMap : mCards) {
										String mCard = (String) mMap
												.get("cardNo");
										if (cCard.equals(mCard)) {
											hasCardNo = true;
											break;
										}
									}
									if (hasCardNo) {
										rMpiId = mMpiId;
										break;
									}
								}
							}
							if (hasCardNo) {
								break;
							}
							// 姓名，性别，出生日期 相关
							if (cPersonName.equals(mPersonName)
									&& cSexCode.equals(mSexCode)
									&& cBirthday.equals(mBirthday)) {
								rMpiId = mMpiId;
								hasNSD = true;
							}
						}
						if (StringUtils.isNotEmpty(rMpiId)) {
							if (hasMpiId) {
								PIXData.put("mpiId", rMpiId);
							} else if (hasIdCard) {
								PIXData.put("mpiId", rMpiId);
							} else if (hasCardNo) {
								PIXData.put("mpiId", rMpiId);
							} else if (hasNSD) {
								PIXData.put("mpiId", rMpiId);
							}
						}
					}
					try {
						res = empiInterfaceImpi.updatePerson(PIXData);
					} catch (Exception e) {
						throw new ServiceException(e);
					}
					if (res != null) {
						String versionNumber = (String) res
								.get("versionNumber");
						reqBody.put("versionNumber", versionNumber);
					}
				} else {
					Map<String, Object> locaInfos = new HashMap<String, Object>();
					String empiId = (String) PIXData.get("mpiId");
					String reverseEmpiId = new StringBuffer(empiId).reverse()
							.toString();
					String empiIdAfter12 = new StringBuffer(
							reverseEmpiId.substring(0, 12)).reverse()
							.toString();
					locaInfos.put("sourceId", "chis" + empiIdAfter12);
					locaInfos.put("localId", empiId);
					locaInfos.put("sourceType", "1");
					locaInfos.put("createTime", Calendar.getInstance()
							.getTime());
					PIXData.put("locaInfos", locaInfos);
					PIXData.remove("mpiId");
					Map<String, Object> map = empiInterfaceImpi
							.submitPerson(PIXData);
					String versionNumber = (String) map.get("versionNumber");
					reqBody.put("versionNumber", versionNumber);
				}
			}
			String empiId = (String) reqBody.get("empiId");
			String birthday = (String) reqBody.get("birthday");

			boolean result = ifVisitPlanChange(empiId, birthday, empiModel,
					dao, ctx);
			empiModel.saveEmpiRecord(reqBody, "update", false);
			
			if (reqBody.get("addressChange") != null
					&& (Boolean) reqBody.get("addressChange")) {
				empiModel.deleteAddressByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = (List<Map<String, Object>>) reqBody.get("addresses");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> address = list.get(i);
					address.put("empiId", empiId);
					empiModel.saveAddress(address);
				}
			}
			if (reqBody.get("telephoneChange") != null
					&& (Boolean) reqBody.get("telephoneChange")) {
				empiModel.deletePhoneByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = (List<Map<String, Object>>) reqBody.get("phones");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> phone = list.get(i);
					phone.put("empiId", empiId);
					empiModel.savePhone(phone);
				}
			}
			if (reqBody.get("certificateChange") != null
					&& (Boolean) reqBody.get("certificateChange")) {
				empiModel.deleteCertificateByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = (List<Map<String, Object>>) reqBody.get("certificates");
				if (((String) reqBody.get("idCard")).trim().length() > 0) {
					boolean flag = true;
					for (Map<String, Object> map : list) {
						String certificateTypeCode = (String) map
								.get("certificateTypeCode");
						if ("01".equals(certificateTypeCode)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						Map<String, Object> idCard = new HashMap<String, Object>();
						idCard.put("certificateTypeCode", "01");
						idCard.put("certificateNo", reqBody.get("idCard"));
						list.add(idCard);
					}
				}
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> certificate = list.get(i);
					certificate.put("empiId", empiId);
					empiModel.saveCertificate(certificate, "create");
				}
			} else if (reqBody.get("noIdCard") != null
					&& (Boolean) reqBody.get("noIdCard")
					&& ((String) reqBody.get("idCard")).trim().length() > 0) {
				Map<String, Object> idCard = new HashMap<String, Object>();
				idCard.put("certificateTypeCode", "01");
				idCard.put("certificateNo", reqBody.get("idCard"));
				idCard.put("empiId", empiId);
				empiModel.saveCertificate(idCard, "create");
			}
			if (reqBody.get("extensionChange") != null
					&& (Boolean) reqBody.get("extensionChange")) {
				empiModel.deleteExtensionByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = (List<Map<String, Object>>) reqBody.get("extensions");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> extension = list.get(i);
					if (extension.get("propValue") != null) {
						extension.put("empiId", empiId);
						empiModel.saveExtension(extension);
					}
				}
			}
			if (reqBody.get("cardChange") != null
					&& (Boolean) reqBody.get("cardChange")) {
				empiModel.deleteCardByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = (List<Map<String, Object>>) reqBody.get("cards");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> card = list.get(i);
					String cardNo = (String) card.get("cardNo");
					if (cardNo != null) {
						card.put("cardNo", cardNo.toUpperCase());
						card.put("empiId", empiId);
						empiModel.saveCard(card);
					}
				}
			}
			reqBody.put("visitPlanChanged", result);
			return reqBody;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("更新个人基本信息失败。", e);
		}
	}

	/**
	 * 
	 * @param dao
	 * @param ctx
	 * @param reqBody
	 *            必须内含empiId
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	public static Map<String, Object> queryByEmpiId(BaseDAO dao, Context ctx,
			Map<String, Object> reqBody) throws ServiceException {
		Map<String, Object> result = new HashMap<String, Object>();
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> info = new HashMap<String, Object>();
		EmpiModel empiModel = new EmpiModel(dao);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			info = empiModel.getEmpiInfoByEmpiid(empiId);
			if (info != null && info.size() > 0) {
				info.put("score", "1.0");
				putCertificateInfo(info, dao);
				list.add(info);
				result.put("body", list);
			} else {
				if (getIfNeedPix(dao, ctx)) {
					reqBody.put("mpiId", reqBody.get("empiId"));
					list = queryWithPIX(reqBody);
					if (list != null && list.size() > 0) {
						info = list.get(0);
						info.put("score", "1.0");
						result.put("body", list);
						result.put("dataSource", "pix");
					} else {
						result.put("body", new ArrayList<Map<String, Object>>());
					}
				}
			}
			return result;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据cardNo查询个人基本信息失败");
		}
	}

	/**
	 * 验证儿童随访计划是否修改
	 * 
	 * @param empiId
	 * @param birthday
	 * @param empiModel
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	private static boolean ifVisitPlanChange(String empiId, String birthday,
			EmpiModel empiModel, BaseDAO dao, Context ctx)
			throws ServiceException {
		Date nowBirth = BSCHISUtil.toDate(birthday);
		try {
			Map<String, Object> perMap = empiModel.getEmpiInfoByEmpiid(empiId);
			Date perBirth = (Date) perMap.get("birthday");
			// ** 判断出生日期是否有改动
			int dateCompare = BSCHISUtil.dateCompare(perBirth, nowBirth);
			if (dateCompare == 0) {
				return false;
			}
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			Map<String, Object> healthCard = chm
					.getNormalHealthCardByEmpiId(empiId);
			if (healthCard == null || healthCard.size() == 0) {
				return false;
			}
			int curAge = BSCHISUtil.calculateAge(nowBirth, null);
			Application app = null;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e1) {
				throw new ModelDataOperationException(e1);
			}
			String childrenRegisterAge = (String) app
					.getProperty("childrenRegisterAge");
			// ** 判断当前年龄是否大于儿童的建册截止年龄
			if (curAge > Integer.parseInt(childrenRegisterAge)) {
				Date createDate = (Date) healthCard.get("createDate");
				int createAge = BSCHISUtil.calculateAge(nowBirth, createDate);
				// ** 判断儿童档案的建册年龄是否大于儿童的建册截止年龄，若大于不允许修改出生日期
				if (createAge > Integer.parseInt(childrenRegisterAge)) {
					throw new ServiceException("儿童档案信息错误，不允许修改出生日期!");
				} else {
					return false;
				}
			} else {// ** 小于建册截止年龄，调整儿童档案的相关计划
				chm.changeChildPlan(birthday, empiId);
				return true;
			}
		} catch (Exception e) {
			throw new ServiceException("验证出生日期是否可以修改失败。", e);
		}
	}

	/**
	 * 
	 * @param dao
	 * @param ctx
	 * @param reqBody
	 *            必须内含卡号(cardNo)
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	public static Map<String, Object> queryByCardNo(BaseDAO dao, Context ctx,
			Map<String, Object> reqBody) throws ServiceException {
		Map<String, Object> result = new HashMap<String, Object>();
		String cardNo = (String) reqBody.get("cardNo");
		cardNo = cardNo.toUpperCase();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			list = empiModel.getEmpiInfoByCardNo(cardNo);
			List<Map<String, Object>> empiListStatus = empiModel
					.getEmpiListInStatus(cardNo);
			if (empiListStatus == null || empiListStatus.size() == 0) {
				if (list != null && list.size() > 0) {
					result.put("hasDisableRecord", true);
				}
				return result;
			} else {
				for (Map<String, Object> map : list) {
					String empiId = (String) map.get("empiId");
					for (Map<String, Object> map2 : empiListStatus) {
						String empiId2 = (String) map2.get("empiId");
						if (empiId.equals(empiId2)) {
							list2.add(map);
						}
					}
				}
				list = list2;
			}
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> record = list.get(i);
					String empiId = (String) record.get("empiId");
					record.put("cards", empiModel.getCardsList(empiId));
					record.put("score", "1.0");
					putCertificateInfo(record, dao);
				}
				result.put("body", list);
				return result;
			} else {
				if (getIfNeedPix(dao, ctx)) {
					Map<String, Object> card = new HashMap<String, Object>();
					card.put("cardNo", cardNo);
					card.put("cardTypeCode", "*");
					Map<String, Object> requestData = new HashMap<String, Object>();
					List<Map<String, Object>> cards = new ArrayList<Map<String, Object>>();
					cards.add(card);
					requestData.put("cards", cards);
					list = queryWithPIX(requestData);
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							list.get(i).put("score", "1.0");
						}
						result.put("dataSource", "pix");
						result.put("body", list);
					} else {
						result.put("body", new ArrayList<Map<String, Object>>());
					}
				}
				return result;
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"根据cardNo查询个人基本信息失败", e);
		}
	}

	private static void putCertificateInfo(Map<String, Object> record,
			BaseDAO dao) throws ModelDataOperationException {
		EmpiModel empiModel = new EmpiModel(dao);
		String empiId = (String) record.get("empiId");
		List<Map<String, Object>> list = empiModel
				.getCertificatesByEmpiId(empiId);
		if (list != null && list.size() > 0) {
			record.put("certificates", list);
		}
	}

	private static List<Map<String, Object>> queryWithPIX(
			Map<String, Object> reqBody) throws ServiceException {
		List<Map<String, Object>> records = empiInterfaceImpi
				.advancedSearch(reqBody);
		return records;
	}

	/**
	 * 根据身份证号码查询个人信息
	 * 
	 * @param dao
	 * @param ctx
	 * @param reqBody
	 *            必须内含idCard值，姓名值可有可无
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	public static Map<String, Object> queryByIdCardAndName(BaseDAO dao,
			Context ctx, Map<String, Object> reqBody) throws ServiceException {
		Map<String, Object> result = new HashMap<String, Object>();
		String idCard = (String) reqBody.get("idCard");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			if (idCard != null && idCard.trim().length() > 0) {
				list = empiModel.getEmpiInfoByIdcard(idCard);
			}
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> record = list.get(i);
					record.put("score", "1.0");
					String empiId = (String) record.get("empiId");
					record.put("cards", empiModel.getCardsList(empiId));
				}
			} else {
				if (reqBody.get("personName") != null) {
					String personName = (String) reqBody.get("personName");
					String year = idCard.substring(6, 10);
					String month = idCard.substring(10, 12);
					String day = idCard.substring(12, 14);
					String birthday = year + "-" + month + "-" + day;
					String sex = "";
					if (idCard.length() == 15) {
						sex = idCard.substring(14);
					} else if (idCard.length() == 18) {
						sex = idCard.substring(16, 17);
					}
					int sexNum = 3;
					if (sex.length() > 0) {
						sexNum = Integer.parseInt(sex) % 2;
						if (sexNum == 0) {
							sexNum = 2;
						}
						sex = sexNum + "";
					}
					list = empiModel.getEmpiInfoByBase(personName, sex,
							birthday);
					if (idCard != null && idCard.trim().length() > 0) {
						for (Map<String, Object> m : list) {
							int i = 0;
							String idC = (String) m.get("idCard");
							if (idC != null && idC.length() > 0) {
								list.remove(i);
								i--;
							}
							String empiId = (String) m.get("empiId");
							m.put("cards", empiModel.getCardsList(empiId));
							i++;
						}
					}
				}
			}
			if (list != null && list.size() > 0) {
				result.put("body", list);
			} else {
				if (getIfNeedPix(dao, ctx)) {
					list = queryWithPIX(reqBody);
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							list.get(i).put("score", "1.0");
						}
						result.put("dataSource", "pix");
						result.put("body", list);
					} else {
						result.put("body", new ArrayList<Map<String, Object>>());
					}
				}
			}
			return result;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据身份证号码查询个人信息失败");
		}
	}

	/**
	 * 根据姓名，性别，生日查询个人信息
	 * 
	 * @param dao
	 * @param ctx
	 * @param reqBody
	 *            必须内含姓名，性别，生日（personName，birthday，sexCode）三属性值
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	public static Map<String, Object> queryByPersonInfo(BaseDAO dao,
			Context ctx, Map<String, Object> reqBody) throws ServiceException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		EmpiModel empiModel = new EmpiModel(dao);
		String personName = (String) reqBody.get("personName");
		String birthday = (String) reqBody.get("birthday");
		String sex = (String) reqBody.get("sexCode");
		try {
			list = empiModel.getEmpiInfoByBase(personName, sex, birthday);
			if (list != null && list.size() > 0) {
				for (Map<String, Object> record : list) {
					String empiId = (String) record.get("empiId");
					record.put("cards", empiModel.getCardsList(empiId));
				}
				result.put("body", list);
				return result;
			} else {
				if (getIfNeedPix(dao, ctx)) {
					list = queryWithPIX(reqBody);
					if (list != null && list.size() > 0) {
						result.put("dataSource", "pix");
						result.put("body", list);
					} else {
						result.put("body", new ArrayList<Map<String, Object>>());
					}
				}
				return result;
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据姓名，性别，生日查询个人信息失败");
		}
	}

	/**
	 * 根据一张卡信息查询
	 * 
	 * @param dao
	 * @param ctx
	 * @param reqBody
	 *            参数形式如｛cards:[{cardNo:111,cardTypeCode:01}]｝,只允许有一张卡信息
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> queryByCard(BaseDAO dao, Context ctx,
			Map<String, Object> reqBody) throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		List<Map<String, Object>> cards = (List<Map<String, Object>>) reqBody
				.get("cards");
		if (cards == null || cards.size() == 0) {
			return null;
		}
		Map<String, Object> card = cards.get(0);
		String cardNo = (String) card.get("cardNo");
		String cardTypeCode = (String) card.get("cardTypeCode");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = empiModel.getEmpiInfoByCardNoAndCardTypeCode(cardNo,
					cardTypeCode);

			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			if (getIfNeedPix(dao, ctx)) {
				list = queryWithPIX(reqBody);
				if (list != null && list.size() > 0) {
					list.get(0).put("dataSource", "pix");
					return list.get(0);
				}
			}
			return null;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据卡号，卡类型查询个人信息失败");
		}
	}

	/**
	 * 根据一张证件信息查询
	 * 
	 * @param dao
	 * @param ctx
	 * @param certificateNo
	 * @param certificateTypeCode
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	public static Map<String, Object> queryByCertificate(BaseDAO dao,
			Context ctx, String certificateNo, String certificateTypeCode)
			throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = empiModel.getEmpiInfoBycertificateNoAndcertificateTypeCode(
					certificateNo, certificateTypeCode);

			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			Map<String, Object> reqBody = new HashMap<String, Object>();
			Map<String, Object> certificate = new HashMap<String, Object>();
			certificate.put("certificateNo", certificateNo);
			certificate.put("certificateTypeCode", certificateTypeCode);
			List<Map<String, Object>> certificates = new ArrayList<Map<String, Object>>();
			certificates.add(certificate);
			reqBody.put("certificates", certificates);
			if (getIfNeedPix(dao, ctx)) {
				list = queryWithPIX(reqBody);
				if (list != null && list.size() > 0) {
					list.get(0).put("dataSource", "pix");
					return list.get(0);
				}
			}
			return null;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据证件号，证件类型查询个人信息失败");
		}
	}

	/**
	 * 将传入的个人信息保存到EMPI并返回empiId，如果已保存过将直接返回empiId。
	 * 
	 * @param personInfo
	 * @param session
	 * @param isChild
	 *            是否为儿童基本信息
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	public static String saveEmpiInfo(Map<String, Object> personInfo,
			Context ctx, BaseDAO dao, Map<String, Object> jsonRes)
			throws ModelDataOperationException, ServiceException {
		// @@ 检查是否已存在。
		EmpiModel empiModel = new EmpiModel(dao);
		String empiId = (String) personInfo.get("empiId");
		if (empiId != null
				&& empiModel.isRecordExists(MPI_DemographicInfo, empiId, true)) {
			try {
				Map<String, Object> PIXData = EmpiUtil
						.changeToPIXFormat(personInfo);
				personInfo = EmpiUtil.updatePerson(dao, ctx, personInfo,
						PIXData, jsonRes);
				if (personInfo.get("idCard") != null
						&& ((String) personInfo.get("idCard")).length() > 0) {
					List<Map<String, Object>> list = empiModel
							.queryCertificateInfoByEmpiIdAndTypeCode(empiId,
									"01");
					if (list.size() > 0) {
						Map<String, Object> cardInfo = list.get(0);
						cardInfo.put("certificateNo", personInfo.get("idCard"));
						empiModel.saveCertificate(cardInfo, "update");
					} else {
						Map<String, Object> cardInfo = new HashMap<String, Object>();
						cardInfo.put("empiId", empiId);
						cardInfo.put("certificateTypeCode", "01");
						cardInfo.put("certificateNo", personInfo.get("idCard"));
						empiModel.saveCertificate(cardInfo, "create");
					}
				}
				return empiId;
			} catch (ModelDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_UNKNOWN_ERROR, "更新基本信息失败。");
			}
		}
		// @@ 如果没有注册过，新建一个。
		Map<String, Object> PIXData = EmpiUtil.changeToPIXFormat(personInfo);
		personInfo = EmpiUtil.submitPerson(dao, ctx, personInfo, PIXData);
		return (String) personInfo.get("empiId");
	}

	// 注册卡号
	public static void registerCards(BaseDAO dao, Context ctx,
			List<Map<String, Object>> cards) throws ServiceException,
			ModelDataOperationException {
		try {
			if (getIfNeedPix(dao, ctx)) {
				empiInterfaceImpi.registerCards(cards);
			}

		} catch (ServiceException e) {
			throw new ServiceException(Constants.CODE_UNKNOWN_ERROR, "注册卡信息失败。");

		}

	}

	// 组装卡号的数据
	public static List<Map<String, Object>> changeToCardData(
			Map<String, Object> body) {
		List<Map<String, Object>> cards = new ArrayList<Map<String, Object>>();
		Map<String, Object> info = new HashMap<String, Object>();
		if (body.get("empiId") != null
				&& ((String) body.get("empiId")).trim().length() > 0) {
			info.put("mpiId", body.get("empiId"));
		}
		if (body.get("cardTypeCode") != null
				&& ((String) body.get("cardTypeCode")).trim().length() > 0) {
			info.put("cardTypeCode", body.get("cardTypeCode"));
		}
		if (body.get("cardNo") != null
				&& ((String) body.get("cardNo")).trim().length() > 0) {
			info.put("cardNo", body.get("cardNo"));
		}

		if (info != null && info.size() > 0) {
			cards.add(info);
			return cards;
		}
		return null;

	}
}
