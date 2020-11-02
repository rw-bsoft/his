package phis.application.sup.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 消耗明细Model
 */
public class ConsumptionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ConsumptionModel.class);

	public ConsumptionModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doCreateDocument(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("body") != null) {
			body = (Map<String, Object>) req.get("body");
		}
		List<Map<String, Object>> records = (List<Map<String, Object>>) body
				.get("records");
		Integer SCLX = Integer.parseInt(body.get("sclx") + "");
		Map<String, Object> ck01 = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			parameters.put("KFXH",
					Integer.parseInt(user.getProperty("treasuryId") + ""));
		} else {
			parameters.put("KFXH", 0);
		}
		ck01.put("KFXH", user.getProperty("treasuryId"));
		ck01.put("CKRQ", new Date());
		ck01.put("ZDRQ", new Date());
		ck01.put("ZDGH", user.getUserId());
		ck01.put("JGID", user.getManageUnit().getId() + "");
		ck01.put("DJLX", 2);
		ck01.put("CKFS", 1);
		ck01.put("DJZT", 0);
		ck01.put("ZBLB", 0);
		try {
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				if (record.get("KCXH") != null && record.get("KCXH") != ""
						&& Long.parseLong(record.get("KCXH") + "") > 0) {
					record.put("YKBZ", 1);
					Map<String, Object> parametersWZKC = new HashMap<String, Object>();
					parametersWZKC.put("KCXH",
							Long.parseLong(record.get("KCXH") + ""));
					parametersWZKC.put("KFXH",
							parseInt(record.get("KFXH") + ""));
					Map<String, Object> map_wzkc = dao
							.doLoad("select CJXH as CJXH,WZPH as WZPH,MJPH as MJPH,SCRQ as SCRQ,SXRQ as SXRQ,WZJG as WZJG,LSJG as LSJG"
									+ " from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parametersWZKC);
					record.put("WZJG",
							Double.parseDouble(map_wzkc.get("WZJG") + ""));
					record.put(
							"WZJE",
							Double.parseDouble(map_wzkc.get("WZJG") + "")
									* Double.parseDouble(record.get("WZSL")
											+ ""));
					if (map_wzkc.get("LSJG") != null) {
						record.put("LSJG",
								Double.parseDouble(map_wzkc.get("LSJG") + ""));
						record.put(
								"LSJE",
								Double.parseDouble(map_wzkc.get("LSJG") + "")
										* Double.parseDouble(record.get("WZSL")
												+ ""));
					}
					record.put("CJXH",
							Long.parseLong(map_wzkc.get("CJXH") + ""));
					if (map_wzkc.get("WZPH") != null) {
						record.put("WZPH", map_wzkc.get("WZPH") + "");
					} else {
						record.put("WZPH", "");
					}
					if (map_wzkc.get("SCRQ") != null) {
						record.put("SCRQ",
								sdfTime.parse(map_wzkc.get("SCRQ") + ""));
					} else {
						record.put("SCRQ", null);
					}
					if (map_wzkc.get("SXRQ") != null) {
						record.put("SXRQ",
								sdfTime.parse(map_wzkc.get("SXRQ") + ""));
					} else {
						record.put("SXRQ", null);
					}
					if (map_wzkc.get("MJPH") != null) {
						record.put("MJPH", map_wzkc.get("MJPH") + "");
					} else {
						record.put("MJPH", "");
					}

				} else {
					record.put("KCXH", 0);
					record.put("YKBZ", 0);
				}
			}

			Boolean hasPositive = false;
			Boolean hasNegative = false;

			if (SCLX.equals(0)) {
				// 按病人
				List<Map<String, Object>> list_lzfs = new ArrayList<Map<String, Object>>();
				Map<String, Object> map_ck01 = new HashMap<String, Object>();
				Map<String, Object> parametersXHMX = new HashMap<String, Object>();
				for (int i = 0; i < records.size(); i++) {
					Map<String, Object> record = records.get(i);
					if (Double.parseDouble(record.get("WZSL") + "") > 0) {
						list_lzfs = dao
								.doQuery(
										"select FSXH as FSXH from WL_LZFS where YWLB=-1 and KFXH=:KFXH and DJLX='CK'",
										parameters);
						ck01.put(
								"LZFS",
								Long.parseLong(list_lzfs.get(0).get("FSXH")
										+ ""));

					} else if (Double.parseDouble(record.get("WZSL") + "") < 0) {
						list_lzfs = dao
								.doQuery(
										"select FSXH as FSXH from WL_LZFS where YWLB=1 and KFXH=:KFXH and DJLX='CK'",
										parameters);
						ck01.put(
								"LZFS",
								Long.parseLong(list_lzfs.get(0).get("FSXH")
										+ ""));
						ck01.put("THDJ", -1L);
					}
					// 如果按病人生成，则单据备注默认为病人号＋病人姓名
					ck01.put("DJBZ",
							"【" + record.get("BRHM") + "" + record.get("BRXM")
									+ "】");
					// 是按病人方式时，应该给科室代码 (KSDM)赋值，同时默认ckfs为3
					ck01.put("CKFS", 3);
					ck01.put("KSDM", Long.parseLong(record.get("KSDM") + ""));

					map_ck01 = dao.doSave("create", BSPHISEntryNames.WL_CK01,
							ck01, false);
					record.put("DJXH",
							Long.parseLong(map_ck01.get("DJXH") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, record,
							false);
					// 把选中的记录状态标识(ZTBZ)设置为1,DJXH为
					parametersXHMX.put("DJXH",
							Long.parseLong(map_ck01.get("DJXH") + ""));
					parametersXHMX.put("JLXH",
							Long.parseLong(record.get("JLXH") + ""));
					dao.doUpdate(
							"update WL_XHMX set ZTBZ=1,DJXH=:DJXH where JLXH=:JLXH",
							parametersXHMX);
				}
			} else if (SCLX.equals(1)) {
			} else if (SCLX.equals(2)) {
				// 按科室
				String jgid = user.getManageUnit().getId();
				Map<Long, List<Map<String, Object>>> map_records = new HashMap<Long, List<Map<String, Object>>>();

				// 生成出库单时要判断为同一个病人 2013/7/30
				Map<String, Object> bridMap = new HashMap<String, Object>();
				for (Map<String, Object> record : records) {
					bridMap.put(record.get("BRID") + "", record.get("BRID")
							+ "");
				}
				if (bridMap.size() > 1) {
					throw new RuntimeException("所选项目包含两个以上的病人，无法生成");
				}

				for (int i = 0; i < records.size(); i++) {
					Map<String, Object> record = records.get(i);
					Map<String, Object> parametersMZXH = new HashMap<String, Object>();
					String MZWPJFBZ = ParameterUtil.getParameter(jgid,
							"MZWPJFBZ", "0", "物品计费标志。1启用，0不启用", ctx);
					String WZSFXMJG = ParameterUtil.getParameter(jgid,
							"WZSFXMJG", "0", "物品收费项目价格", ctx);
					String WZSFXMJGZY = ParameterUtil.getParameter(jgid,
							"WZSFXMJGZY", "0", "物品收费项目价格住院", ctx);
					String WPJFBZ = ParameterUtil.getParameter(jgid, "WPJFBZ",
							"0", "物品计费标志", ctx);
					int brly = parseInt(record.get("BRLY"));
					long mzxh = parseLong(record.get("MZXH"));
					parametersMZXH.put("MZXH", mzxh);
					parametersMZXH.put("JGID", user.getManageUnit().getId()
							+ "");
					if (brly == 1) {
						if (MZWPJFBZ.equals("1") && WZSFXMJG.equals("0")) {
							Map<String, Object> resMap = dao
									.doLoad("select sum(ZFPB) as ZFPB from MS_YJ01 where MZXH=:MZXH and JGID=:JGID",
											parametersMZXH);
							if (resMap != null
									&& parseInt(resMap.get("ZFPB")) > 0) {
								throw new RuntimeException("所选的第" + (i + 1)
										+ "条数据病人已经退费，不允许产生出库单!");
							}
						} else if (MZWPJFBZ.equals("1") && WZSFXMJG.equals("1")) {
							Map<String, Object> resMap = dao
									.doLoad("select ZFPB as ZFPB from MS_YJ01 "
											+ " where YJXH=(select max(YJXH) as YJXH from MS_YJ02 where SBXH =:MZXH) and JGID=:JGID",
											parametersMZXH);
							if (resMap != null
									&& parseInt(resMap.get("ZFPB")) == 1) {
								throw new RuntimeException("所选的第" + (i + 1)
										+ "条数据病人已经退费，不允许产生出库单!");
							}
						}
					} else if (brly == 2) {
						if (WPJFBZ.equals("1") && WZSFXMJGZY.equals("1")) {
							Map<String, Object> resMap = dao
									.doLoad("select sum(FYSL) as NUM from ZY_FYMX where YZXH=:MZXH and JGID=:JGID",
											parametersMZXH);
							if (resMap.get("NUM") != null
									&& parseDouble(resMap.get("NUM")) == 0) {
								throw new RuntimeException("所选的第" + (i + 1)
										+ "条数据病人已经退费，不允许产生出库单!");
							}
						}
					}

					// 得到科室代码，并以此分组
					Long KSDM = Long.parseLong(record.get("KSDM") + "");
					if (map_records.containsKey(KSDM)) {
						map_records.get(KSDM).add(record);
					} else {
						map_records.put(KSDM,
								new ArrayList<Map<String, Object>>());
						map_records.get(KSDM).add(record);
					}
				}
				List<Long> djxh_list = new ArrayList<Long>();
				// 遍历map_records
				Set<Long> key = map_records.keySet();
				for (Iterator<Long> it = key.iterator(); it.hasNext();) {
					Long brid = it.next();
					List<Map<String, Object>> classified_records = map_records
							.get(brid);
					// 下面与按汇总的方法一样
					createDocumentByHZ(classified_records, hasPositive,
							hasNegative, parameters, ck01, SCLX, djxh_list);

				}
				doAllCommitByDJXH(djxh_list, req, res, ctx);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "生成失败");
		} catch (PersistentDataOperationException e) {
			logger.error("生成失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "生成失败");
		} catch (ParseException e) {
			logger.error("生成失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "生成失败");
		}
	}

	public void createDocumentByHZ(List<Map<String, Object>> records,
			Boolean hasPositive, Boolean hasNegative,
			Map<String, Object> parameters, Map<String, Object> ck01,
			Integer SCLX, List<Long> djxh_list) throws ValidateException,
			PersistentDataOperationException {

		// 按汇总
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			if (Double.parseDouble(record.get("WZSL") + "") > 0) {
				hasPositive = true;
			}
			if (Double.parseDouble(record.get("WZSL") + "") < 0) {
				hasNegative = true;
			}
		}
		List<Map<String, Object>> list_lzfs = new ArrayList<Map<String, Object>>();
		Map<String, Object> map_ck01 = new HashMap<String, Object>();
		Map<String, Object> parametersXHMX = new HashMap<String, Object>();
		// 数量大于0的
		if (hasPositive) {
			list_lzfs = dao
					.doQuery(
							"select FSXH as FSXH from WL_LZFS where YWLB=-1 and KFXH=:KFXH and DJLX='CK'",
							parameters);
			ck01.put("LZFS", Long.parseLong(list_lzfs.get(0).get("FSXH") + ""));
			// 如果按病人生成，则单据备注默认为病人号＋病人姓名
			if (SCLX.equals(0)) {
				ck01.put("DJBZ", "【" + records.get(0).get("BRHM") + ""
						+ records.get(0).get("BRXM") + "】");
			}

			if (SCLX.equals(2)) {
				// 如果按科室生成，则单据备注默认为病人号＋病人姓名
				// ck01.put("DJBZ", "【" + records.get(0).get("BRHM") + ""
				// + records.get(0).get("BRXM") + "】");
				// 是按科室方式时，应该给科室代码 (KSDM)赋值，同时默认ckfs为3
				ck01.put("CKFS", 1);
				ck01.put("KSDM",
						Long.parseLong(records.get(0).get("KSDM") + ""));
			}

			map_ck01 = dao.doSave("create", BSPHISEntryNames.WL_CK01, ck01,
					false);
			djxh_list.add(parseLong(map_ck01.get("DJXH")));
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				if (Double.parseDouble(record.get("WZSL") + "") > 0) {
					record.put("DJXH",
							Long.parseLong(map_ck01.get("DJXH") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, record,
							false);
					// 把选中的记录状态标识(ZTBZ)设置为1,DJXH为
					parametersXHMX.put("DJXH",
							Long.parseLong(map_ck01.get("DJXH") + ""));
					parametersXHMX.put("JLXH",
							Long.parseLong(record.get("JLXH") + ""));
					dao.doUpdate(
							"update WL_XHMX set ZTBZ=1,DJXH=:DJXH where JLXH=:JLXH",
							parametersXHMX);
				}
			}
		}
		// 数量小于0的
		if (hasNegative) {
			list_lzfs.clear();
			map_ck01.clear();
			list_lzfs = dao
					.doQuery(
							"select FSXH as FSXH from WL_LZFS where YWLB=1 and KFXH=:KFXH and DJLX='CK'",
							parameters);
			ck01.put("LZFS", Long.parseLong(list_lzfs.get(0).get("FSXH") + ""));
			ck01.put("THDJ", -1L);
			// 如果按病人生成，则单据备注默认为病人号＋病人姓名
			if (SCLX.equals(0)) {
				ck01.put("DJBZ", "【" + records.get(0).get("BRHM") + ""
						+ records.get(0).get("BRXM") + "】");
			}
			if (SCLX.equals(2)) {
				// 如果按科室生成，则单据备注默认为病人号＋病人姓名
				// ck01.put("DJBZ", "【" + records.get(0).get("BRHM") + ""
				// + records.get(0).get("BRXM") + "】");
				// 是按科室方式时，应该给科室代码 (KSDM)赋值，同时默认ckfs为3
				ck01.put("CKFS", 1);
				ck01.put("KSDM",
						Long.parseLong(records.get(0).get("KSDM") + ""));
			}

			map_ck01 = dao.doSave("create", BSPHISEntryNames.WL_CK01, ck01,
					false);
			djxh_list.add(parseLong(map_ck01.get("DJXH")));
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				if (Double.parseDouble(record.get("WZSL") + "") < 0) {
					record.put("DJXH",
							Long.parseLong(map_ck01.get("DJXH") + ""));
					record.put("WZSL",
							Double.parseDouble((record.get("WZSL") + "")
									.substring(1)));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, record,
							false);
					// 把选中的记录状态标识(ZTBZ)设置为1,DJXH为
					parametersXHMX.put("DJXH",
							Long.parseLong(map_ck01.get("DJXH") + ""));
					parametersXHMX.put("JLXH",
							Long.parseLong(record.get("JLXH") + ""));
					dao.doUpdate(
							"update WL_XHMX set ZTBZ=1,DJXH=:DJXH where JLXH=:JLXH",
							parametersXHMX);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> ck = (Map<String, Object>) body.get("WL_CK01");
		List<Map<String, Object>> _lzfs = BSPHISUtil.uf_get_lzfs(
				Long.parseLong(ck.get("LZFS") + ""), dao, ctx);
		if (Long.parseLong(_lzfs.get(0).get("TSBZ") + "") != 1) {
			if (ck.get("KSDM") == null || ck.get("KSDM") == ""
					|| ck.get("JBGH") == null || ck.get("JBGH") == "") {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "出库科室和经办人不能为空");
			}
		}
		ck.put("KFXH", user.getProperty("treasuryId"));
		ck.put("ZDRQ", new Date());
		ck.put("ZDGH", user.getUserId());
		ck.put("JGID", user.getManageUnit().getId() + "");

		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		double djje = 0.0;
		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			if (mat.get("WZJE") == null || mat.get("WZJE") == "") {
				continue;
			}
			djje += Double.parseDouble(mat.get("WZJE").toString());
		}
		ck.put("DJJE", djje);
		try {
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,
						BSPHISEntryNames.WL_CK01, ck, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", DJXH);
					dao.doSave("create", BSPHISEntryNames.WL_CK02, mat, false);
				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_CK01, ck, false);
				dao.removeByFieldValue("DJXH", ck.get("DJXH"),
						BSPHISEntryNames.WL_CK02);
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", ck.get("DJXH"));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, mat, false);
				}
			}
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void getZblbByKfxh(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Integer kfxh = (Integer) body.get("KFXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", Long.parseLong(kfxh.toString()));
		try {
			List<Map<String, Object>> list = dao.doQuery(
					"from WL_KFXX where KFXH=:KFXH", parameters);
			String kfzb = (String) list.get(0).get("KFZB");
			parameters.clear();
			list = dao.doSqlQuery(
					"select ZBLB,ZBMC from WL_ZBLB where ZBLB in(" + kfzb
							+ ") order by ZBLB", parameters);
			res.put("list", list);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(BSPHISEntryNames.WL_CK01,
					Long.parseLong(body.get("DJXH") + ""));
			if (map.size() == 0) {
				return;
			}
			res.put("djzt", map.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doGetCjxhByWzxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Integer wzxh = (Integer) body.get("WZXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", Long.parseLong(wzxh.toString()));
		try {
			Map<String, Object> map = dao.doLoad(
					"from WL_WZCJ where WZXH=:WZXH", parameters);
			if (map.size() == 0) {
				return;
			}
			res.put("cjxh", map.get("CJXH"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doVerify(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> parameterswzkc = new HashMap<String, Object>();
		Map<String, Object> parameterswzxx = new HashMap<String, Object>();
		Map<String, Object> parameterskcxh = new HashMap<String, Object>();
		Map<String, Object> parametersck02upd = new HashMap<String, Object>();
		Map<String, Object> parametersck02ins = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupd = new HashMap<String, Object>();
		Map<String, Object> parametersykslupd = new HashMap<String, Object>();
		Map<String, Object> parameterssetck02glxh = new HashMap<String, Object>();
		Map<String, Object> parametersck01upd = new HashMap<String, Object>();
		Map<String, Object> parameterszczbupd = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		UserRoleToken user =UserRoleToken.getCurrent();
		double sysl = 0.00;
		int shsign = 0;
		try {
			List<Map<String, Object>> ck02 = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ from "
									+ " WL_CK02 a,"
									+ " WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH=0 and (a.YKBZ<>1 or a.YKBZ=null)",
							parameters);
			List<Map<String, Object>> ck02ph = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ,a.YKBZ as YKBZ from "
									+ " WL_CK02 a,"
									+ " WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH<>0 and (a.YKBZ<>1 or a.YKBZ=null)",
							parameters);
			for (int i = 0; i < ck02.size(); i++) {
				parameterswzkc.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				parameterswzkc.put("KFXH",
						Integer.parseInt(ck02.get(i).get("KFXH") + ""));
				parameterswzxx.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				Map<String, Object> wzckwzsl = dao
						.doLoad("select sum(WZSL-YKSL) as KYSL from WL_WZKC where KFXH=:KFXH and WZXH=:WZXH",
								parameterswzkc);
				if (wzckwzsl.get("KYSL") != null) {
					if (Double.parseDouble(ck02.get(i).get("WZSL") + "") > Double
							.parseDouble(wzckwzsl.get("KYSL") + "")) {
						Map<String, Object> wzzdwzmc = dao
								.doLoad("select WZMC as WZMC from WL_WZZD where WZXH=:WZXH",
										parameterswzxx);
						res.put("WZMC", wzzdwzmc.get("WZMC"));
						shsign = 1;
					}
				} else {
					Map<String, Object> wzzdwzmc = dao
							.doLoad("select WZMC as WZMC from WL_WZZD where WZXH=:WZXH",
									parameterswzxx);
					res.put("WZMC", wzzdwzmc.get("WZMC"));
					shsign = 1;
				}
			}
			for (int i = 0; i < ck02ph.size(); i++) {
				if (ck02ph.get(i).get("THDJ") == null
						&& (ck02ph.get(i).get("YKBZ") == null || !ck02ph.get(i)
								.get("YKBZ").equals("1"))) {
					parameterswzkc.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					parameterswzkc.put("KFXH",
							Integer.parseInt(ck02ph.get(i).get("KFXH") + ""));
					parameterskcxh.put("KCXH",
							Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
					parameterskcxh.put("KFXH",
							Integer.parseInt(ck02ph.get(i).get("KFXH") + ""));
					parameterswzxx.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					Map<String, Object> wzckwzsl = dao
							.doLoad("select (WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterskcxh);
					if (wzckwzsl.get("KYSL") != null) {
						if (Double.parseDouble(ck02ph.get(i).get("WZSL") + "") > Double
								.parseDouble(wzckwzsl.get("KYSL") + "")) {
							Map<String, Object> wzzdwzmc = dao
									.doLoad("select WZMC as WZMC from WL_WZZD where WZXH=:WZXH",
											parameterswzxx);
							res.put("WZMC", wzzdwzmc.get("WZMC"));
							shsign = 1;
						}
					} else {
						Map<String, Object> wzzdwzmc = dao
								.doLoad("select WZMC as WZMC from WL_WZZD where WZXH=:WZXH",
										parameterswzxx);
						res.put("WZMC", wzzdwzmc.get("WZMC"));
						shsign = 1;
					}
				}
			}
			if (shsign == 0) {
				for (int i = 0; i < ck02.size(); i++) {
					parameterswzkc.put("WZXH",
							Long.parseLong(ck02.get(i).get("WZXH") + ""));
					parameterswzkc.put("KFXH",
							Integer.parseInt(ck02.get(i).get("KFXH") + ""));
					sysl = Double.parseDouble(ck02.get(i).get("WZSL") + "");
					List<Map<String, Object>> wzkc = dao
							.doQuery(
									"select JLXH as JLXH,KCXH as KCXH,WZJG as WZJG,LSJG as LSJG,CJXH as CJXH,WZPH as WZPH,str(SCRQ,'YYYY-MM-DD HH24:MI:SS') as SCRQ,str(SXRQ,'YYYY-MM-DD HH24:MI:SS') as SXRQ,MJPH as MJPH,(WZSL-YKSL) as KYSL,YKSL as YKSL from "
											+ "WL_WZKC where WZXH=:WZXH and KFXH=:KFXH and WZSL<>YKSL order by KCXH",
									parameterswzkc);

					for (int j = 0; j < wzkc.size(); j++) {
						if (sysl <= Double.parseDouble(wzkc.get(j).get("KYSL")
								+ "")) {
							if (sysl == Double.parseDouble(ck02.get(i).get(
									"WZSL")
									+ "")) {
								parametersck02upd.put("WZSL", sysl);
								parametersck02upd.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02upd.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02upd.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* Double.parseDouble(ck02
														.get(i).get("WZSL")
														+ ""));
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02upd.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02upd.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* Double.parseDouble(ck02
															.get(i).get("WZSL")
															+ ""));
								}
								parametersck02upd.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02upd.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02upd.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02upd.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02upd.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02upd.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02upd.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02upd.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02upd.put("MJPH", "");
								}
								parametersck02upd.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ sysl);
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								dao.doUpdate(
										"update WL_CK02 set WZSL=:WZSL,KCXH=:KCXH,WZJG=:WZJG,WZJE=:WZJE,LSJG=:LSJG,LSJE=:LSJE,CJXH=:CJXH,WZPH=:WZPH,SCRQ=:SCRQ,SXRQ=:SXRQ,MJPH=:MJPH where JLXH=:JLXH",
										parametersck02upd);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
							} else {
								parametersck02ins.put("DJXH",
										Long.parseLong(body.get("DJXH") + ""));
								parametersck02ins.put(
										"WZXH",
										Long.parseLong(ck02.get(i).get("WZXH")
												+ ""));
								parametersck02ins.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								parametersck02ins.put("WZSL", sysl);
								parametersck02ins.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02ins.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* sysl);
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02ins.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02ins.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* sysl);
								}
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02ins.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02ins.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02ins.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02ins.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02ins.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02ins.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02ins.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02ins.put("MJPH", "");
								}
								parametersck02ins.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02ins.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ sysl);
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								Map<String, Object> jlxhMap = dao.doSave(
										"create", BSPHISEntryNames.WL_CK02,
										parametersck02ins, false);
								Long jlxh = Long.parseLong(jlxhMap.get("JLXH")
										+ "");
								parameterssetck02glxh.put("GLXH", jlxh);
								parameterssetck02glxh.put("JLXH", jlxh);
								dao.doUpdate(
										"update WL_CK02 set GLXH=:GLXH where JLXH=:JLXH",
										parameterssetck02glxh);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
							}
							break;
						} else {
							if (j == 0) {
								parametersck02upd.put(
										"WZSL",
										Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ ""));
								parametersck02upd.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02upd.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02upd.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02upd.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02upd.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* Double.parseDouble(wzkc
															.get(j).get("KYSL")
															+ ""));
								}
								parametersck02upd.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02upd.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02upd.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02upd.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02upd.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02upd.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02upd.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02upd.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02upd.put("MJPH", "");
								}
								parametersck02upd.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								dao.doUpdate(
										"update WL_CK02 set WZSL=:WZSL,KCXH=:KCXH,WZJG=:WZJG,WZJE=:WZJE,LSJG=:LSJG,LSJE=:LSJE,CJXH=:CJXH,WZPH=:WZPH,SCRQ=:SCRQ,SXRQ=:SXRQ,MJPH=:MJPH where JLXH=:JLXH",
										parametersck02upd);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
								sysl = sysl
										- Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ "");
							} else {
								parametersck02ins.put("DJXH",
										Long.parseLong(body.get("DJXH") + ""));
								parametersck02ins.put(
										"WZXH",
										Long.parseLong(ck02.get(i).get("WZXH")
												+ ""));
								parametersck02ins.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								parametersck02ins.put(
										"WZSL",
										Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ ""));
								parametersck02ins.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02ins.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02ins.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02ins.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* Double.parseDouble(wzkc
															.get(j).get("KYSL")
															+ ""));
								}
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02ins.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02ins.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02ins.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02ins.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02ins.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02ins.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02ins.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02ins.put("MJPH", "");
								}
								parametersck02ins.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02ins.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								Map<String, Object> jlxhMap = dao.doSave(
										"create", BSPHISEntryNames.WL_CK02,
										parametersck02ins, false);
								Long jlxh = Long.parseLong(jlxhMap.get("JLXH")
										+ "");
								parameterssetck02glxh.put("GLXH", jlxh);
								parameterssetck02glxh.put("JLXH", jlxh);
								dao.doUpdate(
										"update WL_CK02 set GLXH=:GLXH where JLXH=:JLXH",
										parameterssetck02glxh);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
								sysl = sysl
										- Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ "");
							}
						}
					}
				}
			}
			if (shsign == 0) {
				for (int i = 0; i < ck02ph.size(); i++) {
					if (ck02ph.get(i).get("YKBZ") != null
							&& ck02ph.get(i).get("YKBZ").equals("1")) {
						continue;
					}
					int glfs = 0;
					parameterswzxx.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					Map<String, Object> wzzdglfs = dao
							.doLoad("select GLFS as GLFS from WL_WZZD where WZXH=:WZXH",
									parameterswzxx);
					if (wzzdglfs.get("GLFS") != null) {
						glfs = Integer.parseInt(wzzdglfs.get("GLFS") + "");
					}
					if (glfs == 3) {
						if (ck02ph.get(i).get("ZBXH") != null) {
							parameterszczbupd.put(
									"ZBXH",
									Long.parseLong(ck02ph.get(i).get("ZBXH")
											+ ""));
							dao.doUpdate(
									"update WL_ZCZB set CLBZ=1 where ZBXH=:ZBXH",
									parameterszczbupd);
						}
					} else {
						parameterskcxh.put("KCXH",
								Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
						parameterskcxh.put("KFXH", Integer.parseInt(ck02ph.get(
								i).get("KFXH")
								+ ""));
						Map<String, Object> wzckwzsl = dao
								.doLoad("select (WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
										parameterskcxh);
						if (ck02ph.get(i).get("THDJ") == null) {
							parametersykslupd.put(
									"KCXH",
									Long.parseLong(ck02ph.get(i).get("KCXH")
											+ ""));
							parametersykslupd.put(
									"KFXH",
									Integer.parseInt(ck02ph.get(i).get("KFXH")
											+ ""));
							parametersykslupd.put(
									"YKSL",
									Double.parseDouble(wzckwzsl.get("YKSL")
											+ "")
											+ Double.parseDouble(ck02ph.get(i)
													.get("WZSL") + ""));
							dao.doUpdate(
									"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
									parametersykslupd);
						} else {
							if (glfs == 2) {
								parametersykslupd.put(
										"KCXH",
										Long.parseLong(ck02ph.get(i)
												.get("KCXH") + ""));
								parametersykslupd.put(
										"KFXH",
										Integer.parseInt(ck02ph.get(i).get(
												"KFXH")
												+ ""));
								parametersykslupd.put(
										"YKSL",
										Double.parseDouble(wzckwzsl.get("YKSL")
												+ "")
												+ Double.parseDouble(ck02ph
														.get(i).get("WZSL")
														+ ""));
								dao.doUpdate(
										"update WL_KSZC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
										parametersykslupd);
							}
						}
					}
				}
			}
			if (shsign == 0) {
				if (ck02.size() > 0 || ck02ph.size() >= 0) {
					String uid = user.getUserId();
					parametersck01upd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck01upd.put("SHGH", uid);
					parametersck01upd.put("SHRQ", new Date());
					parametersck01upd.put("LZDH", getLzdh());
					dao.doUpdate(
							"update WL_CK01 set LZDH=:LZDH,DJZT=1,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH",
							parametersck01upd);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void doIsEnoughInventory(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select WZSL as WZSL from WL_WZKC where KCXH=:KCXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				parameters.put("KCXH", Integer.parseInt(mat.get("KCXH") + ""));
				Map<String, Object> map = dao
						.doLoad(hql.toString(), parameters);
				Double kcsl = (Double) map.get("WZSL");
				Integer wzsl = (Integer) (mat.get("WZSL"));
				if (wzsl > kcsl) {
					res.put("isEnoughInventory", false);
					return;
				}
			}
			res.put("isEnoughInventory", true);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doCancelVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupd = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcyksl = new HashMap<String, Object>();
		Map<String, Object> parametersck02wzsl = new HashMap<String, Object>();
		Map<String, Object> parametersck02upd = new HashMap<String, Object>();
		Map<String, Object> parametersck02nowzslupd = new HashMap<String, Object>();
		Map<String, Object> parameterswzxx = new HashMap<String, Object>();
		Map<String, Object> parameterszczbupd = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			long kfxh = Integer
					.parseInt(user.getProperty("treasuryId") == null ? "0"
							: user.getProperty("treasuryId") + "");
			String jgid = user.getManageUnit().getId();// 锟矫伙拷锟侥伙拷ID
			String ckwzjslx = ParameterUtil.getParameter(jgid, "CKWZJSLX"
					+ kfxh, "0", "物资出库物资检索类型 0.按库存检索1.按批次检索", ctx);
			List<Map<String, Object>> ck02 = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,a.GLXH as GLXH,b.KFXH as KFXH,b.THDJ as THDJ,a.YKBZ as YKBZ from "
									+ " WL_CK02 a,"
									+ " WL_CK01 WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.YKBZ<>1",
							parameters);
			// 不管是按批次还是库存,都要更新WL_WZKC表里的YKSL
			for (int i = 0; i < ck02.size(); i++) {
				if (ck02.get(i).get("YKBZ") != null
						&& ck02.get(i).get("YKBZ").equals("1")) {
					continue;
				}
				int glfs = 0;
				parameterswzxx.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				Map<String, Object> wzzdglfs = dao.doLoad(
						"select GLFS as GLFS from WL_WZZD where WZXH=:WZXH",
						parameterswzxx);
				if (wzzdglfs.get("GLFS") != null) {
					glfs = Integer.parseInt(wzzdglfs.get("GLFS") + "");
				}

				if (glfs == 3) {
					if (ck02.get(i).get("ZBXH") != null) {
						parameterszczbupd.put("ZBXH",
								Long.parseLong(ck02.get(i).get("ZBXH") + ""));
						dao.doUpdate(
								"update WL_ZCZB set CLBZ=1 where ZBXH=:ZBXH",
								parameterszczbupd);
					}
				} else {
					parameterswzkcyksl.put("KCXH",
							Long.parseLong(ck02.get(i).get("KCXH") + ""));
					parameterswzkcyksl.put("KFXH",
							Integer.parseInt(ck02.get(i).get("KFXH") + ""));
					Map<String, Object> wzkcyksl = dao
							.doLoad("select YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterswzkcyksl);
					if (ck02.get(i).get("THDJ") == null) {
						parameterswzkcupd.put("KCXH",
								Long.parseLong(ck02.get(i).get("KCXH") + ""));
						parameterswzkcupd.put("KFXH",
								Integer.parseInt(ck02.get(i).get("KFXH") + ""));
						parameterswzkcupd.put(
								"YKSL",
								Double.parseDouble(wzkcyksl.get("YKSL") + "")
										- Double.parseDouble(ck02.get(i).get(
												"WZSL")
												+ ""));
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
								parameterswzkcupd);
					} else {
						if (glfs == 2) {
							parameterswzkcupd.put("KCXH", Long.parseLong(ck02
									.get(i).get("KCXH") + ""));
							parameterswzkcupd.put(
									"KFXH",
									Integer.parseInt(ck02.get(i).get("KFXH")
											+ ""));
							parameterswzkcupd.put(
									"YKSL",
									Double.parseDouble(wzkcyksl.get("YKSL")
											+ "")
											- Double.parseDouble(ck02.get(i)
													.get("WZSL") + ""));
							dao.doUpdate(
									"update WL_KSZC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
									parameterswzkcupd);
						}
					}
				}
			}
			if ("0".equals(ckwzjslx)) {
				List<Map<String, Object>> ck02wzsl = dao
						.doQuery(
								"select sum(WZSL) as WZSL,WZXH as WZXH from "
										+ "WL_CK02 where DJXH=:DJXH and GLXH is not null and YKBZ<>1 group by WZXH",
								parameters);
				for (int i = 0; i < ck02wzsl.size(); i++) {
					parametersck02wzsl.put("WZXH",
							Long.parseLong(ck02wzsl.get(i).get("WZXH") + ""));
					parametersck02wzsl.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					Map<String, Object> wzslMap = dao
							.doLoad("select WZSL as WZSL from WL_CK02"
									+ " where DJXH=:DJXH and WZXH=:WZXH and GLXH is null and YKBZ<>1",
									parametersck02wzsl);
					parametersck02upd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck02nowzslupd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck02upd.put("WZXH",
							Long.parseLong(ck02wzsl.get(i).get("WZXH") + ""));
					parametersck02upd
							.put("WZSL",
									Double.parseDouble(ck02wzsl.get(i).get(
											"WZSL")
											+ "")
											+ Double.parseDouble(wzslMap
													.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_CK02 set WZSL=:WZSL,KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0,WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and WZXH=:WZXH and GLXH is null and YKBZ<>1",
							parametersck02upd);
					dao.doUpdate(
							"update WL_CK02 set KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null and KCXH is not null and YKBZ<>1",
							parametersck02nowzslupd);
					dao.doUpdate(
							"delete from WL_CK02 where DJXH=:DJXH and WZXH=:WZXH and GLXH is not null and YKBZ<>1",
							parametersck02wzsl);
				}
				if (ck02wzsl.size() <= 0) {
					parametersck02nowzslupd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					dao.doUpdate(
							"update WL_CK02 set KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null and YKBZ<>1",
							parametersck02nowzslupd);
				}
			}
			dao.doUpdate(
					"update WL_CK01 set SHRQ='',SHGH='',DJZT=0 where DJXH=:DJXH",
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doUpdateWzcj(Map<String, Object> body, String op)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update WL_WZCJ set WZJG=:WZJG where WZXH=:WZXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				Integer wzjg = (Integer) mat.get("WZJG");
				parameters.put("WZJG", wzjg.doubleValue());
				Integer wzxh = (Integer) mat.get("WZXH");
				parameters.put("WZXH", wzxh.longValue());
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCommit(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> ck = (Map<String, Object>) body.get("WL_CK01");
		double djje = 0;

		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			mat.put("KFXH", ck.get("KFXH"));
			mat.put("ZBLB", ck.get("ZBLB"));
			mat.put("YWRQ", ck.get("CKRQ"));
			mat.put("KSDM", ck.get("KSDM"));
			mat.put("ZckS", ck.get("KSDM"));
			mat.put("GLXH", ck.get("THDJ"));
			if (ck.get("PDDJ") != null && ck.get("PDDJ") != ""
					&& Long.parseLong(ck.get("PDDJ") + "") > 0) {
				mat.put("YWFS", 1);
			} else {
				mat.put("YWFS", 0);
			}
		}

		if (BSPHISUtil.Uf_access(mats, Long.parseLong((ck.get("LZFS") + "")),
				dao, ctx)) {
			UserRoleToken  user = UserRoleToken.getCurrent();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", Long.parseLong(ck.get("DJXH").toString()));
			parameters.put("DJZT", 2);
			parameters.put("JZGH", user.getUserId());
			parameters.put("JZRQ", new Date());
			// 增加单据金额
			for (int i = 0; i < mats.size(); i++) {
				if (mats.get(i).get("WZJE") != null) {
					djje += Double.parseDouble(mats.get(i).get("WZJE") + "");
				}
			}
			parameters.put("DJJE", djje);
			try {
				dao.doUpdate(
						"update WL_CK01 set DJZT=:DJZT,JZGH=:JZGH,JZRQ=:JZRQ,DJJE=:DJJE where DJXH=:DJXH",
						parameters);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
			}

		}
	}

	@SuppressWarnings("unchecked")
	public void doDelete(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Integer> body = new ArrayList<Integer>();
		if (req.get("body") != null) {
			body = (List<Integer>) req.get("body");
		}

		try {
			for (Integer i : body) {
				Long DJXH = i.longValue();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", DJXH);
				dao.doRemove(DJXH, BSPHISEntryNames.WL_CK01);
				dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_CK02);
				dao.doUpdate(
						"update WL_XHMX set ZTBZ=0,DJXH=null where DJXH=:DJXH",
						parameters);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}

	public void doDeleteByDJXH(List<Long> djxh_list, Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		try {
			for (Long DJXH : djxh_list) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", DJXH);
				dao.doRemove(DJXH, BSPHISEntryNames.WL_CK01);
				dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_CK02);
				dao.doUpdate(
						"update WL_XHMX set ZTBZ=0,DJXH=null where DJXH=:DJXH",
						parameters);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doAllCommit(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Integer> body = new ArrayList<Integer>();
		if (req.get("body") != null) {
			body = (List<Integer>) req.get("body");
		}

		try {
			for (Integer i : body) {
				Long DJXH = i.longValue();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", DJXH);
				Map<String, Object> ck01 = dao.doLoad(BSPHISEntryNames.WL_CK01,
						DJXH);
				if (Integer.parseInt(ck01.get("DJZT") + "") == 0) {
					doVerify(ck01, ctx, res);
					if (res.containsKey("WZMC") && res.get("WZMC") != null) {
						return;
					}
				}

			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
			for (Integer i : body) {
				Long DJXH = i.longValue();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", DJXH);
				Map<String, Object> ck01 = dao.doLoad(BSPHISEntryNames.WL_CK01,
						DJXH);
				List<Map<String, Object>> ck02 = dao
						.doQuery(
								"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,"
										+ "a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.MJPH as MJPH,a.SCRQ as SCRQ,"
										+ "a.SXRQ as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,a.SLXH as SLXH,a.SLSL as SLSL,"
										+ "a.WFSL as WFSL,a.JHBZ as JHBZ,b.GLFS as GLFS from WL_CK02 a,WL_WZZD b where a.DJXH=:DJXH and a.WZXH=b.WZXH",
								parameters);
				Map<String, Object> parametersCommit = new HashMap<String, Object>();
				parametersCommit.put("WL_CK01", ck01);
				parametersCommit.put("WL_CK02", ck02);
				doCommit(parametersCommit, ctx);
			}
			ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("确认失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "确认失败");
		}
	}

	public void doAllCommitByDJXH(List<Long> djxh_list,
			Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			for (Long i : djxh_list) {
				Long DJXH = i.longValue();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", DJXH);
				Map<String, Object> ck01 = dao.doLoad(BSPHISEntryNames.WL_CK01,
						DJXH);
				if (Integer.parseInt(ck01.get("DJZT") + "") == 0) {
					doVerify(ck01, ctx, res);
					if (res.containsKey("WZMC") && res.get("WZMC") != null) {
						doDeleteByDJXH(djxh_list, req, res, ctx);
						return;
					}
				}

			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
			for (Long i : djxh_list) {
				Long DJXH = i.longValue();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", DJXH);
				Map<String, Object> ck01 = dao.doLoad(BSPHISEntryNames.WL_CK01,
						DJXH);
				List<Map<String, Object>> ck02 = dao
						.doQuery(
								"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,"
										+ "a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.MJPH as MJPH,a.SCRQ as SCRQ,"
										+ "a.SXRQ as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,a.SLXH as SLXH,a.SLSL as SLSL,"
										+ "a.WFSL as WFSL,a.JHBZ as JHBZ,b.GLFS as GLFS from WL_CK02 a,WL_WZZD b where a.DJXH=:DJXH and a.WZXH=b.WZXH",
								parameters);
				Map<String, Object> parametersCommit = new HashMap<String, Object>();
				parametersCommit.put("WL_CK01", ck01);
				parametersCommit.put("WL_CK02", ck02);
				if (Integer.parseInt(ck01.get("DJZT") + "") == 1) {
					doCommit(parametersCommit, ctx);
				}
			}
			ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("确认失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "确认失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCancellation(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("body") != null) {
			body = (Map<String, Object>) req.get("body");
		}
		List<Map<String, Object>> records = (List<Map<String, Object>>) body
				.get("records");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				// 判断是否存在未退费的
				Map<String, Object> parametersMZXH = new HashMap<String, Object>();
				String MZWPJFBZ = ParameterUtil.getParameter(jgid, "MZWPJFBZ",
						"0", "物品计费标志。1启用，0不启用", ctx);
				String WZSFXMJG = ParameterUtil.getParameter(jgid, "WZSFXMJG",
						"0", "物品收费项目价格", ctx);
				String WZSFXMJGZY = ParameterUtil.getParameter(jgid,
						"WZSFXMJGZY", "0", "物品收费项目价格住院", ctx);
				String WPJFBZ = ParameterUtil.getParameter(jgid, "WPJFBZ", "0",
						"物品计费标志", ctx);
				int brly = parseInt(record.get("BRLY"));
				long mzxh = parseLong(record.get("MZXH"));
				parametersMZXH.put("MZXH", mzxh);
				parametersMZXH.put("JGID", jgid);
				if (brly == 1) {
					if (MZWPJFBZ.equals("1") && WZSFXMJG.equals("0")) {
						Map<String, Object> resMap = dao
								.doLoad("select sum(ZFPB) as ZFPB from MS_YJ01 where MZXH=:MZXH and JGID=:JGID",
										parametersMZXH);
						if (resMap == null) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
						if (parseInt(resMap.get("ZFPB")) == 0) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
					} else if (MZWPJFBZ.equals("1") && WZSFXMJG.equals("1")) {
						Map<String, Object> resMap = dao
								.doLoad("select ZFPB as ZFPB from MS_YJ01 "
										+ " where YJXH=(select max(YJXH) as YJXH from MS_YJ02 where SBXH =:MZXH) and JGID=:JGID",
										parametersMZXH);
						if (resMap == null) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
						if (parseInt(resMap.get("ZFPB")) != 1) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
					} else {
						throw new RuntimeException("存在无法作废的明细，请重新选择");
					}
				} else if (brly == 2) {
					if (WPJFBZ.equals("1") && WZSFXMJGZY.equals("1")) {
						Map<String, Object> resMap = dao
								.doLoad("select sum(FYSL) as NUM from ZY_FYMX where YZXH=:MZXH and JGID=:JGID",
										parametersMZXH);
						if (resMap == null) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
						if (resMap.get("NUM") == null) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
						if (parseDouble(resMap.get("NUM")) > 0) {
							throw new RuntimeException("存在无法作废的明细，请重新选择");
						}
					} else {
						throw new RuntimeException("存在无法作废的明细，请重新选择");
					}
				} else {
					throw new RuntimeException("存在无法作废的明细，请重新选择");
				}
			}
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				record.put("ZTBZ", -1);
				parameters.put("JLXH", Long.parseLong(record.get("JLXH") + ""));
				parameters.put("ZTBZ", -1);
				dao.doUpdate("update WL_XHMX set ZTBZ=:ZTBZ where JLXH=:JLXH",
						parameters);

				// 如果当前作废记录的KCXH大于0，则作废时要更新库存中对应批次的预扣数量（预扣数量要减去本次出库数量）；
				if (record.get("KCXH") != null && record.get("KCXH") != ""
						&& Long.parseLong(record.get("KCXH") + "") > 0) {
					Map<String, Object> parametersWZKC = new HashMap<String, Object>();
					parametersWZKC.put("KCXH",
							Long.parseLong(record.get("KCXH") + ""));
					parametersWZKC.put("WZSL",
							Double.parseDouble(record.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_WZKC set YKSL=YKSL-:WZSL where KCXH=:KCXH",
							parametersWZKC);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("生成失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "生成失败");
		}

	}

	public void doGetCK01Info(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId").toString();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			parameters.put("KFXH", KFXH);
			StringBuffer sql_list = new StringBuffer(
					"select * from (select b.DJXH as DJXH,b.LZFS as LZFS,b.LZDH as LZDH,b.CKRQ as CKRQ,b.CKFS as CKFS,"
							+ " b.KSDM as KSDM,b.SLGH as SLGH,b.DJZT as DJZT,b.DJJE as DJJE,b.JBGH as JBGH,"
							+ " b.DJBZ as DJBZ,b.SHRQ as SHRQ,b.JZRQ as JZRQ,b.DJLX as DJLX,"
							+ " b.ZDRQ as ZDRQ,b.JGID as JGID,b.KFXH as KFXH,b.ZBLB as ZBLB,b.CKKF as CKKF,"
							+ " b.JGFS as JGFS,b.ZDGH as ZDGH,b.SHGH as SHGH,b.JZGH as JZGH,b.THDJ as THDJ,"
							+ " b.PDDJ as PDDJ,b.QRBZ as QRBZ,b.QRRK as QRRK,b.QRRQ as QRRQ,b.QRGH as QRGH,b.RKDJ as RKDJ,"
							+ " (select max(g.BRXM) as BRXM from WL_XHMX g where g.djxh = b.djxh) as BRXM,"
							+ " (select max(g.BRLY) as BRLY from WL_XHMX g where g.djxh = b.djxh) as BRLY"
							+ " from  WL_CK01 b where b.DJLX<4 and b.KFXH =:KFXH)");
			int djzt = -1;
			if (req.containsKey("DJZT")) {
				djzt = parseInt(req.get("DJZT"));
				sql_list.append(" where DJZT =:DJZT ");
				parameters.put("DJZT", djzt);
			}
			if (req.containsKey("SHRQQ") && djzt == 1) {
				Date SHRQQ = sdf.parse(req.get("SHRQQ") + "");
				sql_list.append(" and SHRQ >=:SHRQQ ");
				parameters.put("SHRQQ", SHRQQ);
			}
			if (req.containsKey("SHRQZ") && djzt == 1) {
				Date SHRQZ = sdf.parse(req.get("SHRQZ") + "");
				sql_list.append(" and SHRQ <=:SHRQZ ");
				parameters.put("SHRQZ", SHRQZ);
			}
			if (req.containsKey("JZRQQ") && djzt == 2) {
				Date JZRQQ = sdf.parse(req.get("JZRQQ") + "");
				sql_list.append(" and JZRQ >=:JZRQQ ");
				parameters.put("JZRQQ", JZRQQ);
			}
			if (req.containsKey("JZRQZ") && djzt == 2) {
				Date JZRQZ = sdf.parse(req.get("JZRQZ") + "");
				sql_list.append(" and JZRQ <=:JZRQZ ");
				parameters.put("JZRQZ", JZRQZ);
			}

			sql_list.append(" order by BRXM asc,KSDM asc,CKRQ desc");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.sup.schemas.WL_CK01_EJKF");
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "二级库房出库明细查询失败！");
		} catch (ParseException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "二级库房出库明细查询失败！");
		}
	}

	public String getLzdh() {
		String lzdh = "CK";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
