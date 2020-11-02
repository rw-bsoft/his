<?xml version="1.0" encoding="UTF-8"?>

<role id="phis.system" name="系统管理员" parent="phis.base" pageCount="1" version="1511433202115">
  <accredit>
    <apps>
      <app id="phis.application.sys.SYS">
        <catagory id="REG">
          <others/>
        </catagory>
        <catagory id="SYS">
          <others/>
        </catagory>
        <catagory id="YJ_CFG">
          <others/>
        </catagory>
      </app>
    </apps>
    <storage acType="whitelist"> 
      <store id="phis.application.cfg.schemas.GY_YLMX_DR" acValue="1111"> 
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
