<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_TYMX"   alias="家床退药明细">
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item ref="e.BRXM" alias="病人姓名" type="string" queryable="false"/>
	<item ref="e.ZYHM" alias="家床号" type="string" queryable="false"/>
	<item ref="b.YPMC" alias="药品名称" width="120" type="string"/>
	<item id="YPGG" alias="规格" length="20" width="50" type="string" renderer="onRendererNull"/>
	<item id="YFDW" alias="单位" length="4" width="50" type="string" renderer="onRendererNull"/>
	<item id="YPSL" alias="数量" type="double" length="9" precision="2"/>
	<item id="YPJG" alias="药品单价" type="double" length="13" precision="4" />
	<item id="JE" defaultValue="1" virtual="true" type="double" precision="2" alias="金额" renderer="onRenderer"/>
	<item ref="c.CDMC" alias="药品产地"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="YFSB" alias="药房识别" display="0" type="long" length="18"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" display="0"/>
	<!--<item ref="b.TYPE" display="0"/>-->
	<item ref="b.ZXBZ" display="0"/>
	<item ref="d.LSJG" display="0"/>
	<item ref="d.PFJG" display="0"/>
	<item id="SQRQ" alias="申请日期" type="datetime" not-null="1" display="0"/>
	<item id="TYRQ" alias="退药日期" type="datetime" display="0"/>
	<item id="TYBQ" alias="退药病区" type="long" length="18" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1" display="0"/>
	<item id="CZGH" alias="操作工号" length="10" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" display="0"/>
	<item id="ZFPB" alias="自费判别" type="int" length="2" display="0"/>
	<item id="TJBZ" alias="提交标志" type="int" length="1" display="0"/>
	<item id="TYQR" defaultValue="1" virtual="true" display="0"/>
	<item ref="b.FYFS" display="0"/>
	<item id="YZID" alias="医嘱ID" type="long" length="18" display="0"/>
	<item id="THBZ" alias="退回标志" type="int" length="8" not-null="1" display="0"/>
	<item id="THSJ" alias="退回时间" type="datetime" display="0"/>
	<item id="THR" alias="退回人" length="10" display="0"/>
	<item id="JLID" alias="记录ID" length="18" type="long" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
		<relation type="child" entryName="phis.application.sto.schemas.YK_CDXX" >
			<join parent="YPCD" child="YPCD"></join>
			<join parent="YPXH" child="YPXH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
		<relation type="parent" entryName="phis.application.fsb.schemas.JC_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
