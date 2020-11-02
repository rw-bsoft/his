<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_TYMX" alias="病区退药明细">
	<item id="YPXH" alias="药品序号" type="long" length="18" display="0"/>
	<item ref="b.YPMC" alias="药品名称" width="120"/>
	<item id="YPGG" alias="规格" length="20" width="50" renderer="onRendererNull"/>
	<item id="YFDW" alias="单位" length="4" width="50" renderer="onRendererNull"/>
	<item id="YPSL" alias="数量" type="double" length="9" precision="2"/>
	<item id="YPJG" alias="药品单价" type="double" length="13" precision="4" />
	<!--  <item id="JE" defaultValue="1" virtual="true" type="double" precision="2" alias="金额" renderer="onRenderer"/>-->
	<item id="YZID" alias="医嘱ID" type="long" length="18" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
