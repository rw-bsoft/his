<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_DeliveryRecordSelect" alias="产时记录">
  <item id="deliveryId" alias="记录序号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true" />
  <item id="empiId" alias="EMPIID" type="string" length="32"
    hidden="true" />
  <item id="formCode" alias="表单号" type="string" length="32"
    hidden="true" />
  <item id="certificateId" alias="孕册号" type="string" length="10"
    display="0" />
  <item id="deliveryCode" alias="准生证" type="string" length="32"
    display="0" />
  <item id="inHospitalCode" alias="住院号" type="string" length="32"
    display="0" />
  <item id="babyName" alias="新生儿姓名" type="string" length="32" />
  <item id="babySex" alias="性别" type="string" length="1">
    <dic id="chis.dictionary.gender" />
  </item>
  <item id="babyBirthday" alias="出生日期" type="date" />
  <item id="gestation" alias="出生孕周" type="int" display="0" />
  <item id="birthHeight" alias="出生身长(cm)" type="int" display="0" />
  <item id="birthWeight" alias="出生体重(g)" type="int" display="0" />
  <item id="headSize" alias="头围(cm)" type="bigDecimal" length="8"
    display="0" />
  <item id="circumference" alias="胸围(cm)" type="bigDecimal" length="8"
    display="0" />
  <item id="motherName" alias="母亲姓名" type="string" length="32"
    hidden="true" />
  <item id="motherNationality" alias="母亲国籍代码" type="string"
    hidden="true" length="3">
    <dic id="chis.dictionary.nationality" />
  </item>
  <item id="motherNation" alias="母亲民族代码" type="string" length="2"
    hidden="true">
    <dic id="chis.dictionary.ethnic" />
  </item>
  <item id="motherCardNo" alias="母亲身份证件-号码" type="string" length="18"
    hidden="true" />
  <item id="fatherName" alias="父亲姓名" type="string" length="30"
    hidden="true" />
  <item id="fatherNationality" alias="父亲国籍代码" type="string"
    hidden="true" length="3">
    <dic id="chis.dictionary.nationality" />
  </item>
  <item id="fatherNation" alias="父亲民族代码" type="string" length="2"
    hidden="true">
    <dic id="chis.dictionary.ethnic" />
  </item>
  <item id="fatherCardNo" alias="父亲身份证件-号码" type="string" length="18"
    hidden="true" />
  <item id="fetusNo" alias="胎" type="int" hidden="true" />
  <item id="parity" alias="产次" type="int" hidden="true" />
  <item id="expectedDate" alias="预产期" type="date" display="0" />
  <item id="deliveryPlace" alias="分娩地点" type="string" length="32"
    display="0" />
  <item id="gestationalweeksDelivery" alias="孕周(d)" type="int"
    display="0" />
  <item id="deliveryTime" alias="分娩时间" type="date" display="0" />
  <item id="modeOfDelivery" alias="分娩方式" type="string" length="2"
    display="0">
    <dic id="chis.dictionary.deliveryType" />
  </item>
  <item id="modeOfDeliveryDesc" alias="分娩方式其他说明" type="string"
    hidden="true" length="100" />
  <item id="totalLabor" alias="总产程(min)" type="int" display="0" />
  <item id="firstLabor" alias="第一产程(min)" type="int" display="0" />
  <item id="secondLabor" alias="第二产程(min)" type="int" display="0" />
  <item id="thirdlabor" alias="第三产程(min)" type="int" display="0" />
  <item id="deliverybleeding" alias="产时出血量(ml)" type="int"
    hidden="true" />
  <item id="totalbleeding" alias="总出血量" type="int" hidden="true" />
  <item id="afterBleeding" alias="产后两小时出血量(ml)" type="int"
    hidden="true" />
  <item id="perineum" alias="会阴切开标志" type="string" length="1"
    hidden="true" />
  <item id="perineumStatus" alias="会阴裂伤程度" type="string" length="1"
    hidden="true">
    <dic id="chis.dictionary.CV5202_04"></dic>
  </item>
  <item id="perinatalcomplications" alias="产时并发症" type="string"
    hidden="true" length="2">
    <dic id="chis.dictionary.CV5202_03"></dic>
  </item>
  <item id="dbp" alias="产后舒张压(mmHg)" type="int" hidden="true" />
  <item id="sbp" alias="产后收缩压(mmHg)" type="int" hidden="true" />
  <item id="openMilkTime" alias="开奶时间(min)" type="int" display="0" />
  <item id="outcomeOfPregnancy" alias="分娩结局" type="string" display="0"
    length="50" />
  <item id="deadTime" alias="孕产妇死亡时间类别" type="string" length="1"
    display="0" hidden="true">
    <dic>
      <item key="1" text="产前"></item>
      <item key="2" text="产时"></item>
      <item key="3" text="产后"></item>
    </dic>
  </item>
  <item id="dischargedDate" alias="出院日期" type="date" hidden="true" />
  <item id="apgar1" alias="1分钟Apgar评分" type="int" display="0" />
  <item id="apgar5" alias="5分钟Apgar评分" type="int" display="0" />
  <item id="apgar10" alias="10分钟评分" type="int" display="0" />
  <item id="birthDefect" alias="出生缺陷" type="string" length="2"
    display="0">
    <dic id="chis.dictionary.defectsType"></dic>
  </item>
  <item id="neonatalcomplications" alias="并发症代码" type="string"
    display="0" length="7">
    <dic id="chis.dictionary.CV5502_04"></dic>
  </item>
  <item id="neonatalRescue" alias="抢救方法代码" type="string" length="1"
    display="0">
    <dic id="chis.dictionary.CV5201_09"></dic>
  </item>
  <item id="neonataldeathsreason" alias="新生儿死亡原因" type="string"
    hidden="true" length="100" />
  <item id="neonataldeathsTime" alias="新生儿死亡时间" type="date"
    hidden="true" />
  <item id="hospitalCode" alias="分娩医院编码" type="string" length="8"
    hidden="true" />
  <item id="hospitalName" alias="出生医院名称" type="string" length="70"
    display="0" />
  <item id="drdeliveryCode" alias="分娩医生编号" type="string" length="32"
    hidden="true" />
  <item id="drdeliveryName" alias="分娩医生名称" type="string" length="30"
    display="0" />
  <item id="neonatalDisposition" alias="处理指导意见" type="string"
    display="0" colspan="2" length="100" />
  <item id="vaccinate1" alias="是否接种乙肝疫苗" type="string" length="1"
    hidden="true" />
  <item id="vaccinateDate1" alias="乙肝疫苗接种日期" type="date"
    hidden="true" />
  <item id="vaccinateNo1" alias="乙肝疫苗接种批号" type="string" length="32"
    hidden="true" />
  <item id="reasonOfNotVaccinated1" alias="乙肝疫苗未接种原因" type="string"
    hidden="true" length="32" />
  <item id="vaccinate2" alias="是否接种卡介苗" type="string" length="1"
    hidden="true" />
  <item id="vaccinateDate2" alias="卡介苗接种日期" type="date" hidden="true" />
  <item id="vaccinateNo2" alias="卡介苗接种批号" type="string" length="32"
    hidden="true" />
  <item id="reasonOfNotVaccinated2" alias="卡介苗未接种原因" type="string"
    hidden="true" length="100" />
  <item id="vaccinate3" alias="是否接种乙肝高价免疫球蛋白" type="string"
    hidden="true" length="1" />
  <item id="vaccinateDate3" alias="乙肝高价免疫球蛋白接种日期" type="date"
    hidden="true" />
  <item id="vaccinateNo3" alias="乙肝高价免疫球蛋白接种批号" type="string"
    hidden="true" length="32" />
  <item id="reasonOfNotVaccinated3" alias="乙肝高价免疫球蛋白未接种原因"
    hidden="true" type="string" length="100" />
  <item id="modifydate" alias="修改日期" type="date" length="1"
    hidden="true" />
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
