package phis.application.war.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * description:提醒DAO
  *@author:zhangfs
 * create on 2013-5-22
 */
public class RemainModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(RemainModel.class);

	private static final String ZY_ZKJL = "ZY_ZKJL";

	public RemainModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:转入列表
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void queryIn(Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();

			String sql = "SELECT ZY_ZKJL.JLXH as JLXH,ZY_ZKJL.ZYH as ZYH,  "
					+ " ZY_BRRY.BRXM as BRXM, ZY_BRRY.BRCH as BRCH,ZY_ZKJL.HCLX as HCLX,ZY_ZKJL.YSSQRQ as YSSQRQ,ZY_ZKJL.YSSQGH as YSSQGH,ZY_ZKJL.BQSQRQ as BQSQRQ,ZY_ZKJL.BQSQGH as BQSQGH,ZY_ZKJL.BQZXRQ as BQZXRQ,  "
					+ " ZY_ZKJL.BQZXGH as BQZXGH,ZY_ZKJL.YSZXRQ as YSZXRQ,ZY_ZKJL.YSZXGH as YSZXGH,ZY_ZKJL.ZXBZ as ZXBZ,ZY_ZKJL.HQCH as HQCH,ZY_ZKJL.HHCH as HHCH,ZY_ZKJL.HQBQ as HQBQ,ZY_ZKJL.HHBQ as HHBQ,  "
					+ " ZY_ZKJL.HQKS as HQKS,ZY_ZKJL.HHKS as HHKS,ZY_ZKJL.HQYS as HQYS,ZY_ZKJL.HHYS as HHYS,  ZY_ZKJL.JGID as JGID FROM ZY_ZKJL ZY_ZKJL,ZY_BRRY  ZY_BRRY WHERE ( ZY_BRRY.ZYH = ZY_ZKJL.ZYH ) and "
					+ " ( ZY_BRRY.JGID = ZY_ZKJL.JGID ) and  (ZY_ZKJL.HCLX = 3 OR ZY_ZKJL.HCLX = 1) AND ZY_ZKJL.HHBQ = :al_hsql AND "
					+ "ZY_ZKJL.ZXBZ = 2 AND  ZY_BRRY.JGID = :al_jgid ";
			map_par.put("al_jgid", jgid);
			map_par.put("al_hsql", Long.parseLong(req.get("warId").toString()));//用户所属病区
			List<Map<String, Object>> BRXX = dao.doQuery(sql, map_par);

			SchemaUtil.setDictionaryMassageForList(BRXX, "phis.application.hos.schemas.ZY_ZKJL_Remain");

			res.put("body", BRXX);
		} catch (Exception e) {
			logger.error("Load failed.", e);
		}
	}

	/**
	 * description:科室转出而对方未确认
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void queryOut(Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();

			String sql = "SELECT ZY_ZKJL.JLXH as JLXH,ZY_ZKJL.ZYH as ZYH,  "
				+ " ZY_BRRY.BRXM as BRXM, ZY_BRRY.BRCH as BRCH,ZY_ZKJL.HCLX as HCLX,ZY_ZKJL.YSSQRQ as YSSQRQ,ZY_ZKJL.YSSQGH as YSSQGH,ZY_ZKJL.BQSQRQ as BQSQRQ,ZY_ZKJL.BQSQGH as BQSQGH,ZY_ZKJL.BQZXRQ as BQZXRQ,  "
				+ " ZY_ZKJL.BQZXGH as BQZXGH,ZY_ZKJL.YSZXRQ as YSZXRQ,ZY_ZKJL.YSZXGH as YSZXGH,ZY_ZKJL.ZXBZ as ZXBZ,ZY_ZKJL.HQCH as HQCH,ZY_ZKJL.HHCH as HHCH,ZY_ZKJL.HQBQ as HQBQ,ZY_ZKJL.HHBQ as HHBQ,  "
				+ " ZY_ZKJL.HQKS as HQKS,ZY_ZKJL.HHKS as HHKS,ZY_ZKJL.HQYS as HQYS,ZY_ZKJL.HHYS as HHYS,  ZY_ZKJL.JGID as JGID FROM ZY_ZKJL ZY_ZKJL,ZY_BRRY  ZY_BRRY WHERE ( ZY_BRRY.ZYH = ZY_ZKJL.ZYH ) and "
				+ " ( ZY_BRRY.JGID = ZY_ZKJL.JGID ) and  (ZY_ZKJL.HCLX = 3 OR ZY_ZKJL.HCLX = 1) AND ZY_ZKJL.HQBQ = :al_hsql AND "
				+ "ZY_ZKJL.ZXBZ = 2 AND  ZY_BRRY.JGID = :al_jgid ";
			map_par.put("al_jgid", jgid);
			map_par.put("al_hsql",Long.parseLong(req.get("warId").toString()));//用户所属病区
			List<Map<String, Object>> BRXX= dao.doQuery(sql, map_par);

			SchemaUtil.setDictionaryMassageForList(BRXX, "phis.application.hos.schemas.ZY_ZKJL_Remain");

			res.put("body", BRXX);
		} catch (Exception e) {
			logger.error("Ward query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取科室转出而对方未确认信息出错!");
		}
	}

	/**
	 * description:保存
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public Map<String, Object> save(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ZYH", body.get("ZYH"));//住院号
		map.put("HQCH", body.get("HQCH"));//换前床号
		map.put("HHKS", body.get("HHKS"));//换后科室
		map.put("JGID", body.get("JGID"));//机构ID

		try {
			Map<String, Object> map2 = dao.doInsert(ZY_ZKJL, map, false);
			return map2;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 分配床位
	 */
	@SuppressWarnings("unchecked")
	public void queryCwInfo(Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer();
			sql.append("select a.BRCH as BRCH, a.ZYH as ZYH from ZY_CWSZ a where")
				.append(" a.KSDM=:al_cwbq and a.CWKS=:al_ksdm and a.ZYH is null AND a.JGID = :al_jgid")
				.append(" and (a.CWXB = (select BRXB from ZY_BRRY where ZYH = :zyh) or a.CWXB = 3)");
			map_par.put("al_jgid", jgid);
			map_par.put("al_cwbq", Long.parseLong(body.get("wardId").toString()));//用户所属病区
			map_par.put("al_ksdm", Long.parseLong(body.get("ksdm").toString()));
			map_par.put("zyh", Long.parseLong(body.get("zyh").toString()));
			List<Map<String, Object>> map = dao.doQuery(sql.toString(), map_par);
			SchemaUtil.setDictionaryMassageForList(map, BSPHISEntryNames.ZY_CWSZ);
			res.put("body", map);
		} catch (Exception e) {
			logger.error("Load failed.", e);

		}
	}

	/**
	 * 科室转出而对方未确认
	
	public void queryOutList(Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();

			String sql = "SELECT ZY_ZKJL.JLXH as JLXH,ZY_ZKJL.ZYH as ZYH,ZY_BRRY.BRXM as BRXM, ZY_BRRY.BRCH as BRCH,"
					+ "ZY_ZKJL.HCLX as HCLX, ZY_ZKJL.YSSQRQ as YSSQRQ, ZY_ZKJL.YSSQGH as YSSQGH, ZY_ZKJL.BQSQRQ as BQSQRQ, ZY_ZKJL.BQSQGH as BQSQGH,"
					+ "ZY_ZKJL.BQZXRQ as BQZXRQ,ZY_ZKJL.BQZXGH as BQZXGH,ZY_ZKJL.YSZXRQ as YSZXRQ, ZY_ZKJL.YSZXGH as YSZXGH,ZY_ZKJL.ZXBZ as ZXBZ,ZY_ZKJL.HQCH as HQCH,"
					+ "ZY_ZKJL.HHCH as HHCH, ZY_ZKJL.HQBQ as HQBQ, ZY_ZKJL.HHBQ as HHBQ,ZY_ZKJL.HQKS as HQKS, ZY_ZKJL.HHKS as HHKS,ZY_ZKJL.HQYS as HQYS,"
					+ "ZY_ZKJL.HHYS as HHYS, ZY_ZKJL.JGID as JGID FROM ZY_ZKJL ZY_ZKJL,ZY_BRRY ZY_BRRY  WHERE ( ZY_BRRY.ZYH = ZY_ZKJL.ZYH ) and "
					+ " ( ZY_BRRY.JGID = ZY_ZKJL.JGID ) and (ZY_ZKJL.HCLX = 3 OR  ZY_ZKJL.HCLX = 1) AND  "
					+ "ZY_ZKJL.HQBQ = :al_hsql AND ZY_ZKJL.ZXBZ = 2 AND ZY_BRRY.JGID = :al_jgid ";
			map_par.put("al_jgid", jgid);
			map_par.put("al_hsql", Long.parseLong(req.get("warId").toString()));//用户所属病区
			List<Map<String, Object>> BRXX = dao.doQuery(sql, map_par);

			SchemaUtil.setDictionaryMassageForList(BRXX, ZY_ZKJL);

			res.put("body", BRXX);
		} catch (Exception e) {
			logger.error("Load failed.", e);

		}
	} */

	/**
	 * 判断新床位是否有病人
	 */
	public Long queryIsExistPatient(Context ctx, String fpcw) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long count = 0l;
		StringBuffer hql = new StringBuffer();

		hql.append("BRCH = :fpcw and JGID = :al_jgid and (ZYH is not null or ZYH <> '')");

		try {

			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("al_jgid", user.getManageUnitId());//机构ID
			map_par.put("fpcw", fpcw);	//床号为字符型
			count = dao.doCount("ZY_CWSZ", hql.toString(), map_par);

		} catch (Exception e) {
			logger.error("check the bed of not checkout exception", e.getMessage());

		}
		return count;
	}
	
	public boolean checkBedSexLimit(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		boolean illegal = true;
		Map<String, Object> map_par = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		map_par.put("jgid", user.getManageUnitId()); //机构ID
		map_par.put("ksdm", Long.parseLong(body.get("wardId").toString()));//用户所属病区
		map_par.put("cwks", Long.parseLong(body.get("ksdm").toString()));
		map_par.put("zyh", Long.parseLong(body.get("zyh").toString()));
		map_par.put("brch", body.get("brch").toString());
		StringBuffer sql = new StringBuffer();
		sql.append("select a.BRCH as BRCH from ZY_CWSZ a where")
			.append(" a.KSDM=:ksdm and a.CWKS=:cwks and a.JGID = :jgid and a.BRCH =:brch")
			.append(" and (a.CWXB = (select BRXB from ZY_BRRY where ZYH = :zyh) or a.CWXB = 3)");
		try {
			Map<String, Object> map = dao.doLoad(sql.toString(), map_par);
			if(map!=null && map.size() > 0 && map.get("BRCH") != null) {
				illegal = false;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("check the bed of not checkout exception", e.getMessage());
		}
		return illegal;
	}

	/**
	 * 根据床位号码查询床位信息
	 */
	public List<Map<String, Object>> queryCwInfoByFpcw(Context ctx, String cwhm, Map<String, Object> res)
			throws ModelDataOperationException {

		StringBuffer hql = new StringBuffer();
		UserRoleToken user = UserRoleToken.getCurrent();
		hql.append("SELECT KSDM as KSDM,CWKS as CWKS,JCPB as JCPB,CWXB as CWXB  From ZY_CWSZ ZY_CWSZ  Where BRCH = :as_cwhm And JGID = :gl_jgid");

		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("as_cwhm", parseLong(cwhm));
			map_par.put("gl_jgid", user.getManageUnitId());//机构ID
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(), map_par);
			SchemaUtil.setDictionaryMassageForList(list, BSPHISEntryNames.ZY_CWSZ);
			res.put("body", list);
			return list;
		} catch (Exception e) {
			logger.error("ps exception", e.getMessage());

		}
		return null;
	}

	/**
	 * description:床位分配确认
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void savecwgl_zccl(Map<String, Object> parameters, BaseDAO dao, Context ctx,Map<String, Object> res) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId()+"";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Long ll_bqdm_Old = 0L;
		Long ll_bqdm_New = 0L;
		int ll_jcpb = 0;
		int ll_jcpb2 = 0;
		int ll_cwxb_New = 0;
		int ll_brxb_Old = 0;
		Long ll_bqks_Old = 0L;
		Long ll_bqks_New = 0L;
		Long ll_zyh_Old = 0L;
		try {
			// 取床位病区代码
			Map<String, Object> parameterscwszold = new HashMap<String, Object>();
			parameterscwszold.put("BRCH", parameters.get("as_cwhm_Old"));
			parameterscwszold.put("JGID", manaUnitId);
			Map<String, Object> ksdmoldmap;
			ksdmoldmap = dao
					.doLoad("SELECT KSDM as KSDM,CWKS as CWKS,JCPB as JCPB,CWXB as CWXB From ZY_CWSZ Where BRCH = :BRCH And JGID = :JGID",
							parameterscwszold);
			if (ksdmoldmap != null && ksdmoldmap.size() > 0) {
				ll_bqdm_Old = Long.parseLong(ksdmoldmap.get("KSDM") + "");
				ll_bqks_Old = Long.parseLong(ksdmoldmap.get("CWKS") + "");
				ll_jcpb = Integer.parseInt(ksdmoldmap.get("JCPB") + "");
				ll_brxb_Old = Integer.parseInt(ksdmoldmap.get("CWXB") + "");
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

			if (!ll_bqks_New.equals(Long.parseLong(parameters.get("al_zwks_New").toString()))) {
				res.put("mes", "所转科室与所转床位上病人科室不符,不能转科!");
			
				throw new ModelDataOperationException("所转科室与所转床位上病人科室不符，不能转科!");
			}
			if (ll_cwxb_New != 3 && ll_brxb_Old != 3) {
				if (ll_cwxb_New != ll_brxb_Old) {
					res.put("mes", "分配床位性别限制与病人转科前床位性别限制不匹配，不能转床!");

					throw new ModelDataOperationException("分配床位性别限制与病人转科前床位性别限制不匹配，不能转床!");
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
				parametersSaveCwtjks.put("CZRQ", sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjks.put("CZLX", 2);
				parametersSaveCwtjks.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjks.put("BRKS", ll_bqks_Old);
				parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
				parametersSaveCwtjks.put("XSYS", ll_cwsys_ks - 1);
				parametersSaveCwtjks.put("BQPB", 0);
				parametersSaveCwtjks.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ, parametersSaveCwtjks, false);
				Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
				parametersSaveCwtjbq.put("CZRQ", sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjbq.put("CZLX", 2);
				parametersSaveCwtjbq.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjbq.put("BRKS", ll_bqdm_Old);
				parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
				parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq - 1);
				parametersSaveCwtjbq.put("BQPB", 1);
				parametersSaveCwtjbq.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ, parametersSaveCwtjbq, false);
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
				parametersSaveCwtjks.put("CZRQ", sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjks.put("CZLX", 2);
				parametersSaveCwtjks.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjks.put("BRKS", ll_bqks_New);
				parametersSaveCwtjks.put("YSYS", ll_cwsys_ks);
				parametersSaveCwtjks.put("XSYS", ll_cwsys_ks + 1);
				parametersSaveCwtjks.put("BQPB", 0);
				parametersSaveCwtjks.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ, parametersSaveCwtjks, false);
				Map<String, Object> parametersSaveCwtjbq = new HashMap<String, Object>();
				parametersSaveCwtjbq.put("CZRQ", sdf.parse(sdf.format(new Date())));
				parametersSaveCwtjbq.put("CZLX", 2);
				parametersSaveCwtjbq.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
				parametersSaveCwtjbq.put("BRKS", ll_bqdm_New);
				parametersSaveCwtjbq.put("YSYS", ll_cwsys_bq);
				parametersSaveCwtjbq.put("XSYS", ll_cwsys_bq + 1);
				parametersSaveCwtjbq.put("BQPB", 1);
				parametersSaveCwtjbq.put("JGID", manaUnitId);
				dao.doSave("create", BSPHISEntryNames.ZY_CWTJ, parametersSaveCwtjbq, false);
			}

			//锁定原床位、新床位
			Map<String, Object> parameterscwzk = new HashMap<String, Object>();
			parameterscwzk.put("as_cwhm_Old", parameters.get("as_cwhm_Old"));
			parameterscwzk.put("gl_jgid", manaUnitId);
			//parameterscwsznew.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
			dao.doUpdate("UPDATE ZY_CWSZ Set ZYH = ZYH Where BRCH = :as_cwhm_Old And JGID = :gl_jgid", parameterscwzk);
			Map<String, Object> parametersnewcwzk = new HashMap<String, Object>();
			parametersnewcwzk.put("as_cwhm_New", parameters.get("as_cwhm_New"));
			parametersnewcwzk.put("gl_jgid", manaUnitId);
			//parametersnewcwzk.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
			dao.doUpdate("UPDATE ZY_CWSZ Set ZYH =ZYH Where BRCH = :as_cwhm_New And JGID = :gl_jgid", parametersnewcwzk);
			//判断原床位是否已发生变化
			Map<String, Object> parameterscwszzyh = new HashMap<String, Object>();
			parameterscwszzyh.put("JGID", manaUnitId);
			parameterscwszzyh.put("BRCH", parameters.get("as_cwhm_Old"));
			Map<String, Object> zyhmap = dao.doLoad(
					"SELECT ZYH as ZYH From ZY_CWSZ Where BRCH = :BRCH And JGID = :JGID", parameterscwszzyh);
			if (zyhmap != null && zyhmap.size() > 0) {
				if (zyhmap.get("ZYH") == null) {
					ll_zyh_Old = 0L;
				} else {
					ll_zyh_Old = Long.parseLong(zyhmap.get("ZYH") + "");
				}
			}
			if (ll_zyh_Old != Long.parseLong(parameters.get("al_zyh") + "")) {
				res.put("mes", "当前病人床位在转床前已发生变化，不能进行转科处理!");
				throw new ModelDataOperationException("当前病人床位在转床前已发生变化，不能进行转科处理!");
			}
			//3个update语句
			Map<String, Object> parametersnewcwzk3 = new HashMap<String, Object>();
			parametersnewcwzk3.put("ll_zyh_old", Long.parseLong(parameters.get("al_zyh") + ""));
			parametersnewcwzk3.put("as_cwhm_New", parameters.get("as_cwhm_New"));
			parametersnewcwzk3.put("gl_jgid", manaUnitId);
			dao.doUpdate(
					"UPDATE ZY_CWSZ Set ZYH = :ll_zyh_old Where BRCH = :as_cwhm_New And ZYH Is Null And JGID = :gl_jgid",
					parametersnewcwzk3);
			Map<String, Object> parametersnewcwzk4 = new HashMap<String, Object>();
			parametersnewcwzk4.put("ll_zyh_old", Long.parseLong(parameters.get("al_zyh") + ""));
			parametersnewcwzk4.put("al_zwks_New", Long.parseLong(parameters.get("al_zwks_New").toString()));
			parametersnewcwzk4.put("ll_bqdm_New", Long.parseLong(parameters.get("ll_bqdm_New").toString()));
			parametersnewcwzk4.put("as_cwhm_New", parameters.get("as_cwhm_New")+"");
//			parametersnewcwzk4.put("gl_jgid", manaUnitId);
			dao.doUpdate(
					"UPDATE ZY_BRRY SET BRCH = :as_cwhm_New , BRKS = :al_zwks_New , BRBQ = :ll_bqdm_New , JCKS = NULL Where ZYH = :ll_zyh_old",
					parametersnewcwzk4);

			Map<String, Object> parametersnewcwzk5 = new HashMap<String, Object>();
			parametersnewcwzk5.put("ll_zyh_old", Long.parseLong(parameters.get("al_zyh") + ""));
			parametersnewcwzk5.put("as_cwhm_Old", parameters.get("as_cwhm_Old"));
			parametersnewcwzk5.put("gl_jgid", manaUnitId);
			dao.doUpdate(
					"UPDATE ZY_CWSZ Set ZYH = Null Where BRCH = :as_cwhm_Old And ZYH = :ll_zyh_old And JGID = :gl_jgid",
					parametersnewcwzk5);
			/*Map<String, Object> parameterscwzk1 = new HashMap<String, Object>();
			parameterscwzk1.put("as_cwhm_Old", parameters.get("as_cwhm_Old"));
			parameterscwzk1.put("gl_jgid", manaUnitId);
			parametersnewcwzk3.put("ZYH",Long.parseLong(parameters.get("al_zyh") + ""));
			dao.doUpdate("UPDATE ZY_CWSZ Set ZYH = :ZYH Where BRCH = :as_cwhm_Old And JGID = :gl_jgid", parameterscwzk1);*/
			Map<String, Object> parametersSaveHcmx = new HashMap<String, Object>();
			parametersSaveHcmx.put("ZYH",Long.parseLong(parameters.get("al_zyh") + ""));
			parametersSaveHcmx.put("HCRQ", sdf.parse(sdf.format(new Date())));
			parametersSaveHcmx.put("HCLX", 2);
			parametersSaveHcmx.put("HQCH", parameters.get("as_cwhm_Old"));
			parametersSaveHcmx.put("HHCH", parameters.get("as_cwhm_New"));
			parametersSaveHcmx.put("HQKS", ll_bqks_Old);
			parametersSaveHcmx.put("HHKS", ll_bqks_New);
			parametersSaveHcmx.put("HQBQ", ll_bqdm_Old);
			parametersSaveHcmx.put("HHBQ", ll_bqdm_New);
			parametersSaveHcmx.put("JGID", manaUnitId);
			//parametersSaveHcmx.put("JSCS", 0);
		//	parametersSaveHcmx.put("CZGH", uid);
			//parametersSaveHcmx.put("JGID", manaUnitId);
			dao.doSave("create", BSPHISEntryNames.ZY_HCMX, parametersSaveHcmx,false);

			Map<String, Object> ywcl = new HashMap<String, Object>();
			ywcl.put("CZLX", -2);
			ywcl.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
			ywcl.put("RYRQ", sdf.format(new Date()));
			ywcl.put("BRKS", Long.parseLong(parameters.get("al_zwks_Old").toString()));
			ywcl.put("BQPB", 0);
			ywcl.put("CYFS", 0);

			ywcl.put("JGID", manaUnitId);
			BSPHISUtil.uf_ywcl(ywcl, dao, ctx);

			// 新科室转入记录
			Map<String, Object> ywc2 = new HashMap<String, Object>();
			ywc2.put("CZLX", 2);
			ywc2.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
			ywc2.put("RYRQ", sdf.format(new Date()));
			ywc2.put("BRKS", Long.parseLong(parameters.get("al_zwks_New").toString()));
			ywc2.put("BQPB", 0);
			ywc2.put("CYFS", 0);
			ywc2.put("JGID", manaUnitId);

			BSPHISUtil.uf_ywcl(ywc2, dao, ctx);

			if (!StringUtils.equals(parameters.get("ll_bqdm_Old").toString(), parameters.get("ll_bqdm_New").toString())) {
				//// 原病区转出记录
				Map<String, Object> ywc3 = new HashMap<String, Object>();
				ywc3.put("CZLX", -2);
				ywc3.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
				ywc3.put("RYRQ", sdf.format(new Date()));
				ywc3.put("BRKS", Long.parseLong(parameters.get("ll_bqdm_Old").toString()));
				ywc3.put("BQPB", 1);
				ywc3.put("CYFS", 0);
				ywc3.put("JGID", manaUnitId);

				BSPHISUtil.uf_ywcl(ywc3, dao, ctx);

				// 新病区转入记录
				Map<String, Object> ywc4 = new HashMap<String, Object>();
				ywc4.put("CZLX", 2);
				ywc4.put("ZYH", Long.parseLong(parameters.get("al_zyh") + ""));
				ywc4.put("RYRQ", sdf.format(new Date()));
				ywc4.put("BRKS", Long.parseLong(parameters.get("ll_bqdm_New").toString()));
				ywc4.put("BQPB", 1);
				ywc4.put("CYFS", 0);
				ywc4.put("JGID", manaUnitId);


				BSPHISUtil.uf_ywcl(ywc4, dao, ctx);

				
			}
			String HHYS = null;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("al_zyh", Long.parseLong(parameters.get("al_zyh") + ""));
			Map<String, Object> zyhmap111 = dao.doLoad("SELECT HHYS AS HHYS FROM ZY_ZKJL ZY_ZKJL WHERE ZYH =:al_zyh and (hclx =1 Or hclx =3) and ZXBZ =2",
					map);
			if (zyhmap111 != null && zyhmap111.size() > 0) {

				HHYS = zyhmap111.get("HHYS") + "";

			}
			Map<String, Object> x = new HashMap<String, Object>();
			x.put("ls_zzys", HHYS);
			x.put("al_zyh", Long.parseLong(parameters.get("al_zyh") + ""));
			x.put("gl_jgid", manaUnitId);
			dao.doUpdate("UPDATE ZY_BRRY Set ZKZT = 0,ZZYS = :ls_zzys Where ZYH = :al_zyh And ZKZT = 1 And JGID = :gl_jgid",
					x);

			Map<String, Object> y = new HashMap<String, Object>();
			y.put("ldt_Today", sdf.parse(sdf.format(new Date())));
			y.put("hhch", parameters.get("as_cwhm_New").toString());
			y.put("al_zyh", Long.parseLong(parameters.get("al_zyh") + ""));

			y.put("ygbh", user.getUserId()+"");
			dao.doUpdate("UPDATE ZY_ZKJL Set ZXBZ = 3,BQZXRQ = :ldt_Today,BQZXGH = :ygbh,HHCH = :hhch Where ZYH = :al_zyh And (hclx = 1 Or hclx = 3) And ZXBZ = 2",
					y);
			res.put("mes", "转科成功!");
		} catch (PersistentDataOperationException e) {
			res.put("mes", "换床位失败!");
			throw new ModelDataOperationException("换床位失败!", e);
		} catch (ValidateException e) {
			res.put("mes", "换床位失败!");
			throw new ModelDataOperationException("换床位失败!", e);
		} catch (ParseException e) {
			res.put("mes", "时间转换失败!");
			throw new ModelDataOperationException("时间转换失败!", e);
		}
	}

