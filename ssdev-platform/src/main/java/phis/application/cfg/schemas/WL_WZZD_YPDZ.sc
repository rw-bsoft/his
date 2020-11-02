<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZZD_YPDZ" alias="物资字典(WL_WZZD)药品对照" tableName="WL_WZZD">
    <item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
    </item>
    <item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
    <item id="WZMC" alias="物资名称" length="80" width="160"/>
     <item id="WZGG" alias="物资规格" length="40"/>
     <item id="WZDW" alias="物资单位" length="10" />
    <item id="PYDM" alias="拼音代码" display="0"  length="10" queryable="true"/>
    <item id="SFBZ" alias="库房序号" type="int" length="8" display="0" />
   <item id="SFBZ" alias="可收费标志" type="int" length="1" defaultValue="0" display="0" >
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
	</item>
</entry>
