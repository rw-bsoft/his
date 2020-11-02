<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_ANALYSIS_PURCHASE" alias="采购分析" >
  <item id="ID" alias="id"   type="String" display="0" />
  <item id="YPLBMC" alias="药品类别名称"  width="250" type="string" filterType="string" filter="true" summaryType="count" summaryRenderer="textRenderer"/>
  <item id="JHZE" alias="进货总额" type="double" precision="4"  summaryType="sum" filter="true" filterType="numeric"/>
  <item id="LSZE" alias="零售总额" type="double" precision="4"  summaryType="sum" filter="true" filterType="numeric"/>
  <item id="JXCE" alias="进销差额" type="double" precision="4"  summaryType="sum" filter="true" filterType="numeric"/>
  <item id="KL" alias="扣率（%）" type="double" precision="4"  summaryType="sum" filter="true" filterType="numeric"/>
</entry>
