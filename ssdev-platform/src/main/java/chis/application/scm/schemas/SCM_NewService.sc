<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_NewService" alias="新增服务项目表">
    <item id="serviceId" alias="服务记录id" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
    <item id="taskId" alias="对应任务id" type="long" length="18" display="0"/>
    <item id="empiId" alias="服务对象id" type="string" length="32" display="0"/>
    <item id="serviceObj" alias="服务对象" type="string" length="50" not-null="1" fixed="true"/>
    <item id="serviceOrg" alias="服务机构" type="string" length="50" defaultValue="%user.manageUnit.id">
        <dic id="chis.@manageUnit"  includeParentMinLen="6" render="Tree"/>
    </item>
    <item id="serviceTeam" alias="签约团队" type="string" length="50" not-null="1" width="180" defaultValue="%user.manageUnit.id">
        <dic id="chis.dictionary.user_scm" render="Tree"  parentKey="%user.userId"/>
        <!-- <set type="exp">['$','%user_scm.manageUnit']</set> -->
    </item>
    <item id="servicer" alias="服务人" type="string" length="50" not-null="1" defaultValue="%user.userId">
        <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
    </item>
    <item id="servicePackId" alias="服务包id" type="string" length="50"  display="0"/>
    <item id="servicePack" alias="服务包" type="string" length="100" not-null="1" fixed="true"/>
    <item id="serviceItemsId" alias="服务项id" type="string" length="50"  display="0"/>
    <item id="serviceItems" alias="服务项" type="string" length="100" not-null="1" fixed="true"/>
    <item id="SCIID" alias="增值主键" type="long" length="18"  display="0"/>
    <item id="SCINID" alias="增值项目主键" type="long" length="18"  display="0"/>
    <item id="serviceMode" alias="服务方式" type="string" length="50" not-null="1" defaultValue="0" >
        <dic>
            <item key="0" text="门诊服务"/>
            <item key="1" text="电话服务"/>
            <item key="2" text="家庭服务"/>
        </dic>
    </item>
    <item id="serviceDate" alias="服务时间" type="date" not-null="1" defaultValue="%server.date.today"/>
    <item id="gridAddress" alias="服务地址" type="string" length="100"  display="0">
        <dic id="chis.dictionary.areaGrid"  render="Tree" />
    </item>
    <item id="detailedAddress" alias="详细地址" type="string" length="150"  display="0"/>
    <item id="SBXH" alias="挂号识别序号" type="long" length="18"  display="0"/>
    <item id="SCID" alias="签约记录id" type="long" length="18"  display="0"/>
    <item id="SPIID" alias="服务包项id" type="string" length="20"  display="0"/>
    <item id="createTime" alias="创建时间" type="datetime" not-null="1" defaultValue="%server.date.datetime" display="0" update="false"/>
    <item id="createUser" alias="创建人" type="string" length="20" not-null="1" defaultValue="%user.userId" display="0">
        <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
    </item>
    <item id="serviceDesc" alias="服务说明" type="string" length="200" xtype="textarea" colspan="2" rowspan="2"/>
</entry>
