<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_DeliveryOnRecord" alias="产妇分娩记录表">
  <item id="DRID" alias="表单编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>
  <item  ref="b.personName"  display="1" queryable="true" />
  <item  ref="b.sexCode" display="1" queryable="false"/>
  <item  ref="b.birthday" display="1" queryable="true" />
  <item  ref="b.mobileNumber" display="1" queryable="true" />
  <item ref="c.homeAddress" display="1" queryable="true" width="300"/>
  <item ref="c.homeAddress_text"  display="0"/>
  <item  ref="b.idCard" display="0"/> 
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
     <relation type="children" entryName="chis.application.mhc.schemas.MHC_PregnantRecord">
      <join parent="pregnantId" child="pregnantId" />
    </relation>
  </relations>
  
  <item id="empiId" alias="empiId" type="string" length="32" display="0"/>
  <item id="pregnantId"  display="0" alias="孕妇档案标识" type="string" length="16"/>
  <item id="ZYH" display="0" alias="住院号" length="10"/>
  <item id="presentAddress" alias="现住址" length="100" display="0"/>
 
  <item id="deliveryWay"  alias="分娩方式" not-null="true" type="string" length="1" >
    <dic>
      <item key="1" text="自然分娩" />
      <item key="2" text="吸引" />
      <item key="3" text="产钳" />
      <item key="4" text="臀助产" />
      <item key="5" text="剖宫产" />
      <item key="6" text="其他" />
    </dic>
  </item>
  <item id="otherDeliveryWay" alias="其它分娩方式" length="20"  colspan="2" fixed="true"/>
  <item id="sideCut" alias="有无侧切" type="string" length="1" >
    <dic>
      <item key="1" text="有" />
      <item key="2" text="无" />
    </dic>
  </item>
  <item id="totalStage" alias="总产程(min）" not-null="true" type="int" length="4"/>
  <item id="firstStage" alias="第一产程（min）" type="int" length="4" width="100"/>
  <item id="secondStage" alias="第二产程（min）" type="int" length="4" width="100"/>
  <item id="lastStage" alias="第三产程（min）" type="int" length="4" width="100"/>
  <item id="deliveryBleeding" alias="产时出血量(ml)" type="int" length="4" width="100"/>
  <item id="totalBleeding" alias="总出血量(ml)" not-null="true"  type="int" length="4" width="100"/>
  <item id="postpartum2HourBleeding" alias="产后两小时出血量" type="int" length="4" width="100"/>
  <item id="perineumSituation"  alias="会阴情况" not-null="true"  type="string" length="1" >
    <dic> 
      <item key="1" text="完整" />
      <item key="2" text="裂伤" />
      <item key="3" text="切开" />
    </dic>
  </item>
  <item id="PID" alias="会阴裂伤程度"   type="string" length="1" >
    <dic>
      <item key="1" text="无" />
      <item key="2" text="Ⅰ度" />
      <item key="3" text="Ⅱ度" />
      <item key="4" text="Ⅲ度" />
    </dic>
  </item>
  <item id="suture" alias="缝合针"  type="string" length="1" >
    <dic>
      <item key="1" text="外针" />
      <item key="2" text="内针" />
    </dic>
  </item>
  <item id="isConjuncture"   alias="是否危急" type="string" length="1" >
    <dic>
      <item key="1" text="是" />
      <item key="2" text="否" />
    </dic>
  </item>
  <item id="ConjunctureDesc" alias="危急说明" colspan="2" length="100" fixed="true" width="120"/>
  <item id="hasComplication"   alias="有无产时并发症" not-null="true" type="string" length="1" width="120">
    <dic>
      <item key="1" text="有" />
      <item key="2" text="无" />
    </dic>
  </item>
  <item id="ComplicationDesc" alias="产时并发症描述" colspan="2" fixed="true" length="100" width="120"/>
  <item id="SBP" alias="产后收缩压(mmHg)" not-null="true" type="int" length="20" width="120"/>
  <item id="DBP" alias="产后舒张压(mmHg)" not-null="true" type="int" length="20" width="120"/>
  <item id="nurseTime" alias="开奶时间:产后(分)" type="int" />
  <item id="deliveryOutcome" alias="产妇结局"   not-null="true" type="string" length="1">
    <dic>
      <item key="1" text="存活" />
      <item key="2" text="死亡" />
    </dic>
  </item> 
  <item id="diePeriod" alias="死亡时期"   type="string" length="1">
    <dic>
      <item key="1" text="产前 " />
      <item key="2" text="产时" />
      <item key="3" text="产后" />
    </dic>	 
  </item>
  
  <item id="numberofply" alias="生产胎数" not-null="true" type="int" length="1">
    <dic>
      <item key="1" text="单胎" />
      <item key="2" text="双胎" />
      <item key="3" text="三胎及以上" />
    </dic>
  </item>
  <item id="deliver" alias="接生者" not-null="true" length="20"/>
  <item id="deliveryUnit" alias="分娩单位" not-null="true" length="22" colspan="3" />
  <item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
    defaultValue="%server.date.date">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="createUser" alias="录入员工" type="string" update="false" length="20" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createUnit" alias="录入单位" type="string" update="false" length="22"  fixed="true" defaultValue="%user.manageUnit.id" width="150">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="22" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="1">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.date" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="administrativeId" alias="行政区划代码" type="string" length="20" update="false"
    display="0" defaultValue="%user.manageUnit.administrativeId">
    <set type="exp">['$','%user.manageUnit.administrativeId']</set>
  </item>
</entry>
