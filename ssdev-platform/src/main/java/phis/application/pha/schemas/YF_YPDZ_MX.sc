<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YF_RK01"  alias="药房管理_库存对账" >
	<item id="YPXH" alias="药品序号" length="18"  not-null="1" hidden="true" type="long"/>
	<item id="DAY" width="140" alias="日期" />
	<item id="REMARK" alias="备注" type="string" width="200" not-null="1" length="10"/>
	<item id="RKFS" alias="方式代码" length="4" not-null="1"  display="0"  generator="assigned"  pkey="true" type="int">
	  	<key>
	      <rule name="increaseId" type="increase" length="16" startPos="18"/>
	    </key>
	</item>
	<item id="YPSL" alias="数量" length="10" type="double" precision="2" not-null="1" width="100" min="0" max="999999.99"  renderer="onRenderer_two"/>
	<item id="LSJG" alias="单价" length="12" precision="4" type="double" not-null="1" width="100" fixed="true" renderer="onRenderer_four"/>
	<item id="LSJE" alias="金额" length="12" precision="4" type="double" not-null="1" width="100" fixed="true" renderer="onRenderer_four"/>
</entry>