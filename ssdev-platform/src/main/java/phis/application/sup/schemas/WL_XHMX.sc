<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_XHMX" alias="消耗明细(WL_XHMX)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="BRHM" alias="病人号码" length="30"/>
	<item id="BRXM" alias="病人姓名" length="30"/>
	<item ref="b.WZMC"  width="120"/>
	<item ref="b.WZGG" fixed="true"/>
	<item ref="b.WZDW" fixed="true"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="KSMC" alias="科室名称" length="40"/>
	<item id="BRID" alias="患者ID" type="long" length="18" display="0"/>
	<item id="BRLY" alias="病人来源" length="10">
		<dic>
			<item key="1" text="门诊"/>
			<item key="2" text="住院"/>
		</dic>
	</item>
	<item id="XHRQ" alias="消耗日期" type="date"/>
    
	<item id="KSDM" alias="科室" type="long" length="18" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="ZTBZ" alias="状态标志" type="int" length="1" display="0"/>
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0"/>
	<item id="XMJE" alias="项目金额" type="double" length="18" precision="4" display="0"/>
	<item id="MZXH" alias="门诊序号" type="long" length="18" display="0"/>
    <item id="KCXH" alias="库存序号" type="long" display="0" length="18"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
	</relations>
</entry>
