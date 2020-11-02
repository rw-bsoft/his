/**
 * ���ʳ���ȷ�ϣ�������
 * 
 * @author gaof
 */
$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.ConsumptionMaterialsOutList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.ConsumptionMaterialsOutList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.sup.script.ConsumptionMaterialsOutList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = "getCK01Info";
				this.requestData.DJZT = 2;
				this.requestData.JZRQQ = this.dateFrom.getValue()
						.format('Y-m-d');
				this.requestData.JZRQZ = this.dateTo.getValue().format('Y-m-d');

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
			// ������ڿ�
			getCndBar : function(items) {
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
							text : "��������:"
						})
				this.dateFrom = new Ext.form.DateField({
							name : 'ConsumptionMaterialsOutListdateFrom',
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
							name : 'ConsumptionMaterialsOutListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '����ʱ��'
						});
				return ["<h1 style='text-align:center'>�Ѽ��˳��ⵥ:</h1>", '-',
						datelable, this.dateFrom, tolable, this.dateTo, '-']
			},
			// ˢ��ҳ��
			doRefreshWin : function() {
				this.requestData.DJZT = 2;
				this.requestData.JZRQQ = this.dateFrom.getValue()
						.format('Y-m-d');
				this.requestData.JZRQZ = this.dateTo.getValue().format('Y-m-d');
				this.refresh();
				return;
			},
			// �鿴
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.storageOfMaterialsModule = this.createModule(
						"storageOfMaterialsModule", this.addRef);
				this.storageOfMaterialsModule.on("save", this.onSave, this);
				this.storageOfMaterialsModule
						.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.storageOfMaterialsModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.storageOfMaterialsModule.changeButtonState("new");
				} else if (djzt == 1) {
					this.storageOfMaterialsModule.changeButtonState("verified");
				} else if (djzt == 2) {
					this.storageOfMaterialsModule.changeButtonState("commited");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageOfMaterialsModule.op = "update";
					this.storageOfMaterialsModule.initDataBody = initDataBody;
					this.storageOfMaterialsModule.loadData(initDataBody);
				}

			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "�鿴";
				item.cmd = "look";
				this.doAction(item, e)

			},
			onClose : function() {
				this.getWin().hide();
			},
			doCancel : function() {
				if (this.storageModule) {
					return this.storageModule.doClose();
				}
			},
			onSave : function() {
				this.fireEvent("save", this);
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
				if (r.get("DJZT") == 0) {
					MyMessageTip.msg("��ʾ", "��ӡʧ�ܣ�û����˲��ܴ�ӡ!", true);
					return;
				}
				module.DJXH = r.data.DJXH;
				module.initPanel();
				module.doPrint();
			}
		})