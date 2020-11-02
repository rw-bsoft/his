package phis.application.ivc.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.service.core.Service;
import ctd.util.context.Context;

public class ClinicTreatmentProgramsNameModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicTreatmentProgramsNameModel.class);
	public ClinicTreatmentProgramsNameModel(BaseDAO dao) {
		this.dao = dao;
	}
	public void clinicTreatmentProgramsNameVerification(Map<String, Object> body,
			Map<String, Object> res,String op,BaseDAO dao, Context ctx) throws ModelDataOperationException{
		       if("create".equals(op)){
			    		  String sql="ZLMC=:ZLMC and YGDM=:YGDM and JGID=:JGID and SSLB=:SSLB";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   parameters.put("ZLMC", body.get("ZLMC"));
						   parameters.put("YGDM", body.get("YGDM"));
						   parameters.put("JGID", body.get("JGID"));
						   parameters.put("SSLB", Integer.parseInt(body.get("SSLB").toString()));
						   try {
							Long l = dao.doCount("GY_ZLFA", sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 613);
								res.put(Service.RES_MESSAGE, "诊疗名称已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "诊疗名称校验失败");
						}
		       }else{
				    		  String sql="ZLMC=:ZLMC and YGDM=:YGDM and JGID=:JGID and SSLB=:SSLB and ZLXH<>:ZLXH";
							   Map<String, Object> parameters = new HashMap<String, Object>();
							   parameters.put("ZLMC", body.get("ZLMC"));
							   parameters.put("YGDM", body.get("YGDM"));
							   parameters.put("JGID", body.get("JGID"));
							   parameters.put("SSLB",Integer.parseInt(body.get("SSLB").toString()));
							   parameters.put("ZLXH", body.get("ZLXH"));
							   try {
								Long l = dao.doCount("GY_ZLFA", sql, parameters);
								if(l>0){
									res.put(Service.RES_CODE, 613);
									res.put(Service.RES_MESSAGE, "诊疗名称已经存在");
								}
							} catch (PersistentDataOperationException e) {
								logger.error("Save failed.", e);
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "诊疗名称校验失败");
							}
			   }
	}
}
