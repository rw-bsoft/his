<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_TBKK" alias="退补缴款">
	<item id="JKXH" alias="缴款序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
	<item id="JKRQ" alias="缴款日期" type="date" not-null="1"/>
	<item id="JKJE" alias="缴款金额" type="double" length="10" precision="2" not-null="1"/>
	<item id="JKFS" alias="缴款方式" type="int" length="6" not-null="1"/>
	<item id="SJHM" alias="收据号码" length="20" not-null="1"/>
	<item id="ZPHM" alias="支票号码" length="20"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1"/>
	<item id="CZGH" alias="操作工号" length="10"/>
	<item id="JZRQ" alias="结帐日期" type="date"/>
	<item id="HZRQ" alias="汇总日期" type="date"/>
	<item id="ZFRQ" alias="作废日期" type="date"/>
	<item id="ZFGH" alias="作废工号" length="10"/>
	<item id="ZFPB" alias="作废判别" type="int" length="1" not-null="1"/>
	<item id="ZCPB" alias="转存判别" type="int" length="1" not-null="1"/>
</entry>
