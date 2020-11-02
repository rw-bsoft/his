<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZKC_EJ_HZ" tableName="WL_WZKC"  alias="物资库存(WL_WZKC)">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" defaultValue="%user.manageUnit.id" display="0"/>
	<item id="KCXH" alias="库存序号" type="long" generator="assigned" length="18" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0" />
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12" display="0"/>
	<item id="WZMC" alias="物资名称" virtual="true" length="60" queryable="true"/>
	<item id="WZGG" alias="规格" virtual="true" length="60"/>
	<item id="CJMC" alias="厂家名称" virtual="true" length="60" display="0"/>
	<item id="WZDW" alias="单位" virtual="true" length="60"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
    <item id="YKSL" alias="预扣数量" type="double" length="18" precision="2" defaultValue="0"/>
    <item id="KYSL" alias="可用数量" virtual="true" type="double" length="18" precision="2" />
	<item id="WZJG" alias="物资价格" type="double" length="18" display="0" precision="4"/>
	<item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
	<item id="SCRQ" alias="生产日期" type="date" display="0"/>
	<item id="SXRQ" alias="失效日期" type="date" display="0"/>
	<item id="WZPH" alias="物资批号" length="20" display="0"/>
	<item id="MJPH" alias="灭菌批号" length="20" display="0"/>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8">
		<dic id="phis.dictionary.booksCategory"  autoLoad="true" />
	</item>
	<item id="FSRQ" alias="入库日期" type="date" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
			<join parent="WZXH" child="WZXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_SCCJ" >
			<join parent="CJXH" child="CJXH"></join>
		</relation>
	</relations>
</entry>
