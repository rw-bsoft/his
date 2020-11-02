package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class StorehouseGDCFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String ypxh = request.get("ypxh") + "";
		StringBuffer hql = new StringBuffer();
		hql.append("select KCSL as KCSL, YPXH as YPXH, YPMC as YPMC, YPGG as YPGG, YPDW as YPDW, GCSL as GCSL , DCSL as DCSL from ( ");
		hql.append("select sum(a.KCSL) as KCSL ,a.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,b.GCSL as GCSL,b.DCSL as DCSL from YK_KCMX a,YK_YPXX b,YK_TYPK c where a.YPXH=b.YPXH and a.YPXH=c.YPXH and b.YKZF=0 and c.ZFPB=0 and b.YKSB=:yksb and a.JGID=b.JGID ");
		
		hql.append(" having (sum(a.KCSL)>=b.GCSL and b.GCSL is not null and b.GCSL!=0) or (sum(a.KCSL)<=b.DCSL and b.DCSL is not null and b.DCSL!=0) group by a.YPXH,c.YPMC,c.YPGG,c.YPDW,b.GCSL,b.DCSL ");
		//添加不在药库库存明细表中的药品
		hql.append(" union all ");
		hql.append("select 0 as KCSL,b.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,b.GCSL as GCSL,b.DCSL as DCSL ");
		hql.append(" from YK_YPXX b,YK_TYPK c where  b.YPXH=c.YPXH and b.YKZF=0 and c.ZFPB=0 and b.YKSB=:yksb ");
		hql.append(" and b.JGID=:JGID  and ((b.GCSL is not null and b.GCSL!=0) or ( b.DCSL is not null and b.DCSL!=0)) ");
		hql.append(" and b.ypxh not in(select ypxh from yk_kcmx where jgid=:JGID) ");
		hql.append(" group by b.YPXH,c.YPMC,c.YPGG,c.YPDW,b.GCSL,b.DCSL ");
		//end
		//以下代码做了修改
		hql.append(" )  ");
		if (!"0".equals(ypxh) && ypxh != "" && !"".equals(ypxh)) {
			hql.append(" where YPXH in(" + ypxh + ")");
		}
		//end
		
		Map<String, Object> map_par = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		BaseDAO dao = new BaseDAO(ctx);
		long yksb = parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		map_par.put("yksb", yksb);
		map_par.put("JGID", manaUnitId);
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			ret = dao.doSqlQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		records.addAll(ret);
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String username = user.getUserName();
		long yksb = parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		Map<String, Object> hqlParameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hqlParameters.put("YKSB", yksb);
		hqlParameters.put("JGID", user.getManageUnit().getId());
		hql.append("select a.YKMC as YKMC from YK_YKLB" 
				+ " a where a.YKSB=:YKSB and a.JGID=:JGID");
		String ykmc = "";
		Map<String, Object> ykmcMap;
		try {
			ykmcMap = dao.doLoad(hql.toString(), hqlParameters);
			if (ykmcMap.get("YKMC") != null) {
				ykmc = ykmcMap.get("YKMC") + "";
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		
		response.put("title", user.getManageUnitName() + ykmc + "药品高低储");
		response.put("ZBR", username);
		response.put("ZBRQ", sdf.format(new Date()));
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
}
