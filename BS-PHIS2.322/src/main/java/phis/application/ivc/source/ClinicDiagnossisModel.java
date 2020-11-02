package phis.application.ivc.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ClinicDiagnossisModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicDiagnossisModel.class);

	public ClinicDiagnossisModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 新建或者修改诊断信息
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doSaveClinicDiagnossis(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();;
		String op = null;
		try {
			List<Map<String, Object>> dignosisList = (List<Map<String, Object>>) body
					.get("dignosisList");
			Long JZXH =  Long.parseLong(body.get("jzxh").toString());
			String BRID = (String) body.get("brid");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnitId();
			int plxh = 0;
			Map<String,Long> SJZD_MAP = new HashMap<String,Long>();
			StringBuffer JBPBStr = new StringBuffer();
			StringBuffer JBBGKStr = new StringBuffer();
			for (int i=0; i<dignosisList.size();i++) {
				Map<String, Object> dignosis = dignosisList.get(i);
				op = dignosis.get("JLBH") == null ? "create" : "update";
				int deep = (Integer)(dignosis.get("DEEP"));
				if(deep==0) {
					dignosis.put("SJZD", 0);
				}else {
					dignosis.put("SJZD", SJZD_MAP.get("SJZD_"+(deep-1)));
				}
				if(i==0) {
					dignosis.put("ZZBZ", 1);
				}else {
					dignosis.put("ZZBZ", 0);
				}
				dignosis.put("JZXH", JZXH);
				dignosis.put("BRID", Long.parseLong(BRID));
				dignosis.put("JGID", manageUnit);
				dignosis.put("PLXH", plxh++);
				dignosis.put("ZDSJ", BSHISUtil.toDate(dignosis.get("ZDSJ").toString()));
				if(dignosis.containsKey("ZGQK")){
					if((dignosis.get("ZGQK")+"").length()==0){
						dignosis.remove("ZGQK");
					}
				}
				Map<String,Object> genValue = dao.doSave(op, BSPHISEntryNames.MS_BRZD, dignosis, false);
				if(op.equals("create")) {
					SJZD_MAP.put("SJZD_"+deep, (Long) genValue.get("JLBH"));
				}else {
					SJZD_MAP.put("SJZD_"+deep, Long.parseLong(dignosis.get("JLBH").toString()));
				}
				String topUnitId = ParameterUtil.getTopUnitId();
				String SFQYGWXT = ParameterUtil.getParameter(topUnitId, BSPHISSystemArgument.SFQYGWXT,ctx);
				if("1".equals(SFQYGWXT)){
					List<Map<String, Object>> JBPB = dao.doQuery("select JBPB as JBPB,JBBGK as JBBGK from GY_JBBM where JBXH = "+dignosis.get("ZDXH"), null);
					if(JBPB.size()>0){
						Map<String, Object> map = JBPB.get(0);
						if(map.get("JBPB") != null && (map.get("JBPB")+"").length()>0){
							JBPBStr.append(map.get("JBPB"));
						}
						if(map.get("JBBGK") != null && (map.get("JBBGK")+"").length() >0){
							JBBGKStr.append(map.get("JBBGK"));
						}
					}
				}
			}
			req.put("JBPB", JBPBStr);
			req.put("JBBGK", JBBGKStr);
		} catch (ValidateException e) {
			logger.error("fail to validate dignosis information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "诊断信息保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("fail to save dignosis information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "诊断信息保存失败.");
		} catch (Exception e) {
			logger.error("fail to save dignosis information by unknown reason",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "诊断信息保存失败.");
		}
		return req;
	}
	
	public void doRemoveClinicDiagnossis(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		Object jlbh = body.get("jlbh");
		try {
			dao.doRemove(jlbh, BSPHISEntryNames.MS_BRZD);
		} catch (PersistentDataOperationException e) {
			logger.error("fail to remove dignosis information by unknown reason",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "诊断信息删除失败.");
		}
		
	}
}
