<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYSSJL" alias="住院患者手术记录">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="JZXH" alias="就诊序号" type="long" length="18" display="0"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="SSBZ" alias="手术操作标志" type="int" length="1" width="100" defaultValue="2">
		<dic autoLoad="true">
			<item key="1" text="手术"/>
			<item key="2" text="操作"/>
		</dic>
	</item>
	<item id="SSXH" alias="手术操作序号" type="long" length="18" display="0"/>
	<item id="SSDM" alias="手术操作编码" type="string" length="16" width="100" fixed="true"/>
	<item id="SSRQ" alias="手术操作日期" type="date" width="120" defaultValue="%server.date.date" />
	<item id="SSMC" alias="手术操作名称" type="string" length="100"  mode="remote" width="150"/>
	<item id="SSJB" alias="手术级别" type="int" length="4">
		<dic autoLoad="true">
			<item key="1" text="一级"/>
			<item key="2" text="二级"/>
			<item key="3" text="三级"/>
			<item key="4" text="四级"/>
		</dic>
	</item>
	<item id="SSLB" alias="手术类别" type="int" length="4">
		<dic autoLoad="true">
			<item key="1" text="择期手术"/>
			<item key="2" text="急诊手术"/>
		</dic>
	</item>
	<item id="SSYS" alias="手术者签名" type="string" length="10">
		<dic id="phis.dictionary.user06" autoLoad="true" sliceType="1" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="YZYS" alias="I助签名" type="string" length="10">
		<dic id="phis.dictionary.user06" autoLoad="true" sliceType="1" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="EZYS" alias="II助签名" type="string" length="10">
		<dic id="phis.dictionary.user06" autoLoad="true" sliceType="1" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="QKLB" alias="切口类别" type="int" length="4">
		<dic autoLoad="true">
			<item key="0" text="0类切口"/>
			<item key="1" text="I类切口"/>
			<item key="2" text="II类切口"/>
			<item key="3" text="III类切口"/>
		</dic>
	</item>
	<item id="YHDJ" alias="愈合等级" type="int" length="4">
		<dic autoLoad="true">
			<item key="1" text="甲"/>
			<item key="2" text="乙"/>
			<item key="3" text="丙"/>
			<!--<item key="5" text="未愈"/>-->
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="MZFS" alias="麻醉方式" type="int" length="4" width="150">
		<dic id="phis.dictionary.Anaesthetic"  autoLoad="true"/>
	</item>
	<item id="MZYS" alias="麻醉医师签名" type="string" length="10" width="100">
		<dic id="phis.dictionary.user06" autoLoad="true" sliceType="1" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ASA" alias="ASA分级" type="int" length="4" defaultValue="0">
		<dic autoLoad="true">
			<item key="0" text=" "/>
			<item key="1" text="I"/>
			<item key="2" text="II"/>
			<item key="3" text="III"/>
			<item key="4" text="IV"/>
			<item key="5" text="V"/>
		</dic>
	</item>
	<item id="SSSQDH" alias="手术申请单号" type="long" length="18" display="0"/>
	<item id="HBFSJ" alias="回病房时间" type="timestamp" display="0"/>
	<item id="SSQCSJ" alias="手术全程时间" type="int" width="100" length="4" max="9999" min="0"/>
	<item id="DJGH" alias="登记工号" type="string" length="10" display="0"/>
	<item id="DJSJ" alias="登记时间" type="timestamp" display="0"/>
	
</entry>
