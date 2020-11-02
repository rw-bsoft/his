<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pub.schemas.PUB_Log" alias="日志表" sort="createTime desc">
	<item id="recordId" alias="日志序号" type="string" length="16"
		width="160" generator="assigned" pkey="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="operType" alias="操作类型" type="string" colspan="3"
		width="300" anchor="100%" length="1">
		<dic>
			<item key="1" text="注销" />
			<item key="2" text="撤销" />
			<item key="3" text="伪造证件" />
		</dic>
	</item>
	<item id="recordType" alias="档案类型" type="string" length="2" >
		<dic>
			<item key="0" text="个人基本信息" />
			<item key="1" text="个人健康档案" />
			<item key="2" text="家庭档案" />
			<item key="3" text="高血压档案" />
			<item key="4" text="糖尿病档案" />
			<item key="5" text="肿瘤档案" />
			<item key="6" text="老年人档案档案" />
			<item key="7" text="儿童档案" />
			<item key="8" text="体弱儿档案" />
			<item key="9" text="孕妇档案" />
			<item key="10" text="体检记录" />
			<item key="11" text="精神病档案" />
		</dic>
	</item>
	
	<item id="phrId" alias="档案ID" type="string" length="30" />
	<item id="empiId" alias="EMPIID" type="string" length="32" />
	<item id="personName" alias="姓名" type="string" length="16" />
	<item id="idCard" alias="身份证号" type="string" length="20" />
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" />
	<item id="regionCode" alias="网格地址" display="0" type="string" length="30" width="260" >
		<dic id="areaGrid" minChars="4" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="regionCode_text" alias="网格地址" type="string" length="30" display="0"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" />
	<item id="description" alias="描述" type="string" xtype="textarea"
		colspan="3" width="300" anchor="100%" length="2000" />
	<item id="createUnit" alias="录入单位" type="string" length="20"
		width="180" fixed="true" update="false"
		defaultValue="%user.manageUnit.id">
		<dic id="manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createTime" alias="录入日期" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.date" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
