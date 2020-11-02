<?xml version="1.0" encoding="UTF-8"?>              
<entry entityName="chis.application.cdh.schemas.CDH_ChildVisitRecord" alias="新生儿家庭访视记录">
	<item id="visitId" alias="随访序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="babyId" alias="新生儿编号" type="string" length="16"
		hidden="true" />
	<item id="weightNow" alias="目前体重(kg)" type="bigDecimal" length="5" precision="2" tag="text"  />
	<item id="feedWay" alias="喂养方式" type="string" length="1" tag="radioGroup"  />
	<item id="eatNum" alias="吃奶量(ml)" type="int" length="20" tag="text"  />	
	<item id="eatCount" alias="吃奶次数(次/日)" type="int" length="5" tag="text"  />
	<item id="vomit" alias="呕吐" type="string" length="1" tag="radioGroup"  />
	<item id="stoolStatus" alias="大便性状" type="string" length="1" tag="radioGroup"  />
	<item id="stoolTimes" alias="大便次数" type="int" length="1" tag="text"  />
	<item id="temperature" alias="体温(℃)" type="bigDecimal" length="4" precision="1" tag="text"  />
	<item id="pulse" alias="脉率(次/分)" type="int" length="5" width="100" tag="text"  />
	<item id="respiratoryFrequency" alias="呼吸频率(次/分)" type="int" length="5" width="100" tag="text"  />
	<item id="face" alias="面色" type="string" length="1" tag="radioGroup"  />	
	<item id="faceOther" alias="其它面色" type="string" length="30" tag="text"  />
	<item id="jaundice" alias="黄疸部位" type="string" length="1" tag="radioGroup"  />
	<item id="bregmaTransverse" alias="前囟纵径(cm)" type="bigDecimal"
		length="5" precision="2" width="120" tag="text"  />
	<item id="bregmaLongitudinal" alias="前囟横径(cm)" type="bigDecimal"
		length="5" precision="2" width="120" tag="text"  />
	<item id="bregmaStatus" alias="前囟状态" type="string" length="1" tag="radioGroup"  />
	<item id="otherStatus1" alias="其它前囟状态" type="string" length="30" tag="text"  />
	<item id="eye" alias="眼" type="string" length="2" tag="radioGroup"  />
	<item id="eyeAbnormal" alias="眼其他异常" type="string" length="30" tag="text"  />
	<item id="limbs" alias="四肢活动" type="string" length="1" tag="radioGroup"  />
	<item id="limbsAbnormal" alias="四肢活动异常" type="string" length="30" tag="text"  />
	<item id="ear" alias="耳" type="string" length="1" tag="radioGroup"  />
	<item id="earAbnormal" alias="耳其他异常" type="string" length="30" tag="text"  />
	<item id="neck" alias="颈部包块" type="string" length="1" tag="radioGroup"  />
	<item id="neck1" alias="颈部包块描述" type="string" length="30" tag="text"  />
	<item id="nose" alias="鼻" type="string" length="1" tag="radioGroup"  />
	<item id="noseAbnormal" alias="鼻异常" type="string" length="30" tag="text"  />
	<item id="skin" alias="皮肤" type="string" length="2" tag="radioGroup"  />
	<item id="skinAbnormal" alias="皮肤其它症状" type="string" length="30" tag="text"  />
	<item id="mouse" alias="口腔" type="string" length="2" tag="radioGroup"  />
	<item id="mouseAbnormal" alias="口腔其他异常" type="string" length="30" tag="text"  />
	<item id="anal" alias="肛门" type="string" length="1" tag="radioGroup"  />
	<item id="analAbnormal" alias="肛门异常" type="string" length="30" tag="text"  />
	<item id="heartlung" alias="心肺听诊" type="string" length="1" tag="radioGroup"  />
	<item id="heartLungAbnormal" alias="心肺异常" type="string" length="30" tag="text"  />
	<item id="genitalia" alias="外生殖器" type="string" length="1" tag="radioGroup"  />
	<item id="genitaliaAbnormal" alias="外生殖器异常" type="string" length="30" tag="text"  />
	<item id="abdominal" alias="腹部" type="string" length="1" tag="radioGroup"  />
	<item id="abdominalabnormal" alias="腹部其他异常" type="string" length="30" tag="text"  />
	<item id="spine" alias="脊柱" type="string" length="1" tag="radioGroup"  />
	<item id="spineAbnormal" alias="脊柱异常" type="string" length="30" tag="text"  />
	<item id="umbilical" alias="脐带" type="string" length="1" tag="radioGroup"  />
	<item id="umbilicalOther" alias="脐带其它" type="string" length="30" tag="text"  />
	<item id="referral" alias="转诊" type="string" length="1" tag="radioGroup"  />
	<item id="referralUnit" alias="转诊机构及科室" type="string" length="50" anchor="100%" width="120" tag="text"  />
	<item id="referralReason" alias="转诊原因" type="string" length="50" anchor="100%" tag="text"  />	
	<item id="guide" alias="指导" type="string" length="64" tag="checkBox"  />
	<item id="visitDate" alias="本次访视日期" type="date" 
		defaultValue="%server.date.today" tag="text"  maxValue="%server.date.today">
	</item>
	<item id="nextVisitAddress" alias="下次随访地点" type="string"
		length="100" width="120" tag="text"   />
	<item id="nextVisitDate" alias="下次随访日期" type="date"  minValue="%server.date.today" tag="text"  />
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		tag="text"  >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="visitUnit" alias="随访单位" type="string" length="20"
		update="false" fixed="true" width="180" colspan="2" anchor="100%"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	
</entry>