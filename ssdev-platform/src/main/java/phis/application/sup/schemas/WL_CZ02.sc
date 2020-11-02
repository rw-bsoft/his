<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CZ02" alias="重置明细(WL_CZ02)">
  <item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
	  <key>
		 <rule name="increaseId" type="increase" startPos="24" />
	  </key>
  </item>
  <item id="DJXH" alias="单据序号" type="long" length="18" display="0" />
  <item id="WZXH" alias="物品序号" type="long" length="18" display="0"/>
  <item id="WZBH" alias="资产编号" length="30" fixed="false"/>
  <item ref="b.WZMC" alias="物资名称" length="60" fixed="false" width="150"/>
  <item ref="b.WZGG" alias="规格" length="60" fixed="false"/>
  <item ref="b.GLFS" alias="管理方式" length="60" fixed="false"/>
  <item ref="b.WZDW" alias="单位" length="60" fixed="false"/>
  <item id="ZCYZ" alias="资产原值" type="double" length="18" precision="4" fixed="false"  width="100"/>
  <item id="ZCXZ" alias="增/减现值" type="double" length="18" precision="4" defaultValue="0" width="100"/>
  <item ref="c.CJMC" alias="厂家名称" length="60"  fixed="false"  width="160"/>
  <item id="KCXH" alias="库存序号" virtual="true" display="0"/>
  <item id="CJXH" alias="厂家序号" type="long" length="12" display="0"/>
  <item id="ZBXH" alias="帐薄序号" type="long" length="18" display="0"/>
  <item id="ZYKS" alias="在用科室" type="long" length="18" fixed="false">
 	<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" >
	</dic>
  </item>
   <relations>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
		<join parent="WZXH" child="WZXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_SCCJ" >
		<join parent="CJXH" child="CJXH"></join>
	  </relation>
  </relations>
</entry>
