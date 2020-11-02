<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.conf.CONF" name="系统配置管理"  type="1">
	<catagory id="CONF" name="系统配置管理">
		<module id="SC01" name="系统配置管理" script="chis.application.conf.script.SystemConfigModule" />
		<module id="SC01_1" name="公共设置" type="1"
			script="chis.application.conf.script.pub.SystemCommonManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.SYS_CommonConfig</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="SC01_12" name="机构类型维护" type="1"
			script="chis.application.conf.script.UnitTypeForm" />
		<module id="SC01_2" name="接口设置" type="1"
			script="chis.application.conf.script.pub.InterfaceManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.SYS_InterfaceConfig</p>
			</properties>
			<action id="save" name="确定" />
			<action id="test" name="测试" iconCls="test" />
		</module>
		<module id="SC01_21" name="管理方式设置" type="1"
			script="chis.application.conf.script.pub.SystemManageTypeForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.SYS_ManageTypeConfig</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="SC01_4" name="外部服务接口设置" type="1"
			script="chis.application.conf.script.pub.ZookeeperManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.SYS_ZookeeperConfig</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="SC01_3" name="计划类型设置管理" type="1"
			script="chis.application.conf.script.pub.PlanTypeManageModule">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_PlanType</p>
				<p name="saveServiceId">chis.planTypeManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="计划类型设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_302" />
			<action id="计划类型设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_301" />
		</module>
		<module id="SC01_301" name="计划类型设置管理列表" type="1"
			script="chis.application.conf.script.pub.PlanTypeList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_PlanType</p>
			</properties>
			<action id="add" name="增加" iconCls="create" notReadOnly="true" />
			<action id="save" name="保存" iconCls="update" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_302" name="计划类型设置管理表单" type="1"
			script="chis.application.conf.script.pub.PlanTypeForm">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_PlanType</p>
			</properties>
		</module>
		<module id="SC01_5" name="老年人模块参数设置管理" type="1"
			script="chis.application.conf.script.mdc.OldPeopleConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_OldPeopleFormConfig</p>
				<p name="saveServiceId">chis.oldPeopleConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="老年人模块设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_502" />
			<action id="老年人模块设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_501" />
		</module>
		<module id="SC01_501" name="老年人模块列表" type="1"
			script="chis.application.conf.script.mdc.OldPeopleList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_OldPeopleListConfig</p>
				<p name="listServiceId">chis.oldPeopleConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_502" name="老年人模块表单" type="1"
			script="chis.application.conf.script.mdc.OldPeopleForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_OldPeopleFormConfig</p>
				<p name="loadServiceId">chis.oldPeopleConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
			<action id="save" name="确定" notReadOnly="true" />
		</module>
		<module id="SC01_6" name="高血压模块参数设置管理" type="1"
			script="chis.application.conf.script.mdc.HypertensionConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_HypertensionConfig</p>
				<p name="saveServiceId">chis.hypertensionConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="高血压档案参数设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_600" />
			<action id="高血压高危参数设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_609" />
		</module>
		<module id="SC01_600" name="高血压档案参数设置管理" type="1"
			script="chis.application.conf.script.mdc.HypertensionVisitConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_HypertensionConfig</p>
				<p name="saveServiceId">chis.hypertensionConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="高血压档案设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_602" />
			<action id="高血压档案设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_601" />
		</module>
		<module id="SC01_601" name="高血压档案参数设置管理列表" type="1"
			script="chis.application.conf.script.mdc.HypertensionConfigManageList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_EstimateDictionary</p>
			</properties>
		</module>
		<module id="SC01_602" name="高血压档案参数设置管理表单" type="1"
			script="chis.application.conf.script.mdc.HypertensionConfigManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_HypertensionConfig</p>
				<p name="loadServiceId">chis.hypertensionConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
		</module>
		<module id="SC01_603" name="高血压高危人群模块参数设置管理表单" type="1"
			script="chis.application.conf.script.mdc.HypertensionRiskConfigManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_HypertensionRiskConfig</p>
				<p name="saveServiceId">chis.hypertensionRiskConfigManageService</p>
				<p name="saveAction">saveConfig</p>
				<p name="loadServiceId">chis.hypertensionRiskConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
		</module>
		<module id="SC01_604" name="高血压评估参数设置" type="1"
			script="chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_HypertensionConfig</p>
				<p name="saveServiceId">chis.hypertensionConfigManageService</p>
				<p name="saveAction">saveAssessmentConfig</p>
			</properties>
			<action id="高血压评估参数设置表单" ref="chis.application.conf.CONF/CONF/SC01_605" />
			<action id="高血压评估参数设置列表" ref="chis.application.conf.CONF/CONF/SC01_606" />
		</module>
		<module id="SC01_605" name="高血压评估参数设置表单" type="1"
			script="chis.application.conf.script.mdc.HypertensionAssessmentConfigManageForm">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionAssessParamete</p>
				<p name="loadServiceId">chis.hypertensionConfigManageService</p>
				<p name="loadAction">queryAssessmentFormConfig</p>
			</properties>
		</module>
		<module id="SC01_606" name="高血压评估参数设置列表" type="1"
			script="chis.application.conf.script.mdc.HypertensionAssessmentConfigManageList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionBPControl</p>
			</properties>
		</module>
		<module id="SC01_607" name="高血压分组列表" type="1"
			script="chis.application.conf.script.mdc.HypertensionGroupManageList">
			<properties>
				<p name="entryName">chis.application.hy.schemas.MDC_HypertensionControl</p>
			</properties>
		</module>
		<module id="SC01_608" name="高血压评估参数设置" type="1"
			script="chis.application.conf.script.mdc.HypertensionAssessmentConfigManageModule2">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_HypertensionConfig</p>
				<p name="saveServiceId">chis.hypertensionConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="高血压评估参数设置列表" ref="chis.application.conf.CONF/CONF/SC01_607" />
			<action id="高血压评估参数设置表单" ref="chis.application.conf.CONF/CONF/SC01_604" />
		</module>
		<module id="SC01_609" name="高血压模块参数设置管理" type="1"
			script="chis.application.conf.script.mdc.HypertensionConfigManageModule2">
			<action id="高血压评估参数设置" ref="chis.application.conf.CONF/CONF/SC01_608" />
			<action id="高血压高危参数设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_603" />
		</module>
		<module id="SC01_7" name="糖尿病模块参数设置管理" type="1"
			script="chis.application.conf.script.mdc.DiabetesConfigManageModule">
			<action id="糖尿病模块设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_7_1" />
			<action id="糖尿病模块设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_7_2" />
		</module>
		<module id="SC01_7_1" name="糖尿病档案模块参数设置管理表单" type="1"
			script="chis.application.conf.script.mdc.DiabetesConfigManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_DiabetesConfig</p>
				<p name="saveServiceId">chis.diabetesConfigManageService</p>
				<p name="saveAction">saveConfig</p>
				<p name="loadServiceId">chis.diabetesConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
		</module>
		<module id="SC01_7_2" name="糖尿病高危人群模块参数设置管理表单" type="1"
			script="chis.application.conf.script.mdc.DiabetesRiskConfigManageForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_DiabetesRiskConfig</p>
				<p name="saveServiceId">chis.diabetesRiskConfigManageService</p>
				<p name="saveAction">saveConfig</p>
				<p name="loadServiceId">chis.diabetesRiskConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
		</module>
		<module id="SC01_8" name="儿童模块参数设置管理" type="1"
			script="chis.application.conf.script.cdh.ChildrenConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_ChildrenConfig</p>
				<p name="saveServiceId">chis.childrenConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="儿童模块设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_802" />
			<action id="儿童模块设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_801" />
		</module>
		<module id="SC01_801" name="儿童模块设置管理列表" type="1"
			script="chis.application.conf.script.cdh.ChildrenConfigList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_ChildrenConfigDetail</p>
				<p name="listServiceId">chis.childrenConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_802" name="儿童模块设置管理表单" type="1"
			script="chis.application.conf.script.cdh.ChildrenConfigForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_ChildrenConfig</p>
				<p name="loadServiceId">chis.childrenConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
			<action id="save" name="确定" notReadOnly="true" />
		</module>
		<module id="SC01_09" name="体弱儿模块参数设置管理" type="1"
			script="chis.application.conf.script.cdh.DebilityChildrenConfigModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_DebilityChildrenConfig</p>
				<p name="saveServiceId">chis.debilityChildrenConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="体弱儿模块设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_0902" />
			<action id="体弱儿模块设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_0901" />
		</module>
		<module id="SC01_0901" name="体弱儿模块设置管理列表" type="1"
			script="chis.application.conf.script.cdh.DebilityChildrenConfigList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_DebilityChildrenConfigDetail</p>
				<p name="listServiceId">chis.debilityChildrenConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_0902" name="体弱儿模块设置管理表单" type="1"
			script="chis.application.conf.script.cdh.DebilityChildrenConfigForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_DebilityChildrenConfig</p>
				<p name="loadServiceId">chis.debilityChildrenConfigManageService</p>
				<p name="loadAction">queryFormConfig</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="SC01_10" name="孕妇模块参数设置管理" type="1"
			script="chis.application.conf.script.mhc.PregnantConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_PregnantConfig</p>
				<p name="saveServiceId">chis.pregnantConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="孕妇模块设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_1002" />
			<action id="孕妇模块设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_1001" />
		</module>
		<module id="SC01_1001" name="孕妇模块设置管理列表" type="1"
			script="chis.application.conf.script.mhc.PregnantConfigList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_PregnantConfigDetail</p>
				<p name="listServiceId">chis.pregnantConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_1002" name="孕妇模块设置管理表单" type="1"
			script="chis.application.conf.script.mhc.PregnantConfigForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_PregnantConfig</p>
				<p name="loadServiceId">chis.pregnantConfigManageService</p>
				<p name="loadAction">queryConfig</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="SC01_11" name="精神病模块参数设置管理" type="1"
			script="chis.application.conf.script.psy.PsychosisConfigManageModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_PsychosisConfig</p>
				<p name="saveServiceId">chis.psychosisConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="精神病模块设置管理表单" ref="chis.application.conf.CONF/CONF/SC01_1102" />
			<action id="精神病模块设置管理列表" ref="chis.application.conf.CONF/CONF/SC01_1101" />
		</module>
		<module id="SC01_1101" name="精神病模块设置管理列表" type="1"
			script="chis.application.conf.script.psy.PsychosisConfigList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_PsychosisConfigDetail</p>
				<p name="listServiceId">chis.psychosisConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_1102" name="精神病模块设置管理表单" type="1"
			script="chis.application.conf.script.psy.PsychosisConfigForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_PsychosisConfig</p>
				<p name="loadServiceId">chis.psychosisConfigManageService</p>
				<p name="loadAction">queryFormConfig</p>
			</properties>
			<action id="save" name="确定" notReadOnly="true" />
		</module>
		<module id="SC012" name="离休干部模块参数设置管理" type="1"
			script="chis.application.conf.script.mdc.RetiredVeteranCadresForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_RVCFormConfig</p>
			</properties>
			<action id="save" name="确定" notReadOnly="true" />
		</module>
		<module id="SC01_13" name="肿瘤高危人群随访参数设置" type="1" script="chis.application.conf.script.thr.TumourHighRiskConfigModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_TumourHighRiskConfig</p>
				<p name="saveServiceId">chis.tumourHighRiskConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="thrForm" name="肿瘤高危人群随访参数设置表单" ref="chis.application.conf.CONF/CONF/SC01_1301" />
			<action id="thrList" name="肿瘤高危人群随访参数设置列表" ref="chis.application.conf.CONF/CONF/SC01_1302" />
		</module>
		<module id="SC01_1301" name="肿瘤高危人群随访参数设置表单" type="1" script="chis.application.conf.script.thr.TumourHighRiskConfigForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_TumourHighRiskConfig</p>
				<p name="loadServiceId">chis.tumourHighRiskConfigManageService</p>
				<p name="loadAction">queryFormConfig</p>
			</properties>
			<action id="save" name="确定" notReadOnly="true" />
		</module>
		<module id="SC01_1302" name="肿瘤高危人群随访参数设置列表" type="1" script="chis.application.conf.script.thr.TumourHighRiskConfigList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_TumourHighRiskConfigDetail</p>
				<p name="showRowNumber">true</p>
				<p name="listServiceId">chis.tumourHighRiskConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_14" name="肿瘤患者随访参数设置" type="1" script="chis.application.conf.script.tpv.TumourPatientVisitConfigModule">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_TumourPatientVisitConfig</p>
				<p name="saveServiceId">chis.tumourPatientVisitConfigManageService</p>
				<p name="saveAction">saveConfig</p>
			</properties>
			<action id="thrForm" name="肿瘤患者随访参数设置表单" ref="chis.application.conf.CONF/CONF/SC01_1401" />
			<action id="thrList" name="肿瘤患者随访参数设置列表" ref="chis.application.conf.CONF/CONF/SC01_1402" />
		</module>
		<module id="SC01_1401" name="肿瘤患者随访参数设置表单" type="1" script="chis.application.conf.script.tpv.TumourPatientVisitConfigForm">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_TumourPatientVisitConfig</p>
				<p name="loadServiceId">chis.tumourPatientVisitConfigManageService</p>
				<p name="loadAction">queryFormConfig</p>
			</properties>
			<action id="save" name="确定" notReadOnly="true" />
		</module>
		<module id="SC01_1402" name="肿瘤患者随访参数设置列表" type="1" script="chis.application.conf.script.tpv.TumourPatientVisitConfigList">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_TumourPatientVisitConfigDetail</p>
				<p name="showRowNumber">true</p>
				<p name="listServiceId">chis.tumourPatientVisitConfigManageService</p>
				<p name="listAction">queryListConfig</p>
			</properties>
			<action id="create" name="增加" iconCls="create" />
			<action id="remove" name="删除" />
		</module>
		<module id="SC01_15" name="糖尿病评估标准维护" type="1" script="chis.application.conf.script.mdc.DiabetesAssessmentManage">
			<properties>
				<p name="entryName">chis.application.conf.schemas.ADMIN_DiabetesAssessmentManage</p>
				<p name="saveServiceId">chis.diabetesConfigManageService</p>
				<p name="saveAction">saveAssessConfig</p>
				<p name="loadServiceId">chis.diabetesConfigManageService</p>
				<p name="loadAction">queryAssessConfig</p>
			</properties>
			<action id="save" name="保存" />
		</module>
	</catagory>
</application>