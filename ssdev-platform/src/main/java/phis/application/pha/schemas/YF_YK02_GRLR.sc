<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YK02_GRLR" alias="药房盈亏个人录入">
	<item id="JGID" alias="机构ID" length="20" not-null="1" defaultValue="%user.manageUnit.id"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" type="long"/>
	<item id="CKBH" alias="窗口编号" length="2" not-null="1" type="int"/>
	<item id="PDDH" alias="盘点单号" length="8" not-null="1" type="int"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long"/>
	<item id="YPGG" alias="药品规格" type="string" length="20"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int"/>
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="PQSL" alias="盘前数量" length="10" precision="2" not-null="1" type="double" max="999999.99" min="0"/>
	<item id="SPSL" alias="实盘数量" length="10" precision="4" not-null="1" type="double" max="999999.99" min="0"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" not-null="1" type="double" max="999999.9999" min="0"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4"  type="double" max="999999.9999" min="0"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" not-null="1" type="double" max="999999.9999" min="0"/>
	<item id="YPPH" alias="药品批号" type="string" length="20"/>
	<item id="YPXQ" alias="药品效期" type="timestamp"/>
	<item id="YLSE" alias="原零售额" length="12" precision="4" not-null="1" type="double" max="99999999.9999" min="0"/>
	<item id="YPFE" alias="原批发额" length="12" precision="4"  type="double" max="99999999.9999" min="0"/>
	<item id="YJHE" alias="原进货额" length="12" precision="4" not-null="1" type="double" max="99999999.9999" min="0"/>
	<item id="XLSE" alias="新零售额" length="12" precision="4" not-null="1" type="double" max="99999999.9999" min="0"/>
	<item id="XPFE" alias="新批发额" length="12" precision="4"  type="double" max="99999999.9999" min="0"/>
	<item id="XJHE" alias="新进货额" length="12" precision="4" not-null="1" type="double" max="99999999.9999" min="0"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"/>
	<item id="LRBZ" alias="录入标志" length="1" type="int"/>
	<item id="LRRY" alias="录入人员" type="string" length="10"/>
	<item id="LRWC" alias="录入完成" length="1" type="int"/>
	<item id="BZ" alias="备注" type="string" length="80"/>
	<item id="YKBZ" alias="药库包装" length="4" type="int"/>
	<item id="YKDW" alias="药库单位" type="string" length="4"/>
	<item id="YKSL" alias="实盘药库数量" length="10" precision="4" not-null="1" type="double" max="999999.99" min="0"/>
</entry>
