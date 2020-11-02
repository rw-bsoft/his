package phis.application.ivc.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.util.context.Context;
import edu.emory.mathcs.backport.java.util.Arrays;

public class ChangeDoctorOrDepartmentModel {
	protected BaseDAO dao;
	
	public ChangeDoctorOrDepartmentModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	//查询医生代码
	public String doFindYsdm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<String> res = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("MZKS", body.get("ksId"));
		String hql = "select c.PERSONID as YGDM from ms_ghks a,gy_qxkz b,SYS_Personnel c where a.mzks=:MZKS and a.ksdm=b.ksdm and c.PERSONID=b.ygdm";
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(hql,params);
			for(int i=0;i<list.size();i++){
				res.add((String) list.get(i).get("YGDM"));				
			}			
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("员工代码查询出错！", e);
		}
		return ArrtoString(res);
	}
	//查询科室代码
	public String doFindKsdm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<String> res = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("YGDM", body.get("ysId"));
		//String s = (String) body.get("ysId");
		String hql = "select a.mzks as MZKS,a.ksdm as KSDM from ms_ghks a,gy_qxkz b,SYS_Personnel c where c.personid=b.ygdm and a.ksdm=b.ksdm and c.personid=:YGDM";
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(hql,params);
			for(int i=0;i<list.size();i++){				
				res.add(list.get(i).get("MZKS").toString());				
			}			
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("科室代码查询出错！", e);
		}
		return Arrays.toString(res.toArray());
	}
	
	public String ArrtoString(List<String> arr){
		StringBuffer res = new StringBuffer("[");
		for(int i = 0 ; i < arr.size() ; i ++){
			if(i==0){
				res.append("'");
				res.append(arr.get(i));
				res.append("'");
			}else{
				res.append(",");
				res.append("'");
				res.append(arr.get(i));
				res.append("'");
			}
		}
		res.append("]");
		return res.toString();
	}
}
