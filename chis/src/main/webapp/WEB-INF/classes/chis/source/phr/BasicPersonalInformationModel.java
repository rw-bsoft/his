package chis.source.phr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiUtil;
import chis.source.fhr.FamilyRecordModule;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description 个人信息
 * 
 * @author <a href="mailto:zhouw@bsoft.com.cn">zhouw</a>
 */
public class BasicPersonalInformationModel implements BSCHISEntryNames {

//	private static final Logger logger = LoggerFactory.getLogger(BasicPersonalInformationModel.class);
	BaseDAO dao = null;
	Context ctx = null;
	public BasicPersonalInformationModel(BaseDAO dao, Context ctx) {
		this.dao = dao;
		this.ctx = ctx;
	}

	/**
	 * 保存个人信息基本资料并返回empIid
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public String saveDemographicInfo(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		Map<String, Object> map_jbxx = (Map<String, Object>) body.get("jbxx");
		Map<String, Object> map_jbxx1 = (Map<String, Object>) body.get("jbxx");
		String empi = null;
		map_jbxx.put("insuranceType", map_jbxx.get("insuranceCode1"));
		map_jbxx.put("insuranceText", map_jbxx.get("insuranceCode1"));
		Map<String, Object> PIXData = EmpiUtil.changeToPIXFormat(map_jbxx);
		map_jbxx = EmpiUtil.submitPerson(dao, ctx, map_jbxx, PIXData);
		empi = (String) map_jbxx.get("empiId");
		map_jbxx1.put("empiId", empi);
		return empi;
	}

	/**
	 * 更新个人信息基本资料并返回empIid
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void updateDemographicInfo(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		Map<String, Object> map_jbxx = (Map<String, Object>) body.get("jbxx");
		Map<String, Object> cardNoInfo1 = new HashMap<String, Object>();
		String empiId = "";

		empiId = (String) body.get("empiId");
		if (empiId != null && empiId.length() > 0) {
			map_jbxx.put("empiId", empiId);
		}
		map_jbxx.put("insuranceType", map_jbxx.get("insuranceCode1"));
		map_jbxx.put("insuranceText", map_jbxx.get("insuranceCode1"));
		Map<String, Object> PIXData = EmpiUtil.changeToPIXFormat(map_jbxx);
		EmpiUtil.updatePerson(dao, ctx, map_jbxx, PIXData, cardNoInfo1);

	}

	//保存个人健康档案
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveHealthtRecord(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ServiceException, PersistentDataOperationException {
		Map<String, Object> mm = null;
		Map<String, Object> map_condit = new HashMap<String, Object>();
		Map<String, Object> map_jbxx = (Map<String, Object>) body.get("jbxx");
		map_condit.put("regionCode", map_jbxx.get("regionCode"));
		map_condit.put("regionCode_text", map_jbxx.get("regionCode_text"));
		map_condit.put("manaDoctorId", map_jbxx.get("manaDoctorId"));
		map_condit.put("manaUnitId", map_jbxx.get("manaUnitId"));
		map_condit.put("empiId", body.get("empiId"));
		map_condit.put("masterFlag", map_jbxx.get("masterFlag"));
		map_condit.put("insuranceCode", map_jbxx.get("insuranceCode"));
		map_condit.put("phrId", map_jbxx.get("phrId"));
		map_condit.put("isAgrRegister", map_jbxx.get("isAgrRegister"));
		map_condit.put("incomeSource", map_jbxx.get("incomeSource"));
		map_condit.put("deadFlag", map_jbxx.get("deadFlag"));
		map_condit.put("deadReason", map_jbxx.get("deadReason"));
		map_condit.put("signFlag", map_jbxx.get("signFlag"));
		map_condit.put("deadDate", map_jbxx.get("deadDate"));
		map_condit.put("status", 0);// 0:正常 1：注销
		map_condit.put("knowFlag", map_jbxx.get("knowFlag"));
		map_condit.put("isPovertyAlleviation", map_jbxx.get("isPovertyAlleviation"));
		String familyId = (String) map_jbxx.get("familyId");
		if (S.isEmpty(familyId)) {
			familyId = (String) body.get("familyId");
		}
		map_condit.put("familyId", familyId);
		String empiIdV = (String) body.get("empiId");
		String phrIdV = "";
		try {
			if ("y".equals(map_jbxx.get("masterFlag"))) {
				map_condit.put("relaCode", "02");
			}
			phrIdV = getPhrIdByempiId(empiIdV);
			if (!"null".equals(phrIdV) && phrIdV.length() > 0) {
				mm = dao.doSave("update", BSCHISEntryNames.EHR_HealthRecord,map_condit, false);
				mm.put("phrId", phrIdV);
			} else {
				map_condit.put("isDiabetes", "n");
				map_condit.put("isHypertension", "n");
				mm = dao.doSave("create", BSCHISEntryNames.EHR_HealthRecord,map_condit, false);
			}
			return mm;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("保存个人健康档案失败！！", e);
		}
	}

	/**
	 * 保存家庭档案（）
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> saveFamilyRecord(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ServiceException, PersistentDataOperationException {
		Map<String, Object> mm = new HashMap<String, Object>();
		Map<String, Object> conditV = new HashMap<String, Object>();
		Map<String, Object> map_condit = (HashMap<String, Object>) body.get("shhj");
		Map<String, Object> map_jbxx = (Map<String, Object>) body.get("jbxx");
		map_condit.put("empiId", map_jbxx.get("empiId"));
		map_condit.put("cookAirTool", map_condit.get("shhjCheckCFPFSS"));
		map_condit.put("fuelType", map_condit.get("shhjCheckRLLX"));
		map_condit.put("waterSourceCode", map_condit.get("shhjCheckYS"));
		map_condit.put("washroom", map_condit.get("shhjCheckCS"));
		map_condit.put("livestockColumn", map_condit.get("shhjCheckQCL"));
		Map<String, Object> map_condit1 = new HashMap<String, Object>();
		map_condit1.putAll(map_condit);
		if ("y".equals(map_jbxx.get("masterFlag"))) {
			map_condit.remove("empiId");
			map_condit.put("ownerName", map_jbxx.get("personName"));
			map_condit.put("regionCode", map_jbxx.get("regionCode"));
			map_condit.put("regionCode_text", map_jbxx.get("regionCode_text"));
			map_condit.put("manaDoctorId", map_jbxx.get("manaDoctorId"));
			map_condit.put("manaUnitId", map_jbxx.get("manaUnitId"));
			map_condit.put("status", 0);// 0:正常 1：注销 位置不能改变
			map_condit.put("masterFlag", map_jbxx.get("masterFlag"));

		}
		String flag = (String) map_jbxx.get("masterFlag");// 是否户主的标志
		String empiId = (String) body.get("empiId");
		String familyIdV = GetFamilyIdByEmpiId(empiId);
		conditV.put("empiId", empiId);
		map_jbxx.put("familyId", familyIdV);
		map_condit1.put("familyId", familyIdV);
		try {
			/************* 添加成员的时候 *******************/
			if ((String) body.get("familyId") != null && ((String) body.get("familyId")).length() > 0
				&& "1".equals(body.get("flagFamilyId"))) {
				Map<String, Object> p = new HashMap<String, Object>();
				familyIdV = (String) body.get("familyId");
				p.put("familyId", familyIdV);
				p.put("empiId", empiId);
				updateFamilyHealthById(p);
			}
			/********************************/
			// 针对家庭档案
			if (familyIdV != null && familyIdV.length() > 0) {// update
				String masterFlagV = this.getValueByParameters(
						EHR_HealthRecord, "masterFlag", conditV);
				// 是户主情况下 下面有两种情况 1.户主变户主，只是改变生活环境或者其他与家庭档案无关的信息
				if ("y".equals(flag) && "y".equals(masterFlagV)) {// 户主变户主
					map_condit.put("familyId", familyIdV);
					dao.doSave("update", BSCHISEntryNames.EHR_FamilyRecord,map_condit, false);
				}
				// 2.户主变非户主
				if ("n".equals(flag) && "y".equals(masterFlagV)) {
					updateMasterFlagByEmpiId(empiId, "n", "");// 更新 个人健康档案,由“是”变“否”
					updateOwnerNameById(familyIdV);// 修改家庭档案的户主名字
					// 更新成员和户主关系
					updateRelaCodeByFamilyId("", familyIdV);
				}
				// 家庭成员：非户主变户主
				if ("y".equals(flag) && "n".equals(masterFlagV)) {
					String ownerNameV = getOwnerName(familyIdV);
					if (S.isNotEmpty(ownerNameV)) {// 查询该家庭档案是否存在户主
						mm.put("message", "该家庭已经有户主!");
						return mm;
					} else {// 是没有户主的家庭成员，变为户主
						updateMasterFlagByEmpiId(empiId, "y", "02");// 更新个人健康档案,由“是”变“否”
						updateOwnerNameById(familyIdV,(String) map_jbxx.get("personName"));// 修改家庭档案的户主名字
					}
				}

			} else {// create
				if ("y".equals(flag)) {// 户主情况下
					String familyId = (String) body.get("familyId");
					String FROP = "create";
					if (S.isNotEmpty(familyId)) {
						Map<String, Object> frMap = getFamilyRecordById(familyId);
						if (frMap != null && frMap.size() > 0) {
							map_condit.put("familyId", familyId);
							FROP = "update";
						}
					}
					Map<String, Object> mmm = dao.doSave(FROP,BSCHISEntryNames.EHR_FamilyRecord, map_condit,false);
					familyIdV = (String) mmm.get("familyId");
					map_jbxx.put("familyId", familyIdV);
					String masterFlagV = this.getValueByParameters(EHR_HealthRecord, "masterFlag", conditV);
					// 是户主情况下 下面有两种情况 1.户主变户主，只是改变生活环境或者其他与家庭档案无关的信息
					if ("y".equals(flag) && "y".equals(masterFlagV)) {// 户主变户主
						map_condit.put("familyId", familyIdV);
						dao.doSave("update", BSCHISEntryNames.EHR_FamilyRecord,map_condit, false);
					}
					// 2.户主变非户主
					if ("n".equals(flag) && "y".equals(masterFlagV)) {
						updateMasterFlagByEmpiId(empiId, "n", "");// 更新个人健康档案,由“是”变“否”
						updateOwnerNameById(familyIdV);// 修改家庭档案的户主名字
						// 更新成员和户主关系
						updateRelaCodeByFamilyId("", familyIdV);
					}
					// 家庭成员：非户主变户主
					if ("y".equals(flag) && "n".equals(masterFlagV)) {
						updateMasterFlagByEmpiId(empiId, "y", "02");// 更新个人健康档案,由“是”变“否”
						updateOwnerNameById(familyIdV,(String) map_jbxx.get("personName"));// 修改家庭档案的户主名字
					}
					// 增加家庭成员
					conditV.put("familyId", familyIdV);
					updateFamilyHealthById(conditV);
				}
			}
			// 增加 家庭成员变动记录
			Map<String, Object> fmrMap = new HashMap<String, Object>();
			fmrMap.put("personName", map_jbxx.get("personName"));
			fmrMap.put("sexCode", map_jbxx.get("sexCode"));
			fmrMap.put("moveType", "2");
			fmrMap.put("phrId", body.get("phrId"));
			fmrMap.put("newFamilyId", map_jbxx.get("familyId"));
			if (familyIdV != null && familyIdV.length() > 0) {
				fmrMap.put("familyId", familyIdV);
			}
			fmrMap.put("targetDoctor", map_jbxx.get("manaDoctorId"));
			fmrMap.put("targetArea", map_jbxx.get("regionCode"));
			fmrMap.put("targetArea_text", map_jbxx.get("regionCode_text"));
			fmrMap.put("targetUnit", map_jbxx.get("manaUnitId"));
			fmrMap.put("applyUnit", UserUtil.get(UserUtil.USER_ID));
			fmrMap.put("applyUser", UserUtil.get(UserUtil.MANAUNIT_ID));
			fmrMap.put("applyDate", Calendar.getInstance().getTime());

