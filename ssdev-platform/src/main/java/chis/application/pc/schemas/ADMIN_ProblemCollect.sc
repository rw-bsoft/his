<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pc.schemas.ADMIN_ProblemCollect" alias="问题收集" sort="createTime desc">
	<item id="problemId" alias="问题序号" type="string" length="16"
		width="160" not-null="1" generator="assigned" pkey="true"
		display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="description" alias="描述" type="string" xtype="textarea"
		colspan="3" width="300" anchor="100%" length="2000" not-null="true" />
	<item id="contactPhone" alias="联系人电话" type="string" length="20" not-null="1" />
	<item id="status" alias="状态" type="string" length="2"
		defaultValue="0" not-null="true" queryable="true">
		<dic>
			<item key="0" text="未处理" />
			<item key="2" text="已受理" />
			<item key="1" text="已处理" />
		</dic>
	</item>
	<item id="treatTime" alias="处理" type="date" fixed="true" />
	<item id="treatDesc" alias="处理描述" type="string" xtype="textarea"
		width="300" length="500" fixed="true" colspan="3" anchor="100%" />
	
	<item id="createUnit" alias="录入单位" type="string" length="20"
		width="180" fixed="true" update="false" display="1"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人员" type="string" length="20" display="1"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createTime" alias="录入日期" type="datetime"  xtype="datefield" update="false" display="1"
		fixed="true" defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId"  display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today"  display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
