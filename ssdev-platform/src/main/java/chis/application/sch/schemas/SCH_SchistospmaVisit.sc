<?xml version="1.0" encoding="UTF-8"?>
<entry alias="血吸虫病随访记录" sort="schisVisitId desc">
	<item id="schisVisitId" alias="随访记录号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="schisRecordId" alias="档案号" type="string" length="16" hidden="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
  
	<item ref="c.status" display="0" />
	<item ref="c.closeFlag" display="0" />
  
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		queryable="true" not-null="1" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
  
	<item id="visitDate" alias="随访时间" type="date" defaultValue="%server.date.today" not-null="1" maxValue="%server.date.today"/>
  
	<item id="lastTherapyDate" alias="末次治疗时间" type="date" maxValue="%server.date.today" defaultValue="%server.date.today"/>
	<item id="personState" alias="现个人情况" type="string" length="1" defaultValue="1">
		<dic>
			<item key="1" text="好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
			<item key="4" text="生活能自理" />
			<item key="5" text="生活不能自理" />
		</dic>
	</item>
	<item id="therapyAndMedicine" alias="治疗和用药" length="200"/>
	<item id="symptom" alias="伴发症和体征" length="20" colspan="2" not-null="1">
		<dic render="LovCombo">
			<item key="1" text="浮肿" />
			<item key="2" text="黄疸" />
			<item key="3" text="腹水" />
			<item key="4" text="肝区疼痛" />
			<item key="5" text="肝脾肿大" />
			<item key="6" text="贫血" />
			<item key="9" text="其它" />
		</dic>
	</item>
	<item id="visitRecord" alias="随访记录" length="200" xtype="textarea" colspan="2" not-null="1"/>
	<item id="illnessName" alias="疾病名称" length="20" colspan="2">
		<dic render="LovCombo">
			<item key="1" text="高血压" />
			<item key="2" text="糖尿病" />
			<item key="3" text="冠心病" />
			<item key="4" text="肿瘤" />
			<item key="5" text="慢支炎" />
			<item key="6" text="关节炎" />
			<item key="7" text="脑卒中" />
			<item key="9" text="其它" />
		</dic>
	</item>
	<item id="illnessOther" alias="是否其他疾病" type="string" length="1" defaultValue="2" not-null="1">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="visitDoctor" alias="随访医生" length="20" defaultValue="%user.userId" queryable="true" not-null="1">
		<dic id="chis.dictionary.user16" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitUnit" alias="录入机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="touchAgain" alias="再次接触疫水" type="string" length="1" defaultValue="2" not-null="1">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
  
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"
		defaultValue="%server.date.today" fixed="true" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUnit" alias="录入单位" length="20" colspan="1" update="false" defaultValue="%user.manageUnit.id"
		fixed="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入者" type="string" length="20"
		update="false" defaultValue="%user.userId" fixed="true" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
  
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.sch.schemas.SCH_SchistospmaRecord">
			<join parent="empiId" child="empiId" />
		</relation>
		<relation type="parent" entryName="chis.application.pub.schemas.PUB_VisitPlan">
			<join parent="recordId" child="phrId" />
			<join parent="visitId" child="visitId" />
		</relation>
	</relations>
</entry>
