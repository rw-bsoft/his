<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_FYXX" tableName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" length="18" not-null="1" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" isGenerate="false" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="BRID" alias="病人ID号" length="18" display="0" type="long"/>
	<item id="JZSJ" alias="就诊时间" type="date" virtual="true"/>
	<item id="MZHM" alias="门诊号码" length="32" type="string" virtual="true"/>
	<item id="BRXM" alias="病人姓名" length="40" type="string" virtual="true"/>
	<item id="BRXB" alias="性别" length="4" type="string" virtual="true">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="BRXZ" alias="病人性质" length="18" type="string" virtual="true">
		<dic id="phis.dictionary.patientProperties"/>
	</item>
	<item id="KSDM" alias="就诊科室" length="18" type="long" virtual="true">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="CFS" alias="处方数" length="10" type="int" virtual="true"/>
	<item id="CFJE" alias="处方金额" length="12" type="double" precision="2" virtual="true"/>
	<item id="JCS" alias="检查数" length="10" type="int" virtual="true"/>
	<item id="JCJE" alias="检查金额" length="12" type="double" precision="2" virtual="true"/>
	<item id="FJJE" alias="附加项目" length="12" type="double" precision="2" virtual="true"/>
</entry>
