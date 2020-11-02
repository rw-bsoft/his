<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FYMX" alias="费用明细表">
	<item id="JFRQ" alias="记账日期" width="150" type="date" not-null="1"/>
	<item id="ZYHM" alias="家床号"  not-null="1"/>
	<item id="BRXM" alias="姓  名" length="40" not-null="1"/>
	<item id="FYRQ" alias="费用日期" width="150" type="date" not-null="1" />
	<item id="FYMC" alias="费用名称" width="200" length="80"/>
	<item id="FYSL" alias="数  量" type="double" length="10" precision="2" not-null="1"/>
	<item id="FYDJ" alias="单  价" type="double" length="10" precision="4" not-null="1"/>
	<item id="ZJJE" alias="金  额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" not-null="1" />
	<item id="ZXKS" alias="执行科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_zyyj" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YGXM" alias="操作员" length="10" />
</entry>
