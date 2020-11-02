<?xml version="1.0" encoding="UTF-8"?>

<entry alias="门诊处方" sort="createDate desc">
	<item id="recipeId" alias="处方记录号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true" />
	<item id="clinicRecordId" alias="门诊记录号" type="string" length="16"
		hidden="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		hidden="true" />
	<item id="recipeNo" alias="处方号码" type="string" length="20"
		width="160" />
	<item id="recipeType" alias="处方类别" type="string" length="1">
		<dic>
			<item key="1" text="西药"/>
			<item key="2" text="中成药"/>
		</dic>
	</item>
	<item id="shareNum" alias="剂数" type="int" />
	<item id="createDept" alias="开方科室" type="string" length="8"
		width="180" />
	<item id="createDoctor" alias="开方医生" type="string" length="20" />
	<item id="createDate" alias="开方时间" type="date" />
	<item id="putoutDruggist" alias="发药医师" type="string" length="20" />
	<item id="putoutDate" alias="发药时间" type="date" />
	<item id="status" alias="处方状态" type="string" length="1">
		<dic>
			<item key="1">已发药</item>
			<item key="2">未发药</item>
		</dic>
	</item>
	<item id="memo" alias="备注" type="string" length="500" width="180" />
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
