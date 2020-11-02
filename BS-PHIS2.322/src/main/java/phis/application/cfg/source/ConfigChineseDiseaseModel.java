package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigChineseDiseaseModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChineseDiseaseModel.class);

	public ConfigChineseDiseaseModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doSaveChineseDiseaseUnion(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		List<Map<String, Object>> list_jbzz = (List<Map<String, Object>>) req.get("body");
		if(list_jbzz.size()==0){
			return;
		}
		try {
			dao.doRemove("JBBS", Long.parseLong(list_jbzz.get(0).get("JBBS")+""), BSPHISEntryNames.EMR_JBZZ);
			for(Map<String, Object> map_jbzz:list_jbzz){
				dao.doSave("create", BSPHISEntryNames.EMR_JBZZ, map_jbzz, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		} catch (ValidateException e) {
			logger.error("保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}

}
