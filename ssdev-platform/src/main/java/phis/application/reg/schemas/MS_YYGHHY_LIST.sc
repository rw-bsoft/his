<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YYGHHY" alias="预约挂号号源">
	<item id="HYXH" alias="号源序号" length="18" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1000"/>
		</key>
	</item>
	<item id="HYSJ" alias="号源(挂号)时间" type="timestamp" width="120"/>
	<item id="CARDID" alias="卡号" type="string" length="20" width="160" queryable="true"/>
	<item id="PATIENTNAME" alias="病人姓名" type="string" length="40" display="1" queryable="true"/>
	<item id="BRXZ" alias="挂号性质" type="string" length="4">
		<dic>
			<item key="1000" text="自费"/>
			<item key="2000" text="医保"/>
			<item key="6000" text="农合"/>
		</dic>
	</item>
	<item id="KSMC" alias="所挂科室" type="string" length="20" />
	<item id="HYZT" alias="号源状态" type="string" length="1">
		<dic id="phis.dictionary.HYZT" />
	</item>
	<item id="XJJE" alias="挂号金额" type="double" length="10" precision="2" width="80"/>
	<item id="YSMC" alias="预约医生" type="string" length="20" />
	<item id="ZBLB" alias="值班类别" type="string" length="1">
		<dic>
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
</entry>
