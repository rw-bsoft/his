<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="WL_BS02" alias="报损明细(WL_BS02)">
	<item ref="c.FRDB" fixed="false"  alias="资产编号" width="200"/>
	<item ref="b.WZMC" alias="物资名称" width="200" fixed="false"/>
	<item ref="b.WZGG" alias="规格" fixed="false"/>
	<item ref="b.WZDW" alias="单位" fixed="false"/>
	<item id="TJSL" alias="推荐数量" fixed="false" type="double" length="18"  virtual="true"  precision="2"/>
	<item id="WZSL" alias="报损数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="单价" type="double" length="18" precision="4"  fixed="false"/>
	<item id="WZJE" alias="金额" type="double" length="18" precision="4" fixed="false"/>
	<item ref="b.GLFS" alias="管理方式" display="0" type="int"  length="1" fixed="false"/>
	<item ref="c.CJMC" fixed="false" width="200"/>
	<item id="WZPH" alias="物资批号" length="20" fixed="false" />
	<item id="SCRQ" alias="生产日期" type="date" fixed="false" />
	<item id="WZXH" alias="物品序号"  display="0" type="long" length="18"/>
	<item id="DJXH" alias="单据序号"  display="0" type="long" length="18"/>
	<item id="JLXH" alias="记录序号"  display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="ZBXH" alias="账簿序号" width="120"  display="0"  type="long" length="18"/>
	<item id="CJXH" alias="厂家序号" display="0" type="long" length="12"/>
	<item id="SXRQ" alias="失效日期" type="date" fixed="false" />
	<item id="KCXH" alias="库存序号" display="0"  type="long" length="18"/>
  
  
  
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" />
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>  
	</relations>
</entry>
