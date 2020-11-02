<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_HealthCheck" alias="基本情况" sort="checkDate desc" >
<item id="healthCheck" alias="检查单号" length="16" width="130"
		type="string" pkey="true" generator="assigned" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item queryable="true" id="checkDate" alias="年检日期" type="date"
		not-null="true" defaultValue="%server.date.today"/>
	<!-- <item id="checkWay" alias="检查途径"
		length="20" type="string" queryable="true" width="200" display="1" defaultValue="1">
		<dic render="LovCombo">
			<item key="1" text="健康档案" />
			<item key="2" text="老年人" />
			<item key="3" text="高血压" />
			<item key="4" text="糖尿病" />
			<item key="5" text="精神病" />
		</dic>
	</item>-->
	
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
