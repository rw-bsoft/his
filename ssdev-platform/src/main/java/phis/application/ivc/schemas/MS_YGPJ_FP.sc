<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YGPJ"  alias="票据号码维护">
	<item id="JLXH" alias="记录序号" type="int" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="PJLX" alias="票据类型" not-null="1" type="int" display="0" defaultValue="2"/>
	<item id="JGID" alias="机构ID" not-null="1" type="string" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="QSHM" alias="起始号码" not-null="1" type="string" width="120" length="20"/>
	<item id="ZZHM" alias="终止号码" not-null="1" type="string" width="120" length="20"/>
	<item id="SYHM" alias="使用号码" not-null="1" type="string" width="120" length="20"/>
	<item id="SYPB" alias="使用判别" not-null="1" type="int" display="0" defaultValue="0"/>
	<item id="YGDM" alias="领用人" length="10" type="string" not-null="1" defaultValue="%user.userId">
		<dic id="phis.dictionary.user_bill" sliceType="1" filter="['eq',['$','item.properties..manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="LYRQ" alias="领用日期" not-null="1" width="130" type="datetime" defaultValue="%server.date.datetime" />
</entry>
