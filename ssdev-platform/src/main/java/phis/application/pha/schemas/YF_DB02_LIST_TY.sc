<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_DB02" alias="药房调拔02"  >
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string" display="0"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" type="increase" length="8" startPos="1" />
		</key>
	</item>
	<item id="SQYF" alias="申请药房" length="18" not-null="1" type="long" display="0"/>
	<item id="SQDH" alias="申请单号" length="6" not-null="1" type="int" display="0"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0"/>
	<item ref="b.YPMC" mode="remote" />
	<item id="YFGG" alias="药房规格" type="string" length="20" fixed="true"/>
	<item id="YFDW" alias="单位" type="string" length="4" fixed="true" width="60"/>
	<item ref="c.CDMC" alias="产地" fixed="true"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" not-null="1" type="double" min="0" max="999999.9999" fixed="true"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" not-null="1" type="double" min="0" max="999999.9999" fixed="true"/>
	<item id="YPSL" alias="申领数量" length="10" precision="2" not-null="1" type="double" min="-999999.99" max="0"/>
	<item id="QRSL" alias="实发数量" length="10" precision="2" not-null="1" type="double" min="-999999.99" max="0" fixed="true"/>
	<item id="YPPH" alias="药品批号" type="string" length="20" fixed="true"/>
	<item id="YPXQ" alias="药品效期" type="datetime" fixed="true"/>
	<item id="KCSL" alias="本药房库存" fixed="true" width="100" virtual="true" type="double"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int" display="0"/>
	<item id="QRBZ" alias="确认包装" length="4" not-null="1" type="int" display="0"/>
	<item id="QRGG" alias="确认规格" type="string" length="20" display="0"/>
	<item id="QRDW" alias="确认单位" type="string" length="4" display="0"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4"  type="double" min="0" max="999999.9999" display="0"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double" min="-99999999.99" max="0" display="0"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double" min="-99999999.99" max="0" display="0"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" type="double" min="-99999999.99" max="0" display="0"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		      <join parent="YPXH" child="YPXH" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		      <join parent="YPCD" child="YPCD" />
	</relations>
</entry>
