<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.med.MED" name="医技管理">
	<catagory id="MED" name="医技管理">
		<module id="MED01" name="医技项目执行" script="phis.application.med.script.MedicalTechnicalSectionsModule">
			<properties>
				<p name="refTable">phis.application.med.MED/MED/MED0101</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="增加" />
			<action id="modify" name="修改" iconCls="update"/>
			<action id="delete" name="删除" iconCls="remove"/>
			<action id="execute" name="执行" iconCls="commit"/>
			<action id="goback" name="退回" iconCls="arrow_undo" />
		</module> 
		<module id="MED0103" name="消耗明细打印" type="1" script="phis.prints.script.SuppliesxhmxPrintView">
		</module>
		<module id="MED0101" name="医技处理tab" type="1"  script="phis.application.med.script.MedicalTechnicalSectionsTab">
			<action id="mzTab" name="门诊业务" ref="phis.application.med.MED/MED/MED010101" type="tab"/>
			<action id="zyTab" name="住院业务" ref="phis.application.med.MED/MED/MED010102" type="tab"/>
			<action id="jcTab" name="家床业务" ref="phis.application.med.MED/MED/MED010105" type="tab"/>
		</module>
		
		<module id="MED010102" name="住院业务" type="1"  script="phis.application.med.script.ZYBusiList">
			<properties>   
				<p name="entryName">phis.application.med.schemas.YJ_ZYYW</p>
			</properties>
		</module>
		<module id="MED010104" name="住院医技项目" type="1"  script="phis.application.med.script.ZYBusiListProj">
			<properties>   
				<p name="entryName">phis.application.med.schemas.YJ_ZYYWMX</p>
			</properties>
		</module>
		<module id="MED01010201" name="住院业务项目维护" type="1"  script="phis.application.med.script.ZYBusiListProjectManage">
			<properties> 
				<p name="ZYListForm">phis.application.med.MED/MED/MED01010202</p>
				<p name="ZYProjectEditorList">phis.application.med.MED/MED/MED01010203</p>
			</properties>	
			<action id="add" name="增加"/>
			<action id="delete" name="删除" iconCls="remove"/>
			<action id="save" name="保存" />
			<action id="clear" name="重置" iconCls="page_refresh"/>
			<action id="close" name="关闭" iconCls="close"/>
		</module>
		
		<module id="MED01010202" name="医技表单" type="1" script="phis.application.med.script.ZYListForm"> 
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZYFORM</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		
		<module id="MED01010203" name="门诊医技项目录入" type="1" script="phis.application.med.script.ZYProjectEditorList">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_MZLIST</p>
				<p name="autoLoadData">0</p>
			</properties>
		</module >
		
		<module id="MED01010204" name="诊断结果选择" type="1" script="phis.application.med.script.DiagnosisMaintainSelect">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZDJG</p>
			</properties>
		</module>
		
		
		<!-- 医技门诊-->
		<module id="MED010101" name="门诊业务" type="1"  script="phis.application.med.script.MZBusiList">
			<properties> 
				<p name="entryName">phis.application.med.schemas.YJ_MZYW</p>
			</properties>
		</module>
		
		<module id="MED010103" name="门诊医技项目" type="1"  script="phis.application.med.script.MZBusiListProj">
			<properties> 
				<p name="entryName">phis.application.med.schemas.YJ_MZYWMX</p>
			</properties>
		</module>
		
		<module id="MED01010101" name="门诊业务项目维护" type="1"  script="phis.application.med.script.MZBusiListProjectManage">
			<properties> 
				<p name="MZListForm">phis.application.med.MED/MED/MED01010102</p>
				<p name="MZProjectEditorList">phis.application.med.MED/MED/MED01010103</p>
			</properties>	
			<action id="add" name="增加"/>
			<action id="delete" name="删除" iconCls="remove"/>
			<action id="save" name="保存" />
			<action id="clear" name="重置" iconCls="page_refresh"/>
			<action id="close" name="关闭" iconCls="close"/>
		</module>
	 
		<module id="MED01010102" name="医技表单" type="1" script="phis.application.med.script.MZListForm"> 
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_MZFORM</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		
		<module id="MED01010103" name="门诊医技项目录入" type="1" script="phis.application.med.script.MZProjectEditorList">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_MZLIST</p>
				<p name="autoLoadData">0</p>
			</properties>
		</module >
		
		<module id="MED01010104" name="诊断结果选择" type="1" script="phis.application.med.script.DiagnosisMaintainSelect">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZDJG</p>
			</properties>
		</module>
		
		<!-- 医技家床-->
		<module id="MED010105" name="家床业务" type="1"  script="phis.application.fsb.script.JCBusiList">
			<properties> 
				<p name="entryName">phis.application.fsb.schemas.JC_YJ01_ZX</p>
			</properties>
		</module>
		
		<module id="MED010106" name="家床医技项目" type="1"  script="phis.application.fsb.script.JCBusiListProj">
			<properties> 
				<p name="entryName">phis.application.fsb.schemas.JC_YJ02_ZX</p>
			</properties>
		</module>
		
		<module id="MED01010501" name="家床业务项目维护" type="1"  script="phis.application.fsb.script.JCBusiListProjectManage">
			<properties> 
				<p name="JCListForm">phis.application.med.MED/MED/MED01010502</p>
				<p name="JCProjectEditorList">phis.application.med.MED/MED/MED01010503</p>
			</properties>	
			<action id="add" name="增加"/>
			<action id="delete" name="删除" iconCls="remove"/>
			<action id="save" name="保存" />
			<action id="clear" name="重置" iconCls="page_refresh"/>
			<action id="close" name="关闭" iconCls="close"/>
		</module>
	 
		<module id="MED01010502" name="医技表单" type="1" script="phis.application.fsb.script.JCListForm"> 
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YJFORM</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		
		<module id="MED01010503" name="家床医技项目录入" type="1" script="phis.application.fsb.script.JCProjectEditorList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YJLIST</p>
				<p name="autoLoadData">0</p>
			</properties>
		</module >
		
		<module id="MED01010504" name="家床诊断结果选择" type="1" script="phis.application.med.script.DiagnosisMaintainSelect">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZDJG</p>
			</properties>
		</module>
		
		
		<module id="MED02" name="医技项目取消" script="phis.application.med.script.MedicalTechnologyProjectCancel">
			<properties>
				<p name="refTable">phis.application.med.MED/MED/MED0201</p>
			</properties>
			<action id="refresh" name="刷新" ></action>
			<action id="cancel" name="取消执行" iconCls="common_cancel"/>
		</module>
		<module id="MED0201" name="取消医技处理tab" type="1"  script="phis.application.med.script.MedicalTechnologyProjectCancelTab">
			<action id="mzTabcancel" name="门诊业务" ref="phis.application.med.MED/MED/MED020101" type="tab"/>
			<action id="zyTabcancel" name="住院业务" ref="phis.application.med.MED/MED/MED020102" type="tab"/>
			<action id="jcTabcancel" name="家床业务" ref="phis.application.med.MED/MED/MED020103" type="tab"/>
		</module>
		<module id="MED020101" name="取消医技-门诊" type="1" script="phis.application.med.script.MedicalTechnologyProjectCancelMod">
			<properties> 
				<p name="refUplist">phis.application.med.MED/MED/MED02010101</p>
				<p name="refDowList">phis.application.med.MED/MED/MED02010102</p>
			</properties>    
		</module>
		<module id="MED02010101" name="门诊病人列表" script="phis.application.med.script.MSPatientList" type="1" > 
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_MS_PATIENT</p>
				<p name="listServiceId">medicalTechnologyProjectService</p>
				<p name="serviceAction">queryMSPatient</p>
			</properties>
		</module>
		<module id="MED02010102" name="门诊项目列表" script="phis.application.med.script.MSProjectList" type="1" >
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_MS_PROJECT</p>
			</properties>
		</module>
		<module id="MED020102" name="取消医技-住院" type="1" script="phis.application.med.script.MedicalTechnologyProjectZyModuleMod">
			<properties> 
				<p name="refUplist">phis.application.med.MED/MED/MED02010201</p>
				<p name="refDowList">phis.application.med.MED/MED/MED02010202</p>
			</properties>    
		</module>
		<module id="MED02010201" name="住院病人列表" script="phis.application.med.script.ZYPatientList" type="1" > 
			<properties>
				<p name="entryName">phis.application.med.schemas.MED_ZY_PATIENT</p>
				<p name="listServiceId">phis.medicalTechnologyProjectService</p>
				<p name="serviceAction">queryZYPatient</p>
			</properties>
		</module>
		<module id="MED02010202" name="住院项目列表" type="1"  script="phis.application.med.script.ZYProjectList">
			<properties>   
				<p name="entryName">phis.application.med.schemas.YJ_ZYYWMX</p>
			</properties>
		</module>
		<module id="MED020103" name="取消医技-家床" type="1" script="phis.application.fsb.script.MedicalTechnologyProjectJcModuleMod">
			<properties> 
				<p name="refUplist">phis.application.med.MED/MED/MED02010301</p>
				<p name="refDowList">phis.application.med.MED/MED/MED02010302</p>
			</properties>    
		</module>
		<module id="MED02010301" name="家床病人列表" script="phis.application.fsb.script.JCPatientList" type="1" > 
			<properties>
				<p name="entryName">phis.application.fsb.schemas.MED_JC_PATIENT</p>
				<p name="listServiceId">phis.medicalTechnologyProjectService</p>
				<p name="serviceAction">queryJCPatient</p>
			</properties>
		</module>
		<module id="MED02010302" name="家床项目列表" type="1"  script="phis.application.fsb.script.JCProjectList">
			<properties>   
				<p name="entryName">phis.application.fsb.schemas.JC_YJYWMX</p>
			</properties>
		</module>
		<!-- 医技项目退回病区  add by wuyl 大面板-->		
		<module id="MED08" name="医技退回病区" type="1" script="phis.application.med.script.MedicalProjectBackWardModule">
			<properties>				    			
				<p name="refMedAppModule">phis.application.med.MED/MED/MED0801</p>	
				<p name="refWestAppModule">phis.application.med.MED/MED/MED0802</p>							
			</properties>
			<action id="retreat" name="退回" ></action>
			<action id="refresh" name="刷新" ></action>
			<action id="close" name="关闭" ></action>
		</module>			
		<!-- 医技项目退回病区  add by wuyl 左边面板-->		
		<module id="MED0802" name="医技科室名称" type="1" script="phis.application.med.script.MedicalDepartmentNameList">
			<properties>
				<p name="entryName">phis.application.med.schemas.GY_KSDM_THBQ</p>
			</properties>
		</module>			
		<!-- 医技项目退回病区  add by wuyl 中间面板-->	
		<module id="MED0801" name="医技项目" type="1" script="phis.application.med.script.MedicalProjectModule">
			<properties>
				<p name="refProjectList">phis.application.med.MED/MED/MED080101</p>
				<p name="refMedProjectList" >phis.application.med.MED/MED/MED080102</p>
			</properties>	
		</module>		
		<!-- 医技项目退回病区  add by wuyl 中间面板上部-->
		<module id="MED080101" name="医技审检情况" type="1" script="phis.application.med.script.MedicalProjectApplication">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZY01_SJ</p>					
			</properties>
		</module>		
		<!-- 医技项目退回病区  add by wuyl 中间面板下部-->
		<module id="MED080102" name="医技审检项目" type="1" script="phis.application.med.script.MedicalProjects">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZY02_XM</p>
			</properties>
		</module>
		<!-- 2013-07-09 住院项目列表直接使用医技项目执行中的MED010104  
				   把上面<p name="ZYProject">MED010104</p>替换成<p name="ZYProject">MED0204</p>即可还原
			<module id="MED0204" titile="住院项目列表" script="phis.application.med.script.ZYProjectList" type="1" >
				<properties>
					<p name="entryName">MED_ZY_PROJECT</p>
				</properties>
			</module>
			-->	
		<module id="MED03" name="诊断结果维护" script="phis.application.med.script.DiagnosisMaintainList">
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_ZDJG</p>
				<p name="sID">diagnosisMaintainService</p>
				<p name="sAction">saveZDJG</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="save" name="保存"/>
		</module> 
		<!--<module id="MED04" name="医技收入统计" script="phis.prints.script.MedicalTechnologyPrintView">
		</module>-->
		<module id="MED04" name="医技收入统计" script="phis.application.med.script.MedicalTechnologyModule">
			<properties>
				<p name="refLeftList">phis.application.med.MED/MED/MED0401</p>
				<p name="refRightList">phis.application.med.MED/MED/MED0402</p>
				<p name="serviceId">phis.medicalTechnologyService</p>
				<p name="leftActionId">queryMedicalTechnology</p>
				<p name="rightActionId">queryMedicalTechnologyDetail</p>
			</properties>
			<action id="query" name="刷新" />
		</module> 
		<module id="MED0401" name="医技收入统计左边list" script="phis.application.med.script.MedicalTechnologyLeftList" type="1" >
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_TJL</p>
				<p name="disablePagingTbr">true</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module> 
		<module id="MED0402" name="医技收入统计右边list" script="phis.application.med.script.MedicalTechnologyRightList" type="1" >
			<properties>
				<p name="entryName">phis.application.med.schemas.YJ_TJR</p>
				<p name="disablePagingTbr">true</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module> 
		<module id="MED05" name="基本统计" type="1" script="phis.application.med.script.MedicalTechnologyChat">
			<properties>
				<p name="entryName">phis.report.MedicalTechnologyChat</p>
			</properties>
		</module>
	</catagory>
</application>