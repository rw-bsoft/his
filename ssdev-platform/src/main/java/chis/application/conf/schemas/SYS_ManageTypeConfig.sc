<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.SYS_ManageTypeConfig" alias="管理方式" version="1331800524540" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS\WebRoot\WEB-INF\config\schema\sys/SYS_CommonConfig.xml">

	<item id="areaGridType" alias="网格化管理方式" type="string" not-null="1">
		<dic>
			<item key="part" text="部分网格化管理"/>
			<item key="full" text="全网格化管理"/>
		</dic>
	</item>
	<item id="diabetesType" alias="糖尿病随访录入格式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="hypertensionType" alias="高血压随访录入格式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="healthCheckType" alias="健康体检录入格式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<!--添加-->
	<item id="areaGridShowType" alias="网格地址显示方式" type="int" not-null="1">
		<dic>
			<item key="tree" text="树状形式"/>
			<item key="form" text="列表形式"/>
			<item key="pycode" text="拼音形式"/>
		</dic>
	</item>
	<!--
	<item id="checkFollowUpShowType" alias="首次产检随访录入格式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
-->
	<item id="areaGridType" alias="网格化管理方式" type="string" not-null="1">
		<dic>
			<item key="part" text="部分网格化管理"/>
			<item key="full" text="全网格化管理"/>
		</dic>
	</item>
	<item id="areaGridShowType" alias="网格地址显示方式" type="string" not-null="1">
		<dic>
			<item key="tree" text="树状形式"/>
			<item key="form" text="列表形式"/>
			<item key="pycode" text="拼音形式"/>
		</dic>
	</item>

	<item id="healthCheckType" alias="健康体检录入格式" type="string" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<!-- ===========慢病========s======= -->
	<item id="hypertensionType" alias="高血压随访录入格式" type="string" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="diabetesType" alias="糖尿病随访录入格式" type="string" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="psychosisType" alias="精神病录入格式" type="string" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<!-- ===========孕妇产检随访========e======= -->
	<item id="pregnantFirstType" alias="首次产检随访录入" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="PregnantVisitFormType" alias="2～5次产检随访录入" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="childrenCheckupType" alias="儿童体格检查录入格式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="postnatal42dayType" alias="产后42天录入格式" type="string" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
	<item id="postnatalVisitType" alias="产后访视录入格式" type="string" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>

	<item id="debilityShowType" alias="新生儿随访录入格式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>

	<item id="PhisShowEhrViewType" alias="基本医疗健康档案显示方式" type="int" not-null="1">
		<dic>
			<item key="paper" text="纸质格式"/>
			<item key="form" text="表单格式"/>
		</dic>
	</item>
</entry>