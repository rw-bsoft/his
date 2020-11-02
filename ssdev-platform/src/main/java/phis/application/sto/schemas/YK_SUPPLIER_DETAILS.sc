<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_SUPPLIER_DETAILS"  alias="药库采购历史_供应商明细列表" >
	<item id="RKRQ" alias="入库日期" type="timestamp"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" length="80" />
	<item id="YPGG" alias="规格" type="string" length="20" />
	<item id="YPDW" alias="单位" type="string" width="40" length="2" not-null="1"/>
	<item id="CDMC" alias="产地简称" type="string"  width="100" />
	<item id="RKSL" alias="入库数量" type="double" width="80" />
	<item id="JHJG" alias="进货价格" type="double" precision="3" />
	<item id="PFJG" alias="批发价格" type="double" precision="2" summaryType="sum" summaryRenderer="PFJGSummaryRenderer" display="0"/>
	<item id="LSJG" alias="零售价格" type="double" precision="2"/>
	<item id="FPHM" alias="发票号码" type="string" length="10"/>
	<item id="RKDH" alias="入库单号" length="6" not-null="1"  type="int"/>
	<item id="CWPB" alias="财务判别" length="1" not-null="1" type="int">
		<dic>
            <item key="0" text="未验收"/>
            <item key="1" text="已验收"/>
        </dic>
    </item>
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" display="0"/>
	<item id="JHHJ" alias="进货合计" length="11" width="100" precision="4" not-null="1"  type="double" fixed="true" summaryType="sum" summaryRenderer="JHJGSummaryRenderer"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double" display="0" />
	<item id="LSJE" alias="零售合计" length="12"  width="100" precision="4" not-null="1" type="double" fixed="true" summaryType="sum" summaryRenderer="LSJGSummaryRenderer"/>
</entry>