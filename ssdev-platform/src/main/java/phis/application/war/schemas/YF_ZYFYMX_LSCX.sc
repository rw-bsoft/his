<?xml version="1.0" encoding="UTF-8"?>

<entry alias="住院病人发药明细"  entityName="YF_ZYFYMX" >
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item ref="c.BRCH" width="60"/>
	<item ref="c.BRXM" width="60"/>
	<item ref="b.YPMC" />
	<item ref="b.BFGG" />
	<item ref="b.BFDW" />
	<item ref="d.CDMC" />
	<item id="YPSL" alias="费用数量" type="double" length="10" precision="2" not-null="1" />
	<item id="YPDJ" alias="费用单价" type="double" length="12" precision="4" not-null="1" />
	<item id="LSJE" alias="零售金额" type="double" length="12" precision="2" width="140" not-null="1" summaryType="sum" summaryRenderer="LSJESummaryRenderer"/>
	<item ref="c.ZYH" display="0"/>
	<item id="YPXH" alias="费用序号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="ZFPB" alias="自负判别" type="int" length="2" display="0"/>
	<item id="YPLX" alias="药品类型" type="int" length="1" not-null="1" display="0"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
	<item id="FYFS" alias="发药方式" type="long" length="18" not-null="1" display="0"/>
	<item id="JFRQ" alias="记费日期" type="timestamp" not-null="1" display="0"/>
	<item id="LYBQ" alias="领药病区" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YFGG" display="0"/>
	<item ref="b.YFDW" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<relations>
		<relation type="child" entryName="YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
		<relation type="child" entryName="YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
