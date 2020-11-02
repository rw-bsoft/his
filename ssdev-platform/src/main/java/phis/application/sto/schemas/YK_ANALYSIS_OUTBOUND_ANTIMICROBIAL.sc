<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_ANALYSIS_OUTBOUND_ANTIMICROBIAL" alias="抗菌药出库分析" >
	<item id="YPXH" alias="药品序号" length="18"  type="int" display="0" />
  <item id="YPMC" alias="药品名称"  width="200" type="string"/>
  <item id="YPGG" alias="药品规格"  width="90" type="string"/>
  <item id="YPDW" alias="单位"  width="50" type="string"/>
  <item id="CKSL" alias="出库数量"  width="80" type="string"/>
  <item id="JHJE" alias="进货总额"  width="80" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
  <item id="LSJE" alias="零售总额"  width="80" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
  <item id="JXCE" alias="进销差额"  width="80" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
  <item id="KL" alias="扣率（%）"  width="100" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
</entry>
