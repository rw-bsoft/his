<?xml version="1.0" encoding="UTF-8"?>

<role id="phis.75" name="全科药房角色" parent="base" pageCount="3" version="1513669481734">
  <accredit>
    <apps>
      <app id="phis.application.menu.TJFX"/>
      <app id="phis.application.menu.TJFX"/>
      <app id="phis.application.sys.SYS"/>
      <app id="phis.application.sys.SYS"/>
      <app id="phis.application.menu.COMM">
        <catagory id="PUB">
          <others/>
        </catagory>
      </app>
      <app id="phis.application.hph.HPH">
        <others/>
      </app>
      <app id="phis.application.pha.PHA">
        <catagory id="PHA">
          <module id="PHA2801">
            <others/>
          </module>
          <module id="PHA77">
            <others/>
          </module>
          <module id="PHA7701">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="phis.application.top.TOP">
        <catagory id="TOPFUNC">
          <module id="PharmacySwitch">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="phis.application.menu.YYGL">
        <catagory id="PHA" acType="blacklist">
          <others/>
        </catagory>
      </app>
      <app id="phis.application.sys.SYS">
        <catagory id="PHA" acType="blacklist">
          <module id="PCM02">
            <others/>
          </module>
        </catagory>
        <catagory id="YB">
          <module id="YB020101"/>
          <module id="YB020201"/>
          <module id="YB020301"/>
          <module id="YB02">
            <others/>
          </module>
          <module id="YB0201">
            <others/>
          </module>
          <module id="YB0202">
            <others/>
          </module>
          <module id="YB0203">
            <others/>
          </module>
        </catagory>
        <catagory id="XNH">
          <module id="XNH0201"/>
          <module id="XNH020102"/>
          <module id="XNH0202"/>
          <module id="XNH020202"/>
          <module id="XNH0104">
            <others/>
          </module>
          <module id="XNH02">
            <others/>
          </module>
          <module id="XNH01">
            <others/>
          </module>
          <module id="XNH0101">
            <others/>
          </module>
          <module id="XNH0102">
            <others/>
          </module>
          <module id="XNH0103">
            <others/>
          </module>
          <module id="XNH020101">
            <others/>
          </module>
          <module id="XNH020201">
            <others/>
          </module>
          <module id="XNH03">
            <others/>
          </module>
          <module id="XNH0301">
            <others/>
          </module>
          <module id="XNH0302">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="phis.application.menu.TJFX">
        <catagory id="PHA"/>
        <catagory id="PHA"/>
        <catagory id="PHA">
          <module id="HPH03">
            <others/>
          </module>
          <module id="HPH0302">
            <others/>
          </module>
          <module id="HPH0301">
            <others/>
          </module>
          <module id="HPH030101">
            <others/>
          </module>
          <module id="HPH030102">
            <others/>
          </module>
          <module id="HPH0303">
            <others/>
          </module>
          <module id="HPH04">
            <others/>
          </module>
          <module id="HPH0401">
            <others/>
          </module>
          <module id="HPH0402">
            <others/>
          </module>
          <module id="PHA22">
            <others/>
          </module>
          <module id="PHA21">
            <others/>
          </module>
          <module id="PHA2101">
            <others/>
          </module>
          <module id="PHA2102">
            <others/>
          </module>
          <module id="PHA210201">
            <others/>
          </module>
          <module id="PHA23">
            <others/>
          </module>
          <module id="PHA24">
            <others/>
          </module>
          <module id="PHA25">
            <others/>
          </module>
          <module id="PHA26">
            <others/>
          </module>
          <module id="PHA27">
            <others/>
          </module>
          <module id="PHA29">
            <others/>
          </module>
          <module id="PHA28">
            <others/>
          </module>
          <module id="PCM03">
            <others/>
          </module>
          <module id="PCM0301">
            <others/>
          </module>
          <module id="PCM030101">
            <others/>
          </module>
          <module id="PCM0302">
            <others/>
          </module>
          <module id="PHA33">
            <others/>
          </module>
          <module id="PHA3301">
            <others/>
          </module>
          <module id="PHA330101">
            <others/>
          </module>
          <module id="PHA330102">
            <others/>
          </module>
          <module id="PHA32">
            <others/>
          </module>
          <module id="PHA34">
            <others/>
          </module>
        </catagory>
        <catagory id="KJY">
          <module id="WAR54">
            <others/>
          </module>
          <module id="WAR55">
            <others/>
          </module>
          <module id="WAR56">
            <others/>
          </module>
          <module id="WAR57">
            <others/>
          </module>
          <module id="WAR58">
            <others/>
          </module>
		  <module id="WAR59">
            <others/>
          </module>
          <module id="WAR_MZKJYWMX">
            <others/>
          </module>
        </catagory>
      <!-- add renwei 2020-08-07 门诊基本药物使用统计 -->   
          <catagory id="JBY">
          <module id="WAR71">
            <others/>
          </module>
          <module id="WAR72">
            <others/>
          </module>
          <module id="WAR73">
            <others/>
          </module>
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
