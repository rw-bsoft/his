<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_FNYCJYQ" alias="孕产节育期" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="tc" alias="胎次" type="string" group="孕产期"/>
	<item id="cc" alias="产次" type="string" group="孕产期"/>
	<item id="cqjc" alias="产前检查" type="string" defaultValue="1" group="孕产期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="cjyfxx" alias="参加孕妇学校" type="string" defaultValue="1" group="孕产期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="yqgwys" alias="孕期高危因素" type="string" defaultValue="1" group="孕产期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="ys" alias="因素" type="string" group="孕产期"/>
	<item id="fmdd" alias="分娩地点" type="string" group="孕产期">
		<dic>
			<item key="1" text="医院"/>
			<item key="2" text="家庭"/>
			<item key="3" text="其他"/>
		</dic>
	</item>
	<item id="fmfs" alias="分娩方式" type="string" group="孕产期">
		<dic id="chis.dictionary.deliveryType" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="rcjj" alias="妊娠结局" type="string" group="孕产期">
		<dic>
			<item key="1" text="足月存活"/>
			<item key="2" text="早产"/>
			<item key="3" text="流产"/>
			<item key="4" text="死胎"/>
			<item key="5" text="死产"/>
			<item key="6" text="新生儿死亡"/>
		</dic>
	</item>
	<item id="csqx" alias="出生缺陷" type="string" defaultValue="1" group="孕产期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="brfs" alias="哺乳方式" type="string" group="孕产期">
		<dic id="chis.dictionary.feedWay" onlySelectLeaf="true"/>
	</item>
	<item id="cmrq" alias="纯母乳期" type="string" group="孕产期">
		<dic>
			<item key="1" text="四个月以内"/>
			<item key="2" text="四个月以上"/>
			<item key="3" text="六个月以上"/>
		</dic>
	</item>
	
	<item id="byfs" alias="避孕方式" type="string" group="节育期">
		<dic >
			<item key="1" text="绝育"/>
			<item key="2" text="放环"/>
			<item key="3" text="药物"/>
			<item key="4" text="皮埋"/>
			<item key="5" text="男方落实"/>
			<item key="6" text="其他"/>
			<item key="7" text="无"/>
		</dic>
	</item>
	<item id="fhsf" alias="放环随访" type="string" group="节育期">
		<dic >
			<item key="1" text="定期"/>
			<item key="2" text="不定期"/>
			<item key="3" text="无"/>
		</dic>
	</item>
	<item id="fhnx" alias="放环年限" type="string" group="节育期">
		<dic >
			<item key="1" text="&lt;=5年"/>
			<item key="2" text="6年以上"/>
			<item key="3" text="10年以上"/>
			<item key="3" text="15年以上"/>
		</dic>
	</item>
	<item id="sfdw" alias="随访单位" type="string" colspan="2" group="节育期">
		<dic >
			<item key="1" text="闵行精神卫生中心"/>
			<item key="2" text="古美社区卫生服务中心"/>
		</dic>
	</item>
	
</entry>
