<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.ccl.CCL" name="电子开单">
	<catagory id="CCL" name="电子开单">
	    <module id="CCL15" name="检查申请字典维护" script="phis.application.ccl.script.CheckApplyMaintain">
			<action id="CheckTypeList" viewType="list" name="检查类型" ref="phis.application.ccl.CCL/CCL/CCL1501" type="tab"/>
			<action id="CheckPointList" viewType="list" name="检查部位" ref="phis.application.ccl.CCL/CCL/CCL1502" type="tab"/>
			<action id="CheckProjectList" viewType="list" name="检查项目" ref="phis.application.ccl.CCL/CCL/CCL1503" type="tab"/>
			<action id="CheckRelationList" viewType="list" name="对应关系维护" ref="phis.application.ccl.CCL/CCL/CCL1504" type="tab"/>
		</module>
		
		<module id="CCL1501" name="检查类型" script="phis.application.ccl.script.CheckApplyType_WH" type="1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCLB</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="CCL1502" name="检查部位" script="phis.application.ccl.script.CheckApplyPoint_WH" type="1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCBW</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="CCL1503" name="检查项目" script="phis.application.ccl.script.CheckApplyProject_WH" type="1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCXM_WH</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="CCL1504" name="对应关系维护" type="1" script="phis.application.ccl.script.CheckApplyRelationModule">
			<properties>
				<p name="refCheckTypeList">phis.application.ccl.CCL/CCL/CCL150401</p>
				<p name="refCheckPointList">phis.application.ccl.CCL/CCL/CCL150402</p>
				<p name="refCheckProjectList">phis.application.ccl.CCL/CCL/CCL150403</p>
			</properties>
		</module>
		
		<module id="CCL150401" name="检查类别" type="1" script="phis.application.ccl.script.CheckTypeList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCLB</p>
			</properties>
			<action id="remove" name="删除" />
		</module>
		
		<module id="CCL150402" name="检查部位" type="1" script="phis.application.ccl.script.CheckPointList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY_BW</p>
				<p name="refPointModule">phis.application.ccl.CCL/CCL/CCL15040201</p>
			</properties>
			<action id="add" name="增加" iconcls="add"/>
			<action id="remove" name="删除" />
		</module>
		
		<module id="CCL15040201" name="检查部位导入" type="1" script="phis.application.ccl.script.CheckPointPutInModule">
			<properties>
				<p name="refPointList">phis.application.ccl.CCL/CCL/CCL1504020101</p>
			</properties>
		</module>
		
		<module id="CCL1504020101" name="检查部位导入list" type="1" script="phis.application.ccl.script.CheckPointPutInList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCBW</p>
			</properties>
		</module>
		
		<module id="CCL150403" name="检查项目" type="1" script="phis.application.ccl.script.CheckProjectList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY_XM</p>
				<p name="refProjectModule">phis.application.ccl.CCL/CCL/CCL15040301</p>
			</properties>
			<action id="add" name="增加" iconcls="add"/>
			<action id="save" name="保存" iconcls="save"/>
			<action id="remove" name="删除" />
		</module>
		
		<module id="CCL15040301" name="检查项目导入" type="1" script="phis.application.ccl.script.CheckProjectPutInModule">
			<properties>
				<p name="refProjectList">phis.application.ccl.CCL/CCL/CCL1504030101</p>
			</properties>
		</module>
		
		<module id="CCL1504030101" name="检查项目导入list" type="1" script="phis.application.ccl.script.CheckProjectPutInModule1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCXM</p>
			</properties>
		</module>
		
		
		<module id="CCL40" name="心电申请" type="1" script="phis.application.ccl.script.CheckApplyModule_CIC1">
			<properties>
				<p name="refCheckApplyList">phis.application.ccl.CCL/CCL/CCL4001</p>
				<p name="refCheckApplyForm">phis.application.ccl.CCL/CCL/CCL4002</p>
				<!--<p name="refCheckApplyBQMSMBModule">phis.application.ccl.CCL/CCL/CCL2903</p>-->
			</properties>
		</module>
		
		<module id="CCL4002" name="检查开单FORM" type="1" script="phis.application.ccl.script.CheckApplyForm_CIC1">
			<properties>
				<p name="colCount">6</p>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_ZDJBXX</p>
			</properties>
		</module>
		
		<module id="CCL4001" name="心电检查申请开单list" type="1" script="phis.application.ccl.script.CheckApplyTabModule1">
			<action id="CCL400101" viewType="module" name="检查开单/修改" ref="phis.application.ccl.CCL/CCL/CCL400101" type="tab"/>
			<action id="CCL400102" viewType="module" name="已开检查单列表" ref="phis.application.ccl.CCL/CCL/CCL400102" type="tab"/>
		</module>
		
		<module id="CCL400101" name="心电检查开单/修改" type="1" script="phis.application.ccl.script.CheckApplyOperationModule1">
			<properties>
				<p name="refCheckTypeList">phis.application.ccl.CCL/CCL/CCL40010101</p>
				<p name="refCheckPointList">phis.application.ccl.CCL/CCL/CCL40010102</p>
				<p name="refCheckProjectList">phis.application.ccl.CCL/CCL/CCL40010103</p> 
				<p name="refFeeDetailsList">phis.application.ccl.CCL/CCL/CCL40010104</p> 
			</properties>
		</module>
		
		<module id="CCL400102" name="已开检查申请单" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationModule_CIC1">
			<properties>
				<p name="refCheckApplyExchangedApplicationList">phis.application.ccl.CCL/CCL/CCL40010201</p>
				<p name="refCheckApplyExchangedApplicationDetailsList">phis.application.ccl.CCL/CCL/CCL40010202</p>
			</properties>
		</module>
		
		<module id="CCL40010101" name="检查类别" type="1" script="phis.application.ccl.script.CheckTypeList_KD1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCLB</p>
				<p name="serviceId">phis.checkApplyService</p>
				<p name="queryServiceAction">getCheckPaintList</p>
			</properties>
		</module>
		
		<module id="CCL40010102" name="检查部位" type="1" script="phis.application.ccl.script.CheckPointList_KD1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY_BW</p>
				<p name="pageSize">100</p>
			</properties>
		</module>
		<module id="CCL40010103" name="检查项目" type="1" script="phis.application.ccl.script.CheckProjectList_KD1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY_XM</p>
				<p name="pageSize">100</p>
			</properties>
		</module>
		<module id="CCL40010104" name="收费明细" type="1" script="phis.application.ccl.script.CheckApplyFeeDetails_KD_CIC1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_SFMX_KD</p>
			</properties>
			<action id="commit" name="提交" iconCls="commit"/>
			<action id="remove" name="删除"/>
			<action id="renew" name="清空" iconCls="new"/>
			<!--<action id="print" name="打印" iconCls="print"/>-->
		</module>
		
		<module id="CCL40010201" name="已开检查申请单list" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationList1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_YKSQD_CIC</p>
				<p name="openby">CIC</p>
			</properties>
		</module>
		<module id="CCL40010202" name="已开检查申请单明细list" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_YKSQDMX</p>
			</properties>
		</module>
		<module id="CCL400103" name="已开检查申请单" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationModule_CIC3">
			<properties>
				<p name="refCheckApplyExchangedApplicationList">phis.application.ccl.CCL/CCL/CCL40010301</p>
				<p name="refCheckApplyExchangedApplicationDetailsList">phis.application.ccl.CCL/CCL/CCL40010302</p>
			</properties>
		</module>
		<module id="CCL40010301" name="已开检查申请单list" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationList3">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_YKSQD_CIC</p>
				<p name="openby">CIC</p>
			</properties>
		</module>
		<module id="CCL40010302" name="已开检查申请单明细list" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList1">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_YKSQDMX</p>
			</properties>
		</module>
		
		<module id="CCL16" name="检查费用明细维护" script="phis.application.ccl.script.CheckApplyFeeModule">
			<properties>
				<p name="refCheckList">phis.application.ccl.CCL/CCL/CCL1601</p>
				<p name="refStackList">phis.application.ccl.CCL/CCL/CCL1602</p>
				<p name="refFeeBoundList">phis.application.ccl.CCL/CCL/CCL1603</p>
			</properties>
		</module>
		<module id="CCL1601" name="检查项目" type="1" script="phis.application.ccl.script.CheckApplyProjectForFee">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY</p>
			</properties>
		</module>
		<module id="CCL1602" name="项目组套" type="1" script="phis.application.ccl.script.PublicStackList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_ZT01</p>
				<p name="refPublicStackDetailsModule">phis.application.ccl.CCL/CCL/CCL160201</p>
				<p name="refPublicStackDetailsList">phis.application.ccl.CCL/CCL/CCL16020101</p> 
			</properties>
		</module>
		
		<module id="CCL160201" name="组套明细" type="1" script="phis.application.ccl.script.PublicStackDetailsModule">
			<properties>
				<p name="refPublicStackDetailsList">phis.application.ccl.CCL/CCL/CCL16020101</p> 
			</properties>
		</module>
		<module id="CCL16020101" name="组套明细list" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_ZT02</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		
		<module id="CCL1603" name="费用绑定" type="1" script="phis.application.ccl.script.FeeBoundList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_SFMX</p>
			</properties>
			<action id="save" name="保存" iconcls="save"/>
			<action id="remove" name="删除" />
		</module>
		
		
		
		
		<module id="CCL22" name="检查申请" type="1" script="phis.application.ccl.script.CheckApplyModule_WAR">
			<properties>
				<p name="refCheckApplyList">phis.application.ccl.CCL/CCL/CCL2201</p>
				<p name="refCheckApplyForm">phis.application.ccl.CCL/CCL/CCL2202</p> 
				<!--<p name="refCheckApplyBQMSMBModule">CCL2203</p>-->
			</properties>
		</module>
		
		<module id="CCL2201" name="检查申请开单list" type="1" script="phis.application.ccl.script.CheckApplyTabModule">
			<action id="CCL220101" viewType="module" name="检查开单/修改" ref="phis.application.ccl.CCL/CCL/CCL220101" type="tab"/>
			<action id="CCL220102" viewType="module" name="已开检查单列表" ref="phis.application.ccl.CCL/CCL/CCL220102" type="tab"/>
		</module>
		<module id="CCL220101" name="检查开单/修改" type="1" script="phis.application.ccl.script.CheckApplyOperationModule">
			<properties> 
				<p name="refCheckTypeList">phis.application.ccl.CCL/CCL/CCL22010101</p>
				<p name="refCheckPointList">phis.application.ccl.CCL/CCL/CCL22010102</p>
				<p name="refCheckProjectList">phis.application.ccl.CCL/CCL/CCL22010103</p> 
				<p name="refFeeDetailsList">phis.application.ccl.CCL/CCL/CCL22010104</p> 
				<p name="openby">WAR</p>
			</properties>
		</module>
		<module id="CCL22010101" name="检查类别" type="1" script="phis.application.ccl.script.CheckTypeList_KD">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_JCLB</p>
			</properties>
		</module>
		<module id="CCL22010102" name="检查部位" type="1" script="phis.application.ccl.script.CheckPointList_KD">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY_BW</p>
				<p name="pageSize">100</p>
			</properties>
		</module>
		<module id="CCL22010103" name="检查项目" type="1" script="phis.application.ccl.script.CheckProjectList_KD">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_XMDY_XM</p>
			</properties>
		</module>
		<module id="CCL22010104" name="收费明细" type="1" script="phis.application.ccl.script.CheckApplyFeeDetails_KD_WAR">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_SFMX_KD</p>
			</properties>
			<action id="remove" name="删除"/>
			<action id="renew" name="清空" iconCls="new"/>
			<action id="commit" name="提交" iconCls="commit"/>
			

		</module>
		
		<module id="CCL220102" name="已开检查申请单" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationModule_WAR">
			<properties>
				<p name="refCheckApplyExchangedApplicationList">phis.application.ccl.CCL/CCL/CCL22010201</p>
				<p name="refCheckApplyExchangedApplicationDetailsList">phis.application.ccl.CCL/CCL/CCL22010202</p>
			</properties>
		</module>
		<module id="CCL22010201" name="已开检查申请单list" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_YKSQD_WAR</p>
				<p name="openby">WAR</p>
			</properties>
		</module>
		<module id="CCL22010202" name="已开检查申请单明细list" type="1" script="phis.application.ccl.script.CheckApplyExchangedApplicationDetailsList">
			<properties>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_YKSQDMX</p>
			</properties>
		</module>
		
		<module id="CCL2202" name="检查开单FORM" type="1" script="phis.application.ccl.script.CheckApplyForm_WAR">
			<properties>
				<p name="colCount">6</p>
				<p name="entryName">phis.application.ccl.schemas.YJ_JCSQ_ZYJBXX</p>
			</properties>
		</module>
		
	 	
	</catagory>
</application>