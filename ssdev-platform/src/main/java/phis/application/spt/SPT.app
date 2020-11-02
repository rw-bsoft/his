<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.spt.SPT" name="省平台药品信息">
	<catagory id="SPT" name="省平台药品信息">
		<module id="SPT01" name="省平台药品信息下载"   script="phis.application.spt.script.MedicinesSPT">
			<properties>
				<p name="initCnd">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
				<p name="entryName">phis.application.spt.schemas.YK_TYPK_SPT</p>
			</properties>
			<action id="downloads" name="下载药品信息" 	 iconCls="arrow-down" />
			<action id="gysdownloads" name="下载药品供应商信息" 	 iconCls="arrow-down" />
		</module> 
		<module id="SPT02" name="省平台药品信息对照"   script="phis.application.spt.script.SptMedicinesCompareModule">
			<properties>
				<p name="refyyypmlList">phis.application.spt.SPT/SPT/SPT0201</p>
				<p name="refnhypmlList">phis.application.spt.SPT/SPT/SPT0202</p>
			</properties>
		</module>
		<module id="SPT0201" name="医院药品信息" type="1" script="phis.application.spt.script.SptMedicinesCompareList">
			<properties>
				<p name="initCnd">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
				<p name="entryName">phis.application.spt.schemas.V_YPXX_SPT</p>
			</properties>
		     <action id="cancelmatch" name="取消匹配" iconCls="treeRemove" />
		</module>
		<module id="SPT0202" name="省平台药品目录(双击匹配)" type="1" script="phis.script.SimpleList">
			<properties>
			    <p name="initCnd">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
				<p name="entryName">phis.application.spt.schemas.YK_TYPK_SPT</p>
			</properties>
		</module>
	</catagory>
</application>