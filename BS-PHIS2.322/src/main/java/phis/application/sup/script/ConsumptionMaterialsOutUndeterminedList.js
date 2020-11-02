/**
 * ���ʳ���ȷ�ϣ�������
 * 
 * @author gaof
 */
$package("phis.application.sup.script")
$import("phis.script.SelectList")

phis.application.sup.script.ConsumptionMaterialsOutUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
//	cfg.modal = this.modal = true;
    cfg.selectOnFocus = true;
//	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	// cfg.autoLoadData = false;
	phis.application.sup.script.ConsumptionMaterialsOutUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.ConsumptionMaterialsOutUndeterminedList,
		phis.script.SelectList, {
             loadData : function() {
                this.clear(); 
                this.requestData.serviceId = this.serviceId;
                this.requestData.serviceAction = "getCK01Info";
                
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
                this.resetButtons();
            },

			getCndBar : function(items) {
				var filelable = new Ext.form.Label({
							text : "����״̬:"
						})
				this.statusRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 100,
					name : 'ConsumptionMaterialsOutUndeterminedListdjzt', // ��̨���ص�JSON��ʽ��ֱ�Ӹ�ֵ
					value : "0",
					items : [{
								boxLabel : '�Ƶ�',
								name : 'ConsumptionMaterialsOutUndeterminedListdjzt',
								inputValue : 0
							}, {
								boxLabel : '���',
								name : 'ConsumptionMaterialsOutUndeterminedListdjzt',
								inputValue : 1
							}],
					listeners : {
						change : function(group, newValue, oldValue) {
							djztValue = parseInt(newValue.inputValue);
							if (djztValue == 0) {
								datelable.setDisabled(true);
								this.dateFrom.setDisabled(true);
								tolable.setDisabled(true);
								this.dateTo.setDisabled(true);
                                this.doRefreshWin();
							} else if (djztValue == 1) {
								datelable.setDisabled(false);
								this.dateFrom.setDisabled(false);
								tolable.setDisabled(false);
								this.dateTo.setDisabled(false);
                                this.doRefreshWin();
							}

						},
						scope : this
					}
				});
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
							text : "�������:"
						})
				this.dateFrom = new Ext.form.DateField({
							name : 'ConsumptionMaterialsOutUndeterminedListdateFrom',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '��ʼʱ��'
						});
				var tolable = new Ext.form.Label({
							text : " �� "
						});
				this.dateTo = new Ext.form.DateField({
							name : 'ConsumptionMaterialsOutUndeterminedListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '����ʱ��'
						});

				return ["<h1 style='text-align:center'>δ���˳��ⵥ:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
			// ˢ��ҳ��
			doRefreshWin : function() {
				if (this.statusRadio.getValue() != null) {
					if (this.statusRadio.getValue().inputValue == 0) {
						this.requestData.DJZT=0;
						this.refresh();
						return;
					} else if (this.statusRadio.getValue().inputValue == 1) {
						this.requestData.DJZT=1;
                        this.requestData.SHRQQ=this.dateFrom.getValue().format('Y-m-d');     
                        this.requestData.SHRQZ=this.dateTo.getValue().format('Y-m-d');
						this.refresh();
						return;
					}
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.secondaryMaterialsOutDetailModule = this.createModule(
						"secondaryMaterialsOutDetailModule", this.addRef);
				this.secondaryMaterialsOutDetailModule.on("save", this.onSave,
						this);
				this.secondaryMaterialsOutDetailModule.on("winClose",
						this.onClose, this);
				var win = this.getWin();
				win.add(this.secondaryMaterialsOutDetailModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.secondaryMaterialsOutDetailModule
							.changeButtonState("new");
				} else if (djzt == 1) {
					this.secondaryMaterialsOutDetailModule
							.changeButtonState("verified");
				}
				// else if (djzt == 2) {
				// this.secondaryMaterialsOutDetailModule.changeButtonState("commited");
				// }
				win.show()
				win.center()
				if (!win.hidden) {
					this.secondaryMaterialsOutDetailModule.op = "update";
					this.secondaryMaterialsOutDetailModule.initDataBody = initDataBody;
					this.secondaryMaterialsOutDetailModule
							.loadData(initDataBody);
				}

			},
			onClose : function() {
				this.getWin().hide();
				this.refresh();
			},
			doCancel : function() {
				if (this.secondaryMaterialsOutDetailModule) {
					return this.secondaryMaterialsOutDetailModule.doClose();
				}
			},
			onSave : function() {
				this.fireEvent("save", this);
			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "�޸�";
				item.cmd = "upd";
				this.doAction(item, e)
			},
			// �ύ
			doCommit : function() {
				var records = this.getSelectedRecords()
				if (records == null) {
					return;
				}
				this.clearSelect();

				var body = [];
				Ext.each(records, function() {
							body.push(this.id);
						});

				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "allCommit",
							body : body
						});
				if (r1.code > 300) {
					this.processReturnMsg(r1.code, r1.msg, this.onBeforeSave);
					return false;
				} else {
                    if (r1.json.WZMC) {
                        MyMessageTip.msg("��ʾ", "����" + r1.json.WZMC
                                        + "��治��,�������!", true);
                    }
					// this.fireEvent("winClose", this);
					this.fireEvent("delete", this);
					this.refresh();
				}
			},
			doRemove : function() {
				var records = this.getSelectedRecords()
				if (records == null) {
					return;
				}
				this.clearSelect();

				var body = [];
				Ext.each(records, function() {
							if (this.data["DJLX"] == 3) {
								Ext.Msg.alert("��ʾ", "����" + this.id
												+ ",�̵����ݲ����?��");
								return;
							}
							if (this.data["DJZT"] == 1) {
								Ext.Msg.alert("��ʾ", "����" + this.id
												+ ",����״̬��Ϊ�Ƶ��Ĳ����?��");
								return;
							}
							body.push(this.id);
						});

				Ext.Msg.show({
							title : 'ȷ��ɾ���¼',
							msg : 'ɾ��������޷��ָ����Ƿ����?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {

									var r1 = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : "delete",
												body : body
											});
									if (r1.code > 300) {
										this.processReturnMsg(r1.code, r1.msg,
												this.onBeforeSave);
										return false;
									} else {
										// this.fireEvent("winClose", this);
										this.fireEvent("delete", this);
										this.refresh();
									}
								}
							},
							scope : this
						})
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
							// add by huangpf.
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
                var module = this.createModule("noStoreListPrint",
                        this.refNoStoreListPrint)
                var r = this.getSelectedRecord()
                if (r == null) {
                    MyMessageTip.msg("��ʾ", "��ӡʧ�ܣ���Ч�ĳ��ⵥ��Ϣ!", true);
                    return;
                }
                if(r.get("DJZT") == 0){
                    MyMessageTip.msg("��ʾ", "��ӡʧ�ܣ�û����˲��ܴ�ӡ!", true);
                    return;
                }
                module.DJXH = r.data.DJXH;
                module.initPanel();
                module.doPrint();
            }
		})