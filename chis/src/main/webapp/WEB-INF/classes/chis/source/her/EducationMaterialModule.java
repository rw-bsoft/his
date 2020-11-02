/**
 * @(#)EducationMaterialModule.java Created on 2013-5-8 下午4:02:01
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.util.HashMap;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class EducationMaterialModule implements BSCHISEntryNames {

	private BaseDAO dao;

	public EducationMaterialModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存健康教育资料信息
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午4:10:26
	 * @Modify:
	 */
	public Map<String, Object> saveEduMaterialInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, HER_FileUpload, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存健康教育资料信息数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康教育资料信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 检查资料文件是否存在
	 * 
	 * @Description:
	 * @param setId
	 * @param fileName
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-8 下午6:12:33
	 * @Modify:
	 */
	public boolean checkFileName(String setId, String exeId, String recordId,
			String fileName) throws ModelDataOperationException {
		boolean exist = false;
		String hql = new StringBuffer("select count(*) as recordNum from ")
				.append(HER_FileUpload)
				.append(" where setId = :setId and exeId = :exeId and recordId = :recordId and fileName = :fileName")
				.toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("setId", setId);
		paraMap.put("exeId", exeId);
		paraMap.put("recordId", recordId);
		paraMap.put("fileName", fileName);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查资料文件是否存在失败！", e);
		}
		long recordNum = 0;
		if (rsMap != null && rsMap.size() > 0) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		if (recordNum > 0) {
			exist = true;
		}
		return exist;
	}

	/**
	 * 根据主键获取健康教育资料信息
	 * 
	 * @Description:
	 * @param fileId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-9 下午3:41:26
	 * @Modify:
	 */
	public Map<String, Object> getEduMaterialByPkey(String fileId)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(HER_FileUpload, fileId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康教育资料信息失败!", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:删除文件信息
	 * @param pkey
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-19 下午5:43:55
	 * @Modify:
	 */
	public void removeFileInfo(String pkey) throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, HER_FileUpload);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除文件信息失败！", e);
		}
	}
}
