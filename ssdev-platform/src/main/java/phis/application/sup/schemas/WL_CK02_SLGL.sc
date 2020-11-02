<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_CK02_SLGL" tableName="WL_CK02" alias="出库明细(WL_CK02)">
    <item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
        <key>
            <value name="increaseId" type="increase" startPos="24"/>
        </key>
    </item>
    <item id="DJXH" alias="单据序号" type="long" display="0" length="18"/>
    <item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
    
    <item ref="b.WZMC" fixed="true" width="140"/>
    <item ref="b.WZGG" fixed="true"/>
    <item ref="b.WZDW" fixed="true"/>
    <item ref="b.GLFS" fixed="true"/>
    <item id="TJSL" alias="推荐数量" type="double" width="90" virtual="true" fixed="true" length="18" precision="2"/>
    <item id="WZSL" alias="实发数量" type="double" length="18" precision="2"/>
    <item id="WZJG" alias="价格" type="double" fixed="true" length="18" precision="4"/>
    <item id="WZJE" alias="金额" type="double" fixed="true" length="18" precision="4"/>
    <item id="LSJG" alias="零售价格" type="double" length="18" precision="4" fixed="true"/>
    <item id="LSJE" alias="零售金额" type="double" length="18" precision="4" fixed="true"/>
    <item id="SLSL" alias="申领数量" type="double" length="18" precision="2" fixed="true"/>
    <item id="WFSL" alias="未发数量" type="double" length="18" precision="2" fixed="true" />
    <item ref="c.SLSJ" alias="申领时间" type="date" virtual="true" fixed="true"/>
    
    <item id="SLXH" alias="申领序号" type="long" length="18" display="0" />
    <item id="JHBZ" alias="计划标志" type="int" length="1" display="0" defaultValue="0"/>
    <item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
    <item id="WZPH" alias="批号" fixed="true" length="20" display="0"/>
    <item id="SCRQ" alias="生产日期" fixed="true" type="date" display="0"/>
    <item id="SXRQ" alias="失效日期" fixed="true" type="date" display="0"/>
    <item id="MJPH" alias="灭菌批号" fixed="true" length="20" display="0"/>
    <item id="GLXH" alias="关联序号" type="long" display="0" length="18"/>
    <item id="THMX" alias="退回明细" type="long" display="0" length="18"/>
    <item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
    <item id="ZBXH" alias="帐薄序号" type="long" display="0" length="18"/>
    <relations>
        <relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD_JBXX" >
            <join parent="WZXH" child="WZXH"></join>
        </relation>
    </relations>
    <relations>
        <relation type="children" entryName="phis.application.sup.schemas.WL_SLXX_DETAIL" >
            <join parent="SLXH" child="JLXH"></join>
        </relation>
    </relations>
</entry>
