<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_ZCZB_YR" tableName="WL_ZCZB" alias="资产帐薄(WL_ZCZB)">
	<item id="ZBXH" alias="帐簿序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item ref="b.WZMC" width="180"/>
	<item id="WZBH" alias="物资编号" length="40" width="120"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
	<item ref="c.CJMC" width="200"/>
	<item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
	<item id="ZCYZ" alias="资产原值" type="double" length="18" precision="4"/>
	<item id="CZYZ" alias="重置原值" type="double" display="0" length="18" precision="4"/>
	<item id="WHYZ" alias="外汇原值" type="double" display="0" length="18" precision="4"/>
	<item id="HBDW" alias="货币单位" type="int" display="0" length="5"/>
	<item id="GHDW" alias="供货单位" type="long" display="0" length="18"/>
	<item id="ZYKS" alias="在用科室" type="int" display="0" length="6"/>
	<item id="BGGH" alias="保管工号" length="10" display="0"/>
	<item id="WZZT" alias="物资状态" type="int" length="2">
		<dic id="assetstatus" autoLoad="true"/>
	</item>
	<item id="WXZT" alias="维修状态" type="int" display="0" length="2"/>
	<item id="TZRQ" alias="添置日期" display="0" type="date"/>
	<item id="QYRQ" alias="启用日期" type="date"/>
	<item id="CZRQ" alias="重置日期" display="0" type="date"/>
	<item id="ZRRQ" alias="转入日期" display="0" type="date"/>
	<item id="BSRQ" alias="报损日期" display="0" type="date"/>
	<item id="ZJRQ" alias="折旧日期" display="0" type="date"/>
	<item id="JTZJ" alias="计提折旧" display="0" type="double" length="18" precision="4"/>
	<item id="ZJYS" alias="折旧月数" display="0" type="int" length="4"/>
	<item id="FCYS" alias="封存月数" display="0" type="int" length="4"/>
	<item id="ZCNH" alias="资产年号" display="0" length="4"/>
	<item id="WZTM" alias="资产条码" display="0" length="40"/>
	<item id="ZDTM" alias="自定条码" display="0" length="40"/>
	<item id="CCBH" alias="出厂编号" display="0" length="30"/>
	<item id="CLBZ" alias="锁定标志" display="0" type="int" length="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD_JBXX" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>				
	</relations>
</entry>
