<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_KFDZ" alias="库房对照">
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0" not-null="1" pkey="true"/>
	<item id="KSDM" alias="科室代码" type="long" length="18" display="0" not-null="1" pkey="true"/>
	<item ref="b.OFFICENAME" alias="选择科室" width="120" type="string" length="50" />
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.SYS_Office_SELECT" >
			<join parent="ID" child="KSDM"></join>
		</relation>					
	</relations>
</entry>
