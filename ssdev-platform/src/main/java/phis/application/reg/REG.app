<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.reg.REG" name="挂号管理">
	<catagory id="REG" name="挂号管理">
		<module id="REG30" name="挂号预检"
			script="phis.application.reg.script.RegistrationPreflightManageModule">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHKS_MANAGE</p>
			</properties>
		</module>
		<module id="REG31" name="挂号预检列表" type="1" 
			script="phis.application.reg.script.RegistrationPreflightList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHYJ</p>
			</properties>
		</module>
		<module id="TEST" name="测试" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHKS</p>
			</properties>
		</module>
		<module id="REG01" name="挂号管理"
			script="phis.application.reg.script.RegistrationManageModule">
			<properties>
				<p name="regForm">phis.application.reg.REG/REG/REG0101</p>
				<p name="regKsList">phis.application.reg.REG/REG/REG0102</p>
				<p name="regYsList">phis.application.reg.REG/REG/REG0103</p>
				<p name="regMpiForm">phis.application.reg.REG/REG/REG02</p>
			</properties>
		</module>
		<module id="REG0101" name="挂号信息FORM" type="1"
			script="phis.application.reg.script.RegistrationManageForm">
			<properties>
				<p name="colCount">35</p>
				<p name="entryName">phis.application.reg.schemas.MS_GHMX_MANAGE</p>
				<p name="reCallIn">phis.application.reg.REG/REG/REG0105</p>
				<p name="refTurnDept">phis.application.reg.REG/REG/REG0107</p>
				<p name="refJsForm">phis.application.reg.REG/REG/REG0104</p>
				<p name="retireForm">phis.application.reg.REG/REG/REG0106</p>
			</properties>
			<action id="appointment" name="预约"  iconCls="common_add"/>
			<action id="create" name="新增" />
			<action id="new" name="重置" iconCls="page_refresh" />
			<action id="callIn" name="调入" iconCls="ransferred_all" />
			<action id="save" name="完成" iconCls="page_save" />
			<action id="retire" name="退号" iconCls="page_delete" />
			<action id="turnDept" name="转科" iconCls="arrow_switch" />
			<action id="ybdk" name="医保读卡" iconCls="money"/>
			<action id="nhdk" name="农合读卡" iconCls="money_add"/>
			<action id="njjb" name="南京金保" iconCls="money" />
			<action id="printSet" name="打印设置" iconCls="print"/>
			<action id="jkkdk" name="健康卡" iconCls="ransferred_all" />
			<!--<action id="getNo" name="取号"  iconCls="common_add"/>-->
			<action id="yycl" name="预约处理" iconCls="clock_go" />
		</module>
		<module id="REG0102" name="挂号科室LIST" type="1"
			script="phis.application.reg.script.RegistrationDepartmentList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHKS_MANAGE</p>
			</properties>
		</module>
		<module id="REG0103" name="挂号医生LIST" type="1"
			script="phis.application.reg.script.RegistrationDoctorManageList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_YSPB_MANAGE</p>
			</properties>
		</module>
		<module id="REG0104" name="挂号结算" type="1"
			script="phis.application.reg.script.RegisteredSettlementForm">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHJS</p>
			</properties>
		</module>
		<module id="REG08" name="预约挂号" 
			script="phis.application.reg.script.AppointmentManageModule">
			<properties>
				<p name="entryName">chis.application.his.schemas.HIS_AppointmentRecord</p>	
				<p name="serviceId">phis.registeredManagementService</p>
				<p name="serviceAction">queryAppointmentRecord</p>
				<p name="saveAction">saveAppointmentRecord</p>
			</properties>
		</module>
		<module id="REG09" name="分时预约列表" type="1" script="phis.application.reg.script.FsyyManageList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_YYGHHY_LIST</p>	
				<p name="serviceId">phis.registeredManagementService</p>
				<p name="listServiceId">registeredManagementService</p>
				<p name="queryActionId">getFyssList</p>
			</properties>
			<action id="tranNh" name="转农合" iconCls="group_go"/>
			<action id="tranYb" name="转医保" iconCls="group_go"/>
			<action id="retreat" name="退号" iconCls="close"/>
		</module>
		<module id="REG0901" name="取号界面" type="1" script="phis.application.reg.script.FsyyManageForm">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_YYGHHY_FORM</p>	
			</properties>
		</module>
		<module id="REG0105" name="预约病人管理" type="1"
			script="phis.application.reg.script.AnAppointmentModule">
			<properties>
				<p name="refAnAppointmentForm">phis.application.reg.REG/REG/REG010501</p>
				<p name="refAnAppointmentDetailsList">phis.application.reg.REG/REG/REG010502</p>
			</properties>
		</module>
		<module id="REG010501" name="预约病人form" type="1"
			script="phis.application.reg.script.AnAppointmentForm">
			<properties>
				<p name="colCount">4</p>
				<p name="entryName">phis.application.reg.schemas.MS_YYGL</p>
			</properties>
		</module>
		<module id="REG010502" name="预约病人List" type="1"
			script="phis.application.reg.script.AnAppointmentList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_YYGL_MX</p>
				<p name="disablePagingTbr">1</p>
				<p name="showButtonOnTop">0</p>
				<p name="autoLoadData">0</p>
			</properties>
			<action id="commit" name="确认" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="REG0106" name="退号处理" type="1"
			script="phis.application.reg.script.RegisteredRetreatSignalForm">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHJS</p>
				<p name="thbrList">phis.application.reg.REG/REG/REG010601</p>
			</properties>
		</module>
		<module id="REG010601" name="挂号单选择" type="1"
			script="phis.application.reg.script.RegisteredPrescriptionList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHMX_GHD</p>
				<p name="height">300</p>
				<p name="width">515</p>
				<p name="modal">true</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="REG0107" name="转科处理" type="1"
			script="phis.application.reg.script.RegistrationDeptForm">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHMX_TD</p>
				<p name="serviceId">registeredManagementTurnDeptService</p>
				<p name="serviceAction">getParameter</p>
				<p name="checkAction">checkKSFY</p>
				<p name="cmmmitAction">commitTurnDept</p>
				<p name="refRegisteredDeptGHList">phis.application.reg.REG/REG/REG010701</p>
			</properties>
		</module>

		<module id="REG010701" name="挂号单选择" type="1"
			script="phis.application.reg.script.RegisteredDeptGHList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHMX_TDMX</p>
				<p name="height">300</p>
				<p name="width">515</p>
				<p name="modal">true</p>
				<p name="serviceId">registeredManagementTurnDeptService</p>
				<p name="serviceAction">queryTurnDept</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		
		<module id="REG0108" name="农合挂号" type="1" script="phis.application.reg.script.RegistrationNhdjList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.NHDJ_BRDA</p>
				<p name="initCnd">['eq',['$','a.SBHM'],["s","1"]]</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="readcard" name="农合卡" iconCls="start_stm" />
			<action id="readsmcard" name="市民卡" iconCls="start_stm" />
			<action id="save" name="确定" iconCls="save" />
			<action id="readcard_hh" name="农合卡(火狐)" iconCls="start_stm" />
		</module>
		<module id="REG0109" name="金保人员信息" type="1" script="phis.application.njjb.script.Njjbmessageform">
			<action id="save" name="确定" iconCls="save" />
		</module>
		<module id="REG0110" name="金保读卡" type="1" script="phis.application.njjb.script.Njjbmessagenomalform">
			<action id="save" name="确定" iconCls="save" />
		</module>
		<module id="REG0111" name="预约转金保" type="1" script="phis.application.njjb.script.TranToNjjbForm">
			<action id="save" name="确定" iconCls="save" />
		</module>
		<module id="REG07" name="挂号查询"
			script="phis.application.reg.script.RegisteredQueriesModule">
			<properties>
				<p name="refList">phis.application.reg.REG/REG/REG0701</p>
			</properties>
		</module>
		<module id="REG0701" name="挂号查询List" type="1"
			script="phis.application.reg.script.RegisteredQueriesList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHCX</p>
			</properties>
		</module>
		<module id="REG02" name="档案管理" type="1"
			script="phis.application.reg.script.RegistrationArchivesModule">
			<properties>
				<p name="winState" type="jo">{pos:[0,0]}</p>
				<p name="serviceId">registrationService</p>
				<p name="saveServiceAction">saveRegistrationInfo</p>
			</properties>
			<action id="basetab" viewType="form" name="基本信息"
				ref="phis.application.reg.REG/REG/REG0201" />
			<action id="addressestab" viewType="list" name="其它联系地址"
				ref="phis.application.reg.REG/REG/REG0202" />
			<action id="phonestab" viewType="list" name="其它联系电话"
				ref="phis.application.reg.REG/REG/REG0203" />
		</module>
		<module id="REG0201" name="基本信息" type="1"
			script="app.biz.BizTableFormView">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MPI_DemographicInfo</p>
			</properties>
		</module>
		<module id="REG0202" name="其它联系地址" type="1" script="app.biz.BizEditorList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MPI_Address</p>
				<p name="autoLoadData">0</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="REG0203" name="其它联系电话" type="1"
			script="phis.script.EditorList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MPI_Phone</p>
				<p name="autoLoadData">0</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="REG03" name="医生排班维护"
			script="phis.application.reg.script.RegistrationDoctorPlanModule">
			<properties>
				<p name="activateId">0</p>
				<p name="refList">phis.application.reg.REG/REG/REG0301</p>
				<p name="refYsList">phis.application.reg.REG/REG/REG0302</p>
			</properties>
			<action id="sunday" name="星期天" group="week" iconCls="sunday" />
			<action id="monday" name="星期一" group="week" iconCls="monday" />
			<action id="tuesday" name="星期二" group="week" iconCls="tuesday" />
			<action id="wednesday" name="星期三" group="week" iconCls="wednesday" />
			<action id="thursday" name="星期四" group="week" iconCls="thursday" />
			<action id="friday" name="星期五" group="week" iconCls="friday" />
			<action id="saturday" name="星期六" group="week" iconCls="saturday" />
			<!--<action id="morning" name="上午" group="date" iconCls="am" /> -->
			<!--<action id="afternoon" name="下午" group="date" iconCls="pm" /> -->
		</module>
		<module id="REG0301" name="医生排班LIST" type="1"
			script="phis.application.reg.script.RegistrationDoctorPlanList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_YSPB</p>
				<p name="serviceId">registrationDoctorPlanService</p>
				<p name="serviceActionSave">saveRegistrationDoctorPlan</p>
				<p name="removeAction">removeRegistrationDoctorPlan</p>
				<p name="autoLoadData">0</p>
				<p name="disablePagingTbr">1</p>
				<p name="group">ZBLB</p>
				<p name="showButtonOnTop">true</p>
				<p name="removeByFiled">YSDM_text</p>
			</properties>
			<action id="save" name="保存" />
			<action id="remove" name="删除" />
			<action id="morning" name="上午" iconCls="am" />
			<action id="afternoon" name="下午" iconCls="pm" />
		</module>
		<module id="REG0302" name="医生LIST" type="1"
			script="phis.application.reg.script.RegistrationDoctorList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.GY_YGDM_REG</p>
				<p name="serviceId">officeDataService</p>
				<p name="serviceAction">getOfficeData</p>
			</properties>
		</module>
		<module id="REG04" name="科室排班维护"
			script="phis.application.reg.script.DepartmentSchedulingModule">
			<properties>
				<p name="refDepartmentSchedulingList">phis.application.reg.REG/REG/REG0401</p>
				<p name="serviceSave">departmentSchedulingService</p>
				<p name="serviceActionSave">saveDepartmentScheduling</p>
			</properties>
		</module>
		<module id="REG0401" name="科室排班LIST" type="1"
			script="phis.script.EditorList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_KSPB</p>
			</properties>
		</module>
		<module id="REG05" name="挂号科室维护"
			script="phis.application.reg.script.RegistrationDepartmentConfigList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_GHKS</p>
				<p name="removeByFiled">KSMC</p>
				<p name="createCls">phis.application.reg.script.RegistrationDepartmentForm</p>
				<p name="updateCls">phis.application.reg.script.RegistrationDepartmentForm</p>
				<p name="initCnd">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="REG06" name="医生科室对照" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.reg.schemas.MS_YSKS</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="REG27" name="基本统计" type="1" script="phis.application.cic.script.ClinicOutpatientStatistics">
			<properties>
				<p name="entryName">phis.report.DctRootChart</p>
			</properties>
		</module>
		<module id="REG28" name="基本统计" type="1" script="phis.application.reg.script.RegistrationAccounting">
			<properties>
				<p name="entryName">phis.report.RegistrationAccountingChart</p>
			</properties>
		</module>
		<module id="REG29" name="挂号分类统计"
			script="phis.application.reg.script.RegCountModule">
			<action id="query" name="统计" />
			<properties>
				<p name="refList">phis.application.reg.REG/REG/REG2901</p>
				<p name="refTXModel">phis.application.reg.REG/REG/REG2902</p>
			</properties>
		</module>
		<module id="REG2901" name="挂号分类" type="1"  script="phis.application.reg.script.RegCountList">
			<properties> 
				<p name="entryName">phis.application.reg.schemas.MS_GHFLTJ</p>
				<p name="disablePagingTbr">1</p>
				<p name="listServiceId">registrationDoctorPlanService</p>
				<p name="queryActionId">regCount</p>
			</properties>
		</module>
		<module id="REG2902" name="挂号分类统计图形" type="1"  script="phis.application.reg.script.ReportChartModule">
		</module>
	</catagory>
</application>