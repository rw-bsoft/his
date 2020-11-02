<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.per.schemas.PER_CheckupRegister" alias="体检登记表"
	sort="a.checkupNo desc">
	<item id="checkupNo" alias="体检号" type="string" 
		not-null="1" generator="assigned" pkey="true" queryable="true"
		width="160">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="checkupId" alias="体检编号" type="string" length="30"
		width="160" queryable="true" />
	<item id="checkupTime" alias="体检时间" type="date" queryable="true"
		width="100" defaultValue="%server.date.today" />
	<item id="phrId" alias="档案号" type="string" length="30"
		width="160" queryable="true" display="1" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" />
	<item id="name" alias="姓名" type="string" length="40"
		queryable="true" display="1" />
	<item id="sex" alias="性别" type="string" length="2" queryable="true"
		display="1">
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="age" alias="年龄" type="string" length="10" queryable="true"
		display="1" />
	<item id="idCard" alias="身份证号" type="string" length="30"
		queryable="true" display="1" />
	<item id="zipCode" alias="邮政编码" type="string" length="6"
		display="0" />
	<item id="address" alias="联系地址" type="string" length="100"
		colspan="2" display="0" />
	<item id="phoneNumber" alias="联系电话" type="string" length="20"
		display="0" />
	<item id="mobileNumber" alias="手机号码" type="string" length="20"
		queryable="true" display="1" />
	<item id="birthday" alias="出生日期" type="date" queryable="true"
		display="1" />
	<item id="checkupType" alias="体检名称" type="string" length="20"
		update="false" queryable="true" >
		<dic id="chis.dictionary.perComboType" />
	</item>
	<item id="checkupName" alias="体检名称" type="string"  width="200">
	</item>
	<item id="checkupOrganization" alias="体检单位" type="string"
		colspan="2" anchor="100%" length="30" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree"/>
	</item>
	<item id="totalCheckupDate" alias="总检日期" type="date"
		queryable="true"  />
	<item id="checkupExce" alias="体检异常综述" colspan="4" anchor="100%"
		xtype="textarea" type="string" length="1000" display="2"  />
	<item id="checkupOutcome" alias="体检结果综述" colspan="2" anchor="100%"
		xtype="textarea" type="string" length="1000"  />
	<item id="checkupAdvice" alias="体检结果建议" colspan="2" anchor="100%"
		type="string" length="1000" xtype="textarea" display="2" />
	<item id="checkupGuide" alias="体检指导" colspan="1" anchor="100%"  length="10" display="2">
		<dic >  
			<item key="0" text="定期随访 " />
			<item key="1" text="纳入慢性病患者健康管理 " />
			<item key="2" text="建议复查 " />
			<item key="3" text="建议转诊" />
		</dic>
	</item>
	<item id="checkupAllergy" alias="过敏史" colspan="3" anchor="100%"  length="200" display="2"/>
	<item id="pastHistory" alias="既往史" colspan="2" anchor="100%"  length="200" display="2"/>
	<item id="checkupSymptom" alias="症状" colspan="2" anchor="100%"  length="100" display="2"/>
	<item id="symptomOther" alias="其他症状" colspan="2" anchor="100%"  length="100" display="2" fixed="true"/>
	<item id="status" alias="状态标志" type="string" length="2"
		display="0">
		<dic render="Simple">
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="fromHis" alias="数据来源" type="int" display="0">
		<dic render="Simple">
			<item  key="0" text="系统录入"></item>
			<item key="1" text="His" />
		</dic>
	</item>
	<item id="inputUser" alias="录入人员" type="string" length="20" display="0"
		fixed="true" defaultValue="%user.userId" update="false">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
