package phis.application.fsb.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.validator.ValidateException;

public class FsbCostProcessingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public FsbCostProcessingModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doQueryItemInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String cndValue = (String) req.get("cndValue");
		String cndName = (String) req.get("cndName");
		String sql = "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("manaUnitId", manaUnitId);
		if ("ZYHM".equals(cndName)) {
			String ZYHM = BSPHISUtil.get_public_fillleft(cndValue, "0",
					BSPHISUtil.getRydjNo(manaUnitId, "JCHM", "", dao).length());
			parameters.put("ZYHM", ZYHM);
			sql = "select a.ZYH as ZYH, a.ZYHM as ZYHM,a.BRXM as BRXM,a.BRXB as BRXB,a.BRXZ as BRXZ,a.KSRQ as KSRQ,  a.JSRQ as JSRQ,a.JSCS as JSCS from JC_BRRY a where a.CYPB < 8 AND a.JGID = :manaUnitId AND a.ZYHM = :ZYHM ORDER BY a.ZYHM";
		}
//		if ("BRCH".equals(cndName)) {
//			parameters.put("BRCH", cndValue);
//			sql = "select a.ZYH as ZYH, a.ZYHM as ZYHM,a.BRCH as BRCH,a.BRXM as BRXM,a.BRXB as BRXB,a.BRXZ as BRXZ,a.BRKS as BRKS,a.RYRQ as RYRQ,  a.CYRQ as CYRQ,a.ZSYS as ZSYS,a.BRBQ as BRBQ,a.JSCS as JSCS from JC_BRRY a where a.CYPB < 8 AND a.JGID = :manaUnitId AND a.BRCH = :BRCH ORDER BY a.ZYHM";
//		}
		try {
			List<Map<String, Object>> ListPerson = dao.doQuery(sql, parameters);
			if (ListPerson.size() == 1) {
				Map<String, Object> person = ListPerson.get(0);
				res.put("body", person);

			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 获取自负比例
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCost(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> setBody = new HashMap<String, Object>();
		setBody.put("BRXZ", body.get("BRXZ"));
		setBody.put("FYXH", body.get("FYXH"));
		if (body.get("isZT") != "" && parseInt(body.get("isZT")) == 1) {
			// 项目组套
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZTBH", body.get("FYXH"));
			parameters.put("JGID", manageUnit);
			try {
				StringBuffer hql = new StringBuffer("");
				hql.append("select a.FYXH as FYXH,a.FYMC as FYMC,a.FYDW as FYDW,a.FYGB as FYGB,e.XMSL as XMSL,round(c.FYDJ,4) as FYDJ,");
				hql.append("c.FYKS as FYKS,d.OFFICENAME as CDMC,");
				hql.append(" 0 as TYPE,0 as YPLX ,e.XMSL as XMSL from ");
				hql.append("GY_YLSF");
				hql.append(" a,");
				hql.append("GY_FYBM");
				hql.append(" b,");
				hql.append("GY_YLMX c");
				hql.append(" inner join YS_MZ_ZT02 e on e.ZTBH=:ZTBH and e.XMBH=c.FYXH");
				hql.append(" left outer join ");
				hql.append("SYS_Office");
				hql.append(" d on d.ID = c.FYKS where a.FYXH = b.FYXH AND a.FYXH = c.FYXH and c.jgid=:JGID ");
				// hql.append(" and  c.FYXH in ( select XMBH from YS_MZ_ZT02 e where e.ZTBH=:ZTBH)");
				List<Map<String, Object>> fyList = dao.doSqlQuery(
						hql.toString(), parameters);
				Double total = 0d;
				String Errmsg = "";
				for (Map<String, Object> fyMap : fyList) {
					if ("null".equals(fyMap.get("CDMC") + "")) {
						fyMap.put("CDMC", "");
					}
					if ("null".equals(fyMap.get("FYDW") + "")) {
						fyMap.put("FYDW", "");
					}
					if ("null".equals(fyMap.get("FYKS") + "")) {
						fyMap.put("FYKS", "");
					}
					if(parseDouble(fyMap.get("FYDJ"))==0){
						Errmsg = fyMap.get("FYMC")+"的单价为0不能记账!";
					}
					total += parseDouble(fyMap.get("FYDJ"))*parseDouble(fyMap.get("XMSL"));
					setBody.put("FYXH", fyMap.get("FYXH"));
					setBody.put("TYPE", 0);
					setBody.put("FYGB", fyMap.get("FYGB"));
					Map<String, Object> ZFBL = BSPHISUtil.getzfbl(setBody, ctx,
							dao);
					fyMap.put("ZFBL", ZFBL.get("ZFBL"));
				}
				if(Errmsg.length()>0){
					res.put("Errmsg", Errmsg);
				}
				res.put("fyList", fyList);
				res.put("total", total);
				// res.put("ZFBL", fyList.get(0).get("ZFBL"));
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		} else {
			if (body.get("TYPE") != null
					&& (body.get("TYPE") + "").length() > 0) {// 药品
				setBody.put("TYPE", body.get("TYPE"));
				setBody.put("FYGB", 0);
				Map<String, Object> ZFBL = BSPHISUtil
						.getzfbl(setBody, ctx, dao);
				res.put("body", ZFBL);
			} else {// 费用
				setBody.put("TYPE", 0);
				setBody.put("FYGB", body.get("FYGB"));
				Map<String, Object> ZFBL = BSPHISUtil
						.getzfbl(setBody, ctx, dao);
				res.put("body", ZFBL);
			}
		}
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public Double parseDouble(Object o) {
		if (o == null) {
			return 0d;
		}
		return Double.parseDouble(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	@SuppressWarnings("unchecked")
	public void doSaveCost(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			String userId = (String) user.getUserId();
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			if (body.get("isZT") != "" && parseInt(body.get("isZT")) == 1) {
				// 项目组套
				List<Map<String, Object>> fyList = (List<Map<String, Object>>) body
						.get("fyList");
				Date ldt_now = new Date();
				long ZYH = parseLong(body.get("ZYH"));
				long BRXZ = parseLong(body.get("BRXZ"));
				long ZXKS = 0;
				if (body.containsKey("ZXKS")&&body.get("ZXKS")!="") {
					ZXKS = parseLong(body.get("ZXKS"));
				}
//				long FYKS = parseLong(body.get("FYKS"));
				// long FYXM = Long.parseLong(body.get("FYXM") + "");
				double FYSL = parseDouble(body.get("FYSL"));
				// 执行科室为空时，默认是费用科室
//				if (FYSL > 0 && ZXKS == 0) {
//					ZXKS = FYKS;
//				}
				// 医院病人在院期间所有的费用都记入主诊医生的名下, 当主诊医生为空时,执行医生记入开嘱医生名下
				String ls_ysgh_kzys = body.get("YSGH") + "";

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZYH", ZYH);
				String YSGH = "";
				if (!ls_ysgh_kzys.equals("null")) {
					YSGH = ls_ysgh_kzys;
				}
				Date ldt_fyrq = ldt_now;
				if (body.containsKey("FYRQ")) {
					ldt_fyrq = BSHISUtil.toDate(body.get("FYRQ") + "");
				}
				Date JFRQ = ldt_now;
				if (body.containsKey("JFRQ")) {
					JFRQ = BSHISUtil.toDate(body.get("JFRQ") + "");
				}

				for (Map<String, Object> fyMap : fyList) {
					int YPLX = parseInt(fyMap.get("YPLX"));
					long FYXH = parseLong(fyMap.get("FYXH"));
					double FYDJ = parseDouble(fyMap.get("FYDJ"));
					double XMSL = parseDouble(fyMap.get("XMSL"));
					if(FYDJ==0){
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "组套明细的【"+fyMap.get("FYMC")+"】费用单价为0，不能保存！");
					}
					long FYXM = BSPHISUtil.getfygb(YPLX, FYXH, dao, ctx);
					if (FYSL < 0) {
						// 验证是否有正的记录
						String sql = "select a.ZYH as ZYH,a.FYXH as FYXH,sum(a.FYSL) as FYSL,sum(a.ZFJE) as ZFJE,"
								+ " sum(a.ZJJE) as ZJJE,sum(a.ZLJE) as ZLJE from JC_FYMX a where a.JGID =:JGID and a.ZYH =:ZYH"
								+ " AND a.YPCD = 0 AND a.YPLX = 0 and a.FYXH =:FYXH and a.JSCS = 0"
								+ " group by a.ZYH,a.FYXH";
						Map<String, Object> parametersV = new HashMap<String, Object>();
						parametersV.put("JGID", JGID);
						parametersV.put("ZYH", ZYH);
						parametersV.put("FYXH", FYXH);
						Map<String, Object> vMap = dao.doLoad(sql, parametersV);
						if (vMap == null) {
							res.put("isCannotFindRecord", 1);
							return;
						}
						if (parseDouble(vMap.get("FYSL")) + FYSL < 0) {
							res.put("isNotEnough", 1);
							return;
						}
					}
					Map<String, Object> iu_fymx = new HashMap<String, Object>();
					iu_fymx.put("fyxm", FYXM);
					if (FYSL < 0) {
						iu_fymx.put("ZFBL", fyMap.get("ZFBL"));
						iu_fymx.put("ZJJE", FYDJ * FYSL * XMSL);
						iu_fymx.put("ZFJE", FYDJ * FYSL * XMSL * parseDouble(fyMap.get("ZFBL")));
						iu_fymx.put("ZLJE", FYDJ * FYSL * XMSL * (1 - parseDouble(fyMap.get("ZFBL"))));
					} else {
						if (body.containsKey("ZFJE")
								&& Double.parseDouble(body.get("ZFJE") + "") > 0) {
							iu_fymx.put("ZFBL", fyMap.get("ZFBL"));
							iu_fymx.put("ZJJE", fyMap.get("ZJJE"));
							iu_fymx.put("ZFJE", fyMap.get("ZFJE"));
							iu_fymx.put("ZLJE", fyMap.get("ZLJE"));
						} else {
							Map<String, Object> reje = BSPHISUtil.getje(YPLX,
									BRXZ, FYXH, FYXM, FYDJ, FYSL * XMSL, dao, ctx);
							iu_fymx.put("ZFBL", reje.get("ZFBL"));
							iu_fymx.put("ZJJE", reje.get("ZJJE"));
							iu_fymx.put("ZFJE", reje.get("ZFJE"));
							iu_fymx.put("ZLJE", reje.get("ZLJE"));
						}
					}
					if (YPLX == 0) {
						iu_fymx.put("YPCD", 0);
					} else {
						iu_fymx.put("YPCD", body.get("YPCD"));
					}
					if (body.get("YZXH") != null) {
						iu_fymx.put("YZXH", body.get("YZXH"));
					} else {
						iu_fymx.put("YZXH", 0);
					}

					iu_fymx.put("ZYH", ZYH);
					iu_fymx.put("FYRQ", ldt_fyrq);
					iu_fymx.put("FYXH", FYXH);
					iu_fymx.put("FYXM", FYXM);
					iu_fymx.put("FYMC", fyMap.get("FYMC"));
					iu_fymx.put("FYSL", parseDouble(fyMap.get("XMSL"))*FYSL);
					iu_fymx.put("FYDJ", FYDJ);
					iu_fymx.put("SRGH", userId);
					iu_fymx.put("QRGH", userId);
					iu_fymx.put("ZXKS", ZXKS);
					iu_fymx.put("JFRQ", JFRQ);
					iu_fymx.put("XMLX", body.get("XMLX"));
					iu_fymx.put("YPLX", YPLX);
					iu_fymx.put("YSGH", YSGH);
					iu_fymx.put("FYBQ", body.get("FYBQ"));
					iu_fymx.put("JSCS", 0);
					iu_fymx.put("YEPB", 0);
					iu_fymx.put("JGID", JGID);
					iu_fymx.put("DZBL", 0);
					iu_fymx.put("ZTBH", parseLong(body.get("ZTBH")));
					dao.doSave("create", BSPHISEntryNames.JC_FYMX, iu_fymx,
							false);
				}
			} else {
				Date ldt_now = new Date();
				long ZYH = Long.parseLong(body.get("ZYH") + "");
				int YPLX = (Integer) body.get("YPLX");
				long FYXH = Long.parseLong(body.get("FYXH") + "");
				long BRXZ = Long.parseLong(body.get("BRXZ") + "");
				long ZXKS = 0;
				if (body.containsKey("ZXKS")&&body.get("ZXKS")!=null&&!"".equals(body.get("ZXKS")+"")) {
					ZXKS = Long.parseLong(body.get("ZXKS") + "");
				}
				// long FYXM = Long.parseLong(body.get("FYXM") + "");
				double FYSL = Double.parseDouble(body.get("FYSL") + "");
				double FYDJ = Double.parseDouble(body.get("FYDJ") + "");
				// 执行科室为空时，默认是费用科室
//				if (FYSL > 0 && ZXKS == 0) {
//					ZXKS = FYKS;
//				}
				// 医院病人在院期间所有的费用都记入主诊医生的名下, 当主诊医生为空时,执行医生记入开嘱医生名下
				String ls_ysgh_kzys = body.get("YSGH") + "";

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZYH", ZYH);
				String YSGH = "";
				if (!ls_ysgh_kzys.equals("null")) {
					YSGH = ls_ysgh_kzys;
				}
				long FYXM = BSPHISUtil.getfygb(YPLX, FYXH, dao, ctx);
				Map<String, Object> iu_fymx = new HashMap<String, Object>();
				iu_fymx.put("fyxm", FYXM);
				if (YPLX == 0) {// 表示费用
					if (FYSL < 0) {
						iu_fymx.put("ZFBL", body.get("ZFBL"));
						iu_fymx.put("ZJJE", body.get("ZJJE"));
						iu_fymx.put("ZFJE", body.get("ZFJE"));
						iu_fymx.put("ZLJE", body.get("ZLJE"));
					} else {
						if (body.containsKey("ZFJE")
								&& Double.parseDouble(body.get("ZFJE") + "") > 0) {
							iu_fymx.put("ZFBL", body.get("ZFBL"));
							iu_fymx.put("ZJJE", body.get("ZJJE"));
							iu_fymx.put("ZFJE", body.get("ZFJE"));
							iu_fymx.put("ZLJE", body.get("ZLJE"));
						} else {
							Map<String, Object> reje = BSPHISUtil.getje(YPLX,
									BRXZ, FYXH, FYXM, FYDJ, FYSL, dao, ctx);
							iu_fymx.put("ZFBL", reje.get("ZFBL"));
							iu_fymx.put("ZJJE", reje.get("ZJJE"));
							iu_fymx.put("ZFJE", reje.get("ZFJE"));
							iu_fymx.put("ZLJE", reje.get("ZLJE"));
						}
					}
				} else {// 表示药品
					if (FYSL < 0) { // 退费时退费数据与计费时一样
						iu_fymx.put("ZFBL", body.get("ZFBL"));
						iu_fymx.put("ZJJE", body.get("ZJJE"));
						iu_fymx.put("ZFJE", body.get("ZFJE"));
						iu_fymx.put("ZLJE", body.get("ZLJE"));
					} else {
						// 有自负金额表示传入的对象中已经进行了计算
						if (body.containsKey("ZFJE")
								&& Double.parseDouble(body.get("ZFJE") + "") > 0) {
							iu_fymx.put("FYXM", body.get("FYXM"));
							iu_fymx.put("ZFBL", body.get("ZFBL"));
							iu_fymx.put("ZJJE", body.get("ZJJE"));
							iu_fymx.put("ZFJE", body.get("ZFJE"));
							iu_fymx.put("ZLJE", body.get("ZLJE"));
						} else {
							Map<String, Object> reje = BSPHISUtil.getje(YPLX,
									BRXZ, FYXH, FYXM, FYDJ, FYSL, dao, ctx);
							FYXM = Long.parseLong(reje.get("FYGB") + "");
							iu_fymx.put("FYXM", FYXM);
							iu_fymx.put("ZFBL", reje.get("ZFBL"));
							iu_fymx.put("ZJJE", reje.get("ZJJE"));
							iu_fymx.put("ZFJE", reje.get("ZFJE"));
							iu_fymx.put("ZLJE", reje.get("ZLJE"));
						}
					}
				}
				Date ldt_fyrq = ldt_now;
				if (body.containsKey("FYRQ")) {
					ldt_fyrq = BSHISUtil.toDate(body.get("FYRQ") + "");
				}
				Date JFRQ = ldt_now;
				if (body.containsKey("JFRQ")) {
					JFRQ = BSHISUtil.toDate(body.get("JFRQ") + "");
				}

				if (YPLX == 0) {
					iu_fymx.put("YPCD", 0);
				} else {
					iu_fymx.put("YPCD", body.get("YPCD"));
				}
				if (body.get("YZXH") != null) {
					iu_fymx.put("YZXH", body.get("YZXH"));
				} else {
					iu_fymx.put("YZXH", 0);
				}

				iu_fymx.put("ZYH", ZYH);
				iu_fymx.put("FYRQ", ldt_fyrq);
				iu_fymx.put("FYXH", FYXH);
				iu_fymx.put("FYXM", FYXM);
				iu_fymx.put("FYMC", body.get("FYMC"));
				iu_fymx.put("FYSL", FYSL);
				iu_fymx.put("FYDJ", FYDJ);
				iu_fymx.put("SRGH", userId);
				iu_fymx.put("QRGH", userId);
				iu_fymx.put("ZXKS", ZXKS);
				iu_fymx.put("JFRQ", JFRQ);
				iu_fymx.put("XMLX", body.get("XMLX"));
				iu_fymx.put("YPLX", YPLX);
				iu_fymx.put("YSGH", YSGH);
				iu_fymx.put("FYBQ", body.get("FYBQ"));
				iu_fymx.put("JSCS", 0);
				iu_fymx.put("YEPB", 0);
				iu_fymx.put("JGID", JGID);
				iu_fymx.put("DZBL", 0);
				dao.doSave("create", BSPHISEntryNames.JC_FYMX, iu_fymx, false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用信息保存失败");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用信息保存失败");
		}
	}

	public void doQueryRefundInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		long ZYH = (Integer) body.get("ZYH");
		long FYXH = (Integer) body.get("FYXH");
		long YPCD = 0L;
		if (!body.get("YPCD").equals("")) {
			YPCD = (Integer) body.get("YPCD");
		}
		Integer YPLX = (Integer) body.get("YPLX");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> jscsParameters = new HashMap<String, Object>();
		jscsParameters.put("ZYH", ZYH);
		jscsParameters.put("JGID", JGID);
		parameters.put("JGID", JGID);
		parameters.put("ZYH", ZYH);
		parameters.put("FYXH", FYXH);
		parameters.put("YPCD", YPCD);
		parameters.put("YPLX", YPLX);
		try {
			Map<String, Object> JSCS = dao
					.doLoad("SELECT Max(JSCS) as JSCS From ZY_ZYJS Where ZYH = :ZYH AND JGID = :JGID",
							jscsParameters);
			if (JSCS.get("JSCS") != null) {
				parameters.put("JSCS", JSCS);
			} else {
				parameters.put("JSCS", 0);
			}
		} catch (PersistentDataOperationException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "结算次数查询失败");
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select a.ZYH as ZYH,   a.FYXH as FYXH,  a.FYMC as FYMC,a.YPCD as YPCD,a.ZFBL as ZFBL,a.FYDJ as FYDJ,a.DZBL as DZBL,a.YZXH as YZXH,a.ZXKS as ZXKS,a.FYBQ as FYBQ,sum(a.FYSL) as FYSL,sum(a.ZFJE) as ZFJE,sum(a.ZJJE) as ZJJE,sum(a.ZLJE) as ZLJE,0.00 as TYSL from JC_FYMX  a");
		hql.append(" where a.JGID = :JGID and  a.ZYH  = :ZYH  AND  a.YPCD = :YPCD  AND  a.YPLX = :YPLX and  a.FYXH = :FYXH and a.JSCS = :JSCS  ");
		hql.append("group by  a.ZYH,a.FYXH,a.FYMC,   a.YPCD,   a.ZFBL,a.FYDJ, a.DZBL,a.YZXH,a.FYKS,a.ZXKS,a.FYBQ");
		try {
			List<Map<String, Object>> ListRecords = dao.doQuery(hql.toString(),
					parameters);
			res.put("body", ListRecords);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用明细查询失败");
		}
	}

	public void doQueryRefundInfo1(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		@SuppressWarnings("unchecked")
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> JSCSparameter = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("FYLR");
		long ZYH = (Integer) body.get("ZYH");
		long FYXH = (Integer) body.get("FYXH");
		long YPCD = 0L;
		if (!body.get("YPCD").equals("")) {
			YPCD = (Integer) body.get("YPCD");
		}
		Integer YPLX = (Integer) body.get("YPLX");
		parameters.put("ZYH", ZYH);
		parameters.put("FYXH", FYXH);
		parameters.put("YPCD", YPCD);
		parameters.put("YPLX", YPLX);
		parameters.put("JGID", JGID);
		JSCSparameter.put("JGID", JGID);
		JSCSparameter.put("ZYH", ZYH);
		// try {
		// Map<String, Object> JSCS = dao
		// .doLoad("SELECT Max(JSCS) as JSCS From ZY_ZYJS Where  ZFPB=0 and JGID = :JGID  and ZYH = :ZYH",
		// JSCSparameter);
		// if (JSCS.get("JSCS") != null) {
		// parameters.put("JSCS", JSCS.get("JSCS"));
		// } else {
		parameters.put("JSCS", 0);
		// }
		// } catch (PersistentDataOperationException e1) {
		// throw new ModelDataOperationException(
		// ServiceCode.CODE_DATABASE_ERROR, "结算次数查询失败");
		// }
		StringBuffer hql = new StringBuffer();
		hql.append("select a.ZYH as ZYH,   a.FYXH as FYXH,  a.FYMC as FYMC,a.YPCD as YPCD,a.ZFBL as ZFBL,a.FYDJ as FYDJ,a.DZBL as DZBL,a.YZXH as YZXH,a.ZXKS as ZXKS,sum(a.FYSL) as FYSL,sum(a.ZFJE) as ZFJE,sum(a.ZJJE) as ZJJE,sum(a.ZLJE) as ZLJE,0.00 as TYSL from JC_FYMX  a");
		hql.append(" where a.JGID = :JGID and  a.ZYH  = :ZYH  AND  a.YPCD = :YPCD  AND  a.YPLX = :YPLX and  a.FYXH = :FYXH and a.JSCS = :JSCS  ");
		String JCKCGL = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.JCKCGL, ctx);
		if(!"1".equals(JCKCGL)){
			hql.append(" and a.XMLX = 4 ");
		}
		hql.append("group by  a.ZYH,a.FYXH,a.FYMC,   a.YPCD,   a.ZFBL,a.FYDJ, a.DZBL,a.YZXH,a.ZXKS");
		try {
			List<Map<String, Object>> ListRecords = dao.doQuery(hql.toString(),
					parameters);
			if (YPLX != 0) {
				parameters.remove("YPLX");
				parameters.remove("JSCS");
				String BFTYList = " SELECT a.ZYH as ZYH,a.YPXH as YPXH,  a.YPCD as YPCD,  a.YZID as YZID,a.TYBQ as TYBQ,SUM(a.YPSL) as TYSL FROM BQ_TYMX a WHERE "
						+ "( a.JGID = :JGID ) AND ( a.ZYH  = :ZYH ) AND ( a.YPXH = :FYXH ) AND ( a.YPCD = :YPCD ) AND( a.TYRQ IS NULL ) GROUP BY a.ZYH,a.YPXH,a.YPCD,a.YZID,a.TYBQ";
				List<Map<String, Object>> TymxList = dao.doQuery(BFTYList,
						parameters);

				for (Map<String, Object> map : ListRecords) {
					String cdmc = DictionaryController.instance()
							.getDic("phis.dictionary.medicinePlace")
							.getText(map.get("YPCD") + "");
					map.put("YPCD_text", cdmc);
					double TYSL = 0d;
					for (Map<String, Object> BqMap : TymxList) {
						if ((map.get("YZXH") + "").equals(BqMap.get("YZID")
								+ "")
								&& (map.get("FYXH") + "").equals(BqMap
										.get("YPXH") + "")
								&& (map.get("YPCD") + "").equals(BqMap
										.get("YPCD") + "")
								&& (map.get("FYBQ") + "").equals(BqMap
										.get("TYBQ") + "")) {
							TYSL = TYSL + (Double) BqMap.get("TYSL");
						}
					}
					map.put("TYSL", TYSL);
				}
			}
			// for (Map<String, Object> map : ListRecords) {
			// System.out.println(111);
			// if(!((Double)map.get("FYSL")-(Double)map.get("TYSL")>0D)){
			// ListRecords.remove(map);
			// }
			// }
			double KTSL = 0;
			for (int i = 0; i < ListRecords.size(); i++) {
				Map<String, Object> map = ListRecords.get(i);
				KTSL = (Double) map.get("FYSL") + (Double) map.get("TYSL");
				ListRecords.get(i).put("FYSL", KTSL);
				if (KTSL <= 0) {
					ListRecords.remove(map);
					i--;
				}
			}
			res.put("body", ListRecords);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用明细查询失败");
		}
	}

	public void doQueryCostList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameter = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String beginDate = (String) body.get("FYRQFrom");
		String endDate = (String) body.get("FYRQTo");
		String ygdm = (String) body.get("SRGH");
		String ZYHM = (String) body.get("ZYHM");
		String ZXKS = (String) body.get("ZXKS");
		parameter.put("manaUnitId", manaUnitId);
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		parameterssize.put("manaUnitId", manaUnitId);
		parameterssize.put("beginDate", beginDate);
		parameterssize.put("endDate", endDate);
		String sql = "SELECT b.ZYHM as ZYHM, b.BRXM as BRXM,a.JFRQ as JFRQ, a.FYRQ as FYRQ,a.YPLX as YPLX,a.FYXH as FYXH," +
				" a.FYMC as FYMC,a.FYSL as FYSL,a.FYDJ as FYDJ,a.ZFBL as ZFBL,c.PERSONNAME as YGXM,a.JGID as JGID," +
				" a.ZJJE as ZJJE,a.ZXKS as ZXKS FROM JC_FYMX  a,JC_BRRY   b,SYS_Personnel c" +
				" WHERE (a.SRGH = c.PERSONID) AND (a.XMLX = 4) AND ( a.ZYH = b.ZYH )  AND ( a.JGID = b.JGID ) AND  ( a.JGID = :manaUnitId )  ";
		sql += "and( to_char(a.JFRQ,'yyyy-mm-dd')>=:beginDate ) and(to_char(a.JFRQ ,'yyyy-mm-dd') <=:endDate )";
		if (ygdm != null && !"".equals(ygdm)) {
			parameter.put("ygdm", ygdm);
			parameterssize.put("ygdm", ygdm);
			sql += "and( a.SRGH = :ygdm )";
		}
		if (ZYHM != null && !"".equals(ZYHM)) {
			parameter.put("zyh", ZYHM);
			parameterssize.put("zyh", ZYHM);
			sql += "and(b.ZYHM =:zyh)";
		}
		if (ZXKS != null && !"".equals(ZXKS)) {
			parameter.put("ZXKS", parseLong(ZXKS));
			parameterssize.put("ZXKS", parseLong(ZXKS));
			sql += "and(a.ZXKS =:ZXKS)";
		}
		sql += " order by a.JFRQ desc";
		double total = 0;
		double zfje = 0;
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameter.put("first", pageNo * pageSize);
		parameter.put("max", pageSize);
		try {
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			SchemaUtil.setDictionaryMassageForList(rklist, "phis.application.fsb.schemas.JC_FYMX_FYCX");
			for (int i = 0; i < rklist.size(); i++) {
				// String ygxm = Dictionaries.instance().getDic("doctor")
				// .getText(rklist.get(i).get("SRGH") + "");
				// rklist.get(i).put("SRGH_text", ygxm);
				total += (Double) rklist.get(i).get("ZJJE");
				zfje += ((Double) rklist.get(i).get("ZJJE"))
						* ((Double) rklist.get(i).get("ZFBL"));
			}
			listsize = dao.doSqlQuery(sql, parameterssize);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", rklist);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用明细查询失败");
		}
	}

	public void doQueryCostMx(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String ZYH = body.get("ZYH").toString();
		String startDate = (String) body.get("startDate");
		String endDate = (String) body.get("endDate");
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("ZYH", ZYH);
		parameter.put("startDate", startDate);
		parameter.put("endDate", endDate);
		// System.out.println(parameter);
		try {
			StringBuilder sql = new StringBuilder(
					"select a.JLXH as FYMX from JC_FYMX a where ");
			sql.append(" a.ZYH=:ZYH and a.FYRQ>=to_date(:startDate,'yyyy-mm-dd HH24:mi:ss') and a.FYRQ<to_date(:endDate,'yyyy-mm-dd HH24:mi:ss')");
			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					parameter);
			System.out.println(list.size());
			res.put("FYMX", list.size());
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用明细查询失败");
		}
	}

	public void doNumberFormat(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		double strNmber = Double.parseDouble(body.get("strNumber") + "");
		int count = Integer.parseInt(body.get("count") + "");
		double ret = BSPHISUtil.getDouble(strNmber, count);
		res.put("rest", ret + "");
	}

	public void doHospitalCostMxQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx, BaseDAO dao2)
			throws ExpException {
		@SuppressWarnings("unchecked")
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameter = new HashMap<String, Object>();
		try {

			StringBuilder fyhjSql = new StringBuilder(
					"select sum(a.ZJJE) as FYHJ,sum(a.ZFJE) as ZFHJ from JC_FYMX a  where ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = ExpRunner.toString(cnd, ctx);
					fyhjSql.append(where);
				}
			}
			List<Map<String, Object>> fyhjlist = dao.doSqlQuery(
					fyhjSql.toString(), null);
			for (int i = 0; i < fyhjlist.size(); i++) {
				res.put("ZFHJ", fyhjlist.get(i).get("ZFHJ") == null ? "0.00"
						: fyhjlist.get(i).get("ZFHJ"));
				res.put("FYHJ", fyhjlist.get(i).get("FYHJ") == null ? "0.00"
						: fyhjlist.get(i).get("FYHJ"));
			}
			// res.put("body",list);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doHospitalCostDetalsQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx, BaseDAO dao2)
			throws ExpException {
		@SuppressWarnings("unchecked")
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameter = new HashMap<String, Object>();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameter.put("first", pageNo * pageSize);
		parameter.put("max", pageSize);
		String sql = "select  min(a.FYRQ) as KSRQ, max(a.FYRQ) as ZZRQ, a.YPLX as YPLX, a.FYXH as FYXH, a.FYMC as FYMC,sum(a.FYSL) as FYSL,a.FYDJ as FYDJ,sum(a.ZJJE) as ZJJE,sum(a.ZFJE) as ZFJE,sum(a.ZLJE) as ZLJE,a.ZFBL as ZFBL "
				+ "from JC_FYMX a  where ";
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where = ExpRunner.toString(cnd, ctx);
				sql += where;
			}
		}
		sql += " group by YPLX,FYXH,FYMC,FYDJ,ZFBL order by KSRQ desc,YPLX, FYXH ,FYSL,FYDJ,ZFBL desc";

		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
			List<Map<String, Object>> listsize = dao.doSqlQuery(
					"select count(1) as total from (" + sql + ")", null);
			res.put("totalCount",
					Long.parseLong(listsize.get(0).get("TOTAL") + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doHospitalCostSumQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx, BaseDAO dao2)
			throws ExpException {
		@SuppressWarnings("unchecked")
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameter = new HashMap<String, Object>();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameter.put("first", pageNo * pageSize);
		parameter.put("max", pageSize);
		String sql = "select a.ZYH as ZYH, to_char(a.FYRQ,'yyyy-MM-dd') as FYRQ, a.YPLX as YPLX , a.FYXH as FYXH, a.FYMC as FYMC, sum(a.FYSL) as FYSL, a.FYDJ as FYDJ, sum(a.ZJJE) as ZJJE, sum(a.ZFJE) as ZFJE, a.ZFBL as ZFBL "
				+ " from JC_FYMX a where ";
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where = ExpRunner.toString(cnd, ctx);
				sql += where;
			}
		}
		sql += " group by ZYH,to_char(FYRQ,'yyyy-MM-dd'),YPLX,FYXH,FYMC,FYDJ,ZFBL order by fyrq desc";

		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
			List<Map<String, Object>> listsize = dao.doSqlQuery(
					"select count(1) as total from (" + sql + ")", null);
			res.put("totalCount",
					Long.parseLong(listsize.get(0).get("TOTAL") + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
