<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.transport.TRANSPORT" name="数据交换">
	<catagory id="DATACATA" name="数据交换">
		<module id="DATA_01" name="节点管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/ETL/ControlView.html</p>
			</properties>
		</module>
		<module id="DATA_02" name="服务管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://172.16.171.250:8083/rpcmonitor/client/index.html</p>
			</properties>
		</module>
		<module id="DATA_03" name="适配器管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://172.16.171.250:8083/rpcmonitor/client/index.html</p>
				<p name="param"><![CDATA[?mainCls=rpc.adapter.AdapterManager]]></p>
			</properties>
		</module>
	</catagory>
	<catagory id="ETLCATA" name="数据采集">
		<module id="ETL_01" name="业务管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/ETL/BizManage.html</p>
			</properties>
		</module>
		<module id="ETL_02" name="脚本管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/ETL/ScriptManage.html</p>
			</properties>
		</module>
		<module id="ETL_03" name="运行管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/ETL/RunningStateManage.html</p>
			</properties>
		</module>
	</catagory>
</application>