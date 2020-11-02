package chis.source.print.instance;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import javax.servlet.http.HttpSession;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.BSCHISUtil;
import com.alibaba.fastjson.JSONException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HealthCheckupFile extends BSCHISPrint implements IHandler {

	private String checkupNo;

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		try {
			response = getFirstRecord(getBaseInfo(EHR_FamilyRecord, request,
					ctx));
			checkupNo = (String) request.get("checkupNo");
			getDao(ctx);
			response.putAll(this.getEmpiInfo(request, ctx));
			String jrxml = (String) request.get("jrxml");
			if (jrxml.contains("healthCheckupALL")) {
				response.putAll(this.getLifeStyle(request, ctx));
				response.putAll(this.getPastHistory(request, ctx));
				response.putAll(this.getHealthCheckupRecord(request, ctx));
				response.putAll(this.getHealthRecord(request, ctx));
				response.putAll(this.getCardNo(request, ctx));
			} else if (jrxml.contains("healthCheckup01")) {
				Date birth = BSCHISUtil.toDate((String) response
						.get("birthday"));
				int age = BSCHISUtil.calculateAge(birth, null);
				response.put("age", age);
				response.putAll(this.getHealthCheckupBady(request, ctx));
			} else {
				response.putAll(this.getHealthCheckupDetail(request, ctx));
				response.putAll(this.getHealthCheckupRecord(request, ctx));
				response.putAll(this.getCardNo(request, ctx));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		sqlDate2String(response);
	}

	private Map<String, Object> getHealthRecord(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = getBaseInfo(EHR_HealthRecord, map, ctx);
		;
		if (list.size() > 0) {
			Map<String, Object> record = list.get(0);
			parameters.put("all_manager", record.get("manaUnitId_text"));
		}
		return parameters;
	}

	private Map<String, Object> getHealthCheckupBady(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq',['$','a.checkupNo'],['s','"
					+ checkupNo + "']]"), null,
			"PER_CheckupBady");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list.size() > 0) {
			Map<String, Object> record = list.get(0);
			parameters.putAll(record);
		}
		String url = ((HttpSession) ctx.get(Context.WEB_SESSION))
				.getServletContext().getRealPath("img/babyScreen.jpg");
		parameters.put("babyScreen", url);

		return parameters;
	}

	private Map<String, Object> getHealthCheckupRecord(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq',['$','checkupNo'],['s','"
					+ checkupNo + "']]"), "a.checkupNo desc",
			BSCHISEntryNames.PER_CheckupRegister);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list.size() > 0) {
			Map<String, Object> record = list.get(0);
			parameters.putAll(record);
			parameters.put("checkupTime", record.get("checkupTime").toString());
			parameters.put("birthday", record.get("birthday").toString());
			parameters.put("jobYear", record.get("jobYear") == null ? ""
					: record.get("jobYear").toString());
			if (null != record.get("checkupExce")
					&& !record.get("checkupExce").toString().trim().equals("")) {
				parameters.put("hasExce", "2");
			} else {
				parameters.put("hasExce", "1");
			}
		}
		return parameters;
	}

	private Map<String, Object> getHealthCheckupDetail(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("physique", "0");
		parameters.put("wh", "0");
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['eq',['$','checkupNo'],['s','"
					+ checkupNo + "']]"),null,
					BSCHISEntryNames.PER_CheckupDetail);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Iterator<Map<String, Object>> it = list.iterator();
		String h = null;// 身高
		String w = null;// 体重
		String wl = null;// 腰围
		String hl = null;// 臀围
		Map<String, Object> dicItems = this.getDicItems();
		while (it.hasNext()) {
			Map<String, Object> detail = it.next();

			// key
			String checkupProjectId = detail.get("checkupProjectId").toString()
					.trim();
			String checkupProjectIdifException = checkupProjectId
					+ "ifException";

			// value
			Object ifException = detail.get("ifException");// 体检结果
			// Object checkupOutcome = detail.get("checkupOutcome");// 结果描述
			Object memo = detail.get("memo");// 备注
			if (dicItems.containsKey(checkupProjectId)) {
				parameters.put(checkupProjectId, memo);
				if (ifException != null) {
					parameters.put(checkupProjectIdifException,
							ifException.toString());
				}
			} else {
				parameters.put(checkupProjectId, ifException);
			}

			if (checkupProjectId != null
					&& checkupProjectId.equals("0000000000000066")
					&& ifException != null) {
				if (this.isNum(ifException.toString())) {
					w = ifException.toString();
				}
			} else if (checkupProjectId != null
					&& checkupProjectId.equals("0000000000000065")
					&& ifException != null) {
				if (this.isNum(ifException.toString())) {
					h = ifException.toString();
					// parameters.put("0000000000000065", Float.valueOf(h) / 100
					// + "");
				}
			} else if (checkupProjectId != null
					&& checkupProjectId.equals("0000000000000071")
					&& ifException != null) {
				if (this.isNum(ifException.toString())) {
					wl = ifException.toString();
				}
			} else if (checkupProjectId != null
					&& checkupProjectId.equals("0000000000000072")
					&& ifException != null) {
				if (this.isNum(ifException.toString())) {
					hl = ifException.toString();
				}
			}
			if (w != null && h != null) {
				float physique = Float.valueOf(w)
						/ ((Float.valueOf(h) / 100) * (Float.valueOf(h) / 100));
				parameters.put("physique",
						new DecimalFormat("###,###,###.##").format(physique)
								+ "");
			}

			if (wl != null && hl != null) {
				float wh = Float.valueOf(wl) / Float.valueOf(hl);
				parameters.put("wh",
						new DecimalFormat("###,###,###.##").format(wh) + "");
			}

			if (checkupProjectId != null
					&& (checkupProjectId.equals("0000000000000174")
							|| checkupProjectId.equals("0000000000000009")
							|| checkupProjectId.equals("0000000000000021") || checkupProjectId
							.equals("0000000000000230"))) {
				parameters.put(
						checkupProjectId
								+ detail.get("projectOfficeCode").toString(),
						ifException);
			}

			// if (checkupProjectId != null && ifException != null
			// && checkupProjectId.equals("0000000000000114")
			// && Integer.parseInt(ifException.toString()) > 3)// 乳腺
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 3;
			// } else if (checkupProjectId != null && ifException != null
			// && checkupProjectId.equals("0000000000000082")
			// && Integer.parseInt(ifException.toString()) > 3)// 桶状胸
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 12;
			// } else if (checkupProjectId != null && ifException != null
			// && checkupProjectId.equals("0000000000000042")
			// && Integer.parseInt(ifException.toString()) > 3)// 罗音
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 8;
			// } else if (checkupProjectId != null && ifException != null
			// && checkupProjectId.equals("0000000000000031")
			// && Integer.parseInt(ifException.toString()) > 3)// 心律
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 14;
			// } else if (checkupProjectId != null && ifException != null
			// && checkupProjectId.equals("0000000000000221")
			// && Integer.parseInt(ifException.toString()) > 3)// 血型
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 25;
			// } else if (checkupProjectId != null && ifException != null
			// && checkupProjectId.equals("0000000000000282")
			// && Integer.parseInt(ifException.toString()) > 3)// RH阴性
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 30;
			// } else if (checkupProjectId != null
			// && ifException != null
			// && (checkupProjectId.equals("0000000000000036") ||
			// checkupProjectId
			// .equals("0000000000000037"))
			// && Integer.parseInt(ifException.toString()) > 3)// 杂音 移动性浑浊音
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 17;
			// } else if (checkupProjectId != null
			// && ifException != null
			// && (checkupProjectId.equals("0000000000000063") ||
			// checkupProjectId
			// .equals("0000000000000062"))
			// && Integer.parseInt(ifException.toString()) > 3)// 老年人认知 情感
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 21;
			// } else if (checkupProjectId != null && ifException != null
			// && (checkupProjectId.equals("0000000000000099"))
			// && Integer.parseInt(ifException.toString()) > 3)// 水肿
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 33;
			// } else if (checkupProjectId != null && ifException != null
			// && (checkupProjectId.equals("0000000000000032"))
			// && Integer.parseInt(ifException.toString()) > 3)// 足背动脉搏动
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 37;
			// } else if (checkupProjectId != null && ifException != null
			// && (checkupProjectId.equals("0000000000000285"))
			// && Integer.parseInt(ifException.toString()) > 3)// 老年人运动
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 23;
			// } else if (checkupProjectId != null
			// && ifException != null
			// && (checkupProjectId.equals("0000000000000050")
			// || checkupProjectId.equals("0000000000000051")
			// || checkupProjectId.equals("0000000000000046") ||
			// checkupProjectId
			// .equals("0000000000000048"))
			// && Integer.parseInt(ifException.toString()) > 3)//
			// 腹部：压痛BLK、包块BLK、肝大、脾大
			// {
			// ifException = Integer.parseInt(ifException.toString()) - 19;
			// }

			// if (ifException != null) {
			// parameters.put(checkupProjectIdifException,
			// ifException.toString());
			// }
		}
		return parameters;
	}

	public boolean isNum(String s) {
		if (s.length() < 1) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				if (s.charAt(i) != '.')
					return false;
			}
		}
		return true;
	}

	/**
	 * 返回存在字典的体检项目列表
	 * 
	 * @return
	 * @throws PrintException
	 * @throws ServiceException
	 * @throws JSONException
	 */

	private Map<String, Object> getDicItems() throws PrintException,
			ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("['gt', ['$', 'length(checkupDic)'], ['i', '0']]"), "orderNo asc",
					BSCHISEntryNames.PER_CheckupDict);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> detail = it.next();
			String checkupProjectId = detail.get("checkupProjectId").toString()
					.trim();
			String checkupDic = detail.get("checkupDic").toString().trim();
			parameters.put(checkupProjectId, checkupDic);
		}
		return parameters;
	}

	@SuppressWarnings("static-access")
	private Map<String, Object> getEmpiInfo(Map<String, Object> request,
			Context ctx) throws PrintException, ServiceException {

		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = this.getBaseInfo(MPI_DemographicInfo,
				request, ctx);
		if (list.size() > 0) {
			Map<String, Object> empi = list.get(0);
			// 基本信息
			parameters.putAll(empi);
			// 把birthday从java sql Data 换成String
			parameters.put("birthday", empi.get("birthday").toString());
			// 区分 汉族 少数民族
			String nationCode = empi.get("nationCode").toString();
			if (nationCode.equals("01")) {
				nationCode = "1";
			} else {
				nationCode = "2";
			}
			parameters.remove("nationCode");
			parameters.put("nationCode", nationCode);
			String jrxml = (String) request.get("jrxml");
			if (jrxml != null && jrxml.contains("healthCheckupStudent1")) {
				parameters.put("age", this.calculateAge(
						(Date) empi.get("birthday"), new Date()));
			}
		}

		return parameters;
	}

	public static int calculateAge(Date birthday, Date calculateDate) {
		Calendar c = Calendar.getInstance();
		if (calculateDate != null) {
			c.setTime(calculateDate);
		}
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthday);
		int age = c.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		c.set(Calendar.YEAR, birth.get(Calendar.YEAR));
		if (dateCompare(c.getTime(), birth.getTime()) < 0) {
			return age - 1;
		}
		return age;
	}

	public static int dateCompare(Date d1, Date d2) {
		Calendar c = Calendar.getInstance();
		c.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c2.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c2.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR));
		Date date0 = c2.getTime();

		c.setTime(d2);
		c2.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c2.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c2.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR));
		Date date1 = c2.getTime();

		return date0.compareTo(date1);
	}

	private Map<String, Object> getCardNo(Map<String, Object> map, Context ctx)
			throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String empiId = (String) map.get("empiId");
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd("[and,['eq',['$','empiId'],['s','"
					+ empiId + "']],['eq',['$','cardTypeCode'],['s','03']]]"),null,
					BSCHISEntryNames.MPI_Card);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list.size() > 0) {
			parameters.putAll(list.get(0));
		}
		return parameters;
	}

	private Map<String, Object> getLifeStyle(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {

		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = getBaseInfo(EHR_LifeStyle, map, ctx);

		if (list.size() > 0) {
			Map<String, Object> lifeStyle = list.get(0);
			parameters.putAll(lifeStyle);
			parameters.put(
					"smokeCount",
					lifeStyle.get("smokeCount") == null ? "" : lifeStyle.get(
							"smokeCount").toString());
			parameters.put("smokeStartAge",
					lifeStyle.get("smokeStartAge") == null ? "" : lifeStyle
							.get("smokeStartAge").toString());
			parameters.put(
					"smokeEndAge",
					lifeStyle.get("smokeEndAge") == null ? "" : lifeStyle.get(
							"smokeEndAge").toString());
			parameters.put("drinkStartAge",
					lifeStyle.get("drinkStartAge") == null ? "" : lifeStyle
							.get("drinkStartAge").toString());
			parameters.put(
					"trainMinute",
					lifeStyle.get("trainMinute") == null ? "" : lifeStyle.get(
							"trainMinute").toString());
			parameters.put("trainYear", lifeStyle.get("trainYear") == null ? ""
					: lifeStyle.get("trainYear").toString());
			parameters.put(
					"drinkEndAge",
					lifeStyle.get("drinkEndAge") == null ? "" : lifeStyle.get(
							"drinkEndAge").toString());
			parameters.put(
					"drinkCount",
					lifeStyle.get("drinkCount") == null ? "" : lifeStyle.get(
							"drinkCount").toString());
		}

		return parameters;
	}

	private Map<String, Object> getPastHistory(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = getBaseInfo(EHR_PastHistory, map, ctx);

		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> healthHis = list.get(i);
				if (healthHis.get("diseaseCode").toString() != null
						&& healthHis.get("diseaseCode").toString()
								.equals("1501")) {
					parameters.put("151501", healthHis.get("diseaseText"));
				} else if (healthHis.get("diseaseCode").toString() != null
						&& healthHis.get("diseaseCode").toString()
								.equals("1502")) {
					parameters.put("151502", healthHis.get("diseaseText"));
				} else if (healthHis.get("diseaseCode").toString() != null
						&& healthHis.get("diseaseCode").toString()
								.equals("1503")) {
					parameters.put("151503", healthHis.get("diseaseText"));
				} else if (healthHis.get("diseaseCode").toString() != null
						&& healthHis.get("diseaseCode").toString()
								.equals("1504")) {
					parameters.put("151504", healthHis.get("diseaseText"));
				}

				if (healthHis.get("diseaseCode").equals("1502")) {
					if (healthHis.get("protect") == null
							|| healthHis.get("protect").toString().trim()
									.equals("")) {
						parameters.put("151502ispro", "1");
					} else {
						parameters.put("151502ispro", "2");
						parameters.put("151502pro", healthHis.get("protect"));
					}
				} else if (healthHis.get("diseaseCode").equals("1503")) {

					if (healthHis.get("protect") == null
							|| healthHis.get("protect").toString().trim()
									.equals("")) {
						parameters.put("151503ispro", "1");
					} else {
						parameters.put("151503ispro", "2");
						parameters.put("151503pro", healthHis.get("protect"));
					}
				} else if (healthHis.get("diseaseCode").equals("1504")) {
					if (healthHis.get("protect") == null
							|| healthHis.get("protect").toString().trim()
									.equals("")) {
						parameters.put("151504ispro", "1");
					} else {
						parameters.put("151504ispro", "2");
						parameters.put("151504pro", healthHis.get("protect"));
					}
				}
			}

		}

		return parameters;
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
