<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JH02_CX" tableName="WL_JH02" alias="计划明细(WL_JH02)" sort="b.LZDH desc">
    <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    <item ref="b.LZDH" alias="流转单号" length="30" width="150" queryable="true"/>
    <item ref="b.LZFS" alias="流转方式" type="long" length="12">
        <dic id="phis.dictionary.transfermodes" autoLoad="true">
        </dic>  
    </item>
    <item id="WZMC" alias="物资名称" length="60" width="160" mode="remote" queryable="true"/>
    <item id="WZGG" alias="物资规格" length="60" fixed="false"/>
    <item id="WZDW" alias="物资单位" length="10" fixed="false"/>
    <item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
    <item id="KSDM" alias="申请科室" type="long" length="18" fixed="false">
        <dic id="phis.dictionary.department" autoLoad="true">
        </dic>
    </item>
    <item id="SLSJ" alias="申领时间" type="date" fixed="false"/>
     <relations>
        <relation type="parent" entryName="phis.application.sup.schemas.WL_JH01" >
            <join parent="DJXH" child="DJXH"></join>
        </relation> 
    </relations>
</entry>
