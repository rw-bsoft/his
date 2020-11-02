<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CDXX"   alias="价格管理_私有"  sort="a.YPXH">
	<item id="YPXH" alias="药品序号" type="long"  width="18" anchor="100%"  length="80" not-null="true" pkey="true"/> 
	<item id="YPCD" alias="药品产地"  width="18" type="long" anchor="100%" length="80" not-null="true"  display="0" pkey="true"/>
	<item ref="c.PYDM" display="0"/>
	<item ref="c.YPMC"  display="1"/>
	<item ref="c.YPGG" alias="药品规格"  display="0"/>
	<item ref="c.YPDW"   display="0"/>
	<item ref="d.YKSB"   display="0"/>
	<item ref="b.CDMC" renderer="onRenderer_jg" alias="生产厂家"/>
	<item id="JHJG" alias="进货价格"   width="80"  type="bigDecimal" precision="4" length="12" not-null="true" />
	<item id="LSJG" alias="零售价格"   width="80"  type="bigDecimal" precision="4"  length="12" not-null="true" />
	<item id="PZWH" alias="批准文号" type="string"  width="100"  length="60" not-null="true" />
	<item id="ZFPB" alias="作废判别" display="0" type="string"   />
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ_YK" >
		<join parent="YPCD" child="YPCD"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
		<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.sto.schemas.YK_YPXX" >
		<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
	
		
	
</entry>
