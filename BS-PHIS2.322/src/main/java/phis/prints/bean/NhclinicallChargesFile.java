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

public class NhclinicallChargesFile implements IHandler {
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		//String sql="SELECT SUM(AA.RC) AS RC,SUM(AA.bcje) AS BCJE,SUM(AA.yljz) AS YLJZ,"+
		//	" SUM(AA.zje) AS ZJE,SUM(AA.kbcje) AS KBCJE"+
		//	" FROM (select count(NVL(B.SUM08,0)) rc,"+
		//	" sum(NVL(B.SUM01,0)) bcje,sum(NVL(B.SUM11,0)) yljz,sum(NVL(B.SUM31,0)) zje ,"+
		//	" sum(NVL(B.sum04,0)) kbcje " +
		//	" FROM MS_MZXX A,NH_BSOFT_JSJL B"+
		//	" WHERE B.MZXH = A.MZXH AND A.JGID = :JGID AND B.JSRQ >= to_date(:beginDate,'yyyy-mm-dd HH24:mi:ss') AND"+
		//	" B.JSRQ <= to_date(:endDate,'yyyy-mm-dd  HH24:mi:ss')"+
		//	" UNION ALL"+
		//	" select -count(NVL(B.SUM08,0)) rc,-sum(NVL(B.SUM01,0)) bcje,-sum(NVL(B.SUM11,0)) yljz,"+
		//	" -sum(NVL(B.SUM31,0)) zje ,-sum(NVL(B.sum04,0)) kbcje FROM MS_ZFFP A,NH_BSOFT_JSJL B"+
		//	" WHERE B.MZXH = A.MZXH AND A.JGID = :JGID AND A.ZFRQ >= to_date(:beginDate,'yyyy-mm-dd HH24:mi:ss') AND"+
		//    " A.ZFRQ <= to_date(:endDate,'yyyy-mm-dd HH24:mi:ss')) AA ";
		                                  // 2019-01-10 wangjl原新农合报表更改为医保报表
//		String sql=" SELECT SUM(AA.ZJE) AS ZJE,SUM(AA.RC) AS  RC,SUM(AA.BCTCZFJE) AS  BCTCZFJE,SUM(AA.BCDBJZZF) AS  BCDBJZZF,"+
//                         " SUM(AA.BCDBBXZF) AS  BCDBBXZF,SUM(AA.BCMZBZZF) AS  BCMZBZZF,SUM(AA.BCZHZFZE) AS  BCZHZFZE,SUM(AA.BCXZZFZE) AS  BCXZZFZE,"+
//                         " SUM(AA.YBFWNFY) AS  YBFWNFY,SUM(AA.YFHJ) AS YFHJ,SUM(AA.ZLXMFHJ) AS ZLXMFHJ,SUM(AA.BBZF) AS BBZF " +
//                         " FROM(select SUM(NVL(B.BCYLFZE,0)) ZJE,COUNT(1) AS RC,SUM(NVL(B.BCTCZFJE,0)) AS BCTCZFJE,SUM(NVL(B.BCDBJZZF,0))AS BCDBJZZF,"+
//                         " SUM(NVL(B.BCDBBXZF,0)) AS BCDBBXZF, SUM(NVL(B.BCMZBZZF,0)) AS BCMZBZZF,SUM(NVL(B.BCZHZFZE,0)) AS BCZHZFZE,SUM(NVL(B.BCXZZFZE,0)) AS BCXZZFZE,"+
//                         " SUM(NVL(B.YBFWNFY,0)) AS YBFWNFY,SUM(NVL(B.YFHJ,0)) AS YFHJ, SUM(NVL(B.ZLXMFHJ,0)) AS ZLXMFHJ,SUM(NVL(B.BBZF,0)) AS BBZF"+
//                         " from MS_MZXX A, NJJB_JSXX B WHERE A.JGID = B.JGID AND A.FPHM = B.FPHM AND A.MZXH = B.MZXH AND TO_CHAR(A.SFRQ, 'YYYY-MM-DD') = TO_CHAR(B.JSSJ, 'YYYY-MM-DD')"+
//                         " AND B.JSSJ >= to_date(:beginDate, 'yyyy-mm-dd HH24:mi:ss') AND B.JSSJ <= to_date(:endDate, 'yyyy-mm-dd  HH24:mi:ss') AND A.JGID = :JGID"+
//                         " UNION ALL"+
//                         " select -SUM(NVL(B.BCYLFZE,0)) ZJE, -COUNT(1) AS RC,-SUM(NVL(B.BCTCZFJE,0)) AS BCTCZFJE, -SUM(NVL(B.BCDBJZZF,0))AS BCDBJZZF,"+
//                         " -SUM(NVL(B.BCDBBXZF,0)) AS BCDBBXZF, -SUM(NVL(B.BCMZBZZF,0)) AS BCMZBZZF, -SUM(NVL(B.BCZHZFZE,0)) AS BCZHZFZE,-SUM(NVL(B.BCXZZFZE,0)) AS BCXZZFZE,"+
//                         " -SUM(NVL(B.YBFWNFY,0)) AS YBFWNFY, -SUM(NVL(B.YFHJ,0)) AS YFHJ, -SUM(NVL(B.ZLXMFHJ,0)) AS ZLXMFHJ, -SUM(NVL(B.BBZF,0)) AS BBZF"+
//                         " from MS_ZFFP A, NJJB_JSXX B WHERE A.JGID = B.JGID AND A.FPHM = B.FPHM AND A.MZXH = B.MZXH AND TO_CHAR(A.ZFRQ, 'YYYY-MM-DD') = TO_CHAR(B.JSSJ, 'YYYY-MM-DD')"+
//                         " AND B.JSSJ >= to_date(:beginDate, 'yyyy-mm-dd HH24:mi:ss') AND B.JSSJ <= to_date(:endDate, 'yyyy-mm-dd  HH24:mi:ss')  AND A.JGID = :JGID) AA";
		 String sql=" SELECT SUM(AA.ZJE) AS ZJE,SUM(AA.RC) AS  RC,SUM(AA.BCTCZFJE) AS  BCTCZFJE,SUM(AA.BCDBJZZF) AS  BCDBJZZF,"+
	                " SUM(AA.BCDBBXZF) AS  BCDBBXZF,SUM(AA.BCMZBZZF) AS  BCMZBZZF,SUM(AA.BCZHZFZE) AS  BCZHZFZE,SUM(AA.BCXZZFZE) AS  BCXZZFZE,"+
	                " SUM(AA.YBFWNFY) AS  YBFWNFY,SUM(AA.YFHJ) AS YFHJ,SUM(AA.ZLXMFHJ) AS ZLXMFHJ,SUM(AA.BBZF) AS BBZF " +
	                " FROM(select SUM(NVL(B.BCYLFZE,0)) ZJE,COUNT(1) AS RC,SUM(NVL(B.BCTCZFJE,0)) AS BCTCZFJE,SUM(NVL(B.BCDBJZZF,0))AS BCDBJZZF,"+
	                " SUM(NVL(B.BCDBBXZF,0)) AS BCDBBXZF, SUM(NVL(B.BCMZBZZF,0)) AS BCMZBZZF,SUM(NVL(B.BCZHZFZE,0)) AS BCZHZFZE,SUM(NVL(B.BCXZZFZE,0)) AS BCXZZFZE,"+
	                " SUM(NVL(B.YBFWNFY,0)) AS YBFWNFY,SUM(NVL(B.YFHJ,0)) AS YFHJ, SUM(NVL(B.ZLXMFHJ,0)) AS ZLXMFHJ,SUM(NVL(B.BBZF,0)) AS BBZF"+
	                " from MS_MZXX A, NJJB_JSXX B,NJJB_KXX C,MS_BRDA D WHERE A.BRID=D.BRID  AND D.SHBZKH=C.SHBZKH AND  A.JGID = B.JGID AND A.FPHM = B.FPHM AND A.MZXH = B.MZXH AND TO_CHAR(A.SFRQ, 'YYYY-MM-DD') = TO_CHAR(B.JSSJ, 'YYYY-MM-DD')"+
	                " AND C.YLRYLB<>'51' AND A.ZFPB<>'1'  AND B.JSSJ >= to_date(:beginDate, 'yyyy-mm-dd HH24:mi:ss') AND B.JSSJ <= to_date(:endDate, 'yyyy-mm-dd  HH24:mi:ss') AND A.JGID = :JGID"+
		 " UNION ALL"+
         " SELECT SUM(NVL(E.BCYLFZE, 0)) ZJE,0 AS RC,SUM(NVL(E.BCTCZFJE, 0)) AS BCTCZFJE,0 AS BCDBJZZF, 0 AS BCDBBXZF,0 AS BCMZBZZF,SUM(E.BCZHZFZE) AS BCZHZFZE,SUM(E.BCXZZFZE-F.YZJM) AS BCXZZFZE,SUM(NVL(E.YBFWNFY, 0)) AS YBFWNFY,0 AS YFHJ,SUM(NVL(E.ZLXMFHJ, 0)) AS ZLXMFHJ,0 AS BBZF "+
         " from NJJB_JSXX E,MS_GHMX F,NJJB_KXX G,MS_BRDA H WHERE H.SHBZKH=G.SHBZKH AND F.BRID=H.BRID AND H.SFZH=G.SFZH AND E.GHXH=F.SBXH   and F.jgid=:JGID AND G.YLRYLB<>'51'  AND e.JSSJ >= to_date(:beginDate, 'yyyy-mm-dd HH24:mi:ss') AND e.JSSJ <= to_date(:endDate, 'yyyy-mm-dd  HH24:mi:ss')  and F.thbz='0')AA";
		 Map<String, Object> p=new HashMap<String, Object>();
		p.put("beginDate",request.get("beginDate").toString()+" 00:00:00");
		p.put("endDate",request.get("endDate").toString()+" 23:59:59");
		p.put("JGID",user.getManageUnitId());
		try {
			List<Map<String, Object>> re=dao.doSqlQuery(sql, p);
			for(int i=0;i<re.size();i++){
				Map<String, Object> temp=re.get(i);
				if(temp.get("ZJE")==null ||"0".equals(temp.get("ZJE")+"")){
					temp.put("ZJE", "1");
				}
				if(temp.get("BCTCZFJE")==null){
					temp.put("BCTCZFJE", "0");
				}
				if(temp.get("BCDBJZZF")==null){
					temp.put("BCDBJZZF", "0");
				}
				if(temp.get("BCDBBXZF")==null){
					temp.put("BCDBBXZF", "0");
				}
				if(temp.get("BCMZBZZF")==null){
					temp.put("BCMZBZZF", "0");
				}
				if(temp.get("BCZHZFZE")==null){
					temp.put("BCZHZFZE", "0");
				}
				if(temp.get("BCXZZFZE")==null){
					temp.put("BCXZZFZE", "0");
				}
				if(temp.get("YBFWNFY")==null){
					temp.put("YBFWNFY", "0");
				}
				if(temp.get("YFHJ")==null){
					temp.put("YFHJ", "0");
				}
				if(temp.get("ZLXMFHJ")==null){
					temp.put("ZLXMFHJ", "0");
				}
				if(temp.get("BBZF")==null){
					temp.put("BBZF", "0");
				}	
				//temp.put("BCB", BSPHISUtil.getDouble(Double.parseDouble(temp.get("BCJE")+"")/Double.parseDouble(temp.get("ZJE")+"")*100,2)+"%");
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
		response.put("ZBR", user.getUserName());
		response.put("JGMC",user.getManageUnitName());
		response.put("beginDate",request.get("beginDate").toString());
		response.put("endDate",request.get("endDate").toString());
	}
}
