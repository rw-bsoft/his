$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.FaultyDeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	var dat = new Date().format('Y-m-d');
	var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))+ "-01";
	this.dateFromValue = dateFromValue;
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.initCnd = ['and',['and',['ge',['$', "str(JZRQ,'yyyy-mm-dd')"],['s', dateFromValue]],
			   ['le',['$', "str(JZRQ,'yyyy-mm-dd')"],['s', new Date()]]],['eq', ['$', 'QRBZ'],['i', 1]]];
	phis.application.sup.script.FaultyDeterminedList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.FaultyDeterminedList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				// 顶部工具栏
				var label = new Ext.form.Label({
							text : "单据日期："
						});
				this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : this.dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
				this.dateFieldEnd = new Ext.form.DateField({
							name : 'storeDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(["<h1 style='text-align:center'>已记账报损单:</h1>", '-',label, this.dateField,"至",this.dateFieldEnd, tbar]);
				
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			onChange : function(radiofield, oldvalue) {
				this.QRZT = oldvalue.inputValue;
			},
			doRefreshWin : function() {
				this.clear();
				var startDate = "";// 开始时间
				var endDate = ""; // 结束时间
				if (this.dateField) {
					startDate = new Date(this.dateField.getValue()).format("Y-m-d");
				}
				if (this.dateFieldEnd) {
					endDate = new Date(this.dateFieldEnd.getValue()).format("Y-m-d");
				}
				var QRZT = 1;
				this.requestData.cnd = ['and',['ge',['$', "str(a.ZDRQ,'yyyy-mm-dd')"],['s', startDate]],
							     ['le',['$', "str(a.ZDRQ,'yyyy-mm-dd')"],['s', endDate]]];
				this.requestData.cnd = ['and', this.initCnd, this.requestData.cnd];
				this.loadData();
				/*this.doCndQuery(null,null,['and',['ge',['$', "str(a.ZDRQ,'yyyy-mm-dd')"],['s', startDate]],
							     ['le',['$', "str(a.ZDRQ,'yyyy-mm-dd')"],['s', endDate]]]);*/
			},
			
			doOpen :  function(r){
				if(!r){
			      r = this.getSelectedRecord();
				}
				if (r == null) {
					return;
				}
				var DJXH = r.get("DJXH");
				var ZBLB = r.get("ZBLB");
				var KFXH = r.get("KFXH");
				var CKRQ = r.get("CKRQ");
				
				this.faultyDetailModule = this.createModule("faultyDetailModule", this.openRef);
				
				this.faultyDetailModule.on("save", this.onSave, this);
				this.faultyDetailModule.on("winClose", this.onClose, this);
				
				//this.faultyDetailModule.initPanel();
				var win = this.getWin();
				win.add(this.faultyDetailModule.initPanel());
				this.faultyDetailModule.DJXH = DJXH;
				this.faultyDetailModule.ZBLB = ZBLB;
				this.faultyDetailModule.KFXH = KFXH;
				this.faultyDetailModule.CKRQ = CKRQ;
				
                this.faultyDetailModule.form.initDataId=DJXH;
                this.faultyDetailModule.DJXH=DJXH;
				this.faultyDetailModule.form.loadData();
				
				this.faultyDetailModule.list.requestData.cnd = ['eq', ['$', 'DJXH'],['i',DJXH]];
				this.faultyDetailModule.list.loadData();
				
				win.show();
				win.center();
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.faultyDetailModule = this.createModule(
						"faultyDetailModule", this.addRef);
				this.faultyDetailModule.zblb = this.zblb;
				this.faultyDetailModule.DJXH=r.data.DJXH;
				this.faultyDetailModule.on("save", this.onSave, this);
				this.faultyDetailModule
						.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.faultyDetailModule.initPanel());
				this.faultyDetailModule.changeButtonState("commited");
				//this.faultyDetailModule.panel.disable();
				win.show()
				win.center()
				if (!win.hidden) {
					this.faultyDetailModule.op = "update";
					this.faultyDetailModule.initDataBody = initDataBody;
					this.faultyDetailModule.loadData(initDataBody);
				}

			},
			doLook : function(){
				this.doUpd();
			},
			onDblClick : function(){
				this.doUpd();
			},
			onClose : function() {
				this.getWin().hide();
			},
			onSave:function(){
              this.fireEvent("save", this);
            },
            doPrint : function() {
                var module = this.createModule("faultyManagerPrint",
                        this.refFaultyManagerPrint)
                var r = this.getSelectedRecord()
                if (r == null) {
                    MyMessageTip.msg("提示", "打印失败：无效的报损单信息!", true);
                    return;
                }
//                if(r.data.DJZT==0){
//                    MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
//                    return;
//                }
                module.djxh=r.data.DJXH;
                module.initPanel();
                module.doPrint();
            }
			
		})