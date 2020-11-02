<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_RK01_EJ_CX" tableName="WL_RK01"  alias="入库单据(WL_RK01)">
	<item id="LZFS" alias="流转方式" type="long" length="12" queryable="true">
		 <dic id="phis.dictionary.transfermodes" filter = "['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],['eq',['$','item.properties.DJLX'],['s','RK']]]">
		</dic>
	</item>
	<item id="LZDH" alias="流转单号" length="30" width="128" queryable="true"/>
	<item id="WZMC" alias="物资名称" virtual="true" queryable="true"  width="150"/>
	<item id="WZGG" alias="物资规格" virtual="true"/>
	<item id="CJMC" alias="厂家名称" virtual="true" width="160"/>
	<item id="WZDW" alias="物资单位" virtual="true"/>
	<item id="WZSL" alias="物资数量" virtual="true" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="物资金额" virtual="true" type="double" length="18" precision="2"/>
	<item id="WZJE" alias="物资价格" virtual="true" type="double" length="18" precision="2"/>
	<item id="WZPH" alias="物资批号" virtual="true"/>
</entry>
