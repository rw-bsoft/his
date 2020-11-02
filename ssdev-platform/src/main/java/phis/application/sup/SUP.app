<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.sup.SUP" name="物资管理">
	<catagory id="SUP" name="物资管理">
		<module id="SUP60" name="物流入库单" type="1" script="phis.prints.script.StorageOfMaterialsPrintView"/>	
		<module id="SUP62" name="转科单" type="1" script="phis.prints.script.StorageOfTransferPrintView"/>
		<module id="SUP63" name="报损单" type="1" script="phis.prints.script.StorageOfFaultyPrintView"/>
		<module id="SUP64" name="库存盘点单" type="1" script="phis.prints.script.StorageOfInventoryPrintView"/>
		<module id="SUP65" name="科室盘点单" type="1" script="phis.prints.script.StorageOfInventoryKsPrintView"/>
		<module id="SUP66" name="台账盘点单" type="1" script="phis.prints.script.StorageOfInventoryTzPrintView"/>
		<module id="SUP67" name="二级库存盘点单" type="1" script="phis.prints.script.StorageOfInventoryEjPrintView"/>
		<module id="SUP01" name="入库管理" script="phis.application.sup.script.StorageOfMaterialsListModule" iconCls="RD02">
			<properties>                              
				<p name="entryName">phis.application.sup.schemas.WL_RK01</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0102</p>
				<p name="serviceId">storageOfMaterialsService</p>
			</properties>
		</module>
		<module id="SUP0101" name="未确认入库单" type="1" script="phis.application.sup.script.StorageOfMaterialsUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01</p>
				<p name="refAdd">phis.application.sup.SUP/SUP/SUP010101</p>
				<p name="closeAction">true</p>
				<p name="refStoreOfMaterialsPrint">phis.application.sup.SUP/SUP/SUP60</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<action id="reject" name="冲红" level="two" iconCls="common_cancel"/>
			<action id="print" level="two" name="打印"/>
		</module>
		<module id="SUP0102" name="确认入库单" type="1" script="phis.application.sup.script.StorageOfMaterialsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP010101</p>
				<p name="serviceId">storageOfMaterialsService</p>
				<p name="refStoreOfMaterialsPrint">phis.application.sup.SUP/SUP/SUP60</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" level="two" iconCls="read"></action>
			<action id="print" level="two" name="打印"/>
		</module>
		<module id="SUP010101" name="入库管理" type="1"  script="phis.application.sup.script.StorageOfMaterialsModule">
			<properties>       
				<p name="entryName">phis.application.sup.schemas.WL_RK01_FORM</p>   
				<p name="refForm">phis.application.sup.SUP/SUP/SUP01010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP01010102</p>
				<p name="serviceId">storageOfMaterialsService</p>
				<p name="refStoreOfMaterialsPrint">phis.application.sup.SUP/SUP/SUP60</p>
			</properties>    
			<action id="create" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="common_cancel"/>
			<action id="commit" name="记账"/>
			<action id="reject" name="退回" iconCls="common_cancel"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP01010101" name="入库管理(form)" type="1" script="phis.application.sup.script.StorageOfMaterialsForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01_FORM</p>
			</properties>
		</module>
		<module id="SUP01010102" name="入库管理(Editor)" type="1" script="phis.application.sup.script.StorageOfMaterialsEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK02</p>
				<p name="serviceId">storageOfMaterialsService</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="SUP0101010201" name="资产库存选择" type="1" script="phis.application.sup.script.FixedAssetsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZCZB_YR</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP03" name="出库管理"
			script="phis.application.sup.script.MaterialsOutModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0301</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0302</p>
				<p name="serviceId">storageOfMaterialsService</p>
			</properties>
		</module>
		<module id="SUP0301" name="未记账出库单" type="1"
			script="phis.application.sup.script.MaterialsUndeterminedOutList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP030101</p>
				<p name="navParentKey">%user.manageUnit.id</p>
				<p name="serviceId">materialsOutService</p>
				<p name="verificationMaterialsOutDeleteActionId">verificationMaterialsOutDelete</p>
				<p name="removeMaterialsOutActionId">removeMaterialsOut</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<!-- <action id="reject" name="冲红" level="two" iconCls="common_cancel"/>-->
			<action id="print" name="打印" level="two" iconCls="print"/>
		</module>
		<module id="SUP0302" name="记账出库单" type="1"
			script="phis.application.sup.script.MaterialsOutList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01</p>
				<p name="readRef">phis.application.sup.SUP/SUP/SUP030101</p>
				<p name="serviceId">materialsOutService</p>
				<p name="verificationMaterialsOutDeleteActionId">verificationMaterialsOutDelete</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" level="two" iconCls="read"></action>
			<action id="print" name="打印" level="two" iconCls="print"/>
		</module>
		<module id="SUP030104" name="物资出库单" type="1" script="phis.prints.script.SuppliesOutPrintView">
		</module>
		<module id="SUP030101" name="物资出库"  type="1"
			script="phis.application.sup.script.MaterialsOutDetailModule">
			<properties>			
				<p name="refForm">phis.application.sup.SUP/SUP/SUP03010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP03010102</p>
				<p name="serviceId">materialsOutService</p>
				<p name="saveMaterialsOutActionId">saveMaterialsOut</p>
				<p name="materialsOutDJZTActionId">getMaterialsOutDJZT</p>
				<p name="materialsOutVerifyActionId">saveMaterialsOutVerify</p>
				<p name="materialsOutNoVerifyActionId">saveMaterialsOutNoVerify</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>		
			<action id="create" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="reject" name="退回" iconCls="common_cancel"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP03010101" name="物资出库详细信息form" type="1" script="phis.application.sup.script.MaterialsOutDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_FORM</p>
				<p name="colCount">3</p>
			</properties>		
		</module>
		<module id="SUP03010102" name="物资出库详细信息list" type="1"  script="phis.application.sup.script.MaterialsOutDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />	
		</module>
		<module id="SUP0301010201" name="资产库存选择" type="1" script="phis.application.sup.script.FixedAssetsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZCZB_YR</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP04" name="出库管理(二级)" 
			script="phis.application.sup.script.SecondaryMaterialsOutModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_EJKF</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0401</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0402</p>
				<p name="serviceId">secondaryMaterialsOutService</p>
			</properties>
		</module>
		<module id="SUP0401" name="未确认出库单" type="1" script="phis.application.sup.script.SecondaryMaterialsOutUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_EJKF</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP040101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">phis.secondaryMaterialsOutService</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<!-- <action id="reject" name="冲红" level="two" iconCls="common_cancel"/>-->
			<action id="print" name="打印" level="two" iconCls="print"/>
		</module>
		<module id="SUP0402" name="确认出库单" type="1"
			script="phis.application.sup.script.SecondaryMaterialsOutList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_EJKF</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP040101</p>
				<p name="serviceId">phis.secondaryMaterialsOutService</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" level="two" iconCls="read"></action>
			<action id="print" name="打印" level="two" iconCls="print"/>
		</module>
		<module id="SUP040101" name="出库管理" type="1" script="phis.application.sup.script.SecondaryMaterialsOutDetailModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP04010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP04010102</p>
				<p name="serviceId">secondaryMaterialsOutService</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>    
			<action id="create" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="common_cancel"/>
			<action id="commit" name="记账"/>
			<action id="reject" name="退回" iconCls="common_cancel"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP04010101" name="出库管理(form)" type="1" script="phis.application.sup.script.SecondaryMaterialsOutDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_FORM_EJKF</p>
			</properties>
		</module>
		<module id="SUP04010102" name="出库管理(Editor)" type="1" script="phis.application.sup.script.SecondaryMaterialsOutDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02_EJKF</p>
				<p name="serviceId">phis.secondaryMaterialsOutService</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="SUP42" name="入库管理(二级)" script="phis.application.sup.script.SecondaryStorageOfMaterialsModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01_EJKF</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP4201</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP4202</p>
				<p name="serviceId">secondaryStorageOfMaterialsService</p>
			</properties>
		</module>
		<module id="SUP4201" name="未确认入库单" type="1" script="phis.application.sup.script.SecondaryStorageOfMaterialsUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01_EJKF</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP420101</p>
				<p name="closeAction">true</p>
				<!--<p name="refStoreOfMaterialsPrint">phis.application.sup.SUP/SUP/SUP60</p> -->
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<!--
					<action id="examine" name="审核" level="two" iconCls="update"/>
					<action id="commit" name="记账" level="two"/> 
				<action id="reject" name="冲红" level="two" iconCls="common_cancel"/>
				<action id="print" level="two" name="打印"/>-->
		</module>
		<module id="SUP4202" name="确认入库单" type="1"
			script="phis.application.sup.script.SecondaryStorageOfMaterialsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01_EJKF</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP420101</p>
				<p name="serviceId">secondaryStorageOfMaterialsService</p>
				<!--<p name="refStoreOfMaterialsPrint">SUP60</p> -->
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" level="two" name="查看" iconCls="read"></action>
			<!--<action id="print" level="two" name="打印"/>-->
		</module>
		<module id="SUP420101" name="入库管理" type="1" script="phis.application.sup.script.SecondaryStorageOfMaterialsDetailModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP42010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP42010102</p>
				<p name="serviceId">secondaryStorageOfMaterialsService</p>
				<!--<p name="refStoreOfMaterialsPrint">SUP60</p> -->
			</properties>    
			<action id="create" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="common_cancel"/>
			<action id="commit" name="记账"/>
			<action id="reject" name="退回" iconCls="common_cancel"/>
			<!--<action id="print" name="打印"/>-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP42010101" name="入库管理(form)" type="1" script="phis.application.sup.script.SecondaryStorageOfMaterialsDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK01_FORM_EJKF</p>
			</properties>
		</module>
		<module id="SUP42010102" name="入库管理(Editor)" type="1" script="phis.application.sup.script.SecondaryStorageOfMaterialsDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_RK02</p>
				<p name="serviceId">secondaryStorageOfMaterialsService</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="SUP02" name="入库确认" script="phis.application.sup.script.StorageConfirmeHZModule">
			<properties>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0201</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0202</p>
				<p name="gridDDGroup">true</p>
			</properties>
		</module>
		<module id="SUP0201" name="未确认入库单" type="1" script="phis.application.sup.script.StorageUNConfirmeList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_RKQR_WRK</p>
				<p name="gridDDGroup">true</p>
				<p name="openRef">phis.application.sup.SUP/SUP/SUP020101</p>
			</properties>
			<action id="refresh" name="刷新" ></action>
		</module>
		<module id="SUP0202" name="确认入库单" type="1" script="phis.application.sup.script.StorageConfirmeList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_RKQR</p>
				<p name="openRef">phis.application.sup.SUP/SUP/SUP020101</p>
			</properties>
			<action id="refresh" name="刷新" ></action>
			<action id="look" name="查看"  iconCls="read"></action>
		</module>
		<module id="SUP020101" name="入库确认单" type="1" script="phis.application.sup.script.StorageConfirmeModule">
			<properties>		
				<p name="refForm">phis.application.sup.SUP/SUP/SUP02010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP02010102</p>
			</properties>		
			<action id="commit" name="确认"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP02010101" name="入库管理(form)" type="1" script="phis.application.sup.script.StorageConfirmeForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_FORM_EJ</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="SUP02010102" name="入库管理(List)" type="1" script="phis.application.sup.script.StorageConfirmeDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02_EJCK</p>
			</properties>
		</module>
		<module id="SUP09" name="申领登记" script="phis.application.sup.script.ApplyRegisterList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_SLXX</p>
				<p name="mutiSelect">true</p>  
				<p name="removeByFiled">JLXH</p>
				<p name="refAdd">phis.application.sup.SUP/SUP/SUP0901</p>
			</properties>
			<action id="refresh" name="刷新"/>
			<action id="add" name="新增"/>
			<action id="open" name="打开" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="提交" />
		</module>
		<module id="SUP0901" name="申领登记" type="1" script="phis.application.sup.script.ApplyRegisterForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_SLXX_FORM</p>    
			</properties>     
			<action id="add" name="新增"/>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module> 
		<module id="SUP13" name="重置管理" script="phis.application.sup.script.ResetBusinessModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CZ01</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP1301</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP1302</p>
			</properties>
		</module>
		<module id="SUP1301" name="未记账重置单" type="1" script="phis.application.sup.script.ResetBusinessUList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CZ01</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP130101</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh"/>
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
		</module>
		<module id="SUP1302" name="记账重置单" type="1" script="phis.application.sup.script.ResetBusinessList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CZ01</p>
				<p name="readRef">phis.application.sup.SUP/SUP/SUP130101</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh"/>
			<action id="look" name="查看" level="two" iconCls="read"></action>
		</module>
		<module id="SUP130101" name="物资重置详细信息"  type="1" script="phis.application.sup.script.ResetBusinessDetailModule">
			<properties>			
				<p name="refForm">phis.application.sup.SUP/SUP/SUP13010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP13010102</p>
				<p name="refImport">phis.application.sup.SUP/SUP/SUP13010103</p>
			</properties>		
			<action id="create" name="新增"/> 
			<action id="import" name="引入" iconCls="ransferred_all"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP13010101" name="物资重置详细信息form" type="1" script="phis.application.sup.script.ResetBusinessDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CZ01_FORM</p>
				<p name="colCount">3</p>
			</properties>		
		</module>
		<module id="SUP13010102" name="物资重置详细信息list" type="1"  script="phis.application.sup.script.ResetBusinessDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CZ02</p>
			</properties>
			<action id="remove" name="删除" />	
		</module>
		<module id="SUP13010103" name="重置物资库存" type="1" script="phis.application.sup.script.ResetBusinessDetailCZList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZCZB_CZ</p>
			</properties>
			<action id="commit" name="确认" />
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP16" name="采购计划" script="phis.application.sup.script.ProcurementPlanList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JH01</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP1601</p>
				<p name="serviceId">procurementPlanService</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" />
			<action id="upd" name="修改"  iconCls="update"/>
			<action id="remove" name="删除" />
		</module>
		<module id="SUP1601" name="采购计划" type="1" script="phis.application.sup.script.ProcurementPlanDetailModule">
			<properties>
				<p name="refForm">phis.application.sup.SUP/SUP/SUP160101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP160102</p>
				<p name="refEditorList">phis.application.sup.SUP/SUP/SUP160103</p>
				<p name="serviceId">procurementPlanService</p>
			</properties>
			<action id="create" name="新增"/>  
			<action id="import" name="引入"  iconCls="ransferred_all"/>
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP160101" name="采购计划form" type="1" script="phis.application.sup.script.ProcurementPlanDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JH01_FORM</p>
				<p name="serviceId">procurementPlanService</p>
			</properties>
		</module>
		<module id="SUP160102" name="采购计划editorList" type="1" script="phis.application.sup.script.ProcurementPlanDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JH02</p>
				<p name="serviceId">procurementPlanService</p>
			</properties>
			<action id="create" name="新增"/> 
			<action id="remove" name="删除"/>
		</module>
		<module id="SUP160103" name="计划生成方法" type="1" script="phis.application.sup.script.ProcurementPlanSelectForm">
			<properties>
				<p name="serviceId">procurementPlanService</p>
			</properties>
			<action id="commit" name="确认" />
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP06" name="转科管理" script="phis.application.sup.script.TransferManagementModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZK01</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0601</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0602</p>
				<p name="serviceId">transferManagementService</p>
			</properties>
		</module>
		<module id="SUP0601" name="未确认转科单" type="1" script="phis.application.sup.script.TransferManagementUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZK01</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP060101</p>
				<p name="serviceId">transferManagementService</p>
				<p name="refStoreOfTransferPrint">phis.application.sup.SUP/SUP/SUP62</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<action id="print" name="打印"/>
		</module>
		<module id="SUP0602" name="确认转科管理" type="1" script="phis.application.sup.script.TransferManagementList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZK01</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP060101</p>
				<p name="refStoreOfTransferPrint">phis.application.sup.SUP/SUP/SUP62</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
			<action id="print" name="打印"/>
		</module>
		<module id="SUP060101" name="转科管理" type="1" script="phis.application.sup.script.TransferManagementDetailModule">
			<properties>
				<p name="refForm">phis.application.sup.SUP/SUP/SUP06010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP06010102</p>
				<p name="refEditorList">phis.application.sup.SUP/SUP/SUP06010103</p>
				<p name="serviceId">transferManagementService</p>
				<p name="refStoreOfTransferPrint">phis.application.sup.SUP/SUP/SUP62</p>
			</properties>
			<!--<action id="create" name="新增"/>--> 
			<action id="import" name="引入" iconCls="ransferred_all"/>
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="common_cancel"/>
			<action id="commit" name="记账"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP06010101" name="转科管理form" type="1" script="phis.application.sup.script.TransferManagementDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZK01_FORM</p>
				<p name="serviceId">transferManagementService</p>
			</properties>
		</module>
		<module id="SUP06010102" name="转科管理editorList" type="1" script="phis.application.sup.script.TransferManagementDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZK02</p>
				<p name="serviceId">phis.transferManagementService</p>
			</properties>
			<action id="remove" name="删除" />
		</module>
		<module id="SUP06010103" name="物资库存" type="1" script="phis.application.sup.script.TransferManagementKCList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZK02_KC</p>
				<p name="serviceId">phis.transferManagementService</p>
			</properties>
			<action id="commit" name="确认" />
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP17" name="消耗明细" script="phis.application.sup.script.ConsumptionList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_XHMX</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP1703</p>
				<p name="serviceId">consumptionService</p>
			</properties>
			<action id="createDoc" name="生成" iconCls="update"/>
			<!--<action id="cancellation" name="作废" iconCls="common_cancel"/>-->
		</module>
		<module id="SUP1703" type="1" name="消耗明细出库管理" 
			script="phis.application.sup.script.ConsumptionModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_EJKF</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP1701</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP1702</p>
				<p name="serviceId">consumptionService</p>
			</properties>
		</module>
		<module id="SUP1701" name="未确认出库单" type="1" script="phis.application.sup.script.ConsumptionMaterialsOutUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_EJKF</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP170101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">consumptionService</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>
			<action id="refreshWin" name="查询" level="two" iconCls="arrow_refresh" />
			<action id="upd" name="打开" level="two" iconCls="update"/>
			<action id="commit" name="确认" level="two" />
			<action id="remove" name="撤销" level="two"/>
			<action id="print" name="打印" level="two" iconCls="print"/>
		</module>
		<module id="SUP1702" name="确认出库单" type="1"
			script="phis.application.sup.script.ConsumptionMaterialsOutList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_EJKF</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP170101</p>
				<p name="serviceId">consumptionService</p>
				<p name="refNoStoreListPrint">SUP030104</p>
			</properties>
			<action id="refreshWin" name="查询" level="two" iconCls="arrow_refresh" />
			<action id="look" name="查看" level="two" iconCls="read"></action>
			<action id="print" name="打印" level="two" iconCls="print"/>
		</module>
		<module id="SUP170101" name="出库管理(module)" type="1" script="phis.application.sup.script.SecondaryMaterialsOutDetailModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP17010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP17010102</p>
				<p name="serviceId">secondaryMaterialsOutService</p>
				<p name="refNoStoreListPrint">phis.application.sup.SUP/SUP/SUP030104</p>
			</properties>    
			
			<action id="save" name="保存"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP17010101" name="出库管理(form)" type="1" script="phis.application.sup.script.SecondaryMaterialsOutDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_FORM_XHMX</p>
				<p name="serviceId">secondaryMaterialsOutService</p>
			</properties>
		</module>
		<module id="SUP17010102" name="出库管理(Editor)" type="1" script="phis.application.sup.script.SecondaryMaterialsOutDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02_EJKF</p>
				<p name="serviceId">secondaryMaterialsOutService</p>
			</properties>
		</module>
		
		<module id="SUP34" name="报损管理"
			script="phis.application.sup.script.FaultyManagerModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_BS01_FORM_CON</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP3401</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP3402</p>
				<p name="serviceId">faultyService</p>
			</properties>
		</module>
		<module id="SUP3401" name="未确认报损单" type="1" script="phis.application.sup.script.FaultyUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_BS01_FORM_CON</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP340101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">faultyService</p>
				<p name="refFaultyManagerPrint">phis.application.sup.SUP/SUP/SUP63</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<action id="print" name="打印"/>
		</module>
		<module id="SUP3402" name="已确认报损单" type="1"
			script="phis.application.sup.script.FaultyDeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_BS01_FORM_CON</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP340101</p>
				<p name="serviceId">faultyService</p>
				<p name="refFaultyManagerPrint">phis.application.sup.SUP/SUP/SUP63</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
			<action id="print" name="打印"/>
		</module>
		<module id="SUP340101" name="报损明细确认单" type="1" script="phis.application.sup.script.FaultyDetailModule">
			<properties>		
				<p name="refForm">phis.application.sup.SUP/SUP/SUP34010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP34010102</p>
				<p name="comeInList">phis.application.sup.SUP/SUP/SUP34010103</p>
				<p name="serviceId">faultyService</p>
				<p name="refFaultyManagerPrint">phis.application.sup.SUP/SUP/SUP63</p>
			</properties>		
			<action id="add" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="common_cancel"/>
			<action id="commit" name="记账"/>
			<action id="comeIn" name="引入" iconCls="ransferred_all"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP34010101" name="报损单(form)" type="1" script="phis.application.sup.script.FaultyConForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_BS01_FORM_CON</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="SUP34010102" name="报损明细(List)" type="1" script="phis.application.sup.script.FaultyDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_BS02</p>
			</properties>
			<action id="remove" name="删除" />
		</module>
		<module id="SUP34010103" name="科室引入" type="1" script="phis.application.sup.script.KSBSComeInList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_BS02_COMEIN</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP05" name="申领管理" script="phis.application.sup.script.ApplyManagementModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_SLGL</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0501</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0502</p>
				<p name="serviceId">applyManagementService</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP050101</p>
			</properties>   
		</module>
		<module id="SUP0501" name="未确认申领单" type="1" script="phis.application.sup.script.ApplyManagementUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_SLGL</p> 
				<p name="addRef">phis.application.sup.SUP/SUP/SUP050101</p>
				<p name="serviceId">applyManagementService</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
		</module>
		<module id="SUP0502" name="确认申领单" type="1" script="phis.application.sup.script.ApplyManagementList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_SLGL</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP050101</p>
				<p name="serviceId">applyManagementService</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="SUP050101" name="申领管理" type="1" script="phis.application.sup.script.ApplyManagementDetailModule">
			<properties>
				<p name="refForm">phis.application.sup.SUP/SUP/SUP05010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP05010102</p>
				<p name="refImportModule">phis.application.sup.SUP/SUP/SUP05010103</p>
				<p name="serviceId">applyManagementService</p>
			</properties>
			<action id="create" name="新增"/> 
			<action id="import" name="引入" iconCls="ransferred_all"/>
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="cancelVerify" name="弃审" iconCls="common_cancel"/>
			<action id="commit" name="记账"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP05010101" name="申领管理form" type="1" script="phis.application.sup.script.ApplyManagementDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_SLGL_FORM</p>
				<p name="serviceId">applyManagementService</p>
			</properties>
		</module>
		<module id="SUP05010102" name="申领管理editorList" type="1" script="phis.application.sup.script.ApplyManagementDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02_SLGL</p>
				<p name="serviceId">applyManagementService</p> 
			</properties>
			<action id="remove" name="删除" />
		</module>
		<module id="SUP05010103" name="引入信息" type="1" script="phis.application.sup.script.ApplyManagementImportModule">
			<properties>
				<p name="refComboNameList">phis.application.sup.SUP/SUP/SUP0501010301</p>
				<p name="refComboNameDetailList">phis.application.sup.SUP/SUP/SUP0501010302</p> 
				<p name="serviceId">applyManagementService</p>
			</properties>
		</module>
		<module id="SUP05010104" name="资产库存选择" type="1" script="phis.application.sup.script.FixedAssetsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZCZB_SL</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP0501010301" name="科室列表" type="1" script="phis.application.sup.script.ApplyManagementImportList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_SLXX_KS</p>
				<p name="serviceId">applyManagementService</p>
			</properties>
		</module>
		<module id="SUP0501010302" name="详细列表" type="1" script="phis.application.sup.script.ApplyManagementImportDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_SLXX_DETAIL</p>
				<p name="serviceId">applyManagementService</p>
			</properties>
			<action id="commit" name="确认" />
			<action id="reject" name="退回" iconCls="common_cancel"/>
		</module>
		<module id="SUP07" name="调拨登记"
			script="phis.application.sup.script.ForRegistrationListModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DB</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0701</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0702</p>
			</properties>
		</module>
		<module id="SUP0701" name="未确认调拨登记" type="1" script="phis.application.sup.script.ForRegistrationUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DB</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP070101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">materialsOutService</p>
				<p name="removeMaterialsOutActionId">removeMaterialsOut</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
		</module>
		<module id="SUP0702" name="确认调拨登记" type="1"
			script="phis.application.sup.script.ForRegistrationList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DB</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP070101</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="SUP070101" name="调拨登记" type="1" script="phis.application.sup.script.ForRegistrationModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP07010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP07010102</p>
				<p name="serviceId">materialsOutService</p>
				<p name="materialsOutDJZTActionId">getMaterialsOutDJZT</p>
				<p name="saveMaterialsOutActionId">saveMaterialsOut</p>
			</properties>    
			<action id="create" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="提交" iconCls="update"/>
			<action id="commit" name="确认"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP07010101" name="调拨登记(form)" type="1" script="phis.application.sup.script.ForRegistrationForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DB_FORM</p>
			</properties>
		</module>
		<module id="SUP07010102" name="调拨登记(Editor)" type="1" script="phis.application.sup.script.ForRegistrationEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02_DB</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="SUP08" name="调拨管理"
			script="phis.application.sup.script.AllocationManagementListModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DBGL</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP0801</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP0802</p>
			</properties>
		</module>
		<module id="SUP0801" name="未确记账拨管理" type="1" script="phis.application.sup.script.AllocationManagementUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DBGL</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP080101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">materialsOutService</p>
				<p name="removeAllocationManagementActionId">removeAllocationManagement</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
		</module>
		<module id="SUP0802" name="已记账调拨管理" type="1"
			script="phis.application.sup.script.AllocationManagementList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DBGL</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP080101</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="SUP080101" name="调拨管理" type="1" script="phis.application.sup.script.AllocationManagementModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP08010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP08010102</p>
				<p name="serviceId">materialsOutService</p>
				<p name="saveMaterialsOutActionId">saveMaterialsOut</p>
				<p name="materialsOutDJZTActionId">getMaterialsOutDJZT</p>
				<p name="materialsOutVerifyActionId">saveMaterialsOutVerify</p>
				<p name="materialsOutNoVerifyActionId">saveMaterialsOutNoVerify</p>
			</properties>    
			<action id="create" name="新增"/> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="reject" name="退回" iconCls="common_cancel"/>
			<!--<action id="print" name="打印"/>-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP08010101" name="调拨管理(form)" type="1" script="phis.application.sup.script.AllocationManagementForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK01_DBGL_FORM</p>
			</properties>
		</module>
		<module id="SUP08010102" name="调拨管理(Editor)" type="1" script="phis.application.sup.script.AllocationManagementEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_CK02_DBGL</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="SUP12" name="盘点业务(二级)"
			script="phis.application.sup.script.InventoryEjListModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP1201</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP1202</p>
			</properties>
		</module>
		<module id="SUP1201" name="未记账盘点业务" type="1" script="phis.application.sup.script.InventoryEjUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01</p>
				<p name="addRefkc">phis.application.sup.SUP/SUP/SUP120101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">inventoryService</p>
				<p name="removeInventoryActionId">removeInventory</p>
				<p name="refInventoryEjPrint">phis.application.sup.SUP/SUP/SUP67</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<action id="print" name="打印"/>
		</module>
		<module id="SUP1202" name="已记账盘点业务" type="1"
			script="phis.application.sup.script.InventoryEjList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP120101</p>
				<p name="refInventoryEjPrint">phis.application.sup.SUP/SUP/SUP67</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
			<action id="print" name="打印"/>
		</module>
		<module id="SUP120101" name="盘点业务" type="1" script="phis.application.sup.script.InventoryEjModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP12010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP12010102</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryActionId">saveInventory</p>
				<p name="inventoryDJZTActionId">getInventoryDJZT</p>
				<p name="inventoryVerifyActionId">saveInventoryVerify</p>
				<p name="inventoryNoVerifyActionId">saveInventoryNoVerify</p>
				<p name="inventoryEjCommitActionId">saveInventoryEjCommit</p>
				<p name="refInventoryEjPrint">phis.application.sup.SUP/SUP/SUP67</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP12010101" name="盘点业务(form)" type="1" script="phis.application.sup.script.InventoryEjForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01_FORM</p>
			</properties>
		</module>
		<module id="SUP12010102" name="盘点业务(Editor)" type="1" script="phis.application.sup.script.InventoryEjEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD02_KC</p>
			</properties>
		</module>
		<module id="SUP15" name="盘点录入(二级)"
			script="phis.application.sup.script.InventoryInEjListModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP1501</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP1502</p>
			</properties>
		</module>
		<module id="SUP1501" name="未提交盘点录入" type="1" script="phis.application.sup.script.InventoryInEjUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD_EJKF</p>
				<p name="addRefkc">phis.application.sup.SUP/SUP/SUP150101</p>
				<p name="closeAction">true</p>
				<p name="serviceId">inventoryService</p>
				<p name="removeInventoryInActionId">removeInventoryIn</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
		</module>
		<module id="SUP1502" name="已提交盘点录入" type="1"
			script="phis.application.sup.script.InventoryInEjList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD_EJKF</p>
				<p name="gridDDGroup">true</p>
				<p name="addRef">phis.application.sup.SUP/SUP/SUP150101</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="SUP150101" name="盘点录入" type="1" script="phis.application.sup.script.InventoryInEjModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP15010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP15010102</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryInActionId">saveInventoryIn</p>
				<p name="inventoryInDJZTActionId">getInventoryInDJZT</p>
				<p name="inventoryInSubitActionId">saveSubitVerify</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="subit" name="提交" iconCls="update"/>
			<action id="callIn" name="引入" iconCls="ransferred_all" />
			<!--<action id="print" name="打印"/>-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP15010101" name="盘点录入(form)" type="1" script="phis.application.sup.script.InventoryInEjForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD_FORM</p>
			</properties>
		</module>
		<module id="SUP15010102" name="盘点录入(Editor)" type="1" script="phis.application.sup.script.InventoryInEjEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRMX_KC</p>
			</properties>
		</module>
		<module id="SUP15010103" name="盘点录入选择物资信息" type="1" script="phis.application.sup.script.InventoryWZZDList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WZZD_WZ</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP45" name="设备折旧管理" script="phis.application.sup.script.DepreciatedManagementList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_YJZC_ZCZB_WZZD</p>
			</properties>
			<action id="refreshWin" name="刷新" iconCls="arrow_refresh" />
			<action id="commit" name="折旧" />
			<action id="canclecommit" name="取消折旧" iconCls="date_edit"/>
		</module>
		<module id="SUP46" name="计量设备管理" script="phis.application.sup.script.EquipmentWeighingManagementModule">
			<action id="measuringInstruments" viewType="list" name="计量器具列表" ref="phis.application.sup.SUP/SUP/SUP4601" />
			<action id="measuringInstrumentsinfo" viewType="list" name="计量器具计量信息" ref="phis.application.sup.SUP/SUP/SUP4602" />
		</module>
		<module id="SUP4601" name="计量器具列表"  type="1"
			script="phis.application.sup.script.MeasuringInstrumentsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX_LIST</p>
				<p name="refjlxxform">phis.application.sup.SUP/SUP/SUP460101</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="add" name="登记" />
		</module>
		<module id="SUP460101" name="计量器具表单"  type="1"
			script="phis.application.sup.script.MeasuringInstrumentsform">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX_FORM</p>
			</properties>
			<action id="save" name="保存"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP4602" name="计量器具计量信息"  type="1"
			script="phis.application.sup.script.MeasuringInstrumentsInfoList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX_INFO</p>
				<p name="refjlxxinfoform">phis.application.sup.SUP/SUP/SUP460201</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="upd" name="修改" iconCls="update"/>
			<action id="remove" name="作废" iconCls="writeoff"/>
		</module>
		<module id="SUP460201" name="计量信息表单"  type="1"
			script="phis.application.sup.script.MeasuringInstrumentsInfoform">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX_FORM_UPD</p>
			</properties>
			<action id="save" name="保存"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP47" name="计量设备检定" script="phis.application.sup.script.MeteringEquipmentTestModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP4701</p>
				<p name="refRList">phis.application.sup.SUP/SUP/SUP4702</p>
				<p name="refDetailModule">phis.application.sup.SUP/SUP/SUP4703</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="jd" name="检定" iconCls="ransferred_all" />
		</module>
		<module id="SUP4701" name="计量设备检定左边list" type="1"  script="phis.application.sup.script.MeteringEquipmentTestLeftList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX</p>
				<p name="serviceId">phis.meteringEquipmentTestService</p>
				<p name="queryAction">queryLeftList</p>
			</properties>
		</module>
		<module id="SUP4702" name="计量设备检定右边list" type="1" script="phis.application.sup.script.MeteringEquipmentTestRightList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JDXX</p>
			</properties>
		</module>
		<module id="SUP4703" name="计量设备检定详细内容" type="1" script="phis.application.sup.script.MeteringEquipmentTestDetailModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JDXX</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP470302</p>
				<p name="refForm">phis.application.sup.SUP/SUP/SUP470301</p>
				<p name="refYrList">phis.application.sup.SUP/SUP/SUP470303</p>
				<p name="serviceId">meteringEquipmentTestService</p>
				<p name="queryAction">saveMeteringEquipmentTest</p>
				<p name="saveActionId">saveMeteringEquipmentTest</p>
			</properties>
			<action id="yr" name="引入" iconCls="commit"/>
			<action id="jd" name="检定" iconCls="ransferred_all" />
			<action id="close" name="退出" />
		</module>
		<module id="SUP470301" name="计量设备检定详细form" type="1" script="phis.application.sup.script.MeteringEquipmentTestDetailForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JDXX_DETAILFORM</p>
			</properties>
		</module>
		<module id="SUP470302" name="计量设备检定详细list" type="1" script="phis.application.sup.script.MeteringEquipmentTestDetailList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX_DETAILLIST</p>
				<p name="serviceId">phis.meteringEquipmentTestService</p>
				<p name="queryAction">queryLeftList</p>
			</properties>
		</module>
		<module id="SUP470303" name="计量设备检定引入列表" type="1" script="phis.application.sup.script.MeteringEquipmentTestList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLXX_JD</p>
				<p name="serviceId">phis.meteringEquipmentTestService</p>
				<p name="queryAction">queryLeftList</p>
			</properties>
			<action id="qd" name="检定"  iconCls="ransferred_all"/>
			<action id="close" name="退出" />
		</module>
		<module id="SUP10" name="盘点业务"
			script="phis.application.sup.script.InventoryListModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP1001</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP1002</p>
			</properties>
			<action id="kcManagement" name="库存管理" value="1"/>
			<action id="ksManagement" name="科室管理" value="2"/>
			<action id="tzManagement" name="台帐管理" value="3"/>
		</module>
		<module id="SUP1001" name="未记账盘点业务" type="1" script="phis.application.sup.script.InventoryUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01</p>
				<p name="addRefkc">phis.application.sup.SUP/SUP/SUP100101</p>
				<p name="addRefks">phis.application.sup.SUP/SUP/SUP100102</p>
				<p name="addReftz">phis.application.sup.SUP/SUP/SUP100103</p>
				<p name="closeAction">true</p>
				<p name="serviceId">inventoryService</p>
				<p name="removeInventoryActionId">removeInventory</p>
				<p name="refInventoryPrint">phis.application.sup.SUP/SUP/SUP64</p>
				<p name="refInventoryKsPrint">phis.application.sup.SUP/SUP/SUP65</p>
				<p name="refInventoryTzPrint">phis.application.sup.SUP/SUP/SUP66</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
			<action id="print" name="打印" />
		</module>
		<module id="SUP1002" name="已记账盘点业务" type="1"
			script="phis.application.sup.script.InventoryList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01</p>
				<p name="gridDDGroup">true</p>
				<p name="addRefkc">phis.application.sup.SUP/SUP/SUP100101</p>
				<p name="addRefks">phis.application.sup.SUP/SUP/SUP100102</p>
				<p name="addReftz">phis.application.sup.SUP/SUP/SUP100103</p>
				<p name="refInventoryPrint">phis.application.sup.SUP/SUP/SUP64</p>
				<p name="refInventoryKsPrint">phis.application.sup.SUP/SUP/SUP65</p>
				<p name="refInventoryTzPrint">phis.application.sup.SUP/SUP/SUP66</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
			<action id="print" name="打印" />
		</module>
		<module id="SUP100101" name="盘点业务按库存" type="1" script="phis.application.sup.script.InventoryModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP10010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP10010102</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryActionId">saveInventory</p>
				<p name="inventoryDJZTActionId">getInventoryDJZT</p>
				<p name="inventoryDJSHActionId">getInventoryDJSH</p>
				<p name="inventoryVerifyActionId">saveInventoryVerify</p>
				<p name="inventoryNoVerifyActionId">saveInventoryNoVerify</p>
				<p name="inventoryCommitActionId">saveInventoryCommit</p>
				<p name="refInventoryPrint">phis.application.sup.SUP/SUP/SUP64</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP10010101" name="盘点业务(form)" type="1" script="phis.application.sup.script.InventoryForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01_FORM</p>
			</properties>
		</module>
		<module id="SUP10010102" name="盘点业务(Editor)" type="1" script="phis.application.sup.script.InventoryEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD02_KC</p>
			</properties>
		</module>
		<module id="SUP100102" name="盘点业务按科室" type="1" script="phis.application.sup.script.InventoryKSModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP10010201</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP10010202</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryActionId">saveInventory</p>
				<p name="inventoryDJZTActionId">getInventoryDJZT</p>
				<p name="inventoryDJSHActionId">getInventoryDJSH</p>
				<p name="inventoryDJSHKSActionId">getInventoryDJSHKS</p>
				<p name="inventoryVerifyActionId">saveInventoryVerify</p>
				<p name="inventoryNoVerifyActionId">saveInventoryNoVerify</p>
				<p name="inventoryCommitActionId">saveInventoryCommit</p>
				<p name="refInventoryKsPrint">phis.application.sup.SUP/SUP/SUP65</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="callIn" name="引入" iconCls="ransferred_all"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP10010201" name="盘点业务(form)" type="1" script="phis.application.sup.script.InventoryForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01_KS_FORM</p>
			</properties>
		</module>
		<module id="SUP10010202" name="盘点业务按科室(Editor)" type="1" script="phis.application.sup.script.InventoryKSEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD02_KS</p>
			</properties>
		</module>
		<module id="SUP10010203" name="盘点业务按科室选择科室" type="1" script="phis.application.sup.script.InventoryKSList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_KSZC_KS</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP100103" name="盘点业务按台帐" type="1" script="phis.application.sup.script.InventoryTZModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP10010301</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP10010302</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryActionId">saveInventory</p>
				<p name="inventoryDJZTActionId">getInventoryDJZT</p>
				<p name="inventoryDJSHActionId">getInventoryDJSH</p>
				<p name="inventoryDJSHKSActionId">getInventoryDJSHKS</p>
				<p name="inventoryVerifyActionId">saveInventoryVerify</p>
				<p name="inventoryNoVerifyActionId">saveInventoryNoVerify</p>
				<p name="inventoryCommitActionId">saveInventoryCommit</p>
				<p name="refInventoryTzPrint">phis.application.sup.SUP/SUP/SUP66</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="verify" name="审核" iconCls="update"/>
			<action id="commit" name="记账"/>
			<action id="callIn" name="引入" iconCls="ransferred_all"/>
			<action id="print" name="打印"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP10010301" name="盘点业务(form)" type="1" script="phis.application.sup.script.InventoryForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD01_TZ_FORM</p>
			</properties>
		</module>
		<module id="SUP10010302" name="盘点业务按台帐(Editor)" type="1" script="phis.application.sup.script.InventoryTZEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_PD02_TZ</p>
			</properties>
		</module>
		<module id="SUP10010303" name="盘点业务按台帐选择科室" type="1" script="phis.application.sup.script.InventoryTZList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_KSZC_KS</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP11" name="盘点录入" script="phis.application.sup.script.InventoryInListModule">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD</p>
				<p name="refUList">phis.application.sup.SUP/SUP/SUP1101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP1102</p>
			</properties>
			<action id="kcInManagement" name="库存管理" value="1"/>
			<action id="ksInManagement" name="科室管理" value="2"/>
			<action id="tzInManagement" name="台帐管理" value="3"/>
		</module>
		<module id="SUP1101" name="未提交盘点录入" type="1" script="phis.application.sup.script.InventoryInUndeterminedList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD</p>
				<p name="addRefkc">phis.application.sup.SUP/SUP/SUP110101</p>
				<p name="addRefks">phis.application.sup.SUP/SUP/SUP110102</p>
				<p name="addReftz">phis.application.sup.SUP/SUP/SUP110103</p>
				<p name="closeAction">true</p>
				<p name="serviceId">inventoryService</p>
				<p name="removeInventoryInActionId">removeInventoryIn</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="add" name="新增" level="two"/>
			<action id="upd" name="修改" level="two" iconCls="update"/>
			<action id="remove" name="删除" level="two"/>
		</module>
		<module id="SUP1102" name="已提交盘点录入" type="1"
			script="phis.application.sup.script.InventoryInList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD</p>
				<p name="gridDDGroup">true</p>
				<p name="addRefkc">phis.application.sup.SUP/SUP/SUP110101</p>
				<p name="addRefks">phis.application.sup.SUP/SUP/SUP110102</p>
				<p name="addReftz">phis.application.sup.SUP/SUP/SUP110103</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="SUP110101" name="盘点录入按库存" type="1" script="phis.application.sup.script.InventoryInModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP11010101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP11010102</p>
				<p name="refMode">phis.application.sup.SUP/SUP/SUP11010103</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryInActionId">saveInventoryIn</p>
				<p name="inventoryInDJZTActionId">getInventoryInDJZT</p>
				<p name="inventoryInSubitActionId">saveSubitVerify</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="subit" name="提交" iconCls="update"/>
			<action id="callIn" name="引入" iconCls="ransferred_all"/>
			<!--<action id="print" name="打印"/>-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP11010101" name="盘点录入(form)" type="1" script="phis.application.sup.script.InventoryInForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD_FORM</p>
			</properties>
		</module>
		<module id="SUP11010102" name="盘点录入(Editor)" type="1" script="phis.application.sup.script.InventoryInEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRMX_KC</p>
			</properties>
		</module>
		<module id="SUP11010103" name="盘点录入选择物资信息" type="1" script="phis.application.sup.script.InventoryWZZDList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WZZD_WZ</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP110102" name="盘点录入按科室" type="1" script="phis.application.sup.script.InventoryInKSModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP11010201</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP11010202</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryInActionId">saveInventoryIn</p>
				<p name="inventoryInDJZTActionId">getInventoryInDJZT</p>
				<p name="inventoryInSubitActionId">saveSubitVerify</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="subit" name="提交" iconCls="update"/>
			<action id="callIn" name="引入" iconCls="ransferred_all"/>
			<!--<action id="print" name="打印"/>-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP11010201" name="盘点录入(form)" type="1" script="phis.application.sup.script.InventoryInForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD_KS_FORM</p>
			</properties>
		</module>
		<module id="SUP11010202" name="盘点录入按科室(Editor)" type="1" script="phis.application.sup.script.InventoryInKSEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRMX_KS</p>
			</properties>
		</module>
		<module id="SUP11010203" name="盘点录入按科室选择科室" type="1" script="phis.application.sup.script.InventoryInKSList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_KSZC_KS</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP110103" name="盘点录入按台帐" type="1" script="phis.application.sup.script.InventoryInTZModule">
			<properties>          
				<p name="refForm">phis.application.sup.SUP/SUP/SUP11010301</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP11010302</p>
				<p name="serviceId">inventoryService</p>
				<p name="saveInventoryInActionId">saveInventoryIn</p>
				<p name="inventoryInDJZTActionId">getInventoryInDJZT</p>
				<p name="inventoryInSubitActionId">saveSubitVerify</p>
			</properties>    
			<!--<action id="create" name="新增"/>--> 
			<action id="save" name="保存"/>
			<action id="subit" name="提交" iconCls="update"/>
			<action id="callIn" name="引入" iconCls="ransferred_all"/>
			<!--<action id="print" name="打印"/>-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP11010301" name="盘点录入(form)" type="1" script="phis.application.sup.script.InventoryInForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRPD_TZ_FORM</p>
			</properties>
		</module>
		<module id="SUP11010302" name="盘点录入按台帐(Editor)" type="1" script="phis.application.sup.script.InventoryInTZEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_LRMX_TZ</p>
			</properties>
		</module>
		<module id="SUP11010303" name="盘点录入按台帐选择科室" type="1" script="phis.application.sup.script.InventoryInTZList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_KSZC_KS</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="确认" iconCls="ransferred_all"/>
			<action id="exit" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP14" name="库房月结"  script="phis.application.sup.script.TreasuryMonthly">
			<properties>
				<p name="refForm">phis.application.sup.SUP/SUP/SUP1401</p>
				<p name="entryName">phis.application.sup.schemas.WL_YJJL</p>
			</properties>
			<action id="monthly" name="月结"  iconCls="date_edit"/>
			<action id="cancelMonthly" name="取消月结"  iconCls="month_edit_delt"/>
		</module>
		<module id="SUP1401" name="库房月结form" type="1" script="phis.application.sup.script.TreasuryMonthlyForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_YJJL_FORM</p>
				<p name="serviceId">treasuryMonthlyService</p>
				<p name="serviceActionId">saveTreasuryMonthly</p>
			</properties>
			<action id="monthly" name="月结" scale ="large"  iconCls="save24"/>
			<action id="cancel" name="关闭" scale ="large"  iconCls="close24"/>
		</module>

		<module id="SUP30" name="库房月结(二级)"  script="phis.application.sup.script.TreasuryEjMonthly">
			<properties>
				<p name="refForm">phis.application.sup.SUP/SUP/SUP3001</p>
				<p name="entryName">phis.application.sup.schemas.WL_EJ_YJJL</p>
			</properties>
			<action id="monthly" name="月结"  iconCls="month_edit"/>
			<action id="cancelMonthly" name="取消月结"  iconCls="month_edit_delt"/>
		</module>
		<module id="SUP3001" name="库房月结form" type="1" script="phis.application.sup.script.TreasuryEjMonthlyForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_YJJL_EJ_FORM</p>
				<p name="serviceId">treasuryMonthlyService</p>
				<p name="serviceActionId">saveTreasuryEjMonthly</p>
			</properties>
			<action id="monthly" name="月结" scale ="large"  iconCls="save24"/>
			<action id="cancel" name="关闭" scale ="large"  iconCls="close24"/>
		</module>
		<module id="SUP50" name="维修单申请" script="phis.application.sup.script.RepairRequestrList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG</p>
				<p name="mutiSelect">true</p>  
				<p name="removeByFiled">WXXH</p>
				<p name="refwxfrom">phis.application.sup.SUP/SUP/SUP5001</p>
				<p name="refYs">phis.application.sup.SUP/SUP/SUP5002</p>
			</properties>
			<action id="refresh" name="刷新"/>
			<action id="add" name="新增"/>
			<action id="open" name="修改" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="提交" />
			<action id="acceptance" name="验收" iconCls="commit"/>
		</module>
		<module id="SUP5001" name="维修单申请-新增" type="1" script="phis.application.sup.script.RepairRequestrForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_FORM</p>    
			</properties>     
			<action id="add" name="新增"/>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module> 
		<module id="SUP5002" name="维修单申请-验收" type="1" script="phis.application.sup.script.RepairRequestrYSForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_YS_FORM</p>    
			</properties>     
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP5004" name="维修单申请-查看" type="1" script="phis.application.sup.script.RepairRequestrCKForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_CK_FORM</p>    
			</properties>     
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP5003" name="维修单申请-验收查看" type="1" script="phis.application.sup.script.RepairRequestrYSCKForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_YS_FORM</p>    
			</properties>     
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP52" name="维修单申请" script="phis.application.sup.script.RepairRequestrList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG</p>
				<p name="mutiSelect">true</p>  
				<p name="removeByFiled">WXXH</p>
				<p name="refwxfrom">phis.application.sup.SUP/SUP/SUP5201</p>
				<p name="refYs">phis.application.sup.SUP/SUP/SUP5202</p>
			</properties>
			<action id="refresh" name="刷新"/>
			<action id="add" name="新增"/>
			<action id="open" name="修改" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="提交" />
			<action id="acceptance" name="验收" iconCls="commit"/>
		</module>
		<module id="SUP5201" name="维修单申请-新增" type="1" script="phis.application.sup.script.RepairRequestrForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_FORM</p>    
			</properties>     
			<action id="add" name="新增"/>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module> 
		<module id="SUP5202" name="维修单申请-验收" type="1" script="phis.application.sup.script.RepairRequestrYSForm">
			<properties> 
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_YS_FORM</p>    
			</properties>     
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="SUP51" name="日常维修管理" script="phis.application.sup.script.MaintenanceMsgModule">
			<properties>
				<p name="refMode">phis.application.sup.SUP/SUP/SUP5101</p>
			</properties>
		</module>
		<module id="SUP5101" name="日常维修管理"  type="1" script="phis.application.sup.script.MaintenanceMsgTabModule">
			<action id="maintenanceMsg" viewType="list" name="维修单申请" ref="phis.application.sup.SUP/SUP/SUP510101" />
			<action id="maintenanceMsgDetails" viewType="list" name="维修报告单" ref="phis.application.sup.SUP/SUP/SUP510102" />
		</module>
		<module id="SUP510101" name="维修单申请" type="1" script="phis.application.sup.script.MaintenanceMsgSearchList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_SQ</p>
				<p name="serviceId">repairRequestrService</p>
			</properties>
			<action id="refresh" name="刷新"/>
			<action id="service" name="维修" iconCls="commit"/>
			<action id="back" name="退回" iconCls="arrow_undo"/>
			<action id="view" name="查看" iconCls="read"/>
		</module>
		<module id="SUP510103" name="日常维修报告" type="1" script="phis.application.sup.script.MaintenanceServiceModule">
			<properties>
				<p name="serviceId">repairRequestrService</p>
			</properties>
			<action id="servicetab" viewType="form1" name="维修单申请报告" ref="phis.application.sup.SUP/SUP/SUP51010301"/>
			<action id="equipmenttab" viewType="form2" name="设备维修状况" ref="phis.application.sup.SUP/SUP/SUP51010302"/>
		</module>
		<module id="SUP51010301" name="维修单申请报告" type="1"  script="phis.application.sup.script.MaintenanceModule">
			<properties>       
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_WX_FORM</p>   
				<p name="refForm">phis.application.sup.SUP/SUP/SUP5101030101</p>
				<p name="refList">phis.application.sup.SUP/SUP/SUP5101030102</p>
			</properties>    
		</module>
		<module id="SUP5101030101" name="维修单申请报告" type="1" script="phis.application.sup.script.MaintenanceServiceForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_WX_FORM</p>
			</properties>
		</module>
		<module id="SUP5101030102" name="维修材料" type="1" script="phis.application.sup.script.MaintenanceMaterialsEditorList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXPJ</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="SUP51010302" name="设备维修状况" type="1" script="phis.application.sup.script.MaintenanceServiceForSbForm">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_SBWX_FORM</p>
			</properties>
		</module>		
		<module id="SUP510102" name="维修报告单" type="1" script="phis.application.sup.script.MaintenanceMsgDetailsList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG_BG</p>
			</properties>
			<action id="refresh" name="刷新"/>
			<action id="modify" name="修改" iconCls="update"/>
			<action id="audit" name="审核" iconCls="archiveMove_commit"/>
		</module>
		<module id="SUP510104" name="日常维修报告" type="1" script="phis.application.sup.script.MaintenanceServiceBgModule">
			<properties>
				<p name="serviceId">repairRequestrService</p>
			</properties>
			<action id="servicetab1" viewType="form1" name="维修单申请报告" ref="phis.application.sup.SUP/SUP/SUP51010301"/>
			<action id="equipmenttab1" viewType="form2" name="设备维修状况" ref="phis.application.sup.SUP/SUP/SUP51010302"/>
		</module> 
	</catagory>
</application>