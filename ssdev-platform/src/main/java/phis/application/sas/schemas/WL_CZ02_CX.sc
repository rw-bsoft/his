<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CZ02_CX" tableName="WL_CZ02" alias="重置明细(WL_CZ02)" sort="c.LZDH desc">
  <item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
	  <key>
		 <rule name="increaseId" type="increase" startPos="24" />
	  </key>
  </item>
  <item ref="c.LZDH" alias="流转单号" length="60" queryable="true" width ="150"/>
  <item ref="c.LZFS" alias="流转方式" length="60">
  	 <dic id="phis.dictionary.transfermodes" />
  </item>
  <item ref="b.WZMC" alias="物资名称" length="60" queryable="true" width ="150"/>
  <item ref="b.WZGG" alias="规格" length="60"/>
  <item ref="b.WZDW" alias="单位" length="60"/>
  <item id="ZCYZ" alias="资产原值" type="double" length="18" precision="4"/>
  <item ref="c.CZFS" alias="重置方式" length="60"/>
  <item id="ZCXZ" alias="增/减值" type="double" length="18" precision="4" defaultValue="0"/>
   <relations>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
		<join parent="WZXH" child="WZXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.sup.schemas.WL_CZ01" >
		<join parent="DJXH" child="DJXH"></join>
	  </relation>
  </relations>
</entry>
