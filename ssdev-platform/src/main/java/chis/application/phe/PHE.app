<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.phe.PHE" name="突发公共卫生事件"  type="1">
	<catagory id="PHE" name="突发公共卫生事件">
		<module id="DCPHEList01" name="突发公共卫生事件" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.phe.schemas.PHE_RelevantInformation</p> 
					<p name="areaGridField">detailAdreess</p> 
					<p name="manageUnitField">reportUnit</p>  
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.phe.PHE/PHE/DCPHE01"/> 
			</module>  
			<module id="DCPHE01" type="1" name="突发公共卫生事件" script="chis.application.phe.script.PHE_RelevantInformationList"> 
				<properties> 
					<p name="entryName">chis.application.phe.schemas.PHE_RelevantInformation</p> 
					<p name="dcphe01form">chis.application.phe.PHE/PHE/DCPHE01_01</p>
				</properties>   
				<action id="createRelevant" iconCls="create" name="新建"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="remove" name="删除"/>  
				<action id="print"  name="打印"/> 
			</module>
			<module id="DCPHE01_01" name="突发公共卫生事件相关信息" type="1" script="chis.application.phe.script.PHE_RelevantInformationForm">
				<properties>
					<p name="entryName">chis.application.phe.schemas.PHE_RelevantInformation</p>
					<p name="isAutoScroll">true</p>
					<!--
						<p name="loadServiceId">chis.stillbirthReportCardService</p>
						<p name="loadAction">getStillbirthReportByPkey</p>
						<p name="saveServiceId">chis.stillbirthReportCardService</p>
						<p name="saveAction">saveStillbirthReport</p>
						-->
				</properties>
				<action id="save" name="确定" group="create||update"/> 
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
				<action id="print"  name="打印"/> 
			</module>
			<module id="DCPHEList02" name="环境卫生事件" script="chis.script.CombinedManageUnitDocList"> 
				<properties> 
					<p name="entryName">chis.application.phe.schemas.PHE_EnvironmentalEvent</p> 
					<p name="manageUnitField">reportUnit</p>  
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.phe.PHE/PHE/DCPHE02"/> 
			</module>  
			<module id="DCPHE02" type="1" name="环境卫生事件" script="chis.application.phe.script.PHE_EnvironmentalEventList"> 
				<properties> 
					<p name="manageUnitField">reportUnit</p>  
					<p name="entryName">chis.application.phe.schemas.PHE_EnvironmentalEvent</p>  
					<p name="dcphe02form">chis.application.phe.PHE/PHE/DCPHE02_01</p> 
				</properties>  
				<action id="createRelevant" name="新建" iconCls="create"/>  
				<action id="modify" name="查看"  iconCls="update"/>  
				<action id="remove" name="删除"/>  
				<action id="print"  name="打印"/> 
			</module>
			<module id="DCPHE02_01" name="环境卫生事件" type="1" script="chis.application.phe.script.PHE_EnvironmentalEventForm">
				<properties>
					<p name="entryName">chis.application.phe.schemas.PHE_EnvironmentalEvent</p>
				</properties>
				<action id="save" name="确定" group="create||update"/> 
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
				<action id="print"  name="打印"/> 
			</module>
	</catagory>
</application>