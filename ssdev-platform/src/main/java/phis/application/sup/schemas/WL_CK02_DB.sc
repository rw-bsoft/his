<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_CK02_DB" tableName="WL_CK02" alias="出库明细(WL_CK02)">
    <item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
        <key>
            <value name="increaseId" type="increase" startPos="24"/>
        </key>
    </item>
    <item id="DJXH" alias="单据序号" type="long" display="0" length="18"/>
    <item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
    <item id="WZMC" alias="物资名称" mode="remote" virtual="true" width="200"/>
    <item id="WZGG" alias="物资规格" fixed="true" width="120" virtual="true"/>
    <item id="WZDW" alias="物资单位" fixed="true" virtual="true"/>
    <item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
    <item id="TJCKSL" alias="推荐出库数" type="double" width="90" virtual="true" fixed="true" length="18" precision="2"/>
    <item id="WZSL" alias="调拨数量" type="double" length="18" precision="2"/>
    <item id="WZJG" alias="价格" type="double" fixed="true" display="0" length="18" precision="4"/>
    <item id="WZJE" alias="金额" type="double" fixed="true" display="0" length="18" precision="4"/>
    <item id="LSJG" alias="零售价格" type="double" length="18" precision="4" fixed="true" display="0"/>
    <item id="LSJE" alias="零售金额" type="double" length="18" precision="4" fixed="true" display="0"/>
    <item id="WZPH" alias="批号" fixed="true" length="20" display="0"/>
    <item id="SCRQ" alias="生产日期" fixed="true" type="date" display="0"/>
    <item id="SXRQ" alias="失效日期" fixed="true" type="date" display="0"/>
    <item id="MJPH" alias="灭菌批号" fixed="true" length="20" display="0"/>
    <item id="GLXH" alias="关联序号" type="long" display="0" length="18"/>
    <item id="THMX" alias="退回明细" type="long" display="0" length="18"/>
    <item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
    <item id="ZBXH" alias="帐薄序号" type="long" display="0" length="18"/>
    <item ref="b.GLFS" alias="管理方式" display="0" />
</entry>
