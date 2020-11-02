<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_Hypertension_FCBP" alias="高血压35岁首诊测压">
	<item id="fcbpId" alias="标识列" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="个人健康档案号" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="measureDate" alias="测量时间" type="date" colspan="2" fixed="true" not-null="1" maxValue="%server.date.today" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<item id="hypertensionHistory" alias="高血压史" not-null="1" type="String" enableKeyEvents="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
			<item key="9" text="不详" />
		</dic>
	</item>
	<item id="hypertensionFirst" alias="首次发现血压异常" type="String" enableKeyEvents="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
			<item key="9" text="不详" />
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6" width="70" display="0"
		minValue="100" maxValue="300" enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" type="double" length="6"  width="70" display="0"
		minValue="30" maxValue="500" enableKeyEvents="true" />
	<item id="bmi" alias="BMI" length="6" type="double" fixed="true"  width="40" display="0"/>
	<item id="hypertensionLevel" alias="血压级别" type="int" enableKeyEvents="true" display="0" />
	
	<item id="measureDoctor" alias="测量医生" type="string" length="20" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="measureUnit" alias="测量机构" type="string" length="20" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	
	<item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" queryable="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建时间" type="datetime"  xtype="datefield" update="false"  not-null="1" fixed="true" defaultValue="%server.date.today" enableKeyEvents="true" validationEvent="false" queryable="true" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
</entry>