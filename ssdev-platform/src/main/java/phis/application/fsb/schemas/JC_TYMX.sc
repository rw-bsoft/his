<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_TYMX" alias="退药明细">
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" />
	<item id="SQRQ" alias="申请日期" type="timestamp" not-null="1"/>
	<item id="YPXH" alias="退药药品序号" width="180" type="long" length="18" not-null="1"/>
	<item ref="c.CDMC" alias="产地" />
	<item id="YPCD" alias="药品产地" display="0" type="long" length="18" not-null="1"/>
	<item id="YPSL" alias="数量" type="double" length="8" precision="4"/>
	<item id="YPJG" alias="单价" type="double" length="12" precision="6" not-null="1"/>
	<item id="CZGH" alias="操作工号" length="10"/>
	<item ref="b.YFMC" alias="发药药房" />
	<item id="TYRQ" alias="退药日期" type="timestamp"/>
	<item id="TYLX" alias="类型" type="int" length="1"/>
	<item id="YFSB" alias="药房识别" display="0" type="long" length="18"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.YF_YFLB" >
			<join parent="YFSB" child="YFSB"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
