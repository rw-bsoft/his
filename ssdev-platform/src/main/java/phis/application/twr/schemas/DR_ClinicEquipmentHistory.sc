<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICXXEQUIPMENTHISTORY" tableName="DR_CLINICXXEQUIPMENTHISTORY" alias="设备预约记录">
	<!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" hidden="true" />
    <item id="YUYUEH" alias="预约号" type="string" length="32"/>
    <item id="YUYUESQDBH" alias="预约申请单编号" length="20" width="120" fixed="true" type="string"/>
    <item id="YUYUERQ" alias="预约日期" type="string" length="20" fixed="true" update="false" not-null="true" />
    <item id="YUYUESJ" alias="预约时间" type="string" length="10" fixed="true" update="false" not-null="true" />
</entry>
