<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.gdr.GDR" name="群宴管理"  type="1">
	<catagory id="GDR" name="群宴管理"> 
			<module id="Q00" name="群宴登记" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.gdr.schemas.GDR_GroupDinnerRecord</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.gdr.GDR/GDR/Q01"/> 
			</module>  
			<module id="Q01" name="群宴登记" script="chis.application.gdr.script.GroupDinnerList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.gdr.schemas.GDR_GroupDinnerRecord</p>  
					<p name="navField">RegionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="createCls">chis.application.gdr.script.GroupDinnerRecordModule</p>  
					<p name="updateCls">chis.application.gdr.script.GroupDinnerRecordModule</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="Q01-1" name="群宴登记form" type="1"> 
				<action id="save" name="确定"/> 
			</module>  
			<module id="Q01-2" name="群宴首次指导" type="1"> 
				<action id="save" name="确定"/> 
			</module>  
			<module id="Q01-3" name="群宴第二次指导" type="1"> 
				<action id="save" name="确定"/> 
			</module>  
			<module id="Q01-4" name="回访信息" type="1"> 
				<action id="save" name="确定"/> 
			</module> 
		</catagory> 
</application>