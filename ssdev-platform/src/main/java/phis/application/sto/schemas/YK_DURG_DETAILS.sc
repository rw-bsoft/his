<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_DURG_DETAILS"  alias="药库采购历史_药品记录" >
	<item id="RKRQ" alias="入库日期" type="timestamp"/>
	<item id="DWMC" alias="单位名称" type="string"  length="10" not-null="true" />
	<item id="PYDM" alias="拼音代码" type="string" length="10" display="0"/>
	<item id="RKSL" alias="入库数量" type="double" width="80" />
	<item id="JHJG" alias="进货价格" type="double"  precision="3"/>
	<item id="PFJG" alias="批发价格" type="double" precision="2" summaryType="sum" summaryRenderer="PFJGSummaryRenderer" display="0"/>
	<item id="LSJG" alias="零售价格" type="double" precision="2" />
	<item id="FPHM" alias="发票号码" type="string" length="10"/>
	<item id="RKDH" alias="入库单号" length="6" not-null="1"  type="int"/>
	<item id="CWPB" alias="财务判别" length="1" not-null="1" type="int">
		<dic>
            <item key="0" text="未验收"/>
            <item key="1" text="已验收"/>
        </dic>
    </item>
	<item id="JHHJ" alias="进货合计" length="11" precision="4" width="100" not-null="1" min="-9999999.99" max="99999999.99" type="double" fixed="true" summaryType="sum" summaryRenderer="JHJGSummaryRenderer"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  display="0" type="double" />
	<item id="LSJE" alias="零售合计" length="12" precision="4" width="100" not-null="1" type="double" fixed="true" summaryType="sum" summaryRenderer="LSJGSummaryRenderer"/>
</entry>
