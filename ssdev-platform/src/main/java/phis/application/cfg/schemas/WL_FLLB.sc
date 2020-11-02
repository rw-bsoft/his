<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_FLLB" alias="分类类别">
    <item id="LBXH" alias="类别序号" type="int" length="8" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    <item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
    <item id="LBMC" alias="类别名称" length="30" not-null="1" width="120"/>
    
    <!-- <item id="ZFBZ" alias="注销判别" type="int" length="1" defaultValue="1" renderer="onRenderer" display="1"/>-->
    <item id="ZFBZ" alias="注销判别" type="int" length="1" defaultValue="1" renderer="onRenderer" display="1">
        <dic>
            <item key="1" text="启用"/>
            <item key="-1" text="注销"/>
        </dic>
    </item>
    <item id="SYBZ" alias="默认使用" type="int" length="1" display="0"/>
</entry>
