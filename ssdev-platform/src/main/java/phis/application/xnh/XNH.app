<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.xnh.XNH" name="新农合配置管理">
	<catagory id="XNH" name="新农合配置管理">
		<module id="XNH01" name="农合信息下载" script="phis.script.TabModule">
			<action id="ypxxTab" viewType="list" name="农合药品信息" ref="phis.application.xnh.XNH/XNH/XNH0101"/>
			<action id="zlxxTab" viewType="list" name="农合诊疗信息" ref="phis.application.xnh.XNH/XNH/XNH0102"/>
			<action id="jbxxTab" viewType="list" name="农合疾病信息" ref="phis.application.xnh.XNH/XNH/XNH0103"/>
			<action id="dbzxxTab" viewType="list" name="农合单病种信息" ref="phis.application.xnh.XNH/XNH/XNH0104"/>
		</module>
		<module id="XNH0101" name="农合药品信息" type="1" script="phis.application.xnh.script.NhMedicinesManageList">
			<properties> 
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_YPML</p>
			</properties>
	        <action id="downloads" name="下载" iconCls="arrow-down" />
	        <action id="print" name="打印" />
		</module>
		
		<module id="XNH0102" name="农合诊疗 信息" type="1" script="phis.application.xnh.script.NhClinicItemsManageList">
			<properties> 
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_ZLML</p>
			</properties>
	        <action id="downloads" name="下载" iconCls="arrow-down" />
	        <action id="print" name="打印" />
		</module>

		<module id="XNH0103" name="农合疾病目录" type="1" script="phis.application.xnh.script.NhDiseasesManageList">
			<properties> 
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_JBBM</p>
			</properties>
	        <action id="downloads" name="下载" iconCls="arrow-down" />
	        <action id="print" name="打印" />
		</module>
		<module id="XNH0104" name="农合单病种目录" type="1" script="phis.application.xnh.script.NhDigsDiseasesManageList">
			<properties> 
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_JBBM_DRGS</p>
			</properties>
	        <action id="downloads" name="下载" iconCls="arrow-down" />
	        <action id="print" name="打印" />
		</module>
		
		<module id="XNH02" name="农合信息对照" script="phis.script.TabModule">
			<action id="ypxxdzTab" viewType="list" name="药品项目对照" ref="phis.application.xnh.XNH/XNH/XNH0201"/>
			<action id="zlxxdzTab" viewType="list" name="诊疗项目对照" ref="phis.application.xnh.XNH/XNH/XNH0202"/>
		</module>
		<module id="XNH0201" name="药品项目对照" type="1" script="phis.application.xnh.script.NhMedicinesCompareModule">
			<properties>
				<p name="refyyypmlList">phis.application.xnh.XNH/XNH/XNH020101</p>
				<p name="refnhypmlList">phis.application.xnh.XNH/XNH/XNH020102</p>
			</properties>
		</module>
		<module id="XNH020101" name="医院药品信息" type="1" script="phis.application.xnh.script.NhMedicinesCompareYyypmlLIst">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NHDZ_YK_TYPK</p>
			</properties>
			<action id="cancelmatch" name="取消匹配" iconCls="treeRemove" />
			<action id="matchself" name="自动匹配" iconCls="copy" />
		</module>
		<module id="XNH020102" name="农合药品目录(双击匹配)" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_YPML</p>
			</properties>
		</module>
		
		<module id="XNH0202" name="诊疗项目对照" type="1" script="phis.application.xnh.script.NhClinicItemsCompareModule">
			<properties>
				<p name="refyyypmlList">phis.application.xnh.XNH/XNH/XNH020201</p>
				<p name="refnhypmlList">phis.application.xnh.XNH/XNH/XNH020202</p>
			</properties>
		</module>
		<module id="XNH020201" name="医院诊疗信息" type="1" script="phis.application.xnh.script.NhClinicItemsCompareYyzlmlLIst">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NHDZ_GY_YLSF</p>
			</properties>
			<action id="cancelmatch" name="取消匹配" iconCls="treeRemove" />
			<action id="matchself" name="自动匹配" iconCls="copy" />
		</module>
		<module id="XNH020202" name="农合诊疗目录(双击匹配)" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_ZLML</p>
			</properties>
		</module>
		<module id="XNH03" name="农合对照上传下载" script="phis.script.TabModule">
			<action id="ypdzsxzcTab" viewType="list" name="药品对照上传下载" ref="phis.application.xnh.XNH/XNH/XNH0301"/>
			<action id="zldzsxzcTab" viewType="list" name="诊疗对照上传下载" ref="phis.application.xnh.XNH/XNH/XNH0302"/>
		</module>
		<module id="XNH0301" name="药品上传下载" type="1" script="phis.application.xnh.script.NhMedicinesSendandLoadLIst">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_YP_NEEDSP</p>
				<p name="initCnd">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
			</properties>
			<action id="sendmatch" name="单个上传" iconCls="arrow-up" />
			<action id="sendmatchall" name="批量上传" iconCls="arrow-up" />
			<action id="loadmatch" name="单个下载" iconCls="arrow-down" />
			<action id="loadmatchall" name="批量下载" iconCls="arrow-down" />
		</module>
		<module id="XNH0302" name="诊疗上传下载" type="1" script="phis.application.xnh.script.NhClinicItemsSendandLoadLIst">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_ZL_NEEDSP</p>
				<p name="initCnd">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
			</properties>
			<action id="sendmatch" name="上传" iconCls="arrow-up" />
			<action id="loadmatch" name="单个下载" iconCls="arrow-down" />
			<action id="loadmatchall" name="批量下载" iconCls="arrow-down" />
		</module>
		<module id="XNH04" name="医保报表" script="phis.script.TabModule">
			<!--<action id="hldzxx" viewType="list" name="门诊对账信息" ref="phis.application.xnh.XNH/XNH/XNH0401"/>-->
			<!--<action id="clinicall" viewType="list" name="门诊汇总" ref="phis.application.xnh.XNH/XNH/XNH0402"/>-->
			<action id="clinicall" viewType="list" name="门诊补偿汇总(职工)" ref="phis.application.xnh.XNH/XNH/XNH0402"/>
            <action id="clinicalljm" viewType="list" name="门诊补偿汇总(居民)" ref="phis.application.xnh.XNH/XNH/XNH0405"/>
			<!--<action id="hosdetalis" viewType="list" name="住院补偿明细" ref="phis.application.xnh.XNH/XNH/XNH0403"/>-->
			<!--<action id="xnhbcmxcx" viewType="list" name="新农合补偿明细对账" ref="phis.application.xnh.XNH/XNH/XNH0404"/>-->
		</module>
		<module id="XNH0401" name="合疗对账" type="1" script="phis.application.xnh.script.NhFyxxCompareModule">
			<properties>
				<p name="nhfyxxList">phis.application.xnh.XNH/XNH/XNH040101</p>
				<p name="sfxxList">phis.application.xnh.XNH/XNH/XNH040102</p>
			</properties>
		</module>
		<module id="XNH040101" name="农合费用信息" type="1" script="phis.application.xnh.script.NhFyxxCompareList">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_JSJL_MZ_DZ</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="XNH040102" name="医院收费信息" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_MZXX_DZ</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="XNH0402" name="门诊补偿汇总(职工)" type="1" script="phis.application.xnh.script.NhclinicallChargesPrintView">
             </module>
        <module id="XNH0405" name="门诊补偿汇总(居民)" type="1" script="phis.application.xnh.script.NhclinicallChargesjmPrintView">
             </module>
		<module id="XNH0403" name="住院补偿明细" type="1" script="phis.application.xnh.script.NhHosChargedetailsPrintView">
		</module>
		<module id="XNH0404" name="新农合补偿明细对账" type="1" script="phis.application.xnh.script.NhdetailsList">
			<properties>
				<p name="entryName">phis.application.xnh.schemas.NH_BSOFT_MXDZ</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>		
	</catagory>
</application>