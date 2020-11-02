<?xml version="1.0" encoding="UTF-8"?>
<entry alias="高血压定转组信息" sort="fixId" version="1331800522000" filename="E:\MyProject\BZWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\mdc/MDC_HypertensionFixGroup.xml">
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.phoneNumber" display="1" queryable="true" />
	<item id="fixId" alias="记录序号" type="string" length="16" pkey="true" fixed="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1" display="0"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" not-null="1" fixed="true" display="0"/>
	<item id="fixDate" alias="定转组日期" width="80" type="date" defaultValue="%server.date.date">
		<!--<set type="exp">['$','%server.date.date']</set>-->
	</item>
	<item id="fixType" alias="定转组类型" type="string" length="1" width="80" fixed="true">
		<dic>
			<item key="1" text="初次定组"/>
			<item key="2" text="维持原组"/>
			<item key="3" text="定期转组"/>
			<item key="4" text="不定期转组"/>
			<item key="5" text="随访评估定组" />
			<item key="6" text="年度评估定组" />
		</dic>
	</item>
	<item id="oldGroup" alias="原定分组" type="string" length="2" display="0" fixed="true">
		<dic id="chis.dictionary.hypertensionGroup"/>
	</item>
	<item id="fixUnit" alias="转组机构" type="string" length="20" display="0" fixed="true" update="false" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="hypertensionGroup" alias="管理分组" type="string" length="2" width="80" fixed="true">
		<dic id="chis.dictionary.hyperGroupExt"/>
	</item>
	<item id="riskLevel" alias="危险分层" type="string" length="1" display="2" fixed="true">
		<dic>
			<item key="1" text="低危"/>
			<item key="2" text="中危"/>
			<item key="3" text="高危"/>
			<item key="4" text="很高危"/>
		</dic>
	</item>
	<item id="hypertensionLevel" alias="血压级别" type="string" fixed="true" display="2" colspan="2">
		<dic>
			<item key="1" text="1级高血压（轻度）"/>
			<item key="2" text="2级高血压（中度）"/>
			<item key="3" text="3级高血压（重度）"/>
			<item key="4" text="理想血压"/>
			<item key="5" text="正常血压"/>
			<item key="6" text="正常高值"/>
			<item key="7" text="单纯收缩性高血压"/>
		</dic>
	</item>
	<item id="controlResult" alias="控制情况" type="string" length="1" display="0" fixed="true">
		<dic>
			<item key="1" text="优良"/>
			<item key="2" text="尚可"/>
			<item key="3" text="不良"/>
			<item key="4" text="未评价"/>
			<item key="5" text="新病人"/>
		</dic>
	</item>
	
	<item id="constriction" alias="收缩压(mmHg)" minValue="50" maxValue="500" type="int" not-null="1" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="diastolic" alias="舒张压(mmHg)" minValue="50" maxValue="500" type="int" not-null="1" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="IGT" alias="糖耐量受损" type="double" length="8" precision="2" maxValue="11" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="fbs" alias="空腹血糖值(mmol/L)" type="double" length="6" precision="2" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="TC" alias="TC(mmol/L)" type="double" length="8" display="2" enableKeyEvents="true" not-null="false" group="危险因素"/>
	<item id="LDL" alias="LDL-C(mmol/L)" type="double" length="8" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="HDL" alias="HDL-C(mmol/L)" type="double" length="8" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="familyHistoryOfCardiovascular" alias="心血管家族史" type="string" length="1" not-null="1" display="2" group="危险因素">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="waistLine" alias="腰围(cm)" type="double"  not-null="1" minValue="40" maxValue="200" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="height" alias="身高(cm)" type="double" not-null="1"   minValue="100" maxValue="300" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="weight" alias="体重(kg)" type="double"  not-null="1" minValue="30" maxValue="500" display="2" enableKeyEvents="true" group="危险因素"/>
	<item id="bmi" alias="BMI" type="double" length="6" fixed="true" display="2" group="危险因素" />
	<item id="riskiness" alias="危险因素" type="string" length="64" display="2" defaultValue="0"  colspan="3" group="危险因素">
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo"/>
	</item>
	
	<item id="SokolowLyons" alias="SokolowLyons(mv)" type="int" group="靶器官损害" display="2" length="8"/>
	<item id="Cornell" alias="Cornell（mm·mms）" type="int" group="靶器官损害" display="2" length="8"/>
	<item id="LVMI" alias="LVMI（g/m 2）" type="int" group="靶器官损害" display="2" length="8"/>
	<item id="carotidUltrasound" alias="颈动脉超声IMT(mm)" type="double" length="8" precision="2" display="2" enableKeyEvents="true" group="靶器官损害"/>
	<item id="arteryGruelTypeMottling" alias="动脉粥样斑块" type="string" length="1" display="2" group="靶器官损害" >
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="sphygmus" alias="颈-股动脉搏波速度(m/s)" type="int" display="2" group="靶器官损害" length="8" />
	<item id="ankleOrArmBPI" alias="踝/臂血压指数" type="double" length="8" precision="2" display="2" enableKeyEvents="true" group="靶器官损害"/>
	<item id="GFRDecreased" alias="肾小球滤过率降低" type="string" length="1" display="2" group="靶器官损害">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="serumCreatinine" alias="血肌酐(μmol/L)" type="double" length="7" display="2" enableKeyEvents="true" group="靶器官损害"/>
	<item id="microalbuminuria" alias="微量尿白蛋白(mg/24h)" type="double" length="7" display="2" enableKeyEvents="true" group="靶器官损害"/>
	<item id="buminuriaSerum" alias="白蛋白/肌酐比(mg/g)" type="double" length="7" display="2" group="靶器官损害" colspan="2" />
	<item id="targetHurt" alias="靶器官损害" type="string" length="64" display="2" defaultValue="0" colspan="3" group="靶器官损害">
		<dic id="chis.dictionary.targetHurt" render="LovCombo"/>
	</item>
	
	<item id="proteinuria" alias="蛋白尿(mg/24h)" type="double" length="8" display="2" enableKeyEvents="true" group="伴临床疾患"/>
	<item id="pbs" alias="餐后血糖（mmol/L）" type="double" length="8" precision="2" display="2" enableKeyEvents="true" group="伴临床疾患"/>
	<item id="ghp" alias="糖化血红蛋白%" type="double" length="8" precision="2" display="2" enableKeyEvents="true" group="伴临床疾患"/>
	<item id="complication" alias="伴临床疾患" type="string" display="2" colspan="3" group="伴临床疾患">
		<dic id="chis.dictionary.complication" render="LovCombo"/>
	</item>
		
	<item id="fixUser" alias="医生签名" type="string" length="20" display="0" fixed="true" update="false" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" display="0" fixed="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item ref="c.regionCode" display="1" queryable="true" />
	<item ref="d.status" display="0" />
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<relation type="children" entryName="chis.application.hy.schemas.MDC_HypertensionRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
