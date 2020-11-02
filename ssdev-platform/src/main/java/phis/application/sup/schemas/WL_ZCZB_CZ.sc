<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_ZCZB_CZ" tableName="WL_ZCZB" alias="资产帐薄(WL_ZCZB)">
	<item id="ZBXH" alias="帐簿序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="WZBH" alias="物资编号" length="40" width="120"/>
	<item ref="b.WZMC" width="150"/>
	<item ref="b.WZDW"/>
	<item ref="b.WZGG"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" length="18"/>
	<item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
	  <item id="ZCYZ" alias="资产原值" type="double" length="18" precision="4" display="0"/>
	<item id="CZYZ" alias="资产原值" type="double"  length="18" precision="4"/>
	<item id="KCXH" alias="库存序号" type="long" length="18" display="0"/>
	<item id="ZYKS" alias="在用科室" type="int" length="6">
		<dic id="phis.dictionary.department" autoLoad="true">
		</dic>
	</item>
	<item id="CJXH" alias="厂家序号" type="long" display="0" length="12"/>
	<item ref="c.CJMC" width="150"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD_JBXX" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>				
	</relations>
</entry>
