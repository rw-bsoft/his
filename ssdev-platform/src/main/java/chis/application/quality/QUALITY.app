<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.quality.QUALITY" name="质控评分标准维护"   type="1">
	<catagory id="QUALITY" name="配置设置">
	 <module id="QUALITY01" name="质控评分标准维护" script="chis.application.quality.script.QualityControl"> 
				<properties> 
					<p name="entryName">chis.application.quality.schemas.QUALITY_ZK</p>  
					<p name="addModule">chis.application.quality.QUALITY/QUALITY/QUALITY02</p>
				</properties>  
			   <action id="open" name="新建" iconCls="update"/>
		       <action id="modify" name="查看" iconCls="update" />
			   <action id="remove" name="删除" group="update" />
	 </module>
	 <module id="QUALITY02" name="质控评分标准维护" script="chis.application.quality.script.QualityControl_module"> 
				<action id="Control_form" name="质控评分标准维护form" ref="chis.application.quality.QUALITY/QUALITY/QUALITY03"/>  
				<action id="Control_list" name="质控评分标准维护list" ref="chis.application.quality.QUALITY/QUALITY/QUALITY04"/>
	 </module>  
	 <module id="QUALITY03" name="质控评分标准维护form" script="chis.application.quality.script.QualityControl_form"> 
	         <properties> 
					<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_Form</p>  
				</properties>
			   <action id="save" name="保存" group="update"/>
		       <action id="modify" name="新填" iconCls="update" />
			   <action id="close" name="关闭" group="update" />
	 </module>  
	  <module id="QUALITY04" name="质控评分标准维护list" script="chis.application.quality.script.QualityControl_list"> 
	         <properties> 
					<p name="entryName">chis.application.quality.schemas.QUALITY_ZK_GXSD</p>  
			 </properties>
			 <action id="onsave" name="添加至列表" iconCls="update" />
			 <action id="remove" name="清空" group="update" />
	 </module>
	</catagory>
</application>