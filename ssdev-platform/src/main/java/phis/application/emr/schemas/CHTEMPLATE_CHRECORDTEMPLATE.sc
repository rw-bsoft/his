<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="phis.application.emr.schemas.CHTEMPLATE_CHRECORDTEMPLATE" alias="病历病史模板">
	<item id="CHTCODE" alias="模板编号" type="string" display="0" generator="assigned"/>
	<item id="CHTNAME" alias="模板名称" type="string" width="200" not-null="true" length="30"/>
	<item id="CHTNAME_T" alias="模板名称" type="string" display="0"/>
	<item id="FRAMEWORKCODE" alias="病历类别" type="string" width="150">
		<dic id="phis.dictionary.emr_kbm_bllb"  editable="false" fields="key,text,SJLBBH" filter="['eq',['$','item.properties.MLBZ'],['i',1]]" autoLoad = "true"/>
	</item>
	<item id="TEMPLATETYPE" alias="模板类别" type="int"  width="150">
		<dic id="phis.dictionary.emr_kbm_bllb"  editable="false" fields="key,text,SJLBBH" autoLoad = "true"/>
	</item>
	<item id="MBLX" alias="模板类型" type="int" defaultValue="1" queryable="true" display="0">
		<dic >
			<item key="1" text="病历模板"/>
			<item key="2" text="段落模板"/>
			<item key="3" text="页眉页脚"/>
		</dic>
	</item>
	<item id="INOROUTTYPE" alias="使用范围" type="int" not-null="true">
		<dic autoLoad = "true">
			<item key="0" text="住院使用" />
			<item key="1" text="门诊使用" />
			<item key="2" text="家床使用" />
		</dic>
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="50" not-null="1" target="CHTNAME" selected="true" queryable="true" fixed="true"/>
	<item id="TABLENAME" alias="表名" type="string" display="0"/>
	<item id="ISHDRFTRTEMP" alias="页眉页脚" type="string" display="0"/>
	<item id="ISDY" alias="是否订阅" type="int" display="0"/>
	<item id="ISTY" alias="停用" type="int" not-null="true" width="60">
		<dic autoLoad = "true">
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
</entry>
