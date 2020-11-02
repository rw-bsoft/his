<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.her.schemas.HER_EducationPlanExe" alias="健康教育计划执行" sort="a.exeId desc">
	<item id="exeId" alias="执行编号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key> 
	</item>
	<item id="setId" alias="计划编号" type="string" length="16" not-null="1"  width="150" display="0" fixed="true"/>
	
	<item ref="b.planType" display="1" width="120" queryable="false"/>
	<item ref="b.beginDate" fixed="true" queryable="false"/>
	<item ref="b.endDate" fixed="true" queryable="false"/>
	<item ref="b.planContent" display="1"/>
	<item ref="b.planPerson" display="1" queryable="false"/>
	<item ref="b.planDate" display="1" queryable="false"/>
	<item ref="b.status" display="2" fixed="true"/>
	
	<item id="executePerson" alias="计划执行人"  defaultValue="%user.userId" length="100"  width="80" fixed="true" queryable="true">
		<dic id="chis.dictionary.userHER" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="executeUnit" alias="计划执行机构" defaultValue="%user.manageUnit.id" length="20"  width="200" fixed="true" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="planNumber" alias="已执行数" length="20" type="int" virtual="true" display="1"/>
	<item id="createUser" alias="录入员工" length="20" update="false" display="1" hidden="true"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入单位" length="20" update="false"  display="1" hidden="true"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"  display="1" hidden="true"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="0" defaultValue="%user.userId">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="0"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<relations>
		<relation type="parent" entryName="chis.application.her.schemas.HER_EducationPlanSet" />
	</relations>
</entry>
