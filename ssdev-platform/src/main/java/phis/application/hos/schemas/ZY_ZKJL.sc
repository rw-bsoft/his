<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_ZKJL" alias="转科记录">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
	<item id="HCLX" alias="换床类型" type="int" length="1"/>
	<item id="YSSQRQ" alias="医生申请日期" type="date"/>
	<item id="YSSQGH" alias="医生申请工号" length="10" />
	<item id="BQSQRQ" alias="病区申请日期" type="date"/>
	<item id="BQSQGH" alias="病区申请工号" length="10" defaultValue="%user.userId"/>
	<item id="BQZXRQ" alias="病区执行日期" type="date"/>
	<item id="BQZXGH" alias="病区执行工号" length="10"/>
	<item id="YSZXRQ" alias="医生执行日期" type="date"/>
	<item id="YSZXGH" alias="医生执行工号" length="10"/>
	<item id="ZXBZ" alias="执行标志" type="int" length="1"/>
	<item id="HQCH" alias="换前床号" length="12"/>
	<item id="HHCH" alias="换后床号" length="12"/>
	<item id="HQBQ" alias="换前病区" type="long" length="18"/>
	<item id="HHBQ" alias="换后病区" type="long" length="18"/>
	<item id="HQKS" alias="换前科室" type="long" length="18"/>
	<item id="HHKS" alias="换后科室" type="long" length="18"/>
	<item id="HQYS" alias="换前医生" length="10"/>
	<item id="HHYS" alias="换后医生" length="10"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
</entry>
