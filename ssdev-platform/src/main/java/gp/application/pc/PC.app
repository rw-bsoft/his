<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.pc.PC" name="意见反馈"  type="1">
	<catagory id="PC" name="问题收集">
			<module id="AB1" name="问题收集" script="gp.application.pc.script.PorblemCollectListView"> 
				<properties> 
					<p name="entryName">gp.application.pc.schemas.ADMIN_ProblemCollect</p>  
					<p name="updateCls">gp.application.pc.script.PorblemCollectFormView</p>  
					<p name="createCls">gp.application.pc.script.PorblemCollectFormView</p>  
				</properties>  
				<action id="createInfo" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="treat" name="处理" iconCls="common_treat"/>  
				<action id="remove" name="删除"/> 
				<action id="tbjg" name="同步机构到数据库" iconCls="create"/> 
			</module>  
			<module id="AB1_1" name="问题收集表单" script="gp.application.pc.script.PorblemCollectFormView" type="1"> 
				<properties> 
					<p name="entryName">gp.application.pc.schemas.ADMIN_ProblemCollect</p> 
				</properties>  
				<action id="save" name="保存"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module> 
	</catagory>
</application>