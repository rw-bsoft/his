<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_ETFYS" alias="发育史" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">

	<item id="mqtz" alias="目前体重(kg)" type="string"/>
	<item id="mqsg" alias="目前身高(cm)" type="string"/>
	<item id="ycs" alias="牙齿数(只)" type="string"/>
	<item id="qcs" alias="龋齿数(只)" type="string"/>
	<item id="sl" alias="视力" type="string" colspan="2">
		<dic render="LovCombo">
			<item key="1" text="未查"/>
			<item key="2" text="正常"/>
			<item key="3" text="弱视"/>
			<item key="4" text="散光"/>
			<item key="5" text="近视"/>
			<item key="6" text="远视"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	
	<item id="tt" alias="抬头(月)" type="string" group="0-1岁"/>
	<item id="fs" alias="翻身(月)" type="string" group="0-1岁"/>
	<item id="hz" alias="会坐(月)" type="string" group="0-1岁"/>
	<item id="hp" alias="会爬(月)" type="string" group="0-1岁"/>
	<item id="dz" alias="独站(月)" type="string" group="0-1岁"/>
	<item id="fz" alias="扶走(月)" type="string" group="0-1岁"/>
	
	<item id="dzhan" alias="独走(月)" type="string" group="1-2岁"/>
	<item id="hstj" alias="会上台阶(月)" type="string" group="1-2岁"/>
	<item id="hpao" alias="会跑(月)" type="string" group="1-2岁"/>
	<item id="hzwg" alias="会指五官(月)" type="string" group="1-2岁"/>
	<item id="hjbm" alias="会叫爸妈(月)" type="string" group="1-2岁"/>
	
	<item id="zjcf" alias="自己吃饭(月)" type="string" group="2-3岁"/>
	<item id="nzjctyf" alias="能自己穿脱衣服(月)" type="string" group="2-3岁"/>
	<item id="hszj" alias="会说再见(月)" type="string" group="2-3岁"/>
	<item id="scstgbf" alias="说出身体各部分(岁)" type="string" group="2-3岁"/>
	<item id="njcwpmc" alias="能叫出物品名称(岁)" type="string" group="2-3岁"/>
	
	<item id="nsdgy" alias="能说短歌谣(岁)" type="string" group="3-6岁"/>
	<item id="nceg" alias="能唱儿歌(岁)" type="string" group="3-6岁"/>
	<item id="ngjjdgs" alias="能够讲简单故事(岁)" type="string" group="3-6岁"/>
	
</entry>
