<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionSimilarityC" alias="糖尿病高危登记子表">
	<item id="recordId" alias="标识列" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="similarityId" alias="主表登记号" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="phrId" alias="个人健康档案号" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true"
		fixed="true" />
		
	
	<item id="registerDate" alias="测量时间" type="date" fixed="true"
		defaultValue="%server.date.today" display="1">
	</item>
	
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="hypertensionLevel" alias="血压级别" type="int" enableKeyEvents="true" fixed="true">
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
	
	<item id="manaUnitId" alias="管辖单位" type="string" length="20"
		hidden="true">
		<dic id="chis.@manageUnit" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputUnit" alias="登记单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" display="0">
		<set type="exp">["$","%server.date.today"]</set>
	</item>

	<item id="inputUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
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
