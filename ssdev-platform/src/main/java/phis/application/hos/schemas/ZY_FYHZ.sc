<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYHZ" alias="费用汇总">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" generator="assigned" pkey="true"/>
	<item id="HZRQ" alias="汇总日期" type="date" not-null="1" pkey="true"/>
	<item id="FYXM" alias="费用项目" type="long" length="18" not-null="1" pkey="true"/>
	<item id="SQJC" alias="上期结存" type="double" length="12" precision="2" not-null="1"/>
	<item id="BQFS" alias="本期发生" type="double" length="12" precision="2" not-null="1"/>
	<item id="BQJS" alias="本期结算" type="double" length="12" precision="2" not-null="1"/>
	<item id="BQJC" alias="本期结存" type="double" length="12" precision="2" not-null="1"/>
	<item id="SJJC" alias="实际结存" type="double" length="12" precision="2" not-null="1"/>
</entry>
