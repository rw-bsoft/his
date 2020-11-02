<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.cvm.CVM" name="来电来访管理" type="0">
	<catagory id="CVM" name="来电来访管理">
		<module id="CVM01" name="来电来访管理" script="gp.application.cvm.script.CallVisitManageList">
			<properties>
				<p name="entryName">gp.application.cvm.schemas.CVM_CallVisitManage</p>
			</properties>
			<action id="createDoc" name="新建" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="writeOff" name="注销" iconCls="remove"/>
		</module>
		<module id="CVM01_1" type="1" name="来电来访管理" script="gp.application.cvm.script.CallVisitManageForm">
			<properties>
				<p name="entryName">gp.application.cvm.schemas.CVM_CallVisitManage</p>
			</properties>
			<action id="save" name="保存"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>
		<module id="CVM1_1" name="预约服务管理" type="1" script="gp.application.cvm.script.CVMAppointmentManagementModule"> 
			<properties> 
				<p name="refTopForm">gp.application.cvm.CVM/CVM/CVM1_1_1</p>
				<p name="refView">gp.application.cvm.CVM/CVM/CVM1_1_2</p>    
			</properties>  
			<action id="save" name="保存"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module> 
		<module id="CVM1_1_1" name="预约服务管理" type="1" script="gp.application.cvm.script.CVMAppointmentManagementTopForm"> 
			<properties> 
				<p name="entryName">gp.application.fd.schemas.GP_YYGL_FORM</p>  
			</properties>  
		</module>  
		<module id="CVM1_1_2" name="预约服务管理" type="1" script="gp.application.cvm.script.CVMAppointmentManagementCalendar"> 
			<properties> 
			</properties>  
		</module> 
	</catagory>
</application>