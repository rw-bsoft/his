<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CSZC_LIST" tableName="WL_CSZC" alias="初始帐册">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="CZYZ" alias="重置原值" type="double" length="12" precision="2"/>
	<item id="WZZT" alias="状态" type="int" length="2">
		<dic id="phis.dictionary.assetstatuszc" autoLoad="true"/>
	</item>
	<item id="ZYKS" alias="科室" type="int" length="4">
		<dic id="phis.dictionary.department" filter = "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYCODE"/>
	</item>
	<item id="QYRQ" alias="启用日期" type="date"/>
	<item id="CZRQ" alias="重置日期" type="date"/>
	<item id="ZJRQ" alias="折旧日期" type="date"/>
	<item id="JTZJ" alias="计提折旧" type="double" length="12" precision="2"/>
	<item id="ZJYS" alias="折旧月数" type="int" length="4"/>
	<item id="FCYS" alias="封存月数" type="int" length="4"/>
</entry>
