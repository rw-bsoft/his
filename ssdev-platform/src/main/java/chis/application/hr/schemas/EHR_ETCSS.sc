<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_ETCSS" alias="出生史" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="csyz" alias="出生孕周(周)" type="string" group="出生史"/>
	<item id="cstz" alias="出生体重(kg)" type="string" group="出生史"/>
	<item id="sc" alias="身长(cm)" type="string" group="出生史"/>
	<item id="tw" alias="头围(cm)" type="string" group="出生史"/>
	
	<item id="bdcz" alias="被调查者" type="string" defaultValue=""/>
	<item id="dcz" alias="调查者" type="string" defaultValue="">
	</item>
	<item id="jlz" alias="记录者" type="string" defaultValue="" fixed="true">
	</item>
	<item id="qmrq" alias="签名日期" type="datetime" xtype="datefield" />
	<item id="dcrq" alias="调查日期" type="datetime" xtype="datefield" />
	<item id="jlrq" alias="记录日期" type="datetime" xtype="datefield" fixed="true"/>
</entry>
