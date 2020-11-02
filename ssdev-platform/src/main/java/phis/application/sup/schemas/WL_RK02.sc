<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_RK02" alias="入库明细(WL_RK02)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase"
				startPos="24" />
		</key>
	</item>
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" />
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0" />
	<item id="ZBXH" alias="账簿序号" type="long" length="18" display="0" />
	<item ref="b.WZMC" mode="remote" width="140" />
	<item ref="b.WZGG" fixed="false" />
	<item id="ZCZH" alias="注册证号" length="30" />
	<item ref="b.WZDW" fixed="false" />
	<item id="KTSL" alias="可退数量" type="double" length="18" precision="2"
		virtual="true" fixed="false" />
	<item id="WZSL" alias="数量" type="double" length="18" precision="2" />
	<item id="WZJG" alias="进货价格" type="double" length="18" precision="4" />
	<item id="WZJE" alias="进货金额" type="double" length="18" precision="4" />
	<item id="LSJG" alias="零售价格" type="double" length="18" precision="4" />
	<item id="LSJE" alias="零售金额" type="double" length="18" precision="4" />
	<item ref="c.CJMC" fixed="true" width="160" />
	<item id="CJXH" alias="厂家序号" type="long" length="12" fixed="false"
		display="0">
	</item>
	<item id="GLFS" ref="b.GLFS" fixed="false" display="0">
		<dic>
			<item key="1" text="库存管理" />
			<item key="2" text="科室管理" />
		</dic>
	</item>
	<item id="FPHM" alias="发票号码" length="50" />
	<item id="FPRQ" alias="发票日期" type="date" />
	<item id="WZPH" alias="物资批号" length="20" />
	<item id="MJPH" alias="灭菌批号" length="20" display="0" />
	<item id="SCRQ" alias="生产日期" type="date" display="0" />
	<item id="SXRQ" alias="失效日期" type="date" />

	<item id="KCXH" alias="库存序号" type="long" length="18" fixed="true"
		display="0" />
	<item id="FKJE" alias="付款金额" type="double" length="18" precision="4"
		display="0" />
	<item id="HTMX" alias="合同明细" type="long" length="18" display="0" />
	<item id="YSBZ" alias="验收标志" type="int" length="1" display="0" />
	<item id="YFJE" alias="应付金额" type="double" length="18" precision="4"
		display="0" />
	<item id="CLFY" alias="材料费用" type="double" length="18" precision="4"
		display="0" />
	<item id="JGFY" alias="加工费用" type="double" length="18" precision="4"
		display="0" />
	<item id="QTFY" alias="其他费用" type="double" length="18" precision="4"
		display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" />
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ">
			<join parent="CJXH" child="CJXH"></join>
		</relation>
	</relations>

</entry>
