<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_ShhjApp" alias="生活环境" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="COOKAIRTOOL" alias="厨房排风设施" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="油烟机"/>
			<item key="3" text="换气扇"/>
			<item key="4" text="烟筒"/>
		</dic>
	</item>
	<item id="FUELTYPE" alias="燃料类型" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="液化气"/>
			<item key="2" text="煤"/>
			<item key="3" text="天然气"/>
			<item key="4" text="沼气"/>
			<item key="5" text="柴火"/>
			<item key="6" text="其他"/>
		</dic>
	</item>
	<item id="WATERSOURCECODE" alias="饮水" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="自来水"/>
			<item key="2" text="经净化过滤的水"/>
			<item key="3" text="井水"/>
			<item key="4" text="河湖水"/>
			<item key="5" text="塘水"/>
			<item key="6" text="其他"/>
		</dic>
	</item>
	<item id="livestockColumn" alias="禽畜栏" type="string" length="2" display="2">
		<dic id="chis.dictionary.livestockColumn"/>
	</item>
	<item id="WASHROOM" alias="厕所" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="卫生厕所"/>
			<item key="2" text="一格或二格粪池式"/>
			<item key="3" text="马桶"/>
			<item key="4" text="露天粪坑"/>
			<item key="5" text="简易棚厕"/>
		</dic>
	</item>
</entry>
