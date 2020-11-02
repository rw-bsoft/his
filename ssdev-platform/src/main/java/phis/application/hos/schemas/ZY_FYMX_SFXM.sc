<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX" alias="费用明细日期汇总">
	<item id="FYRQ" alias="费用日期" type="data" not-null="1" width="75"/>
	<item id="FYXM" alias="费用项目" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.feesDic"/>
	</item>
	<item id="ZJJE" alias="总金额" type="double" length="12" precision="2" not-null="1" width="60"/>
	<item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1" width="75"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZXKS" alias="执行科室" type="long" length="18">
		<dic id="phis.dictionary.department_zyyj" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSGH" alias="工号" length="10" width="50"/>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" display="0"/>
</entry>
