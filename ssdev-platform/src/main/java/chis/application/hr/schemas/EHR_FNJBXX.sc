<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_FNJBXX" alias="妇女基本信息" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="yjlc" alias="月经来潮" type="string" defaultValue="1" group="青春期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="ccnl" alias="初潮年龄(岁)" type="string" group="青春期"/>
	<item id="jqts" alias="经期天数(天)" type="string" group="青春期"/>
	<item id="zqts" alias="周期天数(天)" type="string" group="青春期"/>
	<item id="tj" alias="痛经" type="string" defaultValue="1" group="青春期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="jhnl" alias="结婚年龄(岁)" type="string" group="围婚期"/>
	<item id="jhrq" alias="结婚日期" type="datetime" xtype="datefield" group="围婚期"/>
	<item id="hqjc" alias="婚前检查" type="string" defaultValue="1" group="围婚期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	<item id="jshyzs" alias="接受婚育知识" type="string" defaultValue="1" group="围婚期">
		<dic render="Radio" colWidth="50">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
		</dic>
	</item>
	
	<item id="bdcz" alias="被调查者" type="string" defaultValue=""/>
	<item id="dcz" alias="调查者" type="string" defaultValue="">
	</item>
	<item id="jlz" alias="记录者" type="string" defaultValue="" fixed="true">
	</item>
	<item id="qmrq" alias="签名日期" type="datetime" xtype="datefield" />
	<item id="dcrq" alias="调查日期" type="datetime" xtype="datefield" />
	<item id="jlrq" alias="记录日期" type="datetime" xtype="datefield" fixed="true"/>
</entry>
