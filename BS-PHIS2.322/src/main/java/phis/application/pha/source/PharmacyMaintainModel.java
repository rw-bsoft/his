package phis.application.pha.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class PharmacyMaintainModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyMaintainModel.class);

	public PharmacyMaintainModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @createDate 2014-3-21
	 * @description 养护明细查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryConservationDetail(
			Map<String, Object> body, String op, Map<String, Object> req)
			throws ModelDataOperationException {
		String yhdh = MedicineUtils.parseString(body.get("YHDH"));
		List<Map<String, Object>> list_ret = null;
		Map<String, Object> map_ret = new HashMap<String, Object>();
		List<?> queryCnd = null;// 用于拼音码查询
		if (req.containsKey("cnd")) {
			queryCnd = (List<?>) req.get("cnd");
		}
		try {
			if ("0".equals(yhdh)) {// 新增
				UserRoleToken user = UserRoleToken.getCurrent();
				long yfsb = MedicineUtils.parseLong(user
						.getProperty("pharmacyId"));// 用户的药房识别
				StringBuffer hql = new StringBuffer();
				hql.append("select b.YPDW as YPDW,b.PYDM as PYDM, b.WBDM as WBDM, b.YPMC as YPMC, c.YFGG as YPGG,"
						+ " c.YFDW as YFDW, d.CDMC as CDMC, a.YPPH as YPPH, a.YPXQ as YPXQ, a.LSJG as LSJG, a.YPSL as YPSL,"
						+ "  a.YPSL as KCSL, b.TYPE as TYPE, a.YPCD as YPCD, a.LSJE as LSJE, a.JHJG as JHJG, "
						+ "a.JHJE as JHJE, a.PFJG as PFJG, a.PFJE as PFJE, a.JGID as JGID, a.SBXH as SBXH, "
						+ "a.YFSB as YFSB, a.CKBH as CKBH, a.YPXH as YPXH, a.JYBZ as JYBZ from YF_KCMX a "
						+ "left join YK_KCMX e on a.YPXH=e.YPXH and a.YPCD=e.YPCD and a.JGID=e.JGID and "
						+ "(a.YPPH = e.YPPH or (a.YPPH is null and e.YPPH is null)) and "
						+ "(a.YPXQ = e.YPXQ or (a.YPXQ is null and e.YPXQ is null)) and a.LSJG=e.LSJG"
						+ " and a.JHJG=e.JHJG, YK_TYPK b, YF_YPXX c, YK_CDDZ d where b.YPXH=a.YPXH and "
						+ "a.YPXH=c.YPXH and a.YFSB=c.YFSB and a.YPCD=d.YPCD and a.YFSB=:yfsb");
				if (queryCnd != null) {
					hql.append(" and ").append(
							ExpressionProcessor.instance().toString(queryCnd));
				}
				// hql.append(" order by a.SBXH");
				hql.append(" group by b.YPDW,b.PYDM, b.WBDM, b.YPMC, c.YFGG, c.YFDW, d.CDMC, a.YPPH, a.YPXQ, a.LSJG, "
						+ "a.YPSL, b.TYPE, a.YPCD, a.LSJE, a.JHJG, a.JHJE, a.PFJG, a.PFJE, a.JGID, a.SBXH, a.YFSB, a.CKBH, a.YPXH, a.JYBZ");
				Map<String, Object> map_par = new HashMap<String, Object>();

				// map_par.put("jgid", jgid);
				map_par.put("yfsb", yfsb);
				MedicineCommonModel model = new MedicineCommonModel(dao);
				return model.getPageInfoRecord(req, map_par, hql.toString(),
						"phis.application.pha.schemas.YF_YH02");
			}
			long xtsb = MedicineUtils.parseLong(body.get("XTSB"));
			List<?> cnd = CNDHelper.createArrayCnd("and",
					CNDHelper.createSimpleCnd("eq", "YHDH", "s", yhdh),
					CNDHelper.createSimpleCnd("eq", "XTSB", "l", xtsb));
			if (queryCnd != null) {
				cnd = CNDHelper.createArrayCnd("and", cnd, queryCnd);
			}
			list_ret = dao.doList(cnd, null,
					"phis.application.pha.schemas.YF_YH02");
			if ("read".equals(op)) {// 如果是查看,直接显示合格数量
				return dao.doList(cnd, null,
						"phis.application.pha.schemas.YF_YH02",
						(Integer) req.get("pageNo"),
						(Integer) req.get("pageSize"), null);
			}
			map_ret.put("totalCount", list_ret.size());
			List<Map<String, Object>> list_ret_body = new ArrayList<Map<String, Object>>();
			Map<String, Object> map_pageinfo = new HashMap<String, Object>();
			MedicineUtils.getPageInfo(req, map_pageinfo);
			for (int i = (Integer) map_pageinfo.get("first"); i < (Integer) map_pageinfo
					.get("first") + (Integer) map_pageinfo.get("max"); i++) {
				if (list_ret.size() > i) {
					list_ret_body.add(list_ret.get(i));
				}
			}
			map_ret.put("body", list_ret_body);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护明细查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "养护明细查询失败,查询条件输入错误", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @createDate 2014-3-24
	 * @description 养护单保存
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveConservation(Map<String, Object> body,
			String op) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药库识别
		Map<String, Object> yf_yh01 = (Map<String, Object>) body.get("YF_YH01");
		yf_yh01.put("XTSB", yksb);
		List<Map<String, Object>> list_yh02 = (List<Map<String, Object>>) body
				.get("YF_YH02");// 界面上修改的明细
		try {
			if ("create".equals(op)) {
				String yhdh = "1";
				StringBuffer hql_yhdh = new StringBuffer();// 查询当前药库的最大养护单号
				hql_yhdh.append("select max(YHDH) as YHDH from YF_YH01 where XTSB=:xtsb");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("xtsb", yksb);
				List<Map<String, Object>> list_yhdh = dao.doSqlQuery(
						hql_yhdh.toString(), map_par);
				if (list_yhdh != null && list_yhdh.size() > 0
						&& list_yhdh.get(0) != null
						&& list_yhdh.get(0).size() > 0
						&& list_yhdh.get(0).get("YHDH") != null
						&& !"".equals(list_yhdh.get(0).get("YHDH") + "")) {
					yhdh = (MedicineUtils.parseLong(list_yhdh.get(0)
							.get("YHDH")) + 1) + "";
				}
				yf_yh01.put("YHDH", yhdh);
				yf_yh01.put("XTSB", yksb);
				yf_yh01.put("JGID", jgid);
				yf_yh01.put("YPLB", 0);
				yf_yh01.put("KWLB", 0);
				yf_yh01.putAll(dao.doSave("create", "phis.application.pha.schemas.YF_YH01",
						yf_yh01, false));
				for (Map<String, Object> map_yh02 : list_yh02) {
					map_yh02.put("YHDH", yhdh);
					map_yh02.put("XTSB", yksb);
					map_yh02.put("JGID", jgid);
					// map_yh02.put("PFJG", 0);
					map_yh02.put(
							"JHJE",
							MedicineUtils.simpleMultiply(4,
									map_yh02.get("KCSL"), map_yh02.get("JHJG")));
					map_yh02.put(
							"PFJE",
							MedicineUtils.simpleMultiply(4,
									map_yh02.get("KCSL"), map_yh02.get("PFJG")));
					map_yh02.put(
							"LSJE",
							MedicineUtils.simpleMultiply(4,
									map_yh02.get("KCSL"), map_yh02.get("LSJG")));
					dao.doSave("create", "phis.application.pha.schemas.YF_YH02", map_yh02,
							false);
				}
			} else {
				StringBuffer hql_yqr = new StringBuffer();// 查询是否确认,已确认不能保存
				hql_yqr.append(" YHDH=:yhdh and XTSB=:xtsb and YSGH is not null");
				Map<String, Object> map_par_yqr = new HashMap<String, Object>();
				map_par_yqr.put("yhdh",
						MedicineUtils.parseString(yf_yh01.get("YHDH")));
				map_par_yqr.put("xtsb",
						MedicineUtils.parseLong(yf_yh01.get("XTSB")));
				long l = dao
						.doCount("YF_YH01", hql_yqr.toString(), map_par_yqr);
				if (l > 0) {
					return MedicineUtils.getRetMap("养护单已确认,不能保存!");
				}
				dao.doSave("update", "phis.application.pha.schemas.YF_YH01", yf_yh01, false);
				for (Map<String, Object> map_yh02 : list_yh02) {
					map_yh02.put(
							"JHJE",
							MedicineUtils.simpleMultiply(4,
									map_yh02.get("KCSL"), map_yh02.get("JHJG")));
					map_yh02.put(
							"PFJE",
							MedicineUtils.simpleMultiply(4,
									map_yh02.get("KCSL"), map_yh02.get("PFJG")));
					map_yh02.put(
							"LSJE",
							MedicineUtils.simpleMultiply(4,
									map_yh02.get("KCSL"), map_yh02.get("LSJG")));
					dao.doSave("update", "phis.application.pha.schemas.YF_YH02", map_yh02,
							false);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护单保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "养护单保存失败", e);
		}

		Map<String, Object> reMap = MedicineUtils.getRetMap();
		reMap.put("body", yf_yh01);
		return reMap;
	}

	/**
	 * 
	 * @createDate 2014-3-24
	 * @description 养护单确认
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveConservationCommit(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> YF_YH01 = (Map<String, Object>) body.get("YF_YH01");
		Map<String, Object> map_saveret = saveConservation(body, "update");// 确认前先保存数据
		if (MedicineUtils.parseInt(map_saveret.get("code")) > 300) {
			return map_saveret;
		}
		try {
			YF_YH01.put("YSGH", UserRoleToken.getCurrent().getId());
			YF_YH01.put("ZXRQ", new Date());
			dao.doSave("update", "phis.application.pha.schemas.YF_YH01", YF_YH01, false);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护单确认失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "养护单确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @createDate 2014-3-24
	 * @description 删除养护单
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeConservation(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yhdh", MedicineUtils.parseString(body.get("YHDH")));
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
		StringBuffer hql_isDelete = new StringBuffer();// 查询是否已经删除
		hql_isDelete.append(" XTSB=:xtsb and YHDH=:yhdh");
		StringBuffer hql_isCommit = new StringBuffer();// 查询是否已经确认
		hql_isCommit.append(" XTSB=:xtsb and YHDH=:yhdh and YSGH is not null");
		StringBuffer hql_yh01_delete = new StringBuffer();// 删除养护01
		StringBuffer hql_yh02_delete = new StringBuffer();// 删除养护02
		hql_yh01_delete
				.append("delete from YF_YH01 where XTSB=:xtsb and YHDH=:yhdh");
		hql_yh02_delete
				.append("delete from YF_YH02 where XTSB=:xtsb and YHDH=:yhdh");
		try {
			long l = dao.doCount("YF_YH01", hql_isDelete.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("养护单已删除,已刷新页面!");
			}
			l = dao.doCount("YF_YH01", hql_isCommit.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("养护单已确认,已刷新页面!");
			}
			dao.doSqlUpdate(hql_yh02_delete.toString(), map_par);
			dao.doSqlUpdate(hql_yh01_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护单删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}
}
