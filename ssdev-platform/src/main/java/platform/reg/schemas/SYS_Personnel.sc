<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_Personnel" alias="人员注册">
	<item id="personId" alias="人员编号" type="string" queryable="true" pkey="true" generator="assigned"  not-null="1" length="50"/>
	<item id="personName" alias="人员姓名" type="string" queryable="true" not-null="1" length="50"/>
	<item id="photo" alias="人员照片" xtype="imagefieldex" type="string" display="2" rowspan="4" />
	<item id="cardtype" alias="身份证件类型" type="string" width="150" not-null="1" length="25" defaultValue="01">
		<dic id="platform.reg.dictionary.cardtype"/>
	</item>
	<item id="cardnum" alias="身份证件号码" type="string" width="150" not-null="1" queryable="true" length="25"/>
	<item id="birthday" alias="出生日期" type="date"/>
	<item id="gender" alias="性别" type="string" length="1" width="40" defaultValue="1">
		<dic id="platform.reg.dictionary.gender"/>
	</item>
	<item id="ethnic" alias="民族" type="string" width="60" defaultValue="01">
		<dic id="platform.reg.dictionary.ethnic"/>
	</item>
	<item id="hometown" alias="籍贯" type="string" width="100" length="150"/>
	<item id="joininwork" alias="参加工作时间" type="date" queryable="true"/>
	<item id="certificatenum" alias="执业证书编码" type="string" length="20" width="150" />
	<item id="jobpost" alias="行政职务" type="string">
		<dic id="platform.reg.dictionary.jobpost"/>
	</item>
	<item id="education" alias="最高学历" type="string" length="2" defaultValue="20" >
		<dic id="platform.reg.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="educationbackground" alias="学位" type="string">
		<dic id="platform.reg.dictionary.educationbackground"/>
	</item>
	
	<item id="majorname" alias="所学专业" type="string">
		<dic render="Tree" id="platform.reg.dictionary.majorname"/>
	</item>
	<item id="majorqualify" alias="专业技术资格" width="100" type="string">
		<dic render="Tree" onlySelectLeaf="true" id="platform.reg.dictionary.majorqualify"/>
	</item>
	<item id="majorjob" alias="专业技术职务" width="100" type="string">
		<dic id="platform.reg.dictionary.majorjob"/>
	</item>
	<item id="majortype" alias="专业类别" type="string">
		<dic id="platform.reg.dictionary.majortype"/>
	</item>
	<item id="operationtype" alias="医师执业类别" width="100" type="string">
		<dic id="platform.reg.dictionary.operationtype"/>
	</item>
	<item id="operationscope" alias="医师执业范围" type="string" colspan="2" width="100" length="50">
		<dic render="TreeCheck" onlyLeafCheckable="true" id="platform.reg.dictionary.operationscope"/>
	</item>
	<item id="medicalRoles" alias="医疗角色" width="100" display="2" type="string">
		<dic render="Tree" id="phis.dictionary.medicalRoles"/>
	</item>
	<item id="isexpert" alias="专家判别" width="100" type="int" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="expertcost" alias="专家费用" width="100" display="2" type="double"/>
	<item id="prescriberight" alias="开处方权" width="100" type="string" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="iscancel" alias="作废判别" width="100" type="int" display="0" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="narcoticright" alias="麻醉药权" width="100" type="string" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="psychotropicright" alias="精神药权" width="100" type="string" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="antibioticright" alias="抗生素权" width="100" type="int" display="2" defaultValue="0">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="email" alias="电子邮箱" type="string" colspan="1" width="150" length="50"/>
	<item id="mobile" alias="手机号码" type="string" width="100" length="25"/>
	
	<item id="AntibioticLevel" alias="抗生素等级" type="string" width="200" colspan="3">
		<dic render="Checkbox">
			<item key="1" text="抗菌一级"/>
			<item key="2" text="抗菌二级"/>
			<item key="3" text="抗菌三级"/>
		</dic>
	</item>
	<item id="organizCode" alias="所在机构" type="string" width="200" not-null="1">
		<dic render="Tree" id="platform.reg.dictionary.organizationDic" rootVisible="true"/>
	</item>
	<item id="officeCode" alias="所在科室" type="string" width="200" display="-1">
		<dic render="Tree" id="platform.reg.dictionary.officeDic" />
	</item>
	<item id="address" alias="联系地址" type="string" colspan="1" display="0" length="150"/>
	<item id="lastModifyDate" alias="最后修改日期" type="date" width="120" display="1">
		<set type="exp" run="server">['$','%server.date.date']</set>
	</item>
	<item id="pyCode" alias="拼音助记" type="string" length="50">
		<set type="exp" run="server">['py',['$','r.personName']]</set>
	</item>
	<item id="wbCode" alias="五笔码" type="string" length="10" fixed="false"/>
	<item id="jxCode" alias="角型码" type="string" length="10" fixed="false"/>
	<item id="qtCode" alias="其它码" type="string" length="10"/>
	<item id="logoff" alias="状态" type="string" display="0" defaultValue="0"/>
	<item id="remark" alias="业务特长" type="string"  colspan="3"
		width="250" height="50" xtype="textarea" length="500"/>
</entry>
