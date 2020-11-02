<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.cvm.schemas.CVM_CallVisitManage" alias="来电来访管理">
	<item id="recordId" alias="recordId" type="string" length="16" pkey="true"
		width="160" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<!--<item id="empiId" alias="empiId" display="0" length="32"/>
		<item id="b.personName" queryable="true" fixed="true" />
		<item id="b.sexCode" queryable="true" fixed="true" />
		<item id="b.birthday" queryable="true" fixed="true" />
		<item id="b.idCard" queryable="true" fixed="true" />
		<item id="b.mobileNumber" queryable="true" fixed="true" />
		<item id="b.address" display="2" colspan="3" fixed="true"/> -->
	<item id="healthNo" alias="健康卡号" type="string" length="30"/>
	<item id="idCard" alias="身份证号" type="string" length="20" width="160" queryable="true" vtype="idCard" enableKeyEvents="true"/>
	<item id="personName" alias="姓名" type="string" fixed="true" length="20" queryable="true" not-null="1"/>
	<item id="sexCode" alias="性别" type="string" fixed="true" length="1" width="40" queryable="true" not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" fixed="true" width="75" queryable="true" not-null="1" maxValue="%server.date.today"/>
	<item id="mobileNumber" alias="联系电话" type="string" length="20" not-null="1" width="90"/>
	<item id="address" alias="联系地址" type="string" length="100" width="200" colspan="2"/>
	<item id="callDate" alias="来电来访时间"  type="date" not-null="1" queryable="true"
		defaultValue="%server.date.today" />
	<item id="fd" alias="家庭医生" width="100" queryable="true" update="false" length="200" not-null="1" type="string">
		<dic id="gp.dictionary.relevanceDoctor" filter="['eq',['$','item.properties.fda'],['$','%user.userId']]"/>
	</item>
	<item id="ToS" alias="服务类型" length="1" not-null="1" update="false" defaultValue="1">
		<dic>
			<item key="1" text="意见反馈" />
			<item key="2" text="门诊预约" />
			<item key="3" text="出诊预约" />
			<item key="4" text="随访预约" />
			<item key="5" text="家床预约" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" fixed="true" update="false"
		defaultValue="%user.userId">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="describe" alias="请求内容" xtype="textarea" length="200" colspan="3"/>
	<!--<item id="dispose" alias="处理结果" length="1" not-null="1" anchor="33%" colspan="3" defaultValue="0">
			<dic id="gp.dictionary.ifDispose"/>
		</item>-->
	<item id="disposeResult" alias="处理结果" xtype="textarea" length="200" colspan="3"/>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" display="1" defaultValue="%user.userId">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" width="120"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="gp.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改时间"  type="datetime"  xtype="datefield"  fixed="true"
		display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false"
		fixed="true" display="1" defaultValue="%user.manageUnit.id" width="120">
		<dic id="gp.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield"  fixed="true" update="false"
		display="1" defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="status" alias="状态" type="string" length="1" defaultValue="0" display="0"/>
	<item id="YYID" alias="预约主键" type="string" length="16" display="0"/>
</entry>
