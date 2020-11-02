package chis.source.print.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
/**
 * 协议书打印
 * @author Administrator
 *
 */
public class SignContract  extends BSCHISPrint  implements IHandler{


	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> response, Context ctx) throws PrintException {}

	
	
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {

		// TODO Auto-generated method stub
		String SCID = (String) request.get("SCID");
		getDao(ctx);
		StringBuffer hql=new StringBuffer();
		hql.append("SELECT a.SCID  as SCID,Extract(year  from scDate) as scyear,Extract(month  from scDate)as scmonth,Extract(day  from scDate)as scday,b.personName  as personName,b.sexCode  as sexCode,b.birthday  as birthday, b.idCard  as idCard,b.workPlace  as workPlace,b.mobileNumber  as mobileNumber,b.phoneNumber  as phoneNumber, b.address  as address, a.scDate  as scDate,a.beginDate  as beginDate,a.endDate  as endDate, a.stopDate  as stopDate,a.stopReason  as stopReason,a.signFlag  as signFlag,a.favoreeEmpiId  as favoreeEmpiId,a.favoreeName  as favoreeName,a.favoreePhone  as favoreePhone,a.favoreeMobile  as favoreeMobile,a.intendedPopulation  as intendedPopulation,a.familyId  as familyId,a.ownerName  as ownerName,a.firstPartyName  as firstPartyName, a.SecondPartyEmpiId  as SecondPartyEmpiId,a.SecondPartyName  as SecondPartyName,a.createUser  as createUser,a.createUnit  as createUnit,a.year as year FROM SCM_SignContractRecord a, MPI_DemographicInfo b" +
				"  WHERE b.empiId=a.favoreeEmpiId");
		hql.append(" and SCID=:SCID");
		StringBuffer hql2=new StringBuffer();
		Map<String, Object> map_par=new HashMap<String, Object>();
		Map<String, Object> map_par2=new HashMap<String, Object>();
		map_par.put("SCID", SCID);
		try {
			Map<String, Object>map_rk01=dao.doLoad(hql.toString(), map_par);
			
			if(map_rk01==null){
				return;
			}
			String favoreeEmpiId=(String) map_rk01.get("favoreeEmpiId");
			hql2.append("SELECT DISTINCT a.spid as apid,a.packageName as packageName FROM   (SELECT a.taskId AS taskId,a.empiId AS empiId, a.SCID AS SCID, a.taskName AS taskName,a.taskCode AS taskCode,a.moduleAppId  AS moduleAppId,a.status AS status,a.year AS year,a.visitId AS visitId,a.planId  AS planId,a.sort AS sort,a.SPID  AS SPID,a.packageName AS packageName,b.createUser AS createUser,b.scDate AS scDate,b.createUnit AS createUnit FROM SCM_ServiceContractPlanTask a,SCM_SignContractRecord b WHERE b.SCID=a.SCID AND a.empiId='"+favoreeEmpiId+"' AND a.SCID  ='"+SCID+"')a");
			//System.out.println(hql2.toString());
			List<Map<String, Object>>list_rk02=dao.doSqlQuery(hql2.toString(),map_par2);
			
			response.put("createUnit", map_rk01.get("createUnit"));
			response.put("createUser", map_rk01.get("createUser"));
			response.put("SecondPartyName", map_rk01.get("SecondPartyName"));
			response.put("phoneNumber", map_rk01.get("phoneNumber"));
			
			response.put("address", map_rk01.get("address"));
			
			response.put("idCard", map_rk01.get("idCard"));
			response.put("year", map_rk01.get("year"));
			response.put("scyear", map_rk01.get("scyear"));
			response.put("scmonth", map_rk01.get("scmonth"));
			response.put("scday", map_rk01.get("scday"));
			
			
			for (Object value : list_rk02) {
					String apid=(String) ((HashMap<String,Object>) value).get("APID");
					if("0000000000000002".equals(apid)){
						response.put("SPID_ER", "√");
					}else if("0000000000000003".equals(apid)){
						response.put("SPID_YF", "√");
					}else if("0000000000000004".equals(apid)){
						response.put("SPID_LNR", "√");
					}else if("0000000000000005".equals(apid)){
						response.put("SPID_GXY", "√");
					}else if("0000000000000007".equals(apid)){
						response.put("SPID_JSB", "√");
					}else{
						
					}
			   }
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,"报表数据查询失败："+e.getMessage());
		}
	}



}
