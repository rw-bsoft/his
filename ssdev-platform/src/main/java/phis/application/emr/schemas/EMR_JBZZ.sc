<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_JBZZ" alias="疾病治则关联">
    <item id="GLBS" alias="关联标识" type="long" length="9" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" startPos="24" />
        </key>
    </item>
    
    <item ref="b.ZHMC" mode="remote" width="200"/>
    
    <item id="JBBS" alias="疾病标识" type="long" length="9" display="0"/>
    <item id="ZHBS" alias="证侯标识" type="long" length="9" display="0"/>
    <item id="ZZBS" alias="治则标识" type="long" length="9" display="0"/>
    <item id="BBXX" alias="备注信息" type="string" length="255" width="200"/>
    <relations>
        <relation type="parent" entryName="phis.application.emr.schemas.EMR_ZYZH"/>
    </relations>
</entry>
