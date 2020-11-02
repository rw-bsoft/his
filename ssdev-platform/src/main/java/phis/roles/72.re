<?xml version="1.0" encoding="UTF-8"?>

<role id="phis.72" name="物资二级库房角色" parent="base" pageCount="5" type="group_12" version="1511433591293">
  <accredit>
    <apps>
      <app id="phis.application.menu.COMM">
        <catagory id="PUB">
          <others/>
        </catagory>
      </app>
      <app id="phis.application.mds.MDS">
        <others/>
      </app>
      <app id="phis.application.top.TOP">
        <catagory id="TOPFUNC">
          <module id="TreasurySwitch">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="phis.application.menu.YYGL">
        <catagory id="SUP_TOW">
          <module id="SUP02">
            <others/>
          </module>
          <module id="SUP0201">
            <others/>
          </module>
          <module id="SUP0202">
            <others/>
          </module>
          <module id="SUP020101">
            <others/>
          </module>
          <module id="SUP02010101">
            <others/>
          </module>
          <module id="SUP02010102">
            <others/>
          </module>
          <module id="SUP030104"/>
          <module id="SUP04">
            <others/>
          </module>
          <module id="SUP0401">
            <others/>
          </module>
          <module id="SUP0402">
            <others/>
          </module>
          <module id="SUP040101">
            <others/>
          </module>
          <module id="SUP04010101">
            <others/>
          </module>
          <module id="SUP04010102">
            <others/>
          </module>
          <module id="SUP07">
            <others/>
          </module>
          <module id="SUP0701">
            <others/>
          </module>
          <module id="SUP0702">
            <others/>
          </module>
          <module id="SUP070101">
            <others/>
          </module>
          <module id="SUP07010101">
            <others/>
          </module>
          <module id="SUP07010102">
            <others/>
          </module>
          <module id="SUP08">
            <others/>
          </module>
          <module id="SUP0801">
            <others/>
          </module>
          <module id="SUP0802">
            <others/>
          </module>
          <module id="SUP080101">
            <others/>
          </module>
          <module id="SUP08010101">
            <others/>
          </module>
          <module id="SUP08010102">
            <others/>
          </module>
          <module id="SUP09">
            <others/>
          </module>
          <module id="SUP0901">
            <others/>
          </module>
          <module id="SUP12">
            <others/>
          </module>
          <module id="SUP1201">
            <others/>
          </module>
          <module id="SUP1202">
            <others/>
          </module>
          <module id="SUP120101">
            <others/>
          </module>
          <module id="SUP12010101">
            <others/>
          </module>
          <module id="SUP12010102">
            <others/>
          </module>
          <module id="SUP15"/>
          <module id="SUP1501">
            <others/>
          </module>
          <module id="SUP1502">
            <others/>
          </module>
          <module id="SUP150101">
            <others/>
          </module>
          <module id="SUP15010101">
            <others/>
          </module>
          <module id="SUP15010102">
            <others/>
          </module>
          <module id="SUP15010103">
            <others/>
          </module>
          <module id="SUP17">
            <others/>
          </module>
          <module id="SUP1701">
            <others/>
          </module>
          <module id="SUP1702">
            <others/>
          </module>
          <module id="SUP1703">
            <others/>
          </module>
          <module id="SUP170101">
            <others/>
          </module>
          <module id="SUP17010102">
            <others/>
          </module>
          <module id="SUP17010101">
            <others/>
          </module>
          <module id="SUP30">
            <others/>
          </module>
          <module id="SUP3001">
            <others/>
          </module>
          <module id="SUP42">
            <others/>
          </module>
          <module id="SUP4201">
            <others/>
          </module>
          <module id="SUP4202">
            <others/>
          </module>
          <module id="SUP420101">
            <others/>
          </module>
          <module id="SUP42010101">
            <others/>
          </module>
          <module id="SUP42010102">
            <others/>
          </module>
          <module id="SUP52">
            <others/>
          </module>
          <module id="SUP5201">
            <others/>
          </module>
          <module id="SUP5202">
            <others/>
          </module>
          <module id="SUP60">
            <others/>
          </module>
        </catagory>
      </app>
      <app id="phis.application.menu.TJFX">
        <catagory id="SUP_2">
          <others/>
        </catagory>
      </app>
      <app id="phis.application.sys.SYS">
        <catagory id="SUP_TWO">
          <others/>
        </catagory>
      </app>
    </apps>
    <storage acType="whitelist"> 
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
      <store id="chis.application.pif.schemas.PUB_PublicInfo" acValue="1111"> 
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
      <!--物资管理一级库房 -->  
      <store id="phis.application.cfg.schemas.WL_SCCJ" acValue="1111"> 
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
