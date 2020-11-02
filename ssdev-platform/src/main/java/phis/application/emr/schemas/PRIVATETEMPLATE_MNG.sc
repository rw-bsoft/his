<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PRIVATETEMPLATE" alias="个人模版管理">
	<item id="PTID" alias="编号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="PTNAME" alias="模版名称" length="25" not-null="1" width="175"/>
	<item id="PTTYPE" alias="模板类型" type="int" length="1" queryable="true" fixed="true"  selected="true">
		<dic>
			<item key="0" text="病历"/>
			<item key="1" text="病程"/>
			<item key="3" text="常用语"/>
		</dic>
	</item>	
	<item id="SPTTYPE" alias="所属类型" type="int" length="1" not-null="1" fixed="true"   defaultValue="1">
		<dic>
			<item key="0" text="科室"/>
			<item key="1" text="个人"/>
			<item key="2" text="公共"/>
		</dic>
	</item>	
	<item id="TEMPLATETYPE" alias="模板类别" type="int" length="1"  fixed="true" >
		<dic id="phis.dictionary.emr_kbm_bllb"/>
	</item>
	<item id="FRAMEWORKCODE" alias="病历类别" length="20"  fixed="true" >
		<dic id="phis.dictionary.emr_kbm_bllb"/>
	</item>
	<item id="TEMPLATECODE" alias="模板编号" type="long" length="18" display="0"/>
	<item id="DISEASENAME" alias="疾病名称" fixed="true"  width="160"/>
	<item id="PTSTATE" alias="状态" length="1" queryable="true"  fixed="true" >
		<dic autoLoad="true">
			<item key="0" text="使用"/>
			<item key="9" text="注销"/>
		</dic>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" />
</entry>