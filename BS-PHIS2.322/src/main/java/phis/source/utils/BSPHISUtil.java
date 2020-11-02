package phis.source.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.impl.SessionFactoryImpl;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class BSPHISUtil {
	private static Map<String, Long> mzlbCache = new ConcurrentHashMap<String, Long>();

	/**
	 * 修改发票号码
	 * 
	 * @param uid
	 *            员工代码
	 * @param manageUnit
	 *            所属机构
	 * @param type
	 *            票据类别 1:就诊号码 2：门诊号码 3：发票号码
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public static String updateNotesNumber(String uid, String manageUnit,
			int type, BaseDAO dao, String pjhm)
			throws PersistentDataOperationException, ValidateException {
		StringBuffer hql = new StringBuffer(
				"select SYHM as SYHM,JLXH as JLXH,ZZHM as ZZHM,QSHM as QSHM from MS_YGPJ");
		hql.append(" where YGDM=:YGDM and JGID=:JGID and SYHM<=ZZHM and PJLX=:PJLX and SYPB = 0 order by LYRQ ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("YGDM", uid);
		params.put("JGID", manageUnit);
		params.put("PJLX", type);
		params.put("first", 0);
		params.put("max", 1);
		List<Map<String, Object>> ret = dao.doQuery(hql.toString(), params);
		if (ret.size() < 1)
			return null;
		Map<String, Object> record = ret.get(0);
		String syhm = MedicineUtils.parseString(record.get("SYHM"));
		String zzhm = MedicineUtils.parseString(record.get("ZZHM")) ;
		String qshm = MedicineUtils.parseString(record.get("QSHM")) ;
		if (pjhm.length() == syhm.length()
				&& pjhm.compareTo(zzhm) <0 
				&& pjhm.compareTo(qshm) > 0) {//无需类型转换比较，使用compareTo方法比较
			record.put("SYHM", pjhm);
			dao.doSave("update", BSPHISEntryNames.MS_YGPJ + "_JZ", record,
					false);
		} else {
			return null;
		}

		return pjhm;
	}

	/**
	 * 获取票据号码，并自增1
	 * 
	 * @param uid
	 *            员工代码
	 * @param manageUnit
	 *            所属机构
	 * @param type
	 *            票据类别 1:就诊号码 2：门诊号码 3：发票号码
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("static-access")
	public static String getNotesNumber(String uid, String manageUnit,
			int type, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		String autoCreate = "0";
		parameters.put("PJLX", type);
		parameters.put("YGDM", uid);
		parameters.put("JGID", manageUnit);
		try {
			long ll_count = dao
					.doCount(
							"MS_YGPJ",
							"JGID = :JGID and YGDM = :YGDM and PJLX =:PJLX and SYPB = 0",
							parameters);
			if (ll_count > 0) {
				return getNotesNumber1(uid, manageUnit, type, dao);
			}
			ParameterUtil pu = new ParameterUtil();
			if (3 == type) {
				autoCreate = pu.getParameter(manageUnit,
						BSPHISSystemArgument.ZDCSMZH, ctx);
			} else if (1 == type) {
				autoCreate = pu.getParameter(manageUnit,
						BSPHISSystemArgument.ZDCSJZH, ctx);
			}
			if ("1".equals(autoCreate)) {
				String topUnitId = pu.getTopUnitId();
				int li_zdgh = 0;
				// 自动生成门诊号码的前10位
				BSHISUtil bsu = new BSHISUtil();
				String ls_today = bsu.getDate();
				String ls_rq = ls_today.substring(2, 4)
						+ ls_today.substring(5, 7) + ls_today.substring(8, 10);
				String ls_zdmzh = "0";
				if (3 == type) {
					String ZDMZHXNGH = pu.getParameter(topUnitId,
							BSPHISSystemArgument.ZDMZHXNGH, ctx);
					li_zdgh = Integer.parseInt(ZDMZHXNGH);
					if (li_zdgh == 9999) {
						li_zdgh = 1;
					} else {
						li_zdgh = Integer.parseInt(ZDMZHXNGH) + 1;
					}
					ls_zdmzh = ls_rq + String.format("%0" + 4 + "d", li_zdgh);
				} else if (1 == type) {
					String ZDMZHXNGH = pu.getParameter(topUnitId,
							BSPHISSystemArgument.ZDJZHXNGH, ctx);
					li_zdgh = Integer.parseInt(ZDMZHXNGH);
					if (li_zdgh == 99) {
						li_zdgh = 1;
					} else {
						li_zdgh = Integer.parseInt(ZDMZHXNGH) + 1;
					}
					ls_zdmzh = ls_rq + String.format("%0" + 2 + "d", li_zdgh);
				}
				// 获取起始和终止号码
				String ls_qshm = ls_zdmzh + "001";
				String ls_syhm = ls_zdmzh + "002";
				String ls_zzhm = ls_zdmzh + "999";

				// 将生成的自动门诊号码写入员工票据中
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("JGID", manageUnit);
				data.put("YGDM", uid);
				data.put("LYRQ", new Date());
				data.put("PJLX", type);
				data.put("QSHM", ls_qshm);
				data.put("ZZHM", ls_zzhm);
				data.put("SYHM", ls_syhm);
				data.put("SYPB", 0);
				dao.doInsert(BSPHISEntryNames.MS_YGPJ + "_MZ", data, true);

				parameters1.put("CSZ", "" + li_zdgh);
				if (3 == type) {
					dao.doUpdate(
							"UPDATE GY_XTCS Set CSZ = :CSZ Where CSMC = 'ZDMZHXNGH'",
							parameters1);
					pu.reloadParams(topUnitId, BSPHISSystemArgument.ZDMZHXNGH);
				} else if (1 == type) {
					dao.doUpdate(
							"UPDATE GY_XTCS Set CSZ = :CSZ Where CSMC = 'ZDJZHXNGH'",
							parameters1);
					pu.reloadParams(topUnitId, BSPHISSystemArgument.ZDJZHXNGH);
				}
				return ls_qshm;
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			return null;
		} catch (ValidateException e) {
			return null;
		}
	}

	/**
	 * 获取票据号码，并自增1
	 * 
	 * @param uid
	 *            员工代码
	 * @param manageUnit
	 *            所属机构
	 * @param type
	 *            票据类别 1:就诊号码 2：门诊号码 3：发票号码
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public static String getNotesNumber1(String uid, String manageUnit,
			int type, BaseDAO dao) throws PersistentDataOperationException,
			ValidateException {
		StringBuffer hql = new StringBuffer(
				"select SYHM as SYHM,JLXH as JLXH,ZZHM as ZZHM from MS_YGPJ");
		hql.append(" where YGDM=:YGDM and JGID=:JGID and SYHM<=ZZHM and PJLX=:PJLX and SYPB = 0 order by LYRQ ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("YGDM", uid);
		params.put("JGID", manageUnit);
		params.put("PJLX", type);
		params.put("first", 0);
		params.put("max", 1);
		List<Map<String, Object>> ret = dao.doQuery(hql.toString(), params);
		if (ret.size() < 1)
			return null;
		Map<String, Object> record = ret.get(0);
		String syhm = MedicineUtils.parseString(record.get("SYHM")) ;
		StringBuffer sb = new StringBuffer(syhm);
		sb = sb.reverse();
		String syhmnew = sb.toString();
		String zzhm = MedicineUtils.parseString(record.get("ZZHM")) ;
		if (zzhm.equals(syhm)) {
			record.put("SYPB", 1);
			dao.doSave("update", BSPHISEntryNames.MS_YGPJ + "_JZ", record,
					false);
		} else {
			if (type == 2) {
				int length = syhm.length();
				int slen = length;
				for (int i = 0; i < length; i++) {
					char n = syhmnew.charAt(i);
					if (n < 48 || n > 57) {
						slen = i;
						break;
					}
				}
				String sz = syhm.substring(syhm.length() - slen);
				String zm = syhm.substring(0, syhm.length() - slen);
				long intnewsz = Long.parseLong(sz) + 1;
				String newssz = String.format("%0" + (slen) + "d", intnewsz);
				record.put("SYHM", zm + newssz);
				dao.doSave("update", BSPHISEntryNames.MS_YGPJ + "_JZ", record,
						false);
			} else {
				int length = syhm.length();
				long intnewsyhm = Long.parseLong(syhm) + 1;
				String newsyhm = String.format("%0" + length + "d", intnewsyhm);
				record.put("SYHM", newsyhm);
				dao.doSave("update", BSPHISEntryNames.MS_YGPJ + "_JZ", record,
						false);
			}
		}
		return syhm;
	}

	/**
	 * 获取票据号码，票据号码不自增
	 * 
	 * @param uid
	 *            员工代码
	 * @param manageUnit
	 *            所属机构
	 * @param type
	 *            票据类别 1:就诊号码 2：门诊号码 3：发票号码
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public static String getFPHM(String uid, String manageUnit, int n,
			BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("PJLX", 2);
		parameters.put("YGDM", uid);
		parameters.put("JGID", manageUnit);
		try {
			long ll_count = dao
					.doCount(
							"MS_YGPJ",
							"JGID = :JGID and YGDM = :YGDM and PJLX =:PJLX and SYPB = 0",
							parameters);
			if (ll_count > 0) {
				StringBuffer hql = new StringBuffer(
						"select SYHM as SYHM,JLXH as JLXH,ZZHM as ZZHM from MS_YGPJ where YGDM=:YGDM and JGID=:JGID and SYHM<=ZZHM and PJLX=:PJLX and SYPB = 0 order by LYRQ ");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("YGDM", uid);
				params.put("JGID", manageUnit);
				params.put("PJLX", 2);
				params.put("first", 0);
				params.put("max", 1);
				List<Map<String, Object>> ret = dao.doQuery(hql.toString(),
						params);
				if (ret.size() < 1)
					return null;
				Map<String, Object> record = ret.get(0);
				String syhm = MedicineUtils.parseString(record.get("SYHM")) ;
				String zzhm = MedicineUtils.parseString(record.get("ZZHM")) ;
				int k = -1;
				for (int i = syhm.length() - 1; i >= 0; i--) {
					if (syhm.charAt(i) < '0' || syhm.charAt(i) > '9') {
						k = i;
						break;
					}
				}
				String syhmzm = syhm.substring(0, k + 1);
				String syhmsz = syhm.substring(k + 1);
				String zzhmsz = zzhm.substring(k + 1);
				long intnewsyhm = Long.parseLong(syhmsz) + n - 1;
				long intzzhm = Long.parseLong(zzhmsz);
				String newsyhm = "";
				if (intzzhm < intnewsyhm) {
					if (ret.size() > 1) {
						Map<String, Object> record1 = ret.get(1);
						syhm = MedicineUtils.parseString(record1.get("SYHM"));
						k = -1;
						for (int i = syhm.length() - 1; i >= 0; i--) {
							if (syhm.charAt(i) < '0' || syhm.charAt(i) > '9') {
								k = i;
								break;
							}
						}
						syhmzm = syhm.substring(0, k + 1);
						syhmsz = syhm.substring(k + 1);
						// int length1 = syhm1.length();
						intnewsyhm = Long.parseLong(syhmsz) + intnewsyhm
								- intzzhm - 1;
						intzzhm = Long.parseLong(zzhmsz);
						if (intzzhm >= intnewsyhm) {
							newsyhm = syhmzm
									+ String.format("%0" + syhmsz.length()
											+ "d", intnewsyhm);
						} else {
							if (ret.size() > 2) {
								Map<String, Object> record2 = ret.get(2);
								syhm = MedicineUtils.parseString(record2.get("SYHM"));
								k = -1;
								for (int i = syhm.length() - 1; i >= 0; i--) {
									if (syhm.charAt(i) < '0'
											|| syhm.charAt(i) > '9') {
										k = i;
										break;
									}
								}
								syhmzm = syhm.substring(0, k + 1);
								syhmsz = syhm.substring(k + 1);
								// int length1 = syhm1.length();
								intnewsyhm = Long.parseLong(syhmsz)
										+ intnewsyhm - intzzhm - 1;
								intzzhm = Long.parseLong(zzhmsz);
								if (intzzhm >= intnewsyhm) {
									newsyhm = syhmzm
											+ String.format(
													"%0" + syhmsz.length()
															+ "d", intnewsyhm);
								} else {
									return null;
								}
							} else {
								return null;
							}
						}
					} else {
						return null;
					}
				} else {
					newsyhm = syhmzm
							+ String.format("%0" + syhmsz.length() + "d",
									intnewsyhm);
				}
				// record.put("SYHM", newsyhm);
				return newsyhm;
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			return null;
		}
	}

	/**
	 * 获取票据号码，票据号码不自增
	 * 
	 * @param uid
	 *            员工代码
	 * @param manageUnit
	 *            所属机构
	 * @param type
	 *            票据类别 1:就诊号码 2：发票号码 3：门诊号码
	 * @return票据类别 1:就诊号码 2：发票号码 3：门诊号码
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public static String getNotesNumberNotIncrement(String uid,
			String manageUnit, int type, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		parameters.put("PJLX", type);
		parameters.put("YGDM", uid);
		parameters.put("JGID", manageUnit);
		try {
			long ll_count = dao
					.doCount(
							"MS_YGPJ",
							"JGID = :JGID and YGDM = :YGDM and PJLX =:PJLX and SYPB = 0",
							parameters);
			if (ll_count > 0) {
				return getNotesNumberNotIncrement1(uid, manageUnit, type, dao);
			}
			String autoCreate = "0";
			if (3 == type) {
				autoCreate = ParameterUtil.getParameter(manageUnit,
						BSPHISSystemArgument.ZDCSMZH, ctx);
			} else if (1 == type) {
				autoCreate = ParameterUtil.getParameter(manageUnit,
						BSPHISSystemArgument.ZDCSJZH, ctx);
			}
			if ("1".equals(autoCreate)) {
				String topUnitId = ParameterUtil.getTopUnitId();
				int li_zdgh = 0;
				String ls_today = BSHISUtil.getDate();
				String ls_rq = ls_today.substring(2, 4)
						+ ls_today.substring(5, 7) + ls_today.substring(8, 10);
				String ls_zdmzh = "0";
				if (3 == type) {
					String ZDMZHXNGH = ParameterUtil.getParameter(topUnitId,
							BSPHISSystemArgument.ZDMZHXNGH, ctx);
					li_zdgh = Integer.parseInt(ZDMZHXNGH);
					if (li_zdgh == 9999) {
						li_zdgh = 1;
					} else {
						li_zdgh = Integer.parseInt(ZDMZHXNGH) + 1;
					}
					ls_zdmzh = ls_rq + String.format("%0" + 4 + "d", li_zdgh);
				} else if (1 == type) {
					String ZDMZHXNGH = ParameterUtil.getParameter(topUnitId,
							BSPHISSystemArgument.ZDJZHXNGH, ctx);
					li_zdgh = Integer.parseInt(ZDMZHXNGH);
					if (li_zdgh == 99) {
						li_zdgh = 1;
					} else {
						li_zdgh = Integer.parseInt(ZDMZHXNGH) + 1;
					}
					ls_zdmzh = ls_rq + String.format("%0" + 2 + "d", li_zdgh);
				}
				// 获取起始和终止号码
				String ls_qshm = ls_zdmzh + "001";
				String ls_zzhm = ls_zdmzh + "999";

				// 将生成的自动门诊号码写入员工票据中
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("JGID", manageUnit);
				data.put("YGDM", uid);
				data.put("LYRQ", new Date());
				data.put("PJLX", type);
				data.put("QSHM", ls_qshm);
				data.put("ZZHM", ls_zzhm);
				data.put("SYHM", ls_qshm);
				data.put("SYPB", 0);
				dao.doSave("create", BSPHISEntryNames.MS_YGPJ + "_MZ", data,
						false);
				parameters1.put("CSZ", "" + li_zdgh);
				if (3 == type) {
					dao.doUpdate(
							"UPDATE GY_XTCS Set CSZ = :CSZ Where CSMC = 'ZDMZHXNGH'",
							parameters1);
					ParameterUtil.reloadParams(topUnitId,
							BSPHISSystemArgument.ZDMZHXNGH);
				} else if (1 == type) {
					dao.doUpdate(
							"UPDATE GY_XTCS Set CSZ = :CSZ Where CSMC = 'ZDJZHXNGH'",
							parameters1);
					ParameterUtil.reloadParams(topUnitId,
							BSPHISSystemArgument.ZDJZHXNGH);
				}
				return ls_qshm;
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			return null;
		} catch (ValidateException e) {
			return null;
		}
	}

	/**
	 * 获取票据号码，票据号码不自增
	 * 
	 * @param uid
	 *            员工代码
	 * @param manageUnit
	 *            所属机构
	 * @param type
	 *            票据类别 1:就诊号码 2：门诊号码 3：发票号码
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public static String getNotesNumberNotIncrement1(String uid,
			String manageUnit, int type, BaseDAO dao)
			throws PersistentDataOperationException, ValidateException {
		StringBuffer hql = new StringBuffer(
				"select SYHM as SYHM,JLXH as JLXH from MS_YGPJ where YGDM=:YGDM and JGID=:JGID and SYHM<=ZZHM and PJLX=:PJLX and SYPB = 0 order by LYRQ ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("YGDM", uid);
		params.put("JGID", manageUnit);
		params.put("PJLX", type);
		params.put("first", 0);
		params.put("max", 1);
		List<Map<String, Object>> ret = dao.doQuery(hql.toString(), params);
		if (ret.size() < 1)
			return null;
		Map<String, Object> record = ret.get(0);
		return MedicineUtils.parseString(record.get("SYHM"));
	}

	/**
	 * 获取费用自负比例
	 * 
	 * @param body
	 * @return
	 */
	public static Map<String, Object> getPayProportion(
			Map<String, Object> body, Context ctx, BaseDAO dao)
			throws ModelDataOperationException {
		Integer al_yplx = Integer.parseInt(body.get("TYPE") + "");// 药品传药品类型1,2,3,费用传0
		Object al_brxz = body.get("BRXZ");// 病人性质
		Object al_fyxh = body.get("FYXH");// 费用序号
		Object al_fygb = body.get("FYGB");// 费用归并
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BRXZ", Long.parseLong(al_brxz.toString()));
		params.put("FYXH", Long.parseLong(al_fyxh.toString()));
		String hql = "";
		try {
			if (al_yplx == 0) {
				// 项目
				hql = "Select ZFBL as ZFBL,FYXE as FYXE,CXBL as CXBL From GY_FYJY Where BRXZ= :BRXZ And FYXH= :FYXH";
			} else {
				hql = "Select ZFBL as ZFBL From GY_YPJY Where BRXZ= :BRXZ And YPXH= :FYXH";
			}
			Map<String, Object> map = dao.doLoad(hql, params);
			if (map != null) {
				if (map.get("FYXE") == null || map.get("FYXE") == "") {
					map.put("FYXE", 0.0);
				}
				if (map.get("CXBL") == null || map.get("CXBL") == "") {
					map.put("CXBL", 0.0);
				}
				map.put("CXBL", (MedicineUtils.parseDouble(map.get("CXBL")) ) / 100);
				return map;
			}
			params.clear();
			params.put("BRXZ", Long.parseLong(al_brxz.toString()));
			params.put("FYGB", Long.parseLong(al_fygb.toString()));
			hql = "Select ZFBL as ZFBL From GY_ZFBL Where BRXZ= :BRXZ And SFXM= :FYGB";
			Map<String, Object> zfbl_map = dao.doLoad(hql, params);
			if (zfbl_map == null) {
				zfbl_map = new HashMap<String, Object>();
				zfbl_map.put("ZFBL", 1);
			}
			return zfbl_map;

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取自负比例失败!", e);
		} catch (Exception e) {
			throw new ModelDataOperationException("获取自负比例失败!", e);
		}
	}

	/**
	 * 
	 * @param amount
	 * @return
	 */
	public static String changeMoneyUpper(Object amount) {
		if (amount == null || amount.toString().length() == 0)
			return null;
		String sNumber = amount.toString();
		String[] oneUnit = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾",
				"佰", "仟", "兆", "拾", "佰", "仟" };
		String[] twoUnit = { "分", "角" };
		String[] sChinese = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String sign = ""; // 符号位 add by Zhangxw
		if (sNumber.startsWith("-")) {
			sNumber = sNumber.substring(1);
			sign = "负";
		}
		int pointPos = sNumber.indexOf("."); // 小数点的位置
		String sInteger;// 记录整数部分
		String sDecimal;// 记录小数部分
		String value = "";// 记录返回结果
		if (pointPos != -1) {
			// 分解并记录整数部分，注意substring()的用法
			sInteger = sNumber.substring(0, pointPos); // 分解并记录小数部分
			sDecimal = sNumber.substring(pointPos + 1, pointPos + 3 < sNumber
					.length() ? pointPos + 3 : sNumber.length());
		} else { // 没有小数部分的情况
			sInteger = sNumber;
			sDecimal = "";
		} // 格式化整数部分，并记录到返回结果
		for (int i = 0; i < sInteger.length(); i++) {
			int temp = Integer.parseInt(sInteger.substring(i, i + 1));
			value += sChinese[temp] + oneUnit[sInteger.length() - i - 1];
		} // 格式化小数部分，并记录到返回结果
		for (int i = 0; i < sDecimal.length(); i++) {
			int temp = Integer.parseInt(sDecimal.substring(i, i + 1));
			// value += sChinese[temp] + twoUnit[sDecimal.length() - i - 1];
			value += sChinese[temp] + twoUnit[2 - i - 1];
		}
		return sign + value;
	}

	/**
	 * 获取MZLB的方法
	 * 
	 * @param manaUnitId
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static long getMZLB(String manaUnitId, BaseDAO dao)
			throws ModelDataOperationException {
		if (mzlbCache.containsKey(manaUnitId)) {
			return mzlbCache.get(manaUnitId);
		}
		Map<String, Object> MS_MZLB = new HashMap<String, Object>();
		MS_MZLB.put("JGID", manaUnitId);
		MS_MZLB.put("MZMC", "门诊");
		List<Map<String, Object>> MZLB_List;
		Long mzlb = 0l;
		try {
			MZLB_List = dao
					.doQuery(
							"select MZLB as MZLB from MS_MZLB where JGID=:JGID and MZMC=:MZMC",
							MS_MZLB);
			if (MZLB_List.size() > 0) {
				mzlb = Long.parseLong(MZLB_List.get(0).get("MZLB") + "");// 门诊类别
			} else {
				Map<String, Object> MZLB = dao.doSave("create",
						BSPHISEntryNames.MS_MZLB, MS_MZLB, false);
				mzlb = Long.parseLong(MZLB.get("MZLB").toString() + "");
			}
			mzlbCache.put(manaUnitId, mzlb);
			return mzlb;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取门诊类别失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("获取门诊类别失败!", e);
		}
	}

	/**
	 * tochar函数转化
	 * 
	 * @param prop
	 * @param format
	 * @return
	 */
	public static String toChar(String prop, String format) {
		// 由于使用的是sql查询，无法使用hql中的str自动转化，增加判断收工更改，只适用oracle与DB2
		String tochar = "";
		// WebApplicationContext wac = AppContextHolder.get();
		// SessionFactory sf = (SessionFactory) wac.getBean("mySessionFactory");
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String dialectName = ((SessionFactoryImpl) sf).getDialect().getClass()
				.getName();
		if (dialectName.contains("MyDB2")) {
			tochar = "char(" + prop + ")";
		} else {
			tochar += "to_char(" + prop + ",'" + format + "')";
		}
		return tochar;
	}

	/**
	 * tochar函数转化
	 * 
	 * @param prop
	 * @param format
	 * @return
	 */
	public static String toDate(String value, String format) {
		// 由于使用的是sql查询，无法使用hql中的str自动转化，增加判断收工更改，只适用oracle与DB2
		String todate = "";
		// WebApplicationContext wac = AppContextHolder.get();
		// SessionFactory sf = (SessionFactory) wac.getBean("mySessionFactory");
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String dialectName = ((SessionFactoryImpl) sf).getDialect().getClass()
				.getName();
		if (dialectName.contains("MyDB2")) {
			todate = "date(" + value + ")";
		} else {
			todate += "to_date('" + value + "','" + format + "')";
		}
		return todate;
	}

	/**
	 * 性质转换
	 * 
	 * @param oldBrxz
	 * @param newBrxz
	 * @param list_ZY_FYMX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static List<Map<String, Object>> change(long oldBrxz, long newBrxz,
			List<Map<String, Object>> list_ZY_FYMX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		for (int i = 0; i < list_ZY_FYMX.size(); i++) {
			Map<String, Object> ZY_FYMX = list_ZY_FYMX.get(i);
			int YPLX = (Integer) ZY_FYMX.get("YPLX");
			long FYXH = Long.parseLong(ZY_FYMX.get("FYXH") + "");
			long FYGB = Long.parseLong(ZY_FYMX.get("FYXM") + "");
			double FYDJ = Double.parseDouble(ZY_FYMX.get("FYDJ") + "");
			double FYSL = Double.parseDouble(ZY_FYMX.get("FYSL") + "");
			Map<String, Object> je = getje(YPLX, newBrxz, FYXH, FYGB, FYDJ,
					FYSL, dao, ctx);
			ZY_FYMX.put("ZFBL", je.get("ZFBL"));
			ZY_FYMX.put("ZFJE", je.get("ZFJE"));
			ZY_FYMX.put("ZLJE", je.get("ZLJE"));
			ZY_FYMX.put("ZJJE", je.get("ZJJE"));
			list_ZY_FYMX.set(i, ZY_FYMX);
		}
		return list_ZY_FYMX;
	}

	/**
	 * 获取金额
	 * 
	 * @param al_yplx
	 * @param al_brxz
	 * @param al_fyxh
	 * @param al_fygb
	 * @param ad_fydj
	 * @param ad_fysl
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> getje(int al_yplx, long al_brxz,
			long al_fyxh, long al_fygb, double ad_fydj, double ad_fysl,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		double ld_zfbl = 1;
		boolean adc_zfbl = false;
		double ld_cxbl = 1;
		double ld_zfje = 0;
		double ld_zlje = 0;
		double ld_fyxe = 0;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BRXZ", al_brxz);

		try {
			if (al_yplx == 0) {
				parameters.put("FYXH", al_fyxh);
				Map<String, Object> GY_FYJY = dao
						.doLoad("Select ZFBL as ZFBL,FYXE as FYXE,CXBL as CXBL From GY_FYJY Where BRXZ= :BRXZ And FYXH= :FYXH",
								parameters);
				if (GY_FYJY != null) {
					if (GY_FYJY.get("FYXE") != null) {
						ld_fyxe = Double.parseDouble(GY_FYJY.get("FYXE") + "");
					}
					if (GY_FYJY.get("CXBL") != null) {
						ld_cxbl = Double.parseDouble(GY_FYJY.get("CXBL") + "");
					}
					if (GY_FYJY.get("ZFBL") != null) {
						ld_zfbl = Double.parseDouble(GY_FYJY.get("ZFBL") + "");
						adc_zfbl = true;
					}
				}
				if (ld_fyxe > 0 && ad_fydj > ld_fyxe) {
					ld_zfje = ad_fysl * ld_fyxe * ld_zfbl;
					ld_zlje = ad_fysl * (ad_fydj - ld_fyxe) * ld_cxbl;
					ld_zfje = ld_zfje + ld_zlje;
				} else {
					ld_zfje = ad_fysl * ad_fydj * ld_zfbl;
					ld_zlje = 0;
				}
			} else if (al_yplx == 1 || al_yplx == 2 || al_yplx == 3) {
				parameters.put("YPXH", al_fyxh);
				Map<String, Object> GY_YPJY = dao
						.doLoad("Select ZFBL as ZFBL From GY_YPJY Where BRXZ= :BRXZ And YPXH= :YPXH",
								parameters);
				if (GY_YPJY != null) {
					if (GY_YPJY.get("ZFBL") != null) {
						ld_zfbl = Double.parseDouble(GY_YPJY.get("ZFBL") + "");
						adc_zfbl = true;
					}
				}
				ld_zfje = ad_fysl * ad_fydj * ld_zfbl;
				ld_zlje = 0;
			}
			if (!adc_zfbl) {
				al_fygb = getfygb(al_yplx, al_fyxh, dao, ctx);
				parameters.remove("YPXH");
				parameters.remove("FYXH");
				parameters.put("SFXM", al_fygb);
				Map<String, Object> GY_ZFBL = dao
						.doLoad("Select ZFBL as ZFBL From GY_ZFBL Where BRXZ= :BRXZ And SFXM= :SFXM",
								parameters);
				if (GY_ZFBL != null) {
					if (GY_ZFBL.get("ZFBL") != null) {
						ld_zfbl = Double.parseDouble(GY_ZFBL.get("ZFBL") + "");
					}
				}
				ld_zfje = ad_fysl * ad_fydj * ld_zfbl;
				ld_zlje = 0;
			}
			double ld_zjje = ad_fydj * ad_fysl;
			Map<String, Object> Getje = new HashMap<String, Object>();
			Getje.put("ZFBL", ld_zfbl);
			Getje.put("ZFJE", ld_zfje);
			Getje.put("ZLJE", ld_zlje);
			Getje.put("ZJJE", ld_zjje);
			Getje.put("FYGB", al_fygb);
			return Getje;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "金额信息查询失败");
		}
	}

	/**
	 * 功能:根据传入的药品数量判断库存是否满足 update by caijy for 还要根据零售价格判断
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> checkInventory(Map<String, Object> body,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Object pharmId = body.get("pharmId");// 药房编号
		Object medId = body.get("medId");// 药品编号
		Object medsource = body.get("medsource");// 药品产地
		Object quantity = body.get("quantity");// 药品总量
		Object lsjg = body.get("lsjg");// 药品总量
		String hql = "SELECT SUM(YPSL) as KCZL FROM YF_KCMX WHERE JGID=:JGID AND YFSB=:YFSB AND YPXH=:YPXH AND YPCD=:YPCD AND LSJG=:LSJG AND JYBZ = 0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JGID",jgid);
		params.put("YFSB", Long.parseLong(pharmId.toString()));
		params.put("YPXH", Long.parseLong(medId.toString()));
		params.put("YPCD", Long.parseLong(medsource.toString()));
		params.put("LSJG", Double.parseDouble(lsjg.toString()));
		try {
			Map<String, Object> map = dao.doLoad(hql, params);
			if (map == null) {
				map = new HashMap<String, Object>();
				map.put("sign", -1);
				map.put("KCZL", 0);
				return map;
			}
			Double kczl = MedicineUtils.parseDouble(map.get("KCZL")) ;
			// 库存冻结代码
//			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			double KCDJTS= MedicineUtils.parseDouble(ParameterUtil
					.getParameter(UserRoleToken.getCurrent().getManageUnit().getId(), BSPHISSystemArgument.KCDJTS, ctx));
			if (SFQYYFYFY == 1) {
//				Session ss = (Session) ctx.get(Context.DB_SESSION);
//				ss.beginTransaction();
//				MedicineCommonModel model = new MedicineCommonModel(
//						new BaseDAO(ctx));
//				model.deleteKCDJ(manaUnitId, ctx);
//				ss.getTransaction().commit();
				long jlxh = 0;
				if (body.containsKey("jlxh")) {
					jlxh = MedicineUtils.parseLong(body.get("jlxh"));
				}
				StringBuffer hql_djsl = new StringBuffer();
				hql_djsl.append("select sum(a.YPSL) as DJSL from YF_KCDJ a,MS_CF02 b where b.JGID=:JGID and a.YFSB=:YFSB and a.YPXH=:YPXH and a.YPCD=:YPCD and a.JLXH=b.SBXH and b.YPDJ=:LSJG and a.JLXH!=:jlxh and sysdate-DJSJ <=:kcdjts");
				params.put("jlxh", jlxh);
				params.put("kcdjts", KCDJTS);
				List<Map<String, Object>> list_djsl = dao.doSqlQuery(
						hql_djsl.toString(), params);
				// Map<String, Object> map_djsl=dao.doLoad(hql_djsl.toString(),
				// params);
				if (list_djsl != null && list_djsl.size() > 0
						&& list_djsl.get(0).get("DJSL") != null
						&& !("null").equals(list_djsl.get(0).get("DJSL"))) {
					kczl -= MedicineUtils.parseDouble(list_djsl.get(0).get(
							"DJSL"));
				}
			}
			/************add by lizhi 需减去已结算但未发药的药品数量*******/
			if (!body.containsKey("isYscf")) {
				params.clear();
				params.put("JGID",jgid);
				params.put("YPXH", Long.parseLong(medId.toString()));
				params.put("YPCD", Long.parseLong(medsource.toString()));
				params.put("YFSB", Long.parseLong(pharmId.toString()));
				String wfyHql = "select (case b.XMLX when 3 then sum(b.YPSL*b.CFTS) else sum(b.YPSL) end) as wfysl from MS_CF01 a,MS_CF02 b" +
						" where a.JGID = b.JGID and a.CFSB = b.CFSB and a.FPHM is not null and a.FYBZ = 0 and a.ZFPB = 0" +
						" and a.JGID=:JGID and b.YPXH=:YPXH and b.YPCD=:YPCD and a.YFSB=:YFSB group by b.XMLX";
				List<Map<String, Object>> list_wfysl = dao.doSqlQuery(
						wfyHql.toString(), params);
				if (list_wfysl != null && list_wfysl.size() > 0
						&& list_wfysl.get(0).get("WFYSL") != null
						&& !("null").equals(list_wfysl.get(0).get("WFYSL"))) {
					kczl -= MedicineUtils.parseDouble(list_wfysl.get(0).get(
							"WFYSL"));
				}
			}
			/************add by lizhi 需减去已结算但未发药的药品数量*******/
			// 库存冻结代码结束
			if (kczl == null || kczl < Double.parseDouble(quantity.toString())) {
				if (kczl == null) {
					map.put("KCZL", 0);
				} else {
					DecimalFormat df = new DecimalFormat("#.00");
					map.put("KCZL", df.format(kczl));
				}
				map.put("sign", -1);
			} else {
				map.put("sign", 1);
			}
			return map;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("库存查询失败!", e);
		}
	}

	/**
	 * 获取费用归并
	 * 
	 * @param al_yplx
	 * @param al_fyxh
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static long getfygb(long al_yplx, long al_fyxh, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		long al_fygb = 0;
		String topUnitId = ParameterUtil.getTopUnitId();
		if (al_yplx == 1 || al_yplx == 2 || al_yplx == 3) {// 如果不是费用,先查询有吴账簿类别,没有账簿类别则按药品类型分
			StringBuffer hql_zblb = new StringBuffer();
			hql_zblb.append("select ZBLB as ZBLB from YK_TYPK where YPXH=:ypxh");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ypxh", al_fyxh);
			try {
				Map<String, Object> map_zblb = dao.doLoad(hql_zblb.toString(),
						map_par);
				if (map_zblb != null && map_zblb.size() != 0
						&& map_zblb.get("ZBLB") != null
						&& Long.parseLong(map_zblb.get("ZBLB") + "") != 0) {
					al_fygb = Long.parseLong(map_zblb.get("ZBLB") + "");
				} else {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "药品账簿类别查询失败");
				}
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "药品账簿类别查询失败");
			}
		} else {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FYXH", al_fyxh);
			try {
				Map<String, Object> GY_YLSF = dao.doLoad(
						"SELECT FYGB as FYGB FROM GY_YLSF WHERE FYXH=:FYXH",
						parameters);
				al_fygb = Long.parseLong(GY_YLSF.get("FYGB") + "");
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "费用归并查询失败");
			}
		}
		return al_fygb;
	}

	/**
	 * double四舍五入保留小数
	 * 
	 * @param d
	 * @param i
	 * @return
	 */
	public static double getDouble(Object d, int i) {
		if (d == null) {
			d = 0d;
		}
		String rStr = String.format("%." + i + "f", Double.parseDouble(d + ""));
		double rd = Double.parseDouble(rStr);
		return rd;
	}

	/**
	 * add by zhangyq 获取合计金额
	 * 
	 * @param BRXX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_zy_gxmk_getjkhj(
			Map<String, Object> BRXX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("JKHJ", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(
					"ZYH",
					Long.parseLong(BRXX.get("ZYH") == null ? "0" : BRXX.get(
							"ZYH").toString()));
			if (Integer.parseInt(BRXX.get("JSLX") + "") == 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 5) {
				Map<String, Object> ZY_TBKK = dao
						.doLoad("SELECT sum(JKJE) as JKHJ FROM ZY_TBKK WHERE ZYH  = :ZYH AND ZFPB = 0 AND JSCS = 0",
								parameters);
				if (ZY_TBKK.get("JKHJ") != null) {
					BRXX.put("JKHJ", ZY_TBKK.get("JKHJ"));
				}
			} else if (Integer.parseInt(BRXX.get("JSLX") + "") < 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 10) {
				parameters.put("JSCS", BRXX.get("JSCS"));
				Map<String, Object> ZY_ZYJS = dao
						.doLoad("SELECT sum(JKHJ) as JKHJ FROM ZY_ZYJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
								parameters);
				if (ZY_ZYJS.get("JKHJ") != null) {
					BRXX.put("JKHJ", ZY_ZYJS.get("JKHJ"));
				}
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取合计金额失败");
		}
	}
	/**
	 * add by zhaojian 2019-06-18 获取合计金额
	 * 
	 * @param BRXX
	 * @param JSXZ
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_zy_gxmk_getjkhj(
			Map<String, Object> BRXX, int JSXZ, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("JKHJ", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(
					"ZYH",
					Long.parseLong(BRXX.get("ZYH") == null ? "0" : BRXX.get(
							"ZYH").toString()));
			if (Integer.parseInt(BRXX.get("JSLX") + "") == 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 5) {
				Map<String, Object> ZY_TBKK = dao
						.doLoad("SELECT sum(JKJE) as JKHJ FROM ZY_TBKK WHERE ZYH  = :ZYH AND ZFPB = 0 AND JSCS = 0",
								parameters);
				if (ZY_TBKK.get("JKHJ") != null) {
					BRXX.put("JKHJ", ZY_TBKK.get("JKHJ"));
				}
			} else if (Integer.parseInt(BRXX.get("JSLX") + "") < 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 10) {
				parameters.put("JSCS", BRXX.get("JSCS"));
				//parameters.put("JSXZ", JSXZ);
				Map<String, Object> ZY_ZYJS = dao
						.doLoad("SELECT sum(JKHJ) as JKHJ FROM ZY_ZYJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
								parameters);
				if (ZY_ZYJS.get("JKHJ") != null) {
					BRXX.put("JKHJ", ZY_ZYJS.get("JKHJ"));
				}
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取合计金额失败");
		}
	}
	/**
	 * 获取合计金额
	 * 
	 * @param BRXX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_jc_gxmk_getjkhj(
			Map<String, Object> BRXX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("JKHJ", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(
					"ZYH",
					Long.parseLong(BRXX.get("ZYH") == null ? "0" : BRXX.get(
							"ZYH").toString()));
			if (Integer.parseInt(BRXX.get("JSLX") + "") == 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 5) {
				Map<String, Object> JC_TBKK = dao
						.doLoad("SELECT sum(JKJE) as JKHJ FROM JC_TBKK WHERE ZYH  = :ZYH AND ZFPB = 0 AND JSCS = 0",
								parameters);
				if (JC_TBKK.get("JKHJ") != null) {
					BRXX.put("JKHJ", JC_TBKK.get("JKHJ"));
				}
			} else if (Integer.parseInt(BRXX.get("JSLX") + "") < 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 10) {
				parameters.put("JSCS", BRXX.get("JSCS"));
				Map<String, Object> JC_JCJS = dao
						.doLoad("SELECT sum(JKHJ) as JKHJ FROM JC_JCJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
								parameters);
				if (JC_JCJS.get("JKHJ") != null) {
					BRXX.put("JKHJ", JC_JCJS.get("JKHJ"));
				}
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取合计金额失败");
		}
	}

	/**
	 * add by zhangyq 获取自负金额
	 * 
	 * @param BRXX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_zy_gxmk_getzfhj(
			Map<String, Object> BRXX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("ZFHJ", 0);
		BRXX.put("YL_ZFHJ", 0);
		BRXX.put("YP_ZFHJ", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(
					"ZYH",
					Long.parseLong(BRXX.get("ZYH") == null ? "0" : BRXX.get(
							"ZYH").toString()));
			if (Integer.parseInt(BRXX.get("JSLX") + "") == 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 5) {
				Map<String, Object> ZY_FYMX = dao
						.doLoad("SELECT nvl(sum(ZFJE),0) as ZFHJ FROM ZY_FYMX WHERE ZYH=:ZYH AND JSCS=0",
								parameters);
				if (ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("ZFHJ", ZY_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YL_ZY_FYMX = dao
						.doLoad("SELECT nvl(sum(ZFJE),0) as ZFHJ FROM ZY_FYMX WHERE ZYH  = :ZYH AND JSCS = 0 and YPLX = 0",
								parameters);
				if (YL_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YL_ZFHJ", YL_ZY_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YP_ZY_FYMX = dao
						.doLoad("SELECT nvl(sum(ZFJE),0) as ZFHJ FROM ZY_FYMX WHERE ZYH  = :ZYH AND JSCS = 0 and YPLX <> 0",
								parameters);
				if (YP_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YP_ZFHJ", YP_ZY_FYMX.get("ZFHJ"));
				}
			} else if (Integer.parseInt(BRXX.get("JSLX") + "") < 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 10) {
				parameters.put("JSCS", BRXX.get("JSCS"));
				Map<String, Object> ZY_ZYJS = dao
						.doLoad("SELECT sum(ZFHJ) as ZFHJ FROM ZY_ZYJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
								parameters);
				if (ZY_ZYJS.get("ZFHJ") != null) {
					BRXX.put("ZFHJ", ZY_ZYJS.get("ZFHJ"));
				}
				Map<String, Object> YL_ZY_FYMX = dao
						.doLoad("SELECT sum(ZFJE) as ZFHJ FROM ZY_FYMX WHERE ZYH  = :ZYH AND JSCS = :JSCS and YPLX = 0",
								parameters);
				if (YL_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YL_ZFHJ", YL_ZY_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YP_ZY_FYMX = dao
						.doLoad("SELECT sum(ZFJE) as ZFHJ FROM ZY_FYMX WHERE ZYH  = :ZYH AND JSCS = :JSCS and YPLX <> 0",
								parameters);
				if (YP_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YP_ZFHJ", YP_ZY_FYMX.get("ZFHJ"));
				}
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取自负金额失败");
		}
	}

	/**
	 * add by zhaojian 2019-06-18 获取自负金额
	 * 
	 * @param BRXX
	 * @param JSXZ
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_zy_gxmk_getzfhj(
			Map<String, Object> BRXX, int JSXZ, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		BRXX.put("ZFHJ", 0);
		BRXX.put("YL_ZFHJ", 0);
		BRXX.put("YP_ZFHJ", 0);
		String sql_jsxz = "";
		if(JSXZ==1000){
			sql_jsxz = " and b.YYZBM is null ";
		}else{
			sql_jsxz = " and b.YYZBM is not null ";
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(
					"ZYH",
					Long.parseLong(BRXX.get("ZYH") == null ? "0" : BRXX.get(
							"ZYH").toString()));
			if (Integer.parseInt(BRXX.get("JSLX") + "") == 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 5) {
				Map<String, Object> ZY_FYMX = dao
						.doSqlLoad("SELECT sum(a.ZFHJ) as ZFHJ from (SELECT nvl(sum(a.ZFJE),0) as ZFHJ FROM ZY_FYMX a,GY_YLMX b WHERE ZYH  = :ZYH and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.FYXH AND a.JSCS = 0 and a.YPLX = 0"+sql_jsxz+
								" UNION SELECT nvl(sum(a.ZFJE),0) as ZFHJ FROM ZY_FYMX a,YK_CDXX b WHERE a.ZYH  = :ZYH and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.YPXH and a.YPCD=b.YPCD AND a.JSCS = 0 and a.YPLX <> 0"+sql_jsxz+") a",
								parameters);
				if (ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("ZFHJ", ZY_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YL_ZY_FYMX = dao
						.doSqlLoad("SELECT nvl(sum(a.ZFJE),0) as ZFHJ FROM ZY_FYMX a,GY_YLMX b WHERE ZYH  = :ZYH and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.FYXH AND a.JSCS = 0 and a.YPLX = 0"+sql_jsxz,
								parameters);
				if (YL_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YL_ZFHJ", YL_ZY_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YP_ZY_FYMX = dao
						.doSqlLoad("SELECT nvl(sum(a.ZFJE),0) as ZFHJ FROM ZY_FYMX a,YK_CDXX b WHERE a.ZYH  = :ZYH and "+(JGID.equals("320124005016")?"substr(a.jgid,0,9)=b.jgid":"a.jgid = b.jgid")+" and a.FYXH=b.YPXH and a.YPCD=b.YPCD AND a.JSCS = 0 and a.YPLX <> 0"+sql_jsxz,
								parameters);
				if (YP_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YP_ZFHJ", YP_ZY_FYMX.get("ZFHJ"));
				}
			} else if (Integer.parseInt(BRXX.get("JSLX") + "") < 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 10) {
				parameters.put("JSCS", BRXX.get("JSCS"));
				parameters.put("JSXZ", JSXZ);
				parameters.put("FPHM", BRXX.get("FPHM"));
				Map<String, Object> ZY_ZYJS = dao
						.doLoad("SELECT sum(ZFHJ) as ZFHJ FROM ZY_ZYJS WHERE ZYH  = :ZYH AND JSCS = :JSCS AND JSXZ=:JSXZ AND FPHM=:FPHM",
								parameters);
				if (ZY_ZYJS.get("ZFHJ") != null) {
					BRXX.put("ZFHJ", ZY_ZYJS.get("ZFHJ"));
				}
				Map<String, Object> YL_ZY_FYMX = dao
						.doLoad("SELECT sum(ZFJE) as ZFHJ FROM ZY_FYMX_JS WHERE ZYH  = :ZYH AND JSCS = 0 and YPLX = 0 AND JSXZ=:JSXZ AND FPHM=:FPHM",
								parameters);
				if (YL_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YL_ZFHJ", YL_ZY_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YP_ZY_FYMX = dao
						.doLoad("SELECT sum(ZFJE) as ZFHJ FROM ZY_FYMX_JS WHERE ZYH  = :ZYH AND JSCS = 0 and YPLX <> 0 AND JSXZ=:JSXZ AND FPHM=:FPHM",
								parameters);
				if (YP_ZY_FYMX.get("ZFHJ") != null) {
					BRXX.put("YP_ZFHJ", YP_ZY_FYMX.get("ZFHJ"));
				}
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取自负金额失败");
		}
	}
	/**
	 * 
	 * @param BRXX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_jc_gxmk_getzfhj(
			Map<String, Object> BRXX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("ZFHJ", 0);
		BRXX.put("YL_ZFHJ", 0);
		BRXX.put("YP_ZFHJ", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(
					"ZYH",
					Long.parseLong(BRXX.get("ZYH") == null ? "0" : BRXX.get(
							"ZYH").toString()));
			if (Integer.parseInt(BRXX.get("JSLX") + "") == 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 5) {
				Map<String, Object> JC_FYMX = dao
						.doLoad("SELECT nvl(sum(ZFJE),0) as ZFHJ FROM JC_FYMX WHERE ZYH=:ZYH AND JSCS=0",
								parameters);
				if (JC_FYMX.get("ZFHJ") != null) {
					BRXX.put("ZFHJ", JC_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YL_JC_FYMX = dao
						.doLoad("SELECT nvl(sum(ZFJE),0) as ZFHJ FROM JC_FYMX WHERE ZYH  = :ZYH AND JSCS = 0 and YPLX = 0",
								parameters);
				if (YL_JC_FYMX.get("ZFHJ") != null) {
					BRXX.put("YL_ZFHJ", YL_JC_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YP_JC_FYMX = dao
						.doLoad("SELECT nvl(sum(ZFJE),0) as ZFHJ FROM JC_FYMX WHERE ZYH  = :ZYH AND JSCS = 0 and YPLX <> 0",
								parameters);
				if (YP_JC_FYMX.get("ZFHJ") != null) {
					BRXX.put("YP_ZFHJ", YP_JC_FYMX.get("ZFHJ"));
				}
			} else if (Integer.parseInt(BRXX.get("JSLX") + "") < 0
					|| Integer.parseInt(BRXX.get("JSLX") + "") == 10) {
				parameters.put("JSCS", BRXX.get("JSCS"));
				Map<String, Object> JC_JCJS = dao
						.doLoad("SELECT sum(ZFHJ) as ZFHJ FROM JC_JCJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
								parameters);
				if (JC_JCJS.get("ZFHJ") != null) {
					BRXX.put("ZFHJ", JC_JCJS.get("ZFHJ"));
				}
				Map<String, Object> YL_JC_FYMX = dao
						.doLoad("SELECT sum(ZFJE) as ZFHJ FROM JC_FYMX WHERE ZYH  = :ZYH AND JSCS = :JSCS and YPLX = 0",
								parameters);
				if (YL_JC_FYMX.get("ZFHJ") != null) {
					BRXX.put("YL_ZFHJ", YL_JC_FYMX.get("ZFHJ"));
				}
				Map<String, Object> YP_JC_FYMX = dao
						.doLoad("SELECT sum(ZFJE) as ZFHJ FROM JC_FYMX WHERE ZYH  = :ZYH AND JSCS = :JSCS and YPLX <> 0",
								parameters);
				if (YP_JC_FYMX.get("ZFHJ") != null) {
					BRXX.put("YP_ZFHJ", YP_JC_FYMX.get("ZFHJ"));
				}
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取自负金额失败");
		}
	}
	/**
	 * add by zhangyq 获取结算金额
	 * 
	 * @param BRXX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_zy_gxmk_getjsje(
			Map<String, Object> BRXX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("FYHJ", 0);
		BRXX.put("ZFHJ", 0);
		BRXX.put("JKHJ", 0);
		BRXX.put("JSJE", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", BRXX.get("ZYH"));
			parameters.put("JSCS", BRXX.get("JSCS"));
			Map<String, Object> ZY_ZYJS = dao
					.doLoad("SELECT sum(FYHJ) as FYHJ,sum(ZFHJ) as ZFHJ,sum(JKHJ) as JKHJ,sum(ZFHJ - JKHJ) as JSJE FROM ZY_ZYJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
							parameters);
			if (ZY_ZYJS.get("FYHJ") != null) {
				BRXX.put("FYHJ", ZY_ZYJS.get("FYHJ"));
			}
			if (ZY_ZYJS.get("ZFHJ") != null) {
				BRXX.put("ZFHJ", ZY_ZYJS.get("ZFHJ"));
			}
			if (ZY_ZYJS.get("JKHJ") != null) {
				BRXX.put("JKHJ", ZY_ZYJS.get("JKHJ"));
			}
			if (ZY_ZYJS.get("JSJE") != null) {
				BRXX.put("JSJE", ZY_ZYJS.get("JSJE"));
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取结算金额失败");
		}
	}
	/**
	 * add by zhaojian 2019-06-18 获取结算金额
	 * 
	 * @param BRXX
	 * @param JSXZ
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_zy_gxmk_getjsje(
			Map<String, Object> BRXX, int JSXZ, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("FYHJ", 0);
		BRXX.put("ZFHJ", 0);
		BRXX.put("JKHJ", 0);
		BRXX.put("JSJE", 0);
		try {
			String sql_zy_zyjs = "SELECT sum(FYHJ) as FYHJ,sum(ZFHJ) as ZFHJ,sum(JKHJ) as JKHJ,sum(ZFHJ - JKHJ) as JSJE FROM ZY_ZYJS WHERE ZFPB=0 AND ZYH  = :ZYH AND JSXZ = :JSXZ";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", BRXX.get("ZYH"));
			parameters.put("JSXZ", JSXZ);
			if (BRXX.get("FPHM")!=null) {
				parameters.put("FPHM", BRXX.get("FPHM"));
				sql_zy_zyjs += " AND FPHM=:FPHM";
			}
			Map<String, Object> ZY_ZYJS = dao
					.doLoad(sql_zy_zyjs,
							parameters);
			if (ZY_ZYJS.get("FYHJ") != null) {
				BRXX.put("FYHJ", ZY_ZYJS.get("FYHJ"));
			}
			if (ZY_ZYJS.get("ZFHJ") != null) {
				BRXX.put("ZFHJ", ZY_ZYJS.get("ZFHJ"));
			}
			if (ZY_ZYJS.get("JKHJ") != null) {
				BRXX.put("JKHJ", ZY_ZYJS.get("JKHJ"));
			}
			if (ZY_ZYJS.get("JSJE") != null) {
				BRXX.put("JSJE", ZY_ZYJS.get("JSJE"));
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取结算金额失败");
		}
	}
	
	/**
	 * add by zhangyq 获取结算金额
	 * 
	 * @param BRXX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> gf_jc_gxmk_getjsje(
			Map<String, Object> BRXX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		BRXX.put("FYHJ", 0);
		BRXX.put("ZFHJ", 0);
		BRXX.put("JKHJ", 0);
		BRXX.put("JSJE", 0);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", BRXX.get("ZYH"));
			parameters.put("JSCS", BRXX.get("JSCS"));
			Map<String, Object> JC_JCJS = dao
					.doLoad("SELECT sum(FYHJ) as FYHJ,sum(ZFHJ) as ZFHJ,sum(JKHJ) as JKHJ,sum(ZFHJ - JKHJ) as JSJE FROM JC_JCJS WHERE ZYH  = :ZYH AND JSCS = :JSCS",
							parameters);
			if (JC_JCJS.get("FYHJ") != null) {
				BRXX.put("FYHJ", JC_JCJS.get("FYHJ"));
			}
			if (JC_JCJS.get("ZFHJ") != null) {
				BRXX.put("ZFHJ", JC_JCJS.get("ZFHJ"));
			}
			if (JC_JCJS.get("JKHJ") != null) {
				BRXX.put("JKHJ", JC_JCJS.get("JKHJ"));
			}
			if (JC_JCJS.get("JSJE") != null) {
				BRXX.put("JSJE", JC_JCJS.get("JSJE"));
			}
			return BRXX;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取结算金额失败");
		}
	}

	/**
	 * 床位分配 parameters里需要传入参数 adt_date 分配床位日期, al_zyh 住院号 ,as_cwhm 床位号码
	 */
	public static void cwgl_cwfp(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String uid = user.getUserId();
		Long ll_brks = 0L;// 病人科室
		Long ll_cwks = 0L;// 床位科室
		Long ll_cwbq = 0L;// 床位病区
		int ll_jcpb = 0;// 加床判别
		int zyh = 0;
		try {
			// 取病人及床位信息
			Map<String, Object> parametersbrryinfo = new HashMap<String, Object>();
			parametersbrryinfo.put("ZYH",
					Long.parseLong(parameters.get("ZYH") + ""));
			parametersbrryinfo.put("JGID", manaUnitId);
			Map<String, Object> brryinfomap = dao
					.doLoad("SELECT BRXB as BRXB,BRKS as BRKS,RYRQ as RYRQ FROM ZY_BRRY Where ZYH  = :ZYH  and JGID = :JGID",
							parametersbrryinfo);
			Map<String, Object> parameterscwszinfo = new HashMap<String, Object>();
			parameterscwszinfo.put("BRCH", parameters.get("BRCH"));
			parameterscwszinfo.put("JGID", manaUnitId);
			Map<String, Object> cwszinfomap = dao
					.doLoad("SELECT CWXB as CWXB,CWKS as CWKS,KSDM as KSDM,JCPB as JCPB FROM ZY_CWSZ Where BRCH = :BRCH and JGID = :JGID",
							parameterscwszinfo);
			if (brryinfomap != null && brryinfomap.size() > 0) {
				ll_brks = Long.parseLong(brryinfomap.get("BRKS") + "");
			}
			if (cwszinfomap != null && cwszinfomap.size() > 0) {
				ll_cwks = Long.parseLong(cwszinfomap.get("CWKS") + "");
				ll_cwbq = Long.parseLong(cwszinfomap.get("KSDM") + "");
				ll_jcpb = Integer.parseInt(cwszinfomap.get("JCPB") + "");
			}
			// 判断床位是否已使用
			Map<String, Object> parameterscwszzyh = new HashMap<String, Object>();
			parameterscwszzyh.put("JGID", manaUnitId);
			parameterscwszzyh.put("BRCH", parameters.get("BRCH") + "");
			Map<String, Object> zyhmap = dao
					.doLoad("SELECT ZYH as ZYH FROM ZY_CWSZ Where BRCH = :BRCH  and JGID = :JGID",
							parameterscwszzyh);
			if (zyhmap.get("ZYH") != null) {
				zyh = Integer.parseInt(zyhmap.get("ZYH") + "");
			}
			if (zyh > 0) {
				throw new ModelDataOperationException("床位"
						+ parameters.get("BRCH") + "" + "已被其他病人使用，不能进行床位分配!");
			}
			Map<String, Object> parameterscwsz = new HashMap<String, Object>();
			parameterscwsz.put("JGID", manaUnitId);
			parameterscwsz.put("BRCH", parameters.get("BRCH") + "");
			long ZYH = Long.parseLong(parameters.get("ZYH") + "");
			parameterscwsz.put("ZYH", ZYH);
			dao.doUpdate(
					"UPDATE ZY_CWSZ Set ZYH=:ZYH Where BRCH=:BRCH AND ZYH IS Null and JGID=:JGID",
					parameterscwsz);

			Map<String, Object> parametersbrry = new HashMap<String, Object>();
			parametersbrry.put("JGID", manaUnitId);
			parametersbrry.put("BRCH", parameters.get("BRCH") + "");
			parametersbrry.put("ZYH",
					Long.parseLong(parameters.get("ZYH") + ""));
			parametersbrry.put("BRBQ", ll_cwbq);
			parametersbrry.put("KSRQ", new Date());
			dao.doUpdate(
					"UPDATE ZY_BRRY SET BRCH=:BRCH,BRBQ=:BRBQ,KSRQ=:KSRQ Where ZYH=:ZYH and JGID=:JGID",
					parametersbrry);
			// 医保
			// Map<String, Object> brry = dao.doLoad(BSPHISEntryNames.ZY_BRRY
			// + "_RYDJ", ZYH);
			// if (brry.containsKey("YWLSH")) {
			// if (brry.get("YWLSH") != null) {
			// JXMedicareModel jxmm = new JXMedicareModel(dao);
			// jxmm.saveTransferBed(parameterscwsz, ctx);
			// }
			// }

			Map<String, Object> parametersrcjlcount = new HashMap<String, Object>();
			parametersrcjlcount.put("JGID", manaUnitId);
			parametersrcjlcount.put("ZYH",
					Long.parseLong(parameters.get("ZYH") + ""));
			Long l = dao.doCount("ZY_RCJL",
					"ZYH =:ZYH and JGID =:JGID and CZLX=1 and BQPB=0",
					parametersrcjlcount);
			if (l <= 0) {
				Map<String, Object> parametersryrq = new HashMap<String, Object>();
				parametersryrq.put("ZYH", parameters.get("ZYH"));
				parametersryrq.put("RYRQ", parameters.get("RYRQ")=="0000-00-00 00:00:00"?sdf.format(new Date())+"":parameters.get("RYRQ"));
				uf_ryrq_set(parametersryrq, dao, ctx);
			}
			Map<String, Object> parametersSaveHcmx = new HashMap<String, Object>();
			parametersSaveHcmx.put("ZYH",
					Long.parseLong(parameters.get("ZYH") + ""));
			parametersSaveHcmx.put("HCRQ", sdf.parse(sdf.format(new Date())));
			parametersSaveHcmx.put("HCLX", 0);
			parametersSaveHcmx.put("HHCH", parameters.get("BRCH") + "");
			parametersSaveHcmx.put("HHKS", ll_brks);
			parametersSaveHcmx.put("HHBQ", ll_cwbq);
			parametersSaveHcmx.put("HQCH", null);
			parametersSaveHcmx.put("HQKS", null);
			parametersSaveHcmx.put("HQBQ", null);
			parametersSaveHcmx.put("JSCS", 0);
			parametersSaveHcmx.put("CZGH", uid);
			parametersSaveHcmx.put("JGID", manaUnitId);
			dao.doSave("create", BSPHISEntryNames.ZY_HCMX, parametersSaveHcmx,
					false);

			Map<String, Object> parameterscwszcwks = new HashMap<String, Object>();
			parameterscwszcwks.put("KSDM", ll_cwks);
			parameterscwszcwks.put("BQPB", 0);
			Map<String, Object> parameterscwszksdm = new HashMap<String, Object>();
			parameterscwszksdm.put("KSDM", ll_cwbq);
			parameterscwszksdm.put("BQPB", 1);
			if (ll_jcpb < 2) {
				int ll_cwsys_ks = cwtj(parameterscwszcwks, dao, ctx);
				int ll_cwsys_bq = cwtj(parameterscwszksdm, dao, ctx);
				Map<String, Object> parametersSaveCwtjks = new HashMap<String, Object>();
				parametersSaveCwtjks.put("CZRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjks.put("CZLX", 1);
				parametersSaveCwtjks.put("ZYH",
						Long.parseLong(parameters.get("ZYH") + ""));
				parametersSaveCwtjks.put("BRKS", ll_cwks);
				parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
				parametersSaveCwtjks.put("XSYS", ll_cwsys_ks + 1);
				parametersSaveCwtjks.put("BQPB", 0);
				parametersSaveCwtjks.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
						parametersSaveCwtjks, false);
				Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
				parametersSaveCwtjbq.put("CZRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjbq.put("CZLX", 1);
				parametersSaveCwtjbq.put("ZYH",
						Long.parseLong(parameters.get("ZYH") + ""));
				parametersSaveCwtjbq.put("BRKS", ll_cwbq);
				parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
				parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq + 1);
				parametersSaveCwtjbq.put("BQPB", 1);
				parametersSaveCwtjbq.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
						parametersSaveCwtjbq, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("分配床位失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("分配位失败!", e);
		}
	}

	/**
	 * 换床位 parameters里需要传入参数 adt_date 转床日期 ,al_zyh 住院号 ,as_cwhm_Old 床位号码(原)
	 * as_cwhm_New床位号码(新), il_brks 科室
	 */
	public static void cwgl_zccl(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		// String uid = user.getId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String uid = user.getUserId();
		// 判断新床位是否已有病人
		int ll_zyh_New = 0;
		Long ll_bqdm_Old = 0L;
		Long ll_bqks_Old = 0L;
		int ll_jcpb = 0;
		Long ll_bqdm_New = 0L;
		Long ll_bqks_New = 0L;
		int ll_jcpb2 = 0;
		int ll_cwxb_New = 0;
		int ll_brxb_Old = 0;
		Map<String, Object> parameterscwszzyh = new HashMap<String, Object>();
		parameterscwszzyh.put("JGID", manaUnitId);
		parameterscwszzyh.put("BRCH", parameters.get("as_cwhm_New"));
		try {
			Map<String, Object> zyhmap = dao
					.doLoad("SELECT ZYH as ZYH From ZY_CWSZ Where BRCH = :BRCH And JGID = :JGID",
							parameterscwszzyh);
			if (zyhmap != null && zyhmap.size() > 0) {
				if (zyhmap.get("ZYH") == null) {
					ll_zyh_New = 0;
				} else {
					ll_zyh_New = Integer.parseInt(zyhmap.get("ZYH") + "");
				}
			}
			// 取床位病区代码
			Map<String, Object> parameterscwszold = new HashMap<String, Object>();
			parameterscwszold.put("BRCH", parameters.get("as_cwhm_Old"));
			parameterscwszold.put("JGID", manaUnitId);
			Map<String, Object> ksdmoldmap = dao
					.doLoad("SELECT zc.KSDM as KSDM,zc.CWKS as CWKS,zc.JCPB as JCPB,zc.CWXB as CWXB,zb.BRXB as BRXB From ZY_CWSZ zc,ZY_BRRY zb Where zc.ZYH=zb.ZYH and zc.BRCH=:BRCH And zc.JGID=:JGID",
							parameterscwszold);
			if (ksdmoldmap != null && ksdmoldmap.size() > 0) {
				ll_bqdm_Old = Long.parseLong(ksdmoldmap.get("KSDM") + "");
				ll_bqks_Old = Long.parseLong(ksdmoldmap.get("CWKS") + "");
				ll_jcpb = Integer.parseInt(ksdmoldmap.get("JCPB") + "");
				ll_brxb_Old = Integer.parseInt(ksdmoldmap.get("BRXB") + "");
			}
			Map<String, Object> parameterscwsznew = new HashMap<String, Object>();
			parameterscwsznew.put("BRCH", parameters.get("as_cwhm_New"));
			parameterscwsznew.put("JGID", manaUnitId);
			Map<String, Object> ksdnewmmap = dao
					.doLoad("SELECT KSDM as KSDM,CWKS as CWKS,JCPB as JCPB,CWXB as CWXB From ZY_CWSZ Where BRCH = :BRCH And JGID = :JGID",
							parameterscwsznew);
			if (ksdnewmmap != null && ksdnewmmap.size() > 0) {
				ll_bqdm_New = Long.parseLong(ksdnewmmap.get("KSDM") + "");
				ll_bqks_New = Long.parseLong(ksdnewmmap.get("CWKS") + "");
				ll_jcpb2 = Integer.parseInt(ksdnewmmap.get("JCPB") + "");
				ll_cwxb_New = Integer.parseInt(ksdnewmmap.get("CWXB") + "");
			}
			if (!ll_bqks_Old.equals(ll_bqks_New)) {
				throw new ModelDataOperationException("科室不同，不能转床!");
			}
			// if (!ll_bqdm_Old.equals(ll_bqdm_New)) {
			// throw new ModelDataOperationException("病区不同，不能转床!");
			// }
			if (ll_cwxb_New != 3) {
				if (ll_cwxb_New != ll_brxb_Old) {
					throw new ModelDataOperationException("性别不同，不能转床!");
				}
			}
			if (ll_jcpb < 2) {
				Map<String, Object> parameterscwszcwks = new HashMap<String, Object>();
				parameterscwszcwks.put("KSDM", ll_bqks_Old);
				parameterscwszcwks.put("BQPB", 0);
				Map<String, Object> parameterscwszksdm = new HashMap<String, Object>();
				parameterscwszksdm.put("KSDM", ll_bqdm_Old);
				parameterscwszksdm.put("BQPB", 1);
				int ll_cwsys_ks = cwtj(parameterscwszcwks, dao, ctx);
				int ll_cwsys_bq = cwtj(parameterscwszksdm, dao, ctx);

				Map<String, Object> parametersSaveCwtjks = new HashMap<String, Object>();
				parametersSaveCwtjks.put("CZRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjks.put("CZLX", 2);
				parametersSaveCwtjks.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjks.put("BRKS", ll_bqks_Old);
				parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
				parametersSaveCwtjks.put("XSYS", ll_cwsys_ks - 1);
				parametersSaveCwtjks.put("BQPB", 0);
				parametersSaveCwtjks.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
						parametersSaveCwtjks, false);
				Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
				parametersSaveCwtjbq.put("CZRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjbq.put("CZLX", 2);
				parametersSaveCwtjbq.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjbq.put("BRKS", ll_bqdm_Old);
				parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
				parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq - 1);
				parametersSaveCwtjbq.put("BQPB", 1);
				parametersSaveCwtjbq.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
						parametersSaveCwtjbq, false);
			}
			if (ll_jcpb2 < 2) {
				Map<String, Object> parameterscwszcwks = new HashMap<String, Object>();
				parameterscwszcwks.put("KSDM", ll_bqks_New);
				parameterscwszcwks.put("BQPB", 0);
				Map<String, Object> parameterscwszksdm = new HashMap<String, Object>();
				parameterscwszksdm.put("KSDM", ll_bqdm_New);
				parameterscwszksdm.put("BQPB", 1);
				int ll_cwsys_ks = cwtj(parameterscwszcwks, dao, ctx);
				int ll_cwsys_bq = cwtj(parameterscwszksdm, dao, ctx);
				Map<String, Object> parametersSaveCwtjks = new HashMap<String, Object>();
				parametersSaveCwtjks.put("CZRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjks.put("CZLX", 2);
				parametersSaveCwtjks.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjks.put("BRKS", ll_bqks_New);
				parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
				parametersSaveCwtjks.put("XSYS", ll_cwsys_ks + 1);
				parametersSaveCwtjks.put("BQPB", 0);
				parametersSaveCwtjks.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
						parametersSaveCwtjks, false);
				Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
				parametersSaveCwtjbq.put("CZRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjbq.put("CZLX", 2);
				parametersSaveCwtjbq.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjbq.put("BRKS", ll_bqdm_New);
				parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
				parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq + 1);
				parametersSaveCwtjbq.put("BQPB", 1);
				parametersSaveCwtjbq.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
						parametersSaveCwtjbq, false);
			}
			if (ll_zyh_New > 0) {
				if (ll_jcpb < 2) {
					Map<String, Object> parameterscwszcwks = new HashMap<String, Object>();
					parameterscwszcwks.put("KSDM", ll_bqks_Old);
					parameterscwszcwks.put("BQPB", 0);
					Map<String, Object> parameterscwszksdm = new HashMap<String, Object>();
					parameterscwszksdm.put("KSDM", ll_bqdm_Old);
					parameterscwszksdm.put("BQPB", 1);
					int ll_cwsys_ks = cwtj(parameterscwszcwks, dao, ctx);
					int ll_cwsys_bq = cwtj(parameterscwszksdm, dao, ctx);

					Map<String, Object> parametersSaveCwtjks = new HashMap<String, Object>();
					parametersSaveCwtjks.put("CZRQ",
							sdf.parse(sdf.format(new Date())));
					parametersSaveCwtjks.put("CZLX", 2);
					parametersSaveCwtjks.put("ZYH",
							Long.parseLong(ll_zyh_New + ""));
					parametersSaveCwtjks.put("BRKS", ll_bqks_Old);
					parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
					parametersSaveCwtjks.put("XSYS", ll_cwsys_ks + 1);
					parametersSaveCwtjks.put("BQPB", 0);
					parametersSaveCwtjks.put("JGID", manaUnitId);
					dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
							parametersSaveCwtjks, false);
					Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
					parametersSaveCwtjbq.put("CZRQ",
							sdf.parse(sdf.format(new Date())));
					parametersSaveCwtjbq.put("CZLX", 2);
					parametersSaveCwtjbq.put("ZYH",
							Long.parseLong(ll_zyh_New + ""));
					parametersSaveCwtjbq.put("BRKS", ll_bqdm_Old);
					parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
					parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq + 1);
					parametersSaveCwtjbq.put("BQPB", 1);
					parametersSaveCwtjbq.put("JGID", manaUnitId);
					dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
							parametersSaveCwtjbq, false);
				}
				if (ll_jcpb2 < 2) {
					Map<String, Object> parameterscwszcwks = new HashMap<String, Object>();
					parameterscwszcwks.put("KSDM", ll_bqks_New);
					parameterscwszcwks.put("BQPB", 0);
					Map<String, Object> parameterscwszksdm = new HashMap<String, Object>();
					parameterscwszksdm.put("KSDM", ll_bqdm_New);
					parameterscwszksdm.put("BQPB", 1);
					int ll_cwsys_ks = cwtj(parameterscwszcwks, dao, ctx);
					int ll_cwsys_bq = cwtj(parameterscwszksdm, dao, ctx);
					Map<String, Object> parametersSaveCwtjks = new HashMap<String, Object>();
					parametersSaveCwtjks.put("CZRQ",
							sdf.parse(sdf.format(new Date())));
					parametersSaveCwtjks.put("CZLX", 2);
					parametersSaveCwtjks.put("ZYH",
							Long.parseLong(ll_zyh_New + ""));
					parametersSaveCwtjks.put("BRKS", ll_bqks_New);
					parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
					parametersSaveCwtjks.put("XSYS", ll_cwsys_ks - 1);
					parametersSaveCwtjks.put("BQPB", 0);
					parametersSaveCwtjks.put("JGID", manaUnitId);
					dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
							parametersSaveCwtjks, false);
					Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
					parametersSaveCwtjbq.put("CZRQ",
							sdf.parse(sdf.format(new Date())));
					parametersSaveCwtjbq.put("CZLX", 2);
					parametersSaveCwtjbq.put("ZYH",
							Long.parseLong(ll_zyh_New + ""));
					parametersSaveCwtjbq.put("BRKS", ll_bqdm_New);
					parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
					parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq - 1);
					parametersSaveCwtjbq.put("BQPB", 1);
					parametersSaveCwtjbq.put("JGID", manaUnitId);
					dao.doSave("create", BSPHISEntryNames.ZY_CWTJ,
							parametersSaveCwtjbq, false);
				}
			}
			if (ll_zyh_New > 0) {
				Map<String, Object> parameterscwdd = new HashMap<String, Object>();
				parameterscwdd.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parameterscwdd.put("BRCH", parameters.get("as_cwhm_New"));
				parameterscwdd.put("ZYHNEW", Long.parseLong(ll_zyh_New + ""));
				parameterscwdd.put("JGID", manaUnitId);
				// 床位对调
				dao.doUpdate(
						"UPDATE ZY_CWSZ Set ZYH =:ZYH Where BRCH =:BRCH And ZYH =:ZYHNEW And JGID =:JGID",
						parameterscwdd);
			} else {
				Map<String, Object> parameterscwzk = new HashMap<String, Object>();
				parameterscwzk.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parameterscwzk.put("BRCH", parameters.get("as_cwhm_New"));
				parameterscwzk.put("JGID", manaUnitId);
				// 转到空床
				dao.doUpdate(
						"UPDATE ZY_CWSZ Set ZYH =:ZYH Where BRCH =:BRCH And ZYH Is Null And JGID =:JGID",
						parameterscwzk);
			}
			Map<String, Object> parametersbrryzh = new HashMap<String, Object>();
			parametersbrryzh.put("ZYH",
					Long.parseLong(parameters.get("al_zyh") + ""));
			parametersbrryzh.put("BRCH", parameters.get("as_cwhm_New"));
			parametersbrryzh.put("BRKS", ll_bqks_New);
			parametersbrryzh.put("BRBQ", ll_bqdm_New);
			// 转到空床
			dao.doUpdate(
					"UPDATE ZY_BRRY SET BRCH =:BRCH,BRKS =:BRKS,BRBQ =:BRBQ,JCKS=NULL Where ZYH =:ZYH",
					parametersbrryzh);
			// 医保
			// Map<String, Object> brry = dao.doLoad(BSPHISEntryNames.ZY_BRRY
			// + "_RYDJ", parametersbrryzh.get("ZYH"));
			// if (brry.containsKey("YWLSH")) {
			// if (brry.get("YWLSH") != null) {
			// JXMedicareModel jxmm = new JXMedicareModel(dao);
			// jxmm.saveTransferBed(parametersbrryzh, ctx);
			// }
			// }
			if (ll_zyh_New > 0) {
				Map<String, Object> parameterscwdd = new HashMap<String, Object>();
				parameterscwdd.put("ZYH", Long.parseLong(ll_zyh_New + ""));
				parameterscwdd.put("BRCH", parameters.get("as_cwhm_Old"));
				parameterscwdd.put("ZYHOLD",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parameterscwdd.put("JGID", manaUnitId);
				// 床位对调
				dao.doUpdate(
						"UPDATE ZY_CWSZ Set ZYH =:ZYH Where BRCH =:BRCH And ZYH =:ZYHOLD And JGID =:JGID",
						parameterscwdd);

				Map<String, Object> parametersbrryzs = new HashMap<String, Object>();
				parametersbrryzs.put("ZYH", Long.parseLong(ll_zyh_New + ""));
				parametersbrryzs.put("BRCH", parameters.get("as_cwhm_Old"));
				parametersbrryzs.put("BRKS", ll_bqks_Old);
				parametersbrryzs.put("BRBQ", ll_bqdm_Old);
				// 转到空床
				dao.doUpdate(
						"UPDATE ZY_BRRY SET BRCH=:BRCH,BRKS=:BRKS,BRBQ=:BRBQ,JCKS=NULL Where ZYH=:ZYH",
						parametersbrryzs);
				// 医保
				// Map<String, Object> brry1 =
				// dao.doLoad(BSPHISEntryNames.ZY_BRRY
				// + "_RYDJ", parametersbrryzs.get("ZYH"));
				// if (brry1.containsKey("YWLSH")) {
				// if (brry1.get("YWLSH") != null) {
				// JXMedicareModel jxmm = new JXMedicareModel(dao);
				// jxmm.saveTransferBed(parametersbrryzs, ctx);
				// }
				// }
			} else {
				Map<String, Object> parameterscwzk = new HashMap<String, Object>();
				parameterscwzk.put("ZYH",
						Long.parseLong(parameters.get("al_zyh") + ""));
				parameterscwzk.put("BRCH", parameters.get("as_cwhm_Old"));
				parameterscwzk.put("JGID", manaUnitId);
				// 转到空床
				dao.doUpdate(
						"UPDATE ZY_CWSZ Set ZYH = Null Where BRCH =:BRCH And ZYH =:ZYH And JGID =:JGID",
						parameterscwzk);
			}
			Map<String, Object> parameterssavezkjl = new HashMap<String, Object>();
			parameterssavezkjl.put("ZYH",
					Long.parseLong(parameters.get("al_zyh") + ""));
			parameterssavezkjl.put("HCLX", 2);
			parameterssavezkjl.put("BQZXRQ", sdf.parse(sdf.format(new Date())));
			parameterssavezkjl.put("BQZXGH", uid);
			parameterssavezkjl.put("ZXBZ", 3);
			parameterssavezkjl.put("HQCH", parameters.get("as_cwhm_Old"));
			parameterssavezkjl.put("HQBQ", ll_bqdm_Old);
			parameterssavezkjl.put("HQKS", ll_bqks_Old);
			parameterssavezkjl.put("HHKS",
					Long.parseLong(parameters.get("il_brks") + ""));
			parameterssavezkjl.put("HHBQ", ll_bqdm_New);
			parameterssavezkjl.put("HHCH", parameters.get("as_cwhm_New"));
			parameterssavezkjl.put("JGID", manaUnitId);
			dao.doSave("create", BSPHISEntryNames.ZY_ZKJL, parameterssavezkjl,
					false);

			if (ll_zyh_New > 0) {
				Map<String, Object> parameterssavezkjlnew = new HashMap<String, Object>();
				parameterssavezkjlnew.put("ZYH",
						Long.parseLong(ll_zyh_New + ""));
				parameterssavezkjlnew.put("HCLX", 2);
				parameterssavezkjlnew.put("BQZXRQ",
						sdf.parse(sdf.format(new Date())));
				parameterssavezkjlnew.put("BQZXGH", uid);
				parameterssavezkjlnew.put("ZXBZ", 3);
				parameterssavezkjlnew
						.put("HQCH", parameters.get("as_cwhm_New"));
				parameterssavezkjlnew.put("HQBQ", ll_bqdm_New);
				parameterssavezkjlnew.put("HQKS", ll_bqks_New);
				parameterssavezkjlnew.put("HHKS", ll_bqks_Old);
				parameterssavezkjlnew.put("HHBQ", ll_bqdm_Old);
				parameterssavezkjlnew
						.put("HHCH", parameters.get("as_cwhm_Old"));
				parameterssavezkjlnew.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_ZKJL,
						parameterssavezkjlnew, false);
			}

			Map<String, Object> parametersSaveHcmx = new HashMap<String, Object>();
			parametersSaveHcmx.put("ZYH",
					Long.parseLong(parameters.get("al_zyh") + ""));
			parametersSaveHcmx.put("HCRQ", sdf.parse(sdf.format(new Date())));
			parametersSaveHcmx.put("HCLX", 1);
			parametersSaveHcmx.put("HQCH", parameters.get("as_cwhm_Old"));
			parametersSaveHcmx.put("HHCH", parameters.get("as_cwhm_New"));
			parametersSaveHcmx.put("HQKS", ll_bqks_Old);
			parametersSaveHcmx.put("HHKS", ll_bqks_New);
			parametersSaveHcmx.put("HQBQ", ll_bqdm_Old);
			parametersSaveHcmx.put("HHBQ", ll_bqdm_New);
			parametersSaveHcmx.put("JSCS", 0);
			parametersSaveHcmx.put("CZGH", uid);
			parametersSaveHcmx.put("JGID", manaUnitId);
			dao.doSave("create", BSPHISEntryNames.ZY_HCMX, parametersSaveHcmx,
					false);
			if (ll_zyh_New > 0) {
				Map<String, Object> parametersSaveHcmxNew = new HashMap<String, Object>();
				parametersSaveHcmxNew.put("ZYH",
						Long.parseLong(ll_zyh_New + ""));
				parametersSaveHcmxNew.put("HCRQ",
						sdf.parse(sdf.format(new Date())));
				parametersSaveHcmxNew.put("HCLX", 1);
				parametersSaveHcmxNew
						.put("HQCH", parameters.get("as_cwhm_New"));
				parametersSaveHcmxNew
						.put("HHCH", parameters.get("as_cwhm_Old"));
				parametersSaveHcmxNew.put("HQKS", ll_bqks_New);
				parametersSaveHcmxNew.put("HHKS", ll_bqks_Old);
				parametersSaveHcmxNew.put("HQBQ", ll_bqdm_New);
				parametersSaveHcmxNew.put("HHBQ", ll_bqdm_Old);
				parametersSaveHcmxNew.put("JSCS", 0);
				parametersSaveHcmxNew.put("CZGH", uid);
				parametersSaveHcmxNew.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_HCMX,
						parametersSaveHcmxNew, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("换床位失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("换床位失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		}
	}

	// 统计床位使用信息
	/*
	 * parameters里放所需的如下值 ai_ksdm 科室代码/床位科室 ,ai_bqpb 病区判别 (0 科室 1 病区)
	 * 出参：ll_cwsys床位使用数
	 */
	public static int cwtj(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameterscwszks = new HashMap<String, Object>();
		parameterscwszks.put("KSDM", parameters.get("KSDM"));
		int ll_cwsys = 0;
		int bqpb = Integer.parseInt(parameters.get("BQPB") + "");
		try {
			if (bqpb == 0) {
				Long l = dao.doCount("ZY_CWSZ",
						"CWKS=:KSDM AND JCPB < 2 AND ZYH IS NOT NULL",
						parameterscwszks);
				ll_cwsys = Integer.parseInt(l + "");
			} else {
				Long l = dao.doCount("ZY_CWSZ",
						"KSDM =:KSDM AND JCPB < 2 AND ZYH IS NOT NULL",
						parameterscwszks);
				ll_cwsys = Integer.parseInt(l + "");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("统计床位使用信息失败!", e);
		}
		return ll_cwsys;
	}

	/**
	 * 检查是否已经入院或出院
	 * 
	 * 入参：parameters integer ai_czlx 操作类型（1=入院，-1 = 出院） ,long al_zyh住院号 datetime
	 * adt_lcrq 临床日期 ,long al_brks 病人科室 ,integer ai_bqpb 病区判别（0：科室 1:病区）,long
	 * al_cyfs 出院方式,string as_jyxx 出院建议信息
	 */
	public static void uf_ywcl(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		Map<String, Object> parametersrcjl = new HashMap<String, Object>();
		parametersrcjl.put("JGID", manaUnitId);
		parametersrcjl.put("ZYH", Long.parseLong(parameters.get("ZYH") + ""));
		parametersrcjl.put("BRKS", Long.parseLong(parameters.get("BRKS") + ""));
		int czlx = Integer.parseInt(parameters.get("CZLX") + "");
		int bqpb = Integer.parseInt(parameters.get("BQPB") + "");
		parametersrcjl.put("BQPB", bqpb);
		int al_cyfs = Integer.parseInt(parameters.get("CYFS") + "");
		String as_jyxx = parameters.get("JYXX") + "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (czlx == 1) {
				Long l = dao
						.doCount(
								"ZY_RCJL",
								"JGID=:JGID and ZYH=:ZYH and CZLX=1 and BRKS=:BRKS AND BQPB=:BQPB",
								parametersrcjl);
				if (l > 0) {
					throw new ModelDataOperationException("该病人已有临床入院记录,不能重复入院!");
				}
			} else {
				Long l = dao
						.doCount(
								"ZY_RCJL",
								"JGID=:JGID and ZYH=:ZYH and CZLX=-1 and BRKS=:BRKS AND BQPB=:BQPB",
								parametersrcjl);
				if (l > 0) {
					throw new ModelDataOperationException(
							"该病人已有临床出院记录,不能重复出院,请进行出院日期变动处理!");
				}
			}
			Map<String, Object> parametersSaveRcjl = new HashMap<String, Object>();
			parametersSaveRcjl.put("JGID", parameters.get("JGID"));
			parametersSaveRcjl.put("CZRQ", sdf.parse(sdf.format(new Date())));
			parametersSaveRcjl.put("LCRQ",
					sdf.parse(parameters.get("RYRQ") + ""));

			parametersSaveRcjl.put("CZLX", czlx);
			parametersSaveRcjl.put("ZYH",
					Long.parseLong(parameters.get("ZYH") + ""));
			parametersSaveRcjl.put("BRKS",
					Long.parseLong(parametersrcjl.get("BRKS") + ""));
			parametersSaveRcjl.put("YJZYRS", 0);
			parametersSaveRcjl.put("BQPB", bqpb);
			parametersSaveRcjl.put("CYFS", al_cyfs);
			parametersSaveRcjl.put("BZXX", as_jyxx);
			Map<String, Object> jlxhmap = dao.doSave("create",
					BSPHISEntryNames.ZY_RCJL, parametersSaveRcjl, false);
			parameters.put("JLXH", jlxhmap.get("JLXH") + "");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("检查是否已经入院或出院失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("检查是否已经入院或出院失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		}
	}

	// 设置入院日期
	/*
	 * 入参： parameters long al_zyh 住院号,datetime adt_xryrq
	 */
	public static void uf_ryrq_set(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		// 获取取病人的病人科室和病人病区
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parametersksbqlcrq = new HashMap<String, Object>();
		Map<String, Object> ksbqmap = new HashMap<String, Object>();
		Map<String, Object> lcrqmap = new HashMap<String, Object>();
		parametersksbqlcrq.put("ZYH",
				Long.parseLong(parameters.get("ZYH") + ""));
		parametersksbqlcrq.put("JGID", manaUnitId);
		Date ldt_now = new Date();
		Date ldt_yryrq = null;
		Date adt_xryrq = null;
		try {
			if (parameters.get("RYRQ") != null) {
				adt_xryrq = sdf.parse(parameters.get("RYRQ") + "");
			}
			ksbqmap = dao
					.doLoad("SELECT BRKS as BRKS,BRBQ as BRBQ FROM ZY_BRRY Where JGID=:JGID and ZYH=:ZYH",
							parametersksbqlcrq);
			// 取原入院日期到ldt_yryrq，如果取不到则为null
			Long ll_Count = dao.doCount("ZY_RCJL",
					"JGID=:JGID and ZYH=:ZYH AND CZLX=1 AND BQPB=0",
					parametersksbqlcrq);
			if (ll_Count > 1) {
				throw new ModelDataOperationException("存在非法入院记录,请与管理员联系!");
			}
			if (ll_Count == 1) {
				lcrqmap = dao
						.doLoad("SELECT LCRQ as LCRQ FROM ZY_RCJL Where JGID=:JGID and ZYH=:ZYH AND CZLX=1 AND BQPB=0",
								parametersksbqlcrq);
				if (lcrqmap != null && lcrqmap.size() > 0) {
					ldt_yryrq = sdf.parse(lcrqmap.get("LCRQ") + "");
				}
			}
			if (ldt_yryrq != adt_xryrq) { // 日期没改变
				// 进行取消入院处理
				if (adt_xryrq == null) {
					dao.doUpdate(
							"DELETE ZY_RCJL Where JGID=:JGID and ZYH=:ZYH AND CZLX =1",
							parametersksbqlcrq);
				} else {
					if (ldt_now.getTime() < adt_xryrq.getTime()) {
						throw new ModelDataOperationException("临床入院时间不能大于当天!");
					}
					// 如果原入院日期为空,则新设置入院日期
					if (ldt_yryrq == null) {
						Map<String, Object> parametersbrryryrq = new HashMap<String, Object>();
						parametersbrryryrq.put("ZYH",
								Long.parseLong(parameters.get("ZYH") + ""));
						parametersbrryryrq.put("JGID", manaUnitId);
						parametersbrryryrq.put("RYRQ", adt_xryrq);
						dao.doUpdate(
								"UPDATE ZY_BRRY Set RYRQ=:RYRQ Where JGID=:JGID and ZYH=:ZYH",
								parametersbrryryrq);
						Map<String, Object> parametersrcjl = new HashMap<String, Object>();
						parametersrcjl.put("JGID", manaUnitId);
						parametersrcjl.put("ZYH",
								Long.parseLong(parameters.get("ZYH") + ""));
						if (ksbqmap != null && ksbqmap.size() > 0) {
							parametersrcjl.put("BRKS",
									Long.parseLong(ksbqmap.get("BRKS") + ""));
						}
						parametersrcjl.put("CZLX", 1);
						parametersrcjl.put("BQPB", 0);
						parametersrcjl.put("CYFS", 0);
						parametersrcjl.put("JYXX", null);
						parametersrcjl.put("RYRQ", parameters.get("RYRQ") + "");
						uf_ywcl(parametersrcjl, dao, ctx);
						Map<String, Object> parametersrcjl2 = new HashMap<String, Object>();
						parametersrcjl2.put("JGID", manaUnitId);
						parametersrcjl2.put("ZYH",
								Long.parseLong(parameters.get("ZYH") + ""));
						if (ksbqmap != null && ksbqmap.size() > 0) {
							parametersrcjl2.put("BRKS",
									Long.parseLong(ksbqmap.get("BRBQ") + ""));
						}
						parametersrcjl2.put("CZLX", 1);
						parametersrcjl2.put("BQPB", 1);
						parametersrcjl2.put("CYFS", 0);
						parametersrcjl2.put("JYXX", null);
						parametersrcjl2
								.put("RYRQ", parameters.get("RYRQ") + "");
						uf_ywcl(parametersrcjl2, dao, ctx);
					} else {
						// 变动入院日期
						Map<String, Object> parametersupdbrryryrq = new HashMap<String, Object>();
						parametersupdbrryryrq.put("ZYH",
								Long.parseLong(parameters.get("ZYH") + ""));
						parametersupdbrryryrq.put("JGID", manaUnitId);
						parametersupdbrryryrq.put("RYRQ", adt_xryrq);
						dao.doUpdate(
								"UPDATE ZY_BRRY Set RYRQ=:RYRQ Where JGID=:JGID and ZYH=:ZYH",
								parametersupdbrryryrq);

						Map<String, Object> parametersrcjllcrq = new HashMap<String, Object>();
						parametersrcjllcrq.put("ZYH",
								Long.parseLong(parameters.get("ZYH") + ""));
						parametersrcjllcrq.put("JGID", manaUnitId);
						parametersrcjllcrq.put("CZRQ", ldt_now);
						parametersupdbrryryrq.put("LCRQ", adt_xryrq);
						dao.doUpdate(
								"UPDATE ZY_RCJL Set LCRQ=:LCRQ,CZRQ=:CZRQ Where JGID=:JGID and ZYH=:ZYH AND CZLX=1",
								parametersrcjllcrq);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("设置入院日期失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		}
	}

	/**
	 * 设置出院日期
	 * 
	 * @param parameters
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public static void uf_cyrq_set(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		// 获取取病人的病人科室和病人病区
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Object xcyrq = parameters.get("XCYRQ");// 当前出院时间
		Long zyh = Long.parseLong(parameters.get("ZYH").toString());// 住院号
		Object CYFS = parameters.get("CYFS");// 出院方式
		Object BZXX = parameters.get("BZXX");// 备注信息
		String YBZY=parameters.get("YBZY")==null?"":parameters.get("YBZY")+"";
		Integer cyfs = Integer.parseInt(CYFS == null ? "0" : CYFS.toString());
		if (xcyrq != null) {
			xcyrq = BSHISUtil.toDate(xcyrq.toString());
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ZYH", zyh);
		params.put("JGID", manaUnitId);
		try {
			Long l = dao.doCount("ZY_RCJL","JGID=:JGID and ZYH=:ZYH and CZLX=1", params);
			if (l == 0) {
				throw new ModelDataOperationException("无临床入院时间，无法出院!");
			}
			Map<String, Object> rcjl = dao.doLoad("select LCRQ as LCRQ from ZY_RCJL where JGID=:JGID " +
					" and ZYH=:ZYH and CZLX=1 and BQPB=0",params);
			if (xcyrq != null && BSHISUtil.dateCompare((Date) rcjl.get("LCRQ"),(Date) xcyrq) > 0) {
				throw new ModelDataOperationException("出院日期不能小于入院日期!");
			}
			Map<String, Object> brry = dao.doLoad("select BRKS as BRKS,BRBQ as BRBQ,CYPB as CYPB FROM ZY_BRRY " +
					" Where JGID=:JGID and ZYH=:ZYH",params);
			Map<String, Object> yrcjl = dao.doLoad("select LCRQ as LCRQ from ZY_RCJL " +
					" where JGID=:JGID and ZYH=:ZYH and CZLX=-1 and BQPB=0",params);
			if (Integer.parseInt(brry.get("CYPB").toString()) != 0) {
				throw new ModelDataOperationException("需先取消出院结算证明才能取消临床出院!");
			}
			if (xcyrq == null && (yrcjl == null || yrcjl.get("LCRQ") == null)) {
				throw new ModelDataOperationException("该病人未出过院，无需取消出院!");
			}
			if (xcyrq == null) {
				Map<String, Object> jlxhMap = dao.doLoad("select JLXH as JLXH from ZY_RCJL " +
						" where JGID=:JGID and ZYH=:ZYH and CZLX=-1 and BQPB=1",params);
				Map<String, Object> delbqyzpar = new HashMap<String, Object>();
				delbqyzpar.put("JGID", manaUnitId);
				if (jlxhMap != null) {
					if (jlxhMap.containsKey("JLXH")) {
						if (jlxhMap.get("JLXH") != null) {
							delbqyzpar.put("YWID",Long.parseLong(jlxhMap.get("JLXH") + ""));
							dao.doUpdate("delete from ZY_BQYZ where YWID=:YWID and JGID=:JGID",delbqyzpar);
						}
					}
				}
				dao.doUpdate("delete from ZY_RCJL where JGID=:JGID and ZYH=:ZYH and CZLX=-1",params);
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("JGID", manaUnitId);
				record.put("CZRQ", new Date());
				record.put("CYSJ", xcyrq);
				record.put("CZLX", 2);
				record.put("ZYH", zyh);
				record.put("CZR", user.getUserId());
				record.put("CYFS", 0);
				dao.doSave("create", BSPHISEntryNames.ZY_CYJL, record, false);
			} else {
				if (yrcjl != null && 
				    (BSHISUtil.toString((Date) yrcjl.get("LCRQ"),Constants.DEFAULT_DATE_FORMAT))
					.equals(BSHISUtil.toString((Date) xcyrq,Constants.DEFAULT_DATE_FORMAT))) {
					Map<String, Object> zyinfo = new HashMap<String, Object>();
					zyinfo.put("CYFS", cyfs);
					zyinfo.put("BZXX", BZXX);
					zyinfo.put("JGID", manaUnitId);
					zyinfo.put("ZYH", zyh);
					Map<String, Object> jlxhMap = dao.doLoad("select JLXH as JLXH from ZY_RCJL " +
							" where JGID=:JGID and ZYH=:ZYH and CZLX=-1 and BQPB=1",params);
					Map<String, Object> delbqyzpar = new HashMap<String, Object>();
					delbqyzpar.put("JGID", manaUnitId);
					if (jlxhMap != null) {
						if (jlxhMap.containsKey("JLXH")) {
							if (jlxhMap.get("JLXH") != null) {
								delbqyzpar.put("YWID", Long.parseLong(jlxhMap.get("JLXH") + ""));
								parameters.put("JLXH", jlxhMap.get("JLXH") + "");
								dao.doUpdate("delete from ZY_BQYZ where YWID=:YWID and JGID=:JGID",delbqyzpar);
							}
						}
					}
					dao.doUpdate("UPDATE ZY_RCJL Set CYFS=:CYFS,BZXX=:BZXX Where JGID =:JGID and ZYH=:ZYH and CZLX=-1",
							zyinfo);
					return;
				}
				params.put("CYRQ", xcyrq);
				params.put("CYFS", cyfs);
				if (yrcjl == null || yrcjl.get("LCRQ") == null) {
					params.put("YBZY", YBZY);
					dao.doUpdate("update ZY_BRRY set CYRQ=:CYRQ,CYFS=:CYFS,YBZY=:YBZY " +
							" where JGID=:JGID and ZYH=:ZYH and CYPB=0",params);
					params.remove("YBZY");
					Map<String, Object> ywcl = new HashMap<String, Object>();
					ywcl.put("ZYH", zyh);
					ywcl.put("JGID", manaUnitId);
					ywcl.put("CZLX", -1);
					ywcl.put("BQPB", 0);
					ywcl.put("RYRQ", BSHISUtil.toString((Date) xcyrq,Constants.DEFAULT_DATE_FORMAT));
					ywcl.put("BRKS", brry.get("BRKS"));
					ywcl.put("JYXX", BZXX);
					ywcl.put("CYFS", cyfs);
					uf_ywcl(ywcl, dao, ctx);
					ywcl.put("BQPB", 1);
					ywcl.put("BRKS", brry.get("BRBQ"));
					uf_ywcl(ywcl, dao, ctx);
					parameters.put("JLXH", ywcl.get("JLXH") + "");
				} else {
					params.put("CZRQ", new Date());
					params.put("BZXX", BZXX);
					dao.doUpdate("update ZY_RCJL set LCRQ=:CYRQ,CZRQ=:CZRQ,CYFS=:CYFS,BZXX=:BZXX " +
							" where JGID=:JGID and ZYH=:ZYH and CZLX=-1",params);
					Map<String, Object> jlxhMap = dao.doLoad("select JLXH as JLXH from ZY_RCJL " +
							" where JGID=:JGID and ZYH=:ZYH and CZLX=-1 and BQPB=1",params);
					Map<String, Object> delbqyzpar = new HashMap<String, Object>();
					delbqyzpar.put("JGID", manaUnitId);
					if (jlxhMap != null) {
						if (jlxhMap.containsKey("JLXH")) {
							if (jlxhMap.get("JLXH") != null) {
								delbqyzpar.put("YWID", Long.parseLong(jlxhMap.get("JLXH") + ""));
								parameters.put("JLXH", jlxhMap.get("JLXH") + "");
								dao.doUpdate("delete from ZY_BQYZ where YWID=:YWID and JGID=:JGID",delbqyzpar);
							}
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("出院证设置失败：数据库操作失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("出院证设置失败：数据校验失败!", e);
		}

	}

	/**
	 * 退床处理
	 * 
	 * @param inParams
	 *            ZYH(住院号) CWHM(床位号)
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static boolean cwgl_tccl(Map<String, Object> inParams, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String manageUnit = user.get("manageUnit.id");
		// String uid = user.getId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();// 用户的机构ID
		String uid = user.getUserId();
		Long zyh = Long.parseLong(inParams.get("ZYH").toString());
		String cwhm = inParams.get("CWHM").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			parameters.put("JGID", manageUnit);
			parameters.put("ZYH", zyh);
			long l = dao.doCount("ZY_RCJL",
					"JGID =:JGID and ZYH=:ZYH and CZLX=-1", parameters);
			if (l == 0) {
				throw new ModelDataOperationException("床位【" + cwhm
						+ "】的病人尚未办理出院证，不能退床!");
			}
			parameters.clear();
			parameters.put("BRCH", cwhm);
			parameters.put("JGID", manageUnit);
			String hql = "SELECT ZYH as ZYH,KSDM as KSDM,CWKS as CWKS,JCPB as JCPB FROM ZY_CWSZ Where BRCH = :BRCH  and JGID = :JGID";
			Map<String, Object> cwsz = dao.doLoad(hql, parameters);
			if (cwsz == null || cwsz.get("ZYH") == null
					|| cwsz.get("ZYH").toString() == "") {
				throw new ModelDataOperationException("床位【" + cwhm
						+ "】已被其他操作员退掉，不能重复退床");
			}
			parameters.put("ZYH", zyh);
			dao.doUpdate(
					"UPDATE ZY_CWSZ Set ZYH=null Where BRCH=:BRCH AND ZYH=:ZYH  and JGID=:JGID",
					parameters);
			dao.doUpdate(
					"UPDATE ZY_BRRY Set BRCH=null Where BRCH=:BRCH AND ZYH=:ZYH  and JGID=:JGID",
					parameters);
			parameters.remove("BRCH");
			String mxSql = "SELECT BRKS as HQKS,BRBQ as HQBQ FROM ZY_BRRY Where ZYH=:ZYH and JGID=:JGID";
			Map<String, Object> hcmx = dao.doLoad(mxSql, parameters);
			hcmx.put("ZYH", zyh);
			hcmx.put("HCRQ", new Date());
			hcmx.put("HCLX", 5);
			hcmx.put("HQCH", cwhm);
			hcmx.put("HHCH", null);
			hcmx.put("HHKS", null);
			hcmx.put("HHBQ", null);
			hcmx.put("JSCS", 0);
			hcmx.put("CZGH", uid);
			hcmx.put("JGID", manageUnit);

			dao.doSave("create", BSPHISEntryNames.ZY_HCMX, hcmx, false);

			int jcpb = (Integer) cwsz.get("JCPB");
			if (jcpb < 2) {
				Map<String, Object> cwtj = new HashMap<String, Object>();
				Map<String, Object> cwtj_params = new HashMap<String, Object>();
				cwtj_params.put("KSDM",
						Long.parseLong(cwsz.get("CWKS").toString()));
				cwtj_params.put("BQPB", 0);
				int ysys = cwtj(cwtj_params, dao, ctx) + 1;
				cwtj.put("CZRQ", new Date());
				cwtj.put("CZLX", -4);
				cwtj.put("ZYH", zyh);
				cwtj.put("BRKS", hcmx.get("HQKS"));
				cwtj.put("YSYS", Integer.parseInt(ysys + ""));
				cwtj.put("XSYS", Integer.parseInt(ysys + "") - 1);
				cwtj.put("BQPB", 0);
				cwtj.put("JGID", manageUnit);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ, cwtj, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退床处理失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("床位明细校验失败!", e);
		}

		return true;
	}

	/**
	 * 医嘱执行——确认
	 * 
	 * @param list_FYMX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public static boolean uf_insert_fymx(List<Map<String, Object>> list_FYMX,
			List<Map<String, Object>> listForputFYMX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ServiceException,
			ParseException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		long BQ = 0;
		if (user.getProperty("wardId") != null) {
			// BQ = Long.parseLong(user.getProperty("wardId"));// 当前病区
			BQ = Long.parseLong(user.getProperty("wardId") + "");// 当前病区
		} else {
			if (list_FYMX != null && list_FYMX.size() > 0)
				BQ = Long.parseLong(String
						.valueOf(list_FYMX.get(0).get("FYBQ")));
		}
		String YGGH = user.getUserId();// 当前操作员工号
//		try {
			for (int i = 0; i < list_FYMX.size(); i++) {
				long ZXKS = 0;
				double ZFBL = 0;
				double ZFJE = 0;
				double ZLJE = 0;
				double DZBL = 0;
				double ZJJE = 0;
				if (list_FYMX.get(i).get("ZFBL") != null) {
					ZFBL = Double
							.parseDouble(list_FYMX.get(i).get("ZFBL") + "");
				}
				if (list_FYMX.get(i).get("ZFJE") != null) {
					ZFJE = Double
							.parseDouble(list_FYMX.get(i).get("ZFJE") + "");
				}
				if (list_FYMX.get(i).get("ZLJE") != null) {
					ZLJE = Double
							.parseDouble(list_FYMX.get(i).get("ZLJE") + "");
				}
				if (list_FYMX.get(i).get("DZBL") != null) {
					DZBL = Double
							.parseDouble(list_FYMX.get(i).get("DZBL") + "");
				}
				if (list_FYMX.get(i).get("ZJJE") != null) {
					ZJJE = Double
							.parseDouble(list_FYMX.get(i).get("ZJJE") + "");
				}
				long ZYH = Long.parseLong(list_FYMX.get(i).get("ZYH") + "");
				long FYKS = Long.parseLong(list_FYMX.get(i).get("FYKS") + "");
				int YPLX = Integer.parseInt(list_FYMX.get(i).get("YPLX") + "");
				long BRXZ = Long.parseLong(list_FYMX.get(i).get("BRXZ") + "");
				double FYDJ = Double.parseDouble(list_FYMX.get(i).get("FYDJ")
						+ "");
				double FYSL = Double.parseDouble(list_FYMX.get(i).get("FYSL")
						+ "");
				String YSGH = list_FYMX.get(i).get("YSGH") + "";
				long YPCD = Long.parseLong(list_FYMX.get(i).get("YPCD") + "");
				// 执行科室为空时 默认为费用科室
				if (list_FYMX.get(i).get("ZXKS") == null
						|| list_FYMX.get(i).get("ZXKS") == ""
						|| list_FYMX.get(i).get("ZXKS").equals("null")) {
					ZXKS = FYKS;
				} else {
					ZXKS = Long.parseLong(list_FYMX.get(i).get("ZXKS") + "");
				}
				// 判断主治医生是否为空
//				Map<String, Object> parameters = new HashMap<String, Object>();
//				parameters.put("ZYH", ZYH);
//				parameters.put("JGID", JGID);
//				Map<String, Object> map_ZSYS = dao
//						.doLoad("SELECT ZSYS as YSGH From ZY_BRRY where JGID = :JGID and ZYH = :ZYH",
//								parameters);
//				if (map_ZSYS.get("YSGH") != null) {
//					YSGH = map_ZSYS.get("YSGH") + "";
//				}
				// 住院费用明细表的用于插入的Map
				Map<String, Object> zyfymx_map = (Map<String, Object>) list_FYMX
						.get(i);
				// 费用性质 YPLX_c 参数药品类型。
				long YPLX_c = Long.parseLong(list_FYMX.get(i).get("YPLX") + "");
				long FYXH = Long.parseLong(list_FYMX.get(i).get("FYXH") + "");
				long FYXM = getfygb(YPLX_c, FYXH, dao, ctx);
				zyfymx_map.put("FYXM", FYXM);
				// YPLX 为0表示费用
				if (YPLX == 0) {
					if (FYSL < 0) {
						zyfymx_map.put("ZFBL", ZFBL);
						zyfymx_map.put("ZJJE", ZJJE);
						zyfymx_map.put("ZFJE", ZFJE);
						zyfymx_map.put("ZLJE", ZLJE);
					} else {
						if (ZFJE > 0) {
							zyfymx_map.put("ZFBL", ZFBL);
							zyfymx_map.put("ZJJE", ZJJE);
							zyfymx_map.put("ZFJE", ZFJE);
							zyfymx_map.put("ZLJE", ZLJE);
						} else {
							Map<String, Object> FYXX = getje(YPLX, BRXZ, FYXH,
									FYXM, FYDJ, FYSL, dao, ctx);
							zyfymx_map.put("ZFBL", FYXX.get("ZFBL"));
							zyfymx_map.put("ZJJE", FYXX.get("ZJJE"));
							zyfymx_map.put("ZFJE", FYXX.get("ZFJE"));
							zyfymx_map.put("ZLJE", FYXX.get("ZLJE"));
						}
					}
				} else {
					// 否则就是药品
					if (FYSL < 0) {
						zyfymx_map.put("ZFBL", ZFBL);
						zyfymx_map.put("ZJJE", ZJJE);
						zyfymx_map.put("ZFJE", ZFJE);
						zyfymx_map.put("ZLJE", ZLJE);
					} else {
						if (ZFJE > 0) {
							zyfymx_map.put("ZFBL", ZFBL);
							zyfymx_map.put("ZJJE", ZJJE);
							zyfymx_map.put("ZFJE", ZFJE);
							zyfymx_map.put("ZLJE", ZLJE);
							zyfymx_map.put("FYXM", FYXM);
						} else {
							Map<String, Object> FYXX = getje(YPLX, BRXZ, FYXH,
									FYXM, FYDJ, FYSL, dao, ctx);
							zyfymx_map.put("ZFBL", FYXX.get("ZFBL"));
							zyfymx_map.put("ZJJE", FYXX.get("ZJJE"));
							zyfymx_map.put("ZFJE", FYXX.get("ZFJE"));
							zyfymx_map.put("ZLJE", FYXX.get("ZLJE"));
							zyfymx_map.put("FYXM", FYXX.get("FYGB"));
						}
					}
				}
				// 判断发药日期是否为空
				if (list_FYMX.get(i).get("FYRQ") == null
						|| list_FYMX.get(i).get("FYRQ") == "") {
					zyfymx_map.put("FYRQ", new Date());
				} else {
					String FYRQ = list_FYMX.get(i).get("FYRQ") + "";
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date = list_FYMX.get(i).get("FYRQ") instanceof Date ? (Date) list_FYMX
							.get(i).get("FYRQ") : sdf.parse(FYRQ);
					zyfymx_map.put("FYRQ", date);
				}
				// 判断计费日期是否为空
				if (list_FYMX.get(i).get("JFRQ") == null
						|| list_FYMX.get(i).get("JFRQ") == "") {
					zyfymx_map.put("JFRQ", new Date());
				} else {
					String JFRQ = list_FYMX.get(i).get("JFRQ") + "";
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date = list_FYMX.get(i).get("JFRQ") instanceof Date ? (Date) list_FYMX
							.get(i).get("JFRQ") : sdf.parse(JFRQ);
					zyfymx_map.put("JFRQ", date);
				}
				// 判断药品类型
				if (YPLX == 0) {
					zyfymx_map.put("YPCD", 0);
				} else {
					zyfymx_map.put("YPCD", YPCD);
				}
				if (list_FYMX.get(i).get("JLXH") != null) {
					zyfymx_map.put("YZXH",
							Long.parseLong(list_FYMX.get(i).get("JLXH") + ""));// 医嘱序号
				}
				zyfymx_map.put("ZYH", ZYH);// 住院号
				zyfymx_map.put("FYXH", FYXH);// 发药序号
				zyfymx_map.put("FYMC", list_FYMX.get(i).get("FYMC"));// 费用名称
				zyfymx_map.put("FYSL", FYSL);// 发药数量
				zyfymx_map.put("FYDJ", FYDJ);// 发药单价
				zyfymx_map.put("QRGH", YGGH);// 当前操作员工号
				zyfymx_map.put("FYKS", FYKS);// 费用科室 long
				zyfymx_map.put("ZXKS", ZXKS);// 执行科室 long
				zyfymx_map.put("XMLX", 1);// 项目类型// int
				zyfymx_map.put("YPLX", YPLX);// 药品类型
				zyfymx_map.put("YSGH", YSGH);// 医生工号
				zyfymx_map.put("FYBQ", BQ);// 费用病区 long
				zyfymx_map.put("DZBL", DZBL);
				zyfymx_map.put("JSCS", 0);
				zyfymx_map.put("YEPB", 0);
				zyfymx_map.put("JGID", JGID);
				zyfymx_map.put("JFRQ", new Date());
				listForputFYMX.add(i, zyfymx_map);
				/*
				 * Map<String, Object> parameters_update = new HashMap<String,
				 * Object>(); for (int j = 0; j < inofList.size(); j++) { long
				 * JLXH = Long .parseLong(inofList.get(j).get("JLXH") + "");
				 * long JLXH_l = Long.parseLong(list_FYMX.get(i).get("JLXH") +
				 * ""); String QRSJ = inofList.get(j).get("QRSJ") + ""; double
				 * YCSL = Double.parseDouble(inofList.get(j) .get("YCSL") + "");
				 * int LSBZ = Integer.parseInt(inofList.get(j).get("LSBZ") +
				 * "");
				 * 
				 * parameters_update.put("JLXH", JLXH);
				 * parameters_update.put("QRSJ", BSHISUtil.toDate(QRSJ));
				 * parameters_update.put("LSBZ", LSBZ); if (JLXH == JLXH_l) {
				 * dao.doUpdate(
				 * "update ZY_BQYZ set QRSJ=:QRSJ,LSBZ=:LSBZ where JLXH =:JLXH",
				 * parameters_update); } } dao.doSave("create", "ZY_FYMX",
				 * zyfymx_map, false);
				 */
			}
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//			throw new RuntimeException("确认失败！");
//		}
		return true;
	}

	/**
	 * 医嘱执行——确认
	 * 
	 * @param list_FYMX
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public static boolean uf_insert_jc_fymx(
			List<Map<String, Object>> list_FYMX,
			List<Map<String, Object>> listForputFYMX, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ServiceException,
			ParseException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		long BQ = 0;
		if (user.getProperty("wardId") != null) {
			// BQ = Long.parseLong(user.getProperty("wardId"));// 当前病区
			BQ = Long.parseLong(user.getProperty("wardId") + "");// 当前病区
		} else {
			if (list_FYMX != null && list_FYMX.size() > 0)
				BQ = Long.parseLong(String
						.valueOf(list_FYMX.get(0).get("FYBQ")));
		}
		String YGGH = user.getUserId();// 当前操作员工号
		try {
			for (int i = 0; i < list_FYMX.size(); i++) {
				long ZXKS = 0;
				double ZFBL = 0;
				double ZFJE = 0;
				double ZLJE = 0;
				double DZBL = 0;
				double ZJJE = 0;
				if (list_FYMX.get(i).get("ZFBL") != null) {
					ZFBL = Double
							.parseDouble(list_FYMX.get(i).get("ZFBL") + "");
				}
				if (list_FYMX.get(i).get("ZFJE") != null) {
					ZFJE = Double
							.parseDouble(list_FYMX.get(i).get("ZFJE") + "");
				}
				if (list_FYMX.get(i).get("ZLJE") != null) {
					ZLJE = Double
							.parseDouble(list_FYMX.get(i).get("ZLJE") + "");
				}
				if (list_FYMX.get(i).get("DZBL") != null) {
					DZBL = Double
							.parseDouble(list_FYMX.get(i).get("DZBL") + "");
				}
				if (list_FYMX.get(i).get("ZJJE") != null) {
					ZJJE = Double
							.parseDouble(list_FYMX.get(i).get("ZJJE") + "");
				}
				long ZYH = Long.parseLong(list_FYMX.get(i).get("ZYH") + "");
				long FYKS = Long.parseLong(list_FYMX.get(i).get("FYKS") + "");
				int YPLX = Integer.parseInt(list_FYMX.get(i).get("YPLX") + "");
				long BRXZ = Long.parseLong(list_FYMX.get(i).get("BRXZ") + "");
				double FYDJ = Double.parseDouble(list_FYMX.get(i).get("FYDJ")
						+ "");
				double FYSL = Double.parseDouble(list_FYMX.get(i).get("FYSL")
						+ "");
				String YSGH = list_FYMX.get(i).get("YSGH") + "";
				long YPCD = Long.parseLong(list_FYMX.get(i).get("YPCD") + "");
				// 执行科室为空时 默认为费用科室
				if (list_FYMX.get(i).get("ZXKS") == null
						|| list_FYMX.get(i).get("ZXKS") == ""
						|| list_FYMX.get(i).get("ZXKS").equals("null")) {
					ZXKS = FYKS;
				} else {
					ZXKS = Long.parseLong(list_FYMX.get(i).get("ZXKS") + "");
				}
				// 判断主治医生是否为空
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZYH", ZYH);
				parameters.put("JGID", JGID);
				Map<String, Object> map_ZSYS = dao
						.doLoad("SELECT ZRYS as YSGH From JC_BRRY where JGID = :JGID and ZYH = :ZYH",
								parameters);
				if (map_ZSYS.get("YSGH") != null) {
					YSGH = map_ZSYS.get("YSGH") + "";
				}
				// 住院费用明细表的用于插入的Map
				Map<String, Object> zyfymx_map = (Map<String, Object>) list_FYMX
						.get(i);
				// 费用性质 YPLX_c 参数药品类型。
				long YPLX_c = Long.parseLong(list_FYMX.get(i).get("YPLX") + "");
				long FYXH = Long.parseLong(list_FYMX.get(i).get("FYXH") + "");
				long FYXM = getfygb(YPLX_c, FYXH, dao, ctx);
				zyfymx_map.put("FYXM", FYXM);
				// YPLX 为0表示费用
				if (YPLX == 0) {
					if (FYSL < 0) {
						zyfymx_map.put("ZFBL", ZFBL);
						zyfymx_map.put("ZJJE", ZJJE);
						zyfymx_map.put("ZFJE", ZFJE);
						zyfymx_map.put("ZLJE", ZLJE);
					} else {
						if (ZFJE > 0) {
							zyfymx_map.put("ZFBL", ZFBL);
							zyfymx_map.put("ZJJE", ZJJE);
							zyfymx_map.put("ZFJE", ZFJE);
							zyfymx_map.put("ZLJE", ZLJE);
						} else {
							Map<String, Object> FYXX = getje(YPLX, BRXZ, FYXH,
									FYXM, FYDJ, FYSL, dao, ctx);
							zyfymx_map.put("ZFBL", FYXX.get("ZFBL"));
							zyfymx_map.put("ZJJE", FYXX.get("ZJJE"));
							zyfymx_map.put("ZFJE", FYXX.get("ZFJE"));
							zyfymx_map.put("ZLJE", FYXX.get("ZLJE"));
						}
					}
				} else {
					// 否则就是药品
					if (FYSL < 0) {
						zyfymx_map.put("ZFBL", ZFBL);
						zyfymx_map.put("ZJJE", ZJJE);
						zyfymx_map.put("ZFJE", ZFJE);
						zyfymx_map.put("ZLJE", ZLJE);
					} else {
						if (ZFJE > 0) {
							zyfymx_map.put("ZFBL", ZFBL);
							zyfymx_map.put("ZJJE", ZJJE);
							zyfymx_map.put("ZFJE", ZFJE);
							zyfymx_map.put("ZLJE", ZLJE);
							zyfymx_map.put("FYXM", FYXM);
						} else {
							Map<String, Object> FYXX = getje(YPLX, BRXZ, FYXH,
									FYXM, FYDJ, FYSL, dao, ctx);
							zyfymx_map.put("ZFBL", FYXX.get("ZFBL"));
							zyfymx_map.put("ZJJE", FYXX.get("ZJJE"));
							zyfymx_map.put("ZFJE", FYXX.get("ZFJE"));
							zyfymx_map.put("ZLJE", FYXX.get("ZLJE"));
							zyfymx_map.put("FYXM", FYXX.get("FYGB"));
						}
					}
				}
				// 判断发药日期是否为空
				if (list_FYMX.get(i).get("FYRQ") == null
						|| list_FYMX.get(i).get("FYRQ") == "") {
					zyfymx_map.put("FYRQ", new Date());
				} else {
					String FYRQ = list_FYMX.get(i).get("FYRQ") + "";
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date = list_FYMX.get(i).get("FYRQ") instanceof Date ? (Date) list_FYMX
							.get(i).get("FYRQ") : sdf.parse(FYRQ);
					zyfymx_map.put("FYRQ", date);
				}
				// 判断计费日期是否为空
				if (list_FYMX.get(i).get("JFRQ") == null
						|| list_FYMX.get(i).get("JFRQ") == "") {
					zyfymx_map.put("JFRQ", new Date());
				} else {
					String JFRQ = list_FYMX.get(i).get("JFRQ") + "";
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date = list_FYMX.get(i).get("JFRQ") instanceof Date ? (Date) list_FYMX
							.get(i).get("JFRQ") : sdf.parse(JFRQ);
					zyfymx_map.put("JFRQ", date);
				}
				// 判断药品类型
				if (YPLX == 0) {
					zyfymx_map.put("YPCD", 0);
				} else {
					zyfymx_map.put("YPCD", YPCD);
				}
				if (list_FYMX.get(i).get("JLXH") != null) {
					zyfymx_map.put("YZXH",
							Long.parseLong(list_FYMX.get(i).get("JLXH") + ""));// 医嘱序号
				}
				zyfymx_map.put("ZYH", ZYH);// 住院号
				zyfymx_map.put("FYXH", FYXH);// 发药序号
				zyfymx_map.put("FYMC", list_FYMX.get(i).get("FYMC"));// 费用名称
				zyfymx_map.put("FYSL", FYSL);// 发药数量
				zyfymx_map.put("FYDJ", FYDJ);// 发药单价
				zyfymx_map.put("QRGH", YGGH);// 当前操作员工号
				zyfymx_map.put("FYKS", FYKS);// 费用科室 long
				zyfymx_map.put("ZXKS", ZXKS);// 执行科室 long
				zyfymx_map.put("XMLX", 1);// 项目类型// int
				zyfymx_map.put("YPLX", YPLX);// 药品类型
				zyfymx_map.put("YSGH", YSGH);// 医生工号
				zyfymx_map.put("FYBQ", BQ);// 费用病区 long
				zyfymx_map.put("DZBL", DZBL);
				zyfymx_map.put("JSCS", 0);
				zyfymx_map.put("YEPB", 0);
				zyfymx_map.put("JGID", JGID);
				zyfymx_map.put("JFRQ", new Date());
				listForputFYMX.add(i, zyfymx_map);
				/*
				 * Map<String, Object> parameters_update = new HashMap<String,
				 * Object>(); for (int j = 0; j < inofList.size(); j++) { long
				 * JLXH = Long .parseLong(inofList.get(j).get("JLXH") + "");
				 * long JLXH_l = Long.parseLong(list_FYMX.get(i).get("JLXH") +
				 * ""); String QRSJ = inofList.get(j).get("QRSJ") + ""; double
				 * YCSL = Double.parseDouble(inofList.get(j) .get("YCSL") + "");
				 * int LSBZ = Integer.parseInt(inofList.get(j).get("LSBZ") +
				 * "");
				 * 
				 * parameters_update.put("JLXH", JLXH);
				 * parameters_update.put("QRSJ", BSHISUtil.toDate(QRSJ));
				 * parameters_update.put("LSBZ", LSBZ); if (JLXH == JLXH_l) {
				 * dao.doUpdate(
				 * "update ZY_BQYZ set QRSJ=:QRSJ,LSBZ=:LSBZ where JLXH =:JLXH",
				 * parameters_update); } } dao.doSave("create", "ZY_FYMX",
				 * zyfymx_map, false);
				 */
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new RuntimeException("确认失败！");
		}
		return true;
	}

	// 新模式:
	// 年龄大于等于3*12个月的，用岁表示；
	// 小于3*12个月而又大于等于1*12个月的，用岁月表示；
	// 小于12个月而又大于等于6个月的，用月表示；
	// 小于6个月而大于等于29天的，用月天表示；
	// 大于72小时小于29天的，用天表示；
	// 小于72小时的，用小时表示。

	public static Map<String, Object> getPersonAge(Date birthday, Date nowDate) {
		Calendar now = Calendar.getInstance();
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthday);
		if (nowDate != null) {
			// nowDate = new Date();
			now.setTime(nowDate);
		}
		// Calendar now = Calendar.getInstance();
		// now.setTime(nowDate);
		// Calendar birth = Calendar.getInstance();
		// birth.setTime(birthday);
		int age = BSHISUtil.calculateAge(birthday, nowDate);
		String reAge = age + "岁";
		if (age < 3 && age >= 1) {
			int month = BSHISUtil.getMonths(birthday, now.getTime());
			reAge = age + "岁";
			if ((month - 12 * age) > 0) {
				reAge = age + "岁" + (month - 12 * age) + "月";
			}
		} else if (age < 1) {
			int month = BSHISUtil.getMonths(birthday, now.getTime());
			if (month < 12 && month >= 6) {
				reAge = month + "月";
			} else {
				int day = BSHISUtil.getPeriod(birthday, null);
				if (day >= 29 && month > 0) {
					if (now.get(Calendar.DAY_OF_MONTH) >= birth
							.get(Calendar.DAY_OF_MONTH)) {
						day = now.get(Calendar.DAY_OF_MONTH)
								- birth.get(Calendar.DAY_OF_MONTH);
					} else {
						now.set(Calendar.MONTH, birth.get(Calendar.MONTH) + 1);
						day = now.get(Calendar.DAY_OF_YEAR)
								- birth.get(Calendar.DAY_OF_YEAR);
					}
					reAge = month + "月";
					if (day > 0) {
						reAge = month + "月" + day + "天";
					}
				} else {
					if (day >= 4) {
						if ((now.get(Calendar.DAY_OF_YEAR) - birth
								.get(Calendar.DAY_OF_YEAR)) > 0) {
							day = now.get(Calendar.DAY_OF_YEAR)
									- birth.get(Calendar.DAY_OF_YEAR);
						}
						reAge = day - 1 + "天";
					} else {
						int hour = now.get(Calendar.HOUR_OF_DAY)
								- birth.get(Calendar.HOUR_OF_DAY);
						reAge = hour + 24 * (day) + "小时";
					}
				}
			}
		}
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("age", age);
		resBody.put("ages", reAge);
		return resBody;
	}

	/**
	 * 获取发票号码
	 * 
	 * @param as_BillType
	 * @param as_BillNumber
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static String Getbillnumber(String as_BillType, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		String as_BillNumber = "";
		// User user = (User) ctx.get("user.instance");
		// String Gl_jgid = user.get("manageUnit.id");
		// String ls_UserId = user.getId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String Gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		String ls_UserId = user.getUserId() + "";
		int li_BillType = 0;
		if ("发票".equals(as_BillType)) {
			li_BillType = 1;
		} else if ("收据".equals(as_BillType)) {
			li_BillType = 2;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("li_BillType", li_BillType);
		parameters.put("ls_UserId", ls_UserId + "");
		parameters.put("Gl_jgid", Gl_jgid);
		Map<String, Object> LYRQ;
		try {
			String lyrqsql = "SELECT MIN(LYRQ) as LYRQ FROM ZY_YGPJ WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND JGID = :Gl_jgid";
			// 取发票号码时 如果公有参数启用 这不需要加员工过滤条件 否者需要
			if (("1".equals(ParameterUtil.getParameter(Gl_jgid, "ZYFPSFZCGY",
					ctx)) && li_BillType == 1)
					|| ("1".equals(ParameterUtil.getParameter(Gl_jgid,
							"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
				parameters.remove("ls_UserId");
				lyrqsql = "SELECT MIN(LYRQ) as LYRQ FROM ZY_YGPJ WHERE PJLX = :li_BillType AND SYBZ = 0 AND JGID = :Gl_jgid";
			}
			LYRQ = dao.doLoad(lyrqsql, parameters);
			if (LYRQ != null && LYRQ.get("LYRQ") != null) {
				Date ldt_lyrq = (Date) LYRQ.get("LYRQ");
				parameters.put("ldt_lyrq", ldt_lyrq);
				String dqhmsql = "SELECT DQHM as DQHM FROM ZY_YGPJ WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_lyrq AND JGID = :Gl_jgid";
				if (("1".equals(ParameterUtil.getParameter(Gl_jgid,
						"ZYFPSFZCGY", ctx)) && li_BillType == 1)
						|| ("1".equals(ParameterUtil.getParameter(Gl_jgid,
								"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
					parameters.remove("ls_UserId");
					dqhmsql = "SELECT DQHM as DQHM FROM ZY_YGPJ WHERE PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_lyrq AND JGID = :Gl_jgid";
				}
				List<Map<String, Object>> listDQHM = dao.doQuery(dqhmsql,
						parameters);
				if (listDQHM != null && listDQHM.size() > 0) {
					Map<String, Object> DQHM = listDQHM.get(0);
					if (DQHM != null && DQHM.get("DQHM") != null) {
						as_BillNumber = MedicineUtils.parseString( DQHM.get("DQHM"));
						return as_BillNumber;
					}
				}
			} else {
				as_BillNumber = "";
				return "false";
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("发票号码查询失败!", e);
		}
		as_BillNumber = as_BillNumber.trim();
		if (as_BillNumber == null)
			return "false";
		if (as_BillNumber.length() == 0)
			return "false";
		return as_BillNumber;
	}
	
	/**
	 * 获取发票号码
	 * 
	 * @param as_BillType
	 * @param as_BillNumber
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static String GetFsbBillnumber(String as_BillType, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		String as_BillNumber = "";
		// User user = (User) ctx.get("user.instance");
		// String Gl_jgid = user.get("manageUnit.id");
		// String ls_UserId = user.getId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String Gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		String ls_UserId = user.getUserId() + "";
		int li_BillType = 0;
		if ("发票".equals(as_BillType)) {
			li_BillType = 1;
		} else if ("收据".equals(as_BillType)) {
			li_BillType = 2;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("li_BillType", li_BillType);
		parameters.put("ls_UserId", ls_UserId + "");
		parameters.put("Gl_jgid", Gl_jgid);
		Map<String, Object> LYRQ;
		try {
			String lyrqsql = "SELECT MIN(LYRQ) as LYRQ FROM JC_YGPJ WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND JGID = :Gl_jgid";
			// 取发票号码时 如果公有参数启用 这不需要加员工过滤条件 否者需要
//			if (("1".equals(ParameterUtil.getParameter(Gl_jgid, "ZYFPSFZCGY",
//					ctx)) && li_BillType == 1)
//					|| ("1".equals(ParameterUtil.getParameter(Gl_jgid,
//							"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
//				parameters.remove("ls_UserId");
//				lyrqsql = "SELECT MIN(LYRQ) as LYRQ FROM JC_YGPJ WHERE PJLX = :li_BillType AND SYBZ = 0 AND JGID = :Gl_jgid";
//			}
			LYRQ = dao.doLoad(lyrqsql, parameters);
			if (LYRQ != null && LYRQ.get("LYRQ") != null) {
				Date ldt_lyrq = (Date) LYRQ.get("LYRQ");
				parameters.put("ldt_lyrq", ldt_lyrq);
				String dqhmsql = "SELECT DQHM as DQHM FROM JC_YGPJ WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_lyrq AND JGID = :Gl_jgid";
				if (("1".equals(ParameterUtil.getParameter(Gl_jgid,
						"ZYFPSFZCGY", ctx)) && li_BillType == 1)
						|| ("1".equals(ParameterUtil.getParameter(Gl_jgid,
								"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
					parameters.remove("ls_UserId");
					dqhmsql = "SELECT DQHM as DQHM FROM JC_YGPJ WHERE PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_lyrq AND JGID = :Gl_jgid";
				}
				List<Map<String, Object>> listDQHM = dao.doQuery(dqhmsql,
						parameters);
				if (listDQHM != null && listDQHM.size() > 0) {
					Map<String, Object> DQHM = listDQHM.get(0);
					if (DQHM != null && DQHM.get("DQHM") != null) {
						as_BillNumber = MedicineUtils.parseString( DQHM.get("DQHM"));
						return as_BillNumber;
					}
				}
			} else {
				as_BillNumber = "";
				return "false";
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("发票号码查询失败!", e);
		}
		as_BillNumber = as_BillNumber.trim();
		if (as_BillNumber == null)
			return "false";
		if (as_BillNumber.length() == 0)
			return "false";
		return as_BillNumber;
	}

	public static boolean SetBillNumber(String as_BillType,
			String as_BillNumber, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		String ls_UserId = user.getUserId();
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		// String ls_UserId = user.getId();
		int li_BillType = 1;
		if ("发票".equals(as_BillType)) {
			li_BillType = 1;
		} else if ("收据".equals(as_BillType)) {
			li_BillType = 2;
		}
		String ls_OldBillNumber = as_BillNumber; // 保存更新前的号码
		try {
			// 定位唯一记录 （当前使用的记录),使用该条件来控制单条记录更新
			Map<String, Object> ZY_YGPJparameters = new HashMap<String, Object>();
			ZY_YGPJparameters.put("ls_OldBillNumber", ls_OldBillNumber);
			ZY_YGPJparameters.put("li_BillType", li_BillType);
			ZY_YGPJparameters.put("gl_jgid", gl_jgid);
			List<Map<String, Object>> listZY_YGPJ = dao
					.doQuery(
							"SELECT LYRQ as LYRQ,QSHM as QSHM,ZZHM as ZZHM FROM ZY_YGPJ WHERE DQHM = :ls_OldBillNumber AND PJLX = :li_BillType AND SYBZ = 0 and JGID = :gl_jgid",
							ZY_YGPJparameters);
			Date ldt_Lyrq = new Date();
			// String ls_qshm = "";
			// String ls_zzhm = "";
			if (listZY_YGPJ != null && listZY_YGPJ.size() > 0) {
				Map<String, Object> ZY_YGPJ = listZY_YGPJ.get(0);
				ldt_Lyrq = (Date) ZY_YGPJ.get("LYRQ");
				// ls_qshm = ZY_YGPJ.get("QSHM") + "";
				// ls_zzhm = ZY_YGPJ.get("ZZHM") + "";
			}
			// 检查票据是否已用完
			Map<String, Object> countparameters = new HashMap<String, Object>();
			countparameters.put("li_BillType", li_BillType);
			countparameters.put("ldt_Lyrq", ldt_Lyrq);
			countparameters.put("ls_UserId", ls_UserId + "");
			String wherecnd = "YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND DQHM >= ZZHM AND LYRQ = :ldt_Lyrq";
			if (("1".equals(ParameterUtil.getParameter(gl_jgid, "ZYFPSFZCGY",
					ctx)) && li_BillType == 1)
					|| ("1".equals(ParameterUtil.getParameter(gl_jgid,
							"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
				countparameters.remove("ls_UserId");
				wherecnd = "PJLX = :li_BillType AND SYBZ = 0 AND DQHM >= ZZHM AND LYRQ = :ldt_Lyrq";
			}
			long li_Count = dao.doCount("ZY_YGPJ", wherecnd, countparameters);
			if (li_Count == 0) {
				int li_NumLength = as_BillNumber.length();// 保存当前号码长度
				int slen = 0;
				for (int i = 0; i < li_NumLength; i++) {
					char n = as_BillNumber.charAt(i);
					if (n >= '0' && n <= '9') {
						slen = i;
						break;
					}
				}
				String zm = as_BillNumber.substring(0, slen);
				String sz = as_BillNumber.substring(slen);
				long intnewsz = Long.parseLong(sz) + 1;
				String newssz = String.format("%0" + (li_NumLength - slen)
						+ "d", intnewsz);
				String new_BillNumber = zm + newssz;
				// 更新
				// int li_NumLength = as_BillNumber.length(); // 保存当前号码长度
				// as_BillNumber = Long.parseLong(as_BillNumber) + 1 + "";//
				// 号码加一
				// as_BillNumber = String.format("%0" + li_NumLength + "d",
				// Integer.parseInt(as_BillNumber + ""));
				Map<String, Object> ZY_YGPJUPparameters = new HashMap<String, Object>();
				ZY_YGPJUPparameters.put("as_BillNumber", new_BillNumber);
				ZY_YGPJUPparameters.put("ls_UserId", ls_UserId + "");
				ZY_YGPJUPparameters.put("li_BillType", li_BillType);
				ZY_YGPJUPparameters.put("ldt_Lyrq", ldt_Lyrq);
				String updatedqhmsql = "UPDATE ZY_YGPJ SET DQHM = :as_BillNumber WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
				if (("1".equals(ParameterUtil.getParameter(gl_jgid,
						"ZYFPSFZCGY", ctx)) && li_BillType == 1)
						|| ("1".equals(ParameterUtil.getParameter(gl_jgid,
								"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
					ZY_YGPJUPparameters.remove("ls_UserId");
					updatedqhmsql = "UPDATE ZY_YGPJ SET DQHM = :as_BillNumber WHERE PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
				}
				dao.doUpdate(updatedqhmsql, ZY_YGPJUPparameters);
				return true;
			}
			Map<String, Object> ZY_YGPJUPparameters1 = new HashMap<String, Object>();
			ZY_YGPJUPparameters1.put("ls_UserId", ls_UserId + "");
			ZY_YGPJUPparameters1.put("li_BillType", li_BillType);
			ZY_YGPJUPparameters1.put("ldt_Lyrq", ldt_Lyrq);
			String updatesybzsql = "UPDATE ZY_YGPJ SET SYBZ = 1 WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
			if (("1".equals(ParameterUtil.getParameter(gl_jgid, "ZYFPSFZCGY",
					ctx)) && li_BillType == 1)
					|| ("1".equals(ParameterUtil.getParameter(gl_jgid,
							"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
				ZY_YGPJUPparameters1.remove("ls_UserId");
				updatesybzsql = "UPDATE ZY_YGPJ SET SYBZ = 1 WHERE PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
			}
			dao.doUpdate(updatesybzsql, ZY_YGPJUPparameters1);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新" + as_BillType + "号码失败!",
					e);
		}
		return true;
	}
	
	public static boolean SetFsbBillNumber(String as_BillType,
			String as_BillNumber, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		String ls_UserId = user.getUserId();
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		// String ls_UserId = user.getId();
		int li_BillType = 1;
		if ("发票".equals(as_BillType)) {
			li_BillType = 1;
		} else if ("收据".equals(as_BillType)) {
			li_BillType = 2;
		}
		String ls_OldBillNumber = as_BillNumber; // 保存更新前的号码
		try {
			// 定位唯一记录 （当前使用的记录),使用该条件来控制单条记录更新
			Map<String, Object> ZY_YGPJparameters = new HashMap<String, Object>();
			ZY_YGPJparameters.put("ls_OldBillNumber", ls_OldBillNumber);
			ZY_YGPJparameters.put("li_BillType", li_BillType);
			ZY_YGPJparameters.put("gl_jgid", gl_jgid);
			List<Map<String, Object>> listZY_YGPJ = dao
					.doQuery(
							"SELECT LYRQ as LYRQ,QSHM as QSHM,ZZHM as ZZHM FROM JC_YGPJ WHERE DQHM = :ls_OldBillNumber AND PJLX = :li_BillType AND SYBZ = 0 and JGID = :gl_jgid",
							ZY_YGPJparameters);
			Date ldt_Lyrq = new Date();
			// String ls_qshm = "";
			// String ls_zzhm = "";
			if (listZY_YGPJ != null && listZY_YGPJ.size() > 0) {
				Map<String, Object> ZY_YGPJ = listZY_YGPJ.get(0);
				ldt_Lyrq = (Date) ZY_YGPJ.get("LYRQ");
				// ls_qshm = ZY_YGPJ.get("QSHM") + "";
				// ls_zzhm = ZY_YGPJ.get("ZZHM") + "";
			}
			// 检查票据是否已用完
			Map<String, Object> countparameters = new HashMap<String, Object>();
			countparameters.put("li_BillType", li_BillType);
			countparameters.put("ldt_Lyrq", ldt_Lyrq);
			countparameters.put("ls_UserId", ls_UserId + "");
			String wherecnd = "YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND DQHM >= ZZHM AND LYRQ = :ldt_Lyrq";
			if (("1".equals(ParameterUtil.getParameter(gl_jgid, "ZYFPSFZCGY",
					ctx)) && li_BillType == 1)
					|| ("1".equals(ParameterUtil.getParameter(gl_jgid,
							"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
				countparameters.remove("ls_UserId");
				wherecnd = "PJLX = :li_BillType AND SYBZ = 0 AND DQHM >= ZZHM AND LYRQ = :ldt_Lyrq";
			}
			long li_Count = dao.doCount("JC_YGPJ", wherecnd, countparameters);
			if (li_Count == 0) {
				int li_NumLength = as_BillNumber.length();// 保存当前号码长度
				int slen = 0;
				for (int i = 0; i < li_NumLength; i++) {
					char n = as_BillNumber.charAt(i);
					if (n >= '0' && n <= '9') {
						slen = i;
						break;
					}
				}
				String zm = as_BillNumber.substring(0, slen);
				String sz = as_BillNumber.substring(slen);
				long intnewsz = Long.parseLong(sz) + 1;
				String newssz = String.format("%0" + (li_NumLength - slen)
						+ "d", intnewsz);
				String new_BillNumber = zm + newssz;
				// 更新
				// int li_NumLength = as_BillNumber.length(); // 保存当前号码长度
				// as_BillNumber = Long.parseLong(as_BillNumber) + 1 + "";//
				// 号码加一
				// as_BillNumber = String.format("%0" + li_NumLength + "d",
				// Integer.parseInt(as_BillNumber + ""));
				Map<String, Object> ZY_YGPJUPparameters = new HashMap<String, Object>();
				ZY_YGPJUPparameters.put("as_BillNumber", new_BillNumber);
				ZY_YGPJUPparameters.put("ls_UserId", ls_UserId + "");
				ZY_YGPJUPparameters.put("li_BillType", li_BillType);
				ZY_YGPJUPparameters.put("ldt_Lyrq", ldt_Lyrq);
				String updatedqhmsql = "UPDATE JC_YGPJ SET DQHM = :as_BillNumber WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
				if (("1".equals(ParameterUtil.getParameter(gl_jgid,
						"ZYFPSFZCGY", ctx)) && li_BillType == 1)
						|| ("1".equals(ParameterUtil.getParameter(gl_jgid,
								"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
					ZY_YGPJUPparameters.remove("ls_UserId");
					updatedqhmsql = "UPDATE JC_YGPJ SET DQHM = :as_BillNumber WHERE PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
				}
				dao.doUpdate(updatedqhmsql, ZY_YGPJUPparameters);
				return true;
			}
			Map<String, Object> ZY_YGPJUPparameters1 = new HashMap<String, Object>();
			ZY_YGPJUPparameters1.put("ls_UserId", ls_UserId + "");
			ZY_YGPJUPparameters1.put("li_BillType", li_BillType);
			ZY_YGPJUPparameters1.put("ldt_Lyrq", ldt_Lyrq);
			String updatesybzsql = "UPDATE JC_YGPJ SET SYBZ = 1 WHERE YGDM = :ls_UserId AND PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
			if (("1".equals(ParameterUtil.getParameter(gl_jgid, "ZYFPSFZCGY",
					ctx)) && li_BillType == 1)
					|| ("1".equals(ParameterUtil.getParameter(gl_jgid,
							"JKSJSFZCGY", ctx)) && li_BillType == 2)) {
				ZY_YGPJUPparameters1.remove("ls_UserId");
				updatesybzsql = "UPDATE JC_YGPJ SET SYBZ = 1 WHERE PJLX = :li_BillType AND SYBZ = 0 AND LYRQ = :ldt_Lyrq";
			}
			dao.doUpdate(updatesybzsql, ZY_YGPJUPparameters1);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新" + as_BillType + "号码失败!",
					e);
		}
		return true;
	}

	// 医嘱提交
		/**
		 * collardrugdetailslist 里是用于提交的药品数据集合 parameters 领药日期
		 * modify by yangl 医嘱执行时间根据医嘱序号返回到Map对象中
		 */
		public static void uf_yztj(List<Map<String, Object>> collardrugdetailslist,
				Map<Long,List<Date>> qrsjMap, Map<String, Object> parameters,
				BaseDAO dao, Context ctx) throws ModelDataOperationException {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			int ll_fycs_total = 0;
			try {
				Date adt_ylrq = sdfdate.parse(sdfdate.format(parameters
						.get("ldt_server")));
				List<Map<String, Object>> listGY_SYPC = u_his_share_yzzxsj(
						parameters, dao, ctx);
				for (int ll_row = 0; ll_row < collardrugdetailslist.size(); ll_row++) {
					List<Date> ldt_qrsj_list = new ArrayList<Date>();
					Long ll_yzxh = Long.parseLong(collardrugdetailslist.get(ll_row)
							.get("JLXH") + ""); // 医嘱序号
					String ldt_kssj = null;
					if (collardrugdetailslist.get(ll_row).get("KSSJ") != null) {
						ldt_kssj = collardrugdetailslist.get(ll_row).get("KSSJ")
								+ ""; // 开始时间
					}
					String ldt_tzsj = null;
					if (collardrugdetailslist.get(ll_row).get("TZSJ") != null) {
						ldt_tzsj = collardrugdetailslist.get(ll_row).get("TZSJ")
								+ "";// 停止时间
					}
					String ldt_qrsj = null;
					if (collardrugdetailslist.get(ll_row).get("QRSJ") != null) {
						ldt_qrsj = collardrugdetailslist.get(ll_row).get("QRSJ")
								+ "";
					}// 确认时间
					String ls_sypc = collardrugdetailslist.get(ll_row).get("SYPC")
							+ "";// 使用频次

					int ll_lsyz = Integer.parseInt(collardrugdetailslist
							.get(ll_row).get("LSYZ") + "");// 临时医嘱标志

					String ls_yzzxsj_str = collardrugdetailslist.get(ll_row).get(
							"YZZXSJ")
							+ "";// 医嘱执行时间字符串
					int ll_yfbz = Integer.parseInt(collardrugdetailslist
							.get(ll_row).get("YFBZ") + "");// 药房包装
					int ll_srcs = Integer.parseInt(collardrugdetailslist
							.get(ll_row).get("SRCS") + ""); // 首日次数
					int ll_xmlx = Integer.parseInt(collardrugdetailslist
							.get(ll_row).get("XMLX") + "");// 项目类型
					Double ycsl = Double.parseDouble(collardrugdetailslist.get(
							ll_row).get("YCSL")
							+ ""); // 首日次数
					Map<String, Object> sjparameters = new HashMap<String, Object>();
					sjparameters.put("ldt_kssj", ldt_kssj);
					sjparameters.put("ldt_qrsj", ldt_qrsj);
					sjparameters.put("ldt_tzsj", ldt_tzsj);
					sjparameters.put("ls_sypc", ls_sypc);
					sjparameters.put("ls_yzzxsj_str", ls_yzzxsj_str);
					sjparameters.put("ll_lsyz", ll_lsyz);
					sjparameters.put("al_ypbz", 1);
					if (ll_yfbz + "" == "" || ll_yfbz == 0) {
						ll_yfbz = 1;
					}
					int ll_lsbz = uf_cacl_lsbz(listGY_SYPC, sjparameters, dao, ctx);
					if (ll_lsbz == 1) {
						parameters.put("ll_yzxh", ll_yzxh);
						uf_update_lsbz(parameters, dao, ctx);
						collardrugdetailslist.get(ll_row).put("FYCS", 0);
						continue;
					}
					parameters.put("ldt_kssj", ldt_kssj);
					parameters.put("ldt_qrsj", ldt_qrsj);
					parameters.put("ldt_tzsj", ldt_tzsj);
					parameters.put("adt_ylrq", adt_ylrq);
					parameters.put("ll_srcs", ll_srcs);
					parameters.put("ls_sypc", ls_sypc);
					parameters.put("ls_yzzxsj_str", ls_yzzxsj_str);
					parameters.put("al_fybz", 1);
					// 得到发药次数
					// 长期医嘱
					if (ll_lsyz == 0) {
						ll_fycs_total = uf_cacl_fycs_cq(listGY_SYPC, parameters,
								ldt_qrsj_list, dao, ctx);
					} else {// 临时医嘱
						if (ll_xmlx != 3) {
							ll_fycs_total = uf_cacl_fycs_ls(listGY_SYPC,
									parameters, ldt_qrsj_list, dao, ctx);
						}
					}
					if (ll_xmlx == 3) {
						collardrugdetailslist.get(ll_row).put("FYCS", 1);
						collardrugdetailslist
								.get(ll_row)
								.put("JE",
										String.format(
												"%1$.2f",
												ycsl
														* 1
														* Double.parseDouble(collardrugdetailslist
																.get(ll_row).get(
																		"YPDJ")
																+ "")));
					} else {
						collardrugdetailslist.get(ll_row)
								.put("FYCS", ll_fycs_total);
						collardrugdetailslist
								.get(ll_row)
								.put("JE",
										String.format(
												"%1$.2f",
												ycsl
														* ll_fycs_total
														* Double.parseDouble(collardrugdetailslist
																.get(ll_row).get(
																		"YPDJ")
																+ "")));
					}
					qrsjMap.put(ll_yzxh, ldt_qrsj_list);
				}
			} catch (ParseException e) {
				throw new ModelDataOperationException("时间转换失败!", e);
			}
		}

	// 计算历史标志
	/**
	 * 入参： parameters里的参数 datetime adt_kssj 开嘱时间，adt_qrsj 确认时间，adt_tzsj
	 * 停嘱时间，string as_sypc 频次编码，as_yzzxsj_str执行时间字符串，long al_lsyz 临时医嘱标志 出参：int
	 * lsbz 历史标志 1:历史医嘱 0:正常医嘱
	 */
	public static int uf_cacl_lsbz(List<Map<String, Object>> listGY_SYPC,
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date adt_kssj = null;
			if (parameters.get("ldt_kssj") != null) {
				adt_kssj = sdfdatetime.parse(parameters.get("ldt_kssj") + "");
			}
			Date adt_qrsj = null;
			if (parameters.get("ldt_qrsj") != null) {
				adt_qrsj = sdfdatetime.parse(parameters.get("ldt_qrsj") + "");
			}
			Date adt_tzsj = null;
			if (parameters.get("ldt_tzsj") != null) {
				adt_tzsj = sdfdatetime.parse(parameters.get("ldt_tzsj") + "");
			}
			String as_sypc = parameters.get("ls_sypc") + "";
			String as_yzzxsj_str = parameters.get("ls_yzzxsj_str") + "";
			Long al_lsyz = null;
			if (parameters.get("ll_lsyz") != null) {
				al_lsyz = Long.parseLong(parameters.get("ll_lsyz") + "");
			}
			// int al_ypbz = 0;
			// if (parameters.get("al_ypbz") != null) {
			// al_ypbz = Integer.parseInt(parameters.get("al_ypbz") + "");
			// }
			if (al_lsyz == 1) {
				if (adt_qrsj == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (adt_tzsj == null) {
				return 0;
			}
			if (adt_qrsj == null) {
				adt_qrsj = adt_kssj;
			}
			if (adt_qrsj.getTime() >= adt_tzsj.getTime()) {
				return 1;
			}
			for (int i = 0; i < listGY_SYPC.size(); i++) {
				if ((listGY_SYPC.get(i).get("PCBM") + "").equals(as_sypc)) {
					if (as_yzzxsj_str == null || as_yzzxsj_str == ""
							|| "0".equals(as_yzzxsj_str)) {
						as_yzzxsj_str = listGY_SYPC.get(i).get("ZXSJ") + "";
					}
					List<String> ls_zxsjlist = new ArrayList<String>();
					// 将执行时间字符串转换成执行时间列表
					parameters.put("as_yzzxsj_str", as_yzzxsj_str);
					uf_get_zxsj_list(parameters, ls_zxsjlist, dao, ctx);
					// 获取每日次数
					int ll_mrcs = Integer.parseInt(listGY_SYPC.get(i).get(
							"MRCS")
							+ "");
					// 计算总的需要计算的天数
					int ll_total_ts = BSHISUtil.getPeriod(adt_qrsj, adt_tzsj) + 1;
					// 计算每一天的执行次数 for表示遍历
					for (int ll_ts = 0; ll_ts < ll_total_ts; ll_ts++) {
						// 获取日期
						Date ldt_current = BSHISUtil.getDateAfter(adt_qrsj,
								ll_ts);
						parameters.put("as_sypc", as_sypc);
						parameters.put("adt_kssj", adt_kssj);
						parameters.put("ldt_current", ldt_current);
						int bol = uf_check_zx(listGY_SYPC, parameters, dao, ctx);
						if (bol <= 0) {
							continue;
						}
						for (int ll_zxcs = 0; ll_zxcs < ll_mrcs; ll_zxcs++) {
							Date ldt_zxsj = sdfdatetime.parse(sdfdate
									.format(ldt_current)
									+ " "
									+ ls_zxsjlist.get(ll_zxcs));
							if (ldt_zxsj.getTime() > adt_qrsj.getTime()
									&& ldt_zxsj.getTime() < adt_tzsj.getTime()) {
								return 0;
							}
						}
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("计算历史标志失败!", e);
		}
		return 1;
	}

	/**
	 * 将医嘱转为历史
	 * 
	 * @param params
	 *            {ZYH}
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static int uf_lsyz(List<Map<String, Object>> listGY_SYPC,
			Map<String, Object> params, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Long zyh = Long.parseLong(params.get("ZYH").toString());
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", zyh);
		try {
			List<Map<String, Object>> bryzs = dao
					.doQuery(
							"select JLXH as JLXH,YPLX as al_ypbz,KSSJ as ldt_kssj,QRSJ as ldt_qrsj,TZSJ as ldt_tzsj,LSYZ as ll_lsyz,SYPC as ls_sypc,YZZXSJ as ls_yzzxsj_str from "
									+ "ZY_BQYZ where ZYH=:ZYH and LSBZ<>1",// yangl历史医嘱转化时过滤YZPB<>4
							// + " where ZYH=:ZYH and LSBZ<>1 and YZPB<>4",//
							// yangl历史医嘱转化时过滤YZPB<>4
							parameters);

			for (Map<String, Object> bryz : bryzs) {
				int lsbz = uf_cacl_lsbz(listGY_SYPC, bryz, dao, ctx);
				if (lsbz == 1) {
					Map<String, Object> p = new HashMap<String, Object>();
					p.put("JLXH", bryz.get("JLXH"));
					dao.doUpdate("update ZY_BQYZ set LSBZ=1 where JLXH=:JLXH",
							p);
				}
			}

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("设置病人医嘱历史失败!", e);
		}
		return 1;
	}

	// 使用频次数据集
	public static List<Map<String, Object>> u_his_share_yzzxsj(
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ZY_YGPJparameters = new HashMap<String, Object>();
		List<Map<String, Object>> listGY_SYPC = new ArrayList<Map<String, Object>>();
		try {
			listGY_SYPC = dao
					.doQuery(
							"select PCBM as PCBM,PCMC as PCMC,MRCS as MRCS,ZXZQ as ZXZQ,RLZ as RLZ,ZXSJ as ZXSJ,RZXZQ as RZXZQ from GY_SYPC",
							ZY_YGPJparameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取使用频次数据集失败!", e);
		}
		return listGY_SYPC;
	}

	// 将执行时间字符串转换成执行时间列表
	/**
	 * 入参： parameters里的参数 string as_yzzxsj_str执行时间字符串，ref string
	 * ls_zxsjlist[]用于返回的执行时间列表数组（返回）
	 */
	public static void uf_get_zxsj_list(Map<String, Object> parameters,
			List<String> ls_zxsjlist, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String as_zxsj_str = parameters.get("as_yzzxsj_str") + "";
		String ls_zxsj = as_zxsj_str.trim();
		if (ls_zxsj != "" || ls_zxsj != null) {
			String[] ll_pos = ls_zxsj.split("-");
			for (int i = 0; i < ll_pos.length; i++) {
				if (ll_pos[i].indexOf(":") < 0) {
					ls_zxsjlist.add(ll_pos[i] + ":00:00");
				} else {
					ls_zxsjlist.add(ll_pos[i] + ":00");
				}
			}
		}
	}

	// 判断当前日期是否执行
	/**
	 * 入参：parameters里的参数 string as_sypc 使用频次datetime adt_kssj 开始时间(开嘱时间)
	 * datetime adt_dqrq 当前日期 出参： int ll_zxbz 1:需执行 0:不需执行 -1:有错误发生
	 */
	public static int uf_check_zx(List<Map<String, Object>> listGY_SYPC,
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		int ll_zxbz = 0;
		for (int i = 0; i < listGY_SYPC.size(); i++) {
			if (listGY_SYPC.get(i).get("PCBM").toString()
					.equals(parameters.get("as_sypc").toString())) {
				if (Integer.parseInt(listGY_SYPC.get(i).get("ZXZQ") + "") == 1) {
					ll_zxbz = 1;
					break;
				} else {
					// 取日执行周期
					int ll_zxzq = Integer.parseInt(listGY_SYPC.get(i).get(
							"ZXZQ")
							+ "");
					String ls_rzxzq = listGY_SYPC.get(i).get("RZXZQ") + "";
					if (ll_zxzq != ls_rzxzq.length()) {
						ll_zxbz = -1;
						break;
					} else {
						try {
							// 取执行标志
							if (Integer.parseInt(listGY_SYPC.get(i).get("RLZ")
									+ "") == 1) {
								int weknum = BSHISUtil
										.getWeekOfDate(sdfdatetime.parse(sdfdatetime
												.format(parameters
														.get("ldt_current"))));
								if (ls_rzxzq.length() != weknum) {
									ll_zxbz = Integer.parseInt(ls_rzxzq
											.substring(weknum - 1, weknum));
								} else {
									ll_zxbz = Integer.parseInt(ls_rzxzq
											.substring(weknum - 1));
								}
							} else {
								int ll_days = BSHISUtil.getPeriod(sdfdate
										.parse(sdfdatetime.format(parameters
												.get("adt_kssj"))), sdfdate
										.parse(sdfdatetime.format(parameters
												.get("ldt_current")))) + 1;
								ll_days = ll_days % ll_zxzq;
								if (ll_days == 0) {
									ll_days = ls_rzxzq.length() - 1;
								} else {
									ll_days = ll_days - 1;
								}
								if (ls_rzxzq.length() != ll_days) {
									ll_zxbz = Integer.parseInt(ls_rzxzq
											.substring(ll_days, ll_days + 1)); // 获取该天的执行标志
								} else {
									ll_zxbz = Integer.parseInt(ls_rzxzq
											.substring(ll_days)); // 获取该天的执行标志
								}
							}
						} catch (ParseException e) {
							throw new ModelDataOperationException(
									"当前日期是否可以执行失败!", e);
						}
						break;
					}
				}
			} else {
				ll_zxbz = -1;
			}
		}
		return ll_zxbz;
	}

	// 将指定医嘱转为历史医嘱
	/**
	 * 入参：parameters里的参数 long al_yzxh 医嘱序号
	 */
	public static void uf_update_lsbz(Map<String, Object> parameters,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String manageUnit = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();// 用户的机构ID
		try {
			Map<String, Object> parametersjlxh = new HashMap<String, Object>();
			if (parameters.get("ll_yzxh") != null) {
				parametersjlxh.put("JLXH",
						Long.parseLong(parameters.get("ll_yzxh") + ""));
			} else {
				parametersjlxh.put("JLXH", 0L);
			}
			parametersjlxh.put("JGID", manageUnit);
			dao.doUpdate(
					"update ZY_BQYZ set LSBZ=1 where JLXH=:JLXH and JGID =:JGID and YZPB<>4",
					parametersjlxh);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("将指定医嘱转为历史医嘱失败!", e);
		}

	}

	/**
	 * 入参：parameters里的参数 datetime adt_kssj 开嘱时间，adt_qrsj 确认时间，adt_tzsj
	 * 停嘱时间，adt_ylrq 预领日期，long 首日次数，string as_sypc
	 * 频次编码，as_yzzxsj_str执行时间字符串，long 发药方式， ref datetime adt_qrsj_list[]确认时间列表,
	 * long al_fybz (1:发药 2:退药) 出参：long ll_count 总的需执行的次数
	 */
	public static int uf_cacl_fycs_cq(List<Map<String, Object>> listGY_SYPC,
			Map<String, Object> parameters, List<Date> adt_qrsj_list,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		int ll_count = 0;
		try {
			Date adt_kssj = null;
			if (parameters.get("ldt_kssj") != null) {
				adt_kssj = sdfdatetime.parse(parameters.get("ldt_kssj") + "");
			}
			Date adt_qrsj = null;
			if (parameters.get("ldt_qrsj") != null) {
				adt_qrsj = sdfdatetime.parse(parameters.get("ldt_qrsj") + "");
			}
			Date adt_tzsj = null;
			if (parameters.get("ldt_tzsj") != null) {
				adt_tzsj = sdfdatetime.parse(parameters.get("ldt_tzsj") + "");
			}
			Date adt_ylrq = null;
			if (parameters.get("adt_ylrq") != null) {
				adt_ylrq = sdfdate.parse(sdfdate.format(parameters
						.get("adt_ylrq")));
			}
			int adt_srcs = 0;
			if (parameters.get("ll_srcs") != null) {
				adt_srcs = Integer.parseInt(parameters.get("ll_srcs") + "");
			}
			String as_sypc = parameters.get("ls_sypc") + "";
			String as_yzzxsj_str = parameters.get("ls_yzzxsj_str") + "";
			int al_fybz = 0;
			if (parameters.get("al_fybz") != null) {
				al_fybz = Integer.parseInt(parameters.get("al_fybz") + "");
			}
			for (int i = 0; i < listGY_SYPC.size(); i++) {
				List<String> ls_zxsjlist = new ArrayList<String>();
				if (listGY_SYPC.get(i).get("PCBM").toString().equals(as_sypc)) {
					if (as_yzzxsj_str == null || as_yzzxsj_str == ""
							|| "0".equals(as_yzzxsj_str)) {
						as_yzzxsj_str = listGY_SYPC.get(i).get("ZXSJ") + "";
					}
					// 将执行时间字符串转换成执行时间列表
					parameters.put("as_yzzxsj_str", as_yzzxsj_str);
					uf_get_zxsj_list(parameters, ls_zxsjlist, dao, ctx);
					// 获取每日次数
					int ll_mrcs = Integer.parseInt(listGY_SYPC.get(i).get(
							"MRCS")
							+ "");
					// 如果确认时间为空，则起始时间 = 开嘱时间，否则起始时间 = 确认时间
					Date ldt_qssj = new Date();
					if (adt_qrsj != null) {
						ldt_qssj = adt_qrsj;
					} else if (adt_kssj != null) {
						ldt_qssj = adt_kssj;
					}
					// 计算预领截止时间
					Date ldt_yljzsj = sdfdatetime
							.parse((sdfdate.format(BSHISUtil.getDateAfter(
									adt_ylrq, 1)) + " 00:00:00"));
					// 取预领截止时间和停嘱时间的较小者作为本次提交的终止时间
					Date ldt_zzsj = new Date();
					if (adt_tzsj == null
							|| ldt_yljzsj.getTime() < adt_tzsj.getTime()) {
						ldt_zzsj = ldt_yljzsj;
					} else {
						ldt_zzsj = adt_tzsj;
					}
					// 计算总的需要计算的天数 daysafter获取两个日期之前的天数
					int ll_total_ts = BSHISUtil.getPeriod(
							sdfdate.parse(sdfdatetime.format(ldt_qssj)),
							sdfdate.parse(sdfdatetime.format(ldt_zzsj))) + 1;
					Date ldt_zxsj = new Date();
					for (int ll_ts = 0; ll_ts < ll_total_ts; ll_ts++) {
						// 获取计算日期
						Date ldt_current = BSHISUtil.getDateAfter(ldt_qssj,
								ll_ts);
						// 该天不需要执行,有错误发生,也认为是不能执行
						parameters.put("as_sypc", as_sypc);
						parameters.put("adt_kssj", adt_kssj);
						parameters.put("ldt_current", ldt_current);
						int bol = uf_check_zx(listGY_SYPC, parameters, dao, ctx);
						if (bol <= 0)
							continue;
						// 首日（确认时间为空情况下的第一天）
						if (ll_ts == 0 && adt_qrsj == null) {
							// for表示遍历
							for (int ll_zxcs = (ll_mrcs - adt_srcs); ll_zxcs < ll_mrcs; ll_zxcs++) {
								ldt_zxsj = sdfdatetime.parse(sdfdate
										.format(ldt_current)
										+ " "
										+ ls_zxsjlist.get(ll_zxcs));
								adt_qrsj_list.add(ldt_zxsj);
								ll_count++;
							}
						} else {
							// 非首日
							// for表示遍历
							for (int ll_zxcs = 0; ll_zxcs < ll_mrcs; ll_zxcs++) {
								ldt_zxsj = sdfdatetime.parse(sdfdate
										.format(ldt_current)
										+ " "
										+ ls_zxsjlist.get(ll_zxcs));
								// 发药时是否执行的判断
								if (al_fybz == 1
										&& ldt_zxsj.getTime() > ldt_qssj
												.getTime()
										&& ldt_zxsj.getTime() < ldt_zzsj
												.getTime()) {
									adt_qrsj_list.add(ldt_zxsj);
									ll_count++;
								}

								// 退药时是否执行的判断
								if (al_fybz == 2
										&& ldt_zxsj.getTime() > ldt_qssj
												.getTime()
										&& ldt_zxsj.getTime() <= ldt_zzsj
												.getTime()) {
									adt_qrsj_list.add(ldt_zxsj);
									ll_count++;
								}
							}
						}
					}
				}
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException("长期医嘱得到发药次数失败!", e);
		}
		return ll_count;
	}

	/**
	 * 入参：parameters里的参数 datetime adt_kssj 开嘱时间，string as_sypc
	 * 频次编码，as_yzzxsj_str执行时间字符串，ref datetime adt_qrsj[]确认时间列表 出参：long ll_mrcs
	 * 总的需执行的次数
	 */
	public static int uf_cacl_fycs_ls(List<Map<String, Object>> listGY_SYPC,
			Map<String, Object> parameters, List<Date> adt_qrsj_list,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		int ll_mrcs = 0;
		try {
			Date adt_kssj = null;
			if (parameters.get("ldt_kssj") != null) {
				adt_kssj = sdfdatetime.parse(parameters.get("ldt_kssj") + "");
			}
			String as_sypc = parameters.get("ls_sypc") + "";
			String as_yzzxsj_str = parameters.get("ls_yzzxsj_str") + "";
			for (int i = 0; i < listGY_SYPC.size(); i++) {
				List<String> ls_zxsjlist = new ArrayList<String>();
				if (listGY_SYPC.get(i).get("PCBM").toString().equals(as_sypc)) {
					if (as_yzzxsj_str == null || as_yzzxsj_str == ""
							|| "0".equals(as_yzzxsj_str)) {
						as_yzzxsj_str = listGY_SYPC.get(i).get("ZXSJ") + "";
					}
					// 将执行时间字符串转换成执行时间列表
					parameters.put("as_yzzxsj_str", as_yzzxsj_str);
					uf_get_zxsj_list(parameters, ls_zxsjlist, dao, ctx);
					// 获取每日次数
					ll_mrcs = Integer.parseInt(listGY_SYPC.get(i).get("MRCS")
							+ "");
					// 遍历每日次数，将开嘱时间和执行时间字符数组中的执行时间组成一个时间加入adt_qrsj确认时间中
					// for表示遍历
					Date ldt_zxsj = new Date();
					for (int ll_zxcs = 0; ll_zxcs < ll_mrcs; ll_zxcs++) {
						ldt_zxsj = sdfdatetime.parse(sdfdate.format(adt_kssj)
								+ " " + ls_zxsjlist.get(ll_zxcs));
						adt_qrsj_list.add(ldt_zxsj);
					}
				}
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException("临时医嘱得到发药次数失败!", e);
		}
		return ll_mrcs;
	}

	public static Map<String, Object> getzfbl(Map<String, Object> body,
			Context ctx, BaseDAO dao) throws ModelDataOperationException {
		long al_yplx = Long.parseLong(body.get("TYPE") + "");// 药品传药品类型1,2,3,费用传0
		long al_brxz = Long.parseLong(body.get("BRXZ") + "");// 病人性质
		long al_fyxh = Long.parseLong(body.get("FYXH") + "");// 费用序号
		long al_fygb = Long.parseLong(body.get("FYGB") + "");// 费用归并
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BRXZ", al_brxz);
		params.put("FYXH", al_fyxh);
		Map<String, Object> reMap = new HashMap<String, Object>();
		reMap.put("FYGB", al_fygb);
		reMap.put("ZFBL", 1);
		String hql = "";
		if (al_yplx == 0) {
			// 项目
			hql = "Select ZFBL as ZFBL From GY_FYJY Where BRXZ= :BRXZ And FYXH= :FYXH";
		} else {
			hql = "Select ZFBL as ZFBL From GY_YPJY Where BRXZ= :BRXZ And YPXH= :FYXH";
		}
		Map<String, Object> map;
		try {
			map = dao.doLoad(hql, params);
			if (map == null) {
				if (al_fygb == 0) {
					al_fygb = getfygb(al_yplx, al_fyxh, dao, ctx);
					reMap.put("FYGB", al_fygb);
				}
				params.remove("FYXH");
				params.put("SFXM", al_fygb);
				Map<String, Object> map1 = dao
						.doLoad("Select ZFBL as ZFBL From GY_ZFBL Where BRXZ= :BRXZ And SFXM= :SFXM",
								params);
				if (map1 != null) {
					reMap.put("ZFBL", map1.get("ZFBL"));
				}
			} else {
				reMap.put("ZFBL", map.get("ZFBL"));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取自负比例失败!", e);
		}
		return reMap;
	}

	/**
	 * 长期医嘱项目执行次数
	 * 
	 * @param parameters
	 * @param adt_qrsj_list
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */

	public static int uf_cacl_zxcs_cq(List<Map<String, Object>> listGY_SYPC,
			Map<String, Object> parameters, Map<String, Object> parameters_cq,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		int ll_count = 0;
		try {
			Date adt_kssj = null;
			if (parameters.get("ldt_kssj") != null) {
				adt_kssj = sdfdatetime.parse(parameters.get("ldt_kssj") + "");
			}
			Date adt_qrsj = null;
			if (parameters.get("ldt_qrsj") != null) {
				adt_qrsj = sdfdatetime.parse(parameters.get("ldt_qrsj") + "");
			}
			Date adt_tzsj = null;
			if (parameters.get("ldt_tzsj") != null) {
				adt_tzsj = sdfdatetime.parse(parameters.get("ldt_tzsj") + "");
			}
			int SRCS = 0; // 首日次数
			if (parameters.get("SRCS") != null) {
				SRCS = Integer.parseInt(parameters.get("SRCS") + "");
			}

			String as_sypc = parameters.get("ls_sypc") + "";
			String as_yzzxsj_str = parameters.get("ls_yzzxsj_str") + "";

			for (int i = 0; i < listGY_SYPC.size(); i++) {
				List<String> ls_zxsjlist = new ArrayList<String>();
				int mrcs = Integer
						.parseInt(listGY_SYPC.get(i).get("MRCS") + "");

				if (listGY_SYPC.get(i).get("PCBM").toString().equals(as_sypc)) {
					if (as_yzzxsj_str == null || as_yzzxsj_str == ""
							|| "0".equals(as_yzzxsj_str)) {
						as_yzzxsj_str = listGY_SYPC.get(i).get("ZXSJ") + "";
					}
					// 将执行时间字符串转换成执行时间列表
					parameters.put("as_yzzxsj_str", as_yzzxsj_str);
					uf_get_zxsj_list(parameters, ls_zxsjlist, dao, ctx);

					// 如果确认时间为空，则起始时间 = 开嘱时间，否则起始时间 = 确认时间
					Date ldt_qssj = new Date();
					if (adt_qrsj != null) {
						ldt_qssj = adt_qrsj;
					} else if (adt_kssj != null) {
						ldt_qssj = adt_kssj;
					}
					// 截止时间为当前时间
					Date ldt_yljzsj = sdfdatetime
							.parse((sdfdate.format(BSHISUtil.getDateAfter(
									new Date(), 1)) + " 00:00:00"));
					// 截止时间和停嘱时间的较小者作为本次提交的终止时间
					Date ldt_zzsj = new Date();
					if (adt_tzsj == null
							|| ldt_yljzsj.getTime() < adt_tzsj.getTime()) {
						ldt_zzsj = ldt_yljzsj;
					} else {
						ldt_zzsj = adt_tzsj;
					}
					// 计算总的需要计算的天数 daysafter获取两个日期之前的天数
					int ll_total_ts = BSHISUtil.getPeriod(
							sdfdate.parse(sdfdatetime.format(ldt_qssj)),
							sdfdate.parse(sdfdatetime.format(ldt_zzsj))) + 1;

					// 计算每一天的执行次数
					int ll_zxcs_day = 0;
					Date ldt_zxsj = new Date();
					Date zxsj = new Date();

					// 计算每一天的执行次数
					for (int ll_ts = 0; ll_ts < ll_total_ts; ll_ts++) {
						// 获取计算日期
						Date ldt_current = BSHISUtil.getDateAfter(ldt_qssj,
								ll_ts);
						// 该天不需要执行,有错误发生,也认为是不能执行
						parameters.put("as_sypc", as_sypc);
						parameters.put("adt_kssj", adt_kssj);
						parameters.put("ldt_current", ldt_current);
						int bol = uf_check_zx(listGY_SYPC, parameters, dao, ctx);
						if (bol <= 0)
							continue;
						// 计算该天的执行次数
						if (ll_ts == 0 && adt_qrsj == null && SRCS != 0) {
							for (int j = mrcs - SRCS; j < mrcs; j++) {
								ll_zxcs_day++;
								ldt_zxsj = sdfdatetime.parse(sdfdate
										.format(ldt_current)
										+ " "
										+ ls_zxsjlist.get(j) + ":00");
								Date current = BSHISUtil.getDateAfter(ldt_zxsj,
										1);
								zxsj = sdfdatetime.parse(sdfdate
										.format(current) + " " + "00:00:00");
							}
						} else {
							for (int j = 0; j < mrcs; j++) {
								ls_zxsjlist.get(j);
								ldt_zxsj = sdfdatetime.parse(sdfdate
										.format(ldt_current)
										+ " "
										+ ls_zxsjlist.get(j) + ":00");
								if (ldt_zxsj.getTime() > ldt_qssj.getTime()) {
									if (ldt_zxsj.getTime() <= ldt_zzsj
											.getTime()) {
										ll_zxcs_day++;
										// zxsj = ldt_zxsj;
										Date current = BSHISUtil.getDateAfter(
												ldt_zxsj, 1);
										zxsj = sdfdatetime.parse(sdfdate
												.format(current)
												+ " "
												+ "00:00:00");
									}
								}
							}
						}
						if (ll_zxcs_day > 0) {
							ll_count++;
							parameters_cq.put("al_zxcs", ll_zxcs_day);
							parameters_cq.put("currentTime", zxsj);// 确认时间取医嘱的最后执行时间。
						}
					}
				}
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException("长期医嘱得到项目执行次数失败!", e);
		}
		return ll_count;
	}

	/**
	 * 计算医嘱执行
	 * 
	 * @param projectList
	 * @param parameters
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static boolean uf_comp_yzzx(List<Map<String, Object>> listGY_SYPC,
			List<Map<String, Object>> projectList, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			long ll_yzzh_old = 0;
			int fysx = 0;
			int ll_jfbz = 0;
			Date currentDate = null;
			/**
			 * 2013-08-20 modify by gejj
			 * 在病区项目执行中的附加计价显示时，计费标识为9时未进行过滤，通过张伟确认现修改如下代码 1、增加ll_yjxh变量
			 * 2、在sqlString中增加, min(YJXH) as LL_YJXH 3、增加ll_yjxh =
			 * Long.parseLong(list_fysx.get(0).get("LL_YJXH") + "");代码 4、将原有的if
			 * (ll_jfbz == 1) { 修改为if (ll_jfbz == 1 || (ll_yjxh > 0 && ll_jfbz
			 * == 9)) { 5、将原有的LL_JFBZ小写改成大小(共三处)
			 * **/
			long ll_yjxh = 0;
			for (int i = 0; i < projectList.size(); i++) {
				Map<String, Object> parameters_lsbz = new HashMap<String, Object>();
				Map<String, Object> parameters_up_lsbz = new HashMap<String, Object>();
				Map<String, Object> parameters_cq = new HashMap<String, Object>(); // 作为uf_cacl_zxcs_cq
																					// 的返回值

				String QRSJ = null;
				if (projectList.get(i).get("QRSJ") != null
						&& projectList.get(i).get("QRSJ") != "") {
					QRSJ = projectList.get(i).get("QRSJ") + "";// 确认时间
				}
				String TZSJ = null;
				if (projectList.get(i).get("TZSJ") != null
						&& projectList.get(i).get("TZSJ") != "") {
					TZSJ = projectList.get(i).get("TZSJ") + "";// 停医嘱时间
				}
				String KSSJ = null;
				if (projectList.get(i).get("KSSJ") != null
						&& projectList.get(i).get("KSSJ") != "") {
					KSSJ = projectList.get(i).get("KSSJ") + "";// 开始时间
				}
				String YZZXSJ = projectList.get(i).get("YZZXSJ") + "";// 医嘱执行时间
				String SYPC = projectList.get(i).get("SYPC") + "";// 使用频次
				long YZXH = Long.parseLong(projectList.get(i).get("JLXH") + "");// 医嘱序号
				int LSYZ = Integer
						.parseInt(projectList.get(i).get("LSYZ") + "");// 临时医嘱//
																		// 1,长期医嘱
																		// 0

				int SRCS = 0;
				if (projectList.get(i).get("SRCS") != null) {
					SRCS = Integer
							.parseInt(projectList.get(i).get("SRCS") + "");// 首日次数
				}
				int FJBZ = 0; // 附加计价标志
				if (projectList.get(0).get("FJBZ") != null) {
					FJBZ = Integer
							.parseInt(projectList.get(0).get("FJBZ") + "");// 首日次数
				}

				int ZXCS_TOTAL = 0;// 执行次数

				parameters_lsbz.put("ldt_kssj", KSSJ);
				parameters_lsbz.put("ldt_qrsj", QRSJ);
				parameters_lsbz.put("ldt_tzsj", TZSJ);
				parameters_lsbz.put("ls_sypc", SYPC);
				parameters_lsbz.put("ls_yzzxsj_str", YZZXSJ);
				parameters_lsbz.put("ll_lsyz", LSYZ);
				parameters_lsbz.put("al_ypbz", 0);
				parameters_lsbz.put("SRCS", SRCS);
				// 得到历史标志
				int ll_lsbz = uf_cacl_lsbz(listGY_SYPC, parameters_lsbz, dao,
						ctx);

				if (ll_lsbz == 1) {// 在执行前已经不再需要执行,即可置为历史医嘱
					parameters_up_lsbz.put("ll_yzxh", YZXH);
					uf_update_lsbz(parameters_up_lsbz, dao, ctx); // 更新历史标志
					projectList.get(i).put("FYCS", 0);
					continue;
				}
				// 计算附加计价执行的次数

				if (FJBZ == 1) {
					long ll_yzzh = Long.parseLong(projectList.get(i)
							.get("YZZH") + "");
					if (ll_yzzh_old != ll_yzzh) {
						ll_yjxh = 0;
						Map<String, Object> map_FY = new HashMap<String, Object>();
						map_FY.put(
								"YZZH",
								Long.parseLong(projectList.get(i).get("YZZH")
										+ ""));
						// 药品的附加项目必须在药品发药以后才可以执行
						StringBuffer sqlString = new StringBuffer(
								"SELECT to_char(MAX(QRSJ),'YYYY-MM-DD HH24:MI:SS') as LDT_QRSJ,MIN(FYSX) as LL_FYSX ,min(JFBZ) as LL_JFBZ, min(YJXH) as LL_YJXH FROM ZY_BQYZ WHERE YZZH=:YZZH and YZPB=0");
						List<Map<String, Object>> list_fysx = dao.doSqlQuery(
								sqlString.toString(), map_FY);
						fysx = Integer.parseInt(list_fysx.get(0).get("LL_FYSX")
								+ "");
						ll_yjxh = Long.parseLong(list_fysx.get(0)
								.get("LL_YJXH") + "");
						if (list_fysx.get(0).get("LL_JFBZ") != null) {
							ll_jfbz = Integer.parseInt(list_fysx.get(0).get(
									"LL_JFBZ")
									+ "");
						}
						if (list_fysx.get(0).get("LDT_QRSJ") != null) {
							currentDate = sdfdatetime.parse(list_fysx.get(0)
									.get("LDT_QRSJ") + "");
						} else {
							currentDate = null;
						}
					}
					ll_yzzh_old = Long.parseLong(projectList.get(i).get("YZZH")
							+ "");

					if (fysx == 2) {
						projectList.get(i).put("FYCS", 0);
						continue;
					} else {
						if (ll_jfbz == 1 || (ll_yjxh > 0 && ll_jfbz == 9)) {
							if (currentDate != null) {
								if (TZSJ != null
										&& BSHISUtil.toDate(TZSJ).getTime() > currentDate
												.getTime()) {
									TZSJ = sdfdatetime.format(currentDate);
								}
								parameters_lsbz.put("ldt_tzsj", TZSJ);
							} else {
								projectList.get(i).put("FYCS", 0);
								continue;
							}
						}
					}
				}
				if (LSYZ == 0) {// 长期医嘱
					ZXCS_TOTAL = uf_cacl_zxcs_cq(listGY_SYPC, parameters_lsbz,
							parameters_cq, dao, ctx);

					double al_zxcs = 0;
					if (parameters_cq.get("al_zxcs") != null) {
						al_zxcs = Double.parseDouble(parameters_cq
								.get("al_zxcs") + "");
					}
					projectList.get(i).put("FYCS", al_zxcs);
					if (ZXCS_TOTAL > 0) {
						String currentTime = null;
						if (parameters_cq.get("currentTime") != null) {
							currentTime = sdfdatetime.format(parameters_cq
									.get("currentTime"));
						}
						// 当前最大时间放入表中
						projectList.get(i).put("QRSJ", currentTime);
						// 把最大时间当做QRSJ传入 重新获取 ll_lsbz(历史标志)
						parameters_lsbz.put("ldt_qrsj", currentTime);
						ll_lsbz = uf_cacl_lsbz(listGY_SYPC, parameters_lsbz,
								dao, ctx);
						if (ll_lsbz == 1) {
							projectList.get(i).put("LSBZ", 1);
						}
					}
				} else { // 临时医嘱
					// 得到频次的每日次数
					int count_MRCS = 0;
					for (int j = 0; j < listGY_SYPC.size(); j++) {
						if (SYPC.equals(listGY_SYPC.get(j).get("PCBM"))) {
							count_MRCS = Integer.parseInt(listGY_SYPC.get(j)
									.get("MRCS") + "");
						}
					}
					if (count_MRCS > 0) {
						projectList.get(i).put("QRSJ", KSSJ);
						projectList.get(i).put("LSBZ", 1);
					}
					projectList.get(i).put("FYCS", count_MRCS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("时间转换失败!", e);
		}

		return true;
	}

	public static String get_public_fillleft(String format, String leftStr,
			int length) throws ModelDataOperationException {
		try {
			if (format.length() > Integer.parseInt(leftStr + length)) {
				return format;
			}
			long inNum = Long.parseLong(format);
			return String.format("%" + leftStr + length + "d", inNum);
		} catch (NumberFormatException e) {
			return format;
		}

	}

	public static String getRydjNo(String JGID, String key, String BZ,
			BaseDAO dao) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String reStr;
		parameters.put("CSMC", key);
		parameters.put("JGID", JGID);
		try {
			Map<String, Object> BAHM = dao
					.doLoad("select CSZ as CSZ from GY_XTCS where CSMC= :CSMC and JGID = :JGID",
							parameters);
			if (BAHM == null) {
				Map<String, Object> GY_XTCS = new HashMap<String, Object>();
				GY_XTCS.put("JGID", JGID);
				GY_XTCS.put("CSMC", key);
				GY_XTCS.put("MRZ", "0000000001");
				GY_XTCS.put("CSZ", "0000000001");
				GY_XTCS.put("BZ", BZ);
				reStr = "0000000001";
				dao.doInsert(BSPHISEntryNames.GY_XTCS, GY_XTCS, false);
			} else {
				reStr = MedicineUtils.parseString(BAHM.get("CSZ")) ;
			}
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住" + BZ + "失败");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取" + BZ + "失败");
		}
		return reStr;
	}
	
	public static String getFsbRydjNo(String JGID, String key, String BZ,
			BaseDAO dao) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String reStr;
		parameters.put("CSMC", key);
		parameters.put("JGID", JGID);
		try {
			Map<String, Object> BAHM = dao
					.doLoad("select CSZ as CSZ from GY_XTCS where CSMC= :CSMC and JGID = :JGID",
							parameters);
			if (BAHM == null) {
				Map<String, Object> GY_XTCS = new HashMap<String, Object>();
				GY_XTCS.put("JGID", JGID);
				GY_XTCS.put("CSMC", key);
				GY_XTCS.put("MRZ", "0000000001");
				GY_XTCS.put("CSZ", "0000000001");
				GY_XTCS.put("SSLB", "15");
				GY_XTCS.put("BZ", BZ);
				reStr = "0000000001";
				dao.doInsert(BSPHISEntryNames.GY_XTCS, GY_XTCS, false);
			} else {
				reStr = MedicineUtils.parseString(BAHM.get("CSZ")) ;
			}
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住" + BZ + "失败");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取" + BZ + "失败");
		}
		return reStr;
	}

	public static int wf_IsGather(Date adt_hzrq,
			Map<String, Object> idt_LastDate, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametershzrq = new HashMap<String, Object>();
		try {
			Date ldt_Begin = sdfdate.parse(sdfdate.format(adt_hzrq));
			Date ldt_End = sdfdate.parse(sdfdate.format(BSHISUtil.getDateAfter(
					adt_hzrq, 1)));
			parameters.put("ldt_Begin", ldt_Begin);
			parameters.put("ldt_End", ldt_End);
			parameters.put("gl_jgid", gl_jgid);
			parametershzrq.put("gl_jgid", gl_jgid);
			Long l = dao.doCount("ZY_JZHZ",
					"HZRQ>=:ldt_Begin AND HZRQ<:ldt_End and JGID=:gl_jgid",
					parameters);
			Map<String, Object> hzrqmap = dao
					.doLoad("SELECT str(MAX(HZRQ),'YYYY-MM-DD HH24:MI:SS') as HZRQ FROM ZY_JZHZ where JGID=:gl_jgid",
							parametershzrq);
			if (hzrqmap.get("HZRQ") != null) {
				idt_LastDate.put("idt_LastDate",
						sdfdatetime.parse(hzrqmap.get("HZRQ") + ""));
			}
			if (l > 0) {
				return 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int fsb_wf_IsGather(Date adt_hzrq,
			Map<String, Object> idt_LastDate, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametershzrq = new HashMap<String, Object>();
		try {
			Date ldt_Begin = sdfdate.parse(sdfdate.format(adt_hzrq));
			Date ldt_End = sdfdate.parse(sdfdate.format(BSHISUtil.getDateAfter(
					adt_hzrq, 1)));
			parameters.put("ldt_Begin", ldt_Begin);
			parameters.put("ldt_End", ldt_End);
			parameters.put("gl_jgid", gl_jgid);
			parametershzrq.put("gl_jgid", gl_jgid);
			Long l = dao.doCount("JC_JZHZ",
					"HZRQ>=:ldt_Begin AND HZRQ<:ldt_End and JGID=:gl_jgid",
					parameters);
			Map<String, Object> hzrqmap = dao
					.doLoad("SELECT str(MAX(HZRQ),'YYYY-MM-DD HH24:MI:SS') as HZRQ FROM JC_JZHZ where JGID=:gl_jgid",
							parametershzrq);
			if (hzrqmap.get("HZRQ") != null) {
				idt_LastDate.put("idt_LastDate",
						sdfdatetime.parse(hzrqmap.get("HZRQ") + ""));
			}
			if (l > 0) {
				return 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 
	 * @param adt_hzrq
	 *            汇总日期
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static void wf_Create_jzhz(Date adt_hzrq, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parametershzrq = new HashMap<String, Object>();
		Date ldt_sqhzrq = null; // 上期汇总日期
		parametershzrq.put("gl_jgid", gl_jgid);
		try {
			adt_hzrq = sdfdatetime
					.parse(sdfdate.format(adt_hzrq) + " 00:00:00");
			Map<String, Object> hzrqmap = dao
					.doLoad("SELECT str(MAX(HZRQ),'YYYY-MM-DD HH24:MI:SS') as HZRQ FROM ZY_JZHZ where JGID=:gl_jgid",
							parametershzrq);
			if (hzrqmap.get("HZRQ") != null) {
				ldt_sqhzrq = sdfdatetime.parse(hzrqmap.get("HZRQ") + "");
			}
			// １、取上期结存(sqjc)
			List<Map<String, Object>> lws_hzxxsqjc = new ArrayList<Map<String, Object>>();
			if (ldt_sqhzrq != null) {
				Map<String, Object> parametersbqye = new HashMap<String, Object>();
				parametersbqye.put("ldt_sqhzrq", ldt_sqhzrq);
				parametersbqye.put("gl_jgid", gl_jgid);
				Map<String, Object> mapbqye1 = dao
						.doLoad("SELECT BQYE as SQJC FROM ZY_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=1 and JGID=:gl_jgid",
								parametersbqye);
				if (mapbqye1.get("SQJC") != null) {
					lws_hzxxsqjc.add(0, mapbqye1);
				} else {
					mapbqye1.put("SQJC", 0);
					lws_hzxxsqjc.add(0, mapbqye1);
				}
				Map<String, Object> mapbqye2 = dao
						.doLoad("SELECT BQYE as SQJC FROM ZY_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=2 and JGID=:gl_jgid",
								parametersbqye);
				if (mapbqye2.get("SQJC") != null) {
					lws_hzxxsqjc.add(1, mapbqye2);
				} else {
					mapbqye2.put("SQJC", 0);
					lws_hzxxsqjc.add(1, mapbqye2);
				}
				Map<String, Object> mapbqye3 = dao
						.doLoad("SELECT BQYE as SQJC FROM ZY_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=3 and JGID=:gl_jgid",
								parametersbqye);
				if (mapbqye3 != null && mapbqye3.get("SQJC") != null) {
					lws_hzxxsqjc.add(2, mapbqye3);
				} else {
					mapbqye3 = new HashMap<String, Object>();
					mapbqye3.put("SQJC", 0);
					lws_hzxxsqjc.add(2, mapbqye3);
				}
				// Map<String, Object> mapbqye3 = dao
				// .doLoad("SELECT BQYE as SQJC FROM ZY_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=3 and JGID=:gl_jgid",
				// parametersbqye);
				// if (mapbqye3.get("SQJC") != null) {
				// lws_hzxxsqjc.add(2, mapbqye3);
				// } else {
				// mapbqye3.put("SQJC", 0);
				// lws_hzxxsqjc.add(2, mapbqye3);
				// }
			} else {
				Map<String, Object> mapbqye4 = new HashMap<String, Object>();
				mapbqye4.put("SQJC", 0);
				lws_hzxxsqjc.add(0, mapbqye4);
				lws_hzxxsqjc.add(1, mapbqye4);
				lws_hzxxsqjc.add(2, mapbqye4);
				// lws_hzxxsqjc.add(2, mapbqye4);
			}

			// ２、取本期发生(bqfs)
			List<Map<String, Object>> lws_hzxxbqfs = new ArrayList<Map<String, Object>>();
			Map<String, Object> parametersje = new HashMap<String, Object>();
			parametersje.put("adt_hzrq", adt_hzrq);
			parametersje.put("gl_jgid", gl_jgid);
			Map<String, Object> mapzjje1 = dao
					.doLoad("SELECT sum(ZJJE) as BQFS FROM ZY_FYMX WHERE HZRQ=:adt_hzrq and JGID=:gl_jgid",
							parametersje);
			if (mapzjje1.get("BQFS") != null) {
				lws_hzxxbqfs.add(0, mapzjje1);
			} else {
				mapzjje1.put("BQFS", 0);
				lws_hzxxbqfs.add(0, mapzjje1);
			}
			Map<String, Object> mapjkje2 = dao
					.doLoad("SELECT sum(JKJE) as BQFS FROM ZY_TBKK WHERE HZRQ=:adt_hzrq and JGID=:gl_jgid",
							parametersje);
			if (mapjkje2.get("BQFS") != null) {
				lws_hzxxbqfs.add(1, mapjkje2);
			} else {
				mapjkje2.put("BQFS", 0);
				lws_hzxxbqfs.add(1, mapjkje2);
			}
			Map<String, Object> mapjkje3 = new HashMap<String, Object>();
			mapjkje3.put("BQFS", 0);
			lws_hzxxbqfs.add(2, mapjkje3);
			// Map<String, Object> defaultmap = new HashMap<String, Object>();
			// defaultmap.put("BQFS", 0);
			// lws_hzxxbqfs.add(2, defaultmap);

			double lws_Temp1 = 0.00;
			Map<String, Object> calculatemap = dao
					.doLoad("SELECT sum(ZY_TBKK.JKJE) as BQFS FROM ZY_TBKK ZY_TBKK,ZY_JKZF ZY_JKZF WHERE ZY_TBKK.JKXH = ZY_JKZF.JKXH AND ZY_TBKK.JGID = ZY_JKZF.JGID AND ZY_JKZF.HZRQ=:adt_hzrq and ZY_TBKK.JGID=:gl_jgid",
							parametersje);
			if (calculatemap.get("BQFS") != null) {
				lws_Temp1 = Double.parseDouble(calculatemap.get("BQFS") + "");
			}
			lws_hzxxbqfs.get(1).put(
					"BQFS",
					Double.parseDouble(lws_hzxxbqfs.get(1).get("BQFS") + "")
							- lws_Temp1);

			// ３、取本期结算(bqjs)
			List<Map<String, Object>> lws_hzxxbqjs = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapfyhj = dao
					.doLoad("SELECT sum(FYHJ) as BQJS FROM ZY_ZYJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapfyhj.get("BQJS") != null) {
				lws_hzxxbqjs.add(0, mapfyhj);
			} else {
				mapfyhj.put("BQJS", 0);
				lws_hzxxbqjs.add(0, mapfyhj);
			}
			Map<String, Object> mapjkhj = dao
					.doLoad("SELECT sum(JKHJ) as BQJS FROM ZY_ZYJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapjkhj.get("BQJS") != null) {
				lws_hzxxbqjs.add(1, mapjkhj);
			} else {
				mapjkhj.put("BQJS", 0);
				lws_hzxxbqjs.add(1, mapjkhj);
			}
			Map<String, Object> mapbqjs3 = new HashMap<String, Object>();
			mapbqjs3.put("BQJS", 0);
			lws_hzxxbqjs.add(2, mapbqjs3);
			// Map<String, Object> defaultmap1 = new HashMap<String, Object>();
			// defaultmap1.put("BQJS", 0);
			// lws_hzxxbqjs.add(2, defaultmap1);

			double lws_Temp2 = 0.00;
			Map<String, Object> calculatemap1 = dao
					.doLoad("SELECT sum(ZY_ZYJS.FYHJ) as BQJS FROM ZY_ZYJS ZY_ZYJS,ZY_JSZF ZY_JSZF WHERE ZY_ZYJS.ZYH = ZY_JSZF.ZYH AND ZY_ZYJS.JSCS = ZY_JSZF.JSCS AND ZY_ZYJS.JGID = ZY_JSZF.JGID AND ZY_JSZF.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) and ZY_JSZF.JGID=:gl_jgid",
							parametersje);
			if (calculatemap1.get("BQJS") != null) {
				lws_Temp2 = Double.parseDouble(calculatemap1.get("BQJS") + "");
			}
			lws_hzxxbqjs.get(0).put(
					"BQJS",
					Double.parseDouble(lws_hzxxbqjs.get(0).get("BQJS") + "")
							- lws_Temp2);
			double lws_Temp6 = 0.00;
			Map<String, Object> calculatemap6 = dao
					.doLoad("SELECT sum(ZY_ZYJS.JKHJ) as BQJS FROM ZY_ZYJS ZY_ZYJS,ZY_JSZF ZY_JSZF WHERE ZY_ZYJS.ZYH = ZY_JSZF.ZYH AND ZY_ZYJS.JSCS = ZY_JSZF.JSCS AND ZY_ZYJS.JGID = ZY_JSZF.JGID AND ZY_JSZF.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) and ZY_JSZF.JGID=:gl_jgid",
							parametersje);
			if (calculatemap6.get("BQJS") != null) {
				lws_Temp6 = Double.parseDouble(calculatemap6.get("BQJS") + "");
			}
			lws_hzxxbqjs.get(1).put(
					"BQJS",
					Double.parseDouble(lws_hzxxbqjs.get(1).get("BQJS") + "")
							- lws_Temp6);

			// ４、取现金支票(xjzp)
			List<Map<String, Object>> lws_hzxxxjzp = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapzfhj = dao
					.doLoad("SELECT sum(ZFHJ) as XJZP FROM ZY_ZYJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapzfhj.get("XJZP") != null) {
				lws_hzxxxjzp.add(0, mapzfhj);
			} else {
				mapzfhj.put("XJZP", 0);
				lws_hzxxxjzp.add(0, mapzfhj);
			}
			Map<String, Object> mapjkhj1 = dao
					.doLoad("SELECT sum(JKHJ) as XJZP FROM ZY_ZYJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapjkhj1.get("XJZP") != null) {
				lws_hzxxxjzp.add(1, mapjkhj1);
			} else {
				mapjkhj1.put("XJZP", 0);
				lws_hzxxxjzp.add(1, mapjkhj1);
			}
			Map<String, Object> mapxjzp3 = new HashMap<String, Object>();
			mapxjzp3.put("XJZP", 0);
			lws_hzxxxjzp.add(2, mapxjzp3);
			// Map<String, Object> defaultmap2 = new HashMap<String, Object>();
			// defaultmap2.put("XJZP", 0);
			// lws_hzxxxjzp.add(2, defaultmap2);

			double lws_Temp3 = 0.00;
			Map<String, Object> calculatemap2 = dao
					.doLoad("SELECT sum(ZY_ZYJS.ZFHJ) as XJZP FROM ZY_ZYJS ZY_ZYJS,ZY_JSZF ZY_JSZF WHERE ZY_ZYJS.ZYH = ZY_JSZF.ZYH AND ZY_ZYJS.JSCS = ZY_JSZF.JSCS AND ZY_ZYJS.JGID = ZY_JSZF.JGID AND ZY_JSZF.HZRQ=:adt_hzrq  AND ZY_ZYJS.JSLX in(5,1) and ZY_ZYJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap2.get("XJZP") != null) {
				lws_Temp3 = Double.parseDouble(calculatemap2.get("XJZP") + "");
			}
			double lws_Temp4 = 0.00;
			Map<String, Object> calculatemap3 = dao
					.doLoad("SELECT sum(ZY_ZYJS.JKHJ) as XJZP FROM ZY_ZYJS ZY_ZYJS,ZY_JSZF ZY_JSZF WHERE ZY_ZYJS.ZYH = ZY_JSZF.ZYH AND ZY_ZYJS.JSCS = ZY_JSZF.JSCS AND ZY_ZYJS.JGID = ZY_JSZF.JGID AND ZY_JSZF.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) and ZY_ZYJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap3.get("XJZP") != null) {
				lws_Temp4 = Double.parseDouble(calculatemap3.get("XJZP") + "");
			}
			double yqfje = 0.00;
			double dqfje = 0.00;
			Map<String, Object> qfjemap3 = dao
					.doLoad("select sum(a.ZFHJ) as ZFHJ,sum(a.JKHJ) as JKHJ from ZY_ZYJS a WHERE a.HZRQ=:adt_hzrq and a.JSLX = 4 and a.JGID=:gl_jgid",
							parametersje);
			if (qfjemap3.get("ZFHJ") != null) {
				yqfje = Double.parseDouble(qfjemap3.get("JKHJ") + "");
				dqfje = Double.parseDouble(qfjemap3.get("ZFHJ") + "");
				lws_hzxxxjzp.get(0).put("QFJE", 0);
				lws_hzxxxjzp.get(1).put("QFJE", yqfje);
				lws_hzxxxjzp.get(2).put("QFJE", dqfje);
			} else {
				lws_hzxxxjzp.get(0).put("QFJE", 0);
				lws_hzxxxjzp.get(1).put("QFJE", 0);
				lws_hzxxxjzp.get(2).put("QFJE", 0);
			}
			lws_hzxxxjzp.get(0).put(
					"XJZP",
					Double.parseDouble(lws_hzxxxjzp.get(0).get("XJZP") + "")
							- lws_Temp3);
			lws_hzxxxjzp.get(1).put(
					"XJZP",
					Double.parseDouble(lws_hzxxxjzp.get(1).get("XJZP") + "")
							- lws_Temp4);
			// 5、取其它应收(qtys)
			List<Map<String, Object>> lws_hzxxqtys = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapzfhjzfhj = dao
					.doLoad("SELECT sum(FYHJ - ZFHJ) as QTYS FROM ZY_ZYJS ZY_ZYJS WHERE ZY_ZYJS.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) AND ZY_ZYJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB=0) and JGID=:gl_jgid",
							parametersje);
			if (mapzfhjzfhj.get("QTYS") != null) {
				lws_hzxxqtys.add(0, mapzfhjzfhj);
			} else {
				mapzfhjzfhj.put("QTYS", 0);
				lws_hzxxqtys.add(0, mapzfhjzfhj);
			}
			Map<String, Object> defaultmap3 = new HashMap<String, Object>();
			defaultmap3.put("QTYS", 0);
			lws_hzxxqtys.add(1, defaultmap3);
			lws_hzxxqtys.add(2, defaultmap3);
			// Map<String, Object> mapzfhjzfhj1 = dao
			// .doLoad("SELECT sum(FYHJ - ZFHJ) as QTYS FROM ZY_ZYJS ZY_ZYJS WHERE ZY_ZYJS.HZRQ=:adt_hzrq AND (ZY_ZYJS.JSLX=3) AND ZY_ZYJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB=0) and JGID=:gl_jgid",
			// parametersje);
			// if (mapzfhjzfhj1.get("QTYS") != null) {
			// lws_hzxxqtys.add(2, mapzfhjzfhj1);
			// } else {
			// mapzfhjzfhj1.put("QTYS", 0);
			// lws_hzxxqtys.add(2, mapzfhjzfhj1);
			// }
			double lws_Temp5 = 0.00;
			Map<String, Object> calculatemap4 = dao
					.doLoad("SELECT sum(ZY_ZYJS.FYHJ - ZY_ZYJS.ZFHJ) as QTYS FROM ZY_ZYJS ZY_ZYJS,ZY_JSZF ZY_JSZF WHERE ZY_ZYJS.ZYH=ZY_JSZF.ZYH AND ZY_ZYJS.JSCS=ZY_JSZF.JSCS AND ZY_ZYJS.JGID=ZY_JSZF.JGID AND ZY_JSZF.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) AND ZY_ZYJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB=0) and ZY_ZYJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap4.get("QTYS") != null) {
				lws_Temp5 = Double.parseDouble(calculatemap4.get("QTYS") + "");
			}
			lws_hzxxqtys.get(0).put(
					"QTYS",
					Double.parseDouble(lws_hzxxqtys.get(0).get("QTYS") + "")
							- lws_Temp5);
			// 6、取参保应收(cbys)
			List<Map<String, Object>> lws_hzxxcbys = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapfyhjfyhj = dao
					.doLoad("SELECT sum(FYHJ - ZFHJ) as CBYS FROM ZY_ZYJS ZY_ZYJS WHERE ZY_ZYJS.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) AND ZY_ZYJS.BRXZ IN ( SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB>0) and JGID=:gl_jgid",
							parametersje);
			if (mapfyhjfyhj.get("CBYS") != null) {
				lws_hzxxcbys.add(0, mapfyhjfyhj);
			} else {
				mapfyhjfyhj.put("CBYS", 0);
				lws_hzxxcbys.add(0, mapfyhjfyhj);
			}
			Map<String, Object> defaultmap4 = new HashMap<String, Object>();
			defaultmap4.put("CBYS", 0);
			lws_hzxxcbys.add(1, defaultmap4);
			lws_hzxxcbys.add(2, defaultmap4);
			// Map<String, Object> mapfyhjfyhj1 = dao
			// .doLoad("SELECT sum(FYHJ - ZFHJ) as CBYS FROM ZY_ZYJS ZY_ZYJS WHERE ZY_ZYJS.HZRQ=:adt_hzrq AND (ZY_ZYJS.JSLX=3) AND ZY_ZYJS.BRXZ IN ( SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB>0) and JGID=:gl_jgid",
			// parametersje);
			// if (mapfyhjfyhj1.get("CBYS") != null) {
			// lws_hzxxcbys.add(2, mapfyhjfyhj1);
			// } else {
			// mapfyhjfyhj1.put("CBYS", 0);
			// lws_hzxxcbys.add(2, mapfyhjfyhj1);
			// }
			double lws_Temp7 = 0.00;
			Map<String, Object> calculatemap5 = dao
					.doLoad("SELECT sum(ZY_ZYJS.FYHJ - ZY_ZYJS.ZFHJ) as CBYS FROM ZY_ZYJS ZY_ZYJS,ZY_JSZF ZY_JSZF WHERE ZY_ZYJS.ZYH  = ZY_JSZF.ZYH AND ZY_ZYJS.JSCS = ZY_JSZF.JSCS AND ZY_ZYJS.JGID = ZY_JSZF.JGID AND ZY_JSZF.HZRQ=:adt_hzrq AND ZY_ZYJS.JSLX in(5,1) AND ZY_ZYJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB>0) and ZY_ZYJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap5.get("CBYS") != null) {
				lws_Temp7 = Double.parseDouble(calculatemap5.get("CBYS") + "");
			}
			lws_hzxxcbys.get(0).put(
					"CBYS",
					Double.parseDouble(lws_hzxxcbys.get(0).get("CBYS") + "")
							- lws_Temp7);

			// 7、计算本期余额(bqye) = 上期结存 + 本期发生 - 本期结算
			List<Double> lws_hzxxbqye = new ArrayList<Double>();
			lws_hzxxbqye.add(
					0,
					Double.parseDouble(lws_hzxxsqjc.get(0).get("SQJC") + "")
							+ Double.parseDouble(lws_hzxxbqfs.get(0)
									.get("BQFS") + "")
							- Double.parseDouble(lws_hzxxbqjs.get(0)
									.get("BQJS") + ""));
			lws_hzxxbqye.add(
					1,
					Double.parseDouble(lws_hzxxsqjc.get(1).get("SQJC") + "")
							+ Double.parseDouble(lws_hzxxbqfs.get(1)
									.get("BQFS") + "")
							- Double.parseDouble(lws_hzxxbqjs.get(1)
									.get("BQJS") + ""));
			lws_hzxxbqye.add(
					2,
					Double.parseDouble(lws_hzxxsqjc.get(2).get("SQJC") + "")
							+ Double.parseDouble(lws_hzxxbqfs.get(2)
									.get("BQFS") + "")
							- Double.parseDouble(lws_hzxxbqjs.get(2)
									.get("BQJS") + ""));
			// lws_hzxxbqye.add(
			// 2,
			// Double.parseDouble(lws_hzxxsqjc.get(2).get("SQJC") + "")
			// + Double.parseDouble(lws_hzxxbqfs.get(2)
			// .get("BQFS") + "")
			// - Double.parseDouble(lws_hzxxbqjs.get(2)
			// .get("BQJS") + ""));
			// 7. 将汇总信息写入ZY_JZHZ
			for (int ll_row = 0; ll_row < 3; ll_row++) {
				Map<String, Object> zy_jzhzmap = new HashMap<String, Object>();
				zy_jzhzmap.put("HZRQ", adt_hzrq);
				zy_jzhzmap.put("XMBH", ll_row + 1);
				zy_jzhzmap.put("SQJC", lws_hzxxsqjc.get(ll_row).get("SQJC"));
				zy_jzhzmap.put("BQFS", lws_hzxxbqfs.get(ll_row).get("BQFS"));
				zy_jzhzmap.put(
						"BQJS",
						Double.parseDouble(lws_hzxxbqjs.get(ll_row).get("BQJS")
								+ "")
								+ Double.parseDouble(lws_hzxxxjzp.get(ll_row)
										.get("QFJE") + ""));
				zy_jzhzmap.put("XJZP", lws_hzxxxjzp.get(ll_row).get("XJZP"));
				zy_jzhzmap.put("CYDJ", 0);
				zy_jzhzmap.put("QFJE", lws_hzxxxjzp.get(ll_row).get("QFJE"));
				zy_jzhzmap.put("CBJE", lws_hzxxcbys.get(ll_row).get("CBYS"));
				zy_jzhzmap.put("QTJE", lws_hzxxqtys.get(ll_row).get("QTYS"));
				zy_jzhzmap.put(
						"BQYE",
						Double.parseDouble(lws_hzxxbqye.get(ll_row) + "")
								- Double.parseDouble(lws_hzxxxjzp.get(ll_row)
										.get("QFJE") + ""));
				zy_jzhzmap.put("YHJE", 0);
				zy_jzhzmap.put("JGID", gl_jgid);
				dao.doSave("create", BSPHISEntryNames.ZY_JZHZ, zy_jzhzmap,
						false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		}
	}
	
	
	/**
	 * 
	 * @param adt_hzrq
	 *            汇总日期
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static void fsb_wf_Create_jzhz(Date adt_hzrq, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parametershzrq = new HashMap<String, Object>();
		Date ldt_sqhzrq = null; // 上期汇总日期
		parametershzrq.put("gl_jgid", gl_jgid);
		try {
			adt_hzrq = sdfdatetime
					.parse(sdfdate.format(adt_hzrq) + " 00:00:00");
			Map<String, Object> hzrqmap = dao
					.doLoad("SELECT str(MAX(HZRQ),'YYYY-MM-DD HH24:MI:SS') as HZRQ FROM JC_JZHZ where JGID=:gl_jgid",
							parametershzrq);
			if (hzrqmap.get("HZRQ") != null) {
				ldt_sqhzrq = sdfdatetime.parse(hzrqmap.get("HZRQ") + "");
			}
			// １、取上期结存(sqjc)
			List<Map<String, Object>> lws_hzxxsqjc = new ArrayList<Map<String, Object>>();
			if (ldt_sqhzrq != null) {
				Map<String, Object> parametersbqye = new HashMap<String, Object>();
				parametersbqye.put("ldt_sqhzrq", ldt_sqhzrq);
				parametersbqye.put("gl_jgid", gl_jgid);
				Map<String, Object> mapbqye1 = dao
						.doLoad("SELECT BQYE as SQJC FROM JC_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=1 and JGID=:gl_jgid",
								parametersbqye);
				if (mapbqye1.get("SQJC") != null) {
					lws_hzxxsqjc.add(0, mapbqye1);
				} else {
					mapbqye1.put("SQJC", 0);
					lws_hzxxsqjc.add(0, mapbqye1);
				}
				Map<String, Object> mapbqye2 = dao
						.doLoad("SELECT BQYE as SQJC FROM JC_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=2 and JGID=:gl_jgid",
								parametersbqye);
				if (mapbqye2.get("SQJC") != null) {
					lws_hzxxsqjc.add(1, mapbqye2);
				} else {
					mapbqye2.put("SQJC", 0);
					lws_hzxxsqjc.add(1, mapbqye2);
				}
				Map<String, Object> mapbqye3 = dao
						.doLoad("SELECT BQYE as SQJC FROM JC_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=3 and JGID=:gl_jgid",
								parametersbqye);
				if (mapbqye3 != null && mapbqye3.get("SQJC") != null) {
					lws_hzxxsqjc.add(2, mapbqye3);
				} else {
					mapbqye3 = new HashMap<String, Object>();
					mapbqye3.put("SQJC", 0);
					lws_hzxxsqjc.add(2, mapbqye3);
				}
				// Map<String, Object> mapbqye3 = dao
				// .doLoad("SELECT BQYE as SQJC FROM JC_JZHZ WHERE HZRQ=:ldt_sqhzrq AND XMBH=3 and JGID=:gl_jgid",
				// parametersbqye);
				// if (mapbqye3.get("SQJC") != null) {
				// lws_hzxxsqjc.add(2, mapbqye3);
				// } else {
				// mapbqye3.put("SQJC", 0);
				// lws_hzxxsqjc.add(2, mapbqye3);
				// }
			} else {
				Map<String, Object> mapbqye4 = new HashMap<String, Object>();
				mapbqye4.put("SQJC", 0);
				lws_hzxxsqjc.add(0, mapbqye4);
				lws_hzxxsqjc.add(1, mapbqye4);
				lws_hzxxsqjc.add(2, mapbqye4);
				// lws_hzxxsqjc.add(2, mapbqye4);
			}

			// ２、取本期发生(bqfs)
			List<Map<String, Object>> lws_hzxxbqfs = new ArrayList<Map<String, Object>>();
			Map<String, Object> parametersje = new HashMap<String, Object>();
			parametersje.put("adt_hzrq", adt_hzrq);
			parametersje.put("gl_jgid", gl_jgid);
			Map<String, Object> mapzjje1 = dao
					.doLoad("SELECT sum(ZJJE) as BQFS FROM JC_FYMX WHERE HZRQ=:adt_hzrq and JGID=:gl_jgid",
							parametersje);
			if (mapzjje1.get("BQFS") != null) {
				lws_hzxxbqfs.add(0, mapzjje1);
			} else {
				mapzjje1.put("BQFS", 0);
				lws_hzxxbqfs.add(0, mapzjje1);
			}
			Map<String, Object> mapjkje2 = dao
					.doLoad("SELECT sum(JKJE) as BQFS FROM JC_TBKK WHERE HZRQ=:adt_hzrq and JGID=:gl_jgid",
							parametersje);
			if (mapjkje2.get("BQFS") != null) {
				lws_hzxxbqfs.add(1, mapjkje2);
			} else {
				mapjkje2.put("BQFS", 0);
				lws_hzxxbqfs.add(1, mapjkje2);
			}
			Map<String, Object> mapjkje3 = new HashMap<String, Object>();
			mapjkje3.put("BQFS", 0);
			lws_hzxxbqfs.add(2, mapjkje3);
			// Map<String, Object> defaultmap = new HashMap<String, Object>();
			// defaultmap.put("BQFS", 0);
			// lws_hzxxbqfs.add(2, defaultmap);

			double lws_Temp1 = 0.00;
			Map<String, Object> calculatemap = dao
					.doLoad("SELECT sum(JC_TBKK.JKJE) as BQFS FROM JC_TBKK JC_TBKK,JC_JKZF JC_JKZF WHERE JC_TBKK.JKXH = JC_JKZF.JKXH AND JC_TBKK.JGID = JC_JKZF.JGID AND JC_JKZF.HZRQ=:adt_hzrq and JC_TBKK.JGID=:gl_jgid",
							parametersje);
			if (calculatemap.get("BQFS") != null) {
				lws_Temp1 = Double.parseDouble(calculatemap.get("BQFS") + "");
			}
			lws_hzxxbqfs.get(1).put(
					"BQFS",
					Double.parseDouble(lws_hzxxbqfs.get(1).get("BQFS") + "")
							- lws_Temp1);

			// ３、取本期结算(bqjs)
			List<Map<String, Object>> lws_hzxxbqjs = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapfyhj = dao
					.doLoad("SELECT sum(FYHJ) as BQJS FROM JC_JCJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapfyhj.get("BQJS") != null) {
				lws_hzxxbqjs.add(0, mapfyhj);
			} else {
				mapfyhj.put("BQJS", 0);
				lws_hzxxbqjs.add(0, mapfyhj);
			}
			Map<String, Object> mapjkhj = dao
					.doLoad("SELECT sum(JKHJ) as BQJS FROM JC_JCJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapjkhj.get("BQJS") != null) {
				lws_hzxxbqjs.add(1, mapjkhj);
			} else {
				mapjkhj.put("BQJS", 0);
				lws_hzxxbqjs.add(1, mapjkhj);
			}
			Map<String, Object> mapcczj = dao
					.doLoad("SELECT sum(FYHJ) as BQJS FROM JC_JCJS WHERE HZRQ=:adt_hzrq AND JSLX=4 and JGID=:gl_jgid",
							parametersje);
			if (mapcczj.get("BQJS") != null) {
				lws_hzxxbqjs.add(2, mapcczj);
			} else {
				mapcczj.put("BQJS", 0);
				lws_hzxxbqjs.add(2, mapcczj);
			}
			// Map<String, Object> defaultmap1 = new HashMap<String, Object>();
			// defaultmap1.put("BQJS", 0);
			// lws_hzxxbqjs.add(2, defaultmap1);

			double lws_Temp2 = 0.00;
			Map<String, Object> calculatemap1 = dao
					.doLoad("SELECT sum(JC_JCJS.FYHJ) as BQJS FROM JC_JCJS JC_JCJS,JC_JSZF JC_JSZF WHERE JC_JCJS.ZYH = JC_JSZF.ZYH AND JC_JCJS.JSCS = JC_JSZF.JSCS AND JC_JCJS.JGID = JC_JSZF.JGID AND JC_JSZF.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) and JC_JSZF.JGID=:gl_jgid",
							parametersje);
			if (calculatemap1.get("BQJS") != null) {
				lws_Temp2 = Double.parseDouble(calculatemap1.get("BQJS") + "");
			}
			lws_hzxxbqjs.get(0).put(
					"BQJS",
					Double.parseDouble(lws_hzxxbqjs.get(0).get("BQJS") + "")
							- lws_Temp2);
			double lws_Temp6 = 0.00;
			Map<String, Object> calculatemap6 = dao
					.doLoad("SELECT sum(JC_JCJS.JKHJ) as BQJS FROM JC_JCJS JC_JCJS,JC_JSZF JC_JSZF WHERE JC_JCJS.ZYH = JC_JSZF.ZYH AND JC_JCJS.JSCS = JC_JSZF.JSCS AND JC_JCJS.JGID = JC_JSZF.JGID AND JC_JSZF.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) and JC_JSZF.JGID=:gl_jgid",
							parametersje);
			if (calculatemap6.get("BQJS") != null) {
				lws_Temp6 = Double.parseDouble(calculatemap6.get("BQJS") + "");
			}
			lws_hzxxbqjs.get(1).put(
					"BQJS",
					Double.parseDouble(lws_hzxxbqjs.get(1).get("BQJS") + "")
							- lws_Temp6);

			// ４、取现金支票(xjzp)
			List<Map<String, Object>> lws_hzxxxjzp = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapzfhj = dao
					.doLoad("SELECT sum(ZFHJ) as XJZP FROM JC_JCJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapzfhj.get("XJZP") != null) {
				lws_hzxxxjzp.add(0, mapzfhj);
			} else {
				mapzfhj.put("XJZP", 0);
				lws_hzxxxjzp.add(0, mapzfhj);
			}
			Map<String, Object> mapjkhj1 = dao
					.doLoad("SELECT sum(JKHJ) as XJZP FROM JC_JCJS WHERE HZRQ=:adt_hzrq AND JSLX in(5,1) and JGID=:gl_jgid",
							parametersje);
			if (mapjkhj1.get("XJZP") != null) {
				lws_hzxxxjzp.add(1, mapjkhj1);
			} else {
				mapjkhj1.put("XJZP", 0);
				lws_hzxxxjzp.add(1, mapjkhj1);
			}
			Map<String, Object> mapxjzp3 = new HashMap<String, Object>();
			mapxjzp3.put("XJZP", 0);
			lws_hzxxxjzp.add(2, mapxjzp3);
			// Map<String, Object> defaultmap2 = new HashMap<String, Object>();
			// defaultmap2.put("XJZP", 0);
			// lws_hzxxxjzp.add(2, defaultmap2);

			double lws_Temp3 = 0.00;
			Map<String, Object> calculatemap2 = dao
					.doLoad("SELECT sum(JC_JCJS.ZFHJ) as XJZP FROM JC_JCJS JC_JCJS,JC_JSZF JC_JSZF WHERE JC_JCJS.ZYH = JC_JSZF.ZYH AND JC_JCJS.JSCS = JC_JSZF.JSCS AND JC_JCJS.JGID = JC_JSZF.JGID AND JC_JSZF.HZRQ=:adt_hzrq  AND JC_JCJS.JSLX in(5,1) and JC_JCJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap2.get("XJZP") != null) {
				lws_Temp3 = Double.parseDouble(calculatemap2.get("XJZP") + "");
			}
			double lws_Temp4 = 0.00;
			Map<String, Object> calculatemap3 = dao
					.doLoad("SELECT sum(JC_JCJS.JKHJ) as XJZP FROM JC_JCJS JC_JCJS,JC_JSZF JC_JSZF WHERE JC_JCJS.ZYH = JC_JSZF.ZYH AND JC_JCJS.JSCS = JC_JSZF.JSCS AND JC_JCJS.JGID = JC_JSZF.JGID AND JC_JSZF.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) and JC_JCJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap3.get("XJZP") != null) {
				lws_Temp4 = Double.parseDouble(calculatemap3.get("XJZP") + "");
			}
			double yqfje = 0.00;
			double dqfje = 0.00;
			Map<String, Object> qfjemap3 = dao
					.doLoad("select sum(a.ZFHJ) as ZFHJ,sum(a.JKHJ) as JKHJ from JC_JCJS a WHERE a.HZRQ=:adt_hzrq and a.JSLX = 4 and a.JGID=:gl_jgid",
							parametersje);
			if (qfjemap3.get("ZFHJ") != null) {
				yqfje = Double.parseDouble(qfjemap3.get("JKHJ") + "");
				dqfje = Double.parseDouble(qfjemap3.get("ZFHJ") + "");
				lws_hzxxxjzp.get(0).put("QFJE", 0);
				lws_hzxxxjzp.get(1).put("QFJE", yqfje);
				lws_hzxxxjzp.get(2).put("QFJE", dqfje);
			} else {
				lws_hzxxxjzp.get(0).put("QFJE", 0);
				lws_hzxxxjzp.get(1).put("QFJE", 0);
				lws_hzxxxjzp.get(2).put("QFJE", 0);
			}
			lws_hzxxxjzp.get(0).put(
					"XJZP",
					Double.parseDouble(lws_hzxxxjzp.get(0).get("XJZP") + "")
							- lws_Temp3);
			lws_hzxxxjzp.get(1).put(
					"XJZP",
					Double.parseDouble(lws_hzxxxjzp.get(1).get("XJZP") + "")
							- lws_Temp4);
			// 5、取其它应收(qtys)
			List<Map<String, Object>> lws_hzxxqtys = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapzfhjzfhj = dao
					.doLoad("SELECT sum(FYHJ - ZFHJ) as QTYS FROM JC_JCJS JC_JCJS WHERE JC_JCJS.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) AND JC_JCJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB=0) and JGID=:gl_jgid",
							parametersje);
			if (mapzfhjzfhj.get("QTYS") != null) {
				lws_hzxxqtys.add(0, mapzfhjzfhj);
			} else {
				mapzfhjzfhj.put("QTYS", 0);
				lws_hzxxqtys.add(0, mapzfhjzfhj);
			}
			Map<String, Object> defaultmap3 = new HashMap<String, Object>();
			defaultmap3.put("QTYS", 0);
			lws_hzxxqtys.add(1, defaultmap3);
			lws_hzxxqtys.add(2, defaultmap3);
			// Map<String, Object> mapzfhjzfhj1 = dao
			// .doLoad("SELECT sum(FYHJ - ZFHJ) as QTYS FROM JC_JCJS JC_JCJS WHERE JC_JCJS.HZRQ=:adt_hzrq AND (JC_JCJS.JSLX=3) AND JC_JCJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB=0) and JGID=:gl_jgid",
			// parametersje);
			// if (mapzfhjzfhj1.get("QTYS") != null) {
			// lws_hzxxqtys.add(2, mapzfhjzfhj1);
			// } else {
			// mapzfhjzfhj1.put("QTYS", 0);
			// lws_hzxxqtys.add(2, mapzfhjzfhj1);
			// }
			double lws_Temp5 = 0.00;
			Map<String, Object> calculatemap4 = dao
					.doLoad("SELECT sum(JC_JCJS.FYHJ - JC_JCJS.ZFHJ) as QTYS FROM JC_JCJS JC_JCJS,JC_JSZF JC_JSZF WHERE JC_JCJS.ZYH=JC_JSZF.ZYH AND JC_JCJS.JSCS=JC_JSZF.JSCS AND JC_JCJS.JGID=JC_JSZF.JGID AND JC_JSZF.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) AND JC_JCJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB=0) and JC_JCJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap4.get("QTYS") != null) {
				lws_Temp5 = Double.parseDouble(calculatemap4.get("QTYS") + "");
			}
			lws_hzxxqtys.get(0).put(
					"QTYS",
					Double.parseDouble(lws_hzxxqtys.get(0).get("QTYS") + "")
							- lws_Temp5);
			// 6、取参保应收(cbys)
			List<Map<String, Object>> lws_hzxxcbys = new ArrayList<Map<String, Object>>();
			Map<String, Object> mapfyhjfyhj = dao
					.doLoad("SELECT sum(FYHJ - ZFHJ) as CBYS FROM JC_JCJS JC_JCJS WHERE JC_JCJS.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) AND JC_JCJS.BRXZ IN ( SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB>0) and JGID=:gl_jgid",
							parametersje);
			if (mapfyhjfyhj.get("CBYS") != null) {
				lws_hzxxcbys.add(0, mapfyhjfyhj);
			} else {
				mapfyhjfyhj.put("CBYS", 0);
				lws_hzxxcbys.add(0, mapfyhjfyhj);
			}
			Map<String, Object> defaultmap4 = new HashMap<String, Object>();
			defaultmap4.put("CBYS", 0);
			lws_hzxxcbys.add(1, defaultmap4);
			lws_hzxxcbys.add(2, defaultmap4);
			// Map<String, Object> mapfyhjfyhj1 = dao
			// .doLoad("SELECT sum(FYHJ - ZFHJ) as CBYS FROM JC_JCJS JC_JCJS WHERE JC_JCJS.HZRQ=:adt_hzrq AND (JC_JCJS.JSLX=3) AND JC_JCJS.BRXZ IN ( SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB>0) and JGID=:gl_jgid",
			// parametersje);
			// if (mapfyhjfyhj1.get("CBYS") != null) {
			// lws_hzxxcbys.add(2, mapfyhjfyhj1);
			// } else {
			// mapfyhjfyhj1.put("CBYS", 0);
			// lws_hzxxcbys.add(2, mapfyhjfyhj1);
			// }
			double lws_Temp7 = 0.00;
			Map<String, Object> calculatemap5 = dao
					.doLoad("SELECT sum(JC_JCJS.FYHJ - JC_JCJS.ZFHJ) as CBYS FROM JC_JCJS JC_JCJS,JC_JSZF JC_JSZF WHERE JC_JCJS.ZYH  = JC_JSZF.ZYH AND JC_JCJS.JSCS = JC_JSZF.JSCS AND JC_JCJS.JGID = JC_JSZF.JGID AND JC_JSZF.HZRQ=:adt_hzrq AND JC_JCJS.JSLX in(5,1) AND JC_JCJS.BRXZ IN (SELECT GY_BRXZ.BRXZ FROM GY_BRXZ GY_BRXZ WHERE GY_BRXZ.DBPB>0) and JC_JCJS.JGID=:gl_jgid",
							parametersje);
			if (calculatemap5.get("CBYS") != null) {
				lws_Temp7 = Double.parseDouble(calculatemap5.get("CBYS") + "");
			}
			lws_hzxxcbys.get(0).put(
					"CBYS",
					Double.parseDouble(lws_hzxxcbys.get(0).get("CBYS") + "")
							- lws_Temp7);

			// 7、计算本期余额(bqye) = 上期结存 + 本期发生 - 本期结算
			List<Double> lws_hzxxbqye = new ArrayList<Double>();
			lws_hzxxbqye.add(
					0,
					Double.parseDouble(lws_hzxxsqjc.get(0).get("SQJC") + "")
							+ Double.parseDouble(lws_hzxxbqfs.get(0)
									.get("BQFS") + "")
							- Double.parseDouble(lws_hzxxbqjs.get(0)
									.get("BQJS") + ""));
			lws_hzxxbqye.add(
					1,
					Double.parseDouble(lws_hzxxsqjc.get(1).get("SQJC") + "")
							+ Double.parseDouble(lws_hzxxbqfs.get(1)
									.get("BQFS") + "")
							- Double.parseDouble(lws_hzxxbqjs.get(1)
									.get("BQJS") + ""));
			lws_hzxxbqye.add(
					2,
					Double.parseDouble(lws_hzxxsqjc.get(2).get("SQJC") + "")
							+ Double.parseDouble(lws_hzxxbqfs.get(2)
									.get("BQFS") + "")
							- Double.parseDouble(lws_hzxxbqjs.get(2)
									.get("BQJS") + ""));
			// lws_hzxxbqye.add(
			// 2,
			// Double.parseDouble(lws_hzxxsqjc.get(2).get("SQJC") + "")
			// + Double.parseDouble(lws_hzxxbqfs.get(2)
			// .get("BQFS") + "")
			// - Double.parseDouble(lws_hzxxbqjs.get(2)
			// .get("BQJS") + ""));
			// 7. 将汇总信息写入JC_JZHZ
			for (int ll_row = 0; ll_row < 3; ll_row++) {
				Map<String, Object> JC_jzhzmap = new HashMap<String, Object>();
				JC_jzhzmap.put("HZRQ", adt_hzrq);
				JC_jzhzmap.put("XMBH", ll_row + 1);
				JC_jzhzmap.put("SQJC", lws_hzxxsqjc.get(ll_row).get("SQJC"));
				JC_jzhzmap.put("BQFS", lws_hzxxbqfs.get(ll_row).get("BQFS"));
				JC_jzhzmap.put(
						"BQJS",
						Double.parseDouble(lws_hzxxbqjs.get(ll_row).get("BQJS")
								+ ""));
				JC_jzhzmap.put("XJZP", lws_hzxxxjzp.get(ll_row).get("XJZP"));
				JC_jzhzmap.put("CYDJ", 0);
				JC_jzhzmap.put("QFJE", lws_hzxxxjzp.get(ll_row).get("QFJE"));
				JC_jzhzmap.put("CBJE", lws_hzxxcbys.get(ll_row).get("CBYS"));
				JC_jzhzmap.put("QTJE", lws_hzxxqtys.get(ll_row).get("QTYS"));
				JC_jzhzmap.put(
						"BQYE",
						Double.parseDouble(lws_hzxxbqye.get(ll_row) + "")
								);
				JC_jzhzmap.put("YHJE", 0);
				JC_jzhzmap.put("JGID", gl_jgid);
				dao.doSave("create", BSPHISEntryNames.JC_JZHZ, JC_jzhzmap,
						false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		}
	}
	/**
	 * 
	 * @param adt_hzrq
	 *            汇总日期
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static void wf_create_fyhz(Date adt_hzrq, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		List<Map<String, Object>> zy_fyhzlist = new ArrayList<Map<String, Object>>();
		try {
			adt_hzrq = sdfdatetime
					.parse(sdfdate.format(adt_hzrq) + " 00:00:00");
			Map<String, Object> parametershzrq = new HashMap<String, Object>();
			Date ldt_sqhzrq = null; // 上期汇总日期
			parametershzrq.put("gl_jgid", gl_jgid);
			Map<String, Object> hzrqmap = dao
					.doLoad("SELECT max(HZRQ) as HZRQ FROM ZY_FYHZ where JGID=:gl_jgid",
							parametershzrq);
			if (hzrqmap.get("HZRQ") != null) {
				ldt_sqhzrq = sdfdatetime.parse(hzrqmap.get("HZRQ") + "");
			}
			// １、取上期结存(sqjc)
			Map<String, Object> parameterslds_sqjc = new HashMap<String, Object>();
			parameterslds_sqjc.put("adt_hzrq", ldt_sqhzrq);
			parameterslds_sqjc.put("al_jgid", gl_jgid);
			List<Map<String, Object>> lds_sqjc = dao
					.doQuery(
							"SELECT ZY_FYHZ.FYXM as FYXM,sum(ZY_FYHZ.BQJC) AS FYJE FROM ZY_FYHZ ZY_FYHZ WHERE ZY_FYHZ.HZRQ=:adt_hzrq and ZY_FYHZ.JGID=:al_jgid GROUP BY ZY_FYHZ.FYXM",
							parameterslds_sqjc);
			for (int ll_Row = 0; ll_Row < lds_sqjc.size(); ll_Row++) {
				Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
				zy_fyhzmap.put("HZRQ", adt_hzrq);
				zy_fyhzmap.put("FYXM", lds_sqjc.get(ll_Row).get("FYXM"));
				zy_fyhzmap.put("SQJC", lds_sqjc.get(ll_Row).get("FYJE"));
				zy_fyhzmap.put("JGID", gl_jgid);
				zy_fyhzmap.put("BQFS", 0.00);
				zy_fyhzmap.put("BQJS", 0.00);
				zy_fyhzmap.put("SJJC", 0.00);
				zy_fyhzmap.put("BQJC", 0.00);
				zy_fyhzlist.add(zy_fyhzmap);
			}
			// ２、取本期发生(bqfs)
			Map<String, Object> parametersfyje = new HashMap<String, Object>();
			parametersfyje.put("adt_hzrq", adt_hzrq);
			parametersfyje.put("al_jgid", gl_jgid);
			List<Map<String, Object>> lds_bqfs = dao
					.doQuery(
							"SELECT ZY_FYMX.FYXM as FYXM,sum(ZY_FYMX.ZJJE) AS FYJE FROM ZY_FYMX ZY_FYMX	WHERE ZY_FYMX.HZRQ=:adt_hzrq and ZY_FYMX.JGID=:al_jgid GROUP BY ZY_FYMX.FYXM",
							parametersfyje);
			List<Map<String, Object>> lds_bqfslist = new ArrayList<Map<String, Object>>();
			int sign1 = 0;
			for (int ll_Row = 0; ll_Row < lds_bqfs.size(); ll_Row++) {
				if (zy_fyhzlist.size() > 0) {
					for (int j = 0; j < zy_fyhzlist.size(); j++) {
						if (lds_bqfs
								.get(ll_Row)
								.get("FYXM")
								.toString()
								.equals(zy_fyhzlist.get(j).get("FYXM")
										.toString())) {
							zy_fyhzlist.get(j).put(
									"BQFS",
									Double.parseDouble(zy_fyhzlist.get(j).get(
											"BQFS")
											+ "")
											+ Double.parseDouble(lds_bqfs.get(
													ll_Row).get("FYJE")
													+ ""));
							sign1 = 1;
							break;
						}
					}
					if (sign1 == 0) {
						Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
						zy_fyhzmap.put("HZRQ", adt_hzrq);
						zy_fyhzmap
								.put("FYXM", lds_bqfs.get(ll_Row).get("FYXM"));
						zy_fyhzmap.put("SQJC", 0.00);
						zy_fyhzmap.put("JGID", gl_jgid);
						zy_fyhzmap.put(
								"BQFS",
								Double.parseDouble(lds_bqfs.get(ll_Row).get(
										"FYJE")
										+ ""));
						zy_fyhzmap.put("BQJS", 0.00);
						zy_fyhzmap.put("SJJC", 0.00);
						zy_fyhzmap.put("BQJC", 0.00);
						lds_bqfslist.add(zy_fyhzmap);
					}
				} else {
					Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
					zy_fyhzmap.put("HZRQ", adt_hzrq);
					zy_fyhzmap.put("FYXM", lds_bqfs.get(ll_Row).get("FYXM"));
					zy_fyhzmap.put("SQJC", 0.00);
					zy_fyhzmap.put("JGID", gl_jgid);
					zy_fyhzmap.put(
							"BQFS",
							Double.parseDouble(lds_bqfs.get(ll_Row).get("FYJE")
									+ ""));
					zy_fyhzmap.put("BQJS", 0.00);
					zy_fyhzmap.put("SJJC", 0.00);
					zy_fyhzmap.put("BQJC", 0.00);
					lds_bqfslist.add(zy_fyhzmap);
				}
			}
			if (lds_bqfslist.size() > 0) {
				zy_fyhzlist.addAll(lds_bqfslist);
			}
			// ３、取本期结算(bqjs)
			List<Map<String, Object>> lds_bqjs = dao
					.doQuery(
							"SELECT ZY_JSMX.FYXM as FYXM,sum(ZY_JSMX.ZJJE) AS FYJE FROM ZY_ZYJS ZY_ZYJS,ZY_JSMX ZY_JSMX WHERE ZY_ZYJS.ZYH=ZY_JSMX.ZYH AND ZY_ZYJS.JSCS=ZY_JSMX.JSCS AND	ZY_ZYJS.HZRQ=:adt_hzrq and ZY_ZYJS.JGID=:al_jgid and ZY_ZYJS.JSLX in(5,1) GROUP BY ZY_JSMX.FYXM UNION ALL SELECT ZY_JSMX.FYXM,- sum(ZY_JSMX.ZJJE) AS FYJE FROM ZY_ZYJS,ZY_JSMX,ZY_JSZF WHERE ZY_ZYJS.JSLX in(5,1) and ZY_ZYJS.ZYH=ZY_JSMX.ZYH AND ZY_ZYJS.JSCS=ZY_JSMX.JSCS AND ZY_ZYJS.ZYH=ZY_JSZF.ZYH AND ZY_ZYJS.JSCS=ZY_JSZF.JSCS AND ZY_JSZF.HZRQ=:adt_hzrq and ZY_ZYJS.JGID=:al_jgid GROUP BY ZY_JSMX.FYXM",
							parametersfyje);
			List<Map<String, Object>> lds_bqjslist = new ArrayList<Map<String, Object>>();
			int sign2 = 0;
			for (int ll_Row = 0; ll_Row < lds_bqjs.size(); ll_Row++) {
				if (zy_fyhzlist.size() > 0) {
					for (int j = 0; j < zy_fyhzlist.size(); j++) {
						if (lds_bqjs
								.get(ll_Row)
								.get("FYXM")
								.toString()
								.equals(zy_fyhzlist.get(j).get("FYXM")
										.toString())) {
							zy_fyhzlist.get(j).put(
									"BQJS",
									Double.parseDouble(zy_fyhzlist.get(j).get(
											"BQJS")
											+ "")
											+ Double.parseDouble(lds_bqjs.get(
													ll_Row).get("FYJE")
													+ ""));
							sign2 = 1;
							break;
						}
					}
					if (sign2 == 0) {
						Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
						zy_fyhzmap.put("HZRQ", adt_hzrq);
						zy_fyhzmap
								.put("FYXM", lds_bqjs.get(ll_Row).get("FYXM"));
						zy_fyhzmap.put("SQJC", 0.00);
						zy_fyhzmap.put("JGID", gl_jgid);
						zy_fyhzmap.put("BQFS", 0.00);
						zy_fyhzmap.put(
								"BQJS",
								Double.parseDouble(lds_bqjs.get(ll_Row).get(
										"FYJE")
										+ ""));
						zy_fyhzmap.put("SJJC", 0.00);
						zy_fyhzmap.put("BQJC", 0.00);
						lds_bqjslist.add(zy_fyhzmap);
					}

				} else {
					Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
					zy_fyhzmap.put("HZRQ", adt_hzrq);
					zy_fyhzmap.put("FYXM", lds_bqjs.get(ll_Row).get("FYXM"));
					zy_fyhzmap.put("SQJC", 0.00);
					zy_fyhzmap.put("JGID", gl_jgid);
					zy_fyhzmap.put("BQFS", 0.00);
					zy_fyhzmap.put(
							"BQJS",
							Double.parseDouble(lds_bqjs.get(ll_Row).get("FYJE")
									+ ""));
					zy_fyhzmap.put("SJJC", 0.00);
					zy_fyhzmap.put("BQJC", 0.00);
					lds_bqjslist.add(zy_fyhzmap);
				}
			}
			if (lds_bqjslist.size() > 0) {
				zy_fyhzlist.addAll(lds_bqjslist);
			}
			// ４、取实际结存(sjjc)
			List<Map<String, Object>> lds_sjjc = dao
					.doQuery(
							"SELECT ZY_FYMX.FYXM as FYXM,sum(ZY_FYMX.ZJJE) AS FYJE FROM ZY_FYMX ZY_FYMX WHERE ZY_FYMX.HZRQ<=:adt_hzrq AND ZY_FYMX.JSCS=0 and ZY_FYMX.JGID=:al_jgid GROUP BY ZY_FYMX.FYXM",
							parametersfyje);
			List<Map<String, Object>> lds_sjjclist = new ArrayList<Map<String, Object>>();
			int sign3 = 0;
			for (int ll_Row = 0; ll_Row < lds_sjjc.size(); ll_Row++) {
				if (zy_fyhzlist.size() > 0) {
					for (int j = 0; j < zy_fyhzlist.size(); j++) {
						if (lds_sjjc
								.get(ll_Row)
								.get("FYXM")
								.toString()
								.equals(zy_fyhzlist.get(j).get("FYXM")
										.toString())) {
							zy_fyhzlist.get(j).put(
									"SJJC",
									Double.parseDouble(zy_fyhzlist.get(j).get(
											"SJJC")
											+ "")
											+ Double.parseDouble(lds_sjjc.get(
													ll_Row).get("FYJE")
													+ ""));
							sign3 = 1;
							break;
						}
					}
					if (sign3 == 0) {
						Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
						zy_fyhzmap.put("HZRQ", adt_hzrq);
						zy_fyhzmap
								.put("FYXM", lds_sjjc.get(ll_Row).get("FYXM"));
						zy_fyhzmap.put("SQJC", 0);
						zy_fyhzmap.put("JGID", gl_jgid);
						zy_fyhzmap.put("BQFS", 0.00);
						zy_fyhzmap.put("BQJS", 0.00);
						zy_fyhzmap.put(
								"SJJC",
								Double.parseDouble(lds_sjjc.get(ll_Row).get(
										"FYJE")
										+ ""));
						zy_fyhzmap.put("BQJC", 0.00);
						lds_sjjclist.add(zy_fyhzmap);
					}

				} else {
					Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
					zy_fyhzmap.put("HZRQ", adt_hzrq);
					zy_fyhzmap.put("FYXM", lds_sjjc.get(ll_Row).get("FYXM"));
					zy_fyhzmap.put("SQJC", 0);
					zy_fyhzmap.put("JGID", gl_jgid);
					zy_fyhzmap.put("BQFS", 0.00);
					zy_fyhzmap.put("BQJS", 0.00);
					zy_fyhzmap.put(
							"SJJC",
							Double.parseDouble(lds_sjjc.get(ll_Row).get("FYJE")
									+ ""));
					zy_fyhzmap.put("BQJC", 0.00);
					lds_sjjclist.add(zy_fyhzmap);
				}
			}
			if (lds_sjjclist.size() > 0) {
				zy_fyhzlist.addAll(lds_sjjclist);
			}
			// ５、计算本期结存(bqjc) = 上期结存 + 本期发生 - 本期结算
			for (int ll_Row = 0; ll_Row < zy_fyhzlist.size(); ll_Row++) {
				zy_fyhzlist.get(ll_Row).put(
						"BQJC",
						Double.parseDouble(zy_fyhzlist.get(ll_Row).get("SQJC")
								+ "")
								+ Double.parseDouble(zy_fyhzlist.get(ll_Row)
										.get("BQFS") + "")
								- Double.parseDouble(zy_fyhzlist.get(ll_Row)
										.get("BQJS") + ""));
				// 删除全部项目为零的记录
				if (Double
						.parseDouble(zy_fyhzlist.get(ll_Row).get("SQJC") + "")
						+ Double.parseDouble(zy_fyhzlist.get(ll_Row)
								.get("BQFS") + "")
						+ Double.parseDouble(zy_fyhzlist.get(ll_Row)
								.get("BQJS") + "")
						+ Double.parseDouble(zy_fyhzlist.get(ll_Row)
								.get("BQJC") + "")
						+ Double.parseDouble(zy_fyhzlist.get(ll_Row)
								.get("SJJC") + "") == 0.0) {
					zy_fyhzlist.remove(ll_Row);
					ll_Row--;
				}
			}
			// ５、计算本期结存(bqjc) = 上期结存 + 本期发生 - 本期结算
			for (int ll_Row = 0; ll_Row < zy_fyhzlist.size(); ll_Row++) {
				Map<String, Object> zy_fyhzmap = new HashMap<String, Object>();
				zy_fyhzmap.put("HZRQ", zy_fyhzlist.get(ll_Row).get("HZRQ"));
				zy_fyhzmap.put("FYXM", zy_fyhzlist.get(ll_Row).get("FYXM"));
				zy_fyhzmap.put("SQJC", zy_fyhzlist.get(ll_Row).get("SQJC"));
				zy_fyhzmap.put("JGID", zy_fyhzlist.get(ll_Row).get("JGID"));
				zy_fyhzmap.put("BQFS", zy_fyhzlist.get(ll_Row).get("BQFS"));
				zy_fyhzmap.put("BQJS", zy_fyhzlist.get(ll_Row).get("BQJS"));
				zy_fyhzmap.put("SJJC", zy_fyhzlist.get(ll_Row).get("SJJC"));
				zy_fyhzmap.put("BQJC", zy_fyhzlist.get(ll_Row).get("BQJC"));
				dao.doSave("create", BSPHISEntryNames.ZY_FYHZ, zy_fyhzmap,
						false);
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		}

	}
	
	/**
	 * 
	 * @param adt_hzrq
	 *            汇总日期
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static void fsb_wf_create_fyhz(Date adt_hzrq, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		// String gl_jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String gl_jgid = user.getManageUnit().getId();// 用户的机构ID
		List<Map<String, Object>> JC_fyhzlist = new ArrayList<Map<String, Object>>();
		try {
			adt_hzrq = sdfdatetime
					.parse(sdfdate.format(adt_hzrq) + " 00:00:00");
			Map<String, Object> parametershzrq = new HashMap<String, Object>();
			Date ldt_sqhzrq = null; // 上期汇总日期
			parametershzrq.put("gl_jgid", gl_jgid);
			Map<String, Object> hzrqmap = dao
					.doLoad("SELECT max(HZRQ) as HZRQ FROM JC_FYHZ where JGID=:gl_jgid",
							parametershzrq);
			if (hzrqmap.get("HZRQ") != null) {
				ldt_sqhzrq = sdfdatetime.parse(hzrqmap.get("HZRQ") + "");
			}
			// １、取上期结存(sqjc)
			Map<String, Object> parameterslds_sqjc = new HashMap<String, Object>();
			parameterslds_sqjc.put("adt_hzrq", ldt_sqhzrq);
			parameterslds_sqjc.put("al_jgid", gl_jgid);
			List<Map<String, Object>> lds_sqjc = dao
					.doQuery(
							"SELECT JC_FYHZ.FYXM as FYXM,sum(JC_FYHZ.BQJC) AS FYJE FROM JC_FYHZ JC_FYHZ WHERE JC_FYHZ.HZRQ=:adt_hzrq and JC_FYHZ.JGID=:al_jgid GROUP BY JC_FYHZ.FYXM",
							parameterslds_sqjc);
			for (int ll_Row = 0; ll_Row < lds_sqjc.size(); ll_Row++) {
				Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
				JC_fyhzmap.put("HZRQ", adt_hzrq);
				JC_fyhzmap.put("FYXM", lds_sqjc.get(ll_Row).get("FYXM"));
				JC_fyhzmap.put("SQJC", lds_sqjc.get(ll_Row).get("FYJE"));
				JC_fyhzmap.put("JGID", gl_jgid);
				JC_fyhzmap.put("BQFS", 0.00);
				JC_fyhzmap.put("BQJS", 0.00);
				JC_fyhzmap.put("SJJC", 0.00);
				JC_fyhzmap.put("BQJC", 0.00);
				JC_fyhzlist.add(JC_fyhzmap);
			}
			// ２、取本期发生(bqfs)
			Map<String, Object> parametersfyje = new HashMap<String, Object>();
			parametersfyje.put("adt_hzrq", adt_hzrq);
			parametersfyje.put("al_jgid", gl_jgid);
			List<Map<String, Object>> lds_bqfs = dao
					.doQuery(
							"SELECT JC_FYMX.FYXM as FYXM,sum(JC_FYMX.ZJJE) AS FYJE FROM JC_FYMX JC_FYMX	WHERE JC_FYMX.HZRQ=:adt_hzrq and JC_FYMX.JGID=:al_jgid GROUP BY JC_FYMX.FYXM",
							parametersfyje);
			List<Map<String, Object>> lds_bqfslist = new ArrayList<Map<String, Object>>();
			int sign1 = 0;
			for (int ll_Row = 0; ll_Row < lds_bqfs.size(); ll_Row++) {
				if (JC_fyhzlist.size() > 0) {
					for (int j = 0; j < JC_fyhzlist.size(); j++) {
						if (lds_bqfs
								.get(ll_Row)
								.get("FYXM")
								.toString()
								.equals(JC_fyhzlist.get(j).get("FYXM")
										.toString())) {
							JC_fyhzlist.get(j).put(
									"BQFS",
									Double.parseDouble(JC_fyhzlist.get(j).get(
											"BQFS")
											+ "")
											+ Double.parseDouble(lds_bqfs.get(
													ll_Row).get("FYJE")
													+ ""));
							sign1 = 1;
							break;
						}
					}
					if (sign1 == 0) {
						Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
						JC_fyhzmap.put("HZRQ", adt_hzrq);
						JC_fyhzmap
								.put("FYXM", lds_bqfs.get(ll_Row).get("FYXM"));
						JC_fyhzmap.put("SQJC", 0.00);
						JC_fyhzmap.put("JGID", gl_jgid);
						JC_fyhzmap.put(
								"BQFS",
								Double.parseDouble(lds_bqfs.get(ll_Row).get(
										"FYJE")
										+ ""));
						JC_fyhzmap.put("BQJS", 0.00);
						JC_fyhzmap.put("SJJC", 0.00);
						JC_fyhzmap.put("BQJC", 0.00);
						lds_bqfslist.add(JC_fyhzmap);
					}
				} else {
					Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
					JC_fyhzmap.put("HZRQ", adt_hzrq);
					JC_fyhzmap.put("FYXM", lds_bqfs.get(ll_Row).get("FYXM"));
					JC_fyhzmap.put("SQJC", 0.00);
					JC_fyhzmap.put("JGID", gl_jgid);
					JC_fyhzmap.put(
							"BQFS",
							Double.parseDouble(lds_bqfs.get(ll_Row).get("FYJE")
									+ ""));
					JC_fyhzmap.put("BQJS", 0.00);
					JC_fyhzmap.put("SJJC", 0.00);
					JC_fyhzmap.put("BQJC", 0.00);
					lds_bqfslist.add(JC_fyhzmap);
				}
			}
			if (lds_bqfslist.size() > 0) {
				JC_fyhzlist.addAll(lds_bqfslist);
			}
			// ３、取本期结算(bqjs)
			List<Map<String, Object>> lds_bqjs = dao
					.doQuery(
							"SELECT JC_JSMX.FYXM as FYXM,sum(JC_JSMX.ZJJE) AS FYJE FROM JC_JCJS JC_JCJS,JC_JSMX JC_JSMX WHERE JC_JCJS.ZYH=JC_JSMX.ZYH AND JC_JCJS.JSCS=JC_JSMX.JSCS AND	JC_JCJS.HZRQ=:adt_hzrq and JC_JCJS.JGID=:al_jgid and JC_JCJS.JSLX in(5,1) GROUP BY JC_JSMX.FYXM UNION ALL SELECT JC_JSMX.FYXM,- sum(JC_JSMX.ZJJE) AS FYJE FROM JC_JCJS,JC_JSMX,JC_JSZF WHERE JC_JCJS.JSLX in(5,1) and JC_JCJS.ZYH=JC_JSMX.ZYH AND JC_JCJS.JSCS=JC_JSMX.JSCS AND JC_JCJS.ZYH=JC_JSZF.ZYH AND JC_JCJS.JSCS=JC_JSZF.JSCS AND JC_JSZF.HZRQ=:adt_hzrq and JC_JCJS.JGID=:al_jgid GROUP BY JC_JSMX.FYXM",
							parametersfyje);
			List<Map<String, Object>> lds_bqjslist = new ArrayList<Map<String, Object>>();
			int sign2 = 0;
			for (int ll_Row = 0; ll_Row < lds_bqjs.size(); ll_Row++) {
				if (JC_fyhzlist.size() > 0) {
					for (int j = 0; j < JC_fyhzlist.size(); j++) {
						if (lds_bqjs
								.get(ll_Row)
								.get("FYXM")
								.toString()
								.equals(JC_fyhzlist.get(j).get("FYXM")
										.toString())) {
							JC_fyhzlist.get(j).put(
									"BQJS",
									Double.parseDouble(JC_fyhzlist.get(j).get(
											"BQJS")
											+ "")
											+ Double.parseDouble(lds_bqjs.get(
													ll_Row).get("FYJE")
													+ ""));
							sign2 = 1;
							break;
						}
					}
					if (sign2 == 0) {
						Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
						JC_fyhzmap.put("HZRQ", adt_hzrq);
						JC_fyhzmap
								.put("FYXM", lds_bqjs.get(ll_Row).get("FYXM"));
						JC_fyhzmap.put("SQJC", 0.00);
						JC_fyhzmap.put("JGID", gl_jgid);
						JC_fyhzmap.put("BQFS", 0.00);
						JC_fyhzmap.put(
								"BQJS",
								Double.parseDouble(lds_bqjs.get(ll_Row).get(
										"FYJE")
										+ ""));
						JC_fyhzmap.put("SJJC", 0.00);
						JC_fyhzmap.put("BQJC", 0.00);
						lds_bqjslist.add(JC_fyhzmap);
					}

				} else {
					Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
					JC_fyhzmap.put("HZRQ", adt_hzrq);
					JC_fyhzmap.put("FYXM", lds_bqjs.get(ll_Row).get("FYXM"));
					JC_fyhzmap.put("SQJC", 0.00);
					JC_fyhzmap.put("JGID", gl_jgid);
					JC_fyhzmap.put("BQFS", 0.00);
					JC_fyhzmap.put(
							"BQJS",
							Double.parseDouble(lds_bqjs.get(ll_Row).get("FYJE")
									+ ""));
					JC_fyhzmap.put("SJJC", 0.00);
					JC_fyhzmap.put("BQJC", 0.00);
					lds_bqjslist.add(JC_fyhzmap);
				}
			}
			if (lds_bqjslist.size() > 0) {
				JC_fyhzlist.addAll(lds_bqjslist);
			}
			// ４、取实际结存(sjjc)
			List<Map<String, Object>> lds_sjjc = dao
					.doQuery(
							"SELECT JC_FYMX.FYXM as FYXM,sum(JC_FYMX.ZJJE) AS FYJE FROM JC_FYMX JC_FYMX WHERE JC_FYMX.HZRQ<=:adt_hzrq AND JC_FYMX.JSCS=0 and JC_FYMX.JGID=:al_jgid GROUP BY JC_FYMX.FYXM",
							parametersfyje);
			List<Map<String, Object>> lds_sjjclist = new ArrayList<Map<String, Object>>();
			int sign3 = 0;
			for (int ll_Row = 0; ll_Row < lds_sjjc.size(); ll_Row++) {
				if (JC_fyhzlist.size() > 0) {
					for (int j = 0; j < JC_fyhzlist.size(); j++) {
						if (lds_sjjc
								.get(ll_Row)
								.get("FYXM")
								.toString()
								.equals(JC_fyhzlist.get(j).get("FYXM")
										.toString())) {
							JC_fyhzlist.get(j).put(
									"SJJC",
									Double.parseDouble(JC_fyhzlist.get(j).get(
											"SJJC")
											+ "")
											+ Double.parseDouble(lds_sjjc.get(
													ll_Row).get("FYJE")
													+ ""));
							sign3 = 1;
							break;
						}
					}
					if (sign3 == 0) {
						Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
						JC_fyhzmap.put("HZRQ", adt_hzrq);
						JC_fyhzmap
								.put("FYXM", lds_sjjc.get(ll_Row).get("FYXM"));
						JC_fyhzmap.put("SQJC", 0);
						JC_fyhzmap.put("JGID", gl_jgid);
						JC_fyhzmap.put("BQFS", 0.00);
						JC_fyhzmap.put("BQJS", 0.00);
						JC_fyhzmap.put(
								"SJJC",
								Double.parseDouble(lds_sjjc.get(ll_Row).get(
										"FYJE")
										+ ""));
						JC_fyhzmap.put("BQJC", 0.00);
						lds_sjjclist.add(JC_fyhzmap);
					}

				} else {
					Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
					JC_fyhzmap.put("HZRQ", adt_hzrq);
					JC_fyhzmap.put("FYXM", lds_sjjc.get(ll_Row).get("FYXM"));
					JC_fyhzmap.put("SQJC", 0);
					JC_fyhzmap.put("JGID", gl_jgid);
					JC_fyhzmap.put("BQFS", 0.00);
					JC_fyhzmap.put("BQJS", 0.00);
					JC_fyhzmap.put(
							"SJJC",
							Double.parseDouble(lds_sjjc.get(ll_Row).get("FYJE")
									+ ""));
					JC_fyhzmap.put("BQJC", 0.00);
					lds_sjjclist.add(JC_fyhzmap);
				}
			}
			if (lds_sjjclist.size() > 0) {
				JC_fyhzlist.addAll(lds_sjjclist);
			}
			// ５、计算本期结存(bqjc) = 上期结存 + 本期发生 - 本期结算
			for (int ll_Row = 0; ll_Row < JC_fyhzlist.size(); ll_Row++) {
				JC_fyhzlist.get(ll_Row).put(
						"BQJC",
						Double.parseDouble(JC_fyhzlist.get(ll_Row).get("SQJC")
								+ "")
								+ Double.parseDouble(JC_fyhzlist.get(ll_Row)
										.get("BQFS") + "")
								- Double.parseDouble(JC_fyhzlist.get(ll_Row)
										.get("BQJS") + ""));
				// 删除全部项目为零的记录
				if (Double
						.parseDouble(JC_fyhzlist.get(ll_Row).get("SQJC") + "")
						+ Double.parseDouble(JC_fyhzlist.get(ll_Row)
								.get("BQFS") + "")
						+ Double.parseDouble(JC_fyhzlist.get(ll_Row)
								.get("BQJS") + "")
						+ Double.parseDouble(JC_fyhzlist.get(ll_Row)
								.get("BQJC") + "")
						+ Double.parseDouble(JC_fyhzlist.get(ll_Row)
								.get("SJJC") + "") == 0.0) {
					JC_fyhzlist.remove(ll_Row);
					ll_Row--;
				}
			}
			// ５、计算本期结存(bqjc) = 上期结存 + 本期发生 - 本期结算
			for (int ll_Row = 0; ll_Row < JC_fyhzlist.size(); ll_Row++) {
				Map<String, Object> JC_fyhzmap = new HashMap<String, Object>();
				JC_fyhzmap.put("HZRQ", JC_fyhzlist.get(ll_Row).get("HZRQ"));
				JC_fyhzmap.put("FYXM", JC_fyhzlist.get(ll_Row).get("FYXM"));
				JC_fyhzmap.put("SQJC", JC_fyhzlist.get(ll_Row).get("SQJC"));
				JC_fyhzmap.put("JGID", JC_fyhzlist.get(ll_Row).get("JGID"));
				JC_fyhzmap.put("BQFS", JC_fyhzlist.get(ll_Row).get("BQFS"));
				JC_fyhzmap.put("BQJS", JC_fyhzlist.get(ll_Row).get("BQJS"));
				JC_fyhzmap.put("SJJC", JC_fyhzlist.get(ll_Row).get("SJJC"));
				JC_fyhzmap.put("BQJC", JC_fyhzlist.get(ll_Row).get("BQJC"));
				dao.doSave("create", BSPHISEntryNames.JC_FYHZ, JC_fyhzmap,
						false);
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public static double parseDouble(Object o) {
		if (o == null || "null".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @param adt_hzrq_start
	 *            汇总开始日期
	 * @param adt_hzrq_end
	 *            汇总结束日期
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public static void wf_Query(Map<String, Object> request,
			Map<String, Object> response, List<Map<String, Object>> resultLi,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// User user = (User) ctx.get("user.instance");
		// String jgname = user.get("manageUnit.name");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		response.put("title", jgname);
		Date adt_hzrq_start = null;
		Date adt_hzrq_end = null;
		try {
			if (request.get("beginDate") != null) {
				adt_hzrq_start = sdfdate.parse(request.get("beginDate") + "");
			}
			if (request.get("endDate") != null) {
				adt_hzrq_end = sdfdate.parse(request.get("endDate") + "");
			}
			String[] lS_ksrq = (request.get("beginDate") + "").split("-| ");
			String ksrq = lS_ksrq[0] + "年" + lS_ksrq[1] + "月" + lS_ksrq[2]
					+ "日";
			String[] lS_jsrq = (request.get("endDate") + "").split("-| ");
			String jsrq = lS_jsrq[0] + "年" + lS_jsrq[1] + "月" + lS_jsrq[2]
					+ "日";
			response.put("HZRQ", "汇总日期:" + ksrq);
			Map<String, Object> parametershzbd = new HashMap<String, Object>();
			parametershzbd.put("adt_hzrq_b", adt_hzrq_start);
			parametershzbd.put("adt_hzrq_e", adt_hzrq_end);
			parametershzbd.put("al_jgid", manaUnitId);
			// 日结汇总汇总表单
			// List<Map<String, Object>> resultList2 = dao
			// .doQuery(
			// "SELECT ZY_JZXX.QTYSFB as QTYSFB FROM ZY_JZXX ZY_JZXX WHERE ZY_JZXX.HZRQ>=:adt_hzrq_b AND ZY_JZXX.HZRQ<=:adt_hzrq_e AND ZY_JZXX.JGID=:al_jgid ",
			// parametershzbd);
			// if(resultList2.size()>0){
			// response.put("qtysFb",resultList2.get(0).get("QTYSFB")+"");
			// }
			List<Map<String, Object>> resultList = dao
					.doQuery(
							"SELECT ZY_JZXX.CZGH as CZGH,sum(ZY_JZXX.CYSR) as CYSR,sum(ZY_JZXX.YJJE) as YJJE,sum(ZY_JZXX.TPJE) as TPJE,sum(ZY_JZXX.FPZS) as FPZS,sum(ZY_JZXX.SJZS) as SJZS,sum(ZY_JZXX.YSJE) as YSJE,sum(ZY_JZXX.YSXJ) as YSXJ,sum(ZY_JZXX.ZPZS) as ZPZS,sum(ZY_JZXX.TYJJ) as TYJJ,sum(ZY_JZXX.YSQT) as YSQT,sum(ZY_JZXX.QTZS) as QTZS,sum(ZY_JZXX.SRJE) as SRJE,sum(ZY_JZXX.TCZC) as TCZC,sum(ZY_JZXX.DBZC) as DBZC,sum(ZY_JZXX.ZXJZFY) as ZXJZFY,sum(ZY_JZXX.GRXJZF) as GRXJZF,sum(ZY_JZXX.BCZHZF) as BCZHZF,sum(ZY_JZXX.AZQGFY) as AZQGFY FROM ZY_JZXX ZY_JZXX WHERE ZY_JZXX.HZRQ>=:adt_hzrq_b AND ZY_JZXX.HZRQ<=:adt_hzrq_e AND ZY_JZXX.JGID=:al_jgid group by ZY_JZXX.CZGH order by ZY_JZXX.CZGH",
							parametershzbd);
			for (int i = 0; i < resultList.size(); i++) {
				// for(int j=0;j<resultList2.size();j++){
				// if(i==j){
				// resultList.get(i).put("QTYSFB",
				// resultList2.get(j).get("QTYSFB"));
				// }
				// }
				String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("
						+ "select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from ZY_FKXX a, ZY_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.ZFGH = :czgh"
						+ " union all "
						+ "select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.CZGH = :czgh"
						+ " union all "
						+ "SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM ZY_TBKK a WHERE a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid and a.CZGH = :czgh"
						+ " union all "
						+ "SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM ZY_TBKK a ,ZY_JKZF b WHERE b.JKXH = a.JKXH and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.ZFGH = :czgh"
						+ ") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
				String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
						+ "SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid and a.CZGH = :czgh"
						+ " union all "
						+ "SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a ,ZY_JSZF b WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.ZFGH = :czgh"
						+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("adt_hzrq_b", adt_hzrq_start);
				parameters.put("adt_hzrq_e", adt_hzrq_end);
				parameters.put("al_jgid", manaUnitId);
				parameters.put("czgh", resultList.get(i).get("CZGH") + "");
				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(
						ids_brxz_hql, parameters);
				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(
						ids_fkfs_hql, parameters);
				String qtysFb = "";
				String jzjeSt = "0.00";
				if (ids_fkfs != null && ids_fkfs.size() != 0) {
					for (int n = 0; n < ids_fkfs.size(); n++) {
						qtysFb = qtysFb
								+ ids_fkfs.get(n).get("FKMC")
								+ ":"
								+ String.format("%1$.2f",
										ids_fkfs.get(n).get("FKJE")) + " ";
					}
				}
				if (ids_brxz != null && ids_brxz.size() != 0) {
					for (int n = 0; n < ids_brxz.size(); n++) {
						if (Integer.parseInt(ids_brxz.get(n).get("DBPB") + "") == 0) {
							jzjeSt = String
									.format("%1$.2f",
											parseDouble(jzjeSt)
													+ (parseDouble(ids_brxz
															.get(n).get("FYHJ")
															+ "") - parseDouble(ids_brxz
															.get(n).get("ZFHJ")
															+ "")));
						} else {
							qtysFb = qtysFb
									+ ids_brxz.get(n).get("XZMC")
									+ ":"
									+ String.format(
											"%1$.2f",
											(parseDouble(ids_brxz.get(n).get(
													"FYHJ")
													+ "") - parseDouble(ids_brxz
													.get(n).get("ZFHJ") + "")))
									+ " ";
						}
					}
					qtysFb = qtysFb + " " + "记账 :" + jzjeSt + " ";
				}
				resultList.get(i).put("QTYSFB", qtysFb);
				resultList.get(i).put("CYSR",
						String.format("%1$.2f", resultList.get(i).get("CYSR")));
				resultList.get(i).put("YJJE",
						String.format("%1$.2f", resultList.get(i).get("YJJE")));
				resultList.get(i).put("TPJE",
						String.format("%1$.2f", resultList.get(i).get("TPJE")));
				resultList.get(i).put("YSJE",
						String.format("%1$.2f", resultList.get(i).get("YSJE")));
				resultList.get(i).put("YSXJ",
						String.format("%1$.2f", resultList.get(i).get("YSXJ")));
				resultList.get(i).put("TYJJ",
						String.format("%1$.2f", resultList.get(i).get("TYJJ")));
				resultList.get(i).put("YSQT",
						String.format("%1$.2f", resultList.get(i).get("YSQT")));
				resultList.get(i).put("SRJE",
						String.format("%1$.2f", resultList.get(i).get("SRJE")));
				// if (resultList.get(i).get("SZYB") != null) {
				// resultList.get(i).put(
				// "SZYB",
				// String.format("%1$.2f",
				// resultList.get(i).get("SZYB")));
				// }
				// if (resultList.get(i).get("SYB") != null) {
				// resultList.get(i).put(
				// "SYB",
				// String.format("%1$.2f", resultList.get(i)
				// .get("SYB")));
				// }
				// if (resultList.get(i).get("YHYB") != null) {
				// resultList.get(i).put(
				// "YHYB",
				// String.format("%1$.2f",
				// resultList.get(i).get("YHYB")));
				// }
				// if (resultList.get(i).get("SMK") != null) {
				// resultList.get(i).put(
				// "SMK",
				// String.format("%1$.2f", resultList.get(i)
				// .get("SMK")));
				// }
				if (resultList.get(i).get("TCZC") != null) {
					resultList.get(i).put(
							"TCZC",
							String.format("%1$.2f",
									resultList.get(i).get("TCZC")));
				}
				if (resultList.get(i).get("DBZC") != null) {
					resultList.get(i).put(
							"DBZC",
							String.format("%1$.2f",
									resultList.get(i).get("DBZC")));
				}
				if (resultList.get(i).get("ZXJZFY") != null) {
					resultList.get(i).put(
							"ZXJZFY",
							String.format("%1$.2f",
									resultList.get(i).get("ZXJZFY")));
				}
				if (resultList.get(i).get("GRXJZF") != null) {
					resultList.get(i).put(
							"GRXJZF",
							String.format("%1$.2f",
									resultList.get(i).get("GRXJZF")));
				}
				if (resultList.get(i).get("BCZHZF") != null) {
					resultList.get(i).put(
							"BCZHZF",
							String.format("%1$.2f",
									resultList.get(i).get("BCZHZF")));
				}
				if (resultList.get(i).get("AZQGFY") != null) {
					resultList.get(i).put(
							"AZQGFY",
							String.format("%1$.2f",
									resultList.get(i).get("AZQGFY")));
				}
			}
			Map<String, Object> parametershj = dao
					.doLoad("SELECT sum(ZY_JZXX.CYSR) as ZCYSR,sum(ZY_JZXX.YJJE) as ZYJJE,sum(ZY_JZXX.TPJE) as ZTPJE,sum(ZY_JZXX.FPZS) as ZFPZS,sum(ZY_JZXX.SJZS) as ZSJZS,sum(ZY_JZXX.YSJE) as ZYSJE,sum(ZY_JZXX.YSXJ) as ZYSXJ,sum(ZY_JZXX.ZPZS) as ZZPZS,sum(ZY_JZXX.TYJJ) as ZTYJJ,sum(ZY_JZXX.YSQT) as ZYSQT,sum(ZY_JZXX.QTZS) as ZQTZS,sum(ZY_JZXX.SRJE) as ZSRJE,sum(ZY_JZXX.TCZC) as TCZC,sum(ZY_JZXX.DBZC) as DBZC,sum(ZY_JZXX.ZXJZFY) as ZXJZFY,sum(ZY_JZXX.GRXJZF) as GRXJZF,sum(ZY_JZXX.BCZHZF) as BCZHZF,sum(ZY_JZXX.AZQGFY) as AZQGFY FROM ZY_JZXX ZY_JZXX WHERE ZY_JZXX.HZRQ>=:adt_hzrq_b AND ZY_JZXX.HZRQ<=:adt_hzrq_e AND ZY_JZXX.JGID=:al_jgid",
							parametershzbd);
			String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("
					+ "select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from ZY_FKXX a, ZY_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ " union all "
					+ "select a.FKFS as FKFS, a.FKJE as FKJE from ZY_FKXX a, ZY_ZYJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ " union all "
					+ "SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM ZY_TBKK a WHERE a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"
					+ " union all "
					+ "SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM ZY_TBKK a ,ZY_JKZF b WHERE b.JKXH = a.JKXH and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ ") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
			String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
					+ "SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"
					+ " union all "
					+ "SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM ZY_ZYJS a ,ZY_JSZF b WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,
					parametershzbd);
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(ids_fkfs_hql,
					parametershzbd);
			String qtysFb = "";
			String jzjeSt = "0.00";
			if (ids_fkfs != null && ids_fkfs.size() != 0) {
				for (int i = 0; i < ids_fkfs.size(); i++) {
					qtysFb = qtysFb
							+ ids_fkfs.get(i).get("FKMC")
							+ ":"
							+ String.format("%1$.2f",
									ids_fkfs.get(i).get("FKJE")) + " ";
				}
			}
			if (ids_brxz != null && ids_brxz.size() != 0) {
				for (int i = 0; i < ids_brxz.size(); i++) {
					if (Integer.parseInt(ids_brxz.get(i).get("DBPB") + "") == 0) {
						jzjeSt = String.format(
								"%1$.2f",
								parseDouble(jzjeSt)
										+ (parseDouble(ids_brxz.get(i).get(
												"FYHJ")
												+ "") - parseDouble(ids_brxz
												.get(i).get("ZFHJ") + "")));
					} else {
						qtysFb = qtysFb
								+ ids_brxz.get(i).get("XZMC")
								+ ":"
								+ String.format(
										"%1$.2f",
										(parseDouble(ids_brxz.get(i)
												.get("FYHJ") + "") - parseDouble(ids_brxz
												.get(i).get("ZFHJ") + "")))
								+ " ";
					}
				}
				qtysFb = qtysFb + " " + "记账 :" + jzjeSt + " ";
			}
			response.put("qtysFb", qtysFb);
			if (parametershj.get("ZCYSR") != null) {
				response.put("ZCYSR",
						String.format("%1$.2f", parametershj.get("ZCYSR")));
			} else {
				response.put("ZCYSR", "");
			}
			if (parametershj.get("ZYJJE") != null) {
				response.put("ZYJJE",
						String.format("%1$.2f", parametershj.get("ZYJJE")));
			} else {
				response.put("ZYJJE", "");
			}
			if (parametershj.get("ZTPJE") != null) {
				response.put("ZTPJE",
						String.format("%1$.2f", parametershj.get("ZTPJE")));
			} else {
				response.put("ZTPJE", "");
			}
			if (parametershj.get("ZFPZS") != null) {
				response.put("ZFPZS", parametershj.get("ZFPZS") + "");
			} else {
				response.put("ZFPZS", "");
			}
			if (parametershj.get("ZSJZS") != null) {
				response.put("ZSJZS", parametershj.get("ZSJZS") + "");
			} else {
				response.put("ZSJZS", "");
			}
			if (parametershj.get("ZTYJJ") != null) {
				response.put("ZTYJJ",
						String.format("%1$.2f", parametershj.get("ZTYJJ")));
			} else {
				response.put("ZTYJJ", "");
			}
			if (parametershj.get("ZYSJE") != null) {
				response.put("ZYSJE",
						String.format("%1$.2f", parametershj.get("ZYSJE")));
			} else {
				response.put("ZYSJE", "");
			}
			if (parametershj.get("ZYSXJ") != null) {
				response.put("ZYSXJ",
						String.format("%1$.2f", parametershj.get("ZYSXJ")));
			} else {
				response.put("ZYSXJ", "");

			}
			if (parametershj.get("ZYSQT") != null) {
				response.put("ZYSQT",
						String.format("%1$.2f", parametershj.get("ZYSQT")));
			} else {
				response.put("ZYSQT", "");
			}
			if (parametershj.get("ZQTZS") != null) {
				response.put("ZQTZS", parametershj.get("ZQTZS") + "");
			} else {
				response.put("ZQTZS", "");
			}
			if (parametershj.get("ZSRJE") != null) {
				response.put("ZSRJE",
						String.format("%1$.2f", parametershj.get("ZSRJE")));
			} else {
				response.put("ZSRJE", "");
			}
			if (parametershj.get("TCZC") != null) {
				response.put("TCZCHJ",
						String.format("%1$.2f", parametershj.get("TCZC")));
			} else {
				response.put("TCZCHJ", "");
			}
			if (parametershj.get("DBZC") != null) {
				response.put("DBZCHJ",
						String.format("%1$.2f", parametershj.get("DBZC")));
			} else {
				response.put("DBZCHJ", "");
			}
			if (parametershj.get("ZXJZFY") != null) {
				response.put("ZXJZFYHJ",
						String.format("%1$.2f", parametershj.get("ZXJZFY")));
			} else {
				response.put("ZXJZFYHJ", "");
			}
			if (parametershj.get("GRXJZF") != null) {
				response.put("GRXJZFHJ",
						String.format("%1$.2f", parametershj.get("GRXJZF")));
			} else {
				response.put("GRXJZFHJ", "");
			}
			if (parametershj.get("BCZHZF") != null) {
				response.put("BCZHZFHJ",
						String.format("%1$.2f", parametershj.get("BCZHZF")));
			} else {
				response.put("BCZHZFHJ", "");
			}
			if (parametershj.get("AZQGFY") != null) {
				response.put("AZQGFYHJ",
						String.format("%1$.2f", parametershj.get("AZQGFY")));
			} else {
				response.put("AZQGFYHJ", "");
			}
			// if (parametershj.get("SZYB") != null) {
			// response.put("SZYBHJ",
			// String.format("%1$.2f", parametershj.get("SZYB")));
			// }
			// if (parametershj.get("SYB") != null) {
			// response.put("SYBHJ",
			// String.format("%1$.2f", parametershj.get("SYB")));
			// }
			// if (parametershj.get("YHYB") != null) {
			// response.put("YHYBHJ",
			// String.format("%1$.2f", parametershj.get("YHYB")));
			// }
			// if (parametershj.get("SMK") != null) {
			// response.put("SMKHJ",
			// String.format("%1$.2f", parametershj.get("SMK")));
			// }
			List<Map<String, Object>> zfzslist = new ArrayList<Map<String, Object>>();
			StringBuffer sbfp = new StringBuffer();
			StringBuffer sbsj = new StringBuffer();
			zfzslist = dao
					.doQuery(
							"SELECT ZY_ZFPJ.PJLB as PJLB,ZY_ZFPJ.PJHM as PJHM FROM ZY_ZFPJ ZY_ZFPJ,ZY_JZXX ZY_JZXX WHERE ( ZY_ZFPJ.JZRQ = ZY_JZXX.JZRQ ) AND ( ZY_ZFPJ.CZGH = ZY_JZXX.CZGH ) AND ZY_JZXX.HZRQ>=:adt_hzrq_b AND ZY_JZXX.HZRQ<=:adt_hzrq_e AND ZY_JZXX.JGID=:al_jgid ORDER BY ZY_ZFPJ.PJLB,ZY_ZFPJ.PJHM",
							parametershzbd);
			for (int i = 0; i < zfzslist.size(); i++) {
				if (Integer.parseInt(zfzslist.get(i).get("PJLB") + "") == 1) {
					if (zfzslist.get(i).get("PJHM") != null) {
						sbfp.append(zfzslist.get(i).get("PJHM") + " ");
					}
				} else {
					if (zfzslist.get(i).get("PJHM") != null) {
						sbsj.append(zfzslist.get(i).get("PJHM") + " ");
					}
				}
			}
			if (sbfp.toString() != null && sbfp.length() > 0) {
				response.put(
						"ZFFPHM",
						sbfp.toString().substring(0,
								sbfp.toString().length() - 1));
			} else {
				response.put("ZFFPHM", "");
			}
			if (sbsj.toString() != null && sbsj.length() > 0) {
				response.put(
						"ZFSJHM",
						sbsj.toString().substring(0,
								sbsj.toString().length() - 1));
			} else {
				response.put("ZFSJHM", "");
			}
			List<Map<String, Object>> zfpjlist = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < resultList.size(); i++) {
				Map<String, Object> parametersczgh = new HashMap<String, Object>();
				parametersczgh.put("adt_hzrq_b", adt_hzrq_start);
				parametersczgh.put("adt_hzrq_e", adt_hzrq_end);
				parametersczgh.put("as_czgh", resultList.get(i).get("CZGH"));
				parametersczgh.put("gl_jgid", manaUnitId);
				StringBuffer ls_jsfp_all = new StringBuffer();
				StringBuffer ls_jssj_all = new StringBuffer();
				zfpjlist = dao
						.doQuery(
								"SELECT QZPJ as QZPJ,QZSJ as QZSJ From ZY_JZXX Where CZGH=:as_czgh And HZRQ>=:adt_hzrq_b And HZRQ<=:adt_hzrq_e And JGID=:gl_jgid",
								parametersczgh);
				for (int j = 0; j < zfpjlist.size(); j++) {
					if (zfpjlist.get(j).get("QZPJ") != null) {
						ls_jsfp_all.append(zfpjlist.get(j).get("QZPJ") + "");
					}
					if (zfpjlist.get(j).get("QZSJ") != null) {
						ls_jssj_all.append(zfpjlist.get(j).get("QZSJ") + "");
					}
				}
				if (ls_jsfp_all.toString() != null && ls_jsfp_all.length() > 0) {
					resultList.get(i).put("QZPJ", ls_jsfp_all.toString());
				} else {
					resultList.get(i).put("QZPJ", "");
				}
				if (ls_jssj_all.toString() != null && ls_jssj_all.length() > 0) {
					resultList.get(i).put("QZSJ", ls_jssj_all.toString());
				} else {
					resultList.get(i).put("QZSJ", "");
				}
			}
			resultLi.addAll(resultList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param adt_hzrq_start
	 *            家床汇总开始日期
	 * @param adt_hzrq_end
	 *            汇总结束日期
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public static void fsb_wf_Query(Map<String, Object> request,
			Map<String, Object> response, List<Map<String, Object>> resultLi,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// User user = (User) ctx.get("user.instance");
		// String jgname = user.get("manageUnit.name");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		response.put("title", jgname);
		Date adt_hzrq_start = null;
		Date adt_hzrq_end = null;
		try {
			if (request.get("beginDate") != null) {
				adt_hzrq_start = sdfdate.parse(request.get("beginDate") + "");
			}
			if (request.get("endDate") != null) {
				adt_hzrq_end = sdfdate.parse(request.get("endDate") + "");
			}
			String[] lS_ksrq = (request.get("beginDate") + "").split("-| ");
			String ksrq = lS_ksrq[0] + "年" + lS_ksrq[1] + "月" + lS_ksrq[2]
					+ "日";
			String[] lS_jsrq = (request.get("endDate") + "").split("-| ");
			String jsrq = lS_jsrq[0] + "年" + lS_jsrq[1] + "月" + lS_jsrq[2]
					+ "日";
			response.put("HZRQ", "汇总日期:" + ksrq + " 至 " + jsrq);
			Map<String, Object> parametershzbd = new HashMap<String, Object>();
			parametershzbd.put("adt_hzrq_b", adt_hzrq_start);
			parametershzbd.put("adt_hzrq_e", adt_hzrq_end);
			parametershzbd.put("al_jgid", manaUnitId);
			// 日结汇总汇总表单
			// List<Map<String, Object>> resultList2 = dao
			// .doQuery(
			// "SELECT JC_JZXX.QTYSFB as QTYSFB FROM JC_JZXX JC_JZXX WHERE JC_JZXX.HZRQ>=:adt_hzrq_b AND JC_JZXX.HZRQ<=:adt_hzrq_e AND JC_JZXX.JGID=:al_jgid ",
			// parametershzbd);
			// if(resultList2.size()>0){
			// response.put("qtysFb",resultList2.get(0).get("QTYSFB")+"");
			// }
			List<Map<String, Object>> resultList = dao
					.doQuery(
							"SELECT JC_JZXX.CZGH as CZGH,sum(JC_JZXX.CYSR) as CYSR,sum(JC_JZXX.YJJE) as YJJE,sum(JC_JZXX.TPJE) as TPJE,sum(JC_JZXX.FPZS) as FPZS,sum(JC_JZXX.SJZS) as SJZS,sum(JC_JZXX.YSJE) as YSJE,sum(JC_JZXX.YSXJ) as YSXJ,sum(JC_JZXX.ZPZS) as ZPZS,sum(JC_JZXX.TYJJ) as TYJJ,sum(JC_JZXX.YSQT) as YSQT,sum(JC_JZXX.QTZS) as QTZS,sum(JC_JZXX.SRJE) as SRJE,sum(JC_JZXX.TCZC) as TCZC,sum(JC_JZXX.DBZC) as DBZC,sum(JC_JZXX.ZXJZFY) as ZXJZFY,sum(JC_JZXX.GRXJZF) as GRXJZF,sum(JC_JZXX.BCZHZF) as BCZHZF,sum(JC_JZXX.AZQGFY) as AZQGFY FROM JC_JZXX JC_JZXX WHERE JC_JZXX.HZRQ>=:adt_hzrq_b AND JC_JZXX.HZRQ<=:adt_hzrq_e AND JC_JZXX.JGID=:al_jgid group by JC_JZXX.CZGH order by JC_JZXX.CZGH",
							parametershzbd);
			for (int i = 0; i < resultList.size(); i++) {
				// for(int j=0;j<resultList2.size();j++){
				// if(i==j){
				// resultList.get(i).put("QTYSFB",
				// resultList2.get(j).get("QTYSFB"));
				// }
				// }
				String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("
						+ "select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from JC_FKXX a, JC_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.ZFGH = :czgh"
						+ " union all "
						+ "select a.FKFS as FKFS, a.FKJE as FKJE from JC_FKXX a, JC_JCJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.CZGH = :czgh"
						+ " union all "
						+ "SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM JC_TBKK a WHERE a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid and a.CZGH = :czgh"
						+ " union all "
						+ "SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM JC_TBKK a ,JC_JKZF b WHERE b.JKXH = a.JKXH and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.ZFGH = :czgh"
						+ ") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
				String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
						+ "SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid and a.CZGH = :czgh"
						+ " union all "
						+ "SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a ,JC_JSZF b WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid and b.ZFGH = :czgh"
						+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("adt_hzrq_b", adt_hzrq_start);
				parameters.put("adt_hzrq_e", adt_hzrq_end);
				parameters.put("al_jgid", manaUnitId);
				parameters.put("czgh", resultList.get(i).get("CZGH") + "");
				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(
						ids_brxz_hql, parameters);
				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(
						ids_fkfs_hql, parameters);
				String qtysFb = "";
				String jzjeSt = "0.00";
				if (ids_fkfs != null && ids_fkfs.size() != 0) {
					for (int n = 0; n < ids_fkfs.size(); n++) {
						qtysFb = qtysFb
								+ ids_fkfs.get(n).get("FKMC")
								+ ":"
								+ String.format("%1$.2f",
										ids_fkfs.get(n).get("FKJE")) + " ";
					}
				}
				if (ids_brxz != null && ids_brxz.size() != 0) {
					for (int n = 0; n < ids_brxz.size(); n++) {
						if (Integer.parseInt(ids_brxz.get(n).get("DBPB") + "") == 0) {
							jzjeSt = String
									.format("%1$.2f",
											parseDouble(jzjeSt)
													+ (parseDouble(ids_brxz
															.get(n).get("FYHJ")
															+ "") - parseDouble(ids_brxz
															.get(n).get("ZFHJ")
															+ "")));
						} else {
							qtysFb = qtysFb
									+ ids_brxz.get(n).get("XZMC")
									+ ":"
									+ String.format(
											"%1$.2f",
											(parseDouble(ids_brxz.get(n).get(
													"FYHJ")
													+ "") - parseDouble(ids_brxz
													.get(n).get("ZFHJ") + "")))
									+ " ";
						}
					}
					qtysFb = qtysFb + " " + "记账 :" + jzjeSt + " ";
				}
				resultList.get(i).put("QTYSFB", qtysFb);
				resultList.get(i).put("CYSR",
						String.format("%1$.2f", resultList.get(i).get("CYSR")));
				resultList.get(i).put("YJJE",
						String.format("%1$.2f", resultList.get(i).get("YJJE")));
				resultList.get(i).put("TPJE",
						String.format("%1$.2f", resultList.get(i).get("TPJE")));
				resultList.get(i).put("YSJE",
						String.format("%1$.2f", resultList.get(i).get("YSJE")));
				resultList.get(i).put("YSXJ",
						String.format("%1$.2f", resultList.get(i).get("YSXJ")));
				resultList.get(i).put("TYJJ",
						String.format("%1$.2f", resultList.get(i).get("TYJJ")));
				resultList.get(i).put("YSQT",
						String.format("%1$.2f", resultList.get(i).get("YSQT")));
				resultList.get(i).put("SRJE",
						String.format("%1$.2f", resultList.get(i).get("SRJE")));
				// if (resultList.get(i).get("SZYB") != null) {
				// resultList.get(i).put(
				// "SZYB",
				// String.format("%1$.2f",
				// resultList.get(i).get("SZYB")));
				// }
				// if (resultList.get(i).get("SYB") != null) {
				// resultList.get(i).put(
				// "SYB",
				// String.format("%1$.2f", resultList.get(i)
				// .get("SYB")));
				// }
				// if (resultList.get(i).get("YHYB") != null) {
				// resultList.get(i).put(
				// "YHYB",
				// String.format("%1$.2f",
				// resultList.get(i).get("YHYB")));
				// }
				// if (resultList.get(i).get("SMK") != null) {
				// resultList.get(i).put(
				// "SMK",
				// String.format("%1$.2f", resultList.get(i)
				// .get("SMK")));
				// }
				if (resultList.get(i).get("TCZC") != null) {
					resultList.get(i).put(
							"TCZC",
							String.format("%1$.2f",
									resultList.get(i).get("TCZC")));
				}
				if (resultList.get(i).get("DBZC") != null) {
					resultList.get(i).put(
							"DBZC",
							String.format("%1$.2f",
									resultList.get(i).get("DBZC")));
				}
				if (resultList.get(i).get("ZXJZFY") != null) {
					resultList.get(i).put(
							"ZXJZFY",
							String.format("%1$.2f",
									resultList.get(i).get("ZXJZFY")));
				}
				if (resultList.get(i).get("GRXJZF") != null) {
					resultList.get(i).put(
							"GRXJZF",
							String.format("%1$.2f",
									resultList.get(i).get("GRXJZF")));
				}
				if (resultList.get(i).get("BCZHZF") != null) {
					resultList.get(i).put(
							"BCZHZF",
							String.format("%1$.2f",
									resultList.get(i).get("BCZHZF")));
				}
				if (resultList.get(i).get("AZQGFY") != null) {
					resultList.get(i).put(
							"AZQGFY",
							String.format("%1$.2f",
									resultList.get(i).get("AZQGFY")));
				}
			}
			Map<String, Object> parametershj = dao
					.doLoad("SELECT sum(JC_JZXX.CYSR) as ZCYSR,sum(JC_JZXX.YJJE) as ZYJJE,sum(JC_JZXX.TPJE) as ZTPJE,sum(JC_JZXX.FPZS) as ZFPZS,sum(JC_JZXX.SJZS) as ZSJZS,sum(JC_JZXX.YSJE) as ZYSJE,sum(JC_JZXX.YSXJ) as ZYSXJ,sum(JC_JZXX.ZPZS) as ZZPZS,sum(JC_JZXX.TYJJ) as ZTYJJ,sum(JC_JZXX.YSQT) as ZYSQT,sum(JC_JZXX.QTZS) as ZQTZS,sum(JC_JZXX.SRJE) as ZSRJE,sum(JC_JZXX.TCZC) as TCZC,sum(JC_JZXX.DBZC) as DBZC,sum(JC_JZXX.ZXJZFY) as ZXJZFY,sum(JC_JZXX.GRXJZF) as GRXJZF,sum(JC_JZXX.BCZHZF) as BCZHZF,sum(JC_JZXX.AZQGFY) as AZQGFY FROM JC_JZXX JC_JZXX WHERE JC_JZXX.HZRQ>=:adt_hzrq_b AND JC_JZXX.HZRQ<=:adt_hzrq_e AND JC_JZXX.JGID=:al_jgid",
							parametershzbd);
			String ids_fkfs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("
					+ "select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from JC_FKXX a, JC_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ " union all "
					+ "select a.FKFS as FKFS, a.FKJE as FKJE from JC_FKXX a, JC_JCJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ " union all "
					+ "SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM JC_TBKK a WHERE a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"
					+ " union all "
					+ "SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM JC_TBKK a ,JC_JKZF b WHERE b.JKXH = a.JKXH and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ ") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
			String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
					+ "SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a WHERE a.JSLX<>4 and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq_b AND a.HZRQ<=:adt_hzrq_e AND a.JGID=:al_jgid"
					+ " union all "
					+ "SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a ,JC_JSZF b WHERE a.JSLX<>4 and a.ZYH = b.ZYH AND a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and b.HZRQ>=:adt_hzrq_b AND b.HZRQ<=:adt_hzrq_e AND b.JGID=:al_jgid"
					+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,
					parametershzbd);
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(ids_fkfs_hql,
					parametershzbd);
			String qtysFb = "";
			String jzjeSt = "0.00";
			if (ids_fkfs != null && ids_fkfs.size() != 0) {
				for (int i = 0; i < ids_fkfs.size(); i++) {
					qtysFb = qtysFb
							+ ids_fkfs.get(i).get("FKMC")
							+ ":"
							+ String.format("%1$.2f",
									ids_fkfs.get(i).get("FKJE")) + " ";
				}
			}
			if (ids_brxz != null && ids_brxz.size() != 0) {
				for (int i = 0; i < ids_brxz.size(); i++) {
					if (Integer.parseInt(ids_brxz.get(i).get("DBPB") + "") == 0) {
						jzjeSt = String.format(
								"%1$.2f",
								parseDouble(jzjeSt)
										+ (parseDouble(ids_brxz.get(i).get(
												"FYHJ")
												+ "") - parseDouble(ids_brxz
												.get(i).get("ZFHJ") + "")));
					} else {
						qtysFb = qtysFb
								+ ids_brxz.get(i).get("XZMC")
								+ ":"
								+ String.format(
										"%1$.2f",
										(parseDouble(ids_brxz.get(i)
												.get("FYHJ") + "") - parseDouble(ids_brxz
												.get(i).get("ZFHJ") + "")))
								+ " ";
					}
				}
				qtysFb = qtysFb + " " + "记账 :" + jzjeSt + " ";
			}
			response.put("qtysFb", qtysFb);
			if (parametershj.get("ZCYSR") != null) {
				response.put("ZCYSR",
						String.format("%1$.2f", parametershj.get("ZCYSR")));
			} else {
				response.put("ZCYSR", "");
			}
			if (parametershj.get("ZYJJE") != null) {
				response.put("ZYJJE",
						String.format("%1$.2f", parametershj.get("ZYJJE")));
			} else {
				response.put("ZYJJE", "");
			}
			if (parametershj.get("ZTPJE") != null) {
				response.put("ZTPJE",
						String.format("%1$.2f", parametershj.get("ZTPJE")));
			} else {
				response.put("ZTPJE", "");
			}
			if (parametershj.get("ZFPZS") != null) {
				response.put("ZFPZS", parametershj.get("ZFPZS") + "");
			} else {
				response.put("ZFPZS", "");
			}
			if (parametershj.get("ZSJZS") != null) {
				response.put("ZSJZS", parametershj.get("ZSJZS") + "");
			} else {
				response.put("ZSJZS", "");
			}
			if (parametershj.get("ZTYJJ") != null) {
				response.put("ZTYJJ",
						String.format("%1$.2f", parametershj.get("ZTYJJ")));
			} else {
				response.put("ZTYJJ", "");
			}
			if (parametershj.get("ZYSJE") != null) {
				response.put("ZYSJE",
						String.format("%1$.2f", parametershj.get("ZYSJE")));
			} else {
				response.put("ZYSJE", "");
			}
			if (parametershj.get("ZYSXJ") != null) {
				response.put("ZYSXJ",
						String.format("%1$.2f", parametershj.get("ZYSXJ")));
			} else {
				response.put("ZYSXJ", "");

			}
			if (parametershj.get("ZYSQT") != null) {
				response.put("ZYSQT",
						String.format("%1$.2f", parametershj.get("ZYSQT")));
			} else {
				response.put("ZYSQT", "");
			}
			if (parametershj.get("ZQTZS") != null) {
				response.put("ZQTZS", parametershj.get("ZQTZS") + "");
			} else {
				response.put("ZQTZS", "");
			}
			if (parametershj.get("ZSRJE") != null) {
				response.put("ZSRJE",
						String.format("%1$.2f", parametershj.get("ZSRJE")));
			} else {
				response.put("ZSRJE", "");
			}
			if (parametershj.get("TCZC") != null) {
				response.put("TCZCHJ",
						String.format("%1$.2f", parametershj.get("TCZC")));
			} else {
				response.put("TCZCHJ", "");
			}
			if (parametershj.get("DBZC") != null) {
				response.put("DBZCHJ",
						String.format("%1$.2f", parametershj.get("DBZC")));
			} else {
				response.put("DBZCHJ", "");
			}
			if (parametershj.get("ZXJZFY") != null) {
				response.put("ZXJZFYHJ",
						String.format("%1$.2f", parametershj.get("ZXJZFY")));
			} else {
				response.put("ZXJZFYHJ", "");
			}
			if (parametershj.get("GRXJZF") != null) {
				response.put("GRXJZFHJ",
						String.format("%1$.2f", parametershj.get("GRXJZF")));
			} else {
				response.put("GRXJZFHJ", "");
			}
			if (parametershj.get("BCZHZF") != null) {
				response.put("BCZHZFHJ",
						String.format("%1$.2f", parametershj.get("BCZHZF")));
			} else {
				response.put("BCZHZFHJ", "");
			}
			if (parametershj.get("AZQGFY") != null) {
				response.put("AZQGFYHJ",
						String.format("%1$.2f", parametershj.get("AZQGFY")));
			} else {
				response.put("AZQGFYHJ", "");
			}
			// if (parametershj.get("SZYB") != null) {
			// response.put("SZYBHJ",
			// String.format("%1$.2f", parametershj.get("SZYB")));
			// }
			// if (parametershj.get("SYB") != null) {
			// response.put("SYBHJ",
			// String.format("%1$.2f", parametershj.get("SYB")));
			// }
			// if (parametershj.get("YHYB") != null) {
			// response.put("YHYBHJ",
			// String.format("%1$.2f", parametershj.get("YHYB")));
			// }
			// if (parametershj.get("SMK") != null) {
			// response.put("SMKHJ",
			// String.format("%1$.2f", parametershj.get("SMK")));
			// }
			List<Map<String, Object>> zfzslist = new ArrayList<Map<String, Object>>();
			StringBuffer sbfp = new StringBuffer();
			StringBuffer sbsj = new StringBuffer();
			zfzslist = dao
					.doQuery(
							"SELECT JC_ZFPJ.PJLB as PJLB,JC_ZFPJ.PJHM as PJHM FROM JC_ZFPJ JC_ZFPJ,JC_JZXX JC_JZXX WHERE ( JC_ZFPJ.JZRQ = JC_JZXX.JZRQ ) AND ( JC_ZFPJ.CZGH = JC_JZXX.CZGH ) AND JC_JZXX.HZRQ>=:adt_hzrq_b AND JC_JZXX.HZRQ<=:adt_hzrq_e AND JC_JZXX.JGID=:al_jgid ORDER BY JC_ZFPJ.PJLB,JC_ZFPJ.PJHM",
							parametershzbd);
			for (int i = 0; i < zfzslist.size(); i++) {
				if (Integer.parseInt(zfzslist.get(i).get("PJLB") + "") == 1) {
					if (zfzslist.get(i).get("PJHM") != null) {
						sbfp.append(zfzslist.get(i).get("PJHM") + " ");
					}
				} else {
					if (zfzslist.get(i).get("PJHM") != null) {
						sbsj.append(zfzslist.get(i).get("PJHM") + " ");
					}
				}
			}
			if (sbfp.toString() != null && sbfp.length() > 0) {
				response.put(
						"ZFFPHM",
						sbfp.toString().substring(0,
								sbfp.toString().length() - 1));
			} else {
				response.put("ZFFPHM", "");
			}
			if (sbsj.toString() != null && sbsj.length() > 0) {
				response.put(
						"ZFSJHM",
						sbsj.toString().substring(0,
								sbsj.toString().length() - 1));
			} else {
				response.put("ZFSJHM", "");
			}
			List<Map<String, Object>> zfpjlist = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < resultList.size(); i++) {
				Map<String, Object> parametersczgh = new HashMap<String, Object>();
				parametersczgh.put("adt_hzrq_b", adt_hzrq_start);
				parametersczgh.put("adt_hzrq_e", adt_hzrq_end);
				parametersczgh.put("as_czgh", resultList.get(i).get("CZGH"));
				parametersczgh.put("gl_jgid", manaUnitId);
				StringBuffer ls_jsfp_all = new StringBuffer();
				StringBuffer ls_jssj_all = new StringBuffer();
				zfpjlist = dao
						.doQuery(
								"SELECT QZPJ as QZPJ,QZSJ as QZSJ From JC_JZXX Where CZGH=:as_czgh And HZRQ>=:adt_hzrq_b And HZRQ<=:adt_hzrq_e And JGID=:gl_jgid",
								parametersczgh);
				for (int j = 0; j < zfpjlist.size(); j++) {
					if (zfpjlist.get(j).get("QZPJ") != null) {
						ls_jsfp_all.append(zfpjlist.get(j).get("QZPJ") + "");
					}
					if (zfpjlist.get(j).get("QZSJ") != null) {
						ls_jssj_all.append(zfpjlist.get(j).get("QZSJ") + "");
					}
				}
				if (ls_jsfp_all.toString() != null && ls_jsfp_all.length() > 0) {
					resultList.get(i).put("QZPJ", ls_jsfp_all.toString());
				} else {
					resultList.get(i).put("QZPJ", "");
				}
				if (ls_jssj_all.toString() != null && ls_jssj_all.length() > 0) {
					resultList.get(i).put("QZSJ", ls_jssj_all.toString());
				} else {
					resultList.get(i).put("QZSJ", "");
				}
			}
			resultLi.addAll(resultList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param parameters里参数分别是ldt_begin
	 *            (开始时间)、ldt_end(结束时间)、gl_jgid(机构id)、ii_tjfs(提交方式)
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public static List<Map<String, Object>> wf_tj_mzhs(
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> listMS_MZHS = new ArrayList<Map<String, Object>>();
		try {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			int ii_tjfs = Integer.parseInt(parameters.get("ii_tjfs") + "");
			parameters.remove("ii_tjfs");
			String sql = "";
			if (ii_tjfs == 1) {
				sql = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF01.CFLX as CFLX,count(MS_CF01.CFSB) AS CFZS,SUM(MS_CF02.HJJE) AS HJJE FROM MS_CF01 MS_CF01, MS_MZXX MS_MZXX, MS_CF02 MS_CF02 WHERE (MS_CF02.CFSB = MS_CF01.CFSB) AND (MS_MZXX.MZXH = MS_CF01.MZXH) AND (MS_MZXX.SFRQ >= :ldt_begin) AND (MS_MZXX.SFRQ <= :ldt_end) AND (MS_MZXX.JGID = :gl_jgid) GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF01.CFLX ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF01.CFLX";
			} else if (ii_tjfs == 2) {
				sql = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF01.CFLX as CFLX,count(MS_CF01.CFSB) AS CFZS,SUM(MS_CF02.HJJE) AS HJJE FROM MS_CF01 MS_CF01, MS_MZXX MS_MZXX, MS_CF02 MS_CF02 WHERE (MS_CF02.CFSB = MS_CF01.CFSB) AND (MS_MZXX.MZXH = MS_CF01.MZXH) AND (MS_MZXX.JZRQ >= :ldt_begin) AND (MS_MZXX.JZRQ <= :ldt_end) AND (MS_MZXX.JGID = :gl_jgid) GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF01.CFLX ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF01.CFLX";
			} else if (ii_tjfs == 3) {
				sql = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF01.CFLX as CFLX,count(MS_CF01.CFSB) AS CFZS,SUM(MS_CF02.HJJE) AS HJJE FROM MS_CF01 MS_CF01, MS_MZXX MS_MZXX, MS_CF02 MS_CF02 WHERE (MS_CF02.CFSB = MS_CF01.CFSB) AND (MS_MZXX.MZXH = MS_CF01.MZXH) AND (MS_MZXX.HZRQ >= :ldt_begin) AND (MS_MZXX.HZRQ <= :ldt_end) AND (MS_MZXX.JGID = :gl_jgid) GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF01.CFLX ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF01.CFLX";
			}
			// 处方张数临时表单
			List<Map<String, Object>> cf01andmzxxList = dao.doQuery(sql,
					parameters);
			for (int i = 0; i < cf01andmzxxList.size(); i++) {
				int sign = 0;
				Long li_ksdm = 0L;
				if (cf01andmzxxList.get(i).get("KSDM") != null) {
					li_ksdm = Long.parseLong(cf01andmzxxList.get(i).get("KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (cf01andmzxxList.get(i).get("YSDM") != null) {
					ls_ysdm = cf01andmzxxList.get(i).get("YSDM") + "";
				}
				int li_cflx = 0;
				if (cf01andmzxxList.get(i).get("CFLX") != null) {
					li_cflx = Integer.parseInt(cf01andmzxxList.get(i).get(
							"CFLX")
							+ "");
				}

				int li_cfzs = 0;
				if (cf01andmzxxList.get(i).get("CFZS") != null) {
					li_cfzs = Integer.parseInt(cf01andmzxxList.get(i).get(
							"CFZS")
							+ "");
				}
				double ld_hjje = 0.00;
				if (cf01andmzxxList.get(i).get("HJJE") != null) {
					ld_hjje = Double.parseDouble(String.format("%1$.2f",
							cf01andmzxxList.get(i).get("HJJE")));
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (cf01andmzxxList.get(i).get("SFRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(cf01andmzxxList.get(i)
								.get("SFRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 2) {
					if (cf01andmzxxList.get(i).get("JZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(cf01andmzxxList.get(i)
								.get("JZRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 3) {
					if (cf01andmzxxList.get(i).get("HZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(cf01andmzxxList.get(i)
								.get("HZRQ") + "");
					} else {
						continue;
					}
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				if (listMS_MZHS.size() <= 0) {
					Map<String, Object> mapMS_MZHS = new HashMap<String, Object>();
					mapMS_MZHS.put("KSDM", li_ksdm);
					mapMS_MZHS.put("YSDM", ls_ysdm);
					mapMS_MZHS.put("GZRQ", ldt_sfrq);
					mapMS_MZHS.put("TJFS", ii_tjfs);
					mapMS_MZHS.put("HJJE", ld_hjje);
					mapMS_MZHS.put("JCD", 0);
					if (li_cflx == 1) {
						mapMS_MZHS.put("XYF", li_cfzs);
						mapMS_MZHS.put("ZYF", 0);
						mapMS_MZHS.put("CYF", 0);
					} else if (li_cflx == 2) {
						mapMS_MZHS.put("XYF", 0);
						mapMS_MZHS.put("ZYF", li_cfzs);
						mapMS_MZHS.put("CYF", 0);

					} else if (li_cflx == 3) {
						mapMS_MZHS.put("XYF", 0);
						mapMS_MZHS.put("ZYF", 0);
						mapMS_MZHS.put("CYF", li_cfzs);
					}
					mapMS_MZHS.put("MZRC", 0);
					listMS_MZHS.add(mapMS_MZHS);
				} else {
					for (int j = 0; j < listMS_MZHS.size(); j++) {
						if (Long.parseLong(listMS_MZHS.get(j).get("KSDM") + "") == li_ksdm
								&& listMS_MZHS.get(j).get("YSDM").toString()
										.equals(ls_ysdm)
								&& sdfdatetime.parse(
										sdfdatetime.format(listMS_MZHS.get(j)
												.get("GZRQ"))).getTime() == ldt_sfrq
										.getTime()
								&& Integer.parseInt(listMS_MZHS.get(j).get(
										"TJFS")
										+ "") == ii_tjfs) {
							if (li_cflx == 1) {
								int li_zs = 0;
								if (listMS_MZHS.get(j).get("XYF") != null) {
									li_zs = Integer.parseInt(listMS_MZHS.get(j)
											.get("XYF") + "");
									listMS_MZHS.get(j).put("XYF",
											li_zs + li_cfzs);
								}

							} else if (li_cflx == 2) {
								int li_zs = 0;
								if (listMS_MZHS.get(j).get("ZYF") != null) {
									li_zs = Integer.parseInt(listMS_MZHS.get(j)
											.get("ZYF") + "");
									listMS_MZHS.get(j).put("ZYF",
											li_zs + li_cfzs);
								}
							} else if (li_cflx == 3) {
								int li_zs = 0;
								if (listMS_MZHS.get(j).get("CYF") != null) {
									li_zs = Integer.parseInt(listMS_MZHS.get(j)
											.get("CYF") + "");
									listMS_MZHS.get(j).put("CYF",
											li_zs + li_cfzs);
								}
							}
							listMS_MZHS.get(j).put(
									"HJJE",
									Double.parseDouble(String.format(
											"%1$.2f",
											Double.parseDouble(listMS_MZHS.get(
													j).get("HJJE")
													+ "")
													+ ld_hjje)));
							sign = 1;
						}
					}
					if (sign == 0) {
						Map<String, Object> mapMS_MZHS = new HashMap<String, Object>();
						mapMS_MZHS.put("KSDM", li_ksdm);
						mapMS_MZHS.put("YSDM", ls_ysdm);
						mapMS_MZHS.put("GZRQ", ldt_sfrq);
						mapMS_MZHS.put("TJFS", ii_tjfs);
						mapMS_MZHS.put("HJJE", ld_hjje);
						mapMS_MZHS.put("JCD", 0);
						if (li_cflx == 1) {
							mapMS_MZHS.put("XYF", li_cfzs);
							mapMS_MZHS.put("ZYF", 0);
							mapMS_MZHS.put("CYF", 0);
						} else if (li_cflx == 2) {
							mapMS_MZHS.put("XYF", 0);
							mapMS_MZHS.put("ZYF", li_cfzs);
							mapMS_MZHS.put("CYF", 0);

						} else if (li_cflx == 3) {
							mapMS_MZHS.put("XYF", 0);
							mapMS_MZHS.put("ZYF", 0);
							mapMS_MZHS.put("CYF", li_cfzs);
						}
						mapMS_MZHS.put("MZRC", 0);
						listMS_MZHS.add(mapMS_MZHS);
					}
				}
			}
			// 处方作废张数临时表单
			String sql2 = "";
			if (ii_tjfs == 1) {
				sql2 = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF01.CFLX as CFLX,count(MS_CF01.CFSB) AS CFZS,SUM(MS_CF02.HJJE) AS HJJE FROM MS_CF01 MS_CF01,MS_ZFFP MS_ZFFP,MS_MZXX MS_MZXX,MS_CF02 MS_CF02 WHERE (MS_CF02.CFSB = MS_CF01.CFSB) AND (MS_ZFFP.MZXH = MS_CF01.MZXH) AND (MS_ZFFP.MZXH = MS_MZXX.MZXH) AND MS_ZFFP.ZFRQ >= :ldt_begin And MS_ZFFP.ZFRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF01.CFLX ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF01.CFLX";
			} else if (ii_tjfs == 2) {
				sql2 = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF01.CFLX as CFLX,count(MS_CF01.CFSB) AS CFZS,SUM(MS_CF02.HJJE) AS HJJE FROM MS_CF01 MS_CF01,MS_ZFFP MS_ZFFP,MS_MZXX MS_MZXX,MS_CF02 MS_CF02 WHERE (MS_CF02.CFSB = MS_CF01.CFSB) AND (MS_ZFFP.MZXH = MS_CF01.MZXH) AND (MS_ZFFP.MZXH = MS_MZXX.MZXH) AND MS_ZFFP.JZRQ >= :ldt_begin And MS_ZFFP.JZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF01.CFLX ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF01.CFLX";
			} else if (ii_tjfs == 3) {
				sql2 = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF01.CFLX as CFLX,count(MS_CF01.CFSB) AS CFZS,SUM(MS_CF02.HJJE) AS HJJE FROM MS_CF01 MS_CF01,MS_ZFFP MS_ZFFP,MS_MZXX MS_MZXX,MS_CF02 MS_CF02 WHERE (MS_CF02.CFSB = MS_CF01.CFSB) AND (MS_ZFFP.MZXH = MS_CF01.MZXH) AND (MS_ZFFP.MZXH = MS_MZXX.MZXH) AND MS_ZFFP.HZRQ >= :ldt_begin And MS_ZFFP.HZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF01.CFLX ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF01.CFLX";
			}
			List<Map<String, Object>> cf01andmzxxzfList = dao.doQuery(sql2,
					parameters);
			for (int i = 0; i < cf01andmzxxzfList.size(); i++) {
				int signzf = 0;
				Long li_ksdm = 0L;
				if (cf01andmzxxzfList.get(i).get("KSDM") != null) {
					li_ksdm = Long.parseLong(cf01andmzxxzfList.get(i).get(
							"KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (cf01andmzxxzfList.get(i).get("YSDM") != null) {
					ls_ysdm = cf01andmzxxzfList.get(i).get("YSDM") + "";
				}
				int li_cflx = 0;
				if (cf01andmzxxzfList.get(i).get("CFLX") != null) {
					li_cflx = Integer.parseInt(cf01andmzxxzfList.get(i).get(
							"CFLX")
							+ "");
				}

				int li_cfzs = 0;
				if (cf01andmzxxzfList.get(i).get("CFZS") != null) {
					li_cfzs = Integer.parseInt(cf01andmzxxzfList.get(i).get(
							"CFZS")
							+ "");
				}
				double ld_hjje = 0.00;
				if (cf01andmzxxzfList.get(i).get("HJJE") != null) {
					ld_hjje = Double.parseDouble(String.format("%1$.2f",
							cf01andmzxxzfList.get(i).get("HJJE")));
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (cf01andmzxxzfList.get(i).get("SFRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(cf01andmzxxzfList.get(i)
								.get("SFRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 2) {
					if (cf01andmzxxzfList.get(i).get("JZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(cf01andmzxxzfList.get(i)
								.get("JZRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 3) {
					if (cf01andmzxxzfList.get(i).get("HZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(cf01andmzxxzfList.get(i)
								.get("HZRQ") + "");
					} else {
						continue;
					}
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				if (listMS_MZHS.size() <= 0) {
					Map<String, Object> mapzfMS_MZHS = new HashMap<String, Object>();
					mapzfMS_MZHS.put("KSDM", li_ksdm);
					mapzfMS_MZHS.put("YSDM", ls_ysdm);
					mapzfMS_MZHS.put("GZRQ", ldt_sfrq);
					mapzfMS_MZHS.put("TJFS", ii_tjfs);
					mapzfMS_MZHS.put("HJJE", ld_hjje);
					mapzfMS_MZHS.put("JCD", 0);
					if (li_cflx == 1) {
						mapzfMS_MZHS.put("XYF", 0 - li_cfzs);
						mapzfMS_MZHS.put("ZYF", 0);
						mapzfMS_MZHS.put("CYF", 0);
					} else if (li_cflx == 2) {
						mapzfMS_MZHS.put("XYF", 0);
						mapzfMS_MZHS.put("ZYF", 0 - li_cfzs);
						mapzfMS_MZHS.put("CYF", 0);
					} else if (li_cflx == 3) {
						mapzfMS_MZHS.put("XYF", 0);
						mapzfMS_MZHS.put("ZYF", 0);
						mapzfMS_MZHS.put("CYF", 0 - li_cfzs);
					}
					mapzfMS_MZHS.put("MZRC", 0);
					listMS_MZHS.add(mapzfMS_MZHS);
				} else {
					for (int j = 0; j < listMS_MZHS.size(); j++) {
						if (Long.parseLong(listMS_MZHS.get(j).get("KSDM") + "") == li_ksdm
								&& listMS_MZHS.get(j).get("YSDM").toString()
										.equals(ls_ysdm)
								&& sdfdatetime.parse(
										sdfdatetime.format(listMS_MZHS.get(j)
												.get("GZRQ"))).getTime() == ldt_sfrq
										.getTime()
								&& Integer.parseInt(listMS_MZHS.get(j).get(
										"TJFS")
										+ "") == ii_tjfs) {
							if (li_cflx == 1) {
								int li_zs = 0;
								if (listMS_MZHS.get(j).get("XYF") != null) {
									li_zs = Integer.parseInt(listMS_MZHS.get(j)
											.get("XYF") + "");
									listMS_MZHS.get(j).put("XYF",
											li_zs - li_cfzs);
								}

							} else if (li_cflx == 2) {
								int li_zs = 0;
								if (listMS_MZHS.get(j).get("ZYF") != null) {
									li_zs = Integer.parseInt(listMS_MZHS.get(j)
											.get("ZYF") + "");
									listMS_MZHS.get(j).put("ZYF",
											li_zs - li_cfzs);
								}
							} else if (li_cflx == 3) {
								int li_zs = 0;
								if (listMS_MZHS.get(j).get("CYF") != null) {
									li_zs = Integer.parseInt(listMS_MZHS.get(j)
											.get("CYF") + "");
									listMS_MZHS.get(j).put("CYF",
											li_zs - li_cfzs);
								}
							}
							listMS_MZHS.get(j).put(
									"HJJE",
									Double.parseDouble(String.format(
											"%1$.2f",
											Double.parseDouble(listMS_MZHS.get(
													j).get("HJJE")
													+ "")
													- ld_hjje)));
							signzf = 1;
						}
					}
					if (signzf == 0) {
						Map<String, Object> mapzfMS_MZHS = new HashMap<String, Object>();
						mapzfMS_MZHS.put("KSDM", li_ksdm);
						mapzfMS_MZHS.put("YSDM", ls_ysdm);
						mapzfMS_MZHS.put("GZRQ", ldt_sfrq);
						mapzfMS_MZHS.put("TJFS", ii_tjfs);
						mapzfMS_MZHS.put("HJJE", ld_hjje);
						mapzfMS_MZHS.put("JCD", 0);
						if (li_cflx == 1) {
							mapzfMS_MZHS.put("XYF", 0 - li_cfzs);
							mapzfMS_MZHS.put("ZYF", 0);
							mapzfMS_MZHS.put("CYF", 0);
						} else if (li_cflx == 2) {
							mapzfMS_MZHS.put("XYF", 0);
							mapzfMS_MZHS.put("ZYF", 0 - li_cfzs);
							mapzfMS_MZHS.put("CYF", 0);
						} else if (li_cflx == 3) {
							mapzfMS_MZHS.put("XYF", 0);
							mapzfMS_MZHS.put("ZYF", 0);
							mapzfMS_MZHS.put("CYF", 0 - li_cfzs);
						}
						mapzfMS_MZHS.put("MZRC", 0);
						listMS_MZHS.add(mapzfMS_MZHS);
					}
				}
			}
			String sql_yj = "";
			if (ii_tjfs == 1) {
				sql_yj = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,count(MS_YJ01.YJXH) AS JCZS,sum(MS_YJ02.HJJE) AS SFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) AND MS_MZXX.SFRQ >= :ldt_begin And MS_MZXX.SFRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ";
			} else if (ii_tjfs == 2) {
				sql_yj = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,count(MS_YJ01.YJXH) AS JCZS,sum(MS_YJ02.HJJE) AS SFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) AND MS_MZXX.JZRQ >= :ldt_begin And MS_MZXX.JZRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ";
			} else if (ii_tjfs == 3) {
				sql_yj = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,count(MS_YJ01.YJXH) AS JCZS,sum(MS_YJ02.HJJE) AS SFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) AND MS_MZXX.HZRQ >= :ldt_begin And MS_MZXX.HZRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ";
			}
			// 检查张数临时表单
			List<Map<String, Object>> yj01andmzxxList = dao.doQuery(sql_yj,
					parameters);
			for (int i = 0; i < yj01andmzxxList.size(); i++) {
				int sign = 0;
				Long li_ksdm = 0L;
				if (yj01andmzxxList.get(i).get("KSDM") != null) {
					li_ksdm = Long.parseLong(yj01andmzxxList.get(i).get("KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (yj01andmzxxList.get(i).get("YSDM") != null) {
					ls_ysdm = yj01andmzxxList.get(i).get("YSDM") + "";
				}

				int li_jczs = 0;
				if (yj01andmzxxList.get(i).get("JCZS") != null) {
					li_jczs = Integer.parseInt(yj01andmzxxList.get(i).get(
							"JCZS")
							+ "");
				}
				double ld_hjje = 0.00;
				if (yj01andmzxxList.get(i).get("SFJE") != null) {
					ld_hjje = Double.parseDouble(String.format("%1$.2f",
							yj01andmzxxList.get(i).get("SFJE")));
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (yj01andmzxxList.get(i).get("SFRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andmzxxList.get(i)
								.get("SFRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 2) {
					if (yj01andmzxxList.get(i).get("JZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andmzxxList.get(i)
								.get("JZRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 3) {
					if (yj01andmzxxList.get(i).get("HZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andmzxxList.get(i)
								.get("HZRQ") + "");
					} else {
						continue;
					}
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				if (listMS_MZHS.size() <= 0) {
					Map<String, Object> mapMS_MZHS = new HashMap<String, Object>();
					mapMS_MZHS.put("KSDM", li_ksdm);
					mapMS_MZHS.put("YSDM", ls_ysdm);
					mapMS_MZHS.put("GZRQ", ldt_sfrq);
					mapMS_MZHS.put("TJFS", ii_tjfs);
					mapMS_MZHS.put("HJJE", ld_hjje);
					mapMS_MZHS.put("JCD", li_jczs);
					mapMS_MZHS.put("XYF", 0);
					mapMS_MZHS.put("ZYF", 0);
					mapMS_MZHS.put("CYF", 0);
					mapMS_MZHS.put("MZRC", 0);
					listMS_MZHS.add(mapMS_MZHS);
				} else {
					for (int j = 0; j < listMS_MZHS.size(); j++) {
						if (listMS_MZHS.get(j).get("YSDM") != null) {
							if (Long.parseLong(listMS_MZHS.get(j).get("KSDM")
									+ "") == li_ksdm
									&& listMS_MZHS.get(j).get("YSDM")
											.toString().equals(ls_ysdm)
									&& sdfdatetime.parse(
											sdfdatetime.format(listMS_MZHS.get(
													j).get("GZRQ"))).getTime() == ldt_sfrq
											.getTime()
									&& Integer.parseInt(listMS_MZHS.get(j).get(
											"TJFS")
											+ "") == ii_tjfs) {

								listMS_MZHS.get(j).put(
										"JCD",
										Integer.parseInt(listMS_MZHS.get(j)
												.get("JCD") + "")
												+ li_jczs);

								listMS_MZHS.get(j).put(
										"HJJE",
										Double.parseDouble(String.format(
												"%1$.2f",
												Double.parseDouble(listMS_MZHS
														.get(j).get("HJJE")
														+ "") + ld_hjje)));
								sign = 1;
							}
						}
					}
					if (sign == 0) {
						Map<String, Object> mapMS_MZHS = new HashMap<String, Object>();
						mapMS_MZHS.put("KSDM", li_ksdm);
						mapMS_MZHS.put("YSDM", ls_ysdm);
						mapMS_MZHS.put("GZRQ", ldt_sfrq);
						mapMS_MZHS.put("TJFS", ii_tjfs);
						mapMS_MZHS.put("HJJE", ld_hjje);
						mapMS_MZHS.put("JCD", li_jczs);
						mapMS_MZHS.put("XYF", 0);
						mapMS_MZHS.put("ZYF", 0);
						mapMS_MZHS.put("CYF", 0);
						mapMS_MZHS.put("MZRC", 0);
						listMS_MZHS.add(mapMS_MZHS);
					}
				}
			}

			String sql2_yj = "";
			if (ii_tjfs == 1) {
				sql2_yj = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,count(MS_YJ01.YJXH) AS JCZS,sum(MS_YJ02.HJJE) AS SFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND MS_ZFFP.ZFRQ >= :ldt_begin And MS_ZFFP.ZFRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ";
			} else if (ii_tjfs == 2) {
				sql2_yj = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,count(MS_YJ01.YJXH) AS JCZS,sum(MS_YJ02.HJJE) AS SFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND MS_ZFFP.JZRQ >= :ldt_begin And MS_ZFFP.JZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ";
			} else if (ii_tjfs == 3) {
				sql2_yj = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,count(MS_YJ01.YJXH) AS JCZS,sum(MS_YJ02.HJJE) AS SFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND MS_ZFFP.HZRQ >= :ldt_begin And MS_ZFFP.HZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ";
			}
			// 检查作废张数临时表单
			List<Map<String, Object>> yj01andmzxxzfList = dao.doQuery(sql2_yj,
					parameters);
			for (int i = 0; i < yj01andmzxxzfList.size(); i++) {
				int sign = 0;
				Long li_ksdm = 0L;
				if (yj01andmzxxzfList.get(i).get("KSDM") != null) {
					li_ksdm = Long.parseLong(yj01andmzxxzfList.get(i).get(
							"KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (yj01andmzxxzfList.get(i).get("YSDM") != null) {
					ls_ysdm = yj01andmzxxzfList.get(i).get("YSDM") + "";
				}

				int li_jczs = 0;
				if (yj01andmzxxzfList.get(i).get("JCZS") != null) {
					li_jczs = Integer.parseInt(yj01andmzxxzfList.get(i).get(
							"JCZS")
							+ "");
				}
				double ld_hjje = 0.00;
				if (yj01andmzxxzfList.get(i).get("SFJE") != null) {
					ld_hjje = Double.parseDouble(String.format("%1$.2f",
							yj01andmzxxzfList.get(i).get("SFJE")));
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (yj01andmzxxzfList.get(i).get("SFRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andmzxxzfList.get(i)
								.get("SFRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 2) {
					if (yj01andmzxxzfList.get(i).get("JZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andmzxxzfList.get(i)
								.get("JZRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 3) {
					if (yj01andmzxxzfList.get(i).get("HZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andmzxxzfList.get(i)
								.get("HZRQ") + "");
					} else {
						continue;
					}
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				if (listMS_MZHS.size() <= 0) {
					Map<String, Object> mapMS_MZHS = new HashMap<String, Object>();
					mapMS_MZHS.put("KSDM", li_ksdm);
					mapMS_MZHS.put("YSDM", ls_ysdm);
					mapMS_MZHS.put("GZRQ", ldt_sfrq);
					mapMS_MZHS.put("TJFS", ii_tjfs);
					mapMS_MZHS.put("HJJE", ld_hjje);
					mapMS_MZHS.put("JCD", li_jczs);
					mapMS_MZHS.put("XYF", 0);
					mapMS_MZHS.put("ZYF", 0);
					mapMS_MZHS.put("CYF", 0);
					mapMS_MZHS.put("MZRC", 0);
					listMS_MZHS.add(mapMS_MZHS);
				} else {
					for (int j = 0; j < listMS_MZHS.size(); j++) {
						if (Long.parseLong(listMS_MZHS.get(j).get("KSDM") + "") == li_ksdm
								&& listMS_MZHS.get(j).get("YSDM").toString()
										.equals(ls_ysdm)
								&& sdfdatetime.parse(
										sdfdatetime.format(listMS_MZHS.get(j)
												.get("GZRQ"))).getTime() == ldt_sfrq
										.getTime()
								&& Integer.parseInt(listMS_MZHS.get(j).get(
										"TJFS")
										+ "") == ii_tjfs) {
							listMS_MZHS.get(j).put(
									"JCD",
									Integer.parseInt(listMS_MZHS.get(j).get(
											"JCD")
											+ "")
											- li_jczs);
							listMS_MZHS.get(j).put(
									"HJJE",
									Double.parseDouble(String.format(
											"%1$.2f",
											Double.parseDouble(listMS_MZHS.get(
													j).get("HJJE")
													+ "")
													- ld_hjje)));
							sign = 1;
						}
					}
					if (sign == 0) {
						Map<String, Object> mapMS_MZHS = new HashMap<String, Object>();
						mapMS_MZHS.put("KSDM", li_ksdm);
						mapMS_MZHS.put("YSDM", ls_ysdm);
						mapMS_MZHS.put("GZRQ", ldt_sfrq);
						mapMS_MZHS.put("TJFS", ii_tjfs);
						mapMS_MZHS.put("HJJE", 0 - ld_hjje);
						mapMS_MZHS.put("JCD", 0 - li_jczs);
						mapMS_MZHS.put("XYF", 0);
						mapMS_MZHS.put("ZYF", 0);
						mapMS_MZHS.put("CYF", 0);
						mapMS_MZHS.put("MZRC", 0);
						listMS_MZHS.add(mapMS_MZHS);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return listMS_MZHS;
	}

	public static void wf_tj_je(List<Map<String, Object>> adw_dw,
			List<Map<String, Object>> as_data, BaseDAO dao, Context ctx,
			int ii_tjfs, int ai_zfpb) throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			for (int ll_row = 0; ll_row < as_data.size(); ll_row++) {
				int sign = 0;
				Long li_ksdm = 0L;
				if (as_data.get(ll_row).get("KSDM") != null) {
					li_ksdm = Long.parseLong(as_data.get(ll_row).get("KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (as_data.get(ll_row).get("YSDM") != null) {
					ls_ysdm = as_data.get(ll_row).get("YSDM") + "";
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (ai_zfpb == 1) {
						ldt_sfrq = sdfdatetime.parse(as_data.get(ll_row).get(
								"ZFRQ")
								+ "");// 作废日期
					} else {
						ldt_sfrq = sdfdatetime.parse(as_data.get(ll_row).get(
								"SFRQ")
								+ ""); // 收费日期
					}
				} else if (ii_tjfs == 2) {
					ldt_sfrq = sdfdatetime.parse(as_data.get(ll_row)
							.get("JZRQ") + ""); // 结帐日期
				} else if (ii_tjfs == 3) {
					ldt_sfrq = sdfdatetime.parse(as_data.get(ll_row)
							.get("HZRQ") + ""); // 汇总日期
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				Long li_sfxm = 0L;
				if (as_data.get(ll_row).get("SFXM") != null) {
					li_sfxm = Long.parseLong(as_data.get(ll_row).get("SFXM")
							+ "");
				}
				double ld_sfje = 0.00;
				if (as_data.get(ll_row).get("SFJE") != null) {
					ld_sfje = Double.parseDouble(String.format("%1$.2f",
							as_data.get(ll_row).get("SFJE")));
				}
				double ld_zfje = 0.00;
				if (as_data.get(ll_row).get("ZFJE") != null) {
					ld_sfje = Double.parseDouble(String.format("%1$.2f",
							as_data.get(ll_row).get("ZFJE")));
				}
				if (adw_dw.size() <= 0) {
					Map<String, Object> mapadw_dw = new HashMap<String, Object>();
					mapadw_dw.put("KSDM", li_ksdm);
					mapadw_dw.put("YSDM", ls_ysdm);
					mapadw_dw.put("GZRQ", ldt_sfrq);
					mapadw_dw.put("TJFS", ii_tjfs);
					mapadw_dw.put("SFXM", li_sfxm);
					mapadw_dw.put("SFJE", ld_sfje);
					mapadw_dw.put("ZFJE", ld_zfje);
					adw_dw.add(mapadw_dw);
				} else {
					for (int ll_sf_row = 0; ll_sf_row < adw_dw.size(); ll_sf_row++) {
						if (Long.parseLong(adw_dw.get(ll_sf_row).get("KSDM")
								+ "") == li_ksdm
								&& (adw_dw.get(ll_sf_row).get("YSDM") + "")
										.equals(ls_ysdm)
								&& sdfdatetime.parse(
										sdfdatetime.format(adw_dw
												.get(ll_sf_row).get("GZRQ")))
										.getTime() == ldt_sfrq.getTime()
								&& Long.parseLong(adw_dw.get(ll_sf_row).get(
										"SFXM")
										+ "") == li_sfxm
								&& Integer.parseInt(adw_dw.get(ll_sf_row).get(
										"TJFS")
										+ "") == ii_tjfs) {
							adw_dw.get(ll_sf_row).put(
									"SFJE",
									Double.parseDouble(String.format(
											"%1$.2f",
											Double.parseDouble(adw_dw.get(
													ll_sf_row).get("SFJE")
													+ "")
													+ ld_sfje)));
							adw_dw.get(ll_sf_row).put(
									"ZFJE",
									Double.parseDouble(String.format(
											"%1$.2f",
											Double.parseDouble(adw_dw.get(
													ll_sf_row).get("ZFJE")
													+ "")
													+ ld_zfje)));
							sign = 1;
						}
					}
					if (sign == 0) {
						Map<String, Object> mapadw_dw = new HashMap<String, Object>();
						mapadw_dw.put("KSDM", li_ksdm);
						mapadw_dw.put("YSDM", ls_ysdm);
						mapadw_dw.put("GZRQ", ldt_sfrq);
						mapadw_dw.put("TJFS", ii_tjfs);
						mapadw_dw.put("SFXM", li_sfxm);
						mapadw_dw.put("SFJE", ld_sfje);
						mapadw_dw.put("ZFJE", ld_zfje);
						adw_dw.add(mapadw_dw);
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static List<Map<String, Object>> wf_tj_mzmx(
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		int ii_tjfs = Integer.parseInt(parameters.get("ii_tjfs") + "");
		parameters.remove("ii_tjfs");
		List<Map<String, Object>> ms_mzmxlist = new ArrayList<Map<String, Object>>();
		try {
			String sql = "";
			String sql2 = "";
			String sql3 = "";
			String sql4 = "";
			if (ii_tjfs == 1) {
				sql = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF02.FYGB as SFXM,SUM(MS_CF02.HJJE) AS SFJE,SUM(MS_CF02.HJJE * MS_CF02.ZFBL) AS ZFJE FROM MS_CF01 MS_CF01,MS_CF02 MS_CF02,MS_MZXX MS_MZXX WHERE (MS_CF02.CFSB = MS_CF01.CFSB) and (MS_MZXX.MZXH = MS_CF01.MZXH) AND MS_MZXX.SFRQ >= :ldt_begin And MS_MZXX.SFRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF02.FYGB ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF02.FYGB";
				sql2 = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_CF02.FYGB as SFXM,0 - sum(MS_CF02.HJJE) AS SFJE,0 - SUM(MS_CF02.HJJE * MS_CF02.ZFBL) AS ZFJE FROM MS_CF01 MS_CF01,MS_CF02 MS_CF02,MS_ZFFP MS_ZFFP WHERE (MS_CF02.CFSB = MS_CF01.CFSB) and (MS_ZFFP.MZXH = MS_CF01.MZXH) AND MS_ZFFP.ZFRQ >= :ldt_begin And MS_ZFFP.ZFRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF02.FYGB ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF02.FYGB";
				sql3 = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_YJ02.FYGB AS SFXM,sum(MS_YJ02.HJJE) AS SFJE,SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) AND MS_MZXX.SFRQ >= :ldt_begin And MS_MZXX.SFRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB";
				sql4 = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_YJ02.FYGB as SFXM,0 - sum(MS_YJ02.HJJE) AS SFJE,0 - SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND MS_ZFFP.ZFRQ >= :ldt_begin And MS_ZFFP.ZFRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB";
			} else if (ii_tjfs == 2) {
				sql = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF02.FYGB as SFXM,SUM(MS_CF02.HJJE) AS SFJE,SUM(MS_CF02.HJJE * MS_CF02.ZFBL) AS ZFJE FROM MS_CF01 MS_CF01,MS_CF02 MS_CF02,MS_MZXX MS_MZXX WHERE (MS_CF02.CFSB = MS_CF01.CFSB) and (MS_MZXX.MZXH = MS_CF01.MZXH) AND MS_MZXX.JZRQ >= :ldt_begin And MS_MZXX.JZRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF02.FYGB ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF02.FYGB";
				sql2 = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_CF02.FYGB as SFXM,0 - sum(MS_CF02.HJJE) AS SFJE,0 - SUM(MS_CF02.HJJE * MS_CF02.ZFBL) AS ZFJE FROM MS_CF01 MS_CF01,MS_CF02 MS_CF02,MS_ZFFP MS_ZFFP WHERE (MS_CF02.CFSB = MS_CF01.CFSB) and (MS_ZFFP.MZXH = MS_CF01.MZXH) AND MS_ZFFP.JZRQ >= :ldt_begin And MS_ZFFP.JZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF02.FYGB ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF02.FYGB";
				sql3 = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_YJ02.FYGB AS SFXM,sum(MS_YJ02.HJJE) AS SFJE,SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) AND MS_MZXX.JZRQ >= :ldt_begin And MS_MZXX.JZRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB";
				sql4 = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_YJ02.FYGB as SFXM,0 - sum(MS_YJ02.HJJE) AS SFJE,0 - SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND MS_ZFFP.JZRQ >= :ldt_begin And MS_ZFFP.JZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB";
			} else if (ii_tjfs == 3) {
				sql = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_CF02.FYGB as SFXM,SUM(MS_CF02.HJJE) AS SFJE,SUM(MS_CF02.HJJE * MS_CF02.ZFBL) AS ZFJE FROM MS_CF01 MS_CF01,MS_CF02 MS_CF02,MS_MZXX MS_MZXX WHERE (MS_CF02.CFSB = MS_CF01.CFSB) and (MS_MZXX.MZXH = MS_CF01.MZXH) AND MS_MZXX.HZRQ >= :ldt_begin And MS_MZXX.HZRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF02.FYGB ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_CF02.FYGB";
				sql2 = "SELECT MS_CF01.KSDM as KSDM,MS_CF01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_CF02.FYGB as SFXM,0 - sum(MS_CF02.HJJE) AS SFJE,0 - SUM(MS_CF02.HJJE * MS_CF02.ZFBL) AS ZFJE FROM MS_CF01 MS_CF01,MS_CF02 MS_CF02,MS_ZFFP MS_ZFFP WHERE (MS_CF02.CFSB = MS_CF01.CFSB) and (MS_ZFFP.MZXH = MS_CF01.MZXH) AND MS_ZFFP.HZRQ >= :ldt_begin And MS_ZFFP.HZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF02.FYGB ORDER BY MS_CF01.KSDM,MS_CF01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_CF02.FYGB";
				sql3 = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_YJ02.FYGB AS SFXM,sum(MS_YJ02.HJJE) AS SFJE,SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) AND MS_MZXX.HZRQ >= :ldt_begin And MS_MZXX.HZRQ <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB";
				sql4 = "SELECT MS_YJ01.KSDM as KSDM,MS_YJ01.YSDM as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_YJ02.FYGB as SFXM,0 - sum(MS_YJ02.HJJE) AS SFJE,0 - SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND MS_ZFFP.HZRQ >= :ldt_begin And MS_ZFFP.HZRQ <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.KSDM,MS_YJ01.YSDM,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB";
			}

			// 开单金额临时表单
			List<Map<String, Object>> cf01cf02andmzxxList = dao.doQuery(sql,
					parameters);
			wf_tj_je(ms_mzmxlist, cf01cf02andmzxxList, dao, ctx, ii_tjfs, 0);

			// 开单作废金额临时表单
			List<Map<String, Object>> cf01cf02andzffplist = dao.doQuery(sql2,
					parameters);
			wf_tj_je(ms_mzmxlist, cf01cf02andzffplist, dao, ctx, ii_tjfs, 1);
			// 医技开单金额临时表单
			List<Map<String, Object>> yj01yj02andmzxxlist = dao.doQuery(sql3,
					parameters);
			wf_tj_je(ms_mzmxlist, yj01yj02andmzxxlist, dao, ctx, ii_tjfs, 0);
			// 医技开单作废金额临时表单
			List<Map<String, Object>> yj01yj02andzffplist = dao.doQuery(sql4,
					parameters);
			wf_tj_je(ms_mzmxlist, yj01yj02andzffplist, dao, ctx, ii_tjfs, 1);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return ms_mzmxlist;
	}

	/**
	 * 
	 * @param parameters里参数分别是ldt_begin
	 *            (开始时间)、ldt_end(结束时间)、gl_jgid(机构id)、ii_tjfs(提交方式)
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public static List<Map<String, Object>> wf_tj_yjhs(
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> listMS_YJHS = new ArrayList<Map<String, Object>>();
		try {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			int ii_tjfs = Integer.parseInt(parameters.get("ii_tjfs") + "");
			parameters.remove("ii_tjfs");
			String ms_mzxx = "";
			String ms_zffp = "";
			if (ii_tjfs == 1) {
				ms_mzxx = "MS_MZXX.SFRQ";
				ms_zffp = "MS_ZFFP.ZFRQ";
			} else if (ii_tjfs == 2) {
				ms_mzxx = "MS_MZXX.JZRQ";
				ms_zffp = "MS_ZFFP.JZRQ";
			} else if (ii_tjfs == 3) {
				ms_mzxx = "MS_MZXX.HZRQ";
				ms_zffp = "MS_ZFFP.HZRQ";
			}
			// 检查张数临时表单
			List<Map<String, Object>> mzxxandyj01List = dao
					.doQuery(
							"SELECT MS_YJ01.ZXKS as KSDM,MS_YJ01.ZXYS as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,count(MS_YJ01.YJXH) AS YJZS FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ01.ZXPB = 1) AND "
									+ ms_mzxx
									+ " >= :ldt_begin And "
									+ ms_mzxx
									+ " <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ ORDER BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ",
							parameters);
			for (int i = 0; i < mzxxandyj01List.size(); i++) {
				int sign = 0;
				Long li_ksdm = 0L;
				if (mzxxandyj01List.get(i).get("KSDM") != null) {
					li_ksdm = Long.parseLong(mzxxandyj01List.get(i).get("KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (mzxxandyj01List.get(i).get("YSDM") != null) {
					ls_ysdm = mzxxandyj01List.get(i).get("YSDM") + "";
				}

				int li_yjzs = 0;
				if (mzxxandyj01List.get(i).get("YJZS") != null) {
					li_yjzs = Integer.parseInt(mzxxandyj01List.get(i).get(
							"YJZS")
							+ "");
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (mzxxandyj01List.get(i).get("SFRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(mzxxandyj01List.get(i)
								.get("SFRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 2) {
					if (mzxxandyj01List.get(i).get("JZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(mzxxandyj01List.get(i)
								.get("JZRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 3) {
					if (mzxxandyj01List.get(i).get("HZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(mzxxandyj01List.get(i)
								.get("HZRQ") + "");
					} else {
						continue;
					}
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				if (listMS_YJHS.size() <= 0) {
					Map<String, Object> mapMS_YJHS = new HashMap<String, Object>();
					mapMS_YJHS.put("KSDM", li_ksdm);
					mapMS_YJHS.put("YSDM", ls_ysdm);
					mapMS_YJHS.put("GZRQ", ldt_sfrq);
					mapMS_YJHS.put("TJFS", ii_tjfs);
					mapMS_YJHS.put("JCD", li_yjzs);
					mapMS_YJHS.put("XYF", 0);
					mapMS_YJHS.put("ZYF", 0);
					mapMS_YJHS.put("CYF", 0);
					mapMS_YJHS.put("HJJE", 0);
					mapMS_YJHS.put("MZRC", 0);
					listMS_YJHS.add(mapMS_YJHS);
				} else {
					for (int j = 0; j < listMS_YJHS.size(); j++) {
						if (Long.parseLong(listMS_YJHS.get(j).get("KSDM") + "") == li_ksdm
								&& listMS_YJHS.get(j).get("YSDM").toString()
										.equals(ls_ysdm)
								&& sdfdatetime.parse(
										sdfdatetime.format(listMS_YJHS.get(j)
												.get("GZRQ"))).getTime() == ldt_sfrq
										.getTime()
								&& Integer.parseInt(listMS_YJHS.get(j).get(
										"TJFS")
										+ "") == ii_tjfs) {

							listMS_YJHS.get(j).put(
									"JCD",
									Integer.parseInt(listMS_YJHS.get(j).get(
											"JCD")
											+ "")
											+ li_yjzs);
							sign = 1;
						}

					}
					if (sign == 0) {
						Map<String, Object> mapMS_YJHS = new HashMap<String, Object>();
						mapMS_YJHS.put("KSDM", li_ksdm);
						mapMS_YJHS.put("YSDM", ls_ysdm);
						mapMS_YJHS.put("GZRQ", ldt_sfrq);
						mapMS_YJHS.put("TJFS", ii_tjfs);
						mapMS_YJHS.put("JCD", li_yjzs);
						mapMS_YJHS.put("XYF", 0);
						mapMS_YJHS.put("ZYF", 0);
						mapMS_YJHS.put("CYF", 0);
						mapMS_YJHS.put("HJJE", 0);
						mapMS_YJHS.put("MZRC", 0);
						listMS_YJHS.add(mapMS_YJHS);
					}
				}
			}
			// 检查作废张数临时表单
			List<Map<String, Object>> yj01andzffpList = dao
					.doQuery(
							"SELECT MS_YJ01.ZXKS as KSDM,MS_YJ01.ZXYS as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,count(MS_YJ01.YJXH) AS YJZS FROM MS_YJ01 MS_YJ01,MS_ZFFP MS_ZFFP WHERE (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND (MS_YJ01.ZXPB = 1) AND "
									+ ms_zffp
									+ " >= :ldt_begin And "
									+ ms_zffp
									+ " <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ ORDER BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ",
							parameters);
			for (int i = 0; i < yj01andzffpList.size(); i++) {
				int signzf = 0;
				Long li_ksdm = 0L;
				if (yj01andzffpList.get(i).get("KSDM") != null) {
					li_ksdm = Long.parseLong(yj01andzffpList.get(i).get("KSDM")
							+ "");
				}
				String ls_ysdm = null;
				if (yj01andzffpList.get(i).get("YSDM") != null) {
					ls_ysdm = yj01andzffpList.get(i).get("YSDM") + "";
				}
				int li_yjzs = 0;
				if (yj01andzffpList.get(i).get("YJZS") != null) {
					li_yjzs = Integer.parseInt(yj01andzffpList.get(i).get(
							"YJZS")
							+ "");
				}
				Date ldt_sfrq = null;
				if (ii_tjfs == 1) {
					if (yj01andzffpList.get(i).get("SFRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andzffpList.get(i)
								.get("SFRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 2) {
					if (yj01andzffpList.get(i).get("JZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andzffpList.get(i)
								.get("JZRQ") + "");
					} else {
						continue;
					}
				} else if (ii_tjfs == 3) {
					if (yj01andzffpList.get(i).get("HZRQ") != null) {
						ldt_sfrq = sdfdatetime.parse(yj01andzffpList.get(i)
								.get("HZRQ") + "");
					} else {
						continue;
					}
				}
				ldt_sfrq = sdfdatetime.parse(sdfdate.format(ldt_sfrq)
						+ " 00:00:00");
				if (listMS_YJHS.size() <= 0) {
					Map<String, Object> mapzfMS_YJHS = new HashMap<String, Object>();
					mapzfMS_YJHS.put("KSDM", li_ksdm);
					mapzfMS_YJHS.put("YSDM", ls_ysdm);
					mapzfMS_YJHS.put("GZRQ", ldt_sfrq);
					mapzfMS_YJHS.put("TJFS", ii_tjfs);
					mapzfMS_YJHS.put("JCD", -li_yjzs);
					mapzfMS_YJHS.put("XYF", 0);
					mapzfMS_YJHS.put("ZYF", 0);
					mapzfMS_YJHS.put("CYF", 0);
					mapzfMS_YJHS.put("HJJE", 0);
					mapzfMS_YJHS.put("MZRC", 0);
					listMS_YJHS.add(mapzfMS_YJHS);
				} else {
					for (int j = 0; j < listMS_YJHS.size(); j++) {
						if (Long.parseLong(listMS_YJHS.get(j).get("KSDM") + "") == li_ksdm
								&& listMS_YJHS.get(j).get("YSDM").toString()
										.equals(ls_ysdm)
								&& sdfdatetime.parse(
										sdfdatetime.format(listMS_YJHS.get(j)
												.get("GZRQ"))).getTime() == ldt_sfrq
										.getTime()
								&& Integer.parseInt(listMS_YJHS.get(j).get(
										"TJFS")
										+ "") == ii_tjfs) {
							int li_zs = 0;
							li_zs = Integer.parseInt(listMS_YJHS.get(j).get(
									"HJJE")
									+ "");
							listMS_YJHS.get(j).put("JCD", li_zs - li_yjzs);
							signzf = 1;
						}
					}
					if (signzf == 0) {
						Map<String, Object> mapzfMS_YJHS = new HashMap<String, Object>();
						mapzfMS_YJHS.put("KSDM", li_ksdm);
						mapzfMS_YJHS.put("YSDM", ls_ysdm);
						mapzfMS_YJHS.put("GZRQ", ldt_sfrq);
						mapzfMS_YJHS.put("TJFS", ii_tjfs);
						mapzfMS_YJHS.put("JCD", -li_yjzs);
						mapzfMS_YJHS.put("XYF", 0);
						mapzfMS_YJHS.put("ZYF", 0);
						mapzfMS_YJHS.put("CYF", 0);
						mapzfMS_YJHS.put("HJJE", 0);
						mapzfMS_YJHS.put("MZRC", 0);
						listMS_YJHS.add(mapzfMS_YJHS);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return listMS_YJHS;
	}

	public static List<Map<String, Object>> wf_tj_yjmx(
			Map<String, Object> parameters, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		int ii_tjfs = Integer.parseInt(parameters.get("ii_tjfs") + "");
		parameters.remove("ii_tjfs");
		List<Map<String, Object>> ms_yjmxlist = new ArrayList<Map<String, Object>>();
		try {
			String ms_mzxx = "";
			String ms_zffp = "";
			if (ii_tjfs == 1) {
				ms_mzxx = "MS_MZXX.SFRQ";
				ms_zffp = "MS_ZFFP.ZFRQ";
			} else if (ii_tjfs == 2) {
				ms_mzxx = "MS_MZXX.JZRQ";
				ms_zffp = "MS_ZFFP.JZRQ";
			} else if (ii_tjfs == 3) {
				ms_mzxx = "MS_MZXX.HZRQ";
				ms_zffp = "MS_ZFFP.HZRQ";
			}
			// 执行金额临时表单
			List<Map<String, Object>> yj01yj02andmzxxList = dao
					.doQuery(
							"SELECT MS_YJ01.ZXKS as KSDM,MS_YJ01.ZXYS as YSDM,str(MS_MZXX.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_MZXX.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_MZXX.SFRQ,'YYYY-MM-DD HH24:MI:SS') as SFRQ,MS_YJ02.FYGB as SFXM,sum(MS_YJ02.HJJE) AS SFJE,SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_MZXX MS_MZXX,MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02 WHERE (MS_YJ01.MZXH = MS_MZXX.MZXH) and (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_YJ01.ZXPB = 1) AND "
									+ ms_mzxx
									+ " >= :ldt_begin And "
									+ ms_mzxx
									+ " <= :ldt_end and MS_MZXX.JGID = :gl_jgid GROUP BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_MZXX.HZRQ,MS_MZXX.JZRQ,MS_MZXX.SFRQ,MS_YJ02.FYGB",
							parameters);
			wf_tj_je(ms_yjmxlist, yj01yj02andmzxxList, dao, ctx, ii_tjfs, 0);
			// 执行作废金额临时表单
			List<Map<String, Object>> yj01yj02andzffplist = dao
					.doQuery(
							"SELECT MS_YJ01.ZXKS as KSDM,MS_YJ01.ZXYS as YSDM,str(MS_ZFFP.HZRQ,'YYYY-MM-DD HH24:MI:SS') as HZRQ,str(MS_ZFFP.JZRQ,'YYYY-MM-DD HH24:MI:SS') as JZRQ,str(MS_ZFFP.ZFRQ,'YYYY-MM-DD HH24:MI:SS') as ZFRQ,MS_YJ02.FYGB as SFXM,0-sum(MS_YJ02.HJJE) AS SFJE,0 - SUM(MS_YJ02.HJJE * MS_YJ02.ZFBL) AS ZFJE FROM MS_YJ01 MS_YJ01,MS_YJ02 MS_YJ02,MS_ZFFP MS_ZFFP WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) and (MS_ZFFP.MZXH = MS_YJ01.MZXH) AND (MS_YJ01.ZXPB = 1) AND "
									+ ms_zffp
									+ " >= :ldt_begin And "
									+ ms_zffp
									+ " <= :ldt_end and MS_ZFFP.JGID = :gl_jgid GROUP BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB ORDER BY MS_YJ01.ZXKS,MS_YJ01.ZXYS,MS_ZFFP.HZRQ,MS_ZFFP.JZRQ,MS_ZFFP.ZFRQ,MS_YJ02.FYGB",
							parameters);
			wf_tj_je(ms_yjmxlist, yj01yj02andzffplist, dao, ctx, ii_tjfs, 1);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return ms_yjmxlist;
	}

	/*
	 * // 物质--库存信息主方法 public static void u_wl_public_kczc(List<Map<String,
	 * Object>> ads_ywmx,Long al_lzfs, BaseDAO dao, Context ctx) { boolean b =
	 * Uf_access(ads_ywmx,al_lzfs); }
	 */

	// 库存信息 子方法
	public static boolean Uf_access(List<Map<String, Object>> ids_ywmx,
			Long al_lzfs, BaseDAO dao, Context ctx) {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		// 返回的
		boolean back = true;
		// 判断流转方式
		List<Map<String, Object>> ist_lzfs = new ArrayList<Map<String, Object>>();
		ist_lzfs = uf_get_lzfs(al_lzfs, dao, ctx);
		if (ist_lzfs.size() == 0) {
			back = false;
			throw new RuntimeException("流转方式不能为空");
		}

		int YWLB = Integer.parseInt(ist_lzfs.get(0).get("YWLB") + "");
		String DJLX = ist_lzfs.get(0).get("DJLX") + "";
		int JZBZ = Integer.parseInt(ist_lzfs.get(0).get("JZBZ") + "");
		int ZHZT = -1;
		if (ist_lzfs.get(0).get("ZHZT") != null) {
			ZHZT = Integer.parseInt(ist_lzfs.get(0).get("ZHZT") + "");
		}
		for (int i = 0; i < ids_ywmx.size(); i++) {
			ids_ywmx.get(i).put("YWLB", YWLB);
			ids_ywmx.get(i).put("DJLX", DJLX);
			ids_ywmx.get(i).put("LZFS", al_lzfs);
			ids_ywmx.get(i).put("FSRQ", new Date());
			ids_ywmx.get(i).put("JGID", JGID);
		}
		if (YWLB == 1) { // 增
			boolean rk = uf_rk(ids_ywmx, JZBZ, dao, ctx);
			if (!rk) {
				back = false;
			}
		} else if (YWLB == -1) { // 减
			boolean ck = uf_ck(ids_ywmx, JZBZ, dao, ctx);
			if (!ck) {
				back = false;
			}
		} else {// 其他类 (暂时只有转科)
			if (DJLX.equals("ZK")) {
				Uf_zk(ids_ywmx, JZBZ, dao, ctx);
			} else if (DJLX.equals("BS")) {
				uf_bs(ids_ywmx, ZHZT, dao, ctx);
			} else if (DJLX.equals("CZ")) {
				uf_cz(ids_ywmx, dao, ctx);
			}
			{

			}
		}
		return back;
	}

	// 流转方式 子方法
	public static List<Map<String, Object>> uf_get_lzfs(Long al_lzfs,
			BaseDAO dao, Context ctx) {
		// 判断当前流转方式 在 流转方式表中是否存在。
		if (al_lzfs == 0) {
			throw new RuntimeException("方式序号不能为空！");
		}
		Map<String, Object> LZFS_Per = new HashMap<String, Object>();
		LZFS_Per.put("FSXH", al_lzfs);

		List<Map<String, Object>> ist_lzfs = new ArrayList<Map<String, Object>>();
		try {
			StringBuffer hql = new StringBuffer(
					"select FSXH as FSXH,JGID as JGID,KFXH as KFXH,YWLB as YWLB,DJLX as DJLX,SYKF as SYKF,FSMC as FSMC,BLSX as BLSX,JZBZ as JZBZ,ZHZT as ZHZT,TSBZ as TSBZ,FKBZ as FKBZ,FSPX as FSPX,DJQZ as DJQZ,DJNF as DJNF,DJYF as DJYF,XHCD as XHCD,FSZT as FSZT,DYHS as DYHS FROM WL_LZFS where FSXH =:FSXH");
			ist_lzfs = dao.doQuery(hql.toString(), LZFS_Per);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return ist_lzfs;
	}

	// 入库库存增加
	public static boolean uf_rk(List<Map<String, Object>> ids_ywmx,
			Integer JZBZ, BaseDAO dao, Context ctx) {
		// User user = (User) ctx.get("user.instance");
		// String KFXH = user.getProperty("treasuryId");
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		boolean back = true;
		String msg = "记账失败！";
		try {
			for (int i = 0; i < ids_ywmx.size(); i++) {
				Map<String, Object> ywmxMap = ids_ywmx.get(i);
				long ZBXH = 0;
				if (ywmxMap.get("ZBXH") != null) {
					ZBXH = Long.parseLong(ywmxMap.get("ZBXH") + "");
				}
				long KCXH = Long.parseLong(ywmxMap.get("KCXH") + "");
				long KSDM = 0;
				if (ywmxMap.get("KSDM") != null) {
					KSDM = Long.parseLong(ywmxMap.get("KSDM") + "");
				}
				double WZSL = Double.parseDouble(ywmxMap.get("WZSL") + "");
				double WZJG = Double.parseDouble(ywmxMap.get("WZJG") + "");
				double WZJE = Double.parseDouble(ywmxMap.get("WZJE") + "");
				long CJXH = Long.parseLong(ywmxMap.get("CJXH") + "");
				long WZXH = Long.parseLong(ywmxMap.get("WZXH") + "");

				ywmxMap.put("KFXH", Integer.parseInt(KFXH));

				// 判断在物质表中是否存在
				int num = Of_check_wzcj(WZXH, CJXH, dao);
				if (num < 1) {
					break;
				}
				if (WZSL == 0) {
					break;
				}
				// 第四步
				List<Map<String, Object>> count_list = get_wzkc("KCXH", KCXH,
						WZXH, dao, ctx);
				if (count_list.size() > 0) {
					Map<String, Object> record = count_list.get(0);
					double WZSL1 =  MedicineUtils.parseDouble(record.get("WZSL"))  + WZSL;
					double WZJE1 = MedicineUtils.parseDouble(record.get("WZJE"))  + WZJE;
					record.put("WZSL", WZSL1);
					record.put("WZJE", WZJE1);
					dao.doSave("update", BSPHISEntryNames.WL_WZKC, record,
							false);
				} else {
					if (ywmxMap.get("KFXH") == null) {
						ywmxMap.put("KFXH", KFXH);
					}
					ywmxMap.put("YKSL", 0);
					dao.doSave("create", BSPHISEntryNames.WL_WZKC, ywmxMap,
							false);
				}
				/**
				 * 第五步 判断管理方式 和 记账类别 如果（JZBZ == i && GLFS >1）判断 科室帐、资产帐
				 */
				int GLFS = Integer.parseInt(ywmxMap.get("GLFS") + "");
				// 首先判断科室账(根据 KSDM 和 KCXH 判断 wl_kszc 表中是否存在记录)
				Map<String, Object> KSZC_Per = new HashMap<String, Object>();
				KSZC_Per.put("KSDM", KSDM);
				StringBuffer hql = new StringBuffer(
						"select JLXH as JLXH,JGID as JGID,KSDM as KSDM,KFXH as KFXH,ZBLB as ZBLB,KCXH as KCXH,WZXH as WZXH,CJXH as CJXH,SCRQ as SCRQ,SXRQ as SXRQ,WZPH as WZPH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,ZRRQ as ZRRQ,YKSL as YKSL FROM WL_KSZC where KSDM =:KSDM ");
				List<Map<String, Object>> KSZC_list = dao.doQuery(
						hql.toString(), KSZC_Per);
				Map<String, Object> kszcMap = new HashMap<String, Object>();
				if (JZBZ == 1 && GLFS > 1) {
					if (KSZC_list.size() > 0) {
						kszcMap = KSZC_list.get(0);
						Map<String, Object> record = new HashMap<String, Object>();
						double WZSL_KSZC = Double.parseDouble(kszcMap
								.get("WZSL") + "")
								- WZSL;
						double WZJE_KSZC = Double.parseDouble(kszcMap
								.get("WZJE") + "")
								- WZJE;
						record.put("WZSL", WZSL_KSZC);
						record.put("WZJE", WZJE_KSZC);
						record.put("JLXH", kszcMap.get("JLXH"));
						dao.doSave("update", BSPHISEntryNames.WL_KSZC, record,
								false);
					} else {
						back = false;
						msg = "库存不足！";
						throw new RuntimeException("库存不足！");
					}
					if (GLFS == 3) {
						Map<String, Object> ZCZB_Per = new HashMap<String, Object>();
						ZCZB_Per.put("ZBXH", ZBXH);
						StringBuffer ZCZB_hql = new StringBuffer(
								"select ZBXH as ZBXH from WL_ZCZB where ZBXH =:ZBXH ");
						List<Map<String, Object>> ZCZB_list = dao.doQuery(
								ZCZB_hql.toString(), ZCZB_Per);
						if (ZCZB_list.size() > 0) {
							ZCZB_Per.put("WZZT", 0);
							ZCZB_Per.put("ZYKS", 0);
							ZCZB_Per.put("ZRRQ", "");
							ZCZB_Per.put("QYRQ", "");
							dao.doSave("update", BSPHISEntryNames.WL_ZCZB,
									ZCZB_Per, false);
						} else {
							back = false;
							msg = "资产帐不足！";
							throw new RuntimeException("资产帐不足！");
						}
					}
				}
				// 6) 如果管理方式小于等于1或者ist_lzfs.jzbz <>1
				if (GLFS == 3) {
					for (int j = 0; j < WZSL; j++) {
						ywmxMap.put("CLBZ", 0);
						ywmxMap.put("WZZT", 0);
						ywmxMap.put("ZCYZ", WZJG);
						ywmxMap.put("CZYZ", WZJG);
						ywmxMap.put("YWRQ", BSHISUtil.getDate());
						ywmxMap.put("TZRQ", BSHISUtil.getDate());
						// 得到物资编号（WZBH）
						Map<String, Object> parametersforWZBH = new HashMap<String, Object>();
						parametersforWZBH.put("WZXH", WZXH);
						parametersforWZBH.put("ZBLB", ywmxMap.get("ZBLB"));
						parametersforWZBH.put("TZRQ", ywmxMap.get("YWRQ"));
						if (ywmxMap.get("YWRQ") != null) {
							String WZBH = setWZBH(parametersforWZBH, dao, ctx);
							ywmxMap.put("WZBH", WZBH);
						}
						dao.doSave("create", BSPHISEntryNames.WL_ZCZB, ywmxMap,
								false);
					}
				}
				// 第六步 判断库存
				Of_check_kc(count_list, kszcMap, dao);
				Of_Zcmx(ywmxMap, dao);

				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
		return back;
	}

	// 物质厂家
	public static int Of_check_wzcj(Long WZXH, Long CJXH, BaseDAO dao) {
		if (WZXH == 0 || CJXH == 0) {
			throw new RuntimeException("物质序号或厂家序号不能为空！");
		}
		Map<String, Object> WZCJ_Per = new HashMap<String, Object>();
		WZCJ_Per.put("WZXH", WZXH);
		WZCJ_Per.put("CJXH", CJXH);
		int num = -1;
		try {
			long ll_count = dao.doCount("WL_WZCJ",
					"WZXH =:WZXH and CJXH =:CJXH", WZCJ_Per);
			if (ll_count > 0) {
				num = 1;
			} else {
				num = 0;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new RuntimeException("物质序号或厂家序号不能为空！");
		}
		return num;
	}

	// 物质库存
	public static List<Map<String, Object>> get_wzkc(String XH, Long kcxh,
			Long WZ_KCXH, BaseDAO dao, Context ctx) {

		// User user = (User) ctx.get("user.instance");
		// String KFXH = user.getProperty("treasuryId");
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (kcxh == 0) {
			throw new RuntimeException("库存序号不能为空！");
		}
		String ss = "";
		Map<String, Object> LZFS_Per = new HashMap<String, Object>();
		if (XH.equals("KCXH")) {
			LZFS_Per.put("KCXH", kcxh);
			LZFS_Per.put("WZXH", WZ_KCXH);
			if (Integer.parseInt(KFXH) != 0) {
				LZFS_Per.put("KFXH", Integer.parseInt(KFXH));
				ss = " where WZXH =:WZXH and KCXH =:KCXH and KFXH=:KFXH";
			} else {
				ss = " where WZXH =:WZXH and KCXH =:KCXH ";
			}
		}
		if (XH.equals("WZXH")) {
			LZFS_Per.put("WZXH", kcxh);
			LZFS_Per.put("KCXH", WZ_KCXH);
			if (Integer.parseInt(KFXH) != 0) {
				LZFS_Per.put("KFXH", Integer.parseInt(KFXH));
				ss = " where WZXH =:WZXH and KCXH=:KCXH and KFXH=:KFXH";
			} else {
				ss = " where WZXH =:WZXH and KCXH=:KCXH ";
			}

		}
		try {
			StringBuffer hql = new StringBuffer(
					"SELECT JLXH as JLXH,JGID as JGID,KCXH as KCXH,KFXH as KFXH,ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,WZPH as WZPH,MJPH as MJPH,SCRQ as SCRQ,SXRQ as SXRQ,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,LSJG as LSJG ,LSJE as LSJE,YKSL as YKSL,FSRQ as FSRQ FROM WL_WZKC ");
			hql.append(ss);
			list = dao.doQuery(hql.toString(), LZFS_Per);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new RuntimeException("库存序号不能为空！");
		}
		return list;
	}

	/**
	 * 检测库存表数据，对于库存为0的，移入历史库存当，科室账册里0的直接删除
	 */
	public static void Of_check_kc(List<Map<String, Object>> wzkcList,
			Map<String, Object> kszcMap, BaseDAO dao) {
		try {
			for (int i = 0; i < wzkcList.size(); i++) {
				double WZSL_YWMX = Double.parseDouble(wzkcList.get(i).get(
						"WZSL")
						+ "");
				if (WZSL_YWMX < 0) {
					throw new RuntimeException("库存不足！");
				}
				if (WZSL_YWMX == 0) {
					long JLXH = Long
							.parseLong(wzkcList.get(i).get("JLXH") + "");
					dao.doSave("create", BSPHISEntryNames.WL_LSKC,
							wzkcList.get(i), false);
					dao.removeByFieldValue("JLXH", JLXH,
							BSPHISEntryNames.WL_WZKC);
				}
			}
			if (kszcMap.size() != 0) {
				double WZSL_KSZC = Double.parseDouble(kszcMap.get("WZSL") + "");
				long JLXH_KSZC = Long.parseLong(kszcMap.get("JLXH") + "");
				if (WZSL_KSZC < 0) {
					throw new RuntimeException("库存不足！");
				}
				if (WZSL_KSZC == 0) {
					dao.removeByFieldValue("JLXH", JLXH_KSZC,
							BSPHISEntryNames.WL_KSZC);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("库存检查失败！");
		}
	}

	// 插入资产明细表中
	public static void Of_Zcmx(Map<String, Object> ywmxMap, BaseDAO dao) {
		try {
			ywmxMap.put("MXJL", ywmxMap.get("JLXH"));
			dao.doSave("create", BSPHISEntryNames.WL_ZCMX, ywmxMap, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("插入资产明细表失败！");
		}

	}

	// 出库
	public static boolean uf_ck(List<Map<String, Object>> ids_ywmx,
			Integer JZBZ, BaseDAO dao, Context ctx) {
		boolean back = true;
		String msg = "记账失败！";
		try {
			for (int i = 0; i < ids_ywmx.size(); i++) {
				Map<String, Object> ywmxMap = ids_ywmx.get(i);
				long ZBXH = 0;
				if (ywmxMap.get("ZBXH") != null) {
					ZBXH = Long.parseLong(ywmxMap.get("ZBXH") + "");
				}
				long KCXH = Long.parseLong(ywmxMap.get("KCXH") + "");
				long KSDM = 0;
				if (ywmxMap.get("KSDM") != null) {
					KSDM = Long.parseLong(ywmxMap.get("KSDM") + "");
				}
				double WZSL = Double.parseDouble(ywmxMap.get("WZSL") + "");
				double WZJE = Double.parseDouble(ywmxMap.get("WZJE") + "");
				double LSJE = 0D;
				if (ywmxMap.get("LSJE") != null && ywmxMap.get("LSJE") != "") {
					LSJE = Double.parseDouble(ywmxMap.get("LSJE") + "");
				}
				long CJXH = Long.parseLong(ywmxMap.get("CJXH") + "");
				long WZXH = Long.parseLong(ywmxMap.get("WZXH") + "");
				// 判断在物质表中是否存在
				int num = Of_check_wzcj(WZXH, CJXH, dao);
				if (num < 1) {
					break;
				}
				if (WZSL == 0) {
					break;
				}
				// 第四步
				int GLFS = Integer.parseInt(ywmxMap.get("GLFS") + "");

				List<Map<String, Object>> wzkc_list = get_wzkc("WZXH", WZXH,
						KCXH, dao, ctx);

				for (int j = 0; j < wzkc_list.size(); j++) {
					if (Long.parseLong(wzkc_list.get(j).get("KCXH") + "") == KCXH) {
						Map<String, Object> record = wzkc_list.get(j);
						double WZSL1 = MedicineUtils.parseDouble(record.get("WZSL")) - WZSL;
						double WZJE1 = MedicineUtils.parseDouble(record.get("WZJE"))  - WZJE;
						// TOdo
						double LSJE1 = MedicineUtils.parseDouble(record.get("LSJE"))  - LSJE;
						double YKSL = MedicineUtils.parseDouble( record.get("YKSL")) - WZSL;
						record.put("WZSL", WZSL1);
						record.put("WZJE", WZJE1);
						record.put("LSJE", LSJE1);
						wzkc_list.get(j).put("WZSL", WZSL1);
						wzkc_list.get(j).put("WZJE", WZJE1);
						wzkc_list.get(j).put("LSJE", LSJE1);
						// if (GLFS != 3) {
						if (YKSL > 0) {
							record.put("YKSL", YKSL);
						} else {
							record.put("YKSL", 0);
						}
						// }
						dao.doSave("update", BSPHISEntryNames.WL_WZKC, record,
								false);
					}
				}
				// 首先判断科室账(根据 KSDM 和 KCXH 判断 wl_kszc 表中是否存在记录)
				Map<String, Object> kszcMap = new HashMap<String, Object>();
				if (KSDM != 0) {
					Map<String, Object> KSZC_Per = new HashMap<String, Object>();
					KSZC_Per.put("KSDM", KSDM);
					KSZC_Per.put("KCXH", KCXH);
					StringBuffer hql = new StringBuffer(
							"select JLXH as JLXH,JGID as JGID,KSDM as KSDM,KFXH as KFXH,ZBLB as ZBLB,KCXH as KCXH,WZXH as WZXH,CJXH as CJXH,SCRQ as SCRQ,SXRQ as SXRQ,WZPH as WZPH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,LSJG as LSJG,LSJE as LSJE,ZRRQ as ZRRQ,YKSL as YKSL FROM WL_KSZC where KSDM =:KSDM and KCXH=:KCXH ");
					List<Map<String, Object>> KSZC_list = dao.doQuery(
							hql.toString(), KSZC_Per);
					if (JZBZ == 1 && GLFS > 1) {
						if (KSZC_list.size() > 0) {
							kszcMap = KSZC_list.get(0);
							Map<String, Object> record = new HashMap<String, Object>();
							double WZSL_KSZC = Double.parseDouble(kszcMap
									.get("WZSL") + "")
									+ WZSL;
							double WZJE_KSZC = Double.parseDouble(kszcMap
									.get("WZJE") + "")
									+ WZJE;
							double LSJE_KSZC = 0.00;
							if (kszcMap.get("LSJE") != null
									&& kszcMap.get("LSJE") != "") {
								LSJE_KSZC = Double.parseDouble(kszcMap
										.get("LSJE") + "")
										+ LSJE;
							}
							record.put("WZSL", WZSL_KSZC);
							record.put("WZJE", WZJE_KSZC);
							record.put("LSJE", LSJE_KSZC);
							record.put("JLXH", kszcMap.get("JLXH"));
							dao.doSave("update", BSPHISEntryNames.WL_KSZC,
									record, false);
						} else {
							ywmxMap.put("YKSL", 0);
							ywmxMap.put("ZRRQ", new Date());
							dao.doSave("create", BSPHISEntryNames.WL_KSZC,
									ywmxMap, false);
						}
					}
				}
				if (GLFS == 3) {
					Map<String, Object> ZCZB_Per = new HashMap<String, Object>();
					ZCZB_Per.put("ZBXH", ZBXH);
					StringBuffer ZCZB_hql = new StringBuffer(
							"select ZBXH as ZBXH from WL_ZCZB where ZBXH =:ZBXH ");
					List<Map<String, Object>> ZCZB_list = dao.doQuery(
							ZCZB_hql.toString(), ZCZB_Per);
					if (ZCZB_list.size() > 0) {
						if (ywmxMap.get("GLXH") != null) {
							if (ywmxMap.get("GLXH") + "" != null
									&& ywmxMap.get("GLXH") + "" != "") {
								int nul = Integer.parseInt(ywmxMap.get("GLXH")
										+ "");
								if (nul > 0 || nul == -1) {
									ZCZB_Per.put("WZZT", -3);
									ZCZB_Per.put("QYRQ", "");
								} else {
									ZCZB_Per.put("WZZT", 1);
									ZCZB_Per.put("QYRQ", new Date());
								}
							} else {
								ZCZB_Per.put("WZZT", 1);
								ZCZB_Per.put("QYRQ", new Date());
							}
						} else {
							ZCZB_Per.put("WZZT", 1);
							ZCZB_Per.put("QYRQ", new Date());
						}
						ZCZB_Per.put("CLBZ", 0);
						ZCZB_Per.put("ZYKS", KSDM);
						ZCZB_Per.put("ZRRQ", "");
						dao.doSave("update", BSPHISEntryNames.WL_ZCZB,
								ZCZB_Per, false);
					} else {
						back = false;
						msg = "资产帐不足！";
						throw new RuntimeException("资产帐不足！");
					}
				}
				// 第六步 判断库存
				Of_check_kc(wzkc_list, kszcMap, dao);
				Of_Zcmx(ywmxMap, dao);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
		return back;
	}

	// 转科。
	public static boolean Uf_zk(List<Map<String, Object>> ids_ywmx,
			Integer JZBZ, BaseDAO dao, Context ctx) {
		String msg = "转科失败！";
		try {
			for (int i = 0; i < ids_ywmx.size(); i++) {
				Map<String, Object> ywmxMap = ids_ywmx.get(i);
				long ZBXH = 0;
				if (ywmxMap.get("ZBXH") != null) {
					ZBXH = Long.parseLong(ywmxMap.get("ZBXH") + "");
				}
				long KCXH = Long.parseLong(ywmxMap.get("KCXH") + "");
				double WZSL = Double.parseDouble(ywmxMap.get("WZSL") + "");
				double WZJE = Double.parseDouble(ywmxMap.get("WZJE") + "");
				long CJXH = Long.parseLong(ywmxMap.get("CJXH") + "");
				long WZXH = Long.parseLong(ywmxMap.get("WZXH") + "");
				int GLFS = Integer.parseInt(ywmxMap.get("GLFS") + "");
				// 转科
				long ZRKS = 0;
				if (ywmxMap.get("ZRKS") != null) {
					ZRKS = Long.parseLong(ywmxMap.get("ZRKS") + "");
					ywmxMap.put("KSDM", ZRKS);
				}
				long ZCKS = 0;
				if (ywmxMap.get("ZCKS") != null) {
					ZCKS = Long.parseLong(ywmxMap.get("ZCKS") + "");
				}

				// 判断在物质表中是否存在
				int num = Of_check_wzcj(WZXH, CJXH, dao);
				if (num < 1) {
					break;
				}
				if (WZSL == 0) {
					break;
				}
				// 第四步
				/**
				 * 首先判断转 出科室和库存 序号在物资库存表中是否存在。如果存在直接在减去对应的出库数量。
				 */
				List<Map<String, Object>> kszc_list = get_kszc(KCXH, ZCKS, dao);
				if (kszc_list.size() < 1) {
					msg = "科室账不足！";
					throw new RuntimeException("科室账不足！");
				}
				for (int j = 0; j < kszc_list.size(); j++) {
					if (Long.parseLong(kszc_list.get(j).get("KCXH") + "") == KCXH) {
						Map<String, Object> record = kszc_list.get(j);
						double WZSL1 = MedicineUtils.parseDouble(record.get("WZSL")) - WZSL;
						double WZJE1 =MedicineUtils.parseDouble(record.get("WZJE")) - WZJE;
						// glfs = 3 判断 wl_zczb 的CLBZ = 0;
						// 不等 3 YKSL 减去物资数量
						if (GLFS == 3) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("ZBXH", ZBXH);
							map.put("CLBZ", 0);
							dao.doSave("update", BSPHISEntryNames.WL_ZCZB, map,
									false);
						} else {
							double YKSL1 = MedicineUtils.parseDouble(record.get("YKSL"))  - WZSL;
							record.put("YKSL", YKSL1);
							kszc_list.get(j).put("WZSL", WZSL1);
						}
						if (WZSL1 < 0) {
							msg = "科室账不足！";
							throw new RuntimeException("科室账不足！");
						}
						record.put("WZSL", WZSL1);
						record.put("WZJE", WZJE1);
						dao.doSave("update", BSPHISEntryNames.WL_KSZC, record,
								false);
					}
				}
				/**
				 * 然后 则根据库存序号(kcxh)和转入科室在科室帐是否存在。如果存在则在对应记录中加上物资数量、物资金额；
				 * 如果不存在则在科室帐插入对应物资信息
				 */
				// 5） 判断每条记录的库存序号(kcxh)和转入科室在科室帐是否存在
				List<Map<String, Object>> kszclist = get_kszc(KCXH, ZRKS, dao);

				if (kszclist.size() > 0) {
					for (int j = 0; j < kszclist.size(); j++) {
						if (Long.parseLong(kszclist.get(j).get("KCXH") + "") == KCXH) {
							Map<String, Object> record = kszclist.get(j);
							double WZSL1 = MedicineUtils.parseDouble(record.get("WZSL")) + WZSL;
							double WZJE1 = MedicineUtils.parseDouble(record.get("WZJE"))  + WZJE;
							if (WZSL1 < 0) {
								msg = "科室账不足！";
								throw new RuntimeException("科室账不足！");
							}
							record.put("WZSL", WZSL1);
							record.put("WZJE", WZJE1);
							dao.doSave("update", BSPHISEntryNames.WL_KSZC,
									record, false);
						}
					}
				} else {
					ywmxMap.put("YKSL", 0);
					ywmxMap.put("ZRRQ", new Date());
					dao.doSave("create", BSPHISEntryNames.WL_KSZC, ywmxMap,
							false);
				}
				// 判断资产账簿中是否存在。
				Map<String, Object> ZCZB_Per = new HashMap<String, Object>();
				ZCZB_Per.put("ZBXH", ZBXH);
				StringBuffer ZCZB_hql = new StringBuffer(
						"select ZYKS as ZYKS,ZRRQ as ZRRQ FROM WL_ZCZB where ZBXH=:ZBXH");
				List<Map<String, Object>> ZCZB_list = dao.doQuery(
						ZCZB_hql.toString(), ZCZB_Per);
				if (ZCZB_list.size() > 0) {
					Map<String, Object> record = ZCZB_list.get(0);
					record.put("CLBZ", 0);
					record.put("ZYKS", ZRKS);
					record.put("ZRRQ", new Date());
					record.put("ZBXH", ZBXH);
					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, record,
							false);
				}
				// 科室账册不判断物资库存是否为0 所以给Of_check_kc 方法穿一个空的List
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				// 第六步 判断库存
				Of_check_kc(list, kszc_list.get(0), dao);
				Of_Zcmx(ywmxMap, dao);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
		return true;
	}

	// 科室账册
	// 物质库存
	public static List<Map<String, Object>> get_kszc(Long kcxh, Long ksdm,
			BaseDAO dao) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (kcxh == 0 || ksdm == 0) {
			throw new RuntimeException("库存序号或科室代码不能为空！");
		}
		Map<String, Object> kszc_Per = new HashMap<String, Object>();
		kszc_Per.put("KCXH", kcxh);
		kszc_Per.put("KSDM", ksdm);
		try {
			StringBuffer hql = new StringBuffer(
					"SELECT JLXH as JLXH,JGID as JGID,KSDM as KSDM,KCXH as KCXH,KFXH as KFXH,ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,SCRQ as SCRQ,SXRQ as SXRQ,WZPH as WZPH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,YKSL as YKSL,ZRRQ as ZRRQ FROM WL_KSZC  where KCXH =:KCXH and KSDM=:KSDM");
			list = dao.doQuery(hql.toString(), kszc_Per);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new RuntimeException("科室账册查询失败！");
		}
		return list;
	}

	// 报损
	public static void uf_bs(List<Map<String, Object>> ids_ywmx, int ZHZT,
			BaseDAO dao, Context ctx) {
		// User user = (User) ctx.get("user.instance");
		// String KFXH = user.getProperty("treasuryId");
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		String msg = "记账失败！";
		try {
			for (int i = 0; i < ids_ywmx.size(); i++) {
				Map<String, Object> ywmxMap = ids_ywmx.get(i);
				long ZBXH = 0;
				if (ywmxMap.get("ZBXH") != null) {
					ZBXH = Long.parseLong(ywmxMap.get("ZBXH") + "");
				}
				long KCXH = Long.parseLong(ywmxMap.get("KCXH") + "");
				long KSDM = Long.parseLong(ywmxMap.get("KSDM") == null ? "0"
						: ywmxMap.get("KSDM") + "");
				double WZSL = Double.parseDouble(ywmxMap.get("WZSL") + "");
				double WZJE = Double.parseDouble(ywmxMap.get("WZJE") + "");
				long CJXH = Long.parseLong(ywmxMap.get("CJXH") + "");
				long WZXH = Long.parseLong(ywmxMap.get("WZXH") + "");
				int GLFS = Integer.parseInt(ywmxMap.get("GLFS") + "");

				ywmxMap.put("KFXH", Integer.parseInt(KFXH));

				// 判断在物质表中是否存在
				int num = Of_check_wzcj(WZXH, CJXH, dao);
				if (num < 1) {
					break;
				}
				// 资产账的处理
				Map<String, Object> zczbMap = new HashMap<String, Object>();
				zczbMap.put("ZBXH", ZBXH);
				StringBuffer hql = new StringBuffer(
						"select ZYKS as ZYKS,ZRRQ as ZRRQ FROM WL_ZCZB where ZBXH=:ZBXH ");
				List<Map<String, Object>> ZCZB_list = dao.doQuery(
						hql.toString(), zczbMap);
				if (ZCZB_list.size() > 0) {
					Map<String, Object> record = new HashMap<String, Object>();
					record.put("WZZT", ZHZT);
					record.put("CLBZ", 0);
					record.put("BSRQ", new Date());
					record.put("ZBXH", ZBXH);
					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, record,
							false);
				}
				// 科室账的处理
				Map<String, Object> kszcMap = new HashMap<String, Object>();
				if (KSDM > 0) {
					Map<String, Object> KSZC_Per = new HashMap<String, Object>();
					KSZC_Per.put("KSDM", KSDM);
					KSZC_Per.put("KCXH", KCXH);
					KSZC_Per.put("KFXH", Integer.parseInt(KFXH));
					StringBuffer KSZC_hql = new StringBuffer(
							"select JLXH as JLXH,JGID as JGID,KSDM as KSDM,KFXH as KFXH,ZBLB as ZBLB,KCXH as KCXH,WZXH as WZXH,CJXH as CJXH,SCRQ as SCRQ,SXRQ as SXRQ,WZPH as WZPH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,ZRRQ as ZRRQ,YKSL as YKSL FROM WL_KSZC where KSDM =:KSDM and KFXH=:KFXH and KCXH =:KCXH");
					List<Map<String, Object>> KSZC_list = dao.doQuery(
							KSZC_hql.toString(), KSZC_Per);
					if (KSZC_list.size() > 0) {
						Map<String, Object> record = kszcMap = KSZC_list.get(0);
						double WZSL_KSZC = Double.parseDouble(kszcMap
								.get("WZSL") + "")
								- WZSL;
						double WZJE_KSZC = Double.parseDouble(kszcMap
								.get("WZJE") + "")
								- WZJE;
						if (GLFS != 3) {
							double YKSL_KSZC = Double.parseDouble(kszcMap
									.get("YKSL") + "")
									- WZSL;
							record.put("YKSL", YKSL_KSZC);
						}
						// 管理方式
						if (WZSL_KSZC < 0) {
							msg = "库存不足！";
							throw new RuntimeException("库存不足！");
						}
						record.put("WZSL", WZSL_KSZC);
						record.put("WZJE", WZJE_KSZC);
						dao.doSave("update", BSPHISEntryNames.WL_KSZC, record,
								false);
					} else {
						msg = "库存不足！";
						throw new RuntimeException("库存不足！");
					}
				}

				// 库存帐的处理
				List<Map<String, Object>> wzkc_list = get_wzkc("KCXH", KCXH,
						WZXH, dao, ctx);
				if (KSDM <= 0) {
					if (wzkc_list.size() > 0) {
						Map<String, Object> record = wzkc_list.get(0);
						double WZSL1 = MedicineUtils.parseDouble(record.get("WZSL"))  - WZSL;
						double WZJE1 = MedicineUtils.parseDouble(record.get("WZJE"))  - WZJE;
						if (GLFS != 3) {
							double YKSL = MedicineUtils.parseDouble(record.get("YKSL"))  - WZSL;
							record.put("YKSL", YKSL);
						}
						if (WZSL1 < 0) {
							msg = "库存不足！";
							throw new RuntimeException("库存不足！");
						} else {
							record.put("WZSL", WZSL1);
							record.put("WZJE", WZJE1);
							dao.doSave("update", BSPHISEntryNames.WL_WZKC,
									record, false);
						}
					} else {
						msg = "库存不足！";
						throw new RuntimeException("库存不足！");
					}
				}
				// 第六步 判断库存
				Of_check_kc(wzkc_list, kszcMap, dao);
				Of_Zcmx(ywmxMap, dao);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	}

	// 重置
	public static void uf_cz(List<Map<String, Object>> ids_ywmx, BaseDAO dao,
			Context ctx) {
		// User user = (User) ctx.get("user.instance");
		// String KFXH = user.getProperty("treasuryId");
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		String msg = "记账失败！";
		try {
			for (int i = 0; i < ids_ywmx.size(); i++) {
				Map<String, Object> ywmxMap = ids_ywmx.get(i);
				long ZBXH = 0;
				if (ywmxMap.get("ZBXH") != null || ywmxMap.get("ZBXH") != "") {
					ZBXH = Long.parseLong(ywmxMap.get("ZBXH") + "");
				}
				double ZCXZ = Double.parseDouble(ywmxMap.get("ZCXZ") + "");
				int CZFS = Integer.parseInt(ywmxMap.get("CZFS") + "");
				long CJXH = Long.parseLong(ywmxMap.get("CJXH") + "");
				long WZXH = Long.parseLong(ywmxMap.get("WZXH") + "");

				ywmxMap.put("KFXH", KFXH);
				ywmxMap.put("WZSL", 1);
				ywmxMap.put("WZJG", ZCXZ);
				ywmxMap.put("WZJE", ZCXZ);
				ywmxMap.put("YWFS", CZFS);

				// 判断在物质表中是否存在
				int num = Of_check_wzcj(WZXH, CJXH, dao);
				if (num < 1) {
					break;
				}
				// 资产账的处理
				Map<String, Object> zczbMap = new HashMap<String, Object>();
				zczbMap.put("ZBXH", ZBXH);
				StringBuffer hql = new StringBuffer(
						"select ZYKS as ZYKS,ZRRQ as ZRRQ,ZCYZ as ZCYZ,CZYZ as CZYZ FROM WL_ZCZB where ZBXH=:ZBXH ");
				List<Map<String, Object>> ZCZB_list = dao.doQuery(
						hql.toString(), zczbMap);
				if (ZCZB_list.size() > 0) {
					Map<String, Object> record = ZCZB_list.get(0);
					record.put("CZRQ", new Date());
					record.put("CLBZ", 0);
					record.put("ZBXH", ZBXH);
					if (CZFS == 1) {
						record.put("CZYZ",
								Double.parseDouble(record.get("CZYZ") + "")
										+ ZCXZ);
					} else {
						record.put("CZYZ",
								Double.parseDouble(record.get("CZYZ") + "")
										- ZCXZ);
					}

					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, record,
							false);
				} else {
					msg = "找不对当前账簿序号的固定资产!";
					throw new RuntimeException("找不对当前账簿序号的固定资产!");
				}
				// 资产明细
				Of_Zcmx(ywmxMap, dao);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	}

	// 设置物资编号
	public static String setWZBH(Map<String, Object> parameters, BaseDAO dao,
			Context ctx) {
		Map<String, Object> parameterswzbh = new HashMap<String, Object>();
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String tzrq = parameters.get("TZRQ") + "";
		String years = tzrq.substring(0, 4);
		String zblb = parameters.get("ZBLB") + "";
		if (zblb.length() < 2) {
			zblb = "0" + zblb;
		} else if (zblb.length() > 2) {
			zblb = zblb.substring(0, 2);
		}
		String hslb = "00";
		String wzxh = parameters.get("WZXH") + "";
		parameterswzbh.put("WZXH", Long.parseLong(wzxh));
		parameterswzxh.put("WZXH", Long.parseLong(wzxh));
		String xh = "";
		String wzbh = "";
		int number = 0;
		try {
			Date tzrqdate = sdf.parse(tzrq);
			parameterswzbh.put("TZRQ", tzrqdate);
			Map<String, Object> wzxhMap = dao
					.doLoad("select ZBLB as ZBLB,HSLB as HSLB from WL_WZZD where WZXH=:WZXH",
							parameterswzxh);
			parameterswzbh.put("ZBLB",
					Integer.parseInt(wzxhMap.get("ZBLB") + ""));
			parameterswzbh.put("HSLB",
					Integer.parseInt(wzxhMap.get("HSLB") + ""));
			hslb = wzxhMap.get("HSLB") + "";
			if (hslb.length() < 2) {
				hslb = "0" + hslb;
			} else if (hslb.length() > 2) {
				hslb = hslb.substring(0, 2);
			}
			Map<String, Object> saveMap = new HashMap<String, Object>();
			saveMap.put("BZ", 1);
			Map<String, Object> zbxhMap = dao.doSave("create",
					BSPHISEntryNames.WL_ZBXH, saveMap, false);
			if (zbxhMap.size() > 0) {
				if (zbxhMap.get("ZBXH") != null) {
					number = Integer.parseInt(zbxhMap.get("ZBXH") + "");
				}
			}
			if (number < 10) {
				xh = "0000" + number;
			} else if (number < 100) {
				xh = "000" + number;
			} else if (number < 1000) {
				xh = "00" + number;
			} else if (number < 10000) {
				xh = "0" + number;
			} else {
				xh = "99999";
			}
			wzbh = years + zblb + hslb + xh;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
		return wzbh;
	}

	/**
	 * 更新费用明细
	 * 
	 * @param iu_fymxList
	 *            明细对象集合
	 * @param op
	 *            调用dao.doSave中的op参数
	 * @param entryName
	 *            调用dao.doSave中的entryName参数
	 * @param validate
	 *            调用dao.doSave中的validate参数
	 * @param dao
	 * @return
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 */
	public static int uf_update_fymx(List<Map<String, Object>> iu_fymxList,
			String op, String entryName, boolean validate, BaseDAO dao)
			throws ValidateException, PersistentDataOperationException {
		int rValue = 0;
		try {
			if (iu_fymxList != null) {
				for (int i = 0; i < iu_fymxList.size(); i++) {
					dao.doSave(op, entryName, iu_fymxList.get(i), validate);
				}
			}
			rValue = 1;
		} catch (ValidateException e) {
			throw e;
		} catch (PersistentDataOperationException e) {
			throw e;
		}
		return rValue;
	}

	/**
	 * 更新费用明细
	 * 
	 * @param iu_fymxList
	 *            明细对象集合
	 * @param op
	 *            调用dao.doSave中的op参数
	 * @param entryName
	 *            调用dao.doSave中的entryName参数
	 * @param validate
	 *            调用dao.doSave中的validate参数
	 * @param dao
	 * @return
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 */
	public static int uf_update_jc_fymx(List<Map<String, Object>> iu_fymxList,
			String op, String entryName, boolean validate, BaseDAO dao)
			throws ValidateException, PersistentDataOperationException {
		int rValue = 0;
		try {
			if (iu_fymxList != null) {
				for (int i = 0; i < iu_fymxList.size(); i++) {
					dao.doSave(op, entryName, iu_fymxList.get(i), validate);
				}
			}
			rValue = 1;
		} catch (ValidateException e) {
			throw e;
		} catch (PersistentDataOperationException e) {
			throw e;
		}
		return rValue;
	}

	// 设置库存序号
	public static long setKCXH(BaseDAO dao) {
		Long l = 0L;
		Map<String, Object> saveMap = new HashMap<String, Object>();
		saveMap.put("BZ", 1);
		try {
			Map<String, Object> zbxhMap = dao.doSave("create",
					BSPHISEntryNames.WL_KCXH, saveMap, false);
			if (zbxhMap.size() > 0) {
				if (zbxhMap.get("KCXH") != null) {
					l = Long.parseLong(zbxhMap.get("KCXH") + "");
				}
			}
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return l;
	}

	// 欠费控制判断
	public static long Wf_qfkz(Map<String, Object> sendMap, BaseDAO dao, Context ctx) {
		long al_zyh = MedicineUtils.parseLong(sendMap.get("ll_zyh"));
		double ad_fsje = MedicineUtils.parseDouble(sendMap.get("ld_zfhj")) ;
		String JGID = MedicineUtils.parseString(sendMap.get("jgid")) ;
		Map<String, Object> Ls_brxm = (Map<String, Object>) sendMap.get("Ls_brxm");
		long brxz = MedicineUtils.parseLong(sendMap.get("brxz")) ;
		long brks = MedicineUtils.parseLong(sendMap.get("brks")) ;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			parameters.put("ZYH", al_zyh);
			List<Map<String, Object>> jkhjList = dao
					.doQuery(
							"select sum(JKJE)as ld_jkhj From ZY_TBKK Where JGID =:JGID and ZYH=:ZYH and ZFPB = 0 and JSCS = 0",
							parameters);
			List<Map<String, Object>> fyhjList = dao
					.doQuery(
							"Select sum(ZJJE) as ld_fyhj,sum(ZFJE) as ld_zfhj from ZY_FYMX where JGID=:JGID and ZYH =:ZYH and JSCS = 0",
							parameters);
			double ld_jkhj = 0;
			double ld_fyhj = 0;
			double ld_zfhj = 0;
			if (jkhjList.size() > 0) {
				if (jkhjList.get(0) != null) {
					// System.out.println(jkhjList.get(0));
					ld_jkhj = Double.parseDouble(jkhjList.get(0) + "");
				}
			}
			if (fyhjList.size() > 0) {
				if (fyhjList.get(0).get("ld_fyhj") != null) {
					ld_fyhj = Double.parseDouble(fyhjList.get(0).get("ld_fyhj")
							+ "");
				}
				if (fyhjList.get(0).get("ld_zfhj") != null) {
					ld_zfhj = Double.parseDouble(fyhjList.get(0).get("ld_zfhj")
							+ "");
				}
			}
			double ld_qfje = ld_zfhj - ld_jkhj;
			String Ls_brxm_s = "";
			String CKWH = ParameterUtil.getParameter(JGID, BSPHISSystemArgument.CKWH, ctx);
			Map<String, Object> CKWHparameters = new HashMap<String, Object>();
			CKWHparameters.put("JGID", JGID);
			double djje = 0;
			if ("1".equals(CKWH)) {//按科室
				CKWHparameters.put("KSDM", brks);
				Map<String,Object> ZY_CKWH = dao.doLoad("select nvl(a.DJJE,0) as DJJE from ZY_CKWH a where a.KSDM=:KSDM and a.JGID=:JGID", CKWHparameters);
				djje = MedicineUtils.parseDouble(ZY_CKWH.get("DJJE")) ;
			} else {//按性质
				CKWHparameters.put("BRXZ", brxz);
				Map<String,Object> ZY_CKWH = dao.doLoad("select nvl(a.DJJE,0) as DJJE from ZY_CKWH a where a.BRXZ=:BRXZ and a.JGID=:JGID", CKWHparameters);
				djje =MedicineUtils.parseDouble(ZY_CKWH.get("DJJE"));
			}
			if ((ld_qfje >= djje) || (ld_qfje + ad_fsje > djje)) {
				long ls_brxm_new = al_zyh;
				if (Ls_brxm_s.equals("")) {
					Ls_brxm_s = ls_brxm_new + "";
				} else {
					Ls_brxm_s += "," + ls_brxm_new;
				}
				Ls_brxm.put("Ls_brxm", Ls_brxm_s);
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("费用控制查询失败！");
		}
		return 1;
	}
	//update by caijy for 家床
	public static long FSBWf_qfkz(long al_zyh, double ad_fsje, String JGID,
			Map<String, Object> Ls_brxm, BaseDAO dao) {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			parameters.put("ZYH", al_zyh);
			List<Map<String, Object>> jkhjList = dao
					.doQuery(
							"select sum(JKJE)as ld_jkhj From JC_TBKK Where JGID =:JGID and ZYH=:ZYH and ZFPB = 0 and JSCS = 0",
							parameters);
			List<Map<String, Object>> fyhjList = dao
					.doQuery(
							"Select sum(ZJJE) as ld_fyhj,sum(ZFJE) as ld_zfhj from JC_FYMX where JGID=:JGID and ZYH =:ZYH and JSCS = 0",
							parameters);
			double ld_jkhj = 0;
			double ld_fyhj = 0;
			double ld_zfhj = 0;
			if (jkhjList.size() > 0) {
				if (jkhjList.get(0) != null) {
					// System.out.println(jkhjList.get(0));
					ld_jkhj = Double.parseDouble(jkhjList.get(0) + "");
				}
			}
			if (fyhjList.size() > 0) {
				if (fyhjList.get(0).get("ld_fyhj") != null) {
					ld_fyhj = Double.parseDouble(fyhjList.get(0).get("ld_fyhj")
							+ "");
				}
				if (fyhjList.get(0).get("ld_zfhj") != null) {
					ld_zfhj = Double.parseDouble(fyhjList.get(0).get("ld_zfhj")
							+ "");
				}
			}
			double ld_qfje = ld_zfhj - ld_jkhj;
			String Ls_brxm_s = "";
			if ((ld_qfje >= 0) || (ld_qfje + ad_fsje > 0)) {
				long ls_brxm_new = al_zyh;
				if (Ls_brxm_s.equals("")) {
					Ls_brxm_s = ls_brxm_new + "";
				} else {
					Ls_brxm_s += "," + ls_brxm_new;
				}
				Ls_brxm.put("Ls_brxm", Ls_brxm_s);
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("费用控制查询失败！");
		}
		return 1;
	}
	// 病区医嘱（欠费控制）
	public static boolean ArrearsPatientsQuery(
			List<Map<String, Object>> collardrugdetailslist, Context ctx,
			BaseDAO dao, Map<String, Object> res)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String jgid = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		// 私有参数 是否管理收费（0不管，1管）
		String ZYQFJEDJKZ = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.ZYQFJEDJKZ, ctx);
		int ll_qfkz = Integer.parseInt(ZYQFJEDJKZ);
		Map<String, Object> Ls_brxm = new HashMap<String, Object>();
		long ll_zyh = 0;
		String brxString = " ";
		String zyhString = " ";
		boolean flag = false;// 定义返回值，如果有超过余额的返回true 否则返回false
		for (int ll_row = 0; ll_row < collardrugdetailslist.size(); ll_row++) {
			if (ll_qfkz > 0) {
				long ZYH = Long.parseLong(collardrugdetailslist.get(ll_row)
						.get("ZYH") + "");
				if ((ll_row == 0) || (ZYH != ll_zyh)) {
					ll_zyh = ZYH;
//					double ld_hj = 0;
					double ld_zfhj = 0;
					long brxz =  MedicineUtils.parseLong(collardrugdetailslist.get(ll_row).get("BRXZ")) ;
					long brks =  MedicineUtils.parseLong(collardrugdetailslist.get(ll_row).get("BRKS")) ;
					for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
							.size(); ll_row1++) {
						long ZYH1 = Long.parseLong(collardrugdetailslist.get(
								ll_row1).get("ZYH")
								+ "");
						if (ZYH1 == ll_zyh) {
							double je = parseDouble(collardrugdetailslist
									.get(ll_row1).get("JE"));
							Map<String, Object> rowMap = collardrugdetailslist.get(ll_row1);
							Map<String, Object> body = new HashMap<String, Object>();
							body.put("TYPE", rowMap.get("YPLX"));
							body.put("BRXZ", brxz);
							body.put("FYXH", rowMap.get("YPXH"));
							if (rowMap.get("FYGB") != null) {
								body.put("FYGB", rowMap.get("FYGB"));
							} else {
								body.put("FYGB", 0);
							}
							Map<String, Object> zfblMap = getzfbl(body, ctx, dao);
							double zfbl = Double.parseDouble(zfblMap.get("ZFBL") + "");
//							ld_hj += je;
							ld_zfhj += je*zfbl;
						}
						
					}
					Map<String,Object> sendMap = new HashMap<String, Object>();
					sendMap.put("ll_zyh", ll_zyh);
					sendMap.put("ld_zfhj", ld_zfhj);
					sendMap.put("jgid", jgid);
					sendMap.put("Ls_brxm", Ls_brxm);
					sendMap.put("brxz", brxz);
					sendMap.put("brks", brks);
					if (Wf_qfkz(sendMap, dao, ctx) == 1) {
						for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist
									.get(ll_row1).get("ZYH") + "");
							if (collardrugdetailslist.get(ll_row).get("ok") != null) {
								String ok = collardrugdetailslist.get(ll_row)
										.get("ok") + "";
								if (ZYH1 == ll_zyh && ok.equals("1")) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 1);
								}
							} else {
								if (ZYH1 == ll_zyh) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 1);
								}
							}
						}
					} else {
						for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist
									.get(ll_row1).get("ZYH") + "");
							if (collardrugdetailslist.get(ll_row).get("ok") != null) {
								String ok = collardrugdetailslist.get(ll_row)
										.get("ok") + "";
								if (ZYH1 == ll_zyh && ok.equals("1")) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 0);
								}
							} else {
								if (ZYH1 == ll_zyh) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 0);
								}
							}
						}
					}
				}
				res.put("RES_MESSAGE1", "");
				res.put("RES_ZYH", "");
				if (Ls_brxm.get("Ls_brxm") != null) {
					Map<String, Object> brxmMap = new HashMap<String, Object>();
					// int num = 0;
					try {
						for (int ll_row1 = ll_row + 1; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist
									.get(ll_row1).get("ZYH") + "");
							if (ZYH1 == Long.parseLong(Ls_brxm.get("Ls_brxm")
									+ "")) {
								// num++;
								collardrugdetailslist.remove(ll_row1);
								ll_row1--;
							}

						}
						// if (num == 0) {
						brxmMap = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
								Long.parseLong(Ls_brxm.get("Ls_brxm") + ""));
						brxString += "'" + brxmMap.get("BRXM") + "'";
						zyhString += "'" + Ls_brxm.get("Ls_brxm") + "'";
						res.put("RES_MESSAGE1", brxString);
						res.put("RES_ZYH", zyhString);
						// }
						collardrugdetailslist.remove(ll_row);
						ll_row--;
						Ls_brxm.clear();
						if (brxString.length() > 0) {
							res.put("RES_MESSAGE", "病人  " + brxString
									+ " 预交款余额不足，不能确认!");
							flag = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("病人姓名查询失败！");
					}
				}
			}
		}
		return flag;
	}
	
	// 病区医嘱（欠费控制）
		public static boolean FSBArrearsPatientsQuery(
				List<Map<String, Object>> collardrugdetailslist, Context ctx,
				BaseDAO dao, Map<String, Object> res)
				throws ModelDataOperationException {
			// User user = (User) ctx.get("user.instance");
			// String jgid = user.get("manageUnit.id");
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			// 私有参数 是否管理收费（0不管，1管）
			String ZYQFKZ = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.JCQFKZ, ctx);
			int ll_qfkz = Integer.parseInt(ZYQFKZ);
			Map<String, Object> Ls_brxm = new HashMap<String, Object>();
			long ll_zyh = 0;
			String brxString = " ";
			String zyhString = " ";
			boolean flag = false;// 定义返回值，如果有超过余额的返回true 否则返回false
			for (int ll_row = 0; ll_row < collardrugdetailslist.size(); ll_row++) {
				Map<String, Object> rowMap = collardrugdetailslist.get(ll_row);
				Map<String, Object> body = new HashMap<String, Object>();
				body.put("TYPE", rowMap.get("YPLX"));
				body.put("BRXZ", rowMap.get("BRXZ"));
				body.put("FYXH", rowMap.get("YPXH"));
				if (rowMap.get("FYGB") != null) {
					body.put("FYGB", rowMap.get("FYGB"));
				} else {
					body.put("FYGB", 0);
				}
				Map<String, Object> zfblMap = getzfbl(body, ctx, dao);
				double zfbl = Double.parseDouble(zfblMap.get("ZFBL") + "");
				if (ll_qfkz > 0) {// 欠费管理 
					long ZYH = Long.parseLong(collardrugdetailslist.get(ll_row)
							.get("ZYH") + "");
					if ((ll_row == 0) || (ZYH != ll_zyh)) {
						ll_zyh = ZYH;
						double ld_hj = 0;
						for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist.get(
									ll_row1).get("ZYH")
									+ "");
							double je = parseDouble(collardrugdetailslist
									.get(ll_row1).get("JE"));
							if (ZYH1 == ll_zyh) {
								ld_hj += je;
							}
						}
						if (FSBWf_qfkz(ll_zyh, ld_hj * zfbl, jgid, Ls_brxm, dao) == 1) {
							for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
									.size(); ll_row1++) {
								long ZYH1 = Long.parseLong(collardrugdetailslist
										.get(ll_row1).get("ZYH") + "");
								if (collardrugdetailslist.get(ll_row).get("ok") != null) {
									String ok = collardrugdetailslist.get(ll_row)
											.get("ok") + "";
									if (ZYH1 == ll_zyh && ok.equals("1")) {
										collardrugdetailslist.get(ll_row1).put(
												"SFTJ", 1);
									}
								} else {
									if (ZYH1 == ll_zyh) {
										collardrugdetailslist.get(ll_row1).put(
												"SFTJ", 1);
									}
								}
							}
						} else {
							for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
									.size(); ll_row1++) {
								long ZYH1 = Long.parseLong(collardrugdetailslist
										.get(ll_row1).get("ZYH") + "");
								if (collardrugdetailslist.get(ll_row).get("ok") != null) {
									String ok = collardrugdetailslist.get(ll_row)
											.get("ok") + "";
									if (ZYH1 == ll_zyh && ok.equals("1")) {
										collardrugdetailslist.get(ll_row1).put(
												"SFTJ", 0);
									}
								} else {
									if (ZYH1 == ll_zyh) {
										collardrugdetailslist.get(ll_row1).put(
												"SFTJ", 0);
									}
								}
							}
						}
					}
					res.put("RES_MESSAGE1", "");
					res.put("RES_ZYH", "");
					if (Ls_brxm.get("Ls_brxm") != null) {
						Map<String, Object> brxmMap = new HashMap<String, Object>();
						// int num = 0;
						try {
							for (int ll_row1 = ll_row + 1; ll_row1 < collardrugdetailslist
									.size(); ll_row1++) {
								long ZYH1 = Long.parseLong(collardrugdetailslist
										.get(ll_row1).get("ZYH") + "");
								if (ZYH1 == Long.parseLong(Ls_brxm.get("Ls_brxm")
										+ "")) {
									// num++;
									collardrugdetailslist.remove(ll_row1);
									ll_row1--;
								}

							}
							// if (num == 0) {
							brxmMap = dao.doLoad(BSPHISEntryNames.JC_BRRY,
									Long.parseLong(Ls_brxm.get("Ls_brxm") + ""));
							brxString += "'" + brxmMap.get("BRXM") + "'";
							zyhString += "'" + Ls_brxm.get("Ls_brxm") + "'";
							res.put("RES_MESSAGE1", brxString);
							res.put("RES_ZYH", zyhString);
							// }
							collardrugdetailslist.remove(ll_row);
							ll_row--;
							Ls_brxm.clear();
							if (brxString.length() > 0) {
								res.put("RES_MESSAGE", "病人  " + brxString
										+ " 预交款余额不足，不能确认!");
								flag = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException("病人姓名查询失败！");
						}
					}
				}
			}
			return flag;
		}

	/**
	 * 判断一个字符串是否为数字（包括整数和小数）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// 根据处置开的项目去取对应的价格并插入到物资库存表
	public static void setSupplies(BaseDAO dao, Context ctx, Long sbxhs,
			long ghgl) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterskfxh = new HashMap<String, Object>();
		// 根据sbxh取到该条项目的所有信息
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String hql = "select a.SBXH as SBXH,a.YLXH as FYXH,a.YLSL as CZSL,d.BZJG as BZJG,f.JGBZ as JGBZ,b.BRID as BRID,c.BRXM as BRXM,b.JGID as JGID from MS_YJ02 a,MS_YJ01 b,MS_BRDA c,GY_YLSF d,GY_YLMX f where a.YJXH=b.YJXH and b.BRID=c.BRID and a.YLXH=d.FYXH and d.FYXH=f.FYXH and f.JGID=:JGID and a.SBXH=:SBXH and b.MZXH is null";
		try {
			parameters.put("SBXH", sbxhs);
			parameters.put("JGID", manaUnitId);
			Map<String, Object> lisFYXX = dao.doLoad(hql, parameters);
			StringBuffer hql1 = new StringBuffer();
			hql1.append("select KFXH as KFXH from WL_KFDZ where KSDM=:ejkf");
			Long deptId = 0L;
			String ksmc = "";
			if (user.getProperty("biz_departmentId") != null
					&& user.getProperty("biz_departmentId") != "") {
				deptId = user.getProperty("biz_departmentId", Long.class);
				ksmc = DictionaryController.instance()
						.get("phis.dictionary.department").getText(deptId + "");
			}
			if (deptId == 0) {
				StringBuffer hqlksdm = new StringBuffer();
				Map<String, Object> parametersmzks = new HashMap<String, Object>();
				parametersmzks.put("sbxh", ghgl);
				hqlksdm.append(
						"select c.MZKS as MZKS,b.OFFICENAME as KSMC from ")
						.append("MS_GHMX a,SYS_Office b,MS_GHKS c where a.KSDM=c.KSDM and c.MZKS=b.ID and a.SBXH=:sbxh");
				Map<String, Object> map_ghxx = dao.doLoad(hqlksdm.toString(),
						parametersmzks);

				if (map_ghxx != null && map_ghxx.get("MZKS") != null) {
					deptId = Long.parseLong(map_ghxx.get("MZKS") + "");
					ksmc = map_ghxx.get("KSMC") + "";
				}
			}
			parameterskfxh.put("ejkf", deptId);
			Map<String, Object> kfxhMap = dao.doLoad(hql1.toString(),
					parameterskfxh);
			int kfxh = 0;
			if (kfxhMap != null) {
				if (kfxhMap.get("KFXH") != null) {
					kfxh = Integer.parseInt(kfxhMap.get("KFXH") + "");
				}
			} else {
				throw new RuntimeException("当前科室或病区没有对应的物资库房！");
			}
			if (lisFYXX != null) {
				double czsl = Double.parseDouble(lisFYXX.get("CZSL") + "");
				Long sbxh = Long.parseLong(lisFYXX.get("SBXH") + "");
				Long fyxh = Long.parseLong(lisFYXX.get("FYXH") + "");
				Long brid = Long.parseLong(lisFYXX.get("BRID") + "");
				int jgbz = 0;
				if (lisFYXX.get("JGBZ") != null) {
					jgbz = Integer.parseInt(lisFYXX.get("JGBZ") + "");
				}
				double bzjg = 0;
				if (lisFYXX.get("BZJG") != null) {
					bzjg = Double.parseDouble(lisFYXX.get("BZJG") + "");
				}
				String brxm = lisFYXX.get("BRXM") + "";
				String jgid = lisFYXX.get("JGID") + "";
				setSuppliesJG(dao, sbxh, fyxh, kfxh, czsl, brid, "1", deptId,
						brxm, ksmc, -2, jgid, jgbz, bzjg, null);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}

	}

	// 根据处置开的项目去取对应的价格并插入到物资库存表
	public static void setSuppliesZY(BaseDAO dao, Context ctx, Long jlxh) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterskfxh = new HashMap<String, Object>();
		Map<String, Object> parametersksdm = new HashMap<String, Object>();
		// 根据sbxh取到该条项目的所有信息
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String hql = "select a.JLXH as SBXH,a.YPXH as FYXH,(a.YCSL*a.MRCS) as CZSL,b.BRID as BRID,c.BRXM as BRXM,b.JGID as JGID,d.BZJG as BZJG,f.JGBZ as JGBZ,a.ZYH as ZYH from ZY_BQYZ a, ZY_BRRY b, MS_BRDA c,GY_YLSF d,GY_YLMX f where a.ZYH=b.ZYH and a.JGID=b.JGID and b.BRID = c.BRID and a.YPXH = d.FYXH and d.FYXH = f.FYXH and f.JGID = :JGID and a.JLXH = :JLXH and a.QRSJ is null";
		try {
			parameters.put("JLXH", jlxh);
			parameters.put("JGID", manaUnitId);
			parametersksdm.put("JGID", manaUnitId);
			Map<String, Object> lisFYXX = dao.doLoad(hql, parameters);
			if (lisFYXX != null) {
				parametersksdm.put("ZYH",
						Long.parseLong(lisFYXX.get("ZYH") + ""));
			}
			StringBuffer hql1 = new StringBuffer();
			hql1.append("select KFXH as KFXH from WL_KFDZ where KSDM=:ejkf");
			Long wardId = 0L;
			String wardName = null;
			String zyhm = null;
			Map<String, Object> ksdmMap = dao
					.doLoad("select a.BRBQ as BRBQ,b.OFFICENAME as KSMC,a.ZYHM as ZYHM from ZY_BRRY a,SYS_Office b where a.BRBQ=b.ID and a.ZYH=:ZYH and a.JGID=:JGID",
							parametersksdm);
			if (ksdmMap != null) {
				if (ksdmMap.get("BRBQ") != null) {
					wardId = Long.parseLong(ksdmMap.get("BRBQ") + "");
				}
				if (ksdmMap.get("KSMC") != null) {
					wardName = ksdmMap.get("KSMC") + "";
				}
				if (ksdmMap.get("ZYHM") != null) {
					zyhm = ksdmMap.get("ZYHM") + "";
				}
			}
			parameterskfxh.put("ejkf", wardId);
			Map<String, Object> kfxhMap = dao.doLoad(hql1.toString(),
					parameterskfxh);
			int kfxh = 0;
			if (kfxhMap != null) {
				if (kfxhMap.get("KFXH") != null) {
					kfxh = Integer.parseInt(kfxhMap.get("KFXH") + "");
				}
			} else {
				throw new RuntimeException("当前科室或病区没有对应的物资库房！");
			}
			if (lisFYXX != null) {
				double czsl = Double.parseDouble(lisFYXX.get("CZSL") + "");
				Long sbxh = Long.parseLong(lisFYXX.get("SBXH") + "");
				Long fyxh = Long.parseLong(lisFYXX.get("FYXH") + "");
				Long brid = Long.parseLong(lisFYXX.get("BRID") + "");
				String brxm = lisFYXX.get("BRXM") + "";
				String jgid = lisFYXX.get("JGID") + "";
				int jgbz = 0;
				if (lisFYXX.get("JGBZ") != null) {
					jgbz = Integer.parseInt(lisFYXX.get("JGBZ") + "");
				}
				double bzjg = 0;
				if (lisFYXX.get("BZJG") != null) {
					bzjg = Double.parseDouble(lisFYXX.get("BZJG") + "");
				}
				setSuppliesJG(dao, sbxh, fyxh, kfxh, czsl, brid, "2", wardId,
						brxm, wardName, -2, jgid, jgbz, bzjg, zyhm);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	// 根据处置开的项目去取对应的价格本计算金额
	public static void setSuppliesJG(BaseDAO dao, Long sbxh, Long fyxh,
			int kfxh, double czsl, Long brid, String brly, Long ksdm,
			String brxm, String ksmc, int ztbz, String jgid, int jgbz,
			double bzjg, String zyhm) {
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
		Map<String, Object> parameterswzjg = new HashMap<String, Object>();
		Map<String, Object> parametersupd = new HashMap<String, Object>();
		Map<String, Object> parametersykslupd = new HashMap<String, Object>();
		parameterswzxh.put("FYXH", fyxh);
		parameterswzxh.put("JGID", jgid);
		parametersupd.put("SBXH", sbxh);
		try {
			// 根据fyxh取对应的wzxh
			List<Map<String, Object>> lisWZXH = dao
					.doQuery(
							"select WZXH as WZXH,WZSL as WZSL from GY_FYWZ where FYXH=:FYXH and JGID=:JGID",
							parameterswzxh);
			double wzjg = 0.00;
			double czje = 0.00;
			for (int i = 0; i < lisWZXH.size(); i++) {
				double wzslall = czsl
						* Double.parseDouble(lisWZXH.get(i).get("WZSL") + "");// 取到第一个物资的实际数量
				parameterswzjg.put("WZXH",
						Long.parseLong(lisWZXH.get(i).get("WZXH") + ""));
				parameterswzjg.put("JGID", jgid);
				parameterswzjg.put("KFXH", kfxh);
				List<Map<String, Object>> lisWZKC = dao
						.doQuery(
								"select LSJG as WZJG,WZSL as WZSL,KCXH as KCXH,YKSL as YKSL,WZXH as WZXH from WL_WZKC where WZXH=:WZXH and JGID=:JGID and KFXH=:KFXH order by JLXH",
								parameterswzjg);
				for (int j = 0; j < lisWZKC.size(); j++) {// 第一个去做的金额
					double wzsl = Double.parseDouble(lisWZKC.get(j).get("WZSL")
							+ "");
					double yksl = Double.parseDouble(lisWZKC.get(j).get("YKSL")
							+ "");
					long kcxh = Long.parseLong(lisWZKC.get(j).get("KCXH") + "");
					parametersykslupd.put("KCXH", kcxh);
					parametersykslupd.put("KFXH", kfxh);
					parametersykslupd.put("JGID", jgid);
					long wzxhs = Long
							.parseLong(lisWZKC.get(j).get("WZXH") + "");
					if (wzslall <= (wzsl - yksl)) {
						if (jgbz == 1) {
							wzjg = Double.parseDouble(lisWZKC.get(j)
									.get("WZJG") + "");
						} else {
							wzjg = bzjg;
						}
						czje += wzslall * wzjg;
						parametersykslupd.put("YKSL", yksl + wzslall);
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH and JGID=:JGID",
								parametersykslupd);
						Map<String, Object> parametersyxhmxsave = new HashMap<String, Object>();
						parametersyxhmxsave.put("BRHM", zyhm);
						parametersyxhmxsave.put("BRXM", brxm);
						parametersyxhmxsave.put("WZSL", wzslall);
						parametersyxhmxsave.put("KSMC", ksmc);
						parametersyxhmxsave.put("BRID", brid);
						parametersyxhmxsave.put("BRLY", brly);
						parametersyxhmxsave.put("XHRQ", new Date());
						parametersyxhmxsave.put("KSDM", ksdm);
						parametersyxhmxsave.put("JGID", jgid);
						parametersyxhmxsave.put("KFXH", kfxh);
						parametersyxhmxsave.put("WZXH", wzxhs);
						parametersyxhmxsave.put("ZTBZ", ztbz);
						parametersyxhmxsave.put("MZXH", sbxh);
						parametersyxhmxsave.put("KCXH", kcxh);
						dao.doSave("create", BSPHISEntryNames.WL_XHMX,
								parametersyxhmxsave, false);
						break;
					} else if (wzslall > (wzsl - yksl)) {
						wzslall = wzslall - (wzsl - yksl);
						parametersykslupd.put("YKSL", yksl + (wzsl - yksl));
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH and JGID=:JGID",
								parametersykslupd);
						if ((wzsl - yksl) > 0) {
							Map<String, Object> parametersyxhmxsave = new HashMap<String, Object>();
							parametersyxhmxsave.put("BRHM", zyhm);
							parametersyxhmxsave.put("BRXM", brxm);
							parametersyxhmxsave.put("WZSL", (wzsl - yksl));
							parametersyxhmxsave.put("KSMC", ksmc);
							parametersyxhmxsave.put("BRID", brid);
							parametersyxhmxsave.put("BRLY", brly);
							parametersyxhmxsave.put("XHRQ", new Date());
							parametersyxhmxsave.put("KSDM", ksdm);
							parametersyxhmxsave.put("JGID", jgid);
							parametersyxhmxsave.put("KFXH", kfxh);
							parametersyxhmxsave.put("WZXH", wzxhs);
							parametersyxhmxsave.put("ZTBZ", ztbz);
							parametersyxhmxsave.put("MZXH", sbxh);
							parametersyxhmxsave.put("KCXH", kcxh);
							dao.doSave("create", BSPHISEntryNames.WL_XHMX,
									parametersyxhmxsave, false);
						}
						if (jgbz == 1) {
							wzjg = Double.parseDouble(lisWZKC.get(j)
									.get("WZJG") + "");
						} else {
							wzjg = bzjg;
						}
						czje += (wzsl - yksl) * wzjg;
					}
				}
			}
			double wzdj = czje / czsl;
			if (wzdj != 0.00 && czje != 0.00) {
				if ("1".equals(brly)) {
					parametersupd.put("YLDJ", wzdj);
					parametersupd.put("HJJE", czje);
					dao.doUpdate(
							"update MS_YJ02 set YLDJ=:YLDJ,HJJE=:HJJE where SBXH=:SBXH",
							parametersupd);
				} else if ("2".equals(brly)) {
					parametersupd.put("YPDJ", wzdj);
					dao.doUpdate(
							"update ZY_BQYZ set YPDJ=:YPDJ where JLXH=:SBXH",
							parametersupd);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	// 根据sbxh取消收费项目所对应的物资
	public static void deleteSupplies(BaseDAO dao, Context ctx, Long sbxh) {
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		deleteSuppliesJG(dao, sbxh, "1", manaUnitId);
	} // 根据处置开的项目去取对应的价格本计算金额

	// 根据sbxh取消收费项目所对应的物资
	public static void deleteSuppliesZY(BaseDAO dao, Context ctx, Long jlxh) {
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		deleteSuppliesJG(dao, jlxh, "2", manaUnitId);
	} // 根据处置开的项目去取对应的价格本计算金额

	public static void deleteSuppliesJG(BaseDAO dao, Long sbxh, String brly,
			String jgid) {
		Map<String, Object> parametersxhmx = new HashMap<String, Object>();
		Map<String, Object> parameterswzkc = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupdate = new HashMap<String, Object>();
		parametersxhmx.put("JGID", jgid);
		parametersxhmx.put("MZXH", sbxh);
		parametersxhmx.put("BRLY", brly);
		try {
			// 根据fyxh取对应的wzxh
			List<Map<String, Object>> lisXHMX = dao
					.doQuery(
							"select KFXH as KFXH,KCXH as KCXH,WZSL as WZSL from WL_XHMX where MZXH=:MZXH and JGID=:JGID and BRLY=:BRLY",
							parametersxhmx);
			for (int i = 0; i < lisXHMX.size(); i++) {
				parameterswzkc.put("KCXH",
						Long.parseLong(lisXHMX.get(i).get("KCXH") + ""));
				parameterswzkc.put("KFXH",
						Integer.parseInt(lisXHMX.get(i).get("KFXH") + ""));
				parameterswzkc.put("JGID", jgid);
				double wzsl = Double.parseDouble(lisXHMX.get(i).get("WZSL")
						+ "");
				Map<String, Object> lisWZKC = dao
						.doLoad("select YKSL as YKSL,KCXH as KCXH,KFXH as KFXH from WL_WZKC where KCXH=:KCXH and JGID=:JGID and KFXH=:KFXH",
								parameterswzkc);
				if (lisWZKC != null) {// 第一个去做的金额
					double yksl = Double.parseDouble(lisWZKC.get("YKSL") + "");
					parameterswzkcupdate.put("KCXH",
							Long.parseLong(lisWZKC.get("KCXH") + ""));
					parameterswzkcupdate.put("KFXH",
							Integer.parseInt(lisWZKC.get("KFXH") + ""));
					parameterswzkcupdate.put("JGID", jgid);
					parameterswzkcupdate.put("YKSL", yksl - wzsl);
					dao.doUpdate(
							"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and JGID=:JGID and KFXH=:KFXH",
							parameterswzkcupdate);
				}
			}
			dao.doUpdate(
					"delete from WL_XHMX where MZXH=:MZXH and JGID=:JGID and BRLY=:BRLY",
					parametersxhmx);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	// 根据处置开的项目去取对应的价格并插入到物资库存表
	public static void setSuppliesYKSL(BaseDAO dao, Context ctx, Long sbxh,
			int sign) {
		// 根据sbxh取到该条项目的所有信息
		// User user = (User) ctx.get("user.instance");
		// String jgid = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if (sign == 0) {
			addSuppliesYKSL(dao, sbxh, "1", jgid);
		} else {
			deleteSuppliesYKSL(dao, sbxh, "1", jgid);
		}
	}

	// 根据处置开的项目去取对应的价格并插入到物资库存表
	public static void setSuppliesYKSLZY(BaseDAO dao, Context ctx, Long jlxh,
			int sign) {
		// 根据sbxh取到该条项目的所有信息
		// User user = (User) ctx.get("user.instance");
		// String jgid = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if (sign == 0) {
			addSuppliesYKSL(dao, jlxh, "2", jgid);
		} else {
			deleteSuppliesYKSL(dao, jlxh, "2", jgid);
		}
	}

	public static void deleteSuppliesYKSL(BaseDAO dao, Long sbxh, String brly,
			String jgid) {
		Map<String, Object> parametersxhmx = new HashMap<String, Object>();
		Map<String, Object> parameterswzkc = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupdate = new HashMap<String, Object>();
		parametersxhmx.put("JGID", jgid);
		parametersxhmx.put("MZXH", sbxh);
		parametersxhmx.put("BRLY", brly);
		try {
			// 根据fyxh取对应的wzxh
			List<Map<String, Object>> lisXHMX = dao
					.doQuery(
							"select KFXH as KFXH,KCXH as KCXH,WZSL as WZSL from WL_XHMX where MZXH=:MZXH and JGID=:JGID and BRLY=:BRLY",
							parametersxhmx);
			for (int i = 0; i < lisXHMX.size(); i++) {
				parameterswzkc.put("KCXH",
						Long.parseLong(lisXHMX.get(i).get("KCXH") + ""));
				parameterswzkc.put("KFXH",
						Integer.parseInt(lisXHMX.get(i).get("KFXH") + ""));
				parameterswzkc.put("JGID", jgid);
				double wzsl = Double.parseDouble(lisXHMX.get(i).get("WZSL")
						+ "");
				Map<String, Object> lisWZKC = dao
						.doLoad("select YKSL as YKSL,KCXH as KCXH,KFXH as KFXH from WL_WZKC where KCXH=:KCXH and JGID=:JGID and KFXH=:KFXH",
								parameterswzkc);
				for (int j = 0; j < lisWZKC.size(); j++) {// 第一个去做的金额
					double yksl = Double.parseDouble(lisWZKC.get("YKSL") + "");
					parameterswzkcupdate.put("KCXH",
							Long.parseLong(lisWZKC.get("KCXH") + ""));
					parameterswzkcupdate.put("KFXH",
							Integer.parseInt(lisWZKC.get("KFXH") + ""));
					parameterswzkcupdate.put("JGID", jgid);
					parameterswzkcupdate.put("YKSL", yksl - wzsl);
					dao.doUpdate(
							"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and JGID=:JGID and KFXH=:KFXH",
							parameterswzkcupdate);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public static void addSuppliesYKSL(BaseDAO dao, Long sbxh, String brly,
			String jgid) {
		Map<String, Object> parametersxhmx = new HashMap<String, Object>();
		Map<String, Object> parameterswzkc = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupdate = new HashMap<String, Object>();
		parametersxhmx.put("JGID", jgid);
		parametersxhmx.put("MZXH", sbxh);
		parametersxhmx.put("BRLY", brly);
		try {
			// 根据fyxh取对应的wzxh
			List<Map<String, Object>> lisXHMX = dao
					.doQuery(
							"select KFXH as KFXH,KCXH as KCXH,WZSL as WZSL from WL_XHMX where MZXH=:MZXH and JGID=:JGID and BRLY=:BRLY",
							parametersxhmx);
			for (int i = 0; i < lisXHMX.size(); i++) {
				parameterswzkc.put("KCXH",
						Long.parseLong(lisXHMX.get(i).get("KCXH") + ""));
				parameterswzkc.put("KFXH",
						Integer.parseInt(lisXHMX.get(i).get("KFXH") + ""));
				parameterswzkc.put("JGID", jgid);
				double wzsl = Double.parseDouble(lisXHMX.get(i).get("WZSL")
						+ "");
				Map<String, Object> lisWZKC = dao
						.doLoad("select YKSL as YKSL,KCXH as KCXH,KFXH as KFXH from WL_WZKC where KCXH=:KCXH and JGID=:JGID and KFXH=:KFXH",
								parameterswzkc);
				for (int j = 0; j < lisWZKC.size(); j++) {// 第一个去做的金额
					double yksl = Double.parseDouble(lisWZKC.get("YKSL") + "");
					parameterswzkcupdate.put("KCXH",
							Long.parseLong(lisWZKC.get("KCXH") + ""));
					parameterswzkcupdate.put("KFXH",
							Integer.parseInt(lisWZKC.get("KFXH") + ""));
					parameterswzkcupdate.put("JGID", jgid);
					parameterswzkcupdate.put("YKSL", yksl + wzsl);
					dao.doUpdate(
							"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and JGID=:JGID and KFXH=:KFXH",
							parameterswzkcupdate);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public static void updateXHMXZT(BaseDAO dao, Long sbxh, String brly,
			String jgid) {
		Map<String, Object> parametersxhmx = new HashMap<String, Object>();
		parametersxhmx.put("JGID", jgid);
		parametersxhmx.put("MZXH", sbxh);
		parametersxhmx.put("BRLY", brly);
		parametersxhmx.put("ZTBZ", 0);
		try {
			dao.doUpdate(
					"update WL_XHMX set ZTBZ=:ZTBZ where MZXH=:MZXH and JGID=:JGID and BRLY=:BRLY",
					parametersxhmx);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public static String daysBetween(String smdate, String bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = Math.abs(time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days)) + "";
	}
	/**
	 * xml格式字符串转成map(檢查接口專用)
	 * 只解析3层结构
	 * @author caijy
	 * @createDate 2017-3-7
	 * @description 
	 * @updateInfo
	 * @param xml
	 * @return key 大寫
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String,Object> xml2map_jc(String xml) throws Exception{
		 Map<String,Object> map = new HashMap<String,Object>();
	        Document doc = null;
	            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
	            Element rootElt = doc.getRootElement(); // 获取根节点
				Iterator iter =rootElt.elementIterator();
	            //Iterator iter = rootElt.elementIterator("head"); // 获取根节点下的子节点head
	            while (iter.hasNext()) {
	                Element recordEle = (Element) iter.next();
	                if(recordEle.isTextOnly()){
	                	map.put(recordEle.getName().toUpperCase(), recordEle.getText());
	                }else{
	                	Iterator iter_c=recordEle.elementIterator();
	                	Map<String,Object> map_c=new HashMap<String, Object>();
	                	while(iter_c.hasNext()){
	                		 Element recordEle_c = (Element) iter_c.next();
	                		if(recordEle_c.isTextOnly()){
	                			map_c.put(recordEle_c.getName().toUpperCase(), recordEle_c.getText());
	    	                }
	                	}
	                	map.put(recordEle.getName().toUpperCase(), map_c);
	                }
	            }   
	        return map;
	}
	
	/**
	 * map轉成xml格式的字符串(檢查專用)
	 * @author caijy
	 * @createDate 2017-3-7
	 * @description 
	 * @updateInfo
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String map2xml_jc(Map<String, Object> map){
		StringBuffer s=new StringBuffer();
		for(String key:map.keySet()){
			
			if(map.get(key)!=null&&map.get(key) instanceof  Map){
				s.append("<").append(key).append(">");
				s.append(map2xml_jc((Map<String, Object>)map.get(key)));
				s.append("</").append(key).append(">");
			}else if(map.get(key)!=null&&map.get(key) instanceof  List){
				for(Map<String,Object> m:(List<Map<String,Object>>)map.get(key)){
					s.append("<").append(key).append(">");
					s.append(map2xml_jc(m));
					s.append("</").append(key).append(">");
				}
			}
			else{
				s.append("<").append(key).append(">");
				s.append(map.get(key));
				s.append("</").append(key).append(">");
			}
			
		}
		return s.toString();
	}
	
	public static String getZDDMforKSDM(long ksdm,BaseDAO dao) throws ModelDataOperationException {
		try {
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("KSDM", ksdm);
			Map<String,Object> zddm_map = dao.doLoad("select nvl(ZDDM,'0') as ZDDM from SYS_Office where ID=:KSDM", parameters);
			if("0".equals(zddm_map.get("ZDDM"))){
				throw new ModelDataOperationException("当前科室的站点还未维护,请先维护!");
			}
			return zddm_map.get("ZDDM").toString();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取当前科室的站点代码失败!");
		}
	}
}
