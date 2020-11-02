<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="V_EMR_BLMB" alias="病历模版表">
	<item id="MBBH" alias="模版编号" type="int" length="9" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="MBFL" alias="模版分类" length="8" not-null="1" display="0"/>
	<item id="MBMC" alias="模版名称" length="255"/>
	<item id="MBLB" alias="模版类别" length="8" not-null="1" display="0"/>
	<item ref="b.XSMC" alias="模版显示名称" display="0"/>
	<item ref="b.LBMC" alias="类别名称" />
	<item ref="b.HYBZ" alias="换页标志" display="0"/>
	<item id="BLLB" type="int" alias="模版类别" length="30" />
	<item id="PYDM" alias="拼音码" length="30" queryable="true"/>
	<relations>
		<relation type="child" entryName="phis.application.emr.schemas.EMR_KBM_BLLB" >
			<join parent="MBLB" child="LBBH"></join>
		</relation>
	</relations>
</entry>
