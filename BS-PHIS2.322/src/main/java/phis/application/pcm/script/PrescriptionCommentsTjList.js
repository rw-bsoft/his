$package("phis.application.pcm.script")
$import("phis.script.SimpleList")

phis.application.pcm.script.PrescriptionCommentsTjList = function(cfg) {
	cfg.autoLoadData=false;
	cfg.disablePagingTbr=true;
	phis.application.pcm.script.PrescriptionCommentsTjList.superclass.constructor
			.apply(this, [cfg])
			this.on("loadData",this.afterLoadData,this)
}
var _ctr=null;
function dpsClick(){
_ctr.onDpsClick(this);
}
function bhlsClick(){
_ctr.onBhlsClick(this);
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsTjList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				var grid = phis.application.pcm.script.PrescriptionCommentsTjList.superclass.initPanel
						.call(this, sc)
				_ctr = this;
				return grid
			},
			onRenderer_dps : function(value, metaData, r) {
				if(value!=null&&value>0){
				return "<a href='javascript:dpsClick();' ><font color='blue' size=6>"+value+"</font></a>"
				}
				return value;
			},
			onRenderer_bhl : function(value, metaData, r) {
				if(value!=null&&value>0){
				return "<a href='javascript:bhlsClick();'><font color='blue' size=6>"+value+"</font></a>"
				}
				return value;
			},
			//点击点评数
			onDpsClick:function(span){
			this.openMX(1)
			},
			//点击不合理数
			onBhlsClick:function(){
			this.openMX(2)
			},
			loadData:function(){
			this.requestData.serviceId=this.fullServiceid;
			this.requestData.serviceAction=this.serviceActionId;
			phis.application.pcm.script.PrescriptionCommentsTjList.superclass.loadData.call(this);
			},
			//将查出来的数据丢给下面的图形报表
			afterLoadData:function(store){
			this.fireEvent("txbbData",store.data);
			},
			//界面打开自动加载数据
			afterOpen:function(){
			this.loadData();
			},
			//打开明细界面
			openMX:function(tag){
			var r=this.getSelectedRecord()
			if(r==null){
			return;}
			if(!this.requestData.body){
			return;}
			var cnd=['and',['eq',['$','b.JGID'],['s',this.mainApp['phisApp'].deptId]],['eq',['$','b.DPBZ'],['i',1]],['ge', ['$', "str(b.DPRQ,'yyyy-mm-dd')"],['s', this.requestData.body.DPRQKS]],['le', ['$', "str(b.DPRQ,'yyyy-mm-dd')"],['s', this.requestData.body.DPRQJS]]];
			if(tag==2){
			cnd=['and',cnd,['eq',['$','b.SFHL'],['i',1]]];
			}
			if(this.requestData.body.DPLX==2){
			cnd=['and',cnd,['ge',['$','b.JBYW'],['i',1]]];
			}else if(this.requestData.body.DPLX==1){
			cnd=['and',cnd,['ge',['$','b.KJYW'],['i',1]]];
			}else if(this.requestData.body.DPLX==0){
			cnd=['and',cnd,['eq',['$','b.JBYW'],['i',0]],['eq',['$','b.KJYW'],['i',0]]];
			}
			if(r.get("KSDM")!=0){
			cnd=['and',cnd,['eq',['$','b.KSDM'],['l',r.get("KSDM")]]];
			}
				var list = this.createModule("list", this.refList);
				var win = list.getWin();
				win.add(list.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					list.requestData.cnd=cnd;
					list.loadData();
				}
			
			}

		})