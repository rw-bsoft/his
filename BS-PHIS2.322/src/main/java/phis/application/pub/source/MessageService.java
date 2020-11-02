/**
 * @(#)PublicService.java Created on 2012-1-5 上午11:40:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pub.source;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.message.Message;
import phis.source.message.MsgClient;
import phis.source.message.MsgManager;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BCLUtil;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.JSONProtocol;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description 短信服务
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class MessageService extends AbstractActionService implements
		DAOSupportable {

	static Logger logger = LoggerFactory.getLogger(MessageService.class);

	/*
	 * 传染病报告卡发送短信
	 */
	public void doSendMessage(Map<String, Object> req,
					Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
				//Map<String, Object> body = (Map<String, Object>) req.get("body");
				MessageModel model = new MessageModel(dao);
				try {
					model.doSendMessage(res,ctx);
				} catch (ModelDataOperationException e) {
					throw new ServiceException("发送短信失败！", e);
				}
			}
	
	/*
	 * 家医履约发送短信
	 */
	public void doSendJYLYMessage(Map<String, Object> req,
					Map<String, Object> res, BaseDAO dao, Context ctx)
					throws ServiceException {
				//Map<String, Object> body = (Map<String, Object>) req.get("body");
				MessageModel model = new MessageModel(dao);
				try {
					model.doSendJYLYMessage(req,res,ctx);
				} catch (ModelDataOperationException e) {
					throw new ServiceException("发送短信失败！", e);
				}
			}
}
