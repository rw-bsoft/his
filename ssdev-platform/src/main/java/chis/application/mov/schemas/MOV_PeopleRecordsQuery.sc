<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_PeopleRecordsQuery"   alias="修改管理医生子档查询页面"
	sort="a.createDate desc">
	<item id="recordType" alias="档案类型" type="int" length="1" pkey="true" virtual="true">
		<dic>
			<item key="1" text="个人健康档案" />
			<item key="3" text="高血压档案" />
			<item key="4" text="糖尿病档案" />
			<item key="5" text="儿童档案档案" />
			<item key="6" text="孕产妇档案" />
			<item key="7" text="精神病档案" />
			<item key="8" text="肿瘤病例档案" />
			<item key="9" text="老年人档案" />
			<item key="10" text="肢体残疾档案" />
			<item key="11" text="脑瘫残疾档案" />
			<item key="12" text="智力残疾档案" />
			<item key="13" text="离休干部档案" />
			<item key="14" text="肿瘤高危档案" />
		</dic>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32"
		fixed="true" virtual="true" hidden="true" />
	<item id="recordId" alias="档案编号" type="string" length="16" 
		width="160" not-null="1" virtual="true" hidden="true" />
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" virtual="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="未审核" />
		</dic>
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		not-null="1" display="1" fixed="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="manaUnitId" alias="管理机构" type="string" length="20"
		colspan="2" anchor="100%" width="180" not-null="1" fixed="true"
		virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="createUnit" alias="建档机构" type="string" length="20"
		width="180" fixed="true" update="false"
		defaultValue="%user.manageUnit.id" virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId"
		virtual="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="createDate" alias="建档日期" type="date" update="false"
		fixed="true" defaultValue="%server.date.today" virtual="true" >
		<set type="exp">['$','%server.date.today']</set>
	</item>
</entry>
