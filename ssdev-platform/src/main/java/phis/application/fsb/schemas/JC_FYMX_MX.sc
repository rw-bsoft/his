<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FYMX" alias="家床费用明细表">
	<item id="FYRQ" alias="费用日期" type="data" not-null="1" width="75"/>
	<item id="FYSL" alias="数量" type="double" length="10" precision="2" not-null="1" width="50"/>
	<item id="FYDJ" alias="单价" type="double" length="10" precision="4" not-null="1" width="60"/>
	<item id="ZFBL" alias="比例" type="double" length="4" precision="3" not-null="1" width="50"/>
	<item id="ZJJE" alias="总金额" type="double" length="12" precision="2" not-null="1" width="60"/>
	<item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1" width="75"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZXKS" alias="执行科室" type="long" length="18">
		<dic id="phis.dictionary.department_zyyj" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSGH" alias="工号" length="10" width="50"/>
</entry>
