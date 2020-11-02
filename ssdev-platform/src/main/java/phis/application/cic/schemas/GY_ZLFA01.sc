<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_ZLFA01" tableName="GY_ZLFA" sort="a.ZLXH desc" alias="诊疗方案名称">
	<item id="ZLXH" alias="诊疗序号" length="12" type="int" display="0" not-null="1" generator="assigned" pkey="true" >
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="1"/>
		</key>
	</item>
	<item id="ZLMC" alias="诊疗方案名称" type="string" length="30" not-null="1" width="160"/>
	<item id="PYDM" alias="拼音代码" type="string" display="2" length="10" queryable="true" selected="true" target="ZLMC" codeType="py"/>
	<item id="QYBZ" alias="启用" length="1" type="int" display="1" defaultValue="0" renderer="onRenderer"/>
	<item id="SSLB" alias="所属类别" length="2" display="0" type="int" defaultValue="4"/>
	<item id="YGDM" alias="员工代码" type="string" display="0" length="10" defaultValue="%user.userId"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" defaultValue="%user.manageUnit.id"/>
	<item id="KSDM" alias="科室代码" type="int" display="0" length="10"/>
	<item id="CFZTBH" alias="处方组套" type="long" length="18" display="0"/>
	<item id="XMZTBH" alias="项目组套" type="long" length="18" display="0"/>
	<item id="JBXH" alias="常用诊断" type="long" length="18" display="0"/>
	<item id="BLMBBH" alias="病例模版" type="long" length="18" display="0"/>
</entry>
