<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="phis.application.cic.schemas.PUB_PelpleHealthDiagnose" alias="健康处方维护_疾病" sort="a.ICD10">
	<item id="diagnoseId" alias="疾病序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="diagnoseType" alias="疾病类别" type="string" length="20">
		<dic>
			<item key="1" text="西医疾病"/>
			<item key="2" text="中医疾病"/>
			<item key="3" text="中医证候"/>
		</dic>
	</item>
	<item id="recordId" alias="处方序号" type="string" length="16" not-null="1"  display="0"/>
	<item id="ICD10" alias="疾病编码" type="string" length="50" width="100" mode="remote"/>
	<item id="diagnoseName" alias="疾病名称" type="string" length="100" width="200" colspan="2" fixed="true"/> 
	<item id="diagnoseNamePy" alias="疾病拼音码" type="string" length="100"> 
		<set type="exp" run="server">['py',['$','r.diagnoseName']]</set>
	</item> 
	<item id="inputUnit" alias="创建机构" type="string" update="false" length="20" display="0" fixed="true" defaultValue="%user.manageUnit.id" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="创建日期" type="datetime"  xtype="datefield" display="0" fixed="true" update="false"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUser" alias="创建人" type="string" length="20" width="150"  display="0" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="150"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" width="150"
		defaultValue="%user.manageUnit.id"  display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
