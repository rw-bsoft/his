<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZCMX" alias="明细帐册(WL_ZCMX)">
    <item id="MXXH" alias="明细序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" type="increase" startPos="24" />
        </key>
    </item>
    <item id="JGID" alias="机构ID" type="string" length="20"/>
    <item id="KFXH" alias="库房序号" type="int" length="8"/>
    <item id="ZBLB" alias="帐薄类别" type="int" length="8"/>
    <item id="DJXH" alias="单据序号" type="long" length="18"/>
    <item id="LZFS" alias="流转方式" type="long" length="12"/>
    <item id="YWLB" alias="业务类别" type="int" length="1"/>
    <item id="DJLX" alias="单据类型" length="6"/>
    <item id="FSRQ" alias="发生日期" type="date"/>
    <item id="MXJL" alias="明细记录" type="long" length="18"/>
    <item id="GLFS" alias="管理方式" type="int" length="1"/>
    <item id="WZXH" alias="物资序号" type="long" length="18"/>
    <item id="CJXH" alias="厂家序号" type="int" length="8"/>
    <item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
    <item id="WZJG" alias="物资价格" type="double" length="18" precision="4"/>
    <item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
    <item id="LSJG" alias="零售价格" type="double" length="18" precision="4"/>
    <item id="LSJE" alias="零售金额" type="double" length="18" precision="4"/>
    <item id="WZPH" alias="物资批号" length="20"/>
    <item id="MJPH" alias="灭菌批号" length="20"/>
    <item id="SCRQ" alias="生产日期" type="date"/>
    <item id="SXRQ" alias="失效日期" type="date"/>
    <item id="KCXH" alias="库存识别" type="long" length="18"/>
    <item id="ZBXH" alias="帐薄序号" type="long" length="18"/>
    <item id="YWFS" alias="业务方式" type="int" length="4"/>
    <item id="KSDM" alias="科室代码" type="long" length="18"/>
    <item id="YWGH" alias="业务人员" length="10"/>
    <item id="MXSM" alias="明细说明" length="160"/>
    <item id="CWYF" alias="财务月份" length="6"/>
</entry>
