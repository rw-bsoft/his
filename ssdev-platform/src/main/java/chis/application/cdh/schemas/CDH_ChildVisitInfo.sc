<?xml version="1.0" encoding="UTF-8"?>              
<entry entityName="chis.application.cdh.schemas.CDH_ChildVisitInfo" alias="新生儿家庭访视基本信息">
	<item id="babyId" alias="序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="babyName" alias="婴儿姓名" type="string" length="30"  hidden="true"/>
	<item id="babySex" alias="性别"   type="string" length="10" width="540" not-null="1"  tag="radioGroup" colspan="3"  />
	<item id="babyBirth" alias="出生日期"   type="date" width="150"   not-null="1"  tag="text" maxValue="%server.date.today" />
	<item id="babyIdCard" alias="身份证号" type="string" length="18" width="135"   vtype="idCard"   enableKeyEvents="true"  tag="text" />
	<item id="certificateNo" alias="出生证号" type="string" length="16" tag="text"  />
	<item id="babyAddress" alias="家庭住址" type="string" length="100" width="394" tag="text"  />
	<item id="fatherName" alias="父亲姓名" type="string" length="32" tag="text"  />
	<item id="fatherJob" alias="父亲的职业" type="string" length="20"    tag="text"  >
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
	</item>
	<item id="fatherPhone" alias="父亲电话" type="string" length="25" width="90"   tag="text" />
	<item id="fatherBirth" alias="父亲出生日期"   type="date" width="150"     tag="text" maxValue="%server.date.today" />
	 <item id="fatherEmpiId" type="string" display="0"/>
	<item id="motherName" alias="母亲姓名" type="string" length="32" tag="text"  />
	<item id="motherJob" alias="母亲的职业" type="string" length="20"     tag="text" >
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
	</item>
	<item id="motherPhone" alias="母亲电话" type="string" length="25" width="90"   tag="text" />
	<item id="motherBirth" alias="母亲出生日期"   type="date" width="150"     tag="text"  maxValue="%server.date.today"/>
	<item id="motherEmpiId" type="string" display="0"/>
	<item id="gestation" alias="出生孕周" type="string" length="32" tag="text"  />
	<item id="pregnancyDisease" alias="母亲妊娠期患病情况" type="string" length="1" tag="radioGroup"  />
	<item id="otherDisease" alias="妊娠期其他疾病" type="string" length="30" tag="text"  /> 
	<item id="deliveryUnit" alias="助产机构名称" type="string" length="70"
		width="150" tag="text"  />
	<item id="empiId" type="string" length="32" display="0"/>	
	<item id="birthStatus" alias="出生情况" type="string" length="2" tag="checkBox"  />
	<item id="otherStatus" alias="其它出生情况" type="string" length="30" tag="text"  />
	<item id="asphyxia" alias="新生儿窒息" type="string" length="2" tag="radioGroup"  />
	<item id="apgar1" alias="Apgar评分1" type="string" length="10" />
	<item id="apgar5" alias="Apgar评分5" type="string" length="10" />
	
	<item id="apgarNew" alias="Apgar评分" type="string" length="1" tag="radioGroup" lb="bqf"/>
	
	<item id="malforMation" alias="是否有畸型" type="string" length="2" tag="radioGroup"  />
	<item id="malforMationDescription" alias="畸形描述" type="string"
		length="30"  tag="text" />
	<item id="hearingTest" alias="新生儿听力筛查" type="string" length="1" tag="radioGroup"  />
	<item id="illnessScreening" alias="新生儿疾病筛查" type="string" length="1" tag="radioGroup"  />
	<item id="otherIllness" alias="其他遗传代谢病" type="string" length="50" width="120" tag="text"  />
	<item id="weight" alias="出生体重(kg)" type="bigDecimal" length="5"
		precision="2" width="120" tag="text"  />
	<item id="length" alias="出生身长(cm)" type="bigDecimal" length="5"
		precision="2" width="120" tag="text"  />
	<item id="inputUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="120"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入员工" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期"  type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId"  >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"   />
	</item>
	
</entry>