<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServicePackageItems" alias="签约服务包项目表">
    <item id="SPIID" alias="服务包项目编号" type="string" length="20" generator="assigned" pkey="true" width="120"
          hidden="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>
    </item>
    <item id="SPID" alias="所属服务包" type="string" length="20" hidden="true"/>
    <item id="itemCode" alias="服务项目编码" type="string" length="20" hidden="true"/>

    <item ref="b.itemCode" display="0" width="100"/>
    <item ref="b.parentCode" display="0" width="100"/>
    <item ref="b.itemName" display="1" width="150" readOnly="true"/>
    <item id="LOGOFF" alias="注销标志" type="int" display="1" length="1" defaultValue="0">
        <dic>
            <item key="0" text="启用"/>
            <item key="1" text="禁用"/>
        </dic>
    </item>
    <item id="serviceTimes" display="1" alias="服务次数" type="int" length="3"/>

    <!-- item ref="b.serviceTimes" display="1"/ -->
    <item ref="b.price" display="1" fixed="readOnly"/>
    <item ref="b.realPrice" display="1" readOnly="true"/>
    <!--<item ref="b.intendedPopulation" display="1" readOnly="true"/>-->
    <item ref="b.startUsingDate" display="1" readOnly="true"/>
    <item ref="b.itemNature" display="1" readOnly="true"/>
    <!--<item ref="b.itemType" display="2" readOnly="true"/>-->
    <item ref="b.isBottom" display="2" readOnly="true"/>
    <item ref="b.isOneWeekWork" display="1" readOnly="true"/>
    <!--<item ref="b.moduleAppId" display="1" readOnly="true"/>-->


    <item ref="b.intro" display="1" width="400" readOnly="true"/>
    <item ref="b.remark" display="1" readOnly="true"/>
    <item ref="b.fyxh" display="1" readOnly="true"/>

    <item id="status" alias="服务项目状态" type="string" length="1" defaultValue="2" hidden="true">
        <dic>
            <item key="1" text="完成"/>
            <item key="2" text="未完成"/>
        </dic>
    </item>

    <relations>
        <relation type="children" entryName="chis.application.scm.schemas.SCM_ServiceItems_his">
            <join parent="itemCode" child="itemCode"/>
        </relation>
    </relations>
</entry>