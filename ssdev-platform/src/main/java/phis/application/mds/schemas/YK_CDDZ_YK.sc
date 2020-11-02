<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CDDZ" alias="进货单位"  >
  <item id="YPCD" alias="药品产地"  length="18"  not-null="1" generator="assigned" pkey="true" display="0">
    <key>
      <rule name="increaseId" type="increase" length="16" startPos="1"/>
    </key>
  </item>
   <item id="CDQC" alias="产地全称" type="string"  width="300" anchor="100%" length="60" not-null="true" colspan="3"/>
  <item id="CDMC" fixed="true" alias="产地简称" type="string"  width="100" anchor="100%" length="15" not-null="true" colspan="2"/>
  <item id="PYDM" alias="拼音代码" type="string" length="10" mode = "remote">
  </item>
</entry>
