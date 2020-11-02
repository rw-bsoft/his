<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.cic.CIC" name="门诊管理" type="3">
	<catagory id="CIC" name="门诊管理">
		<module id="CIC90" name="日历" type="1"
			script="phis.application.cic.script.TestModule">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_BQ</p>
			</properties>
			<action id="test1" name="测试1" iconCls="commit" />
			<action id="test2" name="测试2" iconCls="commit" />
			<action id="test3" name="测试2" iconCls="commit" />
		</module>
		<module id="CIC00" name="辖区病人"
			script="phis.application.cic.script.ClinicAreaPatientList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BRDA_CIC</p>
				<p name="listServiceId">clinicManageService</p>
			</properties>
			<action id="newPerson" name="新建" iconCls="new" />
			<action id="updatePerson" name="修改" iconCls="update" />
			<action id="save" name="就诊" iconCls="commit" />
			<action id="pay" name="支付" iconCls="commit"  type="1" />
			<!--<action id="dyhzxx" name="EHRView" iconCls="ehrview"/> -->
			<!--<action id="mpi" name="mpi测试" iconCls="commit"/ -->
			<!--action id="smk" name="市民卡" iconCls="commit"/ -->
		</module>
		<module id="PatientList" name="病人列表"
			script="phis.application.cic.script.ClinicPeopleList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_GHMX_MZ</p>
			</properties>
			<action id="new" name="新增" />
			<!--拱墅版本中的功能暂时隐藏,如果需要则要创建PD_ZSQK和PD_JZLB两张表 <action id="call" name="叫号" 
				iconCls="commit"/> <action id="skip" name="跳过" iconCls="commit"/> -->
			<action id="save" name="就诊" iconCls="commit" />
			<action id="jkk" name="健康卡" iconCls="ransferred_all"/>
			<!-- <action id="cancel" name="关闭" iconCls="common_cancel"/> -->
		</module>
		<module id="CIC0001" name="公卫用户列表" type="1"
			script="com.bsoft.phis.pub.UserCollateList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GW_YGXX_CIC</p>
				<p name="autoLoadData">0</p>
				<p name="disablePagingTbr">0</p>
				<p name="closeAction">hide</p>
				<p name="modal">true</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC01" name="门诊病历" type="1"
			script="phis.application.cic.script.ClinicMedicalRecordModule">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YK_TYPK_CIC</p>
				<p name="refTheTreatmentForm">phis.application.cic.CIC/CIC/CIC0101</p>
				<p name="refSkinTestForm">phis.application.cic.CIC/CIC/CIC0102</p>
				<p name="refSkinTestHistroyList">phis.application.cic.CIC/CIC/CIC03</p>
				<p name="refDiagnosisEntryModule">phis.application.cic.CIC/CIC/CIC04</p>
				<p name="refPrescriptionEntryModule">phis.application.cic.CIC/CIC/CIC05</p>
				<p name="refDisposalEntryModule">phis.application.cic.CIC/CIC/CIC06</p>
				<p name="refClinicHistroyList">phis.application.cic.CIC/CIC/CIC0104</p>
				<p name="refClinicTherapeuticList">phis.application.cic.CIC/CIC/CIC0105</p>
				<p name="refRecipeImportModule">phis.application.cic.CIC/CIC/CIC0106</p>
				<p name="refFZJCModule">phis.application.cic.CIC/CIC/CIC109</p>
				<!--【中医馆】知识库-->
				<p name="refKnowledgeBase">phis.application.tcm.TCM/TCM/TCM05</p>
				<!--【中医馆】电子病历-->
				<p name="refClinicMedicalRecordTcm">phis.application.tcm.TCM/TCM/TCM06</p>
				<!--【中医馆】辩证论治-->
				<p name="refSyndromeDifferentiationAndRreatment">phis.application.tcm.TCM/TCM/TCM07</p>
				<!--【中医馆】治未病-->
				<p name="refPreventiveTreatmentOfDiseases">phis.application.tcm.TCM/TCM/TCM08</p>
				<!--【中医馆】远程教育-->
				<p name="refDistanceLearning">phis.application.tcm.TCM/TCM/TCM09</p>
				<!--【中医馆】远程会诊-->
				<p name="refTelemedicine">phis.application.tcm.TCM/TCM/TCM10</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CIC01010101" name="门诊病历打印" type="1"
			script="phis.prints.script.PatientMedicalRecordsPrintView">
		</module>
		<module id="CIC0101" name="就诊信息" type="1"
			script="phis.application.cic.script.TheTreatmentForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CCJZ</p>
				<p name="fzyyEntryName">phis.application.cic.schemas.MS_YYGH</p>
				<p name="refSettlementModule">phis.application.cic.CIC/CIC/CIC010101</p>
			</properties>
			<action id="settlement" name="结算" iconCls="coins" />
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
			<!--<action id="fyjs" name = "市民卡费用结算" iconCls="coins"/> <action id="test" 
				name = "千万别点" /> -->
		</module>
		<module id="CIC010101" name="门诊结算" type="1"
			script="phis.application.cic.script.ClinicSettlementMsModule">
			<properties>
				<p name="refSettlementForm">phis.application.ivc.IVC/IVC/IVC01010301</p>
				<p name="refSettlementPrint">phis.application.ivc.IVC/IVC/IVC0205</p>
			</properties>
		</module>
		<module id="CIC0102" name="皮试结果录入"
			script="phis.application.cic.script.SkinTestRecordForm" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_GHYY</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CIC0103" name="过敏药品记录"
			script="phis.application.cic.script.ClinicPatientAllergyMedList" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_PSJL_MZ</p>
				<p name="modal">true</p>
				<p name="autoLoadData">0</p>
			</properties>
			<action id="create" name="新增" />
			<action id="save" name="保存" />
			<action id="remove" name="删除" />
		</module>
		<module id="CIC0104" name="病历模板列表" type="1"
			script="phis.application.cic.script.MedicalHistoryModule">
			<properties>
				<p name="refMedicalHistoryList">phis.application.cic.CIC/CIC/CIC010401</p>
				<p name="refMedicalHistoryDetailList">phis.application.cic.CIC/CIC/CIC010402</p>
				<p name="closeAction">hide</p>
			</properties>
		</module>
		<module id="CIC010401" name="病历模板" type="1"
			script="phis.application.cic.script.MedicalHistoryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_BLMB_Z</p>
				<p name="queryWidth">80</p>
			</properties>
			<action id="import" name="调入" iconCls="commit" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC010402" name="右边模板" type="1"
			script="phis.application.cic.script.MedicalHistoryForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_BLMB_BL</p>
				<p name="refRecipeImportModule">phis.application.cic.CIC/CIC/CIC0106</p>
			</properties>
		</module>
		<module id="CIC0105" name="诊疗方案列表" type="1"
			script="phis.application.cic.script.ClinicTherapeuticList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_ZLFA01</p>
				<p name="closeAction">hide</p>
			</properties>
			<action id="import" name="调入" iconCls="commit" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC0106" name="引入健康处方" type="1"
			script="phis.application.cic.script.HealthRecipeImportModule">
			<properties>
				<p name="entryName">phis.application.cic.schemas.PUB_PelpleHealthTeach</p>
				<p name="refRecipeList">phis.application.cic.CIC/CIC/CIC010601</p>
				<p name="refRecipeHasList">phis.application.cic.CIC/CIC/CIC010602</p>
			</properties>
		</module>
		<module id="CIC010601" name="引入健康处方列表" type="1"
			script="phis.application.cic.script.HealthRecipeImportList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.PUB_PelpleHealthTeach</p>
			</properties>
		</module>
		<module id="CIC010602" name="已引入健康处方列表" type="1"
			script="phis.application.cic.script.HealthRecipeHasImportList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.PUB_PelpleHealthTeach</p>
			</properties>
		</module>
		<module id="CIC0107" name="病历书写向导" type="1"
			script="phis.application.cic.script.BLSXXDModule">
			<properties>
			</properties>
			<action id="comfire" name="确定" iconCls="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="CIC02" name="就诊历史记录"
			script="phis.application.cic.script.ClinicHistoryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_JZLS_EMR</p>
				<p name="refCFMX">phis.application.cic.CIC/CIC/CIC0202</p>
				<p name="refCZMX">phis.application.cic.CIC/CIC/CIC15</p>
				<p name="listServiceId">clinicManageService</p>
				<p name="serviceAction">queryHistoryList</p>
			</properties>
			<action id="printCF" name="打印处方" iconCls="print" />
			<action id="printCZ" name="打印处置" iconCls="print" />
			<action id="printBL" name="打印病历" iconCls="print" />
		</module>
		<module id="CIC0201" type="1" name="就诊历史记录(EMR)"
			script="phis.application.cic.script.ClinicHistoryForEMRList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_JZLS</p>
			</properties>
		</module>
		<module id="CIC0202" type="1" name="处方打印"
			script="phis.application.cic.script.ClinicPrescriptionEntryQueryModule">
			<properties>
				<p name="clinicPrescriptionEntryForm">phis.application.cic.CIC/CIC/CIC0501</p>
				<p name="clinicPrescriptionEntryList">phis.application.cic.CIC/CIC/CIC0502</p>
			</properties>
		</module>
		<module id="CIC0203" name="住院病历记录" type="1"
			script="phis.application.cic.script.ClinicMedicalRecordsList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERYLIST_MZ</p>
				<p name="refMedicalDetails">phis.application.war.WAR/WAR/WAR340102</p>
				<p name="basyView">phis.application.war.WAR/WAR/WAR34020101</p>
			</properties>
			<action id="print" name="打印" />
			<action id="medicalDetails" name="病历详情" iconCls="report" />
			<action id="seeRecord" name="浏览病历" iconCls="report_magnify" />
		</module>
		<module id="CIC18" name="门诊工作日志"
			script="phis.application.cic.script.OutpatientWorkLogList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BCJL_RZ</p>
				<p name="listServiceId">clinicManageService</p>
				<p name="serviceAction">queryWorkLogs</p>
			</properties>
			<action id="query" name="查询" />
			<action id="print" name="打印" />
		</module>
		
		<module id="CIC17" name="门诊工作日志报表" type="1"
			script="phis.prints.script.OutpatientWorkLogPrintView">
		</module>
		<module id="CIC03" name="发药药房设置"
			script="phis.application.cic.script.ClinicOutPharmacySetForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_XTCS_MS</p>
				<p name="showWinOnly">true</p>
				<p name="colCount">1</p>
				<p name="width">400</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC04" name="诊断录入" type="1"
			script="phis.application.cic.script.ClinicDiagnosisEntryModule">
			<properties>
				<p name="clinicDiagnosisEntryList">phis.application.cic.CIC/CIC/CIC0401</p>
				<p name="clinicDiagnosisQuickInputTab">phis.application.cic.CIC/CIC/CIC0402</p>
			</properties>
		</module>
		<module id="CIC0401" name="初步诊断" type="1"
			script="phis.application.cic.script.ClinicDiagnosisEntryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BRZD_CIC</p>
			</properties>
			<action id="newClinic" name="新增诊断" iconCls="create" />
			<action id="subClinic" name="子诊断" iconCls="newclinic" />
			<action id="upClick" name="上移" iconCls="arrow-up" />
			<action id="downClick" name="下移" iconCls="arrow-down" />
			<action id="save" name="保存" />
			<action id="remove" name="删除" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>

		<module id="CIC0402" name="诊断快速输入tab" type="1"
			script="phis.application.cic.script.ClinicQuickInputTab">
			<action id="WestQuickInput" viewType="list" name="西医诊断快速输入"
				ref="phis.application.cic.CIC/CIC/CIC040201" />
			<action id="CenterQuickInput" viewType="list" name="中医诊断快速输入"
				ref="phis.application.cic.CIC/CIC/CIC040202" />
		</module>
		<module id="CIC040201" name="西医诊断快速输入tab" type="1"
			script="phis.application.cic.script.ClinicDiagnosisEntryQuickInputTab">
			<action id="clinicCommonZD" viewType="list" name="常用诊断"
				ref="phis.application.cic.CIC/CIC/CIC04020101" />
			<action id="clinicPositionZD" viewType="list" name="诊断部位"
				ref="phis.application.cic.CIC/CIC/CIC04020102" />
			<action id="clinicAllZD" viewType="list" name="诊断录入"
				ref="phis.application.cic.CIC/CIC/CIC04020103" />
		</module>
		<module id="CIC04020101" name="常用诊断list" type="1"
			script="phis.application.cic.script.ClinicdiagnosisOftenList">
			<properties>
				<p name="height">540</p>
				<p name="entryName">phis.application.cic.schemas.GY_CYZD_CIC</p>
				<p name="initCnd">['and',['eq',['$','SSLB'],['i',1]],['eq',['$','CFLX'],['i',1]]]
				</p>
			</properties>
		</module>
		<module id="CIC04020102" name="部位list" type="1"
			script="phis.application.cic.script.ClinicDiagnosisPositionList">
			<properties>
				<p name="height">540</p>
				<p name="entryName">phis.application.cic.schemas.MS_ZLBW_CIC</p>
			</properties>
		</module>
		<module id="CIC04020103" name="疾病list" type="1"
			script="phis.application.cic.script.ClinicDiagnosisAllList">
			<properties>
				<p name="height">540</p>
				<p name="entryName">phis.application.cic.schemas.GY_JBBM_MS</p>
			</properties>
		</module>

		<module id="CIC040202" name="中医诊断快速输入tab" type="1"
			script="phis.application.cic.script.ClinicDiagnosisEntryQuickInputZyTab">
			<action id="clinicCommonZy" viewType="list" name="常用诊断"
				ref="phis.application.cic.CIC/CIC/CIC04020202" />
			<action id="clinicCommonZh" viewType="list" name="症候"
				ref="phis.application.cic.CIC/CIC/CIC04020203" />
			<action id="clinicCommonAll" viewType="list" name="诊断录入"
				ref="phis.application.cic.CIC/CIC/CIC04020201" />
		</module>

		<module id="CIC04020201" name="中医诊断Tab" type="1"
			script="phis.application.cic.script.ClinicDiagnosisCenterQuickInputModule">
			<properties>
				<p name="refJBList">phis.application.cic.CIC/CIC/CIC0402020101</p>
				<p name="refZHList">phis.application.cic.CIC/CIC/CIC0402020102</p>
			</properties>
			<action id="zdmc" viewType="list" name="诊断名称" ref="CIC04020201" />
			<action id="zh" viewType="list" name="证侯" ref="CIC04020202" />
		</module>
		<module id="CIC0402020101" name="诊断名称" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYJB_ZD</p>
			</properties>
		</module>
		<module id="CIC0402020102" name="证侯" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYZH_ZD</p>
			</properties>
		</module>
		<module id="CIC04020203" name="证侯" type="1"
			script="phis.application.cic.script.ClinicSymptomsList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYZH_ZS</p>
			</properties>
		</module>
		<module id="CIC04020202" name="中医常用诊断Tab" type="1"
			script="phis.application.cic.script.ClinicDiagnosisCenterQuickInputCYModule">
			<properties>
				<p name="refJBList">phis.application.cic.CIC/CIC/CIC0402020201</p>
				<p name="refZHList">phis.application.cic.CIC/CIC/CIC0402020102</p>
			</properties>
			<action id="zdmcCY" viewType="list" name="诊断名称" ref="CIC0402020201" />
			<action id="zhCY" viewType="list" name="证侯" ref="CIC0402020102" />
		</module>
		<module id="CIC0402020201" name="常用诊断名称" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_CYZD_ZY</p>
				<p name="initCnd">['and',['eq',['$','SSLB'],['i',1]],['eq',['$','CFLX'],['i',2]]]
				</p>
			</properties>
		</module>

		<module id="CIC0403" name="传染病报告卡" type="1" script="phis.application.cic.script.IDR_ReportPhisModule" >
			<properties>
				<p name="crbBgkForm">phis.application.cic.CIC/CIC/CIC040301</p>
			</properties>
		</module>
		<module id="CIC040301" name="个人传染病表单" type="1" script="phis.application.cic.script.IDR_ReportPhisForm">
			<properties>
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>
				<p name="IDR_ReportPhisForm2">phis.application.cic.CIC/CIC/CIC040302</p>
				<p name="IDR_ReportPhisForm3">phis.application.cic.CIC/CIC/CIC040303</p>
				<p name="IDR_ReportPhisForm4">phis.application.cic.CIC/CIC/CIC040304</p>
				<p name="IDR_ReportPhisForm5">phis.application.cic.CIC/CIC/CIC040305</p>
				<p name="IDR_ReportPhisForm6">phis.application.cic.CIC/CIC/CIC040306</p>
				<p name="saveServiceId">phis.clinicManageService</p>
				<p name="saveAction">saveIdrReport</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
			<!--<action id="print"  name="打印"/>-->
			<action id="closeVerify"  name="关闭" iconCls="common_cancel"/>
		</module>
		 <module id="CIC040302" name="传染病记录表单-手足口病附卡" script="phis.application.cic.script.IdrReport_HFMD" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report_HFMD</p>
				<p name="serviceId">phis.clinicManageService</p>
			</properties>
		</module>
		 <module id="CIC040303" name="传染病记录表单-HIV附卡" script="phis.application.cic.script.IdrReport_HIV" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report_HIV</p>
				<p name="serviceId">phis.clinicManageService</p>
			</properties>
		</module>
		 <module id="CIC040304" name="传染病记录表单-AFP病附卡" script="phis.application.cic.script.IdrReport_AFP" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report_AFP</p>
				<p name="serviceId">phis.clinicManageService</p>
			</properties>
		</module>
		<module id="CIC040305" name="传染病记录表单-梅毒病例附卡" script="phis.application.cic.script.IdrReport_MD" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report_MD</p>
				<p name="serviceId">phis.clinicManageService</p>
			</properties>
		</module>
		<module id="CIC040306" name="传染病记录表单-乙肝病例附卡" script="phis.application.cic.script.IdrReport_HBV" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report_HBV</p>
				<p name="serviceId">phis.clinicManageService</p>
			</properties>
		</module>
		<module id="CIC0404" name="传染病审核" script="phis.application.cic.script.IdrReportListMoudle">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report</p>
				<p name="serviceId">phis.clinicManageService</p>
				<p name="navField">reportFlag</p>
				<p name="reportDetailForm">phis.application.cic.CIC/CIC/CIC040401</p>
			</properties>
			<action id="modify" name="查看" iconCls="report"/>
			<action id="print"  name="打印"/>
			<action id="report" name="上报" iconCls="report"/>
			<!--<action id="cancel" name="退回" iconCls="report"/>-->
		</module>
		<!-- add by zhj-->
		<module id="CIC0405" name="传染病列表" script="phis.application.cic.script.IdrReportListMoudle">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report</p>
				<p name="serviceId">phis.clinicManageService</p>
				<p name="navField">reportFlag</p>
				<p name="reportDetailForm">phis.application.cic.CIC/CIC/CIC040401</p>
			</properties>
			<action id="modify" name="查看" iconCls="report"/>
			<action id="print"  name="打印"/>
			<action id="report" name="上报" iconCls="report"/>
			<!--<action id="cancel" name="退回" iconCls="report"/>-->
		</module>
		<!-- add by zhj-->
		<module id="CIC040401" name="传染病记录表单" script="phis.application.cic.script.IdrReportListView" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report</p>				
				<p name="reportVerifyForm">phis.application.cic.CIC/CIC/CIC040402</p>
				<p name="serviceId">phis.clinicManageService</p>
				<p name="saveServiceId">phis.clinicManageService</p>
				<p name="saveAction">saveIdrReport</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
			<action id="verify" name="审核" iconCls="update"/>
			<action id="print"  name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="CIC040402" name="传染病记录表单审核" script="phis.application.cic.script.IdrReportVerifyForm" type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.IDR_Report_Verify</p>
				<p name="serviceId">phis.clinicManageService</p>
			</properties>
		</module>

		<module id="CIC05" name="处方录入" type="1"
			script="phis.application.cic.script.ClinicPrescriptionEntryModule">
			<properties>
				<p name="clinicPrescriptionEntryForm">phis.application.cic.CIC/CIC/CIC0501</p>
				<p name="clinicPrescriptionEntryList">phis.application.cic.CIC/CIC/CIC0502</p>
				<p name="clinicPrescriptionQuickInputTab">phis.application.cic.CIC/CIC/CIC0503</p>
				<p name="refFjList">phis.application.cic.CIC/CIC/IVC01010402</p>
			</properties>
		</module>
		<module id="CIC06" name="处置录入" type="1"
			script="phis.application.cic.script.ClinicDisposalEntryModule">
			<properties>
				<p name="refDisposalEntryList">phis.application.cic.CIC/CIC/CIC0601</p>
				<p name="refDisposalEntryTabList">phis.application.cic.CIC/CIC/CIC0602</p>
			</properties>
		</module>

		<module id="CIC07" name="常用组套维护"
			script="phis.application.cfg.script.AdvicePersonalComboModule">
			<properties>
				<p name="refComboNameList">phis.application.cic.CIC/CIC/CIC0701</p>
				<p name="refComboNameDetailList">phis.application.cic.CIC/CIC/CIC0702</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">saveCommonlyUsedDrugs</p>
				<p name="cnds">1</p>
			</properties>
			<action id="westernDrug" name="西药" value="1" />
			<action id="ChineseDrug" name="成药" value="2" />
			<action id="herbs" name="草药" value="3" />
			<action id="others" name="项目" value="4" />
			<action id="mix" name="文嘱" value="5" />
			<action id="common" name="常用" value="4" />
			<action id="sets" name="组套" value="1" />
		</module>
		<module id="CIC0701" name="组套名称" type="1"
			script="phis.application.cfg.script.AdvicePersonalComboNameList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.BQ_ZT01</p>
				<p name="closeAction">true</p>
				<p name="removeByFiled">ZTMC</p>
				<p name="queryWidth">80</p>
				<p name="addRef">phis.application.cic.CIC/CIC/CIC070101</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">updatePrescriptionStack</p>
				<p name="serviceActionDel">removePrescriptionDel</p>
				<p name="updateCls">phis.application.cfg.script.AdviceComboNameForm</p>
				<p name="cnds">1</p>
			</properties>
			<action id="add" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="CIC070101" name="组套-新增" type="1"
			script="phis.application.cfg.script.AdviceComboNamePerForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.BQ_ZT01</p>
			</properties>
			<action id="new" name="新增" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC0702" name="组套明细" type="1"
			script="phis.application.cfg.script.AdvicePersonalComboNameDetailList">
			<properties>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceActionSave">savePrescriptionDetails</p>
				<p name="serviceActionDel">removePrescriptionDetails</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>
		<!-- <module id="CIC07" name="处方组套维护" script="phis.application.cic.script.ClinicPersonalComboModule"> 
			<properties> <p name="refComboNameList">phis.application.cic.CIC/CIC/CIC0701</p> 
			<p name="refComboNameDetailList">phis.application.cic.CIC/CIC/CIC0702</p> 
			<p name="serviceId">clinicComboService</p> <p name="serviceAction">saveCommonlyUsedDrugs</p> 
			</properties> <action id="personal" name="个人组套" value="1"/> <action id="personalCommonDrug" 
			name="个人常用药" value="4"/> <action id="westernDrug" name="西药" value="1"/> <action 
			id="ChineseDrug" name="中药" value="2"/> <action id="herbs" name="草药" value="3"/> 
			</module> <module id="CIC0701" name="组套名称" type="1" script="phis.application.cic.script.ClinicPersonalComboNameList"> 
			<properties> <p name="entryName">phis.application.cic.schemas.YS_MZ_ZT01_CF</p> 
			<p name="closeAction">true</p> <p name="removeByFiled">ZTMC</p> <p name="queryWidth">80</p> 
			<p name="addRef">phis.application.cic.CIC/CIC/CIC070101</p> <p name="serviceId">clinicComboService</p> 
			<p name="serviceAction">updatePrescriptionStack</p> <p name="serviceActionDel">removePrescriptionDel</p> 
			<p name="updateCls">phis.application.cic.script.ClinicComboNameForm</p> </properties> 
			<action id="add" name="新增" iconCls="new"/> <action id="update" name="修改"/> 
			<action id="remove" name="删除"/> <action id="execute" name="启用" iconCls="commit"/> 
			</module> <module id="CIC070101" name="组套名称-新增(F1)" type="1" script="phis.application.cic.script.ClinicComboNameForm"> 
			<properties> <p name="entryName">phis.application.cic.schemas.YS_MZ_ZT01_CF</p> 
			</properties> <action id="new" name="新增" /> <action id="save" name="保存" /> 
			<action id="cancel" name="关闭" iconCls="common_cancel"/> </module> <module 
			id="CIC0702" name="处方明细" type="1" script="phis.application.cic.script.ClinicPersonalComboNameDetailList"> 
			<properties> <p name="entryName">phis.application.cic.schemas.YS_MZ_ZT02_CF</p> 
			<p name="serviceId">clinicComboService</p> <p name="serviceActionSave">savePrescriptionDetails</p> 
			<p name="serviceActionDel">removePrescriptionDetails</p> </properties> <action 
			id="insert" name="插入" iconCls="insertgroup"/> <action id="newGroup" name="新组" 
			iconCls="newgroup"/> <action id="remove" name="删除" /> <action id="save" name="保存" 
			/> </module> <module id="CIC08" name="项目组套维护" script="phis.application.cic.script.ClinicProjectComboModule"> 
			<properties> <p name="refProjectComboList">phis.application.cic.CIC/CIC/CIC0801</p> 
			<p name="refProjectComboDetailList">phis.application.cic.CIC/CIC/CIC0802</p> 
			<p name="serviceId">clinicComboService</p> <p name="serviceAction">savePersonalCommonly</p> 
			</properties> <action id="personal" name="个人组套" value="1"/> <action id="personalcom" 
			name="个人常用" value="4"/> </module> <module id="CIC0801" name="组套名称" type="1" 
			script="phis.application.cic.script.ClinicProjectComboList"> <properties> 
			<p name="entryName">phis.application.cic.schemas.YS_MZ_ZT01_XM</p> <p name="removeByFiled">ZTMC</p> 
			<p name="queryWidth">80</p> <p name="addRef">phis.application.cic.CIC/CIC/CIC080101</p> 
			<p name="updateCls">phis.application.cic.script.ClinicProjectSetForm</p> 
			<p name="serviceId">clinicComboService</p> <p name="serviceAction">updatePrescriptionStack</p> 
			<p name="serviceActionDel">removePrescriptionDel</p> </properties> <action 
			id="add" name="新增" iconCls="new"/> <action id="update" name="修改"/> <action 
			id="remove" name="删除"/> <action id="execute" name="启用" iconCls="commit"/> 
			</module> <module id="CIC080101" name="组套名称-新增(F1)" type="1" script="phis.application.cic.script.ClinicProjectSetForm"> 
			<properties> <p name="entryName">phis.application.cic.schemas.YS_MZ_ZT01_XM</p> 
			</properties> <action id="new" name="新增" /> <action id="save" name="保存" /> 
			<action id="cancel" name="关闭" iconCls="common_cancel"/> </module> <module 
			id="CIC0802" name="组套明细" type="1" script="phis.application.cic.script.ClinicProjectComboDetailList"> 
			<properties> <p name="entryName">phis.application.cic.schemas.YS_MZ_ZT02_XM</p> 
			<p name="serviceId">clinicComboService</p> <p name="serviceActionSave">savePrescriptionDetails</p> 
			<p name="serviceActionDel">removePrescriptionDetails</p> </properties> <action 
			id="insert" name="插入" iconCls="insertgroup"/> <action id="newGroup" name="新组" 
			iconCls="newgroup"/> <action id="remove" name="删除"/> <action id="save" name="保存"/> 
			</module> -->
		<module id="CIC09" name="常用诊断维护"
			script="phis.application.cic.script.ClinicProjectComboUserModule">
			<properties>
				<p name="refProjectComboUseList">phis.application.cic.CIC/CIC/CIC0901</p>
				<p name="refClinicAllList">phis.application.cic.CIC/CIC/CIC0902</p>
			</properties>
			<action id="WestMed" name="西医诊断" value="1" />
			<action id="ChinMed" name="中医诊断" value="2" />
			<action id="personal" name="个人常用诊断" value="1" />
			<!--<action id="public" name="科室常用诊断" value="3"/> -->
		</module>

		<module id="CIC0901" name="常用诊断list" type="1"
			script="phis.application.cic.script.ClinicInCommonUseList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_CYZD_CIC</p>
				<p name="removeByFiled">ZDMC</p>
				<p name="disablePagingTbr">true</p>
				<p name="gridDDGroup">refProjectComboUseList</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CIC0902" name="所有诊断list" type="1"
			script="phis.application.cic.script.ClinicProjectComboUserList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_JBBM_CY</p>
				<p name="gridDDGroup">refClinicAllList</p>
				<p name="autoLoadData">0</p>
			</properties>
		</module>

		<module id="CIC10" name="病历模板维护"
			script="phis.application.cic.script.ClinicMedicalTemplateModule">
			<properties>
				<p name="refMedicalTemplateList">phis.application.cic.CIC/CIC/CIC1001</p>
				<p name="refMedicalDetailList">phis.application.cic.CIC/CIC/CIC1002</p>
			</properties>
			<action id="personal" name="个人模板" value="1" />
			<!--<action id="public" name="科室模板" value="2"/> -->
		</module>

		<module id="CIC1001" name="病历模板" type="1"
			script="phis.application.cic.script.ClinicMedicalTemplateList">
			<properties>
				<p name="queryWidth">80</p>
				<p name="entryName">phis.application.cic.schemas.GY_BLMB_Z</p>
				<p name="removeByFiled">MBMC</p>
				<p name="addRef">phis.application.cic.CIC/CIC/CIC100101</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">medicalTemplateExecute</p>
			</properties>
			<action id="add" name="新增" iconCls="new" />
			<action id="remove" name="刪除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="CIC1002" name="右边模板" type="1"
			script="phis.application.cic.script.ClinicMedicalDetailForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_BLMB_Y</p>
				<p name="refRecipeImportModule">phis.application.cic.CIC/CIC/CIC0106</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CIC100101" name="新增病历模板" type="1"
			script="phis.application.cic.script.ClinicStencilForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_BLMB_Z</p>
			</properties>
			<action id="new" name="新增" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>

		<module id="CIC11" name="诊疗方案维护"
			script="phis.application.cic.script.ClinicTherapeuticRegimenModule">
			<properties>
				<p name="refTherapeuticRegimenList">phis.application.cic.CIC/CIC/CIC1101</p>
				<p name="refTherapeuticDetailList">phis.application.cic.CIC/CIC/CIC1102</p>
			</properties>
		</module>
		<module id="CIC1101" name="诊疗方案" type="1"
			script="phis.application.cic.script.ClinicTherapeuticRegimenList">
			<properties>
				<p name="queryWidth">80</p>
				<p name="entryName">phis.application.cic.schemas.GY_ZLFA01</p>
				<p name="removeByFiled">ZLMC</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">therapeuticRegimenExecute</p>
				<p name="createCls">phis.application.cic.script.ClinicTreatmentProgramsForm
				</p>
			</properties>
			<action id="create" name="新增" iconCls="new" />
			<action id="remove" name="刪除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="CIC1102" name="诊疗方案明细" type="1"
			script="phis.application.cic.script.ClinicTherapeuticRegimenForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_ZLFA02</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CIC12" name="医生科室权限维护" type="1"
			script="phis.application.cic.script.ClinicDoctorDepartmentModule">
			<properties>
				<p name="refDoctorDepartmentList">phis.application.cic.CIC/CIC/CIC1201</p>
				<p name="refDoctorDepartmentDetailList">phis.application.cic.CIC/CIC/CIC1202</p>
			</properties>
		</module>
		<module id="CIC1201" name="医生列表" type="1"
			script="phis.application.cic.script.ClinicDoctorDepartmentList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.SYS_Personnel1</p>
			</properties>
		</module>
		<module id="CIC1202" name="科室信息" type="1"
			script="phis.application.cic.script.ClinicDoctorDepartmentDetailsList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YS_KSQX_CIC</p>
			</properties>
			<action id="new" name="新增" />
			<action id="remove" name="刪除" />
		</module>
		<module id="CIC1203" name="科室选择List" type="1"
			script="phis.application.cic.script.ClinicDepartmentList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_KSDM_PHARMACY</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<!-- 不需要<module id="CIC20" name="门诊记账汇总" script="phis.prints.script.ChargeSummaryMZPrintView"> 
			</module> <module id="CIC21" name="职工医保门诊汇总" script="phis.prints.script.ClinicAccountingMZPrintView"> 
			</module> <module id="CIC22" name="门诊公费明细" script="phis.prints.script.ClinicPublicPrintView"> 
			</module> -->
		<!-- add by wuGang.Lo start -->
		<module id="CIC23" name="门诊转诊" type="1"
			script="phis.application.twr.script.TwoWayReferralClinicModule">
			<properties>
				<p name="formModule">phis.application.cic.CIC/CIC/CIC2301</p>
				<p name="listSDModule">phis.application.cic.CIC/CIC/CIC2302</p>
				<p name="listHMModule">phis.application.cic.CIC/CIC/CIC2303</p>
			</properties>
		</module>
		<module id="CIC2301" name="门诊转诊单" type="1"
			script="phis.application.twr.script.TwoWayReferralClinicForm">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRZL</p>
			</properties>
			<action id="submit" name="提交"  iconCls="confirm"/>
			<action id="close" name="关闭" />
		</module>
		<module id="CIC2302" name="时间段" type="1"
			script="phis.application.twr.script.TwoWayReferralClinicList">
		</module>
		<module id="CIC2303" name="时间段" type="1"
			script="phis.application.twr.script.TwoWayReferralNOClinicList">
		</module>
		<module id="CIC2304" name="挂号预约单" type="1"
			script="phis.prints.script.RegisterReqOrderPrintView">
		</module>
		<module id="CIC2305" name="门诊转诊单" type="1"
			script="phis.application.twr.script.TwoWayReferralClinicFormLook">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRZL_LOOK</p>
			</properties>
		</module>
		<module id="CIC40" name="门诊转诊申请列表" type="1" script="phis.application.twr.script.TwoWayReferralApplicationList">
			<properties>
				<p name="entryName">phis.application.twr.schemas.DR_CLINICRECORDLHISTORYLIST</p>
				<p name="winModule">phis.application.cic.CIC/CIC/CIC4001</p>
			</properties>
			<action id="new" name="新增" />
			<action id="look" name="查看"  iconCls="report"/>
			<!--<action id="cancel" name="取消"  iconCls="common_cancel"/>-->
			<action id="print" name="打印" />
		</module>
		<module id="CIC4001" name="转诊申请列表" type="1" script="phis.application.twr.script.TwoWayReferralApplicationModule">
			<properties>
				<p name="formModule">phis.application.cic.CIC/CIC/CIC2305</p>
				<p name="listModule">phis.application.cic.CIC/CIC/CIC2304</p>
			</properties>
			<action id="print" name="打印预约单" />
		</module>

		<module id="CIC28" name="住院转诊模块" type="1"
			script="phis.application.twr.script.DRHospitalApplicationModule">
			<properties>
				<p name="refForm">phis.application.cic.CIC/CIC/CIC2801</p>
				<p name="refList">phis.application.cic.CIC/CIC/CIC2802</p>
			</properties>
		</module>
		<module id="CIC2801" name="住院转诊单" type="1"
			script="phis.application.twr.script.DRNewHospitalizationApplicationForm">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRZY</p>
			</properties>
			<action id="submit" name="提交" iconCls="confirm"/>
			<action id="close" name="关闭" />
		</module>
		
		<module id="CIC2802" name="住院转诊模块" type="1"
			script="phis.application.twr.script.DRNewHospitalModule">
			<properties>
				<p name="refDateForm">phis.application.cic.CIC/CIC/CIC280201</p>
				<p name="refHisList">phis.application.cic.CIC/CIC/CIC280202</p>
			</properties>
		</module>
		<module id="CIC280201" name="检查日期" type="1"
			script="phis.application.twr.script.DRScheduleCalendar" />
		<module id="CIC280202" name="检查医院列表" type="1"
			script="phis.application.twr.script.DRHospitalList">
		</module>
		<!--<module id="CIC280204" name="住院转诊单查看" type="1"
			script="phis.application.twr.script.DRNewHospitalizationApplicationFormLook">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRZY_LOOK</p>
			</properties>
		</module>-->
		<module id="CIC280204" name="住院转诊单查看" type="1"
			script="phis.application.twr.script.DRTwoWayReferralApplicationModule">
			<properties>
				<p name="formModule">phis.application.cic.CIC/CIC/CIC28020401</p>
				<p name="listModule">phis.application.cic.CIC/CIC/CIC28020402</p>
			</properties>
			<action id="print" name="打印"/>
		</module>
	
		<module id="CIC28020401" name="住院转诊单" type="1"
			script="phis.application.twr.script.DRNewHospitalizationApplicationFormLook">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRZY_LOOK</p>
			</properties>
		</module>
		
		<module id="CIC28020402" name="住院转诊申请单" type="1"
			script="phis.prints.script.DRRegisterReqOrderPrintView">
		</module>
		
		<module id="CIC42" name="住院转诊申请列表" type="1" script="phis.application.twr.script.DRNewHospitalModuleList">
			<properties>
				<p name="entryName">phis.application.twr.schemas.DR_ClinicCheckHistoryHospitalList</p>
				<p name="winModule">phis.application.cic.CIC/CIC/CIC280204</p>
			</properties>
			<action id="new" name="新增" />
			<!--<action id="cancel" name="取消" />-->
			<action id="look" name="查看" iconCls="report"/>
			<action id="print" name="打印" />
		</module>
		<module id="CIC280203" name="住院转诊记录" type="1"
			script="phis.application.drc.script.DRNewHospitalizationApplicationList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_HOSPITALREGRECORD</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="CIC27" name="电子检查单模块" type="1"
			script="phis.application.twr.script.DRApplicationModule">
			<properties>
				<p name="refForm">phis.application.cic.CIC/CIC/CIC2701</p>
				<p name="refList">phis.application.cic.CIC/CIC/CIC2702</p>
			</properties>
		</module>
		<module id="CIC2701" name="电子检查单" type="1"
			script="phis.application.twr.script.DRExamineApplicationForm">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRJC</p>
			</properties>
			<action id="submit" name="提交" iconCls="confirm"/>
			<action id="close" name="关闭" />
		</module>
		<module id="CIC2702" name="检查资源模块" type="1"
			script="phis.application.twr.script.DRExamScheduleModule">
			<properties>
				<p name="refDateForm">phis.application.cic.CIC/CIC/CIC270201</p>
				<p name="refHisList">phis.application.cic.CIC/CIC/CIC270202</p>
			</properties>
		</module>
		<module id="CIC270201" name="检查日期" type="1"
			script="phis.application.twr.script.DRScheduleCalendar" />
		<module id="CIC270202" name="检查医院列表" type="1"
			script="phis.application.twr.script.DRExamScheduleList">
		</module>
		
		<module id="CIC2703" name="检查信息" type="1" script="phis.script.TabModule">
			<action id="CIC2702" viewType="module" name="检查日期"
				ref="phis.application.cic.CIC/CIC/CIC2702" />
			<action id="CIC270302" viewType="module" name="检查项目"
				ref="phis.application.cic.CIC/CIC/CIC270302" />
			<action id="CIC270303" viewType="module" name="检查部位"
				ref="phis.application.cic.CIC/CIC/CIC270303" />
		</module>
		<module id="CIC270302" name="检查申请记录" type="1"
			script="phis.application.twr.script.DRClinicCheckQueryList">
			<properties>
				<p name="entryName">phis.application.twr.schemas.DR_ClinicCheckHistory</p>
			</properties>
		</module>
		<module id="CIC270303" name="设备预约记录" type="1"
			script="phis.application.twr.script.DRClinicEquipmentQueryList">
			<properties>
				<p name="entryName">phis.application.twr.schemas.DR_ClinicEquipmentHistory</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">registerDevicerCancel</p>
			</properties>
			<action id="cancel" name="取消" iconCls="common_cancel" />
			<action id="print" name="打印" />
		</module>
		<!--<module id="CIC2704" name="电子检查单查看" type="1"
			script="phis.application.twr.script.DRExamineApplicationFormLook">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRJC_LOOK</p>
			</properties>
		</module>-->
		
		<module id="CIC2704" name="电子检查单查看" type="1"
			script="phis.application.twr.script.DRTwoWayCheckApplicationModule">
			<properties>
				<p name="formModule">phis.application.cic.CIC/CIC/CIC270401</p>
				<p name="listModule">phis.application.cic.CIC/CIC/CIC270402</p>
			</properties>
			<action id="print" name="打印"/>
		</module>
		<module id="CIC270401" name="电子检查单查看" type="1"
			script="phis.application.twr.script.DRExamineApplicationFormLook">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_BRJC_LOOK</p>
			</properties>
		</module>
		<module id="CIC270402" name="电子检查单查看" type="1"
			script="phis.prints.script.DRCheckRegisterReqOrderPrintView">
		</module>
		
		<module id="CIC41" name="检查转诊申请列表" type="1" script="phis.application.twr.script.TwoWayExamScheduleList">
			<properties>
				<p name="entryName">phis.application.twr.schemas.DR_ClinicCheckHistory</p>
				<p name="winModule">phis.application.cic.CIC/CIC/CIC2704</p>
			</properties>
			<action id="new" name="新增" />
			<action id="look" name="查看" iconCls="report"/>
			<action id="cancel" name="取消"  iconCls="common_cancel"/>
			<action id="print" name="打印" />
		</module>

		<!-- add by wuGang.Lo end -->
		<module id="CIC24" name="门诊检验录入" type="1"
			script="phis.application.cic.script.ClinicLisMedicalRecordModule">
		</module>
		<module id="CIC26" name="门诊检验报告" type="1"
			script="phis.application.cic.script.ClinicLisMedicalReportModule">
		</module>
		<module id="CIC25" name="门诊体检报告" type="1"
			script="phis.application.cic.script.ClinicPhysicalExaminationRecordList">
		</module>
		 <module id="CIC44" name="放射报告结果"  type="1"
			script="phis.application.cic.script.ClinicalcheckbgModule">
		</module>
		 <module id="CIC45" name="超声报告结果" type="1"
			script="phis.application.cic.script.ClinicalcheckbgModule">
		</module>
		<module id="CIC46" name="pacs影像查看" type="1"
			script="phis.application.cic.script.ClinicalcheckbgModule">
		</module>
		<module id="CIC471" name="心电图查看" type="1"
			script="phis.application.cic.script.ClinicElectrocardiogramModule">
		</module>
		<module id="CIC48" name="糖尿病并发症筛查报告" type="1"
			script="phis.application.cic.script.ClinicTnbbfzscModule">
		</module>
		<!--<module id="CIC270203" name="检查分类列表" type="1"
				script="phis.application.twr.script.DRItemCategoryList">
				<properties>
					<p name="entryName">phis.application.twr.schemas.DR_ItemCategory</p>
					<p name="disablePagingTbr">true</p>
					<p name="serviceId">referralService</p>
					<p name="serviceAction">getItemCategory</p>
				</properties>
			</module>-->
		<module id="CIC270203" name="住院回转登记" type="1" script="phis.application.twr.script.DRHospitalHZDJForm">
			<properties>
				<p name="entryName">phis.application.twr.schemas.TWR_HZDJ</p>
			</properties>
			<action id="submit" name="提交" />
			<action id="back" name="取消" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" />
		</module>
		<!--<module id="CIC270204" name="检查方向列表" type="1"
				script="phis.application.twr.script.DRCheckDirectionList">
				<properties>
					<p name="entryName">phis.application.twr.schemas.DR_CheckDirection</p>
					<p name="mutiSelect">true</p>
					<p name="disablePagingTbr">true</p>
					<p name="serviceId">referralService</p>
					<p name="serviceAction">getCheckDirection</p>
				</properties>
			</module>-->
		<module id="CIC270204" name="双向转诊单(存根)" type="1" script="phis.prints.script.TwoWayReferralStubPrintView">
			<properties>
			</properties>
		</module>
		<!--<module id="CIC270205" name="检查部位列表" type="1"
				script="phis.application.twr.script.DRCheckPartList">
				<properties>
					<p name="entryName">phis.application.twr.schemas.DR_CheckPart</p>
					<p name="mutiSelect">true</p>
					<p name="disablePagingTbr">true</p>
					<p name="serviceId">referralService</p>
					<p name="serviceAction">getCheckPart</p>
				</properties>
			</module>-->
		<module id="CIC270205" name="双向转诊单(上转单)" type="1" script="phis.prints.script.TwoWayReferralUploadPrintView">
			<properties>
			</properties>
		</module>
		<!--<module id="CIC270206" name="检查项目列表" type="1"
				script="phis.application.twr.script.DRCheckItemsList">
				<properties>
					<p name="entryName">phis.application.twr.schemas.DR_CheckItems</p>
					<p name="mutiSelect">true</p>
					<p name="disablePagingTbr">true</p>
					<p name="serviceId">referralService</p>
					<p name="serviceAction">getCheckItems</p>
				</properties>
			</module>-->
		<module id="CIC270206" name="双向转诊单(下转单)" type="1" script="phis.prints.script.TwoWayReferralNextTurnPrintView">
			<properties>
			</properties>
		</module>
		<module id="CIC270207" name="检查设备状态列表" type="1"
			script="phis.application.twr.script.DRMedicalStatusList">
			<properties>
				<p name="entryName">phis.application.twr.schemas.DR_MedicalStatus</p>
				<p name="mutiSelect">true</p>
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">getMedicalStatus</p>
			</properties>
		</module>
		
		<module id="CIC23020102" name="转诊医院列表" type="1"
			script="phis.application.drc.script.DRHospitalList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_Hospital</p>
				<!--<p name="mutiSelect">true</p> -->
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">getClinicHospital</p>
			</properties>
		</module>
		<module id="CIC23020103" name="转诊科室列表" type="1"
			script="phis.application.drc.script.DRDepartmentList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_Department</p>
				<!--<p name="mutiSelect">true</p> -->
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">getClinicDept</p>
			</properties>
		</module>
		<module id="CIC23020104" name="转诊医生列表" type="1"
			script="phis.application.drc.script.DRDoctorList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_Doctor</p>
				<!--<p name="mutiSelect">true</p> -->
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">getRegisterDoctor</p>
			</properties>
		</module>
		<module id="CIC23020105" name="医生排版列表" type="1"
			script="phis.application.drc.script.DRDoctorScheduleList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_DoctorSchedule</p>
				<!--<p name="mutiSelect">true</p> -->
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">registerSource</p>
			</properties>
		</module>
		<module id="CIC230202" name="门诊转诊记录" type="1"
			script="phis.application.drc.script.DRClinicRecordQueryList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_CLINICRECORDLHISTORY</p>
			</properties>
		</module>
		<module id="CIC230203" name="当天挂号记录" type="1"
			script="phis.application.drc.script.DRClinicRegisterQueryList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_CLINICREGISTERHISTORY</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="CIC230204" name="预约挂号记录" type="1"
			script="phis.application.drc.script.DRClinicRegisterReqQueryList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_CLINICREGISTERREQHISTORY
				</p>
				<p name="serviceId">referralService</p>
				<p name="serviceAction">registerCancel</p>
			</properties>
			<action id="cancel" name="取消" iconCls="common_cancel" />
			<action id="print" name="打印" />
		</module>

		<!-- 公共模块 -->
		<module id="CIC0501" name="处方录入Form" type="1"
			script="phis.script.TableForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CF01_YFQH</p>
				<p name="colCount">3</p>
				<p name="showButtonOnTop">0</p>
			</properties>
		</module>
		<module id="CIC0502" name="处方录入List" type="1"
			script="phis.application.cic.script.ClinicPrescriptionEntryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CF02_CF</p>
				<p name="refPrescriptionPrint">phis.application.cic.CIC/CIC/CIC13</p>
				<p name="refPrescriptionChinePrint">phis.application.cic.CIC/CIC/CIC14</p>
				<p name="refInjectionCardPrint">phis.application.cic.CIC/CIC/CIC16</p>
				<p name="rePrescriptionCopy">phis.application.cic.CIC/CIC/CIC050304</p>
				<p name="refAntibioticsReasonForm">phis.application.cic.CIC/CIC/CIC050201</p>
				<p name="refSkinTestForm">phis.application.cic.CIC/CIC/CIC0102</p>
				<p name="YSZHLYYModuleRef">phis.application.mds.MDS/MDS/MDS010101</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="newClinic" name="新处方" iconCls="newclinic" />
			<action id="removeGroup" name="删除组" iconCls="removeclinic"/>
			<action id="delClinic" name="删除处方" iconCls="removeclinic" />
			<action id="copyClinic" name="复制处方" iconCls="copy" />
			<action id="save" name="保存" />
			<action id="print" name="打印" />
			<action id="injectionCardPrint" name="注射卡" iconCls="printing" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC050201" name="门诊处方抗菌药物原因录入" type="1"
			script="phis.application.cic.script.ClinicPrescriptionAntibioticsReasonForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CF02_KJYW</p>
			</properties>
			<action id="confirm" name="确认" />
			<action id="close" name="取消" />
		</module>
		<module id="CIC0503" name="处方快速输入tab" type="1"
			script="phis.application.cic.script.ClinicPrescriptionEntryQuickInputTab">
			<action id="clinicCommon" viewType="list" name="常用药"
				ref="phis.application.cic.CIC/CIC/CIC050301" />
			<action id="clinicPersonalSet" viewType="list" name="处方组套"
				ref="phis.application.cic.CIC/CIC/CIC050302" />
			<action id="clinicAll" viewType="list" name="全部"
				ref="phis.application.cic.CIC/CIC/CIC050303" />
		</module>
		<module id="CIC050301" name="个人常用药" type="1"
			script="phis.application.cic.script.ClinicCommonMedicineList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_ZT02_CS</p>
			</properties>
		</module>
		<module id="CIC050302" name="选取个人组套" type="1"
			script="phis.application.cic.script.ClinicPersonalSetList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_ZT01_MS</p>
				<p name="createCls">phis.script.TableForm</p>
				<p name="updateCls">phis.script.TableForm</p>
			</properties>
		</module>
		<module id="CIC050303" name="全部药品" type="1"
			script="phis.application.cic.script.ClinicAllMedicineList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.YF_YPXX_MS</p>
			</properties>
		</module>
		<module id="CIC050304" name="门诊处方拷贝" type="1"
			script="phis.application.cic.script.ClinicPrescriptionCopyModule">
			<properties>
				<p name="refPrescriptionJL">phis.application.cic.CIC/CIC/CIC05030401</p>
				<p name="refPrescriptionCF01">phis.application.cic.CIC/CIC/CIC05030402</p>
				<p name="refPrescriptionCF02">phis.application.cic.CIC/CIC/CIC05030403</p>
				<p name="serviceId">clinicPerscriptionService</p>
				<p name="serviceAction">perscriptionCopyCheck</p>
			</properties>
			<action id="confirm" name="确认" />
			<action id="refresh" name="刷新" />
		</module>
		<module id="CIC05030401" name="就诊记录列表" type="1"
			script="phis.application.cic.script.PrescriptionCopyJZLSList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_JZLS_CP</p>
			</properties>
		</module>
		<module id="CIC05030402" name="门诊处方" type="1"
			script="phis.application.cic.script.PrescriptionCopyCF01List">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CF01</p>
			</properties>
		</module>
		<module id="CIC05030403" name="门诊处方明细" type="1"
			script="phis.application.cic.script.PrescriptionCopyCF02List">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CF02_CF_COPY</p>
			</properties>
		</module>
		<module id="CIC0601" name="处置录入List" type="1"
			script="phis.application.cic.script.ClinicDisposalEntryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_YJ02_CZ</p>
				<p name="removeByFiled">FYMC</p>
				<p name="serviceId">clinicDisposalEntryService</p>
				<p name="serviceActionSave">saveDisposalEntry</p>
				<p name="serviceActionRemove">removeDisposalEntry</p>
				<p name="refClinicDisposalPrint">phis.application.cic.CIC/CIC/CIC15</p>
				<p name="bgRef">phis.application.cic.CIC/CIC/CIC060102</p>
			</properties>
			<action id="insert" name="插入" iconCls="create" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="print" name="打印" />
			<!--<action id="bgck" name="报告结果查看" iconCls="read"/>-->
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC060102" name="报告结果查看" type="1"
			script="phis.application.pacs.script.BgjgckForm">
			<properties>
				<p name="entryName">phis.application.pacs.schemas.PACS_BGJG</p>
				<p name="colCount">1</p>
				<p name="serviceId">jckdService</p>
				<p name="queryDataActionId">queryBgjg</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />			
		</module>
		<module id="CIC0602" name="处置快速输入tab" type="1"
			script="phis.application.cic.script.ClinicDisposalEntryQuickInputTab">
			<action id="disposalCommon" viewType="list" name="常用项"
				ref="phis.application.cic.CIC/CIC/CIC060201" />
			<action id="disposalPersonalSet" viewType="list" name="组套"
				ref="phis.application.cic.CIC/CIC/CIC060202" />
			<action id="disposalAll" viewType="list" name="全部"
				ref="phis.application.cic.CIC/CIC/CIC060203" />
		</module>
		<module id="CIC060201" name="常用项" type="1"
			script="phis.application.cic.script.ClinicCommonItemList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_ZT02_CI</p>
			</properties>
		</module>
		<module id="CIC060202" name="组套" type="1"
			script="phis.application.cic.script.ClinicSetList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_ZT01_CS</p>
				<p name="createCls">phis.script.TableForm</p>
				<p name="updateCls">phis.script.TableForm</p>
			</properties>
		</module>
		<module id="CIC060203" name="全部" type="1"
			script="phis.application.cic.script.ClinicAllItemList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.GY_YLSF_AL</p>
			</properties>
		</module>
		<module id="CIC13" name="西药处方笺" type="1"
			script="phis.prints.script.PrescriptionPrintView">
		</module>
		<module id="CIC14" name="中草药处方笺" type="1"
			script="phis.prints.script.PrescriptionChinePrintView">
		</module>
		<module id="CIC15" name="处置笺" type="1"
			script="phis.prints.script.TreatmentPrintView">
		</module>
		<module id="CIC16" name="注射卡" type="1"
			script="phis.prints.script.InjectionCardPrintView">
		</module>
		<module id="IVC01010402" name="附加项目"
			script="phis.application.cic.script.ClinicPrescriptionEntryAdditionalList"
			type="1">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_YJ02_FJXM</p>
			</properties>
			<action id="insert" name="插入" iconCls="add" />
			<action id="remove" name="删除" />
		</module>
		<module id="CIC29" name="电子病历" type="1"
			script="phis.application.emr.script.EMREditorMZModule">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BRDA_CIC</p>
				<p name="refTplChooseModule">phis.application.cic.CIC/CIC/CIC30</p>
				<p name="refEmrDetailModule">phis.application.cic.CIC/CIC/CIC310102</p>
				<p name="refParaWin">phis.application.cic.CIC/CIC/CIC3004</p>
				<p name="refDiagnosisEntryModule">phis.application.cic.CIC/CIC/CIC34</p>
				<p name="refPrescriptionEntryModule">phis.application.cic.CIC/CIC/CIC35</p>
				<p name="refDisposalEntryModule">phis.application.cic.CIC/CIC/CIC36</p>
				<p name="refRecipeImportModule">phis.application.cic.CIC/CIC/CIC0106</p>
			</properties>
			<action id="new" name="新建" />
			<action id="beforeEditor" name="编辑" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="printBtn" name="打印" iconCls="print" />
			<action id="openPlate" name="模版" iconCls="page_paintbrush" />
			<action id="saveAsPersonal" name="另存为个人模版" iconCls="page_save" />
			<action id="updateRef" name="更新引用元素" iconCls="page_edit" />
			<action id="showEmrDetail" name="病历详情" iconCls="report" />
			<action id="signed" name="签名" iconCls="page_edit" />
			<action id="unSigned" name="取消签名" iconCls="page_delete" />
			<action id="showModify" name="修改痕迹" iconCls="page_gear" />
			<action id="revRecords" name="审计" iconCls="report_edit" />
		</module>
		<module id="CIC30" name="病历模版选择" type="1"
			script="phis.application.emr.script.EMRmodeChooseModule">
			<properties>
				<p name="refBllbTree">phis.application.cic.CIC/CIC/CIC3001</p>
				<p name="refBlmbList">phis.application.cic.CIC/CIC/CIC3002</p>
				<p name="refBlmbForm">phis.application.cic.CIC/CIC/CIC3003</p>
			</properties>
			<action id="save" name="确定" iconCls="commit" />
			<action id="loadEMR" name="预览" iconCls="page_white_magnify" />
			<action id="close" name="关闭" />
		</module>
		<module id="CIC3001" name="病历类别tree" type="1"
			script="phis.application.emr.script.EMRmodeBllbTree">
		</module>
		<module id="CIC3002" name="病历模版List" type="1"
			script="phis.application.emr.script.EMRmodeBlmbMZList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.V_EMR_BLMB_PRI</p>
			</properties>
		</module>
		<module id="CIC3003" name="病历模版Form" type="1"
			script="phis.application.emr.script.EMRmodeBlmbForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BCYS</p>
				<p name="autoLoadData">false</p>
				<p name="colCount">2</p>
			</properties>
		</module>
		<module id="CIC3004" name="修改标题" type="1"
			script="phis.application.emr.script.EMRmodeBlmbForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BCYS</p>
				<p name="autoLoadData">false</p>
				<p name="colCount">2</p>
			</properties>
			<action id="save" name="确定" iconCls="commit" />
			<action id="close" name="关闭" />
		</module>
		<module id="CIC310102" name="查看病历信息" type="1"
			script="phis.application.emr.script.EMRMedicalDetailsMZModule">
			<properties>
				<p name="recordInfo">phis.application.cic.CIC/CIC/CIC31010201</p>
				<p name="reviewList">phis.application.cic.CIC/CIC/CIC31010202</p>
			</properties>
			<action id="saveAsFile" name="导出病历" iconCls="page_save" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC31010201" name="病历信息" type="1"
			script="phis.application.emr.script.EMRMedicalDetailsForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.OMR_BL01_DETAIL</p>
			</properties>
		</module>
		<module id="CIC31010202" name="审阅记录List" type="1"
			script="phis.application.emr.script.EMRMedicalDetailsList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSY_WSYCX</p>
			</properties>
		</module>
		<module id="CIC3201" type="1" name="就诊历史记录(EMR)"
			script="phis.application.cic.script.ClinicEmrHistoryForEMRList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_JZLS_EMR</p>
				<p name="listServiceId">clinicManageService</p>
				<p name="serviceAction">queryOMRHistoryList</p>
			</properties>
		</module>
		<module id="CIC33" name="病历首页" type="1"
			script="phis.application.emr.script.EMRTabModule">
		</module>
		<module id="CIC34" name="诊断录入" type="1"
			script="phis.application.cic.script.ClinicDiagnosisEntryModule">
			<properties>
				<p name="clinicDiagnosisEntryList">phis.application.cic.CIC/CIC/CIC3401</p>
				<p name="clinicDiagnosisQuickInputTab">phis.application.cic.CIC/CIC/CIC0402</p>
			</properties>
		</module>
		<module id="CIC3401" name="初步诊断" type="1"
			script="phis.application.cic.script.ClinicDiagnosisEntryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BRZD_CIC</p>
			</properties>
			<action id="newClinic" name="新增诊断" iconCls="create" />
			<action id="subClinic" name="子诊断" iconCls="newclinic" />
			<action id="upClick" name="上移" iconCls="arrow-up" />
			<action id="downClick" name="下移" iconCls="arrow-down" />
			<action id="save" name="保存" />
			<action id="remove" name="删除" />
		</module>
		<module id="CIC35" name="处方录入" type="1"
			script="phis.application.cic.script.ClinicPrescriptionEntryModule">
			<properties>
				<p name="clinicPrescriptionEntryForm">phis.application.cic.CIC/CIC/CIC0501</p>
				<p name="clinicPrescriptionEntryList">phis.application.cic.CIC/CIC/CIC3502</p>
				<p name="clinicPrescriptionQuickInputTab">phis.application.cic.CIC/CIC/CIC0503</p>
				<p name="refFjList">phis.application.cic.CIC/CIC/IVC01010402</p>
			</properties>
		</module>
		<module id="CIC3502" name="处方录入List" type="1"
			script="phis.application.cic.script.ClinicPrescriptionEntryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_CF02_CF</p>
				<p name="refPrescriptionPrint">phis.application.cic.CIC/CIC/CIC13</p>
				<p name="refPrescriptionChinePrint">phis.application.cic.CIC/CIC/CIC14</p>
				<p name="refInjectionCardPrint">phis.application.cic.CIC/CIC/CIC16</p>
				<p name="rePrescriptionCopy">phis.application.cic.CIC/CIC/CIC050304</p>
				<p name="refAntibioticsReasonForm">phis.application.cic.CIC/CIC/CIC050201</p>
				<p name="refSkinTestForm">phis.application.cic.CIC/CIC/CIC0102</p>
				<p name="YSZHLYYModuleRef">phis.application.mds.MDS/MDS/MDS010101</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="newClinic" name="新处方" iconCls="newclinic" />
			<action id="delClinic" name="删除处方" iconCls="removeclinic" />
			<action id="copyClinic" name="复制处方" iconCls="copy" />
			<action id="save" name="保存" />
			<action id="print" name="打印" />
			<action id="injectionCardPrint" name="注射卡" iconCls="printing" />
		</module>
		<module id="CIC36" name="处置录入" type="1"
			script="phis.application.cic.script.ClinicDisposalEntryModule">
			<properties>
				<p name="refDisposalEntryList">phis.application.cic.CIC/CIC/CIC3601</p>
				<p name="refDisposalEntryTabList">phis.application.cic.CIC/CIC/CIC0602</p>
			</properties>
		</module>
		<module id="CIC3601" name="处置录入List" type="1"
			script="phis.application.cic.script.ClinicDisposalEntryList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_YJ02_CIC</p>
				<p name="removeByFiled">FYMC</p>
				<p name="serviceId">clinicDisposalEntryService</p>
				<p name="serviceActionSave">saveDisposalEntry</p>
				<p name="serviceActionRemove">removeDisposalEntry</p>
				<p name="refClinicDisposalPrint">phis.application.cic.CIC/CIC/CIC15</p>
			</properties>
			<action id="insert" name="插入" iconCls="create" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="print" name="打印" />
		</module>
		<module id="CIC37" name="数据盒" type="1"
			script="phis.application.cic.script.UserMZDataBoxModule">
			<properties>
				<p name="viewPanel">phis.application.cic.CIC/CIC/CIC3701</p>
				<p name="dataList">phis.application.cic.CIC/CIC/CIC3702</p>
				<p name="dataList2">phis.application.war.WAR/WAR/WAR9503</p>
				<p name="temporaryTab">phis.application.war.WAR/WAR/WAR9504</p>
				<p name="dicNormal">phis.application.war.WAR/WAR/WAR9505</p>
			</properties>
		</module>
		<module id="CIC3701" name="数据盒视图" type="1"
			script="phis.application.cic.script.UserMZDataBoxViewPanel">
			<properties>
			</properties>
		</module>
		<module id="CIC3702" name="病历" type="1"
			script="phis.application.cic.script.UserMZDataGroupList">
			<properties>
				<p name="listServiceId">clinicManageService</p>
				<p name="serviceAction">queryOMRHistoryList</p>
			</properties>
			<action id="open" name="打开" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC38" name="病历审阅日志" type="1"
			script="phis.application.chr.script.CaseMZHistoryReviewModule">
			<properties>
				<p name="refLModule">phis.application.cic.CIC/CIC/CIC3801</p>
				<p name="refRModule">phis.application.war.WAR/WAR/WAR9302</p>
			</properties>
		</module>
		<module id="CIC3801" name="病历信息" type="1"
			script="phis.application.chr.script.CaseInfoTabModule">
			<properties>
			</properties>
			<action id="caseInfo" viewType="form" name="病历信息"
				ref="phis.application.cic.CIC/CIC/CIC380101" iconCls="coins" />
			<action id="allCase" viewType="list" name="所有病历"
				ref="phis.application.cic.CIC/CIC/CIC380102" iconCls="coins" />
		</module>
		<module id="CIC380101" name="病历信息" type="1"
			script="phis.application.chr.script.CaseInfomationForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.OMR_BL01_SJRZ</p>
			</properties>
			<action id="openXML" name="查看XML数据" />
			<action id="openHTML" name="查看HTML文档" />
			<action id="openSTR" name="查看结构化元素" />
		</module>
		<module id="CIC380102" name="所有病历" type="1"
			script="phis.application.chr.script.AllCaseSelectList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.OMR_BL01_SJRZ</p>
			</properties>
		</module>
		<module id="CIC39" name="住院预约" type="1"
			script="phis.application.cic.script.ClinicHospitalAppointmentModule">
			<properties>
				<p name="refForm">phis.application.cic.CIC/CIC/CIC3901</p>
				<p name="refList">phis.application.cic.CIC/CIC/CIC3902</p>
				<p name="serviceId">clinicHospitalAppointmentService</p>
				<p name="serviceActionSave">saveHospitalAppointment</p>
				<p name="serviceActionUpdate">updateHospitalAppointment</p>
			</properties>
		</module>
		<module id="CIC3901" name="病人信息" type="1"
			script="phis.application.cic.script.ClinicHospitalAppointmentForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_ZYYY_INFO</p>
			</properties>		
		</module>
		<module id="CIC3902" name="科室列表" type="1"
			script="phis.application.cic.script.ClinicHospitalAppointmentList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_ZYYY_YYKS</p>
			</properties>
		</module>
		<module id="CIC43" name="住院证" type="1"
			script="phis.application.cic.script.InpatientCertificateModule">
			<properties>
				<p name="refForm">phis.application.cic.CIC/CIC/CIC4301</p>
				<p name="refList">phis.application.cic.CIC/CIC/CIC3902</p>
				<p name="serviceId">clinicHospitalAppointmentService</p>
				<p name="serviceActionSave">saveInpatientCertificate</p>
				<p name="serviceActionUpdate">updateInpatientCertificate</p>
			</properties>
		</module>
		<module id="CIC4301" name="病人信息（住院证）" type="1"
			script="phis.application.cic.script.InpatientCertificateForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_ZYZ_INFO</p>
			</properties>		
		</module>
		<!--EHR zhaojian 2017-10-25-->
		<module id="CIC50" name="EHR" type="1"
			script="phis.application.cic.script.ClinicHospitalEHR">
		</module>
		<!--家医签约、签约服务 Wangjl 2018-09-18-->
		<module id="CIC51" name="家医签约" type="1"
			script="phis.application.cic.script.ClinicHospitalJYQY">
		</module>
		<module id="CIC52" name="签约服务" type="1"
			script="phis.application.cic.script.ClinicHospitalqyfw">
		</module>
		<!--EHR zhaojian 2018-10-19-->
		<module id="CIC53" name="EHR(市)" type="1"
			script="phis.application.znts.script.HealthRecordBrowser">
		</module>
		<!--EHR zhaojian 2018-10-29-->
		<module id="CIC54" name="妇幼保健" type="1"
			script="phis.application.znts.script.FybjBrowser">
		</module>
		
		<module id="CIC109" name="辅助检查" type="1"
			script="phis.application.cic.script.FZJCModule">
			<properties>
				<p name="refList1">phis.application.cic.CIC/CIC/CIC10901</p>
				<p name="refList2">phis.application.cic.CIC/CIC/CIC10902</p>
			</properties>
			<action id="appoint" name="引用" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CIC10901" name="辅助检查list1" type="1"
			script="phis.application.cic.script.FzjcList1">
			<properties>
				<p name="mutiSelect">true</p>
				<p name="entryName">phis.application.war.schemas.L_PATIENTINFO</p>
			</properties>
		</module>
		<module id="CIC10902" name="辅助检查list2" type="1"
			script="phis.application.cic.script.FzjcList2">
			<properties>
				<p name="mutiSelect">true</p>
				<p name="entryName">phis.application.war.schemas.L_TESTRESULT</p>
			</properties>
		</module>

		<module id="CIC56" name="发热门诊填报" script="phis.application.cic.script.frmz.ClinicFeverPatientList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.frmz.MS_MZFR_L</p>
				<p name="listServiceId">phis.clinicFeverPatientService</p>
				<p name="loadInFeverlData">queryClinicFeverInfo</p>
				<p name="refModule">phis.application.cic.CIC/CIC/CIC5601</p>
			</properties>
			<action id="add" name="新建" iconCls="add" />
			<action id="updateInfo" name="修改" iconCls="update" />
			<action id="remove" name="删除" iconCls="remove"/>
		</module>
		<module id="CIC5601" name="发热病人信息填报" type="1" script="phis.application.cic.script.frmz.ClinicFeverPatientModule">
			<properties>
				<p name="refNorthForm">phis.application.cic.CIC/CIC/CIC560101</p>
				<p name="refSouthForm">phis.application.cic.CIC/CIC/CIC560103</p>
			</properties>
			<action id="import" name="调入" iconCls="add"/>
			<action id="save" name="保存" iconCls="save"/>
			<action id="cancel" name="关闭" iconCls="cancel"/>
		</module>
		<module id="CIC560101" name="基本信息" type="1" script="phis.application.cic.script.frmz.ClinicFeverPatientNouthForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.frmz.MS_MZFR_N</p>
			</properties>
		</module>
		<module id="CIC560103" name="医技检查" type="1" script="phis.application.cic.script.frmz.ClinicFeverPatientNouthForm">
			<properties>
				<p name="entryName">phis.application.cic.schemas.frmz.MS_MZFR_S</p>
			</properties>
		</module>
	</catagory>
</application>