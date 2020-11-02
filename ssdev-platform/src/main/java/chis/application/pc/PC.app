<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.pc.PC" name="意见反馈"  type="1">
	<catagory id="PC" name="问题收集">
			<module id="AB1" name="问题收集" script="chis.application.pc.script.PorblemCollectListView"> 
				<properties> 
					<p name="entryName">chis.application.pc.schemas.ADMIN_ProblemCollect</p>  
					<p name="updateCls">chis.application.pc.script.PorblemCollectFormView</p>  
					<p name="createCls">chis.application.pc.script.PorblemCollectFormView</p>  
				</properties>  
				<action id="createInfo" name="新建" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="treat" name="处理" iconCls="common_treat"/>  
				<action id="remove" name="删除"/> 
			</module>  
			<module id="AB1_1" name="问题收集表单" script="chis.application.pc.script.PorblemCollectFormView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pc.schemas.ADMIN_ProblemCollect</p> 
				</properties>  
				<action id="save" name="保存"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module> 
			<module id="AB2" name="文件下载" script="chis.application.pc.script.UpandDownload"> 
				<properties> 
					<p name="entryName">chis.application.pc.schemas.ADMIN_ProblemCollect</p>  
					<p name="updateCls">chis.application.pc.script.PorblemCollectFormView</p>  
					<p name="createCls">chis.application.pc.script.PorblemCollectFormView</p>  
				</properties>
				<action id="upload" name="上传文件" iconCls="create"/>  
				<action id="modify" name="查看" iconCls="update"/>  
				<action id="treat" name="处理" iconCls="common_treat"/>  
				<action id="remove" name="删除"/> 
			</module>
			<module id="AB2_1" name="问题收集表单" script="chis.application.pc.script.PorblemCollectFormView" type="1"> 
				<properties> 
					<p name="entryName">chis.application.pc.schemas.ADMIN_ProblemCollect</p> 
				</properties>  
				<action id="save" name="保存"/>  
				<action id="cancel" name="取消" iconCls="common_cancel"/> 
			</module> 
	</catagory>
</application>