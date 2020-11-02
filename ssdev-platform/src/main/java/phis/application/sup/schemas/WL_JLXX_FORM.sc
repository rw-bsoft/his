<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JLXX_FORM" tableName="WL_JLXX" alias="计量信息(WL_JLXX)">
	<item id="JLXH" alias="计量序号" length="18" not-null="1" type="long" generator="assigned" display="0" pkey="true">
		<key>
			<value name="increaseId" defaultFill="0" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="WZXH" alias="物资序号" type="long" length="12" display="0"/>
	<item id="ZBXH" alias="账簿序号" type="long" length="12" display="0"/>
	<item id="JLBH" alias="记录编号" type="string" length="12" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" display="0"/>
	<item id="WZMC" alias="物资名称" type="string" length="12" fixed="true" virtual="true" layout="JBXX"/>
	<item id="WZGG" alias="物资规格" type="string" length="12" fixed="true" virtual="true" layout="JBXX"/>
	<item id="WZDW" alias="物资单位" type="string" length="12" fixed="true" virtual="true" layout="JBXX"/>
	<item id="WZBH" alias="物资编号" type="string" length="20" fixed="true" virtual="true" layout="JBXX"/>
	<item id="WZDJ" alias="物资价格" type="double" length="12" fixed="true" virtual="true" layout="JBXX"/>
	<item id="KSDM" alias="在用科室" type="long" length="12" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="CJXH" alias="厂家序号" type="long" length="12" display="0"/>
	<item id="CJMC" alias="生产厂家" type="string" virtual="true" fixed="true" length="60" layout="JBXX"/>
	<item id="DWXH" alias="供货单位" type="long" length="12" layout="JBXX">
		<dic id="phis.dictionary.supplyUnit" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true"/>
	</item>
	<item id="BGGH" alias="保管人员" type="string" length="12" layout="JBXX">
		<dic id="phis.dictionary.wzdoctor_yjqx" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="CCBH" alias="出厂编号" type="string" length="12" layout="JBXX"/>
	<item id="CCRQ" alias="出厂日期" type="date" length="12" layout="JBXX"/>
	<item id="GRRQ" alias="购入日期" type="date" length="12" layout="JBXX"/>
	<item id="GRGH" alias="购入人员" type="string" length="12" layout="JBXX">
		<dic id="phis.dictionary.wzdoctor_yjqx" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="WZSL" alias="登记数量" type="int" length="12" not-null="1" layout="JBXX"/>
	<item id="SYSL" alias="剩余数量" type="double" length="12" fixed="true" layout="JBXX"/>
	<item id="JLQJFL" alias="计量器具分类" type="int" length="2" not-null="1" layout="JLXX">
		<dic id="phis.dictionary.jlqjfl" autoLoad="true"/>
	</item>
	<item id="JLLB" alias="计量类别" type="int" length="1" not-null="1" layout="JLXX">
		<dic id="phis.dictionary.jlfl" autoLoad="true"/>
	</item>
	<item id="CLFW" alias="测量范围" type="string" length="30" layout="JLXX"/>
	<item id="ZQDJ" alias="准确度等级" type="string" length="10" layout="JLXX"/>
	<item id="FDZ" alias="分度值" type="string" length="10" layout="JLXX"/>
	<item id="JDZQ" alias="检定周期(月)" type="int" length="2" layout="JLXX"/>
	<item id="XCJD" alias="下次检定" type="date" fixed="true" layout="JLXX"/>
	<item id="DDMC" alias="地点名称" type="string" length="50" layout="JLXX"/>
	<item id="SJQD" alias="时间区段" type="string" length="14" layout="JLXX"/>
	<item id="QJBZ" alias="强检标志" type="int" xtype="checkbox" length="1" layout="JLXX"/>
</entry>
