$package("phis.application.twr.script")
$import("app.desktop.Module", "phis.application.twr.script.MyDatePicker")
phis.application.twr.script.DRScheduleCalendar = function(cfg) {
	phis.application.twr.script.DRScheduleCalendar.superclass.constructor
			.apply(this, [ cfg ])

}
Ext.extend(phis.application.twr.script.DRScheduleCalendar, app.desktop.Module,
		{
			initPanel : function() {
				var datefield = new Ext.Panel({
					labelWidth : 100, // label settings here cascade
					frame : true,
					bodyStyle : 'padding:20px 0px 0px',
					defaults : {
						height : 197
					},
					defaultType : 'myDatePicker',
					items : [ {
						fieldLabel : 'Date',
						name : 'date',
						showToday : true,
						minDate : new Date(Date.parse(Date.getServerDate())),
						mainApp : this.mainApp,
						listeners : {
							select : this.openWin,
							scope : this
						}
					} ]
				});
				return datefield
			},
			openWin : function(e, t) {
				this.fireEvent("beforeOpenWin", this);
				this.fireEvent("openWin", e, t, this);
			}
		})