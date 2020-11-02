/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.ivc.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.ParameterUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 处方组套维护
 * 
 * @author zhangyq 2012.05.28
 */
public class ClinicDisposalEntryModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicDisposalEntryModule.class);

	public ClinicDisposalEntryModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 处方组套明细保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDisposalEntry(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx, int clinicId,
			int brid, String brxm, int djly, long ghgl)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String uid = user.getUserId()+"";// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJG, ctx));
		Long yjxh = (long) 0;
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		List<Map<String, Object>> list_ = new ArrayList<Map<String, Object>>();
		try {
			List<Object> rebody = new ArrayList<Object>();
			// 规定病种
//			long pdgdbz = 0;
//			if (req.get("GDBZ") != null) {
//				pdgdbz = Long.parseLong(req.get("GDBZ").toString());
//			}

			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> map_ = body.get(i);// 一条一条放到map_中
				if ((!"0".equals(map_.get("YJXH") + "")
						|| map_.get("YJXH") != null || map_.get("YJXH") != "")
						&& map_.get("SBXH") != null) {// 如果有yjxh 就直接修改
					Map<String, Object> parameters = new HashMap<String, Object>();
					if (map_.get("ZXKS") != null && map_.get("ZXKS") != "") {
						parameters.put("ZXKS",
								Long.parseLong(map_.get("ZXKS") + ""));
					} else {
						parameters.put("ZXKS", 0L);
					}
					if (map_.get("YSDM") != null && map_.get("YSDM") != "") {
						parameters.put("YSDM", map_.get("YSDM"));
					} else {
						parameters.put("YSDM", uid);
					}
					parameters.put("YJXH",
							Long.parseLong(map_.get("YJXH") + ""));
					dao.doSave("update", BSPHISEntryNames.MS_YJ02_CIC, map_, false);
					if (wpjfbz == 1 && wzsfxmjg == 1) {
						BSPHISUtil.setSupplies(dao, ctx,
								Long.parseLong(map_.get("SBXH") + ""), ghgl);

					}
					// 规定病种
//					if (pdgdbz == 1) {
//						parameters.put("GDBZ", pdgdbz);
//					} else {
//						parameters.put("GDBZ", pdgdbz);
//					}
					StringBuffer hql = new StringBuffer();
//					hql.append("update MS_YJ01 set ZXKS=:ZXKS,GDBZ=:GDBZ,YSDM=:YSDM where YJXH=:YJXH");
					hql.append("update MS_YJ01 set ZXKS=:ZXKS,YSDM=:YSDM where YJXH=:YJXH");
					dao.doUpdate(hql.toString(), parameters);// 规定病种
				} else {
					// 新插入的一条上一条做对比 如果相同就是同一组 把上一组的yjxh拿过来用
					for (int j = 0; j < list_.size(); j++) {
						Map<String, Object> map_ls = list_.get(j);
						if (map_ls.get("YJZH").equals(map_.get("YJZH"))) {// 如果YJZH组号相同
							map_.put("YJXH", map_ls.get("YJXH"));// 就设置上一条对应的主表的ID
						}
					}
					if (("0".equals(map_.get("YJXH") + ""))
							|| map_.get("YJXH") == null
							|| map_.get("YJXH") == "") {// 如果当前条没有YJXH
						// 就新增一张表
						Map<String, Object> yj01Map = new HashMap<String, Object>();
						HttpSession session = (HttpSession) ctx
								.get(Context.WEB_SESSION);
						Long deptId = 0L;
						if (session.getAttribute("departmentId") != null
								&& session.getAttribute("departmentId") != "") {
							deptId = Long.parseLong(session.getAttribute(
									"departmentId").toString());
						}
						yj01Map.put("KSDM", deptId);
						if (map_.get("YSDM") == null
								|| map_.get("YSDM").toString().trim().length() == 0) {
							yj01Map.put("YSDM", uid);
						} else {
							yj01Map.put("YSDM", map_.get("YSDM"));
						}
						yj01Map.put("KDRQ", new Date());
						yj01Map.put("ZFPB", 0);
						yj01Map.put("ZXPB", 0);
						yj01Map.put("CFBZ", 0);
						yj01Map.put("DJZT", 0);
						yj01Map.put("JGID", jgid);
						if (map_.get("ZXKS") != null && map_.get("ZXKS") != "") {
							yj01Map.put("ZXKS",
									Long.parseLong(map_.get("ZXKS") + ""));
						} else {
							yj01Map.put("ZXKS", 0);
						}
						yj01Map.put("JZXH", clinicId);
						yj01Map.put("BRID", brid);
						yj01Map.put("BRXM", brxm);
						yj01Map.put("DJLY", djly);
						// 规定病种
//						if (pdgdbz == 1) {
//							yj01Map.put("GDBZ", 1);
//						} else {
//							yj01Map.put("GDBZ", 0);
//						}
						Map<String, Object> yj01Req = dao.doSave("create",
								BSPHISEntryNames.MS_YJ01_CIC, yj01Map, false);
						yjxh = Long.parseLong(yj01Req.get("YJXH") + "");// 获取主键

						rebody.add(yjxh);
						// 保存主表获得的主键;
						Map<String, Object> map_l = new HashMap<String, Object>();
						map_l.put("YJZH", map_.get("YJZH"));// 把YJZH放到map_1中
						map_l.put("YJXH", yjxh);// 把YJXH放到map_1中
						list_.add(map_l);// 把这些值放到list_中
						map_.put("YJXH", yjxh);// 把YJXH设置到要保存的明细中
						map_.put("YJZX", 1);// 并设置当前行YJZX为1
					}
					// 接着保存明细
					String op = (String) body.get(i).get("_opStatus");
					Map<String, Object> sbxhMap = dao.doSave(op,
							BSPHISEntryNames.MS_YJ02_CIC, map_, false);
					if (wpjfbz == 1 && wzsfxmjg == 1) {
						BSPHISUtil.setSupplies(dao, ctx,
								Long.parseLong(sbxhMap.get("SBXH") + ""), ghgl);
					}
				}
			}
			res.put("body", rebody);
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}

	}

	/**
	 * 处置明细删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doRemoveDisposalEntry(Map<String, Object> body, int pkey,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
					jgid, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
					: ParameterUtil.getParameter(jgid,
							BSPHISSystemArgument.MZWPJFBZ, ctx));
			int wzsfxmjg = Integer
					.parseInt("".equals(ParameterUtil.getParameter(jgid,
							BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
							: ParameterUtil.getParameter(jgid,
									BSPHISSystemArgument.WZSFXMJG, ctx));
			Long pkey1 = Long.parseLong(pkey + "");
			// 如果根据主键查询得到的门诊序号有值 就不能删除
			Map<String, Object> parametersMZXH = new HashMap<String, Object>();
			parametersMZXH.put("SBXH", pkey1);
			String sqlMZXH = "select a.MZXH as MZXH from " + schemaList + " a,"
					+ schemaDetailsList
					+ " b where a.YJXH=b.YJXH and b.SBXH=:SBXH";
			Map<String, Object> mzxh = dao.doLoad(sqlMZXH, parametersMZXH);// 拿到医技序号
			if (mzxh.get("MZXH") != null) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "该处置已收费,不能删除");
			} else {
				if ("1".equals(body.get("SIGN") + "")) {// 删除医技主项
														// 并删除同组医技信息,接着删除本张医技单
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SBXH", pkey1);
					String sql = "select YJXH as YJXH from "
							+ schemaDetailsList + " where SBXH=:SBXH";
					Map<String, Object> yjxh = dao.doLoad(sql, parameters);// 拿到医技序号
					Map<String, Object> yjxhParameters = new HashMap<String, Object>();
					yjxhParameters.put("YJXH", yjxh.get("YJXH"));
					if (wpjfbz == 1 && wzsfxmjg == 1) {
						BSPHISUtil.deleteSupplies(dao, ctx, pkey1);
					}
					String yj02Hql = "delete from " + schemaDetailsList
							+ " where YJXH=:YJXH";// 删明细
					dao.doUpdate(yj02Hql, yjxhParameters);
					String yj01Hql = "delete from " + schemaList
							+ " where YJXH=:YJXH";// 删主表
					dao.doUpdate(yj01Hql, yjxhParameters);
					res.put("YJXH", yjxh.get("YJXH"));
				} else if ("2".equals(body.get("SIGN") + "")) {
					if (wzsfxmjg == 1) {
						BSPHISUtil.deleteSupplies(dao, ctx, pkey1);
					}
					dao.doRemove(pkey1 + "", schemaDetailsList);// 删一条明细
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SBXH", pkey1 + 1);
					String hql = "update " + schemaDetailsList
							+ " set YJZX=1 where SBXH=:SBXH";
					dao.doUpdate(hql, parameters);// 修改下一条为医技主项
				} else {
					if (wpjfbz == 1 && wzsfxmjg == 1) {
						BSPHISUtil.deleteSupplies(dao, ctx, pkey1);
					}
					dao.doRemove(pkey1 + "", schemaDetailsList);// 直接删除一条
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("remove failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
		}

	}

	public List<Map<String, Object>> doLoadPersonalSet(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		// String uid=user.getUserId();
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		Map<String, Object> params = new HashMap<String, Object>();
		if (body.get("ZTBH") == null) {
			throw new ModelDataOperationException("无法获取组套信息!");
		}
		params.put("ZTBH", Long.parseLong(body.get("ZTBH").toString()));
		params.put("JGID", jgid);
		StringBuffer hql = new StringBuffer(
				"select distinct a.JLBH,b.FYXH as FYXH,a.XMMC as XMMC,a.XMSL as XMSL,a.YPZH as YPZH,c.FYDJ as FYDJ,b.FYGB as FYGB,b.FYDW as FYDW,b.XMLX as XMLX,c.FYKS as FYKS,b.MZSY as MZSY");
		hql.append(" from YS_MZ_ZT02 a,GY_YLSF b,GY_YLMX c ");
		hql.append(" where  a.XMBH = b.FYXH AND  b.FYXH = c.FYXH and a.ZTBH = :ZTBH and c.JGID=:JGID ORDER BY a.JLBH");
		try {
			List<Map<String, Object>> meds;
			meds = dao.doQuery(hql.toString(), params);

			for (Map<String, Object> med : meds) {
				if (!"1".equals(med.get("MZSY") + "")) {
					med.put("errorMsg", "不存在!");
					continue;
				}
				if (med.get("FYKS") != null
						&& med.get("FYKS").toString().length() > 0) {
					med.put("FYKS_text",
							med.get("FYKS") == null ? "" : DictionaryController
									.instance().get("phis.dictionary.department_mzyj")
									.getText(med.get("FYKS").toString()));
				}
				res.add(med);
			}
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("数据字典加载失败!", e);
		}
	}

	public Map<String, Object> doLoadCostInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JLBH", body.get("JLBH"));
			params.put("JGID", jgid);
			StringBuffer hql = new StringBuffer(
					"select distinct b.FYXH as FYXH,a.XMMC as XMMC,a.XMSL as XMSL,c.FYDJ as FYDJ,b.FYGB as FYGB,b.FYDW as FYDW,b.XMLX as XMLX,c.FYKS as FYKS");
			hql.append(" from YS_MZ_ZT02 a,GY_YLSF b,GY_YLMX c ");
			hql.append(" where  a.XMBH = b.FYXH AND  b.FYXH = c.FYXH and a.JLBH = :JLBH and c.JGID=:JGID ORDER BY a.ZTBH");
			Map<String, Object> meds = dao.doLoad(hql.toString(), params);
			if (meds.get("FYKS") != null && meds.get("FYKS") != "") {
					meds.put(
							"FYKS_text",
							meds.get("FYKS") == null ? "" : DictionaryController
									.instance().get("department_mzyj")
									.getText(meds.get("FYKS").toString()));
			}
			return meds;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("数据字典加载失败!", e);
		}

	}

	public Map<String, Object> doLoadclinicAll(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("FYXH", Long.parseLong(body.get("FYXH") + ""));
			params.put("JGID", jgid);
			StringBuffer hql = new StringBuffer(
					"select distinct a.FYXH as FYXH,a.FYMC as XMMC,a.XMLX as XMLX,1 as XMSL,b.FYDJ as FYDJ,a.FYGB as FYGB ,a.FYDW as FYDW,b.FYKS as FYKS");
			hql.append(" from GY_YLSF a,GY_YLMX b ");
			hql.append(" where a.FYXH = b.FYXH and a.FYXH = :FYXH and b.JGID=:JGID and a.MZSY=1 ORDER BY a.FYXH");
			Map<String, Object> meds = dao.doLoad(hql.toString(), params);
			if (meds.get("FYKS") != null
					&& meds.get("FYKS").toString().length() > 0) {
				meds.put(
						"FYKS_text",
						DictionaryController.instance()
								.get("department_mzyj")
								.getText(meds.get("FYKS").toString()));
			}
			return meds;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("数据字典加载失败!", e);
		}

	}

	/**
	 * 根据根据病人信息和费用序号获取zfbl
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public Map<String, Object> doGetZFBL(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		try {
			res.put("ZFBL", BSPHISUtil.getPayProportion(req, ctx, dao));
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 根据根据就诊序号获取处置录入的ID
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetCZYJXH(Map<String, Object> body, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> params = new HashMap<String, Object>();
		Long jzxh = 0L;
		if (body.get("jzxh") != null) {
			jzxh = Long.parseLong(body.get("jzxh") + "");
		}
		params.put("JZXH", jzxh);
		params.put("JGID", jgid);
		String sql = "select YJXH as YJXH from MS_YJ01 where JZXH=:JZXH and JGID=:JGID";
		try {
			List<Map<String, Object>> yjxhlist = dao.doQuery(sql, params);
			res.put("YJXH", yjxhlist);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doCheckProjectMaterials(List<Map<String, Object>> bodys,
			long ghgl, int mzzy, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wpjfbzzy = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WPJFBZ, ctx)) ? "0" : ParameterUtil
				.getParameter(jgid, BSPHISSystemArgument.WPJFBZ, ctx));

		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJG, ctx));

		int wzsfxmjgzy = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJGZY, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
		String msg = "获取数据失败！";

		String HSQL = null;// 护士权利。

		if (user.getProperty("biz_departmentId") != null
				&& user.getProperty("biz_departmentId") != "") {
			HSQL = user.getProperty("biz_departmentId") + "";
		}
		try {
			// 针对划价收费取科室
			if (ghgl != 0 && HSQL == null) {
				StringBuffer hqlksdm = new StringBuffer();
				Map<String, Object> parametersmzks = new HashMap<String, Object>();
				parametersmzks.put("sbxh", ghgl);
				hqlksdm.append("select c.MZKS as MZKS,b.OFFICENAME as OFFICENAME from MS_GHMX a,SYS_Office b,MS_GHKS c where a.KSDM=c.KSDM and c.MZKS=b.ID and a.SBXH=:sbxh");
				Map<String, Object> map_ghxx = dao.doLoad(hqlksdm.toString(),
						parametersmzks);
				if (map_ghxx.get("MZKS") != null) {
					HSQL = map_ghxx.get("MZKS") + "";
				}
			}
			if (HSQL == null) {
				Map<String, Object> parametersksdm = new HashMap<String, Object>();
				parametersksdm.put("JGID", jgid);
				parametersksdm.put("ZYH",
						Long.parseLong(bodys.get(0).get("ZYH") + ""));
				Map<String, Object> ksdmMap = dao
						.doLoad("select a.BRBQ as BRBQ,b.OFFICENAME as OFFICENAME from ZY_BRRY a,SYS_Office b where a.BRBQ=b.ID and a.ZYH=:ZYH and a.JGID=:JGID",
								parametersksdm);
				if (ksdmMap != null) {
					if (ksdmMap.get("BRBQ") != null) {
						HSQL = ksdmMap.get("BRBQ") + "";
					}
				}
				if (HSQL == null) {
					msg = "获取科室失败！";
				}
			}
			List<Map<String, Object>> kcpdList = new ArrayList<Map<String, Object>>();
			// 如果是门诊mzzy=0 并且 门诊参数开启，如果是住院 mzzy=1 并且 住院参数开启 才能执行。
			if ((wzsfxmjg == 1 && mzzy == 0 && wpjfbz == 1)
					|| (wzsfxmjgzy == 1 && mzzy == 1 && wpjfbzzy == 1)) {
				long KSDM = Long.parseLong(HSQL);
				Map<String, Object> kfxhMap = new HashMap<String, Object>();
				kfxhMap.put("KSDM", KSDM);
				Map<String, Object> map = dao
						.doLoad("select KFXH as KFXH ,KSDM as KSDM from WL_KFDZ where KSDM=:KSDM",
								kfxhMap);
				if (map != null) {
					int KFXH = Integer.parseInt(map.get("KFXH") + "");
					for (int i = 0; i < bodys.size(); i++) {
						// 门诊中如果项目收完费了 就不判断。
						if (bodys.get(i).get("MZXH") != null
								&& bodys.get(i).get("MZXH") != "") {
							continue;
						}
						if (bodys.get(i).get("SBXH") != null) {
							BSPHISUtil.deleteSupplies(
									dao,
									ctx,
									Long.parseLong(bodys.get(i).get("SBXH")
											+ ""));
						}
						if (bodys.get(i).get("JLXH") != null) {
							BSPHISUtil.deleteSuppliesZY(
									dao,
									ctx,
									Long.parseLong(bodys.get(i).get("JLXH")
											+ ""));
						}
						String WZMC_TS = "";
						long FYXH = Long.parseLong(bodys.get(i).get("YLXH")
								+ "");
						int YLSL = Integer.parseInt(bodys.get(i).get("YLSL")
								+ "");// 数量
						String FYMC = bodys.get(i).get("FYMC") + "";

						Map<String, Object> parMap = new HashMap<String, Object>();
						parMap.put("FYXH", FYXH);
						parMap.put("JGID", jgid);
						String hql = "SELECT b.WZMC as WZMC,a.WZXH as WZXH,a.WZSL as WZSL FROM GY_FYWZ a,WL_WZZD b where a.WZXH=b.WZXH and a.JGID=:JGID and a.FYXH =:FYXH ";
						List<Map<String, Object>> resList = dao.doQuery(hql,
								parMap);
						for (int j = 0; j < resList.size(); j++) {
							Map<String, Object> kcpdMap = new HashMap<String, Object>();

							long WZXH = Long.parseLong(resList.get(j).get(
									"WZXH")
									+ "");
							double WZSL = Double.parseDouble(resList.get(j)
									.get("WZSL") + "");
							String WZMC1 = resList.get(j).get("WZMC") + "";
							parMap.clear();
							parMap.put("WZXH", WZXH);
							parMap.put("KFXH", KFXH);
							String sqlString = "SELECT b.WZMC as WZMC,sum(a.WZSL) as WZSL,sum(a.YKSL) as YKSL from WL_WZKC a ,WL_WZZD b where a.WZXH = b.WZXH and a.KFXH=:KFXH and a.WZXH =:WZXH group by WZMC ";
							List<Map<String, Object>> compList = dao.doQuery(
									sqlString, parMap);
							if (compList.size() > 0) {
								Double WZSL_res = Double.parseDouble(compList
										.get(0).get("WZSL") + "");
								String YKSL_s = compList.get(0).get("YKSL")
										+ "";
								Double YKSL_res = 0.0;
								if (YKSL_s != null) {
									YKSL_res = Double.parseDouble(compList.get(
											0).get("YKSL")
											+ "");
								}
								String WZMC = compList.get(0).get("WZMC") + "";
								Double kykc = WZSL_res - YKSL_res;
								if (kykc < (YLSL * WZSL)) {
									if (WZMC_TS.length() == 0) {
										WZMC_TS = " " + WZMC;
									} else {
										WZMC_TS += "," + WZMC;
									}
								} else {
									kcpdMap.put("WZXH", WZXH);
									kcpdMap.put("WZSL", WZSL_res);
									kcpdMap.put("YKSL", (YLSL * WZSL));
									kcpdMap.put("WZMC", WZMC);
									kcpdMap.put("FYMC", FYMC);
									kcpdList.add(kcpdMap);
								}
							} else {
								if (WZMC_TS.length() == 0) {
									WZMC_TS = " " + WZMC1;
								} else {
									WZMC_TS += "," + WZMC1;
								}
							}

						}
						if (WZMC_TS.length() > 0) {
							msg = "项目为：" + FYMC + "所对应的的物资名称：" + WZMC_TS
									+ "库存不足不能保存!";
							throw new RuntimeException("项目为：" + FYMC
									+ "所对应的的物资名称：" + WZMC_TS + "库存不足不能保存!");
						}
					}
					double YKSL_Z = 0;
					for (int i = 0; i < kcpdList.size(); i++) {
						long WZXH = Long.parseLong(kcpdList.get(i).get("WZXH")
								+ "");
						YKSL_Z = Double.parseDouble(kcpdList.get(i).get("YKSL")
								+ "");
						double WZSL = Double.parseDouble(kcpdList.get(i).get(
								"WZSL")
								+ "");
						String WZMC = kcpdList.get(i).get("WZMC") + "";
						String FYMC = kcpdList.get(i).get("FYMC") + "";

						for (int j = i + 1; j < kcpdList.size(); j++) {
							long WZXHj = Long.parseLong(kcpdList.get(j).get(
									"WZXH")
									+ "");
							if (WZXH == WZXHj) {
								FYMC += "," + kcpdList.get(j).get("FYMC") + "";
								YKSL_Z += Double.parseDouble(kcpdList.get(j)
										.get("YKSL") + "");
								if ((WZSL - YKSL_Z) < 0) {
									msg = "项目为" + FYMC + "物资：" + WZMC
											+ "库存不足不能保存!";
									throw new RuntimeException("项目为" + FYMC
											+ "物资：" + WZMC + "库存不足不能保存!");
								} else {
									kcpdList.remove(j);
									j--;
								}
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(msg, e);
		}
	}

	public void doQueryIsNeedVerify(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			String brxz = "";
			if (body.get("BRXZ") != null && body.get("BRXZ") != "") {
				brxz = body.get("BRXZ") + "";
			}
			String SHIYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
			if (!brxz.equals(SHIYB)) {
				return;
			}
			// 处置
			// 测试
			// res.put("isNeedVerify", 1);
			// res.put("ZWMC", "xxxxx");
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			long fyxh = 0L;
			long brid = 0l;
			if (body.containsKey("FYXH")) {
				fyxh = Long.parseLong(body.get("FYXH") + "");
			}
			if (body.containsKey("BRID")) {
				brid = Long.parseLong(body.get("BRID") + "");
			}
			String ptbjbz = "";
			String rylb = "";
			// Ls_btbjbz = 医保病人普通保健标志
			// Ls_rylb = 医保病人人员类别
			Map<String, Object> parametersBRID = new HashMap<String, Object>();
			parametersBRID.put("BRID", brid);
			List<Map<String, Object>> list_brxx = dao.doQuery(
					"select YLRYLB as RYLB,PTRYXSBJDYBZ as PTBJBZ "
							+ " from YB_CBRYJBXX where BRID=:BRID",
					parametersBRID);
			if (list_brxx.size() > 0) {
				ptbjbz = list_brxx.get(0).get("PTBJBZ") + "";
				rylb = list_brxx.get(0).get("RYLB") + "";
			}

			String SYB_LXLB = ParameterUtil
					.getParameter(
							JGID,
							"SYB_LXLB",
							"31,32,33,34,3A,3B,3C,3D,3E,3F",
							"市医保离休病人类别，用逗号间隔的字符串，默认‘31,32,33,34,3A,3B,3C,3D,3E,3F",
							ctx);
			String YB_BJGBQY = ParameterUtil.getParameter(JGID, "YB_BJGBQY",
					"0", "保健干部是否启用，1表示启用，0表示不启用", ctx);
			if ((ptbjbz.equals("1") || ptbjbz.equals("2") || ptbjbz.equals("3") || ptbjbz
					.equals("5")) && YB_BJGBQY.equals("1")) {
				return;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FYXH", fyxh);
			parameters.put("FYRQ", new Date());
			String sql = "select YBBM as YBBM,SPLX as SPLX,SPLX_LX as SPLX_SLX,XMMC as ZWMC"
					+ " from YB_FYDZ "
					+ " where YBPB = 1 and FYXH=:FYXH and KSSJ<=:FYRQ and (ZZSJ>=:FYRQ or ZZSJ is null)";

			List<Map<String, Object>> list_ypdz = dao.doQuery(sql, parameters);
			if (list_ypdz.size() == 0) {
				return;
			}
			int SPLX = parseInt(list_ypdz.get(0).get("SPLX"));
			int SPLX_SLX = parseInt(list_ypdz.get(0).get("SPLX_SLX"));
			String[] syb_lxlbStrings = SYB_LXLB.split(",");
			for (String str : syb_lxlbStrings) {
				if (rylb.equals(str)) {
					SPLX = SPLX_SLX;
					break;
				}
			}
			if (SPLX == 2) {
				res.put("isNeedVerify", 1);
				res.put("ZWMC", list_ypdz.get(0).get("ZWMC") + "");
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doGetKsxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao2, Context ctx) throws ModelDataOperationException {
		String sql = "select b.ID as ksdm,b.OFFICENAME as ksmc  from SYS_Personnel a left join SYS_Office b on a.ksdm = b.ID where a.ygdm="
				+ req.get("ygdm");
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, null);
			res.put("ksdm", list.get(0).get("KSDM"));
			res.put("ksmc", list.get(0).get("KSMC"));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("科室信息查询失败！", e);
		}

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
