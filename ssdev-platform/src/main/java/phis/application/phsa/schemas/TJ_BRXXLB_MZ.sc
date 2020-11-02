<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="TJ_BRXXLB_MZ" alias="病人信息" sort="a.MZHM">
	<item id="EMPIID" alias="EMPIID" type="string" length="32" display="0"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0"/>
	<item id="MZHM" alias="门诊号码" type="string" length="32"  width="100" virtual="true"
		not-null="1"  editable="true" xtype="lookupfield_his" />
	<item id="BRXZ" alias="病人性质" virtual="true" length="18" width="150" not-null="1">
		<dic id="phis.dictionary.patientProperties" autoLoad="true"
			searchField="PYDM" />
	</item>
	<item id="BRXM" alias="病人姓名" type="string" length="20" width="150"
		 not-null="1" />
	<item id="BRXB" alias="病人性别" type="string" length="1" width="100"
		 defaultValue="1" not-null="1">
		<dic id="phis.dictionary.gender" autoLoad="true" />
	</item>
	<item id="CSNY" alias="出生日期" type="date" width="100"
		maxValue="%server.date.today" />
	<item id="DWMC" alias="工作单位" type="string" length="40"  width="300"/>
	<item id="JDSJ" alias="建档日期" type="timestamp" width="100"/>
</entry>