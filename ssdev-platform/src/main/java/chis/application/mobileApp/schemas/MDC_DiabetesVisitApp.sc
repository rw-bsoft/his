<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesVisit" alias="糖尿病随访" version="1344848127031" filename="D:\workspace\BSCHIS\WebRoot\WEB-INF\config\schema\mdc/MDC_DiabetesVisit.xml"> 
	<item id="visitId" pkey="true" alias="随访标识" type="string" length="16" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item>  
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>	
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.phoneNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	<item id="phrId" alias="档案编码" type="string" length="30" fixed="true" colspan="2" hidden="true" display="0"/>  
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" hidden="true" display="0"/>  
	<item id="visitDate" alias="随访日期" type="date" not-null="1" queryable="true"/>  
	
	<item id="notes" alias="备注" type="string" length="50"/>

	  
	<item id="nextDate" alias="下次预约时间" type="date" />  
	
	<item id="otherSymptoms" alias="其他症状" type="string" length="64" colspan="2"/>  
	
	<item id="constriction" alias="收缩压(mmHg)" type="int" length="3" minValue="10" maxValue="500" enableKeyEvents="true" not-null="1"/>  
	<item id="diastolic" alias="舒张压(mmHg)" type="int" length="3" minValue="10" maxValue="500" enableKeyEvents="true" not-null="1"/>  
	<item id="weight" alias="体重(kg)" type="double" length="6" minValue="20" maxValue="500" enableKeyEvents="true" not-null="1"/>  
	<item id="targetWeight" alias="目标体重(kg)" type="double" length="6"/>  
	<item id="bmi" alias="BMI" type="double" length="6" not-null="1" fixed="true"/> 
	<item id="targetBmi" alias="目标BMI" type="double" length="6" fixed="true"/>  
	
	<item id="otherSigns" alias="其它体征" type="string" length="20"/>  
	
	<item id="smokeCount" alias="日吸烟量(支)" type="int" minValue="0" maxValue="999" length="3" />  
	<item id="targetSmokeCount" alias="目标量(支)" type="int" length="3"  minValue="0" maxValue="999"/>  
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="3"  minValue="0" maxValue="999" enableKeyEvents="true"/>  
	<item id="targetDrinkCount" alias="目标量(两)"  minValue="0" maxValue="999" type="int" length="3"/>  
	<item id="trainTimesWeek" alias="周运动次数" type="int" length="2"  minValue="0" maxValue="99" not-null="1"/>  
	<item id="targetTrainTimesWeek" alias="目标运动次数" type="int"  minValue="0" maxValue="999" length="3"/>  
	<item id="trainMinute" alias="每次时长(分)" type="int"  minValue="0" maxValue="999" length="3" not-null="1"/>  
	<item id="targetTrainMinute" alias="目标时长(分)" type="int"  minValue="0" maxValue="999" length="3"/>  
	
	<item id="food" alias="主食(克/天)" type="int" length="3"  minValue="0" maxValue="999" not-null="1"/>  
	<item id="targetFood" alias="目标(克/天)" type="int"  minValue="0" maxValue="999" length="3"/>  
	<item id="fbs" alias="空腹血糖" type="double" length="6" enableKeyEvents="true"/>  
	
	<item id="pbs" alias="餐后血糖" type="double" length="6" enableKeyEvents="true"/>  
	
	<item id="rbs" alias="随机血糖" type="double" length="6" enableKeyEvents="true"/>  
	
	<item id="tc" alias="TC(mmol/L)" type="double" length="8"/>  
	<item id="hdl" alias="HDL(mmol/L)" type="double" length="8"/>  
	<item id="ldl" alias="LDL(mmol/L)" type="double" length="8"/>  
	<item id="hbA1c" alias="糖化血红蛋白(%)" type="double" length="6"/>  
	<item id="testDate" alias="检查日期" type="date"/>  
	<item id="bloodFat" alias="血脂" type="double" length="6" precision="2"/>  
	

	<item id="medicineOtherNot" alias="其他原因" fixed="true" type="string" length="100"  colspan="2">
	</item>
	<item id="otherHealthProposal" alias="其他建议" type="string" colspan="2" fixed="true" length="100"/>  
	
	<item id="referralOffice" alias="机构及科别" type="string" length="50"/>  
	<item id="referralReason" alias="转诊原因" type="string" length="200" colspan="2">
		<dic id="chis.dictionary.reason01" render="LovCombo" />
	</item>
</entry>
