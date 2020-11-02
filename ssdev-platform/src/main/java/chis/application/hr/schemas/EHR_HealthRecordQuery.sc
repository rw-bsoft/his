<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_HealthRecordQuery" tableName="chis.application.hr.schemas.EHR_HealthRecord" alias="个人健康档案查询" sort="a.phrId desc">
	<!--<item id="phrId" pkey="true" alias="档案编号" type="string" length="30"-->
		<!--width="160"   queryable="true" hidden="true"	generator="assigned">-->
		<!--<key>-->
			<!--<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>-->
		<!--</key>-->
	<!--</item>-->

	<item ref="b.personName" not-null="0" queryable="true" />
	<item ref="b.sexCode" not-null="0" queryable="true" />
	<item ref="b.birthday" not-null="0" queryable="true" />
	<item ref="b.idCard" queryable="true" />
	<item id="regionCode" alias="网格地址" type="string" length="25"
		  width="200" colspan="2" anchor="100%" update="false">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			 parentKey="%user.manageUnit.id" />
	</item>
	<item id="manaUnitId" alias="管理机构" type="string" length="20"
		  colspan="2" anchor="100%" width="180"   queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			 parentKey="%user.manageUnit.id" />
	</item>
	<!--<item ref="b.mobileNumber" hidden="true" queryable="true" />-->
	<!--<item ref="b.contactPhone" hidden="true" queryable="true" />-->
	<!--<item ref="b.registeredPermanent" hidden="true" queryable="true" />-->

	<!-- 
			<item id="homeAddress" alias="户籍地址" length="21" display="2"
			colspan="3" >
			<dic id="chis.dictionary.areaGrid" minChars="4" render="Tree" onlySelectLeaf="true"
			parentKey="%user.role.regionCode" />
			</item>
		-->
	<item id="familyId" alias="家庭编号" type="string" length="30" queryable="true"/>
	<!--<item id="masterFlag" alias="是否户主" type="string" -->
		<!--width="180" length="1" hidden="true">-->
		<!--<dic id="chis.dictionary.yesOrNo"/>-->
	<!--</item>-->
	<!--<item id="relaCode" alias="与户主关系" type="string"  hidden="true"-->
		<!--length="2">-->
		<!--<dic id="chis.dictionary.relaCode" />-->
	<!--</item>-->
	<!-- item id="manaNurse" alias="责任护士" type="string" length="20" hidden="true">
			<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
				parentKey="%user.manageUnit.id" />
		</item>
		<item id="healthDoctor" alias="公卫医生" type="string" length="20" hidden="true">
			<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
				parentKey="%user.manageUnit.id" />
		</item-->
	<!--<item id="signFlag" alias="签约标志" type="string" length="1" hidden="true">-->
		<!--<dic id="chis.dictionary.yesOrNo"/>-->
	<!--</item>-->
	<!--<item id="fatherId" alias="父亲编号" type="string" length="32"-->
	<!--hidden="true" />-->
	<!--<item id="fatherName" alias="父亲姓名" type="string" length="20"-->
		  <!--hidden="true" xtype="lookupfieldex" />-->
	<!--<item id="motherId" alias="母亲编号" type="string" length="32"-->
		  <!--hidden="true" />-->
	<!--<item id="motherName" alias="母亲姓名" type="string" length="20"-->
		  <!--hidden="true" xtype="lookupfieldex" />-->
	<!--<item id="partnerId" alias="配偶编号" type="string" length="32"-->
		  <!--hidden="true" />-->
	<!--<item id="partnerName" alias="配偶姓名" type="string" length="20"-->
		  <!--hidden="true" xtype="lookupfieldex" />-->
	<!-- 
			<item id="allergic" alias="过敏物质" type="string" length="500"
			display="2" />
		-->
	<!--<item id="isAgrRegister" alias="是否农业户籍" type="string" length="1"-->
		<!--hidden="true">-->
		<!--<dic id="chis.dictionary.yesOrNo"/>-->
	<!--</item>-->
	<!--<item id="recordSource" alias="档案来源"  update="false" type="string" length="1">-->
		<!--<dic>-->
			<!--<item key="1" text="门诊诊疗"/>-->
			<!--<item key="2" text="健康档案"/>-->
			<!--<item key="3" text="移动建档"/>-->
			<!--<item key="4" text="妇幼建档"/>-->
		<!--</dic>-->
	<!--</item>-->
	<!--<item id="deadFlag" alias="死亡标志" type="string" length="1"-->
		<!--hidden="true">-->
		<!--<dic id="chis.dictionary.yesOrNo"/>-->
	<!--</item>-->
	<!--<item id="deadDate" alias="死亡日期" type="date" -->
		<!--hidden="true" />-->
	<!--<item id="status" alias="档案状态" type="string" length="1"-->
		<!--hidden="true">-->
		<!--<dic>-->
			<!--<item key="0" text="正常" />-->
			<!--<item key="1" text="已注销" />-->
			<!--<item key="2" text="未审核" />-->
		<!--</dic>-->
	<!--</item>-->
	<!--<item id="cancellationReason" alias="注销原因" type="string" length="1"-->
		<!--hidden="true">-->
		<!--<dic>-->
			<!--<item key="1" text="死亡" />-->
			<!--<item key="2" text="迁出" />-->
			<!--<item key="3" text="失访" />-->
			<!--<item key="4" text="拒绝" />-->
			<!--<item key="9" text="其他" />-->
		<!--</dic>-->
	<!--</item>-->

	<!--<item id="isDiabetes" alias="是否糖尿病" type="string" length="1"-->
		<!--queryable="true" hidden="true">-->
		<!--<dic id="chis.dictionary.yesOrNo"/>-->
	<!--</item>-->
	<!--<item id="isHypertension" alias="是否高血压" type="string" length="1"-->
		<!--queryable="true" hidden="true">-->
		<!--<dic id="chis.dictionary.yesOrNo"/>-->
	<!--</item>-->
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
	</relations>

</entry>