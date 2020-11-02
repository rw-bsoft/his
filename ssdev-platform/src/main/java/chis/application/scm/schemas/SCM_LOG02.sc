<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_LOG02" alias="家医交易日志">
    <item id="log2Id" alias="明细id" type="string" length="20" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>
    </item>
    <item id="log1Id" alias="概况id" type="string" length="16"/>
    <item id="SPIID" alias="任务id" type="string" length="20"/>
    <item id="sciId" alias="增值主键" type="long" length="18"/>
    <item id="scinId" alias="增值项目主键" type="long" length="18"/>
    <item id="serviceTimes" alias="剩余服务次数" type="int" length="3">
    </item>
    <item id="costTimes" alias="消耗次数" type="int" length="3" />
    <item id="unitPrice" alias="单价" type="double" length="8" precision="2"/>
    <item id="totPrice" alias="总价" type="double" length="8" precision="2"/>
</entry>
