<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_JKZK" alias="健康状况" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="zfmb" alias="(外)祖父母辈" type="string" colspan="2" group="家庭史">
		<dic render="LovCombo">
			<item key="1" text="高血压"/>
			<item key="2" text="高血脂"/>
			<item key="3" text="冠心病"/>
			<item key="4" text="卒中"/>
			<item key="5" text="乳腺癌"/>
			<item key="6" text="糖尿病"/>
			<item key="7" text="不详"/>
		</dic>
	</item>
	<item id="fmb" alias="父母辈" type="string" colspan="2" group="家庭史">
		<dic render="LovCombo">
			<item key="1" text="高血压"/>
			<item key="2" text="高血脂"/>
			<item key="3" text="冠心病"/>
			<item key="4" text="卒中"/>
			<item key="5" text="乳腺癌"/>
			<item key="6" text="糖尿病"/>
			<item key="7" text="不详"/>
		</dic>
	</item>
	<item id="tb" alias="同辈" type="string" colspan="2" group="家庭史">
		<dic render="LovCombo">
			<item key="1" text="高血压"/>
			<item key="2" text="高血脂"/>
			<item key="3" text="冠心病"/>
			<item key="4" text="卒中"/>
			<item key="5" text="乳腺癌"/>
			<item key="6" text="糖尿病"/>
			<item key="7" text="不详"/>
		</dic>
	</item>
	
	<item id="ywcj" alias="有无残疾" type="string" group="残疾情况">
		<dic>
			<item key="1" text="无残"/>
			<item key="2" text="听力残"/>
			<item key="3" text="语言残"/>
			<item key="4" text="肢体残"/>
			<item key="5" text="智力残"/>
			<item key="6" text="眼残"/>
			<item key="7" text="精神残"/>
		</dic>
	</item>
	<item id="sflz" alias="是否领证" type="string" group="残疾情况">
		<dic render="Radio" colWidth="50">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="cjzh" alias="残疾证号" type="string" colspan="2" group="残疾情况"/>
	
	<item id="jhwmy" alias="计划外免疫" type="string" colspan="2">
		<dic>
			<item key="1" text="乙肝疫苗"/>
			<item key="2" text="甲肝疫苗"/>
			<item key="3" text="流感疫苗"/>
			<item key="4" text="肺炎球菌疫苗"/>
			<item key="5" text="狂犬疫苗"/>
			<item key="6" text="精白破"/>
			<item key="7" text="麻疹"/>
			<item key="8" text="脊髓灰质炎糖丸"/>
			<item key="9" text="从未接种过"/>
			<item key="10" text="不详"/>
		</dic>
	</item>
	
	<item id="gmqk" alias="过敏情况" type="string" group="过敏史">
		<dic>
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="3" text="不详"/>
		</dic>
	</item>
	<item id="gmwz" alias="过敏物质" type="string" colspan="2" group="过敏史">
		<dic render="LovCombo">
			<item key="1" text="青霉素"/>
			<item key="2" text="磺胺"/>
			<item key="3" text="油漆"/>
			<item key="4" text="海鲜"/>
		</dic>
	</item>
	<item id="sg" alias="身高(cm)" type="string" group="体检"/>
	<item id="tz" alias="体重(kg)" type="string" group="体检"/>
	<item id="ssy" alias="收缩压(mmHg)" type="string" group="体检"/>
	<item id="szy" alias="舒张压(mmHg)" type="string" group="体检"/>
	<item id="xl" alias="心率(次/分)" type="string" group="体检"/>
</entry>
