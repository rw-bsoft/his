<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionAssessParamete" alias="高血压评估参数设置">
	<item id="recordId" alias="recordId" type="string" length="16" not-null="1" display="0"
		pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="assessType" alias="评估类型" length="1" type="string" colspan="3" defaultValue="1">
		<dic render="Radio" colWidth="350" columns="2">
			<item key="1" text="随访评估（单个病人在随访完成后，系统自动执行）"/>
			<item key="2" text="年度评估（人工启动，系统根据下面条件选出病人一起评估）"/>
		</dic>
	</item>
	<item id="recordWriteOff" alias="排除高血压档案已注销或个人档案已注销病人" length="5" type="string" xtype="checkbox"/>
	<item id="newPatient" alias="新病人不评价(维持原组不变)" length="5" type="string" xtype="checkbox"/>
	<item id="notNormPatient" alias="未规范管理的病人不进行年度评估" length="5" type="string" xtype="checkbox"/>
	<item id="oneGroup" alias="一组随访次数最低标准" length="5" type="string" xtype="checkbox" group="规范管理"/>
	<item id="oneGroupProportion" colspan="2" anchor="40%" alias="一组占计划随访数比例(%)"
		maxValue="100" minValue="1" type="int" group="规范管理"/>
	<item id="twoGroup" alias="二组随访次数最低标准" length="5" type="string" xtype="checkbox" group="规范管理"/>
	<item id="twoGroupProportion" colspan="2" anchor="40%" alias="二组占计划随访数比例(%)"
		maxValue="100" minValue="1" type="int" group="规范管理"/>
	<item id="threeGroup" alias="三组随访次数最低标准" length="5" type="string" xtype="checkbox" group="规范管理"/>
	<item id="threeGroupProportion" colspan="2" anchor="40%" alias="三组占计划随访数比例(%)"
		maxValue="100" minValue="1" type="int" group="规范管理"/>
	<item id="assessDays" colspan="2" anchor="100%" alias="距年末的最大可评估天数"
		maxValue="5" minValue="1" type="int" group="年度评估时间"/>
	<item id="days"  alias="天" xtype="label" virtual="true" group="年度评估时间"/>
	<item id="assessHour1" alias="可评估时间段:开始时间"  anchor="100%"
		maxValue="24" minValue="0" type="int" group="年度评估时间"/>
	<item id="assessHour2" alias="时  --------------  结束时间" anchor="100%"
		maxValue="24" minValue="0" type="int" group="年度评估时间"/>
	<item id="hours" alias="时" xtype="label" virtual="true" group="年度评估时间"/>
</entry>
