<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FYYF" alias="病区发药药房" sort="TYPE asc">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="JGID" display="0" alias="机构ID" type="string" length="20" not-null="1"  defaultValue="%user.manageUnit.id"/>	
	<item id="GNFL" alias="功能分类" type="int" length="1" not-null="1" defaultValue="4" display="0">
		<dic id="phis.dictionary.Pharmacyfunclass" autoLoad="true"/>
	</item>
	<item id="TYPE" alias="医嘱类型" type="int" length="1" not-null="1" defaultValue="1" >
		<dic id="phis.dictionary.jcadvicetype" autoLoad="true"/>
	</item>
 
	<item id="YFSB" alias="药房"  type="long" not-null="1" length="18">
		<dic id="phis.dictionary.pharmacy_bq" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
  
	<item id="DMSB" alias="药品类型" type="string" length="255" not-null="1">
		<dic id="phis.dictionary.prescriptionType" autoLoad="true"/>
	</item>
	<item id="ZXPB" alias="注销" type="int" length="1" not-null="1" fixed="true" defaultValue="0">
		<dic>
			<item key="1" text="注销"/>
			<item key="0" text="正常"/>
		</dic>
	</item>
</entry>
