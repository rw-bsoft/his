<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.her.schemas.HER_EducationPlanSet" alias="健康教育计划制定" sort="a.setId desc">
	<item id="setId" alias="教育计划编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" width="120" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="planType" alias="计划类型" type="string" length="50" not-null="1" queryable="true" width="150">
		<dic id="chis.dictionary.HealthEducationType" render="LovCombo" />
	</item>
	<item id="beginDate" alias="执行起始时间" type="date" queryable="true" width="120" not-null="1" minValue="%server.date.today"/>
	<item id="endDate" alias="执行终止时间" type="date" queryable="true" not-null="1" width="120" minValue="%server.date.today"/>
	<item id="executePerson" alias="计划执行人" length="1000" colspan="3" width="250" not-null="1">
		<dic id="chis.dictionary.userHER" render="TreeCheck" onlyLeafCheckable="true" maxHeight="400" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="planContent" alias="计划内容" length="1000" xtype="textarea" colspan="3" width="250" not-null="1"/>
	<item id="executeResult" alias="计划执行结果" length="100" colspan="3" width="250" fixed="true"/>
	<item id="planPerson" alias="计划制定人" fixed="true" update="false" type="string" length="20" queryable="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" checkModel="single" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
</item>
	<item id="planDate" alias="计划制定时间" type="datetime"  xtype="datefield" fixed="true" update="false" queryable="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="planUnit" alias="计划制定机构" fixed="true" update="false" queryable="true" defaultValue="%user.manageUnit.id" length="20" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="status" alias="档案状态" type="string" length="2" display="0" defaultValue = "0" >
		<dic id="chis.dictionary.statusPer"/>  
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
