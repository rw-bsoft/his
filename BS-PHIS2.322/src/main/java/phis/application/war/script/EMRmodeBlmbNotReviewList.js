$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.EMRmodeBlmbNotReviewList = function(cfg) {
	phis.application.war.script.EMRmodeBlmbNotReviewList.superclass.constructor.apply(
			this, [cfg])
	this.autoLoadData = false;
	this.disablePagingTbr = true;
}

Ext.extend(phis.application.war.script.EMRmodeBlmbNotReviewList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				this.requestData.serviceId = "phis.medicalRecordsQueryService";
				this.requestData.serviceAction = "loadBlmb";
				var labelText = new Ext.form.Label({
							text : "科室"
						});
				var depCombo = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.department_zy",
							filter :"['eq',['$','item.properties.ORGANIZCODE'],['s','"+this.mainApp.deptRef+"']]",
//							filter : "['eq',['$map',['s','JGID']],['s','"
//									+ this.mainApp['phisApp'].deptId + "']]",
							width : 100,
							autoLoad : true
						});
				var radioGroup = [{
							xtype : "radio",
							boxLabel : '病历模版',
							inputValue : 1,
							name : 'mbfl',
							clearCls : true,
							checked : true,
							listeners : {
								check : this.radioChange,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '个人模版',
							inputValue : 2,
							name : 'mbfl',
							clearCls : true,
							listeners : {
								check : this.radioChange,
								scope : this
							}
						}];
				this.radioGroup = radioGroup;
				this.depCombo = depCombo;
				this.depCombo.store.on("load", this.depComboLoad, this);
				depCombo.on("select", this.doRefresh, this);
				depCombo.on("change", this.combChange, this);
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(labelText, '-', depCombo, '-', radioGroup, ['-'],
						tbar);
			},
			depComboLoad : function(store) {
				var r1 = new Ext.data.Record({
							key : "0",
							text : "全院科室"
						})
				store.insert(0, [r1]);
			},
			radioChange : function(radio, checked) {
				if (checked) {
					var sslb = radio.inputValue;
					this.sslb = sslb;
					this.doRefresh();
				}
			},
			combChange : function() {
				if (!this.depCombo.getValue()) {
					this.doRefresh();
				}
			},
			selectRow : function(v) {
				if (!this.grid.hidden) {
					this.grid.el.focus()
				}
				try {
					if (this.grid && this.selectFirst) {
						var sm = this.grid.getSelectionModel()
						if (sm.selectRow) {
							sm.selectRow(v)
						}
						if (!this.grid.hidden) {
							var view = this.grid.getView()
							if (this.store.getCount() > 0) {
								view.focusRow(0)
							} else {
								var el = this.grid.el
								setTimeout(function() {
											el.focus()
										}, 300)
							}
						}
					}
				} catch (e) {
				}
			},
			onDblClick : function() {
				this.fireEvent("listDblClick");
			},
			doRefresh : function() {
				var cnd = [];
				if (this.depCombo.getValue() && this.depCombo.getValue() != "0") {
					this.requestData.SSKS = this.depCombo.getValue();
				} else {
					delete this.requestData.SSKS;
				}
				var sslb = this.sslb || 1;
				if (sslb == 1) {
					this.requestData.schema = "phis.application.emr.schemas.V_EMR_BLMB";
				} else {
					this.requestData.schema = "phis.application.emr.schemas.V_EMR_BLMB_PRI";
				}
				this.refresh();
			}
		})