<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PRIVATETEMPLATE" alias="常用语">
	<item id="PTID" alias="私有模板ID" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="PTTYPE" alias="私有模板类型" type="int" length="1" display="0">
		<dic>
			<item key="0" text="病历"/>
			<item key="1" text="病程"/>
			<item key="3" text="常用语"/>
		</dic>
	</item>
	<item id="PTNAME" alias="常用语名称" length="25" not-null="1" width="175"/>
	<item id="FRAMEWORKCODE" alias="结构代码" length="20" display="0"/>
	<item id="TEMPLATETYPE" alias="模板类别" length="20" display="0"/>
	<item id="SPTTYPE" alias="常用语类型" type="int" length="1" not-null="1" display="2" defaultValue="1">
		<dic>
			<item key="0" text="科室"/>
			<item key="1" text="个人"/>
			<item key="2" text="公共"/>
		</dic>
	</item>
	<item id="SPTCODE" alias="所属对象ID" length="20" display="0">
	</item>
	<item id="PTTEMPLATE" alias="模板内容" display="0" type="object"/>
	<item id="PTTEMPLATETEXT" alias="模板内容文本" display="0" type="object"/>
	<item id="TEMPLATECODE" alias="模板编号" type="long" length="18" display="0"/>
	<item id="REGISTRAR" alias="记录者" length="20" display="0" defaultValue="%user.userId">
		<dic id="phis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.properties.manaUnitId" />
	</item>
	<item id="PTSTATE" alias="状态" type="int" length="1" display="0">
		<dic>
			<item key="0" text="使用"/>
			<item key="9" text="注销"/>
		</dic>
	</item>
	<item id="DISEASEID" alias="疾病ID" type="int" length="9" display="0"/>
	<item id="DISEASETYPE" alias="病种名称" length="100" display="0"/>
	<item id="SJBZ" alias="升级标志" type="int" length="1" display="0">
		<dic>
			<item key="0" text="需要升级"/>
			<item key="1" text="已升级"/>
			<item key="2" text="无需升级"/>
		</dic>
	</item>
	<item id="BZMBBH" alias="病种模板编号" type="long" length="18" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id">
		<dic id="phis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.properties.manaUnitId" />
	</item>
</entry>