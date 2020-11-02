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

public class NhHosChargedateilsFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		String sql=" select A.ZYH as ZYH,A.ZYHM as ZYHM,B.SUM08 as SUM08,B.SUM12 as SUM12,B.ICKH as ICKH,"
          +" A.BRXM as BRXM,A.HKDZ as HKDZ, D.FYHJ as ZJJE,B.SUM02 as BCJE,c.jbmc as JBMC,"
          +" b.sum06 as SUM06"
          +" FROM ZY_BRRY A,NH_BSOFT_JSJL B, EMR_ZYZDJL C ,ZY_ZYJS D"
          +" WHERE A.JGID =:JGID AND D.ZYH = A.ZYH AND B.ZYH = D.ZYH AND"
          +" B.JSCS = D.JSCS AND D.JSRQ >= to_date(:beginDate,'yyyy-mm-dd  HH24:mi:ss') "
          +" AND D.JSRQ <=to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') and"
          +" A.ZYH=C.JZXH and c.ZDLB='51'"
          +" UNION ALL"
          +" select A.ZYH as ZYH,A.ZYHM as ZYHM,-B.SUM08 as SUM08,- B.SUM12 as SUM12,B.ICKH as ICKH,"
          +" A.BRXM as BRXM,A.HKDZ as HKDZ,-b.zjje as ZJJE,-B.SUM02 as BCJE, c.jbmc as JBMC,"
          +" b.sum06 as SUM06 "
          +" FROM ZY_BRRY A,NH_BSOFT_JSJL B,EMR_ZYZDJL C , ZY_JSZF D"
          +" WHERE A.JGID =:JGID AND D.ZYH = A.ZYH AND B.ZYH = D.ZYH AND B.JSCS = D.JSCS AND "
          +" A.ZYH=C.JZXH and c.ZDLB='51' and D.ZFRQ >=  to_date(:beginDate,'yyyy-mm-dd  HH24:mi:ss') "
          +" AND D.ZFRQ <=to_date(:endDate,'yyyy-mm-dd HH24:mi:ss') ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("beginDate",request.get("beginDate").toString()+" 00:00:00");
		p.put("endDate",request.get("endDate").toString()+" 23:59:59");
		p.put("JGID",user.getManageUnitId());
		try {
			List<Map<String, Object>> re=dao.doSqlQuery(sql, p);
			for(int i=0;i<re.size();i++){
				Map<String, Object> temp=re.get(i);
				temp.put("XH",i+1);
				records.add(temp);
			}
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
		response.put("beginDate",request.get("beginDate").toString());
		response.put("endDate",request.get("endDate").toString());
	}
}
