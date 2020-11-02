<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="CHTEMPLATE" alias="病历病史模板">
	<item id="CHTCODE" alias="病历模板代码" type="long" pkey="true"/>
	<item id="CHTNAME" alias="模板名称" type="string"/>
	<item id="VERSIONNO" alias="版本号" type="long"/>
	<item id="DEPTGROUPCODE" alias="科室部门组别" type="string"/>
	<item id="XMLTEXTPAT" alias="主诉的XML文本" type="blob"/>
	<item id="XMLTEXTILLHIS" alias="既往史的XML文本" type="blob"/>
	<item id="XMLTEXTILLHISCUR" alias="现病史的XML文本" type="blob"/>
	<item id="XMLTEXTPERSON" alias="个人史的XML文本" type="blob"/>
	<item id="XMLTEXTFAMILY" alias="家族史的XML文本" type="blob"/>
	<item id="USED" alias="是否已经被使用过" type="long"/>
	<item id="ICD10CODE" alias="ICD10编码" type="string"/>
	<item id="INOROUTTYPE" alias="住院门诊类型" type="int"/>
	<item id="FRAMEWORKCODE" alias="结构代码" type="string"/>
	<item id="ISHDRFTRTEMP" alias="页眉页脚" type="int"/>
	<item id="TEMPLATETYPE" alias="模板类别" type="string"/>
	<item id="NOTAVAILABLE" alias="停用标志" type="int"/>
	<item id="SSZK" alias="所属专科" type="long"/>
	<item id="SSQY" alias="所属区域" type="long"/>
	<item id="PYDM" alias="拼音代码" type="string"/>
</entry>
