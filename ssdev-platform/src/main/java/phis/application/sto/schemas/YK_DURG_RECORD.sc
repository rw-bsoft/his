<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_DURG_RECORD"  alias="药库采购历史_药品记录" >
	<item id="YPXH" alias="药品序号" type="long"  width="18" anchor="100%" length="80" not-null="true" pkey="true" display="0" /> 
	<item id="YPCD" alias="药品产地"  width="18" type="long" anchor="100%" length="80" not-null="true"  display="0" pkey="true"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" length="80" />
	<item id="YPGG" alias="规格" type="string" length="20" />
	<item id="YPDW" alias="单位" type="string" length="2" not-null="1" width="40"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" display="0"/>
	<item id="RKSL" alias="入库数量" type="double" width="80"/>
	<item id="JHJG" alias="进货价格" type="double" precision="3" />
	<item id="PFJG" alias="批发价格" type="double" precision="2" summaryType="sum" summaryRenderer="PFJGSummaryRenderer" display="0"/>
	<item id="LSJG" alias="零售价格" type="double" precision="2" />
	<item id="JHHJ" alias="进货合计" length="11" precision="4" width="100" not-null="1" min="-9999999.99" max="99999999.99" type="double" fixed="true" summaryType="sum" summaryRenderer="JHJGSummaryRenderer"/>
	<item id="LSJE" alias="零售合计" length="12" precision="4" width="100" not-null="1" type="double" fixed="true" summaryType="sum" summaryRenderer="LSJGSummaryRenderer"/>
	<item id="CDMC" alias="产地简称" type="string"  width="100" />
	<item id="WBDM" alias="五笔码" type="string" length="10" display="0"/>
	<item id="JXDM" alias="角形码" type="string" length="10" display="0"/>
</entry>