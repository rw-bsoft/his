<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_MZFYMX" alias="门诊发药明细">
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string" defaultValue="%user.properties.manaUnitId"/>
	<item id="JLXH" alias="记录序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="300" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" defaultValue="%user.properties.pharmacyId"  type="long"/>
	<item id="FYCK" alias="发药窗口" length="2" not-null="1" type="int" defaultValue="1"/>
	<item id="FYRQ" alias="发药日期" type="datetime"   defaultValue="%server.date.datetime"/>
	<item id="CFSB" alias="处方识别" length="18" not-null="1" type="long"/>
	<item id="CFLX" alias="处方类型" length="1" not-null="1" type="int"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" not-null="1"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long"/>
	<item id="YPLX" alias="药品类型" length="1" not-null="1" type="int"/>
	<item id="YPGG" alias="药品规格" type="string" length="20" />
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int"/>
	<item id="YPSL" alias="药品数量" length="10" precision="4" not-null="1" type="double"/>
	<item id="HJJG" alias="划价价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="LSJG" alias="零售价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="PFJG" alias="批发价格" length="12" precision="6"  type="double"/>
	<item id="JHJG" alias="进货价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="HJJE" alias="划价金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="YPPH" alias="药品批号" type="string" length="20"/>
	<item id="YPXQ" alias="药品效期" type="datetime"/>
	<item id="TYGL" alias="退药关联" length="18" not-null="1" type="long"/>
	<item id="YKJH" alias="药库进货" length="12" precision="4" type="double"/>
	<item id="JBYWBZ" alias="基本药物标志" length="1" not-null="1" type="int"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"/>
</entry>
