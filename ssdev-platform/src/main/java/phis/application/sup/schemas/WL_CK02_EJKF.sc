<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CK02_EJKF" tableName="WL_CK02" alias="出库明细(WL_CK02)">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="DJXH" alias="单据序号" type="long" display="0" length="18"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
	<item ref="b.WZMC" mode="remote" width="120"/>
	<item ref="b.WZGG" fixed="true"/>
	<item ref="b.GLFS" fixed="true" display="0"/>
	<item ref="b.WZDW" fixed="true"/>
	<item id="CJMC" alias="厂家名称" type="string" fixed="true" virtual="true"/>
	<item id="CJXH" alias="厂家" type="long" length="12" width="120" fixed="true" display="0"/>
	<item id="TJCKSL" alias="推荐出库数" type="double" width="90" virtual="true" fixed="true" length="18" precision="2"/>
	<item id="KTSL" alias="可退数量" type="double" length="18" precision="2"  virtual="true" fixed="false"/>
	<item id="WZSL" alias="数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="进货价格" type="double" fixed="true" length="18" precision="4"/>
	<item id="WZJE" alias="进货金额" type="double" fixed="true" length="18" precision="4"/>
	<item id="LSJG" alias="零售价格" type="double" length="18" precision="4" fixed="true"/>
	<item id="LSJE" alias="零售金额" type="double" length="18" precision="4" fixed="true"/>
	<item id="WZPH" alias="批号" type="string" fixed="true" length="20"/>
	<item id="SCRQ" alias="生产日期" fixed="true" type="date"/>
	<item id="SXRQ" alias="失效日期" fixed="true" type="date"/>
	<item id="MJPH" alias="灭菌批号" type="string" fixed="true" length="20"/>
	<item id="GLXH" alias="关联序号" type="long" display="0" length="18"/>
	<item id="THMX" alias="退回明细" type="long" display="0" length="18"/>
	<item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
	<item id="ZBXH" alias="帐薄序号" type="long" display="0" length="18"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
	</relations>
</entry>
