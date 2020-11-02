<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.odm.ODM" name="自备药管理">
	<catagory id="ODM" name="自备药管理">
		<module id="ODM01" name="自备药使用查询" 
			script="phis.application.odm.script.OwnedDrugManageModule" >
			<properties>
				<p name="refLeft">phis.application.odm.ODM/ODM/ODM0101</p>
				<p name="refRight">phis.application.odm.ODM/ODM/ODM0102</p>
				<p name="serviceId">phis.ownedDrugManageService</p>
				<p name="queryLeftActionId">queryYplb</p>
				<p name="queryRightActionId">queryMx</p>
			</properties>
			<action id="query" name="查询" iconCls="query" />
		</module>
		<module id="ODM0101" name="自备药门诊统计-左边List" type="1"
			script="phis.application.odm.script.OwnedDrugManageLeftList" >
			<properties>
				<p name="entryName">phis.application.odm.schemas.ODM_YPLB</p>
				<p name="autoLoadData">false</p>	
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="ODM0102" name="自备药门诊统计-右边List" type="1"
			script="phis.application.odm.script.OwnedDrugManageRightList" >
			<properties>
				<p name="entryName">phis.application.odm.schemas.ODM_MXLB</p>
				<p name="autoLoadData">false</p>	
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
	</catagory>
</application>