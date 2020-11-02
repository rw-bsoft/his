<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hsr.schemas.HSR_AssistingRegistration"
	alias="卫生监督协管巡查登记表">
	<item id="RecordID" alias="卫生监督协管巡查登记记录编号" type="string" length="16"
		pkey="true" not-null="1" fixed="true" hidden="true" generator="assigned"
		display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="adreesAndContent" alias="巡查地点与内容" length="500" width="150"
		colspan="3" xtype="textarea" />
	<item id="findProblem" alias="发现的主要问题" length="500" width="200"
		colspan="3" xtype="textarea" />
	<item id="patrolDate" width="80" queryable="true" alias="巡查日期"
		type="date" not-null="1" maxValue="%server.date.today" />
	<item id="patrolUser" alias="巡查人" queryable="true" width="100"
		type="string" length="20" not-null="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user14" parentKey="%user.manageUnit.id"
			render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
	</item>
	<item id="unitName" alias="巡查机构" type="string" length="20"
		 width="120" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"
			render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="commen" alias="备注" length="500" colspan="3" xtype="textarea"
		width="100" />
	<item id="createDate" alias="录入时间" type="datetime" xtype="datefield" display="2"
		update="false" fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" display="2"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user14" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" display="2"
		width="150" fixed="true" update="false" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"
			render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"
			render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
