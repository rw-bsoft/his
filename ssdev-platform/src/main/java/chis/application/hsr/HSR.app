<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hsr.HSR" name="卫生监督协管"  type="1">
	<catagory id="HSR" name="卫生监督协管">
		<module id="DCHSR01" name="卫生监督协管信息" script="chis.application.hsr.script.HSR_AssistantInformationList"> 
				<properties> 
					<p name="entryName">chis.application.hsr.schemas.HSR_AssistantInformation</p>  
					<p name="dchsr01form">chis.application.hsr.HSR/HSR/DCHSR01_01</p>
					<p name="printTitle">卫生监督协管信息报告登记表</p> 
					<p name="showRowNumber">true</p>  
				</properties>  
				<action id="createAssis" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="remove" name="删除"/>  
				<action id="print"  name="打印"/> 
			</module>
			<module id="DCHSR01_01" name="卫生监督协管信息" type="1" script="chis.application.hsr.script.HSR_AssistantInformationForm">
				<properties>
					<p name="entryName">chis.application.hsr.schemas.HSR_AssistantInformation</p>
				</properties>
				<action id="save" name="确定" group="create||update"/> 
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>
            
			<module id="DCHSR02" name="卫生监督协管巡查" script="chis.application.hsr.script.HSR_AssistingRegistrationList"> 
				<properties> 
					<p name="entryName">chis.application.hsr.schemas.HSR_AssistingRegistration</p>  
					<p name="dchsr02form">chis.application.hsr.HSR/HSR/DCHSR02_01</p> 
					<p name="printTitle">卫生监督协管巡查登记表</p> 
					<p name="showRowNumber">true</p> 
				</properties>  
				<action id="createAssis" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="remove" name="删除"/>  
				<action id="print"  name="打印"/> 
			</module> 
			<module id="DCHSR02_01" name="卫生监督协管巡查" type="1" script="chis.application.hsr.script.HSR_AssistingRegistrationForm">
				<properties>
					<p name="entryName">chis.application.hsr.schemas.HSR_AssistingRegistration</p>
				</properties>
				<action id="save" name="确定" group="create||update"/> 
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>
		
	</catagory>
</application>