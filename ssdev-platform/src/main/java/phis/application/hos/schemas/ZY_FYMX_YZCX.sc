<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX" alias="费用明细表">
	<item id="JFRQ" alias="记费日期" width="150" type="date" not-null="1"/>
	<item id="FYRQ" alias="费用日期" width="150" type="date" not-null="1" />
	<item id="FYMC" alias="费用名称" width="200" type="string" length="80"/>
	<item id="FYSL" alias="数  量" type="double" length="10" precision="2" not-null="1"/>
	<item id="FYDJ" alias="单  价" type="double" length="10" precision="4" not-null="1"/>
	<item id="ZJJE" alias="金  额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" not-null="1" />
	<item id="FYKS" alias="收费科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_zy" />
	</item>
</entry>
