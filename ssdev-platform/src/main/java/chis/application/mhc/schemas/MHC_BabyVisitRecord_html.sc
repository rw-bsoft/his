<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_BabyVisitRecord_html" alias="新生儿访视记录">
	<item id="visitDate" alias="访视日期" type="date"  maxValue="%server.date.today"   defaultValue="%server.date.today">
		
	</item>
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		update="false" fixed="true"   not-null="1" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			  />
	</item>
	<item id="visitUnit" alias="随访单位" type="string" length="20"
		update="false" fixed="true" width="180" colspan="2" anchor="100%"
		defaultValue="%user.manageUnit.id"  >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="temperature" alias="体温(℃)"  length="5"
		precision="2" type="double" display="2"  tag="text" />
		
		
	<item id="weight" alias="目前体重(kg)" type="int" length="5"
		precision="2"  tag="text" />
	<item id="respiratoryFrequency" alias="呼吸频率(次/分)" type="int"
		width="100"  tag="text" />
	<item id="pulse" alias="脉率(次/分)" type="int" width="100"  tag="text" />
	<item id="feedWay" alias="喂养方式" type="string" length="2"
		display="2" tag="radioGroup" >
	</item>
	<item id="eatNum" alias="吃奶量(ml)" type="int" length="20"  tag="text" />
	<item id="eatCount" alias="吃奶次数(次/日)" type="int" length="5"  tag="text" />
	<item id="vomit" alias="呕吐" type="string" length="2"  tag="radioGroup" >
	</item>
	<item id="jaundice" alias="黄疸部位" type="string" length="1" tag="radioGroup"  >
	</item>
	<item id="face" alias="面色" type="string" length="1" tag="radioGroup" controValue="9" controId="faceOther" >
	</item>
	<item id="faceOther" alias="其它面色" type="string" length="30"  tag="text" />
	<item id="bregmaTransverse" alias="前囟纵径(cm)" type="int"
		length="5" precision="2" width="120"  tag="text" />
	<item id="bregmaLongitudinal" alias="前囟横径(cm)" type="int"
		length="5" precision="2" width="120"  tag="text" />
	<item id="bregmaStatus" alias="前囟状态" type="string" length="1" tag="radioGroup" controValue="4" controId="otherStatus" >
  
	</item>
	<item id="otherStatus" alias="其它前囟状态" type="string" length="30"  tag="text" />
   
	<item id="eye" alias="眼" type="string" length="2" tag="radioGroup" controValue="99" controId="eyeAbnormal">
   
	</item>
	<item id="eyeAbnormal" alias="眼其他异常" type="string" length="30"  tag="text" />
	<item id="ear" alias="耳" type="string" length="1" tag="radioGroup" controValue="y" controId="earAbnormal">
   
	</item>
	<item id="earAbnormal" alias="耳其他异常" type="string" length="30"  tag="text" />
	<item id="nose" alias="鼻" type="string" length="1" tag="radioGroup" controValue="2" controId="noseAbnormal">
  
	</item>
	<item id="noseAbnormal" alias="鼻异常" type="string" length="30"  tag="text" />
	<item id="mouse" alias="口腔" type="string" length="2" tag="radioGroup" controValue="14" controId="mouseAbnormal">
  
	</item>
	<item id="mouseAbnormal" alias="口腔其他异常" type="string" length="30"  tag="text" />

	<item id="thrush" alias="鹅口疮" type="string" length="1" tag="radioGroup" >
	</item>
	<item id="heartlung" alias="心肺听诊" type="string" length="1" tag="radioGroup" controValue="2" controId="heartLungAbnormal">
    
	</item>
	<item id="heartLungAbnormal" alias="心肺异常" type="string" length="30"  tag="text" />
	<item id="abdominal" alias="腹部" type="string" length="1" tag="radioGroup" controValue="5" controId="abdominalabnormal">
  
	</item>
	<item id="abdominalabnormal" alias="腹部其他异常" type="string" length="30"  tag="text" />

	<item id="limbs" alias="四肢活动" type="string" length="1" tag="radioGroup" controValue="2" controId="limbsAbnormal">
    
	</item>
	<item id="limbsAbnormal" alias="四肢活动异常" type="string" length="30"  tag="text" />
	<item id="neck" alias="颈部包块" type="string" length="1" tag="radioGroup" controValue="y" controId="neck1">
   
	</item>
	<item id="neck1" alias="颈部包块描述" type="string" length="30"  tag="text" />
	<item id="skin" alias="皮肤" type="string" length="2" tag="radioGroup" controValue="99" controId="skinAbnormal">
    
	</item>
	<item id="skinAbnormal" alias="皮肤其它症状" type="string" length="30"  tag="text" />
	<item id="anal" alias="肛门" type="string" length="1" tag="radioGroup" controValue="2" controId="analAbnormal">
    
	</item>
	<item id="analAbnormal" alias="肛门异常" type="string" length="30"  tag="text" />
	<item id="genitalia" alias="外生殖器" type="string" length="1" tag="radioGroup" controValue="2" controId="genitaliaAbnormal">
		
	</item>
	<item id="genitaliaAbnormal" alias="外生殖器异常" type="string"
		length="30"  tag="text" />
	<item id="spine" alias="脊柱" type="string" length="1" tag="radioGroup" controValue="2" controId="spineAbnormal">
		
	</item>
	<item id="spineAbnormal" alias="脊柱异常" type="string" length="30"  tag="text" />
	<item id="umbilical" alias="脐带" type="string" length="1" tag="radioGroup" controValue="9" controId="umbilicalOther">
		
	</item>
	<item id="umbilicalOther" alias="脐带其它" type="string" length="30"  tag="text" />
	<item id="stoolTimes" alias="大便次数" type="int"  tag="text" />
	<!--
		<item id="stoolDates" alias="/天数" type="int"  tag="text" />
		<item id="stoolColor" alias="大便颜色" type="string" length="20" tag="radioGroup" >
		</item>
			-->
	
	<item id="stoolStatus" alias="大便性状" type="string" length="20" tag="radioGroup" >
	</item>
	<item id="guide" alias="指导" type="string" length="64" tag="checkBox" >
		
	</item>
	<item id="referral" alias="转诊" type="string" length="1" tag="radioGroup" controValue="y" controId="referralReason,referralUnit">
	</item>
	<item id="referralUnit" alias="转诊机构及科室" type="string" length="50" anchor="100%" fixed="true"
		width="120"  tag="text" />
	<item id="referralReason" alias="转诊原因" type="string" length="50" anchor="100%" fixed="true" tag="text" />
	<item id="nextVisitDate" alias="下次随访日期" type="date"  minValue="%server.date.today" tag="text" />
	<item id="nextVisitAddress" alias="下次随访地点" type="string"
		length="100" width="120"  tag="text" />
	<item id="createUnit" alias="录入机构" type="string" length="20"
		width="180" fixed="true" update="false" display="0" 
		defaultValue="%user.manageUnit.id"  >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" display="0"
		update="false" fixed="true" defaultValue="%user.userId"  >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield" update="false" display="0"
		fixed="true" defaultValue="%server.date.today"  >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id"  >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		hidden="true" defaultValue="%user.userId"  >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期"  type="datetime"  xtype="datefield" hidden="true"
		defaultValue="%server.date.today"  >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>