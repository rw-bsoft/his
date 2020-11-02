$package("phis.application.fsb.script")

$import("phis.script.SelectList","phis.application.mds.script.MySimpleListCommon")

phis.application.fsb.script.FamilySickBedProjectExecutionRightList = function(cfg) {
		Ext.apply(this, phis.application.mds.script.MySimpleListCommon)
	phis.application.fsb.script.FamilySickBedProjectExecutionRightList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedProjectExecutionRightList,
		phis.script.SelectList, {
			onStoreLoadData : function(store, records, ops) {
				phis.script.SelectList.superclass.onStoreLoadData.call(this,
						store, records, ops)
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				this.sm.suspendEvents(false);
				this.grid.getSelectionModel().selectAll();
				this.sm.resumeEvents();
				var hd = this.grid.body.select("div.x-grid3-hd-checker")
						.first();
				hd.addClass('x-grid3-hd-checker-on');
			},
			getCM : function(items) {
				var cm = phis.script.SelectList.superclass.getCM.call(this,
						items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : this.checkOnly,
							singleSelect : !this.mutiSelect
						})
				this.sm = sm
				sm.on("rowselect", function(sm, rowIndex, record) {
								this.selects[record.id] = record
								this.fireEvent("selectRecord",this);
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
								delete this.selects[record.id]
								this.fireEvent("selectRecord",this);
						}, this)
				var _ctr=this;
				this.sm.selectAll=function(){
				if(this.isLocked()){return}
				_ctr.selects = {};
				var count=_ctr.store.getCount();
				this.suspendEvents(false);
				this.selections.clear();
				for(var i=0;i<count;i++){
					this.selectRow(i,true)
				_ctr.selects[_ctr.store.getAt(i).id] = _ctr.store.getAt(i)
				}
				this.resumeEvents();
				_ctr.fireEvent("selectRecord",this);
				}
				
				this.sm.clearSelections=function(a){
					_ctr.selects = {};
					if(this.isLocked()){return}
						this.suspendEvents(false);
					if(a!==true){var c=this.grid.store,b=this.selections;b.each(function(d){this.deselectRow(c.indexOfId(d.id))},this);b.clear()}else{this.selections.clear()}this.last=false
				this.resumeEvents();
				_ctr.fireEvent("selectRecord",this);
				}
				return [sm].concat(cm)
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			},
			doRender : function(v, params,record) {
				var zje = record.get("YCSL")*record.get("YPDJ")
				return parseFloat(zje).toFixed(2);
			}
		})