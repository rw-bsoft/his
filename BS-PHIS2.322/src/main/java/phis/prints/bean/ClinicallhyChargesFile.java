package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ClinicallhyChargesFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		String sql="SELECT YLXH as YLXH, FYMC as FYMC,SUM (YLSL) AS YLSL ,SUM(HJJE) AS HJJE FROM (" +
				" SELECT A.YLXH AS YLXH,C.FYMC AS FYMC ,YLSL AS YLSL ,HJJE AS HJJE FROM MS_YJ02 A ," +
				" MS_YJ01 B,GY_YLSF C ,MS_MZXX D" +
				" WHERE A.YJXH=B.YJXH AND A.YLXH=C.FYXH AND C.FYGB=9 AND " +
				" D.SFRQ >to_date(:beginDate,'yyyy-mm-dd HH24:mi:ss') AND  " +
				" D.SFRQ <to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') AND" +
				" B.FPHM IS NOT NULL AND B.MZXH=D.MZXH and b.JGID=:JGID";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("beginDate",request.get("beginDate").toString()+" 00:00:00");
		p.put("endDate",request.get("endDate").toString()+" 23:59:59");
		p.put("JGID",user.getManageUnitId());
		String xmlx = request.get("xmlx").toString();
		if(xmlx!=null && !"".equals(xmlx) && !"0".equals(xmlx)){
			p.put("XMLX",xmlx);
			sql+=" and C.XMLX=:XMLX";
		}
		sql+=" union all " +
				" SELECT A.YLXH AS YLXH,C.FYMC AS FYMC ,-YLSL AS  YLSL,-HJJE AS HJJE  " +
				" FROM MS_YJ02 A ,MS_YJ01 B,GY_YLSF C ,MS_ZFFP D" +
				" WHERE A.YJXH=B.YJXH AND A.YLXH=C.FYXH AND C.FYGB=9 AND " +
				" D.ZFRQ >to_date(:beginDate,'yyyy-mm-dd HH24:mi:ss') AND" +
				" D.ZFRQ <to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') AND" +
				" B.FPHM IS NOT NULL AND B.MZXH=D.MZXH and b.JGID=:JGID";
		if(xmlx!=null && !"".equals(xmlx) && !"0".equals(xmlx)){
			sql+=" and C.XMLX=:XMLX";
		}
		sql+=" ) GROUP BY YLXH,FYMC";	
		try {
			List<Map<String, Object>> re=dao.doSqlQuery(sql, p);
			long slcount=0;
			double jecount=0.0;
			for(int i=0;i<re.size();i++){
				Map<String, Object> temp=re.get(i);
				slcount+=Long.parseLong(re.get(i).get("YLSL")+"");
				jecount+=Double.parseDouble(re.get(i).get("HJJE")+"");
				records.add(temp);
			}
			Map<String, Object> zh=new HashMap<String, Object>();
			zh.put("YLXH", "");
			zh.put("FYMC", "                                                       总计:");
			zh.put("YLSL", slcount);
			zh.put("HJJE", BSPHISUtil.getDouble(jecount,2));
			records.add(zh);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("JGMC",user.getManageUnitName());
		response.put("ZBR",user.getUserName());
		response.put("beginDate",request.get("beginDate").toString());
		response.put("endDate",request.get("endDate").toString());
	}
}
