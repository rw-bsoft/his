<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病区提交明细表" entityName="BQ_TJ02" sort="a.YZXH,a.QRRQ">
	<item ref="c.BRCH" alias="床号" />
	<item ref="c.BRXM" alias="姓名" display="0"/>
	<item ref="b.YPMC" alias="药品名称" width="120"/>
	<item id="KSSJ" alias="开嘱时间" type="timestamp" not-null="1" width="130"/>
	<item id="YCSL" alias="一次数量" type="double" length="8" precision="2" not-null="1" width="100"/>
	<item id="YFGG" alias="规格" length="20" renderer="onRendererNull"/>
	<item id="YFDW" alias="单位" length="4" renderer="onRendererNull"/>
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" not-null="1"/>
	<item id="FYJE" alias="金额" type="double" length="12" precision="2" not-null="1" renderer="onRenderer"/>
	<item ref="g.CDMC"   alias="药品产地"/>
	<item id="SYPC" alias="频次" length="6" display="0"/>
	<item ref="c.ZYH" display="0"/>
	<item ref="c.ZLXZ" display="0"/>
	<item ref="c.ZSYS" display="0"/>
	<item id="YTCS" alias="一天次数" type="int" length="2" not-null="1" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="QRRQ" alias="确认日期" type="timestamp" not-null="1" width="130"/>
	<item ref="d.FYFS" display="0"/>
	<item ref="d.TJBQ" display="0"/>
	<item id="JFRQ" alias="记费日期" type="timestamp" not-null="1" display="0"/>
	<item id="TJXH" alias="提交序号" type="long" length="18" not-null="1" display="0"/>
	<item id="FYGH" alias="发药工号" length="10" display="0"/>
	<item id="FYRQ" alias="发药日期" type="timestamp" display="0"/>
	<item id="FYQR" defaultValue="0" virtual="true" display="0"/>
	<item id="FYTS" defaultValue="0" virtual="true" display="0"/>
	<item id="YZXH" alias="医嘱序号" type="long" length="18" not-null="1" display="0"/>
	<item id="LSYZ" alias="临时医嘱" type="int" length="1" display="0"/>
	<item id="FYBZ" alias="发药标志" type="int" length="1" not-null="1" display="0"/>
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item ref="e.QZCL"  display="0"/>
	<item id="FYCS" defaultValue="0" virtual="true" display="0"/>
	<item id="YPSL" defaultValue="0.0000" virtual="true" display="0"/>
	<item id="DSJBF" defaultValue="0" virtual="true" display="0"/>
	<item id="FYSL" alias="发药数量" type="double" length="8" precision="2" not-null="1" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1" display="0"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" not-null="1" display="0"/>
	<item id="YYTS" alias="用药天数" type="int" length="4" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
		<relation type="child" entryName="phis.application.war.schemas.BQ_TJ01" >
			<join parent="TJXH" child="TJXH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
		<relation type="child" entryName="phis.application.sto.schemas.YK_YPXX" >
			<join parent="YPXH" child="YPXH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.SYS_Office" >
			<join parent="TJBQ" child="ID"></join>
			<join parent="JGID" child="ORGANIZCODE"></join>
		</relation>
		<relation type="child" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
