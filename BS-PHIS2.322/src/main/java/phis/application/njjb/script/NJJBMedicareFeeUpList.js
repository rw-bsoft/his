$package("phis.application.njjb.script");

$import("phis.script.SimpleList", "phis.script.widgets.MyMessageTip","phis.script.Phisinterface");
/**
 * 南京金保费用上传
 */
phis.application.njjb.script.NJJBMedicareFeeUpList = function(cfg) {
	// this.serverParams = {serviceAction:cfg.serviceAction};
	//cfg.mutiSelect = true;
//	Ext.apply(this, phis.application.yb.script.YbUtil);
	cfg.disablePagingTbr = true;
	Ext.apply(this, phis.script.Phisinterface);
	phis.application.njjb.script.NJJBMedicareFeeUpList.superclass.constructor.apply(this,
			[cfg]);
},

Ext.extend(phis.application.njjb.script.NJJBMedicareFeeUpList, phis.script.SimpleList, {
	onDblClick : function(grid, index, e) {
		this.doUpload();
	},
	/**
	 * 费用上传
	 */
	doUpload : function(rowsize) {
		var rs = this.grid.getSelectionModel().getSelected();
		var commitList = [];
		if(!rs){
			return;
		}
		this.grid.el.mask("正在上传数据...", "x-mask-loading");
		var rs_data = rs.data;
		if(rowsize != null && rowsize > 0) {
			rs_data['ROWNUM'] = rowsize;
		}
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "hopitalFeeUp",
					body : rs_data
				});
		if(ret.code > 200){
			this.processReturnMsg(ret.code, ret.msg);
			this.grid.el.unmask();
			return;
		}
		var data = ret.json.body;
		if(data.sfmx && data.sfmx.length <= 0){
			MyMessageTip.msg("提示", "没有需要上传的处方", true);
			this.grid.el.unmask();
			return;
		}   
		
		var ret1 = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:{
					USERID : this.mainApp.uid
				}
				});
		if (ret1.code <= 300) {
		}else {
			MyMessageTip.msg("提示", "获取业务周期号失败", true);
			return;
		}
		var ywzqh=ret1.json.YWZQH;
		this.addPKPHISOBJHtmlElement();
		//处方明细上报
		if(data.sblength>0){
			for(var i=0;i<data.sblength;i++){
				data.sfmx=data["sfmx"+i];
				data.CFH=data.NJJBLSH+"-"+i
				var str=this.buildstr("2310",ywzqh,data);
				var drre=this.drinterfacebusinesshandle(str);
				var obj = drre.split('^');
				if(obj[0] != 0){
					MyMessageTip.msg("提示", obj[3], true);
					return;
				}
			}
			data.sfmx=data.allsfmx;
			
		}else{
			var str=this.buildstr("2310",ywzqh,data);
				var drre=this.drinterfacebusinesshandle(str);
				var obj = drre.split('^');
				if(obj[0] != 0){
					MyMessageTip.msg("提示", obj[3], true);
					return;
				}
		}
			
		var ret_save = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : "saveMedicarePrescriptions",
					body : data.sfmx
				});
		if (ret_save.code > 200) {
			//处方上传成功本地保存失败，直接去撤销所有
			this.doRemoveScbz();
			MyMessageTip.msg("提示", "住院处方上传失败，请重新上传", true);
			this.grid.el.unmask();
			return;
		}
		MyMessageTip.msg("提示", "住院处方上传成功", true);
		this.grid.el.unmask();
	},
	/**
	 * 费用删除上传(所有)
	 */
	doRemoveScbz : function() {
		var rs = this.grid.getSelectionModel().getSelected();
		if(!rs){
			return;
		}
		this.grid.el.mask("正在取消上传...", "x-mask-loading");
		var ret1 = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:{
					USERID : this.mainApp.uid
				}
				});
		if (ret1.code <= 300) {
			
		}else {
			MyMessageTip.msg("提示", "获取业务周期号失败", true);
			return;
		}
		var ywzqh=ret1.json.YWZQH;
		alert(rs.data.NJJBLSH)
		var data = {LSH : rs.data.NJJBLSH,CFH : '',CFLSH : '',JBR:this.mainApp.uid};
		this.addPKPHISOBJHtmlElement();
		//处方明细撤销
		var str=this.buildstr("2320",ywzqh,data);
		var drre=this.drinterfacebusinesshandle(str);
		var obj = drre.split('^');
		if(obj[0] !=0){
			MyMessageTip.msg("南京金保返回提示",drre, true);
			this.grid.el.unmask();
			return;
		}	
		//成功,本地修改 set 所有SCBZ为0
		var ret_remove = phis.script.rmi.miniJsonRequestSync({
				serviceId : "medicareService",
				serviceAction : "removeScbz",
				body : rs.data
			});
		if (ret_remove.code > 200) {
			this.processReturnMsg(ret_remove.code, ret_remove.msg);
			this.grid.el.unmask();
			return;
		}
		MyMessageTip.msg("提示", "处方明细撤销成功", true);
		this.grid.el.unmask();
	},
	loadData : function() {
		var cnd = ['and', ['lt', ['$', 'CYPB'], ['i', 8]],['eq', ['$', 'BRXZ'], ['d', 2000]]];
		var jgidCnd = ['eq', ['$', 'JGID'], ['s', this.mainApp.deptId]];
		if(this.requestData.cnd){
			this.requestData.cnd = ['and',cnd,jgidCnd,this.requestData.cnd];
		}else{
			this.requestData.cnd = ['and',cnd,jgidCnd];
		}
		phis.application.njjb.script.NJJBMedicareFeeUpList.superclass.loadData.call(this);
	}

});