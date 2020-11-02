<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YYGHHY" alias="预约挂号号源">
	<item id="HYXH" alias="号源序号" length="18" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1000"/>
		</key>
	</item>
	<item id="ZBLB" alias="值班类别" type="string" length="1" />
	<item id="HYKS" alias="号源科室" type="long" length="18" />
	<item id="HYJG" alias="号源机构" type="string" length="20" />
	<item id="HYYS" alias="号源医生" type="string" length="20" />
	<item id="HYSJ" alias="号源时间" type="timestamp" />
	<item id="HYZT" alias="号源状态" type="string" length="1" />
	<item id="BRID" alias="病人编号" type="long" length="18" />
	<item id="SFZH" alias="身份证号" type="string" length="18" />
	<item id="THSJ" alias="退号时间" type="timestamp" />
</entry>
