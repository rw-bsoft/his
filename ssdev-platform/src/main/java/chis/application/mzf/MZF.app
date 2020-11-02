<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.mzf.MZF" name="慢阻肺管理">
	<catagory id="MZF" name="慢阻肺管理">
		<module id="MZFDA01" name="慢阻肺档案管理" script="chis.script.CombinedDocList">
			<properties> 
				<p name="entryName">chis.application.mzf.schemas.MZF_DocumentRecord</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.mzf.MZF/MZF/MZFDA0101" />
		</module>
		<module id="MZFDA0101" name="慢阻肺档案" type="1" script="chis.application.mzf.script.MZFRecordListView">
			<properties>
				<p name="entryName">chis.application.mzf.schemas.MZF_DocumentRecord</p>
				<p name="listServiceId">chis.mZFRecordService</p>
				<p name="listAction">ListMZFRecord</p>

			</properties>
			<action id="createByEmpi" name="新建" iconCls="create" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="visit" name="随访" iconCls="hypertension_visit" />
			<action id="removeRecord" name="删除" iconCls="remove" />
			<action id="print" name="打印" />
		</module>
		
		<module id="MZFDA0102" name="慢阻肺基本档案组合模块" script="chis.application.mzf.script.MZFRecordModule" type="1">
			<properties>
				<p name="entryName">chis.application.mzf.schemas.MZF_DocumentRecord</p>
			</properties>
			<action id="MZFRecordForm" name="慢阻肺档案表单" ref="chis.application.mzf.MZF/MZF/MZFDA010201" />
		</module>
		
		<module id="MZFDA010201" name="慢阻肺档案表单" script="chis.application.mzf.script.MZFRecordForm" type="1" >
			<properties>
				<p name="entryName">chis.application.mzf.schemas.MZF_DocumentRecord</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
		</module>
		
		<module id="MZF01" name="慢阻肺随访记录" script="chis.application.mzf.script.MZFList">
			<properties>
				<p name="entryName">chis.application.mzf.schemas.MZF_VisitRecord</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
			<!-- <action id="createRecord" name="新建" iconCls="create"/> -->
            <action id="remove" name="删除" iconCls="remove"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="print"  name="导出" />
            <action id="quchong" name="去重" iconCls="remove" />
		</module>
		<module id="MZF02" name="慢阻肺随访整体" script="chis.application.mzf.script.MZFVisitModule" type="1">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="InquireList" name="慢阻肺随访列表" ref="chis.application.mzf.MZF/MZF/MZF02_01" />
			<action id="InquireForm" name="慢阻肺随访记录" ref="chis.application.mzf.MZF/MZF/MZF02_02" />
		</module>
		<module id="MZF02_01" name="慢阻肺随访列表"
			script="chis.application.mzf.script.MZFVisitList" type="1">
			<properties>
				<p name="entryName">chis.application.mzf.schemas.MZF_VisitRecord</p>
				<p name="selectFirst">true</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="MZF02_02" name="慢阻肺随访记录"
			script="chis.application.mzf.script.MZFVisitForm" type="1">
			<properties>
				<p name="entryName">chis.application.mzf.schemas.MZF_VisitRecord</p>
				<p name="colCount">2</p>
				<p name="labelAlign">left</p>
				<p name="labelWidth">100</p>
				<p name="autoFieldWidth">false</p>
				<p name="fldDefaultWidth">200</p>
				<p name="autoLoadData">false</p>
				<p name="showButtonOnTop">true</p>
				<p name="autoLoadSchema">false</p>
				<p name="serviceId">chis.mzfVisitService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="add" name="新增" group="create" />
		</module>
	</catagory>	
</application>