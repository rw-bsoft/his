package phis.application.cfg.source;

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

public class ConfigDiseaseNumberModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigDiseaseNumberModel.class);
	public ConfigDiseaseNumberModel(BaseDAO dao) {
		this.dao = dao;
	}
	public void iCD10NumberAndJBMCVerification(Map<String, Object> body,
			Map<String, Object> res,String schemaDetailsList,String op,BaseDAO dao, Context ctx) throws ModelDataOperationException{
		       if("create".equals(op)){
		    	   if(body.get("ICD10")!=null){
		    		  String sql="ICD10=:ICD10";
					   Map<String, Object> parameters = new HashMap<String, Object>();
					   parameters.put("ICD10", body.get("ICD10"));
					   try {
						Long l = dao.doCount(schemaDetailsList, sql, parameters);
						if(l>0){
							res.put(Service.RES_CODE, 612);
							res.put(Service.RES_MESSAGE, "ICD10编码已经存在");
						}
					} catch (PersistentDataOperationException e) {
						logger.error("Save failed.", e);
						throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "ICD10编码校验失败");
					}
		    	   }
		    	   if(body.get("JBMC")!=null){
			    		  String sql="JBMC=:JBMC";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   parameters.put("JBMC", body.get("JBMC"));
						   try {
							Long l = dao.doCount(schemaDetailsList, sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 613);
								res.put(Service.RES_MESSAGE, "疾病名称已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "疾病名称校验失败");
						}
			    	   }
		       }else{
		    	   if(body.get("ICD10")!=null){
			    		  String sql="ICD10=:ICD10 and JBXH<>:JBXH";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   parameters.put("ICD10", body.get("ICD10"));
						   parameters.put("JBXH", body.get("JBXH"));
						   try {
							Long l = dao.doCount(schemaDetailsList, sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 612);
								res.put(Service.RES_MESSAGE, "ICD10编码已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "ICD10编码校验失败");
						}
			    	   }
			    	   if(body.get("JBMC")!=null){
				    		  String sql="JBMC=:JBMC and JBXH<>:JBXH";
							   Map<String, Object> parameters = new HashMap<String, Object>();
							   parameters.put("JBMC", body.get("JBMC"));
							   parameters.put("JBXH", body.get("JBXH"));
							   try {
								Long l = dao.doCount(schemaDetailsList, sql, parameters);
								if(l>0){
									res.put(Service.RES_CODE, 613);
									res.put(Service.RES_MESSAGE, "疾病名称已经存在");
								}
							} catch (PersistentDataOperationException e) {
								logger.error("Save failed.", e);
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "疾病名称校验失败");
							}
				    	   }
			   }
	}
	//中医诊断保存前判断
	public void ZYJBVerification(Map<String, Object> body,
			Map<String, Object> res,String schemaDetailsList,String op,BaseDAO dao, Context ctx) throws ModelDataOperationException{
		       if("create".equals(op)){ // 疾病代码暂时可以重复
		    	   /*if(body.get("JBDM")!=null){
		    		  String sql="JBDM=:JBDM";
					   Map<String, Object> parameters = new HashMap<String, Object>();
					   parameters.put("JBDM", body.get("JBDM"));
					   try {
						Long l = dao.doCount(schemaDetailsList, sql, parameters);
						if(l>0){
							res.put(Service.RES_CODE, 612);
							res.put(Service.RES_MESSAGE, "疾病编码已经存在");
						}
					} catch (PersistentDataOperationException e) {
						logger.error("Save failed.", e);
						throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "疾病编码校验失败");
					}
		    	   }*/
		    	   if(body.get("JBMC")!=null){
			    		  String sql="JBMC=:JBMC";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   parameters.put("JBMC", body.get("JBMC"));
						   try {
							Long l = dao.doCount(schemaDetailsList, sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 613);
								res.put(Service.RES_MESSAGE, "疾病名称已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "疾病名称校验失败");
						}
			    	   }
		       }else{
		    	  /* if(body.get("JBDM")!=null){
			    		  String sql="JBDM=:JBDM and JBBS<>:JBBS";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   parameters.put("JBDM", body.get("JBDM"));
						   parameters.put("JBBS", Long.parseLong(body.get("JBBS")+""));
						   try {
							Long l = dao.doCount(schemaDetailsList, sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 612);
								res.put(Service.RES_MESSAGE, "疾病编码已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "疾病编码校验失败");
						}
			    	   }*/
			    	   if(body.get("JBMC")!=null){
				    		  String sql="JBMC=:JBMC and JBBS<>:JBBS";
							   Map<String, Object> parameters = new HashMap<String, Object>();
							   parameters.put("JBMC", body.get("JBMC"));
							   parameters.put("JBBS", Long.parseLong(body.get("JBBS")+""));
							   try {
								Long l = dao.doCount(schemaDetailsList, sql, parameters);
								if(l>0){
									res.put(Service.RES_CODE, 613);
									res.put(Service.RES_MESSAGE, "疾病名称已经存在");
								}
							} catch (PersistentDataOperationException e) {
								logger.error("Save failed.", e);
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "疾病名称校验失败");
							}
				    	   }
			   }
	}
	
	   //中医证侯保存前判断
		public void ZYZHVerification(Map<String, Object> body,
				Map<String, Object> res,String schemaDetailsList,String op,BaseDAO dao, Context ctx) throws ModelDataOperationException{
			       if("create".equals(op)){
			    	   if(body.get("ZHDM")!=null){
			    		  String sql="ZHDM=:ZHDM";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   parameters.put("ZHDM", body.get("ZHDM"));
						   try {
							Long l = dao.doCount(schemaDetailsList, sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 612);
								res.put(Service.RES_MESSAGE, "证侯编码已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "证侯编码校验失败");
						}
			    	   }
			    	   if(body.get("ZHMC")!=null){
				    		  String sql="ZHMC=:ZHMC";
							   Map<String, Object> parameters = new HashMap<String, Object>();
							   parameters.put("ZHMC", body.get("ZHMC"));
							   try {
								Long l = dao.doCount(schemaDetailsList, sql, parameters);
								if(l>0){
									res.put(Service.RES_CODE, 613);
									res.put(Service.RES_MESSAGE, "证侯名称已经存在");
								}
							} catch (PersistentDataOperationException e) {
								logger.error("Save failed.", e);
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "证侯名称校验失败");
							}
				    	   }
			       }else{
			    	   if(body.get("ZHDM")!=null){
				    		  String sql="ZHDM=:ZHDM and ZHBS<>:ZHBS";
							   Map<String, Object> parameters = new HashMap<String, Object>();
							   parameters.put("ZHDM", body.get("ZHDM"));
							   parameters.put("ZHBS", Long.parseLong(body.get("ZHBS")+""));
							   try {
								Long l = dao.doCount(schemaDetailsList, sql, parameters);
								if(l>0){
									res.put(Service.RES_CODE, 612);
									res.put(Service.RES_MESSAGE, "证侯编码已经存在");
								}
							} catch (PersistentDataOperationException e) {
								logger.error("Save failed.", e);
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "证侯编码校验失败");
							}
				    	   }
				    	   if(body.get("ZHMC")!=null){
					    		  String sql="ZHMC=:ZHMC and ZHBS<>:ZHBS";
								   Map<String, Object> parameters = new HashMap<String, Object>();
								   parameters.put("ZHMC", body.get("ZHMC"));
								   parameters.put("ZHBS", Long.parseLong(body.get("ZHBS")+""));
								   try {
									Long l = dao.doCount(schemaDetailsList, sql, parameters);
									if(l>0){
										res.put(Service.RES_CODE, 613);
										res.put(Service.RES_MESSAGE, "证侯名称已经存在");
									}
								} catch (PersistentDataOperationException e) {
									logger.error("Save failed.", e);
									throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "证侯名称校验失败");
								}
					    	   }
				   }
		}
}
