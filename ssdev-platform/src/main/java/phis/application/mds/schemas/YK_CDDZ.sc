<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CDDZ" alias="进货单位" sort="a.YPCD desc">
  <item id="YPCD" alias="药品产地" type="long" length="18"  not-null="1" generator="assigned" pkey="true" display="0">
    <key>
      <rule name="increaseId" type="increase" length="16" startPos="2723"/>
    </key>
  </item>
   <item id="CDQC" alias="产地全称" type="string"  width="200"  length="60" not-null="true"  />
  <item id="CDMC" alias="产地简称" type="string"  width="130"  length="15" not-null="false"  />
  <item id="PYDM" alias="拼音代码" type="string" length="10" queryable="true" selected="true" target="CDMC" codeType="py" />
</entry>
