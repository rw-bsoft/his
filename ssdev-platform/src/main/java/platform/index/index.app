<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.index.index" name="首页" type="index">
   <properties>
      <p name="entryName">platform.index.schemas.SYS_HomePage</p>
   </properties>
   <module id="REG3_01" name="机构注册" script="app.modules.list.SimpleListView" iconCls="RD02">
	  <properties>
		 <p name="dicId">platform.reg.dictionary.adminDivision</p>
		 <p name="entryName">platform.reg.schemas.SYS_Organization</p>
		 <!--  <p name="daoServiceId">platform.daoService</p> -->
	  </properties>
   </module>
   <module id="SYS_02" name="角色管理" script="app.modules.config.RoleConfigList" iconCls="E01">
	  <properties>
		  <p name="dicId">platform.reg.dictionary.rolelist</p>
	  </properties>
   </module>
   <module id="SYS_01" name="应用注册" script="app.modules.config.domain.DomainListView" iconCls="AB1">
	  <properties>
		  <p name="entryName">platform.reg.schemas.SYS_DOMAIN</p>
		  <p name="saveServiceId">domainConfig</p>
		  <p name="serviceId">domainConfig</p>
		  <p name="fieldId">ID</p>
		  <p name="textField">DomainName</p>
	  </properties>
  </module>
</application>