$package("phis.application.sup.script")
$import("phis.script.common", "phis.script.TableForm")

phis.application.sup.script.MeasuringInstrumentsform = function(cfg) {
	phis.application.sup.script.MeasuringInstrumentsform.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinshows, this);
}
Ext.extend(phis.application.sup.script.MeasuringInstrumentsform,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var _layoutConfig = {
					columns : 3,
					tableAttrs : {
						border : 0,
						cellpadding : '2',
						cellspacing : "2"
					}
				};
				this.form = new Ext.FormPanel({
							labelWidth : 80, // label settings here cascade
							// unless overridden
							frame : true,
							// autoScroll : true,
							defaultType : 'fieldset',
							items : [{
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : _layoutConfig,
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										title : '计量信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : _layoutConfig,
										defaultType : 'textfield',
										items : this.getItems('JLXX')
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())

						});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this)
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
				this.schema = schema;
				return this.form
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				this.doNew();
				var form = this.form.getForm();
				var WZMC = form.findField("WZMC");
				WZMC.setValue(this.djxx.get("WZMC"));
				var WZGG = form.findField("WZGG");
				WZGG.setValue(this.djxx.get("WZGG"));
				var WZDW = form.findField("WZDW");
				WZDW.setValue(this.djxx.get("WZDW"));
				var CJMC = form.findField("CJMC");
				CJMC.setValue(this.djxx.get("CJMC"));
				var WZDJ = form.findField("WZDJ");
				WZDJ.setValue(this.djxx.get("WZJG"));
				var SYSL = form.findField("SYSL");
				SYSL.setValue(this.djxx.get("SL"));
			},
			onReady : function() {
				phis.application.sup.script.MeasuringInstrumentsform.superclass.onReady
						.call(this);

				var form = this.form.getForm();
				this.JDZQ = form.findField("JDZQ");
				this.WZSL = form.findField("WZSL");
				if (this.JDZQ) {
					this.JDZQ.on("blur", this.onJDZQBlur, this);
				}
				if (this.WZSL) {
					this.WZSL.on("blur", this.onWZSLBlur, this);
					// this.WZSL.on("focus", this.onWZSLFocus, this);
				}
			},
			onWinshows : function() {
				this.onReady();
			},
			onJDZQBlur : function() {
				var form = this.form.getForm();
				var m = this.JDZQ.getValue();
				var strDate = new Date().format('Y-m-d');
				var now = new Date(strDate.replace(/\-/g, "/"));
				var perMonth = new Date(now.setMonth(now.getMonth() + m));
				var yearmonth = perMonth.format('Y-m');
				yearmonth = yearmonth + "-01";
				this.XCJD = form.findField("XCJD");
				this.XCJD.setValue(yearmonth)
			},
			onWZSLBlur : function() {
				var form = this.form.getForm();
				this.SYSL = form.findField("SYSL");
				this.SYSL.setValue(this.djxx.get("SL"));
				var wzsl = parseInt(this.WZSL.getValue());
				var sysl = parseInt(this.SYSL.getValue());
// if (sysl - wzsl < 0) {
// MyMessageTip.msg("提示", "物资数量不能大于剩余数量!", true);
// this.SYSL.setValue(sysl)
// }
				this.SYSL.setValue(sysl - wzsl)
			},
			onWZSLFocus : function() {
				var form = this.form.getForm();
				this.SYSL = form.findField("SYSL");
				this.SYSL.setValue(this.syslls)
			},
			doClose : function() {
				this.win.hide();
			},
			doSave : function() {
				var form = this.form.getForm();
				var wzsl = 1;
				var body={
					"DWXH":form.findField("DWXH").getValue(),
					"BGGH":form.findField("BGGH").getValue(),
					"CCBH":form.findField("CCBH").getValue(),
					"CCRQ":form.findField("CCRQ").getValue(),
					"GRRQ":form.findField("GRRQ").getValue(),
					"GRGH":form.findField("GRGH").getValue(),
					"JLQJFL":form.findField("JLQJFL").getValue(),
					"JLLB":form.findField("JLLB").getValue(),
					"CLFW":form.findField("CLFW").getValue(),
					"ZQDJ":form.findField("ZQDJ").getValue(),
					"FDZ":form.findField("FDZ").getValue(),
					"JDZQ":form.findField("JDZQ").getValue(),
					"XCJD":form.findField("XCJD").getValue(),
					"DDMC":form.findField("DDMC").getValue(),
					"SJQD":form.findField("SJQD").getValue(),
					"WZXH":this.djxx.get("WZXH"),
					"CJXH":this.djxx.get("CJXH"),
					"KSDM":this.djxx.get("KSDM"),
					"WZDW":this.djxx.get("WZDW"),
					"WZDJ":this.djxx.get("WZJG")
				};
				wzsl = form.findField("WZSL").getValue();
				if(!wzsl){
					MyMessageTip.msg("提示", "登记数量不能为空!", true);
					return;
				}
				if(!form.findField("JLQJFL").getValue()){
					MyMessageTip.msg("提示", "计量器具分类不能为空!", true);
					return;
				}
				if(!form.findField("JLLB").getValue()){
					MyMessageTip.msg("提示", "计量类别不能为空!", true);
					return;
				}
				var wzsl1 = parseInt(form.findField("WZSL").getValue());
				var sysl = this.djxx.get("SL");
				if (sysl - wzsl1 < 0) {
					MyMessageTip.msg("提示", "物资数量不能大于剩余数量!", true);
				} else {
					if (form.findField("QJBZ").getValue() == true) {
						body['QJBZ'] = 1;
					} else {
						body['QJBZ'] = 0;
					}
					var r1save = phis.script.rmi.miniJsonRequestSync({
								serviceId : "equipmentWeighingManagementService",
								serviceAction : "saveJLXX",
								body : body,
								op : "create",
								wzsl : wzsl
							});
					if (r1save.code > 200) {
						MyMessageTip.msg("提示", "保存失败!", true);
					} else {
						MyMessageTip.msg("提示", "保存成功!", true);
						this.doClose();
						this.oper.loadData();
					}
				}
			}
		})