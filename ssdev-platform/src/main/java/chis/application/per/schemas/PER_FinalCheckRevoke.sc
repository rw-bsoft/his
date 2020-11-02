<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.per.schemas.PER_FinalCheckRevoke" alias="总检撤销" sort="createTime desc">
	<item id="phrId" alias="档案ID" type="string" length="30" generator="assigned" pkey="true" hidden="true"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
	<item id="description" alias="描述" type="string" xtype="textarea"
		colspan="3" width="300" anchor="100%" length="2000" />
	<item id="createUnit" alias="录入单位" type="string" length="20"
		width="180" fixed="true" update="false"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createTime" alias="录入日期" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
