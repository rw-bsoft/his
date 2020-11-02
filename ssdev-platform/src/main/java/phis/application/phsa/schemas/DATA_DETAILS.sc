<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DATA_DETAILS" alias="门诊人次、大处方数、在院、出院、入院、危重等明细">
  <item id="KSDM" alias="科室名称" length="1" type="long" not-null="1" defaultValue="1" display="1" width="200" generator="assigned" summaryType="count" summaryRenderer="showHJ">
  	<dic id="phis.dictionary.department" autoLoad="true" searchField="PYCODE" />
  </item>
  
  <item id="RS" alias="人数" type="long" not-null="1" length="30" display="1" width="200" summaryType="sum"/>
  <item id="ZB" alias="占比" type="string" not-null="1" length="30" display="1" width="100" precision="2" summaryType="count" summaryRenderer="showZB" />
 
  
</entry>
