<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_PSJL" tableName="YS_MZ_PSJL" alias="皮试记录表"
	sort="SQSJ desc">
	<item id="JLBH" alias="记录编号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="皮试医疗机构" type="string" length="20" width="100"
		display="0" />
	<item id="BRBH" alias="病人编号" type="long" length="18" not-null="1"
		display="0" />
	<item id="CFSB" alias="处方识别" type="long" length="18" display="0" />
	<item id="BRXM" alias="姓名" width="60" />
	<item id="FPHM" alias="发票号码" width="100" type="string" />
	<item id="CFHM" alias="处方号码" width="100" type="string" />
</entry>