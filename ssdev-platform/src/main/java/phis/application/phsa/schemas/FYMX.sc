<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="FYMX" alias="费用明细">
  <item id="KSDM" alias="科室名称" length="1" type="long" not-null="1" defaultValue="1" display="1" generator="assigned"  summaryType="count" summaryRenderer="showHJ">
  	<dic id="phis.dictionary.department" autoLoad="true" searchField="PYCODE" />
  </item>
  <item id="LB" alias="类别" type="long" length="10" not-null="1" display="1">
  	<dic>
  		<item text="门诊" key="1"/>
  		<item text="住院" key="2"/>
  	</dic>
  </item>
  <item id="GHF" alias="挂号费" type="double" length="18" not-null="1" display="1" summaryType="sum" summaryRenderer="totalGHF"/>
  <item id="GH_ZB" alias="挂号占比" type="string" not-null="1" length="18" display="1" />
  <item id="YLF" alias="医疗费" type="double" not-null="1" length="18" display="1" summaryType="sum" summaryRenderer="totalYLF"/>
  <item id="YL_ZB" alias="医疗占比" type="string" not-null="1" length="18" display="1" />
  <item id="YPF" alias="药品费" length="18" type="double"  not-null="1" display="1" summaryType="sum" summaryRenderer="totalYPF"/>
  <item id="YP_ZB" alias="药品占比" length="18" type="string"  not-null="1" display="1"/>
  <item id="QTF" alias="其他费" length="18" type="double"  not-null="1"  display="1" summaryType="sum" summaryRenderer="totalQTF"/>
  <item id="QT_ZB" alias="其他占比" length="18" type="string"  not-null="1" display="1"/>
  <item id="HJFY" alias="合计费用" type="double" length="18" display="1" summaryType="sum" summaryRenderer="totalQTF"/>
  
</entry>
