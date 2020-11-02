<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病历01表" entityName="OMR_BL01">
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
	<item id="CJSJ" alias="系统时间" type="timestamp"  display="2" update="false" fixed="true"/>
	<item id="WCSJ" alias="完成时间" type="timestamp" display="2" update="false" fixed="true"/>
	<item id="SXKS" alias="创建科室" length="10" type="long"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="SXYS" alias="书写医生" type="long" length="18"  display="2" update="false" fixed="true">
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
	<item id="BRKS" alias="病人科室" type="long" length="18"  display="2" update="false" fixed="true">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="JZXH" alias="就诊序号" type="long" length="18"  display="0" update="false" fixed="true"/>
	<item id="BLBH" alias="病历编号" type="long" length="18"  generator="assigned" pkey="true" display="2" update="false" fixed="true"/>
	<item id="JGID" alias="医疗机构" type="string" length="20"  display="2" update="false" fixed="true">
		<dic id="phis.@manageUnit"/>
	</item>
</entry>
