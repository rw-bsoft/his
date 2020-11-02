/*
 * @(#)BschisDAO.java Created on 2011-12-15 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.pix.source;

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

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;

import com.bsoft.pix.server.EmpiIdGenerator;

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
public class EmpiUtil implements BSPHISEntryNames {

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
			phone.put("contactNo", records.get("phoneNumber"));
			phones.add(phone);
		}
		if (records.get("email") != null
				&& ((String) records.get("email")).trim().length() > 0) {
			Map<String, Object> phone = new HashMap<String, Object>();
			phone.put("contactTypeCode", "07");
			phone.put("contactNo", records.get("email"));
			phones.add(phone);
		}
		if (records.get("contactPhone") != null
				&& ((String) records.get("contactPhone")).trim().length() > 0) {
			Map<String, Object> phone = new HashMap<String, Object>();
			phone.put("contactTypeCode", "03");
			phone.put("contactNo", records.get("contactPhone"));
			phones.add(phone);
		}
		records.put("contactWays", phones);
		if (records.get("zipCode") != null
				&& ((String) records.get("zipCode")).trim().length() > 0) {
			records.put("postalCode", records.get("zipCode"));
		}
		// if ((records.get("address") != null && ((String)
		// records.get("address"))
		// .trim().length() > 0)
		// || (records.get("zipCode") != null && ((String) records
		// .get("zipCode")).trim().length() > 0)) {
		// List<Map<String, Object>> addresses = (List<Map<String, Object>>)
		// records
		// .get("addresses");
		// if (addresses == null) {
		// addresses = new ArrayList<Map<String, Object>>();
		// }
		// Map<String, Object> address = new HashMap<String, Object>();
		// if (records.get("address") != null
		// && ((String) records.get("address")).trim().length() > 0) {
		// address.put("address", records.get("address"));
		// }
		// if (records.get("zipCode") != null
		// && ((String) records.get("zipCode")).trim().length() > 0) {
		// address.put("postalCode", records.get("zipCode"));
		// }
		// addresses.add(address);
		// records.put("addresses", addresses);
		// }
		if (records.get("contact") != null
				&& ((String) records.get("contact")).trim().length() > 0) {
			records.put("contactName", records.get("contact"));
		}
		removePkey(records);
		// if (records.get("empiId") != null) {
		// records = putMpiIdToRecords(records);
		// }
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
		empiModel.saveEmpiRecord(body, "create", false);
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
		if (((String) body.get("idCard")).trim().length() > 0
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
			empiModel.deleteCardByEmpiId(empiId);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> card = list.get(i);
				if(card.get("cardNo").toString().equals(body.get("MZHM").toString())){
					card.put("cardTypeCode", "04");
				}
				card.put("empiId", empiId);
				empiModel.saveCard(card);
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
					}
				}
				if (record.get("contactName") != null) {
					record.put("contact", record.get("contactName"));
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
		if (body.get("empiId") != null) {
			empiId = (String) body.get("empiId");
		} else {
			if (getIfNeedPix(dao, ctx)) {
				try {
					Map<String, Object> map = empiInterfaceImpi
							.submitPerson(PIXData);
					empiId = (String) map.get("mpiId");
					String versionNumber = (String) map.get("versionNumber");
					body.put("versionNumber", versionNumber);
				} catch (Exception e) {
					throw new ServiceException("连接MPI系统失败!", e);
				}
			} else {
				empiId = EmpiIdGenerator.generate();
			}
		}
		body.put("empiId", empiId);
		body.put("status", Constants.CODE_STATUS_NORMAL);
		try {
			saveEmpiInfo(body, empiModel, empiId);
			empiModel.saveBRDA(body, ctx);
			return body;
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存个人基本信息失败。", e);
		}
	}

	private static EmpiInterfaceImpi empiInterfaceImpi = null;

	private static boolean getIfNeedPix(BaseDAO dao, Context ctx) {
		String topUnitId = ParameterUtil.getTopUnitId();
		String mpi = ParameterUtil.getParameter(topUnitId,
				BSPHISSystemArgument.MPI, ctx);
		if ("1".equals(mpi)) {
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
			Map<String, Object> reqBody, Map<String, Object> PIXData)
			throws ServiceException {
		EmpiModel empiModel = new EmpiModel(dao);
		if (getIfNeedPix(dao, ctx)) {
			// 2012-12-11 add by zhangyq
			Map<String, Object> QMap = new HashMap<String, Object>();
			QMap = empiInterfaceImpi.advancedSearch(reqBody.get("empiId") + "");
			if (QMap != null && QMap.size() > 0) {
				try {
					Map<String, Object> res = empiInterfaceImpi
							.updatePerson(PIXData);
					String versionNumber = (String) res.get("versionNumber");
					reqBody.put("versionNumber", versionNumber);
				} catch (Exception e) {
					throw new ServiceException("连接MPI系统失败!", e);
				}
			} else {
				String empiId = "";
				String oldEmpiId = (String) PIXData.get("mpiId");
				PIXData.remove("mpiId");
				try {
					Map<String, Object> map = empiInterfaceImpi
							.submitPerson(PIXData);
					empiId = (String) map.get("mpiId");
					String versionNumber = (String) map.get("versionNumber");
					reqBody.put("versionNumber", versionNumber);
				} catch (Exception e) {
					throw new ServiceException("连接MPI系统失败!", e);
				}
				reqBody.put("empiId", empiId);
				reqBody.put("status", Constants.CODE_STATUS_NORMAL);
				try {
					saveEmpiInfo(reqBody, empiModel, empiId);
					dao.doRemove("empiId", oldEmpiId,
							BSPHISEntryNames.MPI_DemographicInfo);
					empiModel.deleteCardByEmpiId(oldEmpiId);
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("EMPIID", empiId);
					parameters.put("OLDEMPIID", oldEmpiId);
					dao.doUpdate(
							"update MS_BRDA "
									+ " set EMPIID = :EMPIID where EMPIID = :OLDEMPIID",
							parameters);
//					dao.doUpdate(
//							"update MPI_Card "
//									+ " set empiId = :EMPIID where empiId = :OLDEMPIID",
//							parameters);
				} catch (ModelDataOperationException e) {
					throw new ServiceException("修改个人基本信息失败。", e);
				} catch (PersistentDataOperationException e) {
					throw new ServiceException("修改个人基本信息失败。", e);
				}
			}
			// 2012-12-11 add by zhangyq
			// Map<String, Object> res =
			// empiInterfaceImpi.updatePerson(PIXData);
			// String versionNumber = (String) res.get("versionNumber");
			// reqBody.put("versionNumber", versionNumber);
		}
		String empiId = (String) reqBody.get("empiId");
		try {
			boolean result = false;
			empiModel.saveEmpiRecord(reqBody, "update", false);
			empiModel.saveBRDA(reqBody, ctx);
			//其它联系地址
			if (reqBody.get("addressChange") != null
					&& (Boolean) reqBody.get("addressChange")) {
				empiModel.deleteAddressByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = reqBody.get("addresses") == null ? list : (List<Map<String, Object>>) reqBody.get("addresses");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> address = list.get(i);
					address.put("empiId", empiId);
					empiModel.saveAddress(address);
				}
			}
			//其它联系电话
			if (reqBody.get("telephoneChange") != null
					&& (Boolean) reqBody.get("telephoneChange")) {
				empiModel.deletePhoneByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = reqBody.get("phones") == null ? list : (List<Map<String, Object>>) reqBody.get("phones");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> phone = list.get(i);
					phone.put("empiId", empiId);
					empiModel.savePhone(phone);
				}
			}
			//其它证件
			if (reqBody.get("certificateChange") != null
					&& (Boolean) reqBody.get("certificateChange")) {
				empiModel.deleteCertificateByEmpiId(empiId);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = reqBody.get("certificates") == null ? list : (List<Map<String, Object>>) reqBody.get("certificates");
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
				if(list != null){
					for (int i = 0; i < list.size(); i++) {
						Map<String, Object> certificate = list.get(i);
						certificate.put("empiId", empiId);
						empiModel.saveCertificate(certificate, "create");
					}
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
				list = reqBody.get("extensions") == null ? list : (List<Map<String, Object>>) reqBody.get("extensions");
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
				list = reqBody.get("cards") == null ? list : (List<Map<String, Object>>) reqBody.get("cards");
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						Map<String, Object> card = list.get(i);
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
	 *            必须内含卡号(cardNo)
	 * @return 如果是PIX中数据，会在结果集中，放入参数result.put("dataSource", "pix");
	 * @throws ServiceException
	 */
	public static Map<String, Object> queryByMZHM(BaseDAO dao, Context ctx,
			Map<String, Object> reqBody) throws ServiceException {
		Map<String, Object> result = new HashMap<String, Object>();
		String MZHM = (String) reqBody.get("MZHM");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			list = empiModel.getEmpiInfoByMZHM(MZHM);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> record = list.get(i);
					record.put("score", "1.0");
					putCertificateInfo(record, dao);
				}
				result.put("body", list);
				return result;
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
				return result;
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据MZHM查询个人基本信息失败");
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
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		EmpiModel empiModel = new EmpiModel(dao);
		try {
			list = empiModel.getEmpiInfoByCardNo(cardNo);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> record = list.get(i);
					record.put("score", "1.0");
					putCertificateInfo(record, dao);
				}
				result.put("body", list);
				return result;
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
				return result;
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据cardNo查询个人基本信息失败");
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

	public static Map<String, Object> queryWithPIX(String empiId, BaseDAO dao,
			Context ctx) throws ServiceException {
		Map<String, Object> record = new HashMap<String, Object>();
		;
		if (getIfNeedPix(dao, ctx)) {
			record = empiInterfaceImpi.advancedSearch(empiId);
		}
		return record;
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
					list.get(i).put("score", "1.0");
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
		if (getIfNeedPix(dao, ctx)) {
			list = queryWithPIX(reqBody);
			if (list != null && list.size() > 0) {
				// result.put("dataSource", "pix");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					Map<String, Object> mapParameters = new HashMap<String, Object>();
					mapParameters.put("EMPIID", map.get("mpiId"));
					try {
						Map<String, Object> map2=dao.doLoad(
								"select MZHM as MZHM,BRXZ as BRXZ from "
										+ " MS_BRDA"
										+ " where EMPIID=:EMPIID",
								mapParameters);
						if(!"".equals(map2) && !"null".equals(map2)  && map2!=null){
							map.putAll(map2);
						}
						
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				result.put("body", list);
			} else {
				try {
					list = empiModel.getEmpiInfoByBase(personName, sex,
							birthday);
				} catch (ModelDataOperationException e) {
					throw new ServiceException("根据姓名，性别，生日查询个人信息失败");
				}
				if (list != null && list.size() > 0) {
					result.put("body", list);
					return result;
				} else {
					result.put("body", new ArrayList<Map<String, Object>>());
				}
			}
		} else {
			try {
				list = empiModel.getEmpiInfoByBase(personName, sex, birthday);
			} catch (ModelDataOperationException e) {
				throw new ServiceException("根据姓名，性别，生日查询个人信息失败");
			}
			if (list != null && list.size() > 0) {
				result.put("body", list);
				return result;
			} else {
				result.put("body", new ArrayList<Map<String, Object>>());
			}
		}
		return result;
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
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据卡号，卡类型查询个人信息失败");
		}
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
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据证件号，证件类型查询个人信息失败");
		} catch (ControllerException e) {
			throw new ServiceException("根据证件号，证件类型查询个人信息失败");
		}
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
			Context ctx, BaseDAO dao) throws ModelDataOperationException,
			ServiceException {
		// @@ 检查是否已存在。
		EmpiModel empiModel = new EmpiModel(dao);
		String empiId = (String) personInfo.get("empiId");
		if (empiId != null
				&& empiModel.isRecordExists(MPI_DemographicInfo, empiId, true)) {
			try {
				Map<String, Object> PIXData = EmpiUtil
						.changeToPIXFormat(personInfo);
				personInfo = EmpiUtil.updatePerson(dao, ctx, personInfo,
						PIXData);
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
}
