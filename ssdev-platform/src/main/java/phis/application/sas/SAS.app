<?xml version="1.0" encoding="UTF-8"?>

<application id="phis.application.sas.SAS" name="统计分析">
	<catagory id="SAS" name="统计分析">
		<module id="SAS40" name="科室领用汇总" script="phis.prints.script.DepartmentCollectPrintView">
			<properties>
				<p name="navParentKey">%user.manageUnit.ref</p>
				<p name="navParentText">%user.manageUnit.name</p>
			</properties>
		</module>
		<module id="SAS18" name="库存查询" script="phis.application.sas.script.SuppliesStockModule">
			<properties>
				<p name="refMode">phis.application.sas.SAS/SAS/SAS1801</p>
			</properties>
		</module>
		<module id="SAS1801" name="库存查询"  type="1" script="phis.application.sas.script.SuppliesStockSearchTabModule">
			<action id="stockSearch" viewType="list" name="库存明细查询" ref="phis.application.sas.SAS/SAS/SAS180101" />
			<action id="stockSearchDetails" viewType="list" name="库存汇总查询" ref="phis.application.sas.SAS/SAS/SAS180102" />
			<action id="stockEjKYJSearchDetails" viewType="list" name="二级库房库存汇总查询" ref="phis.application.sas.SAS/SAS/SAS180103" />
		</module>
		<module id="SAS180101" name="库存明细查询" type="1" script="phis.application.sas.script.SuppliesStockSearchList">
			<action id="refresh" name="刷新"   />
			<action id="print" name="打印" />
		</module>
		<module id="SAS180102" name="库存汇总查询" type="1" script="phis.application.sas.script.SuppliesStockSearchDetailsList">
			<action id="refresh" name="刷新"   />
			<action id="print" name="打印" />
		</module>
		<module id="SAS180103" name="二级库房库存汇总查询" type="1" script="phis.application.sas.script.SuppliesStockSearchYJKEJDetailList">
		</module>
		<module id="SAS38" name="库存查询(二级)" script="phis.application.sas.script.SuppliesStockEjModule">
			<properties>
				<p name="refMode">phis.application.sas.SAS/SAS/SAS3801</p>
			</properties>
		</module>
		<module id="SAS3801" name="库存查询"  type="1" script="phis.application.sas.script.SuppliesStockEjSearchTabModule">
			<action id="stockEjSearch" viewType="list" name="库存明细查询" ref="phis.application.sas.SAS/SAS/SAS380101" />
			<action id="stockEjSearchDetails" viewType="list" name="库存汇总查询" ref="phis.application.sas.SAS/SAS/SAS380102" />
			<action id="stockYJKEJSearchDetails" viewType="list" name="一级库房库存汇总查询" ref="phis.application.sas.SAS/SAS/SAS380103" />
		</module>
		<module id="SAS380101" name="库存明细查询" type="1" script="phis.application.sas.script.SuppliesStockEjSearchList">
			<action id="refresh" name="刷新"   />
			<action id="print" name="打印" />
		</module>
		<module id="SAS38010101" name="库存明细打印" type="1" script="phis.prints.script.SuppliesStockEjSearchPrintView">
		</module>
		<module id="SAS380102" name="库存汇总查询" type="1" script="phis.application.sas.script.SuppliesStockEjSearchDetailsList">
			<action id="refresh" name="刷新"   />
			<action id="print" name="打印" />
		</module>
		<module id="SAS380103" name="一级库房库存汇总查询" type="1" script="phis.application.sas.script.SuppliesStockEjKYJSearchDetailsList">
		</module>
		<module id="SAS38010201" name="库存汇总打印" type="1" script="phis.prints.script.SuppliesStockEjSearchDetailsPrintView">
		</module>
		<module id="SAS19" name="科室账册查询" script="phis.application.sas.script.DepartmentBooksModule">
			<properties>
				<p name="refMode">phis.application.sas.SAS/SAS/SAS1901</p>
			</properties>
		</module>
		<module id="SAS1901" name="科室账册查询"  type="1" script="phis.application.sas.script.DepartmentBooksTabModule">
			<action id="departmentBooksSearch" viewType="list" name="科室账册明细查询" ref="phis.application.sas.SAS/SAS/SAS190101" />
			<action id="departmentBooksDetails" viewType="list" name="科室账册汇总查询" ref="phis.application.sas.SAS/SAS/SAS190102" />
		</module>
		<module id="SAS190101" name="科室账册明细查询" type="1" script="phis.application.sas.script.DepartmentBooksList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_KSZC_CX</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="print" name="打印" />
		</module>
		<module id="SAS190102" name="科室账册汇总查询" type="1" script="phis.application.sas.script.DepartmentBooksDetailsList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_KSZC_HZ</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="print" name="打印" />
		</module>
		<module id="SAS20" name="固定资产查询" script="phis.application.sas.script.FixedAssetsSearchList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_ZCZB_CX</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="print" name="打印" />
		</module>
		<module id="SAS21" name="业务明细查询"   script="phis.application.sas.script.BusinessDocumentDetailTabModule">
			<action id="stockSearch" viewType="list" name="入库查询" ref="phis.application.sas.SAS/SAS/SAS2101" />
			<action id="stockSearchDetails" viewType="list" name="出库查询" ref="phis.application.sas.SAS/SAS/SAS2102" />
			<action id="changeKS" viewType="list" name="转科查询"  ref="phis.application.sas.SAS/SAS/SAS2103" />
			<action id="reported" viewType="list" name="报损查询"  ref="phis.application.sas.SAS/SAS/SAS2104" />
			<action id="reset" viewType="list" name="重置查询"  ref="phis.application.sas.SAS/SAS/SAS2105" />
			<action id="Plan" viewType="list" name="计划单查询"  ref="phis.application.sas.SAS/SAS/SAS2106" />
		</module>
		<module id="SAS2101" name="入库查询" type="1" script="phis.application.sas.script.BusinessDocumentDetailforRKList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_RK01_CX</p>
			</properties>
			<action id="refresh" name="刷新" />
		</module>
		<module id="SAS2102" name="出库查询" type="1" script="phis.application.sas.script.BusinessDocumentDetailforCKList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_CK02_CX</p>
			</properties>
			<action id="refresh" name="刷新" />
		</module>
		<module id="SAS2103" name="转科查询" type="1" script="phis.application.sas.script.ChangeDepartmentsList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_ZK02_CX</p>
				<p name="autoLoadData">0</p>--><!--关闭默认加载-->
			</properties>
			<action id="refresh" name="刷新" />
		</module>
		<module id="SAS2104" name="报损查询" type="1" script="phis.application.sas.script.ReportedLossList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_BS02_CX</p>
				<p name="autoLoadData">0</p>--><!--关闭默认加载-->
			</properties>
			<action id="refresh" name="刷新" />
		</module>
		<module id="SAS48" name="折旧信息查询" script="phis.application.sup.script.DepreciatedManagementInfoList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_ZJMX_LIST</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<!--<action id="print" name="打印" />-->
		</module>
		<module id="SAS49" name="计量设备查询" script="phis.application.sup.script.MeteringEquipmentInfoList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_JLSBCX</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<!--<action id="print" name="打印" />-->
		</module>
		<module id="SAS50" name="维修查询" script="phis.application.sup.script.RepairRequestrInfoList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG</p>
				<p name="refYs">phis.application.sup.SUP/SUP/SUP5003</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<!--<action id="print" name="打印" />-->
		</module>
		<module id="SAS51" name="维修查询" script="phis.application.sup.script.RepairRequestrInfoList">
			<properties>
				<p name="entryName">phis.application.sup.schemas.WL_WXBG</p>
				<p name="refYs">phis.application.sup.SUP/SUP/SUP5202</p>
			</properties>
			<action id="refreshWin" name="查询" iconCls="arrow_refresh" />
			<!--<action id="print" name="打印" />-->
		</module>
		<module id="SAS2105" name="重置查询" type="1" script="phis.application.sas.script.ResetSearchList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_CZ02_CX</p>
				<p name="autoLoadData">0</p>--><!--关闭默认加载-->
			</properties>
			<action id="refresh" name="刷新" />
		</module>
		<module id="SAS2106" name="计划单" type="1" script="phis.application.sas.script.PlanSearchList">
			<properties>
				<p name="entryName">phis.application.sas.schemas.WL_JH02_CX</p>
				<p name="autoLoadData">0</p>--><!--关闭默认加载-->
			</properties>
			<action id="refresh" name="刷新" />
			<!--<action id="print" name="打印" />-->
		</module>
		<module id="SAS22" name="库存台帐查询" script="phis.prints.script.InventoryLedgerPrintView">
		</module>
		<module id="SAS41" name="库存台帐查询(二级)" script="phis.prints.script.InventoryLedgerEjPrintView">
		</module>
		<module id="SAS33" name="入库汇总报表(二级)" script="phis.prints.script.WarehousingEjSummaryPrintView">
		</module>
		<module id="SAS37" name="出库汇总报表(二级)" script="phis.prints.script.LibraryEjSummaryPrintView">
		</module>
		<module id="SAS23" name="库房预警" script="phis.application.sas.script.SuppliesStockWarningTabModule">
			<action id="LowWarning" viewType="list" name="低储库房预警" ref="phis.application.sas.SAS/SAS/SAS2301" />
			<action id="HighWarning" viewType="list" name="高储库房预警" ref="phis.application.sas.SAS/SAS/SAS2302" />
			<action id="ExpirationWarning"  viewType="list" name="库存失效预警" ref="phis.application.sas.SAS/SAS/SAS2303" />
		</module>
		<module id="SAS2301" name="低储库房预警" type="1" script="phis.application.sas.script.SuppliesStockLowWarning">
			<action id="refresh" name="刷新"   />
		</module>
		<module id="SAS2302" name="高储库房预警" type="1" script="phis.application.sas.script.SuppliesStockHighWarning">
			<action id="refresh" name="刷新"   />
		</module>
		<module id="SAS2303" name="库存失效预警" type="1" script="phis.application.sas.script.SuppliesStockExpirationWarning">
			<action id="refresh" name="刷新"   />
		</module>
		
		<module id="SAS31" name="入库汇总报表" script="phis.prints.script.WarehousingSummaryPrintView">
			<properties>
				<p name="navParentKey">-1</p>
			</properties>
		</module>
		<module id="SAS32" name="出库汇总报表" script="phis.prints.script.LibrarySummaryPrintView">
			<properties>
				<p name="navParentKey">-1</p>
			</properties>
		</module> 
		<module id="SAS35" name="转科汇总" script="phis.prints.script.TransferAccountingPrintView">
		</module>
		<module id="SAS44" name="报损汇总" script="phis.prints.script.FaultySummaryPrintView">
		</module>
		<module id="SAS36" name="库房收支月报" script="phis.prints.script.StoreMonthlyPrintView">
		</module>
		<module id="SAS43" name="库房收支汇总" script="phis.prints.script.StoreSummaryPrintView">
		</module>
	</catagory>
</application>