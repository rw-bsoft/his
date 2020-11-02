<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_CK02" alias="出库02">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" defaultValue="%user.manageUnit.id" display="0" />
	<item id="SBXH" alias="识别序号" length="18" type="long" not-null="1" generator="assigned" pkey="true" display="0" >
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18" display="0" defaultValue="%user.properties.pharmacyId" not-null="1" type="long"/>
	<item id="CKBH" alias="窗口编号" length="2" display="0" type="int" not-null="1" defaultValue="0"/>
	<item id="CKFS" alias="出库方式" length="4"  display="0" not-null="1" type="int">
		<dic id="phis.dictionary.drugDelivery"/>
	</item>
	<item ref="d.YPSL" display="0"></item>
	<item id="CKDH" alias="出库单号" length="8" type="int" display="0" not-null="1"/>
	<item id="YPXH" alias="药品序号" length="18" display="0"  not-null="1" type="long"/>
	<item id="YFBZ" alias="药房包装" length="4" display="0"  not-null="1" type="int"/>
	<item id="PFJG" alias="批发价格" length="12" type="double" precision="6"  display="0"/>	
	<item id="PFJE" alias="批发金额" length="12" type="double" precision="4"  display="0"/>
	<item ref="b.YPMC" mode="remote"/>
	<item id="YPGG" alias="规格" type="string" length="40" width="80" fixed="true"/>
	<item id="YFDW" alias="单位" type="string" length="4" width="40" fixed="true"/>
	<item ref="c.CDMC" alias="产地" fixed="true"/>
	<item id="YPCD" alias="产地" type="long" length="18" not-null="1" display="0"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="CKSL" alias="出库数量" length="10" type="double" precision="2" not-null="1" width="80" min="0" max="999999.99"/>
	`<item id="LSJE" alias="零售金额" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="YPPH" alias="药品批号" type="string" length="20" fixed="true" width="71"/>
	<item id="YPXQ" alias="药品效期" type="datetime" fixed="true"/>
	<item id="KCSB" alias="库存识别" length="18" type="long" display="0"  not-null="1"/>
	<item id="LGJLXH" alias="留观记录序号" length="18" type="long" display="0" defaultValue="0" not-null="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		<join parent="YPXH" child="YPXH"></join>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		<join parent="YPCD" child="YPCD"></join>  
		<relation type="parent" entryName="phis.application.pha.schemas.YF_KCMX_CSH" >
			<join parent="SBXH" child="KCSB"></join>
		</relation>
	</relations>
</entry>
