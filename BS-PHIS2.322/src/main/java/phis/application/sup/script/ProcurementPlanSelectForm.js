$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")
phis.application.sup.script.ProcurementPlanSelectForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.ProcurementPlanSelectForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.ProcurementPlanSelectForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
                    this.planCheckboxGroup.setValue([false, false, false]);
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : 1,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
							* colCount
					table.layoutConfig.forceWidth = forceViewWidth
				}

				this.planCheckboxGroup = new Ext.form.RadioGroup({
						fieldLabel : '计划方法：',
						name : 'planCheckboxGroup',
						columns : 1,
						items : [{
									boxLabel : '未确认申领单',
									name : 'planCheckboxGroup',
									inputValue : 1
								}, {
									boxLabel : '补足到高储',
									name : 'planCheckboxGroup',
									inputValue : 2
								}, {
									boxLabel : '补足到低储',
									name : 'planCheckboxGroup',
									inputValue : 3
								}]
						});

				table.items.push(this.planCheckboxGroup)

				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoHeight : true,
					autoScroll : true,
					floating : false
				}

				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					cfg.height = this.height
				} else {
					cfg.autoHeight = true
				}
				if (this.disAutoHeight) {
					delete cfg.autoHeight;
				}
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.expansion(table);
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			doCommit : function() {
				if (!this.planCheckboxGroup.getValue()) {
					Ext.Msg.alert("提示", "请选择计划方法");
					return;
				}
				var records = {};
				records['' + this.planCheckboxGroup.getValue().inputValue + ''] = this.planCheckboxGroup
						.getValue().inputValue;
				this.fireEvent("checkData", records);

			},
			doClose : function() {
				this.fireEvent("winClose", this);
			}
		})