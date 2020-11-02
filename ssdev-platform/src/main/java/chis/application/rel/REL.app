<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.rel.REL" name="添加关联管理" type="0">
	<catagory id="REL" name="添加关联管理">
		<!--<module id="REL01" name="关联管理" script="gp.application.rel.script.RelevanceManageModule">
			<properties> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="relList" name="添加关联管理list" ref="gp.application.rel.REL/REL/REL01_1"/>  
			<action id="business" name="业务" ref="gp.application.rel.REL/REL/REL01_1"/> 
		</module>-->
		<!--<module id="REL01_1" name="添加关联管理" script="gp.application.rel.script.RelevanceManageList">
			<properties> 
				<p name="entryName">gp.application.rel.schemas.REL_RelevanceDoctor_list</p>
				<p name="listServiceId">gp.relevanceManageService</p>  
				<p name="listMethod">loadRelevanceManageDoctor</p>  
			</properties> 
			<action id="add" name="添加关联" iconCls="create"/> 
		</module>
		<module id="REL01_1_1" name="添加关联管理" type="1" script="gp.application.rel.script.RelevanceManageForm">
			<properties> 
				<p name="entryName">gp.application.rel.schemas.REL_RelevanceDoctor</p>
			</properties> 
			<action id="save" name="确定"/> 
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>
		<module id="REL01_2" name="添加关联管理" type="1" script="gp.application.rel.script.RelevanceManageListR">
			<properties> 
				<p name="entryName">gp.application.rel.schemas.REL_RelevanceDoctor</p>
			</properties> 
			<action id="add" name="添加关联" iconCls="create"/> 
		</module>-->
		<module id="REL01" name="添加关联管理" script="chis.application.rel.script.RelevanceManageList">
			<properties> 
				<p name="entryName">chis.application.rel.schemas.REL_RelevanceDoctor_list</p>
				<p name="listServiceId">chis.relevanceManageService</p>  
				<p name="listAction">loadRelevanceManageDoctor</p>  
			</properties> 
			<action id="save" name="保存"/> 
		</module>
		<module id="REL02" name="关联列表" script="chis.application.rel.script.RelevanceManageQueryList">
			<properties> 
				<p name="entryName">chis.application.rel.schemas.REL_RelevanceDoctor</p>
			</properties> 
		</module>
		<module id="REL03" name="责任医生助理维护" script="chis.application.rel.script.ResponsibleDoctorManageList">
			<properties> 
				<p name="entryName">chis.application.rel.schemas.REL_ResponsibleDoctor</p>
				<p name="refForm">chis.application.rel.REL/REL/REL0301</p>
			</properties>
			<action id="createNew" name="新增" iconCls="create" />
			<action id="updatedata" name="查看" iconCls="update" />
		</module>
		<module id="REL0301" type="1" name="责任医生助理维护表单" script="chis.application.rel.script.ResponsibleDoctorManageForm">
			<properties> 
				<p name="entryName">chis.application.rel.schemas.REL_ResponsibleDoctor</p>
			</properties>
			<action id="saveData" name="保存" iconCls="create" />
			<action id="createNew" name="新建" iconCls="update" />
		</module>
	</catagory>
</application>