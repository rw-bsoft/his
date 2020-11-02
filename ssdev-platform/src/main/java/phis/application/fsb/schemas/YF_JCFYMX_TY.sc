<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_JCFYMX" alias="家床病人发药明细">
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="YFSB" alias="药房识别" type="long" display="0" length="18" not-null="1"/>
	<item id="FYRQ" alias="发药日期" type="date" display="0" not-null="1"/>
	<item ref="d.YPMC" alias="药品名称" />
	<item id="YPXH" alias="已发药品序号" width="180" type="long" length="18" not-null="1" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" display="0" length="18" not-null="1"/>
	<item ref="c.CDMC" alias="产地" />
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" not-null="1"/>
	<item id="YPSL" alias="数量" type="double" length="10" precision="2" not-null="1"/>
	<item ref="b.YFMC" alias="发药药房" />
	<item id="YPGG" alias="规格" length="20" display="0"/>
	<item id="YFDW" alias="药房单位" length="4" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" not-null="1" display="0"/>
	<item id="LYBQ" alias="领药病区" type="long" length="18" not-null="1" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1" display="0" />
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" display="0"/>
	 <item id="ZFPB" alias="自负判别" type="int" length="2" display="0"/>
	 <item id="YZXH" alias="医嘱序号" type="long" length="18" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.pha.schemas.YF_YFLB" >
			<join parent="YFSB" child="YFSB"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
