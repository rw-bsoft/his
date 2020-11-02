package phis.application.war.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

/**
 * description:转科
 * 
 * @author:zhangfs create on 2013-5-8
 */
public class WardTransferDeptModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(WardTransferDeptModel.class);

	public WardTransferDeptModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:是否有转科记录 add_by zhangfs
	 * 
	 * @param
	 * @return
	 */
	public Long queryisExistZkjl(Context ctx, String zyh)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();

		// SELECT COUNT(1) Into :ll_count From ZY_ZKJL Where ZYH = :ll_zyh And
		// JGID = :Gl_jgid And (HCLX = 3 OR HCLX = 1) AND ZXBZ =

		long count = 0l, count2 = 0l;
		String hql = " ZKZT!=0 AND JGID = :al_jgid and ZYH = :al_zyh";
		String hql2 = "ZYH = :al_zyh And JGID = :al_jgid And (HCLX = 3 OR HCLX = 1) AND ZXBZ =2";
		try {

			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_jgid", user.getManageUnitId());// 机构ID
			map_par.put("al_zyh", parseLong(zyh));

			count2 = dao.doCount("ZY_ZKJL", hql2, map_par);
			if (count2 > 0) {

				count = dao.doCount("ZY_BRRY", hql, map_par);
			}
		} catch (Exception e) {
			logger.error("query fails", e);
		}
		return count;

	}

	/**
	 * 查看病人入院表中转科状态
	 * 
	 * @param ctx
	 * @param zyh
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Long queryisExistBrinfo(Context ctx, Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();

		long count = 0l;
		String hql = "";
		hql = "JGID = :al_jgid and ZYH = :al_zyh and BRBQ =:il_bqks";
		try {

			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_jgid", user.getManageUnitId());// 机构ID
			map_par.put("al_zyh", parseLong(body.get("zyh").toString()));

			map_par.put("il_bqks", parseLong(body.get("CWHM").toString()));
			count = dao.doCount("ZY_BRRY", hql, map_par);
		} catch (Exception e) {
			logger.error("query fails", e);
		}
		return count;

	}

	/**
	 * 
	 * @author zfs
	 * @createDate 2013-5-8
	 * @description 查询是否有未退包床
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Long queryIsExistBc(Context ctx, String zyh)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long count = 0l;
		StringBuffer hql = new StringBuffer();

		hql.append("ZYH = :al_zyh  AND JGID = :al_jgid");

		try {

			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_jgid", user.getManageUnitId());// 机构ID
			map_par.put("al_zyh", parseLong(zyh));
			count = dao.doCount("ZY_CWSZ", hql.toString(), map_par);

		} catch (Exception e) {
			logger.error("check the bed of not checkout exception",
					e.getMessage());

		}
		return count;
	}

	/**
	 * 查询是否有出院证
	 */
	public Long queryIsExistCyz(Context ctx, Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long count = 0l;
		StringBuffer hql = new StringBuffer();

		hql.append("JGID = :gl_jgid and ZYH= :ll_zyh and CZLX=-1 and BRKS =:il_hsql AND BQPB = 1");

		try {

			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("gl_jgid", user.getManageUnitId());// 机构ID
			map_par.put("ll_zyh", parseLong(body.get("zyh").toString()));
			map_par.put("il_hsql", parseLong(body.get("CWHM").toString()));
			count = dao.doCount("ZY_RCJL", hql.toString(), map_par);

		} catch (Exception e) {
			logger.error("check the bed of not checkout exception",
					e.getMessage());

		}
		return count;
	}

	/**
	 * 有会诊申请转科给予提示
	 */
	public Long queryIsExistHz(Context ctx, Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long count = 0l;
		StringBuffer hql = new StringBuffer();

		hql.append("ZYH = :ll_zyh And HZKS Is Not Null And HZKS <> 0 AND JGID = :gl_jgid");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("gl_jgid", user.getManageUnitId());// 机构ID
			map_par.put("ll_zyh", parseLong(body.get("zyh").toString()));
			count = dao.doCount("ZY_BRRY", hql.toString(), map_par);

		} catch (Exception e) {
			logger.error("hz exception", e.getMessage());

		}
		return count;
	}

	/**
	 * 查看是否有皮试药品
	 */
	public Long queryIsExistPs(Context ctx, Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long count = 0l;
		StringBuffer hql = new StringBuffer();

		hql.append("PSPB = 1 And ( PSJG IS NULL or PSJG = 0 ) And zyh = :ll_zyh And JGID = :gl_jgid");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("gl_jgid", user.getManageUnitId());// 机构ID
			map_par.put("ll_zyh", parseLong(body.get("zyh").toString()));
			count = dao.doCount("ZY_BQYZ", hql.toString(), map_par);

		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return count;
	}

	/**
	 * 未停未提交医嘱表单检索数据
	 */
	public List<Map<String, Object>> queryIsExistyzbd(Context ctx,
			Map<String, Object> body) throws ModelDataOperationException {

		StringBuffer hql = new StringBuffer();

		hql.append("SELECT ZY_BQYZ.YZMC as YZMC FROM ZY_BQYZ WHERE ( ZYH = :al_zyh ) and ( ZY_BQYZ.YSBZ = 0 OR  ( ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND ( LSBZ = 0  OR ( LSBZ = 2  AND SYBZ = 0 ) ) AND ( JFBZ < 3 OR JFBZ = 9) AND FYSX <> 2");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_zyh", parseLong(body.get("zyh").toString()));
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.ZY_BQYZ_ZYFY");
			return list;
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return null;
	}

	/**
	 * 退药医嘱未提交或未确认表单记录数
	 */
	public List<Map<String, Object>> queryIsExisttyzy(Context ctx,
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();

		hql.append("SELECT YK_TYPK.YPMC as YPMC, BQ_TYMX.YPXH as YPXH FROM BQ_TYMX,YK_TYPK WHERE (YK_TYPK.YPXH = BQ_TYMX.YPXH) AND (BQ_TYMX.TJBZ = 1 OR BQ_TYMX.TYRQ IS NULL) AND (BQ_TYMX.ZYH  = :al_zyh )");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_zyh", parseLong(body.get("zyh").toString()));
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.BQ_TYMX");
			return list;
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return null;
	}

	/**
	 * 退费单未确认表单记录数
	 */
	public List<Map<String, Object>> queryIsExistf(Context ctx,
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();

		hql.append("SELECT   BQ_TFMX.FYXH as FYXH FROM BQ_TFMX   WHERE ( BQ_TFMX.ZYH = :al_zyh ) AND  ( BQ_TFMX.TFRQ IS NULL ) ");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_zyh", parseLong(body.get("zyh").toString()));
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.BQ_TFMX");

			return list;
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return null;
	}

	/**
	 * 药品医嘱未提交未发药
	 */
	public List<Map<String, Object>> queryIsExistyp(Context ctx,
			Map<String, Object> body) throws ModelDataOperationException {

		StringBuffer hql = new StringBuffer();

		hql.append("SELECT ZY_BQYZ.YZMC as YZMC FROM ZY_BQYZ WHERE ( ZYH = :al_zyh ) and (ZY_BQYZ.YPLX <> 0) and ( ZY_BQYZ.YSBZ = 0 OR  ( ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND ( LSBZ = 0  OR ( LSBZ = 2  AND SYBZ = 0 ) ) AND ( JFBZ < 3 OR JFBZ = 9) AND FYSX <> 2 AND ZFYP=0");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_zyh", parseLong(body.get("zyh").toString()));
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.ZY_BQYZ_ZYFY");
			return list;
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return null;
	}

	/**
	 * 项目医嘱未提交或未执行
	 */
	public List<Map<String, Object>> queryIsExistxm(Context ctx,
			Map<String, Object> body) throws ModelDataOperationException {

		StringBuffer hql = new StringBuffer();

		hql.append("SELECT ZY_BQYZ.JLXH as JLXH,ZY_BQYZ.YCSL as YCSL,ZY_BQYZ.KSSJ as KSSJ,ZY_BQYZ.YPDJ as YPDJ,ZY_BQYZ.YSGH as YSGH,ZY_BQYZ.YZMC as YZMC FROM ZY_BQYZ WHERE ( ZYH = :al_zyh ) and (ZY_BQYZ.YPLX = 0) and ( ZY_BQYZ.YSBZ = 0 OR  ( ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND ( LSBZ = 0  OR ( LSBZ = 2  AND SYBZ = 0 ) ) AND ( JFBZ < 3 OR JFBZ = 9) AND FYSX <> 2");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_zyh", parseLong(body.get("zyh").toString()));
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.ZY_BQYZ_ZYFY");
			return list;
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return null;
	}

	/**
	 * 根据选后科室，动态级联换后病区
	 */
	public List<Map<String, Object>> querySelectList(Context ctx,
			Map<String, Object> body) {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		StringBuffer hql = new StringBuffer();

		hql.append(
				" SELECT DISTINCT A.KSDM as KSDM,B.OFFICENAME as OFFICENAME FROM ZY_CWSZ A,SYS_Office B")
				.append(" WHERE (A.KSDM = B.ID) AND ( A.CWKS = :AL_CWKS) and ( A.JGID = :al_jgid)");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_jgid", user.getManageUnitId());// 机构ID
			map_par.put("AL_CWKS", parseLong(body.get("hhks").toString()));
			list = dao.doSqlQuery(hql.toString(), map_par);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.SYS_Office);
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return list;
	}

	/**
	 * 
	 * @author zfs
	 * @createDate 2012-11-13
	 * @description 保存转床记录
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveZkjl(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("HCLX", "1");// 换床类型
		map.put("ZXBZ", "2");// 2为病区确认
		map.put("YSSQRQ", new Date());// 医生申请时间
		map.put("HQCH", body.get("brch").toString());// 换前床号
		map.put("HQKS", body.get("brks").toString());// 换前科室
		map.put("HQBQ", body.get("brbq").toString());// 换前病区

		map.put("HQYS", body.get("zrys") != null ? body.get("zrys").toString()
				: null);// 换前医生
		map.put("ZYH", body.get("zyh").toString());// 住院号
		map.put("ZYHM", body.get("zyhm").toString());// 住院号码

		map.put("HHYS", body.get("hhys").toString());// 换后医生

		map.put("HHKS", body.get("hhks").toString());// 换后科室

		map.put("HHBQ", body.get("hhbq").toString());// 换后科室
		map.put("BQSQGH", user.getUserId());// 病区申请工号
		map.put("BQSQRQ", new Date());// 病区申请时间
		map.put("JGID", jgid);// 机构
		try {
			// 保存转科记录
			Map<String, Object> jlxhmap = dao.doInsert(
					BSPHISEntryNames.ZY_ZKJL, map, false);
			body.put("JLXH", jlxhmap.get("JLXH") + "");
			saveZkYz(body, ctx);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-13
	 * @description 更新病人入院表
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void updateBrry(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update ZY_BRRY set ZKZT = 1, ZSYS =:zrys") // 更改转科状态值 和
																// 同步病人主治医师
				.append(" where ZYH=:zyh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zrys", body.get("hhys").toString());
		map_par.put("zyh", parseLong(body.get("zyh")));
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("fails", e);
		}

	}

	/**
	 * 取消转科
	 */
	public void updateundoZk(Map<String, Object> res, Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long count = 0l;
		StringBuffer hql = new StringBuffer();
		hql.append(" ZYH = :ll_zyh and JGID = :gl_jgid and ZKZT=0");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("gl_jgid", user.getManageUnitId());// 机构ID
			map_par.put("ll_zyh", parseLong(body.get("zyh").toString()));
			count = dao.doCount("ZY_BRRY", hql.toString(), map_par);

			if (count > 1) {
				res.put("body", 1);
			} else {
				Map<String, Object> zkjlInfo = getZkInfo(body.get("zyh")
						.toString());
				if (zkjlInfo.size() > 0) {
					// 删除转科记录
					Map<String, Object> delzqyzpar = new HashMap<String, Object>();
					delzqyzpar.put("YWID", parseLong(zkjlInfo.get("JLXH")));
					delzqyzpar.put("JGID", user.getManageUnitId());
					delzqyzpar.put("YDLB", "302");
					dao.doRemove(parseLong(zkjlInfo.get("JLXH")),
							BSPHISEntryNames.ZY_ZKJL);
					dao.doUpdate(
							"delete from ZY_BQYZ where YWID=:YWID and JGID=:JGID and YDLB=:YDLB",
							delzqyzpar);
					Object hqysObj = zkjlInfo.get("HQYS"); // 换前医生
					String hqys = "";
					if (hqysObj != null) {
						hqys = hqysObj.toString();
					}
					StringBuffer hql2 = new StringBuffer();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("zyh", parseLong(body.get("zyh")));
					map.put("gl_jgid", user.getManageUnitId());// 机构ID
					map.put("zsys", hqys);
					hql2.append("update ZY_BRRY set ZKZT = 0,ZSYS =:zsys where ZKZT = 1 and ZYH=:zyh and JGID = :gl_jgid");
					dao.doUpdate(hql2.toString(), map);
				}
				res.put("body", 2);
			}
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}

	}

	/**
	 * 根据住院号查询记录序号
	 * 
	 * @throws PersistentDataOperationException
	 */
	private Map<String, Object> getZkInfo(String zyh)
			throws PersistentDataOperationException {

		Map<String, Object> parameterscwszold = new HashMap<String, Object>();
		parameterscwszold.put("zyh", Long.parseLong(zyh));
		Map<String, Object> ksdmoldmap;
		ksdmoldmap = dao
				.doLoad("SELECT JLXH as JLXH, HQYS as HQYS from ZY_ZKJL where ZYH=:zyh and (HCLX = 3 or HCLX = 1) and ZXBZ = 2",
						parameterscwszold);
		return ksdmoldmap;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
		// if (number > 8) {
		// return 0;
		// }
		// double x = Math.pow(10, number);
		// return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data, number);
	}

	/**
	 * 生成会诊医嘱记录----------------------------------------
	 */
	public Map<String, Object> saveZkYz(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ZYH", Long.parseLong(body.get("zyh").toString()));// 住院号
		Map<String, Object> zyyspar = new HashMap<String, Object>();
		zyyspar.put("ZYH", Long.parseLong(body.get("zyh").toString()));
		try {
			map.put("BRKS", Long.parseLong(body.get("brks").toString()));// 病人科室
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
					"转往"
							+ DictionaryController.instance()
									.getDic("phis.dictionary.department_zy")
									.getText(body.get("hhks") + "") + "科室");// 药嘱名称
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
			List<Map<String, Object>> yzzhlist = dao
					.doSqlQuery(
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
			map.put("YWID", Long.parseLong(body.get("JLXH").toString()));// 申请序号
			map.put("KSSJ", new Date());// 开嘱时间
			map.put("YDLB", "302");// 约定类别
			map.put("SRKS", Long.parseLong(body.get("brks").toString()));// 输入科室
			map.put("ZXKS", Long.parseLong(body.get("brks").toString()));// 执行科室
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
			throw new ModelDataOperationException("转科保存失败!", e);
		}
	}
}
