<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PD02" alias="药库盘点">
	<item ref="b.PYDM" display="0"/>
	<item id="YPMC" alias="药品名称" type="string" fixed="true" width="120"/>
	<item id="YPGG" alias="规格" type="string" fixed="true"/>
	<item id="YPDW" alias="单位" type="string" fixed="true" width="60"/>
	<item id="YPCD" alias="生产厂家" length="18" type="string"  fixed="true"/>
	<item id="KCSL" alias="账面数量" length="10" type="double" precision="2" fixed="true"/>
	<item id="SPSL" alias="实盘数量" length="10" type="double" precision="2" max="999999.99" min="0"/>
	<item id="KCSB" alias="库存识别" length="18" type="long" display="0"/>
	<item id="LSJG" alias="零售价格" length="12" type="doubel" precision="4" fixed="true"/>
	<item id="JHJG" alias="进货价格" length="12" type="doubel" precision="4" fixed="true"/>
	<item id="YPPH" alias="药品批号" length="20" type="string" fixed="true"/>
	<item id="YPXQ" alias="药品效期" type="datetime" defaultValue="%server.date.datetime" fixed="true"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
