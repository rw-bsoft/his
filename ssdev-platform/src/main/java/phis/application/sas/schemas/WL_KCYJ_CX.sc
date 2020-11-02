<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_KCYJ_CX" tableName="WL_KCYJ" alias="库存预警(WL_KCYJ)" >
  <item id="KFXH" alias="库房序号" type="int" length="8" display="0" not-null="1" generator="assigned" pkey="true"/>
  <item id="WZXH" alias="物资序号" type="long" length="18" display="0" not-null="1" pkey="true"/>
  <item ref="b.WZMC" alias="物资名称" length="60" width="150"/>
  <item ref="b.WZGG" alias="规格" length="60"/>
  <item ref="b.WZDW" alias="单位" length="60"/>
  <item ref="c.WZSL" alias="物资数量" type="double" length="18" precision="2"/>
  <item id="GCSL" alias="高储数量" type="double" length="18" precision="2"/>
  <item id="DCSL" alias="低储数量" type="double" length="18" precision="2"/>
  <relations>
        <relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
            <join parent="WZXH" child="WZXH"></join>
        </relation> 
        <relation type="child" entryName="phis.application.sup.schemas.WL_WZKC" >
            <join parent="KFXH" child="KFXH"></join>
            <join parent="WZXH" child="WZXH"></join>
        </relation> 
    </relations>
</entry>
