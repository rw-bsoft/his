<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_NewServiceQuery" alias="手动履约服务记录查询">
    <item id="empiId" alias="服务对象id" type="string" length="32" display="0"/>
    <item id="serviceId" alias="服务记录id" type="long" length="50" display="0"/>
    <item id="serviceDate" alias="服务时间" type="date" />
    <item id="servicePack" alias="服务包" type="string" length="100" not-null="1" width="160"/>
    <item id="serviceItems" alias="服务项" type="string" length="100" width="180"/>
    <item id="serviceTeam" alias="签约团队" type="string" length="50"  display="0"/>
    <item id="serviceObj" alias="服务对象" type="string" length="50" not-null="1" />
    <item id="serviceOrg" alias="服务机构" type="string" length="50"  display="0"/>
    <item id="serviceOrgName" alias="服务机构" type="string" length="50" width="180"/>
    <item id="serviceTeamName" alias="签约团队" type="string" length="50" width="120"/>
    <item id="servicer" alias="服务人" type="string"  display="0" />
    <item id="servicerName" alias="服务人" type="string" length="50" />
    <item id="servicePackId" alias="服务包id" type="string" length="50"  display="0"/>
    <item id="serviceItemsId" alias="服务项id" type="string" length="50"  display="0"/>
    <item id="SCIID" alias="增值主键" type="long" length="18"  display="0"/>
    <item id="SCINID" alias="增值项目主键" type="long" length="18"  display="0"/>
    <item id="serviceMode" alias="服务方式" type="string"  display="0"/>
    <item id="serviceModeName" alias="服务方式" type="string" length="50"/>
    <item id="gridAddressName" alias="服务地址" type="string" length="100" display="0"/>
    <item id="gridAddress" alias="服务地址" type="string" length="100"  display="0"/>
    <item id="detailedAddress" alias="详细地址" type="string" length="150" display="0"/>
    <item id="serviceDesc" alias="服务说明" type="string" length="200" width="155" renderer="showColor"/>
	<item id="price" alias="原价格" type="string" length="50" display="0"/>
	<item id="realPrice" alias="实际价格" type="string" length="50" display="0"/>
	<item id="diffPrice" alias="减免" type="string" length="50"/>
    <item id="dataSource" alias="数据来源" type="string"  display="0"/>
    <item id="SCID" alias="签约记录id" type="long" length="18"  display="0"/>
    <item id="SPIID" alias="服务包项id" type="string" length="20"  display="0"/>
    <item id="createTime" alias="创建日期" type="datetime" width="145"/>
    <item id="createUserName" alias="创建人" type="string" length="20"/>
</entry>
