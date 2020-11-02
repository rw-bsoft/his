<?xml version="1.0" encoding="UTF-8"?>
<entry alias="家庭主要问题">
	<item id="familyProblemId" pkey="true" alias="记录序号" type="string"  width="160"
		length="16" not-null="1" fixed="true" hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="familyId" alias="家庭编码" type="string" length="30"
		not-null="1" hidden="true" />
	<item id="happenDate" alias="发生日期" type="date" defaultValue="%server.date.today" maxValue="%server.date.today" />
	<item id="solveDate" alias="解决日期" type="date" defaultValue="%server.date.today" />
	<item id="empiId" alias="empiId" type="string" length="32" hidden="true"/>
	<item id="problemName" alias="问题名称" type="string" length="50"
		width="180" colspan="2" anchor="100%" queryable="true" not-null="1"/>
	<item id="description" alias="问题描述" type="string" xtype="textarea"
		length="500" width="180" colspan="2" anchor="100%" />
	<item id="solveResult" alias="处理及结果" type="string" xtype="textarea"
		length="500"  width="180" colspan="2" anchor="100%" not-null="1"/>
	<item id="recordUnit" alias="记录机构" type="string" length="20"  width="180" update="false"
		hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="recordUser" alias="记录医生" type="string" length="20" update="false"
		hidden="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="recordDate" alias="记录日期" type="datetime"  xtype="datefield" hidden="true" defaultValue="%server.date.today" update="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>