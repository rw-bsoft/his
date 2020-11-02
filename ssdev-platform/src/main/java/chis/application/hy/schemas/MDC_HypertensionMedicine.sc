<?xml version="1.0" encoding="UTF-8"?>

<entry alias="高血压服药情况">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30"
		hidden="true" />
	<item id="visitId" alias="随访标识" type="string" length="16"
		hidden="true" />
	<item id="medicineId" alias="药物ID" type="string" length="16"
		hidden="true" />
	<item id="medicineType" alias="药物种类" type="string"
		length="2" width="120" colspan="2">
		<dic>
			<item key="0" text="其他" />
			<item key="1" text="钙拮抗剂" />
			<item key="2" text="ACEI" />
			<item key="3" text="ARB" />
			<item key="4" text="利尿剂" />
			<item key="5" text="β阻滞剂" />
			<item key="6" text="复方制剂" />
			<item key="7" text="中枢交感抑制剂" />
			<item key="8" text="α阻滞剂" />
			<item key="9" text="α+β阻滞剂" />
		</dic>
	</item>
	<item id="medicineName" alias="药物名称" type="string" not-null="1"
		length="50" width="180" colspan="2" mode="remote">
	</item>
	
	<item id="medicineFrequency" alias="次数" type="string"
		not-null="1" length="20" />
	<item id="days" alias="/天数" type="int" not-null="1" length="20" defaultValue="1"/>
	
	<item id="medicineDosage" alias="每次剂量" type="double" not-null="1"
		length="10" />
	<item id="medicineUnit" alias="剂量单位" type="string" length="6">
	</item>
	<item id="medicineDate" alias="开药时间" type="date"
		defaultValue="%server.date.today" maxValue="%server.date.today"/>
	<item id="totalCount" alias="总剂量" type="double" length="6" width="90"/>
		
	<item id="useUnits" alias="剂量单位" type="string" length="10"
		display="0" />
		
	<item id="otherMedicineDesc" alias="其他用药描述" type="string" length="100"
		colspan="2" width="150" />
	<item id="medicineTotalDosage" alias="总剂量" type="double"
		length="10" display="0" />
	<item id="medicineWay" alias="使用途径" type="string" length="3"
		width="130" display="0">
		<dic id="chis.dictionary.CV5201_22" />
	</item>
	<item id="sideEffectsFlag" alias="副作用" type="string" length="1"
		defaultValue="2" colspan="2" anchor="50%" display="0">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="sideEffects" alias="副作用描述" type="string" length="100"
		fixed="true" colspan="2" xtype="textarea" width="150" display="0" />
	<item id="createUser" alias="录入员工" length="20" display="0"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入单位" length="20" display="0"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" display="0"
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
