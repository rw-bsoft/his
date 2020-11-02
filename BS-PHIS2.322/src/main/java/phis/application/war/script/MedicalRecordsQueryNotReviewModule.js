$package("phis.application.war.script")

$import("phis.script.SimpleModule");
phis.application.war.script.MedicalRecordsQueryNotReviewModule = function(cfg) {
	phis.application.war.script.MedicalRecordsQueryNotReviewModule.superclass.constructor.apply(
			this, [cfg]);

},

Ext.extend(phis.application.war.script.MedicalRecordsQueryNotReviewModule,
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
							items : [{
										id : "queryNotReviewForm",
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 140,
										items : this.getQueryForm()
									}, {
										id : "listNotReview",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRecordsList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getQueryForm:function(){
				var module = this.createModule("NotReviewQueryForm", this.NotReviewQueryForm);
				this.form = module;
				module.on("select",this.onDoSelect,this);
				module.on("cancel",this.onCancel,this);
				var qForm=module.initPanel();
				this.qForm=qForm;
				return qForm;
			},
			getRecordsList:function(){
				var module = this.createModule("NotReviewRecordsList", this.NotReviewRecordsList);
				this.list = module;
				module.initCnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['ne', ['$', 'BLZT'], ['s', '9']]];
				module.requestData.cnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['ne', ['$', 'BLZT'], ['s', '9']]];
				var grid=module.initPanel();
				this.grid=grid;
				return grid;
			},
			onDoSelect:function(cnd){
				var cnd1=['eq',['$','JGID'],['s',this.mainApp['phisApp'].deptId]]
				if(cnd!=null){
					cnd1=['and',cnd,cnd1];
				}
				this.list.requestData.serviceId="phis.medicalRecordsQueryService";
				this.list.requestData.serviceAction="listMedicalRecords";
				this.list.requestData.cnd = cnd1;
				this.list.requestData.type="1";
				this.list.initCnd = cnd1;
				this.list.loadData();
			},
			onCancel:function(){
				if (this.panel) {
					this.panel.destroy();
				}
			}
		});