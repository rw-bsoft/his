<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PRIVATETEMPLATE" alias="私有模板">
	<item id="PTID" alias="私有模板ID" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="PTTYPE" alias="私有模板类型" type="int" length="1" not-null="1"/>
	<item id="PTNAME" alias="模板名称" length="50" not-null="1"/>
	<item id="FRAMEWORKCODE" alias="结构代码" length="20" not-null="1"/>
	<item id="TEMPLATETYPE" alias="模板类别" length="20" not-null="1"/>
	<item id="SPTTYPE" alias="所属类型" type="int" length="1" not-null="1"/>
	<item id="SPTCODE" alias="所属对象ID" length="20" not-null="1"/>
	<item id="PTTEMPLATE" alias="模板内容" type="object"/>
	<item id="PTTEMPLATETEXT" alias="模板内容文本" type="object"/>
	<item id="TEMPLATECODE" alias="模板编号" type="long" length="18"/>
	<item id="REGISTRAR" alias="记录者" length="20"/>
	<item id="PTSTATE" alias="模板状态" type="int" length="1"/>
	<item id="DISEASEID" alias="疾病ID" type="int" length="9"/>
	<item id="DISEASETYPE" alias="病种名称" length="100"/>
	<item id="SJBZ" alias="升级标志" type="int" length="1"/>
	<item id="BZMBBH" alias="病种模板编号" type="long" length="18"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
</entry>
