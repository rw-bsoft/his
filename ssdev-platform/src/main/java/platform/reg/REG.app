<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.reg.REG" name="配置管理">
	<catagory id="REGCATA1" name="注册管理">
		<module id="REG_01" name="机构科室注册" script="app.modules.config.ManageUnitConfigPanel" iconCls="RD02">
				<properties>
					<p name="dicId">platform.reg.dictionary.organizationDic</p>
					<p name="entryName">platform.reg.schemas.SYS_Organization</p>
				</properties>
				<action id="create" name="新建" iconCls="add" />
				<action id="update" name="修改" />
				<action id="logoff" name="注销" iconCls="remove"/>
			</module>
		<!-- 权限测试用 -->
		<module id="REG_02" name="人员注册" script="app.modules.config.PersonUnitPanel" iconCls="S02">
			<properties>
				<p name="dicId">platform.reg.dictionary.organWithOfficeDic</p>
				<p name="entryName">platform.reg.schemas.SYS_Personnel</p>
				<p name="loadServiceId">platform.simpleLoad</p>
				<p name="createCls">app.modules.config.user.PersonConfigForm</p>
				<p name="updateCls">app.modules.config.user.PersonConfigForm</p>
			</properties>
			<action id="create" name="新建" iconCls="add" />
			<action id="update" name="修改" />
			<action id="logoff" name="注销" iconCls="remove"/>
			<action id="excel" name="excel导入人员信息"/>
			<action id="recover" name="恢复" iconCls="refresh"/>
		</module>
		<module id="REG_03" name="用户注册" script="app.modules.config.user.UserConfigPanel" iconCls="A01">
			<properties>
				<p name="dicId">platform.reg.dictionary.rolelist</p>
				<p name="loadServiceId">platform.simpleLoad</p>
			</properties>
			<action id="create" name="新建" iconCls="add" />
			<action id="update" name="修改" />
		</module>
		<module id="REG_04" name="添加关联管理" script="gp.application.rel.script.RelevanceManageList" iconCls="A01">
			<properties>
				<p name="loadServiceId">gp.simpleLoad</p>
			</properties>
			<action id="create" name="新建" iconCls="add" />
			<action id="update" name="修改" />
		</module>
	</catagory>
	<catagory id="SYS" name="应用域管理">
		<module id="SYS_01" name="域注册" script="app.modules.config.domain.DomainListView" iconCls="AB1">
			<properties>
				<p name="entryName">platform.reg.schemas.SYS_DOMAIN</p>
				<p name="saveServiceId">platform.domainConfig</p>
				<p name="loadServiceId">platform.simpleLoad</p>
				<p name="serviceId">platform.domainConfig</p>
				<p name="fieldId">ID</p>
				<p name="textField">DomainName</p>
			</properties>
			<action id="create" name="新建" iconCls="add" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="SYS_03" name="应用菜单管理" script="app.modules.config.AppConfigList" iconCls="S05">
			<properties>
				<p name="dicId">applist4config</p>
			</properties>
		</module>
		<module id="SYS_02" name="角色管理" script="app.modules.config.RoleConfigList" iconCls="E01">
			<properties>
				<p name="dicId">platform.reg.dictionary.rolelist</p>
				<p name="dirType">banners</p>
			</properties>
			<action id="create" name="新建" iconCls="add" />
			<action id="remove" name="删除" />
		</module>
		<module id="SYS_04" name="组织架构类型" script="app.modules.config.UnitTypeForm" iconCls="E01">
		</module>
		<module id="SYS_05" name="组织架构" script="app.modules.config.unit.manageUnitBuilder" iconCls="E01">
			<properties>
				<p name="dicId">manageUnit</p>
				<p name="listServiceId">platform.simpleQuery</p>
			</properties>
		</module>
		<module id="SYS_07" name="字典管理" script="app.modules.config.dic.DicConfig" iconCls="S02">
		</module>
		<module id="SYS_08" name="数据模型管理" script="app.modules.config.SchemaConfig" iconCls="E01">
				<properties>
					<p name="dicId">platform.reg.dictionary.schemalist</p>
					<p name="entryName">platform.reg.schemas.SYS_Organization</p>
				</properties>
		</module>
	</catagory>
</application>