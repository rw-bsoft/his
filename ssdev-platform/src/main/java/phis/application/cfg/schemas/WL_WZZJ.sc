<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZZJ" alias="物资证件(WL_WZZJ)">
  <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" type="increase" startPos="1" />
        </key>
  </item>
  <item id="WZXH" alias="物资序号" type="long" length="18" not-null="1"/>
  <item id="CJXH" alias="厂家序号" type="long" length="12" not-null="1"/>
  <item id="ZJXH" alias="证件序号" type="long" length="12"/>
</entry>
