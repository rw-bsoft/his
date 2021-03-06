<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_KSZC_HZ" tableName="WL_KSZC"  alias="科室帐册(WL_KSZC)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KSDM" alias="科室代码" type="long" length="18" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" display="0"/>
	<item id="KCXH" alias="库存序号" type="long" generator="assigned" display="0" length="18"/>
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="SCRQ" alias="生产日期" type="date" display="0"/>
	<item id="SXRQ" alias="失效日期" type="date" display="0"/>
	<item id="WZPH" alias="物资批号" length="20" display="0"/>
	<item id="YKSL" alias="预扣数量" type="double" length="18" precision="2" display="0"/>
	
	<item id="KSMC"  alias="科室名称" length="60"/>
	<item ref="b.WZMC" alias="物资名称" length="60" width="150"/>
	<item ref="b.WZGG" alias="规格" length="60"/>
	<item ref="b.WZDW" alias="单位" length="60"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="物资价格" type="double" length="18" precision="4" display="0"/>
	<item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
	<item id="ZRRQ" alias="转入日期" type="date" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.SYS_Office" >
			<join parent="KSDM" child="ID"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>
	</relations>
</entry>
