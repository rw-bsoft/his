<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YJJG" alias="月结结果库">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
	<item id="YFSB" alias="药房识别" length="18" type="long" not-null="1" generator="assigned" pkey="true"/>
	<item id="CWYF" alias="财务月份" type="datetime"  not-null="1" pkey="true"/>
	<item id="YPXH" alias="药品序号" length="18" type="long" not-null="1" pkey="true"/>
	<item id="YPCD" alias="药品产地" length="18" type="long" not-null="1" pkey="true"/>
	<item id="CKBH" alias="窗口编号" length="2" type="int" not-null="1" pkey="true"/>
	<item id="YPGG" alias="药品规格" type="string" length="20"/>
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="YFBZ" alias="药房包装" length="4" not-null="1" type="int"/>
	<item id="KCSL" alias="库存数量" length="10" precision="2" min="0" max="999999.99" not-null="1" type="double"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" not-null="1" min="0" max="999999.9999" type="double"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4"  min="0" max="999999.9999" type="double"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" not-null="1" min="0" max="999999.9999" type="double"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" min="0" max="99999999.99" type="double"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  min="0" max="99999999.99" type="double"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" min="0" max="99999999.99" type="double"/>
</entry>
