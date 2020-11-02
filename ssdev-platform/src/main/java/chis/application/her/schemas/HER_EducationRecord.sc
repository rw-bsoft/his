<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.her.schemas.HER_EducationRecord" alias="健康教育记录" sort="a.recordId desc">
	<item id="recordId" alias="编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" width="120" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	
	<item ref="b.executePerson"  display="0" queryable="false"/>
	<item ref="b.executeUnit"  display="0" queryable="false"/>
	<item ref="c.beginDate"  display="0" queryable="false"/>
	<item ref="c.endDate" display="0" queryable="false"/>
	<item ref="c.status" display="0"/>
	
	<item id="setId" alias="计划编号" type="string" length="16" display="0" width="120" />
	<item id="exeId" alias="执行编号" type="string" length="16" display="0" width="120" />
	<item id="activeDate" alias="活动时间" type="date" not-null="1" defaultValue="%server.date.today" maxValue="%server.date.today" queryable="true"/>
	<item id="activePlace" alias="活动地点" type="string" length="100" not-null="1" queryable="true"/>
	<item id="activeForm" alias="活动形式" type="string" length="50" colspan="2" not-null="1" queryable="true"/>
	<item id="activeTheme" alias="活动主题" type="string" length="50" colspan="2" not-null="1" queryable="true" width="120"/>
	<item id="organizer" alias="组织者" type="string" length="20" colspan="2" not-null="1" />
	<item id="personnelCategory" alias="接受教育人员类别" type="string" length="100" not-null="1" width="120"/>
	<item id="population" alias="接受教育人数" type="int"  length="6" not-null="1" width="100"/>
	<item id="materialCategory" alias="资料种类及数量" type="string" length="100" colspan="2" not-null="1" width="120"/>
	<item id="activeContent" alias="活动内容" type="string" length="1000" xtype="textarea" colspan="2" not-null="1" width="120"/>
	<item id="activeValuation" alias="活动总结评价" type="string" length="1000" xtype="textarea" colspan="2" not-null="1" width="120"/>
	<item id="saveMaterial" alias="存档材料" type="string" length="20" not-null="1" width="100">
		<dic render="LovCombo">
			<item key="1" text="书面材料"/>
			<item key="2" text="图片材料"/>
			<item key="3" text="印刷材料"/>
			<item key="4" text="影音材料"/>
			<item key="5" text="签到表"/>
			<item key="6" text="其他材料"/>
		</dic>
	</item>
	
	<item id="manager" alias="负责人" type="string" length="20" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputDate" alias="填表时间" type="date" update="false" defaultValue="%server.date.today" fixed="true" >
		
	</item>
	<item id="inputuser" alias="填表人" type="string" update="false" length="20" queryable="true" defaultValue="%user.userId" not-null="1" fixed="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
	</item>
	
	<item id="inputUnit" alias="填表机构" defaultValue="%user.manageUnit.id" update="false" length="20" fixed="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
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
	
	<relations>
		<relation type="parent" entryName="chis.application.her.schemas.HER_EducationPlanExe"/>
		<relation type="parent" entryName="chis.application.her.schemas.HER_EducationPlanSet"/>
	</relations>
	
</entry>
