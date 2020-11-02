<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="HIS_AppointmentRecord" tableName="APPOINTMENT_RECORD" alias="预约挂号">
	<item id="ID" alias="识别序号" length="18" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="PATIENTNAME" alias="患者姓名" type="string" length="10" not-null="1" width="80"/>
	<item id="PATIENTCARD" alias="身份证号" type="string" length="50" not-null="1" display="0"/>
	<item id="PATIENTMOBILE" alias="患者号码" type="string" length="20" display="0"/>
	<item id="DOCTORID" alias="医生ID" type="string" length="20" display="0"/>
	<item id="HOSPITALID" alias="医院ID" type="string" length="50" display="0"/>
	<item id="SECTIONID" alias="科室ID" type="string" length="50" display="0"/>
	<item id="DOCTOR" alias="医生" type="string" length="50" width="80"/>
	<item id="HOSPITAL" alias="医院" type="string" length="50" width="150"/>
	<item id="SECTION" alias="科室" type="string" length="50" width="120"/>
    <item id="STARTTIME" alias="预约时间" type="timestamp" length="20" width="130"/>
	<item id="ENDTIME" alias="最后取号时间" type="timestamp" length="20" width="130"/>
	<item id="STATUS" alias="当前状态" type="int" length="1" defaultValue="1" renderer="onRenderder"/>
	<item id="CODEIMG" alias="二维码链接" type="int" length="20" display="0"/>
</entry>
