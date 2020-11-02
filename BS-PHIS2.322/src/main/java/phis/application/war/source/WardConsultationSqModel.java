package phis.application.war.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

/**
 * description:会诊申请DAO
 * 
 * @author:zhangfs create on 2013-5-22
 */
public class WardConsultationSqModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(WardConsultationSqModel.class);

	private static final String YS_ZY_HZSQ = "YS_ZY_HZSQ";

	public WardConsultationSqModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:会诊记录 add_by zhangfs
	 * 
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void getList(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();

		try {
			// 员工编号
			// String
			// gh=queryYGBH(user.getUserId()).get(0).get("YGBH").toString();
			// List<Map<String, Object>> list = new ArrayList<Map<String,
			// Object>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			int type = Integer.parseInt(body.get("alias").toString());
			String sql = "select YS_ZY_HZSQ.JZHM as JZHM,YS_ZY_HZSQ.SQSJ as SQSJ,YS_ZY_HZSQ.SQKS as SQKS ,YS_ZY_HZSQ.SQYS as SQYS ,YS_ZY_HZSQ.HZSJ as HZSJ ,"
					+ "YS_ZY_HZSQ.YQDX as YQDX,YS_ZY_HZSQ.JJBZ as JJBZ ,YS_ZY_HZSQ.JSBZ as JSBZ,YS_ZY_HZSQ.ZFBZ as  ZFBZ,YS_ZY_HZSQ.SQXH as SQXH,YS_ZY_HZSQ.TJBZ as TJBZ,YS_ZY_HZSQ.TJYS as TJYS,"
					+ "YS_ZY_HZSQ.TJSJ as TJSJ,YS_ZY_HZSQ.TXRY as TXRY,YS_ZY_HZSQ.YQDX as  YQDX ,YS_ZY_HZSQ.JGID as JGID ,YS_ZY_HZSQ.HZMD as HZMD,YS_ZY_HZSQ.BQZL as BQZL from YS_ZY_HZSQ where YS_ZY_HZSQ.JZHM = :al_zyh";
			if (type == 1) {
				sql += " and TXRY=:writer";
				parameters.put("writer", user.getUserId());
			} else if (type == 2) {

				sql += " and YQDX=:yqdx";
				parameters.put("yqdx", user.getUserId());
				// parameters.put("yqdx", user.getUserId());
			}

			String zyh = body.get("zyh").toString();

			parameters.put("al_zyh", zyh);

			List<Map<String, Object>> listMaps = dao
					.doSqlQuery(sql, parameters);
			// for (int i = 0; i < listMaps.size(); i++) {
			// Map<String, Object> ppMap = listMaps.get(i);
			// ppMap.put("YQDX", queryYGDM(ppMap.get("YQDX").toString()));
			// }
			SchemaUtil.setDictionaryMassageForList(listMaps,
					BSPHISEntryNames.YS_ZY_HZSQ);
			res.put("body", listMaps);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * description:查看详细 add_by zhangfs
	 * 
	 * @param
	 * @return
	 */
	public void doLoadInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long pkey = Long.parseLong(req.get("pkey") + "");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();

			String sql = "select YS_ZY_HZSQ.JZHM as JZHM,YS_ZY_HZSQ.SQSJ as SQSJ,YS_ZY_HZSQ.SQKS as SQKS ,YS_ZY_HZSQ.SQYS as SQYS ,YS_ZY_HZSQ.HZSJ as HZSJ ,"
					+ "YS_ZY_HZSQ.YQDX as YQDX,YS_ZY_HZSQ.JJBZ as JJBZ ,YS_ZY_HZSQ.JSBZ as JSBZ,YS_ZY_HZSQ.ZFBZ as  ZFBZ,YS_ZY_HZSQ.SQXH as SQXH,YS_ZY_HZSQ.TJBZ as TJBZ,YS_ZY_HZSQ.TJYS as TJYS,"
					+ "YS_ZY_HZSQ.TJSJ as TJSJ,YS_ZY_HZSQ.TXRY as TXRY,YS_ZY_HZSQ.YQDX as  YQDX ,YS_ZY_HZSQ.JGID as JGID ,YS_ZY_HZSQ.HZMD as HZMD,YS_ZY_HZSQ.BQZL as BQZL from YS_ZY_HZSQ YS_ZY_HZSQ where YS_ZY_HZSQ.SQXH = :code";
			map_par.put("code", pkey);
			Map<String, Object> BRXX = dao.doLoad(sql, map_par);
			String YQDX = (String) BRXX.get("YQDX");
			// BRXX.put("YQDX", queryYGDM(YQDX));
			BRXX.put("GH", YQDX);
			/*
			 * List<Map<String, Object>> hgList = this.queryYGBH(YQDX); if
			 * (hgList != null && hgList.size() > 0) { Map<String, Object> hgMap
			 * = hgList.get(0); BRXX.put("GH", hgMap.get("YGBH"));
			 * 
			 * }
			 */
			String currentZd = queryCurrentBq(BRXX.get("JZHM").toString(), ctx);// 当前诊断
			if (StringUtils.isNotBlank(currentZd)) {
				BRXX.put("ZDMC", currentZd);
			}
			SchemaUtil.setDictionaryMassageForForm(BRXX,
					BSPHISEntryNames.YS_ZY_HZSQ);

			res.put("body", BRXX);
		} catch (Exception e) {
			logger.error("Save failed.", e);
			/*
			 * throw new ModelDataOperationException(
			 * ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
			 */
		}
	}

	/**
	 * description:查看是否有会诊者记录存在 add_by zhangfs
	 * 
	 * @param
	 * @return
	 */
	public Long queryisExistRecord(Context ctx, String adviceObj)
			throws ModelDataOperationException {

		long count = 0l;
		String hql = "";
		hql = "YQDX = :obj";
		try {

			Map<String, Object> map_par = new HashMap<String, Object>();

			map_par.put("obj", adviceObj);

			count = dao.doCount(YS_ZY_HZSQ, hql, map_par);
		} catch (Exception e) {
			logger.error("query fails", e);
		}
		return count;

	}

	/**
	 * 级联员工编号
	 * 
	 * @throws PersistentDataOperationException
	 */
	// public List<Map<String, Object>> queryYGBH(String ygdm)
	// throws PersistentDataOperationException {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("ygdm", ygdm);
	// List<Map<String, Object>> list = dao.doQuery(
	// "select YGBH as YGBH from SYS_Personnel WHERE YGDM=:ygdm", map);
	// // SchemaUtil.setDictionaryMassageForList(list, "YS_ZY_HZSQ");
	//
	// return list;
	// }

	/**
	 * 查询员工信息根据员工编号YGBH
	 */
	// public String queryYGDM(String ygbh)
	// throws PersistentDataOperationException {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("ygbh", ygbh);
	// List<Map<String, Object>> list = dao.doQuery(
	// "select PERSONID as PERSONID from SYS_Personnel WHERE YGBH=:ygbh", map);
	// if (list.size() > 0) {
	// Map<String, Object> map2 = list.get(0);
	// return map2.get("YGDM").toString();
	// }
	// // SchemaUtil.setDictionaryMassageForList(list, "YS_ZY_HZSQ");
	//
	// return null;
	// }

	/**
	 * description:保存会诊记录表单 add_by zhangfs
	 * 
	 * @param
	 * @return
	 */
	public Map<String, Object> save(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("JZHM", body.get("zyh"));// 住院号
		map.put("SQKS", body.get("sqks"));// 申请科室
		map.put("SQYS", body.get("sqys"));// 申请医生
		map.put("SQSJ", body.get("sqsj"));// 申请时间
		map.put("HZMD", body.get("hzmd"));//
		map.put("HZSJ", body.get("hzsj"));
		map.put("YQDX", body.get("yqdx"));
		if (StringUtils.isBlank(body.get("jjbz") + ""))
			map.put("JJBZ", 0);// 紧急标志
		else {
			map.put("JJBZ", body.get("jjbz"));// 紧急标志

		}
		map.put("JSBZ", 0);// 结束标志
		map.put("TJBZ", 0);// 提交标志
		map.put("ZFBZ", 0);// 作废标志
		map.put("TXRY", user.getUserId());// 填写人员
		map.put("BQZL", body.get("bqzl"));// 病区治疗
		map.put("JGID", jgid);// 机构ID

		try {
			Map<String, Object> map2 = dao.doInsert(
					BSPHISEntryNames.YS_ZY_HZSQ, map, false);
			return map2;
		} catch (Exception e) {
			throw new ModelDataOperationException("会诊申请单保存失败!", e);
		}
	}

	/**
	 * 修改会诊记录信息
	 */

	public void update(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> updmap = new HashMap<String, Object>();
		map.put("JZHM", body.get("zyh"));// 住院号
		map.put("SQKS", body.get("sqks"));// 申请科室
		map.put("SQYS", body.get("sqys"));// 申请医生
		map.put("SQSJ", body.get("sqsj"));// 申请时间
		map.put("HZMD", body.get("hzmd"));//
		map.put("HZSJ", body.get("hzsj"));
		map.put("YQDX", body.get("yqdx"));
		if (StringUtils.isBlank(body.get("jjbz") + ""))
			map.put("JJBZ", 0);// 紧急标志
		else {
			map.put("JJBZ", body.get("jjbz"));// 紧急标志

		}
		map.put("JSBZ", 0);// 结束标志
		map.put("TJBZ", 0);// 提交标志
		map.put("ZFBZ", 0);// 作废标志
		map.put("TXRY", user.getUserId());// 填写人员
		map.put("BQZL", body.get("bqzl"));// 病区治疗
		map.put("JGID", jgid);// 机构ID
		map.put("SQXH", body.get("sqxh"));
		updmap.put(
				"YZMC",
				"邀请"
						+ DictionaryController.instance()
								.getDic("phis.dictionary.doctor_cfqx")
								.getText(body.get("yqdx") + "") + "会诊");
		updmap.put("KSSJ", new Date());// 开嘱时间
		updmap.put("YWID", Long.parseLong(body.get("sqxh") + ""));// 开嘱时间
		updmap.put("ZYH", Long.parseLong(body.get("zyh") + ""));// 开嘱时间
		try {
			dao.doSave("update", BSPHISEntryNames.YS_ZY_HZSQ, map, false);
			dao.doUpdate(
					"update ZY_BQYZ set YZMC=:YZMC,KSSJ=:KSSJ where YWID=:YWID and ZYH=:ZYH",
					updmap);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * 生成会诊医嘱记录----------------------------------------
	 */
	public Map<String, Object> saveHzYz(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ZYH", Long.parseLong(body.get("zyh").toString()));// 住院号
		Map<String, Object> zyyspar = new HashMap<String, Object>();
		zyyspar.put("ZYH", Long.parseLong(body.get("zyh").toString()));
		try {
			map.put("BRKS", Long.parseLong(body.get("sqks").toString()));// 病人科室
			map.put("BRBQ", Long.parseLong(body.get("brbq").toString()));// 病人病区
			map.put("BRCH", body.get("brch"));// 病人床号
			map.put("YPXH", Long.parseLong("0"));//
			map.put("YPCD", (long) 0);
			map.put("XMLX", 10);
			map.put("YPLX", 0);// 药片类型
			map.put("TZFHBZ", 0);// 停嘱复核标志
			map.put("FHBZ", 0);
			map.put("YCJL", (double) 1);// 一次剂量
			map.put("YCSL", (double) 1);// 一次数量
			map.put("LSYZ", 1);// 临时医嘱
			map.put("MRCS", 1);
			map.put("MZCS", 0);
			map.put("JFBZ", 3);// 计费标志
			map.put("YPYF", (long) 0);
			map.put("SRCS", 1);// 首日次数
			map.put("YZMC",
					"邀请"
							+ DictionaryController.instance()
									.getDic("phis.dictionary.doctor_cfqx")
									.getText(body.get("YQDX") + "") + "会诊");// 药嘱名称
			map.put("CZGH", user.getUserId());// 转抄
			Map<String, Object> zyysmap = dao.doLoad(
					"select ZSYS as ZSYS from ZY_BRRY where ZYH=:ZYH", zyyspar);
			if (zyysmap != null) {
				if (zyysmap.containsKey("ZSYS")) {
					if (zyysmap.get("ZSYS") != null) {
						map.put("YSGH", zyysmap.get("ZSYS") + "");// 开嘱医生
					}
				}
			}
			List<Map<String, Object>> yzzhlist = dao.doSqlQuery(
					"select max(YZZH) as YZZH from ZY_BQYZ where ZYH=:ZYH",
					zyyspar);
			if (yzzhlist.size() > 0) {
				if (yzzhlist.get(0).get("YZZH") != null) {
					map.put("YZZH",
							Long.parseLong(yzzhlist.get(0).get("YZZH") + "") + 1);// 开嘱医生
				} else {
					map.put("YZZH", 1l);
				}
			}
			map.put("KSSJ", new Date());// 开嘱时间
			map.put("YDLB", "301");// 约定类别
			map.put("SRKS", Long.parseLong(body.get("sqks").toString()));// 输入科室
			map.put("SQID", Long.parseLong(body.get("SQXH").toString()));// 申请序号
			map.put("YWID", Long.parseLong(body.get("SQXH").toString()));// 申请序号
			map.put("ZXKS", Long.parseLong(body.get("sqks").toString()));// 执行科室
			map.put("JGID", jgid);// 机构ID
			map.put("SFJG", "0");
			map.put("YPDJ", (double) 0);
			map.put("SYBZ", 0);
			map.put("ZFPB", 0);
			map.put("YJZX", 0);
			map.put("YJXH", (long) 0);
			map.put("ZFBZ", 0);// 作废标志
			map.put("FYSX", 0);
			map.put("YEPB", 0);
			map.put("YFSB", (long) 0);
			map.put("LSYZ", 1);
			map.put("LSBZ", 0);
			map.put("YZPB", 0);
			map.put("JFBZ", 3);
			map.put("TPN", 0);
			int zyysqy = Integer.parseInt(ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.ZYYSQY, ctx));
			if (zyysqy == 1) {
				map.put("YSBZ", 1);
			} else {
				map.put("YSBZ", 0);
			}
			map.put("YSTJ", 0);
			map.put("PSPB", 0);
			map.put("SYPC", "ST");
			Map<String, Object> map2 = dao.doInsert(BSPHISEntryNames.ZY_BQYZ,
					map, false);
			return map2;
		} catch (Exception e) {
			throw new ModelDataOperationException("会诊申请单保存失败!", e);
		}
	}

	/**
	 * 查询病人当前诊断
	 */

	public String queryCurrentBq(String zyh, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("zyh", Long.parseLong(zyh.toString()));
		map.put("jgid", jgid);

		List<Map<String, Object>> list;
		try {
			list = dao
					.doQuery(
							"select gy_jbbm.JBMC as JBMC from GY_JBBM gy_jbbm,ZY_RYZD  zy_ryzd where  zy_ryzd.ZDXH = gy_jbbm.JBXH and zy_ryzd.ZDLB = 2 and  zy_ryzd.ZYH=:zyh and zy_ryzd.JGID  =:jgid",
							map);
			if (list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				return map2.get("JBMC").toString();
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 提交
	 */
	public void save(Map<String, Object> req) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("sqxh", Long.parseLong(body.get("SQXH").toString()));
		try {
			dao.doUpdate("update " + YS_ZY_HZSQ
					+ " set TJBZ=1 WHERE SQXH=:sqxh", map);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 退回
	 */
	public void back(Map<String, Object> req) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("sqxh", Long.parseLong(body.get("SQXH").toString()));
		try {
			dao.doUpdate("update " + YS_ZY_HZSQ
					+ " set TJBZ=0 WHERE SQXH=:sqxh", map);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 删除
	 */
	@SuppressWarnings("unchecked")
	public void remove(Map<String, Object> req) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			dao.doRemove(Long.parseLong(body.get("SQXH").toString()),
					BSPHISEntryNames.YS_ZY_HZSQ);
			// add by yangl ys_zy_hzyq 是否应该同时删除?
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("SQXH", Long.parseLong(body.get("SQXH").toString()));
			dao.doSqlUpdate("delete YS_ZY_HZYQ where SQXH=:SQXH", params);
			// end
			Map<String, Object> delbzyzpar = new HashMap<String, Object>();
			delbzyzpar.put("YWID", Long.parseLong(body.get("SQXH") + ""));
			delbzyzpar.put("YDLB", "301");
			delbzyzpar.put("JGID", jgid);
			dao.doUpdate(
					"delete from ZY_BQYZ where YWID=:YWID and YDLB=:YDLB and JGID=:JGID",
					delbzyzpar);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 结束会诊
	 */
	public void stop(Map<String, Object> req) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("JSSJ", new Date());
		map.put("sqxh", Long.parseLong(body.get("SQXH").toString()));
		try {
			dao.doUpdate("update " + YS_ZY_HZSQ
					+ " set JSBZ=1 , JSSJ=:JSSJ  WHERE SQXH=:sqxh", map);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
