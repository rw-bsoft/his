/**
 * @(#)EHREntryNames.java Created on 2010-6-1 下午04:43:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source;

import java.util.LinkedHashMap;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public interface BSCHISEntryNames {
	
	//测试
	public static final String PersonInfo = "chis.application.demo.schemas.PersonInfo";
	// @@ 有schema的。
	public static final String ADMIN_GenBuilding = "chis.application.ag.schemas.ADMIN_GenBuilding";
	public static final String ADMIN_GenUnit = "chis.application.ag.schemas.ADMIN_GenUnit";
	public static final String ADMIN_ProblemCollect = "chis.application.pc.schemas.ADMIN_ProblemCollect";
	public static final String ADMIN_RVCFormConfig = "chis.application.conf.schemas.ADMIN_RVCFormConfig";
	public static final String CDH_9CityAge = "chis.application.pd.schemas.CDH_9CityAge";
	public static final String CDH_9CityHeight = "chis.application.pd.schemas.CDH_9CityHeight";
	public static final String CDH_Accident = "chis.application.cdh.schemas.CDH_Accident";
	public static final String CDH_CheckupInOne = "chis.application.cdh.schemas.CDH_CheckupInOne";
	public static final String CDH_CheckupOneToTwo = "chis.application.cdh.schemas.CDH_CheckupOneToTwo";
	public static final String CDH_CheckupThreeToSix = "chis.application.cdh.schemas.CDH_CheckupThreeToSix";
	public static final String CDH_CheckupThreeToSixPaper = "chis.application.cdh.schemas.CDH_CheckupThreeToSixPaper";
	public static final String CDH_HealthGuidance = "chis.application.cdh.schemas.CDH_HealthGuidance";
	public static final String CDH_ChildrenCheckupDescription = "chis.application.cdh.schemas.CDH_ChildrenCheckupDescription";
	public static final String CDH_CheckupHealthTeach = "chis.application.cdh.schemas.CDH_CheckupHealthTeach";
	public static final String CDH_DeadRegister = "chis.application.cdh.schemas.CDH_DeadRegister";
	public static final String CDH_DeadRegisterNh = "chis.application.cdh.schemas.CDH_DeadRegisterNh";
	public static final String CDH_DebilityChildren = "chis.application.cdh.schemas.CDH_DebilityChildren";
	public static final String CDH_DebilityChildrenCheck = "chis.application.cdh.schemas.CDH_DebilityChildrenCheck";
	public static final String CDH_DebilityChildrenVisit = "chis.application.cdh.schemas.CDH_DebilityChildrenVisit";
	public static final String CDH_DebilityCorrectionDic = "chis.application.pd.schemas.CDH_DebilityCorrectionDic";
	public static final String CDH_DefectRegister = "chis.application.cdh.schemas.CDH_DefectRegister";
	public static final String CDH_DictCorrection = "chis.application.pd.schemas.CDH_DictCorrection";
	public static final String CDH_HealthCard = "chis.application.cdh.schemas.CDH_HealthCard";
	public static final String CDH_Inquire = "chis.application.cdh.schemas.CDH_Inquire";
	public static final String CDH_OneYearSummary = "chis.application.cdh.schemas.CDH_OneYearSummary";
	public static final String CDH_WHOAge = "chis.application.pd.schemas.CDH_WHOAge";
	public static final String CDH_WHOHeight = "chis.application.pd.schemas.CDH_WHOHeight";
	public static final String CDH_BirthCertificate = "chis.application.cdh.schemas.CDH_BirthCertificate";
	public static final String CDH_DisabilityMonitor = "chis.application.cdh.schemas.CDH_DisabilityMonitor";
	public static final String CDH_ArchiveMove = "chis.application.cdh.schemas.CDH_ArchiveMove";
	public static final String CVD_Appraisal = "chis.application.cvd.schemas.CVD_Appraisal";
	public static final String CVD_AssessRegister = "chis.application.cvd.schemas.CVD_AssessRegister";
	public static final String CVD_AssessRegisterExport = "chis.application.cvd.schemas.CVD_AssessRegisterExport";
	public static final String CVD_Category = "chis.application.cvd.schemas.CVD_Category";
	public static final String CVD_RiskAssessReason = "chis.application.cvd.schemas.CVD_RiskAssessReason";
	public static final String CVD_Suggestion = "chis.application.cvd.schemas.CVD_Suggestion";
	public static final String CVD_Test = "chis.application.cvd.schemas.CVD_Test";
	public static final String CVD_TestDict = "chis.application.cvd.schemas.CVD_TestDict";
	public static final String DC_RabiesRecord = "chis.application.dc.schemas.DC_RabiesRecord";
	public static final String DC_Vaccination = "chis.application.dc.schemas.DC_Vaccination";
	public static final String IDR_Report = "chis.application.idr.schemas.IDR_Report";
	public static final String DEF_LimbDeformityRecord = "chis.application.def.schemas.DEF_LimbDeformityRecord";
	public static final String DEF_LimbTrainingEvaluate = "chis.application.def.schemas.DEF_LimbTrainingEvaluate";
	public static final String DEF_LimbMiddleEvaluate = "chis.application.def.schemas.DEF_LimbMiddleEvaluate";
	public static final String DEF_LimbTrainingPlan = "chis.application.def.schemas.DEF_LimbTrainingPlan";
	public static final String DEF_LimbTrainingRecord = "chis.application.def.schemas.DEF_LimbTrainingRecord";
	public static final String DEF_LimbTerminalEvaluate = "chis.application.def.schemas.DEF_LimbTerminalEvaluate";
	public static final String DEF_BrainDeformityRecord = "chis.application.def.schemas.DEF_BrainDeformityRecord";
	public static final String DEF_BrainTrainingEvaluate = "chis.application.def.schemas.DEF_BrainTrainingEvaluate";
	public static final String DEF_BrainMiddleEvaluate = "chis.application.def.schemas.DEF_BrainMiddleEvaluate";
	public static final String DEF_BrainTrainingPlan = "chis.application.def.schemas.DEF_BrainTrainingPlan";
	public static final String DEF_BrainTrainingRecord = "chis.application.def.schemas.DEF_BrainTrainingRecord";
	public static final String DEF_BrainTerminalEvaluate = "chis.application.def.schemas.DEF_BrainTerminalEvaluate";
	public static final String DEF_IntellectDeformityRecord = "chis.application.def.schemas.DEF_IntellectDeformityRecord";
	public static final String DEF_IntellectTrainingEvaluate = "chis.application.def.schemas.DEF_IntellectTrainingEvaluate";
	public static final String DEF_IntellectMiddleEvaluate = "chis.application.def.schemas.DEF_IntellectMiddleEvaluate";
	public static final String DEF_IntellectTrainingPlan = "chis.application.def.schemas.DEF_IntellectTrainingPlan";
	public static final String DEF_IntellectTrainingRecord = "chis.application.def.schemas.DEF_IntellectTrainingRecord";
	public static final String DEF_IntellectTerminalEvaluate = "chis.application.def.schemas.DEF_IntellectTerminalEvaluate";
	public static final String EhrBase = "EhrBase";
	public static final String EHR_AreaGrid = "chis.application.ag.schemas.EHR_AreaGrid";
	public static final String EHR_AreaGridChild = "chis.application.ag.schemas.EHR_AreaGridChild";
	public static final String EHR_CycleProblem = "chis.application.hr.schemas.EHR_CycleProblem";
	public static final String EHR_FamilyProblem = "chis.application.fhr.schemas.EHR_FamilyProblem";
	public static final String EHR_NormalPopulationVisit = "chis.application.npvr.schemas.EHR_NormalPopulationVisit";
	public static final String EHR_FamilyRecord = "chis.application.fhr.schemas.EHR_FamilyRecord";
	
	public static final String EHR_Record = "chis.application.fhr.schemas.EHR_Record";//家庭成员迁移记录
	public static final String EHR_FamilyMiddle = "chis.application.fhr.schemas.EHR_FamilyMiddle";//新添加：家庭生活环境的表
	public static final String EHR_HealthRecord = "chis.application.hr.schemas.EHR_HealthRecord";
	public static final String EHR_HealthBackRecord = "chis.application.hr.schemas.EHR_HealthBackRecord";
	public static final String EHR_HealthRecord_JBXX = "chis.application.hr.schemas.EHR_HealthRecord_JBXX";
	public static final String EHR_HealthRecordLogout = "chis.application.hr.schemas.EHR_HealthRecordLogout";
	public static final String EHR_LifeCycle = "chis.application.hr.schemas.EHR_LifeCycle";
	public static final String EHR_LifeStyle = "chis.application.hr.schemas.EHR_LifeStyle";
	public static final String EHR_LifeStyle_Rebuild = "chis.application.hr.schemas.EHR_LifeStyle_Rebuild";
	public static final String EHR_PastHistory = "chis.application.hr.schemas.EHR_PastHistory";
	public static final String EHR_PastHistory_Rebuild = "chis.application.hr.schemas.EHR_PastHistory_Rebuild";
	public static final String EHR_PersonProblem = "chis.application.hr.schemas.EHR_PersonProblem";
	public static final String EHR_PoorPeopleVisit = "chis.application.ppvr.schemas.EHR_PoorPeopleVisit";
	public static final String EHR_CommunityBasicSituation = "EHR_CommunityBasicSituation";
	public static final String EHR_CommunityPepBasicSituation = "EHR_CommunityPepBasicSituation";
	public static final String GA_Info = "GA_Info";
	public static final String GIS_History = "GIS_History";
	public static final String HIS_Recipe = "chis.application.his.schemas.HIS_Recipe";
	public static final String HIS_RecipeDetail = "chis.application.his.schemas.HIS_RecipeDetail";
	public static final String MDC_DiabetesComplication = "chis.application.dbs.schemas.MDC_DiabetesComplication";
	public static final String MDC_DiabetesFixGroup = "chis.application.dbs.schemas.MDC_DiabetesFixGroup";
	public static final String MDC_DiabetesGroupStandard = "chis.application.pd.schemas.MDC_DiabetesGroupStandard";
	public static final String MDC_DiabetesInquire = "chis.application.dbs.schemas.MDC_DiabetesInquire";
	public static final String MDC_DiabetesRepeatVisit = "chis.application.dbs.schemas.MDC_DiabetesRepeatVisit";
	public static final String MDC_DiabetesMedicine = "chis.application.dbs.schemas.MDC_DiabetesMedicine";
	public static final String MDC_DiabetesRecord = "chis.application.dbs.schemas.MDC_DiabetesRecord";
	public static final String MDC_DiabetesRecordEnd = "chis.application.dbs.schemas.MDC_DiabetesRecordEnd";
	public static final String MDC_DiabetesRecordLogout = "chis.application.dbs.schemas.MDC_DiabetesRecordLogout";
	public static final String MDC_DiabetesRecordStopManageSearch = "chis.application.dbs.schemas.MDC_DiabetesRecordStopManageSearch";
	public static final String MDC_DiabetesSimilarity = "chis.application.dbs.schemas.MDC_DiabetesSimilarity";
	public static final String MDC_DiabetesSimilarityCheck = "chis.application.dbs.schemas.MDC_DiabetesSimilarityCheck";
	public static final String MDC_DiabetesRisk = "chis.application.dbs.schemas.MDC_DiabetesRisk";
	public static final String MDC_DiabetesRiskAssessment = "chis.application.dbs.schemas.MDC_DiabetesRiskAssessment";
	public static final String MDC_DiabetesRiskHT = "chis.application.dbs.schemas.MDC_DiabetesRiskHT";
	public static final String MDC_DiabetesRiskVisit = "chis.application.dbs.schemas.MDC_DiabetesRiskVisit";
	public static final String MDC_DiabetesOGTTRecord = "chis.application.dbs.schemas.MDC_DiabetesOGTTRecord";
	// public static final String MDC_DiabetesSimilarityCheck =
	// "chis.application.dbs.schemas.MDC_DiabetesSimilarityCheck";
	// public static final String MDC_DiabetesSimilarityDetail =
	// "chis.application.dbs.schemas.MDC_DiabetesSimilarityDetail";
	// public static final String MDC_DiabetesSimilarityDetailCheck =
	// "chis.application.dbs.schemas.MDC_DiabetesSimilarityDetailCheck";
	public static final String MDC_DiabetesVisit = "chis.application.dbs.schemas.MDC_DiabetesVisit";
	public static final String MDC_DiabetesVisitDescription = "chis.application.dbs.schemas.MDC_DiabetesVisitDescription";
	public static final String MDC_DiabetesVisitHealthTeach = "chis.application.dbs.schemas.MDC_DiabetesVisitHealthTeach";
	public static final String MDC_HypertensionClinicRecord = "chis.application.hy.schemas.MDC_HypertensionClinicRecord";
	public static final String MDC_HypertensionFirst = "chis.application.hy.schemas.MDC_HypertensionFirst";
	public static final String MDC_HypertensionFirstDetail = "chis.application.hy.schemas.MDC_HypertensionFirstDetail";
	public static final String MDC_HypertensionFixGroup = "chis.application.hy.schemas.MDC_HypertensionFixGroup";
	public static final String MDC_HypertensionInquire = "chis.application.hy.schemas.MDC_HypertensionInquire";
	public static final String MDC_HypertensionMedicine = "chis.application.hy.schemas.MDC_HypertensionMedicine";
	public static final String MDC_HypertensionRecord = "chis.application.hy.schemas.MDC_HypertensionRecord";
	public static final String MDC_HypertensionRecordEnd = "chis.application.hy.schemas.MDC_HypertensionRecordEnd";
	public static final String MDC_HypertensionVisit = "chis.application.hy.schemas.MDC_HypertensionVisit";
	public static final String MDC_HyperVisitDescription = "chis.application.hy.schemas.MDC_HyperVisitDescription";
	public static final String MDC_HyperVisitHealthTeach = "chis.application.hy.schemas.MDC_HyperVisitHealthTeach";
	public static final String MDC_HypertensionSimilarity = "chis.application.hy.schemas.MDC_HypertensionSimilarity";
	public static final String MDC_HypertensionSimilarityC = "chis.application.hy.schemas.MDC_HypertensionSimilarityC";
	public static final String MDC_Hypertension_FCBP = "chis.application.hy.schemas.MDC_Hypertension_FCBP";
	public static final String MDC_HypertensionRisk = "chis.application.hy.schemas.MDC_HypertensionRisk";
	public static final String MDC_HypertensionRiskAssessment = "chis.application.hy.schemas.MDC_HypertensionRiskAssessment";
	public static final String MDC_HypertensionRiskHT = "chis.application.hy.schemas.MDC_HypertensionRiskHT";
	public static final String MDC_HypertensionRiskVisit = "chis.application.hy.schemas.MDC_HypertensionRiskVisit";
	public static final String MDC_EstimateDictionary = "chis.application.hy.schemas.MDC_EstimateDictionary";
	public static final String MDC_OldPeopleRecord = "chis.application.ohr.schemas.MDC_OldPeopleRecord";
	public static final String MDC_OldPeopleCheckup = "chis.application.ohr.schemas.MDC_OldPeopleCheckup";
	public static final String MDC_OldPeopleVisit = "chis.application.ohr.schemas.MDC_OldPeopleVisit";
	public static final String MDC_OldPelpleDescription = "chis.application.ohr.schemas.MDC_OldPelpleDescription";
	public static final String MDC_OldPeopleSelfCare = "chis.application.ohr.schemas.MDC_OldPeopleSelfCare";
	public static final String MDC_TumourRecord = "MDC_TumourRecord";
	public static final String MDC_TumourRecordWriteOff = "MDC_TumourRecordWriteOff";
	public static final String MDC_StrokeRecord = "chis.application.sr.schemas.MDC_StrokeRecord";
	public static final String MDC_StrokeVisit = "chis.application.sr.schemas.MDC_StrokeVisit";
	public static final String MDC_StrokeMedicine = "chis.application.sr.schemas.MDC_StrokeMedicine";
	public static final String MHC_BabyVisitInfo = "chis.application.mhc.schemas.MHC_BabyVisitInfo";
	public static final String MHC_BabyVisitRecord = "chis.application.mhc.schemas.MHC_BabyVisitRecord";
	public static final String MHC_EndManagement = "chis.application.mhc.schemas.MHC_EndManagement";
	public static final String MHC_FirstVisitRecord = "chis.application.mhc.schemas.MHC_FirstVisitRecord";
	public static final String MHC_HighRiskVisitReason = "chis.application.mhc.schemas.MHC_HighRiskVisitReason";
	public static final String MHC_HighRiskVisitReasonList = "chis.application.mhc.schemas.MHC_HighRiskVisitReasonList";
	public static final String MHC_Postnatal42dayRecord = "chis.application.mhc.schemas.MHC_Postnatal42dayRecord";
	public static final String MHC_PostnatalVisitInfo = "chis.application.mhc.schemas.MHC_PostnatalVisitInfo";
	public static final String MHC_PregnantRecord = "chis.application.mhc.schemas.MHC_PregnantRecord";
	public static final String MHC_PregnantSpecial = "chis.application.mhc.schemas.MHC_PregnantSpecial";
	public static final String MHC_PregnantWomanIndex = "chis.application.mhc.schemas.MHC_PregnantWomanIndex";
	public static final String MHC_VisitRecord = "chis.application.mhc.schemas.MHC_VisitRecord";
	public static final String MHC_VisitRecordDescription = "chis.application.mhc.schemas.MHC_VisitRecordDescription";
	public static final String MHC_WomanRecord = "chis.application.mhc.schemas.MHC_WomanRecord";
	public static final String MHC_FetalRecord = "chis.application.mhc.schemas.MHC_FetalRecord";
	public static final String MHC_DeliveryRecord = "chis.application.mhc.schemas.MHC_DeliveryRecord";
	public static final String MHC_DeliveryOnRecord = "chis.application.mhc.schemas.MHC_DeliveryOnRecord";
	public static final String MHC_DeliveryOnRecordChild = "chis.application.mhc.schemas.MHC_DeliveryOnRecordChild";
	public static final String MHC_DeliveryRecordSelect = "chis.application.mhc.schemas.MHC_DeliveryRecordSelect";
	public static final String MHC_PregnantScreen = "chis.application.mhc.schemas.MHC_PregnantScreen";
	public static final String DEA_DeathReportCard = "chis.application.mhc.schemas.DEA_DeathReportCard";
	public static final String MPI_Address = "chis.application.mpi.schemas.MPI_Address";
	public static final String MPI_Card = "chis.application.mpi.schemas.MPI_Card";
	public static final String MPI_Certificate = "chis.application.mpi.schemas.MPI_Certificate";
	public static final String MPI_Extension = "chis.application.mpi.schemas.MPI_Extension";
	public static final String MPI_ChildBaseInfo = "chis.application.mpi.schemas.MPI_ChildBaseInfo";
	public static final String MPI_ChildInfo = "chis.application.mpi.schemas.MPI_ChildInfo";
	public static final String MPI_DemographicInfo = "chis.application.mpi.schemas.MPI_DemographicInfo";
	public static final String MPI_LocalInfo = "chis.application.mpi.schemas.MPI_LocalInfo";
	public static final String MPI_Phone = "chis.application.mpi.schemas.MPI_Phone";
	public static final String PER_CheckupDetail = "chis.application.per.schemas.PER_CheckupDetail";
	public static final String PER_CheckupDict = "chis.application.per.schemas.PER_CheckupDict";
	public static final String PER_CheckupRegister = "chis.application.per.schemas.PER_CheckupRegister";
	public static final String PER_CheckupSummary = "chis.application.per.schemas.PER_CheckupSummary";
	public static final String PER_Combo = "chis.application.per.schemas.PER_Combo";
	public static final String PER_ComboDetail = "chis.application.per.schemas.PER_ComboDetail";
	public static final String PER_ICD = "chis.application.per.schemas.PER_ICD";
	public static final String PER_NonImmunization = "chis.application.per.schemas.PER_NonImmunization";
	public static final String PER_ProjectOffice = "chis.application.per.schemas.PER_ProjectOffice";
	public static final String PIV_VaccinateList = "chis.application.pivschemas.PIV_VaccinateList";
	public static final String PIV_VaccinateRecord = "chis.application.piv.schemas.PIV_VaccinateRecord";
	public static final String PSY_PsychosisRecord = "chis.application.psy.schemas.PSY_PsychosisRecord";
	public static final String PSY_PsychosisRecordLogout = "chis.application.psy.schemas.PSY_PsychosisRecordLogout";
	public static final String PSY_PsychosisFirstVisit = "chis.application.psy.schemas.PSY_PsychosisFirstVisit";
	public static final String PSY_PsychosisVisit = "chis.application.psy.schemas.PSY_PsychosisVisit";
	public static final String PSY_PsychosisVisitMedicine = "chis.application.psy.schemas.PSY_PsychosisVisitMedicine";
	public static final String PSY_HealthGuidance = "chis.application.psy.schemas.PSY_HealthGuidance";
	public static final String PSY_RecordPaper = "chis.application.psy.schemas.PSY_RecordPaper";
	public static final String PSY_AnnualAssessment = "chis.application.psy.schemas.PSY_AnnualAssessment";
	public static final String PUB_DrugDirectory = "chis.application.pub.schemas.PUB_DrugDirectory";
	public static final String PUB_ICD10 = "chis.application.pub.schemas.PUB_ICD10";
	public static final String PUB_Populace = "chis.application.pub.schemas.PUB_Populace";
	public static final String PUB_PublicInfo = "chis.application.pif.schemas.PUB_PublicInfo";
	public static final String PUB_Resource = "chis.application.pub.schemas.PUB_Resource";
	public static final String PUB_VisitPlan = "chis.application.pub.schemas.PUB_VisitPlan";
	public static final String PUB_VisitPlan_ChildrenCheckup = "chis.application.pub.schemas.PUB_VisitPlan_ChildrenCheckup";
	public static final String PUB_WorkList = "chis.application.pub.schemas.PUB_WorkList";
	public static final String PUB_Log = "chis.application.pub.schemas.PUB_Log";
	public static final String PUB_Stat = "chis.application.pub.schemas.PUB_Stat";
	public static final String PHE_RelevantInformation = "chis.application.phe.schemas.PHE_RelevantInformation";
	public static final String PHE_EnvironmentalEvent = "chis.application.phe.schemas.PHE_EnvironmentalEvent";
	public static final String STAT_ManageUnit = "STAT_ManageUnit";
	public static final String SYS_USERS = "SYS_USERS";
	public static final String SYS_UserProp = "SYS_UserProp";
	public static final String SYS_USERSSEARCH = "SYS_USERSSEARCH";
	public static final String SCH_SchistospmaRecord = "chis.application.sch.schemas.SCH_SchistospmaRecord";
	public static final String SCH_SchistospmaVisit = "chis.application.sch.schemas.SCH_SchistospmaVisit";
	public static final String SCH_SchistospmaVisitModule = "chis.application.sch.schemas.SCH_SchistospmaVisitModule";
	public static final String SCH_SnailBaseInfomation = "chis.application.sch.schemas.SCH_SnailBaseInfomation";
	public static final String SCH_SnailKillInfomation = "chis.application.sch.schemas.SCH_SnailKillInfomation";
	public static final String SCH_SnailFindInfomation = "chis.application.sch.schemas.SCH_SnailFindInfomation";
	public static final String HER_EducationPlanSet = "chis.application.her.schemas.HER_EducationPlanSet";
	public static final String HER_EducationPlanExe = "chis.application.her.schemas.HER_EducationPlanExe";
	public static final String HER_EducationRecord = "chis.application.her.schemas.HER_EducationRecord";
	public static final String HER_FileUpload = "chis.application.her.schemas.HER_FileUpload";
	public static final String HER_RecipelRecord = "chis.application.her.schemas.HER_RecipelRecord";
	public static final String HER_HealthEducationRecipel = "chis.application.her.schemas.HER_HealthEducationRecipel";
	public static final String SCH_SchistospmaManage = "chis.application.sch.schemas.SCH_SchistospmaManage";
	public static final String GDR_GroupDinnerRecord = "chis.application.gdr.schemas.GDR_GroupDinnerRecord";
	public static final String GDR_FirstGuide = "chis.application.gdr.schemas.GDR_FirstGuide";
	public static final String GDR_SecondGuide = "chis.application.gdr.schemas.GDR_SecondGuide";
	public static final String GDR_Visit = "chis.application.gdr.schemas.GDR_Visit";
	public static final String ADMIN_RecordRevert = "chis.application.hr.schemas.ADMIN_RecordRevert";
	public static final String EHR_PastHistorySearch = "chis.application.hr.schemas.EHR_PastHistorySearch";
	public static final String HC_LifestySituation = "chis.application.hc.schemas.HC_LifestySituation";
	public static final String HC_Examination = "chis.application.hc.schemas.HC_Examination";
	public static final String HC_HealthAssessment = "chis.application.hc.schemas.HC_HealthAssessment";
	public static final String HC_AccessoryExamination = "chis.application.hc.schemas.HC_AccessoryExamination";
	public static final String HC_NonimmuneInoculation = "chis.application.hc.schemas.HC_NonimmuneInoculation";
	public static final String MOV_EHR = "chis.application.mov.schemas.MOV_EHR";
	public static final String MOV_EHRApply = "chis.application.mov.schemas.MOV_EHRApply";
	public static final String MOV_EHRConfirm = "chis.application.mov.schemas.MOV_EHRConfirm";
	public static final String MOV_CDHConfirm = "chis.application.mov.schemas.MOV_CDHConfirm";
	public static final String MOV_CDHApply = "chis.application.mov.schemas.MOV_CDHApply";
	public static final String MOV_CDH = "chis.application.mov.schemas.MOV_CDH";
	public static final String MOV_MHCConfirm = "chis.application.mov.schemas.MOV_MHCConfirm";
	public static final String MOV_MHCApply = "chis.application.mov.schemas.MOV_MHCApply";
	public static final String MOV_MHC = "chis.application.mov.schemas.MOV_MHC";
	public static final String MOV_EHRManaInfoBatchChangeApply = "chis.application.mov.schemas.MOV_EHRManaInfoBatchChangeApply";
	public static final String MOV_EHRManaInfoBatchChangeConfirm = "chis.application.mov.schemas.MOV_EHRManaInfoBatchChangeConfirm";
	public static final String MOV_ManaInfoBatchChange = "chis.application.mov.schemas.MOV_ManaInfoBatchChange";
	public static final String MOV_ManaInfoBatchChangeDetail = "chis.application.mov.schemas.MOV_ManaInfoBatchChangeDetail";
	public static final String MOV_Manachangebyareagrid = "chis.application.mov.schemas.MOV_Manachangebyareagrid";
	public static final String MOV_Changeareagrid = "chis.application.mov.schemas.MOV_Changeareagrid";
	public static final String MOV_ManaInfoChangeApply = "chis.application.mov.schemas.MOV_ManaInfoChangeApply";
	public static final String MOV_ManaInfoChangeConfirm = "chis.application.mov.schemas.MOV_ManaInfoChangeConfirm";
	public static final String MOV_ManaInfoChange = "chis.application.mov.schemas.MOV_ManaInfoChange";
	public static final String MOV_ManaInfoChangeDetail = "chis.application.mov.schemas.MOV_ManaInfoChangeDetail";
	public static final String SYS_ZookeeperConfig = "chis.application.conf.schemas.SYS_ZookeeperConfig";
	public static final String MS_YJ02_CIC = "chis.application.his.schemas.MS_YJ02_CIC";
	// 问卷维护
	public static final String PHQ_GeneralCase = "chis.application.phq.schemas.PHQ_GeneralCase";
	public static final String PHQ_GeneralCaseListView = "chis.application.phq.schemas.PHQ_GeneralCaseListView";
	public static final String PHQ_GeneralCaseMany = "chis.application.phq.schemas.PHQ_GeneralCaseMany";
	public static final String PHQ_AnswerRecord = "chis.application.phq.schemas.PHQ_AnswerRecord";
	public static final String PHQ_HealthEducationCourse = "chis.application.phq.schemas.PHQ_HealthEducationCourse";
	public static final String PHQ_AttendPersonnel = "chis.application.phq.schemas.PHQ_AttendPersonnel";
	
	// 肿瘤模块
	public static final String MDC_TumourInspectionItem = "chis.application.tr.schemas.MDC_TumourInspectionItem";
	public static final String MDC_QuestionnaireCriterion = "chis.application.tr.schemas.MDC_QuestionnaireCriterion";
	public static final String MDC_QuestCriterionDetail = "chis.application.tr.schemas.MDC_QuestCriterionDetail";
	public static final String MDC_TumourHighRiskCriterion = "chis.application.tr.schemas.MDC_TumourHighRiskCriterion";
	public static final String MDC_TumourHRCDetail = "chis.application.tr.schemas.MDC_TumourHRCDetail";
	public static final String MDC_TumourSeemingly = "chis.application.tr.schemas.MDC_TumourSeemingly";
	public static final String MDC_TumourScreening = "chis.application.tr.schemas.MDC_TumourScreening";
	public static final String MDC_TumourScreeningCheckResult = "chis.application.tr.schemas.MDC_TumourScreeningCheckResult";
	public static final String MDC_TumourHighRisk = "chis.application.tr.schemas.MDC_TumourHighRisk";
	public static final String MDC_TumourHighRiskGroup = "chis.application.tr.schemas.MDC_TumourHighRiskGroup";
	public static final String MDC_TumourHighRiskVisit = "chis.application.tr.schemas.MDC_TumourHighRiskVisit";
	public static final String MDC_TumourConfirmed = "chis.application.tr.schemas.MDC_TumourConfirmed";
	public static final String MDC_TumourConfirmedPageList = "chis.application.tr.schemas.MDC_TumourConfirmedPageList";//对应MDC_TumourConfirmed
	public static final String MDC_TumourConfirmedListView = "chis.application.tr.schemas.MDC_TumourConfirmedListView";//对应MDC_TumourConfirmed
	public static final String MDC_TumourPrecancerListView = "chis.application.tr.schemas.MDC_TumourPrecancerListView";//对应MDC_TumourConfirmed
	public static final String MDC_TumourExpertReview = "chis.application.tr.schemas.MDC_TumourExpertReview";
	public static final String MDC_TumourPatientReportCard = "chis.application.tr.schemas.MDC_TumourPatientReportCard";
	public static final String MDC_TumourPatientBaseCase = "chis.application.tr.schemas.MDC_TumourPatientBaseCase";
	public static final String MDC_TumourPatientFirstVisit = "chis.application.tr.schemas.MDC_TumourPatientFirstVisit";// 对应MDC_TumourPatientVisit
	public static final String MDC_TumourPatientVisit = "chis.application.tr.schemas.MDC_TumourPatientVisit";
	public static final String MDC_TumourPastMedicalHistory = "chis.application.tr.schemas.MDC_TumourPastMedicalHistory";
	
	public static final String MDC_TumourHighRiskVisitPlanListView = "chis.application.tr.schemas.MDC_TumourHighRiskVisitPlanListView";//高危随访列表（计划）
	public static final String MDC_TumourHighRiskVisitPlanManagerListView = "chis.application.tr.schemas.MDC_TumourHighRiskVisitPlanManagerListView";//高危随访列表（计划）
	
	// 中医管理-中医体质辨识问卷
	public static final String TCMQuestionnaireOldPeople = "chis.application.tcm.schemas.TCMQuestionnaireOldPeople";
	public static final String TCMQuestionnaireCommonUse = "chis.application.tcm.schemas.TCMQuestionnaireCommonUse";
	public static final String TCM_TCMRegister = "chis.application.tcm.schemas.TCM_TCMRegister";
	public static final String TCM_IFCQResult = "chis.application.tcm.schemas.TCM_IFCQResult";
	public static final String TCM_IFCQResultDetail = "chis.application.tcm.schemas.TCM_IFCQResultDetail";
	public static final String TCM_SickGuidance = "chis.application.tcm.schemas.TCM_SickGuidance";
	public static final String TCM_GuidanceContentLibrary = "chis.application.tcm.schemas.TCM_GuidanceContentLibrary";

	//健康检查HTML
	public static final String HC_HealthExamination = "chis.application.hc.schemas.HC_HealthExamination";
	
	//维护日志
	public static final String LOG_EHR_VindicateNumber = "chis.application.log.schemas.LOG_EHR_VindicateNumber";
	//助理关联的家庭医生
	public static final String REL_RelevanceDoctor = "chis.application.rel.schemas.REL_RelevanceDoctor";
	public static final String REL_RelevanceDoctor_list = "chis.application.rel.schemas.REL_RelevanceDoctor_list";
	
	// 离休干部档案
	public static final String RVC_RetiredVeteranCadresRecord = "chis.application.rvc.schemas.RVC_RetiredVeteranCadresRecord";
	public static final String RVC_RetiredVeteranCadresVisit = "chis.application.rvc.schemas.RVC_RetiredVeteranCadresVisit";

	// 服务记录动态模板
	public static final String EHR_DictionaryMaintain = "chis.application.fhr.schemas.EHR_DictionaryMaintain";
	public static final String EHR_FieldMasterRelation = "chis.application.fhr.schemas.EHR_FieldMasterRelation";
	public static final String EHR_FieldMaintain = "chis.application.fhr.schemas.EHR_FieldMaintain";
	public static final String EHR_MasterplateMaintain = "chis.application.fhr.schemas.EHR_MasterplateMaintain";
	public static final String EHR_FieldData = "chis.application.fhr.schemas.EHR_FieldData";
	public static final String EHR_MasterplateData = "chis.application.fhr.schemas.EHR_MasterplateData";
	
	public static final String INC_IncompleteRecord ="chis.application.inc.schemas.INC_IncompleteRecord";
	public static final String CONS_ConsultationRecord ="chis.application.cons.schemas.CONS_ConsultationRecord";
	
	//高血压配置参数
	public static final String MDC_HypertensionAssessParamete = "chis.application.hy.schemas.MDC_HypertensionAssessParamete";
	public static final String MDC_HypertensionBPControl = "chis.application.hy.schemas.MDC_HypertensionBPControl";
	public static final String MDC_HypertensionControl = "chis.application.hy.schemas.MDC_HypertensionControl";

	// 肺结核
//	public static final String TB_TuberculosisVisit = "chis.application.tb.schemas.TB_TuberculosisVisit";
//	public static final String TB_TuberculosisFirstVisit = "chis.application.tb.schemas.TB_TuberculosisFirstVisit";

	// @@ 没有schema的。
	public static final String ZX_CARD_BASEINFO = "ZX_CARD_BASEINFO";
	public static final String ZX_SMK_GA = "ZX_SMK_GA";
	public static final String PUB_PlanType = "chis.application.pub.schemas.PUB_PlanType";
	public static final String FileResources = "FileResources";
	public static final String PUB_PlanInstance = "chis.application.pub.schemas.PUB_PlanInstance";
	public static final String HC_HealthCheck = "chis.application.hc.schemas.HC_HealthCheck";
	public static final String HC_InhospitalSituation = "chis.application.hc.schemas.HC_InhospitalSituation";
	public static final String HC_MedicineSituation = "chis.application.hc.schemas.HC_MedicineSituation";

	// @@ 没有mapping的。
	public static final String EHR_CommonTaskList = "chis.application.wl.schemas.EHR_CommonTaskList";
	public static final String EHR_MHCTaskList = "chis.application.wl.schemas.EHR_MHCTaskList";
	public static final String EHR_CDHTaskList = "chis.application.wl.schemas.EHR_CDHTaskList";
	public static final String ADMIN_ChildrenConfig = "chis.application.conf.schemas.ADMIN_ChildrenConfig";
	public static final String ADMIN_ChildrenConfigDetail = "chis.application.conf.schemas.ADMIN_ChildrenConfigDetail";
	public static final String ADMIN_DebilityChildrenConfig = "chis.application.conf.schemas.ADMIN_DebilityChildrenConfig";
	public static final String ADMIN_DebilityChildrenConfigDetail = "chis.application.conf.schemas.ADMIN_DebilityChildrenConfigDetail";
	public static final String ADMIN_DiabetesConfig = "chis.application.conf.schemas.ADMIN_DiabetesConfig";
	public static final String ADMIN_DiabetesRiskConfig = "chis.application.conf.schemas.ADMIN_DiabetesRiskConfig";
	public static final String ADMIN_HypertensionConfig = "chis.application.conf.schemas.ADMIN_HypertensionConfig";
	public static final String ADMIN_HypertensionRiskConfig = "chis.application.conf.schemas.ADMIN_HypertensionRiskConfig";
	public static final String ADMIN_OldPeopleFormConfig = "chis.application.conf.schemas.ADMIN_OldPeopleFormConfig";
	public static final String ADMIN_OldPeopleListConfig = "chis.application.conf.schemas.ADMIN_OldPeopleListConfig";
	public static final String ADMIN_PregnantConfig = "chis.application.conf.schemas.ADMIN_PregnantConfig";
	public static final String ADMIN_PregnantConfigDetail = "chis.application.conf.schemas.ADMIN_PregnantConfigDetail";
	public static final String ADMIN_PsychosisConfigDetail = "chis.application.conf.schemas.ADMIN_PsychosisConfigDetail";
	public static final String ADMIN_PsychosisConfig = "chis.application.conf.schemas.ADMIN_PsychosisConfig";
	public static final String SYS_InterfaceConfig = "chis.application.conf.schemas.SYS_InterfaceConfig";
	public static final String SYS_CommonConfig = "chis.application.conf.schemas.SYS_CommonConfig";
	public static final String MOV_PeopleRecordsQuery = "chis.application.mov.schemas.MOV_PeopleRecordsQuery";
	public static final String ADMIN_TumourHighRiskConfig = "chis.application.conf.schemas.ADMIN_TumourHighRiskConfig";
	public static final String ADMIN_TumourHighRiskConfigDetail = "chis.application.conf.schemas.ADMIN_TumourHighRiskConfigDetail";
	public static final String ADMIN_TumourPatientVisitConfig = "chis.application.conf.schemas.ADMIN_TumourPatientVisitConfig";
	public static final String ADMIN_TumourPatientVisitConfigDetail = "chis.application.conf.schemas.ADMIN_TumourPatientVisitConfigDetail";
	public static final String SYS_ManageTypeConfig = "chis.application.conf.schemas.SYS_ManageTypeConfig";
	public static final String PSY_PsychosisRecordWorkList = "chis.application.psy.schemas.PSY_PsychosisRecordWorkList";
	public static final String PUB_VisitPlanFamilyList = "chis.application.fhr.schemas.PUB_VisitPlanFamilyList";
	//

	// **------------------移动扩张业务（家医服务）相关表名--start----------------------------//
	public static final String MPM_MasterplateData = "chis.application.mpm.schemas.MPM_MasterplateData";
	public static final String MPM_FieldData = "chis.application.mpm.schemas.MPM_FieldData";
	public static final String MPM_FieldMaintain = "chis.application.mpm.schemas.MPM_FieldMaintain";
	public static final String MPM_DictionaryMaintain = "chis.application.mpm.schemas.MPM_DictionaryMaintain";
	public static final String MPM_MasterplateMaintain = "chis.application.mpm.schemas.MPM_MasterplateMaintain";
	public static final String MPM_FieldMasterRelation = "chis.application.mpm.schemas.MPM_FieldMasterRelation";
	public static final String EHR_FamilyContractBase = "chis.application.fhr.schemas.EHR_FamilyContractBase";
	public static final String EHR_FamilyContractService = "chis.application.fhr.schemas.EHR_FamilyContractService";
	public static final String EHR_FamilyContractRecord = "chis.application.fhr.schemas.EHR_FamilyContractRecord";
	public static final String MDC_OldPeopleVisitSearch = "chis.application.ohr.schemas.MDC_OldPeopleVisitSearch";
	public static final String MDC_HypertensionVisitSearch = "chis.application.hy.schemas.MDC_HypertensionVisitSearch";
	public static final String MDC_DiabetesVisitSearch = "chis.application.dbs.schemas.MDC_DiabetesVisitSearch";
	public static final String PUB_DataValidity = "chis.application.pub.schemas.PUB_DataValidity";
	public static final String DLL_DownLoadLog = "chis.application.mobile.schemas.DLL_DownLoadLog";
	public static final String DLL_UpLoadLog = "chis.application.mobile.schemas.DLL_UpLoadLog";

	// **------------------移动扩张业务（家医服务）相关表名--end----------------------------//

	public static final String DIV_MasterplateData = "chis.application.div.schemas.DIV_MasterplateData";
	public static final String DIV_FieldData = "chis.application.div.schemas.DIV_FieldData";
	public static final String DIV_FieldMaintain = "chis.application.div.schemas.DIV_FieldMaintain";
	public static final String DIV_DictionaryMaintain = "chis.application.div.schemas.DIV_DictionaryMaintain";
	public static final String DIV_MasterplateMaintain = "chis.application.div.schemas.DIV_MasterplateMaintain";
	public static final String DIV_FieldMasterRelation = "chis.application.div.schemas.DIV_FieldMasterRelation";

	
	
	// **------------------公共卫生系统2.4相关表名--start----------------------------//
	public static final String HER_HealthRecipeRecord = "chis.application.her.schemas.HER_HealthRecipeRecord";
	public static final String HER_HealthRecipeRecord_MZ = "chis.application.her.schemas.HER_HealthRecipeRecord_MZ";
	public static final String HER_HealthRecipeRecord_GXYSF = "chis.application.her.schemas.HER_HealthRecipeRecord_GXYSF";
	public static final String HER_HealthRecipeRecord_TNBSF = "chis.application.her.schemas.HER_HealthRecipeRecord_TNBSF";
	public static final String HER_HealthRecipeRecord_ZLSF = "chis.application.her.schemas.HER_HealthRecipeRecord_ZLSF";
	public static final String HER_HealthRecipeRecord_JSBSF = "chis.application.her.schemas.HER_HealthRecipeRecord_JSBSF";
	public static final String HER_HealthRecipeRecord_JHZX = "chis.application.her.schemas.HER_HealthRecipeRecord_JHZX";
	public static final String HER_HealthRecipeRecord_GXYGWSF = "chis.application.her.schemas.HER_HealthRecipeRecord_GXYGWSF";
	public static final String MDC_ChineseMedicineManage = "chis.application.ohr.schemas.MDC_ChineseMedicineManage";
	public static final String CDH_ChildVisitRecord = "chis.application.cdh.schemas.CDH_ChildVisitRecord";
	public static final String CDH_ChildVisitInfo = "chis.application.cdh.schemas.CDH_ChildVisitInfo";
	public static final String PUB_PelpleHealthDiagnose = "chis.application.pub.schemas.PUB_PelpleHealthDiagnose";
	public static final String QUALITY_ZK = "chis.application.quality.schemas.QUALITY_ZK";
	public static final String QUALITY_ZK_GXSD = "chis.application.quality.schemas.QUALITY_ZK_GXSD";
	public static final String QUALITY_ZK_ZQ = "chis.application.quality.schemas.QUALITY_ZK_ZQ";
	public static final String QUALITY_ZK_SJ = "chis.application.quality.schemas.QUALITY_ZK_SJ";
	public static final String QUALITY_ZK_TNB = "chis.application.quality.schemas.QUALITY_ZK_TNB";

	public static final String EHR_RecordInfo = "chis.application.common.schemas.EHR_RecordInfo";
	public static final String CVD_DiseaseManagement = "chis.application.cvd.schemas.CVD_DiseaseManagement";
	public static final String CVD_DiseaseVerification = "chis.application.cvd.schemas.CVD_DiseaseVerification";
	public static final String BASE_UserRoles = "chis.application.sys.schemas.BASE_UserRoles";
	
	//乡村医生签约服务
	public static final String PER_XCQYFW = "chis.application.fhr.schemas.PER_XCQYFW";
	//his慢病核实
	public static final String MDC_FromHis = "chis.application.wl.schemas.MDC_FromHis";
	//慢病删除记录
	public static final String MDC_Deletemdc = "chis.application.wl.schemas.MDC_Deletemdc";
	//健康小屋自测信息
	public static final String VIEW_ZJ_RECORD = "chis.application.hc.schemas.VIEW_ZJ_RECORD";
	//孕前检查
	public static final String hc_w_progestationask = "chis.application.hc.schemas.hc_w_progestationask";
	public static final String hc_w_progestationcheck = "chis.application.hc.schemas.hc_w_progestationcheck";
	public static final String hc_m_progestationask = "chis.application.hc.schemas.hc_m_progestationask";
	public static final String hc_m_progestationcheck = "chis.application.hc.schemas.hc_m_progestationcheck";
	//预约挂号
	public static final String HIS_AppointmentRecord = "chis.application.his.schemas.HIS_AppointmentRecord";
	public static final LinkedHashMap<String,String> RECORDSMAP_CHIS =new LinkedHashMap<String,String>(){
		{
				 put("GRDA","B_01");     
				 put("GRQY","JY_01");    
				 put("GAO","C_01");     
				 put("TANG","D_01");    
				 put("LAO","B_07");     
				 put("LI","R_01");      
				 put("YI","T_01");   
//				 put("XIAN","T_06");    
//				 put("BAO","T_04");    
				 put("JING","P_01");    
				 put("ER","H_01");      
				 put("RUO","H_09");     
				 put("FU","G_01");      
				 put("CANZT","DEF_01");  
				 put("CANN","DEF_02");  
				 put("CANZL","DEF_03");
				 put("PC","PC_03_02");
		 }};
	public static final LinkedHashMap<String,String> RECORDSMAP_PHIS =new LinkedHashMap<String,String>(){
				 {
						 put("GRDA","A01");
						 put("GRQY","A31");
						 put("GAO","A03");
						 put("TANG","A07");
						 put("LAO","A09"); 
						 put("LI","A32"); 
						 put("YI","A33");     
//						 put("XIAN","A34");   
//						 put("BAO","A41");   
						 put("JING","A35");
						 put("ER","A15");
						 put("RUO","A36");
						 put("FU","A37");
						 put("CANZT","A38");   
						 put("CANN","A39");    
						 put("CANZL","A40");   
				 }
	};
	
	public static final LinkedHashMap<String,String> RECORDSMAP_ZLYH =new LinkedHashMap<String,String>(){
		 {
				 put("1","YI_DC");     
				 put("2","YI_WEI");    
				 put("3","YI_GAN");     
				 put("4","YI_FEI");    
				 put("5","YI_RX");     
				 put("6","YI_GJ");     
		 }};
	public static final LinkedHashMap<String,String> RECORDSMAP_ZLXH =new LinkedHashMap<String,String>(){
		 {
				 put("1","XIAN_DC");     
				 put("2","XIAN_WEI");    
				 put("3","XIAN_GAN");     
				 put("4","XIAN_FEI");    
				 put("5","XIAN_RX");     
				 put("6","XIAN_GJ");     
		 }};	
	
	//dic
	public static final String PWI_DIC = "chis.dictionary.pregnantIndex";//孕妇指标
	
	public static final String DR_DemographicInfo = "chis.application.dr.schemas.DR_DemographicInfo";
	public static final String DR_Referrals = "chis.application.dr.schemas.DR_Referrals";
	public static final String drIt_sendExchangeReport = "chis.application.dr.schemas.drIt_sendExchangeReport";
	//家庭团队
	public static final String PUB_FamilyTeam = "chis.application.pub.schemas.PUB_FamilyTeam";
	//高危档案
	public static final String MDC_HighRiskRecord="chis.application.hq.schemas.MDC_HighRiskRecord";
	public static final String MDC_HighRiskVisit="chis.application.hq.schemas.MDC_HighRiskVisit";
	public static final String REL_ResponsibleDoctor="chis.application.rel.schemas.REL_ResponsibleDoctor";


	public static final String JKTJ_TJFW = "JKTJ_TJFW";//健康体检服务

	public static final String ETJD_ETFW = "ETJD_ETFW";//儿童建档服务
	public static final String ETJKJC_ETFWONE = "ETJKJC_ETFWONE";//1～8月龄儿童健康检查服务
	public static final String ETJKJC_ETFWTOW = "ETJKJC_ETFWTOW";//12~30月儿童健康检查服务
	public static final String ETJKJC_ETFWTHREE = "ETJKJC_ETFWTHREE";//3~6岁儿童健康检查服务

	public static final String TNBDAGL_TNBFW = "TNBDAGL_TNBFW";//糖尿病档案管理服务
	public static final String TNBSF_TNBFW = "TNBSF_TNBFW";//糖尿病随访服务

	public static final String SSZCY_GXYFW = "SSZCY_GXYFW";//35岁首诊测压服务
	public static final String GXYDAGL_GXYFW = "GXYDAGL_GXYFW";//高血压档案管理服务
	public static final String GXYSF_GXYFW = "GXYSF_GXYFW";//高血压随访服务

	public static final String YFJDHDYCCJ_YCFFW = "YFJDHDYCCJ_YCFFW";//孕妇建档和第一次产检服务
	public static final String CJ_YCFFW = "CJ_YCFFW";//产检服务
	public static final String CHFS_YCFFW = "CHFS_YCFFW";//产后访视服务
	public static final String CH42TFS_YCFFW = "CH42TFS_YCFFW";//产后42天访视服务

	public static final String LNRDAGL_LNRFW = "LNRDAGL_LNRFW";//老年人档案管理服务
	public static final String LNRZLPG_LNRFW = "LNRZLPG_LNRFW";//老年人自理评估服务

	public static final String JMJKDA_GLFW = "JMJKDA_GLFW";//居民健康档案管理服务

	public static final String JSBSF_JSBFW = "JSBSF_JSBFW";//严重严重精神障碍患者障碍患者随访服务
	public static final String ETZYYJKZD_ZYYFW = "ETZYYJKZD_ZYYFW";//儿童中医药健康指导服务

	public static final String SCM_SignContractRecord = "chis.application.scm.schemas.SCM_SignContractRecord";
	public static final String SCM_ServiceItems = "chis.application.scm.schemas.SCM_ServiceItems";
	public static final String SCM_ServiceContractPlanTask = "chis.application.scm.schemas.SCM_ServiceContractPlanTask";
	public static final String SCM_ServicePackageItems = "chis.application.scm.schemas.SCM_ServicePackageItems";
	public static final String SCM_SignContractPackage = "chis.application.scm.schemas.SCM_SignContractPackage";
	public static final String SCM_ServicePlan = "chis.application.scm.schemas.SCM_ServicePlan";
	public static final String SCM_ServicePlanTask = "chis.application.scm.schemas.SCM_ServicePlanTask";
	public static final String SCM_ServiceRecords = "chis.application.scm.schemas.SCM_ServiceRecords";
	public static final String SCM_ServicePackage = "chis.application.scm.schemas.SCM_ServicePackage";
	public static final String SCM_NewService = "chis.application.scm.schemas.SCM_NewService";
	
	public static final String HIVS_Screening = "chis.application.hivs.schemas.HIVS_Screening";
	public static final String MZF_VisitRecord = "chis.application.mzf.schemas.MZF_VisitRecord";
	public static final String MZF_DocumentRecord = "chis.application.mzf.schemas.MZF_DocumentRecord";
	
	public static final String MDC_HyBaseline = "chis.application.hy.schemas.MDC_HyBaseline";
}
