<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_Suggestion" alias="建议表" sort="type,category,riskFactor">
	<item id="recordId" alias="登记序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="type" alias="类型" type="string" length="1" not-null="1"
		width="250" queryable="true">
		<dic>
			<item key="1" text="具有CVD患病危险人群的生活方式干预建议" />
			<item key="2" text="具有CVD患病危险人群的药物治疗干预建议" />
			<item key="3" text="针对患心血管疾病人群的生活方式干预建议" />
			<item key="4" text="针对患心血管疾病人群的药物治疗干预建议" />
		</dic>
	</item>
	<item id="category" alias="建议种类" type="string" length="16"
		not-null="1" width="250" queryable="true">
		<dic id="chis.dictionary.category"></dic>
	</item>
	<item id="riskFactor" alias="危险因素" type="string" length="1"
		not-null="1" width="250" queryable="true">
		<dic>
			<item key="0" text="无" />
			<item key="1" text="10年内发生心血管事件的危险度＜10%" />
			<item key="2" text="10年内发生心血管事件的危险度10 - ＜20%" />
			<item key="3" text="10年内发生心血管事件的危险度20 - ＜30%  " />
			<item key="4" text="10年内发生心血管事件的危险度≥30%" />
		</dic>
	</item>

	<item id="content" alias="内容" type="string" length="32" not-null="1"
		xtype="htmleditor" width="900" />
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.prop.manaUnitId" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="timestamp"
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
