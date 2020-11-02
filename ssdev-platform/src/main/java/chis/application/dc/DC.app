<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.dc.DC" name="狂犬病管理"  type="1">
	<catagory id="DC" name="狂犬病管理">
			<module id="DC01" name="狂犬病" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.dc.schemas.DC_RabiesRecord</p>  
					<p name="manageUnitField">a.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.dc.DC/DC/DC01_1"/> 
			</module>  
			<module id="DC01_1" name="狂犬病管理列表" script="chis.application.dc.script.RabiesRecordListView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.dc.schemas.DC_RabiesRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>
				</properties>  
				<action id="createByEmpi" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="writeOff" name="注销" iconCls="common_writeOff" ref="chis.application.dc.DC/DC/DC01_1_1"/> 
			</module>  
			<module id="DC01_1_1" name="狂犬病档案注销表单" type="1" script="chis.application.dc.script.RabiesRecordLogoutForm"> 
				<properties> 
					<p name="entryName">chis.application.hr.schemas.EHR_MultitermWriteOff</p>
				</properties>  
				<action id="save" name="确定" group="update"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="DC01_2" name="狂犬病档案管理" type="1" script="chis.application.dc.script.RabiesRecordModule"> 
				<properties> 
					<p name="entryName">chis.application.dc.schemas.DC_RabiesRecord</p>  
					<p name="serviceId"/> 
				</properties>  
				<action id="RabiesList" name="狂犬病档案列表" ref="chis.application.dc.DC/DC/DC01_2_1"/>  
				<action id="Rabies" name="狂犬病档案" ref="chis.application.dc.DC/DC/DC01_2_2"/> 
			</module>  
			<module id="DC01_2_1" name="狂犬病档案列表" type="1" script="chis.application.dc.script.RabiesRecordList"/>  
			<module id="DC01_2_2" name="狂犬病档案" type="1" script="chis.application.dc.script.RabiesTabModule"> 
				<action id="RabiesForm" name="狂犬病档案表单" ref="chis.application.dc.DC/DC/DC01_2_2_1"/>  
				<action id="VaccinationList" name="接种列表" ref="chis.application.dc.DC/DC/DC01_2_2_2"/> 
			</module>  
			<module id="DC01_2_2_1" name="狂犬病档案表单" type="1" script="chis.application.dc.script.RabiesRecordForm"> 
				<properties> 
					<p name="entryName">chis.application.dc.schemas.DC_RabiesRecord</p> 
					<p name="isAutoScroll">true</p>
				</properties>  
				<action id="save" name="确定" group="create||update"/>  
				<action id="close" name="结案" group="update"/>  
				<action id="add" name="新增" group="create"/> 
			</module>  
			<module id="DC01_2_2_2" name="接种列表" type="1" script="chis.application.dc.script.VaccinationList"> 
				<properties> 
					<p name="entryName">chis.application.dc.schemas.DC_Vaccination</p>  
					<p name="createCls">chis.application.dc.script.VaccinationForm</p>  
					<p name="updateCls">chis.application.dc.script.VaccinationForm</p> 
				</properties>  
				<action id="create" name="增加" iconCls="create" group="create"/>  
				<action id="update" name="查看" group="update"/>  
				<action id="remove" name="删除" group="update"/> 
			</module> 
	</catagory>
</application>