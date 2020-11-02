/**
 * 未完成：①查询时右边list未刷新②审核结果选择后未修改对应record③保存按钮未实现
 * @param {} cfg
 */
$package("phis.application.pha.script")
$import("phis.script.EditorList")

phis.application.pha.script.PharmacyPrescriptionAuditEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.proposalTitle = '审方意见维护';
	cfg.disablePagingTbr = true;
	cfg.entryName="phis.application.pha.schemas.YF_CF02_SH";
	cfg.serviceId="phis.pharmacyManageService";
	cfg.serviceAction="queryAuditPrescriptionDetail"; 
	cfg.adoptAllAction="updateToAdoptAll"; 
	cfg.auditAction="auditPrescription";  
	phis.application.pha.script.PharmacyPrescriptionAuditEditorList.superclass.constructor.apply(this, [cfg]);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this); 
}
Ext.extend(phis.application.pha.script.PharmacyPrescriptionAuditEditorList,phis.script.EditorList, {
			auditRender : function(value, metadata, record, rowIndex, colIndex, store) {
				var v = ''; 
				var key=Number(value);
				switch(key) {
					case 1:
						v = '通过'
					break;
					case 2:
						v = '未过'
					break;
					case 0:
						v = '未审'
					break;
					case null:
						v = '未审'
					break;
				} 
				return v;
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='pharmacy_prescription_audit_editor_list_"
							+ this.summaryBar
							+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "合计金额：0.00&nbsp;&nbsp;￥</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			beforeCellEdit : function(e) {
				e.cancel = true;
				var record = e.record;
				var field = e.field;
				var value = e.value;
				if(field == 'SFYJ'){
					this.quoteAuditProposal(e);
				}
				if(field == 'SFJG' && (value == null || value == 0 || record.dirty)) {
					var column = e.column;
					var cm = this.grid.getColumnModel();
					cm.setEditor(column, new Ext.form.ComboBox({
						hiddenName : 'sfjg',
						store : new Ext.data.SimpleStore({
						   fields : ['sfjg', 'text'],
						   data : [[this.passAudit, '通过'], [this.unpassAudit, '未过'], [this.unAudit, '未审']]
						}),
						triggerAction : 'all',
						displayField : 'text',
						valueField : 'sfjg',
						mode : 'local', 
						editable : false,
						forceSelection : true
					}));
					e.cancel = false;
				}
			},
			//同步审核结果
			synAuditResult : function(sfjgVal, sfyjVal) {
				var count = this.store.getCount();
				if(count != 0) {
					for(var i = 0; i < count; i++) {
						var r = this.store.getAt(i);
						r.set('SFJG', sfjgVal);
						r.set('SFYJ', sfyjVal);
					}
				}
			},
			//引用审核意见
			quoteAuditProposal : function(e) {
				var win = this.win;
				var record = e.record;
				var closeAction = "hide"; 
				if(!this.module) { 
					this.module = this.createModule('auditProposalWin', this.auditProposal);
				}
				if (!win) {
					win = new Ext.Window({
						title : this.proposalTitle,
						width : this.width,
						height : 500,
						buttonAlign : 'center',
						buttons : [{
							text : '确认',
							iconCls : 'commit',
							handler : function() {
								if(this.module.getValueLength() > 255) {
									this.module.textArea.markInvalid('审核意见文字长度过长,请重输!');
									return;
								}
								//同步处方明细审核意见 
								this.synAuditResult(record.get('SFJG'), this.module.getValue())
								win.hide();
							},
							scope : this
						},'-',{
							text : '关闭',
							iconCls : 'common_cancel',
							handler : function() {
								win.hide();
							},
							scope : this
						}],
						iconCls : 'icon-grid',
						layout : "fit",
						closeAction : closeAction,
						animCollapse : true,
						resizable : false,
						shadow : false,
						modal : true,
						items : this.module.initPanel()
					});
				}
				win.on("beforehide", function() {
					this.module.reset();
				}, this);
				if((record.get('SFJG') == 2 && record.dirty)) {
					this.module.enableSelectValue(true);
					this.module.textArea.setDisabled(false);
					win.buttons[0].setVisible(true);
				} else {
					this.module.enableSelectValue(false);
					this.module.textArea.setDisabled(true);
					win.buttons[0].setVisible(false);
				}
				this.module.setValue(record.get('SFYJ'));
				win.show();
				this.win = win;
			},
			afterCellEdit : function(e) {
				var record = e.record;
				var field = e.field;
				var value = e.value;
				var sfjg = record.get('SFJG');
				var sfyj = record.get('SFYJ');
				if(sfjg == 2) {
					this.quoteAuditProposal(e);
				}
				if(sfjg!=2){//如果是通过或者未审,重置审方意见
				record.set("SFYJ","");
				sfyj="";
				}
				this.synAuditResult(sfjg, sfyj);
			},
			loadData : function(data) {
				this.data = data;
				var cnd = ['and',
						['eq',['$','a.CFSB'],['s',data['CFSB']]],
						['eq',['$','a.JGID'],['s',data['JGID']]]
				];
				this.status = data['sfjg'];
				this.requestData.body = {
					'cfsb' : data['CFSB'],
					'jgid' : data['JGID'],
					'yfsb' : data['YFSB'],
					'sfjg' : data['sfjg']
				}
				this.requestData.cnd = cnd;
				this.requestData.serviceId=this.serviceId;
				this.requestData.serviceAction=this.serviceAction;
				phis.application.pha.script.PharmacyPrescriptionAuditEditorList.superclass.loadData.call(this);
			},
			allAdopt : function(opener) {
				var totalCount =this.store.getCount();
				if(totalCount < 1) {
					MyMessageTip.msg("提示", "找不到未审核的处方信息!", true);
					return;
				}
				if(this.status != 0) {
					MyMessageTip.msg("提示", "全部通过只适用于未审核的处方信息!", true);
				} else {
					Ext.Msg.show({
						title : '提示信息',
						msg : '确认审核通过已选病人的全部未审核处方信息?',
						modal : true,
						minWidth : 300,
						maxWidth : 600,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								this.mask("正在执行操作...");
								var count = this.store.getCount();
								var body=[];
								for(var i = 0; i < count; i++) {
									var r = this.store.getAt(i);
									body.push(r.data);
								}
								phis.script.rmi.jsonRequest({
									serviceId : this.serviceId,
									serviceAction : this.adoptAllAction,
									body : body
								}, function(code, msg, json) {
									this.unmask();
									if (code > 300) {
										this.processReturnMsg(code, msg);
										return;
									} else {
										opener.doRefresh();
									}
								}, this);
							}
							return;
						},
						scope : this
					})
				}
			},
			save : function(opener) {
				this.mask("正在保存数据...","x-mask-loading");
				var count = this.store.getCount();
				if(count==0){
					this.unmask();
					return;
				}
				var body=[];
				var dirtyRows = [];
				for(var i = 0; i < count; i++) {
					var r = this.store.getAt(i);
					if (r.dirty) {
						body.push(r.data);
						if(r.get('SFJG') ==2 && Ext.isEmpty(r.get('SFYJ'), false)) {
							dirtyRows.push(i + 1);
						}
					}
				}
				if(body.length == 0) {
					MyMessageTip.msg("提示", '处方审核数据没有改变!', true);
					this.unmask();
					return;
				} else {
					if(dirtyRows > 0) {
						MyMessageTip.msg("提示", '第[' + dirtyRows +']行审核未通过处方信息对应审核意见为空!', true);
						this.unmask();
						return;
					}
				}
				phis.script.rmi.jsonRequest({
					serviceId: this.serviceId,
					serviceAction : "saveAuditPrescription",
					op : this.op,
					schema : this.entryName,
					body: body
				},
				function(code,msg,json){
					this.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg, this.doSave);
						return;
					}
					opener.doRefresh();
					this.fireEvent("save",this);
				}, this);
			},
			setCountInfo : function() {
				var totalMoney = 0;
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					var hjje = parseFloat(r.get("HJJE"));
					totalMoney += parseFloat(hjje);
					if (isNaN(totalMoney)) {
						totalMoney = 0;
					}
				}

				document.getElementById("pharmacy_prescription_audit_editor_list_" + this.summaryBar).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "合计金额："
						+ parseFloat(totalMoney).toFixed(4) + "&nbsp;&nbsp;￥";
			},
			onStoreLoadData : function(store, records, ops) {
				this.setCountInfo();
			}
		});