<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.common.schemas.EHR_RecordInfo" alias="档案信息">
	<item id="empiId" pkey="true" alias="empiid" type="string" length="32" width="160" not-null="1" fixed="true" queryable="true" generator="assigned">
	</item>
	<item id="GRDA" alias="个人档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="GRQY" alias="个人签约" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="GAO" alias="高血压档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="TANG" alias="糖尿病档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="LAO" alias="老年人档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="LI" alias="离休干部档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="YI" alias="肿瘤易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN" alias="肿瘤现患" type="int" length="1" defaultValue="0" display="0"></item>
	
	<item id="YI_DC" alias="大肠易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN_DC" alias="大肠现患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="YI_WEI" alias="胃易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN_WEI" alias="胃现患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="YI_GAN" alias="肝易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN_GAN" alias="肝现患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="YI_FEI" alias="肺易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN_FEI" alias="肺现患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="YI_RX" alias="乳腺易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN_RX" alias="乳腺现患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="YI_GJ" alias="宫颈易患" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="XIAN_GJ" alias="宫颈现患" type="int" length="1" defaultValue="0" display="0"></item>
	
	
	<item id="BAO" alias="肿瘤报告" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="JING" alias="精神病档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="ER" alias="儿童档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="RUO" alias="体弱儿童档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="FU" alias="孕产妇档案" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="CANZT" alias="残疾人肢体" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="CANN" alias="残疾人脑" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="CANZL" alias="残疾人智力" type="int" length="1" defaultValue="0" display="0"></item>
	<item id="LOGOUT" alias="注销标志" type="int" length="1" defaultValue="0" display="0"></item>
	
	<item id="createUnit" alias="建档单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
