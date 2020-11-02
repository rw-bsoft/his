<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CSKC" alias="初始库存">
	<item id="JLXH" alias="记录序号" type="long" length="18"  not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZBLB" alias="类别序号" type="int" display="0" length="8"/>
	<item id="CSLB" alias="初始类别" type="int" display="0" length="1"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
	<item ref="b.WZMC" alias="物资名称" length="20"/>
	<item ref="b.WZGG" alias="规格型号" length="8"/>
	<item ref="b.WZDW" alias="单位" length="8"/>
	<item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
	<item id="WZSL" alias="物资数量"  type="double" length="18" precision="2"/>
	<item id="WZJG" alias="物资价格" type="double" length="18" precision="4"/>
	<item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
	<item id="LSJG" alias="零售价格" type="double" length="18" precision="4"/>
	<item id="LSJE" alias="零售金额" type="double" length="18" precision="4"/>
	<item ref="c.CJMC" alias="生产厂家" length="8"/>
	<item id="KSDM" alias="科室代码" display="0" type="long" length="18"/>
	<!--<item ref="d.DWMC" alias="供货单位" type="long" mode="remote" length="12"/>-->
	<item id="GHDW" alias="供货单位" display="0" type="long" length="12"/>
	<item id="RKRQ" alias="入库时间" type="date"/>
	<item id="SCRQ" alias="生产日期" type="date"/>
	<item id="SXRQ" alias="失效日期" type="date"/>
	<item id="WZPH" alias="物资批号" length="20"/>
	<item id="MJPH" alias="灭菌批号" length="20"/>
	<item id="KCXH" alias="库存序号" display="0" type="long" length="18"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>
		<!--<relation type="parent" entryName="phis.application.cfg.schemas.WL_GHDW" >
				<join parent="DWXH" child="GHDW"></join>
			</relation>-->				
	</relations>
</entry>
