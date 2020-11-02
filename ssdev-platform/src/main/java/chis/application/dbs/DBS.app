<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.dbs.DBS" name="糖尿病管理" type="1">
	<catagory id="DBS" name="糖尿病管理">
	<module id="D04" name="糖尿病档案管理" script="chis.script.CombinedDocList">
			<properties> 
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecord</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.dbs.DBS/DBS/D04-1" />
		</module>
		<module id="D04-1" name="糖尿病档案" type="1"
			script="chis.application.dbs.script.record.DiabetesRecordListView">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecord</p>
				<p name="listServiceId">chis.diabetesService</p>
				<p name="listAction">ListDiabetesRecord</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="visit" name="随访" iconCls="hypertension_visit" />
			<action id="writeOff" name="注销" iconCls="common_writeOff" />
			<action id="print" name="打印" />
		</module>
		<module id="D05" name="糖尿病随访查询" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesVisit</p>
				<p name="manageUnitField">c.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.dbs.DBS/DBS/D05-list" />
		</module>
		<module id="D05-list" name="糖尿病随访列表"
			script="chis.application.dbs.script.visit.DiabetesVisitListView" type="1">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesVisitPlan</p>
				<p name="listServiceId">chis.diabetesVisitService</p>  
				<p name="listAction">listDiabetesVistPlan</p>  
			</properties>
			<action id="modify" name="随访" iconCls="update" />
			<action id="print" name="打印" />
			<action id="delete" name="删除" iconCls="remove" />
			<action id="quchong" name="去重" iconCls="remove" />
		</module>
		<module id="D01" name="糖尿病高危管理" script="chis.script.CombinedDocList">
			<properties> 
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesOGTTRecord</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.dbs.DBS/DBS/D01-1" />
		</module>
		<module id="D01-1" name="糖尿病高危管理" type="1"
			script="chis.application.dbs.script.ogtt.DiabetesOGTTRecordListView">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesOGTTRecordListView</p>
				<p name="refOGTTModule">chis.application.dbs.DBS/DBS/D01-2</p> 
			</properties>
			<action id="confirm" name="核实" iconCls="common_writeOffCheck" />
			<action id="modify" name="糖尿病档案" iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="D01-2" name="糖尿病高危核实" type="1"
			script="chis.application.dbs.script.ogtt.DiabetesOGTTModule">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesOGTTRecord</p>
			</properties>
			<action id="OGTTList" name="糖尿病高危核实" ref="chis.application.dbs.DBS/DBS/D01-3" />
			<action id="OGTTForm" name="糖尿病高危核实" ref="chis.application.dbs.DBS/DBS/D01-4" />
		</module>
		<module id="D01-3" name="糖尿病高危核实" type="1"
			script="chis.application.dbs.script.ogtt.DiabetesOGTTList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
			</properties>
		</module>
		<module id="D01-4" name="糖尿病高危核实" type="1"
			script="chis.application.dbs.script.ogtt.DiabetesOGTTForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesOGTTRecord</p>
				<p name="saveServiceId">chis.diabetesOGTTService</p>
				<p name="saveAction">saveDiabetesOGTTRecord</p>
			</properties>
			<action id="save" name="保存"/>
		</module>
		<module id="D20" name="糖尿病疑似核实"
			script="chis.application.dbs.script.similarity.DiabetesSimilarityListView">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesSimilarityList</p>
				<p name="listServiceId">chis.diabetesSimilarityService</p>
				<p name="listAction">listDiabetesSimilarity</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>
			<action id="check" name="核实" iconCls="hypertension_check"/>
			<action id="print" name="打印" />
		</module>
		<module id="D20-1" name="糖尿病疑似新建" type="1"
			script="chis.application.dbs.script.similarity.DiabetesSimilarityForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesSimilarity</p>
				<p name="saveServiceId">chis.diabetesSimilarityService</p>
				<p name="saveAction">saveDiabetesSimilarity</p>
			</properties>
			<action id="save" name="确定" group="create" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="D20-2" name="糖尿病疑似核实" type="1"
			script="chis.application.dbs.script.similarity.DiabetesSimilarityCheckForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesSimilarityCheck</p>
				<p name="saveServiceId">chis.diabetesSimilarityService</p>
				<p name="saveAction">saveDiabetesSimilarityCheck</p>
				<p name="loadServiceId">chis.diabetesSimilarityService</p>
				<p name="loadAction">loadDiabetesSimilarityCheck</p>
			</properties>
			<action id="save" name="保存" group="create"  iconCls="save" />
			<action id="definiteDiagnosis" name="确诊" iconCls="common_writeOffCheck" />
			<action id="turnHighRisk" name="转高危" iconCls="common_turn" />
			<action id="exclude" name="排除" iconCls="common_cancel" />
		</module>
		<module id="D21" name="糖尿病档案" script="chis.script.CombinedDocList" type = "1">
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list"
				ref="chis.application.dbs.DBS/DBS/D21-1" />
		</module>
		<module id="D21-1" name="糖尿病档案" type="1"
			script="chis.application.wl.script.MyWorkDiabetesRecordList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="createByEmpiId" name="新建" iconCls="create" />
		</module>
		<module id="D22" name="糖尿病高危核实" type="1"
			script="chis.application.wl.script.MyWorkDiabetesRiskConfirmList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="confirm" name="核实" iconCls="common_writeOffCheck" />
		</module>
		<module id="D23" name="糖尿病高危评估" type="1"
			script="chis.application.wl.script.MyWorkDiabetesRiskAssessmentList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="estimate" name="评估"  iconCls="update" />
		</module>
		<module id="D11" name="糖尿病管理组合模块配置文件" icon="default" type="1"
			script="chis.script.EHRView">
			<action id="RecordModule" name="糖尿病档案" ref="chis.application.dbs.DBS/DBS/D11-1" />
			<action id="FixGroupModule" name="糖尿病定转组信息" ref="chis.application.dbs.DBS/DBS/D11-2-2" />
			<action id="VisitModule" name="糖尿病随访管理" ref="chis.application.dbs.DBS/DBS/D11-3" />
			<action id="VisitRecordModule" name="质控随访记录" ref="chis.application.dbs.DBS/DBS/D11-6" />
			<action id="MDC_T1" name="曲线图" script="" />
		</module>
		<module id="D11-6" script="chis.application.common.script.VisitRecordModule"
			name="质控随访记录组合模块" type="1">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="VisitRecordList" name="质控随访记录列表" ref="chis.application.dbs.DBS/DBS/D11-6-0" />
			<action id="VisitRecordDetialModule" name="质控随访明细" ref="chis.application.dbs.DBS/DBS/D11-6-1" type="tab" />		
		</module>
		<module id="D11-6-0" type="1" name="质控随访记录列表" script="chis.application.common.script.VisitRecordList">
			<properties>
				<p name="entryName">chis.application.mh.schemas.SQ_ZKSFJH</p>
			</properties>
		</module>
		<module id="D11-6-1" type="1" name="质控随访明细模块" script="chis.application.common.script.VisitRecordDetialModule">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.mh.schemas.SQ_JY01</p>
				<p name="VisitRecordDetialList">chis.application.dbs.DBS/DBS/D11-6-2</p>
				<p name="VisitRecordMuseList">chis.application.dbs.DBS/DBS/D11-6-3</p>
				<p name="VisitRecordPacsList">chis.application.dbs.DBS/DBS/D11-6-4</p>
			</properties>
		</module>
		<module id="D11-6-2" type="1" name="质控随访明细列表" script="chis.application.common.script.VisitRecordDetialListView">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.mh.schemas.SQ_JY01</p>
			</properties>
		</module>
		<module id="D11-6-3" type="1" name="质控随访明细心电图列表" script="chis.application.common.script.VisitRecordMuseListView">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.mh.schemas.SQ_MUSE</p>
			</properties>
		</module>
		<module id="D11-6-4" type="1" name="质控随访明细胸片列表" script="chis.application.common.script.VisitRecordPacsListView">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.mh.schemas.SQ_PACS</p>
			</properties>
		</module>
		<module id="D11-1" script="chis.application.dbs.script.record.DiabetesRecordModule"
			name="糖尿病基本档案组合模块" type="1">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecord</p>
			</properties>
			<action id="DiabetesRecordForm" name="糖尿病档案form" ref="chis.application.dbs.DBS/DBS/D11-1-1" />
			<action id="DiabetesRecordMedicineList" name="糖尿病档案用药" ref="chis.application.dbs.DBS/DBS/D11-1-2" />
		</module>
		<module id="D11-1-1" type="1" name="糖尿病档案组合模块的form"
			script="chis.application.dbs.script.record.DiabetesRecordForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecord</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="turn" name="血糖单位转换为mmol/L" iconCls="common_turn" />
			<action id="check" name="健康检查" iconCls="update"/>
		</module>
		<module id="D11-1-2" type="1" name="糖尿病档案用药列表"
			script="chis.application.dbs.script.record.DiabetesRecordMedicineList">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesMedicine</p>
			</properties>
			<action id="add" name="服药情况增加" group="update" />
			<action id="modify" name="服药情况查看" group="update" iconCls="update" />
			<action id="delete" name="删除" iconCls="remove" group="update" />
		</module>
		<module id="D11-2" script="chis.application.dbs.script.fixgroup.DiabetesFixGroupModule"
			name="糖尿病分组组合模块" type="1">
			<action id="DiabetesFixGroupList" name="糖尿病分级组合模块的list" ref="chis.application.dbs.DBS/DBS/D11-2-2" />
			<action id="DiabetesFixGroupForm" name="糖尿病分级组合模块的form" ref="chis.application.dbs.DBS/DBS/D11-2-1" />
		</module>
		<module id="D11-2-1" type="1" name="糖尿病分级组合模块的form"
			script="chis.application.dbs.script.fixgroup.DiabetesFixGroupForm" >
			<action id="save" name="确定"/>
			<action id="look" name="友情提醒"/>
		</module>
		<module id="D11-2-2" type="1" name="糖尿病分级组合模块的list"
			script="chis.application.dbs.script.fixgroup.DiabetesFixGroupList" />
		<module id="D11-3" script="chis.application.dbs.script.visit.DiabetesVisitModule"
			name="糖尿病随访组合模块" type="1">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="VisitPlanList" name="随访计划列表" ref="chis.application.dbs.DBS/DBS/D11-3-0" />
			<action id="DiabetesVisitForm" name="随访基本信息" ref="chis.application.dbs.DBS/DBS/D11-3-1" type="tab" />
			<action id="DiabetesVisitFormPaper" name="随访基本信息" ref="chis.application.dbs.DBS/DBS/D11-3-1-1" type="tab" />
			<action id="DiabetesVisitMedicineList" name="服药情况" ref="chis.application.dbs.DBS/DBS/D11-3-2" type="tab" />
			<action id="DiabetesComplicationListView" name="并发症" ref="chis.application.dbs.DBS/DBS/D11-3-5" type="tab" />
			<action id="DiabetesRepeatVisitModule" name="复诊信息" ref="chis.application.dbs.DBS/DBS/D11-3-7" type="tab" />
			<action id="DiabetesVisitDescriptionForm" name="中医辩体" ref="chis.application.dbs.DBS/DBS/D11-3-3" type="tab" />
			<action id="DiabetesVisitHealthTeachForm" name="健康教育" ref="chis.application.dbs.DBS/DBS/D11-3-4" type="tab" />			
		</module>
		<module id="D11-3-7" script="chis.application.dbs.script.visit.DiabetesRepeatModule" name="糖尿病复诊模块" type="1">
			<properties>
				<p name="serviceId">chis.diabetesService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="DiabetesRepeatFormView" name="复诊表单" ref="chis.application.dbs.DBS/DBS/D11-3-7-1" />
			<action id="DiabetesRepeatListView" name="复诊列表" ref="chis.application.dbs.DBS/DBS/D11-3-7-2" />
		</module>	
		<module id="D11-3-7-1" type="1" name="糖尿病复诊组合模块的list" script="chis.application.dbs.script.visit.DiabetesRepeatList">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRepeatVisit</p>
			</properties>
		</module>
		<module id="D11-3-7-2" type="1" name="糖尿病复诊组合模块的form" script="chis.application.dbs.script.visit.DiabetesRepeatForm">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRepeatVisit</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" group="create" />
		</module>
		<module id="D11-3-0" type="1" name="糖尿病随访列表" script="chis.application.dbs.script.visit.DiabetesVisitList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
			</properties>
		</module>
		<module id="D11-3-1" type="1" name="糖尿病随访组合模块的form" script="chis.application.dbs.script.visit.DiabetesVisitForm">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesVisit</p>
			</properties>
			<action id="save" name="确定" group="create,update" />
			<action id="turn" name="血糖单位转换为mmol/L" iconCls="common_turn" />
			<action id="importJzInfo" name="导入就诊记录" group="create,update" iconCls="add"/>	
		</module>
		<module id="D11-3-1-1" type="1" name="糖尿病随访组合模块的form" script="chis.application.dbs.script.visit.DiabetesVisitFormPaper">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesVisit</p>
			</properties>
			<action id="save" name="确定" group="create,update" />
			<action id="importJzInfo" name="导入就诊记录" group="create,update" iconCls="add"/>
			<action id="print" name="打印"/>
			<action id="deletevisit" name="删除" iconCls="remove" />
		</module>
		<module id="D11-3-2" type="1" name="糖尿病随访组合模块的服药情况" script="chis.application.dbs.script.visit.DiabetesVisitMedicineList">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesMedicine</p>
			</properties>
			<action id="add" name="服药情况增加" group="update" />
			<action id="modify" name="服药情况查看" group="update" iconCls="update" />
			<action id="importDrugInfo" name="导入药品信息" group="update" iconCls="healthDoc_import"/>
			<action id="delete" name="删除" iconCls="remove" group="update" />
		</module>
		<module id="D11-3-5" type="1" name="糖尿病档案组合模块的并发症列表" script="chis.application.dbs.script.visit.DiabetesComplicationListView">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesComplication</p>
			</properties>
			<action id="add" name="增加并发症" group="update" />
			<action id="remove" name="删除并发症" group="update" />
		</module>
		<module id="D11-3-3" type="1" name="糖尿病随访中医辩体form" script="chis.application.dbs.script.visit.DiabetesVisitDescriptionForm">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesVisitDescription</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="D11-3-4" type="1" name="糖尿病随访健康教育form" script="chis.application.dbs.script.visit.DiabetesVisitHealthTeachForm">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord_TNBSF</p> 
				<p name="refRecipeImportModule">chis.application.psy.PSY/PSY/I01-1-2-3-1</p> 
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="printRecipe" name="打印健康处方"  iconCls="print" />
		</module>
		<module id="D11-4" name="糖尿病曲线图" script="chis.script.gis.powerChartView"
			type="1">
			<properties>
				<p name="entryName">chis.report.CHART_Diabetics</p>
			</properties>
		</module>
		<module id="D11-5" script="chis.application.dbs.script.inquire.DiabetesInquireModule"
			name="糖尿病询问组合模块" type="1">
			<properties>
				<p name="serviceId">chis.diabetesService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="RecordFormView" name="档案基本信息" ref="chis.application.dbs.DBS/DBS/D11-5-0" />
			<action id="HyperMedicineListView" name="服药情况" ref="chis.application.dbs.DBS/DBS/D11-5-1" />
		</module>
		<module id="D11-5-0" type="1" name="糖尿病询问组合模块的list" script="chis.application.dbs.script.inquire.DiabetesInquireList">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesInquire</p>
			</properties>
		</module>
		<module id="D11-5-1" type="1" name="糖尿病询问组合模块的form" script="chis.application.dbs.script.inquire.DiabetesInquireForm">
			<properties>
				<p name="isAutoScroll">true</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesInquire</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" group="create" />
		</module>
		<module id="D17" name="糖尿病高危核实"
			script="chis.application.dbs.script.risk.DiabetesRiskListView"  type="1">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRisk</p>
			</properties>
			<action id="confirm" name="核实" iconCls="common_writeOffCheck" />
			<action id="estimate" name="评估"  iconCls="update" />
			<action id="close" name="结案" />
			<action id="visit" name="随访" iconCls="hypertension_visit" />
			<action id="print" name="打印" />
		</module>
		<module id="D17-1" name="糖尿病高危人群核实表单" type="1"
			script="chis.application.dbs.script.risk.DiabetesRiskConfirmForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRisk</p>
				<p name="saveServiceId">chis.diabetesRiskService</p>
				<p name="saveAction">saveConfirmDiabetesRisk</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="D18" name="糖尿病高危评估"
			script="chis.application.dbs.script.risk.DiabetesRiskAssessmentListView"  type="1">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskAssessment</p>
			</properties>
			<action id="estimate" name="查看"  iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="D18-1" name="糖尿病高危人群评估整体模块" type="1"
			script="chis.application.dbs.script.risk.DiabetesRiskAssessmentModule">
			<properties>
				<p name="title">糖尿病高危评估</p>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskAssessment</p>
			</properties>
			<action id="assessmentList" name="评估列表" ref="chis.application.dbs.DBS/DBS/D18-1-1" />
			<action id="assessmentForm" name="评估表单" ref="chis.application.dbs.DBS/DBS/D18-1-2" />
		</module>
		<module id="D18-1-1" name="儿糖尿病高危人群评估列表" type="1"
			script="chis.application.dbs.script.risk.DiabetesRiskAssessmentList">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskAssessmentList</p>
			</properties>
		</module>
		<module id="D18-1-2" name="糖尿病高危人群评估表单" type="1"
			script="chis.application.dbs.script.risk.DiabetesRiskAssessmentForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskAssessment</p>
				<p name="saveServiceId">chis.diabetesRiskService</p>
				<p name="saveAction">saveDiabetesRiskAssessment</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" iconCls="create" group="update" />
		</module>
		<module id="D18-1-3" name="健康教育处方" type="1"
			script="chis.application.dbs.script.risk.DiabetesRiskHTForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskHT</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="import" name="引入健康处方" iconCls="create" group="update" />
		</module>
		<module id="D18-2" name="糖尿病高危结案" type="1"
			script="chis.application.dbs.script.risk.DiabetesRiskCloseForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRisk</p>
				<p name="saveServiceId">chis.diabetesRiskService</p>
				<p name="saveAction">saveCloseDiabetesRisk</p>
			</properties>
			<action id="save" name="确定" group="update"/>
		</module>
		<module id="D19" name="糖尿病高危随访"
			script="chis.application.dbs.script.risk.DiabetesRiskVisitListView"  type="1">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskVisit</p>
			</properties>
			<action id="visit" name="查看" iconCls="hypertension_visit" />
			<action id="print" name="打印" />
		</module>
		<module id="D19-1" name="糖尿病高危人群随访整体模块" type="1"  script="chis.application.dbs.script.risk.DiabetesRiskVisitModule">
			<properties>
				<p name="saveServiceId">chis.diabetesRiskService</p>
			</properties>
			<action id="DiabetesRiskVisitPlan" name="糖尿病高危人群随访计划列表"  ref="chis.application.dbs.DBS/DBS/D19-1-1" />
			<action id="DiabetesRiskVisitForm" name="糖尿病高危人群随访信息表单"  ref="chis.application.dbs.DBS/DBS/D19-1-2" />
		</module>
		<module id="D19-1-1" name="糖尿病高危人群计划列表" type="1"  script="chis.application.dbs.script.risk.DiabetesRiskVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
			</properties>
		</module>
		<module id="D19-1-2" name="糖尿病高危人群随访表单" type="1"  	script="chis.application.dbs.script.risk.DiabetesRiskVisitForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRiskVisit</p>
				<p name="saveServiceId">chis.diabetesRiskService</p>
				<p name="saveAction">saveDiabetesRiskVisit</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="D33" name="糖尿病终止管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecordEnd</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.dbs.DBS/DBS/D33-1" />
		</module>
		<module id="D33-1" name="糖尿病终止管理列表"
			script="chis.application.dbs.script.record.DiabetesRecordEndListView" type="1">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecordEnd</p>
				<p name="refEndForm">chis.application.dbs.DBS/DBS/D33-2</p>
			</properties>
			<action id="check" name="核实" iconCls="hypertension_check"/>
			<action id="print" name="打印" />
		</module>
		<module id="D33-2" name="糖尿病终止核实" type="1"
			script="chis.application.dbs.script.record.DiabetesRecordEndForm">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesRecordEnd</p>
				<p name="saveServiceId">chis.diabetesService</p>
				<p name="saveAction">saveDiabetesEndCheck</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<!---糖尿病QualityControl-->
		<module id="D25" name="糖尿病随访质控" script="chis.application.quality.script.QualityControl_tnb_module"> 
		<action id="Control_tnbYsj_list" name="糖尿病随访原数据list" ref="chis.application.dbs.DBS/DBS/D25-1"/>  
			<action id="Control_tnbYf_list" name="质控月份list" ref="chis.application.dbs.DBS/DBS/D25-2"/>
			<action id="Control__tnbZk_list" name="质控数据list" ref="chis.application.dbs.DBS/DBS/D25-3"/>
		</module>
		<module id="D25-1" name="糖尿病随访原数据list1" script="chis.application.quality.script.QualityControl_tnbYsj_list" type="1" > 
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_TnbYsj</p>
			</properties>
		</module>
		<module id="D25-2" name="质控月份list2" script="chis.application.quality.script.QualityControl_tnbYf_list"  type="1" >
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_ZQ</p>
			</properties> 
			<action id="button1" name="评分标准" iconCls="update"   />
			<action id="button2" name="完成质控" iconCls="update"   />
			<action id="button3" name="批量评分" iconCls="update"   />
		</module>
		<module id="D25-3" name="质控数据list3" script="chis.application.quality.script.QualityControl_gxyZk_list"  type="1" > 
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_TNB</p>
				<p name="addModule">chis.application.hy.HY/HY/C31_3_1</p>
				<p name="addCkzkbg">chis.application.hy.HY/HY/C31_3_2</p>
			</properties> 
			<action id="addYB" name="添加样本"   />
			<action id="remove" name="删除样本" group="update" />
			<action id="someOne" name="随机抽样" iconCls="update"   />
			<action id="cancel" name="清空" iconCls="update"   />
			<action id="print" name="打印" iconCls="update"   />
		</module>
		<module id="D24" name="糖尿病年度评估"
			script="chis.application.dbs.script.assess.DiabetesYearAssessListView">
			<properties>
				<p name="entryName">chis.application.dbs.schemas.MDC_DiabetesYearAssess</p>
			</properties>
			<action id="check" name="开始年度评估" iconCls="hypertension_check"/>
			<action id="print" name="打印" />
		</module>
	</catagory>
</application>