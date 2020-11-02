<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.102" name="信息录入" parent="base" pageCount="4" type="T102" version="1525406065887">
  <accredit>
    <apps>
      <app id="chis.application.healthmanage.HEALTHMANAGE"/>
      <app id="chis.application.healthmanage.HEALTHMANAGE"/>
      <app id="chis.application.healthmanage.HEALTHMANAGE"/>
      <app id="chis.application.healthmanage.HEALTHMANAGE"/>
      <app id="chis.application.index.INDEX">
        <others/>
      </app>
      <app id="chis.application.healthmanage.HEALTHMANAGE">
        <catagory id="HR">
          <module id="B34">
            <others/>
          </module>
          <module id="B341">
            <action id="xg"/>
            <action id="tjb"/>
          </module>
          <module id="B34101"/>
          <module id="B3410101">
            <others/>
          </module>
          <module id="B08">
            <others/>
          </module>
          <module id="B081">
            <action id="modify"/>
          </module>
          <module id="A04">
            <others/>
          </module>
          <module id="A04_1">
            <others/>
          </module>
          <module id="A07">
            <others/>
          </module>
          <module id="A07_1">
            <others/>
          </module>
          <module id="B07">
            <others/>
          </module>
          <module id="B09">
            <others/>
          </module>
          <module id="B10">
            <others/>
          </module>
          <module id="B1001">
            <others/>
          </module>
          <module id="B11">
            <others/>
          </module>
          <module id="B1101">
            <others/>
          </module>
          <module id="B12">
            <others/>
          </module>
          <module id="HC01">
            <others/>
          </module>
          <module id="HC0101">
            <others/>
          </module>
          <module id="D20">
            <others/>
          </module>
          <module id="D20_1">
            <others/>
          </module>
          <module id="D20_2">
            <others/>
          </module>
          <module id="D20_2_1">
            <others/>
          </module>
          <module id="D20_2_2">
            <others/>
          </module>
          <module id="D20_2_3">
            <others/>
          </module>
          <module id="D20_2_4">
            <others/>
          </module>
          <module id="D20_2_5">
            <others/>
          </module>
          <module id="D20_2_6">
            <others/>
          </module>
          <module id="D20_2_6_1">
            <others/>
          </module>
          <module id="D20_2_6_1_1">
            <others/>
          </module>
          <module id="D20_2_6_2">
            <others/>
          </module>
          <module id="D20_2_6_2_1">
            <others/>
          </module>
          <module id="D20_2_7">
            <others/>
          </module>
          <module id="D20_2_8">
            <others/>
          </module>
          <module id="D20_2_8_1">
            <others/>
          </module>
          <module id="B16">
            <others/>
          </module>
          <module id="B16-1">
            <others/>
          </module>
          <module id="B0110">
            <others/>
          </module>
          <module id="B0110_1">
            <others/>
          </module>
          <module id="B0110_2">
            <others/>
          </module>
          <module id="B0110_7">
            <others/>
          </module>
          <module id="B0110_8">
            <others/>
          </module>
          <module id="B17">
            <others/>
          </module>
          <module id="B17-1">
            <others/>
          </module>
          <module id="B17-2">
            <others/>
          </module>
          <module id="B17-2-1">
            <others/>
          </module>
          <module id="B17-2-2">
            <others/>
          </module>
          <module id="B18">
            <others/>
          </module>
          <module id="B18-1">
            <others/>
          </module>
          <module id="B18-2">
            <others/>
          </module>
          <module id="B18-2-1">
            <others/>
          </module>
          <module id="B18-2-2">
            <others/>
          </module>
          <module id="B05">
            <others/>
          </module>
          <module id="B051">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="chis.application.hr.HR">
        <others/>
      </app>
    </apps>
    <storage acType="whitelist"> 
      <store id="chis.application.pub.schemas.EHR_CommonTaskList" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.taskDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hr.schemas.EHR_PastHistorySearch" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','b.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hr.schemas.EHR_PastHistorySearch" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','b.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.cvd.schemas.CVD_AssessRegister" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','c.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <!--
      <store id="chis.application.hr.schemas.EHR_HealthRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
	    -->  
      <store id="chis.application.hc.schemas.HC_HealthCheck" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.createUser'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.ppvr.schemas.EHR_PoorPeopleVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUser'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.npvr.schemas.EHR_NormalPopulationVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.createUser'],["$",'%user.userId']]</condition> 
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
      <store id="chis.application.gdr.schemas.GDR_GroupDinnerRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaUnitId'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.ohr.schemas.MDC_OldPeopleRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.rvc.schemas.RVC_RetiredVeteranCadresRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.dbs.schemas.MDC_DiabetesRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.ohr.schemas.MDC_OldPeopleVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.rvc.schemas.RVC_RetiredVeteranCadresVisit_list" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','e.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.piv.schemas.PIV_VaccinateRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.cdh.schemas.CDH_HealthCard" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.cdh.schemas.CDH_DefectRegister" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.cdh.schemas.CDH_DeadRegister_Module" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','b.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.cdh.schemas.CDH_DebilityChildren" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.cdh.schemas.CDH_BirthCertificate" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mov.schemas.MOV_EHRConfirm" acValue="1111"> 
        <conditions> 
          <condition type="filter">['or',['eq',['$','a.sourceDoctor'],['$','%user.userId']], ['eq',['$','a.targetDoctor'],['$','%user.userId']], ['eq',['$','a.applyUser'],['$','%user.userId']] ]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mov.schemas.MOV_ManaInfoBatchChange" acValue="1111"> 
        <conditions> 
          <condition type="filter">['and',['eq',['$','a.archiveType'],['s','1']],['or',['eq',['$','a.applyUser'],['$','%user.userId']],['eq',['$','a.targetDoctor'],['$','%user.userId']]]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mov.schemas.MOV_HealthRecordQuery" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
        </conditions>  
        <items> 
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mov.schemas.MOV_ManaInfoChange" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.applyUser'],['$','%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionFixGroup" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionFirst" acValue="1111"> 
        <conditions> 
          <condition type="filter">['and',['eq',['$','b.manaDoctorId'],["$",'%user.userId']],['ne',['$','diagnosisType'],["$",'03']]]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionRisk" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionRiskAssessment" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionRiskVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_HypertensionSimilarity" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.dbs.schemas.MDC_DiabetesVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.dbs.schemas.MDC_DiabetesSimilarity" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_DiabetesRisk" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_DiabetesRiskAssessment" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_DiabetesRiskVisit" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="MDC_TumourQC" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','c.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/>  
              <o target="fixed" value="true"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mhc.schemas.MHC_PregnantRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mhc.schemas.MHC_VisitRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mhc.schemas.MHC_Postnatal42dayRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','d.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mhc.schemas.MHC_PregnantStopManage" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','c.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.mhc.schemas.MHC_HighRiskVisitReasonList" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','c.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.per.schemas.PER_CheckupRegister" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like',['$','a.checkupOrganization'],['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.ohr.schemas.MDC_ChineseMedicineManageListView" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','c.manaDoctorId'],['$','%user.userId']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.psy.schemas.PSY_PsychosisRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <item id="manaDoctorId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.userId"/> 
            </condition> 
          </item>  
          <item id="manaUnitId"> 
            <condition type="override"> 
              <o target="defaultValue" value="%user.manageUnit.id"/> 
            </condition> 
          </item>  
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="MDC_TumourRecord" acValue="0000"/>  
      <store id="MDC_TumourVisit" acValue="0000"/>  
      <store id="chis.application.sch.schemas.SCH_SchistospmaManage" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like',['$','a.inputUnit'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]</condition> 
        </conditions> 
      </store>  
      <store id="chis.application.sch.schemas.SCH_SnailBaseInfomation" acValue="1111"> 
        <conditions> 
          <condition type="filter">['like',['$','a.inputUnit'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]</condition> 
        </conditions> 
      </store>  
      <store id="chis.application.her.schemas.HER_EducationPlanExe" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.executePerson'],['concat',['$','%user.manageUnit.id'],['s','@'],["$",'%user.userId']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.her.schemas.HER_EducationRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','b.executePerson'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.her.schemas.HER_RecipelRecordList" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.createUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.her.schemas.HER_HealthRecipeRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','c.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.his.schemas.HIS_ClinicRecord" acValue="1111"> 
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.his.schemas.HIS_ClinicDiag" acValue="1111"> 
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.his.schemas.HIS_ClnicLab" acValue="1111"> 
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.his.schemas.HIS_Recipe" acValue="1111"> 
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.his.schemas.HIS_ClnicLabDetail" acValue="1111"> 
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.his.schemas.HIS_RecipeDetailOther" acValue="1111"> 
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.def.schemas.DEF_LimbDeformityRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.def.schemas.DEF_BrainDeformityRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.def.schemas.DEF_IntellectDeformityRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.fhr.schemas.EHR_FamilyRecord" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="chis.application.hy.schemas.MDC_Hypertension_FCBP" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.measureDoctor'],["$",'%user.userId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <others acValue="1111"/> 
    </storage>
  </accredit>
</role>