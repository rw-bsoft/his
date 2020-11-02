<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYQD" alias="费用清单">
	<item id="BRXM" alias="病人姓名" fixed="true"/>
	<item id="BRXZ" alias="病人性质" fixed="true">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="ZYTS" alias="住院天数" type="int" fixed="true"/>
	<item id="RYRQ" alias="入院日期" fixed="true"/>
	<item id="ZFLJ" alias="自负累计" type="double" length="10" precision="2" fixed="true"/>
	<item id="ZFHJ" alias="自负合计" type="double" length="10" precision="2" fixed="true"/>
	<item id="ZYHM" alias="住院号码" fixed="true"/>
	<item id="BRKS" alias="病人科室" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="BRCH" alias="病人床号" fixed="true"/>
	<item id="CYRQ" alias="出院日期" fixed="true"/>
	<item id="FYLJ" alias="费用累计" type="double" length="10" precision="2" fixed="true"/>
	<item id="FYHJ" alias="费用合计" type="double" length="10" precision="2" fixed="true"/>
</entry>
