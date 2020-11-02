<?xml version="1.0" encoding="UTF-8"?>              
<entry entityName="chis.application.cdh.schemas.CDH_ChildVisitInfoAndRecord" alias="新生儿家庭访视记录">
	<item id="babySex" alias="性别"   type="string" length="10" width="540" not-null="1"  tag="radioGroup" colspan="3"  lb="jbxx" >
	<dic id="chis.dictionary.gender"/>
	</item>
	<item id="babyBirth" alias="出生日期"   type="date" width="150"   not-null="1"  tag="text" maxValue="%server.date.today" lb="jbxx" />
	<item id="babyIdCard" alias="身份证号" type="string" length="18"  width="135"   vtype="idCard"   enableKeyEvents="true"  tag="text" lb="jbxx" />
	<item id="certificateNo" alias="出生证号" type="int" length="16"  tag="text"  lb="jbxx" />
	<item id="babyAddress" alias="家庭住址" type="string" length="100" width="394" tag="text"  lb="jbxx" />
	<item id="fatherName" alias="父亲姓名" type="string" length="32" tag="text"  lb="jbxx" />
	<item id="fatherJob" alias="父亲的职业" type="string" length="20"    tag="text"  lb="jbxx" />
	<item id="fatherPhone" alias="父亲电话" type="string" length="25" width="90"   tag="text" lb="jbxx" />
	<item id="fatherBirth" alias="父亲出生日期"   type="date" width="150"     tag="text" maxValue="%server.date.today" lb="jbxx" />
	 <item id="fatherEmpiId" type="string" display="0"/>
	<item id="motherName" alias="母亲姓名" type="string" length="32" tag="text"  lb="jbxx" />
	<item id="motherJob" alias="母亲的职业" type="string" length="20"     tag="text" lb="jbxx" />
	<item id="motherPhone" alias="母亲电话" type="int" length="25" width="90"   tag="text" lb="jbxx" />
	<item id="motherBirth" alias="母亲出生日期"   type="date" width="150"     tag="text"  maxValue="%server.date.today" lb="jbxx" />
	<item id="motherEmpiId" type="string" display="0"/>
	<item id="gestation" alias="出生孕周" type="int" length="32" tag="text"  lb="jbxx" />
	<item id="pregnancyDisease" alias="母亲妊娠期患病情况" type="string" length="1" tag="radioGroup"  lb="jbxx" controValue="3" controId="otherDisease"/>
	<item id="otherDisease" alias="妊娠期其他疾病" type="string" length="30" tag="text"  lb="jbxx" /> 
	<item id="deliveryUnit" alias="助产机构名称" type="string" length="70"
		width="150" tag="text"  lb="jbxx" />
	<item id="empiId" type="string" length="32" display="0" lb="jbxx" />	
	<item id="birthStatus" alias="出生情况" type="string" length="6" tag="checkBox"  lb="jbxx" />
	<item id="otherStatus" alias="其它出生情况" type="string" length="30" tag="text"  lb="jbxx" />
	<item id="asphyxia" alias="新生儿窒息" type="string" length="2" tag="radioGroup"  lb="jbxx" />
	<item id="apgar1" alias="Apgar评分1" type="string" length="10" tag="text" lb="jbxx" />
	<item id="apgar5" alias="Apgar评分5" type="string" length="10" tag="text" lb="jbxx" />
	<item id="apgarNew" alias="Apgar评分" type="string" length="1" tag="radioGroup"  lb="jbxx" />
	<item id="malforMation" alias="是否有畸型" type="string" length="2" tag="radioGroup"  lb="jbxx" controValue="y" controId="malforMationDescription"/>
	<item id="malforMationDescription" alias="畸形描述" type="string"
		length="30"  tag="text" lb="jbxx" />
	<item id="hearingTest" alias="新生儿听力筛查" type="string" length="1" tag="radioGroup"  lb="jbxx" />
	<item id="illnessScreening" alias="新生儿疾病筛查" type="string" length="1" tag="radioGroup"  lb="jbxx" controValue="3" controId="otherIllness"/>
	<item id="otherIllness" alias="其他遗传代谢病" type="string" length="50" width="120" tag="text"  lb="jbxx" />
	<item id="weight" alias="出生体重(kg)" type="int" length="5"
		precision="2" width="120" tag="text"  lb="jbxx" />
	<item id="weightNow" alias="目前体重(kg)" type="int" length="5" precision="2" tag="text"  lb="fsjl" />
	<item id="length" alias="出生身长(cm)" type="int" length="5"
		precision="2" width="120" tag="text"  lb="jbxx" />

	<item id="feedWay" alias="喂养方式" type="string" length="1" tag="radioGroup"  lb="fsjl" />
	<item id="eatNum" alias="吃奶量(ml)" type="int" length="20" tag="text"  lb="fsjl" />	
	<item id="eatCount" alias="吃奶次数(次/日)" type="int" length="5" tag="text"  lb="fsjl" />
	<item id="vomit" alias="呕吐" type="string" length="1" tag="radioGroup"  lb="fsjl" />
	<item id="stoolStatus" alias="大便性状" type="string" length="1" tag="radioGroup"  lb="fsjl" />
	<item id="stoolTimes" alias="大便次数" type="int" tag="text" length="1" lb="fsjl" />
	<item id="temperature" alias="体温(℃)" type="int" length="4" precision="1" tag="text"  lb="fsjl" />
	<item id="pulse" alias="脉率(次/分)" type="int" length="5" width="100" tag="text"  lb="fsjl" />
	<item id="respiratoryFrequency" alias="呼吸频率(次/分)" type="int" length="5" width="100" tag="text"  lb="fsjl" />
	<item id="face" alias="面色" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="9" controId="faceOther"/>	
	<item id="faceOther" alias="其它面色" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="jaundice" alias="黄疸部位" type="string" length="1" tag="radioGroup"  lb="fsjl" />
	<item id="bregmaTransverse" alias="前囟纵径(cm)" type="int"
		length="5" precision="2" width="120" tag="text"  lb="fsjl" />
	<item id="bregmaLongitudinal" alias="前囟横径(cm)" type="int"
		length="5" precision="2" width="120" tag="text"  lb="fsjl" />
	<item id="bregmaStatus" alias="前囟状态" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="4" controId="otherStatus1"/>
	<item id="otherStatus1" alias="其它前囟状态" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="eye" alias="眼" type="string" length="2" tag="radioGroup"  lb="fsjl" controValue="99" controId="eyeAbnormal"/>
	<item id="eyeAbnormal" alias="眼其他异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="limbs" alias="四肢活动" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="2" controId="limbsAbnormal"/>
	<item id="limbsAbnormal" alias="四肢活动异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="ear" alias="耳" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="y" controId="earAbnormal"/>
	<item id="earAbnormal" alias="耳其他异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="neck" alias="颈部包块" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="y" controId="neck1"/>
	<item id="neck1" alias="颈部包块描述" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="nose" alias="鼻" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="2" controId="noseAbnormal"/>
	<item id="noseAbnormal" alias="鼻异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="skin" alias="皮肤" type="string" length="2" tag="radioGroup"  lb="fsjl" controValue="99" controId="skinAbnormal"/>
	<item id="skinAbnormal" alias="皮肤其它症状" type="string" length="30" tag="text" lb="fsjl"  />
	<item id="mouse" alias="口腔" type="string" length="2" tag="radioGroup"  lb="fsjl" controValue="14" controId="mouseAbnormal"/>
	<item id="mouseAbnormal" alias="口腔其他异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="anal" alias="肛门" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="2" controId="analAbnormal"/>
	<item id="analAbnormal" alias="肛门异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="heartlung" alias="心肺听诊" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="2" controId="heartLungAbnormal"/>
	<item id="heartLungAbnormal" alias="心肺异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="genitalia" alias="外生殖器" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="2" controId="genitaliaAbnormal"/>
	<item id="genitaliaAbnormal" alias="外生殖器异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="abdominal" alias="腹部" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="5" controId="abdominalabnormal"/>
	<item id="abdominalabnormal" alias="腹部其他异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="spine" alias="脊柱" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="2" controId="spineAbnormal"/>
	<item id="spineAbnormal" alias="脊柱异常" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="umbilical" alias="脐带" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="9" controId="umbilicalOther"/>
	<item id="umbilicalOther" alias="脐带其它" type="string" length="30" tag="text"  lb="fsjl" />
	<item id="referral" alias="转诊" type="string" length="1" tag="radioGroup"  lb="fsjl" controValue="y" controId="referralReason,referralUnit"/>
	<item id="referralUnit" alias="转诊机构及科室" type="string" length="50" anchor="100%" width="120" tag="text"  lb="fsjl" />
	<item id="referralReason" alias="转诊原因" type="string" length="50" anchor="100%" tag="text"  lb="fsjl" />	
	<item id="guide" alias="指导" type="string" length="64" tag="checkBox"  lb="fsjl" />
	<item id="visitDate" alias="本次访视日期" type="date" 
		defaultValue="%server.date.today" tag="text"  maxValue="%server.date.today" lb="fsjl" >
	</item>
	<item id="nextVisitAddress" alias="下次随访地点" type="string"
		length="100" width="120" tag="text"   lb="fsjl" />
	<item id="nextVisitDate" alias="下次随访日期" type="date"  minValue="%server.date.today" tag="text"  />
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		 tag="text"   >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	
</entry>