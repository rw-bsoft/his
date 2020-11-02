<entry entityName="chis.application.cdh.schemas.CDH_BirthCertificate" alias="出生医学证明" sort="a.phrId desc">
  <item id="recordId" pkey="true" alias="记录编号" type="string" length="16"
    width="160" not-null="1" display="0">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="phrId" alias="档案编号" type="string" length="30" width="160" display="0" />
  <item id="empiId" alias="EMPI" type="string" length="32" display="0"
    not-null="true" />
  <item ref="c.certificateNo" queryable="true" not-null="1" />
  <item id="certificateIDold" alias="原证号" type="string" length="10" display="3" />
  <item ref="b.personName" queryable="true" fixed="true" not-null="0" display="1"/>
  <item ref="b.sexCode" queryable="true" fixed="true"  display="1"/>
  <item ref="b.idCard" queryable="true" fixed="true" display="1" />
  <item ref="b.birthday" queryable="true" fixed="true" display="1"/>
  <item ref="d.regionCode" display="1"/>
  <item ref="d.manaUnitId" display="1"/>
  <item ref="d.regionCode_text" display="0" />
  <item id="birthAddressType" alias="出生地点类别" type="string" length="1" queryable="true" display="3" not-null="1">   
    <dic >
      <item key="1" text="医院" />
      <item key="2" text="妇幼保健院" />
      <item key="3" text="家庭" />
      <item key="9" text="其他" />
    </dic>
  </item>
  <item id="healthStatus" alias="健康状况" type="string" length="1" display="3" not-null="1">
    <dic>
      <item key="1" text="良好" />
      <item key="2" text="一般" />
      <item key="3" text="差" />
    </dic>
  </item>
  <item id="gestation" alias="出生孕周" type="int" 	display="3" not-null="1" />
  <item id="height" alias="出生身长(cm)" type="double" 	display="3" not-null="1"/>
  <item id="weight" alias="出生体重(g)" type="double" 	display="3" not-null="1"/>
  <item id="motherName" alias="母亲姓名" type="string" length="30"  display="3" not-null="1"/>
  <item id="motherNationality" alias="母亲国籍" type="string" length="3"  display="3" not-null="1" defaultValue="CN">
    <dic id="chis.dictionary.nationality" />
  </item>
  <item id="motherNation" alias="母亲民族" type="string" length="2"  display="3" not-null="1" defaultValue="01">
    <dic id="chis.dictionary.ethnic" />
  </item>
  <item id="motherCardNo" alias="母亲身份证号" vtype="idCard" type="string"
    length="18"  display="3" not-null="1"/>
  <item id="fatherName" alias="父亲姓名" type="string" length="30" display="3" not-null="1"/>
  <item id="fatherNationality" alias="父亲国籍" type="string" length="3"  display="3" not-null="1" defaultValue="CN">
    <dic id="chis.dictionary.nationality" />
  </item>
  <item id="fatherNation" alias="父亲民族" type="string" length="2"  display="3" not-null="1" defaultValue="01">
    <dic id="chis.dictionary.ethnic" />
  </item>
  <item id="fatherCardNo" alias="父亲身份证号" vtype="idCard" type="string"
    length="18" display="3" not-null="1"/>
  <item id="deliveryDoctor" alias="助产人员" type="string" length="30" display="3"/>
  <item id="deliveryUnit" alias="助产机构" type="string" length="70"/>
  <item id="issueDate" alias="签发日期" type="date" 
    display="3" maxValue="%server.date.today" not-null="1" queryable="true"/>
  <item id="issueUnit" alias="签证机构" type="string" length="70" 
    display="3" queryable="true" not-null="1"/>
  <item id="lastModifyUser" alias="最后修改人" type="string" length="20"
    fixed="true" display="1" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
    defaultValue="%user.manageUnit.id"  display="1">
    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="lastModifyDate" alias="最后修改时间"  type="datetime"  xtype="datefield"  fixed="true"
    display="1" defaultValue="%server.date.today">
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="createUser" alias="录入人" type="string" length="20" fixed="true" update="false"
    display="3" defaultValue="%user.userId">
    <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
    <set type="exp">['$','%user.userId']</set>
  </item>
  <item id="createUnit" alias="录入机构" type="string" length="20" update="false"
    fixed="true" display="3" defaultValue="%user.manageUnit.id" width="450">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
    <set type="exp">['$','%user.manageUnit.id']</set>
  </item>
  <item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield"  fixed="true" update="false"
    display="1" defaultValue="%server.date.today" >
    <set type="exp">['$','%server.date.datetime']</set>
  </item>
  <item id="empiId" alias="基本档案" type="string" length="32" fixed="false"
    display="0" />
	
  <relations>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
      <join parent="empiId" child="empiId" />
    </relation>
    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_ChildInfo">
      <join parent="childEmpiId" child="empiId" />
    </relation>
    <relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord">
      <join parent="empiId" child="empiId" />
    </relation>
  </relations>
</entry>
