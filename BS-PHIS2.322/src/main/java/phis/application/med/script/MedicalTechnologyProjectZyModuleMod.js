$package("phis.application.med.script");
$import("phis.script.SimpleModule", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil");
		phis.application.med.script.MedicalTechnologyProjectZyModuleMod = function(
				cfg) {
			phis.application.med.script.MedicalTechnologyProjectZyModuleMod.superclass.constructor
					.apply(this, [ cfg ]);
		},

		Ext
				.extend(
						phis.application.med.script.MedicalTechnologyProjectZyModuleMod,
						phis.script.SimpleModule, {
							initPanel : function() {
								if (this.panel) {
									return this.panel;
								}
								var panel = new Ext.Panel({
									border : false,
									width : this.width,
									height : this.height,
									frame : true,
									layout : 'border',
									defaults : {
										border : false
									},
									items : [ {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										width : 960,
										height : 250,
										items : this.getUpList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getDowList()
									} ],
									tbar : (this.tbar || []).concat(this
											.createButtons())
								});
								this.panel = panel;
								return panel;
							},
							getUpList : function() {
								this.uplist = this.createModule("uplist",
										this.refUplist);
								this.uplist.oper=this;
								this.uplist.on("queryProject", this.onQueryProject,
										this);
								return this.uplist.initPanel();
							},
							getDowList : function() {
								this.dowlist = this.createModule("dowlist",
										this.refDowList);
								return this.dowlist.initPanel();
							},
							loadData : function(strdate, enddate, carno) {
								this.uplist.loadData(strdate, enddate, carno);
							},
							onQueryProject : function() {
								var r = this.uplist.getSelectedRecord()
								if (r == null) {
									this.dowlist.loadData(0);
									return
								}
								this.dowlist.loadData(r.get("YJXH"));
							},
							doCancel:function(){
								this.uplist.cancel();
							}
						});