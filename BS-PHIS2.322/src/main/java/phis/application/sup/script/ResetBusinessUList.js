$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.ResetBusinessUList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.ResetBusinessUList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.sup.script.ResetBusinessUList,
		phis.script.SimpleList, {
			onWinShow : function() {
				// this.resetBusinessDetailModule.zblb = this.zblb;
			},
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.ResetBusinessUList.superclass.onReady.call(this);
					var startDate = "";
					var endDate = "";
					if (this.dateFrom) {
						startDate = new Date(this.dateFrom.getValue()).format("Y-m-d");
					}
					if (this.dateTo) {
						endDate = new Date(this.dateTo.getValue()).format("Y-m-d");
					}
					this.requestData.cnd = ['and',['and',['and',['and',['ge',['$',"str(a.ZDRQ,'yyyy-mm-dd')"],['s', startDate]],
					                                            ['le',['$',"str(a.ZDRQ,'yyyy-mm-dd')"],['s', endDate]]],
									                            ['le', ['$', 'DJZT'], ['i', 0]]],
							                                    ['eq', ['$', 'ZBLB'], ['i', this.zblb]]],
							                                    ['eq', ['$', 'KFXH'],['$','%user.properties.treasuryId']]];
							                                
					
					this.loadData();
				}
			},

			expansion : function(cfg) {
							var bar = cfg.tbar;
							cfg.tbar = {
								enableOverflow : true,
								items : bar
							}
						},
			getCndBar : function(items) {
				var filelable = new Ext.form.Label({
					text : "单据状态："
				})
		       this.statusRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 100,
					id : 'djztcz',
					name : 'djztcz', // 后台返回的JSON格式，直接赋值
					value : "0",
					items : [{
								boxLabel : '制单',
								name : 'djztcz',
								inputValue : 0
							}, {
								boxLabel : '审核',
								name : 'djztcz',
								inputValue : 1
							}]
				    });
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))+ "-01";
				var datelable = new Ext.form.Label({
				     	text : "单据日期："
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'stardatecz',
							name : 'stardatecz',
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
							id : 'enddatecz',
							name : 'enddatecz',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '����ʱ��'
						});
				return ["<h1 style='text-align:center'>未记账重置单:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
			doRefreshWin : function() {
				this.clear();
				var startDate = "";
				var endDate = ""; 
				if (this.dateFrom) {
					startDate = new Date(this.dateFrom.getValue()).format("Y-m-d");
				}
				if (this.dateTo) {
					endDate = new Date(this.dateTo.getValue()).format("Y-m-d");
				}
				var selectRq = this.statusRadio.getValue().inputValue;
				if(selectRq == 0){
					this.requestData.cnd = ['and',['and',['and',['and',['ge',['$',"str(a.ZDRQ,'yyyy-mm-dd')"],['s', startDate]],
										                    ['le',['$',"str(a.ZDRQ,'yyyy-mm-dd')"],['s', endDate]]],
								                            ['eq',['$', 'DJZT'],['i',this.statusRadio.getValue().inputValue]]],
						                                    ['eq', ['$', 'ZBLB'], ['i', this.zblb]]],
						                                    ['eq', ['$', 'KFXH'],['$','%user.properties.treasuryId']]];
				}else{
				  this.requestData.cnd = ['and',['and',['and',['and',['ge',['$',"str(a.SHRQ,'yyyy-mm-dd')"],['s', startDate]],
										                    ['le',['$',"str(a.SHRQ,'yyyy-mm-dd')"],['s', endDate]]],
								                            ['eq',['$', 'DJZT'],['i',this.statusRadio.getValue().inputValue]]],
						                                    ['eq', ['$', 'ZBLB'], ['i', this.zblb]]],
						                                    ['eq', ['$', 'KFXH'],['$','%user.properties.treasuryId']]];
				}
				this.loadData();
			},
			doAdd : function() {
				this.resetBusinessDetailModule = this.createModule("resetBusinessDetailModule", this.addRef);
				this.resetBusinessDetailModule.zblb = this.zblb;
				this.resetBusinessDetailModule.on("save", this.onSave,this);
				this.resetBusinessDetailModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.resetBusinessDetailModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.resetBusinessDetailModule.op = "create";
					this.resetBusinessDetailModule.doNew();
				}
			},
			onClose : function() {
			    this.getWin().hide();
				this.doRefreshWin();
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.resetBusinessDetailModule = this.createModule("resetBusinessDetailModule", this.addRef);
				this.resetBusinessDetailModule.zblb = this.zblb;
				this.resetBusinessDetailModule.on("save", this.onSave,this);
				this.resetBusinessDetailModule.on("winClose",this.onClose, this);
				
				var win = this.getWin();
				win.add(this.resetBusinessDetailModule.initPanel());
				
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.resetBusinessDetailModule.changeButtonState("new");
				} else if (djzt == 1) {
					this.resetBusinessDetailModule.changeButtonState("verified");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.resetBusinessDetailModule.op = "update";
					this.resetBusinessDetailModule.initDataBody = initDataBody;
					this.resetBusinessDetailModule.loadData(initDataBody);
				}

			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("DJZT") == 1) {
					Ext.Msg.alert("提示", "单据已审核不允许删除");
					return;
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						title += r.get(this.schema.pkeys[i])
					}
				}
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var body={};
                                    body["DJXH"]=r.id;
                                    var r1 = phis.script.rmi.miniJsonRequestSync({
                                                serviceId : "resetBusinessService",
                                                serviceAction : "delete",
                                                body : body,
                                                op : this.op
                                            });
                                    if (r1.code > 300) {
                                        this.processReturnMsg(r1.code, r1.msg,
                                                this.onBeforeSave);
                                        return false;
                                    } else {
                                        this.doRefreshWin();
                                    }
								}
							},
							scope : this
						})
			},
			onDblClick : function(grid, index, e) {
				this.doUpd();
			},
			onSave : function() {
				this.fireEvent("save", this);
			},
			doCancel : function() {
				if (this.resetBusinessDetailModule) {
					return this.resetBusinessDetailModule.doClose();
				}
			}
			
		})