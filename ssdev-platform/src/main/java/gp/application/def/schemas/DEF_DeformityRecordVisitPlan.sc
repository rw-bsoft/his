<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.def.schemas.DEF_DeformityRecordVisitPlan" alias="残疾人随访计划">
	<item id="PHRID" alias="健康档案号" length="30" width="130"/>
	<item id="EMPIID" alias="empiId" length="30" width="120" display="0"/>
	<item id="CJRID" alias="残疾人档案主键" length="18" width="120" display="0"/>
	<item id="INCOMESOURCE" alias="收入" length="2" width="120" display="0"/>
	<item id="PERSONNAME" alias="姓名" type="string" length="20"/>
	<item id="IDCARD" alias="身份证号" type="string" length="20" width="160" />
	<item id="BIRTHDAY" alias="出生日期" type="date" width="75"/>
	<item id="SEXCODE" alias="性别" type="string" length="1" width="40"  defalutValue="9">
		<dic id="gp.dictionary.gender"/>
	</item>
	<item id="CJRLB" alias="残疾类别" type="string" length="1">
		<dic>
			<item key="1" text="肢体残疾"/>  
			<item key="2" text="脑瘫残疾"/>  
			<item key="3" text="智力残疾"/> 
		</dic>
	</item>
	<item id="REGIONCODE" alias="网格地址" type="string" length="25" width="200" colspan="2" anchor="100%" update="false">
		<dic id="gp.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="MOBILENUMBER" alias="本人电话" type="string" length="20" width="90"/>
	<item id="CONTACTPHONE" alias="联系电话" length="20" />
	<item id="MANAUNITID" alias="管辖机构" type="string" not-null="true" length="20" colspan="2" anchor="100%" width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="gp.@manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="MANADOCTORID" alias="责任医生" type="string" length="20" not-null="true" update="false">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
  	
	<item id="WORKPLACE" alias="工作单位" type="string" length="50"/>
	<item id="HEALINGDATE" alias="制定计划日期" type="date" defaultValue="%server.date.today" width="120" maxValue="%server.date.today" />
	<item id="HEALINGTRAINER" alias="康复指导员" type="string" length="20"
		defaultValue="%user.userId" display="2" colspan="2" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="STATUS" alias="档案状态" type="string" length="1"
		defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="INPUTUNIT" alias="录入单位" type="string" length="20" 
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="INPUTUSER" alias="录入人" type="string" length="20" 
		update="false" fixed="true" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="INPUTDATE" alias="录入时间" type="datetime"  xtype="datefield"  update="false" 
		fixed="true" defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="LASTMODIFYUSER" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="LASTMODIFYUNIT" alias="最后修改机构" type="string" length="20"  display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="LASTMODIFYDATE" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>