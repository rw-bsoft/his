<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.per.schemas.PER_CheckupRegister" alias="体检登记表" sort="a.checkupNo desc">
	<item id="checkupNo" alias="体检号" type="string" length="16" 	not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="checkupId" alias="体检编号" type="string" length="30" width="160" queryable="true" colspan="2"/>
	
	<item id="phrId" alias="档案号" type="string" length="30" width="160" queryable="true" display="1"/>
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" />
	<item id="name" alias="姓名" type="string" length="40" queryable="true" display="1"/>
	<item id="sex" alias="性别" type="string" length="2" queryable="true" display="1">
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="age" alias="年龄" type="string" length="10" queryable="true" display="1"/>
	<item id="idCard" alias="身份证号" type="string" length="30" queryable="true" display="1" width="140"/>
	<item id="zipCode" alias="邮政编码" type="string" length="6" display="0"/>
	<item id="address" alias="联系地址" type="string" length="100" colspan="2" display="0"/>
	<item id="phoneNumber" alias="联系电话" type="string" length="20" display="0"/>
	<item id="mobileNumber" alias="手机号码" type="string" length="20" queryable="true" display="1"/>
	<item id="birthday" alias="出生日期" type="date" queryable="true" display="1"/>
	<item id="checkupTime" alias="体检时间"  type="date" queryable="true" width="100" defaultValue="%server.date.today"  maxValue="%server.date.today"/>
	
	<item id="totalCheckupDate" alias="总检日期" type="date" queryable="true" fixed="true"/>
	<item id="checkupType" alias="体检名称" type="string" length="20"  queryable="true" not-null="1" colspan="2">
		<dic id="chis.dictionary.perComboList"  filter="['or',['contains',['$','item.properties.manaUnitId'],['substring',['$','%user.manageUnit.id'],0,9]],['eq',['$','item.properties.manaUnitId'],['$','%server.topUnit']]]"/>
	</item>
	<item id="comboName" alias="套餐类型" type="string" fixed="true" not-null="1" width="200" colspan="2">
		<dic id="chis.dictionary.perComboType"/>
	</item>
	<item id="checkupOrganization" alias="体检单位" type="string"  colspan="4" anchor="100%" not-null="1" width="150"
		length="30" fixed="true" defaultValue="['if',['ge',['len',['$','%user.manageUnit.id']],['i',9]],['substring',['$','%user.manageUnit.id'],0,9],['s','']]">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" parentKey="%user.manageUnit.id"/>	
	</item>
	
	<item id="checkupExce" alias="异常综述"  colspan="2" anchor="100%"  xtype="textarea" length="1000" display="2" fixed="true"/>
	<item id="checkupOutcome" alias="结果综述"  colspan="2" anchor="100%" xtype="textarea" length="1000" display="2"/>
	<item id="checkupAdvice" alias="结果建议" colspan="4" anchor="100%"  type="string" length="1000" />

	<!-- 
		<item id="checkupGuide" alias="体检指导" colspan="2" anchor="100%"  length="10" display="2">
			<dic render="LovCombo">  
				<item key="1" text="定期随访 " />
				<item key="2" text="纳入慢性病患者健康管理 " />
				<item key="3" text="建议复查 " />
				<item key="4" text="建议转诊" />
			</dic>
		</item>
		<item id="checkupAdvice" alias="结果建议" colspan="2" anchor="100%"  type="string" length="1000" display="2"/>
		<item id="checkupAllergy" alias="过敏史"  anchor="100%"  length="200" display="2"/>
		<item id="pastHistory" alias="既往病史"  anchor="100%"  length="200" display="2"/>
		<item id="jobMajor" alias="具体职业"  anchor="100%" type="string" length="200" display="2"/>
		<item id="jobYear" alias="从业年数"  anchor="100%" type="int" display="2"/>

		<item id="checkupSymptom" alias="症状" colspan="2" anchor="100%"  length="100" display="2">
			<dic id="chis.dictionary.symptomCode" render="LovCombo"/>
		</item>
		<item id="symptomOther" alias="其他症状" anchor="100%"  length="100" display="2" fixed="true"/>

		<item id="checkupDoctor" alias="体检医生" type="string"
			length="50" defaultValue="%user.userId">
			<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  parentKey="['substring',['$','%user.manageUnit.id'],0,9]" />
		</item>
		-->
	<item id="status" alias="档案状态" type="string" length="2" display="0" defaultValue = "0" >
		<dic render="Simple">  
			<item key="0" text="正常" />
			<item key="1" text="作废" />
		</dic>
	</item>
	<item id="fromHis" alias="数据来源" type="string" display="0" defaultValue="0">
		<dic>
			<item  key="0" text="系统录入"></item>
			<item  key="1" text="HIS"></item>
		</dic>
	</item>
	<item id="inputUser" alias="录入人员" type="string" length="20" display="0"
		fixed="true" defaultValue="%user.userId" update="false">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
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
