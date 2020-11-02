<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="IDR_Report" alias="传染病报告卡" sort="fillDate  desc" >
 <item id="RecordID" alias="传染病报告卡记录编号" type="long" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
   </item> 
    <item id="cardCategory" defaultValue="1" alias="报卡类别" length="1" not-null="1">
  	 <dic>
      <item key="1" text="初次报告" />
      <item key="2" text="订正报告" />
    </dic>
  </item>
	<item id="personName"  alias="姓名" queryable="true" length="29" not-null="1"/>
	 <item id="parentsName" alias="患儿家长姓名" length="30" display="2"/>
	 <item id="idCard" alias="身份证号" queryable="true"  length="18" not-null="1"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40" queryable="true" not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="birthday" alias="出生日期"  type="datetime" queryable="true"  not-null="1"/>
	 <item id="fullAge" alias="实足年龄" length="20" fixed="true" display="2" />
	 
	  <!--  <item id="finishStatusend" defaultValue="0" alias="审核状态" queryable="true" length="1" not-null="1">
  	 <dic>
      <item key="0" text="未审核" />
      <item key="1" text="已审核" />
      <item key="2" text="已审核" />
      <item key="3" text="已退回" />
    </dic>
  </item>
  
  -->
	<item id="workPlace" alias="工作单位" display="2" type="string" length="50" colspan="2"/>
	<item id="mobileNumber" alias="本人电话" display="2" type="string" length="20" not-null="1" width="90"/>
	<item id="address" alias="现住址" type="string" display="2" length="100" width="200" colspan="2"/>
	<item id="manaUnitId" display="0" alias="管辖机构" >
	<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
	</item>
	<item id="nationCode" alias="民族" type="string" display="0" length="2"  defaultValue="01">
		<dic id="chis.dictionary.ethnic"/>
	</item>
	
	<item id="maritalStatusCode" alias="婚姻状况" display="0" type="string" length="2"  defaultValue="9" width="50">
		<dic id="chis.dictionary.maritals" render="Tree" minChars="1" onlySelectLeaf="true"/>
	</item>
	<item id="educationCode" alias="文化程度" display="0" type="string" length="2" >
		<dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="organizCode"  alias="填报机构" display="0" fixed="true">
	<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
	</item>
	
	
	<item id="regionCode_text" alias="审核意见" display="0" width="300"/>

  <item id="empiId" alias="empiId" type="string" display="0" length="32" not-null="1"/>
  <item id="phrId" alias="档案编号" type="string" display="0"  length="30"/>
  <item id="birthPlace"  alias="病人属于" length="1" not-null="1" fixed="true">
  	 <dic>
      <item key="1" text="本县区" />
      <item key="2" text="本市其他县区" />
      <item key="3" text="本省其它地市" />
      <item key="4" text="外省" />
      <item key="5" text="港澳台" />
      <item key="6" text="外籍" />
  	 </dic>
  </item>
  <item id="patientJob"  alias="患者职业" length="2" not-null="1" display="2">
  	 <dic>
      <item key="1" text="幼托儿童" />
      <item key="2" text="散居儿童 " />
      <item key="3" text="学生" />
      <item key="4" text="教师" />
      <item key="5" text="保育员及保姆" />
      <item key="6" text="餐饮食品业 " />
      <item key="8" text="商业服务" />
      <item key="9" text="医务人员" />
      <item key="16" text="工人" />
      <item key="17" text="民工" />
      <item key="18" text="农民" />
      <item key="19" text="牧民" />
      <item key="20" text="渔（船） 民" />
      <item key="22" text="干部职员 " />
      <item key="23" text="离退人员 " />
      <item key="24" text="家务及待业" />
      <item key="28" text="不详" />
      <item key="29" text="其他" />
    </dic>
  </item>
  <item id="otherPatientJob" alias="其它职业" length="20" display="2"/>
  
  <item id="casemixCategory1" queryable="true" alias="病例分类1" length="1" not-null="1" >
  	 <dic>
      <item key="1" text="临床诊断病例" />
      <item key="2" text="确诊病例" />
      <item key="3" text="疑似病例" />
      <item key="4" text="病原携带者" />
      <item key="6" text="埃博拉留观病例" />
  	 </dic>
  </item>
  <item id="casemixCategory2"  queryable="true" alias="病例分类2" length="1" display="2">
  	 <dic>
      <item key="0" text="未分型" />
      <item key="1" text="急性" />
      <item key="2" text="慢性（乙型肝炎、血吸虫病填写）" />
  	 </dic>
  </item>
   <!-- maxValue="%server.date.today" -->
  <item id="dateAccident" alias="发病日期"   queryable="true"  type="datetime" not-null="1" />
  <item id="diagnosedDate" alias="诊断日期" not-null="1"  type="datetime"  />
  <item id="deathDate" alias="死亡日期" type="date" maxValue="%server.date.today" display="0"/>
  <item id="categoryAInfectious" alias="甲类传染病" length="50" enableKeyEvents="true" display="2">
  	 <dic render="LovCombo">
      <item key="0100" text="鼠疫" />
      <item key="0200" text="霍乱" />
  	 </dic>
  </item>
  <item id="categoryBInfectious" alias="乙类传染病" length="100" width="150" enableKeyEvents="true" display="2">
  	 <dic render="LovCombo">
      <item key="2700" text="传染性非典型肺炎" />
      <item key="0600" text="艾滋病" />
      <item key="0300" text="病毒性肝炎（  甲型、  乙型、  丙型、  戍型、  未分型）" />
      <item key="0900" text="脊髓灰质炎" />
      <item key="9900" text="人感染高致病性禽流感" />
      <item key="5200" text="人感染H7N9禽流感" />
      <item key="1000" text="麻疹" />
      <item key="1500" text="流行性出血热" />
      <item key="1600" text="狂犬病" />
      <item key="2100" text="流行性乙型脑炎" />
      <item key="2400" text="登革热" />
      <item key="1900" text="炭疽" />
      <item key="0400" text="痢疾" />
      <item key="2600" text="肺结核" />
      <item key="0500" text="伤寒及副伤寒" />
      <item key="1300" text="流行性脑脊髓膜炎" />
      <item key="1100" text="百日咳" />
      <item key="1200" text="白喉" />
      <item key="2500" text="新生儿破伤风" />
      <item key="1400" text="猩红热" />
      <item key="1800" text="布鲁氏菌病" />
      <item key="0700" text="淋病" />
      <item key="0800" text="梅毒" />
      <item key="1700" text="钩端螺旋体病" />
      <item key="3100" text="血吸虫病" />
      <item key="2300" text="疟疾" />
    </dic>
  </item>
  <item id="categoryCInfectious" alias="丙类传染病" length="50" width="150" enableKeyEvents="true" display="2">
  	 <dic render="LovCombo">
      <item key="3500" text="流行性感冒" />
      <item key="3600" text="流行性腮腺炎" />
      <item key="3700" text="风疹" />
      <item key="3800" text="急性出血性结膜炎" />
      <item key="3400" text="麻风病" />
      <item key="2000" text="流行性和地方性斑疹伤寒" />
      <item key="2200" text="黑热病" />
      <item key="3300" text="包虫病" />
      <item key="3200" text="丝虫病" />
      <item key="4000" text="手足口病" />
      <item key="3900" text="除霍乱、细菌性和阿米巴性痢疾、伤寒和副伤寒以外的感染性腹泻病" />
  	 </dic>
  </item>
  <item id="viralHepatitis" alias="病毒性肝炎" length="50" display="2">
  	 <dic>
      <item key="0301" text="甲型" />
      <item key="0302" text="乙型" />
      <item key="0303" text="丙型" />
      <item key="0304" text="戍型" />
      <item key="0305" text="未分型" />
  	 </dic>
  </item>
  <item id="anthrax" alias="炭疽" type="string" length="1" display="2">
  	 <dic>
      <item key="1901" text="肺炭疽" />
      <item key="1902" text="皮肤炭疽" />
      <item key="1903" text="未分型" />
  	 </dic>
  </item>
  <item id="dysentery" alias="痢疾" type="string" length="1" display="2">
  	 <dic>
      <item key="0401" text="细菌性" />
      <item key="0402" text="阿米巴性" />
  	 </dic>
  </item>
  <item id="phthisis" alias="肺结核" type="string" length="1" display="2">
  	 <dic>
      <item key="2605" text="利福平耐药" />
      <item key="2606" text="病原学阳性" />
      <item key="2607" text="病原学阴性" />
      <item key="2608" text="无病原学结果" />
  	 </dic>
  </item>

  <item id="typhia" alias="伤寒" type="string" length="1" display="2">
  	 <dic>
      <item key="0501" text="伤寒" />
      <item key="0502" text="副伤寒" />
  	 </dic>
  </item>
  <item id="syphilis" alias="梅毒" type="string" length="1" display="2">
  	 <dic>
      <item key="0801" text="Ⅰ期" />
      <item key="0802" text="Ⅱ期" />
      <item key="0803" text="Ⅲ期" />
      <item key="0804" text="胎传" />
      <item key="0805" text="隐性" />
  	 </dic>
  </item>
  <item id="malaria" alias="疟疾" type="string" length="1" display="2">
  	 <dic>
      <item key="2301" text="间日疟" />
      <item key="2302" text="恶性疟" />
      <item key="2303" text="未分型" />
  	 </dic>
  </item>
  
  <item id="otherCategoryInfectious" alias="其他传染病" length="200" width="150" enableKeyEvents="true" display="2"/>
  
  <item id="correctionProblems" alias="订正病名" length="20" display="2"/>
  
  <item id="returnReason" alias="退卡原因" length="20" width="150" display="2"/>
  <item id="fillDate" alias="填卡日期" queryable="true" type="datetime" not-null="1" />
  <item id="icd10" alias="icd10" length="20" display="0"/>
  
 <!-- <item id="reportDoctor" alias="报告医生" type="string" length="20" not-null="1"  defaultValue="%user.userId" display="2">
    <dic id="chis.dictionary.user05" parentKey="%user.manageUnit.id" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
  </item>-->
