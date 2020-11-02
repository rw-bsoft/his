<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_KCFL" alias="库存分裂">
  <item id="JGID" alias="机构ID" length="20" not-null="1" type="string"/>
  <item id="XTSB" alias="药库识别" length="18" not-null="1" type="long"/>
  <item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"/>
  <item id="FLSB" alias="分裂识别" length="18" not-null="1" generator="assigned" pkey="true" type="long">
  </item>
  <item id="KCSL" alias="库存数量" length="10" precision="4" not-null="1" type="double"/>
  <item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" type="double"/>
  <item id="PFJE" alias="批发金额" length="12" precision="4" not-null="1" type="double"/>
  <item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double"/>
</entry>
