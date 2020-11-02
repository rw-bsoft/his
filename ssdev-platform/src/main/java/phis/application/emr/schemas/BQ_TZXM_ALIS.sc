<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_TZXM" alias="体征录入">

	<item id="CJSJ" alias="测量日期" length="25" type="datetime" colspan="2"
		defaultValue="%server.date.datetime" />
	<item id="SJ" alias="快捷选择" length="25" type="string" colspan="2"
		virtual="true">
		<dic render="Radio" colWidth="70" columns="6">
			<item key="02:00" text="02:00" />
			<item key="06:00" text="06:00" />
			<item key="10:00" text="10:00" />
			<item key="14:00" text="14:00" />
			<item key="18:00" text="18:00" />
			<item key="22:00" text="22:00" />
		</dic>
	</item>
	<item id="XMXB" alias="体温类型" length="25" type="string" colspan="2"
		defaultValue="腋温" not-null="1">
		<dic autoLoad="true" render="Radio">
			<item key="口温" text="口温" />
			<item key="腋温" text="腋温" />
			<item key="肛温" text="肛温" />
		</dic>
	</item>
	<item id="TW" alias="体温(℃)" length="25" type="double" minValue="34"
		maxValue="42" />
	<!-- <item id="FC" alias="复测" length="25" xtype="checkbox"/> -->
	<item id="MB" alias="脉搏(次/分)" length="25" type="int" minValue="20"
		maxValue="180" />
	<item id="HX" alias="呼吸(次/分)" length="25" type="int" minValue="0"
		maxValue="999" />
	<item id="XL" alias="心率(次/分)" length="25" type="int" minValue="20"
		maxValue="180" />
	<item id="SSY" alias="收缩压(mmHg)" length="25" type="int" minValue="0"
		maxValue="999" />
	<item id="SZY" alias="舒张压(mmHg)" length="25" type="int" minValue="0"
		maxValue="999" />
	<item id="TZ" alias="体重(kg)" length="25" type="string" />
	<item id="SG" alias="身高(cm)" length="25" type="string" minValue="0"
		maxValue="999" />
	<item id="DB" alias="大便(次/日)" length="25" type="string" minValue="0"
		maxValue="99">
		<dic customInput="true">
			<item key="0/E" text="0" />
			<item key="1/E" text="1" />
			<item key="☆" text="☆" />
			<item key="※" text="※" />
		</dic>
	</item>
	<item id="XB" alias="尿量(ml)" length="25" type="string" minValue="0"
		maxValue="999999" />
	<item id="QTCL" alias="出量(ml)" length="25" type="string" minValue="0"
		maxValue="999999" />
	<item id="SW" alias="食物(ml)" length="25" display="0" type="double"
		minValue="0" maxValue="999999" />
	<item id="RL" alias="入量(ml)" length="25" type="string" minValue="0"
		maxValue="999999" />
	<item id="S" alias="水(ml)" length="25" display="0" type="double"
		minValue="0" maxValue="999999" />
	<item id="TTPF" alias="疼痛评分" display="0" length="25" type="string">
	</item>
	<item id="PS" alias="皮试" length="25" type="string">
		<dic>
			<item key="磺胺+" text="磺胺阳性" />
			<item key="普卡+" text="普卡阳性" />
			<item key="普卡-" text="普卡阴性" />
			<item key="青+" text="青阳性" />
			<item key="青-" text="青阴性" />
			<item key="头孢+" text="头孢阳性" />
			<item key="头孢-" text="头孢阴性" />
			<item key="左氧+" text="左氧阳性" />
			<item key="TAT+" text="TAT阳性" />
			<item key="TAT-" text="TAT阴性" />
			<item key="头孢美唑+" text="头孢美唑阳性" />
			<item key="头孢美唑-" text="头孢美唑阴性" />
			<item key="头孢米诺+" text="头孢米诺阳性" />
			<item key="头孢米诺-" text="头孢米诺阴性" />
			<item key="PDD mm*mm" text="PDD mm*mm" />
			<item key="碘酒试液+" text="碘酒试液阳性" />
			<item key="碘酒试液-" text="碘酒试液阴性" />
		</dic>
	</item>
	<item id="TTPFFS" alias="疼痛评分方式" display="0" length="25" type="string" />
	<item id="CJH" alias="号" length="25" display="0" type="double" />
	<item id="BZLX" alias="备注类型" length="25" display="0" type="string" />
	<item id="BZXX" alias="备注信息" length="25" display="0" type="string" />
	<item id="FC" alias="复测" length="25" display="0" type="string" />
</entry>