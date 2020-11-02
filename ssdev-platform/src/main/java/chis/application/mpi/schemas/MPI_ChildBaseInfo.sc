<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mpi.schemas.MPI_DemographicInfo" alias="儿童个人基本信息"
  sort=" createTime desc">
  <item id="empiId" alias="EMPI" type="string" length="32" display="0"
    pkey="true" />
  <item id="cardNo" alias="卡号" xtype="iccardfield" type="string" virtual="true" display="2"
    length="20"  enableKeyEvents="true" update="false"/>
  <item id="idCard" alias="本人身份证" type="string" length="20"
    width="160" queryable="true" vtype="childIdCard" enableKeyEvents="true" />
  <item id="vaccinateCardNo" alias="计免接种卡号" type="string" length="20" />
  <item id="personName" alias="姓名" type="string" length="20"
    queryable="true" />
  <item id="photo" alias="" xtype="imagefield" type="string"
    display="0" rowspan="5" />
  <item id="sexCode" alias="性别" type="string" length="1" width="40"  update="false"  
    queryable="true" defalutValue="9" not-null="1">
    <dic id="chis.dictionary.gender" />
  </item>
  <item id="birthday" alias="出生日期" type="date" width="75"   
    queryable="true" not-null="1" maxValue="%server.date.today"/>
  <item id="workPlace" alias="工作单位" type="string" length="50"
    display="0" />
  <item id="mobileNumber" alias="本人电话" type="string" length="20"
    display="0" width="90" />
  <item id="relativeIdCard" alias="直系亲属身份证" type="string" length="20"
    not-null="1" width="160" queryable="true" enableKeyEvents="true"
    vtype="idCard" display="2" />
  <item id="relativeName" alias="直系亲属姓名" type="string" length="20"
    queryable="true" display="2" not-null="1" enableKeyEvents="true" />
  <item id="relativeEmpiId" alias="直系亲属EMPIID" type="string"
    length="32" display="0" />
  <item id="certificateNo" alias="出生证号" type="string" length="10"
    width="150" />	
  <item id="regionCode" alias="网格地址" type="string" length="25"
    not-null="1" width="200"  update="false"   anchor="100%"  >
    <dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true"/>
  </item>
  <item id="regionCode_text" alias="网格地址" type="string" length="200" display="0" />
  <item id="manaDoctorId" alias="责任医生" type="string" length="20" update="false"
    not-null="1">
    <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"/>
  </item>
  <item id="manaUnitId" alias="管辖机构" type="string" length="20" width="180" not-null="1" fixed="true" queryable="true" >
    <dic id="chis.@manageUnit"  includeParentMinLen="6" render="Tree"/>
  </item>
  <item id="homePlace" alias="出生地" type="string" length="100" width="90" />
  <item id="contact" alias="联系人姓名" type="string" length="20" />
  <item id="contactPhone" alias="联系人电话" type="string" length="20" />
  <item id="address" alias="联系地址" type="string" length="100"   colspan="2"/>
  <item id="phoneNumber" alias="家庭电话" type="string" length="20" />
  <item id="nationalityCode" alias="国籍" type="string" length="3"
    defaultValue="CN">
    <dic id="chis.dictionary.nationality" />
  </item>
  <item id="registeredPermanent" alias="常住类型" type="string" length="1"
    not-null="1">
    <dic id="chis.dictionary.registeredPermanent" />
  </item>
  <item id="isAgrRegister" alias="是否农业户籍" type="string" length="1"
    not-null = "1" display="2">
    <dic id="chis.dictionary.yesOrNo" />
  </item>
  <item id="nationCode" alias="民族" type="string" length="2"
    defaultValue="01">
    <dic id="chis.dictionary.ethnic" />
  </item>
  <item id="bloodTypeCode" alias="血型" type="string" length="1"
    defaultValue="5">
    <dic id="chis.dictionary.blood" />
  </item>
  <item id="rhBloodCode" alias="RH血型" type="string" length="1"
    defaultValue="3">
    <dic id="chis.dictionary.rhBlood" />
  </item>
  <item id="educationCode" alias="文化程度" type="string" length="2"
    display="0">
    <dic id="chis.dictionary.education" />
  </item>
  <item id="workCode" alias="职业类别" type="string" length="3"
    display="0" defaultValue="Y">
    <dic id="chis.dictionary.jobtitle" onlySelectLeaf="true" />
  </item>
  <item id="maritalStatusCode" alias="婚姻状况" type="string" length="1"
    display="0" defaultValue="9" width="50">
    <dic id="chis.dictionary.maritals" />
  </item>
  <item id="insuranceCode" alias="医疗支付方式" type="string" length="2">
    <dic id="chis.dictionary.payMode" />
  </item>
  <item id="insuranceType" alias="其他支付方式" type="string" length="100"  fixed="true"/>
  <item id="zipCode" alias="邮政编码" type="string" length="6"
    display="0" />
	
  <item id="email" alias="电子邮件" type="string" length="30" display="0"
    vtype="email" />
	
  <item id="startWorkDate" alias="参加工作日期" type="date" display="0" />
  <item id="createUnit" alias="建档机构" type="string" length="16"
    canRead="false" display="0" />
  <item id="createUser" alias="建档人" type="string" length="20"
    display="0" queryable="true"/>
  <item id="createTime" alias="建档时间" type="date" display="0" />
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="16"
    display="0" />
  <item id="lastModifyTime" alias="最后修改时间" type="date" display="0" />
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    display="1" />
  <item id="status" alias="状态" type="string" length="2"
    display="0" >
    <dic>
      <item key="1" text="死亡登记" />
      <item key="2" text="年龄大于四周岁" />
    </dic>
  </item>
</entry>