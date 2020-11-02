<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_Office" alias="机构报表">
	<item id="officeCode" alias="科室代码" type="string" pkey="true" queryable="true" generator="assigned" width="100" update="false" not-null="1" length="50"/>
	<item id="officeName" alias="科室名称" type="string" queryable="true" colspan="1" width="120" not-null="1" length="50"/>
	<item id="organizCode" alias="所属机构" type="string" width="120" display="1" >
	    <dic render="Tree" id="platform.reg.dictionary.organizationDic"/>
	</item>
	<item id="organizType" alias="科室类型" type="string" width="120" not-null="1" queryable="true">
		<dic render="Tree" id="platform.reg.dictionary.officeType" />
	</item>		
	<item id="parentId" alias="上级科室" type="string" display="2">
<!-- 	   <dic render="Tree" id="officeDic"/> -->
	</item>
	<item id="address" alias="地址" type="string" colspan="2" width="200" length="50"/>
	<item id="outPatientClinic" alias="门诊科室" type="string" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="medicalLab" alias="医技科室" type="string" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="hospitaldept" alias="住院科室" type="string" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="hospitalArea" alias="住院病区" type="string" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="ratedBed" alias="额定床位" type="int" length="4"/>
	<item id="telphone" alias="联系电话" type="string" width="100" length="50"/>
<!-- 	<item id="email" alias="电子邮箱" type="string" width="120" length="45"/>	 -->
	<item id="plsx" alias="排列顺序" type="string" length="10"/>	
	<item id="pyCode" alias="拼音助记" type="string" length="50">
		<set type="exp" run="server">['py',['$','r.officeName']]</set>
	</item>
	<item id="logoff" alias="状态" type="string" display="0" defaultValue="0"/>
</entry>