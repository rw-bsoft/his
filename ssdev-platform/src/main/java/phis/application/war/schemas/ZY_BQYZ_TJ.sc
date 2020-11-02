<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱">
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="BRCH" alias="床号" virtual="true" width="60"/>
	<item id="BRXM" alias="姓名" display="0" virtual="true"/>
	<item id="YZZH" alias=" " type="long" length="1" fixed="true" width="10" renderer="showColor"/>
	<item id="YZMC" alias="药品名称" width="200" virtual="true"/>
	<item id="YPYF" alias="途径" width="50">
		<dic id="phis.dictionary.drugMode" autoLoad="true" searchField="PYDM"/>
	</item>
	<item id="YCSL" alias="一次量" type="double" width="60" length="7" precision="3"/>
	<item id="FYCS" alias="次数" type="int" width="50" length="1"/>
	<item id="SYPC" alias="用法" type="long" length="18" width="40">
		<dic id="phis.dictionary.useRate_yztj" autoLoad="true" searchField="text" fields="key,text,MRCS" />
	</item>
	<item id="JE" alias="金额" type="double" precision="2" virtual="true" width="80"/>
	<item id="KSSJ" alias="开嘱时间" type="datetime" width="125"/>
	<item id="TZSJ" alias="停嘱时间" type="datetime" width="125"/>
	<item id="BZXX" alias="说明" width="40"/>
	<item id="YFSB" alias="发药药房" type="long" width="60">
		<dic id="phis.dictionary.pharmacy_bqtj" autoLoad="true"/>
	</item>
</entry>