//	public static Map<String, Object> queryYGBH(String ygdm, BaseDAO dao) throws PersistentDataOperationException {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("ygdm", ygdm);
//		Map<String, Object> list = dao.doLoad("select PERSONID as PERSONID from SYS_Personnel WHERE PERSONID=:ygdm", map);
//		SchemaUtil.setDictionaryMassageForList(list, "YS_ZY_HZSQ");
//
//		return list;
//	}

	public static int cwtj(Map<String, Object> parameters, BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameterscwszks = new HashMap<String, Object>();
		parameterscwszks.put("KSDM", parameters.get("KSDM"));
		int ll_cwsys = 0;
		int bqpb = Integer.parseInt(parameters.get("BQPB") + "");
		try {
			if (bqpb == 0) {
				Long l = dao.doCount("ZY_CWSZ", "CWKS=:KSDM AND JCPB < 2 AND ZYH IS NOT NULL", parameterscwszks);
				ll_cwsys = Integer.parseInt(l + "");
			} else {
				Long l = dao.doCount("ZY_CWSZ", "KSDM =:KSDM AND JCPB < 2 AND ZYH IS NOT NULL", parameterscwszks);
				ll_cwsys = Integer.parseInt(l + "");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("统计床位使用信息失败!", e);
		}
		return ll_cwsys;
	}

	public void saveZycwtj(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CZRQ", "1");//换床类型
		map.put("CZLX", "2");//2为病区确认
		map.put("ZYH", new Date());//医生申请时间
		map.put("BRKS", body.get("brch").toString());//换前床号
		map.put("YSYS", body.get("brks").toString());//换前科室
		map.put("XSYS", body.get("brbq").toString());//换前病区

		map.put("BQPB", body.get("zzys") != null ? body.get("zzys").toString() : null);//换前医生
		map.put("JGID", jgid);//住院号

		try {
			dao.doInsert(BSPHISEntryNames.ZY_ZKJL, map, false);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * description:锁定原床位、新床位
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void updateBrry(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update ZY_CWSZ set ZYH=:zyh Where BRCH = :as_cwhm_Old And JGID = :gl_jgid");

		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", parseLong(body.get("zyh")));

		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("fails", e);
		}

	}

	/**
	 * description:
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void updateBrry2(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update ZY_CWSZ set ZYH=:zyh Where BRCH = :as_cwhm_Old And ZYH Is Null and  JGID = :gl_jgid");

		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", parseLong(body.get("zyh")));

		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("fails", e);
		}

	}

	public void updateBrry3(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update ZY_CWSZ SET ZYH =NULL Where BRCH = :as_cwhm_Old And ZYH = :ll_zyh_old  and  JGID = :gl_jgid");

		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", parseLong(body.get("zyh")));

		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("fails", e);
		}

	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
}
