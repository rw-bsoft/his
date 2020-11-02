<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.wl.schemas.MDC_Ehr" alias="his慢病核实" sort="a.mdcid desc">
  <item id="mdcid" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" display="1"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="ehrjgdm" alias="产生机构" type="string" update="false" >
  	<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
  </item>
  <item id="workType" alias="类型" type="string" length="500" >
      <dic id="chis.dictionary.workType" />
  </item>
 
</entry>