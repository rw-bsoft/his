<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesDetailApp" tableName="MDC_DiabetesRecord" alias="糖尿病档案明细" sort="a.createDate desc" version="1332292315384" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS22\WebRoot\WEB-INF\config\schema\mdc/MDC_DiabetesRecord.xml">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="familyHistroy" alias="糖尿病家族史" type="string" length="1" display="2" defaultValue="3" not-null="1">
		<dic>
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="3" text="不知道"/>
		</dic>
	</item>
	<item id="history" alias="家族史" type="string" length="100" virtual="true" display="2" fixed="true"/>
	
	<item id="riskFactors" alias="危险因素" type="string" length="50" display="2">
		<dic render="LovCombo">
			<item key="1" text="有糖调节受损史"/>
			<item key="2" text="年龄≥45岁"/>
			<item key="3" text="超重、肥胖（BMI≥24kg/m2），男性腰围≥90cm，女性腰围≥85cm"/>
			<item key="4" text="2型糖尿病患者的一级亲属"/>
			<item key="5" text="高危种族"/>
			<item key="6" text="有巨大儿（出生体重≥4kg）生产史，妊娠糖尿病史"/>
			<item key="7" text="高血压（血压≥140/90mmHg)，或正在接受降压治疗"/>
			<item key="8" text="血脂异常（HDL-C≤0.91mmol/L（≤35mg/dl）及TG≥2.22mmol/L（≥200mg/dl)，或正在接受调脂治疗"/>
			<item key="9" text="心脑血管疾病患者"/>
			<item key="10" text="有一过性糖皮质激素诱发糖尿病病史者"/>
			<item key="11" text="BMI≥28kg/m2的多囊卵巢综合征患者"/>
			<item key="12" text="严重精神病和（或）长期接受抗抑郁症药物治疗的患者"/>
			<item key="13" text="静坐生活方式"/>
		</dic>
	</item>
	<item id="diagnosisDate" alias="确诊年月" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true" not-null="1" maxValue="%server.date.today"/>
	<item id="diagnosisUnit" alias="确诊单位" type="string" length="25" display="2"/>
	<item id="fbs" alias="建卡空腹血糖" type="double" length="6" display="2" enableKeyEvents="true"/>
	<item id="pbs" alias="建卡餐后血糖" type="double" length="6" display="2" enableKeyEvents="true"/>
	<item id="unit" alias="血糖单位" type="string" not-null="1" display="2" defaultValue="1" fixed="true">
		<dic>
			<item key="1" text="mmol/L"/>
			<item key="2" text="mg/dl"/>
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6" display="2" minValue="100" maxValue="300" not-null="1" enableKeyEvents="true"/>
	<item id="weight" alias="体重(kg)" type="double" length="6" display="2" minValue="30" maxValue="500" not-null="1" enableKeyEvents="true"/>
	<item id="bmi" alias="BMI" type="double" display="2" length="6" fixed="true" virtual="true"/>
	<item id="diabetesType" alias="病例种类" type="string" length="1" display="3" not-null="1">
		<dic id="chis.dictionary.diabetesType"/>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
