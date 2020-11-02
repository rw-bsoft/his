<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_DB02" alias="药房调拔02">
	<item id="JGID" alias="机构ID" length="20" not-null="1"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="SQYF" alias="申请药房" length="18" not-null="1" type="long"/>
	<item id="SQDH" alias="申请单号" length="6" not-null="1" type="int"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long"/>
	<item id="YPSL" alias="药品数量" length="10" precision="2" not-null="1" type="double" min="0" max="999999.99"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int"/>
	<item id="YFGG" alias="药房规格" type="string" length="20"/>
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="QRSL" alias="确认数量" length="10" precision="2" not-null="1" type="double" min="0" max="999999.99"/>
	<item id="QRBZ" alias="确认包装" length="4" not-null="1" type="int"/>
	<item id="QRGG" alias="确认规格" type="string" length="20"/>
	<item id="QRDW" alias="确认单位" type="string" length="4"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" not-null="1" type="double" min="0" max="999999.9999"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4"  type="double" min="0" max="999999.9999"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" not-null="1" type="double" min="0" max="999999.9999"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double" min="0" max="99999999.99"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double" min="0" max="99999999.99"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" type="double" min="0" max="99999999.99"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"/>
	<item id="YPPH" alias="药品批号" type="string" length="20"/>
	<item id="YPXQ" alias="药品效期" type="timestamp"/>
	<item id="DRKCSB" alias="调入库存识别" length="18"  type="long"/>
</entry>
