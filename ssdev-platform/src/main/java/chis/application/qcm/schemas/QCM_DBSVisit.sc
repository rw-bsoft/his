<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.qcm.schemas.QCM_DBSVisit" tableName="chis.application.dbs.schemas.MDC_DiabetesVisit" alias="糖尿病随访">
	<item id="visitId" pkey="true" alias="随访标识" type="string" length="16" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item>  
	
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.mobileNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	
	<item ref="d.diabetesGroup" alias="组别" type="string" length="2" fixed="true" display="1"> 
		<dic> 
			<item key="01" text="一组"/>  
			<item key="02" text="二组"/>  
			<item key="03" text="三组"/>  
		</dic> 
	</item>  
	<item id="visitDate" alias="随访日期" type="date" width="100" length="1" display="1"/>	
	<item id="visitWay" alias="随访方式" type="string" length="1" not-null="true"> 
		<dic id="chis.dictionary.visitWay"/> 
	</item>  	
	<item ref="d.manaUnitId" alias="管辖机构" type="string" length="20" display="1" queryable="true"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" parentKey="%user.manageUnit.id" rootVisible="true"/> 
	</item>  	
	<item ref="d.manaDoctorId" alias="责任医生" type="string" length="20" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>	
	
	<item id="visitEffect" alias="转归" type="string" length="1" defaultValue="1"> 
		<dic> 
			<item key="1" text="继续随访"/>  
			<item key="2" text="暂时失访"/>  
			<item key="9" text="终止管理"/> 
		</dic> 
	</item>  
	<item id="noVisitReason" alias="原因" type="string" length="100" colspan="2" anchor="100%" fixed="true"> 
		<dic> 
			<item key="1" text="死亡"/>  
			<item key="2" text="迁出"/>  
			<item key="3" text="失访"/> 
			<item key="4" text="拒绝"/> 
		</dic> 
	</item> 	
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="medicine" alias="服药依从性" type="string" length="1" enableKeyEvents="true" not-null="1"> 
		<dic> 
			<item key="1" text="规律"/>  
			<item key="2" text="间断"/>  
			<item key="3" text="不服药"/>  
			<item key="4" text="拒绝服药"/> 
		</dic> 
	</item> 
	<item id="visitType" alias="随访分类" type="string" length="1" colspan="1"> 
		<dic id="chis.dictionary.visitType"/> 
	</item>  
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

	
	<item id="phrId" alias="档案编码" type="string" length="30" fixed="true" colspan="2" hidden="true" display="0"/>  
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" hidden="true" display="0"/>  
	<item id="visitUnit" alias="随访机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="nextDate" alias="下次预约时间" type="date" display="0"/>  
	<item id="symptoms" alias="症状" type="string" length="64" colspan="2" defaultValue="1" display="0"> 
		<dic id="chis.dictionary.symptoms" render="LovCombo"/> 
	</item>  
	<item id="otherSymptoms" alias="其他症状" type="string" length="64" colspan="2" display="0"/>  

	<item id="constriction" alias="收缩压(mmHg)" type="int" length="3" minValue="10" maxValue="500" enableKeyEvents="true" not-null="1" display="0"/>  
	<item id="diastolic" alias="舒张压(mmHg)" type="int" length="3" minValue="10" maxValue="500" enableKeyEvents="true" not-null="1" display="0"/>  
	<item id="weight" alias="体重(kg)" type="double" length="6" minValue="20" maxValue="140" enableKeyEvents="true" not-null="1" display="0"/>  
	<item id="targetWeight" alias="目标体重(kg)" type="double" length="6" display="0"/>  
	<item id="bmi" alias="BMI" type="double" length="6" not-null="1" fixed="true" display="0"/> 
	<item id="targetBmi" alias="目标BMI" type="double" length="6" fixed="true" display="0"/>  
	<item id="pulsation" alias="足背动脉搏动" type="string" length="1" display="0"> 
		<dic id="chis.dictionary.dorsalArtery"/> 
	</item>  
	<item id="otherSigns" alias="其它体征" type="string" length="20" display="0"/>  
	<item id="diet" alias="饮食" type="string" length="1" display="0"> 
		<dic> 
			<item key="1" text="合理"/>  
			<item key="2" text="不合理"/> 
		</dic> 
	</item>  
	<item id="smokeCount" alias="日吸烟量(支)" type="int" minValue="0" maxValue="999" length="3" not-null="1" display="0"/>  
	<item id="targetSmokeCount" alias="目标量(支)" type="int" length="3"  minValue="0" maxValue="999" display="0"/>  
	<item id="drinkTypeCode" alias="饮酒种类" type="string" display="0" length="20" > 
		<dic id="chis.dictionary.drinkTypeCode" render="LovCombo"/> 
	</item>  
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="3"  minValue="0" maxValue="999" not-null="1" enableKeyEvents="true" display="0"/>  
	<item id="targetDrinkCount" alias="目标量(两)"  minValue="0" maxValue="999" type="int" length="3" display="0"/>  
	<item id="trainTimesWeek" alias="周运动次数" type="int" length="2"  minValue="0" maxValue="99" not-null="1" display="0"/>  
	<item id="targetTrainTimesWeek" alias="目标运动次数" type="int"  minValue="0" maxValue="999" length="3" display="0"/>  
	<item id="trainMinute" alias="每次时长(分)" type="int"  minValue="0" maxValue="999" length="3" not-null="1" display="0"/>  
	<item id="targetTrainMinute" alias="目标时长(分)" type="int"  minValue="0" maxValue="999" length="3" display="0"/>  
	<item id="loseWeight" alias="是否减轻体重" type="string" length="1" fixed="true" display="0"> 
		<dic> 
			<item key="1" text="需要"/>  
			<item key="2" text="不需要"/> 
		</dic> 
	</item>  
	<item id="food" alias="主食(克/天)" type="int" length="3"  minValue="0" maxValue="999" not-null="1" display="0"/>  
	<item id="targetFood" alias="目标(克/天)" type="int"  minValue="0" maxValue="999" length="3" display="0"/>  
	<item id="fbs" alias="空腹血糖" type="double" length="6" enableKeyEvents="true" display="0"/>  
	<item id="fbsTest" alias="空腹测量方式" type="string" length="1" enableKeyEvents="true" display="0">
		<dic> 
			<item key="1" text="末梢血"/>  
			<item key="2" text="血浆"/> 
		</dic>   
	</item>    
	<item id="pbs" alias="餐后血糖" type="double" length="6" enableKeyEvents="true" display="0"/>  
	<item id="pbsTest" alias="餐后测量方式" type="string" length="1" enableKeyEvents="true" display="0">
		<dic> 
			<item key="1" text="末梢血"/>  
			<item key="2" text="血浆"/> 
		</dic>   
	</item>  
	<item id="unit" alias="血糖单位" type="string" defaultValue="1" fixed="true" display="0"> 
		<dic> 
			<item key="1" text="mmol/L"/>  
			<item key="2" text="mg/dl"/> 
		</dic> 
	</item>  
	<item id="psychologyChange" alias="心理调整" type="string" length="1" display="0"> 
		<dic> 
			<item key="1" text="良好"/>  
			<item key="2" text="一般"/>  
			<item key="3" text="差"/> 
		</dic> 
	</item>  
	<item id="obeyDoctor" alias="遵医行为" type="string" length="1" canInput="true" display="0"> 
		<dic> 
			<item key="1" text="良好"/>  
			<item key="2" text="一般"/>  
			<item key="3" text="差"/> 
		</dic> 
	</item>  
	<item id="tc" alias="TC(mmol/L)" type="double" length="8" display="0"/>  
	<item id="hdl" alias="HDL(mmol/L)" type="double" length="8" display="0"/>  
	<item id="ldl" alias="LDL(mmol/L)" type="double" length="8" display="0"/>  
	<item id="hbA1c" alias="糖化血红蛋白(%)" type="double" length="6" display="0"/>  
	<item id="testDate" alias="检查日期" type="date" display="0"/>  
	<item id="bloodFat" alias="血脂" type="double" length="6" precision="2" display="0"/>  
	<item id="medicineNot" alias="不规律服药原因" fixed="true" type="string" length="2" display="0">
		<dic>
			<item key="1" text="经济原因" />
			<item key="2" text="忘记" />
			<item key="3" text="不良反应" />
			<item key="4" text="配药不方便" />
			<item key="99" text="其他" />
		</dic>
	</item>
	<item id="medicineOtherNot" alias="其他原因" fixed="true" type="string" length="100"  colspan="2" display="0">
	</item>
	<item id="adverseReactions" alias="药物不良反应" type="string" length="1" display="0"> 
		<dic> 
			<item key="1" text="有"/>  
			<item key="2" text="无"/> 
		</dic> 
	</item>  
	<item id="glycopenia" alias="低血糖反应" type="string" length="1" display="0"> 
		<dic> 
			<item key="1" text="无"/>  
			<item key="2" text="偶尔"/>  
			<item key="3" text="频繁"/> 
		</dic> 
	</item>  
	<item id="healthProposal" alias="健康处方建议" type="string" colspan="2"  length="100" display="0">
		<dic  render="LovCombo">
			<item key="1" text="控制饮食" />
			<item key="2" text="戒烟戒酒" />
			<item key="3" text="减轻体重" />
			<item key="4" text="规律活动" />
			<item key="5" text="放松情绪" />
			<item key="6" text="定期检查" />
			<item key="7" text="遵医嘱服药" />
			<item key="8" text="其他" />
		</dic>
	</item>
	<item id="otherHealthProposal" alias="其他建议" type="string" colspan="2" fixed="true" length="100" display="0"/>  
	<item id="complicationChange" alias="并发症变化" type="string" width="150" length="10" defaultValue="3" not-null="1" display="0"> 
		<dic> 
			<item key="1" text="有新并发症"/>  
			<item key="2" text="原有并发症加重"/> 
			<item key="3" text="无并发症"/> 
		</dic> 
	</item>  
	<item id="referralOffice" alias="机构及科别" type="string" length="20" display="0"/>  
	<item id="referralReason" alias="转诊原因" type="string" length="200" colspan="2" display="0"/>  
	<item id="lateInput" alias="延后录入" type="string" display="0"> 
		<dic> 
			<item key="1" text="是"/>  
			<item key="2" text="否"/> 
		</dic> 
	</item>  
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="0"> 
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.userId']</set> 
	</item>  
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0" width="180" defaultValue="%user.manageUnit.id"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.manageUnit.id']</set> 
	</item>  
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield" defaultValue="%server.date.today" display="0"> 
		<set type="exp">['$','%server.date.datetime']</set> 
	</item>  
	<item ref="d.status" display="0"/>  
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
