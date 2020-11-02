<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_ETMQQK" alias="目前情况" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="hjqk" alias="合局情况" type="string" group="目前情况">
		<dic>
			<item key="1" text="与父母"/>
			<item key="2" text="与祖父母"/>
			<item key="3" text="三代同堂"/>
			<item key="4" text="亲属寄养"/>
			<item key="5" text="寄养"/>
			<item key="6" text="其他"/>
		</dic>
	</item>
	<item id="sfdq" alias="是否单亲" type="string" group="目前情况">
		<dic render="Radio" colWidth="50">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="shfs" alias="生活方式" type="string" group="目前情况">
		<dic>
			<item key="1" text="散居"/>
			<item key="2" text="日托"/>
			<item key="3" text="全托"/>
		</dic>
	</item>
	<item id="rtes" alias="入托儿所(月)" type="string" group="目前情况"/>
	<item id="ryey" alias="入幼儿园(岁)" type="string" group="目前情况"/>
	<item id="sxx" alias="上小学(岁)" type="string" group="目前情况"/>
	<item id="wyfs" alias="喂养方式" type="string" group="目前情况">
		<dic>
			<item key="1" text="纯母乳喂养"/>
			<item key="2" text="母乳喂养"/>
			<item key="3" text="人工喂养"/>
		</dic>
	</item>
	<item id="mrwy" alias="母乳喂养(月)" type="string" group="目前情况"/>
	<item id="tjfs" alias="添加副食(月)" type="string" group="目前情况"/>
	<item id="sfcnn" alias="是否吃牛奶" type="string" group="目前情况">
		<dic>
			<item key="1" text="不吃"/>
			<item key="2" text="偶尔吃"/>
			<item key="3" text="天天吃"/>
		</dic>
	</item>
	
	<item id="jwsbj" alias="既往史标记" type="string" group="疾病残疾">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="jws" alias="既往史" type="string" colspan="2" group="疾病残疾">
		<dic render="LovCombo">
			<item key="1" text="哮喘"/>
			<item key="2" text="反复呼吸道感染"/>
			<item key="3" text="肺炎"/>
			<item key="4" text="癫痫"/>
			<item key="5" text="先心"/>
			<item key="6" text="水痘"/>
			<item key="7" text="腮腺炎"/>
			<item key="8" text="糖尿病"/>
			<item key="9" text="心脑血管疾病"/>
			<item key="10" text="胃炎"/>
		</dic>
	</item>
	
	<item id="gmsbj" alias="过敏史标记" type="string" group="疾病残疾">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="gmwz" alias="过敏物质" type="string" colspan="2" group="疾病残疾">
		<dic render="LovCombo">
			<item key="1" text="青霉素"/>
			<item key="2" text="油漆"/>
		</dic>
	</item>
	
	<item id="xbsbj" alias="现病史标记" type="string" group="疾病残疾">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="xbqk" alias="现病情况" type="string" colspan="2" group="疾病残疾">
		<dic render="LovCombo">
			<item key="1" text="佝偻病"/>
			<item key="2" text="贫血"/>
			<item key="3" text="营养不良"/>
			<item key="4" text="肥胖"/>
			<item key="5" text="生长迟缓"/>
		</dic>
	</item>
	
	<item id="cjbj" alias="残疾标记" type="string" group="疾病残疾">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="cjqk" alias="残疾情况" type="string" colspan="2" group="疾病残疾">
		<dic render="LovCombo">
			<item key="1" text="视力"/>
			<item key="2" text="听力"/>
			<item key="3" text="肢体"/>
			<item key="4" text="智力"/>
			<item key="5" text="语言残"/>
			<item key="6" text="将神残"/>
		</dic>
	</item>
	
	<item id="ysyn" alias="1 岁以内" type="string" group="体格检查">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="tjsj1" alias="体检时间" type="string" colspan="2" group="体格检查">
		<dic render="LovCombo">
			<item key="1" text="28天(满月)"/>
			<item key="2" text="2个月"/>
			<item key="3" text="4个月"/>
			<item key="4" text="6个月"/>
			<item key="5" text="9个月"/>
			<item key="6" text="12个月"/>
		</dic>
	</item>
	
	<item id="ydss" alias="1 - 3岁" type="string" group="体格检查">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="tjsj2" alias="体检时间" type="string" colspan="2" group="体格检查">
		<dic render="LovCombo">
			<item key="1" text="1岁半"/>
			<item key="2" text="2岁"/>
			<item key="3" text="2岁半"/>
			<item key="4" text="3岁"/>
		</dic>
	</item>
	
	<item id="sdls" alias="4 - 6岁" type="string" group="体格检查">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="tjsj3" alias="体检时间" type="string" colspan="2" group="体格检查">
		<dic render="LovCombo">
			<item key="1" text="4岁"/>
			<item key="2" text="5岁"/>
			<item key="3" text="6岁"/>
		</dic>
	</item>
</entry>
