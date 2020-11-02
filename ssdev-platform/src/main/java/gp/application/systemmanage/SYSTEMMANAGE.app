<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.systemmanage.SYSTEMMANAGE"
	name="系统管理">
	<catagory id="DATA" name="基础数据维护">
		<module id="DA01" name="机构科室注册" ref="platform.reg.REG/REGCATA1/REG_01" />	
		<module id="DA02" name="人员注册" ref="platform.reg.REG/REGCATA1/REG_02" />
		<module id="DA03" name="用户注册" ref="platform.reg.REG/REGCATA1/REG_03" />
		<!--<module id="DA05" ref="platform.reg.REG/REGCATA1/REG_04" />-->
		<module id="DA04" name="组织架构维护" ref="platform.reg.REG/SYS/SYS_05" />
	</catagory>
	<catagory id="SYS" name="系统维护">
		<module id="SYS01" name="域注册" ref="platform.reg.REG/SYS/SYS_01" />
		<module id="SYS03" name="角色管理" ref="platform.reg.REG/SYS/SYS_02" />
		<module id="SYS02" name="应用菜单管理" ref="platform.reg.REG/SYS/SYS_03" />
		<module id="SYS04" name="组织架构类型" ref="platform.reg.REG/SYS/SYS_04" />
		<module id="SYS05" name="字典管理" ref="platform.reg.REG/SYS/SYS_07" />
		<module id="SYS06" name="数据模型管理" ref="platform.reg.REG/SYS/SYS_08" />
		
		<module id="SYS07" name="同步机构数据" ref="gp.application.config.CONFIG/CONFIG/CONFIG01" />
	</catagory>
</application>

