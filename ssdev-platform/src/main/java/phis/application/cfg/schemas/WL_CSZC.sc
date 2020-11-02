<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CSZC" alias="初始帐册">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="int" display="0" length="8"/>
	<item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
	<item id="WZBH" alias="资产编号" length="40" display="0"/>
	<item ref="b.WZMC" alias="资产名称" length="20"/>
	<item ref="b.WZGG" alias="规格型号" length="8"/>
	<item ref="b.WZDW" alias="单位" length="8"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
	<item id="ZCYZ" alias="资产原值" type="double" length="12" precision="2"/>
	<item id="CZYZ" alias="重置原值" type="double" length="12" precision="2"/>
	<item id="WHYZ" alias="外汇原值" type="double" length="12" precision="2"/>
	<item id="HBDW" alias="货币单位" type="int" length="5">
		<dic id="phis.dictionary.currency" autoLoad="true"/>
	</item>
	<!--<item ref="d.DWMC" alias="供应商" type="long" mode="remote" length="12"/>-->
	<item id="GHDW" alias="供货单位" display="0" type="long" length="12"/>
	<item id="ZYKS" alias="在用科室" type="int" length="4">
		<dic id="phis.dictionary.department" autoLoad="true" searchField="PYCODE"/>
	</item>
	<item id="BGGH" alias="保管工号" display="0" length="10"/>
	<item id="WZZT" alias="物资状态" type="int" length="2">
		<dic id="phis.dictionary.assetstatus" autoLoad="true"/>
	</item>
	<item id="TZRQ" alias="添置日期" type="date"/>
	<item id="QYRQ" alias="启用日期" type="date"/>
	<item id="CZRQ" alias="重置日期" type="date"/>
	<item id="JTZJ" alias="计提折旧" type="double" length="12" precision="2"/>
	<item id="BSRQ" alias="报损日期" display="0" type="date"/>
	<item id="ZJRQ" alias="折旧日期"  display="0" type="date"/>
	<item id="ZJYS" alias="折旧月数" type="int" length="4"/>
	<item id="FCYS" alias="封存月数" type="int" length="4"/>
	<item id="WZTM" alias="资产条码" display="0" length="40"/>
	<item id="ZDTM" alias="自定条码" display="0" length="40"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_GHDW" >
			<join parent="DWXH" child="GHDW"></join>
		</relation>				
	</relations>
</entry>
