package phis.application.hos.source;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.validator.ValidateException;

public class HospitalBedSetModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalBedSetModel.class);

	public HospitalBedSetModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 床位维护保存
	 */
	protected Map<String, Object> doSaveBed(String op,
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> remap = new HashMap<String, Object>();
		remap.put("NUM", "0");
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BRCH", body.get("BRCH"));
			map.put("JGID", body.get("JGID"));
			long num = dao.doCount("ZY_CWSZ","BRCH=:BRCH and JGID=:JGID", map);
			if ("create".equals(op)&& num > 0 ) {
				remap.put("NUM", "1");
				return remap;
			}
			if("update".equals(op) && num > 0 && !(body.get("originalBRCH")+"").equals(body.get("BRCH")+"")){
				remap.put("NUM", "-1");
				return remap;
			}
			if("create".equals(op)){
				body.put("JCRQ", new Date());
			}
			if ("update".equals(op) && num == 0) {
				op = "create";
				map.put("BRCH", body.get("originalBRCH"));
				dao.doRemove(map, BSPHISEntryNames.ZY_CWSZ);
			}
			remap = dao.doSave(op, BSPHISEntryNames.ZY_CWSZ, body,
					false);
		} catch (ValidateException e) {
			logger.error("fail to validate medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("fail to save medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
		return remap;
	}
}
