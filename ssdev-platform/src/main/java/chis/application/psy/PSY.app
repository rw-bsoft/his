<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.psy.PSY" name="精神病管理"  type="1">
	<catagory id="PSY" name="精神病管理">
		<module id="I01" name="重性精神病管理" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisRecord</p>  
				<p name="manageUnitField">a.manaUnitId</p>  
				<p name="areaGridField">c.regionCode</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="navField">manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.psy.PSY/PSY/I01-1"/> 
		</module>  
		<module id="I01-1" name="精神病档案列表" type="1" script="chis.application.psy.script.record.PsychosisRecordListView"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisRecord</p>  
				<p name="serviceId">chis.psychosisRecordService</p>  
				<p name="navField">manaUnitId</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="listServiceId">chis.publicService</p>
				<p name="listAction">queryRecordList</p>
			</properties>  
			<action id="createByEmpi" name="新建" iconCls="create" group="create"/>  
			<action id="modify" name="查看" iconCls="update" group="update"/>  
			<action id="psychosisRecordLogout" name="注销" iconCls="common_writeOff" group="update"/>  
			<!-- action id="check" name="注销核实" iconCls="common_writeOffCheck" group="update"/ -->  
			<action id="print" name="打印"/> 
		</module>  
		<module id="A06" name="待评估精神病档案" script="chis.script.CombinedDocList"
			type="1">
			<properties>
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisRecordWorkList</p>
				<p name="manageUnitField">b.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.psy.PSY/PSY/A06_1" />
		</module>
		<module id="A06_1" name="精神病档案列表"
			script="chis.application.wl.script.MyWorkPsychosisRecordList" type="1">
			<properties>
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisRecordWorkList</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
		</module>
		<module id="I01-1-1" name="精神病档案" script="chis.application.psy.script.record.PsychosisRecordModule" type="1"> 
			<properties> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="PsyRecordForm" name="精神病档案" ref="chis.application.psy.PSY/PSY/I01-1-1-1" type="tab"/>  
			<action id="PsyFirstVisitModule" name="精神病首次随访" ref="chis.application.psy.PSY/PSY/I01-1-1-2" type="tab"/> 
			<action id="PsyPaperForm" name="精神病档案" ref="chis.application.psy.PSY/PSY/PSY01_01" type="tab"/>  
			<action id="PsyPaperFirstVisitForm" name="精神病首次随访" ref="chis.application.psy.PSY/PSY/PSY01_02" type="tab"/> 
		</module>  
		<module id="I01-1-1-1" name="精神病档案" script="chis.application.psy.script.record.PsychosisRecordForm" type="1"> 
			<properties> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="check" name="健康检查" iconCls="update"/>
		</module>  
		<module id="I01-1-1-2" script="chis.application.psy.script.record.PsychosisFirstVisitModule" name="精神病首次随访" type="1"> 
			<properties> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="PsyFirstVisitForm" name="精神病首次随访信息" ref="chis.application.psy.PSY/PSY/I01-1-1-2-1"/>  
			<action id="PsyFirstVisitMedicineList" name="精神病首次随访服药情况" ref="chis.application.psy.PSY/PSY/I01-1-1-2-2"/> 
		</module>  
		<module id="I01-1-1-2-1" script="chis.application.psy.script.record.PsychosisFirstVisitForm" name="精神病首次随访信息" type="1"> 
			<properties> 
				<p name="isAutoScroll">true</p> 
			</properties> 
			<action id="save" name="确定" group="create||update"/> 
		</module>  
		<module id="I01-1-1-2-2" script="chis.application.psy.script.record.PsychosisFirstVisitMedicineList" name="精神病首次随访服药情况" type="1"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisVisitMedicine</p> 
			</properties>  
			<action id="add" name="服药情况增加"/>  
			<action id="delete" name="删除" iconCls="remove"/> 
		</module>  
		<!--==================PSY**纸质化##s==================-->
		<module id="PSY01_01" name="精神病档案" script="chis.application.psy.script.html.PsychosisRecordHtmlForm" type="1">
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisRecord</p> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="check" name="健康检查" iconCls="update"/>
		</module>
		<module id="PSY01_02" name="精神病首次随访" script="chis.application.psy.script.html.PsychosisFirstVisitHtmlForm" type="1">
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisFirstVisit</p> 
				<p name="medicineEntryName">chis.application.psy.schemas.PSY_PsychosisVisitMedicine</p> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>
		<module id="PYS02_02" name="随访基本信息" script="chis.application.psy.script.html.PsychosisVisitHtmlForm" type="1">
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisVisit</p> 
				<p name="medicineEntryName">chis.application.psy.schemas.PSY_PsychosisVisitMedicine</p> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>
		<!--==================PSY**纸质化##e==================-->
		<module id="I01-1-2" name="精神病普通随访" script="chis.application.psy.script.visit.PsychosisVisitModule" type="1"> 
			<properties> 
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="PsyVisitList" name="精神病普通随访列表" ref="chis.application.psy.PSY/PSY/I01-1-2-0"/>  
			<action id="PsyVisitForm" name="随访基本信息" ref="chis.application.psy.PSY/PSY/I01-1-2-1" type="tab"/>
			<action id="PsyVisitPaperForm" name="随访基本信息" ref="chis.application.psy.PSY/PSY/PYS02_02" type="tab"/>  
			<action id="PsyVisitMedicineList" name="服药情况" ref="chis.application.psy.PSY/PSY/I01-1-2-2" type="tab"/> 
			<action id="PsyHealthGuidanceForm" name="健康指导" ref="chis.application.psy.PSY/PSY/I01-1-2-3" type="tab"/> 
		</module>  
		<module id="I01-1-2-0" name="精神病普通随访列表" script="chis.application.psy.script.visit.PsychosisVisitPlanList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>  
				<p name="serviceId">chis.psychosisVisitService</p> 
			</properties> 
		</module>  
		<module id="I01-1-2-1" name="随访基本信息" script="chis.application.psy.script.visit.PsychosisVisitForm" type="1"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisVisit</p>  
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>  
		<module id="I01-1-2-2" name="服药情况" script="chis.application.psy.script.visit.PsychosisVisitMedicineList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisVisitMedicine</p> 
			</properties>  
			<action id="add" name="服药情况增加" group="update"/>  
			<action id="delete" name="删除" iconCls="remove" group="update"/> 
		</module>  
		<module id="I01-1-2-3" name="健康指导" type="1" script="chis.application.psy.script.visit.PsychosisHealthGuidanceForm"> 
			<properties> 
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord_JSBSF</p>  
				<p name="loadServiceId">chis.psychosisVisitService</p>  
				<p name="loadAction">getPsyHealthGuidance</p>  
				<p name="saveServiceId">chis.psychosisVisitService</p>  
				<p name="saveAction">savePsyHealthGuidance</p>
				<p name="refRecipeImportModule">chis.application.psy.PSY/PSY/I01-1-2-3-1</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="printRecipe" name="打印健康处方"  iconCls="print" /> 
		</module>  
		<module id="I01-1-2-3-1" name="引入健康处方" type="1"
			script="chis.application.psy.script.visit.HealthRecipeImportModule">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthTeach_S</p>
				<p name="refRecipeList">chis.application.psy.PSY/PSY/I01-1-2-3-2</p>
				<p name="refRecipeHasList">chis.application.psy.PSY/PSY/I01-1-2-3-3</p>
			</properties>
		</module>
		<module id="I01-1-2-3-2" name="引入健康处方列表" type="1"
			script="chis.application.psy.script.visit.HealthRecipeImportList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthTeach_S</p>
			</properties>
		</module>
		<module id="I01-1-2-3-3" name="已引入健康处方列表" type="1"
			script="chis.application.psy.script.visit.HealthRecipeHasImportList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthTeach_S</p>
			</properties>
		</module>
		<module id="I01-1-3" name="精神病记录纸整体模块" type="1" script="chis.application.psy.script.paper.RecordPaperModule"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_RecordPaper</p> 
			</properties>  
			<action id="RecordPaperList" name="记录纸列表列表" ref="chis.application.psy.PSY/PSY/I01-1-3-1"/>  
			<action id="RecordPaperForm" name="记录纸表单" ref="chis.application.psy.PSY/PSY/I01-1-3-2"/> 
		</module>  
		<module id="I01-1-3-1" name="记录纸列表列表" type="1" script="chis.application.psy.script.paper.RecordPaperList"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_RecordPaper</p> 
			</properties> 
		</module>  
		<module id="I01-1-3-2" name="记录纸表单" type="1" script="chis.application.psy.script.paper.RecordPaperForm"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_RecordPaper</p>  
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="add" name="新增" group="create"/> 
		</module>  
		<module id="I01-1-4" name="精神病年度评估" type="1" script="chis.application.psy.script.assessment.AnnualAssessmentModule"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_AnnualAssessment</p> 
			</properties>  
			<action id="AssessmentList" name="评估列表" ref="chis.application.psy.PSY/PSY/I01-1-4-1"/>  
			<action id="AssessmentForm" name="评估表单" ref="chis.application.psy.PSY/PSY/I01-1-4-2"/> 
		</module>  
		<module id="I01-1-4-1" name="评估列表" type="1" script="chis.application.psy.script.assessment.AnnualAssessmentList"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_AnnualAssessment</p> 
			</properties> 
		</module>  
		<module id="I01-1-4-2" name="评估表单" type="1" script="chis.application.psy.script.assessment.AnnualAssessmentForm"> 
			<properties> 
				<p name="entryName">chis.application.psy.schemas.PSY_AnnualAssessment</p>  
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="add" name="新增" group="create"/> 
		</module>
		<module id="I02" name="精神病随访记录" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionVisit</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.psy.PSY/PSY/I02-1" />
		</module>
		<module id="I02-1" name="精神病随访列表" type="1" script="chis.application.psy.script.visit.PsychosisVisitPlanListView">
			<properties>
				<p name="entryName">chis.application.psy.schemas.PSY_PsychosisVisitPlanListView</p>
				<p name="listServiceId">chis.psychosisVisitService</p>  
				<p name="listAction">listPsychosisVistPlan</p>  
			</properties>
			<action id="modify" name="随访" iconCls="update" />
			<action id="print" name="打印" />
		</module>
	</catagory>
</application>