<?xml version="1.0" encoding="UTF-8"?>
<entry entity-name="chis.application.fhr.schemas.EHR_FamilyContractBase"
	alias="家庭签约基本信息" sort="FC_Begin desc,FC_CreateDate desc">
	<item id="FC_Id" alias="主键" pkey="true" type="string" length="32"
		fixed="true" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="32" startPos="1" />
		</key>
	</item>
	<item id="F_Id" alias="家庭编号" type="string" length="32" hidden="true" />
	<item id="FC_Begin" alias="开始日期" not-null="1" type="date" defaultValue="%server.date.date" />
	<item id="FC_End" alias="到期日期" not-null="1" type="date" />
	<item id="FC_Stop_Date" alias="解约日期" type="date" width="110" display="2" />
	<item id="FC_Stop_Reason" alias="解约原因" type="string" width="110" length="60" display="2">
		<dic>
			<item key="1" text="迁出本辖区" />
			<item key="2" text="连续一年无法取得联系" />
			<item key="3" text="连续一年拒绝服务" />
			<item key="4" text="主动解约" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="FC_Sign_Flag" alias="签约状态" type="string" length="1"
		display="1" defaultValue="1">
		<dic>
			<item key="1" text="签约" />
			<item key="2" text="解约" />
		</dic>
	</item>
	<item id="FC_Repre" alias="家庭代表" display="0" type="string" width="110" length="60" fixed="true" >
		<!--<dic id="chis.dictionary.familyMember"/>-->
	</item>
	<item id="FC_RepreName" alias="家庭代表" not-null="1" type="string" width="110" length="60" display="2" fixed="true" >
		<!--<dic id="chis.dictionary.familyMember"/>-->
	</item>
	<item id="FC_SingingType" alias="签约服务类型" type="string" length="1" width="100"  not-null="1" queryable="true">
		<dic>
			<item key="01" text="基本服务组合" />
			<item key="02" text="诊疗优惠组合" />
			<item key="03" text="健康管理组合" />
		</dic>
	</item>
	<item id="FC_FamilyTeamId" alias="签约团队" type="string" width="110" length="60"  colspan="1" display="2" >
		<dic id="chis.dictionary.familyteam" />
	</item>
	<item id="FC_CreateUser" alias="录入人" not-null="1" type="string" length="20" fixed="true"
		display="2" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="villageDoctorId" alias="乡村医生" type="string" width="110" length="60" display="2" >
		<dic id="chis.dictionary.familydoctors" />
	</item>
	<item id="villageUnitId" alias="签约卫生室" type="string" width="110" length="60" display="2" fixed="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="villageDoctorPhone" alias="乡村医生电话" type="string" length="20" colspan="2" />
	
	<item id="hospitalDoctorId" alias="卫生院医生" type="string" width="110" length="60" display="2" >
		<dic id="chis.dictionary.familydoctors" />
	</item>
	<item id="hospitalUnitId" alias="签约卫生院" type="string" width="110" length="60" display="2" fixed="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="hospitalDoctorPhone" alias="医生电话" type="string" length="20" colspan="2" />
	<!--
	<item id="FC_FamilyDoctorId" alias="家庭医生" type="string" width="110" length="60" display="2" >
		<dic id="chis.dictionary.familydoctors" />
	</item>-->
	<item id="FC_Phone" alias="电话" type="string" width="110" length="60" display="0" />
	<item id="FC_Mobile" alias="手机" type="string" width="110" length="60" display="0" />
	<item id="FC_Party" alias="医生签字" not-null="1" type="string" width="110" length="60" display="2" />
	<item id="FC_Party2" alias="居民签字" type="string" width="110" length="60" display="0" />
	<item id="FC_CreateDate" alias="记录日期" not-null="1" type="date" display="2" 
		  defaultValue="%server.date.date" fixed="true" />
	<item id="FC_CreateUnit" alias="录入单位" type="string" length="20" display="0" update="false"
		width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>
