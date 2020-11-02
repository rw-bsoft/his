<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_SignContractCheckPackage_1" tableName="SCM_ServicePackageItems" sort="b.sortNumberSP,a.sortNumberSPI"   alias="签约服务包项目表">
    <item id="SPIID" alias="服务包项目编号" type="string" length="20" generator="assigned" pkey="true" distinct="true"  width="120" not-null="1"
          hidden="true" display="0">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>
    </item>
    <item id="SPID" alias="所属服务包" type="string" length="20" display="1" fixed="true"/>
    <item id="itemCode" alias="服务项目编码" type="string" length="20" display="0" hidden="true"/>
    <item ref="c.itemName" alias="服务 包/项目 名" display="1" width="180" queryable="false" fixed="true"/>
    <item id="ztmc" alias="组套名称" type="string" width="180" length="50" display="1" fixed="true"/>
    <item ref="b.packageIntendedPopulation" width="75" display="0" fixed="true"/>
    <item ref="d.SERVICETIMES" alias="服务次数" type="int" length="3" display="1" width="75"  readOnly="true" />
    <item ref="b.packageName" display="0" hidden="true"/>
    <item ref="b.intro" display="0" hidden="true"/>
   <item ref="c.price" alias="单价" display="0" readOnly="true" fixed="true"/>
    <item ref="c.discount" display="0" readOnly="true" fixed="true"/>
	 <item ref="c.realPrice"  alias="折扣单价"  display="1"  width="75"  readOnly="true" fixed="true"/>
    <item ref="c.hisZFBL" display="1"  width="75"  readOnly="true" hidden="true"/>
     <item ref="b.remark" display="1" width="300" readOnly="true" hidden="true"/>    
    <item ref="b.LOGOFF" alias="包启用标志" display="0" width="200" queryable="false" hidden="true"/>
    <item id="LOGOFF" alias="项目启用标志" display="0" width="200" queryable="false" hidden="true"/>
    <item ref="c.intendedPopulation" display="1" width="150" readOnly="true" hidden="true"/>
    <item ref="c.intro" display="1" width="500" readOnly="true" fixed="true"/>
    <item ref="c.moduleAppId" display="2" />
    <item ref="c.fyxh" display="0" readOnly="true" hidden="true"/>
    <item ref="b.SFXM" display="0" readOnly="true" hidden="true"/>
    <item ref="b.isUsePrice" display="0" readOnly="true" hidden="true"/>
    <item ref="b.spRealPrice" display="0" readOnly="true" hidden="true"/>
     <item ref="d.SCSPID" display="0" readOnly="true" hidden="true"/>
    <item ref="d.SCID" display="0" readOnly="true"  hidden="true"/>
    <relations>
        <relation type="parent" entryName="chis.application.scm.schemas.SCM_ServicePackage">
            <join parent="SPID" child="SPID"/>
        </relation>
        <relation type="children" entryName="chis.application.scm.schemas.SCM_ServiceItems">
            <join parent="itemCode" child="itemCode"/>
        </relation>
        <relation type="parent" entryName="chis.application.scm.schemas.SCM_SignContractPackage">
            <join parent="SPIID" child="SPIID"/>
        </relation>
    </relations>
</entry>