<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.mhc.MHC" name="孕妇档案"  type="1">
	<catagory id="MHC" name="孕妇档案管理">
		<module id="G01" name="孕妇档案管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantRecord</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">a.homeAddress</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" 	ref="chis.application.mhc.MHC/MHC/G0101" />
		</module>
		<module id="G0101" name="孕妇档案列表" type="1" 	script="chis.application.mhc.script.record.PregnantRecordList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="screen" name="产前筛查" iconCls="hypertension_visit" />
			<action id="endManage" name="终止妊娠" iconCls="close"  	ref="chis.application.mhc.MHC/MHC/G0101_3" />
			<action id="writeOff" name="注销" iconCls="remove"   	ref="chis.application.mhc.MHC/MHC/G0101_4" />
			<action id="print" name="打印" />
		</module>
		<module id="G0101_2" name="产前筛查" type="1"  script="chis.application.mhc.script.record.PregnantScreenForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantScreen</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePregnantScreen</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="G0101_1" name="孕妇档案管理整体模块"  script="chis.application.mhc.script.record.PregnantRecordModule" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePregnantRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="BaseRecord" name="孕妇档案" ref="chis.application.mhc.MHC/MHC/G0101_1_1" />
			<action id="PregnantIndex" name="孕妇检查" ref="chis.application.mhc.MHC/MHC/G0101_1_3" />
			<action id="FirstVisit" name="产检随访" ref="chis.application.mhc.MHC/MHC/G0101_1_2" />
			<action id="FirstVisitHtml" name="第1次产前随访服务记录表" ref="chis.application.mhc.MHC/MHC/G0101_1_2_html" />
		</module>
		<module id="G0101_1_1" name="孕妇基本信息表单" type="1"  script="chis.application.mhc.script.record.PregnantRecordForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePregnantRecord</p>
				<p name="loadServiceId">chis.pregnantRecordService</p>
				<p name="loadAction">getPregnantRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="G0101_1_2" name="孕妇产检随访信息表单" type="1"  	script="chis.application.mhc.script.record.PregnantFirstVisitForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_FirstVisitRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="refHighRiskModule">chis.application.mhc.MHC/MHC/GHR</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="G0101_1_2_html" name="孕妇产检第1次随访信息表单" type="1"  	script="chis.application.mhc.script.record.PregnantFirstVisitFormHtml">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_FirstVisitRecord_html</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="refHighRiskModule">chis.application.mhc.MHC/MHC/GHR</p>
				<p name="loadServiceId">chis.pregnantRecordService</p>
				<p name="loadAction">loadHtmlData</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="printPregnantRecord" name="打印" iconCls="update"/>
		</module>
		<module id="G0101_1_3" name="孕妇检查" type="1"  script="chis.application.mhc.script.record.PregnantIndexModule">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantWomanIndex</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
			</properties>
			<action id="indexList" name="孕妇检查列表" ref="chis.application.mhc.MHC/MHC/G0101_1_3_1" />
		</module>
		<module id="G0101_1_3_1" name="孕妇检查列表" type="1"  script="chis.application.mhc.script.record.PregnantIndexList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantWomanIndex</p>
				<p name="saveAction">saveVisitRecord</p>
				<p name="listAction">getPregnantCheckUp</p>
				<p name="listServiceId">chis.pregnantRecordService</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		<module id="G0101_3" name="孕妇终止妊娠表单" type="1"  script="chis.application.mhc.script.record.PostnatalEndManageForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_EndManagement</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">saveEndManage</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="G0101_4" name="孕妇档案注销表单" type="1"  script="chis.application.mhc.script.record.PregnantRecordWriteOffForm">
			<properties>
				<p name="entryName">chis.application.pub.schemas.EHR_WriteOff</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="G02" name="孕妇随访记录管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_VisitRecord</p>
				<p name="manageUnitField">d.manaUnitId</p>
				<p name="areaGridField">d.homeAddress</p>
			</properties>
			<action id="list" name="列表视图" viewType="list"  ref="chis.application.mhc.MHC/MHC/G0201" />
		</module>
		<module id="G0201" name="孕妇随访记录管理列表" type="1"  script="chis.application.mhc.script.visit.PregnantVisitRecordList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_VisitRecord</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="G0201_1" name="孕妇产检随访管理整体模块" type="1"  script="chis.application.mhc.script.visit.PregnantVisitModule">
			<properties>
				<p name="saveServiceId">chis.pregnantRecordService</p>
			</properties>
			<action id="PregnantVisitPlan" name="孕妇随访计划列表"  ref="chis.application.mhc.MHC/MHC/G0201_1_0" />
			<action id="PregnantVisitForm" name="孕妇随访信息表单"  ref="chis.application.mhc.MHC/MHC/G0201_1_1" />
			<action id="PregnantVisitFormHtml" name="孕妇随访信息表单"  ref="chis.application.mhc.MHC/MHC/G0201_1_1_html" />
		</module>
		<module id="G0201_1_0" name="孕妇随访计划列表" type="1"  script="chis.application.mhc.script.visit.PregnantVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan_Pregnant</p>
			</properties>
		</module>
		<module id="G0201_1_1" name="孕妇产检随访管理表单" type="1"  	script="chis.application.mhc.script.visit.PregnantVisitForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_VisitRecord</p>
				<p name="saveServiceId">chis.pregnantVisitService</p>
				<p name="saveAction">saveVisitRecord</p>
				<p name="refHighRiskModule">chis.application.mhc.MHC/MHC/GHR</p>
				<p name="refCheckUpModule">chis.application.mhc.MHC/MHC/G0101_1_3</p>
				<p name="refDescriptionForm">chis.application.mhc.MHC/MHC/G0201_1_1_1</p>
				<p name="refFetalsForm">chis.application.mhc.MHC/MHC/G0201_1_1_2</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="checkUp" name="孕妇检查" iconCls="mhc_checkUp" />
			<action id="highRisk" name="高危评估" iconCls="mhc_highRisk" />
			<action id="description" name="中医辩体描述" iconCls="mhc_description" />
			<action id="fetals" name="多胞胎信息" iconCls="mhc_fetals" />
			<action id="printVisit" name="打印" iconCls="print" />
		</module>
		<module id="G0201_1_1_html" name="孕妇产检随访管理表单" type="1"  	script="chis.application.mhc.script.visit.PregnantVisitFormHtml">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_VisitRecord_Html</p>
				<p name="saveServiceId">chis.pregnantVisitService</p>
				<p name="saveAction">saveVisitRecord</p>
				<p name="refHighRiskModule">chis.application.mhc.MHC/MHC/GHR</p>
				<p name="refCheckUpModule">chis.application.mhc.MHC/MHC/G0101_1_3</p>
				<p name="refDescriptionForm">chis.application.mhc.MHC/MHC/G0201_1_1_1</p>
				<p name="refFetalsForm">chis.application.mhc.MHC/MHC/G0201_1_1_2</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="checkUp" name="孕妇检查" iconCls="mhc_checkUp" />
			<action id="highRisk" name="高危评估" iconCls="mhc_highRisk" />
			<action id="description" name="中医辩体描述" iconCls="mhc_description" />
			<action id="fetals" name="多胞胎信息" iconCls="mhc_fetals" />
			<action id="printVisit" name="打印" iconCls="print" />
		</module>
		<module id="G0201_1_1_1" name="中医辩体" type="1"  script="chis.application.mhc.script.visit.PregnantVisitDescriptionForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_VisitRecordDescription</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="G0201_1_1_2" name="多胞胎信息列表" type="1"  script="chis.application.mhc.script.visit.FetalRecordList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_FetalRecord</p>
			</properties>
			<action id="create" name="新增" group="update" />
			<action id="save" name="确定" group="update" />
			<action id="remove" name="删除" group="update" />
		</module>
		<module id="G04" name="产后42天健康检查" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_Postnatal42dayRecord</p>
				<p name="manageUnitField">a.checkManaUnit</p>
				<p name="areaGridField">d.homeAddress</p>
			</properties>
			<action id="list" name="列表视图" viewType="list"  ref="chis.application.mhc.MHC/MHC/G0401" />
		</module>
		<module id="G0401" name="产后42天健康检查记录列表" type="1"  script="chis.application.mhc.script.postnatal42day.Postnatal42dayRecordList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_Postnatal42dayRecord</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create" group="update" />
			<action id="modify" name="查看" iconCls="update" group="update" />
		</module>
		<module id="G04-1" name="产后42天健康检查记录表单"  script="chis.application.mhc.script.postnatal42day.Postnatal42dayRecordForm" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_Postnatal42dayRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePostnatal42dayRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="G04-11" name="产后42天健康检查记录表单"  script="chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlForm" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_Postnatal42dayRecord_html</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePostnatal42dayRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="G04-2" name="孕妇产后访视信息整体模块"  	script="chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoModule" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PostnatalVisitInfo</p>
			</properties>
			<action id="PostnatalVisitInfoList" name="孕妇产后访视信息列表"  	ref="chis.application.mhc.MHC/MHC/G04-2-1" />
			<action id="PostnatalVisitInfoForm" name="孕妇产后访视信息表单" 	ref="chis.application.mhc.MHC/MHC/G04-2-2" />
		</module>
		<module id="G04-21" name="孕妇产后访视信息整体模块"  	script="chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoModule" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PostnatalVisitInfo</p>
			</properties>
			<action id="PostnatalVisitInfoList" name="孕妇产后访视信息列表"  	ref="chis.application.mhc.MHC/MHC/G04-2-1" />
			<action id="PostnatalVisitInfoForm" name="孕妇产后访视信息表单" 	ref="chis.application.mhc.MHC/MHC/G04-2-21" />
		</module>
		<module id="G04-2-1" name="孕妇产后访视信息列表"  script="chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoList" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PostnatalVisitInfo</p>
			</properties>
		</module>
		<module id="G04-2-2" name="孕妇产后访视信息表单"  script="chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoForm" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PostnatalVisitInfo</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePostnatalVisitInfo</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" group="update" />
		</module>
		<module id="G04-2-21" name="孕妇产后访视信息表单"  script="chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlForm" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PostnatalVisitInfo_html</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">savePostnatalVisitInfo</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="G10-1" name="新生儿访视" script="chis.application.mhc.script.babyVisit.BabyVisitModule"
			type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitInfo</p>
			</properties>
			<action id="BabyVisitInfoList" name="新生儿访视信息列表" ref="chis.application.mhc.MHC/MHC/G10-1-1" />
			<action id="BabyVisitInfoForm" name="新生儿访视信息表单" ref="chis.application.mhc.MHC/MHC/G10-1-2" />
		</module>
		<module id="G10-1-1" name="新生儿访视信息列表"  script="chis.application.mhc.script.babyVisit.BabyVisitInfoList" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitInfo</p>
				<p name="refModule">chis.application.mhc.MHC/MHC/G10-2</p>
			</properties>
			<action id="make" name="新建" iconCls="create" group="update" />
			<action id="visit" name="随访" iconCls="hypertension_visit" />
		</module>
		<module id="G10-1-2" name="新生儿访视信息表单"  script="chis.application.mhc.script.babyVisit.BabyVisitInfoForm" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitInfo</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">saveBabyVisitInfo</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="G10-2" name="新生儿随访"  script="chis.application.mhc.script.babyVisit.BabyVisitRecordModule" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitRecord</p>
			</properties>
			<action id="BabyVisitRecordList" name="新生儿随访信息列表"  	ref="chis.application.mhc.MHC/MHC/G10-2-1" />
			<action id="BabyVisitRecordForm" name="新生儿随访信息表单"  	ref="chis.application.mhc.MHC/MHC/G10-2-2" />
		</module>
		<module id="G10-2-1" name="新生儿随访信息列表"  script="chis.application.mhc.script.babyVisit.BabyVisitRecordList" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitRecord</p>
			</properties>
			<action id="make" name="新建" iconCls="create" group="update" />
		</module>
		<module id="G10-2-2" name="新生儿随访信息表单"  script="chis.application.mhc.script.babyVisit.BabyVisitRecordForm" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">saveBabyVisitRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="G0501" name="孕产妊娠图" type="1"	script="chis.script.gis.powerChartView">
			<properties>
				<p name="entryName">chis.report.CHART_Pregant</p>
			</properties>
		</module>
		<module id="G06" name="孕妇特殊情况整体模块" type="1"  script="chis.application.mhc.script.special.PregnantSpecialModule">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantSpecial</p>
			</properties>
			<action id="SpecialList" name="孕妇特殊情况列表" ref="chis.application.mhc.MHC/MHC/G06_1" />
			<action id="SpecialForm" name="孕妇特殊情况表单" ref="chis.application.mhc.MHC/MHC/G06_2" />
		</module>
		<module id="G06_1" name="孕妇特殊情况列表" type="1" 	script="chis.application.mhc.script.special.PregnantSpecialList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantSpecial</p>
			</properties>
		</module>
		<module id="G06_2" name="孕妇特殊情况表单" type="1"	script="chis.application.mhc.script.special.PregnantSpecialForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantSpecial</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">saveSpecialRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" iconCls="create" group="update" />
		</module>
		<module id="GHR" name="高危评估" type="1"	script="chis.application.mhc.script.visit.HighRiskVisitReasonModule">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_HighRiskVisitReason</p>
				<p name="title">高危评估</p>
			</properties>
			<action id="HighRiskVisitReasonList" name="高危因素列表"  ref="chis.application.mhc.MHC/MHC/GHR01" />
		</module>
		<module id="GHR01" name="高危因素列表" type="1" 		script="chis.application.mhc.script.visit.HighRiskVisitReasonList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_HighRiskVisitReason</p>
			</properties>
			<action id="confirm" name="确定" iconCls="archiveMove_commit" 	group="update" />
			<action id="remove" name="删除" group="update" />
		</module>
		<module id="G11" name="终止妊娠一览表" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantStopManage</p>
				<p name="manageUnitField">c.manaUnitId</p>
				<p name="areaGridField">c.homeAddress</p>
			</properties>
			<action id="list" name="列表视图" viewType="list"  ref="chis.application.mhc.MHC/MHC/G11-1" />
		</module>
		<module id="G11-1" type="1" name="孕产妇终止妊娠一览表"  script="chis.application.mhc.script.record.PregnantStopManageList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_PregnantStopManage</p>
				<p name="navDic">chis.dictionary.areaGrid</p>
				<p name="navField">homeAddress</p>
				<p name="listServiceId">chis.pregnantRecordService</p>
				<p name="listAction">getEndManagementRecord</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="G12" name="儿童产时记录表单"  	script="chis.application.mhc.script.delivery.ChildrenDeliveryRecordForm1" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
		</module>
		<module id="G14" name="孕妇高危一览表" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_HighRiskVisitReasonList</p>
				<p name="manageUnitField">c.manaUnitId</p>
				<p name="areaGridField">c.homeAddress</p>
			</properties>
			<action id="list" name="列表视图" viewType="list"  ref="chis.application.mhc.MHC/MHC/G1401" />
		</module>
		<module id="G1401" name="孕妇高危信息一览表" type="1"  script="chis.application.mhc.script.visit.PregnantHighRiskReasonList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_HighRiskVisitReasonList</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="print" name="打印" />
		</module>
		<module id="G17" name="孕产妇死亡报告卡" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.DEA_DeathReportCard</p>  
				<p name="manageUnitField">a.createUnit</p>  
				<p name="areaGridField">a.permanentRegionCode</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.mhc.MHC/MHC/G17_01"/> 
		</module>  
		<module id="G17_01" name="孕产妇死亡报告卡列表" type="1" script="chis.application.mhc.script.dea.PrenatalDeathReportList"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.DEA_DeathReportCard</p> 
			</properties>  
			<action id="createByEmpi" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="print" name="打印"/> 
		</module>  
		<module id="G17_02" name="孕产妇死亡报告卡表单" type="1" script="chis.application.mhc.script.dea.PrenatalDeathReportForm"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.DEA_DeathReportCard</p>  
				<p name="saveAction">saveDeathReport</p>  
				<p name="loadAction">loadInitPreDeaRepData</p>  
				<p name="saveServiceId">chis.deathReportCardService</p>  
				<p name="loadServiceId">chis.deathReportCardService</p>  
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/>  
			<action id="print" name="打印"/> 
		</module> 
		<module id="G20" name="产时管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryOnRecord</p>
				<p name="manageUnitField">a.deliveryUnit</p>
				<p name="areaGridField">c.homeAddress</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.mhc.MHC/MHC/G20_01"/>
		</module>
		<module id="G20_01" name="产妇分娩记录列表" script="chis.application.mhc.script.delivery.DeliveryRecord2List" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryOnRecord</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="print" name="打印"/>
		</module>
		<module id="G20_02" name="产妇分娩记录及新生儿登记模块" script="chis.application.mhc.script.delivery.DeliveryRecord2Module" type="1">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryOnRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="DeliveryRecord2Form" name="产妇分娩记录" ref="chis.application.mhc.MHC/MHC/G20_03"/>
			<action id="DeliveryRecord2ChildForm" name="新生儿登记" ref="chis.application.mhc.MHC/MHC/G20_04"/>
		</module>
		<module id="G20_03" name="产时信息记录表单" type="1" script="chis.application.mhc.script.delivery.DeliveryRecord2Form">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryOnRecord</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">saveDeliveryRecord</p>
				<p name="controlActions">update</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update"/>
		</module>
		<module id="G20_04" name="产妇所生新生儿登记模块" script="chis.application.mhc.script.delivery.DeliveryChildrenRecordModule" type="1">
			<action id="MHC_DeliveryRecordChild" name="新生儿登记列表" ref="chis.application.mhc.MHC/MHC/G20_05"/>
			<action id="MHC_DeliveryRecordChildForm" name="新生儿记表单" ref="chis.application.mhc.MHC/MHC/G20_06"/>
		</module>
		<module id="G20_05" name="新生儿登记列表" type="1" script="chis.application.mhc.script.delivery.DeliveryChildrenRecordList">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryOnRecordChild</p>
			</properties>
		</module>
		<module id="G20_06" name="新生儿记表单" type="1" script="chis.application.mhc.script.delivery.DeliveryChildrenRecordForm">
			<properties>
				<p name="entryName">chis.application.mhc.schemas.MHC_DeliveryOnRecordChild</p>
				<p name="saveServiceId">chis.pregnantRecordService</p>
				<p name="saveAction">saveDeliveryChildren</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update"/>
			<action id="create" name="新建" iconCls="create" group="update"/>
		</module>
		<!--新生儿访视 html-->
		<module id="G21-1" name="新生儿访视html" script="chis.application.mhc.script.deliveryHtml.DeliveryChildrenRecordHtmlModul" type="1"> 
			<action id="DebilityRecordHtmlList" name="新生儿列表" ref="chis.application.mhc.MHC/MHC/G21-2"/>  
			<action id="DebilityRecordHtmlForm" name="基本信息" ref="chis.application.mhc.MHC/MHC/G21-3" type="tab"/>  
			<action id="DebilityVisitHtmlModul" name="随访记录" ref="chis.application.mhc.MHC/MHC/G21-4" type="tab"/> 
		</module>  
		<module id="G21-2" name="新生儿列表" script="chis.application.mhc.script.deliveryHtml.DebilityChildrenHtmlList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitInfo_list</p> 
			</properties> 
		</module> 
		
		<module id="G21-3" name="新生儿基本信息" script="chis.application.mhc.script.deliveryHtml.DebilityChildrenRecordHtmlForm" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitInfo_text</p> 
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="create" name="新建" group="update"/>  
		</module>  
		
		<module id="G21-4" name="新生儿随访记录"  script="chis.application.mhc.script.deliveryHtml.DeliveryChildrenvisitHtmlModul" type="1"> 
			<action id="VisitHtmlList" name="随访计划" ref="chis.application.mhc.MHC/MHC/G21-5"/>  
			<action id="VisitFormHtmlView" name="随访基本信息" ref="chis.application.mhc.MHC/MHC/G21-6"/>  
		</module>
		
		<module id="G21-5" name="随访记录" script="chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitRecordList</p> 
			</properties> 
		</module>  
		<module id="G21-6" name="随访基本信息" script="chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlform" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mhc.schemas.MHC_BabyVisitRecord_html</p>  
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="create" name="新建" group="update"/> 
			<action id="printVisitRecord" name="打印" iconCls="print"/>
		</module> 
		 
	</catagory>
</application>