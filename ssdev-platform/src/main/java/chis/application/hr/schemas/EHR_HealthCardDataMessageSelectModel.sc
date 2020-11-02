<?xml version="1.0" encoding="UTF-8"?>
<entry   entityName="chis.application.hr.schemas.EHR_HealthCardDataMessageSelectModel" alias="短信模块查询Model" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="mblx" alias="模版类型" type="string" defaultValue="1">
		<dic render="Radio" colWidth="50">
			<item key="1" text="公有"/>
			<item key="2" text="私有"/>
		</dic>
	</item>
	<item id="ywlx" alias="业务类型" defaultValue="1">
		<dic>
			<item key="1" text="高血压" />
			<item key="2" text="糖尿病" />
			<item key="3" text="肿瘤" />
			<item key="4" text="肺结核" />
			<item key="5" text="计免" />
			<item key="6" text="妇保" />
			<item key="7" text="儿保" />
			<item key="8" text="艾性" />
		</dic>
	</item>
	<item id="mbjj" alias="模版简介" colspan="2"></item>
	<item id="mbmc" alias="模版名称" colspan="2"></item>
	<item id="mbnr" alias="模版内容" colspan="2" xtype="textarea"></item>
	<item id="jlys" alias="建立医生" fixed="true"></item>
	<item id="jlsj" alias="建立时间" fixed="true"></item>
	<item id="xgys" alias="修改医生" fixed="true"></item>
	<item id="xgsj" alias="修改时间" fixed="true"></item>
	
</entry>





