<?xml version="1.0" encoding="UTF-8"?>
<entry tableName="SCH_SchistospmaVisit" alias="血吸虫病随访记录" sort="visitDate">
	<item id="schisVisitId" alias="随访记录号" type="string" length="16" width="150" not-null="1" generator="assigned" pkey="true" hidden="true"/>
	<item id="schisRecordId" alias="档案号" type="string" length="16" hidden="true" />
  
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true" />
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		not-null="1" defaultValue="%user.manageUnit.id" hidden="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="touchAgain" alias="再次接触疫水" type="string" length="1" hidden="true" >
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="lastTherapyDate" alias="末次治疗时间" type="date" hidden="true" />
	<item id="therapyAndMedicine" alias="治疗方法和用药情况" length="200" hidden="true" />
	<item id="personState" alias="现个人情况" type="string" length="1" hidden="true" >
		<dic>
			<item key="1" text="好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
			<item key="4" text="生活能自理" />
			<item key="5" text="生活不能自理" />
		</dic>
	</item>
	<item id="symptom" alias="现有伴发症和体征" length="20" hidden="true" >
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
	<item id="illnessOther" alias="现是否患有其他疾病" type="string" length="1" hidden="true" >
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="illnessName" alias="疾病名称" length="20" hidden="true" >
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
	<item id="visitRecord" alias="随访记录" length="200" hidden="true" />
	<item id="visitDoctor" alias="随访医生" length="20" hidden="true" >
		<dic id="chis.dictionary.user16" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitDate" alias="随访时间" type="date" width="140"/>
	<item id="inputUnit" alias="录入单位" length="20" hidden="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="date" hidden="true">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="inputUser" alias="录入人" length="20" hidden="true" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改时间" type="datetime"  xtype="datefield" hidden="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" hidden="true"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<!-- 
		  <relations>
				<relation type="parent" entryName="SCH_SchistospmaRecord">
					<join parent="empiId" child="empiId" />
				</relation>
		  </relations>
		  -->
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
