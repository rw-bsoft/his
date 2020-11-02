/**
 * @(#)EducationMaterial.java Created on 2013-5-8 下午3:13:10
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.FileUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class EducationMaterialService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(EducationMaterialService.class);

	/**
	 * 保存健康教育资料信息
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午3:28:41
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveEcucationMaterialInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> bodyMap = (Map<String, Object>) req.get("body");
		bodyMap.put("createDate", new Date());
		bodyMap.put("lastModifyDate", new Date());
		EducationMaterialModule emModule = new EducationMaterialModule(dao);
		String op = "create";
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = emModule.saveEduMaterialInfo(op, bodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save education material info failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", resMap);
	}

	/**
	 * 检察文件是否已经上传
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-8 下午4:52:12
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doCheckFileName(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String setId = (String) reqBodyMap.get("setId");
		String exeId = (String) reqBodyMap.get("exeId");
		String recordId = (String) reqBodyMap.get("recordId");
		String fileName = (String) reqBodyMap.get("fileName");
		boolean exist = false;
		EducationMaterialModule emModule = new EducationMaterialModule(dao);
		try {
			exist = emModule.checkFileName(setId, exeId, recordId, fileName);
		} catch (ModelDataOperationException e) {
			logger.error("Check the File is exist failed. ", e);
			throw new ServiceException(e);
		}
		res.put("exist", exist);
	}

	/**
	 * 
	 * @Description:删除文件及文件项目记录信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-19 下午5:39:08
	 * @Modify:
	 */
	public void doRemoveFile(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		boolean success = false;
		String pkey = (String) req.get("pkey");
		EducationMaterialModule emModule = new EducationMaterialModule(dao);
		Map<String, Object> fileInfoMap = null;
		try {
			fileInfoMap = emModule.getEduMaterialByPkey(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to Get the file info by pkey[" + pkey + "].",
					e);
			throw new ServiceException(e);
		}
		String fileSite = (String) fileInfoMap.get("fileSite");
		String fileFullName = fileSite.replace("\\", "\\\\");
		boolean flag = FileUtil.deleteFile(fileFullName);
		if (flag) {
			try {
				emModule.removeFileInfo(pkey);
				success = true;
			} catch (ModelDataOperationException e) {
				logger.error("Failed to delete the file and fileInfo by pkey["
						+ pkey + "]", e);
				throw new ServiceException(e);
			}
		}
		res.put("success", success);
	}
}
