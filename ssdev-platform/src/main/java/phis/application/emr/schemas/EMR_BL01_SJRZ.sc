<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病历01表" entityName="EMR_BL01">
	<item id="BRID" alias="病人ID" type="long" length="18" display="0" update="false" fixed="true"/>
	<item id="BLLX" alias="病历类型" type="int" length="1" display="0" update="false" fixed="true">
		<dic>
			<item key="0" text="病历"/>
			<item key="1" text="病程"/>
		</dic>
	</item>
	<item id="BLLB" alias="病历类别" type="int" length="9" display="0" update="false" fixed="true">
		<dic id="phis.dictionary.caseFramework"/>
	</item>
	<item id="BLMC" alias="病历名称" length="100" width="200" update="false" fixed="true"/>
	<item id="DLLB" alias="段落类别" type="int" length="9" display="0" update="false" fixed="true"/>
	<item id="DLJ" alias="段落键" length="18" display="0" update="false" fixed="true"/>
	<item id="MBLB" alias="模板类别" type="int" length="9" display="2" update="false" fixed="true">
		<dic id="phis.dictionary.caseType"/>
	</item>
	<item id="MBBH" alias="模板编号" type="int" length="9"  display="0" update="false" fixed="true"/>
	<item id="JLSJ" alias="记录时间" type="timestamp"  display="2" update="false" fixed="true"/>
	<item id="XTSJ" alias="系统时间" type="timestamp"  display="2" update="false" fixed="true"/>
	<item id="WCSJ" alias="完成时间" type="timestamp" display="2" update="false" fixed="true"/>
	<item id="CJKS" alias="创建科室" length="10"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="SXYS" alias="书写医生" type="long" length="18"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.user06"/>
	</item>
	<item id="SSYS" alias="所属医生" length="8"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.user06"/>
	</item>
	<item id="BLZT" alias="病历状态" type="int" length="1" update="false" fixed="true">
		<dic>
			<item key="0" text="书写" />
			<item key="1" text="完成" />
			<item key="2" text="封存(归档)" />
			<item key="9" text="删除" />
		</dic>
	</item>
	<item id="SYBZ" alias="审阅标志" type="int" length="1"  display="0" update="false" fixed="true"/>
	<item id="BLYM" alias="病历页码" type="int" length="4"  display="0" update="false" fixed="true"/>
	<item id="YMJL" alias="页眉距离" length="10"  display="0" update="false" fixed="true"/>
	<item id="BRXM" alias="病人姓名" length="30"  display="2" update="false" fixed="true"/>
	<item id="BRKS" alias="病人科室" type="long" length="18"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="BRBQ" alias="病人病区" type="long" length="18"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.lesionOffice"/>
	</item>
	<item id="BRCH" alias="病人床号" length="10"  display="2" update="false" fixed="true"/>
	<item id="BRNL" alias="病人年龄" length="20"  display="2" update="false" fixed="true"/>
	<item id="BRZD" alias="病人诊断" length="255"  display="2" update="false" fixed="true"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18"  display="0" update="false" fixed="true"/>
	<item id="BRBH" alias="病人编号" length="18"  display="2" update="false" fixed="true"/>
	<item id="BLBH" alias="病历编号" type="long" length="18"  generator="assigned" pkey="true" display="2" update="false" fixed="true"/>
	<item id="JGID" alias="医疗机构" type="string" length="20"  display="2" update="false" fixed="true">
		<dic id="phis.@manageUnit"/>
	</item>
</entry>
