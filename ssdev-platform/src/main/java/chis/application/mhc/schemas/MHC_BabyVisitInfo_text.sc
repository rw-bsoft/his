<?xml version="1.0" encoding="UTF-8"?>              
<entry entityName="chis.application.mhc.schemas.MHC_BabyVisitInfo_text" alias="新生儿家庭访视记录">
	<item id="babyName" alias="婴儿姓名" type="string" length="30" tag="text"/>
	<item id="babySex" alias="性别"   type="string" length="10" width="540"   tag="radioGroup" colspan="3"   not-null="1">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="babyBirth" alias="出生日期"   type="date" width="150"   not-null="1"  tag="text" maxValue="%server.date.today"  />
	
	<item id="babyIdCard" alias="身份证号" type="string" length="18"  width="135"   vtype="idCard"   enableKeyEvents="true"  tag="text"  />
	<item id="certificateNo" alias="出生证号" type="int" length="10"  tag="text"   />
	         
	<item id="babyAddress" alias="家庭住址" type="string" length="100" width="394" tag="text"   />
	<item id="fatherName" alias="父亲姓名" type="string" length="32" tag="text"   />
	<item id="fatherJob" alias="父亲职业" type="string" length="30"
		display="2">
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
	</item>
	<item id="fatherPhone" alias="父亲电话" type="int" length="25" width="90"   tag="text"  />
	<item id="fatherBirth" alias="父亲出生日期"   type="date" width="150"     tag="text" maxValue="%server.date.today"  />
	<item id="fatherEmpiId" type="string" display="0"/>
	<item id="motherName" alias="母亲姓名" type="string" length="32" tag="text"   />
	<item id="motherJob" alias="母亲职业" type="string" length="30"
		display="2" fixed="true"  >
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
	</item>
	<item id="motherPhone" alias="母亲电话" type="int" length="25" width="90"   tag="text"  />
	<item id="motherBirth" alias="母亲出生日期"   type="date" width="150"     tag="text"  maxValue="%server.date.today"  />
	<item id="gestation" alias="出生孕周" type="int" length="2" tag="text"   />
	<item id="pregnancyDisease" alias="母亲妊娠期患病情况" type="string" length="1" tag="radioGroup"   controValue="3" controId="otherDisease"/>
	<item id="otherDisease" alias="妊娠期其他疾病" type="string" length="30" tag="text"   /> 
	<item id="deliveryUnit" alias="助产机构名称" type="string" length="70"
		width="150" tag="text"   />
	<item id="empiId" type="string" length="32" display="0"  />	
	<item id="birthStatus" alias="出生情况" type="string" length="6" tag="checkBox"   >
		<dic>
	      <item key="1" text="顺产" />
	      <item key="2" text="头吸" />
	      <item key="3" text="产钳" />
	      <item key="4" text="剖宫" />
	      <item key="5" text="双多胎" />
	      <item key="6" text="臀位" />
	      <item key="7" text="其他" />
    	</dic>
	</item>
	<item id="otherStatus" alias="其它出生情况" type="string" length="30" tag="text"   />
	<item id="asphyxia" alias="新生儿窒息" type="string" length="2" tag="radioGroup"   />
	<item id="apgar1" alias="Apgar评分1" type="string" length="10" tag="text"  />
	<item id="apgar5" alias="Apgar评分5" type="string" length="10" tag="text"  />
	<item id="apgarNew" alias="Apgar评分" type="string" length="1" tag="radioGroup"   />
	<item id="malforMation" alias="是否有畸型" type="string" length="2" tag="radioGroup"  controValue="y" controId="malforMationDescription" />
	<item id="malforMationDescription" alias="畸形描述" type="string"
		length="30"  tag="text"  />
	<item id="hearingTest" alias="新生儿听力筛查" type="string" length="1" tag="radioGroup"   />
	<item id="illnessScreening" alias="新生儿疾病筛查" type="string" length="1" tag="radioGroup"   controValue="3" controId="otherIllness" />
	         
	<item id="otherIllness" alias="其他遗传代谢病" type="string" length="50" width="120" tag="text"   />
	<item id="weight" alias="出生体重(kg)" type="int" length="5"
		precision="2" width="120" tag="text"   />
	<item id="length" alias="出生身长(cm)" type="int" length="5"
		precision="2" width="120" tag="text"   />

	
	
</entry>