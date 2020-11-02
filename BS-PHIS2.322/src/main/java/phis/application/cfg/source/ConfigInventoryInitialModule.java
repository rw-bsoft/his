package phis.application.cfg.source;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigInventoryInitialModule {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigInventoryInitialModule.class);

	public ConfigInventoryInitialModule(BaseDAO dao) {
		this.dao = dao;
	}

	public void doSaveInventoryIn(Map<String, Object> req,
			List<Map<String, Object>> listReq, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String op = "create";
		for (int i = 0; i < listReq.size(); i++) {
			listReq.get(i).put("WZXH", req.get("WZXH"));
			listReq.get(i).put("CJXH", req.get("CJXH"));
			listReq.get(i).put("KFXH", req.get("KFXH"));
			listReq.get(i).put("ZBLB", req.get("ZBLB"));
			listReq.get(i).put("CSLB", 1);
			listReq.get(i).put("JGID", JGID);
			listReq.get(i).remove("DWMC");
			Long kcxh = BSPHISUtil.setKCXH(dao);
			listReq.get(i).put("KCXH", kcxh);
			try {
				if (listReq.get(i).get("_opStatus") == ""
						|| listReq.get(i).get("_opStatus") == null) {
					op = "update";
				} else {
					op = "create";
				}
				dao.doSave(op, BSPHISEntryNames.WL_CSKC, listReq.get(i), false);
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}

	public void doSaveDepartmentsIn(Map<String, Object> req,
			List<Map<String, Object>> listReq, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String op = "create";
		for (int i = 0; i < listReq.size(); i++) {
			listReq.get(i).put("WZXH", req.get("WZXH"));
			listReq.get(i).put("CJXH", req.get("CJXH"));
			listReq.get(i).put("KFXH", req.get("KFXH"));
			listReq.get(i).put("ZBLB", req.get("ZBLB"));
			listReq.get(i).put("CSLB", 2);
			listReq.get(i).put("JGID", JGID);
			listReq.get(i).remove("DWMC");
			Long kcxh = BSPHISUtil.setKCXH(dao);
			listReq.get(i).put("KCXH", kcxh);
			try {
				if (listReq.get(i).get("_opStatus") == ""
						|| listReq.get(i).get("_opStatus") == null) {
					op = "update";
				} else {
					op = "create";
				}
				dao.doSave(op, BSPHISEntryNames.WL_CSKC, listReq.get(i), false);
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}

	public void doSaveAssetsIn(Map<String, Object> req,
			List<Map<String, Object>> listReq, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String uid = user.getUserId();
		String op = "create";
		for (int i = 0; i < listReq.size(); i++) {
			listReq.get(i).put("WZXH", req.get("WZXH"));
			listReq.get(i).put("CJXH", req.get("CJXH"));
			listReq.get(i).put("KFXH", req.get("KFXH"));
			listReq.get(i).put("ZBLB", req.get("ZBLB"));
			listReq.get(i).put("GHDW", req.get("GHDW"));
			listReq.get(i).put("TZRQ", req.get("TZRQ"));
			listReq.get(i).put("ZCYZ", req.get("ZCYZ"));
			listReq.get(i).put("WHYZ", req.get("WHYZ"));
			listReq.get(i).put("HBDW", req.get("HBDW"));
			listReq.get(i).put("JGID", JGID);
			listReq.get(i).put("BGGH", uid);
			Long kcxh = BSPHISUtil.setKCXH(dao);
			listReq.get(i).put("KCXH", kcxh);
			if ("2".equals(listReq.get(i).get("WZZT") + "")) {
				listReq.get(i).put("WZZT", 0);
			}
			try {
				if (listReq.get(i).get("_opStatus") == ""
						|| listReq.get(i).get("_opStatus") == null) {
					op = "update";
				} else {
					op = "create";
				}
				dao.doSave(op, BSPHISEntryNames.WL_CSZC, listReq.get(i), false);
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}

	public void doBackfillingInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long jlxh = Long.parseLong(req.get("jlxh") + "");
		parameters.put("JLXH", jlxh);
		try {
			Map<String, Object> wzxx = dao
					.doLoad("SELECT b.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,c.CJXH as CJXH,c.CJMC as CJMC,a.KFXH as KFXH,a.ZBLB as ZBLB from WL_CSKC a,WL_WZZD b,WL_SCCJ c where a.WZXH=b.WZXH and a.CJXH=c.CJXH and a.JLXH=:JLXH",
							parameters);
			res.put("body", wzxx);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doBackfillingAssetsIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersdwmc = new HashMap<String, Object>();

		Long jlxh = Long.parseLong(req.get("jlxh") + "");
		parameters.put("JLXH", jlxh);
		try {
			Map<String, Object> wzxx = dao
					.doLoad("SELECT b.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,c.CJXH as CJXH,c.CJMC as CJMC,a.KFXH as KFXH,a.ZBLB as ZBLB,str(a.TZRQ,'YYYY-MM-DD') as TZRQ,a.ZCYZ as ZCYZ,a.WHYZ as WHYZ,a.HBDW as HBDW,a.GHDW as GHDW from WL_CSZC a,WL_WZZD b,WL_SCCJ c where a.WZXH=b.WZXH and a.CJXH=c.CJXH and a.JLXH=:JLXH",
							parameters);
			if (Integer.parseInt(wzxx.get("GHDW") + "") != 0) {
				parametersdwmc.put("DWXH",
						Long.parseLong(wzxx.get("GHDW") + ""));
				Map<String, Object> dwmc = dao.doLoad(
						"SELECT DWMC as DWMC from WL_GHDW where DWXH=:DWXH",
						parametersdwmc);
				if (dwmc.get("DWMC") != null) {
					wzxx.put("DWMC", dwmc.get("DWMC") + "");
				}
			}
			res.put("body", wzxx);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doDeleteInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long jlxh = Long.parseLong(req.get("pkey") + "");
		parameters.put("JLXH", jlxh);
		try {
			dao.doUpdate("delete from WL_CSKC where JLXH=:JLXH", parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doDeleteAssetsIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long jlxh = Long.parseLong(req.get("pkey") + "");
		parameters.put("JLXH", jlxh);
		try {
			dao.doUpdate("delete from WL_CSZC where JLXH=:JLXH", parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doSaveTransferInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterszblb = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Long kfxhl = Long.parseLong(user.getProperty("treasuryId") + "");
		int ejkf = 0;
		if (user.getProperty("treasuryEjkf") != null
				&& user.getProperty("treasuryEjkf") != "") {
			ejkf = Integer.parseInt(user.getProperty("treasuryEjkf") + "");
		}
		int yjsh = Integer.parseInt(ParameterUtil.getParameter(JGID, "YJSJ_KF"
				+ kfxh, "32", "库房月结时间 对应一个月的31天  32为月底结 ", ctx));
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.MONTH, 0);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat formatYM = new SimpleDateFormat("yyyy-MM");
		String time = format.format(c.getTime());
		String timeSY = formatYM.format(c.getTime());
		String timeBY = formatYM.format(c1.getTime());
		int enddayby = c1.getActualMaximum(Calendar.DAY_OF_MONTH);
		int enddaysy = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		parameters.put("KFXH", kfxh);
		parameters.put("JGID", JGID);
		parameterszblb.put("KFXH", kfxhl);
		Map<String, Object> yjjlmap = new HashMap<String, Object>();
		Map<String, Object> yjjgmap = new HashMap<String, Object>();
		Map<String, Object> yjjgnooryesmap = new HashMap<String, Object>();
		Map<String, Object> yjjgupdmap = new HashMap<String, Object>();
		Map<String, Object> parameterswzkc = new HashMap<String, Object>();
		Map<String, Object> parameterskszc = new HashMap<String, Object>();
		Map<String, Object> parametersyjzc = new HashMap<String, Object>();

		Date begindate = null;
		Date enddate = null;
		try {
			if (yjsh == 32) {
				begindate = (Date) BSHISUtil.getFirstDayOfMonth(sdfdate
						.parse(BSHISUtil.getDate()));
				enddate = (Date) BSHISUtil.getLastDayOfMonth(sdfdate
						.parse(BSHISUtil.getDate()));
			} else {
				String days = "01";
				if (enddaysy < yjsh) {
					days = "-" + enddaysy;
				} else {
					if (yjsh < 10) {
						days = "-0" + yjsh;
					} else {
						days = "-" + yjsh;
					}
				}
				String dayb = "01";
				if (enddayby < yjsh) {
					dayb = "-" + enddayby;
				} else {
					if (yjsh < 10) {
						dayb = "-0" + yjsh;
					} else {
						dayb = "-" + yjsh;
					}
				}
				begindate = (Date) sdfdate.parse(timeSY + days);
				enddate = (Date) sdfdate.parse(timeBY + dayb);
			}
			List<Map<String, Object>> kszclist = dao
					.doQuery(
							"SELECT a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.JGID as JGID,a.KFXH as KFXH,a.KSDM as KSDM,a.WZSL as WZSL,0 as YKSL,a.WZJG as WZJG,a.WZJE as WZJE,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.MJPH as MJPH FROM WL_CSKC a WHERE a.KFXH =:KFXH and a.JGID=:JGID and a.CSLB =2",
							parameters);
			List<Map<String, Object>> wzzclist = dao
					.doQuery(
							"SELECT a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.JGID as JGID,a.KFXH as KFXH,a.WZSL as WZSL,0 as YKSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH ,a.MJPH as MJPH,a.RKRQ as FSRQ FROM WL_CSKC a WHERE a.KFXH =:KFXH and a.JGID=:JGID and a.CSLB =1",
							parameters);
			List<Map<String, Object>> cszclist = dao
					.doQuery(
							"SELECT a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.JGID as JGID,a.KFXH as KFXH,a.WZSL as WZSL,a.ZCYZ as ZCYZ,a.CZYZ as CZYZ,a.WHYZ as WHYZ,a.HBDW as HBDW,a.GHDW as GHDW,a.ZYKS as ZYKS,a.BGGH as BGGH,a.WZZT as WZZT,a.TZRQ as TZRQ,a.QYRQ as QYRQ,a.CZRQ as CZRQ,a.JTZJ as JTZJ,a.ZJYS as ZJYS,a.FCYS as FCYS,0 as CLBZ,a.KCXH as KCXH FROM WL_CSZC a WHERE a.KFXH =:KFXH and a.JGID=:JGID",
							parameters);
			for (int i = 0; i < kszclist.size(); i++) {
				dao.doSave("create", BSPHISEntryNames.WL_KSZC, kszclist.get(i),
						false);
			}
			for (int i = 0; i < wzzclist.size(); i++) {
				dao.doSave("create", BSPHISEntryNames.WL_WZKC, wzzclist.get(i),
						false);
			}
			List<Map<String, Object>> yjjllist = dao
					.doQuery(
							"SELECT a.WZXH as WZXH,a.CJXH as CJXH,a.ZBLB as ZBLB,sum(a.WZSL) as WZSL,sum(a.WZJE) as WZJE,sum(a.LSJE) as LSJE FROM WL_CSKC a WHERE a.KFXH =:KFXH and a.JGID=:JGID and a.CSLB =1 group by a.WZXH,a.CJXH,a.ZBLB",
							parameters);
			Map<String, Object> zblbmap = dao.doLoad(
					"select KFZB as KFZB from WL_KFXX where KFXH=:KFXH",
					parameterszblb);
			String KFZB = zblbmap.get("KFZB") + "";
			String[] str = KFZB.split(",");
			if (ejkf == 0) {
				for (int i = 0; i < str.length; i++) {
					yjjlmap.put("JGID", JGID);
					yjjlmap.put("KFXH", kfxh);
					yjjlmap.put("ZBLB", str[i]);
					yjjlmap.put("CWYF", time);
					yjjlmap.put("QSSJ", begindate);
					yjjlmap.put("ZZSJ", enddate);
					yjjlmap.put("JZSJ", sdfdate.parse(BSHISUtil.getDate()));
					yjjlmap.put("CSBZ", 1);
					Map<String, Object> mapjzxh = dao.doSave("create",
							BSPHISEntryNames.WL_YJJL, yjjlmap, false);
					Long jzxh = Long.parseLong(mapjzxh.get("JZXH") + "");
					for (int j = 0; j < yjjllist.size(); j++) {
						if (yjjllist.get(j).get("ZBLB").toString()
								.equals(str[i])) {
							yjjgnooryesmap.put("JZXH", jzxh);
							yjjgnooryesmap.put(
									"WZXH",
									Long.parseLong(yjjllist.get(j).get("WZXH")
											+ ""));
							yjjgnooryesmap.put(
									"CJXH",
									Long.parseLong(yjjllist.get(j).get("CJXH")
											+ ""));
							Long l = dao.doCount("WL_YJJG",
									"JZXH=:JZXH and WZXH=:WZXH and CJXH=:CJXH",
									yjjgnooryesmap);
							if (l > 0) {
								yjjgupdmap.put("JZXH", jzxh);
								yjjgupdmap.put(
										"WZXH",
										Long.parseLong(yjjllist.get(j).get(
												"WZXH")
												+ ""));
								yjjgupdmap.put(
										"CJXH",
										Long.parseLong(yjjllist.get(j).get(
												"CJXH")
												+ ""));
								yjjgupdmap.put(
										"QMSL",
										Double.parseDouble(yjjllist.get(j).get(
												"WZSL")
												+ ""));
								yjjgupdmap.put(
										"QMJE",
										Double.parseDouble(yjjllist.get(j).get(
												"WZJE")
												+ ""));
								yjjgupdmap.put(
										"JCLSJE",
										Double.parseDouble(yjjllist.get(j).get(
												"LSJE")
												+ ""));
								dao.doUpdate(
										"update WL_YJJG set QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JZXH=:JZXH and WZXH=:WZXH and CJXH=:CJXH",
										yjjgupdmap);
							} else {
								yjjgmap.put("JZXH", jzxh);
								yjjgmap.put(
										"ZBLB",
										Integer.parseInt(yjjllist.get(j).get(
												"ZBLB")
												+ ""));
								yjjgmap.put(
										"WZXH",
										Long.parseLong(yjjllist.get(j).get(
												"WZXH")
												+ ""));
								yjjgmap.put(
										"CJXH",
										Long.parseLong(yjjllist.get(j).get(
												"CJXH")
												+ ""));
								yjjgmap.put("QCSL", 0.00);
								yjjgmap.put("QCJE", 0.00);
								yjjgmap.put("RKSL", 0.00);
								yjjgmap.put("RKJE", 0.00);
								yjjgmap.put("CKSL", 0.00);
								yjjgmap.put("CKJE", 0.00);
								yjjgmap.put("BSSL", 0.00);
								yjjgmap.put("BSJE", 0.00);
								yjjgmap.put("PYSL", 0.00);
								yjjgmap.put("PYJE", 0.00);
								yjjgmap.put("QCLSJE", 0.00);
								yjjgmap.put("RKLSJE", 0.00);
								yjjgmap.put("CKLSJE", 0.00);
								yjjgmap.put(
										"QMSL",
										Double.parseDouble(yjjllist.get(j).get(
												"WZSL")
												+ ""));
								yjjgmap.put(
										"QMJE",
										Double.parseDouble(yjjllist.get(j).get(
												"WZJE")
												+ ""));
								yjjgmap.put(
										"JCLSJE",
										Double.parseDouble(yjjllist.get(j).get(
												"LSJE")
												+ ""));
								dao.doSave("create", BSPHISEntryNames.WL_YJJG,
										yjjgmap, false);
							}
						}
					}
				}
			} else {
				yjjlmap.put("JGID", JGID);
				yjjlmap.put("KFXH", kfxh);
				yjjlmap.put("ZBLB", 0);
				yjjlmap.put("CWYF", time);
				yjjlmap.put("QSSJ", begindate);
				yjjlmap.put("ZZSJ", enddate);
				yjjlmap.put("JZSJ", sdfdate.parse(BSHISUtil.getDate()));
				yjjlmap.put("CSBZ", 1);
				Map<String, Object> mapjzxh = dao.doSave("create",
						BSPHISEntryNames.WL_YJJL, yjjlmap, false);
				Long jzxh = Long.parseLong(mapjzxh.get("JZXH") + "");
				for (int j = 0; j < yjjllist.size(); j++) {
					yjjgnooryesmap.put("JZXH", jzxh);
					yjjgnooryesmap.put("WZXH",
							Long.parseLong(yjjllist.get(j).get("WZXH") + ""));
					yjjgnooryesmap.put("CJXH",
							Long.parseLong(yjjllist.get(j).get("CJXH") + ""));
					Long l = dao.doCount("WL_YJJG",
							"JZXH=:JZXH and WZXH=:WZXH and CJXH=:CJXH",
							yjjgnooryesmap);
					if (l > 0) {
						yjjgupdmap.put("JZXH", jzxh);
						yjjgupdmap.put("WZXH", Long.parseLong(yjjllist.get(j)
								.get("WZXH") + ""));
						yjjgupdmap.put("CJXH", Long.parseLong(yjjllist.get(j)
								.get("CJXH") + ""));
						yjjgupdmap.put(
								"QMSL",
								Double.parseDouble(yjjllist.get(j).get("WZSL")
										+ ""));
						yjjgupdmap.put(
								"QMJE",
								Double.parseDouble(yjjllist.get(j).get("WZJE")
										+ ""));
						yjjgupdmap.put(
								"JCLSJE",
								Double.parseDouble(yjjllist.get(j).get("LSJE")
										+ ""));
						dao.doUpdate(
								"update WL_YJJG set QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JZXH=:JZXH and WZXH=:WZXH and CJXH=:CJXH",
								yjjgupdmap);
					} else {
						yjjgmap.put("JZXH", jzxh);
						yjjgmap.put(
								"ZBLB",
								Integer.parseInt(yjjllist.get(j).get("ZBLB")
										+ ""));
						yjjgmap.put("WZXH", Long.parseLong(yjjllist.get(j).get(
								"WZXH")
								+ ""));
						yjjgmap.put("CJXH", Long.parseLong(yjjllist.get(j).get(
								"CJXH")
								+ ""));
						yjjgmap.put("QCSL", 0.00);
						yjjgmap.put("QCJE", 0.00);
						yjjgmap.put("RKSL", 0.00);
						yjjgmap.put("RKJE", 0.00);
						yjjgmap.put("CKSL", 0.00);
						yjjgmap.put("CKJE", 0.00);
						yjjgmap.put("BSSL", 0.00);
						yjjgmap.put("BSJE", 0.00);
						yjjgmap.put("PYSL", 0.00);
						yjjgmap.put("PYJE", 0.00);
						yjjgmap.put("QCLSJE", 0.00);
						yjjgmap.put("RKLSJE", 0.00);
						yjjgmap.put("CKLSJE", 0.00);
						yjjgmap.put(
								"QMSL",
								Double.parseDouble(yjjllist.get(j).get("WZSL")
										+ ""));
						yjjgmap.put(
								"QMJE",
								Double.parseDouble(yjjllist.get(j).get("WZJE")
										+ ""));
						yjjgmap.put(
								"JCLSJE",
								Double.parseDouble(yjjllist.get(j).get("LSJE")
										+ ""));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG, yjjgmap,
								false);
					}
				}
			}
			for (int i = 0; i < cszclist.size(); i++) {
				int wzsl = (int) Double.parseDouble(cszclist.get(i).get("WZSL")
						+ "");
				for (int j = 0; j < wzsl; j++) {
					String wzbh = BSPHISUtil.setWZBH(cszclist.get(i), dao, ctx)
							+ "";
					cszclist.get(i).put("WZBH", wzbh);
					Map<String, Object> zbxhmap = dao.doSave("create",
							BSPHISEntryNames.WL_ZCZB, cszclist.get(i), false);
					if ("1".equals(cszclist.get(i).get("WZZT") + "")) {
						parametersyjzc.put("JGID", cszclist.get(i).get("JGID")
								+ "");
						if (str.length > 0) {
							parametersyjzc.put("JZXH", Long.parseLong(str[0]));
						}
						parametersyjzc.put(
								"KFXH",
								Integer.parseInt(cszclist.get(i).get("KFXH")
										+ ""));
						parametersyjzc.put(
								"ZBLB",
								Integer.parseInt(cszclist.get(i).get("ZBLB")
										+ ""));
						parametersyjzc.put("CWYF", time);
						parametersyjzc.put("WZXH", Long.parseLong(cszclist.get(
								i).get("WZXH")
								+ ""));
						parametersyjzc.put(
								"WZZT",
								Integer.parseInt(cszclist.get(i).get("WZZT")
										+ ""));
						parametersyjzc.put("ZYKS", Long.parseLong(cszclist.get(
								i).get("ZYKS")
								+ ""));
						parametersyjzc.put(
								"CZYZ",
								Double.parseDouble(cszclist.get(i).get("CZYZ")
										+ ""));
						parametersyjzc.put("ZBXH",
								Long.parseLong(zbxhmap.get("ZBXH") + ""));
						dao.doSave("create", BSPHISEntryNames.WL_YJZC,
								parametersyjzc, false);
					}
				}
				if ("0".equals(cszclist.get(i).get("WZZT") + "")
						|| "-1".equals(cszclist.get(i).get("WZZT") + "")) {
					double wzje = Double.parseDouble(cszclist.get(i)
							.get("WZSL") + "")
							* Double.parseDouble(cszclist.get(i).get("CZYZ")
									+ "");
					parameterswzkc
							.put("JGID", cszclist.get(i).get("JGID") + "");
					parameterswzkc.put("KCXH",
							Long.parseLong(cszclist.get(i).get("KCXH") + ""));
					parameterswzkc.put("KFXH",
							Integer.parseInt(cszclist.get(i).get("KFXH") + ""));
					parameterswzkc.put("ZBLB",
							Integer.parseInt(cszclist.get(i).get("ZBLB") + ""));
					parameterswzkc.put("WZXH",
							Long.parseLong(cszclist.get(i).get("WZXH") + ""));
					parameterswzkc.put("CJXH",
							Long.parseLong(cszclist.get(i).get("CJXH") + ""));
					parameterswzkc.put("WZSL", wzsl);
					parameterswzkc.put("WZJG", Double.parseDouble(cszclist.get(
							i).get("CZYZ")
							+ ""));
					parameterswzkc.put("LSJG", Double.parseDouble(cszclist.get(
							i).get("CZYZ")
							+ ""));
					parameterswzkc.put("LSJE", wzje);
					parameterswzkc.put("WZJE", wzje);
					parameterswzkc.put("YKSL", 0);
					dao.doSave("create", BSPHISEntryNames.WL_WZKC,
							parameterswzkc, false);
				}
				if ("1".equals(cszclist.get(i).get("WZZT") + "")) {
					double wzje = Double.parseDouble(cszclist.get(i)
							.get("WZSL") + "")
							* Double.parseDouble(cszclist.get(i).get("CZYZ")
									+ "");
					parameterskszc
							.put("JGID", cszclist.get(i).get("JGID") + "");
					parameterskszc.put("KCXH",
							Long.parseLong(cszclist.get(i).get("KCXH") + ""));
					parameterskszc.put("KFXH",
							Integer.parseInt(cszclist.get(i).get("KFXH") + ""));
					parameterskszc.put("ZBLB",
							Integer.parseInt(cszclist.get(i).get("ZBLB") + ""));
					parameterskszc.put("WZXH",
							Long.parseLong(cszclist.get(i).get("WZXH") + ""));
					parameterskszc.put("CJXH",
							Long.parseLong(cszclist.get(i).get("CJXH") + ""));
					parameterskszc.put("WZSL", wzsl);
					parameterskszc.put("WZJG", Double.parseDouble(cszclist.get(
							i).get("CZYZ")
							+ ""));

					parameterswzkc.put("LSJG", Double.parseDouble(cszclist.get(
							i).get("CZYZ")
							+ ""));
					parameterswzkc.put("LSJE", wzje);

					parameterskszc.put("WZJE", wzje);
					parameterskszc.put("YKSL", 0);
					parameterskszc.put("KSDM",
							Long.parseLong(cszclist.get(i).get("ZYKS") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_KSZC,
							parameterskszc, false);
				}
			}
			List<Map<String, Object>> cszcyjjg = dao
					.doQuery(
							"SELECT a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,sum(a.WZSL) as WZSL,sum(a.WZSL*a.CZYZ) as WZJE FROM WL_CSZC a WHERE a.KFXH =:KFXH and a.JGID=:JGID and (a.WZZT=-1 or a.WZZT=0) group by a.WZXH,a.CJXH,a.ZBLB",
							parameters);
			List<Map<String, Object>> cszcyjjl = dao
					.doQuery(
							"SELECT a.JZXH as JZXH,a.ZBLB as ZBLB FROM WL_YJJL a WHERE a.KFXH =:KFXH and a.JGID=:JGID",
							parameters);
			for (int i = 0; i < cszcyjjl.size(); i++) {
				for (int j = 0; j < cszcyjjg.size(); j++) {
					if (cszcyjjg.get(j).get("ZBLB").toString()
							.equals(cszcyjjl.get(i).get("ZBLB") + "")) {
						yjjgnooryesmap.put("JZXH", Long.parseLong(cszcyjjl.get(
								i).get("JZXH")
								+ ""));
						yjjgnooryesmap.put("WZXH", Long.parseLong(cszcyjjg.get(
								j).get("WZXH")
								+ ""));
						yjjgnooryesmap.put("CJXH", Long.parseLong(cszcyjjg.get(
								j).get("CJXH")
								+ ""));
						Long l = dao.doCount("WL_YJJG",
								"JZXH=:JZXH and WZXH=:WZXH and CJXH=:CJXH",
								yjjgnooryesmap);
						if (l > 0) {
							yjjgupdmap.put(
									"JZXH",
									Long.parseLong(cszcyjjl.get(i).get("JZXH")
											+ ""));
							yjjgupdmap.put(
									"WZXH",
									Long.parseLong(cszcyjjg.get(j).get("WZXH")
											+ ""));
							yjjgupdmap.put(
									"CJXH",
									Long.parseLong(cszcyjjg.get(j).get("CJXH")
											+ ""));
							yjjgupdmap.put("QMSL", cszcyjjg.get(j).get("WZSL"));
							yjjgupdmap.put("QMJE", cszcyjjg.get(j).get("WZJE"));
							yjjgupdmap.put("JCLSJE", cszcyjjg.get(j)
									.get("WZJE"));
							dao.doUpdate(
									"update WL_YJJG set QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JZXH=:JZXH and WZXH=:WZXH and CJXH=:CJXH",
									yjjgupdmap);
						} else {
							yjjgmap.put(
									"JZXH",
									Long.parseLong(cszcyjjl.get(i).get("JZXH")
											+ ""));
							yjjgmap.put(
									"ZBLB",
									Integer.parseInt(cszcyjjg.get(j)
											.get("ZBLB") + ""));
							yjjgmap.put(
									"WZXH",
									Long.parseLong(cszcyjjg.get(j).get("WZXH")
											+ ""));
							yjjgmap.put(
									"CJXH",
									Long.parseLong(cszcyjjg.get(j).get("CJXH")
											+ ""));
							yjjgmap.put("QCSL", 0.00);
							yjjgmap.put("QCJE", 0.00);
							yjjgmap.put("RKSL", 0.00);
							yjjgmap.put("RKJE", 0.00);
							yjjgmap.put("CKSL", 0.00);
							yjjgmap.put("CKJE", 0.00);
							yjjgmap.put("QCLSJE", 0.00);
							yjjgmap.put("RKLSJE", 0.00);
							yjjgmap.put("CKLSJE", 0.00);
							yjjgmap.put(
									"QMSL",
									Double.parseDouble(cszcyjjg.get(j).get(
											"WZSL")
											+ ""));
							yjjgmap.put(
									"QMJE",
									Double.parseDouble(cszcyjjg.get(j).get(
											"WZJE")
											+ ""));
							yjjgmap.put(
									"JCLSJE",
									Double.parseDouble(cszcyjjg.get(j).get(
											"WZJE")
											+ ""));
							dao.doSave("create", BSPHISEntryNames.WL_YJJG,
									yjjgmap, false);
						}
					}
				}
			}
			dao.doUpdate(
					"UPDATE WL_KFXX SET CSBZ =1 WHERE KFXH ="
							+ Long.parseLong(kfxh + ""), null);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
