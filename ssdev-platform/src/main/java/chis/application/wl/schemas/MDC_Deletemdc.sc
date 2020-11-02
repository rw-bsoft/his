<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.wl.schemas.MDC_Deletemdc" alias="慢病删除记录" sort="a.Id desc">
  <item id="Id" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" display="1"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="empiId" alias="EMPIID" type="string"  fixed="true"  display="0"/>
  <item id="phrId" alias="档案编号" type="string"  fixed="true" hidden="true" />
  <item id="deleteUser" alias="删除医生" type="string"  hidden="true" update="false"  length="20" fixed="true" defaultValue="%user.userId" queryable="true" >
  	<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
 </item>
 <item id="deleteUnit" alias="建档机构" type="string" update="false" hidden="true" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
  <item id="deleteDate" alias="删除日期" type="datetime" hidden="true" xtype="datefield" update="false"  not-null="1" fixed="true" defaultValue="%server.date.today" enableKeyEvents="true" validationEvent="false" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
  <item id="deletereson" alias="删除原因" type="string" not-null="true" length="200"  width="200" />		
  <item id="recordType" alias="档案类型" type="string"  fixed="true" hidden="true" >
  	<dic render="LovCombo">
  		<item key="1" text="高血压" />
  		<item key="2" text="糖尿病" />
  	</dic>
  </item>
    <item id="oldCreateUser" alias="建档医生" type="string" hidden="true" update="false"  length="20" fixed="true" defaultValue="%user.userId" queryable="true" >
  	<dic id="chis.dictionary.Personnel"/>	 
 </item>
  <item id="oldCreateUnit" alias="建档机构" type="string" hidden="true" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
  <item id="oldCreateDate" alias="删除日期" type="datetime" hidden="true" xtype="datefield" update="false"  not-null="1" fixed="true" defaultValue="%server.date.today" enableKeyEvents="true" validationEvent="false" queryable="true">
	</item>
</entry>