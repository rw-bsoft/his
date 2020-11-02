<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_RK01_CX" tableName="WL_RK01"  alias="入库单据(WL_RK01)" sort="LZDH desc,ZDRQ desc">
	<item id="LZFS" alias="流转方式" type="long" length="12" queryable="true" >
		<dic id="phis.dictionary.transfermodes" filter = "['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],['eq',['$','item.properties.DJLX'],['s','RK']]]">
		</dic>
	</item>
	<item id="LZDH" alias="流转单号" length="30" width="128" queryable="true"/>
	<item ref="d.WZMC" alias="物资名称" queryable="true" width ="150"/>
	<item ref="d.WZGG" alias="物资规格"/>
	<item ref="d.CJXH" alias="厂家序号"/>
	<item ref="d.WZDW" alias="物资单位"/>
	<item ref="c.WZSL" alias="物资数量"/>
	<item ref="c.WZJG" alias="物资价格"/>
	<item ref="c.WZJE" alias="物资金额"/>
	<item ref="c.WZPH" alias="物资批号"/>
	<item id="DWXH" alias="供货单位" type="long" length="12" width ="150">
		<dic id="phis.dictionary.supplyUnit" filter="['and',['eq', '$','item.properties.DWZT'], ['i', 1]],['or',['eq', '$','item.properties.KFXH'],['$', '%user.properties.treasuryId']],['eq', '$','item.properties.KFXH'],['i', 0]]]]" autoLoad="true" searchField="PYDM">
		</dic> 
	</item>
	<relations>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_GHDW">
			<join parent="DWXH" child="DWXH"></join>
	  </relation>
		<relation type="child" entryName="phis.application.sup.schemas.WL_RK02">
			<join parent="DJXH" child="DJXH"></join>
	  </relation>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD">
			<join parent="WZXH" child="WZXH"></join>
	  </relation>
	  
	</relations>
</entry>
