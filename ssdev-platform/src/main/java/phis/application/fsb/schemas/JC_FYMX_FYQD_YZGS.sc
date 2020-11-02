<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FYMX"  alias="费用清单—医嘱格式">
	<item id="KSRQ" alias="开始日期" type="date" not-null="1" width="130"/>
	<item id="FYMC" alias="费用名称" length="80" width="170"/>
	<item id="ZZRQ" alias="终止日期" type="date" not-null="1" width="130"/>
	<item id="FYSL" alias="数量*天数" type="double"  width="80" length="10" precision="2" not-null="1"/>
	<item id="FYDJ" alias="费用单价" type="double" length="10" precision="4" not-null="1"/>
	<item id="ZJJE" alias="费用金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" not-null="1"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" not-null="1" width="130" display="0">
		<dic id="department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
</entry>
