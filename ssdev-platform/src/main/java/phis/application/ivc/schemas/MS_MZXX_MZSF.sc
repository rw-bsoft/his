<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="MZGL" alias="卡号" length="32" />
	<item id="BRXM" alias="病人姓名" type="string" length="40" fixed="true" />
	<item id="BRXZ" alias="病人性质" length="18" fixed="true">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="FPHM" alias="发票号码" fixed="true" virtual="true"/>
	<item id="BRXB" alias="病人性别" length="4" type="int" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="AGE" alias="年龄" type="string" fixed="true"/>
</entry>
