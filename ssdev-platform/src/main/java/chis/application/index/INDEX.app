<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.index.INDEX" name="首页" type="index" >
	<properties>
		<p name="entryName">chis.application.index.schemas.SYS_HomePage</p>
	</properties>
	<module id="MYPAGE01" name="个人信息" script="chis.application.index.script.LanderInfo2">
	</module>
	
	<module id="MYPAGE02" name="辖区健康分析" script="chis.script.chart.QTChartView"/>
	<module id="MYPAGE03" name="任务列表" script="chis.application.index.script.MyWorkListNew">
		<properties>
			<p name="entryName">chis.application.pub.schemas.PUB_WorkListNew</p>
			<p name="enableCnd">false</p>
			<p name="autoLoadSchema">false</p>
			<p name="isCombined">true</p>
			<p name="disablePagingTbr">true</p>
			<p name="listServiceId">chis.myWorkListService</p>
			<p name="listAction">getMyWorkList</p>
		</properties>
	</module>
	<module id="MYPAGE04" name="工作计划" script="chis.application.index.script.ReminderModule" />
	<module id="MYPAGE05" name="管理对象分析" script="chis.application.index.script.RecordGrid" />
	<module id="MYPAGE06" name="公告维护"
		script="chis.application.pif.script.PublicInfoListView2">
		<properties>
			<p name="entryName">chis.application.pif.schemas.PUB_PublicInfoNew</p>
		</properties>
	</module>
	<module id="MYPAGE03_1" name="公告维护表单" type="1"
		script="chis.application.pif.script.PublicInfoFormView2">
		<properties>
			<p name="entryName">chis.application.pif.schemas.PUB_PublicInfo</p>
			<p name="autoLoadSchema">true</p>
			<p name="isCombined">true</p>
		</properties>
	</module>
	<!-- 
	<module id="MYPAGE04" name="基本统计" script="app.modules.chart.DiggerChartView">
		<properties>
			<p name="entryName">DctRootChart</p>
		</properties>
	</module>
	 -->
</application>