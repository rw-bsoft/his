<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hc.HC" name="健康检查"  type="1">
	<catagory id="HC" name="健康检查">
		<module id="HC01" name="健康检查管理" type="1" script="chis.application.hc.script.HealthCheckModule"> 
			<properties> 
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="loadAction">loadCreateControl</p>
				<p name="isAutoScroll">true</p> 
			</properties>
			<action id="HealthCheckList" name="健康检查列表" ref="chis.application.hc.HC/HC/D20_2_1"/>  
			<action id="HealthCheckForm" name="健康检查" ref="chis.application.hc.HC/HC/HC0101" type="tab"/>
			<!--<action id="InhospitalMedicine" name="住院治疗用药情况" ref="chis.application.hc.HC/HC/D20_2_6" type="tab"/>  
			 -->
			 <!-- 
			<action id="NonimmuneInoculation" name="非免疫规划预防接种" ref="chis.application.hc.HC/HC/D20_2_8" type="tab"/>  
		     -->
		</module>
		<!--健康检查纸质页面 -->
		<module id="HC0101" name="健康检查" script="chis.application.hc.script.HealthCheckHtmlForm" type="1">	
			<properties>  
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="loadAction">getHMNIListOfHTML</p> 
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveHealthCheckHtml</p> 
			</properties>
			<action id="save" name="确定" group="update"/>  
			<action id="create" name="新建" group="create"/>  
			<action id="printCheck" name="打印" iconCls="print"/> 
		</module>
		<module id="HC20" name="待体检列表" script="chis.script.CombinedDocList"> 
			<properties>
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hc.HC/HC/HC20_1"/> 
		</module>
		<module id="HC20_1" name="待健康体检列表" script="chis.application.hc.script.HealthNeedCheckRecord" type="1"> 
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>  
				<p name="navField">b.manaUnitId</p>
				<p name="navDic">b.manageUnit</p>
				<p name="removeServiceId">chis.healthCheckService</p>
				<p name="removeAction">deleteHealthCheckRecord</p>
				<p name="listServiceId">chis.healthCheckService</p>
				<p name="listAction">ListNeedCheckRecord</p>
			</properties>
			<action id="modify" name="体检" iconCls="update"/>  
			<action id="print" name="打印"/> 
		</module>
		<module id="D20" name="健康检查表" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>  
				<p name="manageUnitField">a.manaUnitId</p>  
				<p name="areaGridField">c.regionCode</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="navField">manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hc.HC/HC/D20_1"/> 
		</module>  
		<module id="D20_1" name="健康检查列表" script="chis.application.hc.script.HealthCheckRecord" type="1"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>  
				<p name="navField">b.manaUnitId</p>  
				<p name="navDic">b.manageUnit</p>  
				<p name="removeServiceId">chis.healthCheckService</p>  
				<p name="removeAction">deleteHealthCheckRecord</p> 
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
			<action id="repeat" name="查重" iconCls="copy"/>
			<action id="print" name="打印"/> 
		</module>  
		<module id="D20_2" name="健康检查管理" type="1" script="chis.application.hc.script.HealthCheckModule"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>  
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="loadAction">loadCreateControl</p>
				<p name="isAutoScroll">true</p> 
			</properties>  
			<action id="HealthCheckList" name="健康检查列表" ref="chis.application.hc.HC/HC/D20_2_1"/>  
			<action id="HealthCheckForm" name="基本信息" ref="chis.application.hc.HC/HC/D20_2_2" type="tab"/>  
			<action id="LifestySituation" name="生活方式" ref="chis.application.hc.HC/HC/D20_2_3" type="tab"/>  
			<action id="Examination" name="查体" ref="chis.application.hc.HC/HC/D20_2_4" type="tab"/>  
			<action id="AccessoryExamination" name="辅助检查" ref="chis.application.hc.HC/HC/D20_2_7" type="tab"/> 
			<action id="InhospitalMedicine" name="住院治疗用药情况" ref="chis.application.hc.HC/HC/D20_2_6" type="tab"/>  
			<action id="NonimmuneInoculation" name="非免疫规划预防接种" ref="chis.application.hc.HC/HC/D20_2_8" type="tab"/>  
			<action id="HealthAssessment" name="健康评价表" ref="chis.application.hc.HC/HC/D20_2_5" type="tab"/> 
		</module>  
		<module id="D20_2_1" name="健康检查列表" type="1" script="chis.application.hc.script.HealthCheckList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck_list</p> 
			</properties> 
		</module>  
		<module id="D20_2_2" type="1" name="健康检查表单" script="chis.application.hc.script.HealthCheckMemuForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>  
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveAnnualHealthCheck</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="create" name="新建" group="create"/>  
			<action id="printCheck" name="打印" iconCls="print"/> 
		</module>  
		<module id="D20_2_3" type="1" name="生活方式" script="chis.application.hc.script.lifestySituationForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_LifestySituation</p>  
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="isAutoScroll">true</p>
				<p name="loadAction">loadLifestySituationData</p> 
			</properties>  
			<action id="save" name="确定" iconCls="save" group="update"/> 
		</module>  
		<module id="D20_2_4" type="1" name="查体" script="chis.application.hc.script.HealthExaminationForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_Examination</p>  
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="isAutoScroll">true</p>
				<p name="loadAction">loadExaminationData</p> 
			</properties>  
			<action id="save" name="确定" iconCls="save" group="update"/> 
		</module>  
		<module id="D20_2_5" type="1" name="健康评价表" script="chis.application.hc.script.HealthAssessmentForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthAssessment</p>  
				<p name="loadServiceId">chis.healthCheckService</p>
				<p name="isAutoScroll">true</p>  
				<p name="loadAction">loadHealthAssessmentData</p> 
			</properties>  
			<action id="save" name="确定" iconCls="save" group="update"/> 
		</module>  
		<module id="D20_2_6" type="1" name="住院治疗用药情况" script="chis.application.hc.script.lifestySituationTabModule" icon="default"> 
			<properties> 
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="loadAction">loadInhospitalAndMedicineData</p> 
			</properties>  
			<action id="action1" name="住院治疗情况" ref="chis.application.hc.HC/HC/D20_2_6_1"/>  
			<action id="action2" name="用药情况" ref="chis.application.hc.HC/HC/D20_2_6_2"/> 
		</module>  
		<module id="D20_2_6_1" type="1" name="住院治疗情况" script="chis.application.hc.script.InhospitalSituationList" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_InhospitalSituation</p> 
			</properties>  
			<action id="create" name="新建" ref="chis.application.hc.HC/HC/D20_2_6_1_1" group="update"/>  
			<action id="update" name="查看" iconCls="update" ref="chis.application.hc.HC/HC/D20_2_6_1_1"/>  
			<action id="remove" name="删除" group="update"/> 
		</module>  
		<module id="D20_2_6_1_1" type="1" name="住院治疗情况表单" script="chis.application.hc.script.InhospitalSituationForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_InhospitalSituation</p>  
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveInhospitalSituation</p> 
			</properties>  
			<action id="create" name="新建" group="update"/>  
			<action id="save" name="保存" group="update"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="D20_2_6_2" type="1" name="用药情况" script="chis.application.hc.script.MedicineSituationList" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_MedicineSituation</p> 
			</properties>  
			<action id="create" name="新建" ref="chis.application.hc.HC/HC/D20_2_6_2_1" group="update"/>  
			<action id="update" name="查看" iconCls="update" ref="chis.application.hc.HC/HC/D20_2_6_2_1"/>  
			<action id="remove" name="删除" group="update"/> 
		</module>  
		<module id="D20_2_6_2_1" type="1" name="用药情况表单" script="chis.application.hc.script.MedicineSituationForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_MedicineSituation</p>  
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveMedicineSituation</p> 
			</properties>  
			<action id="create" name="新建" group="update"/>  
			<action id="save" name="保存" group="update"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
		<module id="D20_2_7" type="1" name="辅助检查" script="chis.application.hc.script.AccessoryExaminationForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_AccessoryExamination</p> 
				<p name="isAutoScroll">true</p>   
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="loadAction">loadAccessoryExaminationData</p> 
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveAccessoryExaminationData</p> 
			</properties>  
			<action id="save" name="确定" iconCls="save" group="update"/> 
		</module>  
		<module id="D20_2_8" type="1" name="非免疫规划预防接种" script="chis.application.hc.script.NonimmuneInoculationList" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_NonimmuneInoculation</p> 
			</properties>  
			<action id="create" name="新建" ref="chis.application.hc.HC/HC/D20_2_8_1" group="update"/>  
			<action id="update" name="查看" iconCls="update" ref="chis.application.hc.HC/HC/D20_2_8_1"/>  
			<action id="remove" name="删除" group="update"/> 
		</module>  
		<module id="D20_2_8_1" type="1" name="非免疫规划预防接种表单" script="chis.application.hc.script.NonimmuneInoculationForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_NonimmuneInoculation</p>  
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveNonimmuneInoculation</p> 
			</properties>  
			<action id="create" name="新建" group="update"/>  
			<action id="save" name="保存" group="update"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>  
	</catagory>
	<catagory id="JKXW" name="健康小屋">
	    <!--修改重大数据库获取信息
		<module id="JKXW_01" name="自测信息" script="chis.script.CombinedDocList"> 
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hc.HC/JKXW/JKXW_0101"/> 
		</module>
		<module id="JKXW_0101" name="自测信息" script="chis.application.hc.script.JkxwRecordList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.VIEW_ZJ_RECORD</p>  
			</properties>
			<action id="viewqxt" name="查看我的曲线图" iconCls="look"/>
		</module>
		-->
		<module id="JKXW_01" name="自测信息" script="chis.application.healthhome.script.healthHomeModule">
        </module>
        <module id="JKXW_0101" name="自测信息" script="chis.application.hc.script.JkxwRecordList" type="1">
            <properties>
                <p name="entryName">chis.application.hc.schemas.VIEW_ZJ_RECORD</p>
        	</properties>
        		<action id="viewqxt" name="查看我的曲线图" iconCls="look"/>
        </module>
	</catagory>
	
	<catagory id="PC" name="孕前检查">
		<module id="PC_01" name="妻子孕前检查表" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_w_progestationask</p>  
				<p name="manageUnitField">a.manaUnitId</p>  
				<p name="areaGridField">c.regionCode</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="navField">manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hc.HC/PC/PC_01_01"/> 
		</module>  
		<module id="PC_01_01" name="妻子孕前检查列表" script="chis.application.hc.script.WProgestationaskRecord" type="1"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_w_progestationask</p>  
				<p name="navField">b.manaUnitId</p>  
				<p name="navDic">b.manageUnit</p>  
				<p name="removeServiceId">chis.healthCheckService</p>  
				<p name="removeAction">deleteHealthCheckRecord</p> 
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/> 
		</module>
		<module id="PC_02" name="妻子孕前检查组合模块配置文件" icon="default" type="1"
			script="chis.script.EHRView">
			<action id="WProgestationAskForm" name="妻子孕前检查询问" ref="chis.application.hc.HC/PC/PC_02_02" />
			<action id="WProgestationCheck" name="妻子孕前检查信息" ref="chis.application.hc.HC/PC/PC_04" />
		</module>
		<module id="PC_02_02" name="妻子健康检查询问" script="chis.application.hc.script.WProgestationaskForm" type="1">	
			<properties>  
				<p name="entryName">chis.application.hc.schemas.hc_w_progestationask</p>
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveWProgestationaskrecord</p> 
			</properties>
			<action id="save" name="确定" group="update"/>  
			<action id="print" name="打印" iconCls="print"/> 
		</module>
		<module id="PC_04" name="妻子孕前检查管理" type="1" script="chis.application.hc.script.WProgestationCheckModule"> 
			<properties> 
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="isAutoScroll">true</p> 
			</properties>
			<action id="WProgestationCheckList" name="妻子孕前检查列表" ref="chis.application.hc.HC/PC/PC_04_01"/> 
			<action id="WProgestationCheckForm" name="妻子孕前检查项目" ref="chis.application.hc.HC/PC/PC_04_02" type="tab"/>
		</module>
		<module id="PC_04_01" name="妻子健康检查列表" type="1" script="chis.application.hc.script.WProgestationCheckList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_w_progestationcheck_list</p> 
			</properties> 
		</module>
		<module id="PC_04_02" type="1" name="妻子健康检查表单" script="chis.application.hc.script.WProgestationCheckForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_w_progestationcheck</p>  
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveWProgestationaskrecord</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="create" name="新建" group="create"/>  
			<action id="printCheck" name="打印" iconCls="print"/> 
		</module>
		
		<module id="PC_05" name="丈夫孕前检查表" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_m_progestationask</p>  
				<p name="manageUnitField">a.manaUnitId</p>  
				<p name="areaGridField">c.regionCode</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="navField">manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hc.HC/PC/PC_05_01"/> 
		</module>  
		<module id="PC_05_01" name="丈夫孕前检查列表" script="chis.application.hc.script.MProgestationaskRecord" type="1"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_m_progestationask</p>  
				<p name="navField">b.manaUnitId</p>  
				<p name="navDic">b.manageUnit</p>  
				<p name="removeServiceId">chis.healthCheckService</p>  
				<p name="removeAction">deleteHealthCheckRecord</p> 
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/> 
		</module>
		<module id="PC_06" name="丈夫孕前检查组合模块配置文件" icon="default" type="1"
			script="chis.script.EHRView">
			<action id="MProgestationAskForm" name="丈夫孕前检查询问" ref="chis.application.hc.HC/PC/PC_06_02" />
			<action id="MProgestationCheck" name="丈夫孕前检查信息" ref="chis.application.hc.HC/PC/PC_07" />
		</module>
		<module id="PC_06_02" name="丈夫健康检查询问" script="chis.application.hc.script.MProgestationaskForm" type="1">	
			<properties>  
				<p name="entryName">chis.application.hc.schemas.hc_m_progestationask</p>
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveMProgestationaskrecord</p> 
			</properties>
			<action id="save" name="确定" group="update"/>  
			<action id="print" name="打印" iconCls="print"/> 
		</module>
		<module id="PC_07" name="妻子孕前检查管理" type="1" script="chis.application.hc.script.MProgestationCheckModule"> 
			<properties> 
				<p name="loadServiceId">chis.healthCheckService</p>  
				<p name="isAutoScroll">true</p> 
			</properties>
			<action id="WProgestationCheckList" name="妻子孕前检查列表" ref="chis.application.hc.HC/PC/PC_07_01"/> 
			<action id="WProgestationCheckForm" name="妻子孕前检查项目" ref="chis.application.hc.HC/PC/PC_07_02" type="tab"/>
		</module>
		<module id="PC_07_01" name="妻子健康检查列表" type="1" script="chis.application.hc.script.MProgestationCheckList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_m_progestationcheck_list</p> 
			</properties> 
		</module>
		<module id="PC_07_02" type="1" name="妻子健康检查表单" script="chis.application.hc.script.MProgestationCheckForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.hc_m_progestationcheck</p>  
				<p name="saveServiceId">chis.healthCheckService</p>  
				<p name="saveAction">saveMProgestationaskrecord</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="update"/>  
			<action id="create" name="新建" group="create"/>  
			<action id="printCheck" name="打印" iconCls="print"/> 
		</module> 
	</catagory>
	<catagory id="HC-QJ" name="健康检查表(区级医院)">
	<module id="D201" name="健康检查表(区级医院)" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>  
				<p name="manageUnitField">a.manaUnitId</p>  
				<p name="areaGridField">c.regionCode</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="navField">manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hc.HC/HC-QJ/D20_11"/> 
		</module>  
		<module id="D20_11" name="健康检查列表" script="chis.application.hcqj.script.HealthCheckRecord" type="1"> 
			<properties> 
				<p name="entryName">chis.application.hc.schemas.HC_HealthCheck</p>  
				<p name="navField">b.manaUnitId</p>  
				<p name="navDic">b.manageUnit</p>  
				<p name="removeServiceId">chis.healthCheckService</p>  
				<p name="removeAction">deleteHealthCheckRecord</p> 
			</properties>
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>    
	</catagory>
</application>