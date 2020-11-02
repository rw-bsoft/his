<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_Personnel" alias="PHSA_人员注册">
	<item id="PERSONID" alias="人员编号" type="string" queryable="true" pkey="true" generator="assigned"  not-null="1" length="50" selected="true"/>
	<item id="PERSONNAME" alias="人员姓名" type="string" queryable="true" not-null="1" length="50"/>
	<item id="PHOTO" alias="人员照片" xtype="imagefieldex" type="string" display="2" rowspan="4" />
	<item id="CARDTYPE" alias="身份证件类型" type="string" width="150" not-null="1" length="25" defaultValue="01">
		<dic id="platform.reg.dictionary.cardtype"/>
	</item>
	<item id="CARDNUM" alias="身份证件号码" type="string" width="150" not-null="1" queryable="true" length="25"/>
	<item id="BIRTHDAY" alias="出生日期" type="date"/>
	<item id="GENDER" alias="性别" type="string" length="1" width="40" defaultValue="1">
		<dic id="platform.reg.dictionary.gender"/>
	</item>
	<item id="ETHNIC" alias="民族" type="string" width="60" defaultValue="01">
		<dic id="platform.reg.dictionary.ethnic"/>
	</item>
	<item id="HOMETOWN" alias="籍贯" type="string" width="100" length="150"/>
	<item id="JOININWORK" alias="参加工作时间" type="date" queryable="true"/>
	<item id="CERTIFICATENUM" alias="执业证书编码" type="string" length="20" width="150" />
	<item id="JOBPOST" alias="行政职务" type="string">
		<dic id="platform.reg.dictionary.jobpost"/>
	</item>
	<item id="EDUCATION" alias="最高学历" type="string" length="2" defaultValue="20" >
		<dic id="platform.reg.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="EDUCATIONBACKGROUND" alias="学位" type="string">
		<dic id="platform.reg.dictionary.educationbackground"/>
	</item>
	
	<item id="MAJORNAME" alias="所学专业" type="string">
		<dic render="Tree" id="platform.reg.dictionary.majorname"/>
	</item>
	<item id="MAJORQUALIFY" alias="专业技术资格" width="100" type="string">
		<dic render="Tree" onlySelectLeaf="true" id="platform.reg.dictionary.majorqualify"/>
	</item>
	<item id="MAJORJOB" alias="专业技术职务" width="100" type="string">
		<dic id="platform.reg.dictionary.majorjob"/>
	</item>
	<item id="MAJORTYPE" alias="专业类别" type="string">
		<dic id="platform.reg.dictionary.majortype"/>
	</item>
	<item id="OPERATIONTYPE" alias="医师执业类别" width="100" type="string">
		<dic id="platform.reg.dictionary.operationtype"/>
	</item>
	<item id="OPERATIONSCOPE" alias="医师执业范围" type="string" colspan="2" width="100" length="50">
		<dic render="TreeCheck" onlyLeafCheckable="true" id="platform.reg.dictionary.operationscope"/>
	</item>
	<item id="ISEXPERT" alias="专家判别" width="100" type="int" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="EXPERTCOST" alias="专家费用" width="100" display="2" type="double"/>
	<item id="ISCANCEL" alias="作废判别" width="100" type="int" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="PRESCRIBERIGHT" alias="开处方权" width="100" type="string" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="NARCOTICRIGHT" alias="麻醉药权" width="100" type="string" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="PSYCHOTROPICRIGHT" alias="精神药权" width="100" type="string" display="2" defaultValue="0" editable="false" not-null="1">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="ANTIBIOTICRIGHT" alias="抗生素权" width="100" type="int" display="2" defaultValue="0">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="EMAIL" alias="电子邮箱" type="string" colspan="1" width="150" length="50"/>
	<item id="MOBILE" alias="手机号码" type="string" width="100" length="25"/>
	<item id="ORGANIZCODE" alias="所在机构" type="string" width="200" not-null="1" display="-1">
		<dic render="Tree" id="platform.reg.dictionary.organizationDic" />
	</item>
	<item id="OFFICECODE" alias="所在科室" type="string" width="200" display="-1">
		<dic render="Tree" id="platform.reg.dictionary.officeDic" />
	</item>
	<item id="ADDRESS" alias="联系地址" type="string" colspan="1" display="0" length="150"/>
	<item id="LASTMODIFYDATE" alias="最后修改日期" type="date" width="120" display="1">
		<set type="exp" run="server">['$','%server.date.date']</set>
	</item>
	<item id="PYCODE" alias="拼音助记" type="string" length="50">
		<set type="exp" run="server">['py',['$','r.personName']]</set>
	</item>
	<item id="WBCODE" alias="五笔码" type="string" length="10" fixed="false"/>
	<item id="JXCODE" alias="角型码" type="string" length="10" fixed="false"/>
	<item id="QTCODE" alias="其它码" type="string" length="10"/>
	<item id="LOGOFF" alias="状态" type="string" display="0" defaultValue="0"/>
	<item id="REMARK" alias="医生简介" type="string"  colspan="3"
		width="250" height="50" xtype="textarea" length="500"/>
</entry>
