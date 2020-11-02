/**
 * @(#)FamilyRecordService.java Created on Aug 18, 2009 5:27:07 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yao zhenhua</a>
 */
public class SystemUserService extends AbstractActionService implements
		DAOSupportable {

	private static final Log logger = LogFactory
			.getLog(SystemUserService.class);

	@SuppressWarnings("unchecked")
	protected void doCheckUser(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException,
			ModelDataOperationException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String userId = StringUtils.trimToEmpty((String) reqBody.get("userId"));
		String jobId = StringUtils.trimToEmpty((String) reqBody.get("jobId"));
		String manaUnitId = StringUtils.trimToEmpty((String) reqBody
				.get("manaUnitId"));
		String[][][] query = null;
		String jobTitles[];
		String manaUnits[];
		if (jobId.equals("")) {
			SystemUserModel sum = new SystemUserModel(dao);
			List<Map<String, Object>> props = sum.getUserProps(userId);
			jobTitles = new String[props.size()];
			manaUnits = new String[props.size()];
			for (int i = 0; i < props.size(); i++) {
				Map<String, Object> prop = props.get(i);
				jobTitles[i] = (String) prop.get("jobId");
				manaUnits[i] = (String) prop.get("manaUnitId");
			}
		} else {
			jobTitles = jobId.split(",");
			manaUnits = manaUnitId.split(",");
		}
		for (int j = 0; j < jobTitles.length; j++) {
			if ("01".equals(jobTitles[j]) || "02".equals(jobTitles[j])
					|| "03".equals(jobTitles[j]) || "05".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { MPI_DemographicInfo },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_FamilyRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" } },
						{ { EHR_HealthRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" } },
						{ { EHR_LifeStyle },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { EHR_PastHistory }, { "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PersonProblem },
								{ "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PoorPeopleVisit },
								{ "inputUser", "inputUnit" },
								{ "visitUser", "inputUnit" } },
						{ { EHR_NormalPopulationVisit },
								{ "createUser", "createUnit" },
								{ "visitUser", "createUnit" } },
						// {{ EHR_AreaGrid},{ "manaDoctor",""}},
						{ { MDC_OldPeopleRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { MDC_OldPeopleVisit }, { "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_OldPelpleDescription },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_OldPeopleCheckup },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { HC_HealthCheck }, { "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_HypertensionRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						// {{ MDC_HypertensionVisit},{ "visitDoctor" ,""}},
						{ { MDC_HypertensionMedicine },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { MDC_HyperVisitDescription },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_HyperVisitHealthTeach },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_DiabetesRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						// {{ MDC_DiabetesVisit},{
						// "visitDoctor",""},{"inputUser","inputUnit"},{"lastModifyUser"
						// ,"lastModifyUnit"}},
						{ { MDC_DiabetesMedicine },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { MDC_DiabetesVisitDescription },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_DiabetesVisitHealthTeach },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MDC_DiabetesComplication },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { PIV_VaccinateRecord },
								{ "manaDoctorId", "manaUnitId" } },
						{ { PSY_PsychosisRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { HER_EducationRecord },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { DEF_BrainDeformityRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { DEF_IntellectDeformityRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { DEF_LimbDeformityRecord },
								{ "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { GDR_FirstGuide }, { "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "teacher", "createUnit" } },
						{ { GDR_GroupDinnerRecord },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { GDR_SecondGuide }, { "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "guider", "createUnit" } },
						{ { GDR_Visit }, { "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "visitUser", "visitUnit" } },
						{ { MOV_EHR }, { "applyUser", "applyUnit" },
								{ "targetDoctor", "targetUnit" },
								{ "sourceDoctor", "sourceUnit" } },
						{ { MOV_ManaInfoBatchChange },
								{ "applyUser", "applyUnit" },
								{ "targetDoctor", "targetUnit" } },
						// {{ MOV_ManaInfoBatchChangeDetail},{ "sourceDoctor"
						// ,""}},
						{ { MOV_ManaInfoChange }, { "applyUser", "applyUnit" },
								{ "affirmUser", "affirmUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_ManaInfoChangeDetail },
								{ "targetDoctor", "targetUnit" },
								{ "sourceDoctor", "sourceUnit" } },
						{ { PER_CheckupRegister },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						// {{ PER_CheckupSummary},{
						// "inputDoctor",""},{"lastModifyUser"
						// ,"lastModifyUnit"}},
						{ { PER_CheckupDetail },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { ADMIN_ProblemCollect },
								{ "createUser", "createUnit" } } };
			} else if ("07".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { MPI_DemographicInfo },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_FamilyRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { EHR_HealthRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { EHR_LifeStyle },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PastHistory }, { "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PersonProblem },
								{ "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						// {{ EHR_AreaGrid},{ "cdhDoctor",""}},
						{ { CDH_HealthCard }, { "manaDoctorId", "manaUnitId" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cdhDoctorId", "manaUnitId" } },
						{ { CDH_Accident }, { "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_CheckupInOne },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_CheckupOneToTwo },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_CheckupThreeToSix },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_Inquire }, { "inquireDoctor", "inquireUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_DebilityChildren },
								{ "createUser", "createUnit" },
								{ "closedDoctor", "closedUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { CDH_DebilityChildrenVisit },
								{ "examineDoctor", "examineUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_ChildrenCheckupDescription },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_DeadRegister }, { "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cdhDoctorId", "manaUnitId" } },
						{ { CDH_BirthCertificate },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_DisabilityMonitor },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_DeadRegister }, { "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { CDH_OneYearSummary },
								{ "summaryDoctor", "summaryUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_CDH }, { "applyUser", "applyUnit" },
								{ "sourceCdhDoctorId", "sourceManaUnitId" },
								{ "targetCdhDoctorId", "targetManaUnitId" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_ManaInfoBatchChange },
								{ "applyUser", "applyUnit" },
								{ "targetDoctor", "targetUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						// {{ MOV_ManaInfoBatchChangeDetail},{
						// "sourceDoctor",""} },
						{ { MOV_ManaInfoChange }, { "applyUser", "applyUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_ManaInfoChangeDetail },
								{ "targetDoctor", "targetUnit" },
								{ "sourceDoctor", "sourceUnit" } },
						// {{ PER_CheckupRegister},{"inputUser",""},{
						// "lastModifyUser" ,"lastModifyUnit"}},
						// {{ PER_CheckupSummary},{ "checkupDoctor",""},{
						// "inputDoctor",""},{"lastModifyUser"
						// ,"lastModifyUnit"}},
						{ { PER_CheckupDetail },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { ADMIN_ProblemCollect },
								{ "createUser", "createUnit" } } };
			} else if ("08".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { MPI_DemographicInfo },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_FamilyRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { EHR_HealthRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						{ { EHR_LifeStyle },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PastHistory }, { "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PersonProblem },
								{ "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						// {{ EHR_AreaGrid},{ "mhcDoctor",""}},
						{ { MHC_PregnantRecord },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cancellationUser", "cancellationUnit" },
								{ "mhcDoctorId", "manaUnitId" },
								{ "manaDoctorId", "manaUnitId" } },
						{ { MHC_VisitRecord }, { "doctorId", "hospitalCode" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_PregnantWomanIndex },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { MHC_VisitRecordDescription },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_WomanRecord }, { "mhcDoctorId", "manaUnitId" },
								{ "manaDoctorId", "manaUnitId" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_Postnatal42dayRecord },
								{ "checkDoctor", "checkUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_BabyVisitRecord },
								{ "visitDoctor", "visitUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_BabyVisitInfo }, { "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_EndManagement }, { "endDoctor", "endUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_FirstVisitRecord },
								{ "visitDoctorCode", "visitUnitCode" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_FetalRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { MHC_PostnatalVisitInfo },
								{ "checkDoctor", "checkUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MHC_HighRiskVisitReason },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { MHC_PregnantSpecial },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_MHC }, { "applyUser", "applyUnit" },
								{ "targetMhcDoctorId", "targetManaUnitId" },
								{ "sourceMhcDoctorId", "sourceManaUnitId" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_ManaInfoBatchChange },
								{ "applyUser", "applyUnit" },
								{ "targetDoctor", "targetUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						// {{ MOV_ManaInfoBatchChangeDetail},{ "sourceDoctor"
						// ,""}},
						{ { MOV_ManaInfoChange }, { "applyUser", "applyUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_ManaInfoChangeDetail },
								{ "targetDoctor", "targetUnit" },
								{ "sourceDoctor", "sourceUnit" } },
						// {{ PER_CheckupRegister},{ "inputUser",""},{
						// "lastModifyUser" ,"lastModifyUnit"}},
						// {{ PER_CheckupSummary},{ "checkupDoctor",
						// ""},{"inputDoctor",""},{"lastModifyUser"
						// ,"lastModifyUnit"}},
						{ { PER_CheckupDetail },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { ADMIN_ProblemCollect },
								{ "createUser", "createUnit" } } };
			} else if ("15".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { MPI_DemographicInfo },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_HealthRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_LifeStyle },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PastHistory }, { "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PersonProblem },
								{ "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { DC_RabiesRecord }, { "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { DC_Vaccination }, { "createUser", "createUnit" } },
						{ { ADMIN_ProblemCollect },
								{ "createUser", "createUnit" } } };
			} else if ("16".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { MPI_DemographicInfo },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_HealthRecord },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" },
								{ "cancellationUser", "cancellationUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_LifeStyle },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PastHistory }, { "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { EHR_PersonProblem },
								{ "recordUser", "recordUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { SCH_SchistospmaManage },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { SCH_SchistospmaRecord },
								{ "closedDoctor", "closedUnit" },
								{ "createUser", "createUnit" },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "cancellationUser", "cancellationUnit" } },
						// {{ SCH_SchistospmaVisit},{
						// "visitDoctor",""},{"inputUser","inputUnit"},{
						// "lastModifyUser" ,"lastModifyUnit"}},
						{ { SCH_SnailBaseInfomation },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { SCH_SnailFindInfomation },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { SCH_SnailKillInfomation },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { SCH_SchistospmaManage },
								{ "inputUser", "inputUnit" },
								{ "lastModifyUser", "lastModifyUnit" } } };
			} else if ("14".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { HER_EducationPlanSet },
								{ "planPerson", "planUnit" },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { HER_EducationPlanExe },
								{ "lastModifyUser", "lastModifyUnit" } },
						{ { MOV_CDH }, { "applyUser", "applyUnit" },
								{ "affirmUser", "affirmUnit" } },
						{ { MOV_EHR }, { "applyUser", "applyUnit" },
								{ "affirmUser", "affirmUnit" } },
						{ { MOV_MHC }, { "applyUser", "applyUnit" },
								{ "affirmUser", "affirmUnit" } },
						{ { MOV_ManaInfoBatchChange },
								{ "applyUser", "applyUnit" },
								{ "affirmUser", "affirmUnit" } },
						{ { MOV_ManaInfoChange }, { "applyUser", "applyUnit" },
								{ "affirmUser", "affirmUnit" } },
						{ { ADMIN_ProblemCollect },
								{ "createUser", "createUnit" } } };
			} else if ("06".equals(jobTitles[j])) {
				query = new String[][][] {
						{ { SYS_USERS },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { SYS_UserProp },
								{ "lastModifyUser", "lastModifyUnit" },
								{ "createUser", "createUnit" } },
						{ { ADMIN_ProblemCollect },
								{ "createUser", "createUnit" } } };
			} else {
				query = new String[][][] {};
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			for (int i = 0; i < query.length; i++) {
				StringBuffer sql = new StringBuffer(
						"select count(*) as count from ");
				sql.append(query[i][0][0]).append(" where ");
				parameters = new HashMap<String, Object>();
				for (int k = 1; k < query[i].length; k++) {
					if (k > 1) {
						sql.append(" or ");
					}
					sql.append("(").append(query[i][k][0]).append(" =:doc")
							.append(k).append(" and ").append(query[i][k][1])
							.append(" =:unit").append(k).append(")");
					parameters.put("doc" + k, userId);
					parameters.put("unit" + k, manaUnits[j]);
				}
				// Query q=session.createQuery(sql.toString());
				// q.setString("doc", userId);
				List<Map<String, Object>> l = dao.doQuery(sql.toString(),
						parameters);
				Map<String, Object> m;
				if (l.size() > 0) {
					m = (Map<String, Object>) l.get(0);
					Long count = (Long) m.get("count");
					if (count > 0) {
						HashMap<String, Object> resBody = new HashMap<String, Object>();
						resBody.put("userIdUsed", true);
						res.put("body", resBody);
						return;
					}
				}
			}
		}
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("userIdUsed", false);
		res.put("body", resBody);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doCheckRoleUse(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap();
		List<HashMap<String, Object>> props = (List<HashMap<String, Object>>) body
				.get("props");
		Map m = props.get(0);
		String reqRole = (String) m.get("value");
		String[] reqRoles = reqRole.split(",");
		String dicId = (String) body.get("dicId");
		Dictionary unitDic = null;
		Dictionary roleDic = null;
		try {
			unitDic = DictionaryController.instance().get(dicId);
			roleDic = DictionaryController.instance().get("rolelist");
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		List l = unitDic.itemsList();
		List unfindRoles = new ArrayList();
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < reqRoles.length; j++) {
			String role = reqRoles[j];
			for (int i = 0; i < l.size(); i++) {
				DictionaryItem di = (DictionaryItem) l.get(i);
				String docRole = (String) di.getProperty("roles");
				String[] docRoles = docRole.split(",");
				if (di.getKey().equals(body.get("key"))) {
					for (int k = 0; k < docRoles.length; k++) {
						String tempRole = docRoles[k];
						if (!reqRole.contains(tempRole)) {
							if (!unfindRoles.contains(tempRole)) {
								unfindRoles.add(tempRole);
							}
						}
					}
				} else {
					if (docRole.contains(role)) {
						res.put("body", resBody);
						resBody.put("role", role);
						res.put(Service.RES_CODE, "400");
						sb.append("角色").append(roleDic.getText(role))
								.append("已经被使用不允许保存");
						res.put(Service.RES_MESSAGE, sb.toString());
						return;
					}
				}
			}
		}
		if (unfindRoles.size() > 0) {
			SystemUserModel sum = new SystemUserModel(dao);
			boolean b = sum.checkRoleUse(unfindRoles);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < unfindRoles.size(); i++) {
				String r = (String) unfindRoles.get(i);
				String text = roleDic.getText(r);
				buffer.append(text).append(",");
			}
			buffer.setLength(buffer.length() - 1);
			buffer.append("已经使用不允许修改");
			if (b) {
				res.put("body", resBody);
				resBody.put("roles", unfindRoles);
				res.put(Service.RES_CODE, "401");
				res.put(Service.RES_MESSAGE, buffer.toString());
			}
		}
	}

	protected void doQueryUsers(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			SystemUserModel sysModel = new SystemUserModel(dao);
			Map<String, Object> reValue = sysModel.queryUsers(req);
			if (reValue != null) {
				List<?> records = (List<?>) reValue.get("records");
				int pageSize = (Integer) reValue.get("pageSize");
				int pageNo = (Integer) reValue.get("pageNo");
				Long totalCount = (Long) reValue.get("totalCount");
				res.put("body", records);
				res.put("pageSize", pageSize);
				res.put("pageNo", pageNo);
				res.put("totalCount", totalCount);
			}
		} catch (ModelDataOperationException e) {
			logger.error("searching user unsuccessfully");
			throw new ServiceException(e);
		}
	}
}
