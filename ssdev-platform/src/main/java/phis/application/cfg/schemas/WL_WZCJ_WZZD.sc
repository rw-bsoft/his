<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZCJ_WZZD" tableName="WL_WZCJ" alias="物资厂家(WL_WZCJ)">
	<item id="WZXH" alias="物资序号" type="long" length="18"  generator="assigned" pkey="true" display="0">
	</item>
	<item id="CJXH" alias="厂家名称" type="long" length="12" width="280" pkey="true" not-null="1">
		<dic id="phis.dictionary.WL_SCCJ_WZZD" filter = "['and',['or',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],['eq',['$','item.properties.KFXH'],['i',0]]],['eq',['$','item.properties.CJZT'],['i',1]]]" searchField="PYDM"  autoLoad="true"/>
	</item>
	<item id="WZJG" alias="购入价格" type="double" length="18" precision="4"/>
	<item id="SYZT" alias="使用状态" type="int" defaultValue="1" length="1">
		<dic autoLoad="true"> 
			<item key="-1" text="注销"/>
			<item key="1" text="在用"/>
		</dic>
	</item>
	<item id="LSJG" alias="零售价格" type="double" length="18" precision="4"/>
	<item id="JGBL" alias="价格比例%" width="100" type="double" length="18" precision="4"/>
	<item ref="b.QYXZ" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" />
	</relations>
</entry>
