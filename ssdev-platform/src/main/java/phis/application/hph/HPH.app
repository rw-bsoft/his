<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.hph.HPH" name="住院药房管理">
	<catagory id="HPH" name="住院药房管理">
		<module id="HPH01" name="医嘱发药" script="phis.application.hph.script.HospitalPharmacyDispensing" >
			<properties>
				<p name="refList">phis.application.hph.HPH/HPH/HPH0101</p>
				<p name="refModule">phis.application.hph.HPH/HPH/HPH0102</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="saveActionID">saveHospitalPharmacyDispensing</p>
				<p name="saveFullRefundActionID">saveMedicineFullRefund</p>
				<p name="saveRefundActionID">saveMedicineRefund</p>
				<p name="initializationServiceID">pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="queryMedicineDepartmentActionID">queryMedicineDepartment</p>
			</properties>
			<action id="sx" name="刷新"  iconCls="refresh"/>
			<action id="fy" name="医嘱发药" iconCls="antibiotics"/>
			<action id="yzhzfy" name="汇总发药" iconCls="antibiotics"/>
			<action id="qt" name="全退" iconCls="pill_add"/>
			<action id="thbq" name="退回病区" iconCls="pill_go"/>
		</module>
		<module id="HPH0101" name="病区提交module" script="phis.application.hph.script.HospitalPharmacyDispensingModule" type="1" >
			<properties>
				<p name="refTopList">phis.application.hph.HPH/HPH/HPH010101</p>
				<p name="refUnderList">phis.application.hph.HPH/HPH/HPH010102</p>
			</properties>
		</module>
		<module id="HPH010101" name="病区待发药记录病区" script="phis.application.hph.script.HospitalPharmacyWaitDispensing"  type="1">
			<properties>
				<p name="fullserviceId">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryDispensingWard</p>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ01_BQ</p>
			</properties>
		</module>
		<module id="HPH010102" name="病区待发药记录Tab" script="phis.application.hph.script.HospitalPharmacyDispensingLeftModule"  type="1">
			<properties>
			</properties>
			<action id="tjd" name="提交单" ref="phis.application.hph.HPH/HPH/HPH01010201" viewType="tjd"/>
			<action id="br" name="病人" ref="phis.application.hph.HPH/HPH/HPH01010202" viewType="br"/>
		</module>
		<module id="HPH01010201" name="病区待发药记录(提交单)" script="phis.application.hph.script.HospitalPharmacyDispensingLeftList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ01_TJ</p>
				<p name="fullserviceId">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryDispensing_bq</p>
			</properties>
		</module>
		<module id="HPH01010202" name="病区待发药记录(病人)" script="phis.application.hph.script.HospitalPharmacyDispensingLeftList_br"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ02_BR</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="fullserviceId">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryDispensing_br</p>
			</properties>
		</module>
		<module id="HPH0102" name="病区提交右边tab" script="phis.application.hph.script.HospitalPharmacyDispensingRightTabModule"  type="1">
			<properties>
			</properties>
			<action id="yzfy" name="医嘱发药" ref="phis.application.hph.HPH/HPH/HPH010201" viewType="mx"/>
			<action id="hzfy" name="汇总发药" ref="phis.application.hph.HPH/HPH/HPH010202" viewType="hz"/>
			<action id="thbq" name="退回病区" ref="phis.application.hph.HPH/HPH/HPH010203" viewType="th"/>
		</module>
		<module id="HPH010201" name="医嘱发药" script="phis.application.hph.script.HospitalPharmacyDispensingRightList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ02_YZFY</p>
				<p name="fullserviceId">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryDispensing</p>
			</properties>
		</module>
		<module id="HPH010202" name="汇总发药" script="phis.application.hph.script.HospitalPharmacyDispensingRightCollectList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.ZY_BQYZ_FY</p>
			</properties>
		</module>
		<module id="HPH010203" name="退回病区" script="phis.application.hph.script.HospitalPharmacyDispensingRightDetailList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TJ02_FY</p>
			</properties>
		</module>
		<module id="HPH0103" name="医嘱发药打印" script="phis.application.hph.script.DispensingDetailsPrintView"  type="1">
			<properties>
			</properties>
		</module> 
		<module id="HPH02" name="病区退药" script="phis.application.hph.script.HospitalPharmacyBackMedicine" >
			<properties>
				<p name="refList">phis.application.hph.HPH/HPH/HPH0201</p>
				<p name="refModule">phis.application.hph.HPH/HPH/HPH0202</p>
				<p name="initializationServiceID">pharmacyManageService</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="saveActionID">saveHospitalPharmacyBackMedicine</p>
				<p name="saveFullRefundActionID">saveBackMedicineFullRefund</p>
				<p name="saveRefundActionID">saveBackMedicineRefund</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="queryMedicineDepartmentActionID">queryMedicineDepartment</p>
			</properties>
			<action id="sx" name="刷新" iconCls="refresh" />
			<action id="ty" name="退药" iconCls="pill_delete"/>
			<action id="thbq" name="退回病区" iconCls="pill_go"/>
			<action id="qt" name="全退" iconCls="pill_add"/>
		</module>
		<module id="HPH0201" name="退药处理左边module" script="phis.application.hph.script.HospitalPharmacyBackMedicineModule"   type="1">
			<properties>
				<p name="refTopList">phis.application.hph.HPH/HPH/HPH020101</p>
				<p name="refUnderList">phis.application.hph.HPH/HPH/HPH020102</p>
			</properties>
		</module>
		<module id="HPH020101" name="病区待退药记录" script="phis.application.hph.script.HospitalPharmacyWaitBackMedicine"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TY</p>
				<p name="fullserviceId">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryBackMedicineWard</p>
			</properties>
		</module>
		<module id="HPH020102" name="退药处理左下LIST" script="phis.application.hph.script.HospitalPharmacyBackMedicineLeftList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.ZY_BRRY_BQTY</p>
				<p name="fullserviceId">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryBackMedicine</p>
			</properties>
		</module>
		<module id="HPH0202" name="病区退药右边tab" script="phis.application.hph.script.HospitalPharmacyBackMedicineUnderTabModule" type="1" >
			<properties>
			</properties>
			<action id="tycl" viewType="tycl" name="退药处理" ref="phis.application.hph.HPH/HPH/HPH020201" />
			<action id="thbq" viewType="thbq" name="退回病区" ref="phis.application.hph.HPH/HPH/HPH020202" />
		</module>
		<module id="HPH020201" name="退药处理" script="phis.application.hph.script.HospitalPharmacyBackMedicineRightList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TYMX_TY</p>
			</properties>
		</module>
		<module id="HPH020202" name="退回病区" script="phis.application.hph.script.HospitalPharmacyBackMedicineToWardList"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.BQ_TYMX_TY</p>
			</properties>
		</module>
		
		<module id="HPH05" name="住院记账" script="phis.application.hph.script.HospitalPharmacyAccountingModule">
			<properties>
				<p name="entryName">phis.application.hph.schemas.ZY_YPJZ_FORM</p>
				<p name="refForm">phis.application.hph.HPH/HPH/HPH0501</p>
				<p name="refList">phis.application.hph.HPH/HPH/HPH0502</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="saveActionId">saveHospitalPharmacyAccounting</p>
			</properties>
			<action id="xy" name="西药方(alt+1)" iconCls="newclinic"/>
			<action id="zy" name="中药方(alt+2)" iconCls="pill_add"/>
			<action id="cy" name="草药方(alt+3)" iconCls="page_add"/>
			<action id="new" name="取消(alt+Q)" />
			<action id="save" name="记账(alt+A)" />
		</module>
		<module id="HPH0501" name="住院记账Form"  type="1" script="phis.application.hph.script.HospitalPharmacyAccountingForm"> 
			<properties> 
				<p name="entryName">phis.application.hph.schemas.ZY_YPJZ_FORM</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="loadActionId">loadZyxx</p>
				<p name="colCount">5</p>
				<p name="showButtonOnTop">0</p>
			</properties>
		</module>
		<module id="HPH0502" name="住院记账List" type="1"  script="phis.application.hph.script.HospitalPharmacyAccountingList">
			<properties> 
				<p name="entryName">phis.application.hph.schemas.ZY_YPJZ_LIST</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="queryActionId">queryJzmx</p>
				<p name="refList">phis.application.hph.HPH/HPH/HPH050201</p>
				<p name="refKcList">phis.application.hph.HPH/HPH/HPH050202</p>
			</properties>
			<action id="create" name="新增(alt+C)" />
			<action id="remove" name="删除(alt+D)" />
		</module >
		<module id="HPH050201" name="记账记录" type="1"
			script="phis.application.hph.script.HospitalPharmacyAccountingRecordList" >
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_ZYFYMX_JZ</p>
			</properties>
			<action id="confirm" name="确定" />
			<action id="close" name="取消" />
		</module>
		<module id="HPH050202" name="库存记录" type="1"
			script="phis.application.hph.script.HospitalPharmacyKCRecordList" >
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_KCMX_JZ</p>
			</properties>
			<action id="confirm" name="确定" />
			<action id="close" name="取消" />
		</module>
		
		<module id="HPH03" name="病区发药查询" script="phis.application.hph.script.HospitalPharmacyHistoryDispensing" >
			<properties>
				<p name="refLeftList">phis.application.hph.HPH/HPH/HPH0301</p>
				<p name="refRightList">phis.application.hph.HPH/HPH/HPH0302</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="initializationServiceID">pharmacyManageService</p>
				<p name="serviceId">hospitalPharmacyService</p>
				<p name="queryMedicineDepartmentActionID">queryMedicineDepartment</p>
				<p name="entryName">phis.application.hph.schemas.YF_FYJL_LSCX</p>
				<p name="refDispensingDetailsPrint">phis.application.hph.HPH/HPH/HPH0303</p>
				<p name="queryWardActionID">queryWard</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print"/>
		</module>
		<module id="HPH0301" name="病区发药查询左边Tab" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList" type="1">
			<properties>
			</properties>
			<action id="tjd" name="提交单" ref="phis.application.hph.HPH/HPH/HPH030101" viewType="tjd"/>
			<action id="br" name="病人" ref="phis.application.hph.HPH/HPH/HPH030102" viewType="br"/>
		</module>
		<module id="HPH030101" name="病区发药记录list(提交单)" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList_tjd"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_FYJL_LSCX</p>
				<p name="serviceId">phis.hospitalPharmacyService</p>
				<p name="queryActionId">queryFyjltjd</p>
			</properties>
		</module>
		<module id="HPH030102" name="病区发药记录list(病人)" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList_br"  type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_FYJL_BR</p>
				<p name="serviceId">phis.hospitalPharmacyService</p>
				<p name="queryActionId">queryFyjlbr</p>
			</properties>
		</module>
		
		<module id="HPH0302" name="病区发药明细清单" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingRightList" type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_ZYFYMX_LSCX</p>
				<p name="summaryable">true</p>
			</properties>
			<action id="print" name="导出"/>
		</module>
		<module id="HPH0303" name="病区发药明细打印" script="phis.application.hph.script.DispensingDetailsPrintView" type="1">
			<properties>
			</properties>
		</module>
		
		
		
		<module id="HPH04" name="病区发药汇总查询" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingCollect" >
			<properties>
				<p name="initializationServiceActionID">initialization</p>
				<p name="initializationServiceID">pharmacyManageService</p>
				<p name="serviceid">hospitalPharmacyService</p>
				<p name="fullserviceid">phis.hospitalPharmacyService</p>
				<p name="queryServiceActionID">queryCollectRecords</p>
				<p name="queryMedicineDepartmentActionID">queryMedicineDepartment</p>
				<p name="refLeftList">phis.application.hph.HPH/HPH/HPH0401</p>
				<p name="refRightList">phis.application.hph.HPH/HPH/HPH0402</p>
				<p name="entryName">phis.application.hph.schemas.YF_ZYFYMX_HZCX_RIGHT</p>
				<p name="queryWardActionID">queryWard</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print" />
			<action id="export" name="导出" iconCls="excel" />
		</module>
		<module id="HPH0401" name="病区发药汇总查询左边list" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectLeftList" type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_ZYFYMX_HZCX_LEFT</p>
			</properties>
		</module>
		<module id="HPH0402" name="病区发药汇总查询右边list" script="phis.application.hph.script.HospitalPharmacyHistoryDispensingCollectRightList" type="1">
			<properties>
				<p name="entryName">phis.application.hph.schemas.YF_ZYFYMX_HZCX_RIGHT</p>
			</properties>
		</module>

	</catagory>
</application>