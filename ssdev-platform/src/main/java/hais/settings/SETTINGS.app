<?xml version="1.0" encoding="UTF-8"?>

<application id="hais.settings.SETTINGS" name="系统设置" pageCount="1"> 
 	<catagory id="normal" name="基本管理">
 	  <module id="indicator" name="指标管理" iconCls="Indicator.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.IndicatorsManager</p> 
      		<p name="module">indicator</p> 
    	 </properties> 
 	  </module>
  	<module id="dimension" name="维度管理" iconCls="Dimension.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.DimensionManager</p> 
      		<p name="module">dimension</p> 
    	 </properties> 
 	  </module>  
  	<module id="datamodel" name="数据模型" iconCls="DataModel.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.DataModelManager</p> 
      		<p name="module">datamodel</p>
    	 </properties> 
 	  </module>  
  	<module id="thematic" name="主题管理" iconCls="Thematic.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.ThematicManager</p> 
      		<p name="module">thematic</p>
    	 </properties> 
 	  </module> 
  	<module id="complexThematic" name="组图管理" iconCls="ComplexThematic.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.ThematicManager</p> 
      		<p name="type">complex</p>
      		<p name="module">thematic</p>
    	 </properties> 
 	  </module> 
  	<module id="report" name="报表管理" iconCls="Report.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.ReportManager</p> 
      		<p name="module">report</p>
    	 </properties> 
 	  </module>   
  	<module id="advisory" name="报告管理" iconCls="User.png" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.ImagetextManager</p>
      		<p name="module">advisory</p> 
    	 </properties> 
 	  </module>   
 	</catagory>
	<catagory id="system" name="系统管理">
	 	<module id="restore" name="缓存管理" iconCls="Restore.png" group="system" script="hais.rpc.SwfEmbedPanel">  
 	  	 <properties> 
      		<p name="class">com.bsoft.hais.modules.manager.CacheManager</p> 
      		<p name="module">reload</p> 
    	 </properties> 
 	  </module>   
	</catagory>
</application>
