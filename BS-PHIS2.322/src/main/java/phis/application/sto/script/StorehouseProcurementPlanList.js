/**
 * 药库采购计划
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.StorehouseProcurementPlanList = function(cfg) {
	phis.application.sto.script.StorehouseProcurementPlanList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseProcurementPlanList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				return phis.application.sto.script.StorehouseProcurementPlanList.superclass.initPanel
						.call(this, sc)
			},
			doAdd : function() {
				this.module = this.createModule("module", this.addRef);
				this.module.on("winClose", this.onClose, this);
				this.module.on("save", this.onSave, this);
				this.module.initPanel();
				var win = this.module.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.doNew();
					this.module.op = "create"
				}
			},
			onClose : function() {
				this.module.getWin().hide();
			},
			loadData : function() {
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.sto.script.StorehouseProcurementPlanList.superclass.loadData
						.call(this)
			},
			// 双击修改
			onDblClick : function(grid, index, e) {
				this.doUpd("new","update");
			},
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				if(r.get("SP")==1){
				this.processReturnMsg(9000, "已审核的计划单不能删除");
				return;
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.get("JHDH") + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.grid.el.mask("正在删除...",
											"x-mask-loading");
									var ret = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.removeActionId,
												body : {
													"XTSB" : r.get("XTSB"),
													"JHDH" : r.get("JHDH")
												}
											});
									this.grid.el.unmask();
									if (ret.code > 300) {
										this.processReturnMsg(ret.code, ret.msg);
										return;
									}
									MyMessageTip.msg("提示", "删除成功!", true);
									this.loadData();
								}
							},
							scope : this
						})
			},
			onRenderer_sure:function(value, metaData, r){
				if(value==1){
				return "√"
				}
				return "";
			},
			doSp:function(){
			this.doUpd("sp","sp")
			},
			doUpd:function(state,op){
			var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				if(r.get("SP")==1){
				state="read";
				op="read";
				}
				this.module = this.createModule("module", this.addRef);
				this.module.on("winClose", this.onClose, this);
				this.module.on("save", this.onSave, this);
				this.module.initPanel();
				var win = this.module.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.changeButtonState(state);
					this.module.form.initDataId = r.get("SBXH");
					this.module.form.op = op
					this.module.form.loadData();
					this.module.list.requestData.cnd = ['and',
							['eq', ['$', 'a.JHDH'], ['i', r.get("JHDH")]],
							['eq', ['$', 'a.XTSB'], ['i', r.get("XTSB")]]];
					this.module.list.requestData.op=op;
					this.module.list.op = op
					this.module.list.loadData();
					this.module.op = op
				}
			},
			doUploadProducts : function() {
				debugger;
				this.grid.el.mask("正在上传...", "x-mask-loading")
			    var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.zyyService",
					serviceAction : "upProduct",
					body : {
					}
				});
				var json=result.json;
				var body=json.body;
				debugger;
				this.grid.el.unmask()
				if (body == "fail"){
					MyMessageTip.msg("提示","商品同步失败!",true);
					return;
				}else {
					MyMessageTip.msg("提示", "商品同步成功!", true);
				}
				this.refresh();
			},
			
			doUploadSuppliers : function() {
				debugger;
				this.grid.el.mask("正在上传...", "x-mask-loading")
			    var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.zyyService",
					serviceAction : "upSuppliers",
					body : {
					}
				});
				var json=result.json;
				var body=json.body;
				debugger;
				this.grid.el.unmask()
				if (body == "fail"){
					MyMessageTip.msg("提示","供应商同步失败!",true);
					return;
				}else {
					MyMessageTip.msg("提示", "供应商同步成功!", true);
				}
				this.refresh();
			},
			
			doUpload : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				if(r.get("SP")!=1&& r.get("ZX")!=1){
				MyMessageTip.msg("提示","请选中已审批且未执行的采购计划上传！" , true);
				return;
				}
				if(r.get("SCBZ")==2 ){
				MyMessageTip.msg("提示","请选中未上传的采购计划进行上传！" , true);
				return;
				}			    
			    var DWXH=r.get("DWXH");
			    var JHDH=r.get("JHDH");
			    var SBXH=r.get("SBXH");
			    var BZRQ=r.get("BZRQ");
			    var CKJE=r.get("CKJE");
			    var JHBZ=r.get("JHBZ");
			    var BZGH=r.get("BZGH");
			    var XTSB=r.get("XTSB");	
			    debugger;
			    this.grid.el.mask("正在上传采购计划...", "x-mask-loading")
				if(this.mainApp.deptId == "320124004" || this.mainApp.deptId == "320124008"){ //320124004 柘塘 320124008 和凤
					var payapi = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.zyyService",
								serviceAction : "upProcurementPlan",
								body : {
									DWXH:DWXH,
									JHDH:JHDH,
									SBXH:SBXH,
									BZRQ:BZRQ,
									CKJE:CKJE,
									JHBZ:JHBZ,
									BZGH:BZGH,
									XTSB:XTSB,
									YYDZ:this.mainApp.dept
								}
							});
				}else{
				    var url = "http://10.2.202.21:8280/services/upmedicalbusiness";
					var payapi = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.SptService",
								serviceAction : "upProcurementPlan",
								body : {
									url : url,
									DWXH:DWXH,
									JHDH:JHDH,
									SBXH:SBXH,
									BZRQ:BZRQ,
									CKJE:CKJE,
									JHBZ:JHBZ,
									BZGH:BZGH,
									XTSB:XTSB,
									YYDZ:this.mainApp.dept
								}
							});
				}						
				var json=payapi.json;
				var body=json.body;
				debugger;
				this.grid.el.unmask()
				if 	(payapi.code !=200){
					MyMessageTip.msg("提示",payapi.msg , true);
					return;
				}else if (body.code!=200){
					MyMessageTip.msg("提示","采购计划信息上传失败!"+body.msg , true);
					return;
				}else {
					MyMessageTip.msg("提示", "采购计划信息上传成功!", true);
				}
				this.refresh();
			}
		});