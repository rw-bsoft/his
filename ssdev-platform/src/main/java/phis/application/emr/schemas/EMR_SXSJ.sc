<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_SXSJ" alias="时限时间" sort="GZXH asc">
	<item id="GZXH" alias="规则序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="GZMC" alias="规则名称" type="string" length="50" width="180" fixed ="true"/>
	<item id="KSSJ" alias="开始事件" type="string" length="50" width="140" fixed ="true"/>
	<item id="SXSX" alias="书写时限" type="int" length="9"/>
	<item id="WCBL" alias="完成病历" type="int" length="9" width="180">
		<dic id="phis.dictionary.bllb" autoLoad="true"/>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
</entry>
