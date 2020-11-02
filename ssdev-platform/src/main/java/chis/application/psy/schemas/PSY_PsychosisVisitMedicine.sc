<?xml version="1.0" encoding="UTF-8"?>

<entry alias="精神病随访药品">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<!--
		<item id="phrId" alias="档案编号" type="string" length="30" display="0" />
		-->
	<item id="visitId" alias="随访标识" type="string" length="16"
		display="0" />
	<item id="medicineId" alias="药物ID" type="string" length="16"
		display="0" />
	<item id="medicineName" alias="药物名称" type="string" length="50" width="150" not-null="1" colspan="2" mode="remote"/>
	<item id="medicineFrequency" alias="次数" type="string"
		not-null="1" length="20" />
	<item id="days" alias="天数" type="int" not-null="1" length="20" defaultValue="1"/>
	<item id="medicineDosage" alias="每次" type="double" not-null="1"
		length="10" width="150"/>
	<item id="medicineUnit" alias="剂量单位" type="string"
		length="6" width="150">
	</item>
	<item id="createUnit" alias="录入单位" type="string" length="20" fixed="true" 
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" 
			onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="createUser" alias="录入人员" type="string" length="20" fixed="true" 
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" 
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" 
			onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
