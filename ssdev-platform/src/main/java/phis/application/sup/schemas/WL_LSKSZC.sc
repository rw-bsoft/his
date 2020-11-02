<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_LSKSZC" alias="历史科室账册(WL_LSKSZC)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="JZXH" alias="结帐序号" type="long" length="18">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" startPos="24" />
		</key>
	</item>
	<item id="CWYF" alias="财务月份" length="6"/>
	<item id="KSDM" alias="科室代码" type="long" length="18"/>
	<item id="KFXH" alias="库房序号" type="int" length="8"/>
	<item id="ZBLB" alias="账簿类别" type="int" length="8"/>
	<item id="KCXH" alias="库存序号" type="long" length="18"/>
	<item id="WZXH" alias="物资序号" type="long" length="18"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12"/>
	<item id="SCRQ" alias="生产日期" type="date"/>
	<item id="SXRQ" alias="失效日期" type="date"/>
	<item id="WZPH" alias="物资批号" length="20"/>
	<item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="物资价格" type="double" length="18" precision="4"/>
	<item id="WZJE" alias="物资金额" type="double" length="18" precision="4"/>
	<item id="ZRRQ" alias="转入入期" type="date"/>
	<item id="YKSL" alias="预扣数量" type="double" length="18" precision="2"/>
</entry>
