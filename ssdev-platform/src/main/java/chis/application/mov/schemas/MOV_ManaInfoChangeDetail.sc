<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mov.schemas.MOV_ManaInfoChangeDetail" alias="修改档案管理医生迁移记录明细"
	sort="archiveId desc">
	<item id="detailId" pkey="true" alias="明细ID" type="string"
		width="160" length="16" not-null="1" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="archiveMoveId" alias="记录序号" type="string" length="16"
		width="160" fixed="true"  hidden="true"/>
	<item id="archiveType" alias="档案类别" type="string" length="2" queryable="true" fixed="true">
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
	<item id="archiveId" alias="档案编号" type="string" length="30"   width="130" fixed="true"/>
	<item id="empiId" alias="empiid" type="string" length="32"  hidden="true"/>
	<item id="sourceDoctor" alias="原责任医生" type="string" length="20" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="sourceUnit" alias="原管辖机构" type="string" length="20"
		width="180" not-null="true" colspan="2" queryable="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="targetDoctor" alias="现责任医生" type="string" length="20"  width="180">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"   keyNotUniquely="true"/>
	</item>
	<item id="targetUnit" alias="现管辖机构" type="string" length="20"
		width="180" not-null="true" colspan="2" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" />
	</item>
	<item id="affirmType" alias="确认标志" type="string"  length="1" queryable="true"  defaultValue="n"  fixed="true">
		<dic id="chis.dictionary.yesOrNo"/> 
	</item>
</entry>