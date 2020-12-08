<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ"  alias="汇总发药">
	<item ref="b.YPMC" alias="药品名称" />
	<item ref="d.YFGG" alias="规格" renderer="onRendererNull"/>
	<item ref="d.YFDW" alias="单位" renderer="onRendererNull"/>
	<item id="YPYF" alias="给药方式"  type="string" />
	<item id="CDMC" defaultValue="" virtual="true" alias="药品产地" display="1"/>
	<item id="YCSL" alias="药品数量" type="double" length="8" precision="2" not-null="1" display="1"/>
	<item id="FYSL" alias="实发数量" type="double" length="8" precision="2" not-null="1" display="0"/>
	<item id="YPDJ" alias="药品单价" type="double" length="12" precision="4" not-null="1" display="1"/>	
	<item id="FYJE" defaultValue="0.00" type="double" alias="金额" virtual="true" display="1"/>
	<item ref="c.BRCH" display="0"/>
	<item id="JEHJ" defaultValue="0" virtual="true" display="0" alias="金额合计 "/>
	<item ref="c.ZYH" display="0"/>
	<!--<item ref="c.MRCS" display="0"/>-->
	<item id="MZCS" alias="每周次数" type="int" length="1" not-null="1" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="1" not-null="1" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="KSSJ" alias="开始时间" type="datetime" display="0"/>
	<item id="QRSJ" alias="确认时间" type="datetime" display="0"/>
	<item id="TZSJ" alias="停止时间" type="datetime" display="0"/>
	<item id="FYQR" defaultValue="0" virtual="true" display="0"/>
	<item id="FYTS" defaultValue="0" virtual="true" display="0"/>
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="SYBZ" alias="使用标志" type="int" length="1" not-null="1" display="0"/>
	<item id="YPCD" defaultValue="0" virtual="true" display="0"/>
	<item ref="c.ZSYS" display="0"/>
	<relations>
	<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
		<join parent="YPXH" child="YPXH"></join>
	</relation>
	<relation type="child" entryName="phis.application.hos.schemas.ZY_BRRY" >
		<join parent="ZYH" child="ZYH"></join>
		<join parent="JGID" child="JGID"></join>
	</relation>
	<relation type="child" entryName="phis.application.pha.schemas.YF_YPXX" >
		<join parent="YPXH" child="YPXH"></join>
		<join parent="JGID" child="JGID"></join>
	</relation>
	</relations>
</entry>
