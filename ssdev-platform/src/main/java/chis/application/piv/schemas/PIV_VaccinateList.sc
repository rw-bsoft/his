<?xml version="1.0" encoding="utf-8"?>

<entry alias="接种记录">
	<item id="recordId" alias="记录编号" type="string" length="16"
		not-null="1" width="150" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" display="2" />
	<item id="immuneType" alias="免疫类型" type="string" length="1"
		width="100" display="0"/>
	<item id="vaccineCode" alias="疫苗-名称代码" type="string" length="4"
		width="100" display="0" />
	<item id="vaccineName" alias="疫苗名称" type="string" length="50"
		width="200" />
	<item id="vaccineBN" alias="疫苗-批号" type="string" length="20"
		width="100" />
	<item id="times" alias="针次" tRRype="string" length="10" />
	<item id="planDate" alias="应接日期" type="date" width="100"
		display="0" />
	<item id="vaccinateDate" alias="接种日期" type="date" width="100" />
	<item id="vaccinatePlace" alias="接种地点" type="string" length="100"
		width="200" />
	<item id="vaccineManuCode" alias="疫苗生产厂家代码" type="string" length="2"
		display="0" />
	<item id="vaccineManu" alias="疫苗生产厂家" type="string" length="50"
		display="0" />
	<item id="vaccinateUser" alias="疫苗接种者姓名" type="string" length="30"
		display="0" />
	<item id="vaccinateUnit" alias="疫苗接种单位名称" type="string" length="70"
		display="0" />
	<item id="reactionCode" alias="预防接种后不良反应症状" type="string" length="2"
		width="200" />
	<item id="reactionDate" alias="预防接种后不良反应发生日期" type="date"
		display="0" />
	<item id="reactionTreat" alias="预防接种后不良反应处理结果" type="string"
		length="1000" display="0" />
</entry>
