<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hy.HY" name="高血压管理"  type="1">
	<catagory id="HY" name="高血压管理">
	<module id="D01" name="高血压档案管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecord</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hy.HY/HY/D01-hr" />
		</module>
		<module id="D01-hr" name="高血压档案列表"
			script="chis.application.hy.script.record.HypertensionRecordListView" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecord</p>
				<p name="serviceId">chis.hypertensionService</p>
				<p name="listServiceId">chis.hypertensionListService</p>
				<p name="visitRefId">D0-1-3</p>
			</properties>
			<action id="createDoc" name="新建" iconCls="create" group="create" />
			<action id="modify" name="查看" iconCls="update" group="update" />
			<action id="writeOff" name="注销" iconCls="common_writeOff" group="update" />
			<!-- action id="confirmWriteOff" name="注销核实" iconCls="common_writeOffCheck" group="update"/ -->
			<action id="visit" name="随访" iconCls="hypertension_visit" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="D03" name="高血压随访记录" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionVisit</p>
				<p name="manageUnitField">c.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hy.HY/HY/D03-list" />
		</module>
		<module id="D03-list" name="高血压随访列表"
			script="chis.application.hy.script.visit.HypertensionVisitListView"
			type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionVisitPlan</p>
				<p name="navField">b.manaUnitId</p>  
				<p name="navDic">b.manageUnit</p> 
				<p name="listServiceId">chis.hypertensionVisitService</p>  
				<p name="listAction">listHypertensionVistPlan</p>  
			</properties>
			<action id="modify" name="随访" iconCls="update" />
			<action id="print" name="打印" />
			<action id="delete" name="删除" iconCls="remove" />
			<action id="quchong" name="去重" iconCls="remove" />
		</module>
		<!-- 高血压首诊业务操作走疑似 数据分存 -->
		<module id="C30_list" name="高血压首诊测压" script="chis.application.hy.script.first.HypertensionFCBPList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP</p>
			</properties>
			<action id="update" name="查看" ref="chis.application.hy.HY/HY/C30_form" />
			<action id="print" name="打印" />
		</module>
		
		<!-- C30_list2增加导航栏查询高血压首诊测试的功能 2019-07-10 Wangjl -->
		<module id="C30_list2" name="高血压首诊测压" script="chis.application.hy.script.first.HypertensionFCBPList2" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP</p>
			</properties>
			<action id="update" name="查看" ref="chis.application.hy.HY/HY/C30_form" />
			<action id="print" name="打印" />
		</module>
		
		<module id="C30_form" name="高血压首诊测压" type="1"
			script="chis.application.hy.script.first.HypertensionFCBPForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP</p>
			</properties>
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="C30" name="高血压首诊测压" type="1"
			script="chis.application.hy.script.first.HypertensionFCBPForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP_PHIS</p>
				<p name="saveServiceId">chis.hypertensionFCBPService</p>
				<p name="saveAction">saveHyperFCBP</p>
			</properties>
			<action id="save" name="确定" group="create" />
		</module>
		<module id="C32" name="高血压首诊测压" type="1"
			script="chis.application.hy.script.first.HypertensionFCBPPHISModule">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP_PHIS</p>
			</properties>
			<action id="HypertensionFCBPList" name="高血压首诊测压列表"  ref="chis.application.hy.HY/HY/C32-1" />
			<action id="HypertensionFCBPForm" name="高血压首诊测压表单"  ref="chis.application.hy.HY/HY/C32-2" />
		</module>
		<module id="C32-1" name="高血压首诊测压" type="1"
			script="chis.application.hy.script.first.HypertensionFCBPPHISList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP_PHIS</p>
			</properties>
		</module>
		<module id="C32-2" name="高血压首诊测压" type="1"
			script="chis.application.hy.script.first.HypertensionFCBPPHISForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_Hypertension_FCBP_PHIS</p>
				<p name="saveServiceId">chis.hypertensionFCBPService</p>
				<p name="saveAction">saveHyperFCBPPHIS</p>
			</properties>
			<action id="save" name="保存" group="create" />
			<action id="create" name="新增" group="create" />
		</module>
		<module id="C17" name="高血压高危管理"
			script="chis.application.hy.script.risk.HypertensionRiskListView">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRisk</p>
				<p name="listServiceId">chis.hypertensionRiskService</p>
				<p name="listAction">listHypertensionRisk</p>
			</properties>
			<action id="createByEmpi" name="新增" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="核实" iconCls="common_writeOffCheck" />
			<action id="visit" name="随访" iconCls="hypertension_visit" />
			<action id="estimate" name="高血压档案"  iconCls="update" />
			<action id="writeOff" name="注销" iconCls="common_writeOff" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="C17-1" name="高血压高危人群核实表单" type="1"
			script="chis.application.hy.script.risk.HypertensionRiskConfirmForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRisk</p>
				<p name="saveServiceId">chis.hypertensionRiskService</p>
				<p name="saveAction">saveConfirmHypertensionRisk</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="C18-1" name="高血压高危档案" type="1"
			script="chis.application.hy.script.risk.HypertensionRiskAssessmentForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskAssessment</p>
				<p name="saveServiceId">chis.hypertensionRiskService</p>
				<p name="saveAction">saveHypertensionRiskAssessment</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<!--<action id="create" name="新建" iconCls="create" group="update" />-->
		</module>
		<module id="C18-1-3" name="健康教育处方" type="1"
			script="chis.application.hy.script.risk.HypertensionRiskHTForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskHT</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="import" name="引入健康处方" iconCls="create" group="update" />
		</module>
		<module id="C18-2" name="高血压高危结案" type="1"
			script="chis.application.hy.script.risk.HypertensionRiskCloseForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRisk</p>
				<p name="saveServiceId">chis.hypertensionRiskService</p>
				<p name="saveAction">saveCloseHypertensionRisk</p>
			</properties>
			<action id="save" name="确定" group="update"/>
		</module>
		<module id="C20" name="高血压疑似核实"
			script="chis.application.hy.script.similarity.HypertensionSimilarityListView">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionSimilarity</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>
			<action id="check" name="核实" iconCls="hypertension_check"/>
			<action id="estimate" name="高血压档案"  iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="C20-1" name="高血压疑似新建" type="1"
			script="chis.application.hy.script.similarity.HypertensionSimilarityForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionSimilarity</p>
				<p name="saveServiceId">chis.hypertensionSimilarityService</p>
				<p name="saveAction">saveHypertensionSimilarity</p>
			</properties>
			<action id="save" name="确定" group="create" />
		</module>
		<module id="C20-2" name="高血压疑似核实" type="1"
			script="chis.application.hy.script.similarity.HypertensionSimilarityCheckModule">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionSimilarityC</p>
				<p name="saveServiceId">chis.hypertensionSimilarityService</p>
				<p name="saveAction">saveHypertensionSimilarityCheck</p>
			</properties>
			<action id="HypertensionSimilarityCheckForm" name="高血压高危人群随访计划表单"  ref="chis.application.hy.HY/HY/C20-2-1" />
			<action id="HypertensionSimilarityCheckList" name="高血压高危人群随访信息列表"  ref="chis.application.hy.HY/HY/C20-2-2" />
		</module>
		<module id="C20-2-1" name="高血压疑似核实" type="1"
			script="chis.application.hy.script.similarity.HypertensionSimilarityCheckForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionSimilarityC</p>
				<p name="saveServiceId">chis.hypertensionSimilarityService</p>
				<p name="saveAction">saveHypertensionSimilarityCheck</p>
			</properties>
			<action id="save" name="确定" group="create" />
			<action id="confirm" name="确诊" group="create" />
			<action id="turnHighRisk" name="转高危"  group="update" iconCls="common_turn" />
			<action id="eliminate" name="排除"  group="update" iconCls="common_cancel" />
		</module>
		<module id="C20-2-2" name="高血压疑似核实" type="1"
			script="chis.application.hy.script.similarity.HypertensionSimilarityCheckList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionSimilarityC</p>
			</properties>
		</module>
		<module id="C19" name="高血压高危随访"
			script="chis.application.hy.script.risk.HypertensionRiskVisitListView">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskVisitPlan</p>
				<p name="listServiceId">chis.hypertensionRiskService</p>  
				<p name="listAction">listHypertensionRiskVistPlan</p>  
			</properties>
			<action id="visit" name="随访" iconCls="hypertension_visit" />
			<action id="print" name="打印" />
		</module>
		<module id="C19-1" name="高血压高危人群随访整体模块" type="1"  script="chis.application.hy.script.risk.HypertensionRiskVisitModule">
			<properties>
				<p name="saveServiceId">chis.hypertensionRiskService</p>
			</properties>
			<action id="HypertensionRiskVisitPlan" name="高血压高危人群随访计划列表"  ref="chis.application.hy.HY/HY/C19-1-1" />
			<action id="HypertensionRiskVisitForm" name="高血压高危人群随访信息表单"  ref="chis.application.hy.HY/HY/C19-1-2" />
		</module>
		<module id="C19-1-1" name="高血压高危人群计划列表" type="1"  script="chis.application.hy.script.risk.HypertensionRiskVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
			</properties>
		</module>
		<module id="C19-1-2" name="高血压高危人群随访表单" type="1"  	script="chis.application.hy.script.risk.HypertensionRiskVisitForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskVisit</p>
				<p name="loadServiceId">chis.hypertensionRiskService</p>
				<p name="loadAction">loadHypertensionRiskVisit</p>
				<p name="saveServiceId">chis.hypertensionRiskService</p>
				<p name="saveAction">saveHypertensionRiskVisit</p>
				<p name="refRecipeImportModule">chis.application.psy.PSY/PSY/I01-1-2-3-1</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="printRecipe" name="打印健康处方" iconCls="print"/>
		</module>
		<module id="A05" name="高血压年度评估列表" script="chis.application.hy.script.record.MyWorkHypertensionRecordList" type="1"> 
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecordWorkList</p> 
				<p name="serviceId">chis.hypertensionService</p>
				<p name="listServiceId">chis.hypertensionListService</p>
			</properties>  
			<action id="modify" name="查看" iconCls="update"/> 
		</module>
		<module id="C21" name="高血压档案" type="1"
			script="chis.application.wl.script.MyWorkHypertensionRecordList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="createByEmpiId" name="新建" iconCls="create" />
		</module>
		<module id="C22" name="高血压高危核实" type="1"
			script="chis.application.wl.script.MyWorkHypertensionRiskConfirmList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="confirm" name="核实" iconCls="common_writeOffCheck" />
		</module>
		<module id="C23" name="高血压高危档案" type="1"
			script="chis.application.wl.script.MyWorkHypertensionRiskAssessmentList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_WorkList</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="estimate" name="查看"  iconCls="update" />
		</module>
		<module id="D0-1" name="高血压整体模块" icon="default" type="1"
			script="chis.script.EHRView">
			<action id="RecordFormView" name="高血压档案" ref="chis.application.hy.HY/HY/D0-1-1" />
			<action id="FixGroupFormView" name="等级评估" ref="chis.application.hy.HY/HY/D0-1-2" />
			<action id="VisitFormView" name="高血压随访" ref="chis.application.hy.HY/HY/D0-1-3" />
			<action id="InquireFormView" name="高血压询问" ref="chis.application.hy.HY/HY/DHI_01" />
			<action id="VisitRecordModule" name="质控随访记录" ref="chis.application.dbs.DBS/DBS/D11-6" />
			<action id="MDC_T1" name="曲线图" ref="chis.application.hy.HY/HY/D0-1-4" />
		</module>
		<module id="D11-6" script="chis.application.common.script.VisitRecordModule" name="质控随访记录组合模块" type="1">
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
		<module id="D0-1-1" name="高血压档案整体模块" type="1"
			script="chis.application.hy.script.record.HypertensionRecordModule">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecord</p>
			</properties>
			<action id="RecordFormView" name="档案基本信息" ref="chis.application.hy.HY/HY/D0-1-1-1" />
			<action id="HyperMedicineListView" name="服药情况" ref="chis.application.hy.HY/HY/D0-1-1-2" />
		</module>
		<module id="D0-1-1-1" name="高血压档案表单" type="1"
			script="chis.application.hy.script.record.HypertensionRecordForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="importJzInfo" name="导入就诊数据" group="create||update" iconCls="add"/>
			<action id="check" name="健康检查" iconCls="update"/>
		</module>
		<module id="D0-1-1-2" name="档案服药情况列表" type="1"
			script="chis.application.hy.script.medicine.HypertensionMedicineList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionMedicine</p>
				<p name="refModule">chis.application.hy.HY/HY/D0-1-1-2-0</p>
			</properties>
			<action id="add" name="服药情况添加" group="create" />
			<action id="modify" name="查看" iconCls="update" group="update" />
			<action id="delete" name="删除" iconCls="remove" group="update" />
			<action id="import" name="导入处方服药情况" iconCls="healthDoc_import" group="create" />
		</module>
		<module id="D0-1-1-2-0" name="服药情况表单" type="1"
			script="chis.application.hy.script.medicine.HypertensionMedicineForm">
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="D0-1-2" name="高血压等级评估整体模块" type="1"
			script="chis.application.hy.script.fixgroup.HypertensionGroupModule">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionFixGroup</p>
				<p name="serviceId">chis.hypertensionService</p>
				<p name="refList">chis.application.hy.HY/HY/D0-1-2-1</p>
			</properties>
			<action id="HyperFixGroupList" name="分级列表" ref="chis.application.hy.HY/HY/D0-1-2-1" />
			<action id="HyperFixGroupForm" name="分级表单" ref="chis.application.hy.HY/HY/D0-1-2-2" />
		</module>
		<module id="D0-1-2-1" name="分级列表" type="1"
			script="chis.application.hy.script.fixgroup.HypertensionGroupList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionFixGroupList</p>
			</properties>
		</module>
		<module id="D0-1-2-2" name="分级表单" type="1"
			script="chis.application.hy.script.fixgroup.HypertensionGroupForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionFixGroup</p>
				<p name="serviceId">chis.hypertensionService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定"/>
			<action id="add" name="新增"/>
			<action id="look" name="友情提醒"/>
		</module>
		<module id="D0-1-3" name="高血压随访整体模块" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitModule">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="VisitPlanList" name="随访计划列表" ref="chis.application.hy.HY/HY/D0-2-0" />
			<action id="VisitBaseForm" name="随访基本信息" ref="chis.application.hy.HY/HY/D0-2-1" type="tab" />
			<action id="VisitPaperForm" name="随访基本信息" ref="chis.application.hy.HY/HY/HY01_01" type="tab" />
			<action id="HyperMedicineList" name="服药情况" ref="chis.application.hy.HY/HY/D0-2-2" type="tab" />
			<action id="HyperVisitDescriptionForm" name="中医辩体" ref="chis.application.hy.HY/HY/D0-2-3" type="tab" />
			<action id="HyperVisitHealthTeachForm" name="健康教育" ref="chis.application.hy.HY/HY/D0-2-4" type="tab" />
		</module>
		<module id="HY01_01" name="随访基本信息" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitBaseInfoFormHtml">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionVisit_html</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="importJzInfo" name="导入就诊数据" group="create||update" iconCls="add"/>
			<action id="print" name="打印" />
			<action id="deletevisit" name="删除" iconCls="remove" />
			<!--<action id="import" name="导入就诊数据" iconCls="healthDoc_import" group="create" />-->
		</module>
		<module id="D0-2-0" name="随访计划列表" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
				<p name="serviceId">chis.hypertensionVisitService</p>
			</properties>
		</module>
		<module id="D0-2-1" name="随访基本信息" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitBaseInfoForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionVisit</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="import" name="导入就诊数据" iconCls="healthDoc_import"
				group="create" />
			<action id="importJzInfo" name="导入就诊数据" group="create||update" iconCls="add"/>
		</module>
		<module id="D0-2-2" name="服药情况" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitMedicineList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionMedicine</p>
				<p name="refModule">chis.application.hy.HY/HY/D0-2-2-1</p>
			</properties>
			<action id="add" name="服药情况添加" group="create" />
			<action id="delete" name="删除" iconCls="remove" group="update" />
			<action id="modify" name="查看" iconCls="update" group="update" />
			<!--<action id="import" name="导入处方服药情况" iconCls="healthDoc_import" group="create" />-->
			<action id="importDrugInfo" name="导入药品信息" group="create" iconCls="healthDoc_import"/>
		</module>
		<module id="D0-2-2-1" name="服药情况表单" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitMedicineForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionMedicine</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="D0-2-3" name="中医辩体" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitDescriptionForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HyperVisitDescription</p>
				<p name="showButtonOnTop">true</p>
				<p name="autoFieldWidth">false</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="D0-2-4" name="健康教育" type="1"
			script="chis.application.hy.script.visit.HypertensionVisitHealthTeachForm">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord_GXYSF</p>  
				<p name="refRecipeImportModule">chis.application.psy.PSY/PSY/I01-1-2-3-1</p> 
				<p name="showButtonOnTop">true</p>
				<p name="autoFieldWidth">false</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="printRecipe" name="打印健康处方"  iconCls="print" />
		</module>
		<module id="DHI_01" name="高血压询问整体"
			script="chis.application.hy.script.inquire.HypertensionInquireModule" type="1">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="InquireList" name="询问计划列表" ref="chis.application.hy.HY/HY/DHI_01_01" />
			<action id="InquireForm" name="询问记录" ref="chis.application.hy.HY/HY/DHI_01_02" />
		</module>
		<module id="DHI_01_01" name="询问计划列表"
			script="chis.application.hy.script.inquire.HypertensionInquireList" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionInquire</p>
				<p name="selectFirst">true</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="DHI_01_02" name="询问记录"
			script="chis.application.hy.script.inquire.HypertensionInquireForm" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionInquire</p>
				<p name="colCount">2</p>
				<p name="labelAlign">left</p>
				<p name="labelWidth">100</p>
				<p name="autoFieldWidth">false</p>
				<p name="fldDefaultWidth">200</p>
				<p name="autoLoadData">false</p>
				<p name="showButtonOnTop">true</p>
				<p name="autoLoadSchema">false</p>
				<p name="serviceId">chis.hypertensionInquireService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="add" name="新增" group="create" />
		</module>
		<module id="D0-1-4" name="高血压曲线图" type="1" script="chis.script.gis.powerChartView">
			<properties>
				<p name="entryName">chis.report.CHART_Hypertension</p>
			</properties>
		</module>
		<module id="D02" name="高血压评估记录" type="1" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionFixGroup</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hy.HY/HY/D02-list" />
		</module>
		<module id="D02-list" name="高血压评估列表"
			script="chis.application.hy.script.fixgroup.HypertensionGroupListView" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionFixGroup</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<!--
			<module id="D04" name="高危人群核实" script="">
			</module>

			<module id="D07" name="高血压疑似病人核实"
				script="chis.application.hy.script.first.HypertensionFirstCheckList">
				<properties>
					<p name="entryName">chis.application.hy.schemas.MDC_HypertensionFirst</p>
					<p name="navDic">chis.@manageUnit</p>
					<p name="navField">b.manaUnitId</p>
				</properties>
				<action id="check" name="核实" iconCls="hypertension_check" />
			</module>
			-->
		<module id="C18" name="高血压高危评估" type="1"
			script="chis.application.hy.script.risk.HypertensionRiskAssessmentListView">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskAssessment</p>
			</properties>
			<action id="estimate" name="查看"  iconCls="update"  />
			<action id="print" name="打印" />
		</module>
		<!--<module id="C18-1" name="高血压高危人群评估整体模块" type="1"
				script="chis.application.hy.script.risk.HypertensionRiskAssessmentModule">
				<properties>
					<p name="title">高血压高危评估</p>
					<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskAssessment</p>
				</properties>
				<action id="assessmentList" name="评估列表" ref="chis.application.hy.HY/HY/C18-1-1" />
				<action id="assessmentForm" name="评估表单" ref="chis.application.hy.HY/HY/C18-1-2" />
			</module>
			<module id="C18-1-1" name="高血压高危人群评估列表" type="1"
				script="chis.application.hy.script.risk.HypertensionRiskAssessmentList">
				<properties>
					<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRiskAssessmentList</p>
				</properties>
			</module>-->
		<module id="D32" name="高血压终止管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecordEnd</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hy.HY/HY/D32-1" />
		</module>
		<module id="D32-1" name="高血压终止管理列表"
			script="chis.application.hy.script.record.HypertensionRecordEndListView" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecordEnd</p>
				<p name="refEndForm">chis.application.hy.HY/HY/D32-2</p>
			</properties>
			<action id="check" name="核实" iconCls="hypertension_check"/>
			<action id="print" name="打印" />
		</module>
		<module id="D32-2" name="高血压终止核实" type="1"
			script="chis.application.hy.script.record.HypertensionRecordEndForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionRecordEnd</p>
				<p name="saveServiceId">chis.hypertensionService</p>
				<p name="saveAction">saveHypertensionEndCheck</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<!---高血压质控QualityControl-->
		<module id="C31" name="高血压随访质控" script="chis.application.quality.script.QualityControl_gxy_module"> 
			<action id="Control_gxyYsj_list" name="高血压随访原数据list" ref="chis.application.hy.HY/HY/C31_1"/>  
			<action id="Control_gxyYf_list" name="质控月份list" ref="chis.application.hy.HY/HY/C31_2"/>
			<action id="Control__gxyZk_list" name="质控数据list" ref="chis.application.hy.HY/HY/C31_3"/>
		</module> 
		<module id="C31_1" name="高血压随访原数据list" script="chis.application.quality.script.QualityControl_gxyYsj_list" type="1"> 
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_YSJ</p>
			</properties>
		</module>
		<module id="C31_2" name="质控月份list" script="chis.application.quality.script.QualityControl_gxyYf_list" type="1">
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_ZQ</p>
			</properties> 
			<action id="button1" name="评分标准" iconCls="update"   />
			<action id="button2" name="完成质控" iconCls="update"   />
			<action id="button3" name="批量评分" iconCls="update"   />
		</module> 
		<module id="C31_3" name="质控数据list" script="chis.application.quality.script.QualityControl_gxyZk_list" type="1"> 
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_SJ</p>
				<p name="addModule">chis.application.hy.HY/HY/C31_3_1</p>
				<p name="addCkzkbg">chis.application.hy.HY/HY/C31_3_2</p>
			</properties> 
			<action id="addYB" name="添加样本"   />
			<action id="remove" name="删除样本" group="update" />
			<action id="someOne" name="随机抽样" iconCls="update"   />
			<action id="cancel" name="清空" iconCls="update"   />
			<action id="print" name="打印" iconCls="update"   />
		</module>
		<module id="C31_3_1" name="随访基本信息" type="1" script="chis.application.quality.script.QualityVisitBaseInfo_module">
			<action id="lrzksj" name="随访基本信息录入form" ref="chis.application.hy.HY/HY/C31_3_1_1"/>  
		</module>
		<module id="C31_3_1_1" name="随访基本信息" type="1" 	script="chis.application.quality.script.QualityVisitBaseInfo_form">
			<action id="remove2" name="删除样本" group="update" />
			<action id="someOne" name="随机抽样" iconCls="update"   />
			<action id="cancel" name="清空" iconCls="update"   />
			<action id="print" name="打印" iconCls="update"   />
		</module>
		<module id="C31_3_1" name="随访基本信息" type="1" script="chis.application.quality.script.QualityVisitBaseInfo_module">
			<action id="lrzksj" name="随访基本信息录入form" ref="chis.application.hy.HY/HY/C31_3_1_1"/>  
		</module>
		<module id="C31_3_1_1" name="随访基本信息" type="1" 	script="chis.application.quality.script.QualityVisitBaseInfo_form">
			<properties>
				<p name="entryName">chis.application.quality.schemas.QUALITY_Visit</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="C31_3_2"  type="1" name="查看质控报告" 	script="chis.application.quality.script.QualityCkZkBg_module">
			<action id="Control_form" name="查看质控报告form" ref="chis.application.hy.HY/HY/C31_3_2_from"/>  
			<action id="Control_list" name="查看质控报告list" ref="chis.application.hy.HY/HY/C31_3_2_list"/>
		</module>
		<module id="C31_3_2_from" type="1" name="查看质控报告form" script="chis.application.quality.script.QualityCkZkBg_form"> 
			<properties> 
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_CKBG</p>  
			</properties>
		</module>  
		<module id="C31_3_2_list" type="1" name="查看质控报告list" script="chis.application.quality.script.QualityCkZkBg_list"> 
			<properties> 
				<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_CKZKBG_DZ</p>  
			</properties>
		</module>
		<module id="C24" name="高血压年度评估" script="chis.application.hy.script.assess.HypertensionYearAssessListView">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionYearAssess</p>
			</properties>
			<action id="check" name="开始年度评估" iconCls="hypertension_check"/>
			<action id="print" name="打印" />
		</module>
		
		
		
		<module id="D05" name="高血压基线调查表管理" script="chis.application.hy.script.baseline.HyBaselineList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HyBaseline</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
			<action id="createRecord" name="新建" iconCls="create"/>
            <action id="remove" name="删除" iconCls="remove"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="print"  name="导出" />
		</module>
		<module id="D0501" name="高血压基线调查表整体" script="chis.application.hy.script.baseline.HyBaselineModule" type="1">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="BaselineList" name="高血压基线调查列表" ref="chis.application.hy.HY/HY/D050101" />
			<action id="BaselineForm" name="高血压基线调查表格" ref="chis.application.hy.HY/HY/D050102" />
		</module>
		<module id="D050101" name="高血压基线调查个人列表"
			script="chis.application.hy.script.baseline.HyBaselinePersonalList" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HyBaseline</p>
				<p name="selectFirst">true</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="D050102" name="分级表单"
			script="chis.application.hy.script.baseline.HyBaselineForm" type="1">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HyBaseline</p>
				<p name="colCount">2</p>
				<p name="labelAlign">left</p>
				<p name="labelWidth">100</p>
				<p name="autoFieldWidth">false</p>
				<p name="fldDefaultWidth">200</p>
				<p name="autoLoadData">false</p>
				<p name="showButtonOnTop">true</p>
				<p name="autoLoadSchema">false</p>
				<p name="serviceId">chis.hypertensionBaselineService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<!-- <action id="add" name="新增" group="create" /> -->
		</module>
	</catagory>
</application>