package chis.source.phr;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import chis.source.PersistentDataOperationException;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.cdh.DebilityChildrenModel;
import chis.source.conf.SystemCofigManageModel;
import chis.source.dc.RabiesRecordModel;
import chis.source.def.DefLimbModel;
import chis.source.idr.IdrReportModel;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.ohr.OldPeopleRecordService;
import chis.source.psy.PsychosisRecordModel;
import chis.source.pub.PublicService;
import chis.source.rvc.RetiredVeteranCadresModel;
import chis.source.sch.SchistospmaModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.tr.TumourConfirmedModel;
import chis.source.tr.TumourHighRiskModel;
import chis.source.tr.TumourPatientReportCardModel;
import chis.source.tr.TumourQuestionnaireModel;
import chis.source.tr.TumourScreeningModel;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanModel;

import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

/**
 * @description 个人信息
 * 
 * @author <a href="mailto:zhouw@bsoft.com.cn">zhouw</a>
 */
public class BasicPersonalInformationService extends AbstractActionService
		implements DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(BasicPersonalInformationService.class);

	// private static final BaseDAO dao=new BaseDAO(null);

	/**
	 * 保存个人相关信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveBasicPersonalInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> body_map = new HashMap<String, Object>();
		String op = (String) req.get("op");
		//传日志到大数据接口 （健康档案管理任务提醒）--wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String curDate1= sdf.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = (String) ctx.get(Context.CLIENT_IP_ADDRESS);	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"JKDAGLRWTX\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.JKDAGLRWTX\",\n"+
			"\"methodDesc\":\"void doSaveBasicPersonalInformation()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(dao, ctx);
		try {
			// 对信息分类
			Map<String, Object> bodyMap = splitInfoBySchema(body);
			bodyMap.put("flagFamilyId", body.get("flagFamilyId"));
			String empi = "";
			String fl = model.loadInfoByEmpiId((String) body.get("empiId"));
			if (fl != null && fl.trim().length() > 0) {
				empi = (String) body.get("empiId");
				op = "update";// 说明身份证有登记过,防止用户新建时，连续点击保存按钮
			} else {
				op = "create";// 说明身份证没有登记过，查不到信息
			}
			// 从家庭档案那边传过来的数据
			Context c = new Context();
			c.put("regionCode", body.get("regionCode"));
			ctx.put("codeCtx", c);
			String empiId = (String) body.get("empiId");
			if (empiId != null && empiId.length() > 0) {
				op = "update";
			}
			if ("create".equals(op)) {
				empiId = model.saveDemographicInfo(op, bodyMap);
				if (empiId == null) {
					res.put(RES_CODE, Constants.CODE_RECORD_NOT_FOUND);
					res.put(RES_MESSAGE, "保存個人信息失敗！");
					logger.error("保存個人信息失敗！");
					return;
				}
				bodyMap.put("empiId", empiId);
				bodyMap.put("status", "0");
				// 保存个人健康档案
				Map<String, Object> mm = model.saveHealthtRecord(op, bodyMap);
				// 保存家庭档案
				if(mm != null && mm.size() > 0){
					bodyMap.put("phrId", mm.get("phrId"));
				}
				Map<String, Object> mm1 = model.saveFamilyRecord(op, bodyMap);
				// 保存药物过敏史
				model.savePastHistoryYWGMS(op, bodyMap);
				// 保存暴露史
				model.savePastHistoryBLS(op, bodyMap);
				// 保存疾病史
				model.savePastHistoryJBS(op, bodyMap);
				// 保存手术、外伤、输血史
				model.savePastHistorySSandWSandSX(op, bodyMap);
				// 保存遗传病、残疾
				model.savePastHistoryYCBandCJ(op, bodyMap);
				// 保存家族史
				model.savePastHistoryJZS(op, bodyMap);
				if (mm != null && mm.size() > 0) {
					body_map.put("phrId", (String) mm.get("phrId"));
				}
				if (mm1 != null && mm1.size() > 0) {
					body_map.put("middleId", (String) mm1.get("middleId"));
				}
				body_map.put("empiId", empiId);
				body_map.put("regionCode", body.get("regionCode"));
				body_map.put("manaUnitId", body.get("manaUnitId"));
				body_map = SchemaUtil.setDictionaryMessageForForm(body_map, EHR_HealthRecord_JBXX);
				 
				Map<String, Object> jbxx =(Map<String, Object>)bodyMap.get("jbxx");
				String S_birthday=jbxx.get("birthday").toString();
				if (BSCHISUtil.calculateAge(BSCHISUtil.toDate(S_birthday),new Date())>=65){
					OldPeopleRecordModel oldmodel =new OldPeopleRecordModel(dao);
					Map<String, Object> oldrecord=new HashMap<String, Object>() ;
					oldrecord=oldmodel.getOldPeopleRecordByEmpiIdnostatus(empiId);
					if(oldrecord==null){
						Map<String, Object> oldrecordmsg = new HashMap<String, Object>();
						oldrecordmsg.put("phrId", (String) body_map.get("phrId"));
						oldrecordmsg.put("empiId", (String) body_map.get("empiId"));
						oldrecordmsg.put("manaDoctorId", (String) jbxx.get("manaDoctorId"));
						oldrecordmsg.put("manaUnitId", (String) jbxx.get("manaUnitId"));
						oldrecordmsg.put("status", "0");
						oldrecordmsg.put("createDate", new Date());
						oldrecordmsg.put("createUser", (String) jbxx.get("manaDoctorId"));
						oldrecordmsg.put("createUnit", (String) jbxx.get("manaUnitId"));
						oldmodel.saveOldPeopleRecord("create",oldrecordmsg,false);
					}
				}
				res.put("body", body_map);

			} else {
				Map<String, Object> map_jbxx = (Map<String, Object>) bodyMap
						.get("jbxx");
				body_map.putAll(map_jbxx);
				if (empi == null || empi.trim().length() == 0) {
					empi = (String) map_jbxx.get("empiId");
				}
				map_jbxx.put("empiId", empi);
				bodyMap.put("empiId", empi);
				// 对于个人既往史(家庭健康档案:不能删除)，修改的时候，先删除原来的信息，然后添加现在的信息
				// 删除个人既往史EHR_PASTHISTORY
				dao.doRemove("empiId", empi, BSCHISEntryNames.EHR_PastHistory);
				// 个人基本信息
				model.updateDemographicInfo(op, bodyMap);
				// 更新家庭健康档案
				String familyId = (String) bodyMap.get("familyId");
				if(S.isEmpty(familyId)){
					bodyMap.put("familyId", body.get("familyId"));
				}
				Map<String, Object> mess = model.saveFamilyRecord(op, bodyMap);
				if (mess.get("message") != null
						&& "该家庭已经有户主!".equals((String) mess.get("message"))) {
					res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
					res.put(Service.RES_MESSAGE, "该家庭已经有户主！");
					return;
				}
				if (mess != null && mess.size() > 0&&mess.get("middleId")!=null) {
					body_map.put("middleId", (String) mess.get("middleId"));
					body_map.put("familyId", (String) mess.get("familyId"));
					map_jbxx.put("familyId", (String) mess.get("familyId"));
				}
				// 更新个人健康档案
				String phrId = (String) map_jbxx.get("phrId");
				if (phrId != null && phrId.length() > 0) {
					model.saveHealthtRecord(op, bodyMap);
				} else {
					mess=model.saveHealthtRecord("create", bodyMap);
				}
				if (mess != null && mess.size() > 0&&mess.get("phrId")!=null) {
					body_map.put("phrId", (String) mess.get("phrId"));
				}
				String middleId = (String) map_jbxx.get("middleId");
				Map<String, Object> shhj = (HashMap<String, Object>) bodyMap.get("shhj");
				shhj.put("empiId", (String) map_jbxx.get("empiId"));
				if (middleId != null && middleId.length() > 0) {
					model.updateFamilyMiddle(shhj);
				}
				// 保存药物过敏史
				model.savePastHistoryYWGMS(op, bodyMap);
				// 保存暴露史
				model.savePastHistoryBLS(op, bodyMap);
				// 保存疾病史
				model.savePastHistoryJBS(op, bodyMap);
				// 保存手术、外伤、输血史
				model.savePastHistorySSandWSandSX(op, bodyMap);
				// 保存遗传病、残疾
				model.savePastHistoryYCBandCJ(op, bodyMap);
				// 保存家族史
				model.savePastHistoryJZS(op, bodyMap);
				body_map = SchemaUtil.setDictionaryMessageForForm(
						body_map, EHR_HealthRecord_JBXX);
				String S_birthday=body_map.get("birthday").toString();
				if (BSCHISUtil.calculateAge(BSCHISUtil.toDate(S_birthday),new Date())>=65){
					OldPeopleRecordModel oldmodel =new OldPeopleRecordModel(dao);
					Map<String, Object> oldrecord=new HashMap<String, Object>() ;
					oldrecord=oldmodel.getOldPeopleRecordByEmpiIdnostatus(empiId);
					if(oldrecord==null){
						Map<String, Object> oldrecordmsg = new HashMap<String, Object>();
						oldrecordmsg.put("phrId", (String) map_jbxx.get("phrId"));
						oldrecordmsg.put("empiId", (String) map_jbxx.get("empiId"));
						oldrecordmsg.put("manaDoctorId", (String) map_jbxx.get("manaDoctorId"));
						oldrecordmsg.put("manaUnitId", (String) map_jbxx.get("manaUnitId"));
						oldrecordmsg.put("status", "0");
						oldrecordmsg.put("createDate", new Date());
						oldrecordmsg.put("createUser", (String) map_jbxx.get("manaDoctorId"));
						oldrecordmsg.put("createUnit", (String) map_jbxx.get("manaUnitId"));
						oldmodel.saveOldPeopleRecord("create",oldrecordmsg,false);
					}
				}
				}
				res.put("body", body_map);
			vLogService.saveRecords("GRDA","create",dao,empiId);
			//打签约项目完成标识
			this.finishSCServiceTask(empiId, JMJKDA_GLFW, null, dao);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	private Map<String, Object> splitInfoBySchema(Map<String, Object> body) {
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(EHR_HealthRecord_JBXX);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		Map<String, Object> jbxx = new HashMap<String, Object>();
		Map<String, Object> ywgms = new HashMap<String, Object>();
		Map<String, Object> bls = new HashMap<String, Object>();
		Map<String, Object> jbs = new HashMap<String, Object>();
		Map<String, Object> ss = new HashMap<String, Object>();
		Map<String, Object> ws = new HashMap<String, Object>();
		Map<String, Object> sx = new HashMap<String, Object>();
		Map<String, Object> jzjbs = new HashMap<String, Object>();
		Map<String, Object> ycbs = new HashMap<String, Object>();
		Map<String, Object> cj = new HashMap<String, Object>();
		Map<String, Object> shhj = new HashMap<String, Object>();
		jbxx.put("cards", body.get("cards"));
		List<SchemaItem> itemList = sc.getItems();
		for (SchemaItem si : itemList) {
			String type = (String) si.getProperty("lb");
			String key = si.getId();
			if (body.get(key) == null||"null".equals(body.get(key))) {
				continue;
			}
			Object value = body.get(key);
			if ("jbxx".equals(type)) {
				jbxx.put(key, value);
			} else if ("ywgms".equals(type)) {
				ywgms.put(key, value);
			} else if ("bls".equals(type)) {
				bls.put(key, value);
			} else if ("jbs".equals(type)) {
				jbs.put(key, value);
			} else if ("ss".equals(type)) {
				ss.put(key, value);
			} else if ("ws".equals(type)) {
				ws.put(key, value);
			} else if ("sx".equals(type)) {
				sx.put(key, value);
			} else if ("jzjbs".equals(type)) {
				jzjbs.put(key, value);
			} else if ("ycbs".equals(type)) {
				ycbs.put(key, value);
			} else if ("cj".equals(type)) {
				cj.put(key, value);
			} else if ("shhj".equals(type)) {
				shhj.put(key, value);
			}
		}
		bodyMap.put("jbxx", jbxx);
		bodyMap.put("ywgms", ywgms);
		bodyMap.put("bls", bls);
		bodyMap.put("jbs", jbs);
		bodyMap.put("ss", ss);
		bodyMap.put("ws", ws);
		bodyMap.put("sx", sx);
		bodyMap.put("jzjbs", jzjbs);
		bodyMap.put("ycbs", ycbs);
		bodyMap.put("cj", cj);
		bodyMap.put("shhj", shhj);
		return bodyMap;
	}

	/**
	 * 查询个人相关信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadBasicPersonalInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException, ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		Map<String, Object> body_map = null;
		try {
			if ((String) body.get("fag_regionCode") != null
					&& ((String) body.get("fag_regionCode")).length() > 0) {
				body_map = model.LoadDoctorAndFamily(body);
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						body_map, EHR_HealthRecord_JBXX));
			} else {
				body_map = model.LoadDemographicInfo(body);
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						body_map, EHR_HealthRecord_JBXX));
			}
		} catch (ModelDataOperationException e) {
			logger.error("查询個人信息失敗！");
			throw new ServiceException(e);
		}

	}

	/**
	 * 根据医生的id查询医生的名称以及获取用的empiId
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetDoctorById(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException, ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		Map<String, Object> body_map = null;
		String empiId = null;
		String familyId = null;
		body_map = model.getDoctorByUserId((String) body.get("manaDoctorId"));
		empiId = (String) body.get("empiId");
		familyId = model.GetFamilyIdByEmpiId(empiId);
		// body= SchemaUtil.setDictionaryMessageForForm(infoList, schemaName)
		body_map.put("empiId", empiId);
		body_map.put("familyId", familyId);
		res.put("body", body_map);

	}

	/**
	 * 根据empiId 查出家庭档案的id并修改个人家庭健康档案的status
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doUpdateStatus(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		String familyId = "";
		familyId = model.GetFamilyIdByEmpiId((String) body.get("empiId"));

		if (familyId != null && familyId.length() > 0) {
			model.UpdateStatusByFamilyId(familyId);
		}
	}

	/**
	 * 删除家庭成员 解除家庭签约和服务记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveFamily(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException,
			ParseException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");

		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		String phrId = (String) body.get("phrId");
		String familyId = "";
		int flage = 0;
		int flage1 = 0;
		familyId = model.GetFamilyIdByPhrId(phrId);
		// 如果删除的户主本人的时候
		flage1 = model.isOwnerByPhrIdAndStatus(phrId);
		if (flage1 == 1) {
			model.removeFamilyByid(phrId);// 删除家庭成员以及改变签约的状态
			model.removeMasterplateData(phrId);// 解除服务记录的签约状态
			// model.updateHealthByFamilyId(familyId);// 移除所有家庭成员
			model.updateHealthByPhrId(phrId);// 修改原户主的个人健康档案的是否户主的字段
			body.put("ownerName", "");
			Map<String, Object> body1 = new HashMap<String, Object>();
			body1.put("familyId", familyId);
			model.updateOwnerNameById((String) body1.get("familyId"));// 修改家庭档案的户主名字为空
			// 向前台传个标识
			body.clear();
			body.put("flage", "1");
			body.put("familyId", familyId);
			res.put("body", body);
		} else {
			// 每次删除后，在判断是否还有成员，如果没有，则情况家庭档案的户主字段

			model.removeFamilyByid(phrId);// 删除家庭成员以及改变签约的状态
			model.removeMasterplateData(phrId);// 解除服务记录的签约状态

		}

	}

	/**
	 * 修改家庭的状态,当没有家庭成员的时候，状态改为1，
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doRemoveFamilyInfo(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		String code = "";
		try {
			code = model.updateFamilyStatus((String) body.get("familyId"));
			if ("n".equals(code)) {
				res.put(Service.RES_CODE, Constants.CODE_RECORD_EXSIT);
				res.put(Service.RES_MESSAGE, "该家庭有成员，不能删除！");
				return;
			}

		} catch (ModelDataOperationException e) {
			logger.error("doRemove is fail");
			throw new ServiceException(e);
		}

	}

	/**
	 * 注销个人健康档案信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doIsZx(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);

		model.CancelHealthByempiId(body);

	}

	/**
	 * 获取户主的名字
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetOwnerName(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String s = "";
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		s = (String) body.get("familyId");
		if (s != null && s.length() > 0) {
			s = model.getOwnerName1(s);
			body.clear();
			if (s != null && s.length() > 0) {
				// 说明有户主
				body.put("flag", true);
				res.put("body", body);
				return;
			} else {

				body.put("flag", false);
				res.put("body", body);
				return;

			}

		}

	}

	/**
	 * 当全网格化的时候 获取户主的名字，根据网格地址
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetOwnerNameByRegionCode(Map<String, Object> req,

	Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> body1 = new HashMap<String, Object>();
		String s = "";
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		s = model.getOwnerNameByRegionCode(body);
		if (s != null && s.length() > 0) {
			body1.put("ownerName", s);
			body1.put("flag", "1");
		} else {
			body1.put("flag", "0");
		}
		res.put("body", body1);
	}

	/**
	 * 设置参数，供phis调用
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @return
	 * @throws Exception
	 * 
	 */

	protected void doGetSystemInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {

			String areaGridShowType = scmm
					.getSystemConfigData("areaGridShowType");
			body.put("areaGridShowType", areaGridShowType);
			res.put("body", body);

		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断是否存在同类型的相同的卡号
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @return
	 * @throws Exception
	 * 
	 */

	protected void doSelectCardNo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> body1 = new HashMap<String, Object>();
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);

		int t = model.selectCardNo(body, (String) body.get("cardTypeCode"));
		if (t == 1) {// 表示已经存在记录
			body1.put("flag", "1");
			res.put("body", body1);
			return;

		}
		// model.saveCard(body,body.get("cardTypeCode")+"");

	}

	// 根据empiId查询卡号
	public void doQueryPersonInfoByEmpiId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		Map<String, Object> body1 = new HashMap<String, Object>();
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		try {
			Map<String, Object> map = model.queryPersonInfoByHealthNo(empiId);
			if (map != null && map.size() > 0) {
				body1.put("flag", "1");
				res.put("body", body1);
			} else {

				body1.put("flag", "0");
				res.put("body", body1);
			}

		} catch (ModelDataOperationException e) {

			throw new ServiceException(e);
		}
	}

	/**
	 * 注销个人健康档案和相关子档
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutAllRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = StringUtils.trimToEmpty((String) body.get("phrId"));
		String empiId = StringUtils.trimToEmpty((String) body.get("empiId"));
		Map<String, Object> body1 = (Map<String, Object>) body.get("jbxx");
		String deadReason = StringUtils.trimToEmpty((String) body1
				.get("deadReason"));
		// String cancellationReason = StringUtils.trimToEmpty((String) body1
		// .get("cancellationReason"));
		String cancellationReason = "1";
		String deadDate = StringUtils.trimToEmpty((String) body1
				.get("deadDate"));
		String deadFlag = StringUtils.trimToEmpty((String) body1
				.get("deadFlag"));
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			// **注销个人健康档案
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			hrModel.logoutHealthRecord(phrId, deadReason, cancellationReason,
					deadDate, deadFlag);
			vLogService.saveVindicateLog(EHR_HealthRecord, "3", phrId, dao,
					empiId);

			// ** 注销传染病档案
			IdrReportModel idrm = new IdrReportModel(dao);
			idrm.logOutIdrReport("empiId", empiId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(IDR_Report, "3", phrId, dao, empiId);

			// **注销老年人档案
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			oprModel.logoutOldPeopleRecord(phrId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(MDC_OldPeopleRecord, "3", phrId, dao,
					empiId);

			// **注销高血压档案
			HypertensionModel hypModel = new HypertensionModel(dao);
			hypModel.logoutHypertensionRecord(phrId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(MDC_HypertensionRecord, "3", phrId,
					dao, empiId);

			// **注销糖尿病档案
			DiabetesRecordModel drModel = new DiabetesRecordModel(dao);
			drModel.logoutDiabetesRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(MDC_DiabetesRecord, "3", phrId, dao,
					empiId);

			// **注销儿童档案
			ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
			chModel.logOutHealthCardRecord("phrId", phrId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(CDH_HealthCard, "3", phrId, dao,
					empiId);

			// ** 注销体弱儿童档案
			DebilityChildrenModel dcModel = new DebilityChildrenModel(dao);
			dcModel.logOutDebilityChildrenRecord("empiId", empiId,
					cancellationReason, deadReason);
			vLogService.saveVindicateLog(CDH_DebilityChildren, "3", phrId, dao,
					empiId);

			// ** 注销孕妇档案
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			prm.logOutPregnantRecord("empiId", empiId, cancellationReason,
					deadReason);
			vLogService.saveVindicateLog(MHC_PregnantRecord, "3", phrId, dao,
					empiId);

			// ** 注销精神病档案
			PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
			prModel.logoutPsychosisRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(PSY_PsychosisRecord, "3", phrId, dao,
					empiId);

			// ** 注销血吸虫病档案
			SchistospmaModel sm = new SchistospmaModel(dao);
			sm.logoutSchistospmaRecord(phrId, cancellationReason, deadReason,
					true);
			vLogService.saveVindicateLog(SCH_SchistospmaRecord, "3", phrId,
					dao, empiId);

			// ** 注销狂犬病档案
			RabiesRecordModel rrm = new RabiesRecordModel(dao);
			rrm.logoutRabiesRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(DC_RabiesRecord, "3", phrId, dao,
					empiId);

			// ** 注销残疾人档案
			DefLimbModel dlm = new DefLimbModel(dao);
			dlm.logoutDeformityRecord(body, cancellationReason, deadReason,
					DEF_LimbDeformityRecord);
			vLogService.saveVindicateLog(DEF_LimbDeformityRecord, "3", phrId,
					dao, empiId);
			dlm.logoutDeformityRecord(body, cancellationReason, deadReason,
					DEF_BrainDeformityRecord);
			vLogService.saveVindicateLog(DEF_BrainDeformityRecord, "3", phrId,
					dao, empiId);
			dlm.logoutDeformityRecord(body, cancellationReason, deadReason,
					DEF_IntellectDeformityRecord);
			vLogService.saveVindicateLog(DEF_IntellectDeformityRecord, "3",
					phrId, dao, empiId);

			// ** 注销离休干部档案
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			rvcModule.logoutRVCRecord(phrId, cancellationReason, deadReason);
			vLogService.saveVindicateLog(RVC_RetiredVeteranCadresRecord, "3",
					phrId, dao, empiId);

			// ** 注销肿瘤高危档案(该人员下全部记录[所有类别])
			TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
			thrModel.logoutAllTHR(empiId, cancellationReason);
			vLogService.saveVindicateLog(MDC_TumourHighRisk, "3", phrId, dao,
					empiId);
			// ** 注销该人员的全部肿瘤确诊记录
			TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
			tcModel.logoutAllTC(empiId, cancellationReason);
			vLogService.saveVindicateLog(MDC_TumourConfirmed, "3", phrId, dao,
					empiId);
			// ** 注销该人员的全部肿瘤现患报卡
			TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
					dao);
			tprcModel.logoutAllTPRCRecord(empiId, cancellationReason, deadFlag,
					deadDate);
			vLogService.saveVindicateLog(MDC_TumourPatientReportCard, "3",
					phrId, dao, empiId);
			// ** 迁出或死亡 注销该人员的全部肿瘤初筛
			TumourScreeningModel tsModel = new TumourScreeningModel(dao);
			tsModel.logoutMyAllTSR(empiId, cancellationReason);
			// ** 迁出或死亡 注销该人员的全部肿瘤问卷
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			tqModel.logoutMyAllTHQ(empiId, cancellationReason);

			// **注销未执行过的随访计划
			vpModel.logOutVisitPlan(vpModel.EMPIID, empiId);

		} catch (ModelDataOperationException e) {
			logger.error("logout healthRecord failed");
			throw new ServiceException(e);
		}
	}

	//
	public void doCleanDieInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		Map<String, Object> body1 = new HashMap<String, Object>();
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		body1.put("deadFlag", "");
		body1.put("deadDate", null);
		body1.put("deadReason", "");
		body1.put("phrId", phrId);
		model.updateDieInfo(body1);

	}

	public void doGetInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		@SuppressWarnings("unchecked")
		Map<String, Object> map_jbxx = (Map<String, Object>) body.get("jbxx");
		Map<String, Object> m = new HashMap<String, Object>();
		String empiId = "";
		if(map_jbxx != null && map_jbxx.size() > 0){
			empiId = (String) map_jbxx.get("empiId");
		}
		if(S.isEmpty(empiId)){
			empiId = (String) body.get("empiId");
		}
		if(S.isEmpty(empiId)){
			return;
		}
		m.put("empiId", empiId);
		BasicPersonalInformationModel model = new BasicPersonalInformationModel(
				dao, ctx);
		String familyId = model.GetFamilyIdByEmpiId(empiId);
		String masterFlag = model.getValueByParameters(EHR_HealthRecord,
				"masterFlag", m);
		if (familyId != null && familyId.length() > 0) {
			if ("n".equals(masterFlag)) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "该人员已经有家庭档案!");
				String hzsql="select ownerName as ownerName,familyAddr as familyAddr from EHR_FamilyRecord where familyId=:familyId";
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("familyId", familyId);
				Map<String, Object> hzmap=dao.doSqlLoad(hzsql, p);
				if(hzmap!=null && hzmap.size() >0){
					res.put("ownerName", hzmap.get("OWNERNAME")+"");
					res.put("familyAddr", hzmap.get("FAMILYADDR")+"");
				}
				return;
			}
			
		}

	}
	public void doChangeIdCard(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			ModelDataOperationException, PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId=body.get("empiId")+"";
		String idCard=body.get("idCard")+"";
		String sql="select count(a.empiid) as COUNT from mpi_demographicinfo a" +
				" where a.idCard=:idCard and a.empiId<>:empiId";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("empiId",empiId);
		p.put("idCard",idCard);
		Map<String, Object> map=dao.doSqlLoad(sql,p);
		if(map!=null && Integer.parseInt(map.get("COUNT")+"")>0){
			res.put("code","400");
			res.put("msg","输入的身份证号已被其他档案占用！");
		}else{
			String cerupsql="update MPI_Certificate set certificateNo=:idCard" +
					" where empiId=:empiId and certificateTypeCode='01'";
			dao.doUpdate(cerupsql, p);
			String mpiupsql="update MPI_DemographicInfo set idCard=:idCard" +
					"  where empiId=:empiId";
			dao.doUpdate(mpiupsql, p);
		}
	}
}
