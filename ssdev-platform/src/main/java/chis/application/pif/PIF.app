<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.pif.PIF" name="公告维护"  type="1">
	<catagory id="PIF" name="公告维护">
		<module id="S03" name="公告维护" script="chis.application.pif.script.PublicInfoListView"> 
				<properties> 
					<p name="entryName">chis.application.pif.schemas.PUB_PublicInfo</p> 
				</properties>  
				<action id="createInfo" name="新建" iconCls="create" group="create"/>  
				<action id="modify" name="查看" iconCls="update" group="update"/>  
				<action id="remove" name="删除" group="update"/> 
			</module>  
			<module id="S03_1" name="公告维护表单" type="1" script="chis.application.pif.script.PublicInfoFormView"> 
				<properties> 
					<p name="entryName">chis.application.pif.schemas.PUB_PublicInfo</p> 
				</properties>  
				<action id="save" name="保存"/>  
				<action id="create" name="新建"/> 
			</module>  
	</catagory>
</application>