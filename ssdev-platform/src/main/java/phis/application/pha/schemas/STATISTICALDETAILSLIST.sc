<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="STATISTICALDETAILSLIST" alias="药房发药统计明细"  >
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80"  not-null="true"/>
	<item id="YPXH" alias="药品序号" type="double" display="0"/>
	<item id="YPGG" alias="药品规格" type="string" width="180" anchor="100%"
		length="20"  not-null="true"/>
	<item id="YPDW" alias="单位" width="40" type="string" length="4" not-null="1"/>
	<item id="FYCK" alias="发药窗口" type="double" display="0"/>
	<item id="CFLX" alias="处方类型" type="double" display="0"/>
	<item id="CFZS" alias="处方张数" type="double" display="0" />
	<item id="FYSL" alias="发药数" type="double" width="150" summaryType="sum" summaryRenderer="FYSLSummaryRenderer"/>
	<item id="FYJE" alias="发药金额" type="double"  precision="2" summaryType="sum" summaryRenderer="FYJESummaryRenderer"/>
	<item id="PYDM" alias="拼音代码" type="string" queryable="true" display="0" />
</entry>