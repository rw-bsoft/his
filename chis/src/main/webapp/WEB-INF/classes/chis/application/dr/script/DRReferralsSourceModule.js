$package("chis.application.dr.script")

$import("chis.script.BizModule", "chis.script.BizCommon")

chis.application.dr.script.DRReferralsSourceModule = function(cfg) {
	this.width =999;
	this.height =666;
	Ext.apply(cfg, chis.script.BizCommon);
	chis.application.dr.script.DRReferralsSourceModule.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(chis.application.dr.script.DRReferralsSourceModule,
	chis.script.BizModule,{
		initPanel : function() {
			var formPanel = this.createTopFormPanel();
			var listPanel = this.createTabPanel();
			listPanel.split = true;
			listPanel.region = "center";
			var panel = new Ext.Panel( {
				resizable : true,
				autoScroll : true,
				frame : false,
				layout : 'form',
				width : this.width,
				height : this.height,
				labelAlign : "right",
				items : [formPanel, listPanel]
			});
			this.panel = panel;
			this.setModel(this);
			this.doNew();
			return this.panel;
		},
		onCancel : function(delPastId) {
			this.getWin().hide();
		},
		createTopFormPanel : function() {
			var form = this.midiModules["refForm"];
			if (!form) {
				$import("chis.application.dr.script.DRReferralsForm");
				form = new chis.application.dr.script.DRReferralsForm({
							isCombined : true,
							entryName : this.entryName,
							empiId : this.empiId,
							actions : []
						});
				this.midiModules["refForm"] = form;
			} else {
				form.empiId = this.empiId;
			}
			this.form = form;
			form.on("cancel", this.onCancel, this);
			return form.initPanel();
		
//			var form;
//			var ref = this.refForm;
//			var cfg = {
//				autoScroll : true,
//				autoHeight:true,
//				layout : "form",
//				title : '',
//				region : 'north',
//				isCombined : false,
//				autoLoadSchema : false,
//				autoLoadData : false
//			};
//			var result = util.rmi.miniJsonRequestSync( {
//					url : "app/loadModule",
//					id : ref
//			});
//			if (result.code == 200) {
//				Ext.apply(cfg, result.json.body);
//			}
//			var cls = cfg.script;
//			if (!cls) {
//				return;
//			}
//			$import(cls);
//			$require(cls, [ function() {
//				form = eval("new " + cls + "(cfg)")
//				form.setMainApp(this.mainApp);
//			}, this ]);
//			form.setParent(this);
//			this.topPanel = form;
//			var formPanel = form.initPanel();
//			return formPanel;
		},
		createCenterListPanel : function() {
			if (arguments == null || arguments.length == 0) {
				return;
			}
			var ref = arguments[0];
			var cfg = {
//							autoScroll : true,
				autoWidth : true,
				autoHeight : true,
				isCombined : false,
				autoLoadSchema : false,
				autoLoadData : false
			};
			if (ref) {
				var result = util.rmi.miniJsonRequestSync( {
					url : "app/loadModule",
					id : ref
				});
				if (result.code == 200) {
					Ext.apply(cfg, result.json.body);
				}
			}
			var cls = cfg.script
			if (!cls) {
				return;
			}
			var list;
			$import(cls)
			$require(cls, [ function() {
				list = eval("new " + cls + "(cfg)");
				list.setMainApp(this.mainApp);
			}, this ]);
			list.getSummary();
			list.setParent(this);
			this.htmlSource = list;
			return list.initPanel();
		},
		loadData : function() {
			if (this.loading) {
				return
			}
	
			if (!this.schema) {
				return
			}
			if (!this.initDataId) {
				return;
			}
			if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId)) {
				return
			}
			if (this.form && this.form.el) {
				this.form.el.mask("正在载入数据...", "x-mask-loading")
			}
			this.loading = true
			util.rmi.jsonRequest({
					method:"execute",
					serviceId : this.loadServiceId,
					schema : this.entryName,
					action : this.loadAction,
					pkey : this.initDataId
				}, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData)
						return
					}
					if (json.body) {
//						console.dir(json.body)
						this.doNew()
						this.initFormData(json.body)
						this.fireEvent("loadData", this.entryName, json.body);
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)// jsonRequest
			},
			createTabPanel : function() {
				var tab = new Ext.TabPanel( {
					 height:this.height-281,
					 minHeight:262,
					activeTab : 0,
					items : [],
					enableTabScroll : true
				});
				var panel = this.createCenterListPanel(this.refLists);
				panel.title = "转诊资源";
				tab.add(panel);
				this.tabPanel = tab;
				return tab;
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window( {
						id : this.id,
						constrain : true,
						title : this.title,
						draggable : true,
						width : this.width,
						height : this.height,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrainHeader : true,
						minimizable : false,
						resizable : false,
						shadow : false,
						modal : true,
						closeAction : 'hide',
						items : this.initPanel()
					});
					this.win = win;
				}
				this.topPanel.onlyResetForm();
				this.htmlSource.getSummary();
				return win;
			},
			resize : function() {
				var height = document.body.clientHeight;
				var width = document.body.clientWidth;
			},
			doNew : function() {
				this.topPanel.doNew();
				if (this.getQueryStringArgs()["mpiId"]) {
					var mpiId = this.getQueryStringArgs()["mpiId"]
					var data = this.getMPI(mpiId);
					if (data != null && data.length != 0) {
						this.topPanel.setFormData( {
							data : data
						})
					}
				}
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			},
			setModel : function(model) {
				window.DRReferralsSourceModule = model;
			},
			getMPI : function(mpiId) {
				var result = util.rmi.miniJsonRequestSync( {
					serviceId : this.servicedId,
					action : "getMPIDataById",
					body : {
						mpiId : mpiId
					}
				});
	
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				return result.json.body;
			},
			getQueryStringArgs : function() {
				var qs = (location.search.length > 0 ? location.search
						.substring(1) : "");
				var args = {};
				var items = qs.split("&");
				var item = null;
				var name = null;
				var value = null;
				for ( var i = 0; i < items.length; i++) {
					item = items[i].split("=");
					name = decodeURIComponent(item[0]);
					value = decodeURIComponent(item[1]);
					args[name] = value
				}
				return args;
			}
	
		});