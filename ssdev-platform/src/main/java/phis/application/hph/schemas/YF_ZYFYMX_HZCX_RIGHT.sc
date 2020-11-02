<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_ZYFYMX" alias="住院病人发药明细"  sort="a.FYRQ desc">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="JLID" alias="记录ID" type="string" length="20" not-null="1" display="0"/>
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item ref="c.ZYHM" />
	<item ref="c.BRXM" width="60"/>
	<item id="LYBQ" alias="领药病区" type="long" length="18" not-null="1" >
		<dic id="phis.dictionary.department_bq" autoLoad="true"></dic>
	</item>
	<item ref="c.BRCH" width="60"/>
	<item id="FYRQ" alias="日期" type="datetime" not-null="1" width="140" />
	<item ref="b.YPMC" />
	<item id="YPGG" alias="药品规格" length="20"/>
	<item id="YFDW" alias="药房单位" length="4"/>
	<item id="YPSL" alias="费用数量" type="double" length="10" precision="2" not-null="1" />
	<item id="LSJE" alias="零售金额" type="double" length="12" precision="2" not-null="1" />
	<item ref="d.CDMC" />
	<item ref="e.PERSONNAME" alias="操作人"/>
	<item id="YPDJ" alias="费用单价" type="double" length="12" precision="4" not-null="1" />
	<item id="YPXH" alias="费用序号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="FYFS" alias="发药方式" type="long" length="18" not-null="1" display="0"/>
	<item id="JFRQ" alias="记费日期" type="datetime" not-null="1" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="YFSB" alias="药房识别" type="long" length="18" not-null="1" display="0"/>
	<item id="FYLX" alias="发药类型" type="int" length="1" display="0">
		<dic id="phis.dictionary.dispensingType"></dic>
	</item>
	<item ref="f.JLID" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
		<relation type="child" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.SYS_Personnel" >
			<join parent="QRGH" child="PERSONID"></join>
		</relation>
		<relation type="child" entryName="phis.application.hph.schemas.YF_FYJL" >
			<join parent="JLID" child="JLID"></join>
		</relation>
	</relations>
</entry>
