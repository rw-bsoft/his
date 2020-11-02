<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PHSA_JCFY" alias="均次费用明细">
  <item id="KSDM" alias="科室名称" length="1" type="long" not-null="1" defaultValue="1" display="1" generator="assigned" summaryType="count" summaryRenderer="showHJ">
  	<dic id="phis.dictionary.department" autoLoad="true" searchField="PYCODE" />
  </item>
 
  <item id="MZRC" alias="门诊人次" type="double" length="10" not-null="1" display="1"  summaryType="sum" />
  <item id="GHF" alias="挂号费" length="18" type="double"  not-null="1"  display="1" precision="2" summaryType="sum" />
  <item id="YLF" alias="医疗费" type="double" not-null="1" length="30" display="1" precision="2" summaryType="sum" />
  <item id="YPF" alias="药品费" length="18" type="double"  not-null="1" display="1" precision="2" summaryType="sum" />
  <item id="MZ_ZSR" alias="其他费" length="18" type="double"  not-null="1" display="1" precision="2" summaryType="sum" />
  <item id="HJFY" alias="合计费用" type="double" length="6" display="1"  precision="2" summaryType="sum" />
  <item id="JCFY" alias="均次费用" type="double" length="6" precision="2" display="1" summaryType="count" summaryRenderer="showJCFY"/>
</entry>
