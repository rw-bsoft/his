/**
 * @(#)ExtendLogonOut.java Created on 2013-04-26 上午09:17:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.service.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.domain.DomainUtil;
import ctd.mvc.controller.support.logon.CommonAspectLogon;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ChisAspectLogon extends CommonAspectLogon {

	Logger logger = LoggerFactory.getLogger(ChisAspectLogon.class);

	@SuppressWarnings("unchecked")
	@Override
	public void afterLogon(Map<String, Object> body) {
		// super.afterLogon(body);
		body.put("serverDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		body.put("serverDateTime",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			logger.error(e.getMessage(), e);
		}
		body.put("childrenRegisterAge",
				parseToInt(app.getProperty("childrenRegisterAge")));
		body.put("childrenDieAge",
				parseToInt(app.getProperty("childrenDieAge")));
		body.put("oldPeopleAge", parseToInt(app.getProperty("oldPeopleAge")));
		body.put("oldPeopleMode",
				parseToInt(app.getProperty(BusinessType.LNR + "_planMode")));
		body.put("hypertensionMode",
				parseToInt(app.getProperty(BusinessType.GXY + "_planMode")));
		body.put("diabetesMode",
				parseToInt(app.getProperty(BusinessType.TNB + "_planMode")));
		body.put("diabetesPrecedeDays",
				parseToInt(app.getProperty(BusinessType.TNB + "_precedeDays")));
		body.put("diabetesDelayDays",
				parseToInt(app.getProperty(BusinessType.TNB + "_delayDays")));
		body.put(
				"pregnantMode",
				parseToInt(app.getProperty(BusinessType.MATERNAL + "_planMode")));
		body.put("rvcMode",
				parseToInt(app.getProperty(BusinessType.RVC + "_planMode")));
		// 自定义年度开始月份
		body.put("oldPeopleStartMonth", app.getProperty("oldPeopleStartMonth"));// 老年人档案随访
		body.put("hypertensionStartMonth",
				app.getProperty("hypertensionStartMonth"));// 高血压档案随访
		body.put("hypertensionRiskStartMonth",
				app.getProperty("hypertensionRiskStartMonth"));// 高血压高危档案随访
		body.put("diabetesStartMonth", app.getProperty("diabetesStartMonth"));// 糖尿病档案随访
		body.put("diabetesRiskStartMonth",
				app.getProperty("diabetesRiskStartMonth"));// 糖尿病高危档案随访
		body.put("psychosisStartMonth", app.getProperty("psychosisStartMonth"));// 精神病档案随访
		body.put("tumourHighRiskStartMonth",
				app.getProperty("tumourHighRiskStartMonth"));// 肿瘤高危档案随访
		body.put("tumourPatientVisitStartMonth",
				app.getProperty("tumourPatientVisitStartMonth"));// 肿瘤现患档案随访
		// 纸质化相关配置
		body.put("phisActiveYW", app.getProperty("phisActiveYW"));
		body.put("areaGridType", app.getProperty("areaGridType"));
		body.put("areaGridShowType", app.getProperty("areaGridShowType"));
		body.put("healthCheckType", app.getProperty("healthCheckType"));
		body.put("hypertensionType", app.getProperty("hypertensionType"));
		body.put("diabetesType", app.getProperty("diabetesType"));
		body.put("psychosisType", app.getProperty("psychosisType"));
		body.put("postnatalVisitType", app.getProperty("postnatalVisitType"));
		body.put("postnatal42dayType", app.getProperty("postnatal42dayType"));
		body.put("childrenHealthCheckShowType",
				app.getProperty("childrenHealthCheckShowType"));
		body.put("checkFollowUpShowType",
				app.getProperty("checkFollowUpShowType"));
		body.put("debilityShowType", app.getProperty("debilityShowType"));
		// 判断phis域是否可用（活跃） phisActive == true[可用] false[不可用]
		CopyOnWriteArraySet<String> adMap = DomainUtil.getActiveDomains();
		if (adMap.contains("phis")) {
			body.put("phisActive", true);
		} else {
			body.put("phisActive", false);
		}

		UserRoleToken token = UserRoleToken.getCurrent();
		body.put("roleType", token.getRole().getType());
		if (token.getProperty("refUserId") == null) {
			token.setProperty("refUserId", token.getUserId());
		}
		if (token.getProperty("refRoleId") == null) {
			token.setProperty("refRoleId", token.getRoleId());
		}
		//团队机构取中心
		String unit = token.getManageUnitId();
		if (unit != null && unit.length() > 9) {
			unit = unit.substring(0, 9);
		}
		body.put("centerUnit", unit);
		token.setProperty("centerUnit", unit);
		try {
			body.put("centerUnitName",
					DictionaryController.instance().get("chis.@manageUnit")
							.getText(unit));
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		HashMap<String, Object> prop = (HashMap<String, Object>) httpSession
				.getAttribute("properties");
		if (prop == null) {
			prop = new HashMap<String, Object>();
		}
		ChisLogonModel clModel = new ChisLogonModel();
		String fds = clModel.getFamilyDoctorOfAssistant();
		body.put("fds", fds);
		prop.put("fds", fds);
		String rds=clModel.getResponsibleDoctorOfAssistant();
		body.put("rds", rds);
		prop.put("rds", rds);
		httpSession.setAttribute("properties", prop);
	}

	private int parseToInt(Object value) {
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(value + "");

	}

}
