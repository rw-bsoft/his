<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_BRRY" alias="病人管理">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="ZYHM" alias="住院号码" length="10" fixed="true" not-null="1"/>
	<item id="BRXM" alias="病人姓名" length="20" fixed="true" not-null="1"/>
	<item id="BRXB" alias="病人性别" fixed="true" length="4">
		<dic id="phis.dictionary.gender"/>
	</item>	
	<item id="BRXZ" alias="病人性质" fixed="true" length="18">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="病人科室" fixed="true" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE" autoLoad="true"/>
	</item>
	<item id="BRCH" alias="病人床号" fixed="true" type="int" length="12" not-null="1" display="0"/>
	<item id="RYRQ" alias="入院日期" fixed="true" type="datetime" not-null="1" width="130"/>
</entry>
