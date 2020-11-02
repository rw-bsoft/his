<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.per.schemas.PER_ComboDetail" alias="体检套餐明细">
	<item id="id" pkey="true" alias="套餐明细内码" type="string" not-null="1" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="comboId"  alias="套餐编号" type="string" hidden="true"/>
	<item id="projectOfficeId" alias="科室代码" type="string" not-null="1" colspan="3" width="120">
		<dic id="chis.dictionary.projectOffice"  filter="['or',['contains',['$','item.properties.manaUnitId'],['substring',['$','%user.manageUnit.id'],0,9]],['eq',['$','item.properties.manaUnitId'],['$','%server.topUnit']]]" />
	</item>
	<item id="itemId" alias="项目编号" type="string" not-null="1" length="1000" colspan="3" width="620">
		<dic  id="chis.dictionary.projects" render="TreeCheck" checkModel="childCascade" selectValueModel="leaf" maxHeight="400"/>
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
</entry>