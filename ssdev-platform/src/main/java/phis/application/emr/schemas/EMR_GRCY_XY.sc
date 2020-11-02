<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_GRCY" alias="个人常用诊断">
	<item id="CYBS" alias="常用标识" type="long" length="9" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="9" />
		</key>
	</item>
	<item id="ZDYS" alias="诊断医生" type="string" length="10" not-null="1" display="0"/>
	<item id="ZXLB" alias="中西类别" type="int" length="1" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="西医"/>
			<item key="2" text="中医"/>
		</dic>
	</item>
	<item id="JBBS" alias="疾病标识" type="long" length="9" display="0"/>
	<item id="FJBS" alias="附加标识" type="long" length="9">
		<dic id="phis.dictionary.clinicSite" autoLoad="true"/>
	</item>
	<item id="MSZD" alias="诊断名称" type="string" length="100"/>
	<item id="FJMC" alias="疾病编码" type="string" length="60"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="JBMC" alias="疾病名称" type="string" length="200" display="0"/>
</entry>
