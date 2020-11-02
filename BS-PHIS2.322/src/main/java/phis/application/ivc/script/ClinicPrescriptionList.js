$package("phis.application.ivc.script")

$import("phis.script.SelectList")

phis.application.ivc.script.ClinicPrescriptionList = function(cfg) {
	// cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	phis.application.ivc.script.ClinicPrescriptionList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.ivc.script.ClinicPrescriptionList,
		phis.script.SelectList, {
			onDblClick:function(grid,index,e){
			},
			loadData : function() {
				this.setcfd = true;
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = "phis.clinicChargesProcessingService";
				this.requestData.serviceAction = "queryDJ";
				this.requestData.body = this.MZXX;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
				this.resetButtons();
			},
			expansion : function(cfg) {
				cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
			},
			onStoreLoadData : function(store, records, ops) {
				if(this.MZXX.KSJZXH){
					var ksjzxh=this.MZXX.KSJZXH
				var selRecords=[];
				store.each(function(record) {
				if(record.get("JZXH")==ksjzxh){
				selRecords.push(record)
				}
				})
				this.grid.selModel.selectRecords(selRecords);
				}else{
				this.grid.selModel.selectAll();
				}
				this.grid.getView().focusRow(0);
				this.cndField.setValue(store.getAt(0).get("XQ"))
				if (this.showButtonOnTop) {
					btnss = this.grid.getTopToolbar();
					var btns = btnss.find("cmd", "commit");
					var btn = btns[0];
					btn.focus();
				}
			},
			onEnterKey : function() {
				this.doCommit();
			},
			DJMSRenderer : function(value, metaData, r, row, col) {
				if(value==undefined){
					return "";
				}
				if(value=="全自费"){
					return "<font style='color:green;font-weight:bold'>"+value+"</font>";
				}
				return "<font style='color:red;font-weight:bold'>"+value+"</font>";
			},
			doCommit : function() {
				var bllx = 0;
				if (this.YBXX) {
					bllx = this.YBXX.BLLX
				}
				var rs = this.grid.getSelectionModel().getSelections();
				var djs = [];
				var msg = [];
				var isSameDjms = true;//单据描述是否相同
				var tmpDjms = "0";
				for (var i = 0; i < rs.length; i++) {
					var r = rs[i];
					if(i == 0){
						if(r.get("DJMS")==0){
							isSameDjms = false;
						}
						tmpDjms = r.get("DJMS");
					}else if(isSameDjms && tmpDjms != r.get("DJMS")){
						isSameDjms = false;
					}
					var dj = {};
					dj.CFSB = r.get("CFSB");
					dj.CFLX = r.get("CFLX");
					/**********add by lizhi at 2017-06-20处理科室代码带不过去的问题************/
					dj.KDKS = r.get("KDKS");
					/**********add by lizhi at 2017-06-20处理科室代码带不过去的问题************/
					dj.BLLX = bllx;
					djs.push(dj);
					var dates = r.get("KDRQ");
					var date = dates.split("-");
					var now = new Date();
					if (!(parseInt(date[0], 10) == now.getFullYear()
							&& parseInt(date[1], 10) == (now.getMonth() + 1) && parseInt(
							date[2].substr(0, 2), 10) == now.getDate())) {
						var n = this.store.indexOf(r);
						msg.push(n + 1);
					}
				}
				if(this.MZXX.BRXZ==2000 && !isSameDjms){
					MyMessageTip.msg("提示", "医保病人单次结算只能选择全自费单据或医保可报销单据！", true);
					return;
				}
				var n = this.grid.getStore().getCount();
				if(n>rs.length){
					this.MZXX.isExistsNopayJsd = true;
				}else{
					this.MZXX.isExistsNopayJsd = false;
				}
				if (msg.length > 0) {
					Ext.Msg.show({
								title : '提示',
								msg : '第' + msg + '行单据非当天收费项目,请询问病人是否收费?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.setcfd = false;
										this.opener.setCFD(djs);
										this.win.hide();
									} else {
									}
								},
								scope : this
							})
				}else {
					this.setcfd = false;
					this.opener.setCFD(djs);
					this.win.hide();
					// this.doSPLR(djs);
				}
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
					this.opener.formModule.form.getForm().findField("MZGL")
							.focus(false, 200);
				}
			},
			getWin : function() {
				var win = this.win
				var closeAction = "close"
				if (!this.mainApp || this.closeAction) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.name,
						width : 660,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("close", function() {
								this.opener.setCFD([]);
								var n = this.grid.getStore().getCount();
								if(n>0){
									this.MZXX.isExistsNopayJsd = true;
								}else{
									this.MZXX.isExistsNopayJsd = false;
									}
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
						if(this.setcfd){
							this.opener.setCFD([]);
							var n = this.grid.getStore().getCount();
							if(n>0){
								this.MZXX.isExistsNopayJsd = true;
							}
						}
						this.fireEvent("hide", this)
					}, this)
					this.win = win
				}
				return win;
			}
		})