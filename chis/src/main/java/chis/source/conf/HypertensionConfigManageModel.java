package chis.source.conf;

import java.util.List;
import java.util.Map;

import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

public class HypertensionConfigManageModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public HypertensionConfigManageModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	public void saveHypertensionAssessParamete(Map<String, Object> formBody)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> list = dao.doList(null, null,
					MDC_HypertensionAssessParamete);
			String op = "create";
			if (list != null && list.size() > 0) {
				Map<String, Object> r = list.get(0);
				formBody.put("recordId", r.get("recordId"));
				op = "update";
			}
			dao.doSave(op, MDC_HypertensionAssessParamete, formBody, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存高血压评估参数表单设置失败。", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("保存高血压评估参数表单设置失败。", e);
		}
	}

	public void saveHypertensionBPControl(List<Map<String, Object>> listBody)
			throws ModelDataOperationException {
		try {
			for (Map<String, Object> r : listBody) {
				dao.doSave("update", MDC_HypertensionBPControl, r, false);
			}
		} catch (ValidateException e) {
			throw new ModelDataOperationException("保存高血压评估参数列表设置失败。", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存高血压评估参数列表设置失败。", e);
		}
	}

	public void saveHypertensionControl(List<Map<String, Object>> list)
			throws ModelDataOperationException {
		try {
			for (Map<String, Object> r : list) {
				dao.doSave("update", MDC_HypertensionControl, r, false);
			}
		} catch (ValidateException e) {
			throw new ModelDataOperationException("保存高血压评估参数列表设置失败。", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存高血压评估参数列表设置失败。", e);
		}
	}

}
