$package("phis.application.reg.script")

$import( "phis.script.TableForm")

phis.application.reg.script.FsyyManageForm = function(cfg) {
	cfg.colCount=3;
	cfg.width=340;
	cfg.height=450;
	phis.application.reg.script.FsyyManageForm.superclass.constructor.apply(this,[cfg])
}
Ext.extend(phis.application.reg.script.FsyyManageForm,phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var table = {
					frame : true,
					layout : 'tableform',
					bodyStyle : 'padding:5px 5px 0',
					layoutConfig : {
						columns : 1,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : [],
					buttons : [{
								cmd : 'commit',
								text : '确定',
								handler : this.doSaveCommit,
								scope : this
							}, {
								text : '取消',
								handler : this.doSaveConcel,
								scope : this
							}]
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					f.style += 'font-size:16px;font-weight:bold;color:blue;textAlign:right';
					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					table.items.push(f)
				}

				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					this.width = 360
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.expansion(table);
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)
				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			}
			,doSaveCommit:function(){
				debugger;
				var saveData=this.getFormData();
				var re = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.registeredManagementService",
						serviceAction : "saveYyxx",
						HYXH:saveData.HYXH
					});
				if(re.code>300) MyMessageTip.msg("错误提示",re.msg,true);
				this.refresh();
			}
			,doSaveConcel:function(){
				this.win.hide();
			}
		})