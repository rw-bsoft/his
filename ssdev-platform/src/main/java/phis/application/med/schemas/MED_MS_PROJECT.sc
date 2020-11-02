<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MS_YJ02" alias="医技项目取消项目集合" >
	<item ref="b.FYMC" alias="费用名称"  length="80" type="string" not-null="1" />
	<item ref="b.FYDW" alias="费用单位"  length="4" type="string"  />
	<item id="YLDJ" alias="医疗单价"  length="10" type="double" precision="2" not-null="1"/>
	<item id="YLSL" alias="医疗数量"  length="8" type="double" precision="2" not-null="1"/>
	<item id="HJJE" alias="划价金额"  length="12" type="double" precision="2" not-null="1" />
	<item id="ZFBL" alias="自负比例"  length="5" type="double" precision="3" not-null="1" />
	<item id="FYGB" alias="费用归并"  length="18" type="long" not-null="1" display="0"/>
	<item id="BZXX" alias="备注信息"  length="255" type="string" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_YLSF" >
			<join parent="FYXH" child="YLXH"></join>
		</relation>
	</relations>
</entry>