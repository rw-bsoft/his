<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_ZK02_CX" tableName="WL_ZK02" alias="转科明细(WL_ZK02)" sort="b.LZDH desc">
    <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" type="increase" startPos="24" />
        </key>
    </item>
    <item ref="b.LZFS" alias="流转方式" type="long" length="12" >
        <dic id="phis.dictionary.transfermodes" />
    </item>
    <item ref="b.LZDH" alias="流转单号" length="30" queryable="true" width ="150"/>
    <item ref="c.WZMC" alias="物资名称" fixed="false" queryable="true" width ="150"/>
    <item ref="c.WZGG" alias="物资规格" fixed="false"/>
    <item ref="c.WZDW" alias="物资单位" fixed="false"/>
    <item ref="d.CJMC" alias="生产厂家" fixed="true" width ="150"/>
    <item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
    <item id="WZJG" alias="物资价格" type="double" length="18" precision="4"/>
    <item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
    <item ref="b.ZCKS" alias="转出科室" fixed="true" queryable="true"/>
    <item ref="b.ZRKS" alias="转入科室" fixed="true" queryable="true"/>
    <item ref="b.DJZT" alias="单据状态" display="0"/>
    <relations>
        <relation type="parent" entryName="phis.application.sup.schemas.WL_ZK01" >
            <join parent="DJXH" child="DJXH"></join>
        </relation> 
        <relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" >
            <join parent="WZXH" child="WZXH"></join>
        </relation> 
        <relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
            <join parent="CJXH" child="CJXH"></join>
        </relation>
    </relations>
</entry>
