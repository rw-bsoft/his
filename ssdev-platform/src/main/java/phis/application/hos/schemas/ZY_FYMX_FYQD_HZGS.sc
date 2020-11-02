<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX_FYHZ"  alias="费用清单—汇总格式">
	<item id="ZYH" alias="住院号" display="0" type="long" length="18" not-null="1"/>
	<item id="FYRQ" alias="费用日期" type="dateTime" not-null="1" width="130"/>
	<item id="YPLX" alias="药品类型" display="0" type="int" length="1" not-null="1"/>
	<item id="FYXH" alias="费用序号" display="0" type="long" length="18" not-null="1"/>
	<item id="FYMC" alias="费用名称" length="80" width="170"/>
	<item id="FYSL" alias="费用数量" type="double" length="10" precision="2" not-null="1"/>
	<item id="FYDJ" alias="费用单价" type="double" length="10" precision="4" not-null="1"/>
	<item id="ZJJE" alias="费用金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" not-null="1"/>
	<item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" not-null="1" width="130">
		<dic id="phis.dictionary.department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
</entry>
