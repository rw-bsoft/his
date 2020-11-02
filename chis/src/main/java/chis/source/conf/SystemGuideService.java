/**
 * @(#)SystemGuideService.java Created on 2012-6-8 上午09:54:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class SystemGuideService extends AbstractActionService implements
		DAOSupportable {
	public static final String SYSTEMCOMMONINITED = "systemCommonInited";
	// public static final String UNITTYPEINITED = "unitTypeInited";
	public static final String INTERFACEINITED = "interfaceInited";
	public static final String ZOOKEEPERINITED = "zookeeperInited";
	public static final String PLANTYPEINITED = "planTypeInited";
	public static final String OLDPEOPLEINITED = "oldPeopleInited";
	public static final String HYPERTENSIONINITED = "hypertensionInited";
	public static final String DIABETESINITED = "diabetesInited";
	public static final String CHILDRENINITED = "childrenInited";
	public static final String DEBILITYCHILDRENINITED = "debilityChildrenInited";
	public static final String PREGNANTINITED = "pregnantInited";
	public static final String PSYCHOSISINITED = "psychosisInited";
	public static final String ALLINITED = "allInited";
	private static Logger logger = LoggerFactory
			.getLogger(SystemGuideService.class);

	protected void doCheckSystemInited(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();

		List<String> configList = getConfigList();
		String flag = "";
		for (String s : configList) {
			try {
				flag = smm.getSystemConfigData(s);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
			if (s.equals(ALLINITED) && flag.equals("true")) {
				body.put(s, flag);
				break;
			}
			if (flag.equals("false")) {
				body.put(s, flag);
			}
		}
		int count = body.size();
		body.put("size", count);
		res.put("body", body);
	}

	private List<String> getConfigList() {
		List<String> configList = new ArrayList<String>();
		configList.add(ALLINITED);
		configList.add(SYSTEMCOMMONINITED);
		// configList.add(UNITTYPEINITED);
		configList.add(INTERFACEINITED);
		configList.add(ZOOKEEPERINITED);
		configList.add(PLANTYPEINITED);
		configList.add(OLDPEOPLEINITED);
		configList.add(HYPERTENSIONINITED);
		configList.add(DIABETESINITED);
		configList.add(CHILDRENINITED);
		configList.add(DEBILITYCHILDRENINITED);
		configList.add(PREGNANTINITED);
		configList.add(PSYCHOSISINITED);
		return configList;
	}

	protected void doSaveGuideToApp(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		String panelId = (String) req.get("panelId");
		String initFlag = (String) req.get("initFlag");
		String type = (String) req.get("type");
		try {
			if (type.equals("next")) {
				smm.saveSystemConfigData(panelId, "true");
				if (initFlag.equals("true")) {
					smm.saveSystemConfigData(ALLINITED, initFlag);
				}
			} else if (type.equals("pre")) {
				smm.saveSystemConfigData(panelId, "false");
			} else {
				List<String> configList = getConfigList();
				for (String s : configList) {
					smm.saveSystemConfigData(s, "true");
				}
			}
		} catch (Exception e) {
			logger.error("failed to save guide to app file !", e);
			throw new ServiceException(e);
		}
	}

}
