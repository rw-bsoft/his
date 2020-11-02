<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_HCMX" alias="换床明细">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="HCRQ" alias="换床日期" type="date" not-null="1" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="ZZRQ" alias="终止日期" type="date"/>
	<item id="HCLX" alias="换床类型" type="int" length="1" not-null="1"/>
	<item id="HQCH" alias="换前床号" length="6"/>
	<item id="HHCH" alias="换后床号" length="6"/>
	<item id="HQKS" alias="换前科室" type="long" length="18"/>
	<item id="HHKS" alias="换后科室" type="long" length="18"/>
	<item id="HQBQ" alias="换前病区" type="long" length="18"/>
	<item id="HHBQ" alias="换后病区" type="long" length="18"/>
	<item id="JSCS" alias="结算次数" type="int" length="3"/>
	<item id="CZGH" alias="操作工号" length="10"/>
</entry>
