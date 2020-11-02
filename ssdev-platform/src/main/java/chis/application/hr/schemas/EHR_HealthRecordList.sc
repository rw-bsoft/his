<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_HealthRecord"   alias="个人健康档案" sort="a.phrId desc">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30"
		width="160" not-null="1" fixed="true" queryable="true" display="0"	generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>

	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="0" queryable="true" />
	<item ref="b.contactPhone" display="0" queryable="true" />
	<item ref="b.registeredPermanent" display="0" queryable="true" />

	<item id="empiId" alias="empiid" type="string" length="32"
		fixed="true" notDefaultValue="true" hidden="true" />
	<item id="regionCode" alias="网格地址" type="string" length="25"
		not-null="1" width="200" colspan="2" anchor="100%" update="false">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<!-- 
			<item id="homeAddress" alias="户籍地址" length="21" display="2"
			colspan="3" not-null="1">
			<dic id="chis.dictionary.areaGrid" minChars="4" render="Tree" onlySelectLeaf="true"
			parentKey="%user.role.regionCode" />
			</item>
		-->
	<item id="familyId" alias="所属家庭" type="string" length="30" display="0"
		hidden="true" />
	<item id="masterFlag" alias="是否户主" type="string" not-null="1"
		width="180" length="1" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="relaCode" alias="与户主关系" type="string" not-null="1" display="0"
		length="2">
		<dic id="chis.dictionary.relaCode" />
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		not-null="1" fixed="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<!-- item id="manaNurse" alias="责任护士" type="string" length="20" display="0">
			<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
				parentKey="%user.manageUnit.id" />
		</item>
		<item id="healthDoctor" alias="公卫医生" type="string" length="20" display="0">
			<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
				parentKey="%user.manageUnit.id" />
		</item-->
	<item id="manaUnitId" alias="管理机构" type="string" length="20"
		colspan="2" anchor="100%" width="180" not-null="1" fixed="true" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="signFlag" alias="签约标志" type="string" length="1" display="0"
		defaultValue="y">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="fatherId" alias="父亲编号" type="string" length="32" 
		display="0" />
	<item id="fatherName" alias="父亲姓名" type="string" length="20"
		display="0" xtype="lookupfieldex" />
	<item id="motherId" alias="母亲编号" type="string" length="32"
		display="0" />
	<item id="motherName" alias="母亲姓名" type="string" length="20"
		display="0" xtype="lookupfieldex" />
	<item id="partnerId" alias="配偶编号" type="string" length="32"
		display="0" />
	<item id="partnerName" alias="配偶姓名" type="string" length="20"
		display="0" xtype="lookupfieldex" />
	<!-- 
			<item id="allergic" alias="过敏物质" type="string" length="500"
			defaultValue="无" display="2" />
		-->
	<item id="isAgrRegister" alias="是否农业户籍" type="string" length="1"
		defaultValue="n" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="deadFlag" alias="死亡标志" type="string" length="1"
		defaultValue="n" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="deadDate" alias="死亡日期" type="date" fixed="true"
		display="0" />
	<item id="createUnit" alias="建档单位" type="string" length="20" display="0" update="false"
		width="180" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20" display="0"
		fixed="true" update="false" defaultValue="%user.userId"
		queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="date" update="false" display="0"
		fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date" display="1"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" hidden="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="未审核" />
		</dic>
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1"
		hidden="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>

	<item id="isDiabetes" alias="是否糖尿病" type="string" length="1"
		defaultValue="n" queryable="true" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="isHypertension" alias="是否高血压" type="string" length="1"
		defaultValue="2" queryable="true" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
	</relations>

</entry>