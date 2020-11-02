<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.security.SECURITY" name="安全管理">
	<catagory id="SECCATA" name="安全管理">
		<module id="SEC_01" name="节点认证" script="app.modules.list.SimpleListView">
			<properties>
				<p name="entryName">platform.security.schemas.R_SLAVE</p>
				<p name="initCnd">['eq',['$','FLAG'],['s','0']]</p>
				<p name="updateCls">sys.auth.NodeAuthForm</p>
			</properties>
			<action id="update" name="认证管理" />
		</module>
		<module id="SEC_02" name="隐私管理" script="app.modules.list.TreeNavListView">
			<properties>
				<p name="entryName">platform.security.schemas.SYS_PrivacyRules</p>
				<p name="navDic">platform.security.dic.sysPrivacyRule</p>
				<p name="treeTitle">规则类型</p>
				<p name="navField">rule</p>
			</properties>
			<action id="create" name="新建" iconCls="add" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="SEC_03" name="审计管理" script="sys.audit.Log4jList">
			<properties>
				<p name="entryName">platform.security.schemas.SYS_Log4j</p>
				<p name="dicId">platform.security.dic.logType</p>
			</properties>
			<action id="sql" name="查看sql信息" iconCls="read" />
			<action id="service" name="查看service信息" iconCls="read" />
		</module>
		<module id="SEC_04" name="健康浏览器隐私控制" script="app.lang.EmbedPanel" iconCls="E01">
			<properties>
				<p name="url"><![CDATA[http://172.16.171.250:4444/ehrview/ralasafe/main.jsp]]></p>
			</properties>
		</module>
	</catagory>
</application>