<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人会诊申请">
  <item id="ZYH" alias="住院号" type="long" length="18"  generator="assigned" pkey="true" fixed="true"/>
  
  <item id="JGID" alias="机构ID" type="string" length="20"  display="0"/>
  <item id="BRID" alias="病人ID" type="long" length="18"  display="0"/>
  <item id="ZYHM" alias="住院号码" length="10"  display="0"/>
  
  
  <item id="BRXM" alias="病人姓名" length="20" fixed="true"/>
  <item id="BRXB" alias="病人性别" type="int" length="4" fixed="true">
	<dic id="phis.dictionary.gender" autoLoad="true"/>
  </item>
  <item id="BRKS" alias="病人科室" type="long" length="18" fixed="true">
	<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE"/>
  </item>
  <item id="BRCH" alias="病人床号" length="12" fixed="true"/>
  
  <item id="KSDM" alias="床位病区" type="long" length="18" fixed="true">
  	<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
  </item>
  
</entry>