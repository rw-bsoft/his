<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_RK01" alias="入库单据(WL_RK01)" sort="DJXH desc">
    <item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" type="increase" startPos="24" />
        </key>
    </item>
    <item id="JGID" alias="机构ID" display="0" type="string" length="20"/>
    <item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
    <item id="ZBLB" alias="帐薄类别" type="int" display="0" length="8"/>
    <item id="LZDH" alias="流转单号" length="30" width="128"/>
    <item id="LZFS" alias="流转方式" type="long" length="12" width="120">
        <dic id="phis.dictionary.transfermodes"  autoLoad="true">
        </dic>
    </item>
    <item id="DWXH" alias="供货单位" type="long" length="12" width="170">
        <dic id="phis.dictionary.supplyUnit" filter="['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']]" autoLoad="true">
        </dic> 
    </item>
    <item id="RKRQ" alias="入库日期" type="date"/>
    <item id="JBGH" alias="经办人员" length="10">
        <dic id="phis.dictionary.doctor" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  autoLoad="true">
        </dic>
    </item>
    <item id="JGFS" alias="加工方式" type="int" length="2" display="0"/>
    <item id="DJJE" alias="单据金额" type="double" length="18" precision="4"/>
    <item id="YFJE" alias="应付金额" type="double" length="18" precision="4" display="0"/>
    <item id="FKJE" alias="付款金额" type="double" length="18" precision="4" display="0"/>
    <item id="FPBZ" alias="发票标志" type="int" length="1" display="0"/>
    <item id="DJLX" alias="单据类型" type="int" length="1" display="0"/>
    <item id="ZDRQ" alias="制单日期" type="date" display="0"/>
    <item id="ZDGH" alias="制单人员" length="10" display="0"/>
    <item id="SHRQ" alias="审核日期" type="date" display="0"/>
    <item id="SHGH" alias="审核人员" length="10" display="0"/>
    <item id="JZRQ" alias="记帐日期" type="date" display="0"/>
    <item id="JZGH" alias="记帐人员" length="10" display="0"/>
    <item id="DJZT" alias="单据状态" type="int" length="1">
        <dic>
            <item key="0" text="制单"/>
            <item key="1" text="审核"/>
            <item key="2" text="记账"/>
        </dic>
    </item>
    <item id="THDJ" alias="退回单据" type="long" length="18" defaultValue="0" display="0"/>
    <item id="DJBZ" alias="单据备注" length="160" display="0"/>
    <item id="PDDJ" alias="盘点单据" type="long" length="18" display="0"/>
    <item id="DRBZ" alias="导入标志" type="int" length="1" display="0"/>
    <item id="CWBH" alias="财务编号" type="long" length="18" display="0"/>
</entry>
