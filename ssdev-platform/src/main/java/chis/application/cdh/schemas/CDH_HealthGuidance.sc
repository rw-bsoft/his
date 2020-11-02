<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.cdh.schemas.CDH_HealthGuidance" alias="健康指导">
	<item id="hgId" alias="健康指导编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true"  hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" hidden="true" />
	<item id="checkupId" alias="体检序号" type="string" length="16" hidden="true"/>
	<item id="checkupType" alias="体检类型" type="string" length="1" hidden="true">
	    <dic>
	      <item key="1" text="1岁以内"/>
	      <item key="2" text="1～2岁"/>
	      <item key="3" text="3～6岁"/>
	    </dic>
  	</item>
	<item id="correctDate" alias="矫治日期" type="date" not-null="1"/>
	<item id="processor" alias="处理人" type="string" length="20" width="80" defaultValue="%user.userId" fixed="true" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="ckeckupResult" alias="本次体检结论" type="string" xtype="textarea" length="2000" colspan="3"/>
	<item id="guidingIdea" alias="指导意见" type="binary" xtype="htmleditor" colspan="3" display="2"/>
  
	<item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" display="0"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false"  not-null="1" fixed="true" 
		defaultValue="%server.date.date" enableKeyEvents="true" validationEvent="false" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>