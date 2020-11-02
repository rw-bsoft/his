<?xml version="1.0" encoding="UTF-8"?>

<role id="phis.93" name="处方流转审核" parent="base" pageCount="3" version="1513669481734">
  <accredit>
    <apps>
      <app id="phis.application.top.TOP">
        <catagory id="TOPFUNC">
          <module id="PharmacySwitch">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="phis.application.menu.YYGL">
        <catagory id="CFLZ" acType="blacklist">
          <others/>
        </catagory>
      </app>
     
    </apps>
    <storage acType="whitelist"> 
      <!--药房模块 -->  
      <store id="phis.application.pha.schemas.YF_KCDJ_CX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_YPXX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_YFLB" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YK_YPXX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.sto.schemas.YK_YPXX_BZ" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_RKFS" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_CKFS" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_YPXX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_RK01" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_RK01_QR" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_CK01" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_CK01_QR" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_KCMX_CSH" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_KCMX_JY" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.MS_CF01_YFFY" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.sto.schemas.YK_TJ01" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.sto.schemas.YK_TJ01_ZX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_JZJL" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pha.schemas.YF_YK01_RQ" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <!--住院药房模块 -->  
      <store id="BQ_TJ01_TJ" acValue="1111"> 
        <conditions> 
          <condition type="filter">['and',['eq',['$','a.TJYF'],["$",'%user.prop.pharmacyId','d']],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="BQ_TJ02_FY" acValue="1111"> 
        <conditions> 
          <condition type="filter">['and',['eq',['$','d.TJYF'],["$",'%user.prop.pharmacyId','d']],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="ZY_BRRY_BQTY" acValue="1111"> 
        <conditions> 
          <condition type="filter">['and',['eq',['$','a.YFSB'],["$",'%user.prop.pharmacyId','d']],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="YF_FYJL_LSCX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="YF_ZYFYMX_LSCX" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <!--住院药房模块结束 -->  
      <!-- 维护模块 -->  
      <store id="phis.application.pub.schemas.GY_QXKZ_CFG" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.pub.schemas.SYS_Personnel_CFG" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="SYS_Personnel_YH" acValue="1111"> 
        <conditions> 
          <condition type="filter">['and',['like', ['$','a.JGID'], ['concat',['$','%user.manageUnit.id'],['s','%']] ],['eq',['$','a.ZFPB'],["s",'0']]]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="PUB_PublicInfo" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.publishUnit'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <store id="phis.application.cfg.schemas.GY_XTCS" acValue="1111"> 
        <conditions> 
          <condition type="filter">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</condition> 
        </conditions>  
        <items> 
          <others acValue="1111"/> 
        </items> 
      </store>  
      <others acValue="1111"/> 
    </storage>
  </accredit>
</role>
