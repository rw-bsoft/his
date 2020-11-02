<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.fd.FAMILYDOCTOR"
	name="家庭医生">
	<catagory id="FD"  name="预约服务">
		<module id="FD1" name="预约服务管理" script="gp.application.fd.script.AppointmentManagementList"> 
			<properties> 
				<p name="entryName">gp.application.fd.schemas.GP_YYGL</p>  
				<p name="updateCls">gp.application.fd.FAMILYDOCTOR/FD/FD1_1</p>  
				<p name="createCls">gp.application.fd.FAMILYDOCTOR/FD/FD1_1</p>  
			</properties>  
			<action id="refreshWin" name="查询" iconCls="query" />
			<action id="createInfo" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="treat" name="执行" iconCls="common_treat"/>
			<action id="remove" name="取消"/> 
		</module>  
		<module id="FD1_1" name="预约服务管理" type="1" script="gp.application.fd.script.AppointmentManagementModule"> 
			<properties> 
				<p name="refTopForm">gp.application.fd.FAMILYDOCTOR/FD/FD1_1_1</p>
				<p name="refView">gp.application.fd.FAMILYDOCTOR/FD/FD1_1_2</p>    
			</properties>  
			<action id="save" name="保存"/>  
			<action id="cancel" name="关闭" iconCls="common_cancel"/> 
		</module> 
		<module id="FD1_1_1" name="预约服务管理" type="1" script="gp.application.fd.script.AppointmentManagementTopForm"> 
			<properties> 
				<p name="entryName">gp.application.fd.schemas.GP_YYGL_FORM</p>  
			</properties>  
		</module>  
		<module id="FD1_1_2" name="预约服务管理" type="1" script="gp.application.fd.script.AppointmentManagementCalendar"> 
			<properties> 
			</properties>  
		</module> 
		<module id="FD2" name="预约服务" type="1" script="gp.application.fd.script.AppointmentQueryList"> 
			<properties> 
				<p name="entryName">gp.application.fd.schemas.GP_YYGL</p>  
				<p name="updateCls">gp.application.fd.FAMILYDOCTOR/FD/FD1_1</p>  
				<p name="createCls">gp.application.fd.FAMILYDOCTOR/FD/FD1_1</p>  
			</properties>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="treat" name="执行" iconCls="common_treat"/>
			<action id="remove" name="取消预约" iconCls="common_remove"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>  
		</module>
		<module id="FD3" name="就诊历史记录" type="1" script="gp.application.fd.script.ClinicHistoryList">
			<properties>
				<p name="entryName">gp.application.fd.schemas.YS_MZ_JZLS</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/> 
		</module>
		<module id="FD4" name="就诊历史记录" type="1" script="gp.application.fd.script.ClinicHistorySimpleList">
			<properties>
				<p name="entryName">gp.application.fd.schemas.YS_MZ_JZLS_RS</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/> 
		</module>
		<module id="FD5" name="健康检查记录" type="1" script="gp.application.fd.script.HealthCheckList">
			<properties>
				<p name="entryName">gp.application.fd.schemas.HC_HealthCheck</p>
			</properties>
			<action id="modify" name="查看" iconCls="query"/>  
			<action id="cancel" name="关闭" iconCls="common_cancel"/> 
		</module>
		<module id="FD6" name="来电来访管理" type="1" script="gp.application.fd.script.CallVisitManageList">
			<properties>
				<p name="entryName">gp.application.cvm.schemas.CVM_CallVisitManageYygl</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="writeOff" name="注销" iconCls="remove"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/> 
		</module>
		<module id="FD6_1" type="1" name="来电来访管理" script="gp.application.fd.script.CallVisitManageForm">
			<properties>
				<p name="entryName">gp.application.cvm.schemas.CVM_CallVisitManage</p>
			</properties>
			<action id="save" name="保存"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>
	</catagory>
	<catagory id="FHR" ref="chis.application.fhr.FHR/FHR" />
	<catagory id="RVC" ref="chis.application.rvc.RVC/RVC" />
</application>

