<?xml version="1.0" encoding="UTF-8"?>

<entry  sort="JKXH desc" entityName="ZY_TBKK" alias="缴款记录">
	<item id="JKXH" alias="缴款序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" not-null="1"/>
	<item id="ZYH" alias="住院号" display="0" type="long" length="18" not-null="1"/>
	<item id="JKRQ" alias="缴款日期" type="timestamp" width="130" not-null="1"/>
	<item id="SJHM" alias="收据号码" length="20" not-null="1"/>
	<item id="JKJE" alias="缴款金额" type="double" length="10" precision="2" not-null="1"/>
	<item id="JKFS" alias="缴款方式" type="int" length="6" not-null="1">
		<dic id="phis.dictionary.payment"/>
	</item>
	<item id="ZPHM" alias="支票号码" display="0" length="20"/>
	<item id="JSCS" alias="结算次数" display="0" type="int" length="3" not-null="1"/>
	<item id="CZGH" alias="收费员" length="10">
		<dic id="phis.dictionary.user"/>
	</item>
	<item id="JZRQ" alias="结帐日期" display="0" type="timestamp"/>
	<item id="HZRQ" alias="汇总日期" display="0" type="timestamp"/>
	<item id="ZFRQ" alias="作废日期" display="0" type="timestamp"/>
	<item id="ZFGH" alias="作废工号" display="0" length="10"/>
	<item id="ZFPB" alias="备注" type="int" length="1" not-null="1">
		<dic>
			<item key="1" text="作废"/>
		</dic>
	</item>
	<item id="ZCPB" alias="转存判别" display="0" type="int" length="1" not-null="1"/>
</entry>
