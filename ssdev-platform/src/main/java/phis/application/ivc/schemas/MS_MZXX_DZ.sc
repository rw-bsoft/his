<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" length="18" not-null="1" hidden="true" type="long" generator="assigned" pkey="true" />
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" hidden="true"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" not-null="1"/>
	<item id="SFRQ" alias="收费日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="BRXM" alias="病人姓名" type="string" length="40"/>
	<item id="BRXZ" alias="病人性质" length="18" type="long">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="ZJJE" alias="总计金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="QTYS" alias="农合报销" length="12" type="double" precision="2" not-null="1"/>
	<item id="XJJE" alias="现金金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZHJE" alias="帐户金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="HBWC" alias="货币误差" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZFJE" alias="自负金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZFPB" alias="作废判别" length="1" type="int" not-null="1"/>
	<item id="CZGH" alias="收费人员" type="string" length="10">
		<dic id="phis.dictionary.doctor"></dic>
	</item>
	<item id="JZRQ" alias="结帐日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="HZRQ" alias="汇总日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="SFFS" alias="收费方式" length="1" type="int" not-null="1"/>
	<item id="JKJE" alias="交款金额" length="12" type="double" precision="2"/>
	<item id="TZJE" alias="退找金额" length="12" type="double" precision="2"/>
	<item id="HZJE" alias="核准金额" length="12" type="double" precision="2" />
	<item id="YLJZJE" alias="医疗救助金额" length="12" type="double" precision="2"/>
</entry>
