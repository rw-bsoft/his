<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_Office" alias="PHSA_机构报表">
	<item id="ID" alias="编号" type="long" pkey="true" keyGenerator="auto" length="50" display="0"/>
	<item id="OFFICECODE" alias="科室代码" type="string" queryable="true" width="100" update="false" not-null="1" length="50"/>
	<item id="OFFICENAME" alias="科室名称" type="string" queryable="true" colspan="1" width="120" not-null="1" length="50"/>
	<item id="ORGANIZCODE" alias="所属机构" type="string" width="120" display="1" >
		<dic render="Tree" id="platform.reg.dictionary.organizationDic"/>
	</item>
	<item id="ORGANIZTYPE" alias="科室类型" type="string" width="120" not-null="1" queryable="true">
		<dic render="Tree" id="platform.reg.dictionary.officeType" />
	</item>		
	<item id="PARENTID" alias="上级科室" type="string" display="2">
		<!-- 	   <dic render="Tree" id="officeDic"/> -->
	</item>
	<item id="ADDRESS" alias="地址" type="string" colspan="2" width="200" length="50"/>
	<item id="OUTPATIENTCLINIC" alias="门诊科室" type="string" not-null="1" defaultValue="0">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="MEDICALLAB" alias="医技科室" type="string" not-null="1" defaultValue="0">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="HOSPITALDEPT" alias="住院科室" type="string" not-null="1" defaultValue="0">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="HOSPITALAREA" alias="住院病区" type="string" not-null="1" defaultValue="0">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="RATEDBED" alias="额定床位" type="int" length="4"/>
	<item id="TELPHONE" alias="联系电话" type="string" width="100" length="50"/>
	<!-- 	<item id="email" alias="电子邮箱" type="string" width="120" length="45"/>	 -->
	<item id="PLSX" alias="排列顺序" type="string" length="10"/>	
	<item id="PYCODE" alias="拼音助记" type="string" length="50">
		<set type="exp" run="server">['py',['$','r.officeName']]</set>
	</item>
	<item id="LOGOFF" alias="状态" type="string" display="0" defaultValue="0"/>
</entry>