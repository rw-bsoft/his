<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.per.PER" name="体检管理"  type="1">
	<catagory id="PER" name="体检管理">
		<module id="J01" name="体检管理" script="chis.application.per.script.PerCombinedDocList"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupRegister</p>  
					<p name="manageUnitField">checkupOrganization</p> 
				</properties>  
				<action id="list" name="列表视图" viewType="list" ref="chis.application.per.PER/PER/J0101"/> 
			</module>  
			<module id="J0101" name="体检档案列表" type="1" script="chis.application.per.script.checkup.CheckupRecordListView"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupRegister</p>  
					<p name="saveServiceId">chis.checkupRecordService</p>  
					<p name="serviceAction">logoutCheckupRecord</p>  
				</properties>  
				<action id="createByEmpi" name="新建" iconCls="create" group="create"/>  
				<action id="modify" name="查看" iconCls="update" group="update"/>  
				<action id="logOut" name="作废" iconCls="common_writeOff" group="update"/>  
				<action id="print" name="打印"/> 
			</module>  
			<module id="J010101" name="体检档案整体" type="1" script="chis.application.per.script.checkup.CheckupRecordModule"> 
				<properties> 
					<p name="saveServiceId">chis.perService</p>  
					<p name="serviceAction">savePerRecord</p> 
					<p name="isAutoScroll">true</p>
				</properties>  
				<action id="checkupRecordList" name="体检档案列表" ref="chis.application.per.PER/PER/J0102"/>  
				<action id="checkupRecord" name="体检记录" ref="chis.application.per.PER/PER/J01010101" type="tab"/>  
				<action id="checkupDetail" name="体检明细" ref="chis.application.per.PER/PER/J01010102" type="tab"/> 
			</module>  
			<module id="J0102" name="体检档案列表" type="1" script="chis.application.per.script.checkup.CheckupRecordList"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupRegisterModule</p> 
				</properties> 
			</module>  
			<module id="J01010101" name="体检登记整体" type="1" script="chis.application.per.script.checkup.CheckupRegisterModule"> 
				<properties> 
					<p name="checkupSchema">chis.application.per.schemas.PER_CheckupRegister</p>  
					<p name="icdSchema">chis.application.per.schemas.PER_ICD</p>  
					<p name="saveServiceId">chis.checkupRecordService</p>  
					<p name="serviceAction">saveCheckupRecord</p> 
					<p name="isAutoScroll">true</p>
				</properties>  
				<action id="registerForm" name="体检登记表单" ref="chis.application.per.PER/PER/J01010101_1"/>  
				<action id="registerList" name="体检登记ICD列表" ref="chis.application.per.PER/PER/J01010101_2"/> 
			</module>  
			<module id="J01010101_1" name="体检登记表单" type="1" script="chis.application.per.script.checkup.CheckupRegisterForm"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupRegister</p>  
					<p name="saveServiceId">chis.checkupRecordService</p>  
					<p name="refRevokeModule">chis.application.per.PER/PER/J01010101_1_1</p> 
					<p name="isAutoScroll">true</p>
				</properties>  
				<action id="save" name="确定" group="create||update"/>  
				<action id="finalCheck" name="总检" iconCls="per_finalCheck" group="finalCheck"/>  
				<action id="revoke" name="撤销" iconCls="per_revoke" group="update"/>  
				<action id="create" name="新建" group="create"/>  
				<action id="printCheckup" name="打印" iconCls="print" group="print"/> 
			</module>  
			<module id="J01010101_1_1" name="体检总检撤销表单" type="1" script="chis.application.per.script.checkup.CheckupRevokeForm"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_FinalCheckRevoke</p>  
					<p name="saveServiceId">chis.checkupRecordService</p>  
					<p name="saveAction">checkRevoke</p> 
					<p name="isAutoScroll">true</p>
				</properties>  
				<action id="save" name="确定"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="J01010101_2" name="体检登记ICD列表" type="1" script="chis.application.per.script.checkup.CheckupRegisterList"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_ICD</p>  
					<p name="createCls">chis.application.per.script.checkup.CheckupICDForm</p>  
					<p name="updateCls">chis.application.per.script.checkup.CheckupICDForm</p>
				</properties>  
				<action id="create" name="增加" group="create"/>  
				<action id="read" name="查看" group="update"/>  
				<action id="remove" name="删除" group="update"/> 
			</module>  
			<module id="J01010102" name="体检明细整体" type="1" script="chis.application.per.script.checkup.CheckupDetailModule"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupDetail</p> 
				</properties>  
				<action id="checkupSummaryForm" name="体检小结表单" ref="chis.application.per.PER/PER/J01010102_1"/>  
				<action id="checkupDetailEditList" name="体检项目信息录入" ref="chis.application.per.PER/PER/J01010102_2"/> 
			</module>  
			<module id="J01010102_1" name="体检小结表单" type="1" script="chis.application.per.script.checkup.CheckupSummaryForm"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupSummary</p> 
					<p name="isAutoScroll">true</p>
				</properties>  
				<action id="save" name="确定" group="update"/> 
			</module>  
			<module id="J01010102_2" name="体检项目信息录入" type="1" script="chis.application.per.script.checkup.CheckupDictList"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_CheckupDetail</p> 
				</properties> 
			</module>  
			<module id="J08" name="体检项目字典维护" script="chis.application.per.script.dictionary.CheckupDictionaryList"> 
				<properties> 
					<p name="createCls">chis.application.per.script.dictionary.CheckupDictionaryForm</p>  
					<p name="updateCls">chis.application.per.script.dictionary.CheckupDictionaryForm</p>  
					<p name="entryName">chis.application.per.schemas.PER_CheckupDict</p>  
					<p name="navDic">chis.dictionary.perCheckDicType</p>  
					<p name="navField">projectType</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="修改"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="J09" name="体检套餐维护" script="chis.application.per.script.setmeal.CheckupSetMealList"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_Combo</p>  
					<p name="showRowNumber">true</p>  
					<p name="setmealRefId">chis.application.per.PER/PER/J09-1</p> 
				</properties>  
				<action id="createSetMeal" name="新建" iconCls="update"/>  
				<action id="modify" name="修改" iconCls="update"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="J09-1" name="体检套餐维护表单" type="1" script="chis.application.per.script.setmeal.CheckupSetMealModule"> 
				<action id="setMealForm" name="体检套餐表单" ref="chis.application.per.PER/PER/J09-1-1"/>  
				<action id="setMealDetailList" name="体检套餐明细列表" ref="chis.application.per.PER/PER/J09-1-2"/> 
			</module>  
			<module id="J09-1-1" name="体检套餐表单" type="1" script="chis.application.per.script.setmeal.CheckupSetMealForm"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_Combo</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="save" name="保存"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="J09-1-2" name="体检套餐明细列表" type="1" script="chis.application.per.script.setmeal.CheckupSetMealDetailList"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_ComboDetail</p>  
					<p name="setmealDetailRefId">chis.application.per.PER/PER/J09-1-2-1</p> 
				</properties>  
				<action id="add" name="新建"/>  
				<action id="modify" name="修改" iconCls="update"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="J09-1-2-1" name="体检套餐明细表单" type="1" script="chis.application.per.script.setmeal.CheckupSetMealDetailForm"> 
				<properties> 
					<p name="entryName">chis.application.per.schemas.PER_ComboDetail</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="save" name="保存"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module>  
			<module id="J10" name="科室维护" script="chis.application.per.script.office.CheckupProjectOfficeList"> 
				<properties> 
					<p name="createCls">chis.application.per.script.office.CheckupProjectOfficeForm</p>  
					<p name="updateCls">chis.application.per.script.office.CheckupProjectOfficeForm</p>  
					<p name="entryName">chis.application.per.schemas.PER_ProjectOffice</p>  
					<p name="removeServiceId">chis.checkupProjectOfficeService</p> 
				</properties>  
				<action id="create" name="新建"/>  
				<action id="update" name="修改"/>  
				<action id="remove" name="删除"/> 
			</module> 
	</catagory>
</application>