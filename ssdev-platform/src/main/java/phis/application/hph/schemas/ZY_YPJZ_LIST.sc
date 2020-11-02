<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_YPJZ_LIST" tableName="ZY_FYMX" alias="住院记账list">
	<item id="LB" alias="" width="20"  type="string" virtual="true"  fixed="true"/>
	<item id="YPMC" alias="药品名称" length="180"  not-null="1" type="string" mode="remote"/>
	<item id="CDMC" alias="产地"   not-null="1" type="string" fixed="true"/>
	<item id="YFGG" alias="规格" type="string" length="40" width="80" fixed="true"/>
	<item id="YFBZ" alias="药房包装" length="4" dissplay="0"  not-null="1" type="int" fixed="true" />							
	<item id="YFDW" alias="单位" type="string" length="4" width="40" fixed="true"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" />
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" not-null="1" width="80" display="0" />
	<item id="YPSL" alias="数量" length="10" type="double" precision="2" not-null="1" width="80" min="-999999.99" max="999999.99"/>
	<item id="LSJE" alias="金额" length="12" precision="4" type="double" not-null="1" width="80" fixed="true" />
	<item id="YPCD" alias="药品产地" length="18"  not-null="1"  display="0"  type="long"/>
	<item id="YPXH" alias="药品序号" length="18" display="0"  not-null="1" type="long"/>
	<item id="YPPH" alias="药品批号" type="string" length="20"  display="0"/>
	<item id="YPXQ" alias="药品效期" type="datetime"  display="0"/>
	<item id="KCSL" alias="库存数量" length="10" type="double" precision="2" not-null="1" width="80" min="0" max="999999.99" display="0"/>
	<item id="KCSB" alias="库存识别" length="18" display="0"  not-null="1" type="long"/>
	<item id="JYLX"  alias="基药类型" type="int" length="1" display="0"  not-null="1" />
	<item id="FYFS"  type="long" alias="发药方式" length="18"  display="0"  not-null="1" />
</entry>
