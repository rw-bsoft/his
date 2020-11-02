<?xml version="1.0" encoding="UTF-8"?>
<entry alias="护理记录表单" entityName="ENR_JL01" >
	<item alias="记录编号" id="JLBH" pkey="true" generator="assigned" not-null="1" length="18" type="long">
		<key>
	      <rule name="increaseId" type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item alias="住院流水号" id="ZYH" not-null="1" length="18" type="long"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item alias="结构编号" id="JGBH" not-null="1" length="18" type="long"/>
	<item alias="记录名称" id="JLMC" not-null="1" length="100"/>
	<item alias="病历类型" id="BLLX" not-null="1" length="1" type="long"/>
	<item alias="病历类别" id="BLLB" not-null="1" length="9" type="long"/>
	<item alias="模板类别" id="MBLB" not-null="1" length="9" type="long"/>
	<item alias="模板编号" id="MBBH" not-null="1" length="9" type="long"/>
	<item alias="段落类别" id="DLLB" length="9" type="long"/>
	<item alias="段落键" id="DLJ" length="20"/>
	<item alias="记录行数" id="JLHS" not-null="1" length="4" type="long"/>
	<item alias="换页标志" id="HYBZ" not-null="1" length="1" type="long"/>
	<item alias="记录时间" id="JLSJ" not-null="1" type="timestamp"/>
	<item alias="书写时间" id="SXSJ" not-null="1" type="timestamp"/>
	<item alias="系统时间" id="XTSJ" not-null="1" type="timestamp"/>
	<item alias="书写病区" id="SXBQ" length="8" type="long"/>
	<item alias="书写护士" id="SXHS" not-null="1" length="8"/>
	<item alias="完成签名" id="WCQM" length="18" type="long"/>
	<item alias="完成时间" id="WCSJ" type="date"/>
	<item alias="审阅标志" id="SYBZ" length="1" type="long"/>
	<item alias="审阅时间" id="SYSJ" type="date"/>
	<item alias="审阅护士" id="SYHS" length="10"/>
	<item alias="审阅签名" id="SYQM" length="18" type="long"/>
	<item alias="打印标志" id="DYBZ" not-null="1" length="1" type="long"/>
	<item alias="记录状态" id="JLZT" not-null="1" length="1" type="long"/>
	<item alias="总结编号" id="ZJBH" length="18" type="long"/>
	<item alias="总结名称" id="ZJMC" length="40"/>
	<item alias="总结类型" id="ZJLX" length="1" type="long"/>
	<item alias="开始时间" id="KSSJ" type="date"/>
	<item alias="结束时间" id="JSSJ" type="date"/>
	<item alias="独立换行标志" id="DLHHBZ" length="1" type="long"/>
	<item alias="完成段落路径" id="WCDLLJ" length="50"/>
	<item alias="审阅段落路径" id="SYDLLJ" length="50"/>
	<item alias="监控分类" id="JKFL" length="20"/>
	<item alias="监控评分" id="JKPF" length="9" type="long"/>
	<item alias="监控转归" id="JKZG" length="20"/>
</entry>