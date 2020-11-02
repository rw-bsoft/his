<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_FYJY_BRXZ" alias="费用禁用" tableName="GY_FYJY">
  <item id="BRXZ" alias="病人性质" length="18" type="long" display="0" not-null="1" generator="assigned"/>
  <item id="FYXH" alias="费用序号" length="18" type="long" display="0"  not-null="1"/>
  <item ref="b.FYMC" alias="费用名称" type="string" summaryType="count" mode="remote" width="250" anchor="100%" summaryRenderer="totalYPSL" length="50" not-null="1"/>
  <item ref="b.PYDM" queryable="true" display="0"/>
  <item ref="b.FYDW" alias="单位" type="string" width="80" length="18" fixed="true"/>
  <item id="ZFBL" alias="自负比例%" type="double" nullToValue="0" min="0" max="100" precision="1" width="90" length="6" not-null="1"/>
  <item id="FYXE" alias="费用限额" type="double" nullToValue="0" display="0" min="0" precision="2" length="8" not-null="1"/>
  <item id="CXBL" alias="超限比例%" type="double" nullToValue="0" display="0" min="0" max="100" precision="1" width="90" length="6"/>
  <relations>
    <relation type="parent" entryName="phis.application.cfg.schemas.GY_YLSF" />
  </relations>
</entry>
