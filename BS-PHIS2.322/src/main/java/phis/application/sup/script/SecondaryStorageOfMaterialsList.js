$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.SecondaryStorageOfMaterialsList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
    cfg.showBtnOnLevel = true;
	phis.application.sup.script.SecondaryStorageOfMaterialsList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.SecondaryStorageOfMaterialsList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			getCndBar : function(items) {
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
					   text : "记账日期:"
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'SecondaryStorageOfMaterialsListdateFrom',
							//name : 'SecondaryStorageOfMaterialsListdateFrom',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						});
				var tolable = new Ext.form.Label({
					        text : " 到 "
						});
				this.dateTo = new Ext.form.DateField({
							id : 'SecondaryStorageOfMaterialsListdateTo',
							//name : 'SecondaryStorageOfMaterialsListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
				return ["<h1 style='text-align:center'>已记账入库单:</h1>", '-',
						datelable, this.dateFrom, tolable, this.dateTo, '-']
			},
			doRefreshWin : function() {
				var addCndDjzt = [
                                'and',
                                ['eq', ['$', 'KFXH'],
                                        ['i', this.mainApp['phis'].treasuryId]],
                                ['eq', ['$', 'DJZT'], ['i', 2]]];
				var addCndDate = [
						'and',
						['ge', ['$', "str(JZRQ,'yyyy-mm-dd')"],
								['s', this.dateFrom.getValue().format('Y-m-d')]],
						['le', ['$', "str(JZRQ,'yyyy-mm-dd')"],
								['s', this.dateTo.getValue().format('Y-m-d')]]]
				this.requestData.cnd = ['and', addCndDjzt, addCndDate];
				this.refresh();
				return;
			},
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.storageOfMaterialsModule = this.oper.createModule(
						"storageOfMaterialsModule", this.addRef);
				this.storageOfMaterialsModule.on("save", this.onSave, this);
				var win = this.storageOfMaterialsModule.getWin();
				win.add(this.storageOfMaterialsModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.storageOfMaterialsModule.changeButtonState("new");
				} else if (djzt == 1) {
					this.storageOfMaterialsModule.changeButtonState("verified");
				} else if (djzt == 2) {
					this.storageOfMaterialsModule.changeButtonState("commited");
				}
                if(r.data.THDJ!=0){
                     this.storageOfMaterialsModule.changeButtonState("rejectCommited");
                }
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageOfMaterialsModule.op = "update";
					this.storageOfMaterialsModule.initDataBody = initDataBody;
					this.storageOfMaterialsModule.loadData(initDataBody);
				}

			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "查看";
				item.cmd = "look";
				this.doAction(item, e)

			},
            onSave:function(){
              this.fireEvent("save", this);
            },
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
            doPrint : function() {
                var module = this.createModule("storeOfMaterialsPrint",
                        this.refStoreOfMaterialsPrint)
                var r = this.getSelectedRecord()
                if (r == null) {
                	MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
                    return;
                }
                module.djxh=r.data.DJXH;
                module.initPanel();
                module.doPrint();
            }
		})