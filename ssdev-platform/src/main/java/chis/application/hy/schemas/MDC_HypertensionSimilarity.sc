<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionSimilarity" alias="高血压疑似登记">
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item id="similarityId" alias="标识列" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="个人健康档案号" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true"
		fixed="true" />
	
	<item id="registerDate" alias="登记日期" type="date" fixed="true" not-null="1" maxValue="%server.date.today"
		defaultValue="%server.date.today" >
	</item>
	
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"  width="150"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"   width="150"/>
	<item id="hypertensionLevel" alias="血压级别" type="int" enableKeyEvents="true" display="1" >
		<dic>
			<item key="1" text="1级血压（轻度）"/>
			<item key="2" text="2级血压（中度）"/>
			<item key="3" text="3级血压（重度）"/>
			<item key="4" text="理想血压"/>
			<item key="5" text="正常血压"/>
			<item key="6" text="正常高值"/>
			<item key="7" text="单纯收缩性高血压"/>
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6" 
		minValue="100" maxValue="300" not-null="1" enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" type="double" length="6" 
		minValue="30" maxValue="500" not-null="1" enableKeyEvents="true" />
	<item id="bmi" alias="BMI" length="6" type="double" fixed="true" />
	
	<item id="diagnosisType" alias="核实结果" type="string" length="1"
		display="1">
		<dic id = "chis.dictionary.diagnosisType" />
	</item>
	<item id="manaUnitId" alias="管辖单位" type="string" length="20"
		hidden="true">
		<dic id="chis.@manageUnit" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputUnit" alias="登记单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
			<set type="exp">['$','%user.manageUnit.id']</set>
	</item>

	<item id="inputDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>

	<item id="diagnosisUnit" alias="确诊单位" type="string" length="16"
		hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>

	<item id="diagnosisDate" alias="确诊日期" type="date" fixed="true"
		hidden="true">
	</item>
	<item id="diagnosisDoctor" alias="确诊医生" type="string" length="20"
		fixed="true" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>

	<relations>
		<relation type="children" entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>
