/**
 * @(#)MedicalRecordsQueryMedol.java Created on 2013-5-16 下午1:42:10
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class MedicalRecordsQueryModel {
	protected Logger logger = LoggerFactory
			.getLogger(MedicalRecordsQueryModel.class);
	protected BaseDAO dao;

	public MedicalRecordsQueryModel(BaseDAO dao) {
		this.dao = dao;
	}

	public String getRecordHtmlData(String pkey, String jZXH, String bLLX)
			throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "BLBH", "s", pkey);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "WDLX", "s", "1");
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> records = null;
		String resultString = null;
		try {
			records = dao.doQuery(cnd, "BLBH", BSPHISEntryNames.EMR_BL03);
			if ("1".equals(bLLX)) {

			}
			Map<String, Object> o = new HashMap<String, Object>();
			if (records != null && records.size() > 0) {
				Map<String, Object> r = records.get(0);
				Blob blob = (Blob) r.get("WDNR");
				InputStream is = blob.getBinaryStream();
				Reader utf8_reader = new InputStreamReader(is);
				StringBuffer utf8Text = new StringBuffer();
				int charValue = 0;
				while ((charValue = utf8_reader.read()) != -1) {
					utf8Text.append((char) charValue);
				}
				resultString = utf8Text.toString();
			}
		} catch (SQLException e) {
			logger.error("getRecordHtmlData fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询文档html内容失败！");
		} catch (IOException e) {
			logger.error("getRecordHtmlData fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询文档html内容失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("getRecordHtmlData fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询文档html内容失败！");
		}
		return resultString;
	}

	public Map<String, Object> listMedicalRecords(List queryCnd, String schema,
			String queryCndsType, String sortInfo, int pageSize, int pageNo,
			String type) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("ne", "DLLB", "s", "-1");
		if (queryCnd != null) {
			cnd = CNDHelper.createArrayCnd("and", queryCnd, cnd);
		}
		List<Map<String, Object>> body = null;
		Map<String, Object> map=new HashMap<String, Object>();
		try {	
			List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
			if (type != null) {
				body = dao.doList(cnd, sortInfo, schema);
				for (int i = 0; i < body.size(); i++) {
					Map<String, Object> m=body.get(i);
					String pkey=m.get("BLBH").toString();
					List<Map<String, Object>> list=getReviewRecord(pkey);
					if(list!=null&&list.size()>0){
						body.remove(m);
					}
				}
			}else{
				map=dao.doList(cnd, sortInfo, schema, pageNo, pageSize, queryCndsType);
				body=(List<Map<String, Object>>) map.get("body");
			}
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> m=body.get(i);
				String MBLB=m.get("MBLB").toString();
				if("2000001".equals(MBLB)){
					m.put("MBLB", "2000001");
					m.put("MBLB_text", "住院病案首页");
					m.put("BLLB", "2000001");
					m.put("BLLB_text", "住院病案首页");
					m.put("MBBH", "0");
					m.put("MBBH_text", "住院病案首页");
				}
				result.add(m);
			}
			if (type == null) {
				map.put("body", result);
				return map;
			}
			if (pageSize * (pageNo - 1) >= result.size()) {
				map.put("pageNo", 1);
				map.put("totalCount", result.size());
				int endIndex = pageSize;
				if (pageSize > result.size()) {
					endIndex = result.size();
				}
				result = result.subList(0, endIndex);
			} else {
				map.put("totalCount", result.size());
				int endIndex = pageNo * pageSize;
				if (pageNo * pageSize > result.size()) {
					endIndex = result.size();
				}
				result = result.subList(pageSize * (pageNo - 1), endIndex);
			}
			map.put("pageSize", pageSize);
			map.put("body", result);
		} catch (PersistentDataOperationException e) {
			logger.error("list Medical Records fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询所有病历失败！");
		}
		return map;
	}

	public List<Map<String, Object>> getReviewRecord(String pkey)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "BLBH", "s", pkey);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, "JLXH", BSPHISEntryNames.EMR_BLSY);
		} catch (PersistentDataOperationException e) {
			logger.error("get Review Record fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询所有病历失败！");
		}
		return list;
	}

	public Map<String, Object> getMedicalRecordData(String schema, String pkey)
			throws ModelDataOperationException {
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(schema, pkey);
		} catch (PersistentDataOperationException e) {
			logger.error("get Medical Record Data fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病历失败！");
		}
		return map;
	}

	public Map<String, Object> saveMedicalCondition(Map<String, Object> body,
			String schema, String op) throws ModelDataOperationException {
		Map<String, Object> result = null;
		try {
			result = dao.doSave(op, schema, body, true);
		} catch (ValidateException e) {
			logger.error("save Medical Condition fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存病历查询条件失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("save Medical Condition fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存病历查询条件失败！");
		}
		return result;
	}

	public boolean checkHasTJMC(List<Object> cnd, String schema)
			throws ModelDataOperationException {
		boolean flag = true;
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null, schema);
			if (list != null && list.size() > 0) {
				flag = false;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("check Has TJMC fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询是否存在该条件名称失败！");
		}
		return flag;
	}

	public List<Map<String, Object>> listAllConditions(List<Object> cnd,
			String schema) throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		try {
			result = dao.doList(cnd, "TJID", schema);
		} catch (PersistentDataOperationException e) {
			logger.error("list All Conditions fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询所有条件失败！");
		}
		return result;
	}

	public void removeMedicalCondition(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(Long.parseLong(pkey), "phis.application.emr.schemas.EMR_BL01_QUERYC");
		} catch (PersistentDataOperationException e) {
			logger.error("remove Medical Condition fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除查询条件失败！");
		}

	}
}
