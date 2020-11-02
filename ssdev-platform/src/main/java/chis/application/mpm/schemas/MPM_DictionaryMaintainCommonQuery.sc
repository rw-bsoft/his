<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_DictionaryMaintainCommonQuery" tableName="MPM_DictionaryMaintain" alias="数值值域维护">
	<item id="dicId" alias="编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="fieldId" alias="字段名" type="string" length="16" display="0"/>
	<item id="keys" alias="值" type="string" length="10" queryable="true" />
	<item id="text" alias="值含义" type="string" length="100" queryable="true"/>
	<item id="score" alias="分数" type="int" length="5"/>
	<item id="isAnswer" alias="是否答案" type="string" colspan="2" not-null="1">
		<dic>
			<item key="1" text="答案"/>
			<item key="2" text="非答案"/>
		</dic>
	</item>
	<item id="inputUser" alias="录入医生" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user06" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="date" update="false"
		defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.date']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
</entry>