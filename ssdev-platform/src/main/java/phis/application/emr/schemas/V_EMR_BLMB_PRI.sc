<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="V_EMR_BLMB" alias="私有模板">
	<item id="MBBH" alias="模版编号" type="int" length="9" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="MBFL" alias="模版分类" length="8" not-null="1" display="0"/>
	<item id="MBLB" alias="模版类别" length="8" not-null="1" display="0"/>
	<item id="MBMC" alias="模版名称" length="255" width="250"/>
	<item ref="b.PTNAME" alias="模版名称" length="255" width="250"/>
	<item id="SSZK" alias="所属专科" display="0"/>
	<item id="BLLB" type="int" alias="类别编码" length="30" display="0"/>
	<item ref="c.LBMC" alias="模版类别"  width="100"/>
	<item ref="c.XSMC" alias="模版显示名称" display="0"/>
	<item id="PYDM" alias="拼音码" length="30" width="100" queryable="true"/>
	<item ref="b.PTID" alias="私有ID" type="int" length="1" display="0"/>
	<item ref="c.HYBZ" alias="换页标志" type="int" length="1" display="0" />
	<relations>
		<relation type="child" entryName="phis.application.emr.schemas.PRIVATETEMPLATE" >
			<join parent="MBBH" child="TEMPLATECODE"></join>
			<join parent="MBFL" child="PTTYPE+1"></join>
		</relation>
		<relation type="child" entryName="phis.application.emr.schemas.EMR_KBM_BLLB" >
			<join parent="MBLB" child="LBBH"></join>
		</relation>
	</relations>
</entry>
