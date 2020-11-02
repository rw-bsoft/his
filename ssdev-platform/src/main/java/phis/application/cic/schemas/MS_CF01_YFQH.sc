<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01_YFQH" tableName="MS_CF01" alias="门诊处方01表">
	<item id="CFSB" alias="处方识别" display="1" type="long" length="18" not-null="1" isGenerate="false" hidden="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" />
	<item id="CFHM" alias="处方号码" fixed="true" generator="assigned" type="string" length="10">
	</item>
	<item id="KFRQ" alias="开方日期" xtype="datetimefield" width="140" type="date" not-null="1" />
	<item id="CFLX" alias="处方类型" type="int" length="1" not-null="1" defaultValue="1" >
		<dic id="phis.dictionary.prescriptionType" editable="false"/>
	</item>
	<item id="YFSB" alias="发药药房" type="long" length="18" fixed="true">
		<dic id="phis.dictionary.pharmacy" editable='false' defaultIndex="0" filter="['and',['eq',['$','item.properties.XYQX'],['s','1']],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]]" />
	</item>
	<item id="KSDM" alias="就诊科室" type="long"  length="18" fixed="true">
		<dic id="phis.dictionary.department_leaf" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSDM" alias="开方医生" length="10" fixed="true" type="string">
		<dic id="phis.dictionary.doctor_cfqx_kjg" searchField="PYCODE" autoLoad="true" />
	</item>
	<item id="CFTS" alias="草药帖数" defaultValue="1" type="int" minValue="1" maxValue="99" not-null="1" display="2"/>
	<item id="JZXH" alias="就诊序号" display="0"  type="long" length="18" />
	<item id="BRID" alias="病人ID" display="0"  type="long" length="18" />
	<item id="DJLY" alias="单据来源" display="0" type="int" length="8" />
	<item id="DJYBZ" alias="代煎药标志" display="0" type="int" length="1" />
</entry>
