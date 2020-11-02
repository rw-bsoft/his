<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.def.DEF" name="残疾人档案"  type="1">
	<catagory id="DEF" name="残疾人康复训练"> 
			<module id="DEF01" name="肢体残疾登记" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_IntellectDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF01_1"/> 
			</module>  
			<module id="DEFEvaluateWorkList01" name="肢体残疾训练评估待执行任务" script="chis.script.CombinedDocList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_LimbDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF01_1"/> 
			</module>  
			<module id="DEFTrainingWorkList01" name="肢体残疾训练记录待执行任务" script="chis.script.CombinedDocList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_LimbDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF01_1"/> 
			</module>  
			<module id="DEF01_1" name="肢体残疾人管理列表" script="chis.application.def.script.limb.LimbDeformityRecordListView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_LimbDeformityRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>  
				</properties>  
				<action id="createByEmpi" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="remove" name="注销"/>  
				<action id="see" name="查看末期总结" iconCls="read"/> 
			</module>  
			<module id="DEF01_1_1" name="肢体残疾模块" script="chis.application.def.script.limb.LimbDefModule" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_LimbDeformityRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>  
				</properties> 
			</module>  
			<module id="DEF01_1_1_1" name="肢体残疾form" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="create"/>  
				<action id="close" name="结案" group="update"/> 
			</module>  
			<module id="DEF01_1_1_2" name="肢体残疾训练评估" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="update"/> 
			</module>  
			<module id="DEF01_1_1_3" name="肢体残疾中期总结" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/> 
			</module>  
			<module id="DEF01_1_1_4" name="肢体残疾训练计划" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="update"/> 
			</module>  
			<module id="DEF01_1_1_5" name="肢体残疾训练记录列表" type="1"> 
				<action id="add" name="增加" group="update"/>  
				<action id="modify" name="修改" iconCls="update" group="update"/>  
				<action id="remove" name="删除" group="update"/> 
			</module>  
			<module id="DEF01_1_1_6" name="肢体残疾训练记录表单" type="1"> 
				<action id="save" name="确定" group="update"/>  
				<action id="cancel" name="取消" iconCls="common_cancel" group="update"/> 
			</module>  
			<module id="DEF01_1_1_7" name="肢体残疾注销" type="1"> 
				<action id="save" name="确定"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="DEF01_1_1_8" name="肢体残疾注销" type="1"> 
				<action id="save" name="确定" />  
				<action id="cancel" name="取消" iconCls="common_cancel" /> 
			</module>
			<module id="DEF02" name="脑瘫残疾登记" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_BrainDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF02_1"/> 
			</module>  
			<module id="DEFEvaluateWorkList02" name="脑瘫残疾训练评估待执行任务" script="chis.script.CombinedDocList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_BrainDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF02_1"/> 
			</module>  
			<module id="DEFTrainingWorkList02" name="脑瘫残疾训练记录待执行任务" script="chis.script.CombinedDocList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_BrainDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF02_1"/> 
			</module>  
			<module id="DEF02_1" name="脑瘫残疾人管理列表" script="chis.application.def.script.brain.BrainDeformityRecordListView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_BrainDeformityRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>  
				</properties>  
				<action id="createByEmpi" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="remove" name="注销"/>  
				<action id="see" name="查看末期总结" iconCls="read"/> 
			</module>  
			<module id="DEF02_1_1" name="脑瘫残疾模块" script="chis.application.def.script.brain.BrainDefModule" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_BrainDeformityRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="isAutoScroll">true</p>
				</properties> 
			</module>  
			<module id="DEF02_1_1_1" name="脑瘫残疾form" type="1">
				<properties>
					<p name="isAutoScroll">true</p>
				</properties> 
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="create"/>  
				<action id="close" name="结案" group="update"/> 
			</module>  
			<module id="DEF02_1_1_2" name="脑瘫残疾训练评估" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties> 
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="update"/> 
			</module>  
			<module id="DEF02_1_1_3" name="脑瘫残疾中期总结" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties> 
				<action id="save" name="确定" group="update"/> 
			</module>  
			<module id="DEF02_1_1_4" name="脑瘫残疾训练计划" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties> 
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="update"/> 
			</module>  
			<module id="DEF02_1_1_5" name="脑瘫残疾训练记录列表" type="1"> 
				<action id="add" name="增加" group="update"/>  
				<action id="modify" name="修改" iconCls="update" group="update"/>  
				<action id="remove" name="删除" group="update"/> 
			</module>  
			<module id="DEF02_1_1_6" name="脑瘫残疾训练记录表单" type="1"> 
				<action id="save" name="确定" group="update"/>  
				<action id="cancel" name="取消" iconCls="common_cancel" group="update"/> 
			</module>  
			<module id="DEF02_1_1_7" name="脑瘫残疾注销" type="1"> 
				<action id="save" name="确定"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="DEF02_1_1_8" name="脑瘫残疾注销" type="1"> 
				<action id="save" name="确定"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="DEF03" name="智力残疾登记" script="chis.script.CombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_IntellectDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF03_1"/> 
			</module>  
			<module id="DEFEvaluateWorkList03" name="智力残疾训练评估待执行任务" script="chis.script.CombinedDocList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_IntellectDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF03_1"/> 
			</module>  
			<module id="DEFTrainingWorkList03" name="智力残疾训练记录待执行任务" script="chis.script.CombinedDocList" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_IntellectDeformityRecord</p>  
					<p name="manageUnitField">c.manaUnitId</p>  
					<p name="areaGridField">c.regionCode</p>  
					<p name="navDic">chis.@manageUnit</p>  
					<p name="navField">manaUnitId</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.def.DEF/DEF/DEF03_1"/> 
			</module>  
			<module id="DEF03_1" name="智力残疾人管理列表" script="chis.application.def.script.intellect.IntellectDeformityRecordListView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_IntellectDeformityRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>  
				</properties>  
				<action id="createByEmpi" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="remove" name="注销"/>  
				<action id="see" name="查看末期总结" iconCls="read"/> 
			</module>  
			<module id="DEF03_1_1" name="智力残疾模块" script="chis.application.def.script.intellect.IntellectDefModule" type="1"> 
				<properties> 
					<p name="entryName">chis.application.def.schemas.DEF_IntellectDeformityRecord</p>  
					<p name="navField">manaUnitId</p>  
					<p name="navDic">chis.@manageUnit</p>
					<p name="isAutoScroll">true</p>
				</properties> 
			</module>  
			<module id="DEF03_1_1_1" name="智力残疾form" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="create"/>  
				<action id="close" name="结案" group="update"/> 
			</module>  
			<module id="DEF03_1_1_2" name="智力残疾训练评估" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="update"/>  
			</module>  
			<module id="DEF03_1_1_3" name="智力残疾中期总结" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/> 
			</module>  
			<module id="DEF03_1_1_4" name="智力残疾训练计划" type="1"> 
				<properties>
					<p name="isAutoScroll">true</p>
				</properties>
				<action id="save" name="确定" group="update"/>  
				<action id="create" name="新建" group="update"/> 
			</module>  
			<module id="DEF03_1_1_5" name="智力残疾训练记录列表" type="1"> 
				<action id="add" name="增加" group="update"/>  
				<action id="modify" name="修改" iconCls="update" group="update"/>  
				<action id="remove" name="删除" group="update"/> 
			</module>  
			<module id="DEF03_1_1_6" name="智力残疾训练记录表单" type="1"> 
				<action id="save" name="确定" group="update"/>  
				<action id="cancel" name="取消" iconCls="common_cancel" group="update"/> 
			</module>  
			<module id="DEF03_1_1_7" name="智力残疾注销" type="1"> 
				<action id="save" name="确定"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="DEF03_1_1_8" name="智力残疾注销" type="1"> 
				<action id="save" name="确定"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module> 
		</catagory> 
</application>