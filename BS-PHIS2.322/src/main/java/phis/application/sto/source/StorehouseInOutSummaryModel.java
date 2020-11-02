package phis.application.sto.source;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
/**
 * 出入库汇总
 * @author caijy
 *
 */
public class StorehouseInOutSummaryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseInOutSummaryModel.class);

	public StorehouseInOutSummaryModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药品出库汇总查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadStorehouseOutSummary(
			Map<String, Object> body,Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String ldt_start = MedicineUtils.parseString(body.get("KSRQ"));// 开始时间
		String ldt_end = MedicineUtils.parseString(body.get("JSRQ"));// 结束时间
		StringBuffer hql_ckfs = new StringBuffer();// 出库方式查询
		hql_ckfs.append("select CKFS as CKFS,FSMC as FSMC from YK_CKFS where XTSB = :yksb and jgid = :jgid");
		StringBuffer hql_ckfs_count = new StringBuffer();// 查询出库方式总数
		hql_ckfs_count.append(" XTSB = :yksb and jgid = :jgid");
		StringBuffer hql_ckdh = new StringBuffer();// 查询出库单号范围
		hql_ckdh.append("select min(CKDH) as CKDH_b , max(CKDH) as CKDH_e, count(CKDH) as DJSL  from YK_CK01 where JGID = :jgid  AND to_char(CKRQ,'yyyymmdd') >= :start and to_char(CKRQ,'yyyymmdd') <= :end and CKFS = :ckfs  and XTSB = :yksb  and CKPB = 1");
		StringBuffer hql_ckhz = new StringBuffer();// 查询出库总金额
		hql_ckhz.append("select nvl(sum(b.JHJE),0) as JHJE, nvl(sum(b.PFJE),0) as PFHJ, nvl(sum(b.LSJE),0) as LSHJ from YK_CK01  a,YK_CK02  b where to_char(a.CKRQ,'yyyymmdd') >= :start and to_char(a.CKRQ,'yyyymmdd') <= :end and b.CKFS = :ckfs and b.XTSB = :yksb and a.XTSB = b.XTSB and a.JGID = :jgid and a.CKFS = b.CKFS and a.CKDH = b.CKDH and a.CKPB = 1");
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = MedicineUtils.parseInt(req.get("pageSize"));
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = MedicineUtils.parseInt(req.get("pageNo")) - 1;
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		map_par.put("start", ldt_start);
		map_par.put("end", ldt_end);
		map_par.put("yksb", yksb);
		try {
			Map<String, Object> map_par_ckfs = new HashMap<String, Object>();
			map_par_ckfs.put("yksb", yksb);
			map_par_ckfs.put("jgid", jgid);
			map_par_ckfs.put("first", pageNo * pageSize);
			map_par_ckfs.put("max", pageSize);
			List<Map<String, Object>> list_ckfs = dao.doQuery(
					hql_ckfs.toString(), map_par_ckfs); // 出库方式集合
			List<Map<String, Object>> crkhz_list = new ArrayList<Map<String, Object>>();
			DecimalFormat decimalFormat = new DecimalFormat("#.00");
			// 遍历每种出库方式，汇总数据。
			for (Map<String, Object> map_ckfs : list_ckfs) {
				map_par.put("ckfs",
						MedicineUtils.parseInt(map_ckfs.get("CKFS")));
				Map<String, Object> map_tmp = new HashMap<String, Object>();
				map_tmp.putAll(dao.doQuery(hql_ckdh.toString(), map_par).get(0));
				map_tmp.putAll((dao.doQuery(hql_ckhz.toString(), map_par)
						.get(0)));
				map_tmp.put("CKFS", map_ckfs.get("CKFS"));
				double ce = MedicineUtils.parseDouble(map_tmp.get("LSHJ"))
						- MedicineUtils.parseDouble(map_tmp.get("JHJE"));
				if (decimalFormat.format(ce).equals("-.00"))
					ce = 0.00;
				map_tmp.put("CE", ce);
				// 编号范围
				if (!"null".equals(map_tmp.get("CKDH_b") + "")
						&& !"null".equals(map_tmp.get("CKDH_e") + "")) {
					map_tmp.put(
							"BHFW",
							map_tmp.get("CKDH_b") + " - "
									+ map_tmp.get("CKDH_e"));
				} else if ("null".equals(map_tmp.get("CKDH_b") + "")
						&& "null".equals(map_tmp.get("CKDH_e") + "")) {
					map_tmp.put("BHFW", "     -");
				} else {
					map_tmp.put(
							"BHFW",
							"null".equals(map_tmp.get("CKDH_b")) ? "null"
									.equals(map_tmp.get("CKDH_e")) : "null"
									.equals(map_tmp.get("CKDH_b")));
				}
				map_tmp.put("FSMC", map_ckfs.get("FSMC"));
				crkhz_list.add(map_tmp);
			}
			map_par_ckfs.remove("first");
			map_par_ckfs.remove("max");
			long l = dao.doCount("YK_CKFS", hql_ckfs_count.toString(),
					map_par_ckfs);
			map_ret.put("totalCount", l);
			map_ret.put("body", crkhz_list);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品出库汇总查询失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药品入库汇总查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadStorehouseInSummary(
			Map<String, Object> body,Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String ldt_start = MedicineUtils.parseString(body.get("KSRQ"));// 开始时间
		String ldt_end = MedicineUtils.parseString(body.get("JSRQ"));// 结束时间
		StringBuffer hql_rkfs = new StringBuffer();// 入库方式查询
		hql_rkfs.append("select RKFS as RKFS,FSMC as FSMC from YK_RKFS  where XTSB = :yksb and jgid = :jgid");
		StringBuffer hql_rkfs_count = new StringBuffer();// 查询入库方式总数
		hql_rkfs_count.append(" XTSB = :yksb and jgid = :jgid");
		StringBuffer hql_rkdh = new StringBuffer();// 查询入库单号范围
		hql_rkdh.append("select min(RKDH) as RKDH_b , max(RKDH) as RKDH_e, count(RKDH) as DJSL  from YK_RK01 where JGID = :jgid  AND to_char(RKRQ,'yyyymmdd') >= :start and to_char(RKRQ,'yyyymmdd') <= :end and RKFS = :rkfs  and XTSB = :yksb  and RKPB = 1");
		StringBuffer hql_rkhz = new StringBuffer();// 查询入库总金额
		hql_rkhz.append("select nvl(sum(b.JHHJ),0) as JHHJ, nvl(sum(b.PFJE),0) as PFHJ, nvl(sum(b.LSJE),0) as LSHJ from YK_RK01 a,YK_RK02  b where to_char(a.RKRQ,'yyyymmdd') >= :start and to_char(a.RKRQ,'yyyymmdd') <= :end and b.RKFS = :rkfs and b.XTSB = :yksb and a.XTSB = b.XTSB and a.JGID = :jgid and a.RKFS = b.RKFS and a.RKDH = b.RKDH and a.RKPB = 1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		map_par.put("start", ldt_start);
		map_par.put("end", ldt_end);
		map_par.put("yksb", yksb);
		try {
			Map<String, Object> map_par_rkfs = new HashMap<String, Object>();
			map_par_rkfs.put("yksb", yksb);
			map_par_rkfs.put("jgid", jgid);
			MedicineUtils.getPageInfo(req, map_par_rkfs);
			List<Map<String, Object>> list_ckfs = dao.doQuery(
					hql_rkfs.toString(), map_par_rkfs); // 出库方式集合
			List<Map<String, Object>> crkhz_list = new ArrayList<Map<String, Object>>();
			DecimalFormat decimalFormat = new DecimalFormat("#.00");
			// 遍历每种出库方式，汇总数据。
			for (Map<String, Object> map_ckfs : list_ckfs) {
				map_par.put("rkfs",
						MedicineUtils.parseInt(map_ckfs.get("RKFS")));
				Map<String, Object> map_tmp = new HashMap<String, Object>();
				map_tmp.putAll(dao.doQuery(hql_rkdh.toString(), map_par).get(0));
				map_tmp.putAll((dao.doQuery(hql_rkhz.toString(), map_par)
						.get(0)));
				map_tmp.put("RKFS", map_ckfs.get("RKFS"));
				double ce = Double.parseDouble(map_tmp.get("LSHJ") + "")
						- Double.parseDouble(map_tmp.get("JHHJ") + "");
				if (decimalFormat.format(ce).equals("-.00"))
					ce = 0.00;
				map_tmp.put("CE", ce);
				// 编号范围
				if (!"null".equals(map_tmp.get("RKDH_b") + "")
						&& !"null".equals(map_tmp.get("RKDH_e") + "")) {
					map_tmp.put(
							"BHFW",
							map_tmp.get("RKDH_b") + " - "
									+ map_tmp.get("RKDH_e"));
				} else if ("null".equals(map_tmp.get("RKDH_b") + "")
						&& "null".equals(map_tmp.get("RKDH_e") + "")) {
					map_tmp.put("BHFW", "     -");
				} else {
					map_tmp.put(
							"BHFW",
							"null".equals(map_tmp.get("RKDH_b")) ? "null"
									.equals(map_tmp.get("RKDH_e")) : "null"
									.equals(map_tmp.get("RKDH_b")));
				}
				map_tmp.put("FSMC", map_ckfs.get("FSMC"));
				crkhz_list.add(map_tmp);
			}
			map_par_rkfs.remove("first");
			map_par_rkfs.remove("max");
			//long l = dao.doCount("YK_CKFS", hql_rkfs_count.toString(),map_par_rkfs);
			long l = list_ckfs.size();
		    map_ret.put("totalCount", l);  
			map_ret.put("body", crkhz_list);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品入库汇总查询失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药库出入库汇总明细信息查询
	 * @updateInfo
	 * @param body
	 * @param type
	 *            1是出库 2是入库
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadStorehouseInOutSummaryDetailList(
			Map<String, Object> body,Map<String, Object> req ,int type, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("start", MedicineUtils.parseString(body.get("KSRQ")));// 开始时间
		map_par.put("end", MedicineUtils.parseString(body.get("JSRQ")));// 结束时间
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
		map_par.put("yksb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));// 用户的药库识别
		map_par.put("fsid", MedicineUtils.parseInt(body.get("FSID")));
		StringBuffer hql = new StringBuffer();
		if (type == 1) {// 出库
			hql.append("select c.YPMC as YPMC, c.YPGG as YPGG, c.YPDW as YPDW,d.CDMC as CDMC,sum(b.SFSL) as SFSL, sum(b.LSJE) as LSJE, sum(b.PFJE) as  PFJE, sum(b.JHJE) as JHJE,c.YPSX as YPSX,c.YPDM as YPDM,c.PYDM as PYDM, b.YPXH as YPXH,b.YPCD as YPCD from YK_CK01 a,YK_CK02 b,YK_TYPK c,YK_CDDZ d where a.CKFS=:fsid and a.CKPB=1 and to_char(a.CKRQ,'yyyymmdd')>=:start and to_char(a.CKRQ,'yyyymmdd')<=:end and a.XTSB =:yksb and a.JGID =:jgid and (b.XTSB = a.XTSB) and (b.CKFS = a.CKFS) and (b.CKDH = a.CKDH) and (b.YPCD = d.YPCD) and (b.YPXH = c.YPXH) GROUP BY b.YPXH,b.YPCD,c.YPMC,d.CDMC,c.PYDM,c.YPSX,c.YPDW,c.YPDM,c.YPGG ORDER BY c.YPSX ASC, c.PYDM ASC");
		} else {// 入库
			hql.append("select c.YPMC as YPMC, c.YPGG as YPGG, c.YPDW as YPDW,d.CDMC as CDMC,sum(b.RKSL) as RKSL, sum(b.LSJE) as LSJE, sum(b.PFJE) as  PFJE, sum(b.JHHJ) as JHJE,c.YPSX as YPSX,c.YPDM as YPDM,c.PYDM as PYDM, b.YPXH as YPXH,b.YPCD as YPCD from YK_RK01 a,YK_RK02 b,YK_TYPK c,YK_CDDZ d where a.RKFS=:fsid and a.RKPB=1 and to_char(a.RKRQ,'yyyymmdd')>=:start and to_char(a.RKRQ,'yyyymmdd')<=:end and a.XTSB =:yksb and a.JGID =:jgid and (b.XTSB = a.XTSB) and (b.RKFS = a.RKFS) and (b.RKDH = a.RKDH) and (b.YPCD = d.YPCD) and (b.YPXH = c.YPXH) GROUP BY b.YPXH,b.YPCD,c.YPMC,d.CDMC,c.PYDM,c.YPSX,c.YPDW,c.YPDM,c.YPGG ORDER BY c.YPSX ASC, c.PYDM ASC");
		}
		MedicineCommonModel model=new MedicineCommonModel(dao);
		map_ret=model.getPageInfoRecord(req, map_par, hql.toString(),null);
		return map_ret;
	}

}
