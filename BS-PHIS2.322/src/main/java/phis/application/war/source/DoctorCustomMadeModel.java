/**
 * @(#)DoctorCustomMadeModel.java Created on 2013-5-3 下午1:28:21
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class DoctorCustomMadeModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(DoctorCustomMadeModel.class);

	public DoctorCustomMadeModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> savePersonalSetInfo(String op, String schema,
			Map<String, Object> saveData) throws ModelDataOperationException {
		Map<String, Object> body=null;
		try {
			body = dao.doSave(op, schema, saveData, true);
		} catch (ValidateException e) {
			logger.error("savePersonalSetInfo fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存个人参数设置失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("savePersonalSetInfo fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存个人参数设置失败！");
		}
		return body;
	}

	public Map<String, Object> loadPersonalSetInfo(String uid, String schema) throws ModelDataOperationException {
		List<Object> cnd=CNDHelper.createSimpleCnd("eq", "YHBH", "s", uid);
		Map<String, Object> body=null;
		try {
			body = dao.doLoad(cnd, schema);
			SchemaUtil.setDictionaryMassageForForm(body, schema);
		} catch (PersistentDataOperationException e) {
			logger.error("loadPersonalSetInfo fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载个人参数设置失败！");
		}
		return body;
	}

	private Map<String, Object> changeCheckboxValue(Map<String, Object> body, boolean b) {
		Map<String, Object> map=new HashMap<String, Object>();
		if(body==null){
			return body;
		}
		map.putAll(body);
		String[] fields={"YSSR","TZYS","YZXS","SSTX"};
		if(b==false){
			for (int i = 0; i < fields.length; i++) {
				String f=fields[i];
				if((Boolean)body.get(f)==true){
					map.put(f, "1");
				}else{
					map.put(f, "0");
				}
			}
		}else{
			for (int i = 0; i < fields.length; i++) {
				String f=fields[i];
				if(body.get(f).equals("1")){
					map.put(f, true);
				}else{
					map.put(f, false);
				}
			}
		}
		return map;
	}
}
