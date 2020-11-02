<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YPXX" alias="药库药品信息" >
	<item id="JGID" alias="机构ID" length="20" not-null="1"  display="0" 
		defaultValue="%user.manageUnit.id"  layout="GDC" update="false" pkey="true" />
	<item id="YPXH" alias="药品序号" length="18" type="long"  display="0"
		not-null="1" pkey="true" layout="GDC" >	
	</item>
	<item id="YKSB" alias="药库识别" length="18" display="0" type="long" not-null="1" layout="GDC"/>
	<item id="GCSL" alias="高储数量"  type="double" length="11" display="2" maxValue="999999.99" precision="2" defaultValue="0"
		not-null="1" layout="GDC"/>
	<item id="DCSL" alias="低储数量" type="double"  length="11" display="2" maxValue="999999.99" precision="2" defaultValue="0"
		not-null="1" layout="GDC"/>
	<item id="KWBM" alias="库位编码" type="string" display="0" length="16" layout="GDC"/>
	<item id="YKZF" alias="药库作废" type="int" length="1" display="0" not-null="1" layout="GDC" defaultValue="0" update="false">
	</item>
	<item id="CFLX" alias="处方类型" length="2" type="int" display="2" not-null="1" layout="GDC" defaultValue="1">
		<dic id="phis.dictionary.prescriptionType" />
	</item>
</entry>
