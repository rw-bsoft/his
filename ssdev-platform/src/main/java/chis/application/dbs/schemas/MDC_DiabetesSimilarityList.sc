<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesSimilarity" alias="糖尿病高危登记">
	<item ref="b.personName" display="1" queryable="true" />
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
	<item id="sourceId" alias="来源主键" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="riskiness" alias="高危因素" type="string" length="64"
		colspan="2" width="450" display="1">
		<dic render="Checkbox" columnWidth="300" columns="2" >
				<item key="01" text="年龄>=45岁者" />
				<item key="02" text="超重或者肥胖" />
				<item key="03" text="高危种族" />
				<item key="04" text="静坐生活方式" />
				<item key="05" text="糖耐量异常或合并空腹血糖受损" />
				<item key="06" text="有巨大儿生产史，妊娠糖尿病史" />
				<item key="07" text="2型糖尿病患者的一级亲属" />
				<item key="08" text="血脂异常，或正在接受调脂治疗" />
				<item key="09" text="高血压，或正在接受降压治疗" />
				<item key="10" text="心脑血管疾病患者" />
				<item key="11" text="有一过性糖皮质激素诱发糖尿病病史者" />
				<item key="12" text="BMI≥28kg/㎡的多囊卵巢综合症" />
				<item key="13" text="严重精神病和(或)长期接受抗抑郁症药物治疗的患者" />
		</dic>
	</item>
	
	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="6" precision="2" width="150"/>
	<item id="pbs" alias="餐后血糖(mmol/L)" type="double" length="6" precision="2" width="150"/>
	<item id="clinicalSymptom" alias="临床症状" type="string" length="1"
		display="1">
		<dic>
			<item key="1" text="有" />
			<item key="2" text="无" />
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6" 
		minValue="100" maxValue="300" not-null="1" enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" type="double" length="6" 
		minValue="30" maxValue="500" not-null="1" enableKeyEvents="true" />
	<item id="bmi" alias="BMI" length="6" type="double" fixed="true" />
	<item id="dbsCreate" alias="糖尿病档案" type="string" length="1" display="1" virtual="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="diagnosisType" alias="核实结果" type="string" length="1"
		display="1">
		<dic id = "chis.dictionary.diagnosisType" />
	</item>
	<item id="manaUnitId" alias="管辖单位" type="string" length="20"
		hidden="true">
		<dic id="chis.@manageUnit" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="registerDate" alias="登记日期" type="date" defaultValue="%server.date.today" queryable="true">
	</item>
	<item id="registerUser" alias="登记人" type="string" length="20"  defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="registerUnit" alias="登记单位" type="string" length="20" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="fbs1" alias="空腹血糖1(mmol\L)" type="double" length="6" display="1" enableKeyEvents="true" group="第二次"/>
	<item id="pbs1" alias="餐后血糖1(mmol\L)" type="double" length="6" display="1" enableKeyEvents="true" group="第二次"/>
	<item id="result1" alias="结果1" type="string" length="1" display="1" fixed="true" group="第二次">
		<dic id="chis.dictionary.OGTTResult"/>
	</item>
	<item id="checkUser1" alias="核实人1" type="string" length="20" display="1"  defaultValue="%user.userId" queryable="true" group="第二次">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="checkDate1" alias="核实日期1" type="date" display="1" defaultValue="%server.date.today" queryable="true" group="第二次">
	</item>
	<item id="clinicSymptom1" alias="有临床症状1" type="string" display="1" length="1" group="第二次">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="fbs2" alias="空腹血糖2(mmol\L)" type="double" length="6" display="1" enableKeyEvents="true" group="第三次"/>
	<item id="pbs2" alias="餐后血糖2(mmol\L)" type="double" length="6" display="1" enableKeyEvents="true" group="第三次"/>
	<item id="result2" alias="结果2" type="string" length="1" display="1" fixed="true" group="第三次">
		<dic id="chis.dictionary.OGTTResult"/>
	</item>
	<item id="checkUser2" alias="核实人2" type="string" length="20" display="1"  defaultValue="%user.userId" queryable="true" group="第三次">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="checkDate2" alias="核实日期2" type="date" display="1" defaultValue="%server.date.today" queryable="true" group="第三次">
	</item>
	<item id="clinicSymptom2" alias="有临床症状2" type="string" display="1" length="1" group="第三次">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="inputDate" alias="录入日期" type="date" fixed="true" display="1"
		defaultValue="%server.date.today">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
			<set type="exp">['$','%user.manageUnit.id']</set>
	</item>

	<item id="inputUser" alias="录入人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
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
