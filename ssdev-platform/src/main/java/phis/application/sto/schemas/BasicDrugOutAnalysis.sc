<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BasicDrugOutAnalysis" alias="基本药物出库分析" >
	<item id="DWID" alias="单位ID" length="18"  type="int" display="0" />
  <item id="CKDW" alias="出库单位"  width="200" type="string"/>
  <item id="JYZE" alias="基药总额"  width="120" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
  <item id="CKZE" alias="出库总额"  width="120" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
  <item id="BL" alias="比例（%）"  width="100" type="double" precision="2" summaryType="sum" filter="true" filterType="numeric"/>
</entry>
