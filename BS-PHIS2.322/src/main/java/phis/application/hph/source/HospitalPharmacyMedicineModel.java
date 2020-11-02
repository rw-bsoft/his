package phis.application.hph.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phis.application.mds.source.MedicineUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSPHISUtil;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class HospitalPharmacyMedicineModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPharmacyMedicineModel.class);

	public HospitalPharmacyMedicineModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author taojh
	 * @createDate 2014-3-31
	 * @description 发药
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void queryBRFYXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long bqsb = parseLong(user.getProperty("wardId"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		parameters.put("jgid", jgid);
		parameters.put("bqsb", bqsb);
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		StringBuffer hql = new StringBuffer(
				"select distinct a.BRXM as BRXM, (case when a.CYPB=0 then '在院' when a.CYPB=1 then '出院证明' when a.CYPB=2 then '预结出院' when a.CYPB=8 then '正常出院' when a.CYPB=9 then '终结出院' when a.CYPB=99 then '注销出院' end) as CYPB,a.BRCH as BRCH,a.BRID as BRID, a.ZYH as ZYH, a.ZYHM as ZYHM")
				.append(" from ZY_BRRY a, YF_ZYFYMX  b ")
				.append(" where b.LYBQ= :bqsb and a.ZYH=b.ZYH and a.JGID=:jgid ");
		if (body.get("FYSB") != null && body.get("FYSB").equals("FY")) {
			hql.append(" and b.YPSL > 0 ");
		}
		if (body.get("FYSB") != null && body.get("FYSB").equals("TY")) {
			hql.append(" and b.YPSL < 0 ");
		}
		if (body.get("dateFrom") != null) {
			hql.append(" and to_char(b.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
					.append(body.get("dateFrom")).append("'");
		}
		if (body.get("dateTo") != null) {
			hql.append(" and to_char(b.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
					.append(body.get("dateTo")).append("'");
		}
		if (body.get("FYFS") != null && !body.get("FYFS").equals("")) {
			hql.append(" and b.FYFS=:FYFS ");
			parameters.put("FYFS", MedicineUtils.parseLong(body.get("FYFS")));
		}
		if (body.get("YF") != null && !body.get("YF").equals("")) {
			hql.append(" and b.YFSB=:YF ");
			parameters.put("YF", MedicineUtils.parseLong(body.get("YF")));
		}
		try {
			int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("CYPB").equals("正常出院")
						|| list.get(i).get("CYPB").equals("终结出院")
						|| list.get(i).get("CYPB").equals("注销出院")) {
					list.get(i).put("BRCH", "");
				}
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询发药病人信息失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void queryBRFYXM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", JGID);
			Long bq = 0L;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				bq = Long.parseLong(user.getProperty("wardId") + "");
			}
			parameters.put("bqsb", bq);
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int first = 0;
			if (req.containsKey("pageNo")) {
				first = (Integer) req.get("pageNo") - 1;
			}

			Map<String, Object> body = (Map<String, Object>) req.get("body");
			StringBuffer hql = new StringBuffer(
					"select a.YPGG as YFGG,a.YFDW as YFDW,sum(a.YPSL) as YCSL,sum(a.LSJE) as FYJE,a.YPDJ as YPDJ,a.YFSB as YFSB,a.YPXH as YPXH,e.CDMC as CDMC,b.BRXM as BRXM, d.YPMC as YPMC ")
					.append(" from  YF_ZYFYMX a, ZY_BRRY b, YK_TYPK d, YK_CDDZ e ")
					.append(" where a.JGID=:jgid and a.LYBQ=:bqsb and a.ZYH=b.ZYH")
					.append(" and a.YPXH=d.YPXH")
					.append(" and a.YPCD=e.YPCD and a.YPSL>0 ");
			if (body.get("dateFrom") != null) {
				hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
						.append(body.get("dateFrom")).append("'");
			}
			if (body.get("dateTo") != null) {
				hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
						.append(body.get("dateTo")).append("'");
			}
			if (body.get("FYFS") != null && !body.get("FYFS").equals("")) {
				hql.append(" and a.FYFS=:FYFS ");
				parameters.put("FYFS",
						MedicineUtils.parseLong(body.get("FYFS")));
			}
			if (body.get("YF") != null && !body.get("YF").equals("")) {
				hql.append(" and a.YFSB=:YF ");
				parameters.put("YF", MedicineUtils.parseLong(body.get("YF")));
			}
			long zyh = 0;
			if (body.get("ZYH") != null) {
				zyh = Long.parseLong(body.get("ZYH") + "");
			}
			hql.append(" and b.ZYH=:ZYH ");
			parameters.put("ZYH", zyh);
			hql.append("group by a.YPGG,a.YFDW,a.YPDJ,a.YFSB,a.YPXH,e.CDMC,b.BRXM,d.YPMC ");

			int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病人发药明细失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void queryBRFYXMBack(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", JGID);
			Long bq = 0L;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				bq = Long.parseLong(user.getProperty("wardId") + "");
			}
			parameters.put("bqsb", bq);
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int first = 0;
			if (req.containsKey("pageNo")) {
				first = (Integer) req.get("pageNo") - 1;
			}
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			StringBuffer hql = new StringBuffer(
					"select a.YPGG as YFGG,a.YFDW as YFDW,sum(a.YPSL) as YCSL,sum(a.LSJE) as FYJE,a.YPDJ as YPDJ,a.YFSB as YFSB,a.YPXH as YPXH,e.CDMC as CDMC,b.BRXM as BRXM, d.YPMC as YPMC ")
					.append(" from YF_ZYFYMX a, ZY_BRRY b, YK_TYPK d, YK_CDDZ e ")
					.append(" where a.JGID=:jgid and a.LYBQ=:bqsb and a.ZYH=b.ZYH").append(" and a.YPXH=d.YPXH")
					.append(" and a.YPCD=e.YPCD and a.YPSL<0 ");
			if (body.get("dateFrom") != null) {
				hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
						.append(body.get("dateFrom")).append("'");
			}
			if (body.get("dateTo") != null) {
				hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
						.append(body.get("dateTo")).append("'");
			}
			if (body.get("FYFS") != null && !body.get("FYFS").equals("")) {
				hql.append(" and a.FYLX=:FYFS ");
				parameters
						.put("FYFS", MedicineUtils.parseInt(body.get("FYFS")));
			}
			if (body.get("YF") != null && !body.get("YF").equals("")) {
				hql.append(" and a.YFSB=:YF ");
				parameters.put("YF", MedicineUtils.parseLong(body.get("YF")));
			}
			long zyh = 0;
			if (body.get("ZYH") != null) {
				zyh = Long.parseLong(body.get("ZYH") + "");
			}
			hql.append(" and a.ZYH=:ZYH ");
			parameters.put("ZYH", zyh);
			hql.append("group by a.YPGG,a.YFDW,a.YPDJ,a.YFSB,a.YPXH,e.CDMC,b.BRXM,d.YPMC ");
			int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病人退药明细失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null || "".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null || "".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null || "".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
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
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
	 * @description 数据转换成String,去空
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public String parseString(Object o) {
		if (o == null) {
			return "";
		}
		return o + "";
	}
}
