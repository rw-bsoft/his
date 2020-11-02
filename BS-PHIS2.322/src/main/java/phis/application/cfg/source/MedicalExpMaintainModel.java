/**
 * @(#)MedicalExpMaintainModel.java Created on 2013-5-27 下午3:03:44
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.cfg.source;

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

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class MedicalExpMaintainModel {
	protected Logger logger = LoggerFactory
			.getLogger(MedicalExpMaintainModel.class);
	protected BaseDAO dao;

	public MedicalExpMaintainModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void logoutMedicalExp(String pkey, String ZXBZ)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"update from EMR_YXBDS_DY set ZXBZ='");
		if("1".equals(ZXBZ)){
			hql.append("0");
		}else{
			hql.append("1");
		}
		hql.append("' where DYBDSBH='")
				.append(pkey).append("'");
		Map<String, Object> parameters = null;
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("logout MedicalExp fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销医学表达式失败！");
		}

	}

	public Map<String, Object> listMedicalExpRecords(List queryCnd,
			String schema, String queryCndsType, String sortInfo, int pageSize,
			int pageNo) throws ModelDataOperationException {
		Map<String, Object> map = null;
		try {
			map = dao.doList(queryCnd, sortInfo, schema, pageNo, pageSize,
					queryCndsType);
			List<Map<String, Object>> body = (List<Map<String, Object>>) map
					.get("body");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> m = body.get(i);
				if (m != null && m.get("BDSNR") != null) {
					Blob blob = (Blob) m.get("BDSNR");
					InputStream is = blob.getBinaryStream();
					Reader utf8_reader = new InputStreamReader(is);
					StringBuffer utf8Text = new StringBuffer();
					int charValue = 0;
					while ((charValue = utf8_reader.read()) != -1) {
						utf8Text.append((char) charValue);
					}
					String resultString = utf8Text.toString();
					m.put("BDSNR", resultString);
				}
				result.add(m);
			}
			map.put("body", result);
		} catch (PersistentDataOperationException e) {
			logger.error("list MedicalExp Records fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医学表达式失败！");
		} catch (SQLException e) {
			logger.error("list MedicalExp Records fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医学表达式失败！");
		} catch (IOException e) {
			logger.error("list MedicalExp Records fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医学表达式失败！");
		}
		return map;
	}

	public Map<String, Object> loadMedicalExpData(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> map = null;
		try {
			map = dao.doLoad("phis.application.cfg.schemas.EMR_YXBDS_DY", pkey);
		} catch (PersistentDataOperationException e) {
			logger.error("load MedicalExp Data fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载医学表达式失败！");
		}
		return map;
	}

	public Map<String, Object> saveMedicalExpData(String schema, String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> map = null;
		String BDSNR = "";
		String DYBDSBH = "";
		try {
			if (op.equals("update")) {
				BDSNR = (String) reqBody.get("BDSNR");
				DYBDSBH = reqBody.get("DYBDSBH").toString();
			}
			reqBody.put("BDSNR", null);
			map = dao.doSave(op, schema, reqBody, true);
			if (op.equals("create")) {
				DYBDSBH = map.get("DYBDSBH").toString();
			}
			if (op.equals("update")) {
				saveMedicalExpContent(DYBDSBH, BDSNR);
			}
		} catch (ValidateException e) {
			logger.error("save MedicalExp Data fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存医学表达式失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("save MedicalExp Data fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存医学表达式失败！");
		}
		return map;
	}

	public boolean checkHasMedicalExp(String schema, String bDSMC)
			throws ModelDataOperationException {
		boolean flag = true;
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "ZXBZ", "s", "0");
		List<Object> cnd2 = CNDHelper
				.createSimpleCnd("eq", "BDSMC", "s", bDSMC);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null, schema);
			if (list != null && list.size() > 0) {
				flag = false;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("check Has MedicalExp fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查是否存在同名医学表达式失败！");
		}
		return flag;
	}

	public int saveMedicalExpContent(String pkey, String valueB)
			throws ModelDataOperationException {
		Blob blob = Hibernate.createBlob(valueB.getBytes());
		StringBuffer hql = new StringBuffer(
				"update EMR_YXBDS_DY set BDSNR=:BDSNR where DYBDSBH='").append(
				pkey).append("'");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BDSNR", blob);
		int result;
		try {
			result = dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("save MedicalExp Content fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新医学表达式内容失败！");
		}
		return result;

	}
	
	/**
	 * 分页查询医学表达式列表
	 * @param queryCnd
	 * @param schema
	 * @param queryCndsType
	 * @param sortInfo
	 * @param pageSize
	 * @param pageNo
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> listMedicalExpressRecords(List queryCnd, String queryCndsType, String sortInfo, int pageSize,
			int pageNo,BaseDAO dao) throws ModelDataOperationException {
		if(queryCnd==null){
			queryCnd=CNDHelper.createSimpleCnd("eq", "ZXBZ", "s", "0");
		}
		Map<String, Object> map = null;
		try {
			map = dao.doList(queryCnd, sortInfo, "phis.application.cfg.schemas.EMR_YXBDS_DY", pageNo, pageSize,
					queryCndsType);
			List<Map<String, Object>> body = (List<Map<String, Object>>) map
					.get("body");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> m = body.get(i);
				if (m != null && m.get("BDSNR") != null) {
					Blob blob = (Blob) m.get("BDSNR");
					InputStream is = blob.getBinaryStream();
					Reader utf8_reader = new InputStreamReader(is);
					StringBuffer utf8Text = new StringBuffer();
					int charValue = 0;
					while ((charValue = utf8_reader.read()) != -1) {
						utf8Text.append((char) charValue);
					}
					String resultString = utf8Text.toString();
					m.put("BDSNR", resultString);
				}
				result.add(m);
			}
			map.put("body", result);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 不分页查询
	 * @param queryCnd
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static List<Map<String, Object>> listMedicalExpressRecords(List queryCnd,BaseDAO dao) throws ModelDataOperationException {
		List<Map<String, Object>> body = null;
		if(queryCnd==null){
			queryCnd=CNDHelper.createSimpleCnd("eq", "ZXBZ", "s", "0");
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			body = dao.doList(queryCnd, "DYBDSBH", "phis.application.cfg.schemas.EMR_YXBDS_DY");
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> m = body.get(i);
				if (m != null && m.get("BDSNR") != null) {
					Blob blob = (Blob) m.get("BDSNR");
					InputStream is = blob.getBinaryStream();
					Reader utf8_reader = new InputStreamReader(is);
					StringBuffer utf8Text = new StringBuffer();
					int charValue = 0;
					while ((charValue = utf8_reader.read()) != -1) {
						utf8Text.append((char) charValue);
					}
					String resultString = utf8Text.toString();
					m.put("BDSNR", resultString);
				}
				result.add(m);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
