<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.ohr.schemas.MDC_OldPeopleCheckup" alias="老年人体检">
	<item id="checkupId" alias="体检编号" type="string" length="16" not-null="1" pkey="true" fixed="true" generator="assigned" 	hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="随访编号" type="string" length="16"
		hidden="true" />
	<item id="phrId" alias="档案编号" type="string" length="30"
		hidden="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		hidden="true" />
	<item id="indicatorName" alias="项目名称" type="string" width="130"  fixed="true"
		length="100" />
	<item id="indicatorValue" alias="结果值" type="string" length="100"
		width="100" />
	<item id="ifException" alias="是否异常" type="string" width="100" defaultValue="1">
		<dic render="Simple">
			<item key="1" text="正常" />
			<item key="2" text="异常" />
			<item key="3" text="未查" />
		</dic>
	</item>
	<item id="exceptionDesc" alias="异常描述" type="string" length="200" width="285" />
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false" hidden="true"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"  hidden="true"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"  hidden="true"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" hidden="true"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" hidden="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
