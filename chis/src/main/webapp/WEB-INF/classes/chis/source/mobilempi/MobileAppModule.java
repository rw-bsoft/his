package chis.source.mobilempi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.config.ConfigTools;

import ctd.service.core.ServiceException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;

public class MobileAppModule implements BSCHISEntryNames{
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MobileAppModule.class);
	public MobileAppModule(BaseDAO dao) {
		this.dao = dao;
	}
	public void getAlreadyContractList(Map<String, Object> jsonReq,Map<String, Object> jsonRes) 
			throws ServiceException{
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		String content=jsonReq.get("content")==null?"":jsonReq.get("content")+"";
		String doctorId=jsonReq.get("doctorId")==null?"":jsonReq.get("doctorId")+"";
		String and="";
		if(content.length() > 0 && !content.equals("null") ){
			and=" and (c.personName like '"+content+"%' or c.idCard like '"+content+"%')";
		}
		String sql="select d.phrId as phrId,c.personName as personName,d.regioncode as regionCode," +
				" d.regionCode_text as regionCode_text,c.sexCode as sexCode,c.birthday as birthday," +
				" c.idCard as idCard,a.fc_phone as fc_phone,floor(months_between(sysdate,c.birthday)/12) as age," +
				" b.fS_EmpiId as fS_EmpiId,a.fc_Begin as fc_Begin,a.fc_end as fc_End," +
				" a.fc_stop_date as fC_Stop_Date,a.fC_Stop_Reason as fC_Stop_Reason,a.fC_Sign_Flag as fC_Sign_Flag," +
				" a.fC_Phone as fC_Phone,a.fC_Mobile as fC_Mobile,a.fC_Party as fC_Party,a.fC_Party2 as fC_Party2," +
				" b.fS_Kind as fS_Kind,b.fS_PersonGroup as fS_PersonGroup" +
				" from EHR_FamilyContractBase a ,EHR_FamilyContractService b," +
				" mpi_demographicinfo c,EHR_HealthRecord d "+
				" where  a.fc_id=b.fc_id and b.fs_empiid=c.empiid and a.fc_sign_flag='1' and a.fc_begin <=:nowdate " +
				" and c.empiid=d.empiid and a.fc_end >=:nowdate "+and+" and a.fc_createuser=:userId ";
		
		Map<String, Object> pa=new HashMap<String, Object>();
		pa.put("userId", doctorId);
		pa.put("nowdate", new Date());
		Long count=0l;
		try {
			rsList=dao.doSqlQuery(sql, pa);
			count=Long.parseLong((dao.doSqlLoad("select count(1) as count from("+sql+")", pa).get("COUNT")+""));
		} catch (PersistentDataOperationException e) {
			jsonRes.put("code", "400");
			jsonRes.put("msg", "sql语句执行失败！");
			throw new ServiceException(e.getMessage());
		}
		jsonRes.put("code", "200");
		jsonRes.put("msg", "成功");
		jsonRes.put("data", rsList);
		jsonRes.put("totalCount", count);
	} 
}
