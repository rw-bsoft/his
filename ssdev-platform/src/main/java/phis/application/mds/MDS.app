<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.mds.MDS" name="药品管理">
	<catagory id="MDS" name="药品管理">
		<module id="MDS01" name="公共信息维护" script="phis.script.TabModule">
			<action id="basicTab" viewType="list" name="药品基本信息维护" ref="phis.application.mds.MDS/MDS/MDS0101"/>
			<action id="priceManageTab" viewType="list" name="价格管理" ref="phis.application.mds.MDS/MDS/MDS0102" />
			<action id="packageTab" viewType="list" name="药品包装信息管理" ref="phis.application.mds.MDS/MDS/MDS0103" />
		</module>
		<module id="MDS0101" name="药品基本信息维护" type="1"  script="phis.application.mds.script.MedicinesManageList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK</p>
				<p name="impref">phis.application.mds.MDS/MDS/MDS010102</p>
				<p name="serviceId">medicinesManageService</p>
				<p name="invalidActionId">invalidMedicines</p>
				<p name="refbasicMediMsg">phis.application.mds.MDS/MDS/MDS010103</p>
			</properties>
<!--			<action id="create" name="新增" ref="phis.application.mds.MDS/MDS/MDS010101" />-->
			<action id="update" name="修改" ref="phis.application.mds.MDS/MDS/MDS010101" />
			<action id="invalid" name="作废" iconCls="writeoff"/>
			<action id="import" name="调入" iconCls="ransferred_all" />
			<action id="print" name="打印" />
			<action id="lsjdr" name="数据引入" iconCls="ransferred_all" />
		</module>

		<module id="MDS01010106" name="药品数据引入"  type="1" script="phis.application.mds.script.LsjdrList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_YR</p>
			</properties>
		</module>

		<module id="MDS010103" name="药品基本信息维护打印" type="1" script="phis.application.mds.script.MedicinesBasicPrintView">
		</module>
		<module id="MDS010102" name="药品信息调入" type="1" script="phis.application.mds.script.MedicinesImportModule">
			<properties>
				<!--
					<arg name="navDic">medicinesCode</arg>
					-->
				<p name="navDic">phis.@manageUnit</p>
				<p name="houseStoreList">phis.application.mds.MDS/MDS/MDS01010202</p>
				<p name="navParentKey">%user.manageUnit.id</p>
				<p name="navParentText">%user.manageUnit.name</p>
				<p name="rootVisible">true</p>
				<p name="entryName">phis.application.mds.schemas.YK_YPXX_DR</p>
				<p name="refList">phis.application.mds.MDS/MDS/MDS01030201</p>
				<p name="saveActionId">saveMedicinesPrivateImportInformation</p>
				<p name="serviceId">medicinesManageService</p>
				<p name="queryStoreActionId">queryHouseStore</p>
			</properties>
			<action id="timeQuery" name="查询" iconCls ="query"/>
			<action id="save" name="确认" iconCls ="common_select"/>
			<!--<action id="print" name="打印" />-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="MDS01010202" name="机构对应药库" type="1" script="phis.script.SelectList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_YKLB_UNIT</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="MDS01030201" name="药品信息调入" type="1"  script="phis.application.mds.script.MedicinesImportList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_IMPORT</p>
			</properties>
		</module>
		<module id="MDS0102" name="价格管理"  type="1" script="phis.application.mds.script.MedicinesPriceManageList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_YPCD_JGGL</p>
			</properties>
			<!--<action id="print" name="打印" />-->
		</module>
		<module id="MDS0103" name="药品包装信息维护"  type="1"   script="phis.application.mds.script.MedicinesManagePackageList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_BZ</p>
				<p name="headPlug">true</p>
				<p name="refmediPackageMsg">phis.application.mds.MDS/MDS/MDS010301</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="MDS010301" name="药品包装信息维护打印" type="1" script="phis.application.mds.script.MediPackageMsgPrintView">
		</module>
		<module id="MDS010101" name="药品公共信息维护module"  type="1"  script="phis.application.mds.script.MedicinesManageModule">
			<properties>
				<p name="serviceId">medicinesManageService</p>
				<p name="saveServiceAction">saveMedicinesInfomation</p>
			</properties>
			<action id="mdsproptab" viewType="form" name="药品属性" ref="phis.application.mds.MDS/MDS/MDS01010101" />
			<action id="mdspricetab" viewType="priceList" name="药品价格" ref="phis.application.mds.MDS/MDS/MDS01010102" />
			<action id="mdsaliastab" viewType="list" name="药品别名" ref="phis.application.mds.MDS/MDS/MDS01010103"  />
			<action id="mdslimittab" viewType="editlist" name="用药限制" ref="phis.application.mds.MDS/MDS/MDS01010104" />
			<action id="ypsmtab" viewType="text" name="药品说明" ref="phis.application.mds.MDS/MDS/MDS01010105" />
		</module>
		<module id="MDS01010101" name="药品属性"   type="1"  script="phis.application.mds.script.MedicinesManageForm">
			<properties>
				<p name="serviceId">medicinesManageService</p>
				<p name="verifiedUsingServiceAction">verifiedUsing</p>
				<p name="queryPljcActionId">queryPljc</p>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK</p>
				<p name="fldDefaultWidth">100</p>
			</properties>
		</module>
		<module id="MDS01010102" name="价格" type="1"   script="phis.application.mds.script.MedicinesManagePriceList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_YPCD</p>
				<p name="serviceId">medicinesManageService</p>
				<p name="removeServiceActionId">reomovePriceInformation</p>
				<p name="queryPljcActionId">queryPljc</p>
				<p name="refjsq">phis.application.mds.MDS/MDS/MDS0101010201</p>
			</properties>
		</module>
		<module id="MDS0101010201" name="设置计算公式" type="1"  script="phis.application.mds.script.MedicinesManagePriceCalculateModule">
			<properties>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="关闭" iconCls="close"/>
		</module>
		<module id="MDS01010103" name="药品别名" type="1"   script="phis.application.mds.script.MedicinesAliasEditorList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_YPBM</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="MDS01010104" name="用药限制"  type="1" script="phis.application.mds.script.MedicinesLimitEditorList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.GY_BRXZ_YYXZ</p>
				<p name="serviceId">phis.medicinesManageService</p>
				<p name="listMedicinesLimitServiceId">limitInformationList</p>
			</properties>
		</module>
		<module id="MDS01010105" name="药品说明" type="1" script="phis.application.mds.script.MedicinesDescriptionForm">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_YPSM</p>
			</properties>
		</module>
		<module id="MDS02" name="药品属性维护"   script="phis.script.TabModule" iconCls="MDS01">
			<action id="MedicinesDosageTab" viewType="list" name="药品剂型维护" ref="phis.application.mds.MDS/MDS/MDS0201" />
			<action id="DispensingModeTab" viewType="list" name="发药方式维护" ref="phis.application.mds.MDS/MDS/MDS0202" />
		</module>
		<module id="MDS0201" name="药品剂型维护" type="1"  script="phis.application.mds.script.MedicinesDosageList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_YPSX</p>
				<p name="impref">2</p>
				<p name="removeByFiled">SXMC</p>
				<p name="verifiedUsingServiceId">medicinesManageService</p>
				<p name="verifiedUsingActionId">verifiedUsing_base</p>
				<p name="updateCls">phis.application.mds.script.MedicinesDosageForm</p>
				<p name="createCls">phis.application.mds.script.MedicinesDosageForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="MDS0202" name="发药方式维护" type="1" script="phis.application.mds.script.MedicinesDispensingModeList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.ZY_FYFS</p>
				<p name="removeByFiled">FSMC</p>
				<p name="verifiedUsingServiceId">medicinesManageService</p>
				<p name="verifiedUsingActionId">verifiedUsing_base</p>
				<p name="updateCls">phis.application.mds.script.MedicinesDispensingModeForm</p>
				<p name="createCls">phis.application.mds.script.MedicinesDispensingModeForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="MDS03" name="生产厂家维护"  script="phis.application.mds.script.MedicinesManufacturerList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_CDDZ</p>
				<p name="removeByFiled">CDQC</p>
				<p name="verifiedUsingServiceId">medicinesManageService</p>
				<p name="verifiedUsingActionId">verifiedUsing_base</p>
				<p name="updateCls">phis.application.mds.script.MedicinesManufacturerForm</p>
				<p name="createCls">phis.application.mds.script.MedicinesManufacturerForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改"/>
			<action id="remove" name="删除" />
		</module>
		<module id="MDS04" name="进货单位维护"   script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_JHDW</p>
				<p name="createCls">phis.application.mds.script.MedicinesPurchaseUnitsForm</p>
				<p name="updateCls">phis.application.mds.script.MedicinesPurchaseUnitsForm</p>
			</properties>
			<action id="create" name="新增" 	/>
			<action id="update" name="修改" />
		</module>

	</catagory>
</application>