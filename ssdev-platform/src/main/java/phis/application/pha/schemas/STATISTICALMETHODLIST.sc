<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="STATISTICALMETHODLIST" alias="药房发药统计方式"  >
	<item id="YPMC" alias="项目名称" type="string" width="120" anchor="100%"
		length="80" colspan="2" not-null="true"/>
	<item id="YPXH" alias="药品序号" type="string" display="0"/>
	<item id="YPGG" alias="药品规格" type="string" length="20" display="0"/>
	<item id="YPDW" alias="药品单位" type="string" length="4" display="0"/>
	<item id="FYCK" alias="发药窗口" type="double" display="0"/>
	<item id="CFLX" alias="处方类型" type="double" display="0"/>
	<item id="FYSL" alias="发药数量" type="double" display="0"/>	
	<item id="FYJE" alias="金额" type="double" width="120" precision="2" summaryType="sum" summaryRenderer="FYJESummaryRenderer"/>
	<item id="BL" alias="比例" type="double"  precision="2"/>
	<item id="CFZS" alias="处方张数" type="double" />
	<item id="PJCFE" alias="平均处方额" type="double"  width="100" precision="2"/>
	<item id="VIRTUAL_FIELD" alias="虚拟字段用于保存发药窗口病人性质特殊药品科室代码的ID" type ="string" display="0"/>
	<item id="YSDM" alias="医生工号" type="string"/>
	<item id="YSXM" alias="医生姓名" type="string"/>
	<item id="SUM" alias="总计金额" type="string" display="0"/>
	<item id="YSID" alias="医生ID" type="string" display="0"/>
	
</entry>