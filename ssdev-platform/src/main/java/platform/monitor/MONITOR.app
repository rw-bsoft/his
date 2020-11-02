<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.monitor.MONITOR" name="监控管理">
	<catagory id="MONITOR_CATA" name="监控管理">
		<module id="MONITOR_01" name="数据交换监控" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/ETL/RpcMonitor.html</p>
			</properties>
		</module>
		<module id="MONITOR_02" name="数据采集监控" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/ETL/Destktop.html</p>
			</properties>
		</module>
		<module id="MONITOR_03" name="应用服务器监控" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://172.16.171.250:8083/rpcmonitor/client/index.html</p>
				<p name="param"><![CDATA[?mainCls=rpc.event.SystemEventList]]></p>
			</properties>
		</module>
		<module id="MONITOR_04" name="性能监控" script="sys.monitor.MonMain">
		</module>
		<module id="MONITOR_05" name="数据采集报表" script="platform.monitor.client.QuaMain">
			<properties>
				<p name="loadService">platform.qualityData</p>
			</properties>
		</module>
		<module id="MONITOR_06" name="消息传输日志" script="platform.monitor.client.MessageLog">
		</module>
	</catagory>
</application>