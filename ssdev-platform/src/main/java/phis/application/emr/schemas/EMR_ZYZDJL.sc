<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYZDJL" alias="住院患者诊断记录">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="JZXH" alias="就诊序号" type="long" length="18" display="0"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="ZZBZ" alias="主诊标记" type="int" length="1" renderer="onRenderer" fixed="true"/>
	<item id="ZDLB" alias="诊断类别" type="long" length="9" width="120" defaultValue="51" fixed="true">
		<dic autoLoad="true">
			<item key="11" text="门（急）诊诊断"/>
			<item key="22" text="入院主诊断"/>
			<item key="44" text="病理诊断"/>
			<item key="45" text="损伤中毒的外部原因"/>
			<item key="51" text="出院诊断"/>
		</dic>
	</item>
	<item id="ZXLB" alias="中西类别" type="int" length="1" width="60" defaultValue="1">
		<dic autoLoad="true">
			<item key="1" text="西医"/>
			<item key="2" text="中医"/>
		</dic>
	</item>
	<item id="MSZD" alias="描述诊断" type="string" length="200" mode="remote"/>
	<item id="JBBW" alias="疾病部位" type="long" length="18" >
		<dic id="phis.dictionary.clinicSite" autoLoad="true"/>
	</item>
	<item id="FJBS" alias="中医证候ID" type="long" length="18">
		<dic id="phis.dictionary.TcmSymptoms" autoLoad="true"/>
	</item>
	<item id="ZDYS" alias="诊断医生" type="string" length="10" defaultValue="%user.userId">
		<dic autoLoad="true" id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZDRQ" alias="诊断日期" type="datetime" width="100" defaultValue="%server.date.datetime"/>
	<item id="JBBM" alias="疾病编码" type="string" length="20" fixed="true"/>
	<item id="RYBQDM" alias="入院病情" type="int" length="1" width="90">
		<dic autoLoad="true">
			<item key="1" text="有明确诊断"/>
			<item key="2" text="临床未确定"/>
			<item key="3" text="情况不明"/>
			<item key="4" text="无法确定"/>
		</dic>
	</item>
	<item id="CYZGDM" alias="出院转归情况" type="int" length="1" width="90">
		<dic autoLoad="true">
			<item key="1" text="治愈"/>
			<item key="2" text="好转"/>
			<item key="3" text="未愈"/>
			<item key="5" text="死亡"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="ZNXH" alias="组内序号" type="int" length="2" display="0"/>
	<item id="JBXH" alias="疾病序号" type="long" length="18" display="0"/>
	<item id="JBFJM" alias="疾病附加码" type="string" length="20" display="0"/>
	<item id="XTSJ" alias="系统时间" type="timestamp" display="0"/>
	<item id="ZFBZ" alias="作废标志" type="int" length="1" display="0">
		<dic autoLoad="true">
			<item key="0" text="未作废"/>
			<item key="1" text="已作废"/>
		</dic>
	</item>
	<item id="ZFYS" alias="作废医生" type="string" length="10" display="0"/>
	<item id="ZFSJ" alias="作废时间" type="timestamp" display="0"/>
	<item id="JBMC" alias="疾病名称" type="string" length="200" display="0"/>
</entry>
