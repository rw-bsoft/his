<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.war.WAR" name="病区管理">
	<catagory id="WAR" name="病区管理">

		<module id="WAR21" name="病案首页" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsBasicTabModule">
			<properties>
				<p name="refgxxxList">phis.application.war.WAR/WAR/WAR2108</p>
				<p name="basyprint">phis.application.war.WAR/WAR/WAR2109</p>
				<p name="refysqmList">phis.application.war.WAR/WAR/WAR2110</p>
			</properties>
			<action id="WAR2101" viewType="form" name="首页总览"
				ref="phis.application.war.WAR/WAR/WAR2101" />
			<action id="WAR2102" viewType="form" name="基本信息"
				ref="phis.application.war.WAR/WAR/WAR2102" />
			<action id="WAR2103" viewType="list" name="诊断"
				ref="phis.application.war.WAR/WAR/WAR2103" />
			<action id="WAR2104" viewType="list" name="手术信息"
				ref="phis.application.war.WAR/WAR/WAR2104" />
			<action id="WAR2105" viewType="form" name="费用统计"
				ref="phis.application.war.WAR/WAR/WAR2105" />
			<action id="WAR2106" viewType="form" name="医生签名"
				ref="phis.application.war.WAR/WAR/WAR2106" />
			<action id="WAR2107" viewType="form" name="附加项目"
				ref="phis.application.war.WAR/WAR/WAR2107" />
			<action id="save" name="保存" />
			<action id="update" name="更新" />
			<action id="clearSignature" name="清除签名" iconCls="writeoff" />
			<action id="print" name="打印" />
		</module>
		<module id="WAR2101" name="首页总览" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsOverviewModule">
			<properties>
				<p name="EMR_BASY">phis.application.emr.schemas.EMR_BASY</p>
				<p name="EMR_BASY_FY">phis.application.emr.schemas.EMR_BASY_FY</p>
				<p name="EMR_ZYZDJL">phis.application.emr.schemas.EMR_ZYZDJL</p>
				<p name="EMR_ZYSSJL">phis.application.emr.schemas.EMR_ZYSSJL</p>
			</properties>
		</module>
		<module id="WAR2102" name="基本信息" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsBasicForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BASY</p>
			</properties>
		</module>
		<module id="WAR2103" name="诊断" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsDiagnosisModule">
			<properties>
				<p name="refLeft">phis.application.war.WAR/WAR/WAR210301</p>
				<p name="refRight">phis.application.war.WAR/WAR/WAR210302</p>
			</properties>
		</module>
		<module id="WAR210301" name="诊断" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsDiagnosisEditorList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYZDJL_LR</p>
				<p name="refList">phis.application.war.WAR/WAR/WAR210301</p>
			</properties>
			<action id="addCYZD" name="出院诊断" iconCls="page_white_go" />
			<action id="moveUp" name="上移" iconCls="page_white_get" />
			<action id="moveDown" name="下移" iconCls="page_white_put" />
			<action id="addCommon" name="增加常用" iconCls="page_white_edit" />
			<action id="save" name="保存" />
			<action id="sx" name="刷新"  iconCls="refresh"/>
		</module>
		<module id="WAR210302" name="常用诊断" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsCommonList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_GRCY</p>
				<p name="removeByFiled">phis.application.war.WAR/WAR/MSZD</p>
			</properties>
			<action id="remove" name="删除" />
		</module>
		<module id="WAR2104" name="手术信息" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsSurgeryEditorList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYSSJL</p>
			</properties>
			<action id="remove" name="删除" />
		</module>
		<module id="WAR2105" name="费用统计" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsCostsForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BASY_FY</p>
			</properties>
		</module>
		<module id="WAR2106" name="医生签名" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsSignatureForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BASY</p>
			</properties>
		</module>
		<module id="WAR2107" name="附加项目" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsAdditionalForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BASY</p>
			</properties>
		</module>
		<module id="WAR2108" name="本次更新信息" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsBasicUpdateList">
			<properties>
				<p name="mutiSelect">true</p>
				<p name="entryName">phis.application.emr.schemas.EMR_BASY_GXXX</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="WAR2109" name="病案首页打印" type="1"
			script="phis.prints.script.EMRMedicalRecordsPrintView">
		</module>
		<module id="WAR2110" name="签名列表" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsReviewList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSY</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="WAR00" name="病人管理"
			script="phis.application.war.script.WardPatientManageModule">
			<properties>
				<p name="openBy">nurse</p>
				<p name="refDetailsTab">phis.application.war.WAR/WAR/WAR0001</p>
				<p name="refPatientList">phis.application.war.WAR/WAR/WAR0002</p>
				<p name="refPatientView">phis.application.war.WAR/WAR/WAR0003</p>
				<p name="refCyzglModule">phis.application.war.WAR/WAR/WAR0004</p>
				<p name="refBrxxModule">phis.application.war.WAR/WAR/WAR0301</p>
				<p name="refCwfpModule">phis.application.war.WAR/WAR/WAR0303</p>
				<p name="refYzclModule">phis.application.war.WAR/WAR/WAR0304</p>
				<p name="refTzcyModule">phis.application.war.WAR/WAR/WAR0305</p>
				<p name="refZcclModule">phis.application.war.WAR/WAR/WAR0306</p>
				<p name="refZkclModule">phis.application.war.WAR/WAR/WAR0307</p>
				<p name="refZyzzModule">phis.application.war.WAR/WAR/WAR0708</p>
				<p name="refWdjModule">phis.application.war.WAR/WAR/WAR45</p>
				<p name="refTysqModule">phis.application.war.WAR/WAR/WAR06</p>
				<p name="refZkglModule">phis.application.war.WAR/WAR/WAR07</p>
				<p name="refcyZkglModule">phis.application.war.WAR/WAR/WAR0705</p>
				<p name="refWardAdviceQueryModule">phis.application.war.WAR/WAR/WAR030404</p>
				<p name="refConsApplyModule">phis.application.war.WAR/WAR/WAR13</p>
				<p name="serviceZkId">wardTransferDeptService</p>
				<p name="refHljl">phis.application.war.WAR/WAR/WAR15</p>
				<p name="refHljh">phis.application.war.WAR/WAR/WAR66</p>
				<p name="refJykd">phis.application.war.WAR/WAR/WAR16</p>
				<p name="refJyzx">phis.application.war.WAR/WAR/WAR17</p>
				<p name="refJybg">phis.application.war.WAR/WAR/WAR19</p>
				<p name="refAmqcApplyList">phis.application.war.WAR/WAR/WAR39</p>
			</properties>
			<action id="cwfp" name="床位分配" iconCls="brick_edit" />
			<action id="brxx" name="病人信息" iconCls="user" />
			<action id="yzcl" name="医嘱处理" iconCls="advice" />
			<action id="tysq" name="退药申请" iconCls="pill_delete" />
			<action id="tysq" name="退药申请" iconCls="pill_delete" />
			<action id="zccl" name="转床" iconCls="arrow_switch" />
			<action id="tccl" name="退床" iconCls="arrow_undo" />
			<action id="zkcl" name="转科" iconCls="switchdepartment" />
			<action id="twd" name="体温单" iconCls="temperature" />
			<action id="hljl" name="护理记录" iconCls="page_white_edit" />
			<action id="hljh" name="护理计划" iconCls="page_white_edit" />
			<action id="zkgl" name="帐卡" iconCls="coins" />
			<action id="cyzgl" name="出院证" iconCls="page_edit" />
			<action id="tzcy" name="通知出院" iconCls="page_go" />
			<action id="zyzz" name="住院转诊申请" iconCls="transfer" />
			<action id="ctk" name="床头卡" iconCls="temperature" />
			<!-- <action id="amqcApply" name="抗菌药申请" iconCls="page_paintbrush"/> -->
		</module>
		<module id="WAR20" name="病人管理"
			script="phis.application.war.script.WardPatientManageModule">
			<properties>
				<p name="openBy">doctor</p>
				<p name="refDetailsTab">phis.application.war.WAR/WAR/WAR0001</p>
				<p name="refPatientList">phis.application.war.WAR/WAR/WAR0002</p>
				<p name="refPatientView">phis.application.war.WAR/WAR/WAR0003</p>
				<p name="refCyzglModule">phis.application.war.WAR/WAR/WAR0004</p>
				<p name="refBrxxModule">phis.application.war.WAR/WAR/WAR0301</p>
				<p name="refCwfpModule">phis.application.war.WAR/WAR/WAR0303</p>
				<p name="refYzclModule">phis.application.war.WAR/WAR/WAR0304</p>
				<p name="refTzcyModule">phis.application.war.WAR/WAR/WAR0305</p>
				<p name="refZcclModule">phis.application.war.WAR/WAR/WAR0306</p>
				<p name="refZkclModule">phis.application.war.WAR/WAR/WAR0307</p>
				<p name="refZyzzModule">phis.application.war.WAR/WAR/WAR0708</p>
				<p name="refWdjModule">phis.application.war.WAR/WAR/WAR45</p>
				<p name="refTysqModule">phis.application.war.WAR/WAR/WAR06</p>
				<p name="refZkglModule">phis.application.war.WAR/WAR/WAR07</p>
				<p name="refcyZkglModule">phis.application.war.WAR/WAR/WAR0705</p>
				<p name="refWardAdviceQueryModule">phis.application.war.WAR/WAR/WAR030404</p>
				<p name="refConsApplyModule">phis.application.war.WAR/WAR/WAR13</p>
				<p name="serviceZkId">wardTransferDeptService</p>
				<p name="refHljl">phis.application.war.WAR/WAR/WAR15</p>
				<p name="refHljh">phis.application.war.WAR/WAR/WAR66</p>
				<p name="refJykd">phis.application.war.WAR/WAR/WAR16</p>
				<p name="refJyzx">phis.application.war.WAR/WAR/WAR17</p>
				<p name="refJybg">phis.application.war.WAR/WAR/WAR19</p>
				<p name="refAmqcApplyList">phis.application.war.WAR/WAR/WAR39</p>
			</properties>
			<action id="cwfp" name="床位分配" iconCls="brick_edit" />
			<action id="brxx" name="病人信息" iconCls="user" />
			<action id="yzcl" name="病人主页" iconCls="home" />
			<action id="tysq" name="退药申请" iconCls="pill_delete" />
			<action id="zccl" name="转床" iconCls="arrow_switch" />
			<action id="tccl" name="退床" iconCls="arrow_undo" />
			<action id="zkcl" name="转科" iconCls="switchdepartment" />
			<action id="twd" name="体温单" iconCls="temperature" />
			<action id="hljl" name="护理记录" iconCls="page_white_edit" />
		<!--	<action id="hljh" name="护理计划" iconCls="page_white_edit" /> -->
			<action id="zkgl" name="帐卡" iconCls="coins" />
			<action id="cyzgl" name="出院证" iconCls="page_edit" />
			<action id="tzcy" name="通知出院" iconCls="page_go" />
			<action id="consApply" name="会诊申请" iconCls="page_paintbrush" />
			<action id="zyzz" name="住院转诊申请" iconCls="transfer" />
			<action id="amqcApply" name="抗菌药申请" iconCls="page_paintbrush"/>
			<action id="refresh" name="刷新" />
		</module>
		<module id="WAR0001" name="病人详细信息tab" type="1"
			script="phis.script.TabModule">
			<properties>
				<p name="tabPosition">bottom</p>
				<p name="resizeTabs">true</p>
				<p name="autoHeight">false</p>
				<p name="frame">true</p>
			</properties>
			<action id="patientTab" name="病人信息"
				ref="phis.application.war.WAR/WAR/WAR000101" />
			<action id="bedTab" name="床位信息"
				ref="phis.application.war.WAR/WAR/WAR000102" />
		</module>
		<module id="WAR000101" name="病人信息" type="1" script="phis.script.TableForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_DT</p>
				<p name="hideTrigger">true</p>
				<p name="showButtonOnTop">0</p>
				<p name="colCount">1</p>
				<p name="disAutoHeight">true</p>
			</properties>
		</module>
		<module id="WAR000102" name="床位信息" type="1" script="phis.script.TableForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_CWSZ_BQ</p>
				<p name="hideTrigger">true</p>
				<p name="showButtonOnTop">0</p>
				<p name="colCount">1</p>
			</properties>
		</module>
		<module id="WAR0002" name="病人管理(病人)" type="1"
			script="phis.application.war.script.WardPatientListView">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_BQ</p>
			</properties>
		</module>
		<module id="WAR0003" name="病人管理(床位)" type="1"
			script="phis.application.war.script.WardPatientDataView">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_CWSZ</p>
			</properties>
		</module>
		<module id="WAR0004" name="出院证管理" type="1"
			script="phis.application.war.script.WardLeaveHospitalForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_CY2</p>
				<p name="colCount">4</p>
				<p name="labelWidth">60</p>
				<p name="refHospProve">phis.application.war.WAR/WAR/WAR000401</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="取消" iconCls="update" />
			<action id="print" name="打印" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR000401" name="出院证打印" type="1"
			script="phis.prints.script.HospProvePrintView">
		</module>
		<module id="WAR03" name="病人管理" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_BQ</p>
			</properties>
		</module>
		<module id="WAR0301" name="病人信息" type="1"
			script="phis.application.war.script.WardPatientTab">
			<properties>
				<p name="openBy">nurse</p>
			</properties>
			<action id="patientBaseTab" name="基本信息"
				ref="phis.application.war.WAR/WAR/WAR030101" type="tab" />
			<action id="patientClinicTab" name="诊断"
				ref="phis.application.war.WAR/WAR/WAR030102" type="tab" />
			<action id="patientAllergyMedTab" name="过敏药物"
				ref="phis.application.war.WAR/WAR/WAR030103" type="tab" />
			<action id="save" name="确认" type="button" />
			<action id="close" name="关闭" type="button" iconCls="common_cancel" />
		</module>
		<module id="WAR030101" name="病人信息Form" type="1"
			script="phis.application.war.script.WardPatientForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_DT2</p>
			</properties>
		</module>
		<module id="WAR030102" name="病人诊断list" type="1"
			script="phis.application.war.script.WardPatientDiseaseList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_RYZD_BQ</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="WAR030103" name="病人过敏药物list" type="1"
			script="phis.application.war.script.WardPatientAllergyMedList">
			<properties>
				<p name="entryName">phis.application.war.schemas.GY_PSJL_BQ</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="WAR0302" name="医疗信息录入" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_BQ</p>
			</properties>
		</module>
		<module id="WAR0303" name="分配床位" type="1"
			script="phis.application.war.script.WardBedAllotList">
			<properties>
				<p name="serviceId">hospitalBedVerificationService</p>
				<p name="serviceAction">getBedVerification</p>
				<p name="serviceActionBedSave">saveBedVerification</p>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_CW</p>
			</properties>
			<action id="confirm" name="确认" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR0304" name="医嘱处理" type="1"
			script="phis.application.war.script.WardDoctorAdviceModule">
			<properties>
				<p name="openBy">nurse</p>
				<p name="refWardDoctorAdviceForm">phis.application.war.WAR/WAR/WAR030401</p>
				<p name="refWardDoctorAdviceTab">phis.application.war.WAR/WAR/WAR030402</p>
				<p name="refWardDoctorAdviceSet">phis.application.war.WAR/WAR/WAR030403</p>
				<p name="refWardQuickInputTab">phis.application.war.WAR/WAR/WAR030406</p>
				<p name="refWardAdviceExecuteModule">phis.application.war.WAR/WAR/WAR04</p>
				<p name="refWardAdviceSubmitModule">phis.application.war.WAR/WAR/WAR08</p>
				<p name="refWardAdviceQueryModule">phis.application.war.WAR/WAR/WAR030404</p>
				<p name="refWardProjectSubmitModule">phis.application.war.WAR/WAR/WAR1402</p>
				<p name="refWardDoctorAdviceJFList">phis.application.war.WAR/WAR/WAR09020101</p>
				<p name="refWardHerbEnrty">phis.application.war.WAR/WAR/WAR030408</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="remove" name="删除" />
			<action id="removeGroup" name="删除组" iconCls="removeclinic"/>
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="singleStop" name="单停" iconCls="pill" />
			<action id="allStop" name="全停" iconCls="pill_delete" />
			<action id="assignedEmpty" name="赋空" iconCls="page_white_delete" />
			<action id="singleReview" name="单复核" iconCls="vcard_edit" />
			<action id="review" name="全复核" iconCls="vcard_edit" />
			<action id="unReview" name="取消复核" iconCls="vcard_delete " />
			<action id="submit" name="提交" iconCls="pill_go" />
			<action id="yjSubmit" name="医技项目提交" iconCls="images_send" />
			<action id="confirm" name="执行" iconCls="images_send" />
			<action id="goback" name="退回" iconCls="arrow_undo" />
			<action id="gobackGroup" name="退回组" iconCls="arrow_undo"/>
			<action id="save" name="保存" />
			<action id="query" name="查询" />
			<action id="refresh" name="刷新" />
			<action id="cylr" name="草药方" iconCls="newgroup" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR030401" name="医嘱处理form" type="1"
			script="phis.application.war.script.WardDoctorAdviceForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_YZ</p>
				<p name="showButtonOnTop">0</p>
				<p name="loadServiceId">wardPatientLoad</p>
				<p name="colCount">5</p>
			</properties>
		</module>
		<module id="WAR030402" name="医嘱处理tab" type="1"
			script="phis.application.war.script.WardDoctorAdviceTab">
			<action id="longAdviceTab" name="长期医嘱"
				ref="phis.application.war.WAR/WAR/WAR03040201" type="tab" />
			<action id="tempAdviceTab" name="临时医嘱"
				ref="phis.application.war.WAR/WAR/WAR03040202" type="tab" />
			<action id="EmergencyMedicationTab" name="急诊用药"
				ref="phis.application.war.WAR/WAR/WAR03040204" type="tab" />
			<action id="DischargeMedicationTab" name="出院带药"
				ref="phis.application.war.WAR/WAR/WAR03040205" type="tab" />
		</module>
		<module id="WAR03040201" name="医嘱处理list(长期)" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_CQ</p>
				<p name="adviceType">longtime</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040203</p>
			</properties>
		</module>
		<module id="WAR03040202" name="医嘱处理list(临时)" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_LS</p>
				<p name="adviceType">temporary</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040203</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="WAR03040204" name="急诊用药list" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_JZ</p>
				<p name="adviceType">EmergencyMedication</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040203</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="WAR03040205" name="出院带药list" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_DY</p>
				<p name="adviceType">DischargeMedication</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040203</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="WAR03040203" name="附加医嘱" type="1"
			script="phis.application.war.script.WardDoctorAdviceAppendList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJ</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="remove" name="删除" />
		</module>
		<module id="WAR030403" name="医嘱组套调入" type="1"
			script="phis.application.war.script.WardAdvicePersonalComboModule">
			<properties>
				<p name="refComboNameList">phis.application.war.WAR/WAR/WAR03040301</p>
				<p name="refComboNameDetailList">phis.application.war.WAR/WAR/WAR03040302</p>
			</properties>
			<action id="westernDrug" name="西药组套" value="1" />
			<action id="ChineseDrug" name="成药组套" value="2" />
			<action id="herbs" name="草药组套" value="3" />
			<action id="others" name="项目组套" value="4" />
		</module>
		<module id="WAR03040301" name="组套名称" type="1"
			script="phis.application.war.script.WardAdvicePersonalComboList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT01_BQ</p>
				<p name="queryWidth">80</p>
			</properties>
		</module>
		<module id="WAR03040302" name="医嘱明细" type="1"
			script="phis.application.war.script.WardAdvicePersonalComboDetailList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT02_BQ</p>
			</properties>
		</module>
		<module id="WAR030404" name="医嘱查询" type="1"
			script="phis.application.war.script.WardDoctorAdviceQueryModule">
			<properties>
				<p name="refWardDoctorAdviceForm">phis.application.war.WAR/WAR/WAR03040401</p>
				<p name="refWardDoctorAdviceList">phis.application.war.WAR/WAR/WAR03040402</p>
			</properties>
			<action id="assignedEmpty" name="取消医嘱停嘱" iconCls="pill_go" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR03040401" name="医嘱处理查询form" type="1"
			script="phis.script.TableForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_YZ</p>
				<p name="loadServiceId">wardPatientLoad</p>
				<p name="colCount">5</p>
				<p name="showButtonOnTop">0</p>
			</properties>
		</module>
		<module id="WAR03040402" name="医嘱处理查询list" type="1"
			script="phis.application.war.script.WardDoctorAdviceQueryList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_CX</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040403</p>
				<p name="disableContextMenu">true</p>
			</properties>
		</module>
		<module id="WAR03040403" name="附加医嘱" type="1"
			script="phis.application.war.script.WardDoctorAdviceAppendList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJ</p>
			</properties>
		</module>
		<module id="WAR030405" name="医嘱快速输入tab" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">nurse</p>
			</properties>
			<action id="clinicCommon" viewType="list" name="常用药"
				ref="phis.application.war.WAR/WAR/WAR03040501" />
			<action id="clinicPersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040502" />
			<action id="clinicAll" viewType="list" name="全部"
				ref="phis.application.war.WAR/WAR/WAR03040503" />
		</module>
		<module id="WAR03040501" name="个人常用药" type="1"
			script="phis.application.war.script.WardCommonMedicineList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT02_ZY</p>
			</properties>
		</module>
		<module id="WAR03040502" name="选取个人组套" type="1"
			script="phis.application.war.script.WardPersonalSetList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT01_ZY</p>
			</properties>
		</module>
		<module id="WAR03040503" name="全部药品" type="1"
			script="phis.application.cic.script.ClinicAllMedicineList">
			<properties>
				<p name="height">455</p>
				<p name="openBy">ward</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.war.schemas.YF_YPXX_MS</p>
			</properties>
		</module>
		<module id="WAR03040504" name="常用项" type="1"
			script="phis.application.cic.script.ClinicCommonItemList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_ZT02_CI</p>
			</properties>
		</module>
		<module id="WAR03040505" name="组套" type="1"
			script="phis.application.war.script.WardAdviceClinicSetList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT01_ZYXM</p>
			</properties>
		</module>
		<module id="WAR03040506" name="全部" type="1"
			script="phis.application.cic.script.ClinicAllItemList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.cic.schemas.GY_YLSF_AL</p>
			</properties>
		</module>
		<module id="WAR03040507" name="个人常用" type="1"
			script="phis.application.war.script.WardCommonCharacterList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT02_WZ</p>
			</properties>
		</module>
		<module id="WAR03040508" name="选取个人组套" type="1"
			script="phis.application.war.script.WardPersonalSetList">
			<properties>
				<p name="height">455</p>
				<p name="autoLoadData">false</p>
				<p name="entryName">phis.application.war.schemas.YS_MZ_ZT01_ZY</p>
			</properties>
		</module>
		<module id="WAR030406" name="医嘱助手" type="1"
			script="phis.application.war.script.WardDoctorAdviceHelpTab">
			<properties>
				<p name="tabPosition">bottom</p>
				<p name="resizeTabs">true</p>
			</properties>
			<action id="medicine" viewType="list" name="药品助手"
				ref="phis.application.war.WAR/WAR/WAR03040601" />
			<action id="clinic" viewType="list" name="项目助手"
				ref="phis.application.war.WAR/WAR/WAR03040602" />
			<action id="character" viewType="list" name="文字助手"
				ref="phis.application.war.WAR/WAR/WAR03040603" />
		</module>
		<module id="WAR03040601" name="药品医嘱录入" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">nurse</p>
			</properties>
			<action id="medicineCommon" viewType="list" name="常用药"
				ref="phis.application.war.WAR/WAR/WAR03040501" />
			<action id="medicinePersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040502" />
			<action id="medicineAll" viewType="list" name="全部"
				ref="phis.application.war.WAR/WAR/WAR03040503" />
		</module>
		<module id="WAR03040602" name="项目医嘱录入" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">nurse</p>
			</properties>
			<action id="clinicCommon" viewType="list" name="常用项"
				ref="phis.application.war.WAR/WAR/WAR03040504" />
			<action id="clinicPersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040505" />
			<action id="clinicAll" viewType="list" name="全部"
				ref="phis.application.war.WAR/WAR/WAR03040506" />
		</module>
		<module id="WAR03040603" name="文字医嘱录入" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">nurse</p>
			</properties>
			<action id="characterCommon" viewType="list" name="常用"
				ref="phis.application.war.WAR/WAR/WAR03040507" />
			<action id="characterPersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040508" />
		</module>
		<module id="WAR030408" name="草药录入" type="1"
			script="phis.application.war.script.WardHerbEntryModule">
			<properties>
				<p name="refHerbEntryForm">phis.application.war.WAR/WAR/WAR03040801</p>
				<p name="refHerbEntryList">phis.application.war.WAR/WAR/WAR03040802</p>
				<p name="refHerbEntryPrint">phis.application.war.WAR/WAR/WAR03040803</p>
			</properties>
			<action id="create" name="插入" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="new" name="新处方" />
			<action id="print" name="打印" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR03040801" name="草药录入Form" type="1"
			script="phis.application.war.script.WardHerbEntryForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_CY_Form</p>
			</properties>
		</module>
		<module id="WAR03040802" name="草药录入List" type="1"
			script="phis.application.war.script.WardHerbEntryList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_CY_List</p>
			</properties>
		</module>
		<module id="WAR03040803" name="草药录入print" type="1"
			script="phis.prints.script.HerbEntryPrintView">
		</module>

		<module id="WAR0305" name="通知出院" type="1"
			script="phis.application.war.script.LeaveHospitalNoticeModule">
			<properties>
				<p name="refPatientForm">phis.application.war.WAR/WAR/WAR030501</p>
				<p name="refErrorTabs">phis.application.war.WAR/WAR/WAR030502</p>
				<p name="refCyzglModule">phis.application.war.WAR/WAR/WAR030503</p>
			</properties>
			<action id="cyzbl" name="出院证办理" iconCls="page_edit" />
			<action id="confirm" name="确认" iconCls="commit" />
			<action id="cancel" name="取消" iconCls="update" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR030501" name="通知出院Form" type="1"
			script="phis.script.TableForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_CY</p>
				<p name="showButtonOnTop">0</p>
				<p name="colCount">4</p>
				<p name="autoLoadData">false</p>
				<p name="hideTrigger">true</p>
				<p name="loadServiceId">leaveHospitalLoad</p>
			</properties>
		</module>
		<module id="WAR030502" name="禁止出院原因Tab" type="1"
			script="phis.script.TabModule">
			<properties>
				<p name="refMedsList">phis.application.war.WAR/WAR/WAR03050201</p>
				<p name="refProsList">phis.application.war.WAR/WAR/WAR03050202</p>
				<p name="refRtnList">phis.application.war.WAR/WAR/WAR03050203</p>
			</properties>
		</module>
		<module id="WAR03050201" name="药品医嘱List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_MER</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR03050202" name="项目医嘱List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="listServiceId">unSubmitAdviceQuery</p>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_PER</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR03050203" name="退药单List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.BQ_TYMX_ER</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR030503" name="出院证管理" type="1"
			script="phis.application.war.script.WardLeaveHospitalForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_CY2</p>
				<p name="colCount">4</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="取消" iconCls="update" />
			<action id="close" name="关闭" iconCls="common_cancel" />
			<action id="print" name="打印" />
		</module>
		<module id="WAR0306" name="床位转床" type="1"
			script="phis.application.hos.script.HospitalBedspaceToBedModule">
			<properties>
				<p name="serviceId">hospitalBedspaceToBedService</p>
				<p name="serviceActionSave">saveBedToBedVerification</p>
				<p name="refHospitalPatientBedsInformationForm">phis.application.war.WAR/WAR/WAR030601</p>
				<p name="refHospitalToBedInformationList">phis.application.war.WAR/WAR/WAR030602</p>
			</properties>
		</module>
		<module id="WAR0307" name="科室转科" type="1"
			script="phis.application.war.script.WarTransferDeptModule">
			<properties>
				<p name="refHospitalPatientDeptInformationForm">phis.application.war.WAR/WAR/WAR030701</p>
				<p name="refErrorTabs">phis.application.war.WAR/WAR/WAR030702</p>
				<p name="refCyzglModule">phis.application.war.WAR/WAR/WAR030503</p>
			</properties>
			<action id="sure" name="转科" iconCls="arrow_switch" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR030702" name="转科异常Tab" type="1"
			script="phis.script.TabModule">
			<properties>
				<p name="refMedsList">phis.application.war.WAR/WAR/WAR03070201</p>
				<p name="refProsList">phis.application.war.WAR/WAR/WAR03070202</p>
				<p name="refRtnList">phis.application.war.WAR/WAR/WAR03070203</p>
				<p name="refRxmList">phis.application.war.WAR/WAR/WAR03070204</p>
			</properties>
		</module>
		<module id="WAR03070201" name="药品医嘱未停未发药List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_MER</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR03070202" name="退药医嘱未提交或未确认List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.BQ_TYMX_ZK</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR03070203" name="退费单未确认List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.BQ_TFMX_ZK</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR03070204" name="项目医嘱未停或未执行List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_PER</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR030701" name="可转科室信息" type="1"
			script="phis.application.war.script.WarTransferDeptForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_ZK</p>
				<p name="serviceId">wardTransferDeptService</p>
				<p name="serviceActionQuery">queryBq</p>
			</properties>
		</module>
		<module id="WAR030601" name="病人床位信息" type="1"
			script="phis.application.hos.script.HospitalPatientBedsInformationForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_CW</p>
			</properties>
		</module>
		<module id="WAR030602" name="可转床位信息" type="1"
			script="phis.application.hos.script.HospitalToBedInformationList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_CWSZ_KZ</p>
			</properties>
		</module>
		
		<module id="WAR08" name="药品医嘱提交" 
			script="phis.application.war.script.DoctorAdviceSubmitModule">
			<properties>
				<p name="refLList">phis.application.war.WAR/WAR/WAR0801</p>
				<p name="refRList">phis.application.war.WAR/WAR/WAR0802</p>
				<p name="serviceId">doctorAdviceSubmitQueryService</p>
				<p name="serviceActionSave">saveDoctorAdviceSubmit</p>
				<p name="serviceQuery">doctorAdviceQueryVerification</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="print" name="打印" />
			<action id="confirm" name="确认" iconCls="save" />
			<action id="close" name="关闭" iconCls="common_cancel" hide="true"/>
		</module>
		<module id="WAR0801" name="药品医嘱提交左边list" type="1"
			script="phis.application.war.script.DoctorAdviceSubmitLeftList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_TJ</p>
				<p name="serviceId">phis.doctorAdviceSubmitQueryService</p>
				<p name="serviceAction">getDoctorAdviceBrQuery</p>
			</properties>
		</module>
		<module id="WAR0802" name="药品医嘱提交右边list" type="1"
			script="phis.application.war.script.DoctorAdviceSubmitRightList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_TJ</p>
				<p name="serviceId">phis.doctorAdviceSubmitQueryService</p>
				<p name="serviceAction">getDoctorAdviceSubmitQuery</p>
			</properties>
		</module>
		<!--
			<module id="WAR05" name="药品医嘱提交" type="1"
				script="phis.application.war.script.DoctorAdviceSubmitList">
				<properties>
					<p name="entryName">phis.application.war.schemas.ZY_BQYZ_TJ</p>
					<p name="serviceId">doctorAdviceSubmitQueryService</p>
					<p name="serviceActionSave">saveDoctorAdviceSubmit</p>
					<p name="serviceQuery">doctorAdviceQueryVerification</p>
					<p name="refDoctorAdviceSubmitPrint">phis.application.war.WAR/WAR/WAR0501</p>
					<p name="modal">true</p>
				</properties>
				<action id="refresh" name="刷新" />
				<action id="print" name="打印" />
				<action id="confirm" name="确认" iconCls="save" />
				<action id="close" name="关闭" iconCls="common_cancel" />
			</module>
			<module id="WAR0501" name="病区领药单" type="1"
				script="phis.prints.script.DoctorAdviceSubmitPrintView">
			</module>-->
		<module id="WAR14" name="病区项目提交"
			script="phis.application.med.script.WardProjectModule">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR1401</p>
			</properties>
			<action id="refresh" name="刷新" notReadOnly="true" />
			<action id="save" name="保存" notReadOnly="true" />
			<action id="determine" name="确认" notReadOnly="true" iconCls="commit" />
			<!--<action id="print" name="打印" notReadOnly="true"/> -->
			<!-- <action id="update" name="修改" /> <action id="remove" name="删除" /> -->
		</module>
		<module id="WAR1401" name="病区项目" type="1"
			script="phis.application.med.script.WardProjectTabModule">
			<!--<properties> <p name="winState" type="jo">{pos:[0,0]}</p> </properties> -->
			<action id="projectCommitTab" viewType="list" name="按项目提交"
				ref="phis.application.war.WAR/WAR/WAR140101" showType="project" />
			<action id="patientCommitTab" viewType="list" name="按病人提交"
				ref="phis.application.war.WAR/WAR/WAR140102" showType="patient" />
		</module>
		<module id="WAR140101" name="项目列表" type="1"
			script="phis.application.med.script.WardProjectListModule">
			<properties>
				<p name="PROJECT_LEFT">phis.application.war.WAR/WAR/WAR14010101</p>
				<p name="PROJECT_RIGHT">phis.application.war.WAR/WAR/WAR14010102</p>
				<p name="showType">project</p>
			</properties>
		</module>
		<module id="WAR140102" name="项目列表" type="1"
			script="phis.application.med.script.WardProjectListModule">
			<properties>
				<p name="showType">patient</p>
				<p name="PATIENT_LEFT">phis.application.war.WAR/WAR/WAR14010103</p>
				<p name="PATIENT_RIGHT">phis.application.war.WAR/WAR/WAR14010104</p>
			</properties>
		</module>
		<module id="WAR14010101" name="以项目显示左边列表" type="1"
			script="phis.application.med.script.WardProjectList_PRL">
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_YLSF_PRL</p>
				<p name="listServiceId">wardProjectService</p>
				<p name="serviceAction">queryProjectLeft</p>
			</properties>
		</module>
		<module id="WAR14010102" name="以项目显示右边列表" type="1"
			script="phis.application.med.script.WardProjectList_PRR">
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_BQYZ_PRR</p>
				<p name="listServiceId">wardProjectService</p>
				<p name="serviceAction">queryProjectRight</p>
			</properties>
		</module>
		<module id="WAR14010103" name="以病人显示左边列表" type="1"
			script="phis.application.med.script.WardPatientList_PAL">
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_BRRY_PAL</p>
				<p name="listServiceId">wardProjectService</p>
				<p name="serviceAction">queryPatientLeft</p>
			</properties>
		</module>
		<module id="WAR14010104" name="以病人显示右边列表" type="1"
			script="phis.application.med.script.WardPatientList_PAR">
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_BQYZ_PAR</p>
				<p name="listServiceId">wardProjectService</p>
				<p name="serviceAction">queryPatientRight</p>
			</properties>
		</module>
		<module id="WAR1402" name="医技项目提交" type="1"
			script="phis.application.med.script.WardPatientProjectList">
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_BQYZ_PAR</p>
				<p name="listServiceId">wardProjectService</p>
				<p name="serviceAction">queryPatientRight</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="save" name="保存" />
			<action id="confirm" name="确认" iconCls="save" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<!--病区管理的项目执行-->
		<module id="WAR18" name="病区项目执行" 
			script="phis.application.war.script.AdditionalProjectsModule_t">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR1801</p>
			</properties>
			<action id="refresh" name="刷新" notReadOnly="true" />
			<action id="determine" name="执行" notReadOnly="true" iconCls="commit" />
			<action id="print" name="打印" notReadOnly="true"/>
			<action id="close" name="关闭" notReadOnly="true" iconCls="common_cancel" />
		</module>
		<module id="WAR1801" name="病区项目执行" type="1"
			script="phis.application.war.script.AdditionalProjectsTabModule_t">
			<!--<properties> <p name="winState" type="jo">{pos:[0,0]}</p> </propertie	s> -->
			<action id="patientXMTab" viewType="list" name="按病人执行"
				ref="phis.application.war.WAR/WAR/WAR180101" showType="patient" />
			<action id="projectPatientTab" viewType="list" name="按项目执行"
				ref="phis.application.war.WAR/WAR/WAR180103" showType="patient" />
			<!--<action id="projectTab" viewType="list" name="费用医嘱附加计价单"
				ref="phis.application.war.WAR/WAR/WAR180102" showType="project" />-->
		</module>

		<module id="WAR180101" name="按病人执行" type="1"
			script="phis.application.war.script.DoctorAdviceExecuteModule_t">
			<properties>
				<p name="refDocAdvPatientList">phis.application.war.WAR/WAR/WAR18010101</p>
				<p name="refDocAdvDetailsList">phis.application.war.WAR/WAR/WAR18010102</p>
				<p name="serviceId">doctorAdviceExecuteService</p>
				<p name="serviceActionqr">saveConfirm</p>
				<p name="serviceActionsx">detailChargeQuery</p>
				<p name="modal">true</p>
			</properties>
		</module>
		<module id="WAR18010101" name="医嘱病人列表" type="1"
			script="phis.application.war.script.DoctorAdviceExecuteBrList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ</p>
			</properties>
		</module>
		<module id="WAR18010102" name="病人医嘱明细" type="1"
			script="phis.application.war.script.DoctorAdviceDetailAbrList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_MX</p>
				<p name="mutiSelect">true</p>
				<p name="autoLoadData">false</p><!--关闭默认加载 -->
			</properties>
		</module>

		<module id="WAR180103" name="按项目执行" type="1"
			script="phis.application.war.script.DoctorAdviceExecuteModule_XM">
			<properties>
				<p name="refLeft">phis.application.war.WAR/WAR/WAR18010301</p>
				<p name="refRight">phis.application.war.WAR/WAR/WAR18010302</p>
			</properties>
		</module>
		<module id="WAR18010301" name="病人项目" type="1"
			script="phis.application.war.script.DoctorAdviceDetailAxmList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_XM</p>
				<p name="mutiSelect">true</p>
				<p name="autoLoadData">false</p><!--关闭默认加载 -->
			</properties>
		</module>
		<module id="WAR18010302" name="医嘱病人列表" type="1"
			script="phis.application.war.script.DoctorAdviceExecuteBrAxmList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_MX</p>
			</properties>
		</module>
		<!--
			<module id="WAR180102" name="病人医嘱明细" type="1"
				script="phis.application.war.script.AdditionalProjectsFeeSubmitList">
				<properties>
					<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FYYZFJJJ</p>
					<p name="autoLoadData">false</p>
				</properties>
			</module>-->
	<module id="WAR30" name="医技项目退回" 
			script="phis.application.war.script.MedicalTechnologyProjectReturnModule" >
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR3001</p>
				<p name="refModule">phis.application.war.WAR/WAR/WAR3002</p>
				<p name="serviceId">doctorAdviceExecuteService</p>
				<p name="saveActionId">saveYjth</p>
			</properties>
			<action id="th" name="退回" iconCls="arrow_undo"/>
			<action id="refresh" name="刷新" />
		</module>
		<module id="WAR3001" name="医技项目退回左边list" type="1"
			script="phis.application.war.script.MedicalTechnologyProjectReturnLeftList" >
			<properties>
				<p name="entryName">phis.application.war.schemas.YJTH_ZXKS</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">phis.doctorAdviceExecuteService</p>
				<p name="serviceAction">queryZxks</p>
			</properties>
		</module>
		<module id="WAR3002" name="医技项目退回右边Module" type="1"
			script="phis.application.war.script.MedicalTechnologyProjectReturnRightModule" >
			<properties>
				<p name="refTopList">phis.application.war.WAR/WAR/WAR300201</p>
				<p name="refUnderList">phis.application.war.WAR/WAR/WAR300202</p>
			</properties>
		</module>
		<module id="WAR300201" name="医技项目退回右边Modules上面list" type="1"
			script="phis.application.war.script.MedicalTechnologyProjectReturnRightTopList" >
			<properties>
				<p name="entryName">phis.application.war.schemas.YJTH_YJ01</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">phis.doctorAdviceExecuteService</p>
				<p name="serviceAction">queryThyj</p>
			</properties>
		</module>
		<module id="WAR300202" name="医技项目退回右边Modules下面list" type="1"
			script="phis.application.war.script.MedicalTechnologyProjectReturnRightUnderList" >
			<properties>
				<p name="entryName">phis.application.war.schemas.YJTH_YJ02</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<!--医嘱录入的项目执行-->
		<module id="WAR04" name="病区项目执行" type="1"
			script="phis.application.war.script.AdditionalProjectsModule">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR0401</p>
			</properties>
			<action id="refresh" name="刷新" notReadOnly="true" />
			<action id="determine" name="执行" notReadOnly="true" iconCls="commit" />
			<action id="close" name="关闭" notReadOnly="true" iconCls="common_cancel" />
		</module>
		<module id="WAR0401" name="病区项目执行" type="1"
			script="phis.application.war.script.AdditionalProjectsTabModule">
			<!--<properties> <p name="winState" type="jo">{pos:[0,0]}</p> </properties> -->
			<action id="patientXMTab" viewType="list" name="病区项目执行"
				ref="phis.application.war.WAR/WAR/WAR040101" showType="patient" />
			<action id="projectTab" viewType="list" name="费用医嘱附加计价单"
				ref="phis.application.war.WAR/WAR/WAR040102" showType="project" />
		</module>

		<module id="WAR040101" name="病区项目执行" type="1"
			script="phis.application.war.script.DoctorAdviceExecuteModule">
			<properties>
				<p name="refDocAdvPatientList">phis.application.war.WAR/WAR/WAR04010101</p>
				<p name="refDocAdvDetailsList">phis.application.war.WAR/WAR/WAR04010102</p>
				<p name="serviceId">doctorAdviceExecuteService</p>
				<p name="serviceActionqr">saveConfirm</p>
				<p name="serviceActionsx">detailChargeQuery</p>
				<p name="modal">true</p>
			</properties>
		</module>
		<module id="WAR0403" name="消耗明细打印" type="1"
			script="phis.prints.script.SuppliesxhmxPrintView">
		</module>
		<module id="WAR04010101" name="医嘱病人列表" type="1"
			script="phis.application.war.script.DoctorAdviceExecuteList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ</p>
			</properties>
		</module>
		<module id="WAR04010102" name="病人医嘱明细" type="1"
			script="phis.application.war.script.DoctorAdviceDetailList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_MX</p>
				<p name="mutiSelect">true</p>
				<p name="autoLoadData">false</p><!--关闭默认加载 -->
			</properties>
		</module>

		<module id="WAR040102" name="病人医嘱明细" type="1"
			script="phis.application.war.script.AdditionalProjectsFeeSubmitList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FYYZFJJJ</p>
				<p name="autoLoadData">false</p><!--关闭默认加载 -->
			</properties>
		</module>


		<module id="WAR15" name="护理记录" type="1"
			script="phis.application.war.script.NurseRecordModule">
			<properties>
				<p name="NRTree">phis.application.war.WAR/WAR/WAR1501</p>
				<p name="NRDataView">phis.application.war.WAR/WAR/WAR1502</p>
				<p name="NRDataShow">phis.application.war.WAR/WAR/WAR1503</p>
				<p name="listServiceId">nurseRecordService</p>
				<p name="deleteENR_JL01">deleteENR_JL</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="WAR1501" name="左边树列表" type="1"
			script="phis.application.war.script.NurseRecordTree">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZKFL</p>
			</properties>
		</module>
		<module id="WAR1502" name="护理记录修改及新增" type="1"
			script="phis.application.war.script.NurseRecordDataView">
			<properties>
				<p name="entryName">phis.application.war.schemas.ENR_JBYS_JG02</p>
				<p name="listServiceId">nurseRecordService</p>
				<p name="serviceAction">queryRecordMete</p>
				<p name="saveAction">save</p>
				<p name="queryJL02ByJLBH">queryENR_JL02ByJLBH</p>
			</properties>
			<action id="commit" name="保存" iconCls="save" />
		</module>
		<module id="WAR1503" name="护理记录展示" type="1"
			script="phis.application.war.script.NurseRecordDataShowModule">
		</module>
		<module id="WAR1504" name="护理记录打印" type="1"
			script="phis.prints.script.NurseRecordDataShowPrintView">
		</module>
		
		<module id="WAR66" name="护理计划" type="1"
			script="phis.application.war.script.NursePlanModule">
			<properties>
				<p name="NPTree">phis.application.war.WAR/WAR/WAR6601</p>
				<p name="NPDataView">phis.application.war.WAR/WAR/WAR6602</p>
				<p name="NPDataShow">phis.application.war.WAR/WAR/WAR6603</p>
			</properties>
			<action id="createPlan" name="新增" iconCls="add"/>
			<action id="removePlan" name="删除" iconCls="remove"/>
			<action id="printPlan" name="打印" iconCls="print"/>
		</module>
		<module id="WAR6601" name="左边树列表" type="1"
			script="phis.application.war.script.NursePlanTree">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_HLJH</p>
			</properties>
		</module>
		<module id="WAR6602" name="护理计划修改及新增" type="1"
			script="phis.application.war.script.NursePlanDataView">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_HLJH</p>
				<p name="NPList">phis.application.war.WAR/WAR/WAR6605</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
			<action id="inportHLZD" name="引入护理诊断" iconCls="add" />
		</module>
		<module id="WAR6603" name="护理计划展示" type="1"
			script="phis.application.war.script.NursePlanDataShowModule">
		</module>
		<module id="WAR6604" name="护理计划打印" type="1"
			script="phis.prints.script.NursePlanDataShowPrintView">
		</module>
		<module id="WAR6605" name="护理诊断引入" type="1"
			script="phis.application.war.script.NursePlanDataInportList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_HLZD</p>
			</properties>
			<action id="inport" name="引入" iconCls="add" />
		</module>
		<!--新的附加项目执行-->
		<module id="WAR22" name="附加计价执行" 
			script="phis.application.war.script.AdditionalProjectsSubmitTabModule">
			<action id="abr"  name="按病人执行"
				ref="phis.application.war.WAR/WAR/WAR2201" />
			<action id="axm"  name="按项目执行"
				ref="phis.application.war.WAR/WAR/WAR2202" />
		</module>
		<module id="WAR2201" name="附加计价执行_按病人" type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitAbrModule">
			<properties>
				<p name="refLList">phis.application.war.WAR/WAR/WAR220101</p>
				<p name="refRList">phis.application.war.WAR/WAR/WAR220102</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="confirm" name="确认" iconCls="save" />
			<action id="print" name="打印" />
		</module>
		<module id="WAR220101" name="病人列表" type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitAbrLeftList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_TJ</p>
				<p name="serviceId">phis.doctorAdviceExecuteService</p>
				<p name="serviceAction">additionProjectsBrQuery</p>
			</properties>
		</module>
		<module id="WAR220102" name="附加计价明细" type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitAbrRightList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJXM</p>
				<p name="serviceId">phis.doctorAdviceExecuteService</p>
				<p name="serviceAction">additionProjectsQuery</p>
			</properties>
		</module>
		
		<module id="WAR2202" name="附加计价执行_按项目"  type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitAxmModule">
			<properties>
				<p name="refLList">phis.application.war.WAR/WAR/WAR220201</p>
				<p name="refRList">phis.application.war.WAR/WAR/WAR220202</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="confirm" name="确认" iconCls="save" />
			<action id="print" name="打印" />
		</module>
		<module id="WAR220201" name="项目列表" type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitAxmLeftList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJJJZX</p>
				<p name="serviceId">phis.doctorAdviceExecuteService</p>
				<p name="serviceAction">additionProjectsXmQuery</p>
			</properties>
		</module>
		<module id="WAR220202" name="附加计价明细" type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitAxmRightList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJXM</p>
				<p name="serviceId">phis.doctorAdviceExecuteService</p>
				<p name="serviceAction">additionProjectsQuery</p>
			</properties>
		</module>
		<!--老的附加计价执行,不知道哪里有用到 故保留-->
		<module id="WAR11" name="附加计价执行" type="1"
			script="phis.application.war.script.AdditionalProjectsSubmitList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJXM</p>
				<p name="refAdditionalPricing">phis.application.war.WAR/WAR/WAR1101</p>
				<p name="refDoubleModule">phis.application.war.WAR/WAR/WAR1102</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="confirm" name="确认" iconCls="save" />
			<!--<action id="print" name="打印" /> -->
		</module>
		<module id="WAR1101" name="附加计价执行打印" type="1"
			script="phis.prints.script..AdditionalPricingPrintView">
		</module>
		
		<module id="WAR01" name="发药药房设置"
			script="phis.application.war.script.WardOutPharmacySet">
			<properties>
				<p name="entryName">phis.application.war.schemas.BQ_FYYF</p>
				<p name="initCnd">['and',['and',['eq',['$','a.JGID'],['$','%user.manageUnit.id']],['eq',['$','a.GNFL'],['i',4]]],['eq',['$','a.BQDM'],['$','%user.properties.wardId']]]
				</p>
				<!--<p name="initCnd">['and',['and',['and',['eq',['$','a.JGID'],['$','%user.manageUnit.id']],['eq',['$','a.BQDM'],['$','%user.prop.wardId']]],['eq',['$','a.GNFL'],['i',4]]],['eq',['$','a.TYPE'],['i',1]]]</p> -->
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="updateStage" name="注销" iconCls="writeoff" />
			<action id="commit" name="保存" iconCls="save" />
			<!-- <action id="RemoveCell" name="删除行" iconCls="remove" /> -->
		</module>
		
		<!--病案首页上报模块  addby Wangjl-->
		<module id="WAR83"  name="病案首页查询" 
				script="phis.application.war.script.MedicalRecordHomePageQueryList" > 
			<properties> 
				<p name="entryName">phis.application.emr.schemas.EMR_BASY_SB</p> 
				<p name="listServiceId">hospitalCostProcessingService</p>
				<p name="serviceAction">queryMedicalRecordHomePage</p>
			</properties>  
			<action id="query" name="查询" />
			<action id="print" name="导出"/> 
		</module>
		
		<module id="WAR32" name="病历记录查询"
			script="phis.application.war.script.MedicalRecordsQueryModule">
			<properties>
				<p name="refQueryForm">phis.application.war.WAR/WAR/WAR3201</p>
				<p name="refRecordsList">phis.application.war.WAR/WAR/WAR3202</p>
			</properties>
		</module>
		<module id="WAR3201" name="病历记录查询表单" type="1"
			script="phis.application.war.script.MedicalRecordsQueryForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERY</p>
				<p name="refEMRMode">phis.application.war.WAR/WAR/WAR320101</p>
			</properties>
			<action id="select" name="查询" iconCls="query" />
			<action id="reset" name="重置" iconCls="page_refresh" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR320101" name="病历模版选择" type="1"
			script="phis.application.war.script.EMRmodeChooseModule">
			<properties>
				<p name="reflbTree">phis.application.war.WAR/WAR/WAR32010101</p>
				<p name="refmbList">phis.application.war.WAR/WAR/WAR32010102</p>
			</properties>
			<action id="save" name="确定" iconCls="commit" />
			<action id="loadEMR" name="预览" iconCls="page_white_magnify" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR32010101" name="病历类别tree" type="1"
			script="phis.application.war.script.EMRmodeBllbTree">
		</module>
		<module id="WAR32010102" name="病历模版List" type="1"
			script="phis.application.war.script.EMRmodeBlmbList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.V_EMR_BLMB_PRI</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">1</p>
			</properties>
		</module>

		<module id="WAR3202" name="病历记录" type="1"
			script="phis.application.war.script.MedicalRecordsList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERYLIST</p>
				<p name="refMedicalDetails">phis.application.war.WAR/WAR/WAR340102</p>
				<p name="basyView">phis.application.war.WAR/WAR/WAR34020101</p>
			</properties>
			<action id="print" name="打印" />
			<action id="medicalDetails" name="病历详情" iconCls="report" />
			<action id="seeRecord" name="浏览病历" iconCls="report_magnify" />
		</module>
		<module id="WAR34" name="未审阅病历查询"
			script="phis.application.war.script.MedicalRecordsQueryNotReviewModule">
			<properties>
				<p name="NotReviewQueryForm">phis.application.war.WAR/WAR/WAR3401</p>
				<p name="NotReviewRecordsList">phis.application.war.WAR/WAR/WAR3402</p>
			</properties>
		</module>
		<module id="WAR3401" name="病历记录查询表单" type="1"
			script="phis.application.war.script.MedicalRecordsQueryNotReviewForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERY</p>
				<p name="NotReviewEMRMode">phis.application.war.WAR/WAR/WAR340101</p>
				<p name="conditionForm">phis.application.war.WAR/WAR/WAR340103</p>
				<p name="conditionList">phis.application.war.WAR/WAR/WAR340104</p>
			</properties>
			<action id="select" name="查询" iconCls="query" />
			<action id="reset" name="重置" iconCls="page_refresh" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
			<action id="addCondition" name="调入条件" iconCls="page_go" />
			<action id="saveCondition" name="保存条件" iconCls="page_save" />
		</module>

		<module id="WAR340101" name="病历模版选择" type="1"
			script="phis.application.war.script.EMRmodeChooseNotReviewModule">
			<properties>
				<p name="NotReviewlbTree">phis.application.war.WAR/WAR/WAR34010101</p>
				<p name="NotReviewmbList">phis.application.war.WAR/WAR/WAR34010102</p>
			</properties>
			<action id="save" name="确定" iconCls="commit" />
			<action id="loadEMR" name="预览" iconCls="page_white_magnify" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR34010101" name="病历类别tree" type="1"
			script="phis.application.war.script.EMRmodeBllbNotReviewTree">
		</module>
		<module id="WAR34010102" name="病历模版List" type="1"
			script="phis.application.war.script.EMRmodeBlmbNotReviewList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.V_EMR_BLMB_PRI</p>
			</properties>
		</module>

		<module id="WAR340102" name="查看病历信息" type="1"
			script="phis.application.emr.script.EMRMedicalDetailsModule">
			<properties>
				<p name="recordInfo">phis.application.war.WAR/WAR/WAR34010201</p>
				<p name="reviewList">phis.application.war.WAR/WAR/WAR34010202</p>
			</properties>
			<action id="saveAsFile" name="导出病历" iconCls="page_save" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR34010201" name="病历信息" type="1"
			script="phis.application.emr.script.EMRMedicalDetailsForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_DETAIL</p>
			</properties>
		</module>
		<module id="WAR34010202" name="审阅记录List" type="1"
			script="phis.application.emr.script.EMRMedicalDetailsList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSY_WSYCX</p>
			</properties>
		</module>

		<module id="WAR340103" name="保存病历查询条件" type="1"
			script="phis.application.war.script.MedicalConditionForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERYC</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR340104" name="病历查询条件" type="1"
			script="phis.application.war.script.MedicalConditionList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERYC</p>
			</properties>
		</module>
		<module id="WAR3402" name="病历记录" type="1"
			script="phis.application.war.script.MedicalRecordsNotReviewList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_QUERYLIST</p>
				<p name="refMedicalDetails">phis.application.war.WAR/WAR/WAR340102</p>
				<p name="basyView">phis.application.war.WAR/WAR/WAR34020101</p>
			</properties>
			<action id="print" name="打印" />
			<action id="medicalDetails" name="病历详情" iconCls="report" />
			<action id="seeRecord" name="浏览病历" iconCls="report_magnify" />
		</module>
		<module id="WAR34020101" name="首页总览" type="1"
			script="phis.application.emr.script.EMRMedicalRecordsOverView">
			<properties>
				<p name="EMR_BASY">phis.application.emr.schemas.EMR_BASY</p>
				<p name="EMR_BASY_FY">phis.application.emr.schemas.EMR_BASY_FY</p>
				<p name="EMR_ZYZDJL">phis.application.emr.schemas.EMR_ZYZDJL</p>
				<p name="EMR_ZYSSJL">phis.application.emr.schemas.EMR_ZYSSJL</p>
			</properties>
		</module>
		<module id="WAR38" name="抗菌药物审批"
			script="phis.application.hos.script.HospitalAntibacterialAduitModule">
			<properties>
				<p name="refQueryForm">phis.application.war.WAR/WAR/WAR3801</p>
				<p name="refAntibacterialAduisList">phis.application.war.WAR/WAR/WAR3802</p>
			</properties>
			<action id="query" name="查询" iconCls="query" />
			<action id="reset" name="重置" iconCls="page_refresh" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR3801" name="抗菌药物查询表单" type="1"
			script="phis.application.hos.script.HospitalAntibacterialAduitQueryForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01_QUERY</p>
			</properties>
		</module>
		<module id="WAR3802" name="抗菌药物List" type="1"
			script="phis.application.hos.script.HospitalAntibacterialAduitList">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR380201</p>
			</properties>
			<action id="look" name="详细信息" iconCls="read" />
			<action id="print" name="打印" />
			<action id="aduit" name="审批" iconCls="archiveMove_commit" />
		</module>
		<module id="WAR380201" name="抗菌药物使用申请审批" type="1"
			script="phis.application.hos.script.HospitalAntibacterialAduitForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01_SQFORM</p>
			</properties>
			<action id="save" name="审批" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR39" name="抗菌药物申请管理" type="1"
			script="phis.application.hos.script.HospitalAntibacterialApplyList">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01_SQ</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3903</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="modify" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="commit" name="提交" iconCls="commit" />
			<action id="writeoff" name="作废" iconCls="writeoff" />
			<action id="calloff" name="取消作废" iconCls="update" />
			<action id="print" name="打印" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR3901" name="抗菌药物使用申请登记" type="1"
			script="phis.application.hos.script.HospitalAntibacterialApplyForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01_SQFORM</p>
			</properties>
			<action id="save" name="保存" />
			<action id="commit" name="提交" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR3902" name="抗菌药物使用申请登记" type="1"
			script="phis.application.hos.script.HospitalAntibacterialApplyForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01_SQFORM</p>
				<p name="render">applyYz</p>
			</properties>
			<action id="save" name="保存" />
			<action id="commit" name="提交" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR3903" name="抗菌药物使用申请登记" type="1"
			script="phis.application.hos.script.HospitalAntibacterialApplyForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.AMQC_SYSQ01_SQFORM</p>
				<p name="render">yssq</p>
			</properties>
			<action id="save" name="保存" />
			<action id="commit" name="提交" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="HOS07" name="费用记账" script="phis.script.TabModule">
			<action id="HospitalCostProcessingModuleTab" viewType="list"
				name="明细记账" ref="phis.application.war.WAR/WAR/HOS0701" />
			<action id="HosPaymentTypeTab" viewType="list" name="记账查询"
				ref="phis.application.war.WAR/WAR/HOS0702" />
		</module>
		<module id="HOS0701" name="明细记账" type="1"
			script="phis.application.hos.script.HospitalCostProcessingModule">
			<properties>
				<p name="refForm">phis.application.war.WAR/WAR/HOS070101</p>
				<p name="refList">phis.application.war.WAR/WAR/HOS070102</p>
				<p name="refRefundList">phis.application.war.WAR/WAR/HOS070103</p>
			</properties>
		</module>
		<module id="HOS0702" name="记账查询" type="1"
			script="phis.application.hos.script.HospitalAccountingQueryModule">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/HOS070201</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new" />
		</module>
		<module id="HOS070101" name="费用记账联" type="1"
			script="phis.application.hos.script.HospitalCostAccountingForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYCF</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new" />
			<action id="save" name="保存" />
		</module>
		<module id="HOS070102" name="明细记账" type="1"
			script="phis.application.hos.script.HospitalDetailsAccountingList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYCL</p>
			</properties>
		</module>
		<module id="HOS070103" name="退费明细选择" type="1"
			script="phis.application.hos.script.HospitalRefundsDetailsList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_TF</p>
				<p name="listServiceId">hospitalCostProcessingService</p>
				<p name="serviceAction">queryRefundInfo1</p>
			</properties>
			<action id="commit" name="确定" iconCls="commit" />
			<action id="cancel" name="返回" iconCls="common_cancel" />
		</module>
		<module id="HOS070201" name="记账查询list" type="1"
			script="phis.application.hos.script.HospitalCostList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYCX</p>
				<p name="listServiceId">hospitalCostProcessingService</p>
				<p name="serviceAction">queryCostList</p>
			</properties>
		</module>
		<module id="WAR12" name="常用组套维护"
			script="phis.application.cfg.script.AdvicePersonalComboModule">
			<properties>
				<p name="refComboNameList">phis.application.war.WAR/WAR/WAR1201</p>
				<p name="refComboNameDetailList">phis.application.war.WAR/WAR/WAR1202</p>
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
		<module id="WAR1201" name="组套名称" type="1"
			script="phis.application.cfg.script.AdvicePersonalComboNameList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.BQ_ZT01</p>
				<p name="closeAction">true</p>
				<p name="removeByFiled">ZTMC</p>
				<p name="queryWidth">80</p>
				<p name="addRef">phis.application.war.WAR/WAR/WAR120101</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">updatePrescriptionStack</p>
				<p name="serviceActionDel">removePrescriptionDel</p>
				<p name="updateCls">phis.application.cfg.script.AdviceComboNamePerForm</p>
				<p name="cnds">1</p>
			</properties>
			<action id="add" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="WAR120101" name="组套-新增" type="1"
			script="phis.application.cfg.script.AdviceComboNamePerForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.BQ_ZT01</p>
			</properties>
			<action id="new" name="新增" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR1202" name="组套明细" type="1"
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

		<module id="WAR06" name="退药申请" type="1"
			script="phis.application.war.script.MedicalBackApplicationModule">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_CWSZ_BQ</p>
				<p name="refForm">phis.application.war.WAR/WAR/WAR030401</p>
				<p name="refLeftList">phis.application.war.WAR/WAR/WAR0602</p>
				<p name="refRightModule">phis.application.war.WAR/WAR/WAR0603</p>
				<p name="serviceId">doctorAdviceSubmitQueryService</p>
				<p name="saveActionId">saveBackApplication</p>
				<p name="commitActionId">saveCommitBackApplication</p>
			</properties>
			<action id="save" name="保存" />
			<action id="confirm" name="提交" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<!-- <module id="WAR0601" name="退药申请form" type="1" script="phis.application.war.script.MedicalBackApplicationForm"> 
			<properties> <p name="serviceId">doctorAdviceSubmitQueryService</p> <p name="queryActionId">queryBackApplicationPatientInformation</p> 
			<p name="entryName">phis.application.war.schemas.ZY_BRRY_TY</p> <p name="colCount">5</p> 
			<p name="labelWidth">50</p> </properties> </module> -->
		<module id="WAR0602" name="已发药品list" type="1"
			script="phis.application.war.script.MedicalBackApplicationLeftList">
			<properties>
				<p name="serviceId">doctorAdviceSubmitQueryService</p>
				<p name="queryActionId">queryDispensingRecords</p>
				<p name="entryName">phis.application.war.schemas.YF_ZYFYMX_BQ</p>
			</properties>
		</module>
		<module id="WAR0603" name="退药申请右边module" type="1"
			script="phis.application.war.script.MedicalBackApplicationRightModule">
			<properties>
				<p name="refTopList">phis.application.war.WAR/WAR/WAR060301</p>
				<p name="refUnderList">phis.application.war.WAR/WAR/WAR060302</p>
			</properties>
		</module>
		<module id="WAR060301" name="退药申请右上list" type="1"
			script="phis.application.war.script.MedicalBackApplicationRightTopList">
			<properties>
				<p name="serviceId">doctorAdviceSubmitQueryService</p>
				<p name="queryActionId">queryTurningBackNumber</p>
				<p name="entryName">phis.application.war.schemas.BQ_TYMX_TYSQ</p>
			</properties>
		</module>
		<module id="WAR060302" name="退药申请右下list" type="1"
			script="phis.application.war.script.MedicalBackApplicationRightUnderList">
			<properties>
				<p name="serviceId">doctorAdviceSubmitQueryService</p>
				<p name="queryActionId">querytyRecords</p>
				<p name="entryName">phis.application.war.schemas.BQ_TYMX_TYSQ_YT</p>
			</properties>
		</module>
		<module id="WAR07" name="费用帐卡" type="1"
			script="phis.application.hos.script.HospitalPatientCardsViewModule">
			<properties>
				<p name="refForm">phis.application.war.WAR/WAR/WAR0701</p>
				<p name="refList">phis.application.war.WAR/WAR/WAR0702</p>
				<p name="refcontributions">phis.application.war.WAR/WAR/WAR0703</p>
				<p name="refinventory">phis.application.war.WAR/WAR/WAR0704</p>
			</properties>
			<action id="contributions" name="缴款" iconCls="money_add" />
			<action id="inventory" name="清单" iconCls="inventory" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR0701" name="结算管理form" type="1"
			script="phis.application.hos.script.HospitalSettlementManagementForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_FORM</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="WAR0702" name="结算管理list" type="1"
			script="phis.application.hos.script.HospitalSettlementManagementList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_LIST</p>
				<p name="reftabModule">phis.application.war.WAR/WAR/WAR070201</p>
				<p name="openBy">ward</p>
			</properties>
		</module>
		<module id="WAR070201" name="明细项目" type="1" script="phis.application.hos.script.DetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期" ref="phis.application.hos.HOS/HOS/HOS04020302" />
			<action id="mxxm" viewType="list" name="按明细" ref="phis.application.hos.HOS/HOS/HOS04020303" />
		</module>
		<module id="WAR0703" name="缴款记录" type="1"
			script="phis.application.hos.script.HospitalPaymentRecordList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_JSCX</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<!--该处在住院管理-》病人管理模块下(nurse和doctor角色下相同) -->
		<module id="WAR0704" name="费用清单" type="1"
			script="phis.application.hos.script.HospitalCostsListModule">
			<properties>
				<p name="refForm">phis.application.war.WAR/WAR/WAR070401</p>
				<p name="refList">phis.application.war.WAR/WAR/WAR070402</p>
				<p name="refDayList">phis.application.hos.HOS/HOS/HOS08020503</p>
			</properties>
			<action id="whole" name="全部" value="1" />
			<action id="medical" name="医疗" value="2" />
			<action id="drugs" name="药品" value="3" />
			<action id="refresh" name="刷新"/>
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR070401" name="费用清单form" type="1"
			script="phis.application.hos.script.HospitalCostsListForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYQD_FORM</p>
				<p name="colCount">6</p>
			</properties>
		</module>
		<module id="WAR070402" name="费用清单module" type="1"
			script="phis.application.hos.script.CostsListTabModule">
			<properties>
				<p name="openBy">Query</p>
			</properties>
			<action id="HospitalCostsListDetailedFormatTabQuery" viewType="list"
				name="明细格式" ref="phis.application.war.WAR/WAR/WAR07040201" />
			<action id="HospitalCostsListSumFormatTabQuery" viewType="list" name="汇总格式" ref="phis.application.hos.HOS/HOS/HOS0802050203" />	
			<action id="HospitalCostsListDoctorFormatTabQuery" viewType="list"
				name="医嘱格式" ref="phis.application.war.WAR/WAR/WAR07040202" />
		</module>
		<module id="WAR07040201" name="明细格式" type="1"
			script="phis.application.hos.script.HospitalCostsListList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD</p>
			</properties>
		</module>
		<module id="HOS0802050203" name="汇总格式" type="1" script="phis.application.hos.script.HospitalCostsSumListTab">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_HZGS</p>  
			</properties>
		</module>
		<module id="WAR07040202" name="医嘱格式" type="1"
			script="phis.application.hos.script.HospitalCostsListDoctorFormatTab">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_YZGS</p>
			</properties>
		</module>
		<module id="WAR0705" name="费用帐卡" type="1"
			script="phis.application.hos.script.HospitalPatientCardsViewModule">
			<properties>
				<p name="refForm">phis.application.war.WAR/WAR/WAR070501</p>
				<p name="refList">phis.application.war.WAR/WAR/WAR070502</p>
				<p name="refcontributions">phis.application.war.WAR/WAR/WAR0703</p>
				<p name="refinventory">phis.application.war.WAR/WAR/WAR070503</p>
			</properties>
			<action id="contributions" name="缴款" iconCls="money_add" />
			<action id="inventory" name="清单" iconCls="inventory" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR070501" name="结算管理form" type="1"
			script="phis.application.hos.script.HospitalSettlementManagementQueryForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_FORM</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="WAR070502" name="结算管理list" type="1"
			script="phis.application.hos.script.HospitalSettlementManagementQueryList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_LIST</p>
				<p name="reftabModule">phis.application.war.WAR/WAR/WAR070201</p>
				<p name="openBy">hospitalQuery</p>
			</properties>
		</module>
		<!--该处在住院管理-》病人管理模块下(nurse和doctor角色下相同) -->
		<module id="WAR070503" name="费用清单" type="1"
			script="phis.application.hos.script.HospitalCostsListQueryModule">
			<properties>
				<p name="refForm">phis.application.war.WAR/WAR/WAR07050301</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS08020502</p> <!--原为WAR07050302 -->
				<p name="refDayList">phis.application.hos.HOS/HOS/HOS08020503</p>
			</properties>
			<action id="whole" name="全部" value="1" />
			<action id="medical" name="医疗" value="2" />
			<action id="drugs" name="药品" value="3" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR07050301" name="费用清单form" type="1"
			script="phis.application.hos.script.HospitalCostsListForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYQD_FORM</p>
				<p name="labelWidth">55</p>
				<p name="colCount">5</p>
			</properties>
		</module>
		<module id="WAR07050302" name="费用清单list" type="1"
			script="phis.application.hos.script.HospitalCostsListList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD</p>
			</properties>
		</module>
		<module id="WAR09" name="医嘱录入(医生站)" type="1"
			script="phis.application.war.script.WardDoctorAdviceModule">
			<properties>
				<p name="openBy">doctor</p>
				<p name="refWardDoctorAdviceForm">phis.application.war.WAR/WAR/WAR0901</p>
				<p name="refWardDoctorAdviceTab">phis.application.war.WAR/WAR/WAR0902</p>
				<p name="refWardQuickInputTab">phis.application.war.WAR/WAR/WAR090204</p>
				<p name="refWardAdviceExecuteModule">phis.application.war.WAR/WAR/WAR04</p>
				<p name="refWardAdviceSubmitModule">phis.application.war.WAR/WAR/WAR08</p>
				<p name="refWardAdviceQueryModule">phis.application.war.WAR/WAR/WAR030404</p>
				<p name="refWardDoctorAdviceJFList">phis.application.war.WAR/WAR/WAR09020101</p>
				<p name="refWardHerbEnrty">phis.application.war.WAR/WAR/WAR030408</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="remove" name="删除" />
			<action id="removeGroup" name="删除组" iconCls="removeclinic"/>
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="singleStop" name="单停" iconCls="pill_delete" />
			<action id="allStop" name="全停" iconCls="pill_delete" />
			<action id="assignedEmpty" name="赋空" iconCls="pill_go" />
			<action id="submit" name="提交" iconCls="update" />
			<action id="save" name="保存" />
			<action id="query" name="查询" />
			<action id="refresh" name="刷新" />
			<action id="cylr" name="草药方" iconCls="newgroup" />
		</module>
		<module id="WAR0901" name="医嘱处理form" type="1"
			script="phis.application.war.script.WardDoctorAdviceForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_YZ</p>
				<p name="loadServiceId">wardPatientLoad</p>
				<p name="colCount">5</p>
			</properties>
		</module>
		<module id="WAR0902" name="医嘱处理tab" type="1"
			script="phis.application.war.script.WardDoctorAdviceTab">
			<action id="d_longAdviceTab" name="长期医嘱"
				ref="phis.application.war.WAR/WAR/WAR090201" type="tab" />
			<action id="d_tempAdviceTab" name="临时医嘱"
				ref="phis.application.war.WAR/WAR/WAR090202" type="tab" />
			<action id="d_EmergencyMedicationTab" name="急诊用药"
				ref="phis.application.war.WAR/WAR/WAR090205" type="tab" />
			<action id="d_DischargeMedicationTab" name="出院带药"
				ref="phis.application.war.WAR/WAR/WAR090206" type="tab" />
		</module>
		<module id="WAR090201" name="医嘱处理list(长期)" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_CQ</p>
				<p name="adviceType">longtime</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR090203</p>
			</properties>
		</module>
		<module id="WAR090202" name="医嘱处理list(临时)" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_LS</p>
				<p name="adviceType">temporary</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR090203</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="WAR090205" name="急诊用药list" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_JZ</p>
				<p name="adviceType">EmergencyMedication</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040203</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="WAR090206" name="出院带药list" type="1"
			script="phis.application.war.script.WardDoctorAdviceList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_DY</p>
				<p name="adviceType">DischargeMedication</p>
				<p name="refAppendAdviceList">phis.application.war.WAR/WAR/WAR03040203</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="WAR09020101" name="计费明细查询" type="1"
			script="phis.application.war.script.WardDoctorAdviceJFList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_YZCX</p>
			</properties>
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR090203" name="附加医嘱" type="1"
			script="phis.application.war.script.WardDoctorAdviceAppendList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FJ</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="remove" name="删除" />
		</module>
		<module id="WAR090204" name="医嘱助手" type="1"
			script="phis.application.war.script.WardDoctorAdviceHelpTab">
			<properties>
				<p name="tabPosition">bottom</p>
				<p name="resizeTabs">true</p>
			</properties>
			<action id="medicine" viewType="list" name="药品助手"
				ref="phis.application.war.WAR/WAR/WAR09020401" />
			<action id="clinic" viewType="list" name="项目助手"
				ref="phis.application.war.WAR/WAR/WAR09020402" />
			<action id="character" viewType="list" name="文字助手"
				ref="phis.application.war.WAR/WAR/WAR09020403" />
		</module>
		<module id="WAR09020401" name="药品医嘱录入" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">doctor</p>
			</properties>
			<action id="medicineCommon" viewType="list" name="常用药"
				ref="phis.application.war.WAR/WAR/WAR03040501" />
			<action id="medicinePersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040502" />
			<action id="medicineAll" viewType="list" name="全部"
				ref="phis.application.war.WAR/WAR/WAR03040503" />
		</module>
		<module id="WAR09020402" name="项目医嘱录入" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">doctor</p>
			</properties>
			<action id="clinicCommon" viewType="list" name="常用项"
				ref="phis.application.war.WAR/WAR/WAR03040504" />
			<action id="clinicPersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040505" />
			<action id="clinicAll" viewType="list" name="全部"
				ref="phis.application.war.WAR/WAR/WAR03040506" />
		</module>
		<module id="WAR09020403" name="文字医嘱录入" type="1"
			script="phis.application.war.script.WardDoctorAdviceQuickInputTab">
			<properties>
				<p name="openBy">doctor</p>
			</properties>
			<action id="characterCommon" viewType="list" name="常用"
				ref="phis.application.war.WAR/WAR/WAR03040507" />
			<action id="characterPersonalSet" viewType="list" name="医嘱组套"
				ref="phis.application.war.WAR/WAR/WAR03040508" />
		</module>
		<module id="WAR10" name="出院证管理(医生站)" type="1"
			script="phis.application.war.script.WardLeaveHospitalForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_CY2</p>
				<p name="colCount">4</p>
				<p name="labelWidth">60</p>
				<p name="showWinOnly">true</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="取消" iconCls="cancelonly" />
			<action id="close" name="关闭" iconCls="common_cancel" />
			<action id="print" name="打印" />
		</module>
		<module id="WAR1001" name="病人信息(医生站)" type="1"
			script="phis.application.war.script.WardPatientTab">
			<properties>
				<p name="openBy">doctor</p>
			</properties>
			<action id="doc_patientBaseTab" name="基本信息"
				ref="phis.application.war.WAR/WAR/WAR100101" type="tab" />
			<action id="doc_patientClinicTab" name="诊断"
				ref="phis.application.war.WAR/WAR/WAR100102" type="tab" />
			<action id="doc_patientAllergyMedTab" name="过敏药物"
				ref="phis.application.war.WAR/WAR/WAR100103" type="tab" />
			<action id="save" name="确认" type="button" />
			<action id="close" name="关闭" type="button" iconCls="common_cancel" />
		</module>
		<module id="WAR100101" name="病人信息Form" type="1"
			script="phis.application.war.script.WardPatientForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_DT2</p>
			</properties>
		</module>
		<module id="WAR100102" name="病人诊断list" type="1"
			script="phis.application.war.script.WardPatientDiseaseList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_RYZD_BQ</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="sx" name="刷新" iconCls="refresh"/>
		</module>
		<module id="WAR100103" name="病人过敏药物list" type="1"
			script="phis.application.war.script.WardPatientAllergyMedList">
			<properties>
				<p name="entryName">phis.application.war.schemas.GY_PSJL_BQ</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="WAR13" name="会诊申请" type="1"
			script="phis.application.war.script.ConsultationManageList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.YS_ZY_HZSQ</p>
				<p name="entryName_Idea">phis.application.hos.schemas.YS_ZY_HZYJ</p>
				<p name="sqService">sqService</p>
				<p name="serviceAction">getHzList</p>
				<p name="applyform">phis.application.war.WAR/WAR/WAR1303</p>
				<p name="applyModule">phis.application.war.WAR/WAR/WAR1304</p>
				<p name="updateCls">phis.application.war.script.ConsultationApplyForm_New</p>
				<p name="createCls">phis.application.war.script.ConsultationApplyForm_New</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="submit" name="提交" iconCls="commit" />
			<action id="openXML" name="会诊意见" iconCls="page_edit" />
			<action id="close2" name="结束会诊" iconCls="page_go" />
			<action id="thbq" name="退回" iconCls="pill_go" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" iconCls="print" />
			<!--<action id="close" name="关闭" iconCls="common_cancel"/> -->
		</module>
		<module id="WAR1301" name="病人信息" type="1"
			script="phis.application.war.script.ConsultationApplyForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_CONS</p>
			</properties>
		</module>
		<module id="WAR1302" name="病区信息" type="1"
			script="phis.application.war.script.ConsultationApplyList">
			<properties>
				<p name="entryName">phis.application.war.schemas.GY_KSDM_CONS</p>
			</properties>
		</module>
		<module id="WAR1303" name="会诊Form" type="1"
			script="phis.application.war.script.ConsultationApplyForm_New">
			<properties>
				<p name="entryName">phis.application.hos.schemas.YS_ZY_HZSQ</p>
				<p name="colCount">5</p>
				<p name="labelWidth">60</p>
				<p name="showWinOnly">true</p>
			</properties>
		</module>
		<module id="WAR1304" name="会诊申请单" type="1"
			script="phis.application.war.script.ConsultationApplyModule_New">
			<properties>
				<p name="refHospitalPatientDeptInformationForm">phis.application.war.WAR/WAR/WAR030701</p>
			</properties>

		</module>
		<module id="WAR1305" name="会诊意见" type="1"
			script="phis.application.war.script.ConsultationIdeaApplyForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.YS_ZY_HZYJ</p>
				<p name="colCount">5</p>
				<p name="labelWidth">60</p>
				<p name="showWinOnly">true</p>
			</properties>
		</module>
		<module id="WAR82" name="变动医嘱查询"
			script="phis.application.war.script.PatientMedicalAdviceQuery">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR8201</p>
				<p name="refPatientMedicalAdviceQueryPrint">phis.application.war.WAR/WAR/WAR8202</p>
			</properties>
		</module>
		<module id="WAR8201" name="病人变动医嘱查询list" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_BDCX</p>
			</properties>
		</module>
		<module id="WAR8202" name="病人变动医嘱打印"
			script="phis.prints.script.PatientMedicalAdviceQueryPrint" type="1">
			<properties>
			</properties>
		</module>
		<!-- add by chzhxiang -->
		<module id="WAR16" name="住院检验录入" type="1"
			script="phis.application.war.script.InhospLisMedicalRecordModule">
		</module>
		<module id="WAR17" name="住院检验执行" type="1"
			script="phis.application.war.script.InhospLisMedicalExecuteModule">
		</module>
		<module id="WAR19" name="住院检验报告" type="1"
			script="phis.application.war.script.InhospLisMedicalReportModule">
		</module>

		<!--PEMR新增业务功能 -->
		<module id="CFG12" name="西医科室对照" type="1"
			script="phis.application.cfg.script.ConfigWestMediSpecModule">
			<properties>
				<p name="refZkflTree">phis.application.war.WAR/WAR/CFG1201</p>
				<p name="refZkksList">phis.application.war.WAR/WAR/CFG1202</p>
				<p name="refKsdmList">phis.application.war.WAR/WAR/CFG1203</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CFG1201" name="西医专科分类tree" type="1"
			script="phis.application.cfg.script.ConfigWestMediSpecTree">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZKFL</p>
			</properties>
		</module>
		<module id="CFG1202" name="西医科室对照list" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZKKS</p>
				<p name="disablePagingTbr">true</p>
				<p name="gridDDGroup">secondGridDDGroup</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="CFG1203" name="科室列表list" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.GY_KSDM_EMR</p>
				<p name="disablePagingTbr">true</p>
				<p name="gridDDGroup">firstGridDDGroup</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="TEST" name="电子病历" type="1"
			script="phis.application.cic.script.EMRDemoModule">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BRDA_CIC</p>
				<p name="refTplChooseModule">phis.application.war.WAR/WAR/WAR40</p>
				<p name="refUserFulList">phis.application.war.WAR/WAR/WAR90</p>
				<p name="refUserFulForm">phis.application.war.WAR/WAR/WAR91</p>
				<p name="refDataBox">phis.application.war.WAR/WAR/WAR95</p>
			</properties>
			<action id="newEMR" name="新建病历" iconCls="update" />
			<action id="importEMR" name="调入模版" iconCls="commit" />
			<action id="loadEMR" name="加载病历" iconCls="creditcards" />
			<action id="save" name="保存病历" />
			<action id="showVersion" name="当前版本号" />
			<action id="print" name="打印" />
			<action id="downLoad" name="下载安装" iconCls="creditcards" />
			<action id="showWin" name="打开窗口" iconCls="creditcards" />
			<action id="loadBlbh" level="two" name="病历编号" iconCls="creditcards" />
			<action id="setIll" level="two" name="段落键" iconCls="creditcards" />
			<action id="userFul" name="常用语" iconCls="creditcards" />
			<action id="addTemp" name="新增模版" iconCls="creditcards" />
			<action id="dataBox" name="数据盒" iconCls="creditcards" />
		</module>
		<module id="WAR90" name="我的常用语" type="1"
			script="phis.application.cic.script.UserFulExpressionsList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.PRIVATETEMPLATE_USERFUL</p>
			</properties>
		</module>
		<module id="WAR91" name="常用语信息补充" type="1"
			script="phis.application.cic.script.UserFulExpressionsForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.PRIVATETEMPLATE_USERFUL</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="WAR95" name="数据盒" type="1"
			script="phis.application.cic.script.UserDataBoxModule">
			<properties>
				<p name="viewPanel">phis.application.war.WAR/WAR/WAR9501</p>
				<p name="dataList">phis.application.war.WAR/WAR/WAR9502</p>
				<p name="dataList2">phis.application.war.WAR/WAR/WAR9503</p>
				<p name="temporaryTab">phis.application.war.WAR/WAR/WAR9504</p>
				<p name="dicNormal">phis.application.war.WAR/WAR/WAR9505</p>
				<p name="jytestresult">phis.application.war.WAR/WAR/WAR9506</p>
			</properties>
		</module>
		<module id="WAR9501" name="数据盒视图" type="1"
			script="phis.application.cic.script.UserDataBoxViewPanel">
			<properties>
			</properties>
		</module>
		<module id="WAR9502" name="病历" type="1"
			script="phis.application.cic.script.UserDataGroupList">
			<properties>
			</properties>
			<action id="open" name="打开" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR9503" name="医嘱" type="1"
			script="phis.application.cic.script.UserDataBoxList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BQYZ_SJH</p>
			</properties>
			<action id="refresh" name="刷新" iconCls="commit" />
			<action id="appoint" name="引用" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR9504" name="特殊符号" type="1"
			script="phis.application.cic.script.UserDataTemporaryTabModule">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLZD</p>
			</properties>
			<action id="appoint" name="引用" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR9505" name="常用字典" type="1"
			script="phis.application.cic.script.UserDataDicModule">
			<properties>
				<p name="dicId">dictionaries</p>
				<p name="dicList">phis.application.war.WAR/WAR/WAR950501</p>
				<p name="itemList">phis.application.war.WAR/WAR/WAR950502</p>
			</properties>
			<action id="appoint" name="引用" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR950501" name="字典名称列表" type="1"
			script="phis.application.cic.script.UserDataDicList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_CYZD</p>
			</properties>
		</module>
		<module id="WAR950502" name="字典项目列表" type="1"
			script="phis.application.cic.script.UserDataItemList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZDXM</p>
			</properties>
		</module>
		<module id="WAR9506" name="检验结果" type="1" script="phis.application.cic.script.JYTestResult">
			<properties>
				<p name="entryName">phis.application.pub.schemas.V_LIS_TESTRESULT</p>
			</properties>
			<action id="appoint" name="引用" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>		
		<module id="WAR40" name="病历模版选择" type="1"
			script="phis.application.emr.script.EMRmodeChooseModule">
			<properties>
				<p name="refBllbTree">phis.application.war.WAR/WAR/WAR4001</p>
				<p name="refBlmbList">phis.application.war.WAR/WAR/WAR4002</p>
				<p name="refBlmbForm">phis.application.war.WAR/WAR/WAR4003</p>
			</properties>
			<action id="save" name="确定" iconCls="commit" />
			<action id="loadEMR" name="预览" iconCls="page_white_magnify" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR4001" name="病历类别tree" type="1"
			script="phis.application.emr.script.EMRmodeBllbTree">
		</module>
		<module id="WAR4002" name="病历模版List" type="1"
			script="phis.application.emr.script.EMRmodeBlmbList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.V_EMR_BLMB_PRI</p>
			</properties>
		</module>
		<module id="WAR4003" name="病历模版Form" type="1"
			script="phis.application.emr.script.EMRmodeBlmbForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BCYS</p>
				<p name="autoLoadData">false</p>
				<p name="colCount">2</p>
			</properties>
		</module>
		<module id="WAR4004" name="修改标题" type="1"
			script="phis.application.emr.script.EMRmodeBlmbForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BCYS</p>
				<p name="autoLoadData">false</p>
				<p name="colCount">2</p>
			</properties>
			<action id="save" name="确定" iconCls="commit" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR81" name="字典信息" type="1"
			script="phis.application.emr.script.EMRDicInformationList">
			<properties>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="WAR33" name="病案资料查询" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_YPGM_BQ</p>
			</properties>
		</module>


		<module id="WAR35" name="病历文档恢复" type="1"
			script="phis.application.emr.script.EMRRecoverRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_RECOVER</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="preview" name="预览" iconCls="arrow_refresh" />
			<action id="renew" name="恢复" iconCls="arrow_undo" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR36" name="电子病历" type="1"
			script="phis.application.emr.script.EMREditorModule">
			<properties>
				<p name="entryName">phis.application.cic.schemas.MS_BRDA_CIC</p>
				<p name="refTplChooseModule">phis.application.war.WAR/WAR/WAR40</p>
				<p name="refEmrDetailModule">phis.application.war.WAR/WAR/WAR340102</p>
				<p name="refParaWin">phis.application.war.WAR/WAR/WAR4004</p>
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
			<action id="close" name="关闭" />
			<action id="autoSave" name="备份" iconCls="database_save" />
			<action id="downLoad" name="下载安装" iconCls="drive_cd" />
			<action id="downLoadTwo" name="火狐插件" iconCls="drive_cd" />
			<action id="revRecords" name="审计" iconCls="report_edit" />
		</module>
		<module id="WAR41" name="修改痕迹" type="1"
			script="phis.application.emr.script.EMRModifyRecordModule">
			<properties>
				<p name="refModifyList">phis.application.war.WAR/WAR/WAR4101</p>
				<p name="refModifyPanel">phis.application.war.WAR/WAR/WAR4102</p>
			</properties>
			<action id="compare" name="对比两条选中的痕迹" iconCls="comparison" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR4101" name="修改痕迹List" type="1"
			script="phis.application.emr.script.EMRModifyRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLXG_CX</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR4102" name="修改痕迹Form" type="1"
			script="phis.application.emr.script.EMRModifyRecordForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLXG_HJ</p>
			</properties>
		</module>
		<module id="WAR42" name="未填写元素列表" type="1"
			script="phis.application.emr.script.EMRUnsetElementList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_YSLB</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="WAR43" name="病历打印预览" type="1"
			script="phis.application.emr.script.EMRPrintViewModule">
			<action id="print" name="打印" iconCls="print" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR44" name="另存为个人模版" type="1"
			script="phis.application.emr.script.EMRPersonalPlateForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.PRIVATETEMPLATE_SAVEAS</p>
			</properties>
			<action id="save" name="保存" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR45" name="体温单" type="1"
			script="phis.application.emr.script.EMRTemperatureModule">
			<properties>
				<p name="refCharModule">phis.application.war.WAR/WAR/WAR4501</p>
				<p name="refTableModule">phis.application.war.WAR/WAR/WAR4502</p>
			</properties>
			<action id="first" name="首周" iconCls="control_start" />
			<action id="pre" name="上一周" iconCls="control_previous" />
			<action id="next" name="下一周" iconCls="control_next" />
			<action id="last" name="末周" iconCls="control_end" />
			<action id="print" name="打印" iconCls="print" />
			<action id="refresh" name="刷新"  />
		</module>
		<module id="WAR4501" name="体温单(char)" type="1"
			script="phis.application.emr.script.EMRTemperatureChart">

		</module>
		<module id="WAR4502" name="体温单(table)" type="1"
			script="phis.application.emr.script.EMRTemperatureTableModule">
			<properties>
				<p name="entryName">phis.application.war.schemas.BQ_SMTZ</p>
				<p name="tabPosition">bottom</p>
				<p name="resizeTabs">true</p>
				<p name="autoHeight">false</p>
				<p name="frame">true</p>
			</properties>
			<action id="SMTZForm" viewType="form" name="体征录入"
				ref="phis.application.war.WAR/WAR/WAR450201" iconCls="coins" />
			<action id="SMTZList" viewType="list" name="体征列表"
				ref="phis.application.war.WAR/WAR/WAR450202" iconCls="coins" />
		</module>
		<module id="WAR450201" name="体征录入" type="1"
			script="phis.application.emr.script.EMRTemperatureForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.BQ_TZXM_ALIS</p>
			</properties>
			<action id="create" name="保存" iconCls="save" />
			<action id="xz" name="新增" iconCls="create" />
		</module>

		<module id="WAR450202" name="体征列表" type="1"
			script="phis.application.emr.script.EMRTemperatureList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.BQ_SMTZ_List</p>
			</properties>
			<action id="xg" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
		</module>


		<module id="WAR47" name="病历预览" type="1"
			script="phis.application.emr.script.EMRHtmlPreview"></module>
		<module id="WAR48" name="个人模板维护"
			script="phis.application.emr.script.EMRPersonalModeManageModule">
			<properties>
				<p name="refBllbTree">phis.application.war.WAR/WAR/WAR4801</p>
				<p name="refBlmbList">phis.application.war.WAR/WAR/WAR4802</p>
				<p name="refBlmbModifyModule">phis.application.war.WAR/WAR/WAR4803</p>
			</properties>
			<action id="cancellation" name="注销" iconCls="writeoff" />
			<action id="renew" name="恢复" iconCls="arrow_undo" />
			<action id="save" name="保存" />
			<action id="modify" name="修改模版" iconCls="update" />
			<action id="review" name="预览" iconCls="page_white_magnify" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR4801" name="病历类别tree" type="1"
			script="phis.application.emr.script.EMRPersonalModeManageTree">
		</module>
		<module id="WAR4802" name="病历模版List" type="1"
			script="phis.application.emr.script.EMRPersonalModeManageList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.PRIVATETEMPLATE_MNG</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">1</p>
			</properties>
		</module>
		<module id="WAR4803" name="病历模版修改界面" type="1"
			script="phis.application.emr.script.EMRPersonalViewModule">
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>

		<module id="WAR93" name="病历审阅日志" type="1"
			script="phis.application.chr.script.CaseHistoryReviewModule">
			<properties>
				<p name="refLModule">phis.application.war.WAR/WAR/WAR9301</p>
				<p name="refRModule">phis.application.war.WAR/WAR/WAR9302</p>
			</properties>
		</module>
		<module id="WAR9301" name="病历信息" type="1"
			script="phis.application.chr.script.CaseInfoTabModule">
			<properties>
			</properties>
			<action id="caseInfo" viewType="form" name="病历信息"
				ref="phis.application.war.WAR/WAR/WAR930101" iconCls="coins" />
			<action id="allCase" viewType="list" name="所有病历"
				ref="phis.application.war.WAR/WAR/WAR930102" iconCls="coins" />
		</module>
		<module id="WAR930101" name="病历信息" type="1"
			script="phis.application.chr.script.CaseInfomationForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_SJRZ</p>
			</properties>
			<action id="openXML" name="查看XML数据" />
			<action id="openHTML" name="查看HTML文档" />
			<action id="openSTR" name="查看结构化元素" />
		</module>
		<module id="WAR930102" name="所有病历" type="1"
			script="phis.application.chr.script.AllCaseSelectList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BL01_SJRZ</p>
			</properties>
		</module>
		<module id="WAR9302" name="记录信息" type="1"
			script="phis.application.chr.script.RecordInfoTabModule">
			<properties>
			</properties>
			<action id="visitRecord" viewType="list" name="访问记录"
				ref="phis.application.war.WAR/WAR/WAR930201" iconCls="new" />
			<action id="updateRecord" viewType="list" name="修改记录"
				ref="phis.application.war.WAR/WAR/WAR930202" iconCls="new" />
			<action id="autographRecord" viewType="list" name="签名记录"
				ref="phis.application.war.WAR/WAR/WAR930203" iconCls="new" />
			<action id="printRecord" viewType="list" name="打印记录"
				ref="phis.application.war.WAR/WAR/WAR930204" iconCls="new" />
			<action id="qualityInfo" viewType="list" name="质控信息"
				ref="phis.application.war.WAR/WAR/WAR930205" iconCls="new" />
			<action id="allRecord" viewType="list" name="所有记录"
				ref="phis.application.war.WAR/WAR/WAR930206" iconCls="new" />
		</module>
		<module id="WAR930201" name="访问记录" type="1"
			script="phis.application.chr.script.VisitRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSJRZ</p>
			</properties>
		</module>
		<module id="WAR930202" name="修改记录" type="1"
			script="phis.application.chr.script.UpdateRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSJRZ</p>
			</properties>
		</module>
		<module id="WAR930203" name="签名记录" type="1"
			script="phis.application.chr.script.AutographRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSJRZ</p>
			</properties>
		</module>
		<module id="WAR930204" name="打印记录" type="1"
			script="phis.application.chr.script.PrintRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSJRZ</p>
			</properties>
		</module>
		<module id="WAR930205" name="质控信息" type="1"
			script="phis.application.chr.script.QualityInfoList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSJRZ</p>
			</properties>
		</module>
		<module id="WAR930206" name="所有记录" type="1"
			script="phis.application.chr.script.AllRecordList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLSJRZ</p>
			</properties>
		</module>
		<module id="WAR94" name="病历书写设置"
			script="phis.application.war.script.DoctorCustomMadeForm">
			<properties>
				<p name="showWinOnly">true</p>
				<p name="entryName">phis.application.emr.schemas.EMR_YSXG_GRCS</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="WAR96" name="医嘱卡片打印"
			script="phis.application.war.script.OrderCardsPrintModule">
			<properties>
				<p name="refModule">phis.application.war.WAR/WAR/WAR9601</p>
			</properties>
			<action id="print" name="打印" notReadOnly="true" />
		</module>

		<module id="WAR9601" name="医嘱病人Module" type="1"
			script="phis.application.war.script.OrderCardsPrintTabModule">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR960101</p>
				<p name="refModule">phis.application.war.WAR/WAR/WAR960102</p>
			</properties>
		</module>
		<module id="WAR960101" name="医嘱病人list" type="1"
			script="phis.application.war.script.OrderCardsList">
			<properties>
				<p name="entryName">phis.application.war.schemas.BQ_YZKP</p>
				<p name="listServiceId">orderCardsListService</p>
				<p name="serviceAction">queryPatientList</p>
			</properties>
		</module>
		<module id="WAR960102" name="卡片类型Tab" type="1"
			script="phis.application.war.script.OrderCardsTypeTabModule">
			<properties>
			</properties>
			<action id="mouthCard" viewType="list" name="口服卡"
				ref="phis.application.war.WAR/WAR/WAR96010201" />
			<action id="injectionCard" viewType="list" name="注射卡"
				ref="phis.application.war.WAR/WAR/WAR96010202" />
			<action id="stillDripCard" viewType="list" name="静滴卡"
				ref="phis.application.war.WAR/WAR/WAR96010203" />
			<action id="transfusionPatrolCard" viewType="list" name="输液巡视卡"
				ref="phis.application.war.WAR/WAR/WAR96010204" />
		</module>
		<module id="WAR96010201" name="口服卡" type="1"
			script="phis.application.war.script.OrderCardsMouthCard">
			<properties>
			</properties>
		</module>
		<module id="WAR96010202" name="注射卡" type="1"
			script="phis.application.war.script.OrderCardsInjectionCard">
			<properties>
			</properties>
		</module>
		<module id="WAR96010203" name="静滴卡" type="1"
			script="phis.application.war.script.OrderCardsStillDripCard">
			<properties>
			</properties>
		</module>
		<module id="WAR96010204" name="输液巡视卡" type="1"
			script="phis.application.war.script.OrderCardsTransfusionPatrolCard">
			<properties>
			</properties>
		</module>
		<module id="WAR97" name="提醒查询" script="phis.script.TabModule">
			<action id="HospitalCostProcessingModuleTab" viewType="list"
				name="转入记录" ref="phis.application.war.WAR/WAR/WAR9701" />
			<action id="HosPaymentTypeTab" viewType="list" name="科室转出而对方未确认"
				ref="phis.application.war.WAR/WAR/WAR9702" />
		</module>
		<module id="WAR9701" name="转入记录" type="1"
			script="phis.application.war.script.WardRemainInList_In">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_ZKJL_Remain</p>
				<p name="listServiceId">remainService</p>
				<p name="serviceAction">queryInList</p>
			</properties>
			<action id="sure" name="确定" iconCls="commit" />

		</module>
		<module id="WAR9702" name="科室转出而对付未确认" type="1"
			script="phis.application.war.script.WardRemainInList_Out">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_ZKJL_Remain</p>
			</properties>

		</module>
		<module id="WAR9703" name="分配床位" type="1"
			script="phis.application.war.script.FixedAssetsList_Remain">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_CWSZ_Remain</p>
				<p name="listServiceId">remainService</p>
				<p name="serviceAction">queryFp</p>
			</properties>
			<action id="save" name="确认" iconCls="commit" />
			<action id="cancel" iconCls="common_cancel" name="取消"></action>
		</module>
		<module id="WAR98" name="医嘱批量复核"
			script="phis.application.war.script.DoctorBatchReviewModule">
			<properties>
				<p name="refDoctorReviewList">phis.application.war.WAR/WAR/WAR9801</p>
			</properties>
		</module>
		<module id="WAR9801" name="医嘱复核列表" type="1"
			script="phis.application.war.script.DoctorReviewList">
			<properties>
				<p name="listServiceId">wardPatientManageService</p>
				<p name="serviceAction">queryDoctorReviewList</p>
				<p name="entryName">phis.application.war.schemas.ZY_BQYZ_FH</p>
			</properties>
			<action id="dbr" name="单病人" iconCls="user" />
			<action id="qb" name="全部" iconCls="commit" />
			<action id="sx" name="刷新" iconCls="refresh" />
		</module>

		<module id="WAR0708" name="住院转诊模块" type="1"
			script="phis.application.drc.script.DRApplicationModule">
			<properties>
				<p name="refForm">phis.application.war.WAR/WAR/WAR070801</p>
				<p name="refList">phis.application.war.WAR/WAR/WAR070802</p>
			</properties>
		</module>
		<module id="WAR070801" name="住院转诊单" type="1"
			script="phis.application.drc.script.DRHospitalizationApplicationForm">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_SendExchange</p>
				<p name="serviceId">referralService</p>
				<p name="saveServiceAction">saveZySendExchange</p>
			</properties>
			<action id="submit" name="提交" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="WAR070802" name="住院转诊信息" type="1"
			script="phis.script.TabModule">
			<action id="WAR07080201" viewType="module" name="住院转诊记录"
				ref="phis.application.war.WAR/WAR/WAR07080201" />
		</module>
		<module id="WAR07080201" name="住院转诊记录" type="1"
			script="phis.application.drc.script.DRHospitalizationApplicationList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_ZYHOSPITALREGRECORD</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="WAR50" name="病区发药查询"
			script="phis.application.hph.script.HospitalPharmacyMedicine">
			<properties>
			</properties>
			<action id="HZFYTab" viewType="HZFYTab" name="汇总查询"
				ref="phis.application.war.WAR/WAR/WAR5001" />
			<action id="BRFYTab" viewType="BRFYTab" name="按病人查询"
				ref="phis.application.war.WAR/WAR/WAR5002" />
		</module>
		<module id="WAR5001" name="汇总查询" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineHZ">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR500101</p>
				<p name="refHospitalPharmacyMedicineHZPrint">phis.application.war.WAR/WAR/WAR500102</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="WAR500101" name="汇总查询list" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineHZlist">
			<properties>
				<p name="fullserviceId">phis.hospitalPharmacyMedicineService</p>
				<p name="queryServiceActionID">queryCollectRecordsHZ</p>
				<p name="entryName">phis.application.hph.schemas.ZY_BQYZ_FY</p>
			</properties>
		</module>
		<module id="WAR500102" name="汇总查询打印"
			script="phis.prints.script.HospitalPharmacyMedicineHZPrintView" type="1">
			<properties>
			</properties>
		</module>
		<module id="WAR5002" name="按病人查询" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBR">
			<properties>
				<p name="initializationServiceActionID">initialization</p>
				<p name="initializationServiceID">medicinesPharmacyManageService</p>
				<p name="serviceId">hospitalPharmacyMedicineService</p>
				<p name="queryMedicineDepartmentActionID">queryMedicineDepartment</p>
				<p name="refLeftList">phis.application.war.WAR/WAR/WAR500201</p>
				<p name="refRightList">phis.application.war.WAR/WAR/WAR500202</p>
				<p name="refHospitalPharmacyMedicineBRPrint">phis.application.war.WAR/WAR/WAR500203</p>
				<p name="queryWardActionID">queryWard</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="WAR500201" name="按病人查询左边List" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBRleft">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ03_BR</p>
			</properties>
		</module>
		<module id="WAR500202" name="按病人查询右边List" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBRright">
			<properties>
				<p name="entryName">phis.application.hph.schemas.ZY_BQYZ_FY</p>
			</properties>
		</module>
		<module id="WAR500203" name="按病人查询打印"
			script="phis.prints.script.HospitalPharmacyMedicineBRPrintView" type="1">
			<properties>
			</properties>
		</module>
		<module id="WAR51" name="病区退药查询"
			script="phis.application.hph.script.HospitalPharmacyMedicineBack">
			<properties>
			</properties>
			<action id="HZTYTab" viewType="HZTYTab" name="汇总查询"
				ref="phis.application.war.WAR/WAR/WAR5101" />
			<action id="BRTYTab" viewType="BRTYTab" name="按病人查询"
				ref="phis.application.war.WAR/WAR/WAR5102" />
		</module>
		<module id="WAR5101" name="汇总查询" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBackHZ">
			<properties>
				<p name="refList">phis.application.war.WAR/WAR/WAR510101</p>
				<p name="refHospitalPharmacyMedicineBackHZPrint">phis.application.war.WAR/WAR/WAR510102</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="WAR510101" name="汇总查询list" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBackHZlist">
			<properties>
				<p name="fullserviceId">phis.hospitalPharmacyMedicineService</p>
				<p name="queryServiceActionID">queryCollectRecordsBackHZ</p>
				<p name="entryName">phis.application.hph.schemas.ZY_BQYZ_FY</p>
			</properties>
		</module>
		<module id="WAR510102" name="汇总查询打印"
			script="phis.prints.script.HospitalPharmacyMedicineBackHZPrintView"
			type="1">
			<properties>
			</properties>
		</module>
		<module id="WAR5102" name="按病人查询" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBackBR">
			<properties>
				<p name="initializationServiceActionID">initialization</p>
				<p name="initializationServiceID">medicinesPharmacyManageService</p>
				<p name="serviceId">hospitalPharmacyMedicineService</p>
				<p name="queryMedicineDepartmentActionID">queryMedicineDepartment</p>
				<p name="refLeftList">phis.application.war.WAR/WAR/WAR510201</p>
				<p name="refRightList">phis.application.war.WAR/WAR/WAR510202</p>
				<p name="refHospitalPharmacyMedicineBackBRPrint">phis.application.war.WAR/WAR/WAR510203</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="WAR510201" name="按病人查询左边List" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBackBRleft">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ03_BR</p>
			</properties>
		</module>
		<module id="WAR510202" name="按病人查询右边List" type="1"
			script="phis.application.hph.script.HospitalPharmacyMedicineBackBRright">
			<properties>
				<p name="entryName">phis.application.hph.schemas.ZY_BQYZ_FY</p>
			</properties>
		</module>
		<module id="WAR510203" name="按病人查询打印"
			script="phis.prints.script.HospitalPharmacyMedicineBackBRPrintView"
			type="1">
			<properties>
			</properties>
		</module>
		<module id="WAR52" name="病区退费"
			script="phis.application.war.script.WardClinicsRefundModule">
			<properties>
				<p name="refBaseForm">phis.application.war.WAR/WAR/WAR5201</p>
				<p name="refUsedList">phis.application.war.WAR/WAR/WAR5202</p>
				<p name="refUnUsedList">phis.application.war.WAR/WAR/WAR5203</p>
			</properties>
			<action id="save" name="执行" />
			<action id="close" name="关闭" />
		</module>
		<module id="WAR5201" name="病区退费form" type="1"
			script="phis.application.war.script.WardClinicsRefundForm">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_BRRY_YZ</p>
				<p name="showButtonOnTop">0</p>
				<p name="loadServiceId">wardPatientLoad</p>
				<p name="colCount">5</p>
			</properties>
		</module>
		<module id="WAR5202" name="已收费项目list" type="1"
			script="phis.application.war.script.WardClinicsChargeList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_FYMX_BQTF</p>
			</properties>
		</module>
		<module id="WAR5203" name="退费项目list" type="1"
			script="phis.application.war.script.WardClinicsRefundList">
			<properties>
				<p name="entryName">phis.application.war.schemas.ZY_FYMX_BQYT</p>
			</properties>
		</module>
		<module id="WAR53" name="抗菌药物使用信息" type="1"
			script="phis.prints.script.AntimicrobialDrugUseInformationAccountingPrintView">
		</module>
		<module id="WAR54" name="门诊抗菌药物使用统计" script="phis.prints.script.AntimicrobialDrugDepartment_MZ_UseInfoList">
		</module>
		<module id="WAR57" name="门诊医生抗菌药物使用统计"  type="1" script="phis.prints.script.AntimicrobialDrugDoctorUseInformationPrintView">
		</module>
		<module id="WAR_MZKJYWMX" name="门诊抗菌药物使用明细" type="1"  
			script="phis.prints.script.AntimicrobialDrugClinicDoctorUseDetailsPrintView" />
		<module id="WAR55" name="住院抗菌药物使用统计" script="phis.prints.script.AntimicrobialDrugDepartmentUseInformationPrintView">
		</module>
		<module id="WAR59" name="住院医生抗菌药物使用统计"  type="1" script="phis.prints.script.AntimicrobialDrugDoctorUseInformationPrintView_ZY">
		</module>
		<module id="WAR56" name="抗菌药物使用强度明细" type="1" script="phis.prints.script.AntimicrobialDrugUseIntensityInformationPrintView">
		</module>
		<module id="WAR58" name="住院抗菌药物使用强度"   script="phis.prints.script.AntimicrobialDrugDeptUseIntensityInformationPrintView">
			<properties>
				<p name="AntimicrobialDrugUseIntensityInformation">phis.application.war.WAR/WAR/WAR5201</p>
			</properties>
		</module>
		
		<!-- add renwei 2020-08-07 -->
		<module id="WAR71" name="门诊基本药物使用统计" script="phis.prints.script.BasicDrugUseStatistics_MZ">
		</module>
		<!-- add renwei 2020-12-08 -->
		<module id="WAR72" name="门诊患者基本药物处方占比统计" script="phis.prints.script.BasicDrugUseStatistics_MZCF">
		</module>
		<module id="WAR73" name="住院患者基本药物使用率" script="phis.prints.script.BasicDrugUseStatistics_ZYCF">
		</module>
		
		<module id="WAR99" name="病区护士评估"
			script="phis.prints.script.WarNursesAssessPrintView">
		</module>
		<module id="WAR101" name="护理记录查询" script="phis.prints.script.NurseRecordView">
		</module>
		
		<module id="WAR28" name="医嘱本打印" script="phis.application.war.script.WardDoctorPrintManagementModule">
			<properties>
				<p name="refLeftList">phis.application.war.WAR/WAR/WAR2801</p> 
				<p name="rightModule">phis.application.war.WAR/WAR/WAR2802</p>	
			</properties>
		</module>
		<module id="WAR2801" name="医嘱本打印左边List" type="1" script="phis.application.war.script.WardDoctorPrintManagementLeftList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YZDY_BR</p>
				<p name="serviceId">phis.wardDoctorPrintService</p>
				<p name="serviceAction">queryYzdyBrxx</p>
			</properties>
			<action id="query" name="查询" />
		</module>
		<module id="WAR2802" name="医嘱本打印右边tab" type="1" script="phis.application.war.script.WardDoctorPrintManagementRightModule">
			<properties>
				<p name="serviceId">wardDoctorPrintService</p>
				<p name="serviceAction">saveWardDoctorPrint</p>
				<p name="querySFTDAction">querySFTD</p>
				<p name="zdhListRef">phis.application.war.WAR/WAR/WAR280203</p>
				<p name="zdhListRef_ls">phis.application.war.WAR/WAR/WAR280204</p>
			</properties>
			<action id="cqyz"  name="长期医嘱" ref="phis.application.war.WAR/WAR/WAR280201" />
			<action id="lsyz"  name="临时医嘱" ref="phis.application.war.WAR/WAR/WAR280202" />	
		</module>
		<module id="WAR280201" name="医嘱本打印-长期医嘱" type="1" script="phis.application.war.script.WardDoctorPrintManagementRightModuleFirstList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YZDY_CQYZ</p>
				<p name="serviceId">phis.wardDoctorPrintService</p>
				<p name="serviceAction">queryYz</p>
			</properties>
		</module>
		<module id="WAR280202" name="医嘱本打印-临时医嘱" type="1" script="phis.application.war.script.WardDoctorPrintManagementRightModuleSecondList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YZDY_LSYZ</p>
				<p name="serviceId">phis.wardDoctorPrintService</p>
				<p name="serviceAction">queryYz</p>
			</properties>
		</module>
		<module id="WAR280203" name="医嘱本打印-指定行打印" type="1" script="phis.application.war.script.WardDoctorPrintManagementRightModuleThirdList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YZDY_ZDH</p>
				<p name="serviceId">phis.wardDoctorPrintService</p>
				<p name="serviceAction">queryDyjl</p>
			</properties>
			<action id="save" name="确认" />
			<action id="close" name="取消" iconCls="common_cancel"/>
			<action id="zs" name="说明:从选择页码,行号的记录开始往下打印" iconCls="page_edit"/>
		</module>
		<module id="WAR280204" name="医嘱本打印-指定行打印_临时医嘱" type="1" script="phis.application.war.script.WardDoctorPrintManagementRightModuleThirdList">
			<properties>
				<p name="entryName">phis.application.war.schemas.YZDY_ZDH_LS</p>
				<p name="serviceId">phis.wardDoctorPrintService</p>
				<p name="serviceAction">queryDyjl</p>
			</properties>
			<action id="save" name="确认" />
			<action id="close" name="取消" iconCls="common_cancel"/>
		</module>
		<module id="WAR29" name="出院病人分病种统计" 
			script="phis.application.war.script.DischargedPatientsDividedDiseaseStatisticsModule" >
			<properties>
			</properties>
			<action id="query" name="查询" />
			<action id="print" name="打印" />
		</module>
		
		<module id="WAR61" name="放射报告结果"  type="1"
			script="phis.application.cic.script.ClinicalcheckbgModule">
		</module>
		 <module id="WAR62" name="超声报告结果" type="1"
			script="phis.application.cic.script.ClinicalcheckbgModule">
		</module>
		<module id="WAR63" name="pacs影像查看" type="1"
			script="phis.application.cic.script.ClinicalcheckbgModule">
		</module>
		<module id="WAR641" name="查看心电图" type="1"
			script="phis.application.cic.script.ClinicElectrocardiogramModule">
		</module>
		<module id="WAR65" name="糖尿病并发症筛查报告" type="1"
			script="phis.application.cic.script.ClinicTnbbfzscModule">
		</module>
		<!--医嘱本打印(住院医生站) Wangjl 2019-01-25 -->
		<!--<module id="WARYZB" name="医嘱本打印(医生角色)" type="1" script="phis.application.war.script.WardDoctorPrintDocModule">
			<properties>
				<p name="serviceId">wardDoctorPrintService</p>
				<p name="serviceAction">saveWardDoctorPrint</p>
				<p name="querySFTDAction">querySFTD</p>
				<p name="zdhListRef">phis.application.war.WAR/WAR/WAR280203</p>
				<p name="zdhListRef_ls">phis.application.war.WAR/WAR/WAR280204</p>
			</properties>
			<action id="cqyz"  name="长期医嘱" ref="phis.application.war.WAR/WAR/WAR280201" />
			<action id="lsyz"  name="临时医嘱" ref="phis.application.war.WAR/WAR/WAR280202" />	
		</module>-->
	</catagory>
</application>