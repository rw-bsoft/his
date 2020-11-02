<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServiceItems" alias="签约服务项目表">
    <item id="itemCode" alias="服务项目编码" type="string" length="20" pkey="true" fixed="true" display="2" group="基础信息"/>
    <item id="parentCode" alias="父节点编码" type="string" length="20" not-null="1" fixed="true" display="2" group="基础信息"/>
    <item id="itemName" alias="服务项目名" type="string" length="50" not-null="1" queryable="true" width="200" colspan="2" group="基础信息"/>
    <item id="ztmc" alias="组套名称" type="string" length="50" queryable="true"/>
    <item id="pyCode" alias="拼音编码" type="string" length="50" fixed="true" hidden="true" group="基础信息">
    </item>
    <item id="price" alias="价格" type="double" length="6" precision="2" group="基础信息"/>
    <item id="discount" alias="折扣(%)" type="double" length="6" precision="2" group="基础信息"/>
    <item id="serviceTimes" alias="服务次数" type="int" length="3" fixed="true" group="基础信息"/><!--此处服务次数用于标记减免次数  group="基础信息" -->
    <item id="realPrice" alias="服务项单价" type="double" length="6" precision="2" group="基础信息"/>
    <item id="hisZFBL" alias="医疗支付比例" defaultValue="0" type="double" length="6" precision="2" group="基础信息"/>
    <item id="itemNature" alias="项目性质" type="string" length="1" defaultValue="1" group="基础信息">
        <dic render="Tree" onlySelectLeaf="false">
            <item key="1" text="基础服务"/>
            <item key="2" text="增值服务">
                <!--<item key="21" text="医疗-药品"/>-->
                <!--<item key="22" text="医疗-费用"/>-->
                <!--<item key="23" text="医疗-组套"/>-->
            </item>
        </dic>
    </item>

    <item id="itemType" alias="项目类型" type="string" length="1" not-null="1" fixed="true" queryable="true" group="基础信息">
        <dic>
            <item key="1" text="业务分类"/>
            <item key="2" text="项目分类"/>
            <item key="3" text="服务分类"/>
            <item key="4" text="服务项目"/>
        </dic>
    </item>
    <item id="isBottom" alias="底节点" type="string" length="1" group="基础信息">
        <dic id="chis.dictionary.yesOrNo"/>
    </item>


    <item id="intendedPopulation" alias="适应人群" type="string" colspan="2" length="1" not-null="1" defaultValue="9" group="公卫信息">
        <dic id="chis.dictionary.intendedPopulation"/>
    </item>

    <item id="startUsingDate" alias="启用时间" type="date" hidden="true"/>

    <item id="serviceTable" alias="业务表" type="string" length="200" hidden="true">
        <dic id="chis.dictionary.businessTable_TZ" render="Tree" onlySelectLeaf="true"/>
    </item>
    <item id="serviceFields" alias="业务字段集" type="string" length="2000" fixed="true" hidden="true">
        <dic render="LovCombo">
            <item key="0" text="请先选择业务表"/>
        </dic>
    </item>
    <item id="moduleAppId" alias="公卫路径" type="string" length="200" colspan="2" width="180" group="公卫信息">
        <dic id="chis.dictionary.CHISService" render="Tree" onlySelectLeaf="true"/>
    </item>
    <item id="isOneWeekWork" alias="合约生效周内完成" type="string" length="1" hidden="true">
        <dic id="chis.dictionary.yesOrNo"/>
    </item>

    <item id="intro" alias="简介" type="string" xtype="textarea" length="2000" colspan="2" width="400" group="公卫信息"/>
    <item id="remark" alias="备注信息" type="string" xtype="textarea" length="2000" colspan="2" width="300" group="公卫信息"/>
    <item id="fyxh" alias="医疗费用序号" type="string"  length="32" display="0" group="公卫信息"/>
</entry>
