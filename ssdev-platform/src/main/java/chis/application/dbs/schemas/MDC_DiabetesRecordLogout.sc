<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRecordLogout" alias="糖尿病档案注销">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" hidden="true"/>
	
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1"
		queryable="true" not-null="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="cancellationDate" alias="档案注销日期" type="date"
		queryable="true" fixed="true" defaultValue="%server.date.today">
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		 display="2" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"
		length="100" display="2" colspan="2" anchor="100%" />
	<item id="status" alias="档案状态" type="string" length="1"
		queryable="true" hidden="true">
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