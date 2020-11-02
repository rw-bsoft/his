<?xml version="1.0" encoding="UTF-8"?>
<entry alias="高血压档案" sort="a.createDate desc" entityName="chis.application.mobileApp.schemas.MDC_HypertensionDetailApp" tableName="MDC_HypertensionRecord">
	<item id="phrId" alias="档案编号" type="string" length="30" width="160" pkey="true" queryable="true" display="3"/>
	<item id="manaDoctorId" alias="责任医生" not-null="1" update="false" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" fixed="true" width="180" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="familyHistroy" alias="家族史" type="string" length="64" display="2">
	   	<dic render="LovCombo"> 
	      <item key="1" text="无"/>  
	      <item key="2" text="高血压"/>  
	      <item key="3" text="糖尿病"/>  
	      <item key="4" text="冠心病"/>
	      <item key="7" text="脑卒中"/>   
	      <item key="98" text="拒答"/> 
	      <item key="99" text="其他"/> 
    	</dic> 
	</item>
	<item id="familyHistroyOther" alias="家族史其他" type="string" display="2" length="100"/>
	
	<item id="smoke" alias="吸烟频率" type="string" display="2" length="1" not-null="1">
		<dic id="chis.dictionary.CV5101_24"/>
	</item>
	<item id="smokeCount" alias="日吸烟量(支)" type="int" display="2" length="3"/>
	
	<item id="drink" alias="饮酒频率" type="string" display="2" length="2" not-null="1">
		<dic id="chis.dictionary.CV5101_26" render="Tree"/>
	</item>
	<item id="drinkTypeCode" alias="饮酒种类" type="string" display="2" length="64">
		<dic id="chis.dictionary.drinkTypeCode_life" render="LovCombo"/>
	</item>
	<item id="drinkCount" alias="日饮酒量(两)" type="int" display="2" length="4"/>
	
	<item id="drinkOver" alias="饮酒过量" type="string" length="1" display="2" not-null="1">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>

	<item id="SFNRXJGL" alias="纳入细节管理" type="string" length="2" display="2">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="XJGLDXBH" alias="细节管理序号" type="string" display="2" length="32"/>
  	
	<item id="train" alias="锻炼频率" type="string" display="2" length="2" not-null="1">
		<dic id="chis.dictionary.CV5101_28" render="Tree"/>
	</item>
	<item id="eateHabit" alias="饮食习惯" type="string" length="64" >
		<dic id="chis.dictionary.eateHabit" render="LovCombo" />
	</item>
	<item id="recordSource" alias="检出途径" type="string" not-null="1" length="1" display="2">
		<dic id="chis.dictionary.hypertensionRecordSource"/>
	</item>
	
	<item id="confirmDate" alias="临床确诊时间" type="date" display="2" defaultValue="%server.date.today" not-null="1" enableKeyEvents="true" validationEvent="false"  maxValue="%server.date.today"/>
	<item id="deaseAge" alias="病程" type="string" virtual="true" fixed="true" display="2"/>
	<item id="clinicAddress" alias="经常就诊地点" type="string" not-null="1" length="1" display="2">
		<dic>
			<item key="1" text="本院"/>
			<item key="2" text="其他一级医院"/>
			<item key="3" text="本区二、三级医院"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	
	<item id="viability" alias="生活自理能力" type="string" not-null="1" length="1" display="2">
		<dic>
			<item key="1" text="完全自理"/>
			<item key="2" text="部分自理"/>
			<item key="3" text="完全不能自理"/>
		</dic>
	</item>
	<item id="height" alias="身高(cm)" not-null="1" type="double" display="2" minValue="100" maxValue="300" enableKeyEvents="true" validationEvent="false"/>
	<item id="weight" alias="体重(kg)" not-null="1" type="double" display="2" minValue="30" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<item id="bmi" length="6" alias="BMI" type="double" display="2" fixed="true" virtual="true"/>
	
	<item id="constriction" alias="收缩压(mmHg)" type="int" not-null="1" display="2" minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<item id="diastolic" alias="舒张压(mmHg)" type="int" not-null="1" display="2" minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<!-- 以下隐藏	-->
	<item id="riskiness" alias="危险因素" type="string" not-null="1" length="64" defaultValue="0" display="0" >
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo"/> 
	</item>
	<item id="targetHurt" alias="靶器官伤害" type="string" not-null="1" defaultValue="0" length="64" display="0">
		<dic id="chis.dictionary.targetHurt" render="LovCombo"/> 
	</item>
	<item id="complication" alias="并发症" type="string" not-null="1" defaultValue="0" length="64" display="0">
		<dic id="chis.dictionary.complication" render="LovCombo"/> 
	</item>
	<!-- -->
	<!-- 
		2010-04-8 added by chinnsii. 
		to mark whether person with normal blood pressure has bean taken medicine. 
		-->
	<item id="afterMedicine" alias="血压正常原因" type="string" length="20" fixed="true" display="2"/>
	<item id="hypertensionGroup" alias="管理分组" type="string" length="2" display="2" fixed="true" queryable="true">
		<dic id="chis.dictionary.hyperGroupExt"/>
	</item>
	<item id="riskLevel" alias="危险分层" type="string" length="1" fixed="true" display="2">
		<dic id="chis.dictionary.riskLevel"/>
	</item> 
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>