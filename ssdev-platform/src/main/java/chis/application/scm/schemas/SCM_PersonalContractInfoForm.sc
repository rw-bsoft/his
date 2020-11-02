<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schema.SCM_PersonalContractInfoForm" alias="个人信息表">
    <item id="favoreeName" alias="服务人" type="string" length="20"  xtype="lookupfieldex" not-null="1">
    </item>
    <item id="favoreeEmpiId" alias="favoreeEmpiId" type="string" length="32" fixed="true" notDefaultValue="true" hidden="true"/>
    <item id="idCard" alias="身份证号" type="string" length="20" width="160" fixed="true" vtype="idCard" enableKeyEvents="true"/>
    <item id="sexCode" alias="性别" type="string" length="1" width="40" fixed="true"  defaultValue="9"/>
    <item id="birthday" alias="出生日期" type="date" width="75" queryable="true" fixed="true"  />
    <item id="mobileNumber" alias="本人电话" type="string" length="20"  fixed="true" width="90"/>
</entry>
