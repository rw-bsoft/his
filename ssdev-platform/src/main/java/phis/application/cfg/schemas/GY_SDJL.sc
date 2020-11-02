<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SDJL" alias="医院医疗明细项目">
	<item id="JLXH" alias="记录序号" length="18" type="long" not-null="1"
		pkey="true">
		<key>
			<rule type="sequence" seqName="seq_sdjl" />
		</key>
	</item>
	<item id="YWXH" alias="业务序号" length="4" type="string" />
	<item id="BRID" alias="病人ID" length="18" type="long" />
	<item id="BRXM" alias="病人姓名" length="50" type="string" />
	<item id="CZGH" alias="操作工号" length="50" type="string" />
	<item id="CZXM" alias="操作员姓名" length="20" type="string" />
	<item id="SDSJ" alias="锁定时间" length="1" type="datetime" />
	<item id="JGID" alias="机构ID" length="20" type="string" />
</entry>
