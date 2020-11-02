<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICRECORDLHISTORY" tableName="DR_CLINICRECORDLHISTORY" alias="住院转诊记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" hidden="true" />
    <item id="MZHM" alias="门诊号码" type="string" length="32"/>
    <item id="BINGRENXM" alias="病人姓名" length="10" fixed="true" type="string"/>
    <item id="ZHUANZHENDH" alias="转诊单号" length="20" fixed="true" type="string"/>
    <item id="STATUS" alias="转诊状态" length="10" fixed="true" type="string" />
    <item id="SHENQINGJGMC" alias="申请机构" length="20" width="180" fixed="true" type="string" />
    <item id="SHENQINGYS" alias="申请医生" length="20" fixed="true"  type="string"/>
    <item id="SHENQINGRQ" alias="申请日期" type="date" length="20" fixed="true" update="false" defaultValue="%server.date.date" not-null="true" />
</entry>
