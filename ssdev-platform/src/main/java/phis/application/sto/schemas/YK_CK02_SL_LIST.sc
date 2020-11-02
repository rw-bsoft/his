<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CK02" alias="出库02"  >
	<item ref="b.YPMC" mode="remote"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC" fixed="true"/>
	<item id="LSJG" alias="零售价格" length="12" type="double" precision="4" max="999999.9999" min="0" not-null="1" fixed="true"/>
	<item id="JHJG" alias="进货价格" length="12" type="double" precision="4" not-null="1" fixed="true"/>
	<item id="SQSL" alias="申请数量" length="10" type="double" precision="2" max="999999.99" min="0" not-null="1"/>
	<item id="SFSL" alias="实发数量" length="10" precision="2" not-null="1" max="999999.99" min="0"  defaultValue="0" type="double" fixed="true"/>
	<item id="KCSL" alias="药库库存" length="12" type="double" precision="2" max="999999.9999" min="0"  virtual="true" fixed="true"/>
	<item id="LSJE" alias="零售金额" length="12" type="double" precision="4" max="99999999.9999" min="0" not-null="1" fixed="true" display="0"/>
	<item id="JHJE" alias="进货金额" length="12" type="double" precision="4" not-null="1" fixed="true" display="0"/>
	<item id="JGID" alias="机构ID" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" type="long" display="0"/>
	<item id="CKFS" alias="出库方式" length="4" not-null="1" type="int" display="0"/>
	<item id="CKDH" alias="出库单号" length="6" not-null="1" display="0" type="int"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" display="0" type="long"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" display="0" type="long"/>
	<item id="YPPH" alias="药品批号" type="string" length="20" display="0" />
	<item id="YPXQ" alias="药品效期" type="date" display="0"/>
	<item id="PFJG" alias="批发价格" length="12" precision="6"  display="0"  type="double"/>
	<item id="TYPE" alias="库存性质" length="6" display="0" type="int" defaultValue="0"/>
	<item id="PFJE" alias="批发金额" length="12" type="double" precision="4"  display="0" defaultValue="0"/>
	<item id="BZLJ" alias="标准零价" length="12" type="double" precision="6" not-null="1" display="0" defaultValue="0"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" display="0" type="long" defaultValue="0"/>
	<item id="YKJH" alias="药库进货" length="12" precision="4" display="0" type="double"/>
	<item id="JBYWBZ" alias="基本药物标志" length="1" not-null="1" display="0" type="int" defaultValue="0"/>
	<item id="YFKCSB" alias="药房库存识别" length="18" not-null="1" display="0" type="long" defaultValue="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
	        	<join parent="YPXH" child="YPXH" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
	        	<join parent="YPCD" child="YPCD" />
	</relations>
</entry>
