<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_JZHZ" alias="收入结帐汇总">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" generator="assigned" pkey="true"/>
	<item id="HZRQ" alias="汇总日期" type="date" not-null="1" pkey="true"/>
	<item id="XMBH" alias="项目编号" type="int" length="1" not-null="1" pkey="true"/>
	<item id="SQJC" alias="上期结存" type="double" length="12" precision="2" not-null="1"/>
	<item id="BQFS" alias="本期发生" type="double" length="12" precision="2" not-null="1"/>
	<item id="BQJS" alias="本期结算" type="double" length="12" precision="2" not-null="1"/>
	<item id="XJZP" alias="现金支票" type="double" length="12" precision="2" not-null="1"/>
	<item id="YHJE" alias="优惠金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="CYDJ" alias="出院待结" type="double" length="12" precision="2" not-null="1"/>
	<item id="QFJE" alias="欠费金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="CBJE" alias="参保应收" type="double" length="12" precision="2" not-null="1"/>
	<item id="QTJE" alias="其他应收" type="double" length="12" precision="2" not-null="1"/>
	<item id="BQYE" alias="本期余额" type="double" length="12" precision="2" not-null="1"/>
</entry>
