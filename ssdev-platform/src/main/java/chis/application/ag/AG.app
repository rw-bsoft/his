<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.ag.AG" name="网格地址管理"  type="1">
	<catagory id="AG" name="网格地址维护">
		<module id="AA1" name="网格地址维护" script="chis.application.ag.script.AreaGridMaintenanceList">
			<properties>
				<p name="entryName">chis.application.ag.schemas.EHR_AreaGridChild</p>
				<p name="navDic">chis.dictionary.areaGrid</p>
				<p name="navField">RegionCode</p>
				<p name="navParentKey">%user.regionCode</p>
				<p name="codeRule" type="ja">[2,4,6, 9, 12, 15, 18, 21, 24]</p>
			</properties>
			<action id="create" name="新建"/>
			<action id="update" name="查看" />
			<action id="batchupdate" name="批量修改" iconCls="common_treat" />
			<action id="fileUpload" name="批量导入" iconCls="common_treat" />
		</module>
	</catagory>
</application>