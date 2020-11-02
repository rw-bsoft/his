<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_BRRY" alias="欠费清单" sort="BRCH,FJHM desc">
	<item id="ck" alias="是否催款" type="string" renderer='radioCompent' width="100"/>
	<item id="ZYH" alias="住院号" display="0"/>
	<item id="ZYHM" alias="住院号码"/>
	<item id="BRCH" alias="床号"/>
	<item id="BRXZ" alias="病人性质" type="long">
		<dic id='phis.dictionary.patientProperties_GY' autoLoad='true'/>
	</item>

	<item id="BRXM" alias="姓名" type="string"/>
	<item id="BRXB" alias="性别" type="int" >
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="CSNY" alias="年龄" type="int"/>
	<item id="RYRQ" alias="天数" type="long"/>
	<item id="JKJE" alias="缴款金额" type="double"/>
	<item id="ZFJE" alias="自负金额" type="double"/>
	<item id="QFJE" alias="欠费金额" type="double"/>
	<item id="CKJE" alias="催款金额" type="double" display="0"/>
</entry>
