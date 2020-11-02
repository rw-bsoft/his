<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YJHS" alias="医技科室核算汇总表">
	<item id="HZXH" alias="汇总序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="15" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" not-null="1"/>
	<item id="KSDM" alias="科室代码" length="18" not-null="1" type="long"/>
	<item id="YSDM" alias="医生代码" type="string" length="10" not-null="1"/>
	<item id="TJFS" alias="统计方式" length="1" not-null="1" type="int"/>
	<item id="GZRQ" alias="工作日期" type="timestamp" not-null="1"/>
	<item id="JCD" alias="检查单" length="4" not-null="1" type="int"/>
	<item id="XYF" alias="西药方" length="4" not-null="1" type="int"/>
	<item id="ZYF" alias="中药方" length="4" not-null="1" type="int"/>
	<item id="CYF" alias="草药方" length="4" not-null="1" type="int"/>
	<item id="HJJE" alias="合计金额" length="12" precision="2" not-null="1" type="double"/>
	<item id="MZRC" alias="门诊人次" length="4" not-null="1" type="int"/>
</entry>
