<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_CWSZ" alias="床位设置" sort="BRCH,FJHM desc">
  <item id="ZYH" alias="" width="30" type="long" length="18" display="0" renderer="onRenderer" />
  <item id="JGID" alias="机构ID" type="string" display="0" length="20"  not-null="1" defaultValue="%user.manageUnit.id" pkey="true"/>
  <item id="BRCH" alias="床位号码" length="12" not-null="1" pkey="true"/>
  <item id="CWKS" alias="床位科室" type="long" length="18" not-null="1" display="0">
  	<dic id="phis.dictionary.department_zy" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  />
  </item>
  <item id="KSDM" alias="床位病区" type="long" length="18" not-null="1" display="0">
  	<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"></dic>
  </item>
 
</entry>