			// 针对familyMiddle表（生活环境）
			String middleIdV = getMiddleIdByEmpiId(empiId);
			if (middleIdV != null && middleIdV.length() > 0) {
				map_condit1.put("middleId", middleIdV);
				mm = updateFamilyMiddle(map_condit1);
			} else {
				mm = dao.doSave("create", BSCHISEntryNames.EHR_FamilyMiddle,map_condit1,false);
				FamilyRecordModule frModel = new FamilyRecordModule(dao);
				frModel.saveFMChangeRecord("create", fmrMap, false);
			}
			mm.put("familyId", familyIdV);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("家庭档案保存失败！", e);
		}
		return mm;

	}

	//保存个人既往史（药物过敏）
	@SuppressWarnings("unchecked")
	public void savePastHistoryYWGMS(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		String oldop=op;
		op = "create";
		Map<String, Object> map_all = (Map<String, Object>) body.get("ywgms");
		Map<String, Object> map_condit = new HashMap<String, Object>();
		String gm = (String) map_all.get("diseasetext_check_gm");
		List<String> list = (List<String>) (gm != null ? Arrays.asList(gm
				.toString().split(",")) : new ArrayList<String>());
		if (list==null || "null".equals(list) || list.size()==0 || "".equals(list.get(0))) {
			return;
		}
		for (String o : list) {
			if (!"0101".equals(o)) {
				map_condit.put("pastHisTypeCode", "01");
				map_condit.put("diseaseCode", o);
				map_condit.put("empiId", body.get("empiId"));
				if ("0109".equals(o)) {
					map_condit.put("diseaseText", map_all.get("a_qt1"));
				} else {
					map_condit.put("diseaseText", getText("01", o));
				}
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			} else {
				map_condit.put("pastHisTypeCode", "01");
				map_condit.put("diseaseCode", "0101");
				map_condit.put("diseaseText", "无药物过敏史");
				map_condit.put("empiId", body.get("empiId"));
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit,false);
			}
		}
	}

	public void savePersonProblem(Map<String, Object> map) {
		Map<String, Object> record = new HashMap<String, Object>();
		String diseaseCode = (String) map.get("diseaseCode");
		String diseaseText = (String) map.get("diseaseText");
		if (diseaseCode == null || diseaseCode.endsWith("01")) {
			return;
		}
		UserRoleToken urt = UserRoleToken.getCurrent();
		HealthRecordModel hrm = new HealthRecordModel(dao);
		try {
			record.put("empiId", map.get("empiId"));
			String cycleId = (String) hrm.getLifeCycleId(map).get("cycleId");
			record.put("cycleId", cycleId);
			if (map.get("confirmDate") != null) {
				record.put("occurDate", map.get("confirmDate"));
			} else if (map.get("startDate") != null) {
				record.put("occurDate", map.get("startDate"));
			}
			record.put("problemName", diseaseText);
			record.put("recordUnit", urt.getManageUnitId());
			record.put("recordUser", urt.getUserId());
			record.put("recordDate", new Date());
			record.put("lastModifyUnit", urt.getManageUnitId());
			record.put("lastModifyUser", urt.getUserId());
			record.put("lastModifyDate", new Date());
			dao.doSave("create", BSCHISEntryNames.EHR_PersonProblem,record,false);
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}

	}
	//保存个人既往史（暴露史）
	@SuppressWarnings("unchecked")
	public void savePastHistoryBLS(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		String oldop=op;
		op = "create";
		Map<String, Object> map_all = (Map<String, Object>) body.get("bls");
		Map<String, Object> map_condit = new HashMap<String, Object>();
		List<String> list = (List<String>) (map_all.get("diseasetext_check_bl") != null ? Arrays
				.asList(map_all.get("diseasetext_check_bl").toString().split(",")) : new ArrayList<String>());
		// 01表示被选中
		if (list == null || "null".equals(list) || list.size() == 0 || "".equals(list.get(0))) {
			return;
		}
		for (String o : list) {
			if (!"1201".equals(o)) {
				map_condit.put("pastHisTypeCode", "12");
				map_condit.put("diseaseCode", o);
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("diseaseText", getText("12", o));
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			} else {
				map_condit.put("pastHisTypeCode", "12");
				map_condit.put("diseaseCode", "1201");
				map_condit.put("diseaseText", "无暴露史");
				map_condit.put("empiId", body.get("empiId"));
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
		}
	}

	//保存个人既往史（疾病史）
	@SuppressWarnings("unchecked")
	public void savePastHistoryJBS(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		String oldop=op;
		op = "create";
		Map<String, Object> map_all = (Map<String, Object>) body.get("jbs");
		Map<String, Object> map_condit = new HashMap<String, Object>();
		List<String> list = (List<String>) (map_all.get("diseasetext_check_jb") != null ? Arrays
				.asList(map_all.get("diseasetext_check_jb").toString().split(",")) : new ArrayList<String>());
		if ("02".equals((String) map_all.get("diseasetext_radio_jb"))) {// 01表示被选中
			for (String o : list) {
				if ("".equals(o)) {
					continue;
				}
				int y = Integer.parseInt(o.substring(1, o.length()));
				switch (y) {
				case 202:
					map_condit.put("confirmDate",map_all.get("confirmdate_gxy"));
					map_condit.put("diseaseText",getText("02", o));
					break;
				case 203:
					map_condit.put("confirmDate",map_all.get("confirmdate_tnb"));
					map_condit.put("diseaseText",getText("02", o));
					break;
				case 204:
					map_condit.put("confirmDate",map_all.get("confirmdate_gxb"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 205:
					map_condit.put("confirmDate",map_all.get("confirmdate_mxzsxfjb"));
					map_condit.put("diseaseText",getText("02", o));
					break;
				case 206:
					map_condit.put("confirmDate",map_all.get("confirmdate_exzl"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 207:
					map_condit.put("confirmDate",map_all.get("confirmdate_nzz"));
					map_condit.put("diseaseText",getText("02", o));
					break;
				case 208:
					map_condit.put("confirmDate",map_all.get("confirmdate_zxjsjb"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 209:
					map_condit.put("confirmDate",map_all.get("confirmdate_jhb"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 210:
					map_condit.put("confirmDate",map_all.get("confirmdate_gzjb"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 211:
					map_condit.put("confirmDate",map_all.get("confirmdate_xtjx"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 212:
					map_condit.put("confirmDate",map_all.get("confirmdate_zyb"));
					map_condit.put("diseaseText",map_all.get("diseasetext_zyb"));
					break;
				case 213:
					map_condit.put("confirmDate",map_all.get("confirmdate_gzjb"));
					map_condit.put("diseaseText",getText("02", o));
					break;
				case 214:
					map_condit.put("confirmDate", map_all.get("confirmdate_px"));
					map_condit.put("diseaseText", getText("02", o));
					break;
				case 298:
					map_condit.put("confirmDate",map_all.get("confirmdate_qtfdcrb"));
					map_condit.put("diseaseText",map_all.get("diseasetext_qtfdcrb"));
					break;
				case 299:
					map_condit.put("confirmDate", map_all.get("confirmdate_qt"));
					map_condit.put("diseaseText", map_all.get("diseasetext_qt"));
					break;

				}
				map_condit.put("pastHisTypeCode", "02");
				map_condit.put("diseaseCode", o);
				map_condit.put("empiId", body.get("empiId"));
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit,false);
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				map_condit.clear();
			}
		}
		map_condit.clear();
		// 未选中
		if ("0201".equals((String) map_all.get("diseasetext_radio_jb"))) {
			map_condit.put("pastHisTypeCode", "02");
			map_condit.put("diseaseCode", "0201");
			map_condit.put("diseaseText", "无疾病史");
			map_condit.put("empiId", body.get("empiId"));
			dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit, false);
			if(oldop.equals("create")){
				savePersonProblem(map_condit);
			}
		}
	}

	//保存个人既往史（手术、外伤、输血的类别）
	@SuppressWarnings("unchecked")
	public void savePastHistorySSandWSandSX(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		String oldop=op;
		op = "create";
		Map<String, Object> map_ss = (Map<String, Object>) body.get("ss");// 手术
		Map<String, Object> map_ws = (Map<String, Object>) body.get("ws");// 外伤
		Map<String, Object> map_sx = (Map<String, Object>) body.get("sx");// 输血
		Map<String, Object> map_condit = new HashMap<String, Object>();
		// 有手术史
		if ("0302".equals((String) map_ss.get("diseasetext_ss"))) {
			if ((String) map_ss.get("diseasetext_ss0") != null
					&& ((String) map_ss.get("diseasetext_ss0")).length() > 0) {
				map_condit.put("diseaseText", map_ss.get("diseasetext_ss0"));// 手术的描述
				map_condit.put("startDate", map_ss.get("startdate_ss0"));// 手术的时间
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "03");
				map_condit.put("diseaseCode", "0302");
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
			map_condit.clear();
			if ((String) map_ss.get("diseasetext_ss1")!=null && ((String) map_ss.get("diseasetext_ss1")).length()>0) {
				map_condit.put("diseaseText", map_ss.get("diseasetext_ss1"));// 手术的描述
				map_condit.put("startDate", map_ss.get("startdate_ss1"));// 手术的时间
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "03");
				map_condit.put("diseaseCode", "0302");
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
		}
		map_condit.clear();
		// 无手术史
		if ("0301".equals((String) map_ss.get("diseasetext_ss"))) {
			map_condit.put("pastHisTypeCode", "03");
			map_condit.put("diseaseCode", "0301");
			map_condit.put("diseaseText", "无手术史");
			map_condit.put("empiId", body.get("empiId"));
			dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit, false);
		}

		// 有外伤
		map_condit.clear();
		if ("0602".equals((String) map_ws.get("diseasetext_ws"))) {
			if ((String) map_ws.get("diseasetext_ws0")!=null && ((String) map_ws.get("diseasetext_ws0")).length()>0) {
				map_condit.put("diseaseText", map_ws.get("diseasetext_ws0"));// 外伤的描述
				map_condit.put("startDate", map_ws.get("startdate_ws0"));// 外伤的时间
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "06");
				map_condit.put("diseaseCode", "0602");
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
			map_condit.clear();
			if ((String) map_ws.get("diseasetext_ws1")!=null && ((String) map_ws.get("diseasetext_ws1")).length()>0) {
				map_condit.put("diseaseText", map_ws.get("diseasetext_ws1"));// 外伤的描述
				map_condit.put("startDate", map_ws.get("startdate_ws1"));// 外伤的时间
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "06");
				map_condit.put("diseaseCode", "0602");
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit,false);
			}
		}
		map_condit.clear();
		// 无外伤
		if ("0601".equals((String) map_ws.get("diseasetext_ws"))) {
			map_condit.put("pastHisTypeCode", "06");
			map_condit.put("diseaseCode", "0601");
			map_condit.put("diseaseText", "无外伤史");
			map_condit.put("empiId", body.get("empiId"));
			dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit, false);
		}

		// 有输血
		map_condit.clear();
		if ("0402".equals((String) map_sx.get("diseasetext_sx"))) {
			if ((String) map_sx.get("diseasetext_sx0")!=null && ((String) map_sx.get("diseasetext_sx0")).length()>0) {
				map_condit.put("diseaseText", map_sx.get("diseasetext_sx0"));// 输血的描述
				map_condit.put("startDate", map_sx.get("startdate_sx0"));// 输血的时间
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "04");
				map_condit.put("diseaseCode", "0402");
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
			map_condit.clear();
			if ((String) map_sx.get("diseasetext_sx1")!=null && ((String) map_sx.get("diseasetext_sx1")).length()>0) {
				map_condit.put("diseaseText", map_sx.get("diseasetext_sx1"));// 输血的描述
				map_condit.put("startDate", map_sx.get("startdate_sx1"));// 输血的时间
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "04");
				map_condit.put("diseaseCode", "0402");
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit,false);
			}
		}
		map_condit.clear();
		// 无输血
		if ("0401".equals((String) map_sx.get("diseasetext_sx"))) {
			map_condit.put("pastHisTypeCode", "04");
			map_condit.put("diseaseCode", "0401");
			map_condit.put("diseaseText", "无输血史");
			map_condit.put("empiId", body.get("empiId"));
			dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit, false);
		}
	}

	// 保存个人既往史（遗传病和残疾）
	@SuppressWarnings("unchecked")
	public void savePastHistoryYCBandCJ(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		String oldop=op;
		op = "create";
		Map<String, Object> map_ycbs = (Map<String, Object>) body.get("ycbs");
		Map<String, Object> map_cj = (Map<String, Object>) body.get("cj");
		Map<String, Object> map_condit = new HashMap<String, Object>();
		// 有遗传病史
		if ("0502".equals((String) map_ycbs.get("diseasetextRedioYCBS"))) {
			if ((String) map_ycbs.get("diseasetextYCBS")!=null && ((String) map_ycbs.get("diseasetextYCBS")).length()>0) {
				map_condit.put("diseaseText", map_ycbs.get("diseasetextYCBS"));// 手术的描述
				map_condit.put("empiId", body.get("empiId"));
				map_condit.put("pastHisTypeCode", "05");
				map_condit.put("diseaseCode", "0502");
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
		}
		map_condit.clear();
		// 无遗传病史
		if ("0501".equals((String) map_ycbs.get("diseasetextRedioYCBS"))) {
			map_condit.put("pastHisTypeCode", "05");
			map_condit.put("diseaseCode", "0501");
			map_condit.put("diseaseText", "无遗传病史");
			map_condit.put("empiId", body.get("empiId"));
			dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit, false);
		}
		map_condit.clear();
		// 有残疾情况
		List<String> list = (List<String>) (map_cj.get("diseasetextCheckCJ") != null ? Arrays
				.asList(map_cj.get("diseasetextCheckCJ").toString().split(",")):new ArrayList<String>());
		if (list == null || "null".equals(list) || list.size() == 0 || "".equals(list.get(0))) {
			return;
		}
		for (String o : list) {
			if (!"1101".equals(o)) {
				map_condit.put("pastHisTypeCode", "11");
				map_condit.put("diseaseCode", o);
				map_condit.put("empiId", body.get("empiId"));
				if ("1199".equals(o)) {
					map_condit.put("diseaseText", map_cj.get("cjqk_qtcj1"));
				} else {
					map_condit.put("diseaseText", getText("11", o));
				}
				if(oldop.equals("create")){
					savePersonProblem(map_condit);
				}
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			} else {
				map_condit.put("pastHisTypeCode", "11");
				map_condit.put("diseaseCode", "1101");
				map_condit.put("diseaseText", "无残疾");
				map_condit.put("empiId", body.get("empiId"));
				dao.doSave(op, BSCHISEntryNames.EHR_PastHistory, map_condit,false);
			}
		}
	}
	//保存个人既往史（家族史）
	@SuppressWarnings("unchecked")
	public void savePastHistoryJZS(String op, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		op = "create";
		Map<String, Object> jzjbs = (Map<String, Object>) body.get("jzjbs");
		List<String> list_fq = (List<String>) (jzjbs
				.get("diseasetext_check_fq") != null ? Arrays.asList(jzjbs
				.get("diseasetext_check_fq").toString().split(",")): new ArrayList<String>());
		List<String> list_mq = (List<String>) (jzjbs.get("diseasetextCheckMQ") != null ? Arrays
				.asList(jzjbs.get("diseasetextCheckMQ").toString().split(",")): new ArrayList<String>());
		List<String> list_xdjm = (List<String>) (jzjbs.get("diseasetextCheckXDJM") != null ? Arrays.asList(jzjbs
				.get("diseasetextCheckXDJM").toString().split(",")): new ArrayList<String>());
		List<String> list_zn = (List<String>) (jzjbs.get("diseasetextCheckZN") != null ? Arrays
				.asList(jzjbs.get("diseasetextCheckZN").toString().split(",")): new ArrayList<String>());
		Map<String, Object> map_condit = new HashMap<String, Object>();
		// 父亲
		// if ("07".equals(jzjbs.get("diseasetext_redio_fq") + "")) {
		if (list_fq != null && !"null".equals(list_fq) && list_fq.size() > 0 && !"".equals(list_fq.get(0))) {
			for (String o : list_fq) {
				if (!"0701".equals(o)) {
					map_condit.put("diseaseCode", o);
					map_condit.put("pastHisTypeCode", "07");
					map_condit.put("empiId", body.get("empiId"));
					if ("0799".equals(o)) {
						map_condit.put("diseaseText", jzjbs.get("qt_fq1"));
					} else {
						map_condit.put("diseaseText", getText("07", o));
					}
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				} else {
					map_condit.put("diseaseCode", "0701");
					map_condit.put("pastHisTypeCode", "07");
					map_condit.put("diseaseText", "无父亲疾病史");
					map_condit.put("empiId", body.get("empiId"));
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				}
			}
		}
		map_condit.clear();
		// 母亲
		if (list_mq != null && !"null".equals(list_mq) && list_mq.size() > 0 && !"".equals(list_mq.get(0))) {
			for (String o : list_mq) {
				if (!"0801".equals(o)) {
					map_condit.put("diseaseCode", o);
					map_condit.put("pastHisTypeCode", "08");
					map_condit.put("empiId", body.get("empiId"));
					if ("0899".equals(o)) {
						map_condit.put("diseaseText", jzjbs.get("qt_mq1"));
					} else {
						map_condit.put("diseaseText", getText("08", o));
					}
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				} else {
					map_condit.put("diseaseCode", "0801");
					map_condit.put("pastHisTypeCode", "08");
					map_condit.put("diseaseText", "无母亲疾病史");
					map_condit.put("empiId", body.get("empiId"));
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				}
			}
		}
		map_condit.clear();
		// 兄弟姐们
		if (list_xdjm != null && !"null".equals(list_xdjm) && list_xdjm.size() > 0 && !"".equals(list_xdjm.get(0))) {
			for (String o : list_xdjm) {
				if (!"0901".equals(o)) {
					map_condit.put("diseaseCode", o);
					map_condit.put("pastHisTypeCode", "09");
					map_condit.put("empiId", body.get("empiId"));
					if ("0999".equals(o)) {
						map_condit.put("diseaseText", jzjbs.get("qt_xdjm1"));
					} else {
						map_condit.put("diseaseText", getText("09", o));
					}
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				} else {
					map_condit.put("diseaseCode", "0901");
					map_condit.put("pastHisTypeCode", "09");
					map_condit.put("diseaseText", "无兄弟姐妹疾病史");
					map_condit.put("empiId", body.get("empiId"));
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				}
			}
		}
		map_condit.clear();
		// 子女
		if (list_zn != null && !"null".equals(list_zn) && list_zn.size() > 0 && !"".equals(list_zn.get(0))) {
			for (String o : list_zn) {
				if (!"1001".equals(o)) {
					map_condit.put("diseaseCode", o);
					map_condit.put("pastHisTypeCode", "10");
					map_condit.put("empiId", body.get("empiId"));
					if ("1099".equals(o)) {
						map_condit.put("diseaseText", jzjbs.get("qt_zn1"));
					} else {
						map_condit.put("diseaseText", getText("10", o));
					}
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				} else {
					map_condit.put("diseaseCode", "1001");
					map_condit.put("pastHisTypeCode", "10");
					map_condit.put("diseaseText", "无子女疾病史");
					map_condit.put("empiId", body.get("empiId"));
					dao.doSave(op, BSCHISEntryNames.EHR_PastHistory,map_condit, false);
				}
			}
		}
	}

	// 根据empiId获取家庭档案的familyId,flag：true 更新个人健康档案的所属家庭flag：false 不更新
	public String updateHealthFamilyId(Boolean flag, Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		String l = "";
		if (flag) {// falg==true ,说明先拿家庭档案的id，去更新个人健康档案的所属的家庭
			// hql.append(" select familyId as FAMILYID from EHR_FamilyRecord where empiId=:empiId ");
		} else {
			// flag==false，说明通过个人健康的所属家庭来获取家庭健康档案的数据
			hql.append(" select familyId as FAMILYID from EHR_HealthRecord where empiId=:empiId ");
		}
		List<Map<String, Object>> list = dao.doQuery(hql.toString(), body);
		if (list != null && list.size() > 0 && !(list.get(0).get("FAMILYID") == null)) {
			l = (String) (list.get(0).get("FAMILYID"));
			if (flag) {
				hql.setLength(0);
				hql.append(" update ").append(BSCHISEntryNames.EHR_HealthRecord).append(" set familyId =:familyId ")
				.append(" where empiId =:empiId");
				body.put("familyId", l);
				dao.doUpdate(hql.toString(), body);
			} else {
				return l;
			}
		}
		return l;
	}

	/**
	 * 查询个人信息基本资料以及既往史和生活环境
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public Map<String, Object> LoadDemographicInfo(Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException, ParseException {
		List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
		StringBuffer hql = new StringBuffer();
		String empiId = null;
		String regionCode = null;
		Map<String, Object> map_all = new HashMap<String, Object>();
		// 身份证异步请求
		if ((String) body.get("idCard") != null) {
			String hql_mpiId = "select empiId as EMPIID from MPI_DemographicInfo where idCard=:idCard ";
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("idCard", body.get("idCard"));
			List<Map<String, Object>> l = dao.doSqlQuery(hql_mpiId, m);
			if (l != null && l.size() > 0) {
				empiId = (String) (l.get(0).get("EMPIID"));
			} else {
				return null;
			}
			String hql_regionCode = "select regionCode as  REGIONCODE from EHR_HEALTHRECORD where empiId=:empiId ";
			m.clear();
			m.put("empiId", empiId);
			List<Map<String, Object>> ll = dao.doSqlQuery(hql_regionCode, m);
			if (ll != null && ll.size() > 0) {
				regionCode = (String) (ll.get(0).get("REGIONCODE"));
			}
		}
		// 正常的请求（除了事件请求）
		if (empiId == null && !(body.get("empiId") == null)) {
			empiId = (String) body.get("empiId");
			String hql_regionCode = "select regionCode as  REGIONCODE from EHR_HEALTHRECORD where empiId=:empiId ";
			Map<String, Object> mm = new HashMap<String, Object>();
			mm.put("empiId", empiId);
			List<Map<String, Object>> ll = dao.doSqlQuery(hql_regionCode, mm);
			if (ll != null && ll.size() > 0) {
				regionCode = (String) (ll.get(0).get("REGIONCODE"));
			}
		}
		// body只有一个familyId，从添加成员传过来的
		if (!(body.get("familyId") == null)) {
			// 说明empiId为null
			Map<String, Object> map_f = new HashMap<String, Object>();
			Map<String, Object> map_t = new HashMap<String, Object>();
			map_f = this.loadFamilyByFamilyId(body);
			map_all.putAll(map_f);
			String sql = "select regionCode_text as regionCode_text,regionCode as regionCode, manaDoctorId as manaDoctorId ,manaUnitId as manaUnitId "
					+ "from  EHR_FamilyRecord where familyId=:familyId ";
			map_f.clear();
			map_t.put("familyId", body.get("familyId"));
			List<Map<String, Object>> l = dao.doQuery(sql, map_t);
			if (l != null && l.size() > 0) {
				map_all.putAll(l.get(0));
			}
			return map_all;
		}
		// 查詢個人信息
		Map<String, Object> map_condit = new HashMap<String, Object>();
		map_condit.put("empiId", empiId);
		map_all = dao.doLoad(MPI_DemographicInfo, empiId);
		if (map_all == null) {
			map_all = new HashMap<String, Object>();
		}
		map_all.put("insuranceCode1", map_all.get("insuranceText"));
		// 文化程度 的处理：兼容以前原有的数据
		String se = "";
		se = (String) map_all.get("educationCode");
		if (se != null && se.length() > 0) {
			//启用老字典屏蔽字典转换
			//map_all.put("educationCode", GetEducationCode(se));
			map_all.put("educationCode", se );
		}
		// 查詢個人健康檔案信
		hql.setLength(0);
		// if(body.get("familyId")==null){
		hql.append("SELECT t.phrId as phrId,t.regionCode_text as regionCode_text,t.manaDoctorId as manaDoctorId,"
				+ "t.manaUnitId as manaUnitId,t.regionCode as regionCode ,"
				+ "t.masterFlag as masterFlag , t.isAgrRegister as isAgrRegister , t.incomeSource as incomeSource ,t.deadFlag as deadFlag,t.deadReason as deadReason,t.signFlag as signFlag, t.deadDate as deadDate,"
				+ "t.knowFlag as knowFlag,t.isPovertyAlleviation as isPovertyAlleviation from EHR_HealthRecord t  where t.empiId=:empiId");

		Map<String, Object> mm = dao.doLoad(hql.toString(), map_condit);
		if (mm != null && mm.size() > 0) {
			Map<String, Object> hrMap = new HashMap<String, Object>();
			Map<String, Object> hrMap1 = new HashMap<String, Object>();
			hrMap.put("manaDoctorId", mm.get("manaDoctorId"));
			hrMap.put("manaUnitId", mm.get("manaUnitId"));
			hrMap.put("regionCode", regionCode);
			hrMap1 = SchemaUtil.setDictionaryMessageForForm(hrMap,EHR_HealthRecord);
			mm.putAll(hrMap1);
			map_all.putAll(mm);
		}

		// 查询个人既往史
		hql.setLength(0);// 清空hql内容
		hql.append("SELECT t.pastHisTypeCode as pastHisTypeCode,t.diseaseCode as diseaseCode,"
				+ "t.diseaseText as diseaseText,t.confirmDate as confirmDate,t.pastHistoryId as pastHistoryId ,"
				+ "t.startDate as startDate FROM EHR_PastHistory t WHERE t.empiId=:empiId");

		list_map = dao.doQuery(hql.toString(), map_condit);
		Map<String, Object> map_full = new HashMap<String, Object>();
		List<String> list_ywgms = new ArrayList<String>();
		List<String> list_bl = new ArrayList<String>();
		List<String> list_jb = new ArrayList<String>();
		List<String> list_ss = new ArrayList<String>();
		List<String> list_sx = new ArrayList<String>();
		List<String> list_ws = new ArrayList<String>();
		List<String> list_fq = new ArrayList<String>();
		List<String> list_mq = new ArrayList<String>();
		List<String> list_xdjm = new ArrayList<String>();
		List<String> list_zn = new ArrayList<String>();
		List<String> list_cj = new ArrayList<String>();
		int flag = 1;
		int flag1 = 1;
		for (Map<String, Object> o : list_map) {
			String str = (String) o.get("pastHisTypeCode");
			int y = 0;
			if (str.indexOf("0") != -1 && Integer.parseInt(str) != 10) {
				y = Integer.parseInt(str.substring(1, str.length()));
			} else {
				y = Integer.parseInt(str);
			}
			switch (y) {
			case 1:
				map_full.put("diseasetext_radio_gm", "01");
				list_ywgms.add((String) o.get("diseaseCode"));
				if ("0109".equals((String) o.get("diseaseCode"))) {
					map_full.put("a_qt1", o.get("diseaseText"));
				}
				break;
			case 2:
				map_full.put("diseasetext_radio_jb", "02");
				// map_full.put("diseasetext_radio_jb", "02");
				if ("0201".equals((String) o.get("diseaseCode"))) {
					map_full.put("diseasetext_radio_jb", "0201");
				}
				if ("0202".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_gxy", o.get("confirmDate"));
				}
				if ("0203".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_tnb", o.get("confirmDate"));
				}
				if ("0204".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_gxb", o.get("confirmDate"));
				}
				if ("0205".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_mxzsxfjb", o.get("confirmDate"));
				}
				if ("0206".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_exzl", o.get("confirmDate"));
				}
				if ("0207".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_nzz", o.get("confirmDate"));
				}
				if ("0208".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_zxjsjb", o.get("confirmDate"));
				}
				if ("0209".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_jhb", o.get("confirmDate"));
				}
				if ("0210".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_gzjb", o.get("confirmDate"));
				}
				if ("0211".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_xtjx", o.get("confirmDate"));
				}
				if ("0212".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_zyb", o.get("confirmDate"));
					map_full.put("diseasetext_zyb", o.get("diseaseText"));
				}
				if ("0213".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_szjb", o.get("confirmDate"));
				}
				if ("0214".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_px", o.get("confirmDate"));
				}
				if ("0298".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_qtfdcrb", o.get("confirmDate"));
					map_full.put("diseasetext_qtfdcrb", o.get("diseaseText"));
				}
				if ("0299".equals((String) o.get("diseaseCode"))) {
					map_full.put("confirmdate_qt", o.get("confirmDate"));
					map_full.put("diseasetext_qt", o.get("diseaseText"));
				}
				list_jb.add((String) o.get("diseaseCode"));
				break;
			case 3:
				map_full.put("diseasetext_ss", o.get("diseaseCode"));
				if (flag == 1) {
					map_full.put("diseasetext_ss0", o.get("diseaseText"));
					map_full.put("startdate_ss0", o.get("startDate"));
					flag = 2;
				} else {
					map_full.put("diseasetext_ss1", o.get("diseaseText"));
					map_full.put("startdate_ss1", o.get("startDate"));
					flag = 1;
				}
				list_ss.add((String) o.get("diseaseCode"));
				break;
			case 4:
				map_full.put("diseasetext_sx", o.get("diseaseCode"));
				if (flag1 == 1) {
					map_full.put("diseasetext_sx0", o.get("diseaseText"));
					map_full.put("startdate_sx0", o.get("startDate"));
					flag1 = 2;
				} else {
					map_full.put("diseasetext_sx1", o.get("diseaseText"));
					map_full.put("startdate_sx1", o.get("startDate"));
				}
				list_sx.add((String) o.get("diseaseCode"));
				break;
			case 5:
				map_full.put("diseasetextRedioYCBS", o.get("diseaseCode"));
				map_full.put("diseasetextYCBS", o.get("diseaseText"));
				break;
			case 6:
				map_full.put("diseasetext_ws", o.get("diseaseCode"));
				if (flag == 1) {
					map_full.put("diseasetext_ws0", o.get("diseaseText"));
					map_full.put("startdate_ws0", o.get("startDate"));
					flag = 2;
				} else {
					map_full.put("diseasetext_ws1", o.get("diseaseText"));
					map_full.put("startdate_ws1", o.get("startDate"));
				}
				list_ws.add((String) o.get("diseaseCode"));
				break;

			case 7:
				map_full.put("diseasetext_redio_fq", o.get("diseaseCode"));
				list_fq.add((String) o.get("diseaseCode"));
				if ("0799".equals((String) o.get("diseaseCode"))) {
					map_full.put("qt_fq1", o.get("diseaseText"));
				}
				break;
			case 8:
				map_full.put("diseasetextRedioMQ", o.get("diseaseCode"));
				list_mq.add((String) o.get("diseaseCode"));
				if ("0899".equals((String) o.get("diseaseCode"))) {
					map_full.put("qt_mq1", o.get("diseaseText"));
				}
				break;
			case 9:
				map_full.put("diseasetextRedioXDJM", o.get("diseaseCode"));
				list_xdjm.add((String) o.get("diseaseCode"));
				if ("0999".equals((String) o.get("diseaseCode"))) {
					map_full.put("qt_xdjm1", o.get("diseaseText"));
				}
				break;
			case 10:
				map_full.put("diseasetextRedioZN", o.get("diseaseCode"));
				list_zn.add((String) o.get("diseaseCode"));
				if ("1099".equals((String) o.get("diseaseCode"))) {
					map_full.put("qt_zn1", o.get("diseaseText"));
				}
				break;
			case 11:
				map_full.put("diseasetextRedioCJ", o.get("diseaseCode"));
				list_cj.add((String) o.get("diseaseCode"));//
				if ("1199".equals((String) o.get("diseaseCode"))) {
					map_full.put("cjqk_qtcj1", o.get("diseaseText"));
				}
				break;
			case 12:
				map_full.put("diseasetext_radio_bl", "12");
				list_bl.add((String) o.get("diseaseCode"));
			}
		}
		map_full.put("diseasetext_check_gm", listToString(list_ywgms));
		map_full.put("diseasetext_check_bl", listToString(list_bl));
		map_full.put("diseasetext_check_jb", listToString(list_jb));
		map_full.put("diseasetext_check_ss", listToString(list_ss));
		map_full.put("diseasetext_check_sx", listToString(list_sx));
		map_full.put("diseasetext_check_ws", listToString(list_ws));
		map_full.put("diseasetext_check_fq", listToString(list_fq));
		map_full.put("diseasetextCheckMQ", listToString(list_mq));
		map_full.put("diseasetextCheckXDJM", listToString(list_xdjm));
		map_full.put("diseasetextCheckZN", listToString(list_zn));
		map_full.put("diseasetextCheckCJ", listToString(list_cj));
		map_all.putAll(map_full);// 2140806添加的
		// 生活环境:ehr_familyrecord
		hql.setLength(0);
		Map<String, Object> map_condit1 = new HashMap<String, Object>();

		// 查询个人健康档案的所属家庭FAMILYID,并更新个人健康档案的所属家庭
		map_condit1.put("empiId", empiId);
		hql.append("SELECT t.middleId as middleId,t.fuelType as fuelType,"
				+ "t.cookAirTool as cookAirTool,"
				+ "t.waterSourceCode as waterSourceCode ,"
				+ "t.washroom as washroom ,t.livestockColumn as livestockColumn "
				+ "  FROM EHR_FamilyMiddle t where t.empiId=:empiId");
		list_map.clear();
		list_map = dao.doQuery(hql.toString(), map_condit1);
		List<String> list_rllx = new ArrayList<String>();
		List<String> list_ys = new ArrayList<String>();
		List<String> list_cs = new ArrayList<String>();
		List<String> list_qcl = new ArrayList<String>();
		List<String> list_cfpfss = new ArrayList<String>();
		String ll = "";
		for (Map<String, Object> o : list_map) {
			if (!(o.get("cookAirTool") == null)) {
				list_cfpfss.add((String) o.get("cookAirTool"));
			}
			if (!(o.get("fuelType") == null)) {
				list_rllx.add((String) o.get("fuelType"));
			}
			if (!(o.get("waterSourceCode") == null)) {
				list_ys.add((String) o.get("waterSourceCode"));
			}
			if (!(o.get("washroom") == null)) {
				list_cs.add((String) o.get("washroom"));
			}
			if (!(o.get("livestockColumn") == null)) {
				list_qcl.add((String) o.get("livestockColumn"));
			}
			if (!(o.get("middleId") == null)) {
				ll = (String) o.get("middleId");
			}
		}
		map_all.put("shhjCheckCFPFSS", listToString(list_cfpfss));
		map_all.put("shhjCheckRLLX", listToString(list_rllx));
		map_all.put("shhjCheckYS", listToString(list_ys));
		map_all.put("shhjCheckCS", listToString(list_cs));
		map_all.put("shhjCheckQCL", listToString(list_qcl));
		if (ll != null && ll.length() > 0) {
			map_all.put("middleId", ll);// 2014.10.11添加
		}
		map_all.putAll(map_full);
		return map_all;

	}

	public static String listToString(List<String> stringList) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(",");
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.toString();
	}

	public Map<String, Object> LoadDoctorAndFamily(Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException, ParseException {
		Map<String, Object> map_all = new HashMap<String, Object>();
		Map<String, Object> mp = new HashMap<String, Object>();
		List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
		Map<String, Object> map_full = new HashMap<String, Object>();
		String regionCode = null;
		/**
		 * 选中网格地址--->责任医生--->管理机构：一级一级关联
		 */
		// 网格地址的下拉框事件
		if (("1").equals((String) body.get("fag_regionCode"))) {

			regionCode = (String) body.get("regionCode");
			StringBuffer hqll = new StringBuffer();
			mp.put("regionCode", regionCode);
			hqll.append("SELECT t.fuelType as fuelType,"
					+ "t.cookAirTool as cookAirTool,"
					+ "t.waterSourceCode as waterSourceCode ,"
					+ "t.washroom as washroom ,t.livestockColumn as livestockColumn "
					+ "  FROM EHR_FamilyRecord t where t.regionCode=:regionCode");
			list_map.clear();
			list_map = dao.doQuery(hqll.toString(), mp);
			List<String> list_rllx = new ArrayList<String>();
			List<String> list_ys = new ArrayList<String>();
			List<String> list_cs = new ArrayList<String>();
			List<String> list_qcl = new ArrayList<String>();
			List<String> list_cfpfss = new ArrayList<String>();

			for (Map<String, Object> o : list_map) {
				if (o.get("cookAirTool") != null) {
					list_cfpfss.add((String) o.get("cookAirTool"));
				}
				if (o.get("fuelType") != null) {
					list_rllx.add((String) o.get("fuelType"));
				}
				;
				if (o.get("waterSourceCode") != null) {
					list_ys.add((String) o.get("waterSourceCode"));
				}
				;
				if (o.get("washroom") != null) {
					list_cs.add((String) o.get("washroom"));
				}
				;
				if (o.get("livestockColumn") != null) {
					list_qcl.add((String) o.get("livestockColumn"));
				}
				;
			}

			map_all.put("shhjCheckCFPFSS", list_cfpfss);
			map_all.put("shhjCheckRLLX", list_rllx);
			map_all.put("shhjCheckYS", list_ys);
			map_all.put("shhjCheckCS", list_cs);
			map_all.put("shhjCheckQCL", list_qcl);
			// 查找责任医生：根据网格地址找责任医生

			map_all.putAll(map_full);

		}

		return map_all;
	}

	/**
	 * 通过FamilyId来查询家庭信息
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public Map<String, Object> loadFamilyByFamilyId(Map<String, Object> body)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
		Map<String, Object> map_all = new HashMap<String, Object>();
		hql.append("SELECT t.fuelType as fuelType,"
				+ "t.cookAirTool as cookAirTool,"
				+ "t.waterSourceCode as waterSourceCode ,"
				+ "t.washroom as washroom ,t.livestockColumn as livestockColumn "
				+ "  FROM EHR_FamilyRecord t where t.familyId=:familyId");
		list_map.clear();
		list_map = dao.doQuery(hql.toString(), body);
		List<String> list_rllx = new ArrayList<String>();
		List<String> list_ys = new ArrayList<String>();
		List<String> list_cs = new ArrayList<String>();
		List<String> list_qcl = new ArrayList<String>();
		List<String> list_cfpfss = new ArrayList<String>();

		for (Map<String, Object> o : list_map) {
			if (!(o.get("cookAirTool") == null)) {
				list_cfpfss.add((String) o.get("cookAirTool"));
			}
			if (!(o.get("fuelType") == null)) {
				list_rllx.add((String) o.get("fuelType"));
			}
			;
			if (!(o.get("waterSourceCode") == null)) {
				list_ys.add((String) o.get("waterSourceCode"));
			}
			;
			if (!(o.get("washroom") == null)) {
				list_cs.add((String) o.get("washroom"));
			}
			;
			if (!(o.get("livestockColumn") == null)) {
				list_qcl.add((String) o.get("livestockColumn"));
			}
			;
		}
		map_all.put("shhjCheckCFPFSS", list_cfpfss);
		map_all.put("shhjCheckRLLX", list_rllx);
		map_all.put("shhjCheckYS", list_ys);
		map_all.put("shhjCheckCS", list_cs);
		map_all.put("shhjCheckQCL", list_qcl);

		return map_all;
	}

	/**
	 * 通过empiId判断是否注册了个人信息
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public String loadInfoByEmpiId(String empiId)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		// String empiId = null;
		String hql_mpiId = "select personName as PERSONNAME from MPI_DemographicInfo where empiId=:empiId ";
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("empiId", empiId);
		List<Map<String, Object>> l = dao.doQuery(hql_mpiId, m);
		if (l != null && l.size() > 0) {
			if (!(l.get(0).get("PERSONNAME") == null)) {
				return (String) (l.get(0).get("PERSONNAME"));

			}
			return "";
		} else {
			return "";
		}

	}

	/**
	 * 根据主键查询家庭健康档案
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFamilyRecordById(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> familyRecord = new HashMap<String, Object>();
		try {
			familyRecord = dao.doLoad(EHR_FamilyRecord, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家庭信息失败！");
		}
		return familyRecord;
	}

	/**
	 * 根据userid获取username(医生),并装成{,,a{key:value,text:vlaue},,,},提供给前端的tree
	 * 
	 * @param userId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getDoctorByUserId(String userId)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_condit = new HashMap<String, Object>();
		Map<String, Object> map_condit1 = new HashMap<String, Object>();
		Map<String, Object> map_all = new HashMap<String, Object>();
		map_condit.put("userId", userId);
		hql.append("select b.personName as userName from SYS_Personnel b where b.personId =:userId ");

		map_condit1.put("key", userId);
		Map<String, Object> map_doctor = dao.doLoad(hql.toString(), map_condit);
		String doctor = "";
		if (map_doctor != null && map_doctor.size() > 0) {
			doctor = (String) map_doctor.get("userName");
		}
		map_condit1.put("text", doctor);
		map_all.put("manaDoctorId", userId);
		return map_all;

	}

	/**
	 * 根据 删除个人既往史的信息
	 * 
	 * @param userId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public boolean detelePastHistoryById(String empiId)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		StringBuffer hql = new StringBuffer();
		hql.append("detele from EHR_PastHistory where empiId=:empiId ");

		return true;

	}

	/**
	 * 根据 idCard获取empiId
	 * 
	 * @param userId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String getEmpiIdByIdCard(String idCard)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		String empiId = null;
		String hql_mpiId = "select empiId as EMPIID from MPI_DemographicInfo where idCard=:idCard ";
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("idCard", idCard);
		List<Map<String, Object>> l = dao.doSqlQuery(hql_mpiId, m);
		if (l != null && l.size() > 0) {
			empiId = (String) (l.get(0).get("EMPIID"));

		} else {
			return null;
		}
		return empiId;

	}

	/**
	 * 根据 empiId 和 masterFalg 查询家庭档案的familyId
	 * 
	 * @param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String GetFamilyIdByEmpiId(String empiId)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> m1 = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		String familyId = "";
		hql.append("select familyId as familyId from  "
				+ BSCHISEntryNames.EHR_HealthRecord + " where empiId=:empiId");
		m.put("empiId", empiId);

		m1 = dao.doLoad(hql.toString(), m);
		if (m1 != null && m1.size() > 0) {
			familyId = (String) m1.get("familyId");
			if (familyId != null && familyId.length() > 0) {

				return familyId;
			}
			return "";
		}
		return "";
	}

	/**
	 * 根据 familyId更改家庭档案的status为1，注销
	 * 
	 * @param userId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public void UpdateStatusByFamilyId(String familyId)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		Map<String, Object> m = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update ").append("EHR_FamilyRecord")
				.append(" set status=:status ")
				.append(" where familyId=:familyId");
		m.put("familyId", familyId);
		m.put("status", "1");
		dao.doUpdate(hql.toString(), m);

	}

	/**
	 * 根据 empiId查询个人健康档案的phrId
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String getPhrIdByempiId(String empiId)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		Map<String, Object> m = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		String phrId = "";
		hql.append("select"
				+ " phrId as PHRID from EHR_HealthRecord where empiId=:empiId");

		m.put("empiId", empiId);
		List<Map<String, Object>> l = dao.doSqlQuery(hql.toString(), m);
		if (l != null && l.size() > 0) {

			phrId = (String) l.get(0).get("PHRID");
			if (phrId != null && phrId.length() > 0) {
				return phrId;
			}
			return "";
		}
		return "";

	}

	/**
	 * 删除家庭成员
	 * 
	 * @param phrid
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public void removeFamilyByid(String phrid)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		// Map<String, Object> m = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		// Map<String, Object> map_family = new HashMap<String, Object>();
		// Map<String, Object> map_family1 = new HashMap<String, Object>();
		Map<String, Object> map_health = new HashMap<String, Object>();
		// Map<String, Object> map_health1 = new HashMap<String, Object>();
		map_health.put("phrId", phrid);
		// hql.append("select empiId as empiId from EHR_HealthRecord where phrId=:phrId");
		// map_health1 = dao.doLoad(hql.toString(), map_health);
		// if (!map_health1.isEmpty()) {
		// String empiId=map_health1.get("empiId")+"";
		//
		// hql.setLength(0);
		// hql.append("select familyId as familyId from EHR_FamilyRecord where empiId=:empiId ");
		// map_family.put("empiId", empiId);
		// map_family1= dao.doLoad(hql.toString(), map_family);
		// if(map_family1!=null&&!map_family1.isEmpty()){
		// String familyId=map_family1.get("familyId")+"";
		// hql.setLength(0);
		// map_health.put("familyId", familyId);
		// hql.append(" update EHR_HealthRecord  set familyId=:familyId where phrId=:phrId");
		// dao.doUpdate(hql.toString(), map_health);
		//
		// }else{
		// hql.setLength(0);
		map_health.put("familyId", "");
		map_health.put("signFlag", "n");
		hql.append(" update EHR_HealthRecord  set familyId=:familyId , signFlag=:signFlag where phrId=:phrId");
		dao.doUpdate(hql.toString(), map_health);

		// }

		// }

	}

	/**
	 * 修改家庭的状态,当没有家庭成员的时候，状态改为1，
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String updateFamilyStatus(String familyId)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_health = new HashMap<String, Object>();
		List<Map<String, Object>> map_health2 = new ArrayList<Map<String, Object>>();
		hql.append("select phrId as phrId from EHR_HealthRecord where familyId=:familyId");
		map_health.put("familyId", familyId);
		map_health2 = dao.doQuery(hql.toString(), map_health);
		try {
			if (map_health2 != null && map_health2.size() > 0) {
				// throw new
				// ModelDataOperationException(Constants.CODE_DATABASE_ERROR,
				// "该家庭有成员，不能删除！");

				return "n";
			} else {
				hql.setLength(0);
				hql.append("update EHR_FamilyRecord  set status=:status where familyId=:familyId ");
				map_health.put("status", "1");
				dao.doUpdate(hql.toString(), map_health);
				return "y";

			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("删除家庭档案错误！！", e);
		}

	}

	// 下面是对一些病的编码，进行匹配
	/**
	 * 
	 * @param t
	 *            :代表类别
	 * @param s
	 *            ：代表类别下的具体的编码
	 * @return
	 */
	public String getText(String t, String s) {
		int ss = 0;
		// 对“0”开头和不为“0”开头的区分
		int v = Integer.parseInt(s.charAt(0) + "");
		if (v == 0) {
			ss = Integer.parseInt(s.substring(0));
		} else {

			ss = Integer.parseInt(s);
		}
		// 过敏史
		if ("01".equals(t)) {

			switch (ss) {
			case 102:
				return "青霉素";
			case 103:
				return "磺胺";
			case 104:
				return "链霉素";
				// case 109:
				// return "其他";
			}

		}

		// 暴露史
		if ("12".equals(t)) {

			switch (ss) {
			case 1202:
				return "化学品";
			case 1203:
				return "毒物";
			case 1204:
				return "射线";

			}

		}

		// 既 往 史
		if ("02".equals(t)) {

			switch (ss) {
			case 202:
				return "高血压";
			case 203:
				return "糖尿病";
			case 204:
				return "冠心病";
			case 205:
				return "慢性阻塞性肺疾病";
			case 206:
				return "恶性肿瘤";
			case 207:
				return "脑卒中";
			case 208:
				return "重性精神疾病";
			case 209:
				return "结核病";
			case 210:
				return "肝脏疾病";
			case 211:
				return "先天畸形";
			case 214:
				return "贫血";
			case 213:
				return "肾脏疾病";

			}

		}
		// 家族史
		if ("07".equals(t) || "08".equals(t) || "09".equals(t)
				|| "10".equals(t)) {

			switch (ss) {
			case 702:
			case 802:
			case 902:
			case 1002:
				return "高血压";
			case 703:
			case 803:
			case 903:
			case 1003:
				return "糖尿病";
			case 704:
			case 804:
			case 904:
			case 1004:
				return "冠心病 ";
			case 705:
			case 805:
			case 905:
			case 1005:
				return "慢性阻塞性肺疾病";
			case 706:
			case 806:
			case 906:
			case 1006:
				return "恶性肿瘤";
			case 707:
			case 807:
			case 907:
			case 1007:
				return "脑卒中";
			case 708:
			case 808:
			case 908:
			case 1008:
				return "重性精神疾病 ";
			case 709:
			case 809:
			case 909:
			case 1009:

				return "结核病";
			case 710:
			case 810:
			case 910:
			case 1010:
				return "肝炎";
			case 711:
			case 811:
			case 911:
			case 1011:
				return "先天畸形 ";
				// case 799:
				// case 899:
				// case 999:
				// case 1099:
				// return "其他";
			}

		}
		// 残疾情况

		if ("11".equals(t)) {

			switch (ss) {
			case 1102:
				return "视力残疾";
			case 1103:
				return "听力残疾";
			case 1104:
				return "言语残疾";
			case 1105:
				return "肢体残疾 ";
			case 1106:
				return "智力残疾";
			case 1107:
				return "精神残疾";
			case 1108:
				return "孤独症儿童";
			case 1109:
				return "脑瘫儿童";
				// case 1199:
				// return "其他残疾";
			}

		}

		return "";

	}

	public void updateFamilyHealthById(Map<String, Object> map)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append(" update ").append(BSCHISEntryNames.EHR_HealthRecord)
				.append(" set familyId =:familyId ")
				.append(" where empiId =:empiId");

		dao.doUpdate(hql.toString(), map);

	}

	/**
	 * 判断一个用户是否是户主
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> isFamilyMain(String empiId)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		Map<String, Object> map_condit = new HashMap<String, Object>();
		Map<String, Object> map_condit1 = new HashMap<String, Object>();

		map_condit.put("empiId", empiId);
		StringBuffer hql = new StringBuffer();
		hql.append("select familyId as familyId ,masterFlag as masterFlag from EHR_HealthRecord where empiId=:empiId");
		try {
			map_condit1 = dao.doLoad(hql.toString(), map_condit);
			if (map_condit1 != null && map_condit1.size() > 0) {
				return map_condit1;
			} else {

				return null;
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("根据用户信息，查询用户是否是户主失败!", e);
		}

	}

	/**
	 * 根据familyid获取户主的名字
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String getOwnerName(String familyId)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		Map<String, Object> map_condit = new HashMap<String, Object>();
		Map<String, Object> map_condit1 = new HashMap<String, Object>();
		map_condit.put("familyId", familyId);
		StringBuffer hql = new StringBuffer();
		hql.append("select ownerName as ownerName  from EHR_FamilyRecord where familyId=:familyId");
		String ownerName = "";
		try {
			map_condit1 = dao.doLoad(hql.toString(), map_condit);
			if (map_condit1 != null && map_condit1.size() > 0) {
				ownerName = (String) map_condit1.get("ownerName");
			}
			return ownerName;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("根据家庭档案查询户主失败!", e);
		}

	}

	// 根据家庭健康档案的id更新户主
	public void updateOwnerNameById(String familyId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("familyId", familyId);
		map.put("ownerName", "");
		hql.append(" update ").append(BSCHISEntryNames.EHR_FamilyRecord)
				.append(" set ownerName =:ownerName ")
				.append(" where familyId =:familyId");

		dao.doUpdate(hql.toString(), map);

	}

	// 根据家庭健康档案的id更新户主
	public void updateOwnerNameById(String familyId, String ownerName)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("familyId", familyId);
		map.put("ownerName", ownerName);
		hql.append(" update ").append(BSCHISEntryNames.EHR_FamilyRecord)
				.append(" set ownerName =:ownerName ")
				.append(" where familyId =:familyId");
		dao.doUpdate(hql.toString(), map);
	}

	// 保存ehr_familyRecord
	public void saveFamilyMiddle(Map<String, Object> map)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update EHR_FamilyMiddle ")
				.append("set fuelType=:fuelType ,")
				.append("cookAirTool=:cookAirTool ,")
				.append("waterSourceCode=:waterSourceCode ,")
				.append("washroom=:washroom ,")
				.append("livestockColumn=:livestockColumn ")
				.append("where empiId=:empiId");
		dao.doUpdate(hql.toString(), map);
	}

	// 注销个人健康档案的信息
	public void CancelHealthByempiId(Map<String, Object> map)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		map.put("status", "1");
		hql.append("update EHR_HealthRecord ").append("set status=:status where empiId=:empiId");
		dao.doUpdate(hql.toString(), map);
	}

	// 根据empiId删除mpi表：因为这个在同时出现两个户主的情况下使用
	public void deleteByempiId(String empiId)
			throws PersistentDataOperationException {
		dao.doRemove(empiId, BSCHISEntryNames.MPI_DemographicInfo);
	}

	// 文化程度 的处理：兼容以前原有的数据
	public String GetEducationCode(String s)
			throws PersistentDataOperationException {
		if ("null".equals(s)) {
			return "";
		}
		int ss = 0;
		// 对“0”开头和不为“0”开头的区分
		int v = Integer.parseInt(s.charAt(0) + "");
		if (v == 0) {
			ss = Integer.parseInt(s.substring(0));
		} else {
			ss = Integer.parseInt(s);
		}
		switch (ss) {
		case 90:
			return "90";
		case 81:
		case 83:
		case 88:
		case 89:
			return "80";
		case 71:
		case 72:
		case 73:
		case 78:
		case 79:
			return "70";
		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:
		case 47:
		case 48:
		case 49:
		case 50:
		case 51:
		case 59:
		case 61:
		case 62:
		case 63:
		case 68:
		case 69:
			return "60";
		case 03:
		case 04:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 38:
		case 39:
			return "20";
		case 91:
			return "91";
		}
		return s;
	}
	//根据FamilyId查询个人健康档案的empiId
	public int GetEmpiIdByFamilyId(String familyId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> m1 = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select empiId as empiId from  "
				+ BSCHISEntryNames.EHR_HealthRecord
				+ " where familyId=:familyId");
		m.put("familyId", familyId);
		m1 = dao.doLoad(hql.toString(), m);
		if (m1 != null && m1.size() > 0 && !"null".equals(m1.get("empiId"))) {
			return 1;
		}
		return 0;
	}

	// 根据phrid查询个人健康档案的familyId
	public String GetFamilyIdByPhrId(String phrId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> m1 = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select familyId as familyId from  "
				+ BSCHISEntryNames.EHR_HealthRecord + " where phrId=:phrId");
		m.put("phrId", phrId);
		m1 = dao.doLoad(hql.toString(), m);
		if (m1 != null && m1.size() > 0 && !"null".equals(m1.get("familyId"))) {
			return (String) m1.get("familyId");
		}
		return "";
	}

	//根据phrid以及status判断是否是户主
	public int isOwnerByPhrIdAndStatus(String phrId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> m1 = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select familyId as familyId from  "
				+ BSCHISEntryNames.EHR_HealthRecord
				+ " where phrId=:phrId and masterFlag=:masterFlag");
		m.put("phrId", phrId);
		m.put("masterFlag", "y");
		m1 = dao.doLoad(hql.toString(), m);
		if (m1 != null && m1.size() > 0) {
			return 1;
		}
		return 0;
	}

	// 修改个人健康档案的是否户主
	public void updateHealthByPhrId(String phrId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("masterFlag", "n");
		m.put("relaCode", "");
		m.put("phrId", phrId);
		hql.append("update EHR_HealthRecord set masterFlag=:masterFlag ,relaCode=:relaCode ")
			.append("where phrId=:phrId");
		dao.doUpdate(hql.toString(), m);

	}

	// 修改个人健康档案的是否户主
	public void updateMasterFlagByEmpiId(String empiId, String masterFlag,
			String relaCode) throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("empiId", empiId);
		m.put("masterFlag", masterFlag);
		m.put("relaCode", relaCode);// 户主的关系
		hql.append("update EHR_HealthRecord set masterFlag =:masterFlag , relaCode =:relaCode")
			.append(" where empiId=:empiId");
		dao.doUpdate(hql.toString(), m);

	}

	// 修改个人健康档案与户主关系
	public void updateRelaCodeByFamilyId(String relaCode, String familyId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("relaCode", relaCode);// 户主的关系
		m.put("familyId", familyId);
		hql.append("update EHR_HealthRecord ").append("set relaCode =:relaCode ")
		.append(" where familyId=:familyId");
		dao.doUpdate(hql.toString(), m);
	}
	// 根据家庭健康档案的获取户主
	public String getOwnerName1(String s)
			throws PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		String ss = "";
		m = dao.doLoad(BSCHISEntryNames.EHR_FamilyRecord, s);
		if (m != null && m.size() > 0) {
			ss = (String) m.get("ownerName");
			if (ss != null && ss.length() > 0) {
				return ss;
			}
		}
		return "";
	}

	//根据 empiId获取ehr_familyMiddle的主键
	public String getMiddleIdByEmpiId(String empiId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		String middleId = "";
		hql.append("select middleId as MIDDLEID from EHR_FamilyMiddle where empiId=:empiId");
		m.put("empiId", empiId);
		List<Map<String, Object>> l = dao.doSqlQuery(hql.toString(), m);
		if (l != null && l.size() > 0) {
			middleId = (String) (l.get(0).get("MIDDLEID"));
			if (middleId != null && middleId.length() > 0) {
				return middleId;
			}
		}
		return middleId;
	}

	// 保存ehr_familyRecord
	public Map<String, Object> updateFamilyMiddle(Map<String, Object> map)
			throws PersistentDataOperationException {
		Map<String, Object> map_c = new HashMap<String, Object>();
		map_c.put("empiId", map.get("empiId"));
		map_c.put("cookAirTool", map.get("shhjCheckCFPFSS"));
		map_c.put("fuelType", map.get("shhjCheckRLLX"));
		map_c.put("waterSourceCode", map.get("shhjCheckYS"));
		map_c.put("washroom", map.get("shhjCheckCS"));
		map_c.put("livestockColumn", map.get("shhjCheckQCL"));
		StringBuffer hql = new StringBuffer();
		hql.append("update EHR_FamilyMiddle ")
				.append("set fuelType=:fuelType ,")
				.append("cookAirTool=:cookAirTool ,")
				.append("waterSourceCode=:waterSourceCode ,")
				.append("washroom=:washroom ,")
				.append("livestockColumn=:livestockColumn ")
				.append("where empiId=:empiId");
		dao.doUpdate(hql.toString(), map_c);
		return map;
	}

	//新增一条卡信息
	 public void saveCard(Map<String, Object> data, String num)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> m = new HashMap<String, Object>();
		data.put("cardTypeCode", num);
		try {
			// 保存到mpi服务器上
			List<Map<String, Object>> cardsData = EmpiUtil.changeToCardData(data);
			if (cardsData != null && cardsData.size() > 0) {
				EmpiUtil.registerCards(dao, ctx, cardsData);
			}
			// 保存到本地的数据库
			m.put("status", '0');
			m.put("cardTypeCode", num);
			m.put("cardNo", data.get("cardNo"));
			m.put("empiId", data.get("empiId"));
			saveCardNoInfo(m);
		} catch (Exception e) {
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "保存卡信息失败。", e);
		}
	}

	//根据 网格地址获取户主
	public String getOwnerNameByRegionCode(Map<String, Object> body)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		String ownerNameV = null;
		if (body.get("regionCode") == null
				|| body.get("regionCode").toString().length() == 0) {
			return "";
		}
		StringBuffer hql = new StringBuffer();
		hql.append(" select  ownerName as OWNERNAME from EHR_FamilyRecord  where regionCode =:regionCode ");
		List<Map<String, Object>> m = dao.doSqlQuery(hql.toString(), body);
		if (m != null && m.size() > 0) {
			ownerNameV = (String) (m.get(0).get("OWNERNAME"));
			if (ownerNameV != null && ownerNameV.length() > 0) {
				return ownerNameV;
			}
			return "";
		}
		return "";
	}
	//解除该成员在本家庭中的服务记录
	public void removeMasterplateData(String phrid)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> map_health = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		String empiId = GetFamilyIdByPhrId(phrid);
		if (empiId != null && empiId.length() > 0 && !"null".equals(empiId)) {
			map_health.put("masterplateType", "1");
			map_health.put("ServiceFlag", "2");
			map_health.put("empiId", empiId);
			hql.append(" update MPM_MasterplateData  set ServiceFlag=:ServiceFlag where empiId=:empiId and masterplateType=:masterplateType ");
			dao.doUpdate(hql.toString(), map_health);
		}
	}

	//查询卡号是否重复：同种类型的卡号不能出现重复
	public int selectCardNo(Map<String, Object> data, String num)
			throws ModelDataOperationException, ValidateException,
			PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("cardTypeCode", num);
		m.put("cardNo", data.get("cardNo"));
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) as totalcount ").append("from  MPI_Card ")
				.append(" where cardNo=:cardNo")
				.append(" and cardTypeCode=:cardTypeCode ");
		List<Map<String, Object>> count = dao.doSqlQuery(hql.toString(), m);
		String cardNoCount = count.get(0).get("TOTALCOUNT") + "";
		if ("1".equals(cardNoCount)) {
			return 1;
		}
		return 0;
	}

	// 查询卡号类表
	 public List<Map<String, Object>> selectCardNoByEmpiId(String data)
			throws ModelDataOperationException, ValidateException,
			PersistentDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("empiId", data);
		StringBuffer hql = new StringBuffer();
		hql.append("select cardTypeCode as cardTypeCode , cardNo as cardNo ")
				.append("from  MPI_Card ").append(" where empiId=:empiId ");
		List<Map<String, Object>> data1 = dao.doQuery(hql.toString(), m);
		if (data1 != null && !"null".equals(data1) && data1.size() > 0) {
			return data1;
		}
		return null;

	}// 根据卡号查询个人信息

	public Map<String, Object> queryPersonInfoByHealthNo(String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select cardNo as cardNo from ")
				.append(MPI_Card).append(" where empiId=:empiId ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),parameters);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据健康卡号查询个人基本信息失败。");
		}
	}
	// 保存卡号信息
	public void saveCardNoInfo(Map<String, Object> data)
			throws ModelDataOperationException {
		try {
			dao.doSave("create", BSCHISEntryNames.MPI_Card, data, false);
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "保存卡号信息失败。");
		}

	}

	public void updateDieInfo(Map<String, Object> data)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append(" update EHR_HealthRecord  set deadFlag=:deadFlag ,deadDate=:deadDate,deadReason=:deadReason where phrId=:phrId ");
		dao.doUpdate(hql.toString(), data);
	}

	/**
	 * 根据传入的字段(单个)和schemas，获取该值，
	 * 
	 * @param
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String getValueByParameters(String entryName, String parameters,
			Map<String, Object> data) throws ModelDataOperationException,
			PersistentDataOperationException {
		Set<String> set = data.keySet();
		Iterator<String> iterator = set.iterator();
		String s = null;
		String V = null;
		while (iterator.hasNext()) {
			s = iterator.next();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select ").append(parameters).append(" as " + parameters)
				.append(" from " + entryName).append(" where " + s + "=:" + s);
		Map<String, Object> mapInfo = dao.doLoad(sb.toString(), data);
		if (mapInfo != null && mapInfo.size() > 0) {
			V = (String) mapInfo.get(parameters);
			if (V != null && V.length() > 0) {
				return V; // null+""返回 null
			}
			return "";
		}
		return "";
	}
}