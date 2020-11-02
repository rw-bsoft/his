<?xml version="1.0" encoding="UTF-8"?>
<entry  tableName="SCH_SchistospmaRecord" alias="血吸虫病记录" sort="createDate, schisRecordId">
	<item id="schisRecordId" alias="序号" type="string" length="16" width="150" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	
	<item id="phrId" alias="档案号" type="string" length="30" hidden="true"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
  
	<item id="checkDate" alias="检查日期" type="date" hidden="true"/>
  
	<!--
		  <item id="regionCode" alias="网格地址" type="string" length="25"
				not-null="1" width="200" colspan="2" anchor="100%" update="false"  hidden="true">
				<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" minChars="4"
					filterMax="18" render="Tree" onlySelectLeaf="true" />
		  </item>
		  -->
  
	<item id="sickenDate" alias="患病时间" type="date" defaultValue="%server.date.today"/>
  
	<item id="bloodCheckType" alias="血检方式" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="血凝" />
			<item key="2" text="酶标" />
		</dic>
	</item>
  
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		colspan="2" anchor="100%" width="180" not-null="1" fixed="true"  hidden="true"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree"
			parentKey="%user.manageUnit.id" rootVisible="false" />
	</item>
  
	<item id="bloodCheckResult" alias="血检结果" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="阴性" />
			<item key="2" text="阳性" />
		</dic>
	</item>
	<item id="checkType" alias="检查方式" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="粪检" />
			<item key="2" text="血检" />
			<item key="9" text="其它" />
		</dic>
	</item>
	<item id="shitCheckResult" alias="粪检结果" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="阴性" />
			<item key="2" text="阳性" />
		</dic>
	</item>
	<item id="illnessType" alias="病种" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="急性血吸虫病" />
			<item key="2" text="慢性血吸虫病" />
			<item key="3" text="晚性血吸虫病" />
			<item key="4" text="疑似感染" />
		</dic>
	</item>
	<item id="findNew" alias="是否新发现" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="therapy" alias="是否治疗" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
  
	<item id="closeFlag" alias="结案标识" type="string" length="1" width="60"
		display="1" defaultValue="0">
		<dic>
			<item key="0" text="未结案" />
			<item key="1" text="已结案" />
		</dic>
	</item>
  
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
		</dic>
	</item>
  
	<item id="cancellationReason" alias="注销原因" type="string" length="1" >
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="6" text="作废" />
			<item key="9" text="其他" />
		</dic>
	</item>
	
	<item id="createUnit" alias="录入单位" length="20" type="string" 
		width="180" fixed="true" defaultValue="%user.manageUnit.id" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
 
	<item id="createUser" alias="录入人" type="string" length="20"
		fixed="true" update="false" defaultValue="%user.userId"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1"
		defaultValue="%server.date.today" hidden="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
  
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
	</relations>
</entry>
