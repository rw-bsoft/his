<?xml version="1.0" encoding="UTF-8"?>
<entry alias="狂犬病档案" sort="a.createDate,a.discoverDate,a.closeFlag desc">
	<item id="rabiesId" pkey="true" alias="记录序号" type="string" width="160"
		length="16" not-null="1" hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" />
	<item id="discoverDate" alias="暴露日期" type="date" queryable="true" />
	<item id="treatmentDate" alias="就诊日期" type="date" queryable="true" display="0"/>
	<item id="injuryPosition" alias="伤口部位" type="string" length="20"
		width="60" queryable="true">
		<dic render="LovCombo">
			<item key="1" text="头面部" />
			<item key="2" text="躯干部" />
			<item key="3" text="下肢" />
			<item key="4" text="右臂" />
			<item key="5" text="左臂" />
			<item key="6" text="手指" />
		</dic>
	</item>
	<item id="closeFlag" alias="结案标识" type="string" length="1"
		display="1" defaultValue="0">
		<dic>
			<item key="0" text="未结案" />
			<item key="1" text="已结案" />
		</dic>
	</item>
	<item id="status" alias="档案状态" type="string" defaultValue="0"
		length="1">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="注销" />
		</dic>
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1"
		hidden="true" display="0">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="6" text="作废" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="createUnit" alias="录入单位" type="string" length="20" display="0"
		width="180" fixed="true" update="false"
		defaultValue="%user.manageUnit.id" virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="createUser" alias="录入人员" type="string" length="20" display="0"
		update="false" fixed="true" defaultValue="%user.userId"
		virtual="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" queryable="true" update="false"
		defaultValue="%server.date.today"  display="0">
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" virtual="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id" virtual="true" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" virtual="true" display="0">
	</item>
	
</entry>