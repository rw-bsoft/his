package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PHISExpSymbols;
import phis.source.PersistentDataOperationException;
import phis.source.controller.CharacterEncodingController;

import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 收费项目维护
 * 
 * @author shiwy 2012.06.29
 */
public class ConfigChargingProjectsSaveModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsSaveModel.class);

	public ConfigChargingProjectsSaveModel(BaseDAO dao) {
		this.dao = dao;
	}

	// 保存收费项目
	public Map<String, Object> doSaveChargingProjects(String op,
			Map<String, Object> res, Map<String, Object> body, Context ctx)
			throws ValidateException, PersistentDataOperationException {
		Map<String, Object> req = null;
		req = dao.doSave(op, BSPHISEntryNames.GY_SFXM, body, false);// 保存收费项目GY_SFXM
		DictionaryController.instance().reload("phis.dictionary.feesDic");
		/** modified by gaof 2013-9-23  修改bug：新增保存报错 */
		Long sfxm = (op.equals("create") ? (Long) req.get("SFXM") : Long
				.parseLong(body.get("SFXM") + ""));// 获取主键
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("XMGB", sfxm.intValue());
		map.put("SFXM", sfxm);
		body.put("SFXM", sfxm);
		res.put("body", body);
		if ("create".equals(op)) {
			StringBuffer updmap = new StringBuffer();
			updmap.append("UPDATE ");
			updmap.append(" GY_SFXM ");
			updmap.append(" SET FYLB=:XMGB,MZGB=:XMGB,ZYGB=:XMGB WHERE SFXM=:SFXM");
			dao.doUpdate(updmap.toString(), map);
			Map<String, Object> ylsfParameters = new HashMap<String, Object>();
			ylsfParameters.put("SFXM", sfxm);
			String ylsfHql = "select SFXM as FYGB,SFMC as FYMC,'笔' as FYDW,PYDM as PYDM,MZSY as MZSY,ZYSY as ZYSY,'1' as YJSY,'0' as TJFY,'0' as TXZL,'0' as ZFPB,0 as BZJG from GY_SFXM where SFXM=:SFXM";
			Map<String, Object> ylsfMap = dao.doLoad(ylsfHql, ylsfParameters);
			String wbm = CharacterEncodingController.getCode(
					(String) ylsfMap.get("FYMC"), PHISExpSymbols.WB, ctx);
			String jxm = CharacterEncodingController.getCode(
					(String) ylsfMap.get("FYMC"), PHISExpSymbols.JX, ctx);
			ylsfMap.put("WBDM", wbm);
			ylsfMap.put("JXDM", jxm);
			Map<String, Object> ylsfReq = null;
			ylsfReq = dao.doSave("create", BSPHISEntryNames.GY_YLSF, ylsfMap,
					false);// 保存费用属性GY_YLSF
			Map<String, Object> fybmParameters = new HashMap<String, Object>();
			long fyxh = Long.parseLong(op.equals("create") ? ylsfReq
					.get("FYXH") + "" : body.get("FYXH") + "");// 获取GY_YLSF主键
			fybmParameters.put("FYXH", fyxh);
			String fybmHql = "select FYXH as FYXH,FYMC as FYMC,PYDM as PYDM,WBDM as WBDM,JXDM as JXDM,'1' as BMFL from GY_YLSF where FYXH=:FYXH";
			Map<String, Object> fybmMap = dao.doLoad(fybmHql, fybmParameters);
			dao.doSave("create", BSPHISEntryNames.GY_FYBM, fybmMap, false);// 保存费用别名GY_FYBM
			// 先查询到GY_ZFBL表里有几种病人性质
			List<Map<String, Object>> list = dao.doQuery(
					"SELECT DISTINCT BRXZ as BRXZ FROM GY_ZFBL", null);
			// 然后每个病人性质插入一条该收费项目
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> brxzMap = new HashMap<String, Object>();
				brxzMap.put("BRXZ", list.get(i).get("BRXZ"));
				brxzMap.put("SFXM", sfxm);
				brxzMap.put("ZFBL", 1);
				dao.doSave("create", BSPHISEntryNames.GY_ZFBL, brxzMap, false);
			}
		}
		return req;
	}
}
