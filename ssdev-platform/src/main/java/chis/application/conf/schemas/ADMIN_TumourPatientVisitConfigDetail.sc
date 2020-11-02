<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_TumourPatientVisitConfigDetail" alias="肿瘤患者随访参数设置">
	<item id="instanceType"  alias="方案类型" type="string" hidden="true" not-null="true" defaultValue="16" display="0"/>
	<item id="group" alias="随访分组 " type="string" not-null="true" width="400">
		<dic>
			<item key="100" text="(100)一切正常，无不适或病症。"/>
			<item key="90" text="(90)能进行正常活动，有轻微病症。"/>
			<item key="80" text="(80)勉强可进行正常活动，有一些症状和体症。"/>
			<item key="70" text="(70)生活可自理，但不能维持正常活动或工作。"/>
			<item key="60" text="(60)生活偶需帮助，但能照顾大部分私人的需求。"/>
			<item key="50" text="(50)需要颇多的帮助及经常的医疗护理。"/>
			<item key="40" text="(40)失去活动能力，需要特别照顾和帮助。"/>
			<item key="30" text="(30)严重失去活动力，要住医院，但暂未有死亡威胁。"/>
			<item key="20" text="(20)病重，需住院及积极支持治疗。"/>
			<item key="10" text="(10)垂危。"/>
		</dic>
	</item>
	<item id="planTypeCode" alias="计划类型" type="string" length="2" width="200" not-null="true">
		<dic id="chis.dictionary.planTypeDic"/>
	</item>
	<item id="expression" alias="条件表达式"  type="string"  length="200" hidden="true"/>
</entry> 