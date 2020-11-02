<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_MasterplateData" alias="字段数据表（主表）">
	<item id="recordId" alias="编号" pkey="true" type="string"  width="160"
		length="16" not-null="1" fixed="true"  hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="masterplateId" alias="模版编号" type="string" length="16" display="0" />
	<item id="masterplateName" alias="模版名称" type="string" width="200" length="100" display="0" />
	<item id="empiId" alias="empiId" type="string" length="32" display="0" />
	<item id="inputDate" alias="录入日期" type="date" queryable="true" width="100"
		update="false" defaultValue="%server.date.date" fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
	<item id="inputUser" alias="录入医生" type="string" length="20" width="160"
		update="false" queryable="true" defaultValue="%user.userId" fixed="true"
		display="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" 
			parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20"
		update="false" width="160" defaultValue="%user.manageUnit.id" fixed="true"
		colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" width="100" type="date" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1" width="160">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="160" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
