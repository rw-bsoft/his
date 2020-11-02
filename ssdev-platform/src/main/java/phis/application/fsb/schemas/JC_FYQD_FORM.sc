<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FYQD" alias="费用清单">
	<item id="BRXM" alias="病人姓名" fixed="true"/>
	<item id="BRXZ" alias="病人性质" fixed="true">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="ZYTS" alias="天数" type="int" fixed="true"/>
	<item id="KSRQ" alias="开始日期" fixed="true"/>
	<item id="ZZRQ" alias="终止日期" fixed="true"/>
	<item id="ZFLJ" alias="自负累计" type="double" length="10" precision="2" fixed="true"/>
	<item id="ZFHJ" alias="自负合计" type="double" length="10" precision="2" fixed="true"/>
	<item id="ZYHM" alias="家床号码" fixed="true"/>
	<item id="FYLJ" alias="费用累计" type="double" length="10" precision="2" fixed="true"/>
	<item id="FYHJ" alias="费用合计" type="double" length="10" precision="2" fixed="true"/>
</entry>
