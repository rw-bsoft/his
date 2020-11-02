/**
 * 采购入库详细form
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailForm");

phis.application.sto.script.StorehouseProcurementPlanDetailForm = function(cfg) {
	cfg.disabledField=["JHBZ","BZRQ","DWMC"];//如果是查看 界面需要灰掉的标签ID
	cfg.remoteUrl="Medicines";
	cfg.remoteTpl='<td width="1px" style="background-color:#deecfd">{numKey}.</td><td width="200px">{DWMC}</td>';
	cfg.queryParams={"tag":"jhdw"};
	cfg.conditionId=false
	phis.application.sto.script.StorehouseProcurementPlanDetailForm.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseProcurementPlanDetailForm,
				phis.application.sto.script.StorehouseMySimpleDetailForm, {
			getRemoteDicReader : function() {
			return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'DWXH'
								}, {
									name : 'DWMC'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var form=this.form.getForm();
				form.findField("DWMC").setValue(record.get("DWMC"));
				this.data["DWXH"]=record.get("DWXH");
			},
			loadData : function() {
				if(!this.initDataId){
				return;}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.loadServiceId,
							serviceAction : this.loadMethod,
							pkey : this.initDataId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.loadData);
					return;
				}
				this.doNew();
				this.op="update"
				this.initFormData(r.json.body);
				this.fireEvent("loadData",this.entryName,r.json.body);
			}
			
});