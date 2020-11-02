<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YZDY_CQYZ" alias="医嘱打印_长期医嘱">
	<item id="ZYH" alias="住院号" type="long" length="18"  display="0"/>
	<item id="JLXH" alias="记录序号" type="long" length="18"  display="0"/>
	<item id="KSSJ" alias="开嘱时间" type="dateTime"/>
	<item id="YZMC" alias="医嘱名称" width="180"/>
	<item id="YSGH" alias="开嘱医生">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<!--<item id="ZT" alias="状态"/>张伟说不要-->
	<item id="TZSJ" alias="停嘱时间" type="dateTime"/>
	<item id="TZYS" alias="停嘱医生">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="FHGH" alias="复核" type="string" length="10" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="FHSJ" alias="复核时间" type="datetime"/>
	
	<item id="YCJL" alias="剂量" type="double" max="9999999.999" length="10" precision="3" not-null="true" width="60" display="0"/>
	<item id="JLDW" alias=" " type="string"  width="30" fixed="true" virtual="true" display="0"/>
	<item id="SYPC" alias="频次"  not-null="true"  type="string" length="6" width="60" display="0">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="YPYF" alias="途径" not-null="true" type="long" length="18" listWidth="90" width="60" display="0">
		<dic id="phis.dictionary.drugWay" searchField="PYDM" fields="key,text,PYDM,FYXH" autoLoad="true"/>
	</item>
	<item id="TZFHR" alias="复核" type="string" length="10" width="60" display="0">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="DYYM" alias="打印页码" width="180" display="0"/>
	<item id="DYHH" alias="打印行号" width="180" display="0"/>
</entry>