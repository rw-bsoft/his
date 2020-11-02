<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_RK02" alias="入库02">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" defaultValue="%user.manageUnit.id" display="0" />
	<item id="SBXH" alias="识别序号" length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
		
	</item>
	<item id="YFSB" alias="药房识别" length="18" type="long" not-null="1" display="0" defaultValue="%user.properties.pharmacyId"/>
	<item id="CKBH" alias="窗口编号" length="2" type="int" not-null="1" display="0" defaultValue="0"/> 
	<item id="RKFS" alias="入库方式" length="4" type="int" not-null="1" display="0">
		<dic id="phis.dictionary.drugStorage"></dic>
	</item>
	<item id="RKDH" alias="入库单号" length="8" type="int" not-null="1" display="0" />
	<item id="YPXH" alias="药品序号" length="18"  not-null="1" display="0" type="long"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1"  display="0" type="int"/>
	<item id="PFJG" alias="批发价格" length="12" type="double" precision="6"  display="0"/>	
	<item id="PFJE" alias="批发金额" length="12" type="double" precision="4"  display="0"/>
	<item ref="b.YPMC" mode="remote" />
	<item id="YPGG" alias="规格" type="string" length="20" width="80" fixed="true"/>
	<item id="YFDW" alias="单位" type="string" length="4" width="40" fixed="true"/>
	<item ref="c.CDMC" alias="产地" fixed="true"/>
	<item id="YPCD" alias="产地" type="long" length="18" not-null="1" display="0"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="RKSL" alias="入库数量" length="10" type="double" precision="2" not-null="1" width="80" min="0" max="999999.99"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" renderer="onRenderer_four"/>
	<item id="YPPH" alias="药品批号" type="string" length="20" width="71"/>
	<item id="YPXQ" alias="药品效期" type="datetime"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		<join parent="YPXH" child="YPXH" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		<join parent="YPCD" child="YPCD" />
	</relations>

</entry>
