$package("chis.application.per.script.checkup");

$import("chis.script.BizTableFormView",
		"chis.application.mpi.script.CombinationSelect", "util.Accredit");

chis.application.per.script.checkup.CheckupICDForm = function(cfg) {
	cfg.colCount = 3;
	cfg.fldDefaultWidth = 158;
	chis.application.per.script.checkup.CheckupICDForm.superclass.constructor
			.apply(this, [cfg]);
	this.querySchema = "chis.application.pub.schemas.PUB_ICD10";
	this.queryService = "simpleQuery";
};

Ext.extend(chis.application.per.script.checkup.CheckupICDForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.per.script.checkup.CheckupICDForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var icdCode = form.findField("icdCode");
				if (icdCode) {
					icdCode.on("keydown", this.getICD, this);
				}
				var icdName = form.findField("icdName");
				if (icdName) {
					icdName.on("keydown", this.getICD, this);
				}
				var pyCode = form.findField("pyCode");
				if (pyCode) {
					pyCode.on("keydown", this.getICD, this);
				}
			},
			getICD : function(f, e) {
				if (e.getKey() == e.ENTER) {
					e.stopEvent();
					this.doQuery(f);
				}
			},
			doQuery : function(field) {
				var value = field.getValue();
				if (!value) {
					return;
				}
				var queryField;
				var fieldName = field.getName();
				if (fieldName == "icdCode") {
					queryField = "a.icd10";
				} else if (fieldName == "icdName") {
					queryField = "a.diseaseName";
				} else if (fieldName == "pyCode") {
					queryField = "a.pydm";
				}
				this.initQueryCnd = ['like', ['$', queryField],
						['s', value.toUpperCase() + '%']];
				util.rmi.jsonRequest({
							serviceId : this.queryService,
							method : "execute",
							schema : this.querySchema,
							cnd : ['like', ['$', queryField],
									['s', value.toUpperCase() + '%']]
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body && body.length == 0) {
								Ext.Msg.alert("提示", "系统无法检索到对应信息,请确认后重新输入!");
								field.reset();
								return
							}
							if (body && body.length > 0) {
								if (body.length == 1) {
									var data = body[0];
									this.initFormData(this.reMakeData(data));
								} else if (body.length > 1) {
									this.showDataInSelectView(body);
								}
							}
						}, this);

			},
			reMakeData : function(data) {
				var icdData = [];
				icdData.icdCode = data.icd10;
				icdData.icdName = data.diseaseName;
				icdData.pyCode = data.pydm;
				return icdData;
			},
			showDataInSelectView : function(data) {
				var ICDSelectView = this.midiModules["ICDSelectView"];
				if (!ICDSelectView) {
					var ICDSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : this.querySchema,
								disablePagingTbr : false,
								autoLoadData : false,
								enableCnd : false,
								autoLoadSchema : false,
								modal : true,
								title : "选择ICD10记录",
								width : 600,
								initCnd : this.initQueryCnd,
								height : 400
							});
					ICDSelectView.disablePagingTbr = false;
					ICDSelectView.on("onSelect", function(r) {
								var data = r.data;
								this.initFormData(this.reMakeData(data));
							}, this);
					ICDSelectView.initPanel();
					this.midiModules["ICDSelectView"] = ICDSelectView;
				}
				ICDSelectView.initCnd = this.initQueryCnd;
				ICDSelectView.requestData.cnd=this.initQueryCnd;
				ICDSelectView.getWin().show();
				ICDSelectView.loadData();
				// var records = [];
				// for (var i = 0; i < data.length; i++) {
				// var r = data[i];
				// var record = new Ext.data.Record(r);
				// records.push(record);
				// }
				// ICDSelectView.setRecords(records);
			},
			loadData : function() {
				var data = this.selectRecord.data;
				this.initFormData(data);
			},

			doSave : function() {
				if (this.saving) {
					return
				}
				var ac = util.Accredit;
				var form = this.form.getForm();
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items;

				Ext.apply(this.data, this.exContext);

				var icdCode = this.form.getForm().findField("icdCode");

				util.rmi.jsonRequest({
							serviceId : this.queryService,
							schema : this.querySchema,
							method : "execute",
							cnd : ['eq', ['$', "icd10"],
									['s', icdCode.getValue()]]
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body && body.length == 0) {
								Ext.Msg.alert("提示", "系统无法检索到对应信息,请确认后重新输入!");
								return
							} else {
								var data = body[0];
								this.initFormData(this.reMakeData(data));
								if (items) {
									var n = items.length;
									for (var i = 0; i < n; i++) {
										var it = items[i];
										if (this.op == "create"
												&& !ac.canCreate(it.acValue)) {
											continue;
										}
										var v = this.data[it.id];

										if (v == undefined) {
											v = it.defaultValue;
										}
										if (v != null && typeof v == "object") {
											v = v.key;
										}
										var f = form.findField(it.id);
										if (f) {
											v = f.getValue();
											// add by huangpf
											if (f.getXType() == "treeField") {
												var rawVal = f.getRawValue();
												if (rawVal == null
														|| rawVal == "")
													v = "";
											}
											// end
										}

										if (v == null || v === "") {
											if (!it.pkey && it["not-null"]
													&& !it.ref) {
												Ext.Msg.alert("提示", it.alias
																+ "不能为空");
												return;
											}
										}
										values[it.id] = v;
									}
								}

								Ext.apply(this.data, values);
								this.saveToServer(values);

							}
						}, this);

			},
			saveToServer : function(saveData) {
				this.fireEvent("save", saveData, this);
				this.doNew();
			}

		});