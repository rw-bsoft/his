<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mhc.schemas.MHC_VisitRecord" alias="孕妇随访记录" sort="a.empiId" version="1342779105978" filename="D:\eclipse3.7\workspace\BSCHIS\WebRoot\WEB-INF\config\schema\mhc/MHC_VisitRecord.xml"> 
  <item id="visitId" alias="随访序号" length="30" pkey="true" type="string" not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
  </item>  
  <item ref="b.personName" display="1" queryable="true"/>  
  <item ref="b.birthday" display="1" queryable="true"/>  
  <item ref="b.idCard" display="1" queryable="true"/>  
  <item ref="b.mobileNumber" display="1" queryable="true"/>  
  <item ref="d.manaDoctorId" display="1" queryable="true"/>  
  <item ref="d.mhcDoctorId" display="1" queryable="true"/>  
  <item ref="d.manaUnitId" display="0" queryable="true"/>  
  <item ref="d.homeAddress" display="1" queryable="true"/>  
  <item ref="d.homeAddress_text" display="0" queryable="true"/>  
  <item id="pregnantId" alias="孕妇档案编号" length="30" hidden="true" display="0" type="string"/>  
  <item id="empiId" alias="EMPIID" length="32" hidden="true" display="0" type="string"/>  
  <item id="visitDate" alias="检查时间" type="date" not-null="1" defaultValue="%server.date.today" maxValue="%server.date.today"/>  
  <item id="ifLost" alias="是否失访" length="1" width="100" defaultValue="n"  type="string"> 
    <dic id="chis.dictionary.yesOrNo"/> 
  </item>  
  <item id="lostReason" alias="失访原因" length="200" width="100" fixed="true"  type="string" colspan="2" anchor="100%"/>  
  <item id="visitMode" alias="随访方式" length="1" width="100" defaultValue="1"  type="string"> 
    <dic id="chis.dictionary.visitWay"/> 
  </item>  
  <item id="visitResult" alias="随访结果" length="1" width="100"  type="string"> 
    <dic id="chis.dictionary.visitResult"/> 
  </item>  
  <item id="exceptionDesc" alias="其他异常描述" length="500" width="100" fixed="true" colspan="2" anchor="100%" display="2"  type="string"/>  
  <item id="selfFeelSymptom" alias="主诉" length="500" width="100" colspan="3" display="2" type="string"/>  
  <item id="quickeningTimes" alias="胎动次数" width="100" type="int" display="2"/>  
  <item id="weight" alias="体重(kg)" not-null="1" type="double" display="2"/>  
  <item id="sbp" alias="收缩压(mmhg)" type="int" not-null="1" minValue="10" maxValue="500" validationEvent="false" enableKeyEvents="true" display="2"/>  
  <item id="dbp" alias="舒张压(mmhg)" type="int" not-null="1" minValue="10" maxValue="500" validationEvent="false" enableKeyEvents="true" display="2"/>  
  <item id="abdomenCircumFerence" alias="腹围(cm)" not-null="1" type="double" display="2"/>  
  <item id="heightFundusUterus" alias="宫高(cm)" not-null="1" type="double" display="2"/>  
  <item id="fetalPosition" alias="胎方位" length="3" colspan="2" anchor="100%" display="2" type="string"> 
    <dic id="chis.dictionary.CV5105_01" defaultValue="99"/> 
  </item>  
  <item id="fetalPositionFlag" alias="胎位异常" type="string" defaultValue="n" length="1" display="2"> 
    <dic id="chis.dictionary.haveOrNot"/> 
  </item>  
  <item id="fetalHeartRate" alias="胎心率(次/分）" type="int" display="2"/>  
  <item id="fetalHeartFlag" alias="胎心异常" type="string" length="1" defaultValue="n" display="2"> 
    <dic id="chis.dictionary.haveOrNot"/> 
  </item>  
  <item id="weightFlag" alias="体重异常" type="string" length="1" defaultValue="n" display="2"> 
    <dic id="chis.dictionary.haveOrNot"/> 
  </item>  
  <item id="bpFlag" alias="血压异常" type="string" length="1" defaultValue="n" display="2"> 
    <dic id="chis.dictionary.haveOrNot"/> 
  </item>  
  <item id="heightFundusUterusFlag" alias="宫高异常" type="string" defaultValue="n" length="1" display="2"> 
    <dic id="chis.dictionary.haveOrNot"/> 
  </item>  
  <item id="presentPosition" alias="胎先露" length="10" display="2"  type="string"> 
    <dic id="chis.dictionary.CV5202_05" defaultValue="9"/> 
  </item>  
  <item id="xianjie" alias="衔接" length="10" display="2"  type="string"/>  
  <item id="edemaStatus" alias="浮肿" length="10" defaultValue="1" display="2"  type="string"> 
    <dic id="chis.dictionary.CV5102_05"/> 
  </item>  
  <item id="haemoglobin" alias="血红蛋白(g/L)" length="10" display="2" type="string"/>  
  <item id="albuminuria" alias="尿蛋白" length="10" display="2" type="string"/>  
  <item id="bloodSugar" alias="血糖" length="10" display="2" type="string"/>  
  <item id="otherExam" alias="其他辅助检查" length="100" display="2" type="string"/>  
  <item id="amnioticFluidDepth" alias="羊水垂直深度" length="10" display="2"  type="string"/>  
  <item id="afi" alias="羊水指数" length="10" display="2"/>  
  <item id="highRiskReason" alias="高危因素" length="64" hidden="true" display="0" type="string"/>  
  <item id="highRiskScore" alias="高危评分" type="int" fixed="true"/>  
  <item id="highRiskLevel" alias="高危评级" length="10" fixed="true"  type="string"/>  
  <item id="otherHighReasons" alias="其他危险因素" length="500" colspan="2" display="2"  type="string"/>  
  <item id="bultrasonicDiagnosis" alias="B超诊断" length="100" display="2"  type="string"/>  
  <item id="bultrasonicDate" alias="B超诊断日期" type="date" maxValue="%server.date.today" display="2"/>  
  <item id="bultrasonicId" alias="B超检查单号" length="30" display="2"  type="string"/>  
  <item id="category" alias="分类" length="2" defaultValue="n" display="2"  type="string"> 
    <dic> 
      <item key="1" text="未发现异常"/>  
      <item key="2" text="发现异常"/> 
    </dic> 
  </item>  
  <item id="suggestion" alias="处理建议" length="100" colspan="2" anchor="100%" display="2"  type="string"/>  
  <item id="nextDate" alias="下次预约日期" type="date" not-null="0"/>  
  <item id="checkWeek" alias="检查孕周" length="2" fixed="true" width="100"  type="string"/>  
  <item id="gestationMode" alias="终止妊娠方式" length="20" display="2" type="string"> 
    <dic id="chis.dictionary.gestationMode" render="Tree" onlySelectLeaf="true"/> 
  </item>  
  <item id="endDate" alias="终止妊娠时间" type="date" maxValue="%server.date.today"/>  
  <item id="doctorId" alias="随访医生" length="20" defaultValue="%user.userId" colspan="2" anchor="100%"  type="string"> 
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/> 
  </item>  
  <item id="hospitalCode" alias="检查机构" length="20" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" anchor="100%"  type="string"> 
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/> 
  </item>  
  <item id="guide" alias="指导" length="45" colspan="2" display="2"  type="string"> 
    <dic render="LovCombo">
      <item key="01" text="个人卫生" />
      <item key="02" text="心理" />
      <item key="03" text="膳食营养" />
      <item key="04" text="避免致畸因素和疾病对胚胎的不良影响 " />
      <item key="05" text="产前筛查宣传告知" />
      <item key="06" text="运动" />
      <item key="07" text="自我监测" />
      <item key="08" text="分娩准备" />
      <item key="09" text="母乳喂养" />
      <item key="10" text="新生儿护理与喂养" />
      <item key="11" text="婴儿喂养与营养" />
      <item key="12" text="性保健" />
      <item key="13" text="避孕" />
      <item key="99" text="其他" />
    </dic>
  </item>  
  <item id="otherGuide" alias="其他指导描述" length="500" colspan="2" fixed="true" display="2"  type="string"/>  
  <item id="referral" alias="有无转诊" type="string" length="1"> 
    <dic id="chis.dictionary.haveOrNot"/> 
  </item>  
  <item id="referralReason" alias="原因" type="string" length="500" colspan="2" fixed="true"/>  
  <item id="referralUnit" alias="机构及科室" type="string" length="100" fixed="true"/>  
  <item id="createUnit" alias="录入机构" type="string" length="20" width="180" fixed="true" update="false" display="0" defaultValue="%user.manageUnit.id"> 
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.manageUnit.id']</set> 
  </item>  
  <item id="createUser" alias="录入人" type="string" length="20" display="0" update="false" fixed="true" defaultValue="%user.userId"> 
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.userId']</set> 
  </item>  
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" display="0" fixed="true" defaultValue="%server.date.today"> 
    <set type="exp">['$','%server.date.datetime']</set>
  </item>  
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id"> 
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.manageUnit.id']</set> 
  </item>  
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1"> 
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
    <set type="exp">['$','%user.userId']</set> 
  </item>  
  <item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" defaultValue="%server.date.today" display="1"> 
    <set type="exp">['$','%server.date.datetime']</set>
  </item>  
  <relations> 
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>  
    <relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord"> 
      <join parent="empiId" child="empiId"/> 
    </relation>  
    <relation type="children" entryName="chis.application.mhc.schemas.MHC_PregnantRecord"> 
      <join parent="pregnantId" child="pregnantId"/> 
    </relation> 
  </relations> 
</entry>
