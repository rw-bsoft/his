<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLMX" tableName="GY_YLMX" alias="医院医疗明细项目">
	<item id="JGID" alias="机构ID" length="25" type="string" not-null="1" generator="assigned" pkey="true"/>
	<item id="FYXH" alias="费用序号" length="18" type="long" not-null="1"/>
	<item id="FYDJ" alias="费用单价" length="8" type="double" precision="2" not-null="1"/>
	<item id="FYKS" alias="费用科室" length="18" type="long"/>
	<item id="ZFPB" alias="作废判别" length="1" type="int" not-null="1"/>
	<item id="DZBL" alias="打折比例" length="6" type="double" precision="3" not-null="1"/>
	<item id="JGBZ" alias="价格标志" type="int" length="1"/>
	<item id="YYZBM" alias="医保自编码" type="string" length="20"/>
</entry>
