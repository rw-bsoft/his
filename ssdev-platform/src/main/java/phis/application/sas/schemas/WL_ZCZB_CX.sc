<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_ZCZB_CX"  tableName="WL_ZCZB" alias="资产帐薄(WL_ZCZB)">
	<item id="ZBXH" alias="帐簿序号" type="long" length="18" display="0"  not-null="1" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	
	<item id="WZBH" alias="物资编号" length="40" queryable="true" width = "150"/>
	<item ref="d.WZMC" alias="物资名称" length="60" queryable="true" width = "150"/>
	<item ref="d.WZGG" alias="规格" length="60"/>
	<item ref="d.GLFS" alias="管理方式" length="60"/>
	<item ref="d.WZDW" alias="单位" length="60"/>
	
	<item ref="e.CJMC" alias="厂家名称" length="60" width = "150"/>
	
	<item id="ZCYZ" alias="资产原值" type="double" length="18" precision="4" />
	<item id="CZYZ" alias="重置原值" type="double" length="18" precision="4" />
	<item id="TZRQ" alias="添置日期" type="date"/>
	<item id="QYRQ" alias="启用日期" type="date"/>
	<item id="CZRQ" alias="重置日期" type="date" display="0"/>
	<item id="ZRRQ" alias="转入日期" type="date" display="0" />
	<item id="WZZT" alias="物资状态" type="int" length="2" queryable="true">
		<dic>
			<item key="0" text="在库"/>
			<item key="1"  text="在用"/>
			<item key="2"  text="待报损"/>
			<item key="-1"  text="封存"/>
			<item key="-2"  text="报损"/>
			<item key="-3"  text="其它减少"/>
		</dic>
	</item>
	<item id="XHMC" alias="资产名称" length="60" display="0" />
	<item id="XHGG" alias="型号规格" length="60" display="0" />
	<item ref="b.OFFICENAME" alias="科室名称" length="60" />
	<item ref="d.HSLB"  alias="核算类别"  length="8"/>
	<item ref="c.DWMC" alias="单位名称" length="60"/>
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.SYS_Office" >
			<join parent="ZYKS" child="ID"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_GHDW" >
			<join parent="GHDW" child="DWXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>
	</relations>
</entry>
