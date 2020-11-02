<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_ZYYY_INFO" tableName="MS_BRDA" alias="患者信息">
	<item id="BRID" alias="病人ID号" type="string" length="18" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="10" startPos="1"/>
		</key>
	</item>
	<item id="MZHM" alias="门诊号码" fixed="true" type="string"  length="32"/>
	<item id="BRXM" alias="病人姓名" fixed="true" type="string" length="40"/>
	<item id="BRXB" alias="病人性别" fixed="true" type="string" length="4">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="NL" alias="病人年龄" fixed="true" type="int" virtual="true" length="40"/>
	<item id="BRXZ" alias="病人性质" fixed="true" type="string" length="18">
		<dic id="phis.dictionary.patientProperties_MZ"/>
	</item>
	<item id="ZDMC" alias="诊断名称" fixed="true" type="string"  colspan="2" length="32" virtual="true" />
	<item id="YYRQ" alias="住院日期" type="date" length="32" minValue="%server.date.date" virtual="true" />
</entry>
