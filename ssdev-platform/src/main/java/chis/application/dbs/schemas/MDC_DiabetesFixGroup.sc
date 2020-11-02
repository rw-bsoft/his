<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesFixGroup" alias="糖尿病定转组信息" sort="fixDate">
	<item id="fixId" alias="记录序号" type="string" length="16" not-null="1"
		pkey="true" hidden="true" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" fixed="true"
		hidden="true" />
	<item id="empiId" alias="empiId" type="string" length="32"
		fixed="true" hidden="true" />
	<item id="fixDate" alias="定转组日期" type="date" defaultValue="%server.date.today" >
	</item>
	<item id="fbs" alias="空腹血糖" type="double" length="6" precision="2"
		not-null="1" noList="true" />
	<item id="pbs" alias="餐后血糖" type="double" length="6" precision="2"
		noList="true" />
	<item id="controlResult" alias="控制情况" type="string" length="1"
		defaultValue="5" noList="true">
		<dic>
			<item key="1" text="控制不佳" />
			<item key="2" text="控制良好" />
			<item key="3" text="未规范管理" />
			<item key="4" text="未评价" />
			<item key="5" text="新病人" />
		</dic>
	</item>
	<item id="diabetesGroup" alias="组别" type="string" length="2" fixed="true">
		<dic>
			<item key="01" text="一组" />
			<item key="02" text="二组" />
			<item key="03" text="三组" />
			<item key="99" text="一般管理对象" />
		</dic>
	</item>
	<item id="fixType" alias="定转组情况" type="string" length="1"
		defaultValue="1" fixed="true">
		<dic>
			<item key="1" text="初次定组" />
			<item key="2" text="维持原组不变" />
			<item key="3" text="定期转组" />
			<item key="4" text="不定期转组" />
			<item key="5" text="随访评估定组" />
			<item key="6" text="年度评估定组" />
		</dic>
	</item>
	<item id="createUser" alias="定转组医生" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<!-- 
		<item id="laboratoryType" alias="采血类型" type="string" length="1"
			not-null="1" noList="true" colspan="2">
			<dic>
				<item key="1" text="静脉" />
				<item key="2" text="毛细血管" />
			</dic>
		</item>
		 -->
	<item id="oldGroup" alias="原定分组" type="string" length="2"
		update="false" defaultValue="00" hidden="true" noList="true">
	</item>
	
	<item id="createUnit" alias="录入单位" length="20" update="false"  display="0"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield"  update="false"  display="0"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
