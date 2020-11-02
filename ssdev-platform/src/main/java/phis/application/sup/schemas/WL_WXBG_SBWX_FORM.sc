<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WXBG_SBWX_FORM" tableName="WL_WXBG" alias="维修报告(WL_WXBG)">
	<item id="WXXH" alias="维修序号" type="long" length="18" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase"  startPos="1" />
		</key>
	</item>
	<item id="WZXH" alias="物资序号" type="long" length="12" display="0"/>
	<item ref="b.WZMC" not-null="1"/>
	<item ref="b.WZGG" />
	<item ref="b.WZDW"/>
	<item id="WXBH" alias="维修编号" length="20" display="0"/>
	<item id="ZBXH" alias="帐薄序号" type="long" length="18" display="0"/>
	<item ref="d.WZBH" readOnly="true"/>
	<item id="CJXH" alias="厂家序号" type="long" length="18" display="0"/>
	<item ref="c.CJMC" readOnly="true"/>
	<item id="DWMC" alias="供应商" virtual="true" readOnly="true"/>
	<item id="WXDW" alias="维修单位" type="long" fixed="true"  length="18">
		<dic id="phis.dictionary.supplyUnit"  filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
	<item id="SBXZ" alias="设备现状" type="int"  length="2">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="部分维修，能使用"/>
			<item key="3" text="未能修复，不能使用"/>
			<item key="-1" text="建议报废"/>
		</dic>
	</item>
	<item id="GZXZ" alias="工作性质" length="200"  colspan="4" xtype="textarea"/>
	<item id="GZXX" alias="故障现象" length="200"  colspan="4" xtype="textarea"/>
	<item id="GZYY" alias="故障原因" length="200"  colspan="4" xtype="textarea"/>
	<relations>
		<relation type="parent" entryName="phis.application.sup.schemas.WL_WZZD_SBWX" />
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>  
		<relation type="parent" entryName="phis.application.sup.schemas.WL_ZCZB" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>  
	</relations>
</entry>
