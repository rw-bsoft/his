
$package("chis.script.gis");
$import(
	"app.modules.form.SimpleFormView",
	"util.rmi.jsonRequest"
)
		
chis.script.gis.statisticalData = function (cfg) {
	chis.script.gis.statisticalData.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.gis.statisticalData, app.modules.form.SimpleFormView, {

	initPanel:function(sc){
		this.dataId = this.mainApp.deptId
		if(this.panel_04){
			return this.panel_04
		}
 		var panel_04 = new Ext.Panel({
 					id : this.id,
         			bodyStyle:"background-color:#ffffff;",
 					html:"<table id='"+this.id+"_id' cellpadding='0' cellspacing='0' border='0' id='table' width='100%' style='border-left:1px solid #c6d5e1; border-top:1px solid #c6d5e1; border-bottom:none;'>"+
	"<thead>"+
		"<tr>"+
			"<th width='33%' style='padding-left:2px;text-align:left; height:22px;line-height:22px; border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1;'>统计概率列表</th>"+
			"<th width='33%' style='padding-left:2px;text-align:left; border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'></th>"+
			"<th width='33%' style='padding-left:2px;text-align:left; border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'></th>"+
		"</tr>"+
	"</thead>"+
	"<tbody>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='电子健康档案建档率=建立电子健康档案人数/辖区内常住居民数×100％'>健康档案建档率(<span id='"+this.id+"_01'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='老年居民健康管理率=接受健康管理的60岁及以上常住老年人口数/年内辖区内60岁及以上常住老年人口总数×100%'>老年居民健康管理率(<span id='"+this.id+"_02'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='困难群体健康管理率=接受健康管理的困难群体人数/年内辖区内困难群体总人数×100%'>困难群体健康档案率(<span id='"+this.id+"_03'></span>)</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='健康体检表完整率=随机抽样中填写完整的健康体检表数/随机抽样的健康体检表总数×100%'>健康体检表完整率(<span id='"+this.id+"_04'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='高血压发现率=辖区内发现的高血压人数/辖区服务人口数×100%'>高血压发现率(<span id='"+this.id+"_05'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='高血压规范管理率=规范管理的高血压人数/辖区内发现的高血压人数×100%'>高血压规范管理率(<span id='"+this.id+"_06'></span>)</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='糖尿病发现率=辖区内发现的糖尿病人数/辖区服务人口数×100%'>糖尿病发现率(<span id='"+this.id+"_07'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='糖尿病规范管理率=规范管理的糖尿病人数/辖区内发现的高血压人数×100%'>糖尿病规范管理率(<span id='"+this.id+"_08'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='重性精神疾病患者发现率=所有登记在册的确诊重性精神疾病患者数/（辖区内服务人口数）×100％'>重性精神疾病发现率(<span id='"+this.id+"_09'></span>)</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='重性精神疾病患者规范管理率=每年按照规范要求进行管理的确诊重性精神疾病患者数/所有登记在册的确诊重性精神疾病患者数×100％'>重性精神疾病规范管理率(<span id='"+this.id+"_10'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='重性精神疾病患者稳定率=最近一次随访时分类为病情稳定的患者数/所有登记在册的确诊重性精神疾病患者数×100％'>重性精神疾病患者稳定率(<span id='"+this.id+"_11'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='重性精神疾病患者治疗率=最近一次随访时治疗（含规律和间断服药）的患者数/所有登记在册的确诊重性精神疾病患者数×100％'>重性精神疾病患者治疗率(<span id='"+this.id+"_12'></span>)</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='建证率=年度辖区内建立预防接种证人数/年度辖区内应建立预防接种证人数×100％'>建证率(<span id='"+this.id+"_13'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='某种疫苗接种率=年度辖区内某种疫苗年度实际接种人数/某种疫苗年度应接种人数×100％'>某种疫苗接种率(<span id='"+this.id+"_14'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='传染病疫情报告率=网络规范直报传染病病例数/登记传染病病例数×100％'>传染病疫情规范报告率(<span id='"+this.id+"_15'></span>)</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title='传染病疫情报告及时率=网络直报及时的传染病病例数/登记传染病病例数×100％'>传染病疫情报告及时率(<span id='"+this.id+"_16'></span>)</td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title=''><span></span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1' title=''><span></span></td>"+
		"</tr>"+
	"</tbody>"+
 " </table>"
 		});
	    this.panel_04 = panel_04
		this.panel_04.on("afterrender", this.onReady, this)
	    return this.panel_04;
	},
	onDbClick:function(grid,r,c,e){
		var rs = this.data
		var desktop = this.mainApp.desktop
		if(rs && r < rs.length){
			var id = rs[r].refModule
			if(id){
				desktop.openWin(id);
			}
		}
	},
	query : function(id) {
		if(this.dataId == id)
			return ;
		this.dataId = id;
		this.initData();
	},
	initData : function(){return;
		if (this.dataId.length==9) {
			
		}
	},
	isEmptyObject: function( obj ) {
        for ( var name in obj ) {
            return false;
        }
        return true;
    },
    onReady : function() {
    	if(this.body)
			this.setXML(this.body[0]);
	},
	setXML : function(body){
		if(body){
			this.body = [body];
			if(!this.isEmptyObject(body)){
				var prp = this.body[0]["PRP"];				//常住人口
				
				var h = this.body[0]["Hypertension"];		//高血压管理数
				var hn = this.body[0]["HypertensionNorms"]; //高血压规范管理数
				
				var d = this.body[0]["Diabetes"];			//糖尿病管理数
				var dn = this.body[0]["DiabetesNorms"];		//糖尿病规范管理数
				
				var ha = this.body[0]["HealthArchives"];	//健康档案管理数
				
				var hh = this.body[0]["HEALTHEXAMINATION"];	//随机抽样中填写完整的健康体检表数
				var hhc = this.body[0]["HEALTHEXAMINATIONCOUNT"];//随机抽样的健康体检表总数
				
				var op = this.body[0]["OldPeople"]; 		//老年人档案管理数
				
				var bvi = this.body[0]["BEVACCINEINOCULATION"]; //某种疫苗年度应接种人数
				var vi = this.body[0]["VACCINEINOCULATION"];//年度辖区内某种疫苗年度实际接种人数
				
				var a60 = this.body[0]["AGED60"]; 			//年内辖区内60岁及以上常住老年人口总数
				
				var dg = this.body[0]["DIFFICULTGROUP"]; 	//接受健康管理的困难群体人数
				var dgy = this.body[0]["DIFFICULTGROUPYEAR"];//年内辖区内困难群体总人数
				
				var vv = this.body[0]["vaccination"]; 	//年度辖区内建立预防接种证人数
				var bvv = this.body[0]["beVaccination"];//年度辖区内应建立预防接种证人数
				
				var id = this.body[0]["infectiousDisease"]; 	//网络规范直报传染病病例数
				var idc = this.body[0]["infectiousDiseaseCount"];//登记传染病病例数
				var tid = this.body[0]["timelyInfectiousDisease"];//网络直报及时的传染病病例数
				
				var v
				if(!isNaN(prp)){
					//电子健康档案建档率=建立电子健康档案人数/辖区内常住居民数×100％
					if(!isNaN(ha)){
						v = ha/prp*100
						flied = document.getElementById(this.id+"_01");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					//老年居民健康管理率=接受健康管理的60岁及以上常住老年人口数/年内辖区内60岁及以上常住老年人口总数×100%
					if(!isNaN(op)){
						v = op/a60*100
						flied = document.getElementById(this.id+"_02");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					
					//困难群体健康管理率=接受健康管理的困难群体人数/年内辖区内困难群体
					if(!isNaN(dg)){
						v = dg/dgy*100
						flied = document.getElementById(this.id+"_03");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					
					//健康体检表完整率=随机抽样中填写完整的健康体检表数/随机抽样的健康体检表总数×100%
					//要改
					if(!isNaN(hh)){
						v = hh/hhc*100
						flied = document.getElementById(this.id+"_04");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					//高血压发现率=辖区内发现的高血压人数/辖区服务人口数×100%
					if(!isNaN(h)){
						v = h/prp*100
						flied = document.getElementById(this.id+"_05");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					//高血压规范管理率=规范管理的高血压人数/辖区内发现的高血压人数×100%
					if(!isNaN(hn)){
						v = hn/h*100
						flied = document.getElementById(this.id+"_06");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					//糖尿病发现率=辖区内发现的糖尿病人数/辖区服务人口数×100%
					if(!isNaN(d)){
						v = d/prp*100
						flied = document.getElementById(this.id+"_07");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					//糖尿病规范管理率=规范管理的糖尿病人数/辖区内发现的高血压人数×100%
					if(!isNaN(dn)){
						v = dn/d*100
						flied = document.getElementById(this.id+"_08");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					
					//重性精神疾病患者发现率=所有登记在册的确诊重性精神疾病患者数/（辖区内服务人口数）×100％
					
					//重性精神疾病患者规范管理率=每年按照规范要求进行管理的确诊重性精神疾病患者数/所有登记在册的确诊重性精神疾病患者数×100％
					
					//重性精神疾病患者稳定率=最近一次随访时分类为病情稳定的患者数/所有登记在册的确诊重性精神疾病患者数×100％。
					
					//重性精神疾病患者治疗率=最近一次随访时治疗（含规律和间断服药）的患者数/所有登记在册的确诊重性精神疾病患者数×100％。
					
					//建证率=年度辖区内建立预防接种证人数/年度辖区内应建立预防接种证人数×100％
					if(!isNaN(vv)){
						v = vv/bvv*100
						flied = document.getElementById(this.id+"_13");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					
					//某种疫苗接种率=年度辖区内某种疫苗年度实际接种人数/某种疫苗年度应接种人数×100％
					if(!isNaN(vi)){
						v = vi/bvi*100
						flied = document.getElementById(this.id+"_14");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					//传染病疫情报告率=网络规范直报传染病病例数/登记传染病病例数×100％
					if(!isNaN(id)){
						v = id/idc*100
						flied = document.getElementById(this.id+"_15");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
					
					//传染病疫情报告及时率=网络直报及时的传染病病例数/登记传染病病例数×100％
					if(!isNaN(tid)){
						v = tid/idc*100
						flied = document.getElementById(this.id+"_16");
						if(flied!=null)
							flied.innerHTML = v.toFixed(2)+"%";
					}
				
				}
					
				
			}else{
				if(document.getElementById(this.id+"_id")){
					var table = document.getElementById(this.id+"_id");
		         	var tableCount = table.rows.length;
		         	for (var i = 0; i < tableCount ; i++) {
		         		var eveRow = table.rows[i];
		                var spans = eveRow.getElementsByTagName('span');
		               	for (var j = 0; j < spans.length; j++) {
							spans[j].innerHTML = "";
						}
		         	}
	         	}
			}
		}
	},
	loadData:function(){
		if(this.loading){
			return
		}
//		this.panel_04.setHeight(190);
		this.initData();
	},
	feedData:function(json){
		var store = this.gridStore
		var rs = json.body
		this.data = rs
		store.removeAll();
		if(rs && rs.length > 0){
			for(var i = 0; i < rs.length; i ++){
				var r = new this.Record(rs[i])
				store.add(r)
			}
		}
		this.selectFirstRow()
	},
	selectFirstRow:function(){
		if(this.grid){
			var sm = this.grid.getSelectionModel()
			if(sm.selectRow){
				sm.selectRow(0)
			}
			else if(sm.select){
				sm.select(0,0)
			}
			if(!this.grid.hidden){
				try{
					this.grid.getView().getRow(0).focus(true,true)
				}
				catch(e){}
			}
		}	
	},	
	initCM:function(){
	  var cm =new Ext.grid.ColumnModel([
			{
	           header: "标题",
	           dataIndex: 'eventName',
	           width: 100,
	           css : "font-weight:bold;"
	        },{
	           header: "内容",
	           dataIndex: 'eventValue',
	           width: 350
	        }
	    ]);	
	    return cm;
	},
	initRecord:function(){
	    this.Record = Ext.data.Record.create([
	           {name: 'eventName', type: 'string'},
	           {name: 'eventValue', type: 'string'},
	           {name: 'refModule',type:'string'}
	      ]);
	      return this.Record;
	},
	getWin: function(){
		var win = this.win
		var closeAction = "close"
		if(!this.mainApp){
			closeAction = "hide"
		}
		if(!win){
			win = new Ext.Window({
				id: this.id,
		        title: this.title,
		        width: this.width,
		        iconCls: 'icon-grid',
		        shim:true,
		        layout:"fit",
		        animCollapse:true,
		        closeAction:closeAction,
		        constrainHeader:true,
		        minimizable: true,
		        maximizable: true,
		        shadow:false,
		        items:this.initPanel()
            })
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			win.on("add",function(){
				this.win.doLayout()
			},this)
			this.win = win
		}
		win.instance = this;
		return win;
	}	
});


