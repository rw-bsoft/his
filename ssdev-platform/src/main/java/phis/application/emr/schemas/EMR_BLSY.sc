<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLSY" alias="病历审阅">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1" display="0"/>
	<item id="SYQX" alias="审阅权限" type="long" length="18" not-null="1" display="0"/>
	<item id="SYYS" alias="审阅医生" type="string" length="10" not-null="1">
		<dic id="phis.dictionary.user" autoLoad="true"/>
	</item>
	<item id="SYSJ" alias="审阅时间" type="timestamp" not-null="1" width="150"/>
	<item id="JLSJ" alias="记录时间" type="timestamp" not-null="1" display="0"/>
	<item id="BZXX" alias="备注信息" type="string" length="100" not-null="1" display="0"/>
	<item id="QXJB" alias="权限级别" type="int" length="2" display="0"/>
	<item id="QMLX" alias="签名类型" type="int" length="1" display="0"/>
	<item id="QMLSH" alias="签名流水号" type="long" length="18" display="0"/>
	<item id="DLLJ" alias="段落路径" type="string" length="25" display="0"/>
	<item id="QMYS" alias="签名元素" type="string" length="25" width="120">
		<dic>
			<item key="KZRQM" text="科主任"/>
			<item key="ZRYSQM" text="主任（副主任）医师"/>
			<item key="ZZYSQM" text="主治医师"/>
			<item key="ZYYSQM" text="住院医师"/>
			<item key="ZRHSQM" text="责任护士"/>
			<item key="JXYSQM" text="进修医师"/>
			<item key="SXYSQM" text="实习医师"/>
			<item key="BABMYQM" text="病案编码员"/>
			<item key="ZKYSQM" text="质控医师"/>
			<item key="ZKHSQM" text="质控护士"/>
		</dic>
	</item>
	<item id="YSMRZ" alias="元素默认值" type="string" length="25" display="0"/>
	<item id="QMYSZ" alias="签名元素值" type="string" length="25" display="0"/>
	<item id="ZJBJ" alias="追加签名标记" type="int" length="1" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
</entry>
