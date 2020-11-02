<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.tr.TR" name="肿瘤管理"  type="1"> 
	<catagory id="TR" name="肿瘤管理"> 
		<module id="TR01" name="问卷标准维护" script="chis.application.tr.script.sms.TumourQuestionnaireCriterionList"> 
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_QuestionnaireCriterion</p>  
				<p name="refModule">chis.application.tr.TR/TR/TR01_01</p> 
			</properties>  
			<action id="createCriterion" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TR01_01" name="问卷标准维护器" type="1" script="chis.application.tr.script.sms.TumourQuestionnaireCriterionModule">
			<action id="QCForm" name="问卷标准表单" ref="chis.application.tr.TR/TR/TR01_0101"/>  
			<action id="QCDModule" name="问卷标准明细选择器" ref="chis.application.tr.TR/TR/TR01_0102"/>  
		</module>
		<module id="TR01_0101" name="问卷标准表单" type="1" script="chis.application.tr.script.sms.TumourQuestionnaireCriterionForm">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_QuestionnaireCriterion</p>
			</properties>
			<action id="save" name="保存"/>  
		</module>
		<module id="TR01_0102" name="问卷标准明细选择器" type="1" script="chis.application.tr.script.sms.TumourQuestionnaireCriterionDetailModule">
			<action id="QFieldList" name="问卷题目集" ref="chis.application.tr.TR/TR/TR01_0102_01"/>
			<action id="QItemList" name="问卷题项集" ref="chis.application.tr.TR/TR/TR01_0102_02"/>
			<action id="QCDList" name="问卷标准明细集" ref="chis.application.tr.TR/TR/TR01_0102_03"/>  
		</module>
		<module id="TR01_0102_01" name="问卷题目集" type="1" script="chis.application.tr.script.sms.QuestCriterionFieldMaintainList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_QuestCriterionFieldMaintain</p>
			</properties>
		</module>
		<module id="TR01_0102_02" name="问卷题项集" type="1" script="chis.application.tr.script.sms.QuestCriterionDictionaryMaintainList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_QuestCriterionDictionaryMaintain</p>
			</properties>
		</module>
		<module id="TR01_0102_03" name="问卷标明细准集" type="1" script="chis.application.tr.script.sms.QuestCriterionDetailList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_QuestCriterionDetail</p>
			</properties>
		</module>
		<module id="TR01_02" name="健康教育课程" script="chis.application.tr.script.phq.TumourHealthEducationCourseList">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_HealthEducationCourse</p>
				<p name="refTHECForm">chis.application.tr.TR/TR/TR01_0201</p>
				<p name="refListenerRegisterModule">chis.application.tr.TR/TR/TR01_0202</p>
			</properties>
			<action id="createTHECForm" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="listenerRegister" name="听课人员登记" iconCls="hypertension_check"/>
			<action id="remove" name="删除"/>
		</module>
		<module id="TR01_0201" name="健康教育课程表单" type="1" script="chis.application.tr.script.phq.TumourHealthEducationCourseForm">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_HealthEducationCourse</p>
				<p name="saveServiceId">chis.tumourHealthEducationCourseService</p>
				<p name="saveAction">saveTHECourse</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR01_0202" name="听课人员登记" type="1" script="chis.application.tr.script.phq.TumourHealthEducationListenerRegisterModule">
			<properties> 
				<p name="TPHQModule">chis.application.tr.TR/TR/THQ</p>
				<p name="THQMModule">chis.application.tr.TR/TR/THQM</p>
			</properties>
			<action id="thecInfo" name="课程信息" ref="chis.application.tr.TR/TR/TR01_0202_01"/>
			<action id="theListenersList" name="听课人员列表" ref="chis.application.tr.TR/TR/TR01_0202_02"/>
			<action id="HQOfListener" name="听课人员的问卷" ref="chis.application.tr.TR/TR/TR01_0202_03"/>
		</module>
		<module id="TR01_0202_01" name="课程信息" type="1" script="chis.application.tr.script.phq.TumourHealthEducationCourseInfo">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_HealthEducationCourseShow</p>
			</properties>
		</module>
		<module id="TR01_0202_02" name="听课人员列表" type="1" script="chis.application.tr.script.phq.TumourHealthEducationListenersList">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_AttendPersonnel</p>
				<p name="TPHQModule">chis.application.tr.TR/TR/THQ</p>
				<p name="removeServiceId">chis.tumourHealthEducationCourseService</p>
				<p name="removeAction">removeAttendPersonnel</p>
			</properties>
			<action id="createListener" name="新增" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TR01_0202_03" name="听课人员的问卷" type="1" script="chis.application.tr.script.phq.TumourHealthQuestionList">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_GeneralCaseShow</p>
				<p name="TPHQModule">chis.application.tr.TR/TR/THQ</p>
				<p name="removeServiceId">chis.tumourQuestionnaireService</p>
				<p name="removeAction">removeTumourQuestionnaireRecord</p>
			</properties>
			<action id="healthQuestion" name="新增问卷" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TR01_03" name="肿瘤健康问卷" script="chis.application.tr.script.phq.TumourHealthQuestionListView">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_GeneralCaseListView</p>
				<p name="TPHQModule">chis.application.tr.TR/TR/THQ</p>
				<p name="listServiceId">chis.tumourQuestionnaireService</p>
				<p name="listAction">loadTHQRecord</p>
				<p name="removeServiceId">chis.tumourQuestionnaireService</p>
				<p name="removeAction">removeTumourQuestionnaireRecord</p>
				<p name="oneKeyRemoveAction">oneKeyRemoveTQAndTS</p>
				<p name="saveServiceId">chis.tumourQuestionnaireService</p>
				<p name="writeOffAction">logoutTumourQuestionnaireRecord</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/> 
			<!-- action id="writeOff" name="注销" iconCls="common_writeOff"/ --> 
			<action id="remove" name="删除"/>
			<action id="oneKeyRemove" name="一键删除"  iconCls="remove"/>
		</module>
		<module id="TR02" name="筛查检查项目维护" script="chis.application.tr.script.inspectionItem.TumourInspectionItemList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourInspectionItem</p> 
				<p name="refPhisForm">chis.application.tr.TR/TR/TR02_01</p>
				<p name="refChisForm">chis.application.tr.TR/TR/TR02_02</p>
			</properties>  
			<action id="createTII" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TR02_01" name="筛查检查项目维护表单" script="chis.application.tr.script.inspectionItem.TumourInspectionItemForm">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourInspectionItem</p> 
				<p name="saveServiceId">chis.tumourInspectionItemService</p>
				<p name="saveAction">saveTumourInspectionItem</p>
			</properties> 
			<action id="save" name="保存"/> 
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="TR02_02" name="筛查检查项目维护表单" script="chis.application.tr.script.inspectionItem.TumourInspectionItemInputForm">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourInspectionItemInput</p> 
				<p name="saveServiceId">chis.tumourInspectionItemService</p>
				<p name="saveAction">saveTumourInspectionItem</p>
			</properties> 
			<action id="save" name="保存"/> 
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="TR03" name="肿瘤高危确诊维护" script="chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskCriterion</p> 
				<p name="refModule">chis.application.tr.TR/TR/TR03_01</p> 
				<p name="removeServiceId">chis.tumourCriterionService</p>
				<p name="removeAction">removeTHRCriterion</p>
			</properties>
			<action id="createHRC" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TR03_01" name="肿瘤高危确诊维护维护器" type="1" script="chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionModule">
			<action id="THCList" name="肿瘤高危确诊维护同类列表" ref="chis.application.tr.TR/TR/TR03_0101"/>  
			<action id="THCForm" name="肿瘤高危确诊维护" ref="chis.application.tr.TR/TR/TR03_0102" type="tab"/>  
			<action id="THCDList" name="检查标准列表" ref="chis.application.tr.TR/TR/TR03_0103" type="tab"/>
			<action id="THCQList" name="问卷标准列表" ref="chis.application.tr.TR/TR/TR01" type="tab"/>
		</module>
		<module id="TR03_0101" name="肿瘤高危确诊维护同类列表" type="1" script="chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionSameKindList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskCriterionSameKind</p> 
			</properties>
		</module>
		<module id="TR03_0102" name="肿瘤高危确诊维护表单" type="1" script="chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionForm">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskCriterion</p>
				<p name="saveServiceId">chis.tumourCriterionService</p>
				<p name="saveAction">saveTHRCriterion</p>
			</properties>
			<action id="save" name="保存"/> 
			<action id="create" name="新建" iconCls="create"/>  
		</module>
		<module id="TR03_0103" name="检查标准维护列表" type="1" script="chis.application.tr.script.highRiskCriterion.TumourHRCDetailList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHRCDetail</p>
				<p name="createCls">chis.application.tr.script.highRiskCriterion.TumourHRCDetailForm</p>  
				<p name="updateCls">chis.application.tr.script.highRiskCriterion.TumourHRCDetailForm</p>  
			</properties>
			<action id="create" name="新建" iconCls="create"/>  
			<action id="update" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TR04" name="T疑似人群管理" script="chis.application.tr.script.seemingly.TumourSeeminglyList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourSeemingly</p>
				<p name="refModule">chis.application.tr.TR/TR/TR04_01</p> 
				<p name="refRecheckModule">chis.application.tr.TR/TR/TR04_02</p> 
			</properties>
			<action id="createTS" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="recheck" name="复核" iconCls="common_writeOffCheck"/>
		</module>
		<module id="TR04_01" name="疑似肿瘤" type="1" script="chis.application.tr.script.seemingly.TumourSeeminglyForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourSeemingly</p>
				<p name="saveServiceId">chis.TumourSeeminglyService</p>
				<p name="saveAction">saveTumourSeemingly</p>
			</properties>
			<action id="save" name="保存"/> 
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="TR04_02" name="疑似肿瘤复核" type="1" script="chis.application.tr.script.seemingly.TumourSeeminglyRecheckForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourSeeminglyRecheck</p>
				<p name="saveServiceId">chis.TumourSeeminglyService</p>
				<p name="saveAction">saveTumourSeeminglyRecheck</p>
			</properties>
			<action id="save" name="保存"/> 
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="TR05" name="T初筛人群管理" script="chis.application.tr.script.screening.TumourScreeningList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreeningListView</p>
				<p name="refModule">chis.application.tr.TR/TR/TR05_01</p> 
				<p name="refConfirmedModule">chis.application.tr.TR/TR/TR0701_01</p>
				<p name="refPMHModule">chis.application.tr.TR/TR/TR_PMHView</p>
				<p name="listServiceId">chis.tumourScreeningService</p>
				<p name="listAction">simpleQueryPAList</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="viewPMH" name="既往情况" iconCls="common_query"/>
			<action id="checkResultInput" name="检查录入" iconCls="hypertension_check"/>
			<action id="turnHighRisk" name="转高危" iconCls="common_turn"/>
			<action id="definiteDiagnosis" name="确诊" iconCls="common_writeOffCheck"/>
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>
			<action id="remove" name="删除"/>
		</module>
		<module id="TR05_01" name="T初筛人群管理器" type="1" script="chis.application.tr.script.screening.TumourScreeningModule">
			<properties>
				<p name="refConfirmedModule">chis.application.tr.TR/TR/TR0701_01</p>
			</properties>
			<action id="TSList" name="T初筛人群列表" ref="chis.application.tr.TR/TR/TR05_0101"/>  
			<action id="TSForm" name="T初筛人群表单" ref="chis.application.tr.TR/TR/TR05_0102" type="tab"/>  
			<action id="TSCRList" name="检查结果列表" ref="chis.application.tr.TR/TR/TR05_0103" type="tab"/>
			<action id="PMHM" name="既往史" ref="chis.application.tr.TR/TR/TR_PMH" type="tab"/>
		</module>
		<module id="TR05_0101" name="T初筛人群列表" type="1" script="chis.application.tr.script.screening.TumourScreeningSamePersonList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreeningSamePerson</p>
			</properties>
		</module>
		<module id="TR05_0102" name="T初筛人群表单" type="1" script="chis.application.tr.script.screening.TumourScreeningForm">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreening</p>
				<p name="saveServiceId">chis.tumourScreeningService</p>
				<p name="saveAction">saveTumourScreening</p>
			</properties>
			<action id="create" name="新建" iconCls="create" group="create"/>  
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR05_0103" name="检查结果列表" type="1" script="chis.application.tr.script.screening.TumourScreeningCheckResultList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreeningCheckResult</p>
				<p name="createCls">chis.application.tr.script.screening.TumourScreeningCheckResultForm</p>  
				<p name="updateCls">chis.application.tr.script.screening.TumourScreeningCheckResultForm</p>  
			</properties>
			<action id="create" name="检查结果录入"  iconCls="create" group="create"/>
			<action id="update" name="查看" iconCls="update" group="update"/>  
			<action id="remove" name="删除" group="update"/>
			<action id="turnHighRisk" name="转高危" iconCls="common_turn" group="update"/>
		</module>
		<module id="TR06" name="T高危人群管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRisk</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.tr.TR/TR/TR0601" />
		</module>
		<module id="TR0601" name="T高危人群管理" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskListView">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskListView</p>
				<p name="refModule">chis.application.tr.TR/TR/TR0601_01</p> 
				<p name="refConfirmedModule">chis.application.tr.TR/TR/TR0701_01</p>
				<p name="refPMHModule">chis.application.tr.TR/TR/TR_PMHView</p>
				<p name="listServiceId">chis.tumourHighRiskService</p>  
				<p name="listAction">loadTHRListView</p>  
			</properties>
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="viewPMH" name="既往情况" iconCls="common_query"/>
			<action id="definiteDiagnosis" name="确诊" iconCls="common_writeOffCheck"/>
			<action id="normal" name="正常" iconCls="common_turn"/>
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>
			<!-- action id="createTHR" name="新建" iconCls="create"/ -->  
		</module>
		<module id="TR0601_01" name="T高危人群管理器" type="1" script="chis.script.EHRView">
			<action id="THRBase" name="T高危管理卡" ref="chis.application.tr.TR/TR/TR0601_0101" />
			<action id="THRGroup" name="T高危定转组" ref="chis.application.tr.TR/TR/TR0601_0102" />
			<action id="THRVisit" name="T高危人群随访" ref="chis.application.tr.TR/TR/TR0601_0103" />
		</module>
		<module id="TR0601_0101" name="T高危管理卡" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskBaseModule">
			<action id="THRForm" name="T高危卡信息" ref="chis.application.tr.TR/TR/TR0601_0101_01"/>
			<action id="TCRList" name="T高危检查结果" ref="chis.application.tr.TR/TR/TR0601_0101_02"/>
		</module>
		<module id="TR0601_0101_01" name="T高危卡信息" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskBaseForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRisk</p>
				<p name="saveServiceId">chis.tumourHighRiskService</p>
				<p name="saveAction">saveTumourHighRiskRecord</p>
			</properties>
			<action id="save" name="保存" group="create||update" /> 
		</module>
		<module id="TR0601_0101_02" name="T高危检查结果" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskBaseList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreeningCheckResult</p>
			</properties>
		</module>
		<module id="TR0601_0102" name="T高危定转组" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskGroupList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskGroup</p>
			</properties>
		</module>
		<module id="TR0601_0103" name="T高危人群随访" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskVisitModule">
			<properties>
				<p name="refPMHModule">chis.application.tr.TR/TR/TR_PMHView</p>
			</properties>
			<action id="visitPlanList" name="随访计划列表" ref="chis.application.tr.TR/TR/TR0601_0103_01"/>
			<action id="visitForm" name="随访表单" ref="chis.application.tr.TR/TR/TR0601_0103_02"/>
		</module>
		<module id="TR0601_0103_01" name="随访计划列表" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
				<p name="serviceId">chis.tumourHighRiskVisitService</p>
			</properties>
		</module>
		<module id="TR0601_0103_02" name="随访表单" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskVisitForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskVisit</p>
				<p name="loadServiceId">chis.tumourHighRiskVisitService</p>  
				<p name="loadAction">getTHRVisit</p>  
				<p name="refRecipeImportModule">chis.application.psy.PSY/PSY/I01-1-2-3-1</p> 
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update" /> 
			<action id="printRecipe" name="打印健康处方"  iconCls="print" /> 
		</module>
		<module id="TR0602" name="T高危人群随访" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskVisitListView</p>
				<p name="manageUnitField">d.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.tr.TR/TR/TR0602_01" />
		</module>
		<module id="TR0602_01" name="T高危人群随访管理列表" type="1" script="chis.application.tr.script.highRisk.TumourHighRiskVisitPlanListView">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourHighRiskVisitPlanManagerListView</p>
				<p name="refPMHModule">chis.application.tr.TR/TR/TR_PMHView</p>
				<p name="listServiceId">chis.tumourHighRiskVisitService</p>  
				<p name="listAction">loadTHRVistPlanListView</p>  
			</properties>
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="viewPMH" name="既往情况" iconCls="common_query"/>
		</module>
		<module id="TR07" name="T确诊人群管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourConfirmed</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.tr.TR/TR/TR0701" />
		</module>
		<module id="TR0701" name="T确诊人群管理" type="1" script="chis.application.tr.script.confirmed.TumourConfirmedList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourConfirmedListView</p>
				<p name="refConfirmedModule">chis.application.tr.TR/TR/TR0701_01</p>
				<p name="reviewModule">chis.application.tr.TR/TR/TR0701_02</p>
				<p name="refPMHModule">chis.application.tr.TR/TR/TR_PMHView</p>
				<p name="listServiceId">chis.tumourConfirmedService</p>
				<p name="listAction">loadTCPageList</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="viewPMH" name="既往情况" iconCls="common_query"/>
			<action id="expertJudgment" name="专家评定" iconCls="update" /> 
			<action id="reportCard" name="报告卡" iconCls="common_writeOffCheck" />
			<action id="THRView" name="高危档案" iconCls="common_reset" />
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>
			<!-- action id="turnHighRisk" name="转高危" iconCls="common_turn"/>  -->
		</module>
		<module id="TR0701_M" name="肿瘤确诊模块" type="1" script="chis.application.tr.script.confirmed.TumourConfirmedModule">
			<action id="TCForm" name="肿瘤确诊表单" ref="chis.application.tr.TR/TR/TR0701_01"/>
			<action id="TCCheckupResultList" name="肿瘤确诊检查结果" ref="chis.application.tr.TR/TR/TR0701_03"/>
		</module>
		<module id="TR0701_01" name="肿瘤确诊表单" type="1" script="chis.application.tr.script.confirmed.TumourConfirmedForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourConfirmed</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR0701_02" name="专家评审" type="1" script="chis.application.tr.script.confirmed.TumourConfirmedReviewForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourExpertReview</p>
				<p name="loadServiceId">chis.tumourConfirmedService</p>
				<p name="loadAction">loadTumourConfirmedReview</p>
				<p name="saveServiceId">chis.tumourConfirmedService</p>
				<p name="saveAction">saveTumourConfirmedReview</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="TR0701_03" name="肿瘤确认检查记录" type="1" script="chis.application.tr.script.confirmed.TumourConfirmedCheckupList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreeningCheckResult</p>
			</properties>
		</module>
		<module id="TR0702" name="癌前期人群管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPrecancerListView</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.tr.TR/TR/TR0702_01" />
		</module>
		<module id="TR0702_01" name="癌前期列表" type="1" script="chis.application.tr.script.precancer.TumourPrecancerListView">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPrecancerListView</p>
				<p name="refConfirmedModule">chis.application.tr.TR/TR/TR0701_01</p>
				<p name="reviewModule">chis.application.tr.TR/TR/TR0701_02</p>
				<p name="refPMHModule">chis.application.tr.TR/TR/TR_PMHView</p>
				<p name="listServiceId">chis.tumourConfirmedService</p>
				<p name="listAction">loadTPrecancerPageList</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="viewPMH" name="既往情况" iconCls="common_query"/>
			<action id="turnHighRisk" name="转高危" iconCls="common_turn"/>
			<action id="definiteDiagnosis" name="确诊" iconCls="common_writeOffCheck"/>
			<!-- <action id="expertJudgment" name="专家评定" iconCls="update" /> -->
			<action id="THRView" name="高危档案" iconCls="common_reset" />
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>
		</module>
		<module id="TR08" name="T现患病人管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientReportCard</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.tr.TR/TR/TR0801" />
		</module>
		<module id="TR0801" name="现患病人管理列表" type="1" script="chis.application.tr.script.tprc.TumourPatientReportCardListView">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientReportCard</p>
				<p name="refModule">chis.application.tr.TR/TR/TR0801_01</p> 
			</properties>
			<action id="createTPC" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="createDie" name="死补" iconCls="create"/>
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>
		</module>
		<module id="TR0801_01" name="肿瘤患者报告卡" type="1" script="chis.application.tr.script.tprc.TumourPatientReportCardModule">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientReportCard</p>
				<p name="saveServiceId">chis.tumourPatientReportCardService</p>
				<p name="saveAction">saveTumourPatientReportCard</p>
			</properties>
			<action id="TPRCForm" name="肿瘤患者报告卡" ref="chis.application.tr.TR/TR/TR0801_0101" type="tab"/>  
			<action id="TPFBVForm" name="基本信息核实" ref="chis.application.tr.TR/TR/TR0801_0102" type="tab"/>
			<action id="TPFVForm" name="肿瘤患者首访" ref="chis.application.tr.TR/TR/TR0801_0103" type="tab"/>
		</module>
		<module id="TR0801_0101" name="肿瘤患者报告卡" type="1" script="chis.application.tr.script.tprc.TumourPatientReportCardForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientReportCard</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR0801_0101_2" name="肿瘤患者报告卡" type="1" script="chis.application.tr.script.tprc.TumourPatientReportCardForm2">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientReportCard2</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR0801_0102" name="首访基本信息" type="1" script="chis.application.tr.script.tprc.TumourPatientBaseCaseForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientBaseCase</p>
				<p name="loadServiceId">chis.tumourPatientReportCardService</p>
				<p name="loadAction">getTumourBaseCheckInfo</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR0801_0103" name="肿瘤患者首访" type="1" script="chis.application.tr.script.tprc.TumourPatientFirstVisitForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientFirstVisit</p>
				<p name="loadServiceId">chis.tumourPatientReportCardService</p>
				<p name="loadAction">getTumourFirstVisitInfo</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<module id="TR0801_02" name="肿瘤患者随访" type="1" script="chis.application.tr.script.tprc.TumourPatientVisitModule">
			<action id="TPFVPList" name="肿瘤患者随访计划" ref="chis.application.tr.TR/TR/TR0801_0201" />
			<action id="TPFVForm" name="肿瘤患者随访表单" ref="chis.application.tr.TR/TR/TR0801_0202" />
		</module>
		<module id="TR0801_0201" name="肿瘤患者随访计划" type="1" script="chis.application.tr.script.tprc.TumourPatientVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
				<p name="serviceId">chis.tumourPatientVisitService</p>
			</properties>
		</module>
		<module id="TR0801_0202" name="肿瘤患者随访表单" type="1" script="chis.application.tr.script.tprc.TumourPatientVisitForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPatientVisit</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
		</module>
		<!-- =================肿瘤既往史查看器================ -->
		<module id="TR_PMHView" name="既往史View" script="chis.application.tr.script.pmh.TumourPastMedicalHistoryView">
			<properties>
				<p name="saveServiceId">chis.tumourPatientReportCardService</p>
				<p name="saveAction">saveTumourPatientReportCard</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="THQModule" name="健康问卷" ref="chis.application.tr.TR/TR/TR_THQM" type="tab"/>  
			<action id="THCList" name="检查结果" ref="chis.application.tr.TR/TR/TR_TCRList" type="tab"/>
			<action id="TMHModule" name="既往史" ref="chis.application.tr.TR/TR/TR_PMH" type="tab"/>
		</module>
		<module id="TR_THQM" name="健康问卷" type="1" script="chis.application.tr.script.pmh.TumourHealthQuestionView">
			<action id="THQList" name="肿瘤健康问卷列表" ref="chis.application.tr.TR/TR/THQList"/> 
			<action id="THQForm" name="肿瘤健康问卷" ref="chis.application.tr.TR/TR/PMH_THQ"/> 
		</module>
		<module id="TR_TCRList" name="检查结果列表" type="1" script="chis.application.tr.script.screening.TumourScreeningCheckResultList">
			<properties> 
				<p name="entryName">chis.application.tr.schemas.MDC_TumourScreeningCheckResult</p>
				<p name="createCls">chis.application.tr.script.screening.TumourScreeningCheckResultSee</p>  
				<p name="updateCls">chis.application.tr.script.screening.TumourScreeningCheckResultSee</p>  
			</properties>
			<action id="update" name="查看" iconCls="update" op="read"/>  
		</module>
		<module id="TR_PMH" name="肿瘤既往史" type="1" script="chis.application.tr.script.pmh.TumourPastMedicalHistoryModule">
			<action id="PMHList" name="肿瘤既往史列表" ref="chis.application.tr.TR/TR/TR_PMH_List"/> 
			<action id="PMHForm" name="肿瘤既往史表单" ref="chis.application.tr.TR/TR/TR_PMH_Form"/> 
		</module>
		<module id="TR_PMH_List" name="肿瘤既往史列表" type="1" script="chis.application.tr.script.pmh.TumourPastMedicalHistoryList">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPastMedicalHistory</p>
			</properties>
		</module>
		<module id="TR_PMH_Form" name="肿瘤既往史表单" type="1" script="chis.application.tr.script.pmh.TumourPastMedicalHistoryForm">
			<properties>
				<p name="entryName">chis.application.tr.schemas.MDC_TumourPastMedicalHistory</p>
				<p name="saveServiceId">chis.tumourPastMedicalHistoryService</p>
				<p name="saveAction">saveTumourPMH</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="update"/> 
			<action id="create" name="新建" iconCls="create" group="create"/>  
		</module>
		<!-- =========问卷公用模块=======s==== -->
		<module id="THQList" name="肿瘤健康问卷列表"  type="1" script="chis.application.tr.script.phq.TumourHealthQuestionList">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_GeneralCaseShow</p>
				<p name="TPHQModule">chis.application.tr.TR/TR/THQ</p>
				<p name="removeServiceId">chis.tumourQuestionnaireService</p>
				<p name="removeAction">removeTumourQuestionnaireRecord</p>
			</properties>
		</module>
		<module id="PMH_THQ" name="肿瘤健康问卷" type="1" script="chis.application.tr.script.phq.TumourHealthQuestionModule">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="loadServiceId">chis.tumourQuestionnaireService</p>
				<p name="loadAction">loadTumourQuestionnaireData</p>
				<p name="saveServiceId">chis.tumourQuestionnaireService</p>
				<p name="saveAction">saveTumourQuestionnaireData</p>
			</properties>
			<action id="HQGCForm" name="基本信息卷" ref="chis.application.tr.TR/TR/HQGCForm"/>
			<action id="HQForm" name="相关问题卷" ref="chis.application.tr.TR/TR/HQForm"/>
		</module>
		<module id="THQ" name="肿瘤健康问卷" type="1" script="chis.application.tr.script.phq.TumourHealthQuestionModule">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="loadServiceId">chis.tumourQuestionnaireService</p>
				<p name="loadAction">loadTumourQuestionnaireData</p>
				<p name="saveServiceId">chis.tumourQuestionnaireService</p>
				<p name="saveAction">saveTumourQuestionnaireData</p>
			</properties>
			<action id="HQGCForm" name="基本信息卷" ref="chis.application.tr.TR/TR/HQGCForm"/>
			<action id="HQForm" name="相关问题卷" ref="chis.application.tr.TR/TR/HQForm"/>
			<action id="save" name="确定" group="create||update"/> 
		</module>
		<module id="HQGCForm" name="基本信息卷" type="1" script="chis.application.tr.script.phq.HealthQuestionGeneralCaseForm">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_GeneralCase</p> 
			</properties> 
		</module>
		<module id="HQForm" name="相关问题卷" type="1" script="chis.application.tr.script.phq.HealthQuestionsForm">
			
		</module>
		<module id="THQM" name="肿瘤健康问卷" type="1" script="chis.application.tr.script.phq.TumourHealthQuestionManyModule">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="loadServiceId">chis.tumourQuestionnaireService</p>
				<p name="loadAction">initTumourQuestionnaireBaseInfo</p>
				<p name="saveServiceId">chis.tumourQuestionnaireService</p>
				<p name="saveAction">saveTumourQuestionnaireManyData</p>
			</properties>
			<action id="THQM_GCForm" name="基本信息卷" ref="chis.application.tr.TR/TR/THQM_GCForm"/>
			<action id="THQM_TQForm" name="相关问题卷" ref="chis.application.tr.TR/TR/THQM_TQForm"/>
			<action id="create" name="新增" group="create"/> 
			<action id="save" name="确定" group="create||update"/> 
		</module>
		<module id="THQM_GCForm" name="基本信息卷" type="1" script="chis.application.tr.script.phq.TumourHealthQuestionManyGeneralCaseForm">
			<properties> 
				<p name="entryName">chis.application.phq.schemas.PHQ_GeneralCaseMany</p> 
			</properties> 
		</module>
		<module id="THQM_TQForm" name="相关问题卷" type="1" script="chis.application.tr.script.phq.TumourHealthQuestionsManyTestQuestionsForm">
			
		</module>
		<!-- =========问题公用模块=======e==== -->
	</catagory>
</application>