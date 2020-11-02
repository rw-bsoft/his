package chis.source.service.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;

import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.domain.DomainUtil;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.service.logon.LogonInfoLoader;
import ctd.util.AppContextHolder;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import ctd.validator.Validator;

public class ChisCommonLogonInfo implements LogonInfoLoader {
	String HER_HealthRecipeRecord="chis.application.her.schemas.HER_HealthRecipeRecord";
	private static final Logger logger = LoggerFactory
			.getLogger(ChisCommonLogonInfo.class);

	@SuppressWarnings("unchecked")
	@RpcService
	@Override
	public Map<String, Object> afterLogon() {
		HashMap<String, Object> res = new HashMap<String, Object>();
		res.put("serverDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		res.put("serverDateTime",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		Map<String, Object> body = new HashMap<String, Object>();
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			logger.error(e.getMessage(), e);
		}
		body.put("childrenRegisterAge", Integer.parseInt((String) app
				.getProperty("childrenRegisterAge")));
		body.put("childrenDieAge",
				Integer.parseInt((String) app.getProperty("childrenDieAge")));
		body.put("oldPeopleAge",
				Integer.parseInt((String) app.getProperty("oldPeopleAge")));
		body.put(
				"oldPeopleMode",
				Integer.parseInt((String) app.getProperty(BusinessType.LNR
						+ "_planMode")));
		body.put(
				"hypertensionMode",
				Integer.parseInt((String) app.getProperty(BusinessType.GXY
						+ "_planMode")));
		body.put(
				"diabetesMode",
				Integer.parseInt((String) app.getProperty(BusinessType.TNB
						+ "_planMode")));
		body.put(
				"diabetesPrecedeDays",
				Integer.parseInt((String) app.getProperty(BusinessType.TNB
						+ "_precedeDays")));
		body.put(
				"diabetesDelayDays",
				Integer.parseInt((String) app.getProperty(BusinessType.TNB
						+ "_delayDays")));
		body.put(
				"pregnantMode",
				Integer.parseInt((String) app.getProperty(BusinessType.MATERNAL
						+ "_planMode")));
		body.put(
				"rvcMode",
				Integer.parseInt((String) app.getProperty(BusinessType.RVC
						+ "_planMode")));
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

		body.put("childrenCheckupType", app.getProperty("childrenCheckupType"));
		body.put("KLX", app.getProperty("KLX"));
		// 判断phis域是否可用（活跃） phisActive == true[可用] false[不可用]
		CopyOnWriteArraySet<String> adMap = DomainUtil.getActiveDomains();
		if (adMap.contains("phis")) {
			body.put("phisActive", true);
		} else {
			body.put("phisActive", false);
		}

		res.put("topUnitId", getTopUnitId());
		UserRoleToken token = UserRoleToken.getCurrent();
		token.setProperty("userId", token.getUserId());
		body.put("roleType", token.getRole().getType());
		res.put("exContext", body);
		if (token.getProperty("refUserId") == null) {
			token.setProperty("refUserId", token.getUserId());

		}
		if (token.getProperty("refRoleId") == null) {
			token.setProperty("refRoleId", token.getRoleId());
		}
		// 团队机构取中心
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
		ChisLogonModel clModel = new ChisLogonModel();
		String fds = clModel.getFamilyDoctorOfAssistant();
		res.put("fds", fds);
		String rds = clModel.getResponsibleDoctorOfAssistant();
		res.put("rds", rds);
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		if (httpSession != null) {
			HashMap<String, Object> prop = (HashMap<String, Object>) httpSession
					.getAttribute("properties");
			if (prop == null) {
				prop = new HashMap<String, Object>();
			}
			prop.put("fds", fds);
			httpSession.setAttribute("properties", prop);
		} else {
			UserRoleToken t = UserRoleToken.getCurrent();
			t.setProperty("fds", fds);
		}

		return res;
	}

	private static String getTopUnitId() {
		String organId = "";
		try {
			organId = UserRoleToken.getCurrent().getOrganId();
		} catch (Exception e) {
			Dictionary manage;
			try {
				manage = DictionaryController.instance()
						.get("phis.@manageUnit");
				organId = manage.getSlice("", 3, null).get(0).getKey();
			} catch (ControllerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (organId != null && organId.indexOf(".") >= 0) {
			return organId.substring(organId.indexOf(".") + 1);
		}
		return organId;
	}
	@RpcService
	public void execute(Map<String, Object> record)
			throws Exception {
		String op = (String) record.get("op");
		if (StringUtils.isEmpty(op)) {
			op = "create";
		}
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		try {
			ss.beginTransaction();
			//保存健康处方信息
			if(record.get("action").equals("saveHealthRecipeRecord")){
				saveHealthRecipeRecord(record,op,false);
			}
			ss.getTransaction().commit();
		} catch (Exception e) {
			ss.getTransaction().rollback();
			throw e;
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}
	@RpcService
	public void saveHealthRecipeRecord(Map<String, Object> record, String op,
			boolean validate) throws ValidateException, ServiceException {
		doSave(op, HER_HealthRecipeRecord, record, validate);
	}
	public Map<String, Object> doSave(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ServiceException {
		Context ctx = ContextUtils.getContext();
		SimpleDAO dao = null;
		Map<String, Object> genValues = null;
		try {
			Schema sc = SchemaController.instance().get(entryName);
			dao = new SimpleDAO(sc, ctx);
			if (validate) {
				Validator.validate(sc, record, ctx);
			}
			if (StringUtils.isEmpty(op)) {
				op = "create";
			}
			if (op.equals("create")) {
				genValues = dao.create(record);
			} else {
				genValues = dao.update(record);
			}
		} catch (DataAccessException e) {
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}

		return genValues;
	}
}
