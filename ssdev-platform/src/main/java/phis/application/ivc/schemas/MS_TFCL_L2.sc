<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="退费处理list2">
	<item id="CFSB" alias="处方识别" type="long" display="0" length="18" />
	<item id="YPMC" alias="费用名称" type="string" width="140" renderer="onRenderer"/>
	<item id="YFGG" alias="规格" type="string" length="20"/>
	<item id="YFDW" alias="单位" type="string" width="50" length="4"/>
	<item id="YPDJ" alias="单价" type="double" length="12" width="60" precision="4" not-null="1"/>
	<item id="YPSL" alias="数量" type="double" length="10" width="50" precision="2" not-null="1"/>
	<item id="HJJE" alias="金额" type="double" length="10" width="60" precision="2"/>
	<item id="ZFBL" alias="自负比例" type="double" length="6" precision="3" not-null="1"/>
	<item id="YPYF" alias="频次" type="string" width="60"/>
	<item id="ZFYP" alias="转方药品" length="1" type="int" display="0" not-null="1"/>
</entry>
