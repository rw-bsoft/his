<?xml version="1.0" encoding="UTF-8"?>

<entry alias="本地药品对照表" entityName="V_YPXX_SPT">
	<item id="JGID" alias="机构代码" type="string" length="20" pkey="true" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" pkey="true" display="0" />
	<item id="YPCD" alias="药品产地" type="long" length="18" pkey="true" display="0" />
	<item id="YPMC" alias="药品名称" type="string" length="80" queryable="true"  width="200" fixed="true"/>
	<item id="ZBBM" alias="中标编码" type="string" queryable="true" length="20" width="100" />
	<item id="YPGG" alias="规格" type="string" length="20" fixed="true"/>
	<item id="YPDW" alias="单位" type="string" length="4" width="40" fixed="true"/>
	<item id="CDMC" alias="药品产地" type="string" length="15" width="100" fixed="true"/>
	<item id="LSJG" alias="零售价格" type="double"  length="12" fixed="true"/>
	<item id="PYDM" alias="拼音码" type="string" display="0" length="10" selected="true" queryable="true" fixed="true"/>
</entry>