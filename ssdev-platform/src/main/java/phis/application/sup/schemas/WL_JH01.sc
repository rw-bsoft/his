<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_JH01" alias="计划单据(WL_JH01)">
    <item id="DJXH" alias="单据序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    
    <item id="LZDH" alias="流转单号" length="30" width="150"/>
    <item id="LZFS" alias="流转方式" type="long" length="12">
        <dic id="phis.dictionary.transfermodes" autoLoad="true">
        </dic>  
    </item>
    <item id="ZDRQ" alias="制单日期" type="date"/>
    <item id="ZDGH" alias="制单人员" length="10">
        <dic id="phis.dictionary.doctor" autoLoad="true">
        </dic>
    </item>
    <item id="SHRQ" alias="审核日期" type="date"/>
    <item id="SHGH" alias="审核人员" length="10">
        <dic id="phis.dictionary.doctor" autoLoad="true">
        </dic>
    </item>
    <item id="DJZT" alias="单据状态" type="int" length="1">
        <dic>
            <item key="0" text="制单"/>
            <item key="1" text="审核"/>
        </dic>
    </item>
    
    <item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
    <item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
    <item id="DJBZ" alias="单据备注" length="160" display="0"/>
</entry>
