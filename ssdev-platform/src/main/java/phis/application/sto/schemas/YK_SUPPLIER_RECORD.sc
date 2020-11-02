<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_SUPPLIER_RECORD"  alias="药库采购历史_供应商记录" >
	<item id="DWXH" alias="单位序号" length="18" type="long" display="0" />
	<item id="DWMC" alias="单位名称" type="string"  length="10" not-null="true" width="150"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" display="0"/>
	<item id="JHJG" alias="进货价格" type="double"  precision="3" display="0"/>
	<item id="PFJG" alias="批发价格" type="double" precision="2" summaryType="sum" summaryRenderer="PFJGSummaryRenderer" display="0"/>
	<item id="LSJG" alias="零售价格" type="double" precision="2" display="0"/>
	<item id="RKSL" alias="入库数量" type="double" width="80" display="0"/>
	<item id="JHHJ" alias="进货合计" length="11"  width="100" precision="4" not-null="1"  type="double" fixed="true" summaryType="sum" summaryRenderer="JHJGSummaryRenderer"/>
	<item id="LSJE" alias="零售合计" length="12" width="100" precision="4" not-null="1" type="double" fixed="true" summaryType="sum" summaryRenderer="LSJGSummaryRenderer"/>
</entry>