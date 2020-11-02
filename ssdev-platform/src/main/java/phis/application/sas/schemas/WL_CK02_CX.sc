<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CK02_CX" tableName="WL_CK02" alias="出库明细(WL_CK02)">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
    <item ref="b.LZFS" alias="流转方式" type="long" length="12" queryable="true">
	</item>
	<item ref="b.LZDH" alias="流转单号" length="30" queryable="true" width="150"/>
	<item ref="c.WZMC" alias="物资名称" queryable="true" width="150" />
	<item ref="c.WZGG" alias="物资规格"/>
	<item ref="c.WZDW" alias="物资单位"/>
	<item ref="d.CJMC" alias="生产厂家" queryable="true"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="价格" type="double" fixed="true" display="0" length="18" precision="4"/>
	<item id="WZJE" alias="金额" type="double" fixed="true" display="0" length="18" precision="4"/>
	<item ref="b.KSDM" alias="科室" length="30" display="1" queryable="true">
	</item>
	<item id="WZPH" alias="批号" fixed="true" length="20" display="0" queryable="true"/>
	<relations>
	  <relation type="child" entryName="phis.application.sas.schemas.WL_CK01_FOR_CKMXfor" >
		<join parent="DJXH" child="DJXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
		<join parent="WZXH" child="WZXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_SCCJ" >
		<join parent="CJXH" child="CJXH"></join>
	  </relation>
  </relations>
</entry>
