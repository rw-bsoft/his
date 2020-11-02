<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.phe.schemas.PHE_EnvironmentalEvent" alias="突发环境卫生事件相关信息表">
 <item id="RecordID" alias="突发环境卫生事件记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item> 
  <item id="reportDate" queryable="true" alias="填报日期"  not-null="1"  type="date" maxValue="%server.date.today"/>
  <item id="eventName" queryable="true" alias="事件名称"  not-null="1"  length="200" width="120"/>
  <item id="eventType" queryable="true"  alias="卫生事件类别"  not-null="1"  length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="空气污染" />
      <item key="2" text="水污染" />
      <item key="3" text="土壤污染" />
    </dic>
  </item>
   <item id="controlMeasures" not-null="1" alias="事件控制措施" colspan="2" length="50" width="150">
  	 <dic render="LovCombo">
      <item key="01" text="发布新的规章制度" />
      <item key="02" text="现场防护措施" />
      <item key="03" text="严格操作程序" />
      <item key="04" text="综合治理污染源" />
      <item key="05" text="宣传教育" />
      <item key="06" text="恢复被污染环境" />
      <item key="07" text="救助受害人员" />
      <item key="08" text="毒物鉴定分析" />
      <item key="09" text="样本采集分析" />
      <item key="10" text="其他" />
    </dic>
  </item>
  <item id="pathogenicFactor" queryable="true" alias="致病因素"  not-null="1"  length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="空气" />
      <item key="2" text="水污染" />
      <item key="3" text="土壤" />
       <item key="4" text="其他" />
    </dic>
  </item>
  <item id="air" fixed="true" alias="空气因素" length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="氯" />
      <item key="2" text="氨" />
      <item key="3" text="一氧化碳" />
       <item key="4" text="硫化物" />
    </dic>
  </item>
  <item id="water" fixed="true" alias="水污染因素" length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="生活污水" />
      <item key="2" text="医院污水" />
      <item key="3" text="农药" />
    </dic>
  </item>
  <item id="eventReason"  alias="事件发生原因"  not-null="1" length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="室内装修" />
      <item key="2" text="违章操作" />
      <item key="3" text="设备故障" />
      <item key="4" text="其他生物性污染" />
      <item key="5" text="其他室内污染" />
      <item key="6" text="其他工业污染" />
      <item key="7" text="其他原因" />
    </dic>
  </item>

  <item id="biologicReason" fixed="true" alias="其它生物性污染" length="20" width="120">
  	 <dic>
      <item key="1" text="污水排放" />
      <item key="2" text="设备故障" />
      <item key="3" text="下水堵塞" />
      <item key="4" text="无消毒措施" />
    </dic>
  </item>
  <item id="indoorPollution" fixed="true" alias="其它室内污染" length="20" width="120">
  	 <dic>
      <item key="1" text="煤气中毒" />
      <item key="2" text="室内养殖" />
    </dic>
  </item>
  <item id="industrialPollution" fixed="true" alias="其他工业污染" length="20" width="120">
  	 <dic>
      <item key="1" text="工业三废" />
    </dic>
  </item>
  <item id="otherReason" fixed="true" alias="其它原因" colspan ="2" length="20"/>
  <item id="triggerTarget" alias="引发事件污染物" length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="氯" />
      <item key="2" text="氨" />
      <item key="3" text="煤气" />
      <item key="4" text="硫化物" />
      <item key="5" text="生活污水" />
      <item key="6" text="医院污水" />
      <item key="7" text="农药" />
      <item key="8" text="其他" />
    </dic>
  </item>
  <item id="contaminatedEnviroment" alias="被污染环境" length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="大气" />
      <item key="2" text="室内空气" />
      <item key="3" text="自来水管网" />
      <item key="4" text="二次供水" />
      <item key="5" text="自来水源" />
      <item key="6" text="分散供水源" />
      <item key="7" text="土壤" />
      <item key="8" text="河流" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="treatmentProcess" alias="病人处理过程" length="20" width="150">
  	 <dic render="LovCombo">
      <item key="1" text="集中收治" />
      <item key="2" text="特异性治疗" />
      <item key="3" text="对症治疗" />
      <item key="4" text="其他处理" />
      <item key="5" text="明确诊断" />
      <item key="6" text="采样检验" />
      <item key="7" text="其他" />
    </dic>
  </item>
  <item id="accountabilityUnit" alias="责任单位" type="string" length="20" display="2" width="180"   defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item> 
 <item id="reportUnit"  queryable="true" alias="填报单位" type="string" length="20" colspan ="2" width="180" defaultValue="%user.manageUnit.id">
   <dic id="chis.@manageUnit" includeParentMinLen="6"  lengthLimit="9"  render="Tree"  rootVisible = "true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="createUser" alias="录入人" type="string" length="20" 
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createUnit" alias="录入机构" type="string" length="20" colspan ="2"
    width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="0">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="0">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
