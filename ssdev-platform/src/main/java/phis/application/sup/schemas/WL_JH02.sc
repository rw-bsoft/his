<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_JH02" alias="计划明细(WL_JH02)">
    <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    
    <item id="WZMC" alias="物资名称" length="60" width="160" mode="remote"/>
    <item id="WZGG" alias="物资规格" length="60" fixed="false"/>
    <item id="WZDW" alias="物资单位" length="10" fixed="false"/>
    <item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
    <item id="KSDM" alias="申请科室" type="long" length="18" fixed="false">
        <dic id="phis.dictionary.department" autoLoad="true">
        </dic>
    </item>
    <item id="SLSJ" alias="申领时间" type="date" fixed="false" width="160"/>
    
    <item id="DJXH" alias="单据序号" type="long" length="18" display="0"/>
    <item id="ZBLB" alias="帐薄类别" type="int" length="8" display="0"/>
    <item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
    <item id="CJXH" alias="厂家序号" type="long" length="12" display="0"/>
    <item id="SLXH" alias="申领序号" type="long" length="18" display="0"/>
    <item id="ZDBZ" alias="制单标志" type="int" length="1" display="0"/>
    <item id="SQLY" alias="申请原因" length="250" display="0"/>
    <item id="DJLX" alias="单据类型" type="int" length="1" display="0"/>
</entry>
