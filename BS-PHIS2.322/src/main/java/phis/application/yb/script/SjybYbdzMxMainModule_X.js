$package("phis.application.yb.script");
$import("phis.script.SimpleModule","phis.script.Phisinterface");

phis.application.yb.script.SjybYbdzMxMainModule_X = function(cfg) {
	cfg.autoLoadData = false;
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.yb.script.SjybYbdzMxMainModule_X.superclass.constructor.apply(this,[cfg]);
	this.on("winShow", this.onWinShow, this)
};
Ext.extend(phis.application.yb.script.SjybYbdzMxMainModule_X, phis.script.SimpleModule, {
	initPanel : function(sc) {
		if (this.panel) {
			return this.panel;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		var panel = new Ext.Panel({
			border : false,
			frame : true,
			layout : 'border',
			height : 600,
			width : 800,
			defaults : {
				border : false
			},
			items : [ {
				layout : "fit",
				border : false,
				split : true,
				title : "本地交易记录",
				region : 'north',
				height : 300,
				items : this.getLeftList()
			}, {
				layout : "fit",
				border : false,
				split : true,
				title : "医保中心记录",
				region : 'center',
				items : this.getRightList()
			} ],
			tbar : (this.tbar || []).concat(this.createButtons())
		});
		this.panel = panel;
		return panel;
	},
	doDzxz : function(){
		this.panel.el.mask("正在下载交易...");
		this.rightList.grid.getStore().removeAll();
		var DZKSSJ = this.DZKSSJ
		var DZJSSJ = this.DZJSSJ
		var data = {};
		data.DZKSSJ = DZKSSJ;
		data.DZJSSJ = DZJSSJ
		var body={};
		body.USERID=this.mainApp.uid;
		//获取业务周期号
		var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:body
				});
		if (ret.code <= 300) {
			var ywzqh=ret.json.YWZQH;
			this.addPKPHISOBJHtmlElement();
//			this.drinterfaceinit();
			var str=this.buildstr("1101",ywzqh,data);
			var drre=this.drinterfacebusinesshandle(str);
			//alert(drre);
//			var drre="0^1^0^C:\\Users\\Administrator\\Desktop\\XZWJ_1101_20181207190559.txt";
			var arr=drre.split("^");
			if(arr[0]=="0"){
				var obj=[];
					try {
						//var ct=this.readFile(arr[3])
						var ct=this.readFile(arr[2])
						var strline=ct.split("\n");
						for(var j=0;j<strline.length;j++){
							var cons = strline[j].split("	");
							obj.push(cons);
						}
					 }catch (e) {
				   		alert(e)
				   		this.panel.el.unmask();
					}
				for(var i=0;i<obj.length;i++){
					var jkzs = {};
					jkzs["LSH"] = obj[i][0]
					jkzs["DJH"] = obj[i][1]
					jkzs["BCYLFZE"] = obj[i][2]
					jkzs["BCTCZFJE"] = obj[i][3]
					jkzs["BCDBJZZF"] = obj[i][4]
					jkzs["BCDBBXZF"] = obj[i][5]
					jkzs["BCMZBZZF"] = obj[i][6]
					jkzs["BCZHZFZE"] = obj[i][7]
					jkzs["BCXZZFZE"] = obj[i][8]
					jkzs["YYJYLSH"] = obj[i][9]
					jkzs["JYLX"] = obj[i][10]
					this.rightList.addData(jkzs);
					this.panel.el.unmask();
				}
			}else{
				MyMessageTip.msg("提示：",drre, true);
				this.panel.el.unmask();
				return;	
			}
		}
		this.setButtonsState(['mxdz'],true);
	},
	doMxdz : function(){
		this.panel.el.mask("正在对账...");	
		var bddata=""
		var zxdata="";
		var bdstore = this.leftList.grid.getStore();
		for(var i = 0; i < this.leftList.grid.getStore().getCount(); i++){
			bddata = this.leftList.grid.getStore().getAt(i);
			for(var j=0;j<this.rightList.grid.getStore().getCount();j++){
				zxdata = this.rightList.grid.getStore().getAt(j);
				if(bddata.get("LSH") == zxdata.get("LSH") &&  bddata.get("FPHM")==zxdata.get("DJH")){
					this.rightList.grid.getStore().remove(zxdata);
					this.leftList.grid.getStore().remove(bddata);
					i--;
				}
			}	
		}
		this.panel.el.unmask();
		//过滤掉正负的数据
		var tempcount=this.rightList.grid.getStore().getCount();
		var arrx=this.rightList.grid.getStore();
		var arry=arrx;
		for(var j=0;j<tempcount;j++){
			var mapx=arrx.getAt(j)
			if(mapx){
				for(var k=0;k<tempcount;k++){
					var mapy=arry.getAt(k)
					if(mapy){
						if(mapx.get("LSH")==mapy.get("LSH")&& mapx.get("DJH")==mapy.get("DJH")
						&& mapx.get("BCYLFZE")!=mapy.get("BCYLFZE")
						&& Math.abs(mapx.get("BCYLFZE"))==Math.abs(mapy.get("BCYLFZE"))){
							this.rightList.grid.getStore().remove(mapx);
							this.rightList.grid.getStore().remove(mapy);
							j=j-2;
							k=k-2;
						}
					}
				}
			}
		}
		this.setButtonsState(['qrcgwqr'],false);
		this.setButtonsState(['qrsbwqr'],false);
		this.setButtonsState(['jycz'],true);
		this.setButtonsState(['dzxz'],false);
	},
	doJycz : function(){
		var r = this.rightList.getSelectedRecord();
		if (r == null ) {
			MyMessageTip.msg("提示", "请选中医保交易一条记录。", true);
			return;
		}
		if(r.get("BCYLFZE") <0){
			MyMessageTip.msg("提示", "负记录不能冲账!", true);
			return;
		}
		var zxlsh = r.get("YYJYLSH");
		var data = {};
		data.LSH = zxlsh;
		var body={};
		body.USERID=this.mainApp.uid;
		//保险起见去后台校验下再冲正
		var re = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "checkJyXx",
				zxlsh:zxlsh
				});
		if (re.code>300) {
			MyMessageTip.msg("提示：",re.msg, true);
		}	
		if(re.json.jy=="y"){
			MyMessageTip.msg("提示：","本地已有结算信息不能冲账", true);
			return;
		}
		//获取业务周期号
		var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:body
				});
		if (ret.code <= 300) {
			var ywzqh=ret.json.YWZQH;
			this.addPKPHISOBJHtmlElement();
			this.drinterfaceinit();
			var str=this.buildstr("2421",ywzqh,data);
			var drre=this.drinterfacebusinesshandle(str);
			var arr=drre.split("^");
			if(arr[0]=="0"){
				MyMessageTip.msg("提示：","冲正成功！请退出重新对账！", true);
				return;
			}else{
				MyMessageTip.msg("提示：",drre, true);
				this.panel.el.unmask();
				return;	
			}
		}else {
			MyMessageTip.msg("提示：",ret.msg, true);
			return;
		}	
	},
	getNowFormatDate:function() {
        var date = new Date();
        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = year + "" + month+ ""  + strDate;
        return currentdate;
    },
	doDzxzwqr : function(){
		this.panel.el.mask("正在下载未确定交易...");
		this.rightList.grid.getStore().removeAll();
		var wcbz = 0;
		var xzxh = 0;
		var date = "$$1~~~~"+"20170101"+"~"+this.getNowFormatDate()+"~$$";
		var obj = this.XsybDk(17,date,"","");
		if(!obj){
			return;
		}
		var reData = obj.split("$$")[1].split("~")[5].split("^");
		var QRJY = {};
		for(var i=0;i<reData.length;i++){
			var reformat = "MZHM|YBJSJYH|RYBM|BRXM|FYZE|";
			QRJY =this.StrToObj(reformat,reData[i],"|");
			var jkzs = {};
			jkzs["YBJSJYH"] = QRJY.YBJSJYH
			jkzs["FYZE"] = QRJY.FYZE
			jkzs["RYBM"] = QRJY.RYBM
			jkzs["BRXM"] = QRJY.BRXM
			this.rightList.addData(jkzs);
		}
		this.panel.el.unmask();
		this.setButtonsState(['mxdz'],false);
		this.setButtonsState(['qrcg'],false);
		this.setButtonsState(['qrsb'],false);
		this.setButtonsState(['jycz'],false);
		this.setButtonsState(['qrcgwqr'],false);
		this.setButtonsState(['qrsbwqr'],false);
	},
	doQrcg : function(){
		var r = this.leftList.getSelectedRecord();
		if (r == null ) {
			MyMessageTip.msg("提示", "请选中本地交易列表内容。", true);
			return;
		}
		Ext.Msg.show({
			title : '医保提示，发票号码：'+r.get("FPHM"),
			msg : '将向医保中心做确认，是否继续？交易总金额：'+r.get("ZJJE"),
			modal : true,
			width : 300,
			buttons : {ok:'确定成功',cancel:"取消"},
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					this.qrjyGgff(1,r,1);
					return;
				}
				if (btn == "cancel") { 
					return;
				}
			},
			scope : this
		})
	},
	doQrcgwqr : function(){
		var r = this.rightList.getSelectedRecord();
		if (r == null ) {
			MyMessageTip.msg("提示", "请选中医保中心交易列表内容。", true);
			return;
		}
		Ext.Msg.show({
			title : '医保提示，发票号码：',
			msg : '将向医保中心做确认，是否继续？交易总金额：'+r.get("FYZE"),
			modal : true,
			width : 300,
			buttons : {ok:'确定成功未确认交易',cancel:"取消"},
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					this.qrjyGgff(1,r,2);
					return;
				}
				if (btn == "cancel") { 
					return;
				}
			},
			scope : this
		})
	},
	doQrsb : function(){
		var r = this.leftList.getSelectedRecord();
		if (r == null ) {
			MyMessageTip.msg("提示", "请选中本地交易列表内容。", true);
			return;
		}
		Ext.Msg.show({
			title : '医保提示，发票号码：'+r.get("FPHM"),
			msg : '将向医保中心做确认，是否继续？交易总金额：'+r.get("ZJJE"),
			modal : true,
			width : 300,
			buttons : {ok:'确定失败',cancel:"取消"},
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					this.qrjyGgff(2,r,1);
					return;
				}
				if (btn == "cancel") { 
					return;
				}
			},
			scope : this
		})
	},
	doQrsbwqr : function(){
		var r = this.rightList.getSelectedRecord();
		if (r == null ) {
			MyMessageTip.msg("提示", "请选中医保中心交易列表内容。", true);
			return;
		}
		Ext.Msg.show({
			title : '医保提示',
			msg : '将向医保中心做确认，是否继续？交易总金额：'+r.get("FYZE"),
			modal : true,
			width : 300,
			buttons : {ok:'确定失败未确认交易',cancel:"取消"},
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					this.qrjyGgff(2,r,2);
					return;
				}
				if (btn == "cancel") { 
					return;
				}
			},
			scope : this
		})
	},
	//qrbz:1成功、2失败；data列表数据；zxorbd：1本地、2中心（可不要）
	qrjyGgff : function(qrbz,data,zxorbd){
		if(data == null){
			MyMessageTip.msg("提示", "数据为空，无法交易。", true);
			return
		}
		var zxlsh = data.get("YBJSJYH");
		var ret = phis.script.rmi.miniJsonRequestSync({
			serviceId : this.serviceId,
			serviceAction : "getXCountAndJygn",
			body : {
				zxlsh : zxlsh
			}
		});
		if(ret.code > 200){
			MyMessageTip.msg("提示", "查询交易记录数失败，获取交易功能号失败。", true);
		}
		var jlcount = ret.json.JLCOUNT ;
		var jygn = "1";
		
		if(jlcount > 0){
			jygn = ret.json.JYGN ;
		}
		var inData20 = "$$~~~~"+20+"~"+zxlsh+"~"+qrbz+"~$$";
		var obj = this.XsybDk_New("50",inData20, "");
		if(!obj){
			var inData36 = "$$~~~~"+36+"~"+zxlsh+"~"+qrbz+"~$$";
			var obj36 = this.XsybDk("50",inData36, "");
			if(!obj36){
				Ext.Msg.alert("提示", "省医保交易确认失败，请进去交易确认页面手动确认!");
			}
		} else {
		
			Ext.Msg.alert("提示", "省医保交易操作成功");
			
		}
	},
	// 获取上边的list
	getLeftList : function() {
		this.leftList = this.createModule("refSjybBdList", this.refBdList);
		return this.leftList.initPanel();
	},
	// 获取下边的list
	getRightList : function() {
		this.rightList = this.createModule("refSjybZxList", this.refZxList);
		this.rightList.opener = this;
		return this.rightList.initPanel();
	},
	onWinShow : function() {
		this.leftList.loadData(this.DZKSSJ,this.DZJSSJ);
		this.setButtonsState(['dzxz'],true);
		this.setButtonsState(['dzxzwqr'],true);
		this.setButtonsState(['mxdz'],false);
		this.setButtonsState(['jycz'],false);
		this.setButtonsState(['qrcg'],false);
		this.setButtonsState(['qrsb'],false);
		this.setButtonsState(['qrcgwqr'],false);
		this.setButtonsState(['qrsbwqr'],false);
	},
	doClose : function(){
		this.win.hide();
	},
	setButtonsState : function(m, enable) {
		var btns;
		var btn;
		if (this.showButtonOnTop) {
			btns = this.panel.getTopToolbar();
		} else {
			btns = this.panel.buttons;
		}
		if (!btns) {
			return;
		}
		if (this.showButtonOnTop) {
			for (var j = 0; j < m.length; j++) {
				if (!isNaN(m[j])) {
					btn = btns.items.item(m[j]);
				} else {
					btn = btns.find("cmd", m[j]);
					btn = btn[0];
				}
				if (btn) {
					(enable) ? btn.enable() : btn.disable();
				}
			}
		} else {
			for (var j = 0; j < m.length; j++) {
				if (!isNaN(m[j])) {
					btn = btns[m[j]];
				} else {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == m[j]) {
							btn = btns[i];
						}
					}
				}
				if (btn) {
					(enable) ? btn.enable() : btn.disable();
				}
			}
		}
	}
})