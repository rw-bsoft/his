/**
 * @(#)MyPageService.java Created on 2012-7-25 上午8:46:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mypage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.mvc.controller.util.MVCSessionListener;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MyPageService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(MyPageService.class);

	/**
	 * 获取登陆者信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetLanderInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
//		List<HashMap<String, Object>> onLineUser = OnlineUserInfoCollector
//				.instance().getOnlineUsers();
		UserRoleToken user = UserRoleToken.getCurrent();
		Date logonTime =user.getLastLoginTime();
		if(logonTime == null){
			logonTime = new Date();
			user.setLastLoginTime(new Date());
		}
//		String urmId =UserUtil.get(UserUtil.USER_ID)+"="+UserUtil.get(UserUtil.MANAUNIT_ID)+"@"+UserUtil.get(UserUtil.JOB_ID);
//		HttpSession session = (HttpSession) ctx.get(Context.WEB_SESSION);
//		for (int i = 0; i < onLineUser.size(); i++) {
//			HashMap<String, Object> map = onLineUser.get(i);
//			if (urmId.equals(map.get("logonName")) && session.getAttribute(OnlineUserInfo.SERVER_IP).equals(map.get("serverIP"))) {
//				if (map.get("logonTime") instanceof Date) {
//					logonTime = (Date) map.get("logonTime");
//				}
//				if (map.get("logonTime") instanceof Long) {
//					Calendar cc = Calendar.getInstance();
//					cc.setTimeInMillis((Long) map.get("logonTime"));
//					logonTime = cc.getTime();
//				}
//				break;
//			}
//		}
		Calendar c = Calendar.getInstance();
		long oltms = c.getTimeInMillis() - logonTime.getTime();
		int hh = (int) oltms / (1000 * 60 * 60);
		int mm = (int) (oltms - hh * 1000 * 60 * 60) / (1000 * 60);
		String olt = hh + ":" + mm;

		Map<String, Object> luMap = new HashMap<String, Object>();
		luMap.put("userName", UserUtil.get(UserUtil.USER_NAME));
		luMap.put("userOrg", UserUtil.get(UserUtil.MANAUNIT_NAME));
		luMap.put("userJob", UserUtil.get(UserUtil.JOB_TITLE));
		luMap.put("loadTime",BSCHISUtil.toString(logonTime, "yyyy-MM-dd HH:mm:ss"));
		luMap.put("onLineTime", olt);

		res.put("body", luMap);
	}

	/**
	 * 任务列表菜单定位
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doFixedPositionMenu(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
//		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
//		String id = (String) reqBodyMap.get("id");
//		AppConfigController ac = AppConfigController.instance();
//		Document defineDoc  = ac.getDefineDoc();
//		Element root = defineDoc.getRootElement();
//		Element el = (Element)root.selectSingleNode("//module[@id='" + id + "']");
//		if(el == null){
//			return ;
//		}
//		Element catalogElement = el.getParent();
//		String catalogId = catalogElement.attributeValue("id");
//		//Catalog c = ac.findCatalog(pid);
//		String appId = catalogElement.getParent().attributeValue("id");
//		Map<String, Object> resBodyMap = new HashMap<String, Object>();
//		resBodyMap.put("appId", appId);
//		resBodyMap.put("catalogId", catalogId);
//		res.put("body", resBodyMap);
	}
}
