<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_LOG01"  alias="家医交易日志">
    <item id="log1Id" alias="操作日志id" type="string" length="20" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>
    </item>
    <item id="logTime" alias="记录时间" type="datetime" defaultValue="%server.date.today">
        <!--        <set type="exp">['$','%server.date.today']</set>-->
    </item>
    <item id="manaunitId" alias="操作机构" type="string" length="20" defaultValue="%user.manageUnit.id">
    </item>
    <item id="LogUser" alias="操作人员" type="string" length="20" defaultValue="%user.userId">
        <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
    </item>
    <item id="fphm" alias="发票号码" type="string" length="12" />
    <item id="empiId" alias="EMPI" type="String" length="32"/>
</entry>
