<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_FirstVisitRecord" alias="孕妇首次随访表">
  <item id="pregnantId" alias="孕妇档案编号" length="30" pkey="true"
    type="string" not-null="1" fixed="true" hidden="true"
    generator="assigned">
  </item>
  <item id="empiId" alias="EMPIID" length="32" not-null="1" type="string" 
    display="0" />
  <item id="diagnosisMethod" alias="妊娠确诊方法" type="string" not-null="1"
    length="1">
    <dic id="chis.dictionary.CV5201_08" />
  </item>
  <item id="diagnosisDate" alias="妊娠确诊时间" type="date" update="false"
    not-null="1" maxValue="%server.date.today" />
  <item id="quickeningWeek" alias="胎动孕周" type="int"/>
  <item id="selfFeelSymptom" alias="自觉症状" length="2" type="string"
    defaultValue="1">
    <dic>
      <item key="1" text="无" />
      <item key="2" text="头晕眼花" />
      <item key="3" text="不详" />
    </dic>
  </item>
  <item id="symptomDesc" alias="症状描述" length="100" colspan="2" type="string"
    fixed="true" />
  <item id="morningSickness" alias="早孕反应" type="string" length="1"
    defaultValue="n">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="morningSicknessWeek" alias="早孕反应孕周" fixed="true" type="int"/>
  <item id="BMI" alias="体质指数" length="10" fixed="true" type="string"/>
  <item id="weight" alias="体重(kg)" not-null="1" type="double"/>
  <item id="sbp" alias="收缩压(mmhg)" type="int" not-null="1"  minValue="10" maxValue="500" validationEvent="false" enableKeyEvents="true"/>
  <item id="dbp" alias="舒张压(mmhg)" type="int" not-null="1"  minValue="10" maxValue="500" validationEvent="false" enableKeyEvents="true"/>
  <item id="heightFundusUterus" alias="宫高(cm)" type="double"/>
  <item id="abdomenCircumFerence" alias="腹围(cm)" type="double"/>
  <item id="headache" alias="是否头痛" type="string" length="1"
    defaultValue="n">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="headacheWeek" alias="头痛孕周" length="2" fixed="true" type="string" />
  <item id="edema" alias="是否浮肿" type="string" length="1"
    defaultValue="n">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="edemaWeek" alias="浮肿孕周" fixed="true" type="int"/>
  <item id="bleeding" alias="是否出血" type="string" length="1"
    defaultValue="n">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="bleedingWeek" alias="出血孕周" fixed="true" type="int"/>
  <item id="fever" alias="是否发热" type="string" length="1"
    defaultValue="n">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="feverWeek" alias="发热孕周" fixed="true" type="int"/>
  <item id="otherSympton" alias="其他症状描述" length="100" colspan="2" type="string"/>
  <item id="gestationalWeeks" alias="发生孕周" length="20" fixed="true" type="string"/>
  <item id="diagnoseDate" alias="产前诊断日期" type="date" hidden="true" />
  <item id="diagnoseResult" alias="产前诊断结果" type="string" length="1" hidden="true">
    <dic>
      <item key="1" text="阳性" />
      <item key="2" text="阴性" />
    </dic>
  </item>
  <item id="screeningDate" alias="产前筛查日期" type="date" hidden="true"/>
  <item id="screening" alias="产前筛查结果" type="string" length="1" hidden="true">
    <dic>
      <item key="1" text="低风险" />
      <item key="2" text="高风险" />
      <item key="3" text="未查" />
    </dic>
  </item>
  <item id="category" alias="分类" length="2" defaultValue="1" display="0" type="string" >
    <dic>
      <item key="1" text="未发现异常"></item>
      <item key="2" text="发现异常"></item>
    </dic>
  </item>
  <item id="generalComment" alias="总体评估" length="1"  colspan="2" type="string">
    <dic>
      <item key="1" text="未见异常"></item>
      <item key="2" text="异常"></item>
    </dic>
  </item>
  <item id="commentText" alias="异常描述" length="200" colspan="2"  fixed="true" type="string"/>
  <item id="highRiskReason" alias="高危因素" length="64" hidden="true" type="string" >
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="highRiskScore" alias="高危评分" type="int" not-null="1" xtype="lookupfield" />
  <item id="highRiskLevel" alias="高危评级" length="64" fixed="true" type="string"/>
  <item id="suggestion" alias="保健指导" length="45" display="2"
    type="string" colspan="2">
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
  <item id="otherSuggestion" alias="其他指导" type="string" display="2"
    length="200" fixed="true" colspan="2"/>
  <item id="referral" alias="转诊" type="string" display="2"
    length="1">
    <dic id="chis.dictionary.haveOrNot" />
  </item>
  <item id="reason" alias="原因" type="string" display="2" length="200"  colspan="2" fixed="true"/>
  <item id="doccol" alias="机构及科室" type="string" display="2" length="50"  fixed="true"/>
  <item id="visitPrecontractTime" alias="下次随访日期" type="date" minValue="%server.date.today"/>
  <item id="visitDoctorCode" alias="随访医生" length="20" type="string"
    defaultValue="%user.userId">
    <dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
  </item>
  <item id="visitUnitCode" alias="随访机构" length="20"  type="string"  fixed="true"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" />
  </item>
  <item id="createUnit" alias="录入机构" type="string" length="20"
    width="180" fixed="true" update="false" display="0"
    defaultValue="%user.manageUnit.id">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createUser" alias="录入人" type="string" length="20" display="0"
    update="false" fixed="true" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" display="0"
    fixed="true" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
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
    defaultValue="%server.date.today" display="1">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
</entry>
