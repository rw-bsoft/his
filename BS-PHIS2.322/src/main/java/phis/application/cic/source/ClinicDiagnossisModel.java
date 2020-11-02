package phis.application.cic.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.service.remind.RemindServer;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
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
		Map<String, Object> req = new HashMap<String, Object>();
		;
		String op = null;
		try {
			List<Map<String, Object>> dignosisList = (List<Map<String, Object>>) body
					.get("dignosisList");
			Long JZXH = Long.parseLong(body.get("jzxh").toString());
			String BRID = (String) body.get("brid");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			int plxh = 0;
			Map<String, Long> SJZD_MAP = new HashMap<String, Long>();
			StringBuffer JBPBStr = new StringBuffer();
			StringBuffer JBBGKStr = new StringBuffer();
			String JLBHStr = "";
			StringBuffer hql = new StringBuffer();// 更新挂号表的诊断序号
			hql.append("update MS_GHMX set ZDXH=:zdxh where SBXH=(select GHXH from YS_MZ_JZLS where JZXH=:jzxh)");
			for (int i = 0; i < dignosisList.size(); i++) {
				Map<String, Object> dignosis = dignosisList.get(i);
				op = dignosis.get("JLBH") == null ? "create" : "update";
				int deep = (Integer) (dignosis.get("DEEP"));
				if (deep == 0) {
					dignosis.put("SJZD", 0);
				} else {
					dignosis.put("SJZD", SJZD_MAP.get("SJZD_" + (deep - 1)));
				}
				if (dignosis.get("JBPB") == null) {
					dignosis.put("JBPB", "");
				} else {
					dignosis.put("JBPB", dignosis.get("JBPB") + "");
				}
				if (i == 0) {
					dignosis.put("ZZBZ", 1);
				} else {
					dignosis.put("ZZBZ", 0);
				}
				if(dignosisList.size() >0){
					dignosis.put("FFBZ", 1);
				}else{
					dignosis.put("FFBZ", 0);
				}
				dignosis.put("BLZD", 0);
				dignosis.put("YGZD", 0);
				if(!dignosis.containsKey("FZBZ")){
					dignosis.put("FZBZ", 0);
				}
				dignosis.put("JZXH", JZXH);
				dignosis.put("BRID", Long.parseLong(BRID));
				dignosis.put("JGID", manageUnit);
				dignosis.put("PLXH", plxh++);
				dignosis.put("ZDSJ",BSHISUtil.toDate(dignosis.get("ZDSJ").toString()));
				if (dignosis.containsKey("ZGQK")) {
					if ((dignosis.get("ZGQK") + "").length() == 0) {
						dignosis.remove("ZGQK");
					}
				}
				if (dignosis.containsKey("FBRQ")
						&& dignosis.get("FBRQ") != null) {
					dignosis.put("FBRQ",
							BSHISUtil.toDate(dignosis.get("FBRQ").toString()));
				}
				
				Map<String, Object> genValue = dao.doSave(op,
						BSPHISEntryNames.MS_BRZD_CIC, dignosis, true);
				if (MedicineUtils.parseInt(dignosis.get("ZZBZ")) == 1) {// 主诊断的话更新挂号表
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("jzxh", JZXH);
					map_par.put("zdxh",
							MedicineUtils.parseLong(dignosis.get("ZDXH")));
					dao.doSqlUpdate(hql.toString(), map_par);
				}
				if (op.equals("create")) {
					SJZD_MAP.put("SJZD_" + deep, (Long) genValue.get("JLBH"));
				} else {
					SJZD_MAP.put("SJZD_" + deep,
							Long.parseLong(dignosis.get("JLBH").toString()));
				}
				String topUnitId = ParameterUtil.getTopUnitId();
				String SFQYGWXT = ParameterUtil.getParameter(topUnitId,
						BSPHISSystemArgument.SFQYGWXT, ctx);
				if ("1".equals(SFQYGWXT)) {
					List<Map<String, Object>> JBPB = dao.doQuery(
							"select JBPB as JBPB,JBBGK as JBBGK from GY_JBBM  where JBXH = "
									+ dignosis.get("ZDXH"), null);
					if (JBPB.size() > 0) {
						Map<String, Object> map = JBPB.get(0);
						if (map.get("JBPB") != null
								&& (map.get("JBPB") + "").length() > 0) {
							JBPBStr.append(map.get("JBPB"));
							if("09".equals(map.get("JBPB") + "")){//add by lizhi 2018-01-19增加传染病记录编号
								JLBHStr = SJZD_MAP.get("SJZD_" + deep) + "";
							}
						}
						if (map.get("JBBGK") != null
								&& (map.get("JBBGK") + "").length() > 0) {
							JBBGKStr.append(map.get("JBBGK"));
						}
					}
				}
			}
			req.put("JBPB", JBPBStr);
			req.put("JBBGK", JBBGKStr);
			req.put("MS_BRZD_JLBH", JLBHStr);//add by lizhi 2018-01-19增加传染病记录编号
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

	public void doRemoveClinicDiagnossis(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Object jlbh = body.get("jlbh");
		StringBuffer hql = new StringBuffer();
		hql.append("update MS_GHMX set ZDXH=null where SBXH=(select GHXH from YS_MZ_JZLS a,MS_BRZD b where a.JZXH=b.JZXH and b.JLBH=:jlbh) and ZDXH=(select ZDXH from MS_BRZD where JLBH=:jlbh)");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("jlbh", Long.parseLong(jlbh + ""));
			dao.doSqlUpdate(hql.toString(), map_par);
			dao.doRemove(Long.parseLong(jlbh + ""),
					BSPHISEntryNames.MS_BRZD_CIC);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to remove dignosis information by unknown reason", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "诊断信息删除失败.");
		}

	}

	@SuppressWarnings("unchecked")
	public void doQuerySymptom(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		Long JBBS = 0l;
		if (body.size() > 0 && body.containsKey("JBBS")) {
			JBBS = Long.parseLong(body.get("JBBS") + "");
		}
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("JBBS", JBBS);
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(
					"select ZHBS as ZHBS from EMR_JBZZ where JBBS=:JBBS",
					parameter);
			res.put("ZHBS", list);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to query dignosis information by unknown reason", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询诊断信息失败.", e);
		}
	}
	//诊断提醒 wy
	public void DiagnosisMsg(Map<String, Object> req, Map<String, Object> res, Context ctx) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) req.get("list");
