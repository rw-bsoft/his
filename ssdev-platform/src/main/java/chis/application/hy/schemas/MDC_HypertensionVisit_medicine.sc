<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="高血压随访" sort="a.empiId">
	<item id="visitId" alias="随访标识" type="string" display="0"
		length="16" not-null="1" pkey="true" fixed="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1"
		display="0" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
	<item id="drugNames1" alias="药物名称1" type="string" display="2" virtual="true">
	</item>
	<item id="medicineType1" alias="药物种类" type="string" virtual="true"
		length="2" width="120" colspan="2">
		<dic id="chis.dictionary.medicineTypeGXY"/>
	</item>
	<item id="everyDayTime1" alias="次数" type="string" display="2" virtual="true">
	</item>
	<item id="oneDosage1" alias="剂量" type="string" display="2" virtual="true"/>
	<item id="medicineUnit1" alias="单位" type="string" length="6" display="2" virtual="true">
		<dic id="chis.dictionary.medicineUnitMB"/>
	</item>
	<item id="drugNames2" alias="药物名称2" type="string" display="2" virtual="true">
	</item>
	<item id="medicineType2" alias="药物种类" type="string" virtual="true"
		length="2" width="120" colspan="2">
		<dic id="chis.dictionary.medicineTypeGXY"/>
	</item>
	<item id="everyDayTime2" alias="次数" type="string" display="2" virtual="true">
	</item>
	<item id="oneDosage2" alias="剂量" type="string" display="2" virtual="true"/>
	<item id="medicineUnit2" alias="单位" type="string" length="6" display="2" virtual="true">
		<dic id="chis.dictionary.medicineUnitMB"/>
	</item>
	<item id="drugNames3" alias="药物名称3" type="string" display="2" virtual="true">
	</item>
	<item id="medicineType3" alias="药物种类" type="string" virtual="true"
		length="2" width="120" colspan="2">
		<dic id="chis.dictionary.medicineTypeGXY"/>
	</item>
	<item id="everyDayTime3" alias="次数" type="string" display="2" virtual="true">
	</item>
	<item id="oneDosage3" alias="剂量" type="string" display="2" virtual="true"/>
	<item id="medicineUnit3" alias="单位" type="string" length="6" display="2" virtual="true">
		<dic id="chis.dictionary.medicineUnitMB"/>
	</item>
	<item id="drugNames4" alias="药物名称4" type="string" display="2" virtual="true">
	</item>
	<item id="medicineType4" alias="药物种类" type="string" virtual="true"
		length="2" width="120" colspan="2">
		<dic id="chis.dictionary.medicineTypeGXY"/>
	</item>
	<item id="everyDayTime4" alias="次数" type="string" display="2" virtual="true">
	</item>
	<item id="oneDosage4" alias="剂量" type="string" display="2" virtual="true"/>
	<item id="medicineUnit4" alias="单位" type="string" length="6" display="2" virtual="true">
		<dic id="chis.dictionary.medicineUnitMB"/>
	</item>
	<item id="drugNames5" alias="药物名称5" type="string" display="2" virtual="true">
	</item>
	<item id="medicineType5" alias="药物种类" type="string" virtual="true"
		length="2" width="120" colspan="2">
		<dic id="chis.dictionary.medicineTypeGXY"/>
	</item>
	<item id="everyDayTime5" alias="次数" type="string" display="2" virtual="true">
	</item>
	<item id="oneDosage5" alias="剂量" type="string" display="2" virtual="true"/>
	<item id="medicineUnit5" alias="单位" type="string" length="6" display="2" virtual="true">
		<dic id="chis.dictionary.medicineUnitMB"/>
	</item>
</entry>
