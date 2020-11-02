<?xml version="1.0" encoding="UTF-8"?>

<entry id="YK_GDC" tableName="YK_KCMX" alias="药库高低储提示">
	<item id="YPXH" alias="药品序号" length="20" not-null="1" type="string"
		display="0" />
	<item id="YPMC" alias="药品名称" type="string" virtual="true" width="180" />
	<item id="PYDM" alias="拼音代码" type="string" virtual="true" queryable="true" width="180" display="2"/>
	<item id="YPGG" alias="药品规格" type="string" virtual="true" width="180" />
	<item id="YPDW" alias="药品单位" type="string" virtual="true" width="180" />
	<item id="KCSL" alias="当前库存" type="double" />
	<item id="GCSL" alias="高储数量" type="double" renderer="onRenderer_gc"
		virtual="true" />
	<item id="DCSL" alias="低储数量" type="double" renderer="onRenderer_dc"
		virtual="true" />
</entry>
