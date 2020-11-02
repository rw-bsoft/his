<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.demo.DEMO" name="演示模块" type="1">
	<catagory id="DMCat" name="开发演示">
	<!--  
		<module id="DMCat_01" name="我的一个演示" script="chis.script.BizSimpleListView">
			<properties> 
				<p name="entryName">chis.application.demo.schemas.PersonInfo</p>
			</properties>
			<action id="create" name="新建" iconCls="create"/>
			<action id="update" name="查看" iconCls="update"/>
			<action id="remove" name="删除"/>
		</module>
	-->
		<module id="DMCat_01" name="我的一个演示" script="chis.application.demo.script.PersonInfoList">
			<properties> 
				<p name="entryName">chis.application.demo.schemas.PersonInfo</p>
				<p name="ref">chis.application.demo.DEMO/DMCat/DMCat_0101</p>
			</properties>
			<action id="add" name="新建" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="remove" name="删除"/>
		</module>
		<module id="DMCat_0101" title="演示表单" script="chis.application.demo.script.PersonInfoForm" type="1">
			<properties>
				<p name="entryName">chis.application.demo.schemas.PersonInfo</p>
				<p name="saveServiceId">chis.personInfoService</p>
				<p name="saveAction">saveDemo</p>
				<p name="loadServiceId">chis.personInfoService</p>
				<p name="loadAction">getPersonInfo</p>
			</properties>
			<action id="save" name="确定" group="create||update"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>
		</module>

	</catagory>
</application>