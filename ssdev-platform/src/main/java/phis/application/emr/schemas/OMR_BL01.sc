<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="OMR_BL01" alias="病历01表">
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1" isGenerate="false" generator="assigned" pkey="true" />
	<item id="JZXH" alias="就诊序号" type="long" length="18" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1"/>
	<item id="BLLX" alias="病历类型" type="int" length="1" not-null="1"/>
	<item id="BLLB" alias="病历类别" type="int" length="9" not-null="1"/>
	<item id="BLMC" alias="病历名称" length="100" not-null="1"/>
	<item id="DLLB" alias="段落类别" type="int" length="9" not-null="1"/>
	<item id="DLJ" alias="段落键" length="18" not-null="1"/>
	<item id="MBLB" alias="模板类别" type="int" length="9" not-null="1"/>
	<item id="MBBH" alias="模板编号" type="int" length="9" not-null="1"/>
	<item id="JLSJ" alias="记录时间" type="date" not-null="1"/>
	<item id="CJSJ" alias="创建时间" type="date" not-null="1"/>
	<item id="WCSJ" alias="完成时间" type="date"/>
	<item id="SXYS" alias="书写医生" type="string" length="18" not-null="1"/>
	<item id="SXKS" alias="书写科室" length="10" type="long" not-null="1"/>
	<item id="BRKS" alias="病人科室" length="10" type="long" not-null="1"/>
	<item id="BLZT" alias="病历状态" type="int" length="1" not-null="1"/>
	<item id="SYBZ" alias="审阅标志" type="int" length="1" not-null="1"/>
	<item id="BZMBBH" alias="病种模板编号" type="long" length="18" not-null="1"/>
	<item id="BLBBZ" alias="病历本标志" type="int" length="1" not-null="1"/>
	<item id="BLPF" alias="病历评分" type="int" length="8" not-null="1"/>
	<item id="ZZDY" alias="自助打印" type="int" length="1" not-null="1"/>
	<item id="PTID" alias="私有模板ID" type="long" length="18" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
</entry>
