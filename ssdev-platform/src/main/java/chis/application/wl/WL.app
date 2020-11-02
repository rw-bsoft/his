<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.wl.WL" name="工作计划"  type="1">
	<catagory id="WL" name="工作计划">
     <module id="A01" name="慢病工作列表" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.wl.schemas.EHR_CommonTaskList</p>  
					<p name="manageUnitField">c.manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.wl.WL/WL/A01_1"/> 
			</module>  
			<module id="A01_1" name="慢病精神病老年人工作列表" script="chis.application.wl.script.CommonTaskList" type="1"> 
				<properties>
					<p name="entryName">chis.application.wl.schemas.EHR_CommonTaskList</p>  
					<p name="listServiceId">chis.commonTaskListService</p>  
					<p name="listAction">getTaskList</p> 
				</properties>  
				<action id="visit" name="随访" iconCls="hypertension_visit"/> 
			</module>  
			<module id="A02" name="妇保工作列表" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.wl.schemas.EHR_MHCTaskList</p>  
					<p name="manageUnitField">d.manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.wl.WL/WL/A02_1"/> 
			</module>  
			<module id="A02_1" name="孕产妇保健工作列表" script="chis.application.wl.script.MHCTaskList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.wl.schemas.EHR_MHCTaskList</p>  
					<p name="listServiceId">chis.mhcTaskListService</p>  
					<p name="listAction">getTaskList</p> 
				</properties>  
				<action id="visit" name="随访" iconCls="hypertension_visit"/> 
			</module>  
			<module id="A03" name="儿保工作列表" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.wl.schemas.EHR_CDHTaskList</p>  
					<p name="manageUnitField">d.manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.wl.WL/WL/A03_1"/> 
			</module>  
			<module id="A03_1" name="儿童保健工作列表" script="chis.application.wl.script.CDHTaskList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.wl.schemas.EHR_CDHTaskList</p>  
					<p name="listServiceId">chis.cdhTaskListService</p>  
					<p name="listAction">getTaskList</p> 
				</properties>  
				<action id="visit" name="随访" iconCls="hypertension_visit"/> 
			</module>
			<module id="R04" name="医疗慢病核实"
			script="chis.application.wl.script.FromhisList">
			<properties>
				<p name="entryName">chis.application.wl.schemas.MDC_FromHis</p>
				<p name="navField">Status</p>
				<p name="refConfirmModule">chis.application.wl.WL/WL/R04_2</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="R04_2" name="医疗慢病核实表单" type="1"
			script="chis.application.wl.script.FromhisConfirmForm">
			<properties>
				<p name="entryName">chis.application.wl.schemas.MDC_FromHis</p>
			</properties>
			<action id="jumpEHR" name="EHR" iconCls="healthDoc_familyGraphic"/>
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<!--<module id="R05" name="体检项目匹配" script="chis.application.wl.script.HssxmmatchList">
			<properties>
				<p name="entryName">chis.application.wl.schemas.View_TjxmMatch</p>
				<p name="refform">chis.application.wl.WL/WL/R05_01</p>
			</properties>
			<action id="match" name="匹配" iconCls="update" />-->
			<!--体检项目是公用的，起初设计有问题，下面两个功能屏蔽掉
			<action id="synchronize" name="同步到其他医院" iconCls="arrow-up" />
			<action id="synchronizeAll" name="一键同步" iconCls="copy" />
			 -->
		<!--</module>
		<module id="R05_01" name="体检项目匹配表单" type="1"
			script="chis.application.wl.script.HssXmMatchForm">
			<properties>
				<p name="entryName">chis.application.wl.schemas.View_TjxmMatch</p>
			</properties>
			<action id="match" name="确定" iconCls="confirm" />
		</module>-->
	</catagory>
</application>