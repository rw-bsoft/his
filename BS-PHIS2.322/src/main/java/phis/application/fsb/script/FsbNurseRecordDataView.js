$package("phis.application.fsb.script");

$import("phis.script.TableForm", "phis.script.rmi.miniJsonRequestSync",
		"phis.script.util.DateUtil", "phis.script.widgets.DatetimeField");

phis.application.fsb.script.FsbNurseRecordDataView = function(cfg) {
	cfg.colCount = 1;
	// this.serverParams = {
	// serviceAction : cfg.serviceAction
	// };
	// cfg.height=200;
	cfg.autoHeight = false,
	// cfg.isCombined=true;
	cfg.disAutoHeight = true;
	phis.application.fsb.script.FsbNurseRecordDataView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FsbNurseRecordDataView,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.fsb.script.FsbNurseRecordDataView.superclass.onReady
						.call(this);
				this.form.getForm().findField("HLRQ").setValue(Date
						.getServerDate());
			},
			initPanel : function(sc) {
				this.zyh = sc.zyh;
				this.brbqdm = sc.brbqdm;
				this.param = {
					'ZYH' : this.zyh,
					'BRBQDM' : this.brbqdm
				};
				var grid = phis.application.fsb.script.FsbNurseRecordDataView.superclass.initPanel
						.call(this);
				return grid;
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				values["ZYH"] = this.zyh;
				Ext.apply(this.data, values);
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "fsbNurseRecordService",
							serviceAction : "saveHLJL",
							body : values
						});
				if (result.code == 200) {
					MyMessageTip.msg("提示", '保存成功!', true);
					// Ext.Msg.alert('状态:', "保存成功!");
					this.fireEvent("refreshTree", this);
					// 新增完后清空提交表单
					this.addJl02();
				} else {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
			},
			/**
			 * 选择左边树列表中的叶子节点后显示在右边窗口中的护理记录录入
			 * 
			 * @param node
			 */
			doNodeClick : function(node) {
				this.initDataId = node.attributes.id;
				this.op = "update";
				this.loadData();
			},
			/**
			 * 将服务端返回的json数据插入到store中
			 * 
			 * @param json
			 */
			/**
			 * 新增
			 */
			addJl02 : function() {
				this.doNew();
				this.form.getForm().findField("HLRQ").setValue(Date
						.getServerDate());
				this.param = {
					'ZYH' : this.zyh
				};
			},
			/**
			 * 改变病人信息
			 * 
			 * @param data
			 */
			changeBrInfo : function(data) {
				// {'zyh': this.zyh, 'brbqdm' : this.brbqdm}
				this.zyh = data.zyh;
				this.brbqdm = data.brbqdm;
				this.param = {
					'ZYH' : this.zyh,
					'BRBQDM' : this.brbqdm
				};
			},
			doInportHLJH : function() {
				this.list = this.createModule(this.NPList, this.NPList);
				this.list.opener = this;
				// list.on("refreshTree", this.refreshTree, this);
				var win = this.list.getWin();
				win.setWidth(650);
				win.add(this.list.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
				}
			}
		});