<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.ppvr.schemas.EHR_PoorPeopleVisit" alias="贫困人群随访" sort="a.visitId">
	<item id="visitId" pkey="true" alias="随访编号" type="string" length="16"
		width="160" not-null="1" fixed="true"  display="0"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30"
		hidden="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32"  hidden="true" />
	<item ref="b.personName" display="1"  />
	<item ref="b.sexCode" display="1"  />
	<item ref="b.birthday" display="1"  />
	<item ref="b.idCard" display="1"  />
	<item ref="b.phoneNumber" display="1"  />
	<item ref="c.regionCode" display="1"  />
	<item ref="c.regionCode_text" display="0" />
	<item ref="c.status" display="0" />
	<item ref="c.manaUnitId" display="1" />
	<item ref="c.manaDoctorId" display="1" />
	<item id="visitDate" alias="随访日期" type="date" not-null="1"
		defaultValue="%server.date.today" update="false" maxValue="%server.date.today" />
	<item id="visitUser" alias="随访医生" type="string" length="20" not-null="1" defaultValue="%user.userId" > 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="['substring',['$','%user.manageUnit.id'],[0,12]]"/>
	</item>
	<item id="isWork" alias="是否就业" type="string" length="1"
		not-null="1" >
		<dic id="chis.dictionary.yesOrNo"/>
	</item>	
	<item id="houseHoldIncome" alias="目前家庭收入" type="double" length="10" not-null="1"/>
	<item id="incomeSource" alias="收入来源" type="string" length="10" not-null="1">
		<dic render ="LovCombo">
			<item key="1" text="社会救济" />
			<item key="2" text="工作收入" />
			<item key="3" text="其他" />
		</dic>
	</item>	
	
	<item id="povertyReason" alias="致贫原因" type="string" length="200" />
	<item id="consortHealth" alias="配偶健康状况" type="string" length="1"
		not-null="1" >
		<dic >
			<item key="1" text="好" />
			<item key="2" text="一般，生活能自理" />
			<item key="3" text="差，生活不能自理" />
		</dic>
	</item>	
	<item id="childAmount" alias="子女状况(人)" type="int" length="1" not-null="1"/>
	<item id="oldAmount" alias="负担老人(人)" type="int" length="1" not-null="1"/>
	<item id="oldHealth" alias="老人健康状况" type="string" length="1"
		not-null="1" >
		<dic >
			<item key="1" text="好" />
			<item key="2" text="一般，生活能自理" />
			<item key="3" text="差，生活不能自理" />
		</dic>
	</item>	
	<item id="personalHealth" alias="个人健康状况" type="string" length="1"
		not-null="1">
		<dic >
			<item key="1" text="好" />
			<item key="2" text="一般，生活能自理" />
			<item key="3" text="差，生活不能自理" />
		</dic>
	</item>	
	<item id="isSick" alias="目前是否患病" type="string" length="1"
		not-null="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="diseaseType" alias="患病种类" type="string" length="100" colspan = "3" >
		<dic id="chis.dictionary.diseaseType_PoorVisit" render="LovCombo"/>
	</item>
	<item id="otherDisease" alias="其他病" type="string" length="200" colspan = "3"/>
	<item id="visitInfo" alias="随访记录" type="string" xtype="textarea" length="1000" colspan = "3" not-null="1"/>
	
	<item id="inputUnit" alias="录入机构" type="string" length="20" not-null="1"
		update="false" fixed="true"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputUser" alias="录入医生" type="string" length="20"
		not-null="1" defaultValue="%user.userId" update="false"  fixed="true" display="1"> 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" not-null="1"
		defaultValue="%server.date.today" update="false"   display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.datetime" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>