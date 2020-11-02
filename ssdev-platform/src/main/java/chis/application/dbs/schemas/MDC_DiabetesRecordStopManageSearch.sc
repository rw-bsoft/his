<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRecordStopManageSearch" alias="糖尿病终止管理查询">
	<item id="phrId" pkey="true" alias="档案编号" type="int" length="30"
		generator="auto" />
	<item id="cancellationCheckUser" alias="注销复核者" type="string"
		length="20" queryable="true" >
		<set type="exp">["$","%user.userName"]</set>
	</item>
	<item id="cancellationCheckDate" alias="注销复核时间" type="date"
		queryable="true" >
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="cancellationCheckUnit" alias="注销复核单位" type="string"
		length="20" queryable="true" noList="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue = "0"
		queryable="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="（注销）待核实"/>
		</dic>
	</item>
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false" display="1"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"  display="1"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield"  update="false"  display="1"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>