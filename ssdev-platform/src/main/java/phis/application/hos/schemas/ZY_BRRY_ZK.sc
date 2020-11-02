<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY_ZK" alias="病人入院转科" tableName="ZY_BRRY">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>

	<item id="ZYHM" alias="住院号码" length="10" not-null="1" fixed="true"/>

	<item id="BRXM" alias="病人姓名" length="20" not-null="1" fixed="true"/>
	<item id="HCLX" alias="转床类型" type="int" length="1" display="0" defaultValue="1"/>
	<item id="ZXBZ" alias="执行标志" type="int" length="1" display="0" defaultValue="2"/>
	<item id="YSSQRQ" alias="医生申请日期" type="date" display="0"/>
	<item id="BRKS" alias="病人科室" type="long" length="18" fixed="true" >
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="BRBQ" alias="病人病区" type="long" length="18" fixed="true">
		<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"></dic>
	</item>
	<item id="BRCH" alias="病人床号" length="12" fixed="true"/>
	<item id="ZSYS" alias="主任医师" length="10"  fixed="true">
		<dic id="phis.dictionary.doctor_cfqx"  autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="HHKS" alias="转后科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_zy" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  />
	</item>
	<item id="HHYS" alias="转后医生" length="10" not-null="1">
		<dic id="phis.dictionary.doctor_cfqx"  autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="HHBQ" alias="转后病区" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"></dic>
	</item>
</entry>
