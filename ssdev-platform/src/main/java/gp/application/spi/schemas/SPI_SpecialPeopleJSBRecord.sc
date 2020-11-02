<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.spi.schemas.SPI_SpecialPeopleJSBRecord" alias="特殊人群-残疾人随访">
	<item id="PHRID" alias="健康档案号" length="30" width="130"/>
	<item id="EMPIID" alias="empiId" length="30" width="120" display="0"/>
	<item id="CJRID" alias="残疾人档案主键" length="18" width="120" display="0"/>
	<item id="PERSONNAME" alias="姓名" type="string" length="20"/>
	<item id="SEXCODE" alias="性别" type="string" length="1" width="40"  defalutValue="9">
		<dic id="gp.dictionary.gender"/>
	</item>
	<item id="REGIONCODE" alias="网格地址" type="string" length="25" width="200" colspan="2" anchor="100%" update="false">
		<dic id="gp.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="MANAUNITID" alias="管辖机构" type="string" not-null="true" length="20" colspan="2" anchor="100%" width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="gp.@manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="PLANDATE" alias="计划随访日期" type="date" width="100"/>
	<item id="VISITDATE" alias="实际随访日期" type="date" width="100"/>
	<item id="CREATEUSER" alias="录入人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="MANADOCTORID" alias="责任医生" type="string" length="20" not-null="true" update="false">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
</entry>
