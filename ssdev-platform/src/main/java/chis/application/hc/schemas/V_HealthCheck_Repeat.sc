<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.V_HealthCheck_Repeat" tableName="chis.application.hc.schemas.V_HealthCheck_Repeat"  alias="健康检查表" >
	<item id="healthCheck" alias="检查单号" length="16"
		type="string" pkey="true" generator="assigned" not-null="1" display="1" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" length="32" type="string" display="0" />
	<item id="personName" alias="姓名" type="string" length="20" />
	<item id="idCard" alias="身份证号" type="string" length="20" width="160"/>
	<item id="repeat" alias="重复条数" type="string" length="20" />
</entry>
