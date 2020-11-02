$package("chis.application.tr.script.tprc");

$import("chis.script.BizCombinedModule2");

chis.application.tr.script.tprc.TumourPatientVisitModule = function(cfg){
	chis.application.tr.script.tprc.TumourPatientVisitModule.superclass.constructor.apply(this,[cfg]);
	this.itemWidth = 180;
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientVisitModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.tr.script.tprc.TumourPatientVisitModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.tpList = this.midiModules[this.actions[0].id];
		this.grid = this.tpList.grid;
		this.tpList.on("noVisitPlan", this.onNoVisitPlan, this);
		this.tpList.on("loadData", this.onLoadGridData, this);
		this.grid.on("rowClick", this.onRowClick, this);
		this.form=this.midiModules[this.actions[1].id];
		this.form.on("save",this.onSaveAfter,this);
		return panel;
	},
	loadData : function(){
		this.tpList.firstLoad = true;
		Ext.apply(this.tpList.exContext,this.exContext);
		Ext.apply(this.form.exContext,this.exContext);
		this.tpList.loadData();
		//this.form.loadData();
	},
	onNoVisitPlan : function(){
		Ext.Msg.alert("提示信息", "没有计划,无需随访!");
		this.form.form.disable();
	},
	onLoadGridData : function(store){
		if (store.getCount() == 0) {
	    	if(this.exContext.ids.status == '0'){
           		Ext.Msg.alert("提示信息", "没有计划,无需随访!");
	    	}
            this.activeModule(0);
	    	this.visitForm.exContext.args={};
            this.form.form.disable();
            this.exContext.control = this.getFrmControl(null,null);
            return;
        }else{
        	this.form.form.enable();
        }
        var index = this.tpList.selectedIndex;
        if(!index){
        	index = 0;
        }
        this.selectedIndex = index;
        this.tpList.selectRow(index);
        var r = store.getAt(index);
       // this.process(r, index);
	},
	onRowClick : function(grid, index, e){
		if (!this.tpList) {
           return;
       }
       var r = this.tpList.grid.getSelectionModel().getSelected();
       if (!r) {
           return;
       }
       this.selectedIndex = index;
	   this.process(r, index);
	},
	process : function(r, index) {
      // ****获取下次随访记录,便于计算下次随访预约时间****
      var nextIndex = 0;
      if (!index){
          nextIndex = 1;
      }else{
          nextIndex = index + 1;
      }
      var nextPlan = this.grid.store.getAt(nextIndex);
      var nextDateDisable = false;
      if (nextPlan) {
        var nextStatus = nextPlan.get("planStatus");
        if (nextStatus == '0') {
            this.exContext.args.nextMinDate = nextPlan.get("beginDate");
            this.exContext.args.nextMaxDate = nextPlan.get("endDate");
            this.exContext.args.nextPlanId = nextPlan.get("planId");
        }else{
            nextDateDisable = true;
        }
      }
      
      this.exContext.args.nextRemindDate = r.get("beginVisitDate");
      this.exContext.args.nextDateDisable = nextDateDisable;
      this.exContext.args.selectedPlanId = r.get("planId");
      this.exContext.args.empiId = r.get('empiId');
      this.exContext.args.TPRCID = r.get('recordId');
      this.exContext.args.planDate = r.get("planDate");
      this.exContext.args.beginDate = r.get("beginDate");
      this.exContext.args.endDate = r.get("endDate");
      this.exContext.args.planId = r.get("planId");
      this.exContext.args.visitId = r.get("visitId");
      this.exContext.args.sn = r.get("sn");
      this.exContext.args.planStatus = r.get("planStatus");
      this.exContext.args.fixGroupDate = r.get("fixGroupDate");
      Ext.apply(this.tpList.exContext,this.exContext);
      
      this.exContext.control = this.getFrmControl(r.get("planId"),r.get("visitId"));
      
      var status = r.get("planStatus");
      if (status == "1" || status == "2") {// 已访、失访
      	//第一个可用其他不可用
          this.form.form.enable();
          this.initVisitFormData("update");
          return;
      }else if (status == "3") {// 未访
         Ext.Msg.alert("提示", "本次计划未访，无法进行补录！");
         this.form.form.disable();
         this.initVisitFormData("update");
         return;
      }else if(status == "8"){
      	 Ext.Msg.alert("提示", "该计划已结案,无须再随访！");
        this.form.form.disable();
         this.initVisitFormData("update");
         return;
      }else if(status == "9"){
      	 Ext.Msg.alert("提示", "该计划已注销,无法进行操作！");
        this.form.form.disable();
         this.initVisitFormData("update");
         return;
      } else {// 过访,应访
      	 var beginDate =  Date.parseDate(r.get("beginDate"), "Y-m-d");;
      	 //var endDate = Date.parseDate(r.get("planId"),"Y-m-d");
      	 var nowDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");

         if (beginDate <= nowDate) {
              this.beginVisitDate = r.get("beginVisitDate");
               this.form.form.enable();
              this.initVisitFormData("create");
              return;
          } else if (beginDate > nowDate) {
              this.form.form.disable();
              Ext.Msg.alert("提示", "未到随访时间,无法进行随访!");
              this.exContext.args.nonArrivalDate = true;
              this.initVisitFormData("update");
              return;
          } else {
              this.beginVisitDate = r.get("beginVisitDate");
              this.form.form.enable();
              this.initVisitFormData("create");
              return;
          }
      }
	},
	getFrmControl : function(planId,visitId){
    	var body = {};
    	body.TPRCID = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
    	body.empiId = this.exContext.ids.empiId;
    	body.planId = planId || null;
    	body.visitId = visitId || null;
    	this.panel.el.mask("正在获取操作权限...", "x-mask-loading");
    	var result = util.rmi.miniJsonRequestSync({
    		serviceId : "chis.tumourPatientVisitService",
			serviceAction : "getTPVisitControl",
			method:"execute",
			body : body
    	});
    	this.panel.el.unmask();
		if (result.code != 200) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		return result.json.body;
    },
    initVisitFormData : function(op){
		if (!this.form) {
			return;
		}
		Ext.apply(this.form.exContext,this.exContext);
		this.form.op = op;
		this.form.initDataId = this.exContext.args.visitId;
		if(op == "create"){
			this.form.doNew();
		}
		if(op == "update"){
			this.form.loadData();
		}
		this.form.initFormSet();
	},
	onSaveAfter : function(entryName,op,json,data){
		this.tpList.refresh();
		var body = json.body
		if(body){
			var control = body.control;
			if(control){
				this.exContext.control = control;
				this.form.exContext.control = control;
				this.form.resetButtons();
			}
		}
	}
});