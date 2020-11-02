<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BL01" alias="病历01表_关联换页">
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1" isGenerate="false" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1000" />
		</key>
	</item>
	<item id="JZXH" alias="就诊序号" type="long" length="18" not-null="1"/>
	<item id="BRBH" alias="病人编号" length="18" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1"/>
	<item id="BLLX" alias="病历类型" type="int" length="1" not-null="1"/>
	<item id="BLLB" alias="病历类别" type="int" length="9" not-null="1"/>
	<item id="BLMC" alias="病历名称" length="100" not-null="1"/>
	<item id="DLLB" alias="段落类别" type="int" length="9" not-null="1"/>
	<item id="DLJ" alias="段落键" length="18" not-null="1"/>
	<item id="MBLB" alias="模板类别" type="int" length="9" not-null="1"/>
	<item id="MBBH" alias="模板编号" type="int" length="9" not-null="1"/>
	<item id="JLSJ" alias="记录时间" type="date" not-null="1"/>
	<item id="XTSJ" alias="系统时间" type="date" not-null="1"/>
	<item id="WCSJ" alias="完成时间" type="date"/>
	<item id="CJKS" alias="创建科室" length="10" not-null="1"/>
	<item id="SXYS" alias="书写医生" type="long" length="18" not-null="1"/>
	<item id="SSYS" alias="所属医生" length="8" not-null="1"/>
	<item id="BLZT" alias="病历状态" type="int" length="1" not-null="1"/>
	<item id="SYBZ" alias="审阅标志" type="int" length="1" not-null="1"/>
	<item id="BLYM" alias="病历页码" type="int" length="4" not-null="1"/>
	<item id="YMJL" alias="页眉距离" length="10" not-null="1"/>
	<item id="BRXM" alias="病人姓名" length="30" not-null="1"/>
	<item id="BRKS" alias="病人科室" type="long" length="18" not-null="1"/>
	<item id="BRBQ" alias="病人病区" type="long" length="18" not-null="1"/>
	<item id="BRCH" alias="病人床号" length="10" not-null="1"/>
	<item id="BRNL" alias="病人年龄" length="20" not-null="1"/>
	<item id="BRZD" alias="病人诊断" length="255" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item ref="b.HYBZ" alias="换页标志" type="int" length="1" />
	<relations>
		<relation type="parent" entryName="phis.application.emr.schemas.EMR_KBM_BLLB" >
			<join parent="LBBH" child="MBLB"></join>
		</relation>
	</relations>
</entry>
