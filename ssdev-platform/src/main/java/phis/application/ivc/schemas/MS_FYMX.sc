<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_FYMX" tableName="MS_CF02" alias="门诊收费信息">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1000" />
		</key>
	</item>
	<item id="DH" alias="单号" type="string" virtual="true"/>
	<item id="LB" alias="类别" type="string" display="0" virtual="true"/>
	<item id="YSDM" alias="医生" length="32" display="0" type="string" virtual="true"/>
	<item id="KDRQ" alias="开单日期" length="40" display="0" type="string" virtual="true"/>
	<item id="YPMC" alias="项目名称" length="220" width="200" type="string" virtual="true"/>
	<item id="YCJL" alias="剂量" type="double" length="10" precision="2"/>
	<item id="CFTS" alias="贴数" type="int" length="2" not-null="1" width="50"/>
	<item id="YFGG" alias="规格" type="string" length="20"/>
	<item id="YFDW" alias="单位" type="string" length="4" width="60"/>
	<item id="YPYF" alias="频次" type="string" length="18" width="50">
		<dic id="phis.dictionary.useRate" searchField="MRCS"/>
	</item>
	<item id="GYTJ" alias="用法" type="int" length="4">
		<dic id="phis.dictionary.drugMode" />
	</item>
	<item id="YPSL" alias="总量" type="double" length="10" precision="2" not-null="1"/>
	<item id="HJJE" alias="合计金额" type="double" length="12" />
</entry>
