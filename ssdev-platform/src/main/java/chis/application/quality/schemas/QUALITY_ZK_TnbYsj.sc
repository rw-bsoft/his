<?xml version="1.0" encoding="UTF-8"?>
 
<entry alias="高血压随访"  
	   tableName="chis.application.dbs.schemas.MDC_DiabetesVisit" >
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
  <item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
  <item id="visitUnit" alias="随访机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>
  <item id="visitWay" alias="随访方式" type="string" length="1" not-null="true"> 
    <dic id="chis.dictionary.visitWay"/> 
  </item>  
  <item id="nextDate" alias="下次预约时间" type="date" />  
  <item id="symptoms" alias="症状" type="string" length="64" colspan="2" defaultValue="1"> 
    <dic id="chis.dictionary.symptoms" render="LovCombo"/> 
  </item>  
  <item id="otherSymptoms" alias="其他症状" type="string" length="64" colspan="2"/>  
  <item id="diabetesGroup" alias="管理组别" type="string" length="2" fixed="true"> 
    <dic> 
      <item key="01" text="一组"/>  
      <item key="02" text="二组"/>  
      <item key="03" text="三组"/>  
      <item key="99" text="一般管理对象"/> 
    </dic> 
  </item>  
  <item id="visitEffect" alias="转归" type="string" length="1" defaultValue="1"> 
    <dic> 
      <item key="1" text="继续随访"/>  
      <item key="2" text="暂时失访"/>  
      <item key="9" text="终止管理"/> 
    </dic> 
  </item>  
  <item id="noVisitReason" alias="原因" type="string" length="100" colspan="2" anchor="100%" fixed="true"/>  
  <item id="constriction" alias="收缩压(mmHg)" type="int" length="3" minValue="10" maxValue="500" enableKeyEvents="true" not-null="1"/>  
  <item id="diastolic" alias="舒张压(mmHg)" type="int" length="3" minValue="10" maxValue="500" enableKeyEvents="true" not-null="1"/>  
  <item id="weight" alias="体重(kg)" type="double" length="6" minValue="20" maxValue="140" enableKeyEvents="true" not-null="1"/>  
  <item id="targetWeight" alias="目标体重(kg)" type="double" length="6"/>  
  <item id="bmi" alias="BMI" type="double" length="6" not-null="1" fixed="true"/> 
  <item id="targetBmi" alias="目标BMI" type="double" length="6" fixed="true"/>  
  <item id="pulsation" alias="足背动脉搏动" type="string" length="1"> 
    <dic id="chis.dictionary.dorsalArtery"/> 
  </item>  
  <item id="otherSigns" alias="其它体征" type="string" length="20"/>  
  <item id="diet" alias="饮食" type="string" length="1"> 
    <dic> 
      <item key="1" text="合理"/>  
      <item key="2" text="不合理"/> 
    </dic> 
  </item>  
  <item id="smokeCount" alias="日吸烟量(支)" type="int" minValue="0" maxValue="999" length="3" not-null="1"/>  
  <item id="targetSmokeCount" alias="目标量(支)" type="int" length="3"  minValue="0" maxValue="999"/>  
  <item id="drinkTypeCode" alias="饮酒种类" type="string" display="2" length="20" > 
    <dic id="chis.dictionary.drinkTypeCode" render="LovCombo"/> 
  </item>  
  <item id="drinkCount" alias="日饮酒量(两)" type="int" length="3"  minValue="0" maxValue="999" not-null="1" enableKeyEvents="true"/>  
  <item id="targetDrinkCount" alias="目标量(两)"  minValue="0" maxValue="999" type="int" length="3"/>  
  <item id="trainTimesWeek" alias="周运动次数" type="int" length="2"  minValue="0" maxValue="99" not-null="1"/>  
  <item id="targetTrainTimesWeek" alias="目标运动次数" type="int"  minValue="0" maxValue="999" length="3"/>  
  <item id="trainMinute" alias="每次时长(分)" type="int"  minValue="0" maxValue="999" length="3" not-null="1"/>  
  <item id="targetTrainMinute" alias="目标时长(分)" type="int"  minValue="0" maxValue="999" length="3"/>  
  <item id="loseWeight" alias="是否减轻体重" type="string" length="1" fixed="true"> 
    <dic> 
      <item key="1" text="需要"/>  
      <item key="2" text="不需要"/> 
    </dic> 
  </item>  
  <item id="food" alias="主食(克/天)" type="int" length="3"  minValue="0" maxValue="999" not-null="1"/>  
  <item id="targetFood" alias="目标(克/天)" type="int"  minValue="0" maxValue="999" length="3"/>  
  <item id="fbs" alias="空腹血糖" type="double" length="6" enableKeyEvents="true"/>  
  <item id="pbs" alias="餐后血糖" type="double" length="6" enableKeyEvents="true"/>  
  <item id="unit" alias="血糖单位" type="string" defaultValue="1" fixed="true"> 
    <dic> 
      <item key="1" text="mmol/L"/>  
      <item key="2" text="mg/dl"/> 
    </dic> 
  </item>  
  <item id="psychologyChange" alias="心理调整" type="string" length="1"> 
    <dic> 
      <item key="1" text="良好"/>  
      <item key="2" text="一般"/>  
      <item key="3" text="差"/> 
    </dic> 
  </item>  
  <item id="obeyDoctor" alias="遵医行为" type="string" length="1" canInput="true"> 
    <dic> 
      <item key="1" text="良好"/>  
      <item key="2" text="一般"/>  
      <item key="3" text="差"/> 
    </dic> 
  </item>  
  <item id="tc" alias="TC(mmol/L)" type="double" length="8"/>  
  <item id="hdl" alias="HDL(mmol/L)" type="double" length="8"/>  
  <item id="ldl" alias="LDL(mmol/L)" type="double" length="8"/>  
  <item id="hbA1c" alias="糖化血红蛋白(%)" type="double" length="6"/>  
  <item id="testDate" alias="检查日期" type="date"/>  
  <item id="bloodFat" alias="血脂" type="double" length="6" precision="2"/>  
  <item id="medicine" alias="服药依从性" type="string" length="1" enableKeyEvents="true" not-null="1"> 
    <dic> 
      <item key="1" text="规律"/>  
      <item key="2" text="间断"/>  
      <item key="3" text="不服药"/>  
      <item key="4" text="拒绝服药"/> 
    </dic> 
  </item>  
  <item id="adverseReactions" alias="药物不良反应" type="string" length="1"> 
    <dic> 
      <item key="1" text="有"/>  
      <item key="2" text="无"/> 
    </dic> 
  </item>  
  <item id="glycopenia" alias="低血糖反应" type="string" length="1"> 
    <dic> 
      <item key="1" text="无"/>  
      <item key="2" text="偶尔"/>  
      <item key="3" text="频繁"/> 
    </dic> 
  </item>  
  <item id="visitType" alias="随访分类" type="string" length="1" colspan="1"> 
    <dic id="chis.dictionary.visitType"/> 
  </item>  
  <item id="complicationChange" alias="并发症变化" type="string" width="150" length="10" display="2"> 
    <dic render="LovCombo"> 
      <item key="1" text="有新并发症"/>  
      <item key="2" text="原有并发症加重"/> 
    </dic> 
  </item>  
  <item id="referralOffice" alias="机构及科别" type="string" length="20"/>  
  <item id="referralReason" alias="转诊原因" type="string" length="200" colspan="2"/>  
  <item id="inputUnit" alias="录入机构" type="string" length="20" colspan="2" update="false" defaultValue="%user.manageUnit.id" fixed="true"> 
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.manageUnit.id']</set> 
  </item>  
  <item id="inputUser" alias="录入者" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" queryable="true"> 
    <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.userId']</set> 
  </item>  
  <item id="inputDate" alias="录入日期" type="datetime" xtype="datefield" update="false" defaultValue="%server.date.today" fixed="true" queryable="true"> 
    <set type="exp">['$','%server.date.datetime']</set> 
  </item>  
  <item id="lateInput" alias="延后录入" type="string" display="0"> 
    <dic> 
      <item key="1" text="是"/>  
      <item key="2" text="否"/> 
    </dic> 
  </item>  
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1"> 
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.userId']</set> 
  </item>  
  <item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" defaultValue="%user.manageUnit.id"> 
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.manageUnit.id']</set> 
  </item>  
  <item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield" defaultValue="%server.date.today" display="1"> 
    <set type="exp">['$','%server.date.datetime']</set> 
  </item>  
  <item id="manaUnitId" alias="管辖机构" type="string" length="20" display="0" queryable="true"> 
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" parentKey="%user.manageUnit.id" rootVisible="true"/> 
  </item>  
  <item ref="d.status" display="0"/>  
  <item ref="d.manaDoctorId" display="0" queryable="true"/>  
  <item ref="c.regionCode_text" alias="网格地址" display="0"/>  
  <relations> 
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>  
    <relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord"> 
      <join parent="phrId" child="phrId"/> 
    </relation>  
    <relation type="children" entryName="chis.application.dbs.schemas.MDC_DiabetesRecord"> 
      <join parent="phrId" child="phrId"/> 
    </relation> 
  </relations> 
</entry>
