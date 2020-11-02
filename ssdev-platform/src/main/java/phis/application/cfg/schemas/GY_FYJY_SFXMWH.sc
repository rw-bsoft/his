<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_FYJY_SFXMWH" tableName="GY_FYJY" alias="费用禁用">
  <item id="BRXZ" alias="病人性质" display="0" type="long" length="18" not-null="1"/>
  <item id="XZMC" alias="性质名称" width="160" type="string" not-null="1" length="30" fixed="true"/>
  <item id="FYXH" alias="费用序号" length="18" type="long" display="0"  not-null="1"/>
  <item id="ZFBL" alias="自负比例%" type="double" max="100" min="0" width="90" length="6" precision="1" not-null="1"/>
  <item id="FYXE" alias="费用限额" length="8" display="0" type="double" min="0" precision="2" not-null="1"/>
  <item id="CXBL" alias="超限比例%" type="double" display="0" max="100" min="0" width="90" length="6" precision="1"/>
</entry>
