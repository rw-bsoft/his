<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.wjz.WJZ" name="危机值管理">
	<catagory id="WJZ" name="危机值管理">
		<module id="WJZ01" name="危机值处理录入" type="1"
			script="phis.application.wjz.script.WjzclrlForm">
			<properties>
				<p name="entryName">phis.application.wjz.schemas.JG_WJZCZ</p>
				<p name="refCyyList">phis.application.wjz.WJZ/WJZ/WJZ0101</p>
			</properties>
		<!--	<action id="cyy" name="常用语" iconCls="default"/>-->
			<action id="qr" name="确认" iconCls="drug"/>
			<action id="cancel" name="退出" iconCls="common_cancel"/>
		</module>
		<module id="WJZ0101" name="危机值处理常用语" type="1"
			script="phis.application.wjz.script.WjzclrlCyyList">
			<properties>
				<p name="entryName">phis.application.wjz.schemas.JG_WJZCYY</p>
			</properties>
			<action id="qr" name="确认" iconCls="drug"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>
		</module>
	</catagory>
</application>
