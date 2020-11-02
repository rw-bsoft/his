/**
 * @(#)NormalPopulationService.java Created on 2012-3-30 上午10:58:26
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.nor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.control.ControlRunner;
import chis.source.dc.RabiesRecordModel;
import chis.source.def.DefModel;
import chis.source.dic.RecordStatus;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.phr.HealthRecordModel;
import chis.source.psy.PsychosisRecordModel;
import chis.source.sch.SchistospmaModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class NormalPopulationService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(NormalPopulationService.class);

	/**
	 * 保存非重点人群随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveNormalPopulationVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			NormalPopulationModel npModel = new NormalPopulationModel(dao);
			Map<String, Object> value = npModel
					.saveNormalPopulationVisitRecord(op, body);
			resBody.putAll(SchemaUtil.setDictionaryMessageForForm(body,
					EHR_NormalPopulationVisit));
			if (value != null) {
				resBody.putAll(value);
			}
		} catch (ModelDataOperationException e) {
			logger.error("saving SaveNormalPopulationVisitRecord is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入非重点人群随访表单新建按钮权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doLoadCreateControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		Map<String, Object> control = new HashMap<String, Object>();
		res.put("body", control);
		try {
			HealthRecordModel hrModel = new HealthRecordModel(dao);

			Map<String, Object> r = hrModel.getHealthRecordByEmpiId(empiId);
			Map<String, Boolean> update = ControlRunner.run(
					EHR_NormalPopulationVisit, r, ctx, ControlRunner.CREATE,
					ControlRunner.UPDATE);
			if (update != null && update.get("create") == false) {
				control.put("create", false);
				return;
			}

			if (r == null || !RecordStatus.NOMAL.equals(r.get("status"))) {
				control.put("create", false);
				return;
			}

			// 是否存在糖尿病档案
			DiabetesRecordModel drModel = new DiabetesRecordModel(dao);
			if (drModel.getDiabetesByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}

			// 是否存在高血压档案
			HypertensionModel hyModel = new HypertensionModel(dao);
			if (hyModel.getHypertensionByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}

			// 是否存在老年人档案
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			if (oprModel.getOldPeopleRecordByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}

			// 是否存在儿童档案
			ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
			if (chModel.getNormalHealthCardByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}

			// 是否存在精神病档案
			PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
			if (prModel.getPsychosisRecordByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}

			// 是否存在残疾人档案
			DefModel defModel = new DefModel(dao);
			List<?> defList = defModel.getDefRecordByEmpiId(empiId);
			if (defList != null && defList.size() > 0) {
				control.put("create", false);
				return;
			}
			
			// 是否存在血吸虫病档案
			SchistospmaModel sModel = new SchistospmaModel(dao);
			if (sModel.getSchistospmaRecordByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}
			
			// 是否存在狂犬病档案
			RabiesRecordModel rrm = new RabiesRecordModel(dao);
			if (rrm.getRabiesRecordByEmpiId(empiId) != null) {
				control.put("create", false);
				return;
			}
			
			// 是否存在孕妇档案
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			List<?> prfList = prm.getNormalPregnantRecord(empiId);
			if (prfList != null && prfList.size() > 0) {
				control.put("create", false);
				return;
			}
			
			// 没有专档，放开新建按钮操作权限
			control.put("create", true);

		} catch (ModelDataOperationException e) {
			logger.error("loading NormalPopulationCreateControl unsuccessfully");
			throw new ServiceException(e);
		}
	}
}
