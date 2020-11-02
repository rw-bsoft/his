<?xml version="1.0" encoding="UTF-8"?>

<entry id="PACS_BGJG" tableName="PACS_BGJG" alias="PACS报告结果" >
	<item id="JGID" alias="机构ID" display="0"  type="string"   length="20" not-null="1" />
	<item id="SBXH" alias="主键" type="long" display="0" update="false" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="SQID" alias="申请ID" display="0" type="long" length="18" not-null="1" />
	<item id="JCLB" alias="检查类别" display="0" type="string" length="2" not-null="1" >
		<dic>
			<item text="超声类" key="01"></item>
			<item text="放射类" key="02"></item>
		</dic>
	</item>
	<item id="JCMC" alias="检查名称"  type="string" length="255" not-null="1" fixed="true"/>
	<item id="YXSJ" alias="影像所见" xtype="textarea" type="string" length="1000" not-null="1"  height="200"  fixed="true"/>
	<item id="YXZD" alias="影像诊断" xtype="textarea" type="string" length="1000" not-null="1"  height="200"  fixed="true"/>
	<item id="BGYS" alias="报告医生"  type="string" length="50" not-null="1"  fixed="true"/>
	<item id="BGSJ" alias="报告时间" type="timestamp"  fixed="true"/>
	<item id="HZLX" alias="患者类型" display="0" type="string" length="2" not-null="1" >
		<dic>
			<item text="门诊" key="01"></item>
			<item text="急诊" key="02"></item>
			<item text="体检" key="03"></item>
			<item text="住院" key="04"></item>
		</dic>
	</item>
</entry>
