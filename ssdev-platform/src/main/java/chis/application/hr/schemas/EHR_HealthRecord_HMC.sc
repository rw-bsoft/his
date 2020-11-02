<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_HealthRecord" alias="个人健康档案" sort="zlls,a.phrId desc">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="20" width="160" not-null="1" fixed="true" queryable="true" generator="assigned">
		<key>
			<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
			</Rule>
			<Rule name="increaseId" index="1" length="8" startPos="1" seedRel="areaCode" type="increase"/>
		</key>
	</item>
    <item ref="b.definePhrid" display="1" queryable="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item id="checkType" type="string" display="1"  virtual = "true"  alias="是否年检"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="b.contactPhone" display="1" queryable="true"/>
	<item ref="b.registeredPermanent" display="0" queryable="true"/>
	<item ref="b.bloodTypeCode" display="1" queryable="true"/>
	<item ref="b.zlls" display="1" />
	<item id="existHealthCheck" alias="是否有体检表" type="string" length="1" defaultValue="n" queryable="true" display="1" update="false" hidden="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" notDefaultValue="true" hidden="true"/>
	<item id="regionCode" alias="网格地址" type="string" length="25" not-null="1" width="300" colspan="2" anchor="100%"  queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="regionCode_text" alias="网格地址" type="string" length="200" display="0"/>
	<item ref="b.address" display="1"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="true" queryable="true" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" not-null="true" length="20" colspan="2" anchor="100%" width="150"  fixed="true" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="masterFlag" alias="是否户主" type="string"  length="1">
		<dic id="chis.dictionary.yesOrNo" render="Tree"/>
	</item>
	<item id="relaCode" alias="与户主关系" type="string" length="2" >
		<dic id="chis.dictionary.relaCode" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="familyId" alias="所属家庭" type="string" length="30" hidden="true"/>
	<item id="fatherId" alias="父亲编号" type="string" length="32" hidden="true" display="2"/>
	<item id="fatherName" alias="父亲姓名" type="string" length="20" display="2" xtype="lookupfieldex"/>
	<item id="motherId" alias="母亲编号" type="string" length="32" hidden="true" display="2"/>
	<item id="motherName" alias="母亲姓名" type="string" length="20" display="2" xtype="lookupfieldex"/>
	<item id="partnerId" alias="配偶编号" type="string" length="32" hidden="true" display="2"/>
	<item id="partnerName" alias="配偶姓名" type="string" length="20" display="2" xtype="lookupfieldex"/>
	<item id="signFlag" alias="签约标志" type="string" length="1" defaultValue="n" canInput="true" validateOnBlur="false">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="knowFlag" alias="居民知晓标志" type="string" length="1" defaultValue="n" canInput="true" validateOnBlur="false">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="isAgrRegister" alias="是否农业户籍" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="isPovertyAlleviation" alias="是否扶贫对象" type="string" length="1" not-null="1" queryable="true">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="incomeSource" alias="经济来源" type="string" length="10" not-null="1" queryable="true">
		<dic render="LovCombo">
			<item key="1" text="社会救济"/>
			<item key="2" text="工作收入"/>
			<item key="3" text="其他"/>
		</dic>
	</item>
	<item id="deadFlag" alias="死亡标志" type="string" length="1" defaultValue="n" display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="deadDate" alias="死亡日期" type="date" fixed="true" display="2" maxValue="%server.date.today"/>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true" length="100" display="2" />
	<item id="createUnit" alias="建档单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id"  queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" hidden="true"/>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" hidden="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="死亡"/>
			<item key="2" text="迁出"/>
			<item key="3" text="失访"/>
			<item key="4" text="拒绝"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="oldlastVisitDate" alias="老年人最后随访时间" type="date" hidden="true"/>
	<item id="isDiabetes" alias="是否糖尿病" type="string" length="1" defaultValue="n" queryable="true" display="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="isHypertension" alias="是否高血压" type="string" length="1" defaultValue="n" queryable="true" display="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="familyDoctorId" alias="家庭医生" type="string" length="20" queryable="true" update="false" fixed="true">
		<dic id="chis.dictionary.familydoctor" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="sxsj" alias="生效时间" type="date" update="false" fixed="true" />
	<item id="zzsj" alias="终止时间" type="date" update="false" fixed="true" />
	<item id="isOffer" alias="一级审核" type="string" fixed="true"  length="1" width="80" update="false"  queryable="true">
		<dic id="chis.dictionary.yesOrNoOrBack"/>
	</item>
	<item id="offerLastModifyPerson" alias="一级审核人" type="string" fixed="true" width="80"  length="20" queryable="true" update="false" >
	<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="offerLastModifyDate" alias="一级审核日期" type="datetime"  xtype="datefield" width="100"   queryable="true" fixed="true"  update="false"/>
	<item id="unOfferLastModifyPerson" alias="一级取消审核人" type="string"  length="20" width="100"  update="false"  display="1">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="unOfferLastModifyDate" alias="一级取消审核日期" type="datetime"   xtype="datefield"  width="150"   update="false" display="1"/>
	
	<item id="isFirstVerify" alias="二级审核" type="string" fixed="true" length="1" width="80"  update="false"  queryable="true">
		<dic>
			<item key="0" text="未审核"/>
			<item key="1" text="已审核"/>
			<item key="2" text="取消审核"/>
		</dic>
	</item>
	<item id="firstVerifyPerson" alias="二级审核人" type="string" update="false" width="80"  fixed="true" length="20">
	<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="firstVerifyDate" alias="二级审核日期" type="datetime"  xtype="datefield" display="1" fixed="true" width="100" queryable="true"  update="false"/>
	<item id="unFirstVerifyPerson" alias="二级取消审核人" type="string"  length="20" width="100" display="1" update="false">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="unFirstVerifyDate" alias="二级取消审核日期" type="datetime"  xtype="datefield" width="130" display="1"  update="false"/>
	
	<item id="isSecondVerify" alias="三级审核" type="string" length="1" width="80"  update="false" fixed="true"  queryable="true">
		<dic>
			<item key="0" text="未审核"/>
			<item key="1" text="已审核"/>
			<item key="2" text="已开放"/>
			<item key="3" text="取消审核"/>
		</dic>
	</item>
	<item id="secondVerifyPerson" alias="三级审核人" type="string" fixed="true"  length="20" width="80"  update="false" >
	<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="secondVerifyDate" alias="三级审核日期" type="datetime"  xtype="datefield" display="1" fixed="true" width="100" queryable="true"  update="false"/>
	<item id="unSecondVerifyPerson" alias="三级取消审核人" type="string"  length="20" display="1" width="100"  update="false">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="unSecondVerifyDate" alias="三级取消审核日期" type="datetime"  xtype="datefield" width="130" display="1"   update="false"/>
	<!--<item id="isRemarked" alias="是否标记" type="string"  length="1" update="false" defaultValue="0" display="0">
		<dic>
			<item key="0" text="未标记"/>
			<item key="1" text="已标记"/>
		</dic>
	</item>
	<item id="remarkDate" alias="标记日期" type="datetime" width="130" queryable="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="remarkPerson" alias="标记人" type="string" length="20" display="0" defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>-->
	<item id="dataSource" alias="数据来源" type="string" length="1"
		colspan="1" display="0">
		<dic id="chis.dictionary.datasource"/>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
