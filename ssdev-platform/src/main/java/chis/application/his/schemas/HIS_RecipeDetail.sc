<?xml version="1.0" encoding="UTF-8"?>

<entry alias="门诊处方明细" >
  <!-- <item ref="c.createDate" display="1" /> -->
  <item ref="b.formulation" display="1" />
  <item ref="c.createDate" display="1" />
  <item id="recipeDetailId" alias="记录序号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true" />
  <item id="recipeId" alias="处方记录号" type="string" length="16"
    hidden="true" />
  <item id="empiId" alias="empiid" type="string" length="32"
    fixed="true" notDefaultValue="true" hidden="true" />
  <item id="groupNo" alias="药品组号" type="int" hidden="true"/>
  <item id="drugType" alias="药品类型" type="string" length="1" hidden="true"/>
  <item id="drugClass" alias="药品分类" type="string" length="30" hidden="true"/>
  <item id="drugCode" alias="药品编号" type="string" length="30" hidden="true"/>
  <item id="drugName" alias="药物名称" type="string" length="80" />
  <item ref="b.formulation" display="1" />
  <item id="drugSpec" alias="药品规格" type="string" length="50" hidden="true"/>
  <item id="drugMedi" alias="药物剂型" type="string" length="2" hidden="true"/>
  <item id="useDays" alias="用药天数" type="int" />
  <item id="frequency" alias="使用频率" type="string" length="2" />
  <item id="useUnits" alias="剂量单位" type="string" length="10" />
  <item id="onesDose" alias="一次剂量" type="bigDecimal" length="8"
    precision="2" />
  <item id="totalDose" alias="总剂量" type="bigDecimal" length="8"
    precision="2" />
  <item id="packgeUnits" alias="包装单位" type="string" length="6" hidden="true"/>
  <item id="totalCount" alias="总数量" type="bigDecimal" length="8"
    precision="2" />
  <item id="method" alias="使用途径" type="string" length="2" hidden="true"/>
  <item id="startDate" alias="开始时间" type="date" hidden="true"/>
  <item id="stopDate" alias="停止时间" type="date" hidden="true"/>
  <item id="drugPrice" alias="药品价格" type="bigDecimal" length="8"
    precision="2" hidden="true"/>
  <item id="drugMoney" alias="药品金额" type="bigDecimal" length="8"
    precision="2" hidden="true"/>
  <item id="selfScale" alias="自负比例" type="bigDecimal" length="6"
    precision="2" hidden="true"/>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
    defaultValue="%server.date.date" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <relations>
    <relation type="children" entryName="chis.application.pub.schemas.PUB_DrugDirectory">
      <join parent="drugCode" child="drugCode" />
    </relation>
    <relation type="children" entryName="chis.application.his.schemas.HIS_Recipe">
      <join parent="recipeId" child="recipeNo" />
    </relation>
  </relations>
</entry>
