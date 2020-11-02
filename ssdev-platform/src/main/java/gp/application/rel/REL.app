<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.rel.REL" name="添加关联管理" type="0">
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
		<module id="REL01" name="添加关联管理" script="gp.application.rel.script.RelevanceManageList">
			<properties> 
				<p name="entryName">gp.application.rel.schemas.REL_RelevanceDoctor_list</p>
				<p name="listServiceId">gp.relevanceManageService</p>  
				<p name="listMethod">loadRelevanceManageDoctor</p>  
			</properties> 
			<action id="save" name="保存"/> 
		</module>
		<module id="REL02" name="关联列表" script="gp.application.rel.script.RelevanceManageQueryList">
			<properties> 
				<p name="entryName">gp.application.rel.schemas.REL_RelevanceDoctor</p>
			</properties> 
		</module>
	</catagory>
</application>