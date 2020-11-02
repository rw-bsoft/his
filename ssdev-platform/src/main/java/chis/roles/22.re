<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.22" name="健康档案二级审核" parent="base" pageCount="4" type="T22" version="1557142966998">
  <accredit>
    <apps>
      <app id="chis.application.index.INDEX">
        <others/>
      </app>
      <app id="chis.application.healthmanage.HEALTHMANAGE">
        <catagory id="OHR">
          <others/>
        </catagory>
        <catagory id="HR">
          <others/>
        </catagory>
        <catagory id="HMC">
          <module id="HMC01">
            <others/>
          </module>
          <module id="HMC0101">
            <action id="modify"/>
            <action id="verify"/>
            <action id="cancelVerify"/>
            <action id="back"/>
          </module>
        </catagory>
      </app>
      <app id="chis.application.diseasemanage.DISEASEMANAGE">
        <catagory id="RVC">
          <others/>
        </catagory>
        <catagory id="HY">
          <others/>
        </catagory>
        <catagory id="DBS">
          <others/>
        </catagory>
        <catagory id="PSY">
          <others/>
        </catagory>
        <catagory id="TR">
          <others/>
        </catagory>
        <catagory id="DEF">
          <others/>
        </catagory>
        <catagory id="INC">
          <others/>
        </catagory>
        <catagory id="CONS">
          <others/>
        </catagory>
      </app>
      <app id="chis.application.hmc.HMC">
        <others/>
      </app>
    </apps>
    <storage acType="whitelist"> 
      <store id="chis.application.hr.schemas.EHR_HealthRecord" acValue="1111"> 
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
