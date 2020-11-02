<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZK02" alias="转科明细(WL_ZK02)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
    
    <item id="WZBH" alias="物资编号" fixed="false" display="1" width="120" length="40"/>
    <item ref="b.WZMC" fixed="false"/>
    <item id="WZGG" ref="b.WZGG" fixed="false"/>
    <item id="WZDW" ref="b.WZDW" fixed="false"/>
    <item id="GLFS" ref="b.GLFS" fixed="false" display="1">
        <dic>
            <item key="1" text="库存管理"/>
            <item key="2" text="科室管理"/>
            <item key="3" text="台账管理"/>
        </dic>
    </item>
    <item id="TJSL" alias="推荐数量" type="double" length="18" precision="2" virtual="true" fixed="true"/>
    <item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
    <item id="WZJG" alias="物资价格" type="double" length="18" precision="4"/>
    <item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
    <item id="CJMC" ref="c.CJMC" fixed="true"/>
    <item id="WZPH" alias="物资批号" length="20"/>
    <item id="SCRQ" alias="生产日期" type="date"/>
    <item id="SXRQ" alias="失效日期" type="date"/>
    
    <item id="ZBXH" alias="帐薄序号" type="long" length="18" display="0"/>
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0"/>
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12" display="0"/>
	<item id="KCXH" alias="库存序号" type="long" length="18" display="0"/>
    <relations>
        <relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" >
            <join parent="WZXH" child="WZXH"></join>
        </relation> 
        <relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
            <join parent="CJXH" child="CJXH"></join>
        </relation>
    </relations>
</entry>
