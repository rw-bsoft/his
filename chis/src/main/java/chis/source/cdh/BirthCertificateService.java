/**
 * @(#)BirthCertificateService.java Created on 2012-2-7 下午4:36:19
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.RecordStatus;
import chis.source.empi.EmpiModel;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class BirthCertificateService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(BirthCertificateService.class);

	/**
	 * 是否需要创建出生证明
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doWhetherNeedBirthCertificate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String empiId = StringUtils.trimToEmpty((String) reqBody.get("empiId"));
		Map<String, Object> resBody = (HashMap<String, Object>) res.get("body");
		boolean cdhHealthCardExists;
		String phrid;
		try {
			EmpiModel em = new EmpiModel(dao);
			cdhHealthCardExists = em.isRecordExists(CDH_HealthCard, empiId,
					true);
			HealthRecordModel hrm = new HealthRecordModel(dao);
			phrid = hrm.getPhrId(empiId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}
		if (phrid != null && !phrid.equals("")) {
			resBody.put("phrid", phrid);
		} else {
			resBody.put("message", "健康档案号不存在！");
		}
		if (!cdhHealthCardExists) {
			resBody.put("cdhHealthCardExists", 0);
			resBody.put("message", "儿童档案不存在！");
		}
		try {
			ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
			Map<String, Object> map = cbm.getChildInfoByEmpiId(empiId);
			if (map != null && map.size() > 0) {
				resBody.put("exists", 1);
				resBody.put("message", "出生证明已经存在！");
			}
		} catch (ModelDataOperationException e1) {
			logger.error("no Persistent:DataOperation.", e1);
			res.put(RES_MESSAGE, "没有权限。");
			throw new ServiceException(e1);
		}
	}

	/**
	 * 检查出生证编号是否重复
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckCNo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String certificateNo = StringUtils.trimToEmpty((String) body
				.get("certificateNo"));
		String empiId = StringUtils.trimToEmpty((String) body.get("empiId"));
		boolean isRepeat;
		try {
			ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
			isRepeat = cbm.checkCertificateNo(empiId, certificateNo);
		} catch (ModelDataOperationException e) {
			logger.error("check  child certificateNo if exisit failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> rebody = new HashMap<String, Object>();
		rebody.put("isRepeat", isRepeat);
		res.put("body", rebody);
	}

	/**
	 * 出生证明保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveBirthCertificate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ValidateException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String recordId = (String) body.get("recordId");
		String op = (String) req.get("op");
		try {
			BirthCertificateModel bcm = new BirthCertificateModel(dao);
			bcm.saveChildInfo(body, op, res);
			String certificateNo = (String) body.get("certificateNo");
			bcm.updateCertificateNo(empiId, certificateNo);
		} catch (ModelDataOperationException e) {
			logger.error("save child relative record message.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBody = (Map<String, Object>) res.get("body");
		if("create".equals(op) && resBody != null){
			recordId = (String) resBody.get("recordId");
		}
		vLogService.saveVindicateLog(CDH_BirthCertificate, op, recordId, dao, empiId);
	}

	/**
	 * 出生证明数据初始化
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitBirthData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) ((HashMap<String, Object>) req.get("body"))
				.get("empiId");
		BirthCertificateModel bcm = new BirthCertificateModel(dao);
		Map<String, Object> resBody;
		try {
			resBody = bcm.initBirthData(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Init birth certificate error");
			throw new ServiceException(e);
		}
		if (resBody != null) {
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					CDH_BirthCertificate));
		}

	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadBirthCertificate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("fieldValue");
		BirthCertificateModel bcm = new BirthCertificateModel(dao);
		Map<String, Object> BirthData;
		try {
			BirthData = bcm.getBirthCertificateRecordByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get birth certificate record failed.");
			throw new ServiceException(e);
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(BirthData,
				CDH_BirthCertificate));
	}

	/**
	 * 获取儿童出生证明的操作权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckBirthCertificateControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		if (empiId == null || "".equals(empiId)) {
			return;
		}
		try {
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			Map<String, Object> record = chm.getChildHealthCardByEmpiId(empiId);
			Map<String, Object> resBody = new HashMap<String, Object>();
			if (record != null && record.size() > 0) {
				Map<String, Boolean> data = ControlRunner.run(
						CDH_BirthCertificate, record, ctx);
				resBody.put("_actions", data);
			}
			res.put("body", resBody);
		} catch (Exception e) {
			logger.error("check child dead record control error .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取儿童出生证明的删除操作权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckBirthCertificateDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		if (empiId == null || "".equals(empiId)) {
			return;
		}
		try {
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			Map<String, Object> record = chm.getChildHealthCardByEmpiId(empiId);
			Map<String, Object> resBody = new HashMap<String, Object>();
			if (record != null && record.size() > 0) {
				String status = (String) record.get("status");
				if (status.equals(RecordStatus.NOMAL)) {
					resBody.put("canDelete", true);
				} else {
					resBody.put("canDelete", false);
				}
			} else {
				resBody.put("canDelete", true);
			}
			res.put("body", resBody);
		} catch (Exception e) {
			logger.error("check child dead record control error .", e);
			throw new ServiceException(e);
		}
	}

}
