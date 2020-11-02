<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_FLGZ" alias="分类规则">
    <item id="GZXH" alias="规则序号" type="int" length="8" not-null="1" generator="assigned" pkey="true" display="0">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    <item id="LBXH" alias="类别序号" type="int" length="8" display="0"/>
    <item id="GZCC" alias="规则层次" type="int" length="2" fixed="false"/>
    <item id="GZMC" alias="规则名称" length="30" not-null="1"/>
    <item id="GZCD" alias="规则长度" type="int" length="2" defaultValue="2"/>
    
</entry>
