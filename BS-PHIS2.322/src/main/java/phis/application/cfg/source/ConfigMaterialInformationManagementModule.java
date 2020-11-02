package phis.application.cfg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigMaterialInformationManagementModule {
	protected BaseDAO dao;

	public ConfigMaterialInformationManagementModule() {
	}

	public ConfigMaterialInformationManagementModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 查询类别序号信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryLBXH(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		String KFXH = req.get("KFXH") + "";
		try {
			ret = dao.doLoad(BSPHISEntryNames.WL_KFXX, KFXH);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		if (ret.get("LBXH") != null) {
			res.put("ret", ret.get("LBXH"));
		}

	}

	/**
	 * 查询是否检定
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryJLXX(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> wzxhpar = new HashMap<String, Object>();
		wzxhpar.put("WZXH", Long.parseLong(req.get("WZXH") + ""));
		try {
			List<Map<String, Object>> jlxxlist = dao.doQuery(
					"select JLXH as JLXH from WL_JLXX where WZXH=:WZXH",
					wzxhpar);
			if (jlxxlist.size() > 0) {
				res.put("JLXH", 1);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}

	}

	/**
	 * 保存数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void doSaveData(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ValidateException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> wzzdwzmc = new HashMap<String, Object>();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null) {
			kfxh = parseInt(user.getProperty("treasuryId"));// 用户的机构ID
		}
		String TOPID = ParameterUtil.getTopUnitId();
		// 卫生局JGID
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		@SuppressWarnings("unchecked")
		Map<String, Object> WZZDinfor = (Map<String, Object>) body
				.get("baseInfoTab");

		if (JGID.equals(TOPID)) {
			WZZDinfor.put("KFXH", 0);
		} else {
			WZZDinfor.put("KFXH", kfxh);
		}
		if (WZZDinfor.get("GCSL") == null) {
			WZZDinfor.put("GCSL", 0.0d);
		}
		if (WZZDinfor.get("DCSL") == null) {
			WZZDinfor.put("DCSL", 0.0d);
		}
		if (WZZDinfor.get("ZGZL") == null) {
			WZZDinfor.put("ZGZL", 0.0d);
		}
		if (Integer.parseInt(WZZDinfor.get("WZZT") + "") == 0) {
			WZZDinfor.put("WZZT", 1);
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> WZCJinfor = (List<Map<String, Object>>) body
				.get("manufacturerTab");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> WZBMinfor = (List<Map<String, Object>>) body
				.get("itemAliasTab");
		try {
			// 保存物资字典
			String op = body.get("op") + "";
			if ("create".equals(op)) {
				List<Map<String, Object>> WZXH = dao.doSqlQuery(
						"select max(wzxh) as wzxh from wl_wzzd", null);
				if (WZXH.get(0).get("WZXH") == null) {
					WZZDinfor.put("WZXH", 1);
				} else {
					WZZDinfor
							.put("WZXH", parseInt(WZXH.get(0).get("WZXH")) + 1);
				}
			} else {
				WZZDinfor.put("WZXH", body.get("WZXH"));
			}
			WZZDinfor.put("GCSL", parseDouble((WZZDinfor.get("GCSL"))));
			WZZDinfor.put("DCSL", parseDouble((WZZDinfor.get("DCSL"))));
			WZZDinfor.put("ZGZL", parseDouble((WZZDinfor.get("ZGZL"))));
			Long lc = 0l;
			String wheresql = "";
			if (op.endsWith("create")) {
				wzzdwzmc.put("KFXH", kfxh);
				wzzdwzmc.put("WZMC", WZZDinfor.get("WZMC") + "");
				if (WZZDinfor.get("WZGG") != null
						&& WZZDinfor.get("WZGG") != "") {
					wzzdwzmc.put("WZGG", WZZDinfor.get("WZGG") + "");
					wheresql = " and WZGG=:WZGG ";
				} else {
					wheresql = " and WZGG is null ";
				}
				wzzdwzmc.put("JGID", JGID);
				wzzdwzmc.put("JGIDTOP", TOPID);
				lc = dao.doCount("WL_WZZD", "WZMC=:WZMC " + wheresql
						+ "and (JGID=:JGID or JGID=:JGIDTOP) and KFXH=:KFXH",
						wzzdwzmc);
			} else {
				wzzdwzmc.put("WZMC", WZZDinfor.get("WZMC") + "");
				if (WZZDinfor.get("WZGG") != null
						&& WZZDinfor.get("WZGG") != "") {
					wzzdwzmc.put("WZGG", WZZDinfor.get("WZGG") + "");
					wheresql = " and WZGG=:WZGG ";
				} else {
					wheresql = " and WZGG is null ";
				}
				wzzdwzmc.put("JGID", JGID);
				wzzdwzmc.put("JGIDTOP", TOPID);
				wzzdwzmc.put("WZXH", Long.parseLong(WZZDinfor.get("WZXH") + ""));
				wzzdwzmc.put("KFXH", kfxh);
				lc = dao.doCount(
						"WL_WZZD",
						"WZMC=:WZMC "
								+ wheresql
								+ "and (JGID=:JGID or JGID=:JGIDTOP) and WZXH<>:WZXH and KFXH=:KFXH",
						wzzdwzmc);
			}
			if (lc > 0) {
				res.put(Service.RES_CODE, 600);
			} else {
				Map<String, Object> wzmbold = new HashMap<String, Object>();
				Map<String, Object> wzmboldmap = new HashMap<String, Object>();
				if (op.equals("update")) {
					wzmbold.put("WZXH",
							Long.parseLong(WZZDinfor.get("WZXH") + ""));
					wzmboldmap = dao
							.doLoad("select WZMC as WZMC,PYDM as PYDM,WBDM as WBDM,JXDM as JXDM,QTDM as QTDM from WL_WZZD where WZXH=:WZXH",
									wzmbold);
				}
				dao.doSave(op, BSPHISEntryNames.WL_WZZD_JBXX, WZZDinfor, false);
				if (wzmboldmap.size() > 0 && wzmboldmap != null) {
					Map<String, Object> wzmbnewmap = new HashMap<String, Object>();
					Map<String, Object> wzmbnewupdmap = new HashMap<String, Object>();
					wzmbnewmap = dao
							.doLoad("select WZMC as WZMC,PYDM as PYDM,WBDM as WBDM,JXDM as JXDM,QTDM as QTDM from WL_WZZD where WZXH=:WZXH",
									wzmbold);
					wzmbnewupdmap.put("WZXH",
							Long.parseLong(WZZDinfor.get("WZXH") + ""));
					wzmbnewupdmap.put("WZBMOLD", wzmboldmap.get("WZMC") + "");
					String pydmstr = "";
					if (wzmboldmap.get("PYDM") != null) {
						wzmbnewupdmap.put("PYDMOLD", wzmboldmap.get("PYDM")
								+ "");
						pydmstr = "and PYDM=:PYDMOLD ";
					} else {
						pydmstr = "and PYDM is null ";
					}
					String wbdmstr = "";
					if (wzmboldmap.get("WBDM") != null) {
						wzmbnewupdmap.put("WBDMOLD", wzmboldmap.get("WBDM")
								+ "");
						wbdmstr = "and WBDM=:WBDMOLD ";
					} else {
						wbdmstr = "and WBDM is null ";
					}
					String jxdmstr = "";
					if (wzmboldmap.get("JXDM") != null) {
						wzmbnewupdmap.put("JXDMOLD", wzmboldmap.get("JXDM")
								+ "");
						jxdmstr = "and JXDM=:JXDMOLD ";
					} else {
						jxdmstr = "and JXDM is null ";
					}
					String qtdmstr = "";
					if (wzmboldmap.get("QTDM") != null) {
						wzmbnewupdmap.put("QTDMOLD", wzmboldmap.get("QTDM")
								+ "");
						qtdmstr = "and QTDM=:QTDMOLD ";
					} else {
						qtdmstr = "and QTDM is null ";
					}
					if (wzmbnewmap.size() > 0 && wzmbnewmap != null) {
						wzmbnewupdmap.put("WZBM", wzmbnewmap.get("WZMC") + "");
						if (wzmbnewmap.get("PYDM") != null) {
							wzmbnewupdmap.put("PYDM", wzmbnewmap.get("PYDM")
									+ "");
						} else {
							wzmbnewupdmap.put("PYDM", "");
						}
						if (wzmbnewmap.get("WBDM") != null) {
							wzmbnewupdmap.put("WBDM", wzmbnewmap.get("WBDM")
									+ "");
						} else {
							wzmbnewupdmap.put("WBDM", "");
						}
						if (wzmbnewmap.get("JXDM") != null) {
							wzmbnewupdmap.put("JXDM", wzmbnewmap.get("JXDM")
									+ "");
						} else {
							wzmbnewupdmap.put("JXDM", "");
						}
						if (wzmbnewmap.get("QTDM") != null) {
							wzmbnewupdmap.put("QTDM", wzmbnewmap.get("QTDM")
									+ "");
						} else {
							wzmbnewupdmap.put("QTDM", "");
						}
						dao.doUpdate(
								"update WL_WZBM set WZBM=:WZBM,PYDM=:PYDM,WBDM=:WBDM,JXDM=:JXDM,QTDM=:QTDM where WZBM=:WZBMOLD "
										+ pydmstr
										+ wbdmstr
										+ jxdmstr
										+ qtdmstr
										+ "and WZXH=:WZXH", wzmbnewupdmap);
					}
				}
				for (int i = 0; WZCJinfor != null && i < WZCJinfor.size(); i++) {
					Map<String, Object> record = WZCJinfor.get(i);
					if (record.get("WZJG") == null || record.get("WZJG") == "") {
						record.put("WZJG", 0.0000);
					}
					if (record.get("LSJG") == null || record.get("LSJG") == "") {
						record.put("LSJG", 0.0000);
					}
					if (record.get("JGBL") == null || record.get("JGBL") == "") {
						record.put("JGBL", 1.000);
					}
					Map<String, Object> pkey = new HashMap<String, Object>();
					pkey.put("WZXH", Long.parseLong(WZZDinfor.get("WZXH") + ""));
					pkey.put("CJXH", Long.parseLong(record.get("CJXH") + ""));
					if ("remove".equals(record.get("_opStatus"))) {
						Long l = dao.doCount(BSPHISEntryNames.WL_WZCJ,
								"WZXH=:WZXH and CJXH=:CJXH", pkey);
						if (l > 0) {
							dao.doRemove(pkey, BSPHISEntryNames.WL_WZCJ);
						}
					} else {
						Long l = dao.doCount("WL_WZCJ",
								"WZXH=:WZXH and CJXH=:CJXH", pkey);
						if (l <= 0) {
							record.putAll(pkey);
							dao.doSave("create", BSPHISEntryNames.WL_WZCJ,
									record, false);
						} else {
							if (!"create".equals(record.get("_opStatus"))) {
								dao.doSave("update", BSPHISEntryNames.WL_WZCJ,
										record, false);
							}
						}
					}
				}
				// 保存物资别名
				for (int i = 0; WZBMinfor != null && i < WZBMinfor.size(); i++) {
					Map<String, Object> record = WZBMinfor.get(i);
					Map<String, Object> parameters = new HashMap<String, Object>();

					if ("remove".equals(record.get("_opStatus"))) {
						long pkey = Long.parseLong(record.get("BMXH") + "");
						dao.doRemove(pkey, BSPHISEntryNames.WL_WZBM);
					} else {
						record.put("WZXH", WZZDinfor.get("WZXH"));
						parameters.put("WZXH",
								Long.parseLong(WZZDinfor.get("WZXH") + ""));
						parameters.put("WZBM", record.get("WZBM") + "");
						Long l = dao.doCount("WL_WZBM",
								"WZXH=:WZXH and WZBM=:WZBM", parameters);
						if (l <= 0) {
							dao.doSave(record.get("_opStatus") + "",
									BSPHISEntryNames.WL_WZBM, record, false);
						}
					}
				}

				// 如果当前角色是分机构角色，在“新增”或者“复制”模式下，要把对应物资序号插入到WL_WZGS中
				if (!JGID.equals(TOPID)
						&& ("create".equals(op) || "copy".equals(op))) {
					Map<String, Object> record = new HashMap<String, Object>();
					record.put("JGID", JGID);
					record.put("GYBZ", 0);
					record.put("WZZT", 1);
					record.put("KFXH", WZZDinfor.get("KFXH"));
					record.put("WZXH", WZZDinfor.get("WZXH"));
					dao.doSave("create", BSPHISEntryNames.WL_WZGS, record,
							false);

					// 同时库房物资分类不是按账簿类别，则保存WL_WZFL
					if (body.get("ZDXH") != null) {
						record = dao.doLoad(BSPHISEntryNames.WL_KFXX,
								WZZDinfor.get("KFXH"));
						int lbxh = 0;
						if (record.containsKey("LBXH")) {
							lbxh = record.get("LBXH") != null ? parseInt(record
									.get("LBXH")) : null;// 在库房信息表中获取lbxh
						}
						record.clear();
						record.put("JGID", JGID);
						record.put("LBXH", lbxh);
						record.put("WZXH", WZZDinfor.get("WZXH"));
						Map<String, Object> parameters = new HashMap<String, Object>();
						parameters.put("FLBM", body.get("ZDXH") + "");
						parameters.put("JGID", JGID);
						Map<String, Object> zdxhMap = dao
								.doLoad("select ZDXH as ZDXH from WL_FLZD where FLBM=:FLBM and JGID=:JGID",
										parameters);
						record.put("ZDXH",
								Long.parseLong(zdxhMap.get("ZDXH") + ""));
						dao.doSave("create", BSPHISEntryNames.WL_WZFL, record,
								false);

					}
				}
				// 根据库房序号和物资序号,在库存预警(WL_KCYJ)中查找是否存在对应记录，如果存在则
				// 把高低存储数量改为当前修改记录数量；如果不存在则在库存预警(WL_KCYJ)中插入记录信息
				String hql = "select KFXH as KFXH,WZXH as WZXH,GCSL as GCSL,DCSL as DCSL from WL_KCYJ where WZXH=:WZXH and KFXH=:KFXH";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("WZXH", Long.parseLong(WZZDinfor.get("WZXH") + ""));
				params.put("KFXH", WZZDinfor.get("KFXH"));
				List<Map<String, Object>> records = dao.doQuery(hql, params);
				// 如果修改后的高低存储数量都为0，则直接中库存预警中删除当前记录；
				if (records.size() != 0) {
					if (("0.0".equals(WZZDinfor.get("GCSL") + ""))
							&& "0.0".equals(WZZDinfor.get("DCSL") + "")) {
						dao.doRemove(params, BSPHISEntryNames.WL_KCYJ);
					} else {
						records.get(0).put("GCSL",
								parseDouble(WZZDinfor.get("GCSL")));
						records.get(0).put("DCSL",
								parseDouble(WZZDinfor.get("DCSL")));
						records.get(0).put("WZXH", WZZDinfor.get("WZXH"));
						records.get(0).put("KFXH", WZZDinfor.get("KFXH"));
						dao.doSave("update", BSPHISEntryNames.WL_KCYJ,
								records.get(0), false);
					}
				}
				if (records.size() == 0) {
					if (!("0.0".equals(WZZDinfor.get("GCSL") + "") && "0.0"
							.equals(WZZDinfor.get("DCSL") + ""))) {
						Map<String, Object> record = new HashMap<String, Object>();
						record.put("GCSL", parseDouble(WZZDinfor.get("GCSL")));
						record.put("DCSL", parseDouble(WZZDinfor.get("DCSL")));
						record.put("WZXH", WZZDinfor.get("WZXH"));
						record.put("KFXH", WZZDinfor.get("KFXH"));
						dao.doSave("create", BSPHISEntryNames.WL_KCYJ, record,
								false);
					}
				}
				// 判断二级建库标志(EJJK)是否为1，如果为1则把当前库房序号插入到表WL_EJJK中
				if ("1".equals(WZZDinfor.get("EJJK") + "")) {
					Map<String, Object> record = new HashMap<String, Object>();
					Map<String, Object> parameters = new HashMap<String, Object>();
					record.put("JGID", JGID);
					record.put("KFXH", WZZDinfor.get("KFXH"));
					// 科室代码 角色为中心机构或一级库房时默认为0；如果是二级库房从一级库房引入，则为对应二级库房的科室代码
					record.put("KSDM", 0L);
					record.put("WZXH", parseInt(WZZDinfor.get("WZXH")));
					record.put("WZZT", parseInt(WZZDinfor.get("WZZT")));
					record.put("YRBZ", 0);// 引入标志 默认为0；如果是二级库房从一级库房引入则为1
					parameters.put("WZXH",
							Long.parseLong(WZZDinfor.get("WZXH") + ""));
					parameters.put("KFXH", parseInt(WZZDinfor.get("KFXH")));
					parameters.put("JGID", WZZDinfor.get("JGID") + "");
					Long l = dao
							.doCount(
									"WL_EJJK",
									"WZXH=:WZXH and KFXH=:KFXH and JGID=:JGID and KSDM=0",
									parameters);
					if (l <= 0) {
						dao.doSave("create", BSPHISEntryNames.WL_EJJK, record,
								false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败！");
		}
		res.put("WZXH", WZZDinfor.get("WZXH"));

	}

	/**
	 * 查询物资字典的厂家是否可删除
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryIfCanInvalid(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String WZXH = body.get("WZXH") + "";
		String CJXH = body.get("CJXH") + "";
		// Map<String, Object> parameters =new HashMap<String, Object>();
		// parameters.put("CJXH", CJXH);
		// parameters.put("WZXH", WZXH);
		String hqlWhere = "WZXH=" + WZXH + " and " + "CJXH=" + CJXH;
		try {
			long count = dao.doCount(BSPHISEntryNames.WL_WZKC, hqlWhere, null);
			res.put("count", count);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}

	}

	@SuppressWarnings("unchecked")
	public void doGetList(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			if (req.get("cnd") != null) {
				queryCnd = req.get("cnd") + "";
			}
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		int treasuryId = 0;
		String schemaName = "phis.application.cfg.schemas.WL_WZZD_ZBHS";
		if (req.get("KFXH") != null) {
			treasuryId = parseInt(req.get("KFXH"));
		}
		int lbxh = 0;
		if (req.get("LBXH") != null) {
			lbxh = parseInt(req.get("LBXH"));
		}
		String sql = "";
		if (req.get("isWSJ") != null) { // 卫生局(中心机构) d_wl_whgl_wzxx_zbhs_jg窗口
			sql = "SELECT WL_WZZD.WZXH as WZXH," + "WL_WZZD.JGID as JGID,"
					+ "WL_WZZD.KFXH as KFXH," + "WL_WZZD.ZBLB as ZBLB,"
					+ "WL_WZZD.HSLB as HSLB," + "WL_WZZD.WZMC as WZMC,"
					+ "WL_WZZD.WZGG as WZGG," + "WL_WZZD.WZDW as WZDW,"
					+ "WL_WZZD.PYDM as PYDM," + "WL_WZZD.WBDM as WBDM,"
					+ "WL_WZZD.JXDM as JXDM," + "WL_WZZD.QTDM as QTDM,"
					+ "WL_WZZD.WZZT as WZZT," + "WL_WZZD.KWBH as KWBH,"
					+ "WL_WZZD.BKBZ as BKBZ," + "WL_WZZD.YCWC as YCWC,"
					+ "WL_WZZD.JLBZ as JLBZ,"
					+ "nvl(WL_WZZD.GCSL,0.00) as GCSL,"
					+ "nvl(WL_WZZD.DCSL,0.00) as DCSL,"
					+ "WL_WZZD.WZTM as WZTM," + "WL_WZZD.ZDTM as ZDTM,"
					+ "WL_WZZD.GLKF aS GLKF," + "WL_WZZD.GLFS as GLFS,"
					+ "0  as GYBZ," + "WL_WZZD.SXYJ as SXYJ,"
					+ "WL_WZZD.EJJK as EJJK," + "WL_WZZD.SFBZ as SFBZ,"
					+ "WL_WZZD.ZJFF as ZJFF," + "WL_WZZD.ZJNX as ZJNX,"
					+ "nvl(WL_WZZD.ZGZL,0.00) as ZGZL,"
					+ "nvl(WL_WZZD.JCZL,0.00) as JCZL"
					+ " FROM WL_WZZD WHERE KFXH = 0";
			List<Map<String, Object>> HSLB = (List<Map<String, Object>>) req
					.get("HSLB");
			// 如果点击了左边的树，加HSLB字段过滤条件
			if (HSLB != null && HSLB.size() != 0) {
				String sql_in = "(";
				for (int i = 0; i < HSLB.size(); i++) {
					sql_in += HSLB.get(i) + ",";
				}
				String sql_ins = sql_in.substring(0, sql_in.length() - 1) + ")";
				sql += " and HSLB in" + sql_ins;
			}
			if (queryCnd != null) {
				String[] que = queryCnd.split(",");
				String textName = que[2];
				textName = textName.substring(3, textName.length() - 1);
				String textValue = que[4].substring(0, que[4].indexOf("]"));
				textValue = textValue.substring(1);
				sql += " and ( WL_WZZD." + textName + " like '" + textValue
						+ "' or WL_WZZD.PYDM like '" + textValue.toUpperCase()
						+ "' or WL_WZZD.WBDM like '" + textValue.toUpperCase()
						+ "')";
			}

		} else {// 分机构
			schemaName = "phis.application.cfg.schemas.WL_WZZD_ZBHS_JG";
			if (0 == lbxh) { // 库房的lbxh==0时，d_wl_whgl_wzxx_zbhs窗口
				sql = "SELECT WL_WZZD.WZXH as WZXH,WL_WZZD.JGID as JGID,WL_WZZD.KFXH as KFXH,WL_WZZD.ZBLB as ZBLB,WL_WZZD.HSLB as HSLB,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,"
						+ "WL_WZZD.PYDM as PYDM,WL_WZZD.WBDM as WBDM,WL_WZZD.JXDM as JXDM,WL_WZZD.QTDM as QTDM,WL_WZGS.WZZT as WZZT,WL_WZZD.KWBH as KWBH,WL_WZZD.BKBZ as BKBZ,WL_WZZD.YCWC as YCWC,"
						+ "WL_WZZD.JLBZ as JLBZ,nvl(A.GCSL,0.00) as GCSL,nvl(A.DCSL,0.00) as DCSL,WL_WZZD.WZTM as WZTM,WL_WZZD.ZDTM as ZDTM,WL_WZZD.GLKF as GLKF,WL_WZZD.GLFS as GLFS,WL_WZGS.GYBZ as GYBZ,WL_WZZD.SXYJ as SXYJ,WL_WZZD.EJJK as EJJK,WL_WZZD.SFBZ as SFBZ,"
						+ "WL_WZZD.ZJFF as ZJFF,WL_WZZD.ZJNX as ZJNX,nvl(WL_WZZD.ZGZL,0.00) as ZGZL,nvl(WL_WZZD.JCZL,0.00) as JCZL FROM WL_WZZD LEFT OUTER JOIN (SELECT WZXH,GCSL,DCSL FROM WL_KCYJ WHERE WL_KCYJ.KFXH ="
						+ treasuryId
						+ ") A ON A.WZXH = WL_WZZD.WZXH,WL_WZGS WHERE WL_WZZD.WZXH = WL_WZGS.WZXH AND WL_WZGS.KFXH ="
						+ treasuryId;
				List<Map<String, Object>> HSLB = (List<Map<String, Object>>) req
						.get("HSLB");
				// 如果点击了左边的树，加HSLB字段过滤条件
				if (HSLB != null && HSLB.size() != 0) {
					String sql_in = "(";
					for (int i = 0; i < HSLB.size(); i++) {
						sql_in += HSLB.get(i) + ",";
					}
					String sql_ins = sql_in.substring(0, sql_in.length() - 1)
							+ ")";
					sql += " and HSLB in" + sql_ins;
				}
				if (queryCnd != null) {
					String[] que = queryCnd.split(",");
					String textName = que[2];
					textName = textName.substring(3, textName.length() - 1);
					String textValue = que[4].substring(0, que[4].indexOf("]"));
					textValue = textValue.substring(1);
					sql += " and ( WL_WZZD." + textName + " like '" + textValue
							+ "' or WL_WZZD.PYDM like '"
							+ textValue.toUpperCase()
							+ "'or WL_WZZD.WBDM like '"
							+ textValue.toUpperCase() + "')";
				}
			} else {// 库房的lbxh!=0
				sql = "SELECT WL_WZZD.WZXH as WZXH,WL_WZZD.JGID as JGID,WL_WZZD.KFXH as KFXH,WL_WZZD.ZBLB as ZBLB,WL_WZZD.HSLB as HSLB,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,"
						+ " WL_WZZD.WZDW as WZDW,WL_WZZD.PYDM as PYDM,WL_WZZD.WBDM as WBDM,WL_WZZD.JXDM as JXDM,WL_WZZD.QTDM as QTDM,WL_WZGS.WZZT as WZZT,WL_WZZD.KWBH as KWBH,WL_WZZD.BKBZ as BKBZ,"
						+ " WL_WZZD.YCWC as YCWC,WL_WZZD.JLBZ as JLBZ,nvl(A.GCSL,0.00) as GCSL,nvl(A.DCSL,0.00) as DCSL,WL_WZZD.WZTM as WZTM,WL_WZZD.ZDTM as ZDTM,WL_WZZD.GLFS as GLFS,WL_FLZD.ZDXH as ZDXH,"
						+ " WL_FLZD.FLMC as FLMC,WL_FLZD.LBXH as LBXH,WL_WZZD.GLKF as GLKF, WL_WZGS.GYBZ as GYBZ,WL_WZZD.SXYJ as SXYJ,WL_WZZD.EJJK as EJJK,WL_WZZD.SFBZ as SFBZ,"
						+ "WL_WZZD.ZJFF as ZJFF,WL_WZZD.ZJNX as ZJNX,nvl(WL_WZZD.ZGZL,0.00) as ZGZL,nvl(WL_WZZD.JCZL,0.00) as JCZL FROM WL_WZGS,WL_WZZD LEFT OUTER JOIN WL_WZFL ON (WL_WZZD.WZXH = WL_WZFL.WZXH) LEFT OUTER JOIN (SELECT WZXH,GCSL,DCSL FROM WL_KCYJ  WHERE WL_KCYJ.KFXH = "
						+ treasuryId
						+ ") A ON A.WZXH = WL_WZZD.WZXH,WL_FLZD"
						+ " WHERE ( WL_WZGS.WZXH = WL_WZZD.WZXH) AND ( WL_WZFL.ZDXH = WL_FLZD.ZDXH ) AND ( WL_WZFL.LBXH = "
						+ lbxh
						+ ") AND (WL_WZGS.KFXH = "
						+ treasuryId
						+ ") AND (WL_FLZD.FLBM like '%"
						+ req.get("FLBM")
						+ "%' OR WL_FLZD.FLBM= '')";
				if (queryCnd != null) {
					String[] que = queryCnd.split(",");
					String textName = que[2];
					textName = textName.substring(3, textName.length() - 1);
					String textValue = que[4].substring(0, que[4].indexOf("]"));
					textValue = textValue.substring(1);
					sql += " and ( WL_WZZD." + textName + " like '" + textValue
							+ "' or WL_WZZD.PYDM like '"
							+ textValue.toUpperCase()
							+ "'or WL_WZZD.WBDM like '"
							+ textValue.toUpperCase() + "')";
				}
			}
		}
		try {
			sql += " order by WL_WZZD.WZXH desc";
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
			List<Map<String, Object>> listsize = dao.doSqlQuery(sql, null);

			// Dictionary dic_manageUnit =
			// Dictionaries.instance().getDic("manageUnit");
			// Dictionary dic_gender = Dictionaries.instance().getDic("gender");
			// for (int i = 0; i < list.size(); i++) {
			// list.get(i).put("JGID_text",dic_manageUnit.getText(list.get(i).get("JGID").toString()));
			// list.get(i).put("YGXB_text",dic_gender.getText(list.get(i).get("YGXB").toString()));
			// }
			SchemaUtil.setDictionaryMassageForList(list, schemaName);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
	}

	/**
	 * 查询物资信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetMaterialInformation(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long kfxh = parseLong(user.getProperty("treasuryId"));
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String TOPID = ParameterUtil.getTopUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> kfzbpar = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			if (req.get("cnd") != null) {
				queryCnd = req.get("cnd") + "";
			}
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		try {
			kfzbpar.put("KFXH", kfxh);
			Map<String, Object> kfzbMap = dao.doLoad(
					"select KFZB as KFZB from WL_KFXX where KFXH=:KFXH",
					kfzbpar);
			String kfzbval = "0";
			if (kfzbMap != null) {
				if (kfzbMap.get("KFZB") != null) {
					kfzbval = kfzbMap.get("KFZB") + "";
				}
			}
			parameters.put("first", pageNo * pageSize);
			parameters.put("max", pageSize);
			parameters.put("JGID", JGID);
			parameterssize.put("JGID", JGID);
			parameters.put("TOPJGID", TOPID);
			parameterssize.put("TOPJGID", TOPID);
			StringBuffer sql = new StringBuffer(
					"SELECT a.ZBLB as ZBLB,a.HSLB as HSLB,a.GLFS as GLFS,a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,a.PYDM as PYDM,a.WZZT as WZZT,a.WZXH as WZXH,0 as XZBZ,0 as ZDXH FROM WL_WZZD a,WL_WZBM b WHERE a.WZXH=b.WZXH and a.JGID =:TOPJGID AND a.WZZT = 1 AND a.WZXH Not In (SELECT b.WZXH From WL_WZGS b WHERE b.JGID=:JGID) and a.ZBLB in("
							+ kfzbval + ")");
			if (queryCnd != null) {
				String[] que = queryCnd.split(",");
				String textName = que[2];
				textName = textName.substring(3, textName.length() - 1);
				String textValue = que[4].substring(0, que[4].indexOf("]"));
				textValue = textValue.substring(1);
				sql.append(" and b.");
				sql.append(textName);
				sql.append(" like '");
				sql.append(textValue);
				sql.append("'");
			}

			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_WZZD + "_YR");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询物资信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetMaterialInformationEjkf(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			if (req.get("cnd") != null) {
				queryCnd = req.get("cnd") + "";
			}
		}
		if (queryCnd != null) {
			String[] que = queryCnd.split(",");
			String textName = que[2];
			String textValue = que[4].substring(0, que[4].indexOf("]"));
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int pageNo = 0;
			if (req.containsKey("pageNo")) {
				pageNo = (Integer) req.get("pageNo") - 1;
			}
			parameters.put("first", pageNo * pageSize);
			parameters.put("max", pageSize);
			parameters.put("KFXH", kfxh);
			parameterssize.put("KFXH", kfxh);
			StringBuffer sql = new StringBuffer(
					"SELECT a.QTDM as QTDM,a.JXDM as JXDM,a.WBDM as WBDM,a.PYDM as PYDM,a.WZDW as WZDW,a.WZGG as WZGG,a.WZMC as WZMC,a.HSLB as HSLB,a.ZBLB as ZBLB,a.KFXH as KFXH,a.WZXH as WZXH,a.WZZT as WZZT,a.WZTM as WZTM,0 as XZBZ,0.00 as GCSL,0.00 as DCSL FROM WL_WZZD a, WL_WZGS b WHERE ( a.WZXH > 0 ) AND ( a.GLFS = 1 ) AND ( a.EJJK = 0 OR a.EJJK IS NULL ) AND ( a.WZZT = 1 )");
			if (textName.indexOf("KFXH") > 0) {
				parameters.put("EJKFXH", textValue);
				parameterssize.put("EJKFXH", textValue);
				sql.append(" AND ( b.KFXH=:EJKFXH )");
			}
			if (textName.indexOf("PYDM") > 0) {
				parameters.put("PYDM", textValue);
				parameterssize.put("PYDM", textValue);
				sql.append(" AND ( a.PYDM=:PYDM )");
			}
			sql.append(" AND a.WZXH = b.WZXH AND ( b.WZXH NOT IN ( SELECT c.WZXH FROM WL_EJJK c WHERE c.KFXH =:KFXH))");
			try {
				list = dao.doSqlQuery(sql.toString(), parameters);
				listsize = dao.doSqlQuery(sql.toString(), parameterssize);
				SchemaUtil.setDictionaryMassageForList(list,
						BSPHISEntryNames.WL_WZZD + "_YR");
				res.put("totalCount", Long.parseLong(listsize.size() + ""));
				res.put("body", list);

			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}

	}

	// 物资的引入
	public void doSaveCallin(List<Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		int kfxh = parseInt(user.getProperty("treasuryId"));
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String WZXH = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> wzzdMap = dao.doQuery(
						"select WZXH as WZXH,1 as GYBZ from WL_WZZD where WZXH in("
								+ WZXH + ")", parameters);
				for (int i = 0; i < wzzdMap.size(); i++) {
					wzzdMap.get(i).put("JGID", JGID);
					wzzdMap.get(i).put("KFXH", kfxh);
					wzzdMap.get(i).put("WZZT", 1);
					dao.doSave("create", BSPHISEntryNames.WL_WZGS,
							wzzdMap.get(i), false);
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	// 物资的引入
	public void doSaveGDCMaterialInfo(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		double dcsl = 0.00;
		double gcsl = 0.00;
		long wzxh = 0L;
		if (body.get("WZXH") != null) {
			wzxh = Long.parseLong(body.get("WZXH") + "");
		}
		if (body.get("DCSL") != null) {
			dcsl = parseDouble(body.get("DCSL"));
		}
		if (body.get("GCSL") != null) {
			gcsl = parseDouble(body.get("GCSL"));
		}
		Map<String, Object> parametersCount = new HashMap<String, Object>();
		Map<String, Object> parametersSave = new HashMap<String, Object>();
		parametersCount.put("KFXH", kfxh);
		parametersCount.put("WZXH", wzxh);
		parametersSave.put("KFXH", kfxh);
		parametersSave.put("WZXH", wzxh);
		parametersSave.put("DCSL", dcsl);
		parametersSave.put("GCSL", gcsl);
		try {
			Long l = dao.doCount("WL_KCYJ", "WZXH=:WZXH and KFXH=:KFXH",
					parametersCount);
			if (l <= 0) {
				dao.doSave("create", BSPHISEntryNames.WL_KCYJ, parametersSave,
						false);
			} else {
				dao.doSave("update", BSPHISEntryNames.WL_KCYJ, parametersSave,
						false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	// 物资的引入
	public void doSaveCallinEjkf(List<Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		long ejkf = Long.parseLong(user.getProperty("treasuryEjkf") + "");
		int kfxh = parseInt(user.getProperty("treasuryId"));
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String WZXH = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> wzzdMap = dao.doQuery(
						"select WZZT as WZZT,WZXH as WZXH,1 as YRBZ from WL_WZZD where WZXH in("
								+ WZXH + ")", parameters);
				for (int i = 0; i < wzzdMap.size(); i++) {
					wzzdMap.get(i).put("KSDM", ejkf);
					wzzdMap.get(i).put("JGID", JGID);
					wzzdMap.get(i).put("KFXH", kfxh);
					dao.doSave("create", BSPHISEntryNames.WL_EJJK,
							wzzdMap.get(i), false);
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	public void doUpdateCanceledMaterial(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null) {
			kfxh = parseInt(user.getProperty("treasuryId"));
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterswzgs = new HashMap<String, Object>();
		String wzzt = body.get("WZZT") + "";
		int gybz = parseInt(body.get("GYBZ"));
		try {
			// 在用的物质不能注销 by dingcj
			int ZT = parseInt(wzzt);
			if (ZT == -1) {
				parameterswzgs.put("KFXH", kfxh);
				parameterswzgs.put("WZXH",
						Long.parseLong(body.get("WZXH").toString()));
				long l = dao.doCount("WL_WZKC", "KFXH=:KFXH and WZXH=:WZXH",
						parameterswzgs);
				if (l > 0) {
					res.put(Service.RES_CODE, 201);
					res.put(Service.RES_MESSAGE, "物资有库存不能注销!");
					throw new RuntimeException("物资有库存不能注销!");
				}
			}
			if (gybz == 1) {
				parameterswzgs.put("KFXH", kfxh);
				parameterswzgs.put("WZXH",
						Long.parseLong(body.get("WZXH").toString()));
				Map<String, Object> wzgsMap = dao
						.doLoad("select WZZT as WZZT from WL_WZGS where KFXH=:KFXH and WZXH=:WZXH",
								parameterswzgs);
				if (parseInt(wzgsMap.get("WZZT")) == 1
						|| parseInt(wzgsMap.get("WZZT")) == 0) {
					dao.doUpdate(
							"update WL_WZGS set WZZT=-1 where KFXH=:KFXH and WZXH=:WZXH",
							parameterswzgs);
				} else {
					dao.doUpdate(
							"update WL_WZGS set WZZT=1 where KFXH=:KFXH and WZXH=:WZXH",
							parameterswzgs);
				}
			} else {
				parameters.put("WZXH",
						Long.parseLong(body.get("WZXH").toString()));
				if (kfxh != 0) {
					parameterswzgs.put("KFXH", kfxh);
					parameterswzgs.put("WZXH",
							Long.parseLong(body.get("WZXH").toString()));
					Map<String, Object> wzgsMap = dao
							.doLoad("select WZZT as WZZT from WL_WZGS where KFXH=:KFXH and WZXH=:WZXH",
									parameterswzgs);
					if (parseInt(wzgsMap.get("WZZT")) == 1
							|| parseInt(wzgsMap.get("WZZT")) == 0) {
						dao.doUpdate(
								"update WL_WZGS set WZZT=-1 where KFXH=:KFXH and WZXH=:WZXH",
								parameterswzgs);
					} else {
						dao.doUpdate(
								"update WL_WZGS set WZZT=1 where KFXH=:KFXH and WZXH=:WZXH",
								parameterswzgs);
					}
				}
				// 已经注销
				if ("1".equalsIgnoreCase(wzzt)) {
					// 取消注销
					parameters.put("WZZT", 1);
					dao.doUpdate("update " + schemaList
							+ " set WZZT=:WZZT where WZXH=:WZXH", parameters);
					res.put(Service.RES_CODE, 601);
					res.put(Service.RES_MESSAGE, "取消注销成功");
				} else {
					// 注销
					parameters.put("WZZT", -1);
					dao.doUpdate("update " + schemaList
							+ " set WZZT=:WZZT WHERE WZXH=:WZXH", parameters);
					res.put(Service.RES_CODE, 602);
					res.put(Service.RES_MESSAGE, "注销成功");
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销失败");
		}
	}

	/**
	 * EJJK室信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetEJJKInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		int kfxh = parseInt(user.getProperty("treasuryId"));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select distinct a.JLXH as JLXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,c.GCSL as GCSL,c.DCSL as DCSL,a.WZZT as WZZT,a.JGID as JGID,a.KFXH as KFXH,a.KSDM as KSDM,a.WZXH as WZXH,a.YRBZ as YRBZ from WL_EJJK a left outer join WL_KCYJ c on a.KFXH=c.KFXH and a.WZXH=c.WZXH,WL_WZZD b where a.WZXH=b.WZXH and (a.KFXH=:KFXH or (a.JGID=:JGID AND a.KSDM=0) or a.KFXH=0)");
		parameters.put("KFXH", kfxh);
		parameterssize.put("KFXH", kfxh);
		parameters.put("JGID", JGID);
		parameterssize.put("JGID", JGID);
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and ( b."
					+ que[2].substring(3, que[2].indexOf("]"))
					+ " like '"
					+ que[4].substring(0, que[4].indexOf("]")).trim()
					+ "'"
					+ " or b.PYDM like '"
					+ que[4].substring(0, que[4].indexOf("]")).trim()
							.toUpperCase()
					+ "'"
					+ " or b.WBDM like '"
					+ que[4].substring(0, que[4].indexOf("]")).trim()
							.toUpperCase() + "' )";

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_EJJK);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2012-7-20
	 * @description 修改库存预警的高低储
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateKCYJ(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("WL_KCYJ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterkcyjsave = new HashMap<String, Object>();
		Map<String, Object> parameterkcyjuppdate = new HashMap<String, Object>();
		int kfxh = 0;
		UserRoleToken user = UserRoleToken.getCurrent();
		kfxh = parseInt(user.getProperty("treasuryId"));
		for (int i = 0; i < meds.size(); i++) {
			Long wzxh = 0L;
			double dcsl = 0.00;
			double gcsl = 0.00;
			if (meds.get(i).get("WZXH") != null) {
				wzxh = Long.parseLong(meds.get(i).get("WZXH") + "");
			}
			if (meds.get(i).get("DCSL") != null) {
				dcsl = parseDouble(meds.get(i).get("DCSL"));
			}
			if (meds.get(i).get("GCSL") != null) {
				gcsl = parseDouble(meds.get(i).get("GCSL"));
			}
			parameters.put("KFXH", kfxh);
			parameters.put("WZXH", wzxh);
			try {
				Long l = dao.doCount("WL_KCYJ", "KFXH=:KFXH and WZXH=:WZXH",
						parameters);
				if (l > 0) {
					parameterkcyjuppdate.put("KFXH", kfxh);
					parameterkcyjuppdate.put("WZXH", wzxh);
					parameterkcyjuppdate.put("DCSL", dcsl);
					parameterkcyjuppdate.put("GCSL", gcsl);
					dao.doUpdate(
							"update WL_KCYJ set DCSL=:DCSL,GCSL=:GCSL where KFXH=:KFXH and WZXH=:WZXH",
							parameterkcyjuppdate);
				} else {
					parameterkcyjsave.put("KFXH", kfxh);
					parameterkcyjsave.put("WZXH", wzxh);
					parameterkcyjsave.put("DCSL", dcsl);
					parameterkcyjsave.put("GCSL", gcsl);
					dao.doSave("create", BSPHISEntryNames.WL_KCYJ,
							parameterkcyjsave, false);
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			} catch (ValidateException e) {
				e.printStackTrace();
			}
		}
	}

	public void doUpdateCanceledEjjk(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterswzgs = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null) {
			kfxh = parseInt(user.getProperty("treasuryId"));
		}
		String wzzt = body.get("WZZT") + "";
		try {
			int ZT = parseInt(wzzt);
			if (ZT == -1) {
				parameterswzgs.put("KFXH", kfxh);
				parameterswzgs.put("WZXH",
						Long.parseLong(body.get("WZXH").toString()));
				long l = dao.doCount("WL_WZKC", "KFXH=:KFXH and WZXH=:WZXH",
						parameterswzgs);
				if (l > 0) {
					res.put(Service.RES_CODE, 201);
					res.put(Service.RES_MESSAGE, "物资有库存不能注销!");
					throw new RuntimeException("物资有库存不能注销!");
				}
			}

			parameters.put("JLXH", Long.parseLong(body.get("JLXH").toString()));
			// 已经注销
			if ("1".equalsIgnoreCase(wzzt)) {
				// 取消注销
				parameters.put("WZZT", 1);
				dao.doUpdate("update " + schemaList
						+ " set WZZT=:WZZT where JLXH=:JLXH", parameters);
				res.put(Service.RES_CODE, 601);
				res.put(Service.RES_MESSAGE, "取消注销成功");
			} else {
				// 注销
				parameters.put("WZZT", -1);
				dao.doUpdate("update " + schemaList
						+ " set WZZT=:WZZT WHERE JLXH=:JLXH", parameters);
				res.put(Service.RES_CODE, 602);
				res.put(Service.RES_MESSAGE, "注销成功");
			}

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销失败");
		}
	}

	/**
	 * 查询类别序号信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryHSLB(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (req.get("ZBLB") != null) {
			parameters.put("ZBLB", parseInt(req.get("ZBLB")));
		}
		try {
			List<Map<String, Object>> hslblsit = dao
					.doSqlQuery(
							"select HSLB as HSLB from WL_HSLB where ZBLB=:ZBLB and HSLB not in (select SJHS from WL_HSLB)",
							parameters);
			res.put("body", hslblsit);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}

	}

	public double parseDouble(Object o) {
		if (o == null || o == "") {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null || o == "") {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null || o == "") {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
}
