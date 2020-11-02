<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YJJG" alias="月结结果库">
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" type="long" pkey="true"/>
	<item id="CWYF" alias="财务月份" type="timestamp" not-null="1" pkey="true"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" pkey="true"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" pkey="true"/>
	<item id="KCSL" alias="库存数量" length="10" precision="4" not-null="1" type="double"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="LSJG" alias="零售价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="JHJG" alias="进货价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="PFJG" alias="批发价格" length="12" precision="6"  type="double"/>
</entry>
