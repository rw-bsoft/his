<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_PersonProblem" alias="个人主要问题" sort=" a.recordDate desc" >
	<item id="sickRecordId" pkey="true" alias="记录序号" type="string"
		length="16" not-null="1" fixed="true" hidden="true" generator="assigned" width="160" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiid" type="string" length="32"
		fixed="true" notDefaultValue="true" hidden="true" />
	<item id="cycleId" alias="周期" type="string" length="2"
		queryable="true" not-null="1" fixed="true">
		<dic id="chis.dictionary.cycleId" />
	</item>
	<item id="problemName" alias="主要问题" type="string" length="80" width="180"  not-null="1" queryable="true"/>
	<item id="description" alias="问题描述" type="string" length="500" width="200"  colspan="2" anchor="100%"/>
	<item id="result" alias="处理情况" type="string" length="500"  width="200" colspan="2" anchor="100%" not-null="1"/>
	<item id="occurDate" alias="发生日期" type="date" not-null="1" maxValue="%server.date.today" />
	<item id="solveDate" alias="解决日期" type="date" maxValue="%server.date.today" />
	<item id="recordUnit" alias="记录机构" type="string" length="16" width="180" hidden="true" update="false">
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="recordUser" alias="记录医生" type="string" length="20" update="false" queryable="true">
		<dic id="chis.dictionary.Personnel" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="recordDate" alias="记录日期" type="datetime"  xtype="datefield" update="false"
		defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="pastHistoryId" alias="既往史编号" type="string" length="16" hidden="true" />
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>