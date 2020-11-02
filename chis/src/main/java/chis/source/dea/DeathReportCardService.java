package chis.source.dea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.cdh.DebilityChildrenModel;
import chis.source.dic.CancellationReason;
import chis.source.dic.RegisteredPermanent;
import chis.source.mhc.PregnantRecordModel;
import chis.source.mhc.WomanRecordModel;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanModel;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exception.CodedBaseException;

public class DeathReportCardService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(DeathReportCardService.class);
	
	
	/**
	 * 保存死亡报告卡数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDeathReport(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = StringUtils.trimToEmpty((String) reqBody.get("pregnantId"));
		String empiId = StringUtils.trimToEmpty((String) reqBody.get("empiId"));
		String deadReason = StringUtils.trimToEmpty((String) reqBody
				.get("deathDiagnosis1"));
		String cancellationReason = CancellationReason.PASS_AWAY;
		String op = (String) req.get("op");
		String phrId =StringUtils.trimToEmpty((String)reqBody.get("phrId"));
		String deadDate = StringUtils.trimToEmpty((String) reqBody
				.get("deadTime"));
		String cardId = (String) reqBody.get("cardId");
		
		try {
			// **注销儿童档案
			ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
			chModel.logOutHealthCardRecord("empiId", empiId, cancellationReason,
					deadReason);

			// ** 注销体弱儿童档案
			DebilityChildrenModel dcModel = new DebilityChildrenModel(dao);
			dcModel.logOutDebilityChildrenRecord("empiId", empiId,
					cancellationReason, deadReason);
			
			// ** 注销妇女档案
//			WomanRecordModel wrm = new WomanRecordModel(dao);
//			wrm.logOutWomanDemographicInfo(empiId);
//			wrm.logOutWomanRecord("empiId", empiId, cancellationReason, deadReason);

			// ** 注销孕妇档案
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			prm.logOutPregnantRecord("pregnantId", pregnantId, cancellationReason,
					deadReason);
			//注销个人健康档案
			HealthRecordModel hr = new HealthRecordModel(dao);
			hr.logoutHealthRecord(phrId, deadReason, cancellationReason, deadDate, Constants.CODE_CLOSEFLAG_YES);

			// **注销未执行过的随访计划
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			vpModel.logOutVisitPlan(vpModel.EMPIID, empiId);

			// **保存孕妇死亡报告卡
			DeathReportCardModel drcModel = new DeathReportCardModel(dao);
			Map<String, Object> resBody = drcModel.saveDeathReport(reqBody, op);
			if("create".equals(op)){
				cardId = (String) resBody.get("cardId");
			}
			resBody.putAll(reqBody);
			Map<String, Object> m = SchemaUtil.setDictionaryMessageForForm(resBody,BSCHISEntryNames.DEA_DeathReportCard);
			res.put("body", m);

		} catch (ModelDataOperationException e) {
			logger.error("Failed to save prenatal deathReport info.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(DEA_DeathReportCard, op, cardId, dao, empiId);
	}

	/**
	 * 载入最近的一条孕妇数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLatestPregnantRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) body.get("empiId");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		Map<String, Object> resBody = null;
		try {
			List<Map<String, Object>> list = prm
					.getPregnantRecordByEmpiId(empiId);
			if (list != null && list.size() > 0) {
				resBody = list.get(0);
			}
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to load prenatal screening info.", e);
			throw new ServiceException(e);
		}
	}
	public void marshallRecord(Map<String, Object> rec,Schema sc,Context ctx) throws CodedBaseException {
		if (rec == null) {
			return;
		}
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			String fid = it.getId();
			if (it.getProperty("ref") == null || it.getProperty("ref").equals("")) {
				continue;
			}
			if (it.isCodedValue()) {
				Map<String, Object> o = new HashMap<String, Object>();
				Object fv = rec.get(fid);
				o.put("key", fv);
				if (rec.containsKey(fid + "_text")) {
					o.put("text", rec.get(fid + "_text"));
				} else {
					o.put("text", it.toDisplayValue(fv));
				}
				rec.put(fid, o);
			}
			if (it.isVirtual() && it.isEvalValue()) {
				Object ev = it.eval();
				rec.put(fid, ev);
			}
		}
	}
	/**
	 * 初始化孕妇死亡报告卡数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadInitPreDeaRepData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) body.get("empiId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			DeathReportCardModel drcModel = new DeathReportCardModel(dao);
			Map<String, Object> dia = SchemaUtil.setDictionaryMessageForForm(drcModel.loadDeathReportCard(empiId), DEA_DeathReportCard);
			if (dia != null) {
				String schemaId = (String)body.get("schema");
				Schema sc = SchemaController.instance().get(schemaId);
				marshallRecord(dia, sc, ctx);
				resBody.putAll(dia);
				return;
			}

			PregnantRecordModel prm = new PregnantRecordModel(dao);
			Map<String, Object> initData = null;
			List<Map<String, Object>> list = prm
					.getPregnantRecordByEmpiId(empiId);
			if (list != null && list.size() > 0) {
				initData = list.get(0);
				initData.put("gravidityTimes", initData.get("gravidity"));
				int vaginalDelivery = (Integer) initData.get("vaginalDelivery");
				int abdominalDelivery = (Integer) initData
						.get("abdominalDelivery");
				int trafficFlow = (Integer) initData.get("trafficFlow");
				int naturalAbortion = (Integer) initData.get("naturalAbortion");
				int qweTimes = (Integer) initData.get("qweTimes");
				int odinopoeia = (Integer) initData.get("odinopoeia");
				int deliverTimes = vaginalDelivery + abdominalDelivery;
				initData.put("deliverTimes", deliverTimes);
				int abortTimes = trafficFlow + naturalAbortion + qweTimes
						+ odinopoeia;
				initData.put("abortTimes", abortTimes);
				initData = SchemaUtil.getDataListToForm(initData);
				initData.put("permanentRegionCode",
						initData.get("restRegionCode"));
				WomanRecordModel wrModel = new WomanRecordModel(dao);
				Map<String, Object> person = wrModel
						.getWomanBaseInfoByEmpiId(empiId);
				if (person != null) {
					Map<String,Object> base = new HashMap<String, Object>();
					base.put("nationCode", person.get("nationCode"));
					base.put("educationCode", person.get("educationCode"));
					String  register = (String) person.get("registeredPermanent");
					base.put("register",register);
					base.put("registerRegionCode",
							person.get("householdAddress_text"));
					if (RegisteredPermanent.NOREGISTERED.equals(register)) {
						base.put("temporaryRegionCode",
								person.get("liveAddress"));
					}
					initData.putAll(SchemaUtil.setDictionaryMessageForForm(base, DEA_DeathReportCard));
				}
			}
			resBody.put("cardId", null);
			resBody.put("initData", initData);
		} catch (Exception e) {
			logger.error("Failed to load prenatal screening info.", e);
			throw new ServiceException(e);
		}
	}
}
