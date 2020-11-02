<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YZDY_LSYZ" alias="医嘱打印_临时医嘱">
	<item id="ZYH" alias="住院号" type="long" length="18"  display="0"/>
	<item id="JLXH" alias="记录序号" type="long" length="18"  display="0"/>
	<item id="KSSJ" alias="开嘱时间" type="dateTime"/>
	<item id="YZMC" alias="医嘱名称" width="180"/>
	<item id="YSGH" alias="开嘱医生">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<!--<item id="ZT" alias="状态"/>张伟说不要-->
	<item id="FHGH" alias="复核" type="string" length="10" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="FHSJ" alias="复核时间" type="datetime"/>
</entry>