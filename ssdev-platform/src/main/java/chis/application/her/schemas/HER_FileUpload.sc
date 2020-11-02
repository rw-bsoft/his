<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.her.schemas.HER_FileUpload" alias="健康教育资料" sort="a.fileId desc">
	<item id="fileId" alias="文件编号" type="string" length="16" not-null="1" width="150" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key> 
	</item>
	<item id="setId" alias="计划编号" type="string" length="16" not-null="1"  width="150" display="0" fixed="true"/>
	<item id="exeId" alias="执行编号" type="string" length="16" not-null="1"  display="0"/>
	<item id="recordId" alias="执行内容编号" type="string" length="16" not-null="1"  display="0"/>
	
	<item id="fileName" alias="文件名称" type="string" length="200" width="200" display="1"/>
	<item id="fileType" alias="资料分类" type="string" length="2">
		<dic id="chis.dictionary.fileType"/>
	</item>
	<item id="fileObjectType" alias="对象分类" type="string" length="2">
		<dic id="chis.dictionary.objectType"/>
	</item>
	<item id="fileSite" alias="文件保存目录" type="string" length="1000" display="0"/>
	
	<item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" display="0"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false"  not-null="1" fixed="true" 
		  defaultValue="%server.date.date" enableKeyEvents="true" validationEvent="false" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>