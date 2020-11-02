<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PHSA_CRB" alias="传染病">
	<item id="CRBBH" alias="传染病编号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="22543"/>
		</key>
	</item>
	<item id="GXJG" alias="管辖机构" length="20" width="220" type="string">
		<dic id="chis.@manageUnit" render="Tree"/>
	</item>
	<item id="CRBBGL" alias="传染病疫情报告率" length="10" width="220" type="string"/>
	<item id="CRBBGJSL" alias="传染病疫情报告及时率" length="10" width="220" type="string"/>
	<item id="TJSJ" alias="统计时间" display="0" type="datetime" width="100">
	</item>
</entry>