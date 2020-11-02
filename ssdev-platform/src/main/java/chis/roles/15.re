<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.15" name="防保员" parent="base" pageCount="1" type="T15" version="1498197950518">
  <accredit>
    <apps>
      <app id="chis.application.diseasemanage.DISEASEMANAGE"/>
      <app id="chis.application.diseasemanage.DISEASEMANAGE"/>
      <app id="chis.application.index.INDEX">
        <others/>
      </app>
      <app id="chis.application.diseasemanage.DISEASEMANAGE">
        <catagory id="HY"/>
        <catagory id="DBS">
          <module id="D05-list">
            <action id="print"/>
          </module>
        </catagory>
        <catagory id="OHR">
          <module id="B14_02">
            <others/>
          </module>
          <module id="B14_04">
            <others/>
          </module>
          <module id="B20-1">
            <others/>
          </module>
          <module id="B4">
            <others/>
          </module>
          <module id="B41">
            <action id="modify"/>
            <action id="visit"/>
          </module>
          <module id="B19"/>
          <module id="B14">
            <others/>
          </module>
          <module id="B14_01"/>
          <module id="B14_02_01"/>
          <module id="B14_03"/>
          <module id="B5">
            <others/>
          </module>
          <module id="B15">
            <others/>
          </module>
          <module id="B20">
            <others/>
          </module>
          <module id="B20-2"/>
          <module id="B6">
            <others/>
          </module>
          <module id="B61">
            <action id="modify"/>
          </module>
          <module id="B62">
            <others/>
          </module>
          <module id="B62_1"/>
          <module id="B62_2"/>
          <module id="B62_3"/>
        </catagory>
        <catagory id="HY">
          <module id="D01-hr"/>
          <module id="D01-hr">
            <action id="print"/>
          </module>
        </catagory>
        <catagory id="TR">
          <others/>
        </catagory>
      </app>
      <app id="chis.application.diseasecontrol.DISEASECONTROL">
        <catagory id="DC">
          <others/>
        </catagory>
      </app>
      <app id="chis.application.healthmanage.HEALTHMANAGE">
        <catagory id="HR">
          <module id="B01">
            <others/>
          </module>
          <module id="B01_">
            <action id="modify"/>
          </module>
          <module id="B011">
            <others/>
          </module>
          <module id="B011_1"/>
          <module id="B011_2">
            <action id="readMember"/>
          </module>
          <module id="B011_2_1">
            <others/>
          </module>
          <module id="B011_3">
            <action id="update"/>
          </module>
          <module id="B011_3_1">
            <others/>
          </module>
          <module id="B011_5">
            <others/>
          </module>
          <module id="B011_51">
            <others/>
          </module>
          <module id="B011_5_1_1">
            <others/>
          </module>
          <module id="B011_6">
            <others/>
          </module>
          <module id="B011_6_1">
            <others/>
          </module>
          <module id="B011_6_2">
            <action id="modify"/>
          </module>
          <module id="B0110"/>
          <module id="B0110_1">
            <others/>
          </module>
          <module id="B0110_2">
            <action id="readMember"/>
          </module>
          <module id="B0110_7">
            <others/>
          </module>
          <module id="B0110_8">
            <others/>
          </module>
          <module id="B0110">
            <others/>
            <others/>
          </module>
          <module id="B08">
            <others/>
          </module>
          <module id="B081">
            <action id="showModule"/>
          </module>
          <module id="B09"/>
          <module id="B11">
            <action id="update"/>
            <action id="print"/>
          </module>
          <module id="B1101">
            <action id="cancel"/>
          </module>
          <module id="B12">
            <others/>
          </module>
          <module id="B34">
            <others/>
          </module>
          <module id="B341">
            <action id="new"/>
          </module>
          <module id="B34101">
            <others/>
          </module>
          <module id="B3410101"/>
          <module id="B07">
            <others/>
          </module>
        </catagory>
        <catagory id="HER">
          <module id="HE02_01_01">
            <others/>
          </module>
          <module id="HE01">
            <action id="create"/>
            <action id="update"/>
            <action id="logOut"/>
            <action id="remove"/>
            <action id="print"/>
          </module>
          <module id="HE01_01">
            <others/>
          </module>
          <module id="HE01_01_01">
            <action id="save"/>
          </module>
          <module id="HE02_01">
            <action id="action1"/>
            <action id="action2"/>
          </module>
          <module id="HE02_01_02">
            <action id="updateHEC"/>
          </module>
          <module id="HE02_01_0201">
            <action id="action1"/>
            <action id="action2"/>
          </module>
          <module id="HE02_01_0201_01">
            <action id="fileView"/>
            <action id="printTeach"/>
          </module>
          <module id="HE02_01_0201_0101">
            <action id="fileDownLoad"/>
          </module>
          <module id="HE02_01_0201_02"/>
          <module id="HE01_01_02">
            <action id="update"/>
          </module>
          <module id="HE04">
            <others/>
          </module>
          <module id="HE04_01">
            <others/>
          </module>
          <module id="HE04_02">
            <others/>
          </module>
          <module id="HE04_03">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="chis.application.systemmanage.SYSTEMMANAGE">
        <catagory id="PC">
          <module id="AB1">
            <action id="createInfo"/>
            <action id="modify"/>
            <action id="remove"/>
          </module>
          <module id="AB1_1">
            <action id="save"/>
            <action id="cancel"/>
          </module>
        </catagory>
      </app>
    </apps>
    <storage acType="whitelist"> 
      <store id="chis.application.ohr.schemas.MDC_OldPeopleRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.dc.schemas.DC_RabiesRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like', ['$','a.createUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.pc.schemas.ADMIN_ProblemCollect" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq', ['$','a.createUser'], ['$','%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hr.schemas.EHR_HealthRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.fhr.schemas.EHR_FamilyRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <others acValue="1111"/> 
    </storage>
  </accredit>
</role>
