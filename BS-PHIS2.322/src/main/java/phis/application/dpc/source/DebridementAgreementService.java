/**
 * @(#)EmpiService2.java Created on 2012-7-2 下午03:53:53
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.dpc.source;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pix.source.EmpiModel;
import phis.application.pix.source.EmpiUtil;
import phis.application.pix.source.SmzModel;
import phis.application.pub.source.PublicModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class DebridementAgreementService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(DebridementAgreementService.class);

	/**
	 * 新建清创缝合同意书
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDebridementAgreement(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebridementAgreementModel dam = new DebridementAgreementModel(dao);
		dam.saveDebridementAgreement(jsonReq, jsonRes, dao, ctx);
	}
}
