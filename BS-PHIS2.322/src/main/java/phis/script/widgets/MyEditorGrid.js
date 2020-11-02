$package("util.widgets")

util.widgets.MyEditorGrid = Ext.extend(Ext.grid.EditorGridPanel, {
			startEditing : function(row, col) {
				this.stopEditing();
				if (this.colModel.isCellEditable(col, row)) {
					this.view.ensureVisible(row, col, true);
					var r = this.store.getAt(row);
					var field = this.colModel.getDataIndex(col);
					var e = {
						grid : this,
						record : r,
						field : field,
						value : r.data[field],
						row : row,
						column : col,
						cancel : false
					};
					if (this.fireEvent("beforeedit", e) !== false && !e.cancel) {
						this.editing = true;
						var ed = this.colModel.getCellEditor(col, row);
						if (!ed.rendered) {
							ed.render(this.view.getEditorParent(ed));
						}
(function				() { // complex but required for focus issues in
							// safari, ie and opera
							// add by yangl 增加字段名称,用于焦点跳转
							ed.fieldName = field;
							ed.row = row;
							ed.col = col;
							ed.record = r;
							ed.on("complete", this.onEditComplete, this, {
										single : true
									});
							ed.on("specialkey", this.onEditorKey, this);
							this.activeEditor = ed;
							var v = e.value// this.preEditValue(r, field);
							ed.startEdit(
									this.view.getCell(row, col).firstChild, v);
						}).defer(50, this);
					}
				}
			},
			onEditorKey : function(field, e) {
				if (field.needFocus) {
					field.needFocus = false;
					ed = this.activeEditor;
					if (!ed) {
						ed = this.lastActiveEditor;
					}
					this.startEditing(ed.row, ed.col);
					return;
				}
				if (e.getKey() == e.ENTER && !e.shiftKey) {
					var sm = this.getSelectionModel();
					var cell = sm.getSelectedCell();
					var count = this.colModel.getColumnCount()
					if (cell[1] + 1 >= count && !this.editing) {
						this.fireEvent("doNewColumn");
						return;
					}
				}
				this.selModel.onEditorKey(field, e);
			}
		});
Ext.reg('myditorgrid', util.widgets.MyEditorGrid);