<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_FNBJ" alias="妇女保健" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="pcnx" alias="普查年限" type="string">
		<dic >
			<item key="1" text="2年一次"/>
			<item key="2" text="3年一次"/>
			<item key="3" text="不定期"/>
			<item key="4" text="无"/>
		</dic>
	</item>
	<item id="rfzc" alias="乳房自查" type="string" >
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="fcjc" alias="乳房检查" type="string">
		<dic >
			<item key="1" text="定期"/>
			<item key="2" text="不定期"/>
			<item key="3" text="无"/>
		</dic>
	</item>
	
	<item id="rxzs" alias="乳腺增生" type="string" group="乳房疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="zslb" alias="增生类别" type="string" colspan="2" group="乳房疾病">
		<dic >
			<item key="1" text="乳痛症"/>
			<item key="2" text="小叶增生"/>
			<item key="3" text="纤维囊性增生"/>
			<item key="4" text="乳头状瘤"/>
		</dic>
	</item>
	<item id="lxzl" alias="良性肿瘤" type="string" group="乳房疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="lllb" alias="良瘤类别" type="string" colspan="2" group="乳房疾病">
		<dic >
			<item key="1" text="乳腺纤维腺瘤"/>
			<item key="2" text="乳管内乳头状瘤"/>
		</dic>
	</item>
	<item id="exzl" alias="恶性肿瘤" type="string" group="乳房疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="ellb" alias="恶瘤类别" type="string" colspan="2" group="乳房疾病">
		<dic >
			<item key="1" text="乳头状癌"/>
			<item key="2" text="乳腺癌"/>
			<item key="3" text="导管腺癌"/>
		</dic>
	</item>
	<item id="yjwl" alias="月经紊乱" type="string" group="妇科疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="zgcx" alias="子宫出血" type="string" group="妇科疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="zgjl" alias="子宫肌瘤" type="string" group="妇科疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="rcnz" alias="卵巢囊肿" type="string" group="妇科疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="zgyw" alias="子宫异位" type="string" group="妇科疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="fkqt" alias="妇科其他" type="string" group="妇科疾病"/>
	<item id="az" alias="癌症" type="string" group="妇科疾病">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="azlb" alias="癌症类别" type="string" colspan="2" group="妇科疾病">
		<dic render="LovCombo">
			<item key="1" text="子宫内膜癌"/>
			<item key="2" text="子宫颈癌"/>
			<item key="3" text="宫体癌"/>
			<item key="4" text="卵巢恶性肿瘤"/>
		</dic>
	</item>
	<item id="azqt" alias="癌症其他" type="string" colspan="2" group="妇科疾病"/>
	
	<item id="zdfs" alias="诊断方式" type="string" group="其他">
		<dic render="LovCombo">
			<item key="1" text="妇女病检查"/>
			<item key="2" text="自己到医院检查"/>
		</dic>
	</item>
	<item id="jbzl" alias="疾病治疗" type="string" group="其他">
		<dic render="LovCombo">
			<item key="1" text="手术"/>
			<item key="2" text="药物"/>
			<item key="3" text="随访"/>
			<item key="4" text="手术+化疗"/>
		</dic>
	</item>
</entry>
