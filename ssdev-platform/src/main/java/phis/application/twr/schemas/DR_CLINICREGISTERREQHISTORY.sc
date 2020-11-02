<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICREGISTERREQHISTORY"  alias="预约挂号记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="QUHAOMM" alias="取号密码" type="string" length="32"/>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" hidden="true" />
    <item id="GUAHAOXH" alias="挂号序号" type="string" length="32"/>
    <item id="JIUZHENSJ" alias="就诊时间段" length="20" width="120" fixed="true" type="string"/>
    <item id="FEIYONGMX" alias="费用明细" length="20" width="120" fixed="true" type="string"/>
</entry>
