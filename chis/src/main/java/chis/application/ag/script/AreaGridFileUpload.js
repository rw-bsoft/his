/**
 * 网格地址批量导入
 * 
 * @author : yub
 */
$package("chis.application.ag.script")
$import("chis.script.ExcelFileUploader", "util.rmi.jsonRequest")

chis.application.ag.script.AreaGridFileUpload = function(filter) {
	chis.application.ag.script.AreaGridFileUpload.superclass.constructor.apply(this,
			[filter]);
}

Ext.extend(chis.application.ag.script.AreaGridFileUpload,
		chis.script.ExcelFileUploader, {
			getUrl : function() {
				return 'excelUploadForm?serviceId=chis.areaGridExcelFileUpload&serviceAction=saveAreaGrid';
			},

			haveProgress : function() {
				return true;
			},

			onUploadEnd : function() {
				this.runner.stop(this.task);
				this.stop = true;
			},

			getProgress : function() {
				Ext.MessageBox.show({
							title : '请等待',
							msg : '读取数据中...',
							width : 240,
							progress : true,
							closable : false
						});
				this.stop = false;// 任务定义
				this.task = {
					run : function() {
						if (!this.stop) {// 执行处理
							util.rmi.jsonRequest({
										serviceId : "chis.areaGridProgress",
										serviceAction : "getProgress",
										method:"execute",
										body : {}
									}, function(code, msg, json) {
										var resBody = json.body;
										if (resBody) {
											this.progress = resBody.progress;
											if (this.progress == 0) {
												return;
											}
										} else {
											this.runner.stop(this.task); // 停止执行
											alert('进度条读取失败...');
										}
									}, this);
							Ext.MessageBox.updateProgress(this.progress / 100,
									this.progress + '%');
						} else {
							Ext.MessageBox.hide();
							this.runner.stop(this.task); // 停止执行
						}
					},
					interval : 100
					// 单位为毫秒，目前定义时间间隔为 0.1 秒};
				}
				this.runner = new Ext.util.TaskRunner();
				this.runner.start(this.task);
			}

		})