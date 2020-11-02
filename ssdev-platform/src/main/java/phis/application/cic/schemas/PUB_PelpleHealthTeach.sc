<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="phis.application.cic.schemas.PUB_PelpleHealthTeach" alias="健康处方维护">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recipeName" alias="健康处方名称" type="string" length="160" width="170" colspan="3"/>
	<item ref="b.diagnoseName"/>
	<item ref="b.ICD10"/>
	<item ref="b.diagnoseId"/>
	<item id="healthTeach" alias="健康处方内容" type="string" height="260" length="2000" width="200" colspan="3" hidden="true" xtype="textarea">
	</item>
	<item id="recipeNamePy" alias="健康处方名称拼音码" type="string" length="100" display="0">
		<set type="exp" run="server">['py',['$','r.recipeName']]</set>
	</item>
	<item ref="b.diagnoseNamePy" alias="诊断名称拼音码" type="string" length="100"  display="0">
	</item>
	<item id="inputUnit" alias="创建机构" type="string" update="false" length="20" fixed="true" hidden="true" defaultValue="%user.manageUnit.id" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="创建日期" type="datetime"  xtype="datefield" fixed="true" update="false" hidden="true"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUser" alias="创建人" type="string" length="20" width="150" update="false" fixed="true" hidden="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="150" hidden="true"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" width="150" hidden="true"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	 <relations>
		<relation type="parent" entryName="phis.application.cic.schemas.PUB_PelpleHealthDiagnose" >
			<join parent="recordId" child="recordId"></join>
		</relation>
  </relations>
</entry>
