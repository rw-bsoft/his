package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class AntimicrobialDrugUseCausesModule {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(AntimicrobialDrugUseCausesModule.class);

	public AntimicrobialDrugUseCausesModule(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 删除抗菌药物使用原因
	 * @param req
	 * @param res
	 * @param dao2
	 * @param ctx
	 */
	public void doSaveAntimicrobialDrug(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao2, Context ctx) {
		try {
//			UserRoleToken user = UserRoleToken.getCurrent();
//			String JGID = user.getManageUnit().getId();// 用户的机构ID
			int pkey = Integer.parseInt(req.get("pkey") + "");
			String syyy = req.get("syyy") + "";
			Map<String, Object> parameter = new HashMap<String, Object>();
//			parameter.put("JGID", JGID);
			parameter.put("SYYY", syyy);
			long count = dao.doCount("MS_CF02", " (syyy ='"+syyy+"' or syyy like '"+syyy+",%' or syyy like '%,"+syyy+",%' or syyy like '%,"+syyy+"')",
					null);
			if (count == 0) {
				dao.doRemove((long) pkey,
						"phis.application.cfg.schemas.EMR_CYDY");
			}
			res.put("canDelete", count == 0 ? true : false);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void reloadYY(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao2, Context ctx) {
		DictionaryController.instance().reload(
				"phis.dictionary.AntibacterialUseReason");
		
	}
	public void doQueryIfUsed(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao2, Context ctx) {
//			UserRoleToken user = UserRoleToken.getCurrent();
//			String JGID = user.getManageUnit().getId();// 用户的机构ID
			String syyy = req.get("syyy") + "";
			Map<String, Object> parameter = new HashMap<String, Object>();
//			parameter.put("JGID", JGID); 
			parameter.put("SYYY", syyy);
			long count;
			try {
				count = dao.doCount("MS_CF02", " (syyy ='"+syyy+"' or syyy like '"+syyy+",%' or syyy like '%,"+syyy+",%' or syyy like '%,"+syyy+"')",
						null);
				res.put("canUpdate", count == 0 ? true : false);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

}
