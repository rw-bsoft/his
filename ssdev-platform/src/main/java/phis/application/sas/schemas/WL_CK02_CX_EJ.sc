<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CK02_CX_EJ" tableName="WL_CK02" alias="出库明细(WL_CK02)"  sort="b.LZDH desc">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item ref="b.LZFS" alias="流转方式" type="long" length="12" queryable="true">
	</item>
	<item ref="b.LZDH" alias="流转单号" length="30" queryable="true" width="150"/>
    <item ref="b.CKRQ" alias="出库日期" type="date" width="120"/>
    <item id="BRXM" alias="病人姓名" length="30" virtual="true" queryable="true"/>
    <item id="BRLY" alias="病人来源" length="10" virtual="true" queryable="true">
        <dic>
            <item key="1" text="门诊"/>
            <item key="2" text="住院"/>
        </dic>
    </item>
	<item ref="c.WZMC" alias="物资名称" queryable="true" width="150" />
	<item ref="c.WZGG" alias="物资规格"/>
	<item ref="c.WZDW" alias="物资单位"/>
	<item ref="d.CJMC" alias="生产厂家" queryable="true" width="160"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="价格" type="double" fixed="true" display="0" length="18" precision="4"/>
	<item id="WZJE" alias="金额" type="double" fixed="true" display="0" length="18" precision="4"/>
	<item ref="b.KSDM" alias="科室" length="30" display="1" queryable="true">
		 <dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true" searchField="PYDM">
        </dic>
	</item>
	<item id="WZPH" alias="批号" fixed="true" length="20" display="0"/>
	<relations>
	  <relation type="child" entryName="phis.application.sas.schemas.WL_CK01_FOR_EJCX" >
		<join parent="DJXH" child="DJXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
		<join parent="WZXH" child="WZXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_SCCJ" >
		<join parent="CJXH" child="CJXH"></join>
	  </relation>
	  <relation type="child" entryName="phis.application.cfg.schemas.WL_LZFS" >
		<join parent="b.LZFS" child="FSXH"></join>
	  </relation>
  </relations>
</entry>
