/**
 * @(#)OperationCodeService.java Created on 2020-9-21 中午11:10:08
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import ctd.validator.ValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import ctd.util.context.Context;
import phis.source.PersistentDataOperationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * 手术内码维护Model
 * @author 杨贺
 */
public class OperationCodeModel {
	protected Logger logger = LoggerFactory
			.getLogger(OperationCodeModel.class);
	protected BaseDAO dao;

	public OperationCodeModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * @author yanghe
	 * @createDate 2020/9/21
	 * @description 手术内码导入功能
	 * @updateInfo
	 */
	public void saveSsnm(List<Map<String,Object>> body, Context ctx)
			throws ModelDataOperationException {
		try {
			for (Map<String, Object> ssnmMap : body) {
				Map<String, Object> map_repeat = new HashMap<String, Object>();
				map_repeat.put("keyName", "SSDM");
				map_repeat.put("keyValue", MedicineUtils.parseString(ssnmMap.get("SSDM")));
				map_repeat.put("tableName", "GY_SSDM");
				MedicineCommonModel model = new MedicineCommonModel(dao);
				if (model.repeatVerification(map_repeat)) {
					continue;
				}
				dao.doSave("create", BSPHISEntryNames.GY_SSDM, ssnmMap, false);// 保存基础信息GY_SSDM_YR
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "手术内码保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "手术内码保存失败", e);
		}
	}

}
