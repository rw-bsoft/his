<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="chis.application.fhr.schemas.PUB_VisitPlanFamilyList" alias="随访计划" sort="beginDate" >
	<item id="planId" pkey="true" alias="计划识别" type="string" length="16" not-null="1" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="sn" alias="序号" type="int" display="0"/>
	<item id="businessType" alias="类别" type="string" length="2" display="1">
		<dic id="chis.dictionary.planType"/>
	</item>
	<item id="planStatus" alias="完成" type="string" length="1" default="0" display="1">
		<dic>
			<item key="0" text="应访"/>
			<item key="1" text="已访"/>
			<item key="2" text="失访"/>
			<item key="3" text="未访"/>
			<item key="4" text="过访"/>
			<item key="8" text="结案"/>
			<item key="9" text="档案注销"/>
		</dic>
	</item>
	<item id="planDate" alias="随访日期" type="date" display="1"/>
	<item id="visitDate" alias="实际随访日期" type="date" display="1" width="100"/>
	<item id="beginDate" alias="开始日期" type="date" display="1" queryable="true"/>
	<item id="endDate" alias="结束日期" type="date" display="1"/>
  
	<item id="beginVisitDate" alias="提醒日期" type="date" display="0"/>
	<item id="extend1" alias="扩展字段1" type="int" display="0" width="50"/>
	<item id="visitId" alias="随访记录ID" type="string" length="16" hidden="true"/>
	<item id="personName" alias="姓名" type="string" length="20" display="1" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40" display="1" >
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" width="75" display="1" />
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" display="1" />
	<item id="groupCode" alias="分组组别" type="string" length="2" hidden="true">
		<dic>
			<item key="01" text="一组"/>
			<item key="02" text="二组"/>
			<item key="03" text="三组"/>
			<item key="99" text="一般管理"/>
		</dic>
	</item>
	<item id="fixGroupDate" alias="转组日期" type="date" hidden="true"/>
	<item id="recordId" alias="档案编号" type="string" length="30" hidden="true"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
	<item id="remark" alias="备注" type="string" length="500" display="0"/>
	<item id="extend2" alias="扩展字段2" type="string" length="60" display="0"/>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="taskDoctorId" alias="工作计划列表医生" type="string" length="20"  display="0">
		<dic id = "chis.dictionary.user" />
	</item>
</entry>
