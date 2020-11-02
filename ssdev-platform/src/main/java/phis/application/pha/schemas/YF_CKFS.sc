<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_CKFS" alias="出库方式" sort="a.CKFS desc">
  <item id="JGID" alias="机构ID" length="20" not-null="1" display="0" type="string" update="false" defaultValue="%user.manageUnit.id" />
  <item id="YFSB" alias="药房识别" length="18" not-null="1" defaultValue="%user.properties.pharmacyId" update="false"  display="0" pkey="true" type="long"/>
  <item id="CKFS" alias="方式代码" length="4" not-null="1"  display="0"  generator="assigned" pkey="true" type="int">
  	<key>
      <rule name="increaseId" type="increase" length="16" startPos="21"/>
    </key>
  </item>
  <item id="FSMC" alias="方式名称" type="string" width="200" not-null="1" length="20" />
  <item id="CKDH" alias="出库单号" length="8" type="int" width="150" not-null="1" />
  <item id="PYDM" alias="拼音代码" type="string" width="150" queryable="true"  selected="true" length="10" target="FSMC" codeType="py"/>	
</entry>
