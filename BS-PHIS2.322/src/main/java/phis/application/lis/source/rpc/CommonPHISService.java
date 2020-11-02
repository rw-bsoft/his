package phis.application.lis.source.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.account.user.UserController;
import ctd.controller.exception.ControllerException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class CommonPHISService implements IPHISService {

	protected Logger logger = LoggerFactory.getLogger(CommonPHISService.class);
	private Map<Action, HISService> services = null;
	private SessionFactory sessionFactory;
	private static List<Action> noTrActions = new ArrayList<Action>();

	public Map<Action, HISService> getServices() {
		return services;
	}

	public void setServices(Map<Action, HISService> services) {
		this.services = services;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> execute(Map<String, Object> paramMap)
			throws PHISServiceException {
		
		Context ctx = ContextUtils.getContext();
		authorization(ctx);
		Session session = (Session) ctx.get(Context.DB_SESSION);
		if (session == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			session = sf.openSession();
			ctx.put(Context.DB_SESSION, session);
		}
		Action action = Action.valueOf(paramMap.get("action").toString());
		if (transactedActions(action)) {
			session.beginTransaction();
		}
		HISService service = (HISService) this.services.get(action);
		if (service == null)
			throw new PHISServiceException(4004, "No such action[" + action
					+ "].");
		Map res;
		Transaction trx;
		try {
			res = service.process((Map) paramMap.get("body"), ctx);
		} catch (HISException e) {
			throw new PHISServiceException(6000, e.getMessage(), e);
		} finally {
			trx = session.getTransaction();
			if (trx.isActive()) {
				trx.commit();
			}
			session.close();
		}
		if (res == null) {
			res = new HashMap();
		}
		if (res.get("x-response-code") == null) {
			res.put("x-response-code", Integer.valueOf(200));
			res.put("x-response-msg", "success");
		} else {
			int code = Integer.valueOf(res.get("x-response-code").toString())
					.intValue();
			if (code != 200) {
				throw new PHISServiceException(code, res.get("x-response-msg")
						.toString());
			}else{
				res.put("x-response-code", Integer.valueOf(200));
				res.put("x-response-msg", "success");
			}
		}
		return res;
	}

	private boolean authorization(Context ctx) {
		User user;
		UserRoleToken token;
		try {
			user = UserController.instance().get("system");
			UserRoleToken ur = new UserRoleToken();
			ur.setId(123);
			ur.setRoleId("system");
//			token = user.getUserRoleToken(8)
			ContextUtils.put(Context.USER_ROLE_TOKEN, ur);
		} catch (ControllerException e) {
			e.printStackTrace();
			return false;
		}
//		if (user != null) {
//			ContextUtils.put(Context.USER,user);
//			return true;
//		}
		return false;
	}

	private boolean transactedActions(Action action) {
		if (noTrActions.isEmpty()) {
			noTrActions.add(Action.getbrda);
			noTrActions.add(Action.getfyxx);
			noTrActions.add(Action.mzbrxxxg);
			noTrActions.add(Action.mzyjqxzx);
			noTrActions.add(Action.mzyjtj);
			noTrActions.add(Action.mzyjzf);
			noTrActions.add(Action.mzyjqxzf);
			noTrActions.add(Action.mzyjztcx);
			noTrActions.add(Action.mzyjzx);
			noTrActions.add(Action.zybrxxxg);
			noTrActions.add(Action.zyyzqxzx);
			noTrActions.add(Action.zyyztj);
			noTrActions.add(Action.zyyzzf);
			noTrActions.add(Action.zyyzqxzf);
			noTrActions.add(Action.zyyzztcx);
			noTrActions.add(Action.zyyzzx);
			noTrActions.add(Action.getTablePKey);
		}
		if (noTrActions.contains(action)) {
			return false;
		}
		return true;
	}
}
