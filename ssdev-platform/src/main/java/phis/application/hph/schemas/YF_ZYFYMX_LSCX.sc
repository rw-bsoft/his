<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_ZYFYMX" alias="住院病人发药明细"  >
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item ref="c.BRCH" width="60"/>
	<item ref="c.BRXM" width="60"/>
	<item ref="b.YPMC" />
	<item ref="b.BFGG" width="100"/>
	<item ref="b.BFDW" />
	<item ref="d.CDMC" width="100"/>
	<item id="YPSL" alias="数量" type="double" length="10" precision="2" not-null="1" width="60" />
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" not-null="1" width="60" />
	<item id="LSJE" alias="零售金额" type="double" length="12" precision="2" not-null="1" width="100" summaryType="sum" summaryRenderer="LSJESummaryRenderer"/>
	<item id="YPXH" alias="费用序号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="ZFPB" alias="自负判别" type="int" length="2" display="0"/>
	<item id="YPLX" alias="药品类型" type="int" length="1" not-null="1" display="0"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
	<item id="FYFS" alias="发药方式" type="long" length="18" not-null="1" display="0"/>
	<item id="JFRQ" alias="记费日期" type="datetime" not-null="1" display="0"/>
	<item id="LYBQ" alias="领药病区" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YFGG" display="0"/>
	<item ref="b.YFDW" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.mds.schemas.YK_TYPK_BQFY" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
		<relation type="child" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
