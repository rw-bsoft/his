<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.her.schemas.HER_HealthEducationRecipel" alias="健康教育处方" sort="a.herId">
	<item id="herId" alias="处方编号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key> 
	</item>
	<item id="setId" alias="计划编号" type="string" length="16" display="0"/>
	<item id="exeId" alias="执行编号" type="string" length="16" display="0"/>
	<item id="recordId" alias="执行内容编号" type="string" length="16" display="0"/>
	<item id="recipelContent" alias="处方内容" type="binary" xtype="textarea" colspan="3" display="2"  height="300"/>
	
	<item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" display="0"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" width="100">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  update="false"  not-null="1" fixed="true"  width="100"
		  defaultValue="%server.date.date" enableKeyEvents="true" validationEvent="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="100" 
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" width="100" 
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