<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mh.schemas.SQ_JY02" alias="检验明细表"> 
	<item id="JLXH" pkey="true" alias="记录主键" type="string" length="30" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="30" startPos="1"/> 
		</key> 
	</item>  
	<item id="JYDH" alias="检验单号" type="string" length="64" fixed="true"  display="1"/>  
	<item id="JYRQ" alias="检验日期" type="date"/>  
	<item id="XMBH" alias="项目编号" type="string" length="20" fixed="true"  display="1"/>  
	<item id="XMMC" alias="项目名称" type="string" length="40" fixed="true"  display="1"/>  
	<item id="XMZ" alias="项目值" type="string" length="20" fixed="true"  display="1"/>  
	<item id="DW" alias="单位" type="string" length="32" fixed="true"  display="1"/>  
	<item id="CKFW" alias="参考范围" type="string" length="32" fixed="true"  display="1"/>  
	<item id="JYZB" alias="检验指标" type="string" length="10" fixed="true"  display="1"/>  
	<item id="HBBM" alias="黄本编码" type="string" length="20" fixed="true"  display="1"/>  
	<item id="BZ" alias="备注" type="string" length="20" fixed="true"  display="1"/>  
	<item id="INTIME" alias="随访日期" type="date"/>   
	<item id="ZBLB" alias="指标类别" type="int" length="2" fixed="true"  display="1"/>

	<item id="createUnit" alias="创建单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0" defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="0" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
