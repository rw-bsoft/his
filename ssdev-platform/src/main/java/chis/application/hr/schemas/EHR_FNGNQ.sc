<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_FNGNQ" alias="妇女更年期" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="sg" alias="身高(cm)" type="string"/>
	<item id="tz" alias="体重(kg)" type="string"/>
	<item id="jjqc" alias="绝经情况" type="string" defaultValue="1">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="jjnl" alias="绝经年龄(岁)" type="string"/>
	<item id="sfjsjkjy" alias="是否接受健康教育" type="string" defaultValue="1">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="cjzz" alias="常见症状" type="string" defaultValue="1">
		<dic render="LovCombo">
			<item key="1" text="潮热出血"/>
			<item key="2" text="感觉异常"/>
			<item key="3" text="失眠"/>
			<item key="4" text="情绪波动"/>
			<item key="5" text="犹豫多疑"/>
		</dic>
	</item>
</entry>
