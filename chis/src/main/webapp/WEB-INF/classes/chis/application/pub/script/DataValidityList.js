/**
 * @author : yaozh
 */
$package("chis.application.pub.script")
$import("chis.script.BizSimpleListView")
chis.application.pub.script.DataValidityList = function(cfg) {
	this.cfg = cfg;
	chis.application.pub.script.DataValidityList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.pub.script.DataValidityList,
		chis.script.BizSimpleListView, {
			doMatching : function() {
				var cndValue = this.cndField.getValue();
				var module = this.createSimpleModule("s08form",
						"chis.application.pub.PUB/DV/DV02");
				module.doNew();
				module.isUnUseAble = false;
				module.on("save", this.onSave, this);
				module.cndValue = cndValue;
				this.showWin(module);
			},
			doQuality : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				var registerId = r.get("registerId");
				var status = r.get("validityFlag");
				if (status == 1 || status == 2) {
					Ext.MessageBox.alert("提示", "该数据已处理.");
				} else {
					var yes = Ext.MessageBox.buttonText.yes;
					var no = Ext.MessageBox.buttonText.no;
					Ext.MessageBox.buttonText.yes = '有效';
					Ext.MessageBox.buttonText.no = '无效';
					var box=Ext.Msg.show({
						title : '提示',
						msg : '请确认当前数据的有效性!',
						width : 300,
						scope :this,
						buttons : Ext.Msg.YESNOCANCEL,
						fn : function(b) {
							// Ext.MessageBox.buttonText.yes = yes;
							// Ext.MessageBox.buttonText.no = no;
							if (b == "yes" || b == "no") {
								util.rmi.jsonRequest({
											serviceId : 'chis.gpsDataValidityService',
											serviceAction : "qualityMatch",
											method : "execute",
											registerId : registerId,
											ops : b
										}, function(code, msg, json) {
											if (this.form && this.form.el) {
												this.form.el.unmask();
											}
											this.loading = false;
											if (code > 300) {
												this.processReturnMsg(code,
														msg, this.onWinShow);
												return;
											}
											if (json.body) {
												var data = json.body;
												if (data.msg) {
													this.refresh();
													Ext.MessageBox.alert("提示",
															data.msg);
												}
											}
										}, this);
							}
						}
					},this);
				}
			},
			onSave : function() {
				this.refresh();
			},
			onDblClick : function() {
				// this.doMatching();
			}
		});
