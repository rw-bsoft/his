<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_FirstVisitRecord_html" tableName="chis.application.mhc.schemas.MHC_FirstVisitRecord" alias="孕妇首次随访表">
	<item  Lb="SF" id="pregnantId" alias="孕妇档案编号" length="30" pkey="true"
		type="string"   fixed="true" hidden="true"
		generator="assigned">
	</item>
	<item Lb="SF" id="empiId" alias="EMPIID" length="32"   type="string" 
		display="0" />
	<item Lb="SF" id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"  
		fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item Lb="SF" id="quickeningWeek" alias="胎动孕周" type="int"/>
	<item Lb="DA" id="age" alias="孕妇年龄" type="int"/>
	<item  Lb="DA" id="husbandName" alias="丈夫姓名" disabled="true"/>
	<item Lb="DA"  id="husbandAGE" alias="丈夫年龄"   />
	<item Lb="DA"  id="husbandPhone" alias="丈夫电话"   />
  
	<item Lb="DA" id="gravidity" alias="孕次"  type="int" />
	<item Lb="DA" id="vaginalDelivery" alias="阴道分娩次数"  type="int"    defaultValue="0"/>
	<item Lb="DA" id="abdominalDelivery" alias="剖宫产次数"  type="int"    defaultValue="0" />
	<item Lb="DA" id="trafficFlow" alias="人工流产次数"     type="int"     defaultValue="0" />
	<item Lb="DA" id="dyingFetus" alias="死胎次数"  type="int"   defaultValue="0" />
	<item Lb="DA" id="stillBirth" alias="死产次数" type="int"   defaultValue="0" />
	<item Lb="DA" id="newbronDied" alias="死亡儿次数"    type="int"     defaultValue="0" />
	<item Lb="DA" id="abnormality" alias="畸形儿次数"  type="int"     defaultValue="0"/>
    
	<item Lb="DA" id="lastMenstrualPeriod" alias="末次月经时间" type="date" 
		width="160"    anchor="100%" maxValue="%server.date.today" colspan="2"/>
	<item Lb="DA" id="dateOfPrenatal" alias="预产期" type="date"   
		width="160" fixed="true" />
	<item Lb="DA" id="pastHistory" alias="既往病史"  type="string"  length="20" display="2" >
		<dic render="LovCombo">
			<item key="1" text="无" />
			<item key="2" text="心脏病" />
			<item key="3" text="肾脏疾病" />
			<item key="4" text="肝脏疾病" />
			<item key="5" text="高血压" />
			<item key="6" text="贫血" />
			<item key="7" text="糖尿病" />
			<item key="8" text="其他" />
		</dic>
	</item>
	<item Lb="DA" id="otherPastHistory" alias="其他既往病史" length="100" display="2"   type="string"
		colspan="2" fixed="true" />
	<item Lb="DA" id="familyHistory" alias="家族史" length="10" display="2"   type="string">
		<dic render="LovCombo">
			<item key="1" text="遗传性疾病史" />
			<item key="2" text="精神疾病史" />
			<item key="3" text="其他" />
		</dic>
	</item>
	<item Lb="DA" id="otherFamilyHistory" alias="其他家族史" length="200" display="2"   type="string"
		colspan="2" fixed="true" />
	<item Lb="DA" id="personHistory" alias="个人史" length="20" display="2"  type="string">
		<dic render="LovCombo">
			<item key="1" text="吸烟" />
			<item key="2" text="饮酒" />
			<item key="3" text="服用药物" />
			<item key="4" text="接触有毒有害物质" />
			<item key="5" text="接触放射线" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item Lb="DA" id="otherPersonHistory" alias="其他个人史" length="200" display="2"  type="string"
		colspan="2" fixed="true" />
	<item Lb="DA" id="gynecologyOPS" alias="妇科手术史" length="100" display="2"   type="radio" > 
		<dic render="LovCombo">
			<item key="1" text="无" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item Lb="DA" id="gynecologyOPS_other" alias="其他妇科手术史" length="200" display="2"  type="string"
		colspan="2" fixed="true" />
    
	<item Lb="DA"  id="height" alias="身高(cm)" display="2"  />
	<item  Lb="DA"  id="bloodTypeCode" alias="血型" >
		<dic id="chis.dictionary.blood"/>
	</item>
	<item  Lb="DA"  id="rhBloodCode" alias="RH血型"  >
		<dic id="chis.dictionary.rhBlood"/>
	</item>
	<item Lb="SF"  id="weight" alias="基础体重(kg)" display="2" precision="2" type="double"  not-null = "1"  length="8"/>
	<item Lb="SF" id="bmi" alias="体质指数" length="10" fixed="true" type="string"/>
   
	<item Lb="JY" id="JY_1" alias="听诊"  />
	<item Lb="JY" id="JY_101" alias="心脏" type="radio" idIf="JY_101_if">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_101_other" alias="心脏异常描述"  />
	<item Lb="JY" id="JY_102" alias="肺部"  type="radio" idIf="JY_102_if">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_102_other" alias="心脏异常描述"  />
   
	<item Lb="JY" id="JY_3" alias="妇科检查"  />
	<item Lb="JY" id="JY_301" alias="外阴"  type="radio" idIf="JY_301_if">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_301_other" alias="外阴异常描述"  />
	<item Lb="JY" id="JY_302" alias="阴道"  type="radio" idIf="JY_302_if">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_302_other" alias="阴道异常描述"  />
	<item Lb="JY" id="JY_303" alias="宫颈"  type="radio" idIf="JY_303_if">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_303_other" alias="宫颈异常描述"  />
	<item Lb="JY" id="JY_304" alias="子宫"  type="radio" idIf="JY_304_if">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_304_other" alias="子宫异常描述"  />
	<item Lb="JY" id="JY_305" alias="附件" type="radio" idIf="JY_305_if" >
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_305_other" alias="附件异常描述"  />
   
	<item Lb="JY" id="JY_5" alias="辅助检查"  />
	<item Lb="JY" id="JY_501" alias="血常规"  />
	<item Lb="JY" id="JY_50101" alias="血红蛋白值(g/L)"  />
	<item Lb="JY" id="JY_50102" alias="白细胞计数值(10^9/L)"  />
	<item Lb="JY" id="JY_50103" alias="血小板计数值(10^9/L)"  />
	<item Lb="JY" id="JY_50104" alias="其他"  />
   
	<item Lb="JY" id="JY_505" alias="尿常规"  />
	<item Lb="JY" id="JY_50501" alias="尿蛋白"  />
	<item Lb="JY" id="JY_50502" alias="尿糖"  />
	<item Lb="JY" id="JY_50503" alias="尿酮体"  />
	<item Lb="JY" id="JY_50504" alias="尿潜血"  />
	<item Lb="JY" id="JY_50505" alias="其他"  />
   
	<item Lb="JY" id="JY_504" alias="血糖(mmol/L)" not-null = "1"/>
   
	<item Lb="JY" id="JY_506" alias="肝功能"  />
	<item Lb="JY" id="JY_50601" alias="血清谷丙转氨酶(U/L)"  />
	<item Lb="JY" id="JY_50602" alias="血清谷草转氨酶(U/L)"  />
	<item Lb="JY" id="JY_50603" alias="白蛋白(g/L)"  />
	<item Lb="JY" id="JY_50604" alias="总胆红素(μmol/L)"  />
	<item Lb="JY" id="JY_50605" alias="结合胆红素(μmol/L)"  />
   
	<item Lb="JY" id="JY_507" alias="肾功能"  />
	<item Lb="JY" id="JY_50701" alias="血清肌酐(μmol/L)"  />
	<item Lb="JY" id="JY_50702" alias="血尿素氮(mmol/L)"  />
   
	<item Lb="JY" id="JY_508" alias="阴道分泌物"  type="checkbox"  not-null = "1">
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="滴虫" />
			<item key="3" text="假丝酵母菌" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item Lb="JY" id="JY_508_other" alias="阴道分泌物异常描述"   />
    
	<item Lb="JY" id="JY_509" alias="阴道清洁度" type="radio" >
		<dic render="LovCombo">
			<item key="1" text="Ⅰ度 " />
			<item key="2" text="Ⅱ度" />
			<item key="3" text="Ⅲ度" />
			<item key="4" text="Ⅳ度" />
		</dic>
	</item>
   
	<item Lb="JY" id="JY_510" alias="乙肝三系检测"  />
	<item Lb="JY" id="JY_51001" alias="乙肝表面抗原(HBSAG)"  />
	<item Lb="JY" id="JY_51002" alias="乙肝表面抗体(HBSAB)"  />
	<item Lb="JY" id="JY_51003" alias="乙型肝炎e抗原(HBEAG)"  />
	<item Lb="JY" id="JY_51004" alias="乙型肝炎e抗体(HBEAB)"  />
	<item Lb="JY" id="JY_51005" alias="乙型肝炎核心抗体(HBCAB)"  />
   
	<item Lb="JY" id="JY_511" alias="梅毒血清学试验" not-null = "1" type="radio" >
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_512" alias="HIV抗体检测"  not-null = "1" type="radio"  >
		<dic render="LovCombo">
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item Lb="JY" id="JY_514" alias="B超"  />
   
	<item Lb="SF" id="sbp" alias="收缩压(mmHg)" display="2" type="int" minValue="10"  length="8"
		maxValue="500" enableKeyEvents="true" validationEvent="false" not-null = "1"  />
	<item  Lb="SF" id="dbp" alias="舒张压(mmHg)" display="2" type="int" minValue="10"  length="8"
		maxValue="500" enableKeyEvents="true" validationEvent="false" not-null = "1"  /> 
 
	<item Lb="SF" id="diagnosisMethod" alias="妊娠确诊方法" type="string"  not-null = "1"
		length="1">
		<!--    <dic id="chis.dictionary.CV5201_08" />-->
	</item>
	<item Lb="SF" id="diagnosisDate" alias="妊娠确诊时间" type="date" update="false"  not-null = "1"
		maxValue="%server.date.today" />

	<item Lb="SF" id="generalComment" alias="总体评估" type="radio"    length="100" >
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item Lb="SF" id="commentText" alias="总体评估异常描述" type="radio"    length="200" />
	<item Lb="SF" id="HIGHRISKREASON" alias="高危因素" type="string"   length="100" />
  
	<item Lb="SF" id="highRiskLevel" alias="高危评级" type="string"   length="100" />
	<item Lb="SF" id="highRiskScore" alias="高危评分" type="int"   not-null = "1"/>
	<item Lb="SF" id="suggestion" alias="保健指导" length="45" display="2"
		type="string" colspan="2">
		<dic render="LovCombo">
			<item key="01" text="个人卫生" />
			<item key="02" text="心理" />
			<item key="03" text="膳食营养" />
			<item key="04" text="避免致畸因素和疾病对胚胎的不良影响 " />
			<item key="05" text="产前筛查宣传告知" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item Lb="SF" id="otherSuggestion" alias="其他指导" type="string" display="2"
		length="200" fixed="true" colspan="2"/>
    
	<item Lb="SF" id="referral" alias="转诊" type="radio"  display="2" length="1">
		<dic render="LovCombo">
			<item key="1" text="无" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item Lb="SF" id="reason" alias="原因" type="string" display="2" length="200"  colspan="2" fixed="true"/>
	<item Lb="SF" id="doccol" alias="机构及科室" type="string" display="2" length="50"  fixed="true"/>
	<item Lb="SF" id="visitPrecontractTime" alias="下次随访日期" type="date" minValue="%server.date.today"/>
	<item Lb="SF" id="visitDoctorCode" alias="随访医生" length="20" type="string"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
  
	<item Lb="SF" id="visitUnitCode" alias="随访机构" length="20"  type="string">
	</item>
	<item Lb="SF" id="createUnit" alias="录入机构" type="string" length="20">
	</item>
	<item Lb="SF" id="createUser" alias="录入人" type="string" length="20">
	</item>
	<item Lb="SF" id="lastModifyUnit" alias="最后修改机构" type="string" length="20">
	</item>
	<item Lb="SF" id="lastModifyUser" alias="最后修改人" type="string" length="20">
	</item>
	<item Lb="SF" id="lastModifyDate" alias="最后修改日期" type="datetime" >
	</item>
   
</entry>
