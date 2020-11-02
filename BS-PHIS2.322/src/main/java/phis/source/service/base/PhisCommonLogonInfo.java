package phis.source.service.base;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pub.source.PermissionsVerifyService;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.domain.DomainUtil;
import ctd.print.PrintUtil;
import ctd.service.core.ServiceException;
import ctd.service.logon.LogonInfoLoader;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class PhisCommonLogonInfo implements LogonInfoLoader {
	private static final Logger logger = LoggerFactory
			.getLogger(PhisCommonLogonInfo.class);

	// @RpcService
	@SuppressWarnings("unchecked")
	@RpcService(executor = "HibernateSupportExecutor")
	// @DBSupport(transaction=true)
	@Override
	public Map<String, Object> afterLogon() {
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		HashMap<String, Object> prop = null;
		if (httpSession != null) {
			prop = (HashMap<String, Object>) httpSession
					.getAttribute("properties");
		}
		if (prop == null) {
			prop = new HashMap<String, Object>();
		}

		HashMap<String, Object> res = new HashMap<String, Object>();
		// super.afterLogon(res);
		try {
			res.put("serverDate",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			res.put("serverDateTime", new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss").format(new Date()));
			// add by yangl 根据用户ID，找到SYS_Personnel表中的YGDM
			UserRoleToken token = UserRoleToken.getCurrent();
			BaseDAO dao = new BaseDAO();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PERSONID", token.getUserId());
			List<Map<String, Object>> list = dao
					.doQuery(
							"select MEDICALROLES as MEDICALROLES from SYS_Personnel where PERSONID=:PERSONID",
							parameters);
			if (list.size() > 0) {
				Map<String, Object> SYS_Personnel = list.get(0);
				prop.put("YLJS", SYS_Personnel.get("MEDICALROLES"));//
				res.put("YLJS", SYS_Personnel.get("MEDICALROLES"));
				token.setProperty("YLJS", SYS_Personnel.get("MEDICALROLES"));

			}
			// 框架登录用户对应HIS员工的YGDM
			// res.put("staffId", SYS_Personnel.get("YGDM"));// 返回到前台缓存
			// ContextUtils.put("user.prop.staffId",
			// SYS_Personnel.get("YGDM"));//
			// 支持表达式中的用户
			// 增加顶级节点
			prop.put("topUnitId", ParameterUtil.getTopUnitId());
			res.put("topUnitId", ParameterUtil.getTopUnitId());// 返回到前台缓存
			res.put("roleType", token.getRole().getType());
			// 增加判断社区系统是否可用，（即chis domain is active at the same zookeeper)
			CopyOnWriteArraySet<String> adMap = DomainUtil.getActiveDomains();
			if (adMap.contains("chis")) {
				res.put("chisActive", true);
			} else {
				res.put("chisActive", false);
			}
			// 增加基本信息,跨域访问时使用
			Map<String, Object> phisApp = new HashMap<String, Object>();
			phisApp.put("uid", token.getUserId());
			phisApp.put("logonName", token.getUserId());
			phisApp.put("uname", token.getUserName());
			phisApp.put("dept", token.getManageUnitName());
			phisApp.put("deptId", token.getManageUnitId());
			phisApp.put("deptRef", token.getManageUnit().getRef());
			phisApp.put("jobtitle", token.getRoleName());
			phisApp.put("jobtitleId", token.getRoleId());
			phisApp.put("jobId", token.getRoleId());
			phisApp.put("urt", token.getId());
			phisApp.put("roleType", token.getRole().getType());
			res.put("phisApp", phisApp);
			// 业务权限信息
			PermissionsVerifyService pvs = new PermissionsVerifyService();
			Map<String, Object> req = new HashMap<String, Object>();
			List<String> pvList = Arrays.asList("TOPFUNC.PatientList",
					"TOPFUNC.PharmacySwitch", "TOPFUNC.WardSwitch",
					"TOPFUNC.DepartmentSwitch_out", "TOPFUNC.StoreHouseSwitch",
					"TOPFUNC.TreasurySwitch", "TOPFUNC.MedicalSwitch");
			req.put("body", pvList);

			pvs.doLoadPermissions(req, res, dao, ContextUtils.getContext());
		} catch (ServiceException e) {
			logger.error("init logon info error:" + e.getMessage());
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			logger.error("init logon info error:" + e.getMessage());
			e.printStackTrace();
		}
		if (httpSession != null) {
			httpSession.setAttribute("properties", prop);
		}
		PrintUtil.setLOAD("phis.simpleLoad");
		PrintUtil.setQUERY("phis.simpleQuery");
		return res;
	}

}
