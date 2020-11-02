<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_LNFN" alias="老年妇女" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="syst" alias="生育史(胎)" type="string" />
	<item id="sysc" alias="生育史(产)" type="string" />
	<item id="ccs" alias="初潮史(岁)" type="string" />
	<item id="jjnl" alias="绝经年龄(岁)" type="string" />
	
	<item id="fkjc" alias="近两年是否做过妇科检查" type="string" colspan="2">
		<dic render="Radio" colWidth="50">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="fxjb" alias="检查中是否发现妇科疾病" type="string" colspan="2">
		<dic render="Radio" colWidth="50">
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="jbmc1" alias="疾病名称1" type="string" colspan="2">
		<dic>
			<item key="1" text="乳房良性肿瘤"/>
			<item key="2" text="子宫内壁平滑肌瘤"/>
		</dic>
	</item>
	<item id="bm1" alias="编码" type="string" fixed="true" colspan="2"/>
	<item id="jbmc2" alias="疾病名称2" type="string" colspan="2">
		<dic>
			<item key="1" text="乳房良性肿瘤"/>
			<item key="2" text="子宫内壁平滑肌瘤"/>
		</dic>
	</item>
	<item id="bm2" alias="编码" type="string" fixed="true" colspan="2"/>
	<item id="jbmc3" alias="疾病名称3" type="string" colspan="2">
		<dic>
			<item key="1" text="乳房良性肿瘤"/>
			<item key="2" text="子宫内壁平滑肌瘤"/>
		</dic>
	</item>
	<item id="bm3" alias="编码" type="string" fixed="true" colspan="2"/>
	<item id="jbmc4" alias="疾病名称4" type="string" colspan="2">
		<dic>
			<item key="1" text="乳房良性肿瘤"/>
			<item key="2" text="子宫内壁平滑肌瘤"/>
		</dic>
	</item>
	<item id="bm4" alias="编码" type="string" fixed="true" colspan="2"/>
	<item id="jbmc5" alias="疾病名称5" type="string" colspan="2">
		<dic>
			<item key="1" text="乳房良性肿瘤"/>
			<item key="2" text="子宫内壁平滑肌瘤"/>
		</dic>
	</item>
	<item id="bm5" alias="编码" type="string" fixed="true" colspan="2"/>
	
	<item id="hyyxzz" alias="目前是否患有以下症状" type="string" colspan="4">
		<dic render="LovCombo">
			<item key="1" text="黄水样白带,严重时呈脓血色,外阴瘙痒,烧灼感"/>
			<item key="2" text="腹压突然增高时(咳嗽、大哭、搬运重物等)尿液不自主流出"/>
			<item key="3" text="容易骨折(如轻微碰撞即引起骨折)、腰背痛(夜间为重、晨起或体力劳动后加重)"/>
			<item key="4" text="都没有"/>
		</dic>
	</item>
	
</entry>
