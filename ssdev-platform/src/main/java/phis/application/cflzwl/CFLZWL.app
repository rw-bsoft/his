<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.cflzwl.CFLZWL" name="处方流转模块" type="1">
	<catagory id="CFLZWL" name="物流信息">
		<module id="CFLZWL_01" name="物流信息列表" script="phis.application.cflzwl.script.cflzWlList">
			<properties> 
				<p name="entryName">phis.application.cflzwl.schemas.CFLZ_WLXX_MZ</p>
				<p name="ref">phis.application.cflzwl.CFLZWL/CFLZWL/CFLZWL_0101</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="remove" name="删除"/>
		</module>
		<module id="CFLZWL_0101" title="物流表单" script="phis.application.cflzwl.script.cflzWlForm" type="1">
			<properties>
				<p name="entryName">phis.application.cflzwl.schemas.CFLZ_WLXX_MZ</p>
				<p name="saveServiceId">phis.CFLZWLInfoService</p>
				<p name="saveAction">saveDemo</p>
				<p name="loadServiceId">phis.CFLZWLInfoService</p>
				<p name="loadAction">getCFLZWLInfo</p>
			</properties>
			<action id="save" name="修改" group="create||update"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>
		</module>
	</catagory>
</application>