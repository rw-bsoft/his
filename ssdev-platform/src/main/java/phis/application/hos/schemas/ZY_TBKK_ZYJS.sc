<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_TBKK" sort="JKXH desc"  alias="缴款记录">
	<item id="JKXH" alias="缴款序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" not-null="1"/>
	<item id="ZYH" alias="住院号" display="0" type="long" length="18" not-null="1"/>
	<item id="JKRQ" alias="缴款日期" type="timestamp" width="130" not-null="1"/>
	<item id="JKJE" alias="缴款金额" type="double" length="10" precision="2" not-null="1"/>
	<item id="JKFS" alias="方式" type="int" length="6" width="50" not-null="1">
		<dic id="phis.dictionary.payment"/>
	</item>
	<item id="SJHM" alias="收据号" length="20" width="60" not-null="1"/>
	<item id="JSCS" alias="结算次数" display="0" type="int" length="3" not-null="1"/>
	<item id="CZGH" alias="收费员" length="10" width="50">
		<dic id="phis.dictionary.user"/>
	</item>
</entry>
