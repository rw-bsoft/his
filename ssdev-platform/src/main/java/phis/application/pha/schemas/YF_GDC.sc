<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YF_GDC" alias="药库高低储提示">
	<item id="YPMC" alias="药品名称" type="string"  width="180"/>
	<item id="PYDM" alias="拼音码" type="string"  display="0" queryable="true"/>
	<item id="YFGG" alias="规格" type="string"  />
	<item id="YFDW" alias="单位" type="string"  />
	<item id="GCSL" alias="高储数量"  type="double" renderer="onRenderer_gc"/>
	<item id="KCSL" alias="当前库存"  type="double" />
	<item id="DCSL" alias="低储数量"  type="double" renderer="onRenderer_dc"/>
</entry>