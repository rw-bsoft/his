<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱" sort="YZPX ASC">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="SRKS" alias="开嘱病区" type="string" length="20">
		<dic id="phis.dictionary.department_bq" autoLoad="true"/>
	</item> 
	<item id="SYBZ" alias="状态" type="int">
		<dic id="phis.dictionary.submit" autoLoad="true"/>
	</item>
	<item id="YFSB" alias="药房名称" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.wardPharmacy" autoLoad="true"/>
	</item>
	<item id="YZMC" alias="医嘱名称" type="string" mode="remote" length="100" width="160"/>
	<item id="KSSJ" alias="开嘱时间" type="datetimefield" width="120" renderer="dateFormat" />
	<item id="TZSJ" alias="停嘱时间" type="datetimefield" renderer="dateFormat" width="120" fixed="true" />
	<item id="YPJL" alias="药品剂量" type="string" display="0" virtual="true"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" min="0.01" max="9999.99" not-null="1" width="60" defaultValue="1"/>
	<item id="YPYF" alias="途径" type="long" length="18" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.drugMode"/>
	</item>
	<item id="SYPC" alias="频次" type="string" length="6" width="60">
		<dic id="phis.dictionary.useRate" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="YSGH" alias="医生" type="string" length="10" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true"/>
	</item>
</entry>
