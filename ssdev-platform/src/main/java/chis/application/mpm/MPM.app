<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.mpm.MPM" name="指标数据维护"  type="1"> 
	<catagory id="MPM" name="模版维护管理"> 
		<module id="MPM1" name="数据结构维护" script="chis.application.mpm.script.FieldMaintainList"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p>  
				<p name="refModule">chis.application.mpm.MPM/MPM/MPM1_1</p> 
			</properties>  
			<action id="createInfo" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>  
			<action id="print" name="打印"/> 
		</module>  
		<module id="MPM1_1" name="数值值域维护模块" type="1" script="chis.application.mpm.script.FieldDicMaintainModule"> 
			<action id="fieldForm" name="数据结构维护表单" ref="chis.application.mpm.MPM/MPM/MPM1_2"/>  
			<action id="dicList" name="数值值域维护列表" ref="chis.application.mpm.MPM/MPM/MPM1_3"/> 
		</module>  
		<module id="MPM1_2" name="数据结构维护表单" script="chis.application.mpm.script.FieldMaintainForm" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p> 
			</properties>  
			<action id="save" name="保存"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="MPM1_3" name="数值值域维护列表" script="chis.application.mpm.script.DictionaryMaintainList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_DictionaryMaintain</p>  
				<p name="refModule">chis.application.mpm.MPM/MPM/MPM1_4</p> 
			</properties>  
			<action id="createInfo" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>  
			<action id="print" name="打印"/> 
		</module>  
		<module id="MPM1_4" name="数值值域维护表单" script="chis.application.mpm.script.DictionaryMaintainForm" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_DictionaryMaintain</p> 
			</properties>  
			<action id="save" name="保存"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="MPM2" name="模版维护" script="chis.application.mpm.script.MasterplateMaintainList"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_MasterplateMaintain</p>  
				<p name="refModule">chis.application.mpm.MPM/MPM/MPM2_1</p> 
			</properties>  
			<action id="createInfo" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>  
			<action id="print" name="打印"/> 
		</module>  
		<module id="MPM2_1" name="模版维护模块" type="1" script="chis.application.mpm.script.MasterplateMaintainModule"> 
			<action id="masterForm" name="模版维护表单" ref="chis.application.mpm.MPM/MPM/MPM2_2"/>  
			<action id="fieldList" name="数据结构维护列表" ref="chis.application.mpm.MPM/MPM/MPM2_3"/> 
		</module>  
		<module id="MPM2_2" name="模版维护表单" script="chis.application.mpm.script.MasterplateMaintainForm" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_MasterplateMaintain</p> 
			</properties>  
			<action id="save" name="保存"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="MPM2_3" name="数据结构维护列表" script="chis.application.mpm.script.FieldMaintainMPList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p>  
				<p name="addModule">chis.application.mpm.MPM/MPM/MPM2_5</p>  
				<p name="refModule">chis.application.mpm.MPM/MPM/MPM2_4</p>  
				<p name="removeModule">chis.application.mpm.MPM/MPM/MPM2_8</p> 
			</properties>  
			<action id="addInfo" name="新增" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>  
			<action id="batchRemove" name="批量删除" iconCls="remove"/>  
			<action id="print" name="打印"/> 
		</module>  
		<module id="MPM2_4" name="数据结构维护表单" script="chis.application.mpm.script.FieldMaintainForm" type="1"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p> 
			</properties>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="MPM2_5" name="新增数据结构信息" type="1" script="chis.application.mpm.script.FieldMaintainAddModule"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p> 
			</properties>  
			<action id="FieldMaintainForm" name="数据结构表单" ref="chis.application.mpm.MPM/MPM/MPM2_6"/>  
			<action id="FieldMaintainList" name="数据结构列表" ref="chis.application.mpm.MPM/MPM/MPM2_7"/> 
		</module>  
		<module id="MPM2_6" name="数据结构表单" type="1" script="chis.application.mpm.script.FieldMaintainAddForm"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintainQuery</p> 
			</properties>  
			<action id="select" name="查询" iconCls="common_query"/>  
			<action id="reset" name="重置" iconCls="common_reset"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="MPM2_7" name="数据结构列表" type="1" script="chis.application.mpm.script.FieldMaintainAddList"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>  
		<module id="MPM2_8" name="批量删除数据结构" type="1" script="chis.application.mpm.script.FieldMaintainRemoveList"> 
			<properties> 
				<p name="entryName">chis.application.mpm.schemas.MPM_FieldMaintain</p> 
			</properties>  
			<action id="remove" name="删除"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module> 
		<module id="MPM3" name="指标数据维护"
			script="chis.application.div.script.ConfirmIndexDataList">
			<properties>
				<p name="entryName">chis.application.mpm.schemas.MPM_MasterplateData</p> 
				<p name="createCls">chis.application.div.script.ConfirmIndexDataForm</p>  
				<p name="updateCls">chis.application.div.script.ConfirmIndexDataForm</p>  
			</properties>
			<action id="create" name="新建"/>  
			<action id="update" name="修改"/>  
			<action id="remove" name="删除"/> 
		</module>
	</catagory>
</application>  