<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.per.schemas.PER_Combo" alias="体检套餐">
	<item id="id" pkey="true" alias="套餐内码" type="string" not-null="1" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="name" alias="套餐名称" type="string" length="100" not-null="1" width="180"/>
	<item id="manaUnitId" alias="管理中心" type="string" length="20"  not-null="1" width="350" anchor="100%" defaultValue="['if',['ge',['len',['$','%user.manageUnit.id']],['i',9]],['substring',['$','%user.manageUnit.id'],0,9],['s','']]">
		<dic id="chis.@manageUnit" includeParentMinLen="6" rootVisible="true" render="Tree" parentKey="%user.manageUnit.id"   filter="['le',['len',['$','item.key']],['i',9]]"/>
	</item>
	<item id="comboName" alias="套餐类型" type="string" not-null="1" width="200">
		<dic id="chis.dictionary.perComboType"/>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20"
		width="180" fixed="true" update="false" display="1"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" display="1"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" display="1"
		fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<!--  
		<item id="projects" alias="项目包含" type="string" length="1000" width="520" colspan="3">
			<dic  id="projects" render="TreeCheck" checkModel="cascade" selectValueModel="leaf" maxHeight="400"/>
		</item>
		-->
	
</entry>