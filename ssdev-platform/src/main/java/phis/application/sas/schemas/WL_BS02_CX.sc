<?xml version="1.0" encoding="UTF-8"?>
<entry id="WL_BS02_CX" tableName="WL_BS02" alias="报损明细(WL_BS02)"
	sort="b.BSRQ desc">
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18"
		not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase"
				startPos="24" />
		</key>
	</item>
	<item ref="c.WZMC" alias="物资名称" fixed="false" queryable="true"
		width="150" />
	<item ref="c.WZGG" alias="规格" fixed="false" />
	<item ref="c.WZDW" alias="单位" fixed="false" />
	<item id="WZSL" alias="数量" type="double" length="18" precision="2" />
	<item id="WZJG" alias="单价" type="double" length="18" precision="4"
		fixed="false" />
	<item id="WZJE" alias="金额" type="double" length="18" precision="4"
		fixed="false" />

	<item ref="b.BSKS" alias="申请科室" type="long" length="18" fixed="false">
		<dic id="phis.dictionary.department" autoLoad="true">
		</dic>
	</item>

	<item ref="b.BSRQ" alias="报损日期" fixed="false" />
	<relations>
		<relation type="parent" entryName="phis.application.sup.schemas.WL_BS01">
			<join parent="DJXH" child="DJXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD">
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ">
			<join parent="CJXH" child="CJXH"></join>
		</relation>
	</relations>
</entry>
