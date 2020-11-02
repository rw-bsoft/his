<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.ag.schemas.ADMIN_GenBuilding" alias="批量生成栋">
  <item id="count"  alias="总数" type="int" not-null="true" />
  <item id="start"	alias="起始数" type="int" defaultValue="1" not-null="true"/>
  <item id="unit"  alias="单位" type="int" not-null="true" defaultValue="01">
	<dic> 
	    <item key="01" text="幢"/>
	    <item key="02" text="单元"/>
	    <item key="03" text="号"/>
	    <item key="04" text="组"/>
	    <item key="05" text="#"/>
	</dic>
  </item>
<item id="isFamily"  alias="层次属性" type="string" length="2"  not-null="1" >
	<dic id="chis.dictionary.isFamily" render="Tree" onlySelectLeaf="true"/>
</item>
</entry>
  	