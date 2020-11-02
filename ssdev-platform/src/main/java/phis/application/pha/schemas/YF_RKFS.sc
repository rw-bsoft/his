<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_RKFS" alias="入库方式" sort="a.RKFS desc">
  <item id="JGID" alias="机构ID" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id" update="false"/>
  <item id="YFSB" alias="药房识别" length="18" not-null="1" display="0" defaultValue="%user.properties.pharmacyId" pkey="true" type="long" update="false"/>
  <item id="RKFS" alias="方式代码" length="4" not-null="1"  display="0"  generator="assigned"  pkey="true" type="int">
  	<key>
      <rule name="increaseId" type="increase" length="16" startPos="18"/>
    </key>
  </item>
  <item id="FSMC" alias="方式名称" type="string" width="200" not-null="1" length="20"/>
  <item id="RKDH" alias="入库单号" length="8" width="150" type="int" not-null="1" />
  <item id="PYDM" alias="拼音代码" type="string" width="150" length="10" queryable="true"  selected="true" target="FSMC" codeType="py"/>
</entry>
