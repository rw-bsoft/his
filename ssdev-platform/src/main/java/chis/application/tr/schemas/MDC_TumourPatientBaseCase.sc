<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourPatientBaseCase" alias="肿瘤患者首次随访">
	<item id="firstVisitId" alias="首次随访记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="TPRCID" alias="TPRCID" type="string" length="16" display="0"/>
	<item id="censusRegisterCheck" alias="户口地核实" type="string" length="1" not-null="1">
		<dic>
			<item key="0" text="已核实"/>
			<item key="1" text="未核实"/>
		</dic>
	</item>
	<item id="noCheckReason" alias="未核实原因" type="string" length="1" colspan="2">
		<dic>
			<item key="0" text="无此地"/>
			<item key="1" text="无此人"/>
			<item key="2" text="外地"/>
		</dic>
	</item>
	<item id="liveAddressCheck" alias="居住地核实" type="string" length="1" not-null="1"> 
		<dic>
			<item key="0" text="已核实"/>
			<item key="1" text="未核实"/>
		</dic>
	</item>
	<item id="LANocheckReason" alias="居住地未核实原因" type="string" length="1" colspan="2">
		<dic>
			<item key="0" text="未核实"/>
			<item key="1" text="户口空挂"/>
			<item key="2" text="袋袋户口"/>
			<item key="3" text="动迁"/>
			<item key="4" text="拒访"/>
		</dic>
	</item>
	<item id="hight" alias="身高(CM)" type="int" length="3"/>
	<item id="smokingCode" alias="吸烟状况" type="string" length="1">
		<dic id="chis.dictionary.CV5101_24"/>
	</item>
	<item id="passiveSmoking" alias="被动吸烟场所类别" type="string" length="100">
		<dic id="chis.dictionary.CV5101_25" render="LovCombo"/>
	</item>
	<item id="smokingStartAge" alias="吸烟开始年龄" type="int" length="2"/>
	<item id="stopSmokingAge" alias="戒烟年龄" type="int" length="2"/>
	<item id="smokingNumber" alias="日均吸烟数(支)" type="int" length="2"/>
	<item id="tumourFamilyHistory" alias="肿瘤家族史" type="string" length="1" not-null="1">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="relationshipCode" alias="患者与本人关系" type="string" length="20">
		<dic  render="LovCombo">
			<item key="2" text="子"/>
			<item key="3" text="女"/>
			<item key="5" text="父母"/>
			<item key="6" text="外祖父母"/>
			<item key="7" text="兄弟姐妹"/>
		</dic>
	</item>
	<item id="tumourType" alias="肿瘤家族史瘤别" type="string" length="20"/>
	<item id="firstHaveSymptomDate" alias="首次出现症状日期" type="date" not-null="1"/>
	<item id="firstSeeDoctorDate" alias="首次就诊日期" type="date" colspan="2" not-null="1"/>
	<item id="surgery1" alias="手术1" type="string" length="50"/>
	<item id="surgery2" alias="手术2" type="string" length="50"/>
	<item id="surgery3" alias="手术3" type="string" length="50"/>
	<item id="chemotherapy1" alias="化疗1" type="string" length="50"/>
	<item id="chemotherapy2" alias="化疗2" type="string" length="50"/>
	<item id="chemotherapy3" alias="化疗3" type="string" length="50"/>
	<item id="radiotherapy1" alias="放疗1" type="string" length="50"/>
	<item id="radiotherapy2" alias="放疗2" type="string" length="50"/>
	<item id="radiotherapy3" alias="放疗3" type="string" length="50"/>
	<item id="firstSurgeryUnit" alias="首次手术机构" type="string" length="50"/>
	<item id="firstSurgeryDate" alias="首次手术日期" type="date"/>
	<item id="firstSurgeryNature" alias="首次手术性质" type="string" length="1">
		<dic>
			<item key="1" text="根治"/>
			<item key="2" text="姑息"/>
			<item key="3" text="探查"/>
		</dic>
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
