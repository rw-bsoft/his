package phis.application.emr.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.S;
import ctd.util.context.Context;

public class EmrTemperatureModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(EmrTemperatureModel.class);

	public EmrTemperatureModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doLoadDaynicSchema(Map<String, Object> res)
			throws PersistentDataOperationException {
		dao.doList(CNDHelper.createSimpleCnd("and", "XTBZ", "i", 1), "XMH",
				BSPHISEntryNames.BQ_SMTZ);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void saveSMTZ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> smtz = (Map<String, Object>) body.get("BQ_TZXM");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Long zyh = Long.parseLong(body.get("zyh").toString());// 住院号
		Long sqxh = Long.parseLong(body.get("sqxh").toString());// 会诊申请号
		Map<String, Object> parMap = new HashMap<String, Object>();
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			StringBuffer sql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			List<Map<String, Object>> tzxmlist = dao.doQuery(" FROM BQ_TZXM",
					new HashMap<String, Object>());
			if (body.get("CJH") != null) {
				parMap.put("ZYH", zyh + "");
				parMap.put("CJSJ", sdftime.parse(body.get("cjsj") + ""));
				List<Map<String, Object>> tzxmParlist = dao
						.doQuery(
								"select b.PYDM as PYDM,a.CJH as CJH,a.XMH as XMH from BQ_SMTZ a,BQ_TZXM b where a.ZYH=:ZYH and a.CJSJ=:CJSJ and a.XMH = b.XMH and a.FCBZ=0 ",
								parMap);
				for (int i = 0; i < tzxmParlist.size(); i++) {
					Map<String, Object> tzMap = tzxmParlist.get(i);
					if (smtz.containsKey(tzMap.get("PYDM"))) {
						HashMap<String, Object> updateMap = new HashMap<String, Object>();
						long CJH = Long.parseLong(tzMap.get("CJH") + "");
						updateMap.put("CJH", CJH);
						if (tzMap.get("XMH") != null) {
							if (Long.parseLong(tzMap.get("XMH").toString()) == 1) {// 体温
								updateMap.put("XMXB", body.get("xmxb"));// 项目下标（体温类型等）
							}
						}
						if (Long.parseLong(tzMap.get("XMH").toString()) == 11) { // 备注类型
							if(!"".equals(smtz.get("BZXX")+"")){
								updateMap.put("BZXX", smtz.get("BZXX"));
							}else{
								updateMap.put("BZXX", DictionaryController.instance()
									.get("phis.dictionary.remarkType")
										.getText(smtz.get("BZLX")+""));
							}
						}
						if (tzMap.get("PYDM") != null) {
							if (smtz.get(tzMap.get("PYDM")) != "") {
								updateMap.put("TZNR",
										smtz.get(tzMap.get("PYDM")));
							}
						}
						dao.doSave("update", BSPHISEntryNames.BQ_SMTZ,
								updateMap, false);
						smtz.remove(tzMap.get("PYDM"));
					}
				}
				params.put("ZYH", zyh);
				Map<String, Object> br = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
						zyh);

				Map<String, Object> smtzObj = new HashMap<String, Object>();
				smtzObj.put("CJZH", getUID());
				smtzObj.put("ZYH", zyh);
				smtzObj.put("JHBZ", 1);
				smtzObj.put("BRKS", Integer.parseInt(br.get("BRKS").toString()));// 病人科室
				smtzObj.put("BRBQ", Integer.parseInt(br.get("BRBQ").toString()));// 病人病区
				smtzObj.put("BRCH", br.get("BRCH").toString());// 病人床号

				smtzObj.put("FCBZ", 0);// 复测标志（0：首测 1：复测）
				smtzObj.put("TWDXS", 0);// 体温单显示（0：不显示 1：显示，显示在体温单的‘空白栏’或‘其他’栏内）
				smtzObj.put("JLSJ", new Date());// 记录时间（实际录入系统的时间）
				smtzObj.put("JLGH", user.getUserId());// 记录人员
				smtzObj.put("ZFBZ", 0);// 作废标志（0：有效 1：已作废）
				smtzObj.put("BZXX", "");// 备注信息（复测方法、辅助呼吸等）
				smtzObj.put("YCBZ", 1);// 异常标志（-2：低 -1：偏低 0：正常 1：偏高 2：高）
				smtzObj.put("CJSJ", body.get("cjsj"));
				smtzObj.put("JGID", jgid);
				for (Map<String, Object> tzObj : tzxmlist) {
					String pydm = tzObj.get("PYDM").toString();
					if (smtz.containsKey(pydm) && smtz.get(pydm) != null
							&& !"".equals(smtz.get(pydm))) {
						if (!"DB".equals(pydm) && "0".equals(smtz.get(pydm).toString())) { // 非大便时，录入值必须大于0
							continue;
						}
						smtzObj.put("XMH",
								Long.parseLong(tzObj.get("XMH").toString()));
						if (Long.parseLong(tzObj.get("XMH").toString()) == 1) {// 体温
							smtzObj.put("XMXB", body.get("xmxb"));// 项目下标（体温类型等）
							if (body.get("FCGL") != null) {
								smtzObj.put("FCBZ", 1);
								smtzObj.put("FCGL", body.get("FCGL"));
								smtzObj.put("CJSJ", smtz.get("FCSJ"));
							}
						}
						if (Long.parseLong(tzObj.get("XMH").toString()) == 11) { // 备注类型
							if(!"".equals(smtz.get("BZXX")+"")){
								smtzObj.put("BZXX", smtz.get("BZXX"));
							}else{
								smtzObj.put("BZXX", DictionaryController.instance()
									.get("phis.dictionary.remarkType")
										.getText(smtz.get("BZLX")+""));
							}
						}
						smtzObj.put("TZNR", smtz.get(pydm));
						dao.doSave("create", BSPHISEntryNames.BQ_SMTZ, smtzObj,
								false);
						smtzObj.remove("XMXB");
					}
				}

			} else {
				// 获取病人信息
				params.put("ZYH", zyh);
				Map<String, Object> br = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
						zyh);

				Map<String, Object> smtzObj = new HashMap<String, Object>();
				smtzObj.put("CJZH", getUID());
				smtzObj.put("ZYH", zyh);
				smtzObj.put("JHBZ", 1);
				smtzObj.put("BRKS", Integer.parseInt(br.get("BRKS").toString()));// 病人科室
				smtzObj.put("BRBQ", Integer.parseInt(br.get("BRBQ").toString()));// 病人病区
				smtzObj.put("BRCH", br.get("BRCH").toString());// 病人床号

				smtzObj.put("FCBZ", 0);// 复测标志（0：首测 1：复测）
				smtzObj.put("TWDXS", 0);// 体温单显示（0：不显示 1：显示，显示在体温单的‘空白栏’或‘其他’栏内）
				smtzObj.put("JLSJ", new Date());// 记录时间（实际录入系统的时间）
				smtzObj.put("JLGH", user.getUserId());// 记录人员
				smtzObj.put("ZFBZ", 0);// 作废标志（0：有效 1：已作废）
				smtzObj.put("BZXX", "");// 备注信息（复测方法、辅助呼吸等）
				smtzObj.put("YCBZ", 1);// 异常标志（-2：低 -1：偏低 0：正常 1：偏高 2：高）
				smtzObj.put("CJSJ", body.get("cjsj"));
				smtzObj.put("JGID", jgid);
				for (Map<String, Object> tzObj : tzxmlist) {
					String pydm = tzObj.get("PYDM").toString();
					if (smtz.containsKey(pydm) && smtz.get(pydm) != null
							&& !"".equals(smtz.get(pydm))) {
						if (!"DB".equals(pydm) && "0".equals(smtz.get(pydm).toString())) { // 非大便时，录入值必须大于0
							continue;
						}
						smtzObj.put("XMH",
								Long.parseLong(tzObj.get("XMH").toString()));
						if (Long.parseLong(tzObj.get("XMH").toString()) == 1) {// 体温
							smtzObj.put("XMXB", body.get("xmxb"));// 项目下标（体温类型等）
							if (body.get("FCGL") != null
									&& !S.isEmpty(body.get("FCGL") + "")) {
								smtzObj.put("FCBZ", 1);
								smtzObj.put("FCGL", body.get("FCGL"));
								smtzObj.put("CJSJ", smtz.get("FCSJ"));
							}
						}
						if (Long.parseLong(tzObj.get("XMH").toString()) == 11) { // 备注类型
							if(!"".equals(smtz.get("BZXX")+"")){
								smtzObj.put("BZXX", smtz.get("BZXX"));
							}else{
								smtzObj.put("BZXX", DictionaryController.instance()
									.get("phis.dictionary.remarkType")
										.getText(smtz.get("BZLX")+""));
							}
						}
						// if(smtz.get(pydm)==null||"".equals(smtz.get(pydm)))continue;
						smtzObj.put("TZNR", smtz.get(pydm));
						dao.doSave("create", BSPHISEntryNames.BQ_SMTZ, smtzObj,
								false);
						smtzObj.remove("XMXB");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fail to get Temporary Data.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}

	}

	private static final long ONE_STEP = 100;
	private static long lastTime = System.currentTimeMillis();
	private static short count = 0;

	public synchronized Long getUID() {
		try {
			if (count == ONE_STEP) {
				boolean done = false;
				while (!done) {
					long now = System.currentTimeMillis();
					if (now == lastTime) {
						try {
							Thread.sleep(1);
						} catch (java.lang.InterruptedException e) {
						}
						continue;
					} else {
						lastTime = now;
						count = 0;
						done = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long result = lastTime * 100 + (count++);
		return result;
	}
}
