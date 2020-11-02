<?xml version="1.0" encoding="UTF-8"?>

<entry  id="JC_YGPJ_JK" tableName="JC_YGPJ"  alias="票据号码维护">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="QSHM" alias="起始号码" length="20" not-null="1"/>
	<item id="ZZHM" alias="终止号码" length="20" not-null="1"/>
	<item id="DQHM" alias="使用号码" length="20" not-null="1"/>
	<item id="YGDM" alias="领用人" length="10" not-null="1" defaultValue="%user.userId">
		<dic id="phis.dictionary.user_jcbill" sliceType="1" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="LYRQ" alias="领用日期" not-null="1" width="130" xtype="datetimefield" type="datetime" defaultValue="%server.date.datetime" />
	<item id="SYBZ" alias="使用标志" type="int" length="1" not-null="1" display="0" defaultValue="0"/>
	<item id="PJLX" alias="票据类型" type="int" display="0" length="1" not-null="1" defaultValue="2"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1" defaultValue="%user.manageUnit.id"/>

</entry>
