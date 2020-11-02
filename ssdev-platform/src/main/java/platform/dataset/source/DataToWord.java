package platform.dataset.source;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;

public class DataToWord {
	private Font contextFont;
	private BaseFont bfChinese;
	private Dictionary groupDic=DictionaryController.instance().getDic("platform.dataset.dic.resDataGroup");
	public DataToWord() {
		try {
			bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			contextFont = new Font(bfChinese, 8, Font.NORMAL);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String parseName(String str) {
		str = str.replaceAll(" ", "");
		if (str.startsWith("G")) {
			str = str.split("-")[0].replaceAll("\\/", "\\.");
		} else if (str.startsWith("W")) {
			if (str.contains("CV"))
				str = str.substring(str.indexOf("C"), str.length());
		}
		return str;
	}

	public List getQuertCnd(String key, String value) throws Exception {
		String exp = "['eq',['$','" + key + "'],['i'," + value + "]]";
		List queryCnd = JSONUtils.parse(exp, List.class);
		return queryCnd;
	}

	public QueryResult find(String enrtyName, List queryCnd, Context ctx)
			throws Exception {
		Schema schema = SchemaController.instance().getSchema(enrtyName);
		SimpleDAO dao = new SimpleDAO(schema, ctx);
		QueryResult qr = dao.find(queryCnd);
		return qr;
	}

	public Cell getCell(String cellName) throws BadElementException {
		Cell cell = new Cell(new Paragraph(cellName, contextFont));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}
	public Cell getTitleCell(String cellName) throws BadElementException{
		Cell cell = new Cell(new Paragraph(cellName, new Font(bfChinese, 8, Font.BOLD)));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		return cell;
	}
	
	public void addElements(List<Map<String, Object>> list,Table table,Set<String> set) throws Exception{
		for (Map<String, Object> map : list) {
			if (map.get("bCodeSystem") != null&&!String.valueOf(map.get("CodeSystem")).equals("1")) {
				String dicName = parseName(String.valueOf(map.get("bCodeSystem")));
				String dataStandardId=S.obj2String(map.get("DataStandardId"));
				if(!StringUtils.isEmpty(dataStandardId)){
					dicName="platform.dataset.dic.res."+dataStandardId+"."+dicName;
				}
				set.add(dicName+","+String.valueOf(map.get("bCodeSystem")));
			}
			if(StringUtils.isEmpty(S.obj2String(map.get("CustomIdentify")))){
				table.addCell(getCell(S.obj2String(map.get("bCustomIdentify"))));
			}else{
				table.addCell(getCell(S.obj2String(map.get("CustomIdentify"))));
			}
			table.addCell(getCell(S.obj2String(map.get("StandardIdentify"))));
			if(StringUtils.isEmpty(S.obj2String(map.get("DName")))){
				table.addCell(getCell(S.obj2String(map.get("bDName"))));
			}else{
				table.addCell(getCell(S.obj2String(map.get("DName"))));
			}
			if(StringUtils.isEmpty(S.obj2String(map.get("DataType")))){
				table.addCell(getCell(S.obj2String(map.get("bDataType"))));
			}else{
				table.addCell(getCell(S.obj2String(map.get("DataType"))));
			}
			if(StringUtils.isEmpty(S.obj2String(map.get("DataLength")))){
				table.addCell(getCell(S.obj2String(map.get("bDataLength"))));
			}else{
				table.addCell(getCell(S.obj2String(map.get("DataLength"))));
			}
			if (String.valueOf(map.get("CodeSystem")).equals("1")) {
				table.addCell(getCell(""));
			}else{
				table.addCell(getCell(S.obj2String(map.get("bCodeSystem"))));
			}			
			String section="";
			if(!StringUtils.isEmpty(groupDic.getText(String.valueOf(map.get("DataGroup"))))){
				section=groupDic.getText(String.valueOf(map.get("DataGroup")));
			}
			table.addCell(getCell(section));
			table.addCell(getCell(S.obj2String(map.get("ForceIdentify_text"))));
			Cell c = new Cell(new Paragraph("", contextFont));
			if(StringUtils.isEmpty(S.obj2String(map.get("DComment")))){
				c = getCell(S.obj2String(map.get("bDComment")));
			}else{
				c = getCell(S.obj2String(map.get("DComment")));
			}
			c.setColspan(3);
			table.addCell(c);
		}
	}

	public InputStream toWord(List<Map<String,Object>> li, Context ctx) {
		if(li == null || li.size() == 0){
			return null;
		}
		try {
			// 设置纸张大小
			Document document = new Document(PageSize.A4.rotate());
			// 建立一个书写器，与document对象关联
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			RtfWriter2.getInstance(document, os);
			document.open();
			/* 设置标题格式 */
			RtfParagraphStyle titleFont = RtfParagraphStyle.STYLE_HEADING_1;
			titleFont.setAlignment(Element.ALIGN_CENTER);
			titleFont.setStyle(Font.BOLD);
			titleFont.setSize(15);
			RtfParagraphStyle titleFont1 = RtfParagraphStyle.STYLE_HEADING_2;
			titleFont1.setAlignment(Element.ALIGN_LEFT);
			titleFont1.setStyle(Font.BOLD);
			titleFont1.setSize(13);
			Paragraph title = new Paragraph(S.obj2String(li.get(0).get("DataStandardId"))+"-"+"数据集",titleFont);
			// 设置标题格式对齐方式
			document.add(title);
			Set<String> set = new HashSet<String>();
			for (Map m : li) {
				Paragraph context = new Paragraph();
				String DataSetId = String.valueOf(m.get("DataSetId"));
				String parentId = S.obj2String(m.get("Parent"));
				if(!StringUtils.isEmpty(parentId)){
					List<Map<String, Object>> parentSet = find("platform.dataset.schemas.RES_DataSet",getQuertCnd("DataSetId", parentId), ctx).getRecords();
				}
				List queryCnd = getQuertCnd("DataSetId", DataSetId);
				List<Map<String, Object>> list = find("platform.dataset.schemas.RES_DataSetContent",queryCnd, ctx).getRecords();
				// 设置表格所属集名
				context = new Paragraph((String) m.get("DName") + "("
						+ S.obj2String(m.get("CustomIdentify")) + ")",
						titleFont1);
				// 段间距
//				context.setSpacingBefore(3);
				// 设置第一行空的列数
				document.add(context);
				if(!StringUtils.isEmpty(parentId)){
					List<Map<String, Object>> parentSet = find("RES_DataSet",getQuertCnd("DataSetId", parentId), ctx).getRecords();
					document.add(new Paragraph("所属数据集: "+parentSet.get(0).get("DName")+ "("
							+ S.obj2String(parentSet.get(0).get("CustomIdentify")) + ")", new Font(bfChinese, 10, Font.NORMAL)));
				}
				Table table = new Table(11);
				table.setTableFitsPage(false);
				table.setTop(0);
				table.setWidth(Float.parseFloat("100"));
				// table.setWidth(90);//占页面宽度比例
				table.setAlignment(Element.ALIGN_CENTER);// 居中
				table.setAlignment(Element.ALIGN_MIDDLE);// 垂直居中
				table.setAutoFillEmptyCells(true);// 自动填满
				table.setBorderWidth(1);// 边框宽度
				table.addCell(getTitleCell("自定义标识符"));
				table.addCell(getTitleCell("卫生部标识符"));
				table.addCell(getTitleCell("名称"));
				table.addCell(getTitleCell("数据类型"));
				table.addCell(getTitleCell("数据长度"));
				table.addCell(getTitleCell("字典"));
				table.addCell(getTitleCell("段落"));
				table.addCell(getTitleCell("填报要求"));
				Cell cell = getTitleCell("说明");
				cell.setColspan(3);
				table.addCell(cell);
				addElements(list, table, set);
				
				context=new Paragraph();
				context.add(table);
				document.add(context);
			}
				
				if (set.size() > 0) {
					for (String s : set) {
						Paragraph context = new Paragraph();
						String[] arr=s.split(",");
						String dicName = arr[0];
						String alias = arr[1];
						Dictionary dic = DictionaryController.instance()
								.getDic(dicName);
						if (dic == null) {
							continue;
						}
						String dicAlias=dic.getAlias();
						if(!StringUtils.isEmpty(dicAlias)&&!alias.equals(dicAlias)){
							dicAlias="包含字典(" + alias + ":"+dicAlias+")";
						}else{
							dicAlias="包含字典(" + alias + ")";
						}
						context = new Paragraph(dicAlias,
								titleFont1);
						document.add(context);
						Table table = new Table(2);
						table.setWidth(Float.parseFloat("100"));
						int width[] = { 50, 50 };// 设置每列宽度比例
						table.setWidths(width);
						// table.setWidth(90);//占页面宽度比例
						table.setAlignment(Element.ALIGN_CENTER);// 居中
						table.setAlignment(Element.ALIGN_MIDDLE);// 垂直居中
						table.setAutoFillEmptyCells(true);// 自动填满
						table.setBorderWidth(1);// 边框宽度
						Font f = new Font(bfChinese, 10, Font.BOLD);
						Cell c = new Cell(new Paragraph("键", f));
						c.setVerticalAlignment(Element.ALIGN_MIDDLE);
						table.addCell(c);
						c.setBackgroundColor(Color.yellow);
						c = new Cell(new Paragraph("值", f));
						c.setVerticalAlignment(Element.ALIGN_MIDDLE);
						table.addCell(c);
						c.setBackgroundColor(Color.yellow);
						List<DictionaryItem> items = dic.itemsList();
						for (DictionaryItem item : items) {
							table.addCell(getCell(item.getKey()));
							table.addCell(getCell(item.getText()));
						}
						context = new Paragraph();
						context.add(table);
						document.add(context);
					}
				}
			document.close();
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
