<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="医嘱打印_指定行打印" sort="b.DYYM,b.DYHH">
	<item id="ZYH" alias="住院号" type="long" length="18"  display="0"/>
	<item id="JLXH" alias="记录序号" type="long" length="18"  display="0" />
	<item id="DYXH" alias="打印序号" type="long" length="18"  display="0" pkey="true"/>
	<item id="KSSJ" alias="开嘱时间" type="dateTime" width="140"/>
	<item id="YZMC" alias="医嘱名称" width="350"/>
	<item id="YSGH" alias="开嘱医生">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="FHGH" alias="护士" type="string" length="10" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="TZSJ" alias="停嘱时间" type="dateTime" width="140"/>
	<item id="TZYS" alias="停嘱医生">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="FHGH" alias="护士" type="string" length="10" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="FHSJ" alias="复核时间" type="datetime" width="140"/>
	<item ref="b.DYYM" alias="页码"/>
	<item ref="b.DYHH" alias="行号"/>
	<relations>
		<relation type="parent" entryName="phis.application.war.schemas.EMR_YZDY">
		<join parent="YZBXH" child="JLXH" />
		</relation>
	</relations>
</entry>