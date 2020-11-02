<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hy.schemas.MDC_HypertensionRecord" sort="a.createDate desc">
	<item id="phrId" alias="档案编号" type="string" length="30" width="160"
		pkey="true" queryable="true" display="3" />
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="b.phoneNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	<item ref="d.businessType" display="1" queryable="true" />
	<item ref="d.planDate" display="1" queryable="true" />
	<item ref="d.beginDate" display="1" queryable="true" />
	<item ref="d.endDate" display="1" queryable="true" />
	<item ref="d.planStatus" display="1" queryable="true" />
	<item ref="d.planId" display="1" queryable="true" />

	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
	<item id="manaDoctorId" alias="责任医生" not-null="1" update="false"
		type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			keyNotUniquely="true"
			parentKey="['substring',['$','%user.manageUnit.id'],['i',0],['i',9]]" />
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		fixed="true" width="180" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>

	<item id="familyHistroy" alias="家族史" type="string" fixed="true"
		length="64" display="2" />
	<item id="smoke" alias="吸烟频率" type="string" display="2" length="1">
		<dic id="chis.dictionary.CV5101_24" />
	</item>
	<item id="smokeCount" alias="日吸烟量(支)" type="int" display="2"
		length="3" />

	<item id="drink" alias="饮酒频率" type="string" display="2" length="2">
		<dic id="chis.dictionary.CV5101_26" render="Tree" />
	</item>
	<item id="drinkTypeCode" alias="饮酒种类" type="string" display="2"
		length="64">
		<dic id="chis.dictionary.drinkTypeCode_life" render="LovCombo" />
	</item>
	<item id="drinkCount" alias="日饮酒量(两)" type="int" display="2"
		length="4" />

	<item id="train" alias="锻炼频率" type="string" display="2" length="2">
		<dic id="chis.dictionary.CV5101_28" render="Tree" />
	</item>
	<item id="eateHabit" alias="饮食习惯" type="string" length="64">
		<dic id="chis.dictionary.eateHabit" render="LovCombo" />
	</item>
	<item id="recordSource" alias="检出途径" type="string" not-null="1"
		length="1" display="2">
		<dic id="chis.dictionary.hypertensionRecordSource" />
	</item>

	<item id="confirmDate" alias="临床确诊时间" type="date" display="2"
		defaultValue="%server.date.date" not-null="1" enableKeyEvents="true"
		validationEvent="false" maxValue="%server.date.date" />
	<item id="deaseAge" alias="病程" type="string" virtual="true" fixed="true"
		display="2" />
	<item id="clinicAddress" alias="经常就诊地点" type="string" not-null="1"
		length="1" display="2">
		<dic>
			<item key="1" text="本院" />
			<item key="2" text="其他一级医院" />
			<item key="3" text="本区二、三级医院" />
			<item key="4" text="其他" />
		</dic>
	</item>

	<item id="viability" alias="生活自理能力" type="string" not-null="1"
		length="1" display="2">
		<dic>
			<item key="1" text="完全自理" />
			<item key="2" text="部分自理" />
			<item key="3" text="完全不能自理" />
		</dic>
	</item>
	<item id="height" alias="身高(cm)" not-null="1" type="double"
		display="2" minValue="0" maxValue="300" enableKeyEvents="true"
		validationEvent="false" />
	<item id="weight" alias="体重(kg)" not-null="1" type="double"
		display="2" minValue="0" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="bmi" alias="BMI" type="double" display="2" fixed="true"
		virtual="true" />

	<item id="constriction" alias="收缩压(mmHg)" type="int" not-null="1"
		display="2" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="diastolic" alias="舒张压(mmHg)" type="int" not-null="1"
		display="2" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<!-- 以下隐藏 -->
	<item id="riskiness" alias="危险因素" type="string" not-null="1"
		length="64" defaultValue="0" display="0">
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo" />
	</item>
	<item id="targetHurt" alias="靶器官伤害" type="string" not-null="1"
		defaultValue="0" length="64" display="0">
		<dic id="chis.dictionary.targetHurt" render="LovCombo" />
	</item>
	<item id="complication" alias="并发症" type="string" not-null="1"
		defaultValue="0" length="64" display="0">
		<dic id="chis.dictionary.complication" render="LovCombo" />
	</item>
	<!-- -->

	<!-- 2010-04-8 added by chinnsii. to mark whether person with normal blood 
		pressure has bean taken medicine. -->
	<item id="afterMedicine" alias="血压正常原因" type="string" length="20"
		fixed="true" display="2" />
	<item id="hypertensionGroup" alias="管理分组" type="string" length="2"
		display="2" fixed="true" queryable="true">
		<dic id="chis.dictionary.hyperGroupExt" />
	</item>
	<item id="riskLevel" alias="危险分层" type="string" length="1" fixed="true"
		display="2">
		<dic id="chis.dictionary.riskLevel" />
	</item>

	<item id="createUnit" alias="建档机构" type="string" update="false"
		length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180"
		queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" update="false"
		length="20" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime" xtype="datefield"
		update="false" not-null="1" fixed="true" defaultValue="%server.date.date"
		enableKeyEvents="true" validationEvent="false" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="cancellationDate" alias="档案注销日期" type="date" display="0"
		fixed="true" />
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1"
		fixed="true" display="0">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"
		length="100" hidden="true" colspan="3" anchor="100%" />
	<item id="cancellationCheckUnit" alias="注销复核机构" type="string"
		length="16" fixed="true" display="0" />
	<item id="cancellationCheckUser" alias="注销复核者" type="string"
		length="20" fixed="true" display="0" />
	<item id="cancellationCheckDate" alias="注销复核时间" type="date" fixed="true"
		display="0" />
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" fixed="true" display="0">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="注销核实中" />
		</dic>
	</item>
	<item id="yearEstimate" alias="年度评估" type="date" fixed="true"
		display="0" />
	<item id="planTypeCode" alias="随访计划类型" type="string" fixed="true"
		display="0" />

	<item id="needDoVisit" alias="标识该档案是否有随访需要做" type="boolean"
		display="0" virtual="true" />
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield"
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<!-- add by yyd -->

	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>

	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0" />

	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<relation type="parent" entryName="chis.application.pub.schemas.PUB_VisitPlan">
			<join parent="recordId" child="phrId" />
		</relation>
	</relations>
</entry>