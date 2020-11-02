<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.phe.schemas.PHE_RelevantInformation" alias="突发公共卫生事件相关信息报告卡">
  <item id="RecordID" alias="突发公共卫生事件相关信息记录编号" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item> 
  <item id="reportCatagory" not-null="1"  alias="报告类别" type="string" length="1" >
  	 <dic>
      <item key="1" text="初步报告" />
      <item key="2" text="进程报告" />
      <item key="3" text="结案报告" />
    </dic>
  </item>
  <item id="reportNumber" type="int" fixed="true" length="2"  alias="报告次数"/>
  <item id="reportUser" alias="报告人" type="string" length="20" not-null="1"  defaultValue="%user.userId" display="2">
    <dic id="chis.dictionary.user" parentKey="%user.manageUnit.id" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
  </item>
  <item id="reportDate" alias="填报日期"  queryable="true" not-null="1"  type="date" maxValue="%server.date.today"/>
  
  <item id="contact" alias="联系电话" not-null="1" length="20"/>
  <item id="eventName" alias="事件名称" queryable="true" not-null="1" length="20"/>
  <item id="messageType" alias="信息类别" not-null="1" queryable="true" length="50" width="120">
  <dic render="LovCombo">
      <item key="01" text="传染病" />
      <item key="02" text="食物中毒" />
      <item key="03" text="职业中毒" />
      <item key="04" text="其它中毒事件" />
      <item key="05" text="环境卫生" />
      <item key="06" text="免疫接种" />
      <item key="07" text="群体性不明原因疾病" />
      <item key="08" text="医疗机构内感染" />
      <item key="09" text="放射性卫生" />
      <item key="10" text="其它" />
    </dic>
  </item>
  	
  <item id="eventLevel" alias="事件等级" queryable="true" not-null="1" type="string" length="1">
  	<dic>
  	  <item key="1" text="特别重大" />
      <item key="2" text="重大" />
      <item key="3" text="较大" />
      <item key="4" text="一般" />
      <item key="5" text="未分级" />
      <item key="6" text="非突发事件" />
   </dic>
  </item>
  <item id="reportArea" alias="报告地区" type="string" length="25" not-null="1"
  	  width="220" colspan="2" anchor="100%"  queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="8" filterMin="10" minChars="4"
			 filterMax="25" render="Tree" onlySelectLeaf="false"/>
  </item> 
   <item id="eventArea" alias="发生地区" type="string" length="25" not-null="1"
  	  width="220" colspan="2" anchor="100%"  queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4"
			 filterMax="25" render="Tree" onlySelectLeaf="false"/>
  </item>
  <item id="detailAdreess" alias="详细地点"   type="string" length="25" not-null="1"
  	  width="250" colspan="2" anchor="100%"  queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="4" filterMin="6" minChars="4"
			 filterMax="6" render="Tree" onlySelectLeaf="true"/>
  </item>
  <item id="confirmLevelTime" alias="确认分级时间" type="date" maxValue="%server.date.today"/>
  <item id="correctionLevelTime" alias="订正分级时间" type="date" maxValue="%server.date.today"/>
  <item id="primaryDiagnosis" alias="初步诊断" not-null="1" colspan="3" length="200" maxValue="%server.date.today"/>
  <item id="primaryDiagnosisTime" alias="初步诊断时间" not-null="1" type="date" maxValue="%server.date.today"/>
  <item id="correctionDiagnosis" alias="订正诊断"  colspan="3"  length="200" maxValue="%server.date.today"/>
  <item id="correctionDiagnosisTime" alias="订正诊断时间" type="date" maxValue="%server.date.today"/>
  <item id="eventAdreess"  not-null="1" colspan="2" alias="发生场所" length="50" width="150">
  	<dic render="LovCombo">
  		 <item key="01" text="学校" />
  		 <item key="02" text="医疗卫生机构" />
  		 <item key="03" text="家庭" />
  		 <item key="04" text="宾馆饭店写字楼" />
  		 <item key="05" text="餐饮服务单位" />
  		 <item key="06" text="交通运输工具" />
  		 <item key="07" text="菜场、商场或超市" />
  		 <item key="08" text="车站、码头或机场" />
  		 <item key="09" text="党政机关办公场所" />
  		 <item key="10" text="企事业单位办公场所" />
  		 <item key="11" text="大型厂矿企业生产场所" />
  		 <item key="12" text="中小型厂矿企业生产场所" />
  		 <item key="13" text="城市住宅小区" />
  		 <item key="14" text="城市其它公共场所" />
  		 <item key="15" text="农村村庄" />
  		 <item key="16" text="农村农田野外" />
  		 <item key="17" text="其它重要公共场所" />
  	</dic>
  </item>
  <item id="medicalOrganizationsType" fixed="true" alias="医疗机构类别" type="string" length="1" display="2">
  <dic>
  	     <item key="1" text="公办医疗机构" />
  		 <item key="2" text="疾病预防控制机构" />
  		 <item key="3" text="采供血机构" />
  		 <item key="4" text="检验检疫机构" />
  		 <item key="5" text="其它及私立机构" />
  </dic>
  </item>
  <item id="medicalOrganizationsDep" fixed="true" alias="感染部门" type="string" length="1" display="2">
  	 <dic>
  	     <item key="1" text="病房" />
  		 <item key="2" text="手术室" />
  		 <item key="3" text="门诊" />
  		 <item key="4" text="化验室" />
  		 <item key="5" text="药房" />
  		 <item key="6" text="办公室" />
  		 <item key="7" text="治疗室" />
  		 <item key="8" text="特殊检查室" />
  		 <item key="9" text="其他场所" />
  </dic>
  </item>
  <item id="schoolType" alias="学校类别" fixed="true" type="string" length="1" display="2">
  	 <dic> 
  	 	 <item key="1" text="托幼机构" />
  	     <item key="2" text="小学" />
  		 <item key="3" text="中学" />
  		 <item key="4" text="大、中专院校" />
  		 <item key="5" text="综合类学校" />
  		 <item key="6" text="其它" />
  </dic>
  </item>
  <item id="eventSource"  not-null="1" alias="事件信息来源" length="50" width="150">
  		 <dic render="LovCombo" >
  	     <item key="01" text="属地医疗机构" />
  		 <item key="02" text="外地医疗机构" />
  		 <item key="03" text="报纸" />
  		 <item key="04" text="电视" />
  		 <item key="05" text="特服号电话95120" />
  		 <item key="06" text="互联网" />
  		 <item key="07" text="市民电话报告" />
  		 <item key="08" text="上门直接报告" />
  		 <item key="09" text="本系统自动预警产生" />
  		 <item key="10" text="广播" />
  		 <item key="11" text="填报单位人员目睹" />
  		 <item key="12" text="其它" />
  </dic> 
  </item>
  <item id="eventSourceDetail" alias="信息来源详细" colspan ="2" length="200" width="150"/>
  <item id="eventAreaScop" alias="波及范围" colspan="3" length="100" width="150"/>
  <item id="newReportCases" type="int" alias="新病例数" length="9"/>
  <item id="newReportDies" type="int" alias="新死亡数" length="9"/>
  <item id="ExcludeCase" type="int" alias="排除病例数" length="9"/>
  <item id="totalReportCases" type="int" alias="累计病例数" length="9"/>
  <item id="totalReportDies" type="int" alias="累计死亡数" length="9"/>
  <item id="eventTime"   not-null="1" alias="发生时间" type="datetime" colspan ="2" maxValue="%server.date.datetime" width="140"/>
  <item id="comeToTime"  not-null="1" alias="报告时间" type="datetime" colspan ="2" maxValue="%server.date.datetime" width="140"/>
  <item id="firstInvasionTime" alias="首例发病时间" type="datetime" colspan ="2" maxValue="%server.date.datetime" width="140"/>
  <item id="lastInvasionTime" alias="末例发病时间" type="datetime" colspan ="2"  maxValue="%server.date.datetime" width="140"/>
  <item id="cardinalSymptom" alias="主要症状" type="string" length="1">
  	 <dic> 
  	 	 <item key="1" text="呼吸道症状" />
  	     <item key="2" text="胃肠道症状" />
  		 <item key="3" text="神经系统症状" />
  		 <item key="4" text="皮肤粘膜症状" />
  		 <item key="5" text="精神症状" />
  		 <item key="6" text="其它" />
  </dic>
  </item>
  <item id="otherSymptom" alias="其它症状" fixed="true" length="20" colspan="3" />
  <item id="mainSigns" alias="主要体征" xtype="textarea" colspan="2" anchor="100%" display="2"  length="200"/>
  <item id="mainMeasure" alias="主要措施效果" xtype="textarea" colspan="2" anchor="100%" display="2"  length="500"/>
 
 <item id="reportUnit" alias="填报单位" queryable="true"  fixed="false" type="string" length="20" width="180"   defaultValue="%user.manageUnit.id">
     <dic id="chis.@manageUnit" includeParentMinLen="6"  lengthLimit="9"  render="Tree"  rootVisible = "true"
      parentKey="%user.manageUnit.id" />
  </item>
 <item id="createUnit" alias="录入机构" type="string" length="20"
    width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="录入人" type="string" length="20" 
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
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
