/**
 * @(#)ExtendLogonOut.java Created on 2013-10-30 上午09:17:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.source.service.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.domain.DomainUtil;
import ctd.mvc.controller.support.logon.CommonAspectLogon;
import ctd.print.PrintUtil;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class PhisAspectLogon extends CommonAspectLogon {

	Logger logger = LoggerFactory.getLogger(PhisAspectLogon.class);

	@SuppressWarnings("unchecked")
	@Override
	public void afterLogon(Map<String, Object> body) {
		// super.afterLogon(body);
		HttpSession httpSession = ((HttpServletRequest) ContextUtils
				.get(Context.HTTP_REQUEST)).getSession(false);
		HashMap<String, Object> prop = (HashMap<String, Object>) httpSession
				.getAttribute("properties");
		if (prop == null) {
			prop = new HashMap<String, Object>();
		}
		body.put("serverDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		body.put("serverDateTime",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		// 支持表达式中的用户
		// 增加顶级节点
		prop.put("topUnitId", ParameterUtil.getTopUnitId());
		body.put("topUnitId", ParameterUtil.getTopUnitId());// 返回到前台缓存
		//增加判断社区系统是否可用，（即chis domain is active at the same zookeeper)
		CopyOnWriteArraySet<String> adMap = DomainUtil.getActiveDomains();
		if(adMap.contains("chis")){
			body.put("chisActive", true);
		}else{
			body.put("chisActive", false);
		}
		httpSession.setAttribute("properties", prop);
		PrintUtil.setLOAD("phis.simpleLoad");
		PrintUtil.setQUERY("phis.simpleQuery");
	}
}
