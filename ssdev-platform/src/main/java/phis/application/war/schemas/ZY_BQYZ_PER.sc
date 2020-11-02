<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱" sort="YZPX ASC">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="YZMC" alias="医嘱名称" type="string" mode="remote" length="100" width="160"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" width="60" />
	<item id="KSSJ" alias="开嘱时间" type="datetimefield" width="130" renderer="dateFormat" />
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" nullToValue="0" not-null="1" width="70"/>
	<item id="YSGH" alias="开嘱医生" type="string" length="10" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true"/>
	</item>
</entry>
