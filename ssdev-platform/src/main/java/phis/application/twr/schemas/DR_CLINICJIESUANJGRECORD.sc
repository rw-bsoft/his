<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICJIESUANJGRECORD" tableName="DR_CLINICJIESUANJGRECORD" alias="挂号结算结果记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" hidden="true" />
    <item id="MZHM" alias="门诊号码" type="string" length="32"/>
    <item id="GUAHAOID" alias="挂号ID" type="string" length="30" fixed="true" />
    <item id="QUHAOMM" alias="取号密码" type="string" length="20" width="120" fixed="true"/>
    <item id="ZIFEIJE" alias="自费金额" type="string" length="20" width="120" fixed="true"/>
    <item id="ZILIJE" alias="自理金额" type="string" length="20" fixed="true" />
    <item id="FEIYONGZE" alias="费用ZE" type="string" length="20" fixed="true" />
    <item id="ZIFUJE" alias="自付金额" type="string" length="32" hidden="true" />
    <item id="DAISHOUJE" alias="代收金额" type="string" length="32"/>
    <item id="YOUHUIJE" alias="优惠金额" type="string" length="20" width="120" fixed="true"/>
    <item id="YIYUANCDJE" alias="医院CD金额" type="string" length="30" fixed="true" />
    <item id="BAOXIAOJE" alias="报销金额" type="string" length="20" width="120" fixed="true"/>
    <item id="XIANJINZF" alias="现金自付" type="string" length="32" hidden="true" />
    <item id="DONGJIEJE" alias="冻结金额" type="string" length="32"/>
</entry>
