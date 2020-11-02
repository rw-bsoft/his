$package("phis.application.cic.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")
$styleSheet("phis.resources.css.app.biz.tplStyle")
phis.application.cic.script.ClinicPersonalSetList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.autoLoadData = false;
	cfg.disableContextMenu = true;
	phis.application.cic.script.ClinicPersonalSetList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cic.script.ClinicPersonalSetList, phis.script.SimpleList,
		{
			expansion : function(cfg) {
				var radiogroup = new Ext.form.RadioGroup({
				width : 180,
				items : [{
							boxLabel :  '本人',
							inputValue : 1,
							cmd : "gltj",
							name : "gltj",
							checked : true,
							clearCls : true
							
						}, {
							boxLabel : '本科室',
							cmd : "gltj",
							name : "gltj",
							inputValue : 2,
							clearCls : true
						}, {
							boxLabel : '全院',
							cmd : "gltj",
							name : "gltj",
							inputValue : 3,
							clearCls : true
						}]
				});
				this.gltj = 1;
		
				radiogroup.on('change', function(radiogroup, radio) {
					var gltj = radio.inputValue;
					this.gltj = gltj;
					var cnd = null;
					if (gltj == 3) {//全院，与张伟沟通：查询条件为JGID为当前结构，ZTLB为当前选择类别(西药、中药、草药)，KSDM为空，YGDM为空
							cnd = ['and', ['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]
										, ['eq', ['$', 'ZTLB'], ['i', this.exContext.clinicType]]
										, ['eq', ['$', 'KSDM'], ['s', 0]]
										, ['eq',['$','SFQY'],['i',1]]
							, ['eq',['$','SSLB'],['i',3]]
										//, ['isNull', ['$', 'KSDM']]
										, ['isNull',['$','YGDM']]
								  ];
//							cnd = [ 'and', [ 'and', [ 'and', ['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]], [ 'or', [ 'or', [ 'and', [ 'eq', [ '$', 'KSDM'],
// 									[ 's', this.mainApp['phis'].departmentId]],
//								[ 'eq', [ '$', 'SSLB'], [ 'i', 2]]], [ 'and', [ 'eq', [ '$', 'YGDM'], [ 's', this.mainApp.uid]], [ 'eq', [ '$', 'SSLB'], [ 'i', 1]]]],
//								['eq', ['$', 'SSLB'], ['i', 3]]]], ['eq', ['$', 'SFQY'], ['i', 1]]], ['eq', ['$', 'ZTLB'], ['i', this.exContext.clinicType]]];
					} else if (gltj == 2) {//科室
						cnd =['and',['and',['and',['and',['eq',['$','JGID'],['s',this.mainApp['phisApp'].deptId]],['eq',['$','KSDM'],['s',this.mainApp['phis'].departmentId]]],['eq',['$','SSLB'],['i',2]]],['eq',['$','SFQY'],['i',1]]],['eq',['$','ZTLB'],['i',this.exContext.clinicType]]]

					} else if (gltj == 1) {//个人
												
						cnd = ['and',['and',['and',['and',['eq',['$','JGID'],['s',this.mainApp['phisApp'].deptId]],['eq',['$','YGDM'],['s',this.mainApp.uid]] ], ['eq',['$','SSLB'],['i',1]] ] , ['eq',['$','SFQY'],['i',1]]],['eq',['$','ZTLB'],['i',this.exContext.clinicType]]];

					}
					this.initCnd = cnd;
					this.requestData.cnd = cnd;
					this.requestData.pageNo = 1;  //radio变换时，页码重置为1
					this.refresh();
				}, this);
						
				this.radiogroup = radiogroup;
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([radiogroup, "-", tbar]);
			},
			onRenderer : function(value, metaData, r) {
				var SFQY = r.get("SFQY");
				var src = (SFQY == 1) ? "yes" : "no";
				return "<img src='"+ ClassLoader.appRootOffsetPath +"resources/phis/resources/images/" + src + ".png'/>";
			},
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			},
			onReady : function() {
				phis.application.cic.script.ClinicPersonalSetList.superclass.onReady
						.call(this);
				this.grid.un('rowcontextmenu', this.onContextMenu, this);
				this.grid.on('rowcontextmenu', function(grid, rowIndex, e) {
							e.preventDefault();
							grid.getSelectionModel().selectRow(rowIndex);
							var tip = new Ext.ToolTip({
										html : this.getHTML(rowIndex),
										dismissDelay : 0,
										width : 500,
										autoScroll : true,
										style : "background:#f9f9f9;"
									});
							tip.showAt(0, 0)
						}, this);
			},
			getHTML : function(rowIndex) {
				var r = this.grid.getStore().getAt(rowIndex);
				if (!this.tpl) {
					var url = document.URL;
					url = url
							+ "resources/css/app/desktop/images/icons/AB1.gif";
					var img = "<img src='" + url
							+ "' width='18px' height='15px' />";

					var html = "<table id='mytable' cellspacing='0'><caption> </caption> ";
					html += "<tr><th scope='col'>药品名称</th><th scope='col'>数量</th><th scope='col'>剂量</th><th scope='col'>用法</th><th scope='col'>频次</th></tr>";
					html += '<tpl for="body">'
					html += '<tr><td class="row">{XMMC}</td><td class="row">{XMSL}</td><td class="row">{YCJL}<tpl if="JLDW">{JLDW}</tpl></td><td class="row">{GYTJ_text}</td><td class="row">{SYPC_text}</td></tr>'
					html += "</tpl></table>";

					// html = "<div style='padding:10px;1px solid
					// #999;color:#555;background:#f9f9f9;max-height:620px;'>"
					// + html + "</div>";
					this.tpl = new Ext.XTemplate(html);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "simpleQuery",
							schema : "YS_MZ_ZT02_BQ",
							cnd : ['eq', ['$', 'a.ZTBH'], ['d', r.get("ZTBH")]],
							pageSize : 0,
							pageNo : 1
						});
				var data = resData.json;
				return this.tpl.apply(data);
			}
		})
