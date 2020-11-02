<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICXXEQUIPMENTHISTORY" tableName="DR_CLINICXXEQUIPMENTHISTORY" alias="设备预约详细记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" display="0">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" display="0" />
    <item id="BINGRENXM" alias="病人姓名" type="string" length="32"/>
    <item id="YUYUERQ" alias="转诊日期" type="string" length="20" width="120" fixed="true"/>
    <item id="JIANCHAYYMC" alias="检查医院名称" type="string" length="30" fixed="true" />
    <item id="JIANCHAXMLX" alias="检查类别" type="string" length="20" width="120" fixed="true"/>
    <item id="YINGXIANGFX" alias="影像方向" type="string" length="20" width="120" fixed="true"/>
    <item id="JIANCHABWMC" alias="检查部位名称" type="string" length="20" fixed="true" />
    <item id="JIANCHASBMC" alias="检查设备名称" type="string" length="20" fixed="true" />
    <item id="JIANCHASBDD" alias="检查地点" type="string" length="32" />
    <item id="JIANCHAXMMC" alias="检查项目名称" type="string" length="32"/>
    <item id="YUYUESF" alias="预约收费" type="string" length="20" width="120" fixed="true"/>
    <item id="YUYUESQDBH" alias="检查申请单号" type="string" length="30" fixed="true" />
    <item id="BINGRENMZH" alias="门诊号码" type="string" length="20" width="120" fixed="true"/>
    <item id="YUYUEH" alias="预约号" type="string" length="20" width="120" fixed="true"/>
    <item id="YIQIYUYUEH" alias="仪器预约号" type="string" length="30" fixed="true" />
    <item id="YUYUESJ" alias="预约时间" type="string" length="20" width="120" fixed="true"/>
</entry>
