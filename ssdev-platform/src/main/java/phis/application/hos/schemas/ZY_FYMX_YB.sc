<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX_FYQD" sort="FYRQ desc" tableName="ZY_FYMX" alias="病人入院">
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1"/>
	<item id="ZYH" alias="住院号" display="0" type="long" length="18" not-null="1"/>
	<item id="FYRQ" alias="费用日期" type="dateTime" not-null="1" width="130"/>
	<item id="YPLX" alias="药品类型" display="0" type="int" length="1" not-null="1"/>
	<!--<item id="FYXH" alias="费用序号" display="0" type="long" length="18" not-null="1"/>-->
	<item id="FYMC" alias="费用名称" length="80" width="170"/>
	<item id="FYSL" alias="数量" type="double"  width="60" length="10" precision="2" not-null="1"/>
	<item id="FYDJ" alias="费用单价" type="double" length="10" precision="4" not-null="1"/>
	<item id="ZJJE" alias="费用金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" not-null="1" width="120">
		<dic id="phis.dictionary.department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="SRGH" alias="操作员" length="10">
		<dic id="phis.dictionary.user"/>
	</item>
</entry>