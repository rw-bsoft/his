<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YPJY_BRXZ" alias="药品禁用" tableName="GY_YPJY">
  <item id="BRXZ" alias="病人性质" type="long" length="18" display="0" not-null="1" pkey="true"/>
  <item id="YPXH" alias="药品序号" type="long" display="0" length="18" not-null="1" pkey="true"/>
  <item ref="b.YPMC" alias="药品名称" type="string" summaryType="count" mode="remote" width="250" anchor="100%" summaryRenderer="totalYPSL" length="50" not-null="1"/>
  <item ref="b.PYDM" queryable="true" display="0"/>
  <item ref="b.YPGG" alias="药品规格" type="string" length="18" width="150" fixed="true"/>
  <item ref="b.YPDW" alias="药品单位" type="string" length="18" fixed="true"/>
  <item id="ZFBL" alias="自负比例%" type="double" nullToValue="0" precision="1" min="0"  max="100" width="90" length="6" not-null="1"/>
  <item id="YPXE" alias="药品限额" display="0"  type="double" nullToValue="0" precision="2" min="0" length="8" not-null="1"/>
  <relations>
    <relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" />
  </relations>
</entry>
