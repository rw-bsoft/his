<entry  alias="精神病记录纸" sort="createDate asc,recordPaperId asc">
	<item id="recordPaperId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="精神病档案编号" type="string" length="30" fixed="true" display="0" />
	<item id="content" alias="记录内容" type="string" length="1000" height="300" xtype="textarea"
		colspan="3" not-null="true" display="2"/>
	<item id="createUnit" alias="录入机构" type="string" length="20"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" display="2" not-null="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入医生" type="string" length="20"
		queryable="true" defaultValue="%user.userId" fixed="true"  not-null="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" queryable="true" defaultValue="%server.date.today" fixed="true" not-null="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
