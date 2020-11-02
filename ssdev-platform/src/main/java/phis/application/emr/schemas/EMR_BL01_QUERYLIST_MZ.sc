<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病历01表" entityName="EMR_BL01">
	<item id="BLMC" alias="病历名称" length="100" width="200"/>
	<item id="BLZT" alias="病历状态" type="int" length="1">
		<dic>
			<item key="0" text="书写" />
			<item key="1" text="完成" />
			<item key="2" text="封存(归档)" />
			<item key="9" text="删除" />
		</dic>
	</item>
	<item id="BLLX" alias="病历类型" type="int" length="1">
		<dic>
			<item key="0" text="病历"/>
			<item key="1" text="病程"/>
		</dic>
	</item>
	<item id="BRBH" alias="病人编号" length="18"/>
 
	<item id="BRXM" alias="病人姓名" length="30"/>
	<item id="BRZD" alias="病人诊断" length="255"/>
	<item id="SXYS" alias="书写医生" type="long" length="18">
		<dic id="phis.dictionary.doctor" sliceType="1"/>
	</item>
	<item id="CJKS" alias="书写科室" length="10">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="BRKS" alias="病人科室" type="long" length="18">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="JLSJ" alias="记录时间" type="date" queryable="true"/>
	<item id="XTSJ" alias="创建时间" type="date"/>
	<item id="WCSJ" alias="完成时间" type="date"/>
	<item id="BRBQ" alias="病人病区" type="long" length="18">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="BRCH" alias="病人床号" length="10" queryable="true"/>
	<item id="BRNL" alias="病人年龄" length="20" queryable="true"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18" queryable="true"/>
	<item id="BRID" alias="病人号码" type="long" length="18" queryable="true"/>
	<item id="BLBH" alias="病历编号" type="long" length="18"  generator="assigned" pkey="true" display="0"/>
	<item id="JGID" alias="医疗机构" type="string" length="20">
		<dic id="phis.@manageUnit"/>
	</item>
	<item id="MBLB" alias="模板类别" type="int" length="9">
		<dic id="phis.dictionary.caseType"/>
	</item>
	<item id="BLLB" alias="病历类别" type="int" length="9">
		<dic id="phis.dictionary.caseFramework"/>
	</item>
	<item id="MBBH" alias="指定模板" length="9">
		<dic id="phis.dictionary.model"/>
	</item>
	<item id="SSYS" alias="所属医生" length="8">
		<dic id="phis.dictionary.doctor" sliceType="1"/>
	</item>
	<item id="DLLB" alias="段落类别" type="int" length="9" display="0"/>
	<item id="DLJ" alias="段落键" length="18" display="0"/>

	<item id="SYBZ" alias="审阅标志" type="int" length="1" display="0"/>
	<item id="BLYM" alias="病历页码" type="int" length="4" display="0"/>
	<item id="YMJL" alias="页眉距离" length="10" display="0"/>
  
</entry>