//		String


		StringBuffer sb  = new StringBuffer();

		if (list != null && list.size() > 0) {

			for (Map<String, Object> map : list) {
				//诊断编码
				String ICD10 = map.get("ICD10") + "";
				//诊断名称
				String ZDMC = map.get("ZDMC") + "";
				String content = "    <ITEM>         \n" +
						"      <ZDBM>" + ICD10 + "</ZDBM>\n" +
						"      <ZDMC>" + ZDMC + "</ZDMC>\n" +
						"      <BMLX>01</BMLX>  \n" +
						"    </ITEM>                     \n";
				sb.append(content);
			}

		}

		String KH = req.get("KH") + "";
		String ip = req.get("ip") + "";
		String manageUnit = (String) req.get("manageUnit");
		/** 查询医疗机构代码 */
		String dyHql = "select jgdm_dr as YLJGDM from ehr_manageunit where manageunitid='" + manageUnit + "'";
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> dy = null;
		try {
			dy = dao.doSqlLoad(dyHql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String yljgdm = "";
		if (dy != null) {
			yljgdm = (String) dy.get("YLJGDM");
		}

		String YYKSBM = req.get("YYKSBM") + "";
		String YYYSGH = req.get("YYYSGH") + "";
		String YSXM = req.get("YSXM") + "";
		String strxml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
				"<YL_ACTIVE_ROOT>\n" +
				"  <CFDDM>3102</CFDDM>           \n" +
				"  <KH>" + KH + "</KH>\n" +
				"  <KLX>01</KLX>           \n" +
				"  <YLJGDM>" + yljgdm + "</YLJGDM>\n" +
				"  <JZLX>100</JZLX>\n" +
				"  <YYKSBM>" + YYKSBM + "</YYKSBM>\n" +
				"  <YYYSGH>" + YYYSGH + "</YYYSGH>\n" +
				"  <YSXM>" + YYYSGH + "</YSXM> \n" +
				"  <AGENTIP>10.1.7.11</AGENTIP>\n" +
				"  <AGENTMAC>ff-ff-ff-ff-ff</AGENTMAC>\n" +
				"  <ZD> \n" +
				sb.toString()+
				"  </ZD>        \n" +
				"</YL_ACTIVE_ROOT>";
		System.out.println(strxml);
		RemindServer.sendMsgToRemind(strxml, ip);
	}
}
