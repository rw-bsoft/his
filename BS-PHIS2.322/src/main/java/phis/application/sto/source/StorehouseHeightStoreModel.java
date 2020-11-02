package phis.application.sto.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;

public class StorehouseHeightStoreModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseHeightStoreModel.class);
	protected SchemaItem item_yk_kc = null;

	// protected SchemaItem item_yk_ls = null;
	public StorehouseHeightStoreModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-7-27
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
	 * @createDate 2012-8-10
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
	 * @createDate 2012-8-10
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
	 * @createDate 2012-8-20
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
		if (number > 8) {
			return 0;
		}
		double x = Math.pow(10, number);
		return Math.round(data * x) / x;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 药库高低储提示查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void queryYKGDC(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		String likepar = null;
		if (req.get("cnd") != null) {
			String cnd = req.get("cnd") + "";
			String[] cndstr = cnd.split(",");
			if (cndstr.length == 5) {
				likepar = cndstr[4].substring(1, cndstr[4].indexOf("]"));
			} else {
				likepar = cndstr[5].substring(1, cndstr[5].indexOf("]")).toUpperCase();
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
		hql.append("select sum(a.KCSL) as KCSL,a.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,b.GCSL as GCSL,b.DCSL as DCSL from YK_KCMX a,YK_YPXX b,YK_TYPK c where a.YPXH=b.YPXH and a.YPXH=c.YPXH and b.YKZF=0 and c.ZFPB=0 and b.YKSB=:yksb and a.JGID=b.JGID and b.JGID=:JGID ");
		if (likepar != null) {
			hql.append(" and c.PYDM like '%" + likepar + "%'");
		}
		hql.append(" having (sum(a.KCSL)>=b.GCSL and b.GCSL is not null and b.GCSL!=0) or (sum(a.KCSL)<=b.DCSL and b.DCSL is not null and b.DCSL!=0) group by a.YPXH,c.YPMC,c.YPGG,c.YPDW,b.GCSL,b.DCSL");
		// 添加不在药库库存明细表中的药品
		hql.append(" union all ");
		hql.append("select 0 as KCSL,b.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,b.GCSL as GCSL,b.DCSL as DCSL ");
		hql.append(" from YK_YPXX b,YK_TYPK c where  b.YPXH=c.YPXH and b.YKZF=0 and c.ZFPB=0 and b.YKSB=:yksb ");
		hql.append(" and b.JGID=:JGID  ");
		if (likepar != null) {
			hql.append(" and c.PYDM like '%" + likepar + "%'");
		}
		hql.append(" and ((b.GCSL is not null and b.GCSL!=0) or ( b.DCSL is not null and b.DCSL!=0)) ");
		hql.append(" and b.ypxh not in(select ypxh from yk_kcmx where jgid=:JGID) ");
		hql.append(" group by b.YPXH,c.YPMC,c.YPGG,c.YPDW,b.GCSL,b.DCSL ");
		// end
		Map<String, Object> map_par = new HashMap<String, Object>();
		Map<String, Object> map_parsize = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		long yksb = parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		map_par.put("yksb", yksb);
		map_parsize.put("yksb", yksb);
		map_parsize.put("JGID", manaUnitId);
		map_par.put("first", pageNo * pageSize);
		map_par.put("max", pageSize);
		map_par.put("JGID", manaUnitId);
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> retsize = new ArrayList<Map<String, Object>>();
		try {
			ret = dao.doSqlQuery(hql.toString(), map_par);
			retsize = dao.doSqlQuery(hql.toString(), map_parsize);
			res.put("totalCount", Long.parseLong(retsize.size() + ""));
			res.put("body", ret);
		} catch (PersistentDataOperationException e) {
			logger.error("药库高低储提示查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "药库高低储提示查询失败");
		}
	}

}