<!--<item id="reportUnit" alias="填报单位"   colspan="2" fixed="false" type="string" length="20" width="180" display="2" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  lengthLimit="9"  render="Tree"  rootVisible = "true"
      parentKey="%user.manageUnit.id" />
  </item>  -->   
   <item id="reportUnit" alias="报告单位编码"  fixed="false" type="string" length="20" display="0" colspan="1"/>
   <item id="reportUnitName" alias="报告单位"  type="string" length="50" fixed="false"  colspan="2"/>
  <item id="status" alias="状态" length="1" hidden="true" display="2"
    defaultValue="0">
    <dic>
      <item key="0" text="正常" />
      <item key="1" text="已注销" />
    </dic>
  </item>
	
  <item id="comments" alias="备注"  length="200" display="2"  colspan="2"/>
  <item id="reportDoctor" alias="报告医生" fixed="true" type="string" defaultValue="0"  length="1" colspan="1" enableKeyEvents="true" renderer="jgRender" queryable="true">
 <dic id="phis.dictionary.user" />
  </item>
  <item id="cancellationUser" alias="注销人" type="string" length="20" display="0"
    hidden="true">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationDate" alias="注销日期" type="date" hidden="true" display="0" />
  <item id="cancellationUnit" alias="注销单位" type="string" length="20" display="0"
    width="180" hidden="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="cancellationReason" alias="注销原因" type="string" length="1" display="0"
    hidden="true">
    <dic>
      <item key="1" text="死亡" />
      <item key="2" text="迁出" />
      <item key="3" text="失访" />
      <item key="4" text="拒绝" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="deadReason" alias="死亡原因" type="string" fixed="true" hidden="true"
    length="100" display="0" anchor="100%" />
  <item id="MS_BRZD_JLBH" alias="门诊诊断记录编号" display="0" type="string" length="20" hidden="true" editable="false"/>
  <item id="createUnit" alias="录入机构" queryable="true" type="string" length="20"  display="1"
    width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="录入人" type="string" length="20"  
    update="false" fixed="true" >
   <dic id="phis.dictionary.user" />
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datetimefield" update="false"  display="2"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    defaultValue="%user.userId" display="0">
    <dic id="chis.dictionary.Personnel"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
    defaultValue="%server.date.today" display="0">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="finishStatus" alias="审核结果" fixed="true" type="string" defaultValue="0"  length="1" colspan="1" enableKeyEvents="true" renderer="jgRender" queryable="true">
    <dic>
     <item key="0" text="待审核" />
     <item key="1" text="同意" />
     <item key="2" text="不同意" />
    </dic>
  </item>
  <item id="finishReason" alias="审核意见" type="string" length="200" xtype="textarea" colspan="3" width="140"/>
  <item id="reportFlag" alias="上报状态" type="string" fixed="true" defaultValue="" length="1" colspan="3" renderer="reportRender" queryable="true">
    <dic>
      <item key="0" text="未上报" />
      <item key="1" text="已上报" />
      <item key="2" text="上报失败" />
    </dic>
  </item>
  
 
 </entry>