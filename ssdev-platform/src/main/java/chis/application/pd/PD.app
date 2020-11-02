<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.pd.PD" name="指标数据维护"  type="1">
	<catagory id="PD" name="指标数据维护">
			<module id="S04" name="指标数据维护" script="chis.application.pd.script.DBDataSetMaintenanceListView"> 
				<properties> 
					<p name="dicId">chis.dictionary.dataListNav</p>  
				</properties> 
			</module>  
			<module id="S04-1" name="9市标准-年龄别身高、体重、头围列表" script="chis.application.pd.script.city.CityAgeList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_9CityAge</p>  
					<p name="createCls">chis.application.pd.script.city.CityAgeForm</p>  
					<p name="updateCls">chis.application.pd.script.city.CityAgeForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="S04-2"  name="9市标准-身高别体重列表" script="chis.application.pd.script.city.CityHeightList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_9CityHeight</p>  
					<p name="createCls">chis.application.pd.script.city.CityHeightForm</p>  
					<p name="updateCls">chis.application.pd.script.city.CityHeightForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="S04-3"  name="WHO标准-年龄别身高、年龄别体重列表" script="chis.application.pd.script.who.WHOAgeList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_WHOAge</p>  
					<p name="createCls">chis.application.pd.script.who.WHOAgeForm</p>  
					<p name="updateCls">chis.application.pd.script.who.WHOAgeForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="S04-4" name="WHO标准-身高别体重列表" script="chis.application.pd.script.who.WHOHeightList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_WHOHeight</p>  
					<p name="createCls">chis.application.pd.script.who.WHOHeightForm</p>  
					<p name="updateCls">chis.application.pd.script.who.WHOHeightForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="S04-5" name="WHO标准-BMI指标列表" script="chis.application.pd.script.who.WHOBMIList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_WHOBMI</p>  
					<p name="createCls">chis.application.pd.script.who.WHOBMIForm</p>  
					<p name="updateCls">chis.application.pd.script.who.WHOBMIForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module> 
			<!--  
			<module id="S04-6"   script="chis.application.pd.script.dbgs.DiabetesGroupStandardListView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.MDC_DiabetesGroupStandard</p>  
					<p name="createCls">chis.application.pd.script.dbgs.DiabetesGroupStandardFormView</p>  
					<p name="updateCls">chis.application.pd.script.dbgs.DiabetesGroupStandardFormView</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			 -->
			<module id="S04-7" name="保健处方列表" script="chis.application.pd.script.cdc.ChildrenDictCorrectionList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_DictCorrection</p>  
					<p name="createCls">chis.application.pd.script.cdc.ChildrenDictCorrectionForm</p>  
					<p name="updateCls">chis.application.pd.script.cdc.ChildrenDictCorrectionForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="S04-8"  name="体弱儿指导意见列表" script="chis.application.pd.script.dccd.DebilityChildrenCorrectionDicList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pd.schemas.CDH_DebilityCorrectionDic</p>  
					<p name="createCls">chis.application.pd.script.dccd.DebilityChildrenCorrectionDicForm</p>  
					<p name="updateCls">chis.application.pd.script.dccd.DebilityChildrenCorrectionDicForm</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="查看"/>  
				<action id="remove" name="删除"/> 
			</module>  
	</catagory>
</application>